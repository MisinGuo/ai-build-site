-- ============================================================
-- 批量生成任务功能完整初始化脚本
-- Version: 3.0
-- Date: 2025-12-30
-- Description: 创建任务配置表、标题池和导入功能
-- ============================================================

-- ----------------------------
-- 1. 创建文章生成任务配置表（全新表）
-- ----------------------------
DROP TABLE IF EXISTS `gb_article_generation_tasks`;
CREATE TABLE `gb_article_generation_tasks` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `site_id` BIGINT NOT NULL COMMENT '站点ID',
  `platform_id` BIGINT NOT NULL COMMENT 'AI平台ID',
  `template_id` BIGINT COMMENT '提示词模板ID',
  `task_name` VARCHAR(100) NOT NULL COMMENT '任务名称',
  `product_type` VARCHAR(20) DEFAULT 'game' COMMENT '关联产品类型：game-游戏, drama-短剧, general-通用',
  `product_ids` VARCHAR(1000) COMMENT '关联产品IDs JSON数组',
  `generate_count` INT DEFAULT 1 COMMENT '生成数量',
  `model` VARCHAR(100) COMMENT '使用的模型',
  `actual_prompt` TEXT COMMENT '实际提示词内容',
  `generation_params` JSON COMMENT '生成参数JSON',
  `task_status` VARCHAR(20) DEFAULT 'pending' COMMENT '任务状态: pending-待执行 running-执行中 completed-已完成 failed-失败',
  `generated_count` INT DEFAULT 0 COMMENT '已生成数量',
  `success_count` INT DEFAULT 0 COMMENT '成功数量',
  `failed_count` INT DEFAULT 0 COMMENT '失败数量',
  `start_time` DATETIME COMMENT '开始时间',
  `finish_time` DATETIME COMMENT '完成时间',
  `tokens_used` BIGINT DEFAULT 0 COMMENT '使用的Token数量',
  `error_message` TEXT COMMENT '错误信息',
  
  -- 数据源配置
  `data_source_type` VARCHAR(20) DEFAULT 'title_pool' COMMENT '数据源类型: title_pool-标题池 manual-手动输入',
  
  -- 游戏绑定策略
  `game_binding_strategy` VARCHAR(20) DEFAULT 'manual' COMMENT '游戏绑定策略: manual-手动 ai-AI智能推荐 es-ElasticSearch语义搜索 hybrid-混合模式',
  
  -- 多语言支持
  `target_locales` JSON COMMENT '目标语言列表JSON数组 如: ["zh-CN", "en-US", "ja-JP"]',
  `translation_strategy` VARCHAR(20) DEFAULT 'ai' COMMENT '翻译策略: ai-AI翻译 manual-人工翻译 skip-跳过翻译',
  
  -- 发布配置
  `auto_publish` CHAR(1) DEFAULT '1' COMMENT '生成后自动发布: 0-否(保存为草稿) 1-是(自动发布)',
  `related_game_ids` VARCHAR(500) COMMENT '关联的游戏IDs(逗号分隔)',
  `related_game_box_ids` VARCHAR(500) COMMENT '关联的游戏盒子IDs(逗号分隔)',
  
  -- 定时任务配置
  `task_type` VARCHAR(20) DEFAULT 'manual' COMMENT '任务类型: manual-手动执行 scheduled-定时执行',
  `cron_expression` VARCHAR(100) COMMENT 'Cron表达式(用于定时任务)',
  `task_enabled` CHAR(1) DEFAULT '0' COMMENT '任务启用状态: 0-禁用 1-启用',
  `daily_quota` INT DEFAULT 10 COMMENT '每日生成配额',
  `daily_generated` INT DEFAULT 0 COMMENT '今日已生成数量',
  `last_run_time` DATETIME COMMENT '上次执行时间',
  `next_run_time` DATETIME COMMENT '下次执行时间',
  `quartz_job_id` BIGINT COMMENT '关联的Quartz定时任务ID',
  
  -- 审计字段
  `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` VARCHAR(500) COMMENT '备注',
  
  PRIMARY KEY (`id`),
  INDEX `idx_site_id` (`site_id`),
  INDEX `idx_platform_id` (`platform_id`),
  INDEX `idx_task_status` (`task_status`),
  INDEX `idx_data_source` (`data_source_type`),
  INDEX `idx_game_binding` (`game_binding_strategy`),
  INDEX `idx_auto_publish` (`auto_publish`),
  INDEX `idx_task_type` (`task_type`),
  INDEX `idx_task_enabled` (`task_enabled`),
  INDEX `idx_next_run_time` (`next_run_time`),
  INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章生成任务配置表';

