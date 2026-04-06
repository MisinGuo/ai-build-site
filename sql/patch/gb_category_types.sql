-- 分类类型配置表（如果表已存在但结构不对，先删除）
DROP TABLE IF EXISTS `gb_category_types`;

CREATE TABLE `gb_category_types` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `value` varchar(50) NOT NULL COMMENT '分类类型值（唯一标识）',
  `label` varchar(100) NOT NULL COMMENT '分类类型标签（显示名称）',
  `tag_type` varchar(20) DEFAULT 'info' COMMENT 'Tag标签类型：primary/success/warning/danger/info',
  `description` varchar(500) DEFAULT NULL COMMENT '分类类型描述',
  `sort_order` int(11) DEFAULT 0 COMMENT '排序号',
  `status` char(1) DEFAULT '0' COMMENT '状态：0正常 1停用',
  `is_system` char(1) DEFAULT '0' COMMENT '是否系统内置：0否 1是（系统内置不可删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_value` (`value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类类型配置表';

-- 插入初始数据
INSERT INTO `gb_category_types` (`value`, `label`, `tag_type`, `description`, `sort_order`, `status`, `is_system`, `create_time`) VALUES
('game', '游戏分类', 'primary', '用于游戏管理页面的分类', 1, '0', '1', NOW()),
('drama', '短剧分类', 'success', '用于短剧管理页面的分类', 2, '0', '1', NOW()),
('article', '文章分类', 'warning', '用于文章管理页面的分类', 3, '0', '1', NOW()),
('website', '网站分类', 'info', '用于网站管理页面的分类', 4, '0', '1', NOW()),
('gamebox', '游戏盒子分类', 'danger', '用于游戏盒子管理页面的分类', 5, '0', '1', NOW()),
('document', '文档分类', '', '用于文档管理页面的分类', 6, '0', '1', NOW()),
('storage', '存储配置分类', 'primary', '用于存储配置页面的分类', 7, '0', '1', NOW()),
('other', '其他分类', 'info', '用于其他未分类的内容', 99, '0', '1', NOW());
