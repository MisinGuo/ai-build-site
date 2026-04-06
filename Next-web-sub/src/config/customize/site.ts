/**
 * 站点配置文件
 *
 * 🔧 修改此文件即可定制站点，无需改任何组件代码。
 *    开发模式下保存后浏览器自动热更新。
 *
 * 📦 后台系统会自动覆盖此文件并推送到仓库触发重新构建。
 */

import type { SiteConfigFile } from '@/config/types/site'

const siteConfigData: SiteConfigFile = {
  // ──────────────────────────────────────
  // 品牌信息
  // ──────────────────────────────────────
  branding: {
    /** 站点名称（简体中文，作为默认值） */
    siteName: '卡牌游戏攻略站',

    /** 各语言站点名称（可选，不填则使用上方 siteName） */
    siteNameI18n: {
      'zh-TW': '卡牌遊戲攻略站',
    },

    /** 站点标语（首屏副标题） */
    tagline: '聚焦卡牌手游的阵容搭配、开荒思路、礼包福利与版本强度分析',

    /** 网站描述（用于 SEO） */
    description: '卡牌游戏攻略站 - 聚焦卡牌手游的阵容搭配、开荒思路、礼包福利与版本强度分析。',

    /** 版权声明 */
    copyright: 'Copyright © 2025-至今 卡牌游戏攻略站',

    /** Logo 路径（相对 public 目录） */
    logo: '/logo.png',

    /** Favicon 路径 */
    favicon: '/favicon.ico',

    /** Open Graph 分享图片 */
    ogImage: '/logo.png',

    /** 作者信息 */
    author: {
      name: '卡牌游戏攻略站',
      url: 'https://kapai-web.94kj.cn',
      email: 'contact@example.com',
    },

    /** 社交媒体链接（key 随意，未填则隐藏对应按钮） */
    social: {
      // github: 'https://github.com/your-org',
      // twitter: 'https://twitter.com/yourhandle',
      discord: 'https://discord.gg/gamebox',
      telegram: 'https://t.me/gamebox',
    },

    /** SEO 关键词 */
    keywords: ['卡牌游戏攻略', '卡牌手游阵容', '开荒攻略', '版本强度榜', '礼包福利', '游戏攻略站'],

    // ── 页脚描述文案 ──────────────────────
    /** 页脚描述第一行（简体中文） */
    footerDescription: '专注卡牌游戏攻略与实战阵容分析。',

    /** 页脚描述第二行（简体中文） */
    footerDescription2: '汇集版本资讯、养成思路与礼包情报，帮助你高效提升战力。',

    /** 各语言页脚描述覆盖（可选） */
    footerDescriptionI18n: {
      'zh-TW': {
        footerDescription: '專注卡牌遊戲攻略與實戰陣容分析。',
        footerDescription2: '彙集版本資訊、養成思路與禮包情報，幫助你高效提升戰力。',
      },
    },
  },

  // ──────────────────────────────────────
  // 站点地址
  // ──────────────────────────────────────
  site: {
    /** 当前站点域名（可被 NEXT_PUBLIC_SITE_HOSTNAME 环境变量覆盖） */
    hostname: 'https://kapai-web.94kj.cn',

    /** 主站地址（用于子站顶部"返回主站"按钮） */
    mainSiteUrl: 'https://www.5awyx.com',

    /** 下载跳转域名 */
    jumpDomain: 'https://www.5awyx.com',

    /** 默认语言 */
    defaultLocale: 'zh-CN',

    /** 支持的语言列表 */
    supportedLocales: ['zh-CN', 'zh-TW'],
  },

  // ──────────────────────────────────────
  // 主题外观
  // ──────────────────────────────────────
  theme: {
    /** 主色调（按钮、链接、强调元素） */
    primaryColor: '#8b5cf6',

    /** 辅色调 */
    secondaryColor: '#ec4899',

    /** 强调色（提示、徽标等） */
    accentColor: '#f59e0b',

    /**
     * 预设色彩方案（供后台选择器使用）
     * purple | blue | green | orange | custom
     */
    colorScheme: 'purple',

    /** 是否启用暗色模式 */
    darkMode: true,

    /** 圆角大小：4px | 8px | 12px | 16px */
    borderRadius: '8px',
  },

  // ──────────────────────────────────────
  // 导航配置
  // ──────────────────────────────────────
  navigation: {
    header: [
      { path: '/', i18nKey: 'home', label: '首页', enabled: true, inSitemap: true },
      { path: '/boxes', i18nKey: 'boxes', label: '游戏盒子', enabled: true, inSitemap: false },
      { path: '/games', i18nKey: 'games', label: '游戏', enabled: true, inSitemap: false },
      { path: '/guides', i18nKey: 'guides', label: '攻略', enabled: true, inSitemap: true },
      { path: '/reviews', i18nKey: 'reviews', label: '评测', enabled: true, inSitemap: true },
      // { path: '/gifts', i18nKey: 'gifts', label: '礼包', enabled: false, inSitemap: false },
    ],
  },

  // ──────────────────────────────────────
  // 功能开关
  // ──────────────────────────────────────
  features: {
    search: true,
    darkMode: true,
    comments: false,
    analytics: true,
    i18n: false,
    rss: true,
  },

  // ──────────────────────────────────────
  // 第三方集成
  // ──────────────────────────────────────
  integrations: {
    /** Google Analytics ID，如 G-XXXXXXXX */
    googleAnalyticsId: '',

    /** Microsoft Clarity ID */
    clarityId: '',

    /** 注入 <head> 的自定义脚本（如统计代码） */
    customHeadScript: '',

    /** 注入 <body> 底部的自定义脚本 */
    customBodyScript: '',
  },
}

export default siteConfigData
