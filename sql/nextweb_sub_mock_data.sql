-- =============================================================================
-- Next-web-sub 子站 Mock 数据
-- 站点名称  : 卡牌手游攻略站
-- 目标站点  : site_id = 2（脚本会自动插入，若已存在请先删除或调整）
-- 语言支持  : zh-CN（默认）+ zh-TW
-- 执行方式  : 在目标 MySQL 8.0+ 数据库中直接运行本文件
-- 注意事项  :
--   1. 请先确保 gamebox_init.sql 及所有迁移脚本已执行完毕
--   2. 若需重复执行，可先 DELETE FROM gb_sites WHERE id=2; 触发级联
--   3. domain 字段请改为你实际使用的子站域名
-- =============================================================================

-- ====== 0. 安全检查：如果 site_id=2 已存在则跳过站点本身，但保留数据插入幂等性 ======
-- 使用 INSERT IGNORE 处理 AUTO_INCREMENT 以外有唯一约束的表

SET NAMES utf8mb4;

-- =============================================================================
-- 0. 清理旧数据（幂等执行：每次运行前先删除 site_id=2 的所有关联数据）
-- =============================================================================
DELETE FROM `gb_articles` WHERE `master_article_id` IN (SELECT `id` FROM `gb_master_articles` WHERE `site_id`=2);
DELETE FROM `gb_master_article_games` WHERE `master_article_id` IN (SELECT `id` FROM `gb_master_articles` WHERE `site_id`=2);
DELETE FROM `gb_master_articles` WHERE `site_id`=2;
DELETE FROM `gb_game_category_relations` WHERE `game_id` IN (SELECT `id` FROM `gb_games` WHERE `site_id`=2);
DELETE FROM `gb_game_boxes` WHERE `site_id`=2;
DELETE FROM `gb_games` WHERE `site_id`=2;
DELETE FROM `gb_categories` WHERE `site_id`=2;
DELETE FROM `gb_site_locales` WHERE `site_id`=2;
DELETE FROM `gb_sites` WHERE `id`=2;

-- =============================================================================
-- 1. 站点 (gb_sites)
-- =============================================================================
INSERT IGNORE INTO `gb_sites` (
    `id`, `name`, `code`, `domain`, `site_type`, `is_personal`,
    `default_locale`, `supported_locales`,
    `seo_title`, `seo_keywords`, `seo_description`,
    `logo_url`, `description`,
    `status`, `del_flag`, `sort_order`, `create_by`, `create_time`
) VALUES (
    2,
    '卡牌手游攻略站',
    'sub_card_game',
    'sub.5awyx.com',  -- ← 请替换为实际子站域名
    'sub',
    0,
    'zh-CN',
    '["zh-CN","zh-TW"]',
    '卡牌手游攻略站 - 最全0.1折卡牌手游攻略',
    '卡牌手游,攻略,0.1折,三国卡牌,西游卡牌',
    '收录最热门0.1折卡牌手游攻略、评测与礼包资讯，助你在梦回西游记、真·战三国等游戏中快速成长。',
    '',
    '专注0.1折超值卡牌手游推广与攻略内容',
    '1', '0', 2, 'admin', NOW()
);

-- =============================================================================
-- 2. 站点语言配置 (gb_site_locales)
-- =============================================================================
INSERT IGNORE INTO `gb_site_locales`
    (`site_id`, `locale`, `locale_name`, `native_name`, `is_default`, `is_enabled`, `sort_order`, `create_time`)
VALUES
    (2, 'zh-CN', '简体中文', '简体中文', '1', '1', 1, NOW()),
    (2, 'zh-TW', '繁體中文', '繁體中文', '0', '1', 2, NOW());

-- =============================================================================
-- 3. 游戏分类 (gb_categories — category_type='game')
-- =============================================================================
INSERT INTO `gb_categories`
    (`site_id`, `parent_id`, `category_type`, `is_section`, `name`, `slug`,
     `icon`, `sort_order`, `status`, `del_flag`, `create_by`, `create_time`)
VALUES
    (2, 0, 'game', '0', '三国',   'sanguo', '🏰', 1, '1', '0', 'admin', NOW()),
    (2, 0, 'game', '0', '西游',   'xiyou',  '🌟', 2, '1', '0', 'admin', NOW()),
    (2, 0, 'game', '0', '忍者',   'ninja',  '🥷', 3, '1', '0', 'admin', NOW()),
    (2, 0, 'game', '0', '二次元', 'anime',  '✨', 4, '1', '0', 'admin', NOW());

-- 捕获游戏分类 ID
SET @cat_sanguo = (SELECT `id` FROM `gb_categories` WHERE `site_id`=2 AND `slug`='sanguo' AND `category_type`='game' LIMIT 1);
SET @cat_xiyou  = (SELECT `id` FROM `gb_categories` WHERE `site_id`=2 AND `slug`='xiyou'  AND `category_type`='game' LIMIT 1);
SET @cat_ninja  = (SELECT `id` FROM `gb_categories` WHERE `site_id`=2 AND `slug`='ninja'  AND `category_type`='game' LIMIT 1);
SET @cat_anime  = (SELECT `id` FROM `gb_categories` WHERE `site_id`=2 AND `slug`='anime'  AND `category_type`='game' LIMIT 1);

-- =============================================================================
-- 4. 文章板块（一级 section）+ 攻略子分类 + 评测子分类
-- =============================================================================

-- 4-1. 攻略板块（section）
INSERT INTO `gb_categories`
    (`site_id`, `parent_id`, `category_type`, `is_section`, `name`, `slug`,
     `sort_order`, `status`, `del_flag`, `create_by`, `create_time`)
VALUES
    (2, 0, 'article', '1', '攻略板块', 'guide-section', 1, '1', '0', 'admin', NOW());

