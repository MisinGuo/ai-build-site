-- 扩展 gb_games 表的 remark 字段长度以支持导入源数据
-- 创建时间：2025-12-26

ALTER TABLE `gb_games` MODIFY COLUMN `remark` TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '备注（支持保存导入源数据）';
