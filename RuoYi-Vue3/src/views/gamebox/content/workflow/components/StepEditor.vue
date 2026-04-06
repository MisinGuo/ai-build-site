<template>
  <div class="step-editor">
    <el-alert 
      title="快速添加步骤" 
      type="info" 
      :closable="false"
      style="margin-bottom: 16px;"
    >
      选择工具或子工作流并填写基本信息，参数映射请在右侧面板中配置
    </el-alert>
    
    <el-form :model="localStep" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="步骤类型" prop="stepType">
        <el-radio-group v-model="localStep.stepType">
          <el-radio label="tool">工具步骤</el-radio>
          <el-radio label="workflow">子工作流步骤</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item v-if="localStep.stepType === 'tool'" label="工具选择" prop="toolCode">
        <el-select 
          v-model="localStep.toolCode" 
          placeholder="请选择要使用的工具"
          filterable
          @change="handleToolChange"
          size="large"
        >
          <el-option-group
            v-for="(tools, category) in groupedTools"
            :key="category"
            :label="category"
          >
            <el-option
              v-for="tool in tools"
              :key="tool.toolCode"
              :label="tool.toolName"
              :value="tool.toolCode"
            >
              <div style="display: flex; align-items: center; justify-content: space-between;">
                <span>{{ tool.icon || '🔧' }} {{ tool.toolName }}</span>
                <el-tag size="small" type="info">{{ tool.toolType }}</el-tag>
              </div>
            </el-option>
          </el-option-group>
        </el-select>
      </el-form-item>

      <el-form-item v-else label="子工作流" prop="workflowCode">
        <el-select
          v-model="localStep.workflowCode"
          placeholder="请选择要执行的工作流"
          filterable
          @change="handleWorkflowChange"
          size="large"
        >
          <el-option
            v-for="wf in availableWorkflows"
            :key="wf.workflowCode"
            :label="`${wf.workflowName} (${wf.workflowCode})`"
            :value="wf.workflowCode"
          >
            <div style="display: flex; align-items: center; justify-content: space-between;">
              <span>🔁 {{ wf.workflowName }}</span>
              <el-tag size="small" type="success">{{ wf.workflowCode }}</el-tag>
            </div>
          </el-option>
        </el-select>
      </el-form-item>

      <!-- 显示所选工具信息 -->
      <el-card v-if="localStep.stepType === 'tool' && selectedTool" shadow="never" style="margin-bottom: 16px; background: #f5f7fa;">
        <div style="display: flex; align-items: start; gap: 12px;">
          <span style="font-size: 32px;">{{ selectedTool.icon || '🔧' }}</span>
          <div style="flex: 1;">
            <div style="font-weight: 600; margin-bottom: 4px;">{{ selectedTool.toolName }}</div>
            <div style="font-size: 13px; color: #606266; margin-bottom: 8px;">{{ selectedTool.description }}</div>
            <div style="display: flex; gap: 12px; font-size: 12px; color: #909399;">
              <span>📥 输入: {{ (selectedTool.inputs || []).length }} 个参数</span>
              <span>📤 输出: {{ (selectedTool.outputs || []).length }} 个参数</span>
            </div>
          </div>
        </div>
      </el-card>

      <el-card v-if="localStep.stepType === 'workflow' && selectedWorkflow" shadow="never" style="margin-bottom: 16px; background: #f5f7fa;">
        <div style="display: flex; align-items: start; gap: 12px;">
          <span style="font-size: 32px;">🔁</span>
          <div style="flex: 1;">
            <div style="font-weight: 600; margin-bottom: 4px;">{{ selectedWorkflow.workflowName }}</div>
            <div style="font-size: 13px; color: #606266; margin-bottom: 8px;">编码：{{ selectedWorkflow.workflowCode }}</div>
            <div style="display: flex; gap: 12px; font-size: 12px; color: #909399;">
              <span>📥 输入: {{ (selectedWorkflow.inputs || []).length }} 个参数</span>
              <span>📤 输出: {{ (selectedWorkflow.outputs || []).length }} 个参数</span>
            </div>
          </div>
        </div>
      </el-card>

      <el-form-item label="步骤名称" prop="stepName">
        <el-input 
          v-model="localStep.stepName" 
          placeholder="为这个步骤起个名字"
          clearable
        />
      </el-form-item>

      <el-form-item label="步骤描述">
        <el-input 
          v-model="localStep.description" 
          type="textarea" 
          :rows="3"
          placeholder="简单描述一下这个步骤的作用（可选）"
          clearable
        />
      </el-form-item>

      <el-form-item label="选项">
        <el-checkbox v-model="localStep.enabled">启用此步骤</el-checkbox>
        <el-checkbox v-model="localStep.continueOnError" :disabled="localStep.stepType === 'workflow'" style="margin-left: 16px;">出错时继续执行</el-checkbox>
      </el-form-item>
    </el-form>

    <div class="dialog-footer">
      <el-button @click="$emit('cancel')">取消</el-button>
      <el-button type="primary" @click="handleSave">保存</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'

