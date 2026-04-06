-- ===================================================
-- 游戏盒子管理系统 v3.37 - 为u2game添加gametype字段映射并支持值映射
-- 创建时间: 2026-01-27
-- 描述: 为U2Game盒子添加gametype字段的值映射功能，支持将JSON中的游戏类型文字映射为分类ID
-- ===================================================

-- 为U2Game盒子(box_id=3)添加gametype字段映射配置，支持值映射
INSERT INTO `gb_box_field_mappings` (
    `box_id`, 
    `resource_type`, 
    `source_field`, 
    `target_field`, 
    `field_type`, 
    `target_location`, 
    `transform_expression`, 
    `value_mapping`, 
    `default_value`, 
    `is_required`, 
    `sort_order`, 
    `status`, 
    `create_by`, 
    `create_time`, 
    `update_by`, 
    `update_time`, 
    `remark`
) VALUES (
    3, 
    'game', 
    'gametype', 
    'category_ids', 
    'json', 
    'main', 
    NULL, 
    JSON_OBJECT(
        'type', 'direct',
        'mappings', JSON_OBJECT(
            '放置', CAST(75 AS JSON),    -- 对应 site_id=1 的"放置"分类 (id=75)
            '卡牌', CAST(84 AS JSON),    -- 对应 site_id=1 的"卡牌"分类 (id=84)
            '挂机', CAST(78 AS JSON),    -- 对应 site_id=1 的"挂机"分类 (id=78)
            '角色扮演', CAST(71 AS JSON), -- 对应 site_id=1 的"角色扮演"分类 (id=71)
            '冒险', CAST(67 AS JSON),    -- 对应 site_id=1 的"冒险"分类 (id=67)
            '仙侠', CAST(81 AS JSON),    -- 对应 site_id=1 的"仙侠"分类 (id=81)
            '策略', CAST(83 AS JSON),    -- 对应 site_id=1 的"策略"分类 (id=83)
            '武侠', CAST(80 AS JSON),    -- 对应 site_id=1 的"武侠"分类 (id=80)
            '魔幻', CAST(82 AS JSON),    -- 对应 site_id=1 的"魔幻"分类 (id=82)
            '二次元', CAST(79 AS JSON),  -- 对应 site_id=1 的"二次元"分类 (id=79)
            '西游', CAST(77 AS JSON),    -- 对应 site_id=1 的"西游"分类 (id=77)
            '三国', CAST(85 AS JSON),    -- 对应 site_id=1 的"三国"分类 (id=85)
            '经营', CAST(65 AS JSON),    -- 对应 site_id=1 的"经营"分类 (id=65)
            '养成', CAST(66 AS JSON),    -- 对应 site_id=1 的"养成"分类 (id=66)
            '玄幻', CAST(68 AS JSON),    -- 对应 site_id=1 的"玄幻"分类 (id=68)
            '科幻', CAST(69 AS JSON),    -- 对应 site_id=1 的"科幻"分类 (id=69)
            '割草', CAST(70 AS JSON),    -- 对应 site_id=1 的"割草"分类 (id=70)
            '休闲', CAST(73 AS JSON),    -- 对应 site_id=1 的"休闲"分类 (id=73)
            '动漫', CAST(74 AS JSON),    -- 对应 site_id=1 的"动漫"分类 (id=74)
            '回合', CAST(76 AS JSON)     -- 对应 site_id=1 的"回合"分类 (id=76)
        )
    ), 
    NULL, 
    '0', 
    3, 
    '1', 
    'admin', 
    NOW(), 
    'admin', 
    NOW(), 
    '游戏类型字段映射，支持将u2game中的gametype文字值映射为对应的分类ID数组。例如：JSON中gametype为"放置"时，会自动映射为分类ID [75]'
);

-- 更新现有的一些字段映射，优化排序和描述
UPDATE `gb_box_field_mappings` 
SET 
    `sort_order` = 2,
    `remark` = '游戏名称 - 支持多种字段名'
WHERE `box_id` = 3 AND `resource_type` = 'game' AND `source_field` IN ('game_name', 'gamename', '游戏名');

