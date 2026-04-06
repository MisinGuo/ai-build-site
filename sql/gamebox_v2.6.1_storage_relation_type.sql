-- ====================================================================
-- 数据库升级脚本 v2.6.1 - 存储配置关联表添加 relation_type
-- ====================================================================
-- 功能：为存储配置关联表添加 relation_type 字段
-- 影响表：gb_site_storage_config_relations - 存储配置关联
-- 说明：补充 v2.6 遗漏的存储配置表，实现与其他关联表一致的全局默认配置模式
-- 日期：2025-12-29
-- ====================================================================

-- ====================================================================
-- 存储配置关联表 (gb_site_storage_config_relations)
-- ====================================================================

-- 添加 relation_type 字段
ALTER TABLE `gb_site_storage_config_relations`
ADD COLUMN `relation_type` VARCHAR(20) NOT NULL DEFAULT 'include' 
COMMENT '关联类型：include-正向关联 exclude-排除默认配置' AFTER `storage_config_id`;

-- 添加索引以提升查询性能
ALTER TABLE `gb_site_storage_config_relations`
ADD INDEX `idx_relation_type` (`relation_type` ASC);

-- 添加组合索引优化排除查询
ALTER TABLE `gb_site_storage_config_relations`
ADD INDEX `idx_site_type` (`site_id` ASC, `relation_type` ASC);


-- ====================================================================
-- 验证升级结果
-- ====================================================================

-- 检查 gb_site_storage_config_relations 表结构
SELECT 
    TABLE_NAME,
    COLUMN_NAME,
    COLUMN_TYPE,
    COLUMN_DEFAULT,
    COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'gb_site_storage_config_relations'
    AND COLUMN_NAME = 'relation_type';

-- 检查索引
SHOW INDEX FROM `gb_site_storage_config_relations` WHERE Key_name LIKE 'idx_%type%';


-- ====================================================================
-- 升级说明
-- ====================================================================
-- 
-- 新增功能：
-- 1. 全局默认配置：site_id=0 的存储配置作为所有网站的默认配置
-- 2. 黑名单排除：通过 relation_type='exclude' 排除不需要的默认配置
-- 3. 跨站共享：通过 relation_type='include' + site_id!=当前网站 实现配置共享
-- 4. 统一架构：与其他关联表保持一致的设计模式
--
-- 使用场景：
-- - 创建者模式下可为 site_id=0 配置全局默认存储配置
-- - 网站管理员可排除不需要的默认配置
-- - 支持多网站间存储配置共享
--
-- 代码已实现：
-- 1. Mapper XML 查询逻辑已更新
-- 2. Service 层排除管理方法已添加
-- 3. Controller 排除管理接口已添加
-- 4. 前端排除管理UI已添加
--
-- ====================================================================
