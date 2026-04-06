-- 添加content自动清理机制相关字段
-- 用于减少数据库大文本存储压力
-- 2026-01-31

-- 添加content清理相关字段
ALTER TABLE `gb_articles` 
ADD COLUMN `content_cleared_at` DATETIME NULL DEFAULT NULL COMMENT 'content字段清理时间（清理后此字段有值）' AFTER `content`,
ADD COLUMN `content_auto_clear_days` INT NULL DEFAULT 30 COMMENT 'content自动清理天数（发布后多少天自动清理，默认30天）' AFTER `content_cleared_at`,
ADD COLUMN `content_backup_key` VARCHAR(500) NULL DEFAULT NULL COMMENT 'content备份存储路径（用于恢复）' AFTER `content_auto_clear_days`;

-- 优化远程文件相关字段（已在v3.42添加，这里补充说明和索引）
-- remote_file_status: 0-未检查 1-正常 2-不存在 3-异常
-- last_sync_check_time: 最后一次检查远程文件的时间
-- remote_file_size: 远程文件大小（字节）
-- remote_file_hash: 远程文件哈希值（用于检测变更）

-- 添加索引以优化查询性能
ALTER TABLE `gb_articles` ADD INDEX `idx_content_cleared`(`content_cleared_at` ASC) USING BTREE;
ALTER TABLE `gb_articles` ADD INDEX `idx_auto_clear_days`(`content_auto_clear_days` ASC) USING BTREE;

-- 更新说明
-- 1. content_cleared_at: 当content字段被清理时，记录清理时间。NULL表示未清理
-- 2. content_auto_clear_days: 发布后多少天自动清理content。默认30天，可针对单篇文章调整
-- 3. content_backup_key: content清理前会备份到远程存储，此字段记录备份路径，用于恢复

-- 自动清理逻辑说明：
-- 1. 文章发布到远程存储后，系统每天定时检查
-- 2. 如果 publish_time + content_auto_clear_days < 当前时间，则执行清理
-- 3. 清理前先备份content到远程存储（content_backup_key）
-- 4. 清理后设置 content = NULL, content_cleared_at = 当前时间
-- 5. 用户可通过"恢复content"功能从备份恢复

-- 恢复策略：
-- 1. 从远程存储下载备份文件（根据content_backup_key）
-- 2. 恢复到content字段
-- 3. 清空 content_cleared_at 和 content_backup_key
