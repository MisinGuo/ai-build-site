-- 多语言支持快速实施SQL
-- 执行时间：立即

-- 1. 创建通用翻译表
DROP TABLE IF EXISTS `gb_translations`;
CREATE TABLE `gb_translations` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `entity_type` varchar(50) NOT NULL COMMENT '实体类型：game, box, gamebox, category, drama',
  `entity_id` bigint NOT NULL COMMENT '实体ID',
  `locale` varchar(10) NOT NULL DEFAULT 'zh-CN' COMMENT '语言代码：zh-TW, en（不存储默认语言zh-CN）',
  `field_name` varchar(100) NOT NULL COMMENT '字段名称：name, description, subtitle等',
  `field_value` text COMMENT '字段值',
  `create_by` varchar(64) DEFAULT 'system' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT 'system' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_translation` (`entity_type`, `entity_id`, `locale`, `field_name`),
  KEY `idx_entity` (`entity_type`, `entity_id`),
  KEY `idx_locale` (`locale`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通用翻译表';

-- 2. 添加示例翻译数据（可选，用于测试）
-- 注意：只存储非默认语言的翻译，默认语言zh-CN使用主表数据
-- 如果数据库中有ID为1的游戏，可以执行以下示例：
/*
INSERT INTO `gb_translations` (entity_type, entity_id, locale, field_name, field_value, create_by) VALUES
('game', 1, 'zh-TW', 'name', '王者榮耀', 'system'),
('game', 1, 'en', 'name', 'Honor of Kings', 'system'),
('game', 1, 'zh-TW', 'description', '經典MOBA手遊，5V5競技對戰', 'system'),
('game', 1, 'en', 'description', 'Classic MOBA mobile game with 5V5 competitive battles', 'system');
*/

-- 3. 创建翻译缓存视图（提升查询性能）
-- 3. 创建翻译缓存视图（提升查询性能）
-- 注意：默认使用主表数据，翻译表只存储非默认语言
CREATE OR REPLACE VIEW v_game_translations AS
SELECT 
    g.id,
    g.site_id,
    g.category_id,
    g.name as original_name,
    g.description as original_description,
    g.subtitle as original_subtitle,
    -- 如果没有翻译，直接使用主表的原始值
    COALESCE(t_name.field_value, g.name) as display_name,
    COALESCE(t_desc.field_value, g.description) as display_description,
    COALESCE(t_subtitle.field_value, g.subtitle) as display_subtitle,
    -- 如果没有翻译记录，说明使用默认语言
    COALESCE(t_name.locale, 'zh-CN') as locale
FROM gb_games g
LEFT JOIN gb_translations t_name ON (g.id = t_name.entity_id AND t_name.entity_type = 'game' AND t_name.field_name = 'name')
LEFT JOIN gb_translations t_desc ON (g.id = t_desc.entity_id AND t_desc.entity_type = 'game' AND t_desc.field_name = 'description')
LEFT JOIN gb_translations t_subtitle ON (g.id = t_subtitle.entity_id AND t_subtitle.entity_type = 'game' AND t_subtitle.field_name = 'subtitle')
WHERE g.del_flag IS NULL OR g.del_flag != '2';

-- 4. 验证数据（初始应该为空，只有手动添加翻译后才有数据）
SELECT 
    '翻译表状态检查' as info,
    '翻译表应该为空或只包含非默认语言的翻译' as note;

SELECT 
    entity_type as '实体类型',
    locale as '语言',
    COUNT(*) as '翻译数量',
    COUNT(DISTINCT entity_id) as '实体数量'
FROM gb_translations
GROUP BY entity_type, locale
ORDER BY entity_type, locale;