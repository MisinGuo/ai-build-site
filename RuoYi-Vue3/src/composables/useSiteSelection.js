import { ref, onMounted } from 'vue'
import { listSite } from '@/api/gamebox/site'

const STORAGE_KEY = 'gb_last_site_id'
const VIEW_MODE_STORAGE_KEY = 'gb_last_view_mode'
const CREATOR_SITE_STORAGE_KEY = 'gb_last_creator_site_id'
const RELATED_SITE_STORAGE_KEY = 'gb_last_related_site_id'

function getStoredSiteId(storageKey) {
  const raw = localStorage.getItem(storageKey)
  if (!raw) return null
  const parsed = Number(raw)
  return Number.isFinite(parsed) ? parsed : null
}

function findSiteById(siteList, siteId) {
  if (!Array.isArray(siteList) || siteId === null || siteId === undefined) return null
  return siteList.find(site => site.id === siteId) || null
}

function getFirstRealSite(siteList) {
  if (!Array.isArray(siteList)) return null
  return siteList.find(site => site.isPersonal !== 1) || null
}

function resolveCreatorSiteId(siteList, creatorFallbackSiteId) {
  const storedCreatorSiteId = getStoredSiteId(CREATOR_SITE_STORAGE_KEY)
  const storedCreatorSite = findSiteById(siteList, storedCreatorSiteId)
  if (storedCreatorSite) {
    return storedCreatorSite.id
  }

  if (creatorFallbackSiteId !== null && creatorFallbackSiteId !== undefined) {
    const fallbackSite = findSiteById(siteList, creatorFallbackSiteId)
    if (fallbackSite) {
      return fallbackSite.id
    }
  }

  const defaultSite = resolveDefaultSite(siteList)
  return defaultSite ? defaultSite.id : undefined
}

function resolveRelatedSiteId(siteList) {
  const storedRelatedSiteId = getStoredSiteId(RELATED_SITE_STORAGE_KEY)
  const storedRelatedSite = findSiteById(siteList, storedRelatedSiteId)
  if (storedRelatedSite && storedRelatedSite.isPersonal !== 1) {
    return storedRelatedSite.id
  }

  const firstRealSite = getFirstRealSite(siteList)
  return firstRealSite ? firstRealSite.id : undefined
}

export function restoreViewModeSiteSelection(siteList, options = {}) {
  const { creatorFallbackSiteId } = options

  if (!Array.isArray(siteList) || siteList.length === 0) {
    return { viewMode: 'creator', siteId: undefined }
  }

  const firstRealSite = getFirstRealSite(siteList)
  const storedMode = localStorage.getItem(VIEW_MODE_STORAGE_KEY)
  const viewMode = storedMode === 'related' && firstRealSite ? 'related' : 'creator'

  const siteId = viewMode === 'related'
    ? resolveRelatedSiteId(siteList)
    : resolveCreatorSiteId(siteList, creatorFallbackSiteId)

  return { viewMode, siteId }
}

export function resolveSiteIdByViewMode(viewMode, siteList, options = {}) {
  const { creatorFallbackSiteId } = options
  if (viewMode === 'related') {
    return resolveRelatedSiteId(siteList)
  }
  return resolveCreatorSiteId(siteList, creatorFallbackSiteId)
}

export function saveViewModeSiteSelection(viewMode, siteId) {
  localStorage.setItem(VIEW_MODE_STORAGE_KEY, viewMode)
  if (siteId === null || siteId === undefined || siteId === '') return

  const storageKey = viewMode === 'related'
    ? RELATED_SITE_STORAGE_KEY
    : CREATOR_SITE_STORAGE_KEY
  localStorage.setItem(storageKey, String(siteId))
}

export function resolveDefaultSite(siteList) {
  if (!Array.isArray(siteList) || siteList.length === 0) {
    return null
  }
  return siteList.find(site => site.isDefault === 1)
    || siteList.find(site => site.isPersonal === 1)
    || siteList[0]
}

