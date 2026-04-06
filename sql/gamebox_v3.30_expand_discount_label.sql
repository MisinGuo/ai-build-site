-- ================================
-- 游戏盒子系统 v3.30 补丁
-- 功能: 扩大discount_label字段长度
-- 日期: 2026-01-26
-- 说明: varchar(20) → varchar(200)，支持更长的折扣描述
-- ================================

ALTER TABLE `gb_games` 
MODIFY COLUMN `discount_label` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '折扣标签/福利说明';

-- 验证修改
SHOW FULL COLUMNS FROM `gb_games` WHERE Field = 'discount_label';
