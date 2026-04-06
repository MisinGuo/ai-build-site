# AI 建站与运营平台技术方案

> 定位：面向 AI Agent 和人工操作的矩阵建站 + 自动运营系统
> 版本：v1.0 | 日期：2026-04-06

---

## 一、系统定位

### 1.1 要解决什么问题

```
当前痛点（源自 game-box 实践）：
  - 每建一个新站需要程序员介入
  - AI 生成的内容需要人工粘贴到后台
  - 100 个 SEO 矩阵站 = 100 次重复劳动
  - 站点运营（更新内容、调整 SEO、监控数据）完全手动

目标：
  AI Agent 或运营人员"描述一个站点需求"
  → 系统自动建站、填充内容、配置 SEO
  → 站点上线后自动运营：定期更新内容、监控排名、优化策略
```

### 1.2 两条核心能力线

```
能力线 A：AI 建站
  - 对话式建站（描述行业/目标/风格 → 自动生成站点配置）
  - 矩阵建站（批量生成 N 个关键词站点）
  - 内容批量生成（AI 填充文章/产品/房源等）
  - 一键部署（Cloudflare Pages 自动绑定域名）

能力线 B：AI 运营
  - 定时内容更新（关键词变化自动触发内容刷新）
  - SEO 监控（收录量、排名变化、死链检测）
  - A/B 测试（landing page 文案/布局自动测试）
  - 跨站内链优化（矩阵站之间自动建立内链网络）
```

### 1.3 与 game-box 的关系

**本系统是独立的新产品，game-box 是它的第一个迁移目标。**

```
短期（0-6个月）：
  新系统独立运行，game-box 保持不动

中期（6-12个月）：
  game-box 的新站点开始用本系统建站
  现有游戏/短剧数据可选择性迁入本系统

长期：
  本系统成为统一平台，game-box 作为其中一个"行业配置"运行
```

---

## 二、从 game-box 得到的关键经验

在做技术选型前，先明确 game-box 暴露的**真实**问题：

| 问题 | game-box 现状 | 新系统要求 |
|------|-------------|-----------|
| **动态字段查询** | MySQL JSON 无 GIN 索引，动态字段筛选慢 | 需要 JSONB + GIN 索引 |
| **AI 流式输出** | `callAI` 是同步 RestTemplate，前端无法实时看到 AI 打字 | 需要 SSE 流式推送 |
| **矩阵站批量任务** | Quartz 任务需要手写 Job 类，批量并发控制复杂 | 需要更灵活的任务队列 |
| **向量语义去重** | 无向量能力，矩阵站内容重复风险大 | 需要向量存储 |

**注意：用户权限管理、RBAC、数据权限（DataScope）、字典、菜单管理这些 RuoYi 已经提供——这是巨大的先发优势，不该轻易抛弃。**

---

## 三、整体架构

```
┌─────────────────────────────────────────────────────────────────┐
│                         管理平面 (Admin)                          │
│                                                                   │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────┐  │
│  │  AI 建站助手  │  │  矩阵站管理   │  │  AI 运营控制台        │  │
│  │  (对话建站)   │  │  (批量操作)  │  │  (任务/监控/策略)    │  │
│  └──────────────┘  └──────────────┘  └──────────────────────┘  │
│                                                                   │
│  Vue3 + Element Plus（延续 RuoYi-Vue3 技术栈）                   │
└──────────────────────────┬──────────────────────────────────────┘
                           │ REST API / SSE
┌──────────────────────────▼──────────────────────────────────────┐
│                   服务平面：Spring Boot (RuoYi)                   │
│                                                                   │
│  ┌──────────────────────┐    ┌──────────────────────────────┐   │
│  │  业务模块 (Spring MVC)│    │  AI 引擎模块 (Spring WebFlux)│   │
│  │  - 内容 CRUD          │    │  - LLM 流式调用             │   │
│  │  - 站点管理           │    │  - Workflow Runner          │   │
│  │  - 系统权限(RuoYi)   │    │  - Tool Executor            │   │
│  └──────────┬───────────┘    └────────────┬─────────────────┘   │
│             │                             │                       │
│  ┌──────────▼─────────────────────────────▼──────────────────┐  │
│  │        任务调度：Quartz（保留）+ 异步任务队列（Redis）       │  │
│  │        矩阵建站批量任务 | SEO监控 | 内容刷新 | 发布任务      │  │
│  └────────────────────────────────────────────────────────────┘  │
└──────────────────────────┬──────────────────────────────────────┘
                           │
┌──────────────────────────▼──────────────────────────────────────┐
│                         数据平面                                  │
│                                                                   │
│    PostgreSQL 16 (JSONB + pgvector)                               │
│    Redis（缓存 + 异步任务队列）                                    │
│    Elasticsearch（全文搜索 + 向量检索）                             │
│    Cloudflare R2（媒体文件存储）                                   │
└──────────────────────────┬──────────────────────────────────────┘
                           │
┌──────────────────────────▼──────────────────────────────────────┐
│                         展示平面 (Display)                        │
│                                                                   │
│    Universal Renderer（Next.js on Cloudflare Pages）              │
│    - 多租户：Host 头识别站点 → 拉取 site_config                   │
│    - 矩阵站：单次部署，N 个域名绑定同一个 Pages 项目              │
│    - ISR：内容更新后自动 revalidate                               │
└─────────────────────────────────────────────────────────────────┘
```

