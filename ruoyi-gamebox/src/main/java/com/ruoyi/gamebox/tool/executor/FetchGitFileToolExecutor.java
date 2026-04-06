package com.ruoyi.gamebox.tool.executor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.gamebox.support.GbProxyConfigSupport;
import com.ruoyi.gamebox.tool.SystemTool;
import com.ruoyi.gamebox.tool.ToolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 获取Git仓库文件工具执行器
 * 支持 GitHub / GitLab / Gitee 三大平台，通过各平台 REST API 获取指定文件的原始内容。
 * 典型使用场景：从爬虫数据仓库同步 JSON 数据文件。
 *
 * @author ruoyi
 * @date 2026-03-05
 */
@Component
@SystemTool(
    code = "fetch_git_file",
    toolType = "integration",
    name = "获取Git仓库文件",
    description = "从Git仓库（GitHub/GitLab/Gitee）中获取指定文件的原始内容，用于数据同步场景。支持私有仓库（需传入访问令牌）。",
    inputs = "["
        + "{\"name\":\"repoUrl\",\"type\":\"text\",\"label\":\"仓库URL\",\"required\":true,"
        + "\"description\":\"Git仓库地址，支持GitHub/GitLab/Gitee，如 https://github.com/owner/repo\"},"
        + "{\"name\":\"accessToken\",\"type\":\"text\",\"label\":\"访问令牌\",\"required\":false,"
        + "\"description\":\"私有仓库或需要提高API速率限制时使用的访问令牌（Personal Access Token）\"},"
        + "{\"name\":\"filePath\",\"type\":\"text\",\"label\":\"文件路径\",\"required\":true,"
        + "\"description\":\"文件在仓库中的相对路径，如 data/games.json 或 output/list.json\"},"
        + "{\"name\":\"branch\",\"type\":\"text\",\"label\":\"分支/标签\",\"required\":false,"
        + "\"description\":\"分支名或标签名，默认为 main\"}"
        + "]",
    outputs = "["
        + "{\"name\":\"content\",\"type\":\"textarea\",\"label\":\"文件内容\",\"description\":\"文件的原始文本内容（UTF-8）\"},"
        + "{\"name\":\"fileName\",\"type\":\"text\",\"label\":\"文件名\",\"description\":\"文件名（不含路径）\"},"
        + "{\"name\":\"fileSize\",\"type\":\"number\",\"label\":\"文件大小\",\"description\":\"文件大小（字节）\"},"
        + "{\"name\":\"success\",\"type\":\"boolean\",\"label\":\"是否成功\",\"description\":\"文件是否获取成功\"},"
        + "{\"name\":\"message\",\"type\":\"text\",\"label\":\"返回消息\",\"description\":\"操作结果消息\"}"
        + "]",
    order = 150
)
public class FetchGitFileToolExecutor implements ToolExecutor {

    private static final Logger log = LoggerFactory.getLogger(FetchGitFileToolExecutor.class);

    @Autowired
    private GbProxyConfigSupport proxyConfigSupport;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String getType() {
        return "fetch_git_file";
    }

