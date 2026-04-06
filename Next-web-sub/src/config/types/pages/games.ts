import type { LocalizedString } from '../common'

export interface GamesPageConfig {
  /** Hero 区域内容配置 */
  hero: {
    /** 页面主标题 */
    title: LocalizedString
    /** 页面副标题描述 */
    description: LocalizedString
    /** 标题旁的小徽标文字 */
    badge: LocalizedString
  }
  /** 筛选器配置 */
  filter: {
    /** 是否开启筛选功能 */
    enabled: boolean
    /** 筛选项列表 */
    filters: Array<{
      /** 筛选字段标识符，对应 API 返回的字段名 */
      key: string
      /** 筛选项展示标签 */
      label: LocalizedString
      /** 筛选控件类型：select 下拉 / checkbox 多选 */
      type: 'select' | 'checkbox'
      /** 可选项列表 */
      options: Array<{
        /** 选项展示文字 */
        label: LocalizedString
        /** 选项实际传参値 */
        value: string
      }>
    }>
  }
  /** 排序配置 */
  sort: {
    /** 是否开启排序功能 */
    enabled: boolean
    /** 默认排序的 value 字符串 */
    defaultSort: string
    /** 排序选项列表 */
    options: Array<{
      /** 选项展示文字 */
      label: LocalizedString
      /** 选项实际传参値 */
      value: string
    }>
  }
  /** 分页配置 */
  pagination: {
    /** 默认每页条数 */
    pageSize: number
    /** 是否显示每页条数切换器 */
    showSizeChanger: boolean
    /** 可选每页条数列表 */
    pageSizeOptions: number[]
  }
  /** 卡片显示字段控制 */
  card: {
    /** 是否显示封面图 */
    showCover: boolean
    /** 是否显示分类标签 */
    showCategory: boolean
    /** 是否显示发布日期 */
    showDate: boolean
    /** 是否显示浏览数 */
    showViews: boolean
    /** 是否显示预读时长 */
    showReadingTime: boolean
  }
  /** 页面内各处 UI 文案 */
  ui: {
    /** 游戏分类标题文字 */
    gameCategories: LocalizedString
    /** 游戏数量统计文字，如“160 个游戏” */
    gamesCount: LocalizedString
    /** 分类下“查看全部”链接文字 */
    viewAll: LocalizedString
    /** 无分类时的提示文字 */
    noCategories: LocalizedString
    /** 分类下无游戏时的提示文字 */
    noCategoryGames: LocalizedString
    /** 热门游戏小徽标文字 */
    hotLabel: LocalizedString
    /** 未分类游戏的分组标题 */
    uncategorizedTitle: LocalizedString
    /** 游戏暂时不可用提示文字 */
    unavailableText: LocalizedString
    /** 加载失败后的重试按鈕文字 */
    retryText: LocalizedString
  }
}
