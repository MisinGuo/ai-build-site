-- ===================================================================
-- 标题池架构优化：重命名表并优化结构
-- 版本：v2.79
-- 日期：2026-01-01
-- 说明：
-- 1. 重命名 gb_article_title_pool -> gb_title_pool (名称更简洁清晰)
-- 2. 移除标题表的 site_id 和 category_id 字段（由批次统一管理）
-- 3. 清空旧数据，采用新的批次管理架构
-- ===================================================================

-- 表结构说明：
-- gb_title_pool_batch: 批次管理表，存储批次元信息(site_id, category_id, batch_code等)
-- gb_title_pool: 标题内容表，只存储标题内容，通过import_batch关联到批次
-- gb_title_import_log: 导入日志表，记录每次导入的详细信息

-- ===================================================================
-- 第一步：清理旧数据
-- ===================================================================

-- 清空标题池旧数据
TRUNCATE TABLE gb_article_title_pool;

-- 清空批次表旧数据
TRUNCATE TABLE gb_title_pool_batch;

-- 清空导入日志旧数据
TRUNCATE TABLE gb_title_import_log;

-- ===================================================================
-- 第二步：重命名表
-- ===================================================================

-- 重命名标题池表：gb_article_title_pool -> gb_title_pool
ALTER TABLE gb_article_title_pool RENAME TO gb_title_pool;

-- ===================================================================
-- 第三步：优化表结构
-- ===================================================================

-- 移除冗余字段的索引
ALTER TABLE gb_title_pool DROP INDEX IF EXISTS idx_site_id;
ALTER TABLE gb_title_pool DROP INDEX IF EXISTS idx_category_id;

-- 移除冗余字段
ALTER TABLE gb_title_pool 
    DROP COLUMN IF EXISTS site_id,
    DROP COLUMN IF EXISTS category_id;

-- ===================================================================
-- 第四步：验证表结构
-- ===================================================================

-- 查看新表结构
DESC gb_title_pool;

-- 查看批次表结构
DESC gb_title_pool_batch;

-- 查看导入日志表结构
DESC gb_title_import_log;

-- ===================================================================
-- 完成说明：
-- ===================================================================
-- 表重命名：gb_article_title_pool -> gb_title_pool
-- 
-- 三个核心表的职责：
-- 1. gb_title_pool_batch (批次管理表)
--    - 存储批次元信息：site_id, category_id, batch_code, batch_name
--    - 每个批次对应一次导入操作
--    - 统一管理批次下所有标题的归属信息
--
-- 2. gb_title_pool (标题内容表)
--    - 只存储标题内容：title, keywords, reference_content, priority等
--    - 通过 import_batch 字段关联到批次
--    - 从批次获取 site_id 和 category_id（JOIN查询）
--
-- 3. gb_title_import_log (导入日志表)
--    - 记录每次导入的详细信息
--    - import_batch 关联到批次号
--    - 存储导入统计：total_count, success_count, failed_count等
--
-- 优势：
-- - 消除数据冗余（site_id/category_id不在标题中重复存储）
-- - 批次级别统一管理元信息
-- - 表名更简洁清晰
-- - 符合标题池批次管理的核心理念
-- ===================================================================
