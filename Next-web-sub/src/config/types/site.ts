/**
 * site-config.ts 的 TypeScript 类型定义
 *
 * 此文件由后台系统写入仓库，前端通过 import 直接读取，构建时打包。
 * 不要在业务代码中直接 import 此文件，统一通过 @/lib/site-config 获取。
 */

export interface SiteConfigBranding {
  /** 站点名称，简体中文，作为所有语言的默认值 */
  siteName: string
  /** 各语言站点名称覆盖，key 为语言代码（如 'zh-TW'），不填则使用 siteName */
  siteNameI18n?: Record<string, string>
  /** 站点标语，显示在首屏副标题 */
  tagline: string
  /** 网站描述，用于 SEO meta description */
  description: string
  /** 版权声明，显示在页脚底部 */
  copyright: string
  /** Logo 图片路径，相对于 public 目录，如 '/logo.svg' */
  logo: string
  /** Favicon 路径，相对于 public 目录，如 '/favicon.ico' */
  favicon: string
  /** Open Graph 分享图片路径，相对于 public 目录，如 '/og-image.jpg' */
  ogImage: string
  /** 作者 / 站点所有者信息 */
  author: {
    /** 作者名称 */
    name: string
    /** 作者主页 URL */
    url: string
    /** 联系邮箱（可选） */
    email?: string
  }
  /** 社交媒体链接，key 为平台名（如 'github'、'twitter'），不填则隐藏对应按钮 */
  social?: Record<string, string>
  /** SEO 关键词列表 */
  keywords?: string[]
  /** 页脚描述第一行，简体中文 */
  footerDescription?: string
  /** 页脚描述第二行，简体中文 */
  footerDescription2?: string
  /** 各语言页脚描述覆盖，key 为语言代码（如 'zh-TW'） */
  footerDescriptionI18n?: Record<string, { footerDescription?: string; footerDescription2?: string }>
}

export interface SiteConfigSite {
  /** 当前站点完整域名，含协议，如 'https://example.com'。可被环境变量 NEXT_PUBLIC_SITE_HOSTNAME 覆盖 */
  hostname: string
  /** 主站地址，用于子站顶部"返回主站"按钮。可被 NEXT_PUBLIC_MAIN_SITE_URL 覆盖 */
  mainSiteUrl: string
  /** 下载跳转域名。可被 NEXT_PUBLIC_JUMP_DOMAIN 覆盖 */
  jumpDomain: string
  /** 默认语言代码，如 'zh-CN' */
  defaultLocale: string
  /** 站点支持的语言代码列表，如 ['zh-CN', 'zh-TW'] */
  supportedLocales: string[]
}

export interface SiteConfigTheme {
  /** 主色调，用于按钮、链接、强调元素，十六进制颜色值，如 '#8b5cf6' */
  primaryColor: string
  /** 辅色调，十六进制颜色值 */
  secondaryColor: string
  /** 强调色，用于提示、徽标等，十六进制颜色值 */
  accentColor: string
  /** 预设色彩方案标识，供后台选择器使用。custom 表示完全自定义 */
  colorScheme: 'purple' | 'blue' | 'green' | 'orange' | 'custom'
  /** 是否默认启用暗色模式 */
  darkMode: boolean
  /** 全局圆角大小，CSS 像素值，如 '8px' */
  borderRadius: string
}

export interface SiteConfigNavItem {
  /** 页面路径，如 '/guides' */
  path: string
  /** i18n 翻译 key，对应 locales.ts 中的键名 */
  i18nKey: string
  /** 导航文字默认值（i18n 未命中时显示） */
  label: string
  /**
   * 是否在导航栏显示此项，同时控制对应静态页是否可被访问。
   * - true：显示在导航栏
   * - false：隐藏（页面本身仍存在，只是不显示入口）
   */
  enabled: boolean
  /**
   * 是否将此页面及其动态内容加入 sitemap.xml（必填）
   *
   * 规则：
   * - 导航项不存在于 header 列表搜不到）→ 一定不会加入 sitemap
   * - enabled: false → 不加入 sitemap（无论 inSitemap 是什么）
   * - enabled: true 且 inSitemap: true → 加入静态页 sitemap
   * - enabled: true 且 inSitemap: false → 页面可访问，但不加入 sitemap（不希望被搜索引擎收录）
   *
   * 示例：点击刻丢店页面 enabled: true, inSitemap: false
   */
  inSitemap: boolean
}

export interface SiteConfigNavigation {
  /** 顶部导航栏菜单项列表，顺序即显示顺序 */
  header: SiteConfigNavItem[]
}

export interface SiteConfigFeatures {
  /** 是否启用全站搜索功能 */
  search?: boolean
  /** 是否显示暗色模式切换按钮 */
  darkMode?: boolean
  /** 是否启用评论功能 */
  comments?: boolean
  /** 是否启用数据分析（Google Analytics / Clarity） */
  analytics?: boolean
  /** 是否启用多语言切换 */
  i18n?: boolean
  /** 是否启用 RSS 订阅 */
  rss?: boolean
  [key: string]: boolean | undefined
}

export interface SiteConfigIntegrations {
  /** Google Analytics 追踪 ID，格式如 'G-XXXXXXXX'，留空则不注入 */
  googleAnalyticsId?: string
  /** Microsoft Clarity 项目 ID，留空则不注入 */
  clarityId?: string
  /** 注入 <head> 的自定义脚本字符串（如第三方统计代码） */
  customHeadScript?: string
  /** 注入 <body> 底部的自定义脚本字符串 */
  customBodyScript?: string
}

/** site-config.ts 完整结构 */
export interface SiteConfigFile {
  /** 品牌与展示信息：站点名、描述、Logo、社交链接等 */
  branding: SiteConfigBranding
  /** 站点地址配置：域名、主站URL、语言等 */
  site: SiteConfigSite
  /** 主题外观：颜色、圆角、暗色模式 */
  theme: SiteConfigTheme
  /** 导航配置：顶部菜单项 */
  navigation: SiteConfigNavigation
  /** 功能开关：搜索、评论、分析等 */
  features: SiteConfigFeatures
  /** 第三方集成：Analytics、Clarity、自定义脚本 */
  integrations: SiteConfigIntegrations
}
