-- 修复工作流网站关联表唯一约束
-- 问题: 原唯一约束 uk_site_workflow(site_id, workflow_id) 不支持同一网站和工作流有多种关系类型
-- 解决: 修改为 uk_site_workflow_relation(site_id, workflow_id, relation_type)

-- 1. 删除旧的唯一约束
ALTER TABLE `gb_site_workflow_relation` DROP INDEX `uk_site_workflow`;

-- 2. 添加新的唯一约束,包含 relation_type
ALTER TABLE `gb_site_workflow_relation` 
ADD UNIQUE INDEX `uk_site_workflow_relation`(`site_id` ASC, `workflow_id` ASC, `relation_type` ASC) USING BTREE;

-- 说明:
-- 修改后,同一个网站和工作流可以有多种关系类型:
-- - site_id=3, workflow_id=2, relation_type='include' (关联)
-- - site_id=3, workflow_id=2, relation_type='exclude' (排除)
-- 这符合业务逻辑:默认配置可以同时设置"关联"和"排除"关系,关联优先生效