    @Override
    public Map<String, Object> execute(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<>();

        String repoUrl     = getStringParam(params, "repoUrl");
        String accessToken = getStringParam(params, "accessToken");
        String filePath    = getStringParam(params, "filePath");
        String branch      = getStringParam(params, "branch");

        if (branch == null || branch.isEmpty()) {
            branch = "main";
        }

        if (repoUrl == null || repoUrl.isEmpty()) {
            result.put("success", false);
            result.put("message", "仓库URL不能为空");
            return result;
        }
        if (filePath == null || filePath.isEmpty()) {
            result.put("success", false);
            result.put("message", "文件路径不能为空");
            return result;
        }

        // 每次执行按代理配置构建 RestTemplate（支持实时生效，无需重启）
        RestTemplate rt = proxyConfigSupport.buildRestTemplate("workflow_git_tool");

        log.info("[FetchGitFile] 开始获取文件: repoUrl={}, filePath={}, branch={}", repoUrl, filePath, branch);
        // 不 catch 异常——让异常向上传播，工作流引擎可正确感知失败并标记步骤为 failed
        String content = fetchContent(rt, repoUrl, filePath, branch, accessToken);

        String fileName = filePath.contains("/")
            ? filePath.substring(filePath.lastIndexOf('/') + 1)
            : filePath;

        log.info("[FetchGitFile] 文件获取成功: fileName={}, size={}B", fileName, content.length());
        result.put("success", true);
        result.put("message", "文件获取成功");
        result.put("content", content);
        result.put("fileName", fileName);
        result.put("fileSize", content.getBytes(StandardCharsets.UTF_8).length);

        return result;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 平台分发
    // ──────────────────────────────────────────────────────────────────────────

    private String fetchContent(RestTemplate rt, String repoUrl, String filePath,
                                String branch, String accessToken) throws Exception {
        // 标准化：去掉末尾斜杠 / 去掉文件路径开头斜杠
        repoUrl  = repoUrl.trim().replaceAll("/+$", "");
        filePath = filePath.trim().replaceAll("^/+", "");
        if (repoUrl.contains("github.com")) {
            return fetchFromGitHub(rt, repoUrl, filePath, branch, accessToken);
        } else if (repoUrl.contains("gitlab")) {
            return fetchFromGitLab(rt, repoUrl, filePath, branch, accessToken);
        } else if (repoUrl.contains("gitee.com")) {
            return fetchFromGitee(rt, repoUrl, filePath, branch, accessToken);
        } else {
            // 其他平台兜底：尝试拼接 raw URL（适用于自建 GitLab 等）
            return fetchDirectUrl(rt, repoUrl + "/raw/" + branch + "/" + filePath, accessToken);
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // GitHub
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * GitHub Contents API
     * GET https://api.github.com/repos/{owner}/{repo}/contents/{path}?ref={branch}
     * Response: { "content": "<base64>", "encoding": "base64" }
     */
    private String fetchFromGitHub(RestTemplate rt, String repoUrl, String filePath,
                                   String branch, String accessToken) throws Exception {
        Pattern p = Pattern.compile("github\\.com[:/]([^/]+)/([^/\\.]+?)(\\.git)?$");
        Matcher m = p.matcher(repoUrl);
        if (!m.find()) {
            throw new IllegalArgumentException("无法解析 GitHub 仓库 URL: " + repoUrl);
        }
        String owner = m.group(1);
        String repo  = m.group(2);

        String apiUrl = String.format(
            "https://api.github.com/repos/%s/%s/contents/%s?ref=%s",
            owner, repo, filePath, branch
        );
        log.debug("[FetchGitFile][GitHub] API URL: {}", apiUrl);

        HttpHeaders headers = new HttpHeaders();
        // application/vnd.github.v3.raw 让 GitHub API 直接返回文件原始内容，
        // 无需访问 raw.githubusercontent.com，对大文件也有效
        headers.set("Accept", "application/vnd.github.v3.raw");
        headers.set("User-Agent", "GameBox-Sync-Bot/1.0");
        if (accessToken != null && !accessToken.isEmpty()) {
            String scheme = accessToken.startsWith("ghp_") || accessToken.startsWith("github_") ? "token" : "Bearer";
            headers.set("Authorization", scheme + " " + accessToken);
        }

        ResponseEntity<String> response = rt.exchange(
            apiUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class
        );

        return response.getBody() != null ? response.getBody() : "";
    }

    // ──────────────────────────────────────────────────────────────────────────
    // GitLab
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * GitLab Repository Files API（raw endpoint，直接返回文件内容）
     * GET {host}/api/v4/projects/{encoded_namespace_path}/repository/files/{encoded_file_path}/raw?ref={branch}
     */
    private String fetchFromGitLab(RestTemplate rt, String repoUrl, String filePath,
                                   String branch, String accessToken) throws Exception {
        URI uri = new URI(repoUrl.startsWith("http") ? repoUrl : "https://" + repoUrl);
        String host = uri.getScheme() + "://" + uri.getHost()
            + (uri.getPort() != -1 && uri.getPort() != 80 && uri.getPort() != 443
               ? ":" + uri.getPort() : "");

        String projectPath    = uri.getPath().replaceFirst("^/", "").replaceAll("\\.git$", "");
        String encodedProject = URLEncoder.encode(projectPath, "UTF-8");
        String encodedFile    = URLEncoder.encode(filePath, "UTF-8");

        String apiUrl = String.format(
            "%s/api/v4/projects/%s/repository/files/%s/raw?ref=%s",
            host, encodedProject, encodedFile, branch
        );
        log.debug("[FetchGitFile][GitLab] API URL: {}", apiUrl);

        HttpHeaders headers = new HttpHeaders();
        if (accessToken != null && !accessToken.isEmpty()) {
            headers.set("PRIVATE-TOKEN", accessToken);
        }

        ResponseEntity<String> response = rt.exchange(
            apiUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class
        );
        return response.getBody();
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Gitee
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Gitee Contents API
     * GET https://gitee.com/api/v5/repos/{owner}/{repo}/contents/{path}?ref={branch}&access_token={token}
     * Response: { "content": "<base64>", "encoding": "base64" }
     */
    private String fetchFromGitee(RestTemplate rt, String repoUrl, String filePath,
                                  String branch, String accessToken) throws Exception {
        Pattern p = Pattern.compile("gitee\\.com[:/]([^/]+)/([^/\\.]+?)(\\.git)?$");
        Matcher m = p.matcher(repoUrl);
        if (!m.find()) {
            throw new IllegalArgumentException("无法解析 Gitee 仓库 URL: " + repoUrl);
        }
        String owner = m.group(1);
        String repo  = m.group(2);

        String apiUrl = String.format(
            "https://gitee.com/api/v5/repos/%s/%s/contents/%s?ref=%s",
            owner, repo, filePath, branch
        );
        if (accessToken != null && !accessToken.isEmpty()) {
            apiUrl += "&access_token=" + URLEncoder.encode(accessToken, "UTF-8");
        }
        log.debug("[FetchGitFile][Gitee] API URL: {}", apiUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "GameBox-Sync-Bot/1.0");

        ResponseEntity<String> response = rt.exchange(
            apiUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class
        );

        JsonNode root = objectMapper.readTree(response.getBody());
        String encoding = root.path("encoding").asText("base64");
        String content  = root.path("content").asText("");

        if ("base64".equals(encoding)) {
            content = content.replaceAll("\\s+", "");
            byte[] decoded = Base64.getDecoder().decode(content);
            return new String(decoded, StandardCharsets.UTF_8);
        }
        return content;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 兜底：直接 GET URL
    // ──────────────────────────────────────────────────────────────────────────

    private String fetchDirectUrl(RestTemplate rt, String url, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "GameBox-Sync-Bot/1.0");
        if (accessToken != null && !accessToken.isEmpty()) {
            headers.set("Authorization", "Bearer " + accessToken);
        }
        log.debug("[FetchGitFile][Direct] URL: {}", url);
        ResponseEntity<String> response = rt.exchange(
            url, HttpMethod.GET, new HttpEntity<>(headers), String.class
        );
        return response.getBody();
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 工具方法
    // ──────────────────────────────────────────────────────────────────────────

    private String getStringParam(Map<String, Object> params, String key) {
        Object val = params.get(key);
        return (val != null) ? val.toString().trim() : null;
    }
}
