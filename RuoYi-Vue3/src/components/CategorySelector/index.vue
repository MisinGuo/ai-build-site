<template>
  <el-select
    v-model="selectedValue"
    :placeholder="placeholder"
    :clearable="clearable"
    :multiple="multiple"
    :filterable="filterable"
    :disabled="disabled"
    @change="handleChange"
  >
    <el-option
      v-for="item in categoryOptions"
      :key="item.id"
      :label="item.name"
      :value="item.id"
    >
      <span v-if="item.icon" class="category-icon">{{ item.icon }}</span>
      <span>{{ item.name }}</span>
      <span v-if="showCount && item.documentCount" class="category-count">({{ item.documentCount }})</span>
    </el-option>
  </el-select>
</template>

<script setup name="CategorySelector">
import { ref, watch, onMounted, computed } from 'vue'
import { listCategory } from '@/api/gamebox/category'

const props = defineProps({
  modelValue: {
    type: [Number, String, Array],
    default: null
  },
  // 分类类型：game-游戏、drama-短剧、article-文章、website-网站、gamebox-游戏盒子、document-文档、other-其他
  categoryType: {
    type: String,
    required: true,
    validator: (value) => {
      return ['game', 'drama', 'article', 'website', 'gamebox', 'document', 'other'].includes(value)
    }
  },
  placeholder: {
    type: String,
    default: '请选择分类'
  },
  clearable: {
    type: Boolean,
    default: true
  },
  multiple: {
    type: Boolean,
    default: false
  },
  filterable: {
    type: Boolean,
    default: true
  },
  disabled: {
    type: Boolean,
    default: false
  },
  siteId: {
    type: Number,
    default: null
  },
  showCount: {
    type: Boolean,
    default: false
  },
  // 是否只显示父分类
  parentOnly: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'change'])

const selectedValue = ref(props.modelValue)
const categoryOptions = ref([])

watch(() => props.modelValue, (newVal) => {
  selectedValue.value = newVal
})

watch(() => props.categoryType, () => {
  loadCategories()
})

watch(() => props.siteId, () => {
  loadCategories()
})

onMounted(() => {
  loadCategories()
})

function loadCategories() {
  const params = {
    categoryType: props.categoryType,
    status: '0', // 只查询启用的
    pageNum: 1,
    pageSize: 1000
  }
  
  if (props.siteId) {
    params.siteId = props.siteId
  }
  
  listCategory(params).then(response => {
    let categories = response.rows || []
    
    // 如果只显示父分类，过滤掉子分类
    if (props.parentOnly) {
      categories = categories.filter(item => !item.parentId || item.parentId === 0)
    }
    
    categoryOptions.value = categories
  })
}

function handleChange(value) {
  emit('update:modelValue', value)
  
  // 如果是单选，返回完整的分类对象
  if (!props.multiple && value) {
    const category = categoryOptions.value.find(item => item.id === value)
    emit('change', value, category)
  } else {
    // 如果是多选，返回所有选中的分类对象
    const categories = categoryOptions.value.filter(item => value && value.includes(item.id))
    emit('change', value, categories)
  }
}

// 暴露方法供父组件调用
defineExpose({
  loadCategories
})
</script>

<style scoped>
.category-icon {
  margin-right: 5px;
}
.category-count {
  color: #909399;
  font-size: 12px;
  margin-left: 5px;
}
</style>
