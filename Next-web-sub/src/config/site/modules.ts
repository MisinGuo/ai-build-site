import type { ModuleConfig, ModuleType } from '../types'

/**
 * 文档模块配置
 * 
 * 每个模块 (guides、reviews) 可以有不同的：
 * - 布局和样式
 * - 功能组件 (下载入口、分类过滤器等)
 * - 侧边栏内容
 * - 列表展示方式
 */
export const modules: Record<ModuleType, ModuleConfig> = {
  /**
   * 攻略模块
   */
  guides: {
    type: 'guides',
    title: '游戏攻略',
    description: '游戏攻略与阵容思路',
    contentPath: 'api/articles',
    routePrefix: '/guides',
    
    theme: {
      primaryColor: 'green',
      badgeColor: 'bg-green-500/10 text-green-400 border-green-500/20',
      badgeText: '攻略',
    },
    
    categoryFilter: {
      enabled: true,
      title: '攻略分类',
      type: 'category',
      showCount: true,
    },
    
    downloadEntry: {
      enabled: true,
      position: 'sidebar',
      buttonText: '查看游戏盒子',
      buttonLink: '/boxes',
      buttonStyle: 'gradient',
    },
    
    sidebar: {
      enabled: true,
      showTOC: true,
      showRelated: true,
      showDownload: true,
      components: ['TOC', 'RelatedArticles', 'Download'],
    },
    
    articleList: {
      layout: 'grid',
      showCover: true,
      showExcerpt: true,
      pageSize: 12,
      showDate: true,
      showCategory: true,
      showReadingTime: false,
    },
    
    articleDetail: {
      showBreadcrumb: true,
      showMeta: true,
      showTOC: true,
      showAuthor: false,
      showTags: false,
      showRelated: false,
      showComments: false,
      showShare: true,
    },
  },

  /**
   * 评测模块
   */
  reviews: {
    type: 'reviews',
    title: '游戏评测',
    description: '横评对比与实测结论',
    contentPath: 'api/articles',
    routePrefix: '/reviews',

    theme: {
      primaryColor: 'orange',
      badgeColor: 'bg-orange-500/10 text-orange-400 border-orange-500/20',
      badgeText: '评测',
    },

    categoryFilter: {
      enabled: true,
      title: '评测分类',
      type: 'category',
      showCount: true,
    },

    downloadEntry: {
      enabled: true,
      position: 'sidebar',
      buttonText: '查看游戏盒子',
      buttonLink: '/boxes',
      buttonStyle: 'gradient',
    },

    sidebar: {
      enabled: true,
      showTOC: true,
      showRelated: true,
      showDownload: true,
      components: ['TOC', 'RelatedArticles', 'Download'],
    },

    articleList: {
      layout: 'grid',
      showCover: true,
      showExcerpt: true,
      pageSize: 12,
      showDate: true,
      showCategory: true,
      showReadingTime: false,
    },

    articleDetail: {
      showBreadcrumb: true,
      showMeta: true,
      showTOC: true,
      showAuthor: false,
      showTags: false,
      showRelated: false,
      showComments: false,
      showShare: true,
    },
  },
}

/** 获取模块配置 */
export function getModuleConfig(type: ModuleType): ModuleConfig {
  return modules[type]
}