SET @guide_section_id = (SELECT `id` FROM `gb_categories` WHERE `site_id`=2 AND `slug`='guide-section' AND `category_type`='article' LIMIT 1);

-- 4-2. 攻略子分类（名称必须与前端 locales.ts filterXxx 完全一致）
INSERT INTO `gb_categories`
    (`site_id`, `parent_id`, `category_type`, `is_section`, `name`, `slug`,
     `sort_order`, `status`, `del_flag`, `create_by`, `create_time`)
VALUES
    (2, @guide_section_id, 'article', '0', '新手指南', 'newbie',   1, '1', '0', 'admin', NOW()),
    (2, @guide_section_id, 'article', '0', '进阶技巧', 'advanced', 2, '1', '0', 'admin', NOW()),
    (2, @guide_section_id, 'article', '0', '阵容推荐', 'lineup',   3, '1', '0', 'admin', NOW()),
    (2, @guide_section_id, 'article', '0', '氪金分析', 'pay',      4, '1', '0', 'admin', NOW()),
    (2, @guide_section_id, 'article', '0', '角色评级', 'ranking',  5, '1', '0', 'admin', NOW()),
    (2, @guide_section_id, 'article', '0', '专题集合', 'special',  6, '1', '0', 'admin', NOW());

SET @acat_newbie   = (SELECT `id` FROM `gb_categories` WHERE `site_id`=2 AND `slug`='newbie'   AND `category_type`='article' LIMIT 1);
SET @acat_advanced = (SELECT `id` FROM `gb_categories` WHERE `site_id`=2 AND `slug`='advanced' AND `category_type`='article' LIMIT 1);
SET @acat_lineup   = (SELECT `id` FROM `gb_categories` WHERE `site_id`=2 AND `slug`='lineup'   AND `category_type`='article' LIMIT 1);
SET @acat_pay      = (SELECT `id` FROM `gb_categories` WHERE `site_id`=2 AND `slug`='pay'      AND `category_type`='article' LIMIT 1);
SET @acat_ranking  = (SELECT `id` FROM `gb_categories` WHERE `site_id`=2 AND `slug`='ranking'  AND `category_type`='article' LIMIT 1);
SET @acat_special  = (SELECT `id` FROM `gb_categories` WHERE `site_id`=2 AND `slug`='special'  AND `category_type`='article' LIMIT 1);

-- 4-3. 评测板块（section）
INSERT INTO `gb_categories`
    (`site_id`, `parent_id`, `category_type`, `is_section`, `name`, `slug`,
     `sort_order`, `status`, `del_flag`, `create_by`, `create_time`)
VALUES
    (2, 0, 'article', '1', '评测板块', 'review-section', 2, '1', '0', 'admin', NOW());

SET @review_section_id = (SELECT `id` FROM `gb_categories` WHERE `site_id`=2 AND `slug`='review-section' AND `category_type`='article' LIMIT 1);

-- 4-4. 评测子分类（slug='review' 对应前端 section: 'review' 参数查询）
INSERT INTO `gb_categories`
    (`site_id`, `parent_id`, `category_type`, `is_section`, `name`, `slug`,
     `sort_order`, `status`, `del_flag`, `create_by`, `create_time`)
VALUES
    (2, @review_section_id, 'article', '0', '游戏评测', 'review', 1, '1', '0', 'admin', NOW());

SET @acat_review = (SELECT `id` FROM `gb_categories` WHERE `site_id`=2 AND `slug`='review' AND `category_type`='article' LIMIT 1);

-- =============================================================================
-- 5. 游戏数据 (gb_games) — 6 款游戏，download_url 指向主站
-- =============================================================================
INSERT INTO `gb_games` (
    `site_id`,
    `name`, `subtitle`, `game_type`,
    `cover_url`, `icon_url`,
    `description`,
    `download_url`, `android_url`,
    `rating`, `download_count`,
    `tags`, `is_hot`, `is_new`, `is_recommend`,
    `status`, `del_flag`, `sort_order`, `create_by`, `create_time`
) VALUES
-- 1. 梦回西游记（西游类）
(2, '梦回西游记', '0.1折西游卡牌放置', 'card',
 'https://cp.u2game99.com/storage/operator/20260311/2e09410b3eda12988958bddfe2d5e699.png',
 'https://cp.u2game99.com/storage/operator/20260311/2e09410b3eda12988958bddfe2d5e699.png',
 '穿越经典西游世界，集结百余位神话英雄，0.1折超值体验，上线即送毕业阵容，护肝放置天花板。',
 'https://www.5awyx.com', 'https://www.5awyx.com',
 4.8, 18600000,
 '["西游","放置养成","0.1折"]', '1', '0', '1',
 '1', '0', 1, 'admin', DATE_SUB(NOW(), INTERVAL 30 DAY)),

-- 2. 真·战三国（三国类）
(2, '真·战三国', '百抽开局三国策略卡牌', 'card',
 'https://www.u2game99.com/data/upload/game/20260305/69a91bbfdb78e.gif',
 'https://www.u2game99.com/data/upload/game/20260305/69a91bbfdb78e.gif',
 '三国题材策略布阵卡牌，百抽开局诚意十足，跨服竞技激烈刺激，挑战三国最强布阵大师。',
 'https://www.5awyx.com', 'https://www.5awyx.com',
 4.7, 23400000,
 '["三国","策略布阵","百抽开局"]', '1', '0', '1',
 '1', '0', 2, 'admin', DATE_SUB(NOW(), INTERVAL 28 DAY)),

