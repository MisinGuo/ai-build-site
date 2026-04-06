-- =====================================================================
-- AI 建站运营平台 - PostgreSQL 初始化脚本
-- 版本: v1.0
-- 日期: 2026-04-06
-- 数据库: PostgreSQL 16 + pgvector
-- 包含：① RuoYi 系统表  ② 通用建站表  ③ AI 平台专属表
-- =====================================================================

-- 启用扩展
-- pgvector 需要使用 pgvector/pgvector:pg16 镜像，如容器未包含则跳过
-- CREATE EXTENSION IF NOT EXISTS "pgvector";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

-- =====================================================================
-- 第一部分：RuoYi 系统表（从 MySQL 转换为 PostgreSQL 语法）
-- =====================================================================

-- 1. 部门表
DROP TABLE IF EXISTS sys_dept CASCADE;
CREATE TABLE sys_dept (
  dept_id           BIGSERIAL       PRIMARY KEY,
  parent_id         BIGINT          DEFAULT 0,
  ancestors         VARCHAR(50)     DEFAULT '',
  dept_name         VARCHAR(30)     DEFAULT '',
  order_num         INT             DEFAULT 0,
  leader            VARCHAR(20),
  phone             VARCHAR(11),
  email             VARCHAR(50),
  status            CHAR(1)         DEFAULT '0',
  del_flag          CHAR(1)         DEFAULT '0',
  create_by         VARCHAR(64)     DEFAULT '',
  create_time       TIMESTAMP,
  update_by         VARCHAR(64)     DEFAULT '',
  update_time       TIMESTAMP
);
COMMENT ON TABLE sys_dept IS '部门表';

INSERT INTO sys_dept VALUES(100, 0,   '0',          'AI建站平台',  0, 'admin', '15888888888', 'admin@aisite.io', '0', '0', 'admin', NOW(), '', NULL);
INSERT INTO sys_dept VALUES(101, 100, '0,100',      '技术部',      1, 'admin', '15888888888', 'admin@aisite.io', '0', '0', 'admin', NOW(), '', NULL);
INSERT INTO sys_dept VALUES(102, 100, '0,100',      '运营部',      2, 'admin', '15888888888', 'admin@aisite.io', '0', '0', 'admin', NOW(), '', NULL);

SELECT setval('sys_dept_dept_id_seq', 200);

-- 2. 用户信息表
DROP TABLE IF EXISTS sys_user CASCADE;
CREATE TABLE sys_user (
  user_id           BIGSERIAL       PRIMARY KEY,
  dept_id           BIGINT,
  user_name         VARCHAR(30)     NOT NULL,
  nick_name         VARCHAR(30)     NOT NULL,
  user_type         VARCHAR(2)      DEFAULT '00',
  email             VARCHAR(50)     DEFAULT '',
  phonenumber       VARCHAR(11)     DEFAULT '',
  sex               CHAR(1)         DEFAULT '0',
  avatar            VARCHAR(100)    DEFAULT '',
  password          VARCHAR(100)    DEFAULT '',
  status            CHAR(1)         DEFAULT '0',
  del_flag          CHAR(1)         DEFAULT '0',
  login_ip          VARCHAR(128)    DEFAULT '',
  login_date        TIMESTAMP,
  pwd_update_date   TIMESTAMP,
  create_by         VARCHAR(64)     DEFAULT '',
  create_time       TIMESTAMP,
  update_by         VARCHAR(64)     DEFAULT '',
  update_time       TIMESTAMP,
  remark            VARCHAR(500)
);
COMMENT ON TABLE sys_user IS '用户信息表';
CREATE INDEX idx_sys_user_dept ON sys_user(dept_id);

