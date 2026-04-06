-- ===================================================
-- 添加游戏分类 - 2025-12-26
-- 说明：根据需求添加新的游戏分类，已存在的分类会被忽略
-- ===================================================

-- 检查并插入：全部（这个通常作为顶级分类或特殊分类）
INSERT INTO `gb_categories` (`site_id`, `parent_id`, `category_type`, `name`, `slug`, `icon`, `description`, `sort_order`, `status`, `del_flag`, `create_by`, `create_time`)
SELECT 0, 0, 'game', '全部', 'all', '🎮', '所有游戏分类', 0, '1', '0', 'admin', NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `gb_categories` WHERE `slug` = 'all' AND `category_type` = 'game' AND `site_id` = 0);

-- 检查并插入：经营
INSERT INTO `gb_categories` (`site_id`, `parent_id`, `category_type`, `name`, `slug`, `icon`, `description`, `sort_order`, `status`, `del_flag`, `create_by`, `create_time`)
SELECT 0, 0, 'game', '经营', 'jingying', '🏪', '模拟经营类游戏', 13, '1', '0', 'admin', NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `gb_categories` WHERE `slug` = 'jingying' AND `category_type` = 'game' AND `site_id` = 0);

-- 检查并插入：养成
INSERT INTO `gb_categories` (`site_id`, `parent_id`, `category_type`, `name`, `slug`, `icon`, `description`, `sort_order`, `status`, `del_flag`, `create_by`, `create_time`)
SELECT 0, 0, 'game', '养成', 'yangcheng', '🌱', '角色养成类游戏', 14, '1', '0', 'admin', NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `gb_categories` WHERE `slug` = 'yangcheng' AND `category_type` = 'game' AND `site_id` = 0);

-- 检查并插入：冒险
INSERT INTO `gb_categories` (`site_id`, `parent_id`, `category_type`, `name`, `slug`, `icon`, `description`, `sort_order`, `status`, `del_flag`, `create_by`, `create_time`)
SELECT 0, 0, 'game', '冒险', 'maoxian', '🗺️', '冒险探索类游戏', 15, '1', '0', 'admin', NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `gb_categories` WHERE `slug` = 'maoxian' AND `category_type` = 'game' AND `site_id` = 0);

-- 检查并插入：玄幻
INSERT INTO `gb_categories` (`site_id`, `parent_id`, `category_type`, `name`, `slug`, `icon`, `description`, `sort_order`, `status`, `del_flag`, `create_by`, `create_time`)
SELECT 0, 0, 'game', '玄幻', 'xuanhuan', '✨', '玄幻奇幻类游戏', 16, '1', '0', 'admin', NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `gb_categories` WHERE `slug` = 'xuanhuan' AND `category_type` = 'game' AND `site_id` = 0);

-- 检查并插入：科幻
INSERT INTO `gb_categories` (`site_id`, `parent_id`, `category_type`, `name`, `slug`, `icon`, `description`, `sort_order`, `status`, `del_flag`, `create_by`, `create_time`)
SELECT 0, 0, 'game', '科幻', 'kehuan', '🚀', '科幻未来类游戏', 17, '1', '0', 'admin', NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `gb_categories` WHERE `slug` = 'kehuan' AND `category_type` = 'game' AND `site_id` = 0);

-- 检查并插入：割草（数据库中已有同类型，此处使用割草作为别名）
INSERT INTO `gb_categories` (`site_id`, `parent_id`, `category_type`, `name`, `slug`, `icon`, `description`, `sort_order`, `status`, `del_flag`, `create_by`, `create_time`)
SELECT 0, 0, 'game', '割草', 'geca', '⚔️', '割草动作类游戏', 18, '1', '0', 'admin', NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `gb_categories` WHERE `slug` = 'geca' AND `category_type` = 'game' AND `site_id` = 0);