-- 3. 侠客道（二次元类）
(2, '侠客道', '二次元古风放置卡牌', 'card',
 'https://cp.u2game99.com/storage/operator/20260311/f233c806c6289b9b45cd143a192f7493.gif',
 'https://cp.u2game99.com/storage/operator/20260311/f233c806c6289b9b45cd143a192f7493.gif',
 '古代侠客与神话英雄融合的二次元卡牌，回合制布阵策略，每日15000代金券超值福利。',
 'https://www.5awyx.com', 'https://www.5awyx.com',
 4.7, 14500000,
 '["二次元","回合制","放置"]', '0', '1', '1',
 '1', '0', 3, 'admin', DATE_SUB(NOW(), INTERVAL 15 DAY)),

-- 4. 侍忍者（忍者类）
(2, '侍忍者', '0.05折忍者千抽卡牌', 'card',
 'https://cp.u2game99.com/storage/operator/20260310/cb1f93d900e875b1f3a654a7f2eccb43.gif',
 'https://cp.u2game99.com/storage/operator/20260310/cb1f93d900e875b1f3a654a7f2eccb43.gif',
 '0.05折超低折扣忍者卡牌，注册即得千抽，三忍系统深度玩法，忍者题材手游首选。',
 'https://www.5awyx.com', 'https://www.5awyx.com',
 4.6, 9800000,
 '["忍者","0.05折","千抽"]', '0', '1', '1',
 '1', '0', 4, 'admin', DATE_SUB(NOW(), INTERVAL 10 DAY)),

-- 5. 群英风华录（三国类）
(2, '群英风华录', '三国国漫1000抽美女卡牌', 'card',
 'https://cp.u2game99.com/storage/operator/20260312/2342816e5121057a8f11d1dc2f15571e.png',
 'https://cp.u2game99.com/storage/operator/20260312/2342816e5121057a8f11d1dc2f15571e.png',
 '三国国漫画风，1000抽礼包超值，靓丽武将尽纳麾下，打造专属三国阵容。',
 'https://www.5awyx.com', 'https://www.5awyx.com',
 4.5, 7600000,
 '["三国","国漫","1000抽"]', '0', '0', '0',
 '1', '0', 5, 'admin', DATE_SUB(NOW(), INTERVAL 20 DAY)),

-- 6. 武将无双（三国类）
(2, '武将无双', '跨服PVP三国策略卡牌', 'card',
 'https://cp.u2game99.com/storage/operator/20260306/6f7c64e06043430d271e0c988b77addc.png',
 'https://cp.u2game99.com/storage/operator/20260306/6f7c64e06043430d271e0c988b77addc.png',
 '三国策略卡牌对战游戏，兵种克制体系深度丰富，跨服PVP竞技燃爆，挑战全服最强武将。',
 'https://www.5awyx.com', 'https://www.5awyx.com',
 4.5, 11200000,
 '["三国","策略","跨服PVP"]', '0', '0', '0',
 '1', '0', 6, 'admin', DATE_SUB(NOW(), INTERVAL 25 DAY));

-- 捕获游戏 ID
SET @game_mhxyj = (SELECT `id` FROM `gb_games` WHERE `site_id`=2 AND `name`='梦回西游记' LIMIT 1);
SET @game_zszg  = (SELECT `id` FROM `gb_games` WHERE `site_id`=2 AND `name`='真·战三国' LIMIT 1);
SET @game_xkd   = (SELECT `id` FROM `gb_games` WHERE `site_id`=2 AND `name`='侠客道' LIMIT 1);
SET @game_srz   = (SELECT `id` FROM `gb_games` WHERE `site_id`=2 AND `name`='侍忍者' LIMIT 1);
SET @game_qyfhl = (SELECT `id` FROM `gb_games` WHERE `site_id`=2 AND `name`='群英风华录' LIMIT 1);
SET @game_jjws  = (SELECT `id` FROM `gb_games` WHERE `site_id`=2 AND `name`='武将无双' LIMIT 1);

-- =============================================================================
-- 6. 游戏-分类关联 (gb_game_category_relations) — 用于 categoryName 显示
-- =============================================================================
INSERT IGNORE INTO `gb_game_category_relations`
    (`game_id`, `category_id`, `is_primary`, `sort_order`, `create_by`, `create_time`)
VALUES
    (@game_mhxyj, @cat_xiyou,  '1', 1, 'admin', NOW()),
    (@game_zszg,  @cat_sanguo, '1', 1, 'admin', NOW()),
    (@game_xkd,   @cat_anime,  '1', 1, 'admin', NOW()),
    (@game_srz,   @cat_ninja,  '1', 1, 'admin', NOW()),
    (@game_qyfhl, @cat_sanguo, '1', 2, 'admin', NOW()),
    (@game_jjws,  @cat_sanguo, '1', 3, 'admin', NOW());

-- =============================================================================
-- 7. 攻略主文章 (gb_master_articles) — 9 篇攻略
-- =============================================================================
-- 注：category_id 指向攻略板块的子分类，is_top/sort_order 影响首页排列顺序

