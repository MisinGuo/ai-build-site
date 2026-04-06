-- ============================================================
-- 脚本名称: gamebox_v2.88_workflow_and_atomic_tool_menu.sql
-- 描述: 添加工作流管理和原子工具菜单配置
-- 作者: System
-- 日期: 2026-01-04
-- 版本: v2.88
-- ============================================================

USE `ry-vue`;

-- ============================================================
-- 1. 添加原子工具主菜单（在AI配置下）
-- ============================================================
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2700, '原子工具', 2005, 4, 'atomic-tool', 'ai-config/atomic-tool/index', NULL, 1, 0, 'C', '0', '0', 'gamebox:atomicTool:list', 'component', 'admin', sysdate(), '', NULL, '原子工具管理菜单');

-- 原子工具按钮权限
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2701, '原子工具查询', 2700, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:atomicTool:query', '#', 'admin', sysdate(), '', NULL, '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2702, '原子工具新增', 2700, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:atomicTool:add', '#', 'admin', sysdate(), '', NULL, '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2703, '原子工具修改', 2700, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:atomicTool:edit', '#', 'admin', sysdate(), '', NULL, '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2704, '原子工具删除', 2700, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:atomicTool:remove', '#', 'admin', sysdate(), '', NULL, '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2705, '原子工具导出', 2700, 5, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:atomicTool:export', '#', 'admin', sysdate(), '', NULL, '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2706, '原子工具测试', 2700, 6, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:atomicTool:test', '#', 'admin', sysdate(), '', NULL, '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2707, '原子工具执行', 2700, 7, '', NULL, NULL, 1, 0, 'F', '0', '0', 'gamebox:atomicTool:execute', '#', 'admin', sysdate(), '', NULL, '');

-- ============================================================
-- 2. 添加工作流主菜单（在内容管理下）
-- ============================================================
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2800, '工作流管理', 2004, 5, 'workflow', 'content/workflow/index', NULL, 1, 0, 'C', '0', '0', 'content:workflow:list', 'tree-table', 'admin', sysdate(), '', NULL, '工作流管理菜单');

-- 工作流按钮权限
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2801, '工作流查询', 2800, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'content:workflow:query', '#', 'admin', sysdate(), '', NULL, '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2802, '工作流新增', 2800, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'content:workflow:add', '#', 'admin', sysdate(), '', NULL, '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2803, '工作流修改', 2800, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'content:workflow:edit', '#', 'admin', sysdate(), '', NULL, '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2804, '工作流删除', 2800, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'content:workflow:remove', '#', 'admin', sysdate(), '', NULL, '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2805, '工作流导出', 2800, 5, '', NULL, NULL, 1, 0, 'F', '0', '0', 'content:workflow:export', '#', 'admin', sysdate(), '', NULL, '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2806, '工作流执行', 2800, 6, '', NULL, NULL, 1, 0, 'F', '0', '0', 'content:workflow:execute', '#', 'admin', sysdate(), '', NULL, '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2807, '工作流测试', 2800, 7, '', NULL, NULL, 1, 0, 'F', '0', '0', 'content:workflow:test', '#', 'admin', sysdate(), '', NULL, '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2808, '工作流验证', 2800, 8, '', NULL, NULL, 1, 0, 'F', '0', '0', 'content:workflow:validate', '#', 'admin', sysdate(), '', NULL, '');

-- ============================================================
-- 3. 给管理员角色分配菜单权限
-- ============================================================
-- 原子工具菜单权限
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, 2700 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2700);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, 2701 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2701);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, 2702 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2702);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, 2703 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2703);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, 2704 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2704);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, 2705 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2705);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, 2706 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2706);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, 2707 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2707);

-- 工作流菜单权限
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, 2800 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2800);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, 2801 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2801);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, 2802 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2802);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, 2803 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2803);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, 2804 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2804);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, 2805 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2805);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, 2806 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2806);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, 2807 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2807);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, 2808 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2808);

-- ============================================================
-- 4. 创建数据表
-- ============================================================

