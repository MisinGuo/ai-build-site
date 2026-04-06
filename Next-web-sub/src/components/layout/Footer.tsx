'use client'

import Link from 'next/link'
import Image from 'next/image'
import { ChevronRight, ExternalLink } from 'lucide-react'
import { useTranslation } from '@/hooks/useTranslation'
import { useLocale } from '@/contexts/LocaleContext'
import { siteConfig } from '@/config'
import siteConf from '@/config/customize/site'

export function Footer() {
  const t = useTranslation()
  const { locale } = useLocale()
  const mainSiteLabel = locale === 'zh-TW' ? '返回主站' : '返回主站'
  const navItems = siteConf.navigation.header
    .filter((item) => item.enabled)
    .map((item) => ({
      id: item.path,
      label: t(item.i18nKey) || item.label,
    }))
  const socialLinks = Object.entries(siteConf.branding.social ?? {})

  const getLocalePath = (path: string) => {
    if (locale === 'zh-CN') {
      return path
    }
    return `/${locale}${path}`
  }

  return (
    <footer style={{ background: '#07070f', borderTop: '1px solid rgba(139,92,246,0.2)' }}>
      <div
        className="py-12 px-4"
        style={{ background: `linear-gradient(135deg, rgba(var(--color-primary-rgb),0.15) 0%, rgba(var(--color-secondary-rgb),0.15) 100%)` }}
      >
        <div className="max-w-4xl mx-auto text-center">
          <p
            style={{
              color: 'var(--color-primary)',
              fontSize: '0.875rem',
              fontWeight: 600,
              textTransform: 'uppercase',
              letterSpacing: '0.1em',
            }}
          >
            {t('platformNavigation')}
          </p>
          <h2 className="mt-3 mb-2" style={{ fontSize: '1.75rem', fontWeight: 700, color: '#fff' }}>
            {t('siteName')}
          </h2>
          <p className="mb-8" style={{ color: '#9090a8' }}>
            {t('footerDescription')}<br />{t('footerDescription2')}
          </p>
          <div className="flex flex-wrap items-center justify-center gap-3">
            {navItems.length > 0 && (
              <Link
                href={getLocalePath(navItems[0].id)}
                className="inline-flex items-center gap-2 px-8 py-4 rounded-xl text-white transition-all duration-200 hover:opacity-90 hover:scale-105 active:scale-95"
                style={{ background: 'linear-gradient(135deg,var(--color-primary),var(--color-secondary))', fontWeight: 700, fontSize: '1rem' }}
              >
                <ExternalLink className="w-5 h-5" />
                {navItems[0].label}
                <ChevronRight className="w-5 h-5" />
              </Link>
            )}
            {siteConfig.mainSiteUrl && (
              <a
                href={siteConfig.mainSiteUrl}
                target="_blank"
                rel="noopener noreferrer"
                className="inline-flex items-center gap-2 px-6 py-3.5 rounded-xl transition-all duration-200 hover:opacity-90"
                style={{ color: 'var(--color-primary)', border: `1px solid rgba(var(--color-primary-rgb),0.35)`, background: 'rgba(10,10,20,0.35)', fontWeight: 600 }}
              >
                {mainSiteLabel}
                <ChevronRight className="w-4 h-4" />
              </a>
            )}
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 py-10">
        <div className="grid grid-cols-2 md:grid-cols-3 gap-8">
          <div>
            <Link href={getLocalePath('/')} className="flex items-center gap-2 mb-4">
              <Image src="/logo.png" alt={t('siteName')} width={48} height={48} className="object-contain" />
            </Link>
            <p style={{ color: '#606078', fontSize: '0.875rem', lineHeight: 1.7 }}>
              {t('footerDescription')}<br />{t('footerDescription2')}
            </p>
          </div>

          <div>
            <h4 style={{ color: 'var(--color-primary)', fontWeight: 600, marginBottom: '1rem', fontSize: '0.875rem' }}>{t('platformNavigation')}</h4>
            <ul className="space-y-2">
              {navItems.map((item) => (
                <li key={item.id}>
                  <Link href={getLocalePath(item.id)} style={{ color: '#8080a0', fontSize: '0.875rem' }} className="hover:text-purple-400 transition-colors">
                    {item.label}
                  </Link>
                </li>
              ))}
            </ul>
          </div>

          <div>
            <h4 style={{ color: 'var(--color-primary)', fontWeight: 600, marginBottom: '1rem', fontSize: '0.875rem' }}>{t('followUs')}</h4>
            <div className="space-y-3">
              {socialLinks.length > 0 ? socialLinks.slice(0, 3).map(([platform, url], index) => (
                <a
                  key={platform}
                  href={url}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="flex items-center gap-2 px-3 py-2 rounded-lg transition-all hover:opacity-80"
                  style={
                    index % 2 === 0
                      ? { background: `rgba(var(--color-primary-rgb),0.15)`, border: `1px solid rgba(var(--color-primary-rgb),0.3)` }
                      : { background: `rgba(var(--color-secondary-rgb),0.15)`, border: `1px solid rgba(var(--color-secondary-rgb),0.3)` }
                  }
                >
                  <ChevronRight className="w-4 h-4" style={{ color: index % 2 === 0 ? 'var(--color-primary)' : 'var(--color-secondary)' }} />
                  <span style={{ color: '#c4c4d4', fontSize: '0.875rem', textTransform: 'capitalize' }}>{platform}</span>
                </a>
              )) : navItems.slice(0, 2).map((item, index) => (
                <Link
                  key={item.id}
                  href={getLocalePath(item.id)}
                  className="flex items-center gap-2 px-3 py-2 rounded-lg transition-all hover:opacity-80"
                  style={
                    index % 2 === 0
                      ? { background: `rgba(var(--color-primary-rgb),0.15)`, border: `1px solid rgba(var(--color-primary-rgb),0.3)` }
                      : { background: `rgba(var(--color-secondary-rgb),0.15)`, border: `1px solid rgba(var(--color-secondary-rgb),0.3)` }
                  }
                >
                  <ChevronRight className="w-4 h-4" style={{ color: index % 2 === 0 ? 'var(--color-primary)' : 'var(--color-secondary)' }} />
                  <span style={{ color: '#c4c4d4', fontSize: '0.875rem' }}>{item.label}</span>
                </Link>
              ))}
            </div>
          </div>
        </div>
      </div>

      <div
        className="mt-10 pt-6 max-w-7xl mx-auto px-4 pb-8 flex flex-col md:flex-row items-center justify-between gap-4"
        style={{ borderTop: '1px solid rgba(255,255,255,0.05)' }}
      >
        <p style={{ color: '#404050', fontSize: '0.8rem' }}>
          {siteConf.branding.copyright}
        </p>
        <p style={{ color: '#404050', fontSize: '0.8rem' }}>
          {t('subscribeNewsletter')}
        </p>
      </div>
    </footer>
  )
}
