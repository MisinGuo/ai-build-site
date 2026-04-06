/**
 * 分类类型配置
 * 可以在此文件中方便地添加、修改或删除分类类型
 */

export const CATEGORY_TYPES = [
  {
    value: 'game',
    label: '游戏分类',
    tagType: 'primary', // Element Plus tag组件的type属性
    description: '用于游戏管理页面的分类'
  },
  {
    value: 'drama',
    label: '短剧分类',
    tagType: 'success',
    description: '用于短剧管理页面的分类'
  },
  {
    value: 'article',
    label: '文章分类',
    tagType: 'warning',
    description: '用于文章管理页面的分类'
  },
  {
    value: 'website',
    label: '网站分类',
    tagType: 'info',
    description: '用于网站管理页面的分类'
  },
  {
    value: 'gamebox',
    label: '游戏盒子分类',
    tagType: 'danger',
    description: '用于游戏盒子管理页面的分类'
  },
  {
    value: 'document',
    label: '文档分类',
    tagType: 'info',
    description: '用于文档管理页面的分类'
  },
  {
    value: 'storage',
    label: '存储配置分类',
    tagType: 'primary',
    description: '用于存储配置页面的分类'
  },
  {
    value: 'other',
    label: '其他分类',
    tagType: 'info',
    description: '用于其他未分类的内容'
  }
]

/**
 * 根据分类类型值获取配置对象
 * @param {string} value - 分类类型值
 * @returns {object|undefined} 分类类型配置对象
 */
export function getCategoryTypeConfig(value) {
  return CATEGORY_TYPES.find(type => type.value === value)
}

/**
 * 根据分类类型值获取标签
 * @param {string} value - 分类类型值
 * @returns {string} 分类类型标签
 */
export function getCategoryTypeLabel(value) {
  const config = getCategoryTypeConfig(value)
  return config ? config.label : value
}

/**
 * 根据分类类型值获取Tag类型
 * @param {string} value - 分类类型值
 * @returns {string} Element Plus Tag组件的type属性值
 */
export function getCategoryTypeTagType(value) {
  const config = getCategoryTypeConfig(value)
  return config ? config.tagType : 'info'
}

/**
 * 获取所有分类类型的选项列表（用于下拉框）
 * @returns {Array} 分类类型选项数组
 */
export function getCategoryTypeOptions() {
  return CATEGORY_TYPES.map(type => ({
    label: type.label,
    value: type.value
  }))
}
