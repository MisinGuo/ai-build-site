-- 扩展字段映射配置以支持映射到游戏盒子-游戏关联表
-- 这允许用户配置平台数据到关联表字段的映射（如折扣、扶持等信息）

-- 1. 修改 target_location 字段的注释，添加 relation 选项
ALTER TABLE `gb_box_field_mappings`
MODIFY COLUMN `target_location` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'main' 
COMMENT '目标位置：main-主表gb_games / ext-扩展表platform_data / relation-关联表gb_box_game_relations';

-- 2. 为关联表字段映射添加示例数据（可选）
-- 以下是一些常见的关联表字段映射示例：

-- 折扣标签映射
INSERT INTO `gb_box_field_mappings` 
(`box_id`, `resource_type`, `source_field`, `target_field`, `field_type`, `target_location`, `default_value`, `is_required`, `sort_order`, `status`, `remark`) 
VALUES 
(NULL, 'game', 'discount_label', 'discount_label', 'string', 'relation', NULL, '0', 100, '1', '折扣标签');

-- 首充折扣-国内
INSERT INTO `gb_box_field_mappings` 
(`box_id`, `resource_type`, `source_field`, `target_field`, `field_type`, `target_location`, `default_value`, `is_required`, `sort_order`, `status`, `remark`) 
VALUES 
(NULL, 'game', 'first_charge_domestic', 'first_charge_domestic', 'decimal', 'relation', NULL, '0', 101, '1', '首充折扣-国内');

-- 首充折扣-海外
INSERT INTO `gb_box_field_mappings` 
(`box_id`, `resource_type`, `source_field`, `target_field`, `field_type`, `target_location`, `default_value`, `is_required`, `sort_order`, `status`, `remark`) 
VALUES 
(NULL, 'game', 'first_charge_overseas', 'first_charge_overseas', 'decimal', 'relation', NULL, '0', 102, '1', '首充折扣-海外');

-- 续充折扣-国内
INSERT INTO `gb_box_field_mappings` 
(`box_id`, `resource_type`, `source_field`, `target_field`, `field_type`, `target_location`, `default_value`, `is_required`, `sort_order`, `status`, `remark`) 
VALUES 
(NULL, 'game', 'recharge_domestic', 'recharge_domestic', 'decimal', 'relation', NULL, '0', 103, '1', '续充折扣-国内');

-- 续充折扣-海外
INSERT INTO `gb_box_field_mappings` 
(`box_id`, `resource_type`, `source_field`, `target_field`, `field_type`, `target_location`, `default_value`, `is_required`, `sort_order`, `status`, `remark`) 
VALUES 
(NULL, 'game', 'recharge_overseas', 'recharge_overseas', 'decimal', 'relation', NULL, '0', 104, '1', '续充折扣-海外');

-- 是否有扶持
INSERT INTO `gb_box_field_mappings` 
(`box_id`, `resource_type`, `source_field`, `target_field`, `field_type`, `target_location`, `default_value`, `is_required`, `sort_order`, `status`, `remark`) 
VALUES 
(NULL, 'game', 'has_support', 'has_support', 'string', 'relation', '0', '0', 105, '1', '是否有扶持：0-无 1-有');

-- 扶持说明
INSERT INTO `gb_box_field_mappings` 
(`box_id`, `resource_type`, `source_field`, `target_field`, `field_type`, `target_location`, `default_value`, `is_required`, `sort_order`, `status`, `remark`) 
VALUES 
(NULL, 'game', 'support_desc', 'support_desc', 'string', 'relation', NULL, '0', 106, '1', '扶持说明');

-- 推广语
INSERT INTO `gb_box_field_mappings` 
(`box_id`, `resource_type`, `source_field`, `target_field`, `field_type`, `target_location`, `default_value`, `is_required`, `sort_order`, `status`, `remark`) 
VALUES 
(NULL, 'game', 'promote_text', 'promote_text', 'string', 'relation', NULL, '0', 107, '1', '推广语');

-- 二维码URL
INSERT INTO `gb_box_field_mappings` 
(`box_id`, `resource_type`, `source_field`, `target_field`, `field_type`, `target_location`, `default_value`, `is_required`, `sort_order`, `status`, `remark`) 
VALUES 
(NULL, 'game', 'qrcode_url', 'qrcode_url', 'string', 'relation', NULL, '0', 108, '1', '游戏二维码URL');

-- 海报URL
INSERT INTO `gb_box_field_mappings` 
(`box_id`, `resource_type`, `source_field`, `target_field`, `field_type`, `target_location`, `default_value`, `is_required`, `sort_order`, `status`, `remark`) 
VALUES 
(NULL, 'game', 'poster_url', 'poster_url', 'string', 'relation', NULL, '0', 109, '1', '宣传卡片/海报URL');

-- 是否独占
INSERT INTO `gb_box_field_mappings` 
(`box_id`, `resource_type`, `source_field`, `target_field`, `field_type`, `target_location`, `default_value`, `is_required`, `sort_order`, `status`, `remark`) 
VALUES 
(NULL, 'game', 'is_exclusive', 'is_exclusive', 'string', 'relation', '0', '0', 110, '1', '是否独占：0-否 1-是');
