package com.ruoyi.gamebox.service.impl;

import com.ruoyi.gamebox.domain.GbTranslation;
import com.ruoyi.gamebox.mapper.GbTranslationMapper;
import com.ruoyi.gamebox.service.ITranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 多语言翻译Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-01-10
 */
@Service
public class TranslationServiceImpl implements ITranslationService
{
    @Autowired
    private GbTranslationMapper translationMapper;
    
    @Override
    public String getTranslatedField(String entityType, Long entityId, String fieldName, String locale) {
        GbTranslation query = new GbTranslation();
        query.setEntityType(entityType);
        query.setEntityId(entityId);
        query.setFieldName(fieldName);
        query.setLocale(locale);
        
        GbTranslation translation = translationMapper.selectTranslation(query);
        return translation != null ? translation.getFieldValue() : null;
    }
    
    @Override
    public void saveTranslation(String entityType, Long entityId, String fieldName, String locale, String value) {
        // 先查询是否已存在
        GbTranslation query = new GbTranslation();
        query.setEntityType(entityType);
        query.setEntityId(entityId);
        query.setFieldName(fieldName);
        query.setLocale(locale);
        
        GbTranslation existing = translationMapper.selectTranslation(query);
        
        if (existing != null) {
            // 更新现有记录
            existing.setFieldValue(value);
            translationMapper.updateTranslation(existing);
        } else {
            // 插入新记录
            GbTranslation newTranslation = new GbTranslation();
            newTranslation.setEntityType(entityType);
            newTranslation.setEntityId(entityId);
            newTranslation.setFieldName(fieldName);
            newTranslation.setLocale(locale);
            newTranslation.setFieldValue(value);
            translationMapper.insertTranslation(newTranslation);
        }
    }
    
    @Override
    public Map<String, String> getEntityTranslations(String entityType, Long entityId, String locale) {
        GbTranslation query = new GbTranslation();
        query.setEntityType(entityType);
        query.setEntityId(entityId);
        query.setLocale(locale);
        
        List<GbTranslation> translations = translationMapper.selectTranslationList(query);
        
        return translations.stream()
                .collect(Collectors.toMap(
                    GbTranslation::getFieldName,
                    GbTranslation::getFieldValue,
                    (existing, replacement) -> replacement // 如果有重复key，使用新值
                ));
    }
    
    @Override
    public Map<Long, Map<String, String>> getBatchEntityTranslations(String entityType, List<Long> entityIds, String locale) {
        if (CollectionUtils.isEmpty(entityIds)) {
            return new HashMap<>();
        }
        
        List<GbTranslation> translations = translationMapper.selectBatchTranslations(entityType, entityIds, locale);
        
        Map<Long, Map<String, String>> result = new HashMap<>();
        
        for (GbTranslation translation : translations) {
            Long entityId = translation.getEntityId();
            String fieldName = translation.getFieldName();
            String fieldValue = translation.getFieldValue();
            
            result.computeIfAbsent(entityId, k -> new HashMap<>())
                  .put(fieldName, fieldValue);
        }
        
        return result;
    }
    
    @Override
    public List<Map<String, Object>> getAllEntityTranslations(String entityType, Long entityId) {
        GbTranslation query = new GbTranslation();
        query.setEntityType(entityType);
        query.setEntityId(entityId);
        
        List<GbTranslation> translations = translationMapper.selectTranslationList(query);
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (GbTranslation translation : translations) {
            Map<String, Object> item = new HashMap<>();
            item.put("locale", translation.getLocale());
            item.put("fieldName", translation.getFieldName());
            item.put("fieldValue", translation.getFieldValue());
            result.add(item);
        }
        
        return result;
    }
    
    @Override
    public void deleteEntityTranslations(String entityType, Long entityId) {
        GbTranslation query = new GbTranslation();
        query.setEntityType(entityType);
        query.setEntityId(entityId);
        
        List<GbTranslation> translations = translationMapper.selectTranslationList(query);
        
        for (GbTranslation translation : translations) {
            translationMapper.deleteTranslationById(translation.getId());
        }
    }
    
    @Override
    public void deleteFieldTranslation(String entityType, Long entityId, String locale, String fieldName) {
        GbTranslation query = new GbTranslation();
        query.setEntityType(entityType);
        query.setEntityId(entityId);
        query.setLocale(locale);
        query.setFieldName(fieldName);
        List<GbTranslation> list = translationMapper.selectTranslationList(query);
        for (GbTranslation t : list) {
            translationMapper.deleteTranslationById(t.getId());
        }
    }
}