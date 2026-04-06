package com.ruoyi.web.controller.gamebox;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.gamebox.service.ITranslationService;
import com.ruoyi.gamebox.service.IAutoTranslationService;
import com.ruoyi.gamebox.service.IGbSiteService;
import com.ruoyi.gamebox.domain.GbSite;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 多语言翻译管理Controller
 * 
 * @author ruoyi
 * @date 2026-01-10
 */
@RestController
@RequestMapping("/gamebox/translation")
public class TranslationController extends BaseController
{
    @Autowired
    private ITranslationService translationService;
    
    @Autowired
    private IAutoTranslationService autoTranslationService;
    
    @Autowired
    private IGbSiteService siteService;
    
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 获取站点支持的语言列表
     */
    @PreAuthorize("@ss.hasPermi('gamebox:translation:query')")
    @GetMapping("/supported-locales/{siteId}")
    public AjaxResult getSupportedLocales(@PathVariable("siteId") Long siteId)
    {
        try {
            GbSite site = siteService.selectGbSiteById(siteId);
            if (site == null) {
                return error("站点不存在");
            }
            
            List<Map<String, String>> localeList = new ArrayList<>();
            
            // 添加默认语言（如果有）
            if (site.getDefaultLocale() != null && !site.getDefaultLocale().isEmpty()) {
                Map<String, String> defaultLocale = new HashMap<>();
                defaultLocale.put("value", site.getDefaultLocale());
                defaultLocale.put("label", getLocaleLabel(site.getDefaultLocale()));
                defaultLocale.put("isDefault", "true");
                localeList.add(defaultLocale);
            }
            
            // 添加支持的其他语言
            String supportedLocalesJson = site.getSupportedLocales();
            if (supportedLocalesJson != null && !supportedLocalesJson.isEmpty()) {
                List<String> supportedLocales = objectMapper.readValue(
                    supportedLocalesJson, 
                    new TypeReference<List<String>>(){}
                );
                
                for (String locale : supportedLocales) {
                    // 跳过默认语言（已添加）
                    if (locale.equals(site.getDefaultLocale())) {
                        continue;
                    }
                    Map<String, String> localeItem = new HashMap<>();
                    localeItem.put("value", locale);
                    localeItem.put("label", getLocaleLabel(locale));
                    localeItem.put("isDefault", "false");
                    localeList.add(localeItem);
                }
            }
            
            return success(localeList);
        } catch (Exception e) {
            logger.error("获取支持的语言失败", e);
            return error("获取支持的语言失败: " + e.getMessage());
        }
    }
    
    /**
     * 将语言代码转换为显示标签
     */
    private String getLocaleLabel(String locale) {
        switch (locale) {
            case "zh-CN":
                return "简体中文";
            case "zh-TW":
                return "繁体中文";
            case "en":
                return "English";
            case "en-US":
                return "English (US)";
            case "ja":
                return "日本語";
            case "ko":
                return "한국어";
            default:
                return locale;
        }
    }

    /**
     * 获取实体的翻译数据
     */
    @PreAuthorize("@ss.hasPermi('gamebox:translation:query')")
    @GetMapping("/{entityType}/{entityId}")
    public AjaxResult getEntityTranslations(@PathVariable("entityType") String entityType,
                                          @PathVariable("entityId") Long entityId,
                                          @RequestParam("locale") String locale)
    {
        Map<String, String> translations = translationService.getEntityTranslations(entityType, entityId, locale);
        return success(translations);
    }
    
    /**
     * 获取实体的所有语言翻译数据
     */
    @PreAuthorize("@ss.hasPermi('gamebox:translation:query')")
    @GetMapping("/{entityType}/{entityId}/all")
    public AjaxResult getAllEntityTranslations(@PathVariable("entityType") String entityType,
                                              @PathVariable("entityId") Long entityId)
    {
        List<Map<String, Object>> translations = translationService.getAllEntityTranslations(entityType, entityId);
        return success(translations);
    }

    /**
     * 保存单个翻译字段
     */
    @PreAuthorize("@ss.hasPermi('gamebox:translation:edit')")
    @Log(title = "翻译管理", businessType = BusinessType.UPDATE)
    @PostMapping("/save")
    public AjaxResult saveTranslation(@RequestBody TranslationSaveRequest request)
    {
        translationService.saveTranslation(request.getEntityType(), request.getEntityId(), 
                                         request.getFieldName(), request.getLocale(), request.getValue());
        return success();
    }
    
    /**
     * 批量保存翻译数据
     */
    @PreAuthorize("@ss.hasPermi('gamebox:translation:edit')")
    @Log(title = "翻译管理", businessType = BusinessType.UPDATE)
    @PostMapping("/batch-save")
    public AjaxResult batchSaveTranslations(@RequestBody BatchTranslationSaveRequest request)
    {
        for (Map.Entry<String, String> entry : request.getTranslations().entrySet()) {
            translationService.saveTranslation(request.getEntityType(), request.getEntityId(),
                                             entry.getKey(), request.getLocale(), entry.getValue());
        }
        return success();
    }

    /**
     * 删除实体的所有翻译
     */
    @PreAuthorize("@ss.hasPermi('gamebox:translation:remove')")
    @Log(title = "翻译管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{entityType}/{entityId}")
    public AjaxResult deleteEntityTranslations(@PathVariable("entityType") String entityType,
                                             @PathVariable("entityId") Long entityId)
    {
        translationService.deleteEntityTranslations(entityType, entityId);
        return success();
    }

