-- ============================================================================
-- 删除提示词模板相关表的 Patch SQL
-- 创建时间: 2026-01-02
-- 说明: 将提示词模板功能迁移至内容工具 (gb_content_tools) 后，删除旧的表结构
-- 依赖: 需要先执行 gamebox_v2.83_content_tools.sql 和 gamebox_v2.85_cleanup_prompt_template.sql
-- ============================================================================

-- 1. 删除网站-提示词模板关联表
-- 功能已迁移至 gb_site_content_tool_relation (支持 entityType='contentTool')
DROP TABLE IF EXISTS `gb_site_template_relations`;

-- 2. 删除提示词模板主表
-- 功能已迁移至 gb_content_tools (toolType='ai_generator')
DROP TABLE IF EXISTS `gb_prompt_templates`;

-- 3. 清理分类相关数据（如果还有残留）
DELETE FROM `gb_categories` WHERE `category_type` = 'prompt_template';
DELETE FROM `gb_category_types` WHERE `value` = 'prompt_template';

-- 执行完成后，提示词模板的所有表结构和数据都将被清除
-- 新系统使用 gb_content_tools 表来管理所有内容工具（包括 AI 生成工具）
