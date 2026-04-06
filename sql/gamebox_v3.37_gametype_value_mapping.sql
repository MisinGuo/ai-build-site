-- ===================================================
-- 专项功能：为u2game添加gametype字段的值映射功能
-- 创建时间: 2026-01-27
-- 功能说明: 支持将JSON中的gametype字段值（如"放置"、"卡牌"）映射为对应的分类ID
-- ===================================================

-- 1. 为U2Game盒子添加gametype字段的值映射配置
INSERT INTO `gb_box_field_mappings` (
    `box_id`, 
    `resource_type`, 
    `source_field`, 
    `target_field`, 
    `field_type`, 
    `target_location`, 
    `value_mapping`, 
    `is_required`, 
    `sort_order`, 
    `status`, 
    `create_by`, 
    `create_time`, 
    `remark`
) VALUES (
    3,                    -- U2Game盒子ID
    'game', 
    'gametype',           -- 源字段：JSON中的gametype
    'category_ids',       -- 目标字段：游戏的分类ID数组
    'json',               -- 字段类型：JSON数组
    'main',               -- 目标位置：主表
    JSON_OBJECT(
        'type', 'direct',     -- 值映射类型：直接映射
        'mappings', JSON_OBJECT(
            '放置', CAST(75 AS JSON),      -- "放置" → 分类ID 75
            '卡牌', CAST(84 AS JSON),      -- "卡牌" → 分类ID 84
            '挂机', CAST(78 AS JSON),      -- "挂机" → 分类ID 78
            '角色扮演', CAST(71 AS JSON),   -- "角色扮演" → 分类ID 71
            '冒险', CAST(67 AS JSON),      -- "冒险" → 分类ID 67
            '仙侠', CAST(81 AS JSON),      -- "仙侠" → 分类ID 81
            '策略', CAST(83 AS JSON),      -- "策略" → 分类ID 83
            '武侠', CAST(80 AS JSON),      -- "武侠" → 分类ID 80
            '魔幻', CAST(82 AS JSON),      -- "魔幻" → 分类ID 82
            '二次元', CAST(79 AS JSON),    -- "二次元" → 分类ID 79
            '西游', CAST(77 AS JSON),      -- "西游" → 分类ID 77
            '三国', CAST(85 AS JSON)       -- "三国" → 分类ID 85
        )
    ), 
    '0',                  -- 非必填
    3,                    -- 排序序号
    '1',                  -- 启用状态
    'admin', 
    NOW(), 
    '游戏类型值映射 - 将u2game的gametype文字值自动映射为对应的分类ID。例如：gametype="放置" → category_ids=[75]'
) ON DUPLICATE KEY UPDATE 
    `value_mapping` = VALUES(`value_mapping`),
    `remark` = VALUES(`remark`),
    `update_time` = NOW();

-- 2. 验证映射配置
SELECT 
    '映射配置验证' as title,
    source_field,
    target_field,
    field_type,
    JSON_EXTRACT(value_mapping, '$.type') as mapping_type,
    JSON_KEYS(JSON_EXTRACT(value_mapping, '$.mappings')) as supported_values,
    remark
FROM `gb_box_field_mappings` 
WHERE `box_id` = 3 
  AND `source_field` = 'gametype';

-- 3. 显示相关的游戏分类信息（用于参考）
SELECT 
    '可用游戏分类' as title,
    id as category_id,
    name as category_name,
    slug as category_slug,
    description
FROM `gb_categories` 
WHERE `site_id` = 1 
  AND `category_type` = 'game' 
  AND `status` = '1'
  AND `del_flag` = '0'
  AND id IN (75, 84, 78, 71, 67, 81, 83, 80, 82, 79, 77, 85)
ORDER BY id;

-- 4. 使用示例说明
SELECT 
    '使用示例' as title,
    'JSON输入: {"gametype": "放置"}' as example_input,
    '映射结果: category_ids = [75]' as example_output,
    '对应分类: 放置挂机类游戏' as category_description
UNION ALL
SELECT 
    '使用示例',
    'JSON输入: {"gametype": "卡牌"}',
    '映射结果: category_ids = [84]',
    '对应分类: 卡牌策略游戏'
UNION ALL
SELECT 
    '使用示例',
    'JSON输入: {"gametype": "挂机"}',
    '映射结果: category_ids = [78]',
    '对应分类: 放置挂机游戏';

-- 5. 测试JSON值映射功能（查看配置是否正确）
SELECT 
    '值映射测试' as title,
    JSON_EXTRACT(value_mapping, '$.mappings."放置"') as 放置_mapping,
    JSON_EXTRACT(value_mapping, '$.mappings."卡牌"') as 卡牌_mapping,
    JSON_EXTRACT(value_mapping, '$.mappings."挂机"') as 挂机_mapping
FROM `gb_box_field_mappings` 
WHERE `box_id` = 3 AND `source_field` = 'gametype';