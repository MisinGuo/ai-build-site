package com.ruoyi.gamebox.service;

import java.util.Map;

/**
 * 代码管理Service接口
 * 负责Git仓库配置、本地构建、预览等功能
 *
 * @author ruoyi
 */
public interface IGbCodeManageService {

    /**
     * 获取网站代码管理信息（基础信息 + 代码管理配置，不含敏感 token）
     */
    Map<String, Object> getCodeManageInfo(Long siteId);

    /**
     * 保存Git配置
     *
     * @param siteId      网站ID
     * @param gitProvider git提供商
     * @param gitRepoUrl  仓库URL
     * @param gitBranch   分支
     * @param gitToken    访问令牌
     * @param gitAutoSync 是否自动同步
     * @return 操作结果
     */
    Map<String, Object> saveGitConfig(Long siteId, String gitProvider, String gitRepoUrl,
                                      String gitBranch, String gitToken, String gitAutoSync,
                                      boolean autoCreate, String repoName, boolean repoPrivate,
                                      Long gitAccountId);

    /**
     * 保存部署配置
     *
     * @param siteId              网站ID
     * @param deployPlatform      部署平台
     * @param cloudflareAccountId Cloudflare账户ID
     * @param cloudflareApiToken  Cloudflare API Token
     * @param cloudflareProjectName Cloudflare项目名称
     * @param deployConfig        部署配置JSON
     * @return 操作结果
     */
    Map<String, Object> saveDeployConfig(Long siteId, String deployPlatform,
                                          Long cfAccountId,
                                          String cloudflareProjectName, String oldCloudflareProjectName, String deployConfig);

    /**
     * 测试Git连接
     *
     * @param siteId 网站ID
     * @param gitRepoUrl 仓库 URL
     * @param gitProvider 提供商
     * @param gitToken 访问令牌
     * @return 测试结果
     */
    Map<String, Object> testGitConnection(Long siteId, String gitRepoUrl, String gitProvider, String gitToken);

    /**
     * 获取仓库目录下的文件列表
     *
     * @param siteId 网站ID
     * @param path   相对路径（空或"/"表示根目录）
     * @return 文件/目录列表
     */
    Map<String, Object> listFiles(Long siteId, String path);

    /**
     * 读取文件内容
     *
     * @param siteId 网站ID
     * @param path   文件相对路径
     * @return 文件内容
     */
    Map<String, Object> readFile(Long siteId, String path);

    /**
     * 保存文件内容
     *
     * @param siteId  网站ID
     * @param path    文件相对路径
     * @param content 文件内容
     * @return 操作结果
     */
    Map<String, Object> saveFile(Long siteId, String path, String content);

    /**
     * 拉取/克隆代码
     *
     * @param siteId 网站ID
     * @return 操作结果（包含日志）
     */
    Map<String, Object> pullCode(Long siteId);

    /**
     * 获取拉取状态和日志（前端轮询接口）
     */
    Map<String, Object> getPullStatus(Long siteId);

    /**
     * 安装依赖并构建项目
     *
     * @param siteId 网站ID
     * @return 操作结果（包含日志）
     */
    Map<String, Object> buildProject(Long siteId);

    /**
     * 获取构建/部署状态和日志
     *
     * @param siteId 网站ID
     * @return 状态信息
     */
    Map<String, Object> getDeployStatus(Long siteId);

    /**
     * 获取本地仓库信息（当前分支、最近提交等）
     *
     * @param siteId 网站ID
     * @return 仓库信息
     */
    Map<String, Object> getRepoInfo(Long siteId);

    /**
     * 异步检查本地仓库与远端的同步状态（执行 fetch 后计算 ahead/behind）
     */
    Map<String, Object> getRepoSyncStatus(Long siteId);

    /**
     * 启动本地预览服务器（pnpm dev）
     *
     * @param siteId 网站ID
     * @return 预览服务器信息（包含端口和URL）
     */
    Map<String, Object> startPreviewServer(Long siteId);

    /**
     * 安装项目依赖（pnpm install）
     *
     * @param siteId 网站ID
     * @return 操作结果（包含log）
     */
    Map<String, Object> installDependencies(Long siteId);

    /**
     * 获取安装依赖状态和日志（前端轮询接口）
     */
    Map<String, Object> getInstallStatus(Long siteId);

