-- ==============================
-- 内容工具分类类型
-- ==============================

-- 添加内容工具分类类型
INSERT INTO `gb_category_types` (`value`, `label`, `tag_type`, `description`, `sort_order`, `is_system`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) 
VALUES ('content_tool', '内容工具分类', 'primary', '用于内容工具管理页面的分类', 9, '1', '0', '', NOW(), '', NULL, '用于AI配置中的内容工具分类管理')
ON DUPLICATE KEY UPDATE 
  `label` = VALUES(`label`),
  `tag_type` = VALUES(`tag_type`),
  `description` = VALUES(`description`),
  `sort_order` = VALUES(`sort_order`),
  `is_system` = VALUES(`is_system`),
  `status` = VALUES(`status`),
  `update_time` = NOW();

-- 注意：默认分类已在 gamebox_v2.83_content_tools.sql 中创建
-- 包括：AI生成工具、翻译工具、图片工具、优化工具