    /**
     * 删除单个字段的翻译
     */
    @PreAuthorize("@ss.hasPermi('gamebox:translation:remove')")
    @Log(title = "翻译管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{entityType}/{entityId}/{locale}/{fieldName}")
    public AjaxResult deleteFieldTranslation(@PathVariable("entityType") String entityType,
                                            @PathVariable("entityId") Long entityId,
                                            @PathVariable("locale") String locale,
                                            @PathVariable("fieldName") String fieldName) {
        translationService.deleteFieldTranslation(entityType, entityId, locale, fieldName);
        return success();
    }
    
    /**
     * 自动翻译单个文本
     */
    @PreAuthorize("@ss.hasPermi('gamebox:translation:edit')")
    @PostMapping("/auto-translate-text")
    public AjaxResult autoTranslateText(@RequestBody AutoTranslateTextRequest request)
    {
        String translatedText = autoTranslationService.translateText(request.getText(), request.getTargetLang());
        return success(translatedText);
    }
    
    /**
     * 自动翻译实体的所有字段
     */
    @PreAuthorize("@ss.hasPermi('gamebox:translation:edit')")
    @Log(title = "自动翻译", businessType = BusinessType.UPDATE)
    @PostMapping("/auto-translate-entity")
    public AjaxResult autoTranslateEntity(@RequestBody AutoTranslateEntityRequest request)
    {
        boolean success = autoTranslationService.autoTranslateEntity(
            request.getEntityType(), 
            request.getEntityId(),
            request.getSiteId(),
            request.getFields()
        );
        
        if (success) {
            return success("自动翻译完成");
        } else {
            return error("自动翻译失败");
        }
    }
    
    /**
     * 批量自动翻译多个实体
     */
    @PreAuthorize("@ss.hasPermi('gamebox:translation:edit')")
    @Log(title = "批量自动翻译", businessType = BusinessType.UPDATE)
    @PostMapping("/batch-auto-translate")
    public AjaxResult batchAutoTranslate(@RequestBody BatchAutoTranslateRequest request)
    {
        int successCount = 0;
        int failCount = 0;
        
        for (BatchAutoTranslateRequest.EntityItem item : request.getEntities()) {
            try {
                boolean success = autoTranslationService.autoTranslateEntity(
                    request.getEntityType(),
                    item.getEntityId(),
                    request.getSiteId(),
                    item.getFields()
                );
                
                if (success) {
                    successCount++;
                } else {
                    failCount++;
                }
            } catch (Exception e) {
                logger.error("批量翻译失败: entityId={}", item.getEntityId(), e);
                failCount++;
            }
        }
        
        return success(String.format("批量翻译完成：成功%d个，失败%d个", successCount, failCount));
    }

    /**
     * 翻译保存请求实体
     */
    public static class TranslationSaveRequest {
        private String entityType;
        private Long entityId;
        private String fieldName;
        private String locale;
        private String value;
        
        // getter and setter
        public String getEntityType() { return entityType; }
        public void setEntityType(String entityType) { this.entityType = entityType; }
        
        public Long getEntityId() { return entityId; }
        public void setEntityId(Long entityId) { this.entityId = entityId; }
        
        public String getFieldName() { return fieldName; }
        public void setFieldName(String fieldName) { this.fieldName = fieldName; }
        
        public String getLocale() { return locale; }
        public void setLocale(String locale) { this.locale = locale; }
        
        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
    }
    
    /**
     * 批量翻译保存请求实体
     */
    public static class BatchTranslationSaveRequest {
        private String entityType;
        private Long entityId;
        private String locale;
        private Map<String, String> translations;
        
        // getter and setter
        public String getEntityType() { return entityType; }
        public void setEntityType(String entityType) { this.entityType = entityType; }
        
        public Long getEntityId() { return entityId; }
        public void setEntityId(Long entityId) { this.entityId = entityId; }
        
        public String getLocale() { return locale; }
        public void setLocale(String locale) { this.locale = locale; }
        
        public Map<String, String> getTranslations() { return translations; }
        public void setTranslations(Map<String, String> translations) { this.translations = translations; }
    }
    
    /**
     * 自动翻译文本请求实体
     */
    public static class AutoTranslateTextRequest {
        private String text;
        private String targetLang;
        
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        
        public String getTargetLang() { return targetLang; }
        public void setTargetLang(String targetLang) { this.targetLang = targetLang; }
    }
    
    /**
     * 自动翻译实体请求
     */
    public static class AutoTranslateEntityRequest {
        private String entityType;
        private Long entityId;
        private Long siteId;
        private Map<String, String> fields;  // 字段名 -> 原始文本
        
        public String getEntityType() { return entityType; }
        public void setEntityType(String entityType) { this.entityType = entityType; }
        
        public Long getEntityId() { return entityId; }
        public void setEntityId(Long entityId) { this.entityId = entityId; }
        
        public Long getSiteId() { return siteId; }
        public void setSiteId(Long siteId) { this.siteId = siteId; }
        
        public Map<String, String> getFields() { return fields; }
        public void setFields(Map<String, String> fields) { this.fields = fields; }
    }
    
    /**
     * 批量自动翻译请求
     */
    public static class BatchAutoTranslateRequest {
        private String entityType;
        private Long siteId;
        private List<EntityItem> entities;
        
        public String getEntityType() { return entityType; }
        public void setEntityType(String entityType) { this.entityType = entityType; }
        
        public Long getSiteId() { return siteId; }
        public void setSiteId(Long siteId) { this.siteId = siteId; }
        
        public List<EntityItem> getEntities() { return entities; }
        public void setEntities(List<EntityItem> entities) { this.entities = entities; }
        
        public static class EntityItem {
            private Long entityId;
            private Map<String, String> fields;  // 字段名 -> 原始文本
            
            public Long getEntityId() { return entityId; }
            public void setEntityId(Long entityId) { this.entityId = entityId; }
            
            public Map<String, String> getFields() { return fields; }
            public void setFields(Map<String, String> fields) { this.fields = fields; }
        }
    }
}