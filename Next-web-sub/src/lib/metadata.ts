/**
 * Metadata 生成辅助函数
 * 统一处理所有页面的 SEO 元数据
 */

import type { Metadata } from 'next'
import type { Locale } from '@/config/types'
import { metadataTemplates } from '@/config/customize/fallback-metadata'
import { supportedLocales, defaultLocale } from '@/config/site/locales'
import { siteConfig } from '@/config/site/site'
import ApiClient from './api'

/**
 * 截断 description 到指定最大长度，避免超出搜索引擎 160 字上限
 */
function truncateDescription(desc: string | undefined, maxLen: number = 160): string | undefined {
  if (!desc) return desc
  return desc.length <= maxLen ? desc : desc.slice(0, maxLen - 1) + '…'
}

/**
 * 根据页面基础路径构建 hreflang 多语言 alternates
 * @param pageBasePath 页面路径（不含语言前缀），如 '/'、'/games'、'/boxes'
 */
function buildAlternateLanguages(pageBasePath: string): Record<string, string> {
  const languages: Record<string, string> = {}
  const isHome = pageBasePath === '/'
  for (const l of supportedLocales) {
    const localePrefix = l === defaultLocale ? '' : `/${l}`
    languages[l] = isHome
      ? `${siteConfig.hostname}${localePrefix || '/'}`
      : `${siteConfig.hostname}${localePrefix}${pageBasePath}`
  }
  // x-default: 默认语言版本
  languages['x-default'] = isHome ? `${siteConfig.hostname}/` : `${siteConfig.hostname}${pageBasePath}`
  return languages
}

/**
 * 生成页面 metadata，优先使用后端网站配置，失败时使用降级配置
 */
export async function generatePageMetadata(
  locale: Locale,
  fallback: {
    title?: string
    description?: string
    keywords?: string
  } = {},
  customFields?: {
    titleTemplate?: (siteTitle: string) => string
    descriptionTemplate?: (siteDescription: string) => string
  },
  pageUrl?: string,
  pageBasePath?: string
): Promise<Metadata> {
  let title: string | undefined
  let description: string | undefined
  let keywords: string | undefined
  let siteName: string | undefined
  let ogImage: string | undefined

  try {
    const siteConfigResponse = await ApiClient.getSiteConfig({ locale })
    const siteConfig = siteConfigResponse.data

    if (siteConfig) {
      title = siteConfig.seoTitle || siteConfig.name
      description = siteConfig.seoDescription || siteConfig.description
      keywords = siteConfig.seoKeywords
      siteName = siteConfig.name

      if (customFields?.titleTemplate) title = customFields.titleTemplate(title || '')
      if (customFields?.descriptionTemplate) description = customFields.descriptionTemplate(description || '')
    }
  } catch (error) {
    console.error('获取网站配置失败，使用降级配置:', error)
  }

  title = title || fallback.title
  description = description || fallback.description
  keywords = keywords || fallback.keywords

  const meta: Metadata = { title, description: truncateDescription(description), keywords }

  if (pageUrl || siteName) {
    meta.openGraph = {
      title: title || '',
      description: truncateDescription(description) || '',
      ...(siteName && { siteName }),
      type: 'website',
      ...(pageUrl && { url: pageUrl }),
      images: [{ url: siteConfig.ogImage || '/og-image.jpg', width: 1200, height: 630 }],
    }
    if (pageUrl) {
      meta.alternates = {
        canonical: pageUrl,
        ...(pageBasePath && { languages: buildAlternateLanguages(pageBasePath) }),
      }
    }
  }

  return meta
}

/**
 * 为首页生成 metadata
 */
export async function generateHomeMetadata(locale: Locale, fallbackTitle: string, fallbackDescription: string, pageUrl?: string, pageBasePath?: string): Promise<Metadata> {
  return generatePageMetadata(locale, {
    title: fallbackTitle,
    description: fallbackDescription,
  }, undefined, pageUrl, pageBasePath)
}

/**
 * 为列表页生成 metadata（游戏列表、盒子列表、攻略列表等）
 */
export async function generateListMetadata(
  locale: Locale,
  pageType: 'games' | 'boxes' | 'strategy' | 'article',
  fallback: { title?: string; description?: string; keywords?: string },
  pageUrl?: string,
  pageBasePath?: string
): Promise<Metadata> {
  const template = metadataTemplates[locale][pageType] || metadataTemplates[locale]['strategy']

  return generatePageMetadata(locale, fallback, {
    titleTemplate: (siteTitle) => `${siteTitle}${template.titleSuffix}`,
    descriptionTemplate: (siteDesc) => `${template.descPrefix}${template.featuredContent} - ${siteDesc}`,
  }, pageUrl, pageBasePath)
}

/**
 * 为详情页生成 metadata
 */
export async function generateDetailMetadata(
  locale: Locale,
  itemTitle: string,
  itemDescription?: string,
  fallback?: { title: string; description: string }
): Promise<Metadata> {
  return generatePageMetadata(locale, fallback || {
    title: itemTitle,
    description: itemDescription || itemTitle,
  }, {
    titleTemplate: (siteTitle) => `${itemTitle} - ${siteTitle}`,
    descriptionTemplate: () => itemDescription || itemTitle,
  })
}
