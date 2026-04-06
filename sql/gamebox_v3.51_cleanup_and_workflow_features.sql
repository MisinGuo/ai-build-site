-- ====================================================================
-- 工作流功能完善 + 无用表清理 v3.51
-- 说明：
--   1. 删除 gb_workflow_tags      - 工作流表已有 tags JSON 字段，独立标签管理表冗余
--   2. 删除 gb_workflow_variables - 执行变量已通过 step_execution 的 input_data/output_data 捕获
--   3. 删除 gb_workflow_favorites - 收藏功能对管理后台实用性不高，已移除
--   4. 说明需要保留并将被代码使用的表：
--      - gb_workflow_step_execution  - 每步执行明细，已集成到执行引擎
--      - gb_workflow_template        - 工作流模板，支持"从模板创建"
--      - gb_workflow_execution       - 整体执行记录（上一版本已实现）
-- ====================================================================

-- 删除冗余的独立标签管理表（功能由 gb_workflow.tags JSON 字段承担）
DROP TABLE IF EXISTS `gb_workflow_tags`;

-- 删除冗余的运行时变量表（变量已通过 step_execution 的 input_data/output_data 保存）
DROP TABLE IF EXISTS `gb_workflow_variables`;

-- 删除收藏表（管理后台场景下收藏功能实用性不足，相关代码已移除）
DROP TABLE IF EXISTS `gb_workflow_favorites`;

-- ====================================================================
-- 升级说明
-- ====================================================================
-- 执行本 SQL 后：
--   - gb_workflow_tags 表被删除（无数据丢失，6条预置标签均未被使用）
--   - gb_workflow_variables 表被删除（无历史执行数据）
--   - gb_workflow_favorites 表被删除（无历史数据）
-- 后端代码将使用以下表实现完整工作流功能：
--   - gb_workflow_step_execution   → 每步执行明细，可分析失败原因
--   - gb_workflow_template         → 预置模板，支持一键创建工作流

-- ====================================================================
-- 修复存量数据：从 definition JSON 重新计算 step_count
-- ====================================================================
UPDATE gb_workflow
SET step_count = JSON_LENGTH(JSON_EXTRACT(definition, '$.steps'))
WHERE definition IS NOT NULL
  AND definition != ''
  AND JSON_VALID(definition)
  AND JSON_TYPE(JSON_EXTRACT(definition, '$.steps')) = 'ARRAY';
