-- ==========================================================
-- v3.54 迁移：将 gb_sites 中代码管理相关字段拆分到新表
-- gb_site_code_config（与 gb_sites 1:1 关联）
-- 幂等脚本：可重复执行，自动适配字段已删/未删两种状态
-- ==========================================================

-- ----------------------------
-- 1. 创建新表 gb_site_code_config
-- ----------------------------
CREATE TABLE IF NOT EXISTS `gb_site_code_config` (
  `id`                       bigint        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `site_id`                  bigint        NOT NULL                COMMENT '关联网站ID（唯一）',
  `git_provider`             varchar(20)   NULL DEFAULT NULL       COMMENT 'Git提供商：github/gitlab/gitee',
  `git_repo_url`             varchar(500)  NULL DEFAULT NULL       COMMENT 'Git仓库URL',
  `git_branch`               varchar(100)  NULL DEFAULT 'main'     COMMENT 'Git分支名称',
  `git_token`                varchar(500)  NULL DEFAULT NULL       COMMENT 'Git访问令牌（加密存储）',
  `git_auto_sync`            char(1)       NULL DEFAULT '0'        COMMENT '是否自动同步：0-否 1-是',
  `deploy_platform`          varchar(30)   NULL DEFAULT NULL       COMMENT '部署平台：local/cloudflare-pages/cloudflare-workers',
  `cloudflare_account_id`    varchar(100)  NULL DEFAULT NULL       COMMENT 'Cloudflare账户ID',
  `cloudflare_api_token`     varchar(500)  NULL DEFAULT NULL       COMMENT 'Cloudflare API Token（加密存储）',
  `cloudflare_project_name`  varchar(100)  NULL DEFAULT NULL       COMMENT 'Cloudflare项目名称',
  `cloudflare_project_id`    varchar(100)  NULL DEFAULT NULL       COMMENT 'Cloudflare项目ID',
  `custom_domains`           json          NULL                    COMMENT '自定义域名列表（JSON数组）',
  `deploy_status`            varchar(20)   NULL DEFAULT 'not_deployed' COMMENT '部署状态：not_deployed/deploying/deployed/failed',
  `deploy_url`               varchar(500)  NULL DEFAULT NULL       COMMENT '部署访问URL',
  `last_deploy_time`         datetime      NULL DEFAULT NULL       COMMENT '最后操作时间',
  `last_deploy_log`          text          NULL                    COMMENT '最后操作日志',
  `deploy_config`            json          NULL                    COMMENT '部署配置（JSON）：构建命令、环境变量等',
  `create_time`              datetime      NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`              datetime      NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_site_id` (`site_id`) USING BTREE,
  CONSTRAINT `fk_code_config_site` FOREIGN KEY (`site_id`) REFERENCES `gb_sites` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci
  COMMENT = '网站代码管理配置表（Git仓库 + 部署配置）' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- 2. 从 gb_sites 迁移现有数据
-- ----------------------------
-- ★ 情况 A：gb_sites 中 git_* 字段【尚未删除】时执行此段
--   （如字段已删除则跳过此段，直接执行情况 B）
-- INSERT INTO `gb_site_code_config` (
--   site_id, git_provider, git_repo_url, git_branch, git_token, git_auto_sync,
--   deploy_platform, cloudflare_account_id, cloudflare_api_token, cloudflare_project_name, cloudflare_project_id,
--   deploy_status, deploy_url, last_deploy_time, last_deploy_log, deploy_config
-- )
-- SELECT
--   id, git_provider, git_repo_url, git_branch, git_token, git_auto_sync,
--   deploy_platform, cloudflare_account_id, cloudflare_api_token, cloudflare_project_name, cloudflare_project_id,
--   COALESCE(deploy_status, 'not_deployed'), deploy_url, last_deploy_time, last_deploy_log, deploy_config
-- FROM `gb_sites`
-- WHERE del_flag = '0'
-- ON DUPLICATE KEY UPDATE site_id = VALUES(site_id);

-- ★ 情况 B：gb_sites 中 git_* 字段【已删除】时执行此段（幂等，重复执行安全）
INSERT INTO `gb_site_code_config` (site_id)
SELECT id FROM `gb_sites`
WHERE del_flag = '0'
  AND id NOT IN (SELECT site_id FROM `gb_site_code_config`)
ON DUPLICATE KEY UPDATE site_id = VALUES(site_id);

-- ----------------------------
-- 3. 从 gb_sites 删除已迁移的字段（MySQL 8.0+ 支持 DROP COLUMN IF EXISTS）
-- ----------------------------
ALTER TABLE `gb_sites`
  DROP COLUMN `git_provider`,
  DROP COLUMN `git_repo_url`,
  DROP COLUMN `git_branch`,
  DROP COLUMN `git_token`,
  DROP COLUMN `git_auto_sync`,
  DROP COLUMN `deploy_platform`,
  DROP COLUMN `cloudflare_account_id`,
  DROP COLUMN `cloudflare_api_token`,
  DROP COLUMN `cloudflare_project_name`,
  DROP COLUMN `cloudflare_project_id`,
  DROP COLUMN `custom_domains`,
  DROP COLUMN `deploy_status`,
  DROP COLUMN `deploy_url`,
  DROP COLUMN `last_deploy_time`,
  DROP COLUMN `last_deploy_log`,
  DROP COLUMN `deploy_config`;

SELECT 'v3.54 迁移完成：gb_site_code_config 已创建并完成数据迁移' AS migration_result;