const props = defineProps({
  step: {
    type: Object,
    required: true
  },
  availableTools: {
    type: Array,
    default: () => []
  },
  availableWorkflows: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['save', 'cancel'])

const formRef = ref(null)
const localStep = ref({})
const selectedTool = ref(null)
const selectedWorkflow = ref(null)

const rules = {
  stepType: [{ required: true, message: '请选择步骤类型', trigger: 'change' }],
  stepName: [{ required: true, message: '请输入步骤名称', trigger: 'blur' }]
}

// 按分类分组工具
const groupedTools = computed(() => {
  const groups = {}
  
  props.availableTools.forEach(tool => {
    const category = tool.toolType || '其他'
    if (!groups[category]) {
      groups[category] = []
    }
    groups[category].push(tool)
  })
  
  return groups
})

// 初始化
watch(() => props.step, (newStep) => {
  localStep.value = JSON.parse(JSON.stringify(newStep))
  
  // 确保必要的字段存在
  if (!localStep.value.stepId) {
    localStep.value.stepId = `step_${Date.now()}`
  }
  if (!localStep.value.inputMapping) {
    localStep.value.inputMapping = {}
  }
  if (!localStep.value.stepType) {
    localStep.value.stepType = localStep.value.workflowCode ? 'workflow' : 'tool'
  }
  if (localStep.value.enabled === undefined) {
    localStep.value.enabled = true
  }
  if (localStep.value.continueOnError === undefined) {
    localStep.value.continueOnError = false
  }

  // 如果已选择工具，加载工具信息
  if (localStep.value.stepType === 'tool' && localStep.value.toolCode) {
    handleToolChange(localStep.value.toolCode)
  }
  if (localStep.value.stepType === 'workflow' && localStep.value.workflowCode) {
    handleWorkflowChange(localStep.value.workflowCode)
  }
}, { immediate: true })

watch(() => localStep.value.stepType, (newType) => {
  if (newType === 'tool') {
    localStep.value.workflowCode = ''
    selectedWorkflow.value = null
  } else if (newType === 'workflow') {
    localStep.value.toolCode = ''
    selectedTool.value = null
    localStep.value.continueOnError = false
  }
})

// 工具变化处理
function handleToolChange(toolCode) {
  selectedTool.value = props.availableTools.find(t => t.toolCode === toolCode)
  
  if (selectedTool.value) {
    // 自动填充步骤名称（如果为空）
    if (!localStep.value.stepName) {
      localStep.value.stepName = selectedTool.value.toolName
    }
    
    // 初始化输入映射（使用默认值）
    if (selectedTool.value.inputs) {
      selectedTool.value.inputs.forEach(input => {
        if (!localStep.value.inputMapping[input.name]) {
          localStep.value.inputMapping[input.name] = {
            source: 'constant',
            value: input.default || ''
          }
        }
      })
    }
  }
}

function handleWorkflowChange(workflowCode) {
  selectedWorkflow.value = props.availableWorkflows.find(wf => wf.workflowCode === workflowCode)
  if (selectedWorkflow.value && !localStep.value.stepName) {
    localStep.value.stepName = `执行${selectedWorkflow.value.workflowName}`
  }
}

// 保存
async function handleSave() {
  try {
    await formRef.value.validate()

    if (localStep.value.stepType === 'tool' && !localStep.value.toolCode) {
      throw new Error('请选择工具')
    }
    if (localStep.value.stepType === 'workflow' && !localStep.value.workflowCode) {
      throw new Error('请选择子工作流')
    }

    emit('save', localStep.value)
  } catch (error) {
    console.error('表单验证失败', error)
  }
}
</script>

<style scoped lang="scss">
.step-editor {
  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 8px;
    margin-top: 24px;
    padding-top: 16px;
    border-top: 1px solid #e4e7ed;
  }
}

:deep(.el-card__body) {
  padding: 12px;
}
</style>
