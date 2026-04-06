<template>
  <el-dialog title="全站数据导入" v-model="dialogVisible" width="800px" append-to-body @close="handleClose">
    <div style="margin: 20px 0;">
      <el-alert 
        title="全站导入说明" 
        type="warning" 
        :closable="false"
        style="margin-bottom: 20px;"
      >
        <template #default>
          <div style="font-size: 13px; line-height: 1.5;">
            <slot name="importTips">
              <p><strong>适用场景：</strong></p>
              <p>• 系统数据迁移：将完整数据从一个系统导入到另一个系统</p>
              <p>• 备份数据恢复：恢复之前导出的全站备份数据</p>
              <p style="margin-top: 10px;"><strong>导入流程：</strong></p>
              <p>1. 上传全站导出的文件（包含网站列表）</p>
              <p>2. 系统识别文件中的网站数据</p>
              <p>3. 选择网站映射关系（源网站 → 目标网站）</p>
              <p>4. 开始导入数据、翻译和关联关系</p>
              <p style="margin-top: 10px; color: #F56C6C;"><strong>⚠️ 注意事项：</strong></p>
              <p>• 如果包含默认配置(siteid=0)，需要选择是否创建为新网站</p>
              <p>• 对于真实网站数据，请映射到已存在的目标网站</p>
              <p>• 建议先在目标系统创建好相应的网站</p>
            </slot>
          </div>
        </template>
      </el-alert>
      
      <!-- 第一步：上传文件 -->
      <div v-if="!importSites.length">
        <el-upload
          ref="uploadRef"
          :auto-upload="false"
          :show-file-list="true"
          :limit="1"
          accept=".xlsx,.json"
          :on-change="handleFileChange"
          :before-remove="handleFileRemove"
          drag
        >
          <el-icon class="el-icon--upload"><upload-filled /></el-icon>
          <div class="el-upload__text">
            将全站导出文件拖到此处，或<em>点击上传</em>
          </div>
          <template #tip>
            <div class="el-upload__tip">
              支持 .xlsx 和 .json 格式文件，必须是通过"全站导出"功能导出的文件
            </div>
          </template>
        </el-upload>
      </div>
      
      <!-- 第二步：配置网站映射 -->
      <div v-else>
        <el-divider content-position="left">
          <el-icon><Connection /></el-icon>
          网站映射配置
        </el-divider>
        
        <!-- 默认配置处理 -->
        <div v-if="hasDefaultConfig" style="margin-bottom: 20px; padding: 15px; background: #FFF7E6; border: 1px solid #FFD591; border-radius: 4px;">
          <h4 style="margin: 0 0 10px 0; color: #FA8C16;">
            <el-icon style="margin-right: 5px;"><InfoFilled /></el-icon>
            检测到默认配置数据 (siteid=0)
          </h4>
          <el-checkbox v-model="localCreateDefaultAsNewSite">
            将默认配置导入为新网站的数据
          </el-checkbox>
          <div v-if="localCreateDefaultAsNewSite" style="margin-top: 10px; padding: 10px; background: #FFF; border: 1px solid #D9D9D9; border-radius: 4px;">
            <el-alert type="info" :closable="false" show-icon>
              <template #default>
                <div style="font-size: 12px;">
                  <slot name="defaultConfigTips">
                    <p>选择目标网站后，默认配置将：</p>
                    <p>1. 创建为该网站的数据</p>
                    <p>2. 自动为其他真实网站创建关联关系</p>
                    <p style="margin-top: 5px; color: #F56C6C;">⚠️ 请确保已在系统中创建好目标网站</p>
                  </slot>
                </div>
              </template>
            </el-alert>
            <el-form-item label="目标网站" style="margin-top: 10px; margin-bottom: 0;">
              <el-select v-model="localSiteMapping[0]" placeholder="选择默认配置要导入到的网站" style="width: 100%">
                <el-option 
                  v-for="site in siteList" 
                  :key="site.id" 
                  :label="`${site.name} (${site.code})`" 
                  :value="site.id" 
                />
              </el-select>
            </el-form-item>
          </div>
        </div>
        
        <!-- 真实网站映射 -->
        <div v-if="realSites.length > 0">
          <h4 style="margin: 15px 0 10px 0;">
            <el-icon style="margin-right: 5px;"><Document /></el-icon>
            真实网站数据映射 ({{ realSites.length }} 个网站)
          </h4>
          <el-table :data="realSites" border size="small" max-height="300">
            <el-table-column label="源网站虚拟ID" prop="网站虚拟ID" width="120" align="center" />
            <el-table-column label="源网站名称" width="150">
              <template #default="scope">
                <div>
                  <div><strong>{{ scope.row['网站名称'] }}</strong></div>
                  <div style="font-size: 12px; color: #909399;">{{ scope.row['网站编码'] }}</div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="源网站域名" prop="域名" width="180" show-overflow-tooltip />
            <el-table-column label="→ 目标网站" width="250">
              <template #default="scope">
                <el-select 
                  v-model="localSiteMapping[scope.row['网站虚拟ID']]" 
                  placeholder="选择目标网站" 
                  size="small"
                  style="width: 100%"
                >
                  <el-option 
                    v-for="site in siteList" 
                    :key="site.id" 
                    :label="`${site.name} (${site.code})`" 
                    :value="site.id" 
                  />
                </el-select>
              </template>
            </el-table-column>
          </el-table>
        </div>
        
        <!-- 数据预览 -->
        <el-divider content-position="left" style="margin-top: 20px;">
          <el-icon><DataAnalysis /></el-icon>
          数据统计
        </el-divider>
        <el-row :gutter="15">
          <el-col :span="6">
            <el-statistic title="网站数量" :value="importSites.length">
              <template #suffix>
                <el-icon><OfficeBuilding /></el-icon>
              </template>
            </el-statistic>
          </el-col>
          <el-col :span="6">
            <el-statistic :title="dataLabel" :value="importData.length">
              <template #suffix>
                <el-icon><Folder /></el-icon>
              </template>
            </el-statistic>
          </el-col>
          <el-col :span="6">
            <el-statistic title="翻译数量" :value="importTranslations.length">
              <template #suffix>
                <el-icon><ChatLineRound /></el-icon>
              </template>
            </el-statistic>
          </el-col>
          <el-col :span="6">
            <el-statistic title="关联关系" :value="importRelations.length">
              <template #suffix>
                <el-icon><Connection /></el-icon>
              </template>
            </el-statistic>
          </el-col>
        </el-row>
      </div>
    </div>
    
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取 消</el-button>
        <el-button 
          v-if="importSites.length > 0"
          type="primary" 
          @click="handleConfirm" 
          :loading="loading"
          :disabled="!isMappingValid"
        >
          <el-icon style="margin-right: 5px;"><Upload /></el-icon>
          开始导入
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed } from 'vue'
import { UploadFilled, Upload, Connection, InfoFilled, Document, DataAnalysis, OfficeBuilding, Folder, ChatLineRound } from '@element-plus/icons-vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  loading: {
    type: Boolean,
    default: false
  },
  siteList: {
    type: Array,
    default: () => []
  },
  importSites: {
    type: Array,
    default: () => []
  },
  importData: {
    type: Array,
    default: () => []
  },
  importTranslations: {
    type: Array,
    default: () => []
  },
  importRelations: {
    type: Array,
    default: () => []
  },
  siteMapping: {
    type: Object,
    default: () => ({})
  },
  createDefaultAsNewSite: {
    type: Boolean,
    default: false
  },
  dataLabel: {
    type: String,
    default: '数据数量'
  }
})

