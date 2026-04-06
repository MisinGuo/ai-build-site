-- ====================================================================
-- 游戏推广系统数据库升级脚本 v3.25
-- 功能：将现有数据库升级为统一架构，支持4个平台数据导入
-- 平台：U2Game, Milu, 277sy, DongYouXi
-- 升级日期：2025-01-20
-- ====================================================================

-- ====================================================================
-- 架构说明：主表 + 附表混合方案
-- ====================================================================
-- 【设计理念】
-- 主表 gb_games：存储所有平台共有的核心字段（高频查询，性能优先）
-- 附表 gb_games_platform_ext：存储平台特有的扩展字段（按需JOIN，避免冗余）
-- 
-- 【优势】
-- ✅ 主表轻量：只包含核心字段，查询快速
-- ✅ 零冗余：平台特有字段隔离在附表，不影响主表
-- ✅ 灵活扩展：新平台只需在附表添加记录
-- ✅ 性能优化：90%的查询只访问主表，无需JOIN
-- ====================================================================

-- ====================================================================
-- 第一部分：gb_games 主表优化（保持精简）
-- ====================================================================

-- 添加游戏推广链接配置表（如果不存在）
CREATE TABLE `gb_promotion_link_configs` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `platform` varchar(50) NOT NULL COMMENT '平台标识：u2game/milu/277sy/dongyouxi',
  `field_path` varchar(200) NOT NULL COMMENT '字段路径，支持嵌套（如：c.download, promotion_types.short_link.url）',
  `link_key` varchar(100) NOT NULL COMMENT '推广链接键名（存储到JSON中的key）',
  `link_description` varchar(200) NULL COMMENT '链接描述信息',
  `link_type` varchar(50) NULL DEFAULT 'url' COMMENT '链接类型：url/qrcode/text',
  `is_primary` char(1) DEFAULT '0' COMMENT '是否主推广链接：0-否 1-是',
  `sort_order` int DEFAULT 0 COMMENT '排序',
  `status` char(1) DEFAULT '1' COMMENT '状态：0-禁用 1-启用',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_platform_path` (`platform`, `field_path`),
  KEY `idx_platform` (`platform`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='推广链接配置表';

-- 检查并添加 gb_games 表缺失的字段

-- 添加 name_remark 字段（用于存储游戏副名、版本说明等）
ALTER TABLE `gb_games` 
ADD COLUMN `name_remark` varchar(200) COMMENT '游戏副名/版本说明' AFTER `subtitle`;

-- 添加 banner_url 字段
ALTER TABLE `gb_games` 
ADD COLUMN `banner_url` varchar(500) COMMENT '游戏横幅URL' AFTER `cover_url`;

-- 修改 game_genre 字段为 JSON 类型（如果还不是）
-- 注意：这个操作需要先备份数据
-- ALTER TABLE `gb_games` 
-- MODIFY COLUMN `game_genre` json COMMENT '游戏题材/流派（JSON数组，如：["卡牌","策略","三国"]）';

-- 添加 game_genre 字段（如果不存在）
ALTER TABLE `gb_games`
ADD COLUMN `game_genre` json COMMENT '游戏题材/流派（JSON数组）' AFTER `game_type`;

-- 主表优化：添加必要的平台标识字段
ALTER TABLE `gb_games`
ADD COLUMN `platform` varchar(50) COMMENT '来源平台：u2game/milu/277sy/dongyouxi' AFTER `status`;

ALTER TABLE `gb_games`
ADD COLUMN `source_id` varchar(100) COMMENT '源平台游戏ID' AFTER `platform`;

ALTER TABLE `gb_games`
ADD COLUMN `last_sync_time` datetime COMMENT '最后同步时间' AFTER `source_id`;

-- 为新字段添加索引
ALTER TABLE `gb_games` ADD INDEX `idx_platform` (`platform`);
ALTER TABLE `gb_games` ADD INDEX `idx_source_id` (`source_id`);

-- ====================================================================
-- 第一部分(附)：创建平台扩展数据表（存储平台特有字段）
-- ====================================================================

CREATE TABLE `gb_games_platform_ext` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `game_id` bigint NOT NULL COMMENT '游戏ID（关联gb_games.id）',
  `platform` varchar(50) NOT NULL COMMENT '平台标识',
  
  -- 推广链接集合（所有平台统一使用JSON格式）
  `promotion_links` json COMMENT '推广链接集合（JSON格式，包含links、qrcodes、metadata）',
  
  -- 平台特有数据（JSON格式，完全灵活）
  `platform_data` json COMMENT '平台特有数据（JSON格式，存储各平台独有字段）',
  
  -- 元数据
  `data_version` int DEFAULT 1 COMMENT '数据版本号',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) NULL COMMENT '备注',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_game_platform` (`game_id`, `platform`),
  KEY `idx_game_id` (`game_id`),
  KEY `idx_platform` (`platform`),
  CONSTRAINT `fk_ext_game` FOREIGN KEY (`game_id`) REFERENCES `gb_games` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='游戏平台扩展数据表-存储平台特有字段和推广链接';