-- 添加更多u2game常见字段的映射配置
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
    `update_by`, 
    `update_time`, 
    `remark`
) VALUES 
-- u2game专有字段映射
(3, 'game', 'id', 'source_id', 'string', 'main', NULL, '1', 1, '1', 'admin', NOW(), 'admin', NOW(), 'u2game游戏ID'),
(3, 'game', 'pic1', 'icon_url', 'string', 'main', NULL, '1', 4, '1', 'admin', NOW(), 'admin', NOW(), 'u2game游戏图标'),
(3, 'game', 'pic2', 'cover_url', 'string', 'main', NULL, '0', 5, '1', 'admin', NOW(), 'admin', NOW(), 'u2game游戏封面'),
(3, 'game', 'pic3', 'banner_url', 'string', 'main', NULL, '0', 6, '1', 'admin', NOW(), 'admin', NOW(), 'u2game游戏横幅'),
(3, 'game', 'excerpt', 'description', 'string', 'main', NULL, '0', 7, '1', 'admin', NOW(), 'admin', NOW(), 'u2game游戏描述'),
(3, 'game', 'video', 'video_url', 'string', 'main', NULL, '0', 8, '1', 'admin', NOW(), 'admin', NOW(), 'u2game游戏视频'),
(3, 'game', 'photo', 'screenshots', 'json', 'main', NULL, '0', 9, '1', 'admin', NOW(), 'admin', NOW(), 'u2game游戏截图数组'),
(3, 'game', 'downurl', 'download_url', 'string', 'main', NULL, '0', 10, '1', 'admin', NOW(), 'admin', NOW(), 'u2game下载链接'),
(3, 'game', 'android_url', 'android_url', 'string', 'main', NULL, '0', 11, '1', 'admin', NOW(), 'admin', NOW(), 'u2game安卓下载链接'),
(3, 'game', 'ios_url', 'ios_url', 'string', 'main', NULL, '0', 12, '1', 'admin', NOW(), 'admin', NOW(), 'u2game iOS下载链接'),
(3, 'game', 'welfare', 'discount_label', 'string', 'main', NULL, '0', 13, '1', 'admin', NOW(), 'admin', NOW(), 'u2game福利说明'),
(3, 'game', 'firstpay', 'first_charge_domestic', 'decimal', 'main', NULL, '0', 14, '1', 'admin', NOW(), 'admin', NOW(), 'u2game首充折扣'),
(3, 'game', 'otherpay', 'recharge_domestic', 'decimal', 'main', NULL, '0', 15, '1', 'admin', NOW(), 'admin', NOW(), 'u2game续充折扣'),
(3, 'game', 'hw_firstpay', 'first_charge_overseas', 'decimal', 'main', NULL, '0', 16, '1', 'admin', NOW(), 'admin', NOW(), 'u2game海外首充折扣'),
(3, 'game', 'hw_otherpay', 'recharge_overseas', 'decimal', 'main', NULL, '0', 17, '1', 'admin', NOW(), 'admin', NOW(), 'u2game海外续充折扣'),
(3, 'game', 'addtime', 'launch_time', 'date', 'main', NULL, '0', 18, '1', 'admin', NOW(), 'admin', NOW(), 'u2game游戏添加时间'),
(3, 'game', 'new', 'is_new', 'boolean', 'main', JSON_OBJECT('type', 'direct', 'mappings', JSON_OBJECT('1', true, '0', false)), '0', 19, '1', 'admin', NOW(), 'admin', NOW(), 'u2game是否新游戏'),

-- 设备支持映射
(3, 'game', 'device_type', 'device_support', 'string', 'main', 
 JSON_OBJECT(
     'type', 'direct',
     'mappings', JSON_OBJECT(
         '0', 'android',   -- 仅安卓
         '1', 'ios',       -- 仅iOS
         '2', 'both'       -- 双端
     )
 ), '0', 20, '1', 'admin', NOW(), 'admin', NOW(), 'u2game设备支持类型映射'),

