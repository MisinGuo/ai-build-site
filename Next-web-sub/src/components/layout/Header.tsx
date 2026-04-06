'use client'

import { useState } from 'react'
import Link from 'next/link'
import Image from 'next/image'
import { usePathname } from 'next/navigation'
import { Menu, X, ExternalLink, Languages } from 'lucide-react'
import { useTranslation } from '@/hooks/useTranslation'
import { useLocale } from '@/contexts/LocaleContext'
import { defaultLocale, supportedLocales } from '@/config/site/locales'
import { siteConfig } from '@/config'
import siteConf from '@/config/customize/site'

export function Header() {
  const [mobileOpen, setMobileOpen] = useState(false)
  const pathname = usePathname()
  const t = useTranslation()
  const { locale, setLocale } = useLocale()

  const routeNavItems = siteConf.navigation.header
    .filter((item) => item.enabled)
    .map((item) => ({
      id: item.path,
      label: t(item.i18nKey) || item.label,
    }))

  const getLocalizedUrl = (path: string) => {
    if (locale === defaultLocale) {
      return path
    }
    return `/${locale}${path}`
  }

  const switchLocale = () => {
    const idx = supportedLocales.indexOf(locale)
    const next = supportedLocales[(idx + 1) % supportedLocales.length]
    setLocale(next)
  }

  const isActive = (path: string) => {
    let cleanPathname = pathname

    for (const configuredLocale of supportedLocales) {
      if (configuredLocale === defaultLocale) continue
      if (pathname.startsWith(`/${configuredLocale}/`) || pathname === `/${configuredLocale}`) {
        cleanPathname = pathname.slice(`/${configuredLocale}`.length) || '/'
        break
      }
    }

    if (path === '/') return cleanPathname === '/' || cleanPathname === ''
    return cleanPathname.startsWith(path)
  }

  return (
    <header
      className="sticky top-0 z-50 backdrop-blur-md"
      style={{ background: 'rgba(10,10,20,0.92)', borderBottom: `1px solid rgba(var(--color-primary-rgb),0.25)` }}
    >
      <div className="max-w-7xl mx-auto px-4 flex h-16 items-center justify-between">
        <Link
          href={getLocalizedUrl('/')}
          className="flex items-center group"
        >
          <Image src="/logo.png" alt={t('siteName')} width={40} height={40} className="object-contain" />
        </Link>

        <nav className="hidden md:flex items-center gap-1">
          {routeNavItems.map((item) => (
            <Link
              key={item.id}
              href={getLocalizedUrl(item.id)}
              className="px-4 py-2 rounded-lg transition-all duration-200"
              style={{
                color: isActive(item.id) ? 'var(--color-primary)' : '#c4c4d4',
                background: isActive(item.id) ? `rgba(var(--color-primary-rgb),0.15)` : 'transparent',
                fontWeight: isActive(item.id) ? 600 : 400,
              }}
            >
              {item.label}
            </Link>
          ))}
        </nav>

        <div className="hidden md:flex items-center gap-3">
          <button
            onClick={switchLocale}
            className="flex items-center gap-1 px-3 py-1.5 rounded-lg transition-all duration-200 hover:opacity-80"
            style={{ color: 'var(--color-primary)', border: `1px solid rgba(var(--color-primary-rgb),0.4)`, background: 'transparent', fontSize: '0.8rem' }}
          >
            <Languages className="w-3.5 h-3.5" />
            {locale}
          </button>

          {siteConfig.mainSiteUrl && (
            <a
              href={siteConfig.mainSiteUrl}
              target="_blank"
              rel="noopener noreferrer"
              className="flex items-center gap-1 text-sm transition-opacity hover:opacity-70"
              style={{ color: '#60607a' }}
            >
              主站
              <ExternalLink className="w-3 h-3 ml-0.5" />
            </a>
          )}
        </div>

        <div className="md:hidden flex items-center gap-1">
          <button
            className="p-2 rounded-lg"
            style={{ color: '#a78bfa' }}
            onClick={() => setMobileOpen(!mobileOpen)}
            aria-label="toggle-menu"
          >
            {mobileOpen ? <X className="w-5 h-5" /> : <Menu className="w-5 h-5" />}
          </button>
        </div>
      </div>

      {mobileOpen && (
        <div className="md:hidden px-4 pb-4 flex flex-col gap-1" style={{ borderTop: '1px solid rgba(139,92,246,0.2)' }}>
          {routeNavItems.map((item) => (
            <Link
              key={item.id}
              href={getLocalizedUrl(item.id)}
              onClick={() => setMobileOpen(false)}
              className="px-4 py-3 rounded-lg"
              style={{ color: isActive(item.id) ? 'var(--color-primary)' : '#c4c4d4',
                background: isActive(item.id) ? `rgba(var(--color-primary-rgb),0.15)` : 'transparent' }}
            >
              {item.label}
            </Link>
          ))}
          <button
            onClick={() => {
              switchLocale()
              setMobileOpen(false)
            }}
            className="mt-1 flex items-center gap-1.5 px-4 py-2.5 rounded-lg"
            style={{ color: 'var(--color-primary)', border: `1px solid rgba(var(--color-primary-rgb),0.4)`, background: 'transparent' }}
          >
            <Languages className="w-4 h-4" />
            {locale}
          </button>
          {siteConfig.mainSiteUrl && (
            <a
              href={siteConfig.mainSiteUrl}
              target="_blank"
              rel="noopener noreferrer"
              className="mt-1 flex items-center gap-1.5 px-4 py-2.5 rounded-lg"
              style={{ color: '#606080', fontSize: '0.875rem' }}
            >
              前往主站
              <ExternalLink className="w-4 h-4" />
            </a>
          )}
        </div>
      )}
    </header>
  )
}