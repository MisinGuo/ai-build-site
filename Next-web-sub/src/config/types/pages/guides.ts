export interface GuidesPageConfig {
  /** 页面路由，固定为 '/guides' */
  route: '/guides'
  /** 攻略内容来源的 section slug 列表。
   * 可通过环境变量 NEXT_PUBLIC_SECTION_SLUGS_GUIDES（逗号分隔）视视视视在运行时覆盖。
   * 示例：'guide-section,featured-guides' */
  sectionSlugs: readonly string[]
}
