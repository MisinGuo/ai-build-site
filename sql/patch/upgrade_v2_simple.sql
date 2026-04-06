-- -- =============================================================================
-- -- 文章关联表 v2.0 升级脚本（精简版，直接执行）
-- -- 升级日期: 2025-12-24
-- -- 说明: 仅添加不存在的字段，如果字段已存在会报错但不影响整体
-- -- =============================================================================

-- SET NAMES utf8mb4;

-- -- =============================================================================
-- -- 第一步：备份数据
-- -- =============================================================================
-- DROP TABLE IF EXISTS `gb_article_games_backup_v1`;
-- CREATE TABLE `gb_article_games_backup_v1` AS SELECT * FROM `gb_article_games`;

-- DROP TABLE IF EXISTS `gb_article_game_boxes_backup_v1`;
-- CREATE TABLE `gb_article_game_boxes_backup_v1` AS SELECT * FROM `gb_article_game_boxes`;

-- SELECT '✓ 数据备份完成' AS status;

-- -- =============================================================================
-- -- 第二步：升级 gb_article_games 表（逐个添加字段）
-- -- =============================================================================

-- -- 如果字段已存在会报错，但可以继续执行后面的语句
-- ALTER TABLE `gb_article_games` ADD COLUMN `relation_source` varchar(20) DEFAULT 'manual' COMMENT '关联来源' AFTER `game_id`;
-- ALTER TABLE `gb_article_games` ADD COLUMN `relation_type` varchar(20) DEFAULT 'primary' COMMENT '关联类型' AFTER `relation_source`;
-- ALTER TABLE `gb_article_games` ADD COLUMN `confidence_score` decimal(6,4) DEFAULT NULL COMMENT 'AI置信度' AFTER `display_order`;
-- ALTER TABLE `gb_article_games` ADD COLUMN `ai_platform_id` bigint DEFAULT NULL COMMENT 'AI平台ID' AFTER `confidence_score`;
-- ALTER TABLE `gb_article_games` ADD COLUMN `ai_reasoning` text COMMENT 'AI推理说明' AFTER `ai_platform_id`;
-- ALTER TABLE `gb_article_games` ADD COLUMN `ai_metadata` json DEFAULT NULL COMMENT 'AI元数据' AFTER `ai_reasoning`;
-- ALTER TABLE `gb_article_games` ADD COLUMN `review_status` char(1) DEFAULT '1' COMMENT '审核状态' AFTER `ai_metadata`;
-- ALTER TABLE `gb_article_games` ADD COLUMN `reviewed_by` varchar(64) DEFAULT NULL COMMENT '审核人' AFTER `review_status`;
-- ALTER TABLE `gb_article_games` ADD COLUMN `reviewed_at` datetime DEFAULT NULL COMMENT '审核时间' AFTER `reviewed_by`;
-- ALTER TABLE `gb_article_games` ADD COLUMN `review_notes` varchar(500) DEFAULT NULL COMMENT '审核备注' AFTER `reviewed_at`;
-- ALTER TABLE `gb_article_games` ADD COLUMN `is_featured` char(1) DEFAULT '0' COMMENT '是否推荐' AFTER `review_notes`;
-- ALTER TABLE `gb_article_games` ADD COLUMN `click_count` int DEFAULT 0 COMMENT '点击次数' AFTER `is_featured`;
-- ALTER TABLE `gb_article_games` ADD COLUMN `last_used_at` datetime DEFAULT NULL COMMENT '最后使用时间' AFTER `click_count`;

-- -- 添加索引
-- ALTER TABLE `gb_article_games` ADD INDEX `idx_relation_source` (`relation_source`);
-- ALTER TABLE `gb_article_games` ADD INDEX `idx_relation_type` (`relation_type`);
-- ALTER TABLE `gb_article_games` ADD INDEX `idx_review_status` (`review_status`);
-- ALTER TABLE `gb_article_games` ADD INDEX `idx_confidence_score` (`confidence_score` DESC);
-- ALTER TABLE `gb_article_games` ADD INDEX `idx_ai_platform` (`ai_platform_id`);

-- SELECT '✓ gb_article_games 表升级完成' AS status;

-- -- =============================================================================
-- -- 第三步：升级 gb_article_game_boxes 表
-- -- =============================================================================

