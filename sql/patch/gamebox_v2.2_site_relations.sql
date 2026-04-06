-- ============================================================
-- GameBox v2.2 补丁 - 网站内容多对多关联架构
-- 创建日期: 2025-12-27
-- 说明: 将一对一的网站归属关系改为多对多关联关系
--       内容可以同时在多个网站展示，解决网站变更时的分类混乱问题
-- ============================================================

-- ============================================================
-- 1. 创建网站-分类关联表
-- ============================================================
CREATE TABLE IF NOT EXISTS `gb_site_category_relations` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `site_id` bigint NOT NULL COMMENT '网站ID',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `is_visible` char(1) DEFAULT '1' COMMENT '是否可见：0-隐藏 1-可见',
  `is_editable` char(1) DEFAULT '0' COMMENT '是否可编辑：0-只读 1-可编辑',
  `sort_order` int DEFAULT 0 COMMENT '在该网站的排序',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_site_category` (`site_id`, `category_id`),
  KEY `idx_site_id` (`site_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_is_visible` (`is_visible`),
  CONSTRAINT `fk_site_category_site` FOREIGN KEY (`site_id`) REFERENCES `gb_sites` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_site_category_category` FOREIGN KEY (`category_id`) REFERENCES `gb_categories` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='网站-分类关联表';

-- ============================================================
-- 2. 创建网站-游戏盒子关联表
-- ============================================================
CREATE TABLE IF NOT EXISTS `gb_site_box_relations` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `site_id` bigint NOT NULL COMMENT '网站ID',
  `box_id` bigint NOT NULL COMMENT '游戏盒子ID',
  `is_visible` char(1) DEFAULT '1' COMMENT '是否可见：0-隐藏 1-可见',
  `is_featured` char(1) DEFAULT '0' COMMENT '是否推荐：0-否 1-是',
  `custom_name` varchar(100) DEFAULT NULL COMMENT '自定义名称（可覆盖原名称）',
  `custom_description` text DEFAULT NULL COMMENT '自定义描述',
  `sort_order` int DEFAULT 0 COMMENT '在该网站的排序',
  `view_count` int DEFAULT 0 COMMENT '在该网站的浏览量',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_site_box` (`site_id`, `box_id`),
  KEY `idx_site_id` (`site_id`),
  KEY `idx_box_id` (`box_id`),
  KEY `idx_is_visible` (`is_visible`),
  KEY `idx_is_featured` (`is_featured`),
  CONSTRAINT `fk_site_box_site` FOREIGN KEY (`site_id`) REFERENCES `gb_sites` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_site_box_box` FOREIGN KEY (`box_id`) REFERENCES `gb_game_boxes` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='网站-游戏盒子关联表';

-- ============================================================
-- 3. 创建网站-游戏关联表
-- ============================================================
CREATE TABLE IF NOT EXISTS `gb_site_game_relations` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `site_id` bigint NOT NULL COMMENT '网站ID',
  `game_id` bigint NOT NULL COMMENT '游戏ID',
  `is_visible` char(1) DEFAULT '1' COMMENT '是否可见：0-隐藏 1-可见',
  `is_featured` char(1) DEFAULT '0' COMMENT '是否推荐：0-否 1-是',
  `is_new` char(1) DEFAULT '0' COMMENT '是否新游：0-否 1-是',
  `custom_name` varchar(100) DEFAULT NULL COMMENT '自定义名称',
  `custom_description` text DEFAULT NULL COMMENT '自定义描述',
  `custom_download_url` varchar(500) DEFAULT NULL COMMENT '自定义下载链接',
  `sort_order` int DEFAULT 0 COMMENT '在该网站的排序',
  `view_count` int DEFAULT 0 COMMENT '在该网站的浏览量',
  `download_count` int DEFAULT 0 COMMENT '在该网站的下载量',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_site_game` (`site_id`, `game_id`),
  KEY `idx_site_id` (`site_id`),
  KEY `idx_game_id` (`game_id`),
  KEY `idx_is_visible` (`is_visible`),
  KEY `idx_is_featured` (`is_featured`),
  KEY `idx_is_new` (`is_new`),
  CONSTRAINT `fk_site_game_site` FOREIGN KEY (`site_id`) REFERENCES `gb_sites` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_site_game_game` FOREIGN KEY (`game_id`) REFERENCES `gb_games` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='网站-游戏关联表';

-- ============================================================
-- 4. 创建网站-文章关联表
-- ============================================================
CREATE TABLE IF NOT EXISTS `gb_site_article_relations` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `site_id` bigint NOT NULL COMMENT '网站ID',
  `article_id` bigint NOT NULL COMMENT '文章ID',
  `is_visible` char(1) DEFAULT '1' COMMENT '是否可见：0-隐藏 1-可见',
  `is_top` char(1) DEFAULT '0' COMMENT '是否置顶：0-否 1-是',
  `is_recommend` char(1) DEFAULT '0' COMMENT '是否推荐：0-否 1-是',
  `custom_title` varchar(255) DEFAULT NULL COMMENT '自定义标题',
  `sort_order` int DEFAULT 0 COMMENT '在该网站的排序',
  `view_count` int DEFAULT 0 COMMENT '在该网站的浏览量',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_site_article` (`site_id`, `article_id`),
  KEY `idx_site_id` (`site_id`),
  KEY `idx_article_id` (`article_id`),
  KEY `idx_is_visible` (`is_visible`),
  KEY `idx_is_top` (`is_top`),
  KEY `idx_is_recommend` (`is_recommend`),
  CONSTRAINT `fk_site_article_site` FOREIGN KEY (`site_id`) REFERENCES `gb_sites` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_site_article_article` FOREIGN KEY (`article_id`) REFERENCES `gb_articles` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='网站-文章关联表';

