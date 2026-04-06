import request from '@/utils/request'

// 获取网站代码管理信息
export function getCodeManageInfo(siteId) {
  return request({
    url: '/gamebox/code-manage/' + siteId,
    method: 'get'
  })
}

// 获取部署状态
export function getDeployStatus(siteId) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/status',
    method: 'get'
  })
}

// 获取本地仓库信息
export function getRepoInfo(siteId) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/repo-info',
    method: 'get'
  })
}

export function getRepoSyncStatus(siteId) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/repo-sync-status',
    method: 'get',
    timeout: 40000
  })
}

// 保存Git配置
export function saveGitConfig(siteId, data) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/git-config',
    method: 'post',
    data: data
  })
}

// 保存部署配置
export function saveDeployConfig(siteId, data) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/deploy-config',
    method: 'post',
    data: data
  })
}

// 测试Git连接
export function testGitConnection(siteId, data) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/git-test',
    method: 'post',
    data: data
  })
}

// 拉取代码
export function pullCode(siteId) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/pull',
    method: 'post',
    timeout: 10000
  })
}

// 获取拉取状态和日志（前端轮询接口）
export function getPullStatus(siteId) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/pull-status',
    method: 'get',
    timeout: 10000
  })
}

// 构建项目（后端立即返回，通过 getBuildStatus 轮询进度）
export function buildProject(siteId) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/build',
    method: 'post',
    timeout: 10000
  })
}

// 安装项目依赖
export function installDependencies(siteId) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/install',
    method: 'post',
    timeout: 10000
  })
}

// 获取安装依赖状态和日志（前端轮询接口）
export function getInstallStatus(siteId) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/install-status',
    method: 'get',
    timeout: 10000
  })
}

// 启动预览服务器
export function startPreview(siteId) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/preview/start',
    method: 'post',
    timeout: 60000
  })
}

// 停止预览服务器
export function stopPreview(siteId) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/preview/stop',
    method: 'post'
  })
}

// 获取预览服务器状态和日志
export function getPreviewStatus(siteId) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/preview/status',
    method: 'get'
  })
}

// ===== 文件管理 =====

// 获取仓库文件列表
export function listFiles(siteId, path) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/files',
    method: 'get',
    params: { path: path || '' }
  })
}

// 读取文件内容
export function readFile(siteId, path) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/file',
    method: 'get',
    params: { path },
    timeout: 30000
  })
}

// 保存文件内容
export function saveFile(siteId, path, content) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/file',
    method: 'put',
    data: { path, content },
    timeout: 30000
  })
}

// 提交并推送代码到远程仓库
export function pushCode(siteId, commitMessage) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/push',
    method: 'post',
    data: { commitMessage },
    timeout: 10000
  })
}

// 获取推送状态和日志（前端轮询接口）
export function getPushStatus(siteId) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/push-status',
    method: 'get',
    timeout: 10000
  })
}

// ===== 文件管理增强 =====

// 删除文件或目录
export function deleteFile(siteId, path) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/file',
    method: 'delete',
    params: { path }
  })
}

// 重命名/移动文件或目录
export function renameFile(siteId, oldPath, newPath) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/file/rename',
    method: 'post',
    data: { oldPath, newPath }
  })
}

// 复制文件或目录
export function copyFile(siteId, sourcePath, destPath) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/file/copy',
    method: 'post',
    data: { sourcePath, destPath }
  })
}

// 新建文件或目录
export function createFile(siteId, path, isDirectory) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/file/create',
    method: 'post',
    data: { path, isDirectory: !!isDirectory }
  })
}

// 上传文件到指定目录
export function uploadFile(siteId, dirPath, file) {
  const formData = new FormData()
  formData.append('file', file)
  if (dirPath) formData.append('dirPath', dirPath)
  return request({
    url: '/gamebox/code-manage/' + siteId + '/file/upload',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data', repeatSubmit: false },
    timeout: 60000
  })
}

// ===== Git 增强 =====

// 获取 git status
export function getGitStatus(siteId) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/git/status',
    method: 'get'
  })
}

// 暂存文件（path 为空则暂存全部）
export function gitStage(siteId, path) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/git/stage',
    method: 'post',
    params: { path: path || '' }
  })
}

