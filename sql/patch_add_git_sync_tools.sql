-- ============================================================
-- 补丁：新增 Git 仓库同步工具 (fetch_git_file + import_box_games)
-- 执行时机：v3.x 之后任意版本
-- ============================================================

-- 防止重复插入
DELETE FROM `gb_atomic_tool` WHERE `tool_code` IN ('fetch_git_file', 'import_box_games');

-- ----------------------------
-- 1. 获取Git仓库文件工具
-- ----------------------------
INSERT INTO `gb_atomic_tool`
  (`tool_code`, `tool_name`, `tool_type`, `description`, `config`,
   `inputs`, `outputs`,
   `enabled`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (
  'fetch_git_file',
  '获取Git仓库文件',
  'system',
  '从 GitHub / GitLab / Gitee 等 Git 仓库中读取指定文件的原始内容。支持私有仓库（通过 Access Token 认证）',
  '{}',
  '[
    {"name":"repoUrl",    "type":"text",   "label":"仓库地址",       "required":true,  "description":"Git 仓库 URL，如 https://github.com/owner/repo"},
    {"name":"accessToken","type":"text",   "label":"访问令牌",       "required":false, "description":"私有仓库的 Personal Access Token 或 Deploy Token"},
    {"name":"filePath",   "type":"text",   "label":"文件路径",       "required":true,  "description":"文件在仓库中的相对路径，如 data/games.json"},
    {"name":"branch",     "type":"text",   "label":"分支名",         "required":false, "default":"main", "description":"目标分支，默认 main"}
  ]',
  '[
    {"name":"content",  "type":"textarea","label":"文件内容",   "description":"文件的原始文本内容（UTF-8）"},
    {"name":"fileName", "type":"text",    "label":"文件名",     "description":"文件名（含扩展名）"},
    {"name":"fileSize", "type":"number",  "label":"文件大小",   "description":"文件字节数"},
    {"name":"success",  "type":"boolean", "label":"是否成功",   "description":"读取操作是否成功"},
    {"name":"message",  "type":"text",    "label":"返回消息",   "description":"操作结果消息"}
  ]',
  1, 0,
  'admin', NOW(), '', NOW(),
  '系统工具 - 自动注册 - 从Git仓库获取文件内容'
);

-- ----------------------------
-- 2. 导入游戏到盒子（JSON）工具
-- ----------------------------
INSERT INTO `gb_atomic_tool`
  (`tool_code`, `tool_name`, `tool_type`, `description`, `config`,
   `inputs`, `outputs`,
   `enabled`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (
  'import_box_games',
  '导入游戏到盒子（JSON）',
  'system',
  '解析 JSON 数据，使用字段映射配置将游戏批量导入到指定盒子。支持新增和覆盖两种模式',
  '{}',
  '[
    {"name":"jsonData",       "type":"textarea","label":"JSON数据",       "required":true,  "description":"包含游戏列表的 JSON 字符串（可以是数组，也可以是含数组字段的对象）","rows":10},
    {"name":"boxId",          "type":"number",  "label":"目标盒子ID",     "required":true,  "description":"要导入游戏的游戏盒子 ID"},
    {"name":"siteId",         "type":"number",  "label":"站点ID",         "required":true,  "description":"所属站点 ID，用于字段映射查找"},
    {"name":"dataPath",       "type":"text",    "label":"数据路径",       "required":false, "description":"JSON 中游戏数组的路径，支持点分隔多级，如 result.list。为空则自动查找第一个数组"},
    {"name":"updateExisting", "type":"boolean", "label":"更新已存在游戏", "required":false, "default":false, "description":"true=同名游戏覆盖更新；false=跳过已存在的游戏"}
  ]',
  '[
    {"name":"totalCount",   "type":"number",  "label":"总数",         "description":"JSON 中游戏总条数"},
    {"name":"successCount", "type":"number",  "label":"成功数",       "description":"成功写入的游戏数（新增+更新）"},
    {"name":"newCount",     "type":"number",  "label":"新增数",       "description":"新增的游戏数"},
    {"name":"updatedCount", "type":"number",  "label":"更新数",       "description":"覆盖更新的游戏数"},
    {"name":"skippedCount", "type":"number",  "label":"跳过数",       "description":"因已存在而跳过的游戏数"},
    {"name":"failureCount", "type":"number",  "label":"失败数",       "description":"处理失败的游戏数"},
    {"name":"message",      "type":"text",    "label":"返回消息",     "description":"操作结果汇总消息"},
    {"name":"success",      "type":"boolean", "label":"是否成功",     "description":"整体操作是否成功"}
  ]',
  1, 0,
  'admin', NOW(), '', NOW(),
  '系统工具 - 自动注册 - 解析JSON并批量导入游戏到盒子'
);
