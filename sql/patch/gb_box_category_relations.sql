-- ====================================
-- 游戏盒子分类多对多关联表 - Patch SQL
-- 创建日期: 2025-12-25
-- 说明: 实现游戏盒子与分类的多对多关系
-- ====================================

-- 步骤1: 创建盒子分类关联表
DROP TABLE IF EXISTS `gb_box_category_relations`;
CREATE TABLE `gb_box_category_relations` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `box_id` bigint NOT NULL COMMENT '盒子ID',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `sort_order` int DEFAULT 0 COMMENT '在该分类中的排序',
  `is_primary` char(1) DEFAULT '0' COMMENT '是否主分类：0-否 1-是',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_box_category` (`box_id`, `category_id`),
  KEY `idx_box_id` (`box_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_is_primary` (`is_primary`),
  KEY `idx_sort_order` (`sort_order`),
  CONSTRAINT `fk_box_category_box` FOREIGN KEY (`box_id`) REFERENCES `gb_game_boxes` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_box_category_category` FOREIGN KEY (`category_id`) REFERENCES `gb_categories` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='游戏盒子分类关联表';

-- 步骤2: 迁移现有的单一分类关联到关联表
INSERT INTO `gb_box_category_relations` (`box_id`, `category_id`, `is_primary`, `sort_order`, `create_by`, `create_time`)
SELECT 
    b.id AS box_id,
    b.category_id,
    '1' AS is_primary,  -- 标记为主分类
    0 AS sort_order,
    'system' AS create_by,
    NOW() AS create_time
FROM gb_game_boxes b
WHERE b.category_id IS NOT NULL 
  AND b.category_id > 0
  AND b.del_flag = '0'
  AND NOT EXISTS (
    SELECT 1 FROM gb_box_category_relations r
    WHERE r.box_id = b.id AND r.category_id = b.category_id
  );

-- 步骤3: 验证迁移结果
SELECT 
    '迁移统计报告' AS 报告类型,
    COUNT(DISTINCT box_id) AS 已关联盒子数,
    COUNT(*) AS 总关联记录数,
    SUM(CASE WHEN is_primary = '1' THEN 1 ELSE 0 END) AS 主分类记录数
FROM gb_box_category_relations;

-- 步骤4: 数据一致性检查
SELECT 
    '数据一致性检查' AS 检查类型,
    COUNT(*) AS 异常数据数
FROM gb_game_boxes b
WHERE b.category_id IS NOT NULL 
  AND b.category_id > 0
  AND b.del_flag = '0'
  AND NOT EXISTS (
    SELECT 1 FROM gb_box_category_relations r
    WHERE r.box_id = b.id AND r.category_id = b.category_id
  );

-- 步骤5: 检查是否有盒子没有主分类
SELECT 
    '主分类检查' AS 检查类型,
    COUNT(DISTINCT r.box_id) AS 有关联但无主分类的盒子数
FROM gb_box_category_relations r
WHERE NOT EXISTS (
    SELECT 1 FROM gb_box_category_relations r2
    WHERE r2.box_id = r.box_id AND r2.is_primary = '1'
);

-- ====================================
-- 使用说明：
-- 1. 执行此脚本将创建 gb_box_category_relations 表
-- 2. 自动迁移 gb_game_boxes 表中的 category_id 数据
-- 3. 保留原有的 category_id 字段，以保持向后兼容
-- 4. 查看迁移结果和数据一致性报告
-- ====================================

-- 可选: 回滚操作（慎用）
-- DROP TABLE IF EXISTS `gb_box_category_relations`;
