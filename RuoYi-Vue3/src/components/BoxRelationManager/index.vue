<template>
  <el-dialog
    :title="getDialogTitle()"
    v-model="visible"
    width="1200px"
    append-to-body
    @close="handleClose"
  >
    <div v-loading="loading">
      <!-- 当前关联的游戏盒子 -->
      <div style="margin-bottom: 20px;">
        <div style="margin-bottom: 12px; font-weight: 500; font-size: 15px;">
          <el-icon style="margin-right: 4px;"><Box /></el-icon>
          当前关联的游戏盒子 ({{ relatedBoxes.length }})
        </div>
        
        <el-alert
          v-if="relatedBoxes.length === 0"
          type="info"
          :closable="false"
          show-icon
          style="margin-bottom: 12px;"
        >
          该游戏暂未关联到任何游戏盒子，请添加盒子关联
        </el-alert>

        <el-table
          v-else
          :data="relatedBoxes"
          border
          stripe
          style="width: 100%"
        >
          <el-table-column label="盒子名称" align="center" width="150">
            <template #default="scope">
              <el-tag type="info" effect="plain">
                {{ scope.row.boxName }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="折扣标签" align="center" width="100">
            <template #default="scope">
              <el-tag v-if="scope.row.discountLabel" type="danger" size="small">{{ scope.row.discountLabel }}</el-tag>
              <span v-else style="color: #909399;">-</span>
            </template>
          </el-table-column>
          <el-table-column label="首充折扣" align="center" width="110">
            <template #default="scope">
              <div v-if="scope.row.firstChargeDomestic || scope.row.firstChargeOverseas" style="font-size: 12px;">
                <div v-if="scope.row.firstChargeDomestic">国内: {{ scope.row.firstChargeDomestic }}</div>
                <div v-if="scope.row.firstChargeOverseas">海外: {{ scope.row.firstChargeOverseas }}</div>
              </div>
              <span v-else style="color: #909399;">-</span>
            </template>
          </el-table-column>
          <el-table-column label="续充折扣" align="center" width="110">
            <template #default="scope">
              <div v-if="scope.row.rechargeDomestic || scope.row.rechargeOverseas" style="font-size: 12px;">
                <div v-if="scope.row.rechargeDomestic">国内: {{ scope.row.rechargeDomestic }}</div>
                <div v-if="scope.row.rechargeOverseas">海外: {{ scope.row.rechargeOverseas }}</div>
              </div>
              <span v-else style="color: #909399;">-</span>
            </template>
          </el-table-column>
          <el-table-column label="扶持" align="center" width="60">
            <template #default="scope">
              <el-tag v-if="scope.row.hasSupport === '1'" type="success" size="small">有</el-tag>
              <span v-else style="color: #909399;">无</span>
            </template>
          </el-table-column>
          <el-table-column label="推荐" align="center" width="60">
            <template #default="scope">
              <el-tag v-if="scope.row.isFeatured === '1'" type="warning" size="small">推荐</el-tag>
              <span v-else style="color: #909399;">-</span>
            </template>
          </el-table-column>
          <el-table-column label="独占" align="center" width="60">
            <template #default="scope">
              <el-tag v-if="scope.row.isExclusive === '1'" type="danger" size="small">独占</el-tag>
              <span v-else style="color: #909399;">-</span>
            </template>
          </el-table-column>
          <el-table-column label="新游" align="center" width="60">
            <template #default="scope">
              <el-tag v-if="scope.row.isNew === '1'" type="success" size="small">新</el-tag>
              <span v-else style="color: #909399;">-</span>
            </template>
          </el-table-column>
          <el-table-column label="推广链接" align="center" width="80">
            <template #default="scope">
              <el-tooltip v-if="scope.row.promotionLinks" :content="scope.row.promotionLinks" placement="top" :show-after="300">
                <el-tag type="success" size="small">有</el-tag>
              </el-tooltip>
              <span v-else style="color: #909399;">-</span>
            </template>
          </el-table-column>
          <el-table-column label="平台数据" align="center" width="80">
            <template #default="scope">
              <el-tooltip v-if="scope.row.platformData" :content="scope.row.platformData" placement="top" :show-after="300">
                <el-tag type="warning" size="small">有</el-tag>
              </el-tooltip>
              <span v-else style="color: #909399;">-</span>
            </template>
          </el-table-column>
          <el-table-column label="排序" align="center" width="70" prop="sortOrder" />
          <el-table-column label="操作" align="center" width="150" fixed="right">
            <template #default="scope">
              <template v-if="readonly">
                <el-button
                  link
                  type="primary"
                  size="small"
                  icon="View"
                  @click="handleEditConfig(scope.row)"
                >
                  查看
                </el-button>
              </template>
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
                  @click="handleRemoveBox(scope.row)"
                >
                  移除
                </el-button>
              </template>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 添加到其他游戏盒子（只有非只读模式才显示） -->
      <div v-if="!readonly" style="margin-bottom: 20px;">
        <div style="margin-bottom: 12px; font-weight: 500; font-size: 15px;">
          <el-icon style="margin-right: 4px;"><Plus /></el-icon>
          添加到其他游戏盒子
        </div>
        
        <div style="display: flex; gap: 12px; align-items: flex-start;">
          <el-select
            v-model="selectedBoxIds"
            placeholder="选择要添加的游戏盒子"
            multiple
            filterable
            style="flex: 1;"
            size="default"
          >
            <el-option
              v-for="box in availableBoxes"
              :key="box.id"
              :label="box.name"
              :value="box.id"
            />
          </el-select>
          <el-button
            type="primary"
            icon="Plus"
            @click="handleAddToBoxes"
            :disabled="selectedBoxIds.length === 0"
            size="default"
          >
            添加
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
      :title="readonly ? '查看游戏盒子关联详情' : '配置游戏盒子关联'"
      v-model="configDialogVisible"
      width="700px"
      append-to-body
    >
      <el-form :model="configForm" label-width="130px">
        <el-form-item label="游戏盒子">
          <el-input :value="currentConfigBox?.boxName" disabled />
        </el-form-item>
        
        <el-divider content-position="left" style="margin: 20px 0;">折扣配置</el-divider>
        <el-form-item label="折扣标签">
          <el-input v-model="configForm.discountLabel" placeholder="如: 5折, 满减等" maxlength="20" :disabled="readonly" />
        </el-form-item>
        <el-form-item label="首充折扣-国内">
          <el-input-number v-model="configForm.firstChargeDomestic" :min="0" :max="99.99" :precision="2" :step="0.1" placeholder="如: 0.5表示5折" :disabled="readonly" />
        </el-form-item>
        <el-form-item label="首充折扣-海外">
          <el-input-number v-model="configForm.firstChargeOverseas" :min="0" :max="99.99" :precision="2" :step="0.1" placeholder="如: 0.5表示5折" :disabled="readonly" />
        </el-form-item>
        <el-form-item label="续充折扣-国内">
          <el-input-number v-model="configForm.rechargeDomestic" :min="0" :max="99.99" :precision="2" :step="0.1" placeholder="如: 0.8表示8折" :disabled="readonly" />
        </el-form-item>
        <el-form-item label="续充折扣-海外">
          <el-input-number v-model="configForm.rechargeOverseas" :min="0" :max="99.99" :precision="2" :step="0.1" placeholder="如: 0.8表示8折" :disabled="readonly" />
        </el-form-item>
        
        <el-divider content-position="left" style="margin: 20px 0;">扶持信息</el-divider>
        <el-form-item label="是否有扶持">
          <el-radio-group v-model="configForm.hasSupport" :disabled="readonly">
            <el-radio value="1">有</el-radio>
            <el-radio value="0">无</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="扶持说明" v-if="configForm.hasSupport === '1'">
          <el-input v-model="configForm.supportDesc" type="textarea" :rows="2" placeholder="请输入扶持说明" :disabled="readonly" />
        </el-form-item>
        
        <el-divider content-position="left" style="margin: 20px 0;">链接配置</el-divider>
        <el-form-item label="下载链接">
          <el-input v-model="configForm.downloadUrl" placeholder="游戏下载链接" :disabled="readonly" />
        </el-form-item>
        <el-form-item label="推广链接">
          <el-input v-model="configForm.promoteUrl" placeholder="游戏推广网站链接" :disabled="readonly" />
        </el-form-item>
        <el-form-item label="二维码URL">
          <el-input v-model="configForm.qrcodeUrl" placeholder="游戏二维码URL" :disabled="readonly" />
        </el-form-item>
        <el-form-item label="海报URL">
          <el-input v-model="configForm.posterUrl" placeholder="宣传卡片/海报URL" :disabled="readonly" />
        </el-form-item>
        
        <el-divider content-position="left" style="margin: 20px 0;">推广文案</el-divider>
        <el-form-item label="推广语">
          <el-input v-model="configForm.promoteText" type="textarea" :rows="3" placeholder="请输入推广语" :disabled="readonly" />
        </el-form-item>
        
        <el-divider content-position="left" style="margin: 20px 0;">标记与排序</el-divider>
        <el-form-item label="推荐">
          <el-radio-group v-model="configForm.isFeatured" :disabled="readonly">
            <el-radio value="1">是</el-radio>
            <el-radio value="0">否</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="独占">
          <el-radio-group v-model="configForm.isExclusive" :disabled="readonly">
            <el-radio value="1">是</el-radio>
            <el-radio value="0">否</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="新游标记">
          <el-radio-group v-model="configForm.isNew" :disabled="readonly">
            <el-radio value="1">是</el-radio>
            <el-radio value="0">否</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="configForm.sortOrder" :min="0" :max="9999" :disabled="readonly" />
        </el-form-item>
        
        <el-divider content-position="left" style="margin: 20px 0;">自定义配置</el-divider>
        <el-form-item label="自定义名称">
          <el-input v-model="configForm.customName" placeholder="留空则使用默认名称" :disabled="readonly" />
        </el-form-item>
        <el-form-item label="自定义描述">
          <el-input v-model="configForm.customDescription" type="textarea" :rows="3" placeholder="留空则使用默认描述" :disabled="readonly" />
        </el-form-item>
        <el-form-item label="自定义下载链接">
          <el-input v-model="configForm.customDownloadUrl" placeholder="留空则使用默认下载链接" :disabled="readonly" />
        </el-form-item>

        <el-divider content-position="left" style="margin: 20px 0;">
          <el-icon style="margin-right: 4px;"><Link /></el-icon>推广链接
        </el-divider>
        <div v-if="parsedPromotionLinks && Object.keys(parsedPromotionLinks).length > 0"
             style="padding: 15px; background: #f5f7fa; border-radius: 4px; border: 1px solid #e4e7ed; margin-bottom: 12px;">
          <el-row :gutter="15" v-for="(value, key) in parsedPromotionLinks" :key="key" style="margin-bottom: 10px;">
            <el-col :span="6">
              <div style="height: 32px; display: flex; align-items: center; justify-content: flex-end; font-size: 13px; color: #606266; font-weight: 500; padding-right: 12px;">
                {{ getFieldLabel(key, 'promotion_link') }}:
              </div>
            </el-col>
            <el-col :span="18">
              <el-input
                :model-value="String(value)"
                @update:model-value="updatePromotionLinkField(key, $event)"
                placeholder="请输入推广链接"
                :disabled="readonly"
              >
                <template #append v-if="value && String(value).startsWith('http')">
                  <el-link :href="String(value)" target="_blank" type="primary" style="text-decoration: none;">
                    <el-icon><View /></el-icon>
                  </el-link>
                </template>
              </el-input>
            </el-col>
          </el-row>
        </div>

        <el-divider content-position="left" style="margin: 20px 0;">
          <el-icon style="margin-right: 4px;"><Setting /></el-icon>平台专属数据
        </el-divider>
        <div v-if="parsedPlatformData && Object.keys(parsedPlatformData).length > 0"
             style="padding: 15px; background: #f5f7fa; border-radius: 4px; border: 1px solid #e4e7ed; margin-bottom: 12px;">
          <el-row :gutter="15" v-for="(value, key) in parsedPlatformData" :key="key" style="margin-bottom: 10px;">
            <template v-if="!String(key).includes('original_data') && !String(key).includes('import_time')">
              <el-col :span="6">
                <div style="height: 32px; display: flex; align-items: center; justify-content: flex-end; font-size: 13px; color: #606266; font-weight: 500; padding-right: 12px;">
                  {{ getFieldLabel(key, 'platform_data') }}:
                </div>
              </el-col>
              <el-col :span="18">
                <template v-if="typeof value === 'object' && value !== null">
                  <el-input
                    :model-value="JSON.stringify(value, null, 2)"
                    @update:model-value="updatePlatformDataField(key, $event, true)"
                    type="textarea"
                    :rows="3"
                    placeholder="JSON 对象数据"
                    :disabled="readonly"
                  />
                </template>
                <template v-else>
                  <el-input
                    :model-value="String(value)"
                    @update:model-value="updatePlatformDataField(key, $event)"
                    placeholder="请输入内容"
                    :disabled="readonly"
                  />
                </template>
              </el-col>
            </template>
          </el-row>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="configDialogVisible = false">关闭</el-button>
        <el-button v-if="!readonly" type="primary" @click="handleSaveConfig">保存</el-button>
      </template>
    </el-dialog>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Box, Plus, Edit, Delete, Refresh, View, Link, Setting } from '@element-plus/icons-vue'
