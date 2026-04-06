-- v3.57 修复工作流步骤执行记录表大字段超限问题
-- TEXT 类型最大 64KB，工具输出（如 JSON 文件内容）可能超过此限制导致 UPDATE 静默失败
-- 将相关列改为 LONGTEXT（4GB 上限），彻底消除截断的必要性

ALTER TABLE `gb_workflow_step_execution`
    MODIFY COLUMN `input_data`  LONGTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '步骤输入数据（JSON）',
    MODIFY COLUMN `output_data` LONGTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '步骤输出数据（JSON）',
    MODIFY COLUMN `step_config` LONGTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '步骤配置快照（JSON）',
    MODIFY COLUMN `error`       LONGTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '错误信息';

-- 同样修复工作流执行主表（output_data 也可能包含大体积内容）
ALTER TABLE `gb_workflow_execution`
    MODIFY COLUMN `input_data`  LONGTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '输入数据（JSON）',
    MODIFY COLUMN `output_data` LONGTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '输出数据（JSON）';
