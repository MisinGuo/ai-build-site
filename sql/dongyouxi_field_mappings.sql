-- ========================================================
-- 巴兔游戏盒子字段映射配置（动游戏/dongyouxi平台）
-- Box ID: 9 (巴兔游戏)
-- 基于 dongyouxi_games.json 数据结构生成
-- 生成时间: 2026-03-08
--
-- 数据字段说明（中文字段名）：
--   游戏名          游戏名称
--   版本            版本/福利说明（如"0.1折极品侠客任选"，空值时无特殊说明）
--   游戏分类        游戏分类（英文点分隔多类，如"角色.玄幻"、"卡牌.武侠"）
--   折扣            折扣率（同277sy单位，10=原价，4.2=4.2折）
--   点位            推广点位/佣金比例（如"0%"，字符串）
--   图片链接        游戏图标URL
--   客户端          支持平台（"Android/IOS"/"Android"/"IOS"）
--   安卓端链接      安卓下载/游戏链接
--   IOS端链接       iOS下载/游戏链接（H5游戏时与安卓相同）
--   推广地址        推广落地页地址（gweb.tsyule.cn）
--
-- 注意：
--   1. 游戏类型字段用"."分隔多个分类，需拆分后分别映射
--   2. 平台没有独立的platform_game_id字段，可从推广地址提取游戏ID
--   3. 折扣单位同277sy（10=原价），需确认系统存储格式
-- ========================================================

-- 清理旧数据
DELETE FROM gb_box_field_mappings WHERE box_id = 9;

-- ========================================================
-- 主表字段映射 (target_location = 'main')
-- ========================================================

-- 游戏名称（必填）
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (9, 'game', '游戏名', 'name', 'string', 'main', '1', 'overwrite', 1, '1', '游戏名称');

-- 版本/福利说明→副标题
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (9, 'game', '版本', 'subtitle', 'string', 'main', '0', 'overwrite', 2, '1', '版本/福利说明（如"天天送1000充"，空值时无特殊福利）');

-- 版本/福利说明→折扣标签
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (9, 'game', '版本', 'discount_label', 'string', 'main', '0', 'overwrite', 3, '1', '折扣/福利标签（同版本字段）');

-- 游戏图标（必填）
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (9, 'game', '图片链接', 'icon_url', 'string', 'main', '1', 'overwrite', 4, '1', '游戏图标URL');

-- 游戏分类（点分隔多分类，regex_all 提取所有非点字符，逐项做值映射）
-- transform_expression: regex_all:[^.]+ → 将 "策略.国战" 匹配为 ["策略","国战"]，然后对每项做值映射
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark, transform_expression, value_mapping)
VALUES (9, 'game', '游戏分类', 'category_id', 'int', 'category_relation', '0', 'overwrite', 5, '1', '游戏分类（英文点分隔，regex_all提取后值映射）',
'regex_all:[^.]+',
'{"type":"direct","mappings":{"角色":{"value":"71","description":"角色扮演类"},"角色扮演":{"value":"71","description":"角色扮演"},"武侠":{"value":"80","description":"武侠江湖"},"仙侠":{"value":"81","description":"仙侠修真"},"玄幻":{"value":"68","description":"玄幻奇幻"},"魔幻":{"value":"82","description":"魔幻题材"},"三国":{"value":"85","description":"三国题材"},"西游":{"value":"77","description":"西游题材"},"二次元":{"value":"79","description":"二次元动漫"},"传奇":{"value":"80","description":"传奇武侠（归入武侠）"},"卡牌":{"value":"84","description":"卡牌策略"},"策略":{"value":"83","description":"策略战争"},"挂机":{"value":"78","description":"放置挂机"},"放置":{"value":"75","description":"放置挂机类"},"回合":{"value":"76","description":"回合制"},"割草":{"value":"70","description":"割草动作"},"动漫":{"value":"74","description":"动漫IP"},"动作":{"value":"67","description":"动作类（归入冒险）"},"休闲":{"value":"73","description":"休闲娱乐"},"冒险":{"value":"67","description":"冒险探索"},"养成":{"value":"66","description":"角色养成"},"经营":{"value":"65","description":"模拟经营"},"科幻":{"value":"69","description":"科幻未来"},"开箱":{"value":"84","description":"开箱抽卡（归入卡牌）"}}}');

