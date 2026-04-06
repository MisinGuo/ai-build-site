-- ==========================================
-- gamebox v3.66 站点模板 & 建站向导
-- 支持模板建站流程，为 gb_sites 增加建站状态追踪
-- ==========================================

-- ----------------------------
-- 1. 修改 gb_sites 表：增加建站状态和模板引用
-- ----------------------------
ALTER TABLE `gb_sites`
    ADD COLUMN `site_function_type` VARCHAR(20) NULL DEFAULT NULL COMMENT '站点功能类型：seo_site-自动获客站 landing_site-落地站' AFTER `site_type`,
    ADD COLUMN `setup_status` VARCHAR(20) NOT NULL DEFAULT 'created' COMMENT '建站状态：created-已创建 code_pulled-代码就绪 configured-已配置 deployed-已部署' AFTER `site_function_type`,
    ADD COLUMN `template_id` BIGINT NULL DEFAULT NULL COMMENT '关联模板ID（来自 gb_site_templates）' AFTER `setup_status`;

-- ----------------------------
-- 2. 创建站点模板表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `gb_site_templates` (
    `id`                    BIGINT          NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`                  VARCHAR(100)    NOT NULL COMMENT '模板名称',
    `site_function_type`    VARCHAR(20)     NOT NULL DEFAULT 'seo_site' COMMENT '适用站点类型：seo_site / landing_site',
    `industry`              VARCHAR(50)     NULL DEFAULT NULL COMMENT '所属行业（可选）',
    `description`           VARCHAR(500)    NULL DEFAULT NULL COMMENT '模板描述',
    `preview_image`         VARCHAR(500)    NULL DEFAULT NULL COMMENT '预览图 URL',
    `template_git_url`      VARCHAR(500)    NOT NULL COMMENT '模板 Git 仓库地址（用于 clone）',
    `template_git_branch`   VARCHAR(100)    NOT NULL DEFAULT 'main' COMMENT '模板 Git 分支',
    `entry_dir`             VARCHAR(200)    NULL DEFAULT NULL COMMENT '代码根目录（若模板仓库中有子目录则填写，否则留空）',
    `framework`             VARCHAR(50)     NULL DEFAULT 'nextjs' COMMENT '框架类型：nextjs / nuxt / react / vue 等',
    `env_template`          TEXT            NULL DEFAULT NULL COMMENT '环境变量模板（JSON 格式，key 为变量名，value 为默认值或占位符）',
    `sort_order`            INT             NOT NULL DEFAULT 0 COMMENT '排序',
    `status`                CHAR(1)         NOT NULL DEFAULT '1' COMMENT '状态：0-禁用 1-启用',
    `create_by`             VARCHAR(64)     NULL DEFAULT '' COMMENT '创建者',
    `create_time`           DATETIME        NULL DEFAULT NULL COMMENT '创建时间',
    `update_by`             VARCHAR(64)     NULL DEFAULT '' COMMENT '更新者',
    `update_time`           DATETIME        NULL DEFAULT NULL COMMENT '更新时间',
    `remark`                VARCHAR(500)    NULL DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='站点模板表：存放用于快速建站的代码模板信息';

-- ----------------------------
-- 3. 预置两个默认模板
--    模板 Git 地址请在管理后台根据实际仓库填写
-- ----------------------------
INSERT INTO `gb_site_templates`
    (`id`, `name`, `site_function_type`, `industry`, `description`, `preview_image`, `template_git_url`, `template_git_branch`, `framework`, `env_template`, `sort_order`, `status`, `create_by`, `create_time`)
VALUES
    (1, '游戏推广 SEO 站', 'seo_site', 'game',
     '适用于游戏推广的 SEO 获客站，内置多语言、游戏列表、游戏详情、攻略文章等功能，对接后端 API 自动展示数据。',
     NULL,
     'https://github.com/your-org/next-web-template.git',
     'main',
     'nextjs',
     '{"NEXT_PUBLIC_SITE_ID":"","NEXT_PUBLIC_API_URL":"","NEXT_PUBLIC_SITE_HOSTNAME":"","NEXT_PUBLIC_MAIN_SITE_URL":"","NEXT_PUBLIC_JUMP_DOMAIN":""}',
     1, '1', 'admin', NOW()),
    (2, '游戏落地站', 'landing_site', 'game',
     '适合游戏公司官网、产品落地页的品牌站，以资讯展示、产品介绍为主，对接后端 API 管理内容。',
     NULL,
     'https://github.com/your-org/next-web-sub-template.git',
     'main',
     'nextjs',
     '{"NEXT_PUBLIC_SITE_ID":"","NEXT_PUBLIC_API_URL":"","NEXT_PUBLIC_SITE_HOSTNAME":"","NEXT_PUBLIC_MAIN_SITE_URL":"","NEXT_PUBLIC_JUMP_DOMAIN":""}',
     2, '1', 'admin', NOW());

-- ----------------------------
-- 4. 菜单：站点模板管理
--    父菜单：基础配置 (menu_id=2001)，属于 游戏盒子 > 基础配置
--    menu_id=2140，排序第5（站点管理=1、分类管理=2、API平台=3、代码管理=4）
-- ----------------------------
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (2140, '站点模板管理', 2001, 5, 'site-templates', 'gamebox/config/site-templates/index', NULL, 1, 0, 'C', '0', '0', 'gamebox:siteTemplate:list', 'component', 'admin', NOW(), '', NULL, '站点模板管理菜单');

-- 子权限：查询、新增、修改、删除
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`)
VALUES (2141, '站点模板查询', 2140, 1, '#', 1, 0, 'F', '0', '0', 'gamebox:siteTemplate:query', '#', 'admin', NOW());
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`)
VALUES (2142, '站点模板新增', 2140, 2, '#', 1, 0, 'F', '0', '0', 'gamebox:siteTemplate:add', '#', 'admin', NOW());
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`)
VALUES (2143, '站点模板修改', 2140, 3, '#', 1, 0, 'F', '0', '0', 'gamebox:siteTemplate:edit', '#', 'admin', NOW());
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`)
VALUES (2144, '站点模板删除', 2140, 4, '#', 1, 0, 'F', '0', '0', 'gamebox:siteTemplate:remove', '#', 'admin', NOW());
