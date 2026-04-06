package com.ruoyi.gamebox.service;

import java.util.List;
import java.util.Map;

/**
 * 多语言翻译Service接口
 * 
 * @author ruoyi
 * @date 2026-01-10
 */
public interface ITranslationService
{
    /**
     * 获取翻译字段值
     * 
     * @param entityType 实体类型
     * @param entityId 实体ID
     * @param fieldName 字段名称
     * @param locale 语言代码
     * @return 翻译后的值，如果不存在返回null
     */
    String getTranslatedField(String entityType, Long entityId, String fieldName, String locale);
    
    /**
     * 保存翻译
     * 
     * @param entityType 实体类型
     * @param entityId 实体ID
     * @param fieldName 字段名称
     * @param locale 语言代码
     * @param value 翻译值
     */
    void saveTranslation(String entityType, Long entityId, String fieldName, String locale, String value);
    
    /**
     * 批量获取实体翻译
     * 
     * @param entityType 实体类型
     * @param entityId 实体ID
     * @param locale 语言代码
     * @return 字段名 -> 翻译值的映射
     */
    Map<String, String> getEntityTranslations(String entityType, Long entityId, String locale);
    
    /**
     * 批量获取多个实体的翻译
     * 
     * @param entityType 实体类型
     * @param entityIds 实体ID列表
     * @param locale 语言代码
     * @return 实体ID -> (字段名 -> 翻译值) 的嵌套映射
     */
    Map<Long, Map<String, String>> getBatchEntityTranslations(String entityType, List<Long> entityIds, String locale);
    
    /**
     * 获取实体的所有语言翻译（用于编辑）
     * 
     * @param entityType 实体类型
     * @param entityId 实体ID
     * @return 翻译列表，每个元素包含 locale, fieldName, fieldValue
     */
    List<Map<String, Object>> getAllEntityTranslations(String entityType, Long entityId);
    
    /**
     * 删除实体的所有翻译
     * 
     * @param entityType 实体类型
     * @param entityId 实体ID
     */
    void deleteEntityTranslations(String entityType, Long entityId);

    /**
     * 删除单个字段的翻译
     * @param entityType 实体类型
     * @param entityId 实体ID
     * @param locale 语言
     * @param fieldName 字段名
     */
    void deleteFieldTranslation(String entityType, Long entityId, String locale, String fieldName);
}