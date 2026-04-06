/*
 Navicat Premium Dump SQL

 Source Server         : local-remote.zeusai.top
 Source Server Type    : PostgreSQL
 Source Server Version : 160013 (160013)
 Source Host           : local-remote.zeusai.top:25432
 Source Catalog        : aisite
 Source Schema         : public

 Target Server Type    : PostgreSQL
 Target Server Version : 160013 (160013)
 File Encoding         : 65001

 Date: 06/04/2026 10:21:00
*/


-- ----------------------------
-- Type structure for gtrgm
-- ----------------------------
DROP TYPE IF EXISTS "public"."gtrgm";
CREATE TYPE "public"."gtrgm" (
  INPUT = "public"."gtrgm_in",
  OUTPUT = "public"."gtrgm_out",
  INTERNALLENGTH = VARIABLE,
  CATEGORY = U,
  DELIMITER = ','
);

-- ----------------------------
-- Sequence structure for ai_atomic_tools_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."ai_atomic_tools_id_seq";
CREATE SEQUENCE "public"."ai_atomic_tools_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for ai_categories_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."ai_categories_id_seq";
CREATE SEQUENCE "public"."ai_categories_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for ai_content_item_categories_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."ai_content_item_categories_id_seq";
CREATE SEQUENCE "public"."ai_content_item_categories_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for ai_content_items_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."ai_content_items_id_seq";
CREATE SEQUENCE "public"."ai_content_items_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for ai_content_types_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."ai_content_types_id_seq";
CREATE SEQUENCE "public"."ai_content_types_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for ai_matrix_groups_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."ai_matrix_groups_id_seq";
CREATE SEQUENCE "public"."ai_matrix_groups_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for ai_operation_logs_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."ai_operation_logs_id_seq";
CREATE SEQUENCE "public"."ai_operation_logs_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for ai_operation_tasks_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."ai_operation_tasks_id_seq";
CREATE SEQUENCE "public"."ai_operation_tasks_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for ai_platforms_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."ai_platforms_id_seq";
CREATE SEQUENCE "public"."ai_platforms_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for ai_prompt_templates_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."ai_prompt_templates_id_seq";
CREATE SEQUENCE "public"."ai_prompt_templates_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for ai_seo_metrics_snapshots_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."ai_seo_metrics_snapshots_id_seq";
CREATE SEQUENCE "public"."ai_seo_metrics_snapshots_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for ai_sites_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."ai_sites_id_seq";
CREATE SEQUENCE "public"."ai_sites_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for ai_storage_configs_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."ai_storage_configs_id_seq";
CREATE SEQUENCE "public"."ai_storage_configs_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for ai_translations_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."ai_translations_id_seq";
CREATE SEQUENCE "public"."ai_translations_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for ai_workflow_executions_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."ai_workflow_executions_id_seq";
CREATE SEQUENCE "public"."ai_workflow_executions_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for ai_workflow_step_executions_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."ai_workflow_step_executions_id_seq";
CREATE SEQUENCE "public"."ai_workflow_step_executions_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for ai_workflows_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."ai_workflows_id_seq";
CREATE SEQUENCE "public"."ai_workflows_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for gen_table_column_column_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."gen_table_column_column_id_seq";
CREATE SEQUENCE "public"."gen_table_column_column_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for gen_table_table_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."gen_table_table_id_seq";
CREATE SEQUENCE "public"."gen_table_table_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for sys_config_config_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."sys_config_config_id_seq";
CREATE SEQUENCE "public"."sys_config_config_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for sys_dept_dept_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."sys_dept_dept_id_seq";
CREATE SEQUENCE "public"."sys_dept_dept_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for sys_dict_data_dict_code_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."sys_dict_data_dict_code_seq";
CREATE SEQUENCE "public"."sys_dict_data_dict_code_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for sys_dict_type_dict_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."sys_dict_type_dict_id_seq";
CREATE SEQUENCE "public"."sys_dict_type_dict_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for sys_job_job_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."sys_job_job_id_seq";
CREATE SEQUENCE "public"."sys_job_job_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for sys_job_log_job_log_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."sys_job_log_job_log_id_seq";
CREATE SEQUENCE "public"."sys_job_log_job_log_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for sys_logininfor_info_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."sys_logininfor_info_id_seq";
CREATE SEQUENCE "public"."sys_logininfor_info_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for sys_menu_menu_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."sys_menu_menu_id_seq";
CREATE SEQUENCE "public"."sys_menu_menu_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for sys_notice_notice_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."sys_notice_notice_id_seq";
CREATE SEQUENCE "public"."sys_notice_notice_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for sys_oper_log_oper_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."sys_oper_log_oper_id_seq";
CREATE SEQUENCE "public"."sys_oper_log_oper_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for sys_post_post_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."sys_post_post_id_seq";
CREATE SEQUENCE "public"."sys_post_post_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for sys_role_role_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."sys_role_role_id_seq";
CREATE SEQUENCE "public"."sys_role_role_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for sys_user_user_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."sys_user_user_id_seq";
CREATE SEQUENCE "public"."sys_user_user_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Table structure for ai_atomic_tools
-- ----------------------------
DROP TABLE IF EXISTS "public"."ai_atomic_tools";
CREATE TABLE "public"."ai_atomic_tools" (
  "id" int8 NOT NULL DEFAULT nextval('ai_atomic_tools_id_seq'::regclass),
  "tool_code" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "tool_name" varchar(200) COLLATE "pg_catalog"."default" NOT NULL,
  "category" varchar(50) COLLATE "pg_catalog"."default" DEFAULT 'general'::character varying,
  "description" text COLLATE "pg_catalog"."default",
  "input_schema" jsonb DEFAULT '{}'::jsonb,
  "output_schema" jsonb DEFAULT '{}'::jsonb,
  "is_builtin" char(1) COLLATE "pg_catalog"."default" DEFAULT '1'::bpchar,
  "is_enabled" char(1) COLLATE "pg_catalog"."default" DEFAULT '1'::bpchar,
  "sort_order" int4 DEFAULT 0,
  "create_time" timestamptz(6) DEFAULT now(),
  "update_time" timestamptz(6) DEFAULT now()
)
;
COMMENT ON TABLE "public"."ai_atomic_tools" IS '原子工具注册表';

-- ----------------------------
-- Records of ai_atomic_tools
-- ----------------------------
INSERT INTO "public"."ai_atomic_tools" VALUES (1, 'site_create', '创建站点', 'deploy', '创建新站点并初始化配置', '{}', '{}', '1', '1', 0, '2026-04-06 10:04:16.641335+08', '2026-04-06 10:04:16.641335+08');
INSERT INTO "public"."ai_atomic_tools" VALUES (2, 'site_config_generate', '生成站点配置', 'ai', '使用 AI 生成站点 site_config JSON', '{}', '{}', '1', '1', 0, '2026-04-06 10:04:16.641335+08', '2026-04-06 10:04:16.641335+08');
INSERT INTO "public"."ai_atomic_tools" VALUES (3, 'content_generate', '生成内容', 'ai', '使用 AI 批量生成内容项', '{}', '{}', '1', '1', 0, '2026-04-06 10:04:16.641335+08', '2026-04-06 10:04:16.641335+08');
INSERT INTO "public"."ai_atomic_tools" VALUES (4, 'content_refresh', '刷新内容', 'content', '重新生成或更新现有内容', '{}', '{}', '1', '1', 0, '2026-04-06 10:04:16.641335+08', '2026-04-06 10:04:16.641335+08');
INSERT INTO "public"."ai_atomic_tools" VALUES (5, 'seo_optimize', 'SEO 优化', 'seo', 'AI 生成或优化 title/description', '{}', '{}', '1', '1', 0, '2026-04-06 10:04:16.641335+08', '2026-04-06 10:04:16.641335+08');
INSERT INTO "public"."ai_atomic_tools" VALUES (6, 'cf_deploy', 'Cloudflare 部署', 'deploy', '触发 Cloudflare Pages 部署', '{}', '{}', '1', '1', 0, '2026-04-06 10:04:16.641335+08', '2026-04-06 10:04:16.641335+08');
INSERT INTO "public"."ai_atomic_tools" VALUES (7, 'interlink_build', '构建内链网络', 'seo', '基于语义相似度自动建立站内链接', '{}', '{}', '1', '1', 0, '2026-04-06 10:04:16.641335+08', '2026-04-06 10:04:16.641335+08');
INSERT INTO "public"."ai_atomic_tools" VALUES (8, 'dead_link_scan', '死链扫描', 'seo', '扫描站点死链并生成修复报告', '{}', '{}', '1', '1', 0, '2026-04-06 10:04:16.641335+08', '2026-04-06 10:04:16.641335+08');

-- ----------------------------
-- Table structure for ai_categories
-- ----------------------------
DROP TABLE IF EXISTS "public"."ai_categories";
CREATE TABLE "public"."ai_categories" (
  "id" int8 NOT NULL DEFAULT nextval('ai_categories_id_seq'::regclass),
  "site_id" int8,
  "type_code" varchar(50) COLLATE "pg_catalog"."default",
  "parent_id" int8 DEFAULT 0,
  "name" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "slug" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "icon" varchar(100) COLLATE "pg_catalog"."default",
  "description" varchar(500) COLLATE "pg_catalog"."default",
  "sort_order" int4 DEFAULT 0,
  "item_count" int4 DEFAULT 0,
  "status" char(1) COLLATE "pg_catalog"."default" DEFAULT '1'::bpchar,
  "del_flag" char(1) COLLATE "pg_catalog"."default" DEFAULT '0'::bpchar,
  "create_by" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "create_time" timestamptz(6) DEFAULT now(),
  "update_by" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "update_time" timestamptz(6) DEFAULT now()
)
;
COMMENT ON TABLE "public"."ai_categories" IS '通用分类表';

-- ----------------------------
-- Records of ai_categories
-- ----------------------------

-- ----------------------------
-- Table structure for ai_content_item_categories
-- ----------------------------
DROP TABLE IF EXISTS "public"."ai_content_item_categories";
CREATE TABLE "public"."ai_content_item_categories" (
  "id" int8 NOT NULL DEFAULT nextval('ai_content_item_categories_id_seq'::regclass),
  "item_id" int8 NOT NULL,
  "category_id" int8 NOT NULL,
  "is_primary" char(1) COLLATE "pg_catalog"."default" DEFAULT '0'::bpchar
)
;
COMMENT ON TABLE "public"."ai_content_item_categories" IS '内容项-分类关联表';

-- ----------------------------
-- Records of ai_content_item_categories
-- ----------------------------

-- ----------------------------
-- Table structure for ai_content_items
-- ----------------------------
DROP TABLE IF EXISTS "public"."ai_content_items";
CREATE TABLE "public"."ai_content_items" (
  "id" int8 NOT NULL DEFAULT nextval('ai_content_items_id_seq'::regclass),
  "site_id" int8 NOT NULL,
  "type_code" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "title" varchar(500) COLLATE "pg_catalog"."default" NOT NULL,
  "slug" varchar(500) COLLATE "pg_catalog"."default" NOT NULL,
  "cover_image" varchar(500) COLLATE "pg_catalog"."default",
  "summary" text COLLATE "pg_catalog"."default",
  "content" text COLLATE "pg_catalog"."default",
  "fields_json" jsonb DEFAULT '{}'::jsonb,
  "seo_title" varchar(255) COLLATE "pg_catalog"."default",
  "seo_description" varchar(500) COLLATE "pg_catalog"."default",
  "seo_keywords" varchar(500) COLLATE "pg_catalog"."default",
  "canonical_url" varchar(500) COLLATE "pg_catalog"."default",
  "publish_status" varchar(20) COLLATE "pg_catalog"."default" DEFAULT 'draft'::character varying,
  "publish_at" timestamptz(6),
  "is_featured" char(1) COLLATE "pg_catalog"."default" DEFAULT '0'::bpchar,
  "sort_order" int4 DEFAULT 0,
  "view_count" int8 DEFAULT 0,
  "embedding" text COLLATE "pg_catalog"."default",
  "creator_id" int8,
  "dept_id" int8,
  "status" char(1) COLLATE "pg_catalog"."default" DEFAULT '1'::bpchar,
  "del_flag" char(1) COLLATE "pg_catalog"."default" DEFAULT '0'::bpchar,
  "create_by" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "create_time" timestamptz(6) DEFAULT now(),
  "update_by" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "update_time" timestamptz(6) DEFAULT now()
)
;
COMMENT ON TABLE "public"."ai_content_items" IS '内容项表（全行业通用，JSONB 动态字段）';