import { 
  getGameBoxes, 
  addGameToBox, 
  removeGameFromBox, 
  updateGameBoxConfig 
} from '@/api/gamebox/boxRelation'
import { listBox } from '@/api/gamebox/box'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  gameId: {
    type: [Number, null],
    default: null
  },
  gameName: {
    type: String,
    default: ''
  },
  readonly: {
    type: Boolean,
    default: false
  },
  siteId: {
    type: [Number, null],
    default: null
  }
})

const emit = defineEmits(['update:modelValue', 'refresh'])

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const loading = ref(false)
const relatedBoxes = ref([])
const allBoxes = ref([])
const selectedBoxIds = ref([])

// 配置对话框
const configDialogVisible = ref(false)
const currentConfigBox = ref(null)
const configForm = ref({
  discountLabel: '',
  firstChargeDomestic: null,
  firstChargeOverseas: null,
  rechargeDomestic: null,
  rechargeOverseas: null,
  hasSupport: '0',
  supportDesc: '',
  downloadUrl: '',
  promoteUrl: '',
  qrcodeUrl: '',
  promoteText: '',
  posterUrl: '',
  isFeatured: '0',
  isExclusive: '0',
  isNew: '0',
  sortOrder: 0,
  customName: '',
  customDescription: '',
  customDownloadUrl: '',
  promotionLinks: '',
  platformData: ''
})

