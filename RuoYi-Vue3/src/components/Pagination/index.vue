<template>
  <div :class="{ 'hidden': hidden }" class="pagination-container">
    <!-- 自定义每页条数选择器（替代内置 sizes） -->
    <el-select
      ref="selectRef"
      :model-value="pageSizeSelectVal"
      size="small"
      class="page-size-select"
      :teleported="false"
      @change="handleSizeSelectChange"
    >
      <el-option
        v-for="size in displaySizes"
        :key="size"
        :label="`${size}条/页`"
        :value="size"
      />
      <!-- 自定义输入行，disabled 防止点击直接选中 -->
      <el-option class="custom-input-option" value="__custom__" label="" disabled>
        <div class="custom-size-row" @click.stop @mousedown.stop>
          <el-input
            v-model="customSizeInput"
            type="number"
            size="small"
            placeholder="自定义"
            class="custom-size-input"
            @click.stop
            @mousedown.stop
            @keyup.enter.stop="applyCustomSize"
          />
          <el-button
            type="primary"
            size="small"
            @mousedown.stop.prevent="applyCustomSize"
          >确定</el-button>
        </div>
      </el-option>
    </el-select>

    <el-pagination
      :background="background"
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :layout="layoutWithoutSizes"
      :pager-count="pagerCount"
      :total="total"
      @current-change="handleCurrentChange"
    />
  </div>
</template>

<script setup>
import { scrollTo } from '@/utils/scroll-to'

const props = defineProps({
  total: {
    required: true,
    type: Number
  },
  page: {
    type: Number,
    default: 1
  },
  limit: {
    type: Number,
    default: 20
  },
  pageSizes: {
    type: Array,
    default() {
      return [10, 20, 30, 50]
    }
  },
  // 移动端页码按钮的数量端默认值5
  pagerCount: {
    type: Number,
    default: document.body.clientWidth < 992 ? 5 : 7
  },
  layout: {
    type: String,
    default: 'total, sizes, prev, pager, next, jumper'
  },
  background: {
    type: Boolean,
    default: true
  },
  autoScroll: {
    type: Boolean,
    default: true
  },
  hidden: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits()
const selectRef = ref(null)
const customSizeInput = ref('')

const currentPage = computed({
  get() {
    return props.page
  },
  set(val) {
    emit('update:page', val)
  }
})
const pageSize = computed({
  get() {
    return props.limit
  },
  set(val){
    emit('update:limit', val)
  }
})

// 如果当前 limit 不在预设列表中（如用户自定义过），动态加进去保证选中态正常显示
const displaySizes = computed(() => {
  if (props.pageSizes.includes(props.limit)) return props.pageSizes
  return [...props.pageSizes, props.limit].sort((a, b) => a - b)
})

const pageSizeSelectVal = computed(() => props.limit)

// 从 layout 中去掉内置的 sizes，由我们的自定义 select 替代
const layoutWithoutSizes = computed(() =>
  props.layout.split(',').map(s => s.trim()).filter(s => s !== 'sizes').join(', ')
)

function handleSizeSelectChange(val) {
  if (typeof val !== 'number') return
  if (currentPage.value * val > props.total) {
    currentPage.value = 1
  }
  emit('update:limit', val)
  emit('pagination', { page: currentPage.value, limit: val })
  if (props.autoScroll) {
    scrollTo(0, 800)
  }
}

function applyCustomSize() {
  const val = parseInt(customSizeInput.value)
  if (!val || val < 1 || val > 9999) return
  customSizeInput.value = ''
  if (currentPage.value * val > props.total) {
    currentPage.value = 1
  }
  emit('update:limit', val)
  emit('pagination', { page: currentPage.value, limit: val })
  if (props.autoScroll) {
    scrollTo(0, 800)
  }
  selectRef.value?.blur()
}

function handleCurrentChange(val) {
  emit('pagination', { page: val, limit: pageSize.value })
  if (props.autoScroll) {
    scrollTo(0, 800)
  }
}
</script>

<style scoped>
.pagination-container {
  background: #fff;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}
.pagination-container.hidden {
  display: none;
}
.page-size-select {
  width: 105px;
}
/* 覆盖 disabled 选项的禁用样式，让自定义输入行正常显示 */
:deep(.custom-input-option.is-disabled) {
  opacity: 1 !important;
  cursor: default !important;
  color: inherit !important;
}
:deep(.custom-input-option) {
  height: auto !important;
  padding: 6px 8px !important;
  border-top: 1px solid #f0f0f0;
}
:deep(.custom-input-option .el-select-dropdown__item) {
  padding: 0;
}
.custom-size-row {
  display: flex;
  align-items: center;
  gap: 6px;
}
.custom-size-input {
  width: 80px;
}
/* 隐藏 number 输入框的上下箭头 */
:deep(.custom-size-input input::-webkit-inner-spin-button),
:deep(.custom-size-input input::-webkit-outer-spin-button) {
  -webkit-appearance: none;
}
</style>