-- ============================================================
-- 迁移：将平台扩展字段从 gb_games_platform_ext 迁移到 gb_box_game_relations
-- 时间：2026-03-06
-- 说明：同一款游戏可出现在多个盒子，每个盒子保留各自的推广链接/平台数据
-- ============================================================

-- Step 1: 为关联表添加 promotion_links 和 platform_data 两列
ALTER TABLE `gb_box_game_relations`
  ADD COLUMN `promotion_links` json NULL
      COMMENT '推广链接集合（JSON格式，含 download_url/web_url/qrcode 等）'
      AFTER `poster_url`,
  ADD COLUMN `platform_data` json NULL
      COMMENT '平台特有数据（JSON格式，含 is_h5/cps_ratio/platform_game_id 等）'
      AFTER `promotion_links`;

-- Step 2: 将已有的 gb_games_platform_ext 数据迁移到关联表
-- （按 platform = box_id 的约定关联；只更新已存在关联记录的行）
UPDATE `gb_box_game_relations` r
INNER JOIN `gb_games_platform_ext` e
    ON e.game_id = r.game_id
    AND e.platform = CAST(r.box_id AS CHAR) COLLATE utf8mb4_unicode_ci
SET
    r.promotion_links = e.promotion_links,
    r.platform_data   = e.platform_data
WHERE r.promotion_links IS NULL;

-- Step 3: 确认迁移完成后删除 gb_games_platform_ext 表
DROP TABLE IF EXISTS `gb_games_platform_ext`;

-- Step 4: 修正 gb_box_field_mappings.target_location 字段注释
--   旧注释还写着 ext-扩展表platform_data（指 gb_games_platform_ext），更新为新说明
ALTER TABLE `gb_box_field_mappings`
  MODIFY COLUMN `target_location` varchar(20)
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci
    NULL DEFAULT 'main'
    COMMENT '目标位置：main-主表gb_games / promotion_link-推广链接JSON(gb_box_game_relations) / platform_data-平台数据JSON(gb_box_game_relations) / relation-关联表直接字段 / category_relation-分类关联';

-- Step 5: 将历史遗留的 target_location='ext' 统一转换为 'platform_data'
--   旧版前端保存时可能写入 'ext'，现统一使用 'platform_data'
UPDATE `gb_box_field_mappings`
SET `target_location` = 'platform_data'
WHERE `target_location` = 'ext';

SELECT
  '迁移完成' AS status,
  (SELECT COUNT(*) FROM `gb_box_game_relations` WHERE promotion_links IS NOT NULL) AS migrated_promotion_links,
  (SELECT COUNT(*) FROM `gb_box_game_relations` WHERE platform_data    IS NOT NULL) AS migrated_platform_data,
  (SELECT COUNT(*) FROM `gb_box_field_mappings`  WHERE target_location = 'platform_data') AS mapping_platform_data_count,
  (SELECT COUNT(*) FROM `gb_box_field_mappings`  WHERE target_location = 'promotion_link') AS mapping_promotion_link_count;