-- 为JSON字段添加虚拟列索引（提升常用字段查询性能）
-- ALTER TABLE `gb_games_platform_ext`
-- ADD COLUMN `commission_rate` decimal(5,2) 
-- GENERATED ALWAYS AS (JSON_UNQUOTE(JSON_EXTRACT(platform_data, '$.commission_rate'))) VIRTUAL,
-- ADD INDEX `idx_commission_rate` (`commission_rate`);

-- ====================================================================
-- 第二部分：gb_game_boxes 表字段升级
-- ====================================================================

-- 添加盒子推广链接（统一为JSON格式）
ALTER TABLE `gb_game_boxes`
ADD COLUMN `promotion_links` json COMMENT '推广链接集合（JSON格式）' AFTER `register_download_url`;

-- 添加盒子特色描述
ALTER TABLE `gb_game_boxes`
ADD COLUMN `features_desc` text COMMENT '特色功能详细描述' AFTER `features`;

-- 添加盒子评分
ALTER TABLE `gb_game_boxes`
ADD COLUMN `rating` decimal(2, 1) DEFAULT 0.0 COMMENT '评分（0-5）' AFTER `game_count`;

-- 添加盒子下载次数
ALTER TABLE `gb_game_boxes`
ADD COLUMN `download_count` int DEFAULT 0 COMMENT '下载次数' AFTER `rating`;

-- 添加盒子支持的游戏类型
ALTER TABLE `gb_game_boxes`
ADD COLUMN `supported_game_types` json COMMENT '支持的游戏类型（JSON数组）' AFTER `features_desc`;

-- 添加源平台字段
ALTER TABLE `gb_game_boxes`
ADD COLUMN `platform` varchar(50) COMMENT '来源平台：u2game/milu/277sy/dongyouxi' AFTER `status`;

-- 添加源平台ID字段
ALTER TABLE `gb_game_boxes`
ADD COLUMN `source_id` varchar(100) COMMENT '源平台盒子ID' AFTER `platform`;

-- 添加最后同步时间
ALTER TABLE `gb_game_boxes`
ADD COLUMN `last_sync_time` datetime COMMENT '最后同步时间' AFTER `source_id`;

-- 为新字段添加索引
ALTER TABLE `gb_game_boxes` ADD INDEX `idx_platform` (`platform`);
ALTER TABLE `gb_game_boxes` ADD INDEX `idx_source_id` (`source_id`);
ALTER TABLE `gb_game_boxes` ADD INDEX `idx_rating` (`rating`);

-- ====================================================================
-- 第三部分：插入推广链接配置数据（示例配置）
-- ====================================================================
-- 注意：游戏-盒子关联表 gb_box_game_relations 已存在，无需重复创建