    /**
     * 停止本地预览服务器
     *
     * @param siteId 网站ID
     * @return 操作结果
     */
    Map<String, Object> stopPreviewServer(Long siteId);

    /**
     * 获取预览服务器状态
     *
     * @param siteId 网站ID
     * @return 状态信息（端口、运行状态等）
     */
    Map<String, Object> getPreviewStatus(Long siteId);

    /**
     * 获取指定网站的预览服务器端口
     *
     * @param siteId 网站ID
     * @return 端口号，未运行则返回 null
     */
    Integer getPreviewPort(Long siteId);

    /**
     * 提交并推送代码到远程仓库（git add . + git commit + git push）
     *
     * @param siteId        网站ID
     * @param commitMessage 提交信息
     * @return 操作结果（包含日志）
     */
    Map<String, Object> pushCode(Long siteId, String commitMessage);

    /**
     * 获取推送状态和日志（前端轮询接口）
     */
    Map<String, Object> getPushStatus(Long siteId);

    // ============== 文件管理增强 ==============

    /**
     * 删除文件或目录
     *
     * @param siteId 网站ID
     * @param path   相对路径
     * @return 操作结果
     */
    Map<String, Object> deleteFile(Long siteId, String path);

    /**
     * 重命名/移动文件或目录
     *
     * @param siteId   网站ID
     * @param oldPath  原路径
     * @param newPath  新路径
     * @return 操作结果
     */
    Map<String, Object> renameFile(Long siteId, String oldPath, String newPath);

    /**
     * 复制文件或目录
     *
     * @param siteId     网站ID
     * @param sourcePath 源路径
     * @param destPath   目标路径
     * @return 操作结果
     */
    Map<String, Object> copyFile(Long siteId, String sourcePath, String destPath);

    /**
     * 新建文件或目录
     *
     * @param siteId      网站ID
     * @param path        相对路径（含文件名）
     * @param isDirectory 是否为目录
     * @return 操作结果
     */
    Map<String, Object> createFile(Long siteId, String path, boolean isDirectory);

    /**
     * 上传文件到指定目录
     *
     * @param siteId    网站ID
     * @param dirPath   目标目录相对路径
     * @param fileName  文件名
     * @param content   文件内容字节
     * @return 操作结果
     */
    Map<String, Object> uploadFile(Long siteId, String dirPath, String fileName, byte[] content);

    // ============== Git 增强 ==============

    /**
     * 获取 git status（分别返回 staged 和 unstaged 列表）
     */
    Map<String, Object> getGitStatus(Long siteId);

    /**
     * 晨2件添加到暂存区（git add）
     * @param path 文件相对路径，为空则暂存全部
     */
    Map<String, Object> gitStage(Long siteId, String path);

    /**
     * 取消暂存（git restore --staged）
     * @param path 文件相对路径，为空则取消全部
     */
    Map<String, Object> gitUnstage(Long siteId, String path);

    /**
     * 提交已暂存的更改（git commit）
     */
    Map<String, Object> gitCommit(Long siteId, String message);

    /**
     * 仅推送（git push，不做 add/commit）
     */
    Map<String, Object> gitPushOnly(Long siteId);

    /**
     * 放弃文件更改（tracked: git checkout -- path；untracked: git clean -f path）
     *
     * @param siteId     网站ID
     * @param path       文件相对路径
     * @param untracked  是否为未跟踪文件
     */
    Map<String, Object> gitDiscard(Long siteId, String path, boolean untracked);

    /**
     * 获取文件的 git diff
     */
    Map<String, Object> getGitDiff(Long siteId, String path);

    /**
     * 获取 git 提交历史
     *
     * @param siteId        网站ID
     * @param limit         最多返回条数
     * @param branch        过滤分支（空/null 表示 --all 所有分支）
     * @param includeRemote false=用本地 remote-tracking refs 构建分支列表（无网络），true=ls-remote 强制刷新
     * @return 提交列表
     */
    Map<String, Object> getGitLog(Long siteId, int limit, String branch, boolean includeRemote);

    /**
     * 获取文件在 HEAD 版本的原始内容（用于 diff 对比）
     *
     * @param siteId 网站ID
     * @param path   文件相对路径
     * @return 原始文件内容
     */
    Map<String, Object> getGitOriginal(Long siteId, String path);

    /**
     * 获取项目 node_modules 下的 .d.ts 类型声明文件（为 Monaco 提供类型支持）
     *
     * @param siteId 网站ID
     * @return 类型声明文件列表，每项包含 path 和 content
     */
    Map<String, Object> getNodeTypes(Long siteId);

