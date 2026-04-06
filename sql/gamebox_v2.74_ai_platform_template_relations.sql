-- ----------------------------
-- 创建AI平台配置和提示词模板的关联表
-- Version: v2.74
-- Date: 2025-12-31
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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '网站-AI平台配置关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for gb_site_template_relations
-- ----------------------------
DROP TABLE IF EXISTS `gb_site_template_relations`;
CREATE TABLE `gb_site_template_relations`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `site_id` bigint NOT NULL COMMENT '网站ID',
  `prompt_template_id` bigint NOT NULL COMMENT '提示词模板ID',
  `relation_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'include' COMMENT '关联类型：include-正向关联 exclude-排除默认配置',
  `is_visible` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1' COMMENT '是否可见：0-否 1-是',
  `is_editable` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1' COMMENT '是否可编辑：0-否 1-是',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_site_template_type`(`site_id` ASC, `prompt_template_id` ASC, `relation_type` ASC) USING BTREE,
  INDEX `idx_site_id`(`site_id` ASC) USING BTREE,
  INDEX `idx_prompt_template_id`(`prompt_template_id` ASC) USING BTREE,
  INDEX `idx_relation_type`(`relation_type` ASC) USING BTREE,
  INDEX `idx_site_type`(`site_id` ASC, `relation_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '网站-提示词模板关联表' ROW_FORMAT = Dynamic;
