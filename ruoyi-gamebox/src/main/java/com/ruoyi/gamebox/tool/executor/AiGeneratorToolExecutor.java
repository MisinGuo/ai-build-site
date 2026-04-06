package com.ruoyi.gamebox.tool.executor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.gamebox.service.IGbAiToolService;
import com.ruoyi.gamebox.tool.ToolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI生成工具执行器
 * 这是一个通用的AI工具执行器，不是系统工具
 * 
 * 工具类型说明：
 * - AI工具（type="ai"）：用户在前端手动配置AI平台、提示词、输入输出参数
 * - 执行器按类型（"ai"）注册，执行时从数据库读取工具配置
 * - 区别于系统工具（有 @SystemTool 注解），系统工具的配置由执行器定义
 * 
 * @author ruoyi
 * @date 2026-01-04
 */
@Component
public class AiGeneratorToolExecutor implements ToolExecutor {
    
    private static final Logger log = LoggerFactory.getLogger(AiGeneratorToolExecutor.class);
    
    @Autowired
    private IGbAiToolService aiToolService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Override
    public String getType() {
        return "ai";
    }
    
    @Override
    public Map<String, Object> execute(Map<String, Object> params) throws Exception {
        // 从config中解析配置
        String configJson = (String) params.get("_config");
        if (configJson == null || configJson.trim().isEmpty()) {
            throw new IllegalArgumentException("工具配置不能为空");
        }
        
        JsonNode config = objectMapper.readTree(configJson);
        
        // 获取必要的配置参数
        Long aiPlatformId = config.has("aiPlatformId") ? config.get("aiPlatformId").asLong() : null;
        String systemPrompt = config.has("systemPrompt") ? config.get("systemPrompt").asText() : "";
        String userPromptTemplate = config.has("userPromptTemplate") ? config.get("userPromptTemplate").asText() : "";
        Double temperature = config.has("temperature") ? config.get("temperature").asDouble() : 0.7;
        Integer maxTokens = config.has("maxTokens") ? config.get("maxTokens").asInt() : 2000;
        
        if (aiPlatformId == null) {
            throw new IllegalArgumentException("请配置AI平台ID");
        }
        
        if (systemPrompt.isEmpty()) {
            throw new IllegalArgumentException("系统提示词不能为空");
        }
        
        if (userPromptTemplate.isEmpty()) {
            throw new IllegalArgumentException("用户提示词模板不能为空");
        }
        
        // 解析输出字段定义
        List<String> outputFieldNames = parseOutputFieldNames((String) params.get("_outputs"));

        // 如果有多个输出字段，在系统提示词中追加JSON格式要求
        String finalSystemPrompt = systemPrompt;
        if (outputFieldNames.size() > 1) {
            String jsonFields = String.join(", ", outputFieldNames.stream()
                    .map(n -> "\"" + n + "\"")
                    .collect(java.util.stream.Collectors.toList()));
            finalSystemPrompt = systemPrompt + "\n\n请严格以JSON对象格式返回结果，包含以下字段：" + jsonFields + "。不要包含任何额外的文字说明，只返回JSON。";
        }

        // 渲染用户提示词（替换变量）
        String userPrompt = renderPrompt(userPromptTemplate, params);
        
        // 准备AI调用参数
        Map<String, Object> aiParams = new HashMap<>();
        aiParams.put("temperature", temperature);
        aiParams.put("maxTokens", maxTokens);
        
        // 调用AI服务
        log.info("调用AI生成工具: platformId={}, temperature={}, maxTokens={}, outputFields={}", 
                aiPlatformId, temperature, maxTokens, outputFieldNames);
        
        String generatedContent = aiToolService.callAI(
            aiPlatformId,
            finalSystemPrompt,
            userPrompt,
            aiParams
        );
        
        // 根据输出字段定义映射结果
        return mapOutputResult(generatedContent, outputFieldNames);
    }
    
    /**
     * 从 _outputs JSON 中解析输出字段名列表
     * 注意：wordCount 是系统保留字段，始终自动计算，不参与 AI 映射
     */
    private List<String> parseOutputFieldNames(String outputsJson) {
        List<String> names = new ArrayList<>();
        if (outputsJson == null || outputsJson.trim().isEmpty()) {
            return names;
        }
        try {
            JsonNode outputsNode = objectMapper.readTree(outputsJson);
            if (outputsNode.isArray()) {
                for (JsonNode node : outputsNode) {
                    if (node.has("name") && !node.get("name").asText().isEmpty()) {
                        String name = node.get("name").asText();
                        // 跳过系统保留字段
                        if (!"wordCount".equals(name)) {
                            names.add(name);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("解析输出字段定义失败: {}", e.getMessage());
        }
        return names;
    }

    /**
     * 根据输出字段定义将 AI 响应内容映射到结果 Map：
     * - 0 个字段：向后兼容，返回 {generatedText, wordCount}
     * - 1 个字段：将全文直接映射到该字段名
     * - 多个字段：尝试将 AI 返回内容解析为 JSON 并按字段名取值；
     *             若 AI 未返回合法 JSON，则将全文放入第一个字段
     */
    private Map<String, Object> mapOutputResult(String generatedContent, List<String> outputFieldNames) {
        Map<String, Object> result = new HashMap<>();

        if (outputFieldNames.isEmpty()) {
            // 无自定义字段，保持向后兼容
            result.put("generatedText", generatedContent);
            result.put("wordCount", generatedContent.length());
            return result;
        }

        if (outputFieldNames.size() == 1) {
            // 单字段：直接映射
            result.put(outputFieldNames.get(0), generatedContent);
            result.put("wordCount", generatedContent.length());
            return result;
        }

        // 多字段：尝试按 JSON 解析
        String trimmed = generatedContent.trim();
        // 提取 ```json ... ``` 代码块中的内容
        if (trimmed.startsWith("```")) {
            int start = trimmed.indexOf('\n');
            int end = trimmed.lastIndexOf("```");
            if (start >= 0 && end > start) {
                trimmed = trimmed.substring(start + 1, end).trim();
            }
        }
        try {
            JsonNode json = objectMapper.readTree(trimmed);
            if (json.isObject()) {
                for (String fieldName : outputFieldNames) {
                    if (json.has(fieldName)) {
                        JsonNode v = json.get(fieldName);
                        if (v.isTextual()) {
                            result.put(fieldName, v.asText());
                        } else if (v.isNumber()) {
                            result.put(fieldName, v.numberValue());
                        } else if (v.isBoolean()) {
                            result.put(fieldName, v.booleanValue());
                        } else {
                            result.put(fieldName, objectMapper.writeValueAsString(v));
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("AI返回内容无法解析为JSON，将全文写入第一个字段: {}", e.getMessage());
        }

        // 若所有字段均未填充（AI未返回正确JSON），全文写入第一个字段
        if (result.isEmpty()) {
            result.put(outputFieldNames.get(0), generatedContent);
        }

        // 始终附带字符数，方便下游使用
        result.put("wordCount", generatedContent.length());

        return result;
    }

    /**
     * 渲染提示词模板（替换变量）
     * 使用 {{varName}} 格式
     */
    private String renderPrompt(String template, Map<String, Object> params) {
        String rendered = template;
        
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            // 跳过内部参数
            if (key.startsWith("_")) {
                continue;
            }
            
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            rendered = rendered.replace("{{" + key + "}}", value);
        }
        
        return rendered;
    }
}
