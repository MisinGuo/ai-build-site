package com.ruoyi.aisite.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * AI 平台配置实体 ai_platforms
 */
public class AiPlatform extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    /** 平台名称 */
    private String name;
    /** 平台类型: openai/anthropic/qwen/wenxin/deepseek/other */
    private String platformType;
    /** API Key（加密存储） */
    private String apiKey;
    /** Base URL */
    private String baseUrl;
    /** 默认模型名称 */
    private String modelName;
    /** 最大 token 数 */
    private Integer maxTokens;
    /** 温度参数 */
    private java.math.BigDecimal temperature;
    /** 扩展配置 JSONB */
    private String extraConfig;
    /** 是否默认 */
    private String isDefault;
    private String status;
    private String delFlag;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPlatformType() { return platformType; }
    public void setPlatformType(String platformType) { this.platformType = platformType; }
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }
    public Integer getMaxTokens() { return maxTokens; }
    public void setMaxTokens(Integer maxTokens) { this.maxTokens = maxTokens; }
    public java.math.BigDecimal getTemperature() { return temperature; }
    public void setTemperature(java.math.BigDecimal temperature) { this.temperature = temperature; }
    public String getExtraConfig() { return extraConfig; }
    public void setExtraConfig(String extraConfig) { this.extraConfig = extraConfig; }
    public String getIsDefault() { return isDefault; }
    public void setIsDefault(String isDefault) { this.isDefault = isDefault; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
}
