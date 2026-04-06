<template>
  <el-dialog
    :title="getDialogTitle()"
    v-model="visible"
    width="900px"
    append-to-body
    @close="handleClose"
  >
    <div v-loading="loading">
      <!-- 默认配置提示 -->
      <el-alert
        v-if="isGlobalEntity"
        type="info"
        :closable="false"
        show-icon
        style="margin-bottom: 16px;"
      >
        <template #title>
          <span style="font-weight: 500;">默认配置 - 灵活管理模式</span>
        </template>
        该内容默认对所有网站可见。您可以灵活添加"关联"或"排除"配置：<br/>
        • <strong>关联</strong>：指定网站可见，并可自定义配置（如排序、可见性等）<br/>
        • <strong>排除</strong>：指定网站不可见，简单快速<br/>
        <el-divider style="margin: 8px 0;" />
        <el-text type="warning" size="small">
          <el-icon><Warning /></el-icon>
          <strong>优先级规则：</strong>当同一网站同时存在"关联"和"排除"配置时，<strong>关联优先生效</strong>，排除配置将被忽略
        </el-text>
      </el-alert>
      
      <!-- 创建者网站提示 -->
      <el-alert
        v-else-if="!isGlobalEntity && creatorSiteId"
        type="success"
        :closable="false"
        show-icon
        style="margin-bottom: 16px;"
      >
        <template #title>
          <span style="font-weight: 500;">创建者网站：{{ getCreatorSiteName() }}</span>
        </template>
        该内容对创建者网站自动可见，无需额外配置关联。下方仅显示跨站共享的网站。
      </el-alert>
      
      <!-- 当前关联的网站 -->
      <div style="margin-bottom: 20px;">
        <div style="margin-bottom: 12px; font-weight: 500; font-size: 15px;">
          <el-icon style="margin-right: 4px;"><Link /></el-icon>
          {{ isGlobalEntity ? '网站配置列表' : '当前关联的网站' }} ({{ relatedSites.filter(s => s.siteId !== creatorSiteId).length }})
        </div>
        
        <el-alert
          v-if="relatedSites.filter(s => s.siteId !== creatorSiteId).length === 0"
          type="info"
          :closable="false"
          show-icon
          style="margin-bottom: 12px;"
        >
          {{ isGlobalEntity ? '尚未配置任何网站，该内容对所有网站可见' : '该内容暂未共享到其他网站，请添加网站关联' }}
        </el-alert>

        <el-table
          v-if="relatedSites.filter(s => s.siteId !== creatorSiteId).length > 0"
          :data="relatedSites.filter(s => s.siteId !== creatorSiteId)"
          border
          stripe
          style="width: 100%"
        >
          <el-table-column label="网站名称" align="center" width="150">
            <template #default="scope">
              <el-tag
                :type="scope.row.siteId === creatorSiteId ? 'success' : 'info'"
                effect="plain"
              >
                {{ scope.row.siteName }}
                <span v-if="scope.row.siteId === creatorSiteId">⭐</span>
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="关系类型" align="center" width="130" v-if="isGlobalEntity">
            <template #default="scope">
              <div style="display: flex; align-items: center; justify-content: center; gap: 4px;">
                <el-tag :type="scope.row.relationType === 'exclude' ? 'warning' : 'success'" size="small">
                  {{ scope.row.relationType === 'exclude' ? '排除' : '关联' }}
                </el-tag>
                <el-tooltip 
                  v-if="hasConflict(scope.row.siteId)" 
                  content="该网站同时存在关联和排除配置，关联优先生效" 
                  placement="top"
                >
                  <el-icon color="#E6A23C" style="cursor: help;"><Warning /></el-icon>
                </el-tooltip>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="网站编码" align="center" width="130">
            <template #default="scope">
              {{ scope.row.siteCode || '-' }}
            </template>
          </el-table-column>
          <el-table-column label="域名" align="center" show-overflow-tooltip min-width="180">
            <template #default="scope">
              {{ scope.row.siteDomain || '-' }}
            </template>
          </el-table-column>
          <el-table-column label="可见性" align="center" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.isVisible === '1' ? 'success' : 'info'" size="small">
                {{ scope.row.isVisible === '1' ? '可见' : '隐藏' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="推荐" align="center" width="80" v-if="entityType === 'box' || entityType === 'game'">
            <template #default="scope">
              <el-tag v-if="scope.row.isFeatured === '1'" type="warning" size="small">推荐</el-tag>
              <span v-else style="color: #909399;">-</span>
            </template>
          </el-table-column>
          <el-table-column label="新游" align="center" width="80" v-if="entityType === 'game'">
            <template #default="scope">
              <el-tag v-if="scope.row.isNew === '1'" type="success" size="small">新</el-tag>
              <span v-else style="color: #909399;">-</span>
            </template>
          </el-table-column>
          <el-table-column label="置顶" align="center" width="80" v-if="entityType === 'article'">
            <template #default="scope">
              <el-tag v-if="scope.row.isTop === '1'" type="danger" size="small">置顶</el-tag>
              <span v-else style="color: #909399;">-</span>
            </template>
          </el-table-column>
          <el-table-column label="自定义名称" align="center" width="120" v-if="entityType === 'box' || entityType === 'game'">
            <template #default="scope">
              <span v-if="scope.row.customName" style="color: #409EFF;">{{ scope.row.customName }}</span>
              <span v-else style="color: #909399;">-</span>
            </template>
          </el-table-column>
          <el-table-column label="排序" align="center" width="80" prop="sortOrder" v-if="entityType === 'box' || entityType === 'game' || entityType === 'article'" />
          <el-table-column label="浏览量" align="center" width="90" prop="viewCount" v-if="entityType === 'box' || entityType === 'game' || entityType === 'article'" />
          <el-table-column label="操作" align="center" width="150" fixed="right">
            <template #default="scope">
              <!-- 排除类型只显示取消排除 -->
              <template v-if="scope.row.relationType === 'exclude'">
                <el-button
                  link
                  type="primary"
                  size="small"
                  icon="CircleCheck"
                  @click="handleRemoveSite(scope.row)"
                >
                  取消排除
                </el-button>
              </template>
              <!-- 关联类型显示配置和移除 -->
              <template v-else>
                <el-button
                  link
                  type="primary"
                  size="small"
                  icon="Edit"
                  @click="handleEditConfig(scope.row)"
                >
                  配置
                </el-button>
                <el-button
                  link
                  type="danger"
                  size="small"
                  icon="Delete"
                  @click="handleRemoveSite(scope.row)"
                >
                  移除
                </el-button>
              </template>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 添加到其他网站 -->
      <div style="margin-bottom: 20px;">
        <div style="margin-bottom: 12px; font-weight: 500; font-size: 15px;">
          <el-icon style="margin-right: 4px;"><Plus /></el-icon>
          {{ isGlobalEntity ? '添加网站配置' : '添加到其他网站' }}
        </div>
        
        <div style="display: flex; gap: 12px; align-items: flex-start;">
          <!-- 关系类型选择（仅默认配置显示） -->
          <el-select
            v-if="isGlobalEntity"
            v-model="addRelationType"
            placeholder="选择关系类型"
            style="width: 140px;"
            size="default"
          >
            <el-option label="关联" value="shared">
              <span style="display: flex; align-items: center; gap: 6px;">
                <el-icon color="#67C23A"><Link /></el-icon>
                <span>关联</span>
              </span>
            </el-option>
            <el-option label="排除" value="exclude">
              <span style="display: flex; align-items: center; gap: 6px;">
                <el-icon color="#E6A23C"><CircleClose /></el-icon>
                <span>排除</span>
              </span>
            </el-option>
          </el-select>
          
          <el-select
            v-model="selectedSiteIds"
            placeholder="选择要添加的网站"
            multiple
            filterable
            style="flex: 1;"
            size="default"
          >
            <el-option
              v-for="site in availableSites"
              :key="site.id"
              :label="site.name"
              :value="site.id"
            />
          </el-select>
          <el-button
            :type="addRelationType === 'exclude' ? 'warning' : 'primary'"
            icon="Plus"
            @click="handleAddToSites"
            :disabled="selectedSiteIds.length === 0"
            size="default"
          >
            {{ isGlobalEntity ? (addRelationType === 'exclude' ? '排除' : '添加关联') : '添加' }}
          </el-button>
        </div>

        <!-- 批量添加选项（仅游戏盒子） -->
        <div v-if="entityType === 'box'" style="margin-top: 12px;">
          <el-checkbox v-model="includeGames">同时添加盒子中的所有游戏</el-checkbox>
          <el-alert
            v-if="includeGames"
            type="info"
            :closable="false"
            show-icon
            style="margin-top: 8px;"
          >
            <template #default>
              <div style="font-size: 12px;">
                <strong>异步处理模式</strong><br/>
                盒子将立即关联，游戏和分类将在后台异步处理。处理流程：<br/>
                1️⃣ 盒子关联到目标网站<br/>
                2️⃣ 盒子的分类关联到目标网站<br/>
                3️⃣ 收集所有游戏的分类并关联<br/>
                4️⃣ 所有游戏关联到目标网站<br/>
                <el-text type="warning" size="small">⚠️ 大量游戏时可能需要几分钟，请稍后刷新查看结果</el-text>
              </div>
            </template>
          </el-alert>
        </div>
      </div>

      <!-- 快速复制到网站（仅游戏盒子） -->
      <div v-if="entityType === 'box'" style="border-top: 1px dashed #dcdfe6; padding-top: 20px;">
        <div style="margin-bottom: 12px; font-weight: 500; font-size: 15px;">
          <el-icon style="margin-right: 4px;"><CopyDocument /></el-icon>
          快速复制到网站
        </div>
        <el-alert
          type="info"
          :closable="false"
          show-icon
          style="margin-bottom: 12px;"
        >
          <template #default>
            <div style="font-size: 13px;">
              复制功能会同时复制游戏盒子、其中的游戏以及相关分类到目标网站
            </div>
          </template>
        </el-alert>
        <div style="display: flex; gap: 12px; align-items: center;">
          <el-select
            v-model="copyTargetSiteId"
            placeholder="选择目标网站"
            filterable
            clearable
            style="flex: 1;"
            size="default"
          >
            <el-option
              v-for="site in availableSites"
              :key="site.id"
              :label="site.name"
              :value="site.id"
            />
          </el-select>
          <el-button
            type="warning"
            icon="CopyDocument"
            @click="handleCopyToSite"
            :disabled="!copyTargetSiteId"
            size="default"
          >
            复制
          </el-button>
        </div>
      </div>
    </div>

    <template #footer>
      <el-button @click="handleClose">关闭</el-button>
      <el-button type="primary" @click="handleRefresh" icon="Refresh">刷新</el-button>
    </template>

    <!-- 配置对话框 -->
    <el-dialog
      :title="getConfigDialogTitle()"
      v-model="configDialogVisible"
      width="500px"
      append-to-body
    >
      <el-form :model="configForm" label-width="100px">
        <el-form-item label="网站">
          <el-input :value="currentConfigSite?.siteName" disabled />
        </el-form-item>
        <el-form-item label="可见性">
          <el-radio-group v-model="configForm.isVisible">
            <el-radio value="1">可见</el-radio>
            <el-radio value="0">隐藏</el-radio>
          </el-radio-group>
        </el-form-item>
        <!-- 以下字段仅对内容实体（box/game/article）显示 -->
        <template v-if="entityType === 'box' || entityType === 'game' || entityType === 'article'">
          <el-form-item label="排序">
            <el-input-number v-model="configForm.sortOrder" :min="0" :max="9999" />
          </el-form-item>
          <el-divider content-position="left" style="margin: 20px 0;">高级配置</el-divider>
          <el-form-item label="推荐" v-if="entityType === 'box' || entityType === 'game'">
            <el-radio-group v-model="configForm.isFeatured">
              <el-radio value="1">是</el-radio>
              <el-radio value="0">否</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="置顶" v-if="entityType === 'article'">
            <el-radio-group v-model="configForm.isTop">
              <el-radio value="1">是</el-radio>
              <el-radio value="0">否</el-radio>
            </el-radio-group>
          </el-form-item>
        </template>
        <el-form-item label="自定义名称" v-if="entityType === 'box' || entityType === 'game'">
          <el-input v-model="configForm.customName" placeholder="留空则使用默认名称" />
        </el-form-item>
        <el-form-item label="自定义描述" v-if="entityType === 'box' || entityType === 'game'">
          <el-input v-model="configForm.customDescription" type="textarea" :rows="3" placeholder="留空则使用默认描述" />
        </el-form-item>
        <el-form-item label="自定义下载链接" v-if="entityType === 'game'">
          <el-input v-model="configForm.customDownloadUrl" placeholder="留空则使用默认下载链接" />
        </el-form-item>
        <el-form-item label="新游标记" v-if="entityType === 'game'">
          <el-radio-group v-model="configForm.isNew">
            <el-radio value="1">是</el-radio>
            <el-radio value="0">否</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="自定义标题" v-if="entityType === 'article'">
          <el-input v-model="configForm.customTitle" placeholder="留空则使用默认标题" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="configDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveConfig">保存</el-button>
      </template>
    </el-dialog>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Link, Plus, CopyDocument, Edit, Delete, Refresh, CircleClose, CircleCheck, Warning } from '@element-plus/icons-vue'
import {
  getBoxSites, updateBoxSiteConfig, copyBoxToSite, batchSaveBoxSiteRelations,
  getGameSites, updateGameSiteConfig, batchSaveGameSiteRelations,
  getArticleSites, updateArticleSiteConfig, batchSaveArticleSiteRelations,
  getCategorySites, batchSaveCategorySiteRelations, updateCategoryVisibility,
  getStorageConfigSites, batchSaveStorageConfigSiteRelations, updateStorageVisibility,
  getPlatformSites, batchSaveAiPlatformSiteRelations, updatePlatformVisibility,
  getAtomicToolSites, batchSaveAtomicToolSiteRelations, updateAtomicToolVisibility,
  getWorkflowSites, updateWorkflowSiteConfig, batchSaveWorkflowSiteRelations,
  getTitleBatchSites, batchSaveTitleBatchSiteRelations, updateTitleBatchVisibility
} from '@/api/gamebox/siteRelation'
import { listSite } from '@/api/gamebox/site'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  entityType: {
    type: String,
    required: true,
    validator: (value) => ['box', 'game', 'article', 'category', 'storageConfig', 'aiPlatform', 'atomicTool', 'template', 'workflow', 'titleBatch'].includes(value)
  },
  entityId: {
    type: [Number, null],
    default: null
  },
  entityName: {
    type: String,
    default: ''
  },
  creatorSiteId: {
    type: Number,
    default: 0
  },
  isPersonalCreator: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'refresh'])

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const loading = ref(false)
const relatedSites = ref([])
const allSites = ref([])
const selectedSiteIds = ref([])
const addDialogVisible = ref(false)
const includeGames = ref(true)
const copyTargetSiteId = ref(null)
const addRelationType = ref('shared')  // 添加关系类型：'shared' 或 'exclude'

