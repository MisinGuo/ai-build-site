-- ============================================================================
-- U2Game 字段映射数据迁移脚本
-- ============================================================================
-- 目的：将 u2game 平台的通用字段映射配置复制到 U2Game盒子(ID=3)
-- 执行前提：gb_field_mappings 表已有 box_id 字段
-- ============================================================================

-- 步骤0: 修复唯一索引，支持盒子级别的字段映射
-- 旧索引：uk_platform_resource_source (platform, resource_type, source_field)
-- 新索引：需要包含 box_id 以区分通用配置和盒子配置

-- 删除旧的唯一索引
ALTER TABLE gb_field_mappings DROP INDEX IF EXISTS uk_platform_resource_source;

-- 创建新的唯一索引（包含 box_id，允许 NULL）
-- 这样可以同时支持：
-- 1. 通用配置: box_id=NULL, platform='u2game', source_field='xxx'
-- 2. 盒子配置: box_id=3, platform='u2game', source_field='xxx'
ALTER TABLE gb_field_mappings 
ADD UNIQUE INDEX uk_box_platform_resource_source (
    box_id, platform, resource_type, source_field
);

-- 或者使用组合索引（如果上面的唯一索引在有NULL值时有问题，使用这个）
-- CREATE UNIQUE INDEX uk_box_platform_resource_source 
-- ON gb_field_mappings(IFNULL(box_id, 0), platform, resource_type, source_field);

-- 步骤1: 更新盒子的 platform 字段（如果为空）
UPDATE gb_game_boxes 
SET platform = 'u2game' 
WHERE id = 3 AND (platform IS NULL OR platform = '');

-- 步骤2: 查看当前 u2game 平台的字段映射配置
SELECT 
    id,
    platform,
    resource_type,
    source_field,
    target_field,
    target_location,
    remark
FROM gb_field_mappings
WHERE platform = 'u2game' 
  AND resource_type = 'game'
  AND box_id IS NULL
ORDER BY sort_order;

-- 步骤3: 为 U2Game盒子(ID=3) 复制字段映射配置
-- 注意：将复制所有 u2game 平台的游戏字段映射到盒子3

INSERT INTO gb_field_mappings (
    box_id,
    platform,
    resource_type,
    source_field,
    target_field,
    field_type,
    target_location,
    transform_expression,
    value_mapping,
    default_value,
    is_required,
    sort_order,
    status,
    remark,
    create_by,
    create_time
)
SELECT 
    3 as box_id,                    -- 固定为盒子ID=3
    platform,
    resource_type,
    source_field,
    target_field,
    field_type,
    target_location,
    transform_expression,
    value_mapping,
    default_value,
    is_required,
    sort_order,
    status,
    CONCAT(remark, ' [从通用配置迁移]') as remark,
    'system' as create_by,
    NOW() as create_time
FROM gb_field_mappings
WHERE platform = 'u2game' 
  AND resource_type = 'game'
  AND box_id IS NULL
  AND NOT EXISTS (
      -- 避免重复：检查该盒子是否已有相同的source_field配置
      SELECT 1 FROM gb_field_mappings fm2
      WHERE fm2.box_id = 3
        AND fm2.source_field = gb_field_mappings.source_field
  );

-- 步骤4: 验证迁移结果
SELECT 
    '迁移完成' as message,
    COUNT(*) as total_mappings
FROM gb_field_mappings
WHERE box_id = 3;

-- 步骤5: 查看盒子3的详细字段映射配置
SELECT 
    id,
    box_id,
    platform,
    source_field,
    target_field,
    field_type,
    target_location,
    SUBSTRING(value_mapping, 1, 50) as value_mapping_preview,
    is_required,
    sort_order,
    status,
    remark
FROM gb_field_mappings
WHERE box_id = 3
ORDER BY sort_order;

-- 步骤6: 对比通用配置和盒子配置的数量
SELECT 
    'u2game通用配置' as config_type,
    COUNT(*) as count
FROM gb_field_mappings
WHERE platform = 'u2game' AND box_id IS NULL
UNION ALL
SELECT 
    'U2Game盒子配置' as config_type,
    COUNT(*) as count
FROM gb_field_mappings
WHERE box_id = 3;

-- ============================================================================
-- 可选操作：如果需要更新其他盒子
-- ============================================================================
-- 如果有其他 u2game 平台的盒子，可以使用以下脚本批量迁移：
-- 
-- INSERT INTO gb_field_mappings (
--     box_id, platform, resource_type, source_field, target_field, field_type,
--     target_location, transform_expression, value_mapping, default_value,
--     is_required, sort_order, status, remark, create_by, create_time
-- )
-- SELECT 
--     b.id as box_id,
--     fm.platform, fm.resource_type, fm.source_field, fm.target_field, fm.field_type,
--     fm.target_location, fm.transform_expression, fm.value_mapping, fm.default_value,
--     fm.is_required, fm.sort_order, fm.status,
--     CONCAT(fm.remark, ' [自动迁移]') as remark,
--     'system' as create_by,
--     NOW() as create_time
-- FROM gb_field_mappings fm
-- CROSS JOIN gb_game_boxes b
-- WHERE fm.platform = 'u2game'
--   AND fm.resource_type = 'game'
--   AND fm.box_id IS NULL
--   AND b.platform = 'u2game'
--   AND NOT EXISTS (
--       SELECT 1 FROM gb_field_mappings fm2
--       WHERE fm2.box_id = b.id
--         AND fm2.source_field = fm.source_field
--   );
