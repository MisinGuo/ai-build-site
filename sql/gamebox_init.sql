-- ----------------------------
-- 游戏盒子内容管理系统 - 业务表初始化脚本
-- 版本: v2.7.0
-- 日期: 2025-12-15
-- ----------------------------

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1、网站表
-- ----------------------------
DROP TABLE IF EXISTS `gb_sites`;
CREATE TABLE `gb_sites` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) NOT NULL COMMENT '网站名称',
  `domain` varchar(255) NOT NULL COMMENT '网站域名',
  `site_type` varchar(20) NOT NULL DEFAULT 'game' COMMENT '网站类型：game-游戏推广 drama-短剧推广 mixed-混合',
  `logo_url` varchar(500) DEFAULT NULL COMMENT '网站Logo',
  `favicon_url` varchar(500) DEFAULT NULL COMMENT '网站Favicon',
  `description` text COMMENT '网站描述',
  `seo_title` varchar(255) DEFAULT NULL COMMENT 'SEO标题',
  `seo_keywords` varchar(500) DEFAULT NULL COMMENT 'SEO关键词',
  `seo_description` varchar(500) DEFAULT NULL COMMENT 'SEO描述',
  `config` json DEFAULT NULL COMMENT '网站配置（JSON）',
  `storage_config_id` bigint DEFAULT NULL COMMENT '默认存储配置ID',
  `default_locale` varchar(10) DEFAULT 'zh-CN' COMMENT '默认语言',
  `supported_locales` json DEFAULT NULL COMMENT '支持的语言列表（JSON数组）',
  `i18n_mode` varchar(20) DEFAULT 'subdirectory' COMMENT '多语言模式：subdirectory-子目录 subdomain-子域名 query-参数',
  `status` char(1) DEFAULT '1' COMMENT '状态：0-禁用 1-启用',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_domain` (`domain`),
  KEY `idx_site_type` (`site_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='网站表';

