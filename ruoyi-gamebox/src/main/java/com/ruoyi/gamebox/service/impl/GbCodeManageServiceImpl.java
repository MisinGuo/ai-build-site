package com.ruoyi.gamebox.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.gamebox.domain.GbPlatformCfAccount;
import com.ruoyi.gamebox.domain.GbPlatformGitAccount;
import com.ruoyi.gamebox.domain.GbSite;
import com.ruoyi.gamebox.domain.GbSiteCodeConfig;
import com.ruoyi.gamebox.domain.GbSiteTemplate;
import com.ruoyi.gamebox.mapper.GbPlatformCfAccountMapper;
import com.ruoyi.gamebox.mapper.GbPlatformGitAccountMapper;
import com.ruoyi.gamebox.mapper.GbSiteCodeConfigMapper;
import com.ruoyi.gamebox.mapper.GbSiteMapper;
import com.ruoyi.gamebox.mapper.GbSiteTemplateMapper;
import com.ruoyi.gamebox.service.IGbCodeManageService;
import com.ruoyi.gamebox.support.GbProxyConfigSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import javax.annotation.PreDestroy;

/**
 * 代码管理Service业务层处理
 * 负责Git仓库操作、本地构建和预览
 *
 * @author ruoyi
 */
@Service
public class GbCodeManageServiceImpl implements IGbCodeManageService {

    private static final Logger log = LoggerFactory.getLogger(GbCodeManageServiceImpl.class);

    /** 代码仓库本地存储根目录 */
    @Value("${ruoyi.profile}")
    private String profilePath;

    @Autowired
    private GbSiteMapper gbSiteMapper;

    @Autowired
    private GbSiteCodeConfigMapper codeConfigMapper;

    @Autowired
    private GbSiteTemplateMapper siteTemplateMapper;

    @Autowired
    private GbPlatformGitAccountMapper platformGitAccountMapper;

    @Autowired
    private GbPlatformCfAccountMapper platformCfAccountMapper;

    /** 代理配置支持（可选注入，缺失时直连） */
    @Autowired(required = false)
    private GbProxyConfigSupport proxyConfigSupport;

    /** 预览服务器进程管理：siteId -> Process */
    private static final Map<Long, Process> previewProcesses = new java.util.concurrent.ConcurrentHashMap<>();
    /** 预览服务器端口管理：siteId -> port */
    private static final Map<Long, Integer> previewPorts = new java.util.concurrent.ConcurrentHashMap<>();
    /** 预览日志管理：siteId -> log */
    private static final Map<Long, StringBuilder> previewLogs = new java.util.concurrent.ConcurrentHashMap<>();

    /** 构建日志管理：siteId -> log（实时追加） */
    private static final Map<Long, StringBuilder> buildLogs = new java.util.concurrent.ConcurrentHashMap<>();
    /** 构建运行状态：siteId -> true=运行中, false=已结束 */
    private static final Map<Long, Boolean> buildRunning = new java.util.concurrent.ConcurrentHashMap<>();
    /** 构建成功标志：siteId -> true/false（仅在 buildRunning=false 时有意义） */
    private static final Map<Long, Boolean> buildSuccess = new java.util.concurrent.ConcurrentHashMap<>();

    /** GitHub Actions 运行 ID：siteId -> runId */
    private static final Map<Long, String> gaRunIds = new java.util.concurrent.ConcurrentHashMap<>();
    /** GitHub Actions 实时日志：siteId -> log */
    private static final Map<Long, StringBuilder> gaLogs = new java.util.concurrent.ConcurrentHashMap<>();
    /** GitHub Actions 运行状态：siteId -> true=运行中 */
    private static final Map<Long, Boolean> gaRunning = new java.util.concurrent.ConcurrentHashMap<>();
    /** GitHub Actions 成功标志：siteId -> true/false */
    private static final Map<Long, Boolean> gaSuccess = new java.util.concurrent.ConcurrentHashMap<>();

    /** 拉取日志管理：siteId -> log（实时追加） */
    private static final Map<Long, StringBuilder> pullLogs = new java.util.concurrent.ConcurrentHashMap<>();
    /** 拉取运行状态：siteId -> true=运行中, false=已结束 */
    private static final Map<Long, Boolean> pullRunning = new java.util.concurrent.ConcurrentHashMap<>();
    /** 拉取成功标志：siteId -> true/false（仅在 pullRunning=false 时有意义） */
    private static final Map<Long, Boolean> pullSuccess = new java.util.concurrent.ConcurrentHashMap<>();

    /** 安装依赖日志管理：siteId -> log（实时追加） */
    private static final Map<Long, StringBuilder> installLogs = new java.util.concurrent.ConcurrentHashMap<>();
    /** 安装依赖运行状态：siteId -> true=运行中, false=已结束 */
    private static final Map<Long, Boolean> installRunning = new java.util.concurrent.ConcurrentHashMap<>();
    /** 安装依赖成功标志：siteId -> true/false（仅在 installRunning=false 时有意义） */
    private static final Map<Long, Boolean> installSuccess = new java.util.concurrent.ConcurrentHashMap<>();

    /** 推送日志管理：siteId -> log（实时追加） */
    private static final Map<Long, StringBuilder> pushLogs = new java.util.concurrent.ConcurrentHashMap<>();
    /** 推送运行状态：siteId -> true=运行中, false=已结束 */
    private static final Map<Long, Boolean> pushRunning = new java.util.concurrent.ConcurrentHashMap<>();
    /** 推送成功标志：siteId -> true/false（仅在 pushRunning=false 时有意义） */
    private static final Map<Long, Boolean> pushSuccess = new java.util.concurrent.ConcurrentHashMap<>();

