import ApiClient from '@/lib/api'
import type { SitemapUrl, ContentType } from './types'
import { sitemapConfig } from '@/config/sitemap/config'
import { generateAlternateUrls } from './generator'
import { defaultLocale } from '@/config/site/locales'
import { guideSectionSlugs } from '@/config/customize/pages/guides'
import { reviewSectionSlugs } from '@/config/customize/pages/reviews'
import siteConf from '@/config/customize/site'

const { contentTypes } = sitemapConfig

/**
 * 将后端返回的时间字符串（如 "2026-03-26 16:21:24"）规范化为 ISO 8601 格式
 * sitemap 标准要求 W3C Datetime 格式，Google 强制需要时区信息
 */
function toSitemapLastmod(value?: string | null): string {
  if (!value) return new Date().toISOString()
  if (value.includes('T')) return value
  return value.replace(' ', 'T') + 'Z'
}

/**
 * 从 customize/site.ts 获取需要加入 sitemap 的静态路径
 * 规则：enabled === true 且 inSitemap === true
 */
function getStaticPaths(): string[] {
  const paths = siteConf.navigation.header
    .filter((item) => item.enabled && item.inSitemap)
    .map((item) => item.path?.trim())
    .filter((path): path is string => Boolean(path) && path.startsWith('/'))

  return Array.from(new Set(paths)).sort((a, b) => a.localeCompare(b))
}

async function fetchArticlesBySections(locale: string, sections: readonly string[]): Promise<any[]> {
  const articleMap = new Map<number, any>()

  for (const section of sections) {
    try {
      const response = await ApiClient.getArticles({
        locale: locale as any,
        pageNum: 1,
        pageSize: 10000,
        section: section as any,
      })

      if (response.code === 200 && response.rows) {
        for (const article of response.rows) {
          if (article?.masterArticleId) {
            articleMap.set(article.masterArticleId, article)
          }
        }
      }
    } catch (error) {
      console.warn(`[Sitemap] 获取 section=${section} 文章失败:`, error)
    }
  }

  return Array.from(articleMap.values())
}

/**
 * 开发模式：校验 routes.ts 是否遗漏了 page.tsx 静态页面
 * 仅在 dev 环境运行（生产环境无文件系统，不执行）
 */
let devCheckDone = false
async function devCheckMissingRoutes(): Promise<void> {
  if (process.env.NODE_ENV !== 'development' || devCheckDone) return
  devCheckDone = true

  try {
    const { readdir } = await import('node:fs/promises')
    const path = await import('node:path')
    const localeAppDir = path.join(process.cwd(), 'src', 'app', '[locale]')

    const pageFiles: string[] = []
    const collect = async (dir: string) => {
      const entries = await readdir(dir, { withFileTypes: true })
      for (const entry of entries) {
        const fullPath = path.join(dir, entry.name)
        if (entry.isDirectory()) {
          await collect(fullPath)
        } else if (entry.name === 'page.tsx') {
          pageFiles.push(fullPath)
        }
      }
    }
    await collect(localeAppDir)

    // 解析为路由路径，排除动态路由
    const discoveredPaths = new Set<string>()
    for (const filePath of pageFiles) {
      const rel = path.relative(localeAppDir, filePath).replace(/\\/g, '/')
      if (rel === 'page.tsx') {
        discoveredPaths.add('/')
        continue
      }
      if (!rel.endsWith('/page.tsx')) continue
      const segments = rel.slice(0, -'/page.tsx'.length).split('/').filter(Boolean)
      if (segments.some(s => s.startsWith('[') || s.startsWith('@') || s.startsWith('_'))) continue
      const urlSegments = segments.filter(s => !(s.startsWith('(') && s.endsWith(')')))
      discoveredPaths.add(urlSegments.length === 0 ? '/' : `/${urlSegments.join('/')}`)
    }

    const configuredPaths = new Set(getStaticPaths())
    const missing = Array.from(discoveredPaths).filter(p => !configuredPaths.has(p))

    if (missing.length > 0) {
      console.warn(
        `\n⚠️  [Sitemap] customize/site.ts 中缺少以下静态页面路由（inSitemap 未设为 true），请及时补充：\n` +
        missing.map(p => `   - ${p}`).join('\n') + '\n'
      )
    }
  } catch {
    // 忽略扫描失败（不影响正常功能）
  }
}

/**
 * 获取静态页面 URLs
 */
export async function fetchStaticUrls(locale: string, hostname: string): Promise<SitemapUrl[]> {
  // 开发模式下异步校验（不阻塞返回）
  devCheckMissingRoutes()

  const staticPaths = getStaticPaths()
  
  const config = contentTypes.static
  const localePrefix = locale === defaultLocale ? '' : `/${locale}`
  
  return staticPaths.map((path) => ({
    loc: `${hostname}${localePrefix}${path}`,
    lastmod: new Date().toISOString(),
    changefreq: config.changefreq as any,
    priority: config.priority,
    alternates: generateAlternateUrls(path, hostname),
  }))
}

/**
 * 获取攻略文章 URLs
 */
export async function fetchGuidesUrls(locale: string, hostname: string): Promise<SitemapUrl[]> {
  try {
    const articles = await fetchArticlesBySections(locale, guideSectionSlugs)
    const config = contentTypes.guides
    const localePrefix = locale === defaultLocale ? '' : `/${locale}`
    
    return articles.map((article: any) => {
      const path = `/guides/${article.masterArticleId}`
      return {
        loc: `${hostname}${localePrefix}${path}`,
        lastmod: toSitemapLastmod(article.updateTime),
        changefreq: config.changefreq as any,
        priority: config.priority,
        alternates: generateAlternateUrls(path, hostname),
      }
    })
  } catch (error) {
    console.error('Error fetching guides URLs:', error)
    return []
  }
}