// 取消暂存（path 为空则取消全部）
export function gitUnstage(siteId, path) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/git/unstage',
    method: 'post',
    params: { path: path || '' }
  })
}

// 提交已暂存更改
export function gitCommit(siteId, message) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/git/commit',
    method: 'post',
    data: { message }
  })
}

// 仅推送
export function gitPushOnly(siteId) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/git/push-only',
    method: 'post',
    timeout: 120000
  })
}

// 放弃更改（tracked: git checkout -- <path>，untracked: git clean -f <path>）
export function gitDiscard(siteId, path, isUntracked) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/git/discard',
    method: 'post',
    params: { path: path || '', untracked: isUntracked ? 'true' : 'false' }
  })
}

// 获取 git diff（path 为空则返回全部 diff）
export function getGitDiff(siteId, path) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/git/diff',
    method: 'get',
    params: { path: path || '' }
  })
}

// 获取文件在 HEAD 的原始内容（用于 diff 对比）
export function getGitOriginal(siteId, path) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/git/original',
    method: 'get',
    params: { path }
  })
}

// 获取 git log（branch 为空时返回 --all 所有分支）
export function getGitLog(siteId, limit, branch, includeRemote = false) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/git/log',
    method: 'get',
    params: { limit: limit || 50, branch: branch || '', includeRemote }
  })
}
// 获取项目 node_modules 的 .d.ts 类型声明文件（供 Monaco 语言服务）
export function getNodeTypes(siteId) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/node-types',
    method: 'get',
    timeout: 30000
  })
}

// 列出 git 分支；includeRemote=false 时跳过 ls-remote，快速返回本地分支
export function gitListBranches(siteId, includeRemote = false) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/git/branches',
    method: 'get',
    params: { includeRemote }
  })
}

// 切换分支
export function gitCheckout(siteId, branch) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/git/checkout',
    method: 'post',
    data: { branch }
  })
}

// 新建分支（git checkout -b）
export function gitCreateBranch(siteId, branch) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/git/create-branch',
    method: 'post',
    data: { branch }
  })
}

// 同步远端分支信息
export function gitFetch(siteId) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/git/fetch',
    method: 'post',
    timeout: 60000
  })
}

// 删除本地分支
export function gitDeleteBranch(siteId, branch, force = false) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/git/delete-branch',
    method: 'post',
    data: { branch, force }
  })
}

// 删除远端分支
export function gitDeleteRemoteBranch(siteId, branch) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/git/delete-remote-branch',
    method: 'post',
    data: { branch },
    timeout: 60000
  })
}

// 推送指定分支到远端
export function gitPushBranch(siteId, branch) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/git/push-branch',
    method: 'post',
    data: { branch },
    timeout: 120000
  })
}

// 检出到指定提交（Detached HEAD）
export function gitCheckoutCommit(siteId, hash) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/git/checkout-commit',
    method: 'post',
    data: { hash }
  })
}

// 基于指定提交创建分支
export function gitBranchFromCommit(siteId, hash, branch) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/git/branch-from-commit',
    method: 'post',
    data: { hash, branch }
  })
}

// 合并指定提交到当前分支
export function gitMergeCommit(siteId, hash) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/git/merge-commit',
    method: 'post',
    data: { hash }
  })
}

// Revert 指定提交
export function gitRevertCommit(siteId, hash) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/git/revert-commit',
    method: 'post',
    data: { hash }
  })
}

// 清空指定提交之后的所有历史（reset --hard，不自动强推）
export function gitResetForcePush(siteId, hash) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/git/reset-force-push',
    method: 'post',
    data: { hash },
    timeout: 120000
  })
}

// 强制推送当前分支到远端（git push --force，用于 reset 后同步）
export function gitForcePush(siteId, branch) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/git/force-push',
    method: 'post',
    data: { branch },
    timeout: 120000
  })
}

// 将指定分支合并到当前分支（git merge <branch>）
export function gitMergeBranch(siteId, branch) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/git/merge-branch',
    method: 'post',
    data: { branch },
    timeout: 60000
  })
}

// 放弃当前合并（git merge --abort）
export function gitMergeAbort(siteId) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/git/merge-abort',
    method: 'post',
    timeout: 30000
  })
}

