package com.ruoyi.gamebox.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 分类管理对象 gb_categories
 * 
 * @author ruoyi
 */
public class GbCategory extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 分类ID */
    private Long id;

    /** 站点ID */
    @Excel(name = "站点ID")
    private Long siteId;

    /** 父分类ID */
    @Excel(name = "父分类ID")
    private Long parentId;

    /** 分类名称 */
    @Excel(name = "分类名称")
    private String name;

    /** 分类类型：game-游戏分类, drama-短剧分类, article-文章分类, website-网站分类, gamebox-游戏盒子分类, document-文档分类, other-其他 */
    @Excel(name = "分类类型", readConverterExp = "game=游戏分类,drama=短剧分类,article=文章分类,website=网站分类,gamebox=游戏盒子分类,document=文档分类,other=其他")
    private String categoryType;

    /** 是否为板块：0-否 1-是（顶级分类用作板块） */
    @Excel(name = "是否为板块", readConverterExp = "0=否,1=是")
    private String isSection;

    /** 分类标识（URL友好） */
    @Excel(name = "分类标识")
    private String slug;

    /** 分类图标（emoji或icon类名） */
    @Excel(name = "分类图标")
    private String icon;

    /** 分类描述 */
    @Excel(name = "分类描述")
    private String description;

    /** 排序号 */
    @Excel(name = "排序号")
    private Integer sortOrder;

    /** 状态：0禁用 1启用 */
    @Excel(name = "状态", readConverterExp = "0=禁用,1=启用")
    private String status;

    /** 删除标志：0存在 2删除 */
    private String delFlag;

    /** 关联网站数量（不映射到数据库，用于前端显示） */
    private Integer relatedSitesCount;
    
    /** 排除网站数量（仅对默认配置有效，不映射到数据库，用于前端显示） */
    private Integer excludedSitesCount;
    
    /** 关联数据个数（游戏、盒子、文章、短剧等，不映射到数据库，用于前端显示） */
    private Integer relatedDataCount;
    
    /** 查询模式：creator-按创建者查询, related-按关联网站查询（不映射到数据库） */
    private String queryMode;
    
    /** 是否包含默认配置（不映射到数据库，用于创建者模式查询） */
    private Boolean includeDefault;
    
    /** 在关联网站下的可见性：0隐藏 1可见（不映射到数据库，仅在关联模式查询时返回） */
    private String isVisible;
    
    /** 是否被当前网站排除：0未排除 1已排除（不映射到数据库，仅在关联模式查询时返回） */
    private Integer isExcluded;
    
    /** 关联来源：own-网站自己的, default-默认配置, shared-其他网站分享（不映射到数据库，仅在关联模式查询时返回） */
    private String relationSource;

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
    public void setParentId(Long parentId) 
    {
        this.parentId = parentId;
    }

    public Long getParentId() 
    {
        return parentId;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setCategoryType(String categoryType) 
    {
        this.categoryType = categoryType;
    }

    public String getCategoryType() 
    {
        return categoryType;
    }
    public void setIsSection(String isSection) 
    {
        this.isSection = isSection;
    }

    public String getIsSection() 
    {
        return isSection;
    }
    
    /**
     * 判断是否为板块（顶级分类）
     */
    public boolean isSectionCategory() 
    {
        return "1".equals(isSection) || (getParentId() != null && getParentId() == 0L);
    }
    public void setSlug(String slug) 
    {
        this.slug = slug;
    }

    public String getSlug() 
    {
        return slug;
    }
    public void setIcon(String icon) 
    {
        this.icon = icon;
    }

    public String getIcon() 
    {
        return icon;
    }
    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }
    public void setSortOrder(Integer sortOrder) 
    {
        this.sortOrder = sortOrder;
    }

    public Integer getSortOrder() 
    {
        return sortOrder;
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
    public void setRelatedSitesCount(Integer relatedSitesCount) 
    {
        this.relatedSitesCount = relatedSitesCount;
    }

    public Integer getRelatedSitesCount() 
    {
        return relatedSitesCount;
    }

    public void setExcludedSitesCount(Integer excludedSitesCount) 
    {
        this.excludedSitesCount = excludedSitesCount;
    }

    public Integer getExcludedSitesCount() 
    {
        return excludedSitesCount;
    }

    public void setRelatedDataCount(Integer relatedDataCount) 
    {
        this.relatedDataCount = relatedDataCount;
    }

    public Integer getRelatedDataCount() 
    {
        return relatedDataCount;
    }

    public void setQueryMode(String queryMode) 
    {
        this.queryMode = queryMode;
    }

    public String getQueryMode() 
    {
        return queryMode;
    }

    public void setIncludeDefault(Boolean includeDefault) 
    {
        this.includeDefault = includeDefault;
    }

    public Boolean getIncludeDefault() 
    {
        return includeDefault;
    }

    public void setIsVisible(String isVisible) 
    {
        this.isVisible = isVisible;
    }

    public String getIsVisible() 
    {
        return isVisible;
    }

    public void setIsExcluded(Integer isExcluded) 
    {
        this.isExcluded = isExcluded;
    }

    public Integer getIsExcluded() 
    {
        return isExcluded;
    }
    
    public void setRelationSource(String relationSource) 
    {
        this.relationSource = relationSource;
    }

    public String getRelationSource() 
    {
        return relationSource;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("siteId", getSiteId())
            .append("parentId", getParentId())
            .append("name", getName())
            .append("categoryType", getCategoryType())
            .append("slug", getSlug())
            .append("icon", getIcon())
            .append("description", getDescription())
            .append("sortOrder", getSortOrder())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
