import { Metadata } from 'next'
import Link from 'next/link'
import { ChevronRight, Eye, BookOpen } from 'lucide-react'
import { Card, CardHeader, CardTitle, CardDescription } from '@/components/ui/card'
import { isValidLocale, supportedLocales, defaultLocale, type Locale } from '@/config/site/locales'
import { generateListMetadata } from '@/lib/metadata'
import { guideSectionSlugs } from '@/config/customize/pages/guides'
import { siteConfig } from '@/config/site/site'
import ImageWithFallback from '../ImageWithFallback'
import ApiClient from '@/lib/api'
import { JsonLd, buildCollectionPageSchema } from '@/components/common/JsonLd'

export const dynamic = 'auto'
export const revalidate = 0

export async function generateStaticParams() {
  return supportedLocales
    .filter(locale => locale !== defaultLocale)
    .map(locale => ({ locale }))
}

export async function generateMetadata({
  params,
}: {
  params: Promise<{ locale: string }>
}): Promise<Metadata> {
  const { locale: localeParam } = await params
  if (!isValidLocale(localeParam)) return { title: '游戏攻略' }

  const locale = localeParam as Locale
  return generateListMetadata(locale, 'strategy', {
    title: locale === 'zh-TW' ? '遊戲攻略 - 手遊攻略大全' : '游戏攻略 - 手游攻略大全',
    description:
      locale === 'zh-TW'
        ? '最全的手遊攻略，卡牌陣容、開荒指南、版本強度分析'
        : '最全的手游攻略，卡牌阵容、开荒指南、版本强度分析',
  }, locale === defaultLocale ? `${siteConfig.hostname}/guides` : `${siteConfig.hostname}/${locale}/guides`, '/guides')
}

interface GuideItem {
  masterArticleId: number
  title: string
  description?: string
  coverImage?: string
  coverUrl?: string
  image?: string
  categoryName?: string
  section?: string
  viewCount?: number
  createTime?: string
  tags?: string[]
  isHot?: boolean
}

async function getGuideArticles(locale: Locale): Promise<GuideItem[]> {
  const articleMap = new Map<number, GuideItem>()

  console.log(`[guides] 开始请求 sections=${JSON.stringify(guideSectionSlugs)} locale=${locale}`)

  for (const section of guideSectionSlugs) {
    try {
      console.log(`[guides] 请求 section=${section}`)
      const response = await ApiClient.getArticles({
        pageNum: 1,
        pageSize: 200,
        locale,
        section,
      })

      console.log(`[guides] section=${section} 响应 code=${response.code} rows=${response.rows?.length ?? 0}`)

      if (response.code === 200 && response.rows) {
        for (const article of response.rows as GuideItem[]) {
          if (article?.masterArticleId) articleMap.set(article.masterArticleId, article)
        }
      }
    } catch (error) {
      console.error(`[guides] section=${section} 请求失败:`, error)
    }
  }

  const articles = Array.from(articleMap.values())
  console.log(`[guides] 共加载 ${articles.length} 篇`)
  return articles
}

