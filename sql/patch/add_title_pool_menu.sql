-- ============================================================
-- 标题池管理菜单SQL
-- 说明：执行此脚本后，在系统管理->菜单管理中刷新即可看到新菜单
-- ============================================================

-- 查询内容管理菜单ID（假设已存在）
SET @contentMenuId = (SELECT menu_id FROM sys_menu WHERE menu_name = '内容管理' AND parent_id = 0 LIMIT 1);

-- 如果内容管理菜单不存在，先创建
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT '内容管理', 0, 4, 'content', NULL, 1, 0, 'M', '0', '0', '', 'edit', 'admin', NOW(), '', NULL, '游戏盒子内容管理目录'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_name = '内容管理' AND parent_id = 0);

-- 重新获取内容管理菜单ID
SET @contentMenuId = (SELECT menu_id FROM sys_menu WHERE menu_name = '内容管理' AND parent_id = 0 LIMIT 1);

-- 插入标题池管理菜单（主菜单）
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('标题池管理', @contentMenuId, 3, 'titlePool', 'gamebox/content/titlePool/index', 1, 0, 'C', '0', '0', 'gamebox:titlePool:list', 'list', 'admin', NOW(), '', NULL, '文章标题池管理菜单');

-- 获取刚插入的标题池管理菜单ID
SET @titlePoolMenuId = LAST_INSERT_ID();

-- 插入标题池管理按钮权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES 
('标题查询', @titlePoolMenuId, 1, '#', '', 1, 0, 'F', '0', '0', 'gamebox:titlePool:query', '#', 'admin', NOW(), '', NULL, ''),
('标题新增', @titlePoolMenuId, 2, '#', '', 1, 0, 'F', '0', '0', 'gamebox:titlePool:add', '#', 'admin', NOW(), '', NULL, ''),
('标题修改', @titlePoolMenuId, 3, '#', '', 1, 0, 'F', '0', '0', 'gamebox:titlePool:edit', '#', 'admin', NOW(), '', NULL, ''),
('标题删除', @titlePoolMenuId, 4, '#', '', 1, 0, 'F', '0', '0', 'gamebox:titlePool:remove', '#', 'admin', NOW(), '', NULL, ''),
('标题导入', @titlePoolMenuId, 5, '#', '', 1, 0, 'F', '0', '0', 'gamebox:titlePool:import', '#', 'admin', NOW(), '', NULL, ''),
('导入历史', @titlePoolMenuId, 6, '#', '', 1, 0, 'F', '0', '0', 'gamebox:titlePool:history', '#', 'admin', NOW(), '', NULL, '');

-- 查看插入结果
SELECT menu_id, menu_name, parent_id, path, perms, menu_type 
FROM sys_menu 
WHERE menu_name LIKE '%标题%' OR menu_id = @titlePoolMenuId OR menu_id IN (SELECT menu_id FROM sys_menu WHERE parent_id = @titlePoolMenuId);

SELECT '菜单添加完成！请在系统管理->菜单管理中刷新，或重新登录查看新菜单。' AS message;
