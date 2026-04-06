-- ========================================================
-- 277游戏盒子字段映射配置
-- Box ID: 7 (277游戏)
-- 基于 277sy_games.json 数据结构生成
-- 生成时间: 2026-03-08
--
-- 数据字段说明：
--   gameid          游戏唯一ID
--   gamename        游戏名称（含括号福利说明，如"烽火大唐H5"）
--   game_intro      游戏简介（简短，如"神话江湖，指尖问道"）
--   gameicon        游戏图标URL
--   banner_pic      横幅/封面图URL
--   gamedes         游戏详细描述
--   genre_name      游戏类型（竖线分隔，如"角色|武侠"）
--   is_notbtgame    是否非变态游戏（0=变态游戏，1=正常游戏）
--   client_type     客户端类型（1=安卓，2=iOS，3=双端）
--   discount        折扣（10为原价，4.2=4.2折，单位为"折"）
--   hw_discount     海外折扣
--   video_url       宣传视频URL
--   video_pic       视频封面图
--   apk_url         安卓下载/游戏链接
--   ios_url         iOS下载/游戏链接
--   gameinfo_url    游戏详情页推广链接
--   screenshot1-5   游戏截图
--   gameshort       游戏短标识
--   game_online_time 上线时间（Unix时间戳）
--   remarks2        推广说明（如"禁海外推广"）
--   cloud_game      是否云游戏（0/1）
--   role_interflow  是否可跨号

-- 注意：discount字段单位为"折"（0~10），若系统存储为0~1小数，需配置
--       transform_expression: value/10
-- ========================================================

-- 清理旧数据
DELETE FROM gb_box_field_mappings WHERE box_id = 7;

-- ========================================================
-- 主表字段映射 (target_location = 'main')
-- ========================================================

-- 游戏名称
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'gamename', 'name', 'string', 'main', '1', 'overwrite', 1, '1', '游戏名称（含括号内福利说明）');

-- 游戏副标题（从gamename括号内提取，如需提取请配置transform_expression）
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'gamename', 'subtitle', 'string', 'main', '0', 'overwrite', 2, '1', '副标题（同gamename，可用transform提取括号内的福利文字）');

-- 游戏短名称（去掉括号内福利说明的游戏主名）
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'gamename', 'short_name', 'string', 'main', '0', 'overwrite', 3, '1', '游戏短名（可用transform提取括号前的游戏名）');

-- 游戏简介→折扣标签
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'game_intro', 'discount_label', 'string', 'main', '0', 'overwrite', 4, '1', '游戏简介（用作折扣/福利标签展示）');

-- 游戏图标
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'gameicon', 'icon_url', 'string', 'main', '1', 'overwrite', 5, '1', '游戏图标URL');

-- 游戏封面/横幅（banner_pic）
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'banner_pic', 'cover_url', 'string', 'main', '0', 'overwrite', 6, '1', '游戏封面图URL（横幅/banner）');

INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'banner_pic', 'banner_url', 'string', 'main', '0', 'overwrite', 7, '1', '游戏横幅URL（banner）');

-- 游戏描述
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'gamedes', 'description', 'string', 'main', '0', 'overwrite', 8, '1', '游戏详情描述');

-- 分类（genre_name，竖线分隔多分类，regex_all 提取所有非竖线字符，逐项做值映射）
-- transform_expression: regex_all:[^|]+ → 将 "挂机|传奇" 匹配为 ["挂机","传奇"]，然后对每项做值映射
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark, transform_expression, value_mapping)
VALUES (7, 'game', 'genre_name', 'category_id', 'int', 'category_relation', '0', 'overwrite', 9, '1', '游戏分类（竖线分隔，regex_all提取后值映射）',
'regex_all:[^|]+',
'{"type":"direct","mappings":{"角色":{"value":"71","description":"角色扮演类"},"角色扮演":{"value":"71","description":"角色扮演类"},"武侠":{"value":"80","description":"武侠江湖类"},"仙侠":{"value":"81","description":"仙侠修真类"},"玄幻":{"value":"68","description":"玄幻奇幻类"},"魔幻":{"value":"82","description":"魔幻题材"},"三国":{"value":"85","description":"三国题材"},"西游":{"value":"77","description":"西游题材"},"二次元":{"value":"79","description":"二次元动漫"},"卡牌":{"value":"84","description":"卡牌策略"},"策略":{"value":"83","description":"策略战争"},"挂机":{"value":"78","description":"放置挂机"},"放置":{"value":"75","description":"放置挂机类"},"回合":{"value":"76","description":"回合制"},"割草":{"value":"70","description":"割草动作"},"动漫":{"value":"74","description":"动漫IP"},"休闲":{"value":"73","description":"休闲娱乐"},"冒险":{"value":"67","description":"冒险探索"},"养成":{"value":"66","description":"角色养成"},"经营":{"value":"65","description":"模拟经营"},"科幻":{"value":"69","description":"科幻未来"},"传奇":{"value":"80","description":"传奇武侠类（归入武侠）"},"动作":{"value":"67","description":"动作冒险（归入冒险）"},"开箱":{"value":"84","description":"开箱抽卡（归入卡牌）"}}}');

