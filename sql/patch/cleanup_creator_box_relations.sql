-- ================================================================
-- 清理游戏盒子创建者的冗余关联记录
-- ================================================================
-- 说明：创建者网站通过 site_id 字段已经天然关联游戏盒子，不需要在关联表中存储
-- 此脚本删除所有创建者网站的关联记录
-- 
-- 执行前建议：
-- 1. 备份数据库
-- 2. 在测试环境先验证
-- 3. 检查受影响的记录数
-- ================================================================

-- 1. 查看将要删除的记录（执行前预览）
SELECT 
    sbr.id as relation_id,
    sbr.site_id,
    sbr.box_id,
    b.name as box_name,
    b.site_id as box_site_id,
    '创建者冗余关联' as relation_type
FROM gb_site_box_relations sbr
INNER JOIN gb_game_boxes b ON sbr.box_id = b.id
WHERE sbr.site_id = b.site_id
  AND b.site_id > 0
ORDER BY sbr.site_id, sbr.box_id;

-- 2. 统计将要删除的记录数
SELECT 
    COUNT(*) as will_be_deleted,
    '创建者冗余关联记录' as description
FROM gb_site_box_relations sbr
INNER JOIN gb_game_boxes b ON sbr.box_id = b.id
WHERE sbr.site_id = b.site_id
  AND b.site_id > 0;

-- 3. 执行删除（确认无误后取消注释执行）
-- DELETE sbr FROM gb_site_box_relations sbr
-- INNER JOIN gb_game_boxes b ON sbr.box_id = b.id
-- WHERE sbr.site_id = b.site_id
--   AND b.site_id > 0;

-- 4. 验证删除结果（删除后执行）
-- SELECT 
--     COUNT(*) as total_relations,
--     SUM(CASE WHEN sbr.site_id = b.site_id THEN 1 ELSE 0 END) as creator_relations_remaining,
--     SUM(CASE WHEN sbr.site_id != b.site_id THEN 1 ELSE 0 END) as cross_site_relations
-- FROM gb_site_box_relations sbr
-- INNER JOIN gb_game_boxes b ON sbr.box_id = b.id;

-- 5. 查看剩余的关联关系分布（删除后执行）
-- SELECT 
--     sbr.site_id,
--     COUNT(*) as relation_count,
--     COUNT(DISTINCT sbr.box_id) as distinct_boxes,
--     '跨站共享关联' as relation_type
-- FROM gb_site_box_relations sbr
-- INNER JOIN gb_game_boxes b ON sbr.box_id = b.id
-- WHERE sbr.site_id != b.site_id
-- GROUP BY sbr.site_id
-- ORDER BY relation_count DESC;

-- 6. 显示每个网站的游戏盒子情况（删除后执行，用于验证）
-- SELECT 
--     s.id as site_id,
--     s.name as site_name,
--     COUNT(DISTINCT CASE WHEN b.site_id = s.id THEN b.id END) as own_boxes,
--     COUNT(DISTINCT CASE WHEN sbr.box_id IS NOT NULL AND b.site_id != s.id THEN b.id END) as shared_boxes,
--     COUNT(DISTINCT CASE WHEN b.site_id = 0 THEN b.id END) as default_boxes
-- FROM gb_sites s
-- LEFT JOIN gb_game_boxes b ON b.site_id = s.id OR b.site_id = 0
-- LEFT JOIN gb_site_box_relations sbr ON sbr.site_id = s.id AND sbr.box_id = b.id
-- GROUP BY s.id, s.name
-- ORDER BY s.id;
