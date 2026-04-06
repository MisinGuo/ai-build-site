/*
 文章板块配置功能数据库升级脚本
 
 版本: v3.1
 功能: 添加文章板块支持
 日期: 2026-01-08
 说明: 为分类表添加板块标识字段，并初始化默认板块数据
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 为分类表添加板块标识字段
-- ----------------------------
ALTER TABLE `gb_categories` 
ADD COLUMN `is_section` char(1) DEFAULT '0' COMMENT '是否为板块：0-否 1-是（顶级分类用作板块）' 
AFTER `category_type`;

-- 添加索引
ALTER TABLE `gb_categories` ADD INDEX `idx_category_type_section`(`category_type`, `is_section`);

-- ----------------------------
-- 2. 初始化现有的 article 类型顶级分类为板块
-- ----------------------------
UPDATE `gb_categories` 
SET `is_section` = '1' 
WHERE `category_type` = 'article' AND `parent_id` = 0 AND `del_flag` = '0';

-- ----------------------------
-- 3. 插入默认文章板块（如果没有的话）
-- ----------------------------
-- 先检查是否已存在攻略分类，如果存在则更新为板块
INSERT INTO `gb_categories` (`site_id`, `parent_id`, `category_type`, `is_section`, `name`, `slug`, `icon`, `description`, `sort_order`, `status`, `del_flag`, `create_by`, `create_time`)
VALUES
(1, 0, 'article', '1', '攻略板块', 'guide-section', '📖', '游戏攻略和教程板块', 1, '1', '0', 'admin', NOW()),
(1, 0, 'article', '1', '资讯板块', 'news-section', '📰', '最新游戏资讯板块', 2, '1', '0', 'admin', NOW()),
(1, 0, 'article', '1', '评测板块', 'review-section', '⭐', '游戏评测推荐板块', 3, '1', '0', 'admin', NOW()),
(1, 0, 'article', '1', '活动板块', 'event-section', '🎉', '活动和福利板块', 4, '1', '0', 'admin', NOW())
ON DUPLICATE KEY UPDATE 
    `is_section` = '1',
    `description` = VALUES(`description`),
    `update_time` = NOW();

-- ----------------------------
-- 4. 将现有的 article 分类关联到攻略板块（如果有的话）
-- ----------------------------
-- 查找现有的游戏分类ID（攻略类），将其设置为攻略板块的子分类
UPDATE `gb_categories` 
SET `parent_id` = (SELECT id FROM (SELECT id FROM `gb_categories` WHERE `slug` = 'guide-section' AND `del_flag` = '0' LIMIT 1) AS temp),
    `is_section` = '0'
WHERE `category_type` = 'article' 
  AND `parent_id` = 0 
  AND `slug` IN ('guide', 'newbie', 'advanced', 'strategy')
  AND `del_flag` = '0'
  AND `slug` != 'guide-section';

-- 将新闻类分类关联到资讯板块
UPDATE `gb_categories` 
SET `parent_id` = (SELECT id FROM (SELECT id FROM `gb_categories` WHERE `slug` = 'news-section' AND `del_flag` = '0' LIMIT 1) AS temp),
    `is_section` = '0'
WHERE `category_type` = 'article' 
  AND `parent_id` = 0 
  AND `slug` IN ('news', 'update', 'announcement')
  AND `del_flag` = '0'
  AND `slug` != 'news-section';

-- 将评测类分类关联到评测板块
UPDATE `gb_categories` 
SET `parent_id` = (SELECT id FROM (SELECT id FROM `gb_categories` WHERE `slug` = 'review-section' AND `del_flag` = '0' LIMIT 1) AS temp),
    `is_section` = '0'
WHERE `category_type` = 'article' 
  AND `parent_id` = 0 
  AND `slug` IN ('review', 'recommend')
  AND `del_flag` = '0'
  AND `slug` != 'review-section';

-- 将活动类分类关联到活动板块
UPDATE `gb_categories` 
SET `parent_id` = (SELECT id FROM (SELECT id FROM `gb_categories` WHERE `slug` = 'event-section' AND `del_flag` = '0' LIMIT 1) AS temp),
    `is_section` = '0'
WHERE `category_type` = 'article' 
  AND `parent_id` = 0 
  AND `slug` IN ('event', 'gift', 'welfare', 'recharge')
  AND `del_flag` = '0'
  AND `slug` != 'event-section';

-- ----------------------------
-- 5. 验证板块数据
-- ----------------------------
-- 查询板块列表
SELECT 
    id,
    site_id,
    parent_id,
    category_type,
    is_section,
    name,
    slug,
    icon,
    description,
    sort_order,
    status
FROM `gb_categories`
WHERE `category_type` = 'article' 
  AND `is_section` = '1' 
  AND `del_flag` = '0'
ORDER BY `sort_order` ASC;

-- 查询板块及其下的分类（树形结构）
SELECT 
    CASE 
        WHEN parent_id = 0 THEN CONCAT('【板块】', name)
        ELSE CONCAT('  └─ ', name)
    END as category_tree,
    id,
    parent_id,
    slug,
    icon,
    sort_order,
    status
FROM `gb_categories`
WHERE `category_type` = 'article' 
  AND `del_flag` = '0'
ORDER BY 
    CASE WHEN parent_id = 0 THEN id ELSE parent_id END,
    parent_id,
    sort_order;

-- 统计每个板块下的分类数量
SELECT 
    s.id as section_id,
    s.name as section_name,
    COUNT(c.id) as sub_category_count
FROM `gb_categories` s
LEFT JOIN `gb_categories` c ON c.parent_id = s.id AND c.del_flag = '0'
WHERE s.category_type = 'article' 
  AND s.is_section = '1' 
  AND s.del_flag = '0'
GROUP BY s.id, s.name
ORDER BY s.sort_order;

SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------
-- 执行说明
-- ----------------------------
-- 1. 本脚本为增量升级脚本，可重复执行
-- 2. 如果字段已存在，会跳过添加字段操作
-- 3. 使用 ON DUPLICATE KEY UPDATE 避免重复插入板块数据
-- 4. 自动将现有分类关联到对应板块下
-- 5. 执行后请检查验证查询结果
