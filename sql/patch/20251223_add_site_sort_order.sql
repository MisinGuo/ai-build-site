-- 为 gb_sites 表添加 sort_order 字段
-- 执行时间: 2025-12-23
-- 说明: 添加排序字段，用于控制站点显示顺序

-- 添加 sort_order 字段
ALTER TABLE `gb_sites`
ADD COLUMN `sort_order` int DEFAULT 0 COMMENT '排序' AFTER `i18n_mode`;

-- 添加索引以提升查询性能
CREATE INDEX `idx_sort_order` ON `gb_sites`(`sort_order`);