-- 原子工具表
CREATE TABLE IF NOT EXISTS `gb_atomic_tool` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '工具ID',
  `tool_code` varchar(100) NOT NULL COMMENT '工具编码（唯一标识）',
  `tool_name` varchar(200) NOT NULL COMMENT '工具名称',
  `tool_type` varchar(50) NOT NULL COMMENT '工具类型：ai-AI生成工具, api-外部API工具, builtin-内置工具',
  `description` text COMMENT '工具描述',
  `config` text COMMENT '工具配置（JSON格式）',
  `inputs` text COMMENT '输入参数定义（JSON数组）',
  `outputs` text COMMENT '输出参数定义（JSON数组）',
  `enabled` tinyint(1) DEFAULT 1 COMMENT '是否启用：0-禁用 1-启用',
  `site_id` bigint DEFAULT NULL COMMENT '站点ID（多站点支持）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tool_code` (`tool_code`),
  KEY `idx_tool_type` (`tool_type`),
  KEY `idx_enabled` (`enabled`),
  KEY `idx_site_id` (`site_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='原子工具表';

-- 工作流表
CREATE TABLE IF NOT EXISTS `gb_workflow` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '工作流ID',
  `workflow_code` varchar(100) NOT NULL COMMENT '工作流编码（唯一标识）',
  `workflow_name` varchar(200) NOT NULL COMMENT '工作流名称',
  `description` text COMMENT '工作流描述',
  `trigger_type` varchar(50) NOT NULL DEFAULT 'manual' COMMENT '触发类型：manual-手动触发, scheduled-定时触发, event-事件触发',
  `definition` text NOT NULL COMMENT '工作流定义（JSON格式，包含inputs和steps）',
  `step_count` int DEFAULT 0 COMMENT '步骤数量',
  `enabled` tinyint(1) DEFAULT 1 COMMENT '是否启用：0-禁用 1-启用',
  `site_id` bigint DEFAULT NULL COMMENT '站点ID（多站点支持）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_workflow_code` (`workflow_code`),
  KEY `idx_trigger_type` (`trigger_type`),
  KEY `idx_enabled` (`enabled`),
  KEY `idx_site_id` (`site_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工作流表';

-- 工作流执行记录表
CREATE TABLE IF NOT EXISTS `gb_workflow_execution` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '执行记录ID',
  `execution_id` varchar(100) NOT NULL COMMENT '执行ID（唯一标识）',
  `workflow_code` varchar(100) NOT NULL COMMENT '工作流编码',
  `mode` varchar(50) NOT NULL COMMENT '执行模式：sync-同步, async-异步, batch-批量',
  `status` varchar(50) NOT NULL COMMENT '执行状态：pending-等待, running-运行中, success-成功, failed-失败, cancelled-已取消',
  `input_data` text COMMENT '输入数据（JSON格式）',
  `output_data` text COMMENT '输出数据（JSON格式）',
  `error` text COMMENT '错误信息',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `duration` bigint DEFAULT NULL COMMENT '执行耗时（毫秒）',
  `site_id` bigint DEFAULT NULL COMMENT '站点ID',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_execution_id` (`execution_id`),
  KEY `idx_workflow_code` (`workflow_code`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_site_id` (`site_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工作流执行记录表';

-- 工作流步骤执行记录表
CREATE TABLE IF NOT EXISTS `gb_workflow_step_execution` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '步骤执行记录ID',
  `execution_id` varchar(100) NOT NULL COMMENT '工作流执行ID',
  `step_id` varchar(100) NOT NULL COMMENT '步骤ID',
  `step_name` varchar(200) DEFAULT NULL COMMENT '步骤名称',
  `step_type` varchar(50) NOT NULL COMMENT '步骤类型',
  `tool_code` varchar(100) DEFAULT NULL COMMENT '工具编码',
  `status` varchar(50) NOT NULL COMMENT '执行状态',
  `input` text COMMENT '步骤输入（JSON格式）',
  `output` text COMMENT '步骤输出（JSON格式）',
  `error` text COMMENT '错误信息',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `duration` bigint DEFAULT NULL COMMENT '执行耗时（毫秒）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_execution_id` (`execution_id`),
  KEY `idx_step_id` (`step_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工作流步骤执行记录表';

-- ============================================================
-- 执行完成提示
-- ============================================================
SELECT '工作流和原子工具菜单配置完成！' AS message;
SELECT '已添加原子工具菜单到AI配置目录' AS notice1;
SELECT '已添加工作流管理菜单到内容管理目录' AS notice2;
SELECT '已创建相关数据表' AS notice3;
SELECT '请刷新浏览器查看新菜单' AS action;