INSERT INTO `gb_master_articles` (
    `site_id`, `category_id`,
    `is_top`, `is_recommend`, `sort_order`,
    `del_flag`, `create_by`, `create_time`, `publish_time`, `remark`
) VALUES
-- G1: 梦回西游记新手入门（新手指南）
(2, @acat_newbie,   '0', '1', 1,  '0', 'admin', DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY), 'guide-mhxyj-newbie'),
-- G2: 真·战三国武将强度排行（角色评级）
(2, @acat_ranking,  '0', '1', 2,  '0', 'admin', DATE_SUB(NOW(), INTERVAL 14 DAY), DATE_SUB(NOW(), INTERVAL 14 DAY), 'guide-zszg-ranking'),
-- G3: 真·战三国竞技场阵容搭配（阵容推荐）
(2, @acat_lineup,   '0', '0', 3,  '0', 'admin', DATE_SUB(NOW(), INTERVAL 13 DAY), DATE_SUB(NOW(), INTERVAL 13 DAY), 'guide-zszg-lineup'),
-- G4: 侠客道全英雄强度一览（角色评级）
(2, @acat_ranking,  '0', '1', 4,  '0', 'admin', DATE_SUB(NOW(), INTERVAL 12 DAY), DATE_SUB(NOW(), INTERVAL 12 DAY), 'guide-xkd-ranking'),
-- G5: 侍忍者氪金分析（氪金分析）
(2, @acat_pay,      '0', '0', 5,  '0', 'admin', DATE_SUB(NOW(), INTERVAL 11 DAY), DATE_SUB(NOW(), INTERVAL 11 DAY), 'guide-srz-pay'),
-- G6: 代金券玩法详解（新手指南）
(2, @acat_newbie,   '0', '0', 6,  '0', 'admin', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY), 'guide-general-coupon'),
-- G7: 2026卡牌手游横向对比专题（专题集合）
(2, @acat_special,  '0', '1', 7,  '0', 'admin', DATE_SUB(NOW(), INTERVAL 9 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY), 'guide-special-compare'),
-- G8: 卡牌手游最优阵容搭配解析（阵容推荐）
(2, @acat_lineup,   '0', '0', 8,  '0', 'admin', DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY), 'guide-lineup-general'),
-- G9: 武将无双进阶技巧（进阶技巧）
(2, @acat_advanced, '0', '0', 9,  '0', 'admin', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY), 'guide-jjws-advanced');

-- 捕获攻略主文章 ID
SET @m_g1 = (SELECT `id` FROM `gb_master_articles` WHERE `site_id`=2 AND `remark`='guide-mhxyj-newbie' LIMIT 1);
SET @m_g2 = (SELECT `id` FROM `gb_master_articles` WHERE `site_id`=2 AND `remark`='guide-zszg-ranking' LIMIT 1);
SET @m_g3 = (SELECT `id` FROM `gb_master_articles` WHERE `site_id`=2 AND `remark`='guide-zszg-lineup' LIMIT 1);
SET @m_g4 = (SELECT `id` FROM `gb_master_articles` WHERE `site_id`=2 AND `remark`='guide-xkd-ranking' LIMIT 1);
SET @m_g5 = (SELECT `id` FROM `gb_master_articles` WHERE `site_id`=2 AND `remark`='guide-srz-pay' LIMIT 1);
SET @m_g6 = (SELECT `id` FROM `gb_master_articles` WHERE `site_id`=2 AND `remark`='guide-general-coupon' LIMIT 1);
SET @m_g7 = (SELECT `id` FROM `gb_master_articles` WHERE `site_id`=2 AND `remark`='guide-special-compare' LIMIT 1);
SET @m_g8 = (SELECT `id` FROM `gb_master_articles` WHERE `site_id`=2 AND `remark`='guide-lineup-general' LIMIT 1);
SET @m_g9 = (SELECT `id` FROM `gb_master_articles` WHERE `site_id`=2 AND `remark`='guide-jjws-advanced' LIMIT 1);

-- =============================================================================
-- 8. 评测主文章 (gb_master_articles) — 3 篇评测
-- =============================================================================
INSERT INTO `gb_master_articles` (
    `site_id`, `category_id`,
    `is_top`, `is_recommend`, `sort_order`,
    `del_flag`, `create_by`, `create_time`, `publish_time`, `remark`
) VALUES
-- R1: 梦回西游记深度评测
(2, @acat_review, '0', '1', 1, '0', 'admin', DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 20 DAY), 'review-mhxyj'),
-- R2: 真·战三国评测
(2, @acat_review, '0', '1', 2, '0', 'admin', DATE_SUB(NOW(), INTERVAL 18 DAY), DATE_SUB(NOW(), INTERVAL 18 DAY), 'review-zszg'),
-- R3: 侠客道评测
(2, @acat_review, '0', '0', 3, '0', 'admin', DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY), 'review-xkd');

SET @m_r1 = (SELECT `id` FROM `gb_master_articles` WHERE `site_id`=2 AND `remark`='review-mhxyj' LIMIT 1);
SET @m_r2 = (SELECT `id` FROM `gb_master_articles` WHERE `site_id`=2 AND `remark`='review-zszg' LIMIT 1);
SET @m_r3 = (SELECT `id` FROM `gb_master_articles` WHERE `site_id`=2 AND `remark`='review-xkd' LIMIT 1);

-- =============================================================================
-- 9. 攻略文章语言版本 (gb_articles) — zh-CN 版本（9 篇）
-- =============================================================================

INSERT INTO `gb_articles` (
    `master_article_id`, `locale`, `slug`,
    `title`, `description`, `cover_url`,
    `keywords`, `view_count`, `reading_time`,
    `status`, `del_flag`, `publish_time`, `create_by`, `create_time`
) VALUES
-- G1 zh-CN
(@m_g1, 'zh-CN', 'guide-mhxyj-newbie',
 '梦回西游记新手入门攻略：0.1折版本如何快速毕业',
 '新手必看！从创角领取毕业阵容、代金券使用技巧到英雄培养优先级，带你从零掌握梦回西游记核心玩法，少走弯路快速成长。',
 'https://cp.u2game99.com/storage/operator/20260311/2e09410b3eda12988958bddfe2d5e699.png',
 '新手,西游,入门', 234000, 8,
 '1', '0', DATE_SUB(NOW(), INTERVAL 15 DAY), 'admin', DATE_SUB(NOW(), INTERVAL 15 DAY)),