-- ============================================================
-- 5. 数据迁移 - 将现有数据迁移到新关联表
-- ============================================================

-- 5.1 迁移分类关联（为每个网站专属分类创建关联）
INSERT INTO gb_site_category_relations (site_id, category_id, is_visible, is_editable, sort_order, create_time, create_by)
SELECT 
    site_id,
    id as category_id,
    status as is_visible,
    '1' as is_editable,  -- 创建者网站可编辑
    sort_order,
    create_time,
    create_by
FROM gb_categories
WHERE site_id IS NOT NULL AND site_id > 0
ON DUPLICATE KEY UPDATE update_time = NOW();

-- 5.2 迁移游戏盒子关联
INSERT INTO gb_site_box_relations (site_id, box_id, is_visible, is_featured, sort_order, view_count, create_time, create_by)
SELECT 
    site_id,
    id as box_id,
    status as is_visible,
    '0' as is_featured,  -- gb_game_boxes表没有is_recommend字段，默认为'0'
    sort_order,
    game_count,  -- 使用game_count代替view_count
    create_time,
    create_by
FROM gb_game_boxes
WHERE site_id IS NOT NULL AND site_id > 0
ON DUPLICATE KEY UPDATE update_time = NOW();

-- 5.3 迁移游戏关联
INSERT INTO gb_site_game_relations (site_id, game_id, is_visible, is_featured, is_new, sort_order, view_count, download_count, create_time, create_by)
SELECT 
    site_id,
    id as game_id,
    status as is_visible,
    is_recommend as is_featured,
    is_new,
    sort_order,
    0 as view_count,  -- gb_games表没有view_count字段，默认为0
    download_count,
    create_time,
    create_by
FROM gb_games
WHERE site_id IS NOT NULL AND site_id > 0
ON DUPLICATE KEY UPDATE update_time = NOW();

-- 5.4 迁移文章关联
INSERT INTO gb_site_article_relations (site_id, article_id, is_visible, is_top, is_recommend, sort_order, view_count, create_time, create_by)
SELECT 
    site_id,
    id as article_id,
    CASE WHEN status = '1' THEN '1' ELSE '0' END as is_visible,
    is_top,
    is_recommend,
    sort_order,
    view_count,
    create_time,
    create_by
FROM gb_articles
WHERE site_id IS NOT NULL AND site_id > 0
ON DUPLICATE KEY UPDATE update_time = NOW();

-- ============================================================
-- 6. 验证数据迁移（查询统计）
-- ============================================================
SELECT 
    '分类关联' as entity_type,
    COUNT(*) as old_count,
    (SELECT COUNT(*) FROM gb_site_category_relations) as new_count
FROM gb_categories
WHERE site_id IS NOT NULL AND site_id > 0
UNION ALL
SELECT 
    '盒子关联' as entity_type,
    COUNT(*) as old_count,
    (SELECT COUNT(*) FROM gb_site_box_relations) as new_count
FROM gb_game_boxes
WHERE site_id IS NOT NULL AND site_id > 0
UNION ALL
SELECT 
    '游戏关联' as entity_type,
    COUNT(*) as old_count,
    (SELECT COUNT(*) FROM gb_site_game_relations) as new_count
FROM gb_games
WHERE site_id IS NOT NULL AND site_id > 0
UNION ALL
SELECT 
    '文章关联' as entity_type,
    COUNT(*) as old_count,
    (SELECT COUNT(*) FROM gb_site_article_relations) as new_count
FROM gb_articles
WHERE site_id IS NOT NULL AND site_id > 0;

-- ============================================================
-- 7. 说明
-- ============================================================
/*
重要提示：
1. site_id字段保留在原表中，但语义变更为"创建者网站"或"主管理网站"
2. 实际的网站可见性通过新的关联表控制
3. 原有的查询逻辑需要修改为JOIN关联表
4. 本补丁不删除原有字段，确保向后兼容

下一步工作：
1. 修改后端查询逻辑，使用关联表筛选数据
2. 添加管理网站关联的Service和Controller接口
3. 修改前端界面，支持多网站关联管理

回滚方案：
如果需要回滚，执行以下SQL：
DROP TABLE IF EXISTS gb_site_article_relations;
DROP TABLE IF EXISTS gb_site_game_relations;
DROP TABLE IF EXISTS gb_site_box_relations;
DROP TABLE IF EXISTS gb_site_category_relations;
*/
