-- 修复站点-文章关联表的数据
-- 确保 gb_site_article_relations 表中的 article_id 字段存储的是 master_article_id

-- 1. 检查当前 gb_site_article_relations 表中的数据情况
SELECT 
    'site_article_relations_current' as table_name,
    COUNT(*) as total_records,
    COUNT(DISTINCT article_id) as distinct_articles,
    COUNT(DISTINCT site_id) as distinct_sites
FROM gb_site_article_relations;

-- 2. 检查 article_id 是否对应的是 master_article_id 还是普通 article_id
SELECT 
    'check_article_id_type' as check_type,
    sar.article_id,
    ma.id as master_article_id,
    a.id as article_id,
    a.master_article_id as article_master_id,
    CASE 
        WHEN sar.article_id = ma.id THEN 'master_article_id'
        WHEN sar.article_id = a.id THEN 'article_id'
        ELSE 'unknown'
    END as id_type
FROM gb_site_article_relations sar
LEFT JOIN gb_master_articles ma ON sar.article_id = ma.id
LEFT JOIN gb_articles a ON sar.article_id = a.id
ORDER BY sar.id
LIMIT 10;

-- 3. 如果发现 article_id 存储的是普通文章ID而不是master_article_id，则进行修复
-- 首先备份当前数据
CREATE TABLE IF NOT EXISTS gb_site_article_relations_backup_v310 AS
SELECT * FROM gb_site_article_relations;

-- 4. 更新 article_id 为对应的 master_article_id（如果需要）
UPDATE gb_site_article_relations sar
INNER JOIN gb_articles a ON sar.article_id = a.id
SET sar.article_id = a.master_article_id
WHERE EXISTS (
    SELECT 1 FROM gb_articles a2 
    WHERE a2.id = sar.article_id 
    AND a2.master_article_id IS NOT NULL
);

-- 5. 删除无效的关联记录（找不到对应文章的）
DELETE FROM gb_site_article_relations 
WHERE article_id NOT IN (
    SELECT DISTINCT master_article_id 
    FROM gb_articles 
    WHERE master_article_id IS NOT NULL
) AND article_id NOT IN (
    SELECT id FROM gb_master_articles WHERE del_flag = '0'
);

-- 6. 验证修复结果
SELECT 
    'after_fix_verification' as check_type,
    COUNT(*) as total_relations,
    COUNT(DISTINCT sar.article_id) as distinct_master_articles,
    COUNT(DISTINCT sar.site_id) as distinct_sites
FROM gb_site_article_relations sar
INNER JOIN gb_master_articles ma ON sar.article_id = ma.id
WHERE ma.del_flag = '0';

-- 7. 显示修复后的数据示例
SELECT 
    sar.id,
    sar.site_id,
    s.name as site_name,
    sar.article_id as master_article_id,
    ma.title as master_title,
    sar.relation_type,
    sar.is_visible
FROM gb_site_article_relations sar
INNER JOIN gb_master_articles ma ON sar.article_id = ma.id
LEFT JOIN gb_sites s ON sar.site_id = s.id
WHERE ma.del_flag = '0'
ORDER BY sar.id
LIMIT 10;