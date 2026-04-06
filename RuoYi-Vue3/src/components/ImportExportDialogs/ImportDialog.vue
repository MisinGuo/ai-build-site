<template>
  <el-dialog title="导入数据" v-model="dialogVisible" width="550px" append-to-body @close="handleClose">
    <div style="margin: 20px 0;">
      <el-alert 
        title="导入说明" 
        type="info" 
        :closable="false"
        style="margin-bottom: 20px;"
      >
        <template #default>
          <div style="font-size: 13px; line-height: 1.5;">
            <slot name="importTips">
              <p>• 支持导入 Excel (.xlsx) 或 JSON (.json) 格式文件</p>
              <p>• 将根据导入数据重建数据结构和关联关系</p>
              <p>• 请确保文件格式与导出的数据格式一致</p>
            </slot>
          </div>
        </template>
      </el-alert>
      
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
          将文件拖到此处，或<em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            支持 .xlsx 和 .json 格式文件，文件大小不超过10MB
          </div>
        </template>
      </el-upload>
      
      <div v-if="previewData.length > 0" style="margin-top: 20px;">
        <el-divider content-position="left">数据预览（前5条）</el-divider>
        <el-table :data="previewData.slice(0, 5)" border size="small" max-height="300">
          <slot name="previewColumns" :data="previewData">
            <el-table-column prop="name" label="名称" width="150" show-overflow-tooltip />
          </slot>
        </el-table>
        <p style="margin-top: 10px; color: #909399; font-size: 13px;">
          共 {{ previewData.length }} 条数据待导入
          <span v-if="translationsData.length > 0">，{{ translationsData.length }} 条翻译数据</span>
        </p>
      </div>
    </div>
    
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取 消</el-button>
        <el-button type="primary" @click="handleConfirm" :loading="loading" :disabled="previewData.length === 0">
          <el-icon style="margin-right: 5px;"><Upload /></el-icon>
          确认导入 ({{ previewData.length }})
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed } from 'vue'
import { UploadFilled, Upload } from '@element-plus/icons-vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  loading: {
    type: Boolean,
    default: false
  },
  previewData: {
    type: Array,
    default: () => []
  },
  translationsData: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['update:modelValue', 'confirm', 'fileChange', 'fileRemove', 'update:previewData', 'update:translationsData'])

const uploadRef = ref(null)

const dialogVisible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const handleClose = () => {
  emit('update:modelValue', false)
  emit('update:previewData', [])
  emit('update:translationsData', [])
}

const handleFileChange = (file) => {
  emit('fileChange', file)
}

const handleFileRemove = () => {
  emit('fileRemove')
  emit('update:previewData', [])
  emit('update:translationsData', [])
  return true
}

const handleConfirm = () => {
  emit('confirm')
}
</script>
