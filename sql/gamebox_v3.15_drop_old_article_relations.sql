-- =====================================================
-- 游戏盒子项目 v3.15 数据库升级脚本
-- 删除旧的文章关联表（已迁移到主文章关联表）
-- 执行日期：2026-01-13
-- =====================================================

USE `gamebox`;

-- 备份说明
-- 建议：在删除前先备份这些表的数据
-- mysqldump -u root -p gamebox gb_article_games gb_article_game_boxes > backup_article_relations.sql

-- 1. 删除旧的文章-游戏关联表
-- 数据已通过 gamebox_v3.14 脚本迁移至 gb_master_article_games
DROP TABLE `gb_article_games`;

-- 2. 删除旧的文章-游戏盒子关联表
-- 数据已通过 gamebox_v3.14 脚本迁移至 gb_master_article_game_boxes
DROP TABLE `gb_article_game_boxes`;

-- =====================================================
-- 说明：
-- 1. 旧关联表已废弃，所有游戏和游戏盒子关联已迁移到主文章层级
-- 2. 新表结构：
--    - gb_master_article_games (主文章-游戏关联)
--    - gb_master_article_game_boxes (主文章-游戏盒子关联)
-- 3. 相关代码已更新：
--    - GbMasterArticleServiceImpl 使用新关联表
--    - SaveArticleToolExecutor 使用新关联表
--    - 前端编辑表单从主文章关联表加载数据
-- 4. 保留的文件（可能需要回滚）：
--    - domain/GbArticleGame.java
--    - domain/GbArticleGameBox.java
--    - mapper/GbArticleGameMapper.java
--    - mapper/GbArticleGameBoxMapper.java
--    - mapper XML 文件
-- =====================================================

-- 验证查询（执行前检查）
-- SELECT COUNT(*) as article_games_count FROM gb_article_games;
-- SELECT COUNT(*) as article_game_boxes_count FROM gb_article_game_boxes;
-- SELECT COUNT(*) as master_article_games_count FROM gb_master_article_games;
-- SELECT COUNT(*) as master_article_game_boxes_count FROM gb_master_article_game_boxes;

-- 执行完成
SELECT '旧的文章关联表已删除，关联数据已迁移至主文章关联表' AS result;
