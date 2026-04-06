-- 工作流多站点关联支持
-- 添加工作流与网站的关联关系表

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
  UNIQUE INDEX `uk_site_workflow`(`site_id` ASC, `workflow_id` ASC) USING BTREE,
  INDEX `idx_site_id`(`site_id` ASC) USING BTREE,
  INDEX `idx_workflow_id`(`workflow_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '网站工作流关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gb_site_workflow_relation
-- ----------------------------
