-- ========================================
-- 文章关联查询 - 常用SQL示例
-- 日期: 2025-12-24
-- 说明: 提供各种常见业务场景的查询SQL
-- ========================================

-- ========================================
-- 1. 全局搜索相关查询
-- ========================================

-- 1.1 查询某游戏的所有文章（全局搜索，不限网站）
SELECT 
  a.id,
  a.site_id,
  s.name as site_name,
  a.title,
  a.slug,
  a.cover_url,
  a.description,
  a.published_at,
  a.view_count,
  ag.display_order
FROM gb_article_games ag
JOIN gb_articles a ON ag.article_id = a.id
JOIN gb_sites s ON a.site_id = s.id
WHERE ag.game_id = ?  -- 游戏ID
  AND a.is_published = '1'
  AND a.del_flag = '0'
ORDER BY ag.display_order, a.published_at DESC
LIMIT ? OFFSET ?;

-- 1.2 查询某游戏盒子的所有文章（全局搜索）
SELECT 
  a.id,
  a.site_id,
  s.name as site_name,
  a.title,
  a.slug,
  a.cover_url,
  a.description,
  a.published_at,
  a.view_count,
  agb.display_order
FROM gb_article_game_boxes agb
JOIN gb_articles a ON agb.article_id = a.id
JOIN gb_sites s ON a.site_id = s.id
WHERE agb.game_box_id = ?  -- 游戏盒子ID
  AND a.is_published = '1'
  AND a.del_flag = '0'
ORDER BY agb.display_order, a.published_at DESC
LIMIT ? OFFSET ?;

-- 1.3 查询某游戏在各网站的文章数分布
SELECT 
  s.id as site_id,
  s.name as site_name,
  s.domain,
  COUNT(DISTINCT a.id) as article_count
FROM gb_sites s
INNER JOIN gb_articles a ON s.id = a.site_id AND a.is_published = '1'
INNER JOIN gb_article_games ag ON a.id = ag.article_id
WHERE ag.game_id = ?  -- 游戏ID
GROUP BY s.id, s.name, s.domain
ORDER BY article_count DESC;

-- ========================================
-- 2. 某网站相关查询
-- ========================================

-- 2.1 查询某网站下某游戏的文章
SELECT 
  a.id,
  a.title,
  a.slug,
  a.cover_url,
  a.description,
  a.published_at,
  a.view_count,
  ag.display_order
FROM gb_article_games ag
JOIN gb_articles a ON ag.article_id = a.id
WHERE ag.game_id = ?     -- 游戏ID
  AND a.site_id = ?      -- 网站ID
  AND a.is_published = '1'
  AND a.del_flag = '0'
ORDER BY ag.display_order, a.published_at DESC
LIMIT ? OFFSET ?;

-- 2.2 查询某网站下某游戏盒子的文章
SELECT 
  a.id,
  a.title,
  a.slug,
  a.cover_url,
  a.description,
  a.published_at,
  a.view_count,
  agb.display_order
FROM gb_article_game_boxes agb
JOIN gb_articles a ON agb.article_id = a.id
WHERE agb.game_box_id = ?  -- 游戏盒子ID
  AND a.site_id = ?        -- 网站ID
  AND a.is_published = '1'
  AND a.del_flag = '0'
ORDER BY agb.display_order, a.published_at DESC
LIMIT ? OFFSET ?;

-- 2.3 查询某网站下所有游戏的文章数统计
SELECT 
  g.id as game_id,
  g.name as game_name,
  g.icon_url,
  COUNT(DISTINCT ag.article_id) as article_count
FROM gb_games g
LEFT JOIN gb_article_games ag ON g.id = ag.game_id
LEFT JOIN gb_articles a ON ag.article_id = a.id AND a.site_id = ? AND a.is_published = '1'
WHERE (g.site_id = ? OR g.site_id IS NULL)  -- 本网站的游戏 或 全局游戏
  AND g.status = '1'
GROUP BY g.id, g.name, g.icon_url
HAVING article_count > 0
ORDER BY article_count DESC;

-- 2.4 查询某网站下所有游戏盒子的文章数统计
SELECT 
  gb.id as game_box_id,
  gb.name as game_box_name,
  gb.icon_url,
  COUNT(DISTINCT agb.article_id) as article_count
FROM gb_game_boxes gb
LEFT JOIN gb_article_game_boxes agb ON gb.id = agb.game_box_id
LEFT JOIN gb_articles a ON agb.article_id = a.id AND a.site_id = ? AND a.is_published = '1'
WHERE (gb.site_id = ? OR gb.site_id IS NULL)  -- 本网站的盒子 或 全局盒子
  AND gb.status = '1'
GROUP BY gb.id, gb.name, gb.icon_url
HAVING article_count > 0
ORDER BY article_count DESC;

-- ========================================
-- 3. 文章详情相关查询
-- ========================================

-- 3.1 查询某文章关联的所有游戏
SELECT 
  g.id,
  g.name,
  g.short_name,
  g.icon_url,
  g.cover_url,
  g.download_url,
  g.android_url,
  g.ios_url,
  g.description,
  g.rating,
  g.download_count,
  ag.display_order
