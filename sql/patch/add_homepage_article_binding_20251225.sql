-- 添加游戏和游戏盒子主页面文章绑定功能支持
-- 日期: 2025-12-25
-- 说明: 支持每个游戏/游戏盒子按语言绑定对应的主页面文章
-- 设计方案: 利用现有的文章关联表，增加is_homepage字段标识主页面绑定

-- ========================================
-- 方案说明
-- ========================================
-- 1. 利用现有的 gb_article_games 和 gb_article_game_boxes 关联表
-- 2. 添加 is_homepage 字段标识该关联是否为主页面绑定
-- 3. 通过唯一索引确保每个游戏/盒子的每个语言只能有一篇主页面文章
-- 4. 查询主页面: WHERE is_homepage='1' AND game_id=? AND article.locale=?
-- 5. 查询普通关联文章: WHERE (is_homepage='0' OR is_homepage IS NULL)

-- ========================================
-- 修改文章-游戏关联表
-- ========================================
ALTER TABLE `gb_article_games`
ADD COLUMN `is_homepage` CHAR(1) NULL DEFAULT '0' COMMENT '是否为主页面：0-否 1-是' AFTER `relation_type`,
ADD COLUMN `homepage_locale` VARCHAR(10) NULL COMMENT '主页面语言（冗余字段，is_homepage=1时必填）' AFTER `is_homepage`;

-- 添加唯一索引：确保每个游戏的每个语言只能有一篇主页面
-- 使用函数索引：只对 is_homepage='1' 的记录建立唯一约束
-- 注意：MySQL 8.0+ 支持函数索引
ALTER TABLE `gb_article_games`
ADD UNIQUE INDEX `uk_game_homepage_locale`(`game_id`, `homepage_locale`, `is_homepage`) USING BTREE;

-- 添加索引以优化主页面查询
ALTER TABLE `gb_article_games`
ADD INDEX `idx_homepage_query`(`is_homepage`, `game_id`, `homepage_locale`) USING BTREE;

-- ========================================
-- 修改文章-游戏盒子关联表
-- ========================================
ALTER TABLE `gb_article_game_boxes`
ADD COLUMN `is_homepage` CHAR(1) NULL DEFAULT '0' COMMENT '是否为主页面：0-否 1-是' AFTER `relation_type`,
ADD COLUMN `homepage_locale` VARCHAR(10) NULL COMMENT '主页面语言（冗余字段，is_homepage=1时必填）' AFTER `is_homepage`;

-- 添加唯一索引：确保每个游戏盒子的每个语言只能有一篇主页面
ALTER TABLE `gb_article_game_boxes`
ADD UNIQUE INDEX `uk_gamebox_homepage_locale`(`game_box_id`, `homepage_locale`, `is_homepage`) USING BTREE;

-- 添加索引以优化主页面查询
ALTER TABLE `gb_article_game_boxes`
ADD INDEX `idx_homepage_query`(`is_homepage`, `game_box_id`, `homepage_locale`) USING BTREE;

-- ========================================
-- 使用示例和查询语句
-- ========================================

-- 示例1: 将文章ID=10（中文）绑定为游戏ID=1的主页面
-- INSERT INTO gb_article_games (article_id, game_id, relation_source, relation_type, is_homepage, homepage_locale, display_order)
-- VALUES (10, 1, 'manual', 'homepage', '1', 'zh-CN', 1);

-- 示例2: 将文章ID=11（英文）绑定为游戏ID=1的主页面
-- INSERT INTO gb_article_games (article_id, game_id, relation_source, relation_type, is_homepage, homepage_locale, display_order)
-- VALUES (11, 1, 'manual', 'homepage', '1', 'en-US', 1);

-- 示例3: 查询游戏ID=1的中文主页面文章（优化后不需要JOIN articles表）
-- SELECT a.* FROM gb_articles a
-- INNER JOIN gb_article_games ag ON a.id = ag.article_id
-- WHERE ag.game_id = 1 AND ag.is_homepage = '1' AND ag.homepage_locale = 'zh-CN' AND ag.del_flag = '0';

-- 示例4: 查询游戏盒子ID=1的所有语言主页面文章
-- SELECT a.*, agb.game_box_id, agb.homepage_locale FROM gb_articles a
-- INNER JOIN gb_article_game_boxes agb ON a.id = agb.article_id
-- WHERE agb.game_box_id = 1 AND agb.is_homepage = '1' AND agb.del_flag = '0';

-- 示例5: 验证多语言支持 - 同一个游戏可以有多个不同语言的主页面
-- SELECT ag.game_id, ag.homepage_locale, ag.article_id, a.title
-- FROM gb_article_games ag
-- INNER JOIN gb_articles a ON ag.article_id = a.id
-- WHERE ag.is_homepage = '1' AND ag.game_id = 1
-- ORDER BY ag.homepage_locale;

-- 示例5: 查询某文章是否被绑定为主页面，以及绑定到哪个游戏/盒子
-- SELECT 'game' as bind_type, game_id as target_id, a.locale
-- FROM gb_article_games ag
-- INNER JOIN gb_articles a ON ag.article_id = a.id
-- WHERE ag.article_id = 10 AND ag.is_homepage = '1' AND ag.del_flag = '0'
-- UNION ALL
-- SELECT 'gamebox' as bind_type, game_box_id as target_id, a.locale
-- FROM gb_article_game_boxes agb
-- INNER JOIN gb_articles a ON agb.article_id = a.id
-- WHERE agb.article_id = 10 AND agb.is_homepage = '1' AND agb.del_flag = '0';

-- 示例6: 更新绑定（先删除旧的主页面绑定，再插入新的）
-- -- 解绑游戏ID=1的中文主页面
-- DELETE FROM gb_article_games WHERE game_id = 1 AND is_homepage = '1'
--   AND article_id IN (SELECT id FROM gb_articles WHERE locale = 'zh-CN');
-- -- 绑定新的主页面文章
-- INSERT INTO gb_article_games (article_id, game_id, relation_source, relation_type, is_homepage, display_order)
-- VALUES (20, 1, 'manual', 'homepage', '1', 1);

-- ========================================
-- 注意事项
-- ========================================
-- 1. is_homepage='1' 的记录，relation_type 建议设置为 'homepage'
-- 2. is_homepage='1' 的记录，display_order 可以设置为 1（但不影响主页面查询）
-- 3. 唯一索引确保同一个游戏/盒子不会有两篇相同语言的主页面
-- 4. 删除主页面绑定时使用软删除（del_flag='2'）或物理删除都可以
-- 5. 如果需要修改主页面绑定，建议先删除旧的再插入新的
