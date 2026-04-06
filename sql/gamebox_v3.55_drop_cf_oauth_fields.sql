-- ============================================================
-- v3.57 删除 gb_site_code_config 表中冗余的 CF OAuth 字段
-- 这三个字段在代码中从未被使用，直接移除
-- ============================================================

ALTER TABLE `gb_site_code_config`
  DROP COLUMN  `cf_oauth_access_token`,
  DROP COLUMN  `cf_oauth_refresh_token`,
  DROP COLUMN  `cf_oauth_expires_at`;
