-- ===================================================
-- 添加工作流分类 - 2026-01-06
-- 说明：为工作流管理添加分类支持
-- ===================================================

-- 1. 首先在 gb_category_types 表中注册 workflow 分类类型
INSERT INTO `gb_category_types` (`value`, `label`, `tag_type`, `description`, `sort_order`, `status`, `is_system`, `create_by`, `create_time`)
SELECT 'workflow', '工作流分类', 'primary', '用于工作流管理页面的分类', 9, '0', '1', 'admin', NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `gb_category_types` WHERE `value` = 'workflow');

-- 2. 添加工作流分类数据

-- 检查并插入：内容生成
INSERT INTO `gb_categories` (`site_id`, `parent_id`, `category_type`, `name`, `slug`, `icon`, `description`, `sort_order`, `status`, `del_flag`, `create_by`, `create_time`)
SELECT 0, 0, 'workflow', '内容生成', 'content-generation', '✍️', '用于生成各类内容的工作流', 1, '1', '0', 'admin', NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `gb_categories` WHERE `slug` = 'content-generation' AND `category_type` = 'workflow' AND `site_id` = 0);

-- 检查并插入：数据处理
INSERT INTO `gb_categories` (`site_id`, `parent_id`, `category_type`, `name`, `slug`, `icon`, `description`, `sort_order`, `status`, `del_flag`, `create_by`, `create_time`)
SELECT 0, 0, 'workflow', '数据处理', 'data-processing', '📊', '数据清洗、转换、分析等工作流', 2, '1', '0', 'admin', NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `gb_categories` WHERE `slug` = 'data-processing' AND `category_type` = 'workflow' AND `site_id` = 0);

-- 检查并插入：批量操作
INSERT INTO `gb_categories` (`site_id`, `parent_id`, `category_type`, `name`, `slug`, `icon`, `description`, `sort_order`, `status`, `del_flag`, `create_by`, `create_time`)
SELECT 0, 0, 'workflow', '批量操作', 'batch-operation', '🔄', '批量处理任务的工作流', 3, '1', '0', 'admin', NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `gb_categories` WHERE `slug` = 'batch-operation' AND `category_type` = 'workflow' AND `site_id` = 0);

-- 检查并插入：自动化任务
INSERT INTO `gb_categories` (`site_id`, `parent_id`, `category_type`, `name`, `slug`, `icon`, `description`, `sort_order`, `status`, `del_flag`, `create_by`, `create_time`)
SELECT 0, 0, 'workflow', '自动化任务', 'automation', '⚙️', '定时或自动触发的任务工作流', 4, '1', '0', 'admin', NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `gb_categories` WHERE `slug` = 'automation' AND `category_type` = 'workflow' AND `site_id` = 0);

-- 检查并插入：AI辅助
INSERT INTO `gb_categories` (`site_id`, `parent_id`, `category_type`, `name`, `slug`, `icon`, `description`, `sort_order`, `status`, `del_flag`, `create_by`, `create_time`)
SELECT 0, 0, 'workflow', 'AI辅助', 'ai-assisted', '🤖', '使用AI能力的工作流', 5, '1', '0', 'admin', NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `gb_categories` WHERE `slug` = 'ai-assisted' AND `category_type` = 'workflow' AND `site_id` = 0);

-- 检查并插入：数据同步
INSERT INTO `gb_categories` (`site_id`, `parent_id`, `category_type`, `name`, `slug`, `icon`, `description`, `sort_order`, `status`, `del_flag`, `create_by`, `create_time`)
SELECT 0, 0, 'workflow', '数据同步', 'data-sync', '🔗', '跨系统数据同步工作流', 6, '1', '0', 'admin', NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `gb_categories` WHERE `slug` = 'data-sync' AND `category_type` = 'workflow' AND `site_id` = 0);

-- 检查并插入：质量检查
INSERT INTO `gb_categories` (`site_id`, `parent_id`, `category_type`, `name`, `slug`, `icon`, `description`, `sort_order`, `status`, `del_flag`, `create_by`, `create_time`)
SELECT 0, 0, 'workflow', '质量检查', 'quality-check', '✅', '内容质量检查和审核工作流', 7, '1', '0', 'admin', NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `gb_categories` WHERE `slug` = 'quality-check' AND `category_type` = 'workflow' AND `site_id` = 0);

-- 检查并插入：通知提醒
INSERT INTO `gb_categories` (`site_id`, `parent_id`, `category_type`, `name`, `slug`, `icon`, `description`, `sort_order`, `status`, `del_flag`, `create_by`, `create_time`)
SELECT 0, 0, 'workflow', '通知提醒', 'notification', '🔔', '消息通知和提醒工作流', 8, '1', '0', 'admin', NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `gb_categories` WHERE `slug` = 'notification' AND `category_type` = 'workflow' AND `site_id` = 0);

-- 检查并插入：自定义
INSERT INTO `gb_categories` (`site_id`, `parent_id`, `category_type`, `name`, `slug`, `icon`, `description`, `sort_order`, `status`, `del_flag`, `create_by`, `create_time`)
SELECT 0, 0, 'workflow', '自定义', 'custom', '🔧', '自定义工作流', 99, '1', '0', 'admin', NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `gb_categories` WHERE `slug` = 'custom' AND `category_type` = 'workflow' AND `site_id` = 0);
