-- 为站点模板表添加 git_account_id 字段
-- 功能：关联用于克隆私有模板仓库的 Git 账号，避免在 URL 中直接嵌入 token
-- 执行时间：2026-04-05

ALTER TABLE `gb_site_templates`
    ADD COLUMN `git_account_id` BIGINT NULL DEFAULT NULL
        COMMENT '克隆模板仓库使用的 Git 账号 ID（可选，留空则仅适用于公开仓库）'
        AFTER `template_git_branch`;
