-- ====================================================================
-- 数据库升级脚本 v2.6.2 - 修复唯一约束以支持 relation_type
-- ====================================================================
-- 功能：更新所有网站关联表的唯一约束，从 (site_id, xxx_id) 改为 (site_id, xxx_id, relation_type)
-- 影响表：
--   1. gb_site_category_relations - 分类关联
--   2. gb_site_box_relations      - 游戏盒子关联
--   3. gb_site_game_relations     - 游戏关联  
--   4. gb_site_article_relations  - 文章关联
--   5. gb_site_storage_config_relations - 存储配置关联
-- 原因：v2.6 添加了 relation_type 字段支持 include/exclude 两种类型
--       同一个 (site_id, xxx_id) 组合现在可以有两条记录：一条 include，一条 exclude
-- 日期：2025-12-29
-- ====================================================================

-- ====================================================================
-- 1. 分类关联表 (gb_site_category_relations)
-- ====================================================================

-- 删除旧的唯一约束
ALTER TABLE `gb_site_category_relations`
DROP INDEX `uk_site_category`;

-- 添加新的唯一约束（包含 relation_type）
ALTER TABLE `gb_site_category_relations`
ADD UNIQUE KEY `uk_site_category_type` (`site_id`, `category_id`, `relation_type`);


-- ====================================================================
-- 2. 游戏盒子关联表 (gb_site_box_relations)
-- ====================================================================

-- 删除旧的唯一约束（如果存在）
ALTER TABLE `gb_site_box_relations`
DROP INDEX IF EXISTS `uk_site_box`;

-- 添加新的唯一约束（包含 relation_type）
ALTER TABLE `gb_site_box_relations`
ADD UNIQUE KEY `uk_site_box_type` (`site_id`, `box_id`, `relation_type`);


-- ====================================================================
-- 3. 游戏关联表 (gb_site_game_relations)
-- ====================================================================

-- 删除旧的唯一约束（如果存在）
ALTER TABLE `gb_site_game_relations`
DROP INDEX IF EXISTS `uk_site_game`;

-- 添加新的唯一约束（包含 relation_type）
ALTER TABLE `gb_site_game_relations`
ADD UNIQUE KEY `uk_site_game_type` (`site_id`, `game_id`, `relation_type`);


-- ====================================================================
-- 4. 文章关联表 (gb_site_article_relations)
-- ====================================================================

-- 删除旧的唯一约束（如果存在）
ALTER TABLE `gb_site_article_relations`
DROP INDEX IF EXISTS `uk_site_article`;

-- 添加新的唯一约束（包含 relation_type）
ALTER TABLE `gb_site_article_relations`
ADD UNIQUE KEY `uk_site_article_type` (`site_id`, `article_id`, `relation_type`);


-- ====================================================================
-- 5. 存储配置关联表 (gb_site_storage_config_relations)
-- ====================================================================

-- 删除旧的唯一约束（如果存在）
ALTER TABLE `gb_site_storage_config_relations`
DROP INDEX IF EXISTS `uk_site_storage`;

-- 添加新的唯一约束（包含 relation_type）
ALTER TABLE `gb_site_storage_config_relations`
ADD UNIQUE KEY `uk_site_storage_type` (`site_id`, `storage_config_id`, `relation_type`);


-- ====================================================================
-- 6. 验证升级结果
-- ====================================================================

-- 检查分类关联表的唯一约束
SHOW INDEX FROM `gb_site_category_relations` WHERE Key_name LIKE 'uk_%';

-- 检查游戏盒子关联表的唯一约束
SHOW INDEX FROM `gb_site_box_relations` WHERE Key_name LIKE 'uk_%';

-- 检查游戏关联表的唯一约束
SHOW INDEX FROM `gb_site_game_relations` WHERE Key_name LIKE 'uk_%';

-- 检查文章关联表的唯一约束
SHOW INDEX FROM `gb_site_article_relations` WHERE Key_name LIKE 'uk_%';

-- 检查存储配置关联表的唯一约束
SHOW INDEX FROM `gb_site_storage_config_relations` WHERE Key_name LIKE 'uk_%';


-- ====================================================================
-- 升级说明
-- ====================================================================
-- 
-- 为什么需要这个升级：
-- 1. v2.6 版本引入了 relation_type 字段，支持 'include' 和 'exclude' 两种关联类型
-- 2. 同一个网站对同一个内容可以同时有两条记录：
--    - site_id=1, category_id=10, relation_type='include'  （正常关联）
--    - site_id=1, category_id=10, relation_type='exclude'  （排除默认配置）
-- 3. 旧的唯一约束 (site_id, category_id) 会阻止这种设计
-- 4. 新的唯一约束 (site_id, category_id, relation_type) 允许同一组合有不同类型
--
-- 应用场景：
-- - 网站A关联了分类B（include）
-- - 同时网站A也可以排除默认配置中的分类B（exclude）
-- - 这两条记录不冲突，分别控制不同的业务逻辑
--
-- 注意事项：
-- 1. 执行前请备份数据库
-- 2. 如果表中已有违反新约束的数据，需要先清理
-- 3. 建议在低峰期执行此升级
--
-- ====================================================================