-- G2 zh-CN
(@m_g2, 'zh-CN', 'guide-zszg-ranking',
 '真·战三国武将强度排行榜（3月最新版）',
 '3月版本更新后，各武将强度重新排序！详细分析每位名将的技能配合与当前阵营适配度，助你合理分配百抽资源。',
 'https://www.u2game99.com/data/upload/game/20260305/69a91bbfdb78e.gif',
 '武将,三国,评级', 187000, 12,
 '1', '0', DATE_SUB(NOW(), INTERVAL 14 DAY), 'admin', DATE_SUB(NOW(), INTERVAL 14 DAY)),

-- G3 zh-CN
(@m_g3, 'zh-CN', 'guide-zszg-lineup',
 '真·战三国阵容搭配指南：竞技场Top5套路解析',
 '竞技场晋级必看！分析当前版本最强的5套阵容搭配，包含详细的布阵思路和应对各类对手的策略打法。',
 'https://www.u2game99.com/data/upload/game/20260305/69a91bce1f20b.jpg',
 '卡牌,竞技,阵容', 92000, 15,
 '1', '0', DATE_SUB(NOW(), INTERVAL 13 DAY), 'admin', DATE_SUB(NOW(), INTERVAL 13 DAY)),

-- G4 zh-CN
(@m_g4, 'zh-CN', 'guide-xkd-ranking',
 '侠客道全英雄强度一览：哪些侠客值得重点培养？',
 '混沌版本全新英雄登场，强度格局迎来大洗牌！从输出、控制、辅助三维度全面评级所有侠客，助你合理规划养成路线。',
 'https://cp.u2game99.com/storage/operator/20260311/f233c806c6289b9b45cd143a192f7493.gif',
 '侠客道,英雄,评级', 156000, 10,
 '1', '0', DATE_SUB(NOW(), INTERVAL 12 DAY), 'admin', DATE_SUB(NOW(), INTERVAL 12 DAY)),

-- G5 zh-CN
(@m_g5, 'zh-CN', 'guide-srz-pay',
 '侍忍者氪金分析：0.05折版本哪些礼包性价比最高？',
 '0.05折版本付费体验超值，哪些礼包才是真正值得购买的？拆解所有充值渠道，帮你用最少的钱养成最强的忍者阵容。',
 'https://cp.u2game99.com/storage/operator/20260310/cb1f93d900e875b1f3a654a7f2eccb43.gif',
 '忍者,氪金,分析', 113000, 9,
 '1', '0', DATE_SUB(NOW(), INTERVAL 11 DAY), 'admin', DATE_SUB(NOW(), INTERVAL 11 DAY)),

-- G6 zh-CN
(@m_g6, 'zh-CN', 'guide-general-coupon',
 '卡牌手游代金券玩法详解：如何白嫖最多千元代金',
 '代金券是0.1折卡牌手游的核心福利，系统讲解代金券的获取途径、使用顺序和最优兑换策略，最大化每日白嫖收益。',
 'https://cp.u2game99.com/storage/operator/20260306/6f7c64e06043430d271e0c988b77addc.png',
 '代金券,攻略,薅羊毛', 286000, 7,
 '1', '0', DATE_SUB(NOW(), INTERVAL 10 DAY), 'admin', DATE_SUB(NOW(), INTERVAL 10 DAY)),

-- G7 zh-CN
(@m_g7, 'zh-CN', 'guide-special-compare',
 '2026卡牌手游横向对比：0.1折超值游戏Top10推荐',
 '2026年0.1折卡牌手游百花齐放，从福利、玩法深度、付费体验三个维度横向对比10款热门卡牌手游，找到最适合自己的那一款。',
 'https://cp.u2game99.com/storage/operator/20260311/2e09410b3eda12988958bddfe2d5e699.png',
 '卡牌,横向对比,专题', 523000, 18,
 '1', '0', DATE_SUB(NOW(), INTERVAL 9 DAY), 'admin', DATE_SUB(NOW(), INTERVAL 9 DAY)),

-- G8 zh-CN
(@m_g8, 'zh-CN', 'guide-lineup-general',
 '三国卡牌阵容搭配全攻略：多阵营最优组合解析',
 '三国卡牌里阵营搭配直接决定战力上限，系统梳理魏蜀吴各阵营最优组合、核心武将选取逻辑及资源分配优先顺序。',
 'https://www.u2game99.com/data/upload/game/20260305/69a91bbfdb78e.gif',
 '三国,阵容,入门', 168000, 11,
 '1', '0', DATE_SUB(NOW(), INTERVAL 8 DAY), 'admin', DATE_SUB(NOW(), INTERVAL 8 DAY)),

-- G9 zh-CN
(@m_g9, 'zh-CN', 'guide-jjws-advanced',
 '武将无双进阶指南：兵种克制与跨服PVP制胜策略',
 '武将无双跨服竞技最核心的就是兵种克制体系，本文从三大兵种相克关系、阵容构建到赛季冲分路线全面解析。',
 'https://cp.u2game99.com/storage/operator/20260306/6f7c64e06043430d271e0c988b77addc.png',
 '武将无双,进阶,PVP', 97000, 13,
 '1', '0', DATE_SUB(NOW(), INTERVAL 7 DAY), 'admin', DATE_SUB(NOW(), INTERVAL 7 DAY));

-- =============================================================================
-- 10. 攻略文章语言版本 (gb_articles) — zh-TW 版本（9 篇）
-- =============================================================================
INSERT INTO `gb_articles` (
    `master_article_id`, `locale`, `slug`,
    `title`, `description`, `cover_url`,
    `keywords`, `view_count`, `reading_time`,
    `status`, `del_flag`, `publish_time`, `create_by`, `create_time`
) VALUES
-- G1 zh-TW
(@m_g1, 'zh-TW', 'guide-mhxyj-newbie',
 '梦回西游记新手入门攻略：0.1折版本如何快速毕业',
 '新手必看！从创角领取毕业阵容、代金券使用技巧到英雄培养优先级，带你从零掌握梦回西游记核心玩法，少走弯路快速成长。',
 'https://cp.u2game99.com/storage/operator/20260311/2e09410b3eda12988958bddfe2d5e699.png',
 '新手,西游,入门', 234000, 8,
 '1', '0', DATE_SUB(NOW(), INTERVAL 15 DAY), 'admin', DATE_SUB(NOW(), INTERVAL 15 DAY)),