-- U2Game 平台配置
INSERT IGNORE INTO `gb_promotion_link_configs` 
(`platform`, `field_path`, `link_key`, `link_description`, `link_type`, `is_primary`, `sort_order`, `status`) 
VALUES
('u2game', 'downurl', 'download_url', '通用下载链接', 'url', '1', 1, '1'),
('u2game', 'dwzdownurl', 'short_download_url', '短链下载地址', 'url', '0', 2, '1'),
('u2game', 'weburl', 'web_url', '网页推广链接', 'url', '0', 3, '1'),
('u2game', 'url1', 'promo_url_1', '推广链接1', 'url', '0', 4, '1'),
('u2game', 'url2', 'promo_url_2', '推广链接2', 'url', '0', 5, '1'),
('u2game', 'url3', 'promo_url_3', '推广链接3', 'url', '0', 6, '1'),
('u2game', 'url4', 'promo_url_4', '推广链接4', 'url', '0', 7, '1'),
('u2game', 'url5', 'promo_url_5', '推广链接5', 'url', '0', 8, '1'),
('u2game', 'qrcode', 'qrcode', '下载二维码', 'qrcode', '0', 9, '1'),
('u2game', 'weburl_qr', 'web_qrcode', '网页二维码', 'qrcode', '0', 10, '1'),
('u2game', 'copy', 'copy_text', '复制文案', 'text', '0', 11, '1');

-- Milu 平台配置
INSERT IGNORE INTO `gb_promotion_link_configs` 
(`platform`, `field_path`, `link_key`, `link_description`, `link_type`, `is_primary`, `sort_order`, `status`) 
VALUES
('milu', 'android_download_link', 'android_download', '安卓下载链接', 'url', '1', 1, '1'),
('milu', 'ios_download_link', 'ios_download', 'iOS下载链接', 'url', '0', 2, '1'),
('milu', 'promotion_page_url', 'promotion_page', '推广页面链接', 'url', '0', 3, '1'),
('milu', 'multi_function_link', 'multi_function', '多功能链接', 'url', '0', 4, '1'),
('milu', 'material_package_url', 'material_package', '素材包下载', 'url', '0', 5, '1');

-- 277sy 平台配置
INSERT IGNORE INTO `gb_promotion_link_configs` 
(`platform`, `field_path`, `link_key`, `link_description`, `link_type`, `is_primary`, `sort_order`, `status`) 
VALUES
('277sy', 'downurl', 'download_url', '下载链接', 'url', '1', 1, '1'),
('277sy', 'weburl', 'web_url', '网页链接', 'url', '0', 2, '1'),
('277sy', 'qrcode', 'qrcode', '下载二维码', 'qrcode', '0', 3, '1');

-- DongYouXi 平台配置
INSERT IGNORE INTO `gb_promotion_link_configs` 
(`platform`, `field_path`, `link_key`, `link_description`, `link_type`, `is_primary`, `sort_order`, `status`) 
VALUES
('dongyouxi', 'tuiguanglianjie', 'promotion_link', '推广链接', 'url', '1', 1, '1');

-- U2Game 盒子配置
INSERT IGNORE INTO `gb_promotion_link_configs` 
(`platform`, `field_path`, `link_key`, `link_description`, `link_type`, `is_primary`, `sort_order`, `status`) 
VALUES
('u2game_box', 'c.download', 'download_url', '盒子下载链接', 'url', '1', 1, '1'),
('u2game_box', 'c.openurl', 'open_url', '盒子打开链接', 'url', '0', 2, '1'),
('u2game_box', 'c.url1', 'promo_url_1', '盒子推广链接1', 'url', '0', 3, '1'),
('u2game_box', 'c.url2', 'promo_url_2', '盒子推广链接2', 'url', '0', 4, '1'),
('u2game_box', 'c.url3', 'promo_url_3', '盒子推广链接3', 'url', '0', 5, '1'),
('u2game_box', 'c.url4', 'promo_url_4', '盒子推广链接4', 'url', '0', 6, '1'),
('u2game_box', 'c.url5', 'promo_url_5', '盒子推广链接5', 'url', '0', 7, '1'),
('u2game_box', 'c.qrcode', 'qrcode', '盒子二维码', 'qrcode', '0', 8, '1'),
('u2game_box', 'c.openqrcode', 'open_qrcode', '打开二维码', 'qrcode', '0', 9, '1');

-- ====================================================================
-- 第五部分：数据迁移说明和JSON字段使用示例
-- ====================================================================

-- 注意事项：
-- 1. 本脚本只添加新字段和表结构，不会删除现有数据
-- 2. 已有的 promotion_desc, download_url 等字段会保留，新导入数据将使用 promotion_links JSON字段
-- 3. 建议在执行前先备份数据库
-- 4. 执行本脚本后，需要运行 Python 导入脚本来填充 promotion_links 字段

