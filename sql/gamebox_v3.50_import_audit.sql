-- =====================================================================
-- v3.50 导入批次审计与变更日志
-- 功能：
--   1. 记录每次批量导入的批次信息（gb_import_batch）
--   2. 记录每次导入中每个游戏/关联行的字段变更快照（gb_game_change_log）
--   3. 支持按批次或按游戏查看修改记录，并可按需撤回
-- =====================================================================

-- 导入批次记录表
CREATE TABLE IF NOT EXISTS `gb_import_batch` (
  `id`            bigint      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `batch_no`      varchar(64) NOT NULL                COMMENT '批次号（UUID）',
  `box_id`        bigint      DEFAULT NULL             COMMENT '关联盒子ID',
  `box_name`      varchar(200) DEFAULT NULL            COMMENT '盒子名称（冗余记录）',
  `site_id`       bigint      DEFAULT NULL             COMMENT '关联网站ID',
  `site_name`     varchar(200) DEFAULT NULL            COMMENT '网站名称（冗余记录）',
  `platform_type` varchar(50) DEFAULT NULL             COMMENT '来源平台类型',
  `total_count`   int         NOT NULL DEFAULT 0       COMMENT '处理总条数',
  `new_count`     int         NOT NULL DEFAULT 0       COMMENT '新增条数',
  `updated_count` int         NOT NULL DEFAULT 0       COMMENT '更新条数',
  `skipped_count` int         NOT NULL DEFAULT 0       COMMENT '跳过条数',
  `failed_count`  int         NOT NULL DEFAULT 0       COMMENT '失败条数',
  `status`        varchar(20) NOT NULL DEFAULT 'completed' COMMENT '批次状态：completed-成功 partial_failed-部分失败',
  `summary`       text        DEFAULT NULL             COMMENT '批次摘要（含错误列表JSON）',
  `reverted`      tinyint(1)  NOT NULL DEFAULT 0       COMMENT '是否已全部撤回（0-否 1-是）',
  `create_time`   datetime    DEFAULT NULL             COMMENT '创建时间',
  `create_by`     varchar(64) DEFAULT NULL             COMMENT '创建人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_batch_no` (`batch_no`),
  KEY `idx_box_id`     (`box_id`),
  KEY `idx_site_id`    (`site_id`),
  KEY `idx_create_time`(`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
  COMMENT='游戏数据导入批次记录表';

-- 游戏字段变更日志表
CREATE TABLE IF NOT EXISTS `gb_game_change_log` (
  `id`              bigint        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `batch_id`        bigint        DEFAULT NULL            COMMENT '所属批次ID',
  `batch_no`        varchar(64)   DEFAULT NULL            COMMENT '批次号',
  `game_id`         bigint        NOT NULL                COMMENT '游戏ID',
  `game_name`       varchar(200)  DEFAULT NULL            COMMENT '游戏名称（冗余）',
  `change_type`     varchar(20)   NOT NULL                COMMENT '变更类型：INSERT-新增 UPDATE-修改',
  `target_table`    varchar(80)   NOT NULL                COMMENT '变更目标表：gb_games / gb_box_game_relations',
  `target_id`       bigint        DEFAULT NULL            COMMENT '目标表记录ID',
  `before_snapshot` mediumtext    DEFAULT NULL            COMMENT '变更前记录快照（JSON），INSERT时为空',
  `after_snapshot`  mediumtext    DEFAULT NULL            COMMENT '变更后记录快照（JSON）',
  `reverted`        tinyint(1)    NOT NULL DEFAULT 0      COMMENT '是否已撤回（0-否 1-是）',
  `revert_time`     datetime      DEFAULT NULL            COMMENT '撤回时间',
  `revert_by`       varchar(64)   DEFAULT NULL            COMMENT '撤回操作人',
  `create_time`     datetime      DEFAULT NULL            COMMENT '创建时间',
  `create_by`       varchar(64)   DEFAULT NULL            COMMENT '创建人',
  PRIMARY KEY (`id`),
  KEY `idx_batch_id`   (`batch_id`),
  KEY `idx_game_id`    (`game_id`),
  KEY `idx_batch_no`   (`batch_no`),
  KEY `idx_create_time`(`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
  COMMENT='游戏字段变更日志表（按批次记录每次导入的数据变更快照）';

-- ---------------------------------------------------------------
-- 菜单：导入历史（挂在"游戏管理"目录 2002 下，排序3）
-- ---------------------------------------------------------------
INSERT IGNORE INTO `sys_menu` VALUES
  (2220, '导入历史', 2002, 3, 'import-history', 'gamebox/game-mgmt/import-history/index',
   NULL, '', 1, 0, 'C', '0', '0', 'gamebox:importBatch:list', 'time', 'admin', NOW(), '', NULL, '导入批次历史与变更审计');

-- 按钮权限
INSERT IGNORE INTO `sys_menu` VALUES
  (2221, '导入历史查询', 2220, 1, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:importBatch:query',  '#', 'admin', NOW(), '', NULL, ''),
  (2222, '变更撤回',     2220, 2, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'gamebox:importBatch:revert', '#', 'admin', NOW(), '', NULL, '');
