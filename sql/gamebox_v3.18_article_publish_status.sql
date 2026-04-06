-- ============================================
-- 游戏盒子系统 v3.18
-- 修复文章发布状态管理
-- 创建时间: 2026-01-14
-- ============================================

-- 说明：
-- 1. gb_master_articles表的status字段应表示主文章组的整体状态（是否存在/有效）
-- 2. gb_articles表需要添加status字段，表示每个语言版本是否已发布到存储
-- 3. gb_articles表需要添加publish_time字段，记录每个语言版本的发布时间

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================
-- 修改 gb_articles 表，添加发布状态管理字段
-- ============================================

-- 添加status字段：标识该语言版本是否已发布到存储
ALTER TABLE `gb_articles` 
ADD COLUMN `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '发布状态：0-草稿 1-已发布 2-已下架' AFTER `share_count`;

-- 添加publish_time字段：记录该语言版本的发布时间
ALTER TABLE `gb_articles` 
ADD COLUMN `publish_time` datetime NULL DEFAULT NULL COMMENT '发布时间' AFTER `update_time`;

-- 添加索引以提升查询性能
ALTER TABLE `gb_articles` 
ADD INDEX `idx_status`(`status` ASC) USING BTREE;

-- ============================================
-- 数据迁移：将现有数据的发布状态同步
-- ============================================

-- 对于已有storage_url的文章（即已发布到存储的），将status设置为1
UPDATE `gb_articles` 
SET `status` = '1' 
WHERE `storage_url` IS NOT NULL AND `storage_url` != '';

-- 对于没有storage_url的文章（即未发布的草稿），保持status为0
-- 这些文章已经默认是'0'，无需更新

-- ============================================
-- 修改主文章表status字段注释，明确其含义
-- ============================================

-- 修改gb_master_articles表的status字段注释
ALTER TABLE `gb_master_articles` 
MODIFY COLUMN `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' 
COMMENT '主文章组状态：0-草稿(至少一个语言版本未发布) 1-已发布(所有语言版本都已发布) 2-已下架';

-- 修改gb_master_articles表的is_published字段注释
ALTER TABLE `gb_master_articles` 
MODIFY COLUMN `is_published` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' 
COMMENT '主文章是否有发布版本：0-否(所有版本都是草稿) 1-是(至少有一个版本已发布)';

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- 更新说明
-- ============================================
-- 
-- 字段使用说明：
-- 
-- gb_master_articles表：
-- - status: 主文章组的整体状态
--   * 0: 草稿 - 至少有一个语言版本是草稿状态
--   * 1: 已发布 - 所有语言版本都已发布
--   * 2: 已下架 - 主文章组被下架
-- - is_published: 是否有任何语言版本已发布
--   * 0: 所有语言版本都是草稿
--   * 1: 至少有一个语言版本已发布
-- 
-- gb_articles表：
-- - status: 该语言版本的发布状态
--   * 0: 草稿 - 未发布到存储
--   * 1: 已发布 - 已发布到存储
--   * 2: 已下架 - 该语言版本被下架
-- - publish_time: 该语言版本的发布时间
-- 
-- ============================================
