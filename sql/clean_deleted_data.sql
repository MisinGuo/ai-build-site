-- ----------------------------
-- 1. 清理业务表中被逻辑删除的数据 (RuoYi框架默认 del_flag = '2' 为删除状态)
-- ----------------------------
DELETE FROM `gb_ai_platforms` WHERE `del_flag` = '2';
DELETE FROM `gb_articles` WHERE `del_flag` = '2';
DELETE FROM `gb_categories` WHERE `del_flag` = '2';
DELETE FROM `gb_drama_vendors` WHERE `del_flag` = '2';
DELETE FROM `gb_dramas` WHERE `del_flag` = '2';
DELETE FROM `gb_game_activities` WHERE `del_flag` = '2';
DELETE FROM `gb_game_boxes` WHERE `del_flag` = '2';
DELETE FROM `gb_game_giftcodes` WHERE `del_flag` = '2';
DELETE FROM `gb_game_materials` WHERE `del_flag` = '2';
DELETE FROM `gb_game_servers` WHERE `del_flag` = '2';
DELETE FROM `gb_games` WHERE `del_flag` = '2';
DELETE FROM `gb_master_article_dramas` WHERE `del_flag` = '2';
DELETE FROM `gb_master_article_game_boxes` WHERE `del_flag` = '2';
DELETE FROM `gb_master_article_games` WHERE `del_flag` = '2';
DELETE FROM `gb_master_articles` WHERE `del_flag` = '2';
DELETE FROM `gb_prompt_templates` WHERE `del_flag` = '2';
DELETE FROM `gb_sites` WHERE `del_flag` = '2';
DELETE FROM `gb_storage_configs` WHERE `del_flag` = '2';
DELETE FROM `gb_tags` WHERE `del_flag` = '2';
DELETE FROM `gb_title_pool` WHERE `del_flag` = '2';

-- ----------------------------
-- 2. 清理系统用户、角色、部门表中被逻辑删除的数据
-- ----------------------------
DELETE FROM `sys_dept` WHERE `del_flag` = '2';
DELETE FROM `sys_role` WHERE `del_flag` = '2';
DELETE FROM `sys_user` WHERE `del_flag` = '2';

-- ----------------------------
-- 3. 清理废弃的关联“孤儿”数据 (主记录被删除后残留的关联表数据)
-- ----------------------------
-- 游戏盒子关联
DELETE FROM `gb_box_game_relations` WHERE `game_id` NOT IN (SELECT `id` FROM `gb_games`);
DELETE FROM `gb_box_game_relations` WHERE `box_id` NOT IN (SELECT `id` FROM `gb_game_boxes`);
DELETE FROM `gb_box_field_mappings` WHERE `box_id` NOT IN (SELECT `id` FROM `gb_game_boxes`);
DELETE FROM `gb_box_category_relations` WHERE `box_id` NOT IN (SELECT `id` FROM `gb_game_boxes`);
DELETE FROM `gb_box_category_relations` WHERE `category_id` NOT IN (SELECT `id` FROM `gb_categories`);

-- 游戏分类关联
DELETE FROM `gb_game_category_relations` WHERE `game_id` NOT IN (SELECT `id` FROM `gb_games`);
DELETE FROM `gb_game_category_relations` WHERE `category_id` NOT IN (SELECT `id` FROM `gb_categories`);

-- 站点关联
DELETE FROM `gb_site_category_relations` WHERE `site_id` NOT IN (SELECT `id` FROM `gb_sites`);
DELETE FROM `gb_site_category_relations` WHERE `category_id` NOT IN (SELECT `id` FROM `gb_categories`);
DELETE FROM `gb_user_site_relation` WHERE `site_id` NOT IN (SELECT `id` FROM `gb_sites`);
DELETE FROM `gb_site_ai_platform_relations` WHERE `site_id` NOT IN (SELECT `id` FROM `gb_sites`);
DELETE FROM `gb_site_ai_platform_relations` WHERE `ai_platform_id` NOT IN (SELECT `id` FROM `gb_ai_platforms`);
DELETE FROM `gb_site_master_article_relations` WHERE `site_id` NOT IN (SELECT `id` FROM `gb_sites`);
DELETE FROM `gb_site_master_article_relations` WHERE `master_article_id` NOT IN (SELECT `id` FROM `gb_master_articles`);
DELETE FROM `gb_site_title_batch_relations` WHERE `site_id` NOT IN (SELECT `id` FROM `gb_sites`);

-- 系统权限关联
DELETE FROM `sys_user_role` WHERE `user_id` NOT IN (SELECT `user_id` FROM `sys_user`);
DELETE FROM `sys_user_role` WHERE `role_id` NOT IN (SELECT `role_id` FROM `sys_role`);
DELETE FROM `sys_role_menu` WHERE `role_id` NOT IN (SELECT `role_id` FROM `sys_role`);
DELETE FROM `sys_role_menu` WHERE `menu_id` NOT IN (SELECT `menu_id` FROM `sys_menu`);
DELETE FROM `sys_role_dept` WHERE `role_id` NOT IN (SELECT `role_id` FROM `sys_role`);
DELETE FROM `sys_role_dept` WHERE `dept_id` NOT IN (SELECT `dept_id` FROM `sys_dept`);
DELETE FROM `sys_user_post` WHERE `user_id` NOT IN (SELECT `user_id` FROM `sys_user`);
DELETE FROM `sys_user_post` WHERE `post_id` NOT IN (SELECT `post_id` FROM `sys_post`);

-- ----------------------------
-- 4. 清空所有日志表、导入批次和工作流执行产生的沉淀记录
-- ----------------------------
TRUNCATE TABLE `gb_workflow_step_execution`;
TRUNCATE TABLE `gb_workflow_execution`;
TRUNCATE TABLE `gb_import_batch`;
TRUNCATE TABLE `gb_game_change_log`;
TRUNCATE TABLE `gb_title_import_log`;
TRUNCATE TABLE `sys_job_log`;
TRUNCATE TABLE `sys_logininfor`;
TRUNCATE TABLE `sys_oper_log`;
