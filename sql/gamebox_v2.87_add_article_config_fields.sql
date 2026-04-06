-- ============================================================
-- 脚本名称: gamebox_v2.87_add_article_config_fields.sql
-- 描述: 为文章生成任务表添加文章配置字段
-- 作者: System
-- 日期: 2026-01-03
-- 版本: v2.87
-- ============================================================

-- 说明：
-- 添加第二步"文章发布配置"相关字段：
-- 1. article_category_id - 目标文章分类ID
-- 2. article_locale - 文章语言
-- 3. set_as_homepage - 是否设为主页

USE `ry-vue`;

-- ============================================================
-- 1. 添加文章分类ID字段
-- ============================================================
ALTER TABLE `gb_article_generation_tasks` 
ADD COLUMN `article_category_id` bigint NULL DEFAULT NULL COMMENT '目标文章分类ID' AFTER `category_id`;

-- ============================================================
-- 2. 添加文章语言字段
-- ============================================================
ALTER TABLE `gb_article_generation_tasks` 
ADD COLUMN `article_locale` varchar(10) NULL DEFAULT 'zh-CN' COMMENT '文章语言: zh-CN-简体中文 zh-TW-繁体中文 en-US-英语 ja-JP-日语 ko-KR-韩语' AFTER `target_locales`;

-- ============================================================
-- 3. 添加设为主页字段
-- ============================================================
ALTER TABLE `gb_article_generation_tasks` 
ADD COLUMN `set_as_homepage` char(1) NULL DEFAULT '0' COMMENT '生成后设为网站主页: 0-否 1-是' AFTER `article_locale`;

-- ============================================================
-- 4. 添加索引
-- ============================================================
ALTER TABLE `gb_article_generation_tasks` 
ADD INDEX `idx_article_category_id`(`article_category_id` ASC) USING BTREE;

-- ============================================================
-- 验证字段添加
-- ============================================================
SELECT 
    COLUMN_NAME,
    COLUMN_TYPE,
    COLUMN_DEFAULT,
    COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'ry-vue'
  AND TABLE_NAME = 'gb_article_generation_tasks'
  AND COLUMN_NAME IN ('article_category_id', 'article_locale', 'set_as_homepage')
ORDER BY ORDINAL_POSITION;

-- ============================================================
-- 完成提示
-- ============================================================
SELECT '✅ v2.87 文章配置字段添加完成！' as message;
