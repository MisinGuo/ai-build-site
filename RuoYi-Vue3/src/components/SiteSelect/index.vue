<template>
  <el-select
    :model-value="normalizedModelValue"
    :placeholder="placeholder"
    :clearable="clearable"
    :disabled="disabled"
    :filterable="filterable"
    :style="{ width: width }"
    @update:model-value="handleUpdate"
    @change="handleChange"
  >
    <el-option v-if="showDefault && defaultSite" :label="resolvedDefaultLabel" :value="defaultSite.id" />
    <el-option
      v-for="site in visibleSiteList"
      :key="site.id"
      :label="site.displayLabel"
      :value="site.id"
    >
      <span>{{ site.name }}</span>
      <el-tag v-if="site.isDefault === 1" type="warning" size="small" style="margin-left:6px;transform:scale(0.85)">默认</el-tag>
      <el-tag v-if="site.isPersonal === 1" type="info" size="small" style="margin-left:4px;transform:scale(0.85)">个人</el-tag>
    </el-option>
    <template v-if="$slots.empty" #empty>
      <slot name="empty" />
    </template>
  </el-select>
</template>

<script setup>
import { computed } from 'vue'
import { getDefaultSiteOptionLabel, resolveDefaultSite } from '@/composables/useSiteSelection'

const props = defineProps({
  modelValue: { type: [Number, String], default: undefined },
  siteList:   { type: Array,           default: () => [] },
  showDefault: { type: Boolean,        default: false },
  defaultLabel: { type: String,        default: '默认配置' },
  hideDefaultSiteInList: { type: Boolean, default: true },
  disabled:    { type: Boolean,         default: false },
  filterable:  { type: Boolean,         default: false },
  clearable:  { type: Boolean,         default: false },
  placeholder: { type: String,         default: '请选择网站' },
  width:      { type: String,          default: '200px' },
})

const emit = defineEmits(['update:modelValue', 'change'])

function toComparableValue(value) {
  if (value === null || value === undefined || value === '') {
    return value
  }
  const numericValue = Number(value)
  return Number.isNaN(numericValue) ? value : numericValue
}

// 内部统一计算 displayLabel，无需外部 enrichSiteList
const enrichedList = computed(() =>
  props.siteList.map(site => {
    const tags = []
    if (site.isDefault === 1) tags.push('[默认]')
    if (site.isPersonal === 1) tags.push('[个人]')
    return {
      ...site,
      displayLabel: tags.length ? `${site.name} ${tags.join(' ')}` : site.name,
    }
  })
)

const defaultSite = computed(() => resolveDefaultSite(props.siteList))

const resolvedDefaultLabel = computed(() =>
  getDefaultSiteOptionLabel(props.siteList, props.defaultLabel)
)

const normalizedModelValue = computed(() => {
  const currentValue = toComparableValue(props.modelValue)
  if (!props.showDefault || !defaultSite.value) {
    return currentValue
  }
  const defaultId = toComparableValue(defaultSite.value.id)
  // 兼容旧值 0：映射到实际个人站点 ID
  if (currentValue === 0) {
    return defaultId
  }
  return currentValue
})

const visibleSiteList = computed(() => {
  return enrichedList.value.filter(site => {
    // 始终过滤掉个人默认配置站（is_personal=1），该站通过 showDefault 选项单独展示
    if (site.isPersonal === 1) return false
    // showDefault 模式下，隐藏默认站的普通条目（已通过"默认配置"选项展示）
    if (props.showDefault && props.hideDefaultSiteInList && defaultSite.value) {
      return site.id !== defaultSite.value.id
    }
    return true
  })
})

function handleUpdate(value) {
  emit('update:modelValue', toComparableValue(value))
}

function handleChange(value) {
  emit('change', toComparableValue(value))
}
</script>
