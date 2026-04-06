-- ===========================================================================================
-- 提示词模板配置多站点支持升级脚本 v2.73
-- 功能：为提示词模板配置添加多站点支持和分类支持，类似存储配置的实现
-- 日期：2025-12-31
-- ===========================================================================================

-- 1. 为 gb_prompt_templates 表添加 category_id 字段（site_id字段已存在）
ALTER TABLE `gb_prompt_templates` 
ADD COLUMN `category_id` bigint NULL DEFAULT NULL COMMENT '分类ID' AFTER `site_id`;

-- 2. 添加索引以提升查询性能
ALTER TABLE `gb_prompt_templates` 
-- ADD INDEX `idx_site_id`(`site_id` ASC) USING BTREE,
-- 已经存在
ADD INDEX `idx_category_id`(`category_id` ASC) USING BTREE;

-- 3. 创建提示词模板配置与网站关联表
DROP TABLE IF EXISTS `gb_site_template_relations`;
CREATE TABLE `gb_site_template_relations` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `site_id` bigint NOT NULL COMMENT '网站ID',
  `template_id` bigint NOT NULL COMMENT '提示词模板配置ID',
  `relation_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'include' COMMENT '关联类型：include-正向关联，exclude-排除（用于排除默认配置）',
  `is_visible` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '是否可见：0-隐藏 1-显示',
  `priority` int NULL DEFAULT 100 COMMENT '优先级（数字越小优先级越高）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_site_template_type`(`site_id` ASC, `template_id` ASC, `relation_type` ASC) USING BTREE COMMENT '同一网站对同一配置的同一关联类型只能有一条记录',
  INDEX `idx_site_id`(`site_id` ASC) USING BTREE,
  INDEX `idx_template_id`(`template_id` ASC) USING BTREE,
  INDEX `idx_relation_type`(`relation_type` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '提示词模板配置与网站关联表' ROW_FORMAT = DYNAMIC;

-- 4. 更新现有数据，将所有现有的提示词模板配置标记为全局默认配置（site_id = 0）
UPDATE `gb_prompt_templates` SET `site_id` = 0 WHERE `site_id` IS NULL OR `site_id` = '';

-- 5. 在分类类型表中添加提示词模板分类类型
INSERT INTO `gb_category_types` (`value`, `label`, `tag_type`, `description`, `sort_order`, `status`, `is_system`, `create_time`)
SELECT 'prompt_template', '提示词模板分类', 'warning', '用于提示词模板配置管理页面的分类', 9, '0', '1', NOW()
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `gb_category_types` WHERE `value` = 'prompt_template'
);

-- 6. 添加说明注释
-- 说明：
-- 1. site_id = 0 表示全局默认配置，所有网站默认可见
-- 2. site_id > 0 表示特定网站创建的配置，只对该网站可见（除非通过关联表共享给其他网站）
-- 3. category_id 关联到 gb_categories 表，用于对提示词模板配置进行分类管理
--    - 可以创建新的分类类型 'prompt_template' 用于提示词模板配置的分类
--    - 分类同样支持多站点，每个网站可以有自己的分类体系
-- 4. gb_site_template_relations 表用于管理配置的跨站点共享和排除关系
--    - relation_type = 'include': 正向关联，将其他网站的配置共享到当前网站
--    - relation_type = 'exclude': 排除关联，排除全局默认配置在当前网站的显示
-- 5. is_visible 字段控制关联配置在目标网站的可见性
