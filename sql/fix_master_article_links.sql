-- ======================================
-- 修复文章与主文章的关联关系
-- 重要：只有与网站默认语言相同的文章才应该创建主文章
-- 其他语言的文章是翻译版本，应关联到主文章
-- ======================================

-- 第一步：查看网站配置和文章情况
SELECT 
    s.id as site_id,
    s.name as site_name,
    s.default_locale as site_default_locale,
    COUNT(a.id) as total_articles,
    SUM(CASE WHEN a.locale = s.default_locale THEN 1 ELSE 0 END) as default_locale_articles,
    SUM(CASE WHEN a.locale != s.default_locale THEN 1 ELSE 0 END) as translation_articles,
    SUM(CASE WHEN a.master_article_id IS NULL THEN 1 ELSE 0 END) as unlinked_articles
FROM gb_sites s
LEFT JOIN gb_articles a ON a.site_id = s.id AND a.del_flag = '0'
WHERE s.del_flag = '0'
GROUP BY s.id, s.name, s.default_locale;

-- 第二步：查看需要创建主文章的文章（只看默认语言的文章）
SELECT 
    a.id,
    a.title,
    a.locale,
    s.default_locale as site_default_locale,
    a.category_id,
    a.status,
    a.master_article_id,
    a.create_time,
    CASE 
        WHEN a.locale = s.default_locale THEN '需要创建主文章'
        ELSE '翻译版本，应关联到主文章'
    END as action_needed
FROM gb_articles a
INNER JOIN gb_sites s ON a.site_id = s.id AND s.del_flag = '0'
WHERE a.del_flag = '0' 
  AND (a.master_article_id IS NULL 
       OR a.master_article_id NOT IN (SELECT id FROM gb_master_articles WHERE del_flag = '0'))
ORDER BY a.site_id, a.locale, a.id;

-- 第三步：备份当前的主文章表（可选，建议在生产环境执行）
-- CREATE TABLE gb_master_articles_backup_20260113 AS SELECT * FROM gb_master_articles;

-- 第四步：删除那些没有对应文章内容的无效主文章记录
-- 保留已经正确关联的主文章
DELETE FROM gb_master_articles 
WHERE id NOT IN (
    SELECT DISTINCT master_article_id 
    FROM gb_articles 
    WHERE master_article_id IS NOT NULL 
      AND del_flag = '0'
  )
  AND del_flag = '0';

-- 第五步：只为网站默认语言的文章创建主文章记录
-- 这是关键：只有和网站默认语言相同的文章才会创建主文章
INSERT INTO gb_master_articles (
    site_id, 
    default_locale, 
    category_id, 
    is_ai_generated,
    prompt_template_id,
    generation_count,
    last_generation_id,
    is_published,
    is_top,
    is_recommend,
    sort_order,
    status,
    del_flag,
    create_by,
    create_time,
    update_by,
    update_time,
    publish_time,
    remark
)
SELECT 
    a.site_id,
    a.locale as default_locale,
    a.category_id,
    a.is_ai_generated,
    a.prompt_template_id,
    a.generation_count,
    a.last_generation_id,
    a.is_published,
    a.is_top,
    a.is_recommend,
    a.sort_order,
    a.status,
    '0' as del_flag,
    a.create_by,
    a.create_time,
    a.update_by,
    a.update_time,
    a.publish_time,
    CONCAT('从默认语言文章ID=', a.id, '自动创建') as remark
FROM gb_articles a
INNER JOIN gb_sites s ON a.site_id = s.id AND s.del_flag = '0'
WHERE a.del_flag = '0'
  AND a.locale = s.default_locale  -- 关键条件：只处理默认语言的文章
  AND (a.master_article_id IS NULL 
       OR a.master_article_id NOT IN (SELECT id FROM gb_master_articles WHERE del_flag = '0'))
  AND a.status != '2'; -- 不处理已下架的文章

-- 第六步：为默认语言的文章设置master_article_id（关联到刚创建的主文章）
-- 使用临时表来建立映射关系
CREATE TEMPORARY TABLE IF NOT EXISTS temp_article_master_mapping (
    article_id BIGINT,
    master_article_id BIGINT,
    PRIMARY KEY (article_id)
);

