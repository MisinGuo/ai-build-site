-- ==========================================
-- gamebox v3.67 平台账号库
-- 新增平台级 Git 账号库 & Cloudflare 账号库
-- 解耦站点与账号信息，支持多账号统一管理
-- ==========================================

-- ----------------------------
-- 1. 平台 Git 账号表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `gb_platform_git_accounts` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`          VARCHAR(100)    NOT NULL COMMENT '账号显示名称（如"公司主GitHub账号"）',
    `provider`      VARCHAR(20)     NOT NULL DEFAULT 'github' COMMENT 'Git平台：github / gitee / gitlab',
    `account`       VARCHAR(100)    NOT NULL COMMENT 'Git用户名或组织名',
    `token`         VARCHAR(1000)   NOT NULL COMMENT 'Personal Access Token（存储原文，展示时脱敏）',
    `is_default`    TINYINT         NOT NULL DEFAULT 0 COMMENT '是否默认账号：0-否 1-是（同一时刻只有一个默认）',
    `status`        CHAR(1)         NOT NULL DEFAULT '1' COMMENT '状态：0-禁用 1-启用',
    `remark`        VARCHAR(500)    NULL DEFAULT NULL COMMENT '备注',
    `create_by`     VARCHAR(64)     NULL DEFAULT '' COMMENT '创建者',
    `create_time`   DATETIME        NULL DEFAULT NULL COMMENT '创建时间',
    `update_by`     VARCHAR(64)     NULL DEFAULT '' COMMENT '更新者',
    `update_time`   DATETIME        NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='平台Git账号库：存放公司各Git平台账号，供建站向导选择使用';

-- ----------------------------
-- 2. 平台 Cloudflare 账号表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `gb_platform_cf_accounts` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`          VARCHAR(100)    NOT NULL COMMENT '账号显示名称（如"主CF账号"）',
    `account_id`    VARCHAR(200)    NOT NULL COMMENT 'Cloudflare Account ID',
    `api_token`     VARCHAR(1000)   NOT NULL COMMENT 'Cloudflare API Token（存储原文，展示时脱敏）',
    `is_default`    TINYINT         NOT NULL DEFAULT 0 COMMENT '是否默认账号：0-否 1-是（同一时刻只有一个默认）',
    `status`        CHAR(1)         NOT NULL DEFAULT '1' COMMENT '状态：0-禁用 1-启用',
    `remark`        VARCHAR(500)    NULL DEFAULT NULL COMMENT '备注',
    `create_by`     VARCHAR(64)     NULL DEFAULT '' COMMENT '创建者',
    `create_time`   DATETIME        NULL DEFAULT NULL COMMENT '创建时间',
    `update_by`     VARCHAR(64)     NULL DEFAULT '' COMMENT '更新者',
    `update_time`   DATETIME        NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='平台Cloudflare账号库：存放公司CF账号，供建站向导部署时选择使用';

-- ----------------------------
-- 3. 菜单：平台账号管理（放在 基础配置 2001 下）
-- ----------------------------

-- Git 账号管理（menu_id=2145）
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (2145, 'Git账号管理', 2001, 6, 'git-accounts', 'gamebox/config/git-accounts/index', NULL, 1, 0, 'C', '0', '0', 'gamebox:gitAccount:list', 'github', 'admin', NOW(), '', NULL, '平台Git账号管理菜单');

INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`)
VALUES (2146, 'Git账号查询', 2145, 1, '#', 1, 0, 'F', '0', '0', 'gamebox:gitAccount:query', '#', 'admin', NOW());
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`)
VALUES (2147, 'Git账号新增', 2145, 2, '#', 1, 0, 'F', '0', '0', 'gamebox:gitAccount:add', '#', 'admin', NOW());
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`)
VALUES (2148, 'Git账号修改', 2145, 3, '#', 1, 0, 'F', '0', '0', 'gamebox:gitAccount:edit', '#', 'admin', NOW());
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`)
VALUES (2149, 'Git账号删除', 2145, 4, '#', 1, 0, 'F', '0', '0', 'gamebox:gitAccount:remove', '#', 'admin', NOW());

-- CF 账号管理（menu_id=2150）
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (2150, 'CF账号管理', 2001, 7, 'cf-accounts', 'gamebox/config/cf-accounts/index', NULL, 1, 0, 'C', '0', '0', 'gamebox:cfAccount:list', 'cloudflare', 'admin', NOW(), '', NULL, '平台Cloudflare账号管理菜单');

INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`)
VALUES (2151, 'CF账号查询', 2150, 1, '#', 1, 0, 'F', '0', '0', 'gamebox:cfAccount:query', '#', 'admin', NOW());
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`)
VALUES (2152, 'CF账号新增', 2150, 2, '#', 1, 0, 'F', '0', '0', 'gamebox:cfAccount:add', '#', 'admin', NOW());
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`)
VALUES (2153, 'CF账号修改', 2150, 3, '#', 1, 0, 'F', '0', '0', 'gamebox:cfAccount:edit', '#', 'admin', NOW());
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`)
VALUES (2154, 'CF账号删除', 2150, 4, '#', 1, 0, 'F', '0', '0', 'gamebox:cfAccount:remove', '#', 'admin', NOW());
