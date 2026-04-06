-- 文章模板变量关联表设计
-- 采用多对多关联表方案，支持 ES 搜索和高效统计分析
-- 注意：一篇文章可以关联多个游戏盒子和多个游戏，但只能属于一个网站（site_id 保留在文章表中）

-- 1. 文章-游戏盒子关联表
CREATE TABLE IF NOT EXISTS `gb_article_game_boxes` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `article_id` bigint NOT NULL COMMENT '文章ID',
  `game_box_id` bigint NOT NULL COMMENT '游戏盒子ID',
  `display_order` int DEFAULT 0 COMMENT '显示顺序（用于模板变量排序，1=第一个）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_article_box` (`article_id`, `game_box_id`),
  KEY `idx_article_id` (`article_id`),
  KEY `idx_game_box_id` (`game_box_id`),
  KEY `idx_display_order` (`display_order`),
  CONSTRAINT `fk_article_box_article` FOREIGN KEY (`article_id`) REFERENCES `gb_articles` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_article_box_gamebox` FOREIGN KEY (`game_box_id`) REFERENCES `gb_game_boxes` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章-游戏盒子关联表';

-- 2. 文章-游戏关联表
CREATE TABLE IF NOT EXISTS `gb_article_games` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `article_id` bigint NOT NULL COMMENT '文章ID',
  `game_id` bigint NOT NULL COMMENT '游戏ID',
  `display_order` int DEFAULT 0 COMMENT '显示顺序（用于模板变量排序，1=第一个）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_article_game` (`article_id`, `game_id`),
  KEY `idx_article_id` (`article_id`),
  KEY `idx_game_id` (`game_id`),
  KEY `idx_display_order` (`display_order`),
  CONSTRAINT `fk_article_game_article` FOREIGN KEY (`article_id`) REFERENCES `gb_articles` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_article_game_game` FOREIGN KEY (`game_id`) REFERENCES `gb_games` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章-游戏关联表';

-- 3. 如果之前创建了 game_box_ids 和 game_ids 字段，删除它们
-- ALTER TABLE `gb_articles` DROP COLUMN IF EXISTS `game_box_ids`;
-- ALTER TABLE `gb_articles` DROP COLUMN IF EXISTS `game_ids`;

-- 注释：
-- 1. display_order 字段确保模板变量的顺序（{{game1}} 对应 display_order=1）
-- 2. ON DELETE CASCADE 确保文章删除时自动清理关联关系
-- 3. UNIQUE KEY 防止重复关联
-- 4. 外键约束保证数据完整性
