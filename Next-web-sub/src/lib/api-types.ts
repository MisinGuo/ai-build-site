/**
 * API多语言请求类型定义
 * 
 * 所有支持多语言的API接口都应该使用这些类型
 */

import type { Locale } from '@/config/types'

const FALLBACK_SUPPORTED_LOCALES: Locale[] = ['zh-CN', 'zh-TW']

function getConfiguredLocales(): Locale[] {
  const raw = process.env.NEXT_PUBLIC_SUPPORTED_LOCALES?.trim()
  if (!raw) return FALLBACK_SUPPORTED_LOCALES

  const allowList: Locale[] = ['zh-CN', 'zh-TW']
  const parsed = raw
    .split(',')
    .map((item) => item.trim() as Locale)
    .filter((item): item is Locale => allowList.includes(item))

  return parsed.length > 0 ? Array.from(new Set(parsed)) : FALLBACK_SUPPORTED_LOCALES
}

function getConfiguredDefaultLocale(supported: Locale[]): Locale {
  const envDefault = process.env.NEXT_PUBLIC_DEFAULT_LOCALE?.trim() as Locale | undefined
  if (envDefault && supported.includes(envDefault)) {
    return envDefault
  }
  return supported[0] || 'zh-CN'
}

// ==================== 响应数据类型 ====================

/**
 * 网站配置信息
 */
export interface SiteConfigInfo {
  id: number
  name: string
  code: string
  domain: string
  logoUrl?: string
  faviconUrl?: string
  description?: string
  seoTitle?: string
  seoKeywords?: string
  seoDescription?: string
  defaultLocale: string
}

/**
 * 首页数据响应
 */
export interface HomeDataResponse {
  /** 网站配置信息 */
  siteConfig?: SiteConfigInfo
  /** 统计数据 */
  statistics?: {
    boxCount: number
    gameCount: number
    articleCount: number
    totalSavings: number
  }
  /** 攻略文章列表 */
  strategyArticles?: ArticleListItem[]
  /** 热门游戏列表 */
  hotGames?: GameListItem[]
}

/**
 * 文章列表项
 */
export interface ArticleListItem {
  masterArticleId: number
  title: string
  slug: string
  coverImage?: string
  coverUrl?: string
  categoryName?: string
  viewCount: number
  createTime: string
  readingTime: number
}

/**
 * 游戏列表项
 */
export interface GameListItem {
  id: number
  title: string
  slug: string
  coverImage?: string
  categoryName?: string
  viewCount?: number
  createTime?: string
  iconUrl?: string
  name?: string
  description?: string
  rating?: number
  downloadCount?: number
  tags?: string[]
  isNew?: boolean
  isHot?: boolean
  isRecommend?: boolean
}

/**
 * 礼包列表项
 */
export interface GiftListItem {
  id: number
  name: string
  code?: string
  content: string
  gameId: number
  gameName: string
  gameIcon?: string
  validUntil?: string
  receiveCount?: number
  totalCount?: number
  status?: string
}

/**
 * 品类详情响应（增强版）
 */
export interface CategoryDetailResponse {
  /** 品类基本信息 */
  category: {
    id: number
    name: string
    slug: string
    icon?: string
    description?: string
    longDescription?: string
    bannerUrl?: string
    tags?: string[]
    welcomeMessage?: string
  }
  /** TOP游戏 */
  topGames: GameListItem[]
  /** 通用攻略 */
  commonGuides?: ArticleListItem[]
  /** 热门攻略 */
  hotGuides?: ArticleListItem[]
  /** 最新攻略 */
  latestGuides?: ArticleListItem[]
  /** 品类礼包 */
  gifts: GiftListItem[]
  /** 相关品类 */
  relatedCategories: Array<{
    id: number
    name: string
    slug: string
    icon?: string
    gamesCount?: number
  }>
  /** 统计数据 */
  stats: {
    gamesCount: number
    guidesCount: number
    giftsCount: number
  }
  /** 子站链接（用于反向导流） */
  subSiteUrl?: string
  /** 是否来自子站 */
  fromSubSite?: boolean
}

/**
 * 基础多语言请求参数
 * 所有API请求都应该包含这些参数
 */
export interface LocaleRequestParams {
  /** 语言代码 */
  locale?: Locale
  /** 站点ID（自动从环境变量读取，一般不需要手动传递） */
  siteId?: number | string
}

/**
 * 分页请求参数（带多语言）
 */
export interface LocalePaginationParams extends LocaleRequestParams {
  /** 页码，从1开始 */
  pageNum?: number
  /** 每页数量 */
  pageSize?: number
}

/**
 * 文章列表请求参数
 */
export interface ArticleListParams extends LocalePaginationParams {
  /** 分类ID */
  categoryId?: number
  /** 标签ID */
  tagId?: number
  /** 是否置顶 */
  isTop?: string
  /** 文章板块或分类 slug（由站点配置维护） */
  section?: string
}