---

## 四、技术选型

### 4.1 后端：Spring Boot + RuoYi（继续沿用）

**结论：后端保持 Java + RuoYi，不重写。**

#### RuoYi 的不可替代价值

RuoYi 框架开箱即提供的能力，若改用 NestJS 从头来过，每一项都需要数天至数周的工作量：

| RuoYi 已有能力 | 改写成本估算 |
|--------------|------------|
| 用户/角色/权限管理（RBAC） | 2-3 周 |
| `@DataScope` 数据权限（自动按部门/个人过滤）| 1 周 |
| 菜单管理、操作日志、登录日志 | 1 周 |
| 字典管理、参数配置 | 3 天 |
| 代码生成器（CRUD 自动生成）| 不可替代 |
| Quartz 定时任务管理界面 | 3 天 |
| 已有工作流引擎（GbWorkflow）| 3-4 周 |
| 已有工具执行器（ToolExecutor）| 2 周 |

**这些功能在新系统里全部需要，保留 RuoYi 等于节省了 2-3 个月的开发量。**

#### AI 流式输出：Spring WebFlux（新增模块）

当前 `callAI` 是同步阻塞的——新系统需要 AI 建站时前端能看到实时流式输出。解决方案：在同一个 Spring Boot 应用中引入 `spring-boot-starter-webflux`（Spring MVC 和 WebFlux 可以在同一个项目中共存）：

```java
// 新增：流式 AI 调用（对比现有 callAI 的同步版本）
@GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<String> streamAiResponse(@RequestParam String prompt) {
    WebClient client = WebClient.builder()
        .baseUrl(platform.getBaseUrl())
        .defaultHeader("Authorization", "Bearer " + platform.getApiKey())
        .build();

    return client.post()
        .uri("/v1/chat/completions")
        .bodyValue(buildStreamRequest(prompt))
        .retrieve()
        .bodyToFlux(String.class)   // 逐块读取 SSE
        .map(this::extractContent); // 解析 delta.content
}
```

前端 Vue3 接收：

```javascript
// Vue3：接收流式输出（EventSource）
const source = new EventSource('/ai/stream?prompt=...')
source.onmessage = (e) => { response.value += e.data }
```

这比 NestJS 的 Vercel AI SDK 代码多 20 行，但功能完全等价。

#### 工具调用（Function Calling / Structured Output）

当前工作流的 `AiGeneratorToolExecutor` 是 Fire-and-forget 模式（生成内容、保存文章）。AI 建站需要 LLM 输出**结构化 JSON**（site_config）。Java 的解法：

```java
// 在系统 prompt 中强制约束 JSON 输出格式
String systemPrompt = """
你是建站配置生成助手，只输出合法的 JSON，不加任何解释或 markdown 代码块。
输出格式必须符合以下 schema：
""" + SITE_CONFIG_SCHEMA_DESC;

// 调用后用 Jackson 验证 schema 是否合规
ObjectMapper mapper = new ObjectMapper();
JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
JsonSchema schema = factory.getSchema(SITE_CONFIG_JSON_SCHEMA);
Set<ValidationMessage> errors = schema.validate(mapper.readTree(aiOutput));
// 如有 errors，重试（最多 3 次）
```

---

### 4.2 前端（管理端）：Vue3 + Element Plus（继续沿用）

**结论：继续用 RuoYi-Vue3，不迁移到 React。**

