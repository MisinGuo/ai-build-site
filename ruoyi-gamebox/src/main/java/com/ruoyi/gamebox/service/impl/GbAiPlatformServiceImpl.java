package com.ruoyi.gamebox.service.impl;

import java.util.*;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import com.ruoyi.gamebox.mapper.GbAiPlatformMapper;
import com.ruoyi.gamebox.mapper.GbSiteMapper;
import com.ruoyi.gamebox.domain.GbAiPlatform;
import com.ruoyi.gamebox.service.IGbAiPlatformService;
import com.ruoyi.gamebox.support.RelatedModeSiteValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * AI平台配置Service业务层处理
 * 
 * @author ruoyi
 */
@Service
public class GbAiPlatformServiceImpl implements IGbAiPlatformService 
{
    private static final Logger log = LoggerFactory.getLogger(GbAiPlatformServiceImpl.class);

    @Autowired
    private GbAiPlatformMapper gbAiPlatformMapper;

    @Autowired
    private GbSiteMapper gbSiteMapper;

    @Autowired
    private RelatedModeSiteValidator relatedModeSiteValidator;

    private void injectPersonalSiteId(BaseEntity entity) {
        try {
            Long personalSiteId = gbSiteMapper.selectPersonalSiteIdByUserId(SecurityUtils.getUserId());
            if (personalSiteId != null) {
                entity.getParams().put("personalSiteId", personalSiteId);
            }
        } catch (Exception ignored) {}
}

    /**
     * 查询AI平台配置
     * 
     * @param id 平台主键
     * @return AI平台配置
     */
    @Override
    public GbAiPlatform selectGbAiPlatformById(Long id)
    {
        return gbAiPlatformMapper.selectGbAiPlatformById(id);
    }

    /**
     * 测试平台连接，返回平台类型、模型数量和模型列表
     * 连接失败时抛出异常，由 Controller 统一返回错误响应
     */
    @Override
    public Map<String, Object> testConnection(Long id) throws Exception {
        GbAiPlatform platform = this.selectGbAiPlatformById(id);
        if (platform == null) {
            throw new Exception("未找到平台配置，ID: " + id);
        }
        String platformType = platform.getPlatformType();
        String apiKey = platform.getApiKey();
        String baseUrl = platform.getBaseUrl();

        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new Exception("API密钥未配置");
        }

        // 使用专用测试方法——不吞掉异常，失败即抛出
        List<String> models = doTestFetchModels(apiKey, baseUrl);

