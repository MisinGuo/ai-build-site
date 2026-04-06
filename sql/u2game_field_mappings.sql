-- ========================================================
-- U2Game盒子字段映射配置
-- Box ID: 3 (U2Game盒子)
-- 基于 all_games.json 数据结构生成
-- 生成时间: 2026-01-27
-- 
-- 使用说明：
-- 1. 分类映射：gametype字段映射到category_id，需要在管理界面配置值映射
--    例如：{"type":"direct","mappings":{"卡牌":"1","角色扮演":"2"}}
-- 2. 推广链接：downurl等字段映射到promotion_link表，会保存为JSON
-- 3. 平台数据：其他扩展字段保存到platform_data
-- ========================================================

-- 清理旧数据（如果存在）
DELETE FROM gb_box_field_mappings WHERE box_id = 3;

-- ========================================================
-- 主表字段映射 (target_location = 'main')
-- ========================================================

-- 游戏名称 (必填)
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, sort_order, status, remark) 
VALUES (3, 'game', 'gamename', 'name', 'string', 'main', '1', 1, '1', '游戏名称');

-- 游戏副标题/版本说明
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, sort_order, status, remark) 
VALUES (3, 'game', 'welfare', 'subtitle', 'string', 'main', '0', 2, '1', '游戏福利/副标题');

-- 游戏图标 (必填)
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, sort_order, status, remark) 
VALUES (3, 'game', 'pic1', 'icon_url', 'string', 'main', '1', 3, '1', '游戏图标URL');

-- 游戏封面图
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, sort_order, status, remark) 
VALUES (3, 'game', 'pic2', 'cover_url', 'string', 'main', '0', 4, '1', '游戏封面图/横幅图');

-- 游戏分类 (gametype字段映射到category_id，需要配置值映射将分类名称映射为分类ID)
-- 注意：请根据实际的分类ID修改value_mapping中的映射关系
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, sort_order, status, remark, value_mapping) 
VALUES (3, 'game', 'gametype', 'category_id', 'int', 'main', '0', 5, '1', '游戏分类（需配置值映射）', '{"type":"direct","mappings":{"仙侠":{"value":"81","description":"仙侠修真类游戏"},"魔幻":{"value":"82","description":"魔幻题材游戏"},"二次元":{"value":"79","description":"二次元动漫风格游戏"},"武侠":{"value":"80","description":"武侠江湖类游戏"},"三国":{"value":"85","description":"三国题材游戏"},"西游":{"value":"77","description":"西游记题材游戏"},"卡牌":{"value":"84","description":"卡牌策略游戏"},"策略":{"value":"83","description":"策略战争游戏"},"挂机":{"value":"78","description":"放置挂机游戏"},"经营":{"value":"65","description":"模拟经营类游戏"},"养成":{"value":"66","description":"角色养成类游戏"},"冒险":{"value":"67","description":"冒险探索类游戏"},"玄幻":{"value":"68","description":"玄幻奇幻类游戏"},"科幻":{"value":"69","description":"科幻未来类游戏"},"割草":{"value":"70","description":"割草动作类游戏"},"角色扮演":{"value":"71","description":"角色扮演类游戏"},"官方":{"value":"72","description":"官方正版游戏"},"休闲":{"value":"73","description":"休闲娱乐类游戏"},"动漫":{"value":"74","description":"动漫IP改编游戏"},"放置":{"value":"75","description":"放置挂机类游戏"},"回合":{"value":"76","description":"回合制游戏"}}}');

-- 游戏类型 (edition: 0=官方, 1=折扣, 2=变态; yuyue=1时为即将上线)
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, sort_order, status, remark, value_mapping) 
VALUES (3, 'game', 'edition', 'game_type', 'string', 'main', '0', 6, '1', '游戏类型（版次）', '{"type":"direct","mappings":{"0":"official","1":"discount","2":"bt"}}');

-- 游戏描述
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, sort_order, status, remark) 
VALUES (3, 'game', 'excerpt', 'description', 'string', 'main', '0', 7, '1', '游戏描述');

-- 设备支持类型 (device_type: 0=双端, 1=安卓, 2=iOS)
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, sort_order, status, remark, value_mapping) 
VALUES (3, 'game', 'device_type', 'device_support', 'string', 'main', '0', 8, '1', '设备支持类型', '{"type":"direct","mappings":{"0":"双端","1":"安卓","2":"iOS"}}');