/**
 * 文章详情请求参数
 */
export interface ArticleDetailParams extends LocaleRequestParams {
  /** 文章ID */
  id: number
}

/**
 * 搜索请求参数
 */
export interface SearchParams extends LocalePaginationParams {
  /** 搜索关键词 */
  keyword: string
}

/**
 * 分类列表请求参数
 */
export interface CategoryListParams extends LocaleRequestParams {
  /** 分类类型：game_box, game, article */
  categoryType?: string
  /** 父级分类ID */
  parentId?: number
}

/**
 * 分类树请求参数
 */
export interface CategoryTreeParams extends LocaleRequestParams {
  /** 分类类型：game_box, game, article */
  categoryType: string
}

/**
 * 游戏盒子列表请求参数
 */
export interface BoxListParams extends LocalePaginationParams {
  /** 分类ID */
  categoryId?: number
}

/**
 * 游戏盒子详情请求参数
 */
export interface BoxDetailParams extends LocaleRequestParams {
  /** 盒子ID */
  id: number
}

/**
 * 游戏列表请求参数
 */
export interface GameListParams extends LocalePaginationParams {
  /** 分类ID */
  categoryId?: number
  /** 游戏类型 */
  gameType?: string
  /** 仅查询无分类游戏 */
  uncategorized?: boolean
}

/**
 * 游戏详情请求参数
 */
export interface GameDetailParams extends LocaleRequestParams {
  /** 游戏ID */
  id: number
}

/**
 * 游戏详情响应（增强版）
 */
export interface GameDetailResponse {
  /** 游戏基本信息 */
  game: {
    id: number
    name: string
    subtitle?: string
    description: string
    iconUrl: string
    coverUrl?: string
    screenshots?: string[]
    videoUrl?: string
    downloadUrl?: string
    androidUrl?: string
    iosUrl?: string
    downloadCount: number
    rating?: number
    categoryId?: number
    categoryName?: string
    categoryIcon?: string
    version?: string
    size?: string
    launchTime?: string
    developer?: string
    publisher?: string
    tags?: string[]
    features?: string
    deviceSupport?: string
    isNew?: boolean
    isHot?: boolean
    isRecommend?: boolean
    status?: string
  }
  /** 游戏专属礼包 */
  gifts: GiftListItem[]
  /** 关联攻略 */
  guides: ArticleListItem[]
  /** 同品类推荐游戏 */
  relatedGames: GameListItem[]
}

/**
 * 品类详情请求参数
 */
export interface CategoryDetailParams extends LocaleRequestParams {
  /** 品类slug */
  slug: string
  /** 来源（子站域名） */
  source?: string
}

/**
 * 站点配置请求参数
 */
export interface SiteConfigParams extends LocaleRequestParams {
  // 站点配置只需要 locale 和 siteId
}

/**
 * 统计数据请求参数
 */
export interface StatisticsParams extends LocaleRequestParams {
  // 统计数据只需要 siteId
}

/**
 * 获取当前语言环境
 * 优先级：1. 参数传入 > 2. URL路径 > 3. 浏览器语言 > 4. 默认语言
 */
export function getCurrentLocale(localeParam?: Locale): Locale {
  if (localeParam) return localeParam

  const configuredLocales = getConfiguredLocales()
  const defaultLocale = getConfiguredDefaultLocale(configuredLocales)
  
  // 服务端渲染时没有 window 对象
  if (typeof window === 'undefined') {
    return defaultLocale
  }
  
  // 从URL路径获取
  const pathname = window.location.pathname
  for (const locale of configuredLocales) {
    if (locale === defaultLocale) continue
    if (pathname.startsWith(`/${locale}`)) {
      return locale
    }
  }
  
  // 从浏览器语言获取
  const browserLang = navigator.language.toLowerCase()
  if (configuredLocales.includes('zh-TW') && (browserLang.startsWith('zh-tw') || browserLang.startsWith('zh-hk'))) {
    return 'zh-TW'
  }
  if (configuredLocales.includes('zh-CN') && browserLang.startsWith('zh')) {
    return 'zh-CN'
  }
  return defaultLocale
}

/**
 * 为API请求参数添加locale
 */
export function withLocale<T extends LocaleRequestParams>(
  params: T,
  locale?: Locale
): T {
  return {
    ...params,
    locale: params.locale || locale || getCurrentLocale(),
  }
}

/**
 * 格式化locale为后端接受的格式
 * zh-CN -> zh-CN
 * zh-TW -> zh-TW
 */
export function formatLocaleForBackend(locale: Locale): string {
  const localeMap: Record<Locale, string> = {
    'zh-CN': 'zh-CN',
    'zh-TW': 'zh-TW',
  }
  return localeMap[locale] || locale
}
