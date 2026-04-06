<template>
  <div class="step-config-panel">
    <el-form :model="localStep" label-width="80px" size="small">
      <el-form-item label="步骤名称">
        <el-input v-model="localStep.stepName" placeholder="请输入步骤名称" />
      </el-form-item>

      <el-form-item label="步骤描述">
        <el-input
          v-model="localStep.description"
          type="textarea"
          :rows="2"
          placeholder="请输入步骤描述"
        />
      </el-form-item>

      <el-form-item :label="isWorkflowStep ? '子工作流' : '使用工具'">
        <el-input :model-value="stepTargetCode" disabled>
          <template #prepend>
            <span v-if="toolInfo">{{ toolInfo.icon }}</span>
          </template>
        </el-input>
        <div v-if="toolInfo" class="tool-meta">
          <el-tag size="small">{{ toolInfo.toolType }}</el-tag>
          <span class="tool-description">{{ toolInfo.description }}</span>
        </div>
      </el-form-item>

      <el-divider>输入参数映射</el-divider>

      <div v-if="toolInfo && toolInfo.inputs && toolInfo.inputs.length > 0">
        <div
          v-for="input in toolInfo.inputs"
          :key="input.name"
          class="param-mapping-item"
        >
          <div class="param-header">
            <span class="param-name">
              {{ input.label || input.name }}
              <el-tag v-if="input.required" type="danger" size="small">必填</el-tag>
            </span>
            <el-tooltip v-if="input.description" :content="input.description" placement="top">
              <el-icon><QuestionFilled /></el-icon>
            </el-tooltip>
          </div>

          <div class="param-mapping-config">
            <el-select
              v-model="localStep.inputMapping[input.name].source"
              placeholder="选择数据源类型"
              size="small"
              style="width: 130px; margin-right: 8px;"
              @change="handleSourceChange(input.name)"
            >
              <el-option label="常量值" value="constant" />
              <el-option v-if="hasWorkflowInputs" label="工作流输入" value="input" />
              <el-option
                v-for="stepType in availableStepTypes"
                :key="stepType.type"
                :label="stepType.label"
                :value="stepType.type"
              />
            </el-select>

            <template v-if="localStep.inputMapping[input.name].source === 'constant'">
              <el-switch
                v-if="input.type === 'boolean'"
                v-model="localStep.inputMapping[input.name].value"
                size="small"
                style="flex: 1;"
              />
              <el-input
                v-else
                v-model="localStep.inputMapping[input.name].value"
                :placeholder="'输入' + (input.type === 'number' ? '数字' : '文本')"
                :type="input.type === 'number' ? 'number' : 'text'"
                size="small"
                style="flex: 1;"
                clearable
              />
            </template>

            <el-select
              v-else
              v-model="localStep.inputMapping[input.name].value"
              placeholder="选择具体变量"
              size="small"
              style="flex: 1;"
              filterable
              clearable
            >
              <el-option
                v-for="source in getFilteredSources(localStep.inputMapping[input.name].source, input.type)"
                :key="source.value"
                :label="source.label"
                :value="source.value"
              >
                <div style="display: flex; justify-content: space-between; align-items: center;">
                  <span>{{ source.label }}</span>
                  <el-tag v-if="source.dataType" size="small" type="info">{{ source.dataType }}</el-tag>
                </div>
              </el-option>
            </el-select>
          </div>
        </div>
      </div>

      <el-empty v-else description="该步骤没有输入参数" :image-size="60" />

      <el-divider>高级选项</el-divider>

      <el-form-item label="启用状态">
        <el-switch v-model="localStep.enabled" />
      </el-form-item>

      <el-form-item label="出错时">
        <el-radio-group v-model="localStep.continueOnError">
          <el-radio :label="false">停止执行</el-radio>
          <el-radio :label="true" :disabled="isWorkflowStep">继续执行</el-radio>
        </el-radio-group>
        <div v-if="isWorkflowStep" style="font-size: 12px; color: #909399; margin-top: 4px;">
          子工作流步骤必须按顺序执行，失败后不会继续后续步骤。
        </div>
      </el-form-item>

      <el-form-item label="超时时间">
        <el-input-number
          v-model="localStep.timeout"
          :min="0"
          :max="3600"
          :step="30"
          placeholder="秒"
        />
        <span style="margin-left: 8px; font-size: 12px; color: #909399;">秒（0表示不限制）</span>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" size="small" @click="handleSave">
          <el-icon><Check /></el-icon> 保存配置
        </el-button>
        <el-button size="small" @click="handleReset">
          <el-icon><Refresh /></el-icon> 重置
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  step: {
    type: Object,
    required: true
  },
  toolInfo: {
    type: Object,
    default: null
  },
  availableSources: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['update'])

