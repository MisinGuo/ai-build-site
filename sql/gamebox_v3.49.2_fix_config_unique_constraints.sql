-- 配置类表唯一键约束修复
-- 版本：v3.49.2
-- 日期：2026-02-06
-- 说明：修复存储配置、AI平台、原子工具、工作流等配置类表的唯一键约束，以支持数据导入和软删除

-- 问题说明：
-- 1. 系统使用软删除（del_flag='2'），已删除数据仍在表中
-- 2. 配置代码（code）等字段的唯一键约束会导致数据导入时冲突
-- 3. 唯一键约束会阻止：删除->重建->再删除 的生命周期
-- 4. 跨环境数据导入时，配置代码可能重复

-- 解决方案：
-- 1. 对于 code 类字段：改为 (site_id, code, del_flag) 的复合唯一索引
-- 2. 保持查询性能的同时，允许同一 code 在不同网站或不同删除状态下存在
-- 3. 应用层需要在创建/更新时检查 del_flag='0' 的记录


-- ============================================
-- 1. 网站表 (gb_sites)
-- ============================================
-- 移除域名的唯一键约束
ALTER TABLE `gb_sites` DROP INDEX `uk_domain`;

-- 移除网站编码的唯一键约束  
ALTER TABLE `gb_sites` DROP INDEX `uk_code`;

-- 添加包含 del_flag 的复合索引以保持查询性能
ALTER TABLE `gb_sites` ADD INDEX `idx_domain_del`(`domain` ASC, `del_flag` ASC);
ALTER TABLE `gb_sites` ADD INDEX `idx_code_del`(`code` ASC, `del_flag` ASC);


-- ============================================
-- 2. 存储配置表 (gb_storage_configs)
-- ============================================
-- 移除原有的 code 唯一键约束
ALTER TABLE `gb_storage_configs` DROP INDEX `uk_code`;

-- 不添加包含 del_flag 的唯一索引，因为软删除时会冲突
-- 只添加普通索引以保持查询性能
ALTER TABLE `gb_storage_configs` ADD INDEX `idx_site_code_del`(`site_id` ASC, `code` ASC, `del_flag` ASC);
ALTER TABLE `gb_storage_configs` ADD INDEX `idx_code_del`(`code` ASC, `del_flag` ASC);

-- 说明：唯一性由应用层保证，创建/更新时只检查 del_flag='0' 的记录


-- ============================================
-- 3. 分类表 (gb_categories)
-- ============================================
-- 原有的唯一键：uk_site_slug (site_id, category_type, slug)
-- 这个设计基本合理，但不应包含 del_flag，避免软删除冲突
ALTER TABLE `gb_categories` DROP INDEX `uk_site_slug`;

-- 不添加包含 del_flag 的唯一索引，因为软删除时会冲突
-- 只添加普通索引以保持查询性能
ALTER TABLE `gb_categories` ADD INDEX `idx_site_type_slug_del`(`site_id` ASC, `category_type` ASC, `slug` ASC, `del_flag` ASC);
ALTER TABLE `gb_categories` ADD INDEX `idx_slug_del`(`slug` ASC, `del_flag` ASC);

-- 说明：唯一性由应用层保证，创建/更新时只检查 del_flag='0' 的记录


-- ============================================  
-- 4. 文章模板配置表 (gb_prompt_templates)
-- ============================================
-- 移除 template_code 的唯一键约束
ALTER TABLE `gb_prompt_templates` DROP INDEX `uk_template_code`;

-- 不添加包含 del_flag 的唯一索引，因为软删除时会冲突
-- 只添加普通索引以保持查询性能
ALTER TABLE `gb_prompt_templates` ADD INDEX `idx_template_code_del`(`template_code` ASC, `del_flag` ASC);

-- 说明：唯一性由应用层保证，创建/更新时只检查 del_flag='0' 的记录


-- ============================================
-- 5. 分类类型表 (gb_category_types) - 跳过
-- ============================================
-- 说明：该表没有 del_flag 字段，无法应用此修复


-- ============================================
-- 6. 标题批次表 (gb_title_batches) - 跳过
-- ============================================
-- 说明：该表不存在于数据库中


-- ============================================
-- 说明
-- ============================================
-- 1. 修复了 4 个表的唯一键约束：gb_sites, gb_storage_configs, gb_categories, gb_prompt_templates
-- 2. gb_category_types 表没有 del_flag 字段，跳过修复
-- 3. gb_title_batches 表不存在，跳过修复
-- 4. **重要变更**：移除了所有包含 del_flag 的唯一索引
--    - 原因：软删除场景下，同一条数据可能被多次删除，导致 (code, del_flag='2') 冲突
--    - 解决方案：改为普通索引，唯一性由应用层保证
-- 5. 添加了普通索引以保持查询性能
-- 6. 支持跨环境数据导入，允许相同 code 在不同删除状态下共存
-- 7. 支持完整的生命周期：创建->删除->重建->再删除（多次循环）


-- 应用层需要做的事：
-- 1. **创建/更新时**，必须检查唯一性：
--    SELECT COUNT(*) FROM table WHERE code=? AND del_flag='0'
-- 2. **查询有效记录**时，必须加上 del_flag='0' 条件
-- 3. **导入数据时**，如果 code 冲突（del_flag='0' 的记录已存在）：
--    - 选项1：自动添加后缀（如 oracle_imported_20260206）
--    - 选项2：提示用户选择是否覆盖
--    - 选项3：跳过该条记录
-- 4. **软删除时**，不需要担心唯一性冲突，可以多次删除同一 code