-- 游戏版本类型映射
(3, 'game', 'edition', 'game_type', 'string', 'main',
 JSON_OBJECT(
     'type', 'direct',
     'mappings', JSON_OBJECT(
         '1', 'official',  -- 官方版
         '2', 'bt',        -- BT版
         '3', 'discount'   -- 折扣版
     )
 ), '0', 21, '1', 'admin', NOW(), 'admin', NOW(), 'u2game游戏版本类型映射'),

-- 扩展信息字段（存储到platform_data）
(3, 'game', 'weburl', 'web_url', 'string', 'platform_data', NULL, '0', 100, '1', 'admin', NOW(), 'admin', NOW(), 'u2game网页链接'),
(3, 'game', 'qrcode', 'download_qrcode', 'string', 'platform_data', NULL, '0', 101, '1', 'admin', NOW(), 'admin', NOW(), 'u2game下载二维码'),
(3, 'game', 'weburl_qr', 'web_qrcode', 'string', 'platform_data', NULL, '0', 102, '1', 'admin', NOW(), 'admin', NOW(), 'u2game网页二维码'),
(3, 'game', 'copy', 'copy_text', 'string', 'platform_data', NULL, '0', 103, '1', 'admin', NOW(), 'admin', NOW(), 'u2game推广文案'),
(3, 'game', 'box_content', 'box_content', 'string', 'platform_data', NULL, '0', 104, '1', 'admin', NOW(), 'admin', NOW(), 'u2game盒子内容介绍'),
(3, 'game', 'text_cps', 'promotion_note', 'string', 'platform_data', NULL, '0', 105, '1', 'admin', NOW(), 'admin', NOW(), 'u2game推广备注'),
(3, 'game', 'cps_cpa', 'domestic_cps_info', 'string', 'platform_data', NULL, '0', 106, '1', 'admin', NOW(), 'admin', NOW(), 'u2game国内CPS信息'),
(3, 'game', 'hw_cps_cpa', 'overseas_cps_info', 'string', 'platform_data', NULL, '0', 107, '1', 'admin', NOW(), 'admin', NOW(), 'u2game海外CPS信息'),
(3, 'game', 'interchangeable_role', 'role_interchangeable', 'boolean', 'platform_data', JSON_OBJECT('type', 'direct', 'mappings', JSON_OBJECT('1', true, '0', false)), '0', 108, '1', 'admin', NOW(), 'admin', NOW(), 'u2game角色是否可互换'),
(3, 'game', 'h5', 'is_h5_game', 'boolean', 'platform_data', JSON_OBJECT('type', 'direct', 'mappings', JSON_OBJECT('1', true, '0', false)), '0', 109, '1', 'admin', NOW(), 'admin', NOW(), 'u2game是否H5游戏'),
(3, 'game', 'version', 'client_version', 'string', 'platform_data', NULL, '0', 110, '1', 'admin', NOW(), 'admin', NOW(), 'u2game客户端版本'),
(3, 'game', 'updatetime', 'last_update_time', 'date', 'platform_data', NULL, '0', 111, '1', 'admin', NOW(), 'admin', NOW(), 'u2game最后更新时间')

ON DUPLICATE KEY UPDATE 
    `value_mapping` = VALUES(`value_mapping`),
    `remark` = VALUES(`remark`),
    `update_time` = NOW();

-- 添加索引以优化查询性能
ALTER TABLE `gb_box_field_mappings` 
ADD INDEX `idx_box_sort_order` (`box_id` ASC, `sort_order` ASC);

-- 查询验证结果
SELECT 
    id,
    box_id,
    source_field,
    target_field,
    field_type,
    target_location,
    CASE 
        WHEN value_mapping IS NOT NULL THEN '有值映射'
        ELSE '无值映射' 
    END as has_value_mapping,
    sort_order,
    remark
FROM `gb_box_field_mappings` 
WHERE `box_id` = 3 AND `resource_type` = 'game'
ORDER BY `sort_order`, `id`;

-- 显示统计信息
SELECT 
    '字段映射添加完成' as status,
    COUNT(*) as total_mappings,
    SUM(CASE WHEN value_mapping IS NOT NULL THEN 1 ELSE 0 END) as mappings_with_value_mapping
FROM `gb_box_field_mappings` 
WHERE `box_id` = 3 AND `resource_type` = 'game';