-- G2 zh-TW
(@m_g2, 'zh-TW', 'guide-zszg-ranking',
 '真·战三国武将强度排行榜（3月最新版）',
 '3月版本更新后，各武将强度重新排序！详细分析每位名将的技能配合与当前阵营适配度，助你合理分配百抽资源。',
 'https://www.u2game99.com/data/upload/game/20260305/69a91bbfdb78e.gif',
 '武将,三国,评级', 187000, 12,
 '1', '0', DATE_SUB(NOW(), INTERVAL 14 DAY), 'admin', DATE_SUB(NOW(), INTERVAL 14 DAY)),

-- G3 zh-TW
(@m_g3, 'zh-TW', 'guide-zszg-lineup',
 '真·战三国阵容搭配指南：竞技场Top5套路解析',
 '竞技场晋级必看！分析当前版本最强的5套阵容搭配，包含详细的布阵思路和应对各类对手的策略打法。',
 'https://www.u2game99.com/data/upload/game/20260305/69a91bce1f20b.jpg',
 '卡牌,竞技,阵容', 92000, 15,
 '1', '0', DATE_SUB(NOW(), INTERVAL 13 DAY), 'admin', DATE_SUB(NOW(), INTERVAL 13 DAY)),

-- G4 zh-TW
(@m_g4, 'zh-TW', 'guide-xkd-ranking',
 '侠客道全英雄强度一览：哪些侠客值得重点培养？',
 '混沌版本全新英雄登场，强度格局迎来大洗牌！从输出、控制、辅助三维度全面评级所有侠客，助你合理规划养成路线。',
 'https://cp.u2game99.com/storage/operator/20260311/f233c806c6289b9b45cd143a192f7493.gif',
 '侠客道,英雄,评级', 156000, 10,
 '1', '0', DATE_SUB(NOW(), INTERVAL 12 DAY), 'admin', DATE_SUB(NOW(), INTERVAL 12 DAY)),

-- G5 zh-TW
(@m_g5, 'zh-TW', 'guide-srz-pay',
 '侍忍者氪金分析：0.05折版本哪些礼包性价比最高？',
 '0.05折版本付费体验超值，哪些礼包才是真正值得购买的？拆解所有充值渠道，帮你用最少的钱养成最强的忍者阵容。',
 'https://cp.u2game99.com/storage/operator/20260310/cb1f93d900e875b1f3a654a7f2eccb43.gif',
 '忍者,氪金,分析', 113000, 9,
 '1', '0', DATE_SUB(NOW(), INTERVAL 11 DAY), 'admin', DATE_SUB(NOW(), INTERVAL 11 DAY)),

-- G6 zh-TW
(@m_g6, 'zh-TW', 'guide-general-coupon',
 '卡牌手游代金券玩法详解：如何白嫖最多千元代金',
 '代金券是0.1折卡牌手游的核心福利，系统讲解代金券的获取途径、使用顺序和最优兑换策略，最大化每日白嫖收益。',
 'https://cp.u2game99.com/storage/operator/20260306/6f7c64e06043430d271e0c988b77addc.png',
 '代金券,攻略,薅羊毛', 286000, 7,
 '1', '0', DATE_SUB(NOW(), INTERVAL 10 DAY), 'admin', DATE_SUB(NOW(), INTERVAL 10 DAY)),

-- G7 zh-TW
(@m_g7, 'zh-TW', 'guide-special-compare',
 '2026卡牌手游横向对比：0.1折超值游戏Top10推荐',
 '2026年0.1折卡牌手游百花齐放，从福利、玩法深度、付费体验三个维度横向对比10款热门卡牌手游，找到最适合自己的那一款。',
 'https://cp.u2game99.com/storage/operator/20260311/2e09410b3eda12988958bddfe2d5e699.png',
 '卡牌,横向对比,专题', 523000, 18,
 '1', '0', DATE_SUB(NOW(), INTERVAL 9 DAY), 'admin', DATE_SUB(NOW(), INTERVAL 9 DAY)),

-- G8 zh-TW
(@m_g8, 'zh-TW', 'guide-lineup-general',
 '三国卡牌阵容搭配全攻略：多阵营最优组合解析',
 '三国卡牌里阵营搭配直接决定战力上限，系统梳理魏蜀吴各阵营最优组合、核心武将选取逻辑及资源分配优先顺序。',
 'https://www.u2game99.com/data/upload/game/20260305/69a91bbfdb78e.gif',
 '三国,阵容,入门', 168000, 11,
 '1', '0', DATE_SUB(NOW(), INTERVAL 8 DAY), 'admin', DATE_SUB(NOW(), INTERVAL 8 DAY)),

-- G9 zh-TW
(@m_g9, 'zh-TW', 'guide-jjws-advanced',
 '武将无双进阶指南：兵种克制与跨服PVP制胜策略',
 '武将无双跨服竞技最核心的就是兵种克制体系，本文从三大兵种相克关系、阵容构建到赛季冲分路线全面解析。',
 'https://cp.u2game99.com/storage/operator/20260306/6f7c64e06043430d271e0c988b77addc.png',
 '武将无双,进阶,PVP', 97000, 13,
 '1', '0', DATE_SUB(NOW(), INTERVAL 7 DAY), 'admin', DATE_SUB(NOW(), INTERVAL 7 DAY));