// 字段映射缓存 key: boxId, value: { promotion_link: Map<targetField, remark>, platform_data: Map }
const fieldMappingCache = ref(new Map())

// 动态加载指定盒子的字段映射
async function loadBoxFieldMappings(boxId) {
  if (!boxId || fieldMappingCache.value.has(boxId)) return
  try {
    const { listFieldMappingByBoxId } = await import('@/api/gamebox/fieldMapping')
    const response = await listFieldMappingByBoxId(boxId)
    const promotionLinkMap = new Map()
    const platformDataMap = new Map()
    ;(response.data || []).forEach(m => {
      const label = m.remark || m.targetField
      if (m.targetLocation === 'promotion_link') {
        promotionLinkMap.set(m.targetField, label)
      } else if (m.targetLocation === 'platform_data') {
        platformDataMap.set(m.targetField, label)
      }
    })
    fieldMappingCache.value.set(boxId, { promotion_link: promotionLinkMap, platform_data: platformDataMap })
  } catch (e) {
    console.error('加载字段映射失败:', e)
  }
}

function getFieldLabel(key, type = 'promotion_link') {
  const boxId = currentConfigBox.value?.boxId
  if (boxId && fieldMappingCache.value.has(boxId)) {
    const map = fieldMappingCache.value.get(boxId)[type]
    if (map && map.has(key)) return `${map.get(key)} (${key})`
  }
  return key
}

