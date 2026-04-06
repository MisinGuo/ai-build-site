import type { Metadata, Viewport } from 'next'
import { Noto_Sans_SC } from 'next/font/google'
import { Suspense } from 'react'
import { WebVitals } from '@/components/common/WebVitals'
import './globals.css'
import { Header } from '@/components/layout/Header'
import { Footer } from '@/components/layout/Footer'
import { Toaster } from '@/components/ui/sonner'
import { LocaleProvider } from '@/contexts/LocaleContext'
import { NavigationTracker } from '@/components/common/NavigationTracker'
import { siteConfig } from '@/config'
import { defaultLocale } from '@/config/site/locales'
import { backendConfig } from '@/config/api/backend'
import { siteTheme, buildThemeCSSVars } from '@/lib/site-config'
import ApiClient from '@/lib/api'

const notoSansSC = Noto_Sans_SC({
  subsets: ['latin'],
  weight: ['400', '500', '700'],
  display: 'swap',
  preload: true,
  variable: '--font-noto-sans-sc',
})

export async function generateMetadata(): Promise<Metadata> {
  // 从后端 site-config 接口获取站点信息，失败时降级到本地静态配置
  let siteName = siteConfig.name
  let siteDescription = siteConfig.description
  let siteKeywords = siteConfig.keywords

  try {
    const res = await ApiClient.getSiteConfig({ locale: defaultLocale })
    const data = res?.data
    if (data) {
      siteName = data.seoTitle || data.name || siteName
      siteDescription = data.seoDescription || data.description || siteDescription
      if (data.seoKeywords) {
        siteKeywords = data.seoKeywords.split(',').map((k: string) => k.trim())
      }
    }
  } catch {
    // 降级到静态配置，无需处理错误
  }

  return {
    metadataBase: new URL(siteConfig.hostname),
    title: {
      default: siteName,
      template: `%s | ${siteName}`
    },
    description: siteDescription,
    keywords: siteKeywords,
    openGraph: {
      type: 'website',
      locale: 'zh_CN',
      alternateLocale: ['zh_TW'],
      siteName: siteName,
      images: [{ url: siteConfig.ogImage || '/logo.png', width: 1200, height: 630 }],
    },
  }
}

export const viewport: Viewport = {
  width: 'device-width',
  initialScale: 1,
  themeColor: '#0a0a14',
}

export default function RootLayout({
  children,
}: {
  children: React.ReactNode
}) {
  const apiOrigin = (() => { try { return new URL(backendConfig.baseURL).origin } catch { return null } })()
  const themeCSSVars = buildThemeCSSVars(siteTheme)

  return (
    <html lang={defaultLocale} style={themeCSSVars}>
      <head>
        {apiOrigin && <link rel="preconnect" href={apiOrigin} />}
        {apiOrigin && <link rel="dns-prefetch" href={apiOrigin} />}
      </head>
      <body
        className={`${notoSansSC.variable} min-h-screen flex flex-col text-[#e8e8f0] antialiased`}
        style={{ background: 'linear-gradient(180deg, #0a0a14 0%, #090914 40%, #080810 100%)' }}
      >
        <LocaleProvider>
          <Header />
          <main className="flex-1">
            {children}
          </main>
          <Footer />
          <Toaster />
          <Suspense fallback={null}>
            <NavigationTracker />
          </Suspense>
          <WebVitals />
        </LocaleProvider>
      </body>
    </html>
  )
}