-- ----------------------------
-- Records of ai_content_items
-- ----------------------------

-- ----------------------------
-- Table structure for ai_content_types
-- ----------------------------
DROP TABLE IF EXISTS "public"."ai_content_types";
CREATE TABLE "public"."ai_content_types" (
  "id" int8 NOT NULL DEFAULT nextval('ai_content_types_id_seq'::regclass),
  "site_id" int8,
  "code" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "name" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "description" varchar(500) COLLATE "pg_catalog"."default",
  "schema_json" jsonb DEFAULT '{}'::jsonb,
  "list_fields" jsonb DEFAULT '[]'::jsonb,
  "detail_fields" jsonb DEFAULT '[]'::jsonb,
  "filter_fields" jsonb DEFAULT '[]'::jsonb,
  "seo_template" jsonb DEFAULT '{}'::jsonb,
  "sort_order" int4 DEFAULT 0,
  "is_system" char(1) COLLATE "pg_catalog"."default" DEFAULT '0'::bpchar,
  "status" char(1) COLLATE "pg_catalog"."default" DEFAULT '1'::bpchar,
  "del_flag" char(1) COLLATE "pg_catalog"."default" DEFAULT '0'::bpchar,
  "create_by" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "create_time" timestamptz(6) DEFAULT now(),
  "update_by" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "update_time" timestamptz(6) DEFAULT now()
)
;
COMMENT ON TABLE "public"."ai_content_types" IS '内容类型表（全行业通用, JSONB 动态 schema）';

-- ----------------------------
-- Records of ai_content_types
-- ----------------------------
INSERT INTO "public"."ai_content_types" VALUES (1, NULL, 'article', '通用文章', '博客/新闻/SEO文章等通用文章类型', '{}', '[]', '[]', '[]', '{}', 0, '1', '1', '0', '', '2026-04-06 10:04:16.641335+08', '', '2026-04-06 10:04:16.641335+08');
INSERT INTO "public"."ai_content_types" VALUES (2, NULL, 'game', '游戏', '游戏推广站点专用', '{}', '[]', '[]', '[]', '{}', 0, '1', '1', '0', '', '2026-04-06 10:04:16.641335+08', '', '2026-04-06 10:04:16.641335+08');
INSERT INTO "public"."ai_content_types" VALUES (3, NULL, 'drama', '短剧', '短剧推广站点专用', '{}', '[]', '[]', '[]', '{}', 0, '1', '1', '0', '', '2026-04-06 10:04:16.641335+08', '', '2026-04-06 10:04:16.641335+08');
INSERT INTO "public"."ai_content_types" VALUES (4, NULL, 'product', '商品', '电商站点商品类型', '{}', '[]', '[]', '[]', '{}', 0, '1', '1', '0', '', '2026-04-06 10:04:16.641335+08', '', '2026-04-06 10:04:16.641335+08');
INSERT INTO "public"."ai_content_types" VALUES (5, NULL, 'property', '房产', '房产中介类站点', '{}', '[]', '[]', '[]', '{}', 0, '1', '1', '0', '', '2026-04-06 10:04:16.641335+08', '', '2026-04-06 10:04:16.641335+08');

-- ----------------------------
-- Table structure for ai_matrix_groups
-- ----------------------------
DROP TABLE IF EXISTS "public"."ai_matrix_groups";
CREATE TABLE "public"."ai_matrix_groups" (
  "id" int8 NOT NULL DEFAULT nextval('ai_matrix_groups_id_seq'::regclass),
  "name" varchar(200) COLLATE "pg_catalog"."default" NOT NULL,
  "description" text COLLATE "pg_catalog"."default",
  "template_site_id" int8,
  "industry" varchar(50) COLLATE "pg_catalog"."default" DEFAULT 'general'::character varying,
  "keyword_list" jsonb DEFAULT '[]'::jsonb,
  "domain_pattern" varchar(200) COLLATE "pg_catalog"."default",
  "config_overrides" jsonb DEFAULT '{}'::jsonb,
  "total_count" int4 DEFAULT 0,
  "built_count" int4 DEFAULT 0,
  "live_count" int4 DEFAULT 0,
  "status" varchar(20) COLLATE "pg_catalog"."default" DEFAULT 'draft'::character varying,
  "creator_id" int8,
  "dept_id" int8,
  "del_flag" char(1) COLLATE "pg_catalog"."default" DEFAULT '0'::bpchar,
  "create_by" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "create_time" timestamptz(6) DEFAULT now(),
  "update_by" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "update_time" timestamptz(6) DEFAULT now()
)
;
COMMENT ON TABLE "public"."ai_matrix_groups" IS '矩阵站组（批量关键词建站）';

-- ----------------------------
-- Records of ai_matrix_groups
-- ----------------------------

-- ----------------------------
-- Table structure for ai_operation_logs
-- ----------------------------
DROP TABLE IF EXISTS "public"."ai_operation_logs";
CREATE TABLE "public"."ai_operation_logs" (
  "id" int8 NOT NULL DEFAULT nextval('ai_operation_logs_id_seq'::regclass),
  "task_id" int8,
  "site_id" int8 NOT NULL,
  "action" varchar(100) COLLATE "pg_catalog"."default",
  "before_state" jsonb,
  "after_state" jsonb,
  "ai_reasoning" text COLLATE "pg_catalog"."default",
  "status" varchar(20) COLLATE "pg_catalog"."default",
  "error_message" text COLLATE "pg_catalog"."default",
  "create_time" timestamptz(6) DEFAULT now()
)
;
COMMENT ON TABLE "public"."ai_operation_logs" IS '操作日志（可回溯/回滚）';

-- ----------------------------
-- Records of ai_operation_logs
-- ----------------------------

-- ----------------------------
-- Table structure for ai_operation_tasks
-- ----------------------------
DROP TABLE IF EXISTS "public"."ai_operation_tasks";
CREATE TABLE "public"."ai_operation_tasks" (
  "id" int8 NOT NULL DEFAULT nextval('ai_operation_tasks_id_seq'::regclass),
  "site_id" int8,
  "matrix_group_id" int8,
  "task_type" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "task_name" varchar(200) COLLATE "pg_catalog"."default",
  "trigger_type" varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
  "cron_expr" varchar(100) COLLATE "pg_catalog"."default",
  "params" jsonb DEFAULT '{}'::jsonb,
  "priority" int2 DEFAULT 5,
  "status" varchar(20) COLLATE "pg_catalog"."default" DEFAULT 'pending'::character varying,
  "last_run_at" timestamptz(6),
  "next_run_at" timestamptz(6),
  "last_result" text COLLATE "pg_catalog"."default",
  "retry_count" int2 DEFAULT 0,
  "max_retries" int2 DEFAULT 3,
  "creator_id" int8,
  "dept_id" int8,
  "del_flag" char(1) COLLATE "pg_catalog"."default" DEFAULT '0'::bpchar,
  "create_by" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "create_time" timestamptz(6) DEFAULT now(),
  "update_by" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "update_time" timestamptz(6) DEFAULT now()
)
;
COMMENT ON TABLE "public"."ai_operation_tasks" IS '运营任务表（AI 自动运营调度）';

-- ----------------------------
-- Records of ai_operation_tasks
-- ----------------------------

-- ----------------------------
-- Table structure for ai_platforms
-- ----------------------------
DROP TABLE IF EXISTS "public"."ai_platforms";
CREATE TABLE "public"."ai_platforms" (
  "id" int8 NOT NULL DEFAULT nextval('ai_platforms_id_seq'::regclass),
  "name" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "platform_type" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "api_key" varchar(500) COLLATE "pg_catalog"."default",
  "base_url" varchar(500) COLLATE "pg_catalog"."default",
  "model_name" varchar(100) COLLATE "pg_catalog"."default",
  "max_tokens" int4 DEFAULT 4096,
  "temperature" numeric(3,2) DEFAULT 0.7,
  "extra_config" jsonb DEFAULT '{}'::jsonb,
  "is_default" char(1) COLLATE "pg_catalog"."default" DEFAULT '0'::bpchar,
  "status" char(1) COLLATE "pg_catalog"."default" DEFAULT '1'::bpchar,
  "del_flag" char(1) COLLATE "pg_catalog"."default" DEFAULT '0'::bpchar,
  "create_by" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "create_time" timestamptz(6) DEFAULT now(),
  "update_by" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "update_time" timestamptz(6) DEFAULT now()
)
;
COMMENT ON TABLE "public"."ai_platforms" IS 'AI 平台配置表';

-- ----------------------------
-- Records of ai_platforms
-- ----------------------------

-- ----------------------------
-- Table structure for ai_prompt_templates
-- ----------------------------
DROP TABLE IF EXISTS "public"."ai_prompt_templates";
CREATE TABLE "public"."ai_prompt_templates" (
  "id" int8 NOT NULL DEFAULT nextval('ai_prompt_templates_id_seq'::regclass),
  "name" varchar(200) COLLATE "pg_catalog"."default" NOT NULL,
  "code" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "scene" varchar(50) COLLATE "pg_catalog"."default",
  "system_prompt" text COLLATE "pg_catalog"."default",
  "user_prompt" text COLLATE "pg_catalog"."default",
  "variables" jsonb DEFAULT '[]'::jsonb,
  "platform_type" varchar(50) COLLATE "pg_catalog"."default",
  "is_builtin" char(1) COLLATE "pg_catalog"."default" DEFAULT '0'::bpchar,
  "status" char(1) COLLATE "pg_catalog"."default" DEFAULT '1'::bpchar,
  "del_flag" char(1) COLLATE "pg_catalog"."default" DEFAULT '0'::bpchar,
  "create_by" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "create_time" timestamptz(6) DEFAULT now(),
  "update_by" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "update_time" timestamptz(6) DEFAULT now()
)
;
COMMENT ON TABLE "public"."ai_prompt_templates" IS 'Prompt 模板表';

-- ----------------------------
-- Records of ai_prompt_templates
-- ----------------------------

-- ----------------------------
-- Table structure for ai_seo_metrics_snapshots
-- ----------------------------
DROP TABLE IF EXISTS "public"."ai_seo_metrics_snapshots";
CREATE TABLE "public"."ai_seo_metrics_snapshots" (
  "id" int8 NOT NULL DEFAULT nextval('ai_seo_metrics_snapshots_id_seq'::regclass),
  "site_id" int8 NOT NULL,
  "snapshot_date" date NOT NULL,
  "indexed_pages" int4 DEFAULT 0,
  "avg_position" numeric(5,2),
  "clicks" int4 DEFAULT 0,
  "impressions" int4 DEFAULT 0,
  "ctr" numeric(5,4),
  "metrics_json" jsonb DEFAULT '{}'::jsonb
)
;
COMMENT ON TABLE "public"."ai_seo_metrics_snapshots" IS 'SEO 指标快照（每日）';

-- ----------------------------
-- Records of ai_seo_metrics_snapshots
-- ----------------------------

