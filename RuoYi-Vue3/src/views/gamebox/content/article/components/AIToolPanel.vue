<template>
  <el-drawer
    v-model="dialogVisible"
    :title="mode === 'generate' ? 'AI内容生成' : 'AI内容优化'"
    direction="rtl"
    size="650px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <div class="ai-tool-container" v-loading="loading">
      <!-- 第一部分：选择AI平台和模板 -->
      <el-form :model="formData" ref="formRef" label-width="100px">
        <el-form-item label="AI平台" required>
          <el-select 
            v-model="formData.platformId" 
            placeholder="请选择AI平台" 
            style="width: 100%"
            @change="handlePlatformChange"
            :loading="loading"
          >
            <el-option
              v-for="platform in platformList"
              :key="platform.id"
              :label="platform.name"
              :value="platform.id"
              :disabled="platform.status !== '1'"
            >
              <div style="display: flex; justify-content: space-between; align-items: center;">
                <span>{{ platform.name }}</span>
                <el-tag size="small" type="info">{{ platform.platformType }}</el-tag>
              </div>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="模板类型">
          <el-select 
            v-model="filterTemplateType" 
            placeholder="全部类型" 
            clearable
            filterable
            allow-create
            default-first-option
            style="width: 100%"
            @change="handleTemplateTypeChange"
          >
            <el-option label="全部类型" value="" />
            <el-option 
              v-for="typeOption in availableTemplateTypes" 
              :key="typeOption.value" 
              :label="typeOption.label" 
              :value="typeOption.value" 
            />
          </el-select>
        </el-form-item>

        <el-form-item label="内容工具" required>
          <el-select 
            v-model="formData.templateId" 
            placeholder="请选择内容工具" 
            style="width: 100%"
            @change="handleTemplateChange"
            filterable
          >
            <el-option-group
              v-for="group in groupedTemplates"
              :key="group.label"
              :label="group.label"
            >
              <el-option
                v-for="template in group.options"
                :key="template.id"
                :label="template.toolName"
                :value="template.id"
                :disabled="template.enabled !== '1'"
              >
                <div style="display: flex; align-items: center; justify-content: space-between;">
                  <span>{{ template.toolName }}</span>
                  <el-tag 
                    v-if="template.scenario" 
                    size="small" 
                    :type="getScenarioTagType(template.scenario)"
                  >
                    {{ getScenarioLabel(template.scenario) }}
                  </el-tag>
                </div>
              </el-option>
            </el-option-group>
          </el-select>
        </el-form-item>

        <!-- 模板描述 -->
        <el-alert 
          v-if="selectedTemplate && selectedTemplate.toolDescription"
          :title="selectedTemplate.toolDescription" 
          type="info" 
          :closable="false"
          show-icon
          style="margin-bottom: 20px;"
        />

        <!-- 模板变量 -->
        <div v-if="templateVariables.length > 0">
          <el-divider content-position="left">
            <el-icon><Setting /></el-icon>
            <span style="margin-left: 8px;">模板参数</span>
          </el-divider>

          <el-form-item 
            v-for="variable in templateVariables" 
            :key="variable.name"
            :label="variable.label"
            :required="variable.required"
          >
            <!-- 文本输入 -->
            <el-input 
              v-if="variable.type === 'text' || !variable.type" 
              v-model="formData.variables[variable.name]" 
              :placeholder="variable.placeholder || '请输入' + variable.label"
              :maxlength="variable.maxLength || 200"
              show-word-limit
            />
            
            <!-- 多行文本 -->
            <el-input 
              v-else-if="variable.type === 'textarea'" 
              v-model="formData.variables[variable.name]" 
              type="textarea"
              :rows="variable.rows || 3"
              :placeholder="variable.placeholder || '请输入' + variable.label"
              :maxlength="variable.maxLength || 1000"
              show-word-limit
            />
            
            <!-- 数字输入 -->
            <el-input-number 
              v-else-if="variable.type === 'number'" 
              v-model="formData.variables[variable.name]" 
              :min="variable.min || 0"
              :max="variable.max || 10000"
              style="width: 100%"
            />
            
            <!-- 下拉选择 -->
            <el-select 
              v-else-if="variable.type === 'select'" 
              v-model="formData.variables[variable.name]" 
              :placeholder="'请选择' + variable.label"
              style="width: 100%"
              :multiple="variable.multiple"
            >
              <el-option
                v-for="option in variable.options"
                :key="option.value"
                :label="option.label"
                :value="option.value"
              />
            </el-select>
          </el-form-item>
        </div>

        <!-- AI参数 -->
        <el-divider content-position="left">
          <el-icon><Tools /></el-icon>
          <span style="margin-left: 8px;">AI参数</span>
        </el-divider>

        <el-form-item label="Temperature">
          <el-slider 
            v-model="formData.temperature" 
            :min="0" 
            :max="2" 
            :step="0.1"
            :marks="{ 0: '0', 0.7: '0.7', 1: '1', 2: '2' }"
            show-tooltip
          />
          <div style="color: #909399; font-size: 12px; margin-top: 4px;">
            较低值更保守，较高值更有创造性
          </div>
        </el-form-item>

        <el-form-item label="最大Token数">
          <el-input-number 
            v-model="formData.maxTokens" 
            :min="100" 
            :max="32000" 
            :step="100"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="生成数量">
          <el-input-number 
            v-model="formData.n" 
            :min="1" 
            :max="3"
            style="width: 100%"
          />
          <div style="color: #909399; font-size: 12px; margin-top: 4px;">
            生成多个版本供选择
          </div>
        </el-form-item>
      </el-form>

      <!-- 生成结果区域 -->
      <div v-if="generatedResults.length > 0" style="margin-top: 20px;">
        <el-divider content-position="left">
          <el-icon><Document /></el-icon>
          <span style="margin-left: 8px;">生成结果</span>
        </el-divider>

        <el-alert 
          title="内容生成成功！请选择一个版本插入到编辑器" 
          type="success" 
          :closable="false"
          show-icon
          style="margin-bottom: 15px;"
        />

        <el-tabs v-model="selectedResultIndex" type="card">
          <el-tab-pane 
            v-for="(result, index) in generatedResults" 
            :key="index" 
            :label="'版本 ' + (index + 1)"
            :name="String(index)"
          >
            <div class="result-preview">
              <div class="result-meta">
                <span>字数：{{ getWordCount(result.content) }}</span>
                <span v-if="result.model">模型：{{ result.model }}</span>
              </div>
              <el-divider />
              <div class="result-content">
                <MdPreview 
                  :modelValue="result.content" 
                  :theme="'light'"
                  style="background: transparent;"
                />
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>

      <!-- 加载中 -->
      <div v-if="generating" class="generating-overlay">
        <div class="generating-box">
          <el-icon class="is-loading" :size="48"><Loading /></el-icon>
          <p style="margin-top: 20px; font-size: 16px; color: #606266;">正在生成内容，请稍候...</p>
          <p style="margin-top: 8px; font-size: 14px; color: #909399;">
            这可能需要几十秒钟，请耐心等待
          </p>
        </div>
      </div>

      <!-- 错误信息 -->
      <el-alert 
        v-if="errorMessage"
        :title="errorMessage" 
        type="error" 
        :closable="true"
        @close="errorMessage = ''"
        show-icon
        style="margin-top: 20px;"
      />
    </div>

    <!-- 底部按钮 -->
    <template #footer>
      <div style="display: flex; justify-content: flex-end; gap: 10px;">
        <el-button @click="handleClose">取消</el-button>
        <el-button 
          type="primary" 
          @click="handleGenerate"
          :loading="generating"
          :disabled="!canGenerate || generating"
        >
          {{ generating ? '生成中...' : '开始生成' }}
        </el-button>
        <el-button 
          v-if="generatedResults.length > 0" 
          type="success" 
          @click="handleInsertContent"
        >
          插入到编辑器
        </el-button>
      </div>
    </template>
  </el-drawer>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { MdPreview } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import { 
  getAvailablePlatforms, 
  getAvailableTemplates,
  getTemplateVariables, 
  processContent
} from '@/api/gamebox/aiTool'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  mode: {
    type: String,
    default: 'generate', // generate | optimize
    validator: (value) => ['generate', 'optimize'].includes(value)
  },
  // 当前文章内容（用于优化模式）
  currentContent: {
    type: String,
    default: ''
  },
  // 当前文章标题
  currentTitle: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:modelValue', 'insert-content'])

