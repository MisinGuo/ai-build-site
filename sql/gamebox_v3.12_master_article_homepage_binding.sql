-- 创建主文章主页绑定表
-- 说明：主文章与游戏/盒子主页的绑定，一个主文章只能绑定一个目标主页，与语言无关

DROP TABLE IF EXISTS `gb_master_article_homepage_binding`;
CREATE TABLE `gb_master_article_homepage_binding` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `master_article_id` bigint NOT NULL COMMENT '主文章ID',
  `game_id` bigint NULL DEFAULT NULL COMMENT '游戏ID（与game_box_id二选一）',
  `game_box_id` bigint NULL DEFAULT NULL COMMENT '游戏盒子ID（与game_id二选一）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_master_article`(`master_article_id` ASC) USING BTREE COMMENT '一个主文章只能绑定一个主页',
  UNIQUE INDEX `uk_game_homepage`(`game_id` ASC) USING BTREE COMMENT '一个游戏只能有一个主文章作为主页',
  UNIQUE INDEX `uk_gamebox_homepage`(`game_box_id` ASC) USING BTREE COMMENT '一个游戏盒子只能有一个主文章作为主页',
  INDEX `idx_master_article_id`(`master_article_id` ASC) USING BTREE,
  INDEX `idx_game_id`(`game_id` ASC) USING BTREE,
  INDEX `idx_game_box_id`(`game_box_id` ASC) USING BTREE,
  CONSTRAINT `chk_master_article_binding_target` CHECK (((`game_id` IS NOT NULL) AND (`game_box_id` IS NULL)) OR ((`game_id` IS NULL) AND (`game_box_id` IS NOT NULL)))
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '主文章主页绑定表' ROW_FORMAT = DYNAMIC;

-- 数据迁移：从旧的 gb_article_homepage_binding 迁移到新表
-- 注意：旧表是基于语言文章的，新表是基于主文章的
-- 策略：每个游戏/盒子只保留一个主文章绑定（选择最早创建的）
INSERT INTO `gb_master_article_homepage_binding` 
  (`master_article_id`, `game_id`, `game_box_id`, `create_by`, `create_time`)
SELECT 
  a.master_article_id,
  old.game_id,
  old.game_box_id,
  MAX(old.create_by) as create_by,
  MIN(old.create_time) as create_time
FROM `gb_article_homepage_binding` old
INNER JOIN `gb_articles` a ON old.article_id = a.id
WHERE a.master_article_id IS NOT NULL
GROUP BY a.master_article_id, old.game_id, old.game_box_id
ON DUPLICATE KEY UPDATE
  `update_time` = NOW();

-- 可选：备份旧表后删除（谨慎操作）
-- RENAME TABLE `gb_article_homepage_binding` TO `gb_article_homepage_binding_backup_20260113`;