-- ALTER TABLE `gb_article_game_boxes` ADD COLUMN `relation_source` varchar(20) DEFAULT 'manual' COMMENT '关联来源' AFTER `game_box_id`;
-- ALTER TABLE `gb_article_game_boxes` ADD COLUMN `relation_type` varchar(20) DEFAULT 'primary' COMMENT '关联类型' AFTER `relation_source`;
-- ALTER TABLE `gb_article_game_boxes` ADD COLUMN `confidence_score` decimal(6,4) DEFAULT NULL COMMENT 'AI置信度' AFTER `display_order`;
-- ALTER TABLE `gb_article_game_boxes` ADD COLUMN `ai_platform_id` bigint DEFAULT NULL COMMENT 'AI平台ID' AFTER `confidence_score`;
-- ALTER TABLE `gb_article_game_boxes` ADD COLUMN `ai_reasoning` text COMMENT 'AI推理说明' AFTER `ai_platform_id`;
-- ALTER TABLE `gb_article_game_boxes` ADD COLUMN `ai_metadata` json DEFAULT NULL COMMENT 'AI元数据' AFTER `ai_reasoning`;
-- ALTER TABLE `gb_article_game_boxes` ADD COLUMN `review_status` char(1) DEFAULT '1' COMMENT '审核状态' AFTER `ai_metadata`;
-- ALTER TABLE `gb_article_game_boxes` ADD COLUMN `reviewed_by` varchar(64) DEFAULT NULL COMMENT '审核人' AFTER `review_status`;
-- ALTER TABLE `gb_article_game_boxes` ADD COLUMN `reviewed_at` datetime DEFAULT NULL COMMENT '审核时间' AFTER `reviewed_by`;
-- ALTER TABLE `gb_article_game_boxes` ADD COLUMN `review_notes` varchar(500) DEFAULT NULL COMMENT '审核备注' AFTER `reviewed_at`;
-- ALTER TABLE `gb_article_game_boxes` ADD COLUMN `is_featured` char(1) DEFAULT '0' COMMENT '是否推荐' AFTER `review_notes`;
-- ALTER TABLE `gb_article_game_boxes` ADD COLUMN `click_count` int DEFAULT 0 COMMENT '点击次数' AFTER `is_featured`;
-- ALTER TABLE `gb_article_game_boxes` ADD COLUMN `last_used_at` datetime DEFAULT NULL COMMENT '最后使用时间' AFTER `click_count`;

-- -- 添加索引
-- ALTER TABLE `gb_article_game_boxes` ADD INDEX `idx_relation_source` (`relation_source`);
-- ALTER TABLE `gb_article_game_boxes` ADD INDEX `idx_relation_type` (`relation_type`);
-- ALTER TABLE `gb_article_game_boxes` ADD INDEX `idx_review_status` (`review_status`);
-- ALTER TABLE `gb_article_game_boxes` ADD INDEX `idx_confidence_score` (`confidence_score` DESC);
-- ALTER TABLE `gb_article_game_boxes` ADD INDEX `idx_ai_platform` (`ai_platform_id`);

-- SELECT '✓ gb_article_game_boxes 表升级完成' AS status;

-- -- =============================================================================
-- -- 第四步：创建短剧关联表
-- -- =============================================================================

