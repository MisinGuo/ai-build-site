-- ============================================================================
-- 盒子优先级导入系统 - 数据库变更补丁
-- 日期：2026-03-25
-- 说明：
--   1. gb_game_boxes 新增 priority 字段（盒子优先级）
--   2. gb_games 新增 source_box_id 字段（数据来源跟踪）
--   3. gb_game_category 新增 icon_url, order_num 字段

-- ==========================================

-- 1. 添加盒子优先级字段
ALTER TABLE `gb_game_boxes`
  ADD COLUMN `priority` int NULL DEFAULT 0 COMMENT '盒子优先级，数字越大优先级越高（用于解决导入数据冲突）' AFTER `sort_order`;

-- 2. 游戏表添加来源盒子字段
ALTER TABLE `gb_games`
  ADD COLUMN `source_box_id` bigint(20) NULL DEFAULT NULL COMMENT '首次/最近覆盖从哪个盒子导入（主要用于区分渠道特殊数据）' AFTER `low_deduct_coupon_url`;

ALTER TABLE `gb_games` ADD INDEX `idx_source_box_id`(`source_box_id` ASC);