    /**
     * 列出 git 分支；includeRemote=false 时仅返回本地分支（快速），true 时额外通过 ls-remote 查询远端
     */
    Map<String, Object> gitListBranches(Long siteId, boolean includeRemote);

    /**
     * fetch 远端分支信息（--prune）
     *
     * @param siteId 网站ID
     * @return 操作结果
     */
    Map<String, Object> gitFetch(Long siteId);

    /**
     * 切换分支（git checkout &lt;branch&gt;）
     *
     * @param siteId 网站ID
     * @param branch 目标分支名
     * @return 操作结果
     */
    Map<String, Object> gitCheckoutBranch(Long siteId, String branch);

    /**
     * 新建分支并切换（git checkout -b &lt;branch&gt;）
     *
     * @param siteId 网站ID
     * @param branch 新分支名
     * @return 操作结果
     */
    Map<String, Object> gitCreateBranch(Long siteId, String branch);

    /**
     * 删除本地分支
     *
     * @param siteId  网站ID
     * @param branch  分支名
     * @param force   是否强制删除（-D）
     * @return 操作结果
     */
    Map<String, Object> gitDeleteBranch(Long siteId, String branch, boolean force);

    /**
     * 删除远端分支
     *
     * @param siteId  网站ID
     * @param branch  分支名
     * @return 操作结果
     */
    Map<String, Object> gitDeleteRemoteBranch(Long siteId, String branch);

    /**
     * 推送指定分支到远端
     *
     * @param siteId  网站ID
     * @param branch  分支名
     * @return 操作结果
     */
    Map<String, Object> gitPushBranch(Long siteId, String branch);

    /**
     * 强制推送分支到远端（git push --force）
     */
    Map<String, Object> gitForcePush(Long siteId, String branch);

    /**
     * 检出到指定提交（Detached HEAD）
     */
    Map<String, Object> gitCheckoutCommit(Long siteId, String hash);

    /**
     * 基于指定提交创建新分支
     */
    Map<String, Object> gitBranchFromCommit(Long siteId, String hash, String branch);

    /**
     * 将指定提交合并到当前分支（git merge）
     */
    Map<String, Object> gitMergeCommit(Long siteId, String hash);

    /**
     * Revert 指定提交（生成反向提交）
     */
    Map<String, Object> gitRevertCommit(Long siteId, String hash);

    /**
     * 清空指定提交之后的所有历史（git reset --hard + force push）
     * ⚠️ 高危操作，不可恢复
     */
    Map<String, Object> gitResetHardForcePush(Long siteId, String hash);

    /**
     * 将指定分支合并到当前分支（git merge &lt;branch&gt;）
     *
     * @param siteId 网站ID
     * @param branch 要合并的来源分支名
     * @return 操作结果；冲突时含 conflict=true 及 conflictFiles 列表
     */
    Map<String, Object> gitMergeBranch(Long siteId, String branch);

    /**
     * 放弃当前合并（git merge --abort）
     *
     * @param siteId 网站ID
     * @return 操作结果
     */
    Map<String, Object> gitMergeAbort(Long siteId);

    // ============== Cloudflare Workers 部署 ==============

    /**
     * 部署到 Cloudflare Workers（直接调用 CF Workers API 上传脚本）
     * 注意：该方法不执行 build，需先调用 buildProject 完成构建
     *
     * @param siteId 网站 ID
     * @return 操作结果（含 log）
     */
    Map<String, Object> deployToWorkers(Long siteId);

    /**
     * 获取账号下所有 Cloudflare Zone 列表
     *
     * @param siteId 网站 ID
     * @return zones 列表（每项含 id/name）
     */
    java.util.List<java.util.Map<String, Object>> listCfZones(Long siteId);

    /**
     * 获取 Worker 已绑定的自定义域名列表
     *
     * @param siteId 网站 ID
     * @return 域名列表
     */
    Map<String, Object> listWorkerDomains(Long siteId);

    /**
     * 绑定自定义域名到 Worker
     *
     * @param siteId   网站 ID
     * @param hostname 域名（如 example.com）
     * @param zoneName zone 名（通常与 hostname 相同）
     * @return 操作结果
     */
    Map<String, Object> bindWorkerDomain(Long siteId, String hostname, String zoneName);

