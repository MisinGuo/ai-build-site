-- ===================================================================
-- Cloudflare Workers 部署支持
-- 版本: v2.4
-- 日期: 2026-03-01
-- 说明: gb_site_code_config 表已有 cloudflare_account_id / cloudflare_api_token
--       / cloudflare_project_name 字段，每个网站单独配置自己的 CF 账号，
--       无需新增字段，此文件作为版本占位记录。
-- ===================================================================

-- 无需执行 ALTER TABLE，现有字段说明：
-- cloudflare_account_id   每个网站独立的 CF Account ID
-- cloudflare_api_token    每个网站独立的 CF API Token
-- cloudflare_project_name 对应 Cloudflare Workers 的 script name

-- 验证现有字段
SELECT
    COLUMN_NAME,
    COLUMN_TYPE,
    COLUMN_COMMENT
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME = 'gb_site_code_config'
  AND COLUMN_NAME IN ('cloudflare_account_id','cloudflare_api_token','cloudflare_project_name')
ORDER BY ORDINAL_POSITION;