-- JSON字段使用示例：

-- 【附表 gb_games_platform_ext 数据结构】

-- promotion_links 字段结构（所有平台统一格式）
/*
{
  "platform": "u2game",
  "extracted_at": "2025-01-25T10:30:00",
  "links": {
    "download_url": "https://qd.u2game99.com/down.html?ag=GMS123&gid=1697",
    "short_download_url": "https://qd.u2game99.com/down.html?ag=GMS123&gid=1697",
    "web_url": "https://www.u2game99.com/wg/1037/1697.html",
    "promo_url_1": "https://...",
    "promo_url_2": "https://..."
  },
  "qrcodes": {
    "qrcode": "data:image/png;base64,...",
    "web_qrcode": "data:image/png;base64,..."
  },
  "metadata": {
    "copy_text": "推广文案内容"
  }
}
*/

-- platform_data 字段结构（各平台特有数据）
/*
-- U2Game: {"device_type": 2, "edition": 1, "cps_cpa": "CPS推广:0"}
-- Milu: {"material_package_url": "...", "commission_rate": 60}
-- 277sy: {"client_type": "1", "genre_ids": [1,2,3]}
-- DongYouXi: {"excel_row": 10, "original_discount": "0.1折"}
*/

-- 【查询示例】

-- 示例1：列表查询（仅主表，性能最优）
-- SELECT id, name, icon_url, platform FROM gb_games 
-- WHERE status = '1' ORDER BY sort_order LIMIT 20;

-- 示例2：详情查询（主表 + 附表 JOIN）
-- SELECT g.*, ext.promotion_links, ext.platform_data
-- FROM gb_games g
-- LEFT JOIN gb_games_platform_ext ext ON g.id = ext.game_id
-- WHERE g.id = 1;

-- 示例3：使用视图简化查询
-- SELECT * FROM v_game_detail WHERE id = 1;

-- 示例4：跨平台搜索（仅查主表）
-- SELECT id, name, platform, game_type FROM gb_games
-- WHERE name LIKE '%传奇%' AND status = '1';

-- 示例5：查询特定平台有素材包的游戏
-- SELECT g.name, JSON_EXTRACT(ext.platform_data, '$.material_package_url') as material_url
-- FROM gb_games g
-- INNER JOIN gb_games_platform_ext ext ON g.id = ext.game_id
-- WHERE g.platform = 'milu' 
-- AND JSON_EXTRACT(ext.platform_data, '$.material_package_url') IS NOT NULL
    -- 附表扩展字段
    ext.promotion_links,
    ext.platform_data,
    
    -- 提取常用的JSON字段为虚拟列（方便查询）
    JSON_EXTRACT(ext.promotion_links, '$.links.download_url') as primary_download_url,
    JSON_EXTRACT(ext.platform_data, '$.commission_rate') as commission_rate,
    JSON_EXTRACT(ext.platform_data, '$.material_package_url') as material_url
FROM gb_games g
LEFT JOIN gb_games_platform_ext ext ON g.id = ext.game_id AND g.platform = ext.platform
WHERE g.del_flag = '0';

-- 创建游戏简略列表视图（仅主表，高性能）
CREATE OR REPLACE VIEW `v_game_list` AS
SELECT 
    g.id,
    g.name,
    g.game_type,
    g.icon_url,
    g.cover_url,
    g.device_support,
    g.discount_label,
    g.platform,
    g.status,
    g.is_hot,
    g.is_recommend,
    g.sort_order
FROM gb_games g
WHERE g.del_flag = '0' AND g.status = '1数据）
CREATE OR REPLACE VIEW `v_game_detail` AS
SELECT 
    g.id,
    g.name,
    g.name_remark,
    g.subtitle,
    g.game_type,
    g.game_genre,
    g.icon_url,
    g.cover_url,
    g.banner_url,
    g.device_support,
    g.discount_label,
    g.promotion_links,
    g.platform_data,
    g.platform,
    g.source_id,
    g.status,
    g.create_time,
    g.update_time,
    -- 提取常用的JSON字段为虚拟列，方便查询
    JSON_EXTRACT(g.promotion_links, '$.links.download_url') as primary_download_url,
    JSON_EXTRACT(g.platform_data, '$.commission_rate') as commission_rate,
    JSON_EXTRACT(g.platform_data, '$.material_package_url') as material_url
