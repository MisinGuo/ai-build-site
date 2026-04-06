<template>
  <el-dialog
    :title="`管理翻译 - ${entityName}`"
    v-model="dialogVisible"
    width="800px"
    append-to-body
    @close="handleClose"
  >
    <el-alert
      title="说明"
      type="info"
      :closable="false"
      style="margin-bottom: 20px"
    >
      为不同语言版本设置翻译内容。留空的字段将使用原始值。
    </el-alert>

    <div v-loading="loadingLocales" style="min-height: 200px">
      <el-empty v-if="!loadingLocales && locales.length === 0" description="该站点未配置其他语言" />
      
      <el-tabs v-else v-model="activeLocale" type="border-card">
      <el-tab-pane 
        v-for="locale in locales" 
        :key="locale.value" 
        :label="locale.label"
        :name="locale.value"
      >
        <el-form 
          :model="translations[locale.value]" 
          label-width="100px"
          style="margin-top: 20px"
        >
          <el-form-item 
            v-for="field in translationFields" 
            :key="field.name"
            :label="field.label"
          >
            <el-input
              v-if="field.type === 'text'"
              v-model="translations[locale.value][field.name]"
              :placeholder="`${field.label}（${locale.label}）`"
              clearable
            />
            <el-input
              v-else-if="field.type === 'textarea'"
              v-model="translations[locale.value][field.name]"
              type="textarea"
              :rows="4"
              :placeholder="`${field.label}（${locale.label}）`"
              clearable
            />
          </el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取 消</el-button>
        <el-button type="success" @click="handleAutoTranslate" :loading="autoTranslating" icon="MagicStick">AI自动翻译</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">保存全部</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch, reactive } from 'vue'
import { getEntityTranslations, batchSaveTranslations, deleteFieldTranslation, autoTranslateEntity, getSupportedLocales } from '@/api/gamebox/translation'
import { ElMessage, ElMessageBox } from 'element-plus'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  entityType: {
    type: String,
    required: true,
    validator: (value) => ['game', 'box', 'category', 'drama', 'site'].includes(value)
  },
  entityId: {
    type: Number,
    required: true
  },
  entityName: {
    type: String,
    default: ''
  },
  // 站点ID（必需，用于获取支持的语言）
  siteId: {
    type: Number,
    required: true
  },
  // 可翻译的字段配置
  translationFields: {
    type: Array,
    required: true,
    // 格式: [{ name: 'name', label: '名称', type: 'text' }, { name: 'description', label: '描述', type: 'textarea' }]
  },
  // 原始数据（用于自动翻译）
  originalData: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['update:modelValue', 'refresh'])

const dialogVisible = ref(false)
const activeLocale = ref('')
const saving = ref(false)
const autoTranslating = ref(false)
const loadingLocales = ref(false)

// 支持的语言（从API加载）
const locales = ref([])

// 翻译数据：{ 'zh-TW': { name: '', description: '' }, 'en': { name: '', description: '' } }
const translations = reactive({})

// 加载支持的语言列表
async function loadSupportedLocales() {
  if (!props.siteId) return
  
  loadingLocales.value = true
  try {
    const response = await getSupportedLocales(props.siteId)
    const data = response.data || []
    
    // 过滤掉默认语言（isDefault为true的语言不需要翻译）
    locales.value = data.filter(locale => locale.isDefault !== 'true')
    
    if (locales.value.length > 0 && !activeLocale.value) {
      activeLocale.value = locales.value[0].value
    }
    
    // 初始化翻译数据结构
    initTranslations()
  } catch (error) {
    console.error('加载支持的语言失败:', error)
    ElMessage.error('加载支持的语言失败')
  } finally {
    loadingLocales.value = false
  }
}

// 初始化翻译数据结构
function initTranslations() {
  // 清空现有数据
  Object.keys(translations).forEach(key => {
    delete translations[key]
  })
  
  // 根据加载的语言列表初始化
  locales.value.forEach(locale => {
    translations[locale.value] = {}
    props.translationFields.forEach(field => {
      translations[locale.value][field.name] = ''
    })
  })
}

