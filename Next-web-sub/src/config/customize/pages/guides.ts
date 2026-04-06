/**
 * 攻略页配置
 * 路由: /guides
 */

import type { GuidesPageConfig } from '../../types/pages/guides'
export type { GuidesPageConfig }

function parseSectionSlugEnv(input?: string): string[] {
  if (!input) return []
  return input
    .split(',')
    .map((item) => item.trim())
    .filter(Boolean)
}

function readSectionSlugs(envKey: string, fallback: readonly string[]): readonly string[] {
  const configured = parseSectionSlugEnv(process.env[envKey])
  return configured.length > 0 ? configured : fallback
}

const defaultGuideSectionSlugs = ['guide-section'] as const

export const guideSectionSlugs = readSectionSlugs(
  'NEXT_PUBLIC_SECTION_SLUGS_GUIDES',
  defaultGuideSectionSlugs
)

export const guidesConfig: GuidesPageConfig = {
  route: '/guides',
  sectionSlugs: guideSectionSlugs,
}