-- CREATE TABLE IF NOT EXISTS `gb_article_dramas` (
--   `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
--   `article_id` bigint NOT NULL COMMENT '文章ID',
--   `drama_id` bigint NOT NULL COMMENT '短剧ID',
--   `relation_source` varchar(20) DEFAULT 'manual' COMMENT '关联来源',
--   `relation_type` varchar(20) DEFAULT 'primary' COMMENT '关联类型',
--   `display_order` int DEFAULT 1 COMMENT '显示顺序',
--   `confidence_score` decimal(6,4) DEFAULT NULL COMMENT 'AI置信度',
--   `ai_platform_id` bigint DEFAULT NULL COMMENT 'AI平台ID',
--   `ai_reasoning` text COMMENT 'AI推理说明',
--   `ai_metadata` json DEFAULT NULL COMMENT 'AI元数据',
--   `review_status` char(1) DEFAULT '1' COMMENT '审核状态',
--   `reviewed_by` varchar(64) DEFAULT NULL COMMENT '审核人',
--   `reviewed_at` datetime DEFAULT NULL COMMENT '审核时间',
--   `review_notes` varchar(500) DEFAULT NULL COMMENT '审核备注',
--   `is_featured` char(1) DEFAULT '0' COMMENT '是否推荐',
--   `click_count` int DEFAULT 0 COMMENT '点击次数',
--   `last_used_at` datetime DEFAULT NULL COMMENT '最后使用时间',
--   `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
--   `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
--   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
--   `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
--   `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
--   `remark` varchar(500) DEFAULT NULL COMMENT '备注',
--   PRIMARY KEY (`id`),
--   UNIQUE KEY `uk_article_drama` (`article_id`, `drama_id`),
--   KEY `idx_article_id` (`article_id`),
--   KEY `idx_drama_id` (`drama_id`),
--   KEY `idx_relation_source` (`relation_source`),
--   KEY `idx_review_status` (`review_status`),
--   KEY `idx_del_flag` (`del_flag`)
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章-短剧关联表';

-- SELECT '✓ gb_article_dramas 表创建完成' AS status;

-- -- =============================================================================
-- -- 第五步：数据迁移（更新现有数据的默认值）
-- -- =============================================================================

-- UPDATE `gb_article_games` 
-- SET `relation_source` = 'manual', 
--     `relation_type` = 'primary', 
--     `review_status` = '1'
-- WHERE `relation_source` IS NULL OR `relation_source` = '';

-- UPDATE `gb_article_game_boxes` 
-- SET `relation_source` = 'manual', 
--     `relation_type` = 'primary', 
--     `review_status` = '1'
-- WHERE `relation_source` IS NULL OR `relation_source` = '';

-- SELECT '✓ 数据迁移完成' AS status;


-- =============================================================================
-- 第六步：创建管理视图
-- =============================================================================

CREATE OR REPLACE VIEW `v_ai_game_relations_pending` AS
SELECT ag.id, ag.article_id, a.title AS article_title, ag.game_id, g.name AS game_name,
       ag.confidence_score, ag.ai_reasoning, ag.create_time
FROM gb_article_games ag
LEFT JOIN gb_articles a ON ag.article_id = a.id
LEFT JOIN gb_games g ON ag.game_id = g.id
WHERE ag.relation_source = 'ai' AND ag.review_status = '0';

CREATE OR REPLACE VIEW `v_ai_gamebox_relations_pending` AS
SELECT agb.id, agb.article_id, a.title AS article_title, agb.game_box_id, gb.name AS gamebox_name,
       agb.confidence_score, agb.ai_reasoning, agb.create_time
FROM gb_article_game_boxes agb
LEFT JOIN gb_articles a ON agb.article_id = a.id
LEFT JOIN gb_game_boxes gb ON agb.game_box_id = gb.id
WHERE agb.relation_source = 'ai' AND agb.review_status = '0';

CREATE OR REPLACE VIEW `v_ai_drama_relations_pending` AS
SELECT ad.id, ad.article_id, a.title AS article_title, ad.drama_id, d.name AS drama_name,
       ad.confidence_score, ad.ai_reasoning, ad.create_time
FROM gb_article_dramas ad
LEFT JOIN gb_articles a ON ad.article_id = a.id
LEFT JOIN gb_dramas d ON ad.drama_id = d.id
WHERE ad.relation_source = 'ai' AND ad.review_status = '0';

CREATE OR REPLACE VIEW `v_ai_relations_statistics` AS
SELECT 'game' AS product_type, COUNT(*) AS total,
       SUM(CASE WHEN review_status='0' THEN 1 ELSE 0 END) AS pending,
       SUM(CASE WHEN review_status='1' THEN 1 ELSE 0 END) AS approved,
       AVG(confidence_score) AS avg_confidence
FROM gb_article_games WHERE relation_source='ai'
UNION ALL
SELECT 'gamebox', COUNT(*), 
       SUM(CASE WHEN review_status='0' THEN 1 ELSE 0 END),
       SUM(CASE WHEN review_status='1' THEN 1 ELSE 0 END),
       AVG(confidence_score)
FROM gb_article_game_boxes WHERE relation_source='ai'
UNION ALL
SELECT 'drama', COUNT(*),
       SUM(CASE WHEN review_status='0' THEN 1 ELSE 0 END),
       SUM(CASE WHEN review_status='1' THEN 1 ELSE 0 END),
       AVG(confidence_score)
FROM gb_article_dramas WHERE relation_source='ai';

SELECT '✓ 管理视图创建完成' AS status;
-- -- =============================================================================
-- -- 升级完成
-- -- =============================================================================
-- SELECT '
-- ========================================
-- ✓ 升级完成！
-- ========================================
-- 已升级：
--   - gb_article_games
--   - gb_article_game_boxes
--   - gb_article_dramas (新增)

-- 已创建：
--   - 4个管理视图
--   - 2个备份表

-- 注意事项：
--   - 如有字段已存在的错误可忽略
--   - 重启应用服务后生效
--   - 备份表可手动删除
-- ========================================
-- ' AS result;
