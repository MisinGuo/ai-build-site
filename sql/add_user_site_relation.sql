-- =====================================================
-- 用户-站点隔离功能迁移脚本
-- 版本：v1.0  日期：2026-03-17
-- 说明：
--   1. gb_sites 添加 is_personal 字段
--   2. 新增 gb_user_site_relation 用户-站点关联表
--   3. 为 admin 用户创建个人默认站点
--   4. 将 site_id=0 的数据迁移到 admin 个人站点
--   5. 将现有所有站点分配给 admin 用户
-- =====================================================

-- 1. gb_sites 添加 is_personal 字段
ALTER TABLE gb_sites
    ADD COLUMN is_personal tinyint(1) NOT NULL DEFAULT 0
        COMMENT '是否个人默认站点：0-普通站点 1-用户专属个人默认站点' AFTER sort_order;

-- 2. 新增用户-站点关联表
CREATE TABLE IF NOT EXISTS `gb_user_site_relation` (
    `id`          bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`     bigint NOT NULL COMMENT '用户ID（关联 sys_user.user_id）',
    `site_id`     bigint NOT NULL COMMENT '站点ID（关联 gb_sites.id）',
    `is_default`  tinyint(1) NOT NULL DEFAULT 0
                    COMMENT '是否为该用户的默认站点：1-是（个人默认站点），0-否',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_site` (`user_id`, `site_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '用户-站点关联表（控制用户可见站点范围）';

-- 3. 为 admin 用户（user_id=1）创建个人默认站点
--    code/domain 使用 _personal_ 前缀命名，避免与真实站点冲突
INSERT INTO gb_sites (name, code, domain, site_type, is_personal, sort_order, status, del_flag, create_by, default_locale)
VALUES ('admin个人默认站点', '_personal_1', '_personal_1', 'personal', 1, -1, '1', '0', 'admin', 'zh-CN');

-- 记录 admin 个人站点 ID（供后续语句使用）
SET @admin_personal_site_id = LAST_INSERT_ID();

-- 4. 将 site_id=0 的业务数据迁移到 admin 个人站点
--    （这些数据原本是全局共享配置，现在变为 admin 个人站点下的数据）
UPDATE gb_games        SET site_id = @admin_personal_site_id WHERE site_id = 0;
UPDATE gb_game_boxes   SET site_id = @admin_personal_site_id WHERE site_id = 0;
UPDATE gb_categories   SET site_id = @admin_personal_site_id WHERE site_id = 0;
UPDATE gb_workflow     SET site_id = @admin_personal_site_id WHERE site_id = 0 OR site_id IS NULL;
UPDATE gb_atomic_tool  SET site_id = @admin_personal_site_id WHERE site_id = 0 OR site_id IS NULL;
UPDATE gb_ai_platforms SET site_id = @admin_personal_site_id WHERE site_id = 0;
UPDATE gb_storage_configs SET site_id = @admin_personal_site_id WHERE site_id IS NULL OR site_id = 0;
UPDATE gb_prompt_templates SET site_id = @admin_personal_site_id WHERE site_id IS NULL OR site_id = 0;
UPDATE gb_title_pool_batch SET site_id = @admin_personal_site_id WHERE site_id = 0;

-- 5. gb_site_category_relations 等中间关联表同步更新
UPDATE gb_site_category_relations      SET site_id = @admin_personal_site_id WHERE site_id = 0;
UPDATE gb_site_game_relations          SET site_id = @admin_personal_site_id WHERE site_id = 0;
UPDATE gb_site_box_relations           SET site_id = @admin_personal_site_id WHERE site_id = 0;
UPDATE gb_site_ai_platform_relations   SET site_id = @admin_personal_site_id WHERE site_id = 0;
UPDATE gb_site_atomic_tool_relations   SET site_id = @admin_personal_site_id WHERE site_id = 0;
UPDATE gb_site_workflow_relation       SET site_id = @admin_personal_site_id WHERE site_id = 0;

-- 6. 将所有现有普通站点分配给 admin（user_id=1），is_default=0
INSERT IGNORE INTO gb_user_site_relation (user_id, site_id, is_default)
SELECT 1, id, 0
FROM gb_sites
WHERE del_flag = '0'
  AND is_personal = 0;

-- 7. 将 admin 个人站点设为 admin 的默认站点（is_default=1）
INSERT IGNORE INTO gb_user_site_relation (user_id, site_id, is_default)
VALUES (1, @admin_personal_site_id, 1);

SELECT CONCAT('迁移完成，admin 个人站点 ID = ', @admin_personal_site_id) AS result;
