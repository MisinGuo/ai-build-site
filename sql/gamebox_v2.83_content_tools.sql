-- ==========================================
-- 游戏盒子 v2.83 - 内容工具系统
-- 功能：将AI提示词模板改造为通用内容工具系统
-- 支持：AI生成、翻译、图片搜索等多种工具
-- 日期：2026-01-02
-- ==========================================

-- ==========================================
-- 1. 创建内容工具表
-- ==========================================
CREATE TABLE IF NOT EXISTS `gb_content_tools` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '工具ID',
  `tool_type` varchar(50) NOT NULL COMMENT '工具类型：ai_generator-AI生成, translator-翻译, image_search-图片搜索, seo_optimizer-SEO优化',
  `tool_name` varchar(100) NOT NULL COMMENT '工具名称',
  `slug` varchar(100) DEFAULT NULL COMMENT '工具标识（URL友好）',
  `tool_description` varchar(500) DEFAULT NULL COMMENT '工具描述',
  `site_id` bigint(20) DEFAULT 0 COMMENT '所属网站ID，0表示全局',
  `category_id` bigint(20) DEFAULT NULL COMMENT '分类ID',
  `icon` varchar(100) DEFAULT NULL COMMENT '工具图标',
  
  -- AI生成工具专用字段
  `template_code` varchar(100) DEFAULT NULL COMMENT '模板代码',
  `template_type` varchar(50) DEFAULT NULL COMMENT '模板类型',
  `system_prompt` text COMMENT '系统提示词',
  `user_prompt_template` text COMMENT '用户提示词模板',
  `output_format` varchar(50) DEFAULT NULL COMMENT '输出格式',
  `example_output` text COMMENT '示例输出',
  `variables` text COMMENT '可用变量JSON',
  `default_values` text COMMENT '默认值JSON',
  `ai_platform_id` bigint(20) DEFAULT NULL COMMENT '关联的AI平台ID',
  `model_name` varchar(100) DEFAULT NULL COMMENT '模型名称',
  `temperature` decimal(3,2) DEFAULT 0.70 COMMENT '温度参数',
  `max_tokens` int(11) DEFAULT 2000 COMMENT '最大Token数',
  `is_public` char(1) DEFAULT '0' COMMENT '是否公开：0-私有 1-公开',
  
  -- 通用配置字段
  `config` text COMMENT '扩展配置JSON（翻译API配置、图片搜索配置等）',
  `enabled` char(1) DEFAULT '1' COMMENT '是否启用：0-禁用 1-启用',
  `sort_order` int(11) DEFAULT 0 COMMENT '排序',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_tool_type` (`tool_type`),
  KEY `idx_site_id` (`site_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_slug` (`slug`),
  KEY `idx_template_code` (`template_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容工具表';

-- ==========================================
-- 2. 迁移现有AI提示词模板数据到工具表
-- ==========================================
-- 检查并添加icon字段（如果不存在）
SET @column_exists = (
  SELECT COUNT(*) 
  FROM INFORMATION_SCHEMA.COLUMNS 
  WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'gb_prompt_templates' 
  AND COLUMN_NAME = 'icon'
);

SET @sql_add_icon = IF(@column_exists = 0, 
  'ALTER TABLE `gb_prompt_templates` ADD COLUMN `icon` varchar(100) DEFAULT NULL COMMENT ''图标'' AFTER `category_id`',
  'SELECT ''icon字段已存在'' as message'
);

PREPARE stmt FROM @sql_add_icon;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 迁移数据
INSERT INTO `gb_content_tools` (
  `tool_type`,
  `tool_name`,
  `slug`,
  `tool_description`,
  `site_id`,
  `category_id`,
  `icon`,
  `template_code`,
  `template_type`,
  `system_prompt`,
  `user_prompt_template`,
  `output_format`,
  `example_output`,
  `variables`,
  `default_values`,
  `ai_platform_id`,
  `model_name`,
  `temperature`,
  `max_tokens`,
  `is_public`,
  `config`,
  `enabled`,
  `sort_order`,
  `create_by`,
  `create_time`,
  `update_by`,
  `update_time`,
  `remark`
)
SELECT 
  'ai_generator' as tool_type,
  name as tool_name,
  COALESCE(template_code, CONCAT('ai_gen_', id)) as slug,
  COALESCE(description, '') as tool_description,
  COALESCE(site_id, 0) as site_id,
  category_id,
  '🤖' as icon,
  template_code,
  template_type,
  system_prompt,
  user_prompt_template,
  output_format,
  example_output,
  variables,
  default_values,
  ai_platform_id,
  model_name,
  temperature,
  max_tokens,
  COALESCE(is_public, '0') as is_public,
  NULL as config,
  COALESCE(status, '1') as enabled,
  COALESCE(sort_order, 0) as sort_order,
  COALESCE(create_by, 'admin') as create_by,
  COALESCE(create_time, NOW()) as create_time,
  update_by,
  update_time,
  NULL as remark
