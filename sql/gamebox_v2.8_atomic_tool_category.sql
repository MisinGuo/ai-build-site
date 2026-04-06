-- =============================================
-- 原子工具分类支持和多模式查询功能
-- Version: 2.8
-- Date: 2026-01-05
-- =============================================

-- 1. 为原子工具表添加分类字段
ALTER TABLE `gb_atomic_tool` ADD COLUMN `category_id` BIGINT(20) NULL COMMENT '分类ID' AFTER `site_id`;
ALTER TABLE `gb_atomic_tool` ADD INDEX `idx_category_id` (`category_id`);

-- 2. 确保原子工具支持网站关联（如果表不存在则创建）
CREATE TABLE IF NOT EXISTS `gb_site_atomic_tool_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `site_id` bigint(20) NOT NULL COMMENT '网站ID',
  `tool_id` bigint(20) NOT NULL COMMENT '原子工具ID',
  `relation_type` varchar(20) NOT NULL DEFAULT 'shared' COMMENT '关联类型：shared-跨站共享, exclude-排除',
  `is_visible` char(1) DEFAULT '1' COMMENT '是否可见：0-隐藏 1-显示',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_site_tool` (`site_id`,`tool_id`),
  KEY `idx_site_id` (`site_id`),
  KEY `idx_tool_id` (`tool_id`),
  KEY `idx_relation_type` (`relation_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='网站-原子工具关联表';

-- 3. 更新分类类型枚举（如果需要）
-- 注意：根据你的实际表结构调整
UPDATE `gb_categories` SET `category_type` = 'atomic_tool' 
WHERE `category_type` = 'atomic_tool' 
LIMIT 0; -- 这只是一个示例，不会实际执行

-- 4. 创建一些默认的原子工具分类(可选)
INSERT INTO `gb_categories` (`name`, `slug`, `icon`, `category_type`, `site_id`, `sort_order`, `status`, `description`, `create_time`) 
VALUES 
  ('AI工具', 'ai_tools', '🤖', 'atomic_tool', 0, 1, '1', 'AI驱动的原子工具', NOW()),
  ('API工具', 'api_tools', '🔌', 'atomic_tool', 0, 2, '1', '调用外部API的工具', NOW()),
  ('内置工具', 'builtin_tools', '⚙️', 'atomic_tool', 0, 3, '1', '系统内置功能工具', NOW()),
  ('文本处理', 'text_processing', '📝', 'atomic_tool', 0, 4, '1', '文本处理相关工具', NOW()),
  ('图片处理', 'image_processing', '🖼️', 'atomic_tool', 0, 5, '1', '图片处理相关工具', NOW()),
  ('SEO优化', 'seo_optimization', '🔍', 'atomic_tool', 0, 6, '1', 'SEO优化相关工具', NOW())
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- 完成提示
SELECT '原子工具分类支持和多模式查询功能已成功添加！' as message;
