-- ----------------------------
-- 代码管理菜单 SQL
-- 游戏盒子 > 基础配置 > 代码管理
-- ----------------------------

-- 代码管理页面菜单（在基础配置下，排序第4）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2130, '代码管理', 2001, 4, 'code-manage', 'gamebox/config/code-manage/index', NULL, 1, 0, 'C', '0', '0', 'gamebox:codeManage:list', 'code', 'admin', sysdate(), '', NULL, '代码管理菜单-Git仓库配置、本地构建和预览');

-- 代码管理按钮权限
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2131, '代码管理查询', 2130, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:codeManage:query', '#', 'admin', sysdate(), '', NULL, '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2132, '代码管理编辑', 2130, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:codeManage:edit', '#', 'admin', sysdate(), '', NULL, '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2133, '代码管理运维', 2130, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:codeManage:ops', '#', 'admin', sysdate(), '', NULL, '');
