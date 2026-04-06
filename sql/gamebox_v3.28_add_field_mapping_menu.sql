-- ================================
-- 游戏盒子系统 v3.28 补丁
-- 功能: 添加字段映射配置管理菜单
-- 日期: 2026-01-25
-- ================================

-- 添加字段映射配置菜单（挂在基础配置目录下）
INSERT INTO `sys_menu` VALUES (2130, '字段映射配置', 2001, 4, 'field-mapping', 'gamebox/config/field-mapping/index', NULL, '', 1, 0, 'C', '0', '0', 'gamebox:fieldMapping:list', 'build', 'admin', NOW(), '', NULL, '字段映射配置菜单');

-- 添加字段映射配置的功能按钮
INSERT INTO `sys_menu` VALUES (2131, '字段映射查询', 2130, 1, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:fieldMapping:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (2132, '字段映射新增', 2130, 2, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:fieldMapping:add', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (2133, '字段映射修改', 2130, 3, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:fieldMapping:edit', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (2134, '字段映射删除', 2130, 4, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:fieldMapping:remove', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (2135, '字段映射导出', 2130, 5, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:fieldMapping:export', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (2136, '字段映射导入', 2130, 6, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:fieldMapping:import', '#', 'admin', NOW(), '', NULL, '');

-- ================================
-- 说明:
-- menu_id: 2130-2136 (字段映射配置相关)
-- parent_id: 2001 (基础配置目录)
-- order_num: 4 (排在分类管理、站点管理、存储配置之后)
-- menu_type: C-菜单 F-按钮
-- path: gamebox/config/field-mapping/index
-- perms: gamebox:fieldMapping:* (权限标识)
-- icon: build (表单构建图标，符合配置管理的语义)
-- ================================
