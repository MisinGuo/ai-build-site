import { Metadata } from 'next'
import Link from 'next/link'
import { ChevronRight, BarChart2, Calendar, TrendingUp } from 'lucide-react'
import { Badge } from '@/components/ui/badge'
import { Card, CardHeader, CardTitle, CardDescription } from '@/components/ui/card'
import ApiClient from '@/lib/api'
import { isValidLocale, supportedLocales, defaultLocale, getTranslation, type Locale } from '@/config/site/locales'
import { generateListMetadata } from '@/lib/metadata'
import { reviewSectionSlugs } from '@/config/customize/pages/reviews'
import { siteConfig } from '@/config/site/site'
import ImageWithFallback from '../ImageWithFallback'
import { JsonLd, buildCollectionPageSchema } from '@/components/common/JsonLd'

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

  if (!isValidLocale(localeParam)) {
    return { title: '游戏评测' }
  }

  const locale = localeParam as Locale

  return generateListMetadata(locale, 'strategy', {
    title:
      locale === 'zh-CN'
        ? '横评中心 - 游戏横评·盒子横评'
        : '横评中心 - 遂戏横评·盒子横评',
    description:
      locale === 'zh-CN'
        ? '同品类游戏数据横评，多平台盒子折扣横评，帮你选出最値得玩、最划算的'
        : '同品類遂戏數據横评，多平台盒子折扣横评，幫你選出最値得玩、最劃算的',
    keywords:
      locale === 'zh-CN'
        ? '游戏横评,盒子横评,手游对比,盒子折扣对比'
        : '遂戏横评,盒子横评,手遂對比,盒子折扣對比',
  }, locale === defaultLocale ? `${siteConfig.hostname}/reviews` : `${siteConfig.hostname}/${locale}/reviews`, '/reviews')
}

export const dynamic = 'auto'
export const revalidate = 0

interface ReviewArticle {
  masterArticleId: number
  title: string
  description?: string
  coverImage?: string
  coverUrl?: string
  image?: string
  categoryName?: string
  sectionName?: string
  viewCount?: number
  createTime: string
  isHot?: boolean
  tags?: string[]
}

async function getArticlesBySections(locale: Locale, sections: readonly string[]): Promise<ReviewArticle[]> {
  const articleMap = new Map<number, ReviewArticle>()

  console.log(`[reviews] 开始请求 sections=${JSON.stringify(sections)} locale=${locale}`)

  for (const section of sections) {
    try {
      console.log(`[reviews] 请求 section=${section}`)
      const response = await ApiClient.getArticles({
        pageNum: 1,
        pageSize: 200,
        locale,
        section,
      })

      console.log(`[reviews] section=${section} 响应 code=${response.code} rows=${response.rows?.length ?? 0}`)

      if (response.code === 200 && response.rows) {
        for (const article of response.rows as ReviewArticle[]) {
          if (article?.masterArticleId) {
            articleMap.set(article.masterArticleId, article)
          }
        }
      }
    } catch (error) {
      console.error(`[reviews] section=${section} 请求失败:`, error)
    }
  }

  const articles = Array.from(articleMap.values())
  console.log(`[reviews] 共加载 ${articles.length} 篇`)
  return articles
}

async function getReviewArticles(locale: Locale) {
  return getArticlesBySections(locale, reviewSectionSlugs)
}

function formatDate(dateString: string): string {
  if (!dateString) return ''
  return dateString.substring(0, 10)
}

