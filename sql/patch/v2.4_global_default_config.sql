-- =====================================================
-- 版本: v2.4
-- 功能: 全局默认配置+黑名单机制
-- 日期: 2025-12-29
-- 说明: 
--   1. 为site_category_relations表添加relation_type字段
--   2. 默认配置(site_id=0)对所有网站可见
--   3. 网站可通过relation_type='exclude'排除不需要的默认配置
-- =====================================================

USE `ry-vue`;

-- 步骤1: 为gb_site_category_relations表添加relation_type字段
ALTER TABLE `gb_site_category_relations` 
ADD COLUMN `relation_type` VARCHAR(20) NULL DEFAULT 'include' COMMENT '关联类型：include-正向关联 exclude-排除默认配置' AFTER `category_id`;

-- 步骤2: 为relation_type字段添加索引以优化查询
ALTER TABLE `gb_site_category_relations` 
ADD INDEX `idx_relation_type`(`relation_type` ASC) USING BTREE;

-- 步骤3: 添加复合索引以优化全局配置查询
ALTER TABLE `gb_site_category_relations` 
ADD INDEX `idx_site_category_type`(`site_id` ASC, `category_id` ASC, `relation_type` ASC) USING BTREE;

-- 步骤4: 更新现有数据，所有现有关联都是正向关联
UPDATE `gb_site_category_relations` SET `relation_type` = 'include' WHERE `relation_type` IS NULL;

-- 步骤5: 设置relation_type为NOT NULL（在更新完数据后）
ALTER TABLE `gb_site_category_relations` 
MODIFY COLUMN `relation_type` VARCHAR(20) NOT NULL DEFAULT 'include' COMMENT '关联类型：include-正向关联 exclude-排除默认配置';

-- =====================================================
-- 查询逻辑说明（供后端开发参考）
-- =====================================================
-- 
-- 查询某个网站的所有分类时：
-- 1. 获取该网站自己创建的分类（site_id = 当前网站ID）
-- 2. 获取所有默认配置分类（site_id = 0）
-- 3. 排除该网站明确排除的默认配置（relation_type = 'exclude'）
--
-- SQL示例：
-- SELECT c.* 
-- FROM gb_categories c
-- WHERE (
--     -- 网站自己的分类
--     c.site_id = #{siteId}
--     OR 
--     -- 默认配置分类（且未被排除）
--     (c.site_id = 0 AND c.id NOT IN (
--         SELECT category_id 
--         FROM gb_site_category_relations 
--         WHERE site_id = #{siteId} 
--         AND relation_type = 'exclude'
--     ))
-- )
-- AND c.status = '1'
-- AND c.del_flag = '0'
-- ORDER BY c.sort_order;
--
-- =====================================================

-- 步骤6: 创建示例数据（可选 - 用于测试）
-- 假设网站ID=2 要排除分类ID=2的默认配置
-- INSERT INTO `gb_site_category_relations` 
-- (site_id, category_id, relation_type, is_visible, is_editable, sort_order, create_by) 
-- VALUES 
-- (2, 2, 'exclude', '0', '0', 0, 'admin');

-- =====================================================
-- 升级完成提示
-- =====================================================
SELECT '✅ v2.4升级完成！全局默认配置+黑名单机制已启用' AS message;
SELECT '📌 后续步骤：' AS message;
SELECT '1. 修改后端查询逻辑，实现全局配置自动可见' AS step1;
SELECT '2. 前端增加"排除默认配置"的UI交互' AS step2;
SELECT '3. 测试全局配置的继承和排除功能' AS step3;
