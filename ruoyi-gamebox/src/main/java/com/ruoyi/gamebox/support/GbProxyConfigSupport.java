package com.ruoyi.gamebox.support;

import com.ruoyi.gamebox.domain.GbProxyConfig;
import com.ruoyi.gamebox.mapper.GbProxyConfigMapper;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 代理配置支撑工具
 * <p>
 * 从 gb_proxy_config 表读取各功能的代理设置，构建相应的 HTTP 客户端。
 * 内置 60 秒缓存，避免每次请求都查库。
 * 调用方通过 featureKey 指定功能，自动应用对应代理或直连。
 * </p>
 *
 * <p><b>开发验证：</b>每次构建客户端时会在 INFO 级别打印
 * {@code [PROXY ON]} / {@code [PROXY OFF]} 日志，方便本地开发验证代理是否生效。</p>
 */
@Component
public class GbProxyConfigSupport {

    private static final Logger log = LoggerFactory.getLogger(GbProxyConfigSupport.class);

    /** 缓存 TTL（毫秒） */
    private static final long CACHE_TTL_MS = 60_000L;

    @Autowired
    private GbProxyConfigMapper gbProxyConfigMapper;

    // -------- 内存缓存 --------
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

    private static class CacheEntry {
        final GbProxyConfig config;
        final long expireAt;
        CacheEntry(GbProxyConfig config) {
            this.config = config;
            this.expireAt = System.currentTimeMillis() + CACHE_TTL_MS;
        }
        boolean isExpired() { return System.currentTimeMillis() > expireAt; }
    }

    // ==================== 公共 API ====================

    /**
     * 读取指定功能的代理配置（带缓存）。
     */
    public GbProxyConfig getProxyConfig(String featureKey) {
        CacheEntry entry = cache.get(featureKey);
        if (entry == null || entry.isExpired()) {
            GbProxyConfig cfg = gbProxyConfigMapper.selectGbProxyConfigByFeatureKey(featureKey);
            if (cfg != null) {
                cache.put(featureKey, new CacheEntry(cfg));
            } else {
                cache.remove(featureKey);
            }
            return cfg;
        }
        return entry.config;
    }

    /**
     * 主动清除某功能的缓存（在代理配置被更新后调用）。
     */
    public void invalidateCache(String featureKey) {
        if (featureKey != null) {
            cache.remove(featureKey);
        } else {
            cache.clear();
        }
    }

    /**
     * 判断某功能是否已启用且有效的代理。
     */
    public boolean isProxyActive(String featureKey) {
        GbProxyConfig cfg = getProxyConfig(featureKey);
        return cfg != null
                && cfg.getProxyEnabled() != null && cfg.getProxyEnabled() == 1
                && isNotBlank(cfg.getProxyHost())
                && cfg.getProxyPort() != null;
    }

    /**
     * 构建 java.net.Proxy（供 GitHubBuilder.withProxy() / HttpURLConnection 使用）。
     * 若代理未启用，返回 {@code null}（调用方直连）。
     */
    public Proxy buildJavaProxy(String featureKey) {
        GbProxyConfig cfg = getProxyConfig(featureKey);
        if (!isActive(cfg)) {
            logDirect(featureKey);
            return null;
        }
        Proxy.Type type = "socks5".equalsIgnoreCase(cfg.getProxyType())
                ? Proxy.Type.SOCKS : Proxy.Type.HTTP;
        Proxy proxy = new Proxy(type, new InetSocketAddress(cfg.getProxyHost(), cfg.getProxyPort()));
        logProxyOn(featureKey, cfg);
        return proxy;
    }

