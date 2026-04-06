-- ================================================================
-- 清理分类创建者的冗余关联记录
-- ================================================================
-- 说明：创建者网站通过 site_id 字段已经天然关联分类，不需要在关联表中存储
-- 此脚本删除所有创建者网站的 include 类型关联记录
-- 
-- 执行前建议：
-- 1. 备份数据库
-- 2. 在测试环境先验证
-- 3. 检查受影响的记录数
-- ================================================================

-- 1. 查看将要删除的记录（执行前预览）
SELECT 
    scr.id as relation_id,
    scr.site_id,
    scr.category_id,
    c.name as category_name,
    c.site_id as category_site_id,
    '创建者冗余关联' as relation_type
FROM gb_site_category_relations scr
INNER JOIN gb_categories c ON scr.category_id = c.id
WHERE scr.relation_type = 'include'
  AND scr.site_id = c.site_id
  AND c.site_id > 0
ORDER BY scr.site_id, scr.category_id;

-- 2. 统计将要删除的记录数
SELECT 
    COUNT(*) as will_be_deleted,
    '创建者冗余关联记录' as description
FROM gb_site_category_relations scr
INNER JOIN gb_categories c ON scr.category_id = c.id
WHERE scr.relation_type = 'include'
  AND scr.site_id = c.site_id
  AND c.site_id > 0;

-- 3. 执行删除（确认无误后取消注释执行）
-- DELETE scr FROM gb_site_category_relations scr
-- INNER JOIN gb_categories c ON scr.category_id = c.id
-- WHERE scr.relation_type = 'include'
--   AND scr.site_id = c.site_id
--   AND c.site_id > 0;

-- 4. 验证删除结果（删除后执行）
-- SELECT 
--     COUNT(*) as total_relations,
--     SUM(CASE WHEN scr.site_id = c.site_id THEN 1 ELSE 0 END) as creator_relations_remaining,
--     SUM(CASE WHEN scr.site_id != c.site_id THEN 1 ELSE 0 END) as cross_site_relations
-- FROM gb_site_category_relations scr
-- INNER JOIN gb_categories c ON scr.category_id = c.id
-- WHERE scr.relation_type = 'include';

-- 5. 查看剩余的关联关系分布（删除后执行）
-- SELECT 
--     scr.site_id,
--     COUNT(*) as relation_count,
--     COUNT(DISTINCT scr.category_id) as distinct_categories,
--     '跨站共享关联' as relation_type
-- FROM gb_site_category_relations scr
-- INNER JOIN gb_categories c ON scr.category_id = c.id
-- WHERE scr.relation_type = 'include'
--   AND scr.site_id != c.site_id
-- GROUP BY scr.site_id
-- ORDER BY relation_count DESC;