-- ----------------------------
-- 2. 创建文章标题池表
-- ----------------------------
DROP TABLE IF EXISTS `gb_article_title_pool`;
CREATE TABLE `gb_article_title_pool` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` VARCHAR(255) NOT NULL COMMENT '文章标题',
  `keywords` VARCHAR(500) COMMENT '关键词(逗号分隔)',
  `reference_content` TEXT COMMENT '参考内容/描述',
  `source_name` VARCHAR(100) COMMENT '数据来源名称',
  `source_url` VARCHAR(500) COMMENT '来源URL',
  `import_batch` VARCHAR(50) COMMENT '导入批次号',
  `usage_status` CHAR(1) DEFAULT '0' COMMENT '使用状态: 0-未使用 1-已使用 2-已废弃',
  `used_count` INT DEFAULT 0 COMMENT '已使用次数',
  `used_time` DATETIME COMMENT '最后使用时间',
  `used_article_ids` TEXT COMMENT '使用该标题生成的文章IDs(逗号分隔)',
  `scheduled_date` DATE COMMENT '计划发布日期(扩展字段)',
  `target_site_id` BIGINT COMMENT '目标站点ID(扩展字段)',
  `priority` INT DEFAULT 0 COMMENT '优先级(数值越大优先级越高)',
  `tags` VARCHAR(500) COMMENT '标签(逗号分隔)',
  `extra_data` JSON COMMENT '扩展数据(JSON格式,存储其他字段)',
  `del_flag` CHAR(1) DEFAULT '0' COMMENT '删除标志: 0-存在 2-删除',
  `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` VARCHAR(500) COMMENT '备注',
  PRIMARY KEY (`id`),
  INDEX `idx_usage_status` (`usage_status`),
  INDEX `idx_import_batch` (`import_batch`),
  INDEX `idx_scheduled_date` (`scheduled_date`),
  INDEX `idx_target_site` (`target_site_id`),
  INDEX `idx_priority` (`priority` DESC),
  INDEX `idx_create_time` (`create_time` DESC),
  INDEX `idx_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章标题池表(存储外部提供的标题数据)';

-- ----------------------------
-- 3. 创建标题导入日志表
-- ----------------------------
DROP TABLE IF EXISTS `gb_title_import_log`;
CREATE TABLE `gb_title_import_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `import_batch` VARCHAR(50) NOT NULL COMMENT '导入批次号',
  `import_type` VARCHAR(20) DEFAULT 'json' COMMENT '导入类型: json-JSON文件 excel-Excel文件 api-API接口',
  `file_name` VARCHAR(255) COMMENT '文件名称',
  `file_path` VARCHAR(500) COMMENT '文件路径',
  `total_count` INT DEFAULT 0 COMMENT '总数量',
  `success_count` INT DEFAULT 0 COMMENT '成功数量',
  `failed_count` INT DEFAULT 0 COMMENT '失败数量',
  `duplicate_count` INT DEFAULT 0 COMMENT '重复数量',
  `import_status` VARCHAR(20) DEFAULT 'processing' COMMENT '导入状态: processing-处理中 completed-已完成 failed-失败',
  `error_message` TEXT COMMENT '错误信息',
  `import_details` JSON COMMENT '导入详情JSON',
  `create_by` VARCHAR(64) DEFAULT '' COMMENT '导入者',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_import_batch` (`import_batch`),
  INDEX `idx_import_type` (`import_type`),
  INDEX `idx_import_status` (`import_status`),
  INDEX `idx_create_time` (`create_time` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标题导入日志表';

-- ----------------------------
-- 4. 数据迁移（可选）
-- 说明：如果你之前有 gb_article_generations 表的数据需要迁移，执行以下语句
-- ----------------------------
-- INSERT INTO gb_article_generation_tasks 
-- (site_id, platform_id, template_id, task_name, task_status, create_time)
-- SELECT 
--   site_id, ai_platform_id, prompt_template_id, 
--   CONCAT('迁移任务_', id), generation_status, create_time
-- FROM gb_article_generations
-- WHERE generation_type = 'batch' AND batch_task_id IS NOT NULL;

-- 脚本执行完成
SELECT '批量生成任务完整初始化完成! 已创建任务配置表、标题池和导入功能。' AS message;
SELECT '注意：新表名为 gb_article_generation_tasks，与原有的 gb_article_generations（生成记录表）不同。' AS notice;
