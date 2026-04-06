-- ================================================================
-- 游戏表字段优化 - 移除盒子特定冗余字段
-- 创建日期: 2025-12-28
-- 说明: 从 gb_games 表中移除与 gb_box_game_relations 重叠的盒子特定字段
--       这些字段应该只存在于关联表中，以支持同一游戏在不同盒子中有不同的配置
-- 
-- 前置条件: 
-- 1. 必须先执行数据迁移SQL (gb_games_migrate_data_to_relations.sql)
-- 2. 备份数据库
-- ================================================================

USE gamebox;

-- 移除下载相关字段（盒子特定）
ALTER TABLE gb_games DROP COLUMN download_url;
ALTER TABLE gb_games DROP COLUMN android_url;
ALTER TABLE gb_games DROP COLUMN ios_url;
ALTER TABLE gb_games DROP COLUMN apk_url;

-- 移除折扣标签（盒子特定）
ALTER TABLE gb_games DROP COLUMN discount_label;

-- 移除首充折扣相关字段（盒子特定）
ALTER TABLE gb_games DROP COLUMN first_charge_domestic;
ALTER TABLE gb_games DROP COLUMN first_charge_overseas;

-- 移除续充折扣相关字段（盒子特定）
ALTER TABLE gb_games DROP COLUMN recharge_domestic;
ALTER TABLE gb_games DROP COLUMN recharge_overseas;

-- 移除支持相关字段（盒子特定）
ALTER TABLE gb_games DROP COLUMN has_support;
ALTER TABLE gb_games DROP COLUMN support_desc;

-- 移除低扣券相关字段（盒子特定）
ALTER TABLE gb_games DROP COLUMN has_low_deduct_coupon;
ALTER TABLE gb_games DROP COLUMN low_deduct_coupon_url;

-- 验证删除结果
SELECT 
    COLUMN_NAME,
    DATA_TYPE,
    COLUMN_COMMENT
FROM 
    INFORMATION_SCHEMA.COLUMNS
WHERE 
    TABLE_SCHEMA = 'gamebox' 
    AND TABLE_NAME = 'gb_games'
ORDER BY 
    ORDINAL_POSITION;

-- 预期结果: gb_games 表应该只保留游戏基础信息字段
-- 不应包含: download_url, android_url, ios_url, apk_url, discount_label, 
--          first_charge_domestic, first_charge_overseas, 
--          recharge_domestic, recharge_overseas, 
--          has_support, support_desc, 
--          has_low_deduct_coupon, low_deduct_coupon_url