-- ----------------------------
-- Table structure for ai_sites
-- ----------------------------
DROP TABLE IF EXISTS "public"."ai_sites";
CREATE TABLE "public"."ai_sites" (
  "id" int8 NOT NULL DEFAULT nextval('ai_sites_id_seq'::regclass),
  "name" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "domain" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "industry" varchar(50) COLLATE "pg_catalog"."default" DEFAULT 'general'::character varying,
  "template_id" int8,
  "logo_url" varchar(500) COLLATE "pg_catalog"."default",
  "favicon_url" varchar(500) COLLATE "pg_catalog"."default",
  "description" text COLLATE "pg_catalog"."default",
  "seo_title" varchar(255) COLLATE "pg_catalog"."default",
  "seo_keywords" varchar(500) COLLATE "pg_catalog"."default",
  "seo_description" varchar(500) COLLATE "pg_catalog"."default",
  "site_config" jsonb DEFAULT '{}'::jsonb,
  "storage_config_id" int8,
  "default_locale" varchar(10) COLLATE "pg_catalog"."default" DEFAULT 'zh-CN'::character varying,
  "supported_locales" jsonb DEFAULT '["zh-CN"]'::jsonb,
  "i18n_mode" varchar(20) COLLATE "pg_catalog"."default" DEFAULT 'subdirectory'::character varying,
  "deploy_status" varchar(20) COLLATE "pg_catalog"."default" DEFAULT 'draft'::character varying,
  "cf_pages_project" varchar(100) COLLATE "pg_catalog"."default",
  "cf_deploy_url" varchar(500) COLLATE "pg_catalog"."default",
  "matrix_group_id" int8,
  "creator_id" int8,
  "dept_id" int8,
  "status" char(1) COLLATE "pg_catalog"."default" DEFAULT '1'::bpchar,
  "del_flag" char(1) COLLATE "pg_catalog"."default" DEFAULT '0'::bpchar,
  "create_by" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "create_time" timestamptz(6) DEFAULT now(),
  "update_by" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "update_time" timestamptz(6) DEFAULT now(),
  "remark" varchar(500) COLLATE "pg_catalog"."default"
)
;
COMMENT ON TABLE "public"."ai_sites" IS '站点表';

-- ----------------------------
-- Records of ai_sites
-- ----------------------------

-- ----------------------------
-- Table structure for ai_storage_configs
-- ----------------------------
DROP TABLE IF EXISTS "public"."ai_storage_configs";
CREATE TABLE "public"."ai_storage_configs" (
  "id" int8 NOT NULL DEFAULT nextval('ai_storage_configs_id_seq'::regclass),
  "site_id" int8,
  "name" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "code" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "storage_type" varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
  "is_default" char(1) COLLATE "pg_catalog"."default" DEFAULT '0'::bpchar,
  "config_json" jsonb DEFAULT '{}'::jsonb,
  "cdn_url" varchar(500) COLLATE "pg_catalog"."default",
  "status" char(1) COLLATE "pg_catalog"."default" DEFAULT '1'::bpchar,
  "del_flag" char(1) COLLATE "pg_catalog"."default" DEFAULT '0'::bpchar,
  "create_by" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "create_time" timestamptz(6) DEFAULT now(),
  "update_by" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "update_time" timestamptz(6) DEFAULT now()
)
;
COMMENT ON TABLE "public"."ai_storage_configs" IS '对象存储配置表';

-- ----------------------------
-- Records of ai_storage_configs
-- ----------------------------

-- ----------------------------
-- Table structure for ai_translations
-- ----------------------------
DROP TABLE IF EXISTS "public"."ai_translations";
CREATE TABLE "public"."ai_translations" (
  "id" int8 NOT NULL DEFAULT nextval('ai_translations_id_seq'::regclass),
  "entity_type" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "entity_id" int8 NOT NULL,
  "locale" varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
  "field_name" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "field_value" text COLLATE "pg_catalog"."default",
  "translation_status" varchar(20) COLLATE "pg_catalog"."default" DEFAULT 'pending'::character varying,
  "create_time" timestamptz(6) DEFAULT now(),
  "update_time" timestamptz(6) DEFAULT now()
)
;
COMMENT ON TABLE "public"."ai_translations" IS '多语言翻译表';

-- ----------------------------
-- Records of ai_translations
-- ----------------------------

-- ----------------------------
-- Table structure for ai_workflow_executions
-- ----------------------------
DROP TABLE IF EXISTS "public"."ai_workflow_executions";
CREATE TABLE "public"."ai_workflow_executions" (
  "id" int8 NOT NULL DEFAULT nextval('ai_workflow_executions_id_seq'::regclass),
  "workflow_id" int8 NOT NULL,
  "workflow_code" varchar(100) COLLATE "pg_catalog"."default",
  "site_id" int8,
  "trigger_type" varchar(20) COLLATE "pg_catalog"."default",
  "input_params" jsonb DEFAULT '{}'::jsonb,
  "output_result" jsonb DEFAULT '{}'::jsonb,
  "status" varchar(20) COLLATE "pg_catalog"."default" DEFAULT 'pending'::character varying,
  "error_message" text COLLATE "pg_catalog"."default",
  "progress" int2 DEFAULT 0,
  "started_at" timestamptz(6),
  "finished_at" timestamptz(6),
  "creator_id" int8,
  "create_time" timestamptz(6) DEFAULT now()
)
;
COMMENT ON TABLE "public"."ai_workflow_executions" IS '工作流执行记录';

-- ----------------------------
-- Records of ai_workflow_executions
-- ----------------------------

-- ----------------------------
-- Table structure for ai_workflow_step_executions
-- ----------------------------
DROP TABLE IF EXISTS "public"."ai_workflow_step_executions";
CREATE TABLE "public"."ai_workflow_step_executions" (
  "id" int8 NOT NULL DEFAULT nextval('ai_workflow_step_executions_id_seq'::regclass),
  "execution_id" int8 NOT NULL,
  "step_index" int4 NOT NULL,
  "step_name" varchar(200) COLLATE "pg_catalog"."default",
  "tool_code" varchar(100) COLLATE "pg_catalog"."default",
  "input_data" jsonb DEFAULT '{}'::jsonb,
  "output_data" jsonb DEFAULT '{}'::jsonb,
  "stream_content" text COLLATE "pg_catalog"."default",
  "status" varchar(20) COLLATE "pg_catalog"."default" DEFAULT 'pending'::character varying,
  "error_message" text COLLATE "pg_catalog"."default",
  "started_at" timestamptz(6),
  "finished_at" timestamptz(6),
  "cost_ms" int4 DEFAULT 0,
  "token_used" int4 DEFAULT 0
)
;
COMMENT ON TABLE "public"."ai_workflow_step_executions" IS '工作流步骤执行记录';

-- ----------------------------
-- Records of ai_workflow_step_executions
-- ----------------------------

-- ----------------------------
-- Table structure for ai_workflows
-- ----------------------------
DROP TABLE IF EXISTS "public"."ai_workflows";
CREATE TABLE "public"."ai_workflows" (
  "id" int8 NOT NULL DEFAULT nextval('ai_workflows_id_seq'::regclass),
  "name" varchar(200) COLLATE "pg_catalog"."default" NOT NULL,
  "code" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "description" text COLLATE "pg_catalog"."default",
  "category" varchar(50) COLLATE "pg_catalog"."default" DEFAULT 'general'::character varying,
  "steps_json" jsonb DEFAULT '[]'::jsonb,
  "params_schema" jsonb DEFAULT '{}'::jsonb,
  "is_builtin" char(1) COLLATE "pg_catalog"."default" DEFAULT '0'::bpchar,
  "is_enabled" char(1) COLLATE "pg_catalog"."default" DEFAULT '1'::bpchar,
  "version" int4 DEFAULT 1,
  "creator_id" int8,
  "del_flag" char(1) COLLATE "pg_catalog"."default" DEFAULT '0'::bpchar,
  "create_by" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "create_time" timestamptz(6) DEFAULT now(),
  "update_by" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "update_time" timestamptz(6) DEFAULT now()
)
;
COMMENT ON TABLE "public"."ai_workflows" IS '工作流定义表';

-- ----------------------------
-- Records of ai_workflows
-- ----------------------------
INSERT INTO "public"."ai_workflows" VALUES (1, 'AI 对话建站', 'ai_site_build', '通过 AI 对话交互式创建站点', 'build', '[]', '{}', '1', '1', 1, NULL, '0', '', '2026-04-06 10:04:16.641335+08', '', '2026-04-06 10:04:16.641335+08');
INSERT INTO "public"."ai_workflows" VALUES (2, '矩阵站批量建站', 'matrix_batch_build', '批量关键词站点并发创建', 'build', '[]', '{}', '1', '1', 1, NULL, '0', '', '2026-04-06 10:04:16.641335+08', '', '2026-04-06 10:04:16.641335+08');
INSERT INTO "public"."ai_workflows" VALUES (3, '内容批量生成', 'content_batch_gen', '根据内容类型批量 AI 生成内容项', 'content', '[]', '{}', '1', '1', 1, NULL, '0', '', '2026-04-06 10:04:16.641335+08', '', '2026-04-06 10:04:16.641335+08');
INSERT INTO "public"."ai_workflows" VALUES (4, 'SEO 定期优化', 'seo_periodic', '定期扫描并优化站点 SEO 指标', 'seo', '[]', '{}', '1', '1', 1, NULL, '0', '', '2026-04-06 10:04:16.641335+08', '', '2026-04-06 10:04:16.641335+08');
INSERT INTO "public"."ai_workflows" VALUES (5, '内链网络维护', 'interlink_maint', '基于向量相似度自动更新矩阵站内链', 'operation', '[]', '{}', '1', '1', 1, NULL, '0', '', '2026-04-06 10:04:16.641335+08', '', '2026-04-06 10:04:16.641335+08');

-- ----------------------------
-- Table structure for gen_table
-- ----------------------------
DROP TABLE IF EXISTS "public"."gen_table";
CREATE TABLE "public"."gen_table" (
  "table_id" int8 NOT NULL DEFAULT nextval('gen_table_table_id_seq'::regclass),
  "table_name" varchar(200) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "table_comment" varchar(500) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "sub_table_name" varchar(64) COLLATE "pg_catalog"."default",
  "sub_table_fk_name" varchar(64) COLLATE "pg_catalog"."default",
  "class_name" varchar(100) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "tpl_category" varchar(200) COLLATE "pg_catalog"."default" DEFAULT 'crud'::character varying,
  "tpl_web_type" varchar(30) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "package_name" varchar(100) COLLATE "pg_catalog"."default",
  "module_name" varchar(30) COLLATE "pg_catalog"."default",
  "business_name" varchar(30) COLLATE "pg_catalog"."default",
  "function_name" varchar(50) COLLATE "pg_catalog"."default",
  "function_author" varchar(50) COLLATE "pg_catalog"."default",
  "gen_type" char(1) COLLATE "pg_catalog"."default" DEFAULT '0'::bpchar,
  "gen_path" varchar(200) COLLATE "pg_catalog"."default" DEFAULT '/'::character varying,
  "options" varchar(1000) COLLATE "pg_catalog"."default",
  "create_by" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "create_time" timestamp(6),
  "update_by" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "update_time" timestamp(6),
  "remark" varchar(500) COLLATE "pg_catalog"."default"
)
;
COMMENT ON TABLE "public"."gen_table" IS '代码生成业务表';

-- ----------------------------
-- Records of gen_table
-- ----------------------------

