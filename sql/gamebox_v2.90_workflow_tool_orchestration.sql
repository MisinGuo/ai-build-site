-- ====================================================================
-- 工作流工具编排系统升级 v2.90
-- 功能：支持在工作流中可视化组织原子工具，配置输入输出映射
-- 日期：2026-01-05
-- ====================================================================

-- 更新工作流表，增强工具编排能力
ALTER TABLE `gb_workflow` 
  ADD COLUMN `version` int DEFAULT 1 COMMENT '工作流版本号' AFTER `step_count`,
  ADD COLUMN `category` varchar(50) DEFAULT 'custom' COMMENT '工作流分类：custom-自定义, system-系统, template-模板' AFTER `version`,
  ADD COLUMN `tags` varchar(500) DEFAULT NULL COMMENT '标签（JSON数组）' AFTER `category`;

-- 修改definition字段注释，明确JSON结构
ALTER TABLE `gb_workflow` 
  MODIFY COLUMN `definition` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL 
  COMMENT '工作流定义（JSON格式）
{
  "inputs": [{"name":"", "type":"", "label":"", "required":true, "default":""}],
  "steps": [{
    "stepId": "step_1",
    "stepName": "步骤名称",
    "toolCode": "原子工具编码",
    "description": "步骤描述",
    "inputMapping": {
      "toolInputParam": {
        "source": "input|step_{id}|constant",
        "value": "input.xxx | step_1.output.xxx | 常量值"
      }
    },
    "enabled": true,
    "continueOnError": false,
    "timeout": 300,
    "retry": {"times": 0, "interval": 1000}
  }],
  "outputs": [{"name":"", "source":"step_n.output.xxx", "description":""}]
}';

