-- ==========================================
-- gamebox v3.68 gb_site_code_config 迁移至平台账号
-- 移除原始凭据字段（git_token / cloudflare_account_id / cloudflare_api_token），
-- 改用关联平台账号 ID（git_account_id / cf_account_id）存储。
-- 依赖：v3.67 已创建 gb_platform_git_accounts / gb_platform_cf_accounts 表
-- ==========================================

-- 1. 添加平台账号关联列
ALTER TABLE `gb_site_code_config`
    ADD COLUMN `git_account_id` BIGINT NULL COMMENT '关联Git平台账号ID（替代git_token字段）' AFTER `git_auto_sync`,
    ADD COLUMN `cf_account_id`  BIGINT NULL COMMENT '关联CF平台账号ID（替代cloudflare_account_id/cloudflare_api_token字段）' AFTER `git_account_id`;

-- 2. 删除旧的原始凭据字段
ALTER TABLE `gb_site_code_config`
    DROP COLUMN `git_token`,
    DROP COLUMN `cloudflare_account_id`,
    DROP COLUMN `cloudflare_api_token`;
