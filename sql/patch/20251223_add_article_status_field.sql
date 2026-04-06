-- 为 gb_articles 表添加 status 字段和修改必填字段
-- 执行时间: 2025-12-23
-- 说明: 
--   1. 添加文章状态字段，用于区分草稿、已发布、已下架等状态
--   2. 修改必填字段为可空，避免前端提交时报错

-- 修改 site_id 字段，允许为 NULL 并设置默认值为 1
ALTER TABLE `gb_articles` 
MODIFY COLUMN `site_id` bigint NULL DEFAULT 1 COMMENT '所属网站ID';

-- 修改 slug 字段，允许为 NULL
ALTER TABLE `gb_articles` 
MODIFY COLUMN `slug` varchar(255) NULL COMMENT '文章路径标识';

-- 修改 locale 字段，允许为 NULL 并设置默认值
ALTER TABLE `gb_articles` 
MODIFY COLUMN `locale` varchar(10) NULL DEFAULT 'zh-CN' COMMENT '文章语言';

-- 删除唯一索引约束（避免 NULL 值冲突）
ALTER TABLE `gb_articles` 
DROP INDEX IF EXISTS `uk_site_locale_slug`;

-- 检查 status 字段是否存在，如果不存在则添加
ALTER TABLE `gb_articles` 
ADD COLUMN IF NOT EXISTS `status` char(1) DEFAULT '0' COMMENT '文章状态：0-草稿 1-已发布 2-已下架' AFTER `sort_order`;

-- 根据现有的 is_published 字段初始化 status 值
UPDATE `gb_articles` 
SET `status` = CASE 
    WHEN `is_published` = '1' THEN '1'
    ELSE '0'
END;

-- 添加索引以提升查询性能
CREATE INDEX IF NOT EXISTS `idx_status` ON `gb_articles` (`status`);
