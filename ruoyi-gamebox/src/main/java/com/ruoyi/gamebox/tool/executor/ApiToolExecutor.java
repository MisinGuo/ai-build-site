package com.ruoyi.gamebox.tool.executor;

import com.ruoyi.gamebox.support.GbProxyConfigSupport;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.gamebox.tool.ToolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * API工具执行器
 * 这是一个通用的API工具执行器，不是系统工具
 * 
 * 工具类型说明：
 * - API工具（type="api"）：用户在前端手动配置API端点、请求方法、请求头、输入输出参数
 * - 执行器按类型（"api"）注册，执行时从数据库读取工具配置
 * - 支持GET、POST、PUT、DELETE等HTTP方法
 * 
 * @author ruoyi
 * @date 2026-02-07
 */
@Component
public class ApiToolExecutor implements ToolExecutor {
    
    private static final Logger log = LoggerFactory.getLogger(ApiToolExecutor.class);
    
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GbProxyConfigSupport proxyConfigSupport;
    
    @Override
    public String getType() {
        return "api";
    }
    
    @Override
    public Map<String, Object> execute(Map<String, Object> params) throws Exception {
        // 从config中解析配置
        String configJson = (String) params.get("_config");
        if (configJson == null || configJson.trim().isEmpty()) {
            throw new IllegalArgumentException("工具配置不能为空");
        }
        
        JsonNode config = objectMapper.readTree(configJson);
        
        // 获取API配置参数
        String apiUrl = config.has("apiUrl") ? config.get("apiUrl").asText() : null;
        String method = config.has("method") ? config.get("method").asText().toUpperCase() : "GET";
        String contentType = config.has("contentType") ? config.get("contentType").asText() : "application/json";
        
        if (apiUrl == null || apiUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("API地址不能为空");
        }

        // 每次调用按代理配置构建 RestTemplate（实时生效）
        RestTemplate restTemplate = proxyConfigSupport.buildRestTemplate("workflow_api_tool");
        
        // 构建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        
        // 添加自定义请求头
        if (config.has("headers")) {
            JsonNode headersNode = config.get("headers");
            if (headersNode.isObject()) {
                headersNode.fields().forEachRemaining(entry -> {
                    headers.add(entry.getKey(), entry.getValue().asText());
                });
            }
        }
        
        // 构建请求体
        Object requestBody = null;
        if ("POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method)) {
            // 从输入参数中构建请求体
            Map<String, Object> bodyParams = new HashMap<>();
            
            // 获取输入参数定义
            String inputsJson = (String) params.get("_inputs");
            if (inputsJson != null && !inputsJson.trim().isEmpty()) {
                JsonNode inputs = objectMapper.readTree(inputsJson);
                if (inputs.isArray()) {
                    for (JsonNode input : inputs) {
                        String paramName = input.get("name").asText();
                        if (params.containsKey(paramName)) {
                            bodyParams.put(paramName, params.get(paramName));
                        }
                    }
                }
            }
            
            requestBody = bodyParams;
        } else if ("GET".equals(method) || "DELETE".equals(method)) {
            // 对于GET/DELETE请求，将参数添加到URL查询字符串
            StringBuilder urlBuilder = new StringBuilder(apiUrl);
            String inputsJson = (String) params.get("_inputs");
            if (inputsJson != null && !inputsJson.trim().isEmpty()) {
                JsonNode inputs = objectMapper.readTree(inputsJson);
                if (inputs.isArray()) {
                    boolean first = !apiUrl.contains("?");
                    for (JsonNode input : inputs) {
                        String paramName = input.get("name").asText();
                        if (params.containsKey(paramName)) {
                            if (first) {
                                urlBuilder.append("?");
                                first = false;
                            } else {
                                urlBuilder.append("&");
                            }
                            urlBuilder.append(paramName).append("=").append(params.get(paramName));
                        }
                    }
                }
            }
            apiUrl = urlBuilder.toString();
        }
        
        // 创建请求实体
        HttpEntity<?> requestEntity = new HttpEntity<>(requestBody, headers);
        
        // 发送HTTP请求
        log.info("执行API工具: method={}, url={}", method, apiUrl);
        
        ResponseEntity<String> response;
        try {
            switch (method) {
                case "GET":
                    response = restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, String.class);
                    break;
                case "POST":
                    response = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, String.class);
                    break;
                case "PUT":
                    response = restTemplate.exchange(apiUrl, HttpMethod.PUT, requestEntity, String.class);
                    break;
                case "DELETE":
                    response = restTemplate.exchange(apiUrl, HttpMethod.DELETE, requestEntity, String.class);
                    break;
                case "PATCH":
                    response = restTemplate.exchange(apiUrl, HttpMethod.PATCH, requestEntity, String.class);
                    break;
                default:
                    throw new IllegalArgumentException("不支持的HTTP方法: " + method);
            }
        } catch (Exception e) {
            log.error("API请求失败: url={}", apiUrl, e);
            throw new RuntimeException("API请求失败: " + e.getMessage(), e);
        }
        
        // 解析响应
        Map<String, Object> result = new HashMap<>();
        result.put("statusCode", response.getStatusCodeValue());
        result.put("success", response.getStatusCode().is2xxSuccessful());
        
        String responseBody = response.getBody();
        if (responseBody != null && !responseBody.trim().isEmpty()) {
            try {
                // 尝试解析为JSON
                JsonNode responseJson = objectMapper.readTree(responseBody);
                
                // 应用响应映射（如果配置了）
                if (config.has("responseMapping")) {
                    JsonNode mappingNode = config.get("responseMapping");
                    if (mappingNode.isObject()) {
                        mappingNode.fields().forEachRemaining(entry -> {
                            String outputName = entry.getKey();
                            String jsonPath = entry.getValue().asText();
                            
                            // 简单的JSONPath支持（仅支持点号分隔的路径）
                            JsonNode valueNode = responseJson;
                            String[] pathParts = jsonPath.split("\\.");
                            for (String part : pathParts) {
                                if (valueNode != null && valueNode.has(part)) {
                                    valueNode = valueNode.get(part);
                                } else {
                                    valueNode = null;
                                    break;
                                }
                            }
                            
                            if (valueNode != null) {
                                if (valueNode.isTextual()) {
                                    result.put(outputName, valueNode.asText());
                                } else if (valueNode.isNumber()) {
                                    result.put(outputName, valueNode.asDouble());
                                } else if (valueNode.isBoolean()) {
                                    result.put(outputName, valueNode.asBoolean());
                                } else {
                                    result.put(outputName, valueNode.toString());
                                }
                            }
                        });
                    }
                }
                
                // 如果没有配置映射，返回整个响应
                if (result.size() <= 2) { // 只有statusCode和success
                    result.put("response", objectMapper.convertValue(responseJson, Map.class));
                }
            } catch (Exception e) {
                // 如果不是JSON，作为文本返回
                result.put("response", responseBody);
            }
        }
        
        result.put("message", "API调用成功");
        
        log.info("API工具执行成功: statusCode={}, success={}", 
                result.get("statusCode"), result.get("success"));
        
        return result;
    }
}
