-- ============================================================================
-- 重命名字段映射表为盒子字段映射表
-- ============================================================================
-- 目的：将 gb_field_mappings 重命名为 gb_box_field_mappings
-- 原因：现在所有字段映射都是基于盒子的，不再有独立的字段映射管理页面
-- 执行时间：2026-01-27
-- ============================================================================

-- 步骤1: 删除外键约束（为了重命名表）
ALTER TABLE gb_field_mappings DROP FOREIGN KEY IF EXISTS fk_field_mapping_box;

-- 步骤2: 重命名表
RENAME TABLE gb_field_mappings TO gb_box_field_mappings;

-- 步骤3: 重新创建外键约束
ALTER TABLE gb_box_field_mappings 
ADD CONSTRAINT fk_box_field_mapping_box 
FOREIGN KEY (box_id) REFERENCES gb_game_boxes(id) 
ON DELETE CASCADE ON UPDATE CASCADE;

-- 步骤4: 验证重命名结果
SHOW TABLES LIKE 'gb_box_field_mappings';

-- 步骤5: 验证表结构
DESC gb_box_field_mappings;

-- 步骤6: 验证数据完整性
SELECT 
    '数据验证' as check_type,
    COUNT(*) as total_records,
    COUNT(DISTINCT box_id) as boxes_with_mappings,
    COUNT(CASE WHEN box_id IS NULL THEN 1 END) as general_configs
FROM gb_box_field_mappings;

-- 步骤7: 验证索引
SHOW INDEX FROM gb_box_field_mappings;

-- ============================================================================
-- 完成！表已重命名为 gb_box_field_mappings
-- 接下来需要更新：
-- 1. 后端 Entity/Mapper/Service/Controller
-- 2. 前端 API 路径
-- ============================================================================
