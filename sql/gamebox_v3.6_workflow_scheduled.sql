-- =====================================================
-- 工作流定时触发功能 - 数据库迁移脚本
-- 版本: v3.6
-- 说明: 为 gb_workflow 表添加 schedule_expression 字段，支持 Cron 定时触发
-- =====================================================

ALTER TABLE `gb_workflow`
    ADD COLUMN `schedule_expression` varchar(100) DEFAULT NULL COMMENT 'Cron表达式（triggerType=scheduled时使用，如: 0 0 8 * * ?）'
    AFTER `trigger_type`;

-- =====================================================
-- 修复 sys_job 表中非法的 cron 表达式
-- Quartz 要求 cron 必须包含 6 个字段：秒 分 时 日 月 周
-- '0 0 8 * *' 缺少"周"字段，修复为 '0 0 8 * * ?'
-- =====================================================
UPDATE `sys_job`
SET `cron_expression` = '0 0 8 * * ?'
WHERE `cron_expression` = '0 0 8 * *';

-- 修复 7 字段中 ? 写在了年字段（无效）的情况
-- '0 0 0 8 * * ?' → 正确写法 '0 0 0 8 * ?'（每月8号凌晨0点触发）
UPDATE `sys_job`
SET `cron_expression` = '0 0 0 8 * ?'
WHERE `cron_expression` = '0 0 0 8 * * ?';