RuoYi-Vue3 已经提供了所有的权限指令（`v-hasPermi`）、菜单渲染、动态路由、动态表单等基础设施。新增模块只需要在现有目录下新建 `.vue` 文件，代码生成器可以生成 80% 的页面骨架。

**AI 建站对话界面在 Vue3 + Element Plus 中完全可以实现**：

```vue
<!-- AI 建站助手对话组件（Vue3） -->
<script setup>
const answer = ref('')
async function sendMessage(prompt) {
  const source = new EventSource(`/ai/builder/stream?prompt=${encodeURIComponent(prompt)}`)
  source.onmessage = (e) => {
    if (e.data === '[DONE]') { source.close(); return }
    answer.value += JSON.parse(e.data).delta
  }
}
</script>
```

---

### 4.3 前端（展示端）：Next.js（Cloudflare Pages，延续）

与 game-box Next-web 方案完全一致，参见《全行业建站工具升级方案》中的 Universal Renderer 章节。**矩阵站场景下的新要求**：

- 单个 Pages 项目通过 `wrangler.toml` 绑定 500+ 个自定义域名
- Host 头 → siteId → 拉取 site_config → ISR 渲染
- SEO 矩阵站之间的内链由 AI 运营引擎自动写入 `site_config.globalBlocks`

---

### 4.4 数据库选型

#### 主数据库：PostgreSQL 16

**核心理由是 JSONB + GIN 索引**，在《全行业建站工具升级方案》中已详细论证。补充矩阵站场景下的新需求：

**pgvector 扩展（向量存储，本系统新增需求）**

```sql
-- 开启 vector 扩展
CREATE EXTENSION vector;

-- 内容项存储嵌入向量（用于语义去重和相似内容检测）
ALTER TABLE content_items ADD COLUMN embedding vector(1536);
CREATE INDEX idx_embedding ON content_items USING ivfflat (embedding vector_cosine_ops);

-- 用途1：矩阵站内容去重（生成前检查是否已有相似内容）
SELECT id, title, 1 - (embedding <=> $1) AS similarity
FROM content_items
WHERE site_id != $2   -- 只查其他站点
ORDER BY similarity DESC LIMIT 5;

-- 用途2：相关内容推荐（内链自动生成）
-- 用途3：AI 生成内容时，先语义搜索已有内容，避免重复生成
```

**分区表（矩阵站规模考量）**

```sql
-- content_items 按 site_id 范围分区（当站点数超过 200 后生效）
CREATE TABLE content_items (
  id          BIGINT GENERATED ALWAYS AS IDENTITY,
  site_id     BIGINT NOT NULL,
  type_code   VARCHAR(50) NOT NULL,
  -- ...其他字段同全行业方案
) PARTITION BY HASH (site_id);

-- 8 个分区，均匀分布
CREATE TABLE content_items_p0 PARTITION OF content_items FOR VALUES WITH (MODULUS 8, REMAINDER 0);
-- ... p1 ~ p7
```

#### 缓存 + 任务队列：Redis（一个服务两用）

game-box 已经使用 Redis 做缓存（Spring Cache），新系统扩展其用途：

```
Redis 数据库分离：
  DB 0：应用缓存（site_config、热点内容项）  TTL: 5分钟
  DB 1：Redisson 分布式任务队列（替代部分 Quartz 场景）
  DB 2：分布式锁（防止矩阵建站任务并发冲突）
  DB 3：LLM API 调用速率限制计数器
```

**任务调度分工**：
- **Quartz（保留）**：定时运营任务（每天刷内容、每周分析排名）—— 有可视化管理界面，运营人员可直接操作
- **Redis + Spring @Async**：矩阵建站批量任务 —— 需要并发控制和进度追踪，用 Redis List 实现简单队列

#### 全文搜索：Elasticsearch

**选 Elasticsearch（面向规模化产品）：**

| 能力 | 说明 |
|------|------|
| 亿级文档 | 单集群支持 TB 级索引，矩阵站扩展不设上限 |
| 中文分词 | ik-analyzer 插件，支持 ik_max_word / ik_smart |
| 向量搜索 | 内置 `dense_vector` + kNN，与 pgvector 互补 |
| 多租户隔离 | 按 `site_id` 分索引或用别名路由，天然支持矩阵站 |
| 多语言分析器 | 每种语言配置独立 analyzer，满足跨国站点需求 |
| 生态成熟 | Spring Data Elasticsearch / RestHighLevelClient 直接集成 |