// 加载翻译数据
async function loadTranslations() {
  if (!props.entityId) return
  
  try {
    const response = await getEntityTranslations(props.entityType, props.entityId)
    const data = response.data || []
    
    // 重置翻译数据
    initTranslations()
    
    // 填充已有的翻译
    data.forEach(item => {
      if (translations[item.locale]) {
        translations[item.locale][item.fieldName] = item.fieldValue
      }
    })
  } catch (error) {
    console.error('加载翻译失败:', error)
    ElMessage.error('加载翻译失败')
  }
}

// 保存翻译
async function handleSave() {
  if (locales.value.length === 0) {
    ElMessage.warning('没有可保存的语言')
    return
  }
  
  saving.value = true
  
  try {
    // 按语言分别构建保存数据
    const savePromises = []
    // 新增：保存和删除分开处理
    const deletePromises = [];
    locales.value.forEach(locale => {
      const localeTranslations = {}
      let hasData = false
      props.translationFields.forEach(field => {
        const value = translations[locale.value][field.name]
        if (value && value.trim()) {
          localeTranslations[field.name] = value.trim()
          hasData = true
        } else {
          // 如果原来有翻译，现在清空，则删除
          deletePromises.push(deleteFieldTranslation(props.entityType, props.entityId, locale.value, field.name))
        }
      })
      if (hasData) {
        const saveData = {
          entityType: props.entityType,
          entityId: props.entityId,
          locale: locale.value,
          translations: localeTranslations
        }
        savePromises.push(batchSaveTranslations(saveData))
      }
    })
    if (savePromises.length === 0 && deletePromises.length === 0) {
      ElMessage.warning('请至少填写一个翻译字段')
      return
    }
    await Promise.all([...savePromises, ...deletePromises])
    ElMessage.success('保存成功')
    emit('refresh')
    handleClose()
  } catch (error) {
    console.error('保存翻译失败:', error)
    ElMessage.error('保存翻译失败')
  } finally {
    saving.value = false
  }
}

// AI自动翻译
async function handleAutoTranslate() {
  if (locales.value.length === 0) {
    ElMessage.warning('没有可翻译的语言')
    return
  }
  
  const localeLabels = locales.value.map(l => l.label).join('、')
  
  try {
    await ElMessageBox.confirm(
      `将使用AI自动翻译所有字段到${localeLabels}，这将覆盖现有翻译内容。是否继续？`,
      '自动翻译确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    autoTranslating.value = true
    
    // 获取原始字段值（从主表读取，需要调用各自的详情接口）
    // 这里简化处理，假设已经在打开对话框时获取了原始数据
    // 实际应该从父组件传入或者通过API获取
    
    // 构建需要翻译的字段映射
    const fieldsToTranslate = {}
    props.translationFields.forEach(field => {
      // 这里需要获取主表的原始值，暂时使用空字符串占位
      // 实际应该从父组件传入原始数据
      const originalValue = getOriginalFieldValue(field.name)
      if (originalValue) {
        fieldsToTranslate[field.name] = originalValue
      }
    })
    
    if (Object.keys(fieldsToTranslate).length === 0) {
      ElMessage.warning('没有可翻译的字段')
      return
    }
    
    // 调用自动翻译API
    await autoTranslateEntity(props.entityType, props.entityId, props.siteId, fieldsToTranslate)
    
    ElMessage.success('自动翻译完成')
    
    // 重新加载翻译数据
    await loadTranslations()
    
    emit('refresh')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('自动翻译失败:', error)
      ElMessage.error('自动翻译失败')
    }
  } finally {
    autoTranslating.value = false
  }
}

// 获取原始字段值（从传入的原始数据中获取）
function getOriginalFieldValue(fieldName) {
  return props.originalData[fieldName] || ''
}

// 关闭对话框
function handleClose() {
  dialogVisible.value = false
  emit('update:modelValue', false)
}

// 监听 modelValue 变化
watch(() => props.modelValue, (val) => {
  dialogVisible.value = val
  if (val) {
    loadSupportedLocales().then(() => {
      loadTranslations()
    })
  }
})

// 监听 dialogVisible 变化
watch(dialogVisible, (val) => {
  if (!val) {
    emit('update:modelValue', false)
  }
})
</script>

<style scoped>
:deep(.el-tabs__content) {
  padding: 0 20px;
}
</style>
