import { cache } from 'react'
import { Metadata } from 'next'
import { notFound } from 'next/navigation'
import ApiClient from '@/lib/api'
import { ArticleLayout } from '@/components/content/ArticleLayout'
import { JsonLd, buildArticleSchema, buildBreadcrumbSchema } from '@/components/common/JsonLd'
import { getModuleConfig } from '@/config'
import { siteConfig } from '@/config/site/site'
import { isValidLocale, supportedLocales, defaultLocale, type Locale } from '@/config/site/locales'

export const dynamic = 'auto'
export const dynamicParams = true
export const revalidate = 0

export async function generateStaticParams() {
  return []
}

const moduleConfig = getModuleConfig('reviews')

interface ReviewDetail {
  id: number
  title: string
  description?: string
  content: string
  coverImage?: string
  categoryName?: string
  author?: string
  createTime: string
  updateTime?: string
  viewCount?: number
  tags?: string[]
  section?: string
  locale?: string
}

const getReviewDetail = cache(async (id: string, locale: Locale): Promise<ReviewDetail | null> => {
  try {
    const response = await ApiClient.getArticleDetail(parseInt(id, 10), locale)

    if (response.code === 200 && response.data) {
      return response.data
    }
  } catch (error) {
    console.error(`获取评测 ${id} 失败:`, error)
  }

  return null
})

const getAvailableReviewLocales = cache(async (id: string): Promise<Locale[]> => {
  try {
    const response = await ApiClient.getArticleLocales(parseInt(id, 10))
    if (response.code === 200 && Array.isArray(response.data)) {
      const available = response.data.filter((l: string) => supportedLocales.includes(l as any)) as Locale[]
      return available.length > 0 ? available : [defaultLocale]
    }
  } catch (error) {
    console.warn(`获取评测 ${id} 语言版本失败:`, error)
  }
  return [defaultLocale]
})

export async function generateMetadata({
  params,
}: {
  params: Promise<{ locale: string; slug: string }>
}): Promise<Metadata> {
  const { locale: localeParam, slug } = await params

  if (!isValidLocale(localeParam)) {
    return { title: 'Not Found' }
  }

  const locale = localeParam as Locale
  const review = await getReviewDetail(slug, locale)

  if (!review) {
    return { title: locale === 'zh-TW' ? '評測未找到' : '评测未找到' }
  }

  const reviewPath = locale === defaultLocale ? `/reviews/${slug}` : `/${locale}/reviews/${slug}`
  const reviewUrl = `${siteConfig.hostname}${reviewPath}`
  const description = review.description || review.title
  const imageUrl = review.coverImage || '/default-og-image.jpg'
  const titleSuffix = locale === 'zh-TW' ? ' | 遊戲橫評' : ' | 游戏横评'
  const siteNameStr = locale === 'zh-TW' ? '遊戲橫評' : '游戏横评'
  const availableReviewLocales = await getAvailableReviewLocales(slug)

  return {
    title: `${review.title}${titleSuffix}`,
    description,
    keywords: review.tags?.join(', '),
    authors: review.author ? [{ name: review.author }] : undefined,
    openGraph: {
      title: review.title,
      description,
      url: reviewUrl,
      siteName: siteNameStr,
      locale,
      type: 'article',
      publishedTime: review.createTime,
      modifiedTime: review.updateTime,
      authors: review.author ? [review.author] : undefined,
      tags: review.tags,
      images: [{ url: imageUrl, width: 1200, height: 630, alt: review.title }],
    },
    twitter: {
      card: 'summary_large_image',
      title: review.title,
      description,
      images: [imageUrl],
    },
    alternates: {
      canonical: reviewUrl,
      ...(availableReviewLocales.length > 1 ? (() => {
        const langs: Record<string, string> = { 'x-default': `${siteConfig.hostname}/reviews/${slug}` }
        for (const l of availableReviewLocales) {
          const prefix = l === defaultLocale ? '' : `/${l}`
          langs[l] = `${siteConfig.hostname}${prefix}/reviews/${slug}`
        }
        return { languages: langs }
      })() : {}),
    },
  }
}

export default async function ReviewDetailPage({
  params,
}: {
  params: Promise<{ locale: string; slug: string }>
}) {
  const { locale: localeParam, slug } = await params

  if (!isValidLocale(localeParam)) {
    notFound()
  }

  const locale = localeParam as Locale
  const review = await getReviewDetail(slug, locale)
  const availableLocales = await getAvailableReviewLocales(slug)

  if (!review) {
    notFound()
  }

  const reviewUrlFull = `${siteConfig.hostname}${locale === defaultLocale ? '' : `/${locale}`}/reviews/${slug}`
  // 仅当 categoryName 包含非 ASCII 字符（中文）时才显示为面包屑分类
  const displayCategory = review.categoryName && /[^\u0000-\u007F]/.test(review.categoryName)
    ? review.categoryName
    : undefined

  const articleSchema = buildArticleSchema({
    title: review.title,
    description: review.description || review.title,
    url: reviewUrlFull,
    imageUrl: review.coverImage,
    publishedTime: review.createTime,
    modifiedTime: review.updateTime,
    authorName: review.author,
    siteName: siteConfig.name,
    siteUrl: siteConfig.hostname,
  })

  const breadcrumbSchema = buildBreadcrumbSchema([
    { name: locale === 'zh-TW' ? '首頁' : '首页', url: siteConfig.hostname },
    { name: locale === 'zh-TW' ? '遠戲橫評' : '游戏横评', url: `${siteConfig.hostname}${locale === defaultLocale ? '' : `/${locale}`}/reviews` },
    { name: review.title, url: reviewUrlFull },
  ])

  return (
    <>
      <JsonLd data={[articleSchema, breadcrumbSchema]} />
      <ArticleLayout
        config={moduleConfig}
        frontmatter={{
          title: review.title,
          description: review.description,
          date: review.createTime,
          author: review.author,
          tags: review.tags,
          category: displayCategory,
        }}
        content={review.content}
        readingTime={Math.ceil(review.content.length / 500)}
        toc={[]}
        availableLocales={availableLocales}
      />
    </>
  )
}
