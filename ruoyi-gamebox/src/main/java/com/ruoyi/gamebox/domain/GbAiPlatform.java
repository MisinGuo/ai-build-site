package com.ruoyi.gamebox.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * AI平台配置对象 gb_ai_platforms
 * 
 * @author ruoyi
 */
public class GbAiPlatform extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 平台ID */
    private Long id;

    /** 所属网站ID(0表示全局默认配置) */
    @Excel(name = "所属网站ID")
    private Long siteId;

    /** 平台名称 */
    @Excel(name = "平台名称")
    private String name;

    /** 平台类型：openai, azure_openai, anthropic, google, baidu, alibaba, zhipu */
    @Excel(name = "平台类型")
    private String platformType;

    /** API基础URL */
    @Excel(name = "API基础URL")
    private String baseUrl;

    /** API密钥 */
    private String apiKey;

    /** 组织ID（OpenAI使用） */
    private String organizationId;

    /** 默认模型 */
    @Excel(name = "默认模型")
    private String defaultModel;

    /** 可用模型列表JSON数组 */
    @Excel(name = "可用模型列表")
    private String availableModels;

    /** 分类ID */
    @Excel(name = "分类ID")
    private Long categoryId;

    /** 最大Token数 */
    @Excel(name = "最大Token数")
    private Integer maxTokens;

    /** 默认温度参数 */
    @Excel(name = "默认温度参数")
    private BigDecimal temperature;

    /** 每分钟请求限制 */
    @Excel(name = "每分钟请求限制")
    private Integer rateLimitPerMinute;

    /** 每1000 Token成本 */
    @Excel(name = "每1000 Token成本")
    private BigDecimal costPer1kTokens;

    /** 代理配置JSON */
    private String proxyConfig;

    /** 是否默认平台 */
    @Excel(name = "是否默认", readConverterExp = "0=否,1=是")
    private String isDefault;

    /** 状态：0禁用 1启用 */
    @Excel(name = "状态", readConverterExp = "0=禁用,1=启用")
    private String status;

    /** 删除标志：0存在 2删除 */
    private String delFlag;

    // ========== 多站点支持的额外字段（查询时使用，不直接对应表字段） ==========
    
    /** 查询模式：creator-创建者模式，related-关联模式 */
    private String queryMode;

    /** 是否包含默认配置（仅在创建者模式下使用） */
    private Boolean includeDefault;

    /** 关联网站数量 */
    private Integer relatedSitesCount;

    /** 排除网站数量（默认配置被排除的网站数量） */
    private Integer excludedSitesCount;

    /** 是否可见（关联模式下使用） */
    private String isVisible;

    /** 是否被排除（默认配置被某网站排除） */
    private Integer isExcluded;

    /** 关联来源：own-自有，default-默认配置，shared-跨站共享 */
    private String relationSource;

    // ========== 分类相关字段（查询时使用，不直接对应表字段） ==========
    
    /** 分类名称 */
    private String categoryName;

    /** 分类图标 */
    private String categoryIcon;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setSiteId(Long siteId) 
    {
        this.siteId = siteId;
    }

    public Long getSiteId() 
    {
        return siteId;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setPlatformType(String platformType) 
    {
        this.platformType = platformType;
    }

    public String getPlatformType() 
    {
        return platformType;
    }
    public void setBaseUrl(String baseUrl) 
    {
        this.baseUrl = baseUrl;
    }

    public String getBaseUrl() 
    {
        return baseUrl;
    }
    public void setApiKey(String apiKey) 
    {
        this.apiKey = apiKey;
    }

    public String getApiKey() 
    {
        return apiKey;
    }
    public void setOrganizationId(String organizationId) 
    {
        this.organizationId = organizationId;
    }

    public String getOrganizationId() 
    {
        return organizationId;
    }
    public void setDefaultModel(String defaultModel) 
    {
        this.defaultModel = defaultModel;
    }

    public String getDefaultModel() 
    {
        return defaultModel;
    }
    public void setAvailableModels(String availableModels) 
    {
        this.availableModels = availableModels;
    }

    public String getAvailableModels() 
    {
        return availableModels;
    }
    public void setCategoryId(Long categoryId) 
    {
        this.categoryId = categoryId;
    }

    public Long getCategoryId() 
    {
        return categoryId;
    }
    public void setMaxTokens(Integer maxTokens) 
    {
        this.maxTokens = maxTokens;
    }

    public Integer getMaxTokens() 
    {
        return maxTokens;
    }
    public void setTemperature(BigDecimal temperature) 
    {
        this.temperature = temperature;
    }

    public BigDecimal getTemperature() 
    {
        return temperature;
    }
    public void setRateLimitPerMinute(Integer rateLimitPerMinute) 
    {
        this.rateLimitPerMinute = rateLimitPerMinute;
    }

    public Integer getRateLimitPerMinute() 
    {
        return rateLimitPerMinute;
    }
    public void setCostPer1kTokens(BigDecimal costPer1kTokens) 
    {
        this.costPer1kTokens = costPer1kTokens;
    }

    public BigDecimal getCostPer1kTokens() 
    {
        return costPer1kTokens;
    }
    public void setProxyConfig(String proxyConfig) 
    {
        this.proxyConfig = proxyConfig;
    }

    public String getProxyConfig() 
    {
        return proxyConfig;
    }
    public void setIsDefault(String isDefault) 
    {
        this.isDefault = isDefault;
    }

    public String getIsDefault() 
    {
        return isDefault;
    }
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }
    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    public String getQueryMode() {
        return queryMode;
    }

    public void setQueryMode(String queryMode) {
        this.queryMode = queryMode;
    }

    public Boolean getIncludeDefault() {
        return includeDefault;
    }

    public void setIncludeDefault(Boolean includeDefault) {
        this.includeDefault = includeDefault;
    }

    public Integer getRelatedSitesCount() {
        return relatedSitesCount;
    }

    public void setRelatedSitesCount(Integer relatedSitesCount) {
        this.relatedSitesCount = relatedSitesCount;
    }

    public Integer getExcludedSitesCount() {
        return excludedSitesCount;
    }

    public void setExcludedSitesCount(Integer excludedSitesCount) {
        this.excludedSitesCount = excludedSitesCount;
    }

    public String getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(String isVisible) {
        this.isVisible = isVisible;
    }

    public Integer getIsExcluded() {
        return isExcluded;
    }

    public void setIsExcluded(Integer isExcluded) {
        this.isExcluded = isExcluded;
    }

    public String getRelationSource() {
        return relationSource;
    }

    public void setRelationSource(String relationSource) {
        this.relationSource = relationSource;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryIcon() {
        return categoryIcon;
    }

    public void setCategoryIcon(String categoryIcon) {
        this.categoryIcon = categoryIcon;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("siteId", getSiteId())
            .append("name", getName())
            .append("platformType", getPlatformType())
            .append("baseUrl", getBaseUrl())
            .append("apiKey", getApiKey())
            .append("organizationId", getOrganizationId())
            .append("defaultModel", getDefaultModel())
            .append("availableModels", getAvailableModels())
            .append("maxTokens", getMaxTokens())
            .append("temperature", getTemperature())
            .append("rateLimitPerMinute", getRateLimitPerMinute())
            .append("costPer1kTokens", getCostPer1kTokens())
            .append("proxyConfig", getProxyConfig())
            .append("isDefault", getIsDefault())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
