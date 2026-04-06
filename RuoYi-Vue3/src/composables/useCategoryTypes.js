/**
 * 分类类型组合式函数
 * 提供从后端获取分类类型的能力，并提供工具函数
 */
import { ref } from 'vue'
import { getCategoryTypes as fetchCategoryTypes } from '@/api/gamebox/category'

// 全局缓存分类类型数据
let categoryTypesCache = null
let categoryTypesPromise = null

/**
 * 使用分类类型数据的组合式函数
 * @returns {Object} 包含分类类型数据和工具函数
 */
export function useCategoryTypes() {
  const categoryTypes = ref([])
  const loading = ref(false)
  const error = ref(null)

  /**
   * 加载分类类型数据
   */
  const loadCategoryTypes = async () => {
    // 如果有缓存，直接使用
    if (categoryTypesCache) {
      categoryTypes.value = categoryTypesCache
      return categoryTypesCache
    }

    // 如果正在加载，等待同一个Promise
    if (categoryTypesPromise) {
      return categoryTypesPromise
    }

    loading.value = true
    error.value = null

    try {
      categoryTypesPromise = fetchCategoryTypes()
      const response = await categoryTypesPromise
      categoryTypesCache = response.data || []
      categoryTypes.value = categoryTypesCache
      return categoryTypesCache
    } catch (err) {
      error.value = err
      console.error('加载分类类型失败:', err)
      
      // 降级使用前端配置
      try {
        const module = await import('@/config/categoryTypes')
        const fallbackData = module.getCategoryTypeOptions()
        categoryTypes.value = fallbackData
        categoryTypesCache = fallbackData
        return fallbackData
      } catch (fallbackErr) {
        console.error('加载降级配置也失败:', fallbackErr)
        return []
      }
    } finally {
      loading.value = false
      categoryTypesPromise = null
    }
  }

  /**
   * 根据分类类型值获取标签文本
   * @param {string} value - 分类类型值
   * @returns {string} 标签文本
   */
  const getCategoryTypeLabel = (value) => {
    const type = categoryTypes.value.find(t => t.value === value)
    return type ? type.label : value
  }

  /**
   * 根据分类类型值获取Tag类型
   * @param {string} value - 分类类型值
   * @returns {string} Tag类型
   */
  const getCategoryTypeTagType = (value) => {
    const type = categoryTypes.value.find(t => t.value === value)
    const tagType = type?.tagType
    // 确保返回有效的 tag type，避免空字符串
    return tagType && ['primary', 'success', 'info', 'warning', 'danger'].includes(tagType) ? tagType : 'info'
  }

  /**
   * 根据分类类型值获取描述
   * @param {string} value - 分类类型值
   * @returns {string} 描述文本
   */
  const getCategoryTypeDescription = (value) => {
    const type = categoryTypes.value.find(t => t.value === value)
    return type ? type.description : ''
  }

  /**
   * 获取分类类型选项（用于下拉框）
   * @returns {Array} 选项数组
   */
  const getCategoryTypeOptions = () => {
    return categoryTypes.value.map(type => ({
      label: type.label,
      value: type.value
    }))
  }

  /**
   * 清除缓存（在需要刷新数据时调用）
   */
  const clearCache = () => {
    categoryTypesCache = null
  }

  return {
    categoryTypes,
    loading,
    error,
    loadCategoryTypes,
    getCategoryTypeLabel,
    getCategoryTypeTagType,
    getCategoryTypeDescription,
    getCategoryTypeOptions,
    clearCache
  }
}

/**
 * 快速获取分类类型标签（同步函数，需要先加载过数据）
 * @param {string} value - 分类类型值
 * @returns {string} 标签文本
 */
export function getCategoryTypeLabel(value) {
  if (!categoryTypesCache) {
    console.warn('分类类型数据未加载，请先调用 loadCategoryTypes()')
    return value
  }
  const type = categoryTypesCache.find(t => t.value === value)
  return type ? type.label : value
}

/**
 * 快速获取分类类型Tag类型（同步函数，需要先加载过数据）
 * @param {string} value - 分类类型值
 * @returns {string} Tag类型
 */
export function getCategoryTypeTagType(value) {
  if (!categoryTypesCache) {
    console.warn('分类类型数据未加载，请先调用 loadCategoryTypes()')
    return 'info'
  }
  const type = categoryTypesCache.find(t => t.value === value)
  const tagType = type?.tagType
  // 确保返回有效的 tag type，避免空字符串
  return tagType && ['primary', 'success', 'info', 'warning', 'danger'].includes(tagType) ? tagType : 'info'
}
