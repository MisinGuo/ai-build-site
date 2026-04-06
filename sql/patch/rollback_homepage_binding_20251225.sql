-- 回滚主页面绑定功能的字段
-- 日期: 2025-12-25
-- 说明: 如果之前已经执行了add_homepage_article_binding_20251225.sql，需要先执行此回滚脚本

-- ========================================
-- 回滚文章-游戏关联表
-- ========================================
ALTER TABLE `gb_article_games`
DROP INDEX IF EXISTS `idx_homepage_query`,
DROP COLUMN IF EXISTS `homepage_locale`,
DROP COLUMN IF EXISTS `is_homepage`;

-- ========================================
-- 回滚文章-游戏盒子关联表
-- ========================================
ALTER TABLE `gb_article_game_boxes`
DROP INDEX IF EXISTS `idx_homepage_query`,
DROP COLUMN IF EXISTS `homepage_locale`,
DROP COLUMN IF EXISTS `is_homepage`;