// 对话框显示状态
const dialogVisible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

// 平台列表
const platformList = ref([])

// 模板列表（所有模板，用于提取类型）
const allTemplateList = ref([])

// 模板列表（过滤后的）
const templateList = ref([])

// 模板类型筛选
const filterTemplateType = ref('')

// 可用的模板类型列表（从已有模板中提取）
const availableTemplateTypes = computed(() => {
  const types = new Set()
  allTemplateList.value.forEach(template => {
    if (template.templateType) {
      types.add(template.templateType)
    }
  })
  
  // 转换为数组并添加标签映射
  return Array.from(types).map(type => ({
    label: getTemplateTypeLabel(type),
    value: type
  }))
})

// 选中的模板
const selectedTemplate = ref(null)

// 模板变量定义
const templateVariables = ref([])

// 表单数据
const formData = reactive({
  platformId: null,
  templateId: null,
  variables: {},
  temperature: 0.7,
  maxTokens: 2000,
  n: 1
})

// 表单引用
const formRef = ref(null)

// 加载状态
const loading = ref(false)

// 生成状态
const generating = ref(false)

// 生成结果
const generatedResults = ref([])
const selectedResultIndex = ref('0')

// 错误信息
const errorMessage = ref('')

// 按场景分组的模板
const groupedTemplates = computed(() => {
  const groups = {}
  
  templateList.value.forEach(template => {
    const scenario = template.scenario || 'general'
    if (!groups[scenario]) {
      groups[scenario] = []
    }
    groups[scenario].push(template)
  })

  return Object.keys(groups).map(key => ({
    label: getScenarioLabel(key),
    options: groups[key]
  }))
})