// 解析 promotionLinks JSON
const parsedPromotionLinks = computed(() => {
  return parseJson(configForm.value.promotionLinks)
})

// 解析 platformData JSON
const parsedPlatformData = computed(() => {
  return parseJson(configForm.value.platformData)
})

// 解析 JSON 字符串为对象
function parseJson(jsonString) {
  if (!jsonString) return {}
  try {
    const result = JSON.parse(jsonString)
    return typeof result === 'object' && result !== null ? result : {}
  } catch (e) {
    return {}
  }
}

// 递归对对象键排序，保证 JSON 序列化稳定
function sortObjectKeys(obj) {
  if (Array.isArray(obj)) return obj.map(sortObjectKeys)
  if (obj !== null && typeof obj === 'object') {
    return Object.keys(obj).sort().reduce((acc, key) => {
      acc[key] = sortObjectKeys(obj[key])
      return acc
    }, {})
  }
  return obj
}
function sortedStringify(obj) {
  return JSON.stringify(sortObjectKeys(obj))
}

// 更新推广链接字段
function updatePromotionLinkField(key, value) {
  const obj = parseJson(configForm.value.promotionLinks)
  obj[key] = value
  configForm.value.promotionLinks = sortedStringify(obj)
}

// 更新平台数据字段
function updatePlatformDataField(key, value, isJson = false) {
  const obj = parseJson(configForm.value.platformData)
  if (isJson && value) {
    try { obj[key] = JSON.parse(value) } catch (e) { obj[key] = value }
  } else {
    obj[key] = value
  }
  configForm.value.platformData = sortedStringify(obj)
}

