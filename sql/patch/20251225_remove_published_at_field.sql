-- ================================================================
-- 数据库字段优化：移除冗余字段，统一使用 publish_time 和 status
-- 创建时间：2025-12-25
-- 说明：
--   1. 将 published_at 重命名为 publish_time
--   2. 删除 is_published 字段（使用 status 字段代替，0=草稿，1=已发布，2=已下架）
--   3. 删除冗余的 publish_year, publish_month, publish_day 字段（可从 publish_time 计算）
-- ================================================================

USE gamebox;

-- 1. 将 published_at 重命名为 publish_time
ALTER TABLE gb_articles 
CHANGE COLUMN `published_at` `publish_time` datetime NULL DEFAULT NULL COMMENT '发布时间';

-- 2. 删除冗余的 is_published 字段（已被 status 字段替代）
ALTER TABLE gb_articles DROP COLUMN IF EXISTS `is_published`;

-- 3. 删除冗余的发布年月日字段
ALTER TABLE gb_articles DROP COLUMN IF EXISTS `publish_year`;
ALTER TABLE gb_articles DROP COLUMN IF EXISTS `publish_month`;
ALTER TABLE gb_articles DROP COLUMN IF EXISTS `publish_day`;

-- 4. 删除不再需要的索引
DROP INDEX IF EXISTS `idx_is_published` ON gb_articles;
DROP INDEX IF EXISTS `idx_published_at` ON gb_articles;

-- 5. 添加新索引
ALTER TABLE gb_articles ADD INDEX `idx_publish_time` (`publish_time` ASC);

-- 验证修改
SHOW COLUMNS FROM gb_articles LIKE 'publish%';
SHOW COLUMNS FROM gb_articles LIKE 'status';

-- 预期结果：
-- publish_time: datetime, 可空
-- status: char(1), 0=草稿，1=已发布，2=已下架
