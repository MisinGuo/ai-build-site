-- 删除文章表的唯一键约束，添加删除时间字段
-- 问题：软删除场景下，唯一键约束会导致删除和重建记录时产生冲突
-- 解决：删除唯一键约束，添加删除时间字段用于区分删除记录

-- 1. 删除唯一键约束（如果存在）
DROP INDEX IF EXISTS uk_master_locale_slug ON gb_articles;

-- 2. 添加删除时间字段（如果不存在）
ALTER TABLE gb_articles ADD COLUMN IF NOT EXISTS deleted_at DATETIME DEFAULT NULL COMMENT '删除时间，用于区分同一记录的多次删除';

-- 说明：
-- 1. 删除后，不再有唯一性限制，可以自由删除和重新创建记录
-- 2. deleted_at 字段用途：
--    - 未删除的记录：deleted_at = NULL
--    - 已删除的记录：deleted_at = 删除时的时间
-- 3. 通过 deleted_at 字段可以区分：
--    - 哪些是已删除的记录
--    - 同一记录的多次删除（每次删除时间不同）
-- 4. 查询时使用 del_flag = '0' 或 deleted_at IS NULL 来过滤已删除记录
