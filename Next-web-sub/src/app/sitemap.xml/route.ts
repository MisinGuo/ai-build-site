import { NextResponse } from 'next/server'
import { getSitemapChunkCount, generateSitemapIndex } from '@/lib/sitemap/generator'
import { fetchUrlsByType } from '@/lib/sitemap/fetchers'
import { sitemapConfig } from '@/config/sitemap/config'
import { supportedLocales } from '@/config/site/locales'
import { getSecureHostname } from '@/lib/sitemap/security'
import type { ContentType } from '@/lib/sitemap/types'

/**
 * 主 sitemap 索引
 * 路由: /sitemap.xml
 * 包含所有语言的 sitemap 链接，指向 /api/sitemap/{locale}/{type}
 */

export const dynamic = 'force-dynamic'
export const revalidate = 3600 // 1小时重新生成

const validTypes = Object.keys(sitemapConfig.contentTypes) as ContentType[]

function getSitemapCacheControl(): string {
  return process.env.NODE_ENV === 'development'
    ? 'no-store, max-age=0'
    : 'public, max-age=3600, s-maxage=3600'
}

export async function GET(request: Request) {
  try {
    const hostname = getSecureHostname(request)

    const entries: { locale: string; type: ContentType; chunk: number; lastmod: string }[] = []

    for (const locale of supportedLocales) {
      for (const type of validTypes) {
        const urls = await fetchUrlsByType(locale, type, hostname)
        if (urls.length === 0) {
          continue
        }
        const chunkCount = getSitemapChunkCount(urls.length)
        const lastmod = new Date().toISOString()

        for (let chunk = 1; chunk <= chunkCount; chunk += 1) {
          entries.push({ locale, type, chunk, lastmod })
        }
      }
    }

    // 降级：至少保留每种语言的 static 条目
    if (entries.length === 0) {
      for (const locale of supportedLocales) {
        entries.push({ locale, type: 'static', chunk: 1, lastmod: new Date().toISOString() })
      }
    }

    console.log(`[Sitemap] 生成主索引 sitemap.xml，共 ${entries.length} 个子 sitemap`)
    const xml = generateSitemapIndex(hostname, entries)

    return new NextResponse(xml, {
      headers: {
        'Content-Type': 'application/xml; charset=utf-8',
        'Cache-Control': getSitemapCacheControl(),
      },
    })
  } catch (error) {
    console.error('生成主 sitemap 索引失败:', error)
    return new NextResponse('生成 sitemap 失败', { status: 500 })
  }
}
