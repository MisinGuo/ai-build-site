-- ================================================================
-- 清理文章创建者的冗余关联记录
-- ================================================================
-- 说明：创建者网站通过 site_id 字段已经天然关联文章，不需要在关联表中存储
-- 此脚本删除所有创建者网站的关联记录
-- 
-- 执行前建议：
-- 1. 备份数据库
-- 2. 在测试环境先验证
-- 3. 检查受影响的记录数
-- ================================================================

-- 1. 查看将要删除的记录（执行前预览）
SELECT 
    sar.id as relation_id,
    sar.site_id,
    sar.article_id,
    a.title as article_title,
    a.site_id as article_site_id,
    '创建者冗余关联' as relation_type
FROM gb_site_article_relations sar
INNER JOIN gb_articles a ON sar.article_id = a.id
WHERE sar.site_id = a.site_id
  AND a.site_id > 0
ORDER BY sar.site_id, sar.article_id;

-- 2. 统计将要删除的记录数
SELECT 
    COUNT(*) as will_be_deleted,
    '创建者冗余关联记录' as description
FROM gb_site_article_relations sar
INNER JOIN gb_articles a ON sar.article_id = a.id
WHERE sar.site_id = a.site_id
  AND a.site_id > 0;

-- 3. 执行删除（确认无误后取消注释执行）
-- DELETE sar FROM gb_site_article_relations sar
-- INNER JOIN gb_articles a ON sar.article_id = a.id
-- WHERE sar.site_id = a.site_id
--   AND a.site_id > 0;

-- 4. 验证删除结果（删除后执行）
-- SELECT 
--     COUNT(*) as total_relations,
--     SUM(CASE WHEN sar.site_id = a.site_id THEN 1 ELSE 0 END) as creator_relations_remaining,
--     SUM(CASE WHEN sar.site_id != a.site_id THEN 1 ELSE 0 END) as cross_site_relations
-- FROM gb_site_article_relations sar
-- INNER JOIN gb_articles a ON sar.article_id = a.id;

-- 5. 查看剩余的关联关系分布（删除后执行）
-- SELECT 
--     sar.site_id,
--     COUNT(*) as relation_count,
--     COUNT(DISTINCT sar.article_id) as distinct_articles,
--     '跨站共享关联' as relation_type
-- FROM gb_site_article_relations sar
-- INNER JOIN gb_articles a ON sar.article_id = a.id
-- WHERE sar.site_id != a.site_id
-- GROUP BY sar.site_id
-- ORDER BY relation_count DESC;

-- 6. 显示每个网站的文章情况（删除后执行，用于验证）
-- SELECT 
--     s.id as site_id,
--     s.name as site_name,
--     COUNT(DISTINCT CASE WHEN a.site_id = s.id THEN a.id END) as own_articles,
--     COUNT(DISTINCT CASE WHEN sar.article_id IS NOT NULL AND a.site_id != s.id THEN a.id END) as shared_articles,
--     COUNT(DISTINCT CASE WHEN a.site_id = 0 THEN a.id END) as default_articles
-- FROM gb_sites s
-- LEFT JOIN gb_articles a ON a.site_id = s.id OR a.site_id = 0
-- LEFT JOIN gb_site_article_relations sar ON sar.site_id = s.id AND sar.article_id = a.id
-- GROUP BY s.id, s.name
-- ORDER BY s.id;