const localStep = ref({})

const isWorkflowStep = computed(() => (localStep.value.stepType || 'tool') === 'workflow')
const stepTargetCode = computed(() => {
  return isWorkflowStep.value ? (localStep.value.workflowCode || '') : (localStep.value.toolCode || '')
})

watch(() => props.step, (newStep) => {
  localStep.value = JSON.parse(JSON.stringify(newStep))

  if (!localStep.value.inputMapping) {
    localStep.value.inputMapping = {}
  }

  if (props.toolInfo && props.toolInfo.inputs) {
    props.toolInfo.inputs.forEach(input => {
      if (!localStep.value.inputMapping[input.name]) {
        localStep.value.inputMapping[input.name] = {
          source: 'constant',
          value: input.default || ''
        }
      }
    })
  }

  if (isWorkflowStep.value) {
    localStep.value.continueOnError = false
  }
}, { immediate: true, deep: true })

const hasWorkflowInputs = computed(() => {
  return props.availableSources.some(s => s.type === 'input')
})

const availableStepTypes = computed(() => {
  const types = new Set()
  const result = []

  props.availableSources.forEach(source => {
    if (source.type !== 'constant' && source.type !== 'input' && !types.has(source.type)) {
      types.add(source.type)
      const match = source.label.match(/^(步骤\d+\([^)]+\))/)
      const stepLabel = match ? match[1] : source.type
      result.push({
        type: source.type,
        label: stepLabel
      })
    }
  })

  return result
})

function getFilteredSources(sourceType, paramType) {
  return props.availableSources.filter(source => {
    if (source.type === 'constant') {
      return true
    }

    if (source.type !== sourceType) return false

    if (paramType && source.dataType) {
      if (paramType === 'text' || paramType === 'string' || paramType === 'textarea') {
        return true
      }
      return source.dataType === paramType
    }

    return true
  })
}

function handleSourceChange(paramName) {
  localStep.value.inputMapping[paramName].value = ''
}

function handleSave() {
  if (props.toolInfo && props.toolInfo.inputs) {
    const errors = []

    props.toolInfo.inputs.forEach(input => {
      if (input.required) {
        const mapping = localStep.value.inputMapping[input.name]
        const value = mapping ? mapping.value : undefined
        const empty = value === undefined || value === null || value === ''
        if (empty) {
          errors.push(`参数 "${input.label || input.name}" 是必填的`)
        }
      }
    })

    if (errors.length > 0) {
      ElMessage.error(errors.join('; '))
      return
    }
  }

  emit('update', localStep.value)
  ElMessage.success('配置已保存')
}

function handleReset() {
  localStep.value = JSON.parse(JSON.stringify(props.step))
  ElMessage.info('已重置为原始配置')
}
</script>

<style scoped lang="scss">
.step-config-panel {
  .tool-meta {
    margin-top: 8px;
    display: flex;
    align-items: center;
    gap: 8px;

    .tool-description {
      font-size: 12px;
      color: #909399;
      flex: 1;
    }
  }

  .param-mapping-item {
    margin-bottom: 16px;
    padding: 12px;
    background: #f9fafc;
    border-radius: 4px;
    border: 1px solid #e4e7ed;

    .param-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 8px;

      .param-name {
        font-weight: 500;
        font-size: 13px;
        display: flex;
        align-items: center;
        gap: 6px;
      }
    }

    .param-mapping-config {
      display: flex;
      align-items: center;
    }
  }
}

:deep(.el-divider__text) {
  font-size: 13px;
  font-weight: 600;
  color: #606266;
}
</style>
