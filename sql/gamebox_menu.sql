-- ----------------------------
-- 游戏盒子内容管理系统菜单权限初始化 SQL
-- ----------------------------

-- 一级菜单：游戏盒子
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2000, '游戏盒子', 0, 5, 'gamebox', NULL, NULL, 1, 0, 'M', '0', '0', '', 'game', 'admin', sysdate(), '', NULL, '游戏盒子内容管理系统目录');

-- 二级菜单：基础配置
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2001, '基础配置', 2000, 1, 'config', NULL, NULL, 1, 0, 'M', '0', '0', '', 'system', 'admin', sysdate(), '', NULL, '基础配置目录');

-- 二级菜单：游戏管理
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2002, '游戏管理', 2000, 2, 'game-mgmt', NULL, NULL, 1, 0, 'M', '0', '0', '', 'clipboard', 'admin', sysdate(), '', NULL, '游戏管理目录');

-- 二级菜单：短剧管理
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2003, '短剧管理', 2000, 3, 'drama-mgmt', NULL, NULL, 1, 0, 'M', '0', '0', '', 'video', 'admin', sysdate(), '', NULL, '短剧管理目录');

-- 二级菜单：内容管理
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2004, '内容管理', 2000, 4, 'content', NULL, NULL, 1, 0, 'M', '0', '0', '', 'documentation', 'admin', sysdate(), '', NULL, '内容管理目录');

-- 二级菜单：AI配置
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2005, 'AI配置', 2000, 5, 'ai-config', NULL, NULL, 1, 0, 'M', '0', '0', '', 'robot', 'admin', sysdate(), '', NULL, 'AI配置目录');

-- =====================================================
-- 三级菜单：基础配置下的子菜单
-- =====================================================

-- 站点管理
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2100, '站点管理', 2001, 1, 'site', 'gamebox/config/site/index', NULL, 1, 0, 'C', '0', '0', 'gamebox:site:list', 'server', 'admin', sysdate(), '', NULL, '站点管理菜单');

-- 站点管理按钮权限
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2101, '站点查询', 2100, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:site:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2102, '站点新增', 2100, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:site:add', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2103, '站点修改', 2100, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:site:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2104, '站点删除', 2100, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:site:remove', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2105, '站点导出', 2100, 5, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:site:export', '#', 'admin', sysdate(), '', NULL, '');

-- 分类管理
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2110, '分类管理', 2001, 2, 'category', 'gamebox/config/category/index', NULL, 1, 0, 'C', '0', '0', 'gamebox:category:list', 'tree', 'admin', sysdate(), '', NULL, '分类管理菜单');

-- 分类管理按钮权限
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2111, '分类查询', 2110, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:category:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2112, '分类新增', 2110, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:category:add', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2113, '分类修改', 2110, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:category:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2114, '分类删除', 2110, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:category:remove', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2115, '分类导出', 2110, 5, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:category:export', '#', 'admin', sysdate(), '', NULL, '');

-- 存储配置
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2120, '存储配置', 2001, 3, 'storage', 'gamebox/config/storage/index', NULL, 1, 0, 'C', '0', '0', 'gamebox:storage:list', 'upload', 'admin', sysdate(), '', NULL, '存储配置菜单');

-- 存储配置按钮权限
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2121, '存储配置查询', 2120, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:storage:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2122, '存储配置新增', 2120, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:storage:add', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2123, '存储配置修改', 2120, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:storage:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2124, '存储配置删除', 2120, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:storage:remove', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2125, '存储配置导出', 2120, 5, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:storage:export', '#', 'admin', sysdate(), '', NULL, '');

-- =====================================================
-- 三级菜单：游戏管理下的子菜单
-- =====================================================

-- 游戏列表
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2200, '游戏列表', 2002, 1, 'game', 'gamebox/game-mgmt/game/index', NULL, 1, 0, 'C', '0', '0', 'gamebox:game:list', 'star', 'admin', sysdate(), '', NULL, '游戏列表菜单');

-- 游戏列表按钮权限
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2201, '游戏查询', 2200, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:game:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2202, '游戏新增', 2200, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:game:add', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2203, '游戏修改', 2200, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:game:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2204, '游戏删除', 2200, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:game:remove', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2205, '游戏导出', 2200, 5, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:game:export', '#', 'admin', sysdate(), '', NULL, '');

-- 游戏盒子
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2210, '游戏盒子', 2002, 2, 'box', 'gamebox/game-mgmt/box/index', NULL, 1, 0, 'C', '0', '0', 'gamebox:box:list', 'component', 'admin', sysdate(), '', NULL, '游戏盒子菜单');