// ========== Cloudflare Workers 部署 ==========

// 部署到 Cloudflare Workers
export function deployToWorkers(siteId) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/deploy-workers',
    method: 'post',
    timeout: 180000  // 3分钟超时
  })
}

// 获取账号下所有 Cloudflare Zone 列表
export function listCfZones(siteId) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/cf-zones',
    method: 'get',
    timeout: 15000
  })
}

// 获取 Worker 已绑定的域名列表
export function listWorkerDomains(siteId) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/worker-domains',
    method: 'get'
  })
}

// 绑定域名到 Worker
export function bindWorkerDomain(siteId, hostname, zoneName) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/worker-domains',
    method: 'post',
    data: { hostname, zoneName }
  })
}

// 解绑域名
export function unbindWorkerDomain(siteId, domainId) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/worker-domains/' + domainId,
    method: 'delete'
  })
}

// 验证 Cloudflare API Token 与账户 ID 是否有效
export function verifyCfCredentials(siteId) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/cf-verify',
    method: 'get',
    timeout: 15000
  })
}

// 在 Cloudflare 中创建 Workers 项目（无需去 CF 控制台手动操作）
export function createCfProject(siteId) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/create-cf-project',
    method: 'post',
    timeout: 60000
  })
}

// 检查 Cloudflare Workers 项目是否已存在
export function checkCfProject(siteId) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/check-cf-project',
    method: 'get',
    timeout: 15000
  })
}

// 删除 Cloudflare Workers 项目脚本
export function deleteCfProject(siteId) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/delete-cf-project',
    method: 'delete',
    timeout: 20000
  })
}

// 获取构建日志及运行状态（前端轮询接口）
export function getBuildStatus(siteId) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/build-status',
    method: 'get',
    timeout: 10000
  })
}

// 触发 GitHub Actions 工作流部署
export function triggerGithubAction(siteId) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/trigger-github-action',
    method: 'post',
    timeout: 30000
  })
}

// 获取 GitHub Actions 运行状态及日志（前端轮询接口）
export function getGithubActionStatus(siteId) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/github-action-status',
    method: 'get',
    timeout: 10000
  })
}

// 为当前仓库配置「推送自动触发」GitHub Actions（注入 workflow + 设置 Secrets）
export function setupGithubActionsPush(siteId) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/github-actions-push/setup',
    method: 'post',
    timeout: 60000
  })
}

// 查询 GitHub Actions Push 模式已配置状态（工作流文件 + Secrets 是否存在）
export function checkGithubActionsPushStatus(siteId) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/github-actions-push/status',
    method: 'get',
    timeout: 15000
  })
}

// 手动触发模式（workflow_dispatch）：将工作流文件推送到远端 actionRepo
export function setupGithubActionsDispatch(siteId) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/github-actions-dispatch/setup',
    method: 'post',
    timeout: 60000
  })
}

// 查询手动触发模式配置状态（工作流文件 + Token + Secrets）
export function checkGithubActionsDispatchStatus(siteId) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/github-actions-dispatch/status',
    method: 'get',
    timeout: 15000
  })
}

// ========== 向导式建站 ==========

// 从模板初始化站点（异步，立即返回 taskId，通过 getRebuildStatus 轮询进度）
export function setupFromTemplate(siteId, data) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/setup-from-template',
    method: 'post',
    data: data,
    timeout: 15000
  })
}

// 查询重建任务状态（前端轮询接口）
export function getRebuildStatus(siteId, taskId) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/rebuild-status',
    method: 'get',
    params: { taskId },
    timeout: 10000
  })
}

// 将当前 DB 配置写入代码文件并推送
export function applyConfig(siteId, data) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/apply-config',
    method: 'post',
    data: data,
    timeout: 300000
  })
}

// 获取可视化配置（读取 .env.production + DB 信息）
export function getVisualConfig(siteId) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/visual-config',
    method: 'get'
  })
}

// 保存可视化配置（更新 DB + 写入代码文件 + 推送）
export function saveVisualConfig(siteId, data) {
  return request({
    url: '/gamebox/code-manage/' + siteId + '/visual-config',
    method: 'post',
    data: data,
    timeout: 300000
  })
}