FROM gb_article_games ag
JOIN gb_games g ON ag.game_id = g.id
WHERE ag.article_id = ?  -- 文章ID
  AND g.status = '1'
ORDER BY ag.display_order;

-- 3.2 查询某文章关联的所有游戏盒子
SELECT 
  gb.id,
  gb.name,
  gb.icon_url,
  gb.cover_url,
  gb.download_url,
  gb.android_url,
  gb.ios_url,
  gb.description,
  gb.discount_rate,
  gb.game_count,
  agb.display_order
FROM gb_article_game_boxes agb
JOIN gb_game_boxes gb ON agb.game_box_id = gb.id
WHERE agb.article_id = ?  -- 文章ID
  AND gb.status = '1'
ORDER BY agb.display_order;

-- 3.3 查询文章的模板变量数据（用于前端渲染）
-- 游戏数据
SELECT 
  ag.display_order,
  CONCAT('game', ag.display_order) as variable_key,
  JSON_OBJECT(
    'id', g.id,
    'name', g.name,
    'shortName', g.short_name,
    'iconUrl', g.icon_url,
    'coverUrl', g.cover_url,
    'downloadUrl', g.download_url,
    'androidUrl', g.android_url,
    'iosUrl', g.ios_url,
    'description', g.description,
    'rating', g.rating,
    'downloadCount', g.download_count
  ) as game_data
FROM gb_article_games ag
JOIN gb_games g ON ag.game_id = g.id
WHERE ag.article_id = ?
  AND g.status = '1'
ORDER BY ag.display_order;

-- 游戏盒子数据
SELECT 
  agb.display_order,
  CONCAT('gameBox', agb.display_order) as variable_key,
  JSON_OBJECT(
    'id', gb.id,
    'name', gb.name,
    'iconUrl', gb.icon_url,
    'coverUrl', gb.cover_url,
    'downloadUrl', gb.download_url,
    'androidUrl', gb.android_url,
    'iosUrl', gb.ios_url,
    'description', gb.description,
    'discountRate', gb.discount_rate,
    'gameCount', gb.game_count
  ) as game_box_data
FROM gb_article_game_boxes agb
JOIN gb_game_boxes gb ON agb.game_box_id = gb.id
WHERE agb.article_id = ?
  AND gb.status = '1'
ORDER BY agb.display_order;

-- ========================================
-- 4. 推荐/相关文章查询
-- ========================================

-- 4.1 查询与某文章相关的其他文章（基于共同的游戏）
SELECT DISTINCT
  a2.id,
  a2.title,
  a2.slug,
  a2.cover_url,
  a2.published_at,
  COUNT(DISTINCT ag2.game_id) as common_game_count
FROM gb_article_games ag1
JOIN gb_article_games ag2 ON ag1.game_id = ag2.game_id AND ag2.article_id != ?
JOIN gb_articles a2 ON ag2.article_id = a2.id
WHERE ag1.article_id = ?  -- 当前文章ID
  AND a2.is_published = '1'
  AND a2.del_flag = '0'
  AND a2.site_id = ?      -- 限定同一网站（可选）
GROUP BY a2.id, a2.title, a2.slug, a2.cover_url, a2.published_at
ORDER BY common_game_count DESC, a2.published_at DESC
LIMIT 10;

-- 4.2 查询某游戏的最新文章（首页推荐）
SELECT 
  a.id,
  a.site_id,
  a.title,
  a.slug,
  a.cover_url,
  a.description,
  a.published_at,
  a.view_count
FROM gb_article_games ag
JOIN gb_articles a ON ag.article_id = a.id
WHERE ag.game_id = ?
  AND a.is_published = '1'
  AND a.del_flag = '0'
ORDER BY a.published_at DESC
LIMIT 5;

-- ========================================
-- 5. 统计分析查询
-- ========================================

-- 5.1 统计某网站各游戏的文章数（Top 10）
SELECT 
  g.id,
  g.name,
  g.icon_url,
  COUNT(DISTINCT a.id) as article_count,
  SUM(a.view_count) as total_views
FROM gb_games g
JOIN gb_article_games ag ON g.id = ag.game_id
JOIN gb_articles a ON ag.article_id = a.id
WHERE a.site_id = ?
  AND a.is_published = '1'
  AND g.status = '1'
GROUP BY g.id, g.name, g.icon_url
ORDER BY article_count DESC, total_views DESC
LIMIT 10;

-- 5.2 统计某网站各月份的文章-游戏关联数
SELECT 
  DATE_FORMAT(a.published_at, '%Y-%m') as month,
  COUNT(DISTINCT a.id) as article_count,
  COUNT(ag.id) as total_game_relations,
  AVG(game_per_article.game_count) as avg_games_per_article
FROM gb_articles a
LEFT JOIN gb_article_games ag ON a.id = ag.article_id
LEFT JOIN (
  SELECT article_id, COUNT(*) as game_count
  FROM gb_article_games
  GROUP BY article_id
) game_per_article ON a.id = game_per_article.article_id
WHERE a.site_id = ?
  AND a.is_published = '1'
  AND a.published_at >= DATE_SUB(NOW(), INTERVAL 12 MONTH)