// 判断是否可以生成
const canGenerate = computed(() => {
  return formData.platformId && formData.templateId
})

// 获取场景标签
function getScenarioLabel(scenario) {
  const labels = {
    'game': '游戏',
    'drama': '短剧',
    'general': '通用'
  }
  return labels[scenario] || scenario
}

// 获取模板类型标签
function getTemplateTypeLabel(type) {
  const labels = {
    'article': '文章生成',
    'optimize': '文章优化',
    'game': '游戏攻略',
    'drama': '短剧推荐',
    'news': '资讯'
  }
  return labels[type] || type
}

// 获取场景标签类型
function getScenarioTagType(scenario) {
  const types = {
    'game': 'success',
    'drama': 'warning',
    'general': 'info'
  }
  return types[scenario] || 'info'
}

// 获取字数
function getWordCount(text) {
  if (!text) return 0
  // 移除markdown标记后计算
  const plainText = text.replace(/[#*`\[\]()]/g, '').replace(/\n/g, '')
  return plainText.length
}

// 加载平台列表
async function loadPlatforms() {
  loading.value = true
  try {
    const response = await getAvailablePlatforms()
    platformList.value = response.data || []
    
    // 自动选择默认平台
    const defaultPlatform = platformList.value.find(p => p.isDefault === '1' && p.status === '1')
    if (defaultPlatform) {
      formData.platformId = defaultPlatform.id
      await handlePlatformChange(defaultPlatform.id)
    }
  } catch (error) {
    console.error('加载AI平台列表失败:', error)
    ElMessage.error('加载AI平台列表失败')
  } finally {
    loading.value = false
  }
}

// 加载模板列表
async function loadTemplates() {
  try {
    const query = {}
    // 如果有选择模板类型，则按类型过滤
    if (filterTemplateType.value) {
      query.templateType = filterTemplateType.value
    }
    const response = await getAvailableTemplates(query)
    templateList.value = response.data || []
  } catch (error) {
    console.error('加载内容工具失败:', error)
    ElMessage.error('加载内容工具失败')
  }
}

// 加载所有模板（用于提取类型）
async function loadAllTemplates() {
  try {
    const response = await getAvailableTemplates({})
    allTemplateList.value = response.data || []
  } catch (error) {
    console.error('加载所有模板失败:', error)
  }
}

// 模板类型变更
function handleTemplateTypeChange() {
  loadTemplates()
  // 清空已选择的模板
  formData.templateId = null
  selectedTemplate.value = null
  templateVariables.value = []
}

// 平台变更
function handlePlatformChange(platformId) {
  const platform = platformList.value.find(p => p.id === platformId)
  if (platform) {
    // 更新默认参数
    if (platform.temperature) {
      formData.temperature = parseFloat(platform.temperature)
    }
    if (platform.maxTokens) {
      formData.maxTokens = platform.maxTokens
    }
  }
}

// 模板变更
async function handleTemplateChange(templateId) {
  selectedTemplate.value = templateList.value.find(t => t.id === templateId)
  
  if (!selectedTemplate.value) {
    return
  }

  // 加载模板变量定义
  try {
    const response = await getTemplateVariables(templateId)
    templateVariables.value = response.data || []
    
    // 初始化变量默认值
    formData.variables = {}
    templateVariables.value.forEach(variable => {
      if (variable.defaultValue !== undefined && variable.defaultValue !== null) {
        formData.variables[variable.name] = variable.defaultValue
      }
      
      // 自动填充特殊变量
      // 如果是优化模式，自动填充原始文本相关变量
      if (props.mode === 'optimize' && props.currentContent) {
        if (variable.name === 'originalText' || variable.name === 'currentContent' || variable.name === 'content') {
          formData.variables[variable.name] = props.currentContent
        }
      }
      
      // 自动填充标题
      if (props.currentTitle && (variable.name === 'title' || variable.name === 'articleTitle')) {
        formData.variables[variable.name] = props.currentTitle
      }
    })
  } catch (error) {
    console.error('加载模板变量失败:', error)
    templateVariables.value = []
  }
}

// 生成内容
async function handleGenerate() {
  generating.value = true
  errorMessage.value = ''
  generatedResults.value = []
  
  try {
    const requestData = {
      platformId: formData.platformId,
      templateId: formData.templateId,
      mode: props.mode,
      variables: {
        ...formData.variables,
        title: props.currentTitle,
        ...(props.mode === 'optimize' && { currentContent: props.currentContent })
      },
      parameters: {
        temperature: formData.temperature,
        maxTokens: formData.maxTokens,
        n: formData.n
      }
    }

    // 统一调用processContent接口
    const response = await processContent(requestData)
    
    if (response.code === 200 && response.data) {
      generatedResults.value = response.data.results || [response.data]
      selectedResultIndex.value = '0'
      ElMessage.success('内容生成成功！')
    } else {
      throw new Error(response.msg || '生成失败')
    }
  } catch (error) {
    console.error('生成内容失败:', error)
    errorMessage.value = error.message || error.msg || '生成内容失败，请检查配置后重试'
    ElMessage.error(errorMessage.value)
  } finally {
    generating.value = false
  }
}

// 插入内容到编辑器
function handleInsertContent() {
  if (generatedResults.value.length === 0) {
    ElMessage.warning('没有可插入的内容')
    return
  }

  const selectedResult = generatedResults.value[parseInt(selectedResultIndex.value)]
  if (!selectedResult) {
    ElMessage.warning('请选择一个版本')
    return
  }

  // 通过emit将内容传递给父组件
  emit('insert-content', {
    content: selectedResult.content,
    model: selectedResult.model,
    templateId: formData.templateId
  })
  
  ElMessage.success('内容已插入到编辑器')
  handleClose()
}

// 关闭对话框
function handleClose() {
  dialogVisible.value = false
  
  // 延迟重置状态
  setTimeout(() => {
    formData.platformId = null
    formData.templateId = null
    formData.variables = {}
    formData.temperature = 0.7
    formData.maxTokens = 2000
    formData.n = 1
    selectedTemplate.value = null
    templateVariables.value = []
    generatedResults.value = []
    selectedResultIndex.value = '0'
    errorMessage.value = ''
    
    if (formRef.value) {
      formRef.value.clearValidate()
    }
  }, 300)
}

// 监听对话框打开
watch(dialogVisible, async (val) => {
  if (val) {
    loadPlatforms()
    // 先加载所有模板以获取类型列表
    await loadAllTemplates()
    // 再加载显示的模板列表
    loadTemplates()
  }
})
</script>

<style scoped lang="scss">
.ai-tool-container {
  position: relative;
  min-height: 400px;
  padding-bottom: 20px;
}

.generating-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.95);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
}

.generating-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #409EFF;
}

.result-preview {
  border: 1px solid #DCDFE6;
  border-radius: 4px;
  padding: 15px;
  background: #fff;

  .result-meta {
    display: flex;
    gap: 20px;
    font-size: 13px;
    color: #909399;
    margin-bottom: 10px;
  }

  .result-content {
    max-height: 400px;
    overflow-y: auto;
    padding: 10px;
    background: #F5F7FA;
    border-radius: 4px;
  }
}

:deep(.el-slider__marks-text) {
  font-size: 12px;
}

:deep(.el-drawer__body) {
  padding: 20px;
  overflow-y: auto;
}

:deep(.el-drawer__footer) {
  padding: 15px 20px;
  border-top: 1px solid #e4e7ed;
}
</style>