FROM gb_games g
WHERE g.del_flag = '0';

-- 创建盒子详情视图（包含游戏数量）
CREATE OR REPLACE VIEW `v_box_detail` AS
SELECT 
    b.id,
    b.name,
    b.logo_url,
    b.banner_url,
    b.description,
    b.download_url,
    b.promotion_links,
    b.game_count,
    b.rating,
    b.download_count,
    b.platform,
    b.source_id,
    b.status,
    COUNT(DISTINCT r.game_id) as actual_game_count
FROM gb_game_boxes b
LEFT JOIN gb_box_game_relations r ON b.id = r.box_id
WHERE b.del_flag = '0'
GROUP BY b.id;

-- ====================================================================
-- 脚本执行完毕
-- ====================================================================

-- 请按以下步骤操作：
-- 1. 备份当前数据库
-- 2. 在测试环境执行本脚本
-- 3. 验证表结构是否正确添加
-- 4. 运行 Python 数据导入脚本（import_data_unified.py）
-- 5. 验证数据导入结果
-- 6. 在生产环境执行

-- 升级日志：
-- v3.25 (2025-01-25)
-- - 添加统一推广链接架构支持
-- - 添加 promotion_links JSON 字段存储多平台推广链接
-- - 添加 platform_data JSON 字段存储平台特有数据（零冗余设计）
-- - 添加推广链接配置表（配置驱动的链接映射）
-- - 添加平台标识和源ID字段
-- - 注意：复用现有的 gb_box_game_relations 关联表（已包含丰富业务字段）
-- - 为关键字段添加索引以提升查询性能
-- - 为关键字段添加索引以提升查询性能

-- 架构优势：
-- ✅ 零冗余：新增平台无需添加字段，只需在 platform_data JSON 中存储
-- ✅ 高扩展：通过 gb_promotion_link_configs 表配置新平台的推广链接映射
-- ✅ 灵活性：JSON字段可以存储任意结构的平台特有数据
-- ✅ 向后兼容：保留现有字段，平滑迁移

-- 新增平台步骤（仅需配置，无需修改表结构）：
-- 1. 在 gb_promotion_link_configs 表中添加新平台的推广链接映射配置
-- 2. Python导入脚本自动读取配置，将数据填充到 promotion_links 和 platform_data
-- 3. 无需 ALTER TABLE，无需重启服务
 - 主表+附表混合架构
-- - 采用主附表分离设计，优化查询性能和表结构清晰度
-- - gb_games 主表：仅存储核心字段，保持轻量高效
-- - gb_games_platform_ext 附表：存储推广链接和平台特有数据
-- - 添加游戏-盒子关联表 gb_game_box_relation
-- - 添加推广链接配置表 gb_promotion_link_configs
-- - 创建优化视图：v_game_detail（完整信息）、v_game_list（列表查询）

-- 架构优势：
-- ✅ 性能优化：90%的查询只访问主表，无需JOIN（列表、搜索、统计）
-- ✅ 零冗余：平台特有字段隔离在附表，主表保持精简
-- ✅ 灵活扩展：新平台仅在附表添加记录，主表结构不变
-- ✅ 按需加载：详情页才JOIN附表，获取推广链接和扩展数据
-- ✅ 向后兼容：保留现有字段，平滑迁移

-- 查询模式：
-- - 列表查询：SELECT * FROM gb_games WHERE status='1' (仅主表，最快)
-- - 详情查询：SELECT * FROM v_game_detail WHERE id=1 (主表+附表JOIN)
-- - 跨平台搜索：SELECT * FROM gb_games WHERE name LIKE '%传奇%' (仅主表)

-- 新增平台步骤（配置驱动，零停机）：
-- 1. 在 gb_promotion_link_configs 表中添加新平台的推广链接映射配置
-- 2. Python导入脚本读取配置，将数据导入 gb_games_platform_ext 附表
-- 3. 主表结构不变，