// 配置对话框
const configDialogVisible = ref(false)
const currentConfigSite = ref(null)
const configForm = ref({
  isVisible: '1',
  isFeatured: '0',
  isTop: '0',
  sortOrder: 0,
  customName: '',
  customTitle: ''
})

// 可添加的网站（根据当前选择的关系类型过滤）
const availableSites = computed(() => {
  // 获取当前选择的关系类型对应的已存在网站
  const existingSiteIds = relatedSites.value
    .filter(s => s.relationType === addRelationType.value)
    .map(s => s.siteId)
  
  return allSites.value.filter(s => {
    // 过滤掉个人默认配置站（is_personal=1），它是分类的来源站，选它没有实际意义
    if (s.isPersonal === 1 || s.isPersonal === '1') return false
    // 过滤掉当前类型已存在的网站
    if (existingSiteIds.includes(s.id)) return false
    // 过滤掉创建者网站（创建者通过 site_id 已经天然关联，个人默认站不过滤）
    if (!isGlobalEntity.value && props.creatorSiteId && s.id === props.creatorSiteId) return false
    return true
  })
})

// 判断是否是全局实体（个人默认站）
const isGlobalEntity = computed(() => {
  return props.creatorSiteId === null || props.isPersonalCreator
})

// 检测有冲突的网站（同时存在关联和排除）
const conflictingSiteIds = computed(() => {
  if (!isGlobalEntity.value) return []
  
  const siteIdMap = new Map()
  relatedSites.value.forEach(site => {
    if (!siteIdMap.has(site.siteId)) {
      siteIdMap.set(site.siteId, [])
    }
    siteIdMap.get(site.siteId).push(site.relationType)
  })
  
  const conflicts = []
  siteIdMap.forEach((types, siteId) => {
    if (types.includes('shared') && types.includes('exclude')) {
      conflicts.push(siteId)
    }
  })
  
  return conflicts
})

