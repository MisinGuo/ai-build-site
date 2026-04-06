import type { LocaleConfig, Locale } from '../types'
import siteConf from '@/config/customize/site'

// 重新导出类型，方便其他文件使用
export type { Locale, LocaleConfig } from '../types'

/**
 * 多语言配置
 * 默认语言（zh-CN）：无路由前缀，如 /special/
 * 其他语言：有路由前缀，如 /zh-TW/special/
 */

// 默认语言（主语言，无路由前缀）
const ALL_LOCALE_CODES: Locale[] = ['zh-CN', 'zh-TW']
const FALLBACK_SUPPORTED_LOCALES: Locale[] = ['zh-CN', 'zh-TW']
const FALLBACK_DEFAULT_LOCALE: Locale = 'zh-CN'

function parseSupportedLocalesFromEnv(): Locale[] {
  const raw = process.env.NEXT_PUBLIC_SUPPORTED_LOCALES?.trim()
  if (!raw) return FALLBACK_SUPPORTED_LOCALES

  const parsed = raw
    .split(',')
    .map((item) => item.trim() as Locale)
    .filter((item): item is Locale => ALL_LOCALE_CODES.includes(item))

  return parsed.length > 0 ? Array.from(new Set(parsed)) : FALLBACK_SUPPORTED_LOCALES
}

// 支持的所有语言（默认仅简繁体，可通过 NEXT_PUBLIC_SUPPORTED_LOCALES 扩展）
export const supportedLocales: Locale[] = parseSupportedLocalesFromEnv()

// 默认语言（可通过 NEXT_PUBLIC_DEFAULT_LOCALE 覆盖）
const envDefaultLocale = process.env.NEXT_PUBLIC_DEFAULT_LOCALE?.trim() as Locale | undefined
export const defaultLocale: Locale = envDefaultLocale && supportedLocales.includes(envDefaultLocale)
  ? envDefaultLocale
  : (supportedLocales[0] || FALLBACK_DEFAULT_LOCALE)

export const locales: Record<Locale, LocaleConfig> = {
  'zh-CN': {
    code: 'zh-CN',
    name: '简体中文',
    routePrefix: '',  // 默认语言，URL 无前缀
    translations: {
      home: '首页',
      boxes: '游戏盒子',
      games: '游戏',
      guides: '游戏攻略',
      reviews: '游戏评测',
      content: '内容',
      download: '下载',
      noResults: '没有找到相关结果',
      noArticles: '暂无攻略文章',
      stayTuned: '敬请期待更多精彩内容',
      defaultCategory: '游戏',
      siteName: siteConf.branding.siteName,
      footerDescription: siteConf.branding.footerDescription ?? '专注卡牌游戏攻略与实战阵容分析。',
      footerDescription2: siteConf.branding.footerDescription2 ?? '汇集版本资讯、养成思路与礼包情报，帮助你高效提升战力。',
      platformNavigation: '平台导航',
      followUs: '关注我们',
      subscribeNewsletter: '订阅最新游戏折扣情报',
      allRightsReserved: 'Platform. All rights reserved.',
      gameLibrary: '游戏库',
      gamesCount: '款游戏',
      gamesTitle: '游戏',
      noCategoryGames: '暂无游戏',
      noData: '暂无数据',
      viewDetail: '查看详情',
    },
  },
  'zh-TW': {
    code: 'zh-TW',
    name: '繁體中文',
    routePrefix: '/zh-TW',  // 非默认语言，有前缀
    translations: {
      home: '首頁',
      boxes: '遁戲盒子',
      games: '遁戲',
      guides: '遊戲攻略',
      reviews: '遊戲評測',
      content: '內容',
      download: '下載',
      noResults: '沒有找到相關結果',
      noArticles: '暫無攻略文章',
      stayTuned: '敬請期待更多精彩內容',
      defaultCategory: '遊戲',
      siteName: siteConf.branding.siteNameI18n?.['zh-TW'] ?? siteConf.branding.siteName,
      footerDescription: siteConf.branding.footerDescriptionI18n?.['zh-TW']?.footerDescription ?? siteConf.branding.footerDescription ?? '專注卡牌遊戲攻略與實戰陣容分析。',
      footerDescription2: siteConf.branding.footerDescriptionI18n?.['zh-TW']?.footerDescription2 ?? siteConf.branding.footerDescription2 ?? '彙集版本資訊、養成思路與禮包情報，幫助你高效提升戰力。',
      platformNavigation: '平台導航',
      followUs: '關注我們',
      subscribeNewsletter: '訂閱最新遊戲折扣情報',
      allRightsReserved: 'Platform. All rights reserved.',
      gameLibrary: '遊戲庫',
      gamesCount: '款遊戲',
      gamesTitle: '遊戲',
      noCategoryGames: '暫無遊戲',
      noData: '暫無數據',
      viewDetail: '查看詳情',
    },
  },
}

/** 获取语言配置 */
export function getLocale(code: Locale): LocaleConfig {
  return locales[code] || locales[defaultLocale]
}

/** 根据路径获取语言代码 */
export function getLocaleFromPath(path: string): Locale {
  // 检查路径是否以语言前缀开头
  for (const locale of supportedLocales) {
    if (locale === defaultLocale) continue
    if (path.startsWith(`/${locale}/`) || path === `/${locale}`) {
      return locale
    }
  }
  // 默认语言（无前缀）
  return defaultLocale
}

/** 检查是否为有效的语言代码 */
export function isValidLocale(locale: string): locale is Locale {
  return supportedLocales.includes(locale as Locale)
}

/** 服务器端翻译函数（用于 Server Components） */
export function getTranslation(key: string, locale: Locale = defaultLocale): string {
  const config = getLocale(locale)
  return (config.translations as any)[key] || key
}
