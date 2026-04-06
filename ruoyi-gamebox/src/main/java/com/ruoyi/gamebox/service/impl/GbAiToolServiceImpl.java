package com.ruoyi.gamebox.service.impl;

import java.util.*;

import feign.Feign;
import feign.httpclient.ApacheHttpClient;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.gamebox.domain.GbAiPlatform;
import com.ruoyi.gamebox.feign.OpenAIClient;
import com.ruoyi.gamebox.feign.FeignConfig;
import com.ruoyi.gamebox.service.IGbAiPlatformService;
import com.ruoyi.gamebox.service.IGbAiToolService;
import com.ruoyi.gamebox.support.GbProxyConfigSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AI工具Service业务层处理
 * 
 * @author ruoyi
 */
@Service
public class GbAiToolServiceImpl implements IGbAiToolService 
{
    private static final Logger log = LoggerFactory.getLogger(GbAiToolServiceImpl.class);

    @Autowired
    private IGbAiPlatformService aiPlatformService;

    @Autowired
    private GbProxyConfigSupport proxyConfigSupport;

    /** @deprecated 保留字段供其他可能注入此字段的地方兼容，实际 AI 调用已改用 proxyConfigSupport */
    @Autowired(required = false)
    private CloseableHttpClient httpClient;

    @Override
    public String callAI(Long platformId, String systemPrompt, String userPrompt, Map<String, Object> parameters) throws Exception
    {
        GbAiPlatform platform = aiPlatformService.selectGbAiPlatformById(platformId);
        if (platform == null) {
            throw new Exception("AI平台不存在");
        }

        String platformType = platform.getPlatformType();
        
        // 根据平台类型调用不同的API
        if ("openai".equalsIgnoreCase(platformType) || "azure".equalsIgnoreCase(platformType)) {
            return callOpenAI(platform, systemPrompt, userPrompt, parameters);
        } else if ("qwen".equalsIgnoreCase(platformType)) {
            return callQwen(platform, systemPrompt, userPrompt, parameters);
        } else if ("wenxin".equalsIgnoreCase(platformType)) {
            return callWenxin(platform, systemPrompt, userPrompt, parameters);
        } else {
            // 默认使用OpenAI兼容格式
            return callOpenAI(platform, systemPrompt, userPrompt, parameters);
        }
    }

    /**
     * 渲染提示词模板
     */
    private String renderPromptTemplate(String template, Map<String, Object> variables)
    {
        if (template == null || template.isEmpty()) {
            return "";
        }

        String result = template;
        
        // 替换所有变量 {{variableName}}
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            result = result.replace("{{" + key + "}}", value);
        }

        // 清除未替换的变量
        result = result.replaceAll("\\{\\{[^}]+\\}\\}", "");

