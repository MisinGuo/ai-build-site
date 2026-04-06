-- =============================================
-- 原子工具初始数据 - 文本优化工具
-- =============================================
-- 说明：添加第一个原子工具示例，用于文本优化功能
-- 执行时机：在 gamebox_v2.88_workflow_and_atomic_tool_menu.sql 之后执行
-- =============================================

-- 插入文本优化原子工具
INSERT INTO `gb_atomic_tool` (`tool_code`, `tool_name`, `tool_type`, `description`, `config`, `inputs`, `outputs`, `enabled`, `site_id`, `create_by`, `create_time`, `remark`) 
VALUES (
  'text_optimizer',
  '文本优化工具',
  'ai',
  '使用AI优化文本内容，使其更流畅、专业、有条理。适用于文章编辑、内容润色等场景',
  '{
    "aiPlatformId": 1,
    "systemPrompt": "你是一位专业的文案优化助手。你的任务是优化用户提供的文本内容，使其：\\n1. 语言更加流畅自然\\n2. 表达更加准确专业\\n3. 结构更加清晰有条理\\n4. 保持原文的核心意思和关键信息\\n5. 适当润色，提升可读性\\n\\n注意：\\n- 保持原文的markdown格式\\n- 不要添加额外的解释说明\\n- 直接输出优化后的内容",
    "userPromptTemplate": "请优化以下文本：\\n\\n{{originalText}}",
    "temperature": 0.7,
    "maxTokens": 2000
  }',
  '[
    {
      "name": "originalText",
      "type": "textarea",
      "label": "原始文本",
      "required": true,
      "description": "需要优化的文本内容",
      "rows": 5
    }
  ]',
  '[
    {
      "name": "generatedText",
      "type": "text",
      "label": "优化后文本",
      "description": "AI优化后的文本内容"
    },
    {
      "name": "wordCount",
      "type": "number",
      "label": "字数",
      "description": "优化后的文本字数"
    }
  ]',
  1,
  0,
  'admin',
  NOW(),
  '文本优化工具 - 第一个原子工具示例'
);

-- 插入AI文章生成工具
INSERT INTO `gb_atomic_tool` (`tool_code`, `tool_name`, `tool_type`, `description`, `config`, `inputs`, `outputs`, `enabled`, `site_id`, `create_by`, `create_time`, `remark`) 
VALUES (
  'ai_article_generator',
  'AI文章生成器',
  'ai',
  '根据标题和大纲生成完整文章内容。支持自定义字数、风格等参数',
  '{
    "aiPlatformId": 1,
    "systemPrompt": "你是一位专业的内容创作者，擅长根据标题和大纲创作高质量文章。要求：\\n1. 内容充实、逻辑清晰\\n2. 语言流畅、表达专业\\n3. 适当使用markdown格式\\n4. 包含必要的段落结构",
    "userPromptTemplate": "请根据以下信息创作文章：\\n\\n标题：{{title}}\\n大纲：{{outline}}\\n目标字数：{{wordCount}}字\\n\\n请生成完整文章内容：",
    "temperature": 0.8,
    "maxTokens": 3000
  }',
  '[
    {
      "name": "title",
      "type": "text",
      "label": "文章标题",
      "required": true,
      "description": "文章的标题"
    },
    {
      "name": "outline",
      "type": "textarea",
      "label": "文章大纲",
      "required": false,
      "description": "文章的大纲或要点（可选）",
      "rows": 3
    },
    {
      "name": "wordCount",
      "type": "number",
      "label": "目标字数",
      "required": false,
      "default": 800,
      "description": "期望生成的文章字数"
    }
  ]',
  '[
    {
      "name": "generatedText",
      "type": "text",
      "label": "生成的文章",
      "description": "AI生成的完整文章内容"
    },
    {
      "name": "wordCount",
      "type": "number",
      "label": "实际字数",
      "description": "生成文章的实际字数"
    }
  ]',
  1,
  0,
  'admin',
  NOW(),
  'AI文章生成工具'
);

-- 插入关键词提取工具
INSERT INTO `gb_atomic_tool` (`tool_code`, `tool_name`, `tool_type`, `description`, `config`, `inputs`, `outputs`, `enabled`, `site_id`, `create_by`, `create_time`, `remark`) 
VALUES (
  'keyword_extractor',
  'SEO关键词提取',
  'ai',
  '从标题和内容中提取SEO关键词，帮助优化文章搜索引擎排名',
  '{
    "aiPlatformId": 1,
    "systemPrompt": "你是一位SEO专家，擅长从内容中提取关键词。要求：\\n1. 提取最相关的3-5个关键词\\n2. 关键词要符合SEO最佳实践\\n3. 考虑搜索意图和用户需求\\n4. 只输出关键词，用逗号分隔",
    "userPromptTemplate": "请从以下内容中提取SEO关键词：\\n\\n标题：{{title}}\\n内容：{{content}}\\n\\n请提取5个最相关的SEO关键词（用逗号分隔）：",
    "temperature": 0.5,
    "maxTokens": 200
  }',
  '[
    {
      "name": "title",
      "type": "text",
      "label": "标题",
      "required": true,
      "description": "文章或内容的标题"
    },
    {
      "name": "content",
      "type": "textarea",
      "label": "内容",
      "required": true,
      "description": "文章或内容的正文",
      "rows": 5
    }
  ]',
  '[
    {
      "name": "keywords",
      "type": "text",
      "label": "关键词",
      "description": "提取的SEO关键词（逗号分隔）"
    }
  ]',
  1,
  0,
  'admin',
  NOW(),
  'SEO关键词提取工具'
);

-- 插入文章摘要生成工具
INSERT INTO `gb_atomic_tool` (`tool_code`, `tool_name`, `tool_type`, `description`, `config`, `inputs`, `outputs`, `enabled`, `site_id`, `create_by`, `create_time`, `remark`) 
VALUES (
  'summary_generator',
  '文章摘要生成器',
  'ai',
  '自动生成文章摘要，用于文章列表展示或SEO描述',
  '{
    "aiPlatformId": 1,
    "systemPrompt": "你是一位专业的内容编辑，擅长提炼文章核心内容。要求：\\n1. 准确概括文章主要内容\\n2. 语言精炼、吸引人\\n3. 控制在指定字数内\\n4. 不要添加原文中没有的信息",
    "userPromptTemplate": "请为以下文章生成摘要（{{maxLength}}字以内）：\\n\\n{{content}}",
    "temperature": 0.6,
    "maxTokens": 500
  }',
  '[
    {
      "name": "content",
      "type": "textarea",
      "label": "文章内容",
      "required": true,
      "description": "需要生成摘要的文章内容",
      "rows": 8
    },
    {
      "name": "maxLength",
      "type": "number",
      "label": "最大长度",
      "required": false,
      "default": 200,
      "description": "摘要的最大字数"
    }
  ]',
  '[
    {
      "name": "summary",
      "type": "text",
      "label": "文章摘要",
      "description": "生成的文章摘要"
    }
  ]',
  1,
  0,
  'admin',
  NOW(),
  '文章摘要生成工具'
);

-- 验证数据插入
SELECT 
  tool_code AS '工具编码',
  tool_name AS '工具名称',
  tool_type AS '工具类型',
  enabled AS '启用状态',
  create_time AS '创建时间'
FROM gb_atomic_tool 
ORDER BY id;
