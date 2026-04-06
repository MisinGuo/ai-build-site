<template>
  <div class="image-search-executor">
    <el-alert title="图片搜索工具功能开发中" type="info" :closable="false" show-icon />
    <p style="margin-top: 20px; color: #606266;">此功能正在开发中，敬请期待。</p>
    
    <div style="margin-top: 30px;">
      <el-button @click="$emit('cancel')">返回</el-button>
    </div>
  </div>
</template>

<script setup>
defineProps({
  tool: Object,
  siteId: Number
})

defineEmits(['success', 'cancel'])
</script>
