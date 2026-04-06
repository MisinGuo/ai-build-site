-- ===================================================================
-- 标题池多站点支持升级脚本
-- 版本：v2.76
-- 日期：2026-01-01
-- 说明：为标题池添加多站点支持，类似AI平台配置的实现
-- ===================================================================

-- 1. 为 gb_article_title_pool 表添加 site_id 和 category_id 字段
ALTER TABLE `gb_article_title_pool`
ADD COLUMN `site_id` bigint(20) NULL DEFAULT 0 COMMENT '所属网站ID(0表示全局默认配置)' AFTER `id`,
ADD COLUMN `category_id` bigint(20) NULL COMMENT '分类ID' AFTER `site_id`;

-- 2. 为 site_id 和 category_id 添加索引
ALTER TABLE `gb_article_title_pool`
ADD INDEX `idx_site_id` (`site_id`),
ADD INDEX `idx_category_id` (`category_id`);

-- 3. 更新现有数据：将现有数据设置为默认配置（site_id=0）
UPDATE `gb_article_title_pool` SET `site_id` = 0 WHERE `site_id` IS NULL;

-- 4. 在分类类型表中添加标题池分类类型
INSERT INTO `gb_category_types` (`value`, `label`, `tag_type`, `description`, `sort_order`, `status`, `is_system`, `create_time`)
SELECT 'title_pool', '标题池分类', 'primary', '用于标题池管理页面的分类', 10, '0', '1', NOW()
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `gb_category_types` WHERE `value` = 'title_pool'
);

-- 5. 创建标题池分类（title_pool类型）
INSERT INTO `gb_categories` (`site_id`, `category_type`, `name`, `slug`, `icon`, `sort_order`, `status`, `create_time`, `create_by`)
VALUES
(0, 'title_pool', '游戏攻略', 'game-guide', '🎮', 1, '1', NOW(), 'admin'),
(0, 'title_pool', '新闻资讯', 'news', '📰', 2, '1', NOW(), 'admin'),
(0, 'title_pool', '评测推荐', 'review', '⭐', 3, '1', NOW(), 'admin'),
(0, 'title_pool', 'SEO优化', 'seo', '🔍', 4, '1', NOW(), 'admin'),
(0, 'title_pool', '其他', 'other', '📝', 99, '1', NOW(), 'admin')
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- 6. 创建网站-标题池关联表
CREATE TABLE IF NOT EXISTS `gb_site_title_pool_relations` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `site_id` bigint NOT NULL COMMENT '网站ID',
  `title_pool_id` bigint NOT NULL COMMENT '标题池ID',
  `relation_type` varchar(20) NOT NULL DEFAULT 'include' COMMENT '关联类型：include-正向关联 exclude-排除默认配置',
  `is_visible` char(1) DEFAULT '1' COMMENT '是否可见：0-否 1-是',
  `is_editable` char(1) DEFAULT '1' COMMENT '是否可编辑：0-否 1-是',
  `sort_order` int DEFAULT 0 COMMENT '排序',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_site_title_type` (`site_id`, `title_pool_id`, `relation_type`) USING BTREE,
  KEY `idx_site_id` (`site_id`) USING BTREE,
  KEY `idx_title_pool_id` (`title_pool_id`) USING BTREE,
  KEY `idx_relation_type` (`relation_type`) USING BTREE,
  KEY `idx_site_type` (`site_id`, `relation_type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='网站-标题池关联表';

-- 7. 验证修改结果
SELECT 
    COLUMN_NAME, 
    DATA_TYPE, 
    COLUMN_DEFAULT, 
    IS_NULLABLE, 
    COLUMN_COMMENT
FROM 
    INFORMATION_SCHEMA.COLUMNS
WHERE 
    TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'gb_article_title_pool'
    AND COLUMN_NAME IN ('site_id', 'category_id');

-- 8. 查看分类类型是否添加成功
SELECT * FROM gb_category_types WHERE `value` = 'title_pool';

-- 9. 查看新增的分类
SELECT id, site_id, category_type, name, icon, sort_order, status
FROM gb_categories
WHERE category_type = 'title_pool'
ORDER BY sort_order;

-- 10. 验证 gb_site_title_pool_relations 表是否创建成功
SHOW CREATE TABLE gb_site_title_pool_relations;

-- ===================================================================
-- 完成
-- ===================================================================
