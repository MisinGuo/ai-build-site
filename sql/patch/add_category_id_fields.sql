-- =============================================
-- 为游戏、短剧、文章、游戏盒子表添加 category_id 字段
-- 执行日期: 2025-12-23
-- =============================================

-- 1. 为游戏表添加 category_id 字段
ALTER TABLE `gb_games` 
ADD COLUMN `category_id` bigint DEFAULT NULL COMMENT '分类ID' AFTER `site_id`,
ADD INDEX `idx_category_id` (`category_id`);

-- -- 2. 为短剧表添加索引（字段已存在）
-- ALTER TABLE `gb_dramas` 
-- ADD INDEX `idx_category_id` (`category_id`);

-- -- 3. 为文章表添加索引（字段已存在）
-- ALTER TABLE `gb_articles` 
-- ADD INDEX `idx_category_id` (`category_id`);

-- 4. 为游戏盒子表添加 category_id 字段
ALTER TABLE `gb_game_boxes` 
ADD COLUMN `category_id` bigint DEFAULT NULL COMMENT '分类ID' AFTER `site_id`,
ADD INDEX `idx_category_id` (`category_id`);

-- 注意：执行此脚本前请先备份数据库！
