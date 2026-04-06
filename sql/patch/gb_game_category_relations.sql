-- ====================================
-- 游戏分类多对多关联表 - Patch SQL
-- 创建日期: 2025-12-25
-- 说明: 实现游戏与分类的多对多关系
-- ====================================

-- 步骤1: 创建游戏分类关联表
DROP TABLE IF EXISTS `gb_game_category_relations`;
CREATE TABLE `gb_game_category_relations` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `game_id` bigint NOT NULL COMMENT '游戏ID',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `sort_order` int DEFAULT 0 COMMENT '在该分类中的排序',
  `is_primary` char(1) DEFAULT '0' COMMENT '是否主分类：0-否 1-是',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_game_category` (`game_id`, `category_id`),
  KEY `idx_game_id` (`game_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_is_primary` (`is_primary`),
  KEY `idx_sort_order` (`sort_order`),
  CONSTRAINT `fk_game_category_game` FOREIGN KEY (`game_id`) REFERENCES `gb_games` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_game_category_category` FOREIGN KEY (`category_id`) REFERENCES `gb_categories` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='游戏分类关联表';

-- 步骤2: 迁移现有的单一分类关联到关联表
INSERT INTO `gb_game_category_relations` (`game_id`, `category_id`, `is_primary`, `sort_order`, `create_by`, `create_time`)
SELECT 
    g.id AS game_id,
    g.category_id,
    '1' AS is_primary,  -- 标记为主分类
    0 AS sort_order,
    'system' AS create_by,
    NOW() AS create_time
FROM gb_games g
WHERE g.category_id IS NOT NULL 
  AND g.category_id > 0
  AND g.del_flag = '0'
  AND NOT EXISTS (
    SELECT 1 FROM gb_game_category_relations r
    WHERE r.game_id = g.id AND r.category_id = g.category_id
  );

-- 步骤3: 验证迁移结果
SELECT 
    '迁移统计报告' AS 报告类型,
    COUNT(DISTINCT game_id) AS 已关联游戏数,
    COUNT(*) AS 关联记录数,
    SUM(CASE WHEN is_primary = '1' THEN 1 ELSE 0 END) AS 主分类数量
FROM gb_game_category_relations;

-- 步骤4: 检查未迁移的游戏（用于排查问题）
SELECT 
    '未迁移游戏列表' AS 类型,
    g.id,
    g.name,
    g.category_id,
    g.status
FROM gb_games g
LEFT JOIN gb_game_category_relations r ON g.id = r.game_id
WHERE r.id IS NULL 
  AND g.status = '1'
  AND g.del_flag = '0';

-- 步骤5: 更新分类表的游戏数量统计
UPDATE gb_categories c
SET document_count = (
    SELECT COUNT(DISTINCT r.game_id)
    FROM gb_game_category_relations r
    INNER JOIN gb_games g ON r.game_id = g.id
    WHERE r.category_id = c.id 
      AND g.status = '1'
      AND g.del_flag = '0'
)
WHERE c.category_type = 'game';

-- 步骤6: 显示分类统计信息
SELECT 
    c.id,
    c.name AS 分类名称,
    c.document_count AS 游戏数量,
    c.status AS 状态
FROM gb_categories c
WHERE c.category_type = 'game'
ORDER BY c.sort_order, c.id;

-- 执行完成提示
SELECT '===== 游戏分类多对多关联表创建完成 =====' AS 提示;
SELECT '请检查上述统计信息，确认迁移是否成功' AS 说明;
