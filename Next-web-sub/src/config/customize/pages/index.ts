/**
 * 页面配置统一入口
 * 按路由组织所有页面配置
 */

// 导出首页类型（从 home 模块）
export type {
  HomePageConfig,
  HeroConfig,
  StatItem,
  ContentSectionConfig,
} from './home'

export type { GuidesPageConfig } from './guides'
export type { ReviewsPageConfig } from './reviews'

// 导出各页面配置
export { homeConfig } from './home'
export { guidesConfig, guideSectionSlugs } from './guides'
export { reviewsConfig, reviewSectionSlugs } from './reviews'

import { homeConfig } from './home'
import { guidesConfig } from './guides'
import { reviewsConfig } from './reviews'

/**
 * 路由到配置的映射
 */
export const routeConfigs = {
  '/': homeConfig,
  '/guides': guidesConfig,
  '/reviews': reviewsConfig,
} as const

/**
 * 根据路由获取页面配置
 */
export function getPageConfig(route: keyof typeof routeConfigs) {
  return routeConfigs[route]
}