-- 初始 admin 用户（密码: admin123）
INSERT INTO sys_user VALUES(1, 101, 'admin', '管理员', '00', 'admin@aisite.io', '15888888888', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', NOW(), NOW(), 'admin', NOW(), '', NULL, '超级管理员');

SELECT setval('sys_user_user_id_seq', 100);

-- 3. 岗位信息表
DROP TABLE IF EXISTS sys_post CASCADE;
CREATE TABLE sys_post (
  post_id       BIGSERIAL       PRIMARY KEY,
  post_code     VARCHAR(64)     NOT NULL,
  post_name     VARCHAR(50)     NOT NULL,
  post_sort     INT             NOT NULL,
  status        CHAR(1)         NOT NULL,
  create_by     VARCHAR(64)     DEFAULT '',
  create_time   TIMESTAMP,
  update_by     VARCHAR(64)     DEFAULT '',
  update_time   TIMESTAMP,
  remark        VARCHAR(500)
);
COMMENT ON TABLE sys_post IS '岗位信息表';

INSERT INTO sys_post VALUES(1, 'ceo',  '董事长',   1, '0', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_post VALUES(2, 'se',   '技术总监',  2, '0', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_post VALUES(3, 'pm',   '产品经理',  3, '0', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_post VALUES(4, 'user', '普通员工',  4, '0', 'admin', NOW(), '', NULL, '');

-- 4. 角色信息表
DROP TABLE IF EXISTS sys_role CASCADE;
CREATE TABLE sys_role (
  role_id              BIGSERIAL       PRIMARY KEY,
  role_name            VARCHAR(30)     NOT NULL,
  role_key             VARCHAR(100)    NOT NULL,
  role_sort            INT             NOT NULL,
  data_scope           CHAR(1)         DEFAULT '1',
  menu_check_strictly  SMALLINT        DEFAULT 1,
  dept_check_strictly  SMALLINT        DEFAULT 1,
  status               CHAR(1)         NOT NULL,
  del_flag             CHAR(1)         DEFAULT '0',
  create_by            VARCHAR(64)     DEFAULT '',
  create_time          TIMESTAMP,
  update_by            VARCHAR(64)     DEFAULT '',
  update_time          TIMESTAMP,
  remark               VARCHAR(500)
);
COMMENT ON TABLE sys_role IS '角色信息表';

INSERT INTO sys_role VALUES(1, '超级管理员', 'admin',  1, '1', 1, 1, '0', '0', 'admin', NOW(), '', NULL, '超级管理员');
INSERT INTO sys_role VALUES(2, '普通角色',   'common', 2, '2', 1, 1, '0', '0', 'admin', NOW(), '', NULL, '普通角色');

SELECT setval('sys_role_role_id_seq', 100);

-- 5. 菜单权限表
DROP TABLE IF EXISTS sys_menu CASCADE;
CREATE TABLE sys_menu (
  menu_id           BIGSERIAL       PRIMARY KEY,
  menu_name         VARCHAR(50)     NOT NULL,
  parent_id         BIGINT          DEFAULT 0,
  order_num         INT             DEFAULT 0,
  path              VARCHAR(200)    DEFAULT '',
  component         VARCHAR(255),
  query             VARCHAR(255),
  route_name        VARCHAR(50)     DEFAULT '',
  is_frame          INT             DEFAULT 1,
  is_cache          INT             DEFAULT 0,
  menu_type         CHAR(1)         DEFAULT '',
  visible           CHAR(1)         DEFAULT '0',
  status            CHAR(1)         DEFAULT '0',
  perms             VARCHAR(100),
  icon              VARCHAR(100)    DEFAULT '#',
  create_by         VARCHAR(64)     DEFAULT '',
  create_time       TIMESTAMP,
  update_by         VARCHAR(64)     DEFAULT '',
  update_time       TIMESTAMP,
  remark            VARCHAR(500)    DEFAULT ''
);
COMMENT ON TABLE sys_menu IS '菜单权限表';

-- 系统内置菜单
INSERT INTO sys_menu VALUES(1,  '系统管理',  0, 1, 'system',  NULL, '', '', 1, 0, 'M', '0', '0', '',                       'system',   'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(2,  '系统监控',  0, 2, 'monitor', NULL, '', '', 1, 0, 'M', '0', '0', '',                       'monitor',  'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(3,  '系统工具',  0, 3, 'tool',    NULL, '', '', 1, 0, 'M', '0', '0', '',                       'tool',     'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(100, '用户管理', 1, 1, 'user',    'system/user/index',    '', '', 1, 0, 'C', '0', '0', 'system:user:list',    'user',       'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(101, '角色管理', 1, 2, 'role',    'system/role/index',    '', '', 1, 0, 'C', '0', '0', 'system:role:list',    'peoples',    'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(102, '菜单管理', 1, 3, 'menu',    'system/menu/index',    '', '', 1, 0, 'C', '0', '0', 'system:menu:list',    'tree-table', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(103, '部门管理', 1, 4, 'dept',    'system/dept/index',    '', '', 1, 0, 'C', '0', '0', 'system:dept:list',    'tree',       'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(104, '岗位管理', 1, 5, 'post',    'system/post/index',    '', '', 1, 0, 'C', '0', '0', 'system:post:list',    'post',       'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(105, '字典管理', 1, 6, 'dict',    'system/dict/index',    '', '', 1, 0, 'C', '0', '0', 'system:dict:list',    'dict',       'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(106, '参数设置', 1, 7, 'config',  'system/config/index',  '', '', 1, 0, 'C', '0', '0', 'system:config:list',  'edit',       'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(107, '通知公告', 1, 8, 'notice',  'system/notice/index',  '', '', 1, 0, 'C', '0', '0', 'system:notice:list',  'message',    'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(108, '日志管理', 1, 9, 'log',     '',                     '', '', 1, 0, 'M', '0', '0', '',                    'log',        'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(109, '在线用户', 2, 1, 'online',  'monitor/online/index', '', '', 1, 0, 'C', '0', '0', 'monitor:online:list', 'online',     'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(110, '定时任务', 2, 2, 'job',     'monitor/job/index',    '', '', 1, 0, 'C', '0', '0', 'monitor:job:list',    'job',        'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(111, '数据监控', 2, 3, 'druid',   'monitor/druid/index',  '', '', 1, 0, 'C', '0', '0', 'monitor:druid:list',  'druid',      'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(112, '服务监控', 2, 4, 'server',  'monitor/server/index', '', '', 1, 0, 'C', '0', '0', 'monitor:server:list', 'server',     'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(113, '缓存监控', 2, 5, 'cache',   'monitor/cache/index',  '', '', 1, 0, 'C', '0', '0', 'monitor:cache:list',  'redis',      'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(115, '表单构建', 3, 1, 'build',   'tool/build/index',     '', '', 1, 0, 'C', '0', '0', 'tool:build:list',     'build',      'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(116, '代码生成', 3, 2, 'gen',     'tool/gen/index',       '', '', 1, 0, 'C', '0', '0', 'tool:gen:list',       'code',       'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(117, '系统接口', 3, 3, 'swagger', 'tool/swagger/index',   '', '', 1, 0, 'C', '0', '0', 'tool:swagger:list',   'swagger',    'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(500, '操作日志', 108, 1, 'operlog',    'monitor/operlog/index',    '', '', 1, 0, 'C', '0', '0', 'monitor:operlog:list',    'form',       'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(501, '登录日志', 108, 2, 'logininfor', 'monitor/logininfor/index', '', '', 1, 0, 'C', '0', '0', 'monitor:logininfor:list', 'logininfor', 'admin', NOW(), '', NULL, '');
-- 用户管理按钮
INSERT INTO sys_menu VALUES(1000, '用户查询', 100, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:query',     '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(1001, '用户新增', 100, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:add',       '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(1002, '用户修改', 100, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:edit',      '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(1003, '用户删除', 100, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:remove',    '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(1004, '用户导出', 100, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:export',    '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(1005, '用户导入', 100, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:import',    '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(1006, '重置密码', 100, 7, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:resetPwd',  '#', 'admin', NOW(), '', NULL, '');
-- 角色管理按钮
INSERT INTO sys_menu VALUES(1007, '角色查询', 101, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:query',   '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(1008, '角色新增', 101, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:add',     '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(1009, '角色修改', 101, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:edit',    '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(1010, '角色删除', 101, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:remove',  '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(1011, '角色导出', 101, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:export',  '#', 'admin', NOW(), '', NULL, '');
-- 代码生成按钮
INSERT INTO sys_menu VALUES(1055, '生成查询', 116, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:query',   '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(1056, '生成修改', 116, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:edit',    '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(1057, '生成删除', 116, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:remove',  '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(1058, '导入代码', 116, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:import',  '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(1059, '预览代码', 116, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:preview', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(1060, '生成代码', 116, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:code',    '#', 'admin', NOW(), '', NULL, '');

SELECT setval('sys_menu_menu_id_seq', 2000);

-- 6. 用户和角色关联表
DROP TABLE IF EXISTS sys_user_role CASCADE;
CREATE TABLE sys_user_role (
  user_id   BIGINT NOT NULL,
  role_id   BIGINT NOT NULL,
  PRIMARY KEY(user_id, role_id)
);
COMMENT ON TABLE sys_user_role IS '用户和角色关联表';

INSERT INTO sys_user_role VALUES (1, 1);

-- 7. 角色和菜单关联表
DROP TABLE IF EXISTS sys_role_menu CASCADE;
CREATE TABLE sys_role_menu (
  role_id   BIGINT NOT NULL,
  menu_id   BIGINT NOT NULL,
  PRIMARY KEY(role_id, menu_id)
);
COMMENT ON TABLE sys_role_menu IS '角色和菜单关联表';

-- 8. 角色和部门关联表
DROP TABLE IF EXISTS sys_role_dept CASCADE;
CREATE TABLE sys_role_dept (
  role_id   BIGINT NOT NULL,
  dept_id   BIGINT NOT NULL,
  PRIMARY KEY(role_id, dept_id)
);
COMMENT ON TABLE sys_role_dept IS '角色和部门关联表';

-- 9. 用户与岗位关联表
DROP TABLE IF EXISTS sys_user_post CASCADE;
CREATE TABLE sys_user_post (
  user_id   BIGINT NOT NULL,
  post_id   BIGINT NOT NULL,
  PRIMARY KEY (user_id, post_id)
);
COMMENT ON TABLE sys_user_post IS '用户与岗位关联表';

INSERT INTO sys_user_post VALUES (1, 1);

-- 10. 操作日志记录
DROP TABLE IF EXISTS sys_oper_log CASCADE;
CREATE TABLE sys_oper_log (
  oper_id           BIGSERIAL       PRIMARY KEY,
  title             VARCHAR(50)     DEFAULT '',
  business_type     SMALLINT        DEFAULT 0,
  method            VARCHAR(200)    DEFAULT '',
  request_method    VARCHAR(10)     DEFAULT '',
  operator_type     SMALLINT        DEFAULT 0,
  oper_name         VARCHAR(50)     DEFAULT '',
  dept_name         VARCHAR(50)     DEFAULT '',
  oper_url          VARCHAR(255)    DEFAULT '',
  oper_ip           VARCHAR(128)    DEFAULT '',
  oper_location     VARCHAR(255)    DEFAULT '',
  oper_param        VARCHAR(2000)   DEFAULT '',
  json_result       VARCHAR(2000)   DEFAULT '',
  status            SMALLINT        DEFAULT 0,
  error_msg         VARCHAR(2000)   DEFAULT '',
  oper_time         TIMESTAMP,
  cost_time         BIGINT          DEFAULT 0
);
COMMENT ON TABLE sys_oper_log IS '操作日志记录';
CREATE INDEX idx_sys_oper_log_bt ON sys_oper_log(business_type);
CREATE INDEX idx_sys_oper_log_s  ON sys_oper_log(status);
CREATE INDEX idx_sys_oper_log_ot ON sys_oper_log(oper_time);

-- 11. 字典类型表
DROP TABLE IF EXISTS sys_dict_type CASCADE;
CREATE TABLE sys_dict_type (
  dict_id          BIGSERIAL       PRIMARY KEY,
  dict_name        VARCHAR(100)    DEFAULT '',
  dict_type        VARCHAR(100)    DEFAULT '',
  status           CHAR(1)         DEFAULT '0',
  create_by        VARCHAR(64)     DEFAULT '',
  create_time      TIMESTAMP,
  update_by        VARCHAR(64)     DEFAULT '',
  update_time      TIMESTAMP,
  remark           VARCHAR(500),
  UNIQUE (dict_type)
);
COMMENT ON TABLE sys_dict_type IS '字典类型表';

INSERT INTO sys_dict_type VALUES(1,  '用户性别', 'sys_user_sex',        '0', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_dict_type VALUES(2,  '菜单状态', 'sys_show_hide',       '0', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_dict_type VALUES(3,  '系统开关', 'sys_normal_disable',  '0', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_dict_type VALUES(4,  '任务状态', 'sys_job_status',      '0', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_dict_type VALUES(5,  '任务分组', 'sys_job_group',       '0', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_dict_type VALUES(6,  '系统是否', 'sys_yes_no',          '0', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_dict_type VALUES(7,  '通知类型', 'sys_notice_type',     '0', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_dict_type VALUES(8,  '通知状态', 'sys_notice_status',   '0', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_dict_type VALUES(9,  '操作类型', 'sys_oper_type',       '0', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_dict_type VALUES(10, '系统状态', 'sys_common_status',   '0', 'admin', NOW(), '', NULL, '');

SELECT setval('sys_dict_type_dict_id_seq', 100);

-- 12. 字典数据表
DROP TABLE IF EXISTS sys_dict_data CASCADE;
CREATE TABLE sys_dict_data (
  dict_code        BIGSERIAL       PRIMARY KEY,
  dict_sort        INT             DEFAULT 0,
  dict_label       VARCHAR(100)    DEFAULT '',
  dict_value       VARCHAR(100)    DEFAULT '',
  dict_type        VARCHAR(100)    DEFAULT '',
  css_class        VARCHAR(100),
  list_class       VARCHAR(100),
  is_default       CHAR(1)         DEFAULT 'N',
  status           CHAR(1)         DEFAULT '0',
  create_by        VARCHAR(64)     DEFAULT '',
  create_time      TIMESTAMP,
  update_by        VARCHAR(64)     DEFAULT '',
  update_time      TIMESTAMP,
  remark           VARCHAR(500)
);
COMMENT ON TABLE sys_dict_data IS '字典数据表';

INSERT INTO sys_dict_data VALUES(1,  1,  '男',       '0', 'sys_user_sex',       '', '',        'Y', '0', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(2,  2,  '女',       '1', 'sys_user_sex',       '', '',        'N', '0', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(3,  3,  '未知',     '2', 'sys_user_sex',       '', '',        'N', '0', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(4,  1,  '显示',     '0', 'sys_show_hide',      '', 'primary', 'Y', '0', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(5,  2,  '隐藏',     '1', 'sys_show_hide',      '', 'danger',  'N', '0', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(6,  1,  '正常',     '0', 'sys_normal_disable', '', 'primary', 'Y', '0', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(7,  2,  '停用',     '1', 'sys_normal_disable', '', 'danger',  'N', '0', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(8,  1,  '正常',     '0', 'sys_job_status',     '', 'primary', 'Y', '0', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(9,  2,  '暂停',     '1', 'sys_job_status',     '', 'danger',  'N', '0', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(10, 1,  '默认',     'DEFAULT', 'sys_job_group','', '',        'Y', '0', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(11, 2,  '系统',     'SYSTEM',  'sys_job_group','', '',        'N', '0', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(12, 1,  '是',       'Y', 'sys_yes_no',         '', 'primary', 'Y', '0', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(13, 2,  '否',       'N', 'sys_yes_no',         '', 'danger',  'N', '0', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(18, 99, '其他',     '0', 'sys_oper_type',      '', 'info',    'N', '0', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(19, 1,  '新增',     '1', 'sys_oper_type',      '', 'info',    'N', '0', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(20, 2,  '修改',     '2', 'sys_oper_type',      '', 'info',    'N', '0', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(21, 3,  '删除',     '3', 'sys_oper_type',      '', 'danger',  'N', '0', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(28, 1,  '成功',     '0', 'sys_common_status',  '', 'primary', 'N', '0', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(29, 2,  '失败',     '1', 'sys_common_status',  '', 'danger',  'N', '0', 'admin', NOW(), '', NULL, '');

SELECT setval('sys_dict_data_dict_code_seq', 100);

-- 13. 参数配置表
DROP TABLE IF EXISTS sys_config CASCADE;
CREATE TABLE sys_config (
  config_id         SERIAL          PRIMARY KEY,
  config_name       VARCHAR(100)    DEFAULT '',
  config_key        VARCHAR(100)    DEFAULT '',
  config_value      VARCHAR(500)    DEFAULT '',
  config_type       CHAR(1)         DEFAULT 'N',
  create_by         VARCHAR(64)     DEFAULT '',
  create_time       TIMESTAMP,
  update_by         VARCHAR(64)     DEFAULT '',
  update_time       TIMESTAMP,
  remark            VARCHAR(500)
);
COMMENT ON TABLE sys_config IS '参数配置表';

INSERT INTO sys_config VALUES(1, '主框架页-默认皮肤样式名称', 'sys.index.skinName',            'skin-blue',  'Y', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_config VALUES(2, '用户管理-账号初始密码',     'sys.user.initPassword',         '123456',     'Y', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_config VALUES(3, '主框架页-侧边栏主题',       'sys.index.sideTheme',           'theme-dark', 'Y', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_config VALUES(4, '账号自助-验证码开关',       'sys.account.captchaEnabled',    'true',       'Y', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_config VALUES(5, '账号自助-是否开启用户注册', 'sys.account.registerUser',      'false',      'Y', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_config VALUES(6, '用户登录-黑名单列表',       'sys.login.blackIPList',         '',           'Y', 'admin', NOW(), '', NULL, '');

SELECT setval('sys_config_config_id_seq', 100);

-- 14. 系统访问记录
DROP TABLE IF EXISTS sys_logininfor CASCADE;
CREATE TABLE sys_logininfor (
  info_id        BIGSERIAL       PRIMARY KEY,
  user_name      VARCHAR(50)     DEFAULT '',
  ipaddr         VARCHAR(128)    DEFAULT '',
  login_location VARCHAR(255)    DEFAULT '',
  browser        VARCHAR(50)     DEFAULT '',
  os             VARCHAR(50)     DEFAULT '',
  status         CHAR(1)         DEFAULT '0',
  msg            VARCHAR(255)    DEFAULT '',
  login_time     TIMESTAMP
);
COMMENT ON TABLE sys_logininfor IS '系统访问记录';
CREATE INDEX idx_sys_logininfor_s  ON sys_logininfor(status);
CREATE INDEX idx_sys_logininfor_lt ON sys_logininfor(login_time);

SELECT setval('sys_logininfor_info_id_seq', 100);

-- 15. 定时任务调度表
DROP TABLE IF EXISTS sys_job CASCADE;
CREATE TABLE sys_job (
  job_id              BIGSERIAL       NOT NULL,
  job_name            VARCHAR(64)     DEFAULT '',
  job_group           VARCHAR(64)     DEFAULT 'DEFAULT',
  invoke_target       VARCHAR(500)    NOT NULL,
  cron_expression     VARCHAR(255)    DEFAULT '',
  misfire_policy      VARCHAR(20)     DEFAULT '3',
  concurrent          CHAR(1)         DEFAULT '1',
  status              CHAR(1)         DEFAULT '0',
  create_by           VARCHAR(64)     DEFAULT '',
  create_time         TIMESTAMP,
  update_by           VARCHAR(64)     DEFAULT '',
  update_time         TIMESTAMP,
  remark              VARCHAR(500)    DEFAULT '',
  PRIMARY KEY (job_id, job_name, job_group)
);
COMMENT ON TABLE sys_job IS '定时任务调度表';

INSERT INTO sys_job VALUES(1, '系统默认（无参）', 'DEFAULT', 'ryTask.ryNoParams',        '0/10 * * * * ?', '3', '1', '1', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_job VALUES(2, '系统默认（有参）', 'DEFAULT', 'ryTask.ryParams(''ry'')',  '0/15 * * * * ?', '3', '1', '1', 'admin', NOW(), '', NULL, '');

SELECT setval('sys_job_job_id_seq', 100);

-- 16. 定时任务调度日志表
DROP TABLE IF EXISTS sys_job_log CASCADE;
CREATE TABLE sys_job_log (
  job_log_id          BIGSERIAL       PRIMARY KEY,
  job_name            VARCHAR(64)     NOT NULL,
  job_group           VARCHAR(64)     NOT NULL,
  invoke_target       VARCHAR(500)    NOT NULL,
  job_message         VARCHAR(500),
  status              CHAR(1)         DEFAULT '0',
  exception_info      VARCHAR(2000)   DEFAULT '',
  create_time         TIMESTAMP
);
COMMENT ON TABLE sys_job_log IS '定时任务调度日志表';

-- 17. 通知公告表
DROP TABLE IF EXISTS sys_notice CASCADE;
CREATE TABLE sys_notice (
  notice_id         SERIAL          PRIMARY KEY,
  notice_title      VARCHAR(50)     NOT NULL,
  notice_type       CHAR(1)         NOT NULL,
  notice_content    TEXT,
  status            CHAR(1)         DEFAULT '0',
  create_by         VARCHAR(64)     DEFAULT '',
  create_time       TIMESTAMP,
  update_by         VARCHAR(64)     DEFAULT '',
  update_time       TIMESTAMP,
  remark            VARCHAR(255)
);
COMMENT ON TABLE sys_notice IS '通知公告表';

-- 18. 代码生成业务表
DROP TABLE IF EXISTS gen_table CASCADE;
CREATE TABLE gen_table (
  table_id          BIGSERIAL       PRIMARY KEY,
  table_name        VARCHAR(200)    DEFAULT '',
  table_comment     VARCHAR(500)    DEFAULT '',
  sub_table_name    VARCHAR(64),
  sub_table_fk_name VARCHAR(64),
  class_name        VARCHAR(100)    DEFAULT '',
  tpl_category      VARCHAR(200)    DEFAULT 'crud',
  tpl_web_type      VARCHAR(30)     DEFAULT '',
  package_name      VARCHAR(100),
  module_name       VARCHAR(30),
  business_name     VARCHAR(30),
  function_name     VARCHAR(50),
  function_author   VARCHAR(50),
  gen_type          CHAR(1)         DEFAULT '0',
  gen_path          VARCHAR(200)    DEFAULT '/',
  options           VARCHAR(1000),
  create_by         VARCHAR(64)     DEFAULT '',
  create_time       TIMESTAMP,
  update_by         VARCHAR(64)     DEFAULT '',
  update_time       TIMESTAMP,
  remark            VARCHAR(500)
);
COMMENT ON TABLE gen_table IS '代码生成业务表';

-- 19. 代码生成业务表字段
DROP TABLE IF EXISTS gen_table_column CASCADE;
CREATE TABLE gen_table_column (
  column_id         BIGSERIAL       PRIMARY KEY,
  table_id          BIGINT,
  column_name       VARCHAR(200),
  column_comment    VARCHAR(500),
  column_type       VARCHAR(100),
  java_type         VARCHAR(500),
  java_field        VARCHAR(200),
  is_pk             CHAR(1),
  is_increment      CHAR(1),
  is_required       CHAR(1),
  is_insert         CHAR(1),
  is_edit           CHAR(1),
  is_list           CHAR(1),
  is_query          CHAR(1),
  query_type        VARCHAR(200)    DEFAULT 'EQ',
  html_type         VARCHAR(200),
  dict_type         VARCHAR(200)    DEFAULT '',
  sort              INT,
  create_by         VARCHAR(64)     DEFAULT '',
  create_time       TIMESTAMP,
  update_by         VARCHAR(64)     DEFAULT '',
  update_time       TIMESTAMP
);
COMMENT ON TABLE gen_table_column IS '代码生成业务表字段';


-- =====================================================================
-- 第二部分：通用建站表（参考 gamebox_init.sql，通用化重新设计）
-- =====================================================================

-- 1. 站点表（对应 gb_sites，去除 game-box 专属字段，通用化）
DROP TABLE IF EXISTS ai_sites CASCADE;
CREATE TABLE ai_sites (
  id                BIGSERIAL       PRIMARY KEY,
  name              VARCHAR(100)    NOT NULL,
  domain            VARCHAR(255)    NOT NULL,
  industry          VARCHAR(50)     DEFAULT 'general',   -- 行业：game/drama/ecommerce/real_estate/general 等
  template_id       BIGINT,                               -- 关联模板站点 ID
  logo_url          VARCHAR(500),
  favicon_url       VARCHAR(500),
  description       TEXT,
  seo_title         VARCHAR(255),
  seo_keywords      VARCHAR(500),
  seo_description   VARCHAR(500),
  site_config       JSONB           DEFAULT '{}',         -- 完整站点配置 JSON（颜色/布局/区块等）
  storage_config_id BIGINT,
  default_locale    VARCHAR(10)     DEFAULT 'zh-CN',
  supported_locales JSONB           DEFAULT '["zh-CN"]',
  i18n_mode         VARCHAR(20)     DEFAULT 'subdirectory',
  deploy_status     VARCHAR(20)     DEFAULT 'draft',      -- draft/building/live/error
  cf_pages_project  VARCHAR(100),                         -- Cloudflare Pages 项目名称
  cf_deploy_url     VARCHAR(500),                         -- 部署后的访问地址
  matrix_group_id   BIGINT,                               -- 所属矩阵站组（NULL 表示独立站）
  creator_id        BIGINT,
  dept_id           BIGINT,
  status            CHAR(1)         DEFAULT '1',
  del_flag          CHAR(1)         DEFAULT '0',
  create_by         VARCHAR(64)     DEFAULT '',
  create_time       TIMESTAMPTZ     DEFAULT NOW(),
  update_by         VARCHAR(64)     DEFAULT '',
  update_time       TIMESTAMPTZ     DEFAULT NOW(),
  remark            VARCHAR(500),
  UNIQUE (domain)
);
COMMENT ON TABLE ai_sites IS '站点表';
CREATE INDEX idx_ai_sites_industry       ON ai_sites(industry);
CREATE INDEX idx_ai_sites_deploy_status  ON ai_sites(deploy_status);
CREATE INDEX idx_ai_sites_matrix_group   ON ai_sites(matrix_group_id);
CREATE INDEX idx_ai_sites_creator        ON ai_sites(creator_id);
CREATE INDEX idx_ai_sites_dept           ON ai_sites(dept_id);
CREATE INDEX idx_ai_sites_config         ON ai_sites USING GIN (site_config);

-- 2. 内容类型表（全行业通用，扩展 gamebox 的 category_type 概念）
DROP TABLE IF EXISTS ai_content_types CASCADE;
CREATE TABLE ai_content_types (
  id                BIGSERIAL       PRIMARY KEY,
  site_id           BIGINT,                               -- NULL 表示全局类型（可被所有站点使用）
  code              VARCHAR(50)     NOT NULL,             -- 唯一标识：game/article/product/property/drama 等
  name              VARCHAR(100)    NOT NULL,
  description       VARCHAR(500),
  schema_json       JSONB           DEFAULT '{}',         -- 动态字段定义（字段名/类型/必填/显示标签）
  list_fields       JSONB           DEFAULT '[]',         -- 列表页展示的字段
  detail_fields     JSONB           DEFAULT '[]',         -- 详情页展示的字段
  filter_fields     JSONB           DEFAULT '[]',         -- 可用于筛选的字段
  seo_template      JSONB           DEFAULT '{}',         -- SEO 模板（title/desc 模板字符串）
  sort_order        INT             DEFAULT 0,
  is_system         CHAR(1)         DEFAULT '0',          -- 是否系统内置（不可删除）
  status            CHAR(1)         DEFAULT '1',
  del_flag          CHAR(1)         DEFAULT '0',
  create_by         VARCHAR(64)     DEFAULT '',
  create_time       TIMESTAMPTZ     DEFAULT NOW(),
  update_by         VARCHAR(64)     DEFAULT '',
  update_time       TIMESTAMPTZ     DEFAULT NOW(),
  UNIQUE (code, site_id)
);
COMMENT ON TABLE ai_content_types IS '内容类型表（全行业通用, JSONB 动态 schema）';
CREATE INDEX idx_ai_content_types_site   ON ai_content_types(site_id);
CREATE INDEX idx_ai_content_types_schema ON ai_content_types USING GIN (schema_json);

-- 初始化系统内置内容类型
INSERT INTO ai_content_types(site_id, code, name, description, is_system, status)
VALUES
  (NULL, 'article',  '通用文章',  '博客/新闻/SEO文章等通用文章类型', '1', '1'),
  (NULL, 'game',     '游戏',      '游戏推广站点专用',                 '1', '1'),
  (NULL, 'drama',    '短剧',      '短剧推广站点专用',                 '1', '1'),
  (NULL, 'product',  '商品',      '电商站点商品类型',                 '1', '1'),
  (NULL, 'property', '房产',      '房产中介类站点',                   '1', '1');

-- 3. 内容项表（全行业通用内容存储，对应 gamebox 的 gb_games/gb_dramas 等）
DROP TABLE IF EXISTS ai_content_items CASCADE;
CREATE TABLE ai_content_items (
  id                BIGSERIAL       PRIMARY KEY,
  site_id           BIGINT          NOT NULL,
  type_code         VARCHAR(50)     NOT NULL,             -- 关联 ai_content_types.code
  title             VARCHAR(500)    NOT NULL,
  slug              VARCHAR(500)    NOT NULL,
  cover_image       VARCHAR(500),
  summary           TEXT,
  content           TEXT,                                 -- 富文本正文（可选，文章类型使用）
  fields_json       JSONB           DEFAULT '{}',         -- 动态字段值（由 schema_json 定义）
  seo_title         VARCHAR(255),
  seo_description   VARCHAR(500),
  seo_keywords      VARCHAR(500),
  canonical_url     VARCHAR(500),
  publish_status    VARCHAR(20)     DEFAULT 'draft',      -- draft/published/scheduled/archived
  publish_at        TIMESTAMPTZ,
  is_featured       CHAR(1)         DEFAULT '0',
  sort_order        INT             DEFAULT 0,
  view_count        BIGINT          DEFAULT 0,
  embedding         TEXT,                                  -- 语义向量占位（pgvector 安装后可 ALTER COLUMN 改为 vector(1536)）
  creator_id        BIGINT,
  dept_id           BIGINT,
  status            CHAR(1)         DEFAULT '1',
  del_flag          CHAR(1)         DEFAULT '0',
  create_by         VARCHAR(64)     DEFAULT '',
  create_time       TIMESTAMPTZ     DEFAULT NOW(),
  update_by         VARCHAR(64)     DEFAULT '',
  update_time       TIMESTAMPTZ     DEFAULT NOW(),
  UNIQUE (site_id, type_code, slug)
);
COMMENT ON TABLE ai_content_items IS '内容项表（全行业通用，JSONB 动态字段）';
CREATE INDEX idx_ai_content_items_site        ON ai_content_items(site_id);
CREATE INDEX idx_ai_content_items_type        ON ai_content_items(type_code);
CREATE INDEX idx_ai_content_items_status      ON ai_content_items(publish_status);
CREATE INDEX idx_ai_content_items_fields      ON ai_content_items USING GIN (fields_json);
-- ivfflat 索引需要 pgvector，安装后执行：
-- ALTER TABLE ai_content_items ALTER COLUMN embedding TYPE vector(1536) USING embedding::vector;
-- CREATE INDEX idx_ai_content_items_embedding ON ai_content_items USING ivfflat (embedding vector_cosine_ops) WITH (lists = 100);

-- 4. 分类表（通用，对应 gb_categories）
DROP TABLE IF EXISTS ai_categories CASCADE;
CREATE TABLE ai_categories (
  id                BIGSERIAL       PRIMARY KEY,
  site_id           BIGINT,                               -- NULL 表示全局分类
  type_code         VARCHAR(50),                          -- 关联特定内容类型（NULL 表示通用）
  parent_id         BIGINT          DEFAULT 0,
  name              VARCHAR(100)    NOT NULL,
  slug              VARCHAR(100)    NOT NULL,
  icon              VARCHAR(100),
  description       VARCHAR(500),
  sort_order        INT             DEFAULT 0,
  item_count        INT             DEFAULT 0,
  status            CHAR(1)         DEFAULT '1',
  del_flag          CHAR(1)         DEFAULT '0',
  create_by         VARCHAR(64)     DEFAULT '',
  create_time       TIMESTAMPTZ     DEFAULT NOW(),
  update_by         VARCHAR(64)     DEFAULT '',
  update_time       TIMESTAMPTZ     DEFAULT NOW(),
  UNIQUE (site_id, slug)
);
COMMENT ON TABLE ai_categories IS '通用分类表';
CREATE INDEX idx_ai_categories_site    ON ai_categories(site_id);
CREATE INDEX idx_ai_categories_type    ON ai_categories(type_code);
CREATE INDEX idx_ai_categories_parent  ON ai_categories(parent_id);

-- 5. 内容项-分类关联表
DROP TABLE IF EXISTS ai_content_item_categories CASCADE;
CREATE TABLE ai_content_item_categories (
  id              BIGSERIAL   PRIMARY KEY,
  item_id         BIGINT      NOT NULL,
  category_id     BIGINT      NOT NULL,
  is_primary      CHAR(1)     DEFAULT '0',
  UNIQUE (item_id, category_id)
);
COMMENT ON TABLE ai_content_item_categories IS '内容项-分类关联表';
CREATE INDEX idx_ai_item_cat_item     ON ai_content_item_categories(item_id);
CREATE INDEX idx_ai_item_cat_category ON ai_content_item_categories(category_id);

-- 6. 多语言翻译表（对应 gb 的翻译体系）
DROP TABLE IF EXISTS ai_translations CASCADE;
CREATE TABLE ai_translations (
  id              BIGSERIAL       PRIMARY KEY,
  entity_type     VARCHAR(50)     NOT NULL,   -- 实体类型：content_item/site/category
  entity_id       BIGINT          NOT NULL,
  locale          VARCHAR(10)     NOT NULL,   -- zh-CN/en-US/ja-JP 等
  field_name      VARCHAR(100)    NOT NULL,   -- 字段名：title/content/summary/seo_title 等
  field_value     TEXT,
  translation_status VARCHAR(20)  DEFAULT 'pending', -- pending/translated/reviewed
  create_time     TIMESTAMPTZ     DEFAULT NOW(),
  update_time     TIMESTAMPTZ     DEFAULT NOW(),
  UNIQUE (entity_type, entity_id, locale, field_name)
);
COMMENT ON TABLE ai_translations IS '多语言翻译表';
CREATE INDEX idx_ai_trans_entity ON ai_translations(entity_type, entity_id);
CREATE INDEX idx_ai_trans_locale ON ai_translations(locale);

-- 7. 对象存储配置表（精简自 gb_storage_configs）
DROP TABLE IF EXISTS ai_storage_configs CASCADE;
CREATE TABLE ai_storage_configs (
  id                BIGSERIAL       PRIMARY KEY,
  site_id           BIGINT,
  name              VARCHAR(100)    NOT NULL,
  code              VARCHAR(50)     NOT NULL,
  storage_type      VARCHAR(20)     NOT NULL,   -- github/r2/oss/cos/s3/minio
  is_default        CHAR(1)         DEFAULT '0',
  config_json       JSONB           DEFAULT '{}',    -- 统一存储各平台配置（加密字段用 AES 加密后存入）
  cdn_url           VARCHAR(500),
  status            CHAR(1)         DEFAULT '1',
  del_flag          CHAR(1)         DEFAULT '0',
  create_by         VARCHAR(64)     DEFAULT '',
  create_time       TIMESTAMPTZ     DEFAULT NOW(),
  update_by         VARCHAR(64)     DEFAULT '',
  update_time       TIMESTAMPTZ     DEFAULT NOW(),
  UNIQUE (code)
);
COMMENT ON TABLE ai_storage_configs IS '对象存储配置表';


-- =====================================================================
-- 第三部分：AI 建站运营专属表
-- =====================================================================

-- 1. AI 平台配置表（对应 gb_ai_platform）
DROP TABLE IF EXISTS ai_platforms CASCADE;
CREATE TABLE ai_platforms (
  id                BIGSERIAL       PRIMARY KEY,
  name              VARCHAR(100)    NOT NULL,
  platform_type     VARCHAR(50)     NOT NULL,   -- openai/anthropic/qwen/wenxin/deepseek 等
  api_key           VARCHAR(500),               -- 加密存储
  base_url          VARCHAR(500),
  model_name        VARCHAR(100),
  max_tokens        INT             DEFAULT 4096,
  temperature       NUMERIC(3,2)    DEFAULT 0.7,
  extra_config      JSONB           DEFAULT '{}',
  is_default        CHAR(1)         DEFAULT '0',
  status            CHAR(1)         DEFAULT '1',
  del_flag          CHAR(1)         DEFAULT '0',
  create_by         VARCHAR(64)     DEFAULT '',
  create_time       TIMESTAMPTZ     DEFAULT NOW(),
  update_by         VARCHAR(64)     DEFAULT '',
  update_time       TIMESTAMPTZ     DEFAULT NOW()
);
COMMENT ON TABLE ai_platforms IS 'AI 平台配置表';

-- 2. 矩阵站组（批量关键词建站管理）
DROP TABLE IF EXISTS ai_matrix_groups CASCADE;
CREATE TABLE ai_matrix_groups (
  id                BIGSERIAL       PRIMARY KEY,
  name              VARCHAR(200)    NOT NULL,
  description       TEXT,
  template_site_id  BIGINT,                     -- 模板站点 ID
  industry          VARCHAR(50)     DEFAULT 'general',
  keyword_list      JSONB           DEFAULT '[]',  -- ["上海健身房", "北京健身房", ...]
  domain_pattern    VARCHAR(200),               -- 域名生成规则，如 {keyword}.example.com
  config_overrides  JSONB           DEFAULT '{}',  -- 批量覆盖 site_config 的字段
  total_count       INT             DEFAULT 0,
  built_count       INT             DEFAULT 0,
  live_count        INT             DEFAULT 0,
  status            VARCHAR(20)     DEFAULT 'draft',  -- draft/building/live/paused/error
  creator_id        BIGINT,
  dept_id           BIGINT,
  del_flag          CHAR(1)         DEFAULT '0',
  create_by         VARCHAR(64)     DEFAULT '',
  create_time       TIMESTAMPTZ     DEFAULT NOW(),
  update_by         VARCHAR(64)     DEFAULT '',
  update_time       TIMESTAMPTZ     DEFAULT NOW()
);
COMMENT ON TABLE ai_matrix_groups IS '矩阵站组（批量关键词建站）';
CREATE INDEX idx_ai_matrix_groups_status ON ai_matrix_groups(status);
CREATE INDEX idx_ai_matrix_groups_kws    ON ai_matrix_groups USING GIN (keyword_list);

-- 3. 运营任务（AI 自动运营调度）
DROP TABLE IF EXISTS ai_operation_tasks CASCADE;
CREATE TABLE ai_operation_tasks (
  id                BIGSERIAL       PRIMARY KEY,
  site_id           BIGINT,                     -- NULL 表示全局任务
  matrix_group_id   BIGINT,                     -- 针对整个矩阵站组
  task_type         VARCHAR(50)     NOT NULL,   -- content_refresh/seo_optimize/interlink_update/a_b_test/dead_link_fix
  task_name         VARCHAR(200),
  trigger_type      VARCHAR(20)     NOT NULL,   -- cron/event/manual
  cron_expr         VARCHAR(100),
  params            JSONB           DEFAULT '{}',
  priority          SMALLINT        DEFAULT 5,  -- 1-10，越大越优先
  status            VARCHAR(20)     DEFAULT 'pending',  -- pending/running/success/failed/paused
  last_run_at       TIMESTAMPTZ,
  next_run_at       TIMESTAMPTZ,
  last_result       TEXT,
  retry_count       SMALLINT        DEFAULT 0,
  max_retries       SMALLINT        DEFAULT 3,
  creator_id        BIGINT,
  dept_id           BIGINT,
  del_flag          CHAR(1)         DEFAULT '0',
  create_by         VARCHAR(64)     DEFAULT '',
  create_time       TIMESTAMPTZ     DEFAULT NOW(),
  update_by         VARCHAR(64)     DEFAULT '',
  update_time       TIMESTAMPTZ     DEFAULT NOW()
);
COMMENT ON TABLE ai_operation_tasks IS '运营任务表（AI 自动运营调度）';
CREATE INDEX idx_ai_op_tasks_site       ON ai_operation_tasks(site_id);
CREATE INDEX idx_ai_op_tasks_type       ON ai_operation_tasks(task_type);
CREATE INDEX idx_ai_op_tasks_status     ON ai_operation_tasks(status);
CREATE INDEX idx_ai_op_tasks_next_run   ON ai_operation_tasks(next_run_at);

-- 4. 操作日志（可回溯/回滚）
DROP TABLE IF EXISTS ai_operation_logs CASCADE;
CREATE TABLE ai_operation_logs (
  id                BIGSERIAL       PRIMARY KEY,
  task_id           BIGINT,
  site_id           BIGINT          NOT NULL,
  action            VARCHAR(100),
  before_state      JSONB,                      -- 操作前快照
  after_state       JSONB,                      -- 操作后快照
  ai_reasoning      TEXT,                       -- AI 决策说明（可审计）
  status            VARCHAR(20),                -- success/failed/rolled_back
  error_message     TEXT,
  create_time       TIMESTAMPTZ     DEFAULT NOW()
);
COMMENT ON TABLE ai_operation_logs IS '操作日志（可回溯/回滚）';
CREATE INDEX idx_ai_op_logs_task        ON ai_operation_logs(task_id);
CREATE INDEX idx_ai_op_logs_site        ON ai_operation_logs(site_id);
CREATE INDEX idx_ai_op_logs_status      ON ai_operation_logs(status);
CREATE INDEX idx_ai_op_logs_created     ON ai_operation_logs(create_time);

-- 5. SEO 指标快照（每日）
DROP TABLE IF EXISTS ai_seo_metrics_snapshots CASCADE;
CREATE TABLE ai_seo_metrics_snapshots (
  id                BIGSERIAL       PRIMARY KEY,
  site_id           BIGINT          NOT NULL,
  snapshot_date     DATE            NOT NULL,
  indexed_pages     INT             DEFAULT 0,
  avg_position      NUMERIC(5,2),
  clicks            INT             DEFAULT 0,
  impressions       INT             DEFAULT 0,
  ctr               NUMERIC(5,4),               -- 点击率
  metrics_json      JSONB           DEFAULT '{}',  -- 完整原始数据
  UNIQUE (site_id, snapshot_date)
);
COMMENT ON TABLE ai_seo_metrics_snapshots IS 'SEO 指标快照（每日）';
CREATE INDEX idx_ai_seo_site_date ON ai_seo_metrics_snapshots(site_id, snapshot_date DESC);


-- =====================================================================
-- 第四部分：工作流系统表（参考 gamebox 工作流体系，提炼通用版）
-- =====================================================================

-- 1. 原子工具注册表
DROP TABLE IF EXISTS ai_atomic_tools CASCADE;
CREATE TABLE ai_atomic_tools (
  id                BIGSERIAL       PRIMARY KEY,
  tool_code         VARCHAR(100)    NOT NULL UNIQUE,  -- 工具唯一标识，对应 @SystemTool 注解的 name
  tool_name         VARCHAR(200)    NOT NULL,
  category          VARCHAR(50)     DEFAULT 'general',  -- content/seo/deploy/data/ai/system
  description       TEXT,
  input_schema      JSONB           DEFAULT '{}',     -- 输入参数 JSON Schema
  output_schema     JSONB           DEFAULT '{}',     -- 输出参数 JSON Schema
  is_builtin        CHAR(1)         DEFAULT '1',      -- 是否内置
  is_enabled        CHAR(1)         DEFAULT '1',
  sort_order        INT             DEFAULT 0,
  create_time       TIMESTAMPTZ     DEFAULT NOW(),
  update_time       TIMESTAMPTZ     DEFAULT NOW()
);
COMMENT ON TABLE ai_atomic_tools IS '原子工具注册表';
CREATE INDEX idx_ai_tools_category ON ai_atomic_tools(category);

-- 初始化内置工具
INSERT INTO ai_atomic_tools(tool_code, tool_name, category, description, is_builtin)
VALUES
  ('site_create',          '创建站点',         'deploy',   '创建新站点并初始化配置',           '1'),
  ('site_config_generate', '生成站点配置',      'ai',       '使用 AI 生成站点 site_config JSON', '1'),
  ('content_generate',     '生成内容',          'ai',       '使用 AI 批量生成内容项',            '1'),
  ('content_refresh',      '刷新内容',          'content',  '重新生成或更新现有内容',            '1'),
  ('seo_optimize',         'SEO 优化',          'seo',      'AI 生成或优化 title/description',   '1'),
  ('cf_deploy',            'Cloudflare 部署',   'deploy',   '触发 Cloudflare Pages 部署',        '1'),
  ('interlink_build',      '构建内链网络',      'seo',      '基于语义相似度自动建立站内链接',    '1'),
  ('dead_link_scan',       '死链扫描',          'seo',      '扫描站点死链并生成修复报告',        '1');

-- 2. 工作流定义表
DROP TABLE IF EXISTS ai_workflows CASCADE;
CREATE TABLE ai_workflows (
  id                BIGSERIAL       PRIMARY KEY,
  name              VARCHAR(200)    NOT NULL,
  code              VARCHAR(100)    NOT NULL UNIQUE,
  description       TEXT,
  category          VARCHAR(50)     DEFAULT 'general',  -- build/content/seo/operation
  steps_json        JSONB           DEFAULT '[]',       -- 步骤拓扑图（含并行分支/子流程）
  params_schema     JSONB           DEFAULT '{}',       -- 工作流输入参数 Schema
  is_builtin        CHAR(1)         DEFAULT '0',
  is_enabled        CHAR(1)         DEFAULT '1',
  version           INT             DEFAULT 1,
  creator_id        BIGINT,
  del_flag          CHAR(1)         DEFAULT '0',
  create_by         VARCHAR(64)     DEFAULT '',
  create_time       TIMESTAMPTZ     DEFAULT NOW(),
  update_by         VARCHAR(64)     DEFAULT '',
  update_time       TIMESTAMPTZ     DEFAULT NOW()
);
COMMENT ON TABLE ai_workflows IS '工作流定义表';
CREATE INDEX idx_ai_workflows_category ON ai_workflows(category);

-- 初始化内置工作流
INSERT INTO ai_workflows(name, code, category, description, is_builtin)
VALUES
  ('AI 对话建站',   'ai_site_build',    'build',     '通过 AI 对话交互式创建站点',         '1'),
  ('矩阵站批量建站', 'matrix_batch_build', 'build',  '批量关键词站点并发创建',              '1'),
  ('内容批量生成',   'content_batch_gen', 'content', '根据内容类型批量 AI 生成内容项',      '1'),
  ('SEO 定期优化',   'seo_periodic',     'seo',      '定期扫描并优化站点 SEO 指标',         '1'),
  ('内链网络维护',   'interlink_maint',  'operation','基于向量相似度自动更新矩阵站内链',    '1');

-- 3. 工作流执行记录
DROP TABLE IF EXISTS ai_workflow_executions CASCADE;
CREATE TABLE ai_workflow_executions (
  id                BIGSERIAL       PRIMARY KEY,
  workflow_id       BIGINT          NOT NULL,
  workflow_code     VARCHAR(100),
  site_id           BIGINT,
  trigger_type      VARCHAR(20),                -- manual/cron/event/sub_workflow
  input_params      JSONB           DEFAULT '{}',
  output_result     JSONB           DEFAULT '{}',
  status            VARCHAR(20)     DEFAULT 'pending',  -- pending/running/success/failed/cancelled
  error_message     TEXT,
  progress          SMALLINT        DEFAULT 0,           -- 0-100
  started_at        TIMESTAMPTZ,
  finished_at       TIMESTAMPTZ,
  creator_id        BIGINT,
  create_time       TIMESTAMPTZ     DEFAULT NOW()
);
COMMENT ON TABLE ai_workflow_executions IS '工作流执行记录';
CREATE INDEX idx_ai_wf_exec_workflow ON ai_workflow_executions(workflow_id);
CREATE INDEX idx_ai_wf_exec_site     ON ai_workflow_executions(site_id);
CREATE INDEX idx_ai_wf_exec_status   ON ai_workflow_executions(status);
CREATE INDEX idx_ai_wf_exec_created  ON ai_workflow_executions(create_time DESC);

-- 4. 工作流步骤执行记录
DROP TABLE IF EXISTS ai_workflow_step_executions CASCADE;
CREATE TABLE ai_workflow_step_executions (
  id                BIGSERIAL       PRIMARY KEY,
  execution_id      BIGINT          NOT NULL,
  step_index        INT             NOT NULL,
  step_name         VARCHAR(200),
  tool_code         VARCHAR(100),
  input_data        JSONB           DEFAULT '{}',
  output_data       JSONB           DEFAULT '{}',
  stream_content    TEXT,           -- 流式 AI 输出的完整文本（供审计）
  status            VARCHAR(20)     DEFAULT 'pending',
  error_message     TEXT,
  started_at        TIMESTAMPTZ,
  finished_at       TIMESTAMPTZ,
  cost_ms           INT             DEFAULT 0,         -- 耗时毫秒
  token_used        INT             DEFAULT 0          -- LLM token 消耗
);
COMMENT ON TABLE ai_workflow_step_executions IS '工作流步骤执行记录';
CREATE INDEX idx_ai_wf_step_execution ON ai_workflow_step_executions(execution_id);
CREATE INDEX idx_ai_wf_step_status    ON ai_workflow_step_executions(status);

-- 5. Prompt 模板表（供工具/工作流调用）
DROP TABLE IF EXISTS ai_prompt_templates CASCADE;
CREATE TABLE ai_prompt_templates (
  id                BIGSERIAL       PRIMARY KEY,
  name              VARCHAR(200)    NOT NULL,
  code              VARCHAR(100)    NOT NULL UNIQUE,
  scene             VARCHAR(50),                    -- site_build/content_gen/seo_opt/translation 等
  system_prompt     TEXT,
  user_prompt       TEXT,
  variables         JSONB           DEFAULT '[]',   -- 模板变量定义
  platform_type     VARCHAR(50),                    -- 适用 AI 平台类型（NULL 表示通用）
  is_builtin        CHAR(1)         DEFAULT '0',
  status            CHAR(1)         DEFAULT '1',
  del_flag          CHAR(1)         DEFAULT '0',
  create_by         VARCHAR(64)     DEFAULT '',
  create_time       TIMESTAMPTZ     DEFAULT NOW(),
  update_by         VARCHAR(64)     DEFAULT '',
  update_time       TIMESTAMPTZ     DEFAULT NOW()
);
COMMENT ON TABLE ai_prompt_templates IS 'Prompt 模板表';
CREATE INDEX idx_ai_prompt_scene ON ai_prompt_templates(scene);


-- =====================================================================
-- 第五部分：AI 建站菜单（注入 RuoYi sys_menu，菜单 ID 从 2000 开始）
-- =====================================================================

INSERT INTO sys_menu VALUES(2000, 'AI 建站平台', 0, 5, 'aisite', NULL, '', '', 1, 0, 'M', '0', '0', '', 'robot', 'admin', NOW(), '', NULL, 'AI 建站运营平台');

-- 站点管理
INSERT INTO sys_menu VALUES(2001, '站点管理',    2000, 1, 'sites',    'aisite/sites/index',    '', '', 1, 0, 'C', '0', '0', 'aisite:site:list',        'list',       'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(2002, 'AI 建站助手',  2000, 2, 'builder',  'aisite/builder/index',  '', '', 1, 0, 'C', '0', '0', 'aisite:builder:use',      'wand-magic', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(2003, '矩阵站管理',   2000, 3, 'matrix',   'aisite/matrix/index',   '', '', 1, 0, 'C', '0', '0', 'aisite:matrix:list',      'grid',       'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(2004, '内容管理',     2000, 4, 'contents', 'aisite/contents/index', '', '', 1, 0, 'C', '0', '0', 'aisite:content:list',     'documentation', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(2005, 'AI 运营中心',  2000, 5, 'operations','aisite/operations/index','', '',1, 0, 'C', '0', '0', 'aisite:operation:list',   'calendar',   'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(2006, 'SEO 看板',     2000, 6, 'seo',      'aisite/seo/index',      '', '', 1, 0, 'C', '0', '0', 'aisite:seo:view',         'chart',      'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(2007, '工作流',       2000, 7, 'workflows','aisite/workflows/index', '', '', 1, 0, 'C', '0', '0', 'aisite:workflow:list',    'flow',       'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(2008, 'AI 平台配置',  2000, 8, 'ai-platforms','aisite/platforms/index','','',1, 0, 'C', '0', '0', 'aisite:platform:list',   'setting',    'admin', NOW(), '', NULL, '');
-- 站点管理按钮
INSERT INTO sys_menu VALUES(2100, '站点查询', 2001, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'aisite:site:query',  '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(2101, '站点新增', 2001, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'aisite:site:add',    '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(2102, '站点修改', 2001, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'aisite:site:edit',   '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(2103, '站点删除', 2001, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'aisite:site:remove', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES(2104, '站点部署', 2001, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'aisite:site:deploy', '#', 'admin', NOW(), '', NULL, '');

SELECT setval('sys_menu_menu_id_seq', 3000);
