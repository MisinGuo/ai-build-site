<template>
  <div class="text-optimizer-executor" v-loading="loading">
    <el-form :model="formData" ref="formRef" label-width="100px">
      <!-- 显示选中的文本 -->
      <el-alert 
        title="选中的文本" 
        type="info" 
        :closable="false"
        style="margin-bottom: 20px;"
      >
        <div class="selected-text-preview">
          {{ selectedText }}
        </div>
      </el-alert>

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

      <!-- 优化指令 -->
      <el-form-item label="优化指令">
        <el-input
          v-model="formData.instruction"
          type="textarea"
          :rows="3"
          placeholder="可选：额外的优化要求，例如'使其更正式'、'简化表达'等"
        />
      </el-form-item>

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
      </el-form-item>
    </el-form>

    <!-- 优化结果 -->
    <div v-if="optimizedContent" style="margin-top: 20px;">
      <el-divider content-position="left">
        <span style="font-weight: 600;">优化结果</span>
      </el-divider>

      <el-alert 
        title="文本优化成功！" 
        type="success" 
        :closable="false"
        show-icon
        style="margin-bottom: 15px;"
      />

      <div class="result-preview">
        <el-input
          v-model="optimizedContent"
          type="textarea"
          :rows="10"
          placeholder="优化后的内容"
        />
      </div>
    </div>

    <!-- 加载中 -->
    <div v-if="optimizing" class="optimizing-overlay">
      <el-icon class="is-loading" :size="48"><Loading /></el-icon>
      <p style="margin-top: 20px;">正在优化文本...</p>
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
        @click="handleOptimize"
        :loading="optimizing"
        :disabled="!canOptimize"
      >
        {{ optimizing ? '优化中...' : '开始优化' }}
      </el-button>
      <el-button 
        v-if="optimizedContent" 
        type="success" 
        @click="handleReplace"
      >
        替换选中文本
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { listVisiblePlatform } from '@/api/gamebox/platform'
import { executeAiTool } from '@/api/gamebox/aiTool'

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
    required: true
  }
})

const emit = defineEmits(['success', 'cancel'])

const formData = reactive({
  aiPlatformId: null,
  model: '',
  instruction: '',
  temperature: 0.7
})

const availablePlatforms = ref([])
const availableModels = ref([])
const optimizedContent = ref('')
const loading = ref(false)
const optimizing = ref(false)
const errorMessage = ref('')

// 是否可以优化
const canOptimize = computed(() => {
  return formData.aiPlatformId && formData.model && props.selectedText
})

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
    
    // 从工具配置中获取默认平台
    if (props.tool.config) {
      try {
        const config = typeof props.tool.config === 'string' 
          ? JSON.parse(props.tool.config)
          : props.tool.config
        
        if (config.aiPlatformId) {
          console.log('工具配置的默认平台ID:', config.aiPlatformId)
          // 检查平台是否在可用列表中
          const platformExists = availablePlatforms.value.some(p => p.id === config.aiPlatformId)
          if (platformExists) {
            formData.aiPlatformId = config.aiPlatformId
            await handlePlatformChange()
          } else {
            console.warn('工具配置的平台ID不在可用列表中')
          }
        }
      } catch (e) {
        console.error('解析工具配置失败:', e)
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
      
      // 从工具配置中获取默认模型
      let defaultModel = null
      if (props.tool.config) {
        try {
          const config = typeof props.tool.config === 'string' 
            ? JSON.parse(props.tool.config)
            : props.tool.config
          if (config.modelName) {
            defaultModel = config.modelName
          }
        } catch (e) {
          // 忽略解析错误
        }
      }
      
      // 设置模型：优先使用配置的默认模型，否则使用第一个
      if (defaultModel && models.includes(defaultModel)) {
        formData.model = defaultModel
      } else if (models.length > 0) {
        formData.model = models[0]
      }
    } catch (e) {
      console.error('解析模型列表失败:', e)
      availableModels.value = []
    }
  }
}

// 优化文本
async function handleOptimize() {
  optimizing.value = true
  errorMessage.value = ''
  optimizedContent.value = ''

  try {
    // 构建提示词
    const systemPrompt = '你是一个专业的文本优化助手，擅长改善文本的表达、语法和可读性。'
    let userPrompt = `请优化以下文本：\n\n${props.selectedText}`
    
    if (formData.instruction) {
      userPrompt += `\n\n优化要求：${formData.instruction}`
    }

    // 调用AI工具API
    const response = await executeAiTool({
      aiPlatformId: formData.aiPlatformId,
      model: formData.model,
      systemPrompt,
      userPrompt,
      temperature: formData.temperature,
      maxTokens: 4000
    })

    if (response.code === 200 && response.data) {
      optimizedContent.value = response.data.content || response.data
      ElMessage.success('文本优化成功')
    } else {
      throw new Error(response.msg || '优化失败')
    }
  } catch (error) {
    console.error('优化文本失败:', error)
    errorMessage.value = error.msg || error.message || '优化失败'
    ElMessage.error(errorMessage.value)
  } finally {
    optimizing.value = false
  }
}

// 替换选中文本
function handleReplace() {
  emit('success', {
    action: 'replace',
    content: optimizedContent.value
  })
}

onMounted(() => {
  loadPlatforms()
})
</script>

<style scoped lang="scss">
.text-optimizer-executor {
  position: relative;

  .selected-text-preview {
    max-height: 200px;
    overflow-y: auto;
    padding: 10px;
    background: white;
    border-radius: 4px;
    font-size: 14px;
    line-height: 1.6;
    white-space: pre-wrap;
    word-break: break-word;
  }

  .result-preview {
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    padding: 15px;
    background: #f5f7fa;
  }

  .optimizing-overlay {
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
