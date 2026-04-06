-- ================================================================
-- GameBox v3.32 - 为字段映射配置添加中文描述（remark字段）
-- 功能说明：
-- 1. 为u2game平台的字段映射配置添加友好的中文描述
-- 2. 这些描述会在前端展示时显示，替代英文字段名
-- 
-- 执行方式：
-- mysql -u root -p123456 ry-vue < gamebox_v3.32_add_field_mapping_remarks.sql
-- ================================================================

USE `ry-vue`;

-- 更新推广链接字段的中文描述
UPDATE gb_field_mappings SET remark = '网页地址' WHERE platform = 'u2game' AND target_field = 'web_url';
UPDATE gb_field_mappings SET remark = '下载地址' WHERE platform = 'u2game' AND target_field = 'download_url';
UPDATE gb_field_mappings SET remark = '下载二维码' WHERE platform = 'u2game' AND target_field = 'qrcode_data';
UPDATE gb_field_mappings SET remark = '网页二维码' WHERE platform = 'u2game' AND target_field = 'web_qrcode_data';
UPDATE gb_field_mappings SET remark = '短链下载地址' WHERE platform = 'u2game' AND target_field = 'short_download_url';
UPDATE gb_field_mappings SET remark = '掩码下载地址' WHERE platform = 'u2game' AND target_field = 'mask_download_url';
UPDATE gb_field_mappings SET remark = '短链掩码下载地址' WHERE platform = 'u2game' AND target_field = 'short_mask_download_url';
UPDATE gb_field_mappings SET remark = '复制文案' WHERE platform = 'u2game' AND target_field = 'copy_text';

-- 更新平台数据字段的中文描述
UPDATE gb_field_mappings SET remark = '是否H5游戏' WHERE platform = 'u2game' AND target_field = 'is_h5';
UPDATE gb_field_mappings SET remark = 'H5游戏链接' WHERE platform = 'u2game' AND target_field = 'h5_url';
UPDATE gb_field_mappings SET remark = '福利信息' WHERE platform = 'u2game' AND target_field = 'welfare_info';
UPDATE gb_field_mappings SET remark = '是否可互通角色' WHERE platform = 'u2game' AND target_field = 'is_interchangeable';
UPDATE gb_field_mappings SET remark = 'CPS说明文本' WHERE platform = 'u2game' AND target_field = 'cps_text';
UPDATE gb_field_mappings SET remark = '是否新游' WHERE platform = 'u2game' AND target_field = 'is_new';
UPDATE gb_field_mappings SET remark = '是否预约' WHERE platform = 'u2game' AND target_field = 'is_reservation';
UPDATE gb_field_mappings SET remark = '添加时间' WHERE platform = 'u2game' AND target_field = 'add_time';
UPDATE gb_field_mappings SET remark = '更新时间' WHERE platform = 'u2game' AND target_field = 'update_time';
UPDATE gb_field_mappings SET remark = '国内推广' WHERE platform = 'u2game' AND target_field = 'gn_extension';
UPDATE gb_field_mappings SET remark = '海外推广' WHERE platform = 'u2game' AND target_field = 'hw_extension';
UPDATE gb_field_mappings SET remark = 'CPS推广信息' WHERE platform = 'u2game' AND target_field = 'cps_info';
UPDATE gb_field_mappings SET remark = '海外CPS信息' WHERE platform = 'u2game' AND target_field = 'hw_cps_info';
UPDATE gb_field_mappings SET remark = '优惠券重复信息' WHERE platform = 'u2game' AND target_field = 'coupon_repeat_info';
UPDATE gb_field_mappings SET remark = '游戏版本' WHERE platform = 'u2game' AND target_field = 'game_version';
UPDATE gb_field_mappings SET remark = '客户端版本' WHERE platform = 'u2game' AND target_field = 'client_version';
UPDATE gb_field_mappings SET remark = '设备类型' WHERE platform = 'u2game' AND target_field = 'device_type';
UPDATE gb_field_mappings SET remark = '版本号' WHERE platform = 'u2game' AND target_field = 'edition';
UPDATE gb_field_mappings SET remark = '宣传图片' WHERE platform = 'u2game' AND target_field = 'promotion_image';
UPDATE gb_field_mappings SET remark = '是否严格模式' WHERE platform = 'u2game' AND target_field = 'is_strict';
UPDATE gb_field_mappings SET remark = '用户FC' WHERE platform = 'u2game' AND target_field = 'user_fc';
UPDATE gb_field_mappings SET remark = 'CPS比例' WHERE platform = 'u2game' AND target_field = 'cps_ratio';
UPDATE gb_field_mappings SET remark = '海外比例' WHERE platform = 'u2game' AND target_field = 'hw_ratio';
UPDATE gb_field_mappings SET remark = '海外首充比例' WHERE platform = 'u2game' AND target_field = 'hw_first_pay';
UPDATE gb_field_mappings SET remark = '海外其他充值比例' WHERE platform = 'u2game' AND target_field = 'hw_other_pay';
UPDATE gb_field_mappings SET remark = '首充比例' WHERE platform = 'u2game' AND target_field = 'first_pay';
UPDATE gb_field_mappings SET remark = '其他充值比例' WHERE platform = 'u2game' AND target_field = 'other_pay';
UPDATE gb_field_mappings SET remark = '游戏来源' WHERE platform = 'u2game' AND target_field = 'game_source';

-- 验证更新结果
SELECT 
    '已更新remark的字段统计' as info,
    target_location,
    COUNT(*) as total_count,
    SUM(CASE WHEN remark IS NOT NULL AND remark != '' THEN 1 ELSE 0 END) as has_remark_count
FROM gb_field_mappings
WHERE platform = 'u2game' AND status = '1'
GROUP BY target_location;

-- 显示部分更新后的记录（用于验证）
SELECT 
    target_field,
    target_location,
    remark
FROM gb_field_mappings
WHERE platform = 'u2game' AND status = '1'
ORDER BY target_location, target_field
LIMIT 20;
