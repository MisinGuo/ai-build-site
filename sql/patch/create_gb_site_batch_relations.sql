-- 创建网站-标题池批次关联表（用于管理哪些网站排除了哪些默认标题批次）
CREATE TABLE IF NOT EXISTS `gb_site_title_batch_relations` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `site_id` bigint NOT NULL COMMENT '网站ID',
  `title_batch_id` bigint NOT NULL COMMENT '标题池批次ID',
  `relation_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'exclude' COMMENT '关联类型：exclude-排除',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_site_title_batch`(`site_id` ASC, `title_batch_id` ASC, `relation_type` ASC) USING BTREE,
  INDEX `idx_site_id`(`site_id` ASC) USING BTREE,
  INDEX `idx_title_batch_id`(`title_batch_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '网站-标题池批次关联表（排除关系）' ROW_FORMAT = Dynamic;