-- 游戏类型（是否变态服）
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark, value_mapping)
VALUES (7, 'game', 'is_notbtgame', 'game_type', 'string', 'main', '0', 'overwrite', 10, '1', '是否变态游戏（0=变态服bt，1=正常服discount）',
'{"type":"direct","mappings":{"0":{"value":"bt","description":"变态服/极折服"},"1":{"value":"discount","description":"折扣游戏"}}}');

-- 设备支持类型
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark, value_mapping)
VALUES (7, 'game', 'client_type', 'device_support', 'string', 'main', '0', 'overwrite', 11, '1', '设备支持类型（1=安卓，2=iOS，3=双端）',
'{"type":"direct","mappings":{"1":"android","2":"ios","3":"both"}}');

-- 折扣（国内）
-- 注：277sy单位为"折"（10=原价，4.2=4.2折）。若系统用小数存储（4.2折=0.42），需配置 transform_expression: value/10
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'discount', 'first_charge_domestic', 'decimal', 'main', '0', 'overwrite', 12, '1', '首充折扣-国内（单位：折，10=原价）');

-- 折扣（海外）
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'hw_discount', 'first_charge_overseas', 'decimal', 'main', '0', 'overwrite', 13, '1', '首充折扣-海外（单位：折，0为不支持海外）');

-- 宣传视频
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'video_url', 'video_url', 'string', 'main', '0', 'overwrite', 14, '1', '游戏宣传视频URL');

-- 安卓下载链接→主下载地址
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'apk_url', 'download_url', 'string', 'main', '0', 'overwrite', 15, '1', '游戏下载/进入链接（安卓/H5）');

-- 上线时间（Unix时间戳，需转换）
-- 注：game_online_time 为Unix时间戳（秒），如需datetime格式可配置transform_expression: FROM_UNIXTIME(value)
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'game_online_time', 'launch_time', 'datetime', 'main', '0', 'overwrite', 16, '1', '游戏上线时间（Unix时间戳秒，需配置FROM_UNIXTIME转换）');

-- 推广备注（海外禁止等说明）
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'remarks2', 'promotion_desc', 'string', 'main', '0', 'overwrite', 17, '1', '推广说明（如"禁海外推广"）');

-- ========================================================
-- 推广链接映射 (target_location = 'promotion_link')
-- ========================================================

-- 安卓下载链接
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'apk_url', 'download_url', 'string', 'promotion_link', '1', 'overwrite', 50, '1', '安卓下载/H5进入链接');

-- iOS下载/游戏链接
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'ios_url', 'ios_url', 'string', 'promotion_link', '0', 'overwrite', 51, '1', 'iOS下载/进入链接');

-- 游戏详情页推广链接
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'gameinfo_url', 'web_url', 'string', 'promotion_link', '0', 'overwrite', 52, '1', '游戏详情推广页地址');

-- 游戏详情页备用链接
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'gameinfo_url2', 'alt_web_url', 'string', 'promotion_link', '0', 'overwrite', 53, '1', '游戏详情推广页备用地址');

-- ========================================================
-- 关联表字段映射 (target_location = 'relation')
-- ========================================================

-- 折扣 → 关联表首充折扣-国内
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'discount', 'first_charge_domestic', 'decimal', 'relation', '0', 'overwrite', 101, '1', '关联表-首充折扣国内');

-- 折扣 → 关联表首充折扣-海外
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'hw_discount', 'first_charge_overseas', 'decimal', 'relation', '0', 'overwrite', 102, '1', '关联表-首充折扣海外');

