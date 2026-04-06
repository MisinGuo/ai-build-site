/*
 Navicat Premium Dump SQL

 Source Server         : local.zeusai.top-game-box
 Source Server Type    : MySQL
 Source Server Version : 80044 (8.0.44)
 Source Host           : local.zeusai.top:23306
 Source Schema         : ry-vue

 Target Server Type    : MySQL
 Target Server Version : 80044 (8.0.44)
 File Encoding         : 65001

 Date: 25/03/2026 13:21:45
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for QRTZ_BLOB_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_BLOB_TRIGGERS`;
CREATE TABLE `QRTZ_BLOB_TRIGGERS`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `blob_data` blob NULL COMMENT '存放持久化Trigger对象',
  PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
  CONSTRAINT `QRTZ_BLOB_TRIGGERS_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `QRTZ_TRIGGERS` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'Blob类型的触发器表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of QRTZ_BLOB_TRIGGERS
-- ----------------------------

-- ----------------------------
-- Table structure for QRTZ_CALENDARS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_CALENDARS`;
CREATE TABLE `QRTZ_CALENDARS`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调度名称',
  `calendar_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '日历名称',
  `calendar` blob NOT NULL COMMENT '存放持久化calendar对象',
  PRIMARY KEY (`sched_name`, `calendar_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '日历信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of QRTZ_CALENDARS
-- ----------------------------

-- ----------------------------
-- Table structure for QRTZ_CRON_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_CRON_TRIGGERS`;
CREATE TABLE `QRTZ_CRON_TRIGGERS`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `cron_expression` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'cron表达式',
  `time_zone_id` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '时区',
  PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
  CONSTRAINT `QRTZ_CRON_TRIGGERS_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `QRTZ_TRIGGERS` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'Cron类型的触发器表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of QRTZ_CRON_TRIGGERS
-- ----------------------------

-- ----------------------------
-- Table structure for QRTZ_FIRED_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_FIRED_TRIGGERS`;
CREATE TABLE `QRTZ_FIRED_TRIGGERS`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调度名称',
  `entry_id` varchar(95) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调度器实例id',
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `instance_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调度器实例名',
  `fired_time` bigint NOT NULL COMMENT '触发的时间',
  `sched_time` bigint NOT NULL COMMENT '定时器制定的时间',
  `priority` int NOT NULL COMMENT '优先级',
  `state` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '状态',
  `job_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '任务名称',
  `job_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '任务组名',
  `is_nonconcurrent` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否并发',
  `requests_recovery` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否接受恢复执行',
  PRIMARY KEY (`sched_name`, `entry_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '已触发的触发器表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of QRTZ_FIRED_TRIGGERS
-- ----------------------------

-- ----------------------------
-- Table structure for QRTZ_JOB_DETAILS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_JOB_DETAILS`;
CREATE TABLE `QRTZ_JOB_DETAILS`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调度名称',
  `job_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务名称',
  `job_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务组名',
  `description` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '相关介绍',
  `job_class_name` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '执行任务类名称',
  `is_durable` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '是否持久化',
  `is_nonconcurrent` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '是否并发',
  `is_update_data` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '是否更新数据',
  `requests_recovery` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '是否接受恢复执行',
  `job_data` blob NULL COMMENT '存放持久化job对象',
  PRIMARY KEY (`sched_name`, `job_name`, `job_group`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '任务详细信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of QRTZ_JOB_DETAILS
-- ----------------------------

-- ----------------------------
-- Table structure for QRTZ_LOCKS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_LOCKS`;
CREATE TABLE `QRTZ_LOCKS`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调度名称',
  `lock_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '悲观锁名称',
  PRIMARY KEY (`sched_name`, `lock_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '存储的悲观锁信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of QRTZ_LOCKS
-- ----------------------------

-- ----------------------------
-- Table structure for QRTZ_PAUSED_TRIGGER_GRPS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_PAUSED_TRIGGER_GRPS`;
CREATE TABLE `QRTZ_PAUSED_TRIGGER_GRPS`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调度名称',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  PRIMARY KEY (`sched_name`, `trigger_group`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '暂停的触发器表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of QRTZ_PAUSED_TRIGGER_GRPS
-- ----------------------------

-- ----------------------------
-- Table structure for QRTZ_SCHEDULER_STATE
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_SCHEDULER_STATE`;
CREATE TABLE `QRTZ_SCHEDULER_STATE`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调度名称',
  `instance_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '实例名称',
  `last_checkin_time` bigint NOT NULL COMMENT '上次检查时间',
  `checkin_interval` bigint NOT NULL COMMENT '检查间隔时间',
  PRIMARY KEY (`sched_name`, `instance_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '调度器状态表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of QRTZ_SCHEDULER_STATE
-- ----------------------------

-- ----------------------------
-- Table structure for QRTZ_SIMPLE_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_SIMPLE_TRIGGERS`;
CREATE TABLE `QRTZ_SIMPLE_TRIGGERS`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `repeat_count` bigint NOT NULL COMMENT '重复的次数统计',
  `repeat_interval` bigint NOT NULL COMMENT '重复的间隔时间',
  `times_triggered` bigint NOT NULL COMMENT '已经触发的次数',
  PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
  CONSTRAINT `QRTZ_SIMPLE_TRIGGERS_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `QRTZ_TRIGGERS` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '简单触发器的信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of QRTZ_SIMPLE_TRIGGERS
-- ----------------------------

-- ----------------------------
-- Table structure for QRTZ_SIMPROP_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_SIMPROP_TRIGGERS`;
CREATE TABLE `QRTZ_SIMPROP_TRIGGERS`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `str_prop_1` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'String类型的trigger的第一个参数',
  `str_prop_2` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'String类型的trigger的第二个参数',
  `str_prop_3` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'String类型的trigger的第三个参数',
  `int_prop_1` int NULL DEFAULT NULL COMMENT 'int类型的trigger的第一个参数',
  `int_prop_2` int NULL DEFAULT NULL COMMENT 'int类型的trigger的第二个参数',
  `long_prop_1` bigint NULL DEFAULT NULL COMMENT 'long类型的trigger的第一个参数',
  `long_prop_2` bigint NULL DEFAULT NULL COMMENT 'long类型的trigger的第二个参数',
  `dec_prop_1` decimal(13, 4) NULL DEFAULT NULL COMMENT 'decimal类型的trigger的第一个参数',
  `dec_prop_2` decimal(13, 4) NULL DEFAULT NULL COMMENT 'decimal类型的trigger的第二个参数',
  `bool_prop_1` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Boolean类型的trigger的第一个参数',
  `bool_prop_2` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Boolean类型的trigger的第二个参数',
  PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
  CONSTRAINT `QRTZ_SIMPROP_TRIGGERS_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `QRTZ_TRIGGERS` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '同步机制的行锁表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of QRTZ_SIMPROP_TRIGGERS
-- ----------------------------

-- ----------------------------
-- Table structure for QRTZ_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_TRIGGERS`;
CREATE TABLE `QRTZ_TRIGGERS`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '触发器的名字',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '触发器所属组的名字',
  `job_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'qrtz_job_details表job_name的外键',
  `job_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'qrtz_job_details表job_group的外键',
  `description` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '相关介绍',
  `next_fire_time` bigint NULL DEFAULT NULL COMMENT '上一次触发时间（毫秒）',
  `prev_fire_time` bigint NULL DEFAULT NULL COMMENT '下一次触发时间（默认为-1表示不触发）',
  `priority` int NULL DEFAULT NULL COMMENT '优先级',
  `trigger_state` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '触发器状态',
  `trigger_type` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '触发器的类型',
  `start_time` bigint NOT NULL COMMENT '开始时间',
  `end_time` bigint NULL DEFAULT NULL COMMENT '结束时间',
  `calendar_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '日程表名称',
  `misfire_instr` smallint NULL DEFAULT NULL COMMENT '补偿执行的策略',
  `job_data` blob NULL COMMENT '存放持久化job对象',
  PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
  INDEX `sched_name`(`sched_name` ASC, `job_name` ASC, `job_group` ASC) USING BTREE,
  CONSTRAINT `QRTZ_TRIGGERS_ibfk_1` FOREIGN KEY (`sched_name`, `job_name`, `job_group`) REFERENCES `QRTZ_JOB_DETAILS` (`sched_name`, `job_name`, `job_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '触发器详细信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of QRTZ_TRIGGERS
-- ----------------------------

-- ----------------------------
-- Table structure for gb_ai_platforms
-- ----------------------------
DROP TABLE IF EXISTS `gb_ai_platforms`;
CREATE TABLE `gb_ai_platforms`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `site_id` bigint NULL DEFAULT 0 COMMENT '所属网站ID(0表示全局默认配置)',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '平台名称',
  `platform_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '平台类型：openai, azure, qwen, wenxin, other',
  `api_endpoint` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'API地址',
  `api_key` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'API Key（加密存储）',
  `api_secret` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'API Secret（加密存储）',
  `model_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '默认模型名称',
  `available_models` json NULL COMMENT '可用模型列表',
  `category_id` bigint NULL DEFAULT NULL COMMENT '分类ID',
  `config` json NULL COMMENT '平台配置',
  `max_tokens` int NULL DEFAULT 4096 COMMENT '最大Token数',
  `temperature` decimal(3, 2) NULL DEFAULT 0.70 COMMENT '创作温度',
  `rate_limit` int NULL DEFAULT 60 COMMENT '速率限制',
  `cost_per_1k_tokens` decimal(10, 6) NULL DEFAULT 0.000000 COMMENT '每1000 Token成本',
  `is_default` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '是否默认：0-否 1-是',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '状态：0-禁用 1-启用',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '删除标志',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_is_default`(`is_default` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_site_id`(`site_id` ASC) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 35 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'AI平台配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_ai_platforms
-- ----------------------------
INSERT INTO `gb_ai_platforms` VALUES (1, 9, 'groq', 'openai', 'https://gateway.ai.cloudflare.com/v1/0a223df7a80ba3b519ed601835c0edbd/demo/groq/openai/v1', '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', NULL, 'openai/gpt-oss-120b', '[\"allam-2-7b\", \"canopylabs/orpheus-arabic-saudi\", \"canopylabs/orpheus-v1-english\", \"groq/compound\", \"groq/compound-mini\", \"llama-3.1-8b-instant\", \"llama-3.3-70b-versatile\", \"meta-llama/llama-4-maverick-17b-128e-instruct\", \"meta-llama/llama-4-scout-17b-16e-instruct\", \"meta-llama/llama-guard-4-12b\", \"meta-llama/llama-prompt-guard-2-22m\", \"meta-llama/llama-prompt-guard-2-86m\", \"moonshotai/kimi-k2-instruct\", \"moonshotai/kimi-k2-instruct-0905\", \"openai/gpt-oss-120b\", \"openai/gpt-oss-20b\", \"openai/gpt-oss-safeguard-20b\", \"qwen/qwen3-32b\", \"whisper-large-v3\", \"whisper-large-v3-turbo\"]', 114, NULL, 2000, 0.70, 60, 0.000000, '1', '1', '0', 'admin', '2025-12-21 22:35:36', 'admin', '2026-03-17 20:24:20', '');
INSERT INTO `gb_ai_platforms` VALUES (13, 9, 'Gemini', 'openai', 'https://m3u8-player.5yxy5.com/api/forward/https://gemini.5yxy5.com/openai/v1', 'gemini-balance', NULL, 'models/gemini-2.5-flash', '[\"models/aqa\", \"models/deep-research-pro-preview-12-2025\", \"models/embedding-001\", \"models/embedding-gecko-001\", \"models/gemini-2.0-flash\", \"models/gemini-2.0-flash-001\", \"models/gemini-2.0-flash-exp\", \"models/gemini-2.0-flash-exp-image-generation\", \"models/gemini-2.0-flash-lite\", \"models/gemini-2.0-flash-lite-001\", \"models/gemini-2.0-flash-lite-preview\", \"models/gemini-2.0-flash-lite-preview-02-05\", \"models/gemini-2.5-computer-use-preview-10-2025\", \"models/gemini-2.5-flash\", \"models/gemini-2.5-flash-image\", \"models/gemini-2.5-flash-lite\", \"models/gemini-2.5-flash-lite-preview-09-2025\", \"models/gemini-2.5-flash-native-audio-latest\", \"models/gemini-2.5-flash-native-audio-preview-09-2025\", \"models/gemini-2.5-flash-native-audio-preview-12-2025\", \"models/gemini-2.5-flash-preview-09-2025\", \"models/gemini-2.5-flash-preview-tts\", \"models/gemini-2.5-pro\", \"models/gemini-2.5-pro-preview-tts\", \"models/gemini-3-flash-preview\", \"models/gemini-3-pro-image-preview\", \"models/gemini-3-pro-preview\", \"models/gemini-embedding-001\", \"models/gemini-embedding-exp\", \"models/gemini-embedding-exp-03-07\", \"models/gemini-exp-1206\", \"models/gemini-flash-latest\", \"models/gemini-flash-lite-latest\", \"models/gemini-pro-latest\", \"models/gemini-robotics-er-1.5-preview\", \"models/gemma-3-12b-it\", \"models/gemma-3-1b-it\", \"models/gemma-3-27b-it\", \"models/gemma-3-4b-it\", \"models/gemma-3n-e2b-it\", \"models/gemma-3n-e4b-it\", \"models/imagen-4.0-fast-generate-001\", \"models/imagen-4.0-generate-001\", \"models/imagen-4.0-generate-preview-06-06\", \"models/imagen-4.0-ultra-generate-001\", \"models/imagen-4.0-ultra-generate-preview-06-06\", \"models/lyria-realtime-exp\", \"models/nano-banana-pro-preview\", \"models/text-embedding-004\", \"models/veo-2.0-generate-001\", \"models/veo-3.0-fast-generate-001\", \"models/veo-3.0-generate-001\", \"models/veo-3.1-fast-generate-preview\", \"models/veo-3.1-generate-preview\"]', 114, NULL, 2000, 0.70, 60, 0.000000, '0', '1', '0', 'admin', '2025-12-25 10:34:29', 'admin', '2026-03-17 20:24:20', NULL);

-- ----------------------------
-- Table structure for gb_articles
-- ----------------------------
DROP TABLE IF EXISTS `gb_articles`;
CREATE TABLE `gb_articles`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `slug` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文章路径标识',
  `locale` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'zh-CN' COMMENT '文章语言',
  `master_article_id` bigint NULL DEFAULT NULL COMMENT '主文章ID（关联gb_master_articles）',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文章标题',
  `subtitle` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '副标题',
  `author` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '作者',
  `keywords` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '关键词',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '文章摘要',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '文章内容',
  `content_cleared_at` datetime NULL DEFAULT NULL COMMENT 'content字段清理时间（清理后此字段有值）',
  `content_auto_clear_days` int NULL DEFAULT 30 COMMENT 'content自动清理天数（发布后多少天自动清理，默认30天）',
  `content_backup_key` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'content备份存储路径（用于恢复）',
  `content_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'markdown' COMMENT '内容类型',
  `cover_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '封面图URL',
  `storage_config_id` bigint NULL DEFAULT NULL COMMENT '文章内容存储配置ID',
  `storage_key` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '存储键/路径',
  `path_rule` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'date-title' COMMENT '路径规则：date-title-日期+标题 title-only-仅标题 category-title-分类+标题 custom-自定义',
  `storage_url` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '完整访问URL',
  `remote_file_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '远程文件状态：0-未检查 1-正常 2-不存在 3-异常',
  `last_sync_check_time` datetime NULL DEFAULT NULL COMMENT '最后同步检查时间',
  `remote_file_size` bigint NULL DEFAULT NULL COMMENT '远程文件大小（字节）',
  `remote_file_hash` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '远程文件哈希值（用于检测变更）',
  `resource_storage_config_id` bigint NULL DEFAULT NULL COMMENT '资源存储配置ID',
  `resource_base_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '资源基础路径',
  `locale_storage_config_id` bigint NULL DEFAULT NULL COMMENT '该语言版本专用的存储配置ID',
  `locale_resource_storage_config_id` bigint NULL DEFAULT NULL COMMENT '该语言版本专用的资源存储配置ID',
  `is_ai_translated` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '是否AI翻译：0-否 1-是',
  `word_count` int NULL DEFAULT 0 COMMENT '字数统计',
  `reading_time` int NULL DEFAULT 0 COMMENT '预估阅读时间（分钟）',
  `resource_count` int NULL DEFAULT 0 COMMENT '资源数量',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览次数',
  `like_count` int NULL DEFAULT 0 COMMENT '点赞次数',
  `comment_count` int NULL DEFAULT 0 COMMENT '评论次数',
  `share_count` int NULL DEFAULT 0 COMMENT '分享次数',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '发布状态：0-草稿 1-已发布 2-已下架',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '删除标志',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `publish_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  `deleted_at` datetime NULL DEFAULT NULL COMMENT '删除时间，用于区分同一记录的多次删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_locale`(`locale` ASC) USING BTREE,
  INDEX `idx_master_article`(`master_article_id` ASC) USING BTREE,
  INDEX `idx_storage_config`(`storage_config_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_remote_file_status`(`remote_file_status` ASC) USING BTREE,
  INDEX `idx_content_cleared`(`content_cleared_at` ASC) USING BTREE,
  INDEX `idx_auto_clear_days`(`content_auto_clear_days` ASC) USING BTREE,
  CONSTRAINT `fk_articles_master_article` FOREIGN KEY (`master_article_id`) REFERENCES `gb_master_articles` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 107 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文章表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_articles
-- ----------------------------
INSERT INTO `gb_articles` VALUES (1, 'luban7hao', 'zh-CN', 1, '王者荣耀鲁班七号怎么玩？', NULL, 'Misin', 'we', 'dsf', '\n# [斗罗大陆破解版资源无限畅享！无限钻石内置修改器，无限元宝无限资源内置菜单全开，内部号修改版无限仙玉免氪体验！]({{siteConfig.jumpDomain}})\n\n## 游戏简介\n<div style=\"width: 100%; height: 30vh;\">\n<iframe \n    src=\"https://static-cdn.app.wakaifu.com/admin-fast-system/gamedata/20240415/gamevideo/2024041515525623.mp4\" \n    width=\"100%\" \n    height=\"100%\" \n    frameborder=\"0\" \n    allowfullscreen>\n</iframe>\n</div>\n', NULL, 30, NULL, 'markdown', 'https://img.freepik.com/free-photo/female-tourists-who-are-taking-photos-atmosphere_1150-7439.jpg', 1, '2026-03-luban7hao/README.md', 'yearmonth-title', 'https://edgeone.gh-proxy.org/https://github.com/MisinGuo/temp/blob/main/2026-03-luban7hao/README.md', '1', '2026-03-15 18:02:28', -1, NULL, NULL, NULL, NULL, NULL, '0', 0, 0, 0, 0, 0, 0, 0, '1', '0', 'admin', '2025-12-23 09:51:21', 'admin', '2026-03-15 18:02:27', '2026-03-15 18:02:28', NULL);
INSERT INTO `gb_articles` VALUES (21, 'wangzherongyao', 'zh-CN', 23, '王者荣耀哪个英雄好玩？', NULL, NULL, NULL, NULL, '# 王者荣耀好玩英雄推荐与上手攻略（2026中文版）\n\n> 好不好玩，往往比强不强更重要。  \n> 本篇从【操作爽感】【意识博弈】【娱乐快乐】三个维度，推荐当前公认“玩起来最爽”的英雄，并给出上手要点。\n\n---\n\n## 一、操作党首选：秀起来才是王道\n\n这些英雄的共同特点是：  \n**操作上限高、手感好、秀成功一次就会上瘾**\n\n### 1️⃣ 马超 —— 拉扯的艺术\n\n- 英雄定位：对抗路 / 打野  \n- 核心体验：高速位移 + 枪感节奏\n\n**为什么好玩**\n- 捡枪 → 位移 → 再捡枪，节奏像在跳舞  \n- 熟练后可以 1v2、1v3 拉扯反杀\n\n**上手要点**\n- ❗ 永远关注枪的位置  \n- ❗ 别站桩输出，马超是“移动输出”\n\n**适合人群**\n- 喜欢高操作\n- 喜欢极限反杀的玩家\n\n---\n\n### 2️⃣ 李白 —— 情怀与帅气并存\n\n- 英雄定位：打野  \n- 核心体验：刷大 → 进场 → 消失\n\n**为什么好玩**\n- 大招无敌帧，进出战场如入无人之境  \n- 操作流畅，成就感极高\n\n**上手要点**\n- ❗ 熟练掌握一技能位移回点  \n- ❗ 不要无脑进场，等关键控制交完\n\n---\n\n### 3️⃣ 上官婉儿 —— 飞起来就是胜利\n\n- 英雄定位：中路法师  \n- 核心体验：连招飞天 + 无法选中\n\n**为什么好玩**\n- 成功飞天的一刻极度解压  \n- 秒 C 位能力极强\n\n**上手要点**\n- ❗ 先练“直线飞”再练“Z 字飞”  \n- ❗ 别贪，飞不起来就撤\n\n---\n\n## 二、意识流玩家：不用秀，也能赢\n\n如果你更喜欢**判断、运营、节奏掌控**，这些英雄会非常好玩。\n\n### 4️⃣ 鬼谷子 —— 一拉五的快乐\n\n- 英雄定位：辅助  \n- 核心体验：开团之王\n\n**为什么好玩**\n- 一个好大招能直接决定团战  \n- 队友会因为你而变强\n\n**上手要点**\n- ❗ 多看小地图，找“落单的人”  \n- ❗ 不要先手空大\n\n---\n\n### 5️⃣ 老夫子 —— 对线与单挑的极致\n\n- 英雄定位：对抗路  \n- 核心体验：压制、捆人、拆塔\n\n**为什么好玩**\n- 单挑能力极强  \n- 大招捆谁谁难受\n\n**上手要点**\n- ❗ 学会控线  \n- ❗ 捆到核心位就是胜利\n\n---\n\n## 三、娱乐至上：快乐比输赢重要\n\n适合下班、放松、五排整活。\n\n### 6️⃣ 程咬金 —— 快乐源泉\n\n- 英雄定位：对抗路  \n- 核心体验：血条蹦迪\n\n**为什么好玩**\n- 越打越兴奋  \n- 专治对面心态\n\n---\n\n### 7️⃣ 鲁班七号 —— 痛并快乐着\n\n- 英雄定位：射手  \n- 核心体验：高伤害 + 高风险\n\n**为什么好玩**\n- 输出爆炸  \n- 团战存在感极强\n\n---\n\n## 四、怎么选最适合你的“好玩英雄”？\n\n| 你更在意 | 推荐方向 |\n|--------|----------|\n| 操作与手感 | 马超 / 李白 / 上官婉儿 |\n| 节奏与大局 | 鬼谷子 / 老夫子 |\n| 轻松快乐 | 程咬金 / 鲁班七号 |\n\n---\n\n## 结语\n\n**好玩 ≠ 强度第一**\n\n真正适合你的英雄，应该是：  \n- 👉 输了不生气  \n- 👉 赢了有成就感  \n- 👉 愿意长期练下去  \n\n如果你愿意，我可以继续帮你：\n- 制作【单英雄深度攻略】\n- 改成【公众号 / 语雀排版版】\n- 或按你的段位与位置定制推荐\n\n直接告诉我你的需求即可 🎮🔥\n', NULL, 30, NULL, 'markdown', 'https://img.freepik.com/free-photo/female-tourists-who-are-taking-photos-atmosphere_1150-7439.jpg', 1, '2026-03-wangzherongyao/README.md', 'yearmonth-title', 'https://edgeone.gh-proxy.org/https://github.com/MisinGuo/temp/blob/main/2026-03-wangzherongyao/README.md', '1', '2026-03-15 18:02:11', -1, NULL, NULL, NULL, NULL, NULL, '0', 0, 0, 0, 0, 0, 0, 0, '1', '0', 'admin', '2026-01-16 16:11:10', 'admin', '2026-03-15 18:02:10', '2026-03-15 18:02:11', NULL);
INSERT INTO `gb_articles` VALUES (46, 'wangzherongyao-zh-tw', 'zh-TW', 23, '王者榮耀哪個英雄好玩？', NULL, '', '', '', '# 王者榮耀好玩英雄推薦與上手攻略（2026中文版）\n\n> 好不好玩，往往比強不強更重要。  \n> 本篇從【操作爽感】【意識博弈】【娛樂快樂】三個維度，推薦當前公認“玩起來最爽”的英雄，並給出上手要點。\n\n---\n\n## 一、操作黨首選：秀起來才是王道\n\n這些英雄的共同特點是：  \n**操作上限高、手感好、秀成功一次就會上癮**\n\n### 1️⃣ 馬超 —— 拉扯的藝術\n\n- 英雄定位：對抗路 / 打野  \n- 核心體驗：高速位移 + 槍感節奏\n\n**為什麼好玩**\n- 撿槍 → 位移 → 再撿槍，節奏像在跳舞  \n- 熟練後可以 1v2、1v3 拉扯反殺\n\n**上手要點**\n- ❗ 永遠關注槍的位置  \n- ❗ 別站樁輸出，馬超是“移動輸出”\n\n**適合人群**\n- 喜歡高操作\n- 喜歡極限反殺的玩家\n\n---\n\n### 2️⃣ 李白 —— 情懷與帥氣並存\n\n- 英雄定位：打野  \n- 核心體驗：刷大 → 進場 → 消失\n\n**為什麼好玩**\n- 大招無敵幀，進出戰場如入無人之境  \n- 操作流暢，成就感極高\n\n**上手要點**\n- ❗ 熟練掌握一技能位移回點  \n- ❗ 不要無腦進場，等關鍵控制交完\n\n---\n\n### 3️⃣ 上官婉兒 —— 飛起來就是勝利\n\n- 英雄定位：中路法師  \n- 核心體驗：連招飛天 + 無法選中\n\n**為什麼好玩**\n- 成功飛天的一刻極度解壓  \n- 秒 C 位能力極強\n\n**上手要點**\n- ❗ 先練“直線飛”再練“Z 字飛”  \n- ❗ 別貪，飛不起來就撤\n\n---\n\n## 二、意識流玩家：不用秀，也能贏\n\n如果你更喜歡**判斷、運營、節奏掌控**，這些英雄會非常好玩。\n\n### 4️⃣ 鬼穀子 —— 一拉五的快樂\n\n- 英雄定位：輔助  \n- 核心體驗：開團之王\n\n**為什麼好玩**\n- 一個好大招能直接決定團戰  \n- 隊友會因為你而變強\n\n**上手要點**\n- ❗ 多看小地圖，找“落單的人”  \n- ❗ 不要先手空大\n\n---\n\n### 5️⃣ 老夫子 —— 對線與單挑的極致\n\n- 英雄定位：對抗路  \n- 核心體驗：壓制、捆人、拆塔\n\n**為什麼好玩**\n- 單挑能力極強  \n- 大招捆誰誰難受\n\n**上手要點**\n- ❗ 學會控線  \n- ❗ 捆到核心位就是勝利\n\n---\n\n## 三、娛樂至上：快樂比輸贏重要\n\n適合下班、放鬆、五排整活。\n\n### 6️⃣ 程咬金 —— 快樂源泉\n\n- 英雄定位：對抗路  \n- 核心體驗：血條蹦迪\n\n**為什麼好玩**\n- 越打越興奮  \n- 專治對面心態\n\n---\n\n### 7️⃣ 魯班七號 —— 痛并快樂著\n\n- 英雄定位：射手  \n- 核心體驗：高傷害 + 高風險\n\n**為什麼好玩**\n- 輸出爆炸  \n- 團戰存在感極強\n\n---\n\n## 四、怎麼選最適合你的“好玩英雄”？\n\n| 你更在意 | 推薦方向 |\n|--------|----------|\n| 操作與手感 | 馬超 / 李白 / 上官婉兒 |\n| 節奏與大局 | 鬼穀子 / 老夫子 |\n| 輕鬆快樂 | 程咬金 / 魯班七號 |\n\n---\n\n## 結語\n\n**好玩 ≠ 強度第一**\n\n真正適合你的英雄，應該是：  \n- 👉 輸了不生氣  \n- 👉 贏了有成就感  \n- 👉 願意長期練下去  \n\n如果你願意，我可以繼續幫你：\n- 製作【單英雄深度攻略】\n- 改成【公眾號 / 語雀排版版】\n- 或按你的段位與位置定制推薦\n\n直接告訴我你的需求即可 🎮🔥', NULL, 30, NULL, 'markdown', '', 1, '2026-01-wangzherongyao-zh-tw/README.md', 'yearmonth-title', 'https://edgeone.gh-proxy.org/https://github.com/MisinGuo/temp/blob/main/2026-01-wangzherongyao-zh-tw/README.md', '1', '2026-03-14 17:30:52', -1, NULL, NULL, NULL, NULL, NULL, '0', 0, 0, 0, 0, 0, 0, 0, '1', '0', '', '2026-01-16 21:51:09', 'admin', '2026-03-14 17:30:52', '2026-01-31 21:55:22', NULL);
INSERT INTO `gb_articles` VALUES (50, 'wangzherongyao-en-us', 'en-US', 23, 'Which hero is the most fun in Honor of Kings?', NULL, '', '', '', '# Glory of Kings Fun Hero Recommendations and Getting Started Guide (2026 Chinese Version)\n\n>Whether it is fun or not is often more important than whether it is strong or not.  \n> This article recommends the heroes currently recognized as “the most enjoyable to play” from the three dimensions of [Operation Comfort] [Conscious Game] [Entertainment and Happiness], and gives the key points for getting started.\n\n---\n\n## 1. The first choice of the operating party: Showing off is the way to go\n\nThe common characteristics of these heroes are:\n**High operating limit, good feel, and you will be addicted once you succeed**\n\n### 1️⃣ Ma Chao - The Art of Pulling\n\n- Hero positioning: versus lane/jungle\n- Core experience: high-speed displacement + gun-sense rhythm\n\n**Why is it fun**\n- Pick up the gun → Displacement → Pick up the gun again, the rhythm is like dancing\n- After becoming proficient, you can pull and counterattack in 1v2 and 1v3\n\n**Key points to get started**\n- ❗ Always pay attention to the position of the gun\n- ❗ Don’t rely on standing output, Ma Chao is a “mobile output”\n\n**Suitable for the crowd**\n- Like high operation\n- Players who like extreme counter-killing\n\n---\n\n### 2️⃣ Li Bai——Sentiment and handsomeness coexist\n\n- Hero positioning: jungler\n- Core experience: Boost → Enter → Disappear\n\n**Why is it fun**\n- The invincible frame of the ultimate move makes entering and exiting the battlefield feel like entering an uninhabited land.\n- Smooth operation and high sense of achievement\n\n**Key points to get started**\n- ❗ Master one skill to move back to the point\n- ❗ Don’t enter the market without thinking, wait for the key controls to be handed over\n\n---\n\n### 3️⃣ Shangguan Wan\'er - Flying is victory\n\n- Hero positioning: mid-lane mage\n- Core experience: combo flying + cannot be selected\n\n**Why is it fun**\n- The moment of success in flying into the sky is an extreme relief\n-Second C position is extremely capable\n\n**Key points to get started**\n- ❗ Practice \"straight-line flight\" first and then \"Z-shaped flight\"\n- ❗ Don’t be greedy, just retreat if you can’t fly.\n\n---\n\n## 2. Stream of consciousness players: you can win without showing off\n\nIf you prefer **judgment, operation, and rhythm control**, these heroes will be very fun.\n\n### 4️⃣ Guiguzi - the joy of pulling and pulling five\n\n- Hero positioning: auxiliary\n- Core experience: King of group start\n\n**Why is it fun**\n- A good ultimate move can directly determine the team battle\n-Teammates will become stronger because of you\n\n**Key points to get started**\n- ❗ Look at the mini map to find the \"lonely person\"\n- ❗ Don’t start with an empty hand\n\n---\n\n### 5️⃣Old Master——The ultimate in laning and dueling\n\n- Hero positioning: confrontation road\n- Core experience: suppression, binding people, demolishing towers\n\n**Why is it fun**\n- Extremely strong in one-on-one combat\n- Whoever is tied up by the big move will feel uncomfortable\n\n**Key points to get started**\n- ❗ Learn to control the line\n- ❗ If you tie it to the core, you will win.\n\n---\n\n## 3. Entertainment first: happiness is more important than winning or losing\n\nSuitable for getting off work, relaxing, and reorganizing your work.\n\n### 6️⃣ Cheng Yaojin——The source of happiness\n\n- Hero positioning: confrontation road\n- Core experience: health bar disco\n\n**Why is it fun**\n- The more you fight, the more excited you become\n-Specializes in treating the opponent’s mentality\n\n---\n\n### 7️⃣ Luban No. 7——Pain and happiness\n\n- Hero positioning: shooter\n- Core experience: high damage + high risk\n\n**Why is it fun**\n- Output explosion\n- Strong sense of presence in team battles\n\n---\n\n## 4. How to choose the “fun hero” that suits you best?\n\n| You care more | Recommended directions |\n|--------|----------|\n| Operation and feel | Ma Chao / Li Bai / Shangguan Wan\'er |\n| Rhythm and overall situation | Guiguzi/Old Master |\n| Relaxed and happy | Cheng Yaojin / Luban No. 7 |\n\n---\n\n## Conclusion\n\n**Fun ≠ Strongest**\n\nThe hero that really suits you should be:\n- 👉 Don’t be angry if you lose\n- 👉 A sense of accomplishment when you win\n- 👉 Willing to practice for a long time\n\nIf you want, I can continue to help you:\n- Make [Single Hero In-Depth Guide]\n- Changed to [Official account / Yuque typesetting version]\n- Or customize recommendations based on your rank and location\n\nJust tell me your needs directly 🎮🔥', NULL, 30, NULL, 'markdown', '', 1, '2026-01-wangzherongyao-en-us/README.md', 'yearmonth-title', 'https://edgeone.gh-proxy.org/https://github.com/MisinGuo/temp/blob/main/2026-01-wangzherongyao-en-us/README.md', '1', '2026-03-14 17:30:57', -1, NULL, NULL, NULL, NULL, NULL, '0', 0, 0, 0, 0, 0, 0, 0, '1', '0', '', '2026-01-16 21:53:47', 'admin', '2026-03-14 17:30:57', '2026-01-31 21:55:29', NULL);
INSERT INTO `gb_articles` VALUES (51, 'luban7hao-zh-tw', 'zh-TW', 1, '王者榮耀魯班七號怎麼玩？', NULL, 'Misin', '我們', 'dsf', '# [斗羅大陸破解版資源無限暢享！無限鑽石內置修改器，無限元寶無限資源內置菜單全開，內部號修改版無限仙玉免氪體驗！ ]({{siteConfig.jumpDomain}})\n\n## 遊戲簡介\n<div style=\"width: 100%; height: 30vh;\">\n<iframe \n    src=\"https://static-cdn.app.wakaifu.com/admin-fast-system/gamedata/20240415/gamevideo/2024041515525623.mp4\" \n    width=\"100%\" \n    height=\"100%\" \n    frameborder=\"0\" \n    allowfullscreen>\n</iframe>\n</div>', NULL, 30, NULL, 'markdown', '', 1, '2026-01-luban7hao-zh-tw/README.md', 'yearmonth-title', 'https://edgeone.gh-proxy.org/https://github.com/MisinGuo/temp/blob/main/2026-01-luban7hao-zh-tw/README.md', '1', '2026-01-31 22:47:57', -1, NULL, NULL, NULL, NULL, NULL, '0', 0, 0, 0, 0, 0, 0, 0, '1', '0', '', '2026-01-17 11:10:30', 'admin', '2026-01-31 22:47:57', '2026-01-31 21:44:39', NULL);
INSERT INTO `gb_articles` VALUES (96, 'ftgergare', 'zh-CN', 67, 'CS2终极进阶指南：从新手到职业选手', NULL, '', '', '', '## CS2 终极进阶指南：从新手到职业选手  \n\n> **本文目标**：帮助刚接触《反恐精英 2》（CS2）的玩家，系统梳理从基础入门到职业水平的提升路径。全文约 800 字，兼顾理论讲解与实战技巧，适合想要快速进阶的玩家阅读。\n\n---  \n\n### 1. 认识游戏核心机制  \n\n| 关键要素 | 说明 | 提升技巧 |\n|----------|------|----------|\n| **枪械后坐力** | 每把武器都有独特的垂直与水平抖动曲线。 | 练习“短点射+抬枪”或“点射+微调”，在练习场中记录每把枪的最佳射击节奏。 |\n| **经济系统** | 轮到你买枪的回合会受到全队经济影响。 | 了解“全买/全保/半买”常用策略，学会在资金不足时合理选择**P250**、**Uzi**等低价武器。 |\n| **地图控制** | 关键位置（A 点、B 点、长廊、天桥）决定信息优势。 | 通过**站位图**标记常见对峙点，熟悉每条线路的**掩体、翻墙点**。 |\n| **信息获取** | 语音、文字、雷达、脚步声都是情报来源。 | 练习**噪声辨别**（如 7.62mm 与 9mm 的差别），使用 **声音定位** 进行预判。 |\n\n> **小贴士**：新手期建议先在**训练模式**完成“枪械后坐力”与“投掷物”两项任务，每项 10 分钟，形成肌肉记忆后再进入实战。\n\n---  \n\n### 2. 基础练习：打造稳固的技术根基  \n\n1. **瞄准训练**  \n   - 使用官方练习地图 `aim_map`（或社区热门 `training_aim_csgo2`），设定 **“10 秒 30 发”** 的目标，记录命中率。  \n   - 采用 **“双手握枪”**（左手持枪，右手负责鼠标）进行 **“鼠标抬升”** 练习，提升微调能力。  \n\n2. **投掷物练习**  \n   - **闪光弹**：掌握 **“抛物线 + 预判”**（如 `flash_throw`），在 1.5 秒内覆盖敌方视野。  \n   - **烟雾弹**：记忆 **“烟雾点位”**（如 `CT Smoke`、`T Smoke`），在 2 秒内完成投掷，确保不被敌人拦截。  \n   - **燃烧弹**：练习 **“低位投掷”**（防止被对手抢夺），在狭窄通道中形成有效阻断。  \n\n3. **移动与站位**  \n   - 采用 **“蹲-跑-蹲”**（Crouch‑Walk‑Crouch）移动方式，降低被子弹击中的几率。  \n   - 学会 **“角落探头”**（Peeking）——左键快速点射后立即回撤，防止被对手反击。  \n\n> **练习频率**：建议每日 30 分钟瞄准 + 15 分钟投掷 + 15 分钟移动，连续 2 周后命中率可提升至 70%+。\n\n---  \n\n### 3. 战术进阶：团队协作与局势控制  \n\n| 阶段 | 关键战术 | 适用局面 |\n|------|----------|----------|\n| **开局** | **抢占关键点**（如 `Long A`、`B Short`） | 经济充足、全队配合 |\n| **中期** | **假动作 + 轮换**（Fake A → Rotate B） | 对手防守严密、需要制造错觉 |\n| **后期** | **强制抢点 + 逼抢**（Force Plant） | 经济紧张、需要快速结束回合 |\n\n1. **信息共享**  \n   - 使用 **简短的语音指令**（如 “CT”， “B”， “Eco”）或 **快捷键**（`Ctrl+1` 标记 A 点），确保全队在同一信息层。  \n\n2. **角色分工**  \n   - **狙击手**（AWP）负责 **远距离压制** 与 **点位守卫**。  \n   - **突击手**（AK‑47 / M4A4）承担 **冲锋** 与 **突破**。  \n   - **支援手**（P250、Uzi）专注 **经济回合** 与 **投掷物补位**。  \n\n3. **轮换节奏**  \n   - 通过 **计时器**（如 15 秒）决定是否 **“强行轮换”**。若对手在 A 点防守严密，可在 10 秒时启动 **B 点轮换**，形成双点压力。  \n\n---  \n\n### 4. 心理与体能：职业选手的隐形优势  \n\n- **情绪管理**：每回合结束后记录 **“情绪分数”**（0‑5），低于 3 分时立即进行深呼吸或短暂休息，避免连败情绪蔓延。  \n- **视力保养**：每 45 分钟休息 5 分钟，进行 **20‑20‑20 法则**（看 20 英尺外 20 秒），降低视疲劳。  \n- **手部放松**：练习 **手指伸展** 与 **腕部旋转**，防止长时间鼠标操作导致的腕管综合征。  \n\n---  \n\n### 5. 进阶资源推荐  \n\n| 类型 | 名称 | 适用阶段 | 获取方式 |\n|------|------|----------|----------|\n| **教学视频** | **“CS2 Pro Tips – 2024”**（YouTube） | 基础 → 中期 | 免费 |\n| **实战地图** | **“Yprac Training Pack”**（Steam） | 中期 → 高手 | 免费 |\n| **战术分析** | **“ESEA 赛季分析报告”** | 高手 → 职业 | 付费订阅 |\n| **社区讨论** | **Reddit r/CS2Competitive** | 全阶段 | 免费 |\n\n---  \n\n### 6. 成为职业选手的路线图（约 6 个月）  \n\n| 月份 | 目标 | 关键行动 |\n|------|------|----------|\n| **1‑2 月** | 完成 **基础训练**（枪法、投掷、移动） | 每日 1 小时练习，完成 5 张训练地图的全通关。 |\n| **3‑4 月** | 进入 **实战排位**（Silver → Gold） | 每周 3 场 5v5 排位，记录每局关键数据（KDA、经济贡献）。 |\n| **5 月** | 加入 **俱乐部或战队**，参与 **Scrim**（内部练习赛） | 与固定队友进行 2‑4 小时的战术演练，形成固定站位。 |\n| **6 月** | 参加 **线上联赛**（如 **ESEA Open**），争取 **Top 8** | 赛前进行 2 天的战术复盘，赛后提交个人复盘报告。 |\n\n> **成功要点**：坚持 **数据驱动**（每局复盘）、**团队沟通**（固定指令库）以及 **身心平衡**（合理作息）。只要保持上述节奏，即可从新手迈向职业选手的行列。\n\n---  \n\n## 结语  \n\nCS2 的竞争环境日趋激烈，单纯的枪法已经不再是制胜关键。**技术、战术、心理三位一体**，才能在高强度对局中立于不败之地。希望本指南能帮助你在练习中找到方向，在实战中快速提升。记住：**每一次射击都是一次学习，每一次失误都是一次进步**。祝你在《反恐精英 2》的赛场上，早日登上职业舞台！', NULL, 30, NULL, 'markdown', '', 6, '2026-03-16-ftgergare/README.md', 'date-title', 'https://miniio.misindata.com/9000/2026-03-16-ftgergare/README.md', '1', '2026-03-16 16:40:40', NULL, NULL, NULL, NULL, NULL, NULL, '0', 0, 0, 0, 0, 0, 0, 0, '1', '0', '', '2026-03-16 00:00:09', 'admin', '2026-03-16 16:40:39', '2026-03-16 16:40:40', NULL);
INSERT INTO `gb_articles` VALUES (101, NULL, 'zh-CN', 72, 'CS2终极进阶指南：从新手到职业选手', NULL, '', '', '', '# CS2 终极进阶指南：从新手到职业选手  \n\n在《反恐精英：全球攻势 2》（CS2）正式上线后，游戏机制、画面与地图都有了显著提升。想要在这片竞争激烈的战场上脱颖而出，仅靠天赋是不够的——系统的训练、科学的思考和持续的实战经验才是通往职业舞台的关键。本文将围绕 **基础入门 → 技能提升 → 战术进阶 → 心理与体能** 四大阶段，为你提供一套完整、可执行的成长路线图。\n\n---\n\n## Ⅰ. 基础入门：打好“根基”\n\n| 关键要点 | 具体操作 | 练习建议 |\n|----------|----------|----------|\n| **熟悉游戏界面** | 了解主菜单、设置、武器库、购买界面的功能位置 | 进入训练模式，反复打开/关闭各项面板，形成肌肉记忆 |\n| **掌握基本操作** | 键位（默认 W A S D、Shift 蹲、Ctrl 走、空格跳、鼠标左键射击、右键瞄准） | 使用 **练习场**（Free Aim）进行 10 分钟的键位连贯性练习 |\n| **了解武器特性** | 每把枪的伤害、后坐力、弹道、射速差异 | 选定 2–3 把常用枪（如 AK‑47、M4A1‑S、AWP），分别在靶场射击 200 发，记录弹道偏移 |\n| **掌握基础经济** | 了解回合经济、买枪、买装备的时机 | 观看官方教学视频，记录每回合结束的金钱变化，尝试自行计算下一回合的最佳购买方案 |\n\n> **小贴士**：新手前 5 局建议只使用 **P250** 与 **UMP‑45**，降低经济压力的同时熟悉射击手感。\n\n---\n\n## Ⅱ. 技能提升：从“射准”到“射速”\n\n### 1. 瞄准与后坐力控制  \n- **短点射（Tap Shooting）**：适用于 P250、USP‑S、Glock。每发子弹单独点击，保持最高精度。  \n- **连点射（Burst Fire）**：M4A1‑S、FAMAS 采用 2–3 发连射，兼顾速度与控制。  \n- **全自动控制**：AK‑47、M4A4、UMP‑45 需要在前 3–4 发后立即松开鼠标，后续再继续射击，以抵消后坐力。  \n\n> **练习方式**：打开 **练习场 → 目标板**，设置 **“每秒 10 发”**，记录每 10 发的命中率，逐步提升至 80% 以上。\n\n### 2. 移动与射击的配合  \n- **Crouch‑Jump（蹲跳）**：在狭窄通道或高低差处使用，可提升准确度并规避对手的预判。  \n- **Counter‑Strafing（反向冲刺）**：在左/右键 A/D 移动后立即按相反方向键并开火，可瞬间恢复射击精度。  \n\n### 3. 视角与感官训练  \n- **视野（FOV）**：保持默认 90，避免因视野过大导致目标变小、瞄准困难。  \n- **音频定位**：使用 **立体声或 7.1 环绕声**，并在设置中打开 **“高质量音效”**，练习通过脚步声判断敌人距离与方向。\n\n---\n\n## Ⅲ. 战术进阶：团队协作与地图控制\n\n### 1. 关键地图热点（以 Dust2 为例）  \n| 区域 | 进攻要点 | 防守要点 |\n|------|----------|----------|\n| **Long A** | 使用 **AWP** 或 **Scout** 抢占远程视野；配合 **SMG** 进行快速突击 | 设立 **Flash** 与 **Smoke** 阻挡视线，利用 **Molotov** 控制入口 |\n| **Mid** | 通过 **Bait**（诱敌）+ **Entry**（突入）配合快速占点 | 放置 **CT**（防守）时保持 **两人一线**，防止被 **双枪** 突破 |\n| **B Site** | 先手 **Smoke** 盖住 **Car**，随后 **Push**（冲锋） | 在 **Car** 后方布置 **M4**，利用 **角落** 进行 **交叉火力** |\n\n### 2. 经济轮与买枪策略  \n- **Full‑Buy（全装）**：金钱 ≥ 8000 时，团队统一购买主力武器与防具。  \n- **Eco（经济回合）**：金钱 < 4000 时，采用 **Pistol** 或 **SMG**，保持火力同时积累经济。  \n- **Force‑Buy（强买）**：若对手连续两回合 **Eco**，可提前全装压制对手。  \n\n### 3. 角色定位与职责分配  \n| 角色 | 主要职责 | 推荐武器 |\n|------|----------|----------|\n| **入口（Entry Fragger）** | 首波冲锋、打开视野 | AK‑47 / M4A4 |\n| **狙击手（AWPer）** | 长距离压制、守点 | AWP |\n| **支援（Support）** | 投掷 **Flash/Smoke/Molotov**、提供弹药 | SMG / Pistol |\n| **后卫（Lurker）** | 隐蔽侧翼、偷袭 | Deagle / USP‑S |\n| **指挥（In‑Game Leader）** | 战术规划、信息传递 | 任意（视局势而定） |\n\n> **实战技巧**：在每回合结束后进行 **5‑10 分钟的回顾（Demo Review）**，记录关键失误、成功点位，并在下一局中针对性调整。\n\n---\n\n## Ⅳ. 心理与体能：职业选手的软实力\n\n1. **保持冷静**  \n   - 每回合结束后深呼吸 3 次，避免情绪波动影响后续判断。  \n   - 设定 **“每局最多 2 次失误”** 的自我约束，帮助集中注意力。\n\n2. **体能管理**  \n   - 每 90 分钟游戏后进行 **5 分钟的伸展**（手腕、肩膀、颈部），防止肌肉僵硬。  \n   - 保持 **每 2 小时 1 次 10 分钟的休息**，补充水分与碳水，维持血糖稳定。\n\n3. **数据驱动的提升**  \n   - 使用 **CS:GO Stats**、**Overwolf** 等插件记录 **K/D、ADR、HS%** 等关键指标。  \n   - 每周抽出 30 分钟，对比本周数据与上周趋势，设定 **“提升 5%”** 的具体目标。\n\n---\n\n## 结语\n\n从 **新手** 到 **职业选手**，每一步都离不开 **系统训练**、**科学思考** 与 **团队协作**。掌握基础操作后，重点突破 **瞄准控制**、**地图战术** 与 **经济管理**；再配合 **心理调适** 与 **体能维护**，才能在高强度的竞技环境中保持持续的高水平发挥。  \n\n> **行动建议**：立即打开 CS2，按照本文四个阶段制定 **30 天训练计划**，坚持执行，你将看到从 “手残” 到 “赛场主宰” 的显著转变。祝你早日站上职业赛的舞台！', NULL, 30, NULL, 'markdown', '', NULL, NULL, 'date-title', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 0, 0, 0, 0, 0, 0, 0, '0', '0', '', '2026-03-22 00:00:07', '', '2026-03-22 00:00:06', NULL, NULL);
INSERT INTO `gb_articles` VALUES (102, NULL, 'zh-CN', 73, 'CS2终极进阶指南：从新手到职业选手', NULL, '', '', '', '# CS2 终极进阶指南：从新手到职业选手  \n\n> **CS2（Counter‑Strike 2）** 继承了《CS:GO》百年的竞技基因，在画面、帧率与服务器同步上实现了质的飞跃。想要从菜鸟走向职业赛场，光有天赋是不够的，系统化的训练、科学的思考以及对细节的极致追求才是关键。本文围绕 **技术、战术、心态、装备** 四大维度，提供一套完整的进阶路径，帮助你在 800 字左右快速搭建成长框架。\n\n---\n\n## 1. 基础技术：夯实每一次射击\n\n| 项目 | 关键要点 | 练习方式 |\n|------|----------|----------|\n| **准星控制** | - 采用默认 1:1 准星，保持低位<br>- 通过微调 DPI 与灵敏度，使 90° 转向约 20 cm 鼠标位移 | 练习 “Aim Lab”/CS2 练习模式，固定目标 30 秒内 200 次点击 |\n| **喷射控制** | - 熟记每把武器的前 10 发弹道（图谱可在 1v1 练习中记录）<br>- 通过左手拖动鼠标逆向补偿 | 进入训练服务器，开启 “No Recoil” 选项，手动拉回准星，形成肌肉记忆 |\n| **移动射击** | - 站立射击最精准，移动射击仅适用于冲刺或抢占位置<br>- 采用 “counter‑strafe”（A→S，D→W）瞬停后开火 | 在 “Deathmatch” 中刻意练习 A、D 交替后瞬停射击，记录击杀率提升幅度 |\n| **手雷投掷** | - 记住四个核心投掷点：抛物线、弹道、落点、延迟<br>- 常用手雷：闪光、烟雾、燃烧弹、HE | 使用地图编辑器或 “Workshop” 中的 “练习手雷” 模式，反复演练 5 套常用点位 |\n\n> **小技巧**：保持 800‑900 DPI、灵敏度 0.5（默认）是大多数职业选手的黄金配置，适合快速微调与精准射击。\n\n---\n\n## 2. 战术认知：地图、经济、角色\n\n### 2.1 地图知识\n\n1. **关键视野点**（如 Dust2 的 “Long A”、Mirage 的 “Window”）  \n2. **常用卡点**：每张地图至少记住 5 条进攻/防守路线，熟悉 “快闪点” 与 “防守侧翼”。  \n3. **时机判断**：利用声音（脚步、装弹）与雷达信息，推算对手位置并提前布置。\n\n> **练习建议**：每周挑选一张地图，使用 “Warmup” 模式跑 10 遍完整回合，记录每次死亡位置，形成热图并针对性改进。\n\n### 2.2 经济系统\n\n- **买枪时机**：  \n  - **第 1 回合**：全员买 **P250** + **防弹背心**（若有）  \n  - **第 2 回合**：若首回合赢得回合金，升级为 **M4A1‑S / AK‑47**  \n- **经济轮**：了解 “强制买枪（Force‑Buy）” 与 “保守（Eco）” 的切换原则，确保团队始终保持 **30 %** 以上的枪支占比。  \n- **团队共享**：在抢夺 **CT/TE** 侧的 “金钱池” 时，优先让核心玩家（AWPer、Entry Fragger）保持武装。\n\n### 2.3 角色定位\n\n| 角色 | 主要职责 | 推荐武器 | 关键技能 |\n|------|----------|----------|----------|\n| **Entry Fragger** | 首冲打开视野 | AK‑47 / M4A4 | 快速冲刺、精准点射 |\n| **AWPer** | 长距离压制 | AWP | 站位预判、单挑收割 |\n| **Support** | 烟雾、闪光支援 | SG 553 / AUG | 手雷投掷、信息传递 |\n| **Lurker** | 侧翼埋伏 | USP‑S / P2000 | 侦查、单点突破 |\n| **In‑game Leader (IGL)** | 战术指挥 | 任意 | 经济判断、地图控制 |\n\n> **成长路径**：新手先专注 **Entry** 与 **Support**，熟练后再尝试 **AWP** 与 **Lurker**，因为后两者对地图与时机要求更高。\n\n---\n\n## 3. 心理与体能：职业选手的隐形武器\n\n1. **情绪管理**  \n   - 采用 “3‑秒呼吸法”：每次死亡后深呼吸 3 次，避免情绪连锁。  \n   - 设定 **每局目标**（如 2 次成功投掷）而非单纯 “赢/输”，降低压力。\n\n2. **专注训练**  \n   - 每天 **90 分钟** 高强度对局，随后 **15 分钟** 复盘。  \n   - 使用 “Pomodoro” 法，每 25 分钟休息 5 分钟，保持视力与手部灵活度。\n\n3. **体能保障**  \n   - **手部伸展**：每 30 分钟做 5 组手指伸屈，防止腱鞘炎。  \n   - **坐姿矫正**：背部挺直、双脚平放，屏幕中心与视线保持 50‑70 cm 距离。\n\n---\n\n## 4. 硬件与设置：让技术发挥极限\n\n| 硬件 | 推荐配置 | 作用说明 |\n|------|----------|----------|\n| **显示器** | 240 Hz、1440p、G‑Sync | 减少画面撕裂，提升反应速度 |\n| **鼠标** | 400‑800 DPI、可调重量 60‑80 g | 精准控制，适配不同握法 |\n| **键盘** | 机械轴体（青轴/红轴）+ 防冲击键帽 | 快速触发，降低误触 |\n| **耳机** | 7.1 虚拟环绕声、降噪 | 捕捉脚步、炸弹声的微弱差异 |\n| **网络** | 有线千兆、Ping ≤ 15 ms | 稳定帧率，避免延迟波动 |\n\n> **设置细节**：在 **CS2** → **视频设置** 中，将 **渲染分辨率** 调至原始 1920×1080，关闭 **抗锯齿** 与 **阴影**，保证最高帧率；在 **声音** 中开启 **立体声混响** 与 **环境音增强**，帮助定位。\n\n---\n\n## 5. 成为职业选手的路线图\n\n| 阶段 | 时间 | 目标 | 关键行动 |\n|------|------|------|----------|\n| **新手期**（0‑3 月） | 100 小时 | 掌握准星、喷射、基础地图 | 每周完成 3 次 5v5 竞技，复盘 2 场 |\n| **提升期**（3‑9 月） | 500 小时 | 熟悉 5 张主流地图、角色定位 | 加入线上战队、参加小型联赛 |\n| **巅峰期**（9‑18 月） | 1000 小时 | 稳定 1.0 KD、经济判断准确率 ≥ 80% | 参加地区职业联赛、接受教练辅导 |\n| **职业期**（18 月+） | 持续 | 进入官方俱乐部、代表国家队 | 维护体能、心理、战术全方位平衡 |\n\n> **提醒**：进阶并非线性，遇到瓶颈时及时回顾复盘、调整训练计划，比单纯加时更有效。\n\n---\n\n## 结语\n\nCS2 的竞技深度远超表面的枪法对决，真正的高手必须在 **技术细节、战术全局、心理韧性与硬件保障** 四条主线中同步进化。遵循本文提供的系统化路径，保持 **“每周一次目标、每月一次评估、每季一次升级”** 的迭代节奏，你将从新手逐步迈入职业选手的行列。祝你在下一局的 **CT** 侧占领高点，或在 **T** 侧成功炸弹——胜利，正在等着你。  ', NULL, 30, NULL, 'markdown', '', NULL, NULL, 'date-title', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 0, 0, 0, 0, 0, 0, 0, '0', '0', '', '2026-03-22 00:00:09', '', '2026-03-22 00:00:10', NULL, NULL);
INSERT INTO `gb_articles` VALUES (103, NULL, 'zh-CN', 74, 'CS2终极进阶指南：从新手到职业选手', NULL, '', '', '', '# CS2终极进阶指南：从新手到职业选手  \n\n> Counter‑Strike 2（CS2）是当今最具竞争性的第一人称射击游戏之一。想要从“刚学会开枪”迈向职业赛场，既需要扎实的技术基础，也要掌握系统的训练方法与赛场心态。本文按照**新手 → 进阶 → 高手 → 职业**四个阶段，提供一套可操作的完整路线图，帮助你在 800 字左右的篇幅里快速定位自己的成长路径。\n\n---\n\n## Ⅰ. 新手阶段：打好基础\n\n| 目标 | 关键点 | 推荐练习 |\n|------|--------|----------|\n| 熟悉游戏界面与操作 | - 了解主菜单、设置、地图浏览<br>- 熟练键位（移动、蹲下、跳跃、换弹） | 每局结束后回放，检查是否出现误操作 |\n| 掌握枪械基本特性 | - **R8 Revolver**、**USP‑S**、**Glock‑18**（手枪）<br>- **AK‑47**、**M4A1‑S**（步枪）<br>- 了解后坐力、射程、弹道 | 进入 **练习模式**（练枪场），对每把枪进行 30 发点射，记录弹道偏移 |\n| 养成准星控制习惯 | - 使用 **静止准星**（默认）<br>- 练习 **点射** 与 **短点连射** | **Aim Lab** 或 **Kovaak’s** 中的 “Micro‑Shot” 训练，目标命中率 ≥ 80 % |\n| 基础战术概念 | - 站位、掩体、抢点<br>- 简单的 **Rush** 与 **Default** 战术 | 观看官方教学视频，跟随 **5 v 5** 进行实战演练，记录死亡原因 |\n\n> **小贴士**：新手期每周至少完成 **10 小时** 的实战+练习，保持手感的连贯性。\n\n---\n\n## Ⅱ. 进阶阶段：提升个人技术\n\n### 1. 枪法细化  \n- **控制后坐力**：在同一方向持续射击时，手动向左上方轻微拉动鼠标，形成“倒“V”形轨迹。  \n- **爆头优先**：练习 **90°转向** 与 **瞬间点射**，让每一次开火都有可能击中头部。  \n\n### 2. 经济管理  \n- 熟悉每回合的 **买枪、买装** 时机。  \n- 学会 **抢夺** 与 **保留** 经济，避免因一次失误导致整局崩盘。  \n\n### 3. 地图意识  \n- 记忆 **每张地图的关键点位**（如 Dust II 的 **Long A**、**B‑Tunnels**）。  \n- 使用 **小地图** 与 **语音指令**（如 “CT‑mid clear”）快速传递信息。  \n\n### 4. 训练工具  \n- **CS2 内置练枪场**：设置 **“练习模式 → 只显示弹道”**，专注于后坐力。  \n- **第三方工具**：**Aim Lab**（自定义 CS2 模式）+ **Steam Workshop** 中的 “**training_map**”。  \n\n> **练习建议**：每周挑选 **1 张地图**，完成 **5 局** 的 **“死斗”**（Deathmatch），并在每局结束后记录 **K/D**、**ADR**（平均伤害）等数据，持续跟踪提升。\n\n---\n\n## Ⅲ. 高手阶段：团队协作与战术深度\n\n### 1. 角色定位  \n- **入口（Entry）**：冲锋、打开视野。  \n- **狙击（AWPer）**：控制长距离点位。  \n- **支援（Support）**：投掷闪光、烟雾、燃烧瓶。  \n- **守点（Lurker）**：隐藏于侧翼，制造意外。  \n\n### 2. 进阶战术  \n- **双点位切换**：如 **CT** 侧的 **“B‑Site → Mid”** 快速轮换。  \n- **假动作（Fake）**：利用 **烟雾** 与 **声波** 迷惑对手，制造错位。  \n- **经济轮转**：在 **Eco** 回合中使用 **P250**、**SMG** 争取信息，保留资金。  \n\n### 3. 心理调控  \n- **情绪管理**：保持冷静，避免“连败”导致的情绪波动。  \n- **复盘**：每局结束后，使用 **CS2 回放** 分析关键回合，标记 **错误决策** 与 **优秀操作**。  \n\n> **实战练习**：加入 **中低段位俱乐部**（如 **Rank Gold** 以上），每周固定 **2 场** 5 v 5 练习赛，重点练习角色切换与战术执行。\n\n---\n\n## Ⅳ. 职业阶段：从选手到明星\n\n### 1. 体能与生活管理  \n- **作息规律**：每日 **8 小时** 睡眠，保持反应速度。  \n- **体能训练**：每周 **3 次** 有氧 + 2 次 **手指灵活性** 练习（如握力器、弹跳训练）。  \n\n### 2. 高强度训练体系  \n| 时间段 | 内容 | 目的 |\n|--------|------|------|\n| 09:00‑10:30 | 个人技术（Aim、反应） | 维持枪法巅峰 |\n| 10:45‑12:15 | 战术复盘（录像） | 纠正思维误区 |\n| 13:30‑15:30 | 队内对抗（Scrim） | 提升协同 |\n| 16:00‑17:30 | 心理训练（冥想、呼吸） | 稳定情绪 |\n| 18:00‑20:00 | 观赛学习（顶级赛事） | 吸收新meta |\n\n### 3. 数据化提升  \n- 使用 **Mobalytics**、**Tracker GG** 等平台，监控 **KD、ADR、Clutch率** 等关键指标。  \n- 每月制定 **KPI**（如 **Clutch成功率 ≥ 30 %**），完成后进行奖励与复盘。  \n\n### 4. 职业赛道入口  \n- **参加线上联赛**（如 **ESEA、FACEIT**）获取曝光。  \n- **加入战队试训**：准备 **个人简历**（成绩、录像链接）并主动联系教练。  \n- **社交媒体运营**：在 **YouTube、Twitch** 定期直播，提升个人品牌价值。  \n\n> **成功案例**：许多职业选手在 **18 岁** 前完成以上四阶段的系统训练，随后在 **LCS**、**BLAST** 等顶级联赛中崭露头角。\n\n---\n\n## 结语  \n\nCS2 的晋级之路并非“一蹴而就”，而是**技术、战术、心理、生活**四个维度的综合提升。遵循本文的阶段性目标，保持**持续练习 + 数据复盘**的闭环，你将从新手的“枪口抖动”成长为赛场上能够独当一面的职业选手。祝你在下一场比赛中 **“Ace”** 归来！  ', NULL, 30, NULL, 'markdown', '', NULL, NULL, 'date-title', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 0, 0, 0, 0, 0, 0, 0, '0', '0', '', '2026-03-23 00:00:07', '', '2026-03-23 00:00:07', NULL, NULL);
INSERT INTO `gb_articles` VALUES (104, NULL, 'zh-CN', 75, 'CS2终极进阶指南：从新手到职业选手', NULL, '', '', '', '# CS2 终极进阶指南：从新手到职业选手  \n\n> **本文目标**：帮助零基础玩家在 800 字左右的篇幅内，快速掌握《Counter‑Strike 2》从入门到职业层级的核心要点。  \n\n---  \n\n## 1. 认识 CS2  \n\n- **游戏定位**：CS2 是一款强调**精确射击、经济管理与团队配合**的第一人称射击游戏。  \n- **核心循环**：**抢占地图 → 购买武器 → 完成目标（炸弹/解救人质） → 结算经济 → 重复**。  \n- **职业赛制**：大多数职业赛采用 **30 回合（前15回合抢夺、后15回合防守）**，每回合 1.5 分钟，时间管理尤为关键。  \n\n---  \n\n## 2. 基础操作  \n\n| 操作 | 推荐键位 | 关键要点 |\n|------|----------|----------|\n| 移动 | `WASD`   | **保持低姿态**（Shift）时移动速度更慢，但更不易被检测。 |\n| 瞄准 | 鼠标左键 | **短点射**（Burst）在中远距离更稳定，避免连射导致子弹散布。 |\n| 投掷 | `Q/E`   | **闪光/烟雾/手雷**的投掷点位需提前记忆。 |\n| 换枪 | `1‑5`   | **主武器优先**，副武器仅在紧急情况下使用。 |\n\n- **练习方式**：打开 **训练模式**，将灵敏度固定为 **2.5–3.0**（根据个人 DPI 调整），连续完成 10 轮 **前进‑后退‑左转‑右转**，确保鼠标移动平滑。  \n\n---  \n\n## 3. 枪法与瞄准  \n\n1. **掌握两大射击模式**  \n   - **点射（Tap）**：适用于远距离（>30 m）或高价值武器（AWP、M4A4）。  \n   - **点射+短连（Burst）**：中距离（15–30 m）时 2‑3 发为佳，可兼顾精准与伤害。  \n\n2. **练习目标**  \n   - **Headshot 训练地图**（如 “aim_botz”）每天 30 分钟，目标是 **90 % 以上的头部击杀率**。  \n   - **反应时测试**：使用 “training_aim_csgo2” 记录 **反应时间**，保持在 **200 ms 以下**。  \n\n3. **武器选择原则**  \n   - **CT 侧**：首选 **M4A1‑S**（精准）或 **AK‑47**（高伤害）。  \n   - **T 侧**：首选 **AK‑47**（单发即可击倒），副武器 **USP‑S** / **Glock‑18** 用于抢占先手。  \n\n---  \n\n## 4. 经济与购买  \n\n- **经济公式**：  \n  `回合收入 = 800（首回合） + 连杀奖励（+50/100/150） + 失误罚款（-50）`。  \n- **常用买枪策略**：  \n  - **全装**：`$8000`（M4A1‑S + AWP） → 只在 **经济充足**（>$8000）时使用。  \n  - **半装**：`$4500`（M4A1‑S + 2 SMG） → **保留** 资金以备后续回合。  \n  - **节俭回合**：只买 **防弹衣**（$650）+ **手雷**，为后续回合积累资金。  \n\n- **经济判断**：观察对手的 **买枪频率**，若出现 **Eco（经济回合）**，可采取 **压制** 战术。  \n\n---  \n\n## 5. 地图与位置  \n\n| 地图 | 关键点位 | 作用 |\n|------|----------|------|\n| Dust II | 长A、短A、B点中路 | 控制 **视野** 与 **炸弹放置**。 |\n| Mirage | 中路、A箱、B箱 | **分割** 对手视线，制造 **交叉火力**。 |\n| Inferno | 中路、二楼、Banana | **快速支援** 与 **侧翼突袭**。 |\n\n- **学习方式**：使用 **“map overview”** 插件，记忆每条路线的 **常用投掷点** 与 **隐蔽位置**。  \n- **实战练习**：在 **练习模式** 中跑完整张地图，记录 **每秒钟的视野覆盖**，形成 **“热区图”**，帮助判断敌人常出现的角落。  \n\n---  \n\n## 6. 团队沟通  \n\n1. **简洁报点**：  \n   - 位置使用 **“A‑short”**、**“B‑CT”**、**“mid‑window”** 等固定词汇。  \n   - 仅报告 **“敌人数量”**、**“武器类型”**、**“是否埋雷”**。  \n\n2. **指令层级**：  \n   - **领队**：决定 **进攻/防守路线**，使用 **“push A”**、**“rotate B”**。  \n   - **辅助**：负责 **投掷烟雾/闪光**，并在关键时刻 **呼叫支援**。  \n\n3. **语音礼仪**：避免 **负面情绪**，即使失误也应保持 **积极鼓励**，提升整体心态。  \n\n---  \n\n## 7. 心理与体能  \n\n- **呼吸控制**：每次射击前做 **深呼吸（2 秒）**，帮助稳定手抖。  \n- **间歇休息**：每 **30 分钟** 游戏后休息 **5 分钟**，防止视力疲劳。  \n- **比赛心态**：把每局视为 **“学习单元”**，关注 **过程** 而非 **结果**，降低焦虑。  \n\n---  \n\n## 8. 训练计划（示例）  \n\n| 时间段 | 内容 | 目标 |\n|--------|------|------|\n| 周一‑周三 | 30 分钟 Aim Botz + 15 分钟 反应时训练 | Headshot ≥ 90 % |\n| 周四 | 1 小时 地图走位 + 投掷点位练习 | 熟悉 3 张主流地图 |\n| 周五 | 2 小时 实战对局（排位） | 维持 **K/D ≥ 1.0** |\n| 周末 | 观看职业赛录像 + 复盘 1 小时 | 学习 **战术布局** 与 **决策** |\n\n坚持 **4 周**，即可在排位中稳定达到 **Gold Nova 2** 以上，进一步提升至 **高级别**。  \n\n---  \n\n## 9. 进阶资源  \n\n- **官方指南**：Valve 官方博客与更新日志。  \n- **教学视频**：YouTube 频道 “**3kliksphilip**”“**WarOwl**”。  \n- **数据分析**：使用 **CS:GO Stats**、**Tracker GG** 查看个人击杀分布与经济曲线。  \n- **社群平台**：Discord “CS2 Training” 服务器，定期组织 **Scrimmage** 练习。  \n\n---  \n\n## 10. 结语  \n\n从 **新手** 到 **职业选手**，CS2 的成长路径并非“一蹴而就”。关键在于：\n\n1. **扎实基础**：每一次移动、每一次开火都要有意识地练习。  \n2. **系统化训练**：按照 **枪法 → 经济 → 地图 → 团队** 的层级递进。  \n3. **持续复盘**：每局结束后记录 **失误点** 与 **成功点**，形成闭环。  \n\n只要保持 **高效训练 + 正向心态**，你也能在激烈的竞技舞台上站上 **职业赛的舞台**。祝你在《Counter‑Strike 2》中一路升级、终成高手！  ', NULL, 30, NULL, 'markdown', '', NULL, NULL, 'date-title', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 0, 0, 0, 0, 0, 0, 0, '0', '0', '', '2026-03-24 00:00:07', '', '2026-03-24 00:00:07', NULL, NULL);
INSERT INTO `gb_articles` VALUES (105, NULL, 'zh-CN', 76, 'CS2终极进阶指南：从新手到职业选手', NULL, '', '', '', '# CS2终极进阶指南：从新手到职业选手  \n\n> **一句话概括**：掌握基础、精炼技巧、深化战术、锤炼心态，循序渐进，方能在《Counter‑Strike 2》中脱颖而出。\n\n---\n\n## 目录\n1. [新手入门：基础概念与必备设置](#1)  \n2. [核心技巧：瞄准、移动与投掷](#2)  \n3. [地图与位置感知](#3)  \n4. [团队协作与沟通](#4)  \n5. [心理与体能训练](#5)  \n6. [硬件与软件优化](#6)  \n7. [系统化练习计划](#7)  \n\n---\n\n<a id=\"1\"></a>\n## 1. 新手入门：基础概念与必备设置  \n\n| 项目 | 推荐值 | 说明 |\n|------|--------|------|\n| **分辨率** | 1920×1080（或更高）| 保持 16:9，避免拉伸，视野更清晰 |\n| **FOV** | 90–103 | 视野过宽会导致目标变小，影响精准度 |\n| **灵敏度** | 400–800 DPI + 0.5–1.0（CS2）| 先在训练模式找出最舒适的数值，后期再微调 |\n| **键位布局** | “左手”或 “右手”自定义 | 常用键位（跳、蹲、投掷）放在易达位置，减少手指移动距离 |\n\n- **熟悉 UI**：熟练使用主菜单、买枪界面、比分板，避免比赛中因操作失误浪费时间。  \n- **基础术语**：CT、T、B、A、保留位、点位、抢占等，建议先背诵官方地图简介。  \n\n---\n\n<a id=\"2\"></a>\n## 2. 核心技巧：瞄准、移动与投掷  \n\n### 2.1 瞄准（Aim）  \n1. **预瞄（Pre‑aim）**：在敌人出现前将准星对准可能出现的角落。  \n2. **点射（Tap‑fire） vs. 连射**：  \n   - **点射**适用于中远距离（如AWP、M4A1‑S）。  \n   - **连射**适用于近距离冲锋（如AK‑47、SG 553）。  \n3. **训练工具**：  \n   - **Aim Lab / Kovaak’s**：每日 30 分钟，分别练习追踪（Tracking）和反应（Reaction）。  \n   - **CS2 练习模式**：`aim_botz`、`training_aim_csgo2`，重点练习喷射控制（Spray‑Control）和爆头（Headshot）率。\n\n### 2.2 移动（Movement）  \n- **站立射击（Stop‑Shoot）**：在开火前必须完全停止，利用 **Counter‑Strafe**（A/D 快速切换）实现瞬间停顿。  \n- **跳射（Jump‑Shoot）**：仅在极端情况下使用，如抢占高点的瞬间击杀。  \n- **滑步（Bunny‑hop）**：在专业比赛中已被削弱，建议仅用于逃生或制造视觉干扰。\n\n### 2.3 投掷（Utility）  \n| 投掷道具 | 常用技巧 | 关键位置 |\n|----------|----------|----------|\n| **闪光弹** | 双闪（双向）+ 低抛 | 入口、狭窄通道 |\n| **烟雾弹** | 1‑2‑3 抛（左/右/上） | 爆破点、视线封锁 |\n| **燃烧瓶** | 低抛+弹道延迟 | 队友冲锋、逼退敌人 |\n| **诱饵弹** | 隐蔽放置 | 防守侧后方、假点位 |\n\n- **练习地图**：`csgo_workshop_map` 中的 `smoke_training`、`flash_training`，熟记每条投掷线路。\n\n---\n\n<a id=\"3\"></a>\n## 3. 地图与位置感知  \n\n1. **全局视野**：在每局开局前，先观察雷达，记住 **CT/CT‑Spawn**、**Bomb‑Site**、**主要通道**。  \n2. **常用路线**：  \n   - **Dust II**：`Long → Mid → B`、`Catwalk → A`  \n   - **Mirage**：`Palace → A`、`B‑Apps → B`  \n   - **Inferno**：`Banana → B`、`Mid → A`  \n3. **位置记忆**：使用 **地图标记**（`Ctrl + M`）在训练模式中标记常用点位，赛前复盘。  \n4. **视野控制**：利用 **烟雾**、**闪光**、**声波**（如脚步声）推断敌方位置，避免盲目冲刺。\n\n---\n\n<a id=\"4\"></a>\n## 4. 团队协作与沟通  \n\n- **信息传递**：  \n  - **敌人位置**：`<位置> <人数> <装备>`（例：`B‑Apps 两人 AK`）  \n  - **状态报告**：`我在 A‑Site，已买装`、`弹药不足`  \n  - **策略指令**：`全员推 A`、`分散防守 B`  \n- **语音礼仪**：保持简洁、准确，避免情绪化的指责。  \n- **角色分工**：  \n  - **入口（Entry）**：首冲，负责开路。  \n  - **支援（Support）**：投掷、掩护。  \n  - **狙击手（AWPer）**：控制远点位。  \n  - **后卫（Lurker）**：侧翼埋伏，防止被包抄。  \n- **回合复盘**：赛后用 **Demo** 回放，分析失误、找出改进点。\n\n---\n\n<a id=\"5\"></a>\n## 5. 心理与体能训练  \n\n| 方面 | 方法 | 目标 |\n|------|------|------|\n| **专注力** | 采用 **番茄工作法**（25 min 练习 + 5 min 休息）| 防止疲劳、保持高效 |\n| **情绪管理** | 赛前深呼吸、赛后记录情绪日志| 降低“翻车”后情绪波动 |\n| **体能** | 每日 30 min 有氧 + 手腕伸展 | 提高反应速度、预防受伤 |\n| **睡眠** | 保证 7–9 h 高质量睡眠 | 记忆巩固、反应时间最优化 |\n\n- **心理训练**：可使用 **视觉化**（想象自己完成一次完美的 A‑Site 爆破）或 **自我暗示**（如“我每一次射击都精准”）提升自信。  \n\n---\n\n<a id=\"6\"></a>\n## 6. 硬件与软件优化  \n\n- **显示器**：144 Hz 以上，低输入延迟（≤1 ms）是基本要求。  \n- **鼠标**：400–800 DPI + 低抖动的光学或激光传感器。  \n- **键盘**：机械轴体（红轴/茶轴）提供快速回弹。  \n- **网络**：有线连接，Ping ≤ 30 ms，使用 **QoS** 限制后台流量。  \n- **驱动/系统**：保持显卡驱动最新，关闭系统动画、关闭不必要的后台进程。  \n\n---\n\n<a id=\"7\"></a>\n## 7. 系统化练习计划（示例）  \n\n| 时间段 | 内容 | 目标 |\n|--------|------|------|\n| **周一‑周三** | 30 min Aim Lab → 1 h 竞技模式（练习站位） | 提升准星、熟悉地图 |\n| **周四** | 1 h 观看职业选手（如 s1mple）回放，笔记关键点 | 学习战术、思考方式 |\n| **周五** | 2 h 队伍训练（沟通、配合） | 强化团队默契 |\n| **周末** | 2 h 线上比赛或本地 LAN（实战） | 检验学习成果 |\n| **每日** | 10 min 手部伸展、5 min 眼保健操 | 防止疲劳、保持健康 |\n\n> **关键**：坚持 **量化** 与 **反馈**，每周对比 K/D、ADR、爆头率等数据，及时调整训练重点。\n\n---\n\n## 结语  \n\n从新手到职业选手的路程并非一蹴而就，它需要 **技术**、**战术**、**心理** 与 **团队** 四大维度的同步提升。只要遵循本指南的系统化步骤，保持高强度且有针对性的练习，配合科学的硬件与生活作息，你将逐步缩小与职业玩家的差距，迈向 CS2 赛场的巅峰。  \n\n祝你在《Counter‑Strike 2》中玩得开心、进步飞速，早日站上职业舞台！ 🎮🚀', NULL, 30, NULL, 'markdown', '', NULL, NULL, 'date-title', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 0, 0, 0, 0, 0, 0, 0, '0', '0', '', '2026-03-24 00:00:09', '', '2026-03-24 00:00:09', NULL, NULL);
INSERT INTO `gb_articles` VALUES (106, NULL, 'zh-CN', 77, 'CS2终极进阶指南：从新手到职业选手', NULL, '', '', '', '# CS2终极进阶指南：从新手到职业选手  \n\n> **一句话概括**：掌握基础、强化技术、深化战术、锤炼心理，循序渐进，才能在《反恐精英2》（CS2）中脱颖而出，迈向职业赛场。\n\n---  \n\n## 1️⃣ 新手入门：打好根基  \n\n| 关键要点 | 具体做法 |\n| -------- | -------- |\n| **熟悉地图** | 先在练习模式（Workshop）中跑图，记住每条通道、常用掩体和关键点位。 |\n| **武器机制** | 了解每把枪的后坐力、射程、弹道；推荐从 **M4A1‑S**、**AK‑47**、**USP‑S** 开始练习。 |\n| **基础操作** | 练习站立、蹲下、跳跃、冲刺的切换；保持 90°转向的流畅度，避免“卡顿”。 |\n| **瞄准训练** | 使用 **Aim Lab**、**Kovaak’s** 或 CS2 自带的 **练习靶场**，每日 30 分钟的精准射击。 |\n\n> **小技巧**：在练习模式下打开 **“显示弹道”**，观察子弹落点，帮助纠正手抖导致的偏移。\n\n---\n\n## 2️⃣ 技能提升：从“射手”到“狙神”  \n\n1. **控制后坐力**  \n   - **短点射**：先点射 2‑3 发，再进行下一次射击，保持弹道在同一水平线上。  \n   - **长点射**：在射击前先轻轻下压鼠标，形成与枪械后坐力相反的力，形成 **“抖动”** 轨迹。  \n\n2. **移动与射击的平衡**  \n   - **站立射击**：在关键交火时保持站立，提高命中率。  \n   - **冲刺射击**：仅在突围或逼迫对手时使用，需提前预判对手位置。  \n\n3. **手感与灵敏度**  \n   - 建议 **DPI 400‑800** + **游戏灵敏度 0.9‑1.2**（视个人手掌大小而定）。  \n   - 采用 **“360°转向”** 计算法，确保在全图快速转向时不失准。  \n\n---\n\n## 3️⃣ 战术与团队协作  \n\n### 3.1 基本战术概念  \n\n- **占点与防守**：掌握 **“A点占领‑B点防守”** 的轮换节奏，利用烟雾弹（Smoke）切断视线，阻止敌方快速突进。  \n- **信息共享**：使用 **语音指令**（如 “enemy at long A”）或 **文字标记**（地图上红点），确保全队同步情报。  \n\n### 3.2 常用阵型  \n\n| 阵型 | 适用场景 | 关键职责 |\n| ---- | -------- | -------- |\n| **双狙守点** | 关键点位（A、B）防守 | ① 维持视野 ② 及时支援突围 |\n| **三人突击** | 进攻中路 | ① 破拆 ② 抢占高地 |\n| **二二分割** | 防守侧翼 | ① 防止侧翼渗透 ② 形成交叉火力 |\n\n### 3.3 练习团队配合  \n\n- **每日 5 分钟**的 **“站位演练”**（无枪战），熟悉每位队友的站位与换位节奏。  \n- **每周一次**的 **“模拟赛”**（5 v 5），在不计分的环境下检验战术执行效果。  \n\n---\n\n## 4️⃣ 心理与体能：职业选手的隐形武器  \n\n- **情绪管理**：比赛中出现失误时，立刻深呼吸 3 次，避免情绪蔓延影响后续操作。  \n- **专注训练**：使用 **番茄工作法**（25 min 专注 + 5 min 休息），保持高效的练习节奏。  \n- **体能保障**：每日 30 分钟的 **手指伸展** 与 **肩颈放松**，预防长期游戏导致的劳损。  \n\n---\n\n## 5️⃣ 系统化练习计划（8 周）  \n\n| 周次 | 目标 | 训练内容 | 评估方式 |\n| ---- | ---- | -------- | -------- |\n| 1‑2 | 基础熟悉 | 地图跑图、武器射击 | 记录每局死亡原因比例 |\n| 3‑4 | 瞄准提升 | Aim Lab 30 min + 实战点射 | 命中率 ≥ 45% |\n| 5‑6 | 战术执行 | 组队演练 3‑5 v 5 | 胜率提升 10% |\n| 7‑8 | 心理稳固 | 赛前冥想、赛后复盘 | 失误率下降 15% |\n\n> **进阶提示**：每轮训练结束后，回放 **“关键回合”**（Top 5），标记失误点并制定改进方案。\n\n---\n\n## 6️⃣ 结语  \n\n从新手到职业选手，**技术**、**战术**、**心理** 三大维度缺一不可。坚持系统化练习、保持良好的生活作息、积极参与团队沟通，你将逐步缩小与职业水平的差距。  \n**记住**：CS2 是一场 **“不断迭代的思考与反应”**，只有在每一次对局中汲取经验，才能在赛场上实现真正的突破。祝你早日站上世界赛的舞台！  ', NULL, 30, NULL, 'markdown', '', NULL, NULL, 'date-title', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 0, 0, 0, 0, 0, 0, 0, '0', '0', '', '2026-03-25 00:00:06', '', '2026-03-25 00:00:05', NULL, NULL);

-- ----------------------------
-- Table structure for gb_atomic_tool
-- ----------------------------
DROP TABLE IF EXISTS `gb_atomic_tool`;
CREATE TABLE `gb_atomic_tool`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '工具ID',
  `tool_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '工具编码（唯一标识）',
  `tool_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '工具名称',
  `tool_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '工具类型：ai-AI生成工具, api-外部API工具, builtin-内置工具',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '工具描述',
  `config` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '工具配置（JSON格式）',
  `inputs` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '输入参数定义（JSON数组）',
  `outputs` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '输出参数定义（JSON数组）',
  `enabled` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用：0-禁用 1-启用',
  `site_id` bigint NULL DEFAULT NULL COMMENT '站点ID（多站点支持）',
  `category_id` bigint NULL DEFAULT NULL COMMENT '分类ID',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tool_type`(`tool_type` ASC) USING BTREE,
  INDEX `idx_enabled`(`enabled` ASC) USING BTREE,
  INDEX `idx_site_id`(`site_id` ASC) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `uk_tool_code_site`(`tool_code` ASC, `site_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 36 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '原子工具表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_atomic_tool
-- ----------------------------
INSERT INTO `gb_atomic_tool` VALUES (1, 'text_optimizer', '文本优化工具', 'ai', '使用AI优化文本内容，使其更流畅、专业、有条理。适用于文章编辑、内容润色等场景', '{\"aiPlatformId\":1,\"systemPrompt\":\"你是一位专业的文案优化助手。你的任务是优化用户提供的文本内容，使其：\\n1. 语言更加流畅自然\\n2. 表达更加准确专业\\n3. 结构更加清晰有条理\\n4. 保持原文的核心意思和关键信息\\n5. 适当润色，提升可读性\\n\\n注意：\\n- 保持原文的markdown格式\\n- 不要添加额外的解释说明\\n- 直接输出优化后的内容\",\"userPromptTemplate\":\"请优化以下文本：\\n\\n{{originalText}}\",\"temperature\":0.7,\"maxTokens\":2000,\"modelName\":\"openai/gpt-oss-120b\"}', '[{\"name\":\"originalText\",\"type\":\"textarea\",\"label\":\"原始文本\",\"required\":true,\"description\":\"需要优化的文本内容\",\"rows\":5}]', '[{\"name\":\"generatedText\",\"type\":\"text\",\"label\":\"优化后文本\",\"description\":\"AI优化后的文本内容\"},{\"name\":\"wordCount\",\"type\":\"number\",\"label\":\"字数\",\"description\":\"优化后的文本字数\"}]', 1, 9, 127, 'admin', '2026-01-04 14:50:47', '', '2026-03-17 20:24:19', '文本优化工具 - 第一个原子工具示例');
INSERT INTO `gb_atomic_tool` VALUES (2, 'ai_article_generator', 'AI文章生成器', 'ai', '根据标题和大纲生成完整文章内容。支持自定义字数、风格等参数', '{\"aiPlatformId\":1,\"systemPrompt\":\"你是一位专业的内容创作者，擅长根据标题和大纲创作高质量文章。要求：\\n1. 内容充实、逻辑清晰\\n2. 语言流畅、表达专业\\n3. 适当使用markdown格式\\n4. 包含必要的段落结构\",\"userPromptTemplate\":\"请根据以下信息创作文章：\\n\\n标题：{{title}}\\n大纲：{{outline}}\\n目标字数：{{wordCount}}字\\n\\n请生成完整文章内容：\",\"temperature\":0.8,\"maxTokens\":3000,\"modelName\":\"openai/gpt-oss-120b\"}', '[{\"name\":\"title\",\"type\":\"text\",\"label\":\"文章标题\",\"required\":true,\"description\":\"文章的标题\"},{\"name\":\"outline\",\"type\":\"textarea\",\"label\":\"文章大纲\",\"required\":false,\"description\":\"文章的大纲或要点（可选）\",\"rows\":3},{\"name\":\"wordCount\",\"type\":\"number\",\"label\":\"目标字数\",\"required\":false,\"default\":800,\"description\":\"期望生成的文章字数\"}]', '[{\"name\":\"generatedText\",\"type\":\"text\",\"label\":\"生成的文章\",\"description\":\"AI生成的完整文章内容\"},{\"name\":\"wordCount\",\"type\":\"number\",\"label\":\"实际字数\",\"description\":\"生成文章的实际字数\"}]', 1, 9, 127, 'admin', '2026-01-04 14:50:47', '', '2026-03-17 20:24:19', 'AI文章生成工具');
INSERT INTO `gb_atomic_tool` VALUES (3, 'keyword_extractor', 'SEO关键词提取', 'ai', '从标题和内容中提取SEO关键词，帮助优化文章搜索引擎排名', '{\"aiPlatformId\":1,\"systemPrompt\":\"你是一位SEO专家，擅长从内容中提取关键词。要求：\\n1. 提取最相关的3-5个关键词\\n2. 关键词要符合SEO最佳实践\\n3. 考虑搜索意图和用户需求\\n4. 只输出关键词，用逗号分隔\",\"userPromptTemplate\":\"请从以下内容中提取SEO关键词：\\n\\n标题：{{title}}\\n内容：{{content}}\\n\\n请提取5个最相关的SEO关键词（用逗号分隔）：\",\"temperature\":0.5,\"maxTokens\":200,\"modelName\":\"openai/gpt-oss-120b\"}', '[{\"name\":\"title\",\"type\":\"text\",\"label\":\"标题\",\"required\":true,\"description\":\"文章或内容的标题\"},{\"name\":\"content\",\"type\":\"textarea\",\"label\":\"内容\",\"required\":true,\"description\":\"文章或内容的正文\",\"rows\":5}]', '[{\"name\":\"keywords\",\"type\":\"text\",\"label\":\"关键词\",\"description\":\"提取的SEO关键词（逗号分隔）\"}]', 1, 9, 127, 'admin', '2026-01-04 14:50:47', '', '2026-03-17 20:24:19', 'SEO关键词提取工具');
INSERT INTO `gb_atomic_tool` VALUES (4, 'summary_generator', '文章摘要生成器', 'ai', '自动生成文章摘要，用于文章列表展示或SEO描述', '{\"aiPlatformId\":1,\"systemPrompt\":\"你是一位专业的内容编辑，擅长提炼文章核心内容。要求：\\n1. 准确概括文章主要内容\\n2. 语言精炼、吸引人\\n3. 控制在指定字数内\\n4. 不要添加原文中没有的信息\",\"userPromptTemplate\":\"请为以下文章生成摘要（{{maxLength}}字以内）：\\n\\n{{content}}\",\"temperature\":0.6,\"maxTokens\":500}', '[{\"name\":\"content\",\"type\":\"textarea\",\"label\":\"文章内容\",\"required\":true,\"description\":\"需要生成摘要的文章内容\",\"rows\":8},{\"name\":\"maxLength\",\"type\":\"number\",\"label\":\"最大长度\",\"required\":false,\"default\":200,\"description\":\"摘要的最大字数\"}]', '[{\"name\":\"summary\",\"type\":\"text\",\"label\":\"文章摘要\",\"description\":\"生成的文章摘要\"}]', 1, 9, 127, 'admin', '2026-01-04 14:50:47', '', '2026-03-17 20:24:19', '文章摘要生成工具');
INSERT INTO `gb_atomic_tool` VALUES (5, 'save_article', '保存文章工具', 'system', '将生成的文章内容保存到数据库，支持设置网站、分类、状态等信息，并可关联游戏、盒子、短剧', '{}', '[{\"name\":\"title\",\"type\":\"text\",\"label\":\"文章标题\",\"required\":true,\"description\":\"文章的标题\"},{\"name\":\"content\",\"type\":\"textarea\",\"label\":\"文章内容\",\"required\":true,\"description\":\"文章的正文内容（支持Markdown格式）\",\"rows\":10},{\"name\":\"siteId\",\"type\":\"number\",\"label\":\"目标网站ID\",\"required\":true,\"description\":\"文章所属网站的ID\"},{\"name\":\"categoryId\",\"type\":\"number\",\"label\":\"文章分类ID\",\"required\":false,\"description\":\"文章分类ID（可选）\"},{\"name\":\"description\",\"type\":\"textarea\",\"label\":\"文章摘要\",\"required\":false,\"description\":\"文章的简短描述或摘要\",\"rows\":3},{\"name\":\"keywords\",\"type\":\"text\",\"label\":\"关键词\",\"required\":false,\"description\":\"文章的SEO关键词（逗号分隔）\"},{\"name\":\"author\",\"type\":\"text\",\"label\":\"作者\",\"required\":false,\"description\":\"文章作者\"},{\"name\":\"coverUrl\",\"type\":\"text\",\"label\":\"封面图URL\",\"required\":false,\"description\":\"文章封面图片URL\"},{\"name\":\"status\",\"type\":\"text\",\"label\":\"发布状态\",\"required\":false,\"default\":\"0\",\"description\":\"文章状态：0-草稿，1-已发布，2-已删除\"},{\"name\":\"gameIds\",\"type\":\"text\",\"label\":\"关联游戏ID\",\"required\":false,\"description\":\"关联的游戏ID，多个用逗号分隔\"},{\"name\":\"gameBoxIds\",\"type\":\"text\",\"label\":\"关联游戏盒子ID\",\"required\":false,\"description\":\"关联的游戏盒子ID，多个用逗号分隔\"},{\"name\":\"dramaIds\",\"type\":\"text\",\"label\":\"关联短剧ID\",\"required\":false,\"description\":\"关联的短剧ID，多个用逗号分隔\"}]', '[{\"name\":\"articleId\",\"type\":\"number\",\"label\":\"文章ID\",\"description\":\"保存成功后的文章ID\"},{\"name\":\"title\",\"type\":\"text\",\"label\":\"文章标题\",\"description\":\"保存的文章标题\"},{\"name\":\"status\",\"type\":\"text\",\"label\":\"文章状态\",\"description\":\"文章的发布状态\"},{\"name\":\"siteId\",\"type\":\"number\",\"label\":\"网站ID\",\"description\":\"文章所属网站ID\"},{\"name\":\"categoryId\",\"type\":\"number\",\"label\":\"分类ID\",\"description\":\"文章所属分类ID\"},{\"name\":\"success\",\"type\":\"boolean\",\"label\":\"是否成功\",\"description\":\"保存是否成功\"},{\"name\":\"message\",\"type\":\"text\",\"label\":\"返回消息\",\"description\":\"操作结果消息\"}]', 1, 9, 132, 'admin', '2026-01-04 15:06:02', '', '2026-03-17 20:24:19', '系统工具 - 自动注册 - 保存文章到数据库');
INSERT INTO `gb_atomic_tool` VALUES (6, 'fetch_titles_from_pool', '标题池读取工具', 'system', '从标题池中读取标题，支持按批次号、使用状态、数量等条件筛选，可用于工作流中批量获取标题进行文章生成', '{\"executorClass\":\"FetchTitlesFromPoolToolExecutor\"}', '[{\"name\":\"batchCode\",\"type\":\"text\",\"label\":\"批次号\",\"required\":false,\"description\":\"标题池批次号，如SITE_1_20260101_001。如不指定则获取所有未使用的标题\"},{\"name\":\"usageStatus\",\"type\":\"select\",\"label\":\"使用状态\",\"required\":false,\"default\":\"0\",\"options\":[{\"label\":\"未使用\",\"value\":\"0\"},{\"label\":\"已使用\",\"value\":\"1\"},{\"label\":\"已废弃\",\"value\":\"2\"},{\"label\":\"全部\",\"value\":\"all\"}],\"description\":\"标题使用状态筛选\"},{\"name\":\"limit\",\"type\":\"number\",\"label\":\"获取数量\",\"required\":false,\"default\":10,\"description\":\"要获取的标题数量，默认10条\"},{\"name\":\"orderBy\",\"type\":\"select\",\"label\":\"排序方式\",\"required\":false,\"default\":\"priority_desc\",\"options\":[{\"label\":\"优先级降序\",\"value\":\"priority_desc\"},{\"label\":\"优先级升序\",\"value\":\"priority_asc\"},{\"label\":\"创建时间降序\",\"value\":\"create_time_desc\"},{\"label\":\"创建时间升序\",\"value\":\"create_time_asc\"}],\"description\":\"标题排序方式\"},{\"name\":\"markAsUsed\",\"type\":\"boolean\",\"label\":\"标记为已使用\",\"required\":false,\"default\":false,\"description\":\"获取后是否将标题标记为已使用状态\"}]', '[{\"name\":\"titles\",\"type\":\"array\",\"label\":\"标题列表\",\"description\":\"获取到的标题数组，每个元素包含id、title、keywords、referenceContent等字段\"},{\"name\":\"count\",\"type\":\"number\",\"label\":\"标题数量\",\"description\":\"实际获取到的标题数量\"},{\"name\":\"batchCode\",\"type\":\"text\",\"label\":\"批次号\",\"description\":\"标题所属的批次号\"},{\"name\":\"success\",\"type\":\"boolean\",\"label\":\"是否成功\",\"description\":\"操作是否成功\"},{\"name\":\"message\",\"type\":\"text\",\"label\":\"返回消息\",\"description\":\"操作结果消息\"}]', 1, 9, 132, 'admin', '2026-01-05 02:02:31', '', '2026-03-17 20:24:19', '系统工具 - 自动注册 - 标题获取');
INSERT INTO `gb_atomic_tool` VALUES (20, 'fetch_titles_from_pool', '标题池读取工具', 'system', '从标题池中读取标题，支持按批次号、使用状态、数量等条件筛选，可用于工作流中批量获取标题进行文章生成', '{\"executorClass\": \"FetchTitlesFromPoolToolExecutor\"}', '[{\"name\":\"batchCode\",\"type\":\"text\",\"label\":\"批次号\",\"required\":false,\"description\":\"标题池批次号，如SITE_1_20260101_001。如不指定则获取所有未使用的标题\"},{\"name\":\"usageStatus\",\"type\":\"select\",\"label\":\"使用状态\",\"required\":false,\"default\":\"0\",\"options\":[{\"label\":\"未使用\",\"value\":\"0\"},{\"label\":\"已使用\",\"value\":\"1\"},{\"label\":\"已废弃\",\"value\":\"2\"},{\"label\":\"全部\",\"value\":\"all\"}],\"description\":\"标题使用状态筛选\"},{\"name\":\"limit\",\"type\":\"number\",\"label\":\"获取数量\",\"required\":false,\"default\":10,\"description\":\"要获取的标题数量，默认10条\"},{\"name\":\"orderBy\",\"type\":\"select\",\"label\":\"排序方式\",\"required\":false,\"default\":\"priority_desc\",\"options\":[{\"label\":\"优先级降序\",\"value\":\"priority_desc\"},{\"label\":\"优先级升序\",\"value\":\"priority_asc\"},{\"label\":\"创建时间降序\",\"value\":\"create_time_desc\"},{\"label\":\"创建时间升序\",\"value\":\"create_time_asc\"}],\"description\":\"标题排序方式\"},{\"name\":\"markAsUsed\",\"type\":\"boolean\",\"label\":\"标记为已使用\",\"required\":false,\"default\":false,\"description\":\"获取后是否将标题标记为已使用状态\"}]', '[{\"name\":\"titles\",\"type\":\"array\",\"label\":\"标题列表\",\"description\":\"获取到的标题数组，每个元素包含id、title、keywords、referenceContent等字段\"},{\"name\":\"count\",\"type\":\"number\",\"label\":\"标题数量\",\"description\":\"实际获取到的标题数量\"},{\"name\":\"batchCode\",\"type\":\"text\",\"label\":\"批次号\",\"description\":\"标题所属的批次号\"},{\"name\":\"success\",\"type\":\"boolean\",\"label\":\"是否成功\",\"description\":\"操作是否成功\"},{\"name\":\"message\",\"type\":\"text\",\"label\":\"返回消息\",\"description\":\"操作结果消息\"}]', 1, 9, NULL, '', '2026-02-07 15:40:21', '', '2026-03-17 20:24:19', NULL);
INSERT INTO `gb_atomic_tool` VALUES (26, 'api_get_user', '获取用户信息', 'api', '通过JSONPlaceholder API获取用户信息（测试用）', '{\"apiUrl\":\"https://jsonplaceholder.typicode.com/users/1\",\"method\":\"GET\",\"contentType\":\"application/json\"}', '[]', '[{\"name\":\"response\",\"type\":\"object\",\"label\":\"API响应数据\",\"description\":\"完整的API返回数据\"},{\"name\":\"statusCode\",\"type\":\"number\",\"label\":\"HTTP状态码\"},{\"name\":\"success\",\"type\":\"boolean\",\"label\":\"是否成功\"}]', 1, 9, NULL, '', '2026-02-07 18:15:53', '', '2026-03-17 20:24:19', NULL);
INSERT INTO `gb_atomic_tool` VALUES (28, 'import_box_games', '导入游戏到盒子（JSON）', 'system', '解析 JSON 字符串，按字段映射规则将游戏数据批量导入到指定游戏盒子中。支持指定 JSON 内数据路径（如 \'data\' 或 \'result.games\'），支持跳过或覆盖已存在的游戏。', '{\"otherConfig\":\"\"}', '[{\"name\":\"jsonData\",\"type\":\"textarea\",\"label\":\"JSON数据\",\"required\":true,\"description\":\"包含游戏数据的 JSON 字符串，可以是数组或包含数组的对象\"},{\"name\":\"boxId\",\"type\":\"number\",\"label\":\"目标盒子ID\",\"required\":true,\"description\":\"要将游戏导入到哪个游戏盒子（用于关联字段映射配置）\"},{\"name\":\"siteId\",\"type\":\"number\",\"label\":\"目标网站ID\",\"required\":true,\"description\":\"游戏所属网站 ID，导入的游戏将归属于该网站\"},{\"name\":\"dataPath\",\"type\":\"text\",\"label\":\"数据路径\",\"required\":false,\"description\":\"JSON 中游戏数组的路径，支持点分语法，如 \'data\'、\'result.games\'；为空时自动探测根节点\"},{\"name\":\"updateExisting\",\"type\":\"text\",\"label\":\"更新已有游戏\",\"required\":false,\"description\":\"是否更新同名已存在的游戏：true=更新，false=跳过（默认 false）\"}]', '[{\"name\":\"totalCount\",\"type\":\"number\",\"label\":\"总数\",\"description\":\"JSON 中的总游戏数\"},{\"name\":\"successCount\",\"type\":\"number\",\"label\":\"成功数\",\"description\":\"成功新增或更新的游戏数\"},{\"name\":\"newCount\",\"type\":\"number\",\"label\":\"新增数\",\"description\":\"成功新增的游戏数\"},{\"name\":\"updatedCount\",\"type\":\"number\",\"label\":\"更新数\",\"description\":\"成功更新的游戏数\"},{\"name\":\"skippedCount\",\"type\":\"number\",\"label\":\"跳过数\",\"description\":\"因已存在而跳过的游戏数\"},{\"name\":\"failureCount\",\"type\":\"number\",\"label\":\"失败数\",\"description\":\"处理失败的游戏数\"},{\"name\":\"message\",\"type\":\"text\",\"label\":\"结果消息\",\"description\":\"导入结果汇总\"},{\"name\":\"success\",\"type\":\"boolean\",\"label\":\"是否成功\",\"description\":\"是否成功完成（false 表示整批失败）\"}]', 1, 9, NULL, '', '2026-03-05 16:13:30', '', '2026-03-17 20:24:19', NULL);
INSERT INTO `gb_atomic_tool` VALUES (33, 'fetch_git_file', '获取Git仓库文件', 'integration', '从Git仓库（GitHub/GitLab/Gitee）中获取指定文件的原始内容，用于数据同步场景。支持私有仓库（需传入访问令牌）。', '{}', '[{\"name\":\"repoUrl\",\"type\":\"text\",\"label\":\"仓库URL\",\"required\":true,\"description\":\"Git仓库地址，支持GitHub/GitLab/Gitee，如 https://github.com/owner/repo\"},{\"name\":\"accessToken\",\"type\":\"text\",\"label\":\"访问令牌\",\"required\":false,\"description\":\"私有仓库或需要提高API速率限制时使用的访问令牌（Personal Access Token）\"},{\"name\":\"filePath\",\"type\":\"text\",\"label\":\"文件路径\",\"required\":true,\"description\":\"文件在仓库中的相对路径，如 data/games.json 或 output/list.json\"},{\"name\":\"branch\",\"type\":\"text\",\"label\":\"分支/标签\",\"required\":false,\"description\":\"分支名或标签名，默认为 main\"}]', '[{\"name\":\"content\",\"type\":\"textarea\",\"label\":\"文件内容\",\"description\":\"文件的原始文本内容（UTF-8）\"},{\"name\":\"fileName\",\"type\":\"text\",\"label\":\"文件名\",\"description\":\"文件名（不含路径）\"},{\"name\":\"fileSize\",\"type\":\"number\",\"label\":\"文件大小\",\"description\":\"文件大小（字节）\"},{\"name\":\"success\",\"type\":\"boolean\",\"label\":\"是否成功\",\"description\":\"文件是否获取成功\"},{\"name\":\"message\",\"type\":\"text\",\"label\":\"返回消息\",\"description\":\"操作结果消息\"}]', 1, 9, NULL, '', '2026-03-05 19:50:31', '', '2026-03-17 20:24:19', NULL);

-- ----------------------------
-- Table structure for gb_box_category_relations
-- ----------------------------
DROP TABLE IF EXISTS `gb_box_category_relations`;
CREATE TABLE `gb_box_category_relations`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `box_id` bigint NOT NULL COMMENT '盒子ID',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `sort_order` int NULL DEFAULT 0 COMMENT '在该分类中的排序',
  `is_primary` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '是否主分类：0-否 1-是',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_box_category`(`box_id` ASC, `category_id` ASC) USING BTREE,
  INDEX `idx_box_id`(`box_id` ASC) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_is_primary`(`is_primary` ASC) USING BTREE,
  INDEX `idx_sort_order`(`sort_order` ASC) USING BTREE,
  CONSTRAINT `fk_box_category_box` FOREIGN KEY (`box_id`) REFERENCES `gb_game_boxes` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_box_category_category` FOREIGN KEY (`category_id`) REFERENCES `gb_categories` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 288 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '游戏盒子分类关联表-用于游戏盒子的多分类' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_box_category_relations
-- ----------------------------
INSERT INTO `gb_box_category_relations` VALUES (274, 9, 42, 0, '1', NULL, '2026-03-25 13:15:59', '', '2026-03-25 13:15:59');
INSERT INTO `gb_box_category_relations` VALUES (275, 9, 43, 1, '0', NULL, '2026-03-25 13:15:59', '', '2026-03-25 13:15:59');
INSERT INTO `gb_box_category_relations` VALUES (276, 9, 44, 2, '0', NULL, '2026-03-25 13:15:59', '', '2026-03-25 13:15:59');
INSERT INTO `gb_box_category_relations` VALUES (277, 9, 45, 3, '0', NULL, '2026-03-25 13:15:59', '', '2026-03-25 13:15:59');
INSERT INTO `gb_box_category_relations` VALUES (278, 8, 42, 0, '1', NULL, '2026-03-25 13:16:10', '', '2026-03-25 13:16:10');
INSERT INTO `gb_box_category_relations` VALUES (279, 8, 43, 1, '0', NULL, '2026-03-25 13:16:10', '', '2026-03-25 13:16:10');
INSERT INTO `gb_box_category_relations` VALUES (280, 8, 44, 2, '0', NULL, '2026-03-25 13:16:10', '', '2026-03-25 13:16:10');
INSERT INTO `gb_box_category_relations` VALUES (281, 8, 45, 3, '0', NULL, '2026-03-25 13:16:10', '', '2026-03-25 13:16:10');
INSERT INTO `gb_box_category_relations` VALUES (282, 7, 42, 0, '1', NULL, '2026-03-25 13:16:19', '', '2026-03-25 13:16:19');
INSERT INTO `gb_box_category_relations` VALUES (283, 7, 43, 1, '0', NULL, '2026-03-25 13:16:19', '', '2026-03-25 13:16:19');
INSERT INTO `gb_box_category_relations` VALUES (284, 7, 44, 2, '0', NULL, '2026-03-25 13:16:19', '', '2026-03-25 13:16:19');
INSERT INTO `gb_box_category_relations` VALUES (285, 7, 45, 3, '0', NULL, '2026-03-25 13:16:19', '', '2026-03-25 13:16:19');
INSERT INTO `gb_box_category_relations` VALUES (286, 7, 47, 4, '0', NULL, '2026-03-25 13:16:19', '', '2026-03-25 13:16:19');
INSERT INTO `gb_box_category_relations` VALUES (287, 3, 42, 0, '1', NULL, '2026-03-25 13:16:28', '', '2026-03-25 13:16:28');

-- ----------------------------
-- Table structure for gb_box_field_mappings
-- ----------------------------
DROP TABLE IF EXISTS `gb_box_field_mappings`;
CREATE TABLE `gb_box_field_mappings`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `box_id` bigint NULL DEFAULT NULL COMMENT '关联的盒子ID，NULL表示通用配置（基于平台+资源类型的默认配置）',
  `resource_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'game' COMMENT '资源类型：game-游戏 / box-盒子',
  `source_field` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '源字段路径（支持嵌套，如：a.b.c或a.0.b）',
  `target_field` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '目标字段名（主表字段或platform_data中的key）',
  `field_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'string' COMMENT '字段类型：string/int/decimal/json/date/boolean',
  `target_location` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'main' COMMENT '目标位置：main-主表gb_games / promotion_link-推广链接JSON(gb_box_game_relations) / platform_data-平台数据JSON(gb_box_game_relations) / relation-关联表直接字段 / category_relation-分类关联',
  `transform_expression` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '转换表达式（可选，用于字段值转换，如：CASE WHEN...）',
  `value_mapping` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '值映射规则(JSON): {type: \"direct/regex/function\", mappings: {...}}',
  `default_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '默认值（源字段不存在或为空时使用）',
  `is_required` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '是否必填：0-否 1-是',
  `conflict_strategy` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'fill_empty' COMMENT '字段冲突策略（main默认fill_empty；relation/promotion_link/platform_data默认overwrite）: fill_empty-补全空字段/overwrite-始终覆盖/skip-保持不变',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '状态：0-禁用 1-启用',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注说明',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_box_resource_source`(`box_id` ASC, `resource_type` ASC, `source_field` ASC, `target_field` ASC, `target_location` ASC) USING BTREE,
  INDEX `idx_platform_resource`(`resource_type` ASC) USING BTREE,
  INDEX `idx_target_location`(`target_location` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_box_id`(`box_id` ASC) USING BTREE,
  INDEX `idx_platform_resource_type`(`resource_type` ASC) USING BTREE,
  INDEX `idx_box_sort_order`(`box_id` ASC, `sort_order` ASC) USING BTREE,
  CONSTRAINT `fk_box_field_mapping_box` FOREIGN KEY (`box_id`) REFERENCES `gb_game_boxes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1722 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '字段映射配置表(支持值映射规则)' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_box_field_mappings
-- ----------------------------
INSERT INTO `gb_box_field_mappings` VALUES (1304, 3, 'game', 'downurl', 'download_url', 'string', 'promotion_link', NULL, NULL, NULL, '1', 'overwrite', 50, '1', '', '2026-02-02 19:52:05', '', '2026-03-08 13:02:42', '游戏下载链接');
INSERT INTO `gb_box_field_mappings` VALUES (1305, 3, 'game', 'dwzdownurl', 'short_url', 'string', 'promotion_link', NULL, NULL, NULL, '0', 'overwrite', 51, '1', '', '2026-02-02 19:52:05', '', '2026-03-08 13:02:42', '短链接下载地址');
INSERT INTO `gb_box_field_mappings` VALUES (1306, 3, 'game', 'downurlmash', 'mash_url', 'string', 'promotion_link', NULL, NULL, NULL, '0', 'overwrite', 52, '1', '', '2026-02-02 19:52:05', '', '2026-03-08 13:02:42', 'Mash版本下载链接');
INSERT INTO `gb_box_field_mappings` VALUES (1307, 3, 'game', 'dwzdownurlmash', 'short_mash_url', 'string', 'promotion_link', NULL, NULL, NULL, '0', 'overwrite', 53, '1', '', '2026-02-02 19:52:05', '', '2026-03-08 13:02:42', '短链接Mash下载地址');
INSERT INTO `gb_box_field_mappings` VALUES (1308, 3, 'game', 'weburl', 'web_url', 'string', 'promotion_link', NULL, NULL, NULL, '0', 'overwrite', 54, '1', '', '2026-02-02 19:52:05', '', '2026-03-08 13:02:42', '游戏网页地址');
INSERT INTO `gb_box_field_mappings` VALUES (1309, 3, 'game', 'h5url', 'h5_url', 'string', 'promotion_link', NULL, NULL, NULL, '0', 'overwrite', 55, '1', '', '2026-02-02 19:52:05', '', '2026-03-08 13:02:42', 'H5游戏地址');
INSERT INTO `gb_box_field_mappings` VALUES (1310, 3, 'game', 'id', 'platform_game_id', 'string', 'platform_data', NULL, NULL, NULL, '1', 'overwrite', 100, '1', '', '2026-02-02 19:52:05', '', '2026-03-08 13:02:43', 'U2Game平台游戏ID');
INSERT INTO `gb_box_field_mappings` VALUES (1312, 3, 'game', 'firstpay', 'first_charge_domestic', 'decimal', 'relation', NULL, NULL, NULL, '0', 'overwrite', 101, '1', '', '2026-02-02 19:52:05', '', '2026-03-08 13:02:43', '首充折扣-国内');
INSERT INTO `gb_box_field_mappings` VALUES (1313, 3, 'game', 'hw_firstpay', 'first_charge_overseas', 'decimal', 'relation', NULL, NULL, NULL, '0', 'overwrite', 102, '1', '', '2026-02-02 19:52:05', '', '2026-03-08 13:02:43', '首充折扣-海外');
INSERT INTO `gb_box_field_mappings` VALUES (1314, 3, 'game', 'otherpay', 'recharge_domestic', 'decimal', 'relation', NULL, NULL, NULL, '0', 'overwrite', 103, '1', '', '2026-02-02 19:52:05', '', '2026-03-08 13:02:43', '续充折扣-国内');
INSERT INTO `gb_box_field_mappings` VALUES (1315, 3, 'game', 'hw_otherpay', 'recharge_overseas', 'decimal', 'relation', NULL, NULL, NULL, '0', 'overwrite', 104, '1', '', '2026-02-02 19:52:05', '', '2026-03-08 13:02:43', '续充折扣-海外');
INSERT INTO `gb_box_field_mappings` VALUES (1318, 3, 'game', 'qrcode', 'qrcode_base64', 'string', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 107, '1', '', '2026-02-02 19:52:05', '', '2026-03-08 13:02:43', '下载二维码(Base64)');
INSERT INTO `gb_box_field_mappings` VALUES (1320, 3, 'game', 'weburl_qr', 'weburl_qrcode_base64', 'string', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 108, '1', '', '2026-02-02 19:52:05', '', '2026-03-08 13:02:43', '网页地址二维码(Base64)');
INSERT INTO `gb_box_field_mappings` VALUES (1322, 3, 'game', 'video', 'video_url', 'string', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 109, '1', '', '2026-02-02 19:52:05', '', '2026-03-08 13:02:43', '游戏宣传视频URL');
INSERT INTO `gb_box_field_mappings` VALUES (1324, 3, 'game', 'pic_xuanchuan', 'promo_image_url', 'string', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 110, '1', '', '2026-02-02 19:52:05', '', '2026-03-08 13:02:43', '宣传图片URL');
INSERT INTO `gb_box_field_mappings` VALUES (1326, 3, 'game', 'pic3', 'screenshot1', 'string', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 111, '1', '', '2026-02-02 19:52:05', '', '2026-03-08 13:02:44', '游戏截图1');
INSERT INTO `gb_box_field_mappings` VALUES (1327, 3, 'game', 'pic4', 'screenshot2', 'string', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 112, '1', '', '2026-02-02 19:52:05', '', '2026-03-08 13:02:44', '游戏截图2');
INSERT INTO `gb_box_field_mappings` VALUES (1328, 3, 'game', 'photo', 'screenshots', 'json', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 113, '1', '', '2026-02-02 19:52:05', '', '2026-03-08 13:02:44', '游戏截图数组');
INSERT INTO `gb_box_field_mappings` VALUES (1329, 3, 'game', 'box_content', 'box_welfare_content', 'string', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 114, '1', '', '2026-02-02 19:52:05', '', '2026-03-08 13:02:44', '盒子专属福利内容');
INSERT INTO `gb_box_field_mappings` VALUES (1330, 3, 'game', 'copy', 'copy_text', 'string', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 115, '1', '', '2026-02-02 19:52:05', '', '2026-03-08 13:02:44', '完整复制文案');
INSERT INTO `gb_box_field_mappings` VALUES (1331, 3, 'game', 'game_source', 'game_source', 'string', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 116, '1', '', '2026-02-02 19:52:05', '', '2026-03-08 13:02:44', '游戏来源');
INSERT INTO `gb_box_field_mappings` VALUES (1332, 3, 'game', 'version', 'version', 'int', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 117, '1', '', '2026-02-02 19:52:05', '', '2026-03-08 13:02:44', '版本号');
INSERT INTO `gb_box_field_mappings` VALUES (1333, 3, 'game', 'c_version', 'c_version', 'int', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 118, '1', '', '2026-02-02 19:52:05', '', '2026-03-08 13:02:44', 'C版本号');
INSERT INTO `gb_box_field_mappings` VALUES (1334, 3, 'game', 'h5', 'is_h5', 'boolean', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 120, '1', '', '2026-02-02 19:52:05', '', '2026-03-08 13:02:44', '是否H5游戏');
INSERT INTO `gb_box_field_mappings` VALUES (1335, 3, 'game', 'new', 'is_new', 'boolean', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 121, '1', '', '2026-02-02 19:52:05', '', '2026-03-08 13:02:44', '是否新游');
INSERT INTO `gb_box_field_mappings` VALUES (1336, 3, 'game', 'yuyue', 'is_reservation', 'boolean', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 122, '1', '', '2026-02-02 19:52:05', '', '2026-03-08 13:02:45', '是否预约游戏');
INSERT INTO `gb_box_field_mappings` VALUES (1337, 3, 'game', 'interchangeable_role', 'interchangeable_role', 'boolean', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 123, '1', '', '2026-02-02 19:52:05', '', '2026-03-08 13:02:45', '是否可互换角色');
INSERT INTO `gb_box_field_mappings` VALUES (1338, 3, 'game', 'strict', 'is_strict', 'boolean', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 124, '1', '', '2026-02-02 19:52:05', '', '2026-03-08 13:02:45', '是否严格审核');
INSERT INTO `gb_box_field_mappings` VALUES (1339, 3, 'game', 'userfc', 'user_fc', 'boolean', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 125, '1', '', '2026-02-02 19:52:05', '', '2026-03-08 13:02:45', '是否用户FC');
INSERT INTO `gb_box_field_mappings` VALUES (1340, 3, 'game', 'addtime', 'add_time', 'string', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 126, '1', '', '2026-02-02 19:52:05', '', '2026-03-08 13:02:45', '添加时间');
INSERT INTO `gb_box_field_mappings` VALUES (1341, 3, 'game', 'updatetime', 'update_time', 'string', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 127, '1', '', '2026-02-02 19:52:05', '', '2026-03-08 13:02:45', '更新时间');
INSERT INTO `gb_box_field_mappings` VALUES (1342, 3, 'game', 'text_cps', 'cps_text', 'string', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 128, '1', '', '2026-02-02 19:52:05', '', '2026-03-08 13:02:45', 'CPS推广文本说明');
INSERT INTO `gb_box_field_mappings` VALUES (1343, 3, 'game', 'cps_cpa', 'cps_cpa_info', 'string', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 129, '1', '', '2026-02-02 19:52:05', '', '2026-03-08 13:02:45', 'CPS/CPA推广信息');
INSERT INTO `gb_box_field_mappings` VALUES (1344, 3, 'game', 'hw_cps_cpa', 'hw_cps_cpa_info', 'string', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 130, '1', '', '2026-02-02 19:52:05', '', '2026-03-08 13:02:45', '海外CPS/CPA推广信息');
INSERT INTO `gb_box_field_mappings` VALUES (1345, 3, 'game', 'gn_extension', 'gn_extension', 'boolean', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 131, '1', '', '2026-02-02 19:52:05', '', '2026-03-08 13:02:46', '是否国内扩展');
INSERT INTO `gb_box_field_mappings` VALUES (1346, 3, 'game', 'hw_extension', 'hw_extension', 'boolean', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 132, '1', '', '2026-02-02 19:52:05', '', '2026-03-08 13:02:46', '是否海外扩展');
INSERT INTO `gb_box_field_mappings` VALUES (1347, 3, 'game', 'coupon_repeat', 'coupon_repeat', 'string', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 133, '1', '', '2026-02-02 19:52:05', '', '2026-03-08 13:02:46', '优惠券重复说明');
INSERT INTO `gb_box_field_mappings` VALUES (1348, 3, 'game', 'cps_ratio', 'cps_ratio', 'decimal', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 134, '1', '', '2026-02-02 19:52:05', '', '2026-03-08 13:02:46', 'CPS比例');
INSERT INTO `gb_box_field_mappings` VALUES (1367, 3, 'game', 'gamename', 'name', 'string', 'main', 'regex:^([^（(]+)', NULL, NULL, '0', 'overwrite', 0, '1', '', '2026-02-04 09:40:29', '', '2026-03-11 11:17:15', '游戏名称');
INSERT INTO `gb_box_field_mappings` VALUES (1370, 3, 'game', 'gametype', 'category_id', 'integer', 'category_relation', NULL, '{\"type\":\"direct\",\"mappings\":{\"仙侠\":{\"value\":\"81\",\"description\":\"仙侠修真类游戏\"},\"魔幻\":{\"value\":\"82\",\"description\":\"魔幻题材游戏\"},\"二次元\":{\"value\":\"79\",\"description\":\"二次元动漫风格游戏\"},\"武侠\":{\"value\":\"80\",\"description\":\"武侠江湖类游戏\"},\"三国\":{\"value\":\"85\",\"description\":\"三国题材游戏\"},\"西游\":{\"value\":\"77\",\"description\":\"西游记题材游戏\"},\"卡牌\":{\"value\":\"84\",\"description\":\"卡牌策略游戏\"},\"策略\":{\"value\":\"83\",\"description\":\"策略战争游戏\"},\"挂机\":{\"value\":\"78\",\"description\":\"放置挂机游戏\"},\"经营\":{\"value\":\"65\",\"description\":\"模拟经营类游戏\"},\"养成\":{\"value\":\"66\",\"description\":\"角色养成类游戏\"},\"冒险\":{\"value\":\"67\",\"description\":\"冒险探索类游戏\"},\"玄幻\":{\"value\":\"68\",\"description\":\"玄幻奇幻类游戏\"},\"科幻\":{\"value\":\"69\",\"description\":\"科幻未来类游戏\"},\"割草\":{\"value\":\"70\",\"description\":\"割草动作类游戏\"},\"角色扮演\":{\"value\":\"71\",\"description\":\"角色扮演类游戏\"},\"官方\":{\"value\":\"72\",\"description\":\"官方正版游戏\"},\"休闲\":{\"value\":\"73\",\"description\":\"休闲娱乐类游戏\"},\"动漫\":{\"value\":\"74\",\"description\":\"动漫IP改编游戏\"},\"放置\":{\"value\":\"75\",\"description\":\"放置挂机类游戏\"},\"回合\":{\"value\":\"76\",\"description\":\"回合制游戏\"}}}', NULL, '0', 'merge', 0, '1', '', '2026-02-04 09:40:30', '', '2026-03-25 04:05:38', '映射分类数据（需要值映射）');
INSERT INTO `gb_box_field_mappings` VALUES (1371, 3, 'game', 'edition', 'game_type', 'string', 'main', NULL, '{\"type\":\"direct\",\"mappings\":{\"0\":{\"value\":\"官方\",\"description\":\"官方\"},\"1\":{\"value\":\"折扣\",\"description\":\"折扣\"},\"2\":{\"value\":\"BT\",\"description\":\"变态\"}}}', NULL, '0', 'fill_empty', 0, '1', '', '2026-02-04 09:40:30', '', '2026-03-22 14:41:44', '需要值映射(配置官方、折扣、bt、coming)');
INSERT INTO `gb_box_field_mappings` VALUES (1372, 3, 'game', 'device_type', 'device_support', 'string', 'main', NULL, '{\"type\":\"direct\",\"mappings\":{\"0\":\"both\",\"1\":\"android\",\"2\":\"ios\"}}', NULL, '0', 'overwrite', 0, '1', '', '2026-02-04 09:40:30', '', '2026-03-08 13:02:46', 'device_type 映射到 game_type(需要配置值映射)');
INSERT INTO `gb_box_field_mappings` VALUES (1376, 3, 'game', 'gamename', 'subtitle', 'string', 'main', 'regex:[（(]([^）)]+)[）)]', NULL, NULL, '0', 'overwrite', 0, '1', '', '2026-02-04 09:47:03', '', '2026-03-11 11:25:54', '可以值映射括号里面的');
INSERT INTO `gb_box_field_mappings` VALUES (1377, 3, 'game', 'gamename', 'short_name', 'string', 'main', NULL, NULL, NULL, '0', 'overwrite', 0, '1', '', '2026-02-04 09:47:03', '', '2026-03-08 13:02:47', '可以值映射非括号部分');
INSERT INTO `gb_box_field_mappings` VALUES (1378, 3, 'game', 'pic1', 'icon_url', 'string', 'main', NULL, NULL, NULL, '0', 'overwrite', 0, '1', '', '2026-02-04 09:59:56', '', '2026-03-08 13:02:47', '游戏图标');
INSERT INTO `gb_box_field_mappings` VALUES (1379, 3, 'game', 'pic2', 'cover_url', 'string', 'main', NULL, NULL, NULL, '0', 'overwrite', 0, '1', '', '2026-02-04 09:59:56', '', '2026-03-08 13:02:47', '游戏封面');
INSERT INTO `gb_box_field_mappings` VALUES (1380, 3, 'game', 'pic4', 'banner_url', 'string', 'main', NULL, NULL, NULL, '0', 'overwrite', 0, '1', '', '2026-02-04 09:59:56', '', '2026-03-08 13:02:47', '游戏横幅');
INSERT INTO `gb_box_field_mappings` VALUES (1381, 3, 'game', 'video', 'video_url', 'string', 'main', NULL, NULL, NULL, '0', 'overwrite', 0, '1', '', '2026-02-04 09:59:57', '', '2026-03-08 13:02:47', '游戏推广介绍视频');
INSERT INTO `gb_box_field_mappings` VALUES (1382, 3, 'game', 'photo[].url', 'screenshots', 'json', 'main', NULL, NULL, NULL, '0', 'overwrite', 0, '1', '', '2026-02-04 09:59:57', '', '2026-03-08 13:02:47', '游戏截图列表');
INSERT INTO `gb_box_field_mappings` VALUES (1383, 3, 'game', 'excerpt', 'description', 'string', 'main', NULL, NULL, NULL, '0', 'overwrite', 0, '1', '', '2026-02-04 09:59:57', '', '2026-03-08 13:02:47', '游戏描述');
INSERT INTO `gb_box_field_mappings` VALUES (1384, 3, 'game', 'text_cps', 'promotion_desc', 'string', 'main', NULL, NULL, NULL, '0', 'overwrite', 0, '1', '', '2026-02-04 09:59:57', '', '2026-03-08 13:02:47', '推广说明');
INSERT INTO `gb_box_field_mappings` VALUES (1385, 3, 'game', 'updatetime', 'launch_time', 'datetime', 'main', NULL, NULL, NULL, '0', 'overwrite', 0, '1', '', '2026-02-04 18:56:01', '', '2026-03-08 13:02:47', '');
INSERT INTO `gb_box_field_mappings` VALUES (1386, 3, 'game', 'downurlmash', 'download_url', 'string', 'main', NULL, NULL, NULL, '0', 'overwrite', 0, '1', '', '2026-02-04 19:15:00', '', '2026-03-08 13:02:48', 'downurlmash 映射到 download_url');
INSERT INTO `gb_box_field_mappings` VALUES (1387, 3, 'game', 'downurlmash', 'download_url', 'string', 'relation', NULL, NULL, NULL, '0', 'overwrite', 0, '1', '', '2026-02-04 19:15:00', '', '2026-03-08 13:02:48', 'downurlmash 映射到 download_url');
INSERT INTO `gb_box_field_mappings` VALUES (1388, 3, 'game', 'welfare', 'discount_label', 'string', 'main', NULL, NULL, NULL, '0', 'overwrite', 0, '1', '', '2026-02-04 19:15:00', '', '2026-03-08 13:02:48', '福利说明');
INSERT INTO `gb_box_field_mappings` VALUES (1389, 3, 'game', 'firstpay', 'first_charge_domestic', 'decimal', 'main', NULL, NULL, NULL, '0', 'overwrite', 0, '1', '', '2026-02-04 19:15:00', '', '2026-03-08 13:02:48', '');
INSERT INTO `gb_box_field_mappings` VALUES (1390, 3, 'game', 'hw_firstpay', 'first_charge_overseas', 'decimal', 'main', NULL, NULL, NULL, '0', 'overwrite', 0, '1', '', '2026-02-04 19:15:00', '', '2026-03-08 13:02:48', '');
INSERT INTO `gb_box_field_mappings` VALUES (1391, 3, 'game', 'otherpay', 'recharge_domestic', 'decimal', 'main', NULL, NULL, NULL, '0', 'overwrite', 0, '1', '', '2026-02-04 19:15:01', '', '2026-03-08 13:02:48', '');
INSERT INTO `gb_box_field_mappings` VALUES (1392, 3, 'game', 'hw_otherpay', 'recharge_overseas', 'decimal', 'main', NULL, NULL, NULL, '0', 'overwrite', 0, '1', '', '2026-02-04 19:15:01', '', '2026-03-08 13:02:48', '');
INSERT INTO `gb_box_field_mappings` VALUES (1393, 3, 'game', 'copy', 'tags', 'string', 'main', NULL, NULL, NULL, '0', 'overwrite', 0, '1', '', '2026-02-04 21:34:04', '', '2026-03-08 13:02:48', '用于存储copy文案，其中有可复制的游戏下载链接');
INSERT INTO `gb_box_field_mappings` VALUES (1534, 7, 'game', 'gamename', 'name', 'string', 'main', 'regex_replace:（[^）]*）|\\([^)]*\\)=>', NULL, NULL, '1', 'overwrite', 1, '1', '', '2026-03-09 00:02:57', '', '2026-03-21 14:27:52', '游戏名称（含括号内福利说明）');
INSERT INTO `gb_box_field_mappings` VALUES (1535, 7, 'game', 'gamename', 'subtitle', 'string', 'main', 'regex:[（(]([^）)]+)[）)]', NULL, NULL, '0', 'overwrite', 2, '1', '', '2026-03-09 00:02:57', '', '2026-03-11 11:26:57', '副标题（同gamename，可用transform提取括号内的福利文字）');
INSERT INTO `gb_box_field_mappings` VALUES (1536, 7, 'game', 'gamename', 'short_name', 'string', 'main', NULL, NULL, NULL, '0', 'overwrite', 3, '1', '', '2026-03-09 00:02:57', '', '2026-03-09 00:02:57', '游戏短名（可用transform提取括号前的游戏名）');
INSERT INTO `gb_box_field_mappings` VALUES (1537, 7, 'game', 'game_intro', 'discount_label', 'string', 'main', NULL, NULL, NULL, '0', 'overwrite', 4, '1', '', '2026-03-09 00:02:57', '', '2026-03-09 00:02:57', '游戏简介（用作折扣/福利标签展示）');
INSERT INTO `gb_box_field_mappings` VALUES (1538, 7, 'game', 'gameicon', 'icon_url', 'string', 'main', NULL, NULL, NULL, '1', 'overwrite', 5, '1', '', '2026-03-09 00:02:57', '', '2026-03-09 00:02:57', '游戏图标URL');
INSERT INTO `gb_box_field_mappings` VALUES (1539, 7, 'game', 'banner_pic', 'cover_url', 'string', 'main', NULL, NULL, NULL, '0', 'overwrite', 6, '1', '', '2026-03-09 00:02:57', '', '2026-03-09 00:02:57', '游戏封面图URL（横幅/banner）');
INSERT INTO `gb_box_field_mappings` VALUES (1540, 7, 'game', 'banner_pic', 'banner_url', 'string', 'main', NULL, NULL, NULL, '0', 'overwrite', 7, '1', '', '2026-03-09 00:02:57', '', '2026-03-09 00:02:57', '游戏横幅URL（banner）');
INSERT INTO `gb_box_field_mappings` VALUES (1541, 7, 'game', 'gamedes', 'description', 'string', 'main', NULL, NULL, NULL, '0', 'overwrite', 8, '1', '', '2026-03-09 00:02:58', '', '2026-03-09 00:02:58', '游戏详情描述');
INSERT INTO `gb_box_field_mappings` VALUES (1542, 7, 'game', 'genre_name', 'category_id', 'int', 'category_relation', 'regex_all:[^|]+', '{\"type\":\"direct\",\"mappings\":{\"仙侠\":{\"value\":\"81\",\"description\":\"仙侠修真类游戏\"},\"魔幻\":{\"value\":\"82\",\"description\":\"魔幻题材游戏\"},\"二次元\":{\"value\":\"79\",\"description\":\"二次元动漫风格游戏\"},\"武侠\":{\"value\":\"80\",\"description\":\"武侠江湖类游戏\"},\"三国\":{\"value\":\"85\",\"description\":\"三国题材游戏\"},\"西游\":{\"value\":\"77\",\"description\":\"西游记题材游戏\"},\"卡牌\":{\"value\":\"84\",\"description\":\"卡牌策略游戏\"},\"策略\":{\"value\":\"83\",\"description\":\"策略战争游戏\"},\"挂机\":{\"value\":\"78\",\"description\":\"放置挂机游戏\"},\"经营\":{\"value\":\"65\",\"description\":\"模拟经营类游戏\"},\"养成\":{\"value\":\"66\",\"description\":\"角色养成类游戏\"},\"冒险\":{\"value\":\"67\",\"description\":\"冒险探索类游戏\"},\"玄幻\":{\"value\":\"68\",\"description\":\"玄幻奇幻类游戏\"},\"科幻\":{\"value\":\"69\",\"description\":\"科幻未来类游戏\"},\"割草\":{\"value\":\"70\",\"description\":\"割草动作类游戏\"},\"角色扮演\":{\"value\":\"71\",\"description\":\"角色扮演类游戏\"},\"官方\":{\"value\":\"72\",\"description\":\"官方正版游戏\"},\"休闲\":{\"value\":\"73\",\"description\":\"休闲娱乐类游戏\"},\"动漫\":{\"value\":\"74\",\"description\":\"动漫IP改编游戏\"},\"放置\":{\"value\":\"75\",\"description\":\"放置挂机类游戏\"},\"回合\":{\"value\":\"76\",\"description\":\"回合制游戏\"}}}', NULL, '0', 'overwrite', 9, '1', '', '2026-03-09 00:02:58', '', '2026-03-11 14:35:01', '游戏分类（竖线分隔，split_all拆分后值映射）');
INSERT INTO `gb_box_field_mappings` VALUES (1543, 7, 'game', 'game_type', 'game_type', 'string', 'main', NULL, '{\"type\":\"direct\",\"mappings\":{\"1\":{\"value\":\"折扣\",\"description\":\"折扣游戏\"},\"2\":{\"value\":\"官方\",\"description\":\"变态服/极折服\"},\"3\":{\"value\":\"H5\",\"description\":\"H5游戏\"}}}', NULL, '0', 'overwrite', 10, '1', '', '2026-03-09 00:02:58', '', '2026-03-22 14:41:30', '是否变态游戏（0=变态服bt，1=正常服discount）');
INSERT INTO `gb_box_field_mappings` VALUES (1544, 7, 'game', 'client_type', 'device_support', 'string', 'main', NULL, '{\"type\":\"direct\",\"mappings\":{\"1\":\"android\",\"2\":\"ios\",\"3\":\"both\"}}', NULL, '0', 'overwrite', 11, '1', '', '2026-03-09 00:02:58', '', '2026-03-09 00:02:58', '设备支持类型（1=安卓，2=iOS，3=双端）');
INSERT INTO `gb_box_field_mappings` VALUES (1545, 7, 'game', 'discount', 'first_charge_domestic', 'decimal', 'main', NULL, NULL, NULL, '0', 'overwrite', 12, '1', '', '2026-03-09 00:02:59', '', '2026-03-09 00:02:59', '首充折扣-国内（单位：折，10=原价）');
INSERT INTO `gb_box_field_mappings` VALUES (1546, 7, 'game', 'hw_discount', 'first_charge_overseas', 'decimal', 'main', NULL, NULL, NULL, '0', 'overwrite', 13, '1', '', '2026-03-09 00:02:59', '', '2026-03-09 00:02:59', '首充折扣-海外（单位：折，0为不支持海外）');
INSERT INTO `gb_box_field_mappings` VALUES (1547, 7, 'game', 'video_url', 'video_url', 'string', 'main', NULL, NULL, NULL, '0', 'overwrite', 14, '1', '', '2026-03-09 00:02:59', '', '2026-03-09 00:02:59', '游戏宣传视频URL');
INSERT INTO `gb_box_field_mappings` VALUES (1548, 7, 'game', 'apk_url', 'download_url', 'string', 'main', NULL, NULL, NULL, '0', 'overwrite', 15, '1', '', '2026-03-09 00:02:59', '', '2026-03-09 00:02:59', '游戏下载/进入链接（安卓/H5）');
INSERT INTO `gb_box_field_mappings` VALUES (1549, 7, 'game', 'game_online_time', 'launch_time', 'datetime', 'main', NULL, NULL, NULL, '0', 'overwrite', 16, '1', '', '2026-03-09 00:02:59', '', '2026-03-09 00:02:59', '游戏上线时间（Unix时间戳秒，需配置FROM_UNIXTIME转换）');
INSERT INTO `gb_box_field_mappings` VALUES (1550, 7, 'game', 'remarks2', 'promotion_desc', 'string', 'main', NULL, NULL, NULL, '0', 'overwrite', 17, '1', '', '2026-03-09 00:02:59', '', '2026-03-09 00:02:59', '推广说明（如\"禁海外推广\"）');
INSERT INTO `gb_box_field_mappings` VALUES (1551, 7, 'game', 'apk_url', 'download_url', 'string', 'promotion_link', NULL, NULL, NULL, '1', 'overwrite', 50, '1', '', '2026-03-09 00:02:59', '', '2026-03-09 00:02:59', '安卓下载/H5进入链接');
INSERT INTO `gb_box_field_mappings` VALUES (1552, 7, 'game', 'ios_url', 'ios_url', 'string', 'promotion_link', NULL, NULL, NULL, '0', 'overwrite', 51, '1', '', '2026-03-09 00:02:59', '', '2026-03-09 00:02:59', 'iOS下载/进入链接');
INSERT INTO `gb_box_field_mappings` VALUES (1553, 7, 'game', 'gameinfo_url', 'web_url', 'string', 'promotion_link', NULL, NULL, NULL, '0', 'overwrite', 52, '1', '', '2026-03-09 00:03:00', '', '2026-03-09 00:03:00', '游戏详情推广页地址');
INSERT INTO `gb_box_field_mappings` VALUES (1554, 7, 'game', 'gameinfo_url2', 'alt_web_url', 'string', 'promotion_link', NULL, NULL, NULL, '0', 'overwrite', 53, '1', '', '2026-03-09 00:03:00', '', '2026-03-09 00:03:00', '游戏详情推广页备用地址');
INSERT INTO `gb_box_field_mappings` VALUES (1555, 7, 'game', 'discount', 'first_charge_domestic', 'decimal', 'relation', NULL, NULL, NULL, '0', 'overwrite', 101, '1', '', '2026-03-09 00:03:00', '', '2026-03-09 00:03:00', '关联表-首充折扣国内');
INSERT INTO `gb_box_field_mappings` VALUES (1556, 7, 'game', 'hw_discount', 'first_charge_overseas', 'decimal', 'relation', NULL, NULL, NULL, '0', 'overwrite', 102, '1', '', '2026-03-09 00:03:00', '', '2026-03-09 00:03:00', '关联表-首充折扣海外');
INSERT INTO `gb_box_field_mappings` VALUES (1557, 7, 'game', 'gameid', 'platform_game_id', 'string', 'platform_data', NULL, NULL, NULL, '1', 'overwrite', 200, '1', '', '2026-03-09 00:03:00', '', '2026-03-09 00:03:00', '277sy平台游戏ID');
INSERT INTO `gb_box_field_mappings` VALUES (1558, 7, 'game', '[\"screenshot1\",\"screenshot2\",\"screenshot3\",\"screenshot4\",\"screenshot5\"]', 'screenshots', 'json', 'main', NULL, NULL, NULL, '0', 'overwrite', 19, '1', '', '2026-03-09 00:03:00', '', '2026-03-09 00:03:00', '游戏截图数组（多字段合并，screenshot1~5）');
INSERT INTO `gb_box_field_mappings` VALUES (1559, 7, 'game', 'video_pic', 'video_thumbnail', 'string', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 206, '1', '', '2026-03-09 00:03:00', '', '2026-03-09 00:03:00', '视频封面/预览图');
INSERT INTO `gb_box_field_mappings` VALUES (1560, 7, 'game', 'gameshort', 'short_code', 'string', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 207, '1', '', '2026-03-09 00:03:00', '', '2026-03-09 00:03:00', '游戏短标识码');
INSERT INTO `gb_box_field_mappings` VALUES (1561, 7, 'game', 'remarks2', 'promotion_remark', 'string', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 208, '1', '', '2026-03-09 00:03:00', '', '2026-03-09 00:03:00', '推广备注（如禁海外）');
INSERT INTO `gb_box_field_mappings` VALUES (1562, 7, 'game', 'cloud_game', 'is_cloud_game', 'boolean', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 209, '1', '', '2026-03-09 00:03:01', '', '2026-03-09 00:03:01', '是否云游戏');
INSERT INTO `gb_box_field_mappings` VALUES (1563, 7, 'game', 'role_interflow', 'role_interflow', 'boolean', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 210, '1', '', '2026-03-09 00:03:01', '', '2026-03-09 00:03:01', '是否支持角色互通');
INSERT INTO `gb_box_field_mappings` VALUES (1564, 7, 'game', 'is_notbtgame', 'is_notbtgame', 'boolean', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 211, '1', '', '2026-03-09 00:03:01', '', '2026-03-09 00:03:01', '是否非变态游戏（0=变态/BT服）');
INSERT INTO `gb_box_field_mappings` VALUES (1565, 7, 'game', 'game_cloud_code_4', 'cloud_code', 'string', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 212, '1', '', '2026-03-09 00:03:01', '', '2026-03-09 00:03:01', '云游戏专属code（game_cloud_code_4）');
INSERT INTO `gb_box_field_mappings` VALUES (1566, 7, 'game', 'flash_discount', 'flash_discount', 'decimal', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 213, '1', '', '2026-03-09 00:03:01', '', '2026-03-09 00:03:01', '限时折扣（单位：折）');
INSERT INTO `gb_box_field_mappings` VALUES (1567, 7, 'game', 'flash_rate', 'flash_rate', 'decimal', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 214, '1', '', '2026-03-09 00:03:01', '', '2026-03-09 00:03:01', '限时活动返利比例');
INSERT INTO `gb_box_field_mappings` VALUES (1568, 7, 'game', 'flash_wx_rate', 'flash_wx_rate', 'decimal', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 215, '1', '', '2026-03-09 00:03:01', '', '2026-03-09 00:03:01', '限时活动微信返利比例');
INSERT INTO `gb_box_field_mappings` VALUES (1569, 7, 'game', 'rate', 'cps_ratio', 'decimal', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 216, '1', '', '2026-03-09 00:03:01', '', '2026-03-09 00:03:01', 'CPS推广返利比例（含所有渠道）');
INSERT INTO `gb_box_field_mappings` VALUES (1570, 7, 'game', 'rate_wx', 'cps_ratio_wx', 'decimal', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 217, '1', '', '2026-03-09 00:03:01', '', '2026-03-09 00:03:01', 'CPS推广微信返利比例');
INSERT INTO `gb_box_field_mappings` VALUES (1571, 7, 'game', 'rate_1', 'rate_lv1', 'decimal', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 218, '1', '', '2026-03-09 00:03:01', '', '2026-03-09 00:03:01', '返利比例-档位1');
INSERT INTO `gb_box_field_mappings` VALUES (1572, 7, 'game', 'rate_2', 'rate_lv2', 'decimal', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 219, '1', '', '2026-03-09 00:03:02', '', '2026-03-09 00:03:02', '返利比例-档位2');
INSERT INTO `gb_box_field_mappings` VALUES (1573, 7, 'game', 'rate_3', 'rate_lv3', 'decimal', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 220, '1', '', '2026-03-09 00:03:02', '', '2026-03-09 00:03:02', '返利比例-档位3');
INSERT INTO `gb_box_field_mappings` VALUES (1574, 7, 'game', 'rate_4', 'rate_lv4', 'decimal', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 221, '1', '', '2026-03-09 00:03:02', '', '2026-03-09 00:03:02', '返利比例-档位4');
INSERT INTO `gb_box_field_mappings` VALUES (1575, 7, 'game', 'rate_wx_1', 'rate_wx_lv1', 'decimal', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 222, '1', '', '2026-03-09 00:03:02', '', '2026-03-09 00:03:02', '微信返利比例-档位1');
INSERT INTO `gb_box_field_mappings` VALUES (1576, 7, 'game', 'rate_wx_2', 'rate_wx_lv2', 'decimal', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 223, '1', '', '2026-03-09 00:03:02', '', '2026-03-09 00:03:02', '微信返利比例-档位2');
INSERT INTO `gb_box_field_mappings` VALUES (1577, 7, 'game', 'rate_wx_3', 'rate_wx_lv3', 'decimal', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 224, '1', '', '2026-03-09 00:03:02', '', '2026-03-09 00:03:02', '微信返利比例-档位3');
INSERT INTO `gb_box_field_mappings` VALUES (1578, 7, 'game', 'rate_wx_4', 'rate_wx_lv4', 'decimal', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 225, '1', '', '2026-03-09 00:03:02', '', '2026-03-09 00:03:02', '微信返利比例-档位4');
INSERT INTO `gb_box_field_mappings` VALUES (1579, 7, 'game', 'rate_google', 'rate_google', 'decimal', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 226, '1', '', '2026-03-09 00:03:03', '', '2026-03-09 00:03:03', 'Google Play返利比例');
INSERT INTO `gb_box_field_mappings` VALUES (1580, 7, 'game', 'rate_appstore', 'rate_appstore', 'decimal', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 227, '1', '', '2026-03-09 00:03:03', '', '2026-03-09 00:03:03', 'AppStore返利比例');
INSERT INTO `gb_box_field_mappings` VALUES (1581, 7, 'game', 'rate_child', 'rate_child', 'decimal', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 228, '1', '', '2026-03-09 00:03:03', '', '2026-03-09 00:03:03', '子推广返利比例');
INSERT INTO `gb_box_field_mappings` VALUES (1582, 7, 'game', 'rate_child_wx', 'rate_child_wx', 'decimal', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 229, '1', '', '2026-03-09 00:03:03', '', '2026-03-09 00:03:03', '子推广微信返利比例');
INSERT INTO `gb_box_field_mappings` VALUES (1583, 7, 'game', 'is_index', 'is_index', 'boolean', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 230, '1', '', '2026-03-09 00:03:03', '', '2026-03-09 00:03:03', '是否在首页显示');
INSERT INTO `gb_box_field_mappings` VALUES (1584, 7, 'game', 'is_close', 'is_close', 'boolean', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 231, '1', '', '2026-03-09 00:03:03', '', '2026-03-09 00:03:03', '是否已关闭/下架');
INSERT INTO `gb_box_field_mappings` VALUES (1585, 7, 'game', 'show_ios_url', 'show_ios_url', 'boolean', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 232, '1', '', '2026-03-09 00:03:03', '', '2026-03-09 00:03:03', '是否显示iOS链接');
INSERT INTO `gb_box_field_mappings` VALUES (1586, 7, 'game', 'is_discount01', 'is_discount01', 'boolean', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 233, '1', '', '2026-03-09 00:03:03', '', '2026-03-09 00:03:03', '是否0.1折特殊折扣');
INSERT INTO `gb_box_field_mappings` VALUES (1587, 7, 'game', 'can_dc', 'can_dc', 'boolean', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 234, '1', '', '2026-03-09 00:03:03', '', '2026-03-09 00:03:03', '是否支持直充');
INSERT INTO `gb_box_field_mappings` VALUES (1588, 7, 'game', 'f_support', 'f_support', 'boolean', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 235, '1', '', '2026-03-09 00:03:03', '', '2026-03-09 00:03:03', 'F支持标志');
INSERT INTO `gb_box_field_mappings` VALUES (1589, 7, 'game', 'game_accelerator', 'game_accelerator', 'boolean', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 236, '1', '', '2026-03-09 00:03:04', '', '2026-03-09 00:03:04', '是否支持游戏加速');
INSERT INTO `gb_box_field_mappings` VALUES (1590, 7, 'game', 'gh_forbid', 'gh_forbid', 'boolean', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 237, '1', '', '2026-03-09 00:03:04', '', '2026-03-09 00:03:04', '是否禁止推广（gh_forbid）');
INSERT INTO `gb_box_field_mappings` VALUES (1591, 7, 'game', 'hidden_url', 'hidden_url', 'boolean', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 238, '1', '', '2026-03-09 00:03:04', '', '2026-03-09 00:03:04', '是否隐藏链接');
INSERT INTO `gb_box_field_mappings` VALUES (1592, 7, 'game', 'sort', 'platform_sort', 'int', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 240, '1', '', '2026-03-09 00:03:04', '', '2026-03-09 00:03:04', '平台排序权重');
INSERT INTO `gb_box_field_mappings` VALUES (1593, 7, 'game', 'sort_hot', 'hot_rank', 'int', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 241, '1', '', '2026-03-09 00:03:04', '', '2026-03-09 00:03:04', '热门排名权重');
INSERT INTO `gb_box_field_mappings` VALUES (1594, 7, 'game', 'game_level', 'game_level', 'int', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 242, '1', '', '2026-03-09 00:03:04', '', '2026-03-09 00:03:04', '游戏等级/评级');
INSERT INTO `gb_box_field_mappings` VALUES (1595, 7, 'game', 'genre_ids', 'genre_ids_raw', 'string', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 243, '1', '', '2026-03-09 00:03:04', '', '2026-03-09 00:03:04', '分类ID原始值（平台内部使用，逗号分隔）');
INSERT INTO `gb_box_field_mappings` VALUES (1596, 7, 'game', 'color_1', 'color_theme', 'string', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 244, '1', '', '2026-03-09 00:03:04', '', '2026-03-09 00:03:04', '游戏颜色主题（如bg-yellow）');
INSERT INTO `gb_box_field_mappings` VALUES (1597, 7, 'game', 'remarks', 'platform_remarks', 'string', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 245, '1', '', '2026-03-09 00:03:04', '', '2026-03-09 00:03:04', '平台推广备注');
INSERT INTO `gb_box_field_mappings` VALUES (1598, 7, 'game', 'tgremark', 'tg_remark', 'string', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 246, '1', '', '2026-03-09 00:03:05', '', '2026-03-09 00:03:05', 'TG推广备注');
INSERT INTO `gb_box_field_mappings` VALUES (1599, 7, 'game', 'apk_remark', 'apk_remark', 'string', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 247, '1', '', '2026-03-09 00:03:05', '', '2026-03-09 00:03:05', '安卓端备注说明');
INSERT INTO `gb_box_field_mappings` VALUES (1600, 7, 'game', 'ios_remark', 'ios_remark', 'string', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 248, '1', '', '2026-03-09 00:03:05', '', '2026-03-09 00:03:05', 'iOS端备注说明');
INSERT INTO `gb_box_field_mappings` VALUES (1601, 7, 'game', 'dc_discount', 'recharge_domestic', 'decimal', 'main', NULL, NULL, NULL, '0', 'overwrite', 18, '1', '', '2026-03-09 00:03:05', '', '2026-03-09 00:03:05', '续充折扣-国内（dc_discount，4.2=4.2折）');
INSERT INTO `gb_box_field_mappings` VALUES (1602, 7, 'game', 'dc_discount', 'recharge_domestic', 'decimal', 'relation', NULL, NULL, NULL, '0', 'overwrite', 103, '1', '', '2026-03-09 00:03:05', '', '2026-03-09 00:03:05', '关联表-续充折扣国内');
INSERT INTO `gb_box_field_mappings` VALUES (1678, 9, 'game', '游戏名', 'name', 'string', 'main', '', NULL, NULL, '1', 'overwrite', 1, '1', '', '2026-03-11 14:09:40', '', '2026-03-21 22:25:56', '游戏名称');
INSERT INTO `gb_box_field_mappings` VALUES (1679, 9, 'game', '版本', 'subtitle', 'string', 'main', NULL, NULL, NULL, '0', 'overwrite', 2, '1', '', '2026-03-11 14:09:40', '', '2026-03-13 11:26:54', '版本/福利说明（如\"天天送1000充\"，空值时无特殊福利）');
INSERT INTO `gb_box_field_mappings` VALUES (1680, 9, 'game', '版本', 'discount_label', 'string', 'main', NULL, NULL, NULL, '0', 'overwrite', 3, '1', '', '2026-03-11 14:09:41', '', '2026-03-13 11:26:54', '折扣/福利标签（同版本字段）');
INSERT INTO `gb_box_field_mappings` VALUES (1681, 9, 'game', '图片链接', 'icon_url', 'string', 'main', NULL, NULL, NULL, '1', 'overwrite', 4, '1', '', '2026-03-11 14:09:41', '', '2026-03-13 11:26:54', '游戏图标URL');
INSERT INTO `gb_box_field_mappings` VALUES (1682, 9, 'game', '游戏分类', 'category_id', 'int', 'category_relation', 'regex_all:[^.]+', '{\"type\":\"direct\",\"mappings\":{\"角色\":{\"value\":\"71\",\"description\":\"角色扮演类\"},\"角色扮演\":{\"value\":\"71\",\"description\":\"角色扮演\"},\"武侠\":{\"value\":\"80\",\"description\":\"武侠江湖\"},\"仙侠\":{\"value\":\"81\",\"description\":\"仙侠修真\"},\"玄幻\":{\"value\":\"68\",\"description\":\"玄幻奇幻\"},\"魔幻\":{\"value\":\"82\",\"description\":\"魔幻题材\"},\"三国\":{\"value\":\"85\",\"description\":\"三国题材\"},\"西游\":{\"value\":\"77\",\"description\":\"西游题材\"},\"二次元\":{\"value\":\"79\",\"description\":\"二次元动漫\"},\"传奇\":{\"value\":\"80\",\"description\":\"传奇武侠（归入武侠）\"},\"卡牌\":{\"value\":\"84\",\"description\":\"卡牌策略\"},\"策略\":{\"value\":\"83\",\"description\":\"策略战争\"},\"挂机\":{\"value\":\"78\",\"description\":\"放置挂机\"},\"放置\":{\"value\":\"75\",\"description\":\"放置挂机类\"},\"回合\":{\"value\":\"76\",\"description\":\"回合制\"},\"割草\":{\"value\":\"70\",\"description\":\"割草动作\"},\"动漫\":{\"value\":\"74\",\"description\":\"动漫IP\"},\"动作\":{\"value\":\"67\",\"description\":\"动作类（归入冒险）\"},\"休闲\":{\"value\":\"73\",\"description\":\"休闲娱乐\"},\"冒险\":{\"value\":\"67\",\"description\":\"冒险探索\"},\"养成\":{\"value\":\"66\",\"description\":\"角色养成\"},\"经营\":{\"value\":\"65\",\"description\":\"模拟经营\"},\"科幻\":{\"value\":\"69\",\"description\":\"科幻未来\"},\"开箱\":{\"value\":\"84\",\"description\":\"开箱抽卡（归入卡牌）\"}}}', NULL, '0', 'overwrite', 5, '1', '', '2026-03-11 14:09:41', '', '2026-03-13 11:26:54', '游戏分类（英文点分隔，regex_all提取后值映射）');
INSERT INTO `gb_box_field_mappings` VALUES (1683, 9, 'game', '折扣', 'first_charge_domestic', 'decimal', 'main', NULL, NULL, NULL, '0', 'overwrite', 6, '1', '', '2026-03-11 14:09:41', '', '2026-03-13 11:26:54', '游戏折扣（单位：折，10=原价）');
INSERT INTO `gb_box_field_mappings` VALUES (1684, 9, 'game', '客户端', 'device_support', 'string', 'main', NULL, '{\"type\":\"direct\",\"mappings\":{\"Android/IOS\":\"both\",\"Android/iOS\":\"both\",\"Android\":\"android\",\"IOS\":\"ios\",\"iOS\":\"ios\",\"安卓/苹果\":\"both\",\"安卓\":\"android\",\"苹果\":\"ios\"}}', NULL, '0', 'overwrite', 7, '1', '', '2026-03-11 14:09:41', '', '2026-03-13 11:26:54', '支持平台（Android/IOS→both，Android→android，IOS→ios）');
INSERT INTO `gb_box_field_mappings` VALUES (1685, 9, 'game', '推广地址', 'download_url', 'string', 'main', NULL, NULL, NULL, '0', 'overwrite', 8, '1', '', '2026-03-11 14:09:41', '', '2026-03-13 11:26:54', '推广落地页地址（用作主链接）');
INSERT INTO `gb_box_field_mappings` VALUES (1686, 9, 'game', '安卓端链接', 'download_url', 'string', 'promotion_link', NULL, NULL, NULL, '1', 'overwrite', 50, '1', '', '2026-03-11 14:09:41', '', '2026-03-13 11:26:54', '安卓下载/进入链接');
INSERT INTO `gb_box_field_mappings` VALUES (1687, 9, 'game', 'IOS端链接', 'ios_url', 'string', 'promotion_link', NULL, NULL, NULL, '0', 'overwrite', 51, '1', '', '2026-03-11 14:09:42', '', '2026-03-13 11:26:54', 'iOS下载/进入链接');
INSERT INTO `gb_box_field_mappings` VALUES (1688, 9, 'game', '推广地址', 'web_url', 'string', 'promotion_link', NULL, NULL, NULL, '0', 'overwrite', 52, '1', '', '2026-03-11 14:09:42', '', '2026-03-13 11:26:54', '推广落地页地址（gweb.tsyule.cn）');
INSERT INTO `gb_box_field_mappings` VALUES (1689, 9, 'game', '安卓端链接2', 'android_url_alt', 'string', 'promotion_link', NULL, NULL, NULL, '0', 'overwrite', 53, '1', '', '2026-03-11 14:09:42', '', '2026-03-13 11:26:54', '备用安卓链接（chaoai168.com）');
INSERT INTO `gb_box_field_mappings` VALUES (1690, 9, 'game', 'IOS端链接2', 'ios_url_alt', 'string', 'promotion_link', NULL, NULL, NULL, '0', 'overwrite', 54, '1', '', '2026-03-11 14:09:42', '', '2026-03-13 11:26:54', '备用iOS链接（chaoai168.com）');
INSERT INTO `gb_box_field_mappings` VALUES (1691, 9, 'game', '推广地址2', 'web_url_alt', 'string', 'promotion_link', NULL, NULL, NULL, '0', 'overwrite', 55, '1', '', '2026-03-11 14:09:42', '', '2026-03-13 11:26:54', '备用推广落地页（gweb.chaoai168.com）');
INSERT INTO `gb_box_field_mappings` VALUES (1692, 9, 'game', '折扣', 'first_charge_domestic', 'decimal', 'relation', NULL, NULL, NULL, '0', 'overwrite', 101, '1', '', '2026-03-11 14:09:42', '', '2026-03-13 11:26:54', '关联表-首充折扣国内');
INSERT INTO `gb_box_field_mappings` VALUES (1693, 9, 'game', '点位', 'commission_rate', 'string', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 200, '1', '', '2026-03-11 14:09:42', '', '2026-03-13 11:26:54', '推广佣金点位比例（如\"0%\"）');
INSERT INTO `gb_box_field_mappings` VALUES (1694, 9, 'game', '游戏分类', 'genre_raw', 'string', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 201, '1', '', '2026-03-11 14:09:42', '', '2026-03-13 11:26:54', '游戏分类原始值（点分隔，如\"角色.传奇.动作\"）');
INSERT INTO `gb_box_field_mappings` VALUES (1695, 9, 'game', '版本', 'version_raw', 'string', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 202, '1', '', '2026-03-11 14:09:43', '', '2026-03-13 11:26:54', '版本/福利原始值');
INSERT INTO `gb_box_field_mappings` VALUES (1696, 9, 'game', 'gameid', 'platform_game_id', 'string', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 203, '1', '', '2026-03-11 14:09:43', '', '2026-03-13 11:26:54', '平台游戏ID（如\"15963\"）');
INSERT INTO `gb_box_field_mappings` VALUES (1697, 9, 'game', '手游类型', 'game_type', 'string', 'main', NULL, '{\"type\":\"direct\",\"mappings\":{\"官方手游\":\"官方\",\"H5游戏\":\"H5\",\"0.1折扣服\":\"折扣\",\"BT手游\":\"BT\"}}', NULL, '0', 'overwrite', 9, '1', '', '2026-03-11 14:09:43', '', '2026-03-22 14:41:19', '游戏类型（official-官方 discount-折扣 bt-BT版）');
INSERT INTO `gb_box_field_mappings` VALUES (1698, 8, 'game', 'game_name', 'name', 'string', 'main', NULL, NULL, NULL, '1', 'overwrite', 1, '1', '', '2026-03-11 14:20:45', '', '2026-03-13 11:23:23', '游戏名称');
INSERT INTO `gb_box_field_mappings` VALUES (1699, 8, 'game', 'name_remark', 'subtitle', 'string', 'main', NULL, NULL, NULL, '0', 'overwrite', 2, '1', '', '2026-03-11 14:20:45', '', '2026-03-13 11:23:23', '游戏副标题/名称备注（如\"0.1折送满星小乔\"）');
INSERT INTO `gb_box_field_mappings` VALUES (1700, 8, 'game', 'game_name', 'short_name', 'string', 'main', NULL, NULL, NULL, '0', 'overwrite', 3, '1', '', '2026-03-11 14:20:45', '', '2026-03-13 11:23:23', '游戏短名称（同game_name）');
INSERT INTO `gb_box_field_mappings` VALUES (1701, 8, 'game', 'name_remark', 'discount_label', 'string', 'main', NULL, NULL, NULL, '0', 'overwrite', 4, '1', '', '2026-03-11 14:20:45', '', '2026-03-13 11:23:23', '折扣/福利标签（name_remark）');
INSERT INTO `gb_box_field_mappings` VALUES (1702, 8, 'game', 'game_icon', 'icon_url', 'string', 'main', NULL, NULL, NULL, '1', 'overwrite', 5, '1', '', '2026-03-11 14:20:45', '', '2026-03-13 11:23:23', '游戏图标URL');
INSERT INTO `gb_box_field_mappings` VALUES (1703, 8, 'game', 'game_genre', 'category_id', 'int', 'category_relation', NULL, '{\"type\":\"direct\",\"mappings\":{\"仙侠\":{\"value\":\"81\",\"description\":\"仙侠修真类\"},\"魔幻\":{\"value\":\"82\",\"description\":\"魔幻题材\"},\"二次元\":{\"value\":\"79\",\"description\":\"二次元动漫\"},\"武侠\":{\"value\":\"80\",\"description\":\"武侠江湖\"},\"三国\":{\"value\":\"85\",\"description\":\"三国题材\"},\"西游\":{\"value\":\"77\",\"description\":\"西游题材\"},\"卡牌\":{\"value\":\"84\",\"description\":\"卡牌策略\"},\"策略\":{\"value\":\"83\",\"description\":\"策略战争\"},\"挂机\":{\"value\":\"78\",\"description\":\"放置挂机\"},\"放置\":{\"value\":\"75\",\"description\":\"放置挂机类\"},\"回合\":{\"value\":\"76\",\"description\":\"回合制\"},\"割草\":{\"value\":\"70\",\"description\":\"割草动作\"},\"动漫\":{\"value\":\"74\",\"description\":\"动漫IP\"},\"休闲\":{\"value\":\"73\",\"description\":\"休闲娱乐\"},\"冒险\":{\"value\":\"67\",\"description\":\"冒险探索\"},\"养成\":{\"value\":\"66\",\"description\":\"角色养成\"},\"经营\":{\"value\":\"65\",\"description\":\"模拟经营\"},\"玄幻\":{\"value\":\"68\",\"description\":\"玄幻奇幻\"},\"科幻\":{\"value\":\"69\",\"description\":\"科幻未来\"},\"角色\":{\"value\":\"71\",\"description\":\"角色扮演（短名）\"},\"角色扮演\":{\"value\":\"71\",\"description\":\"角色扮演\"},\"修仙\":{\"value\":\"81\",\"description\":\"修仙仙侠类\"},\"传奇\":{\"value\":\"80\",\"description\":\"传奇武侠（归入武侠）\"},\"动作\":{\"value\":\"67\",\"description\":\"动作类（归入冒险）\"},\"开箱\":{\"value\":\"84\",\"description\":\"开箱抽卡（归入卡牌）\"}}}', NULL, '0', 'overwrite', 6, '1', '', '2026-03-11 14:20:46', '', '2026-03-13 11:23:23', '游戏分类（JSON数组[\"卡牌\",\"三国\"]，多值分别映射分类关联）');
INSERT INTO `gb_box_field_mappings` VALUES (1704, 8, 'game', 'category', 'game_type', 'string', 'main', NULL, '{\"type\":\"direct\",\"mappings\":{\"官方手游\":{\"value\":\"官方\",\"description\":\"BT变态服/极折服\"},\"折扣\":{\"value\":\"折扣\",\"description\":\"折扣游戏\"},\"免费版\":{\"value\":\"折扣\",\"description\":\"免费/官方版\"},\"官方\":{\"value\":\"官方\",\"description\":\"官方版\"},\"BT折扣游戏\":{\"value\":\"BT\",\"description\":\"BT折扣游戏\"},\"0.05折游戏\":\"折扣\",\"折扣游戏\":\"折扣\",\"0.1折游戏\":\"折扣\",\"BT游戏\":\"BT\",\"免费版游戏\":\"折扣\"}}', NULL, '0', 'overwrite', 7, '1', '', '2026-03-11 14:20:46', '', '2026-03-22 14:58:27', '游戏大类映射到游戏类型（专服→bt，折扣→discount，免费版→official）');
INSERT INTO `gb_box_field_mappings` VALUES (1705, 8, 'game', 'support_platform', 'device_support', 'string', 'main', NULL, '{\"type\":\"direct\",\"mappings\":{\"双端\":\"both\",\"安卓\":\"android\",\"苹果\":\"ios\",\"Android\":\"android\",\"iOS\":\"ios\",\"Android/iOS\":\"both\"}}', NULL, '0', 'overwrite', 8, '1', '', '2026-03-11 14:20:46', '', '2026-03-13 11:23:23', '支持平台（双端/安卓/苹果→both/android/ios）');
INSERT INTO `gb_box_field_mappings` VALUES (1706, 8, 'game', 'android_download_link', 'download_url', 'string', 'main', NULL, NULL, NULL, '0', 'overwrite', 9, '1', '', '2026-03-11 14:20:46', '', '2026-03-13 11:23:23', '安卓下载链接（主下载地址）');
INSERT INTO `gb_box_field_mappings` VALUES (1707, 8, 'game', 'release_time', 'launch_time', 'datetime', 'main', NULL, NULL, NULL, '0', 'overwrite', 10, '1', '', '2026-03-11 14:20:46', '', '2026-03-13 11:23:23', '游戏上线时间（格式\"2026-03-17 08:00\"）');
INSERT INTO `gb_box_field_mappings` VALUES (1708, 8, 'game', 'android_download_link', 'download_url', 'string', 'promotion_link', NULL, NULL, NULL, '1', 'overwrite', 50, '1', '', '2026-03-11 14:20:46', '', '2026-03-13 11:23:23', '安卓下载链接');
INSERT INTO `gb_box_field_mappings` VALUES (1709, 8, 'game', 'ios_download_link', 'ios_url', 'string', 'promotion_link', NULL, NULL, NULL, '0', 'overwrite', 51, '1', '', '2026-03-11 14:20:46', '', '2026-03-13 11:23:23', 'iOS下载链接（ios_support=true时有值）');
INSERT INTO `gb_box_field_mappings` VALUES (1710, 8, 'game', 'promotion_page_url', 'web_url', 'string', 'promotion_link', NULL, NULL, NULL, '0', 'overwrite', 52, '1', '', '2026-03-11 14:20:46', '', '2026-03-13 11:23:23', '游戏推广落地页地址');
INSERT INTO `gb_box_field_mappings` VALUES (1711, 8, 'game', 'multi_function_link', 'multi_url', 'string', 'promotion_link', NULL, NULL, NULL, '0', 'overwrite', 53, '1', '', '2026-03-11 14:20:47', '', '2026-03-13 11:23:23', '多功能链接（app.heigu.com，注册下载通用）');
INSERT INTO `gb_box_field_mappings` VALUES (1712, 8, 'game', 'discount_info', 'discount_info', 'string', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 200, '1', '', '2026-03-11 14:20:47', '', '2026-03-13 11:23:23', '折扣信息原始字符串（如\"0.1折\"）');
INSERT INTO `gb_box_field_mappings` VALUES (1713, 8, 'game', 'category', 'category_raw', 'string', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 201, '1', '', '2026-03-11 14:20:47', '', '2026-03-13 11:23:23', '游戏大类原始值（专服/折扣/免费版等）');
INSERT INTO `gb_box_field_mappings` VALUES (1714, 8, 'game', 'release_time', 'release_time', 'string', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 202, '1', '', '2026-03-11 14:20:47', '', '2026-03-13 11:23:23', '上线时间字符串');
INSERT INTO `gb_box_field_mappings` VALUES (1715, 8, 'game', 'ios_support', 'is_ios_support', 'boolean', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 203, '1', '', '2026-03-11 14:20:47', '', '2026-03-13 11:23:23', '是否支持iOS下载');
INSERT INTO `gb_box_field_mappings` VALUES (1716, 8, 'game', 'has_profession_tag', 'has_profession_tag', 'boolean', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 204, '1', '', '2026-03-11 14:20:47', '', '2026-03-13 11:23:23', '是否有职业标签');
INSERT INTO `gb_box_field_mappings` VALUES (1717, 8, 'game', 'material_package_url', 'material_package_url', 'string', 'platform_data', NULL, NULL, NULL, '0', 'overwrite', 205, '1', '', '2026-03-11 14:20:47', '', '2026-03-13 11:23:23', '推广素材包ZIP下载地址');
INSERT INTO `gb_box_field_mappings` VALUES (1718, 8, 'game', 'screenshots', 'screenshots', 'json', 'main', NULL, NULL, NULL, NULL, 'fill_empty', NULL, '1', NULL, '2026-03-13 11:21:25', '', '2026-03-13 11:23:23', 'screenshots 映射到 screenshots');
INSERT INTO `gb_box_field_mappings` VALUES (1719, 8, 'game', 'video_url', 'video_url', 'string', 'main', NULL, NULL, NULL, NULL, 'fill_empty', NULL, '1', NULL, '2026-03-13 11:23:22', '', '2026-03-13 11:23:22', 'video_url 映射到视频url');
INSERT INTO `gb_box_field_mappings` VALUES (1720, 9, 'game', 'screenshots[]', 'screenshots', 'json', 'main', NULL, NULL, NULL, NULL, 'fill_empty', NULL, '1', NULL, '2026-03-13 11:26:54', '', '2026-03-13 11:26:54', 'screenshots[] 映射到 screenshots');
INSERT INTO `gb_box_field_mappings` VALUES (1721, 9, 'game', 'video_url', 'video_url', 'string', 'main', NULL, NULL, NULL, NULL, 'fill_empty', NULL, '1', NULL, '2026-03-13 11:26:54', '', '2026-03-13 11:26:54', 'video_url 映射到 video_url ');

-- ----------------------------
-- Table structure for gb_box_game_relations
-- ----------------------------
DROP TABLE IF EXISTS `gb_box_game_relations`;
CREATE TABLE `gb_box_game_relations`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `box_id` bigint NOT NULL COMMENT '游戏盒子ID',
  `game_id` bigint NOT NULL COMMENT '游戏ID',
  `discount_label` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '折扣标签',
  `first_charge_domestic` decimal(4, 2) NULL DEFAULT NULL COMMENT '首充折扣-国内',
  `first_charge_overseas` decimal(4, 2) NULL DEFAULT NULL COMMENT '首充折扣-海外',
  `recharge_domestic` decimal(4, 2) NULL DEFAULT NULL COMMENT '续充折扣-国内',
  `recharge_overseas` decimal(4, 2) NULL DEFAULT NULL COMMENT '续充折扣-海外',
  `has_support` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '是否有扶持：0-无 1-有',
  `support_desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '扶持说明',
  `download_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '游戏下载链接',
  `promote_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '游戏推广网站链接',
  `qrcode_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '游戏二维码URL',
  `promote_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '推广语',
  `poster_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '宣传卡片/海报URL',
  `promotion_links` json NULL COMMENT '推广链接集合（JSON格式，含 download_url/web_url/qrcode 等）',
  `platform_data` json NULL COMMENT '平台特有数据（JSON格式，含 is_h5/cps_ratio/platform_game_id 等）',
  `is_featured` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '是否推荐：0-否 1-是',
  `is_exclusive` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '是否独占：0-否 1-是',
  `is_new` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '是否新游：0-否 1-是',
  `custom_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '自定义名称',
  `custom_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '自定义描述',
  `custom_download_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '自定义下载链接',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览量',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `added_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_box_game`(`box_id` ASC, `game_id` ASC) USING BTREE,
  INDEX `idx_box_id`(`box_id` ASC) USING BTREE,
  INDEX `idx_game_id`(`game_id` ASC) USING BTREE,
  INDEX `idx_is_featured`(`is_featured` ASC) USING BTREE,
  INDEX `idx_is_new`(`is_new` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 32739 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '游戏盒子-游戏关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_box_game_relations
-- ----------------------------

-- ----------------------------
-- Table structure for gb_categories
-- ----------------------------
DROP TABLE IF EXISTS `gb_categories`;
CREATE TABLE `gb_categories`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `site_id` bigint NULL DEFAULT NULL COMMENT '所属网站ID',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父分类ID，0为顶级',
  `category_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'game' COMMENT '分类类型：game-游戏 drama-短剧 article-文章 website-网站 gamebox-游戏盒子 document-文档 other-其他',
  `is_section` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '是否为板块：0-否 1-是（顶级分类用作板块）',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `slug` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类标识',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分类图标',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分类描述',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序序号',
  `document_count` int NULL DEFAULT 0 COMMENT '文档数量',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '状态：0-禁用 1-启用',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '删除标志',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE,
  INDEX `idx_category_type_section`(`category_type` ASC, `is_section` ASC) USING BTREE,
  INDEX `idx_site_type_slug_del`(`site_id` ASC, `category_type` ASC, `slug` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_slug_del`(`slug` ASC, `del_flag` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 295 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '分类表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_categories
-- ----------------------------
INSERT INTO `gb_categories` VALUES (17, 1, 0, 'drama', '0', '都市', 'urban', '🏙️', '都市现代题材', 1, 0, '1', '0', 'admin', '2025-12-23 05:38:36', '', '2026-02-27 20:36:35');
INSERT INTO `gb_categories` VALUES (18, 1, 0, 'drama', '0', '古装', 'costume', '👘', '古装历史题材', 2, 0, '1', '0', 'admin', '2025-12-23 05:38:36', '', '2026-02-27 20:36:35');
INSERT INTO `gb_categories` VALUES (19, 1, 0, 'drama', '0', '悬疑', 'mystery', '🔍', '悬疑推理题材', 3, 0, '1', '0', 'admin', '2025-12-23 05:38:36', '', '2026-02-27 20:36:35');
INSERT INTO `gb_categories` VALUES (20, 1, 0, 'drama', '0', '甜宠', 'sweet', '💕', '甜宠爱情题材', 4, 0, '1', '0', 'admin', '2025-12-23 05:38:36', '', '2026-02-27 20:36:35');
INSERT INTO `gb_categories` VALUES (21, 1, 0, 'drama', '0', '复仇', 'revenge', '⚔️', '复仇反击题材', 5, 0, '1', '0', 'admin', '2025-12-23 05:38:36', '', '2026-02-27 20:36:35');
INSERT INTO `gb_categories` VALUES (22, 1, 0, 'drama', '0', '玄幻', 'xuanhuan', '🌟', '玄幻奇幻题材', 6, 0, '1', '0', 'admin', '2025-12-23 05:38:36', '', '2026-02-27 20:36:35');
INSERT INTO `gb_categories` VALUES (23, 1, 0, 'drama', '0', '霸总', 'ceo', '💼', '霸道总裁题材', 7, 0, '1', '0', 'admin', '2025-12-23 05:38:36', '', '2026-02-27 20:36:35');
INSERT INTO `gb_categories` VALUES (24, 1, 0, 'drama', '0', '武侠', 'martial', '🥋', '武侠江湖题材', 8, 0, '1', '0', 'admin', '2025-12-23 05:38:36', '', '2026-02-27 20:36:36');
INSERT INTO `gb_categories` VALUES (25, 1, 0, 'drama', '0', '宫斗', 'palace', '👑', '宫廷斗争题材', 9, 0, '1', '0', 'admin', '2025-12-23 05:38:36', '', '2026-02-27 20:36:36');
INSERT INTO `gb_categories` VALUES (26, 1, 0, 'drama', '0', '重生', 'rebirth', '🔄', '重生逆袭题材', 10, 0, '1', '0', 'admin', '2025-12-23 05:38:36', '', '2026-02-27 20:36:36');
INSERT INTO `gb_categories` VALUES (27, 1, 145, 'article', '0', '攻略', 'guide', '📖', '游戏攻略教程', 1, 0, '1', '0', 'admin', '2025-12-23 05:38:36', 'admin', '2026-02-27 20:36:36');
INSERT INTO `gb_categories` VALUES (28, 1, 146, 'article', '0', '新闻', 'news', '📰', '游戏新闻资讯', 2, 0, '1', '0', 'admin', '2025-12-23 05:38:36', '', '2026-02-27 20:36:36');
INSERT INTO `gb_categories` VALUES (29, 1, 147, 'article', '0', '评测', 'review', '⭐', '游戏评测推荐', 3, 0, '1', '0', 'admin', '2025-12-23 05:38:36', '', '2026-02-27 20:36:36');
INSERT INTO `gb_categories` VALUES (30, 1, 148, 'article', '0', '活动', 'event', '🎉', '游戏活动公告', 4, 0, '1', '0', 'admin', '2025-12-23 05:38:36', '', '2026-02-27 20:36:36');
INSERT INTO `gb_categories` VALUES (31, 1, 148, 'article', '0', '礼包', 'gift', '🎁', '礼包码发放', 5, 0, '1', '0', 'admin', '2025-12-23 05:38:36', '', '2026-02-27 20:36:36');
INSERT INTO `gb_categories` VALUES (32, 1, 0, 'article', '1', '开服', 'server', '🚀', '开服表预告', 6, 0, '1', '0', 'admin', '2025-12-23 05:38:36', '', '2026-02-27 20:36:36');
INSERT INTO `gb_categories` VALUES (33, 1, 148, 'article', '0', '充值', 'recharge', '💰', '充值福利相关', 7, 0, '1', '0', 'admin', '2025-12-23 05:38:36', '', '2026-02-27 20:36:37');
INSERT INTO `gb_categories` VALUES (34, 1, 148, 'article', '0', '福利', 'welfare', '🎊', '福利活动', 8, 0, '1', '0', 'admin', '2025-12-23 05:38:36', '', '2026-02-27 20:36:37');
INSERT INTO `gb_categories` VALUES (35, 1, 146, 'article', '0', '更新', 'update', '🔄', '版本更新日志', 9, 0, '1', '0', 'admin', '2025-12-23 05:38:36', '', '2026-02-27 20:36:37');
INSERT INTO `gb_categories` VALUES (36, 9, 0, 'website', '0', '官方网站', 'official', '🏢', '官方主站', 1, 0, '1', '0', 'admin', '2025-12-23 05:38:36', 'admin', '2026-03-17 20:24:19');
INSERT INTO `gb_categories` VALUES (37, 9, 0, 'website', '0', '推广网站', 'promotion', '📢', '推广营销站点', 2, 0, '1', '0', 'admin', '2025-12-23 05:38:36', 'admin', '2026-03-17 20:24:19');
INSERT INTO `gb_categories` VALUES (38, 9, 0, 'website', '0', '社区论坛', 'community', '💬', '玩家社区论坛', 3, 0, '1', '0', 'admin', '2025-12-23 05:38:36', '', '2026-03-17 20:24:19');
INSERT INTO `gb_categories` VALUES (39, 9, 0, 'website', '0', '下载站', 'download', '📥', '游戏下载站', 4, 0, '1', '0', 'admin', '2025-12-23 05:38:36', '', '2026-03-17 20:24:19');
INSERT INTO `gb_categories` VALUES (40, 9, 0, 'website', '0', '资讯站', 'news-site', '📰', '新闻资讯站', 5, 0, '1', '0', 'admin', '2025-12-23 05:38:36', '', '2026-03-17 20:24:19');
INSERT INTO `gb_categories` VALUES (41, 9, 0, 'website', '0', '工具站', 'tool', '🔧', '辅助工具站', 6, 0, '1', '0', 'admin', '2025-12-23 05:38:36', '', '2026-03-17 20:24:19');
INSERT INTO `gb_categories` VALUES (42, 1, 0, 'gamebox', '0', '折扣盒子', 'discount-box', '💰', '提供游戏折扣充值', 1, 0, '1', '0', 'admin', '2025-12-23 05:38:36', 'admin', '2026-02-27 20:36:38');
INSERT INTO `gb_categories` VALUES (43, 1, 0, 'gamebox', '0', 'BT盒子', 'bt-box', '⚡', 'BT变态版游戏', 2, 0, '1', '0', 'admin', '2025-12-23 05:38:36', '', '2026-02-27 20:36:38');
INSERT INTO `gb_categories` VALUES (44, 1, 0, 'gamebox', '0', '官服盒子', 'official-box', '🏢', '官方正版游戏', 3, 0, '1', '0', 'admin', '2025-12-23 05:38:36', '', '2026-02-27 20:36:38');
INSERT INTO `gb_categories` VALUES (45, 1, 0, 'gamebox', '0', '首充盒子', 'first-charge-box', '🎁', '首充返利盒子', 4, 0, '1', '0', 'admin', '2025-12-23 05:38:36', '', '2026-02-27 20:36:38');
INSERT INTO `gb_categories` VALUES (46, 1, 0, 'gamebox', '0', '公益盒子', 'welfare-box', '❤️', '公益服盒子', 5, 0, '1', '0', 'admin', '2025-12-23 05:38:36', '', '2026-02-27 20:36:38');
INSERT INTO `gb_categories` VALUES (47, 1, 0, 'gamebox', '0', '满V盒子', 'full-vip-box', '👑', '上线满V盒子', 6, 0, '1', '0', 'admin', '2025-12-23 05:38:36', '', '2026-02-27 20:36:38');
INSERT INTO `gb_categories` VALUES (48, 1, 0, 'document', '0', '入门教程', 'tutorial', '📚', '新手入门教程', 1, 0, '1', '0', 'admin', '2025-12-23 05:38:36', '', '2026-02-27 20:36:38');
INSERT INTO `gb_categories` VALUES (49, 1, 0, 'document', '0', 'API文档', 'api-doc', '🔌', '接口文档说明', 2, 0, '1', '0', 'admin', '2025-12-23 05:38:36', '', '2026-02-27 20:36:38');
INSERT INTO `gb_categories` VALUES (50, 1, 0, 'document', '0', '常见问题', 'faq', '❓', 'FAQ常见问题', 3, 0, '1', '0', 'admin', '2025-12-23 05:38:36', '', '2026-02-27 20:36:38');
INSERT INTO `gb_categories` VALUES (51, 1, 0, 'document', '0', '开发指南', 'dev-guide', '💻', '开发者指南', 4, 0, '1', '0', 'admin', '2025-12-23 05:38:36', '', '2026-02-27 20:36:38');
INSERT INTO `gb_categories` VALUES (52, 1, 0, 'document', '0', '部署文档', 'deploy', '🚀', '部署运维文档', 5, 0, '1', '0', 'admin', '2025-12-23 05:38:36', '', '2026-02-27 20:36:39');
INSERT INTO `gb_categories` VALUES (53, 1, 0, 'document', '0', '更新日志', 'changelog', '📝', '版本更新记录', 6, 0, '1', '0', 'admin', '2025-12-23 05:38:36', '', '2026-02-27 20:36:39');
INSERT INTO `gb_categories` VALUES (58, 1, 0, 'storage', '0', '云存储', 'cloud-storage', '☁️', '云存储服务配置', 1, 0, '1', '0', 'admin', '2025-12-23 06:22:25', 'admin', '2026-02-27 20:36:39');
INSERT INTO `gb_categories` VALUES (59, 1, 0, 'storage', '0', '本地存储', 'local-storage', '💾', '本地存储配置', 2, 0, '1', '0', 'admin', '2025-12-23 06:22:25', 'admin', '2026-02-27 20:36:39');
INSERT INTO `gb_categories` VALUES (60, 1, 0, 'storage', '0', '对象存储', 'object-storage', '📦', '对象存储服务', 3, 0, '1', '0', 'admin', '2025-12-23 06:22:25', 'admin', '2026-02-27 20:36:39');
INSERT INTO `gb_categories` VALUES (61, 1, 0, 'storage', '0', '图片存储', 'image-storage', '🖼️', '图片专用存储', 4, 0, '1', '0', 'admin', '2025-12-23 06:22:25', 'admin', '2026-02-27 20:36:39');
INSERT INTO `gb_categories` VALUES (62, 1, 0, 'storage', '0', '文件存储', 'file-storage', '📁', '文件存储服务', 5, 0, '1', '0', 'admin', '2025-12-23 06:22:25', 'admin', '2026-02-27 20:36:39');
INSERT INTO `gb_categories` VALUES (63, 9, 0, 'storage', '0', '视频存储', 'video-storage', '🎬', '视频存储服务', 6, 0, '1', '0', 'admin', '2025-12-23 06:22:25', 'admin', '2026-03-17 20:24:19');
INSERT INTO `gb_categories` VALUES (64, 1, 0, 'storage', '0', 'CDN加速', 'cdn', '🚀', 'CDN加速服务', 7, 0, '1', '0', 'admin', '2025-12-23 06:22:25', 'admin', '2026-02-27 20:36:40');
INSERT INTO `gb_categories` VALUES (65, 1, 0, 'game', '0', '经营', 'jingying', '🏪', '模拟经营类游戏', 13, 0, '1', '0', 'admin', '2025-12-26 08:05:59', '', '2026-02-27 20:36:40');
INSERT INTO `gb_categories` VALUES (66, 1, 0, 'game', '0', '养成', 'yangcheng', '🌱', '角色养成类游戏', 14, 0, '1', '0', 'admin', '2025-12-26 08:05:59', '', '2026-02-27 20:36:40');
INSERT INTO `gb_categories` VALUES (67, 1, 0, 'game', '0', '冒险', 'maoxian', '🗺️', '冒险探索类游戏', 15, 0, '1', '0', 'admin', '2025-12-26 08:05:59', '', '2026-02-27 20:36:40');
INSERT INTO `gb_categories` VALUES (68, 1, 0, 'game', '0', '玄幻', 'xuanhuan', '✨', '玄幻奇幻类游戏', 16, 0, '1', '0', 'admin', '2025-12-26 08:05:59', '', '2026-02-27 20:36:40');
INSERT INTO `gb_categories` VALUES (69, 1, 0, 'game', '0', '科幻', 'kehuan', '🚀', '科幻未来类游戏', 17, 0, '1', '0', 'admin', '2025-12-26 08:05:59', '', '2026-02-27 20:36:40');
INSERT INTO `gb_categories` VALUES (70, 1, 0, 'game', '0', '割草', 'geca', '⚔️', '割草动作类游戏', 18, 0, '1', '0', 'admin', '2025-12-26 08:05:59', '', '2026-02-27 20:36:40');
INSERT INTO `gb_categories` VALUES (71, 1, 0, 'game', '0', '角色扮演', 'juese-banyan', '🎭', '角色扮演类游戏', 19, 0, '1', '0', 'admin', '2025-12-26 08:05:59', '', '2026-02-27 20:36:40');
INSERT INTO `gb_categories` VALUES (72, 1, 0, 'game', '0', '官方', 'guanfang', '🏢', '官方正版游戏', 20, 0, '1', '0', 'admin', '2025-12-26 08:05:59', '', '2026-02-27 20:36:40');
INSERT INTO `gb_categories` VALUES (73, 1, 0, 'game', '0', '休闲', 'xiuxian', '🎲', '休闲娱乐类游戏', 21, 0, '1', '0', 'admin', '2025-12-26 08:05:59', '', '2026-02-27 20:36:41');
INSERT INTO `gb_categories` VALUES (74, 1, 0, 'game', '0', '动漫', 'dongman', '🎬', '动漫IP改编游戏', 22, 0, '1', '0', 'admin', '2025-12-26 08:05:59', '', '2026-02-27 20:36:41');
INSERT INTO `gb_categories` VALUES (75, 1, 0, 'game', '0', '放置', 'fangzhi', '⏱️', '放置挂机类游戏', 23, 0, '1', '0', 'admin', '2025-12-26 08:05:59', '', '2026-02-27 20:36:41');
INSERT INTO `gb_categories` VALUES (76, 1, 0, 'game', '0', '回合', 'huihe', '🔄', '回合制游戏', 24, 0, '1', '0', 'admin', '2025-12-26 08:05:59', '', '2026-02-27 20:36:41');
INSERT INTO `gb_categories` VALUES (77, 1, 0, 'game', '0', '西游', 'xiyou-global', '🐒', '西游记题材游戏', 7, 0, '1', '0', 'admin', '2025-12-26 08:05:59', '', '2026-02-27 20:36:41');
INSERT INTO `gb_categories` VALUES (78, 1, 0, 'game', '0', '挂机', 'guaji-global', '⏰', '放置挂机游戏', 11, 0, '1', '0', 'admin', '2025-12-26 08:05:59', '', '2026-02-27 20:36:41');
INSERT INTO `gb_categories` VALUES (79, 1, 0, 'game', '0', '二次元', 'erciyuan-global', '🎨', '二次元动漫风格游戏', 4, 0, '1', '0', 'admin', '2025-12-26 08:05:59', '', '2026-02-27 20:36:41');
INSERT INTO `gb_categories` VALUES (80, 1, 0, 'game', '0', '武侠', 'wuxia-global', '🥋', '武侠江湖类游戏', 5, 0, '1', '0', 'admin', '2025-12-26 08:05:59', '', '2026-02-27 20:36:41');
INSERT INTO `gb_categories` VALUES (81, 1, 0, 'game', '0', '仙侠', 'xianxia-global', '⚔️', '仙侠修真类游戏', 1, 0, '1', '0', 'admin', '2025-12-26 08:05:59', 'admin', '2026-02-27 20:36:42');
INSERT INTO `gb_categories` VALUES (82, 1, 0, 'game', '0', '魔幻', 'mohuan-global', '🧙', '魔幻题材游戏', 3, 0, '1', '0', 'admin', '2025-12-26 08:05:59', '', '2026-02-27 20:36:42');
INSERT INTO `gb_categories` VALUES (83, 1, 0, 'game', '0', '策略', 'celue-global', '🎯', '策略战争游戏', 9, 0, '1', '0', 'admin', '2025-12-26 08:05:59', '', '2026-02-27 20:36:42');
INSERT INTO `gb_categories` VALUES (84, 1, 0, 'game', '0', '卡牌', 'kapai-global', '🎴', '卡牌策略游戏', 8, 0, '1', '0', 'admin', '2025-12-26 08:05:59', '', '2026-02-27 20:36:42');
INSERT INTO `gb_categories` VALUES (85, 1, 0, 'game', '0', '三国', 'sanguo-global', '🏛️', '三国题材游戏', 6, 0, '1', '0', 'admin', '2025-12-26 08:05:59', '', '2026-02-27 20:36:42');
INSERT INTO `gb_categories` VALUES (114, 9, 0, 'ai_platform', '0', '默认配置', 'aiplatform', NULL, NULL, 0, 0, '1', '0', 'admin', '2025-12-31 17:26:45', 'admin', '2026-03-18 12:23:47');
INSERT INTO `gb_categories` VALUES (116, 9, 0, 'title_pool', '0', '游戏攻略', 'game-guide', '🎮', NULL, 1, 0, '1', '0', 'admin', '2026-01-01 16:00:52', '', '2026-03-17 20:24:19');
INSERT INTO `gb_categories` VALUES (117, 9, 0, 'title_pool', '0', '新闻资讯', 'news', '📰', NULL, 2, 0, '1', '0', 'admin', '2026-01-01 16:00:52', '', '2026-03-17 20:24:19');
INSERT INTO `gb_categories` VALUES (118, 9, 0, 'title_pool', '0', '评测推荐', 'review', '⭐', NULL, 3, 0, '1', '0', 'admin', '2026-01-01 16:00:52', '', '2026-03-17 20:24:19');
INSERT INTO `gb_categories` VALUES (119, 9, 0, 'title_pool', '0', 'SEO优化', 'seo', '🔍', NULL, 4, 0, '1', '0', 'admin', '2026-01-01 16:00:52', '', '2026-03-17 20:24:19');
INSERT INTO `gb_categories` VALUES (120, 9, 0, 'title_pool', '0', '其他', 'other', '📝', NULL, 99, 0, '1', '0', 'admin', '2026-01-01 16:00:52', '', '2026-03-17 20:24:19');
INSERT INTO `gb_categories` VALUES (127, 9, 0, 'atomic_tool', '0', 'AI工具', 'ai_tools', '🤖', 'AI驱动的原子工具', 1, 0, '1', '0', '', '2026-01-05 15:39:40', '', '2026-03-17 20:24:19');
INSERT INTO `gb_categories` VALUES (128, 9, 0, 'atomic_tool', '0', 'API工具', 'api_tools', '🔌', '调用外部API的工具', 2, 0, '1', '0', '', '2026-01-05 15:39:40', '', '2026-03-17 20:24:19');
INSERT INTO `gb_categories` VALUES (129, 9, 0, 'atomic_tool', '0', '内置工具', 'builtin_tools', '⚙️', '系统内置功能工具', 3, 0, '1', '0', '', '2026-01-05 15:39:40', '', '2026-03-17 20:24:19');
INSERT INTO `gb_categories` VALUES (130, 9, 0, 'atomic_tool', '0', '文本处理', 'text_processing', '📝', '文本处理相关工具', 4, 0, '1', '0', '', '2026-01-05 15:39:40', '', '2026-03-17 20:24:19');
INSERT INTO `gb_categories` VALUES (131, 9, 0, 'atomic_tool', '0', '图片处理', 'image_processing', '🖼️', '图片处理相关工具', 5, 0, '1', '0', '', '2026-01-05 15:39:40', '', '2026-03-17 20:24:19');
INSERT INTO `gb_categories` VALUES (132, 9, 0, 'atomic_tool', '0', 'SEO优化', 'seo_optimization', '🔍', 'SEO优化相关工具', 6, 0, '1', '0', '', '2026-01-05 15:39:40', '', '2026-03-17 20:24:19');
INSERT INTO `gb_categories` VALUES (133, 9, 0, 'workflow', '0', '内容生成', 'content-generation', '✍️', '用于生成各类内容的工作流', 1, 0, '1', '0', 'admin', '2026-01-06 00:15:18', '', '2026-03-17 20:24:19');
INSERT INTO `gb_categories` VALUES (134, 9, 0, 'workflow', '0', '数据处理', 'data-processing', '📊', '数据清洗、转换、分析等工作流', 2, 0, '1', '0', 'admin', '2026-01-06 00:15:18', '', '2026-03-17 20:24:19');
INSERT INTO `gb_categories` VALUES (135, 9, 0, 'workflow', '0', '批量操作', 'batch-operation', '🔄', '批量处理任务的工作流', 3, 0, '1', '0', 'admin', '2026-01-06 00:15:18', '', '2026-03-17 20:24:19');
INSERT INTO `gb_categories` VALUES (136, 9, 0, 'workflow', '0', '自动化任务', 'automation', '⚙️', '定时或自动触发的任务工作流', 4, 0, '1', '0', 'admin', '2026-01-06 00:15:18', '', '2026-03-17 20:24:19');
INSERT INTO `gb_categories` VALUES (137, 9, 0, 'workflow', '0', 'AI辅助', 'ai-assisted', '🤖', '使用AI能力的工作流', 5, 0, '1', '0', 'admin', '2026-01-06 00:15:18', '', '2026-03-17 20:24:19');
INSERT INTO `gb_categories` VALUES (138, 9, 0, 'workflow', '0', '数据同步', 'data-sync', '🔗', '跨系统数据同步工作流', 6, 0, '1', '0', 'admin', '2026-01-06 00:15:18', '', '2026-03-17 20:24:19');
INSERT INTO `gb_categories` VALUES (139, 9, 0, 'workflow', '0', '质量检查', 'quality-check', '✅', '内容质量检查和审核工作流', 7, 0, '1', '0', 'admin', '2026-01-06 00:15:18', '', '2026-03-17 20:24:19');
INSERT INTO `gb_categories` VALUES (140, 9, 0, 'workflow', '0', '通知提醒', 'notification', '🔔', '消息通知和提醒工作流', 8, 0, '1', '0', 'admin', '2026-01-06 00:15:18', '', '2026-03-17 20:24:19');
INSERT INTO `gb_categories` VALUES (141, 9, 0, 'workflow', '0', '自定义', 'custom', '🔧', '自定义工作流', 99, 0, '1', '0', 'admin', '2026-01-06 00:15:18', '', '2026-03-17 20:24:19');
INSERT INTO `gb_categories` VALUES (145, 1, 0, 'article', '1', '攻略板块', 'guide-section', '📖', '游戏攻略和教程板块', 1, 0, '1', '0', 'admin', '2026-01-08 14:20:35', 'admin', '2026-02-27 20:36:46');
INSERT INTO `gb_categories` VALUES (146, 1, 0, 'article', '1', '资讯板块', 'news-section', '📰', '最新游戏资讯板块', 2, 0, '1', '0', 'admin', '2026-01-08 14:20:35', '', '2026-02-27 20:36:46');
INSERT INTO `gb_categories` VALUES (147, 1, 0, 'article', '1', '评测板块', 'review-section', '⭐', '游戏评测推荐板块', 3, 0, '1', '0', 'admin', '2026-01-08 14:20:35', '', '2026-02-27 20:36:46');
INSERT INTO `gb_categories` VALUES (148, 1, 0, 'article', '1', '活动板块', 'event-section', '🎉', '活动和福利板块', 4, 0, '1', '0', 'admin', '2026-01-08 14:20:35', '', '2026-02-27 20:36:47');

-- ----------------------------
-- Table structure for gb_category_types
-- ----------------------------
DROP TABLE IF EXISTS `gb_category_types`;
CREATE TABLE `gb_category_types`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `value` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类类型值（唯一标识）',
  `label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类类型标签（显示名称）',
  `tag_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'info' COMMENT 'Tag标签类型：primary/success/warning/danger/info',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分类类型描述',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序号',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '状态：0正常 1停用',
  `is_system` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '是否系统内置：0否 1是（系统内置不可删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_value`(`value` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '分类类型配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_category_types
-- ----------------------------
INSERT INTO `gb_category_types` VALUES (1, 'game', '游戏分类', 'primary', '用于游戏管理页面的分类', 5, '0', '1', '', '2025-12-23 08:43:36', '', NULL, NULL);
INSERT INTO `gb_category_types` VALUES (2, 'drama', '短剧分类', 'success', '用于短剧管理页面的分类', 99, '0', '1', '', '2025-12-23 08:43:36', '', NULL, NULL);
INSERT INTO `gb_category_types` VALUES (3, 'article', '文章分类', 'warning', '用于文章管理页面的分类', 7, '0', '1', '', '2025-12-23 08:43:36', '', NULL, NULL);
INSERT INTO `gb_category_types` VALUES (4, 'website', '网站分类', 'info', '用于网站管理页面的分类', 1, '0', '1', '', '2025-12-23 08:43:36', '', NULL, NULL);
INSERT INTO `gb_category_types` VALUES (5, 'gamebox', '游戏盒子分类', 'danger', '用于游戏盒子管理页面的分类', 6, '0', '1', '', '2025-12-23 08:43:36', '', NULL, NULL);
INSERT INTO `gb_category_types` VALUES (6, 'document', '文档分类', '', '用于文档管理页面的分类', 99, '0', '1', '', '2025-12-23 08:43:36', '', NULL, NULL);
INSERT INTO `gb_category_types` VALUES (7, 'storage', '存储配置分类', 'primary', '用于存储配置页面的分类', 2, '0', '1', '', '2025-12-23 08:43:36', '', NULL, NULL);
INSERT INTO `gb_category_types` VALUES (8, 'other', '其他分类', 'info', '用于其他未分类的内容', 100, '0', '1', '', '2025-12-23 08:43:36', '', NULL, NULL);
INSERT INTO `gb_category_types` VALUES (9, 'ai_platform', 'AI平台分类', 'primary', '用于AI平台配置的分类', 3, '0', '1', '', '2025-12-31 17:16:08', '', NULL, NULL);
INSERT INTO `gb_category_types` VALUES (11, 'title_pool', '标题池分类', 'primary', '用于标题池管理页面的分类', 8, '0', '1', '', '2026-01-01 15:59:25', '', NULL, NULL);
INSERT INTO `gb_category_types` VALUES (14, 'workflow', '工作流分类', 'primary', '用于工作流管理页面的分类', 9, '0', '1', 'admin', '2026-01-06 00:19:34', '', NULL, NULL);
INSERT INTO `gb_category_types` VALUES (15, 'atomic_tool', '原子工具分类', 'primary', '用于原子工具管理页面的分类', 4, '0', '1', 'admin', '2026-01-06 21:13:20', '', NULL, '用于工作流编排中的原子工具分类管理');

-- ----------------------------
-- Table structure for gb_drama_vendors
-- ----------------------------
DROP TABLE IF EXISTS `gb_drama_vendors`;
CREATE TABLE `gb_drama_vendors`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `site_id` bigint NULL DEFAULT NULL COMMENT '所属网站ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '厂商名称',
  `short_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '厂商简称',
  `logo_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '厂商Logo',
  `banner_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '厂商Banner图',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '厂商描述',
  `official_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '官网地址',
  `app_download_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'APP下载地址',
  `android_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '安卓下载',
  `ios_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'iOS下载',
  `wechat_mini_appid` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '微信小程序AppID',
  `contact_info` json NULL COMMENT '联系方式（JSON）',
  `cooperation_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '合作类型：cps/cpa/cpm',
  `commission_rate` decimal(5, 4) NULL DEFAULT 0.0000 COMMENT '佣金比例',
  `features` json NULL COMMENT '特色功能（JSON数组）',
  `drama_count` int NULL DEFAULT 0 COMMENT '收录短剧数',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '状态：0-下架 1-上架',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '删除标志',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_site_id`(`site_id` ASC) USING BTREE,
  INDEX `idx_name`(`name` ASC) USING BTREE,
  INDEX `idx_cooperation_type`(`cooperation_type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '短剧厂商表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_drama_vendors
-- ----------------------------

-- ----------------------------
-- Table structure for gb_dramas
-- ----------------------------
DROP TABLE IF EXISTS `gb_dramas`;
CREATE TABLE `gb_dramas`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `site_id` bigint NULL DEFAULT NULL COMMENT '所属网站ID',
  `vendor_id` bigint NULL DEFAULT NULL COMMENT '所属厂商ID',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '短剧名称',
  `alias_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '别名',
  `cover_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '封面图URL',
  `poster_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '海报图URL',
  `screenshots` json NULL COMMENT '剧照（JSON数组）',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '剧情简介',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '剧情分类',
  `genre` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '题材标签',
  `director` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '导演',
  `actors` json NULL COMMENT '主演（JSON数组）',
  `episode_count` int NULL DEFAULT 0 COMMENT '总集数',
  `episode_duration` int NULL DEFAULT 0 COMMENT '每集时长（秒）',
  `total_duration` int NULL DEFAULT 0 COMMENT '总时长（秒）',
  `release_date` date NULL DEFAULT NULL COMMENT '上线日期',
  `source_platform` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '首发平台',
  `play_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '播放链接',
  `play_count` bigint NULL DEFAULT 0 COMMENT '播放次数',
  `like_count` int NULL DEFAULT 0 COMMENT '点赞次数',
  `collect_count` int NULL DEFAULT 0 COMMENT '收藏次数',
  `rating` decimal(2, 1) NULL DEFAULT 0.0 COMMENT '评分（0-10）',
  `is_free` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '是否免费：0-否 1-是',
  `price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '价格',
  `tags` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标签，逗号分隔',
  `is_hot` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '是否热门：0-否 1-是',
  `is_new` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '是否新剧：0-否 1-是',
  `is_recommend` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '是否推荐：0-否 1-是',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '状态：0-下架 1-上架',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '删除标志',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_site_id`(`site_id` ASC) USING BTREE,
  INDEX `idx_vendor_id`(`vendor_id` ASC) USING BTREE,
  INDEX `idx_name`(`name` ASC) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  INDEX `idx_release_date`(`release_date` ASC) USING BTREE,
  INDEX `idx_is_hot`(`is_hot` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '短剧表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_dramas
-- ----------------------------

-- ----------------------------
-- Table structure for gb_game_activities
-- ----------------------------
DROP TABLE IF EXISTS `gb_game_activities`;
CREATE TABLE `gb_game_activities`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `game_id` bigint NOT NULL COMMENT '游戏ID',
  `box_id` bigint NULL DEFAULT NULL COMMENT '游戏盒子ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '活动名称',
  `activity_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'activity' COMMENT '类型：activity-活动 server-开服 other-其他',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '活动描述',
  `banner_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '活动Banner',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `is_permanent` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '是否永久：0-否 1-是',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '状态：0-禁用 1-启用',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '删除标志',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_game_id`(`game_id` ASC) USING BTREE,
  INDEX `idx_box_id`(`box_id` ASC) USING BTREE,
  INDEX `idx_activity_type`(`activity_type` ASC) USING BTREE,
  INDEX `idx_time_range`(`start_time` ASC, `end_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '游戏活动表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_game_activities
-- ----------------------------

-- ----------------------------
-- Table structure for gb_game_boxes
-- ----------------------------
DROP TABLE IF EXISTS `gb_game_boxes`;
CREATE TABLE `gb_game_boxes`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `site_id` bigint NULL DEFAULT NULL COMMENT '所属网站ID',
  `category_id` bigint NULL DEFAULT NULL COMMENT '分类ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '盒子名称',
  `logo_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '盒子Logo',
  `banner_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '盒子Banner图',
  `qrcode_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '盒子二维码图片URL',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '盒子描述',
  `official_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '官网地址',
  `download_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '通用下载地址',
  `android_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '安卓下载',
  `ios_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'iOS下载',
  `promote_url_1` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '推广链接①',
  `promote_url_2` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '推广链接②',
  `promote_url_3` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '推广链接③',
  `register_download_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '先注册再下载链接④',
  `promotion_links` json NULL COMMENT '推广链接集合（JSON格式）',
  `discount_rate` decimal(3, 2) NULL DEFAULT 1.00 COMMENT '折扣率（0.1-1.0）',
  `features` json NULL COMMENT '特色功能（JSON数组）',
  `features_desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '特色功能详细描述',
  `supported_game_types` json NULL COMMENT '支持的游戏类型（JSON数组）',
  `game_count` int NULL DEFAULT 0 COMMENT '收录游戏数',
  `rating` decimal(2, 1) NULL DEFAULT 0.0 COMMENT '评分（0-5）',
  `download_count` int NULL DEFAULT 0 COMMENT '下载次数',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `priority` int NULL DEFAULT 0 COMMENT '盒子优先级，数字越大优先级越高（用于解决导入数据冲突）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '状态：0-下架 1-上架',
  `platform` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '来源平台：u2game/milu/277sy/dongyouxi',
  `source_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '源平台盒子ID',
  `last_sync_time` datetime NULL DEFAULT NULL COMMENT '最后同步时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '删除标志',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_site_id`(`site_id` ASC) USING BTREE,
  INDEX `idx_name`(`name` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_platform`(`platform` ASC) USING BTREE,
  INDEX `idx_source_id`(`source_id` ASC) USING BTREE,
  INDEX `idx_rating`(`rating` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '游戏盒子表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_game_boxes
-- ----------------------------
INSERT INTO `gb_game_boxes` VALUES (3, 1, 42, 'U2Game盒子', 'http://www.u2game99.com/data/upload/game/20240813/66bb02722f681.png', NULL, 'https://www.u2game99.com/app/1037.html', NULL, 'https://www.u2game99.com/', 'https://www.u2game99.com/app/1037.html', NULL, NULL, 'https://www.u2game99.com/app/1037.html', 'https://qdzs.u2game99.com/welcome/page/ag/1037.html', 'https://www.u2game99.com/wc/1037/42.html', 'https://www.u2game99.com/?tgid=1037', NULL, 1.00, NULL, NULL, NULL, 0, 0.0, 0, 0, 40, '1', 'u2game', NULL, NULL, '0', 'admin', '2025-12-26 06:15:10', 'admin', '2026-03-25 13:16:26', '导入数据：{\"tag\":\"yxhz\",\"addtime\":\"2025/12/11 21:29:42\",\"originalId\":2}');
INSERT INTO `gb_game_boxes` VALUES (7, 1, 42, '277游戏', 'https://www.277sy.com/pageall/277synew/pc/img/logo.png', NULL, '', NULL, NULL, 'https://www.277sy.com/?tgid=dd0038708', 'https://reg.277sy.com/?tgid=dd0038708', 'https://reg.277sy.com/?tgid=dd0038708', NULL, NULL, NULL, NULL, NULL, 1.00, NULL, NULL, NULL, 0, 0.0, 0, 0, 30, '1', NULL, NULL, NULL, '0', 'admin', '2026-03-06 21:03:55', 'admin', '2026-03-25 13:16:17', NULL);
INSERT INTO `gb_game_boxes` VALUES (8, 1, 42, '嘿咕游戏', 'https://storage.milu.com/agent/kij4/box/20220607143013.png', NULL, NULL, NULL, NULL, 'https://app.heigu.com/?a=4g5j&u=1', 'https://app.heigu.com/?a=4g5j&u=1', 'https://app.heigu.com/?a=4g5j&u=1', NULL, NULL, NULL, NULL, NULL, 1.00, NULL, NULL, NULL, 0, 0.0, 0, 0, 20, '1', NULL, NULL, NULL, '0', 'admin', '2026-03-06 21:53:37', 'admin', '2026-03-25 13:16:08', NULL);
INSERT INTO `gb_game_boxes` VALUES (9, 1, 42, '巴兔游戏', 'https://assets.tsyule.cn/assets/web_vue/auth/batu144.png', NULL, NULL, NULL, 'http://www.tsyule.cn/hb1080814-7.html', 'http://www.tsyule.cn/hb1080814-7.html', 'http://www.tsyule.cn/hb1080814-7.html', 'http://www.tsyule.cn/hb1080814-7.html', NULL, NULL, NULL, NULL, NULL, 1.00, NULL, NULL, NULL, 0, 0.0, 0, 0, 10, '1', NULL, NULL, NULL, '0', 'admin', '2026-03-06 21:54:43', 'admin', '2026-03-25 13:15:57', NULL);

-- ----------------------------
-- Table structure for gb_game_category_relations
-- ----------------------------
DROP TABLE IF EXISTS `gb_game_category_relations`;
CREATE TABLE `gb_game_category_relations`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `game_id` bigint NOT NULL COMMENT '游戏ID',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `sort_order` int NULL DEFAULT 0 COMMENT '在该分类中的排序',
  `is_primary` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '是否主分类：0-否 1-是',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_game_category`(`game_id` ASC, `category_id` ASC) USING BTREE,
  INDEX `idx_game_id`(`game_id` ASC) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_is_primary`(`is_primary` ASC) USING BTREE,
  INDEX `idx_sort_order`(`sort_order` ASC) USING BTREE,
  CONSTRAINT `fk_game_category_category` FOREIGN KEY (`category_id`) REFERENCES `gb_categories` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_game_category_game` FOREIGN KEY (`game_id`) REFERENCES `gb_games` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 49680 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '游戏分类关联表-用于游戏的多分类逻辑' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_game_category_relations
-- ----------------------------

-- ----------------------------
-- Table structure for gb_game_change_log
-- ----------------------------
DROP TABLE IF EXISTS `gb_game_change_log`;
CREATE TABLE `gb_game_change_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `batch_id` bigint NULL DEFAULT NULL COMMENT '所属批次ID',
  `batch_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '批次号',
  `game_id` bigint NOT NULL COMMENT '游戏ID',
  `game_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '游戏名称（冗余）',
  `change_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '变更类型：INSERT-新增 UPDATE-修改',
  `target_table` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '变更目标表：gb_games / gb_box_game_relations',
  `target_id` bigint NULL DEFAULT NULL COMMENT '目标表记录ID',
  `before_snapshot` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '变更前记录快照（JSON），INSERT时为空',
  `after_snapshot` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '变更后记录快照（JSON）',
  `reverted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已撤回（0-否 1-是）',
  `revert_time` datetime NULL DEFAULT NULL COMMENT '撤回时间',
  `revert_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '撤回操作人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_batch_id`(`batch_id` ASC) USING BTREE,
  INDEX `idx_game_id`(`game_id` ASC) USING BTREE,
  INDEX `idx_batch_no`(`batch_no` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 83108 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '游戏字段变更日志表（按批次记录每次导入的数据变更快照）' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_game_change_log
-- ----------------------------

-- ----------------------------
-- Table structure for gb_game_giftcodes
-- ----------------------------
DROP TABLE IF EXISTS `gb_game_giftcodes`;
CREATE TABLE `gb_game_giftcodes`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `game_id` bigint NOT NULL COMMENT '游戏ID',
  `box_id` bigint NULL DEFAULT NULL COMMENT '游戏盒子ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '礼包名称',
  `gift_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'newbie' COMMENT '礼包类型：newbie-新手卡 reserve-预约 community-社区 welfare-福利',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '礼包内容说明',
  `items` json NULL COMMENT '礼包物品（JSON数组）',
  `code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '礼包码',
  `total_count` int NULL DEFAULT 9999 COMMENT '总数量',
  `remain_count` int NULL DEFAULT 9999 COMMENT '剩余数量',
  `limit_per_user` int NULL DEFAULT 1 COMMENT '每人限领',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `is_exclusive` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '是否独占：0-否 1-是',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '状态：0-禁用 1-启用',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '删除标志',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_game_id`(`game_id` ASC) USING BTREE,
  INDEX `idx_box_id`(`box_id` ASC) USING BTREE,
  INDEX `idx_gift_type`(`gift_type` ASC) USING BTREE,
  INDEX `idx_time_range`(`start_time` ASC, `end_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '游戏礼包表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_game_giftcodes
-- ----------------------------

-- ----------------------------
-- Table structure for gb_game_materials
-- ----------------------------
DROP TABLE IF EXISTS `gb_game_materials`;
CREATE TABLE `gb_game_materials`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `game_id` bigint NOT NULL COMMENT '游戏ID',
  `box_id` bigint NULL DEFAULT NULL COMMENT '游戏盒子ID',
  `material_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '素材类型：icon-图标 cover-封面 screenshot-截图 video-视频 poster-海报 qrcode-二维码',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '素材名称',
  `url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '素材URL',
  `thumbnail_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '缩略图URL',
  `file_size` int NULL DEFAULT 0 COMMENT '文件大小（字节）',
  `width` int NULL DEFAULT 0 COMMENT '宽度（像素）',
  `height` int NULL DEFAULT 0 COMMENT '高度（像素）',
  `duration` int NULL DEFAULT 0 COMMENT '时长（秒，视频用）',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '状态：0-禁用 1-启用',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '删除标志',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_game_id`(`game_id` ASC) USING BTREE,
  INDEX `idx_box_id`(`box_id` ASC) USING BTREE,
  INDEX `idx_material_type`(`material_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '游戏素材表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_game_materials
-- ----------------------------

-- ----------------------------
-- Table structure for gb_game_servers
-- ----------------------------
DROP TABLE IF EXISTS `gb_game_servers`;
CREATE TABLE `gb_game_servers`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `game_id` bigint NOT NULL COMMENT '游戏ID',
  `box_id` bigint NULL DEFAULT NULL COMMENT '游戏盒子ID',
  `server_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '服务器名称',
  `server_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '服务器编号',
  `open_time` datetime NOT NULL COMMENT '开服时间',
  `server_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'new' COMMENT '类型：new-新服 merge-合服 test-测试服',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '开服说明',
  `is_recommend` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '是否推荐：0-否 1-是',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '状态：0-已关闭 1-正常 2-火爆',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '删除标志',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_game_id`(`game_id` ASC) USING BTREE,
  INDEX `idx_box_id`(`box_id` ASC) USING BTREE,
  INDEX `idx_open_time`(`open_time` ASC) USING BTREE,
  INDEX `idx_server_type`(`server_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '游戏开服表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_game_servers
-- ----------------------------

-- ----------------------------
-- Table structure for gb_games
-- ----------------------------
DROP TABLE IF EXISTS `gb_games`;
CREATE TABLE `gb_games`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `site_id` bigint NULL DEFAULT NULL COMMENT '所属网站ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '游戏名称',
  `subtitle` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '游戏副标题/版本说明',
  `name_remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '游戏副名/版本说明',
  `short_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '游戏简称',
  `game_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'official' COMMENT '游戏类型：official-官方 discount-折扣 bt-BT版',
  `icon_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '游戏图标URL',
  `cover_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '游戏封面URL',
  `banner_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '游戏横幅URL',
  `screenshots` json NULL COMMENT '游戏截图（JSON数组）',
  `video_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '游戏视频URL',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '游戏描述',
  `promotion_desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '推广说明',
  `developer` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '开发商',
  `publisher` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '发行商',
  `package_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '包名',
  `version` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '版本号',
  `size` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '安装包大小',
  `download_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '通用下载链接',
  `android_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '安卓下载链接',
  `ios_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'iOS下载链接',
  `apk_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'APK直接下载',
  `device_support` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'both' COMMENT '设备支持：android-安卓 ios-iOS both-双端',
  `discount_label` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '折扣标签/福利说明',
  `first_charge_domestic` decimal(4, 2) NULL DEFAULT NULL COMMENT '首充折扣-国内',
  `first_charge_overseas` decimal(4, 2) NULL DEFAULT NULL COMMENT '首充折扣-海外',
  `recharge_domestic` decimal(4, 2) NULL DEFAULT NULL COMMENT '续充折扣-国内',
  `recharge_overseas` decimal(4, 2) NULL DEFAULT NULL COMMENT '续充折扣-海外',
  `has_support` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '是否有扶持：0-无 1-有',
  `support_desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '扶持说明',
  `has_low_deduct_coupon` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '是否有低扣券领取：0-无 1-有',
  `low_deduct_coupon_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '低扣券领取链接',
  `source_box_id` bigint NULL DEFAULT NULL COMMENT '首次/最近覆盖从哪个盒子导入（主要用于区分渠道特殊数据）',
  `download_count` int NULL DEFAULT 0 COMMENT '下载次数',
  `rating` decimal(2, 1) NULL DEFAULT 0.0 COMMENT '评分（0-5）',
  `features` json NULL COMMENT '游戏特性（JSON数组）',
  `tags` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标签，逗号分隔',
  `launch_time` datetime NULL DEFAULT NULL COMMENT '上架/开服时间',
  `is_new` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '是否新游：0-否 1-是',
  `is_hot` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '是否热门：0-否 1-是',
  `is_recommend` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '是否推荐：0-否 1-是',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '状态：0-下架 1-上架',
  `platform` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '来源平台：u2game/milu/277sy/dongyouxi',
  `source_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '源平台游戏ID',
  `last_sync_time` datetime NULL DEFAULT NULL COMMENT '最后同步时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '删除标志',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '备注（支持保存导入源数据）',
  `subtitle_key` varchar(201) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci GENERATED ALWAYS AS (coalesce(`subtitle`,_utf8mb4'')) VIRTUAL COMMENT '副标题索引键（NULL映射为空串，用于唯一约束）' NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_site_id`(`site_id` ASC) USING BTREE,
  INDEX `idx_name`(`name` ASC) USING BTREE,
  INDEX `idx_game_type`(`game_type` ASC) USING BTREE,
  INDEX `idx_device_support`(`device_support` ASC) USING BTREE,
  INDEX `idx_launch_time`(`launch_time` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_platform`(`platform` ASC) USING BTREE,
  INDEX `idx_source_id`(`source_id` ASC) USING BTREE,
  INDEX `idx_source_box_id`(`source_box_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 32999 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '游戏表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_games
-- ----------------------------
INSERT INTO `gb_games` VALUES (32350, 9, '王者荣耀', NULL, NULL, NULL, 'official', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'both', NULL, NULL, NULL, NULL, NULL, '0', NULL, '0', NULL, NULL, 0, 5.0, NULL, NULL, NULL, '0', '0', '0', 0, '1', NULL, NULL, NULL, '0', 'admin', '2026-03-24 16:15:56', '', '2026-03-24 16:15:57', NULL, DEFAULT);

-- ----------------------------
-- Table structure for gb_import_batch
-- ----------------------------
DROP TABLE IF EXISTS `gb_import_batch`;
CREATE TABLE `gb_import_batch`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `batch_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '批次号（UUID）',
  `box_id` bigint NULL DEFAULT NULL COMMENT '关联盒子ID',
  `box_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '盒子名称（冗余记录）',
  `site_id` bigint NULL DEFAULT NULL COMMENT '关联网站ID',
  `site_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '网站名称（冗余记录）',
  `platform_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '来源平台类型',
  `total_count` int NOT NULL DEFAULT 0 COMMENT '处理总条数',
  `new_count` int NOT NULL DEFAULT 0 COMMENT '新增条数',
  `updated_count` int NOT NULL DEFAULT 0 COMMENT '更新条数',
  `skipped_count` int NOT NULL DEFAULT 0 COMMENT '跳过条数',
  `failed_count` int NOT NULL DEFAULT 0 COMMENT '失败条数',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'completed' COMMENT '批次状态：completed-成功 partial_failed-部分失败',
  `summary` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '批次摘要（含错误列表JSON）',
  `reverted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已全部撤回（0-否 1-是）',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_batch_no`(`batch_no` ASC) USING BTREE,
  INDEX `idx_box_id`(`box_id` ASC) USING BTREE,
  INDEX `idx_site_id`(`site_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 234 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '游戏数据导入批次记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_import_batch
-- ----------------------------

-- ----------------------------
-- Table structure for gb_master_article_dramas
-- ----------------------------
DROP TABLE IF EXISTS `gb_master_article_dramas`;
CREATE TABLE `gb_master_article_dramas`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `master_article_id` bigint NOT NULL COMMENT '主文章内容ID',
  `drama_id` bigint NOT NULL COMMENT '剧集ID',
  `relation_source` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'manual' COMMENT '关联来源：manual-人工手动 ai-AI自动 import-批量导入 sync-同步',
  `relation_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'primary' COMMENT '关联类型：primary-主要关联 secondary-次要关联 related-相关推荐 mention-提及',
  `display_order` int NULL DEFAULT 1 COMMENT '显示顺序（用于模板变量排序，1=第一个）',
  `confidence_score` decimal(6, 4) NULL DEFAULT NULL COMMENT 'AI关联置信度（0.0000-1.0000）',
  `ai_platform_id` bigint NULL DEFAULT NULL COMMENT 'AI平台ID',
  `ai_reasoning` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'AI关联推理说明',
  `ai_metadata` json NULL COMMENT 'AI关联元数据（JSON格式）',
  `review_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '审核状态：0-待审核 1-已通过 2-已拒绝',
  `reviewed_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '审核人',
  `reviewed_at` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `review_notes` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '审核备注',
  `is_featured` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '是否推荐展示：0-否 1-是',
  `click_count` int NULL DEFAULT 0 COMMENT '点击次数',
  `last_used_at` datetime NULL DEFAULT NULL COMMENT '最后使用时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '删除标志：0-存在 2-删除',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_master_article_drama`(`master_article_id` ASC, `drama_id` ASC) USING BTREE,
  INDEX `idx_master_article_id`(`master_article_id` ASC) USING BTREE,
  INDEX `idx_drama_id`(`drama_id` ASC) USING BTREE,
  INDEX `idx_relation_source`(`relation_source` ASC) USING BTREE,
  INDEX `idx_relation_type`(`relation_type` ASC) USING BTREE,
  INDEX `idx_review_status`(`review_status` ASC) USING BTREE,
  INDEX `idx_confidence_score`(`confidence_score` DESC) USING BTREE,
  INDEX `idx_ai_platform`(`ai_platform_id` ASC) USING BTREE,
  INDEX `idx_display_order`(`master_article_id` ASC, `display_order` ASC) USING BTREE,
  INDEX `idx_del_flag`(`del_flag` ASC) USING BTREE,
  CONSTRAINT `fk_master_article_dramas_master` FOREIGN KEY (`master_article_id`) REFERENCES `gb_master_articles` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '主文章-剧集关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_master_article_dramas
-- ----------------------------

-- ----------------------------
-- Table structure for gb_master_article_game_boxes
-- ----------------------------
DROP TABLE IF EXISTS `gb_master_article_game_boxes`;
CREATE TABLE `gb_master_article_game_boxes`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `master_article_id` bigint NOT NULL COMMENT '主文章ID',
  `game_box_id` bigint NOT NULL COMMENT '游戏盒子ID',
  `relation_source` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'manual' COMMENT '关联来源：manual-人工手动 ai-AI自动 import-批量导入 sync-同步',
  `relation_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'primary' COMMENT '关联类型：primary-主要关联 secondary-次要关联 related-相关推荐 mention-提及',
  `display_order` int NULL DEFAULT 1 COMMENT '显示顺序（用于模板变量排序，1=第一个）',
  `confidence_score` decimal(6, 4) NULL DEFAULT NULL COMMENT 'AI关联置信度（0.0000-1.0000）',
  `ai_platform_id` bigint NULL DEFAULT NULL COMMENT 'AI平台ID',
  `ai_reasoning` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'AI关联推理说明',
  `ai_metadata` json NULL COMMENT 'AI关联元数据（JSON格式）',
  `review_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '审核状态：0-待审核 1-已通过 2-已拒绝',
  `reviewed_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '审核人',
  `reviewed_at` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `review_notes` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '审核备注',
  `is_featured` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '是否推荐展示：0-否 1-是',
  `click_count` int NULL DEFAULT 0 COMMENT '点击次数',
  `last_used_at` datetime NULL DEFAULT NULL COMMENT '最后使用时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '删除标志：0-存在 2-删除',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_master_article_gamebox`(`master_article_id` ASC, `game_box_id` ASC) USING BTREE COMMENT '一个主文章与一个游戏盒子只能有一个关联',
  INDEX `idx_master_article_id`(`master_article_id` ASC) USING BTREE,
  INDEX `idx_game_box_id`(`game_box_id` ASC) USING BTREE,
  INDEX `idx_relation_source`(`relation_source` ASC) USING BTREE,
  INDEX `idx_relation_type`(`relation_type` ASC) USING BTREE,
  INDEX `idx_review_status`(`review_status` ASC) USING BTREE,
  INDEX `idx_confidence_score`(`confidence_score` DESC) USING BTREE,
  INDEX `idx_ai_platform`(`ai_platform_id` ASC) USING BTREE,
  INDEX `idx_display_order`(`master_article_id` ASC, `display_order` ASC) USING BTREE,
  INDEX `idx_del_flag`(`del_flag` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 103 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '主文章-游戏盒子关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_master_article_game_boxes
-- ----------------------------

-- ----------------------------
-- Table structure for gb_master_article_games
-- ----------------------------
DROP TABLE IF EXISTS `gb_master_article_games`;
CREATE TABLE `gb_master_article_games`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `master_article_id` bigint NOT NULL COMMENT '主文章ID',
  `game_id` bigint NOT NULL COMMENT '游戏ID',
  `relation_source` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'manual' COMMENT '关联来源：manual-人工手动 ai-AI自动 import-批量导入 sync-同步',
  `relation_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'primary' COMMENT '关联类型：primary-主要关联 secondary-次要关联 related-相关推荐 mention-提及',
  `display_order` int NULL DEFAULT 1 COMMENT '显示顺序（用于模板变量排序，1=第一个）',
  `confidence_score` decimal(6, 4) NULL DEFAULT NULL COMMENT 'AI关联置信度（0.0000-1.0000）',
  `ai_platform_id` bigint NULL DEFAULT NULL COMMENT 'AI平台ID',
  `ai_reasoning` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'AI关联推理说明',
  `ai_metadata` json NULL COMMENT 'AI关联元数据（JSON格式）',
  `review_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '审核状态：0-待审核 1-已通过 2-已拒绝',
  `reviewed_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '审核人',
  `reviewed_at` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `review_notes` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '审核备注',
  `is_featured` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '是否推荐展示：0-否 1-是',
  `click_count` int NULL DEFAULT 0 COMMENT '点击次数',
  `last_used_at` datetime NULL DEFAULT NULL COMMENT '最后使用时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '删除标志：0-存在 2-删除',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_master_article_game`(`master_article_id` ASC, `game_id` ASC) USING BTREE COMMENT '一个主文章与一个游戏只能有一个关联',
  INDEX `idx_master_article_id`(`master_article_id` ASC) USING BTREE,
  INDEX `idx_game_id`(`game_id` ASC) USING BTREE,
  INDEX `idx_relation_source`(`relation_source` ASC) USING BTREE,
  INDEX `idx_relation_type`(`relation_type` ASC) USING BTREE,
  INDEX `idx_review_status`(`review_status` ASC) USING BTREE,
  INDEX `idx_confidence_score`(`confidence_score` DESC) USING BTREE,
  INDEX `idx_ai_platform`(`ai_platform_id` ASC) USING BTREE,
  INDEX `idx_display_order`(`master_article_id` ASC, `display_order` ASC) USING BTREE,
  INDEX `idx_del_flag`(`del_flag` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 181 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '主文章-游戏关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_master_article_games
-- ----------------------------

-- ----------------------------
-- Table structure for gb_master_article_homepage_binding
-- ----------------------------
DROP TABLE IF EXISTS `gb_master_article_homepage_binding`;
CREATE TABLE `gb_master_article_homepage_binding`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `master_article_id` bigint NOT NULL COMMENT '主文章ID',
  `game_id` bigint NULL DEFAULT NULL COMMENT '游戏ID（与game_box_id二选一）',
  `game_box_id` bigint NULL DEFAULT NULL COMMENT '游戏盒子ID（与game_id二选一）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_master_article`(`master_article_id` ASC) USING BTREE COMMENT '一个主文章只能绑定一个主页',
  UNIQUE INDEX `uk_game_homepage`(`game_id` ASC) USING BTREE COMMENT '一个游戏只能有一个主文章作为主页',
  UNIQUE INDEX `uk_gamebox_homepage`(`game_box_id` ASC) USING BTREE COMMENT '一个游戏盒子只能有一个主文章作为主页',
  INDEX `idx_master_article_id`(`master_article_id` ASC) USING BTREE,
  INDEX `idx_game_id`(`game_id` ASC) USING BTREE,
  INDEX `idx_game_box_id`(`game_box_id` ASC) USING BTREE,
  CONSTRAINT `chk_master_article_binding_target` CHECK (((`game_id` is not null) and (`game_box_id` is null)) or ((`game_id` is null) and (`game_box_id` is not null)))
) ENGINE = InnoDB AUTO_INCREMENT = 48 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '主文章主页绑定表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_master_article_homepage_binding
-- ----------------------------
INSERT INTO `gb_master_article_homepage_binding` VALUES (41, 1, 461, NULL, '', '2026-01-15 12:03:24', '', NULL, NULL);

-- ----------------------------
-- Table structure for gb_master_articles
-- ----------------------------
DROP TABLE IF EXISTS `gb_master_articles`;
CREATE TABLE `gb_master_articles`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主文章ID',
  `site_id` bigint NOT NULL DEFAULT 1 COMMENT '所属网站ID',
  `category_id` bigint NULL DEFAULT NULL COMMENT '分类ID',
  `is_ai_generated` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '是否AI生成：0-否 1-是',
  `prompt_template_id` bigint NULL DEFAULT NULL COMMENT '使用的提示词模板ID',
  `generation_count` int NULL DEFAULT 0 COMMENT '生成次数',
  `last_generation_id` bigint NULL DEFAULT NULL COMMENT '最后一次生成记录ID',
  `is_top` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '是否置顶：0-否 1-是',
  `is_recommend` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '是否推荐：0-否 1-是',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '删除标志',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `publish_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_site_id`(`site_id` ASC) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 78 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '主文章内容表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_master_articles
-- ----------------------------
INSERT INTO `gb_master_articles` VALUES (1, 1, 27, '0', NULL, 0, NULL, '0', '0', 0, '0', 'admin', '2025-12-23 09:51:21', 'admin', '2026-03-15 18:02:28', '2026-03-15 18:02:28', 'ddd');
INSERT INTO `gb_master_articles` VALUES (23, 1, 27, '0', NULL, 0, NULL, '0', '0', 0, '0', 'admin', '2026-01-16 16:11:10', 'admin', '2026-03-15 18:02:11', '2026-03-15 18:02:11', NULL);
INSERT INTO `gb_master_articles` VALUES (67, 1, 28, '0', NULL, 0, NULL, '0', '0', 0, '0', 'scheduled', '2026-03-16 00:00:09', 'admin', '2026-03-16 16:40:40', '2026-03-16 16:40:40', NULL);
INSERT INTO `gb_master_articles` VALUES (72, 1, NULL, '0', NULL, 0, NULL, '0', '0', 0, '0', 'scheduled', '2026-03-22 00:00:07', 'scheduled', '2026-03-22 00:00:07', NULL, NULL);
INSERT INTO `gb_master_articles` VALUES (73, 1, NULL, '0', NULL, 0, NULL, '0', '0', 0, '0', 'scheduled', '2026-03-22 00:00:09', 'scheduled', '2026-03-22 00:00:09', NULL, NULL);
INSERT INTO `gb_master_articles` VALUES (74, 1, NULL, '0', NULL, 0, NULL, '0', '0', 0, '0', 'scheduled', '2026-03-23 00:00:07', 'scheduled', '2026-03-23 00:00:07', NULL, NULL);
INSERT INTO `gb_master_articles` VALUES (75, 1, NULL, '0', NULL, 0, NULL, '0', '0', 0, '0', 'scheduled', '2026-03-24 00:00:07', 'scheduled', '2026-03-24 00:00:08', NULL, NULL);
INSERT INTO `gb_master_articles` VALUES (76, 1, NULL, '0', NULL, 0, NULL, '0', '0', 0, '0', 'scheduled', '2026-03-24 00:00:09', 'scheduled', '2026-03-24 00:00:09', NULL, NULL);
INSERT INTO `gb_master_articles` VALUES (77, 1, NULL, '0', NULL, 0, NULL, '0', '0', 0, '0', 'scheduled', '2026-03-25 00:00:06', 'scheduled', '2026-03-25 00:00:06', NULL, NULL);

-- ----------------------------
-- Table structure for gb_multilang_storage_configs
-- ----------------------------
DROP TABLE IF EXISTS `gb_multilang_storage_configs`;
CREATE TABLE `gb_multilang_storage_configs`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `master_article_id` bigint NOT NULL COMMENT '主文章内容ID',
  `locale` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '语言代码',
  `content_storage_config_id` bigint NULL DEFAULT NULL COMMENT '内容存储配置ID',
  `resource_storage_config_id` bigint NULL DEFAULT NULL COMMENT '资源存储配置ID',
  `storage_path_prefix` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '存储路径前缀',
  `is_active` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '是否启用：0-否 1-是',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_master_article_locale`(`master_article_id` ASC, `locale` ASC) USING BTREE,
  INDEX `idx_master_article_id`(`master_article_id` ASC) USING BTREE,
  INDEX `idx_locale`(`locale` ASC) USING BTREE,
  INDEX `idx_content_storage`(`content_storage_config_id` ASC) USING BTREE,
  INDEX `idx_resource_storage`(`resource_storage_config_id` ASC) USING BTREE,
  CONSTRAINT `fk_multilang_storage_master` FOREIGN KEY (`master_article_id`) REFERENCES `gb_master_articles` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '多语言文章存储配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_multilang_storage_configs
-- ----------------------------
INSERT INTO `gb_multilang_storage_configs` VALUES (1, 1, 'zh-CN', 1, NULL, NULL, '1', 'admin', '2025-12-23 09:51:21', '', '2026-01-12 22:12:42', NULL);

-- ----------------------------
-- Table structure for gb_promotion_link_configs
-- ----------------------------
DROP TABLE IF EXISTS `gb_promotion_link_configs`;
CREATE TABLE `gb_promotion_link_configs`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `platform` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '平台标识：u2game/milu/277sy/dongyouxi',
  `resource_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'game' COMMENT '资源类型：game-游戏 / box-盒子',
  `source_field` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '源字段路径（支持嵌套）',
  `link_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '推广链接键名（存储到JSON中的key）',
  `link_description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '链接描述信息',
  `link_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'url' COMMENT '链接类型：url/qrcode/text',
  `is_primary` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '是否主推广链接：0-否 1-是',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '状态：0-禁用 1-启用',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_platform_path`(`platform` ASC, `source_field` ASC) USING BTREE,
  INDEX `idx_platform`(`platform` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_platform_resource`(`platform` ASC, `resource_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 30 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '推广链接配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_promotion_link_configs
-- ----------------------------
INSERT INTO `gb_promotion_link_configs` VALUES (1, 'u2game', 'game', 'downurl', 'download_url', '通用下载链接', 'url', '1', 1, '1', '', '2026-01-25 17:26:31', '', '2026-01-25 17:26:31', NULL);
INSERT INTO `gb_promotion_link_configs` VALUES (2, 'u2game', 'game', 'dwzdownurl', 'short_download_url', '短链下载地址', 'url', '0', 2, '1', '', '2026-01-25 17:26:31', '', '2026-01-25 17:26:31', NULL);
INSERT INTO `gb_promotion_link_configs` VALUES (3, 'u2game', 'game', 'weburl', 'web_url', '网页推广链接', 'url', '0', 3, '1', '', '2026-01-25 17:26:31', '', '2026-01-25 17:26:31', NULL);
INSERT INTO `gb_promotion_link_configs` VALUES (4, 'u2game', 'game', 'url1', 'promo_url_1', '推广链接1', 'url', '0', 4, '1', '', '2026-01-25 17:26:31', '', '2026-01-25 17:26:31', NULL);
INSERT INTO `gb_promotion_link_configs` VALUES (5, 'u2game', 'game', 'url2', 'promo_url_2', '推广链接2', 'url', '0', 5, '1', '', '2026-01-25 17:26:31', '', '2026-01-25 17:26:31', NULL);
INSERT INTO `gb_promotion_link_configs` VALUES (6, 'u2game', 'game', 'url3', 'promo_url_3', '推广链接3', 'url', '0', 6, '1', '', '2026-01-25 17:26:31', '', '2026-01-25 17:26:31', NULL);
INSERT INTO `gb_promotion_link_configs` VALUES (7, 'u2game', 'game', 'url4', 'promo_url_4', '推广链接4', 'url', '0', 7, '1', '', '2026-01-25 17:26:31', '', '2026-01-25 17:26:31', NULL);
INSERT INTO `gb_promotion_link_configs` VALUES (8, 'u2game', 'game', 'url5', 'promo_url_5', '推广链接5', 'url', '0', 8, '1', '', '2026-01-25 17:26:31', '', '2026-01-25 17:26:31', NULL);
INSERT INTO `gb_promotion_link_configs` VALUES (9, 'u2game', 'game', 'qrcode', 'qrcode', '下载二维码', 'qrcode', '0', 9, '1', '', '2026-01-25 17:26:31', '', '2026-01-25 17:26:31', NULL);
INSERT INTO `gb_promotion_link_configs` VALUES (10, 'u2game', 'game', 'weburl_qr', 'web_qrcode', '网页二维码', 'qrcode', '0', 10, '1', '', '2026-01-25 17:26:31', '', '2026-01-25 17:26:31', NULL);
INSERT INTO `gb_promotion_link_configs` VALUES (11, 'u2game', 'game', 'copy', 'copy_text', '复制文案', 'text', '0', 11, '1', '', '2026-01-25 17:26:31', '', '2026-01-25 17:26:31', NULL);
INSERT INTO `gb_promotion_link_configs` VALUES (12, 'milu', 'game', 'android_download_link', 'android_download', '安卓下载链接', 'url', '1', 1, '1', '', '2026-01-25 17:26:31', '', '2026-01-25 17:26:31', NULL);
INSERT INTO `gb_promotion_link_configs` VALUES (13, 'milu', 'game', 'ios_download_link', 'ios_download', 'iOS下载链接', 'url', '0', 2, '1', '', '2026-01-25 17:26:31', '', '2026-01-25 17:26:31', NULL);
INSERT INTO `gb_promotion_link_configs` VALUES (14, 'milu', 'game', 'promotion_page_url', 'promotion_page', '推广页面链接', 'url', '0', 3, '1', '', '2026-01-25 17:26:31', '', '2026-01-25 17:26:31', NULL);
INSERT INTO `gb_promotion_link_configs` VALUES (15, 'milu', 'game', 'multi_function_link', 'multi_function', '多功能链接', 'url', '0', 4, '1', '', '2026-01-25 17:26:31', '', '2026-01-25 17:26:31', NULL);
INSERT INTO `gb_promotion_link_configs` VALUES (16, 'milu', 'game', 'material_package_url', 'material_package', '素材包下载', 'url', '0', 5, '1', '', '2026-01-25 17:26:31', '', '2026-01-25 17:26:31', NULL);
INSERT INTO `gb_promotion_link_configs` VALUES (17, '277sy', 'game', 'downurl', 'download_url', '下载链接', 'url', '1', 1, '1', '', '2026-01-25 17:26:31', '', '2026-01-25 17:26:31', NULL);
INSERT INTO `gb_promotion_link_configs` VALUES (18, '277sy', 'game', 'weburl', 'web_url', '网页链接', 'url', '0', 2, '1', '', '2026-01-25 17:26:31', '', '2026-01-25 17:26:31', NULL);
INSERT INTO `gb_promotion_link_configs` VALUES (19, '277sy', 'game', 'qrcode', 'qrcode', '下载二维码', 'qrcode', '0', 3, '1', '', '2026-01-25 17:26:31', '', '2026-01-25 17:26:31', NULL);
INSERT INTO `gb_promotion_link_configs` VALUES (20, 'dongyouxi', 'game', 'tuiguanglianjie', 'promotion_link', '推广链接', 'url', '1', 1, '1', '', '2026-01-25 17:26:31', '', '2026-01-25 17:26:31', NULL);
INSERT INTO `gb_promotion_link_configs` VALUES (21, 'u2game_box', 'game', 'c.download', 'download_url', '盒子下载链接', 'url', '1', 1, '1', '', '2026-01-25 17:26:31', '', '2026-01-25 17:26:31', NULL);
INSERT INTO `gb_promotion_link_configs` VALUES (22, 'u2game_box', 'game', 'c.openurl', 'open_url', '盒子打开链接', 'url', '0', 2, '1', '', '2026-01-25 17:26:31', '', '2026-01-25 17:26:31', NULL);
INSERT INTO `gb_promotion_link_configs` VALUES (23, 'u2game_box', 'game', 'c.url1', 'promo_url_1', '盒子推广链接1', 'url', '0', 3, '1', '', '2026-01-25 17:26:31', '', '2026-01-25 17:26:31', NULL);
INSERT INTO `gb_promotion_link_configs` VALUES (24, 'u2game_box', 'game', 'c.url2', 'promo_url_2', '盒子推广链接2', 'url', '0', 4, '1', '', '2026-01-25 17:26:31', '', '2026-01-25 17:26:31', NULL);
INSERT INTO `gb_promotion_link_configs` VALUES (25, 'u2game_box', 'game', 'c.url3', 'promo_url_3', '盒子推广链接3', 'url', '0', 5, '1', '', '2026-01-25 17:26:31', '', '2026-01-25 17:26:31', NULL);
INSERT INTO `gb_promotion_link_configs` VALUES (26, 'u2game_box', 'game', 'c.url4', 'promo_url_4', '盒子推广链接4', 'url', '0', 6, '1', '', '2026-01-25 17:26:31', '', '2026-01-25 17:26:31', NULL);
INSERT INTO `gb_promotion_link_configs` VALUES (27, 'u2game_box', 'game', 'c.url5', 'promo_url_5', '盒子推广链接5', 'url', '0', 7, '1', '', '2026-01-25 17:26:31', '', '2026-01-25 17:26:31', NULL);
INSERT INTO `gb_promotion_link_configs` VALUES (28, 'u2game_box', 'game', 'c.qrcode', 'qrcode', '盒子二维码', 'qrcode', '0', 8, '1', '', '2026-01-25 17:26:31', '', '2026-01-25 17:26:31', NULL);
INSERT INTO `gb_promotion_link_configs` VALUES (29, 'u2game_box', 'game', 'c.openqrcode', 'open_qrcode', '打开二维码', 'qrcode', '0', 9, '1', '', '2026-01-25 17:26:31', '', '2026-01-25 17:26:31', NULL);

-- ----------------------------
-- Table structure for gb_prompt_templates
-- ----------------------------
DROP TABLE IF EXISTS `gb_prompt_templates`;
CREATE TABLE `gb_prompt_templates`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `site_id` bigint NULL DEFAULT NULL COMMENT '所属网站ID',
  `category_id` bigint NULL DEFAULT NULL COMMENT '分类ID',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图标',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板名称',
  `template_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板代码',
  `template_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板类型',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '模板描述',
  `system_prompt` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '系统提示词',
  `user_prompt_template` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户提示词模板',
  `output_format` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '输出格式说明',
  `example_output` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '示例输出',
  `variables` json NULL COMMENT '模板变量定义',
  `default_values` json NULL COMMENT '变量默认值',
  `ai_platform_id` bigint NULL DEFAULT NULL COMMENT '推荐AI平台ID',
  `model_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '推荐模型',
  `temperature` decimal(3, 2) NULL DEFAULT 0.70 COMMENT '推荐温度',
  `max_tokens` int NULL DEFAULT 4096 COMMENT '推荐最大Token数',
  `use_count` int NULL DEFAULT 0 COMMENT '使用次数',
  `success_count` int NULL DEFAULT 0 COMMENT '成功次数',
  `avg_quality_score` decimal(3, 2) NULL DEFAULT NULL COMMENT '平均质量评分',
  `is_public` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '是否公开：0-私有 1-公开',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '状态：0-禁用 1-启用',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '删除标志',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_site_id`(`site_id` ASC) USING BTREE,
  INDEX `idx_template_type`(`template_type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_template_code_del`(`template_code` ASC, `del_flag` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_prompt_templates
-- ----------------------------

-- ----------------------------
-- Table structure for gb_site_ai_platform_relations
-- ----------------------------
DROP TABLE IF EXISTS `gb_site_ai_platform_relations`;
CREATE TABLE `gb_site_ai_platform_relations`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `site_id` bigint NOT NULL COMMENT '网站ID',
  `ai_platform_id` bigint NOT NULL COMMENT 'AI平台配置ID',
  `relation_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'include' COMMENT '关联类型：include-正向关联 exclude-排除默认配置',
  `is_visible` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1' COMMENT '是否可见：0-否 1-是',
  `is_editable` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1' COMMENT '是否可编辑：0-否 1-是',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_site_platform_type`(`site_id` ASC, `ai_platform_id` ASC, `relation_type` ASC) USING BTREE,
  INDEX `idx_site_id`(`site_id` ASC) USING BTREE,
  INDEX `idx_ai_platform_id`(`ai_platform_id` ASC) USING BTREE,
  INDEX `idx_relation_type`(`relation_type` ASC) USING BTREE,
  INDEX `idx_site_type`(`site_id` ASC, `relation_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 76 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '网站-AI平台配置关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_site_ai_platform_relations
-- ----------------------------

-- ----------------------------
-- Table structure for gb_site_atomic_tool_relations
-- ----------------------------
DROP TABLE IF EXISTS `gb_site_atomic_tool_relations`;
CREATE TABLE `gb_site_atomic_tool_relations`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `site_id` bigint NOT NULL COMMENT '网站ID',
  `tool_id` bigint NOT NULL COMMENT '原子工具ID',
  `relation_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'shared' COMMENT '关联类型：include-正向关联 exclude-排除默认配置',
  `is_visible` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1' COMMENT '是否可见：0-隐藏 1-显示',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_site_id`(`site_id` ASC) USING BTREE,
  INDEX `idx_tool_id`(`tool_id` ASC) USING BTREE,
  INDEX `idx_relation_type`(`relation_type` ASC) USING BTREE,
  INDEX `uk_site_tool`(`site_id` ASC, `tool_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 118 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '网站-原子工具关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_site_atomic_tool_relations
-- ----------------------------

-- ----------------------------
-- Table structure for gb_site_box_relations
-- ----------------------------
DROP TABLE IF EXISTS `gb_site_box_relations`;
CREATE TABLE `gb_site_box_relations`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `site_id` bigint NOT NULL COMMENT '网站ID',
  `box_id` bigint NOT NULL COMMENT '游戏盒子ID',
  `relation_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'include' COMMENT '关联类型：include-正向关联 exclude-排除默认配置',
  `is_visible` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '是否可见：0-隐藏 1-可见',
  `is_featured` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '是否推荐：0-否 1-是',
  `custom_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '自定义名称（可覆盖原名称）',
  `custom_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '自定义描述',
  `sort_order` int NULL DEFAULT 0 COMMENT '在该网站的排序',
  `view_count` int NULL DEFAULT 0 COMMENT '在该网站的浏览量',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '备注',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_site_box_type`(`site_id` ASC, `box_id` ASC, `relation_type` ASC) USING BTREE,
  INDEX `idx_site_id`(`site_id` ASC) USING BTREE,
  INDEX `idx_box_id`(`box_id` ASC) USING BTREE,
  INDEX `idx_is_visible`(`is_visible` ASC) USING BTREE,
  INDEX `idx_is_featured`(`is_featured` ASC) USING BTREE,
  INDEX `idx_relation_type`(`relation_type` ASC) USING BTREE,
  INDEX `idx_site_type`(`site_id` ASC, `relation_type` ASC) USING BTREE,
  CONSTRAINT `fk_site_box_box` FOREIGN KEY (`box_id`) REFERENCES `gb_game_boxes` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_site_box_site` FOREIGN KEY (`site_id`) REFERENCES `gb_sites` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 45 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '网站-游戏盒子关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_site_box_relations
-- ----------------------------

-- ----------------------------
-- Table structure for gb_site_category_relations
-- ----------------------------
DROP TABLE IF EXISTS `gb_site_category_relations`;
CREATE TABLE `gb_site_category_relations`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `site_id` bigint NOT NULL COMMENT '网站ID',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `relation_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'include' COMMENT '关联类型：include-正向关联 exclude-排除默认配置',
  `is_visible` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '是否可见：0-隐藏 1-可见',
  `is_editable` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '是否可编辑：0-只读 1-可编辑',
  `sort_order` int NULL DEFAULT 0 COMMENT '在该网站的排序',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '备注',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_site_category_type`(`site_id` ASC, `category_id` ASC, `relation_type` ASC) USING BTREE,
  INDEX `idx_site_id`(`site_id` ASC) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_is_visible`(`is_visible` ASC) USING BTREE,
  INDEX `idx_relation_type`(`relation_type` ASC) USING BTREE,
  INDEX `idx_site_category_type`(`site_id` ASC, `category_id` ASC, `relation_type` ASC) USING BTREE,
  CONSTRAINT `fk_site_category_category` FOREIGN KEY (`category_id`) REFERENCES `gb_categories` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_site_category_site` FOREIGN KEY (`site_id`) REFERENCES `gb_sites` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1041 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '网站-分类关联表-用于网站的多分类逻辑' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_site_category_relations
-- ----------------------------

-- ----------------------------
-- Table structure for gb_site_code_config
-- ----------------------------
DROP TABLE IF EXISTS `gb_site_code_config`;
CREATE TABLE `gb_site_code_config`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `site_id` bigint NOT NULL COMMENT '关联网站ID（唯一）',
  `git_provider` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Git提供商：github/gitlab/gitee',
  `git_repo_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Git仓库URL',
  `git_branch` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'main' COMMENT 'Git分支名称',
  `git_token` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Git访问令牌（加密存储）',
  `git_auto_sync` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '是否自动同步：0-否 1-是',
  `deploy_platform` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '部署平台：local/cloudflare-pages/cloudflare-workers',
  `cloudflare_account_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Cloudflare账户ID',
  `cloudflare_api_token` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Cloudflare API Token（加密存储）',
  `cloudflare_project_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Cloudflare项目名称',
  `cloudflare_project_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Cloudflare项目ID',
  `custom_domains` json NULL COMMENT '自定义域名列表（JSON数组）',
  `deploy_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'not_deployed' COMMENT '部署状态：not_deployed/deploying/deployed/failed',
  `deploy_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '部署访问URL',
  `last_deploy_time` datetime NULL DEFAULT NULL COMMENT '最后操作时间',
  `last_deploy_log` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '最后操作日志',
  `deploy_config` json NULL COMMENT '部署配置（JSON）：构建命令、环境变量等',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_site_id`(`site_id` ASC) USING BTREE,
  CONSTRAINT `fk_code_config_site` FOREIGN KEY (`site_id`) REFERENCES `gb_sites` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 79 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '网站代码管理配置表（Git仓库 + 部署配置）' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_site_code_config
-- ----------------------------
INSERT INTO `gb_site_code_config` VALUES (1, 1, 'github', 'https://github.com/MisinGuo/test.git', 'main', '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', '0', 'cloudflare-workers', '0a223df7a80ba3b519ed601835c0edbd', 'HMxwyDDLmPWHnLC2KlvPSJxqFa8LDIOcvwiIh2vy', 'test', NULL, NULL, 'failed', 'https://test.workers.dev', '2026-03-17 13:36:56', '使用包管理器: pnpm\n安装依赖...\n[执行命令] cmd /c pnpm install\nLockfile is up to date, resolution step is skipped\nProgress: resolved 1, reused 0, downloaded 0, added 0\nPackages: +993\n++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\nProgress: resolved 993, reused 374, downloaded 0, added 0\nProgress: resolved 993, reused 781, downloaded 0, added 0\nProgress: resolved 993, reused 988, downloaded 0, added 65\nProgress: resolved 993, reused 991, downloaded 0, added 241\nProgress: resolved 993, reused 991, downloaded 0, added 330\nProgress: resolved 993, reused 991, downloaded 0, added 415\nProgress: resolved 993, reused 992, downloaded 0, added 503\nProgress: resolved 993, reused 992, downloaded 0, added 692\nProgress: resolved 993, reused 993, downloaded 0, added 816\nProgress: resolved 993, reused 993, downloaded 0, added 945\nProgress: resolved 993, reused 993, downloaded 0, added 982\nProgress: resolved 993, reused 993, downloaded 0, added 990\nProgress: resolved 993, reused 993, downloaded 0, added 991\nProgress: resolved 993, reused 993, downloaded 0, added 992\nProgress: resolved 993, reused 993, downloaded 0, added 993, done\n\ndependencies:\n+ @mdx-js/loader 3.1.1\n+ @mdx-js/react 3.1.1\n+ @next/mdx 14.2.35\n+ @radix-ui/react-accordion 1.2.12\n+ @radix-ui/react-alert-dialog 1.1.15\n+ @radix-ui/react-aspect-ratio 1.1.8\n+ @radix-ui/react-avatar 1.1.11\n+ @radix-ui/react-checkbox 1.3.3\n+ @radix-ui/react-collapsible 1.1.12\n+ @radix-ui/react-context-menu 2.2.16\n+ @radix-ui/react-dialog 1.1.15\n+ @radix-ui/react-dropdown-menu 2.1.16\n+ @radix-ui/react-hover-card 1.1.15\n+ @radix-ui/react-label 2.1.8\n+ @radix-ui/react-menubar 1.1.16\n+ @radix-ui/react-navigation-menu 1.2.14\n+ @radix-ui/react-popover 1.1.15\n+ @radix-ui/react-progress 1.1.8\n+ @radix-ui/react-radio-group 1.3.8\n+ @radix-ui/react-scroll-area 1.2.10\n+ @radix-ui/react-select 2.2.6\n+ @radix-ui/react-separator 1.1.8\n+ @radix-ui/react-slider 1.3.6\n+ @radix-ui/react-slot 1.2.4\n+ @radix-ui/react-switch 1.2.6\n+ @radix-ui/react-tabs 1.1.13\n+ @radix-ui/react-toggle 1.1.10\n+ @radix-ui/react-toggle-group 1.1.11\n+ @radix-ui/react-tooltip 1.2.8\n+ class-variance-authority 0.7.1\n+ clsx 2.1.1\n+ cmdk 1.1.1\n+ embla-carousel-react 8.6.0\n+ gray-matter 4.0.3\n+ input-otp 1.4.2\n+ lucide-react 0.487.0\n+ next 14.2.35\n+ next-mdx-remote 5.0.0\n+ next-themes 0.4.6\n+ react 18.3.1\n+ react-day-picker 8.10.1\n+ react-dom 18.3.1\n+ react-hook-form 7.68.0\n+ react-markdown 10.1.0\n+ react-resizable-panels 2.1.9\n+ reading-time 1.5.0\n+ recharts 2.15.4\n+ rehype-raw 7.0.0\n+ remark-gfm 4.0.1\n+ sonner 2.0.7\n+ tailwind-merge 2.6.0\n+ vaul 1.1.2\n+ vue 3.5.25\n\ndevDependencies:\n+ @braintree/sanitize-url 7.1.1\n+ @opennextjs/cloudflare 1.14.6\n+ @types/markdown-it 14.1.2\n+ @types/node 20.19.26\n+ @types/react 18.3.27\n+ @types/react-dom 18.3.7\n+ @types/vue 2.0.0\n+ autoprefixer 10.4.22\n+ cross-env 10.1.0\n+ cytoscape 3.33.1\n+ cytoscape-cose-bilkent 4.1.0\n+ cytoscape-fcose 2.2.0\n+ markdown-it-mermaid-plugin 0.1.0\n+ mermaid 11.12.2\n+ postcss 8.5.6\n+ rimraf 6.1.3\n+ tailwindcss 3.4.19\n+ tailwindcss-animate 1.0.7\n+ typescript 5.9.3\n+ vitepress 1.6.4\n+ wrangler 4.54.0\n\n╭ Warning ─────────────────────────────────────────────────────────────────────╮\n│                                                                              │\n│   Ignored build scripts: esbuild, sharp, workerd.                            │\n│   Run \"pnpm approve-builds\" to pick which dependencies should be allowed     │\n│   to run scripts.                                                            │\n│                                                                              │\n╰──────────────────────────────────────────────────────────────────────────────╯\n\nDone in 17.1s using pnpm v10.21.0\n[退出码] 0\n执行构建: pnpm run build:cfworkers\n[执行命令] cmd /c pnpm run build:cfworkers\n\n> game-box@0.1.0 build:cfworkers D:\\ruoyi\\uploadPath\\repos\\1\n> cross-env CLOUDFLARE_WORKERS=true NODE_ENV=production opennextjs-cloudflare build\n\n\n┌─────────────────────────────┐\n│ OpenNext — Cloudflare build │\n└─────────────────────────────┘\n\nWARN OpenNext is not fully compatible with Windows.\nWARN For optimal performance, it is recommended to use Windows Subsystem for Linux (WSL).\nWARN While OpenNext may function on Windows, it could encounter unpredictable failures during runtime.\nApp directory: D:\\ruoyi\\uploadPath\\repos\\1\nNext.js version : 14.2.35\n@opennextjs/cloudflare version: 1.14.6\n@opennextjs/aws version: 3.9.6\n\n┌─────────────────────────────────┐\n│ OpenNext — Building Next.js app │\n└─────────────────────────────────┘\n\n\n> game-box@0.1.0 build D:\\ruoyi\\uploadPath\\repos\\1\n> next build\n\n  ▲ Next.js 14.2.35\n  - Environments: .env.production\n\n   Creating an optimized production build ...\n ✓ Compiled successfully\n   Linting and checking validity of types ...\n   Collecting page data ...\n[API请求] http://local.zeusai.top:8890/api/public/strategies?locale=zh-TW&pageNum=1&pageSize=50&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/strategies?locale=zh-TW&pageNum=1&pageSize=50&siteId=1\nAPI请求失败: Error: HTTP 404: \n    at l (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\chunks\\836.js:1:14620)\n    at process.processTicksAndRejections (node:internal/process/task_queues:105:5)\n    at async Object.p [as generateStaticParams] (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\guides\\[slug]\\page.js:1:2127)\n    at async buildParams (D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\build\\utils.js:1026:40)\n    at async D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\build\\utils.js:1043:33\n    at async D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\build\\utils.js:1178:114\n    at async Span.traceAsyncFn (D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\trace\\trace.js:154:20)\n[API请求] http://local.zeusai.top:8890/api/public/strategies?locale=en-US&pageNum=1&pageSize=50&siteId=1\n预构建 zh-TW 攻略失败: Error: HTTP 404: \n    at l (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\chunks\\836.js:1:14620)\n    at process.processTicksAndRejections (node:internal/process/task_queues:105:5)\n    at async Object.p [as generateStaticParams] (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\guides\\[slug]\\page.js:1:2127)\n    at async buildParams (D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\build\\utils.js:1026:40)\n    at async D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\build\\utils.js:1043:33\n    at async D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\build\\utils.js:1178:114\n    at async Span.traceAsyncFn (D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\trace\\trace.js:154:20)\nAPI请求失败: Error: HTTP 404: \n    at l (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\chunks\\836.js:1:14620)\n    at process.processTicksAndRejections (node:internal/process/task_queues:105:5)\n    at async Object.p [as generateStaticParams] (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\guides\\[slug]\\page.js:1:2127)\n    at async buildParams (D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\build\\utils.js:1026:40)\n    at async D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\build\\utils.js:1043:33\n    at async D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\build\\utils.js:1178:114\n    at async Span.traceAsyncFn (D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\trace\\trace.js:154:20)\n预构建 en-US 攻略失败: Error: HTTP 404: \n    at l (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\chunks\\836.js:1:14620)\n    at process.processTicksAndRejections (node:internal/process/task_queues:105:5)\n    at async Object.p [as generateStaticParams] (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\guides\\[slug]\\page.js:1:2127)\n    at async buildParams (D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\build\\utils.js:1026:40)\n    at async D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\build\\utils.js:1043:33\n    at async D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\build\\utils.js:1178:114\n    at async Span.traceAsyncFn (D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\trace\\trace.js:154:20)\nAPI请求失败: Error: HTTP 404: \n    at l (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\chunks\\836.js:1:14620)\n    at process.processTicksAndRejections (node:internal/process/task_queues:105:5)\n    at async Object.p [as generateStaticParams] (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\news\\[slug]\\page.js:1:2115)\n    at async buildParams (D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\build\\utils.js:1026:40)\n    at async D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\build\\utils.js:1043:33\n    at async D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\build\\utils.js:1178:114\n    at async Span.traceAsyncFn (D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\trace\\trace.js:154:20)\n预构建 zh-TW 资讯失败: Error: HTTP 404: \n    at l (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\chunks\\836.js:1:14620)\n    at process.processTicksAndRejections (node:internal/process/task_queues:105:5)\n    at async Object.p [as generateStaticParams] (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\news\\[slug]\\page.js:1:2115)\n    at async buildParams (D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\build\\utils.js:1026:40)\n    at async D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\build\\utils.js:1043:33\n    at async D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\build\\utils.js:1178:114\n    at async Span.traceAsyncFn (D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\trace\\trace.js:154:20)\n[API请求] http://local.zeusai.top:8890/api/public/strategies?locale=en-US&pageNum=1&pageSize=50&siteId=1\nAPI请求失败: Error: HTTP 404: \n    at l (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\chunks\\836.js:1:14620)\n    at process.processTicksAndRejections (node:internal/process/task_queues:105:5)\n    at async Object.p [as generateStaticParams] (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\news\\[slug]\\page.js:1:2115)\n    at async buildParams (D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\build\\utils.js:1026:40)\n    at async D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\build\\utils.js:1043:33\n    at async D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\build\\utils.js:1178:114\n    at async Span.traceAsyncFn (D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\trace\\trace.js:154:20)\n预构建 en-US 资讯失败: Error: HTTP 404: \n    at l (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\chunks\\836.js:1:14620)\n    at process.processTicksAndRejections (node:internal/process/task_queues:105:5)\n    at async Object.p [as generateStaticParams] (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\news\\[slug]\\page.js:1:2115)\n    at async buildParams (D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\build\\utils.js:1026:40)\n    at async D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\build\\utils.js:1043:33\n    at async D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\build\\utils.js:1178:114\n    at async Span.traceAsyncFn (D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\trace\\trace.js:154:20)\n   Generating static pages (0/22) ...\n[API请求] /api/public/strategy-articles?locale=en-US&pageNum=1&pageSize=200\n[API请求] http://local.zeusai.top:8890/api/public/strategies?locale=en-US&pageNum=1&pageSize=200&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/site-config?locale=en-US&siteId=1\n[API请求] /api/public/strategy-articles?locale=en-US&pageNum=1&pageSize=200\n[API请求] http://local.zeusai.top:8890/api/public/strategies?locale=en-US&pageNum=1&pageSize=200&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/site-config?locale=en-US&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/categories?locale=en-US&categoryType=game&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/site-config?locale=en-US&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/categories?locale=en-US&categoryType=game&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/site-config?locale=en-US&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/site-config?locale=zh-TW&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/site-config?locale=zh-TW&siteId=1\n[API请求] /api/public/strategy-articles?locale=zh-CN&pageNum=1&pageSize=200\n[API请求] http://local.zeusai.top:8890/api/public/strategies?locale=zh-CN&pageNum=1&pageSize=200&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/site-config?locale=zh-CN&siteId=1\n[API请求] /api/public/strategy-articles?locale=zh-CN&pageNum=1&pageSize=200\n[API请求] http://local.zeusai.top:8890/api/public/strategies?locale=zh-CN&pageNum=1&pageSize=200&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/site-config?locale=zh-CN&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/site-config?locale=en-US&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/site-config?locale=en-US&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/site-config?locale=zh-TW&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/site-config?locale=zh-TW&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/boxes?locale=en-US&pageNum=1&pageSize=50&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/site-config?locale=en-US&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/boxes?locale=en-US&pageNum=1&pageSize=50&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/site-config?locale=en-US&siteId=1\n[API请求] /api/public/strategy-articles?locale=zh-TW&pageNum=1&pageSize=200\n[API请求] http://local.zeusai.top:8890/api/public/strategies?locale=zh-TW&pageNum=1&pageSize=200&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/site-config?locale=zh-TW&siteId=1\n[API请求] /api/public/strategy-articles?locale=zh-TW&pageNum=1&pageSize=200\n[API请求] http://local.zeusai.top:8890/api/public/strategies?locale=zh-TW&pageNum=1&pageSize=200&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/site-config?locale=zh-TW&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/home?locale=zh-CN&strategyCount=6&hotGamesCount=6&articleCount=6&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/site-config?locale=zh-CN&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/home?locale=zh-CN&strategyCount=6&hotGamesCount=6&articleCount=6&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/site-config?locale=zh-CN&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/boxes?locale=zh-TW&pageNum=1&pageSize=50&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/site-config?locale=zh-TW&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/boxes?locale=zh-TW&pageNum=1&pageSize=50&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/site-config?locale=zh-TW&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/home?locale=zh-CN&strategyCount=6&hotGamesCount=6&articleCount=6&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/site-config?locale=zh-CN&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/home?locale=zh-CN&strategyCount=6&hotGamesCount=6&articleCount=6&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/site-config?locale=zh-CN&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/site-config?locale=en-US&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/categories?locale=zh-TW&categoryType=game&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/site-config?locale=zh-TW&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/categories?locale=zh-TW&categoryType=game&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/site-config?locale=zh-TW&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/site-config?locale=en-US&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/home?locale=zh-TW&strategyCount=6&hotGamesCount=6&articleCount=6&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/site-config?locale=zh-TW&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/home?locale=zh-TW&strategyCount=6&hotGamesCount=6&articleCount=6&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/site-config?locale=zh-TW&siteId=1\nAPI请求失败: Error: HTTP 404: \n    at l (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\chunks\\836.js:1:14620)\n    at process.processTicksAndRejections (node:internal/process/task_queues:105:5)\n    at async w (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\news\\page.js:1:2771)\n    at async v (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\news\\page.js:1:3269)\n获取资讯列表失败: Error: HTTP 404: \n    at l (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\chunks\\836.js:1:14620)\n    at process.processTicksAndRejections (node:internal/process/task_queues:105:5)\n    at async w (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\news\\page.js:1:2771)\n    at async v (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\news\\page.js:1:3269)\nAPI请求失败: Error: HTTP 404: \n    at l (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\chunks\\836.js:1:14620)\n    at process.processTicksAndRejections (node:internal/process/task_queues:105:5)\n    at async w (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\news\\page.js:1:2771)\n    at async v (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\news\\page.js:1:3269)\n获取资讯列表失败: Error: HTTP 404: \n    at l (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\chunks\\836.js:1:14620)\n    at process.processTicksAndRejections (node:internal/process/task_queues:105:5)\n    at async w (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\news\\page.js:1:2771)\n    at async v (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\news\\page.js:1:3269)\n[API响应] /api/public/site-config - 数据长度: 12 fields\n[API响应] /api/public/site-config - 数据长度: 12 fields\nAPI请求失败: Error: HTTP 404: \n    at s (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\chunks\\516.js:1:4093)\n    at process.processTicksAndRejections (node:internal/process/task_queues:105:5)\n    at async w (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\news\\page.js:1:2771)\n    at async v (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\news\\page.js:1:3269)\n获取资讯列表失败: Error: HTTP 404: \n    at s (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\chunks\\516.js:1:4093)\n    at process.processTicksAndRejections (node:internal/process/task_queues:105:5)\n    at async w (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\news\\page.js:1:2771)\n    at async v (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\news\\page.js:1:3269)\nAPI请求失败: Error: HTTP 404: \n    at s (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\chunks\\516.js:1:4093)\n    at process.processTicksAndRejections (node:internal/process/task_queues:105:5)\n    at async w (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\news\\page.js:1:2771)\n    at async v (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\news\\page.js:1:3269)\n获取资讯列表失败: Error: HTTP 404: \n    at s (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\chunks\\516.js:1:4093)\n    at process.processTicksAndRejections (node:internal/process/task_queues:105:5)\n    at async w (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\news\\page.js:1:2771)\n    at async v (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\news\\page.js:1:3269)\n[API响应] /api/public/site-config - 数据长度: 12 fields\n[API响应] /api/public/site-config - 数据长度: 12 fields\n[API响应] /api/public/site-config - 数据长度: 12 fields\n[API响应] /api/public/site-config - 数据长度: 12 fields\n[API响应] /api/public/site-config - 数据长度: 12 fields\n[API响应] /api/public/site-config - 数据长度: 12 fields\n[API响应] /api/public/boxes - 数据长度: 4 条记录\n[API响应] /api/public/boxes - 数据长度: 4 条记录\n[Boxes页面] API响应: { code: 200, total: 4, rowsLength: 4 }\n[Boxes页面] 数据加载成功: { boxesCount: 4, totalBoxes: 4 }\n[Boxes页面] API响应: { code: 200, total: 4, rowsLength: 4 }\n[Boxes页面] 数据加载成功: { boxesCount: 4, totalBoxes: 4 }\nAPI请求失败: Error: HTTP 404: \n    at s (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\chunks\\516.js:1:4093)\n    at process.processTicksAndRejections (node:internal/process/task_queues:105:5)\n    at async w (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\news\\page.js:1:2771)\n    at async v (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\news\\page.js:1:3269)\n获取资讯列表失败: Error: HTTP 404: \n    at s (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\chunks\\516.js:1:4093)\n    at process.processTicksAndRejections (node:internal/process/task_queues:105:5)\n    at async w (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\news\\page.js:1:2771)\n    at async v (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\news\\page.js:1:3269)\nAPI请求失败: Error: HTTP 404: \n    at s (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\chunks\\516.js:1:4093)\n    at process.processTicksAndRejections (node:internal/process/task_queues:105:5)\n    at async w (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\news\\page.js:1:2771)\n    at async v (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\news\\page.js:1:3269)\n获取资讯列表失败: Error: HTTP 404: \n    at s (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\chunks\\516.js:1:4093)\n    at process.processTicksAndRejections (node:internal/process/task_queues:105:5)\n    at async w (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\news\\page.js:1:2771)\n    at async v (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\news\\page.js:1:3269)\n[API响应] /api/public/site-config - 数据长度: 12 fields\n[API响应] /api/public/site-config - 数据长度: 12 fields\n[API响应] /api/public/categories - 数据长度: 21 条记录\n[API响应] /api/public/categories - 数据长度: 21 条记录\n[API请求] http://local.zeusai.top:8890/api/public/categories/84/games?locale=en-US&pageSize=8&pageNum=1&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/categories/85/games?locale=en-US&pageSize=8&pageNum=1&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/categories/78/games?locale=en-US&pageSize=8&pageNum=1&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/categories/84/games?locale=en-US&pageSize=8&pageNum=1&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/categories/85/games?locale=en-US&pageSize=8&pageNum=1&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/categories/78/games?locale=en-US&pageSize=8&pageNum=1&siteId=1\n[API响应] /api/public/site-config - 数据长度: 12 fields\n[API响应] /api/public/site-config - 数据长度: 12 fields\n[API响应] /api/public/home - 数据长度: 3 fields\n[API响应] /api/public/home - 数据长度: 3 fields\n[API响应] /api/public/site-config - 数据长度: 12 fields\n[API响应] /api/public/site-config - 数据长度: 12 fields\n[API响应] /api/public/site-config - 数据长度: 12 fields\n[API响应] /api/public/site-config - 数据长度: 12 fields\n   Generating static pages (5/22) \n[API响应] /api/public/site-config - 数据长度: 12 fields\n[API响应] /api/public/site-config - 数据长度: 12 fields\n[API响应] /api/public/site-config - 数据长度: 12 fields\n[API响应] /api/public/site-config - 数据长度: 12 fields\n[API请求] http://local.zeusai.top:8890/api/public/home?locale=en-US&strategyCount=6&hotGamesCount=6&articleCount=6&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/site-config?locale=en-US&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/home?locale=en-US&strategyCount=6&hotGamesCount=6&articleCount=6&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/site-config?locale=en-US&siteId=1\n[API响应] /api/public/site-config - 数据长度: 12 fields\n[API响应] /api/public/site-config - 数据长度: 12 fields\n[API请求] /api/public/strategy-articles?locale=zh-CN&pageNum=1&pageSize=200\n[API请求] http://local.zeusai.top:8890/api/public/strategies?locale=zh-CN&pageNum=1&pageSize=200&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/site-config?locale=zh-CN&siteId=1\n[API响应] /api/public/site-config - 数据长度: 12 fields\n[API请求] /api/public/strategy-articles?locale=zh-CN&pageNum=1&pageSize=200\n[API请求] http://local.zeusai.top:8890/api/public/strategies?locale=zh-CN&pageNum=1&pageSize=200&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/site-config?locale=zh-CN&siteId=1\n[API响应] /api/public/site-config - 数据长度: 12 fields\n[API响应] /api/public/site-config - 数据长度: 12 fields\n[API响应] /api/public/site-config - 数据长度: 12 fields\n[API响应] /api/public/categories - 数据长度: 21 条记录\n[API响应] /api/public/categories - 数据长度: 21 条记录\n[API请求] http://local.zeusai.top:8890/api/public/categories/84/games?locale=zh-TW&pageSize=8&pageNum=1&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/categories/85/games?locale=zh-TW&pageSize=8&pageNum=1&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/categories/78/games?locale=zh-TW&pageSize=8&pageNum=1&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/categories/84/games?locale=zh-TW&pageSize=8&pageNum=1&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/categories/85/games?locale=zh-TW&pageSize=8&pageNum=1&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/categories/78/games?locale=zh-TW&pageSize=8&pageNum=1&siteId=1\n[API响应] /api/public/categories/85/games - 数据长度: 2 条记录\n[API响应] /api/public/categories/85/games - 数据长度: 2 条记录\n[API响应] /api/public/categories/84/games - 数据长度: 4 条记录\n[API响应] /api/public/categories/84/games - 数据长度: 4 条记录\n[API响应] /api/public/categories/78/games - 数据长度: 2 条记录\n[API响应] /api/public/categories/78/games - 数据长度: 2 条记录\n[API请求] /api/public/strategy-articles?locale=zh-TW&pageNum=1&pageSize=200\n[API请求] http://local.zeusai.top:8890/api/public/strategies?locale=zh-TW&pageNum=1&pageSize=200&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/site-config?locale=zh-TW&siteId=1\n[API请求] /api/public/strategy-articles?locale=zh-TW&pageNum=1&pageSize=200\n[API请求] http://local.zeusai.top:8890/api/public/strategies?locale=zh-TW&pageNum=1&pageSize=200&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/site-config?locale=zh-TW&siteId=1\n[API响应] /api/public/site-config - 数据长度: 12 fields\n[API响应] /api/public/site-config - 数据长度: 12 fields\n[API请求] /api/public/strategy-articles?locale=en-US&pageNum=1&pageSize=200\n[API请求] http://local.zeusai.top:8890/api/public/strategies?locale=en-US&pageNum=1&pageSize=200&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/site-config?locale=en-US&siteId=1\n[API请求] /api/public/strategy-articles?locale=en-US&pageNum=1&pageSize=200\n[API请求] http://local.zeusai.top:8890/api/public/strategies?locale=en-US&pageNum=1&pageSize=200&siteId=1\n[API请求] http://local.zeusai.top:8890/api/public/site-config?locale=en-US&siteId=1\n[API响应] /api/public/site-config - 数据长度: 12 fields\n[API响应] /api/public/site-config - 数据长度: 12 fields\n[API响应] /api/public/site-config - 数据长度: 12 fields\n[API响应] /api/public/site-config - 数据长度: 12 fields\n[API响应] /api/public/home - 数据长度: 3 fields\n[API响应] /api/public/home - 数据长度: 3 fields\n[API响应] /api/public/boxes - 数据长度: 4 条记录\n[API响应] /api/public/boxes - 数据长度: 4 条记录\n[Boxes页面] API响应: { code: 200, total: 4, rowsLength: 4 }\n[Boxes页面] 数据加载成功: { boxesCount: 4, totalBoxes: 4 }\n[Boxes页面] API响应: { code: 200, total: 4, rowsLength: 4 }\n[Boxes页面] 数据加载成功: { boxesCount: 4, totalBoxes: 4 }\n[API响应] /api/public/site-config - 数据长度: 12 fields\n[API响应] /api/public/site-config - 数据长度: 12 fields\n[API响应] /api/public/categories/85/games - 数据长度: 2 条记录\n[API响应] /api/public/categories/85/games - 数据长度: 2 条记录\n[API响应] /api/public/categories/84/games - 数据长度: 4 条记录\n[API响应] /api/public/categories/84/games - 数据长度: 4 条记录\n[API响应] /api/public/categories/78/games - 数据长度: 2 条记录\n[API响应] /api/public/categories/78/games - 数据长度: 2 条记录\n[API响应] /api/public/home - 数据长度: 3 fields\n[API响应] /api/public/home - 数据长度: 3 fields\nAPI请求失败: Error: HTTP 404: \n    at s (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\chunks\\516.js:1:4093)\n    at process.processTicksAndRejections (node:internal/process/task_queues:105:5)\n    at async N (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\guides\\page.js:1:2911)\n    at async y (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\guides\\page.js:1:3537)\n获取攻略列表失败: Error: HTTP 404: \n    at s (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\chunks\\516.js:1:4093)\n    at process.processTicksAndRejections (node:internal/process/task_queues:105:5)\n    at async N (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\guides\\page.js:1:2911)\n    at async y (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\guides\\page.js:1:3537)\nAPI请求失败: Error: HTTP 404: \n    at s (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\chunks\\516.js:1:4093)\n    at process.processTicksAndRejections (node:internal/process/task_queues:105:5)\n    at async N (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\guides\\page.js:1:2911)\n    at async y (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\guides\\page.js:1:3537)\n获取攻略列表失败: Error: HTTP 404: \n    at s (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\chunks\\516.js:1:4093)\n    at process.processTicksAndRejections (node:internal/process/task_queues:105:5)\n    at async N (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\guides\\page.js:1:2911)\n    at async y (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\guides\\page.js:1:3537)\nAPI请求失败: Error: HTTP 404: \n    at l (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\chunks\\836.js:1:14620)\n    at process.processTicksAndRejections (node:internal/process/task_queues:105:5)\n    at async N (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\guides\\page.js:1:2911)\n    at async y (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\guides\\page.js:1:3537)\n获取攻略列表失败: Error: HTTP 404: \n    at l (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\chunks\\836.js:1:14620)\n    at process.processTicksAndRejections (node:internal/process/task_queues:105:5)\n    at async N (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\guides\\page.js:1:2911)\n    at async y (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\guides\\page.js:1:3537)\nAPI请求失败: Error: HTTP 404: \n    at l (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\chunks\\836.js:1:14620)\n    at process.processTicksAndRejections (node:internal/process/task_queues:105:5)\n    at async N (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\guides\\page.js:1:2911)\n    at async y (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\guides\\page.js:1:3537)\n获取攻略列表失败: Error: HTTP 404: \n    at l (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\chunks\\836.js:1:14620)\n    at process.processTicksAndRejections (node:internal/process/task_queues:105:5)\n    at async N (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\guides\\page.js:1:2911)\n    at async y (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\guides\\page.js:1:3537)\n[API响应] /api/public/site-config - 数据长度: 12 fields\n[API响应] /api/public/site-config - 数据长度: 12 fields\nAPI请求失败: Error: HTTP 404: \n    at s (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\chunks\\516.js:1:4093)\n    at process.processTicksAndRejections (node:internal/process/task_queues:105:5)\n    at async N (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\guides\\page.js:1:2911)\n    at async y (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\guides\\page.js:1:3537)\n获取攻略列表失败: Error: HTTP 404: \n    at s (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\chunks\\516.js:1:4093)\n    at process.processTicksAndRejections (node:internal/process/task_queues:105:5)\n    at async N (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\guides\\page.js:1:2911)\n    at async y (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\guides\\page.js:1:3537)\nAPI请求失败: Error: HTTP 404: \n    at s (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\chunks\\516.js:1:4093)\n    at process.processTicksAndRejections (node:internal/process/task_queues:105:5)\n    at async N (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\guides\\page.js:1:2911)\n    at async y (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\guides\\page.js:1:3537)\n获取攻略列表失败: Error: HTTP 404: \n    at s (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\chunks\\516.js:1:4093)\n    at process.processTicksAndRejections (node:internal/process/task_queues:105:5)\n    at async N (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\guides\\page.js:1:2911)\n    at async y (D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\app\\[locale]\\content\\guides\\page.js:1:3537)\n   Generating static pages (10/22) \n   Generating static pages (16/22) \n[API响应] /api/public/home - 数据长度: 3 fields\n[API响应] /api/public/home - 数据长度: 3 fields\n ✓ Generating static pages (22/22)\n   Finalizing page optimization ...\n   Collecting build traces ...\n ⚠ Failed to copy traced files for D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\pages\\_app.js Error: EPERM: operation not permitted, symlink \'D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\react@18.3.1\\node_modules\\react\' -> \'D:\\ruoyi\\uploadPath\\repos\\1\\.next\\standalone\\node_modules\\react\'\n    at async Object.symlink (node:internal/fs/promises:1008:10)\n    at async D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\build\\utils.js:1402:25\n    at async Promise.all (index 8)\n    at async handleTraceFiles (D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\build\\utils.js:1390:9)\n    at async copyTracedFiles (D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\build\\utils.js:1451:9)\n    at async D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\build\\index.js:224:9\n    at async Span.traceAsyncFn (D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\trace\\trace.js:154:20)\n    at async writeStandaloneDirectory (D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\build\\index.js:223:5)\n    at async D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\build\\index.js:2143:17\n    at async Span.traceAsyncFn (D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\trace\\trace.js:154:20) {\n  errno: -4048,\n  code: \'EPERM\',\n  syscall: \'symlink\',\n  path: \'D:\\\\ruoyi\\\\uploadPath\\\\repos\\\\1\\\\node_modules\\\\.pnpm\\\\react@18.3.1\\\\node_modules\\\\react\',\n  dest: \'D:\\\\ruoyi\\\\uploadPath\\\\repos\\\\1\\\\.next\\\\standalone\\\\node_modules\\\\react\'\n}\n ⚠ Failed to copy traced files for D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\pages\\_error.js Error: EPERM: operation not permitted, symlink \'D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\react@18.3.1\\node_modules\\react\' -> \'D:\\ruoyi\\uploadPath\\repos\\1\\.next\\standalone\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\react\'\n    at async Object.symlink (node:internal/fs/promises:1008:10)\n    at async D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\build\\utils.js:1402:25\n    at async Promise.all (index 13)\n    at async handleTraceFiles (D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\build\\utils.js:1390:9)\n    at async copyTracedFiles (D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\build\\utils.js:1451:9)\n    at async D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\build\\index.js:224:9\n    at async Span.traceAsyncFn (D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\trace\\trace.js:154:20)\n    at async writeStandaloneDirectory (D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\build\\index.js:223:5)\n    at async D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\build\\index.js:2143:17\n    at async Span.traceAsyncFn (D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\trace\\trace.js:154:20) {\n  errno: -4048,\n  code: \'EPERM\',\n  syscall: \'symlink\',\n  path: \'D:\\\\ruoyi\\\\uploadPath\\\\repos\\\\1\\\\node_modules\\\\.pnpm\\\\react@18.3.1\\\\node_modules\\\\react\',\n  dest: \'D:\\\\ruoyi\\\\uploadPath\\\\repos\\\\1\\\\.next\\\\standalone\\\\node_modules\\\\.pnpm\\\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\\\node_modules\\\\react\'\n}\n ⚠ Failed to copy traced files for D:\\ruoyi\\uploadPath\\repos\\1\\.next\\server\\pages\\_document.js Error: EPERM: operation not permitted, symlink \'D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\' -> \'D:\\ruoyi\\uploadPath\\repos\\1\\.next\\standalone\\node_modules\\next\'\n    at async Object.symlink (node:internal/fs/promises:1008:10)\n    at async D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\build\\utils.js:1402:25\n    at async Promise.all (index 36)\n    at async handleTraceFiles (D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\build\\utils.js:1390:9)\n    at async copyTracedFiles (D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\build\\utils.js:1451:9)\n    at async D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\build\\index.js:224:9\n    at async Span.traceAsyncFn (D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\trace\\trace.js:154:20)\n    at async writeStandaloneDirectory (D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\build\\index.js:223:5)\n    at async D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\build\\index.js:2143:17\n    at async Span.traceAsyncFn (D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\trace\\trace.js:154:20) {\n  errno: -4048,\n  code: \'EPERM\',\n  syscall: \'symlink\',\n  path: \'D:\\\\ruoyi\\\\uploadPath\\\\repos\\\\1\\\\node_modules\\\\.pnpm\\\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\\\node_modules\\\\next\',\n  dest: \'D:\\\\ruoyi\\\\uploadPath\\\\repos\\\\1\\\\.next\\\\standalone\\\\node_modules\\\\next\'\n}\n\n> Build error occurred\nError: EPERM: operation not permitted, symlink \'D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\@next+env@14.2.35\\node_modules\\@next\\env\' -> \'D:\\ruoyi\\uploadPath\\repos\\1\\.next\\standalone\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\@next\\env\'\n    at async Object.symlink (node:internal/fs/promises:1008:10)\n    at async D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\build\\utils.js:1402:25\n    at async Promise.all (index 84)\n    at async handleTraceFiles (D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\build\\utils.js:1390:9)\n    at async copyTracedFiles (D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\build\\utils.js:1472:5)\n    at async D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\build\\index.js:224:9\n    at async Span.traceAsyncFn (D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\trace\\trace.js:154:20)\n    at async writeStandaloneDirectory (D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\build\\index.js:223:5)\n    at async D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\build\\index.js:2143:17\n    at async Span.traceAsyncFn (D:\\ruoyi\\uploadPath\\repos\\1\\node_modules\\.pnpm\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\node_modules\\next\\dist\\trace\\trace.js:154:20) {\n  errno: -4048,\n  code: \'EPERM\',\n  syscall: \'symlink\',\n  path: \'D:\\\\ruoyi\\\\uploadPath\\\\repos\\\\1\\\\node_modules\\\\.pnpm\\\\@next+env@14.2.35\\\\node_modules\\\\@next\\\\env\',\n  dest: \'D:\\\\ruoyi\\\\uploadPath\\\\repos\\\\1\\\\.next\\\\standalone\\\\node_modules\\\\.pnpm\\\\next@14.2.35_react-dom@18.3.1_react@18.3.1__react@18.3.1\\\\node_modules\\\\@next\\\\env\'\n}\n ELIFECYCLE  Command failed with exit code 1.\nopennextjs-cloudflare build\n\nBuild an OpenNext Cloudflare worker\n\nOptions:\n      --help                        Show help                          [boolean]\n      --version                     Show version number                [boolean]\n  -c, --config                      Path to Wrangler configuration file [string]\n      --configPath                  Path to Wrangler configuration file\n                                                           [deprecated] [string]\n  -e, --env                         Wrangler environment to use for operations\n                                                                        [string]\n  -s, --skipNextBuild, --skipBuild  Skip building the Next.js app\n                                                      [boolean] [default: false]\n      --noMinify                    Disable worker minification\n                                                      [boolean] [default: false]\n      --skipWranglerConfigCheck     Skip checking for a Wrangler config\n                                                      [boolean] [default: false]\n      --openNextConfigPath          Path to the OpenNext configuration file\n                                                                        [string]\n\nError: Command failed: pnpm build\n    at genericNodeError (node:internal/errors:983:15)\n    at wrappedFn (node:internal/errors:537:14)\n    at checkExecSyncError (node:child_process:916:11)\n    at Object.execSync (node:child_process:988:15)\n    at buildNextjsApp (file:///D:/ruoyi/uploadPath/repos/1/node_modules/.pnpm/@opennextjs+aws@3.9.6_next@_aeb3730e80235177844397c5357dd819/node_modules/@opennextjs/aws/dist/build/buildNextApp.js:15:8)\n    at build (file:///D:/ruoyi/uploadPath/repos/1/node_modules/.pnpm/@opennextjs+cloudflare@1.14_adf90cc44cc5b87c6dfa43ed700fe519/node_modules/@opennextjs/cloudflare/dist/cli/build/build.js:47:9)\n    at buildCommand (file:///D:/ruoyi/uploadPath/repos/1/node_modules/.pnpm/@opennextjs+cloudflare@1.14_adf90cc44cc5b87c6dfa43ed700fe519/node_modules/@opennextjs/cloudflare/dist/cli/commands/build.js:21:11) {\n  status: 1,\n  signal: null,\n  output: [ null, null, null ],\n  pid: 8112,\n  stdout: null,\n  stderr: null\n}\n ELIFECYCLE  Command failed with exit code 1.\n[退出码] 1\n', '{\"actionRef\": \"main\", \"actionRepo\": \"https://github.com/TeRains/mid.git\", \"deployMode\": \"server\", \"actionToken\": \"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\", \"buildCommand\": \"pnpm run build:cfworkers\", \"deployCommand\": \"npx wrangler deploy\", \"actionWorkflow\": \".github/workflows/mid.yml\", \"installCommand\": \"pnpm install\", \"previewCommand\": \"pnpm run dev\", \"actionPushToken\": \"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\", \"actionPushBranch\": \"main\", \"actionPushWorkflow\": \".github/workflows/deploy-cloudflare-auto.yml\"}', '2026-02-27 22:20:57', '2026-03-17 13:36:56');

-- ----------------------------
-- Table structure for gb_site_game_relations
-- ----------------------------
DROP TABLE IF EXISTS `gb_site_game_relations`;
CREATE TABLE `gb_site_game_relations`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `site_id` bigint NOT NULL COMMENT '网站ID',
  `game_id` bigint NOT NULL COMMENT '游戏ID',
  `relation_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'include' COMMENT '关联类型：include-正向关联 exclude-排除默认配置',
  `is_visible` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '是否可见：0-隐藏 1-可见',
  `is_featured` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '是否推荐：0-否 1-是',
  `is_new` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '是否新游：0-否 1-是',
  `custom_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '自定义名称',
  `custom_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '自定义描述',
  `custom_download_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '自定义下载链接',
  `sort_order` int NULL DEFAULT 0 COMMENT '在该网站的排序',
  `view_count` int NULL DEFAULT 0 COMMENT '在该网站的浏览量',
  `download_count` int NULL DEFAULT 0 COMMENT '在该网站的下载量',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '备注',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_site_game_type`(`site_id` ASC, `game_id` ASC, `relation_type` ASC) USING BTREE,
  INDEX `idx_site_id`(`site_id` ASC) USING BTREE,
  INDEX `idx_game_id`(`game_id` ASC) USING BTREE,
  INDEX `idx_is_visible`(`is_visible` ASC) USING BTREE,
  INDEX `idx_is_featured`(`is_featured` ASC) USING BTREE,
  INDEX `idx_is_new`(`is_new` ASC) USING BTREE,
  INDEX `idx_relation_type`(`relation_type` ASC) USING BTREE,
  INDEX `idx_site_type`(`site_id` ASC, `relation_type` ASC) USING BTREE,
  CONSTRAINT `fk_site_game_game` FOREIGN KEY (`game_id`) REFERENCES `gb_games` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_site_game_site` FOREIGN KEY (`site_id`) REFERENCES `gb_sites` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 452 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '网站-游戏关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_site_game_relations
-- ----------------------------

-- ----------------------------
-- Table structure for gb_site_locales
-- ----------------------------
DROP TABLE IF EXISTS `gb_site_locales`;
CREATE TABLE `gb_site_locales`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `site_id` bigint NOT NULL COMMENT '网站ID',
  `locale` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '语言代码（如 zh-CN, en-US, ja-JP）',
  `locale_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '语言名称（如 简体中文, English）',
  `native_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '语言本地名称',
  `is_default` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '是否默认语言：0-否 1-是',
  `is_enabled` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '是否启用：0-否 1-是',
  `storage_config_id` bigint NULL DEFAULT NULL COMMENT '该语言专用存储配置ID',
  `storage_path_template` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '{locale}/{category}/{slug}' COMMENT '存储路径模板',
  `url_prefix` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'URL前缀',
  `url_template` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '/{locale}/{category}/{slug}' COMMENT '发布URL模板',
  `domain_override` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '域名覆盖',
  `seo_title_template` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'SEO标题模板',
  `seo_description_template` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'SEO描述模板',
  `ai_prompt_suffix` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'AI生成时的语言提示词后缀',
  `translation_prompt` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '翻译提示词模板',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_site_locale`(`site_id` ASC, `locale` ASC) USING BTREE,
  INDEX `idx_site_id`(`site_id` ASC) USING BTREE,
  INDEX `idx_is_enabled`(`is_enabled` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '网站语言配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_site_locales
-- ----------------------------

-- ----------------------------
-- Table structure for gb_site_master_article_relations
-- ----------------------------
DROP TABLE IF EXISTS `gb_site_master_article_relations`;
CREATE TABLE `gb_site_master_article_relations`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `site_id` bigint NOT NULL COMMENT '网站ID',
  `master_article_id` bigint NOT NULL COMMENT '主文章ID',
  `relation_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'include' COMMENT '关联类型：include-正向关联 exclude-排除默认配置',
  `is_visible` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '是否可见：0-隐藏 1-可见',
  `is_top` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '是否置顶：0-否 1-是',
  `is_recommend` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '是否推荐：0-否 1-是',
  `custom_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '自定义标题',
  `sort_order` int NULL DEFAULT 0 COMMENT '在该网站的排序',
  `view_count` int NULL DEFAULT 0 COMMENT '在该网站的浏览量',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '备注',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_site_master_article_type`(`site_id` ASC, `master_article_id` ASC, `relation_type` ASC) USING BTREE,
  INDEX `idx_site_id`(`site_id` ASC) USING BTREE,
  INDEX `idx_master_article_id`(`master_article_id` ASC) USING BTREE,
  INDEX `idx_is_visible`(`is_visible` ASC) USING BTREE,
  INDEX `idx_is_top`(`is_top` ASC) USING BTREE,
  INDEX `idx_is_recommend`(`is_recommend` ASC) USING BTREE,
  INDEX `idx_relation_type`(`relation_type` ASC) USING BTREE,
  INDEX `idx_site_type`(`site_id` ASC, `relation_type` ASC) USING BTREE,
  CONSTRAINT `fk_site_master_article_article` FOREIGN KEY (`master_article_id`) REFERENCES `gb_master_articles` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_site_master_article_site` FOREIGN KEY (`site_id`) REFERENCES `gb_sites` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 73 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '网站-主文章关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_site_master_article_relations
-- ----------------------------

-- ----------------------------
-- Table structure for gb_site_platform_relations
-- ----------------------------
DROP TABLE IF EXISTS `gb_site_platform_relations`;
CREATE TABLE `gb_site_platform_relations`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `site_id` bigint NOT NULL COMMENT '网站ID',
  `platform_id` bigint NOT NULL COMMENT 'AI平台配置ID',
  `relation_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'include' COMMENT '关联类型：include-正向关联，exclude-排除（用于排除默认配置）',
  `is_visible` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '是否可见：0-隐藏 1-显示',
  `priority` int NULL DEFAULT 100 COMMENT '优先级（数字越小优先级越高）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_site_platform_type`(`site_id` ASC, `platform_id` ASC, `relation_type` ASC) USING BTREE COMMENT '同一网站对同一配置的同一关联类型只能有一条记录',
  INDEX `idx_site_id`(`site_id` ASC) USING BTREE,
  INDEX `idx_platform_id`(`platform_id` ASC) USING BTREE,
  INDEX `idx_relation_type`(`relation_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'AI平台配置与网站关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_site_platform_relations
-- ----------------------------

-- ----------------------------
-- Table structure for gb_site_storage_config_relations
-- ----------------------------
DROP TABLE IF EXISTS `gb_site_storage_config_relations`;
CREATE TABLE `gb_site_storage_config_relations`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `site_id` bigint NOT NULL COMMENT '网站ID',
  `storage_config_id` bigint NOT NULL COMMENT '存储配置ID',
  `relation_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'include' COMMENT '关联类型：include-正向关联 exclude-排除默认配置',
  `is_visible` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1' COMMENT '是否可见：0-否 1-是',
  `is_editable` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1' COMMENT '是否可编辑：0-否 1-是',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_site_storage_type`(`site_id` ASC, `storage_config_id` ASC, `relation_type` ASC) USING BTREE,
  INDEX `idx_site_id`(`site_id` ASC) USING BTREE,
  INDEX `idx_storage_config_id`(`storage_config_id` ASC) USING BTREE,
  INDEX `idx_relation_type`(`relation_type` ASC) USING BTREE,
  INDEX `idx_site_type`(`site_id` ASC, `relation_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 92 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '网站-存储配置关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_site_storage_config_relations
-- ----------------------------
INSERT INTO `gb_site_storage_config_relations` VALUES (16, 1, 7, 'include', '1', '1', 0, NULL, NULL, '', NULL);

-- ----------------------------
-- Table structure for gb_site_title_batch_relations
-- ----------------------------
DROP TABLE IF EXISTS `gb_site_title_batch_relations`;
CREATE TABLE `gb_site_title_batch_relations`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `site_id` bigint NOT NULL COMMENT '网站ID',
  `title_batch_id` bigint NOT NULL COMMENT '标题池批次ID',
  `relation_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'exclude' COMMENT '关联类型：shared-跨站共享 exclude-排除',
  `is_visible` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '是否可见：0-隐藏 1-可见',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_site_title_batch_type`(`site_id` ASC, `title_batch_id` ASC, `relation_type` ASC) USING BTREE,
  INDEX `idx_site_id`(`site_id` ASC) USING BTREE,
  INDEX `idx_title_batch_id`(`title_batch_id` ASC) USING BTREE,
  INDEX `idx_relation_type`(`relation_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 43 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '网站-标题池批次关联表（排除关系）' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_site_title_batch_relations
-- ----------------------------
INSERT INTO `gb_site_title_batch_relations` VALUES (2, 1, 2, 'exclude', '1', 'admin', '2026-01-02 12:06:53');
INSERT INTO `gb_site_title_batch_relations` VALUES (42, 1, 7, 'include', '1', 'admin', '2026-03-04 20:10:02');

-- ----------------------------
-- Table structure for gb_site_workflow_relation
-- ----------------------------
DROP TABLE IF EXISTS `gb_site_workflow_relation`;
CREATE TABLE `gb_site_workflow_relation`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关系ID',
  `site_id` bigint NOT NULL COMMENT '网站ID',
  `workflow_id` bigint NOT NULL COMMENT '工作流ID',
  `relation_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'include' COMMENT '关系类型：include-包含 exclude-排除',
  `is_visible` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1' COMMENT '是否可见（用于跨站共享的可见性控制）：0-不可见 1-可见',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_site_workflow_relation`(`site_id` ASC, `workflow_id` ASC, `relation_type` ASC) USING BTREE,
  INDEX `idx_site_id`(`site_id` ASC) USING BTREE,
  INDEX `idx_workflow_id`(`workflow_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 97 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '网站工作流关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_site_workflow_relation
-- ----------------------------

-- ----------------------------
-- Table structure for gb_sites
-- ----------------------------
DROP TABLE IF EXISTS `gb_sites`;
CREATE TABLE `gb_sites`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '网站名称',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '站点编码',
  `category_id` bigint NULL DEFAULT NULL COMMENT '分类ID',
  `domain` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '网站域名',
  `site_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'game' COMMENT '网站类型：game-游戏推广 drama-短剧推广 mixed-混合',
  `logo_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '网站Logo',
  `favicon_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '网站Favicon',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '网站描述',
  `seo_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'SEO标题',
  `seo_keywords` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'SEO关键词',
  `seo_description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'SEO描述',
  `config` json NULL COMMENT '网站配置（JSON）',
  `storage_config_id` bigint NULL DEFAULT NULL COMMENT '默认存储配置ID',
  `default_locale` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'zh-CN' COMMENT '默认语言',
  `supported_locales` json NULL COMMENT '支持的语言列表（JSON数组）',
  `i18n_mode` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'subdirectory' COMMENT '多语言模式：subdirectory-子目录 subdomain-子域名 query-参数',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `is_personal` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否个人默认站点：0-普通站点 1-用户专属个人默认站点',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '状态：0-禁用 1-启用',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_site_type`(`site_type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_sort_order`(`sort_order` ASC) USING BTREE,
  INDEX `idx_domain_del`(`domain` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_code_del`(`code` ASC, `del_flag` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '网站表（支持Git仓库和Cloudflare部署）' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_sites
-- ----------------------------
INSERT INTO `gb_sites` VALUES (1, '我爱玩游戏', 'aiGameBox', 36, 'gamebox.zeusai.top', 'game', NULL, NULL, '精选优质游戏盒子平台，提供超值首充折扣、续充福利和海量BT游戏。汇聚热门手游攻略、折扣福利版资源，让每一位玩家都能享受极致游戏体验，畅玩不停！', '我爱玩游戏 - 游戏盒子折扣平台 | 首充续充优惠 | 热门手游攻略大全', '游戏盒子,游戏折扣平台,首充折扣,续充优惠,BT游戏,破解游戏,手游攻略,游戏福利,折扣手游,游戏推广,热门手游,游戏资源,免费手游,手机游戏,游戏下载', '我爱玩游戏是专业的游戏盒子聚合平台，汇集50+主流游戏盒子，提供超低首充折扣、续充返利和海量BT破解游戏。一站式对比游戏福利，精选热门手游攻略，让你玩转最强折扣版，省钱畅玩好游戏！', NULL, NULL, 'zh-CN', '[\"zh-CN\", \"zh-TW\", \"en-US\"]', 'subdirectory', 1, 0, '1', '0', 'admin', '2025-12-15 17:32:37', 'admin', '2026-02-28 22:56:24', '主要盒子');
INSERT INTO `gb_sites` VALUES (8, '卡牌子测试站', 'kapai', 37, 'kapai.zeusai.top', 'game', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'zh-CN', NULL, 'subdirectory', 0, 0, '1', '0', 'admin', '2026-03-17 17:17:29', '', '2026-03-17 17:17:29', NULL);
INSERT INTO `gb_sites` VALUES (9, '默认站点', '_personal_1', NULL, '_personal_1', 'personal', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'zh-CN', NULL, 'subdirectory', 0, 1, '1', '0', 'admin', '2026-03-17 20:24:19', 'admin', '2026-03-18 12:34:36', NULL);
INSERT INTO `gb_sites` VALUES (10, 'ry的个人默认站点', '_personal_2', NULL, '_personal_2', 'personal', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'zh-CN', NULL, 'subdirectory', -1, 1, '1', '0', 'ry', '2026-03-18 15:23:40', '', '2026-03-18 15:23:40', NULL);
INSERT INTO `gb_sites` VALUES (12, 'misin的个人默认站点', '_personal_101', NULL, '_personal_101', 'personal', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'zh-CN', NULL, 'subdirectory', -1, 0, '1', '0', 'misin', '2026-03-18 18:54:35', '', '2026-03-18 18:54:35', NULL);
INSERT INTO `gb_sites` VALUES (13, 'misin的个人默认站点', '_personal_102', NULL, '_personal_102', 'personal', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'zh-CN', NULL, 'subdirectory', -1, 1, '1', '0', 'misin', '2026-03-18 18:59:31', '', '2026-03-18 18:59:31', NULL);

-- ----------------------------
-- Table structure for gb_storage_configs
-- ----------------------------
DROP TABLE IF EXISTS `gb_storage_configs`;
CREATE TABLE `gb_storage_configs`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `site_id` bigint NULL DEFAULT NULL COMMENT '所属网站ID（NULL表示全局配置）',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置名称',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置代码',
  `storage_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '存储类型：github/minio/r2/oss/cos/s3',
  `storage_purpose` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'mixed' COMMENT '存储用途：article-文章 resource-资源 mixed-混合',
  `category_id` bigint NULL DEFAULT NULL COMMENT '分类ID',
  `is_default` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '是否默认：0-否 1-是',
  `priority` int NULL DEFAULT 100 COMMENT '优先级',
  `github_owner` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'GitHub 用户名/组织名',
  `github_repo` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'GitHub 仓库名',
  `github_branch` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'main' COMMENT 'GitHub 分支',
  `github_token` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'GitHub Token（加密存储）',
  `github_path_prefix` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT 'GitHub 路径前缀',
  `minio_endpoint` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'MinIO 服务地址',
  `minio_port` int NULL DEFAULT 9000 COMMENT 'MinIO 端口',
  `minio_use_ssl` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '是否使用SSL',
  `minio_access_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'MinIO Access Key',
  `minio_secret_key` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'MinIO Secret Key（加密存储）',
  `minio_bucket` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'MinIO Bucket名称',
  `minio_region` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'MinIO Region',
  `r2_account_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'R2 Account ID',
  `r2_access_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'R2 Access Key',
  `r2_secret_key` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'R2 Secret Key（加密存储）',
  `r2_bucket` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'R2 Bucket名称',
  `r2_public_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'R2 公开访问URL',
  `oss_endpoint` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'OSS Endpoint',
  `oss_access_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'OSS Access Key',
  `oss_secret_key` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'OSS Secret Key（加密存储）',
  `oss_bucket` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'OSS Bucket名称',
  `oss_region` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'OSS Region',
  `cos_secret_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'COS Secret ID',
  `cos_secret_key` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'COS Secret Key（加密存储）',
  `cos_bucket` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'COS Bucket名称',
  `cos_region` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'COS Region',
  `cdn_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'CDN加速地址',
  `custom_domain` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '自定义访问域名',
  `base_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '基础路径前缀',
  `max_file_size` bigint NULL DEFAULT 10485760 COMMENT '最大文件大小（字节）',
  `allowed_extensions` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'md,json,txt,jpg,png,gif,webp,mp4,webm' COMMENT '允许的文件扩展名',
  `capacity_limit` bigint NULL DEFAULT NULL COMMENT '容量上限（字节）',
  `used_capacity` bigint NULL DEFAULT 0 COMMENT '已使用容量（字节）',
  `file_count` int NULL DEFAULT 0 COMMENT '文件数量',
  `file_count_limit` int NULL DEFAULT NULL COMMENT '文件数量上限',
  `capacity_warning_threshold` decimal(5, 2) NULL DEFAULT 80.00 COMMENT '容量预警阈值（百分比）',
  `is_capacity_full` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '容量是否已满：0-否 1-是',
  `capacity_full_at` datetime NULL DEFAULT NULL COMMENT '容量满时间',
  `fallback_storage_id` bigint NULL DEFAULT NULL COMMENT '备用存储配置ID',
  `auto_switch_on_full` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '容量满时是否自动切换：0-否 1-是',
  `last_health_check` datetime NULL DEFAULT NULL COMMENT '最后健康检查时间',
  `health_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'unknown' COMMENT '健康状态',
  `health_message` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '健康状态信息',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '配置描述',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '状态：0-禁用 1-启用',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '删除标志',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_site_id`(`site_id` ASC) USING BTREE,
  INDEX `idx_storage_type`(`storage_type` ASC) USING BTREE,
  INDEX `idx_is_default`(`is_default` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_site_code_del`(`site_id` ASC, `code` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_code_del`(`code` ASC, `del_flag` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 57 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '对象存储配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_storage_configs
-- ----------------------------
INSERT INTO `gb_storage_configs` VALUES (1, 1, 'github', 'github', 'github', 'mixed', 58, '0', 100, 'misinguo', 'temp', 'main', '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', '', NULL, 9000, '0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'https://edgeone.gh-proxy.org/https://github.com/MisinGuo/temp/blob/main/', '', 10485760, 'md,json,txt,jpg,png,gif,webp,mp4,webm', NULL, 0, 0, NULL, 80.00, '0', NULL, NULL, '1', NULL, 'unknown', NULL, NULL, '1', '0', 'admin', '2025-12-20 22:25:59', 'admin', '2025-12-24 06:44:15');
INSERT INTO `gb_storage_configs` VALUES (2, 1, 'tebi-guogms@qq.com', 'tebi', 's3', 'mixed', 58, '0', 100, NULL, NULL, 'main', NULL, '', NULL, 9000, '0', NULL, NULL, NULL, NULL, NULL, 'QzcsTCE5STFQeinM', 'VK79cdz88ZkahX4K3Ij85eq58LfgwchIAaPm8cnO', 'tebi.zeusai.top', 'http://s3.sgp.tebi.io', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '', 10485760, 'md,json,txt,jpg,png,gif,webp,mp4,webm', NULL, 0, 0, NULL, 80.00, '0', NULL, NULL, '1', NULL, 'unknown', NULL, NULL, '1', '0', 'admin', '2025-12-21 10:23:00', 'admin', '2025-12-24 06:44:15');
INSERT INTO `gb_storage_configs` VALUES (3, 1, 'aliyun', 'aliyun', 'aliyun_oss', 'mixed', 58, '0', 100, NULL, NULL, 'main', NULL, '', NULL, 9000, '0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'oss-cn-beijing.aliyuncs.com', 'LTAI5tNGkZRyV9bgwkGiApPQ', 'KAbD2XtkggQUWUmRQZucvTYKlBEwKZ', 'rsreds', NULL, NULL, NULL, NULL, NULL, NULL, NULL, '', 10485760, 'md,json,txt,jpg,png,gif,webp,mp4,webm', NULL, 0, 0, NULL, 80.00, '0', NULL, NULL, '1', NULL, 'unknown', NULL, NULL, '1', '0', 'admin', '2025-12-21 10:41:56', 'admin', '2025-12-24 06:44:15');
INSERT INTO `gb_storage_configs` VALUES (4, 1, 'cloudflare-free', 'cloudflare', 's3', 'mixed', 58, '0', 100, NULL, NULL, 'main', NULL, '', NULL, 9000, '0', NULL, NULL, NULL, NULL, NULL, '6e2e163e92702e2428555828e79014fa', 'e32f5951030176bda401692e25e32d36d2fbb9c10df46f3f74701cd705e83df8', 'misin-cloudflare', 'https://0a223df7a80ba3b519ed601835c0edbd.r2.cloudflarestorage.com', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '', 10485760, 'md,json,txt,jpg,png,gif,webp,mp4,webm', NULL, 0, 0, NULL, 80.00, '0', NULL, NULL, '1', NULL, 'unknown', NULL, NULL, '1', '0', 'admin', '2025-12-21 10:49:40', 'admin', '2026-01-28 14:39:21');
INSERT INTO `gb_storage_configs` VALUES (5, 9, 'oracle-free', 'oracle', 's3', 'mixed', 63, '0', 100, NULL, NULL, 'main', NULL, '', NULL, 9000, '0', NULL, NULL, NULL, NULL, 'ap-sydney-1', '531a790449e951ac6410ef474b409808906b886f', '8+U20A6iyedpgrZPHfCITCCDl0OWOZh++Y+k0bN2jm0=', 'oracle-free', 'https://sdhf3mb01vew.compat.objectstorage.ap-sydney-1.oraclecloud.com', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'https://objectstorage.ap-sydney-1.oraclecloud.com/p/MuOntq0ndyeSLDUtr-SdtuB7RTKfeVa11C_NwjH5TxMDx-j7HmcpgRJR1BzScxuz/n/sdhf3mb01vew/b/oracle-free/o/', '', 10485760, 'md,json,txt,jpg,png,gif,webp,mp4,webm', NULL, 0, 0, NULL, 80.00, '0', NULL, NULL, '1', NULL, 'unknown', NULL, NULL, '1', '0', 'admin', '2025-12-21 10:58:33', 'admin', '2026-03-17 20:24:20');
INSERT INTO `gb_storage_configs` VALUES (6, 1, 'Miniio', 'Miniio', 's3', 'mixed', 58, '0', 100, NULL, NULL, 'main', NULL, '', NULL, 9000, '0', NULL, NULL, NULL, NULL, '', 'eVa7Em1wC0fAwoq14U5m', 'zkBaGDmUtfA03W9Ur4VpZpvfwZLmgpfEWiEvGuJA', 'docs', 'http://local.zeusai.top:9000', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'https://miniio.misindata.com/9000/', '/tmp', 10485760, 'md,json,txt,jpg,png,gif,webp,mp4,webm', NULL, 0, 0, NULL, 80.00, '0', NULL, NULL, '1', NULL, 'unknown', NULL, 'dd', '1', '0', 'admin', '2025-12-21 20:49:06', 'admin', '2026-01-28 14:39:23');

-- ----------------------------
-- Table structure for gb_tags
-- ----------------------------
DROP TABLE IF EXISTS `gb_tags`;
CREATE TABLE `gb_tags`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `site_id` bigint NULL DEFAULT NULL COMMENT '所属网站ID',
  `tag_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'general' COMMENT '标签类型',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签名称',
  `slug` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签标识',
  `color` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标签颜色',
  `use_count` int NULL DEFAULT 0 COMMENT '使用次数',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '删除标志',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_site_name`(`site_id` ASC, `name` ASC) USING BTREE,
  INDEX `idx_tag_type`(`tag_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '标签表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_tags
-- ----------------------------

-- ----------------------------
-- Table structure for gb_title_import_log
-- ----------------------------
DROP TABLE IF EXISTS `gb_title_import_log`;
CREATE TABLE `gb_title_import_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `import_batch` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '导入批次号',
  `import_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'json' COMMENT '导入类型: json-JSON文件 excel-Excel文件 api-API接口',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件名称',
  `file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件路径',
  `total_count` int NULL DEFAULT 0 COMMENT '总数量',
  `success_count` int NULL DEFAULT 0 COMMENT '成功数量',
  `failed_count` int NULL DEFAULT 0 COMMENT '失败数量',
  `duplicate_count` int NULL DEFAULT 0 COMMENT '重复数量',
  `import_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'processing' COMMENT '导入状态: processing-处理中 completed-已完成 failed-失败',
  `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '错误信息',
  `import_details` json NULL COMMENT '导入详情JSON',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '导入者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_import_batch`(`import_batch` ASC) USING BTREE,
  INDEX `idx_import_type`(`import_type` ASC) USING BTREE,
  INDEX `idx_import_status`(`import_status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` DESC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '标题导入日志表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_title_import_log
-- ----------------------------

-- ----------------------------
-- Table structure for gb_title_pool
-- ----------------------------
DROP TABLE IF EXISTS `gb_title_pool`;
CREATE TABLE `gb_title_pool`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文章标题',
  `keywords` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '关键词(逗号分隔)',
  `reference_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '参考内容/描述',
  `source_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '数据来源名称',
  `source_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '来源URL',
  `import_batch` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '导入批次号',
  `usage_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '使用状态: 0-未使用 1-已使用 2-已废弃',
  `used_count` int NULL DEFAULT 0 COMMENT '已使用次数',
  `used_time` datetime NULL DEFAULT NULL COMMENT '最后使用时间',
  `used_article_ids` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '使用该标题生成的文章IDs(逗号分隔)',
  `priority` int NULL DEFAULT 0 COMMENT '优先级(数值越大优先级越高)',
  `tags` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标签(逗号分隔)',
  `extra_data` json NULL COMMENT '扩展数据(JSON格式,存储其他字段)',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '删除标志: 0-存在 2-删除',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_usage_status`(`usage_status` ASC) USING BTREE,
  INDEX `idx_import_batch`(`import_batch` ASC) USING BTREE,
  INDEX `idx_priority`(`priority` DESC) USING BTREE,
  INDEX `idx_create_time`(`create_time` DESC) USING BTREE,
  INDEX `idx_del_flag`(`del_flag` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 32 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文章标题池表(存储外部提供的标题数据)' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_title_pool
-- ----------------------------
INSERT INTO `gb_title_pool` VALUES (21, 'CS2终极进阶指南：从新手到职业选手', '反恐精英,CS2,FPS,射击游戏', NULL, '手动导入', NULL, 'SITE_0_20260102123424_745', '0', 0, NULL, NULL, 10, NULL, NULL, '0', 'admin', '2026-01-02 12:34:24', '', '2026-01-02 12:34:25', NULL);
INSERT INTO `gb_title_pool` VALUES (22, 'DOTA2最强英雄推荐：2025赛季版本分析', 'DOTA2,MOBA,英雄攻略,电竞', NULL, '手动导入', NULL, 'SITE_0_20260102123424_745', '0', 0, NULL, NULL, 9, NULL, NULL, '0', 'admin', '2026-01-02 12:34:24', '', '2026-01-02 12:34:25', NULL);
INSERT INTO `gb_title_pool` VALUES (23, '绝地求生吃鸡秘籍：落地成盒终结者', 'PUBG,吃鸡,大逃杀,战术竞技', NULL, '手动导入', NULL, 'SITE_0_20260102123424_745', '0', 0, NULL, NULL, 8, NULL, NULL, '0', 'admin', '2026-01-02 12:34:24', '', '2026-01-02 12:34:25', NULL);
INSERT INTO `gb_title_pool` VALUES (24, '英雄联盟S15赛季上分攻略', 'LOL,英雄联盟,MOBA,排位', NULL, '手动导入', NULL, 'SITE_0_20260102123424_745', '0', 0, NULL, NULL, 7, NULL, NULL, '0', 'admin', '2026-01-02 12:34:24', '', '2026-01-02 12:34:25', NULL);
INSERT INTO `gb_title_pool` VALUES (25, '原神5.0版本：新角色抽卡建议', '原神,二次元,角色养成,抽卡', NULL, '手动导入', NULL, 'SITE_0_20260102123424_745', '0', 0, NULL, NULL, 6, NULL, NULL, '0', 'admin', '2026-01-02 12:34:24', '', '2026-01-02 12:34:25', NULL);
INSERT INTO `gb_title_pool` VALUES (26, '王者荣耀最新赛季英雄Tier榜', '王者荣耀,MOBA,手游,排位', NULL, '手动导入', NULL, 'SITE_0_20260102123424_745', '0', 0, NULL, NULL, 5, NULL, NULL, '0', 'admin', '2026-01-02 12:34:24', '', '2026-01-02 12:34:25', NULL);
INSERT INTO `gb_title_pool` VALUES (27, '我的世界红石教程：从入门到精通', 'Minecraft,我的世界,红石,创造', NULL, '手动导入', NULL, 'SITE_0_20260102123424_745', '0', 0, NULL, NULL, 4, NULL, NULL, '0', 'admin', '2026-01-02 12:34:24', '', '2026-01-02 12:34:25', NULL);
INSERT INTO `gb_title_pool` VALUES (28, '赛博朋克2077：全支线任务攻略', '赛博朋克2077,RPG,单机游戏', NULL, '手动导入', NULL, 'SITE_0_20260102123424_745', '0', 0, NULL, NULL, 3, NULL, NULL, '0', 'admin', '2026-01-02 12:34:24', '', '2026-01-02 12:34:25', NULL);
INSERT INTO `gb_title_pool` VALUES (29, '艾尔登法环BOSS击杀技巧大全', '艾尔登法环,魂系游戏,BOSS攻略', NULL, '手动导入', NULL, 'SITE_0_20260102123424_745', '0', 0, NULL, NULL, 2, NULL, NULL, '0', 'admin', '2026-01-02 12:34:24', '', '2026-01-02 12:34:25', NULL);
INSERT INTO `gb_title_pool` VALUES (30, '糖豆人终极淘汰赛：冠军攻略', '糖豆人,派对游戏,多人竞技', NULL, '手动导入', NULL, 'SITE_0_20260102123424_745', '0', 0, NULL, NULL, 1, NULL, NULL, '0', 'admin', '2026-01-02 12:34:24', '', '2026-01-02 12:34:25', NULL);

-- ----------------------------
-- Table structure for gb_title_pool_batch
-- ----------------------------
DROP TABLE IF EXISTS `gb_title_pool_batch`;
CREATE TABLE `gb_title_pool_batch`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `site_id` bigint NOT NULL DEFAULT 0 COMMENT '所属网站ID(0表示全局默认)',
  `batch_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '批次号(格式：SITE_{siteId}_YYYYMMDD_序号)',
  `batch_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '批次名称',
  `import_date` date NOT NULL COMMENT '导入日期',
  `import_source` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '导入来源(json/excel/manual)',
  `title_count` int NULL DEFAULT 0 COMMENT '标题数量',
  `category_id` bigint NULL DEFAULT NULL COMMENT '关联分类ID',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1' COMMENT '状态：0-禁用 1-启用',
  `is_excluded` int NULL DEFAULT 0 COMMENT '是否被排除（默认配置被某网站排除）：0-未排除 1-已排除',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_batch_code`(`batch_code` ASC) USING BTREE,
  INDEX `idx_site_id`(`site_id` ASC) USING BTREE,
  INDEX `idx_import_date`(`import_date` ASC) USING BTREE,
  INDEX `idx_site_date`(`site_id` ASC, `import_date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '标题池批次管理表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_title_pool_batch
-- ----------------------------
INSERT INTO `gb_title_pool_batch` VALUES (7, 9, 'SITE_0_20260102123424_745', 'JSON导入-20260102123424', '2026-01-02', 'json', 10, 116, '1', 0, NULL, 'admin', '2026-01-02 12:34:24', NULL, NULL);
INSERT INTO `gb_title_pool_batch` VALUES (9, 1, 'BATCH_1772625827148', 'cxv', '2026-03-04', NULL, 0, NULL, '1', 0, NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for gb_translations
-- ----------------------------
DROP TABLE IF EXISTS `gb_translations`;
CREATE TABLE `gb_translations`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `entity_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '实体类型：game, game_box, category',
  `entity_id` bigint NOT NULL COMMENT '实体ID',
  `locale` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'zh-CN' COMMENT '语言代码：zh-CN, zh-TW, en',
  `field_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字段名称：name, description等',
  `field_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '字段值',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'system' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'system' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_translation`(`entity_type` ASC, `entity_id` ASC, `locale` ASC, `field_name` ASC) USING BTREE,
  INDEX `idx_entity`(`entity_type` ASC, `entity_id` ASC) USING BTREE,
  INDEX `idx_locale`(`locale` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 76 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '通用翻译表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_translations
-- ----------------------------
INSERT INTO `gb_translations` VALUES (30, 'game', 456, 'zh-TW', 'name', '夢唐', 'system', '2026-01-11 11:24:36', 'system', '2026-01-11 11:24:36');
INSERT INTO `gb_translations` VALUES (31, 'game', 456, 'zh-TW', 'description', '《夢唐（0.05折買斷三國）》沉浸式策略博弈顛覆性三國戰場：真實還原天氣地勢系統。自由招募名將組建無雙軍團，諸葛亮智破八陣，呂佈單騎衝陣，每位武將自帶專屬戰術技能。支持百人聯盟多線作戰，既可歃血為盟重現十八路諸侯討董，亦可暗度陳倉背刺盟友。沙盤版圖隨勢力更迭動態演化，每一局都是全新的問鼎中原史詩。', 'system', '2026-01-11 11:24:36', 'system', '2026-01-11 11:24:36');
INSERT INTO `gb_translations` VALUES (32, 'game', 456, 'en', 'name', 'Mengtang', 'system', '2026-01-11 11:24:37', 'system', '2026-01-11 11:24:37');
INSERT INTO `gb_translations` VALUES (33, 'game', 456, 'en', 'description', '\"Mengtang (0.05% discount to buy out the Three Kingdoms)\" is an immersive strategic game and a subversive Three Kingdoms battlefield: a true restoration of the weather and terrain system. Freely recruit famous generals to form an unparalleled army. Zhuge Liang outsmarts eight formations, and Lu Bu rides alone to charge into the formation. Each general has his or her own unique tactical skills. Support multi-front operations for a hundred-man alliance. You can not only sacrifice your blood for the alliance to reappear the eighteen princes to challenge Dong, but also secretly assassinate Chen Cang\'s allies. The sandbox territory evolves dynamically with the change of forces, and each game is a brand new epic of the Central Plains.', 'system', '2026-01-11 11:24:38', 'system', '2026-01-11 11:24:38');
INSERT INTO `gb_translations` VALUES (34, 'game', 457, 'zh-TW', 'name', '開天霸兵（好運金手指爆爆爆）', 'system', '2026-01-11 11:24:39', 'system', '2026-01-11 11:24:39');
INSERT INTO `gb_translations` VALUES (35, 'game', 457, 'zh-TW', 'description', '開天霸兵（好運金手指爆爆爆）是一款全新超爽白票版本，靈符、平台幣、平台累充可白嫖。上線送:全屏雷電術Ai助手、全屏百分比斬殺劍靈、幻影坦克坐騎。在線十分鐘免費激活十大金手指:超級幻影、十倍攻速、全圖直飛、巨人血統、超級倍攻、雙重烈火、全屏雷電、全屏劇毒、全屏黑龍波、斷網攻擊。諸多特色玩法，又爽又耐玩!', 'system', '2026-01-11 11:24:39', 'system', '2026-01-11 11:24:39');
INSERT INTO `gb_translations` VALUES (36, 'game', 457, 'en', 'name', 'Kaitian Babing (Good Luck Golden Finger Blast Blast Blast)', 'system', '2026-01-11 11:24:40', 'system', '2026-01-11 11:24:40');
INSERT INTO `gb_translations` VALUES (37, 'game', 457, 'en', 'description', 'Kaitian Babing (Good Luck Gold Finger Blast Blast) is a brand new and super cool free ticket version. Talisman, platform currency, and accumulated platform recharge can be used for free. Online bonus: full-screen thunder and lightning AI assistant, full-screen percentage killing sword spirit, and phantom tank mount. Ten cheats can be activated online for free for ten minutes: super phantom, ten times attack speed, direct flight across the map, giant bloodline, super double attack, double fire, full screen thunder and lightning, full screen poison, full screen black dragon wave, and network disconnection attack. Many unique gameplays, fun and fun to play!', 'system', '2026-01-11 11:24:40', 'system', '2026-01-11 11:24:40');
INSERT INTO `gb_translations` VALUES (38, 'game', 458, 'zh-TW', 'name', '九界問仙（0.05折千元永久代金）', 'system', '2026-01-11 11:24:42', 'system', '2026-01-11 11:24:42');
INSERT INTO `gb_translations` VALUES (39, 'game', 458, 'zh-TW', 'description', '開局仙魔供你選擇，以無敵之姿鎮壓天驕，修道成仙，或斬盡世間枷鎖，成就無上魔尊！全新仙俠手游《九界問仙》將滿足你的幻想，你將扮演其中的修道者，尋找機緣，結識夥伴，揭開陰謀，應戰強敵，在這仙俠世界中留下你濃重的一筆！', 'system', '2026-01-11 11:24:42', 'system', '2026-01-11 11:24:42');
INSERT INTO `gb_translations` VALUES (40, 'game', 458, 'en', 'name', 'Asking the Immortals of the Nine Realms (0.05% off thousand yuan permanent deposit)', 'system', '2026-01-11 11:24:43', 'system', '2026-01-11 11:24:43');
INSERT INTO `gb_translations` VALUES (41, 'game', 458, 'en', 'description', 'At the beginning, you can choose between immortals and demons. You can suppress the genius with invincibility, practice Taoism and become an immortal, or cut off the shackles of the world and become the supreme demon! The new Xianxia mobile game \"Nine Worlds of Immortals\" will satisfy your fantasy. You will play the role of a monk, look for opportunities, make friends, uncover conspiracies, challenge powerful enemies, and leave your mark in this Xianxia world!', 'system', '2026-01-11 11:24:43', 'system', '2026-01-11 11:24:43');
INSERT INTO `gb_translations` VALUES (42, 'game', 461, 'zh-TW', 'name', '植物大戰殭屍', 'system', '2026-01-11 11:38:56', 'system', '2026-01-11 11:40:06');
INSERT INTO `gb_translations` VALUES (43, 'game', 461, 'en', 'name', 'plants vs zombies', 'system', '2026-01-11 11:38:56', 'system', '2026-01-11 11:40:06');
INSERT INTO `gb_translations` VALUES (44, 'game', 461, 'zh-TW', 'description', '植物', 'system', '2026-01-11 11:40:03', 'system', '2026-01-11 11:40:06');
INSERT INTO `gb_translations` VALUES (45, 'game', 461, 'en', 'description', 'plant', 'system', '2026-01-11 11:40:04', 'system', '2026-01-11 11:40:06');
INSERT INTO `gb_translations` VALUES (46, 'site', 1, 'zh-TW', 'seoDescription', '我愛玩遊戲是專業的遊戲盒子聚合平台，匯集50+主流遊戲盒子，提供超低首充折扣、續充返利和海量BT破解遊戲。一站式對比遊戲福利，精選熱門手游攻略，讓你玩轉最強折扣版，省錢暢玩好遊戲！', 'system', '2026-01-15 14:07:51', 'system', '2026-01-16 12:11:33');
INSERT INTO `gb_translations` VALUES (47, 'site', 1, 'zh-TW', 'name', '我愛玩遊戲', 'system', '2026-01-15 14:07:51', 'system', '2026-01-16 12:11:33');
INSERT INTO `gb_translations` VALUES (48, 'site', 1, 'zh-TW', 'description', '精選優質遊戲盒子平台，提供超值首充折扣、續充福利和海量BT遊戲。匯聚熱門手游攻略、折扣福利版資源，讓每一位玩家都能享受極致遊戲體驗，暢玩不停！', 'system', '2026-01-15 14:07:51', 'system', '2026-01-16 12:11:33');
INSERT INTO `gb_translations` VALUES (49, 'site', 1, 'zh-TW', 'remark', '主要盒子', 'system', '2026-01-15 14:07:51', 'system', '2026-01-16 12:11:33');
INSERT INTO `gb_translations` VALUES (50, 'site', 1, 'zh-TW', 'seoTitle', '我愛玩遊戲 - 遊戲盒子折扣平台 | 首充續充優惠 | 熱門手游攻略大全', 'system', '2026-01-15 14:07:51', 'system', '2026-01-16 12:11:33');
INSERT INTO `gb_translations` VALUES (51, 'site', 1, 'zh-TW', 'seoKeywords', '遊戲盒子,遊戲折扣平台,首充折扣,續充優惠,BT遊戲,破解遊戲,手游攻略,遊戲福利,折扣手游,遊戲推廣,熱門手游,遊戲資源,免費手游,手機遊戲,遊戲下載', 'system', '2026-01-15 14:07:51', 'system', '2026-01-16 12:11:33');
INSERT INTO `gb_translations` VALUES (52, 'site', 1, 'en-US', 'seoDescription', 'I Love Playing Games is a professional game box aggregation platform that brings together 50+ mainstream game boxes, providing ultra-low first-charge discounts, recharge rebates and massive BT cracked games. One-stop comparison of game benefits, selected popular mobile game guides, allowing you to play the most discounted version, save money and play great games!', 'system', '2026-01-15 14:07:57', 'system', '2026-01-16 12:11:36');
INSERT INTO `gb_translations` VALUES (53, 'site', 1, 'en-US', 'name', 'i love playing games', 'system', '2026-01-15 14:07:57', 'system', '2026-01-16 12:11:36');
INSERT INTO `gb_translations` VALUES (54, 'site', 1, 'en-US', 'description', 'Selected high-quality game box platform, providing great first-time recharge discounts, recharge benefits and massive BT games. It brings together popular mobile game guides and discounted welfare version resources, so that every player can enjoy the ultimate gaming experience and play non-stop!', 'system', '2026-01-15 14:07:57', 'system', '2026-01-16 12:11:36');
INSERT INTO `gb_translations` VALUES (55, 'site', 1, 'en-US', 'remark', 'main box', 'system', '2026-01-15 14:07:58', 'system', '2026-01-16 12:11:36');
INSERT INTO `gb_translations` VALUES (56, 'site', 1, 'en-US', 'seoTitle', 'I love playing games - Game Box Discount Platform | First and Recharge Discounts | Guidelines for Popular Mobile Games', 'system', '2026-01-15 14:07:58', 'system', '2026-01-16 12:11:37');
INSERT INTO `gb_translations` VALUES (57, 'site', 1, 'en-US', 'seoKeywords', 'Game box, game discount platform, first recharge discount, recharge discount, BT games, cracked games, mobile game guides, game benefits, discounted mobile games, game promotion, popular mobile games, game resources, free mobile games, mobile games, game downloads', 'system', '2026-01-15 14:07:58', 'system', '2026-01-16 12:11:37');
INSERT INTO `gb_translations` VALUES (58, 'game', 367, 'zh-TW', 'name', '楚漢爭霸OL手游（0.1折）', 'system', '2026-01-15 21:44:41', 'system', '2026-01-15 21:44:41');
INSERT INTO `gb_translations` VALUES (59, 'game', 367, 'zh-TW', 'description', '《楚漢爭霸OL手游》是一款古風類型的模擬古代經營遊戲，它將帶你穿越回古代，建造屬性自己的一片天地，在這裡，你可以當一個富可敵國的商業大亨，也可以組建聯盟成就一方不朽的霸業，還是會泯滅與記憶之中？這一切的一切等待是你的選擇。', 'system', '2026-01-15 21:44:41', 'system', '2026-01-15 21:44:41');
INSERT INTO `gb_translations` VALUES (60, 'game', 367, 'en-US', 'name', 'Chu-Han Zhan OL mobile game (0.1% off)', 'system', '2026-01-15 21:44:44', 'system', '2026-01-15 21:44:44');
INSERT INTO `gb_translations` VALUES (61, 'game', 367, 'en-US', 'description', '\"Chu-Han Hegemony OL Mobile Game\" is an ancient-style simulation of ancient management games. It will take you back to ancient times and build a world of your own. Here, you can be a business tycoon with great wealth, or you can form an alliance to achieve an immortal hegemony. Or will it be lost and forgotten? All that awaits is your choice.', 'system', '2026-01-15 21:44:44', 'system', '2026-01-15 21:44:44');
INSERT INTO `gb_translations` VALUES (62, 'box', 3, 'zh-TW', 'name', 'U2Game盒子', 'system', '2026-01-16 15:22:46', 'system', '2026-02-18 21:58:49');
INSERT INTO `gb_translations` VALUES (63, 'box', 3, 'en-US', 'name', 'U2Game box', 'system', '2026-01-16 15:22:47', 'system', '2026-02-18 21:58:50');
INSERT INTO `gb_translations` VALUES (64, 'site', 6, 'zh-TW', 'description', '精選優質遊戲盒子平台，提供超值首充折扣、續充福利和海量BT遊戲。匯聚熱門手游攻略、折扣福利版資源，讓每一位玩家都能享受極致遊戲體驗，暢玩不停！', 'system', '2026-03-02 21:16:50', 'system', '2026-03-02 21:16:50');
INSERT INTO `gb_translations` VALUES (65, 'site', 6, 'en-US', 'description', 'Selected high-quality game box platform, providing great first-time recharge discounts, recharge benefits and massive BT games. It brings together popular mobile game guides and discounted welfare version resources, so that every player can enjoy the ultimate gaming experience and play non-stop!', 'system', '2026-03-02 21:16:50', 'system', '2026-03-02 21:16:50');
INSERT INTO `gb_translations` VALUES (66, 'site', 6, 'en-US', 'name', 'i love playing games', 'system', '2026-03-02 21:16:50', 'system', '2026-03-02 21:16:50');
INSERT INTO `gb_translations` VALUES (67, 'site', 6, 'zh-TW', 'name', '我愛玩遊戲', 'system', '2026-03-02 21:16:50', 'system', '2026-03-02 21:16:50');
INSERT INTO `gb_translations` VALUES (68, 'site', 6, 'en-US', 'remark', 'main box', 'system', '2026-03-02 21:16:50', 'system', '2026-03-02 21:16:50');
INSERT INTO `gb_translations` VALUES (69, 'site', 6, 'zh-TW', 'remark', '主要盒子', 'system', '2026-03-02 21:16:50', 'system', '2026-03-02 21:16:50');
INSERT INTO `gb_translations` VALUES (70, 'site', 6, 'en-US', 'seoDescription', 'I Love Playing Games is a professional game box aggregation platform that brings together 50+ mainstream game boxes, providing ultra-low first-charge discounts, recharge rebates and massive BT cracked games. One-stop comparison of game benefits, selected popular mobile game guides, allowing you to play the most discounted version, save money and play great games!', 'system', '2026-03-02 21:16:50', 'system', '2026-03-02 21:16:50');
INSERT INTO `gb_translations` VALUES (71, 'site', 6, 'zh-TW', 'seoDescription', '我愛玩遊戲是專業的遊戲盒子聚合平台，匯集50+主流遊戲盒子，提供超低首充折扣、續充返利和海量BT破解遊戲。一站式對比遊戲福利，精選熱門手游攻略，讓你玩轉最強折扣版，省錢暢玩好遊戲！', 'system', '2026-03-02 21:16:50', 'system', '2026-03-02 21:16:50');
INSERT INTO `gb_translations` VALUES (72, 'site', 6, 'en-US', 'seoKeywords', 'Game box, game discount platform, first recharge discount, recharge discount, BT games, cracked games, mobile game guides, game benefits, discounted mobile games, game promotion, popular mobile games, game resources, free mobile games, mobile games, game downloads', 'system', '2026-03-02 21:16:50', 'system', '2026-03-02 21:16:50');
INSERT INTO `gb_translations` VALUES (73, 'site', 6, 'zh-TW', 'seoKeywords', '遊戲盒子,遊戲折扣平台,首充折扣,續充優惠,BT遊戲,破解遊戲,手游攻略,遊戲福利,折扣手游,遊戲推廣,熱門手游,遊戲資源,免費手游,手機遊戲,遊戲下載', 'system', '2026-03-02 21:16:50', 'system', '2026-03-02 21:16:50');
INSERT INTO `gb_translations` VALUES (74, 'site', 6, 'en-US', 'seoTitle', 'I love playing games - Game Box Discount Platform | First and Recharge Discounts | Guidelines for Popular Mobile Games', 'system', '2026-03-02 21:16:50', 'system', '2026-03-02 21:16:50');
INSERT INTO `gb_translations` VALUES (75, 'site', 6, 'zh-TW', 'seoTitle', '我愛玩遊戲 - 遊戲盒子折扣平台 | 首充續充優惠 | 熱門手游攻略大全', 'system', '2026-03-02 21:16:51', 'system', '2026-03-02 21:16:51');

-- ----------------------------
-- Table structure for gb_user_site_relation
-- ----------------------------
DROP TABLE IF EXISTS `gb_user_site_relation`;
CREATE TABLE `gb_user_site_relation`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户ID（关联 sys_user.user_id）',
  `site_id` bigint NOT NULL COMMENT '站点ID（关联 gb_sites.id）',
  `is_default` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否为该用户的默认站点：1-是（个人默认站点），0-否',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_site`(`user_id` ASC, `site_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户-站点关联表（控制用户可见站点范围）' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_user_site_relation
-- ----------------------------
INSERT INTO `gb_user_site_relation` VALUES (1, 1, 1, 0, '2026-03-17 20:24:20');
INSERT INTO `gb_user_site_relation` VALUES (2, 1, 8, 0, '2026-03-17 20:24:20');
INSERT INTO `gb_user_site_relation` VALUES (4, 1, 9, 1, '2026-03-17 20:24:20');
INSERT INTO `gb_user_site_relation` VALUES (5, 2, 10, 1, '2026-03-18 15:23:40');
INSERT INTO `gb_user_site_relation` VALUES (7, 101, 12, 1, '2026-03-18 18:54:35');
INSERT INTO `gb_user_site_relation` VALUES (8, 102, 13, 1, '2026-03-18 18:59:31');

-- ----------------------------
-- Table structure for gb_workflow
-- ----------------------------
DROP TABLE IF EXISTS `gb_workflow`;
CREATE TABLE `gb_workflow`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '工作流ID',
  `workflow_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '工作流编码（唯一标识）',
  `workflow_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '工作流名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '工作流描述',
  `trigger_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'manual' COMMENT '触发类型：manual-手动触发, scheduled-定时触发, event-事件触发',
  `schedule_expression` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Cron表达式（triggerType=scheduled时使用，如: 0 0 8 * * ?）',
  `definition` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '工作流定义（JSON格式）\r\n{\r\n  \"inputs\": [{\"name\":\"\", \"type\":\"\", \"label\":\"\", \"required\":true, \"default\":\"\"}],\r\n  \"steps\": [{\r\n    \"stepId\": \"step_1\",\r\n    \"stepName\": \"步骤名称\",\r\n    \"toolCode\": \"原子工具编码\",\r\n    \"description\": \"步骤描述\",\r\n    \"inputMapping\": {\r\n      \"toolInputParam\": {\r\n        \"source\": \"input|step_{id}|constant\",\r\n        \"value\": \"input.xxx | step_1.output.xxx | 常量值\"\r\n      }\r\n    },\r\n    \"enabled\": true,\r\n    \"continueOnError\": false,\r\n    \"timeout\": 300,\r\n    \"retry\": {\"times\": 0, \"interval\": 1000}\r\n  }],\r\n  \"outputs\": [{\"name\":\"\", \"source\":\"step_n.output.xxx\", \"description\":\"\"}]\r\n}',
  `step_count` int NULL DEFAULT 0 COMMENT '步骤数量',
  `execution_count` int NULL DEFAULT 0 COMMENT '执行次数',
  `success_count` int NULL DEFAULT 0 COMMENT '成功次数',
  `last_execution_time` datetime NULL DEFAULT NULL COMMENT '最后执行时间',
  `version` int NULL DEFAULT 1 COMMENT '工作流版本号',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'custom' COMMENT '工作流分类：custom-自定义, system-系统, template-模板',
  `tags` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签（JSON数组）',
  `enabled` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用：0-禁用 1-启用',
  `site_id` bigint NULL DEFAULT NULL COMMENT '站点ID（多站点支持）',
  `category_id` bigint NULL DEFAULT NULL COMMENT '分类ID',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_workflow_code`(`workflow_code` ASC) USING BTREE,
  INDEX `idx_trigger_type`(`trigger_type` ASC) USING BTREE,
  INDEX `idx_enabled`(`enabled` ASC) USING BTREE,
  INDEX `idx_site_id`(`site_id` ASC) USING BTREE,
  INDEX `idx_workflow_category`(`category` ASC, `enabled` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 30 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '工作流表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_workflow
-- ----------------------------
INSERT INTO `gb_workflow` VALUES (2, 'wf_1767591244559', '关键词提取文章生成', '1', 'scheduled', '0 0 0 * * ?', '{\"inputs\":[],\"steps\":[{\"stepId\":\"step_1767591393663\",\"stepName\":\"标题池读取工具\",\"toolCode\":\"fetch_titles_from_pool\",\"description\":\"从标题池中读取标题，支持按批次号、使用状态、数量等条件筛选，可用于工作流中批量获取标题进行文章生成\",\"inputMapping\":{\"batchCode\":\"\",\"usageStatus\":\"0\",\"limit\":1,\"orderBy\":\"priority_desc\",\"markAsUsed\":false},\"enabled\":true,\"continueOnError\":false},{\"stepId\":\"step_1767591433220\",\"stepName\":\"AI文章生成器\",\"toolCode\":\"ai_article_generator\",\"description\":\"根据标题和大纲生成完整文章内容。支持自定义字数、风格等参数\",\"inputMapping\":{\"title\":{\"source\":\"step_1\",\"field\":\"titles\"},\"outline\":\"\",\"wordCount\":800},\"enabled\":true,\"continueOnError\":false},{\"stepId\":\"step_1767596243275\",\"stepName\":\"保存文章工具\",\"toolCode\":\"save_article\",\"description\":\"将生成的文章内容保存到数据库，支持设置网站、分类、状态等信息，并可关联游戏、盒子、短剧\",\"inputMapping\":{\"title\":{\"source\":\"step_1\",\"field\":\"titles\"},\"content\":{\"source\":\"step_2\",\"field\":\"generatedText\"},\"siteId\":1,\"categoryId\":null,\"description\":\"\",\"keywords\":\"\",\"author\":\"\",\"coverUrl\":\"\",\"status\":\"0\",\"gameIds\":\"\",\"gameBoxIds\":\"\",\"dramaIds\":\"\"},\"enabled\":true,\"continueOnError\":false}],\"outputs\":[{\"name\":\"result\",\"source\":\"step_3.output.message\",\"description\":\"\"}]}', 3, 0, 0, NULL, 1, 'custom', NULL, 1, 9, 133, '', '2026-01-05 13:34:05', '', '2026-03-17 20:24:19', NULL);
INSERT INTO `gb_workflow` VALUES (24, 'wf_1772623429459', 'cxzcvx', 'vcszv', 'manual', NULL, '{\"inputs\":[],\"steps\":[{\"stepId\":\"step_1772623428049\",\"stepName\":\"获取用户信息\",\"toolCode\":\"api_get_user\",\"description\":\"通过JSONPlaceholder API获取用户信息（测试用）\",\"inputMapping\":{},\"enabled\":true,\"continueOnError\":false}],\"outputs\":[]}', 1, 0, 0, NULL, 1, 'custom', NULL, 1, 2, NULL, '', '2026-03-04 19:23:49', '', NULL, NULL);
INSERT INTO `gb_workflow` VALUES (25, 'wf_1772715967071', 'U2Game游戏同步', '', 'manual', '0 0 1 * * ?', '{\"inputs\":[{\"name\":\"gitUrl\",\"label\":\"仓库URL\",\"type\":\"string\",\"required\":false,\"default\":\"https://github.com/MisinGuo/GameData.git\",\"description\":\"\"},{\"name\":\"gitToken\",\"label\":\"访问令牌\",\"type\":\"string\",\"required\":false,\"default\":\"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\",\"description\":\"\"},{\"name\":\"fileUrl\",\"label\":\"文件路径\",\"type\":\"string\",\"required\":false,\"default\":\"/data/u2game_games.json\",\"description\":\"\"},{\"name\":\"branch\",\"label\":\"分支\",\"type\":\"string\",\"required\":false,\"default\":\"main\",\"description\":\"\"}],\"steps\":[{\"stepId\":\"step_1772715702913\",\"stepName\":\"获取Git仓库文件\",\"toolCode\":\"fetch_git_file\",\"description\":\"从Git仓库（GitHub/GitLab/Gitee）中获取指定文件的原始内容，用于数据同步场景。支持私有仓库（需传入访问令牌）。\",\"inputMapping\":{\"repoUrl\":{\"source\":\"workflow\",\"field\":\"gitUrl\"},\"accessToken\":{\"source\":\"workflow\",\"field\":\"gitToken\"},\"filePath\":{\"source\":\"workflow\",\"field\":\"fileUrl\"},\"branch\":{\"source\":\"workflow\",\"field\":\"branch\"}},\"enabled\":true,\"continueOnError\":false,\"stepType\":\"tool\",\"workflowCode\":\"\"},{\"stepId\":\"step_1772715892369\",\"stepName\":\"导入游戏到盒子（JSON）\",\"toolCode\":\"import_box_games\",\"description\":\"解析 JSON 字符串，按字段映射规则将游戏数据批量导入到指定游戏盒子中。支持指定 JSON 内数据路径（如 \'data\' 或 \'result.games\'），支持跳过或覆盖已存在的游戏。\",\"inputMapping\":{\"jsonData\":{\"source\":\"step_1\",\"field\":\"content\"},\"boxId\":3,\"siteId\":1,\"dataPath\":\"\",\"updateExisting\":\"true\"},\"enabled\":true,\"continueOnError\":false,\"stepType\":\"tool\",\"workflowCode\":\"\"}],\"outputs\":[{\"name\":\"success\",\"source\":\"step_2.output.success\",\"description\":\"\"}]}', 2, 0, 0, NULL, 1, 'custom', NULL, 1, 1, 136, '', '2026-03-05 21:06:07', '', '2026-03-21 19:36:20', NULL);
INSERT INTO `gb_workflow` VALUES (26, 'wf_1772855305411', '277游戏同步', '', 'manual', '0 0 1 * * ?', '{\"inputs\":[{\"name\":\"gitUrl\",\"label\":\"仓库URL\",\"type\":\"string\",\"required\":false,\"default\":\"https://github.com/MisinGuo/GameData.git\",\"description\":\"\"},{\"name\":\"gitToken\",\"label\":\"访问令牌\",\"type\":\"string\",\"required\":false,\"default\":\"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\",\"description\":\"\"},{\"name\":\"fileUrl\",\"label\":\"文件路径\",\"type\":\"string\",\"required\":false,\"default\":\"/data/277sy_games.json\",\"description\":\"\"},{\"name\":\"branch\",\"label\":\"分支\",\"type\":\"string\",\"required\":false,\"default\":\"main\",\"description\":\"\"}],\"steps\":[{\"stepId\":\"step_1772715702913\",\"stepName\":\"获取Git仓库文件\",\"toolCode\":\"fetch_git_file\",\"description\":\"从Git仓库（GitHub/GitLab/Gitee）中获取指定文件的原始内容，用于数据同步场景。支持私有仓库（需传入访问令牌）。\",\"inputMapping\":{\"repoUrl\":{\"source\":\"workflow\",\"field\":\"gitUrl\"},\"accessToken\":{\"source\":\"workflow\",\"field\":\"gitToken\"},\"filePath\":{\"source\":\"workflow\",\"field\":\"fileUrl\"},\"branch\":{\"source\":\"workflow\",\"field\":\"branch\"}},\"enabled\":true,\"continueOnError\":false,\"stepType\":\"tool\",\"workflowCode\":\"\"},{\"stepId\":\"step_1772715892369\",\"stepName\":\"导入游戏到盒子（JSON）\",\"toolCode\":\"import_box_games\",\"description\":\"解析 JSON 字符串，按字段映射规则将游戏数据批量导入到指定游戏盒子中。支持指定 JSON 内数据路径（如 \'data\' 或 \'result.games\'），支持跳过或覆盖已存在的游戏。\",\"inputMapping\":{\"jsonData\":{\"source\":\"step_1\",\"field\":\"content\"},\"boxId\":7,\"siteId\":1,\"dataPath\":\"\",\"updateExisting\":\"true\"},\"enabled\":true,\"continueOnError\":false,\"stepType\":\"tool\",\"workflowCode\":\"\"}],\"outputs\":[{\"name\":\"success\",\"source\":\"step_2.output.success\",\"description\":\"\"}]}', 2, 0, 0, NULL, 1, 'custom', NULL, 1, 1, 136, '', '2026-03-07 11:48:26', '', '2026-03-21 19:36:13', NULL);
INSERT INTO `gb_workflow` VALUES (27, 'wf_1773042094054', '嘿咕游戏同步', '', 'manual', '0 0 1 * * ?', '{\"inputs\":[{\"name\":\"gitUrl\",\"label\":\"仓库URL\",\"type\":\"string\",\"required\":false,\"default\":\"https://github.com/MisinGuo/GameData.git\",\"description\":\"\"},{\"name\":\"gitToken\",\"label\":\"访问令牌\",\"type\":\"string\",\"required\":false,\"default\":\"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\",\"description\":\"\"},{\"name\":\"fileUrl\",\"label\":\"文件路径\",\"type\":\"string\",\"required\":false,\"default\":\"/data/milu_games_full.json\",\"description\":\"\"},{\"name\":\"branch\",\"label\":\"分支\",\"type\":\"string\",\"required\":false,\"default\":\"main\",\"description\":\"\"}],\"steps\":[{\"stepId\":\"step_1772715702913\",\"stepName\":\"获取Git仓库文件\",\"toolCode\":\"fetch_git_file\",\"description\":\"从Git仓库（GitHub/GitLab/Gitee）中获取指定文件的原始内容，用于数据同步场景。支持私有仓库（需传入访问令牌）。\",\"inputMapping\":{\"repoUrl\":{\"source\":\"workflow\",\"field\":\"gitUrl\"},\"accessToken\":{\"source\":\"workflow\",\"field\":\"gitToken\"},\"filePath\":{\"source\":\"workflow\",\"field\":\"fileUrl\"},\"branch\":{\"source\":\"workflow\",\"field\":\"branch\"}},\"enabled\":true,\"continueOnError\":false,\"stepType\":\"tool\",\"workflowCode\":\"\"},{\"stepId\":\"step_1772715892369\",\"stepName\":\"导入游戏到盒子（JSON）\",\"toolCode\":\"import_box_games\",\"description\":\"解析 JSON 字符串，按字段映射规则将游戏数据批量导入到指定游戏盒子中。支持指定 JSON 内数据路径（如 \'data\' 或 \'result.games\'），支持跳过或覆盖已存在的游戏。\",\"inputMapping\":{\"jsonData\":{\"source\":\"step_1\",\"field\":\"content\"},\"boxId\":8,\"siteId\":1,\"dataPath\":\"\",\"updateExisting\":\"true\"},\"enabled\":true,\"continueOnError\":false,\"stepType\":\"tool\",\"workflowCode\":\"\"}],\"outputs\":[{\"name\":\"success\",\"source\":\"step_2.output.success\",\"description\":\"\"}]}', 2, 0, 0, NULL, 1, 'custom', NULL, 1, 1, NULL, '', '2026-03-09 15:41:34', '', '2026-03-21 19:36:07', NULL);
INSERT INTO `gb_workflow` VALUES (28, 'wf_1773042143461', '巴兔游戏同步', '', 'manual', '0 0 1 * * ?', '{\"inputs\":[{\"name\":\"gitUrl\",\"label\":\"仓库URL\",\"type\":\"string\",\"required\":false,\"default\":\"https://github.com/MisinGuo/GameData.git\",\"description\":\"\"},{\"name\":\"gitToken\",\"label\":\"访问令牌\",\"type\":\"string\",\"required\":false,\"default\":\"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\",\"description\":\"\"},{\"name\":\"fileUrl\",\"label\":\"文件路径\",\"type\":\"string\",\"required\":false,\"default\":\"/data/dongyouxi_games_full.json\",\"description\":\"\"},{\"name\":\"branch\",\"label\":\"分支\",\"type\":\"string\",\"required\":false,\"default\":\"main\",\"description\":\"\"}],\"steps\":[{\"stepId\":\"step_1772715702913\",\"stepName\":\"获取Git仓库文件\",\"toolCode\":\"fetch_git_file\",\"description\":\"从Git仓库（GitHub/GitLab/Gitee）中获取指定文件的原始内容，用于数据同步场景。支持私有仓库（需传入访问令牌）。\",\"inputMapping\":{\"repoUrl\":{\"source\":\"workflow\",\"field\":\"gitUrl\"},\"accessToken\":{\"source\":\"workflow\",\"field\":\"gitToken\"},\"filePath\":{\"source\":\"workflow\",\"field\":\"fileUrl\"},\"branch\":{\"source\":\"workflow\",\"field\":\"branch\"}},\"enabled\":true,\"continueOnError\":false,\"stepType\":\"tool\",\"workflowCode\":\"\"},{\"stepId\":\"step_1772715892369\",\"stepName\":\"导入游戏到盒子（JSON）\",\"toolCode\":\"import_box_games\",\"description\":\"解析 JSON 字符串，按字段映射规则将游戏数据批量导入到指定游戏盒子中。支持指定 JSON 内数据路径（如 \'data\' 或 \'result.games\'），支持跳过或覆盖已存在的游戏。\",\"inputMapping\":{\"jsonData\":{\"source\":\"step_1\",\"field\":\"content\"},\"boxId\":9,\"siteId\":1,\"dataPath\":\"\",\"updateExisting\":\"true\"},\"enabled\":true,\"continueOnError\":false,\"stepType\":\"tool\",\"workflowCode\":\"\"}],\"outputs\":[{\"name\":\"success\",\"source\":\"step_2.output.success\",\"description\":\"\"}]}', 2, 0, 0, NULL, 1, 'custom', NULL, 1, 1, NULL, '', '2026-03-09 15:42:23', '', '2026-03-21 19:36:01', NULL);
INSERT INTO `gb_workflow` VALUES (29, 'wf_1774092790037', '游戏数据同步工作流', '', 'scheduled', '0 0 3 * * ?', '{\"inputs\":[],\"steps\":[{\"stepId\":\"step_1774092737291\",\"stepType\":\"workflow\",\"stepName\":\"执行U2Game游戏同步\",\"toolCode\":\"\",\"workflowCode\":\"wf_1772715967071\",\"description\":\"执行子工作流：wf_1772715967071\",\"inputMapping\":{\"gitUrl\":\"https://github.com/MisinGuo/GameData.git\",\"gitToken\":\"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\",\"fileUrl\":\"/data/u2game_games.json\",\"branch\":\"main\"},\"enabled\":true,\"continueOnError\":false},{\"stepId\":\"step_1774092748199\",\"stepType\":\"workflow\",\"stepName\":\"执行277游戏同步\",\"toolCode\":\"\",\"workflowCode\":\"wf_1772855305411\",\"description\":\"执行子工作流：wf_1772855305411\",\"inputMapping\":{\"gitUrl\":\"https://github.com/MisinGuo/GameData.git\",\"gitToken\":\"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\",\"fileUrl\":\"/data/277sy_games.json\",\"branch\":\"main\"},\"enabled\":true,\"continueOnError\":false},{\"stepId\":\"step_1774092743278\",\"stepType\":\"workflow\",\"stepName\":\"执行嘿咕游戏同步\",\"toolCode\":\"\",\"workflowCode\":\"wf_1773042094054\",\"description\":\"执行子工作流：wf_1773042094054\",\"inputMapping\":{\"gitUrl\":\"https://github.com/MisinGuo/GameData.git\",\"gitToken\":\"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\",\"fileUrl\":\"/data/milu_games_full.json\",\"branch\":\"main\"},\"enabled\":true,\"continueOnError\":false},{\"stepId\":\"step_1774092741151\",\"stepType\":\"workflow\",\"stepName\":\"执行巴兔游戏同步\",\"toolCode\":\"\",\"workflowCode\":\"wf_1773042143461\",\"description\":\"执行子工作流：wf_1773042143461\",\"inputMapping\":{\"gitUrl\":\"https://github.com/MisinGuo/GameData.git\",\"gitToken\":\"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\",\"fileUrl\":\"/data/dongyouxi_games_full.json\",\"branch\":\"main\"},\"enabled\":true,\"continueOnError\":false}],\"outputs\":[]}', 4, 0, 0, NULL, 1, 'custom', NULL, 1, 1, NULL, '', '2026-03-21 19:33:12', '', '2026-03-24 09:51:34', NULL);

-- ----------------------------
-- Table structure for gb_workflow_execution
-- ----------------------------
DROP TABLE IF EXISTS `gb_workflow_execution`;
CREATE TABLE `gb_workflow_execution`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '执行记录ID',
  `execution_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '执行ID（唯一标识）',
  `workflow_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '工作流编码',
  `mode` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '执行模式：sync-同步, async-异步, batch-批量',
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '执行状态：pending-等待, running-运行中, success-成功, failed-失败, cancelled-已取消',
  `input_data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '输入数据（JSON）',
  `output_data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '输出数据（JSON）',
  `error` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '错误信息',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `duration` bigint NULL DEFAULT NULL COMMENT '执行耗时（毫秒）',
  `site_id` bigint NULL DEFAULT NULL COMMENT '站点ID',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_execution_id`(`execution_id` ASC) USING BTREE,
  INDEX `idx_workflow_code`(`workflow_code` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_site_id`(`site_id` ASC) USING BTREE,
  INDEX `idx_execution_status_time`(`status` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 312 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '工作流执行记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_workflow_execution
-- ----------------------------

-- ----------------------------
-- Table structure for gb_workflow_step_execution
-- ----------------------------
DROP TABLE IF EXISTS `gb_workflow_step_execution`;
CREATE TABLE `gb_workflow_step_execution`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '步骤执行记录ID',
  `execution_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '工作流执行ID',
  `step_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '步骤ID',
  `step_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '步骤名称',
  `step_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '步骤类型',
  `tool_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '工具编码',
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '执行状态',
  `input_data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '步骤输入数据（JSON）',
  `output_data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '步骤输出数据（JSON）',
  `step_config` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '步骤配置快照（JSON）',
  `input` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '步骤输入（JSON格式）',
  `output` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '步骤输出（JSON格式）',
  `error` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '错误信息',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `duration` bigint NULL DEFAULT NULL COMMENT '执行耗时（毫秒）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_execution_id`(`execution_id` ASC) USING BTREE,
  INDEX `idx_step_id`(`step_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 677 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '工作流步骤执行记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_workflow_step_execution
-- ----------------------------

-- ----------------------------
-- Table structure for gb_workflow_template
-- ----------------------------
DROP TABLE IF EXISTS `gb_workflow_template`;
CREATE TABLE `gb_workflow_template`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `template_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '模板编码',
  `template_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '模板名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '模板描述',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'general' COMMENT '模板分类',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图标',
  `definition` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '工作流定义（JSON格式，结构同gb_workflow）',
  `usage_count` int NULL DEFAULT 0 COMMENT '使用次数',
  `is_public` tinyint(1) NULL DEFAULT 1 COMMENT '是否公开',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_template_code`(`template_code` ASC) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  INDEX `idx_is_public`(`is_public` ASC) USING BTREE,
  INDEX `idx_template_category`(`category` ASC, `is_public` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '工作流模板表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_workflow_template
-- ----------------------------
INSERT INTO `gb_workflow_template` VALUES (1, 'title_to_article', '标题生成文章', '从标题获取开始，生成文章并发布的完整流程', 'content', '📝', '{\r\n  \"inputs\": [\r\n    {\"name\": \"siteId\", \"type\": \"number\", \"label\": \"目标站点\", \"required\": true},\r\n    {\"name\": \"categoryId\", \"type\": \"number\", \"label\": \"文章分类\", \"required\": false}\r\n  ],\r\n  \"steps\": [\r\n    {\r\n      \"stepId\": \"step_1\",\r\n      \"stepName\": \"获取标题\",\r\n      \"toolCode\": \"get_title_from_pool\",\r\n      \"description\": \"从标题池获取一个未使用的标题\",\r\n      \"inputMapping\": {\r\n        \"siteId\": {\"source\": \"input\", \"value\": \"input.siteId\"},\r\n        \"categoryId\": {\"source\": \"input\", \"value\": \"input.categoryId\"}\r\n      },\r\n      \"enabled\": true,\r\n      \"continueOnError\": false\r\n    },\r\n    {\r\n      \"stepId\": \"step_2\",\r\n      \"stepName\": \"AI生成文章\",\r\n      \"toolCode\": \"ai_article_generator\",\r\n      \"description\": \"根据标题使用AI生成文章内容\",\r\n      \"inputMapping\": {\r\n        \"title\": {\"source\": \"step_1\", \"value\": \"step_1.output.title\"},\r\n        \"keywords\": {\"source\": \"step_1\", \"value\": \"step_1.output.keywords\"}\r\n      },\r\n      \"enabled\": true,\r\n      \"continueOnError\": false\r\n    },\r\n    {\r\n      \"stepId\": \"step_3\",\r\n      \"stepName\": \"发布文章\",\r\n      \"toolCode\": \"publish_article\",\r\n      \"description\": \"将生成的文章发布到网站\",\r\n      \"inputMapping\": {\r\n        \"siteId\": {\"source\": \"input\", \"value\": \"input.siteId\"},\r\n        \"categoryId\": {\"source\": \"input\", \"value\": \"input.categoryId\"},\r\n        \"title\": {\"source\": \"step_1\", \"value\": \"step_1.output.title\"},\r\n        \"content\": {\"source\": \"step_2\", \"value\": \"step_2.output.generatedText\"}\r\n      },\r\n      \"enabled\": true,\r\n      \"continueOnError\": false\r\n    }\r\n  ],\r\n  \"outputs\": [\r\n    {\"name\": \"articleId\", \"source\": \"step_3.output.articleId\", \"description\": \"发布的文章ID\"},\r\n    {\"name\": \"articleUrl\", \"source\": \"step_3.output.articleUrl\", \"description\": \"文章访问URL\"}\r\n  ]\r\n}', 4, 1, 1, '', '2026-01-05 04:20:03', '', '2026-02-23 11:15:40');

-- ----------------------------
-- Table structure for gen_table
-- ----------------------------
DROP TABLE IF EXISTS `gen_table`;
CREATE TABLE `gen_table`  (
  `table_id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '表名称',
  `table_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '表描述',
  `sub_table_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '关联子表的表名',
  `sub_table_fk_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '子表关联的外键名',
  `class_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '实体类名称',
  `tpl_category` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'crud' COMMENT '使用的模板（crud单表操作 tree树表操作）',
  `tpl_web_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '前端模板类型（element-ui模版 element-plus模版）',
  `package_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '生成包路径',
  `module_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '生成模块名',
  `business_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '生成业务名',
  `function_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '生成功能名',
  `function_author` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '生成功能作者',
  `gen_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '生成代码方式（0zip压缩包 1自定义路径）',
  `gen_path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '/' COMMENT '生成路径（不填默认项目路径）',
  `options` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '其它生成选项',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`table_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '代码生成业务表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gen_table
-- ----------------------------

-- ----------------------------
-- Table structure for gen_table_column
-- ----------------------------
DROP TABLE IF EXISTS `gen_table_column`;
CREATE TABLE `gen_table_column`  (
  `column_id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_id` bigint NULL DEFAULT NULL COMMENT '归属表编号',
  `column_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '列名称',
  `column_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '列描述',
  `column_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '列类型',
  `java_type` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'JAVA类型',
  `java_field` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'JAVA字段名',
  `is_pk` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否主键（1是）',
  `is_increment` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否自增（1是）',
  `is_required` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否必填（1是）',
  `is_insert` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否为插入字段（1是）',
  `is_edit` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否编辑字段（1是）',
  `is_list` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否列表字段（1是）',
  `is_query` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否查询字段（1是）',
  `query_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'EQ' COMMENT '查询方式（等于、不等于、大于、小于、范围）',
  `html_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）',
  `dict_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '字典类型',
  `sort` int NULL DEFAULT NULL COMMENT '排序',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`column_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '代码生成业务表字段' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gen_table_column
-- ----------------------------

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
  `config_id` int NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `config_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '参数名称',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '参数键名',
  `config_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '参数键值',
  `config_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`config_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 100 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '参数配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES (1, '主框架页-默认皮肤样式名称', 'sys.index.skinName', 'skin-blue', 'Y', 'admin', '2025-12-14 20:12:29', '', NULL, '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow');
INSERT INTO `sys_config` VALUES (2, '用户管理-账号初始密码', 'sys.user.initPassword', '123456', 'Y', 'admin', '2025-12-14 20:12:29', '', NULL, '初始化密码 123456');
INSERT INTO `sys_config` VALUES (3, '主框架页-侧边栏主题', 'sys.index.sideTheme', 'theme-dark', 'Y', 'admin', '2025-12-14 20:12:29', '', NULL, '深色主题theme-dark，浅色主题theme-light');
INSERT INTO `sys_config` VALUES (4, '账号自助-验证码开关', 'sys.account.captchaEnabled', 'true', 'Y', 'admin', '2025-12-14 20:12:29', '', NULL, '是否开启验证码功能（true开启，false关闭）');
INSERT INTO `sys_config` VALUES (5, '账号自助-是否开启用户注册功能', 'sys.account.registerUser', 'false', 'Y', 'admin', '2025-12-14 20:12:29', '', NULL, '是否开启注册用户功能（true开启，false关闭）');
INSERT INTO `sys_config` VALUES (6, '用户登录-黑名单列表', 'sys.login.blackIPList', '', 'Y', 'admin', '2025-12-14 20:12:29', '', NULL, '设置登录IP黑名单限制，多个匹配项以;分隔，支持匹配（*通配、网段）');
INSERT INTO `sys_config` VALUES (7, '用户管理-初始密码修改策略', 'sys.account.initPasswordModify', '1', 'Y', 'admin', '2025-12-14 20:12:29', '', NULL, '0：初始密码修改策略关闭，没有任何提示，1：提醒用户，如果未修改初始密码，则在登录时就会提醒修改密码对话框');
INSERT INTO `sys_config` VALUES (8, '用户管理-账号密码更新周期', 'sys.account.passwordValidateDays', '0', 'Y', 'admin', '2025-12-14 20:12:29', '', NULL, '密码更新周期（填写数字，数据初始化值为0不限制，若修改必须为大于0小于365的正整数），如果超过这个周期登录系统时，则在登录时就会提醒修改密码对话框');

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept`  (
  `dept_id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父部门id',
  `ancestors` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '祖级列表',
  `dept_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '部门名称',
  `order_num` int NULL DEFAULT 0 COMMENT '显示顺序',
  `leader` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`dept_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 200 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '部门表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept` VALUES (100, 0, '0', '若依科技', 0, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-12-14 20:12:28', '', NULL);
INSERT INTO `sys_dept` VALUES (101, 100, '0,100', '深圳总公司', 1, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-12-14 20:12:28', '', NULL);
INSERT INTO `sys_dept` VALUES (102, 100, '0,100', '长沙分公司', 2, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-12-14 20:12:28', '', NULL);
INSERT INTO `sys_dept` VALUES (103, 101, '0,100,101', '研发部门', 1, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-12-14 20:12:28', '', NULL);
INSERT INTO `sys_dept` VALUES (104, 101, '0,100,101', '市场部门', 2, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-12-14 20:12:28', '', NULL);
INSERT INTO `sys_dept` VALUES (105, 101, '0,100,101', '测试部门', 3, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-12-14 20:12:28', '', NULL);
INSERT INTO `sys_dept` VALUES (106, 101, '0,100,101', '财务部门', 4, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-12-14 20:12:28', '', NULL);
INSERT INTO `sys_dept` VALUES (107, 101, '0,100,101', '运维部门', 5, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-12-14 20:12:28', '', NULL);
INSERT INTO `sys_dept` VALUES (108, 102, '0,100,102', '市场部门', 1, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-12-14 20:12:28', '', NULL);
INSERT INTO `sys_dept` VALUES (109, 102, '0,100,102', '财务部门', 2, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-12-14 20:12:28', '', NULL);

-- ----------------------------
-- Table structure for sys_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data`  (
  `dict_code` bigint NOT NULL AUTO_INCREMENT COMMENT '字典编码',
  `dict_sort` int NULL DEFAULT 0 COMMENT '字典排序',
  `dict_label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '字典标签',
  `dict_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '字典键值',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '字典类型',
  `css_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `list_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '表格回显样式',
  `is_default` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 101 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '字典数据表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dict_data
-- ----------------------------
INSERT INTO `sys_dict_data` VALUES (1, 1, '男', '0', 'sys_user_sex', '', '', 'Y', '0', 'admin', '2025-12-14 20:12:29', '', NULL, '性别男');
INSERT INTO `sys_dict_data` VALUES (2, 2, '女', '1', 'sys_user_sex', '', '', 'N', '0', 'admin', '2025-12-14 20:12:29', '', NULL, '性别女');
INSERT INTO `sys_dict_data` VALUES (3, 3, '未知', '2', 'sys_user_sex', '', '', 'N', '0', 'admin', '2025-12-14 20:12:29', '', NULL, '性别未知');
INSERT INTO `sys_dict_data` VALUES (4, 1, '显示', '0', 'sys_show_hide', '', 'primary', 'Y', '0', 'admin', '2025-12-14 20:12:29', '', NULL, '显示菜单');
INSERT INTO `sys_dict_data` VALUES (5, 2, '隐藏', '1', 'sys_show_hide', '', 'danger', 'N', '0', 'admin', '2025-12-14 20:12:29', '', NULL, '隐藏菜单');
INSERT INTO `sys_dict_data` VALUES (6, 1, '正常', '0', 'sys_normal_disable', '', 'primary', 'Y', '0', 'admin', '2025-12-14 20:12:29', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (7, 2, '停用', '1', 'sys_normal_disable', '', 'danger', 'N', '0', 'admin', '2025-12-14 20:12:29', '', NULL, '停用状态');
INSERT INTO `sys_dict_data` VALUES (8, 1, '正常', '0', 'sys_job_status', '', 'primary', 'Y', '0', 'admin', '2025-12-14 20:12:29', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (9, 2, '暂停', '1', 'sys_job_status', '', 'danger', 'N', '0', 'admin', '2025-12-14 20:12:29', '', NULL, '停用状态');
INSERT INTO `sys_dict_data` VALUES (10, 1, '默认', 'DEFAULT', 'sys_job_group', '', '', 'Y', '0', 'admin', '2025-12-14 20:12:29', '', NULL, '默认分组');
INSERT INTO `sys_dict_data` VALUES (11, 2, '系统', 'SYSTEM', 'sys_job_group', '', '', 'N', '0', 'admin', '2025-12-14 20:12:29', '', NULL, '系统分组');
INSERT INTO `sys_dict_data` VALUES (12, 1, '是', 'Y', 'sys_yes_no', '', 'primary', 'Y', '0', 'admin', '2025-12-14 20:12:29', '', NULL, '系统默认是');
INSERT INTO `sys_dict_data` VALUES (13, 2, '否', 'N', 'sys_yes_no', '', 'danger', 'N', '0', 'admin', '2025-12-14 20:12:29', '', NULL, '系统默认否');
INSERT INTO `sys_dict_data` VALUES (14, 1, '通知', '1', 'sys_notice_type', '', 'warning', 'Y', '0', 'admin', '2025-12-14 20:12:29', '', NULL, '通知');
INSERT INTO `sys_dict_data` VALUES (15, 2, '公告', '2', 'sys_notice_type', '', 'success', 'N', '0', 'admin', '2025-12-14 20:12:29', '', NULL, '公告');
INSERT INTO `sys_dict_data` VALUES (16, 1, '正常', '0', 'sys_notice_status', '', 'primary', 'Y', '0', 'admin', '2025-12-14 20:12:29', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (17, 2, '关闭', '1', 'sys_notice_status', '', 'danger', 'N', '0', 'admin', '2025-12-14 20:12:29', '', NULL, '关闭状态');
INSERT INTO `sys_dict_data` VALUES (18, 99, '其他', '0', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2025-12-14 20:12:29', '', NULL, '其他操作');
INSERT INTO `sys_dict_data` VALUES (19, 1, '新增', '1', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2025-12-14 20:12:29', '', NULL, '新增操作');
INSERT INTO `sys_dict_data` VALUES (20, 2, '修改', '2', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2025-12-14 20:12:29', '', NULL, '修改操作');
INSERT INTO `sys_dict_data` VALUES (21, 3, '删除', '3', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2025-12-14 20:12:29', '', NULL, '删除操作');
INSERT INTO `sys_dict_data` VALUES (22, 4, '授权', '4', 'sys_oper_type', '', 'primary', 'N', '0', 'admin', '2025-12-14 20:12:29', '', NULL, '授权操作');
INSERT INTO `sys_dict_data` VALUES (23, 5, '导出', '5', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2025-12-14 20:12:29', '', NULL, '导出操作');
INSERT INTO `sys_dict_data` VALUES (24, 6, '导入', '6', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2025-12-14 20:12:29', '', NULL, '导入操作');
INSERT INTO `sys_dict_data` VALUES (25, 7, '强退', '7', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2025-12-14 20:12:29', '', NULL, '强退操作');
INSERT INTO `sys_dict_data` VALUES (26, 8, '生成代码', '8', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2025-12-14 20:12:29', '', NULL, '生成操作');
INSERT INTO `sys_dict_data` VALUES (27, 9, '清空数据', '9', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2025-12-14 20:12:29', '', NULL, '清空操作');
INSERT INTO `sys_dict_data` VALUES (28, 1, '成功', '0', 'sys_common_status', '', 'primary', 'N', '0', 'admin', '2025-12-14 20:12:29', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (29, 2, '失败', '1', 'sys_common_status', '', 'danger', 'N', '0', 'admin', '2025-12-14 20:12:29', '', NULL, '停用状态');

-- ----------------------------
-- Table structure for sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type`  (
  `dict_id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典主键',
  `dict_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '字典名称',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '字典类型',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_id`) USING BTREE,
  UNIQUE INDEX `dict_type`(`dict_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 100 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '字典类型表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dict_type
-- ----------------------------
INSERT INTO `sys_dict_type` VALUES (1, '用户性别', 'sys_user_sex', '0', 'admin', '2025-12-14 20:12:29', '', NULL, '用户性别列表');
INSERT INTO `sys_dict_type` VALUES (2, '菜单状态', 'sys_show_hide', '0', 'admin', '2025-12-14 20:12:29', '', NULL, '菜单状态列表');
INSERT INTO `sys_dict_type` VALUES (3, '系统开关', 'sys_normal_disable', '0', 'admin', '2025-12-14 20:12:29', '', NULL, '系统开关列表');
INSERT INTO `sys_dict_type` VALUES (4, '任务状态', 'sys_job_status', '0', 'admin', '2025-12-14 20:12:29', '', NULL, '任务状态列表');
INSERT INTO `sys_dict_type` VALUES (5, '任务分组', 'sys_job_group', '0', 'admin', '2025-12-14 20:12:29', '', NULL, '任务分组列表');
INSERT INTO `sys_dict_type` VALUES (6, '系统是否', 'sys_yes_no', '0', 'admin', '2025-12-14 20:12:29', '', NULL, '系统是否列表');
INSERT INTO `sys_dict_type` VALUES (7, '通知类型', 'sys_notice_type', '0', 'admin', '2025-12-14 20:12:29', '', NULL, '通知类型列表');
INSERT INTO `sys_dict_type` VALUES (8, '通知状态', 'sys_notice_status', '0', 'admin', '2025-12-14 20:12:29', '', NULL, '通知状态列表');
INSERT INTO `sys_dict_type` VALUES (9, '操作类型', 'sys_oper_type', '0', 'admin', '2025-12-14 20:12:29', '', NULL, '操作类型列表');
INSERT INTO `sys_dict_type` VALUES (10, '系统状态', 'sys_common_status', '0', 'admin', '2025-12-14 20:12:29', '', NULL, '登录状态列表');

-- ----------------------------
-- Table structure for sys_job
-- ----------------------------
DROP TABLE IF EXISTS `sys_job`;
CREATE TABLE `sys_job`  (
  `job_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `job_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '任务名称',
  `job_group` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
  `invoke_target` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调用目标字符串',
  `cron_expression` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT 'cron执行表达式',
  `misfire_policy` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '3' COMMENT '计划执行错误策略（1立即执行 2执行一次 3放弃执行）',
  `concurrent` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '是否并发执行（0允许 1禁止）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '状态（0正常 1暂停）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '备注信息',
  PRIMARY KEY (`job_id`, `job_name`, `job_group`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 471 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '定时任务调度表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_job
-- ----------------------------
INSERT INTO `sys_job` VALUES (1, '系统默认（无参）', 'DEFAULT', 'ryTask.ryNoParams', '0/10 * * * * ?', '3', '1', '1', 'admin', '2025-12-14 20:12:29', '', NULL, '');
INSERT INTO `sys_job` VALUES (2, '系统默认（有参）', 'DEFAULT', 'ryTask.ryParams(\'ry\')', '0/15 * * * * ?', '3', '1', '1', 'admin', '2025-12-14 20:12:29', '', NULL, '');
INSERT INTO `sys_job` VALUES (3, '系统默认（多参）', 'DEFAULT', 'ryTask.ryMultipleParams(\'ry\', true, 2000L, 316.50D, 100)', '0/20 * * * * ?', '3', '1', '1', 'admin', '2025-12-14 20:12:29', '', NULL, '');
INSERT INTO `sys_job` VALUES (465, '工作流_wf_1767591244559', 'WORKFLOW', 'workflowTask.executeWorkflow(\'wf_1767591244559\')', '0 0 0 * * ?', '3', '1', '0', '', '2026-02-23 10:38:28', '', '2026-03-04 10:13:25', '');
INSERT INTO `sys_job` VALUES (470, '工作流_wf_1774092790037', 'WORKFLOW', 'workflowTask.executeWorkflow(\'wf_1774092790037\')', '0 0 3 * * ?', '3', '1', '0', '', '2026-03-21 19:33:12', '', '2026-03-24 09:51:34', '');

-- ----------------------------
-- Table structure for sys_job_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_job_log`;
CREATE TABLE `sys_job_log`  (
  `job_log_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务日志ID',
  `job_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务名称',
  `job_group` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务组名',
  `invoke_target` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调用目标字符串',
  `job_message` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '日志信息',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '执行状态（0正常 1失败）',
  `exception_info` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '异常信息',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`job_log_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 106 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '定时任务调度日志表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_job_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_logininfor
-- ----------------------------
DROP TABLE IF EXISTS `sys_logininfor`;
CREATE TABLE `sys_logininfor`  (
  `info_id` bigint NOT NULL AUTO_INCREMENT COMMENT '访问ID',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '用户账号',
  `ipaddr` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '登录IP地址',
  `login_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '登录地点',
  `browser` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '浏览器类型',
  `os` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '操作系统',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
  `msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '提示消息',
  `login_time` datetime NULL DEFAULT NULL COMMENT '访问时间',
  PRIMARY KEY (`info_id`) USING BTREE,
  INDEX `idx_sys_logininfor_s`(`status` ASC) USING BTREE,
  INDEX `idx_sys_logininfor_lt`(`login_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 585 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统访问记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_logininfor
-- ----------------------------

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `menu_id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单名称',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父菜单ID',
  `order_num` int NULL DEFAULT 0 COMMENT '显示顺序',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '组件路径',
  `query` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '路由参数',
  `route_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '路由名称',
  `is_frame` int NULL DEFAULT 1 COMMENT '是否为外链（0是 1否）',
  `is_cache` int NULL DEFAULT 0 COMMENT '是否缓存（0缓存 1不缓存）',
  `menu_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '#' COMMENT '菜单图标',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2809 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '菜单权限表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, '系统管理', 0, 1, 'system', NULL, '', '', 1, 0, 'M', '0', '0', '', 'system', 'admin', '2025-12-14 20:12:28', '', NULL, '系统管理目录');
INSERT INTO `sys_menu` VALUES (2, '系统监控', 0, 2, 'monitor', NULL, '', '', 1, 0, 'M', '0', '0', '', 'monitor', 'admin', '2025-12-14 20:12:28', '', NULL, '系统监控目录');
INSERT INTO `sys_menu` VALUES (3, '系统工具', 0, 3, 'tool', NULL, '', '', 1, 0, 'M', '0', '0', '', 'tool', 'admin', '2025-12-14 20:12:28', '', NULL, '系统工具目录');
INSERT INTO `sys_menu` VALUES (4, '若依官网', 0, 4, 'http://ruoyi.vip', NULL, '', '', 0, 0, 'M', '1', '0', '', 'guide', 'admin', '2025-12-14 20:12:28', 'admin', '2025-12-25 10:27:08', '若依官网地址');
INSERT INTO `sys_menu` VALUES (100, '用户管理', 1, 1, 'user', 'system/user/index', '', '', 1, 0, 'C', '0', '0', 'system:user:list', 'user', 'admin', '2025-12-14 20:12:28', '', NULL, '用户管理菜单');
INSERT INTO `sys_menu` VALUES (101, '角色管理', 1, 2, 'role', 'system/role/index', '', '', 1, 0, 'C', '0', '0', 'system:role:list', 'peoples', 'admin', '2025-12-14 20:12:28', '', NULL, '角色管理菜单');
INSERT INTO `sys_menu` VALUES (102, '菜单管理', 1, 3, 'menu', 'system/menu/index', '', '', 1, 0, 'C', '0', '0', 'system:menu:list', 'tree-table', 'admin', '2025-12-14 20:12:28', '', NULL, '菜单管理菜单');
INSERT INTO `sys_menu` VALUES (103, '部门管理', 1, 4, 'dept', 'system/dept/index', '', '', 1, 0, 'C', '0', '0', 'system:dept:list', 'tree', 'admin', '2025-12-14 20:12:28', '', NULL, '部门管理菜单');
INSERT INTO `sys_menu` VALUES (104, '岗位管理', 1, 5, 'post', 'system/post/index', '', '', 1, 0, 'C', '0', '0', 'system:post:list', 'post', 'admin', '2025-12-14 20:12:28', '', NULL, '岗位管理菜单');
INSERT INTO `sys_menu` VALUES (105, '字典管理', 1, 6, 'dict', 'system/dict/index', '', '', 1, 0, 'C', '0', '0', 'system:dict:list', 'dict', 'admin', '2025-12-14 20:12:28', '', NULL, '字典管理菜单');
INSERT INTO `sys_menu` VALUES (106, '参数设置', 1, 7, 'config', 'system/config/index', '', '', 1, 0, 'C', '0', '0', 'system:config:list', 'edit', 'admin', '2025-12-14 20:12:28', '', NULL, '参数设置菜单');
INSERT INTO `sys_menu` VALUES (107, '通知公告', 1, 8, 'notice', 'system/notice/index', '', '', 1, 0, 'C', '0', '0', 'system:notice:list', 'message', 'admin', '2025-12-14 20:12:28', '', NULL, '通知公告菜单');
INSERT INTO `sys_menu` VALUES (108, '日志管理', 1, 9, 'log', '', '', '', 1, 0, 'M', '0', '0', '', 'log', 'admin', '2025-12-14 20:12:28', '', NULL, '日志管理菜单');
INSERT INTO `sys_menu` VALUES (109, '在线用户', 2, 1, 'online', 'monitor/online/index', '', '', 1, 0, 'C', '0', '0', 'monitor:online:list', 'online', 'admin', '2025-12-14 20:12:28', '', NULL, '在线用户菜单');
INSERT INTO `sys_menu` VALUES (110, '定时任务', 2, 2, 'job', 'monitor/job/index', '', '', 1, 0, 'C', '0', '0', 'monitor:job:list', 'job', 'admin', '2025-12-14 20:12:28', '', NULL, '定时任务菜单');
INSERT INTO `sys_menu` VALUES (111, '数据监控', 2, 3, 'druid', 'monitor/druid/index', '', '', 1, 0, 'C', '0', '0', 'monitor:druid:list', 'druid', 'admin', '2025-12-14 20:12:28', '', NULL, '数据监控菜单');
INSERT INTO `sys_menu` VALUES (112, '服务监控', 2, 4, 'server', 'monitor/server/index', '', '', 1, 0, 'C', '0', '0', 'monitor:server:list', 'server', 'admin', '2025-12-14 20:12:28', '', NULL, '服务监控菜单');
INSERT INTO `sys_menu` VALUES (113, '缓存监控', 2, 5, 'cache', 'monitor/cache/index', '', '', 1, 0, 'C', '0', '0', 'monitor:cache:list', 'redis', 'admin', '2025-12-14 20:12:28', '', NULL, '缓存监控菜单');
INSERT INTO `sys_menu` VALUES (114, '缓存列表', 2, 6, 'cacheList', 'monitor/cache/list', '', '', 1, 0, 'C', '0', '0', 'monitor:cache:list', 'redis-list', 'admin', '2025-12-14 20:12:28', '', NULL, '缓存列表菜单');
INSERT INTO `sys_menu` VALUES (115, '表单构建', 3, 1, 'build', 'tool/build/index', '', '', 1, 0, 'C', '0', '0', 'tool:build:list', 'build', 'admin', '2025-12-14 20:12:28', '', NULL, '表单构建菜单');
INSERT INTO `sys_menu` VALUES (116, '代码生成', 3, 2, 'gen', 'tool/gen/index', '', '', 1, 0, 'C', '0', '0', 'tool:gen:list', 'code', 'admin', '2025-12-14 20:12:28', '', NULL, '代码生成菜单');
INSERT INTO `sys_menu` VALUES (117, '系统接口', 3, 3, 'swagger', 'tool/swagger/index', '', '', 1, 0, 'C', '0', '0', 'tool:swagger:list', 'swagger', 'admin', '2025-12-14 20:12:28', '', NULL, '系统接口菜单');
INSERT INTO `sys_menu` VALUES (500, '操作日志', 108, 1, 'operlog', 'monitor/operlog/index', '', '', 1, 0, 'C', '0', '0', 'monitor:operlog:list', 'form', 'admin', '2025-12-14 20:12:28', '', NULL, '操作日志菜单');
INSERT INTO `sys_menu` VALUES (501, '登录日志', 108, 2, 'logininfor', 'monitor/logininfor/index', '', '', 1, 0, 'C', '0', '0', 'monitor:logininfor:list', 'logininfor', 'admin', '2025-12-14 20:12:28', '', NULL, '登录日志菜单');
INSERT INTO `sys_menu` VALUES (1000, '用户查询', 100, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:query', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1001, '用户新增', 100, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:add', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1002, '用户修改', 100, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:edit', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1003, '用户删除', 100, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:remove', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1004, '用户导出', 100, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:export', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1005, '用户导入', 100, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:import', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1006, '重置密码', 100, 7, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:resetPwd', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1007, '角色查询', 101, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:query', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1008, '角色新增', 101, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:add', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1009, '角色修改', 101, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:edit', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1010, '角色删除', 101, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:remove', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1011, '角色导出', 101, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:export', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1012, '菜单查询', 102, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:query', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1013, '菜单新增', 102, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:add', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1014, '菜单修改', 102, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:edit', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1015, '菜单删除', 102, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:remove', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1016, '部门查询', 103, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:query', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1017, '部门新增', 103, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:add', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1018, '部门修改', 103, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:edit', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1019, '部门删除', 103, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:remove', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1020, '岗位查询', 104, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:query', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1021, '岗位新增', 104, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:add', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1022, '岗位修改', 104, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:edit', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1023, '岗位删除', 104, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:remove', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1024, '岗位导出', 104, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:export', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1025, '字典查询', 105, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:query', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1026, '字典新增', 105, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:add', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1027, '字典修改', 105, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:edit', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1028, '字典删除', 105, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:remove', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1029, '字典导出', 105, 5, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:export', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1030, '参数查询', 106, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:query', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1031, '参数新增', 106, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:add', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1032, '参数修改', 106, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:edit', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1033, '参数删除', 106, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:remove', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1034, '参数导出', 106, 5, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:export', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1035, '公告查询', 107, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:query', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1036, '公告新增', 107, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:add', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1037, '公告修改', 107, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:edit', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1038, '公告删除', 107, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:remove', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1039, '操作查询', 500, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:query', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1040, '操作删除', 500, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:remove', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1041, '日志导出', 500, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:export', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1042, '登录查询', 501, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:query', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1043, '登录删除', 501, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:remove', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1044, '日志导出', 501, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:export', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1045, '账户解锁', 501, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:unlock', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1046, '在线查询', 109, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:online:query', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1047, '批量强退', 109, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:online:batchLogout', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1048, '单条强退', 109, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:online:forceLogout', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1049, '任务查询', 110, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:query', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1050, '任务新增', 110, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:add', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1051, '任务修改', 110, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:edit', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1052, '任务删除', 110, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:remove', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1053, '状态修改', 110, 5, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:changeStatus', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1054, '任务导出', 110, 6, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:export', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1055, '生成查询', 116, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:query', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1056, '生成修改', 116, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:edit', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1057, '生成删除', 116, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:remove', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1058, '导入代码', 116, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:import', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1059, '预览代码', 116, 5, '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:preview', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1060, '生成代码', 116, 6, '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:code', '#', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2000, '游戏盒子', 0, 0, 'gamebox', NULL, NULL, '', 1, 0, 'M', '0', '0', '', 'server', 'admin', '2025-12-15 16:15:50', 'admin', '2025-12-25 10:27:52', '游戏盒子内容管理系统目录');
INSERT INTO `sys_menu` VALUES (2001, '基础配置', 2000, 1, 'config', NULL, NULL, '', 1, 0, 'M', '0', '0', '', 'system', 'admin', '2025-12-15 16:15:50', '', NULL, '基础配置目录');
INSERT INTO `sys_menu` VALUES (2002, '游戏管理', 2000, 3, 'game-mgmt', NULL, NULL, '', 1, 0, 'M', '0', '0', '', 'clipboard', 'admin', '2025-12-15 16:15:50', 'admin', '2025-12-31 16:15:19', '游戏管理目录');
INSERT INTO `sys_menu` VALUES (2003, '短剧管理', 2000, 3, 'drama-mgmt', NULL, NULL, '', 1, 0, 'M', '1', '0', '', 'video', 'admin', '2025-12-15 16:15:50', 'admin', '2025-12-24 09:52:39', '短剧管理目录');
INSERT INTO `sys_menu` VALUES (2004, '内容管理', 2000, 4, 'content', NULL, NULL, '', 1, 0, 'M', '0', '0', '', 'documentation', 'admin', '2025-12-15 16:15:50', '', NULL, '内容管理目录');
INSERT INTO `sys_menu` VALUES (2005, 'AI配置', 2000, 2, 'ai-config', NULL, NULL, '', 1, 0, 'M', '0', '0', '', 'swagger', 'admin', '2025-12-15 16:15:50', 'admin', '2025-12-31 16:15:11', 'AI配置目录');
INSERT INTO `sys_menu` VALUES (2100, '站点管理', 2001, 1, 'site', 'gamebox/config/site/index', NULL, '', 1, 0, 'C', '0', '0', 'gamebox:site:list', 'server', 'admin', '2025-12-15 16:15:50', 'admin', '2026-01-28 02:43:42', '站点管理菜单');
INSERT INTO `sys_menu` VALUES (2101, '站点查询', 2100, 1, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:site:query', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2102, '站点新增', 2100, 2, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:site:add', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2103, '站点修改', 2100, 3, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:site:edit', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2104, '站点删除', 2100, 4, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:site:remove', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2105, '站点导出', 2100, 5, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:site:export', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2110, '分类管理', 2001, 2, 'category', 'gamebox/config/category/index', NULL, '', 1, 0, 'C', '0', '0', 'gamebox:category:list', 'tree', 'admin', '2025-12-15 16:15:50', 'admin', '2026-01-28 02:43:46', '分类管理菜单');
INSERT INTO `sys_menu` VALUES (2111, '分类查询', 2110, 1, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:category:query', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2112, '分类新增', 2110, 2, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:category:add', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2113, '分类修改', 2110, 3, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:category:edit', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2114, '分类删除', 2110, 4, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:category:remove', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2115, '分类导出', 2110, 5, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:category:export', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2120, '存储配置', 2001, 3, 'storage', 'gamebox/config/storage/index', NULL, '', 1, 0, 'C', '0', '0', 'gamebox:storage:list', 'upload', 'admin', '2025-12-15 16:15:50', '', NULL, '存储配置菜单');
INSERT INTO `sys_menu` VALUES (2121, '存储配置查询', 2120, 1, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:storage:query', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2122, '存储配置新增', 2120, 2, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:storage:add', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2123, '存储配置修改', 2120, 3, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:storage:edit', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2124, '存储配置删除', 2120, 4, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:storage:remove', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2125, '存储配置导出', 2120, 5, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:storage:export', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2130, '代码管理', 2001, 4, 'code-manage', 'gamebox/config/code-manage/index', NULL, '', 1, 0, 'C', '0', '0', 'gamebox:codeManage:list', 'code', 'admin', '2026-02-23 11:54:01', '', NULL, '代码管理菜单-Git仓库配置、本地构建和预览');
INSERT INTO `sys_menu` VALUES (2131, '代码管理查询', 2130, 1, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:codeManage:query', '#', 'admin', '2026-02-23 11:54:01', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2132, '代码管理编辑', 2130, 2, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:codeManage:edit', '#', 'admin', '2026-02-23 11:54:01', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2133, '代码管理运维', 2130, 3, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:codeManage:ops', '#', 'admin', '2026-02-23 11:54:01', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2200, '游戏列表', 2002, 1, 'game', 'gamebox/game-mgmt/game/index', NULL, '', 1, 0, 'C', '0', '0', 'gamebox:game:list', 'star', 'admin', '2025-12-15 16:15:50', '', NULL, '游戏列表菜单');
INSERT INTO `sys_menu` VALUES (2201, '游戏查询', 2200, 1, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:game:query', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2202, '游戏新增', 2200, 2, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:game:add', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2203, '游戏修改', 2200, 3, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:game:edit', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2204, '游戏删除', 2200, 4, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:game:remove', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2205, '游戏导出', 2200, 5, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:game:export', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2206, '字段映射查询', 2200, 6, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:field-mapping:query', '#', 'admin', '2026-03-18 18:26:07', '', NULL, '字段映射查询权限（tableFields/listByBoxId）');
INSERT INTO `sys_menu` VALUES (2207, '字段映射修改', 2200, 7, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:field-mapping:edit', '#', 'admin', '2026-03-18 18:26:07', '', NULL, '字段映射保存/修改权限');
INSERT INTO `sys_menu` VALUES (2208, '字段映射新增', 2200, 8, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:field-mapping:add', '#', 'admin', '2026-03-18 18:26:07', '', NULL, '字段映射新增权限');
INSERT INTO `sys_menu` VALUES (2209, '字段映射删除', 2200, 9, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:field-mapping:remove', '#', 'admin', '2026-03-18 18:26:07', '', NULL, '字段映射删除权限');
INSERT INTO `sys_menu` VALUES (2210, '游戏盒子', 2002, 2, 'box', 'gamebox/game-mgmt/box/index', NULL, '', 1, 0, 'C', '0', '0', 'gamebox:box:list', 'component', 'admin', '2025-12-15 16:15:50', '', NULL, '游戏盒子菜单');
INSERT INTO `sys_menu` VALUES (2211, '游戏盒子查询', 2210, 1, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:box:query', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2212, '游戏盒子新增', 2210, 2, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:box:add', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2213, '游戏盒子修改', 2210, 3, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:box:edit', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2214, '游戏盒子删除', 2210, 4, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:box:remove', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2215, '游戏盒子导出', 2210, 5, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:box:export', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2216, '字段映射查询(盒子)', 2210, 6, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:field-mapping:query', '#', 'admin', '2026-03-18 18:26:07', '', NULL, '盒子页面字段映射查询权限');
INSERT INTO `sys_menu` VALUES (2217, '字段映射修改(盒子)', 2210, 7, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:field-mapping:edit', '#', 'admin', '2026-03-18 18:26:07', '', NULL, '盒子页面字段映射保存权限');
INSERT INTO `sys_menu` VALUES (2219, '字段映射列表', 2200, 10, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:field-mapping:list', '#', 'admin', '2026-03-18 18:26:07', '', NULL, '字段映射列表查询权限');
INSERT INTO `sys_menu` VALUES (2220, '字段映射导入', 2200, 11, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:field-mapping:import', '#', 'admin', '2026-03-18 18:26:07', '', NULL, '字段映射导入权限');
INSERT INTO `sys_menu` VALUES (2300, '短剧厂商', 2003, 1, 'vendor', 'gamebox/drama-mgmt/vendor/index', NULL, '', 1, 0, 'C', '0', '0', 'gamebox:vendor:list', 'peoples', 'admin', '2025-12-15 16:15:50', '', NULL, '短剧厂商菜单');
INSERT INTO `sys_menu` VALUES (2301, '短剧厂商查询', 2300, 1, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:vendor:query', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2302, '短剧厂商新增', 2300, 2, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:vendor:add', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2303, '短剧厂商修改', 2300, 3, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:vendor:edit', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2304, '短剧厂商删除', 2300, 4, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:vendor:remove', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2305, '短剧厂商导出', 2300, 5, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:vendor:export', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2310, '短剧列表', 2003, 2, 'drama', 'gamebox/drama-mgmt/drama/index', NULL, '', 1, 0, 'C', '0', '0', 'gamebox:drama:list', 'video', 'admin', '2025-12-15 16:15:50', '', NULL, '短剧列表菜单');
INSERT INTO `sys_menu` VALUES (2311, '短剧查询', 2310, 1, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:drama:query', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2312, '短剧新增', 2310, 2, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:drama:add', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2313, '短剧修改', 2310, 3, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:drama:edit', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2314, '短剧删除', 2310, 4, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:drama:remove', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2315, '短剧导出', 2310, 5, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:drama:export', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2400, '文章管理', 2004, 1, 'article', 'gamebox/content/article/index', NULL, '', 1, 0, 'C', '0', '0', 'gamebox:article:list', 'documentation', 'admin', '2025-12-15 16:15:50', '', NULL, '文章管理菜单');
INSERT INTO `sys_menu` VALUES (2401, '文章查询', 2400, 1, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:article:query', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2402, '文章新增', 2400, 2, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:article:add', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2403, '文章修改', 2400, 3, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:article:edit', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2404, '文章删除', 2400, 4, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:article:remove', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2405, '文章导出', 2400, 5, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:article:export', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2500, 'AI平台配置', 2005, 1, 'platform', 'gamebox/ai-config/platform/index', NULL, '', 1, 0, 'C', '0', '0', 'gamebox:platform:list', 'online', 'admin', '2025-12-15 16:15:50', '', NULL, 'AI平台配置菜单');
INSERT INTO `sys_menu` VALUES (2501, 'AI平台配置查询', 2500, 1, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:platform:query', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2502, 'AI平台配置新增', 2500, 2, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:platform:add', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2503, 'AI平台配置修改', 2500, 3, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:platform:edit', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2504, 'AI平台配置删除', 2500, 4, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:platform:remove', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2505, 'AI平台配置导出', 2500, 5, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:platform:export', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2510, '提示词模板', 2005, 2, 'prompt', 'gamebox/ai-config/prompt/index', NULL, '', 1, 0, 'C', '1', '0', 'gamebox:prompt:list', 'message', 'admin', '2025-12-15 16:15:50', '', NULL, '已废弃，请使用内容工具菜单');
INSERT INTO `sys_menu` VALUES (2511, '提示词模板查询', 2510, 1, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:prompt:query', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2512, '提示词模板新增', 2510, 2, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:prompt:add', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2513, '提示词模板修改', 2510, 3, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:prompt:edit', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2514, '提示词模板删除', 2510, 4, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:prompt:remove', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2515, '提示词模板导出', 2510, 5, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:prompt:export', '#', 'admin', '2025-12-15 16:15:50', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2517, '标题池管理', 2004, 1, 'titlePool', 'gamebox/content/titlePool/index', NULL, '', 1, 0, 'C', '0', '0', 'gamebox:titlePool:list', 'list', 'admin', '2025-12-30 21:01:03', 'admin', '2026-01-06 20:29:37', '文章标题池管理菜单');
INSERT INTO `sys_menu` VALUES (2518, '标题查询', 2517, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'gamebox:titlePool:query', '#', 'admin', '2025-12-30 21:01:03', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2519, '标题新增', 2517, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'gamebox:titlePool:add', '#', 'admin', '2025-12-30 21:01:03', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2520, '标题修改', 2517, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'gamebox:titlePool:edit', '#', 'admin', '2025-12-30 21:01:03', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2521, '标题删除', 2517, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'gamebox:titlePool:remove', '#', 'admin', '2025-12-30 21:01:03', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2522, '标题导入', 2517, 5, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'gamebox:titlePool:import', '#', 'admin', '2025-12-30 21:01:03', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2523, '导入历史', 2517, 6, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'gamebox:titlePool:history', '#', 'admin', '2025-12-30 21:01:03', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2700, '原子工具', 2005, 4, 'atomic-tool', 'gamebox/ai-config/atomic-tool/index', NULL, '', 1, 0, 'C', '0', '0', 'gamebox:atomicTool:list', 'component', 'admin', '2026-01-04 14:50:36', 'admin', '2026-01-04 14:53:33', '原子工具管理菜单');
INSERT INTO `sys_menu` VALUES (2701, '原子工具查询', 2700, 1, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:atomicTool:query', '#', 'admin', '2026-01-04 14:50:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2702, '原子工具新增', 2700, 2, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:atomicTool:add', '#', 'admin', '2026-01-04 14:50:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2703, '原子工具修改', 2700, 3, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:atomicTool:edit', '#', 'admin', '2026-01-04 14:50:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2704, '原子工具删除', 2700, 4, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:atomicTool:remove', '#', 'admin', '2026-01-04 14:50:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2705, '原子工具导出', 2700, 5, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:atomicTool:export', '#', 'admin', '2026-01-04 14:50:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2706, '原子工具测试', 2700, 6, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:atomicTool:test', '#', 'admin', '2026-01-04 14:50:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2707, '原子工具执行', 2700, 7, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:atomicTool:execute', '#', 'admin', '2026-01-04 14:50:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2800, '工作流管理', 2004, 5, 'workflow', 'gamebox/content/workflow/index', NULL, '', 1, 0, 'C', '0', '0', 'gamebox:workflow:list', 'tree-table', 'admin', '2026-01-04 14:50:36', 'admin', '2026-01-04 14:57:41', '工作流管理菜单');
INSERT INTO `sys_menu` VALUES (2801, '工作流查询', 2800, 1, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'content:workflow:query', '#', 'admin', '2026-01-04 14:50:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2802, '工作流新增', 2800, 2, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'content:workflow:add', '#', 'admin', '2026-01-04 14:50:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2803, '工作流修改', 2800, 3, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'content:workflow:edit', '#', 'admin', '2026-01-04 14:50:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2804, '工作流删除', 2800, 4, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'content:workflow:remove', '#', 'admin', '2026-01-04 14:50:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2805, '工作流导出', 2800, 5, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'content:workflow:export', '#', 'admin', '2026-01-04 14:50:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2806, '工作流执行', 2800, 6, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'content:workflow:execute', '#', 'admin', '2026-01-04 14:50:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2807, '工作流测试', 2800, 7, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'content:workflow:test', '#', 'admin', '2026-01-04 14:50:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2808, '工作流验证', 2800, 8, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'content:workflow:validate', '#', 'admin', '2026-01-04 14:50:36', '', NULL, '');

-- ----------------------------
-- Table structure for sys_notice
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice`  (
  `notice_id` int NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `notice_title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公告标题',
  `notice_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公告类型（1通知 2公告）',
  `notice_content` longblob NULL COMMENT '公告内容',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '公告状态（0正常 1关闭）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`notice_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '通知公告表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_notice
-- ----------------------------
INSERT INTO `sys_notice` VALUES (1, '温馨提醒：2018-07-01 若依新版本发布啦', '2', 0xE696B0E78988E69CACE58685E5AEB9, '0', 'admin', '2025-12-14 20:12:30', '', NULL, '管理员');
INSERT INTO `sys_notice` VALUES (2, '维护通知：2018-07-01 若依系统凌晨维护', '1', 0xE7BBB4E68AA4E58685E5AEB9, '0', 'admin', '2025-12-14 20:12:30', '', NULL, '管理员');

-- ----------------------------
-- Table structure for sys_oper_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log`  (
  `oper_id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '模块标题',
  `business_type` int NULL DEFAULT 0 COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  `method` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '方法名称',
  `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '请求方式',
  `operator_type` int NULL DEFAULT 0 COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
  `oper_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '操作人员',
  `dept_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '部门名称',
  `oper_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '请求URL',
  `oper_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '主机地址',
  `oper_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '操作地点',
  `oper_param` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '请求参数',
  `json_result` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '返回参数',
  `status` int NULL DEFAULT 0 COMMENT '操作状态（0正常 1异常）',
  `error_msg` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '错误消息',
  `oper_time` datetime NULL DEFAULT NULL COMMENT '操作时间',
  `cost_time` bigint NULL DEFAULT 0 COMMENT '消耗时间',
  PRIMARY KEY (`oper_id`) USING BTREE,
  INDEX `idx_sys_oper_log_bt`(`business_type` ASC) USING BTREE,
  INDEX `idx_sys_oper_log_s`(`status` ASC) USING BTREE,
  INDEX `idx_sys_oper_log_ot`(`oper_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12020 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '操作日志记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_oper_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post`  (
  `post_id` bigint NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
  `post_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '岗位编码',
  `post_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '岗位名称',
  `post_sort` int NOT NULL COMMENT '显示顺序',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`post_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '岗位信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_post
-- ----------------------------
INSERT INTO `sys_post` VALUES (1, 'ceo', '董事长', 1, '0', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_post` VALUES (2, 'se', '项目经理', 2, '0', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_post` VALUES (3, 'hr', '人力资源', 3, '0', 'admin', '2025-12-14 20:12:28', '', NULL, '');
INSERT INTO `sys_post` VALUES (4, 'user', '普通员工', 4, '0', 'admin', '2025-12-14 20:12:28', '', NULL, '');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `role_id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色权限字符串',
  `role_sort` int NOT NULL COMMENT '显示顺序',
  `data_scope` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  `menu_check_strictly` tinyint(1) NULL DEFAULT 1 COMMENT '菜单树选择项是否关联显示',
  `dept_check_strictly` tinyint(1) NULL DEFAULT 1 COMMENT '部门树选择项是否关联显示',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 100 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'admin', 1, '1', 1, 1, '0', '0', 'admin', '2025-12-14 20:12:28', '', NULL, '超级管理员');
INSERT INTO `sys_role` VALUES (2, '普通角色', 'common', 2, '1', 1, 1, '0', '0', 'admin', '2025-12-14 20:12:28', 'admin', '2026-03-18 18:53:45', '普通角色');

-- ----------------------------
-- Table structure for sys_role_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_dept`;
CREATE TABLE `sys_role_dept`  (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `dept_id` bigint NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`, `dept_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色和部门关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_dept
-- ----------------------------

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`, `menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色和菜单关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (1, 2206);
INSERT INTO `sys_role_menu` VALUES (1, 2207);
INSERT INTO `sys_role_menu` VALUES (1, 2208);
INSERT INTO `sys_role_menu` VALUES (1, 2209);
INSERT INTO `sys_role_menu` VALUES (1, 2216);
INSERT INTO `sys_role_menu` VALUES (1, 2217);
INSERT INTO `sys_role_menu` VALUES (1, 2219);
INSERT INTO `sys_role_menu` VALUES (1, 2220);
INSERT INTO `sys_role_menu` VALUES (1, 2700);
INSERT INTO `sys_role_menu` VALUES (1, 2701);
INSERT INTO `sys_role_menu` VALUES (1, 2702);
INSERT INTO `sys_role_menu` VALUES (1, 2703);
INSERT INTO `sys_role_menu` VALUES (1, 2704);
INSERT INTO `sys_role_menu` VALUES (1, 2705);
INSERT INTO `sys_role_menu` VALUES (1, 2706);
INSERT INTO `sys_role_menu` VALUES (1, 2707);
INSERT INTO `sys_role_menu` VALUES (1, 2800);
INSERT INTO `sys_role_menu` VALUES (1, 2801);
INSERT INTO `sys_role_menu` VALUES (1, 2802);
INSERT INTO `sys_role_menu` VALUES (1, 2803);
INSERT INTO `sys_role_menu` VALUES (1, 2804);
INSERT INTO `sys_role_menu` VALUES (1, 2805);
INSERT INTO `sys_role_menu` VALUES (1, 2806);
INSERT INTO `sys_role_menu` VALUES (1, 2807);
INSERT INTO `sys_role_menu` VALUES (1, 2808);
INSERT INTO `sys_role_menu` VALUES (2, 1);
INSERT INTO `sys_role_menu` VALUES (2, 2);
INSERT INTO `sys_role_menu` VALUES (2, 3);
INSERT INTO `sys_role_menu` VALUES (2, 4);
INSERT INTO `sys_role_menu` VALUES (2, 100);
INSERT INTO `sys_role_menu` VALUES (2, 101);
INSERT INTO `sys_role_menu` VALUES (2, 102);
INSERT INTO `sys_role_menu` VALUES (2, 103);
INSERT INTO `sys_role_menu` VALUES (2, 104);
INSERT INTO `sys_role_menu` VALUES (2, 105);
INSERT INTO `sys_role_menu` VALUES (2, 106);
INSERT INTO `sys_role_menu` VALUES (2, 107);
INSERT INTO `sys_role_menu` VALUES (2, 108);
INSERT INTO `sys_role_menu` VALUES (2, 109);
INSERT INTO `sys_role_menu` VALUES (2, 110);
INSERT INTO `sys_role_menu` VALUES (2, 111);
INSERT INTO `sys_role_menu` VALUES (2, 112);
INSERT INTO `sys_role_menu` VALUES (2, 113);
INSERT INTO `sys_role_menu` VALUES (2, 114);
INSERT INTO `sys_role_menu` VALUES (2, 115);
INSERT INTO `sys_role_menu` VALUES (2, 116);
INSERT INTO `sys_role_menu` VALUES (2, 117);
INSERT INTO `sys_role_menu` VALUES (2, 500);
INSERT INTO `sys_role_menu` VALUES (2, 501);
INSERT INTO `sys_role_menu` VALUES (2, 1000);
INSERT INTO `sys_role_menu` VALUES (2, 1001);
INSERT INTO `sys_role_menu` VALUES (2, 1002);
INSERT INTO `sys_role_menu` VALUES (2, 1003);
INSERT INTO `sys_role_menu` VALUES (2, 1004);
INSERT INTO `sys_role_menu` VALUES (2, 1005);
INSERT INTO `sys_role_menu` VALUES (2, 1006);
INSERT INTO `sys_role_menu` VALUES (2, 1007);
INSERT INTO `sys_role_menu` VALUES (2, 1008);
INSERT INTO `sys_role_menu` VALUES (2, 1009);
INSERT INTO `sys_role_menu` VALUES (2, 1010);
INSERT INTO `sys_role_menu` VALUES (2, 1011);
INSERT INTO `sys_role_menu` VALUES (2, 1012);
INSERT INTO `sys_role_menu` VALUES (2, 1013);
INSERT INTO `sys_role_menu` VALUES (2, 1014);
INSERT INTO `sys_role_menu` VALUES (2, 1015);
INSERT INTO `sys_role_menu` VALUES (2, 1016);
INSERT INTO `sys_role_menu` VALUES (2, 1017);
INSERT INTO `sys_role_menu` VALUES (2, 1018);
INSERT INTO `sys_role_menu` VALUES (2, 1019);
INSERT INTO `sys_role_menu` VALUES (2, 1020);
INSERT INTO `sys_role_menu` VALUES (2, 1021);
INSERT INTO `sys_role_menu` VALUES (2, 1022);
INSERT INTO `sys_role_menu` VALUES (2, 1023);
INSERT INTO `sys_role_menu` VALUES (2, 1024);
INSERT INTO `sys_role_menu` VALUES (2, 1025);
INSERT INTO `sys_role_menu` VALUES (2, 1026);
INSERT INTO `sys_role_menu` VALUES (2, 1027);
INSERT INTO `sys_role_menu` VALUES (2, 1028);
INSERT INTO `sys_role_menu` VALUES (2, 1029);
INSERT INTO `sys_role_menu` VALUES (2, 1030);
INSERT INTO `sys_role_menu` VALUES (2, 1031);
INSERT INTO `sys_role_menu` VALUES (2, 1032);
INSERT INTO `sys_role_menu` VALUES (2, 1033);
INSERT INTO `sys_role_menu` VALUES (2, 1034);
INSERT INTO `sys_role_menu` VALUES (2, 1035);
INSERT INTO `sys_role_menu` VALUES (2, 1036);
INSERT INTO `sys_role_menu` VALUES (2, 1037);
INSERT INTO `sys_role_menu` VALUES (2, 1038);
INSERT INTO `sys_role_menu` VALUES (2, 1039);
INSERT INTO `sys_role_menu` VALUES (2, 1040);
INSERT INTO `sys_role_menu` VALUES (2, 1041);
INSERT INTO `sys_role_menu` VALUES (2, 1042);
INSERT INTO `sys_role_menu` VALUES (2, 1043);
INSERT INTO `sys_role_menu` VALUES (2, 1044);
INSERT INTO `sys_role_menu` VALUES (2, 1045);
INSERT INTO `sys_role_menu` VALUES (2, 1046);
INSERT INTO `sys_role_menu` VALUES (2, 1047);
INSERT INTO `sys_role_menu` VALUES (2, 1048);
INSERT INTO `sys_role_menu` VALUES (2, 1049);
INSERT INTO `sys_role_menu` VALUES (2, 1050);
INSERT INTO `sys_role_menu` VALUES (2, 1051);
INSERT INTO `sys_role_menu` VALUES (2, 1052);
INSERT INTO `sys_role_menu` VALUES (2, 1053);
INSERT INTO `sys_role_menu` VALUES (2, 1054);
INSERT INTO `sys_role_menu` VALUES (2, 1055);
INSERT INTO `sys_role_menu` VALUES (2, 1056);
INSERT INTO `sys_role_menu` VALUES (2, 1057);
INSERT INTO `sys_role_menu` VALUES (2, 1058);
INSERT INTO `sys_role_menu` VALUES (2, 1059);
INSERT INTO `sys_role_menu` VALUES (2, 1060);
INSERT INTO `sys_role_menu` VALUES (2, 2000);
INSERT INTO `sys_role_menu` VALUES (2, 2001);
INSERT INTO `sys_role_menu` VALUES (2, 2002);
INSERT INTO `sys_role_menu` VALUES (2, 2003);
INSERT INTO `sys_role_menu` VALUES (2, 2004);
INSERT INTO `sys_role_menu` VALUES (2, 2005);
INSERT INTO `sys_role_menu` VALUES (2, 2100);
INSERT INTO `sys_role_menu` VALUES (2, 2101);
INSERT INTO `sys_role_menu` VALUES (2, 2102);
INSERT INTO `sys_role_menu` VALUES (2, 2103);
INSERT INTO `sys_role_menu` VALUES (2, 2104);
INSERT INTO `sys_role_menu` VALUES (2, 2105);
INSERT INTO `sys_role_menu` VALUES (2, 2110);
INSERT INTO `sys_role_menu` VALUES (2, 2111);
INSERT INTO `sys_role_menu` VALUES (2, 2112);
INSERT INTO `sys_role_menu` VALUES (2, 2113);
INSERT INTO `sys_role_menu` VALUES (2, 2114);
INSERT INTO `sys_role_menu` VALUES (2, 2115);
INSERT INTO `sys_role_menu` VALUES (2, 2120);
INSERT INTO `sys_role_menu` VALUES (2, 2121);
INSERT INTO `sys_role_menu` VALUES (2, 2122);
INSERT INTO `sys_role_menu` VALUES (2, 2123);
INSERT INTO `sys_role_menu` VALUES (2, 2124);
INSERT INTO `sys_role_menu` VALUES (2, 2125);
INSERT INTO `sys_role_menu` VALUES (2, 2130);
INSERT INTO `sys_role_menu` VALUES (2, 2131);
INSERT INTO `sys_role_menu` VALUES (2, 2132);
INSERT INTO `sys_role_menu` VALUES (2, 2133);
INSERT INTO `sys_role_menu` VALUES (2, 2200);
INSERT INTO `sys_role_menu` VALUES (2, 2201);
INSERT INTO `sys_role_menu` VALUES (2, 2202);
INSERT INTO `sys_role_menu` VALUES (2, 2203);
INSERT INTO `sys_role_menu` VALUES (2, 2204);
INSERT INTO `sys_role_menu` VALUES (2, 2205);
INSERT INTO `sys_role_menu` VALUES (2, 2206);
INSERT INTO `sys_role_menu` VALUES (2, 2207);
INSERT INTO `sys_role_menu` VALUES (2, 2208);
INSERT INTO `sys_role_menu` VALUES (2, 2209);
INSERT INTO `sys_role_menu` VALUES (2, 2210);
INSERT INTO `sys_role_menu` VALUES (2, 2211);
INSERT INTO `sys_role_menu` VALUES (2, 2212);
INSERT INTO `sys_role_menu` VALUES (2, 2213);
INSERT INTO `sys_role_menu` VALUES (2, 2214);
INSERT INTO `sys_role_menu` VALUES (2, 2215);
INSERT INTO `sys_role_menu` VALUES (2, 2219);
INSERT INTO `sys_role_menu` VALUES (2, 2220);
INSERT INTO `sys_role_menu` VALUES (2, 2300);
INSERT INTO `sys_role_menu` VALUES (2, 2301);
INSERT INTO `sys_role_menu` VALUES (2, 2302);
INSERT INTO `sys_role_menu` VALUES (2, 2303);
INSERT INTO `sys_role_menu` VALUES (2, 2304);
INSERT INTO `sys_role_menu` VALUES (2, 2305);
INSERT INTO `sys_role_menu` VALUES (2, 2310);
INSERT INTO `sys_role_menu` VALUES (2, 2311);
INSERT INTO `sys_role_menu` VALUES (2, 2312);
INSERT INTO `sys_role_menu` VALUES (2, 2313);
INSERT INTO `sys_role_menu` VALUES (2, 2314);
INSERT INTO `sys_role_menu` VALUES (2, 2315);
INSERT INTO `sys_role_menu` VALUES (2, 2400);
INSERT INTO `sys_role_menu` VALUES (2, 2401);
INSERT INTO `sys_role_menu` VALUES (2, 2402);
INSERT INTO `sys_role_menu` VALUES (2, 2403);
INSERT INTO `sys_role_menu` VALUES (2, 2404);
INSERT INTO `sys_role_menu` VALUES (2, 2405);
INSERT INTO `sys_role_menu` VALUES (2, 2500);
INSERT INTO `sys_role_menu` VALUES (2, 2501);
INSERT INTO `sys_role_menu` VALUES (2, 2502);
INSERT INTO `sys_role_menu` VALUES (2, 2503);
INSERT INTO `sys_role_menu` VALUES (2, 2504);
INSERT INTO `sys_role_menu` VALUES (2, 2505);
INSERT INTO `sys_role_menu` VALUES (2, 2510);
INSERT INTO `sys_role_menu` VALUES (2, 2511);
INSERT INTO `sys_role_menu` VALUES (2, 2512);
INSERT INTO `sys_role_menu` VALUES (2, 2513);
INSERT INTO `sys_role_menu` VALUES (2, 2514);
INSERT INTO `sys_role_menu` VALUES (2, 2515);
INSERT INTO `sys_role_menu` VALUES (2, 2517);
INSERT INTO `sys_role_menu` VALUES (2, 2518);
INSERT INTO `sys_role_menu` VALUES (2, 2519);
INSERT INTO `sys_role_menu` VALUES (2, 2520);
INSERT INTO `sys_role_menu` VALUES (2, 2521);
INSERT INTO `sys_role_menu` VALUES (2, 2522);
INSERT INTO `sys_role_menu` VALUES (2, 2523);
INSERT INTO `sys_role_menu` VALUES (2, 2700);
INSERT INTO `sys_role_menu` VALUES (2, 2701);
INSERT INTO `sys_role_menu` VALUES (2, 2702);
INSERT INTO `sys_role_menu` VALUES (2, 2703);
INSERT INTO `sys_role_menu` VALUES (2, 2704);
INSERT INTO `sys_role_menu` VALUES (2, 2705);
INSERT INTO `sys_role_menu` VALUES (2, 2706);
INSERT INTO `sys_role_menu` VALUES (2, 2707);
INSERT INTO `sys_role_menu` VALUES (2, 2800);
INSERT INTO `sys_role_menu` VALUES (2, 2801);
INSERT INTO `sys_role_menu` VALUES (2, 2802);
INSERT INTO `sys_role_menu` VALUES (2, 2803);
INSERT INTO `sys_role_menu` VALUES (2, 2804);
INSERT INTO `sys_role_menu` VALUES (2, 2805);
INSERT INTO `sys_role_menu` VALUES (2, 2806);
INSERT INTO `sys_role_menu` VALUES (2, 2807);
INSERT INTO `sys_role_menu` VALUES (2, 2808);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '部门ID',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户昵称',
  `user_type` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '00' COMMENT '用户类型（00系统用户）',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '用户邮箱',
  `phonenumber` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '手机号码',
  `sex` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '密码',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '账号状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `login_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `pwd_update_date` datetime NULL DEFAULT NULL COMMENT '密码最后更新时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 103 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 103, 'admin', '若依', '00', 'ry@163.com', '15888888888', '1', '', '$2a$10$n/onc5qrIqEaiwHbufOK4uXEwT7POaJmN6CzeNBYdVeV1YYBtP1R.', '0', '0', '127.0.0.1', '2026-03-25 12:05:45', '2026-01-20 21:12:04', 'admin', '2025-12-14 20:12:28', '', '2026-01-20 21:12:04', '管理员');
INSERT INTO `sys_user` VALUES (2, 105, 'ry', '若依', '00', 'ry@qq.com', '15666666666', '1', '', '$2a$10$EgHjiy.2T2KwtcAvLTlb2O1zIvPvIhYed.FIFzOyYeKU25b3Gocpq', '0', '0', '127.0.0.1', '2026-03-18 18:57:53', '2026-03-17 16:38:07', 'admin', '2025-12-14 20:12:28', '', '2026-03-17 16:38:07', '测试员');
INSERT INTO `sys_user` VALUES (102, NULL, 'misin', 'misin', '00', '', '', '0', '', '$2a$10$EIY4oC2/k7UH5D.v8uMfve8RW6nmN1gQTRcC0nzWZiYg6xsEeiSkC', '0', '0', '127.0.0.1', '2026-03-18 18:59:46', NULL, 'admin', '2026-03-18 18:59:31', '', NULL, NULL);

-- ----------------------------
-- Table structure for sys_user_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_post`;
CREATE TABLE `sys_user_post`  (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `post_id` bigint NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`user_id`, `post_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户与岗位关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_post
-- ----------------------------
INSERT INTO `sys_user_post` VALUES (1, 1);
INSERT INTO `sys_user_post` VALUES (2, 2);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户和角色关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1);
INSERT INTO `sys_user_role` VALUES (2, 2);
INSERT INTO `sys_user_role` VALUES (102, 2);

SET FOREIGN_KEY_CHECKS = 1;
