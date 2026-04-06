import type { LocalizedString, LocalizedArray, LayoutType, DataLoadConfig } from '../common'

/** Hero 区域配置 */
export interface HeroConfig {
  /** 顶部小徽标文字，如"专业卡牌手游攻略平台" */
  badge: LocalizedString
  /** 主标题，如"卡牌手游攻略站" */
  title: LocalizedString
  /** 主标题下方的高亮副标题文字 */
  highlightText: LocalizedString
  /** 描述文字，支持多行（数组中每项为一行） */
  description: LocalizedArray<string>
  /** 主要行动按钮（实心样式） */
  primaryButton: {
    /** 按钮显示文字 */
    text: LocalizedString
    /** 按钮点击跳转路径 */
    href: string
  }
  /** 次要行动按钮（描边样式） */
  secondaryButton: {
    /** 按钮显示文字 */
    text: LocalizedString
    /** 按钮点击跳转路径 */
    href: string
  }
}

/** 统计数据项，显示在 Hero 下方的数字亮点区 */
export interface StatItem {
  /** 数字说明标签，如"卡牌游戏" */
  label: LocalizedString
  /** 显示的数值文字，如"160+" */
  value: string
  /** Lucide 图标名称，如 'Download'、'Flame'、'Gift' */
  icon: string
  /** 后端数据字段映射 key，有值时用后端返回的数字动态替换 value */
  dataKey?: 'boxCount' | 'gameCount' | 'articleCount' | 'totalSavings'
  /** 是否显示此统计项，false 则隐藏 */
  visible?: boolean | Record<string, boolean>
}

/** 内容区块（攻略列表、游戏列表等）的标题 / 更多链接配置 */
export interface ContentSectionConfig {
  /** 区块标题 */
  title: LocalizedString
  /** 区块副标题描述 */
  description: LocalizedString
  /** 右上角"更多"链接，不填则不显示 */
  moreLink?: {
    /** 链接文字 */
    text: LocalizedString
    /** 链接路径 */
    href: string
  }
  /** 区块左上角徽标文字（可选） */
  badge?: LocalizedString
  /** 卡片布局模式，如 'grid-3'、'grid-6'、'list' */
  layout?: LayoutType
}

/** 首页功能亮点卡片（展示核心能力的四宫格卡片） */
export interface FeatureCardConfig {
  /** 卡片标题 */
  title: LocalizedString
  /** 卡片描述文字 */
  desc: LocalizedString
  /** 卡片图标，使用 Lucide 固定图标名 */
  icon: 'BookOpen' | 'Star' | 'Gift' | 'Shield'
  /** 图标背景色或图标颜色，十六进制，如 '#8b5cf6' */
  color: string
}

/** 礼包预览卡片数据（静态展示用，非实时礼包数据） */
export interface GiftPreviewConfig {
  /** 唯一 ID */
  id: number
  /** 礼包名称 */
  title: LocalizedString
  /** 礼包价值描述，如"价值 688 元" */
  value: LocalizedString
  /** 所属游戏名称 */
  gameName: LocalizedString
  /** 封面图路径，相对于 public 目录 */
  image: string
  /** 剩余领取数量（可选，不填则不显示） */
  remaining?: number
  /** 有效期截止日期，格式 'YYYY-MM-DD'（可选） */
  expiry?: string
}

/** 首页通用 UI 文案配置 */
export interface HomeTextConfig {
  /** 游戏分类标签兜底文字（API 未返回分类名时显示） */
  categoryFallbackLabel: LocalizedString
  /** 攻略分类标签兜底文字 */
  guideCategoryFallbackLabel: LocalizedString
  /** 热门攻略区块副标题，如"每日更新 · 深度内容" */
  hotGuidesSub: LocalizedString
  /** 热门游戏区块副标题 */
  hotGamesSub: LocalizedString
  /** 礼包区块副标题 */
  giftSub: LocalizedString
  /** 礼包预览区块标题 */
  giftSectionTitle: LocalizedString
  /** 礼包区块"更多"链接文字 */
  giftMoreLinkText: LocalizedString
  /** 卡片"查看详情"按钮文字 */
  viewDetailText: LocalizedString
  /** 通用"查看"文字 */
  viewText: LocalizedString
  /** 礼包剩余数量前缀文字，如"剩余" */
  remainingText: LocalizedString
  /** 限时礼包标签文字 */
  limitedGiftText: LocalizedString
  /** 立即领取按钮文字 */
  claimNowText: LocalizedString
  /** 盒子含专属礼包徽标文字 */
  boxGiftBadge?: LocalizedString
}

/** 首页完整配置 */
export interface HomePageConfig {
  /** Hero 背景图路径，相对于 public 目录，如 '/images/hero-bg.jpg' */
  heroBackgroundImage: string
  /** Hero 区域内容配置 */
  hero: HeroConfig
  /** Hero 下方统计数字列表 */
  stats: StatItem[]
  /** 页面内通用文案 */
  ui: HomeTextConfig
  /** 功能亮点四宫格卡片列表 */
  featureCards: FeatureCardConfig[]
  /** 礼包预览卡片列表（静态展示） */
  giftPreviews: GiftPreviewConfig[]
  /** 内容列表区块配置 */
  sections: {
    /** 最新攻略列表区块 */
    guides: ContentSectionConfig
    /** 热门游戏列表区块 */
    games: ContentSectionConfig
  }
  /** 数据加载参数 */
  data: DataLoadConfig & {
    /** 首页加载的攻略文章数量 */
    guidesCount: number
    /** 首页加载的游戏数量 */
    gamesCount: number
  }
}
