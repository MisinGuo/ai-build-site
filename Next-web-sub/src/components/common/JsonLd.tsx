/**
 * JSON-LD 结构化数据注入组件
 * 用于向页面 <head> 注入 Schema.org 结构化数据，提升 Google 富文本搜索结果展示
 */

interface JsonLdProps {
  data: Record<string, unknown> | Record<string, unknown>[]
}

export function JsonLd({ data }: JsonLdProps) {
  const json = Array.isArray(data)
    ? JSON.stringify(data)
    : JSON.stringify(data)

  return (
    <script
      type="application/ld+json"
      dangerouslySetInnerHTML={{ __html: json }}
    />
  )
}

// ─── Schema 构造工具函数 ───────────────────────────────────────────────────────

/** Article Schema（文章详情页） */
export function buildArticleSchema({
  title,
  description,
  url,
  imageUrl,
  publishedTime,
  modifiedTime,
  authorName,
  siteName,
  siteUrl,
}: {
  title: string
  description?: string
  url: string
  imageUrl?: string
  publishedTime?: string
  modifiedTime?: string
  authorName?: string
  siteName: string
  siteUrl: string
}) {
  return {
    '@context': 'https://schema.org',
    '@type': 'Article',
    headline: title,
    description,
    url,
    ...(imageUrl && { image: { '@type': 'ImageObject', url: imageUrl } }),
    datePublished: publishedTime,
    dateModified: modifiedTime || publishedTime,
    author: {
      '@type': 'Person',
      name: authorName || siteName,
    },
    publisher: {
      '@type': 'Organization',
      name: siteName,
      url: siteUrl,
    },
    mainEntityOfPage: {
      '@type': 'WebPage',
      '@id': url,
    },
  }
}

/** VideoGame / SoftwareApplication Schema（游戏详情页） */
export function buildGameSchema({
  name,
  description,
  url,
  imageUrl,
  rating,
  downloadCount,
  developer,
  publisher,
  categoryName,
  version,
  launchTime,
}: {
  name: string
  description?: string
  url: string
  imageUrl?: string
  rating?: number | null
  downloadCount?: number
  developer?: string | null
  publisher?: string | null
  categoryName?: string | null
  version?: string | null
  launchTime?: string | null
}) {
  return {
    '@context': 'https://schema.org',
    '@type': 'VideoGame',
    name,
    description,
    url,
    ...(imageUrl && { image: imageUrl }),
    ...(developer && { author: { '@type': 'Organization', name: developer } }),
    ...(publisher && { publisher: { '@type': 'Organization', name: publisher } }),
    ...(categoryName && { genre: categoryName }),
    ...(version && { softwareVersion: version }),
    ...(launchTime && { datePublished: launchTime }),
    ...(rating !== undefined && rating !== null && {
      aggregateRating: {
        '@type': 'AggregateRating',
        ratingValue: rating,
        ratingCount: downloadCount || 1,
        bestRating: 10,
        worstRating: 0,
      },
    }),
    applicationCategory: 'GameApplication',
    operatingSystem: 'Android, iOS',
  }
}

/** BreadcrumbList Schema（面包屑） */
export function buildBreadcrumbSchema(items: { name: string; url: string }[]) {
  return {
    '@context': 'https://schema.org',
    '@type': 'BreadcrumbList',
    itemListElement: items.map((item, index) => ({
      '@type': 'ListItem',
      position: index + 1,
      name: item.name,
      item: item.url,
    })),
  }
}

/** WebSite Schema（首页） */
export function buildWebSiteSchema({
  name,
  url,
  description,
}: {
  name: string
  url: string
  description?: string
}) {
  return {
    '@context': 'https://schema.org',
    '@type': 'WebSite',
    name,
    url,
    description,
    potentialAction: {
      '@type': 'SearchAction',
      target: {
        '@type': 'EntryPoint',
        urlTemplate: `${url}/search?q={search_term_string}`,
      },
      'query-input': 'required name=search_term_string',
    },
  }
}

/** CollectionPage + ItemList Schema（列表页） */
export function buildCollectionPageSchema({
  name,
  description,
  url,
  items = [],
}: {
  name: string
  description: string
  url: string
  items?: { name: string; url: string; image?: string }[]
}) {
  return {
    '@context': 'https://schema.org',
    '@type': 'CollectionPage',
    name,
    description,
    url,
    ...(items.length > 0 && {
      mainEntity: {
        '@type': 'ItemList',
        itemListElement: items.slice(0, 10).map((item, index) => ({
          '@type': 'ListItem',
          position: index + 1,
          name: item.name,
          url: item.url,
          ...(item.image && { image: item.image }),
        })),
      },
    }),
  }
}

/** SoftwareApplication Schema（游戏盒子详情页） */
export function buildBoxSchema({
  name,
  description,
  url,
  imageUrl,
  gameCount,
}: {
  name: string
  description?: string
  url: string
  imageUrl?: string
  gameCount?: number
}) {
  return {
    '@context': 'https://schema.org',
    '@type': 'SoftwareApplication',
    name,
    description,
    url,
    ...(imageUrl && { image: imageUrl }),
    applicationCategory: 'GameApplication',
    operatingSystem: 'Android',
    ...(gameCount !== undefined && gameCount > 0 && {
      numberOfItems: gameCount,
    }),
  }
}