-- =============================================================================
-- 11. 评测文章语言版本 (gb_articles) — zh-CN 版本（3 篇）
-- =============================================================================
INSERT INTO `gb_articles` (
    `master_article_id`, `locale`, `slug`,
    `title`, `description`, `cover_url`,
    `keywords`, `view_count`, `reading_time`,
    `status`, `del_flag`, `publish_time`, `create_by`, `create_time`
) VALUES
-- R1 zh-CN
(@m_r1, 'zh-CN', 'review-mhxyj',
 '梦回西游记深度评测：国风卡牌放置手游的护肝天花板',
 '梦回西游记以百余位西游名将为核心，0.1折福利完全颠覆传统手游付费体验。上线即送鸿钧、太上老君等顶级阵容，让玩家真正享受到白嫖的快乐。',
 'https://cp.u2game99.com/storage/operator/20260311/2e09410b3eda12988958bddfe2d5e699.png',
 '西游,卡牌放置,评测', 325000, 14,
 '1', '0', DATE_SUB(NOW(), INTERVAL 20 DAY), 'admin', DATE_SUB(NOW(), INTERVAL 20 DAY)),

-- R2 zh-CN
(@m_r2, 'zh-CN', 'review-zszg',
 '真·战三国评测：策略布阵卡牌，三国玩家心头好',
 '真·战三国对三国题材的还原度极高，策略布阵融合卡牌收集的核心玩法颇具深度。首充0.01元送满星小乔的福利诚意十足，新手友好度很高。',
 'https://www.u2game99.com/data/upload/game/20260305/69a91bbfdb78e.gif',
 '三国,策略卡牌,评测', 241000, 12,
 '1', '0', DATE_SUB(NOW(), INTERVAL 18 DAY), 'admin', DATE_SUB(NOW(), INTERVAL 18 DAY)),

-- R3 zh-CN
(@m_r3, 'zh-CN', 'review-xkd',
 '侠客道评测：二次元放置卡牌，侠客与神话的完美融合',
 '侠客道将中国古代侠客与神话英雄融入二次元卡牌玩法，战前布阵的回合制系统给予玩家充足的策略空间。每日15000代金券福利让付费体验极为超值。',
 'https://cp.u2game99.com/storage/operator/20260311/f233c806c6289b9b45cd143a192f7493.gif',
 '二次元,放置卡牌,评测', 158000, 11,
 '1', '0', DATE_SUB(NOW(), INTERVAL 15 DAY), 'admin', DATE_SUB(NOW(), INTERVAL 15 DAY));

-- =============================================================================
-- 12. 评测文章语言版本 (gb_articles) — zh-TW 版本（3 篇）
-- =============================================================================
INSERT INTO `gb_articles` (
    `master_article_id`, `locale`, `slug`,
    `title`, `description`, `cover_url`,
    `keywords`, `view_count`, `reading_time`,
    `status`, `del_flag`, `publish_time`, `create_by`, `create_time`
) VALUES
-- R1 zh-TW
(@m_r1, 'zh-TW', 'review-mhxyj',
 '夢回西遊記深度評測：國風卡牌放置手遊的護肝天花板',
 '夢回西遊記以百餘位西遊名將為核心，0.1折福利完全顛覆傳統手遊付費體驗。上線即送鴻鈞、太上老君等頂級陣容，讓玩家真正享受到白嫖的快樂。',
 'https://cp.u2game99.com/storage/operator/20260311/2e09410b3eda12988958bddfe2d5e699.png',
 '西遊,卡牌放置,評測', 325000, 14,
 '1', '0', DATE_SUB(NOW(), INTERVAL 20 DAY), 'admin', DATE_SUB(NOW(), INTERVAL 20 DAY)),

-- R2 zh-TW
(@m_r2, 'zh-TW', 'review-zszg',
 '真·戰三國評測：策略佈陣卡牌，三國玩家心頭好',
 '真·戰三國對三國題材的還原度極高，策略佈陣融合卡牌收集的核心玩法頗具深度。首充0.01元送滿星小喬的福利誠意十足，新手友好度很高。',
 'https://www.u2game99.com/data/upload/game/20260305/69a91bbfdb78e.gif',
 '三國,策略卡牌,評測', 241000, 12,
 '1', '0', DATE_SUB(NOW(), INTERVAL 18 DAY), 'admin', DATE_SUB(NOW(), INTERVAL 18 DAY)),

-- R3 zh-TW
(@m_r3, 'zh-TW', 'review-xkd',
 '俠客道評測：二次元放置卡牌，俠客與神話的完美融合',
 '俠客道將中國古代俠客與神話英雄融入二次元卡牌玩法，戰前佈陣的回合制系統給予玩家充足的策略空間。每日15000代金券福利讓付費體驗極為超值。',
 'https://cp.u2game99.com/storage/operator/20260311/f233c806c6289b9b45cd143a192f7493.gif',
 '二次元,放置卡牌,評測', 158000, 11,
 '1', '0', DATE_SUB(NOW(), INTERVAL 15 DAY), 'admin', DATE_SUB(NOW(), INTERVAL 15 DAY));

-- =============================================================================
-- 13. 文章-游戏关联 (gb_master_article_games) — 便于后续数据扩展
--     注：gameName 字段并不直接从此表返回到前端文章列表 API，
--         前端 adaptGuide/adaptReview 的 game 字段实际使用 categoryName 作为后备。
-- =============================================================================
INSERT IGNORE INTO `gb_master_article_games`
    (`master_article_id`, `game_id`, `relation_source`, `relation_type`,
     `display_order`, `review_status`, `is_featured`, `del_flag`, `create_by`, `create_time`)
