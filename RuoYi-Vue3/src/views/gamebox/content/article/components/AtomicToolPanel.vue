<template>
  <el-drawer
    v-model="visible"
    title="原子工具"
    direction="rtl"
    size="500px"
    :close-on-click-modal="false"
  >
    <div class="atomic-tool-panel">
      <!-- 选中文本信息 -->
      <el-alert 
        v-if="selectedText"
        type="success" 
        :closable="false"
        show-icon
        style="margin-bottom: 15px;"
      >
        <template #default>
          已选中 {{ selectedText.length }} 个字符
        </template>
      </el-alert>

      <!-- 工具列表 -->
      <div class="tool-list">
        <div 
          v-for="tool in availableTools" 
          :key="tool.id"
          class="tool-item"
          @click="selectTool(tool)"
        >
          <div class="tool-header">
            <span class="tool-icon">
              <el-icon v-if="tool.toolType === 'ai'"><MagicStick /></el-icon>
              <el-icon v-else-if="tool.toolType === 'api'"><Link /></el-icon>
              <el-icon v-else><Tools /></el-icon>
            </span>
            <span class="tool-name">{{ tool.toolName }}</span>
          </div>
          <div class="tool-desc">{{ tool.description }}</div>
        </div>
      </div>

      <!-- 工具执行对话框 -->
      <el-dialog
        v-model="executeDialogVisible"
        :title="currentTool?.toolName"
        width="600px"
        append-to-body
      >
        <el-form :model="executeForm" label-width="100px">
          <el-form-item
            v-for="input in currentTool?.inputs"
            :key="input.name"
            :label="input.label || input.name"
            :required="input.required"
          >
            <el-input
              v-if="input.type === 'textarea'"
              v-model="executeForm.params[input.name]"
              type="textarea"
              :rows="input.rows || 5"
              :placeholder="input.description"
            />
            <el-input
              v-else-if="input.type === 'text' || input.type === 'string'"
              v-model="executeForm.params[input.name]"
              :placeholder="input.description"
            />
            <el-input-number
              v-else-if="input.type === 'number'"
              v-model="executeForm.params[input.name]"
              style="width: 100%"
            />
          </el-form-item>
        </el-form>

        <template #footer>
          <el-button @click="executeDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="executeTool" :loading="executing">
            执行
          </el-button>
        </template>
      </el-dialog>
    </div>
  </el-drawer>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { MagicStick, Link, Tools } from '@element-plus/icons-vue'
import { listAtomicTool, executeAtomicToolById } from '@/api/gamebox/atomicTool'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  selectedText: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:modelValue', 'replace-text'])

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const availableTools = ref([])
const currentTool = ref(null)
const executeDialogVisible = ref(false)
const executing = ref(false)
const executeForm = ref({
  params: {}
})

// 加载可用工具
function loadTools() {
  listAtomicTool({ enabled: 1 }).then(response => {
    availableTools.value = response.rows.map(row => ({
      ...row,
      config: parseJSON(row.config, {}),
      inputs: parseJSON(row.inputs, []),
      outputs: parseJSON(row.outputs, [])
    }))
  })
}

// 解析JSON
function parseJSON(str, defaultValue) {
  try {
    return str ? (typeof str === 'string' ? JSON.parse(str) : str) : defaultValue
  } catch (e) {
    console.error('JSON解析失败:', e)
    return defaultValue
  }
}

// 选择工具
function selectTool(tool) {
  currentTool.value = tool
  
  // 初始化参数
  const params = {}
  if (tool.inputs && Array.isArray(tool.inputs)) {
    tool.inputs.forEach(input => {
      // 如果是 originalText 且有选中文本，自动填充
      if (input.name === 'originalText' && props.selectedText) {
        params[input.name] = props.selectedText
      } else if (input.default !== undefined) {
        params[input.name] = input.default
      } else {
        params[input.name] = input.type === 'number' ? 0 : ''
      }
    })
  }
  
  executeForm.value.params = params
  executeDialogVisible.value = true
}

// 执行工具
function executeTool() {
  executing.value = true
  
  executeAtomicToolById(currentTool.value.id, executeForm.value.params)
    .then(response => {
      if (response.data.status === 'success') {
        ElMessage.success('执行成功')
        
        // 如果有 generatedText 输出，替换选中的文本
        const output = response.data.output
        if (output.generatedText) {
          emit('replace-text', output.generatedText)
          executeDialogVisible.value = false
          visible.value = false
        }
      } else {
        ElMessage.error('执行失败: ' + response.data.message)
      }
    })
    .catch(error => {
      ElMessage.error('执行失败: ' + (error.message || '未知错误'))
    })
    .finally(() => {
      executing.value = false
    })
}

// 监听打开状态
watch(visible, (val) => {
  if (val) {
    loadTools()
  }
})
</script>

<style scoped lang="scss">
.atomic-tool-panel {
  padding: 0 10px;
}

.tool-list {
  .tool-item {
    padding: 15px;
    margin-bottom: 10px;
    border: 1px solid #e4e7ed;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      border-color: #409eff;
      background-color: #f0f9ff;
      transform: translateY(-2px);
      box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
    }

    .tool-header {
      display: flex;
      align-items: center;
      margin-bottom: 8px;

      .tool-icon {
        font-size: 20px;
        margin-right: 8px;
        color: #409eff;
      }

      .tool-name {
        font-size: 16px;
        font-weight: 600;
        color: #303133;
      }
    }

    .tool-desc {
      font-size: 13px;
      color: #909399;
      line-height: 1.6;
    }
  }
}
</style>
