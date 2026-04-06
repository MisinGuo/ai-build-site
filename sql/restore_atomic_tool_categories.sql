-- =============================================
-- 恢复原子工具分类数据
-- 创建日期: 2026-01-06
-- 说明: 如果原子工具的分类类型和分类数据丢失，使用此脚本恢复
-- =============================================

-- 1. 确保分类类型存在
INSERT INTO gb_category_types (value, label, tag_type, description, sort_order, status, is_system, create_by, create_time, remark)
VALUES ('atomic_tool', '原子工具分类', 'primary', '用于原子工具管理页面的分类', 14, '0', '1', 'admin', NOW(), '用于工作流编排中的原子工具分类管理')
ON DUPLICATE KEY UPDATE 
    label = '原子工具分类',
    tag_type = 'primary',
    description = '用于原子工具管理页面的分类',
    remark = '用于工作流编排中的原子工具分类管理';

-- 2. 插入原子工具的分类（如果不存在）
INSERT INTO gb_categories (id, site_id, parent_id, category_type, name, slug, icon, description, sort_order, status, del_flag, create_by, create_time, update_by, update_time)
VALUES 
    (127, 0, 0, 'atomic_tool', 'AI工具', 'ai_tools', '🤖', 'AI驱动的原子工具', 1, '1', '0', 'admin', NOW(), 'admin', NOW()),
    (128, 0, 0, 'atomic_tool', 'API工具', 'api_tools', '🔌', '调用外部API的工具', 2, '1', '0', 'admin', NOW(), 'admin', NOW()),
    (129, 0, 0, 'atomic_tool', '内置工具', 'builtin_tools', '⚙️', '系统内置功能工具', 3, '1', '0', 'admin', NOW(), 'admin', NOW()),
    (130, 0, 0, 'atomic_tool', '文本处理', 'text_processing', '📝', '文本处理相关工具', 4, '1', '0', 'admin', NOW(), 'admin', NOW()),
    (131, 0, 0, 'atomic_tool', '图片处理', 'image_processing', '🖼️', '图片处理相关工具', 5, '1', '0', 'admin', NOW(), 'admin', NOW()),
    (132, 0, 0, 'atomic_tool', 'SEO优化', 'seo_optimization', '🔍', 'SEO优化相关工具', 6, '1', '0', 'admin', NOW(), 'admin', NOW())
ON DUPLICATE KEY UPDATE 
    category_type = VALUES(category_type),
    name = VALUES(name),
    slug = VALUES(slug),
    icon = VALUES(icon),
    description = VALUES(description),
    sort_order = VALUES(sort_order),
    status = VALUES(status),
    update_by = 'admin',
    update_time = NOW();

-- 3. 验证结果
SELECT '========== 分类类型 ==========' AS info;
SELECT * FROM gb_category_types WHERE value = 'atomic_tool';

SELECT '========== 原子工具分类 ==========' AS info;
SELECT id, site_id, parent_id, category_type, name, slug, icon, description, sort_order, status 
FROM gb_categories 
WHERE category_type = 'atomic_tool' 
ORDER BY sort_order;

SELECT '========== 原子工具数量统计 ==========' AS info;
SELECT 
    c.name AS category_name,
    COUNT(at.id) AS tool_count
FROM gb_categories c
LEFT JOIN gb_atomic_tool at ON at.category_id = c.id AND at.enabled = '1'
WHERE c.category_type = 'atomic_tool'
GROUP BY c.id, c.name
ORDER BY c.sort_order;

-- =============================================
-- 执行说明
-- =============================================
-- 1. 直接执行此脚本即可恢复原子工具的分类数据
-- 2. 使用 ON DUPLICATE KEY UPDATE 确保数据安全
-- 3. 执行后会显示验证结果
-- =============================================