Java 端集成方式：`spring-data-elasticsearch` starter，用 `ElasticsearchOperations` 接口，后续升级版本不影响业务代码。

#### 文件存储：Cloudflare R2

- 兼容 S3 API，AWS SDK 直接用
- 免出站流量费（相比 S3 的大头成本）
- 与 Cloudflare Pages 同生态，CDN 加速不需要额外配置

---

### 4.5 AI / LLM 集成层

game-box 的 `GbAiPlatformServiceImpl` 已经抽象出多平台支持（OpenAI / 通义千问 / 文心），新系统在此基础上新增两件事：

1. **流式版本**（`callAIStream`）：用 `WebClient` 替代 `RestTemplate`，返回 `Flux<String>`
2. **JSON Schema 校验**（`callAIStructured`）：在现有 `callAI` 返回后，用 `jackson-databind-jsonschema` 校验输出是否符合 site_config schema，不合规时自动重试（最多 3 次）

```java
// 新增 callAIStream 方法签名（扩展现有 IGbAiToolService）
Flux<String> callAIStream(Long platformId, String systemPrompt, String userPrompt);

// 新增 callAIStructured 方法签名
<T> T callAIStructured(Long platformId, String systemPrompt, String userPrompt,
                        Class<T> responseType, JsonSchema schema) throws Exception;
```

现有所有工具（`SaveArticleToolExecutor`、`ImportBoxGamesToolExecutor` 等）**不需要任何改动**，继续使用同步版本。

---

## 五、核心模块设计

### 5.1 AI 建站引擎

```
输入：用户自然语言描述 或 矩阵建站参数（关键词列表）

流程：
  Step 1  解析意图 → 确定行业/目标/风格
  Step 2  查/建内容类型（gb_content_types）
  Step 3  生成 site_config JSON（AI Tool Calling）
  Step 4  批量生成初始内容（工作流异步执行）
  Step 5  预览 → 确认 → 发布到 Cloudflare Pages

矩阵模式（批量建站）：
  输入：N 个关键词 + 模板站点 ID
  流程：克隆模板 site_config → 逐个替换关键词/地域变量 → 批量入队
  并发：Redis List 队列消费线程数 = min(N, LLM API 速率限制)
```

### 5.2 AI 运营引擎

```
触发方式：
  - 定时触发（Cron，每天/每周）
  - 事件触发（新关键词热度变化、外部 API 推送）
  - 手动触发（运营人员点击）

运营动作类型：
  content_refresh   更新某类型的内容（重写旧文章、新增热点内容）
  seo_optimize      调整 title/desc 提升点击率
  interlink_update  更新矩阵站内链网络
  a_b_test          生成 hero banner 的 B 版本
  dead_link_fix     发现并修复死链

状态追踪：每次运营动作写入 operation_logs 表，可回溯/回滚
```

### 5.3 工作流系统（延续 game-box 设计）

**核心实体**（PostgreSQL 表名，参考 game-box 命名）：

```
workflows             工作流定义（步骤拓扑图 JSON）
workflow_executions   每次执行记录
workflow_step_executions 每步的输入/输出/状态
atomic_tools          原子工具注册表（等效 gb_atomic_tool）
```

**对比 game-box 的改进**：
- 步骤支持**并行分支**（game-box 只支持串行）
- 每步 LLM 调用支持**流式输出实时写入** step_executions
- 工作流支持**子流程嵌套**（矩阵建站 = 批量触发子流程）

---

## 六、数据库表结构（核心表）

沿用《全行业建站工具升级方案》中设计的五张核心表，并新增：

