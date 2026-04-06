import type { Metadata } from 'next'
import Link from 'next/link'
import { headers } from 'next/headers'
import { Button } from '@/components/ui/button'

export const metadata: Metadata = { title: 'Not Found' }

const messages: Record<string, {
  title: string
  description: string
  backHome: string
  browseGames: string
  browseGuides: string
  gamesLabel: string
  guidesLabel: string
}> = {
  'zh-CN': {
    title: '页面未找到',
    description: '您访问的页面不存在或已被移除。',
    backHome: '返回首页',
    browseGames: '浏览游戏',
    browseGuides: '浏览攻略',
    gamesLabel: '热门游戏',
    guidesLabel: '游戏攻略',
  },
  'zh-TW': {
    title: '頁面未找到',
    description: '您訪問的頁面不存在或已被移除。',
    backHome: '返回首頁',
    browseGames: '瀏覽遊戲',
    browseGuides: '瀏覽攻略',
    gamesLabel: '熱門遊戲',
    guidesLabel: '遊戲攻略',
  },
}

// not-found.tsx 在 [locale] 段下无法接收 params，
// 通过 x-locale header（由 middleware 注入）识别当前语言
export default async function NotFound() {
  const headersList = await headers()
  const locale = headersList.get('x-locale') ?? 'zh-CN'
  const t = messages[locale] ?? messages['zh-CN']
  const prefix = locale === 'zh-CN' ? '' : `/${locale}`
  const home = `${prefix}/`
  const games = `${prefix}/games`
  const guides = `${prefix}/guides`

  return (
    <div className="min-h-screen bg-slate-950 flex items-center justify-center px-4">
      <div className="text-center max-w-md">
        {/* 404 数字 */}
        <div className="text-8xl font-bold text-slate-700 mb-4 select-none">404</div>

        {/* 标题 */}
        <h1 className="text-2xl font-bold text-white mb-3">{t.title}</h1>

        {/* 描述 */}
        <p className="text-slate-400 mb-8">{t.description}</p>

        {/* 操作按钮组 */}
        <div className="flex flex-col sm:flex-row gap-3 justify-center">
          <Button asChild size="lg">
            <Link href={home}>{t.backHome}</Link>
          </Button>
          <Button asChild variant="outline" size="lg">
            <Link href={games}>{t.browseGames}</Link>
          </Button>
          <Button asChild variant="ghost" size="lg">
            <Link href={guides}>{t.browseGuides}</Link>
          </Button>
        </div>
      </div>
    </div>
  )
}
