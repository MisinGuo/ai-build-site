-- =====================================================
-- 游戏盒子项目 v3.16 数据库升级脚本
-- 迁移网站-文章关联表到主文章模式
-- 执行日期：2026-01-13
-- =====================================================

USE `gamebox`;

-- 1. 备份旧表
CREATE TABLE IF NOT EXISTS gb_site_article_relations_backup_v316 AS
SELECT * FROM gb_site_article_relations;

-- 2. 删除旧的外键约束
ALTER TABLE `gb_site_article_relations` 
DROP FOREIGN KEY IF EXISTS `fk_site_article_article`;

-- 3. 重命名旧表（保留以便回滚）
RENAME TABLE `gb_site_article_relations` TO `gb_site_article_relations_old`;

-- 4. 创建新的主文章-网站关联表
CREATE TABLE `gb_site_master_article_relations` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `site_id` bigint NOT NULL COMMENT '网站ID',
  `master_article_id` bigint NOT NULL COMMENT '主文章ID',
  `relation_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'include' COMMENT '关联类型：include-正向关联 exclude-排除默认配置',
  `is_visible` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '是否可见：0-隐藏 1-可见',
  `is_top` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '是否置顶：0-否 1-是',
  `is_recommend` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '是否推荐：0-否 1-是',
  `custom_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '自定义标题',
  `sort_order` int NULL DEFAULT 0 COMMENT '在该网站的排序',
  `view_count` int NULL DEFAULT 0 COMMENT '在该网站的浏览量',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '备注',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_site_master_article_type`(`site_id` ASC, `master_article_id` ASC, `relation_type` ASC) USING BTREE,
  INDEX `idx_site_id`(`site_id` ASC) USING BTREE,
  INDEX `idx_master_article_id`(`master_article_id` ASC) USING BTREE,
  INDEX `idx_is_visible`(`is_visible` ASC) USING BTREE,
  INDEX `idx_is_top`(`is_top` ASC) USING BTREE,
  INDEX `idx_is_recommend`(`is_recommend` ASC) USING BTREE,
  INDEX `idx_relation_type`(`relation_type` ASC) USING BTREE,
  INDEX `idx_site_type`(`site_id` ASC, `relation_type` ASC) USING BTREE,
  CONSTRAINT `fk_site_master_article_article` FOREIGN KEY (`master_article_id`) REFERENCES `gb_master_articles` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_site_master_article_site` FOREIGN KEY (`site_id`) REFERENCES `gb_sites` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '网站-主文章关联表' ROW_FORMAT = DYNAMIC;

-- 5. 迁移数据：从语言版本文章关联转换为主文章关联
-- 使用 article_id 关联到 gb_articles，然后获取 master_article_id
-- 按 (site_id, master_article_id, relation_type) 分组，避免重复
INSERT INTO gb_site_master_article_relations (
  site_id,
  master_article_id,
  relation_type,
  is_visible,
  is_top,
  is_recommend,
  custom_title,
  sort_order,
  view_count,
  remark,
  create_by,
  create_time,
  update_by,
  update_time
)
SELECT 
  old.site_id,
  a.master_article_id,
  old.relation_type,
  MAX(old.is_visible) as is_visible,
  MAX(old.is_top) as is_top,
  MAX(old.is_recommend) as is_recommend,
  MAX(old.custom_title) as custom_title,
  MIN(old.sort_order) as sort_order,
  SUM(old.view_count) as view_count,
  MAX(old.remark) as remark,
  MAX(old.create_by) as create_by,
  MIN(old.create_time) as create_time,
  MAX(old.update_by) as update_by,
  MAX(old.update_time) as update_time
FROM gb_site_article_relations_old old
INNER JOIN gb_articles a ON old.article_id = a.id
WHERE a.master_article_id IS NOT NULL
GROUP BY old.site_id, a.master_article_id, old.relation_type;

-- 6. 统计迁移结果
SELECT 
  '旧表记录数' as type,
  COUNT(*) as count
FROM gb_site_article_relations_old
UNION ALL
SELECT 
  '新表记录数' as type,
  COUNT(*) as count
FROM gb_site_master_article_relations
UNION ALL
SELECT 
  '有效迁移数（有master_article_id）' as type,
  COUNT(*) as count
FROM gb_site_article_relations_old old
INNER JOIN gb_articles a ON old.article_id = a.id
WHERE a.master_article_id IS NOT NULL;

-- =====================================================
-- 说明：
-- 1. 已备份旧表为 gb_site_article_relations_backup_v316
-- 2. 已重命名旧表为 gb_site_article_relations_old（保留以便回滚）
-- 3. 创建了新表 gb_site_master_article_relations
-- 4. 数据已按主文章分组迁移
-- 5. 如需回滚：
--    DROP TABLE gb_site_master_article_relations;
--    RENAME TABLE gb_site_article_relations_old TO gb_site_article_relations;
-- =====================================================
