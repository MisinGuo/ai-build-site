# AI 建站运营平台 - 技术文档

> 基于 Spring Boot + RuoYi 的 AI 建站、矩阵建站、AI 运营一体化平台

## 核心文档

| 文档 | 说明 |
|------|------|
| [AI建站运营平台技术方案](./AI建站运营平台技术方案.md) | 整体技术架构、技术选型、实施路径 |
| [全行业建站工具升级方案](./全行业建站工具升级方案.md) | 通用内容类型表设计、Universal Renderer |
| [综合站完善规划](./综合站完善规划.md) | 综合展示站规划 |

## 工具与工作流

| 文档 | 说明 |
|------|------|
| [工作流使用指南](./工作流使用指南.md) | 工作流系统基础使用 |
| [工作流工具编排系统使用指南](./工作流工具编排系统使用指南.md) | 工具编排高级用法 |
| [内容工具工作流方案V2](./内容工具工作流方案V2.md) | 内容生成工作流设计 |
| [原子工具实现方案](./原子工具实现方案.md) | @SystemTool 工具执行器体系 |

## 功能参考

| 文档 | 说明 |
|------|------|
| [多语言支持解决方案](./多语言支持解决方案.md) | gb_translations 多语言机制 |
| [多语言API支持说明](./多语言API支持说明.md) | API 多语言接入说明 |
| [子站SEO内容战略指南](./子站SEO内容战略指南.md) | 矩阵站 SEO 策略 |
| [Cloudflare首页缓存方案对比](./Cloudflare首页缓存方案对比.md) | 边缘缓存方案 |

## 技术栈

- **后端**：Spring Boot 2.x + RuoYi + Spring WebFlux（AI 流式）
- **管理前端**：Vue3 + Element Plus（RuoYi-Vue3）
- **展示前端**：Next.js + Cloudflare Pages（Universal Renderer）
- **数据库**：PostgreSQL 16 + pgvector
- **搜索**：Elasticsearch 8.x + ik-analyzer
- **缓存/队列**：Redis + Quartz + Spring @Async
