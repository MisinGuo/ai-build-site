-- 为站点模板表添加 config_mappings 字段
-- 功能：存储模板配置文件变量映射，定义哪些 TS 文件变量开放给用户可视化编辑
-- 执行时间：2026-04-02

ALTER TABLE `gb_site_templates`
    ADD COLUMN `config_mappings` LONGTEXT NULL COMMENT '配置文件映射（JSON 数组：定义哪些 TS 文件变量开放给用户编辑）' AFTER `env_template`;
