-- ============================================
-- 标题池批次关联表扩展：支持跨站共享和可见性控制
-- 版本：v2.82
-- 日期：2026-01-02
-- ============================================

-- 1. 添加 is_visible 字段（在relation_type之后）
ALTER TABLE `gb_site_title_batch_relations` 
ADD COLUMN `is_visible` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '是否可见：0-隐藏 1-可见' AFTER `relation_type`;

-- 2. 修改 relation_type 字段注释和校对规则，支持 shared 类型
ALTER TABLE `gb_site_title_batch_relations` 
MODIFY COLUMN `relation_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'exclude' COMMENT '关联类型：shared-跨站共享 exclude-排除';

-- 3. 更新现有记录的 is_visible 默认值（exclude类型为不可见，shared类型为可见）
UPDATE `gb_site_title_batch_relations` 
SET `is_visible` = CASE WHEN `relation_type` = 'shared' THEN '1' ELSE '0' END 
WHERE `is_visible` IS NULL;

-- 4. 添加索引以优化查询性能
ALTER TABLE `gb_site_title_batch_relations` 
ADD INDEX `idx_relation_type` (`relation_type`) USING BTREE;

-- 5. 更新唯一索引，移除 relation_type（允许同一批次同时有 shared 和 exclude 关联）
-- 注意：这会删除并重新创建索引
ALTER TABLE `gb_site_title_batch_relations` 
DROP INDEX `uk_site_title_batch`,
ADD UNIQUE INDEX `uk_site_title_batch_type`(`site_id` ASC, `title_batch_id` ASC, `relation_type` ASC) USING BTREE;