        return result;
    }

    /**
     * 调用OpenAI API（使用 Feign Client 规范化调用）
     */
    private String callOpenAI(GbAiPlatform platform, String systemPrompt, String userPrompt, Map<String, Object> parameters) throws Exception
    {
        // 每次调用时按代理配置构建专属 HttpClient（不复用全局 bean，确保代理设置实时生效）
        try (CloseableHttpClient proxyAwareClient = proxyConfigSupport.buildApacheHttpClient("ai_platform_call")) {
            // 动态创建 Feign Client（支持不同的 base URL）
            Feign.Builder feignBuilder = Feign.builder()
                .contract(new FeignConfig().feignContract())
                .requestInterceptor(new FeignConfig().requestInterceptor())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .options(new FeignConfig().requestOptions())
                .retryer(new FeignConfig().retryer())
                .logger(new feign.slf4j.Slf4jLogger(OpenAIClient.class))
                .logLevel(new FeignConfig().feignLoggerLevel())
                .errorDecoder(new FeignConfig().errorDecoder())
                .client(new ApacheHttpClient(proxyAwareClient));  // 始终使用代理感知客户端
            
            OpenAIClient client = feignBuilder.target(OpenAIClient.class, platform.getBaseUrl());

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", platform.getDefaultModel());
            
            // 构建消息列表
            List<Map<String, String>> messages = new ArrayList<>();
            if (systemPrompt != null && !systemPrompt.isEmpty()) {
                Map<String, String> systemMessage = new HashMap<>();
                systemMessage.put("role", "system");
                systemMessage.put("content", systemPrompt);
                messages.add(systemMessage);
            }
            
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", userPrompt);
            messages.add(userMessage);
            
            requestBody.put("messages", messages);

            // 添加参数
            if (parameters.containsKey("temperature")) {
                double temp = Double.parseDouble(parameters.get("temperature").toString());
                requestBody.put("temperature", temp);
            } else if (platform.getTemperature() != null) {
                requestBody.put("temperature", platform.getTemperature().doubleValue());
            }

            if (parameters.containsKey("maxTokens")) {
                int maxTokens = Integer.parseInt(parameters.get("maxTokens").toString());
                requestBody.put("max_tokens", maxTokens);
            } else if (platform.getMaxTokens() != null) {
                requestBody.put("max_tokens", platform.getMaxTokens());
            }

            log.info("调用OpenAI API: {}", platform.getBaseUrl());
            log.debug("请求参数: {}", JSON.toJSONString(requestBody));

            // 使用 Feign Client 发送请求
            String authorization = "Bearer " + platform.getApiKey();
            Map<String, Object> response = client.chatCompletions(
                authorization,
                platform.getOrganizationId(),
                requestBody
            );

            log.debug("API响应: {}", JSON.toJSONString(response));

            // 解析响应
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            
            if (choices == null || choices.isEmpty()) {
                throw new Exception("AI返回结果为空");
            }

            Map<String, Object> firstChoice = choices.get(0);
            @SuppressWarnings("unchecked")
            Map<String, String> message = (Map<String, String>) firstChoice.get("message");
            
            return message.get("content");

        } catch (Exception e) {
            log.error("调用OpenAI API失败", e);
            throw new Exception("调用AI接口失败: " + e.getMessage(), e);
        }
    }

    /**
     * 调用通义千问API
     */
    private String callQwen(GbAiPlatform platform, String systemPrompt, String userPrompt, Map<String, Object> parameters) throws Exception
    {
        // 使用代理感知 RestTemplate
        RestTemplate rt = proxyConfigSupport.buildRestTemplate("ai_platform_call");
        try {
            String url = platform.getBaseUrl();
            if (!url.endsWith("/")) {
                url += "/";
            }
            url += "v1/services/aigc/text-generation/generation";

            // 构建请求体 - 通义千问格式
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", platform.getDefaultModel());
            
            Map<String, Object> input = new HashMap<>();
            List<Map<String, String>> messages = new ArrayList<>();
            
            if (systemPrompt != null && !systemPrompt.isEmpty()) {
                Map<String, String> systemMessage = new HashMap<>();
                systemMessage.put("role", "system");
                systemMessage.put("content", systemPrompt);
                messages.add(systemMessage);
            }
            
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", userPrompt);
            messages.add(userMessage);
            
            input.put("messages", messages);
            requestBody.put("input", input);

            // 添加参数
            Map<String, Object> params = new HashMap<>();
            if (parameters.containsKey("temperature")) {
                params.put("temperature", Double.parseDouble(parameters.get("temperature").toString()));
            }
            if (parameters.containsKey("maxTokens")) {
                params.put("max_tokens", Integer.parseInt(parameters.get("maxTokens").toString()));
            }
            requestBody.put("parameters", params);

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + platform.getApiKey());

            HttpEntity<String> request = new HttpEntity<>(JSON.toJSONString(requestBody), headers);

            log.info("调用通义千问API: {}", url);

            // 发送请求
            ResponseEntity<String> response = rt.postForEntity(url, request, String.class);
            
            // 解析响应
            JSONObject jsonResponse = JSON.parseObject(response.getBody());
            JSONObject output = jsonResponse.getJSONObject("output");
            
            if (output == null) {
                throw new Exception("AI返回结果格式错误");
            }

            return output.getString("text");

        } catch (Exception e) {
            log.error("调用通义千问API失败", e);
            throw new Exception("调用AI接口失败: " + e.getMessage(), e);
        }
    }

    /**
     * 调用文心一言API
     */
    private String callWenxin(GbAiPlatform platform, String systemPrompt, String userPrompt, Map<String, Object> parameters) throws Exception
    {
        // 文心一言API调用逻辑
        // 这里可以参考百度文心一言的API文档实现
        throw new Exception("文心一言接口暂未实现");
    }
}
