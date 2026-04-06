-- ====================================================================
-- 数据库升级脚本 v2.6 - 全局默认配置+黑名单扩展
-- ====================================================================
-- 功能：为所有网站关联表添加 relation_type 字段
-- 影响表：
--   1. gb_site_box_relations     - 游戏盒子关联
--   2. gb_site_game_relations    - 游戏关联  
--   3. gb_site_article_relations - 文章关联
-- 说明：此升级实现与 gb_site_category_relations 一致的全局默认配置模式
-- 日期：2025-12-29
-- ====================================================================

-- ====================================================================
-- 1. 游戏盒子关联表 (gb_site_box_relations)
-- ====================================================================

-- 添加 relation_type 字段
ALTER TABLE `gb_site_box_relations`
ADD COLUMN `relation_type` VARCHAR(20) NOT NULL DEFAULT 'include' 
COMMENT '关联类型：include-正向关联 exclude-排除默认配置' AFTER `box_id`;

-- 添加索引以提升查询性能
ALTER TABLE `gb_site_box_relations`
ADD INDEX `idx_relation_type` (`relation_type` ASC);

-- 添加组合索引优化排除查询
ALTER TABLE `gb_site_box_relations`
ADD INDEX `idx_site_type` (`site_id` ASC, `relation_type` ASC);


-- ====================================================================
-- 2. 游戏关联表 (gb_site_game_relations)
-- ====================================================================

-- 添加 relation_type 字段
ALTER TABLE `gb_site_game_relations`
ADD COLUMN `relation_type` VARCHAR(20) NOT NULL DEFAULT 'include' 
COMMENT '关联类型：include-正向关联 exclude-排除默认配置' AFTER `game_id`;

-- 添加索引以提升查询性能
ALTER TABLE `gb_site_game_relations`
ADD INDEX `idx_relation_type` (`relation_type` ASC);

-- 添加组合索引优化排除查询
ALTER TABLE `gb_site_game_relations`
ADD INDEX `idx_site_type` (`site_id` ASC, `relation_type` ASC);


-- ====================================================================
-- 3. 文章关联表 (gb_site_article_relations)
-- ====================================================================

-- 添加 relation_type 字段
ALTER TABLE `gb_site_article_relations`
ADD COLUMN `relation_type` VARCHAR(20) NOT NULL DEFAULT 'include' 
COMMENT '关联类型：include-正向关联 exclude-排除默认配置' AFTER `article_id`;

-- 添加索引以提升查询性能
ALTER TABLE `gb_site_article_relations`
ADD INDEX `idx_relation_type` (`relation_type` ASC);

-- 添加组合索引优化排除查询
ALTER TABLE `gb_site_article_relations`
ADD INDEX `idx_site_type` (`site_id` ASC, `relation_type` ASC);


-- ====================================================================
-- 4. 验证升级结果
-- ====================================================================

-- 检查 gb_site_box_relations 表结构
SELECT 
    TABLE_NAME,
    COLUMN_NAME,
    COLUMN_TYPE,
    COLUMN_DEFAULT,
    COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'gb_site_box_relations'
    AND COLUMN_NAME = 'relation_type';

-- 检查 gb_site_game_relations 表结构
SELECT 
    TABLE_NAME,
    COLUMN_NAME,
    COLUMN_TYPE,
    COLUMN_DEFAULT,
    COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'gb_site_game_relations'
    AND COLUMN_NAME = 'relation_type';

-- 检查 gb_site_article_relations 表结构
SELECT 
    TABLE_NAME,
    COLUMN_NAME,
    COLUMN_TYPE,
    COLUMN_DEFAULT,
    COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'gb_site_article_relations'
    AND COLUMN_NAME = 'relation_type';

-- 检查所有关联表的索引
SHOW INDEX FROM `gb_site_box_relations` WHERE Key_name LIKE 'idx_%type%';
SHOW INDEX FROM `gb_site_game_relations` WHERE Key_name LIKE 'idx_%type%';
SHOW INDEX FROM `gb_site_article_relations` WHERE Key_name LIKE 'idx_%type%';


-- ====================================================================
-- 升级说明
-- ====================================================================
-- 
-- 新增功能：
-- 1. 全局默认配置：site_id=0 的记录作为所有网站的默认内容
-- 2. 黑名单排除：通过 relation_type='exclude' 排除不需要的默认内容
-- 3. 跨站共享：通过 relation_type='include' + site_id!=当前网站 实现内容共享
-- 4. 统一架构：与 gb_site_category_relations 保持一致的设计模式
--
-- 使用场景：
-- - 创建者模式下可为 site_id=0 配置全局默认游戏盒子/游戏/文章
-- - 网站管理员可排除不需要的默认内容
-- - 支持多网站间内容共享
--
-- 后续工作：
-- 1. 更新 Mapper XML 查询逻辑（参考 GbCategoryMapper.xml）
-- 2. 更新 Service 层添加排除管理方法
-- 3. 更新 Controller 添加排除管理接口
-- 4. 更新前端添加排除管理UI
-- 5. 如需支持父级自动关联，需根据业务需求实现
--
-- ====================================================================
