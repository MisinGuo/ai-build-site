-- ========================================
-- 主页绑定数据清理脚本
-- 日期: 2025-01-XX
-- 说明: 清理主页绑定表中的不一致数据
-- ========================================

-- 1. 查看当前的绑定状态
SELECT 
    id,
    article_id,
    game_id,
    game_box_id,
    homepage_locale,
    CASE 
        WHEN game_id IS NOT NULL AND game_box_id IS NOT NULL THEN '违反约束：同时绑定了游戏和盒子'
        WHEN game_id IS NULL AND game_box_id IS NULL THEN '违反约束：未绑定任何目标'
        ELSE '正常'
    END AS status
FROM gb_article_homepage_binding
ORDER BY article_id, homepage_locale;

-- 2. 删除同时绑定了游戏和盒子的记录（违反约束）
-- 注意：在生产环境执行前请先备份数据！
DELETE FROM gb_article_homepage_binding
WHERE game_id IS NOT NULL AND game_box_id IS NOT NULL;

-- 3. 删除未绑定任何目标的记录（违反约束）
DELETE FROM gb_article_homepage_binding
WHERE game_id IS NULL AND game_box_id IS NULL;

-- 4. 查找并清理重复绑定（同一文章+语言有多条记录）
-- 步骤1：查看重复的记录
SELECT article_id, homepage_locale, COUNT(*) as count
FROM gb_article_homepage_binding
GROUP BY article_id, homepage_locale
HAVING COUNT(*) > 1;

-- 步骤2：保留最新的记录，删除旧记录
-- 注意：此操作会保留 update_time 最新的记录，删除其他重复记录
DELETE t1 FROM gb_article_homepage_binding t1
INNER JOIN gb_article_homepage_binding t2 
WHERE 
    t1.article_id = t2.article_id 
    AND t1.homepage_locale = t2.homepage_locale
    AND t1.id < t2.id;

-- 5. 查找孤立的绑定记录（关联的文章、游戏或盒子已被删除）
-- 查找文章不存在的绑定
SELECT b.* 
FROM gb_article_homepage_binding b
LEFT JOIN gb_article a ON b.article_id = a.id
WHERE a.id IS NULL;

-- 查找游戏不存在的绑定
SELECT b.* 
FROM gb_article_homepage_binding b
LEFT JOIN gb_game g ON b.game_id = g.id
WHERE b.game_id IS NOT NULL AND g.id IS NULL;

-- 查找游戏盒子不存在的绑定
SELECT b.* 
FROM gb_article_homepage_binding b
LEFT JOIN gb_game_box gb ON b.game_box_id = gb.id
WHERE b.game_box_id IS NOT NULL AND gb.id IS NULL;

-- 6. 清理孤立记录（可选，谨慎执行）
-- 删除文章不存在的绑定
-- DELETE b FROM gb_article_homepage_binding b
-- LEFT JOIN gb_article a ON b.article_id = a.id
-- WHERE a.id IS NULL;

-- 删除游戏不存在的绑定
-- DELETE b FROM gb_article_homepage_binding b
-- LEFT JOIN gb_game g ON b.game_id = g.id
-- WHERE b.game_id IS NOT NULL AND g.id IS NULL;

-- 删除游戏盒子不存在的绑定
-- DELETE b FROM gb_article_homepage_binding b
-- LEFT JOIN gb_game_box gb ON b.game_box_id = gb.id
-- WHERE b.game_box_id IS NOT NULL AND gb.id IS NULL;

-- 7. 验证清理结果
SELECT 
    '总绑定记录数' as description,
    COUNT(*) as count
FROM gb_article_homepage_binding
UNION ALL
SELECT 
    '游戏主页绑定数',
    COUNT(*)
FROM gb_article_homepage_binding
WHERE game_id IS NOT NULL
UNION ALL
SELECT 
    '游戏盒子主页绑定数',
    COUNT(*)
FROM gb_article_homepage_binding
WHERE game_box_id IS NOT NULL
UNION ALL
SELECT 
    CONCAT('语言: ', homepage_locale),
    COUNT(*)
FROM gb_article_homepage_binding
GROUP BY homepage_locale;

-- 8. 验证唯一约束是否生效
-- 如果以下查询返回记录，说明存在违反唯一约束的数据
SELECT article_id, homepage_locale, COUNT(*) as duplicate_count
FROM gb_article_homepage_binding
GROUP BY article_id, homepage_locale
HAVING COUNT(*) > 1;
