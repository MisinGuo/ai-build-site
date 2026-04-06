/**
 * 站点配置导出
 *
 * 直接 import site-config.ts，构建时打包进产物，运行时零 API 请求。
 * 环境变量仍可覆盖部分字段（hostname / mainSiteUrl / jumpDomain），
 * 方便本地开发与生产环境指向不同后端。
 */

import rawConfig from '@/config/customize/site'
import type { SiteConfigTheme } from '@/config/types/site'

export const siteConfigFile = rawConfig

/** 主题配置，供 layout 注入 CSS 变量使用 */
export const siteTheme: SiteConfigTheme = siteConfigFile.theme

/** 将十六进制颜色转为 "R, G, B" 字符串，用于 rgba(var(--color-xx-rgb), alpha) */
function hexToRgb(hex: string): string {
  const clean = hex.replace('#', '')
  const r = parseInt(clean.slice(0, 2), 16)
  const g = parseInt(clean.slice(2, 4), 16)
  const b = parseInt(clean.slice(4, 6), 16)
  return `${r}, ${g}, ${b}`
}

/**
 * 将 theme 配置转换为 CSS 变量对象，用于 <html> style 属性注入
 */
export function buildThemeCSSVars(theme: SiteConfigTheme): React.CSSProperties {
  return {
    '--color-primary': theme.primaryColor,
    '--color-primary-rgb': hexToRgb(theme.primaryColor),
    '--color-secondary': theme.secondaryColor,
    '--color-secondary-rgb': hexToRgb(theme.secondaryColor),
    '--color-accent': theme.accentColor,
    '--radius': theme.borderRadius,
  } as React.CSSProperties
}