        Map<String, Object> result = new HashMap<>();
        result.put("platformType", platformType);
        result.put("modelCount", models.size());
        result.put("models", models);
        result.put("message", "测试完成");
        return result;
    }

    /**
     * 专用于连接测试的模型获取方法——失败直接抛出异常，不做任何回退
     */
    private List<String> doTestFetchModels(String apiKey, String baseUrl) throws Exception {
        // 构建请求 URL
        String url;
        if (baseUrl != null && !baseUrl.trim().isEmpty()) {
            String trimmed = baseUrl.trim();
            if (trimmed.contains("/v1/models") || trimmed.endsWith("/models")) {
                url = trimmed;
            } else if (trimmed.endsWith("/v1") || trimmed.endsWith("/v1/")) {
                url = (trimmed.endsWith("/") ? trimmed : trimmed + "/") + "models";
            } else {
                url = (trimmed.endsWith("/") ? trimmed : trimmed + "/") + "v1/models";
            }
        } else {
            url = "https://api.openai.com/v1/models";
        }

        log.info("[测试连接] 请求 URL: {}", url);

        // 校验 URL 必须是合法的绝对地址（以 http:// 或 https:// 开头）
        try {
            java.net.URI uri = new java.net.URI(url);
            if (!uri.isAbsolute() || (!"http".equals(uri.getScheme()) && !"https".equals(uri.getScheme()))) {
                throw new Exception("API地址无效，请填写完整的 URL（如 https://api.openai.com）");
            }
        } catch (java.net.URISyntaxException e) {
            throw new Exception("API地址格式错误: " + url);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response;
        try {
            // 4xx/5xx 时 RestTemplate 会抛出 HttpStatusCodeException
            response = rt.exchange(url, HttpMethod.GET, entity, String.class);
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            String body = e.getResponseBodyAsString();
            log.warn("[测试连接] HTTP {} 错误，响应: {}", e.getStatusCode().value(), body);
            throw new Exception("连接失败（HTTP " + e.getStatusCode().value() + "）: " + body);
        } catch (org.springframework.web.client.HttpServerErrorException e) {
            String body = e.getResponseBodyAsString();
            log.warn("[测试连接] HTTP {} 服务端错误，响应: {}", e.getStatusCode().value(), body);
            throw new Exception("服务端错误（HTTP " + e.getStatusCode().value() + "）: " + body);
        } catch (org.springframework.web.client.ResourceAccessException e) {
            log.warn("[测试连接] 无法连接: {}", e.getMessage());
            throw new Exception("无法连接到服务器: " + e.getMessage());
        }

        String responseBody = response.getBody();
        if (responseBody == null || responseBody.isEmpty()) {
            throw new Exception("服务器返回空响应");
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(responseBody);
        JsonNode data = root.get("data");

        if (data == null || !data.isArray()) {
            throw new Exception("响应格式错误（缺少 data 数组），原始响应: "
                    + responseBody.substring(0, Math.min(200, responseBody.length())));
        }

        List<String> models = new ArrayList<>();
        for (JsonNode node : data) {
            JsonNode idNode = node.get("id");
            if (idNode != null) {
                models.add(idNode.asText());
            }
        }
        models.sort(String::compareTo);
        log.info("[测试连接] 成功，获取到 {} 个模型", models.size());
        return models;
    }

    /**
     * 查询AI平台配置列表
     * 
     * @param gbAiPlatform AI平台配置
     * @return AI平台配置
     */
    @Override
    public List<GbAiPlatform> selectGbAiPlatformList(GbAiPlatform gbAiPlatform)
    {
        relatedModeSiteValidator.validate(gbAiPlatform.getQueryMode(), gbAiPlatform.getSiteId());
        injectPersonalSiteId(gbAiPlatform);
        return gbAiPlatformMapper.selectGbAiPlatformList(gbAiPlatform);
    }

    /**
     * 新增AI平台配置
     * 
     * @param gbAiPlatform AI平台配置
     * @return 结果
     */
    @Override
    public int insertGbAiPlatform(GbAiPlatform gbAiPlatform)
    {
        gbAiPlatform.setCreateTime(DateUtils.getNowDate());
        return gbAiPlatformMapper.insertGbAiPlatform(gbAiPlatform);
    }

    /**
     * 修改AI平台配置
     * 
     * @param gbAiPlatform AI平台配置
     * @return 结果
     */
    @Override
    public int updateGbAiPlatform(GbAiPlatform gbAiPlatform)
    {
        gbAiPlatform.setUpdateTime(DateUtils.getNowDate());
        return gbAiPlatformMapper.updateGbAiPlatform(gbAiPlatform);
    }

    /**
     * 批量删除AI平台配置
     * 
     * @param ids 需要删除的平台主键
     * @return 结果
     */
    @Override
    public int deleteGbAiPlatformByIds(Long[] ids)
    {
        return gbAiPlatformMapper.deleteGbAiPlatformByIds(ids);
    }

    /**
     * 删除AI平台配置信息
     * 
     * @param id 平台主键
     * @return 结果
     */
    @Override
    public int deleteGbAiPlatformById(Long id)
    {
        return gbAiPlatformMapper.deleteGbAiPlatformById(id);
    }

    /**
     * 根据平台类型获取可用模型列表
     * 
     * @param platformType 平台类型
     * @param apiKey API密钥（可选，用于实时获取）
     * @param baseUrl API地址（可选）
     * @return 模型列表
     */
    @Override
    public List<String> getAvailableModels(String platformType, String apiKey, String baseUrl)
    {
        List<String> models = new ArrayList<>();
        
        log.info("获取模型列表 - 平台类型: {}, baseUrl: {}, apiKey存在: {}", 
                 platformType, baseUrl, apiKey != null && !apiKey.isEmpty());
        
        try {
            // 如果提供了apiKey，尝试使用OpenAI兼容格式实时获取
            if (apiKey != null && !apiKey.isEmpty()) {
                models = fetchOpenAICompatibleModels(apiKey, baseUrl);
                if (!models.isEmpty()) {
                    log.info("成功获取 {} 个模型", models.size());
                    return models;
                }
            }
            
            // 如果API调用失败或未提供apiKey，返回预定义列表
            models = getDefaultModels(platformType);
            
        } catch (Exception e) {
            log.error("获取模型列表失败: {}", e.getMessage(), e);
            // 失败时返回预定义列表
            models = getDefaultModels(platformType);
        }
        
        return models;
    }

    /**
     * 使用OpenAI兼容格式获取模型列表
     * 支持所有兼容OpenAI API格式的平台（OpenAI、Azure、通义千问、智谱等）
     */
    private List<String> fetchOpenAICompatibleModels(String apiKey, String baseUrl) {
        try {
            log.info("开始获取模型列表，baseUrl: {}", baseUrl);
            
            // 构建请求URL
            String url;
            if (baseUrl != null && !baseUrl.isEmpty()) {
                // 如果baseUrl已经包含v1/models或models路径，直接使用
                if (baseUrl.contains("/v1/models") || baseUrl.endsWith("/models")) {
                    url = baseUrl;
                } else if (baseUrl.endsWith("/v1") || baseUrl.endsWith("/v1/")) {
                    // 如果已经以v1结尾，只添加/models
                    url = (baseUrl.endsWith("/") ? baseUrl : baseUrl + "/") + "models";
                } else {
                    // 否则添加完整路径/v1/models
                    url = (baseUrl.endsWith("/") ? baseUrl : baseUrl + "/") + "v1/models";
                }
            } else {
                url = "https://api.openai.com/v1/models";
            }
            
            log.info("请求URL: {}", url);
            
            // 创建HTTP请求头
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            headers.set("Content-Type", "application/json");
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // 创建RestTemplate
            RestTemplate restTemplate = new RestTemplate();
            
            // 发送请求
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
            );
            
            if (!response.getStatusCode().is2xxSuccessful()) {
                String errorBody = response.getBody() != null ? response.getBody() : "无响应体";
                log.error("请求失败，状态码: {}, 响应: {}", response.getStatusCodeValue(), errorBody);
                return new ArrayList<>();
            }
            
            String responseBody = response.getBody();
            log.info("收到响应，长度: {}, 内容前100字符: {}", 
                responseBody.length(), 
                responseBody.substring(0, Math.min(100, responseBody.length())));
            
            // 解析JSON
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseBody);
            JsonNode data = root.get("data");
            
            if (data == null || !data.isArray()) {
                log.error("响应格式错误，缺少data数组。完整响应: {}", responseBody);
                return new ArrayList<>();
            }
            
            List<String> models = new ArrayList<>();
            for (JsonNode node : data) {
                JsonNode idNode = node.get("id");
                if (idNode != null) {
                    models.add(idNode.asText());
                }
            }
            
            models.sort(String::compareTo);
            log.info("成功获取 {} 个模型", models.size());
            return models;
        } catch (Exception e) {
            log.error("获取模型列表失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取预定义的模型列表（作为后备）
     */
    private List<String> getDefaultModels(String platformType) {
        // 不使用预定义模型，返回空列表
        log.warn("未提供API密钥，无法获取模型列表，平台类型: {}", platformType);
        return new ArrayList<>();
    }

    /**
     * 调用AI平台生成文本
     */
    @Override
    public String generateText(Long platformId, String systemPrompt, String userPrompt, 
                              String modelName, Double temperature, Integer maxTokens) throws Exception {
        GbAiPlatform platform = this.selectGbAiPlatformById(platformId);
        if (platform == null) {
            throw new Exception("未找到AI平台配置");
        }
        
        String apiKey = platform.getApiKey();
        String baseUrl = platform.getBaseUrl();
        String model = modelName != null && !modelName.isEmpty() ? modelName : platform.getDefaultModel();
        
        if (apiKey == null || apiKey.isEmpty()) {
            throw new Exception("API密钥未配置");
        }
        
        if (model == null || model.isEmpty()) {
            throw new Exception("模型未指定");
        }
        
        // 构建请求URL
        String url;
        if (baseUrl != null && !baseUrl.isEmpty()) {
            if (baseUrl.contains("/v1/chat/completions") || baseUrl.endsWith("/chat/completions")) {
                url = baseUrl;
            } else if (baseUrl.endsWith("/v1") || baseUrl.endsWith("/v1/")) {
                url = (baseUrl.endsWith("/") ? baseUrl : baseUrl + "/") + "chat/completions";
            } else {
                url = (baseUrl.endsWith("/") ? baseUrl : baseUrl + "/") + "v1/chat/completions";
            }
        } else {
            url = "https://api.openai.com/v1/chat/completions";
        }
        
        log.info("调用AI生成文本 - URL: {}, model: {}", url, model);
        
        // 创建HTTP请求头
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");
        headers.set("User-Agent", "Mozilla/5.0");
        
        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        
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
        
        if (temperature != null) {
            requestBody.put("temperature", temperature);
        } else {
            requestBody.put("temperature", 0.7);
        }
        
        if (maxTokens != null) {
            requestBody.put("max_tokens", maxTokens);
        } else {
            requestBody.put("max_tokens", 2000);
        }
        
        // 序列化请求体
        ObjectMapper mapper = new ObjectMapper();
        String requestBodyJson = mapper.writeValueAsString(requestBody);
        
        HttpEntity<String> entity = new HttpEntity<>(requestBodyJson, headers);
        
        // 创建RestTemplate
        RestTemplate restTemplate = new RestTemplate();
        
        // 发送请求
        ResponseEntity<String> response = restTemplate.exchange(
            url,
            HttpMethod.POST,
            entity,
            String.class
        );
        
        if (!response.getStatusCode().is2xxSuccessful()) {
            String errorBody = response.getBody() != null ? response.getBody() : "无响应体";
            log.error("AI调用失败，状态码: {}, 响应: {}", response.getStatusCodeValue(), errorBody);
            throw new Exception("AI调用失败: " + errorBody);
        }
        
        String responseBody = response.getBody();
        log.info("AI响应成功，长度: {}", responseBody.length());
        
        // 解析响应
        JsonNode root = mapper.readTree(responseBody);
        JsonNode choices = root.get("choices");
        
        if (choices == null || !choices.isArray() || choices.size() == 0) {
            log.error("响应格式错误，缺少choices数组");
            throw new Exception("AI响应格式错误");
        }
        
        JsonNode firstChoice = choices.get(0);
        JsonNode message = firstChoice.get("message");
        
        if (message == null) {
            log.error("响应格式错误，缺少message字段");
            throw new Exception("AI响应格式错误");
        }
        
        JsonNode content = message.get("content");
        if (content == null) {
            log.error("响应格式错误，缺少content字段");
            throw new Exception("AI响应格式错误");
        }
        
        return content.asText();
    }
}