-- 游戏盒子按钮权限
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2211, '游戏盒子查询', 2210, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:box:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2212, '游戏盒子新增', 2210, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:box:add', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2213, '游戏盒子修改', 2210, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:box:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2214, '游戏盒子删除', 2210, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:box:remove', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2215, '游戏盒子导出', 2210, 5, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:box:export', '#', 'admin', sysdate(), '', NULL, '');

-- =====================================================
-- 三级菜单：短剧管理下的子菜单
-- =====================================================

-- 短剧厂商
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2300, '短剧厂商', 2003, 1, 'vendor', 'gamebox/drama-mgmt/vendor/index', NULL, 1, 0, 'C', '0', '0', 'gamebox:vendor:list', 'peoples', 'admin', sysdate(), '', NULL, '短剧厂商菜单');

-- 短剧厂商按钮权限
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2301, '短剧厂商查询', 2300, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:vendor:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2302, '短剧厂商新增', 2300, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:vendor:add', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2303, '短剧厂商修改', 2300, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:vendor:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2304, '短剧厂商删除', 2300, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:vendor:remove', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2305, '短剧厂商导出', 2300, 5, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:vendor:export', '#', 'admin', sysdate(), '', NULL, '');

-- 短剧列表
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2310, '短剧列表', 2003, 2, 'drama', 'gamebox/drama-mgmt/drama/index', NULL, 1, 0, 'C', '0', '0', 'gamebox:drama:list', 'video', 'admin', sysdate(), '', NULL, '短剧列表菜单');

-- 短剧列表按钮权限
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2311, '短剧查询', 2310, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:drama:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2312, '短剧新增', 2310, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:drama:add', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2313, '短剧修改', 2310, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:drama:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2314, '短剧删除', 2310, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:drama:remove', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2315, '短剧导出', 2310, 5, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:drama:export', '#', 'admin', sysdate(), '', NULL, '');

-- =====================================================
-- 三级菜单：内容管理下的子菜单
-- =====================================================

-- 文章管理
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2400, '文章管理', 2004, 1, 'article', 'gamebox/content/article/index', NULL, 1, 0, 'C', '0', '0', 'gamebox:article:list', 'documentation', 'admin', sysdate(), '', NULL, '文章管理菜单');

-- 文章管理按钮权限
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2401, '文章查询', 2400, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:article:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2402, '文章新增', 2400, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:article:add', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2403, '文章修改', 2400, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:article:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2404, '文章删除', 2400, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:article:remove', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2405, '文章导出', 2400, 5, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:article:export', '#', 'admin', sysdate(), '', NULL, '');

-- AI文章生成
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2410, 'AI文章生成', 2004, 2, 'generation', 'gamebox/content/generation/index', NULL, 1, 0, 'C', '0', '0', 'gamebox:generation:list', 'skill', 'admin', sysdate(), '', NULL, 'AI文章生成菜单');

-- AI文章生成按钮权限
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2411, 'AI文章生成查询', 2410, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:generation:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2412, 'AI文章生成新增', 2410, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:generation:add', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2413, 'AI文章生成修改', 2410, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:generation:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2414, 'AI文章生成删除', 2410, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:generation:remove', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2415, 'AI文章生成导出', 2410, 5, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:generation:export', '#', 'admin', sysdate(), '', NULL, '');

-- =====================================================
-- 三级菜单：AI配置下的子菜单
-- =====================================================

-- AI平台配置
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2500, 'AI平台配置', 2005, 1, 'platform', 'gamebox/ai-config/platform/index', NULL, 1, 0, 'C', '0', '0', 'gamebox:platform:list', 'online', 'admin', sysdate(), '', NULL, 'AI平台配置菜单');

-- AI平台配置按钮权限
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2501, 'AI平台配置查询', 2500, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:platform:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2502, 'AI平台配置新增', 2500, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:platform:add', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2503, 'AI平台配置修改', 2500, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:platform:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2504, 'AI平台配置删除', 2500, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:platform:remove', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2505, 'AI平台配置导出', 2500, 5, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:platform:export', '#', 'admin', sysdate(), '', NULL, '');

-- 提示词模板
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2510, '提示词模板', 2005, 2, 'prompt', 'gamebox/ai-config/prompt/index', NULL, 1, 0, 'C', '0', '0', 'gamebox:prompt:list', 'message', 'admin', sysdate(), '', NULL, '提示词模板菜单');

-- 提示词模板按钮权限
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2511, '提示词模板查询', 2510, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:prompt:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2512, '提示词模板新增', 2510, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:prompt:add', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2513, '提示词模板修改', 2510, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:prompt:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2514, '提示词模板删除', 2510, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:prompt:remove', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2515, '提示词模板导出', 2510, 5, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:prompt:export', '#', 'admin', sysdate(), '', NULL, '');
