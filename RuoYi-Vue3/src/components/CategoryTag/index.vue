<template>
  <el-tag
    v-if="category"
    :type="tagType"
    :effect="effect"
    :closable="closable"
    :size="size"
    @close="handleClose"
  >
    <span v-if="showIcon && category.icon" class="tag-icon">{{ category.icon }}</span>
    {{ category.name }}
  </el-tag>
</template>

<script setup name="CategoryTag">
import { computed } from 'vue'

const props = defineProps({
  category: {
    type: Object,
    required: true
  },
  effect: {
    type: String,
    default: 'light',
    validator: (value) => ['light', 'dark', 'plain'].includes(value)
  },
  closable: {
    type: Boolean,
    default: false
  },
  size: {
    type: String,
    default: 'default',
    validator: (value) => ['large', 'default', 'small'].includes(value)
  },
  showIcon: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['close'])

// 根据分类类型返回不同的 tag 颜色
const tagType = computed(() => {
  const typeMap = {
    game: 'primary',      // 游戏：蓝色
    drama: 'success',     // 短剧：绿色
    article: 'warning',   // 文章：橙色
    website: 'info',      // 网站：灰色
    gamebox: 'danger',    // 游戏盒子：红色
    document: '',         // 文档：默认色
    other: 'info'         // 其他：灰色
  }
  return typeMap[props.category.categoryType] || 'info'
})

function handleClose() {
  emit('close', props.category)
}
</script>

<style scoped>
.tag-icon {
  margin-right: 4px;
}
</style>
