-- 网站-存储配置关联表
CREATE TABLE IF NOT EXISTS `gb_site_storage_config_relations` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `site_id` bigint(20) NOT NULL COMMENT '网站ID',
  `storage_config_id` bigint(20) NOT NULL COMMENT '存储配置ID',
  `is_visible` char(1) DEFAULT '1' COMMENT '是否可见：0-否 1-是',
  `is_editable` char(1) DEFAULT '1' COMMENT '是否可编辑：0-否 1-是',
  `sort_order` int(11) DEFAULT 0 COMMENT '排序',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_site_storage` (`site_id`, `storage_config_id`),
  KEY `idx_site_id` (`site_id`),
  KEY `idx_storage_config_id` (`storage_config_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='网站-存储配置关联表';