GROUP BY DATE_FORMAT(a.published_at, '%Y-%m')
ORDER BY month DESC;

-- 5.3 查询没有关联任何游戏/游戏盒子的文章
SELECT 
  a.id,
  a.title,
  a.slug,
  a.published_at
FROM gb_articles a
LEFT JOIN gb_article_games ag ON a.id = ag.article_id
LEFT JOIN gb_article_game_boxes agb ON a.id = agb.article_id
WHERE a.site_id = ?
  AND a.is_published = '1'
  AND ag.id IS NULL
  AND agb.id IS NULL
ORDER BY a.published_at DESC;

-- 5.4 统计关联数量分布
SELECT 
  game_count,
  COUNT(*) as article_count
FROM (
  SELECT 
    a.id,
    COUNT(ag.game_id) as game_count
  FROM gb_articles a
  LEFT JOIN gb_article_games ag ON a.id = ag.article_id
  WHERE a.site_id = ?
    AND a.is_published = '1'
  GROUP BY a.id
) t
GROUP BY game_count
ORDER BY game_count;

-- ========================================
-- 6. 管理后台查询
-- ========================================

-- 6.1 查询某网站所有文章及其关联统计（管理列表）
SELECT 
  a.id,
  a.title,
  a.slug,
  a.is_published,
  a.published_at,
  a.view_count,
  a.create_time,
  a.update_time,
  (SELECT COUNT(*) FROM gb_article_games WHERE article_id = a.id) as game_count,
  (SELECT COUNT(*) FROM gb_article_game_boxes WHERE article_id = a.id) as game_box_count
FROM gb_articles a
WHERE a.site_id = ?
  AND a.del_flag = '0'
ORDER BY a.update_time DESC
LIMIT ? OFFSET ?;

-- 6.2 检查文章关联的游戏/游戏盒子是否存在或已删除
SELECT 
  'game' as type,
  ag.id as relation_id,
  ag.article_id,
  ag.game_id,
  CASE WHEN g.id IS NULL THEN 1 ELSE 0 END as is_missing
FROM gb_article_games ag
LEFT JOIN gb_games g ON ag.game_id = g.id
WHERE ag.article_id IN (?)  -- 文章ID列表
HAVING is_missing = 1

UNION ALL

SELECT 
  'game_box' as type,
  agb.id as relation_id,
  agb.article_id,
  agb.game_box_id,
  CASE WHEN gb.id IS NULL THEN 1 ELSE 0 END as is_missing
FROM gb_article_game_boxes agb
LEFT JOIN gb_game_boxes gb ON agb.game_box_id = gb.id
WHERE agb.article_id IN (?)  -- 文章ID列表
HAVING is_missing = 1;

-- ========================================
-- 7. 性能优化查询
-- ========================================

-- 7.1 使用子查询优化（仅查询ID列表）
-- 先查询关联ID，再获取详情（适合复杂过滤条件）
SELECT a.* 
FROM gb_articles a
WHERE a.id IN (
  SELECT ag.article_id
  FROM gb_article_games ag
  WHERE ag.game_id = ?
)
AND a.site_id = ?
AND a.is_published = '1'
ORDER BY a.published_at DESC
LIMIT ? OFFSET ?;

-- 7.2 使用EXISTS优化（不需要COUNT）
SELECT a.*
FROM gb_articles a
WHERE a.site_id = ?
  AND a.is_published = '1'
  AND EXISTS (
    SELECT 1 
    FROM gb_article_games ag 
    WHERE ag.article_id = a.id 
      AND ag.game_id = ?
  )
ORDER BY a.published_at DESC
LIMIT ? OFFSET ?;

-- ========================================
-- 8. 数据维护查询
-- ========================================

-- 8.1 清理孤立的关联记录（文章已删除）
DELETE ag FROM gb_article_games ag
LEFT JOIN gb_articles a ON ag.article_id = a.id
WHERE a.id IS NULL;

DELETE agb FROM gb_article_game_boxes agb
LEFT JOIN gb_articles a ON agb.article_id = a.id
WHERE a.id IS NULL;

-- 8.2 更新display_order（重新排序）
-- 为某文章的游戏关联重新排序
SET @row_num = 0;
UPDATE gb_article_games
SET display_order = (@row_num := @row_num + 1)
WHERE article_id = ?
ORDER BY display_order, id;

-- 8.3 批量更新display_order（通过ID列表）
-- 示例：UPDATE gb_article_games SET display_order = 1 WHERE id = 1;
-- 示例：UPDATE gb_article_games SET display_order = 2 WHERE id = 2;
-- 等等...需要在应用层循环执行

-- ========================================
-- 使用提示
-- ========================================
/*
1. 所有查询都应该使用参数化查询（PreparedStatement），防止SQL注入
2. 分页查询建议使用 LIMIT ? OFFSET ? 或 LIMIT ?, ?
3. 对于频繁查询，建议在应用层添加Redis缓存
4. 定期执行 ANALYZE TABLE 更新统计信息
5. 监控慢查询日志，优化性能瓶颈
*/