    /**
     * 构建代理感知的 Apache CloseableHttpClient（供 Feign / RestTemplate 使用）。
     * 始终返回新实例，调用方使用完毕后应关闭。
     */
    public CloseableHttpClient buildApacheHttpClient(String featureKey) {
        GbProxyConfig cfg = getProxyConfig(featureKey);

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(10_000)
                .setSocketTimeout(120_000)
                .setConnectionRequestTimeout(5_000)
                .build();

        HttpClientBuilder builder = HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .setMaxConnTotal(50)
                .setMaxConnPerRoute(20)
                .disableContentCompression();

        if (isActive(cfg)) {
            HttpHost proxyHost = new HttpHost(cfg.getProxyHost(), cfg.getProxyPort());
            builder.setProxy(proxyHost);
            if (isNotBlank(cfg.getProxyUsername())) {
                BasicCredentialsProvider creds = new BasicCredentialsProvider();
                creds.setCredentials(
                        new AuthScope(cfg.getProxyHost(), cfg.getProxyPort()),
                        new UsernamePasswordCredentials(cfg.getProxyUsername(),
                                cfg.getProxyPassword() != null ? cfg.getProxyPassword() : ""));
                builder.setDefaultCredentialsProvider(creds);
            }
            logProxyOn(featureKey, cfg);
        } else {
            logDirect(featureKey);
        }

        return builder.build();
    }

