-- ==========================================
-- gamebox v3.69 功能代理配置
-- 新增 gb_proxy_config 表，记录各系统功能的代理配置
-- 每个功能条目预置，用户只可编辑代理参数，不可新增/删除
-- ==========================================

-- ----------------------------
-- 1. 功能代理配置表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `gb_proxy_config` (
    `id`               BIGINT          NOT NULL AUTO_INCREMENT COMMENT '主键',
    `feature_key`      VARCHAR(100)    NOT NULL COMMENT '功能标识符（唯一）',
    `feature_name`     VARCHAR(200)    NOT NULL COMMENT '功能名称',
    `feature_group`    VARCHAR(100)    NOT NULL DEFAULT '' COMMENT '功能分组',
    `feature_desc`     TEXT            NULL COMMENT '功能说明（对外部调用的描述）',
    `proxy_applicable` TINYINT(1)      NOT NULL DEFAULT 0 COMMENT '是否支持代理配置：0-不支持/本地操作，1-支持/有外部网络调用',
    `proxy_warning`    TEXT            NULL COMMENT '代理使用注意事项（不建议代理或特殊说明）',
    `proxy_enabled`    TINYINT(1)      NOT NULL DEFAULT 0 COMMENT '是否启用代理：0-不启用，1-启用',
    `proxy_type`       VARCHAR(20)     NULL DEFAULT 'http' COMMENT '代理协议类型：http/https/socks5',
    `proxy_host`       VARCHAR(255)    NULL COMMENT '代理服务器地址（IP或域名）',
    `proxy_port`       INT             NULL COMMENT '代理服务器端口',
    `proxy_username`   VARCHAR(200)    NULL COMMENT '代理认证用户名（不需要则留空）',
    `proxy_password`   VARCHAR(500)    NULL COMMENT '代理认证密码（不需要则留空）',
    `sort_order`       INT             NOT NULL DEFAULT 0 COMMENT '排序权重',
    `create_by`        VARCHAR(64)     NOT NULL DEFAULT '' COMMENT '创建者',
    `create_time`      DATETIME        NULL COMMENT '创建时间',
    `update_by`        VARCHAR(64)     NOT NULL DEFAULT '' COMMENT '更新者',
    `update_time`      DATETIME        NULL COMMENT '更新时间',
    `remark`           VARCHAR(500)    NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_feature_key` (`feature_key`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='系统功能代理配置表：记录各功能是否启用代理及代理参数';

-- ----------------------------
-- 2. 预置功能数据
-- ----------------------------

-- ---- 分组：AI内容生成 ----
INSERT INTO `gb_proxy_config` (`feature_key`, `feature_name`, `feature_group`, `feature_desc`, `proxy_applicable`, `proxy_warning`, `sort_order`, `create_by`, `create_time`, `update_by`)
VALUES
('ai_platform_call', 'AI平台API调用', 'AI内容生成',
 '调用 OpenAI、Azure OpenAI、通义千问等 AI 平台 API。国内服务器访问 OpenAI/Azure 等境外服务时通常需要配置代理。',
 1, NULL, 10, 'system', NOW(), 'system'),

('translation_service', '翻译服务调用', 'AI内容生成',
 '调用外部翻译 API（如 Google Translate、DeepL 等）对内容进行翻译。境外翻译服务在国内可能需要代理。',
 1, NULL, 20, 'system', NOW(), 'system');

-- ---- 分组：Git / 代码管理 ----
INSERT INTO `gb_proxy_config` (`feature_key`, `feature_name`, `feature_group`, `feature_desc`, `proxy_applicable`, `proxy_warning`, `sort_order`, `create_by`, `create_time`, `update_by`)
VALUES
('git_account_ops', 'Git账号 - 仓库克隆/推送', 'Git与代码管理',
 '通过 Git 账号执行仓库克隆、代码推送等操作，涉及 GitHub、Gitee、GitLab 等平台的网络连接。GitHub 在国内可能需要代理。',
 1, NULL, 30, 'system', NOW(), 'system'),

('storage_github', 'GitHub存储 - 文件读写', 'Git与代码管理',
 '通过 GitHub API 上传/下载存储于 GitHub 仓库中的图片、文章等文件资源。需要访问 GitHub API，国内通常需要代理。',
 1, NULL, 40, 'system', NOW(), 'system');

-- ---- 分组：云服务账号 ----
INSERT INTO `gb_proxy_config` (`feature_key`, `feature_name`, `feature_group`, `feature_desc`, `proxy_applicable`, `proxy_warning`, `sort_order`, `create_by`, `create_time`, `update_by`)
VALUES
('cf_account_api', 'Cloudflare API操作', '云服务账号',
 '通过 Cloudflare 账号调用 Cloudflare API，管理域名解析、Pages 部署、Workers 配置等。Cloudflare API 一般在国内可直接访问，不一定需要代理。',
 1, '⚠ Cloudflare API 在大多数国内环境下可直接访问，错误配置代理可能导致请求失败，请先测试是否真正需要代理。', 50, 'system', NOW(), 'system'),

('storage_r2_s3', 'R2/S3对象存储 - 文件读写', '云服务账号',
 '通过 AWS S3 API 或 Cloudflare R2 API 上传/下载存储中的文件资源。R2/S3 的 API 端点一般可直连，按实际需要配置代理。',
 1, '⚠ R2/S3 存储的 API 端点通常可直接访问，建议先测试直连后再决定是否启用代理。', 60, 'system', NOW(), 'system');

-- ---- 分组：工作流工具 ----
INSERT INTO `gb_proxy_config` (`feature_key`, `feature_name`, `feature_group`, `feature_desc`, `proxy_applicable`, `proxy_warning`, `sort_order`, `create_by`, `create_time`, `update_by`)
VALUES
('workflow_git_tool', '工作流 - Git文件拉取工具', '工作流工具',
 '工作流中使用"获取Git文件"原子工具，从 GitHub 等平台拉取指定仓库文件。与 Git账号操作共享代理需求。',
 1, NULL, 70, 'system', NOW(), 'system'),

('workflow_api_tool', '工作流 - 外部API调用工具', '工作流工具',
 '工作流中使用"API调用"原子工具，向外部 HTTP/HTTPS 接口发送请求。是否需要代理取决于目标 API 的可访问性。',
 1, '⚠ 该工具调用的目标 API 各不相同，请根据实际目标接口决定是否启用代理。部分目标 API 可能不兼容代理。', 80, 'system', NOW(), 'system'),

('workflow_ai_gen', '工作流 - AI内容生成工具', '工作流工具',
 '工作流中使用"AI生成"原子工具生成文章/标题内容，底层调用 AI 平台 API。该工具直接使用各 AI 平台配置中的代理设置。',
 0, '⚠ 此功能的代理由所关联的"AI平台配置"决定。如需为 AI 生成配置代理，请前往"AI平台配置"页面单独设置各平台的代理参数。', 90, 'system', NOW(), 'system');

-- ---- 分组：数据管理（本地操作，不涉及外部网络） ----
INSERT INTO `gb_proxy_config` (`feature_key`, `feature_name`, `feature_group`, `feature_desc`, `proxy_applicable`, `sort_order`, `create_by`, `create_time`, `update_by`)
VALUES
('site_mgmt', '站点管理', '数据管理',
 '站点信息的增删改查，仅操作本地数据库，不涉及外部网络调用。',
 0, 100, 'system', NOW(), 'system'),

('category_mgmt', '分类管理', '数据管理',
 '游戏/内容分类的增删改查，仅操作本地数据库。',
 0, 110, 'system', NOW(), 'system'),

('game_mgmt', '游戏管理', '数据管理',
 '游戏信息的增删改查，仅操作本地数据库。',
 0, 120, 'system', NOW(), 'system'),

('gamebox_mgmt', '游戏盒子管理', '数据管理',
 '游戏盒子（合集/平台）信息的增删改查，仅操作本地数据库。',
 0, 130, 'system', NOW(), 'system'),

('field_mapping_mgmt', '字段映射配置', '数据管理',
 '配置数据导入时的字段映射规则，仅操作本地数据库。',
 0, 140, 'system', NOW(), 'system'),

('data_import_mgmt', '游戏数据批量导入', '数据管理',
 '从 JSON/Excel 文件导入游戏/盒子数据，为本地文件处理，仅操作本地数据库。',
 0, 150, 'system', NOW(), 'system');

-- ---- 分组：内容管理（本地操作） ----
INSERT INTO `gb_proxy_config` (`feature_key`, `feature_name`, `feature_group`, `feature_desc`, `proxy_applicable`, `sort_order`, `create_by`, `create_time`, `update_by`)
VALUES
('article_mgmt', '文章管理', '内容管理',
 '文章内容的增删改查及发布状态管理，仅操作本地数据库。',
 0, 160, 'system', NOW(), 'system'),

('title_pool_mgmt', '标题词库管理', '内容管理',
 '文章标题词库的批次管理和标题条目维护，仅操作本地数据库。',
 0, 170, 'system', NOW(), 'system'),

('drama_mgmt', '影视/戏剧管理', '内容管理',
 '影视戏剧内容及供应商的增删改查，仅操作本地数据库。',
 0, 180, 'system', NOW(), 'system'),

('master_article_mgmt', '母文章管理', '内容管理',
 '母文章（跨语言关联文章）的管理，仅操作本地数据库。',
 0, 190, 'system', NOW(), 'system');

-- ---- 分组：工作流配置（本地操作） ----
INSERT INTO `gb_proxy_config` (`feature_key`, `feature_name`, `feature_group`, `feature_desc`, `proxy_applicable`, `sort_order`, `create_by`, `create_time`, `update_by`)
VALUES
('workflow_config', '工作流配置管理', '工作流配置',
 '工作流模板和步骤的配置管理，仅操作本地数据库。工作流执行时的网络行为取决于各步骤使用的工具。',
 0, 200, 'system', NOW(), 'system'),

('atomic_tool_config', '原子工具配置管理', '工作流配置',
 '原子工具的定义和配置管理，仅操作本地数据库。',
 0, 210, 'system', NOW(), 'system');

-- ---- 分组：系统配置 ----
INSERT INTO `gb_proxy_config` (`feature_key`, `feature_name`, `feature_group`, `feature_desc`, `proxy_applicable`, `proxy_warning`, `sort_order`, `create_by`, `create_time`, `update_by`)
VALUES
('storage_config_verify', '存储配置 - 连接验证', '系统配置',
 '新增或修改存储配置时验证连接是否可用（R2/S3/OSS/GitHub 等），会向对应存储服务发起测试请求。',
 1, NULL, 220, 'system', NOW(), 'system');

INSERT INTO `gb_proxy_config` (`feature_key`, `feature_name`, `feature_group`, `feature_desc`, `proxy_applicable`, `sort_order`, `create_by`, `create_time`, `update_by`)
VALUES
('site_template_mgmt', '站点模板管理', '系统配置',
 '站点模板的配置管理，仅操作本地数据库。',
 0, 230, 'system', NOW(), 'system'),

('code_manage_mgmt', '代码仓库配置管理', '系统配置',
 '代码仓库配置的管理，仅操作本地数据库（实际的代码部署由 CI/CD 流程完成）。',
 0, 240, 'system', NOW(), 'system'),

('search_index_mgmt', '搜索索引重建', '系统配置',
 '重建系统本地搜索索引，为服务器内部操作，不涉及外部网络调用。',
 0, 250, 'system', NOW(), 'system');

-- ----------------------------
-- 3. 菜单：代理配置（放在 基础配置 2001 下）
-- ----------------------------

-- 代理配置管理主菜单（menu_id=2155）
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (2155, '代理配置', 2001, 8, 'proxy', 'gamebox/config/proxy/index', NULL, 1, 0, 'C', '0', '0', 'gamebox:proxy:list', 'connection', 'admin', NOW(), '', NULL, '系统功能代理配置管理');

-- 权限按钮
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`)
VALUES (2156, '代理配置查询', 2155, 1, '#', 1, 0, 'F', '0', '0', 'gamebox:proxy:query', '#', 'admin', NOW());
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`)
VALUES (2157, '代理配置修改', 2155, 2, '#', 1, 0, 'F', '0', '0', 'gamebox:proxy:edit', '#', 'admin', NOW());
