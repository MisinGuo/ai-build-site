<template>
  <div class="workflow-outputs-editor">
    <div class="outputs-list">
      <div
        v-for="(output, index) in localOutputs"
        :key="index"
        class="output-item"
      >
        <el-form :model="output" label-width="80px" size="small">
          <el-form-item label="输出名">
            <el-input v-model="output.name" placeholder="输出参数名" />
          </el-form-item>

          <el-form-item label="数据源">
            <el-select 
              v-model="output.source" 
              placeholder="选择数据源"
              filterable
            >
              <el-option-group
                v-for="(sources, stepName) in groupedSources"
                :key="stepName"
                :label="stepName"
              >
                <el-option
                  v-for="source in sources"
                  :key="source.value"
                  :label="source.outputLabel"
                  :value="source.value"
                />
              </el-option-group>
            </el-select>
          </el-form-item>

          <el-form-item label="描述">
            <el-input v-model="output.description" placeholder="输出描述" />
          </el-form-item>
        </el-form>

        <el-button
          type="danger"
          text
          size="small"
          class="remove-btn"
          @click="removeOutput(index)"
        >
          <el-icon><Delete /></el-icon> 删除
        </el-button>
      </div>
    </div>

    <el-button type="primary" plain size="small" @click="addOutput">
      <el-icon><Plus /></el-icon> 添加输出
    </el-button>

    <div class="dialog-footer">
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="handleConfirm">确定</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'

const props = defineProps({
  modelValue: {
    type: Array,
    default: () => []
  },
  availableSources: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['update:modelValue', 'confirm', 'cancel'])

const localOutputs = ref([])

// 按步骤分组数据源
const groupedSources = computed(() => {
  const groups = {}
  
  props.availableSources.forEach(source => {
    const stepName = source.stepName || '未知步骤'
    if (!groups[stepName]) {
      groups[stepName] = []
    }
    groups[stepName].push(source)
  })
  
  return groups
})

watch(() => props.modelValue, (val) => {
  localOutputs.value = JSON.parse(JSON.stringify(val || []))
}, { immediate: true })

function addOutput() {
  localOutputs.value.push({
    name: '',
    source: '',
    description: ''
  })
}

function removeOutput(index) {
  localOutputs.value.splice(index, 1)
}

function handleConfirm() {
  emit('update:modelValue', localOutputs.value)
  emit('confirm')
}

function handleCancel() {
  emit('cancel')
}
</script>

<style scoped lang="scss">
.workflow-outputs-editor {
  .outputs-list {
    max-height: 400px;
    overflow-y: auto;
    margin-bottom: 16px;

    .output-item {
      position: relative;
      padding: 16px;
      margin-bottom: 16px;
      background: #f9fafc;
      border: 1px solid #e4e7ed;
      border-radius: 4px;

      .remove-btn {
        position: absolute;
        top: 8px;
        right: 8px;
      }
    }
  }

  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 8px;
    margin-top: 16px;
    padding-top: 16px;
    border-top: 1px solid #e4e7ed;
  }
}
</style>
