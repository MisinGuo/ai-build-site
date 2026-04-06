<template>
  <el-dialog title="全站数据导出" v-model="dialogVisible" width="500px" append-to-body @close="handleClose">
    <div style="margin: 20px 0;">
      <div style="margin-bottom: 20px;">
        <el-alert
          title="导出说明"
          type="warning"
          :closable="false"
        >
          <template #default>
            <div style="font-size: 13px; line-height: 1.5; text-align: left;">
              <slot name="exportTips">
                <p>• 全站导出将包含所有数据（默认配置 + 所有站点）</p>
                <p>• 包含所有翻译数据和网站关联关系</p>
                <p>• 适用于系统迁移、备份等场景</p>
                <p style="margin-top: 10px; color: #F56C6C; font-weight: bold;">⚠️ 数据量可能较大，请耐心等待</p>
              </slot>
            </div>
          </template>
        </el-alert>
      </div>
      
      <el-form label-width="100px">
        <el-form-item label="导出格式">
          <el-radio-group v-model="localExportFormat">
            <el-radio value="excel">
              <el-icon style="margin-right: 8px;"><Document /></el-icon>
              Excel (.xlsx)
            </el-radio>
            <el-radio value="json">
              <el-icon style="margin-right: 8px;"><Files /></el-icon>
              JSON (.json)
            </el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
    </div>
    
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取 消</el-button>
        <el-button type="primary" @click="handleConfirm" :loading="loading">
          <el-icon style="margin-right: 5px;"><Download /></el-icon>
          开始导出
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed } from 'vue'
import { Document, Files, Download } from '@element-plus/icons-vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  exportFormat: {
    type: String,
    default: 'excel'
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'confirm', 'update:exportFormat'])

const dialogVisible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const localExportFormat = computed({
  get: () => props.exportFormat,
  set: (val) => emit('update:exportFormat', val)
})

const handleClose = () => {
  emit('update:modelValue', false)
}

const handleConfirm = () => {
  emit('confirm')
}
</script>