FROM `gb_prompt_templates`
WHERE del_flag = '0'
  AND NOT EXISTS (
    SELECT 1 FROM `gb_content_tools` ct 
    WHERE ct.template_code COLLATE utf8mb4_unicode_ci = gb_prompt_templates.template_code COLLATE utf8mb4_unicode_ci
  );

-- ==========================================
-- 3. 修改生成任务表，添加工具配置字段
-- ==========================================
ALTER TABLE `gb_article_generation_tasks` 
ADD COLUMN `content_tools` text COMMENT '内容工具配置JSON数组' AFTER `target_locales`;

-- 更新现有记录，将翻译配置迁移到工具配置
UPDATE `gb_article_generation_tasks` 
SET `content_tools` = JSON_ARRAY(
  JSON_OBJECT(
    'toolType', 'ai_generator',
    'toolId', template_id,
    'order', 1,
    'enabled', '1',
    'params', JSON_OBJECT(
      'platformId', platform_id,
      'categoryId', category_id
    )
  ),
  JSON_OBJECT(
    'toolType', 'translator',
    'order', 2,
    'enabled', IF(target_locales IS NOT NULL AND target_locales != '[]', '1', '0'),
    'params', JSON_OBJECT(
      'engine', 'google',
      'targetLocales', target_locales,
      'translateTitle', true,
      'translateContent', true,
      'translateDescription', true
    )
  )
)
WHERE 1=1;

