-- ============================================
-- 游戏盒子系统 v3.19
-- 添加文章路径规则字段
-- 创建时间: 2026-01-14
-- ============================================

-- 说明：
-- gb_articles表需要添加path_rule字段，用于存储文章的存储路径生成规则
-- 这个字段用于控制文章在存储中的路径格式

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================
-- 修改 gb_articles 表，添加路径规则字段
-- ============================================

-- 添加path_rule字段：存储文章的路径生成规则
ALTER TABLE `gb_articles` 
ADD COLUMN `path_rule` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'date-title' 
COMMENT '路径规则：date-title-日期+标题 title-only-仅标题 category-title-分类+标题 custom-自定义' 
AFTER `storage_key`;

-- ============================================
-- 数据迁移：为现有数据设置默认路径规则
-- ============================================

-- 对于现有的文章数据，设置默认路径规则为 'date-title'
UPDATE `gb_articles` 
SET `path_rule` = 'date-title' 
WHERE `path_rule` IS NULL;

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- 字段说明
-- ============================================
-- 
-- path_rule字段的可选值：
-- - 'date-title': 使用日期+标题格式（默认）
--   例如：2026-01-14-article-title/README.md
-- - 'title-only': 仅使用标题
--   例如：article-title/README.md  
-- - 'category-title': 使用分类+标题
--   例如：game-guide/article-title/README.md
-- - 'custom': 自定义路径（直接使用storage_key）
--   例如：custom/path/filename.md
-- 
-- 此字段与前端的路径规则选择器对应，用于动态生成存储路径
-- 
-- ============================================