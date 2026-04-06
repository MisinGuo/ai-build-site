-- =============================================
-- 标题池表字段清理 v2.81
-- 移除冗余字段和废弃的关联表
-- =============================================

-- 1. 移除标题表中的冗余字段
-- scheduled_date: 计划发布日期应该在批次层面管理
-- target_site_id: 目标站点已在批次中通过 site_id 管理
ALTER TABLE `gb_title_pool`
DROP COLUMN `scheduled_date`,
DROP COLUMN `target_site_id`;

-- 2. 删除废弃的标题池关联表
-- 原因：标题已通过批次统一管理，网站关联在批次级别处理
-- 使用 gb_site_title_batch_relations 表替代
DROP TABLE `gb_site_title_pool_relations`;

-- 注意：以下字段保留，有其存在意义：
-- - import_batch: 关联批次号，必须保留
-- - usage_status: 标题的使用状态，每条标题独立管理
-- - used_count: 使用次数统计
-- - used_time: 最后使用时间
-- - used_article_ids: 关联的文章ID列表
-- - priority: 标题优先级
-- - tags: 标题标签
-- - extra_data: 扩展数据