// 可添加的游戏盒子（排除已关联的）
const availableBoxes = computed(() => {
  const relatedBoxIds = relatedBoxes.value.map(b => b.boxId)
  return allBoxes.value.filter(b => !relatedBoxIds.includes(b.id))
})

// 获取对话框标题
function getDialogTitle() {
  return `${props.readonly ? '查看' : '管理'}游戏盒子关联 - ${props.gameName}`
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
    // 只有创建者模式（非只读）才需要加载所有盒子，用于"添加"功能
    if (!props.readonly) {
      const boxQuery = { pageNum: 1, pageSize: 10000 }
      if (props.siteId) {
        boxQuery.siteId = props.siteId
      }
      const boxRes = await listBox(boxQuery)
      allBoxes.value = boxRes.rows || []
    } else {
      allBoxes.value = []
    }

    // 加载已关联的游戏盒子
    await loadRelatedBoxes()
  } catch (error) {
    ElMessage.error('加载数据失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 加载已关联的游戏盒子
async function loadRelatedBoxes() {
  const siteId = (props.readonly && props.siteId) ? props.siteId : null
  const res = await getGameBoxes(props.gameId, siteId)
  relatedBoxes.value = res.data || []
}

// 添加到游戏盒子
async function handleAddToBoxes() {
  if (selectedBoxIds.value.length === 0) {
    ElMessage.warning('请选择要添加的游戏盒子')
    return
  }

  try {
    loading.value = true
    
    // 批量添加
    const data = {
      gameId: props.gameId,
      boxIds: selectedBoxIds.value
    }

    await addGameToBox(data)
    
    ElMessage.success('添加成功')
    
    selectedBoxIds.value = []
    await loadRelatedBoxes()
    emit('refresh')
  } catch (error) {
    ElMessage.error('添加失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 移除游戏盒子关联
async function handleRemoveBox(row) {
  await ElMessageBox.confirm(
    `移除后，该游戏将在"${row.boxName}"游戏盒子中不可见，但数据不会被删除。`,
    '移除游戏盒子关联',
    {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    }
  )

  try {
    loading.value = true
    await removeGameFromBox({
      gameId: props.gameId,
      boxId: row.boxId
    })
    ElMessage.success('移除成功')
    await loadRelatedBoxes()
    emit('refresh')
  } catch (error) {
    ElMessage.error('移除失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 编辑配置
function handleEditConfig(row) {
  currentConfigBox.value = row
  configForm.value = {
    discountLabel: row.discountLabel || '',
    firstChargeDomestic: row.firstChargeDomestic || null,
    firstChargeOverseas: row.firstChargeOverseas || null,
    rechargeDomestic: row.rechargeDomestic || null,
    rechargeOverseas: row.rechargeOverseas || null,
    hasSupport: row.hasSupport || '0',
    supportDesc: row.supportDesc || '',
    downloadUrl: row.downloadUrl || '',
    promoteUrl: row.promoteUrl || '',
    qrcodeUrl: row.qrcodeUrl || '',
    promoteText: row.promoteText || '',
    posterUrl: row.posterUrl || '',
    isFeatured: row.isFeatured || '0',
    isExclusive: row.isExclusive || '0',
    isNew: row.isNew || '0',
    sortOrder: row.sortOrder || 0,
    customName: row.customName || '',
    customDescription: row.customDescription || '',
    customDownloadUrl: row.customDownloadUrl || '',
    promotionLinks: row.promotionLinks || '',
    platformData: row.platformData || ''
  }
  // 加载该盒子的字段映射配置
  loadBoxFieldMappings(row.boxId)
  configDialogVisible.value = true
}

// 保存配置
async function handleSaveConfig() {
  try {
    loading.value = true
    await updateGameBoxConfig({
      gameId: props.gameId,
      boxId: currentConfigBox.value.boxId,
      ...configForm.value
    })
    ElMessage.success('配置已保存')
    configDialogVisible.value = false
    await loadRelatedBoxes()
    emit('refresh')
  } catch (error) {
    ElMessage.error('保存失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 刷新
async function handleRefresh() {
  await loadData()
  ElMessage.success('刷新成功')
}

// 关闭对话框
function handleClose() {
  visible.value = false
  selectedBoxIds.value = []
}
</script>

<style scoped>
</style>