-- ----------------------------
-- Table structure for gen_table_column
-- ----------------------------
DROP TABLE IF EXISTS "public"."gen_table_column";
CREATE TABLE "public"."gen_table_column" (
  "column_id" int8 NOT NULL DEFAULT nextval('gen_table_column_column_id_seq'::regclass),
  "table_id" int8,
  "column_name" varchar(200) COLLATE "pg_catalog"."default",
  "column_comment" varchar(500) COLLATE "pg_catalog"."default",
  "column_type" varchar(100) COLLATE "pg_catalog"."default",
  "java_type" varchar(500) COLLATE "pg_catalog"."default",
  "java_field" varchar(200) COLLATE "pg_catalog"."default",
  "is_pk" char(1) COLLATE "pg_catalog"."default",
  "is_increment" char(1) COLLATE "pg_catalog"."default",
  "is_required" char(1) COLLATE "pg_catalog"."default",
  "is_insert" char(1) COLLATE "pg_catalog"."default",
  "is_edit" char(1) COLLATE "pg_catalog"."default",
  "is_list" char(1) COLLATE "pg_catalog"."default",
  "is_query" char(1) COLLATE "pg_catalog"."default",
  "query_type" varchar(200) COLLATE "pg_catalog"."default" DEFAULT 'EQ'::character varying,
  "html_type" varchar(200) COLLATE "pg_catalog"."default",
  "dict_type" varchar(200) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "sort" int4,
  "create_by" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "create_time" timestamp(6),
  "update_by" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "update_time" timestamp(6)
)
;
COMMENT ON TABLE "public"."gen_table_column" IS '代码生成业务表字段';