-- ==========================================
-- 4. 为工具表创建网站关联关系表（支持排除机制）
-- ==========================================
CREATE TABLE IF NOT EXISTS `gb_site_content_tool_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '关系ID',
  `site_id` bigint(20) NOT NULL COMMENT '网站ID',
  `tool_id` bigint(20) NOT NULL COMMENT '工具ID',
  `relation_type` varchar(20) DEFAULT 'include' COMMENT '关系类型：include-包含 exclude-排除',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_site_tool` (`site_id`, `tool_id`),
  KEY `idx_site_id` (`site_id`),
  KEY `idx_tool_id` (`tool_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='网站内容工具关联表';

-- ==========================================
-- 5. 插入默认翻译工具配置
-- ==========================================
INSERT INTO `gb_content_tools` (
  `tool_type`,
  `tool_name`,
  `slug`,
  `tool_description`,
  `site_id`,
  `icon`,
  `is_public`,
  `config`,
  `enabled`,
  `sort_order`,
  `create_by`,
  `create_time`
)
VALUES 
(
  'translator',
  'Google翻译',
  'google_translate',
  '使用Google翻译API进行文本翻译，支持多种语言互译',
  0,
  '🌐',
  '1',
  JSON_OBJECT(
    'engine', 'google',
    'apiUrl', 'https://m3u8-player.5yxy5.com/api/forward/https://translate.googleapis.com/translate_a/single',
    'languageMapping', JSON_OBJECT(
      'zh-CN', 'zh-CN',
      'zh-TW', 'zh-TW',
      'en-US', 'en',
      'ja-JP', 'ja',
      'ko-KR', 'ko'
    ),
    'defaultEngine', 'google',
    'timeout', 30000,
    'retryTimes', 3
  ),
  '1',
  1,
  'admin',
  NOW()
),
(
  'translator',
  'DeepL翻译（待配置）',
  'deepl_translate',
  '使用DeepL翻译API，翻译质量更高（需配置API Key）',
  0,
  '🔷',
  '1',
  JSON_OBJECT(
    'engine', 'deepl',
    'apiUrl', 'https://api-free.deepl.com/v2/translate',
    'apiKey', '',
    'languageMapping', JSON_OBJECT(
      'zh-CN', 'ZH',
      'en-US', 'EN',
      'ja-JP', 'JA',
      'ko-KR', 'KO'
    ),
    'timeout', 30000,
    'retryTimes', 3
  ),
  '0',
  2,
  'admin',
  NOW()
);

-- ==========================================
-- 6. 创建工具分类表（复用现有分类表）
-- ==========================================
-- 为内容工具添加分类支持
INSERT INTO `gb_categories` (`name`, `slug`, `category_type`, `site_id`, `icon`, `description`, `parent_id`, `sort_order`, `status`, `create_time`)
VALUES 
('AI生成工具', 'ai_generator_tools', 'content_tool', 0, '🤖', 'AI内容生成相关工具', 0, 1, '1', NOW()),
('翻译工具', 'translator_tools', 'content_tool', 0, '🌐', '多语言翻译工具', 0, 2, '1', NOW()),
('图片工具', 'image_tools', 'content_tool', 0, '🖼️', '图片搜索和处理工具', 0, 3, '1', NOW()),
('优化工具', 'optimizer_tools', 'content_tool', 0, '⚡', '内容优化相关工具', 0, 4, '1', NOW());

-- ==========================================
-- 7. 添加工具执行日志表（可选，用于监控和调试）
-- ==========================================
CREATE TABLE IF NOT EXISTS `gb_content_tool_logs` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `generation_id` bigint(20) DEFAULT NULL COMMENT '生成任务ID',
  `article_id` bigint(20) DEFAULT NULL COMMENT '文章ID',
  `tool_id` bigint(20) NOT NULL COMMENT '工具ID',
  `tool_type` varchar(50) NOT NULL COMMENT '工具类型',
  `tool_name` varchar(100) DEFAULT NULL COMMENT '工具名称',
  `execution_order` int(11) DEFAULT 0 COMMENT '执行顺序',
  `input_data` text COMMENT '输入数据',
  `output_data` text COMMENT '输出数据',
  `status` varchar(20) DEFAULT 'success' COMMENT '执行状态：success-成功 failed-失败 skipped-跳过',
  `error_message` text COMMENT '错误信息',
  `execution_time` int(11) DEFAULT 0 COMMENT '执行耗时（毫秒）',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_generation_id` (`generation_id`),
  KEY `idx_article_id` (`article_id`),
  KEY `idx_tool_id` (`tool_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容工具执行日志表';

-- ==========================================
-- 8. 添加索引优化
-- ==========================================
-- 为生成任务表的工具配置字段添加说明
ALTER TABLE `gb_article_generation_tasks` 
MODIFY COLUMN `content_tools` text COMMENT '内容工具配置JSON数组，格式：[{toolType, toolId, order, enabled, params}]';

-- ==========================================
-- 9. 数据验证和修正
-- ==========================================
-- 检查迁移的数据数量
SELECT 
  '原提示词模板数量' as item,
  COUNT(*) as count
FROM `gb_prompt_templates`
UNION ALL
SELECT 
  '迁移后AI工具数量' as item,
  COUNT(*) as count
FROM `gb_content_tools`
WHERE tool_type = 'ai_generator'
UNION ALL
SELECT 
  '翻译工具数量' as item,
  COUNT(*) as count
FROM `gb_content_tools`
WHERE tool_type = 'translator';

-- ==========================================
-- 10. 添加视图便于查询
-- ==========================================
CREATE OR REPLACE VIEW `v_content_tools_with_category` AS
SELECT 
  ct.*,
  c.name as category_name,
  c.icon as category_icon,
  s.name as site_name,
  (SELECT COUNT(*) FROM gb_site_content_tool_relation sctr 
   WHERE sctr.tool_id = ct.id AND sctr.relation_type = 'exclude') as excluded_sites_count
FROM `gb_content_tools` ct
LEFT JOIN `gb_categories` c ON ct.category_id = c.id
LEFT JOIN `gb_sites` s ON ct.site_id = s.id;

-- ==========================================
-- 完成提示
-- ==========================================
SELECT '✅ v2.83 内容工具系统数据库迁移完成！' as message;
SELECT '📊 数据统计：' as message;
SELECT tool_type, COUNT(*) as count, GROUP_CONCAT(tool_name SEPARATOR ', ') as tools
FROM gb_content_tools 
GROUP BY tool_type;
