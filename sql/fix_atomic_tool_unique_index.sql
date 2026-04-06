-- 修复原子工具唯一索引：从 tool_code 改为 (tool_code, site_id)
-- 原因：同一个执行器可以被多个站点使用，每个站点有自己的配置

-- 步骤1: 先查看当前重复的数据
SELECT tool_code, tool_name, tool_type, site_id, id
FROM gb_atomic_tool
WHERE tool_code IN (
    SELECT tool_code
    FROM gb_atomic_tool
    GROUP BY tool_code
    HAVING COUNT(*) > 1
)
ORDER BY tool_code, site_id;

-- 步骤2: 删除旧的唯一索引（如果存在会报错可忽略）
ALTER TABLE gb_atomic_tool DROP INDEX IF EXISTS uk_tool_code;

-- 步骤3: 对于重复的记录，决定保留策略
-- 方案A: 保留系统工具（tool_type='system' 且 site_id=NULL 或 0）
-- 方案B: 手动处理 - 根据实际情况决定保留哪条

-- 临时方案：将重复记录的 site_id 设置为不同值
-- UPDATE gb_atomic_tool SET site_id = 1 WHERE id = xxx; -- 根据查询结果手动调整

-- 步骤4: 创建新的组合唯一索引
ALTER TABLE gb_atomic_tool ADD UNIQUE INDEX uk_tool_code_site (tool_code, site_id);

-- 说明：
-- - 系统工具（tool_type='system'）的 site_id 应该为 NULL，表示全局可用
-- - 用户从全局导入到站点的工具，site_id 为对应站点ID
-- - (tool_code='xxx', site_id=NULL) 和 (tool_code='xxx', site_id=1) 可以同时存在
-- - 这样设计支持：同一个执行器，每个站点有自己的配置实例