    /** 日志条目延迟清理器（操作结束后 5 分钟自动释放 StringBuilder 内存） */
    private static final java.util.concurrent.ScheduledExecutorService LOG_CLEANER =
            java.util.concurrent.Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "log-cleaner");
                t.setDaemon(true);
                return t;
            });

    /** 操作结束后延迟 5 分钟清理所有相关状态条目，防止静态 Map 长期占用内存 */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @java.lang.SafeVarargs
    private static void scheduleLogCleanup(Long siteId, Map<Long, ?>... maps) {
        LOG_CLEANER.schedule(() -> {
            for (Map m : maps) m.remove(siteId);
        }, 5, java.util.concurrent.TimeUnit.MINUTES);
    }

    /**
     * Spring Boot 关闭时自动清理所有预览进程，防止重启后端口被占用
     */
    @PreDestroy
    public void cleanupAllPreviewProcesses() {
        log.info("Spring Boot 正在关闭，清理 {} 个预览进程...", previewProcesses.size());
        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
        for (Map.Entry<Long, Process> entry : previewProcesses.entrySet()) {
            Long siteId = entry.getKey();
            Process process = entry.getValue();
            Integer port = previewPorts.get(siteId);
            try {
                if (process.isAlive()) {
                    process.destroyForcibly();
                }
                if (isWindows && port != null) {
                    Thread.sleep(300);
                    killPortProcessesOnWindows(port);
                }
                log.info("已清理预览 siteId={} 端口={}", siteId, port);
            } catch (Exception e) {
                log.warn("清理预览 siteId={} 失败: {}", siteId, e.getMessage());
            }
        }
        previewProcesses.clear();
        previewPorts.clear();
        previewLogs.clear();
    }

    /**
     * 获取网站本地仓库目录（以 siteId 命名，不受网站编码变更影响）。
     */
    private String getRepoDir(Long siteId, String siteCode) {
        return profilePath + "/repos/" + siteId;
    }

    @Override
    public Map<String, Object> getCodeManageInfo(Long siteId) {
        GbSite site = gbSiteMapper.selectGbSiteById(siteId);
        if (site == null) return null;
        Map<String, Object> info = new HashMap<>();
        // 基础字段
        info.put("id", site.getId());
        info.put("name", site.getName());
        info.put("code", site.getCode());
        info.put("domain", site.getDomain());
        info.put("siteType", site.getSiteType());
        info.put("status", site.getStatus());
        info.put("templateId", site.getTemplateId());
        // 代码管理配置字段
        GbSiteCodeConfig cfg = codeConfigMapper.selectBySiteId(siteId);
        if (cfg != null) {
            info.put("gitProvider", cfg.getGitProvider());
            info.put("gitRepoUrl", cfg.getGitRepoUrl());
            info.put("gitBranch", cfg.getGitBranch());
            info.put("gitAutoSync", cfg.getGitAutoSync());
            info.put("gitAccountId", cfg.getGitAccountId());
            info.put("deployPlatform", cfg.getDeployPlatform());
            info.put("cfAccountId", cfg.getCfAccountId());
            info.put("cloudflareProjectName", cfg.getCloudflareProjectName());
            info.put("deployStatus", cfg.getDeployStatus() != null ? cfg.getDeployStatus() : "not_deployed");
            info.put("deployUrl", cfg.getDeployUrl());
            info.put("lastDeployTime", cfg.getLastDeployTime());
            info.put("lastDeployLog", cfg.getLastDeployLog());
            info.put("deployConfig", cfg.getDeployConfig());
        } else {
            info.put("deployStatus", "not_deployed");
        }
        return info;
    }

    @Override
    public Map<String, Object> saveGitConfig(Long siteId, String gitProvider, String gitRepoUrl,
                                              String gitBranch, String gitToken, String gitAutoSync,
                                              boolean autoCreate, String repoName, boolean repoPrivate,
                                              Long gitAccountId) {
        Map<String, Object> result = new HashMap<>();
        GbSite site = gbSiteMapper.selectGbSiteById(siteId);
        if (site == null) {
            result.put("success", false);
            result.put("message", "网站不存在");
            return result;
        }
        // 如果传入了平台账号ID，从平台账号库解析 token / provider
        if (gitAccountId != null) {
            GbPlatformGitAccount acc = platformGitAccountMapper.selectById(gitAccountId);
            if (acc == null || !"1".equals(String.valueOf(acc.getStatus()))) {
                result.put("success", false);
                result.put("message", "所选 Git 账号不存在或已禁用");
                return result;
            }
            gitProvider = acc.getProvider();
            gitToken = acc.getToken();
        }
        GbSiteCodeConfig cfg = codeConfigMapper.selectBySiteId(siteId);
        if (cfg == null) cfg = new GbSiteCodeConfig();

        // 自动建仓：通过 API 创建远程仓库并获取 URL
        if (autoCreate && (gitRepoUrl == null || gitRepoUrl.isEmpty())) {
            if (gitToken == null || gitToken.isEmpty()) {
                result.put("success", false);
                result.put("message", "自动创建仓库需要填写 Access Token");
                return result;
            }
            String name = (repoName != null && !repoName.isEmpty()) ? repoName : site.getCode();
            StringBuilder log = new StringBuilder();
            String createdUrl = createRemoteRepo(gitProvider, "", gitToken, name, repoPrivate, log);
            if (createdUrl == null) {
                result.put("success", false);
                result.put("message", "自动创建仓库失败，请检查 Token 权限后重试\n" + log);
                return result;
            }
            gitRepoUrl = createdUrl;
            result.put("createdRepoUrl", createdUrl);
        }

        // 检测仓库 URL 是否发生变更（旧 URL 非空且与新 URL 不同 → 重绑定）
        String oldRepoUrl = cfg.getGitRepoUrl();
        boolean repoUrlChanged = oldRepoUrl != null && !oldRepoUrl.isEmpty()
                && gitRepoUrl != null && !gitRepoUrl.equals(oldRepoUrl);
        if (repoUrlChanged) {
            // 不删除本地 clone 目录——gitPushOnly 推送前会执行 git remote set-url origin <new-url>，无需手动操作
            // 仅重置 setupStatus 为 template_cloned，让站点列表页的「推送代码」按钮重新出现
            GbSite updateSite = new GbSite();
            updateSite.setId(siteId);
            updateSite.setSetupStatus("template_cloned");
            gbSiteMapper.updateGbSite(updateSite);
            result.put("repoReset", true);
        }

        cfg.setSiteId(siteId);
        cfg.setGitProvider(gitProvider);
        cfg.setGitRepoUrl(gitRepoUrl);
        cfg.setGitBranch(gitBranch != null && !gitBranch.isEmpty() ? gitBranch : "main");
        cfg.setGitAutoSync(gitAutoSync != null ? gitAutoSync : "0");
        if (gitAccountId != null) {
            cfg.setGitAccountId(gitAccountId);
        }
        codeConfigMapper.upsert(cfg);
        result.put("success", true);
        result.put("message", repoUrlChanged ? "Git配置已更新，本地仓库已清除，请重新推送代码" : "Git配置保存成功");
        return result;
    }

    @Override
    public Map<String, Object> saveDeployConfig(Long siteId, String deployPlatform,
                                                  Long cfAccountId,
                                                  String cloudflareProjectName, String oldCloudflareProjectName, String deployConfig) {
        Map<String, Object> result = new HashMap<>();
        GbSite site = gbSiteMapper.selectGbSiteById(siteId);
        if (site == null) {
            result.put("success", false);
            result.put("message", "网站不存在");
            return result;
        }
        GbSiteCodeConfig cfg = codeConfigMapper.selectBySiteId(siteId);
        if (cfg == null) cfg = new GbSiteCodeConfig();

        // 若 Worker 名称发生变更，检查新名称是否已在CF上存在，并删除旧的
        boolean nameChanged = cloudflareProjectName != null && !cloudflareProjectName.isEmpty()
                && oldCloudflareProjectName != null && !oldCloudflareProjectName.isEmpty()
                && !cloudflareProjectName.equals(oldCloudflareProjectName);
        if (nameChanged && "cloudflare-workers".equals(deployPlatform)) {
            // 解析CF账号以获取token和account ID
            cfg.setCfAccountId(cfAccountId);
            GbPlatformCfAccount cfAcc = resolveCfAccount(cfg);
            if (cfAcc != null) {
                String token = cfAcc.getApiToken();
                String accId = cfAcc.getAccountId();
                // 检查新名称是否已存在
                try {
                    String checkUrl = "https://api.cloudflare.com/client/v4/accounts/" + accId
                            + "/workers/scripts/" + cloudflareProjectName;
                    java.net.HttpURLConnection checkConn = openCfApiConn(checkUrl);
                    checkConn.setRequestMethod("GET");
                    checkConn.setRequestProperty("Authorization", "Bearer " + token);
                    checkConn.setConnectTimeout(10000);
                    checkConn.setReadTimeout(10000);
                    if (checkConn.getResponseCode() == 200) {
                        result.put("success", false);
                        result.put("message", "Worker 名称 '" + cloudflareProjectName + "' 在 Cloudflare 上已存在，请使用其他名称");
                        return result;
                    }
                } catch (Exception e) {
                    log.warn("检查新Worker名称是否存在失败", e);
                }
                // 删除旧的Worker项目
                try {
                    String deleteUrl = "https://api.cloudflare.com/client/v4/accounts/" + accId
                            + "/workers/scripts/" + oldCloudflareProjectName;
                    java.net.HttpURLConnection delConn = openCfApiConn(deleteUrl);
                    delConn.setRequestMethod("DELETE");
                    delConn.setRequestProperty("Authorization", "Bearer " + token);
                    delConn.setConnectTimeout(10000);
                    delConn.setReadTimeout(10000);
                    int delCode = delConn.getResponseCode();
                    log.info("删除旧Worker项目 [{}] 返回 HTTP {}", oldCloudflareProjectName, delCode);
                } catch (Exception e) {
                    log.warn("删除旧Worker项目失败: {}", oldCloudflareProjectName, e);
                }
            }
        }

        cfg.setSiteId(siteId);
        cfg.setDeployPlatform(deployPlatform);
        cfg.setCfAccountId(cfAccountId);
        cfg.setCloudflareProjectName(cloudflareProjectName);
        cfg.setDeployConfig(deployConfig);
        // 清除大字段，避免 upsert 时 MyBatis 将巨量构建日志写入 SQL 参数日志
        cfg.setLastDeployLog(null);
        codeConfigMapper.upsert(cfg);

        // 若 Worker 名称发生变更且本地仓库存在，同步更新 wrangler.toml 的 name 字段
        // 用户下次提交/推送时会自动携带新名称，无需额外操作
        if ("cloudflare-workers".equals(deployPlatform)
                && cloudflareProjectName != null && !cloudflareProjectName.isEmpty()) {
            try {
                String repoDir = getRepoDir(siteId, site.getCode());
                File wranglerFile = new File(repoDir, "wrangler.toml");
                if (wranglerFile.exists()) {
                    updateWranglerToml(wranglerFile, cloudflareProjectName, java.util.Collections.emptyMap());
                    log.info("已同步更新 wrangler.toml name = [{}]，siteId={}", cloudflareProjectName, siteId);
                }
            } catch (Exception e) {
                log.warn("同步更新 wrangler.toml 失败，siteId={}", siteId, e);
            }
        }

        result.put("success", true);
        result.put("message", nameChanged ? "部署配置保存成功，旧 Worker 项目已删除" : "部署配置保存成功");
        return result;
    }

    @Override
    public Map<String, Object> testGitConnection(Long siteId, String gitRepoUrl, String gitProvider, String gitToken) {
        Map<String, Object> result = new HashMap<>();
        if (gitRepoUrl == null || gitRepoUrl.isEmpty()) {
            result.put("success", false);
            result.put("message", "请先填写仓库URL");
            return result;
        }
        String token = gitToken;
        if (token == null || token.isEmpty()) {
            result.put("success", false);
            result.put("message", "请填写访问令牌，需要具有推送/管理权限的令牌");
            return result;
        }

        String provider = gitProvider != null ? gitProvider : "github";
        String repoUrl = gitRepoUrl.trim();

        try {
            String[] ownerRepo = extractOwnerRepo(repoUrl);
            if (ownerRepo == null) {
                result.put("success", false);
                result.put("message", "无法解析仓库URL，请确认格式正确（如：https://github.com/user/repo.git）");
                return result;
            }
            String owner = ownerRepo[0];
            String repo = ownerRepo[1];

            if ("github".equals(provider)) {
                verifyGitHubToken(token, owner, repo, result);
            } else if ("gitee".equals(provider)) {
                verifyGiteeToken(token, owner, repo, result);
            } else {
                // GitLab 或其他：提取服务器地址
                String serverUrl = extractServerUrl(repoUrl);
                verifyGitLabToken(token, serverUrl, owner, repo, result);
            }
        } catch (Exception e) {
            log.error("Git连接测试失败", e);
            result.put("success", false);
            result.put("message", "Git连接测试异常: " + e.getMessage());
        }
        return result;
    }

    /** 从仓库URL中提取 [owner, repo] */
    private String[] extractOwnerRepo(String repoUrl) {
        String url = repoUrl.replaceAll("\\.git$", "").trim();
        if (url.startsWith("https://") || url.startsWith("http://")) {
            String path = url.replaceFirst("https?://[^/]+/", "");
            String[] parts = path.split("/", 2);
            if (parts.length == 2) return parts;
        } else if (url.startsWith("git@")) {
            String path = url.replaceFirst("git@[^:]+:", "");
            String[] parts = path.split("/", 2);
            if (parts.length == 2) return parts;
        }
        return null;
    }

    /** 从仓库URL中提取服务器地址（用于自托管 GitLab） */
    private String extractServerUrl(String repoUrl) {
        if (repoUrl.startsWith("https://") || repoUrl.startsWith("http://")) {
            int idx = repoUrl.indexOf("://");
            String rest = repoUrl.substring(idx + 3);
            int slashIdx = rest.indexOf('/');
            return repoUrl.substring(0, idx + 3) + (slashIdx > 0 ? rest.substring(0, slashIdx) : rest);
        }
        return "https://gitlab.com";
    }

    /** 通过 GitHub API 验证令牌是否有推送权限 */
    private void verifyGitHubToken(String token, String owner, String repo, Map<String, Object> result) throws Exception {
        String apiUrl = "https://api.github.com/repos/" + owner + "/" + repo;
        com.ruoyi.gamebox.support.GbProxyConfigSupport.SimpleResponse resp =
                execGitApi("GET", apiUrl, githubHeaders(token), null, null);
        int code = resp.status;
        String body = resp.body;
        if (code == 200) {
            if (body.contains("\"push\":true")) {
                result.put("success", true);
                result.put("message", "连接成功，令牌具有推送权限 ✓");
            } else {
                result.put("success", false);
                result.put("message", "令牌有效但没有推送/管理权限，请检查令牌的权限范围（需要 repo 权限）");
            }
        } else if (code == 401) {
            result.put("success", false);
            result.put("message", "访问令牌无效或已过期（401 Unauthorized）");
        } else if (code == 403) {
            result.put("success", false);
            result.put("message", "访问令牌权限不足（403 Forbidden）");
        } else if (code == 404) {
            result.put("success", false);
            result.put("message", "仓库不存在，或令牌无权访问此私有仓库（404）");
        } else {
            result.put("success", false);
            result.put("message", "GitHub API 返回异常状态码：" + code);
        }
    }

    /** 通过 Gitee API 验证令牌是否有推送权限 */
    private void verifyGiteeToken(String token, String owner, String repo, Map<String, Object> result) throws Exception {
        String apiUrl = "https://gitee.com/api/v5/repos/" + owner + "/" + repo + "?access_token=" + token;
        java.util.Map<String, String> headers = new java.util.LinkedHashMap<>();
        headers.put("Accept", "application/json");
        headers.put("User-Agent", "GameBox");
        com.ruoyi.gamebox.support.GbProxyConfigSupport.SimpleResponse resp =
                execGitApi("GET", apiUrl, headers, null, null);
        int code = resp.status;
        String body = resp.body;
        if (code == 200) {
            if (body.contains("\"push\":true")) {
                result.put("success", true);
                result.put("message", "连接成功，令牌具有推送权限 ✓");
            } else {
                result.put("success", false);
                result.put("message", "令牌有效但没有推送/管理权限，请检查令牌的权限范围");
            }
        } else if (code == 401 || code == 403) {
            result.put("success", false);
            result.put("message", "访问令牌无效或权限不足（" + code + "）");
        } else if (code == 404) {
            result.put("success", false);
            result.put("message", "仓库不存在，或令牌无权访问此私有仓库（404）");
        } else {
            result.put("success", false);
            result.put("message", "Gitee API 返回异常状态码：" + code);
        }
    }

    /** 通过 GitLab API 验证令牌是否有推送权限（access_level >= 30） */
    private void verifyGitLabToken(String token, String serverUrl, String owner, String repo, Map<String, Object> result) throws Exception {
        String encodedPath = java.net.URLEncoder.encode(owner + "/" + repo, "UTF-8");
        String apiUrl = serverUrl + "/api/v4/projects/" + encodedPath;
        java.util.Map<String, String> headers = new java.util.LinkedHashMap<>();
        headers.put("PRIVATE-TOKEN", token);
        headers.put("Accept", "application/json");
        headers.put("User-Agent", "GameBox");
        com.ruoyi.gamebox.support.GbProxyConfigSupport.SimpleResponse resp =
                execGitApi("GET", apiUrl, headers, null, null);
        int code = resp.status;
        String body = resp.body;
        if (code == 200) {
            // access_level: 30=Developer(push), 40=Maintainer, 50=Owner
            boolean hasPush = body.contains("\"access_level\":30") || body.contains("\"access_level\":40") || body.contains("\"access_level\":50");
            if (hasPush) {
                result.put("success", true);
                result.put("message", "连接成功，令牌具有推送权限 ✓");
            } else {
                result.put("success", false);
                result.put("message", "令牌有效但没有推送/管理权限，请检查令牌的权限范围（需要 Developer 或更高权限）");
            }
        } else if (code == 401 || code == 403) {
            result.put("success", false);
            result.put("message", "访问令牌无效或权限不足（" + code + "）");
        } else if (code == 404) {
            result.put("success", false);
            result.put("message", "仓库不存在，或令牌无权访问此私有仓库（404）");
        } else {
            result.put("success", false);
            result.put("message", "GitLab API 返回异常状态码：" + code);
        }
    }

    /**
     * 通过平台 API 自动创建远程仓库，返回仓库 clone URL（HTTPS，不含 token），失败返回 null。
     *
     * @param provider   git 提供商: github / gitee / gitlab
     * @param serverUrl  GitLab 自定义服务器地址（仅 gitlab 时使用），其他平台传 null 或空
     * @param token      访问令牌
     * @param repoName   要创建的仓库名
     * @param isPrivate  是否私有
     * @param log        日志输出
     */
    /** 检查 GitHub 仓库名是否已存在于当前用户账号下（用于重建模式的自动新建警告） */
    private boolean checkGitHubRepoExists(String token, String repoName, StringBuilder log) {
        try {
            com.ruoyi.gamebox.support.GbProxyConfigSupport.SimpleResponse userResp =
                    execGitApi("GET", "https://api.github.com/user", githubHeaders(token), null, null);
            if (userResp.status != 200) return false;
            String login = extractJsonString(userResp.body, "login");
            if (login == null) return false;
            String cleanName = repoName.replaceAll("\\.git$", "").trim();
            com.ruoyi.gamebox.support.GbProxyConfigSupport.SimpleResponse repoResp =
                    execGitApi("GET", "https://api.github.com/repos/" + login + "/" + cleanName,
                            githubHeaders(token), null, null);
            if (repoResp.status == 200) {
                log.append("[检查仓库] ").append(login).append("/").append(cleanName).append(" 已存在\n");
                return true;
            }
            return false; // 404 or other → doesn't exist
        } catch (Exception e) {
            // 网络异常时不阻止流程，保守地返回 false
            return false;
        }
    }

    private String createRemoteRepo(String provider, String serverUrl, String token,
                                     String repoName, boolean isPrivate, StringBuilder log) {
        // 清洗仓库名：去掉用户可能误加的 .git 后缀，避免最终 clone_url 出现 .git.git
        if (repoName != null) {
            repoName = repoName.replaceAll("\\.git$", "").trim();
        }
        try {
            if ("github".equalsIgnoreCase(provider)) {
                String apiUrl = "https://api.github.com/user/repos";
                String body = "{\"name\":\"" + repoName + "\",\"private\":" + isPrivate + ",\"auto_init\":false}";
                com.ruoyi.gamebox.support.GbProxyConfigSupport.SimpleResponse resp =
                        execGitApi("POST", apiUrl, githubHeaders(token), body, "application/json");
                int code = resp.status;
                if (code == 201) {
                    String cloneUrl = extractJsonString(resp.body, "clone_url");
                    if (cloneUrl != null) {
                        log.append("[自动创建仓库成功] ").append(cloneUrl).append("\n");
                        return cloneUrl.replace("\\/", "/");
                    }
                } else if (code == 422) {
                    // 仓库名已存在，获取用户 login 拼出 URL
                    log.append("[仓库已存在，尝试获取现有仓库地址]\n");
                    com.ruoyi.gamebox.support.GbProxyConfigSupport.SimpleResponse userResp =
                            execGitApi("GET", "https://api.github.com/user", githubHeaders(token), null, null);
                    if (userResp.status == 200) {
                        String login = extractJsonString(userResp.body, "login");
                        if (login != null) {
                            String url = "https://github.com/" + login + "/" + repoName + ".git";
                            log.append("[使用已有仓库] ").append(url).append("\n");
                            return url;
                        }
                    }
                } else {
                    log.append("[自动创建仓库失败] GitHub HTTP ").append(code).append(" ").append(resp.body).append("\n");
                }
            } else if ("gitee".equalsIgnoreCase(provider)) {
                String apiUrl = "https://gitee.com/api/v5/user/repos";
                String body = "access_token=" + java.net.URLEncoder.encode(token, "UTF-8")
                        + "&name=" + java.net.URLEncoder.encode(repoName, "UTF-8")
                        + "&private=" + isPrivate
                        + "&auto_init=false";
                java.util.Map<String, String> giteePostHeaders = new java.util.LinkedHashMap<>();
                giteePostHeaders.put("User-Agent", "GameBox");
                com.ruoyi.gamebox.support.GbProxyConfigSupport.SimpleResponse resp =
                        execGitApi("POST", apiUrl, giteePostHeaders, body, "application/x-www-form-urlencoded");
                int code = resp.status;
                if (code == 201) {
                    String htmlUrl = extractJsonString(resp.body, "html_url");
                    if (htmlUrl != null) {
                        String cloneUrl = htmlUrl.endsWith(".git") ? htmlUrl : htmlUrl + ".git";
                        log.append("[自动创建仓库成功] ").append(cloneUrl).append("\n");
                        return cloneUrl;
                    }
                } else if (code == 422) {
                    log.append("[仓库已存在，尝试获取现有仓库地址]\n");
                    String userUrl = "https://gitee.com/api/v5/user?access_token="
                            + java.net.URLEncoder.encode(token, "UTF-8");
                    java.util.Map<String, String> giteeGetHeaders = new java.util.LinkedHashMap<>();
                    giteeGetHeaders.put("User-Agent", "GameBox");
                    com.ruoyi.gamebox.support.GbProxyConfigSupport.SimpleResponse userResp =
                            execGitApi("GET", userUrl, giteeGetHeaders, null, null);
                    if (userResp.status == 200) {
                        String login = extractJsonString(userResp.body, "login");
                        if (login != null) {
                            String url = "https://gitee.com/" + login + "/" + repoName + ".git";
                            log.append("[使用已有仓库] ").append(url).append("\n");
                            return url;
                        }
                    }
                } else {
                    log.append("[自动创建仓库失败] Gitee HTTP ").append(code).append(" ").append(resp.body).append("\n");
                }
            } else if ("gitlab".equalsIgnoreCase(provider)) {
                String srv = (serverUrl != null && !serverUrl.isEmpty()) ? serverUrl : "https://gitlab.com";
                String apiUrl = srv + "/api/v4/projects";
                String visibility = isPrivate ? "private" : "public";
                String body = "{\"name\":\"" + repoName + "\",\"visibility\":\"" + visibility
                        + "\",\"initialize_with_readme\":false}";
                java.util.Map<String, String> glHeaders = new java.util.LinkedHashMap<>();
                glHeaders.put("PRIVATE-TOKEN", token);
                glHeaders.put("User-Agent", "GameBox");
                com.ruoyi.gamebox.support.GbProxyConfigSupport.SimpleResponse resp =
                        execGitApi("POST", apiUrl, glHeaders, body, "application/json");
                int code = resp.status;
                if (code == 201) {
                    String httpUrl = extractJsonString(resp.body, "http_url_to_repo");
                    if (httpUrl != null) {
                        log.append("[自动创建仓库成功] ").append(httpUrl).append("\n");
                        return httpUrl;
                    }
                } else {
                    log.append("[自动创建仓库失败] GitLab HTTP ").append(code).append(" ").append(resp.body).append("\n");
                }
            }
        } catch (Exception e) {
            log.append("[自动创建仓库异常] ").append(e.getMessage()).append("\n");
        }
        return null;
    }

    /** 读取 HTTP InputStream 为字符串（UTF-8） */
    private String readHttpResponseBody(java.io.InputStream is) throws java.io.IOException {
        if (is == null) return "";
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }

    /**
     * 代理感知的 GitHub / Gitee / GitLab API 连接，使用 {@code git_account_ops} 代理配置。
     * 未配置代理时直连，已配置时经代理连接并输出 [PROXY ON] 日志。
     */
    private java.net.HttpURLConnection openGitApiConn(String url) throws java.io.IOException {
        return proxyConfigSupport != null
                ? proxyConfigSupport.buildHttpURLConnection(url, "git_account_ops")
                : (java.net.HttpURLConnection) new java.net.URL(url).openConnection();
    }

    /**
     * 代理感知的 Cloudflare API 连接，使用 {@code cf_account_api} 代理配置。
     */
    private java.net.HttpURLConnection openCfApiConn(String url) throws java.io.IOException {
        return proxyConfigSupport != null
                ? proxyConfigSupport.buildHttpURLConnection(url, "cf_account_api")
                : (java.net.HttpURLConnection) new java.net.URL(url).openConnection();
    }

    /** 构建 GitHub API 标准请求头 */
    private java.util.Map<String, String> githubHeaders(String token) {
        java.util.Map<String, String> h = new java.util.LinkedHashMap<>();
        h.put("Authorization", "Bearer " + token);
        h.put("Accept", "application/vnd.github+json");
        h.put("X-GitHub-Api-Version", "2022-11-28");
        h.put("User-Agent", "GameBox");
        return h;
    }

    /**
     * 通过代理感知的 Apache HttpClient 执行 Git REST API 请求。
     * Apache HttpClient 在建立 HTTPS CONNECT 隧道时能正确处理代理 407 认证，
     * java.net.HttpURLConnection 无法做到这一点。
     */
    private com.ruoyi.gamebox.support.GbProxyConfigSupport.SimpleResponse execGitApi(
            String method, String url,
            java.util.Map<String, String> headers, String body, String contentType) throws Exception {
        if (proxyConfigSupport != null) {
            switch (method.toUpperCase()) {
                case "POST": return proxyConfigSupport.executePost(url, "git_account_ops", headers, body, contentType);
                case "PUT":  return proxyConfigSupport.executePut(url, "git_account_ops", headers, body, contentType);
                default:     return proxyConfigSupport.executeGet(url, "git_account_ops", headers);
            }
        }
        // proxyConfigSupport 未注入时的直连回退（正常不会走此路径）
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) new java.net.URL(url).openConnection();
        conn.setRequestMethod(method.toUpperCase());
        if (headers != null) {
            for (java.util.Map.Entry<String, String> e : headers.entrySet()) conn.setRequestProperty(e.getKey(), e.getValue());
        }
        conn.setConnectTimeout(15000); conn.setReadTimeout(30000);
        if (body != null) {
            conn.setDoOutput(true);
            if (contentType != null) conn.setRequestProperty("Content-Type", contentType);
            conn.getOutputStream().write(body.getBytes("UTF-8"));
        }
        int status = conn.getResponseCode();
        java.io.InputStream is = (status >= 200 && status < 300) ? conn.getInputStream() : conn.getErrorStream();
        String respBody = is != null ? readHttpResponseBody(is) : "";
        return new com.ruoyi.gamebox.support.GbProxyConfigSupport.SimpleResponse(status, respBody);
    }

    @Override
    public Map<String, Object> pullCode(Long siteId) {
        Map<String, Object> result = new HashMap<>();
        GbSite site = gbSiteMapper.selectGbSiteById(siteId);
        if (site == null) {
            result.put("success", false);
            result.put("message", "网站不存在");
            return result;
        }
        GbSiteCodeConfig cfg = codeConfigMapper.selectBySiteId(siteId);
        if (cfg == null || cfg.getGitRepoUrl() == null || cfg.getGitRepoUrl().isEmpty()) {
            result.put("success", false);
            result.put("message", "请先配置Git仓库URL");
            return result;
        }

        // 如果已有拉取正在运行，返回提示
        if (Boolean.TRUE.equals(pullRunning.get(siteId))) {
            result.put("success", false);
            result.put("message", "拉取正在运行中，请稍候");
            return result;
        }

        // 初始化日志缓冲区并标记为运行中
        StringBuilder logBuilder = new StringBuilder();
        pullLogs.put(siteId, logBuilder);
        pullRunning.put(siteId, true);
        pullSuccess.remove(siteId);

        final String repoDir = getRepoDir(site.getId(), site.getCode());
        final String branch = cfg.getGitBranch() != null ? cfg.getGitBranch() : "main";
        final String authUrl = buildAuthenticatedUrl(cfg);
        final GbSiteCodeConfig finalCfg = cfg;

        // 在后台线程中执行实际拉取
        Thread pullThread = new Thread(() -> {
            boolean success = false;
            try {
                File repoDirFile = new File(repoDir);
                File gitDir = new File(repoDir, ".git");

                if (gitDir.exists()) {
                    logBuilder.append("检测到本地仓库，执行 git pull...\n");
                    runCommand(repoDirFile, new StringBuilder(), 10, "git", "remote", "set-branches", "origin", "*");
                    String[] fetchCmd = {"git", "fetch", "origin", "--prune"};
                    success = runCommand(repoDirFile, logBuilder, 120, fetchCmd);
                    if (success) {
                        String[] resetCmd = {"git", "reset", "--hard", "origin/" + branch};
                        success = runCommand(repoDirFile, logBuilder, 30, resetCmd);
                    }
                } else {
                    if (repoDirFile.exists()) {
                        logBuilder.append("目标目录已存在但不是Git仓库，正在清理...\n");
                        try {
                            java.nio.file.Files.walk(repoDirFile.toPath())
                                .sorted(java.util.Comparator.reverseOrder())
                                .map(java.nio.file.Path::toFile)
                                .forEach(File::delete);
                            logBuilder.append("目录清理完成\n");
                        } catch (Exception e) {
                            logBuilder.append("清理目录失败: ").append(e.getMessage()).append("\n");
                        }
                    }
                    logBuilder.append("执行 git clone...\n");
                    new File(profilePath + "/repos").mkdirs();
                    String[] cloneCmd = {"git", "clone", "--branch", branch, "--depth", "1", "--no-single-branch", authUrl, repoDir};
                    success = runCommand(new File(profilePath + "/repos"), logBuilder, 300, cloneCmd);
                }

                // 持久化日志
                codeConfigMapper.updateDeployStatus(siteId,
                        success ? "deployed" : finalCfg.getDeployStatus(),
                        finalCfg.getDeployUrl(),
                        new Date(),
                        logBuilder.toString());

                if (success) {
                    logBuilder.append("\n✅ 拉取成功\n");
                } else {
                    logBuilder.append("\n❌ 拉取失败\n");
                }
            } catch (Exception e) {
                log.error("拉取代码失败", e);
                logBuilder.append("\n❌ 拉取异常: ").append(e.getMessage()).append("\n");
                try {
                    codeConfigMapper.updateDeployStatus(siteId, finalCfg.getDeployStatus(),
                            finalCfg.getDeployUrl(), new Date(), logBuilder.toString());
                } catch (Exception ignored) {}
            } finally {
                pullSuccess.put(siteId, success);
                pullRunning.put(siteId, false);
                scheduleLogCleanup(siteId, pullLogs, pullRunning, pullSuccess);
            }
        });
        pullThread.setDaemon(true);
        pullThread.setName("pull-" + siteId);
        pullThread.start();

        result.put("success", true);
        result.put("message", "拉取已开始，请通过日志接口轮询进度");
        result.put("pulling", true);
        return result;
    }

    @Override
    public Map<String, Object> getPullStatus(Long siteId) {
        Map<String, Object> result = new HashMap<>();
        Boolean running = pullRunning.get(siteId);
        Boolean succeeded = pullSuccess.get(siteId);
        StringBuilder logBuf = pullLogs.get(siteId);

        result.put("running", Boolean.TRUE.equals(running));
        if (!Boolean.TRUE.equals(running) && succeeded != null) {
            result.put("done", true);
            result.put("success", succeeded);
        } else {
            result.put("done", false);
        }
        result.put("log", logBuf != null ? logBuf.toString() : "");
        return result;
    }

    @Override
    public Map<String, Object> pushCode(Long siteId, String commitMessage) {
        Map<String, Object> result = new HashMap<>();
        GbSite site = gbSiteMapper.selectGbSiteById(siteId);
        if (site == null) {
            result.put("success", false);
            result.put("message", "网站不存在");
            return result;
        }
        GbSiteCodeConfig cfg = codeConfigMapper.selectBySiteId(siteId);
        if (cfg == null || cfg.getGitRepoUrl() == null || cfg.getGitRepoUrl().isEmpty()) {
            result.put("success", false);
            result.put("message", "请先配置Git仓库URL");
            return result;
        }

        String repoDir = getRepoDir(site.getId(), site.getCode());
        if (!new File(repoDir, ".git").exists()) {
            result.put("success", false);
            result.put("message", "本地仓库不存在，请先拉取代码");
            return result;
        }

        // 如果已有推送正在运行，返回提示
        if (Boolean.TRUE.equals(pushRunning.get(siteId))) {
            result.put("success", false);
            result.put("message", "推送正在运行中，请稍候");
            return result;
        }

        // 初始化日志缓冲区并标记为运行中
        StringBuilder logBuilder = new StringBuilder();
        pushLogs.put(siteId, logBuilder);
        pushRunning.put(siteId, true);
        pushSuccess.remove(siteId);

        final String finalRepoDir = repoDir;
        final String branch = cfg.getGitBranch() != null ? cfg.getGitBranch() : "main";
        final String message = (commitMessage != null && !commitMessage.trim().isEmpty())
                ? commitMessage.trim() : "Update via Code Manager";
        final GbSiteCodeConfig finalCfg = cfg;

        // 在后台线程中执行实际推送
        Thread pushThread = new Thread(() -> {
            boolean success = false;
            try {
                File repoDirFile = new File(finalRepoDir);

                // 配置 git 用户信息
                runCommand(repoDirFile, new StringBuilder(), 10,
                        "git", "config", "user.email", "codebox@gamebox.local");
                runCommand(repoDirFile, new StringBuilder(), 10,
                        "git", "config", "user.name", "GameBox");

                // 设置远程 URL（注入 token 认证）
                String authUrl = buildAuthenticatedUrl(finalCfg);
                runCommand(repoDirFile, new StringBuilder(), 10,
                        "git", "remote", "set-url", "origin", authUrl);

                // git add .
                logBuilder.append("> git add .\n");
                boolean addOk = runCommand(repoDirFile, logBuilder, 30, "git", "add", ".");
                if (!addOk) {
                    logBuilder.append("\n❌ git add 失败\n");
                } else {
                    // 检查是否有需要提交的变更
                    StringBuilder statusLog = new StringBuilder();
                    runCommand(repoDirFile, statusLog, 10, "git", "status", "--porcelain");
                    boolean hasChanges = Arrays.stream(statusLog.toString().split("\n"))
                            .anyMatch(l -> !l.startsWith("[") && l.length() >= 2);
                    if (!hasChanges) {
                        logBuilder.append("> 工作区是干净的，没有任何变更需要提交。\n");
                        success = true;
                    } else {
                        // git commit
                        logBuilder.append("> git commit -m \"").append(message).append("\"\n");
                        boolean commitOk = runCommand(repoDirFile, logBuilder, 30,
                                "git", "commit", "-m", message);
                        if (!commitOk) {
                            logBuilder.append("\n❌ git commit 失败\n");
                        } else {
                            // git push
                            logBuilder.append("> git push origin ").append(branch).append("\n");
                            success = runCommand(repoDirFile, logBuilder, 120,
                                    "git", "push", "origin", branch);
                            logBuilder.append(success ? "\n✅ 推送成功\n" : "\n❌ 推送失败\n");
                        }
                    }
                }

                codeConfigMapper.updateDeployStatus(siteId,
                        finalCfg.getDeployStatus(), finalCfg.getDeployUrl(), new Date(), logBuilder.toString());
            } catch (Exception e) {
                log.error("推送代码失败", e);
                logBuilder.append("\n❌ 推送异常: ").append(e.getMessage()).append("\n");
                try {
                    codeConfigMapper.updateDeployStatus(siteId, finalCfg.getDeployStatus(),
                            finalCfg.getDeployUrl(), new Date(), logBuilder.toString());
                } catch (Exception ignored) {}
            } finally {
                pushSuccess.put(siteId, success);
                pushRunning.put(siteId, false);
                scheduleLogCleanup(siteId, pushLogs, pushRunning, pushSuccess);
            }
        });
        pushThread.setDaemon(true);
        pushThread.setName("push-" + siteId);
        pushThread.start();

        result.put("success", true);
        result.put("message", "推送已开始，请通过日志接口轮询进度");
        result.put("pushing", true);
        return result;
    }

    @Override
    public Map<String, Object> getPushStatus(Long siteId) {
        Map<String, Object> result = new HashMap<>();
        Boolean running = pushRunning.get(siteId);
        Boolean succeeded = pushSuccess.get(siteId);
        StringBuilder logBuf = pushLogs.get(siteId);

        result.put("running", Boolean.TRUE.equals(running));
        if (!Boolean.TRUE.equals(running) && succeeded != null) {
            result.put("done", true);
            result.put("success", succeeded);
        } else {
            result.put("done", false);
        }
        result.put("log", logBuf != null ? logBuf.toString() : "");
        return result;
    }

    @Override
    public Map<String, Object> buildProject(Long siteId) {
        Map<String, Object> result = new HashMap<>();
        GbSite site = gbSiteMapper.selectGbSiteById(siteId);
        if (site == null) {
            result.put("success", false);
            result.put("message", "网站不存在");
            return result;
        }

        String repoDir = getRepoDir(site.getId(), site.getCode());
        if (!new File(repoDir, "package.json").exists()) {
            result.put("success", false);
            result.put("message", "本地仓库不存在或未包含 package.json，请先拉取代码");
            return result;
        }

        // 如果已有构建正在运行，返回提示
        if (Boolean.TRUE.equals(buildRunning.get(siteId))) {
            result.put("success", false);
            result.put("message", "构建正在运行中，请稍候");
            return result;
        }

        // 初始化日志缓冲区并标记为运行中
        StringBuilder logBuilder = new StringBuilder();
        buildLogs.put(siteId, logBuilder);
        buildRunning.put(siteId, true);
        buildSuccess.remove(siteId);

        final GbSiteCodeConfig finalCfg0 = codeConfigMapper.selectBySiteId(siteId);
        final GbSiteCodeConfig cfg = finalCfg0 != null ? finalCfg0 : new GbSiteCodeConfig();
        cfg.setSiteId(siteId);

        // 更新数据库状态为构建中
        codeConfigMapper.updateDeployStatus(siteId, "deploying",
                cfg.getDeployUrl(), null, "构建中...");

        // 在后台线程中执行实际构建
        Thread buildThread = new Thread(() -> {
            File repoDirFile = new File(repoDir);
            boolean success = false;
            try {
                // 检测包管理器
                String pkgManager = detectPackageManager(repoDirFile);
                pkgManager = ensurePackageManager(repoDirFile, pkgManager, logBuilder);
                logBuilder.append("使用包管理器: ").append(pkgManager).append("\n");

                // 安装依赖
                logBuilder.append("安装依赖...\n");
                boolean installOk = runCommand(repoDirFile, logBuilder, 600,
                        pkgManager, "install");

                if (installOk) {
                    // 读取用户配置的构建命令（存在 deployConfig JSON 的 buildCommand 字段）
                    String[] buildCmd = null;
                    String deployConfigJson = cfg.getDeployConfig();
                    if (deployConfigJson != null && !deployConfigJson.isEmpty()) {
                        try {
                            com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();
                            com.fasterxml.jackson.databind.JsonNode node = om.readTree(deployConfigJson);
                            com.fasterxml.jackson.databind.JsonNode bcNode = node.get("buildCommand");
                            if (bcNode != null && !bcNode.asText().trim().isEmpty()) {
                                buildCmd = bcNode.asText().trim().split("\\s+");
                            }
                        } catch (Exception ignored) {}
                    }
                    if (buildCmd == null || buildCmd.length == 0) {
                        buildCmd = new String[]{pkgManager, "run", "build"};
                    }
                    logBuilder.append("执行构建: ").append(String.join(" ", buildCmd)).append("\n");
                    // 注入 Cloudflare 凭据（供 deploy:cfworkers 等命令使用）
                    Map<String, String> buildEnv = new java.util.HashMap<>();
                    GbPlatformCfAccount buildCfAcc = resolveCfAccount(cfg);
                    if (buildCfAcc != null) {
                        if (buildCfAcc.getApiToken() != null && !buildCfAcc.getApiToken().isEmpty()) {
                            buildEnv.put("CLOUDFLARE_API_TOKEN", buildCfAcc.getApiToken());
                        }
                        if (buildCfAcc.getAccountId() != null && !buildCfAcc.getAccountId().isEmpty()) {
                            buildEnv.put("CLOUDFLARE_ACCOUNT_ID", buildCfAcc.getAccountId());
                        }
                    }
                    success = buildEnv.isEmpty()
                            ? runCommand(repoDirFile, logBuilder, 600, buildCmd)
                            : runCommand(repoDirFile, logBuilder, 600, buildEnv, buildCmd);
                }

                // 更新最终状态
                String newDeployUrl = success ? "/preview/" + site.getCode() : cfg.getDeployUrl();
                codeConfigMapper.updateDeployStatus(siteId,
                        success ? "deployed" : "failed",
                        newDeployUrl,
                        new Date(),
                        logBuilder.toString());

                if (success) {
                    logBuilder.append("\n✅ 构建成功\n");
                } else {
                    logBuilder.append("\n❌ 构建失败\n");
                }
            } catch (Exception e) {
                log.error("构建项目失败", e);
                logBuilder.append("\n❌ 构建异常: ").append(e.getMessage()).append("\n");
                try {
                    codeConfigMapper.updateDeployStatus(siteId, "failed",
                            cfg.getDeployUrl(), new Date(), logBuilder.toString());
                } catch (Exception ignored) {}
            } finally {
                buildSuccess.put(siteId, success);
                buildRunning.put(siteId, false);
                scheduleLogCleanup(siteId, buildLogs, buildRunning, buildSuccess);
            }
        });
        buildThread.setDaemon(true);
        buildThread.setName("build-" + siteId);
        buildThread.start();

        result.put("success", true);
        result.put("message", "构建已开始，请通过日志接口轮询进度");
        result.put("building", true);
        return result;
    }

    @Override
    public Map<String, Object> getBuildStatus(Long siteId) {
        Map<String, Object> result = new HashMap<>();
        Boolean running = buildRunning.get(siteId);
        Boolean succeeded = buildSuccess.get(siteId);
        StringBuilder logBuf = buildLogs.get(siteId);

        result.put("running", Boolean.TRUE.equals(running));
        // 只有在非运行状态下 success 才有意义
        if (!Boolean.TRUE.equals(running) && succeeded != null) {
            result.put("done", true);
            result.put("success", succeeded);
        } else {
            result.put("done", false);
        }
        if (logBuf != null) {
            result.put("log", logBuf.toString());
        } else {
            result.put("log", "");
        }
        return result;
    }

    @Override
    public Map<String, Object> getDeployStatus(Long siteId) {
        Map<String, Object> result = new HashMap<>();
        GbSite site = gbSiteMapper.selectGbSiteById(siteId);
        if (site == null) {
            result.put("success", false);
            result.put("message", "网站不存在");
            return result;
        }
        GbSiteCodeConfig cfg = codeConfigMapper.selectBySiteId(siteId);
        result.put("success", true);
        result.put("deployStatus", cfg != null && cfg.getDeployStatus() != null ? cfg.getDeployStatus() : "not_deployed");
        result.put("deployUrl", cfg != null ? cfg.getDeployUrl() : null);
        result.put("lastDeployTime", cfg != null ? cfg.getLastDeployTime() : null);
        result.put("lastDeployLog", cfg != null ? cfg.getLastDeployLog() : null);
        // 检查本地仓库是否存在
        String repoDir = getRepoDir(site.getId(), site.getCode());
        result.put("repoExists", new File(repoDir, ".git").exists());
        result.put("buildOutputExists", new File(repoDir, ".next").exists()
                || new File(repoDir, "out").exists()
                || new File(repoDir, "dist").exists());
        return result;
    }

    @Override
    public Map<String, Object> getRepoInfo(Long siteId) {
        Map<String, Object> result = new HashMap<>();
        GbSite site = gbSiteMapper.selectGbSiteById(siteId);
        if (site == null) {
            result.put("success", false);
            result.put("message", "网站不存在");
            return result;
        }
        String repoDir = getRepoDir(site.getId(), site.getCode());
        File repoDirFile = new File(repoDir);
        if (!new File(repoDir, ".git").exists()) {
            result.put("success", false);
            result.put("message", "本地仓库不存在，请先拉取代码");
            result.put("repoExists", false);
            return result;
        }
        result.put("success", true);
        result.put("repoExists", true);
        result.put("repoDir", repoDir);

        try {
            // 获取当前分支（只取第一行，避免 runCommand 追加的 [退出码] 污染）
            StringBuilder branchLog = new StringBuilder();
            runCommand(repoDirFile, branchLog, 10, "git", "branch", "--show-current");
            String branchName = "";
            for (String l : branchLog.toString().split("\n")) {
                String trimmed = l.trim();
                if (!trimmed.isEmpty() && !trimmed.startsWith("[")) {
                    branchName = trimmed;
                    break;
                }
            }
            result.put("currentBranch", branchName);

            // 获取最近提交
            StringBuilder commitLog = new StringBuilder();
            runCommand(repoDirFile, commitLog, 10,
                    "git", "log", "--oneline", "-5");
            result.put("recentCommits", commitLog.toString().trim());

            // 获取远程URL
            StringBuilder remoteLog = new StringBuilder();
            runCommand(repoDirFile, remoteLog, 10, "git", "remote", "-v");
            result.put("remoteInfo", remoteLog.toString().trim());

        } catch (Exception e) {
            log.warn("获取仓库信息失败", e);
        }
        return result;
    }

    @Override
    public Map<String, Object> getRepoSyncStatus(Long siteId) {
        Map<String, Object> result = new HashMap<>();
        GbSite site = gbSiteMapper.selectGbSiteById(siteId);
        if (site == null) {
            result.put("success", false);
            return result;
        }
        String repoDir = getRepoDir(site.getId(), site.getCode());
        File repoDirFile = new File(repoDir);
        if (!new File(repoDir, ".git").exists()) {
            result.put("success", false);
            result.put("repoExists", false);
            return result;
        }
        result.put("success", true);
        // fetch 更新远端 tracking ref
        try {
            StringBuilder fetchSb = new StringBuilder();
            runCommand(repoDirFile, fetchSb, 30, "git", "fetch", "--quiet", "origin");
        } catch (Exception fetchEx) {
            log.warn("git fetch 失败", fetchEx);
        }
        try {
            StringBuilder behindSb = new StringBuilder();
            runCommand(repoDirFile, behindSb, 10, "git", "rev-list", "--count", "HEAD..@{u}");
            result.put("behindCount", parseCountFromLog(behindSb.toString()));

            StringBuilder aheadSb = new StringBuilder();
            runCommand(repoDirFile, aheadSb, 10, "git", "rev-list", "--count", "@{u}..HEAD");
            result.put("aheadCount", parseCountFromLog(aheadSb.toString()));
        } catch (Exception ex) {
            result.put("behindCount", -1);
            result.put("aheadCount", -1);
        }
        return result;
    }

    // ===== 工具方法 =====

    /**
     * 从 runCommand 的 logBuilder 输出中提取纯数字行（过滤掉 [执行命令]/[退出码] 等前缀行）
     */
    private int parseCountFromLog(String output) {
        for (String line : output.split("\n")) {
            String trimmed = line.trim();
            if (trimmed.matches("\\d+")) {
                return Integer.parseInt(trimmed);
            }
        }
        return 0;
    }

    /**
     * 构建带认证信息的git URL
     */
    /**
     * 将命令字符串中的 HTTPS 嵌入 token（如 https://x-access-token:TOKEN@）脱敏为 ***
     * 避免 token 出现在日志中
     */
    private static String maskSensitiveUrl(String text) {
        if (text == null) return text;
        // 匹配 https://user:TOKEN@... 模式，将 TOKEN 替换为 ***
        return text.replaceAll("(https?://[^:@\\s]+:)[^@\\s]+(@)", "$1***$2");
    }

    private String buildAuthenticatedUrl(GbSiteCodeConfig cfg) {
        String url = cfg.getGitRepoUrl();
        if (url == null) return url;
        String token = resolveGitToken(cfg);
        if (token == null || token.isEmpty()) {
            return url;
        }
        // 对于 HTTPS URL，注入 token
        if (url.startsWith("https://")) {
            String provider = cfg.getGitProvider();
            if ("github".equals(provider)) {
                // https://x-access-token:TOKEN@github.com/...
                return url.replace("https://", "https://x-access-token:" + token + "@");
            } else if ("gitee".equals(provider)) {
                // Gitee: https://oauth2:TOKEN@gitee.com/...
                return url.replace("https://", "https://oauth2:" + token + "@");
            } else {
                // GitLab 或其他: https://oauth2:TOKEN@...
                return url.replace("https://", "https://oauth2:" + token + "@");
            }
        }
        return url;
    }

    /**
     * 解析 CF 平台账号，null 表示未配置
     */
    private GbPlatformCfAccount resolveCfAccount(GbSiteCodeConfig cfg) {
        if (cfg != null && cfg.getCfAccountId() != null) {
            return platformCfAccountMapper.selectById(cfg.getCfAccountId());
        }
        return null;
    }

    /**
     * 解析 Git Token，null 表示未配置
     */
    private String resolveGitToken(GbSiteCodeConfig cfg) {
        if (cfg != null && cfg.getGitAccountId() != null) {
            GbPlatformGitAccount acc = platformGitAccountMapper.selectById(cfg.getGitAccountId());
            return acc != null ? acc.getToken() : null;
        }
        return null;
    }

    /**
     * 检测项目使用的包管理器
     */
    private String detectPackageManager(File projectDir) {
        if (new File(projectDir, "pnpm-lock.yaml").exists()) return "pnpm";
        if (new File(projectDir, "yarn.lock").exists()) return "yarn";
        return "npm";
    }

    /**
     * 检查命令是否可以在系统中执行
     */
    private boolean isCommandAvailable(String cmd) {
        try {
            boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
            ProcessBuilder pb;
            if (isWindows) {
                pb = new ProcessBuilder("where", cmd);
            } else {
                pb = new ProcessBuilder("which", cmd);
            }
            pb.redirectErrorStream(true);
            Process p = pb.start();
            p.waitFor(5, TimeUnit.SECONDS);
            return p.exitValue() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 确保包管理器可用，若 pnpm/yarn 不在 PATH 中则通过 npm 全局安装
     * 返回实际可用的包管理器名称
     */
    private String ensurePackageManager(File workDir, String pkgManager, StringBuilder logBuilder) throws Exception {
        if ("npm".equals(pkgManager) || isCommandAvailable(pkgManager)) {
            return pkgManager;
        }
        // pnpm 或 yarn 未找到，尝试全局安装
        logBuilder.append("[警告] 未找到命令: ").append(pkgManager).append("，尝试通过 npm 全局安装...\n");
        boolean installed = runCommand(workDir, logBuilder, 120, "npm", "install", "-g", pkgManager);
        if (installed && isCommandAvailable(pkgManager)) {
            logBuilder.append("安装 ").append(pkgManager).append(" 成功\n");
            return pkgManager;
        }
        logBuilder.append("[警告] 安装 ").append(pkgManager).append(" 失败，降级使用 npm\n");
        return "npm";
    }

    /**
     * 执行命令，将输出追加到 logBuilder，返回是否成功
     */
    private boolean runCommand(File workDir, StringBuilder logBuilder, int timeoutSeconds, String... cmds) throws Exception {
        // 在Windows上处理命令
        List<String> command = new ArrayList<>();
        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
        if (isWindows && !cmds[0].equals("git")) {
            command.add("cmd");
            command.add("/c");
        }
        command.addAll(Arrays.asList(cmds));

        // 若是 git 命令且已配置代理，在命令中注入 -c 选项并设置环境变量
        // 使用 http.proxyAuthMethod=basic 可让 libcurl 在初始 CONNECT 请求时就附带 Proxy-Authorization，
        // 避免代理因等不到认证而中断连接（"Proxy CONNECT aborted"）
        Map<String, String> gitProxyEnv = new java.util.LinkedHashMap<>();
        if (cmds.length > 0 && "git".equals(cmds[0]) && proxyConfigSupport != null) {
            gitProxyEnv = proxyConfigSupport.buildGitProxyEnv("git_account_ops");
            if (!gitProxyEnv.isEmpty()) {
                String proxyUrl = gitProxyEnv.get("HTTPS_PROXY");
                if (proxyUrl != null && !proxyUrl.isEmpty()) {
                    // 在 "git" 之后插入：-c http.proxy=<url> -c http.proxyAuthMethod=basic
                    command.addAll(1, java.util.Arrays.asList(
                            "-c", "http.proxy=" + proxyUrl,
                            "-c", "http.proxyAuthMethod=basic"));
                }
            }
        }

        String cmdStr = String.join(" ", command);
        String safeCmdStr = maskSensitiveUrl(cmdStr);
        log.info("[执行命令] {}", safeCmdStr);
        logBuilder.append("[执行命令] ").append(safeCmdStr).append("\n");

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(workDir);
        pb.environment().put("GIT_TERMINAL_PROMPT", "0");
        pb.redirectErrorStream(true);

        Process process = pb.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                logBuilder.append(line).append("\n");
                log.debug("[CMD] {}", line);
            }
        }
        boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
        if (!finished) {
            process.destroyForcibly();
            logBuilder.append("[超时] 命令执行超时（").append(timeoutSeconds).append("秒）\n");
            return false;
        }
        int exitCode = process.exitValue();
        logBuilder.append("[退出码] ").append(exitCode).append("\n");
        return exitCode == 0;
    }

    /**
     * 执行命令，注入额外环境变量（供 Cloudflare 部署等场景使用）
     */
    private boolean runCommand(File workDir, StringBuilder logBuilder, int timeoutSeconds, Map<String, String> extraEnv, String... cmds) throws Exception {
        List<String> command = new ArrayList<>();
        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
        if (isWindows && !cmds[0].equals("git")) {
            command.add("cmd");
            command.add("/c");
        }
        command.addAll(Arrays.asList(cmds));

        String cmdStr = String.join(" ", command);
        log.info("[执行命令] {}", cmdStr);
        logBuilder.append("[执行命令] ").append(cmdStr).append("\n");

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(workDir);
        pb.environment().put("GIT_TERMINAL_PROMPT", "0");
        if (extraEnv != null) {
            pb.environment().putAll(extraEnv);
        }
        pb.redirectErrorStream(true);

        Process process = pb.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                logBuilder.append(line).append("\n");
                log.debug("[CMD] {}", line);
            }
        }
        boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
        if (!finished) {
            process.destroyForcibly();
            logBuilder.append("[超时] 命令执行超时（").append(timeoutSeconds).append("秒）\n");
            return false;
        }
        int exitCode = process.exitValue();
        logBuilder.append("[退出码] ").append(exitCode).append("\n");
        return exitCode == 0;
    }

    @Override
    public Map<String, Object> startPreviewServer(Long siteId) {
        Map<String, Object> result = new HashMap<>();
        GbSite site = gbSiteMapper.selectGbSiteById(siteId);
        if (site == null) {
            result.put("success", false);
            result.put("message", "网站不存在");
            return result;
        }
        String repoDir = getRepoDir(site.getId(), site.getCode());
        File repoDirFile = new File(repoDir);
        if (!new File(repoDir, "package.json").exists()) {
            result.put("success", false);
            result.put("message", "本地仓库不存在或未包含 package.json，请先拉取代码");
            return result;
        }
        // 检查 node_modules 是否存在
        if (!new File(repoDir, "node_modules").exists()) {
            result.put("success", false);
            result.put("message", "node_modules 不存在，请先点击「安装依赖」");
            return result;
        }
        if (previewProcesses.containsKey(siteId)) {
            Process existing = previewProcesses.get(siteId);
            Integer existingPort = previewPorts.get(siteId);
            boolean isWin = System.getProperty("os.name").toLowerCase().contains("win");
            if (existing.isAlive()) {
                existing.destroyForcibly();
            }
            if (existingPort != null) {
                try {
                    Thread.sleep(300);
                    if (isWin) {
                        killPortProcessesOnWindows(existingPort);
                    } else {
                        killPortProcessesOnLinux(existingPort);
                    }
                } catch (Exception ignored) {}
            }
            previewProcesses.remove(siteId);
            previewPorts.remove(siteId);
        }

        try {
            // 查找可用端口（3000-3099范围）
            int port = findAvailablePort(3000, 3099);
            StringBuilder ensureLog = new StringBuilder();
            String pkgManager = detectPackageManager(repoDirFile);
            pkgManager = ensurePackageManager(repoDirFile, pkgManager, ensureLog);
            if (ensureLog.length() > 0) log.info("[预览服务] {}", ensureLog);

            // 读取用户配置的预览命令
            GbSiteCodeConfig cfgForPreview = codeConfigMapper.selectBySiteId(siteId);
            String[] previewCmdArr = null;
            if (cfgForPreview != null && cfgForPreview.getDeployConfig() != null && !cfgForPreview.getDeployConfig().isEmpty()) {
                try {
                    com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();
                    com.fasterxml.jackson.databind.JsonNode node = om.readTree(cfgForPreview.getDeployConfig());
                    com.fasterxml.jackson.databind.JsonNode pcNode = node.get("previewCommand");
                    if (pcNode != null && !pcNode.asText().trim().isEmpty()) {
                        previewCmdArr = pcNode.asText().trim().split("\\s+");
                    }
                } catch (Exception ignored) {}
            }
            if (previewCmdArr == null || previewCmdArr.length == 0) {
                previewCmdArr = new String[]{pkgManager, "run", "dev"};
            }
            log.info("[预览服务] 执行命令: {}", String.join(" ", previewCmdArr));

            boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
            List<String> command = new ArrayList<>();
            if (isWindows) {
                command.addAll(Arrays.asList("cmd", "/c"));
            }
            // 不通过 CLI 参数传递端口（Windows下 -- 分隔符会被 Next.js 误当目录处理）
            // 改用 PORT 环境变量，Next.js/Vite/Nuxt 均原生支持
            command.addAll(Arrays.asList(previewCmdArr));

            // 启动前先强制清理目标端口（防止上次未清理干净的残留占据）
            if (isWindows) {
                try { killPortProcessesOnWindows(port); Thread.sleep(200); } catch (Exception ignored) {}
            }

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(repoDirFile);
            pb.environment().put("PORT", String.valueOf(port));
            pb.environment().put("NODE_ENV", "development");
            // Next.js 13+ 额外支持 NEXT_PORT 环境变量
            pb.environment().put("NEXT_PORT", String.valueOf(port));
            // 设置 Next.js basePath，使其知道自己被挂载在代理路径下。
            // 此路径为 Spring Boot 接收到的路径（不含 /dev-api 前缀）。
            // Next.js 会自动在生成的 HTML 中包含此前缀，无需手动重写。
            pb.environment().put("NEXT_BASE_PATH", "/gamebox/code-manage/" + siteId + "/preview/proxy");
            pb.redirectErrorStream(true);

            Process process = pb.start();
            previewProcesses.put(siteId, process);
            previewPorts.put(siteId, port);
            StringBuilder previewLog = new StringBuilder();
            previewLogs.put(siteId, previewLog);

            // 异步读取输出日志
            Thread logThread = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        previewLog.append(line).append("\n");
                        // 限制日志大小
                        if (previewLog.length() > 50000) {
                            int cutIdx = previewLog.indexOf("\n", 10000);
                            if (cutIdx > 0) {
                                previewLog.delete(0, cutIdx + 1);
                            }
                        }
                    }
                } catch (Exception e) {
                    previewLog.append("[日志读取结束]\n");
                }
            });
            logThread.setDaemon(true);
            logThread.start();

            // 轮询端口监听（最多20秒）
            boolean started = false;
            for (int i = 0; i < 20; i++) {
                Thread.sleep(1000);
                if (!process.isAlive()) {
                    // 进程已退出，稍等日志线程收齐
                    Thread.sleep(500);
                    result.put("success", false);
                    result.put("port", port);
                    result.put("log", previewLog.toString());
                    result.put("message", "预览服务器进程已退出，请查看日志了解原因");
                    previewProcesses.remove(siteId);
                    previewPorts.remove(siteId);
                    return result;
                }
                try (java.net.Socket s = new java.net.Socket()) {
                    s.connect(new java.net.InetSocketAddress("127.0.0.1", port), 300);
                    started = true;
                    break;
                } catch (Exception ignored) {}
            }

            // previewUrl 返回后端代理路径（浏览器可直接访问），而非服务器本地地址
            String proxyUrl = "/gamebox/code-manage/" + siteId + "/preview/proxy";
            if (started) {
                result.put("success", true);
                result.put("port", port);
                result.put("previewUrl", proxyUrl);
                result.put("message", "预览服务器启动成功，端口: " + port);
            } else {
                // 超时：进程还活着但端口未监听——可能尚在启动中，返回成功但提示可能未就绪
                result.put("success", true);
                result.put("port", port);
                result.put("previewUrl", proxyUrl);
                result.put("message", "预览服务器启动中，端口: " + port + "（若页面无法加载请稍候刷新）");
            }
        } catch (Exception e) {
            log.error("启动预览服务器失败", e);
            result.put("success", false);
            result.put("message", "启动预览服务器异常: " + e.getMessage());
        }
        return result;
    }

    @Override
    public Map<String, Object> stopPreviewServer(Long siteId) {
        Map<String, Object> result = new HashMap<>();
        Process process = previewProcesses.get(siteId);
        Integer port = previewPorts.get(siteId);
        if (process == null || !process.isAlive()) {
            previewProcesses.remove(siteId);
            previewPorts.remove(siteId);
            previewLogs.remove(siteId);
            result.put("success", true);
            result.put("message", "预览服务器未在运行");
            return result;
        }
        // 先销毁直接子进程（cmd.exe 壳 或 sh 壳）
        process.destroyForcibly();
        // Windows/Linux 下 shell 启动的 Node.js 子进程不会随父进程一起退出，
        // 会成为孤儿继续占用端口，需要额外通过端口号强制清理进程树
        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
        if (port != null) {
            try {
                Thread.sleep(500); // 等待父进程退出
                if (isWindows) {
                    killPortProcessesOnWindows(port);
                } else {
                    killPortProcessesOnLinux(port);
                }
            } catch (Exception e) {
                log.warn("清理端口 {} 残留进程失败: {}", port, e.getMessage());
            }
        }
        previewProcesses.remove(siteId);
        previewPorts.remove(siteId);
        previewLogs.remove(siteId);
        result.put("success", true);
        result.put("message", "预览服务器已停止（端口: " + port + "）");
        return result;
    }

    /**
     * Linux 专用：优先使用 fuser，降级使用 lsof，杀掉占用指定端口的整个进程树
     * 解决 sh/pnpm 启动的 Node.js 子进程在父进程被销毁后成为孤儿、继续占用端口的问题
     */
    private void killPortProcessesOnLinux(int port) {
        // 方案1：fuser -k -KILL {port}/tcp（大多数 Linux 发行版内置）
        try {
            Process fuser = new ProcessBuilder("fuser", "-k", "-KILL", port + "/tcp")
                    .redirectErrorStream(true)
                    .start();
            boolean finished = fuser.waitFor(5, TimeUnit.SECONDS);
            if (finished) {
                log.info("fuser -k 已清理端口 {} 的进程", port);
                return;
            }
        } catch (Exception e) {
            log.debug("fuser 不可用，尝试 lsof: {}", e.getMessage());
        }
        // 方案2：lsof -ti:{port} 获取 PID，再 kill -9
        try {
            Process lsof = new ProcessBuilder("lsof", "-ti", ":" + port)
                    .redirectErrorStream(true)
                    .start();
            String pidsOutput;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(lsof.getInputStream()))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line.trim()).append(" ");
                pidsOutput = sb.toString().trim();
            }
            lsof.waitFor(5, TimeUnit.SECONDS);
            if (!pidsOutput.isEmpty()) {
                for (String pid : pidsOutput.split("\\s+")) {
                    if (pid.matches("\\d+")) {
                        new ProcessBuilder("kill", "-9", pid)
                                .redirectErrorStream(true)
                                .start()
                                .waitFor(3, TimeUnit.SECONDS);
                        log.info("kill -9 已终止占用端口 {} 的进程 PID={}", port, pid);
                    }
                }
                // 等待端口实际释放（最多 2 秒）
                for (int i = 0; i < 4; i++) {
                    Thread.sleep(500);
                    try (java.net.ServerSocket test = new java.net.ServerSocket(port)) {
                        log.info("端口 {} 已释放", port);
                        break;
                    } catch (Exception ignored) {}
                }
                return;
            }
        } catch (Exception e) {
            log.warn("lsof 清理端口 {} 失败: {}", port, e.getMessage());
        }
        // 方案3：pkill -f "next dev" / "node.*next"
        try {
            new ProcessBuilder("pkill", "-f", "next dev")
                    .redirectErrorStream(true).start().waitFor(3, TimeUnit.SECONDS);
            log.info("pkill next dev 执行完毕（端口 {}）", port);
            Thread.sleep(500);
        } catch (Exception e) {
            log.warn("pkill 清理端口 {} 失败: {}", port, e.getMessage());
        }
    }

    /**
     * Windows 专用：通过 netstat 找到监听指定端口的进程 PID，并用 taskkill /F 杀掉
     * 解决 cmd /c 启动的 Node.js 子进程在父进程被销毁后成为孤儿、继续占用端口的问题
     */
    private void killPortProcessesOnWindows(int port) throws Exception {
        // netstat -ano 获取所有 TCP 连接
        Process netstat = new ProcessBuilder("netstat", "-ano")
                .redirectErrorStream(true)
                .start();
        String output;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(netstat.getInputStream()))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line).append("\n");
            output = sb.toString();
        }
        netstat.waitFor(5, TimeUnit.SECONDS);

        // 解析 LISTENING 行，找到本端口对应的 PID
        String portSuffix = ":" + port + " ";
        Set<String> pids = new HashSet<>();
        for (String line : output.split("\n")) {
            String upper = line.toUpperCase();
            if (upper.contains("LISTENING") && line.contains(portSuffix)) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length > 0) {
                    String pid = parts[parts.length - 1].trim();
                    if (pid.matches("\\d+") && !"0".equals(pid)) {
                        pids.add(pid);
                    }
                }
            }
        }

        // 逐一强制终止
        for (String pid : pids) {
            try {
                Process killer = new ProcessBuilder("taskkill", "/F", "/T", "/PID", pid)
                        .redirectErrorStream(true)
                        .start();
                killer.waitFor(3, TimeUnit.SECONDS);
                log.info("已终止占用端口 {} 的进程 PID={}", port, pid);
            } catch (Exception e) {
                log.warn("taskkill PID={} 失败: {}", pid, e.getMessage());
            }
        }
    }

    @Override
    public Integer getPreviewPort(Long siteId) {
        Process process = previewProcesses.get(siteId);
        if (process == null || !process.isAlive()) return null;
        return previewPorts.get(siteId);
    }

    @Override
    public Map<String, Object> getPreviewStatus(Long siteId) {
        Map<String, Object> result = new HashMap<>();
        Process process = previewProcesses.get(siteId);
        boolean running = process != null && process.isAlive();
        Integer port = previewPorts.get(siteId);
        result.put("running", running);
        result.put("port", port);
        // 返回后端代理路径（浏览器可访问），而非服务器本地地址 localhost:port
        result.put("previewUrl", running ? "/gamebox/code-manage/" + siteId + "/preview/proxy" : null);
        StringBuilder logBuf = previewLogs.get(siteId);
        if (logBuf != null) {
            String logs = logBuf.toString();
            // 返回最后2000个字符
            if (logs.length() > 2000) {
                result.put("log", "...\n" + logs.substring(logs.length() - 2000));
            } else {
                result.put("log", logs);
            }
        }
        return result;
    }

    /**
     * 查找可用端口
     */
    private int findAvailablePort(int startPort, int endPort) {
        for (int port = startPort; port <= endPort; port++) {
            // 先检查previewPorts中是否已被占用
            if (previewPorts.containsValue(port)) continue;
            try (java.net.ServerSocket socket = new java.net.ServerSocket(port)) {
                return port;
            } catch (IOException e) {
                // 端口被占用，尝试下一个
            }
        }
        return startPort; // 兜底返回
    }

    // ===================== 文件管理 =====================

    @Override
    public Map<String, Object> listFiles(Long siteId, String path) {
        Map<String, Object> result = new HashMap<>();
        try {
            GbSite site = gbSiteMapper.selectGbSiteById(siteId);
            if (site == null) { result.put("success", false); result.put("message", "网站不存在"); return result; }
            String repoDir = getRepoDir(site.getId(), site.getCode());
            // 规范化路径：防止路径穿越
            String safePath = (path == null || path.isEmpty() || path.equals("/")) ? "" : path.replace("\\", "/").replaceAll("^\\.+/", "").replaceAll("/\\.+/", "/");
            java.nio.file.Path baseDir = java.nio.file.Paths.get(repoDir).normalize().toAbsolutePath();
            java.nio.file.Path targetDir = baseDir.resolve(safePath).normalize().toAbsolutePath();
            if (!targetDir.startsWith(baseDir)) {
                result.put("success", false); result.put("message", "非法路径"); return result;
            }
            if (!Files.exists(targetDir) || !Files.isDirectory(targetDir)) {
                result.put("success", false); result.put("message", "目录不存在"); return result;
            }
            List<Map<String, Object>> files = new ArrayList<>();
            try (DirectoryStream<java.nio.file.Path> stream = Files.newDirectoryStream(targetDir)) {
                for (java.nio.file.Path entry : stream) {
                    Map<String, Object> fileInfo = new HashMap<>();
                    String name = entry.getFileName().toString();
                    // 过滤 .git 目录和 node_modules
                    if (name.equals(".git") || name.equals("node_modules") || name.equals(".next")) continue;
                    fileInfo.put("name", name);
                    fileInfo.put("type", Files.isDirectory(entry) ? "directory" : "file");
                    if (!Files.isDirectory(entry)) {
                        fileInfo.put("size", Files.size(entry));
                        // 获取文件扩展名
                        int dotIdx = name.lastIndexOf('.');
                        fileInfo.put("ext", dotIdx >= 0 ? name.substring(dotIdx + 1) : "");
                    }
                    String relativePath = safePath.isEmpty() ? name : safePath + "/" + name;
                    fileInfo.put("path", relativePath);
                    files.add(fileInfo);
                }
            }
            // 排序：目录优先，然后按名称
            files.sort((a, b) -> {
                boolean aDir = "directory".equals(a.get("type"));
                boolean bDir = "directory".equals(b.get("type"));
                if (aDir != bDir) return aDir ? -1 : 1;
                return ((String) a.get("name")).compareToIgnoreCase((String) b.get("name"));
            });
            result.put("success", true);
            result.put("files", files);
            result.put("path", safePath);
        } catch (Exception e) {
            log.error("列出文件失败", e);
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @Override
    public Map<String, Object> installDependencies(Long siteId) {
        Map<String, Object> result = new HashMap<>();
        GbSite site = gbSiteMapper.selectGbSiteById(siteId);
        if (site == null) { result.put("success", false); result.put("message", "网站不存在"); return result; }
        String repoDir = getRepoDir(site.getId(), site.getCode());
        if (!new File(repoDir, "package.json").exists()) {
            result.put("success", false);
            result.put("message", "package.json 不存在，请先拉取代码");
            return result;
        }

        // 如果已有安装正在运行，返回提示
        if (Boolean.TRUE.equals(installRunning.get(siteId))) {
            result.put("success", false);
            result.put("message", "依赖安装正在运行中，请稍候");
            return result;
        }

        // 初始化日志缓冲区并标记为运行中
        StringBuilder logBuilder = new StringBuilder();
        installLogs.put(siteId, logBuilder);
        installRunning.put(siteId, true);
        installSuccess.remove(siteId);

        final GbSiteCodeConfig cfg = codeConfigMapper.selectBySiteId(siteId);

        // 在后台线程中执行实际安装
        Thread installThread = new Thread(() -> {
            boolean success = false;
            try {
                File repoDirFile = new File(repoDir);
                String pkgManager = detectPackageManager(repoDirFile);
                pkgManager = ensurePackageManager(repoDirFile, pkgManager, logBuilder);
                logBuilder.append("使用包管理器: ").append(pkgManager).append("\n");

                String[] installCmd = null;
                if (cfg != null && cfg.getDeployConfig() != null && !cfg.getDeployConfig().isEmpty()) {
                    try {
                        com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();
                        com.fasterxml.jackson.databind.JsonNode node = om.readTree(cfg.getDeployConfig());
                        com.fasterxml.jackson.databind.JsonNode icNode = node.get("installCommand");
                        if (icNode != null && !icNode.asText().trim().isEmpty()) {
                            installCmd = icNode.asText().trim().split("\\s+");
                        }
                    } catch (Exception ignored) {}
                }
                if (installCmd == null || installCmd.length == 0) {
                    installCmd = new String[]{pkgManager, "install"};
                }
                logBuilder.append("执行安装: ").append(String.join(" ", installCmd)).append("\n");
                success = runCommand(repoDirFile, logBuilder, 600, installCmd);
                logBuilder.append(success ? "\n✅ 依赖安装完成\n" : "\n❌ 依赖安装失败\n");

                codeConfigMapper.updateDeployStatus(siteId,
                        cfg != null ? cfg.getDeployStatus() : null,
                        cfg != null ? cfg.getDeployUrl() : null,
                        new Date(),
                        logBuilder.toString());
            } catch (Exception e) {
                log.error("安装依赖失败", e);
                logBuilder.append("\n❌ 安装异常: ").append(e.getMessage()).append("\n");
            } finally {
                installSuccess.put(siteId, success);
                installRunning.put(siteId, false);
                scheduleLogCleanup(siteId, installLogs, installRunning, installSuccess);
            }
        });
        installThread.setDaemon(true);
        installThread.setName("install-" + siteId);
        installThread.start();

        result.put("success", true);
        result.put("message", "依赖安装已开始，请通过日志接口轮询进度");
        result.put("installing", true);
        return result;
    }

    @Override
    public Map<String, Object> getInstallStatus(Long siteId) {
        Map<String, Object> result = new HashMap<>();
        Boolean running = installRunning.get(siteId);
        Boolean succeeded = installSuccess.get(siteId);
        StringBuilder logBuf = installLogs.get(siteId);

        result.put("running", Boolean.TRUE.equals(running));
        if (!Boolean.TRUE.equals(running) && succeeded != null) {
            result.put("done", true);
            result.put("success", succeeded);
        } else {
            result.put("done", false);
        }
        result.put("log", logBuf != null ? logBuf.toString() : "");
        return result;
    }

    @Override
    public Map<String, Object> readFile(Long siteId, String path) {
        Map<String, Object> result = new HashMap<>();
        try {
            GbSite site = gbSiteMapper.selectGbSiteById(siteId);
            if (site == null) { result.put("success", false); result.put("message", "网站不存在"); return result; }
            String repoDir = getRepoDir(site.getId(), site.getCode());
            log.info("[readFile] siteId={}, repoDir={}, path={}", siteId, repoDir, path);
            java.nio.file.Path baseDir = java.nio.file.Paths.get(repoDir).normalize().toAbsolutePath();
            java.nio.file.Path filePath = baseDir.resolve(path.replace("\\", "/")).normalize().toAbsolutePath();
            log.info("[readFile] baseDir={}, filePath={}, exists={}", baseDir, filePath, Files.exists(filePath));
            if (!filePath.startsWith(baseDir)) {
                result.put("success", false); result.put("message", "非法路径"); return result;
            }
            if (!Files.exists(filePath) || Files.isDirectory(filePath)) {
                log.warn("[readFile] 文件不存在: {}", filePath);
                result.put("success", false); result.put("message", "文件不存在"); return result;
            }
            long fileSize = Files.size(filePath);
            if (fileSize > 2 * 1024 * 1024) { // 超过2MB不允许在线编辑
                result.put("success", false); result.put("message", "文件过大（>2MB），请使用其他工具编辑"); return result;
            }
            String content = new String(Files.readAllBytes(filePath), java.nio.charset.StandardCharsets.UTF_8);
            log.info("[readFile] 成功读取, size={}, contentLen={}", fileSize, content.length());
            result.put("success", true);
            result.put("content", content);
            result.put("path", path);
            result.put("size", fileSize);
        } catch (Exception e) {
            log.error("读取文件失败: {}", path, e);
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @Override
    public Map<String, Object> saveFile(Long siteId, String path, String content) {
        Map<String, Object> result = new HashMap<>();
        try {
            GbSite site = gbSiteMapper.selectGbSiteById(siteId);
            if (site == null) { result.put("success", false); result.put("message", "网站不存在"); return result; }
            String repoDir = getRepoDir(site.getId(), site.getCode());
            java.nio.file.Path baseDir = java.nio.file.Paths.get(repoDir).normalize().toAbsolutePath();
            java.nio.file.Path filePath = baseDir.resolve(path.replace("\\", "/")).normalize().toAbsolutePath();
            if (!filePath.startsWith(baseDir)) {
                result.put("success", false); result.put("message", "非法路径"); return result;
            }
            // 确保父目录存在
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, content.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            result.put("success", true);
            result.put("message", "文件保存成功");
        } catch (Exception e) {
            log.error("保存文件失败: {}", path, e);
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    // ===================== 文件管理增强 =====================

    /** 安全校验：解析相对路径并确认在 baseDir 之内 */
    private java.nio.file.Path safeResolve(java.nio.file.Path baseDir, String relPath) throws IOException {
        if (relPath == null || relPath.isEmpty()) return baseDir;
        java.nio.file.Path resolved = baseDir.resolve(relPath.replace("\\", "/")).normalize().toAbsolutePath();
        if (!resolved.startsWith(baseDir)) throw new IOException("非法路径: " + relPath);
        return resolved;
    }

    @Override
    public Map<String, Object> deleteFile(Long siteId, String path) {
        Map<String, Object> result = new HashMap<>();
        try {
            GbSite site = gbSiteMapper.selectGbSiteById(siteId);
            if (site == null) { result.put("success", false); result.put("message", "网站不存在"); return result; }
            java.nio.file.Path baseDir = java.nio.file.Paths.get(getRepoDir(site.getId(), site.getCode())).normalize().toAbsolutePath();
            java.nio.file.Path target = safeResolve(baseDir, path);
            if (target.equals(baseDir)) { result.put("success", false); result.put("message", "不能删除仓库根目录"); return result; }
            if (!Files.exists(target)) { result.put("success", false); result.put("message", "文件不存在"); return result; }
            if (Files.isDirectory(target)) {
                Files.walk(target).sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
                    try { Files.delete(p); } catch (IOException ignored) {}
                });
            } else {
                Files.delete(target);
            }
            result.put("success", true);
            result.put("message", "删除成功");
        } catch (Exception e) {
            log.error("删除文件失败: {}", path, e);
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @Override
    public Map<String, Object> renameFile(Long siteId, String oldPath, String newPath) {
        Map<String, Object> result = new HashMap<>();
        try {
            GbSite site = gbSiteMapper.selectGbSiteById(siteId);
            if (site == null) { result.put("success", false); result.put("message", "网站不存在"); return result; }
            java.nio.file.Path baseDir = java.nio.file.Paths.get(getRepoDir(site.getId(), site.getCode())).normalize().toAbsolutePath();
            java.nio.file.Path src = safeResolve(baseDir, oldPath);
            java.nio.file.Path dst = safeResolve(baseDir, newPath);
            if (!Files.exists(src)) { result.put("success", false); result.put("message", "源文件不存在"); return result; }
            if (Files.exists(dst)) { result.put("success", false); result.put("message", "目标路径已存在"); return result; }
            Files.createDirectories(dst.getParent());
            Files.move(src, dst, StandardCopyOption.REPLACE_EXISTING);
            result.put("success", true);
            result.put("message", "重命名成功");
        } catch (Exception e) {
            log.error("重命名文件失败: {} -> {}", oldPath, newPath, e);
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @Override
    public Map<String, Object> copyFile(Long siteId, String sourcePath, String destPath) {
        Map<String, Object> result = new HashMap<>();
        try {
            GbSite site = gbSiteMapper.selectGbSiteById(siteId);
            if (site == null) { result.put("success", false); result.put("message", "网站不存在"); return result; }
            java.nio.file.Path baseDir = java.nio.file.Paths.get(getRepoDir(site.getId(), site.getCode())).normalize().toAbsolutePath();
            java.nio.file.Path src = safeResolve(baseDir, sourcePath);
            java.nio.file.Path dst = safeResolve(baseDir, destPath);
            if (!Files.exists(src)) { result.put("success", false); result.put("message", "源文件不存在"); return result; }
            if (Files.isDirectory(src)) {
                copyDirectoryRecursively(src, dst);
            } else {
                Files.createDirectories(dst.getParent());
                Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING);
            }
            result.put("success", true);
            result.put("message", "复制成功");
        } catch (Exception e) {
            log.error("复制文件失败: {} -> {}", sourcePath, destPath, e);
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    private void copyDirectoryRecursively(java.nio.file.Path src, java.nio.file.Path dst) throws IOException {
        Files.walk(src).forEach(srcPath -> {
            java.nio.file.Path dstPath = dst.resolve(src.relativize(srcPath));
            try {
                if (Files.isDirectory(srcPath)) {
                    Files.createDirectories(dstPath);
                } else {
                    Files.createDirectories(dstPath.getParent());
                    Files.copy(srcPath, dstPath, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public Map<String, Object> createFile(Long siteId, String path, boolean isDirectory) {
        Map<String, Object> result = new HashMap<>();
        try {
            GbSite site = gbSiteMapper.selectGbSiteById(siteId);
            if (site == null) { result.put("success", false); result.put("message", "网站不存在"); return result; }
            java.nio.file.Path baseDir = java.nio.file.Paths.get(getRepoDir(site.getId(), site.getCode())).normalize().toAbsolutePath();
            java.nio.file.Path target = safeResolve(baseDir, path);
            if (Files.exists(target)) { result.put("success", false); result.put("message", "已存在同名文件/目录"); return result; }
            if (isDirectory) {
                Files.createDirectories(target);
            } else {
                Files.createDirectories(target.getParent());
                Files.createFile(target);
            }
            result.put("success", true);
            result.put("message", isDirectory ? "目录创建成功" : "文件创建成功");
        } catch (Exception e) {
            log.error("创建文件失败: {}", path, e);
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @Override
    public Map<String, Object> uploadFile(Long siteId, String dirPath, String fileName, byte[] content) {
        Map<String, Object> result = new HashMap<>();
        try {
            GbSite site = gbSiteMapper.selectGbSiteById(siteId);
            if (site == null) { result.put("success", false); result.put("message", "网站不存在"); return result; }
            java.nio.file.Path baseDir = java.nio.file.Paths.get(getRepoDir(site.getId(), site.getCode())).normalize().toAbsolutePath();
            java.nio.file.Path dir = safeResolve(baseDir, dirPath);
            // 文件名安全处理：只取名称部分
            String safeName = java.nio.file.Paths.get(fileName).getFileName().toString();
            java.nio.file.Path filePath = dir.resolve(safeName).normalize().toAbsolutePath();
            if (!filePath.startsWith(baseDir)) { result.put("success", false); result.put("message", "非法路径"); return result; }
            Files.createDirectories(dir);
            Files.write(filePath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            String relativePath = baseDir.relativize(filePath).toString().replace("\\", "/");
            result.put("success", true);
            result.put("message", "上传成功");
            result.put("path", relativePath);
        } catch (Exception e) {
            log.error("上传文件失败", e);
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    // ===================== Git 增强 =====================

    @Override
    public Map<String, Object> getGitStatus(Long siteId) {
        Map<String, Object> result = new HashMap<>();
        try {
            GbSite site = gbSiteMapper.selectGbSiteById(siteId);
            if (site == null) { result.put("success", false); result.put("message", "网站不存在"); return result; }
            String repoDir = getRepoDir(site.getId(), site.getCode());
            File repoDirFile = new File(repoDir);
            if (!new File(repoDir, ".git").exists()) {
                result.put("success", false); result.put("message", "仓库不存在"); return result;
            }
            // 检测是否处于合并中状态（.git/MERGE_HEAD 存在）
            boolean merging = new File(repoDir, ".git/MERGE_HEAD").exists();
            result.put("merging", merging);

            StringBuilder sb = new StringBuilder();
            runCommand(repoDirFile, sb, 15, "git", "status", "--porcelain");

            List<Map<String, Object>> staged   = new ArrayList<>();
            List<Map<String, Object>> unstaged = new ArrayList<>();
            List<String> conflictFiles = new ArrayList<>();

            for (String line : sb.toString().split("\n")) {
                if (line.length() < 3) continue;
                if (line.startsWith("[退出码]") || line.startsWith("[超时]") || line.startsWith("[执行命令]")) continue;
                char X = line.charAt(0);  // index (staged) status
                char Y = line.charAt(1);  // work-tree (unstaged) status
                String filePath = line.substring(3).trim();
                // renamed: "R  old -> new" 格式
                if ((X == 'R' || X == 'C') && filePath.contains(" -> ")) {
                    String[] parts = filePath.split(" -> ", 2);
                    if (parts.length == 2) filePath = parts[1].trim();
                }
                // 合并冲突：XY 为 UU / AA / DD / AU / UA / DU / UD
                boolean isConflict = (X == 'U' || Y == 'U') || (X == 'A' && Y == 'A') || (X == 'D' && Y == 'D');
                if (isConflict) {
                    conflictFiles.add(filePath);
                    // 冲突文件同时放入 unstaged 以便显示
                    Map<String, Object> ci = new HashMap<>();
                    ci.put("path", filePath);
                    ci.put("statusType", "conflict");
                    ci.put("area", "unstaged");
                    unstaged.add(ci);
                    continue;
                }
                // 暂存区（X 非空格且非?）
                if (X != ' ' && X != '?') {
                    Map<String, Object> item = new HashMap<>();
                    item.put("path", filePath);
                    item.put("statusType", charToType(X));
                    item.put("area", "staged");
                    staged.add(item);
                }
                // 工作区（Y 非空格，包含未跟踪 ??）
                if (Y != ' ') {
                    Map<String, Object> item = new HashMap<>();
                    item.put("path", filePath);
                    item.put("statusType", Y == '?' ? "untracked" : charToType(Y));
                    item.put("area", "unstaged");
                    unstaged.add(item);
                }
            }
            result.put("success", true);
            result.put("staged",   staged);
            result.put("unstaged", unstaged);
            result.put("count",    staged.size() + unstaged.size());
            if (merging) result.put("conflictFiles", conflictFiles);
        } catch (Exception e) {
            log.error("获取git状态失败", e);
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    private String charToType(char c) {
        switch (c) {
            case 'A': return "added";
            case 'M': return "modified";
            case 'D': return "deleted";
            case 'R': return "renamed";
            case 'C': return "copied";
            case 'U': return "conflict";
            default:  return "other";
        }
    }

    @Override
    public Map<String, Object> gitStage(Long siteId, String path) {
        Map<String, Object> result = new HashMap<>();
        try {
            GbSite site = gbSiteMapper.selectGbSiteById(siteId);
            if (site == null) { result.put("success", false); result.put("message", "网站不存在"); return result; }
            File repoDirFile = new File(getRepoDir(site.getId(), site.getCode()));
            StringBuilder sb = new StringBuilder();
            boolean ok = (path == null || path.isEmpty())
                    ? runCommand(repoDirFile, sb, 30, "git", "add", ".")
                    : runCommand(repoDirFile, sb, 15, "git", "add", path.replace('\\', '/'));
            result.put("success", ok);
            if (!ok) result.put("message", sb.toString());
        } catch (Exception e) {
            result.put("success", false); result.put("message", e.getMessage());
        }
        return result;
    }

    @Override
    public Map<String, Object> gitUnstage(Long siteId, String path) {
        Map<String, Object> result = new HashMap<>();
        try {
            GbSite site = gbSiteMapper.selectGbSiteById(siteId);
            if (site == null) { result.put("success", false); result.put("message", "网站不存在"); return result; }
            File repoDirFile = new File(getRepoDir(site.getId(), site.getCode()));
            StringBuilder sb = new StringBuilder();
            boolean ok = (path == null || path.isEmpty())
                    ? runCommand(repoDirFile, sb, 15, "git", "reset", "HEAD")
                    : runCommand(repoDirFile, sb, 15, "git", "restore", "--staged", path.replace('\\', '/'));
            // 旧版 git 不支持 restore --staged，回退到 reset HEAD
            if (!ok && path != null && !path.isEmpty()) {
                sb.setLength(0);
                ok = runCommand(repoDirFile, sb, 15, "git", "reset", "HEAD", path.replace('\\', '/'));
            }
            result.put("success", ok);
            if (!ok) result.put("message", sb.toString());
        } catch (Exception e) {
            result.put("success", false); result.put("message", e.getMessage());
        }
        return result;
    }

    @Override
    public Map<String, Object> gitCommit(Long siteId, String message) {
        Map<String, Object> result = new HashMap<>();
        try {
            GbSite site = gbSiteMapper.selectGbSiteById(siteId);
            if (site == null) { result.put("success", false); result.put("message", "网站不存在"); return result; }
            File repoDirFile = new File(getRepoDir(site.getId(), site.getCode()));
            runCommand(repoDirFile, new StringBuilder(), 10, "git", "config", "user.email", "codebox@gamebox.local");
            runCommand(repoDirFile, new StringBuilder(), 10, "git", "config", "user.name", "GameBox");
            StringBuilder sb = new StringBuilder();
            boolean merging = new File(getRepoDir(site.getId(), site.getCode()), ".git/MERGE_HEAD").exists();
            boolean ok;
            if (merging) {
                // 合并提交：优先使用用户输入的消息，否则用默认合并消息（不传 -m 让 git 自己生成）
                if (message != null && !message.trim().isEmpty()) {
                    ok = runCommand(repoDirFile, sb, 30, "git", "commit", "-m", message.trim());
                } else {
                    ok = runCommand(repoDirFile, sb, 30, "git", "commit", "--no-edit");
                }
            } else {
                String msg = (message != null && !message.trim().isEmpty()) ? message.trim() : "Update via Code Manager";
                ok = runCommand(repoDirFile, sb, 30, "git", "commit", "-m", msg);
            }
            String log = sb.toString();
            // git commit 在暂存区为空时返回退出码 1，但这属于正常情况
            if (!ok && (log.contains("nothing to commit") || log.contains("nothing added to commit"))) {
                result.put("success", true);
                result.put("nothingToCommit", true);
                result.put("message", "暂存区为空，无需提交");
            } else if (!ok && merging && (log.contains("CONFLICT") || log.contains("unmerged"))) {
                result.put("success", false);
                result.put("hasConflict", true);
                result.put("message", "仍有冲突文件未解决，请先解决所有冲突并暂存后再提交");
            } else {
                result.put("success", ok);
                result.put("message", ok ? (merging ? "合并提交成功" : null) : (log.isEmpty() ? "提交失败，可能暂存区为空" : log));
            }
            result.put("log", log);
        } catch (Exception e) {
            result.put("success", false); result.put("message", e.getMessage());
        }
        return result;
    }

    @Override
    public Map<String, Object> gitPushOnly(Long siteId) {
        Map<String, Object> result = new HashMap<>();
        try {
            GbSite site = gbSiteMapper.selectGbSiteById(siteId);
            if (site == null) { result.put("success", false); result.put("message", "网站不存在"); return result; }
            GbSiteCodeConfig cfg = codeConfigMapper.selectBySiteId(siteId);
            if (cfg == null || cfg.getGitRepoUrl() == null || cfg.getGitRepoUrl().isEmpty()) {
                result.put("success", false); result.put("message", "请先配置Git仓库URL"); return result;
            }
            File repoDirFile = new File(getRepoDir(site.getId(), site.getCode()));
            runCommand(repoDirFile, new StringBuilder(), 10, "git", "remote", "set-url", "origin", buildAuthenticatedUrl(cfg));
            // 取当前实际分支，而非配置中的部署分支
            StringBuilder branchSb = new StringBuilder();
            runCommand(repoDirFile, branchSb, 10, "git", "branch", "--show-current");
            String currentBranch = "";
            for (String l : branchSb.toString().split("\n")) {
                String t = l.trim();
                if (!t.isEmpty() && !t.startsWith("[")) { currentBranch = t; break; }
            }
            if (currentBranch.isEmpty()) {
                // 回退到配置分支
                currentBranch = cfg.getGitBranch() != null ? cfg.getGitBranch() : "main";
            }
            StringBuilder sb = new StringBuilder();
            // 尝试普通推送（已有追踪分支）
            boolean ok = runCommand(repoDirFile, sb, 120, "git", "push", "origin", currentBranch);
            if (!ok && (sb.toString().contains("has no upstream") || sb.toString().contains("no upstream branch"))) {
                // 新分支尚未设置 upstream，加 --set-upstream
                sb.setLength(0);
                ok = runCommand(repoDirFile, sb, 120, "git", "push", "--set-upstream", "origin", currentBranch);
            }
            result.put("success", ok);
            result.put("log", sb.toString());
            result.put("branch", currentBranch);
            result.put("message", ok ? "推送成功 → " + currentBranch : "推送失败");
            if (ok) {
                // 推送成功后将 setupStatus 更新为 code_pulled（代码已就绪）
                GbSite updateSite = new GbSite();
                updateSite.setId(siteId);
                updateSite.setSetupStatus("code_pulled");
                gbSiteMapper.updateGbSite(updateSite);
            }
        } catch (Exception e) {
            result.put("success", false); result.put("message", e.getMessage());
        }
        return result;
    }

    @Override
    public Map<String, Object> gitDiscard(Long siteId, String path, boolean untracked) {
        Map<String, Object> result = new HashMap<>();
        try {
            GbSite site = gbSiteMapper.selectGbSiteById(siteId);
            if (site == null) { result.put("success", false); result.put("message", "网站不存在"); return result; }
            File repoDirFile = new File(getRepoDir(site.getId(), site.getCode()));
            if (path == null || path.trim().isEmpty()) {
                result.put("success", false); result.put("message", "路径不能为空"); return result;
            }
            String filePath = path.replace('\\', '/');
            StringBuilder sb = new StringBuilder();
            boolean ok;
            if (untracked) {
                // 未跟踪文件：git clean -f <path>
                ok = runCommand(repoDirFile, sb, 15, "git", "clean", "-f", filePath);
            } else {
                // 已跟踪文件：git checkout -- <path>  (尝试 restore 先)
                ok = runCommand(repoDirFile, sb, 15, "git", "restore", filePath);
                if (!ok) {
                    sb.setLength(0);
                    ok = runCommand(repoDirFile, sb, 15, "git", "checkout", "--", filePath);
                }
            }
            result.put("success", ok);
            if (!ok) result.put("message", sb.toString().trim());
        } catch (Exception e) {
            result.put("success", false); result.put("message", e.getMessage());
        }
        return result;
    }

    @Override
    public Map<String, Object> gitListBranches(Long siteId, boolean includeRemote) {
        Map<String, Object> result = new HashMap<>();
        try {
            GbSite site = gbSiteMapper.selectGbSiteById(siteId);
            if (site == null) { result.put("success", false); result.put("message", "网站不存在"); return result; }
            File repoDirFile = new File(getRepoDir(site.getId(), site.getCode()));
            if (!new File(repoDirFile, ".git").exists()) {
                result.put("success", false); result.put("message", "仓库不存在"); return result;
            }
            // 本地分支
            StringBuilder sb = new StringBuilder();
            runCommand(repoDirFile, sb, 10, "git", "branch");
            List<Map<String, Object>> branches = new ArrayList<>();
            Set<String> localNames = new HashSet<>();
            String currentBranch = "";
            for (String line : sb.toString().split("\n")) {
                if (line.length() < 2) continue;
                if (line.startsWith("[退出码]") || line.startsWith("[超时]") || line.startsWith("[执行命令]")) continue;
                boolean isCurrent = line.startsWith("*");
                String name = line.replaceFirst("^[*\\s]+", "").trim();
                if (name.isEmpty()) continue;
                Map<String, Object> item = new HashMap<>();
                item.put("name", name);
                item.put("current", isCurrent);
                branches.add(item);
                localNames.add(name);
                if (isCurrent) currentBranch = name;
            }
            // 远端分支：
            //   includeRemote=false → git branch -r（读本地 remote-tracking refs，无网络，fetch 后即是最新）
            //   includeRemote=true  → git ls-remote --heads origin（真正的网络请求，强制刷新）
            List<Map<String, Object>> remoteBranches = new ArrayList<>();
            if (includeRemote) {
                GbSiteCodeConfig cfg = codeConfigMapper.selectBySiteId(siteId);
                if (cfg != null && cfg.getGitRepoUrl() != null && !cfg.getGitRepoUrl().isEmpty()) {
                    runCommand(repoDirFile, new StringBuilder(), 10, "git", "remote", "set-url", "origin", buildAuthenticatedUrl(cfg));
                    StringBuilder sbr = new StringBuilder();
                    runCommand(repoDirFile, sbr, 30, "git", "ls-remote", "--heads", "origin");
                    for (String line : sbr.toString().split("\n")) {
                        if (line.length() < 2) continue;
                        if (line.startsWith("[退出码]") || line.startsWith("[超时]") || line.startsWith("[执行命令]")) continue;
                        String[] parts = line.split("\t");
                        if (parts.length < 2) continue;
                        String ref = parts[1].trim();
                        if (!ref.startsWith("refs/heads/")) continue;
                        String rname = ref.substring("refs/heads/".length());
                        if (rname.isEmpty()) continue;
                        Map<String, Object> ritem = new HashMap<>();
                        ritem.put("name", rname);
                        ritem.put("hasLocal", localNames.contains(rname));
                        remoteBranches.add(ritem);
                    }
                }
            } else {
                // 读本地 remote-tracking refs（origin/*），无需网络
                StringBuilder sbr = new StringBuilder();
                runCommand(repoDirFile, sbr, 10, "git", "branch", "-r");
                for (String line : sbr.toString().split("\n")) {
                    if (line.length() < 2) continue;
                    if (line.startsWith("[退出码]") || line.startsWith("[超时]") || line.startsWith("[执行命令]")) continue;
                    String name = line.trim();
                    if (name.isEmpty() || name.contains("->")) continue; // 跳过 HEAD 指向行
                    // 去掉 "origin/" 前缀
                    String rname = name.startsWith("origin/") ? name.substring("origin/".length()) : name;
                    if (rname.isEmpty()) continue;
                    Map<String, Object> ritem = new HashMap<>();
                    ritem.put("name", rname);
                    ritem.put("hasLocal", localNames.contains(rname));
                    remoteBranches.add(ritem);
                }
            }
            result.put("success", true);
            result.put("branches", branches);
            result.put("remoteBranches", remoteBranches);
            result.put("currentBranch", currentBranch);
        } catch (Exception e) {
            result.put("success", false); result.put("message", e.getMessage());
        }
        return result;
    }

    @Override
    public Map<String, Object> gitFetch(Long siteId) {
        Map<String, Object> result = new HashMap<>();
        try {
            GbSite site = gbSiteMapper.selectGbSiteById(siteId);
            if (site == null) { result.put("success", false); result.put("message", "网站不存在"); return result; }
            GbSiteCodeConfig cfg = codeConfigMapper.selectBySiteId(siteId);
            File repoDirFile = new File(getRepoDir(site.getId(), site.getCode()));
            if (cfg != null && cfg.getGitRepoUrl() != null && !cfg.getGitRepoUrl().isEmpty()) {
                runCommand(repoDirFile, new StringBuilder(), 10, "git", "remote", "set-url", "origin", buildAuthenticatedUrl(cfg));
            }
            // 升级 fetch refspec，确保单分支浅克隆也能追踪全部远端分支
            runCommand(repoDirFile, new StringBuilder(), 10, "git", "remote", "set-branches", "origin", "*");
            // 检测是否是浅克隆（shallow clone）
            boolean isShallow = new File(repoDirFile, ".git/shallow").exists();
            StringBuilder sbFetch = new StringBuilder();
            boolean ok;
            boolean didUnshallow = false;
            if (isShallow) {
                // 浅克隆：fetch --unshallow 获取完整历史，同时 --prune 清理已删除的远端追踪分支
                // 注意：unshallow 需要下载完整历史，可能较慢
                ok = runCommand(repoDirFile, sbFetch, 300, "git", "fetch", "--unshallow", "--prune");
                if (ok) {
                    didUnshallow = true;
                } else {
                    // unshallow 失败时降级为普通 fetch（避免报错阻断流程）
                    sbFetch = new StringBuilder();
                    ok = runCommand(repoDirFile, sbFetch, 60, "git", "fetch", "--prune");
                }
            } else {
                ok = runCommand(repoDirFile, sbFetch, 60, "git", "fetch", "--prune");
            }
            if (!ok) {
                result.put("success", false);
                result.put("message", sbFetch.toString().trim());
                return result;
            }
            result.put("success", true);
            result.put("message", didUnshallow ? "已同步（已转换为完整仓库，可查看全部提交历史）。" : "已同步。");
            result.put("created", new ArrayList<>());
            result.put("unshallowed", didUnshallow);
        } catch (Exception e) {
            result.put("success", false); result.put("message", e.getMessage());
        }
        return result;
    }

    @Override
    public Map<String, Object> gitCheckoutBranch(Long siteId, String branch) {
        Map<String, Object> result = new HashMap<>();
        try {
            GbSite site = gbSiteMapper.selectGbSiteById(siteId);
            if (site == null) { result.put("success", false); result.put("message", "网站不存在"); return result; }
            if (branch == null || branch.trim().isEmpty()) {
                result.put("success", false); result.put("message", "分支名不能为空"); return result;
            }
            File repoDirFile = new File(getRepoDir(site.getId(), site.getCode()));
            String b = branch.trim();
            StringBuilder sb = new StringBuilder();
            // 先尝试直接切换（本地已有该分支）
            boolean ok = runCommand(repoDirFile, sb, 30, "git", "checkout", b);
            if (!ok) {
                // 本地没有该分支：先 fetch 建立 origin/<b> 追踪引用，再从追踪引用建本地分支
                GbSiteCodeConfig cfg = codeConfigMapper.selectBySiteId(siteId);
                if (cfg != null && cfg.getGitRepoUrl() != null && !cfg.getGitRepoUrl().isEmpty()) {
                    runCommand(repoDirFile, new StringBuilder(), 10, "git", "remote", "set-url", "origin", buildAuthenticatedUrl(cfg));
                }
                // fetch 该分支到本地追踪引用 refs/remotes/origin/<b>
                runCommand(repoDirFile, new StringBuilder(), 30, "git", "fetch", "origin",
                        b + ":refs/remotes/origin/" + b);
                sb = new StringBuilder();
                ok = runCommand(repoDirFile, sb, 30, "git", "checkout", "-b", b, "origin/" + b);
            }
            result.put("success", ok);
            result.put("message", ok ? "已切换到分支 " + b : sb.toString().trim());
            if (ok) result.put("branch", b);
        } catch (Exception e) {
            result.put("success", false); result.put("message", e.getMessage());
        }
        return result;
    }

    @Override
    public Map<String, Object> gitCreateBranch(Long siteId, String branch) {
        Map<String, Object> result = new HashMap<>();
        try {
            GbSite site = gbSiteMapper.selectGbSiteById(siteId);
            if (site == null) { result.put("success", false); result.put("message", "网站不存在"); return result; }
            if (branch == null || branch.trim().isEmpty()) {
                result.put("success", false); result.put("message", "分支名不能为空"); return result;
            }
            File repoDirFile = new File(getRepoDir(site.getId(), site.getCode()));
            StringBuilder sb = new StringBuilder();
            boolean ok = runCommand(repoDirFile, sb, 30, "git", "checkout", "-b", branch.trim());
            result.put("success", ok);
            result.put("message", ok ? "分支 " + branch.trim() + " 已在本地建立" : sb.toString().trim());
            if (ok) result.put("branch", branch.trim());
        } catch (Exception e) {
            result.put("success", false); result.put("message", e.getMessage());
        }
        return result;
    }

    @Override
    public Map<String, Object> gitDeleteBranch(Long siteId, String branch, boolean force) {
        Map<String, Object> result = new HashMap<>();
        try {
            GbSite site = gbSiteMapper.selectGbSiteById(siteId);
            if (site == null) { result.put("success", false); result.put("message", "网站不存在"); return result; }
            if (branch == null || branch.trim().isEmpty()) {
                result.put("success", false); result.put("message", "分支名不能为空"); return result;
            }
            File repoDirFile = new File(getRepoDir(site.getId(), site.getCode()));
            StringBuilder sb = new StringBuilder();
            String flag = force ? "-D" : "-d";
            boolean ok = runCommand(repoDirFile, sb, 15, "git", "branch", flag, branch.trim());
            result.put("success", ok);
            if (!ok) {
                String raw = sb.toString().trim();
                String msg;
                if (raw.contains("not fully merged")) {
                    msg = "分支「" + branch.trim() + "」存在未合并的提交，是否强制删除？";
                    result.put("notFullyMerged", true);
                } else if (raw.isEmpty()) {
                    msg = "删除分支失败";
                } else {
                    msg = raw;
                }
                result.put("message", msg);
            }
        } catch (Exception e) {
            result.put("success", false); result.put("message", e.getMessage());
        }
        return result;
    }

    @Override
    public Map<String, Object> gitDeleteRemoteBranch(Long siteId, String branch) {
        Map<String, Object> result = new HashMap<>();
        try {
            GbSite site = gbSiteMapper.selectGbSiteById(siteId);
            if (site == null) { result.put("success", false); result.put("message", "网站不存在"); return result; }
            if (branch == null || branch.trim().isEmpty()) {
                result.put("success", false); result.put("message", "分支名不能为空"); return result;
            }
            GbSiteCodeConfig cfg = codeConfigMapper.selectBySiteId(siteId);
            if (cfg == null || cfg.getGitRepoUrl() == null || cfg.getGitRepoUrl().isEmpty()) {
                result.put("success", false); result.put("message", "请先配置Git仓库URL"); return result;
            }
            File repoDirFile = new File(getRepoDir(site.getId(), site.getCode()));
            runCommand(repoDirFile, new StringBuilder(), 10, "git", "remote", "set-url", "origin", buildAuthenticatedUrl(cfg));
            StringBuilder sb = new StringBuilder();
            String b = branch.trim();
            boolean ok = runCommand(repoDirFile, sb, 60, "git", "push", "origin", "--delete", b);
            result.put("success", ok);
            if (ok) {
                result.put("message", "远端分支 origin/" + b + " 已删除");
            } else {
                String errOut = sb.toString();
                if (errOut.contains("remote ref does not exist") || errOut.contains("did not match any")) {
                    result.put("message", "远端分支 origin/" + b + " 不存在，无需删除");
                } else {
                    result.put("message", errOut.trim());
                }
            }
        } catch (Exception e) {
            result.put("success", false); result.put("message", e.getMessage());
        }
        return result;
    }

    @Override
    public Map<String, Object> gitPushBranch(Long siteId, String branch) {
        Map<String, Object> result = new HashMap<>();
        try {
            GbSite site = gbSiteMapper.selectGbSiteById(siteId);
            if (site == null) { result.put("success", false); result.put("message", "网站不存在"); return result; }
            GbSiteCodeConfig cfg = codeConfigMapper.selectBySiteId(siteId);
            if (cfg == null || cfg.getGitRepoUrl() == null || cfg.getGitRepoUrl().isEmpty()) {
                result.put("success", false); result.put("message", "请先配置Git仓库URL"); return result;
            }
            File repoDirFile = new File(getRepoDir(site.getId(), site.getCode()));
            runCommand(repoDirFile, new StringBuilder(), 10, "git", "remote", "set-url", "origin", buildAuthenticatedUrl(cfg));
            String b = branch.trim();
            StringBuilder sb = new StringBuilder();
            boolean ok = runCommand(repoDirFile, sb, 120, "git", "push", "--set-upstream", "origin", b);
            result.put("success", ok);
            result.put("log", sb.toString().trim());
            result.put("message", ok ? "推送成功 → origin/" + b : sb.toString().trim());
        } catch (Exception e) {
            result.put("success", false); result.put("message", e.getMessage());
        }
        return result;
    }

    @Override
    public Map<String, Object> gitForcePush(Long siteId, String branch) {
        Map<String, Object> result = new HashMap<>();
        try {
            GbSite site = gbSiteMapper.selectGbSiteById(siteId);
            if (site == null) { result.put("success", false); result.put("message", "网站不存在"); return result; }
            GbSiteCodeConfig cfg = codeConfigMapper.selectBySiteId(siteId);
            if (cfg == null || cfg.getGitRepoUrl() == null || cfg.getGitRepoUrl().isEmpty()) {
                result.put("success", false); result.put("message", "请先配置Git仓库URL"); return result;
            }
            File repoDirFile = new File(getRepoDir(site.getId(), site.getCode()));
            runCommand(repoDirFile, new StringBuilder(), 10, "git", "remote", "set-url", "origin", buildAuthenticatedUrl(cfg));
            String b = branch.trim();
            StringBuilder sb = new StringBuilder();
            boolean ok = runCommand(repoDirFile, sb, 120, "git", "push", "--force", "origin", b);
            result.put("success", ok);
            result.put("message", ok ? "强制推送成功 → origin/" + b : sb.toString().trim());
        } catch (Exception e) {
            result.put("success", false); result.put("message", e.getMessage());
        }
        return result;
    }

    @Override
    public Map<String, Object> gitCheckoutCommit(Long siteId, String hash) {
        Map<String, Object> result = new HashMap<>();
        try {
            GbSite site = gbSiteMapper.selectGbSiteById(siteId);
            if (site == null) { result.put("success", false); result.put("message", "网站不存在"); return result; }
            if (hash == null || hash.trim().isEmpty()) { result.put("success", false); result.put("message", "Hash 不能为空"); return result; }
            File repoDirFile = new File(getRepoDir(site.getId(), site.getCode()));
            StringBuilder sb = new StringBuilder();
            boolean ok = runCommand(repoDirFile, sb, 15, "git", "checkout", hash.trim());
            result.put("success", ok);
            result.put("message", ok ? "已检出到提交 " + hash.trim().substring(0, Math.min(7, hash.trim().length())) + "（Detached HEAD 状态）" : sb.toString().trim());
        } catch (Exception e) { result.put("success", false); result.put("message", e.getMessage()); }
        return result;
    }

    @Override
    public Map<String, Object> gitBranchFromCommit(Long siteId, String hash, String branch) {
        Map<String, Object> result = new HashMap<>();
        try {
            GbSite site = gbSiteMapper.selectGbSiteById(siteId);
            if (site == null) { result.put("success", false); result.put("message", "网站不存在"); return result; }
            if (hash == null || hash.trim().isEmpty()) { result.put("success", false); result.put("message", "Hash 不能为空"); return result; }
            if (branch == null || branch.trim().isEmpty()) { result.put("success", false); result.put("message", "分支名不能为空"); return result; }
            File repoDirFile = new File(getRepoDir(site.getId(), site.getCode()));
            StringBuilder sb = new StringBuilder();
            boolean ok = runCommand(repoDirFile, sb, 15, "git", "checkout", "-b", branch.trim(), hash.trim());
            result.put("success", ok);
            result.put("message", ok ? "分支「" + branch.trim() + "」已创建并切换" : sb.toString().trim());
            if (ok) result.put("branch", branch.trim());
        } catch (Exception e) { result.put("success", false); result.put("message", e.getMessage()); }
        return result;
    }

    @Override
    public Map<String, Object> gitMergeCommit(Long siteId, String hash) {
        Map<String, Object> result = new HashMap<>();
        try {
            GbSite site = gbSiteMapper.selectGbSiteById(siteId);
            if (site == null) { result.put("success", false); result.put("message", "网站不存在"); return result; }
            if (hash == null || hash.trim().isEmpty()) { result.put("success", false); result.put("message", "Hash 不能为空"); return result; }
            File repoDirFile = new File(getRepoDir(site.getId(), site.getCode()));
            StringBuilder sb = new StringBuilder();
            boolean ok = runCommand(repoDirFile, sb, 30, "git", "merge", hash.trim());
            String out = sb.toString().trim();
            result.put("success", ok);
            if (ok) {
                result.put("message", "已将提交 " + hash.trim().substring(0, Math.min(7, hash.trim().length())) + " 合并到当前分支");
            } else if (out.contains("CONFLICT") || out.contains("conflict")) {
                result.put("message", "合并冲突，请手动解冲突后提交");
                result.put("conflict", true);
            } else {
                result.put("message", out.isEmpty() ? "合并失败" : out);
            }
        } catch (Exception e) { result.put("success", false); result.put("message", e.getMessage()); }
        return result;
    }

    @Override
    public Map<String, Object> gitRevertCommit(Long siteId, String hash) {
        Map<String, Object> result = new HashMap<>();
        try {
            GbSite site = gbSiteMapper.selectGbSiteById(siteId);
            if (site == null) { result.put("success", false); result.put("message", "网站不存在"); return result; }
            if (hash == null || hash.trim().isEmpty()) { result.put("success", false); result.put("message", "Hash 不能为空"); return result; }
            File repoDirFile = new File(getRepoDir(site.getId(), site.getCode()));
            StringBuilder sb = new StringBuilder();
            boolean ok = runCommand(repoDirFile, sb, 30, "git", "revert", "--no-edit", hash.trim());
            String out = sb.toString().trim();
            result.put("success", ok);
            if (ok) {
                result.put("message", "已生成 Revert 提交，撤销了 " + hash.trim().substring(0, Math.min(7, hash.trim().length())) + " 的更改");
            } else if (out.contains("CONFLICT") || out.contains("conflict")) {
                result.put("message", "Revert 冲突，请手动解冲突后提交");
                result.put("conflict", true);
            } else {
                result.put("message", out.isEmpty() ? "Revert 失败" : out);
            }
        } catch (Exception e) { result.put("success", false); result.put("message", e.getMessage()); }
        return result;
    }

    @Override
    public Map<String, Object> gitResetHardForcePush(Long siteId, String hash) {
        Map<String, Object> result = new HashMap<>();
        try {
            GbSite site = gbSiteMapper.selectGbSiteById(siteId);
            if (site == null) { result.put("success", false); result.put("message", "网站不存在"); return result; }
            if (hash == null || hash.trim().isEmpty()) { result.put("success", false); result.put("message", "Hash 不能为空"); return result; }
            File repoDirFile = new File(getRepoDir(site.getId(), site.getCode()));
            String h = hash.trim();
            // 1. 获取当前分支（只取第一行，避免 [退出码] 被混入）
            StringBuilder sbBranch = new StringBuilder();
            runCommand(repoDirFile, sbBranch, 10, "git", "branch", "--show-current");
            String currentBranch = "";
            for (String ln : sbBranch.toString().split("\n")) {
                String t = ln.trim();
                if (!t.isEmpty() && !t.startsWith("[")) { currentBranch = t; break; }
            }
            if (currentBranch.isEmpty()) {
                result.put("success", false); result.put("message", "当前处于 Detached HEAD 状态，请先切回分支再操作"); return result;
            }
            // 2. git reset --hard（只改本地，不自动强推）
            StringBuilder sbReset = new StringBuilder();
            boolean resetOk = runCommand(repoDirFile, sbReset, 15, "git", "reset", "--hard", h);
            if (!resetOk) {
                result.put("success", false);
                result.put("message", "reset 失败: " + sbReset.toString().trim());
                return result;
            }
            String shortHash = h.length() >= 7 ? h.substring(0, 7) : h;
            result.put("success", true);
            result.put("branch", currentBranch);
            result.put("message", "本地历史已回退至 " + shortHash + "，当前分支: " + currentBranch + "。\n请点击「推送」将此变更强制同步到远端");
        } catch (Exception e) { result.put("success", false); result.put("message", e.getMessage()); }
        return result;
    }

    @Override
    public Map<String, Object> getGitDiff(Long siteId, String path) {
        Map<String, Object> result = new HashMap<>();
        try {
            GbSite site = gbSiteMapper.selectGbSiteById(siteId);
            if (site == null) { result.put("success", false); result.put("message", "网站不存在"); return result; }
            String repoDir = getRepoDir(site.getId(), site.getCode());
            File repoDirFile = new File(repoDir);
            if (!new File(repoDir, ".git").exists()) {
                result.put("success", false); result.put("message", "仓库不存在"); return result;
            }
            StringBuilder sb = new StringBuilder();
            if (path != null && !path.isEmpty()) {
                runCommand(repoDirFile, sb, 15, "git", "diff", "HEAD", "--", path);
                if (sb.toString().trim().isEmpty()) {
                    // 可能是新文件，尝试 staged diff
                    runCommand(repoDirFile, sb, 15, "git", "diff", "--cached", "--", path);
                }
            } else {
                runCommand(repoDirFile, sb, 30, "git", "diff", "HEAD");
            }
            result.put("success", true);
            result.put("diff", sb.toString());
        } catch (Exception e) {
            log.error("获取git diff失败", e);
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @Override
    public Map<String, Object> getGitOriginal(Long siteId, String path) {
        Map<String, Object> result = new HashMap<>();
        try {
            GbSite site = gbSiteMapper.selectGbSiteById(siteId);
            if (site == null) { result.put("success", false); result.put("message", "网站不存在"); return result; }
            String repoDir = getRepoDir(site.getId(), site.getCode());
            File repoDirFile = new File(repoDir);
            if (!new File(repoDir, ".git").exists()) {
                result.put("success", false); result.put("message", "仓库不存在"); return result;
            }
            if (path == null || path.isEmpty()) {
                result.put("success", false); result.put("message", "文件路径不能为空"); return result;
            }
            // 统一路径分隔符为正斜杠（git 使用正斜杠）
            String gitPath = path.replace('\\', '/');
            StringBuilder sb = new StringBuilder();
            boolean ok = runCommand(repoDirFile, sb, 15, "git", "show", "HEAD:" + gitPath);
            if (!ok) {
                // 新文件在 HEAD 中不存在，返回空内容
                result.put("success", true);
                result.put("content", "");
                result.put("isNew", true);
                return result;
            }
            // 去除 runCommand 追加的 "[退出码] N" 行
            String content = sb.toString();
            int lastMark = content.lastIndexOf("\n[退出码]");
            if (lastMark >= 0) content = content.substring(0, lastMark);
            // 去除 runCommand 追加的 "[执行命令] ..." 首行
            if (content.startsWith("[执行命令]")) {
                int firstNewline = content.indexOf('\n');
                content = firstNewline >= 0 ? content.substring(firstNewline + 1) : "";
            }
            result.put("success", true);
            result.put("content", content);
            result.put("isNew", false);
        } catch (Exception e) {
            log.error("获取git原始内容失败", e);
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @Override
    public Map<String, Object> getGitLog(Long siteId, int limit, String branch, boolean includeRemote) {
        Map<String, Object> result = new HashMap<>();
        try {
            GbSite site = gbSiteMapper.selectGbSiteById(siteId);
            if (site == null) { result.put("success", false); result.put("message", "网站不存在"); return result; }
            String repoDir = getRepoDir(site.getId(), site.getCode());
            File repoDirFile = new File(repoDir);
            if (!new File(repoDir, ".git").exists()) {
                result.put("success", false); result.put("message", "仓库不存在"); return result;
            }
            int n = (limit > 0 && limit <= 200) ? limit : 50;
            // 构建分支下拉列表：
            //   includeRemote=false → 本地分支 + git branch -r（无网络）
            //   includeRemote=true  → 本地分支 + ls-remote（网络，强制刷新）
            java.util.LinkedHashSet<String> allBranchesSet = new java.util.LinkedHashSet<>();
            // 本地分支
            StringBuilder sbLocal = new StringBuilder();
            runCommand(repoDirFile, sbLocal, 10, "git", "branch");
            for (String line : sbLocal.toString().split("\n")) {
                if (line.length() < 2 || line.startsWith("[退出码]") || line.startsWith("[超时]") || line.startsWith("[执行命令]")) continue;
                String n2 = line.replaceFirst("^[*\\s]+", "").trim();
                if (!n2.isEmpty()) allBranchesSet.add(n2);
            }
            if (includeRemote) {
                // 网络方式：ls-remote
                GbSiteCodeConfig cfg = codeConfigMapper.selectBySiteId(siteId);
                if (cfg != null && cfg.getGitRepoUrl() != null && !cfg.getGitRepoUrl().isEmpty()) {
                    try {
                        runCommand(repoDirFile, new StringBuilder(), 5,
                                "git", "remote", "set-url", "origin", buildAuthenticatedUrl(cfg));
                        StringBuilder sbr = new StringBuilder();
                        runCommand(repoDirFile, sbr, 30, "git", "ls-remote", "--heads", "origin");
                        for (String line : sbr.toString().split("\n")) {
                            if (line.length() < 2 || line.startsWith("[退出码]") || line.startsWith("[超时]") || line.startsWith("[执行命令]")) continue;
                            String[] parts = line.split("\t");
                            if (parts.length < 2) continue;
                            String ref = parts[1].trim();
                            if (!ref.startsWith("refs/heads/")) continue;
                            String rname = ref.substring("refs/heads/".length());
                            if (!rname.isEmpty()) allBranchesSet.add("origin/" + rname);
                        }
                    } catch (Exception lsEx) {
                        log.warn("ls-remote 失败（忽略）: {}", lsEx.getMessage());
                    }
                }
            } else {
                // 本地方式：git branch -r（读 remote-tracking refs，无网络）
                StringBuilder sbr = new StringBuilder();
                runCommand(repoDirFile, sbr, 10, "git", "branch", "-r");
                for (String line : sbr.toString().split("\n")) {
                    if (line.length() < 2 || line.startsWith("[退出码]") || line.startsWith("[超时]") || line.startsWith("[执行命令]")) continue;
                    String name = line.trim();
                    if (name.isEmpty() || name.contains("->")) continue;
                    allBranchesSet.add(name); // 保留 "origin/xxx" 格式
                }
            }
            // 格式: hash|parents|作者|日期|装饰refs|消息  (%P=父哈希空格分隔, %D=refs decoration)
            List<String> cmd = new ArrayList<>(java.util.Arrays.asList(
                "git", "log",
                "--topo-order",
                "--pretty=format:%H|%P|%an|%ad|%D|%s",
                "--date=format:%Y-%m-%d %H:%M",
                "-n", String.valueOf(n)
            ));
            boolean filterBranch = branch != null && !branch.trim().isEmpty();
            if (filterBranch) {
                cmd.add(branch.trim());
            } else {
                cmd.add("--all");
            }
            StringBuilder sb = new StringBuilder();
            runCommand(repoDirFile, sb, 30, cmd.toArray(new String[0]));
            List<Map<String, Object>> commits = new ArrayList<>();
            // allBranchesSet 已在上方通过 ls-remote 构建完毕，此处不再重复声明
            for (String line : sb.toString().split("\n")) {
                if (line.isEmpty()) continue;
                // 跳过执行命令回显行（[执行命令]行本身含 | 字符，会被误分为6段）
                if (line.startsWith("[执行命令]") || line.startsWith("[超时]") || line.startsWith("[退出码]")) continue;
                // 格式: hash|parents|author|date|D|subject  (D 可能为空)
                String[] parts = line.split("\\|", 6);
                if (parts.length < 6) continue;
                String hash = parts[0].trim();
                // parents: 空格分隔的父哈希列表（合并提交有多个父）
                String parentsStr = parts[1].trim();
                List<String> parentHashes = new ArrayList<>();
                if (!parentsStr.isEmpty()) {
                    for (String ph : parentsStr.split(" ")) {
                        String phTrimmed = ph.trim();
                        if (!phTrimmed.isEmpty()) parentHashes.add(phTrimmed);
                    }
                }
                String decStr = parts[4].trim(); // HEAD -> main, origin/main
                String message = parts[5].trim();
                List<Map<String, String>> refs = new ArrayList<>();
                if (!decStr.isEmpty()) {
                    for (String dec : decStr.split(", ")) {
                        dec = dec.trim();
                        if (dec.isEmpty()) continue;
                        // 跳过 git 内部标记（浅克隆 / graft）
                        if (dec.equals("grafted") || dec.equals("shallow")) continue;
                        Map<String, String> ref = new HashMap<>();
                        if (dec.startsWith("HEAD -> ")) {
                            String brName = dec.substring(8);
                            ref.put("type", "head");
                            ref.put("name", brName);
                            ref.put("short", brName);
                        } else if (dec.equals("HEAD")) {
                            ref.put("type", "detached");
                            ref.put("name", "HEAD");
                            ref.put("short", "HEAD");
                        } else if (dec.startsWith("tag: ")) {
                            String tagName = dec.substring(5);
                            ref.put("type", "tag");
                            ref.put("name", tagName);
                            ref.put("short", tagName);
                        } else if (dec.startsWith("refs/remotes/")) {
                            String short2 = dec.substring(13);
                            ref.put("type", "remote");
                            ref.put("name", dec);
                            ref.put("short", short2);
                        } else if (dec.startsWith("refs/heads/")) {
                            String short2 = dec.substring(11);
                            ref.put("type", "local");
                            ref.put("name", dec);
                            ref.put("short", short2);
                        } else {
                            ref.put("type", "local");
                            ref.put("name", dec);
                            ref.put("short", dec);
                        }
                        refs.add(ref);
                    }
                }
                Map<String, Object> commit = new HashMap<>();
                commit.put("hash", hash);
                commit.put("shortHash", hash.length() >= 7 ? hash.substring(0, 7) : hash);
                commit.put("author", parts[2].trim());
                commit.put("date", parts[3].trim());
                commit.put("message", message);
                commit.put("refs", refs);
                commit.put("parents", parentHashes);
                commits.add(commit);
            }
            result.put("success", true);
            result.put("commits", commits);
            result.put("allBranches", new ArrayList<>(allBranchesSet));
            result.put("filteredBranch", filterBranch ? branch.trim() : "");
        } catch (Exception e) {
            log.error("获取git日志失败", e);
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @Override
    public Map<String, Object> getNodeTypes(Long siteId) {
        Map<String, Object> result = new HashMap<>();
        try {
            GbSite site = gbSiteMapper.selectGbSiteById(siteId);
            if (site == null) { result.put("success", false); result.put("message", "网站不存在"); return result; }
            File repoDir = new File(getRepoDir(site.getId(), site.getCode()));
            // 查找 node_modules 目录 (先检查根目录，再检查一级子目录)
            File nodeModules = new File(repoDir, "node_modules");
            if (!nodeModules.isDirectory()) {
                File[] children = repoDir.listFiles(File::isDirectory);
                nodeModules = null;
                if (children != null) {
                    for (File child : children) {
                        if (child.getName().startsWith(".")) continue;
                        File nm = new File(child, "node_modules");
                        if (nm.isDirectory()) { nodeModules = nm; break; }
                    }
                }
            }
            if (nodeModules == null) {
                result.put("success", true); result.put("files", java.util.Collections.emptyList()); return result;
            }
            // 从 package.json 读取依赖列表
            File pkgJson = new File(nodeModules.getParentFile(), "package.json");
            java.util.LinkedHashSet<String> deps = new java.util.LinkedHashSet<>();
            if (pkgJson.exists()) {
                String pkgContent = new String(java.nio.file.Files.readAllBytes(pkgJson.toPath()), java.nio.charset.StandardCharsets.UTF_8);
                nodeTypeParseDeps(pkgContent, deps);
            }
            List<Map<String, String>> files = new ArrayList<>();
            final long maxTotal = 3L * 1024 * 1024; // 3MB
            final long maxFile  = 100L * 1024;       // 100KB per file
            long[] totalSize = {0};
            // 1. 优先扫描 @types/
            File atTypes = new File(nodeModules, "@types");
            if (atTypes.isDirectory()) {
                File[] typePkgs = atTypes.listFiles(File::isDirectory);
                if (typePkgs != null) {
                    for (File tp : typePkgs) {
                        if (totalSize[0] >= maxTotal) break;
                        nodeTypeCollect(tp, "node_modules/@types/" + tp.getName(), files, totalSize, maxFile, maxTotal);
                    }
                }
            }
            // 2. 扫描各依赖的 type entry point
            for (String dep : deps) {
                if (totalSize[0] >= maxTotal) break;
                File depDir = dep.startsWith("@") ? new File(nodeModules, dep) : new File(nodeModules, dep);
                if (!depDir.isDirectory()) continue;
                String virtualBase = "node_modules/" + dep;
                // 读 package.json 的 types/typings 字段
                File depPkg = new File(depDir, "package.json");
                String typesEntry = null;
                if (depPkg.exists()) {
                    String pc = new String(java.nio.file.Files.readAllBytes(depPkg.toPath()), java.nio.charset.StandardCharsets.UTF_8);
                    typesEntry = nodeTypeExtractStr(pc, "types");
                    if (typesEntry == null) typesEntry = nodeTypeExtractStr(pc, "typings");
                }
                if (typesEntry != null) {
                    File tf = new File(depDir, typesEntry);
                    if (tf.isFile() && tf.length() < maxFile) {
                        String content = new String(java.nio.file.Files.readAllBytes(tf.toPath()), java.nio.charset.StandardCharsets.UTF_8);
                        Map<String, String> e = new HashMap<>();
                        e.put("path", virtualBase + "/" + typesEntry.replace('\\', '/'));
                        e.put("content", content);
                        files.add(e); totalSize[0] += content.length();
                    }
                } else {
                    File idx = new File(depDir, "index.d.ts");
                    if (idx.isFile() && idx.length() < maxFile) {
                        String content = new String(java.nio.file.Files.readAllBytes(idx.toPath()), java.nio.charset.StandardCharsets.UTF_8);
                        Map<String, String> e = new HashMap<>();
                        e.put("path", virtualBase + "/index.d.ts");
                        e.put("content", content);
                        files.add(e); totalSize[0] += content.length();
                    }
                }
            }
            result.put("success", true);
            result.put("files", files);
            result.put("count", files.size());
        } catch (Exception e) {
            log.error("获取node types失败", e);
            result.put("success", false); result.put("message", e.getMessage());
        }
        return result;
    }

    private void nodeTypeCollect(File dir, String vPath, List<Map<String, String>> files,
                                  long[] totalSize, long maxFile, long maxTotal) throws Exception {
        if (totalSize[0] >= maxTotal) return;
        File[] children = dir.listFiles();
        if (children == null) return;
        for (File f : children) {
            if (totalSize[0] >= maxTotal) break;
            if (f.isDirectory()) {
                nodeTypeCollect(f, vPath + "/" + f.getName(), files, totalSize, maxFile, maxTotal);
            } else if (f.getName().endsWith(".d.ts") && f.length() < maxFile) {
                String content = new String(java.nio.file.Files.readAllBytes(f.toPath()), java.nio.charset.StandardCharsets.UTF_8);
                Map<String, String> e = new HashMap<>();
                e.put("path", vPath + "/" + f.getName());
                e.put("content", content);
                files.add(e); totalSize[0] += content.length();
            }
        }
    }

    private void nodeTypeParseDeps(String json, java.util.Set<String> deps) {
        for (String section : new String[]{"\"dependencies\"", "\"devDependencies\""}) {
            int idx = json.indexOf(section);
            if (idx < 0) continue;
            int brace = json.indexOf('{', idx);
            if (brace < 0) continue;
            int depth = 0, end = -1;
            for (int i = brace; i < json.length(); i++) {
                char c = json.charAt(i);
                if (c == '{') depth++; else if (c == '}') { if (--depth == 0) { end = i; break; } }
            }
            if (end < 0) continue;
            java.util.regex.Matcher m = java.util.regex.Pattern.compile("\"(@?[^\"]+)\"\\s*:").matcher(json.substring(brace + 1, end));
            while (m.find()) deps.add(m.group(1));
        }
    }

    private String nodeTypeExtractStr(String json, String key) {
        int idx = json.indexOf("\"" + key + "\"");
        if (idx < 0) return null;
        int colon = json.indexOf(':', idx);
        if (colon < 0) return null;
        int q1 = json.indexOf('"', colon + 1);
        if (q1 < 0) return null;
        int q2 = json.indexOf('"', q1 + 1);
        if (q2 < 0) return null;
        return json.substring(q1 + 1, q2);
    }

    @Override
    public Map<String, Object> gitMergeBranch(Long siteId, String branch) {
        Map<String, Object> result = new HashMap<>();
        try {
            GbSite site = gbSiteMapper.selectGbSiteById(siteId);
            if (site == null) { result.put("success", false); result.put("message", "网站不存在"); return result; }
            if (branch == null || branch.trim().isEmpty()) { result.put("success", false); result.put("message", "分支名不能为空"); return result; }
            File repoDirFile = new File(getRepoDir(site.getId(), site.getCode()));
            StringBuilder sb = new StringBuilder();
            boolean ok = runCommand(repoDirFile, sb, 60, "git", "merge", branch.trim(), "--no-edit");
            String out = sb.toString().trim();
            result.put("output", out);
            if (ok && !out.contains("CONFLICT")) {
                result.put("success", true);
                result.put("message", "已将分支「" + branch.trim() + "」合并到当前分支");
            } else if (out.contains("CONFLICT") || out.contains("conflict")) {
                result.put("success", false);
                result.put("conflict", true);
                result.put("message", "合并冲突，请手动解冲突后提交");
                // 提取冲突文件列表
                java.util.List<String> conflictFiles = new java.util.ArrayList<>();
                for (String line : out.split("\\n")) {
                    if (line.startsWith("CONFLICT")) {
                        // 格式: CONFLICT (content): Merge conflict in <file>
                        int inIdx = line.lastIndexOf(" in ");
                        if (inIdx >= 0) conflictFiles.add(line.substring(inIdx + 4).trim());
                    }
                }
                result.put("conflictFiles", conflictFiles);
            } else {
                result.put("success", false);
                result.put("message", out.isEmpty() ? "合并失败" : out);
            }
        } catch (Exception e) { result.put("success", false); result.put("message", e.getMessage()); }
        return result;
    }

    @Override
    public Map<String, Object> gitMergeAbort(Long siteId) {
        Map<String, Object> result = new HashMap<>();
        try {
            GbSite site = gbSiteMapper.selectGbSiteById(siteId);
            if (site == null) { result.put("success", false); result.put("message", "网站不存在"); return result; }
            File repoDirFile = new File(getRepoDir(site.getId(), site.getCode()));
            StringBuilder sb = new StringBuilder();
            boolean ok = runCommand(repoDirFile, sb, 30, "git", "merge", "--abort");
            String out = sb.toString().trim();
            result.put("success", ok);
            result.put("message", ok ? "已放弃合并，工作区已恢复" : (out.isEmpty() ? "放弃合并失败" : out));
        } catch (Exception e) { result.put("success", false); result.put("message", e.getMessage()); }
        return result;
    }

    // ====================================================================
    // =================== Cloudflare Workers 部署 ========================
    // ====================================================================

    @Override
    public Map<String, Object> deployToWorkers(Long siteId) {
        Map<String, Object> result = new HashMap<>();
        GbSite site = gbSiteMapper.selectGbSiteById(siteId);
        if (site == null) {
            result.put("success", false);
            result.put("message", "网站不存在");
            return result;
        }
        GbSiteCodeConfig cfg = codeConfigMapper.selectBySiteId(siteId);
        if (cfg == null) {
            result.put("success", false);
            result.put("message", "请先配置部署信息");
            return result;
        }
        GbPlatformCfAccount deployAcc = resolveCfAccount(cfg);
        String token = deployAcc != null ? deployAcc.getApiToken() : null;
        if (token == null || token.isEmpty()) {
            result.put("success", false);
            result.put("message", "未配置 Cloudflare 账号，请先在部署配置中选择 CF 账号");
            return result;
        }
        String accountId = deployAcc.getAccountId();
        if (accountId == null || accountId.isEmpty()) {
            result.put("success", false);
            result.put("message", "CF 账号 Account ID 未设置，请检查平台账号配置");
            return result;
        }
        String scriptName = cfg.getCloudflareProjectName();
        if (scriptName == null || scriptName.isEmpty()) {
            result.put("success", false);
            result.put("message", "请先配置 Worker 脚本名称（项目名称字段）");
            return result;
        }

        String repoDir = getRepoDir(site.getId(), site.getCode());
        File repoDirFile = new File(repoDir);
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("开始部署到 Cloudflare Workers...\n");
        logBuilder.append("账户 ID: ").append(accountId).append("\n");
        logBuilder.append("脚本名称: ").append(scriptName).append("\n\n");

        try {
            // 确认 wrangler 可用（项目本地依赖中应包含 wrangler）
            logBuilder.append("使用 wrangler 打包并部署...\n");
            logBuilder.append("（wrangler 会将所有依赖打包为单一 bundle 后上传）\n\n");

            // 注入环境变量并调用 wrangler deploy
            //    --name 覆盖 wrangler.toml 中的 name，使其与管理平台配置保持一致
            Map<String, String> deployEnv = new java.util.HashMap<>();
            deployEnv.put("CLOUDFLARE_API_TOKEN", token);
            deployEnv.put("CLOUDFLARE_ACCOUNT_ID", accountId);
            // 关闭 wrangler 交互提示，避免在非交互环境中卡住
            deployEnv.put("WRANGLER_SEND_METRICS", "false");
            deployEnv.put("CI", "true");

            // 读取用户自定义的 deployCommand（默认 "npx wrangler deploy"）
            String customDeployCmd = "npx wrangler deploy";
            try {
                String deployConfigJson = cfg.getDeployConfig();
                if (deployConfigJson != null && !deployConfigJson.isEmpty()) {
                    com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();
                    com.fasterxml.jackson.databind.JsonNode node = om.readTree(deployConfigJson);
                    if (node.has("deployCommand") && !node.get("deployCommand").asText("").isEmpty()) {
                        customDeployCmd = node.get("deployCommand").asText();
                    }
                }
            } catch (Exception ignored) {}

            // 将命令字符串按空格分词，并确保 --name <scriptName> 已包含（自动注入）
            java.util.List<String> cmdTokens = new java.util.ArrayList<>(java.util.Arrays.asList(customDeployCmd.trim().split("\\s+")));
            boolean hasName = false;
            for (int i = 0; i < cmdTokens.size(); i++) {
                if ("--name".equals(cmdTokens.get(i))) {
                    // 用户自己写了 --name，将其值替换为当前 scriptName 以保证一致性
                    if (i + 1 < cmdTokens.size()) {
                        cmdTokens.set(i + 1, scriptName);
                    } else {
                        cmdTokens.add(scriptName);
                    }
                    hasName = true;
                    break;
                }
            }
            if (!hasName) {
                cmdTokens.add("--name");
                cmdTokens.add(scriptName);
            }
            logBuilder.append("执行命令: ").append(String.join(" ", cmdTokens)).append("\n\n");

            boolean success = runCommand(repoDirFile, logBuilder, 300, deployEnv,
                    cmdTokens.toArray(new String[0]));

            if (success) {
                String cfSubdomain = getCfWorkerSubdomain(accountId, token);
                String deployUrl = "https://" + scriptName + "." + cfSubdomain + ".workers.dev";
                logBuilder.append("\n✓ 部署成功!\n");
                logBuilder.append("Worker URL: ").append(deployUrl).append("\n");
                codeConfigMapper.updateDeployStatus(siteId, "deployed", deployUrl,
                        new java.util.Date(), logBuilder.toString());
                result.put("success", true);
                result.put("message", "部署成功");
                result.put("deployUrl", deployUrl);
            } else {
                logBuilder.append("\n✗ 部署失败，请查看上方日志\n");
                codeConfigMapper.updateDeployStatus(siteId, "failed", cfg.getDeployUrl(),
                        new java.util.Date(), logBuilder.toString());
                result.put("success", false);
                result.put("message", "wrangler deploy 失败，请查看日志");
            }
            result.put("log", logBuilder.toString());
        } catch (Exception e) {
            log.error("部署到 CF Workers 失败 siteId={}", siteId, e);
            logBuilder.append("\n异常: ").append(e.getMessage()).append("\n");
            codeConfigMapper.updateDeployStatus(siteId, "failed", cfg.getDeployUrl(),
                    new java.util.Date(), logBuilder.toString());
            result.put("success", false);
            result.put("message", "部署异常: " + e.getMessage());
            result.put("log", logBuilder.toString());
        }
        return result;
    }

    /** 写入一个 multipart part */
    private void writeMultipartPart(java.io.OutputStream out, String boundary,
                                     String name, String filename,
                                     String contentType, byte[] content) throws java.io.IOException {
        String disposition = "form-data; name=\"" + name + "\""
                + (filename != null ? "; filename=\"" + filename + "\"" : "");
        out.write(("--" + boundary + "\r\n").getBytes("UTF-8"));
        out.write(("Content-Disposition: " + disposition + "\r\n").getBytes("UTF-8"));
        out.write(("Content-Type: " + contentType + "\r\n").getBytes("UTF-8"));
        out.write("\r\n".getBytes("UTF-8"));
        out.write(content);
        out.write("\r\n".getBytes("UTF-8"));
    }

    @Override
    public java.util.List<java.util.Map<String, Object>> listCfZones(Long siteId) {
        java.util.List<java.util.Map<String, Object>> list = new java.util.ArrayList<>();
        GbSiteCodeConfig cfg = codeConfigMapper.selectBySiteId(siteId);
        if (cfg == null) return list;
        GbPlatformCfAccount listZoneAcc = resolveCfAccount(cfg);
        String token = listZoneAcc != null ? listZoneAcc.getApiToken() : null;
        String accountId = listZoneAcc != null ? listZoneAcc.getAccountId() : null;
        if (token == null || token.isEmpty() || accountId == null || accountId.isEmpty()) return list;
        try {
            String url = "https://api.cloudflare.com/client/v4/zones?account.id=" + accountId + "&per_page=100&status=active";
            java.net.HttpURLConnection conn = openCfApiConn(url);
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            int code = conn.getResponseCode();
            if (code != 200) return list;
            String body = readHttpBody(conn);
            // 解析 {"result":[{"id":"...","name":"..."},...]} 格式
            int resIdx = body.indexOf("\"result\"");
            if (resIdx < 0) return list;
            int arrStart = body.indexOf('[', resIdx);
            int arrEnd = body.lastIndexOf(']');
            if (arrEnd <= arrStart) return list;
            String arrPart = body.substring(arrStart + 1, arrEnd);
            int depth = 0, objStart = -1;
            for (int i = 0; i < arrPart.length(); i++) {
                char c = arrPart.charAt(i);
                if (c == '{') { if (depth++ == 0) objStart = i; }
                else if (c == '}') {
                    if (--depth == 0 && objStart >= 0) {
                        String obj = arrPart.substring(objStart, i + 1);
                        String id = extractJsonString(obj, "id");
                        String name = extractJsonString(obj, "name");
                        if (id != null && name != null) {
                            java.util.Map<String, Object> item = new java.util.HashMap<>();
                            item.put("id", id);
                            item.put("name", name);
                            list.add(item);
                        }
                        objStart = -1;
                    }
                }
            }
        } catch (Exception e) {
            log.warn("[CF zones] 查询失败: {}", e.getMessage());
        }
        return list;
    }

    @Override
    public Map<String, Object> listWorkerDomains(Long siteId) {
        Map<String, Object> result = new HashMap<>();
        GbSiteCodeConfig cfg = codeConfigMapper.selectBySiteId(siteId);
        if (cfg == null) {
            result.put("success", false);
            result.put("message", "未找到配置");
            return result;
        }
        GbPlatformCfAccount domainsAcc = resolveCfAccount(cfg);
        String token = domainsAcc != null ? domainsAcc.getApiToken() : null;
        String accountId = domainsAcc != null ? domainsAcc.getAccountId() : null;
        String scriptName = cfg.getCloudflareProjectName();
        if (token == null || token.isEmpty() || accountId == null || scriptName == null) {
            result.put("success", false);
            result.put("message", "请先在部署配置中填写账户 ID、API Token 和 Worker 名称");
            return result;
        }
        // 校验账户 ID 格式（应为 32 位十六进制）
        if (!accountId.matches("[0-9a-fA-F]{32}")) {
            result.put("success", false);
            result.put("message", "账户 ID 格式不正确，应为 32 位十六进制字符串（如：1a2b3c4d....）。请登录 Cloudflare 控制台右下角复制账户 ID");
            return result;
        }
        try {
            String url = "https://api.cloudflare.com/client/v4/accounts/" + accountId
                    + "/workers/domains?service=" + java.net.URLEncoder.encode(scriptName, "UTF-8");
            java.net.HttpURLConnection conn = openCfApiConn(url);
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(15000);
            int code = conn.getResponseCode();
            String body = readHttpBody(conn);
            if (code == 200) {
                result.put("success", true);
                result.put("rawJson", body);
                result.put("domains", parseCfDomainList(body));
            } else if (code == 404) {
                result.put("success", false);
                result.put("message", "账户 ID 不正确或无权限（HTTP 404）。请确认账户 ID 是否为十六进制 32 位，且该 Token 具有该账户的 Workers 权限");
            } else {
                result.put("success", false);
                result.put("message", "查询域名列表失败（HTTP " + code + "）: " + extractCfErrorMessage(body));
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询域名列表异常: " + e.getMessage());
        }
        return result;
    }

    @Override
    public Map<String, Object> bindWorkerDomain(Long siteId, String hostname, String zoneName) {
        Map<String, Object> result = new HashMap<>();
        GbSiteCodeConfig cfg = codeConfigMapper.selectBySiteId(siteId);
        if (cfg == null) { result.put("success", false); result.put("message", "未找到配置"); return result; }
        GbPlatformCfAccount bindAcc = resolveCfAccount(cfg);
        String token = bindAcc != null ? bindAcc.getApiToken() : null;
        String accountId = bindAcc != null ? bindAcc.getAccountId() : null;
        String scriptName = cfg.getCloudflareProjectName();
        if (token == null || token.isEmpty() || accountId == null || scriptName == null) {
            result.put("success", false);
            result.put("message", "请先在部署配置中选择 CF 账号和 Worker 名称");
            return result;
        }
        if (hostname == null || hostname.isEmpty()) {
            result.put("success", false);
            result.put("message", "域名不能为空");
            return result;
        }
        // 推断根域名：如未提供则取 hostname 最后两段（如 sub.example.com → example.com）
        String rootDomain = (zoneName != null && !zoneName.isEmpty()) ? zoneName : inferRootDomain(hostname);
        try {
            // 查询 zone_id（CF API 需要 zone_id，不接受 zone_name）
            String zoneId = lookupCfZoneId(rootDomain, token);
            if (zoneId == null || zoneId.isEmpty()) {
                result.put("success", false);
                result.put("message", "未能在 Cloudflare 中找到域名 " + rootDomain + " 对应的 Zone，请确认该域名已添加到 Cloudflare");
                return result;
            }
            String payload = "{"
                    + "\"environment\":\"production\","
                    + "\"hostname\":\"" + hostname + "\","
                    + "\"service\":\"" + scriptName + "\","
                    + "\"zone_id\":\"" + zoneId + "\""
                    + "}";
            java.net.HttpURLConnection conn = openCfApiConn("https://api.cloudflare.com/client/v4/accounts/" + accountId + "/workers/domains");
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);
            try (java.io.OutputStream os = conn.getOutputStream()) {
                os.write(payload.getBytes("UTF-8"));
            }
            int code = conn.getResponseCode();
            String body = readHttpBody(conn);
            if (code == 200) {
                result.put("success", true);
                result.put("message", "域名 " + hostname + " 已成功绑定到 Worker " + scriptName);
                result.put("rawJson", body);
            } else {
                result.put("success", false);
                result.put("message", "绑定域名失败（HTTP " + code + "）: " + extractCfErrorMessage(body));
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "绑定域名异常: " + e.getMessage());
        }
        return result;
    }

    /**
     * 通过 zone_name（根域名）查询 Cloudflare zone_id。
     * GET /zones?name={zoneName}
     */
    private String lookupCfZoneId(String zoneName, String token) {
        try {
            String url = "https://api.cloudflare.com/client/v4/zones?name=" + java.net.URLEncoder.encode(zoneName, "UTF-8");
            java.net.HttpURLConnection conn = openCfApiConn(url);
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            int code = conn.getResponseCode();
            if (code != 200) return null;
            String body = readHttpBody(conn);
            // 从 result 数组中取第一个 id
            return extractJsonString(body, "id");
        } catch (Exception e) {
            log.warn("[CF zone lookup] 查询 zone_id 失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从 hostname 推断根域名（取最后两段，e.g. sub.example.com → example.com）
     */
    private String inferRootDomain(String hostname) {
        if (hostname == null) return null;
        String[] parts = hostname.split("\\.");
        if (parts.length <= 2) return hostname;
        return parts[parts.length - 2] + "." + parts[parts.length - 1];
    }

    @Override
    public Map<String, Object> unbindWorkerDomain(Long siteId, String domainId) {
        Map<String, Object> result = new HashMap<>();
        GbSiteCodeConfig cfg = codeConfigMapper.selectBySiteId(siteId);
        if (cfg == null) { result.put("success", false); result.put("message", "未找到配置"); return result; }
        GbPlatformCfAccount unbindAcc = resolveCfAccount(cfg);
        String token = unbindAcc != null ? unbindAcc.getApiToken() : null;
        String accountId = unbindAcc != null ? unbindAcc.getAccountId() : null;
        if (token == null || token.isEmpty() || accountId == null) {
            result.put("success", false);
            result.put("message", "请先在部署配置中选择 CF 账号");
            return result;
        }
        try {
            java.net.HttpURLConnection conn = openCfApiConn("https://api.cloudflare.com/client/v4/accounts/" + accountId
                    + "/workers/domains/" + domainId);
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            int code = conn.getResponseCode();
            String body = readHttpBody(conn);
            if (code == 200 || code == 204) {
                result.put("success", true);
                result.put("message", "域名绑定已解除");
            } else {
                result.put("success", false);
                result.put("message", "解绑失败（HTTP " + code + "）: " + extractCfErrorMessage(body));
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "解绑域名异常: " + e.getMessage());
        }
        return result;
    }

    // ====================================================================
    // =================== CF 凭证验证 ====================================
    // ====================================================================

    @Override
    public Map<String, Object> verifyCfCredentials(Long siteId) {
        Map<String, Object> result = new HashMap<>();
        GbSiteCodeConfig cfg = codeConfigMapper.selectBySiteId(siteId);
        if (cfg == null) {
            result.put("success", false);
            result.put("message", "未找到配置，请先保存部署配置");
            return result;
        }
        GbPlatformCfAccount verifyCfAcc = resolveCfAccount(cfg);
        String token = verifyCfAcc != null ? verifyCfAcc.getApiToken() : null;
        String accountId = verifyCfAcc != null ? verifyCfAcc.getAccountId() : null;
        if (token == null || token.isEmpty()) {
            result.put("success", false);
            result.put("message", "请先在部署配置中选择 CF 账号");
            return result;
        }
        try {
            // Step 1: 验证 Token 本身是否有效
            java.net.HttpURLConnection conn = openCfApiConn("https://api.cloudflare.com/client/v4/user/tokens/verify");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            int code = conn.getResponseCode();
            String body = readHttpBody(conn);
            if (code != 200) {
                result.put("success", false);
                result.put("message", "API Token 无效（HTTP " + code + "）：" + extractCfErrorMessage(body));
                return result;
            }
            String tokenStatus = extractJsonString(body, "status");
            result.put("tokenValid", true);
            result.put("tokenStatus", tokenStatus != null ? tokenStatus : "active");

            // Step 2: 若提供了账户 ID，验证 Token 是否有权限访问该账户
            if (accountId != null && !accountId.isEmpty()) {
                java.net.HttpURLConnection conn2 = openCfApiConn("https://api.cloudflare.com/client/v4/accounts/" + accountId);
                conn2.setRequestProperty("Authorization", "Bearer " + token);
                conn2.setConnectTimeout(10000);
                conn2.setReadTimeout(10000);
                int code2 = conn2.getResponseCode();
                String body2 = readHttpBody(conn2);
                if (code2 == 200) {
                    String accountName = extractJsonString(body2, "name");
                    result.put("accountValid", true);
                    result.put("accountName", accountName != null ? accountName : "");
                    result.put("success", true);
                    result.put("message", "验证通过" + (accountName != null ? "，账户：" + accountName : ""));
                } else {
                    result.put("accountValid", false);
                    result.put("success", false);
                    result.put("message", "API Token 有效，但无法访问该账户 ID（HTTP " + code2 + "）："
                            + extractCfErrorMessage(body2) + "。请确认账户 ID 是否正确，以及该 Token 是否有此账户的权限。");
                }
            } else {
                result.put("success", true);
                result.put("message", "API Token 验证通过（状态：" + (tokenStatus != null ? tokenStatus : "active") + "），请填写账户 ID 后再次验证");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "验证请求失败：" + e.getMessage());
        }
        return result;
    }

    // ====================================================================
    // =================== CF 创建项目 ====================================
    // ====================================================================

    @Override
    public Map<String, Object> createCloudflareWorkerProject(Long siteId) {
        Map<String, Object> result = new HashMap<>();
        GbSite site = gbSiteMapper.selectGbSiteById(siteId);
        if (site == null) {
            result.put("success", false); result.put("message", "网站不存在"); return result;
        }
        GbSiteCodeConfig cfg = codeConfigMapper.selectBySiteId(siteId);
        if (cfg == null) {
            result.put("success", false); result.put("message", "请先保存部署配置"); return result;
        }
        GbPlatformCfAccount createCfAcc = resolveCfAccount(cfg);
        String token = createCfAcc != null ? createCfAcc.getApiToken() : null;
        if (token == null || token.isEmpty()) {
            result.put("success", false); result.put("message", "未配置 Cloudflare 账号，请先在部署配置中选择"); return result;
        }
        String accountId = createCfAcc.getAccountId();
        if (accountId == null || accountId.isEmpty()) {
            result.put("success", false); result.put("message", "CF 账号的 Account ID 未设置，请检查平台账号配置"); return result;
        }
        String scriptName = cfg.getCloudflareProjectName();
        if (scriptName == null || scriptName.isEmpty()) {
            result.put("success", false); result.put("message", "请先配置 Worker 名称（项目名称字段）"); return result;
        }

        // 上传一个最小占位脚本，创建 Worker 项目（若已存在则更新占位内容）
        String placeholderScript = "addEventListener('fetch', event => {\n"
                + "  event.respondWith(new Response('Project is being set up, please wait...', {\n"
                + "    status: 200,\n"
                + "    headers: { 'Content-Type': 'text/plain; charset=utf-8' }\n"
                + "  }))\n"
                + "})";
        try {
            String boundary = "----cfworkerbound" + System.currentTimeMillis();
            String metadata = "{\"body_part\":\"script\"}";
            java.io.ByteArrayOutputStream multipartBody = new java.io.ByteArrayOutputStream();
            writeMultipartPart(multipartBody, boundary, "metadata", null,
                    "application/json", metadata.getBytes("UTF-8"));
            writeMultipartPart(multipartBody, boundary, "script", null,
                    "application/javascript", placeholderScript.getBytes("UTF-8"));
            multipartBody.write(("--" + boundary + "--\r\n").getBytes("UTF-8"));

            String apiUrl = "https://api.cloudflare.com/client/v4/accounts/" + accountId
                    + "/workers/scripts/" + scriptName;
            java.net.HttpURLConnection conn = openCfApiConn(apiUrl);
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            conn.setDoOutput(true);
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(60000);
            byte[] bodyBytes = multipartBody.toByteArray();
            conn.setRequestProperty("Content-Length", String.valueOf(bodyBytes.length));
            try (java.io.OutputStream os = conn.getOutputStream()) {
                os.write(bodyBytes);
            }
            int httpCode = conn.getResponseCode();
            String responseBody = readHttpBody(conn);
            if (httpCode == 200) {
                String cfSubdomain = getCfWorkerSubdomain(accountId, token);
                String workerUrl = "https://" + scriptName + "." + cfSubdomain + ".workers.dev";
                result.put("success", true);
                result.put("message", "Worker 项目创建成功，占位页已上线");
                result.put("workerUrl", workerUrl);
            } else {
                String errMsg = extractCfErrorMessage(responseBody);
                result.put("success", false);
                result.put("message", "项目创建失败（HTTP " + httpCode + "）：" + errMsg);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "创建请求异常：" + e.getMessage());
        }
        return result;
    }

    @Override
    public Map<String, Object> checkCloudflareWorkerProject(Long siteId) {
        Map<String, Object> result = new HashMap<>();
        GbSiteCodeConfig cfg = codeConfigMapper.selectBySiteId(siteId);
        if (cfg == null) {
            result.put("success", false); result.put("message", "请先保存部署配置"); return result;
        }
        GbPlatformCfAccount checkCfAcc = resolveCfAccount(cfg);
        String token = checkCfAcc != null ? checkCfAcc.getApiToken() : null;
        String accountId = checkCfAcc != null ? checkCfAcc.getAccountId() : null;
        String scriptName = cfg.getCloudflareProjectName();
        if (token == null || token.isEmpty() || accountId == null || accountId.isEmpty()
                || scriptName == null || scriptName.isEmpty()) {
            result.put("success", true);
            result.put("exists", false);
            result.put("message", "配置不完整");
            return result;
        }
        try {
            String apiUrl = "https://api.cloudflare.com/client/v4/accounts/" + accountId
                    + "/workers/scripts/" + scriptName;
            java.net.HttpURLConnection conn = openCfApiConn(apiUrl);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            int httpCode = conn.getResponseCode();
            if (httpCode == 200) {
                String cfSubdomain = getCfWorkerSubdomain(accountId, token);
                String workerUrl = "https://" + scriptName + "." + cfSubdomain + ".workers.dev";
                result.put("success", true);
                result.put("exists", true);
                result.put("workerUrl", workerUrl);
                result.put("message", "Worker 项目已存在");
            } else {
                result.put("success", true);
                result.put("exists", false);
                result.put("message", "Worker 项目尚未创建");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "检查失败：" + e.getMessage());
        }
        return result;
    }

    @Override
    public Map<String, Object> deleteCloudflareWorkerProject(Long siteId) {
        Map<String, Object> result = new HashMap<>();
        GbSiteCodeConfig cfg = codeConfigMapper.selectBySiteId(siteId);
        if (cfg == null) {
            result.put("success", false); result.put("message", "请先保存部署配置"); return result;
        }
        GbPlatformCfAccount delAcc = resolveCfAccount(cfg);
        String token = delAcc != null ? delAcc.getApiToken() : null;
        String accountId = delAcc != null ? delAcc.getAccountId() : null;
        String scriptName = cfg.getCloudflareProjectName();
        if (token == null || token.isEmpty() || accountId == null || accountId.isEmpty()) {
            result.put("success", false); result.put("message", "请先配置 CF 账号"); return result;
        }
        if (scriptName == null || scriptName.isEmpty()) {
            result.put("success", false); result.put("message", "请先配置 Worker 名称（项目名称字段）"); return result;
        }
        try {
            String apiUrl = "https://api.cloudflare.com/client/v4/accounts/" + accountId
                    + "/workers/scripts/" + scriptName;
            java.net.HttpURLConnection conn = openCfApiConn(apiUrl);
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);
            int httpCode = conn.getResponseCode();
            String responseBody = readHttpBody(conn);
            if (httpCode == 200) {
                result.put("success", true);
                result.put("message", "Worker 脚本「" + scriptName + "」已删除");
            } else {
                String errMsg = extractCfErrorMessage(responseBody);
                result.put("success", false);
                result.put("message", "删除失败（HTTP " + httpCode + "）：" + errMsg);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除请求异常：" + e.getMessage());
        }
        return result;
    }

    // ====================================================================
    // =================== CF 工具方法 ====================================
    // ====================================================================

    /**
     * 获取 Cloudflare Workers 账户子域名（即 workers.dev 前的用户名部分）。
     * 调用 GET /accounts/{accountId}/workers/subdomain 接口，返回如 "misinguo"。
     * 失败或接口不可用时回退到 accountId 前 8 位（降级兜底）。
     */
    private String getCfWorkerSubdomain(String accountId, String token) {
        try {
            String apiUrl = "https://api.cloudflare.com/client/v4/accounts/" + accountId + "/workers/subdomain";
            java.net.HttpURLConnection conn = openCfApiConn(apiUrl);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);
            int code = conn.getResponseCode();
            String body = readHttpBody(conn);
            log.info("[CF subdomain] HTTP={} body={}", code, body);
            if (code == 200) {
                // 响应: {"result":{"subdomain":"misinguo"}, ...}
                String sub = extractJsonString(body, "subdomain");
                if (sub != null && !sub.isEmpty()) return sub;
            }
        } catch (Exception ignore) {
            // 网络异常等，降级处理
        }
        // 降级：使用 accountId 前 8 位（旧行为）
        return accountId.length() >= 8 ? accountId.substring(0, 8) : accountId;
    }

    /** 读取 HttpURLConnection 的响应体（自动处理 errorStream） */
    private String readHttpBody(java.net.HttpURLConnection conn) {
        try {
            java.io.InputStream is;
            try { is = conn.getInputStream(); } catch (java.io.IOException e) { is = conn.getErrorStream(); }
            if (is == null) return "";
            StringBuilder sb = new StringBuilder();
            try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(is, "UTF-8"))) {
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

    /** 从 JSON 字符串中简单提取 string 字段（不依赖 JSON 库，兼容冒号后有空格的格式） */
    private String extractJsonString(String json, String key) {
        if (json == null) return null;
        String search = "\"" + key + "\"";
        int idx = json.indexOf(search);
        if (idx < 0) return null;
        // 跳过 key 后的空白和冒号
        int pos = idx + search.length();
        while (pos < json.length() && (json.charAt(pos) == ' ' || json.charAt(pos) == '\t' || json.charAt(pos) == '\r' || json.charAt(pos) == '\n')) pos++;
        if (pos >= json.length() || json.charAt(pos) != ':') return null;
        pos++; // 跳过冒号
        while (pos < json.length() && (json.charAt(pos) == ' ' || json.charAt(pos) == '\t' || json.charAt(pos) == '\r' || json.charAt(pos) == '\n')) pos++;
        if (pos >= json.length() || json.charAt(pos) != '"') return null;
        pos++; // 跳过开头引号
        int end = json.indexOf("\"", pos);
        return end > pos ? json.substring(pos, end) : null;
    }

    /** 从 JSON 字符串中简单提取 long 数字字段 */
    private long extractJsonLong(String json, String key, long defaultVal) {
        if (json == null) return defaultVal;
        String search = "\"" + key + "\":";
        int idx = json.indexOf(search);
        if (idx < 0) return defaultVal;
        int start = idx + search.length();
        int end = start;
        while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '-')) end++;
        try { return Long.parseLong(json.substring(start, end)); } catch (Exception e) { return defaultVal; }
    }

    /** 截断字符串 */
    private String limitStr(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max) + "...";
    }

    /** 从 CF API 错误响应中提取错误信息 */
    private String extractCfErrorMessage(String body) {
        if (body == null || body.isEmpty()) return "未知错误";
        // 尝试提取 errors[].message
        int errIdx = body.indexOf("\"message\":\"");
        if (errIdx >= 0) {
            int start = errIdx + 11;
            int end = body.indexOf("\"", start);
            if (end > start) return body.substring(start, end);
        }
        return limitStr(body, 200);
    }

    // ===================================================================
    // GitHub Actions 部署
    // ===================================================================

    @Override
    public Map<String, Object> triggerGithubAction(Long siteId) {
        Map<String, Object> result = new HashMap<>();
        GbSite site = gbSiteMapper.selectGbSiteById(siteId);
        if (site == null) { result.put("success", false); result.put("message", "网站不存在"); return result; }
        GbSiteCodeConfig cfg = codeConfigMapper.selectBySiteId(siteId);
        if (cfg == null) { result.put("success", false); result.put("message", "请先配置部署信息"); return result; }

        // 从 deployConfig JSON 中解析 GA 配置
        String actionRepo = null, actionToken = null, actionWorkflow = "deploy.yml", actionRef = "main";
        String buildCommand = null;
        String deployConfigJson = cfg.getDeployConfig();
        if (deployConfigJson != null && !deployConfigJson.trim().isEmpty()) {
            try {
                com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();
                com.fasterxml.jackson.databind.JsonNode node = om.readTree(deployConfigJson);
                if (node.has("actionRepo") && !node.get("actionRepo").asText().trim().isEmpty()) actionRepo = node.get("actionRepo").asText().trim();
                if (node.has("actionToken") && !node.get("actionToken").asText().trim().isEmpty()) actionToken = node.get("actionToken").asText().trim();
                if (node.has("actionWorkflow") && !node.get("actionWorkflow").asText().trim().isEmpty()) actionWorkflow = node.get("actionWorkflow").asText().trim();
                if (node.has("actionRef") && !node.get("actionRef").asText().trim().isEmpty()) actionRef = node.get("actionRef").asText().trim();
                if (node.has("buildCommand") && !node.get("buildCommand").asText().trim().isEmpty()) buildCommand = node.get("buildCommand").asText().trim();
            } catch (Exception ignored) {}
        }
        if (actionRepo == null || actionRepo.isEmpty()) {
            result.put("success", false); result.put("message", "请先填写 GitHub Action 仓库的完整 URL（如 https://github.com/owner/repo.git）并保存"); return result;
        }
        String[] repoParts = extractOwnerRepo(actionRepo);
        if (repoParts == null) {
            result.put("success", false); result.put("message", "Action 仓库地址格式不正确，请填写完整 URL（如 https://github.com/owner/repo.git）"); return result;
        }
        if (actionToken == null) {
            result.put("success", false); result.put("message", "请先配置 GitHub Token（需要 workflow 权限）并保存"); return result;
        }
        // 中继模式必须配置代码仓库 URL（workflow 的 git_repo_url 字段 required: true）
        if (cfg.getGitRepoUrl() == null || cfg.getGitRepoUrl().isEmpty()) {
            result.put("success", false); result.put("message", "中继模式需要先在「Git 仓库」配置中填写代码仓库地址，当前未配置"); return result;
        }
        if (Boolean.TRUE.equals(gaRunning.get(siteId))) {
            result.put("success", false); result.put("message", "GitHub Actions 正在运行中，请稍候"); return result;
        }

        final String owner = repoParts[0], repo = repoParts[1];
        final String finalToken = actionToken, finalRef = actionRef;
        // GitHub workflow dispatch API 的 workflow_id 只接受文件名（如 deploy.yml），
        // 若 actionWorkflow 存储的是完整路径（.github/workflows/deploy.yml），需提取文件名
        String workflowId = actionWorkflow;
        if (workflowId.contains("/")) {
            workflowId = workflowId.substring(workflowId.lastIndexOf('/') + 1);
        }
        final String finalWorkflow = workflowId;

        // 初始化日志和状态
        StringBuilder logBuilder = new StringBuilder();
        gaLogs.put(siteId, logBuilder);
        gaRunning.put(siteId, true);
        gaSuccess.remove(siteId);
        gaRunIds.remove(siteId);
        logBuilder.append("正在触发 GitHub Actions 工作流...\n");
        logBuilder.append("仓库: ").append(actionRepo).append("  工作流: ").append(actionWorkflow).append("  分支: ").append(actionRef).append("\n");

        // 构建 workflow_dispatch 请求体，将非敏感参数作为 inputs 传入
        StringBuilder bodyBuilder = new StringBuilder("{\"ref\":\"").append(finalRef).append("\"");
        boolean hasInputs = false;
        StringBuilder inputsSb = new StringBuilder();
        if (buildCommand != null && !buildCommand.isEmpty()) {
            if (hasInputs) inputsSb.append(",");
            inputsSb.append("\"build_command\":\"").append(buildCommand.replace("\"", "\\\"")).append("\"");
            hasInputs = true;
        }
        String workerName = cfg.getCloudflareProjectName();
        if (workerName != null && !workerName.isEmpty()) {
            if (hasInputs) inputsSb.append(",");
            inputsSb.append("\"worker_name\":\"").append(workerName.replace("\"", "\\\"")).append("\"");
            hasInputs = true;
        }
        if (cfg.getGitRepoUrl() != null && !cfg.getGitRepoUrl().isEmpty()) {
            if (hasInputs) inputsSb.append(",");
            inputsSb.append("\"git_repo_url\":\"").append(cfg.getGitRepoUrl().replace("\"", "\\\"")).append("\"");
            hasInputs = true;
            String branch = cfg.getGitBranch() != null ? cfg.getGitBranch() : "main";
            inputsSb.append(",\"git_branch\":\"").append(branch).append("\"");
        }
        if (hasInputs) { bodyBuilder.append(",\"inputs\":{").append(inputsSb).append("}"); }
        bodyBuilder.append("}");

        try {
            String dispatchUrl = "https://api.github.com/repos/" + owner + "/" + repo + "/actions/workflows/" + finalWorkflow + "/dispatches";
            int statusCode = githubApiPost(dispatchUrl, finalToken, bodyBuilder.toString());
            if (statusCode == 204) {
                logBuilder.append("工作流触发成功，等待 GitHub 分配运行实例...\n");
                result.put("success", true);
                result.put("message", "GitHub Actions 工作流已触发，日志实时刷新中");
                result.put("starting", true);
            } else {
                logBuilder.append("触发失败，HTTP 状态码: ").append(statusCode).append("\n如果是 422，请确认工作流文件中已声明 on.workflow_dispatch\n");
                gaRunning.put(siteId, false); gaSuccess.put(siteId, false);
                scheduleLogCleanup(siteId, gaLogs, gaRunning, gaSuccess, gaRunIds);
                result.put("success", false); result.put("message", "触发失败: HTTP " + statusCode); return result;
            }
        } catch (Exception e) {
            logBuilder.append("触发异常: ").append(e.getMessage()).append("\n");
            gaRunning.put(siteId, false); gaSuccess.put(siteId, false);
            scheduleLogCleanup(siteId, gaLogs, gaRunning, gaSuccess, gaRunIds);
            result.put("success", false); result.put("message", "触发异常: " + e.getMessage()); return result;
        }

        // 启动后台监控线程
        final long triggerTimeMs = System.currentTimeMillis();
        Thread monitor = new Thread(() -> {
            try {
                // 等待 GitHub 注册运行记录，最多 60 秒

                String runId = null;
                for (int attempt = 0; attempt < 12 && runId == null; attempt++) {
                    Thread.sleep(5000);
                    runId = findLatestGhRunId(owner, repo, finalToken, triggerTimeMs);
                }
                if (runId == null) {
                    gaLogs.get(siteId).append("⚠ 未能找到对应的工作流运行，请前往 GitHub 手动确认\n");
                    gaRunning.put(siteId, false); gaSuccess.put(siteId, false); return;
                }
                gaRunIds.put(siteId, runId);
                String runUrl = "https://github.com/" + owner + "/" + repo + "/actions/runs/" + runId;
                gaLogs.get(siteId).append("运行链接: ").append(runUrl).append("\n");

                // 轮询运行状态 + 追加日志
                Map<String, Integer> jobLogOffsets = new HashMap<>();
                while (true) {
                    Thread.sleep(8000);
                    String runJson = githubApiGet("https://api.github.com/repos/" + owner + "/" + repo + "/actions/runs/" + runId, finalToken);
                    if (runJson == null) { gaLogs.get(siteId).append("[获取运行状态失败，重试中...]\n"); continue; }
                    String status = extractJsonString(runJson, "status");
                    String conclusion = extractJsonString(runJson, "conclusion");

                    // 获取 Jobs 日志
                    String jobsJson = githubApiGet("https://api.github.com/repos/" + owner + "/" + repo + "/actions/runs/" + runId + "/jobs", finalToken);
                    if (jobsJson != null) {
                        List<String> jobIds = extractGhJobIds(jobsJson);
                        for (String jobId : jobIds) {
                            String logText = githubApiGetText("https://api.github.com/repos/" + owner + "/" + repo + "/actions/jobs/" + jobId + "/logs", finalToken);
                            if (logText != null && !logText.isEmpty()) {
                                int prevOffset = jobLogOffsets.getOrDefault(jobId, 0);
                                if (logText.length() > prevOffset) {
                                    String newPart = logText.substring(prevOffset);
                                    gaLogs.get(siteId).append(stripGhTimestamps(newPart));
                                    jobLogOffsets.put(jobId, logText.length());
                                }
                            }
                        }
                    }

                    // 检查完成
                    if ("completed".equals(status)) {
                        boolean ok = "success".equals(conclusion);
                        gaLogs.get(siteId).append(ok ? "\n✅ GitHub Actions 部署成功\n" : "\n❌ 工作流结束: " + conclusion + "\n");
                        gaSuccess.put(siteId, ok); gaRunning.put(siteId, false); break;
                    }
                }
            } catch (Exception e) {
                StringBuilder lb = gaLogs.get(siteId);
                if (lb != null) lb.append("\n监控异常: ").append(e.getMessage()).append("\n");
                gaRunning.put(siteId, false); gaSuccess.put(siteId, false);
            } finally {
                scheduleLogCleanup(siteId, gaLogs, gaRunning, gaSuccess, gaRunIds);
            }
        });
        monitor.setDaemon(true); monitor.setName("ga-monitor-" + siteId); monitor.start();
        return result;
    }

    @Override
    public Map<String, Object> getGithubActionStatus(Long siteId) {
        Map<String, Object> result = new HashMap<>();
        Boolean running = gaRunning.get(siteId);
        Boolean succeeded = gaSuccess.get(siteId);
        StringBuilder logBuf = gaLogs.get(siteId);
        String runId = gaRunIds.get(siteId);
        result.put("running", Boolean.TRUE.equals(running));
        if (!Boolean.TRUE.equals(running) && succeeded != null) {
            result.put("done", true); result.put("success", succeeded);
        } else {
            result.put("done", false);
        }
        result.put("log", logBuf != null ? logBuf.toString() : "");
        result.put("runId", runId != null ? runId : "");
        return result;
    }

    /** 查找最近触发的 workflow_dispatch 运行 ID */
    private String findLatestGhRunId(String owner, String repo, String token, long triggerTimeMs) {
        try {
            String url = "https://api.github.com/repos/" + owner + "/" + repo + "/actions/runs?event=workflow_dispatch&per_page=5";
            String json = githubApiGet(url, token);
            if (json == null) return null;
            // 遍历 workflow_runs 数组，找触发时间最近的
            int idx = 0;
            while (true) {
                int objStart = json.indexOf("\"id\":", idx);
                if (objStart < 0) break;
                int numStart = objStart + 5;
                int numEnd = numStart;
                while (numEnd < json.length() && Character.isDigit(json.charAt(numEnd))) numEnd++;
                String runId = json.substring(numStart, numEnd);
                // 找 created_at
                int caIdx = json.indexOf("\"created_at\":", objStart);
                if (caIdx >= 0) {
                    String createdAt = extractJsonString(json.substring(caIdx), "created_at");
                    if (createdAt != null) {
                        try {
                            java.time.Instant created = java.time.Instant.parse(createdAt);
                            // 允许 30 秒内的误差
                            if (created.toEpochMilli() >= triggerTimeMs - 30000) return runId;
                        } catch (Exception ignored) {}
                    }
                }
                idx = numEnd;
            }
        } catch (Exception e) { log.debug("查找 GH runId 异常: {}", e.getMessage()); }
        return null;
    }

    /** 从 GitHub jobs JSON 中提取所有 job id */
    private List<String> extractGhJobIds(String json) {
        List<String> ids = new ArrayList<>();
        if (json == null) return ids;
        int idx = 0;
        while (true) {
            int idxId = json.indexOf("\"id\":", idx);
            if (idxId < 0) break;
            int numStart = idxId + 5;
            int numEnd = numStart;
            while (numEnd < json.length() && Character.isDigit(json.charAt(numEnd))) numEnd++;
            if (numEnd > numStart) ids.add(json.substring(numStart, numEnd));
            idx = numEnd;
        }
        return ids;
    }

    /** 去除 GitHub Actions 日志每行开头的 ISO 时间戳前缀 */
    private String stripGhTimestamps(String text) {
        if (text == null) return "";
        String[] lines = text.split("\n", -1);
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            // 格式: "2025-01-01T00:00:00.0000000Z ..."
            if (line.length() > 29 && line.charAt(4) == '-' && line.charAt(10) == 'T' && line.contains("Z ")) {
                int spaceIdx = line.indexOf("Z ") + 2;
                sb.append(line.substring(spaceIdx)).append("\n");
            } else {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }

    /** GitHub API — HTTP POST，返回状态码 */
    private int githubApiPost(String url, String token, String body) throws Exception {
        return execGitApi("POST", url, githubHeaders(token), body, "application/json").status;
    }

    /** GitHub API — HTTP GET，返回响应 JSON 字符串（失败返回 null） */
    private String githubApiGet(String url, String token) {
        try {
            com.ruoyi.gamebox.support.GbProxyConfigSupport.SimpleResponse resp =
                    execGitApi("GET", url, githubHeaders(token), null, null);
            return (resp.status >= 200 && resp.status < 300) ? resp.body : null;
        } catch (Exception e) { log.debug("githubApiGet 异常: {}", e.getMessage()); return null; }
    }

    /** GitHub API — 获取纯文本（如日志），Apache HC 自动跟随 302 重定向 */
    private String githubApiGetText(String url, String token) {
        try {
            com.ruoyi.gamebox.support.GbProxyConfigSupport.SimpleResponse resp =
                    execGitApi("GET", url, githubHeaders(token), null, null);
            return (resp.status == 200) ? resp.body : null;
        } catch (Exception e) { log.debug("githubApiGetText 异常: {}", e.getMessage()); return null; }
    }

    /** 解析 CF 域名列表（简单 JSON 解析，提取 id 和 hostname） */
    @SuppressWarnings("unchecked")
    private java.util.List<Map<String, Object>> parseCfDomainList(String json) {
        java.util.List<Map<String, Object>> list = new java.util.ArrayList<>();
        if (json == null) return list;
        // 在 result 数组中查找 {id, hostname, service} 对象（兼容冒号后有空格的格式）
        int resIdx = json.indexOf("\"result\"");
        if (resIdx < 0) return list;
        int arrStart = json.indexOf('[', resIdx);
        int arrEnd = json.lastIndexOf(']');
        if (arrEnd <= arrStart) return list;
        String arrPart = json.substring(arrStart + 1, arrEnd);
        // 简单分割对象
        int depth = 0;
        int objStart = -1;
        for (int i = 0; i < arrPart.length(); i++) {
            char c = arrPart.charAt(i);
            if (c == '{') { if (depth++ == 0) objStart = i; }
            else if (c == '}') {
                if (--depth == 0 && objStart >= 0) {
                    String obj = arrPart.substring(objStart, i + 1);
                    Map<String, Object> item = new HashMap<>();
                    String id = extractJsonString(obj, "id");
                    String hostname = extractJsonString(obj, "hostname");
                    String service = extractJsonString(obj, "service");
                    String env = extractJsonString(obj, "environment");
                    if (id != null) item.put("id", id);
                    if (hostname != null) item.put("hostname", hostname);
                    if (service != null) item.put("service", service);
                    if (env != null) item.put("environment", env);
                    if (!item.isEmpty()) list.add(item);
                    objStart = -1;
                }
            }
        }
        return list;
    }

    // =========================================================================
    // ============= GitHub Actions Push 模式：工作流注入 + Secret 配置 =========
    // =========================================================================

    @Override
    public Map<String, Object> setupGithubActionsPush(Long siteId) {
        Map<String, Object> result = new HashMap<>();
        try {
            GbSite site = gbSiteMapper.selectGbSiteById(siteId);
            if (site == null) { result.put("success", false); result.put("message", "网站不存在"); return result; }
            GbSiteCodeConfig cfg = codeConfigMapper.selectBySiteId(siteId);
            if (cfg == null) { result.put("success", false); result.put("message", "请先保存部署配置"); return result; }

            // ---- 解析 deployConfig ----
            String dcJson = cfg.getDeployConfig();
            String pushBranch = "main", pushWorkflow = ".github/workflows/deploy.yml";
            String installCommand = "pnpm install", buildCommand = "pnpm run build";
            if (dcJson != null && !dcJson.isEmpty()) {
                try {
                    com.fasterxml.jackson.databind.JsonNode dc =
                            new com.fasterxml.jackson.databind.ObjectMapper().readTree(dcJson);
                    if (dc.has("actionPushBranch") && !dc.get("actionPushBranch").asText().trim().isEmpty())
                        pushBranch = dc.get("actionPushBranch").asText().trim();
                    if (dc.has("actionPushWorkflow") && !dc.get("actionPushWorkflow").asText().trim().isEmpty())
                        pushWorkflow = dc.get("actionPushWorkflow").asText().trim();
                    if (dc.has("installCommand") && !dc.get("installCommand").asText().trim().isEmpty())
                        installCommand = dc.get("installCommand").asText().trim();
                    if (dc.has("buildCommand") && !dc.get("buildCommand").asText().trim().isEmpty())
                        buildCommand = dc.get("buildCommand").asText().trim();
                } catch (Exception ignored) {}
            }
            // Token 统一使用站点绑定的 Git 账号
            String pushToken = resolveGitToken(cfg);
            if (pushToken == null || pushToken.isEmpty()) {
                result.put("success", false); result.put("message", "获取 GitHub Token 失败，请在站点设置中绑定 Git 账号"); return result;
            }
            // ---- 从 gitRepoUrl 解析 owner/repo ----
            String repoUrl = cfg.getGitRepoUrl();
            if (repoUrl == null || repoUrl.isEmpty()) {
                result.put("success", false); result.put("message", "请先配置 Git 仓库 URL"); return result;
            }
            String[] ownerRepoParts = extractOwnerRepo(repoUrl); // [owner, repo]
            if (ownerRepoParts == null) {
                result.put("success", false); result.put("message", "无法从 Git URL 解析 owner/repo：" + repoUrl); return result;
            }
            String ownerRepo = ownerRepoParts[0] + "/" + ownerRepoParts[1];
            GbPlatformCfAccount pushCfAcc = resolveCfAccount(cfg);
            String cfApiToken = pushCfAcc != null ? pushCfAcc.getApiToken() : null;
            String cfAccountId = pushCfAcc != null ? pushCfAcc.getAccountId() : null;

            StringBuilder logSb = new StringBuilder();
            logSb.append("=== GitHub Actions Push 模式配置 ===\n");
            logSb.append("仓库: ").append(ownerRepo).append("  工作流文件: ").append(pushWorkflow).append("\n");

            // ① 将工作流 YAML 写入本地仓库目录（让用户自行 commit + push，而非直接推送到远程）
            String repoDir = getRepoDir(siteId, site.getCode());
            java.io.File localRepo = new java.io.File(repoDir);
            if (!localRepo.exists() || !localRepo.isDirectory()) {
                result.put("success", false);
                result.put("message", "本地仓库目录不存在（" + repoDir + "），请先在「代码管理」页面执行 Git Clone 拉取代码");
                return result;
            }
            String yaml = buildPushWorkflowYaml(pushBranch, installCommand, buildCommand);
            java.io.File workflowFile = new java.io.File(localRepo, pushWorkflow.replace("/", java.io.File.separator));
            java.io.File workflowDir = workflowFile.getParentFile();
            if (!workflowDir.exists()) workflowDir.mkdirs();
            boolean fileExisted = workflowFile.exists();
            try (java.io.OutputStreamWriter writer = new java.io.OutputStreamWriter(
                    new java.io.FileOutputStream(workflowFile), java.nio.charset.StandardCharsets.UTF_8)) {
                writer.write(yaml);
            }
            logSb.append("✓ 工作流文件已").append(fileExisted ? "更新" : "创建").append("到本地：")
                 .append(workflowFile.getAbsolutePath()).append("\n");
            logSb.append("  → 请在代码管理页面执行「提交」→「推送」将此文件同步到 GitHub\n\n");

            // ② 设置 Secrets（仍通过 GitHub API 直接写入，无需本地操作）
            boolean secretOk = true;
            if (cfApiToken != null && !cfApiToken.isEmpty()) {
                boolean ok = githubSetRepoSecret(ownerRepo, pushToken, "CF_API_TOKEN", cfApiToken, logSb);
                if (!ok) secretOk = false;
            } else {
                logSb.append("⚠ 未配置 Cloudflare API Token，跳过 CF_API_TOKEN Secret 设置\n");
            }
            if (cfAccountId != null && !cfAccountId.isEmpty()) {
                boolean ok = githubSetRepoSecret(ownerRepo, pushToken, "CF_ACCOUNT_ID", cfAccountId, logSb);
                if (!ok) secretOk = false;
            } else {
                logSb.append("⚠ 未配置 Cloudflare Account ID，跳过 CF_ACCOUNT_ID Secret 设置\n");
            }
            if (!secretOk) {
                logSb.append("\n⚠ 部分 Secret 设置失败，请确认 Token 是否具有 secrets write 权限，或手动在仓库 Settings → Secrets → Actions 中设置\n");
            }

            logSb.append("\n=== 配置完成 ===\n");
            logSb.append("下一步：在「代码管理」工具栏点击「提交」填写提交信息，再点击「推送」\n");
            logSb.append("推送成功后，每次 git push 到 ").append(pushBranch).append(" 分支将自动触发部署！\n");
            logSb.append("查看 Actions：https://github.com/").append(ownerRepo).append("/actions\n");

            result.put("success", true);
            result.put("log", logSb.toString());
            result.put("actionsUrl", "https://github.com/" + ownerRepo + "/actions");
        } catch (Exception e) {
            log.error("setupGithubActionsPush 异常: {}", e.getMessage(), e);
            result.put("success", false); result.put("message", "配置异常: " + e.getMessage());
        }
        return result;
    }

    /** 检查 GitHub Actions Push 模式已配置状态 */
    public Map<String, Object> checkGithubActionsPushStatus(Long siteId) {
        Map<String, Object> result = new HashMap<>();
        try {
            GbSite site = gbSiteMapper.selectGbSiteById(siteId);
            if (site == null) { result.put("success", false); result.put("message", "网站不存在"); return result; }
            GbSiteCodeConfig cfg = codeConfigMapper.selectBySiteId(siteId);
            if (cfg == null) {
                result.put("success", true);
                result.put("workflowExists", false); result.put("workflowContentMatch", false);
                result.put("secretApiToken", false); result.put("secretAccountId", false);
                result.put("tokenValid", null);
                return result;
            }

            // 解析 deployConfig（与 setup 保持一致）
            String dcJson = cfg.getDeployConfig();
            String pushBranch = "main", pushWorkflow = ".github/workflows/deploy.yml";
            String installCommand = "pnpm install", buildCommand = "pnpm run build";
            if (dcJson != null && !dcJson.isEmpty()) {
                try {
                    com.fasterxml.jackson.databind.JsonNode dc =
                            new com.fasterxml.jackson.databind.ObjectMapper().readTree(dcJson);
                    if (dc.has("actionPushBranch") && !dc.get("actionPushBranch").asText().trim().isEmpty())
                        pushBranch = dc.get("actionPushBranch").asText().trim();
                    if (dc.has("actionPushWorkflow") && !dc.get("actionPushWorkflow").asText().trim().isEmpty())
                        pushWorkflow = dc.get("actionPushWorkflow").asText().trim();
                    if (dc.has("installCommand") && !dc.get("installCommand").asText().trim().isEmpty())
                        installCommand = dc.get("installCommand").asText().trim();
                    if (dc.has("buildCommand") && !dc.get("buildCommand").asText().trim().isEmpty())
                        buildCommand = dc.get("buildCommand").asText().trim();
                } catch (Exception ignored) {}
            }

            // Token 统一使用站点绑定的 Git 账号
            String pushToken = resolveGitToken(cfg);

            // ① 检查本地工作流文件（存在性 + 内容匹配）
            String repoDir = getRepoDir(siteId, site.getCode());
            java.io.File wfFile = new java.io.File(repoDir + java.io.File.separator
                    + pushWorkflow.replace("/", java.io.File.separator));
            boolean wfExists = wfFile.exists();
            result.put("workflowExists", wfExists);
            // 工作流由模板自带，不与系统生成内容比较，文件存在即视为匹配
            result.put("workflowContentMatch", wfExists);

            // ② 验证 GitHub Token 有效性（GET /user）
            if (pushToken != null) {
                try {
                    String userJson = githubApiGet("https://api.github.com/user", pushToken);
                    result.put("tokenValid", userJson != null);
                } catch (Exception e) {
                    result.put("tokenValid", false);
                }
            } else {
                result.put("tokenValid", null); // 未填 token 且无绑定账号
            }

            // ③ 检查 GitHub Secrets（仅能确认存在，GitHub API 不返回实际值）
            if (pushToken != null && cfg.getGitRepoUrl() != null && !cfg.getGitRepoUrl().isEmpty()) {
                String[] parts = extractOwnerRepo(cfg.getGitRepoUrl());
                if (parts != null) {
                    String ownerRepo = parts[0] + "/" + parts[1];
                    result.put("secretApiToken",  githubSecretExists(ownerRepo, pushToken, "CF_API_TOKEN"));
                    result.put("secretAccountId", githubSecretExists(ownerRepo, pushToken, "CF_ACCOUNT_ID"));
                } else {
                    result.put("secretApiToken", false); result.put("secretAccountId", false);
                }
            } else {
                result.put("secretApiToken", null); result.put("secretAccountId", null);
            }
            result.put("success", true);
        } catch (Exception e) {
            log.warn("checkGithubActionsPushStatus 异常: {}", e.getMessage());
            result.put("success", false); result.put("message", e.getMessage());
        }
        return result;
    }

    /** 检查 GitHub 仓库 Secret 是否存在（200=存在，404=不存在） */
    private boolean githubSecretExists(String ownerRepo, String token, String secretName) {
        try {
            String url = "https://api.github.com/repos/" + ownerRepo + "/actions/secrets/" + secretName;
            return githubApiGet(url, token) != null;
        } catch (Exception e) { return false; }
    }

    /** 构造 Push 触发工作流 YAML */
    private String buildPushWorkflowYaml(String branch, String installCommand, String buildCommand) {
        return "# 自动部署工作流 - 由 GameBox 系统生成\n" +
               "# 每次推送到 " + branch + " 分支时自动触发部署到 Cloudflare\n" +
               "name: 自动部署到 Cloudflare\n\n" +
               "on:\n" +
               "  push:\n" +
               "    branches:\n" +
               "      - " + branch + "  # 监听此分支的推送事件\n" +
               "  workflow_dispatch:  # 允许在 GitHub Actions 页面手动触发\n\n" +
               "jobs:\n" +
               "  deploy:\n" +
               "    name: 构建并部署\n" +
               "    runs-on: ubuntu-latest\n" +
               "    steps:\n" +
               "      - name: 拉取代码\n" +
               "        uses: actions/checkout@v4\n\n" +
               "      - name: 安装 Node.js 20\n" +
               "        uses: actions/setup-node@v4\n" +
               "        with:\n" +
               "          node-version: '20'\n\n" +
               "      - name: 启用 Corepack（pnpm/yarn 包管理器支持）\n" +
               "        run: corepack enable\n\n" +
               "      - name: 安装项目依赖\n" +
               "        run: " + installCommand + "\n\n" +
               "      - name: 构建项目\n" +
               "        run: " + buildCommand + "\n\n" +
               "      - name: 部署到 Cloudflare（需要仓库已配置 CF_API_TOKEN / CF_ACCOUNT_ID Secret）\n" +
               "        uses: cloudflare/wrangler-action@v3\n" +
               "        with:\n" +
               "          apiToken: ${{ secrets.CF_API_TOKEN }}\n" +
               "          accountId: ${{ secrets.CF_ACCOUNT_ID }}\n";
    }

    /** 获取 GitHub 仓库文件内容（解码后的原始文本），文件不存在返回 null */
    private String getGithubFileContent(String ownerRepo, String token, String filePath) {
        try {
            String url = "https://api.github.com/repos/" + ownerRepo + "/contents/" +
                    java.net.URLEncoder.encode(filePath, "UTF-8").replace("+", "%20").replace("%2F", "/");
            String resp = githubApiGet(url, token);
            if (resp == null) return null;
            String b64 = extractJsonString(resp, "content");
            if (b64 == null) return null;
            // GitHub 在 base64 中每 60 字符插入 \n（JSON 转义序列），需去除
            b64 = b64.replace("\\n", "").replace("\n", "").replace("\r", "").replace(" ", "");
            byte[] decoded = java.util.Base64.getDecoder().decode(b64);
            return new String(decoded, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) { return null; }
    }

    /** 获取 GitHub 仓库文件的 SHA（用于更新时提供），文件不存在返回 null */
    private String getGithubFileSha(String ownerRepo, String token, String filePath) {
        try {
            String url = "https://api.github.com/repos/" + ownerRepo + "/contents/" +
                    java.net.URLEncoder.encode(filePath, "UTF-8").replace("+", "%20").replace("%2F", "/");
            String resp = githubApiGet(url, token);
            if (resp == null) return null;
            return extractJsonString(resp, "sha");
        } catch (Exception e) { return null; }
    }

    /** 创建或更新 GitHub 仓库文件（内容为 base64），errOut 用于接收 GitHub 错误响应体 */
    private boolean githubUpsertFile(String ownerRepo, String token, String filePath,
                                      String base64Content, String existingSha, String commitMessage) {
        return githubUpsertFile(ownerRepo, token, filePath, base64Content, existingSha, commitMessage, null);
    }

    private boolean githubUpsertFile(String ownerRepo, String token, String filePath,
                                      String base64Content, String existingSha, String commitMessage,
                                      StringBuilder errOut) {
        try {
            String encoded = java.net.URLEncoder.encode(filePath, "UTF-8").replace("+", "%20").replace("%2F", "/");
            String url = "https://api.github.com/repos/" + ownerRepo + "/contents/" + encoded;
            StringBuilder body = new StringBuilder("{");
            body.append("\"message\":\"").append(commitMessage.replace("\"", "\\\"")).append("\",");
            body.append("\"content\":\"").append(base64Content).append("\"");
            if (existingSha != null && !existingSha.isEmpty()) {
                body.append(",\"sha\":\"").append(existingSha).append("\"");
            }
            body.append("}");
            com.ruoyi.gamebox.support.GbProxyConfigSupport.SimpleResponse resp =
                    execGitApi("PUT", url, githubHeaders(token), body.toString(), "application/json");
            int code = resp.status;
            if (code != 200 && code != 201) {
                if (errOut != null) errOut.append(resp.body);
                log.warn("githubUpsertFile HTTP {}: {}/{} {}", code, ownerRepo, filePath, resp.body);
                return false;
            }
            return true;
        } catch (Exception e) {
            log.warn("githubUpsertFile 异常: {}", e.getMessage());
            if (errOut != null) errOut.append(e.getMessage());
            return false;
        }
    }

    /** 设置 GitHub 仓库 Secret（自动获取公钥 → NaCl sealed box 加密 → PUT） */
    private boolean githubSetRepoSecret(String ownerRepo, String token, String secretName, String secretValue, StringBuilder logSb) {
        try {
            // 1. 获取仓库公钥
            String pkUrl = "https://api.github.com/repos/" + ownerRepo + "/actions/secrets/public-key";
            String pkJson = githubApiGet(pkUrl, token);
            if (pkJson == null) {
                logSb.append("✗ ").append(secretName).append(" - 获取仓库公钥失败（Token 可能缺少 secrets 权限）\n");
                return false;
            }
            String keyId = extractJsonString(pkJson, "key_id");
            String publicKey = extractJsonString(pkJson, "key");
            if (keyId == null || publicKey == null) {
                logSb.append("✗ ").append(secretName).append(" - 公钥响应解析失败\n");
                return false;
            }
            // 2. 加密 secret（NaCl sealed box）
            String encryptedValue = naclSealedBoxEncrypt(publicKey, secretValue);
            // 3. PUT secret
            String putUrl = "https://api.github.com/repos/" + ownerRepo + "/actions/secrets/" + secretName;
            String body = "{\"encrypted_value\":\"" + encryptedValue + "\",\"key_id\":\"" + keyId + "\"}";
            int code = githubApiPut(putUrl, token, body);
            if (code == 201 || code == 204) {
                logSb.append("✓ Secret 已设置: ").append(secretName).append("\n");
                return true;
            } else {
                logSb.append("✗ ").append(secretName).append(" - PUT secret 失败 HTTP ").append(code).append("\n");
                return false;
            }
        } catch (Exception e) {
            logSb.append("✗ ").append(secretName).append(" - 异常: ").append(e.getMessage()).append("\n");
            return false;
        }
    }

    /**
     * NaCl Sealed Box 加密（用于 GitHub Actions Secrets API）
     * 算法：X25519 DH + HSalsa20 key derivation + Blake2b nonce + XSalsa20-Poly1305 secretbox
     * 依赖 Bouncy Castle 1.70（bcprov-jdk15on）
     */
    private String naclSealedBoxEncrypt(String base64RecipientKey, String plaintext) throws Exception {
        byte[] recipientPk = java.util.Base64.getDecoder().decode(base64RecipientKey);
        byte[] message = plaintext.getBytes(java.nio.charset.StandardCharsets.UTF_8);

        // 1. 生成临时 X25519 密钥对（使用 KeyPairGenerator，BC 1.60+ 均支持）
        org.bouncycastle.crypto.generators.X25519KeyPairGenerator kpg =
                new org.bouncycastle.crypto.generators.X25519KeyPairGenerator();
        kpg.init(new org.bouncycastle.crypto.KeyGenerationParameters(new java.security.SecureRandom(), 255));
        org.bouncycastle.crypto.AsymmetricCipherKeyPair kp = kpg.generateKeyPair();
        org.bouncycastle.crypto.params.X25519PrivateKeyParameters ephSk =
                (org.bouncycastle.crypto.params.X25519PrivateKeyParameters) kp.getPrivate();
        org.bouncycastle.crypto.params.X25519PublicKeyParameters ephPubParam =
                (org.bouncycastle.crypto.params.X25519PublicKeyParameters) kp.getPublic();
        byte[] ephPk = new byte[32];
        ephPubParam.encode(ephPk, 0);

        // 2. X25519 DH 共享密钥
        org.bouncycastle.crypto.agreement.X25519Agreement dh = new org.bouncycastle.crypto.agreement.X25519Agreement();
        dh.init(ephSk);
        byte[] dhShared = new byte[32];
        dh.calculateAgreement(new org.bouncycastle.crypto.params.X25519PublicKeyParameters(recipientPk, 0), dhShared, 0);

        // 3. HSalsa20 推导对称密钥：symKey = HSalsa20(dhShared, 0^16)
        byte[] symKey = hSalsa20(dhShared, new byte[16]);

        // 4. Blake2b(output=24 bytes)(ephPk || recipientPk) → nonce24
        // libsodium crypto_box_seal_nonce 使用 crypto_generichash(outlen=24)，
        // 即 Blake2b 配置输出长度为 24 字节（192 bit），而非 512-bit 再截断——两者 IV 不同！
        org.bouncycastle.crypto.digests.Blake2bDigest b2 = new org.bouncycastle.crypto.digests.Blake2bDigest(192);
        b2.update(ephPk, 0, 32);
        b2.update(recipientPk, 0, 32);
        byte[] nonce24 = new byte[24];
        b2.doFinal(nonce24, 0);

        // 5. XSalsa20: subKey = HSalsa20(symKey, nonce24[0:16]), salsa20Nonce = nonce24[16:24]
        byte[] xsalsa20Key = hSalsa20(symKey, java.util.Arrays.copyOf(nonce24, 16));
        byte[] salsa20Nonce8 = java.util.Arrays.copyOfRange(nonce24, 16, 24);

        // 6. Salsa20-Poly1305 secretbox
        byte[] secretBoxCt = salsa20Poly1305SecretBox(xsalsa20Key, salsa20Nonce8, message);

        // 7. sealed = ephPk(32) || mac(16) || ciphertext
        byte[] sealed = new byte[32 + secretBoxCt.length];
        System.arraycopy(ephPk, 0, sealed, 0, 32);
        System.arraycopy(secretBoxCt, 0, sealed, 32, secretBoxCt.length);

        return java.util.Base64.getEncoder().encodeToString(sealed);
    }

    /**
     * HSalsa20: Salsa20 20轮核心，不做最终加法，输出特定8个字 (32 bytes)
     * 输入：key(32 bytes), in(16 bytes)
     */
    private static byte[] hSalsa20(byte[] key, byte[] in16) {
        int[] x = new int[16];
        // sigma
        x[0]  = 0x61707865; x[5]  = 0x3320646e; x[10] = 0x79622d32; x[15] = 0x6b206574;
        // key
        x[1]  = sl32(key,  0); x[2]  = sl32(key,  4); x[3]  = sl32(key,  8); x[4]  = sl32(key, 12);
        x[11] = sl32(key, 16); x[12] = sl32(key, 20); x[13] = sl32(key, 24); x[14] = sl32(key, 28);
        // in16
        x[6] = sl32(in16, 0); x[7] = sl32(in16, 4); x[8] = sl32(in16, 8); x[9] = sl32(in16, 12);

        for (int i = 20; i > 0; i -= 2) {
            // column round
            x[4]  ^= rl(x[0]+x[12], 7); x[8]  ^= rl(x[4]+x[0],  9); x[12] ^= rl(x[8]+x[4],  13); x[0]  ^= rl(x[12]+x[8],  18);
            x[9]  ^= rl(x[5]+x[1],  7); x[13] ^= rl(x[9]+x[5],  9); x[1]  ^= rl(x[13]+x[9], 13); x[5]  ^= rl(x[1]+x[13],  18);
            x[14] ^= rl(x[10]+x[6], 7); x[2]  ^= rl(x[14]+x[10],9); x[6]  ^= rl(x[2]+x[14], 13); x[10] ^= rl(x[6]+x[2],   18);
            x[3]  ^= rl(x[15]+x[11],7); x[7]  ^= rl(x[3]+x[15], 9); x[11] ^= rl(x[7]+x[3],  13); x[15] ^= rl(x[11]+x[7],  18);
            // row round
            x[1]  ^= rl(x[0]+x[3],  7); x[2]  ^= rl(x[1]+x[0],  9); x[3]  ^= rl(x[2]+x[1],  13); x[0]  ^= rl(x[3]+x[2],   18);
            x[6]  ^= rl(x[5]+x[4],  7); x[7]  ^= rl(x[6]+x[5],  9); x[4]  ^= rl(x[7]+x[6],  13); x[5]  ^= rl(x[4]+x[7],   18);
            x[11] ^= rl(x[10]+x[9], 7); x[8]  ^= rl(x[11]+x[10],9); x[9]  ^= rl(x[8]+x[11], 13); x[10] ^= rl(x[9]+x[8],   18);
            x[12] ^= rl(x[15]+x[14],7); x[13] ^= rl(x[12]+x[15],9); x[14] ^= rl(x[13]+x[12],13); x[15] ^= rl(x[14]+x[13], 18);
        }
        // HSalsa20 output: x[0,5,10,15,6,7,8,9] without adding original
        byte[] out = new byte[32];
        ss32(x[0],  out, 0);  ss32(x[5],  out, 4);  ss32(x[10], out, 8);  ss32(x[15], out, 12);
        ss32(x[6],  out, 16); ss32(x[7],  out, 20); ss32(x[8],  out, 24); ss32(x[9],  out, 28);
        return out;
    }

    /** Salsa20-Poly1305 secretbox: mac(16) || ciphertext
     *  NaCl secretbox 语义：将 [0^32 || plaintext] 用 Salsa20 加密；
     *  前 32 字节 XOR 0^32 = keystream[0:32] 作 Poly1305 密钥，
     *  其余部分 = keystream[32:] XOR plaintext 为密文，MAC 附在头部。
     */
    private static byte[] salsa20Poly1305SecretBox(byte[] key32, byte[] nonce8, byte[] plaintext) {
        // 构造 [32零字节 || plaintext] 一起处理，保证 keystream 连续推进
        byte[] fullInput = new byte[32 + plaintext.length];
        System.arraycopy(plaintext, 0, fullInput, 32, plaintext.length);
        byte[] fullOutput = new byte[32 + plaintext.length];

        org.bouncycastle.crypto.engines.Salsa20Engine s20 = new org.bouncycastle.crypto.engines.Salsa20Engine();
        s20.init(true, new org.bouncycastle.crypto.params.ParametersWithIV(
                new org.bouncycastle.crypto.params.KeyParameter(key32), nonce8));
        s20.processBytes(fullInput, 0, fullInput.length, fullOutput, 0);

        // fullOutput[0:32] = keystream[0:32] XOR 0^32 = keystream[0:32] → Poly1305 key
        byte[] poly1305Key = java.util.Arrays.copyOf(fullOutput, 32);
        // fullOutput[32:] = keystream[32:] XOR plaintext → ciphertext
        byte[] ciphertext = java.util.Arrays.copyOfRange(fullOutput, 32, fullOutput.length);

        // Poly1305 MAC over ciphertext
        org.bouncycastle.crypto.macs.Poly1305 poly = new org.bouncycastle.crypto.macs.Poly1305();
        poly.init(new org.bouncycastle.crypto.params.KeyParameter(poly1305Key));
        poly.update(ciphertext, 0, ciphertext.length);
        byte[] mac = new byte[16];
        poly.doFinal(mac, 0);

        // 返回 mac || ciphertext
        byte[] result = new byte[16 + ciphertext.length];
        System.arraycopy(mac, 0, result, 0, 16);
        System.arraycopy(ciphertext, 0, result, 16, ciphertext.length);
        return result;
    }

    // ---- Salsa20 位操作辅助 ----
    private static int rl(int v, int n) { return Integer.rotateLeft(v, n); }
    private static int sl32(byte[] b, int i) { return (b[i]&0xFF)|((b[i+1]&0xFF)<<8)|((b[i+2]&0xFF)<<16)|((b[i+3]&0xFF)<<24); }
    private static void ss32(int v, byte[] b, int i) { b[i]=(byte)v; b[i+1]=(byte)(v>>8); b[i+2]=(byte)(v>>16); b[i+3]=(byte)(v>>24); }

    // ===================================================================
    // GitHub Actions 手动触发（workflow_dispatch）配置 / 状态检查
    // ===================================================================

    @Override
    public Map<String, Object> setupGithubActionsDispatch(Long siteId) {
        Map<String, Object> result = new HashMap<>();
        try {
            GbSite site = gbSiteMapper.selectGbSiteById(siteId);
            if (site == null) { result.put("success", false); result.put("message", "网站不存在"); return result; }
            GbSiteCodeConfig cfg = codeConfigMapper.selectBySiteId(siteId);
            if (cfg == null) { result.put("success", false); result.put("message", "请先保存部署配置"); return result; }

            String dcJson = cfg.getDeployConfig();
            String actionRepo = null, actionToken = null, actionWorkflow = "deploy.yml", actionRef = "main";
            String buildCommand = "pnpm run build";
            if (dcJson != null && !dcJson.isEmpty()) {
                try {
                    com.fasterxml.jackson.databind.JsonNode dc =
                            new com.fasterxml.jackson.databind.ObjectMapper().readTree(dcJson);
                    if (dc.has("actionRepo")    && !dc.get("actionRepo").asText().trim().isEmpty())    actionRepo    = dc.get("actionRepo").asText().trim();
                    if (dc.has("actionToken")   && !dc.get("actionToken").asText().trim().isEmpty())   actionToken   = dc.get("actionToken").asText().trim();
                    if (dc.has("actionWorkflow")&& !dc.get("actionWorkflow").asText().trim().isEmpty()) actionWorkflow= dc.get("actionWorkflow").asText().trim();
                    if (dc.has("actionRef")     && !dc.get("actionRef").asText().trim().isEmpty())     actionRef     = dc.get("actionRef").asText().trim();
                    if (dc.has("buildCommand")  && !dc.get("buildCommand").asText().trim().isEmpty())  buildCommand  = dc.get("buildCommand").asText().trim();
                } catch (Exception ignored) {}
            }
            if (actionRepo == null || actionRepo.isEmpty()) {
                result.put("success", false); result.put("message", "请先填写 Action 仓库的完整 URL（如 https://github.com/owner/repo.git）并保存"); return result;
            }
            String[] actionRepoParts = extractOwnerRepo(actionRepo);
            if (actionRepoParts == null) {
                result.put("success", false); result.put("message", "Action 仓库地址格式不正确，请填写完整 URL（如 https://github.com/owner/repo.git）"); return result;
            }
            String ownerRepo = actionRepoParts[0] + "/" + actionRepoParts[1];
            if (actionToken == null) {
                result.put("success", false); result.put("message", "请先填写 GitHub Token 并保存"); return result;
            }

            GbPlatformCfAccount dispatchCfAcc = resolveCfAccount(cfg);
            String cfApiToken  = dispatchCfAcc != null ? dispatchCfAcc.getApiToken() : null;
            String cfAccountId = dispatchCfAcc != null ? dispatchCfAcc.getAccountId() : null;

            StringBuilder logSb = new StringBuilder();
            logSb.append("=== GitHub Actions 手动触发模式配置（中继仓库） ===\n");
            logSb.append("工作流仓库: ").append(actionRepo).append("\n");

            // 生成 YAML（中继仓库模式）
            String yaml = buildDispatchWorkflowYaml(buildCommand, actionRef);

            // 工作流文件路径，确保不以 '/' 开头
            String wfPath = actionWorkflow.startsWith(".github/") ? actionWorkflow
                    : (".github/workflows/" + actionWorkflow);

            // 获取已有 SHA（更新文件时需要）
            String existingSha = getGithubFileSha(ownerRepo, actionToken, wfPath);
            String b64 = java.util.Base64.getEncoder().encodeToString(yaml.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder upsertErr = new StringBuilder();
            boolean upsertOk = githubUpsertFile(ownerRepo, actionToken, wfPath, b64, existingSha,
                    (existingSha != null ? "更新" : "创建") + " workflow_dispatch 工作流（由 GameBox 生成）", upsertErr);
            if (upsertOk) {
                logSb.append("✓ 工作流文件已").append(existingSha != null ? "更新" : "创建").append("到远端仓库: ").append(wfPath).append("\n");
            } else {
                logSb.append("✗ 工作流文件写入失败\n");
                if (upsertErr.length() > 0) logSb.append("  GitHub 返回: ").append(upsertErr).append("\n");
                logSb.append("  常见原因:\n");
                logSb.append("  1. Token 缺少 Contents Write 权限（需要 repo 或 public_repo scope）\n");
                logSb.append("  2. 仓库不存在或地址有误：").append(actionRepo).append("\n");
                result.put("success", false); result.put("log", logSb.toString());
                result.put("message", "工作流文件写入失败" + (upsertErr.length() > 0 ? "：" + upsertErr : "，请检查 Token 权限和仓库地址"));
                return result;
            }

            // 设置 Secrets（CF_API_TOKEN、CF_ACCOUNT_ID、GIT_ACCESS_TOKEN）
            if (cfApiToken != null && !cfApiToken.isEmpty()) {
                githubSetRepoSecret(ownerRepo, actionToken, "CLOUDFLARE_API_TOKEN", cfApiToken, logSb);
            } else { logSb.append("⚠ Cloudflare API Token 未配置，跳过 CLOUDFLARE_API_TOKEN Secret 设置\n"); }
            if (cfAccountId != null && !cfAccountId.isEmpty()) {
                githubSetRepoSecret(ownerRepo, actionToken, "CLOUDFLARE_ACCOUNT_ID", cfAccountId, logSb);
            } else { logSb.append("⚠ Cloudflare Account ID 未配置，跳过 CLOUDFLARE_ACCOUNT_ID Secret 设置\n"); }
            // 用 Git 仓库访问令牌作为 GIT_ACCESS_TOKEN（用于 git clone 私有代码仓库）
            String gitToken = resolveGitToken(cfg);
            if (gitToken != null && !gitToken.isEmpty()) {
                githubSetRepoSecret(ownerRepo, actionToken, "GIT_ACCESS_TOKEN", gitToken, logSb);
                logSb.append("  ℹ GIT_ACCESS_TOKEN 已使用 Git 仓库配置的访问令牌自动写入。\n");
            } else {
                logSb.append("⚠ Git 仓库访问令牌未配置，GIT_ACCESS_TOKEN 未设置。\n");
                logSb.append("  请在「Git 仓库」配置中填写访问令牌，然后重新执行一键配置，\n");
                logSb.append("  或手动在 Action 仓库 Settings → Secrets 中添加 GIT_ACCESS_TOKEN。\n");
            }

            logSb.append("\n=== 配置完成 ===\n");
            logSb.append("现在可以在工具栏点击 [GitHub Actions] 按鈕手动触发构建部署！\n");
            logSb.append("查看 Actions: https://github.com/").append(ownerRepo).append("/actions\n");

            result.put("success", true);
            result.put("log", logSb.toString());
            result.put("actionsUrl", "https://github.com/" + ownerRepo + "/actions");
        } catch (Exception e) {
            log.error("setupGithubActionsDispatch 异常: {}", e.getMessage(), e);
            result.put("success", false); result.put("message", "配置异常: " + e.getMessage());
        }
        return result;
    }

    @Override
    public Map<String, Object> checkGithubActionsDispatchStatus(Long siteId) {
        Map<String, Object> result = new HashMap<>();
        try {
            GbSite site = gbSiteMapper.selectGbSiteById(siteId);
            if (site == null) { result.put("success", false); result.put("message", "网站不存在"); return result; }
            GbSiteCodeConfig cfg = codeConfigMapper.selectBySiteId(siteId);
            if (cfg == null) {
                result.put("success", true);
                result.put("workflowExists", false); result.put("tokenValid", null);
                result.put("secretApiToken", false); result.put("secretAccountId", false);
                return result;
            }

            String dcJson = cfg.getDeployConfig();
            String actionRepo = null, actionToken = null, actionWorkflow = "deploy.yml", actionRef = "main";
            String buildCommand = "pnpm run build";
            if (dcJson != null && !dcJson.isEmpty()) {
                try {
                    com.fasterxml.jackson.databind.JsonNode dc =
                            new com.fasterxml.jackson.databind.ObjectMapper().readTree(dcJson);
                    if (dc.has("actionRepo")    && !dc.get("actionRepo").asText().trim().isEmpty())    actionRepo    = dc.get("actionRepo").asText().trim();
                    if (dc.has("actionToken")   && !dc.get("actionToken").asText().trim().isEmpty())   actionToken   = dc.get("actionToken").asText().trim();
                    if (dc.has("actionWorkflow")&& !dc.get("actionWorkflow").asText().trim().isEmpty()) actionWorkflow= dc.get("actionWorkflow").asText().trim();
                    if (dc.has("actionRef")     && !dc.get("actionRef").asText().trim().isEmpty())     actionRef     = dc.get("actionRef").asText().trim();
                    if (dc.has("buildCommand")  && !dc.get("buildCommand").asText().trim().isEmpty())  buildCommand  = dc.get("buildCommand").asText().trim();
                } catch (Exception ignored) {}
            }
            // 从完整仓库 URL 中提取 owner/repo（用于 GitHub API 调用）
            String ownerRepo = null;
            if (actionRepo != null && !actionRepo.isEmpty()) {
                String[] parts = extractOwnerRepo(actionRepo);
                if (parts != null) ownerRepo = parts[0] + "/" + parts[1];
            }

            // 工作流文件路径
            String wfPath = actionWorkflow.startsWith(".github/") ? actionWorkflow
                    : (".github/workflows/" + actionWorkflow);

            // 检查工作流文件是否存在（通过 Contents API）
            boolean wfExists = false;
            if (ownerRepo != null && actionToken != null) {
                wfExists = getGithubFileSha(ownerRepo, actionToken, wfPath) != null;
            }
            result.put("workflowExists", wfExists);

            // 工作流文件内容是否与当前配置一致
            if (wfExists && ownerRepo != null && actionToken != null) {
                try {
                    String existingContent = getGithubFileContent(ownerRepo, actionToken, wfPath);
                    String expectedContent = buildDispatchWorkflowYaml(buildCommand, actionRef);
                    result.put("workflowContentMatch", existingContent != null && existingContent.equals(expectedContent));
                } catch (Exception e) {
                    result.put("workflowContentMatch", false);
                }
            } else {
                result.put("workflowContentMatch", false);
            }

            // 验证 Token
            if (actionToken != null) {
                try {
                    String userJson = githubApiGet("https://api.github.com/user", actionToken);
                    result.put("tokenValid", userJson != null);
                } catch (Exception e) { result.put("tokenValid", false); }
            } else { result.put("tokenValid", null); }

            // 检查 Secrets
            if (actionToken != null && ownerRepo != null) {
                result.put("secretApiToken",      githubSecretExists(ownerRepo, actionToken, "CLOUDFLARE_API_TOKEN"));
                result.put("secretAccountId",     githubSecretExists(ownerRepo, actionToken, "CLOUDFLARE_ACCOUNT_ID"));
                result.put("secretGitAccessToken",githubSecretExists(ownerRepo, actionToken, "GIT_ACCESS_TOKEN"));
            } else {
                result.put("secretApiToken",       actionToken == null ? null : false);
                result.put("secretAccountId",      actionToken == null ? null : false);
                result.put("secretGitAccessToken", actionToken == null ? null : false);
            }
            result.put("success", true);
        } catch (Exception e) {
            log.warn("checkGithubActionsDispatchStatus 异常: {}", e.getMessage());
            result.put("success", false); result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 生成 workflow_dispatch 模式的 YAML（中继仓库模式：公开 Action 仓库 + git clone 代码仓库）
     * @param buildCommand  构建命令
     * @param branch  代码分支
     */
    private String buildDispatchWorkflowYaml(String buildCommand, String branch) {
        // 注意：buildCommand / branch 在触发时通过 workflow_dispatch inputs 传入，
        // YAML 文件本身使用固定 default，避免命令变更导致误报"内容不一致"
        return "# 中继部署工作流 - 由 GameBox 系统生成\n" +
               "# Action 仓库可为 Public 完全免费，代码仓库保持私有\n" +
               "name: 中继部署到 Cloudflare Workers\n\n" +
               "on:\n" +
               "  workflow_dispatch:\n" +
               "    inputs:\n" +
               "      build_command:\n" +
               "        description: '构建命令（触发时由系统自动传入）'\n" +
               "        required: false\n" +
               "        default: 'pnpm run build'\n" +
               "      worker_name:\n" +
               "        description: 'Worker 名称（留空则使用 wrangler.toml 配置）'\n" +
               "        required: false\n" +
               "      git_repo_url:\n" +
               "        description: '代码仓库 URL（如 https://github.com/owner/my-site）'\n" +
               "        required: true\n" +
               "      git_branch:\n" +
               "        description: '代码分支（触发时由系统自动传入）'\n" +
               "        required: false\n" +
               "        default: 'main'\n\n" +
               "jobs:\n" +
               "  deploy:\n" +
               "    name: 拉取代码并部署到 Cloudflare Workers\n" +
               "    runs-on: ubuntu-latest\n\n" +
               "    steps:\n" +
               "      - name: 拉取代码仓库\n" +
               "        run: |\n" +
               "          REPO_URL=${{ inputs.git_repo_url }}\n" +
               "          AUTH_URL=${REPO_URL/https:\\/\\//https:\\/\\/x-token:${{ secrets.GIT_ACCESS_TOKEN }}@}\n" +
               "          git clone --depth 1 --branch \"${{ inputs.git_branch || 'main' }}\" \"$AUTH_URL\" code-repo\n\n" +
               "      - name: 安装 pnpm\n" +
               "        uses: pnpm/action-setup@v4\n" +
               "        with:\n" +
               "          version: 10\n\n" +
               "      - name: 安装 Node.js\n" +
               "        uses: actions/setup-node@v4\n" +
               "        with:\n" +
               "          node-version: '20'\n" +
               "          cache: 'pnpm'\n" +
               "          cache-dependency-path: code-repo/pnpm-lock.yaml\n\n" +
               "      - name: 安装依赖\n" +
               "        working-directory: code-repo\n" +
               "        run: pnpm install\n\n" +
               "      - name: 构建项目\n" +
               "        working-directory: code-repo\n" +
               "        run: ${{ inputs.build_command || 'pnpm run build' }}\n\n" +
               "      - name: 部署到 Cloudflare\n" +
               "        working-directory: code-repo\n" +
               "        env:\n" +
               "          CLOUDFLARE_API_TOKEN: ${{ secrets.CLOUDFLARE_API_TOKEN }}\n" +
               "          CLOUDFLARE_ACCOUNT_ID: ${{ secrets.CLOUDFLARE_ACCOUNT_ID }}\n" +
               "        run: |\n" +
               "          if [ -n \"${{ inputs.worker_name }}\" ]; then\n" +
               "            npx wrangler deploy --name \"${{ inputs.worker_name }}\"\n" +
               "          else\n" +
               "            npx wrangler deploy\n" +
               "          fi\n";
    }


    /** GitHub API — HTTP PUT，返回状态码（用于创建/更新文件、设置 Secret） */
    private int githubApiPut(String url, String token, String body) throws Exception {
        return execGitApi("PUT", url, githubHeaders(token), body, "application/json").status;
    }

    // ==================== 向导式建站 ====================

    @Override
    public Map<String, Object> setupFromTemplate(Long siteId, Map<String, Object> params) {
        return setupFromTemplate(siteId, params, null);
    }

    @Override
    public Map<String, Object> setupFromTemplate(Long siteId, Map<String, Object> params, StringBuilder liveLog) {
        Map<String, Object> result = new HashMap<>();
        GbSite site = gbSiteMapper.selectGbSiteById(siteId);
        if (site == null) {
            result.put("success", false);
            result.put("message", "网站不存在");
            return result;
        }
        GbSiteCodeConfig cfg = codeConfigMapper.selectBySiteId(siteId);
        // --- 从平台账号库引导 cfg（向导新建站点时使用）---
        if (params != null && params.get("gitAccountId") != null) {
            Long gitAccountId = Long.valueOf(params.get("gitAccountId").toString());
            GbPlatformGitAccount gitAcc = platformGitAccountMapper.selectById(gitAccountId);
            if (gitAcc == null || !"1".equals(String.valueOf(gitAcc.getStatus()))) {
                result.put("success", false);
                result.put("message", "所选 Git 账号不存在或已禁用");
                return result;
            }
            if (cfg == null) {
                cfg = new GbSiteCodeConfig();
                cfg.setSiteId(siteId);
            }
            cfg.setGitProvider(gitAcc.getProvider());
            cfg.setGitAccountId(gitAccountId);
            // 标记为自动创建仓库
            params.put("autoCreateRepo", Boolean.TRUE);
            // 处理 CF 账号
            if (params.get("cfAccountId") != null) {
                Long cfAccountId = Long.valueOf(params.get("cfAccountId").toString());
                GbPlatformCfAccount cfAcc = platformCfAccountMapper.selectById(cfAccountId);
                if (cfAcc != null && "1".equals(String.valueOf(cfAcc.getStatus()))) {
                    cfg.setCfAccountId(cfAccountId);
                    String cfProjectName = (params.get("cfProjectName") != null && !params.get("cfProjectName").toString().isEmpty())
                            ? params.get("cfProjectName").toString()
                            : (params.get("repoName") != null ? params.get("repoName").toString() : site.getCode());
                    cfg.setCloudflareProjectName(cfProjectName);
                }
            }
            // 持久化 cfg
            if (cfg.getId() == null) {
                codeConfigMapper.insert(cfg);
            } else {
                codeConfigMapper.updateBySiteId(cfg);
            }
        }
        if (cfg == null) {
            result.put("success", false);
            result.put("message", "请先配置Git仓库信息，或在向导中选择平台 Git 账号");
            return result;
        }
        // --- 自由创建模式：仅建空仓库 ---
        boolean freeMode = params != null && Boolean.TRUE.equals(params.get("freeMode"));
        if (freeMode) {
            StringBuilder logBuilder = (liveLog != null) ? liveLog : new StringBuilder();
            String repoName = (params.get("repoName") != null && !params.get("repoName").toString().isEmpty())
                    ? params.get("repoName").toString() : site.getCode();
            boolean isPrivate = !"false".equalsIgnoreCase(
                    String.valueOf(params.get("repoPrivate") != null ? params.get("repoPrivate") : "true"));
            String labServer = params.get("gitLabServerUrl") != null ? params.get("gitLabServerUrl").toString() : "";
            logBuilder.append("正在创建空仓库 [").append(repoName).append("]...\n");
            String createdUrl = createRemoteRepo(cfg.getGitProvider(), labServer,
                    resolveGitToken(cfg), repoName, isPrivate, logBuilder);
            if (createdUrl != null) {
                GbSiteCodeConfig updateCfg = new GbSiteCodeConfig();
                updateCfg.setSiteId(siteId);
                updateCfg.setGitRepoUrl(createdUrl);
                codeConfigMapper.updateBySiteId(updateCfg);
                result.put("success", true);
                result.put("message", "空仓库创建成功");
            } else {
                result.put("success", false);
                result.put("message", "创建空仓库失败，请检查 Git Token 权限后在「代码管理」中重试");
            }
            result.put("log", logBuilder.toString());
            return result;
        }
        Long templateId = null;
        if (params != null && params.get("templateId") != null) {
            templateId = Long.valueOf(params.get("templateId").toString());
        } else if (site.getTemplateId() != null) {
            templateId = site.getTemplateId();
        }
        if (templateId == null) {
            result.put("success", false);
            result.put("message", "未指定站点模板");
            return result;
        }
        GbSiteTemplate template = siteTemplateMapper.selectTemplateById(templateId);
        if (template == null || template.getTemplateGitUrl() == null) {
            result.put("success", false);
            result.put("message", "模板不存在或未配置Git地址");
            return result;
        }
        StringBuilder logBuilder = (liveLog != null) ? liveLog : new StringBuilder();
        // ―― 重建模式预处理：根据用户在对话框中的选择决定仓库行为 ――
        boolean rebuildMode = params != null && Boolean.TRUE.equals(params.get("rebuildMode"));
        if (rebuildMode && params != null) {
            String paramRepoUrl = (params.get("gitRepoUrl") != null)
                    ? params.get("gitRepoUrl").toString().trim() : "";
            if ("null".equals(paramRepoUrl)) paramRepoUrl = "";
            if (!paramRepoUrl.isEmpty()) {
                // 推送到已有仓库模式：用用户填写的 URL 覆盖 cfg（可能与 DB 不同）
                cfg.setGitRepoUrl(paramRepoUrl);
                String paramBranch = (params.get("gitBranch") != null) ? params.get("gitBranch").toString().trim() : "";
                if (!paramBranch.isEmpty()) cfg.setGitBranch(paramBranch);
                GbSiteCodeConfig rebuildCfgUpdate = new GbSiteCodeConfig();
                rebuildCfgUpdate.setSiteId(siteId);
                rebuildCfgUpdate.setGitRepoUrl(paramRepoUrl);
                if (!paramBranch.isEmpty()) rebuildCfgUpdate.setGitBranch(paramBranch);
                codeConfigMapper.updateBySiteId(rebuildCfgUpdate);
                logBuilder.append("重建模式（推送到已有仓库）：目标仓库 ").append(paramRepoUrl).append("\n");
            } else {
                // 自动新建仓库模式：清除旧 URL，重走建仓流程；若新仓库名已存在则拦截报错
                String oldUrl = cfg.getGitRepoUrl();
                cfg.setGitRepoUrl(null);
                // 检查新仓库名是否已存在于 GitHub/Gitee
                String repoNameForCheck = (params.get("repoName") != null && !params.get("repoName").toString().isEmpty())
                        ? params.get("repoName").toString().replaceAll("\\.git$", "").trim()
                        : site.getCode();
                String tokenForCheck = resolveGitToken(cfg);
                if (tokenForCheck != null && !tokenForCheck.isEmpty()
                        && "github".equalsIgnoreCase(cfg.getGitProvider())) {
                    boolean existsOnGitHub = checkGitHubRepoExists(tokenForCheck, repoNameForCheck, logBuilder);
                    if (existsOnGitHub) {
                        result.put("success", false);
                        result.put("message", "仓库 [" + repoNameForCheck + "] 在您的 GitHub 账号下已存在。\n"
                                + "如需将新代码推送到该仓库，请切换为「推送到已有仓库」模式并输入仓库地址；\n"
                                + "如需创建全新仓库，请更换仓库名后重试。\n"
                                + "（原仓库地址参考：" + (oldUrl != null ? oldUrl : "未知") + "）");
                        result.put("log", logBuilder.toString());
                        return result;
                    }
                }
                logBuilder.append("重建模式（自动新建仓库）：将创建新仓库 [").append(repoNameForCheck).append("]\n");
            }
        }
        // 若未配置仓库 URL，尝试通过 API 自动创建
        if (cfg.getGitRepoUrl() == null || cfg.getGitRepoUrl().isEmpty()) {
            boolean autoCreate = Boolean.TRUE.equals(params != null ? params.get("autoCreateRepo") : null)
                    || "true".equalsIgnoreCase(String.valueOf(params != null ? params.get("autoCreateRepo") : "false"));
            String resolvedGitToken = resolveGitToken(cfg);
            if (!autoCreate || resolvedGitToken == null || resolvedGitToken.isEmpty()) {
                result.put("success", false);
                result.put("message", "请先配置Git仓库地址，或启用自动创建仓库选项");
                return result;
            }
            String repoName = (params != null && params.get("repoName") != null
                    && !params.get("repoName").toString().isEmpty())
                    ? params.get("repoName").toString() : site.getCode();
            boolean isPrivate = !"false".equalsIgnoreCase(
                    String.valueOf(params != null ? params.get("repoPrivate") : "true"));
            String labServer = (params != null && params.get("gitLabServerUrl") != null)
                    ? params.get("gitLabServerUrl").toString() : "";
            logBuilder.append("正在通过 API 创建远程仓库 [").append(repoName).append("]...\n");
            String createdUrl = createRemoteRepo(cfg.getGitProvider(), labServer,
                    resolvedGitToken, repoName, isPrivate, logBuilder);
            if (createdUrl == null) {
                result.put("success", false);
                result.put("message", "自动创建远程仓库失败，请检查 Token 权限后重试");
                result.put("log", logBuilder.toString());
                return result;
            }
            // 回写数据库
            GbSiteCodeConfig updateCfg = new GbSiteCodeConfig();
            updateCfg.setSiteId(siteId);
            updateCfg.setGitRepoUrl(createdUrl);
            codeConfigMapper.updateBySiteId(updateCfg);
            cfg.setGitRepoUrl(createdUrl);
        }
        // ―― CF 部署：在首次 push 前向 GitHub 仓库注入 Secrets ――
        if (cfg.getCfAccountId() != null) {
            String wizardGitToken = resolveGitToken(cfg);
            GbPlatformCfAccount cfWizardAcc = resolveCfAccount(cfg);
            if (cfWizardAcc != null && cfWizardAcc.getApiToken() != null && !cfWizardAcc.getApiToken().isEmpty()
                    && wizardGitToken != null && !wizardGitToken.isEmpty()
                    && cfg.getGitRepoUrl() != null && "github".equalsIgnoreCase(cfg.getGitProvider())) {
                String[] secretOwnerRepo = extractOwnerRepo(cfg.getGitRepoUrl());
                if (secretOwnerRepo != null) {
                    String ownerRepoStr = secretOwnerRepo[0] + "/" + secretOwnerRepo[1];
                    logBuilder.append("正在设置 GitHub Actions 部署密鑰 (CF_API_TOKEN / CF_ACCOUNT_ID)...\n");
                    githubSetRepoSecret(ownerRepoStr, wizardGitToken, "CF_API_TOKEN", cfWizardAcc.getApiToken(), logBuilder);
                    githubSetRepoSecret(ownerRepoStr, wizardGitToken, "CF_ACCOUNT_ID", cfWizardAcc.getAccountId(), logBuilder);
                }
            }
        }
        String repoDir = getRepoDir(site.getId(), site.getCode());
        String branch = cfg.getGitBranch() != null ? cfg.getGitBranch() : "main";
        File tmpDir = new File(profilePath + "/repos/_tmp_template_" + siteId);
        try {
            if (tmpDir.exists()) deleteDir(tmpDir);
            new File(profilePath + "/repos").mkdirs();
            logBuilder.append("正在克隆模板仓库...\n");
            String tmplBranch = template.getTemplateGitBranch() != null ? template.getTemplateGitBranch() : "main";
            // 若模板配置了 gitAccountId，使用账号凭据构造认证 URL（支持私有模板仓库）
            String tmplCloneUrl = template.getTemplateGitUrl();
            if (template.getGitAccountId() != null) {
                GbPlatformGitAccount tmplAcc = platformGitAccountMapper.selectById(template.getGitAccountId());
                if (tmplAcc != null && tmplAcc.getToken() != null && !tmplAcc.getToken().isEmpty()
                        && tmplCloneUrl != null && tmplCloneUrl.startsWith("https://")) {
                    String tmplProvider = tmplAcc.getProvider() != null ? tmplAcc.getProvider().toLowerCase() : "";
                    if ("github".equals(tmplProvider)) {
                        tmplCloneUrl = tmplCloneUrl.replace("https://", "https://x-access-token:" + tmplAcc.getToken() + "@");
                    } else {
                        tmplCloneUrl = tmplCloneUrl.replace("https://", "https://oauth2:" + tmplAcc.getToken() + "@");
                    }
                    logBuilder.append("使用账号 [").append(tmplAcc.getName()).append("] 凭据克隆私有模板仓库\n");
                }
            }
            boolean cloneOk = runCommand(new File(profilePath + "/repos"), logBuilder, 300,
                    "git", "clone", "--depth", "1", "--branch", tmplBranch,
                    tmplCloneUrl, tmpDir.getAbsolutePath());
            if (!cloneOk) {
                result.put("success", false);
                result.put("message", "克隆模板失败");
                result.put("log", logBuilder.toString());
                return result;
            }
            File srcRoot = tmpDir;
            String entryDir = template.getEntryDir();
            if (entryDir != null && !entryDir.isEmpty()) {
                srcRoot = new File(tmpDir, entryDir);
                if (!srcRoot.exists() || !srcRoot.isDirectory()) {
                    result.put("success", false);
                    result.put("message", "模板 entryDir 不存在: " + entryDir);
                    result.put("log", logBuilder.toString());
                    return result;
                }
            }
            File repoDirFile = new File(repoDir);
            if (repoDirFile.exists()) deleteDir(repoDirFile);
            repoDirFile.mkdirs();
            logBuilder.append("正在拷贝模板文件...\n");
            copyDirectory(srcRoot.toPath(), repoDirFile.toPath());
            File gitDir = new File(repoDir, ".git");
            if (gitDir.exists()) deleteDir(gitDir);
            logBuilder.append("正在初始化 Git 仓库...\n");
            runCommand(repoDirFile, logBuilder, 10, "git", "init", "-b", branch);
            runCommand(repoDirFile, new StringBuilder(), 10, "git", "config", "user.email", "codebox@gamebox.local");
            runCommand(repoDirFile, new StringBuilder(), 10, "git", "config", "user.name", "GameBox");
            String authUrl = buildAuthenticatedUrl(cfg);
            runCommand(repoDirFile, logBuilder, 10, "git", "remote", "add", "origin", authUrl);
            logBuilder.append("正在注入环境变量...\n");
            injectEnvConfig(site, cfg, repoDir, params);
            // ―― CF 部署：检测模板是否已内置工作流文件（管理员应在模板中预配置，系统不自动生成）――
            if (cfg.getCfAccountId() != null) {
                java.io.File wfFile = new java.io.File(repoDirFile,
                        ".github" + java.io.File.separator + "workflows" + java.io.File.separator + "deploy.yml");
                if (wfFile.exists()) {
                    logBuilder.append("✓ 检测到模板已内置工作流文件，将随代码一并推送: .github/workflows/deploy.yml\n");
                } else {
                    logBuilder.append("⚠ 模板未内置工作流文件，请管理员在模板中添加 .github/workflows/deploy.yml 后重试，或在「代码管理-部署配置」中手动配置工作流\n");
                }
            }
            logBuilder.append("正在提交并推送代码...\n");
            // 确保 .gitignore 包含关键排除规则，防止锁定的 native 模块文件被提交
            ensureGitignoreExcludes(repoDirFile, new String[]{"node_modules/", ".next/", ".open-next/"});
            runCommand(repoDirFile, logBuilder, 30, "git", "add", ".");
            runCommand(repoDirFile, logBuilder, 30, "git", "commit", "-m", "chore: init site from template");
            boolean pushOk;
            if (rebuildMode) {
                pushOk = runCommand(repoDirFile, logBuilder, 120, "git", "push", "-u", "origin", branch, "--force");
            } else {
                pushOk = runCommand(repoDirFile, logBuilder, 120, "git", "push", "-u", "origin", branch);
            }
            GbSite updateSite = new GbSite();
            updateSite.setId(siteId);
            updateSite.setSetupStatus(pushOk ? "code_pulled" : "template_cloned");
            if (site.getTemplateId() == null) updateSite.setTemplateId(templateId);
            gbSiteMapper.updateGbSite(updateSite);
            // ―― CF 部署：创建 Worker 项目 + 保存部署配置 ――
            if (pushOk && cfg.getCfAccountId() != null) {
                logBuilder.append("\n=== 初始化 Cloudflare Worker 项目 ===\n");
                Map<String, Object> cfCreateResult = createCloudflareWorkerProject(siteId);
                if (Boolean.TRUE.equals(cfCreateResult.get("success"))) {
                    logBuilder.append("✓ CF Worker 项目 [").append(cfg.getCloudflareProjectName()).append("] 创建成功\n");
                    String workerUrl = (String) cfCreateResult.get("workerUrl");
                    if (workerUrl != null) {
                        logBuilder.append("  预览 URL: ").append(workerUrl).append("\n");
                        result.put("workerUrl", workerUrl);
                    }
                    GbSite deployedSite = new GbSite();
                    deployedSite.setId(siteId);
                    deployedSite.setSetupStatus("deployed");
                    gbSiteMapper.updateGbSite(deployedSite);
                } else {
                    logBuilder.append("⚠ CF Worker 项目创建失败: ").append(cfCreateResult.get("message")).append("\n");
                    logBuilder.append("  请稍后在「代码管理 → 部署配置」中手动创建\n");
                }
                // 保存部署配置（deployMode=github-actions-push，token 由绑定的 Git 账号提供，无需单独存储）
                String cfBuildCmd = "pnpm run build";
                java.io.File localPkgJson = new java.io.File(getRepoDir(siteId, site.getCode()), "package.json");
                if (localPkgJson.exists()) {
                    try {
                        String pkgStr = new String(java.nio.file.Files.readAllBytes(localPkgJson.toPath()),
                                java.nio.charset.StandardCharsets.UTF_8);
                        if (pkgStr.contains("\"build:cfworkers\"")) {
                            cfBuildCmd = "pnpm run build:cfworkers";
                        }
                    } catch (Exception ignored) {}
                }
                String deployConfigJson = "{\"deployMode\":\"github-actions-push\","
                        + "\"installCommand\":\"pnpm install\",\"buildCommand\":\"" + cfBuildCmd + "\"}";
                saveDeployConfig(siteId, "cloudflare-workers", cfg.getCfAccountId(),
                        cfg.getCloudflareProjectName(), null, deployConfigJson);
                logBuilder.append("✓ 部署配置已保存（GitHub Actions 推送自动触发，构建命令: ").append(cfBuildCmd).append("）\n");
                logBuilder.append("每次 git push 到 ").append(branch).append(" 分支将自动部署到 Cloudflare Workers\n");
            }
            result.put("success", pushOk);
            result.put("message", pushOk
                    ? (cfg.getCfAccountId() != null ? "模板初始化完成，代码已推送并自动连接 Cloudflare Workers" : "模板初始化完成，代码已推送到远程仓库")
                    : "代码推送失败，请检查Git配置");
            result.put("log", logBuilder.toString());
        } catch (Exception e) {
            log.error("从模板初始化站点失败, siteId={}", siteId, e);
            result.put("success", false);
            result.put("message", "初始化异常: " + e.getMessage());
            result.put("log", logBuilder.toString());
        } finally {
            if (tmpDir.exists()) { try { deleteDir(tmpDir); } catch (Exception ignored) {} }
        }
        return result;
    }

    @Override
    public Map<String, Object> applyConfigToCode(Long siteId, Map<String, Object> extraParams) {
        Map<String, Object> result = new HashMap<>();
        GbSite site = gbSiteMapper.selectGbSiteById(siteId);
        if (site == null) {
            result.put("success", false);
            result.put("message", "网站不存在");
            return result;
        }
        GbSiteCodeConfig cfg = codeConfigMapper.selectBySiteId(siteId);
        if (cfg == null || cfg.getGitRepoUrl() == null || cfg.getGitRepoUrl().isEmpty()) {
            result.put("success", false);
            result.put("message", "请先配置Git仓库信息");
            return result;
        }
        String repoDir = getRepoDir(site.getId(), site.getCode());
        File repoDirFile = new File(repoDir);
        if (!repoDirFile.exists() || !new File(repoDir, ".git").exists()) {
            result.put("success", false);
            result.put("message", "本地代码仓库不存在，请先拉取或初始化代码");
            return result;
        }
        StringBuilder logBuilder = new StringBuilder();
        String branch = cfg.getGitBranch() != null ? cfg.getGitBranch() : "main";
        try {
            logBuilder.append("正在更新环境变量配置...\n");
            injectEnvConfig(site, cfg, repoDir, extraParams);
            runCommand(repoDirFile, new StringBuilder(), 10, "git", "config", "user.email", "codebox@gamebox.local");
            runCommand(repoDirFile, new StringBuilder(), 10, "git", "config", "user.name", "GameBox");
            runCommand(repoDirFile, new StringBuilder(), 10, "git", "remote", "set-url", "origin", buildAuthenticatedUrl(cfg));
            StringBuilder statusLog = new StringBuilder();
            runCommand(repoDirFile, statusLog, 10, "git", "status", "--porcelain");
            boolean hasChanges = Arrays.stream(statusLog.toString().split("\n"))
                    .anyMatch(l -> !l.startsWith("[") && l.length() >= 2);
            if (!hasChanges) {
                result.put("success", true);
                result.put("message", "配置未变更，无需推送");
                result.put("log", "> 工作区是干净的，配置未变更。\n");
                return result;
            }
            runCommand(repoDirFile, logBuilder, 30, "git", "add", ".");
            runCommand(repoDirFile, logBuilder, 30, "git", "commit", "-m", "chore: update site config");
            boolean pushOk = runCommand(repoDirFile, logBuilder, 120, "git", "push", "origin", branch);
            GbSite updateSite = new GbSite();
            updateSite.setId(siteId);
            updateSite.setSetupStatus("configured");
            gbSiteMapper.updateGbSite(updateSite);
            result.put("success", pushOk);
            result.put("message", pushOk ? "配置已应用并推送到远程仓库" : "配置已写入本地，但推送失败");
            result.put("log", logBuilder.toString());
        } catch (Exception e) {
            log.error("应用配置到代码失败, siteId={}", siteId, e);
            result.put("success", false);
            result.put("message", "应用配置异常: " + e.getMessage());
            result.put("log", logBuilder.toString());
        }
        return result;
    }

    @Override
    public Map<String, Object> getVisualConfig(Long siteId) {
        Map<String, Object> result = new HashMap<>();
        GbSite site = gbSiteMapper.selectGbSiteById(siteId);
        if (site == null) {
            result.put("success", false);
            result.put("message", "网站不存在");
            return result;
        }
        result.put("siteId", String.valueOf(site.getId()));
        result.put("siteName", site.getName());
        result.put("siteType", site.getSiteType());
        result.put("siteFunctionType", site.getSiteFunctionType());
        result.put("setupStatus", site.getSetupStatus());

        // 1. 加载模板的 envTemplate 定义（schema）
        List<Map<String, Object>> envSchema = new ArrayList<>();
        if (site.getTemplateId() != null) {
            GbSiteTemplate template = siteTemplateMapper.selectTemplateById(site.getTemplateId());
            if (template != null && template.getEnvTemplate() != null && !template.getEnvTemplate().isEmpty()) {
                try {
                    com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();
                    Object parsed = om.readValue(template.getEnvTemplate(), Object.class);
                    if (parsed instanceof List) {
                        envSchema = (List<Map<String, Object>>) parsed;
                    }
                } catch (Exception e) {
                    log.warn("解析模板 envTemplate 失败, templateId={}", site.getTemplateId(), e);
                }
            }
        }
        result.put("envSchema", envSchema);

        // 2. 读取 .env.production 文件的实际值
        Map<String, String> envValues = new java.util.LinkedHashMap<>();
        String repoDir = getRepoDir(site.getId(), site.getCode());
        File envFile = new File(repoDir, ".env.production");
        if (envFile.exists()) {
            try {
                envValues = parseEnvFile(envFile);
                result.put("envFileExists", true);
            } catch (Exception e) {
                log.warn("读取 .env.production 失败, siteId={}", siteId, e);
                result.put("envFileExists", false);
            }
        } else {
            result.put("envFileExists", false);
        }
        result.put("envValues", envValues);

        result.put("success", true);
        return result;
    }

    @Override
    public Map<String, Object> saveVisualConfig(Long siteId, Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        GbSite site = gbSiteMapper.selectGbSiteById(siteId);
        if (site == null) {
            result.put("success", false);
            result.put("message", "网站不存在");
            return result;
        }
        // 前端传来 envVars: {KEY: VALUE, ...}，直接作为环境变量写入
        return applyConfigToCode(siteId, params);
    }

    // ==================== 向导式建站工具方法 ====================

    /**
     * 将站点配置注入到 .env.production 和 wrangler.toml。
     * extraParams 需包含 "envVars" Map（来自环境配置面板），键值对将同步写入两个文件。
     */
    private void injectEnvConfig(GbSite site, GbSiteCodeConfig cfg, String repoDir, Map<String, Object> extraParams) throws IOException {
        Map<String, String> envVars = new java.util.LinkedHashMap<>();

        if (extraParams != null && extraParams.get("envVars") instanceof Map) {
            Map<String, Object> raw = (Map<String, Object>) extraParams.get("envVars");
            for (Map.Entry<String, Object> e : raw.entrySet()) {
                if (e.getKey() != null && e.getValue() != null) {
                    envVars.put(e.getKey(), e.getValue().toString());
                }
            }
        }

        // wrangler.toml 的 name 字段始终用 CF Worker 名称覆盖（不依赖 envVars）
        File wranglerFile = new File(repoDir, "wrangler.toml");
        String workerName = (cfg != null && cfg.getCloudflareProjectName() != null && !cfg.getCloudflareProjectName().isEmpty())
                ? cfg.getCloudflareProjectName() : site.getCode();
        if (wranglerFile.exists()) updateWranglerToml(wranglerFile, workerName, envVars);

        // 没有 envVars 时只更新 name，跳过 .env.production
        if (envVars.isEmpty()) return;

        updateEnvFile(new File(repoDir, ".env.production"), envVars);
    }

    /**
     * 更新 .env 文件中的键值对，保留原有未覆盖的行，新键追加到末尾
     */
    private void updateEnvFile(File envFile, Map<String, String> envVars) throws IOException {
        List<String> lines = new ArrayList<>();
        java.util.Set<String> handled = new java.util.HashSet<>();
        if (envFile.exists()) {
            for (String line : java.nio.file.Files.readAllLines(envFile.toPath(), java.nio.charset.StandardCharsets.UTF_8)) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) { lines.add(line); continue; }
                int eq = trimmed.indexOf('=');
                if (eq > 0) {
                    String key = trimmed.substring(0, eq).trim();
                    if (envVars.containsKey(key)) {
                        lines.add(key + "=" + envVars.get(key));
                        handled.add(key);
                    } else {
                        lines.add(line);
                    }
                } else {
                    lines.add(line);
                }
            }
        }
        for (Map.Entry<String, String> entry : envVars.entrySet()) {
            if (!handled.contains(entry.getKey())) lines.add(entry.getKey() + "=" + entry.getValue());
        }
        java.nio.file.Files.write(envFile.toPath(), lines, java.nio.charset.StandardCharsets.UTF_8);
    }

    /**
     * 更新 wrangler.toml：name 字段 + [vars] 区域写入全部环境变量
     */
    private void updateWranglerToml(File wranglerFile, String workerName, Map<String, String> envVars) throws IOException {
        List<String> lines = java.nio.file.Files.readAllLines(wranglerFile.toPath(), java.nio.charset.StandardCharsets.UTF_8);
        List<String> out = new ArrayList<>();
        boolean inVars = false;
        boolean nameWritten = false;
        boolean varsWritten = false;
        for (String line : lines) {
            String trimmed = line.trim();
            // 替换顶层 name
            if (!nameWritten && !inVars && trimmed.startsWith("name") && trimmed.contains("=")) {
                out.add("name = \"" + workerName + "\"");
                nameWritten = true;
                continue;
            }
            // 遇到 [vars] 节：写入全部 envVars 并跳过原有行
            if (trimmed.equals("[vars]")) {
                inVars = true;
                out.add(line);
                for (Map.Entry<String, String> entry : envVars.entrySet()) {
                    out.add(entry.getKey() + " = \"" + entry.getValue() + "\"");
                }
                varsWritten = true;
                continue;
            }
            // 在 [vars] 区域内，跳过原有的变量行和注释行，直到遇到下一个节
            if (inVars) {
                if (trimmed.startsWith("[")) {
                    inVars = false;
                    out.add(line);
                }
                // 跳过 [vars] 区域的旧内容（变量、注释、空行）
                continue;
            }
            out.add(line);
        }
        // 如果文件中没有 [vars] 节，追加
        if (!varsWritten && !envVars.isEmpty()) {
            out.add("");
            out.add("[vars]");
            for (Map.Entry<String, String> entry : envVars.entrySet()) {
                out.add(entry.getKey() + " = \"" + entry.getValue() + "\"");
            }
        }
        java.nio.file.Files.write(wranglerFile.toPath(), out, java.nio.charset.StandardCharsets.UTF_8);
    }

    /**
     * 解析 .env 文件为 Map<key, value>
     */
    private Map<String, String> parseEnvFile(File envFile) throws IOException {
        Map<String, String> map = new java.util.LinkedHashMap<>();
        for (String line : java.nio.file.Files.readAllLines(envFile.toPath(), java.nio.charset.StandardCharsets.UTF_8)) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) continue;
            int eq = trimmed.indexOf('=');
            if (eq > 0) map.put(trimmed.substring(0, eq).trim(), trimmed.substring(eq + 1));
        }
        return map;
    }

    /**
     * 递归拷贝目录（忽略 .git 目录）
     */
    private void copyDirectory(Path src, Path dst) throws IOException {
        java.nio.file.Files.walk(src)
                .filter(p -> {
                    String rel = src.relativize(p).toString();
                    // 只排除 .git 目录本身，保留 .github 等其他以 .git 开头的目录
                    return !rel.equals(".git")
                            && !rel.startsWith(".git" + File.separator)
                            && !rel.contains(File.separator + ".git" + File.separator)
                            && !rel.endsWith(File.separator + ".git");
                })
                .forEach(source -> {
                    Path dest = dst.resolve(src.relativize(source));
                    try {
                        if (java.nio.file.Files.isDirectory(source)) {
                            java.nio.file.Files.createDirectories(dest);
                        } else {
                            java.nio.file.Files.createDirectories(dest.getParent());
                            java.nio.file.Files.copy(source, dest, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                        }
                    } catch (IOException e) {
                        throw new java.io.UncheckedIOException(e);
                    }
                });
    }

    /**
     * 确保 .gitignore 包含指定的排除规则（追加缺失的行）
     */
    private void ensureGitignoreExcludes(File repoDir, String[] rules) {
        File gitignore = new File(repoDir, ".gitignore");
        try {
            String existing = gitignore.exists()
                    ? new String(java.nio.file.Files.readAllBytes(gitignore.toPath()), java.nio.charset.StandardCharsets.UTF_8)
                    : "";
            StringBuilder toAppend = new StringBuilder();
            for (String rule : rules) {
                if (!existing.contains(rule)) {
                    toAppend.append(rule).append("\n");
                }
            }
            if (toAppend.length() > 0) {
                try (java.io.FileWriter fw = new java.io.FileWriter(gitignore, true)) {
                    if (existing.length() > 0 && !existing.endsWith("\n")) fw.write("\n");
                    fw.write(toAppend.toString());
                }
            }
        } catch (IOException e) {
            log.warn("ensureGitignoreExcludes: 无法更新 .gitignore: {}", e.getMessage());
        }
    }

    /**
     * 递归删除目录
     * - Windows: cmd /c rmdir /s /q（正确处理 pnpm junction、只读文件、超长路径）
     * - Linux/Mac: rm -rf（快速且无路径长度限制）
     */
    private void deleteDir(File dir) throws IOException {
        if (!dir.exists()) return;
        String os = System.getProperty("os.name", "").toLowerCase();
        ProcessBuilder pb = os.contains("win")
                ? new ProcessBuilder("cmd", "/c", "rmdir", "/s", "/q", dir.getAbsolutePath())
                : new ProcessBuilder("rm", "-rf", dir.getAbsolutePath());
        pb.redirectErrorStream(true);
        try {
            Process p = pb.start();
            java.util.Scanner sc = new java.util.Scanner(p.getInputStream(), "UTF-8").useDelimiter("\\A");
            String output = sc.hasNext() ? sc.next() : "";
            boolean finished = p.waitFor(120, java.util.concurrent.TimeUnit.SECONDS);
            if (!finished) {
                p.destroyForcibly();
                log.warn("deleteDir: 命令超时，目录可能未完全删除: {}", dir);
            } else if (p.exitValue() != 0 && !output.trim().isEmpty()) {
                log.warn("deleteDir: 命令退出码 {}: {}", p.exitValue(), output.trim());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("deleteDir interrupted: " + dir, e);
        }
    }
}
