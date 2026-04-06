package com.ruoyi.gamebox.feign;

import feign.Logger;
import feign.Request;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import feign.httpclient.ApacheHttpClient;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.protocol.HttpContext;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

/**
 * HTTP 客户端统一配置
 * 提供 Feign 和 RestTemplate 的共享配置
 * 
 * @author ruoyi
 */
@Configuration
public class FeignConfig {

    /**
     * Apache HttpClient配置 - 正确处理308重定向
     * 供 Feign 和 RestTemplate 共用
     */
    @Bean
    public CloseableHttpClient httpClient() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(10000)           // 连接超时10秒
                .setSocketTimeout(120000)           // 读取超时120秒
                .setConnectionRequestTimeout(5000)  // 从连接池获取连接超时5秒
                .build();

        return HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .setMaxConnTotal(200)
                .setMaxConnPerRoute(50)
                .disableContentCompression()  // 禁用自动GZIP解压缩,避免与某些代理服务器冲突
                .setRedirectStrategy(new LaxRedirectStrategy() {
                    @Override
                    public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
                        int statusCode = response.getStatusLine().getStatusCode();
                        // 支持301, 302, 303, 307, 308重定向
                        if (statusCode == 301 || statusCode == 302 || statusCode == 303 || statusCode == 307 || statusCode == 308) {
                            return true;
                        }
                        return super.isRedirected(request, response, context);
                    }

                    @Override
                    public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
                        int statusCode = response.getStatusLine().getStatusCode();
                        // 对于307和308,保持原始请求方法(POST)
                        if (statusCode == 307 || statusCode == 308) {
                            String uri = response.getFirstHeader("Location").getValue();
                            return RequestBuilder.copy(request).setUri(uri).build();
                        }
                        return super.getRedirect(request, response, context);
                    }
                })
                .build();
    }

    /**
     * RestTemplate Bean - 使用统一的 HttpClient 配置
     * 用于调用外部HTTP接口（如翻译API）
     */
    @Bean
    public RestTemplate restTemplate(CloseableHttpClient httpClient) {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        return new RestTemplate(factory);
    }

    /**
     * 使用 Spring MVC 注解
     */
    @Bean
    public SpringMvcContract feignContract() {
        return new SpringMvcContract();
    }

    /**
     * 请求拦截器：添加 User-Agent 以避免被 Cloudflare 拦截
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            template.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        };
    }

    /**
     * 超时配置（保留用于Feign特定配置）
     */
    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(
            10L, TimeUnit.SECONDS,   // 连接超时
            120L, TimeUnit.SECONDS,  // 读取超时
            true                     // 跟随重定向
        );
    }

    /**
     * Feign Client配置 - 使用统一的 Apache HttpClient
     */
    @Bean
    public feign.Client feignClient(CloseableHttpClient httpClient) {
        return new ApacheHttpClient(httpClient);
    }

    /**
     * 不自动重试
     */
    @Bean
    public Retryer retryer() {
        return Retryer.NEVER_RETRY;
    }

    /**
     * 完整日志
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    /**
     * 错误解码器
     */
    @Bean
    public ErrorDecoder errorDecoder() {
        return new AiFeignErrorDecoder();
    }

    static class AiFeignErrorDecoder implements ErrorDecoder {
        private final ErrorDecoder defaultDecoder = new Default();

        @Override
        public Exception decode(String methodKey, feign.Response response) {
            int status = response.status();
            String errorMsg;

            switch (status) {
                case 400: errorMsg = "请求参数错误"; break;
                case 401: errorMsg = "API Key 无效"; break;
                case 403: errorMsg = "无权限访问"; break;
                case 429: errorMsg = "请求频率限制"; break;
                case 500:
                case 502:
                case 503: errorMsg = "AI 服务不可用"; break;
                default: return defaultDecoder.decode(methodKey, response);
            }

            return new RuntimeException(errorMsg + " (HTTP " + status + ")");
        }
    }
}