export function getDefaultSiteOptionLabel(siteList, defaultLabel = '默认配置') {
  const defaultSite = resolveDefaultSite(siteList)
  if (!defaultSite) {
    return defaultLabel
  }
  return `${defaultSite.name} [${defaultLabel}]`
}

/**
 * 获取个人默认站点的 ID
 */
export function getPersonalSiteId(siteList) {
  if (!Array.isArray(siteList)) return null
  const site = siteList.find(s => s.isPersonal === 1)
  return site ? site.id : null
}

/**
 * 判断给定的 siteId 是否为个人默认站点
 */
export function isPersonalSite(siteId, siteList) {
  if (siteId === null || siteId === undefined) return false
  const personalId = getPersonalSiteId(siteList)
  return personalId !== null && siteId === personalId
}

export function getSiteDisplayName(siteId, siteList, defaultLabel = '默认配置') {
  if (siteId === null || siteId === undefined || siteId === '' || isPersonalSite(siteId, siteList)) {
    return getDefaultSiteOptionLabel(siteList, defaultLabel)
  }
  const site = Array.isArray(siteList) ? siteList.find(item => item.id === siteId) : null
  return site ? site.name : `网站ID: ${siteId}`
}

/**
 * 给站点列表每项附加 displayLabel（用于 el-option :label 展示默认/个人标签）
 * 在各页面自己加载完 siteList 后调用此函数即可
 */
export function enrichSiteList(list) {
  if (!Array.isArray(list)) return list
  list.forEach(site => {
    const tags = []
    if (site.isDefault === 1) tags.push('[默认]')
    if (site.isPersonal === 1) tags.push('[个人]')
    site.displayLabel = tags.length ? `${site.name} ${tags.join(' ')}` : site.name
  })
  return list
}

/**
 * 网站选择组合式函数
 * 优先级：localStorage 记忆 > is_default=1 的个人默认站点 > 列表第一个
 */
export function useSiteSelection() {
  const siteList = ref([])
  const currentSiteId = ref(undefined)
  const loading = ref(false)

  const loadSiteList = async (onSiteSelected) => {
    loading.value = true
    try {
      const response = await listSite({ pageNum: 1, pageSize: 9999, status: '1' })
      siteList.value = response.rows || []

      // 给每条站点对象附加 displayLabel，用于 el-option :label 显示
      enrichSiteList(siteList.value)

      if (siteList.value.length > 0 && !currentSiteId.value) {
        // 1. 优先还原上次选中的站点（且该站点仍在可见列表内）
        const lastId = Number(localStorage.getItem(STORAGE_KEY))
        const lastSite = lastId && siteList.value.find(s => s.id === lastId)
        // 2. 其次选 is_default=1 的个人默认站点
        const defaultSite = siteList.value.find(s => s.isDefault === 1)

        currentSiteId.value = (lastSite ? lastSite.id : null)
          || (defaultSite ? defaultSite.id : null)
          || siteList.value[0].id
      }
      // 始终触发回调，确保页面初始化完成
      if (onSiteSelected && typeof onSiteSelected === 'function') {
        onSiteSelected(currentSiteId.value)
      }
    } catch (error) {
      console.error('加载网站列表失败:', error)
      // 即使失败也触发回调，确保页面不会永远 loading
      if (onSiteSelected && typeof onSiteSelected === 'function') {
        onSiteSelected(currentSiteId.value)
      }
    } finally {
      loading.value = false
    }
  }

  const handleSiteChange = (onSiteChanged) => {
    // 切换时持久化选择
    if (currentSiteId.value) {
      localStorage.setItem(STORAGE_KEY, currentSiteId.value)
    }
    if (onSiteChanged && typeof onSiteChanged === 'function') {
      onSiteChanged(currentSiteId.value)
    }
  }

  const getSiteName = (siteId) => {
    return getSiteDisplayName(siteId, siteList.value)
  }

  return {
    siteList,
    currentSiteId,
    loading,
    loadSiteList,
    handleSiteChange,
    getSiteName
  }
}
