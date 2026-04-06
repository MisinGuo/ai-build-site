package com.ruoyi.gamebox.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

/**
 * OpenAI API Feign 客户端
 * 统一规范化的 AI 平台调用接口
 * 
 * @author ruoyi
 */
@FeignClient(
    name = "openai-client",
    url = "${ai.openai.base-url:https://api.openai.com}",
    configuration = com.ruoyi.gamebox.feign.FeignConfig.class
)
public interface OpenAIClient {

    /**
     * 调用 Chat Completions API
     * 
     * @param authorization Authorization header (Bearer token)
     * @param organization  Organization ID (optional)
     * @param requestBody   请求体
     * @return AI 响应
     */
    @PostMapping(value = "/chat/completions", consumes = "application/json")
    Map<String, Object> chatCompletions(
        @RequestHeader("Authorization") String authorization,
        @RequestHeader(value = "OpenAI-Organization", required = false) String organization,
        @RequestBody Map<String, Object> requestBody
    );
}
