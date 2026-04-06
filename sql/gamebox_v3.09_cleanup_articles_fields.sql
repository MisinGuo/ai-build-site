-- ============================================
-- 版本：v3.09
-- 说明：清理文章表中的冗余字段
-- 原因：这些字段已在主文章表(gb_master_articles)中管理
-- 日期：2026-01-13
-- ============================================

-- 备份当前表结构（可选）
-- CREATE TABLE gb_articles_backup_v308 AS SELECT * FROM gb_articles;

-- ============================================
-- 第一步：删除网站ID字段和相关索引（由主文章管理）
-- ============================================
-- 先删除依赖 site_id 的唯一索引
ALTER TABLE `gb_articles` DROP INDEX `uk_site_locale_slug`;
ALTER TABLE `gb_articles` DROP INDEX `idx_site_id`;

-- 删除 site_id 字段
ALTER TABLE `gb_articles` DROP COLUMN `site_id`;

-- 创建新的唯一索引：一个主文章的每个语言版本+slug应该是唯一的
ALTER TABLE `gb_articles` ADD UNIQUE INDEX `uk_master_locale_slug` (`master_article_id`, `locale`, `slug`) USING BTREE;

-- ============================================
-- 第二步：删除分类相关字段（由主文章管理）
-- ============================================
ALTER TABLE `gb_articles` DROP COLUMN `category_id`;
ALTER TABLE `gb_articles` DROP INDEX `idx_category_id`;

-- ============================================
-- 第二步：删除AI生成相关字段（由主文章管理）
-- ============================================
ALTER TABLE `gb_articles` DROP COLUMN `is_ai_generated`;
ALTER TABLE `gb_articles` DROP COLUMN `prompt_template_id`;
ALTER TABLE `gb_articles` DROP COLUMN `generation_count`;
ALTER TABLE `gb_articles` DROP COLUMN `last_generation_id`;

-- ============================================
-- 第三步：删除状态管理字段（由主文章管理）
-- ============================================
ALTER TABLE `gb_articles` DROP COLUMN `is_published`;
ALTER TABLE `gb_articles` DROP COLUMN `is_top`;
ALTER TABLE `gb_articles` DROP COLUMN `is_recommend`;
ALTER TABLE `gb_articles` DROP COLUMN `sort_order`;
ALTER TABLE `gb_articles` DROP COLUMN `status`;
ALTER TABLE `gb_articles` DROP INDEX `idx_status`;

-- ============================================
-- 第四步：删除发布时间（由主文章管理）
-- ============================================
ALTER TABLE `gb_articles` DROP COLUMN `publish_time`;

-- ============================================
-- 第五步：验证表结构
-- ============================================
-- 查看清理后的表结构
SHOW CREATE TABLE `gb_articles`;

-- 查看剩余字段
SELECT 
    COLUMN_NAME as '字段名',
    COLUMN_TYPE as '类型',
    IS_NULLABLE as '可空',
    COLUMN_DEFAULT as '默认值',
    COLUMN_COMMENT as '注释'
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'gb_articles'
ORDER BY ORDINAL_POSITION;

-- ============================================
-- 清理后的字段列表（应保留的字段）
-- ============================================
/*
核心字段：
  - id: 主键ID
  - master_article_id: 主文章ID（关联，通过此字段获取site_id）
  - locale: 文章语言版本

内容字段：
  - slug: 文章路径标识
  - title: 文章标题
  - subtitle: 副标题
  - author: 作者
  - keywords: 关键词
  - description: 文章摘要
  - content: 文章内容
  - content_type: 内容类型
  - cover_url: 封面图URL

存储字段：
  - storage_config_id: 文章内容存储配置ID
  - storage_key: 存储键/路径
  - storage_url: 完整访问URL
  - resource_storage_config_id: 资源存储配置ID
  - resource_base_path: 资源基础路径
  - locale_storage_config_id: 该语言版本专用的存储配置ID
  - locale_resource_storage_config_id: 该语言版本专用的资源存储配置ID

翻译标记：
  - is_ai_translated: 是否AI翻译

统计字段：
  - word_count: 字数统计
  - reading_time: 预估阅读时间
  - resource_count: 资源数量
  - view_count: 浏览次数
  - like_count: 点赞次数
  - comment_count: 评论次数
  - share_count: 分享次数

基础字段：
  - del_flag: 删除标志
  - create_by: 创建者
  - create_time: 创建时间
  - update_by: 更新者
  - update_time: 更新时间
  - remark: 备注
*/

-- ============================================
-- 说明：
-- 1. 网站、分类、状态、排序、AI生成等信息统一由 gb_master_articles 管理
-- 2. gb_articles 表专注于存储不同语言版本的内容和存储信息
-- 3. 通过 master_article_id 关联到主文章获取 site_id 及其他统一管理的信息
-- 4. 唯一索引改为 uk_master_locale_slug 确保一个主文章的每个语言版本唯一
-- ============================================
