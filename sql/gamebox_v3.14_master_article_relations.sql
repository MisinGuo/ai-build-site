-- 迁移文章关联关系到主文章架构
-- 说明：将文章-游戏、文章-游戏盒子的关联从语言文章改为基于主文章
-- 这样一个主文章的所有语言版本共享相同的游戏/盒子关联

-- ============================================
-- 1. 创建主文章-游戏关联表
-- ============================================
DROP TABLE IF EXISTS `gb_master_article_games`;
CREATE TABLE `gb_master_article_games` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `master_article_id` bigint NOT NULL COMMENT '主文章ID',
  `game_id` bigint NOT NULL COMMENT '游戏ID',
  `relation_source` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'manual' COMMENT '关联来源：manual-人工手动 ai-AI自动 import-批量导入 sync-同步',
  `relation_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'primary' COMMENT '关联类型：primary-主要关联 secondary-次要关联 related-相关推荐 mention-提及',
  `display_order` int NULL DEFAULT 1 COMMENT '显示顺序（用于模板变量排序，1=第一个）',
  `confidence_score` decimal(6, 4) NULL DEFAULT NULL COMMENT 'AI关联置信度（0.0000-1.0000）',
  `ai_platform_id` bigint NULL DEFAULT NULL COMMENT 'AI平台ID',
  `ai_reasoning` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'AI关联推理说明',
  `ai_metadata` json NULL COMMENT 'AI关联元数据（JSON格式）',
  `review_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '审核状态：0-待审核 1-已通过 2-已拒绝',
  `reviewed_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '审核人',
  `reviewed_at` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `review_notes` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '审核备注',
  `is_featured` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '是否推荐展示：0-否 1-是',
  `click_count` int NULL DEFAULT 0 COMMENT '点击次数',
  `last_used_at` datetime NULL DEFAULT NULL COMMENT '最后使用时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '删除标志：0-存在 2-删除',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_master_article_game`(`master_article_id` ASC, `game_id` ASC) USING BTREE COMMENT '一个主文章与一个游戏只能有一个关联',
  INDEX `idx_master_article_id`(`master_article_id` ASC) USING BTREE,
  INDEX `idx_game_id`(`game_id` ASC) USING BTREE,
  INDEX `idx_relation_source`(`relation_source` ASC) USING BTREE,
  INDEX `idx_relation_type`(`relation_type` ASC) USING BTREE,
  INDEX `idx_review_status`(`review_status` ASC) USING BTREE,
  INDEX `idx_confidence_score`(`confidence_score` DESC) USING BTREE,
  INDEX `idx_ai_platform`(`ai_platform_id` ASC) USING BTREE,
  INDEX `idx_display_order`(`master_article_id` ASC, `display_order` ASC) USING BTREE,
  INDEX `idx_del_flag`(`del_flag` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '主文章-游戏关联表' ROW_FORMAT = DYNAMIC;

-- ============================================
-- 2. 创建主文章-游戏盒子关联表
-- ============================================
DROP TABLE IF EXISTS `gb_master_article_game_boxes`;
CREATE TABLE `gb_master_article_game_boxes` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `master_article_id` bigint NOT NULL COMMENT '主文章ID',
  `game_box_id` bigint NOT NULL COMMENT '游戏盒子ID',
  `relation_source` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'manual' COMMENT '关联来源：manual-人工手动 ai-AI自动 import-批量导入 sync-同步',
  `relation_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'primary' COMMENT '关联类型：primary-主要关联 secondary-次要关联 related-相关推荐 mention-提及',
  `display_order` int NULL DEFAULT 1 COMMENT '显示顺序（用于模板变量排序，1=第一个）',
  `confidence_score` decimal(6, 4) NULL DEFAULT NULL COMMENT 'AI关联置信度（0.0000-1.0000）',
  `ai_platform_id` bigint NULL DEFAULT NULL COMMENT 'AI平台ID',
  `ai_reasoning` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'AI关联推理说明',
  `ai_metadata` json NULL COMMENT 'AI关联元数据（JSON格式）',
  `review_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '审核状态：0-待审核 1-已通过 2-已拒绝',
  `reviewed_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '审核人',
  `reviewed_at` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `review_notes` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '审核备注',
  `is_featured` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '是否推荐展示：0-否 1-是',
  `click_count` int NULL DEFAULT 0 COMMENT '点击次数',
  `last_used_at` datetime NULL DEFAULT NULL COMMENT '最后使用时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '删除标志：0-存在 2-删除',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_master_article_gamebox`(`master_article_id` ASC, `game_box_id` ASC) USING BTREE COMMENT '一个主文章与一个游戏盒子只能有一个关联',
  INDEX `idx_master_article_id`(`master_article_id` ASC) USING BTREE,
  INDEX `idx_game_box_id`(`game_box_id` ASC) USING BTREE,
  INDEX `idx_relation_source`(`relation_source` ASC) USING BTREE,
  INDEX `idx_relation_type`(`relation_type` ASC) USING BTREE,
  INDEX `idx_review_status`(`review_status` ASC) USING BTREE,
  INDEX `idx_confidence_score`(`confidence_score` DESC) USING BTREE,
  INDEX `idx_ai_platform`(`ai_platform_id` ASC) USING BTREE,
  INDEX `idx_display_order`(`master_article_id` ASC, `display_order` ASC) USING BTREE,
  INDEX `idx_del_flag`(`del_flag` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '主文章-游戏盒子关联表' ROW_FORMAT = DYNAMIC;

-- ============================================
-- 3. 数据迁移：从旧表迁移到新表
-- ============================================

-- 迁移游戏关联
-- 策略：每个主文章+游戏的组合只保留一条记录（选择最早创建的，保留最高的display_order）
INSERT INTO `gb_master_article_games` 
  (`master_article_id`, `game_id`, `relation_source`, `relation_type`, `display_order`, 
   `confidence_score`, `ai_platform_id`, `ai_reasoning`, `ai_metadata`, `review_status`, 
   `reviewed_by`, `reviewed_at`, `review_notes`, `is_featured`, `click_count`, 
   `last_used_at`, `del_flag`, `create_by`, `create_time`)
SELECT 
  a.master_article_id,
  old.game_id,
  MAX(old.relation_source) as relation_source,
  MAX(old.relation_type) as relation_type,
  MIN(old.display_order) as display_order,
  MAX(old.confidence_score) as confidence_score,
  MAX(old.ai_platform_id) as ai_platform_id,
  MAX(old.ai_reasoning) as ai_reasoning,
  MAX(old.ai_metadata) as ai_metadata,
  MAX(old.review_status) as review_status,
  MAX(old.reviewed_by) as reviewed_by,
  MAX(old.reviewed_at) as reviewed_at,
  MAX(old.review_notes) as review_notes,
  MAX(old.is_featured) as is_featured,
  SUM(old.click_count) as click_count,
  MAX(old.last_used_at) as last_used_at,
  MIN(old.del_flag) as del_flag,
  MAX(old.create_by) as create_by,
  MIN(old.create_time) as create_time
FROM `gb_article_games` old
INNER JOIN `gb_articles` a ON old.article_id = a.id
WHERE a.master_article_id IS NOT NULL
  AND old.del_flag = '0'
GROUP BY a.master_article_id, old.game_id
ON DUPLICATE KEY UPDATE
  `update_time` = NOW();

-- 迁移游戏盒子关联
-- 策略：每个主文章+游戏盒子的组合只保留一条记录（选择最早创建的，保留最高的display_order）
INSERT INTO `gb_master_article_game_boxes` 
  (`master_article_id`, `game_box_id`, `relation_source`, `relation_type`, `display_order`, 
   `confidence_score`, `ai_platform_id`, `ai_reasoning`, `ai_metadata`, `review_status`, 
   `reviewed_by`, `reviewed_at`, `review_notes`, `is_featured`, `click_count`, 
   `last_used_at`, `del_flag`, `create_by`, `create_time`)
SELECT 
  a.master_article_id,
  old.game_box_id,
  MAX(old.relation_source) as relation_source,
  MAX(old.relation_type) as relation_type,
  MIN(old.display_order) as display_order,
  MAX(old.confidence_score) as confidence_score,
  MAX(old.ai_platform_id) as ai_platform_id,
  MAX(old.ai_reasoning) as ai_reasoning,
  MAX(old.ai_metadata) as ai_metadata,
  MAX(old.review_status) as review_status,
  MAX(old.reviewed_by) as reviewed_by,
  MAX(old.reviewed_at) as reviewed_at,
  MAX(old.review_notes) as review_notes,
  MAX(old.is_featured) as is_featured,
  SUM(old.click_count) as click_count,
  MAX(old.last_used_at) as last_used_at,
  MIN(old.del_flag) as del_flag,
  MAX(old.create_by) as create_by,
  MIN(old.create_time) as create_time
FROM `gb_article_game_boxes` old
INNER JOIN `gb_articles` a ON old.article_id = a.id
WHERE a.master_article_id IS NOT NULL
  AND old.del_flag = '0'
GROUP BY a.master_article_id, old.game_box_id
ON DUPLICATE KEY UPDATE
  `update_time` = NOW();

-- ============================================
-- 4. 标记旧表为废弃（可选）
-- ============================================
-- 在旧表中添加 _deprecated 字段已经存在，值为 '1' 表示废弃
-- UPDATE `gb_article_games` SET `_deprecated` = '1';
-- UPDATE `gb_article_game_boxes` SET `_deprecated` = '1';

-- ============================================
-- 5. 备份旧表（谨慎操作，建议手动执行）
-- ============================================
-- RENAME TABLE `gb_article_games` TO `gb_article_games_backup_20260113`;
-- RENAME TABLE `gb_article_game_boxes` TO `gb_article_game_boxes_backup_20260113`;
-- RENAME TABLE `gb_article_homepage_binding` TO `gb_article_homepage_binding_backup_20260113`;
