-- ====================================================================
-- 游戏推广系统数据库升级脚本 v3.27
-- 功能：添加字段映射配置表，支持多平台数据导入
-- 升级日期：2026-01-25
-- ====================================================================

-- ====================================================================
-- 字段映射配置表
-- ====================================================================

CREATE TABLE IF NOT EXISTS `gb_field_mappings` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `platform` varchar(50) NOT NULL COMMENT '平台标识：u2game/milu/277sy/dongyouxi',
  `resource_type` varchar(20) NOT NULL COMMENT '资源类型：game-游戏 / box-盒子',
  `source_field` varchar(200) NOT NULL COMMENT '源字段路径（支持嵌套，如：a.b.c或a.0.b）',
  `target_field` varchar(100) NOT NULL COMMENT '目标字段名（主表字段或platform_data中的key）',
  `field_type` varchar(50) DEFAULT 'string' COMMENT '字段类型：string/int/decimal/json/date/boolean',
  `target_location` varchar(20) DEFAULT 'main' COMMENT '目标位置：main-主表 / ext-附表platform_data',
  `transform_expression` text COMMENT '转换表达式（可选，用于字段值转换，如：CASE WHEN...）',
  `default_value` varchar(500) COMMENT '默认值（源字段不存在或为空时使用）',
  `is_required` char(1) DEFAULT '0' COMMENT '是否必填：0-否 1-是',
  `sort_order` int DEFAULT 0 COMMENT '排序',
  `status` char(1) DEFAULT '1' COMMENT '状态：0-禁用 1-启用',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) COMMENT '备注说明',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_platform_resource_source` (`platform`, `resource_type`, `source_field`),
  KEY `idx_platform_resource` (`platform`, `resource_type`),
  KEY `idx_target_location` (`target_location`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字段映射配置表';

-- ====================================================================
-- 初始化配置数据
-- ====================================================================

-- ====================================================================
-- U2Game平台配置
-- ====================================================================

-- U2Game游戏字段映射（主表字段）
INSERT IGNORE INTO `gb_field_mappings` 
(`platform`, `resource_type`, `source_field`, `target_field`, `field_type`, `target_location`, `is_required`, `sort_order`, `status`, `remark`) 
VALUES
('u2game', 'game', 'gamename', 'name', 'string', 'main', '1', 1, '1', '游戏名称'),
('u2game', 'game', 'pic1', 'icon_url', 'string', 'main', '1', 2, '1', '游戏图标URL'),
('u2game', 'game', 'photo.0.url', 'cover_url', 'string', 'main', '0', 3, '1', '封面图（取第一张截图）'),
('u2game', 'game', 'gametype', 'game_type', 'string', 'main', '0', 4, '1', '游戏类型'),
('u2game', 'game', 'excerpt', 'description', 'string', 'main', '0', 5, '1', '游戏描述'),
('u2game', 'game', 'welfare', 'discount_label', 'string', 'main', '0', 6, '1', '折扣标签/福利'),
('u2game', 'game', 'device_type', 'device_support', 'string', 'main', '0', 7, '1', '设备支持（1=安卓, 2=双端, 3=iOS）'),
('u2game', 'game', 'status', 'status', 'string', 'main', '0', 8, '1', '状态（默认为1启用）');

-- U2Game游戏字段映射（附表平台特有数据）
INSERT IGNORE INTO `gb_field_mappings` 
(`platform`, `resource_type`, `source_field`, `target_field`, `field_type`, `target_location`, `is_required`, `sort_order`, `status`, `remark`) 
VALUES
('u2game', 'game', 'id', 'source_id', 'string', 'ext', '1', 100, '1', '源平台游戏ID'),
('u2game', 'game', 'cps_cpa', 'commission_info', 'string', 'ext', '0', 101, '1', 'CPS/CPA佣金信息'),
('u2game', 'game', 'hw_cps_cpa', 'hw_commission_info', 'string', 'ext', '0', 102, '1', '海外佣金信息'),
('u2game', 'game', 'video', 'video_url', 'string', 'ext', '0', 103, '1', '视频链接'),
('u2game', 'game', 'photo', 'screenshots', 'json', 'ext', '0', 104, '1', '游戏截图数组'),
('u2game', 'game', 'edition', 'edition_type', 'int', 'ext', '0', 105, '1', '版本类型'),
('u2game', 'game', 'strict', 'strict_mode', 'int', 'ext', '0', 106, '1', '严格模式'),
('u2game', 'game', 'text_cps', 'promotion_note', 'string', 'ext', '0', 107, '1', '推广说明（如：禁海外推广）'),
('u2game', 'game', 'addtime', 'platform_add_time', 'string', 'ext', '0', 108, '1', '平台添加时间'),
('u2game', 'game', 'updatetime', 'platform_update_time', 'string', 'ext', '0', 109, '1', '平台更新时间');

-- U2Game盒子字段映射
INSERT IGNORE INTO `gb_field_mappings` 
(`platform`, `resource_type`, `source_field`, `target_field`, `field_type`, `target_location`, `is_required`, `sort_order`, `status`, `remark`) 
VALUES
('u2game', 'box', 'boxname', 'name', 'string', 'main', '1', 1, '1', '盒子名称'),
('u2game', 'box', 'boxlogo', 'logo_url', 'string', 'main', '0', 2, '1', '盒子Logo'),
('u2game', 'box', 'boxicon', 'icon_url', 'string', 'main', '0', 3, '1', '盒子图标'),
('u2game', 'box', 'boxdescription', 'description', 'string', 'main', '0', 4, '1', '盒子描述'),
('u2game', 'box', 'c.download', 'download_url', 'string', 'main', '0', 5, '1', '下载链接');

-- ====================================================================
-- Milu平台配置
-- ====================================================================

-- Milu游戏字段映射（主表字段）
INSERT IGNORE INTO `gb_field_mappings` 
(`platform`, `resource_type`, `source_field`, `target_field`, `field_type`, `target_location`, `is_required`, `sort_order`, `status`, `remark`) 
VALUES
('milu', 'game', 'game_name', 'name', 'string', 'main', '1', 1, '1', '游戏名称'),
('milu', 'game', 'name_remark', 'subtitle', 'string', 'main', '0', 2, '1', '游戏副标题/版本说明'),
('milu', 'game', 'game_icon', 'icon_url', 'string', 'main', '1', 3, '1', '游戏图标URL'),
('milu', 'game', 'game_genre', 'game_genre', 'json', 'main', '0', 4, '1', '游戏题材数组（如：["卡牌","冒险"]）'),
('milu', 'game', 'discount_info', 'discount_label', 'string', 'main', '0', 5, '1', '折扣信息（如：0.05折）'),
('milu', 'game', 'support_platform', 'device_support', 'string', 'main', '0', 6, '1', '支持平台（安卓/双端/iOS）');

-- Milu游戏字段映射（附表平台特有数据）
INSERT IGNORE INTO `gb_field_mappings` 
(`platform`, `resource_type`, `source_field`, `target_field`, `field_type`, `target_location`, `is_required`, `sort_order`, `status`, `remark`) 
VALUES
('milu', 'game', 'material_package_url', 'material_url', 'string', 'ext', '0', 100, '1', '素材包下载URL'),
('milu', 'game', 'video_material_url', 'video_url', 'string', 'ext', '0', 101, '1', '视频素材URL'),
('milu', 'game', 'category', 'category_tag', 'string', 'ext', '0', 102, '1', '分类标签（如：专服）'),
('milu', 'game', 'has_profession_tag', 'has_profession', 'boolean', 'ext', '0', 103, '1', '是否有职业标签'),
('milu', 'game', 'release_time', 'release_time', 'string', 'ext', '0', 104, '1', '上线时间'),
('milu', 'game', 'commission_rate', 'commission_rate', 'int', 'ext', '0', 105, '1', '佣金比例');

-- ====================================================================
-- 277sy平台配置
-- ====================================================================

-- 277sy游戏字段映射
INSERT IGNORE INTO `gb_field_mappings` 
(`platform`, `resource_type`, `source_field`, `target_field`, `field_type`, `target_location`, `is_required`, `sort_order`, `status`, `remark`) 
VALUES
('277sy', 'game', 'gamename', 'name', 'string', 'main', '1', 1, '1', '游戏名称'),
('277sy', 'game', 'gameicon', 'icon_url', 'string', 'main', '1', 2, '1', '游戏图标'),
('277sy', 'game', 'banner_pic', 'cover_url', 'string', 'main', '0', 3, '1', '横幅图'),
('277sy', 'game', 'game_type', 'game_type', 'string', 'main', '0', 4, '1', '游戏类型'),
('277sy', 'game', 'gamedes', 'description', 'string', 'main', '0', 5, '1', '游戏描述'),
('277sy', 'game', 'discount', 'first_charge_domestic', 'decimal', 'main', '0', 6, '1', '首充折扣'),
('277sy', 'game', 'genre_ids', 'genre_tags', 'json', 'ext', '0', 100, '1', '题材ID数组'),
('277sy', 'game', 'client_type', 'client_type', 'string', 'ext', '0', 101, '1', '客户端类型（1=原生 2=H5）'),
('277sy', 'game', 'screenshot1', 'screenshot1', 'string', 'ext', '0', 102, '1', '截图1'),
('277sy', 'game', 'screenshot2', 'screenshot2', 'string', 'ext', '0', 103, '1', '截图2'),
('277sy', 'game', 'screenshot3', 'screenshot3', 'string', 'ext', '0', 104, '1', '截图3');

-- ====================================================================
-- DongYouXi平台配置
-- ====================================================================

-- DongYouXi游戏字段映射（Excel数据源）
INSERT IGNORE INTO `gb_field_mappings` 
(`platform`, `resource_type`, `source_field`, `target_field`, `field_type`, `target_location`, `is_required`, `sort_order`, `status`, `remark`) 
VALUES
('dongyouxi', 'game', '游戏名', 'name', 'string', 'main', '1', 1, '1', '游戏名称'),
('dongyouxi', 'game', '版本', 'subtitle', 'string', 'main', '0', 2, '1', '版本信息'),
('dongyouxi', 'game', '游戏类型', 'game_type', 'string', 'main', '0', 3, '1', '游戏类型'),
('dongyouxi', 'game', '折扣', 'discount_label', 'string', 'main', '0', 4, '1', '折扣标签'),
('dongyouxi', 'game', '推广链接', 'tuiguanglianjie', 'string', 'ext', '1', 100, '1', '推广链接（通常只有一个）');

-- ====================================================================
-- 修改推广链接配置表：添加resource_type字段
-- ====================================================================

-- 检查字段是否存在，如果不存在则添加
SET @exist := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
               WHERE TABLE_SCHEMA = DATABASE() 
               AND TABLE_NAME = 'gb_promotion_link_configs' 
               AND COLUMN_NAME = 'resource_type');

SET @sql := IF(@exist = 0, 
    'ALTER TABLE `gb_promotion_link_configs` ADD COLUMN `resource_type` varchar(20) DEFAULT ''game'' COMMENT ''资源类型：game-游戏 / box-盒子'' AFTER `platform`',
    'SELECT ''Column resource_type already exists'' AS message');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加索引（检查是否存在）
SET @idx_exist := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS 
                   WHERE TABLE_SCHEMA = DATABASE() 
                   AND TABLE_NAME = 'gb_promotion_link_configs' 
                   AND INDEX_NAME = 'idx_platform_resource');

SET @idx_sql := IF(@idx_exist = 0, 
    'ALTER TABLE `gb_promotion_link_configs` ADD INDEX `idx_platform_resource` (`platform`, `resource_type`)',
    'SELECT ''Index idx_platform_resource already exists'' AS message');

PREPARE stmt FROM @idx_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 更新现有数据：为已有的配置设置resource_type
UPDATE `gb_promotion_link_configs` 
SET `resource_type` = CASE 
    WHEN `platform` LIKE '%_box' THEN 'box'
    ELSE 'game'
END
WHERE `resource_type` IS NULL OR `resource_type` = '';

-- ====================================================================
-- 修改推广链接配置表：标准化字段名
-- ====================================================================

-- 将 field_path 重命名为 source_field，与 gb_field_mappings 保持一致
SET @exist := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
               WHERE TABLE_SCHEMA = DATABASE() 
               AND TABLE_NAME = 'gb_promotion_link_configs' 
               AND COLUMN_NAME = 'field_path');

SET @sql := IF(@exist > 0, 
    'ALTER TABLE `gb_promotion_link_configs` CHANGE COLUMN `field_path` `source_field` varchar(200) NOT NULL COMMENT ''源字段路径（支持嵌套）''',
    'SELECT ''Column field_path does not exist'' AS message');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ====================================================================
-- 创建视图：平台数据映射概览
-- ====================================================================

CREATE OR REPLACE VIEW `v_platform_mapping_summary` AS
SELECT 
    fm.platform,
    fm.resource_type,
    COUNT(*) as total_mappings,
    SUM(CASE WHEN fm.target_location = 'main' THEN 1 ELSE 0 END) as main_fields,
    SUM(CASE WHEN fm.target_location = 'ext' THEN 1 ELSE 0 END) as ext_fields,
    SUM(CASE WHEN fm.is_required = '1' THEN 1 ELSE 0 END) as required_fields,
    SUM(CASE WHEN fm.status = '1' THEN 1 ELSE 0 END) as enabled_mappings,
    MAX(fm.update_time) as last_update_time
FROM gb_field_mappings fm
GROUP BY fm.platform, fm.resource_type;

-- ====================================================================
-- 脚本执行完毕
-- ====================================================================

SELECT '✅ v3.27升级脚本执行完毕' AS message;
SELECT '📋 字段映射配置表已创建' AS message;
SELECT '📊 查询平台映射概览: SELECT * FROM v_platform_mapping_summary' AS message;

-- 升级日志：
-- v3.27 (2026-01-25)
-- - 创建字段映射配置表 gb_field_mappings
-- - 初始化U2Game、Milu、277sy、DongYouXi平台的字段映射配置
-- - 扩展推广链接配置表，添加resource_type字段区分游戏和盒子
-- - 标准化字段命名：field_path → source_field
-- - 创建平台映射概览视图
-- - 支持主表+附表的字段映射配置
-- - 支持嵌套字段路径提取（如：photo.0.url）
-- - 支持字段转换表达式