-- 填充映射表：将默认语言文章ID与新创建的主文章ID关联
-- 策略：根据备注中的文章ID找到对应的主文章
INSERT INTO temp_article_master_mapping (article_id, master_article_id)
SELECT 
    a.id as article_id,
    m.id as master_article_id
FROM gb_articles a
INNER JOIN gb_master_articles m ON m.remark LIKE CONCAT('%文章ID=', a.id, '%')
INNER JOIN gb_sites s ON a.site_id = s.id AND s.del_flag = '0'
WHERE a.del_flag = '0' 
  AND m.del_flag = '0'
  AND a.locale = s.default_locale  -- 只处理默认语言文章
  AND (a.master_article_id IS NULL 
       OR a.master_article_id NOT IN (SELECT id FROM gb_master_articles WHERE del_flag = '0'));

-- 更新默认语言文章的master_article_id
UPDATE gb_articles a
INNER JOIN temp_article_master_mapping tm ON a.id = tm.article_id
SET a.master_article_id = tm.master_article_id
WHERE a.del_flag = '0';

-- 清理临时表
DROP TEMPORARY TABLE IF EXISTS temp_article_master_mapping;

-- 第七步：处理翻译版本（非默认语言）文章的关联
-- 注意：这一步需要根据业务逻辑来匹配翻译文章和主文章
-- 如果有其他标识（如original_article_id），可以用它来关联
-- 这里先跳过，因为需要更多的业务信息来正确匹配翻译版本
-- 示例（需根据实际情况调整）：
-- UPDATE gb_articles a
-- INNER JOIN gb_articles a_default ON a.original_article_id = a_default.id
-- SET a.master_article_id = a_default.master_article_id
-- WHERE a.del_flag = '0'
--   AND a.locale != (SELECT default_locale FROM gb_sites WHERE id = a.site_id)
--   AND a.master_article_id IS NULL;

-- 第八步：验证修复结果
SELECT '===== 修复后的主文章列表（应该显示标题） =====' as info;

SELECT 
    m.id as 主文章ID,
    m.default_locale as 默认语言,
    m.category_id as 分类ID,
    m.status as 状态,
    a.title as 文章标题,
    a.author as 作者,
    a.view_count as 浏览量,
    a.id as 文章ID,
    a.locale as 文章语言,
    m.create_time as 创建时间
FROM gb_master_articles m
LEFT JOIN gb_articles a ON a.master_article_id = m.id 
    AND a.locale = m.default_locale 
    AND a.del_flag = '0'
WHERE m.del_flag = '0'
ORDER BY m.id;

-- 第九步：检查未关联的默认语言文章（应该为空）
SELECT '===== 未关联主文章的默认语言文章（应该为空） =====' as info;

SELECT 
    a.id as 文章ID,
    a.title as 标题,
    a.locale as 语言,
    s.default_locale as 网站默认语言,
    a.master_article_id as 主文章ID,
    a.status as 状态,
    a.create_time as 创建时间
FROM gb_articles a
INNER JOIN gb_sites s ON a.site_id = s.id AND s.del_flag = '0'
WHERE a.del_flag = '0' 
  AND a.locale = s.default_locale  -- 只看默认语言的文章
  AND (a.master_article_id IS NULL 
       OR a.master_article_id NOT IN (SELECT id FROM gb_master_articles WHERE del_flag = '0'))
ORDER BY a.id;

-- 第十步：查看翻译版本文章（非默认语言）的关联情况
SELECT '===== 翻译版本文章的关联情况 =====' as info;

SELECT 
    a.id as 文章ID,
    a.title as 标题,
    a.locale as 语言,
    s.default_locale as 网站默认语言,
    a.master_article_id as 主文章ID,
    m.default_locale as 主文章语言,
    a.status as 状态,
    CASE 
        WHEN a.master_article_id IS NULL THEN '未关联（需要手动处理）'
        ELSE '已关联'
    END as 关联状态
FROM gb_articles a
INNER JOIN gb_sites s ON a.site_id = s.id AND s.del_flag = '0'
LEFT JOIN gb_master_articles m ON a.master_article_id = m.id AND m.del_flag = '0'
WHERE a.del_flag = '0' 
  AND a.locale != s.default_locale  -- 只看非默认语言的文章（翻译版本）
ORDER BY a.id;
