# 前端项目部署说明

此目录包含前端项目（RuoYi-Vue3）的 Docker 部署配置。

## 部署架构

- **frontend 服务**: 使用 Node.js 18 Alpine 镜像，负责从 Git 拉取代码、安装依赖并构建前端项目
- **nginx 服务**: 使用 Nginx Alpine 镜像，负责提供静态文件服务

## 部署步骤

1. **配置环境变量**
   ```bash
   cp .env.example 1panel.env
   # 编辑 1panel.env 文件，设置正确的 Git 用户名和密码
   ```

2. **启动服务**
   ```bash
   docker-compose up -d
   ```

3. **查看日志**
   ```bash
   # 查看构建日志
   docker-compose logs -f frontend
   
   # 查看 Nginx 日志
   docker-compose logs -f nginx
   ```

4. **访问应用**
   
   浏览器访问: http://localhost:8891

## 配置说明

### 端口
- `8891`: 前端应用访问端口（可在 docker-compose.yml 中修改）

### 环境变量
- `GIT_REPO`: Git 仓库地址
- `GIT_BRANCH`: Git 分支（默认 main）
- `GIT_USER`: Git 用户名
- `GIT_PASS`: Git 密码

### Nginx 配置
- 支持 SPA 路由
- 启用 Gzip 压缩
- 静态资源缓存 1 年
- API 代理配置（已注释，如需要可取消注释）

## 常用命令

```bash
# 启动服务
docker-compose up -d

# 停止服务
docker-compose down

# 重新构建（更新代码）
docker-compose restart frontend

# 查看服务状态
docker-compose ps

# 清理所有数据（包括代码和依赖）
docker-compose down -v
```

## 自定义配置

### 修改 Nginx 配置
编辑 `nginx.conf` 文件，然后重启 nginx 服务：
```bash
docker-compose restart nginx
```

### 修改构建命令
如果需要使用不同的构建命令（如 staging 环境），可以在 docker-compose.yml 中修改：
```yaml
pnpm run build:stage  # 替换 build:prod
```

## 注意事项

1. 首次启动会拉取代码并安装依赖，可能需要几分钟时间
2. pnpm 依赖会被缓存到 volume 中，后续构建会更快
3. 如果代码更新，重启 frontend 服务即可重新构建
4. 确保后端服务已正确配置 CORS，允许前端访问
