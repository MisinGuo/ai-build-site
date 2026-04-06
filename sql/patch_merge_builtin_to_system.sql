-- 合并内置工具(builtin)到系统工具(system)
-- 原因：builtin 和 system 概念重复，统一使用 system
-- 执行日期：2026-02-07

-- 1. 查看当前有多少 builtin 类型的工具
SELECT COUNT(*) as builtin_count FROM gb_atomic_tool WHERE tool_type = 'builtin';

-- 2. 查看具体的 builtin 工具
SELECT id, tool_code, tool_name, tool_type, site_id, create_time 
FROM gb_atomic_tool 
WHERE tool_type = 'builtin';

-- 3. 将所有 builtin 类型更新为 system
UPDATE gb_atomic_tool 
SET tool_type = 'system', 
    update_time = NOW() 
WHERE tool_type = 'builtin';

-- 4. 验证更新结果
SELECT COUNT(*) as system_count FROM gb_atomic_tool WHERE tool_type = 'system';
SELECT COUNT(*) as builtin_count FROM gb_atomic_tool WHERE tool_type = 'builtin';

-- 5. 查看所有工具类型分布
SELECT tool_type, COUNT(*) as count 
FROM gb_atomic_tool 
GROUP BY tool_type;
