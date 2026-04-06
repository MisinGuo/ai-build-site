-- 为 gb_sites 表添加 code 字段
-- 日期：2025-12-15
-- 说明：修复站点编码字段无法保存的问题

ALTER TABLE `gb_sites` 
ADD COLUMN `code` varchar(50) NOT NULL COMMENT '站点编码' AFTER `name`,
ADD UNIQUE KEY `uk_code` (`code`);

-- 为已存在的记录生成默认的 code 值
-- 如果有已存在的站点数据，可以根据实际情况更新
-- UPDATE `gb_sites` SET `code` = CONCAT('site_', id) WHERE `code` IS NULL OR `code` = '';