function GuideCard({
  guide,
  locale,
  featured = false,
}: {
  guide: GuideItem
  locale: Locale
  featured?: boolean
}) {
  const basePath = locale === defaultLocale ? '' : `/${locale}`
  const coverSrc = guide.coverImage || guide.image || guide.coverUrl

  if (featured) {
    return (
      <Link href={`${basePath}/guides/${guide.masterArticleId}`}>
        <Card className="h-full hover:shadow-lg transition-all hover:border-amber-500/50 cursor-pointer group overflow-hidden">
          <div className="relative h-56 overflow-hidden">
            {coverSrc ? (
              <ImageWithFallback
                src={coverSrc}
                alt={guide.title}
                className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
              />
            ) : (
              <div className="w-full h-full bg-slate-800 flex items-center justify-center text-4xl">📝</div>
            )}
            <div className="absolute inset-0" style={{ background: 'linear-gradient(to top, #13131f 0%, transparent 50%)' }} />
            {guide.isHot && (
              <span
                className="absolute top-3 left-3 px-2 py-1 rounded-lg text-xs font-bold"
                style={{ background: 'rgba(239,68,68,0.9)', color: '#fff' }}
              >
                HOT
              </span>
            )}
            {guide.categoryName && (
              <span
                className="absolute top-3 right-3 px-2 py-1 rounded-lg text-xs"
                style={{ background: 'rgba(139,92,246,0.85)', color: '#fff' }}
              >
                {guide.categoryName}
              </span>
            )}
          </div>
          <CardHeader className="p-5">
            <div className="flex items-center gap-3 mb-2 text-xs" style={{ color: '#606080' }}>
              {guide.viewCount && (
                <span className="flex items-center gap-1">
                  <Eye className="w-3 h-3" />
                  {(guide.viewCount / 1000).toFixed(1)}K
                </span>
              )}
              {guide.createTime && <span>{guide.createTime.substring(0, 10)}</span>}
            </div>
            <CardTitle className="text-base group-hover:text-amber-500 transition-colors line-clamp-2">
              {guide.title}
            </CardTitle>
            {guide.description && (
              <CardDescription className="line-clamp-2 text-xs mt-2">{guide.description}</CardDescription>
            )}
            {guide.tags && guide.tags.length > 0 && (
              <div className="flex flex-wrap gap-1.5 mt-3">
                {guide.tags.slice(0, 3).map((tag) => (
                  <span
                    key={tag}
                    className="px-2 py-0.5 rounded-full text-xs"
                    style={{ background: 'rgba(139,92,246,0.12)', color: '#a78bfa' }}
                  >
                    #{tag}
                  </span>
                ))}
              </div>
            )}
            <div className="pt-3">
              <span className="text-sm font-medium" style={{ color: '#a78bfa' }}>
                {locale === 'zh-TW' ? '查看詳情 →' : '查看详情 →'}
              </span>
            </div>
          </CardHeader>
        </Card>
      </Link>
    )
  }

  return (
    <Link href={`${basePath}/guides/${guide.masterArticleId}`}>
      <div
        className="group flex gap-4 p-4 rounded-xl cursor-pointer transition-all hover:bg-white/5"
        style={{ background: '#13131f', border: '1px solid rgba(255,255,255,0.06)' }}
      >
        <div className="relative w-28 h-24 rounded-xl overflow-hidden shrink-0">
          {coverSrc ? (
            <ImageWithFallback
              src={coverSrc}
              alt={guide.title}
              className="w-full h-full object-cover transition-transform duration-300 group-hover:scale-105"
            />
          ) : (
            <div className="w-full h-full bg-slate-800 flex items-center justify-center text-2xl">📝</div>
          )}
        </div>
        <div className="flex-1 min-w-0">
          {guide.categoryName && (
            <span
              className="px-1.5 py-0.5 rounded text-xs mb-1 inline-block"
              style={{ background: 'rgba(139,92,246,0.15)', color: '#a78bfa' }}
            >
              {guide.categoryName}
            </span>
          )}
          <h4
            className="line-clamp-2"
            style={{ color: '#e8e8f0', fontWeight: 600, fontSize: '0.95rem', lineHeight: 1.4, marginBottom: '0.4rem' }}
          >
            {guide.title}
          </h4>
          <div className="flex items-center gap-4" style={{ color: '#505068', fontSize: '0.78rem' }}>
            {guide.viewCount && (
              <span className="flex items-center gap-1">
                <Eye className="w-3 h-3" />
                {(guide.viewCount / 1000).toFixed(1)}K
              </span>
            )}
            <span className="ml-auto flex items-center gap-0.5" style={{ color: '#a78bfa' }}>
              {locale === 'zh-TW' ? '查看詳情' : '查看详情'}
              <ChevronRight className="w-3 h-3" />
            </span>
          </div>
        </div>
      </div>
    </Link>
  )
}