-- ----------------------------
-- Records of gen_table_column
-- ----------------------------

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_config";
CREATE TABLE "public"."sys_config" (
  "config_id" int4 NOT NULL DEFAULT nextval('sys_config_config_id_seq'::regclass),
  "config_name" varchar(100) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "config_key" varchar(100) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "config_value" varchar(500) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "config_type" char(1) COLLATE "pg_catalog"."default" DEFAULT 'N'::bpchar,
  "create_by" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "create_time" timestamp(6),
  "update_by" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "update_time" timestamp(6),
  "remark" varchar(500) COLLATE "pg_catalog"."default"
)
;
COMMENT ON TABLE "public"."sys_config" IS '参数配置表';

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO "public"."sys_config" VALUES (1, '主框架页-默认皮肤样式名称', 'sys.index.skinName', 'skin-blue', 'Y', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_config" VALUES (2, '用户管理-账号初始密码', 'sys.user.initPassword', '123456', 'Y', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_config" VALUES (3, '主框架页-侧边栏主题', 'sys.index.sideTheme', 'theme-dark', 'Y', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_config" VALUES (4, '账号自助-验证码开关', 'sys.account.captchaEnabled', 'true', 'Y', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_config" VALUES (5, '账号自助-是否开启用户注册', 'sys.account.registerUser', 'false', 'Y', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_config" VALUES (6, '用户登录-黑名单列表', 'sys.login.blackIPList', '', 'Y', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_dept";
CREATE TABLE "public"."sys_dept" (
  "dept_id" int8 NOT NULL DEFAULT nextval('sys_dept_dept_id_seq'::regclass),
  "parent_id" int8 DEFAULT 0,
  "ancestors" varchar(50) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "dept_name" varchar(30) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "order_num" int4 DEFAULT 0,
  "leader" varchar(20) COLLATE "pg_catalog"."default",
  "phone" varchar(11) COLLATE "pg_catalog"."default",
  "email" varchar(50) COLLATE "pg_catalog"."default",
  "status" char(1) COLLATE "pg_catalog"."default" DEFAULT '0'::bpchar,
  "del_flag" char(1) COLLATE "pg_catalog"."default" DEFAULT '0'::bpchar,
  "create_by" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "create_time" timestamp(6),
  "update_by" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "update_time" timestamp(6)
)
;
COMMENT ON TABLE "public"."sys_dept" IS '部门表';

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO "public"."sys_dept" VALUES (100, 0, '0', 'AI建站平台', 0, 'admin', '15888888888', 'admin@aisite.io', '0', '0', 'admin', '2026-04-06 10:04:16.641335', '', NULL);
INSERT INTO "public"."sys_dept" VALUES (101, 100, '0,100', '技术部', 1, 'admin', '15888888888', 'admin@aisite.io', '0', '0', 'admin', '2026-04-06 10:04:16.641335', '', NULL);
INSERT INTO "public"."sys_dept" VALUES (102, 100, '0,100', '运营部', 2, 'admin', '15888888888', 'admin@aisite.io', '0', '0', 'admin', '2026-04-06 10:04:16.641335', '', NULL);

-- ----------------------------
-- Table structure for sys_dict_data
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_dict_data";
CREATE TABLE "public"."sys_dict_data" (
  "dict_code" int8 NOT NULL DEFAULT nextval('sys_dict_data_dict_code_seq'::regclass),
  "dict_sort" int4 DEFAULT 0,
  "dict_label" varchar(100) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "dict_value" varchar(100) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "dict_type" varchar(100) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "css_class" varchar(100) COLLATE "pg_catalog"."default",
  "list_class" varchar(100) COLLATE "pg_catalog"."default",
  "is_default" char(1) COLLATE "pg_catalog"."default" DEFAULT 'N'::bpchar,
  "status" char(1) COLLATE "pg_catalog"."default" DEFAULT '0'::bpchar,
  "create_by" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "create_time" timestamp(6),
  "update_by" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "update_time" timestamp(6),
  "remark" varchar(500) COLLATE "pg_catalog"."default"
)
;
COMMENT ON TABLE "public"."sys_dict_data" IS '字典数据表';

-- ----------------------------
-- Records of sys_dict_data
-- ----------------------------
INSERT INTO "public"."sys_dict_data" VALUES (1, 1, '男', '0', 'sys_user_sex', '', '', 'Y', '0', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_dict_data" VALUES (2, 2, '女', '1', 'sys_user_sex', '', '', 'N', '0', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_dict_data" VALUES (3, 3, '未知', '2', 'sys_user_sex', '', '', 'N', '0', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_dict_data" VALUES (4, 1, '显示', '0', 'sys_show_hide', '', 'primary', 'Y', '0', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_dict_data" VALUES (5, 2, '隐藏', '1', 'sys_show_hide', '', 'danger', 'N', '0', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_dict_data" VALUES (6, 1, '正常', '0', 'sys_normal_disable', '', 'primary', 'Y', '0', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_dict_data" VALUES (7, 2, '停用', '1', 'sys_normal_disable', '', 'danger', 'N', '0', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_dict_data" VALUES (8, 1, '正常', '0', 'sys_job_status', '', 'primary', 'Y', '0', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_dict_data" VALUES (9, 2, '暂停', '1', 'sys_job_status', '', 'danger', 'N', '0', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_dict_data" VALUES (10, 1, '默认', 'DEFAULT', 'sys_job_group', '', '', 'Y', '0', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_dict_data" VALUES (11, 2, '系统', 'SYSTEM', 'sys_job_group', '', '', 'N', '0', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_dict_data" VALUES (12, 1, '是', 'Y', 'sys_yes_no', '', 'primary', 'Y', '0', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_dict_data" VALUES (13, 2, '否', 'N', 'sys_yes_no', '', 'danger', 'N', '0', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_dict_data" VALUES (18, 99, '其他', '0', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_dict_data" VALUES (19, 1, '新增', '1', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_dict_data" VALUES (20, 2, '修改', '2', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_dict_data" VALUES (21, 3, '删除', '3', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_dict_data" VALUES (28, 1, '成功', '0', 'sys_common_status', '', 'primary', 'N', '0', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_dict_data" VALUES (29, 2, '失败', '1', 'sys_common_status', '', 'danger', 'N', '0', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');

-- ----------------------------
-- Table structure for sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_dict_type";
CREATE TABLE "public"."sys_dict_type" (
  "dict_id" int8 NOT NULL DEFAULT nextval('sys_dict_type_dict_id_seq'::regclass),
  "dict_name" varchar(100) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "dict_type" varchar(100) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "status" char(1) COLLATE "pg_catalog"."default" DEFAULT '0'::bpchar,
  "create_by" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "create_time" timestamp(6),
  "update_by" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "update_time" timestamp(6),
  "remark" varchar(500) COLLATE "pg_catalog"."default"
)
;
COMMENT ON TABLE "public"."sys_dict_type" IS '字典类型表';

-- ----------------------------
-- Records of sys_dict_type
-- ----------------------------
INSERT INTO "public"."sys_dict_type" VALUES (1, '用户性别', 'sys_user_sex', '0', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_dict_type" VALUES (2, '菜单状态', 'sys_show_hide', '0', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_dict_type" VALUES (3, '系统开关', 'sys_normal_disable', '0', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_dict_type" VALUES (4, '任务状态', 'sys_job_status', '0', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_dict_type" VALUES (5, '任务分组', 'sys_job_group', '0', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_dict_type" VALUES (6, '系统是否', 'sys_yes_no', '0', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_dict_type" VALUES (7, '通知类型', 'sys_notice_type', '0', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_dict_type" VALUES (8, '通知状态', 'sys_notice_status', '0', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_dict_type" VALUES (9, '操作类型', 'sys_oper_type', '0', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_dict_type" VALUES (10, '系统状态', 'sys_common_status', '0', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');

-- ----------------------------
-- Table structure for sys_job
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_job";
CREATE TABLE "public"."sys_job" (
  "job_id" int8 NOT NULL DEFAULT nextval('sys_job_job_id_seq'::regclass),
  "job_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "job_group" varchar(64) COLLATE "pg_catalog"."default" NOT NULL DEFAULT 'DEFAULT'::character varying,
  "invoke_target" varchar(500) COLLATE "pg_catalog"."default" NOT NULL,
  "cron_expression" varchar(255) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "misfire_policy" varchar(20) COLLATE "pg_catalog"."default" DEFAULT '3'::character varying,
  "concurrent" char(1) COLLATE "pg_catalog"."default" DEFAULT '1'::bpchar,
  "status" char(1) COLLATE "pg_catalog"."default" DEFAULT '0'::bpchar,
  "create_by" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "create_time" timestamp(6),
  "update_by" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "update_time" timestamp(6),
  "remark" varchar(500) COLLATE "pg_catalog"."default" DEFAULT ''::character varying
)
;
COMMENT ON TABLE "public"."sys_job" IS '定时任务调度表';

-- ----------------------------
-- Records of sys_job
-- ----------------------------
INSERT INTO "public"."sys_job" VALUES (1, '系统默认（无参）', 'DEFAULT', 'ryTask.ryNoParams', '0/10 * * * * ?', '3', '1', '1', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_job" VALUES (2, '系统默认（有参）', 'DEFAULT', 'ryTask.ryParams(''ry'')', '0/15 * * * * ?', '3', '1', '1', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');

-- ----------------------------
-- Table structure for sys_job_log
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_job_log";
CREATE TABLE "public"."sys_job_log" (
  "job_log_id" int8 NOT NULL DEFAULT nextval('sys_job_log_job_log_id_seq'::regclass),
  "job_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "job_group" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "invoke_target" varchar(500) COLLATE "pg_catalog"."default" NOT NULL,
  "job_message" varchar(500) COLLATE "pg_catalog"."default",
  "status" char(1) COLLATE "pg_catalog"."default" DEFAULT '0'::bpchar,
  "exception_info" varchar(2000) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "create_time" timestamp(6)
)
;
COMMENT ON TABLE "public"."sys_job_log" IS '定时任务调度日志表';

-- ----------------------------
-- Records of sys_job_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_logininfor
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_logininfor";
CREATE TABLE "public"."sys_logininfor" (
  "info_id" int8 NOT NULL DEFAULT nextval('sys_logininfor_info_id_seq'::regclass),
  "user_name" varchar(50) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "ipaddr" varchar(128) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "login_location" varchar(255) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "browser" varchar(50) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "os" varchar(50) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "status" char(1) COLLATE "pg_catalog"."default" DEFAULT '0'::bpchar,
  "msg" varchar(255) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "login_time" timestamp(6)
)
;
COMMENT ON TABLE "public"."sys_logininfor" IS '系统访问记录';

-- ----------------------------
-- Records of sys_logininfor
-- ----------------------------
INSERT INTO "public"."sys_logininfor" VALUES (101, 'admin', '127.0.0.1', '内网IP', 'Chrome 146', 'Windows10', '1', '验证码已失效', '2026-04-06 10:11:42.389433');
INSERT INTO "public"."sys_logininfor" VALUES (102, 'admin', '127.0.0.1', '内网IP', 'Chrome 146', 'Windows10', '0', '登录成功', '2026-04-06 10:11:46.336071');

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_menu";
CREATE TABLE "public"."sys_menu" (
  "menu_id" int8 NOT NULL DEFAULT nextval('sys_menu_menu_id_seq'::regclass),
  "menu_name" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "parent_id" int8 DEFAULT 0,
  "order_num" int4 DEFAULT 0,
  "path" varchar(200) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "component" varchar(255) COLLATE "pg_catalog"."default",
  "query" varchar(255) COLLATE "pg_catalog"."default",
  "route_name" varchar(50) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "is_frame" int4 DEFAULT 1,
  "is_cache" int4 DEFAULT 0,
  "menu_type" char(1) COLLATE "pg_catalog"."default" DEFAULT ''::bpchar,
  "visible" char(1) COLLATE "pg_catalog"."default" DEFAULT '0'::bpchar,
  "status" char(1) COLLATE "pg_catalog"."default" DEFAULT '0'::bpchar,
  "perms" varchar(100) COLLATE "pg_catalog"."default",
  "icon" varchar(100) COLLATE "pg_catalog"."default" DEFAULT '#'::character varying,
  "create_by" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "create_time" timestamp(6),
  "update_by" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "update_time" timestamp(6),
  "remark" varchar(500) COLLATE "pg_catalog"."default" DEFAULT ''::character varying
)
;
COMMENT ON TABLE "public"."sys_menu" IS '菜单权限表';

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO "public"."sys_menu" VALUES (1, '系统管理', 0, 1, 'system', NULL, '', '', 1, 0, 'M', '0', '0', '', 'system', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (2, '系统监控', 0, 2, 'monitor', NULL, '', '', 1, 0, 'M', '0', '0', '', 'monitor', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (3, '系统工具', 0, 3, 'tool', NULL, '', '', 1, 0, 'M', '0', '0', '', 'tool', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (100, '用户管理', 1, 1, 'user', 'system/user/index', '', '', 1, 0, 'C', '0', '0', 'system:user:list', 'user', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (101, '角色管理', 1, 2, 'role', 'system/role/index', '', '', 1, 0, 'C', '0', '0', 'system:role:list', 'peoples', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (102, '菜单管理', 1, 3, 'menu', 'system/menu/index', '', '', 1, 0, 'C', '0', '0', 'system:menu:list', 'tree-table', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (103, '部门管理', 1, 4, 'dept', 'system/dept/index', '', '', 1, 0, 'C', '0', '0', 'system:dept:list', 'tree', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (104, '岗位管理', 1, 5, 'post', 'system/post/index', '', '', 1, 0, 'C', '0', '0', 'system:post:list', 'post', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (105, '字典管理', 1, 6, 'dict', 'system/dict/index', '', '', 1, 0, 'C', '0', '0', 'system:dict:list', 'dict', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (106, '参数设置', 1, 7, 'config', 'system/config/index', '', '', 1, 0, 'C', '0', '0', 'system:config:list', 'edit', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (107, '通知公告', 1, 8, 'notice', 'system/notice/index', '', '', 1, 0, 'C', '0', '0', 'system:notice:list', 'message', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (108, '日志管理', 1, 9, 'log', '', '', '', 1, 0, 'M', '0', '0', '', 'log', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (109, '在线用户', 2, 1, 'online', 'monitor/online/index', '', '', 1, 0, 'C', '0', '0', 'monitor:online:list', 'online', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (110, '定时任务', 2, 2, 'job', 'monitor/job/index', '', '', 1, 0, 'C', '0', '0', 'monitor:job:list', 'job', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (111, '数据监控', 2, 3, 'druid', 'monitor/druid/index', '', '', 1, 0, 'C', '0', '0', 'monitor:druid:list', 'druid', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (112, '服务监控', 2, 4, 'server', 'monitor/server/index', '', '', 1, 0, 'C', '0', '0', 'monitor:server:list', 'server', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (113, '缓存监控', 2, 5, 'cache', 'monitor/cache/index', '', '', 1, 0, 'C', '0', '0', 'monitor:cache:list', 'redis', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (115, '表单构建', 3, 1, 'build', 'tool/build/index', '', '', 1, 0, 'C', '0', '0', 'tool:build:list', 'build', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (116, '代码生成', 3, 2, 'gen', 'tool/gen/index', '', '', 1, 0, 'C', '0', '0', 'tool:gen:list', 'code', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (117, '系统接口', 3, 3, 'swagger', 'tool/swagger/index', '', '', 1, 0, 'C', '0', '0', 'tool:swagger:list', 'swagger', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (500, '操作日志', 108, 1, 'operlog', 'monitor/operlog/index', '', '', 1, 0, 'C', '0', '0', 'monitor:operlog:list', 'form', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (501, '登录日志', 108, 2, 'logininfor', 'monitor/logininfor/index', '', '', 1, 0, 'C', '0', '0', 'monitor:logininfor:list', 'logininfor', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (1000, '用户查询', 100, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:query', '#', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (1001, '用户新增', 100, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:add', '#', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (1002, '用户修改', 100, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:edit', '#', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (1003, '用户删除', 100, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:remove', '#', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (1004, '用户导出', 100, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:export', '#', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (1005, '用户导入', 100, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:import', '#', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (1006, '重置密码', 100, 7, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:resetPwd', '#', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (1007, '角色查询', 101, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:query', '#', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (1008, '角色新增', 101, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:add', '#', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (1009, '角色修改', 101, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:edit', '#', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (1010, '角色删除', 101, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:remove', '#', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (1011, '角色导出', 101, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:export', '#', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (1055, '生成查询', 116, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:query', '#', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (1056, '生成修改', 116, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:edit', '#', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (1057, '生成删除', 116, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:remove', '#', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (1058, '导入代码', 116, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:import', '#', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (1059, '预览代码', 116, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:preview', '#', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (1060, '生成代码', 116, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:code', '#', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (2000, 'AI 建站平台', 0, 5, 'aisite', NULL, '', '', 1, 0, 'M', '0', '0', '', 'robot', 'admin', '2026-04-06 10:04:16.641335', '', NULL, 'AI 建站运营平台');
INSERT INTO "public"."sys_menu" VALUES (2001, '站点管理', 2000, 1, 'sites', 'aisite/sites/index', '', '', 1, 0, 'C', '0', '0', 'aisite:site:list', 'list', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (2002, 'AI 建站助手', 2000, 2, 'builder', 'aisite/builder/index', '', '', 1, 0, 'C', '0', '0', 'aisite:builder:use', 'wand-magic', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (2003, '矩阵站管理', 2000, 3, 'matrix', 'aisite/matrix/index', '', '', 1, 0, 'C', '0', '0', 'aisite:matrix:list', 'grid', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (2004, '内容管理', 2000, 4, 'contents', 'aisite/contents/index', '', '', 1, 0, 'C', '0', '0', 'aisite:content:list', 'documentation', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (2005, 'AI 运营中心', 2000, 5, 'operations', 'aisite/operations/index', '', '', 1, 0, 'C', '0', '0', 'aisite:operation:list', 'calendar', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (2006, 'SEO 看板', 2000, 6, 'seo', 'aisite/seo/index', '', '', 1, 0, 'C', '0', '0', 'aisite:seo:view', 'chart', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (2007, '工作流', 2000, 7, 'workflows', 'aisite/workflows/index', '', '', 1, 0, 'C', '0', '0', 'aisite:workflow:list', 'flow', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (2008, 'AI 平台配置', 2000, 8, 'ai-platforms', 'aisite/platforms/index', '', '', 1, 0, 'C', '0', '0', 'aisite:platform:list', 'setting', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (2100, '站点查询', 2001, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'aisite:site:query', '#', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (2101, '站点新增', 2001, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'aisite:site:add', '#', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (2102, '站点修改', 2001, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'aisite:site:edit', '#', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (2103, '站点删除', 2001, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'aisite:site:remove', '#', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_menu" VALUES (2104, '站点部署', 2001, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'aisite:site:deploy', '#', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');

-- ----------------------------
-- Table structure for sys_notice
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_notice";
CREATE TABLE "public"."sys_notice" (
  "notice_id" int4 NOT NULL DEFAULT nextval('sys_notice_notice_id_seq'::regclass),
  "notice_title" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "notice_type" char(1) COLLATE "pg_catalog"."default" NOT NULL,
  "notice_content" text COLLATE "pg_catalog"."default",
  "status" char(1) COLLATE "pg_catalog"."default" DEFAULT '0'::bpchar,
  "create_by" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "create_time" timestamp(6),
  "update_by" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "update_time" timestamp(6),
  "remark" varchar(255) COLLATE "pg_catalog"."default"
)
;
COMMENT ON TABLE "public"."sys_notice" IS '通知公告表';

-- ----------------------------
-- Records of sys_notice
-- ----------------------------

-- ----------------------------
-- Table structure for sys_oper_log
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_oper_log";
CREATE TABLE "public"."sys_oper_log" (
  "oper_id" int8 NOT NULL DEFAULT nextval('sys_oper_log_oper_id_seq'::regclass),
  "title" varchar(50) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "business_type" int2 DEFAULT 0,
  "method" varchar(200) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "request_method" varchar(10) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "operator_type" int2 DEFAULT 0,
  "oper_name" varchar(50) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "dept_name" varchar(50) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "oper_url" varchar(255) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "oper_ip" varchar(128) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "oper_location" varchar(255) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "oper_param" varchar(2000) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "json_result" varchar(2000) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "status" int2 DEFAULT 0,
  "error_msg" varchar(2000) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "oper_time" timestamp(6),
  "cost_time" int8 DEFAULT 0
)
;
COMMENT ON TABLE "public"."sys_oper_log" IS '操作日志记录';

-- ----------------------------
-- Records of sys_oper_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_post
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_post";
CREATE TABLE "public"."sys_post" (
  "post_id" int8 NOT NULL DEFAULT nextval('sys_post_post_id_seq'::regclass),
  "post_code" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "post_name" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "post_sort" int4 NOT NULL,
  "status" char(1) COLLATE "pg_catalog"."default" NOT NULL,
  "create_by" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "create_time" timestamp(6),
  "update_by" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "update_time" timestamp(6),
  "remark" varchar(500) COLLATE "pg_catalog"."default"
)
;
COMMENT ON TABLE "public"."sys_post" IS '岗位信息表';

-- ----------------------------
-- Records of sys_post
-- ----------------------------
INSERT INTO "public"."sys_post" VALUES (1, 'ceo', '董事长', 1, '0', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_post" VALUES (2, 'se', '技术总监', 2, '0', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_post" VALUES (3, 'pm', '产品经理', 3, '0', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');
INSERT INTO "public"."sys_post" VALUES (4, 'user', '普通员工', 4, '0', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_role";
CREATE TABLE "public"."sys_role" (
  "role_id" int8 NOT NULL DEFAULT nextval('sys_role_role_id_seq'::regclass),
  "role_name" varchar(30) COLLATE "pg_catalog"."default" NOT NULL,
  "role_key" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "role_sort" int4 NOT NULL,
  "data_scope" char(1) COLLATE "pg_catalog"."default" DEFAULT '1'::bpchar,
  "menu_check_strictly" int2 DEFAULT 1,
  "dept_check_strictly" int2 DEFAULT 1,
  "status" char(1) COLLATE "pg_catalog"."default" NOT NULL,
  "del_flag" char(1) COLLATE "pg_catalog"."default" DEFAULT '0'::bpchar,
  "create_by" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "create_time" timestamp(6),
  "update_by" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "update_time" timestamp(6),
  "remark" varchar(500) COLLATE "pg_catalog"."default"
)
;
COMMENT ON TABLE "public"."sys_role" IS '角色信息表';

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO "public"."sys_role" VALUES (1, '超级管理员', 'admin', 1, '1', 1, 1, '0', '0', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '超级管理员');
INSERT INTO "public"."sys_role" VALUES (2, '普通角色', 'common', 2, '2', 1, 1, '0', '0', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '普通角色');

-- ----------------------------
-- Table structure for sys_role_dept
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_role_dept";
CREATE TABLE "public"."sys_role_dept" (
  "role_id" int8 NOT NULL,
  "dept_id" int8 NOT NULL
)
;
COMMENT ON TABLE "public"."sys_role_dept" IS '角色和部门关联表';

-- ----------------------------
-- Records of sys_role_dept
-- ----------------------------

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_role_menu";
CREATE TABLE "public"."sys_role_menu" (
  "role_id" int8 NOT NULL,
  "menu_id" int8 NOT NULL
)
;
COMMENT ON TABLE "public"."sys_role_menu" IS '角色和菜单关联表';

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_user";
CREATE TABLE "public"."sys_user" (
  "user_id" int8 NOT NULL DEFAULT nextval('sys_user_user_id_seq'::regclass),
  "dept_id" int8,
  "user_name" varchar(30) COLLATE "pg_catalog"."default" NOT NULL,
  "nick_name" varchar(30) COLLATE "pg_catalog"."default" NOT NULL,
  "user_type" varchar(2) COLLATE "pg_catalog"."default" DEFAULT '00'::character varying,
  "email" varchar(50) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "phonenumber" varchar(11) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "sex" char(1) COLLATE "pg_catalog"."default" DEFAULT '0'::bpchar,
  "avatar" varchar(100) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "password" varchar(100) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "status" char(1) COLLATE "pg_catalog"."default" DEFAULT '0'::bpchar,
  "del_flag" char(1) COLLATE "pg_catalog"."default" DEFAULT '0'::bpchar,
  "login_ip" varchar(128) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "login_date" timestamp(6),
  "pwd_update_date" timestamp(6),
  "create_by" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "create_time" timestamp(6),
  "update_by" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "update_time" timestamp(6),
  "remark" varchar(500) COLLATE "pg_catalog"."default"
)
;
COMMENT ON TABLE "public"."sys_user" IS '用户信息表';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO "public"."sys_user" VALUES (1, 101, 'admin', '管理员', '00', 'admin@aisite.io', '15888888888', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', '2026-04-06 10:11:45.792', '2026-04-06 10:04:16.641335', 'admin', '2026-04-06 10:04:16.641335', '', NULL, '超级管理员');

-- ----------------------------
-- Table structure for sys_user_post
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_user_post";
CREATE TABLE "public"."sys_user_post" (
  "user_id" int8 NOT NULL,
  "post_id" int8 NOT NULL
)
;
COMMENT ON TABLE "public"."sys_user_post" IS '用户与岗位关联表';

-- ----------------------------
-- Records of sys_user_post
-- ----------------------------
INSERT INTO "public"."sys_user_post" VALUES (1, 1);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_user_role";
CREATE TABLE "public"."sys_user_role" (
  "user_id" int8 NOT NULL,
  "role_id" int8 NOT NULL
)
;
COMMENT ON TABLE "public"."sys_user_role" IS '用户和角色关联表';

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO "public"."sys_user_role" VALUES (1, 1);

-- ----------------------------
-- Function structure for gin_extract_query_trgm
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."gin_extract_query_trgm"(text, internal, int2, internal, internal, internal, internal);
CREATE FUNCTION "public"."gin_extract_query_trgm"(text, internal, int2, internal, internal, internal, internal)
  RETURNS "pg_catalog"."internal" AS '$libdir/pg_trgm', 'gin_extract_query_trgm'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;

-- ----------------------------
-- Function structure for gin_extract_value_trgm
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."gin_extract_value_trgm"(text, internal);
CREATE FUNCTION "public"."gin_extract_value_trgm"(text, internal)
  RETURNS "pg_catalog"."internal" AS '$libdir/pg_trgm', 'gin_extract_value_trgm'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;

-- ----------------------------
-- Function structure for gin_trgm_consistent
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."gin_trgm_consistent"(internal, int2, text, int4, internal, internal, internal, internal);
CREATE FUNCTION "public"."gin_trgm_consistent"(internal, int2, text, int4, internal, internal, internal, internal)
  RETURNS "pg_catalog"."bool" AS '$libdir/pg_trgm', 'gin_trgm_consistent'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;

-- ----------------------------
-- Function structure for gin_trgm_triconsistent
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."gin_trgm_triconsistent"(internal, int2, text, int4, internal, internal, internal);
CREATE FUNCTION "public"."gin_trgm_triconsistent"(internal, int2, text, int4, internal, internal, internal)
  RETURNS "pg_catalog"."char" AS '$libdir/pg_trgm', 'gin_trgm_triconsistent'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;

-- ----------------------------
-- Function structure for gtrgm_compress
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."gtrgm_compress"(internal);
CREATE FUNCTION "public"."gtrgm_compress"(internal)
  RETURNS "pg_catalog"."internal" AS '$libdir/pg_trgm', 'gtrgm_compress'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;

-- ----------------------------
-- Function structure for gtrgm_consistent
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."gtrgm_consistent"(internal, text, int2, oid, internal);
CREATE FUNCTION "public"."gtrgm_consistent"(internal, text, int2, oid, internal)
  RETURNS "pg_catalog"."bool" AS '$libdir/pg_trgm', 'gtrgm_consistent'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;

-- ----------------------------
-- Function structure for gtrgm_decompress
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."gtrgm_decompress"(internal);
CREATE FUNCTION "public"."gtrgm_decompress"(internal)
  RETURNS "pg_catalog"."internal" AS '$libdir/pg_trgm', 'gtrgm_decompress'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;

-- ----------------------------
-- Function structure for gtrgm_distance
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."gtrgm_distance"(internal, text, int2, oid, internal);
CREATE FUNCTION "public"."gtrgm_distance"(internal, text, int2, oid, internal)
  RETURNS "pg_catalog"."float8" AS '$libdir/pg_trgm', 'gtrgm_distance'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;

-- ----------------------------
-- Function structure for gtrgm_in
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."gtrgm_in"(cstring);
CREATE FUNCTION "public"."gtrgm_in"(cstring)
  RETURNS "public"."gtrgm" AS '$libdir/pg_trgm', 'gtrgm_in'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;

-- ----------------------------
-- Function structure for gtrgm_options
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."gtrgm_options"(internal);
CREATE FUNCTION "public"."gtrgm_options"(internal)
  RETURNS "pg_catalog"."void" AS '$libdir/pg_trgm', 'gtrgm_options'
  LANGUAGE c IMMUTABLE
  COST 1;

-- ----------------------------
-- Function structure for gtrgm_out
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."gtrgm_out"("public"."gtrgm");
CREATE FUNCTION "public"."gtrgm_out"("public"."gtrgm")
  RETURNS "pg_catalog"."cstring" AS '$libdir/pg_trgm', 'gtrgm_out'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;

-- ----------------------------
-- Function structure for gtrgm_penalty
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."gtrgm_penalty"(internal, internal, internal);
CREATE FUNCTION "public"."gtrgm_penalty"(internal, internal, internal)
  RETURNS "pg_catalog"."internal" AS '$libdir/pg_trgm', 'gtrgm_penalty'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;

-- ----------------------------
-- Function structure for gtrgm_picksplit
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."gtrgm_picksplit"(internal, internal);
CREATE FUNCTION "public"."gtrgm_picksplit"(internal, internal)
  RETURNS "pg_catalog"."internal" AS '$libdir/pg_trgm', 'gtrgm_picksplit'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;

-- ----------------------------
-- Function structure for gtrgm_same
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."gtrgm_same"("public"."gtrgm", "public"."gtrgm", internal);
CREATE FUNCTION "public"."gtrgm_same"("public"."gtrgm", "public"."gtrgm", internal)
  RETURNS "pg_catalog"."internal" AS '$libdir/pg_trgm', 'gtrgm_same'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;

-- ----------------------------
-- Function structure for gtrgm_union
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."gtrgm_union"(internal, internal);
CREATE FUNCTION "public"."gtrgm_union"(internal, internal)
  RETURNS "public"."gtrgm" AS '$libdir/pg_trgm', 'gtrgm_union'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;

-- ----------------------------
-- Function structure for set_limit
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."set_limit"(float4);
CREATE FUNCTION "public"."set_limit"(float4)
  RETURNS "pg_catalog"."float4" AS '$libdir/pg_trgm', 'set_limit'
  LANGUAGE c VOLATILE STRICT
  COST 1;

-- ----------------------------
-- Function structure for show_limit
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."show_limit"();
CREATE FUNCTION "public"."show_limit"()
  RETURNS "pg_catalog"."float4" AS '$libdir/pg_trgm', 'show_limit'
  LANGUAGE c STABLE STRICT
  COST 1;

-- ----------------------------
-- Function structure for show_trgm
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."show_trgm"(text);
CREATE FUNCTION "public"."show_trgm"(text)
  RETURNS "pg_catalog"."_text" AS '$libdir/pg_trgm', 'show_trgm'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;

-- ----------------------------
-- Function structure for similarity
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."similarity"(text, text);
CREATE FUNCTION "public"."similarity"(text, text)
  RETURNS "pg_catalog"."float4" AS '$libdir/pg_trgm', 'similarity'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;

-- ----------------------------
-- Function structure for similarity_dist
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."similarity_dist"(text, text);
CREATE FUNCTION "public"."similarity_dist"(text, text)
  RETURNS "pg_catalog"."float4" AS '$libdir/pg_trgm', 'similarity_dist'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;

-- ----------------------------
-- Function structure for similarity_op
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."similarity_op"(text, text);
CREATE FUNCTION "public"."similarity_op"(text, text)
  RETURNS "pg_catalog"."bool" AS '$libdir/pg_trgm', 'similarity_op'
  LANGUAGE c STABLE STRICT
  COST 1;

-- ----------------------------
-- Function structure for strict_word_similarity
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."strict_word_similarity"(text, text);
CREATE FUNCTION "public"."strict_word_similarity"(text, text)
  RETURNS "pg_catalog"."float4" AS '$libdir/pg_trgm', 'strict_word_similarity'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;

-- ----------------------------
-- Function structure for strict_word_similarity_commutator_op
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."strict_word_similarity_commutator_op"(text, text);
CREATE FUNCTION "public"."strict_word_similarity_commutator_op"(text, text)
  RETURNS "pg_catalog"."bool" AS '$libdir/pg_trgm', 'strict_word_similarity_commutator_op'
  LANGUAGE c STABLE STRICT
  COST 1;

-- ----------------------------
-- Function structure for strict_word_similarity_dist_commutator_op
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."strict_word_similarity_dist_commutator_op"(text, text);
CREATE FUNCTION "public"."strict_word_similarity_dist_commutator_op"(text, text)
  RETURNS "pg_catalog"."float4" AS '$libdir/pg_trgm', 'strict_word_similarity_dist_commutator_op'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;

-- ----------------------------
-- Function structure for strict_word_similarity_dist_op
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."strict_word_similarity_dist_op"(text, text);
CREATE FUNCTION "public"."strict_word_similarity_dist_op"(text, text)
  RETURNS "pg_catalog"."float4" AS '$libdir/pg_trgm', 'strict_word_similarity_dist_op'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;

-- ----------------------------
-- Function structure for strict_word_similarity_op
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."strict_word_similarity_op"(text, text);
CREATE FUNCTION "public"."strict_word_similarity_op"(text, text)
  RETURNS "pg_catalog"."bool" AS '$libdir/pg_trgm', 'strict_word_similarity_op'
  LANGUAGE c STABLE STRICT
  COST 1;

-- ----------------------------
-- Function structure for word_similarity
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."word_similarity"(text, text);
CREATE FUNCTION "public"."word_similarity"(text, text)
  RETURNS "pg_catalog"."float4" AS '$libdir/pg_trgm', 'word_similarity'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;

-- ----------------------------
-- Function structure for word_similarity_commutator_op
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."word_similarity_commutator_op"(text, text);
CREATE FUNCTION "public"."word_similarity_commutator_op"(text, text)
  RETURNS "pg_catalog"."bool" AS '$libdir/pg_trgm', 'word_similarity_commutator_op'
  LANGUAGE c STABLE STRICT
  COST 1;

-- ----------------------------
-- Function structure for word_similarity_dist_commutator_op
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."word_similarity_dist_commutator_op"(text, text);
CREATE FUNCTION "public"."word_similarity_dist_commutator_op"(text, text)
  RETURNS "pg_catalog"."float4" AS '$libdir/pg_trgm', 'word_similarity_dist_commutator_op'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;

-- ----------------------------
-- Function structure for word_similarity_dist_op
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."word_similarity_dist_op"(text, text);
CREATE FUNCTION "public"."word_similarity_dist_op"(text, text)
  RETURNS "pg_catalog"."float4" AS '$libdir/pg_trgm', 'word_similarity_dist_op'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;

-- ----------------------------
-- Function structure for word_similarity_op
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."word_similarity_op"(text, text);
CREATE FUNCTION "public"."word_similarity_op"(text, text)
  RETURNS "pg_catalog"."bool" AS '$libdir/pg_trgm', 'word_similarity_op'
  LANGUAGE c STABLE STRICT
  COST 1;

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."ai_atomic_tools_id_seq"
OWNED BY "public"."ai_atomic_tools"."id";
SELECT setval('"public"."ai_atomic_tools_id_seq"', 8, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."ai_categories_id_seq"
OWNED BY "public"."ai_categories"."id";
SELECT setval('"public"."ai_categories_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."ai_content_item_categories_id_seq"
OWNED BY "public"."ai_content_item_categories"."id";
SELECT setval('"public"."ai_content_item_categories_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."ai_content_items_id_seq"
OWNED BY "public"."ai_content_items"."id";
SELECT setval('"public"."ai_content_items_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."ai_content_types_id_seq"
OWNED BY "public"."ai_content_types"."id";
SELECT setval('"public"."ai_content_types_id_seq"', 5, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."ai_matrix_groups_id_seq"
OWNED BY "public"."ai_matrix_groups"."id";
SELECT setval('"public"."ai_matrix_groups_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."ai_operation_logs_id_seq"
OWNED BY "public"."ai_operation_logs"."id";
SELECT setval('"public"."ai_operation_logs_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."ai_operation_tasks_id_seq"
OWNED BY "public"."ai_operation_tasks"."id";
SELECT setval('"public"."ai_operation_tasks_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."ai_platforms_id_seq"
OWNED BY "public"."ai_platforms"."id";
SELECT setval('"public"."ai_platforms_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."ai_prompt_templates_id_seq"
OWNED BY "public"."ai_prompt_templates"."id";
SELECT setval('"public"."ai_prompt_templates_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."ai_seo_metrics_snapshots_id_seq"
OWNED BY "public"."ai_seo_metrics_snapshots"."id";
SELECT setval('"public"."ai_seo_metrics_snapshots_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."ai_sites_id_seq"
OWNED BY "public"."ai_sites"."id";
SELECT setval('"public"."ai_sites_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."ai_storage_configs_id_seq"
OWNED BY "public"."ai_storage_configs"."id";
SELECT setval('"public"."ai_storage_configs_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."ai_translations_id_seq"
OWNED BY "public"."ai_translations"."id";
SELECT setval('"public"."ai_translations_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."ai_workflow_executions_id_seq"
OWNED BY "public"."ai_workflow_executions"."id";
SELECT setval('"public"."ai_workflow_executions_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."ai_workflow_step_executions_id_seq"
OWNED BY "public"."ai_workflow_step_executions"."id";
SELECT setval('"public"."ai_workflow_step_executions_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."ai_workflows_id_seq"
OWNED BY "public"."ai_workflows"."id";
SELECT setval('"public"."ai_workflows_id_seq"', 5, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."gen_table_column_column_id_seq"
OWNED BY "public"."gen_table_column"."column_id";
SELECT setval('"public"."gen_table_column_column_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."gen_table_table_id_seq"
OWNED BY "public"."gen_table"."table_id";
SELECT setval('"public"."gen_table_table_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."sys_config_config_id_seq"
OWNED BY "public"."sys_config"."config_id";
SELECT setval('"public"."sys_config_config_id_seq"', 100, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."sys_dept_dept_id_seq"
OWNED BY "public"."sys_dept"."dept_id";
SELECT setval('"public"."sys_dept_dept_id_seq"', 200, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."sys_dict_data_dict_code_seq"
OWNED BY "public"."sys_dict_data"."dict_code";
SELECT setval('"public"."sys_dict_data_dict_code_seq"', 100, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."sys_dict_type_dict_id_seq"
OWNED BY "public"."sys_dict_type"."dict_id";
SELECT setval('"public"."sys_dict_type_dict_id_seq"', 100, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."sys_job_job_id_seq"
OWNED BY "public"."sys_job"."job_id";
SELECT setval('"public"."sys_job_job_id_seq"', 100, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."sys_job_log_job_log_id_seq"
OWNED BY "public"."sys_job_log"."job_log_id";
SELECT setval('"public"."sys_job_log_job_log_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."sys_logininfor_info_id_seq"
OWNED BY "public"."sys_logininfor"."info_id";
SELECT setval('"public"."sys_logininfor_info_id_seq"', 102, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."sys_menu_menu_id_seq"
OWNED BY "public"."sys_menu"."menu_id";
SELECT setval('"public"."sys_menu_menu_id_seq"', 3000, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."sys_notice_notice_id_seq"
OWNED BY "public"."sys_notice"."notice_id";
SELECT setval('"public"."sys_notice_notice_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."sys_oper_log_oper_id_seq"
OWNED BY "public"."sys_oper_log"."oper_id";
SELECT setval('"public"."sys_oper_log_oper_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."sys_post_post_id_seq"
OWNED BY "public"."sys_post"."post_id";
SELECT setval('"public"."sys_post_post_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."sys_role_role_id_seq"
OWNED BY "public"."sys_role"."role_id";
SELECT setval('"public"."sys_role_role_id_seq"', 100, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."sys_user_user_id_seq"
OWNED BY "public"."sys_user"."user_id";
SELECT setval('"public"."sys_user_user_id_seq"', 100, true);

-- ----------------------------
-- Indexes structure for table ai_atomic_tools
-- ----------------------------
CREATE INDEX "idx_ai_tools_category" ON "public"."ai_atomic_tools" USING btree (
  "category" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Uniques structure for table ai_atomic_tools
-- ----------------------------
ALTER TABLE "public"."ai_atomic_tools" ADD CONSTRAINT "ai_atomic_tools_tool_code_key" UNIQUE ("tool_code");

-- ----------------------------
-- Primary Key structure for table ai_atomic_tools
-- ----------------------------
ALTER TABLE "public"."ai_atomic_tools" ADD CONSTRAINT "ai_atomic_tools_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table ai_categories
-- ----------------------------
CREATE INDEX "idx_ai_categories_parent" ON "public"."ai_categories" USING btree (
  "parent_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_ai_categories_site" ON "public"."ai_categories" USING btree (
  "site_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_ai_categories_type" ON "public"."ai_categories" USING btree (
  "type_code" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Uniques structure for table ai_categories
-- ----------------------------
ALTER TABLE "public"."ai_categories" ADD CONSTRAINT "ai_categories_site_id_slug_key" UNIQUE ("site_id", "slug");

-- ----------------------------
-- Primary Key structure for table ai_categories
-- ----------------------------
ALTER TABLE "public"."ai_categories" ADD CONSTRAINT "ai_categories_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table ai_content_item_categories
-- ----------------------------
CREATE INDEX "idx_ai_item_cat_category" ON "public"."ai_content_item_categories" USING btree (
  "category_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_ai_item_cat_item" ON "public"."ai_content_item_categories" USING btree (
  "item_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);

-- ----------------------------
-- Uniques structure for table ai_content_item_categories
-- ----------------------------
ALTER TABLE "public"."ai_content_item_categories" ADD CONSTRAINT "ai_content_item_categories_item_id_category_id_key" UNIQUE ("item_id", "category_id");

-- ----------------------------
-- Primary Key structure for table ai_content_item_categories
-- ----------------------------
ALTER TABLE "public"."ai_content_item_categories" ADD CONSTRAINT "ai_content_item_categories_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table ai_content_items
-- ----------------------------
CREATE INDEX "idx_ai_content_items_fields" ON "public"."ai_content_items" USING gin (
  "fields_json" "pg_catalog"."jsonb_ops"
);
CREATE INDEX "idx_ai_content_items_site" ON "public"."ai_content_items" USING btree (
  "site_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_ai_content_items_status" ON "public"."ai_content_items" USING btree (
  "publish_status" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_ai_content_items_type" ON "public"."ai_content_items" USING btree (
  "type_code" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Uniques structure for table ai_content_items
-- ----------------------------
ALTER TABLE "public"."ai_content_items" ADD CONSTRAINT "ai_content_items_site_id_type_code_slug_key" UNIQUE ("site_id", "type_code", "slug");

-- ----------------------------
-- Primary Key structure for table ai_content_items
-- ----------------------------
ALTER TABLE "public"."ai_content_items" ADD CONSTRAINT "ai_content_items_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table ai_content_types
-- ----------------------------
CREATE INDEX "idx_ai_content_types_schema" ON "public"."ai_content_types" USING gin (
  "schema_json" "pg_catalog"."jsonb_ops"
);
CREATE INDEX "idx_ai_content_types_site" ON "public"."ai_content_types" USING btree (
  "site_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);

-- ----------------------------
-- Uniques structure for table ai_content_types
-- ----------------------------
ALTER TABLE "public"."ai_content_types" ADD CONSTRAINT "ai_content_types_code_site_id_key" UNIQUE ("code", "site_id");

-- ----------------------------
-- Primary Key structure for table ai_content_types
-- ----------------------------
ALTER TABLE "public"."ai_content_types" ADD CONSTRAINT "ai_content_types_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table ai_matrix_groups
-- ----------------------------
CREATE INDEX "idx_ai_matrix_groups_kws" ON "public"."ai_matrix_groups" USING gin (
  "keyword_list" "pg_catalog"."jsonb_ops"
);
CREATE INDEX "idx_ai_matrix_groups_status" ON "public"."ai_matrix_groups" USING btree (
  "status" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table ai_matrix_groups
-- ----------------------------
ALTER TABLE "public"."ai_matrix_groups" ADD CONSTRAINT "ai_matrix_groups_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table ai_operation_logs
-- ----------------------------
CREATE INDEX "idx_ai_op_logs_created" ON "public"."ai_operation_logs" USING btree (
  "create_time" "pg_catalog"."timestamptz_ops" ASC NULLS LAST
);
CREATE INDEX "idx_ai_op_logs_site" ON "public"."ai_operation_logs" USING btree (
  "site_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_ai_op_logs_status" ON "public"."ai_operation_logs" USING btree (
  "status" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_ai_op_logs_task" ON "public"."ai_operation_logs" USING btree (
  "task_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table ai_operation_logs
-- ----------------------------
ALTER TABLE "public"."ai_operation_logs" ADD CONSTRAINT "ai_operation_logs_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table ai_operation_tasks
-- ----------------------------
CREATE INDEX "idx_ai_op_tasks_next_run" ON "public"."ai_operation_tasks" USING btree (
  "next_run_at" "pg_catalog"."timestamptz_ops" ASC NULLS LAST
);
CREATE INDEX "idx_ai_op_tasks_site" ON "public"."ai_operation_tasks" USING btree (
  "site_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_ai_op_tasks_status" ON "public"."ai_operation_tasks" USING btree (
  "status" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_ai_op_tasks_type" ON "public"."ai_operation_tasks" USING btree (
  "task_type" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table ai_operation_tasks
-- ----------------------------
ALTER TABLE "public"."ai_operation_tasks" ADD CONSTRAINT "ai_operation_tasks_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table ai_platforms
-- ----------------------------
ALTER TABLE "public"."ai_platforms" ADD CONSTRAINT "ai_platforms_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table ai_prompt_templates
-- ----------------------------
CREATE INDEX "idx_ai_prompt_scene" ON "public"."ai_prompt_templates" USING btree (
  "scene" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Uniques structure for table ai_prompt_templates
-- ----------------------------
ALTER TABLE "public"."ai_prompt_templates" ADD CONSTRAINT "ai_prompt_templates_code_key" UNIQUE ("code");

-- ----------------------------
-- Primary Key structure for table ai_prompt_templates
-- ----------------------------
ALTER TABLE "public"."ai_prompt_templates" ADD CONSTRAINT "ai_prompt_templates_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table ai_seo_metrics_snapshots
-- ----------------------------
CREATE INDEX "idx_ai_seo_site_date" ON "public"."ai_seo_metrics_snapshots" USING btree (
  "site_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "snapshot_date" "pg_catalog"."date_ops" DESC NULLS FIRST
);

-- ----------------------------
-- Uniques structure for table ai_seo_metrics_snapshots
-- ----------------------------
ALTER TABLE "public"."ai_seo_metrics_snapshots" ADD CONSTRAINT "ai_seo_metrics_snapshots_site_id_snapshot_date_key" UNIQUE ("site_id", "snapshot_date");

-- ----------------------------
-- Primary Key structure for table ai_seo_metrics_snapshots
-- ----------------------------
ALTER TABLE "public"."ai_seo_metrics_snapshots" ADD CONSTRAINT "ai_seo_metrics_snapshots_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table ai_sites
-- ----------------------------
CREATE INDEX "idx_ai_sites_config" ON "public"."ai_sites" USING gin (
  "site_config" "pg_catalog"."jsonb_ops"
);
CREATE INDEX "idx_ai_sites_creator" ON "public"."ai_sites" USING btree (
  "creator_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_ai_sites_deploy_status" ON "public"."ai_sites" USING btree (
  "deploy_status" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_ai_sites_dept" ON "public"."ai_sites" USING btree (
  "dept_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_ai_sites_industry" ON "public"."ai_sites" USING btree (
  "industry" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_ai_sites_matrix_group" ON "public"."ai_sites" USING btree (
  "matrix_group_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);

-- ----------------------------
-- Uniques structure for table ai_sites
-- ----------------------------
ALTER TABLE "public"."ai_sites" ADD CONSTRAINT "ai_sites_domain_key" UNIQUE ("domain");

-- ----------------------------
-- Primary Key structure for table ai_sites
-- ----------------------------
ALTER TABLE "public"."ai_sites" ADD CONSTRAINT "ai_sites_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Uniques structure for table ai_storage_configs
-- ----------------------------
ALTER TABLE "public"."ai_storage_configs" ADD CONSTRAINT "ai_storage_configs_code_key" UNIQUE ("code");

-- ----------------------------
-- Primary Key structure for table ai_storage_configs
-- ----------------------------
ALTER TABLE "public"."ai_storage_configs" ADD CONSTRAINT "ai_storage_configs_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table ai_translations
-- ----------------------------
CREATE INDEX "idx_ai_trans_entity" ON "public"."ai_translations" USING btree (
  "entity_type" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
  "entity_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_ai_trans_locale" ON "public"."ai_translations" USING btree (
  "locale" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Uniques structure for table ai_translations
-- ----------------------------
ALTER TABLE "public"."ai_translations" ADD CONSTRAINT "ai_translations_entity_type_entity_id_locale_field_name_key" UNIQUE ("entity_type", "entity_id", "locale", "field_name");

-- ----------------------------
-- Primary Key structure for table ai_translations
-- ----------------------------
ALTER TABLE "public"."ai_translations" ADD CONSTRAINT "ai_translations_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table ai_workflow_executions
-- ----------------------------
CREATE INDEX "idx_ai_wf_exec_created" ON "public"."ai_workflow_executions" USING btree (
  "create_time" "pg_catalog"."timestamptz_ops" DESC NULLS FIRST
);
CREATE INDEX "idx_ai_wf_exec_site" ON "public"."ai_workflow_executions" USING btree (
  "site_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_ai_wf_exec_status" ON "public"."ai_workflow_executions" USING btree (
  "status" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_ai_wf_exec_workflow" ON "public"."ai_workflow_executions" USING btree (
  "workflow_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table ai_workflow_executions
-- ----------------------------
ALTER TABLE "public"."ai_workflow_executions" ADD CONSTRAINT "ai_workflow_executions_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table ai_workflow_step_executions
-- ----------------------------
CREATE INDEX "idx_ai_wf_step_execution" ON "public"."ai_workflow_step_executions" USING btree (
  "execution_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_ai_wf_step_status" ON "public"."ai_workflow_step_executions" USING btree (
  "status" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table ai_workflow_step_executions
-- ----------------------------
ALTER TABLE "public"."ai_workflow_step_executions" ADD CONSTRAINT "ai_workflow_step_executions_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table ai_workflows
-- ----------------------------
CREATE INDEX "idx_ai_workflows_category" ON "public"."ai_workflows" USING btree (
  "category" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Uniques structure for table ai_workflows
-- ----------------------------
ALTER TABLE "public"."ai_workflows" ADD CONSTRAINT "ai_workflows_code_key" UNIQUE ("code");

-- ----------------------------
-- Primary Key structure for table ai_workflows
-- ----------------------------
ALTER TABLE "public"."ai_workflows" ADD CONSTRAINT "ai_workflows_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table gen_table
-- ----------------------------
ALTER TABLE "public"."gen_table" ADD CONSTRAINT "gen_table_pkey" PRIMARY KEY ("table_id");

-- ----------------------------
-- Primary Key structure for table gen_table_column
-- ----------------------------
ALTER TABLE "public"."gen_table_column" ADD CONSTRAINT "gen_table_column_pkey" PRIMARY KEY ("column_id");

-- ----------------------------
-- Primary Key structure for table sys_config
-- ----------------------------
ALTER TABLE "public"."sys_config" ADD CONSTRAINT "sys_config_pkey" PRIMARY KEY ("config_id");

-- ----------------------------
-- Primary Key structure for table sys_dept
-- ----------------------------
ALTER TABLE "public"."sys_dept" ADD CONSTRAINT "sys_dept_pkey" PRIMARY KEY ("dept_id");

-- ----------------------------
-- Primary Key structure for table sys_dict_data
-- ----------------------------
ALTER TABLE "public"."sys_dict_data" ADD CONSTRAINT "sys_dict_data_pkey" PRIMARY KEY ("dict_code");

-- ----------------------------
-- Uniques structure for table sys_dict_type
-- ----------------------------
ALTER TABLE "public"."sys_dict_type" ADD CONSTRAINT "sys_dict_type_dict_type_key" UNIQUE ("dict_type");

-- ----------------------------
-- Primary Key structure for table sys_dict_type
-- ----------------------------
ALTER TABLE "public"."sys_dict_type" ADD CONSTRAINT "sys_dict_type_pkey" PRIMARY KEY ("dict_id");

-- ----------------------------
-- Primary Key structure for table sys_job
-- ----------------------------
ALTER TABLE "public"."sys_job" ADD CONSTRAINT "sys_job_pkey" PRIMARY KEY ("job_id", "job_name", "job_group");

-- ----------------------------
-- Primary Key structure for table sys_job_log
-- ----------------------------
ALTER TABLE "public"."sys_job_log" ADD CONSTRAINT "sys_job_log_pkey" PRIMARY KEY ("job_log_id");

-- ----------------------------
-- Indexes structure for table sys_logininfor
-- ----------------------------
CREATE INDEX "idx_sys_logininfor_lt" ON "public"."sys_logininfor" USING btree (
  "login_time" "pg_catalog"."timestamp_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_logininfor_s" ON "public"."sys_logininfor" USING btree (
  "status" COLLATE "pg_catalog"."default" "pg_catalog"."bpchar_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table sys_logininfor
-- ----------------------------
ALTER TABLE "public"."sys_logininfor" ADD CONSTRAINT "sys_logininfor_pkey" PRIMARY KEY ("info_id");

-- ----------------------------
-- Primary Key structure for table sys_menu
-- ----------------------------
ALTER TABLE "public"."sys_menu" ADD CONSTRAINT "sys_menu_pkey" PRIMARY KEY ("menu_id");

-- ----------------------------
-- Primary Key structure for table sys_notice
-- ----------------------------
ALTER TABLE "public"."sys_notice" ADD CONSTRAINT "sys_notice_pkey" PRIMARY KEY ("notice_id");

-- ----------------------------
-- Indexes structure for table sys_oper_log
-- ----------------------------
CREATE INDEX "idx_sys_oper_log_bt" ON "public"."sys_oper_log" USING btree (
  "business_type" "pg_catalog"."int2_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_oper_log_ot" ON "public"."sys_oper_log" USING btree (
  "oper_time" "pg_catalog"."timestamp_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_oper_log_s" ON "public"."sys_oper_log" USING btree (
  "status" "pg_catalog"."int2_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table sys_oper_log
-- ----------------------------
ALTER TABLE "public"."sys_oper_log" ADD CONSTRAINT "sys_oper_log_pkey" PRIMARY KEY ("oper_id");

-- ----------------------------
-- Primary Key structure for table sys_post
-- ----------------------------
ALTER TABLE "public"."sys_post" ADD CONSTRAINT "sys_post_pkey" PRIMARY KEY ("post_id");

-- ----------------------------
-- Primary Key structure for table sys_role
-- ----------------------------
ALTER TABLE "public"."sys_role" ADD CONSTRAINT "sys_role_pkey" PRIMARY KEY ("role_id");

-- ----------------------------
-- Primary Key structure for table sys_role_dept
-- ----------------------------
ALTER TABLE "public"."sys_role_dept" ADD CONSTRAINT "sys_role_dept_pkey" PRIMARY KEY ("role_id", "dept_id");

-- ----------------------------
-- Primary Key structure for table sys_role_menu
-- ----------------------------
ALTER TABLE "public"."sys_role_menu" ADD CONSTRAINT "sys_role_menu_pkey" PRIMARY KEY ("role_id", "menu_id");

-- ----------------------------
-- Indexes structure for table sys_user
-- ----------------------------
CREATE INDEX "idx_sys_user_dept" ON "public"."sys_user" USING btree (
  "dept_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table sys_user
-- ----------------------------
ALTER TABLE "public"."sys_user" ADD CONSTRAINT "sys_user_pkey" PRIMARY KEY ("user_id");

-- ----------------------------
-- Primary Key structure for table sys_user_post
-- ----------------------------
ALTER TABLE "public"."sys_user_post" ADD CONSTRAINT "sys_user_post_pkey" PRIMARY KEY ("user_id", "post_id");

-- ----------------------------
-- Primary Key structure for table sys_user_role
-- ----------------------------
ALTER TABLE "public"."sys_user_role" ADD CONSTRAINT "sys_user_role_pkey" PRIMARY KEY ("user_id", "role_id");
