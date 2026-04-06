-- ===========================================================================================
-- AI平台配置多站点支持升级脚本 v2.72
-- 功能：为AI平台配置添加多站点支持和分类支持，类似存储配置的实现
-- 日期：2025-12-31
-- ===========================================================================================

-- 1. 为 gb_ai_platforms 表添加 site_id 字段
ALTER TABLE `gb_ai_platforms` 
ADD COLUMN `site_id` bigint NULL DEFAULT 0 COMMENT '所属网站ID(0表示全局默认配置)' AFTER `id`;

-- 2. 为 gb_ai_platforms 表添加 category_id 字段
ALTER TABLE `gb_ai_platforms` 
ADD COLUMN `category_id` bigint NULL DEFAULT NULL COMMENT '分类ID' AFTER `available_models`;

-- 3. 添加索引以提升查询性能
ALTER TABLE `gb_ai_platforms` 
ADD INDEX `idx_site_id`(`site_id` ASC) USING BTREE,
ADD INDEX `idx_category_id`(`category_id` ASC) USING BTREE;

-- 4. 创建AI平台配置与网站关联表
DROP TABLE IF EXISTS `gb_site_platform_relations`;
CREATE TABLE `gb_site_platform_relations` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `site_id` bigint NOT NULL COMMENT '网站ID',
  `platform_id` bigint NOT NULL COMMENT 'AI平台配置ID',
  `relation_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'include' COMMENT '关联类型：include-正向关联，exclude-排除（用于排除默认配置）',
  `is_visible` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '是否可见：0-隐藏 1-显示',
  `priority` int NULL DEFAULT 100 COMMENT '优先级（数字越小优先级越高）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_site_platform_type`(`site_id` ASC, `platform_id` ASC, `relation_type` ASC) USING BTREE COMMENT '同一网站对同一配置的同一关联类型只能有一条记录',
  INDEX `idx_site_id`(`site_id` ASC) USING BTREE,
  INDEX `idx_platform_id`(`platform_id` ASC) USING BTREE,
  INDEX `idx_relation_type`(`relation_type` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'AI平台配置与网站关联表' ROW_FORMAT = DYNAMIC;

-- 5. 更新现有数据，将所有现有的AI平台配置标记为全局默认配置（site_id = 0）
UPDATE `gb_ai_platforms` SET `site_id` = 0 WHERE `site_id` IS NULL;

-- 6. 在分类类型表中添加AI平台分类类型
INSERT INTO `gb_category_types` (`value`, `label`, `tag_type`, `description`, `sort_order`, `status`, `is_system`, `create_time`)
SELECT 'ai_platform', 'AI平台分类', 'primary', '用于AI平台配置管理页面的分类', 8, '0', '1', NOW()
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `gb_category_types` WHERE `value` = 'ai_platform'
);

-- 7. 添加说明注释
-- 说明：
-- 1. site_id = 0 表示全局默认配置，所有网站默认可见
-- 2. site_id > 0 表示特定网站创建的配置，只对该网站可见（除非通过关联表共享给其他网站）
-- 3. category_id 关联到 gb_categories 表，用于对AI平台配置进行分类管理
--    - 可以创建新的分类类型 'ai_platform' 用于AI平台配置的分类
--    - 分类同样支持多站点，每个网站可以有自己的分类体系
-- 4. gb_site_platform_relations 表用于管理配置的跨站点共享和排除关系
--    - relation_type = 'include': 正向关联，将其他网站的配置共享到当前网站
--    - relation_type = 'exclude': 排除关联，排除全局默认配置在当前网站的显示
-- 5. is_visible 字段控制关联配置在目标网站的可见性
