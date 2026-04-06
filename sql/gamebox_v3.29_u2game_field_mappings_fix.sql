-- ================================
-- 游戏盒子系统 v3.29 补丁（修复版）
-- 功能: 添加u2game平台完整字段映射配置
-- 日期: 2026-01-26
-- 说明: 修复status字段值，确保所有映射启用
-- ================================

-- 删除现有的u2game映射（如果存在）
DELETE FROM `gb_field_mappings` WHERE platform = 'u2game' AND resource_type = 'game';

-- ================================
-- 主表字段映射（存储到 gb_games 表）
-- ================================

-- 基本信息字段
INSERT INTO `gb_field_mappings` 
(`platform`, `resource_type`, `source_field`, `target_field`, `field_type`, `target_location`, `transform_expression`, `default_value`, `is_required`, `sort_order`, `status`, `remark`) 
VALUES
('u2game', 'game', 'gamename', 'name', 'string', 'main', NULL, NULL, '1', 1, '1', '游戏名称'),
('u2game', 'game', 'pic1', 'icon_url', 'string', 'main', NULL, NULL, '1', 2, '1', '游戏图标'),
('u2game', 'game', 'pic2', 'cover_url', 'string', 'main', NULL, NULL, '1', 3, '1', '游戏封面'),
('u2game', 'game', 'video', 'video_url', 'string', 'main', NULL, NULL, '0', 4, '1', '游戏视频'),
('u2game', 'game', 'excerpt', 'description', 'string', 'main', NULL, NULL, '1', 5, '1', '游戏描述'),
('u2game', 'game', 'gametype', 'game_type', 'string', 'main', NULL, NULL, '0', 6, '1', '游戏类型'),
('u2game', 'game', 'device_type', 'device_support', 'number', 'main', NULL, NULL, '0', 7, '1', '设备支持(0=android,1=ios,2=both)'),
('u2game', 'game', 'welfare', 'discount_label', 'string', 'main', NULL, NULL, '0', 8, '1', '折扣标签/福利说明'),
('u2game', 'game', 'firstpay', 'first_charge_discount', 'decimal', 'main', NULL, NULL, '0', 9, '1', '首充折扣'),
('u2game', 'game', 'otherpay', 'recharge_discount', 'decimal', 'main', NULL, NULL, '0', 10, '1', '续充折扣'),
('u2game', 'game', 'addtime', 'launch_time', 'date', 'main', NULL, NULL, '0', 11, '1', '上线时间'),
('u2game', 'game', 'updatetime', 'update_time', 'date', 'main', NULL, NULL, '0', 12, '1', '更新时间'),
('u2game', 'game', 'new', 'is_new', 'number', 'main', NULL, NULL, '0', 13, '1', '是否新游'),
('u2game', 'game', 'photo', 'screenshots', 'array', 'main', 'join:', NULL, '0', 14, '1', '游戏截图数组(提取url后转JSON)');

-- ================================
-- 推广链接字段映射（存储到 promotion_links JSON）
-- ================================

INSERT INTO `gb_field_mappings` 
(`platform`, `resource_type`, `source_field`, `target_field`, `field_type`, `target_location`, `transform_expression`, `default_value`, `is_required`, `sort_order`, `status`, `remark`) 
VALUES
('u2game', 'game', 'downurl', 'download_url', 'string', 'promotion_link', NULL, NULL, '0', 20, '1', '下载链接'),
('u2game', 'game', 'dwzdownurl', 'short_download_url', 'string', 'promotion_link', NULL, NULL, '0', 21, '1', '短链下载地址'),
('u2game', 'game', 'qrcode', 'qrcode_data', 'string', 'promotion_link', NULL, NULL, '0', 22, '1', '二维码数据'),
('u2game', 'game', 'weburl', 'web_url', 'string', 'promotion_link', NULL, NULL, '0', 23, '1', '网页地址'),
('u2game', 'game', 'weburl_qr', 'web_qrcode', 'string', 'promotion_link', NULL, NULL, '0', 24, '1', '网页二维码'),
('u2game', 'game', 'downurlmash', 'mash_download_url', 'string', 'promotion_link', NULL, NULL, '0', 25, '1', 'Mash下载链接'),
('u2game', 'game', 'dwzdownurlmash', 'short_mash_url', 'string', 'promotion_link', NULL, NULL, '0', 26, '1', 'Mash短链'),
('u2game', 'game', 'copy', 'copy_text', 'string', 'promotion_link', NULL, NULL, '0', 27, '1', '复制推广文本');

