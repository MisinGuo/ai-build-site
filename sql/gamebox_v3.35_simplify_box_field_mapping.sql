-- 简化盒子字段映射表结构
-- 移除 platform 字段（因为盒子已经和平台绑定）
-- resource_type 设置默认值为 'game'

USE gamebox;

-- 1. 删除旧的唯一索引（包含 platform）
ALTER TABLE `gb_box_field_mappings` 
DROP INDEX `uk_box_platform_resource_source`;

-- 2. 删除 platform 字段
ALTER TABLE `gb_box_field_mappings` 
DROP COLUMN `platform`;

-- 3. 修改 resource_type 字段为可为空，并设置默认值为 'game'
ALTER TABLE `gb_box_field_mappings` 
MODIFY COLUMN `resource_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'game' COMMENT '资源类型：game-游戏 / box-盒子';

-- 4. 创建新的唯一索引（只基于 box_id, resource_type 和 source_field）
ALTER TABLE `gb_box_field_mappings` 
ADD UNIQUE INDEX `uk_box_resource_source`(`box_id` ASC, `resource_type` ASC, `source_field` ASC) USING BTREE;

-- 完成
SELECT 'gb_box_field_mappings 表结构简化完成' AS message;
