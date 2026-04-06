<template>
  <div class="workflow-inputs-editor">
    <div class="inputs-list">
      <div
        v-for="(input, index) in localInputs"
        :key="index"
        class="input-item"
      >
        <el-form :model="input" label-width="80px" size="small">
          <el-row :gutter="12">
            <el-col :span="12">
              <el-form-item label="参数名">
                <el-input v-model="input.name" placeholder="参数名（英文）" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="显示名称">
                <el-input v-model="input.label" placeholder="显示名称" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="12">
            <el-col :span="12">
              <el-form-item label="类型">
                <el-select v-model="input.type" placeholder="选择类型">
                  <el-option label="文本" value="string" />
                  <el-option label="数字" value="number" />
                  <el-option label="布尔值" value="boolean" />
                  <el-option label="数组" value="array" />
                  <el-option label="对象" value="object" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="默认值">
                <el-input v-model="input.default" placeholder="默认值" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="描述">
            <el-input v-model="input.description" placeholder="参数描述" />
          </el-form-item>

          <el-form-item label="选项">
            <el-checkbox v-model="input.required" label="必填" />
          </el-form-item>
        </el-form>

        <el-button
          type="danger"
          text
          size="small"
          class="remove-btn"
          @click="removeInput(index)"
        >
          <el-icon><Delete /></el-icon> 删除
        </el-button>
      </div>
    </div>

    <el-button type="primary" plain size="small" @click="addInput">
      <el-icon><Plus /></el-icon> 添加参数
    </el-button>

    <div class="dialog-footer">
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="handleConfirm">确定</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  modelValue: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['update:modelValue', 'confirm', 'cancel'])

const localInputs = ref([])

watch(() => props.modelValue, (val) => {
  localInputs.value = JSON.parse(JSON.stringify(val || []))
}, { immediate: true })

function addInput() {
  localInputs.value.push({
    name: '',
    label: '',
    type: 'string',
    required: false,
    default: '',
    description: ''
  })
}

function removeInput(index) {
  localInputs.value.splice(index, 1)
}

function handleConfirm() {
  emit('update:modelValue', localInputs.value)
  emit('confirm')
}

function handleCancel() {
  emit('cancel')
}
</script>

<style scoped lang="scss">
.workflow-inputs-editor {
  .inputs-list {
    max-height: 400px;
    overflow-y: auto;
    margin-bottom: 16px;

    .input-item {
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
