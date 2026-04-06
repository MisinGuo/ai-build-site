package com.ruoyi.gamebox.service.impl;

import com.ruoyi.gamebox.service.IAutoTranslationService;
import com.ruoyi.gamebox.service.ITranslationService;
import com.ruoyi.gamebox.service.IGbSiteService;
import com.ruoyi.gamebox.domain.GbSite;
import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 自动翻译Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-01-11
 */
@Service
public class AutoTranslationServiceImpl implements IAutoTranslationService
{
    private static final Logger log = LoggerFactory.getLogger(AutoTranslationServiceImpl.class);
    
    @Autowired
    private ITranslationService translationService;
    
    @Autowired
    private IGbSiteService siteService;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Value("${translation.api.url:https://m3u8-player.5yxy5.com/api/forward/https://translate.googleapis.com/translate_a/single}")
    private String translationApiUrl;
    
    @Value("${translation.api.timeout:30000}")
    private int timeout;
    
    @Override
    public String translateText(String text, String targetLang) 
    {
        // 参数校验
        if (text == null || text.trim().isEmpty()) {
            return text;
        }
        
        // 最多重试3次
        int maxRetries = 3;
        int retryDelay = 1000; // 1秒
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                // URL编码文本
                String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8.toString());
                
                // 构建完整的API URL
                String url = String.format("%s?client=gtx&dt=t&sl=auto&tl=%s&q=%s", 
                    translationApiUrl, targetLang, encodedText);
                
                // 设置请求头
                HttpHeaders headers = new HttpHeaders();
                headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
                HttpEntity<String> entity = new HttpEntity<>(headers);
                
                // 发送请求
                ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
                String response = responseEntity.getBody();
                
                // 检查响应是否为空
                if (response == null || response.trim().isEmpty()) {
                    log.warn("翻译API返回空响应 (尝试 {}/{}),使用原文", attempt, maxRetries);
                    if (attempt < maxRetries) {
                        Thread.sleep(retryDelay);
                        continue;
                    }
                    return text;
                }
                
                // 解析响应 - 按照Python逻辑: result[0]是翻译段落数组
                JsonNode rootNode = objectMapper.readTree(response);
                StringBuilder translatedText = new StringBuilder();
                
                // 提取翻译结果 - result[0] 是数组,每个元素也是数组 [翻译文本, 原文, ...]
                JsonNode firstArray = rootNode.get(0);
                if (firstArray != null && firstArray.isArray()) {
                    for (JsonNode item : firstArray) {
                        if (item.isArray() && item.size() > 0) {
                            String part = item.get(0).asText();
                            translatedText.append(part);
                        }
                    }
                }
                
                // 结果不需要URL解码(已经是解码后的文本)
                String result = translatedText.toString();
                
                if (result.isEmpty()) {
                    log.warn("翻译结果为空 (尝试 {}/{}),使用原文", attempt, maxRetries);
                    if (attempt < maxRetries) {
                        Thread.sleep(retryDelay);
                        continue;
                    }
                    return text;
                }
                
                // 翻译成功
                if (attempt > 1) {
                    log.info("翻译成功 (重试 {} 次后)", attempt - 1);
                }
                return result;
                
            } catch (Exception e) {
                log.error("翻译失败 (尝试 {}/{}, 目标语言: {}): {}", attempt, maxRetries, targetLang, e.getMessage());
                
                // 如果还有重试机会，等待后重试
                if (attempt < maxRetries) {
                    try {
                        Thread.sleep(retryDelay * attempt); // 递增延迟
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                } else {
                    // 最后一次尝试也失败了，返回原文
                    log.warn("翻译最终失败，使用原文");
                    return text;
                }
            }
        }
        
        // 翻译失败时返回原文
        return text;
    }
    
    @Override
    public Map<String, String> translateEntityFields(String entityType, Long entityId, 
                                                    String targetLang, Map<String, String> fields) 
    {
        Map<String, String> translatedFields = new HashMap<>();
        
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            String fieldName = entry.getKey();
            String originalText = entry.getValue();
            
            if (originalText != null && !originalText.trim().isEmpty()) {
                String translatedText = translateText(originalText, targetLang);
                translatedFields.put(fieldName, translatedText);
            }
        }
        
        return translatedFields;
    }
    
    @Override
    public boolean autoTranslateEntity(String entityType, Long entityId, Long siteId, Map<String, String> fields) 
    {
        try {
            // 从站点配置获取支持的语言列表
            GbSite site = siteService.selectGbSiteById(siteId);
            if (site == null) {
                log.error("站点不存在: siteId={}", siteId);
                return false;
            }
            
            String supportedLocalesJson = site.getSupportedLocales();
            if (supportedLocalesJson == null || supportedLocalesJson.isEmpty()) {
                log.warn("站点未配置支持的语言: siteId={}", siteId);
                return false;
            }
            
            java.util.List<String> supportedLocales = objectMapper.readValue(
                supportedLocalesJson, 
                new TypeReference<java.util.List<String>>(){}
            );
            
            // 过滤掉默认语言（默认语言在主表中，不需要翻译）
            String defaultLocale = site.getDefaultLocale();
            
            int totalTranslations = 0;
            int failedTranslations = 0;
            
            for (String targetLang : supportedLocales) {
                // 跳过默认语言
                if (targetLang.equals(defaultLocale)) {
                    continue;
                }
                log.info("开始为实体生成翻译: type={}, id={}, lang={}", entityType, entityId, targetLang);
                
                try {
                    Map<String, String> translatedFields = translateEntityFields(entityType, entityId, targetLang, fields);
                    
                    // 保存翻译结果
                    for (Map.Entry<String, String> entry : translatedFields.entrySet()) {
                        try {
                            translationService.saveTranslation(entityType, entityId, 
                                entry.getKey(), targetLang, entry.getValue());
                            totalTranslations++;
                        } catch (Exception e) {
                            log.error("保存翻译失败: type={}, id={}, field={}, lang={}", 
                                entityType, entityId, entry.getKey(), targetLang, e);
                            failedTranslations++;
                        }
                    }
                    
                    log.info("实体翻译完成: type={}, id={}, lang={}, fields={}", 
                        entityType, entityId, targetLang, translatedFields.size());
                } catch (Exception e) {
                    log.error("语言翻译失败: type={}, id={}, lang={}", entityType, entityId, targetLang, e);
                    failedTranslations += fields.size();
                }
            }
            
            if (failedTranslations > 0) {
                log.warn("实体翻译部分失败: type={}, id={}, 成功={}, 失败={}", 
                    entityType, entityId, totalTranslations, failedTranslations);
            }
            
            // 只要有部分成功就返回true
            return totalTranslations > 0;
            
        } catch (Exception e) {
            log.error("自动翻译失败: type={}, id={}", entityType, entityId, e);
            return false;
        }
    }
}
