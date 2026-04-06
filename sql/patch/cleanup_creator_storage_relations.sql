-- ================================================================
-- 清理存储配置创建者的冗余关联记录
-- ================================================================
-- 说明：创建者网站通过 site_id 字段已经天然关联存储配置，不需要在关联表中存储
-- 此脚本删除所有创建者网站的关联记录
-- 
-- 执行前建议：
-- 1. 备份数据库
-- 2. 在测试环境先验证
-- 3. 检查受影响的记录数
-- ================================================================

-- 1. 查看将要删除的记录（执行前预览）
SELECT 
    ssr.id as relation_id,
    ssr.site_id,
    ssr.storage_config_id,
    sc.name as storage_name,
    sc.site_id as storage_site_id,
    '创建者冗余关联' as relation_type
FROM gb_site_storage_config_relations ssr
INNER JOIN gb_storage_configs sc ON ssr.storage_config_id = sc.id
WHERE ssr.site_id = sc.site_id
  AND sc.site_id > 0
ORDER BY ssr.site_id, ssr.storage_config_id;

-- 2. 统计将要删除的记录数
SELECT 
    COUNT(*) as will_be_deleted,
    '创建者冗余关联记录' as description
FROM gb_site_storage_config_relations ssr
INNER JOIN gb_storage_configs sc ON ssr.storage_config_id = sc.id
WHERE ssr.site_id = sc.site_id
  AND sc.site_id > 0;

-- 3. 执行删除（确认无误后取消注释执行）
-- DELETE ssr FROM gb_site_storage_config_relations ssr
-- INNER JOIN gb_storage_configs sc ON ssr.storage_config_id = sc.id
-- WHERE ssr.site_id = sc.site_id
--   AND sc.site_id > 0;

-- 4. 验证删除结果（删除后执行）
-- SELECT 
--     COUNT(*) as total_relations,
--     SUM(CASE WHEN ssr.site_id = sc.site_id THEN 1 ELSE 0 END) as creator_relations_remaining,
--     SUM(CASE WHEN ssr.site_id != sc.site_id THEN 1 ELSE 0 END) as cross_site_relations
-- FROM gb_site_storage_config_relations ssr
-- INNER JOIN gb_storage_configs sc ON ssr.storage_config_id = sc.id;

-- 5. 查看剩余的关联关系分布（删除后执行）
-- SELECT 
--     ssr.site_id,
--     COUNT(*) as relation_count,
--     COUNT(DISTINCT ssr.storage_config_id) as distinct_storage_configs,
--     '跨站共享关联' as relation_type
-- FROM gb_site_storage_config_relations ssr
-- INNER JOIN gb_storage_configs sc ON ssr.storage_config_id = sc.id
-- WHERE ssr.site_id != sc.site_id
-- GROUP BY ssr.site_id
-- ORDER BY relation_count DESC;

-- 6. 显示每个网站的存储配置情况（删除后执行，用于验证）
-- SELECT 
--     s.id as site_id,
--     s.name as site_name,
--     COUNT(DISTINCT CASE WHEN sc.site_id = s.id THEN sc.id END) as own_storage_configs,
--     COUNT(DISTINCT CASE WHEN ssr.storage_config_id IS NOT NULL AND sc.site_id != s.id THEN sc.id END) as shared_storage_configs,
--     COUNT(DISTINCT CASE WHEN sc.site_id = 0 THEN sc.id END) as default_storage_configs
-- FROM gb_sites s
-- LEFT JOIN gb_storage_configs sc ON sc.site_id = s.id OR sc.site_id = 0
-- LEFT JOIN gb_site_storage_config_relations ssr ON ssr.site_id = s.id AND ssr.storage_config_id = sc.id
-- GROUP BY s.id, s.name
-- ORDER BY s.id;
