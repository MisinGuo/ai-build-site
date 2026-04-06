-- ----------------------------
-- 内容工具菜单配置
-- 版本: v2.84
-- 日期: 2026-01-02
-- 说明: 添加内容工具管理菜单，替代旧的提示词模板菜单
-- ----------------------------

-- 1. 隐藏旧的提示词模板菜单（保留数据，仅隐藏显示）
UPDATE sys_menu SET visible = '1', remark = '已废弃，请使用内容工具菜单' 
WHERE menu_id = 2510 AND menu_name = '提示词模板';

-- 2. 添加内容工具主菜单
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2600, '内容工具', 2005, 3, 'content-tool', 'gamebox/ai-config/content-tool/index', NULL, 1, 0, 'C', '0', '0', 'gamebox:contentTool:list', 'tool', 'admin', sysdate(), '', NULL, '内容工具管理菜单');

-- 3. 添加内容工具按钮权限
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2601, '内容工具查询', 2600, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:contentTool:query', '#', 'admin', sysdate(), '', NULL, '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2602, '内容工具新增', 2600, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:contentTool:add', '#', 'admin', sysdate(), '', NULL, '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2603, '内容工具修改', 2600, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:contentTool:edit', '#', 'admin', sysdate(), '', NULL, '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2604, '内容工具删除', 2600, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:contentTool:remove', '#', 'admin', sysdate(), '', NULL, '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2605, '内容工具导出', 2600, 5, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:contentTool:export', '#', 'admin', sysdate(), '', NULL, '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2606, '内容工具测试', 2600, 6, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:contentTool:test', '#', 'admin', sysdate(), '', NULL, '');

-- 4. 给管理员角色分配新菜单权限
-- 注意：如果角色ID不是1，请根据实际情况修改
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, 2600 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2600);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, 2601 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2601);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, 2602 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2602);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, 2603 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2603);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, 2604 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2604);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, 2605 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2605);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, 2606 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2606);

-- 执行完成提示
SELECT '内容工具菜单配置完成！' AS message;
SELECT '提示词模板菜单已隐藏（数据保留）' AS notice;
SELECT '请刷新浏览器查看新菜单' AS action;
