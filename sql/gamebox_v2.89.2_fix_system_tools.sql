-- ⚠️ 历史数据迁移脚本 - 仅在从旧版本升级时执行一次
-- 用途：清理旧版本中类型错误的系统工具记录
-- 清理后，系统启动时会自动重新创建正确的系统工具记录

-- 删除类型错误的旧记录（tool_type 为 builtin 但实际应该是 system 的工具）
DELETE FROM `gb_atomic_tool` 
WHERE `tool_code` = 'save_article' 
  AND `tool_type` != 'system';

-- 说明：
-- 1. 执行此脚本后，重启后端服务
-- 2. SystemToolAutoRegistrar 会自动创建正确的系统工具记录（tool_type = 'system'）
-- 3. 新安装的用户不需要执行此脚本
-- 4. 如果 save_article 已经是 system 类型，此脚本不会有任何影响（WHERE 条件会过滤掉）
