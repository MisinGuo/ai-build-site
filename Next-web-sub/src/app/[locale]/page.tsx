import Link from 'next/link'
import { notFound } from 'next/navigation'
import { Suspense } from 'react'
import { ChevronRight, Flame, Star, Shield, BookOpen, Gift, TrendingUp, Gamepad2 } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Card, CardContent } from '@/components/ui/card'
import { homeConfig } from '@/config/customize/pages/home'
import { siteConfig } from '@/config/site/site'
import { fallbackMetadata } from '@/config/customize/fallback-metadata'
import { supportedLocales, defaultLocale, locales } from '@/config/site/locales'
import ApiClient from '@/lib/api'
import { generateHomeMetadata } from '@/lib/metadata'
import { formatCount, formatMoney } from '@/lib/format'
import ImageWithFallback from './ImageWithFallback'
import type { Metadata } from 'next'
import type { Locale } from '@/config/types'
import type { ArticleListItem, GameListItem } from '@/lib/api-types'
import type { StatItem, FeatureCardConfig } from '@/config/customize/pages/home'
import { JsonLd, buildWebSiteSchema } from '@/components/common/JsonLd'

// 与主站保持一致：禁用缓存，每次请求都生成新内容。
// 首页数据均由单一 /api/public/home 接口返回（包含 categories + boxes），备端 Redis 缓存保障速度。
export const revalidate = 0
export const dynamic = 'force-dynamic'

export async function generateStaticParams() {
  // 只为支持的语言生成静态页面
  return supportedLocales.map((locale) => ({
    locale: locale,
  }))
}

// 动态生成metadata
export async function generateMetadata({ params }: { params: Promise<{ locale: Locale }> }): Promise<Metadata> {
  const { locale: currentLocale } = await params
  
  // 验证 locale 是否有效
  if (!supportedLocales.includes(currentLocale as any)) {
    return { title: 'Not Found' }
  }
  
  return generateHomeMetadata(
    currentLocale,
    fallbackMetadata.home.title[currentLocale] ?? fallbackMetadata.home.title[defaultLocale] ?? '',
    fallbackMetadata.home.description[currentLocale] ?? fallbackMetadata.home.description[defaultLocale] ?? '',
    currentLocale === defaultLocale ? siteConfig.hostname : `${siteConfig.hostname}/${currentLocale}`,
    '/'
  )
}