    /**
     * 构建代理感知的 RestTemplate（供工作流工具等使用）。
     */
    public RestTemplate buildRestTemplate(String featureKey) {
        CloseableHttpClient client = buildApacheHttpClient(featureKey);
        return new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));
    }

    /**
     * 创建代理感知的 {@link java.net.HttpURLConnection}。
     * <p>供直接使用 {@code java.net.URL#openConnection()} 的代码（如 GbCodeManageServiceImpl）使用。
     * 若代理已启用，通过代理打开连接并输出 {@code [PROXY ON]} 日志；否则直连。</p>
     */
    public java.net.HttpURLConnection buildHttpURLConnection(String url, String featureKey) throws java.io.IOException {
        java.net.Proxy proxy = buildJavaProxy(featureKey);
        if (proxy != null) {
            log.info("[PROXY ON ] featureKey={} | HttpURLConnection {}", featureKey, url);
            return (java.net.HttpURLConnection) new java.net.URL(url).openConnection(proxy);
        } else {
            log.debug("[PROXY OFF] featureKey={} | HttpURLConnection {}", featureKey, url);
            return (java.net.HttpURLConnection) new java.net.URL(url).openConnection();
        }
    }

    /**
     * 构建供 Git 子进程（{@code git clone} / {@code git push} 等）使用的代理环境变量。
     * <p>当代理已启用时返回包含 {@code HTTP_PROXY}、{@code HTTPS_PROXY}（大/小写各一对）的 Map，
     * 可直接传入 {@link ProcessBuilder#environment()}；否则返回空 Map，不修改进程环境。</p>
     * <p>同时在 INFO 级别打印 {@code [PROXY ON GIT]} 日志供开发验证。</p>
     */
    public java.util.Map<String, String> buildGitProxyEnv(String featureKey) {
        GbProxyConfig cfg = getProxyConfig(featureKey);
        if (!isActive(cfg)) {
            log.debug("[PROXY OFF] featureKey={} | Git代理未启用", featureKey);
            return java.util.Collections.emptyMap();
        }
        // socks5 代理用 socks5://，否则用 http://
        String scheme = "socks5".equalsIgnoreCase(cfg.getProxyType()) ? "socks5" : "http";
        StringBuilder sb = new StringBuilder(scheme).append("://");
        if (isNotBlank(cfg.getProxyUsername())) {
            sb.append(cfg.getProxyUsername());
            if (isNotBlank(cfg.getProxyPassword())) {
                sb.append(":").append(cfg.getProxyPassword());
            }
            sb.append("@");
        }
        sb.append(cfg.getProxyHost()).append(":").append(cfg.getProxyPort());
        String proxyUrl = sb.toString();
        log.info("[PROXY ON GIT] featureKey={} | {}", featureKey, proxyUrl);
        java.util.Map<String, String> env = new java.util.LinkedHashMap<>();
        env.put("HTTP_PROXY",  proxyUrl);
        env.put("HTTPS_PROXY", proxyUrl);
        env.put("http_proxy",  proxyUrl);  // 部分 git 版本读小写变量
        env.put("https_proxy", proxyUrl);
        return env;
    }

    /**
     * 通过代理感知的 Apache HttpClient 执行 GET 请求（正确处理代理 CONNECT 407 认证）。
     */
    public SimpleResponse executeGet(String url, String featureKey,
            java.util.Map<String, String> headers) throws Exception {
        return execute("GET", url, featureKey, headers, null, null);
    }

    /**
     * 通过代理感知的 Apache HttpClient 执行 POST 请求。
     */
    public SimpleResponse executePost(String url, String featureKey,
            java.util.Map<String, String> headers, String body, String contentType) throws Exception {
        return execute("POST", url, featureKey, headers, body, contentType);
    }

    /**
     * 通过代理感知的 Apache HttpClient 执行 PUT 请求。
     */
    public SimpleResponse executePut(String url, String featureKey,
            java.util.Map<String, String> headers, String body, String contentType) throws Exception {
        return execute("PUT", url, featureKey, headers, body, contentType);
    }

    private SimpleResponse execute(String method, String url, String featureKey,
            java.util.Map<String, String> headers, String body, String contentType) throws Exception {
        HttpRequestBase req;
        if ("POST".equalsIgnoreCase(method)) {
            HttpPost post = new HttpPost(url);
            if (body != null) { post.setEntity(new StringEntity(body, "UTF-8")); }
            if (contentType != null) { post.setHeader("Content-Type", contentType); }
            req = post;
        } else if ("PUT".equalsIgnoreCase(method)) {
            HttpPut put = new HttpPut(url);
            if (body != null) { put.setEntity(new StringEntity(body, "UTF-8")); }
            if (contentType != null) { put.setHeader("Content-Type", contentType); }
            req = put;
        } else {
            req = new HttpGet(url);
        }
        if (headers != null) {
            for (java.util.Map.Entry<String, String> e : headers.entrySet()) {
                req.setHeader(e.getKey(), e.getValue());
            }
        }
        try (CloseableHttpClient client = buildApacheHttpClient(featureKey)) {
            org.apache.http.HttpResponse resp = client.execute(req);
            int status = resp.getStatusLine().getStatusCode();
            String respBody = resp.getEntity() != null
                    ? EntityUtils.toString(resp.getEntity(), "UTF-8") : "";
            return new SimpleResponse(status, respBody);
        }
    }

    /**
     * 功能+连通性测试：使用与真实功能<b>完全相同的代码路径</b>（{@link #buildHttpURLConnection}）
     * 访问目标服务的实际 API 端点，从而同时验证：
     * <ol>
     *   <li>代理服务器本身是否可达</li>
     *   <li>实际功能调用是否真的会经过代理</li>
     * </ol>
     * 若 {@code testUrl} 为空，则按 {@code featureKey} 自动选取该功能对应的真实服务端点。
     */
    public ProxyTestResult testConnectivity(String featureKey, String testUrl) {
        // 按功能键选取真实目标端点（未指定时使用默认值）
        if (testUrl == null || testUrl.isEmpty()) {
            testUrl = defaultTestUrl(featureKey);
        }

        GbProxyConfig cfg = getProxyConfig(featureKey);
        boolean proxyActive = isActive(cfg);
        String proxyDesc = proxyActive
                ? cfg.getProxyType() + "://" + cfg.getProxyHost() + ":" + cfg.getProxyPort()
                : "直连（无代理）";

        log.info("[PROXY FEATURE TEST] featureKey={} | 代理={} | 目标={}", featureKey, proxyDesc, testUrl);

        // ---- 使用与真实功能完全相同的代码路径 buildHttpURLConnection ----
        long start = System.currentTimeMillis();
        try {
            // 此行会打印 [PROXY ON] 或 [PROXY OFF]，与实际功能运行时日志完全一致
            java.net.HttpURLConnection conn = buildHttpURLConnection(testUrl, featureKey);
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setInstanceFollowRedirects(true);
            // 公共端点加上 User-Agent，避免被 WAF 拦截
            conn.setRequestProperty("User-Agent", "GameBox-ProxyTest/1.0");
            int status = conn.getResponseCode();
            // 耗尽响应体，避免连接挂起
            try { java.io.InputStream is = conn.getInputStream(); if (is != null) { byte[] buf = new byte[1024]; while (is.read(buf) > 0) {} } } catch (Exception ignored) {}
            long elapsed = System.currentTimeMillis() - start;
            log.info("[PROXY FEATURE TEST] featureKey={} | HTTP {} | {}ms | 代理={}", featureKey, status, elapsed, proxyDesc);
            return ProxyTestResult.success(proxyActive, proxyDesc, testUrl, status, elapsed);
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - start;
            log.warn("[PROXY FEATURE TEST] featureKey={} | 失败: {} | {}ms | 代理={}", featureKey, e.getMessage(), elapsed, proxyDesc);
            return ProxyTestResult.failure(proxyActive, proxyDesc, testUrl, e.getMessage(), elapsed);
        }
    }

    /**
     * 按功能键返回对应真实服务的轻量测试端点（无需鉴权即可访问，仅验证网络可达性）。
     */
    private String defaultTestUrl(String featureKey) {
        if (featureKey == null) return "https://www.google.com";
        switch (featureKey) {
            case "git_account_ops":
            case "workflow_git_tool":
            case "storage_github":
            case "storage_config_verify":
                // GitHub API 根路径，返回 200 + 端点列表（无需 Token）
                return "https://api.github.com";
            case "cf_account_api":
                // Cloudflare API 根路径，返回 200（无需 Token）
                return "https://api.cloudflare.com/client/v4";
            case "ai_platform_call":
                // OpenAI 状态页（无需 Token）
                return "https://status.openai.com/api/v2/status.json";
            case "translation_service":
                return "https://translation.googleapis.com/language/translate/v2";
            case "workflow_api_tool":
            default:
                return "https://www.google.com";
        }
    }

    // ==================== 内部工具 ====================

    private boolean isActive(GbProxyConfig cfg) {
        return cfg != null
                && cfg.getProxyEnabled() != null && cfg.getProxyEnabled() == 1
                && isNotBlank(cfg.getProxyHost())
                && cfg.getProxyPort() != null;
    }

    private boolean isNotBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private void logProxyOn(String featureKey, GbProxyConfig cfg) {
        log.info("[PROXY ON ] featureKey={} | {}://{}:{}", featureKey,
                cfg.getProxyType(), cfg.getProxyHost(), cfg.getProxyPort());
    }

    private void logDirect(String featureKey) {
        log.debug("[PROXY OFF] featureKey={} | 直连（代理未启用）", featureKey);
    }

    // ==================== 内部结果类 ====================

    /**
     * HTTP 响应简单包装（供 Git API 等代理感知调用使用）。
     */
    public static class SimpleResponse {
        public final int status;
        public final String body;
        public SimpleResponse(int status, String body) {
            this.status = status;
            this.body   = body;
        }
    }

    public static class ProxyTestResult {
        public final boolean proxyActive;
        public final String proxyDesc;
        public final String testUrl;
        public final boolean success;
        public final Integer httpStatus;
        public final Long elapsedMs;
        public final String errorMessage;

        private ProxyTestResult(boolean proxyActive, String proxyDesc, String testUrl,
                                boolean success, Integer httpStatus, Long elapsedMs, String errorMessage) {
            this.proxyActive  = proxyActive;
            this.proxyDesc    = proxyDesc;
            this.testUrl      = testUrl;
            this.success      = success;
            this.httpStatus   = httpStatus;
            this.elapsedMs    = elapsedMs;
            this.errorMessage = errorMessage;
        }

        public static ProxyTestResult success(boolean proxyActive, String proxyDesc, String testUrl,
                                              int httpStatus, long elapsedMs) {
            return new ProxyTestResult(proxyActive, proxyDesc, testUrl, true, httpStatus, elapsedMs, null);
        }

        public static ProxyTestResult failure(boolean proxyActive, String proxyDesc, String testUrl,
                                              String errorMessage, long elapsedMs) {
            return new ProxyTestResult(proxyActive, proxyDesc, testUrl, false, null, elapsedMs, errorMessage);
        }
    }
}
