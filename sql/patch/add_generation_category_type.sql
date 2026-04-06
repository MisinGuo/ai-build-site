-- ----------------------------
-- 补丁SQL：添加文章生成分类类型
-- 基于版本：v2.77
-- 创建日期：2026-01-01
-- 说明：为文章生成任务管理页面添加分类类型定义
-- ----------------------------

-- 添加文章生成分类类型（如果不存在）
INSERT INTO `gb_category_types` (`id`, `value`, `label`, `tag_type`, `description`, `sort_order`, `status`, `is_system`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 12, 'generation', '文章生成分类', 'success', '用于文章生成任务管理页面的分类', 11, '0', '1', '', NOW(), '', NULL, NULL
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `gb_category_types` WHERE `value` = 'generation'
);
