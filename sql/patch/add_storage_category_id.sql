-- =============================================
-- 为存储配置表添加分类字段
-- =============================================
-- 版本：v1.1.0
-- 日期：2025-12-23
-- 说明：为 gb_storage_configs 表添加 category_id 字段
-- =============================================

USE `ry-vue`;

-- 添加分类ID字段
ALTER TABLE `gb_storage_configs` 
ADD COLUMN `category_id` bigint NULL COMMENT '分类ID' AFTER `storage_purpose`;

-- 添加索引
ALTER TABLE `gb_storage_configs` 
ADD INDEX `idx_category_id` (`category_id`);

-- 验证字段是否添加成功
SELECT 
    COLUMN_NAME,
    COLUMN_TYPE,
    IS_NULLABLE,
    COLUMN_DEFAULT,
    COLUMN_COMMENT
FROM 
    INFORMATION_SCHEMA.COLUMNS
WHERE 
    TABLE_SCHEMA = 'ry-vue'
    AND TABLE_NAME = 'gb_storage_configs'
    AND COLUMN_NAME = 'category_id';

COMMIT;
