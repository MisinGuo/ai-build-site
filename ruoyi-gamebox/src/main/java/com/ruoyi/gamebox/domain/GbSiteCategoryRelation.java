package com.ruoyi.gamebox.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 网站-分类关联对象 gb_site_category_relations
 * 
 * @author ruoyi
 * @date 2025-12-27
 */
public class GbSiteCategoryRelation extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 网站ID */
    @Excel(name = "网站ID")
    private Long siteId;

    /** 分类ID */
    @Excel(name = "分类ID")
    private Long categoryId;
    
    /** 关联类型：include-正向关联 exclude-排除默认配置 */
    @Excel(name = "关联类型", readConverterExp = "include=正向关联,exclude=排除")
    private String relationType;

    /** 是否可见：0-隐藏 1-可见 */
    @Excel(name = "是否可见", readConverterExp = "0=隐藏,1=可见")
    private String isVisible;

    /** 是否可编辑：0-只读 1-可编辑 */
    @Excel(name = "是否可编辑", readConverterExp = "0=只读,1=可编辑")
    private String isEditable;

    /** 在该网站的排序 */
    @Excel(name = "在该网站的排序")
    private Integer sortOrder;

    // 扩展字段（用于关联查询）
    /** 网站名称 */
    private String siteName;

    /** 网站编码 */
    private String siteCode;

    /** 网站域名 */
    private String siteDomain;

    /** 分类名称 */
    private String categoryName;

    /** 分类标识 */
    private String categorySlug;

    /** 分类类型 */
    private String categoryType;

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

    public void setCategoryId(Long categoryId) 
    {
        this.categoryId = categoryId;
    }

    public Long getCategoryId() 
    {
        return categoryId;
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

    public String getSiteName() 
    {
        return siteName;
    }

    public void setSiteName(String siteName) 
    {
        this.siteName = siteName;
    }

    public String getCategoryName() 
    {
        return categoryName;
    }

    public void setCategoryName(String categoryName) 
    {
        this.categoryName = categoryName;
    }

    public String getSiteCode() 
    {
        return siteCode;
    }

    public void setSiteCode(String siteCode) 
    {
        this.siteCode = siteCode;
    }

    public String getSiteDomain() 
    {
        return siteDomain;
    }

    public void setSiteDomain(String siteDomain) 
    {
        this.siteDomain = siteDomain;
    }

    public String getCategorySlug() 
    {
        return categorySlug;
    }

    public void setCategorySlug(String categorySlug) 
    {
        this.categorySlug = categorySlug;
    }

    public String getCategoryType() 
    {
        return categoryType;
    }

    public void setCategoryType(String categoryType) 
    {
        this.categoryType = categoryType;
    }

    public String getCategoryIcon() 
    {
        return categoryIcon;
    }

    public void setCategoryIcon(String categoryIcon) 
    {
        this.categoryIcon = categoryIcon;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("siteId", getSiteId())
            .append("categoryId", getCategoryId())
            .append("isVisible", getIsVisible())
            .append("isEditable", getIsEditable())
            .append("sortOrder", getSortOrder())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
