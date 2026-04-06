<template>
  <div class="ai-generator-executor" v-loading="loading">
    <el-form :model="formData" ref="formRef" label-width="100px">
      <!-- 显示工具描述 -->
      <el-alert 
        v-if="tool.toolDescription"
        :title="tool.toolDescription" 
        type="info" 
        :closable="false"
        show-icon
        style="margin-bottom: 20px;"
      />

      <!-- AI平台选择 -->
      <el-form-item label="AI平台" required>
        <el-select 
          v-model="formData.aiPlatformId" 
          placeholder="请选择AI平台" 
          style="width: 100%"
          @change="handlePlatformChange"
          :loading="loading"
        >
          <el-option
            v-for="platform in availablePlatforms"
            :key="platform.id"
            :label="platform.name"
            :value="platform.id"
          >
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <span>{{ platform.name }}</span>
              <el-tag size="small" type="info">{{ platform.platformType }}</el-tag>
            </div>
          </el-option>
        </el-select>
      </el-form-item>

      <!-- 模型选择 -->
      <el-form-item label="模型" required v-if="availableModels.length > 0">
        <el-select 
          v-model="formData.model" 
          placeholder="请选择模型" 
          style="width: 100%"
        >
          <el-option
            v-for="model in availableModels"
            :key="model"
            :label="model"
            :value="model"
          />
        </el-select>
      </el-form-item>

      <!-- 提示词模板变量（如果有） -->
      <div v-if="templateVariables.length > 0">
        <el-divider content-position="left">
          <span style="font-weight: 600;">模板参数</span>
        </el-divider>

        <el-form-item 
          v-for="variable in templateVariables" 
          :key="variable.name"
          :label="variable.label"
          :required="variable.required"
        >
          <el-input 
            v-if="variable.type === 'text' || !variable.type" 
            v-model="formData.variables[variable.name]" 
            :placeholder="variable.placeholder || '请输入' + variable.label"
          />
          <el-input 
            v-else-if="variable.type === 'textarea'" 
            v-model="formData.variables[variable.name]" 
            type="textarea"
            :rows="variable.rows || 3"
            :placeholder="variable.placeholder || '请输入' + variable.label"
          />
          <el-input-number 
            v-else-if="variable.type === 'number'" 
            v-model="formData.variables[variable.name]" 
            :min="variable.min || 0"
            :max="variable.max || 10000"
            style="width: 100%"
          />
          <el-select 
            v-else-if="variable.type === 'select'" 
            v-model="formData.variables[variable.name]" 
            :placeholder="'请选择' + variable.label"
            style="width: 100%"
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
        <span style="font-weight: 600;">AI参数</span>
      </el-divider>

      <el-form-item label="Temperature">
        <el-slider 
          v-model="formData.temperature" 
          :min="0" 
          :max="2" 
          :step="0.1"
          show-tooltip
        />
        <div style="color: #909399; font-size: 12px;">
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
    </el-form>

    <!-- 生成结果 -->
    <div v-if="generatedContent" style="margin-top: 20px;">
      <el-divider content-position="left">
        <span style="font-weight: 600;">生成结果</span>
      </el-divider>

      <el-alert 
        title="内容生成成功！" 
        type="success" 
        :closable="false"
        show-icon
        style="margin-bottom: 15px;"
      />

      <div class="result-preview">
        <div class="result-meta">
          <span>字数：{{ getWordCount(generatedContent) }}</span>
        </div>
        <el-divider />
        <div class="result-content">
          <el-input
            v-model="generatedContent"
            type="textarea"
            :rows="15"
            placeholder="生成的内容"
          />
        </div>
      </div>
    </div>

    <!-- 加载中 -->
    <div v-if="generating" class="generating-overlay">
      <el-icon class="is-loading" :size="48"><Loading /></el-icon>
      <p style="margin-top: 20px;">正在生成内容...</p>
    </div>

    <!-- 错误信息 -->
    <el-alert 
      v-if="errorMessage"
      :title="errorMessage" 
      type="error" 
      show-icon
      closable
      @close="errorMessage = ''"
      style="margin-top: 20px;"
    />

    <!-- 底部按钮 -->
    <div style="margin-top: 30px; display: flex; justify-content: flex-end; gap: 10px;">
      <el-button @click="$emit('cancel')">取消</el-button>
      <el-button 
        type="primary" 
        @click="handleGenerate"
        :loading="generating"
        :disabled="!canGenerate"
      >
        {{ generating ? '生成中...' : '开始生成' }}
      </el-button>
      <el-button 
        v-if="generatedContent" 
        type="success" 
        @click="handleInsert"
      >
        插入到编辑器
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { listVisiblePlatform } from '@/api/gamebox/platform'
import { processContent } from '@/api/gamebox/aiTool'

