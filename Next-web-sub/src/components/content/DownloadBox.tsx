'use client'

import Link from 'next/link'
import { ArrowLeft, Download } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { useLocale } from '@/contexts/LocaleContext'
import { defaultLocale } from '@/config/site/locales'
import type { ModuleConfig } from '@/config'

interface DownloadBoxProps {
  config: ModuleConfig
  gameName?: string
}

/**
 * 下载入口组件
 * 根据模块配置决定是否显示以及显示样式
 */
export function DownloadBox({ config, gameName }: DownloadBoxProps) {
  const { locale } = useLocale()
  const lp = (path: string) => locale === defaultLocale ? path : `/${locale}${path}`

  if (!config.downloadEntry.enabled) {
    return null
  }

  const { buttonText, buttonLink, buttonStyle } = config.downloadEntry
  const localizedButtonText = locale === 'zh-TW'
    ? '查看遊戲盒子'
    : buttonText

  const buttonClasses = {
    primary: 'bg-blue-600 hover:bg-blue-700 text-white',
    secondary: 'bg-slate-800 hover:bg-slate-700 text-white border border-slate-700',
    gradient: 'bg-gradient-to-r from-orange-500 to-red-600 hover:from-orange-600 hover:to-red-700 text-white',
  }

  return (
    <div className="bg-gradient-to-br from-orange-500/10 to-red-500/10 border border-orange-500/20 rounded-lg p-6 my-6">
      <h3 className="text-lg font-bold text-white mb-2">
        {locale === 'zh-TW'
          ? (gameName ? `下載 ${gameName}` : '獲取遊戲福利')
          : (gameName ? `下载 ${gameName}` : '获取游戏福利')}
      </h3>
      <p className="text-sm text-slate-400 mb-4">
        {locale === 'zh-TW'
          ? '0.1折起 · 無限鑽石 · 滿V福利'
          : '0.1折起 · 无限钻石 · 满V福利'}
      </p>
      <Button 
        className={`w-full ${buttonClasses[buttonStyle]}`}
        asChild
      >
        <Link href={lp(buttonLink)}>
          <Download className="h-4 w-4 mr-2" />
          {localizedButtonText}
        </Link>
      </Button>
    </div>
  )
}

/**
 * 返回列表按钮
 */
export function BackToListButton({ config }: { config: ModuleConfig }) {
  const { locale } = useLocale()
  const lp = (path: string) => locale === defaultLocale ? path : `/${locale}${path}`
  const moduleTitle = locale === 'zh-TW'
    ? (config.type === 'reviews' ? '遊戲評測' : '遊戲攻略')
    : config.title

  const backText = `返回${moduleTitle}列表`

  return (
    <Link 
      href={lp(config.routePrefix)}
      className="inline-flex items-center text-slate-400 hover:text-white transition-colors"
    >
      <ArrowLeft className="h-4 w-4 mr-2" />
      {backText}
    </Link>
  )
}
