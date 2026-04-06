-- ================================================================
-- GameBox v3.31 - 修复字段映射的目标位置标识
-- 功能说明：
-- 1. 将 target_location='ext' 统一改为 'platform_data'
-- 2. 保持 'main' 和 'promotion_link' 不变
-- 3. 提供更清晰的数据分类
-- 
-- 执行方式：
-- mysql -u root -p123456 ry-vue < gamebox_v3.31_fix_target_location.sql
-- ================================================================

USE `ry-vue`;

-- 显示更新前的状态
SELECT 
    '更新前统计' as stage,
    target_location,
    COUNT(*) as count,
    GROUP_CONCAT(DISTINCT status) as statuses
FROM gb_field_mappings
GROUP BY target_location
ORDER BY target_location;

-- 更新所有 ext 为 platform_data（包括所有状态）
UPDATE gb_field_mappings 
SET target_location = 'platform_data' 
WHERE target_location = 'ext';

-- 显示更新后的状态
SELECT 
    '更新后统计' as stage,
    target_location,
    COUNT(*) as count,
    GROUP_CONCAT(DISTINCT status) as statuses
FROM gb_field_mappings
GROUP BY target_location
ORDER BY target_location;

-- 显示详细的分布（仅启用状态）
SELECT 
    '启用配置分布' as info,
    target_location,
    COUNT(*) as count
FROM gb_field_mappings
WHERE status = '1'
GROUP BY target_location
ORDER BY target_location;