-- 首充折扣-国内
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, sort_order, status, remark) 
VALUES (3, 'game', 'firstpay', 'first_charge_domestic', 'decimal', 'main', '0', 9, '1', '首充折扣-国内');

-- 续充折扣-国内
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, sort_order, status, remark) 
VALUES (3, 'game', 'otherpay', 'recharge_domestic', 'decimal', 'main', '0', 10, '1', '续充折扣-国内');

-- 首充折扣-海外
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, sort_order, status, remark) 
VALUES (3, 'game', 'hw_firstpay', 'first_charge_overseas', 'decimal', 'main', '0', 11, '1', '首充折扣-海外');

-- 续充折扣-海外
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, sort_order, status, remark) 
VALUES (3, 'game', 'hw_otherpay', 'recharge_overseas', 'decimal', 'main', '0', 12, '1', '续充折扣-海外');

-- ========================================================
-- 推广链接映射 (target_location = 'promotion_link')
-- ========================================================

-- 主下载链接
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, sort_order, status, remark) 
VALUES (3, 'game', 'downurl', 'download_url', 'string', 'promotion_link', '1', 50, '1', '游戏下载链接');

-- 短链接下载地址
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, sort_order, status, remark) 
VALUES (3, 'game', 'dwzdownurl', 'short_url', 'string', 'promotion_link', '0', 51, '1', '短链接下载地址');

-- Mash下载链接
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, sort_order, status, remark) 
VALUES (3, 'game', 'downurlmash', 'mash_url', 'string', 'promotion_link', '0', 52, '1', 'Mash版本下载链接');

-- 短链接Mash下载地址
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, sort_order, status, remark) 
VALUES (3, 'game', 'dwzdownurlmash', 'short_mash_url', 'string', 'promotion_link', '0', 53, '1', '短链接Mash下载地址');

-- 网页游戏地址
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, sort_order, status, remark) 
VALUES (3, 'game', 'weburl', 'web_url', 'string', 'promotion_link', '0', 54, '1', '游戏网页地址');

-- H5游戏地址
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, sort_order, status, remark) 
VALUES (3, 'game', 'h5url', 'h5_url', 'string', 'promotion_link', '0', 55, '1', 'H5游戏地址');

-- ========================================================
-- 平台数据字段映射 (target_location = 'platform_data')
-- ========================================================

-- 游戏ID (平台唯一标识)
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, sort_order, status, remark) 
VALUES (3, 'game', 'id', 'platform_game_id', 'string', 'platform_data', '1', 100, '1', 'U2Game平台游戏ID');

-- 二维码 (Base64)
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, sort_order, status, remark) 
VALUES (3, 'game', 'qrcode', 'qrcode_base64', 'string', 'platform_data', '0', 107, '1', '下载二维码(Base64)');

-- 网页地址二维码
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, sort_order, status, remark) 
VALUES (3, 'game', 'weburl_qr', 'weburl_qrcode_base64', 'string', 'platform_data', '0', 108, '1', '网页地址二维码(Base64)');

-- 视频地址
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, sort_order, status, remark) 
VALUES (3, 'game', 'video', 'video_url', 'string', 'platform_data', '0', 109, '1', '游戏宣传视频URL');

-- 宣传图片
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, sort_order, status, remark) 
VALUES (3, 'game', 'pic_xuanchuan', 'promo_image_url', 'string', 'platform_data', '0', 110, '1', '宣传图片URL');

-- 游戏截图3
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, sort_order, status, remark) 
VALUES (3, 'game', 'pic3', 'screenshot1', 'string', 'platform_data', '0', 111, '1', '游戏截图1');

-- 游戏截图4
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, sort_order, status, remark) 
VALUES (3, 'game', 'pic4', 'screenshot2', 'string', 'platform_data', '0', 112, '1', '游戏截图2');

-- 游戏截图数组 (photo)
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, sort_order, status, remark) 
VALUES (3, 'game', 'photo', 'screenshots', 'json', 'platform_data', '0', 113, '1', '游戏截图数组');

-- 盒子福利内容
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, sort_order, status, remark) 
VALUES (3, 'game', 'box_content', 'box_welfare_content', 'string', 'platform_data', '0', 114, '1', '盒子专属福利内容');

-- 复制文案
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, sort_order, status, remark) 
VALUES (3, 'game', 'copy', 'copy_text', 'string', 'platform_data', '0', 115, '1', '完整复制文案');