export default async function ReviewsPage({
  params,
}: {
  params: Promise<{ locale: string }>
}) {
  const { locale: localeParam } = await params

  if (!isValidLocale(localeParam)) {
    return null
  }

  const locale = localeParam as Locale
  const articles = await getReviewArticles(locale)
  const groupedArticles = Array.from(
    articles.reduce((map, article) => {
      const groupName = article.categoryName?.trim() || (locale === 'zh-TW' ? '未分類' : '未分类')
      if (!map.has(groupName)) {
        map.set(groupName, [])
      }
      map.get(groupName)!.push(article)
      return map
    }, new Map<string, ReviewArticle[]>())
  )

  const t = (key: string) => getTranslation(key, locale)
  const basePath = locale === defaultLocale ? '' : `/${locale}`

  const jsonLd = buildCollectionPageSchema({
    name: locale === 'zh-TW' ? '遊戲評測' : '游戏评测',
    description: locale === 'zh-TW'
      ? '同品類遊戲數據橫評，多平台盒子折扣橫評'
      : '同品类游戏数据横评，多平台盒子折扣横评',
    url: locale === defaultLocale ? `${siteConfig.hostname}/reviews` : `${siteConfig.hostname}/${locale}/reviews`,
    items: articles.slice(0, 10).map(a => ({
      name: a.title,
      url: `${siteConfig.hostname}${basePath}/reviews/${a.masterArticleId}`,
      image: a.coverImage || a.image || a.coverUrl,
    })),
  })

  const ArticleCard = ({ article }: { article: ReviewArticle }) => {
    const coverSrc = article.coverImage || article.image || article.coverUrl
    return (
    <Link href={`${basePath}/reviews/${article.masterArticleId}`} className="group">
      <Card className="h-full hover:shadow-lg transition-all hover:border-indigo-500/50">
        <div className={`grid ${coverSrc ? 'md:grid-cols-[120px_1fr]' : ''} gap-0`}>
          {coverSrc && (
            <div className="aspect-square overflow-hidden rounded-l-lg bg-slate-800">
              <ImageWithFallback
                src={coverSrc}
                alt={article.title}
                className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
              />
            </div>
          )}
          <CardHeader className="p-4">
            <CardTitle className="text-base group-hover:text-indigo-400 transition-colors line-clamp-2">
              {article.title}
            </CardTitle>
            {article.description && (
              <CardDescription className="line-clamp-2 text-xs">{article.description}</CardDescription>
            )}
            <div className="flex items-center gap-3 text-xs text-muted-foreground pt-1">
              <span className="flex items-center gap-1">
                <Calendar className="h-3 w-3" />
                {formatDate(article.createTime)}
              </span>
              {article.viewCount !== undefined && (
                <span className="flex items-center gap-1">
                  <TrendingUp className="h-3 w-3" />
                  {article.viewCount}
                </span>
              )}
            </div>
          </CardHeader>
        </div>
      </Card>
    </Link>
    )
  }

  return (
    <div className="min-h-screen bg-[#0a0a14] text-[#e8e8f0]">
      <JsonLd data={jsonLd} />
      <div className="max-w-7xl mx-auto px-4 py-4">
        <div className="flex items-center gap-2 text-sm text-[#8080a0]">
          <Link href={locale === 'zh-CN' ? '/' : `/${locale}`} className="hover:text-[#a78bfa] transition-colors">
            {t('home')}
          </Link>
          <ChevronRight className="h-4 w-4" />
          <span className="text-[#e8e8f0]">{locale === 'zh-TW' ? '評測' : '评测'}</span>
        </div>
      </div>

      <section className="border-b border-white/10 relative overflow-hidden" style={{ background: 'linear-gradient(135deg,#0d0d20,#1a0a2e)' }}>
        <div className="max-w-7xl mx-auto px-4 py-12 relative">
          <div className="max-w-3xl mx-auto text-center space-y-4">
            <div className="inline-flex items-center gap-2 px-4 py-2 rounded-full" style={{ background: 'rgba(139,92,246,0.2)', border: '1px solid rgba(139,92,246,0.4)', color: '#a78bfa' }}>
              <BarChart2 className="h-4 w-4" />
              <span className="font-semibold text-sm">{articles.length} {locale === 'zh-TW' ? '篇橫評' : '篇横评'}</span>
            </div>
            <h1 className="text-4xl md:text-5xl font-bold tracking-tight text-white">
              {locale === 'zh-TW' ? '評測' : '评测'}
            </h1>
            <p className="text-lg text-[#9090a8]">
              {locale === 'zh-TW'
                ? '數據橫評，讓選擇有據可依 — 遊戲好不好、盒子值不值，數字說話'
                : '数据横评，让选择有据可依 — 游戏好不好、盒子值不值，数字说话'}
            </p>
            {groupedArticles.length > 0 && (
              <div className="flex flex-wrap items-center justify-center gap-2 pt-2">
                {groupedArticles.slice(0, 6).map(([groupName]) => (
                  <Badge key={groupName} variant="outline" className="text-sm px-3 py-1.5">
                    {groupName}
                  </Badge>
                ))}
              </div>
            )}
          </div>
        </div>
      </section>

      <section className="max-w-7xl mx-auto px-4 py-12 space-y-14">
        {groupedArticles.map(([groupName, items]) => (
          <div key={groupName} className="scroll-mt-20">
            <div className="flex items-center gap-3 mb-6">
              <div className="h-10 w-10 rounded-lg bg-indigo-500/20 flex items-center justify-center text-2xl">📊</div>
              <div>
                <h2 className="text-2xl font-bold text-[#e8e8f0]">{groupName}</h2>
              </div>
              <Badge className="ml-auto" style={{ background: 'rgba(139,92,246,0.2)', color: '#a78bfa' }}>
                {items.length}
              </Badge>
            </div>
            <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-4">
              {items.map((article: ReviewArticle) => (
                <ArticleCard key={article.masterArticleId} article={article} />
              ))}
            </div>
          </div>
        ))}

        {articles.length === 0 && (
          <div className="text-center py-20">
            <div className="text-6xl mb-4">📊</div>
            <h3 className="text-2xl font-bold mb-2 text-[#e8e8f0]">{t('noArticles')}</h3>
            <p className="text-[#8080a0]">{t('stayTuned')}</p>
          </div>
        )}
      </section>
    </div>
  )
}
