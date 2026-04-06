/**
 * 首页配置
 * 路由: /
 */

import type {
  HomePageConfig,
  HeroConfig,
  StatItem,
  ContentSectionConfig,
  FeatureCardConfig,
  GiftPreviewConfig,
  HomeTextConfig,
} from '../../types/pages/home'

export type {
  HomePageConfig,
  HeroConfig,
  StatItem,
  ContentSectionConfig,
  FeatureCardConfig,
  GiftPreviewConfig,
  HomeTextConfig,
}

export const homeConfig: HomePageConfig = {
  heroBackgroundImage: '/images/default-cover.svg',

  // Hero区域
  hero: {
    badge: {
      'zh-CN': '专业卡牌手游攻略平台',
      'zh-TW': '專業卡牌手遊攻略平台',
    },
    title: {
      'zh-CN': '卡牌手游攻略站',
      'zh-TW': '卡牌手遊攻略站',
    },
    highlightText: {
      'zh-CN': '精选三国、西游、忍者、二次元卡牌手游深度攻略与游戏评测',
      'zh-TW': '精選三國、西遊、忍者、二次元卡牌手遊深度攻略與遊戲評測',
    },
    description: {
      'zh-CN': [
        '覆盖热门卡牌手游的阵容搭配、养成路线与版本思路。',
        '同步礼包资讯与实战评测，少走弯路。',
      ],
      'zh-TW': [
        '覆蓋熱門卡牌手遊的陣容搭配、養成路線與版本思路。',
        '同步禮包資訊與實戰評測，少走彎路。',
      ],
    },
    primaryButton: {
      text: {
        'zh-CN': '查看热门评测',
        'zh-TW': '查看熱門評測',
      },
      href: '/reviews',
    },
    secondaryButton: {
      text: {
        'zh-CN': '浏览攻略',
        'zh-TW': '瀏覽攻略',
      },
      href: '/guides',
    },
  },

  // 统计数据（支持多语言和后端数据映射）
  stats: [
    {
      label: {
        'zh-CN': '卡牌游戏',
        'zh-TW': '卡牌遊戲',
      },
      value: '160+',
      icon: 'Download',
      visible: true,
    },
    {
      label: {
        'zh-CN': '礼包福利',
        'zh-TW': '禮包福利',
      },
      value: '0.1折',
      icon: 'Flame',
      visible: true,
    },
    {
      label: {
        'zh-CN': '玩家领取',
        'zh-TW': '玩家領取',
      },
      value: '300万+',
      icon: 'BookOpen',
      visible: true,
    },
    {
      label: {
        'zh-CN': '累计省钱',
        'zh-TW': '累計省錢',
      },
      value: '¥5000W',
      icon: 'Gift',
      dataKey: 'totalSavings',
    },
  ],

  ui: {
    categoryFallbackLabel: {
      'zh-CN': '分类',
      'zh-TW': '分類',
    },
    guideCategoryFallbackLabel: {
      'zh-CN': '攻略',
      'zh-TW': '攻略',
    },
    hotGuidesSub: {
      'zh-CN': '每日更新 · 深度内容',
      'zh-TW': '每日更新 · 深度內容',
    },
    hotGamesSub: {
      'zh-CN': '三国·西游·忍者 · 每日精选',
      'zh-TW': '三國·西遊·忍者 · 每日精選',
    },
    giftSub: {
      'zh-CN': '每个盒子含专属礼包 · 前往领取',
      'zh-TW': '每個盒子含專屬禮包 · 前往領取',
    },
    giftSectionTitle: {
      'zh-CN': '盒子专属礼包',
      'zh-TW': '盒子專屬禮包',
    },
    giftMoreLinkText: {
      'zh-CN': '前往主站领取',
      'zh-TW': '前往主站領取',
    },
    viewDetailText: {
      'zh-CN': '查看详情',
      'zh-TW': '查看詳情',
    },
    viewText: {
      'zh-CN': '查看',
      'zh-TW': '查看',
    },
    remainingText: {
      'zh-CN': '剩余',
      'zh-TW': '剩餘',
    },
    limitedGiftText: {
      'zh-CN': '限时礼包',
      'zh-TW': '限時禮包',
    },
    claimNowText: {
      'zh-CN': '前往领取',
      'zh-TW': '前往領取',
    },
    boxGiftBadge: {
      'zh-CN': '含专属礼包',
      'zh-TW': '含專屬禮包',
    },
  },

  featureCards: [
    {
      icon: 'BookOpen',
      color: '#8b5cf6',
      title: {
        'zh-CN': '热门攻略',
        'zh-TW': '熱門攻略',
      },
      desc: {
        'zh-CN': '每日更新实战攻略，数据透明可追溯。',
        'zh-TW': '每日更新實戰攻略，數據透明可追溯。',
      },
    },
    {
      icon: 'Star',
      color: '#f59e0b',
      title: {
        'zh-CN': '折扣对比',
        'zh-TW': '折扣對比',
      },
      desc: {
        'zh-CN': '跨盒子平台对比价格与版本福利。',
        'zh-TW': '跨盒子平台對比價格與版本福利。',
      },
    },
    {
      icon: 'Gift',
      color: '#ef4444',
      title: {
        'zh-CN': '礼包聚合',
        'zh-TW': '禮包聚合',
      },
      desc: {
        'zh-CN': '汇总可领礼包与有效期，避免错过。',
        'zh-TW': '彙總可領禮包與有效期，避免錯過。',
      },
    },
    {
      icon: 'Shield',
      color: '#06b6d4',
      title: {
        'zh-CN': '安全可信',
        'zh-TW': '安全可信',
      },
      desc: {
        'zh-CN': '中立数据与来源标注，选择更放心。',
        'zh-TW': '中立數據與來源標註，選擇更放心。',
      },
    },
  ],

  giftPreviews: [
    {
      id: 1,
      title: {
        'zh-CN': '开服成长礼包',
        'zh-TW': '開服成長禮包',
      },
      value: {
        'zh-CN': '价值 688 元',
        'zh-TW': '價值 688 元',
      },
      gameName: {
        'zh-CN': '三国战纪',
        'zh-TW': '三國戰紀',
      },
      image: '/images/default-cover.svg',
      remaining: 286,
      expiry: '2026-12-31',
    },
    {
      id: 2,
      title: {
        'zh-CN': '至尊召唤礼包',
        'zh-TW': '至尊召喚禮包',
      },
      value: {
        'zh-CN': '限时福利',
        'zh-TW': '限時福利',
      },
      gameName: {
        'zh-CN': '西游传说',
        'zh-TW': '西遊傳說',
      },
      image: '/images/default-cover.svg',
      remaining: 129,
      expiry: '2026-10-31',
    },
    {
      id: 3,
      title: {
        'zh-CN': '战力冲榜礼包',
        'zh-TW': '戰力衝榜禮包',
      },
      value: {
        'zh-CN': '独家专属',
        'zh-TW': '獨家專屬',
      },
      gameName: {
        'zh-CN': '忍界对决',
        'zh-TW': '忍界對決',
      },
      image: '/images/default-cover.svg',
      remaining: 57,
      expiry: '2026-09-30',
    },
  ],

  // 内容区块
  sections: {
    // 攻略区块
    guides: {
      title: {
        'zh-CN': '最新游戏攻略',
        'zh-TW': '最新遊戲攻略',
      },
      description: {
        'zh-CN': '深度的游戏解析，帮你快速上手',
        'zh-TW': '深度的遊戲解析，幫你快速上手',
      },
      moreLink: {
        text: {
          'zh-CN': '全部攻略',
          'zh-TW': '全部攻略',
        },
        href: '/guides',
      },
      badge: {
        'zh-CN': '攻略',
        'zh-TW': '攻略',
      },
      layout: 'grid-3',
    },
    // 特价游戏区块
    games: {
      title: {
        'zh-CN': '热门游戏',
        'zh-TW': '熱門遊戲',
      },
      description: {
        'zh-CN': '超低折扣，限时福利版本',
        'zh-TW': '超低折扣，限時福利版本',
      },
      moreLink: {
        text: {
          'zh-CN': '更多游戏',
          'zh-TW': '更多遊戲',
        },
        href: '/games',
      },
      badge: {
        'zh-CN': '0.1折',
        'zh-TW': '0.1折',
      },
      layout: 'grid-6',
    },
  },

  // 数据配置
  data: {
    guidesCount: 6,
    gamesCount: 6,
    enableCache: true,
    cacheTime: 1800, // 30分钟
  },
}
