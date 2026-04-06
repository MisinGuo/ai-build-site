/**
 * 评测页配置
 * 路由: /reviews
 */

import type { ReviewsPageConfig } from '../../types/pages/reviews'
export type { ReviewsPageConfig }

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

const defaultReviewSectionSlugs = ['review-section'] as const

export const reviewSectionSlugs = readSectionSlugs(
  'NEXT_PUBLIC_SECTION_SLUGS_REVIEWS',
  defaultReviewSectionSlugs
)

export const reviewsConfig: ReviewsPageConfig = {
  route: '/reviews',
  sectionSlugs: reviewSectionSlugs,
}