-- 工作流模板表（预定义工作流模板供用户使用）
CREATE TABLE IF NOT EXISTS `gb_workflow_template` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `template_code` varchar(100) NOT NULL COMMENT '模板编码',
  `template_name` varchar(200) NOT NULL COMMENT '模板名称',
  `description` text COMMENT '模板描述',
  `category` varchar(50) DEFAULT 'general' COMMENT '模板分类',
  `icon` varchar(100) DEFAULT NULL COMMENT '图标',
  `definition` text NOT NULL COMMENT '工作流定义（JSON格式，结构同gb_workflow）',
  `usage_count` int DEFAULT 0 COMMENT '使用次数',
  `is_public` tinyint(1) DEFAULT 1 COMMENT '是否公开',
  `sort_order` int DEFAULT 0 COMMENT '排序',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_template_code` (`template_code`),
  KEY `idx_category` (`category`),
  KEY `idx_is_public` (`is_public`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='工作流模板表';

-- 插入预定义工作流模板
INSERT INTO `gb_workflow_template` 
(`template_code`, `template_name`, `description`, `category`, `icon`, `definition`, `is_public`, `sort_order`) 
VALUES 
('title_to_article', '标题生成文章', '从标题获取开始，生成文章并发布的完整流程', 'content', '📝', 
'{
  "inputs": [
    {"name": "siteId", "type": "number", "label": "目标站点", "required": true},
    {"name": "categoryId", "type": "number", "label": "文章分类", "required": false}
  ],
  "steps": [
    {
      "stepId": "step_1",
      "stepName": "获取标题",
      "toolCode": "get_title_from_pool",
      "description": "从标题池获取一个未使用的标题",
      "inputMapping": {
        "siteId": {"source": "input", "value": "input.siteId"},
        "categoryId": {"source": "input", "value": "input.categoryId"}
      },
      "enabled": true,
      "continueOnError": false
    },
    {
      "stepId": "step_2",
      "stepName": "AI生成文章",
      "toolCode": "ai_article_generator",
      "description": "根据标题使用AI生成文章内容",
      "inputMapping": {
        "title": {"source": "step_1", "value": "step_1.output.title"},
        "keywords": {"source": "step_1", "value": "step_1.output.keywords"}
      },
      "enabled": true,
      "continueOnError": false
    },
    {
      "stepId": "step_3",
      "stepName": "发布文章",
      "toolCode": "publish_article",
      "description": "将生成的文章发布到网站",
      "inputMapping": {
        "siteId": {"source": "input", "value": "input.siteId"},
        "categoryId": {"source": "input", "value": "input.categoryId"},
        "title": {"source": "step_1", "value": "step_1.output.title"},
        "content": {"source": "step_2", "value": "step_2.output.generatedText"}
      },
      "enabled": true,
      "continueOnError": false
    }
  ],
  "outputs": [
    {"name": "articleId", "source": "step_3.output.articleId", "description": "发布的文章ID"},
    {"name": "articleUrl", "source": "step_3.output.articleUrl", "description": "文章访问URL"}
  ]
}', 1, 1);

-- 工作流执行日志增强（记录每个步骤的输入输出）
ALTER TABLE `gb_workflow_step_execution`
  ADD COLUMN `input_data` text COMMENT '步骤输入数据（JSON）' AFTER `status`,
  ADD COLUMN `output_data` text COMMENT '步骤输出数据（JSON）' AFTER `input_data`,
  ADD COLUMN `step_config` text COMMENT '步骤配置快照（JSON）' AFTER `output_data`;

-- 创建工作流变量表（用于在工作流执行过程中存储临时变量）
CREATE TABLE IF NOT EXISTS `gb_workflow_variables` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `execution_id` varchar(100) NOT NULL COMMENT '执行ID',
  `variable_name` varchar(100) NOT NULL COMMENT '变量名',
  `variable_value` text COMMENT '变量值（JSON格式）',
  `variable_type` varchar(50) DEFAULT 'string' COMMENT '变量类型',
  `step_id` varchar(100) DEFAULT NULL COMMENT '来源步骤ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_execution_id` (`execution_id`),
  KEY `idx_variable_name` (`variable_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='工作流执行变量表';

-- 更新现有工作流数据的definition字段，使其符合新格式
UPDATE `gb_workflow` 
SET `definition` = JSON_SET(
  `definition`,
  '$.steps',
  JSON_ARRAY()
)
WHERE JSON_EXTRACT(`definition`, '$.steps') IS NULL;

-- 添加工作流执行统计字段
ALTER TABLE `gb_workflow`
  ADD COLUMN `execution_count` int DEFAULT 0 COMMENT '执行次数' AFTER `step_count`,
  ADD COLUMN `success_count` int DEFAULT 0 COMMENT '成功次数' AFTER `execution_count`,
  ADD COLUMN `last_execution_time` datetime DEFAULT NULL COMMENT '最后执行时间' AFTER `success_count`;

-- 创建索引优化查询性能
CREATE INDEX `idx_workflow_category` ON `gb_workflow`(`category`, `enabled`);
CREATE INDEX `idx_template_category` ON `gb_workflow_template`(`category`, `is_public`);
CREATE INDEX `idx_execution_status_time` ON `gb_workflow_execution`(`status`, `create_time`);

-- 添加工作流标签支持
CREATE TABLE IF NOT EXISTS `gb_workflow_tags` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `tag_name` varchar(50) NOT NULL COMMENT '标签名称',
  `tag_color` varchar(20) DEFAULT '#409EFF' COMMENT '标签颜色',
  `usage_count` int DEFAULT 0 COMMENT '使用次数',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tag_name` (`tag_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='工作流标签表';

-- 插入常用标签
INSERT INTO `gb_workflow_tags` (`tag_name`, `tag_color`) VALUES
('内容生成', '#409EFF'),
('批量处理', '#67C23A'),
('定时任务', '#E6A23C'),
('数据同步', '#F56C6C'),
('SEO优化', '#909399'),
('多语言', '#B37FEB');

-- 工作流收藏表
CREATE TABLE IF NOT EXISTS `gb_workflow_favorites` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `workflow_id` bigint NOT NULL COMMENT '工作流ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_workflow` (`user_id`, `workflow_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='工作流收藏表';

-- ====================================================================
-- 升级说明
-- ====================================================================
-- 1. 工作流定义增强：支持步骤间的输入输出映射
-- 2. 新增工作流模板：提供预定义模板供用户快速创建工作流
-- 3. 执行日志增强：记录每个步骤的详细输入输出数据
-- 4. 变量管理：支持工作流执行过程中的临时变量存储
-- 5. 统计功能：添加执行统计字段
-- 6. 标签系统：支持工作流分类和检索
-- 7. 收藏功能：用户可以收藏常用工作流

-- 执行后请重启应用以加载新的数据结构
