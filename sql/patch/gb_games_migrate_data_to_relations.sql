-- ================================================================
-- 游戏数据迁移 - 从 gb_games 迁移盒子特定数据到 gb_box_game_relations
-- 创建日期: 2025-12-28
-- 说明: 将 gb_games 表中的盒子特定字段数据迁移到对应的 gb_box_game_relations 记录中
--       只更新关联表中字段为空的记录，已有数据的记录不覆盖
-- 
-- 执行顺序: 必须在 gb_games_remove_redundant_fields.sql 之前执行
-- ================================================================

USE gamebox;

-- 开始事务
START TRANSACTION;

-- ================================================================
-- 数据迁移逻辑
-- ================================================================

-- 更新下载链接（gb_box_game_relations只有download_url字段）
UPDATE gb_box_game_relations r
INNER JOIN gb_games g ON r.game_id = g.id
SET r.download_url = COALESCE(r.download_url, g.download_url)
WHERE g.download_url IS NOT NULL;

-- 更新折扣标签
UPDATE gb_box_game_relations r
INNER JOIN gb_games g ON r.game_id = g.id
SET r.discount_label = COALESCE(r.discount_label, g.discount_label)
WHERE g.discount_label IS NOT NULL;

-- 更新首充折扣相关字段（使用first_charge_domestic/overseas）
UPDATE gb_box_game_relations r
INNER JOIN gb_games g ON r.game_id = g.id
SET 
    r.first_charge_domestic = COALESCE(r.first_charge_domestic, g.first_charge_domestic),
    r.first_charge_overseas = COALESCE(r.first_charge_overseas, g.first_charge_overseas)
WHERE 
    g.first_charge_domestic IS NOT NULL 
    OR g.first_charge_overseas IS NOT NULL;

-- 更新续充折扣相关字段（使用recharge_domestic/overseas）
UPDATE gb_box_game_relations r
INNER JOIN gb_games g ON r.game_id = g.id
SET 
    r.recharge_domestic = COALESCE(r.recharge_domestic, g.recharge_domestic),
    r.recharge_overseas = COALESCE(r.recharge_overseas, g.recharge_overseas)
WHERE 
    g.recharge_domestic IS NOT NULL 
    OR g.recharge_overseas IS NOT NULL;

-- 更新支持相关字段
UPDATE gb_box_game_relations r
INNER JOIN gb_games g ON r.game_id = g.id
SET 
    r.has_support = COALESCE(r.has_support, g.has_support),
    r.support_desc = COALESCE(r.support_desc, g.support_desc)
WHERE 
    g.has_support IS NOT NULL 
    OR g.support_desc IS NOT NULL;

-- ================================================================
-- 数据验证
-- ================================================================

-- 检查是否有游戏数据未被迁移
SELECT 
    '游戏数据检查' AS check_type,
    COUNT(*) AS total_games_with_data,
    COUNT(DISTINCT g.id) AS games_count
FROM gb_games g
WHERE 
    g.download_url IS NOT NULL 
    OR g.discount_label IS NOT NULL
    OR g.first_charge_domestic IS NOT NULL
    OR g.first_charge_overseas IS NOT NULL
    OR g.recharge_domestic IS NOT NULL
    OR g.recharge_overseas IS NOT NULL
    OR g.has_support IS NOT NULL;

-- 检查关联表中迁移后的数据
SELECT 
    '关联表数据检查' AS check_type,
    COUNT(*) AS total_relations,
    SUM(CASE WHEN download_url IS NOT NULL THEN 1 ELSE 0 END) AS with_download_url,
    SUM(CASE WHEN discount_label IS NOT NULL THEN 1 ELSE 0 END) AS with_discount,
    SUM(CASE WHEN first_charge_domestic IS NOT NULL THEN 1 ELSE 0 END) AS with_first_charge_domestic,
    SUM(CASE WHEN first_charge_overseas IS NOT NULL THEN 1 ELSE 0 END) AS with_first_charge_overseas,
    SUM(CASE WHEN recharge_domestic IS NOT NULL THEN 1 ELSE 0 END) AS with_recharge_domestic,
    SUM(CASE WHEN recharge_overseas IS NOT NULL THEN 1 ELSE 0 END) AS with_recharge_overseas,
    SUM(CASE WHEN has_support IS NOT NULL THEN 1 ELSE 0 END) AS with_support
FROM gb_box_game_relations;

-- 提交事务
COMMIT;

-- ================================================================
-- 执行后注意事项
-- ================================================================
-- 1. 验证数据迁移是否完整
-- 2. 确认关联表中的数据正确
-- 3. 备份当前状态
-- 4. 然后执行 gb_games_remove_redundant_fields.sql 删除冗余字段
-- ================================================================