VALUES
    (@m_g1, @game_mhxyj, 'manual', 'related', 1, '1', '1', '0', 'admin', NOW()),
    (@m_g2, @game_zszg,  'manual', 'related', 1, '1', '1', '0', 'admin', NOW()),
    (@m_g3, @game_zszg,  'manual', 'related', 1, '1', '0', '0', 'admin', NOW()),
    (@m_g4, @game_xkd,   'manual', 'related', 1, '1', '1', '0', 'admin', NOW()),
    (@m_g5, @game_srz,   'manual', 'related', 1, '1', '0', '0', 'admin', NOW()),
    (@m_g9, @game_jjws,  'manual', 'related', 1, '1', '0', '0', 'admin', NOW()),
    (@m_r1, @game_mhxyj, 'manual', 'review',  1, '1', '1', '0', 'admin', NOW()),
    (@m_r2, @game_zszg,  'manual', 'review',  1, '1', '1', '0', 'admin', NOW()),
    (@m_r3, @game_xkd,   'manual', 'review',  1, '1', '0', '0', 'admin', NOW());

-- =============================================================================
-- 14. 游戏礼包盒子 (gb_game_boxes) — 6 个，供 Gifts 页面 /api/public/boxes 使用
--     前端 adaptBox 显示：box.name(标题)，box.description(内容)，box.logoUrl(图片)
-- =============================================================================
INSERT INTO `gb_game_boxes` (
    `site_id`, `name`,
    `logo_url`, `banner_url`,
    `description`,
    `download_url`,
    `discount_rate`, `game_count`,
    `sort_order`, `status`, `del_flag`, `create_by`, `create_time`
) VALUES
-- Box 1: 梦回西游记
(2, '梦回西游记 · 毕业阵容大礼包',
 'https://cp.u2game99.com/storage/operator/20260311/2e09410b3eda12988958bddfe2d5e699.png',
 'https://cp.u2game99.com/storage/operator/20260311/2e09410b3eda12988958bddfe2d5e699.png',
 '毕业阵容免费领,代金券1000枚,7天VIP体验卡,专属新手礼包',
 'https://www.5awyx.com',
 0.10, 1, 1, '1', '0', 'admin', DATE_SUB(NOW(), INTERVAL 30 DAY)),

-- Box 2: 真·战三国
(2, '真·战三国 · 百抽开局专属礼包',
 'https://www.u2game99.com/data/upload/game/20260305/69a91bbfdb78e.gif',
 'https://www.u2game99.com/data/upload/game/20260305/69a91bbfdb78e.gif',
 '百抽武将碎片,满星小乔卡片,首充大礼包,代金券500枚',
 'https://www.5awyx.com',
 0.10, 1, 2, '1', '0', 'admin', DATE_SUB(NOW(), INTERVAL 28 DAY)),

-- Box 3: 侠客道
(2, '侠客道 · 代金券新手启动礼包',
 'https://cp.u2game99.com/storage/operator/20260311/f233c806c6289b9b45cd143a192f7493.gif',
 'https://cp.u2game99.com/storage/operator/20260311/f233c806c6289b9b45cd143a192f7493.gif',
 '代金券500枚,侠客召唤碎片x50,装备强化石x30,新手专属礼包',
 'https://www.5awyx.com',
 0.10, 1, 3, '1', '0', 'admin', DATE_SUB(NOW(), INTERVAL 15 DAY)),

-- Box 4: 侍忍者
(2, '侍忍者 · 千抽忍者专属礼包',
 'https://cp.u2game99.com/storage/operator/20260310/cb1f93d900e875b1f3a654a7f2eccb43.gif',
 'https://cp.u2game99.com/storage/operator/20260310/cb1f93d900e875b1f3a654a7f2eccb43.gif',
 '注册即送千抽资源,忍者觉醒碎片x100,VIP7天体验,专属忍者礼包',
 'https://www.5awyx.com',
 0.05, 1, 4, '1', '0', 'admin', DATE_SUB(NOW(), INTERVAL 10 DAY)),

-- Box 5: 群英风华录
(2, '群英风华录 · 1000抽国漫礼包',
 'https://cp.u2game99.com/storage/operator/20260312/2342816e5121057a8f11d1dc2f15571e.png',
 'https://cp.u2game99.com/storage/operator/20260312/2342816e5121057a8f11d1dc2f15571e.png',
 '注册送1000抽资源,限定美女武将碎片,新手冲级加速包,专属新人礼包',
 'https://www.5awyx.com',
 0.10, 1, 5, '1', '0', 'admin', DATE_SUB(NOW(), INTERVAL 20 DAY)),

-- Box 6: 武将无双
(2, '武将无双 · 跨服PVP战士礼包',
 'https://cp.u2game99.com/storage/operator/20260306/6f7c64e06043430d271e0c988b77addc.png',
 'https://cp.u2game99.com/storage/operator/20260306/6f7c64e06043430d271e0c988b77addc.png',
 '武将兑换资源包,PVP战力加速道具,代金券300枚,新手专属礼包',
 'https://www.5awyx.com',
 0.10, 1, 6, '1', '0', 'admin', DATE_SUB(NOW(), INTERVAL 25 DAY));

-- =============================================================================
-- 完成！
-- 验证插入情况：
--   SELECT id, name, domain, default_locale, supported_locales FROM gb_sites WHERE id=2;
--   SELECT COUNT(*) FROM gb_categories WHERE site_id=2;                  -- 应为 12
--   SELECT COUNT(*) FROM gb_games WHERE site_id=2;                       -- 应为 6
--   SELECT COUNT(*) FROM gb_master_articles WHERE site_id=2;             -- 应为 12
--   SELECT COUNT(*) FROM gb_articles a JOIN gb_master_articles m         --
--     ON a.master_article_id=m.id WHERE m.site_id=2;                     -- 应为 24
--   SELECT COUNT(*) FROM gb_game_boxes WHERE site_id=2;                  -- 应为 6
-- =============================================================================
