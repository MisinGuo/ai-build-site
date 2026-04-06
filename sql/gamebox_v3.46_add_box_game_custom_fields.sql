-- 为游戏盒子-游戏关联表添加自定义字段
-- 这些字段允许在不同盒子中对同一游戏进行个性化配置

ALTER TABLE `gb_box_game_relations`
ADD COLUMN `custom_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '自定义名称' AFTER `is_new`,
ADD COLUMN `custom_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '自定义描述' AFTER `custom_name`,
ADD COLUMN `custom_download_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '自定义下载链接' AFTER `custom_description`,
ADD COLUMN `view_count` int NULL DEFAULT 0 COMMENT '浏览量' AFTER `custom_download_url`;