const emit = defineEmits([
  'update:modelValue', 
  'confirm', 
  'fileChange', 
  'fileRemove',
  'update:importSites',
  'update:importData',
  'update:importTranslations',
  'update:importRelations',
  'update:siteMapping',
  'update:createDefaultAsNewSite'
])

const uploadRef = ref(null)

const dialogVisible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const localSiteMapping = computed({
  get: () => props.siteMapping,
  set: (val) => emit('update:siteMapping', val)
})

const localCreateDefaultAsNewSite = computed({
  get: () => props.createDefaultAsNewSite,
  set: (val) => emit('update:createDefaultAsNewSite', val)
})

const hasDefaultConfig = computed(() => {
  return props.importSites.some(site => site['网站虚拟ID'] === 0)
})

const realSites = computed(() => {
  return props.importSites.filter(s => s['网站虚拟ID'] > 0)
})

const isMappingValid = computed(() => {
  // 如果有默认配置且选择创建为新网站，需要映射
  if (hasDefaultConfig.value && props.createDefaultAsNewSite) {
    if (!props.siteMapping[0]) {
      return false
    }
  }
  
  // 检查所有真实网站是否都已映射
  for (const site of realSites.value) {
    if (!props.siteMapping[site['网站虚拟ID']]) {
      return false
    }
  }
  
  return true
})

const handleClose = () => {
  emit('update:modelValue', false)
  emit('update:importSites', [])
  emit('update:importData', [])
  emit('update:importTranslations', [])
  emit('update:importRelations', [])
  emit('update:siteMapping', {})
  emit('update:createDefaultAsNewSite', false)
}

const handleFileChange = (file) => {
  emit('fileChange', file)
}

const handleFileRemove = () => {
  emit('fileRemove')
  emit('update:importSites', [])
  emit('update:importData', [])
  emit('update:importTranslations', [])
  emit('update:importRelations', [])
  emit('update:siteMapping', {})
  emit('update:createDefaultAsNewSite', false)
}

const handleConfirm = () => {
  emit('confirm')
}
</script>
