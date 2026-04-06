package com.ruoyi.aisite.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 站点实体 ai_sites
 */
public class AiSite extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String domain;
    private String industry;
    private Long templateId;
    private String logoUrl;
    private String faviconUrl;
    private String description;
    private String seoTitle;
    private String seoKeywords;
    private String seoDescription;
    /** JSONB → String */
    private String siteConfig;
    private Long storageConfigId;
    private String defaultLocale;
    /** JSONB → String */
    private String supportedLocales;
    private String i18nMode;
    private String deployStatus;
    private String cfPagesProject;
    private String cfDeployUrl;
    private Long matrixGroupId;
    private Long creatorId;
    private Long deptId;
    private String status;
    private String delFlag;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }
    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }
    public Long getTemplateId() { return templateId; }
    public void setTemplateId(Long templateId) { this.templateId = templateId; }
    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
    public String getFaviconUrl() { return faviconUrl; }
    public void setFaviconUrl(String faviconUrl) { this.faviconUrl = faviconUrl; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getSeoTitle() { return seoTitle; }
    public void setSeoTitle(String seoTitle) { this.seoTitle = seoTitle; }
    public String getSeoKeywords() { return seoKeywords; }
    public void setSeoKeywords(String seoKeywords) { this.seoKeywords = seoKeywords; }
    public String getSeoDescription() { return seoDescription; }
    public void setSeoDescription(String seoDescription) { this.seoDescription = seoDescription; }
    public String getSiteConfig() { return siteConfig; }
    public void setSiteConfig(String siteConfig) { this.siteConfig = siteConfig; }
    public Long getStorageConfigId() { return storageConfigId; }
    public void setStorageConfigId(Long storageConfigId) { this.storageConfigId = storageConfigId; }
    public String getDefaultLocale() { return defaultLocale; }
    public void setDefaultLocale(String defaultLocale) { this.defaultLocale = defaultLocale; }
    public String getSupportedLocales() { return supportedLocales; }
    public void setSupportedLocales(String supportedLocales) { this.supportedLocales = supportedLocales; }
    public String getI18nMode() { return i18nMode; }
    public void setI18nMode(String i18nMode) { this.i18nMode = i18nMode; }
    public String getDeployStatus() { return deployStatus; }
    public void setDeployStatus(String deployStatus) { this.deployStatus = deployStatus; }
    public String getCfPagesProject() { return cfPagesProject; }
    public void setCfPagesProject(String cfPagesProject) { this.cfPagesProject = cfPagesProject; }
    public String getCfDeployUrl() { return cfDeployUrl; }
    public void setCfDeployUrl(String cfDeployUrl) { this.cfDeployUrl = cfDeployUrl; }
    public Long getMatrixGroupId() { return matrixGroupId; }
    public void setMatrixGroupId(Long matrixGroupId) { this.matrixGroupId = matrixGroupId; }
    public Long getCreatorId() { return creatorId; }
    public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }
    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
}