    /**
     * 解绑自定义域名（通过 domain id）
     *
     * @param siteId   网站 ID
     * @param domainId Cloudflare 域名绑定 ID
     * @return 操作结果
     */
    Map<String, Object> unbindWorkerDomain(Long siteId, String domainId);

    /**
     * 验证 Cloudflare API Token 与账户 ID 是否有效
     *
     * @param siteId 网站 ID
     * @return 验证结果，包含 success/message/accountName 等字段
     */
    Map<String, Object> verifyCfCredentials(Long siteId);

    /**
     * 在 Cloudflare 中创建 Workers 项目（上传占位脚本，无需先去 CF 控制台手动创建）
     *
     * @param siteId 网站 ID
     * @return 操作结果，包含 success/message/workerUrl
     */
    Map<String, Object> createCloudflareWorkerProject(Long siteId);

    /**
     * 检查 Cloudflare Workers 项目是否已存在
     *
     * @param siteId 网站 ID
     * @return 操作结果，包含 success/exists/workerUrl
     */
    Map<String, Object> checkCloudflareWorkerProject(Long siteId);

    /**
     * 删除 Cloudflare Workers 项目脚本
     *
     * @param siteId 网站 ID
     * @return 操作结果，包含 success/message
     */
    Map<String, Object> deleteCloudflareWorkerProject(Long siteId);

    /**
     * 获取当前构建日志及运行状态（用于前端实时轮询）
     *
     * @param siteId 网站 ID
     * @return running=是否在运行, done=是否结束, success=结果, log=当前日志
     */
    Map<String, Object> getBuildStatus(Long siteId);

    /**
     * 触发 GitHub Actions 工作流，异步执行构建部署
     */
    Map<String, Object> triggerGithubAction(Long siteId);

    /**
     * 获取 GitHub Actions 运行状态及实时日志（前端轮询接口）
     */
    Map<String, Object> getGithubActionStatus(Long siteId);

    /**
     * 为当前管理仓库配置「推送自动触发」GitHub Actions 工作流：
     * 1. 在受管仓库注入 .github/workflows/ YAML
     * 2. 通过 GitHub API 设置 CF_API_TOKEN / CF_ACCOUNT_ID Secret
     */
    Map<String, Object> setupGithubActionsPush(Long siteId);

    /** 检查 GitHub Actions Push 模式已配置状态（工作流文件 + Secrets） */
    Map<String, Object> checkGithubActionsPushStatus(Long siteId);

    /**
     * 为手动触发模式通过 GitHub Contents API 把 workflow_dispatch YAML推送到远端 actionRepo
     */
    Map<String, Object> setupGithubActionsDispatch(Long siteId);

    /** 检查手动触发模式配置状态（工作流文件是否存在 + Token + Secrets） */
    Map<String, Object> checkGithubActionsDispatchStatus(Long siteId);

    // ──────────────────────────────────────────────────────────────────────────
    // 向导式建站
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * 从模板初始化站点代码
     * 克隆模板仓库 → 注入环境变量 → 重设 remote → push 到用户 Git → 更新 setup_status
     *
     * @param siteId 站点 ID
     * @param params 额外参数（如 templateId、forceOverwrite 等）
     */
    Map<String, Object> setupFromTemplate(Long siteId, java.util.Map<String, Object> params);

    /**
     * 同上，但由调用方提供 liveLog 缓冲区，服务执行过程中实时追加日志，
     * 用于异步任务场景下前端轮询时读取中间进度。
     */
    Map<String, Object> setupFromTemplate(Long siteId, java.util.Map<String, Object> params, StringBuilder liveLog);

    /**
     * 将数据库中站点配置写入代码文件（.env.production、wrangler.toml 等）并 commit/push
     *
     * @param siteId      站点 ID
     * @param extraParams 额外变量覆盖（可为 null）
     */
    Map<String, Object> applyConfigToCode(Long siteId, java.util.Map<String, Object> extraParams);

    /**
     * 从代码仓库文件中解析当前可视化配置
     */
    Map<String, Object> getVisualConfig(Long siteId);

    /**
     * 保存可视化配置：更新数据库 + 写入代码文件 + commit/push
     *
     * @param siteId 站点 ID
     * @param params 新的配置值 Map（key = 配置项，value = 新值）
     */
    Map<String, Object> saveVisualConfig(Long siteId, java.util.Map<String, Object> params);
}
