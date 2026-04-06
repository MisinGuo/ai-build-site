package com.ruoyi.gamebox.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 网站-AI平台配置关联对象 gb_site_ai_platform_relation
 * 
 * @author ruoyi
 * @date 2025-12-31
 */
public class GbSiteAiPlatformRelation extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 网站ID */
    @Excel(name = "网站ID")
    private Long siteId;

    /** AI平台配置ID */
    @Excel(name = "AI平台配置ID")
    private Long aiPlatformId;

    /** 关联类型：include-正向关联, exclude-排除 */
    @Excel(name = "关联类型", readConverterExp = "include=正向关联,exclude=排除")
    private String relationType;

    /** 是否可见：0-否 1-是 */
    @Excel(name = "是否可见", readConverterExp = "0=否,1=是")
    private String isVisible;

    /** 是否可编辑：0-否 1-是 */
    @Excel(name = "是否可编辑", readConverterExp = "0=否,1=是")
    private String isEditable;

    /** 排序 */
    @Excel(name = "排序")
    private Integer sortOrder;

    /** 网站名称（非数据库字段，用于显示） */
    private String siteName;

    /** AI平台配置名称（非数据库字段，用于显示） */
    private String platformName;

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

    public void setAiPlatformId(Long aiPlatformId) 
    {
        this.aiPlatformId = aiPlatformId;
    }

    public Long getAiPlatformId() 
    {
        return aiPlatformId;
    }

    public void setRelationType(String relationType) 
    {
        this.relationType = relationType;
    }

    public String getRelationType() 
    {
        return relationType;
    }

    public void setIsVisible(String isVisible) 
    {
        this.isVisible = isVisible;
    }

    public String getIsVisible() 
    {
        return isVisible;
    }

    public void setIsEditable(String isEditable) 
    {
        this.isEditable = isEditable;
    }

    public String getIsEditable() 
    {
        return isEditable;
    }

    public void setSortOrder(Integer sortOrder) 
    {
        this.sortOrder = sortOrder;
    }

    public Integer getSortOrder() 
    {
        return sortOrder;
    }

    public void setSiteName(String siteName) 
    {
        this.siteName = siteName;
    }

    public String getSiteName() 
    {
        return siteName;
    }

    public void setPlatformName(String platformName) 
    {
        this.platformName = platformName;
    }

    public String getPlatformName() 
    {
        return platformName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("siteId", getSiteId())
            .append("aiPlatformId", getAiPlatformId())
            .append("relationType", getRelationType())
            .append("isVisible", getIsVisible())
            .append("isEditable", getIsEditable())
            .append("sortOrder", getSortOrder())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