```sql
-- 矩阵站组（管理一批关键词站点）
CREATE TABLE site_matrix_groups (
  id           BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  name         VARCHAR(200) NOT NULL,
  template_site_id BIGINT,           -- 模板站点
  keyword_list JSONB,                -- ["上海健身房","北京健身房",...]
  status       VARCHAR(20) DEFAULT 'draft', -- draft/building/live/paused
  config_overrides JSONB,            -- 批量覆盖 site_config 的字段
  create_time  TIMESTAMPTZ DEFAULT NOW()
);

-- 运营任务（计划/执行中/已完成）
CREATE TABLE operation_tasks (
  id           BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  site_id      BIGINT,               -- null 表示全局任务
  task_type    VARCHAR(50) NOT NULL, -- content_refresh/seo_optimize/...
  trigger_type VARCHAR(20) NOT NULL, -- cron/event/manual
  cron_expr    VARCHAR(100),         -- 定时触发时的 cron 表达式
  params       JSONB,                -- 任务参数
  status       VARCHAR(20) DEFAULT 'pending',
  last_run_at  TIMESTAMPTZ,
  next_run_at  TIMESTAMPTZ,
  create_time  TIMESTAMPTZ DEFAULT NOW()
);

-- 操作日志（可回溯/回滚）
CREATE TABLE operation_logs (
  id            BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  task_id       BIGINT,
  site_id       BIGINT NOT NULL,
  action        VARCHAR(100),        -- 执行的具体动作
  before_state  JSONB,               -- 操作前的状态快照
  after_state   JSONB,               -- 操作后的状态快照
  ai_reasoning  TEXT,                -- AI 的决策说明（可审计）
  status        VARCHAR(20),         -- success/failed/rolled_back
  create_time   TIMESTAMPTZ DEFAULT NOW()
);

-- SEO 指标快照（每日）
CREATE TABLE seo_metrics_snapshots (
  id           BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  site_id      BIGINT NOT NULL,
  snapshot_date DATE NOT NULL,
  indexed_pages INT,
  avg_position  NUMERIC(5,2),
  clicks       INT,
  impressions  INT,
  metrics_json JSONB,                -- 完整原始数据
  UNIQUE (site_id, snapshot_date)
);
```

---

## 七、API 设计原则

### 7.1 双接口模式：Human API + Agent API

```
Human API（REST，适合管理后台调用）：
  POST /api/sites                  创建站点
  GET  /api/sites/:id/builder      打开 AI 建站对话
  POST /api/matrix-groups          创建矩阵站组
  
Agent API（支持流式/工具调用，适合 AI Agent 调用）：
  POST /api/agent/stream           主入口，支持 tool_use 多轮对话
  GET  /api/agent/tools            列出当前 Agent 可用的工具
  POST /api/agent/execute-workflow 直接执行工作流
```

### 7.2 租户隔离与数据权限

RuoYi 的 `@DataScope` 切面 + MyBatis XML 动态 SQL 实现多租户数据隔离，无需额外开发：

```java
// Spring Controller：使用 RuoYi 标准权限注解
@PreAuthorize("@ss.hasPermi('site:list')")
@DataScope(deptAlias = "s", userAlias = "s")
@GetMapping("/list")
public TableDataInfo list(AiSite site) {
    startPage();
    List<AiSite> list = siteService.selectSiteList(site);
    return getDataTable(list);
}
```

所有核心表加 `creator_id` + `dept_id` 字段，`DataScopeAspect` 自动追加 WHERE 条件，超管可见全量，普通用户只能看到自己或本部门的站点。

---

## 八、部署架构

```
┌──────────────────────────────────────────────────────────┐
│                    Cloudflare（边缘）                       │
│  Pages（展示侧）                Workers（API 路由/缓存）   │
│  - Universal Renderer            - 速率限制                │
│  - 500+ 自定义域名绑定           - 站点配置边缘缓存         │
└───────────────────────────┬──────────────────────────────┘
                            │
┌───────────────────────────▼──────────────────────────────┐
│                    服务器（VPS 或 容器）                    │
│                                                           │
│  ┌──────────────────────────────────────────────────┐    │
│  │  Spring Boot :8080 (RuoYi)                        │   │
│  │    - Spring MVC：Rest API、Admin 后台              │   │
│  │    - Spring WebFlux：AI 流式 SSE 端点              │   │
│  │    - Quartz：定时运营任务                          │   │
│  └──────────────────────────────────────────────────┘    │
│                                                           │
│  ┌────────────────┐  ┌──────────────────────────────┐    │
│  │  Redis :6379   │  │  Elasticsearch :9200         │    │
│  └────────────────┘  └──────────────────────────────┘    │
│                                                           │
│  ┌────────────────────────────────────────────────────┐  │
│  │  PostgreSQL 16 + pgvector :5432                    │  │
│  └────────────────────────────────────────────────────┘  │
│                                                           │
│  Cloudflare R2（通过 Worker 代理访问）                    │
└───────────────────────────────────────────────────────────┘
```

