-- 删除旧的字段映射菜单（已集成到盒子管理页面）
-- 执行日期: 2026-01-27

-- 查找字段映射菜单的ID（权限标识包含 gamebox:fieldMapping）
-- 删除该菜单及其所有子菜单（按钮）
DELETE FROM sys_menu WHERE perms LIKE 'gamebox:fieldMapping%';

-- 或者如果需要更精确的删除（根据菜单名称）
-- DELETE FROM sys_menu WHERE menu_name = '字段映射' AND perms LIKE 'gamebox:fieldMapping%';
