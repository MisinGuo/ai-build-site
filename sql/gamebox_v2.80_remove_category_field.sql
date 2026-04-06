-- 版本：v2.80
-- 日期：2025-01-15
-- 说明：删除gb_games表中的category和category_id字段
-- 背景：分类关系完全通过gb_game_category_relation关联表管理，isPrimary字段标识主分类

-- 1. 删除category列（已废弃的分类名称字段）
ALTER TABLE `gb_games` DROP COLUMN IF EXISTS `category`;

-- 2. 删除category_id列（主分类ID字段，改用关联表的isPrimary标识）
ALTER TABLE `gb_games` DROP COLUMN IF EXISTS `category_id`;

-- 说明：
-- 所有分类关系现统一通过gb_game_category_relation表管理
-- isPrimary='1'标识主分类，前端查询时从关联表获取分类信息
-- 支持游戏关联多个分类的业务需求
