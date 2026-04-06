package com.ruoyi.web.controller.gamebox;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.gamebox.service.GbRebuildTaskService;
import com.ruoyi.gamebox.service.IGbCodeManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * 代码管理Controller
 * 负责网站Git仓库配置、本地构建和预览
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/gamebox/code-manage")
public class GbCodeManageController extends BaseController {

    @Autowired
    private IGbCodeManageService codeManageService;

    @Autowired
    private GbRebuildTaskService rebuildTaskService;

    /**
     * 获取网站代码管理信息
     */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:query')")
    @GetMapping("/{siteId}")
    public AjaxResult getInfo(@PathVariable Long siteId) {
        return success(codeManageService.getCodeManageInfo(siteId));
    }

    /**
     * 获取部署状态
     */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:query')")
    @GetMapping("/{siteId}/status")
    public AjaxResult getStatus(@PathVariable Long siteId) {
        return success(codeManageService.getDeployStatus(siteId));
    }

    /**
     * 获取本地仓库信息
     */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:query')")
    @GetMapping("/{siteId}/repo-info")
    public AjaxResult getRepoInfo(@PathVariable Long siteId) {
        return success(codeManageService.getRepoInfo(siteId));
    }

    /**
     * 异步检查本地仓库与远端同步状态（fetch + ahead/behind 计数）
     */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:query')")
    @GetMapping("/{siteId}/repo-sync-status")
    public AjaxResult getRepoSyncStatus(@PathVariable Long siteId) {
        return success(codeManageService.getRepoSyncStatus(siteId));
    }

    /**
     * 获取预览服务器状态
     */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:query')")
    @GetMapping("/{siteId}/preview/status")
    public AjaxResult getPreviewStatus(@PathVariable Long siteId) {
        return success(codeManageService.getPreviewStatus(siteId));
    }

    /**
     * 保存Git配置
     */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:edit')")
    @Log(title = "代码管理-保存Git配置", businessType = BusinessType.UPDATE)
    @PostMapping("/{siteId}/git-config")
    public AjaxResult saveGitConfig(@PathVariable Long siteId, @RequestBody Map<String, Object> params) {
        String gitProvider = (String) params.get("gitProvider");
        String gitRepoUrl = (String) params.get("gitRepoUrl");
        String gitBranch = (String) params.get("gitBranch");
        String gitToken = (String) params.get("gitToken");
        String gitAutoSync = (String) params.get("gitAutoSync");
        boolean autoCreate = Boolean.TRUE.equals(params.get("autoCreate")) || "true".equals(String.valueOf(params.get("autoCreate")));
        String repoName = (String) params.get("repoName");
        boolean repoPrivate = !"false".equalsIgnoreCase(String.valueOf(params.getOrDefault("repoPrivate", "true")));
        Long gitAccountId = params.get("gitAccountId") != null ? Long.valueOf(params.get("gitAccountId").toString()) : null;
        Map<String, Object> result = codeManageService.saveGitConfig(siteId, gitProvider, gitRepoUrl, gitBranch, gitToken, gitAutoSync, autoCreate, repoName, repoPrivate, gitAccountId);
        return Boolean.TRUE.equals(result.get("success")) ? success(result) : error((String) result.get("message"));
    }

    /**
     * 保存部署配置
     */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:edit')")
    @Log(title = "代码管理-保存部署配置", businessType = BusinessType.UPDATE)
    @PostMapping("/{siteId}/deploy-config")
    public AjaxResult saveDeployConfig(@PathVariable Long siteId, @RequestBody Map<String, Object> params) {
        String deployPlatform = (String) params.get("deployPlatform");
        Long cfAccountId = params.get("cfAccountId") != null ? Long.valueOf(params.get("cfAccountId").toString()) : null;
        String cloudflareProjectName = (String) params.get("cloudflareProjectName");
        String oldCloudflareProjectName = (String) params.get("oldCloudflareProjectName");
        String deployConfig = (String) params.get("deployConfig");
        Map<String, Object> result = codeManageService.saveDeployConfig(siteId, deployPlatform, cfAccountId, cloudflareProjectName, oldCloudflareProjectName, deployConfig);
        return Boolean.TRUE.equals(result.get("success")) ? success(result.get("message")) : error((String) result.get("message"));
    }

    /**
     * 测试Git连接
     */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:edit')")
    @Log(title = "代码管理-测试Git连接", businessType = BusinessType.OTHER)
    @PostMapping("/{siteId}/git-test")
    public AjaxResult testGitConnection(@PathVariable Long siteId,
            @RequestBody(required = false) Map<String, String> body) {
        String repoUrl = body != null ? body.get("gitRepoUrl") : null;
        String provider = body != null ? body.get("gitProvider") : null;
        String token = body != null ? body.get("gitToken") : null;
        Map<String, Object> result = codeManageService.testGitConnection(siteId, repoUrl, provider, token);
        return Boolean.TRUE.equals(result.get("success")) ? success(result) : error((String) result.get("message"));
    }

    /**
     * 拉取代码
     */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:edit')")
    @Log(title = "代码管理-拉取代码", businessType = BusinessType.OTHER)
    @PostMapping("/{siteId}/pull")
    public AjaxResult pullCode(@PathVariable Long siteId) {
        Map<String, Object> result = codeManageService.pullCode(siteId);
        // 始终返回 200，用 data.success 区分成败，保证前端可以获取到 log
        return success(result);
    }

