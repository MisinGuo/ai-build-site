package com.ruoyi.gamebox.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 网站对象 gb_sites
 * 
 * @author ruoyi
 */
public class GbSite extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 网站名称 */
    @Excel(name = "网站名称")
    @NotBlank(message = "网站名称不能为空")
    @Size(min = 0, max = 100, message = "网站名称长度不能超过100个字符")
    private String name;

    /** 站点编码 */
    @Excel(name = "站点编码")
    @NotBlank(message = "站点编码不能为空")
    @Size(min = 0, max = 50, message = "站点编码长度不能超过50个字符")
    private String code;

    /** 分类ID */
    @Excel(name = "分类ID")
    private Long categoryId;
    /** 分类名称 */
    private String categoryName;

    /** 分类图标 */
    private String categoryIcon;
    /** 网站域名 */
    @Excel(name = "网站域名")
    @NotBlank(message = "网站域名不能为空")
    @Size(min = 0, max = 255, message = "网站域名长度不能超过255个字符")
    private String domain;

    /** 网站类型：game-游戏推广 drama-短剧推广 mixed-混合 */
    @Excel(name = "网站类型", readConverterExp = "game=游戏推广,drama=短剧推广,mixed=混合")
    private String siteType;

    /** 网站Logo */
    private String logoUrl;

    /** 网站Favicon */
    private String faviconUrl;

    /** 网站描述 */
    private String description;

    /** SEO标题 */
    private String seoTitle;

    /** SEO关键词 */
    private String seoKeywords;

    /** SEO描述 */
    private String seoDescription;

    /** 网站配置（JSON） */
    private String config;

    /** 默认存储配置ID */
    private Long storageConfigId;

    /** 默认语言 */
    private String defaultLocale;

    /** 支持的语言列表（JSON数组） */
    private String supportedLocales;

    /** 多语言模式：subdirectory-子目录 subdomain-子域名 query-参数 */
    private String i18nMode;

    /** 排序 */
    @Excel(name = "排序")
    private Integer sortOrder;

    /** 站点功能类型：seo_site-自动获客站 landing_site-落地站 */
    private String siteFunctionType;

    /** 建站状态：created / code_pulled / configured / deployed */
    private String setupStatus;

    /** 关联模板ID */
    private Long templateId;

    /** 是否个人默认站点：0-普通站点 1-用户专属个人默认站点 */
    private Integer isPersonal;

    /** 是否为当前用户的默认站点（查询时从关联表带出，非 DB 字段） */
    private Integer isDefault;

    /** 状态：0-禁用 1-启用 */
    @Excel(name = "状态", readConverterExp = "0=禁用,1=启用")
    private String status;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    /** 用户ID（前端上传用，用于建立用户-网站关联，非DB字段） */
    private Long userId;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getCode()
    {
        return code;
    }

    public void setCategoryId(Long categoryId)
    {
        this.categoryId = categoryId;
    }

    public Long getCategoryId()
    {
        return categoryId;
    }

    public void setCategoryName(String categoryName)
    {
        this.categoryName = categoryName;
    }

    public String getCategoryName()
    {
        return categoryName;
    }

    public void setCategoryIcon(String categoryIcon)
    {
        this.categoryIcon = categoryIcon;
    }

    public String getCategoryIcon()
    {
        return categoryIcon;
    }

    public void setDomain(String domain)
    {
        this.domain = domain;
    }

    public String getDomain()
    {
        return domain;
    }

    public void setSiteType(String siteType)
    {
        this.siteType = siteType;
    }

    public String getSiteType()
    {
        return siteType;
    }

    public void setLogoUrl(String logoUrl)
    {
        this.logoUrl = logoUrl;
    }

    public String getLogoUrl()
    {
        return logoUrl;
    }

    public void setFaviconUrl(String faviconUrl)
    {
        this.faviconUrl = faviconUrl;
    }

    public String getFaviconUrl()
    {
        return faviconUrl;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }

    public void setSeoTitle(String seoTitle)
    {
        this.seoTitle = seoTitle;
    }

    public String getSeoTitle()
    {
        return seoTitle;
    }

    public void setSeoKeywords(String seoKeywords)
    {
        this.seoKeywords = seoKeywords;
    }

    public String getSeoKeywords()
    {
        return seoKeywords;
    }

    public void setSeoDescription(String seoDescription)
    {
        this.seoDescription = seoDescription;
    }

    public String getSeoDescription()
    {
        return seoDescription;
    }

    public void setConfig(String config)
    {
        this.config = config;
    }

    public String getConfig()
    {
        return config;
    }

    public void setStorageConfigId(Long storageConfigId)
    {
        this.storageConfigId = storageConfigId;
    }

    public Long getStorageConfigId()
    {
        return storageConfigId;
    }

    public void setDefaultLocale(String defaultLocale)
    {
        this.defaultLocale = defaultLocale;
    }

    public String getDefaultLocale()
    {
        return defaultLocale;
    }

    public void setSupportedLocales(String supportedLocales)
    {
        this.supportedLocales = supportedLocales;
    }

    public String getSupportedLocales()
    {
        return supportedLocales;
    }

    public void setI18nMode(String i18nMode)
    {
        this.i18nMode = i18nMode;
    }

    public String getI18nMode()
    {
        return i18nMode;
    }

    public void setSortOrder(Integer sortOrder)
    {
        this.sortOrder = sortOrder;
    }

    public Integer getSortOrder()
    {
        return sortOrder;
    }

    public void setIsPersonal(Integer isPersonal)
    {
        this.isPersonal = isPersonal;
    }

    public Integer getIsPersonal()
    {
        return isPersonal;
    }

    public void setIsDefault(Integer isDefault)
    {
        this.isDefault = isDefault;
    }

    public Integer getIsDefault()
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

    public void setSiteFunctionType(String siteFunctionType)
    {
        this.siteFunctionType = siteFunctionType;
    }

    public String getSiteFunctionType()
    {
        return siteFunctionType;
    }

    public void setSetupStatus(String setupStatus)
    {
        this.setupStatus = setupStatus;
    }

    public String getSetupStatus()
    {
        return setupStatus;
    }

    public void setTemplateId(Long templateId)
    {
        this.templateId = templateId;
    }

    public Long getTemplateId()
    {
        return templateId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getUserId()
    {
        return userId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("domain", getDomain())
            .append("siteType", getSiteType())
            .append("logoUrl", getLogoUrl())
            .append("description", getDescription())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