-- 检查并插入：角色扮演（与RPG类似，但中文名称）
INSERT INTO `gb_categories` (`site_id`, `parent_id`, `category_type`, `name`, `slug`, `icon`, `description`, `sort_order`, `status`, `del_flag`, `create_by`, `create_time`)
SELECT 0, 0, 'game', '角色扮演', 'juese-banyan', '🎭', '角色扮演类游戏', 19, '1', '0', 'admin', NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `gb_categories` WHERE `slug` = 'juese-banyan' AND `category_type` = 'game' AND `site_id` = 0);

-- 检查并插入：官方
INSERT INTO `gb_categories` (`site_id`, `parent_id`, `category_type`, `name`, `slug`, `icon`, `description`, `sort_order`, `status`, `del_flag`, `create_by`, `create_time`)
SELECT 0, 0, 'game', '官方', 'guanfang', '🏢', '官方正版游戏', 20, '1', '0', 'admin', NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `gb_categories` WHERE `slug` = 'guanfang' AND `category_type` = 'game' AND `site_id` = 0);

-- 检查并插入：休闲
INSERT INTO `gb_categories` (`site_id`, `parent_id`, `category_type`, `name`, `slug`, `icon`, `description`, `sort_order`, `status`, `del_flag`, `create_by`, `create_time`)
SELECT 0, 0, 'game', '休闲', 'xiuxian', '🎲', '休闲娱乐类游戏', 21, '1', '0', 'admin', NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `gb_categories` WHERE `slug` = 'xiuxian' AND `category_type` = 'game' AND `site_id` = 0);

-- 检查并插入：动漫
INSERT INTO `gb_categories` (`site_id`, `parent_id`, `category_type`, `name`, `slug`, `icon`, `description`, `sort_order`, `status`, `del_flag`, `create_by`, `create_time`)
SELECT 0, 0, 'game', '动漫', 'dongman', '🎬', '动漫IP改编游戏', 22, '1', '0', 'admin', NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `gb_categories` WHERE `slug` = 'dongman' AND `category_type` = 'game' AND `site_id` = 0);

-- 检查并插入：放置
INSERT INTO `gb_categories` (`site_id`, `parent_id`, `category_type`, `name`, `slug`, `icon`, `description`, `sort_order`, `status`, `del_flag`, `create_by`, `create_time`)
SELECT 0, 0, 'game', '放置', 'fangzhi', '⏱️', '放置挂机类游戏', 23, '1', '0', 'admin', NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `gb_categories` WHERE `slug` = 'fangzhi' AND `category_type` = 'game' AND `site_id` = 0);

-- 检查并插入：回合
INSERT INTO `gb_categories` (`site_id`, `parent_id`, `category_type`, `name`, `slug`, `icon`, `description`, `sort_order`, `status`, `del_flag`, `create_by`, `create_time`)
SELECT 0, 0, 'game', '回合', 'huihe', '🔄', '回合制游戏', 24, '1', '0', 'admin', NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `gb_categories` WHERE `slug` = 'huihe' AND `category_type` = 'game' AND `site_id` = 0);

-- ===================================================
-- 以下分类在v2.sql中已存在，这里只是确保它们存在于全局（site_id=0）
-- ===================================================

-- 西游（已存在于site_id=1，这里添加全局版本）
INSERT INTO `gb_categories` (`site_id`, `parent_id`, `category_type`, `name`, `slug`, `icon`, `description`, `sort_order`, `status`, `del_flag`, `create_by`, `create_time`)
SELECT 0, 0, 'game', '西游', 'xiyou-global', '🐒', '西游记题材游戏', 7, '1', '0', 'admin', NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `gb_categories` WHERE `slug` = 'xiyou-global' AND `category_type` = 'game' AND `site_id` = 0);

-- 挂机（已存在于site_id=1，这里添加全局版本）
INSERT INTO `gb_categories` (`site_id`, `parent_id`, `category_type`, `name`, `slug`, `icon`, `description`, `sort_order`, `status`, `del_flag`, `create_by`, `create_time`)
SELECT 0, 0, 'game', '挂机', 'guaji-global', '⏰', '放置挂机游戏', 11, '1', '0', 'admin', NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `gb_categories` WHERE `slug` = 'guaji-global' AND `category_type` = 'game' AND `site_id` = 0);