/**
 * 获取评测文章 URLs
 */
export async function fetchReviewsUrls(locale: string, hostname: string): Promise<SitemapUrl[]> {
  try {
    const articles = await fetchArticlesBySections(locale, reviewSectionSlugs)
    const config = contentTypes.reviews
    const localePrefix = locale === defaultLocale ? '' : `/${locale}`

    return articles.map((article: any) => {
      const path = `/reviews/${article.masterArticleId}`
      return {
        loc: `${hostname}${localePrefix}${path}`,
        lastmod: toSitemapLastmod(article.updateTime),
        changefreq: config.changefreq as any,
        priority: config.priority,
        alternates: generateAlternateUrls(path, hostname),
      }
    })
  } catch (error) {
    console.error('Error fetching reviews URLs:', error)
    return []
  }
}

/**
 * 判断某个导航路径是否允许加入 sitemap
 * 规则：nav 项不存在于 header → 不收录；存在则需 enabled && inSitemap 均为 true
 */
function isPathInSitemap(path: string): boolean {
  const item = siteConf.navigation.header.find((i) => i.path === path)
  if (!item) return false
  return item.enabled && item.inSitemap
}

/**
 * 获取游戏 URLs（详情页 + 分类页）
 * 若 /games 导航项的 inSitemap 为 false，则整体跳过
 */
export async function fetchGameUrls(locale: string, hostname: string): Promise<SitemapUrl[]> {
  if (!isPathInSitemap('/games')) return []

  const config = contentTypes.games
  const localePrefix = locale === defaultLocale ? '' : `/${locale}`
  const urls: SitemapUrl[] = []

  try {
    // 游戏详情页
    const gamesResponse = await ApiClient.getGames({
      locale: locale as any,
      pageNum: 1,
      pageSize: 10000,
    })
    const games = gamesResponse.rows || []
    for (const game of games) {
      const path = `/games/${game.id}`
      urls.push({
        loc: `${hostname}${localePrefix}${path}`,
        lastmod: toSitemapLastmod(game.updateTime),
        changefreq: config.changefreq as any,
        priority: config.priority,
        alternates: generateAlternateUrls(path, hostname),
      })
    }

    // 游戏分类页
    try {
      const categoriesResponse = await ApiClient.getCategories({
        categoryType: 'game',
        locale: locale as any,
      })
      const categories = categoriesResponse.data || []
      for (const category of categories) {
        const slug = category.slug || category.id
        const path = `/games/category/${slug}`
        urls.push({
          loc: `${hostname}${localePrefix}${path}`,
          lastmod: new Date().toISOString(),
          changefreq: 'weekly' as any,
          priority: 0.7,
          alternates: generateAlternateUrls(path, hostname),
        })
      }
    } catch (error) {
      console.warn('[Sitemap] 获取游戏分类失败:', error)
    }
  } catch (error) {
    console.error('[Sitemap] 获取游戏 URLs 失败:', error)
  }

  return urls
}

/**
 * 获取盒子 URLs（详情页 + 下载页）
 * 若 /boxes 导航项的 inSitemap 为 false，则整体跳过
 */
export async function fetchBoxUrls(locale: string, hostname: string): Promise<SitemapUrl[]> {
  if (!isPathInSitemap('/boxes')) return []

  const config = contentTypes.boxes
  const localePrefix = locale === defaultLocale ? '' : `/${locale}`
  const urls: SitemapUrl[] = []

  try {
    const response = await ApiClient.getBoxes({
      locale: locale as any,
      pageNum: 1,
      pageSize: 10000,
    })
    const boxes = response.rows || []
    for (const box of boxes) {
      // 盒子详情页
      const detailPath = `/boxes/${box.id}`
      urls.push({
        loc: `${hostname}${localePrefix}${detailPath}`,
        lastmod: toSitemapLastmod(box.updateTime),
        changefreq: config.changefreq as any,
        priority: config.priority,
        alternates: generateAlternateUrls(detailPath, hostname),
      })
      // 盒子下载页
      const downloadPath = `/boxes/${box.id}/download`
      urls.push({
        loc: `${hostname}${localePrefix}${downloadPath}`,
        lastmod: toSitemapLastmod(box.updateTime),
        changefreq: config.changefreq as any,
        priority: 0.7,
        alternates: generateAlternateUrls(downloadPath, hostname),
      })
    }
  } catch (error) {
    console.error('[Sitemap] 获取盒子 URLs 失败:', error)
  }

  return urls
}

/**
 * 根据类型获取 URLs
 */
export async function fetchUrlsByType(
  locale: string,
  type: ContentType,
  hostname: string
): Promise<SitemapUrl[]> {
  console.log(`[Sitemap] 获取 ${locale} 的 ${type} URLs...`)

  switch (type) {
    case 'static':
      return fetchStaticUrls(locale, hostname)
    case 'games':
      return fetchGameUrls(locale, hostname)
    case 'boxes':
      return fetchBoxUrls(locale, hostname)
    case 'guides':
      return fetchGuidesUrls(locale, hostname)
    case 'reviews':
      return fetchReviewsUrls(locale, hostname)
    default:
      return []
  }
}