function HomeSectionsSkeleton() {
  return (
    <>
      {/* 分类导航骨架 */}
      <section className="py-10 px-4 max-w-7xl mx-auto">
        <div className="grid grid-cols-3 md:grid-cols-6 gap-3">
          {Array.from({ length: 6 }).map((_, i) => (
            <div
              key={i}
              className="animate-pulse flex flex-col items-center gap-2 p-4 rounded-2xl"
              style={{ background: '#13131f', border: '1px solid rgba(255,255,255,0.03)' }}
            >
              <div className="w-8 h-8 rounded-md bg-gray-700" />
              <div className="w-16 h-3 rounded bg-gray-700 mt-2" />
              <div className="w-10 h-3 rounded bg-gray-700 mt-1" />
            </div>
          ))}
        </div>
      </section>

      {/* 攻略骨架 */}
      <section className="py-12 px-4 max-w-7xl mx-auto">
        <div className="h-7 w-40 bg-gray-800 rounded animate-pulse mb-8" />
        <div className="grid md:grid-cols-2 gap-6">
          <div className="rounded-2xl overflow-hidden" style={{ background: '#13131f' }}>
            <div className="h-52 bg-gray-800 animate-pulse" />
            <div className="p-5">
              <div className="h-4 w-1/3 bg-gray-800 rounded animate-pulse mb-3" />
              <div className="h-5 w-5/6 bg-gray-800 rounded animate-pulse mb-2" />
              <div className="h-5 w-3/4 bg-gray-800 rounded animate-pulse" />
            </div>
          </div>
          <div className="flex flex-col gap-4">
            {Array.from({ length: 3 }).map((_, i) => (
              <div key={i} className="flex gap-4 p-4 rounded-xl" style={{ background: '#13131f' }}>
                <div className="w-24 h-20 rounded-lg bg-gray-800 animate-pulse shrink-0" />
                <div className="flex-1">
                  <div className="h-3 w-1/4 bg-gray-800 rounded animate-pulse mb-2" />
                  <div className="h-4 w-full bg-gray-800 rounded animate-pulse mb-2" />
                  <div className="h-4 w-4/5 bg-gray-800 rounded animate-pulse" />
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* 热门游戏骨架 */}
      <section className="py-12 px-4" style={{ background: 'rgba(139,92,246,0.04)' }}>
        <div className="max-w-7xl mx-auto">
          <div className="h-7 w-40 bg-gray-800 rounded animate-pulse mb-8" />
          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
            {Array.from({ length: 6 }).map((_, i) => (
              <div key={i} className="rounded-xl overflow-hidden" style={{ background: '#13131f' }}>
                <div className="aspect-square bg-gray-800 animate-pulse" />
                <div className="p-3">
                  <div className="h-4 w-4/5 bg-gray-800 rounded animate-pulse mb-2" />
                  <div className="h-3 w-1/2 bg-gray-800 rounded animate-pulse" />
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* 礼包骨架 */}
      <section className="py-12 px-4 max-w-7xl mx-auto">
        <div className="h-7 w-40 bg-gray-800 rounded animate-pulse mb-8" />
        <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-5">
          {Array.from({ length: 4 }).map((_, i) => (
            <div key={i} className="rounded-2xl overflow-hidden" style={{ background: '#13131f' }}>
              <div className="h-36 bg-gray-800 animate-pulse" />
              <div className="p-4">
                <div className="h-4 w-2/3 bg-gray-800 rounded animate-pulse mb-3" />
                <div className="h-5 w-full bg-gray-800 rounded animate-pulse mb-4" />
                <div className="h-9 w-full bg-gray-800 rounded-xl animate-pulse" />
              </div>
            </div>
          ))}
        </div>
      </section>
    </>
  )
}

async function HomeDataSections({ currentLocale }: { currentLocale: Locale }) {
  const t = locales[currentLocale || defaultLocale].translations
  const getText = <T extends Record<string, string>>(map: T): string => {
    return map[currentLocale] || map[defaultLocale] || ''
  }
  const lp = (path: string) => (currentLocale === defaultLocale ? path : `/${currentLocale}${path}`)

  // 单一接口获取首页全量数据（包含 categories + boxes）
  const response = await ApiClient.getHomeData({
    locale: currentLocale,
    strategyCount: homeConfig.data.guidesCount,
    hotGamesCount: homeConfig.data.gamesCount,
  }).catch((error) => {
    console.error('[HomePage] 获取首页数据失败，已降级为空数据:', error)
    return { code: 500, msg: 'home data fallback', data: {}, rows: [] }
  })

  const homeData = response.data || {}
  const strategyArticles = (homeData.strategyArticles || []) as ArticleListItem[]
  const hotGames = (homeData.hotGames || []) as GameListItem[]
  const statistics = homeData.statistics || null

  const categoriesRaw = (Array.isArray(homeData.categories)
    ? homeData.categories
    : []) as Array<Record<string, any>>

  const categoryItems = categoriesRaw
    .filter((cat) => cat.name !== '卡牌' && cat.title !== '卡牌')
    .sort((a, b) => {
      const countA = Number(a.relatedDataCount || a.gamesCount || a.gameCount || 0)
      const countB = Number(b.relatedDataCount || b.gamesCount || b.gameCount || 0)
      return countB - countA
    })
    .slice(0, 6)
    .map((cat, idx) => ({
      id: cat.id ?? idx,
      label: cat.name || cat.title || getText(homeConfig.ui.categoryFallbackLabel),
      icon: cat.icon || '📂',
      count: Number(cat.relatedDataCount || cat.gamesCount || cat.gameCount || 0),
      href: lp(`/games/category/${cat.slug || cat.id}`),
    }))

  const boxItems = (Array.isArray(homeData.boxes)
    ? homeData.boxes
    : []).slice(0, 4) as Array<Record<string, any>>

  const statsData = statistics ? homeConfig.stats
    .filter((stat: StatItem) => {
      if (stat.visible === undefined) return true
      if (typeof stat.visible === 'boolean') return stat.visible
      return stat.visible[currentLocale] !== false
    })
    .map((stat: StatItem) => {
      const label = stat.label[currentLocale as keyof typeof stat.label] || stat.label[defaultLocale as keyof typeof stat.label]
      let value = stat.value
      if (stat.dataKey && statistics[stat.dataKey] != null) {
        const rawValue = statistics[stat.dataKey]
        value = stat.dataKey === 'totalSavings' ? formatMoney(rawValue) : formatCount(rawValue)
      }
      return { label, value, icon: stat.icon }
    }) : []

  const hotGuides = strategyArticles.slice(0, 4)
  const localeText = {
    hotGuidesSub: getText(homeConfig.ui.hotGuidesSub),
    hotGamesSub: getText(homeConfig.ui.hotGamesSub),
    giftSub: getText(homeConfig.ui.giftSub),
    giftSectionTitle: getText(homeConfig.ui.giftSectionTitle),
    giftMoreLinkText: getText(homeConfig.ui.giftMoreLinkText),
    viewDetailText: getText(homeConfig.ui.viewDetailText),
    viewText: getText(homeConfig.ui.viewText),
    guideCategoryFallbackLabel: getText(homeConfig.ui.guideCategoryFallbackLabel),
    remainingText: getText(homeConfig.ui.remainingText),
    limitedGiftText: getText(homeConfig.ui.limitedGiftText),
    claimNowText: getText(homeConfig.ui.claimNowText),
    boxGiftBadge: homeConfig.ui.boxGiftBadge ? getText(homeConfig.ui.boxGiftBadge) : (currentLocale === 'zh-TW' ? '含專屬禮包' : '含专属礼包'),
    features: homeConfig.featureCards.map((item: FeatureCardConfig) => ({
      title: getText(item.title),
      desc: getText(item.desc),
      icon: item.icon,
      color: item.color,
    })),
  }

  const featureIconMap = {
    BookOpen,
    Star,
    Gift,
    Shield,
  }

  return (
    <>
      {/* 分类导航 */}
      <section className="py-10 px-4 max-w-7xl mx-auto">
        <div className="grid grid-cols-3 md:grid-cols-6 gap-3">
          {categoryItems.map((cat) => (
            <a
              key={cat.id}
              href={cat.href}
              target="_blank"
              rel="noopener noreferrer"
              className="group flex flex-col items-center gap-2 p-4 rounded-2xl transition-all duration-200 hover:-translate-y-0.5"
              style={{ background: '#13131f', border: '1px solid rgba(255,255,255,0.06)' }}
            >
              <span style={{ fontSize: '1.75rem' }}>{cat.icon}</span>
              <span style={{ color: '#c4c4d4', fontSize: '0.85rem', fontWeight: 600 }}>{cat.label}</span>
              <span style={{ color: '#505068', fontSize: '0.75rem' }}>{cat.count}</span>
            </a>
          ))}
        </div>
      </section>

      {/* 热门攻略 */}
      <section className="py-12 px-4 max-w-7xl mx-auto">
        <div className="flex items-center justify-between mb-8">
          <div className="flex items-center gap-3">
            <div className="w-8 h-8 rounded-lg flex items-center justify-center" style={{ background: 'linear-gradient(135deg,#ef4444,#f97316)' }}>
              <Flame className="w-4 h-4 text-white" />
            </div>
            <div>
              <h2 style={{ color: '#e8e8f0', fontWeight: 700 }}>{homeConfig.sections.guides.title[currentLocale as keyof typeof homeConfig.sections.guides.title]}</h2>
              <p style={{ color: '#606078', fontSize: '0.8rem' }}>{localeText.hotGuidesSub}</p>
            </div>
          </div>
        </div>

        {hotGuides.length > 0 ? (
          <div className="grid md:grid-cols-2 gap-6">
            <Link
              href={lp(`/guides/${hotGuides[0].masterArticleId}`)}
              className="group rounded-2xl overflow-hidden cursor-pointer transition-all duration-300 hover:-translate-y-0.5"
              style={{ background: '#13131f', border: '1px solid rgba(255,255,255,0.06)' }}
            >
              <div className="relative h-52 overflow-hidden">
                <ImageWithFallback src={hotGuides[0].coverUrl || hotGuides[0].coverImage} alt={hotGuides[0].title} className="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105" />
                <div className="absolute inset-0" style={{ background: 'linear-gradient(to top, #13131f 0%, transparent 50%)' }} />
                <span className="absolute top-3 left-3 px-2 py-0.5 rounded-md text-xs font-bold" style={{ background: 'rgba(239,68,68,0.9)', color: '#fff' }}>
                  HOT
                </span>
              </div>
              <div className="p-5">
                <div className="flex items-center gap-2 mb-2">
                  <span className="px-2 py-0.5 rounded-md text-xs" style={{ background: 'rgba(139,92,246,0.2)', color: '#a78bfa' }}>
                    {hotGuides[0].categoryName || localeText.guideCategoryFallbackLabel}
                  </span>
                </div>
                <h3 style={{ color: '#e8e8f0', fontWeight: 700, lineHeight: 1.4, marginBottom: '0.5rem' }} className="line-clamp-2">
                  {hotGuides[0].title}
                </h3>
                <div className="flex items-center justify-between">
                  <span style={{ color: '#505068', fontSize: '0.8rem' }}>👁 {hotGuides[0].viewCount || 0}</span>
                  <Button size="sm" className="px-4">{localeText.viewDetailText}</Button>
                </div>
              </div>
            </Link>

            <div className="flex flex-col gap-4">
              {hotGuides.slice(1).map((guide) => (
                <Link
                  key={guide.masterArticleId}
                  href={lp(`/guides/${guide.masterArticleId}`)}
                  className="group flex gap-4 p-4 rounded-xl cursor-pointer transition-all hover:-translate-y-0.5"
                  style={{ background: '#13131f', border: '1px solid rgba(255,255,255,0.06)' }}
                >
                  <div className="relative w-24 h-20 rounded-lg overflow-hidden shrink-0">
                    <ImageWithFallback src={guide.coverUrl || guide.coverImage} alt={guide.title} className="w-full h-full object-cover transition-transform duration-300 group-hover:scale-110" />
                  </div>
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-2 mb-1">
                      <span className="px-1.5 py-0.5 rounded text-xs" style={{ background: 'rgba(139,92,246,0.15)', color: '#a78bfa' }}>
                        {guide.categoryName || localeText.guideCategoryFallbackLabel}
                      </span>
                    </div>
                    <h4 className="line-clamp-2" style={{ color: '#e8e8f0', fontWeight: 600, fontSize: '0.9rem', lineHeight: 1.4, marginBottom: '0.5rem' }}>
                      {guide.title}
                    </h4>
                    <div className="flex items-center justify-between">
                      <span style={{ color: '#505068', fontSize: '0.75rem' }}>👁 {guide.viewCount || 0}</span>
                      <span style={{ color: '#a78bfa', fontSize: '0.8rem' }} className="flex items-center gap-0.5">
                        {localeText.viewText} <ChevronRight className="w-3 h-3" />
                      </span>
                    </div>
                  </div>
                </Link>
              ))}
            </div>
          </div>
        ) : (
          <div className="text-center py-10" style={{ color: '#707090' }}>
            {t.noArticles}
          </div>
        )}
      </section>

      {/* 热门游戏 */}
      <section className="py-12 px-4" style={{ background: 'rgba(139,92,246,0.04)' }}>
        <div className="max-w-7xl mx-auto">
          <div className="flex items-center justify-between mb-8">
            <div className="flex items-center gap-3">
              <div className="w-8 h-8 rounded-lg flex items-center justify-center" style={{ background: 'linear-gradient(135deg,#8b5cf6,#ec4899)' }}>
                <TrendingUp className="w-4 h-4 text-white" />
              </div>
              <div>
                <h2 style={{ color: '#e8e8f0', fontWeight: 700 }}>{homeConfig.sections.games.title[currentLocale as keyof typeof homeConfig.sections.games.title]}</h2>
                <p style={{ color: '#606078', fontSize: '0.8rem' }}>{localeText.hotGamesSub}</p>
              </div>
            </div>
            <Link href={lp('/games')} className="flex items-center gap-1 text-sm transition-colors hover:opacity-80" style={{ color: '#a78bfa' }}>
              {homeConfig.sections.games.moreLink?.text[currentLocale as keyof typeof homeConfig.sections.games.moreLink.text] || '更多'} →
            </Link>
          </div>

          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
            {hotGames.map((game) => (
              <Link key={game.id} href={lp(`/games/${game.id}`)}>
                <Card className="overflow-hidden cursor-pointer group">
                  <div className="aspect-square bg-[#13131f] relative overflow-hidden">
                    <ImageWithFallback src={game.iconUrl || game.coverImage} alt={game.name || game.title || 'game'} className="w-full h-full object-cover" />
                  </div>
                  <CardContent className="p-3">
                    <h3 className="text-sm font-bold text-white group-hover:text-orange-400 transition-colors line-clamp-1">
                      {game.name || game.title}
                    </h3>
                    <p className="text-xs text-slate-500 mt-1">
                      {game.downloadCount ? `${(game.downloadCount / 1000).toFixed(1)}K ${t.download}` : (game.categoryName || t.defaultCategory)}
                    </p>
                  </CardContent>
                </Card>
              </Link>
            ))}
          </div>
        </div>
      </section>

      {/* 主站导流横幅 */}
      <section className="px-4 pb-6 max-w-7xl mx-auto">
        <a
          href={siteConfig.mainSiteUrl}
          target="_blank"
          rel="noopener noreferrer"
          className="group flex items-center justify-between p-5 md:p-6 rounded-2xl transition-all duration-300 hover:opacity-90 hover:shadow-lg"
          style={{ background: 'linear-gradient(135deg,rgba(139,92,246,0.12),rgba(236,72,153,0.12))', border: '1px solid rgba(139,92,246,0.25)' }}
        >
          <div className="flex items-center gap-4">
            <div
              className="w-12 h-12 rounded-2xl flex items-center justify-center shrink-0"
              style={{ background: 'linear-gradient(135deg,#8b5cf6,#ec4899)' }}
            >
              <Gamepad2 className="w-6 h-6 text-white" />
            </div>
            <div>
              <p className="font-bold" style={{ color: '#e8e8f0' }}>
                {currentLocale === 'zh-TW' ? '遂戲盒子平台' : '游戏盒子平台'}
              </p>
              <p className="text-sm mt-0.5" style={{ color: '#8080a0' }}>
                {currentLocale === 'zh-TW'
                  ? '下載遂戲 · 對比折扣 · 領取專屬礼包'
                  : '下载游戏 · 对比折扣 · 领取专属礼包'}
              </p>
            </div>
          </div>
          <span className="hidden sm:flex items-center gap-1.5 font-semibold shrink-0" style={{ color: '#a78bfa' }}>
            {currentLocale === 'zh-TW' ? '前往主站' : '前往主站'}
            <ChevronRight className="w-5 h-5 group-hover:translate-x-1 transition-transform" />
          </span>
        </a>
      </section>

      {/* 盒子专属礼包 */}
      <section className="py-12 px-4 max-w-7xl mx-auto">
        <div className="flex items-center justify-between mb-8">
          <div className="flex items-center gap-3">
            <div className="w-8 h-8 rounded-lg flex items-center justify-center" style={{ background: 'linear-gradient(135deg,#f59e0b,#ef4444)' }}>
              <Gift className="w-4 h-4 text-white" />
            </div>
            <div>
              <h2 style={{ color: '#e8e8f0', fontWeight: 700 }}>{localeText.giftSectionTitle}</h2>
              <p style={{ color: '#606078', fontSize: '0.8rem' }}>{localeText.giftSub}</p>
            </div>
          </div>
          <a
            href={`${siteConfig.mainSiteUrl}/boxes`}
            target="_blank"
            rel="noopener noreferrer"
            className="flex items-center gap-1 text-sm transition-colors hover:opacity-80"
            style={{ color: '#f59e0b' }}
          >
            {localeText.giftMoreLinkText}
            <ChevronRight className="w-4 h-4" />
          </a>
        </div>

        {boxItems.length > 0 ? (
          <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-5">
            {boxItems.map((box) => (
              <a
                key={box.id}
                href={`${siteConfig.mainSiteUrl}/boxes/${box.id}`}
                target="_blank"
                rel="noopener noreferrer"
                className="group rounded-2xl overflow-hidden transition-all duration-300 hover:-translate-y-0.5 hover:shadow-lg flex flex-col"
                style={{ background: '#13131f', border: '1px solid rgba(245,158,11,0.2)' }}
              >
                <div className="relative h-36 overflow-hidden bg-[#0d0d1a] flex items-center justify-center">
                  {box.logoUrl ? (
                    <ImageWithFallback
                      src={box.logoUrl}
                      alt={box.name || box.title || ''}
                      className="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105"
                    />
                  ) : (
                    <div className="w-20 h-20 rounded-2xl flex items-center justify-center" style={{ background: 'linear-gradient(135deg,#f59e0b33,#ef444433)' }}>
                      <Gift className="w-10 h-10" style={{ color: '#f59e0b' }} />
                    </div>
                  )}
                  <div className="absolute inset-0" style={{ background: 'linear-gradient(to top, #13131f 0%, transparent 50%)' }} />
                  <span className="absolute top-3 left-3 px-2 py-0.5 rounded-md text-xs font-bold" style={{ background: 'rgba(245,158,11,0.9)', color: '#fff' }}>
                    🎁 {localeText.boxGiftBadge}
                  </span>
                </div>
                <div className="p-4 flex flex-col flex-1">
                  <h4 style={{ color: '#e8e8f0', fontWeight: 700, marginBottom: '0.5rem' }} className="line-clamp-1">
                    {box.name || box.title}
                  </h4>
                  {box.description && (
                    <p className="line-clamp-2 text-sm mb-3" style={{ color: '#707090', lineHeight: 1.5 }}>
                      {box.description}
                    </p>
                  )}
                  <div className="mt-auto block w-full text-center py-2.5 rounded-xl text-white font-semibold text-sm transition-all group-hover:opacity-90" style={{ background: 'linear-gradient(135deg,#f59e0b,#ef4444)' }}>
                    🎁 {localeText.claimNowText} →
                  </div>
                </div>
              </a>
            ))}
          </div>
        ) : (
          /* 无盒子数据时：展示引导横幅 */
          <a
            href={`${siteConfig.mainSiteUrl}/boxes`}
            target="_blank"
            rel="noopener noreferrer"
            className="group flex items-center justify-between p-6 rounded-2xl transition-all hover:opacity-90"
            style={{ background: 'linear-gradient(135deg,rgba(245,158,11,0.1),rgba(239,68,68,0.1))', border: '1px solid rgba(245,158,11,0.25)' }}
          >
            <div className="flex items-center gap-4">
              <div className="w-12 h-12 rounded-2xl flex items-center justify-center shrink-0" style={{ background: 'linear-gradient(135deg,#f59e0b,#ef4444)' }}>
                <Gift className="w-6 h-6 text-white" />
              </div>
              <div>
                <p className="font-bold" style={{ color: '#e8e8f0' }}>{localeText.giftSectionTitle}</p>
                <p className="text-sm mt-0.5" style={{ color: '#8080a0' }}>{localeText.giftSub}</p>
              </div>
            </div>
            <span className="hidden sm:flex items-center gap-1.5 font-semibold shrink-0" style={{ color: '#f59e0b' }}>
              {localeText.giftMoreLinkText}
              <ChevronRight className="w-5 h-5 group-hover:translate-x-1 transition-transform" />
            </span>
          </a>
        )}
      </section>

      {/* 特性卡片 */}
      <section className="py-12 px-4" style={{ background: 'rgba(6,182,212,0.04)' }}>
        <div className="max-w-7xl mx-auto">
          <div className="grid md:grid-cols-4 gap-5">
            {localeText.features.map((f) => {
              const Icon = featureIconMap[f.icon]
              return (
                <div key={f.title} className="p-5 rounded-2xl" style={{ background: '#13131f', border: '1px solid rgba(255,255,255,0.06)' }}>
                  <div className="w-12 h-12 rounded-xl flex items-center justify-center mb-4" style={{ background: `${f.color}22`, color: f.color }}>
                    <Icon className="w-6 h-6" />
                  </div>
                  <h3 style={{ color: '#e8e8f0', fontWeight: 700, marginBottom: '0.5rem' }}>{f.title}</h3>
                  <p style={{ color: '#707090', fontSize: '0.875rem', lineHeight: 1.6 }}>{f.desc}</p>
                </div>
              )
            })}
          </div>
        </div>
      </section>

      {/* Hero 统计数字（仅在数据加载完成后显示，用于SEO） */}
      {statsData.length > 0 && (
        <div id="hero-stats-data" className="hidden" aria-hidden="true" data-stats={JSON.stringify(statsData)} />
      )}
    </>
  )
}

export default async function LocaleHomePage({ params }: { params: Promise<{ locale: Locale }> }) {
  const { locale: currentLocale } = await params

  // 验证 locale 是否有效，防止被错误匹配（如 sitemap 路由）
  if (!supportedLocales.includes(currentLocale as any)) {
    notFound()
  }

  const lp = (path: string) => (currentLocale === defaultLocale ? path : `/${currentLocale}${path}`)
  const siteUrl = currentLocale === defaultLocale ? siteConfig.hostname : `${siteConfig.hostname}/${currentLocale}`
  const webSiteSchema = buildWebSiteSchema({
    name: siteConfig.name,
    url: siteUrl,
    description: siteConfig.description,
  })

  return (
    <div className="bg-[#0a0a14] min-h-screen pb-12 text-[#e8e8f0]">
      <JsonLd data={webSiteSchema} />
      {/* Hero 区域（静态，无需等待数据） */}
      <section
        className="relative overflow-hidden min-h-[520px] flex items-center"
        style={{ background: 'linear-gradient(135deg, #0d0d20 0%, #1a0a2e 50%, #0d1a2e 100%)' }}
      >
        <div className="absolute inset-0 opacity-20">
          <ImageWithFallback
            src={homeConfig.heroBackgroundImage}
            alt="Hero background"
            className="w-full h-full object-cover"
          />
        </div>
        <div className="absolute inset-0" style={{ background: 'linear-gradient(90deg, rgba(10,10,20,0.95) 0%, rgba(10,10,20,0.6) 60%, rgba(10,10,20,0.2) 100%)' }} />

        <div className="relative z-10 max-w-7xl mx-auto px-4 py-20 w-full">
          <div className="max-w-xl">
            <div
              className="inline-flex items-center gap-2 px-3 py-1.5 rounded-full mb-5"
              style={{ background: 'rgba(139,92,246,0.2)', border: '1px solid rgba(139,92,246,0.4)' }}
            >
              <Flame className="w-4 h-4" style={{ color: '#f97316' }} />
              <span style={{ color: '#a78bfa', fontSize: '0.8rem', fontWeight: 600 }}>
                {homeConfig.hero.badge[currentLocale as keyof typeof homeConfig.hero.badge]}
              </span>
            </div>

            <h1 style={{ fontSize: '2.5rem', fontWeight: 800, lineHeight: 1.2, color: '#ffffff' }} className="mb-4">
              {homeConfig.hero.title[currentLocale as keyof typeof homeConfig.hero.title]}
              <br />
              <span style={{ background: 'linear-gradient(90deg,#a78bfa,#f472b6)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent' }}>
                {homeConfig.hero.highlightText[currentLocale as keyof typeof homeConfig.hero.highlightText]}
              </span>
            </h1>

            <div className="flex flex-wrap gap-3 mt-2">
              <a
                href={siteConfig.mainSiteUrl}
                target="_blank"
                rel="noopener noreferrer"
                className="inline-flex items-center gap-2 px-6 py-3.5 rounded-lg font-semibold text-white transition-all hover:opacity-90 hover:scale-105"
                style={{ background: 'linear-gradient(135deg,var(--color-primary),var(--color-secondary))', fontSize: '1rem' }}
              >
                <Gamepad2 className="w-5 h-5" />
                {currentLocale === 'zh-TW' ? '進入遂戲盒子主站' : '进入游戏盒子主站'}
              </a>
              <Link
                href={lp(homeConfig.hero.secondaryButton.href)}
                className="inline-flex items-center gap-1.5 px-6 py-3.5 rounded-lg font-semibold transition-all hover:opacity-80"
                style={{ background: 'rgba(255,255,255,0.08)', color: '#e8e8f0', border: '1px solid rgba(255,255,255,0.12)' }}
              >
                {homeConfig.hero.secondaryButton.text[currentLocale as keyof typeof homeConfig.hero.secondaryButton.text]}
                <ChevronRight className="w-4 h-4" />
              </Link>
            </div>
          </div>
        </div>
      </section>

      {/* 数据区域：Suspense 包裹，加载时显示骨架屏 */}
      <Suspense fallback={<HomeSectionsSkeleton />}>
        <HomeDataSections currentLocale={currentLocale} />
      </Suspense>
    </div>
  )
}
