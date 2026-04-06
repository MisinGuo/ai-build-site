import { cache } from 'react'
import { Metadata } from 'next'
import { notFound } from 'next/navigation'
import ApiClient from '@/lib/api'
import { ArticleLayout } from '@/components/content/ArticleLayout'
import { JsonLd, buildArticleSchema, buildBreadcrumbSchema } from '@/components/common/JsonLd'
import { getModuleConfig } from '@/config'
import { siteConfig } from '@/config/site/site'
import { isValidLocale, supportedLocales, defaultLocale, type Locale } from '@/config/site/locales'

// 按需渲染，禁用 Next.js 缓存，由 Cloudflare Workers 控制
export const dynamic = 'auto'
export const revalidate = 0

const moduleConfig = getModuleConfig('guides')

interface GuideDetail {
  id: number
  title: string
  description?: string
  content: string
  coverImage?: string
  categoryName?: string
  categoryId?: number
  author?: string
  createTime: string
  updateTime?: string
  viewCount?: number
  likeCount?: number
  tags?: string[]
  section?: string
  locale?: string
}

function normalizeLocale(value?: string): string | undefined {
  return value ? value.replace('_', '-') : undefined
}

// 获取攻略详情（cache() 确保同一次渲染内相同参数只发一次 HTTP 请求）
const getGuideDetail = cache(async (id: string, locale: Locale): Promise<GuideDetail | null> => {
  try {
    console.log(`[guides] 请求攻略详情 id=${id} locale=${locale}`)
    const response = await ApiClient.getArticleDetail(parseInt(id, 10), locale)
    
    if (response.code === 200 && response.data) {
      return response.data
    }
    console.warn(`[guides] 攻略 ${id} 返回异常 code=${response.code}`)
  } catch (error) {
    console.error(`获取攻略 ${id} 失败:`, error)
  }
  
  return null
})

const getAvailableGuideLocales = cache(async (id: string): Promise<Locale[]> => {
  try {
    const response = await ApiClient.getArticleLocales(parseInt(id, 10))
    
    if (response.code === 200 && Array.isArray(response.data)) {
      const available = response.data.filter((l: string) => 
        supportedLocales.includes(l as any)
      ) as Locale[]
      return available.length > 0 ? available : [defaultLocale]
    }
  } catch (error) {
    console.warn(`获取攻略 ${id} 语言版本失败:`, error)
  }
  return [defaultLocale]
})

export async function generateMetadata({ 
  params 
}: { 
  params: Promise<{ locale: string; slug: string }> 
}): Promise<Metadata> {
  const { locale: localeParam, slug } = await params
  
  if (!isValidLocale(localeParam)) {
    return { title: 'Not Found' }
  }
  
  const locale = localeParam as Locale
  const guide = await getGuideDetail(slug, locale)
  
  if (!guide) {
    return {
      title: '攻略未找到'
    }
  }

  const guidePath = locale === defaultLocale 
    ? `/guides/${slug}` 
    : `/${locale}/guides/${slug}`
  const guideUrl = `${siteConfig.hostname}${guidePath}`
  const description = guide.description || guide.title
  const imageUrl = guide.coverImage || '/default-og-image.jpg'
  const titleSuffix = locale === 'zh-TW' ? ' | 遊戲攻略' : ' | 游戏攻略'
  const siteNameStr = locale === 'zh-TW' ? '遊戲攻略' : '游戏攻略'
  const availableGuideLocales = await getAvailableGuideLocales(slug)

  return {
    title: `${guide.title}${titleSuffix}`,
    description,
    keywords: guide.tags?.join(', '),
    authors: guide.author ? [{ name: guide.author }] : undefined,
    openGraph: {
      title: guide.title,
      description,
      url: guideUrl,
      siteName: siteNameStr,
      locale,
      type: 'article',
      publishedTime: guide.createTime,
      modifiedTime: guide.updateTime,
      authors: guide.author ? [guide.author] : undefined,
      tags: guide.tags,
      images: [
        {
          url: imageUrl,
          width: 1200,
          height: 630,
          alt: guide.title,
        },
      ],
    },
    twitter: {
      card: 'summary_large_image',
      title: guide.title,
      description,
      images: [imageUrl],
    },
    alternates: {
      canonical: guideUrl,
      ...(availableGuideLocales.length > 1 ? (() => {
        const langs: Record<string, string> = { 'x-default': `${siteConfig.hostname}/guides/${slug}` }
        for (const l of availableGuideLocales) {
          const prefix = l === defaultLocale ? '' : `/${l}`
          langs[l] = `${siteConfig.hostname}${prefix}/guides/${slug}`
        }
        return { languages: langs }
      })() : {}),
    },
  }
}

export default async function GuideDetailPage({ 
  params 
}: { 
  params: Promise<{ locale: string; slug: string }> 
}) {
  const { locale: localeParam, slug } = await params
  
  if (!isValidLocale(localeParam)) {
    notFound()
  }
  
  const locale = localeParam as Locale
  const guide = await getGuideDetail(slug, locale)
  const availableLocales = await getAvailableGuideLocales(slug)

  if (!guide) {
    notFound()
  }

  const guideUrl = `${siteConfig.hostname}${locale === defaultLocale ? '' : `/${locale}`}/guides/${slug}`
  // 仅当 categoryName 包含非 ASCII 字符（中文）时才显示为面包屑分类，过滤掉 "guide-section" 这类原始英文 slug
  const displayCategory = guide.categoryName && /[^\u0000-\u007F]/.test(guide.categoryName)
    ? guide.categoryName
    : undefined

  const articleSchema = buildArticleSchema({
    title: guide.title,
    description: guide.description || guide.title,
    url: guideUrl,
    imageUrl: guide.coverImage,
    publishedTime: guide.createTime,
    modifiedTime: guide.updateTime,
    authorName: guide.author,
    siteName: siteConfig.name,
    siteUrl: siteConfig.hostname,
  })

  const breadcrumbSchema = buildBreadcrumbSchema([
    { name: locale === 'zh-TW' ? '首頁' : '首页', url: siteConfig.hostname },
    { name: locale === 'zh-TW' ? '遠戲攻略' : '游戏攻略', url: `${siteConfig.hostname}${locale === defaultLocale ? '' : `/${locale}`}/guides` },
    { name: guide.title, url: guideUrl },
  ])

  return (
    <>
      <JsonLd data={[articleSchema, breadcrumbSchema]} />
      <ArticleLayout
        config={moduleConfig}
        frontmatter={{
          title: guide.title,
          description: guide.description,
          date: guide.createTime,
          author: guide.author,
          tags: guide.tags,
          category: displayCategory,
        }}
        content={guide.content}
        readingTime={Math.ceil(guide.content.length / 500)}
        toc={[]}
        availableLocales={availableLocales}
      />
    </>
  )
}