-- 游戏来源
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, sort_order, status, remark) 
VALUES (3, 'game', 'game_source', 'game_source', 'string', 'platform_data', '0', 116, '1', '游戏来源');

-- 版本号
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, sort_order, status, remark) 
VALUES (3, 'game', 'version', 'version', 'int', 'platform_data', '0', 117, '1', '版本号');

-- C版本号
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, sort_order, status, remark) 
VALUES (3, 'game', 'c_version', 'c_version', 'int', 'platform_data', '0', 118, '1', 'C版本号');

-- 是否H5游戏
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, sort_order, status, remark) 
VALUES (3, 'game', 'h5', 'is_h5', 'boolean', 'platform_data', '0', 120, '1', '是否H5游戏');

-- 是否新游
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, sort_order, status, remark) 
VALUES (3, 'game', 'new', 'is_new', 'boolean', 'platform_data', '0', 121, '1', '是否新游');

-- 是否预约
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, sort_order, status, remark) 
VALUES (3, 'game', 'yuyue', 'is_reservation', 'boolean', 'platform_data', '0', 122, '1', '是否预约游戏');

-- 是否可互换角色
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, sort_order, status, remark) 
VALUES (3, 'game', 'interchangeable_role', 'interchangeable_role', 'boolean', 'platform_data', '0', 123, '1', '是否可互换角色');

-- 是否严格审核
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, sort_order, status, remark) 
VALUES (3, 'game', 'strict', 'is_strict', 'boolean', 'platform_data', '0', 124, '1', '是否严格审核');

-- 是否用户FC
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, sort_order, status, remark) 
VALUES (3, 'game', 'userfc', 'user_fc', 'boolean', 'platform_data', '0', 125, '1', '是否用户FC');

-- 添加时间
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, sort_order, status, remark) 
VALUES (3, 'game', 'addtime', 'add_time', 'string', 'platform_data', '0', 126, '1', '添加时间');

-- 更新时间
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, sort_order, status, remark) 
VALUES (3, 'game', 'updatetime', 'update_time', 'string', 'platform_data', '0', 127, '1', '更新时间');

-- CPS/CPA文本说明
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, sort_order, status, remark) 
VALUES (3, 'game', 'text_cps', 'cps_text', 'string', 'platform_data', '0', 128, '1', 'CPS推广文本说明');

-- CPS/CPA推广信息
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, sort_order, status, remark) 
VALUES (3, 'game', 'cps_cpa', 'cps_cpa_info', 'string', 'platform_data', '0', 129, '1', 'CPS/CPA推广信息');

-- 海外CPS/CPA推广信息
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, sort_order, status, remark) 
VALUES (3, 'game', 'hw_cps_cpa', 'hw_cps_cpa_info', 'string', 'platform_data', '0', 130, '1', '海外CPS/CPA推广信息');

-- 是否国内扩展
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, sort_order, status, remark) 
VALUES (3, 'game', 'gn_extension', 'gn_extension', 'boolean', 'platform_data', '0', 131, '1', '是否国内扩展');

-- 是否海外扩展
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, sort_order, status, remark) 
VALUES (3, 'game', 'hw_extension', 'hw_extension', 'boolean', 'platform_data', '0', 132, '1', '是否海外扩展');

-- 优惠券重复说明
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, sort_order, status, remark) 
VALUES (3, 'game', 'coupon_repeat', 'coupon_repeat', 'string', 'platform_data', '0', 133, '1', '优惠券重复说明');

-- CPS比例
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, sort_order, status, remark) 
VALUES (3, 'game', 'cps_ratio', 'cps_ratio', 'decimal', 'platform_data', '0', 134, '1', 'CPS比例');

-- 海外比例
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, sort_order, status, remark) 
VALUES (3, 'game', 'hw_ratio', 'hw_ratio', 'decimal', 'platform_data', '0', 135, '1', '海外比例');

-- ========================================================
-- 查询验证
-- ========================================================
-- 查看插入的字段映射记录
SELECT 
    id,
    source_field,
    target_field,
    field_type,
    target_location,
    is_required,
    sort_order,
    remark
FROM gb_box_field_mappings 
WHERE box_id = 3
ORDER BY target_location, sort_order;

-- 统计映射数量
SELECT 
    target_location,
    COUNT(*) as mapping_count,
    SUM(CASE WHEN is_required = '1' THEN 1 ELSE 0 END) as required_count
FROM gb_box_field_mappings 
WHERE box_id = 3
GROUP BY target_location;
