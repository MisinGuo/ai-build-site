/**
 * 统一路由配置
 * 集中管理所有页面的路由、导航、SEO配置
 */

export interface RouteConfig {
  /** 路由路径 */
  path: string
  /** 多语言翻译 key（可选） */
  i18nKey?: string
  /** 页面标题（用于导航和SEO） */
  title: string
  /** 页面描述 */
  description?: string
  /** 是否启用该页面 */
  enabled: boolean
  /** 是否在导航栏显示 */
  showInNav: boolean
  /** 导航栏位置（数字越小越靠前） */
  navOrder?: number
  /** 父级路由（用于下拉菜单） */
  parent?: string
  /** 图标（可选） */
  icon?: string
  /** 子路由 */
  children?: RouteConfig[]
}

/**
 * 所有路由配置
 */
export const routes: RouteConfig[] = [
  {
    path: '/',
    i18nKey: 'home',
    title: '首页',
    description: '游戏盒子推荐、游戏折扣对比平台',
    enabled: true,
    showInNav: true,
    navOrder: 1,
  },
  {
    path: '/games',
    i18nKey: 'games',
    title: '游戏',
    description: '热门游戏折扣对比',
    enabled: true,
    showInNav: true,
    navOrder: 2,
  },
  {
    path: '/boxes',
    i18nKey: 'boxes',
    title: '游戏盒子',
    description: '游戏盒子推荐和对比',
    enabled: true,
    showInNav: true,
    navOrder: 3,
  },
  {
    path: '/guides',
    i18nKey: 'guides',
    title: '攻略',
    description: '游戏攻略合集',
    enabled: true,
    showInNav: true,
    navOrder: 4,
  },
  {
    path: '/reviews',
    i18nKey: 'reviews',
    title: '评测',
    description: '游戏评测合集',
    enabled: true,
    showInNav: true,
    navOrder: 5,
  },
]

/**
 * 获取启用的路由
 */
export function getEnabledRoutes(): RouteConfig[] {
  return routes.filter(route => route.enabled)
}

/**
 * 获取导航栏路由
 */
export function getNavRoutes(): RouteConfig[] {
  return routes
    .filter(route => route.enabled && route.showInNav)
    .sort((a, b) => (a.navOrder || 999) - (b.navOrder || 999))
}

/**
 * 解析路由显示文案：优先翻译 key，其次按 path 推导 key，最后回退 title
 */
export function getRouteDisplayLabel(route: RouteConfig, t: (key: string) => string): string {
  const fallbackKey = route.path === '/' ? 'home' : route.path.replace(/^\//, '').split('/')[0]
  const key = route.i18nKey || fallbackKey
  const translated = t(key)
  return translated === key ? route.title : translated
}
