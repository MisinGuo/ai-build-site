-- 将 gb_ai_platforms 表的 platform_code 列重命名为 platform_type
ALTER TABLE `gb_ai_platforms`
    CHANGE COLUMN `platform_code` `platform_type` VARCHAR(50) NOT NULL COMMENT '平台类型：openai, azure, qwen, wenxin, other';
