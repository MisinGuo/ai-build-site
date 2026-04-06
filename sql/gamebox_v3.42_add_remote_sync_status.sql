-- 为文章表添加远程文件同步状态追踪字段
-- 2026-01-31

-- 添加远程文件状态字段
ALTER TABLE `gb_articles` 
ADD COLUMN `remote_file_status` CHAR(1) NULL DEFAULT NULL COMMENT '远程文件状态：0-未检查 1-正常 2-不存在 3-异常' AFTER `storage_url`,
ADD COLUMN `last_sync_check_time` DATETIME NULL DEFAULT NULL COMMENT '最后同步检查时间' AFTER `remote_file_status`,
ADD COLUMN `remote_file_size` BIGINT NULL DEFAULT NULL COMMENT '远程文件大小（字节）' AFTER `last_sync_check_time`,
ADD COLUMN `remote_file_hash` VARCHAR(64) NULL DEFAULT NULL COMMENT '远程文件哈希值（用于检测变更）' AFTER `remote_file_size`;

-- 为远程文件状态添加索引
ALTER TABLE `gb_articles` ADD INDEX `idx_remote_file_status`(`remote_file_status` ASC) USING BTREE;

-- 注释说明
-- remote_file_status 字段说明：
-- 0 - 未检查：初始状态，还未检查过远程文件
-- 1 - 正常：远程文件存在且状态正常
-- 2 - 不存在：远程文件不存在（可能被手动删除或未发布）
-- 3 - 异常：远程文件存在但有问题（如大小为0、无法访问等）
