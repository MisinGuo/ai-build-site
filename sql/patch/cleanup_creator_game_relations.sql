-- ================================================================
-- 清理游戏创建者的冗余关联记录
-- ================================================================
-- 说明：创建者网站通过 site_id 字段已经天然关联游戏，不需要在关联表中存储
-- 此脚本删除所有创建者网站的关联记录
-- 
-- 执行前建议：
-- 1. 备份数据库
-- 2. 在测试环境先验证
-- 3. 检查受影响的记录数
-- ================================================================

-- 1. 查看将要删除的记录（执行前预览）
SELECT 
    sgr.id as relation_id,
    sgr.site_id,
    sgr.game_id,
    g.name as game_name,
    g.site_id as game_site_id,
    '创建者冗余关联' as relation_type
FROM gb_site_game_relations sgr
INNER JOIN gb_games g ON sgr.game_id = g.id
WHERE sgr.site_id = g.site_id
  AND g.site_id > 0
ORDER BY sgr.site_id, sgr.game_id;

-- 2. 统计将要删除的记录数
SELECT 
    COUNT(*) as will_be_deleted,
    '创建者冗余关联记录' as description
FROM gb_site_game_relations sgr
INNER JOIN gb_games g ON sgr.game_id = g.id
WHERE sgr.site_id = g.site_id
  AND g.site_id > 0;

-- 3. 执行删除（确认无误后取消注释执行）
-- DELETE sgr FROM gb_site_game_relations sgr
-- INNER JOIN gb_games g ON sgr.game_id = g.id
-- WHERE sgr.site_id = g.site_id
--   AND g.site_id > 0;

-- 4. 验证删除结果（删除后执行）
-- SELECT 
--     COUNT(*) as total_relations,
--     SUM(CASE WHEN sgr.site_id = g.site_id THEN 1 ELSE 0 END) as creator_relations_remaining,
--     SUM(CASE WHEN sgr.site_id != g.site_id THEN 1 ELSE 0 END) as cross_site_relations
-- FROM gb_site_game_relations sgr
-- INNER JOIN gb_games g ON sgr.game_id = g.id;

-- 5. 查看剩余的关联关系分布（删除后执行）
-- SELECT 
--     sgr.site_id,
--     COUNT(*) as relation_count,
--     COUNT(DISTINCT sgr.game_id) as distinct_games,
--     '跨站共享关联' as relation_type
-- FROM gb_site_game_relations sgr
-- INNER JOIN gb_games g ON sgr.game_id = g.id
-- WHERE sgr.site_id != g.site_id
-- GROUP BY sgr.site_id
-- ORDER BY relation_count DESC;

-- 6. 显示每个网站的游戏情况（删除后执行，用于验证）
-- SELECT 
--     s.id as site_id,
--     s.name as site_name,
--     COUNT(DISTINCT CASE WHEN g.site_id = s.id THEN g.id END) as own_games,
--     COUNT(DISTINCT CASE WHEN sgr.game_id IS NOT NULL AND g.site_id != s.id THEN g.id END) as shared_games,
--     COUNT(DISTINCT CASE WHEN g.site_id = 0 THEN g.id END) as default_games
-- FROM gb_sites s
-- LEFT JOIN gb_games g ON g.site_id = s.id OR g.site_id = 0
-- LEFT JOIN gb_site_game_relations sgr ON sgr.site_id = s.id AND sgr.game_id = g.id
-- GROUP BY s.id, s.name
-- ORDER BY s.id;