    /**
     * 获取拉取状态和日志（前端轮询接口）
     */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:query')")
    @GetMapping("/{siteId}/pull-status")
    public AjaxResult getPullStatus(@PathVariable Long siteId) {
        Map<String, Object> result = codeManageService.getPullStatus(siteId);
        return success(result);
    }

    /**
     * 构建项目
     */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:edit')")
    @Log(title = "代码管理-构建项目", businessType = BusinessType.OTHER)
    @PostMapping("/{siteId}/build")
    public AjaxResult buildProject(@PathVariable Long siteId) {
        Map<String, Object> result = codeManageService.buildProject(siteId);
        // 始终返回 200，用 data.success 区分成败，保证前端可以获取到 log
        return success(result);
    }

    /**
     * 启动本地预览服务器
     */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:edit')")
    @Log(title = "代码管理-启动预览", businessType = BusinessType.OTHER)
    @PostMapping("/{siteId}/preview/start")
    public AjaxResult startPreview(@PathVariable Long siteId) {
        Map<String, Object> result = codeManageService.startPreviewServer(siteId);
        // 始终返回 200，用 data.success 区分成败，保证前端可以获取到 log
        return success(result);
    }

    /**
     * 停止本地预览服务器
     */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:edit')")
    @Log(title = "代码管理-停止预览", businessType = BusinessType.OTHER)
    @PostMapping("/{siteId}/preview/stop")
    public AjaxResult stopPreview(@PathVariable Long siteId) {
        Map<String, Object> result = codeManageService.stopPreviewServer(siteId);
        return Boolean.TRUE.equals(result.get("success")) ? success(result) : error((String) result.get("message"));
    }

    /**
     * 代理预览服务器请求（透明转发到本地预览端口）
     * <p>
     * 使用 @Anonymous 跳过 JWT 校验，因为浏览器加载 iframe 子资源（JS/CSS/图片）时不会携带认证头。
     * 通过检查 previewPort 是否存在来确保只有已启动的预览才可访问。
     * </p>
     */
    @Anonymous
    @GetMapping(value = "/{siteId}/preview/proxy/**")
    public void proxyPreview(@PathVariable Long siteId,
                             HttpServletRequest request,
                             HttpServletResponse response) throws IOException {
        Integer port = codeManageService.getPreviewPort(siteId);
        if (port == null) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write("预览服务器未启动，请先点击「启动预览」");
            return;
        }

        // 提取 /preview/proxy 之后的子路径
        String proxyPathPrefix = "/gamebox/code-manage/" + siteId + "/preview/proxy";
        String requestUri = request.getRequestURI();
        int idx = requestUri.indexOf(proxyPathPrefix);

        // 向 Next.js 转发「完整路径」（不剥离代理前缀）。
        // 因为 Next.js 已通过 NEXT_BASE_PATH 配置了相同的 basePath，
        // 它能自己识别并去除该前缀，正确路由到对应页面。
        String fullPath = (idx >= 0) ? requestUri.substring(idx) : proxyPathPrefix;

        // 通过 X-Forwarded-Prefix 头知道浏览器侧附加的前缀（如 /dev-api）。
        // 此前缀 仅用于重写 3xx 重定向的 Location 头。
        String forwardedPrefix = request.getHeader("X-Forwarded-Prefix");
        boolean hasForwardedPrefix = (forwardedPrefix != null && !forwardedPrefix.isEmpty());

        String queryString = request.getQueryString();
        String targetUrl = "http://127.0.0.1:" + port + fullPath
                + (queryString != null && !queryString.isEmpty() ? "?" + queryString : "");

        // SSE 路径（webpack-hmr、热更新等）需要流式转发：无读取超时 + 逐块 flush
        // 普通资源（HTML/JS/CSS）使用 30 秒超时
        boolean isSse = fullPath.contains("/_next/webpack-hmr")
                || fullPath.contains("/_next/static/webpack/");

