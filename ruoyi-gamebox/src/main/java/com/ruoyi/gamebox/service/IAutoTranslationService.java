package com.ruoyi.gamebox.service;

import java.util.Map;

/**
 * 自动翻译Service接口
 * 
 * @author ruoyi
 * @date 2026-01-11
 */
public interface IAutoTranslationService 
{
    /**
     * 翻译单个文本
     * 
     * @param text 原始文本
     * @param targetLang 目标语言代码：zh-TW, en
     * @return 翻译后的文本
     */
    String translateText(String text, String targetLang);
    
    /**
     * 批量翻译实体的所有字段
     * 
     * @param entityType 实体类型
     * @param entityId 实体ID
     * @param targetLang 目标语言代码
     * @param fields 需要翻译的字段映射（字段名 -> 原始文本）
     * @return 翻译结果映射（字段名 -> 翻译文本）
     */
    Map<String, String> translateEntityFields(String entityType, Long entityId, String targetLang, Map<String, String> fields);
    
    /**
     * 为实体自动生成所有语言的翻译
     * 
     * @param entityType 实体类型
     * @param entityId 实体ID
     * @param siteId 站点ID（用于获取支持的语言列表）
     * @param fields 需要翻译的字段映射
     * @return 是否成功
     */
    boolean autoTranslateEntity(String entityType, Long entityId, Long siteId, Map<String, String> fields);
}
