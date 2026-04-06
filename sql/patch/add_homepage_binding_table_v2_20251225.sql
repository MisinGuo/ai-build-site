-- 创建独立的主页绑定表 v2.0
-- 日期: 2025-12-25
-- 说明: 将主页绑定功能从 gb_article_games/gb_article_game_boxes 表中分离出来，使用独立表管理

-- ========================================
-- 创建文章主页绑定表
-- ========================================
CREATE TABLE IF NOT EXISTS `gb_article_homepage_binding` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `article_id` BIGINT(20) NOT NULL COMMENT '文章ID',
  `game_id` BIGINT(20) DEFAULT NULL COMMENT '游戏ID（与game_box_id二选一）',
  `game_box_id` BIGINT(20) DEFAULT NULL COMMENT '游戏盒子ID（与game_id二选一）',
  `homepage_locale` VARCHAR(10) NOT NULL COMMENT '主页语言代码（如：en, zh-CN, ja）',
  `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  
  -- 一篇文章在同一语言下只能绑定一个游戏/盒子作为主页
  UNIQUE KEY `uk_article_homepage` (`article_id`, `homepage_locale`),
  
  -- 一个游戏在同一语言下只能有一个主页文章
  UNIQUE KEY `uk_game_homepage` (`game_id`, `homepage_locale`),
  
  -- 一个游戏盒子在同一语言下只能有一个主页文章
  UNIQUE KEY `uk_gamebox_homepage` (`game_box_id`, `homepage_locale`),
  
  -- 外键索引（用于查询优化）
  KEY `idx_article_id` (`article_id`),
  KEY `idx_game_id` (`game_id`),
  KEY `idx_game_box_id` (`game_box_id`),
  KEY `idx_homepage_locale` (`homepage_locale`),
  
  -- 约束：game_id 和 game_box_id 必须有且仅有一个不为 NULL
  CONSTRAINT `chk_binding_target` CHECK (
    (`game_id` IS NOT NULL AND `game_box_id` IS NULL) OR
    (`game_id` IS NULL AND `game_box_id` IS NOT NULL)
  )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章主页绑定表';

-- ========================================
-- 数据示例和说明
-- ========================================
-- 示例1: 文章2 绑定为 游戏2 的英文主页
-- INSERT INTO gb_article_homepage_binding (article_id, game_id, game_box_id, homepage_locale, create_by, create_time)
-- VALUES (2, 2, NULL, 'en', 'admin', NOW());

-- 示例2: 文章5 绑定为 游戏2 的中文主页
-- INSERT INTO gb_article_homepage_binding (article_id, game_id, game_box_id, homepage_locale, create_by, create_time)
-- VALUES (5, 2, NULL, 'zh-CN', 'admin', NOW());

-- 示例3: 文章10 绑定为 游戏盒子3 的日文主页
-- INSERT INTO gb_article_homepage_binding (article_id, game_id, game_box_id, homepage_locale, create_by, create_time)
-- VALUES (10, NULL, 3, 'ja', 'admin', NOW());

-- ========================================
-- 重要说明
-- ========================================
-- 1. 主页绑定表 (gb_article_homepage_binding)
--    用途: 管理文章作为游戏/盒子主页的一对一绑定关系
--    特点: 每个游戏每种语言只有一个主页文章
--
-- 2. 普通关联表 (gb_article_games/gb_article_game_boxes)
--    用途: 管理文章提到/关联游戏的多对多关系
--    特点: 一篇文章可以关联多个游戏，一个游戏可以被多篇文章关联
--
-- 3. 两表完全独立，互不影响
--    - 同一篇文章可以同时：
--      a) 作为某个游戏的主页（在 gb_article_homepage_binding 中）
--      b) 关联/提到其他游戏（在 gb_article_games 中）
--    - 编辑文章关联只操作 gb_article_games
--    - 绑定主页功能只操作 gb_article_homepage_binding
