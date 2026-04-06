-- ========================================
-- 修复文章关联表重复键问题
-- 日期: 2024-12-24
-- 问题: 发布文章时报错 Duplicate entry '1-1' for key 'uk_article_game_box'
-- 原因: 软删除后重新插入时，存在未清理的旧记录
-- ========================================

-- 1. 清理 gb_article_game_boxes 表的重复数据
-- 保留 del_flag='0' 的最新记录，删除其他重复记录
DELETE t1 FROM gb_article_game_boxes t1
INNER JOIN gb_article_game_boxes t2 
WHERE t1.article_id = t2.article_id 
  AND t1.game_box_id = t2.game_box_id
  AND t1.id < t2.id;

-- 2. 清理 gb_article_games 表的重复数据
DELETE t1 FROM gb_article_games t1
INNER JOIN gb_article_games t2 
WHERE t1.article_id = t2.article_id 
  AND t1.game_id = t2.game_id
  AND t1.id < t2.id;

-- 3. 清理 gb_article_dramas 表的重复数据（如果存在）
DELETE t1 FROM gb_article_dramas t1
INNER JOIN gb_article_dramas t2 
WHERE t1.article_id = t2.article_id 
  AND t1.drama_id = t2.drama_id
  AND t1.id < t2.id;

-- 4. 验证清理结果
-- 检查是否还有重复的 article_id + game_box_id 组合
SELECT article_id, game_box_id, COUNT(*) as cnt
FROM gb_article_game_boxes
WHERE del_flag = '0'
GROUP BY article_id, game_box_id
HAVING cnt > 1;

-- 检查是否还有重复的 article_id + game_id 组合
SELECT article_id, game_id, COUNT(*) as cnt
FROM gb_article_games
WHERE del_flag = '0'
GROUP BY article_id, game_id
HAVING cnt > 1;

-- 检查是否还有重复的 article_id + drama_id 组合
SELECT article_id, drama_id, COUNT(*) as cnt
FROM gb_article_dramas
WHERE del_flag = '0'
GROUP BY article_id, drama_id
HAVING cnt > 1;

-- ========================================
-- 注意事项:
-- 1. 此脚本会删除重复记录，保留ID最大的记录
-- 2. 建议在执行前先备份数据
-- 3. 执行后需要重启应用或清除缓存
-- 4. 后续需要修改Mapper XML避免此问题再次发生
-- ========================================
