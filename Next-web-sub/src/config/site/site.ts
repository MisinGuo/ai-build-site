import type { SiteConfig } from '../types'
import { siteConfigFile } from '@/lib/site-config'

/**
 * 站点全局配置
 *
 * 🔧 修改方式：编辑 src/config/customize/site.ts，保存后热更新生效。
 *    环境变量（NEXT_PUBLIC_SITE_HOSTNAME / NEXT_PUBLIC_MAIN_SITE_URL /
 *    NEXT_PUBLIC_JUMP_DOMAIN）可在运行时覆盖对应字段。
 */
const { branding, site, features } = siteConfigFile

export const siteConfig: SiteConfig = {
  name: branding.siteName,
  description: branding.description,

  // 环境变量优先，其次读 site-config.json，最终兜底写死值
  hostname: process.env.NEXT_PUBLIC_SITE_HOSTNAME || site.hostname,
  mainSiteUrl: process.env.NEXT_PUBLIC_MAIN_SITE_URL || site.mainSiteUrl,
  jumpDomain: process.env.NEXT_PUBLIC_JUMP_DOMAIN || site.jumpDomain,

  logo: branding.logo,
  favicon: branding.favicon,
  ogImage: branding.ogImage,
  keywords: branding.keywords,
  copyright: branding.copyright,
  author: branding.author,
  social: branding.social,
  features,

  defaultLocale: site.defaultLocale,
  supportedLocales: site.supportedLocales,
}
