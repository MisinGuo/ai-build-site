export interface ReviewsPageConfig {
  /** 页面路由，固定为 '/reviews' */
  route: '/reviews'
  /** 评测内容来源的 section slug 列表。
   * 可通过环境变量 NEXT_PUBLIC_SECTION_SLUGS_REVIEWS（逗号分隔）在运行时覆盖。
   * 示例：'review-section,weekly-reviews' */
  sectionSlugs: readonly string[]
}
