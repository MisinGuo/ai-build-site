-- ----------------------------
-- 文章生成任务多站点支持升级脚本
-- Version: v2.77
-- Date: 2026-01-01
-- Description: 为文章生成任务添加标题批次ID字段,支持批次选择功能
-- ----------------------------

-- 1. 添加 title_batch_id 字段
ALTER TABLE `gb_article_generation_tasks` 
ADD COLUMN `title_batch_id` BIGINT NULL DEFAULT NULL COMMENT '标题批次ID' AFTER `template_id`;

-- 2. 添加 category_id 字段
ALTER TABLE `gb_article_generation_tasks` 
ADD COLUMN `category_id` BIGINT NULL DEFAULT NULL COMMENT '分类ID' AFTER `title_batch_id`;

-- 3. 创建索引
ALTER TABLE `gb_article_generation_tasks` 
ADD INDEX `idx_title_batch_id` (`title_batch_id`),
ADD INDEX `idx_category_id` (`category_id`);

-- 4. 创建文章生成任务网站关联表(用于排除管理)
CREATE TABLE IF NOT EXISTS `gb_site_generation_relations` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `site_id` BIGINT(20) NOT NULL COMMENT '网站ID',
  `generation_id` BIGINT(20) NOT NULL COMMENT '文章生成任务ID',
  `relation_type` VARCHAR(50) NOT NULL DEFAULT 'exclude' COMMENT '关联类型：exclude-排除',
  `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` VARCHAR(64) NULL DEFAULT NULL COMMENT '创建者',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_site_generation` (`site_id`, `generation_id`, `relation_type`),
  KEY `idx_site_id` (`site_id`),
  KEY `idx_generation_id` (`generation_id`),
  KEY `idx_relation_type` (`relation_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章生成任务-网站关联表';

-- 升级完成
SELECT '文章生成任务多站点支持升级完成 (v2.77)' AS status;
