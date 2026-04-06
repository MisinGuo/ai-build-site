import type { Metadata } from 'next'
import { headers } from 'next/headers'
import Link from 'next/link'

export const metadata: Metadata = {
  title: '404 - 页面未找到 | Page Not Found',
  description: '抱歉，您访问的页面不存在。请返回首页继续探索。',
  robots: { index: false, follow: false },
}

const messages: Record<string, { title: string; description: string; backHome: string }> = {
  'zh-CN': { title: '页面未找到', description: '抱歉，您访问的页面不存在。', backHome: '返回首页' },
  'zh-TW': { title: '頁面未找到', description: '抱歉，您訪問的頁面不存在。', backHome: '返回首頁' },
  'en-US': { title: 'Page Not Found', description: 'Sorry, the page you visited does not exist.', backHome: 'Back to Home' },
}

export default async function RootNotFound() {
  const headersList = await headers()
  const locale = headersList.get('x-locale') ?? 'zh-CN'
  const t = messages[locale] ?? messages['zh-CN']
  const homeUrl = locale === 'zh-CN' ? '/' : `/${locale}`

  return (
    <html lang={locale === 'en-US' ? 'en' : locale === 'zh-TW' ? 'zh-TW' : 'zh-CN'}>
      <body style={{ margin: 0, fontFamily: 'sans-serif', background: '#0f172a', color: '#f1f5f9' }}>
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', minHeight: '100vh', gap: '16px', textAlign: 'center', padding: '24px' }}>
          <h1 style={{ fontSize: '4rem', fontWeight: 'bold', color: '#6366f1', margin: 0 }}>404</h1>
          <p style={{ fontSize: '1.25rem', margin: 0 }}>{t.title}</p>
          <p style={{ fontSize: '1rem', margin: 0, color: '#94a3b8' }}>{t.description}</p>
          <Link href={homeUrl} style={{ marginTop: '8px', padding: '10px 24px', background: '#6366f1', color: '#fff', borderRadius: '8px', textDecoration: 'none', fontWeight: 500 }}>
            {t.backHome}
          </Link>
        </div>
      </body>
    </html>
  )
}
