-- =====================================================
-- 权限修复补丁：为 box-field-mapping 添加菜单权限条目
-- 并修正相关权限字符串对齐问题
-- 适用版本：gamebox v3.62+
-- =====================================================

-- ----------------------------
-- 在"游戏列表"菜单(2200)下新增字段映射功能权限按钮
-- 这些权限被 GbBoxFieldMappingController 使用
-- 用于游戏页面加载时调用 /gamebox/box-field-mapping/tableFields 等接口
-- ----------------------------
INSERT IGNORE INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES
  (2206, '字段映射查询', 2200, 6, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:field-mapping:query', '#', 'admin', NOW(), '', NULL, '字段映射查询权限（tableFields/listByBoxId）'),
  (2207, '字段映射修改', 2200, 7, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:field-mapping:edit',  '#', 'admin', NOW(), '', NULL, '字段映射保存/修改权限'),
  (2208, '字段映射新增', 2200, 8, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:field-mapping:add',   '#', 'admin', NOW(), '', NULL, '字段映射新增权限'),
  (2209, '字段映射删除', 2200, 9, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:field-mapping:remove','#', 'admin', NOW(), '', NULL, '字段映射删除权限'),
  (2219, '字段映射列表', 2200, 10,'', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:field-mapping:list',  '#', 'admin', NOW(), '', NULL, '字段映射列表查询权限'),
  (2220, '字段映射导入', 2200, 11,'', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:field-mapping:import','#', 'admin', NOW(), '', NULL, '字段映射导入权限');

-- ----------------------------
-- 同样在"游戏盒子"菜单(2210)下也添加字段映射查询/修改权限
-- 因为盒子页面的字段映射配置对话框也会调用 tableFields
-- ----------------------------
INSERT IGNORE INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES
  (2216, '字段映射查询(盒子)', 2210, 6, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:field-mapping:query', '#', 'admin', NOW(), '', NULL, '盒子页面字段映射查询权限'),
  (2217, '字段映射修改(盒子)', 2210, 7, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:field-mapping:edit',  '#', 'admin', NOW(), '', NULL, '盒子页面字段映射保存权限');

-- ----------------------------
-- 为超级管理员角色(role_id=1)同步新增菜单权限
-- ----------------------------
INSERT IGNORE INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
  (1, 2206), (1, 2207), (1, 2208), (1, 2209), (1, 2216), (1, 2217), (1, 2219), (1, 2220);

-- ----------------------------
-- 验证：查看已插入的字段映射菜单条目
-- ----------------------------
SELECT menu_id, menu_name, parent_id, perms FROM sys_menu
WHERE perms LIKE 'gamebox:field-mapping:%'
ORDER BY menu_id;