-- 折扣（国内）
-- 注：dongyouxi折扣单位为"折"（10=原价，4.2=4.2折），同277sy
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (9, 'game', '折扣', 'first_charge_domestic', 'decimal', 'main', '0', 'overwrite', 6, '1', '游戏折扣（单位：折，10=原价）');

-- 设备支持类型
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark, value_mapping)
VALUES (9, 'game', '客户端', 'device_support', 'string', 'main', '0', 'overwrite', 7, '1', '支持平台（Android/IOS→both，Android→android，IOS→ios）',
'{"type":"direct","mappings":{"Android/IOS":"both","Android/iOS":"both","Android":"android","IOS":"ios","iOS":"ios","安卓/苹果":"both","安卓":"android","苹果":"ios"}}');

-- 推广地址→主链接
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (9, 'game', '推广地址', 'download_url', 'string', 'main', '0', 'overwrite', 8, '1', '推广落地页地址（用作主链接）');

-- ========================================================
-- 推广链接映射 (target_location = 'promotion_link')
-- ========================================================

-- 安卓下载/游戏进入链接
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (9, 'game', '安卓端链接', 'download_url', 'string', 'promotion_link', '1', 'overwrite', 50, '1', '安卓下载/进入链接');

-- iOS下载/游戏进入链接
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (9, 'game', 'IOS端链接', 'ios_url', 'string', 'promotion_link', '0', 'overwrite', 51, '1', 'iOS下载/进入链接');

-- 推广落地页
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (9, 'game', '推广地址', 'web_url', 'string', 'promotion_link', '0', 'overwrite', 52, '1', '推广落地页地址（gweb.tsyule.cn）');

-- 备用安卓链接（chaoai168.com域名）
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (9, 'game', '安卓端链接2', 'android_url_alt', 'string', 'promotion_link', '0', 'overwrite', 53, '1', '备用安卓链接（chaoai168.com）');

-- 备用iOS链接（chaoai168.com域名）
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (9, 'game', 'IOS端链接2', 'ios_url_alt', 'string', 'promotion_link', '0', 'overwrite', 54, '1', '备用iOS链接（chaoai168.com）');

-- 备用推广落地页（chaoai168.com域名）
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (9, 'game', '推广地址2', 'web_url_alt', 'string', 'promotion_link', '0', 'overwrite', 55, '1', '备用推广落地页（gweb.chaoai168.com）');

-- ========================================================
-- 关联表字段映射 (target_location = 'relation')
-- ========================================================

-- 折扣 → 关联表首充折扣
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (9, 'game', '折扣', 'first_charge_domestic', 'decimal', 'relation', '0', 'overwrite', 101, '1', '关联表-首充折扣国内');

-- ========================================================
-- 平台数据字段映射 (target_location = 'platform_data')
-- ========================================================

-- 推广点位/佣金比例
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (9, 'game', '点位', 'commission_rate', 'string', 'platform_data', '0', 'overwrite', 200, '1', '推广佣金点位比例（如"0%"）');

-- 游戏类型原始值（保留原始字段便于追溯）
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (9, 'game', '游戏分类', 'genre_raw', 'string', 'platform_data', '0', 'overwrite', 201, '1', '游戏分类原始值（点分隔，如"角色.传奇.动作"）');

-- 版本原始值（保留）
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (9, 'game', '版本', 'version_raw', 'string', 'platform_data', '0', 'overwrite', 202, '1', '版本/福利原始值');

-- 平台游戏ID（用于平台数据追溯与去重）
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (9, 'game', 'gameid', 'platform_game_id', 'string', 'platform_data', '0', 'overwrite', 203, '1', '平台游戏ID（如"15963"）');

-- 手游类型 → 游戏类型（官方手游→official，变态手游/bt服→bt）
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark, value_mapping)
VALUES (9, 'game', '手游类型', 'game_type', 'string', 'main', '0', 'overwrite', 9, '1', '游戏类型（official-官方 discount-折扣 bt-BT版）',
'{"type":"direct","mappings":{"官方手游":"official","官服":"official","H5游戏":"h5","h5游戏":"h5","折扣手游":"discount","折扣服":"discount","变态手游":"bt","变态服":"bt","bt手游":"bt","BT手游":"bt"}}');

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
WHERE box_id = 9
ORDER BY target_location, sort_order;

-- 统计映射数量
SELECT
    target_location,
    COUNT(*) AS mapping_count,
    SUM(CASE WHEN is_required = '1' THEN 1 ELSE 0 END) AS required_count
FROM gb_box_field_mappings
WHERE box_id = 9
GROUP BY target_location;


