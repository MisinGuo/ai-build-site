-- 为 gb_title_pool_batch 表添加 is_excluded 字段
-- 用于支持多站点排除功能

ALTER TABLE `gb_title_pool_batch` 
ADD COLUMN `is_excluded` int NULL DEFAULT 0 COMMENT '是否被排除（默认配置被某网站排除）：0-未排除 1-已排除' AFTER `status`;
