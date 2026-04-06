/**
 * 降级 Metadata 配置
 * 当无法从后端 API 获取网站配置时使用
 * 
 * 注意：优先使用后端 /api/public/site-config 接口的数据
 * 这些配置仅作为备用方案
 */

import type { LocalizedString } from '../types/common'
import type { Locale } from '../types'

interface FallbackMetadata {
  title: LocalizedString
  description: LocalizedString
  keywords?: Record<string, string>
}

/**
 * Metadata 模板配置（用于生成列表页 metadata）
 */
export const metadataTemplates: Record<Locale, Record<string, { titleSuffix: string; descPrefix: string; featuredContent: string }>> = {
  'zh-CN': {
    games: { titleSuffix: ' - 游戏库', descPrefix: '浏览', featuredContent: '精选内容' },
    boxes: { titleSuffix: ' - 游戏盒子', descPrefix: '发现', featuredContent: '精选内容' },
    strategy: { titleSuffix: ' - 游戏攻略', descPrefix: '查看', featuredContent: '精选内容' },
    article: { titleSuffix: ' - 游戏资讯', descPrefix: '阅读', featuredContent: '精选文章' },
  },
  'zh-TW': {
    games: { titleSuffix: ' - 遊戲庫', descPrefix: '瀏覽', featuredContent: '精選內容' },
    boxes: { titleSuffix: ' - 遊戲盒子', descPrefix: '發現', featuredContent: '精選內容' },
    strategy: { titleSuffix: ' - 遊戲攻略', descPrefix: '查看', featuredContent: '精選內容' },
    article: { titleSuffix: ' - 遊戲資訊', descPrefix: '閱讀', featuredContent: '精選文章' },
  },
}

/**
 * 所有页面的降级 Metadata 配置
 */
export const fallbackMetadata = {
  /** 首页 */
  home: {
    title: {
      'zh-CN': '卡牌游戏攻略站 - 热门卡牌手游阵容与开荒指南',
      'zh-TW': '卡牌遊戲攻略站 - 熱門卡牌手遊陣容與開荒指南',
    },
    description: {
      'zh-CN': '聚焦卡牌手游攻略，提供阵容搭配、开荒思路、版本强度与礼包福利，帮助你更快上手并持续提升战力。',
      'zh-TW': '聚焦卡牌手遊攻略，提供陣容搭配、開荒思路、版本強度與禮包福利，幫助你更快上手並持續提升戰力。',
    },
  } as FallbackMetadata,

  /** 游戏列表页 */
  games: {
    title: {
      'zh-CN': '游戏大全 - 精选优质手游',
      'zh-TW': '遊戲大全 - 精選優質手遊',
    },
    description: {
      'zh-CN': '汇集最热门的手机游戏，涵盖RPG、动作、策略等各类游戏',
      'zh-TW': '匯集最熱門的手機遊戲，涵蓋RPG、動作、策略等各類遊戲',
    },
    keywords: {
      'zh-CN': '手机游戏,手游大全,游戏推荐,热门游戏',
      'zh-TW': '手機遊戲,手遊大全,遊戲推薦,熱門遊戲',
    },
  } as FallbackMetadata,

  /** 游戏盒子列表页 */
  boxes: {
    title: {
      'zh-CN': '游戏盒子大全 - 50+ 主流游戏盒子对比',
      'zh-TW': '遊戲盒子大全 - 50+ 主流遊戲盒子對比',
    },
    description: {
      'zh-CN': '汇集主流游戏盒子，提供详细的折扣对比、游戏数量、用户评价等信息',
      'zh-TW': '匯集主流遊戲盒子，提供詳細的折扣對比、遊戲數量、用戶評價等信息',
    },
    keywords: {
      'zh-CN': '游戏盒子,手游盒子,折扣平台,游戏平台',
      'zh-TW': '遊戲盒子,手遊盒子,折扣平台,遊戲平台',
    },
  } as FallbackMetadata,

  /** 攻略列表页 */
  strategy: {
    title: {
      'zh-CN': '游戏攻略大全',
      'zh-TW': '遊戲攻略大全',
    },
    description: {
      'zh-CN': '最全面的游戏攻略合集，包含RPG、动作、策略等各类游戏的详细攻略指南',
      'zh-TW': '最全面的遊戲攻略合集，包含RPG、動作、策略等各類遊戲的詳細攻略指南',
    },
    keywords: {
      'zh-CN': '游戏攻略,新手攻略,游戏指南,通关攻略',
      'zh-TW': '遊戲攻略,新手攻略,遊戲指南,通關攻略',
    },
  } as FallbackMetadata,

  /** 搜索页 */
  search: {
    title: {
      'zh-CN': '搜索',
      'zh-TW': '搜尋',
    },
    description: {
      'zh-CN': '搜索游戏、攻略、资讯等内容',
      'zh-TW': '搜尋遊戲、攻略、資訊等內容',
    },
    keywords: {
      'zh-CN': '搜索,游戏搜索,攻略搜索',
      'zh-TW': '搜尋,遊戲搜尋,攻略搜尋',
    }
  } as FallbackMetadata,

  /** 文章列表页 */
  article: {
    title: {
      'zh-CN': '游戏资讯 - 最新游戏文章',
      'zh-TW': '遊戲資訊 - 最新遊戲文章',
    },
    description: {
      'zh-CN': '精选游戏资讯文章，涵盖游戏评测、行业动态、玩家心得等优质内容',
      'zh-TW': '精選遊戲資訊文章，涵蓋遊戲評測、行業動態、玩家心得等優質內容',
    },
    keywords: {
      'zh-CN': '游戏资讯,游戏新闻,游戏评测,游戏文章',
      'zh-TW': '遊戲資訊,遊戲新聞,遊戲評測,遊戲文章',
    },
  } as FallbackMetadata,
}
