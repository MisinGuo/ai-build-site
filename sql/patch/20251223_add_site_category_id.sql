-- 为 gb_sites 表添加 category_id 字段
-- 执行时间: 2025-12-23
-- 说明: 添加分类ID字段，用于网站分类管理

-- 添加 category_id 字段
ALTER TABLE `gb_sites` 
ADD COLUMN `category_id` bigint NULL COMMENT '分类ID' AFTER `code`;

-- 添加索引以提升查询性能
CREATE INDEX IF NOT EXISTS `idx_category_id` ON `gb_sites` (`category_id`);