-- ========================================================
-- 平台数据字段映射 (target_location = 'platform_data')
-- ========================================================

-- 平台游戏ID（必填）
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'gameid', 'platform_game_id', 'string', 'platform_data', '1', 'overwrite', 200, '1', '277sy平台游戏ID');

-- 游戏截图数组（多对一合并：screenshot1~5 → screenshots JSON数组）
-- source_field 为 JSON 数组格式，后端 collectMultipleSourceFields() 将依次读取各字段值合并为数组
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', '["screenshot1","screenshot2","screenshot3","screenshot4","screenshot5"]', 'screenshots', 'json', 'main', '0', 'overwrite', 19, '1', '游戏截图数组（多字段合并，screenshot1~5）');

-- 视频封面图
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'video_pic', 'video_thumbnail', 'string', 'platform_data', '0', 'overwrite', 206, '1', '视频封面/预览图');

-- 游戏短标识
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'gameshort', 'short_code', 'string', 'platform_data', '0', 'overwrite', 207, '1', '游戏短标识码');

-- 推广备注（remarks2）
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'remarks2', 'promotion_remark', 'string', 'platform_data', '0', 'overwrite', 208, '1', '推广备注（如禁海外）');

-- 是否云游戏
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'cloud_game', 'is_cloud_game', 'boolean', 'platform_data', '0', 'overwrite', 209, '1', '是否云游戏');

-- 是否支持跨号
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'role_interflow', 'role_interflow', 'boolean', 'platform_data', '0', 'overwrite', 210, '1', '是否支持角色互通');

-- 是否非变态游戏（原始值保留）
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'is_notbtgame', 'is_notbtgame', 'boolean', 'platform_data', '0', 'overwrite', 211, '1', '是否非变态游戏（0=变态/BT服）');

-- 云游戏专属code
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'game_cloud_code_4', 'cloud_code', 'string', 'platform_data', '0', 'overwrite', 212, '1', '云游戏专属code（game_cloud_code_4）');

-- 限时折扣
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'flash_discount', 'flash_discount', 'decimal', 'platform_data', '0', 'overwrite', 213, '1', '限时折扣（单位：折）');

-- 限时活动返利比例
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'flash_rate', 'flash_rate', 'decimal', 'platform_data', '0', 'overwrite', 214, '1', '限时活动返利比例');

-- 限时活动微信返利比例
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'flash_wx_rate', 'flash_wx_rate', 'decimal', 'platform_data', '0', 'overwrite', 215, '1', '限时活动微信返利比例');

-- CPS返利比例（汇总）
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'rate', 'cps_ratio', 'decimal', 'platform_data', '0', 'overwrite', 216, '1', 'CPS推广返利比例（含所有渠道）');

-- CPS微信返利比例
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'rate_wx', 'cps_ratio_wx', 'decimal', 'platform_data', '0', 'overwrite', 217, '1', 'CPS推广微信返利比例');

-- 分级返利比例（4档）
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'rate_1', 'rate_lv1', 'decimal', 'platform_data', '0', 'overwrite', 218, '1', '返利比例-档位1');

INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'rate_2', 'rate_lv2', 'decimal', 'platform_data', '0', 'overwrite', 219, '1', '返利比例-档位2');

INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'rate_3', 'rate_lv3', 'decimal', 'platform_data', '0', 'overwrite', 220, '1', '返利比例-档位3');

INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'rate_4', 'rate_lv4', 'decimal', 'platform_data', '0', 'overwrite', 221, '1', '返利比例-档位4');

-- 微信分级返利比例（4档）
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'rate_wx_1', 'rate_wx_lv1', 'decimal', 'platform_data', '0', 'overwrite', 222, '1', '微信返利比例-档位1');

INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'rate_wx_2', 'rate_wx_lv2', 'decimal', 'platform_data', '0', 'overwrite', 223, '1', '微信返利比例-档位2');

INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'rate_wx_3', 'rate_wx_lv3', 'decimal', 'platform_data', '0', 'overwrite', 224, '1', '微信返利比例-档位3');

INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'rate_wx_4', 'rate_wx_lv4', 'decimal', 'platform_data', '0', 'overwrite', 225, '1', '微信返利比例-档位4');