export default async function GuidesPage({
  params,
}: {
  params: Promise<{ locale: string }>
}) {
  const { locale: localeParam } = await params
  if (!isValidLocale(localeParam)) return null

  const locale = localeParam as Locale
  const articles = await getGuideArticles(locale)
  const basePath = locale === defaultLocale ? '' : `/${locale}`

  const groupedArticles = Array.from(
    articles.reduce((map, article) => {
      const groupName = article.categoryName?.trim() || (locale === 'zh-TW' ? '未分類' : '未分类')
      if (!map.has(groupName)) map.set(groupName, [])
      map.get(groupName)!.push(article)
      return map
    }, new Map<string, GuideItem[]>())
  )

  const jsonLd = buildCollectionPageSchema({
    name: locale === 'zh-TW' ? '遊戲攻略' : '游戏攻略',
    description: locale === 'zh-TW'
      ? '卡牌手遊最全攻略庫，從入門到精通，助你快速提升戰力'
      : '卡牌手游最全攻略库，从入门到精通，助你快速提升战力',
    url: locale === defaultLocale ? `${siteConfig.hostname}/guides` : `${siteConfig.hostname}/${locale}/guides`,
    items: articles.slice(0, 10).map(a => ({
      name: a.title,
      url: `${siteConfig.hostname}${basePath}/guides/${a.masterArticleId}`,
      image: a.coverImage || a.image || a.coverUrl,
    })),
  })

  return (
    <div className="min-h-screen bg-[#0a0a14] text-[#e8e8f0]">
      <JsonLd data={jsonLd} />
      {/* 面包屑 */}
      <div className="max-w-7xl mx-auto px-4 py-4">
        <div className="flex items-center gap-2 text-sm text-[#8080a0]">
          <Link href={basePath || '/'} className="hover:text-[#a78bfa] transition-colors">
            {locale === 'zh-TW' ? '首頁' : '首页'}
          </Link>
          <ChevronRight className="h-4 w-4" />
          <span className="text-[#e8e8f0]">{locale === 'zh-TW' ? '攻略' : '攻略'}</span>
        </div>
      </div>

      {/* Hero */}
      <section
        className="border-b border-white/10 relative overflow-hidden py-12"
        style={{ background: 'linear-gradient(135deg,#0d0d20,#1a0a2e)' }}
      >
        <div className="max-w-7xl mx-auto px-4">
          <h1 style={{ color: '#ffffff', fontWeight: 800, fontSize: '2.5rem', marginBottom: '0.5rem' }}>
            {locale === 'zh-TW' ? '遊戲攻略' : '游戏攻略'}
          </h1>
          <p style={{ color: '#8080a0', marginBottom: '1.5rem' }}>
            {locale === 'zh-TW'
              ? '卡牌手遊最全攻略庫，從入門到精通，助你快速提升戰力'
              : '卡牌手游最全攻略库，从入门到精通，助你快速提升战力'}
          </p>
          {groupedArticles.length > 0 && (
            <div className="flex flex-wrap gap-2">
              {groupedArticles.slice(0, 6).map(([groupName]) => (
                <span
                  key={groupName}
                  className="px-3 py-1 rounded-full text-sm"
                  style={{
                    background: 'rgba(139,92,246,0.15)',
                    color: '#a78bfa',
                    border: '1px solid rgba(139,92,246,0.3)',
                  }}
                >
                  {groupName}
                </span>
              ))}
            </div>
          )}
        </div>
      </section>

      {/* CTA bar */}
      <div className="max-w-7xl mx-auto px-4 pt-6">
        <div
          className="rounded-xl py-3 px-5"
          style={{ background: 'rgba(139,92,246,0.12)', border: '1px solid rgba(139,92,246,0.25)' }}
        >
          <p style={{ color: '#c4c4d4', fontSize: '0.9rem' }}>
            {locale === 'zh-TW'
              ? `收錄 ${articles.length}+ 優質攻略，每日更新實戰指南`
              : `收录 ${articles.length}+ 优质攻略，每日更新实战指南`}
          </p>
        </div>
      </div>

      {/* 分类分组列表 */}
      <section className="max-w-7xl mx-auto px-4 py-8 space-y-12">
        {groupedArticles.map(([groupName, items]) => (
          <div key={groupName}>
            <div className="flex items-center gap-3 mb-6">
              <div
                className="w-8 h-8 rounded-lg flex items-center justify-center"
                style={{ background: 'linear-gradient(135deg,#8b5cf6,#ec4899)' }}
              >
                <BookOpen className="w-4 h-4 text-white" />
              </div>
              <h2 style={{ color: '#e8e8f0', fontWeight: 700, fontSize: '1.25rem' }}>{groupName}</h2>
              <span
                className="px-2 py-0.5 rounded text-xs"
                style={{ background: 'rgba(139,92,246,0.15)', color: '#a78bfa' }}
              >
                {items.length}
              </span>
            </div>

            {/* 前3篇 featured 卡片 */}
            <div className="grid md:grid-cols-3 gap-6 mb-6">
              {items.slice(0, 3).map((guide) => (
                <GuideCard key={guide.masterArticleId} guide={guide} locale={locale} featured />
              ))}
            </div>

            {/* 剩余列表 */}
            {items.length > 3 && (
              <div className="flex flex-col gap-4">
                {items.slice(3).map((guide) => (
                  <GuideCard key={guide.masterArticleId} guide={guide} locale={locale} />
                ))}
              </div>
            )}
          </div>
        ))}

        {articles.length === 0 && (
          <div className="text-center py-20">
            <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>📝</div>
            <h3 style={{ fontSize: '1.5rem', fontWeight: 'bold', marginBottom: '0.5rem', color: '#e8e8f0' }}>
              {locale === 'zh-TW' ? '暫無攻略' : '暂无攻略'}
            </h3>
            <p style={{ color: '#8080a0' }}>{locale === 'zh-TW' ? '請稍後再試' : '请稍后再试'}</p>
          </div>
        )}
      </section>
    </div>
  )
}
