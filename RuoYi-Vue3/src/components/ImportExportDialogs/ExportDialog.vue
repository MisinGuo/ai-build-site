<template>
  <el-dialog title="导出数据" v-model="dialogVisible" width="500px" append-to-body @close="handleClose">
    <div style="margin: 20px 0;">
      <p style="margin-bottom: 20px; color: #606266; text-align: center;">
        已选择 <strong style="color: #409EFF;">{{ selectedCount }}</strong> 条数据
      </p>
      
      <div style="margin-bottom: 20px;">
        <el-alert
          title="导出说明"
          type="info"
          :closable="false"
        >
          <template #default>
            <div style="font-size: 13px; line-height: 1.5; text-align: left;">
              <slot name="exportTips">
                <p>• 数据导出将包含所有选中的数据及其层级关系</p>
                <p>• 翻译数据：可选择需要导出的语言版本</p>
                <p style="margin-top: 10px; color: #909399;">导入时将自动归属到当前选择的网站</p>
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
        
        <el-form-item label="包含翻译" v-if="availableLanguages.length > 0">
          <el-switch v-model="localIncludeTranslations" />
          <span style="margin-left: 10px; color: #909399; font-size: 13px;">是否导出多语言翻译数据</span>
        </el-form-item>
        
        <el-form-item label="额外语言" v-if="localIncludeTranslations && availableLanguages.length > 0">
          <el-select 
            v-model="localSelectedLanguages" 
            multiple 
            collapse-tags 
            collapse-tags-tooltip
            placeholder="请选择要导出的额外语言"
            style="width: 100%;"
          >
            <el-option
              v-for="lang in availableLanguages"
              :key="lang.code"
              :label="lang.name"
              :value="lang.code"
            />
          </el-select>
          <div style="margin-top: 5px; color: #909399; font-size: 12px;">
            默认语言会自动包含，不选择则只导出默认语言
          </div>
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
import { ref, watch, computed } from 'vue'
import { Document, Files, Download } from '@element-plus/icons-vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  selectedCount: {
    type: Number,
    default: 0
  },
  availableLanguages: {
    type: Array,
    default: () => []
  },
  exportFormat: {
    type: String,
    default: 'excel'
  },
  includeTranslations: {
    type: Boolean,
    default: false
  },
  selectedLanguages: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'confirm', 'update:exportFormat', 'update:includeTranslations', 'update:selectedLanguages'])

const dialogVisible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const localExportFormat = computed({
  get: () => props.exportFormat,
  set: (val) => emit('update:exportFormat', val)
})

const localIncludeTranslations = computed({
  get: () => props.includeTranslations,
  set: (val) => emit('update:includeTranslations', val)
})

const localSelectedLanguages = computed({
  get: () => props.selectedLanguages,
  set: (val) => emit('update:selectedLanguages', val)
})

const handleClose = () => {
  emit('update:modelValue', false)
}

const handleConfirm = () => {
  emit('confirm')
}
</script>
