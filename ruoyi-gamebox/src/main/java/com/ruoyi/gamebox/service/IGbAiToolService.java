package com.ruoyi.gamebox.service;

import java.util.Map;

/**
 * AI工具Service接口
 * 
 * @author ruoyi
 */
public interface IGbAiToolService 
{
    /**
     * 调用AI平台接口
     * 
     * @param platformId 平台ID
     * @param systemPrompt 系统提示词
     * @param userPrompt 用户提示词
     * @param parameters AI参数
     * @return AI响应结果
     */
    String callAI(Long platformId, String systemPrompt, String userPrompt, Map<String, Object> parameters) throws Exception;
}
