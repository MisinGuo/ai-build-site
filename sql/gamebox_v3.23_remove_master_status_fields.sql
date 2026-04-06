-- =============================================
-- 版本: v3.23
-- 日期: 2026-01-16
-- 说明: 删除主文章表中的 isPublished 和 status 字段
--       这些状态改为通过文章表（gb_articles）的 status 字段动态计算
--       - 所有语言版本都已发布 → 已发布
--       - 部分语言版本已发布 → 有发布版本  
--       - 全部语言版本未发布 → 未发布
-- =============================================

-- 删除 is_published 字段
ALTER TABLE `gb_master_articles` DROP COLUMN `is_published`;

-- 删除 status 字段
ALTER TABLE `gb_master_articles` DROP COLUMN  `status`;
