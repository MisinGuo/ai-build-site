-- =============================================
-- 清理内容工具和文章生成功能
-- 创建日期: 2026-01-06
-- 说明: 删除 content_tool 和 generation 功能相关的所有数据库数据
-- =============================================

-- 1. 删除菜单数据（需要先查询菜单ID）
-- 查找内容工具和文章生成相关的菜单
SELECT menu_id, menu_name, perms, path FROM sys_menu 
WHERE perms LIKE '%contentTool%' OR perms LIKE '%generation%' 
   OR path LIKE '%content-tool%' OR path LIKE '%generation%'
ORDER BY menu_id;

-- 删除内容工具菜单（根据实际查询到的menu_id调整）
DELETE FROM sys_menu WHERE perms LIKE '%contentTool%';
DELETE FROM sys_menu WHERE path LIKE '%content-tool%';

-- 删除文章生成菜单
DELETE FROM sys_menu WHERE perms LIKE '%generation%' AND menu_name LIKE '%生成%';
DELETE FROM sys_menu WHERE path LIKE '%generation%' AND menu_name LIKE '%生成%';

-- 2. 删除角色菜单关联（如果菜单被删除，这些关联数据也应该清理）
DELETE FROM sys_role_menu WHERE menu_id IN (
    SELECT menu_id FROM sys_menu WHERE perms LIKE '%contentTool%'
);
DELETE FROM sys_role_menu WHERE menu_id IN (
    SELECT menu_id FROM sys_menu WHERE perms LIKE '%generation%'
);

-- 3. 删除内容工具分类（category_type = 'content_tool'）
DELETE FROM gb_categories WHERE category_type = 'content_tool';

-- 删除内容工具分类类型
DELETE FROM gb_category_types WHERE value = 'content_tool';

-- 4. 删除文章生成分类（category_type = 'generation'）
DELETE FROM gb_categories WHERE category_type = 'generation';

-- 删除文章生成分类类型（如果存在）
DELETE FROM gb_category_types WHERE value = 'generation';

-- 5. 删除网站与内容工具的关联关系
DELETE FROM gb_site_content_tool_relation WHERE entity_type = 'contentTool';

-- 6. 删除网站与文章生成的关联关系
DELETE FROM gb_site_generation_relations;

-- 7. 删除内容工具执行日志
DROP TABLE IF EXISTS gb_content_tool_logs;

-- 8. 删除内容工具表数据和表
TRUNCATE TABLE gb_content_tools;
DROP TABLE IF EXISTS gb_content_tools;

-- 9. 删除文章生成任务表数据和表
TRUNCATE TABLE gb_article_generation_tasks;
DROP TABLE IF EXISTS gb_article_generation_tasks;

-- 10. 清理可能存在的旧的生成记录表（如果有的话）
DROP TABLE IF EXISTS gb_article_generations;

-- 11. 删除相关的定时任务（如果有配置自动生成的定时任务）
DELETE FROM sys_job WHERE invoke_target LIKE '%Generation%';

-- =============================================
-- 验证清理结果
-- =============================================

-- 检查菜单是否已删除
SELECT '剩余的内容工具相关菜单:' AS check_type, COUNT(*) AS count
FROM sys_menu WHERE perms LIKE '%contentTool%' OR path LIKE '%content-tool%'
UNION ALL
SELECT '剩余的文章生成相关菜单:', COUNT(*)
FROM sys_menu WHERE perms LIKE '%generation%' AND menu_name LIKE '%生成%';

-- 检查分类是否已删除
SELECT '剩余的content_tool分类:' AS check_type, COUNT(*) AS count
FROM gb_categories WHERE category_type = 'content_tool'
UNION ALL
SELECT '剩余的generation分类:', COUNT(*)
FROM gb_categories WHERE category_type = 'generation';

-- 检查表是否已删除
SELECT 
    CASE 
        WHEN COUNT(*) > 0 THEN '表 gb_content_tools 仍然存在'
        ELSE '表 gb_content_tools 已删除'
    END AS check_result
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'gb_content_tools'
UNION ALL
SELECT 
    CASE 
        WHEN COUNT(*) > 0 THEN '表 gb_article_generation_tasks 仍然存在'
        ELSE '表 gb_article_generation_tasks 已删除'
    END
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'gb_article_generation_tasks';

-- =============================================
-- 执行说明
-- =============================================
-- 1. 执行前请先备份数据库
-- 2. 建议先执行查询语句，确认要删除的数据
-- 3. 逐步执行删除语句，验证每一步的结果
-- 4. 最后执行验证部分，确保清理完整
-- =============================================