-- ================================
-- 平台数据字段映射（存储到 platform_data JSON）
-- ================================

INSERT INTO `gb_field_mappings` 
(`platform`, `resource_type`, `source_field`, `target_field`, `field_type`, `target_location`, `transform_expression`, `default_value`, `is_required`, `sort_order`, `status`, `remark`) 
VALUES
('u2game', 'game', 'box_content', 'box_description', 'string', 'platform_data', NULL, NULL, '0', 30, '1', '盒子推广内容/福利说明'),
('u2game', 'game', 'cps_cpa', 'cps_info', 'string', 'platform_data', NULL, NULL, '0', 31, '1', 'CPS/CPA推广信息'),
('u2game', 'game', 'hw_cps_cpa', 'hw_cps_info', 'string', 'platform_data', NULL, NULL, '0', 32, '1', '海外CPS信息'),
('u2game', 'game', 'text_cps', 'cps_note', 'string', 'platform_data', NULL, NULL, '0', 33, '1', 'CPS备注(如禁海外推广)'),
('u2game', 'game', 'pic3', 'extra_cover_1', 'string', 'platform_data', NULL, NULL, '0', 34, '1', '额外封面图3'),
('u2game', 'game', 'pic4', 'extra_cover_2', 'string', 'platform_data', NULL, NULL, '0', 35, '1', '额外封面图4'),
('u2game', 'game', 'hw_firstpay', 'hw_first_discount', 'decimal', 'platform_data', NULL, NULL, '0', 36, '1', '海外首充折扣'),
('u2game', 'game', 'hw_otherpay', 'hw_other_discount', 'decimal', 'platform_data', NULL, NULL, '0', 37, '1', '海外续充折扣'),
('u2game', 'game', 'h5url', 'h5_url', 'string', 'platform_data', NULL, NULL, '0', 38, '1', 'H5游戏地址'),
('u2game', 'game', 'h5', 'is_h5', 'number', 'platform_data', NULL, NULL, '0', 39, '1', '是否H5游戏'),
('u2game', 'game', 'interchangeable_role', 'support_role_exchange', 'number', 'platform_data', NULL, NULL, '0', 40, '1', '是否支持角色互通'),
('u2game', 'game', 'yuyue', 'is_reservation', 'number', 'platform_data', NULL, NULL, '0', 41, '1', '是否预约'),
('u2game', 'game', 'gn_extension', 'has_gn_extension', 'number', 'platform_data', NULL, NULL, '0', 42, '1', '国内是否推广'),
('u2game', 'game', 'hw_extension', 'has_hw_extension', 'number', 'platform_data', NULL, NULL, '0', 43, '1', '海外是否推广'),
('u2game', 'game', 'version', 'version_code', 'number', 'platform_data', NULL, NULL, '0', 44, '1', '版本号'),
('u2game', 'game', 'edition', 'edition_type', 'number', 'platform_data', NULL, NULL, '0', 45, '1', '版本类型'),
('u2game', 'game', 'strict', 'is_strict', 'number', 'platform_data', NULL, NULL, '0', 46, '1', '是否严格模式'),
('u2game', 'game', 'userfc', 'user_fc', 'number', 'platform_data', NULL, NULL, '0', 47, '1', '用户FC标识'),
('u2game', 'game', 'cps_ratio', 'cps_ratio', 'decimal', 'platform_data', NULL, NULL, '0', 48, '1', 'CPS比例'),
('u2game', 'game', 'hw_ratio', 'hw_ratio', 'decimal', 'platform_data', NULL, NULL, '0', 49, '1', '海外比例'),
('u2game', 'game', 'coupon_repeat', 'coupon_repeat', 'string', 'platform_data', NULL, NULL, '0', 50, '1', '优惠券重复设置'),
('u2game', 'game', 'game_source', 'game_source', 'string', 'platform_data', NULL, NULL, '0', 51, '1', '游戏来源'),
('u2game', 'game', 'pic_xuanchuan', 'promo_pic', 'string', 'platform_data', NULL, NULL, '0', 52, '1', '宣传图');

-- ================================
-- 验证配置
-- ================================

-- 查询刚刚插入的配置数量
SELECT COUNT(*) as total_mappings 
FROM `gb_field_mappings` 
WHERE platform = 'u2game' AND resource_type = 'game' AND status = '1';

-- 查看配置详情（按target_location分组统计）
SELECT target_location, COUNT(*) as count
FROM `gb_field_mappings`
WHERE platform = 'u2game' AND resource_type = 'game' AND status = '1'
GROUP BY target_location;