-- Google Play & AppStore 返利比例
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'rate_google', 'rate_google', 'decimal', 'platform_data', '0', 'overwrite', 226, '1', 'Google Play返利比例');

INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'rate_appstore', 'rate_appstore', 'decimal', 'platform_data', '0', 'overwrite', 227, '1', 'AppStore返利比例');

-- 子推广返利比例
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'rate_child', 'rate_child', 'decimal', 'platform_data', '0', 'overwrite', 228, '1', '子推广返利比例');

INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'rate_child_wx', 'rate_child_wx', 'decimal', 'platform_data', '0', 'overwrite', 229, '1', '子推广微信返利比例');

-- 游戏显示标志
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'is_index', 'is_index', 'boolean', 'platform_data', '0', 'overwrite', 230, '1', '是否在首页显示');

INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'is_close', 'is_close', 'boolean', 'platform_data', '0', 'overwrite', 231, '1', '是否已关闭/下架');

INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'show_ios_url', 'show_ios_url', 'boolean', 'platform_data', '0', 'overwrite', 232, '1', '是否显示iOS链接');

INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'is_discount01', 'is_discount01', 'boolean', 'platform_data', '0', 'overwrite', 233, '1', '是否0.1折特殊折扣');

INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'can_dc', 'can_dc', 'boolean', 'platform_data', '0', 'overwrite', 234, '1', '是否支持直充');

INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'f_support', 'f_support', 'boolean', 'platform_data', '0', 'overwrite', 235, '1', 'F支持标志');

INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'game_accelerator', 'game_accelerator', 'boolean', 'platform_data', '0', 'overwrite', 236, '1', '是否支持游戏加速');

INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'gh_forbid', 'gh_forbid', 'boolean', 'platform_data', '0', 'overwrite', 237, '1', '是否禁止推广（gh_forbid）');

INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'hidden_url', 'hidden_url', 'boolean', 'platform_data', '0', 'overwrite', 238, '1', '是否隐藏链接');

-- 排序和级别
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'sort', 'platform_sort', 'int', 'platform_data', '0', 'overwrite', 240, '1', '平台排序权重');

INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'sort_hot', 'hot_rank', 'int', 'platform_data', '0', 'overwrite', 241, '1', '热门排名权重');

INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'game_level', 'game_level', 'int', 'platform_data', '0', 'overwrite', 242, '1', '游戏等级/评级');

-- 分类ID原始值（平台内部ID，逗号分隔）
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'genre_ids', 'genre_ids_raw', 'string', 'platform_data', '0', 'overwrite', 243, '1', '分类ID原始值（平台内部使用，逗号分隔）');

-- 颜色主题
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'color_1', 'color_theme', 'string', 'platform_data', '0', 'overwrite', 244, '1', '游戏颜色主题（如bg-yellow）');

-- 推广备注（多个备注字段）
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'remarks', 'platform_remarks', 'string', 'platform_data', '0', 'overwrite', 245, '1', '平台推广备注');

INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'tgremark', 'tg_remark', 'string', 'platform_data', '0', 'overwrite', 246, '1', 'TG推广备注');

INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'apk_remark', 'apk_remark', 'string', 'platform_data', '0', 'overwrite', 247, '1', '安卓端备注说明');

INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'ios_remark', 'ios_remark', 'string', 'platform_data', '0', 'overwrite', 248, '1', 'iOS端备注说明');

-- 续充折扣-国内（补充到主表）
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'dc_discount', 'recharge_domestic', 'decimal', 'main', '0', 'overwrite', 18, '1', '续充折扣-国内（dc_discount，4.2=4.2折）');

-- 续充折扣-国内（关联表）
INSERT INTO gb_box_field_mappings (box_id, resource_type, source_field, target_field, field_type, target_location, is_required, conflict_strategy, sort_order, status, remark)
VALUES (7, 'game', 'dc_discount', 'recharge_domestic', 'decimal', 'relation', '0', 'overwrite', 103, '1', '关联表-续充折扣国内');

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
WHERE box_id = 7
ORDER BY target_location, sort_order;

-- 统计映射数量
SELECT
    target_location,
    COUNT(*) AS mapping_count,
    SUM(CASE WHEN is_required = '1' THEN 1 ELSE 0 END) AS required_count
FROM gb_box_field_mappings
WHERE box_id = 7
GROUP BY target_location;


