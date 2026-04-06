-- ========================================================
-- 嘿咕游戏盒子字段映射配置（米鹿/Milu平台）
-- Box ID: 8 (嘿咕游戏)
-- 基于 milu_games.json 数据结构生成
-- 生成时间: 2026-03-08
--
-- 数据字段说明：
--   game_name           游戏名称
--   name_remark         名称备注（折扣/福利说明，如"0.1折送满星小乔"）
--   game_icon           游戏图标URL
--   game_genre          游戏类型（JSON数组，如["卡牌","三国"]）
--   category            游戏分类大类（"专服"/"折扣"/"免费版"等）
--   discount_info       折扣信息字符串（如"0.1折"）
--   support_platform    支持平台（"双端"/"安卓"/"苹果"）
--   ios_support         是否支持iOS（true/false）
--   release_time        上线时间（"2026-03-17 08:00"格式）
--   android_download_link  安卓下载链接
--   ios_download_link      iOS下载链接（ios_support=true时有值）
--   promotion_page_url     游戏推广页地址
--   multi_function_link    多功能链接（注册/下载通用）
--   has_profession_tag     是否有职业标签（true/false）
--   material_package_url   素材包ZIP下载地址
--
-- 注意：category字段映射game_type：
--       "专服"→"bt"（极变态服/BT服）
--       "折扣"→"discount"（折扣游戏）
--       "免费版"→"official"（免费/官方）
-- ========================================================

-- 清理旧数据
DELETE FROM gb_box_field_mappings WHERE box_id = 8;

-- ========================================================
-- 主表字段映射 (target_location = 'main')
-- ========================================================

-- 游戏名称（必填）
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (8, 'game', 'game_name', 'name', 'string', 'main', '1', 'overwrite', 1, '1', '游戏名称');

-- 副标题（name_remark，福利说明）
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (8, 'game', 'name_remark', 'subtitle', 'string', 'main', '0', 'overwrite', 2, '1', '游戏副标题/名称备注（如"0.1折送满星小乔"）');

-- 游戏短名称
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (8, 'game', 'game_name', 'short_name', 'string', 'main', '0', 'overwrite', 3, '1', '游戏短名称（同game_name）');

-- 折扣标签（name_remark）
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (8, 'game', 'name_remark', 'discount_label', 'string', 'main', '0', 'overwrite', 4, '1', '折扣/福利标签（name_remark）');

-- 游戏图标（必填）
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (8, 'game', 'game_icon', 'icon_url', 'string', 'main', '1', 'overwrite', 5, '1', '游戏图标URL');

-- 游戏分类（JSON数组，引擎自动识别数组类型逐项映射，无需 transform_expression）
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark, value_mapping)
VALUES (8, 'game', 'game_genre', 'category_id', 'int', 'category_relation', '0', 'overwrite', 6, '1', '游戏分类（JSON数组["卡牌","三国"]，多值分别映射分类关联）',
'{"type":"direct","mappings":{"仙侠":{"value":"81","description":"仙侠修真类"},"魔幻":{"value":"82","description":"魔幻题材"},"二次元":{"value":"79","description":"二次元动漫"},"武侠":{"value":"80","description":"武侠江湖"},"三国":{"value":"85","description":"三国题材"},"西游":{"value":"77","description":"西游题材"},"卡牌":{"value":"84","description":"卡牌策略"},"策略":{"value":"83","description":"策略战争"},"挂机":{"value":"78","description":"放置挂机"},"放置":{"value":"75","description":"放置挂机类"},"回合":{"value":"76","description":"回合制"},"割草":{"value":"70","description":"割草动作"},"动漫":{"value":"74","description":"动漫IP"},"休闲":{"value":"73","description":"休闲娱乐"},"冒险":{"value":"67","description":"冒险探索"},"养成":{"value":"66","description":"角色养成"},"经营":{"value":"65","description":"模拟经营"},"玄幻":{"value":"68","description":"玄幻奇幻"},"科幻":{"value":"69","description":"科幻未来"},"角色":{"value":"71","description":"角色扮演（短名）"},"角色扮演":{"value":"71","description":"角色扮演"},"修仙":{"value":"81","description":"修仙仙侠类"},"传奇":{"value":"80","description":"传奇武侠（归入武侠）"},"动作":{"value":"67","description":"动作类（归入冒险）"},"开箱":{"value":"84","description":"开箱抽卡（归入卡牌）"}}}');