**扩展路径**（站点数 > 500，内容量 > 500万）：
- Spring Boot 水平扩展（无状态节点，Redis 存 Session）
- PostgreSQL 加只读副本（分流展示侧查询）
- Quartz 集群模式（多节点竞争执行，数据库锁保证唯一执行）
- Elasticsearch 横向扩节点（增加 data node，无需改代码）

---

## 九、技术栈汇总

| 层 | 技术 | 说明 |
|----|------|------|
| **后端服务** | Spring Boot 2.x + RuoYi | 主 API 服务；复用认证/RBAC/代码生成/Quartz UI |
| **AI 流式** | Spring WebFlux (WebClient) | 与 Spring MVC 共存；SSE 流式推送 LLM 响应 |
| **管理前端** | Vue3 + Element Plus (RuoYi-Vue3) | 复用 RuoYi 现有 Admin 框架，零成本权限菜单 |
| **展示前端** | Next.js + Cloudflare Pages | Universal Renderer，多租户，ISR，500+ 自定义域 |
| **主数据库** | PostgreSQL 16 + pgvector | JSONB 动态字段，GIN 索引，向量语义去重 |
| **缓存** | Redis | Session 缓存 + 批量建站任务队列（Redis List） |
| **定时任务** | Quartz（RuoYi 内置） | 运营任务触发；自带 Admin 管理界面 |
| **批量任务** | Spring @Async + Redis List | 矩阵站并发构建队列，无需引入额外中间件 |
| **全文搜索** | Elasticsearch 8.x | ik-analyzer 中文分词，kNN 向量搜索，亿级规模，Spring Data ES 直接集成 |
| **文件存储** | Cloudflare R2 | S3 兼容，免出站流量，复用现有 StorageFileServiceImpl |
| **ORM** | MyBatis XML | 40+ 现有 Mapper 可直接复用，JSONB 原生 SQL 支持 |
| **构建工具** | Maven（多模块） | 与现有 game-box mvnw 一致 |
| **容器化** | Docker + Docker Compose | 本地开发和服务器部署一致 |

---

## 十、Maven 多模块项目结构

在现有 game-box Maven 多模块基础上，新增 `ruoyi-aisite` 主业务模块：

```
ai-site-platform/                  (Maven 多模块，复用 mvnw.cmd)
├── pom.xml                         父 POM，统一依赖版本
│
├── ruoyi-admin/                   启动入口，Spring Boot main 类
│   └── src/main/
│       ├── resources/
│       │   ├── application.yml     数据源改为 PostgreSQL
│       │   └── application-druid.yml
│       └── java/.../RuoYiApplication.java
│
├── ruoyi-framework/               Spring Security / 权限拦截（复用）
├── ruoyi-system/                  用户/角色/菜单/字典（复用）
├── ruoyi-quartz/                  Quartz 定时任务管理（复用）
├── ruoyi-common/                  工具类/注解/BaseEntity（复用）
│
└── ruoyi-aisite/                  ← 新增核心业务模块
    └── src/main/java/.../aisite/
        ├── domain/                 实体类
        │   ├── AiSite.java
        │   ├── AiContentType.java
        │   ├── AiContentItem.java
        │   ├── AiMatrixGroup.java
        │   └── AiOperationTask.java
        ├── mapper/                 MyBatis XML Mapper
        │   └── xml/
        ├── service/
        │   ├── IAiBuilderService.java      AI 建站对话（WebFlux）
        │   ├── IAiContentService.java      内容管理
        │   ├── IAiMatrixService.java       矩阵站批量操作
        │   └── IAiOperationService.java    AI 运营引擎
        ├── controller/
        │   ├── AiSiteController.java       REST API + 权限注解
        │   ├── AiBuilderController.java    流式 SSE 端点（WebFlux）
        │   ├── AiMatrixController.java
        │   └── AiOperationController.java
        └── workflow/               工作流工具（复用 @SystemTool 体系）
            ├── ContentRefreshTool.java
            ├── SeoOptimizeTool.java
            └── SiteBuildTool.java

RuoYi-Vue3/                        管理前端（复用现有，新增页面）
└── src/views/
    ├── aisite/                    ← 新增视图目录
    │   ├── sites/index.vue
    │   ├── matrix/index.vue
    │   ├── builder/index.vue      AI 建站对话界面
    │   └── operations/index.vue
    └── ... (现有页面不动)

Next-web/                          展示前端（Universal Renderer，改造现有）
```

---

## 十一、实施路径

### 阶段 0：基础设施（1周）

