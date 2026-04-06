-- 修复网站分类数据
-- 用途：确保数据库中有website类型的全局分类（site_id = 0）

-- 1. 检查是否存在website类型的分类
SELECT 
    COUNT(*) as total_count,
    COUNT(CASE WHEN site_id = 0 THEN 1 END) as global_count,
    COUNT(CASE WHEN site_id > 0 THEN 1 END) as site_count
FROM gb_categories 
WHERE category_type = 'website' AND del_flag = '0';

-- 2. 查看所有website分类的详细信息
SELECT id, site_id, parent_id, name, slug, category_type, status, create_time
FROM gb_categories 
WHERE category_type = 'website' AND del_flag = '0'
ORDER BY sort_order;

-- 3. 如果没有数据，插入默认的website分类（全局，site_id = 0）
INSERT INTO gb_categories (site_id, parent_id, category_type, name, slug, icon, description, sort_order, status, del_flag, create_by, create_time)
SELECT 0, 0, 'website', '官方网站', 'official', '🏢', '官方主站', 1, '1', '0', 'admin', NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM gb_categories WHERE category_type = 'website' AND slug = 'official' AND del_flag = '0'
);

INSERT INTO gb_categories (site_id, parent_id, category_type, name, slug, icon, description, sort_order, status, del_flag, create_by, create_time)
SELECT 0, 0, 'website', '推广网站', 'promotion', '📢', '推广营销站点', 2, '1', '0', 'admin', NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM gb_categories WHERE category_type = 'website' AND slug = 'promotion' AND del_flag = '0'
);

INSERT INTO gb_categories (site_id, parent_id, category_type, name, slug, icon, description, sort_order, status, del_flag, create_by, create_time)
SELECT 0, 0, 'website', '社区论坛', 'community', '💬', '玩家社区论坛', 3, '1', '0', 'admin', NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM gb_categories WHERE category_type = 'website' AND slug = 'community' AND del_flag = '0'
);

INSERT INTO gb_categories (site_id, parent_id, category_type, name, slug, icon, description, sort_order, status, del_flag, create_by, create_time)
SELECT 0, 0, 'website', '下载站', 'download', '📥', '游戏下载站', 4, '1', '0', 'admin', NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM gb_categories WHERE category_type = 'website' AND slug = 'download' AND del_flag = '0'
);

INSERT INTO gb_categories (site_id, parent_id, category_type, name, slug, icon, description, sort_order, status, del_flag, create_by, create_time)
SELECT 0, 0, 'website', '资讯站', 'news-site', '📰', '新闻资讯站', 5, '1', '0', 'admin', NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM gb_categories WHERE category_type = 'website' AND slug = 'news-site' AND del_flag = '0'
);

INSERT INTO gb_categories (site_id, parent_id, category_type, name, slug, icon, description, sort_order, status, del_flag, create_by, create_time)
SELECT 0, 0, 'website', '工具站', 'tool', '🔧', '辅助工具站', 6, '1', '0', 'admin', NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM gb_categories WHERE category_type = 'website' AND slug = 'tool' AND del_flag = '0'
);

-- 4. 将所有website类型的分类强制设置为全局（site_id = 0）
UPDATE gb_categories 
SET site_id = 0 
WHERE category_type = 'website' 
  AND (site_id IS NULL OR site_id != 0)
  AND del_flag = '0';

-- 5. 验证结果
SELECT 
    id, site_id, parent_id, name, slug, category_type, status, sort_order
FROM gb_categories 
WHERE category_type = 'website' AND del_flag = '0'
ORDER BY sort_order;

-- 预期结果：应该有6条记录，所有的site_id都是0