const props = defineProps({
  tool: {
    type: Object,
    required: true
  },
  siteId: {
    type: Number,
    required: true
  },
  selectedText: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['success', 'cancel'])

const formData = reactive({
  aiPlatformId: null,
  model: '',
  temperature: 0.7,
  maxTokens: 2000,
  variables: {}
})

const availablePlatforms = ref([])
const availableModels = ref([])
const templateVariables = ref([])
const generatedContent = ref('')
const loading = ref(false)
const generating = ref(false)
const errorMessage = ref('')

// 是否可以生成
const canGenerate = computed(() => {
  return formData.aiPlatformId && formData.model
})

// 获取字数
function getWordCount(text) {
  return text ? text.length : 0
}

// 加载AI平台列表
async function loadPlatforms() {
  loading.value = true
  try {
    console.log('加载AI平台，siteId:', props.siteId)
    const response = await listVisiblePlatform({
      siteId: props.siteId
    })
    console.log('AI平台API响应:', response)
    
    // 支持多种响应格式
    availablePlatforms.value = response.rows || response.data || []
    console.log('可用AI平台列表:', availablePlatforms.value)
    
    if (availablePlatforms.value.length === 0) {
      ElMessage.warning('当前网站没有可用的AI平台，请先配置AI平台')
      return
    }
    
    // 如果工具配置了默认平台，自动选中
    if (props.tool.aiPlatformId) {
      console.log('工具配置的默认平台ID:', props.tool.aiPlatformId)
      // 检查平台是否在可用列表中
      const platformExists = availablePlatforms.value.some(p => p.id === props.tool.aiPlatformId)
      if (platformExists) {
        formData.aiPlatformId = props.tool.aiPlatformId
        await handlePlatformChange()
      } else {
        console.warn('工具配置的平台ID不在可用列表中')
      }
    }
    
    // 如果没有设置默认平台，且有可用平台，自动选择第一个
    if (!formData.aiPlatformId && availablePlatforms.value.length > 0) {
      console.log('自动选择第一个可用平台')
      formData.aiPlatformId = availablePlatforms.value[0].id
      await handlePlatformChange()
    }
  } catch (error) {
    console.error('加载AI平台失败:', error)
    ElMessage.error('加载AI平台失败')
  } finally {
    loading.value = false
  }
}

// 平台切换
async function handlePlatformChange() {
  const platform = availablePlatforms.value.find(p => p.id === formData.aiPlatformId)
  if (platform && platform.availableModels) {
    try {
      const models = typeof platform.availableModels === 'string' 
        ? JSON.parse(platform.availableModels)
        : platform.availableModels
      availableModels.value = Array.isArray(models) ? models : []
      
      // 如果工具配置了默认模型，自动选中
      if (props.tool.modelName && models.includes(props.tool.modelName)) {
        formData.model = props.tool.modelName
      } else if (models.length > 0) {
        formData.model = models[0]
      }
    } catch (e) {
      console.error('解析模型列表失败:', e)
      availableModels.value = []
    }
  }
}

// 解析模板变量
function parseTemplateVariables() {
  // 使用工具配置中的 variables JSON 字段
  if (!props.tool.variables) {
    templateVariables.value = []
    return
  }

  try {
    // variables 是 JSON 字符串或对象数组
    const variablesData = typeof props.tool.variables === 'string' 
      ? JSON.parse(props.tool.variables) 
      : props.tool.variables

    if (Array.isArray(variablesData)) {
      templateVariables.value = variablesData
      
      // 初始化变量值
      variablesData.forEach(variable => {
        if (variable.defaultValue !== undefined && variable.defaultValue !== null) {
          formData.variables[variable.name] = variable.defaultValue
        } else {
          formData.variables[variable.name] = ''
        }
        
        // 自动填充选中的文本到原始文本相关变量
        if (props.selectedText && (
          variable.name === 'originalText' || 
          variable.name === 'currentContent' || 
          variable.name === 'content' ||
          variable.name === 'text'
        )) {
          formData.variables[variable.name] = props.selectedText
        }
      })
    } else {
      templateVariables.value = []
    }
  } catch (error) {
    console.error('解析变量配置失败:', error)
    templateVariables.value = []
  }
}

// 生成内容
async function handleGenerate() {
  // 验证必填变量
  for (const variable of templateVariables.value) {
    if (variable.required && !formData.variables[variable.name]) {
      ElMessage.warning(`请输入${variable.label}`)
      return
    }
  }

  generating.value = true
  errorMessage.value = ''
  generatedContent.value = ''

  try {
    console.log('发送的变量:', formData.variables)
    
    // 调用AI工具API（使用process接口）
    const response = await processContent({
      platformId: formData.aiPlatformId,
      templateId: props.tool.id,
      mode: 'generate',
      variables: formData.variables,
      parameters: {
        temperature: formData.temperature,
        maxTokens: formData.maxTokens,
        n: 1
      }
    })

    console.log('AI生成响应:', response)
    console.log('response.data:', response.data)

    if (response.code === 200 && response.data) {
      // 后端返回格式：{ results: [{content: "...", model: "..."}], template: "...", platform: "..." }
      if (response.data.results && Array.isArray(response.data.results) && response.data.results.length > 0) {
        console.log('从results中获取内容:', response.data.results[0])
        generatedContent.value = response.data.results[0].content
        console.log('generatedContent设置为:', generatedContent.value)
      } else if (response.data.content) {
        console.log('从content中获取内容:', response.data.content)
        generatedContent.value = response.data.content
        console.log('generatedContent设置为:', generatedContent.value)
      } else if (typeof response.data === 'string') {
        console.log('data本身是字符串:', response.data)
        generatedContent.value = response.data
        console.log('generatedContent设置为:', generatedContent.value)
      } else {
        console.error('无法解析的数据格式:', response.data)
        throw new Error('返回数据格式错误')
      }
      
      console.log('最终generatedContent.value:', generatedContent.value)
      console.log('generatedContent.value类型:', typeof generatedContent.value)
      console.log('generatedContent.value长度:', generatedContent.value?.length)
      
      ElMessage.success('内容生成成功')
    } else {
      throw new Error(response.msg || '生成失败')
    }
  } catch (error) {
    console.error('生成内容失败:', error)
    errorMessage.value = error.msg || error.message || '生成失败'
    ElMessage.error(errorMessage.value)
  } finally {
    generating.value = false
  }
}

// 插入内容
function handleInsert() {
  emit('success', {
    action: 'insert',
    content: generatedContent.value
  })
}

onMounted(() => {
  loadPlatforms()
  parseTemplateVariables()
})
</script>

<style scoped lang="scss">
.ai-generator-executor {
  position: relative;

  .result-preview {
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    padding: 15px;
    background: #f5f7fa;

    .result-meta {
      display: flex;
      gap: 20px;
      font-size: 13px;
      color: #606266;
    }

    .result-content {
      margin-top: 10px;
    }
  }

  .generating-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(255, 255, 255, 0.9);
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    z-index: 100;
  }
}
</style>