        try {
            java.net.URL url = new java.net.URL(targetUrl);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(isSse ? 0 : 30000); // SSE 连接不设超时
            conn.setInstanceFollowRedirects(false);
            conn.setUseCaches(false); // 禁用 Java 连接内部缓存，确保每次都向 Next.js 发起真实请求
            conn.setRequestProperty("Host", "127.0.0.1:" + port);
            String accept = request.getHeader("Accept");
            if (accept != null) conn.setRequestProperty("Accept", accept);
            // 透传浏览器的缓存控制请求头，确保强制刷新时 Next.js 不返回缓存内容
            String browserCacheControl = request.getHeader("Cache-Control");
            if (browserCacheControl != null) conn.setRequestProperty("Cache-Control", browserCacheControl);
            String pragma = request.getHeader("Pragma");
            if (pragma != null) conn.setRequestProperty("Pragma", pragma);
            if (isSse) {
                // 告知 Next.js 接受 SSE 流
                conn.setRequestProperty("Accept", "text/event-stream");
                conn.setRequestProperty("Cache-Control", "no-cache");
            }

            int status;
            try {
                status = conn.getResponseCode();
            } catch (java.net.ConnectException e) {
                response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write("无法连接到预览服务器（端口 " + port + "），请稍候重试");
                return;
            }

            // 处理 3xx 重定向：将目标 URL 改写为经本代理的路径
            if (status == 301 || status == 302 || status == 307 || status == 308) {
                String location = conn.getHeaderField("Location");
                if (location != null) {
                    // 提取重定向目标的路径（去掉 127.0.0.1:port 前缀）
                    String locPath;
                    if (location.startsWith("http://127.0.0.1:" + port)
                            || location.startsWith("http://localhost:" + port)) {
                        locPath = location.replaceFirst("http://(127\\.0\\.0\\.1|localhost):" + port, "");
                    } else if (location.startsWith("/")) {
                        locPath = location;
                    } else {
                        // 其他情况（外部绝对 URL 等），直接透传
                        response.setStatus(status);
                        response.setHeader("Location", location);
                        return;
                    }
                    // locPath 已包含 basePath（Spring Boot 内部路径），
                    // 若浏览器侧有额外前缀（如 /dev-api），需拼在最前面
                    String newLocation = hasForwardedPrefix ? (forwardedPrefix + locPath) : locPath;
                    response.setStatus(status);
                    response.setHeader("Location", newLocation);
                }
                return;
            }

            response.setStatus(status);
            String contentType = conn.getContentType();
            if (contentType != null) response.setContentType(contentType);
            // HTML 响应强制禁用浏览器缓存（Spring Security cacheControl 已被禁用，需手动补充）
            if (contentType != null && contentType.contains("text/html")) {
                response.setHeader("Cache-Control", "no-store");
                response.setHeader("Pragma", "no-cache");
            }

            // 透传响应头（排除会引起问题的头；Cache-Control 已在上面单独处理，此处排除以免覆盖）
            conn.getHeaderFields().forEach((key, values) -> {
                if (key != null
                        && !key.equalsIgnoreCase("transfer-encoding")
                        && !key.equalsIgnoreCase("content-encoding")
                        && !key.equalsIgnoreCase("content-length")
                        && !key.equalsIgnoreCase("content-type")
                        && !key.equalsIgnoreCase("cache-control")
                        && !key.equalsIgnoreCase("pragma")) {
                    values.forEach(v -> response.addHeader(key, v));
                }
            });

            InputStream inputStream;
            try {
                inputStream = conn.getInputStream();
            } catch (IOException e) {
                inputStream = conn.getErrorStream();
            }
            if (inputStream == null) return;

            // 透传字节流；SSE 连接需要逐块 flush 保证浏览器实时收到事件
            byte[] buffer = new byte[8192];
            int len;
            try (InputStream is = inputStream) {
                while ((len = is.read(buffer)) != -1) {
                    response.getOutputStream().write(buffer, 0, len);
                    if (isSse) {
                        response.getOutputStream().flush();
                    }
                }
            }
        } catch (Exception e) {
            logger.error("代理预览请求失败 [siteId={}, target={}]", siteId, targetUrl, e);
            if (!response.isCommitted()) {
                response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write("代理请求失败: " + e.getMessage());
            }
        }
    }

    /**
     * 推送代码到远程仓库（git add + commit + push）
     */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:edit')")
    @Log(title = "代码管理-推送代码", businessType = BusinessType.OTHER)
    @PostMapping("/{siteId}/push")
    public AjaxResult pushCode(@PathVariable Long siteId,
            @RequestBody(required = false) Map<String, String> body) {
        String commitMessage = body != null ? body.get("commitMessage") : null;
        Map<String, Object> result = codeManageService.pushCode(siteId, commitMessage);
        return success(result);
    }

    /**
     * 获取推送状态和日志（前端轮询接口）
     */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:query')")
    @GetMapping("/{siteId}/push-status")
    public AjaxResult getPushStatus(@PathVariable Long siteId) {
        Map<String, Object> result = codeManageService.getPushStatus(siteId);
        return success(result);
    }

    /**
     * 安装项目依赖（pnpm install）
     */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:edit')")
    @Log(title = "代码管理-安装依赖", businessType = BusinessType.OTHER)
    @PostMapping("/{siteId}/install")
    public AjaxResult installDependencies(@PathVariable Long siteId) {
        Map<String, Object> result = codeManageService.installDependencies(siteId);
        return success(result);
    }

    /**
     * 获取安装依赖状态和日志（前端轮询接口）
     */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:query')")
    @GetMapping("/{siteId}/install-status")
    public AjaxResult getInstallStatus(@PathVariable Long siteId) {
        Map<String, Object> result = codeManageService.getInstallStatus(siteId);
        return success(result);
    }

    /**
     * 获取仓库文件列表
     */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:query')")
    @GetMapping("/{siteId}/files")
    public AjaxResult listFiles(@PathVariable Long siteId,
                                @RequestParam(required = false, defaultValue = "") String path) {
        Map<String, Object> result = codeManageService.listFiles(siteId, path);
        return Boolean.TRUE.equals(result.get("success")) ? success(result) : error((String) result.get("message"));
    }

    /**
     * 读取文件内容
     */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:query')")
    @GetMapping("/{siteId}/file")
    public AjaxResult readFile(@PathVariable Long siteId,
                               @RequestParam String path) {
        Map<String, Object> result = codeManageService.readFile(siteId, path);
        return Boolean.TRUE.equals(result.get("success")) ? success(result) : error((String) result.get("message"));
    }

    /**
     * 保存文件内容
     */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:edit')")
    @Log(title = "代码管理-保存文件", businessType = BusinessType.UPDATE)
    @PutMapping("/{siteId}/file")
    public AjaxResult saveFile(@PathVariable Long siteId, @RequestBody Map<String, Object> params) {
        String path = (String) params.get("path");
        String content = (String) params.get("content");
        if (path == null || path.isEmpty()) return error("文件路径不能为空");
        Map<String, Object> result = codeManageService.saveFile(siteId, path, content != null ? content : "");
        return Boolean.TRUE.equals(result.get("success")) ? success(result) : error((String) result.get("message"));
    }

    // ================== 文件管理增强接口 ==================

    /**
     * 删除文件或目录
     */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:edit')")
    @Log(title = "代码管理-删除文件", businessType = BusinessType.DELETE)
    @DeleteMapping("/{siteId}/file")
    public AjaxResult deleteFile(@PathVariable Long siteId, @RequestParam String path) {
        if (path == null || path.isEmpty()) return error("路径不能为空");
        Map<String, Object> result = codeManageService.deleteFile(siteId, path);
        return Boolean.TRUE.equals(result.get("success")) ? success(result) : error((String) result.get("message"));
    }

    /**
     * 重命名/移动文件或目录
     */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:edit')")
    @Log(title = "代码管理-重命名文件", businessType = BusinessType.UPDATE)
    @PostMapping("/{siteId}/file/rename")
    public AjaxResult renameFile(@PathVariable Long siteId, @RequestBody Map<String, String> params) {
        String oldPath = params.get("oldPath");
        String newPath = params.get("newPath");
        if (oldPath == null || oldPath.isEmpty() || newPath == null || newPath.isEmpty()) return error("路径参数不能为空");
        Map<String, Object> result = codeManageService.renameFile(siteId, oldPath, newPath);
        return Boolean.TRUE.equals(result.get("success")) ? success(result) : error((String) result.get("message"));
    }

    /**
     * 复制文件或目录
     */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:edit')")
    @Log(title = "代码管理-复制文件", businessType = BusinessType.UPDATE)
    @PostMapping("/{siteId}/file/copy")
    public AjaxResult copyFile(@PathVariable Long siteId, @RequestBody Map<String, String> params) {
        String sourcePath = params.get("sourcePath");
        String destPath = params.get("destPath");
        if (sourcePath == null || sourcePath.isEmpty() || destPath == null || destPath.isEmpty()) return error("路径参数不能为空");
        Map<String, Object> result = codeManageService.copyFile(siteId, sourcePath, destPath);
        return Boolean.TRUE.equals(result.get("success")) ? success(result) : error((String) result.get("message"));
    }

    /**
     * 新建文件或目录
     */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:edit')")
    @Log(title = "代码管理-新建文件", businessType = BusinessType.INSERT)
    @PostMapping("/{siteId}/file/create")
    public AjaxResult createFile(@PathVariable Long siteId, @RequestBody Map<String, Object> params) {
        String path = (String) params.get("path");
        Boolean isDirectory = (Boolean) params.get("isDirectory");
        if (path == null || path.isEmpty()) return error("路径不能为空");
        Map<String, Object> result = codeManageService.createFile(siteId, path, Boolean.TRUE.equals(isDirectory));
        return Boolean.TRUE.equals(result.get("success")) ? success(result) : error((String) result.get("message"));
    }

    /**
     * 上传文件到指定目录
     */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:edit')")
    @Log(title = "代码管理-上传文件", businessType = BusinessType.INSERT)
    @PostMapping("/{siteId}/file/upload")
    public AjaxResult uploadFile(@PathVariable Long siteId,
                                 @RequestParam(required = false, defaultValue = "") String dirPath,
                                 @RequestParam("file") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return error("文件不能为空");
        Map<String, Object> result = codeManageService.uploadFile(siteId, dirPath,
                file.getOriginalFilename(), file.getBytes());
        return Boolean.TRUE.equals(result.get("success")) ? success(result) : error((String) result.get("message"));
    }

    // ================== Git 增强接口 ==================

    /**
     * 获取 git status
     */
    /**
     * 获取 git 状态
     */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:query')")
    @GetMapping("/{siteId}/git/status")
    public AjaxResult getGitStatus(@PathVariable Long siteId) {
        Map<String, Object> result = codeManageService.getGitStatus(siteId);
        return Boolean.TRUE.equals(result.get("success")) ? success(result) : error((String) result.get("message"));
    }

    /** 晨2件到暂存区 */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:edit')")
    @PostMapping("/{siteId}/git/stage")
    public AjaxResult gitStage(@PathVariable Long siteId,
                                @RequestParam(required = false, defaultValue = "") String path) {
        Map<String, Object> result = codeManageService.gitStage(siteId, path);
        return Boolean.TRUE.equals(result.get("success")) ? success(result) : error((String) result.get("message"));
    }

    /** 取消暂存 */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:edit')")
    @PostMapping("/{siteId}/git/unstage")
    public AjaxResult gitUnstage(@PathVariable Long siteId,
                                  @RequestParam(required = false, defaultValue = "") String path) {
        Map<String, Object> result = codeManageService.gitUnstage(siteId, path);
        return Boolean.TRUE.equals(result.get("success")) ? success(result) : error((String) result.get("message"));
    }

    /** 提交已暂存的更改 */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:edit')")
    @PostMapping("/{siteId}/git/commit")
    public AjaxResult gitCommit(@PathVariable Long siteId,
                                 @RequestBody(required = false) Map<String, String> body) {
        String message = body != null ? body.get("message") : null;
        Map<String, Object> result = codeManageService.gitCommit(siteId, message);
        return Boolean.TRUE.equals(result.get("success")) ? success(result) : error((String) result.get("message"));
    }

    /** 仅推送（不做 add/commit） */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:edit')")
    @PostMapping("/{siteId}/git/push-only")
    public AjaxResult gitPushOnly(@PathVariable Long siteId) {
        Map<String, Object> result = codeManageService.gitPushOnly(siteId);
        return Boolean.TRUE.equals(result.get("success")) ? success(result) : error((String) result.get("message"));
    }

    /** 放弃文件更改 */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:edit')")
    @PostMapping("/{siteId}/git/discard")
    public AjaxResult gitDiscard(@PathVariable Long siteId,
                                  @RequestParam String path,
                                  @RequestParam(required = false, defaultValue = "false") boolean untracked) {
        Map<String, Object> result = codeManageService.gitDiscard(siteId, path, untracked);
        return Boolean.TRUE.equals(result.get("success")) ? success(result) : error((String) result.get("message"));
    }

    /** 列出 git 分支；includeRemote=false 时跳过 ls-remote 网络请求（默认快速模式） */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:query')")
    @GetMapping("/{siteId}/git/branches")
    public AjaxResult gitListBranches(@PathVariable Long siteId,
                                      @RequestParam(defaultValue = "false") boolean includeRemote) {
        Map<String, Object> result = codeManageService.gitListBranches(siteId, includeRemote);
        return Boolean.TRUE.equals(result.get("success")) ? success(result) : error((String) result.get("message"));
    }

    /** 切换分支 */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:edit')")
    @PostMapping("/{siteId}/git/checkout")
    public AjaxResult gitCheckoutBranch(@PathVariable Long siteId,
                                        @RequestBody Map<String, String> body) {
        String branch = body != null ? body.get("branch") : null;
        if (branch == null || branch.trim().isEmpty()) return error("分支名不能为空");
        Map<String, Object> result = codeManageService.gitCheckoutBranch(siteId, branch);
        return Boolean.TRUE.equals(result.get("success")) ? success(result) : error((String) result.get("message"));
    }

    /** 新建分支（git checkout -b） */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:edit')")
    @PostMapping("/{siteId}/git/create-branch")
    public AjaxResult gitCreateBranch(@PathVariable Long siteId,
                                      @RequestBody Map<String, String> body) {
        String branch = body != null ? body.get("branch") : null;
        if (branch == null || branch.trim().isEmpty()) return error("分支名不能为空");
        Map<String, Object> result = codeManageService.gitCreateBranch(siteId, branch);
        return Boolean.TRUE.equals(result.get("success")) ? success(result) : error((String) result.get("message"));
    }

    /** 删除本地分支 */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:edit')")
    @PostMapping("/{siteId}/git/delete-branch")
    public AjaxResult gitDeleteBranch(@PathVariable Long siteId,
                                      @RequestBody Map<String, Object> body) {
        String branch = body != null ? (String) body.get("branch") : null;
        boolean force = body != null && Boolean.TRUE.equals(body.get("force"));
        if (branch == null || branch.trim().isEmpty()) return error("分支名不能为空");
        Map<String, Object> result = codeManageService.gitDeleteBranch(siteId, branch, force);
        // 始终返回 200，由前端根据 success 字段决定是否弹二次确认（未合并分支强制删除）
        return success(result);
    }

    /** 同步远端分支信息 */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:edit')")
    @PostMapping("/{siteId}/git/fetch")
    public AjaxResult gitFetch(@PathVariable Long siteId) {
        Map<String, Object> result = codeManageService.gitFetch(siteId);
        return success(result);
    }

    /** 删除远端分支 */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:edit')")
    @PostMapping("/{siteId}/git/delete-remote-branch")
    public AjaxResult gitDeleteRemoteBranch(@PathVariable Long siteId,
                                             @RequestBody Map<String, Object> body) {
        String branch = body != null ? (String) body.get("branch") : null;
        if (branch == null || branch.trim().isEmpty()) return error("分支名不能为空");
        Map<String, Object> result = codeManageService.gitDeleteRemoteBranch(siteId, branch);
        return success(result);
    }

    /** 推送指定分支到远端 */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:edit')")
    @PostMapping("/{siteId}/git/push-branch")
    public AjaxResult gitPushBranch(@PathVariable Long siteId,
                                    @RequestBody Map<String, String> body) {
        String branch = body != null ? body.get("branch") : null;
        if (branch == null || branch.trim().isEmpty()) return error("分支名不能为空");
        Map<String, Object> result = codeManageService.gitPushBranch(siteId, branch);
        return Boolean.TRUE.equals(result.get("success")) ? success(result) : error((String) result.get("message"));
    }

    /** 强制推送指定分支到远端（git push --force） */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:edit')")
    @PostMapping("/{siteId}/git/force-push")
    public AjaxResult gitForcePush(@PathVariable Long siteId,
                                   @RequestBody Map<String, String> body) {
        String branch = body != null ? body.get("branch") : null;
        if (branch == null || branch.trim().isEmpty()) return error("分支名不能为空");
        Map<String, Object> result = codeManageService.gitForcePush(siteId, branch);
        return Boolean.TRUE.equals(result.get("success")) ? success(result) : error((String) result.get("message"));
    }

    /** 将指定分支合并到当前分支（git merge &lt;branch&gt;） */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:edit')")
    @Log(title = "代码管理-合并分支", businessType = BusinessType.UPDATE)
    @PostMapping("/{siteId}/git/merge-branch")
    public AjaxResult gitMergeBranch(@PathVariable Long siteId,
                                     @RequestBody Map<String, String> body) {
        String branch = body != null ? body.get("branch") : null;
        if (branch == null || branch.trim().isEmpty()) return error("分支名不能为空");
        Map<String, Object> result = codeManageService.gitMergeBranch(siteId, branch);
        // 冲突时也返回 200，由前端根据 conflict 字段决定处理方式
        return success(result);
    }

    /** 放弃当前合并（git merge --abort） */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:edit')")
    @Log(title = "代码管理-放弃合并", businessType = BusinessType.UPDATE)
    @PostMapping("/{siteId}/git/merge-abort")
    public AjaxResult gitMergeAbort(@PathVariable Long siteId) {
        Map<String, Object> result = codeManageService.gitMergeAbort(siteId);
        return Boolean.TRUE.equals(result.get("success")) ? success(result) : error((String) result.get("message"));
    }

    /** 检出到指定提交（Detached HEAD） */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:edit')")
    @PostMapping("/{siteId}/git/checkout-commit")
    public AjaxResult gitCheckoutCommit(@PathVariable Long siteId, @RequestBody Map<String, String> body) {
        String hash = body != null ? body.get("hash") : null;
        if (hash == null || hash.trim().isEmpty()) return error("Hash 不能为空");
        Map<String, Object> result = codeManageService.gitCheckoutCommit(siteId, hash);
        return success(result);
    }

    /** 基于指定提交创建分支 */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:edit')")
    @PostMapping("/{siteId}/git/branch-from-commit")
    public AjaxResult gitBranchFromCommit(@PathVariable Long siteId, @RequestBody Map<String, String> body) {
        String hash = body != null ? body.get("hash") : null;
        String branch = body != null ? body.get("branch") : null;
        if (hash == null || hash.trim().isEmpty()) return error("Hash 不能为空");
        if (branch == null || branch.trim().isEmpty()) return error("分支名不能为空");
        Map<String, Object> result = codeManageService.gitBranchFromCommit(siteId, hash, branch);
        return success(result);
    }

    /** 合并指定提交到当前分支 */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:edit')")
    @PostMapping("/{siteId}/git/merge-commit")
    public AjaxResult gitMergeCommit(@PathVariable Long siteId, @RequestBody Map<String, String> body) {
        String hash = body != null ? body.get("hash") : null;
        if (hash == null || hash.trim().isEmpty()) return error("Hash 不能为空");
        Map<String, Object> result = codeManageService.gitMergeCommit(siteId, hash);
        return success(result);
    }

    /** Revert 指定提交 */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:edit')")
    @PostMapping("/{siteId}/git/revert-commit")
    public AjaxResult gitRevertCommit(@PathVariable Long siteId, @RequestBody Map<String, String> body) {
        String hash = body != null ? body.get("hash") : null;
        if (hash == null || hash.trim().isEmpty()) return error("Hash 不能为空");
        Map<String, Object> result = codeManageService.gitRevertCommit(siteId, hash);
        return success(result);
    }

    /** 清空指定提交之后的所有历史（reset --hard + force push） */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:edit')")
    @PostMapping("/{siteId}/git/reset-force-push")
    public AjaxResult gitResetHardForcePush(@PathVariable Long siteId, @RequestBody Map<String, String> body) {
        String hash = body != null ? body.get("hash") : null;
        if (hash == null || hash.trim().isEmpty()) return error("Hash 不能为空");
        Map<String, Object> result = codeManageService.gitResetHardForcePush(siteId, hash);
        return success(result);
    }

    /**
     * 获取 git diff
     */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:query')")
    @GetMapping("/{siteId}/git/diff")
    public AjaxResult getGitDiff(@PathVariable Long siteId,
                                  @RequestParam(required = false, defaultValue = "") String path) {
        Map<String, Object> result = codeManageService.getGitDiff(siteId, path);
        return Boolean.TRUE.equals(result.get("success")) ? success(result) : error((String) result.get("message"));
    }

    /**
     * 获取 git log
     */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:query')")
    @GetMapping("/{siteId}/git/log")
    public AjaxResult getGitLog(@PathVariable Long siteId,
                                 @RequestParam(required = false, defaultValue = "50") int limit,
                                 @RequestParam(required = false, defaultValue = "") String branch,
                                 @RequestParam(required = false, defaultValue = "false") boolean includeRemote) {
        Map<String, Object> result = codeManageService.getGitLog(siteId, limit, branch, includeRemote);
        return Boolean.TRUE.equals(result.get("success")) ? success(result) : error((String) result.get("message"));
    }

    /**
     * 获取项目 node_modules 类型声明（供 Monaco 语言服务使用）
     */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:query')")
    @GetMapping("/{siteId}/node-types")
    public AjaxResult getNodeTypes(@PathVariable Long siteId) {
        Map<String, Object> result = codeManageService.getNodeTypes(siteId);
        return Boolean.TRUE.equals(result.get("success")) ? success(result) : error((String) result.get("message"));
    }

    /**
     * 获取文件在 HEAD 版本的原始内容（供前端 diff 对比使用）
     */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:query')")
    @GetMapping("/{siteId}/git/original")
    public AjaxResult getGitOriginal(@PathVariable Long siteId,
                                      @RequestParam String path) {
        Map<String, Object> result = codeManageService.getGitOriginal(siteId, path);
        return Boolean.TRUE.equals(result.get("success")) ? success(result) : error((String) result.get("message"));
    }

    // ====================================================================
    // =================== Cloudflare Workers 部署 ========================
    // ====================================================================

    /**
     * 部署到 Cloudflare Workers（上传构建好的 worker.js 脚本）
     */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:edit')")
    @Log(title = "CF Workers-部署", businessType = BusinessType.OTHER)
    @PostMapping("/{siteId}/deploy-workers")
    public AjaxResult deployToWorkers(@PathVariable Long siteId) {
        Map<String, Object> result = codeManageService.deployToWorkers(siteId);
        return success(result); // 始终返回 200，由 data.success 区分
    }

    /**
     * 获取账号下所有 Cloudflare Zone 列表（供域名绑定时下拉选择）
     */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:query')")
    @GetMapping("/{siteId}/cf-zones")
    public AjaxResult listCfZones(@PathVariable Long siteId) {
        return success(codeManageService.listCfZones(siteId));
    }

    /**
     * 获取 Worker 已绑定的自定义域名列表
     */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:query')")
    @GetMapping("/{siteId}/worker-domains")
    public AjaxResult listWorkerDomains(@PathVariable Long siteId) {
        Map<String, Object> result = codeManageService.listWorkerDomains(siteId);
        return success(result); // 始终返回 200，由 data.success 区分成败
    }

    /**
     * 绑定自定义域名到 Worker
     */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:edit')")
    @Log(title = "CF Workers-绑定域名", businessType = BusinessType.OTHER)
    @PostMapping("/{siteId}/worker-domains")
    public AjaxResult bindWorkerDomain(@PathVariable Long siteId,
                                        @RequestBody Map<String, String> body) {
        String hostname = body.get("hostname");
        String zoneName = body.get("zoneName");
        Map<String, Object> result = codeManageService.bindWorkerDomain(siteId, hostname, zoneName);
        return success(result); // 始终返回 200，由 data.success 区分成败
    }

    /**
     * 解绑自定义域名（通过 domain id）
     */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:edit')")
    @Log(title = "CF Workers-解绑域名", businessType = BusinessType.OTHER)
    @DeleteMapping("/{siteId}/worker-domains/{domainId}")
    public AjaxResult unbindWorkerDomain(@PathVariable Long siteId,
                                          @PathVariable String domainId) {
        Map<String, Object> result = codeManageService.unbindWorkerDomain(siteId, domainId);
        return success(result); // 始终返回 200，由 data.success 区分成败
    }

    /**
     * 验证 Cloudflare API Token 与账户 ID 是否有效
     */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:query')")
    @GetMapping("/{siteId}/cf-verify")
    public AjaxResult verifyCfCredentials(@PathVariable Long siteId) {
        Map<String, Object> result = codeManageService.verifyCfCredentials(siteId);
        return success(result);
    }

    /**
     * 在 Cloudflare 中创建 Workers 项目（无需去 CF 控制台手动创建，上传占位脚本即可初始化）
     */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:edit')")
    @Log(title = "CF Workers-创建项目", businessType = BusinessType.OTHER)
    @PostMapping("/{siteId}/create-cf-project")
    public AjaxResult createCfWorkerProject(@PathVariable Long siteId) {
        Map<String, Object> result = codeManageService.createCloudflareWorkerProject(siteId);
        return success(result);
    }

    /**
     * 检查 Cloudflare Workers 项目是否已存在
     */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:query')")
    @GetMapping("/{siteId}/check-cf-project")
    public AjaxResult checkCfWorkerProject(@PathVariable Long siteId) {
        Map<String, Object> result = codeManageService.checkCloudflareWorkerProject(siteId);
        return success(result);
    }

    /**
     * 删除 Cloudflare Workers 项目脚本
     */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:edit')")
    @Log(title = "CF Workers-删除项目", businessType = BusinessType.DELETE)
    @DeleteMapping("/{siteId}/delete-cf-project")
    public AjaxResult deleteCfWorkerProject(@PathVariable Long siteId) {
        Map<String, Object> result = codeManageService.deleteCloudflareWorkerProject(siteId);
        return success(result);
    }

    /**
     * 获取构建日志及运行状态（前端轮询接口）
     */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:query')")
    @GetMapping("/{siteId}/build-status")
    public AjaxResult getBuildStatus(@PathVariable Long siteId) {
        Map<String, Object> result = codeManageService.getBuildStatus(siteId);
        return success(result);
    }

    /**
     * 触发 GitHub Actions 工作流
     */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:edit')")
    @PostMapping("/{siteId}/trigger-github-action")
    public AjaxResult triggerGithubAction(@PathVariable Long siteId) {
        Map<String, Object> result = codeManageService.triggerGithubAction(siteId);
        return success(result);
    }

    /**
     * 获取 GitHub Actions 运行状态及日志
     */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:query')")
    @GetMapping("/{siteId}/github-action-status")
    public AjaxResult getGithubActionStatus(@PathVariable Long siteId) {
        Map<String, Object> result = codeManageService.getGithubActionStatus(siteId);
        return success(result);
    }
    /**
     * 为当前仓库配置「推送自动触发」GitHub Actions：
     * ① 注入 .github/workflows/{workflow}.yml
     * ② 通过 GitHub API 设置 CF_API_TOKEN / CF_ACCOUNT_ID Secret
     */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:edit')")
    @Log(title = "GA push-触发配置", businessType = BusinessType.OTHER)
    @PostMapping("/{siteId}/github-actions-push/setup")
    public AjaxResult setupGithubActionsPush(@PathVariable Long siteId) {
        Map<String, Object> result = codeManageService.setupGithubActionsPush(siteId);
        return success(result);
    }

    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:query')")
    @GetMapping("/{siteId}/github-actions-push/status")
    public AjaxResult checkGithubActionsPushStatus(@PathVariable Long siteId) {
        Map<String, Object> result = codeManageService.checkGithubActionsPushStatus(siteId);
        return success(result);
    }

    /**
     * 注入 workflow_dispatch 工作流文件到远端 actionRepo（手动触发模式 Setup）
     */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:edit')")
    @Log(title = "GA dispatch-工作流配置", businessType = BusinessType.OTHER)
    @PostMapping("/{siteId}/github-actions-dispatch/setup")
    public AjaxResult setupGithubActionsDispatch(@PathVariable Long siteId) {
        Map<String, Object> result = codeManageService.setupGithubActionsDispatch(siteId);
        return success(result);
    }

    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:query')")
    @GetMapping("/{siteId}/github-actions-dispatch/status")
    public AjaxResult checkGithubActionsDispatchStatus(@PathVariable Long siteId) {
        Map<String, Object> result = codeManageService.checkGithubActionsDispatchStatus(siteId);
        return success(result);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 向导式建站相关端点
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * 从模板初始化站点代码（异步）
     * 流程：克隆模板仓库 → 注入环境变量 → 推送到用户 Git → 更新 setup_status=code_pulled
     * 接口立即返回 taskId，前端通过 /{siteId}/rebuild-status?taskId=xxx 轮询进度。
     */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:edit')")
    @Log(title = "建站向导-从模板初始化代码", businessType = BusinessType.OTHER)
    @PostMapping("/{siteId}/setup-from-template")
    public AjaxResult setupFromTemplate(@PathVariable Long siteId, @RequestBody Map<String, Object> params) {
        String taskId = rebuildTaskService.submit(siteId, params);
        return success(java.util.Collections.singletonMap("taskId", taskId));
    }

    /**
     * 查询重建任务状态（前端轮询接口）
     * 返回字段：status(RUNNING/SUCCESS/FAILED)、log、message
     */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:edit')")
    @GetMapping("/{siteId}/rebuild-status")
    public AjaxResult getRebuildStatus(@PathVariable Long siteId, @RequestParam String taskId) {
        GbRebuildTaskService.RebuildTask task = rebuildTaskService.getTask(taskId);
        if (task == null) {
            return error("任务不存在，可能已过期或服务已重启");
        }
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("taskId",  task.taskId);
        data.put("status",  task.status);
        data.put("log",     task.getLog());
        data.put("message", task.message);
        data.put("finished", task.isFinished());
        data.put("success",  "SUCCESS".equals(task.status));
        return success(data);
    }

    /**
     * 将数据库中的站点配置（域名、API地址、站点ID等）写入代码文件，然后 commit + push
     * 调用方无需手动编辑任何代码文件
     */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:edit')")
    @Log(title = "建站向导-应用站点配置到代码", businessType = BusinessType.UPDATE)
    @PostMapping("/{siteId}/apply-config")
    public AjaxResult applyConfig(@PathVariable Long siteId, @RequestBody(required = false) Map<String, Object> extraParams) {
        Map<String, Object> result = codeManageService.applyConfigToCode(siteId, extraParams);
        return success(result);
    }

    /**
     * 获取当前代码仓库中的可视化配置（从文件解析）
     */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:query')")
    @GetMapping("/{siteId}/visual-config")
    public AjaxResult getVisualConfig(@PathVariable Long siteId) {
        Map<String, Object> result = codeManageService.getVisualConfig(siteId);
        return success(result);
    }

    /**
     * 保存可视化配置：更新数据库 + 写入代码文件 + commit/push
     */
    @PreAuthorize("@ss.hasPermi('gamebox:codeManage:edit')")
    @Log(title = "建站向导-保存可视化配置", businessType = BusinessType.UPDATE)
    @PostMapping("/{siteId}/visual-config")
    public AjaxResult saveVisualConfig(@PathVariable Long siteId, @RequestBody Map<String, Object> params) {
        Map<String, Object> result = codeManageService.saveVisualConfig(siteId, params);
        return success(result);
    }
}