-- 二次元（已存在于site_id=1，这里添加全局版本）
INSERT INTO `gb_categories` (`site_id`, `parent_id`, `category_type`, `name`, `slug`, `icon`, `description`, `sort_order`, `status`, `del_flag`, `create_by`, `create_time`)
SELECT 0, 0, 'game', '二次元', 'erciyuan-global', '🎨', '二次元动漫风格游戏', 4, '1', '0', 'admin', NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `gb_categories` WHERE `slug` = 'erciyuan-global' AND `category_type` = 'game' AND `site_id` = 0);

-- 武侠（已存在于site_id=1，这里添加全局版本）
INSERT INTO `gb_categories` (`site_id`, `parent_id`, `category_type`, `name`, `slug`, `icon`, `description`, `sort_order`, `status`, `del_flag`, `create_by`, `create_time`)
SELECT 0, 0, 'game', '武侠', 'wuxia-global', '🥋', '武侠江湖类游戏', 5, '1', '0', 'admin', NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `gb_categories` WHERE `slug` = 'wuxia-global' AND `category_type` = 'game' AND `site_id` = 0);

-- 仙侠（已存在于site_id=1，这里添加全局版本）
INSERT INTO `gb_categories` (`site_id`, `parent_id`, `category_type`, `name`, `slug`, `icon`, `description`, `sort_order`, `status`, `del_flag`, `create_by`, `create_time`)
SELECT 0, 0, 'game', '仙侠', 'xianxia-global', '⚔️', '仙侠修真类游戏', 1, '1', '0', 'admin', NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `gb_categories` WHERE `slug` = 'xianxia-global' AND `category_type` = 'game' AND `site_id` = 0);

-- 魔幻（已存在于site_id=1，这里添加全局版本）
INSERT INTO `gb_categories` (`site_id`, `parent_id`, `category_type`, `name`, `slug`, `icon`, `description`, `sort_order`, `status`, `del_flag`, `create_by`, `create_time`)
SELECT 0, 0, 'game', '魔幻', 'mohuan-global', '🧙', '魔幻题材游戏', 3, '1', '0', 'admin', NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `gb_categories` WHERE `slug` = 'mohuan-global' AND `category_type` = 'game' AND `site_id` = 0);

-- 策略（已存在于site_id=1，这里添加全局版本）
INSERT INTO `gb_categories` (`site_id`, `parent_id`, `category_type`, `name`, `slug`, `icon`, `description`, `sort_order`, `status`, `del_flag`, `create_by`, `create_time`)
SELECT 0, 0, 'game', '策略', 'celue-global', '🎯', '策略战争游戏', 9, '1', '0', 'admin', NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `gb_categories` WHERE `slug` = 'celue-global' AND `category_type` = 'game' AND `site_id` = 0);

-- 卡牌（已存在于site_id=1，这里添加全局版本）
INSERT INTO `gb_categories` (`site_id`, `parent_id`, `category_type`, `name`, `slug`, `icon`, `description`, `sort_order`, `status`, `del_flag`, `create_by`, `create_time`)
SELECT 0, 0, 'game', '卡牌', 'kapai-global', '🎴', '卡牌策略游戏', 8, '1', '0', 'admin', NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `gb_categories` WHERE `slug` = 'kapai-global' AND `category_type` = 'game' AND `site_id` = 0);

-- 传奇（已存在于site_id=0，无需重复添加）
-- 三国（已存在于site_id=1，这里添加全局版本）
INSERT INTO `gb_categories` (`site_id`, `parent_id`, `category_type`, `name`, `slug`, `icon`, `description`, `sort_order`, `status`, `del_flag`, `create_by`, `create_time`)
SELECT 0, 0, 'game', '三国', 'sanguo-global', '🏛️', '三国题材游戏', 6, '1', '0', 'admin', NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `gb_categories` WHERE `slug` = 'sanguo-global' AND `category_type` = 'game' AND `site_id` = 0);

-- ===================================================
-- 执行完成提示
-- ===================================================
SELECT '游戏分类添加完成！已存在的分类已自动跳过。' AS '执行结果';