// 检查某个网站是否有冲突
function hasConflict(siteId) {
  return conflictingSiteIds.value.includes(siteId)
}

// 获取创建者网站名称
function getCreatorSiteName() {
  if (!props.creatorSiteId || isGlobalEntity.value) return '全局'
  const site = allSites.value.find(s => s.id === props.creatorSiteId)
  return site ? site.name : '未知网站'
}

// 获取对话框标题
function getDialogTitle() {
  const prefix = isGlobalEntity.value ? '[默认配置] ' : ''
  return `${prefix}管理网站关联 - ${props.entityName}`
}

// 获取配置对话框标题
function getConfigDialogTitle() {
  const entityTypeLabels = {
    box: '游戏盒子',
    game: '游戏',
    article: '文章',
    category: '分类',
    storageConfig: '存储配置',
    aiPlatform: 'AI平台',
    atomicTool: '原子工具',
    template: '模板',
    workflow: '工作流',
    titleBatch: '标题池批次'
  }
  const entityLabel = entityTypeLabels[props.entityType] || '内容'
  return `配置网站关联 - ${entityLabel}`
}

// 监听对话框打开
watch(visible, (newVal) => {
  if (newVal) {
    loadData()
  }
})

// 加载数据
async function loadData() {
  loading.value = true
  try {
    // 加载所有网站
    const siteRes = await listSite({ pageNum: 1, pageSize: 1000 })
    allSites.value = siteRes.rows || []
    
    // 加载已关联的网站
    await loadRelatedSites()
  } catch (error) {
    ElMessage.error('加载数据失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 获取当前 include/exclude siteId 列表（供 saveRelations 使用）
function getCurrentRelationIds() {
  const includeSiteIds = relatedSites.value.filter(s => s.relationType === 'shared').map(s => s.siteId)
  const excludeSiteIds = relatedSites.value.filter(s => s.relationType === 'exclude').map(s => s.siteId)
  return { includeSiteIds, excludeSiteIds }
}

// saveRelations API 映射（box/game/article/atomicTool/workflow/titleBatch 使用统一接口）
const saveRelationsApiMap = {
  box: (data) => batchSaveBoxSiteRelations({
    boxIds: [data.boxId],
    includeSiteIds: data.includeSiteIds,
    excludeSiteIds: data.excludeSiteIds
  }),
  game: (data) => batchSaveGameSiteRelations({
    gameIds: [data.gameId],
    includeSiteIds: data.includeSiteIds,
    excludeSiteIds: data.excludeSiteIds
  }),
  article: (data) => batchSaveArticleSiteRelations({
    articleIds: [data.articleId],
    includeSiteIds: data.includeSiteIds,
    excludeSiteIds: data.excludeSiteIds
  }),
  atomicTool: (data) => batchSaveAtomicToolSiteRelations({
    atomicToolIds: [data.atomicToolId],
    includeSiteIds: data.includeSiteIds,
    excludeSiteIds: data.excludeSiteIds
  }),
  workflow: (data) => batchSaveWorkflowSiteRelations({
    workflowIds: [data.workflowId],
    includeSiteIds: data.includeSiteIds,
    excludeSiteIds: data.excludeSiteIds
  }),
  titleBatch: (data) => batchSaveTitleBatchSiteRelations({
    batchIds: [data.titleBatchId],
    includeSiteIds: data.includeSiteIds,
    excludeSiteIds: data.excludeSiteIds
  }),
  aiPlatform: (data) => batchSaveAiPlatformSiteRelations({
    aiPlatformIds: [data.aiPlatformId],
    includeSiteIds: data.includeSiteIds,
    excludeSiteIds: data.excludeSiteIds
  }),
  category: (data) => batchSaveCategorySiteRelations({
    categoryIds: [data.categoryId],
    includeSiteIds: data.includeSiteIds,
    excludeSiteIds: data.excludeSiteIds
  }),
  storageConfig: (data) => batchSaveStorageConfigSiteRelations({
    storageConfigIds: [data.storageConfigId],
    includeSiteIds: data.includeSiteIds,
    excludeSiteIds: data.excludeSiteIds
  })
}

// 加载已关联的网站
async function loadRelatedSites() {
  let apiCall
  switch (props.entityType) {
    case 'box':
      apiCall = getBoxSites
      break
    case 'game':
      apiCall = getGameSites
      break
    case 'article':
      apiCall = getArticleSites
      break
    case 'category':
      apiCall = getCategorySites
      break
    case 'storageConfig':
      apiCall = getStorageConfigSites
      break
    case 'aiPlatform':
      apiCall = getPlatformSites
      break
    case 'atomicTool':
      apiCall = getAtomicToolSites
      break
    case 'workflow':
      apiCall = getWorkflowSites
      break
    case 'titleBatch': {
      const batchRes = await getTitleBatchSites(props.entityId)
      relatedSites.value = enrichSiteInfo(batchRes.data || [])
      return
    }
  }
  
  const res = await apiCall(props.entityId)
  const sites = res.data || []
  
  // 统一接口已经返回了包含 relation_type 的数据，无需单独查询
  relatedSites.value = enrichSiteInfo(sites)
}

// 补充网站信息（编码和域名）
function enrichSiteInfo(sites) {
  return sites.map(site => {
    const fullSiteInfo = allSites.value.find(s => s.id === site.siteId)
    // 后端返回的 relationType 值映射：
    // 'include' -> 'shared'（关联）
    // 'exclude' -> 'exclude'（排除）
    let mappedRelationType = site.relationType
    if (mappedRelationType === 'include') {
      mappedRelationType = 'shared'
    } else if (mappedRelationType === 'exclude') {
      mappedRelationType = 'exclude'
    } else {
      // 默认情况下视为正向关联
      mappedRelationType = 'shared'
    }
    return {
      ...site,
      relationType: mappedRelationType,
      siteCode: site.siteCode || fullSiteInfo?.code || '-',
      siteDomain: site.siteDomain || fullSiteInfo?.domain || '-'
    }
  })
}

// 添加到网站
async function handleAddToSites() {
  if (selectedSiteIds.value.length === 0) {
    ElMessage.warning('请选择要添加的网站')
    return
  }

  try {
    loading.value = true
    
    // 前端使用 'shared'/'exclude'，转换为后端的 'include'/'exclude'
    const backendRelationType = addRelationType.value === 'shared' ? 'include' : 'exclude'
    const { includeSiteIds, excludeSiteIds } = getCurrentRelationIds()
    const newIncludes = [...includeSiteIds]
    const newExcludes = [...excludeSiteIds]
    if (backendRelationType === 'include') {
      selectedSiteIds.value.forEach(id => { if (!newIncludes.includes(id)) newIncludes.push(id) })
    } else {
      selectedSiteIds.value.forEach(id => { if (!newExcludes.includes(id)) newExcludes.push(id) })
    }
    await saveRelationsApiMap[props.entityType]({
      [`${props.entityType}Id`]: props.entityId,
      includeSiteIds: newIncludes,
      excludeSiteIds: newExcludes
    })
    ElMessage.success('添加成功')
    selectedSiteIds.value = []
    includeGames.value = false
    addDialogVisible.value = false
    await loadRelatedSites()
    emit('refresh')
  } catch (error) {
    ElMessage.error('添加失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 复制到网站（仅游戏盒子）
async function handleCopyToSite() {
  if (!copyTargetSiteId.value) {
    ElMessage.warning('请选择目标网站')
    return
  }

  const targetSite = allSites.value.find(s => s.id === copyTargetSiteId.value)
  
  await ElMessageBox.confirm(
    `确认将游戏盒子及其内容复制到"${targetSite?.name}"吗？这将包括：
    • 游戏盒子本身
    • 盒子中的所有游戏
    • 相关分类（自动创建关联）`,
    '确认复制',
    {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    }
  )

  try {
    loading.value = true
    await copyBoxToSite({
      boxId: props.entityId,
      targetSiteId: copyTargetSiteId.value
    })
    ElMessage.success('复制成功')
    copyTargetSiteId.value = null
    await loadRelatedSites()
    emit('refresh')
  } catch (error) {
    ElMessage.error('复制失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 移除网站关联
async function handleRemoveSite(row) {
  if (row.siteId === props.creatorSiteId) {
    ElMessage.warning('无法移除创建者网站的关联')
    return
  }

  // 如果是游戏盒子，让用户直接选择移除方式
  let removeGames = false
  if (props.entityType === 'box') {
    try {
      const result = await ElMessageBox.confirm(
        `移除后，该游戏盒子将在"${row.siteName}"网站中不可见，但数据不会被删除。<br/><br/>请选择移除方式：`,
        '移除网站关联',
        {
          confirmButtonText: '同时移除盒子和游戏',
          cancelButtonText: '仅移除盒子',
          distinguishCancelAndClose: true,
          showCancelButton: true,
          showClose: true,
          type: 'warning',
          dangerouslyUseHTMLString: true
        }
      )
      // 用户点击了"同时移除盒子和游戏"
      removeGames = true
    } catch (action) {
      // 用户点击了"仅移除盒子"
      if (action === 'cancel') {
        removeGames = false
      } else {
        // 用户点击了关闭或其他，取消操作
        return
      }
    }
  } else {
    // 根据实体类型显示不同的提示信息
    const entityTypeLabels = {
      game: '游戏',
      article: '文章',
      category: '分类',
      storageConfig: '存储配置',
      aiPlatform: 'AI平台配置',
      atomicTool: '原子工具'
    }
    const entityLabel = entityTypeLabels[props.entityType] || '内容'
    
    // 根据关系类型显示不同的提示
    const actionLabel = row.relationType === 'exclude' ? '取消排除' : '移除关联'
    const actionMessage = row.relationType === 'exclude' 
      ? `取消后，该${entityLabel}将对"${row.siteName}"网站可见。确认取消排除吗？`
      : `移除后，该${entityLabel}将在"${row.siteName}"网站中不可见，但数据不会被删除。确认移除关联吗？`
    
    await ElMessageBox.confirm(
      actionMessage,
      actionLabel,
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: row.relationType === 'exclude' ? 'info' : 'warning'
      }
    )
  }

  try {
    loading.value = true
    
    const { includeSiteIds, excludeSiteIds } = getCurrentRelationIds()
    const backendRelationType = row.relationType === 'shared' ? 'include' : 'exclude'
    const newIncludes = backendRelationType === 'include'
      ? includeSiteIds.filter(id => id !== row.siteId)
      : includeSiteIds
    const newExcludes = backendRelationType === 'exclude'
      ? excludeSiteIds.filter(id => id !== row.siteId)
      : excludeSiteIds
    await saveRelationsApiMap[props.entityType]({
      [`${props.entityType}Id`]: props.entityId,
      includeSiteIds: newIncludes,
      excludeSiteIds: newExcludes
    })
    ElMessage.success('移除成功')
    await loadRelatedSites()
    emit('refresh')
  } catch (error) {
    ElMessage.error('移除失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 编辑配置
function handleEditConfig(row) {
  currentConfigSite.value = row
  configForm.value = {
    siteId: row.siteId,
    isVisible: row.isVisible || '1',
    isFeatured: row.isFeatured || '0',
    isNew: row.isNew || '0',
    isTop: row.isTop || '0',
    sortOrder: row.sortOrder || 0,
    customName: row.customName || '',
    customDescription: row.customDescription || '',
    customDownloadUrl: row.customDownloadUrl || '',
    customTitle: row.customTitle || ''
  }
  configDialogVisible.value = true
}

// 保存配置
async function handleSaveConfig() {
  try {
    const data = {
      siteId: configForm.value.siteId,
      [`${props.entityType}Id`]: props.entityId,
      ...configForm.value
    }

    switch (props.entityType) {
      case 'box':
        await updateBoxSiteConfig({ siteId: configForm.value.siteId, boxId: props.entityId, ...configForm.value })
        break
      case 'game':
        await updateGameSiteConfig({ siteId: configForm.value.siteId, gameId: props.entityId, ...configForm.value })
        break
      case 'article':
        await updateArticleSiteConfig(data)
        break
      case 'category':
        await updateCategoryVisibility(configForm.value.siteId, props.entityId, configForm.value.isVisible)
        break
      case 'storageConfig':
        await updateStorageVisibility(configForm.value.siteId, props.entityId, configForm.value.isVisible)
        break
      case 'aiPlatform':
        await updatePlatformVisibility(configForm.value.siteId, props.entityId, configForm.value.isVisible)
        break
      case 'atomicTool':
        await updateAtomicToolVisibility(configForm.value.siteId, props.entityId, configForm.value.isVisible)
        break
      case 'workflow':
        await updateWorkflowSiteConfig(configForm.value.siteId, props.entityId, configForm.value.isVisible)
        break
      case 'titleBatch':
        await updateTitleBatchVisibility(configForm.value.siteId, props.entityId, configForm.value.isVisible)
        break
      default:
        ElMessage.error(`不支持的实体类型: ${props.entityType}`)
        return
    }
    ElMessage.success('保存成功')
    configDialogVisible.value = false
    await loadRelatedSites()
    emit('refresh')
  } catch (error) {
    ElMessage.error('保存失败: ' + error.message)
  }
}

// 刷新
async function handleRefresh() {
  await loadData()
  emit('refresh')
}

// 关闭对话框
function handleClose() {
  visible.value = false
}
</script>

<style scoped>
:deep(.el-table__header) {
  font-weight: 600;
}
</style>
