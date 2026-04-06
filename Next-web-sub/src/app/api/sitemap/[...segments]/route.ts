import { NextResponse } from 'next/server'
import { supportedLocales } from '@/config/site/locales'
import {
  generateLocaleIndexWithEntries,
  generateContentSitemap,
  getSitemapChunkCount,
  sliceUrlsByChunk,
} from '@/lib/sitemap/generator'
import { fetchUrlsByType } from '@/lib/sitemap/fetchers'
import { getSecureHostname } from '@/lib/sitemap/security'
import type { ContentType } from '@/lib/sitemap/types'
import { sitemapConfig } from '@/config/sitemap/config'

export const dynamic = 'force-dynamic'
export const revalidate = 3600

const validTypes = Object.keys(sitemapConfig.contentTypes) as ContentType[]

function getSitemapCacheControl(): string {
  return process.env.NODE_ENV === 'development'
    ? 'no-store, max-age=0'
    : 'public, max-age=3600, s-maxage=3600'
}

export async function GET(
  request: Request,
  { params }: { params: Promise<{ segments: string[] }> }
) {
  const { segments } = await params
  
  // 验证并获取安全的 hostname（防止内容被盗用）
  const hostname = getSecureHostname(request)
  
  console.log('[API Sitemap] segments:', segments)
  
  // /api/sitemap/zh-TW -> ['zh-TW']
  if (segments.length === 1) {
    const locale = segments[0]
    
    if (!supportedLocales.includes(locale as any)) {
      return new NextResponse('不支持的语言', { status: 404 })
    }
    
    try {
      const entries: { type: ContentType; chunk: number; lastmod?: string }[] = []

      for (const type of validTypes) {
        const urls = await fetchUrlsByType(locale, type, hostname)
        if (urls.length === 0) {
          continue
        }
        const chunkCount = getSitemapChunkCount(urls.length)
        const lastmod = new Date().toISOString()

        for (let chunk = 1; chunk <= chunkCount; chunk += 1) {
          entries.push({ type, chunk, lastmod })
        }
      }

      const maxLocPerIndex = 50000
      if (entries.length > maxLocPerIndex) {
        console.error(`[Sitemap] 语言索引超限 locale=${locale}, entries=${entries.length}`)
        return new NextResponse('语言索引超出限制', { status: 500 })
      }

      const xml = generateLocaleIndexWithEntries(locale, hostname, entries)
      return new NextResponse(xml, {
        headers: {
          'Content-Type': 'application/xml; charset=utf-8',
          'Cache-Control': getSitemapCacheControl(),
        },
      })
    } catch (error) {
      console.error('生成语言索引失败:', error)
      return new NextResponse('生成 sitemap 失败', { status: 500 })
    }
  }
  
  // /api/sitemap/zh-TW/guides -> ['zh-TW', 'guides']
  // /api/sitemap/zh-TW/guides/2 -> ['zh-TW', 'guides', '2']
  if (segments.length === 2 || segments.length === 3) {
    const [locale, type, chunkRaw] = segments
    const chunk = chunkRaw ? Number.parseInt(chunkRaw, 10) : 1
    
    if (!supportedLocales.includes(locale as any)) {
      return new NextResponse('不支持的语言', { status: 404 })
    }
    if (!validTypes.includes(type as ContentType)) {
      return new NextResponse('不支持的内容类型', { status: 404 })
    }
    if (!Number.isInteger(chunk) || chunk < 1) {
      return new NextResponse('无效的分片编号', { status: 404 })
    }
    
    try {
      const urls = await fetchUrlsByType(locale, type as ContentType, hostname)
      if (urls.length === 0) {
        return new NextResponse('分片不存在', { status: 404 })
      }
      const chunkCount = getSitemapChunkCount(urls.length)

      if (chunk > chunkCount) {
        return new NextResponse('分片不存在', { status: 404 })
      }

      const chunkedUrls = sliceUrlsByChunk(urls, chunk)
      const xml = generateContentSitemap(chunkedUrls, locale, type as ContentType)
      
      return new NextResponse(xml, {
        headers: {
          'Content-Type': 'application/xml; charset=utf-8',
          'Cache-Control': getSitemapCacheControl(),
        },
      })
    } catch (error) {
      console.error('生成内容 sitemap 失败:', error)
      return new NextResponse('生成 sitemap 失败', { status: 500 })
    }
  }
  
  return new NextResponse('Not Found', { status: 404 })
}