-- 游戏类型（大类→game_type）
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark, value_mapping)
VALUES (8, 'game', 'category', 'game_type', 'string', 'main', '0', 'overwrite', 7, '1', '游戏大类映射到游戏类型（专服→bt，折扣→discount，免费版→official）',
'{"type":"direct","mappings":{"专服":{"value":"bt","description":"BT变态服/极折服"},"折扣":{"value":"discount","description":"折扣游戏"},"免费版":{"value":"official","description":"免费/官方版"},"官方":{"value":"official","description":"官方版"}}}');

-- 支持平台→设备支持
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark, value_mapping)
VALUES (8, 'game', 'support_platform', 'device_support', 'string', 'main', '0', 'overwrite', 8, '1', '支持平台（双端/安卓/苹果→both/android/ios）',
'{"type":"direct","mappings":{"双端":"both","安卓":"android","苹果":"ios","Android":"android","iOS":"ios","Android/iOS":"both"}}');

-- 安卓下载链接→主下载地址
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (8, 'game', 'android_download_link', 'download_url', 'string', 'main', '0', 'overwrite', 9, '1', '安卓下载链接（主下载地址）');

-- 上线时间
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (8, 'game', 'release_time', 'launch_time', 'datetime', 'main', '0', 'overwrite', 10, '1', '游戏上线时间（格式"2026-03-17 08:00"）');

-- ========================================================
-- 推广链接映射 (target_location = 'promotion_link')
-- ========================================================

-- 安卓下载链接
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (8, 'game', 'android_download_link', 'download_url', 'string', 'promotion_link', '1', 'overwrite', 50, '1', '安卓下载链接');

-- iOS下载链接
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (8, 'game', 'ios_download_link', 'ios_url', 'string', 'promotion_link', '0', 'overwrite', 51, '1', 'iOS下载链接（ios_support=true时有值）');

-- 游戏推广页
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (8, 'game', 'promotion_page_url', 'web_url', 'string', 'promotion_link', '0', 'overwrite', 52, '1', '游戏推广落地页地址');

-- 多功能链接（注册/下载通用）
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (8, 'game', 'multi_function_link', 'multi_url', 'string', 'promotion_link', '0', 'overwrite', 53, '1', '多功能链接（app.heigu.com，注册下载通用）');

-- ========================================================
-- 平台数据字段映射 (target_location = 'platform_data')
-- ========================================================

-- 折扣信息字符串（原始）
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (8, 'game', 'discount_info', 'discount_info', 'string', 'platform_data', '0', 'overwrite', 200, '1', '折扣信息原始字符串（如"0.1折"）');

-- 游戏大类（原始）
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (8, 'game', 'category', 'category_raw', 'string', 'platform_data', '0', 'overwrite', 201, '1', '游戏大类原始值（专服/折扣/免费版等）');

-- 上线时间（字符串保留）
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (8, 'game', 'release_time', 'release_time', 'string', 'platform_data', '0', 'overwrite', 202, '1', '上线时间字符串');

-- 是否支持iOS
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (8, 'game', 'ios_support', 'is_ios_support', 'boolean', 'platform_data', '0', 'overwrite', 203, '1', '是否支持iOS下载');

-- 是否有职业标签
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (8, 'game', 'has_profession_tag', 'has_profession_tag', 'boolean', 'platform_data', '0', 'overwrite', 204, '1', '是否有职业标签');

-- 素材包下载地址
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (8, 'game', 'material_package_url', 'material_package_url', 'string', 'platform_data', '0', 'overwrite', 205, '1', '推广素材包ZIP下载地址');

-- ========================================================
-- 查询验证
-- ========================================================
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
WHERE box_id = 8
ORDER BY target_location, sort_order;

-- 统计映射数量
SELECT
    target_location,
    COUNT(*) AS mapping_count,
    SUM(CASE WHEN is_required = '1' THEN 1 ELSE 0 END) AS required_count
FROM gb_box_field_mappings
WHERE box_id = 8
GROUP BY target_location;