-- ----------------------------
-- 2、网站语言配置表
-- ----------------------------
DROP TABLE IF EXISTS `gb_site_locales`;
CREATE TABLE `gb_site_locales` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `site_id` bigint NOT NULL COMMENT '网站ID',
  `locale` varchar(10) NOT NULL COMMENT '语言代码（如 zh-CN, en-US, ja-JP）',
  `locale_name` varchar(50) NOT NULL COMMENT '语言名称（如 简体中文, English）',
  `native_name` varchar(50) DEFAULT NULL COMMENT '语言本地名称',
  `is_default` char(1) DEFAULT '0' COMMENT '是否默认语言：0-否 1-是',
  `is_enabled` char(1) DEFAULT '1' COMMENT '是否启用：0-否 1-是',
  `storage_config_id` bigint DEFAULT NULL COMMENT '该语言专用存储配置ID',
  `storage_path_template` varchar(500) DEFAULT '{locale}/{category}/{slug}' COMMENT '存储路径模板',
  `url_prefix` varchar(100) DEFAULT NULL COMMENT 'URL前缀',
  `url_template` varchar(500) DEFAULT '/{locale}/{category}/{slug}' COMMENT '发布URL模板',
  `domain_override` varchar(255) DEFAULT NULL COMMENT '域名覆盖',
  `seo_title_template` varchar(255) DEFAULT NULL COMMENT 'SEO标题模板',
  `seo_description_template` varchar(500) DEFAULT NULL COMMENT 'SEO描述模板',
  `ai_prompt_suffix` text COMMENT 'AI生成时的语言提示词后缀',
  `translation_prompt` text COMMENT '翻译提示词模板',
  `sort_order` int DEFAULT 0 COMMENT '排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_site_locale` (`site_id`, `locale`),
  KEY `idx_site_id` (`site_id`),
  KEY `idx_is_enabled` (`is_enabled`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='网站语言配置表';

-- ----------------------------
-- 3、对象存储配置表
-- ----------------------------
DROP TABLE IF EXISTS `gb_storage_configs`;
CREATE TABLE `gb_storage_configs` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `site_id` bigint DEFAULT NULL COMMENT '所属网站ID（NULL表示全局配置）',
  `name` varchar(100) NOT NULL COMMENT '配置名称',
  `code` varchar(50) NOT NULL COMMENT '配置代码',
  `storage_type` varchar(20) NOT NULL COMMENT '存储类型：github/minio/r2/oss/cos/s3',
  `storage_purpose` varchar(20) DEFAULT 'mixed' COMMENT '存储用途：article-文章 resource-资源 mixed-混合',
  `is_default` char(1) DEFAULT '0' COMMENT '是否默认：0-否 1-是',
  `priority` int DEFAULT 100 COMMENT '优先级',
  `github_owner` varchar(100) DEFAULT NULL COMMENT 'GitHub 用户名/组织名',
  `github_repo` varchar(100) DEFAULT NULL COMMENT 'GitHub 仓库名',
  `github_branch` varchar(50) DEFAULT 'main' COMMENT 'GitHub 分支',
  `github_token` varchar(500) DEFAULT NULL COMMENT 'GitHub Token（加密存储）',
  `github_path_prefix` varchar(255) DEFAULT '' COMMENT 'GitHub 路径前缀',
  `minio_endpoint` varchar(255) DEFAULT NULL COMMENT 'MinIO 服务地址',
  `minio_port` int DEFAULT 9000 COMMENT 'MinIO 端口',
  `minio_use_ssl` char(1) DEFAULT '0' COMMENT '是否使用SSL',
  `minio_access_key` varchar(255) DEFAULT NULL COMMENT 'MinIO Access Key',
  `minio_secret_key` varchar(500) DEFAULT NULL COMMENT 'MinIO Secret Key（加密存储）',
  `minio_bucket` varchar(100) DEFAULT NULL COMMENT 'MinIO Bucket名称',
  `minio_region` varchar(50) DEFAULT NULL COMMENT 'MinIO Region',
  `r2_account_id` varchar(100) DEFAULT NULL COMMENT 'R2 Account ID',
  `r2_access_key` varchar(255) DEFAULT NULL COMMENT 'R2 Access Key',
  `r2_secret_key` varchar(500) DEFAULT NULL COMMENT 'R2 Secret Key（加密存储）',
  `r2_bucket` varchar(100) DEFAULT NULL COMMENT 'R2 Bucket名称',
  `r2_public_url` varchar(500) DEFAULT NULL COMMENT 'R2 公开访问URL',
  `oss_endpoint` varchar(255) DEFAULT NULL COMMENT 'OSS Endpoint',
  `oss_access_key` varchar(255) DEFAULT NULL COMMENT 'OSS Access Key',
  `oss_secret_key` varchar(500) DEFAULT NULL COMMENT 'OSS Secret Key（加密存储）',
  `oss_bucket` varchar(100) DEFAULT NULL COMMENT 'OSS Bucket名称',
  `oss_region` varchar(50) DEFAULT NULL COMMENT 'OSS Region',
  `cos_secret_id` varchar(255) DEFAULT NULL COMMENT 'COS Secret ID',
  `cos_secret_key` varchar(500) DEFAULT NULL COMMENT 'COS Secret Key（加密存储）',
  `cos_bucket` varchar(100) DEFAULT NULL COMMENT 'COS Bucket名称',
  `cos_region` varchar(50) DEFAULT NULL COMMENT 'COS Region',
  `cdn_url` varchar(500) DEFAULT NULL COMMENT 'CDN加速地址',
  `custom_domain` varchar(255) DEFAULT NULL COMMENT '自定义访问域名',
  `base_path` varchar(255) DEFAULT '' COMMENT '基础路径前缀',
  `max_file_size` bigint DEFAULT 10485760 COMMENT '最大文件大小（字节）',
  `allowed_extensions` varchar(500) DEFAULT 'md,json,txt,jpg,png,gif,webp,mp4,webm' COMMENT '允许的文件扩展名',
  `capacity_limit` bigint DEFAULT NULL COMMENT '容量上限（字节）',
  `used_capacity` bigint DEFAULT 0 COMMENT '已使用容量（字节）',
  `file_count` int DEFAULT 0 COMMENT '文件数量',
  `file_count_limit` int DEFAULT NULL COMMENT '文件数量上限',
  `capacity_warning_threshold` decimal(5,2) DEFAULT 80.00 COMMENT '容量预警阈值（百分比）',
  `is_capacity_full` char(1) DEFAULT '0' COMMENT '容量是否已满：0-否 1-是',
  `capacity_full_at` datetime DEFAULT NULL COMMENT '容量满时间',
  `fallback_storage_id` bigint DEFAULT NULL COMMENT '备用存储配置ID',
  `auto_switch_on_full` char(1) DEFAULT '1' COMMENT '容量满时是否自动切换：0-否 1-是',
  `last_health_check` datetime DEFAULT NULL COMMENT '最后健康检查时间',
  `health_status` varchar(20) DEFAULT 'unknown' COMMENT '健康状态',
  `health_message` varchar(500) DEFAULT NULL COMMENT '健康状态信息',
  `description` text COMMENT '配置描述',
  `status` char(1) DEFAULT '1' COMMENT '状态：0-禁用 1-启用',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_site_id` (`site_id`),
  KEY `idx_storage_type` (`storage_type`),
  KEY `idx_is_default` (`is_default`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='对象存储配置表';

-- ----------------------------
-- 4、分类表（通用）
-- ----------------------------
DROP TABLE IF EXISTS `gb_categories`;
CREATE TABLE `gb_categories` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `site_id` bigint DEFAULT NULL COMMENT '所属网站ID（NULL表示全局分类）',
  `parent_id` bigint DEFAULT 0 COMMENT '父分类ID，0为顶级',
  `category_type` varchar(20) DEFAULT 'game' COMMENT '分类类型：game-游戏 drama-短剧 article-文章 website-网站 gamebox-游戏盒子 document-文档 other-其他',
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `slug` varchar(50) NOT NULL COMMENT '分类标识（URL友好）',
  `icon` varchar(100) DEFAULT NULL COMMENT '分类图标（emoji或icon类名）',
  `description` varchar(255) DEFAULT NULL COMMENT '分类描述',
  `sort_order` int DEFAULT 0 COMMENT '排序序号',
  `document_count` int DEFAULT 0 COMMENT '关联文档数量',
  `status` char(1) DEFAULT '1' COMMENT '状态：0-禁用 1-启用',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_site_slug` (`site_id`, `slug`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_category_type` (`category_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通用分类表（支持游戏、短剧、文章、网站、游戏盒子、文档等多种类型）';

-- ----------------------------
-- 5、游戏表
-- ----------------------------
DROP TABLE IF EXISTS `gb_games`;
CREATE TABLE `gb_games` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `site_id` bigint DEFAULT NULL COMMENT '所属网站ID',
  `name` varchar(100) NOT NULL COMMENT '游戏名称',
  `subtitle` varchar(200) DEFAULT NULL COMMENT '游戏副标题/版本说明',
  `short_name` varchar(50) DEFAULT NULL COMMENT '游戏简称',
  `category` varchar(50) DEFAULT NULL COMMENT '游戏分类（仙侠、传奇等）',
  `game_type` varchar(20) DEFAULT 'official' COMMENT '游戏类型：official-官方 discount-折扣 bt-BT版 coming-即将上线',
  `icon_url` varchar(500) DEFAULT NULL COMMENT '游戏图标URL',
  `cover_url` varchar(500) DEFAULT NULL COMMENT '游戏封面URL',
  `screenshots` json DEFAULT NULL COMMENT '游戏截图（JSON数组）',
  `video_url` varchar(500) DEFAULT NULL COMMENT '游戏视频URL',
  `description` text COMMENT '游戏描述',
  `promotion_desc` text COMMENT '推广说明',
  `developer` varchar(100) DEFAULT NULL COMMENT '开发商',
  `publisher` varchar(100) DEFAULT NULL COMMENT '发行商',
  `package_name` varchar(200) DEFAULT NULL COMMENT '包名',
  `version` varchar(50) DEFAULT NULL COMMENT '版本号',
  `size` varchar(50) DEFAULT NULL COMMENT '安装包大小',
  `download_url` varchar(500) DEFAULT NULL COMMENT '通用下载链接',
  `android_url` varchar(500) DEFAULT NULL COMMENT '安卓下载链接',
  `ios_url` varchar(500) DEFAULT NULL COMMENT 'iOS下载链接',
  `apk_url` varchar(500) DEFAULT NULL COMMENT 'APK直接下载',
  `device_support` varchar(20) DEFAULT 'both' COMMENT '设备支持：android-安卓 ios-iOS both-双端',
  `discount_label` varchar(20) DEFAULT NULL COMMENT '折扣标签',
  `first_charge_domestic` decimal(4,2) DEFAULT NULL COMMENT '首充折扣-国内',
  `first_charge_overseas` decimal(4,2) DEFAULT NULL COMMENT '首充折扣-海外',
  `recharge_domestic` decimal(4,2) DEFAULT NULL COMMENT '续充折扣-国内',
  `recharge_overseas` decimal(4,2) DEFAULT NULL COMMENT '续充折扣-海外',
  `has_support` char(1) DEFAULT '0' COMMENT '是否有扶持：0-无 1-有',
  `support_desc` text COMMENT '扶持说明',
  `has_low_deduct_coupon` char(1) DEFAULT '0' COMMENT '是否有低扣券领取：0-无 1-有',
  `low_deduct_coupon_url` varchar(500) DEFAULT NULL COMMENT '低扣券领取链接',
  `download_count` int DEFAULT 0 COMMENT '下载次数',
  `rating` decimal(2,1) DEFAULT 0.0 COMMENT '评分（0-5）',
  `features` json DEFAULT NULL COMMENT '游戏特性（JSON数组）',
  `tags` varchar(255) DEFAULT NULL COMMENT '标签，逗号分隔',
  `launch_time` datetime DEFAULT NULL COMMENT '上架/开服时间',
  `is_new` char(1) DEFAULT '0' COMMENT '是否新游：0-否 1-是',
  `is_hot` char(1) DEFAULT '0' COMMENT '是否热门：0-否 1-是',
  `is_recommend` char(1) DEFAULT '0' COMMENT '是否推荐：0-否 1-是',
  `sort_order` int DEFAULT 0 COMMENT '排序',
  `status` char(1) DEFAULT '1' COMMENT '状态：0-下架 1-上架',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_site_id` (`site_id`),
  KEY `idx_name` (`name`),
  KEY `idx_category` (`category`),
  KEY `idx_game_type` (`game_type`),
  KEY `idx_device_support` (`device_support`),
  KEY `idx_launch_time` (`launch_time`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='游戏表';

-- ----------------------------
-- 6、游戏盒子表
-- ----------------------------
DROP TABLE IF EXISTS `gb_game_boxes`;
CREATE TABLE `gb_game_boxes` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `site_id` bigint DEFAULT NULL COMMENT '所属网站ID',
  `name` varchar(100) NOT NULL COMMENT '盒子名称',
  `logo_url` varchar(500) DEFAULT NULL COMMENT '盒子Logo',
  `banner_url` varchar(500) DEFAULT NULL COMMENT '盒子Banner图',
  `qrcode_url` varchar(500) DEFAULT NULL COMMENT '盒子二维码图片URL',
  `description` text COMMENT '盒子描述',
  `official_url` varchar(500) DEFAULT NULL COMMENT '官网地址',
  `download_url` varchar(500) DEFAULT NULL COMMENT '通用下载地址',
  `android_url` varchar(500) DEFAULT NULL COMMENT '安卓下载',
  `ios_url` varchar(500) DEFAULT NULL COMMENT 'iOS下载',
  `promote_url_1` varchar(500) DEFAULT NULL COMMENT '推广链接①',
  `promote_url_2` varchar(500) DEFAULT NULL COMMENT '推广链接②',
  `promote_url_3` varchar(500) DEFAULT NULL COMMENT '推广链接③',
  `register_download_url` varchar(500) DEFAULT NULL COMMENT '先注册再下载链接④',
  `discount_rate` decimal(3,2) DEFAULT 1.00 COMMENT '折扣率（0.1-1.0）',
  `features` json DEFAULT NULL COMMENT '特色功能（JSON数组）',
  `game_count` int DEFAULT 0 COMMENT '收录游戏数',
  `sort_order` int DEFAULT 0 COMMENT '排序',
  `status` char(1) DEFAULT '1' COMMENT '状态：0-下架 1-上架',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_site_id` (`site_id`),
  KEY `idx_name` (`name`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='游戏盒子表';

-- ----------------------------
-- 7、游戏盒子-游戏关联表
-- ----------------------------
DROP TABLE IF EXISTS `gb_box_game_relations`;
CREATE TABLE `gb_box_game_relations` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `box_id` bigint NOT NULL COMMENT '游戏盒子ID',
  `game_id` bigint NOT NULL COMMENT '游戏ID',
  `discount_label` varchar(20) DEFAULT NULL COMMENT '折扣标签',
  `first_charge_domestic` decimal(4,2) DEFAULT NULL COMMENT '首充折扣-国内',
  `first_charge_overseas` decimal(4,2) DEFAULT NULL COMMENT '首充折扣-海外',
  `recharge_domestic` decimal(4,2) DEFAULT NULL COMMENT '续充折扣-国内',
  `recharge_overseas` decimal(4,2) DEFAULT NULL COMMENT '续充折扣-海外',
  `has_support` char(1) DEFAULT '0' COMMENT '是否有扶持：0-无 1-有',
  `support_desc` text COMMENT '扶持说明',
  `download_url` varchar(500) DEFAULT NULL COMMENT '游戏下载链接',
  `promote_url` varchar(500) DEFAULT NULL COMMENT '游戏推广网站链接',
  `qrcode_url` varchar(500) DEFAULT NULL COMMENT '游戏二维码URL',
  `promote_text` text COMMENT '推广语',
  `poster_url` varchar(500) DEFAULT NULL COMMENT '宣传卡片/海报URL',
  `is_featured` char(1) DEFAULT '0' COMMENT '是否推荐：0-否 1-是',
  `is_exclusive` char(1) DEFAULT '0' COMMENT '是否独占：0-否 1-是',
  `is_new` char(1) DEFAULT '0' COMMENT '是否新游：0-否 1-是',
  `sort_order` int DEFAULT 0 COMMENT '排序',
  `added_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_box_game` (`box_id`, `game_id`),
  KEY `idx_box_id` (`box_id`),
  KEY `idx_game_id` (`game_id`),
  KEY `idx_is_featured` (`is_featured`),
  KEY `idx_is_new` (`is_new`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='游戏盒子-游戏关联表';

-- ----------------------------
-- 8、游戏活动表
-- ----------------------------
DROP TABLE IF EXISTS `gb_game_activities`;
CREATE TABLE `gb_game_activities` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `game_id` bigint NOT NULL COMMENT '游戏ID',
  `box_id` bigint DEFAULT NULL COMMENT '游戏盒子ID',
  `name` varchar(100) NOT NULL COMMENT '活动名称',
  `activity_type` varchar(20) DEFAULT 'activity' COMMENT '类型：activity-活动 server-开服 other-其他',
  `description` text COMMENT '活动描述',
  `banner_url` varchar(500) DEFAULT NULL COMMENT '活动Banner',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `is_permanent` char(1) DEFAULT '0' COMMENT '是否永久：0-否 1-是',
  `sort_order` int DEFAULT 0 COMMENT '排序',
  `status` char(1) DEFAULT '1' COMMENT '状态：0-禁用 1-启用',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_game_id` (`game_id`),
  KEY `idx_box_id` (`box_id`),
  KEY `idx_activity_type` (`activity_type`),
  KEY `idx_time_range` (`start_time`, `end_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='游戏活动表';

-- ----------------------------
-- 9、游戏开服表
-- ----------------------------
DROP TABLE IF EXISTS `gb_game_servers`;
CREATE TABLE `gb_game_servers` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `game_id` bigint NOT NULL COMMENT '游戏ID',
  `box_id` bigint DEFAULT NULL COMMENT '游戏盒子ID',
  `server_name` varchar(100) NOT NULL COMMENT '服务器名称',
  `server_no` varchar(50) DEFAULT NULL COMMENT '服务器编号',
  `open_time` datetime NOT NULL COMMENT '开服时间',
  `server_type` varchar(20) DEFAULT 'new' COMMENT '类型：new-新服 merge-合服 test-测试服',
  `description` text COMMENT '开服说明',
  `is_recommend` char(1) DEFAULT '0' COMMENT '是否推荐：0-否 1-是',
  `status` char(1) DEFAULT '1' COMMENT '状态：0-已关闭 1-正常 2-火爆',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_game_id` (`game_id`),
  KEY `idx_box_id` (`box_id`),
  KEY `idx_open_time` (`open_time`),
  KEY `idx_server_type` (`server_type`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='游戏开服表';

-- ----------------------------
-- 10、游戏礼包表
-- ----------------------------
DROP TABLE IF EXISTS `gb_game_giftcodes`;
CREATE TABLE `gb_game_giftcodes` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `game_id` bigint NOT NULL COMMENT '游戏ID',
  `box_id` bigint DEFAULT NULL COMMENT '游戏盒子ID',
  `name` varchar(100) NOT NULL COMMENT '礼包名称',
  `gift_type` varchar(20) DEFAULT 'newbie' COMMENT '礼包类型：newbie-新手卡 reserve-预约 community-社区 welfare-福利',
  `content` text COMMENT '礼包内容说明',
  `items` json DEFAULT NULL COMMENT '礼包物品（JSON数组）',
  `code` varchar(100) DEFAULT NULL COMMENT '礼包码',
  `total_count` int DEFAULT 9999 COMMENT '总数量',
  `remain_count` int DEFAULT 9999 COMMENT '剩余数量',
  `limit_per_user` int DEFAULT 1 COMMENT '每人限领',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `is_exclusive` char(1) DEFAULT '0' COMMENT '是否独占：0-否 1-是',
  `sort_order` int DEFAULT 0 COMMENT '排序',
  `status` char(1) DEFAULT '1' COMMENT '状态：0-禁用 1-启用',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_game_id` (`game_id`),
  KEY `idx_box_id` (`box_id`),
  KEY `idx_gift_type` (`gift_type`),
  KEY `idx_time_range` (`start_time`, `end_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='游戏礼包表';

-- ----------------------------
-- 11、游戏素材表
-- ----------------------------
DROP TABLE IF EXISTS `gb_game_materials`;
CREATE TABLE `gb_game_materials` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `game_id` bigint NOT NULL COMMENT '游戏ID',
  `box_id` bigint DEFAULT NULL COMMENT '游戏盒子ID',
  `material_type` varchar(20) NOT NULL COMMENT '素材类型：icon-图标 cover-封面 screenshot-截图 video-视频 poster-海报 qrcode-二维码',
  `name` varchar(100) DEFAULT NULL COMMENT '素材名称',
  `url` varchar(500) NOT NULL COMMENT '素材URL',
  `thumbnail_url` varchar(500) DEFAULT NULL COMMENT '缩略图URL',
  `file_size` int DEFAULT 0 COMMENT '文件大小（字节）',
  `width` int DEFAULT 0 COMMENT '宽度（像素）',
  `height` int DEFAULT 0 COMMENT '高度（像素）',
  `duration` int DEFAULT 0 COMMENT '时长（秒，视频用）',
  `sort_order` int DEFAULT 0 COMMENT '排序',
  `status` char(1) DEFAULT '1' COMMENT '状态：0-禁用 1-启用',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_game_id` (`game_id`),
  KEY `idx_box_id` (`box_id`),
  KEY `idx_material_type` (`material_type`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='游戏素材表';

-- ----------------------------
-- 12、短剧厂商表
-- ----------------------------
DROP TABLE IF EXISTS `gb_drama_vendors`;
CREATE TABLE `gb_drama_vendors` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `site_id` bigint DEFAULT NULL COMMENT '所属网站ID',
  `name` varchar(100) NOT NULL COMMENT '厂商名称',
  `short_name` varchar(50) DEFAULT NULL COMMENT '厂商简称',
  `logo_url` varchar(500) DEFAULT NULL COMMENT '厂商Logo',
  `banner_url` varchar(500) DEFAULT NULL COMMENT '厂商Banner图',
  `description` text COMMENT '厂商描述',
  `official_url` varchar(500) DEFAULT NULL COMMENT '官网地址',
  `app_download_url` varchar(500) DEFAULT NULL COMMENT 'APP下载地址',
  `android_url` varchar(500) DEFAULT NULL COMMENT '安卓下载',
  `ios_url` varchar(500) DEFAULT NULL COMMENT 'iOS下载',
  `wechat_mini_appid` varchar(100) DEFAULT NULL COMMENT '微信小程序AppID',
  `contact_info` json DEFAULT NULL COMMENT '联系方式（JSON）',
  `cooperation_type` varchar(50) DEFAULT NULL COMMENT '合作类型：cps/cpa/cpm',
  `commission_rate` decimal(5,4) DEFAULT 0.0000 COMMENT '佣金比例',
  `features` json DEFAULT NULL COMMENT '特色功能（JSON数组）',
  `drama_count` int DEFAULT 0 COMMENT '收录短剧数',
  `sort_order` int DEFAULT 0 COMMENT '排序',
  `status` char(1) DEFAULT '1' COMMENT '状态：0-下架 1-上架',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_site_id` (`site_id`),
  KEY `idx_name` (`name`),
  KEY `idx_cooperation_type` (`cooperation_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='短剧厂商表';

-- ----------------------------
-- 13、短剧表
-- ----------------------------
DROP TABLE IF EXISTS `gb_dramas`;
CREATE TABLE `gb_dramas` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `site_id` bigint DEFAULT NULL COMMENT '所属网站ID',
  `vendor_id` bigint DEFAULT NULL COMMENT '所属厂商ID',
  `name` varchar(200) NOT NULL COMMENT '短剧名称',
  `alias_name` varchar(200) DEFAULT NULL COMMENT '别名',
  `cover_url` varchar(500) DEFAULT NULL COMMENT '封面图URL',
  `poster_url` varchar(500) DEFAULT NULL COMMENT '海报图URL',
  `screenshots` json DEFAULT NULL COMMENT '剧照（JSON数组）',
  `description` text COMMENT '剧情简介',
  `category` varchar(50) DEFAULT NULL COMMENT '剧情分类',
  `genre` varchar(100) DEFAULT NULL COMMENT '题材标签',
  `director` varchar(100) DEFAULT NULL COMMENT '导演',
  `actors` json DEFAULT NULL COMMENT '主演（JSON数组）',
  `episode_count` int DEFAULT 0 COMMENT '总集数',
  `episode_duration` int DEFAULT 0 COMMENT '每集时长（秒）',
  `total_duration` int DEFAULT 0 COMMENT '总时长（秒）',
  `release_date` date DEFAULT NULL COMMENT '上线日期',
  `source_platform` varchar(50) DEFAULT NULL COMMENT '首发平台',
  `play_url` varchar(500) DEFAULT NULL COMMENT '播放链接',
  `play_count` bigint DEFAULT 0 COMMENT '播放次数',
  `like_count` int DEFAULT 0 COMMENT '点赞次数',
  `collect_count` int DEFAULT 0 COMMENT '收藏次数',
  `rating` decimal(2,1) DEFAULT 0.0 COMMENT '评分（0-10）',
  `is_free` char(1) DEFAULT '0' COMMENT '是否免费：0-否 1-是',
  `price` decimal(10,2) DEFAULT 0.00 COMMENT '价格',
  `tags` varchar(255) DEFAULT NULL COMMENT '标签，逗号分隔',
  `is_hot` char(1) DEFAULT '0' COMMENT '是否热门：0-否 1-是',
  `is_new` char(1) DEFAULT '0' COMMENT '是否新剧：0-否 1-是',
  `is_recommend` char(1) DEFAULT '0' COMMENT '是否推荐：0-否 1-是',
  `sort_order` int DEFAULT 0 COMMENT '排序',
  `status` char(1) DEFAULT '1' COMMENT '状态：0-下架 1-上架',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_site_id` (`site_id`),
  KEY `idx_vendor_id` (`vendor_id`),
  KEY `idx_name` (`name`),
  KEY `idx_category` (`category`),
  KEY `idx_release_date` (`release_date`),
  KEY `idx_is_hot` (`is_hot`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='短剧表';

-- ----------------------------
-- 14、AI平台配置表
-- ----------------------------
DROP TABLE IF EXISTS `gb_ai_platforms`;
CREATE TABLE `gb_ai_platforms` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) NOT NULL COMMENT '平台名称',
  `platform_code` varchar(50) NOT NULL COMMENT '平台代码',
  `api_endpoint` varchar(500) NOT NULL COMMENT 'API地址',
  `api_key` varchar(500) DEFAULT NULL COMMENT 'API Key（加密存储）',
  `api_secret` varchar(500) DEFAULT NULL COMMENT 'API Secret（加密存储）',
  `model_name` varchar(100) DEFAULT NULL COMMENT '默认模型名称',
  `available_models` json DEFAULT NULL COMMENT '可用模型列表',
  `config` json DEFAULT NULL COMMENT '平台配置',
  `max_tokens` int DEFAULT 4096 COMMENT '最大Token数',
  `temperature` decimal(3,2) DEFAULT 0.70 COMMENT '创作温度',
  `rate_limit` int DEFAULT 60 COMMENT '速率限制',
  `cost_per_1k_tokens` decimal(10,6) DEFAULT 0.000000 COMMENT '每1000 Token成本',
  `is_default` char(1) DEFAULT '0' COMMENT '是否默认：0-否 1-是',
  `status` char(1) DEFAULT '1' COMMENT '状态：0-禁用 1-启用',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_is_default` (`is_default`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI平台配置表';

-- ----------------------------
-- 15、提示词模板表
-- ----------------------------
DROP TABLE IF EXISTS `gb_prompt_templates`;
CREATE TABLE `gb_prompt_templates` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `site_id` bigint DEFAULT NULL COMMENT '所属网站ID',
  `name` varchar(100) NOT NULL COMMENT '模板名称',
  `template_code` varchar(50) NOT NULL COMMENT '模板代码',
  `template_type` varchar(30) NOT NULL COMMENT '模板类型',
  `description` varchar(500) DEFAULT NULL COMMENT '模板描述',
  `system_prompt` text COMMENT '系统提示词',
  `user_prompt_template` text NOT NULL COMMENT '用户提示词模板',
  `output_format` text COMMENT '输出格式说明',
  `example_output` text COMMENT '示例输出',
  `variables` json DEFAULT NULL COMMENT '模板变量定义',
  `default_values` json DEFAULT NULL COMMENT '变量默认值',
  `ai_platform_id` bigint DEFAULT NULL COMMENT '推荐AI平台ID',
  `model_name` varchar(100) DEFAULT NULL COMMENT '推荐模型',
  `temperature` decimal(3,2) DEFAULT 0.70 COMMENT '推荐温度',
  `max_tokens` int DEFAULT 4096 COMMENT '推荐最大Token数',
  `use_count` int DEFAULT 0 COMMENT '使用次数',
  `success_count` int DEFAULT 0 COMMENT '成功次数',
  `avg_quality_score` decimal(3,2) DEFAULT NULL COMMENT '平均质量评分',
  `is_public` char(1) DEFAULT '0' COMMENT '是否公开：0-私有 1-公开',
  `sort_order` int DEFAULT 0 COMMENT '排序',
  `status` char(1) DEFAULT '1' COMMENT '状态：0-禁用 1-启用',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_template_code` (`template_code`),
  KEY `idx_site_id` (`site_id`),
  KEY `idx_template_type` (`template_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='提示词模板表';

-- ----------------------------
-- 16、文章表
-- ----------------------------
DROP TABLE IF EXISTS `gb_articles`;
CREATE TABLE `gb_articles` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `site_id` bigint NOT NULL COMMENT '所属网站ID',
  `slug` varchar(255) NOT NULL COMMENT '文章路径标识',
  `locale` varchar(10) NOT NULL DEFAULT 'zh-CN' COMMENT '文章语言',
  `master_article_id` bigint DEFAULT NULL COMMENT '主文章ID',
  `title` varchar(255) NOT NULL COMMENT '文章标题',
  `subtitle` varchar(255) DEFAULT NULL COMMENT '副标题',
  `category_id` bigint DEFAULT NULL COMMENT '分类ID',
  `author` varchar(100) DEFAULT NULL COMMENT '作者',
  `keywords` varchar(500) DEFAULT NULL COMMENT '关键词',
  `description` text COMMENT '文章摘要',
  `content` longtext COMMENT '文章内容',
  `content_type` varchar(20) DEFAULT 'markdown' COMMENT '内容类型',
  `cover_url` varchar(500) DEFAULT NULL COMMENT '封面图URL',
  `storage_config_id` bigint DEFAULT NULL COMMENT '文章内容存储配置ID',
  `storage_key` varchar(500) DEFAULT NULL COMMENT '存储键/路径',
  `storage_url` varchar(1000) DEFAULT NULL COMMENT '完整访问URL',
  `resource_storage_config_id` bigint DEFAULT NULL COMMENT '资源存储配置ID',
  `resource_base_path` varchar(500) DEFAULT NULL COMMENT '资源基础路径',
  `is_ai_generated` char(1) DEFAULT '0' COMMENT '是否AI生成：0-否 1-是',
  `is_ai_translated` char(1) DEFAULT '0' COMMENT '是否AI翻译：0-否 1-是',
  `prompt_template_id` bigint DEFAULT NULL COMMENT '使用的提示词模板ID',
  `generation_count` int DEFAULT 0 COMMENT '生成次数',
  `last_generation_id` bigint DEFAULT NULL COMMENT '最后一次生成记录ID',
  `publish_year` int DEFAULT NULL COMMENT '发布年份',
  `publish_month` int DEFAULT NULL COMMENT '发布月份',
  `publish_day` int DEFAULT NULL COMMENT '发布日期',
  `word_count` int DEFAULT 0 COMMENT '字数统计',
  `reading_time` int DEFAULT 0 COMMENT '预估阅读时间（分钟）',
  `resource_count` int DEFAULT 0 COMMENT '资源数量',
  `view_count` int DEFAULT 0 COMMENT '浏览次数',
  `like_count` int DEFAULT 0 COMMENT '点赞次数',
  `comment_count` int DEFAULT 0 COMMENT '评论次数',
  `share_count` int DEFAULT 0 COMMENT '分享次数',
  `is_published` char(1) DEFAULT '0' COMMENT '是否发布：0-否 1-是',
  `is_top` char(1) DEFAULT '0' COMMENT '是否置顶：0-否 1-是',
  `is_recommend` char(1) DEFAULT '0' COMMENT '是否推荐：0-否 1-是',
  `sort_order` int DEFAULT 0 COMMENT '排序',
  `published_at` datetime DEFAULT NULL COMMENT '发布时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_site_locale_slug` (`site_id`, `locale`, `slug`),
  KEY `idx_site_id` (`site_id`),
  KEY `idx_locale` (`locale`),
  KEY `idx_master_article` (`master_article_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_storage_config` (`storage_config_id`),
  KEY `idx_is_published` (`is_published`),
  KEY `idx_published_at` (`published_at`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章表';

-- ----------------------------
-- 17、文章生成记录表
-- ----------------------------
DROP TABLE IF EXISTS `gb_article_generations`;
CREATE TABLE `gb_article_generations` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `article_id` bigint DEFAULT NULL COMMENT '文章ID',
  `site_id` bigint NOT NULL COMMENT '网站ID',
  `ai_platform_id` bigint NOT NULL COMMENT 'AI平台ID',
  `prompt_template_id` bigint DEFAULT NULL COMMENT '提示词模板ID',
  `model_name` varchar(100) DEFAULT NULL COMMENT '使用的模型',
  `prompt_content` text COMMENT '实际提示词内容',
  `generation_type` varchar(20) DEFAULT 'single' COMMENT '生成类型：single-单篇 batch-批量',
  `batch_task_id` bigint DEFAULT NULL COMMENT '批量任务ID',
  `locale` varchar(10) DEFAULT 'zh-CN' COMMENT '生成语言',
  `generation_status` varchar(20) DEFAULT 'pending' COMMENT '状态：pending-待执行 running-执行中 completed-已完成 failed-失败',
  `generated_title` varchar(255) DEFAULT NULL COMMENT '生成的标题',
  `generated_content` longtext COMMENT '生成的内容',
  `input_tokens` int DEFAULT 0 COMMENT '输入Token数',
  `output_tokens` int DEFAULT 0 COMMENT '输出Token数',
  `total_cost` decimal(10,6) DEFAULT 0.000000 COMMENT '总消耗费用',
  `generation_time` int DEFAULT 0 COMMENT '生成耗时（秒）',
  `error_message` text COMMENT '错误信息',
  `quality_score` decimal(3,2) DEFAULT NULL COMMENT '质量评分',
  `github_run_id` varchar(100) DEFAULT NULL COMMENT 'GitHub Action Run ID',
  `github_run_url` varchar(500) DEFAULT NULL COMMENT 'GitHub Action Run URL',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_article_id` (`article_id`),
  KEY `idx_site_id` (`site_id`),
  KEY `idx_ai_platform` (`ai_platform_id`),
  KEY `idx_batch_task` (`batch_task_id`),
  KEY `idx_generation_status` (`generation_status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章生成记录表';

-- ----------------------------
-- 18、文章产品关联表
-- ----------------------------
DROP TABLE IF EXISTS `gb_article_product_relations`;
CREATE TABLE `gb_article_product_relations` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `article_id` bigint NOT NULL COMMENT '文章ID',
  `product_type` varchar(20) NOT NULL COMMENT '产品类型：game-游戏 game_box-游戏盒子 drama-短剧',
  `product_id` bigint NOT NULL COMMENT '产品ID',
  `relation_type` varchar(20) DEFAULT 'auto' COMMENT '关联类型：auto-自动 manual-手动',
  `relevance_score` decimal(5,4) DEFAULT 0.0000 COMMENT '相关度评分',
  `is_primary` char(1) DEFAULT '0' COMMENT '是否主关联：0-否 1-是',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_article_product` (`article_id`, `product_type`, `product_id`),
  KEY `idx_article_id` (`article_id`),
  KEY `idx_product` (`product_type`, `product_id`),
  KEY `idx_relevance` (`relevance_score` DESC)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章产品关联表';

-- ----------------------------
-- 19、标签表
-- ----------------------------
DROP TABLE IF EXISTS `gb_tags`;
CREATE TABLE `gb_tags` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `site_id` bigint DEFAULT NULL COMMENT '所属网站ID',
  `tag_type` varchar(20) DEFAULT 'general' COMMENT '标签类型',
  `name` varchar(50) NOT NULL COMMENT '标签名称',
  `slug` varchar(50) NOT NULL COMMENT '标签标识',
  `color` varchar(20) DEFAULT NULL COMMENT '标签颜色',
  `use_count` int DEFAULT 0 COMMENT '使用次数',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_site_name` (`site_id`, `name`),
  KEY `idx_tag_type` (`tag_type`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';

SET FOREIGN_KEY_CHECKS = 1;