| 步骤 | 内容 |
|------|------|
| 0-1 | Fork game-box → ai-site-platform，新建 `ruoyi-aisite` Maven 模块 |
| 0-2 | `application.yml` 数据源改为 PostgreSQL，验证 MyBatis XML 原生 SQL 跑通 JSONB 读写 |
| 0-3 | Docker Compose 本地启动 PostgreSQL 16 + pgvector + Redis + Elasticsearch（single-node 模式，带 ik-analyzer 插件）|
| 0-4 | 引入 `spring-boot-starter-webflux`，验证 Spring MVC + WebFlux 共存，跑通 SSE demo 端点 |
| 0-5 | 添加 `GbAiToolServiceImpl.callAIStream()`（WebClient 版本），对接已有 `gb_ai_platform` 配置 |

### 阶段 1：内容基础（2周）

| 步骤 | 内容 |
|------|------|
| 1-1 | 执行 5 张核心通用表 DDL（`ai_sites`、`ai_content_types` 等），RuoYi 代码生成器生成 CRUD 骨架 |
| 1-2 | 扩展 `content_types` 的 `schema_json` 动态字段，MyBatis XML 实现 JSONB `@>` 条件查询 |
| 1-3 | 公开查询接口 `/api/public/content-items`（不需认证，展示侧调用） |
| 1-4 | Universal Renderer 基础版（从现有 Next-web 提取通用区块组件） |
| 1-5 | 站点配置 CRUD + `site_config` JSON 存储、验证、返回 |

### 阶段 2：AI 建站（2周）

| 步骤 | 内容 |
|------|------|
| 2-1 | `IAiBuilderService` 接口 + `callAIStream` 集成，测试 OpenAI/通义千问流式输出 |
| 2-2 | `AiBuilderController` SSE 端点，Vue3 `EventSource` 接收流式文本 |
| 2-3 | AI 建站工具定义（`@SystemTool` 注解，复用 `ToolExecutor` 体系）：`createSite`、`generateSiteConfig`、`generateContent` |
| 2-4 | Admin 前端新增 `builder/index.vue`：对话界面 + iframe 实时预览 |
| 2-5 | 封装 Cloudflare Pages 部署工具（调用 wrangler API，复用现有 `GbCodeManageServiceImpl` 逻辑） |

### 阶段 3：矩阵建站（2周）

| 步骤 | 内容 |
|------|------|
| 3-1 | 矩阵站组 CRUD（`ai_matrix_groups`） + 模板克隆逻辑 |
| 3-2 | Redis List 队列 + Spring `@Async` 线程池并发建站（参考 `GbRebuildTaskService` UUID 追踪模式） |
| 3-3 | 矩阵站批量操作 Admin 界面（进度追踪、失败重试、速率控制）|
| 3-4 | pgvector 内容相似度检查（生成前先查向量库，避免重复内容） |

### 阶段 4：AI 运营（3周）

| 步骤 | 内容 |
|------|------|
| 4-1 | `ai_operation_tasks` 表 + Quartz Job 触发运营任务（复用 RuoYi Quartz 管理界面） |
| 4-2 | `content_refresh` 工作流：热点抓取 → AI 重写 → Next.js ISR revalidate |
| 4-3 | SEO 指标采集（Cloudflare Analytics API / GSC API 接入） |
| 4-4 | 矩阵站内链自动优化（基于 pgvector 语义相关性排序）|
| 4-5 | 运营看板 Vue3 界面（ECharts 图表，复用现有工作日志展示模式） |

**总工期估算：~10周（2-3人团队）**  
*对比从零搭建 NestJS + React + 认证系统：~16-20周*

---

## 十二、与 game-box 的数据互通

当新系统稳定后，game-box 现有数据可以按以下路径迁入：

```sql
-- game-box 的游戏数据迁入 content_items（一次性脚本）
INSERT INTO content_items (site_id, type_code, title, slug, cover_image, fields_json)
SELECT 
  sg.site_id,
  'game',
  g.game_name,
  g.slug,
  g.cover_image,
  jsonb_build_object(
    'downloadUrl',  g.download_url,
    'rating',       g.rating,
    'developer',    g.developer
    -- ... 其他字段
  )
FROM gb_games g
JOIN gb_site_game_relations sg ON g.id = sg.game_id;
```

迁移后 game-box 的管理后台可以切换到新系统，展示侧 Next-web 继续用原有代码直到 Universal Renderer 覆盖所有功能。
