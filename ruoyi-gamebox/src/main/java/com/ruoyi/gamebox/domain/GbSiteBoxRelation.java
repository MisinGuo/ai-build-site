package com.ruoyi.gamebox.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 网站-游戏盒子关联对象 gb_site_box_relations
 * 
 * @author ruoyi
 * @date 2025-12-27
 */
public class GbSiteBoxRelation extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 网站ID */
    @Excel(name = "网站ID")
    private Long siteId;

    /** 游戏盒子ID */
    @Excel(name = "游戏盒子ID")
    private Long boxId;

    /** 是否可见：0-隐藏 1-可见 */
    @Excel(name = "是否可见", readConverterExp = "0=隐藏,1=可见")
    private String isVisible;

    /** 是否推荐：0-否 1-是 */
    @Excel(name = "是否推荐", readConverterExp = "0=否,1=是")
    private String isFeatured;

    /** 自定义名称（可覆盖原名称） */
    @Excel(name = "自定义名称")
    private String customName;

    /** 自定义描述 */
    @Excel(name = "自定义描述")
    private String customDescription;

    /** 在该网站的排序 */
    @Excel(name = "在该网站的排序")
    private Integer sortOrder;

    /** 在该网站的浏览量 */
    @Excel(name = "在该网站的浏览量")
    private Integer viewCount;

    /** 关联类型：include-正向关联, exclude-排除 */
    @Excel(name = "关联类型", readConverterExp = "include=正向关联,exclude=排除")
    private String relationType;

    /** 是否可编辑：0-不可编辑(系统), 1-可编辑(用户) */
    @Excel(name = "是否可编辑", readConverterExp = "0=不可编辑(系统),1=可编辑(用户)")
    private String isEditable;

    // 扩展字段（用于关联查询）
    /** 网站名称 */
    private String siteName;

    /** 盒子名称 */
    private String boxName;

    /** 盒子标识 */
    private String boxSlug;

    /** 盒子封面 */
    private String boxCoverImage;

    /** 盒子类型 */
    private String boxType;

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

    public void setBoxId(Long boxId) 
    {
        this.boxId = boxId;
    }

    public Long getBoxId() 
    {
        return boxId;
    }

    public void setIsVisible(String isVisible) 
    {
        this.isVisible = isVisible;
    }

    public String getIsVisible() 
    {
        return isVisible;
    }

    public void setIsFeatured(String isFeatured) 
    {
        this.isFeatured = isFeatured;
    }

    public String getIsFeatured() 
    {
        return isFeatured;
    }

    public void setCustomName(String customName) 
    {
        this.customName = customName;
    }

    public String getCustomName() 
    {
        return customName;
    }

    public void setCustomDescription(String customDescription) 
    {
        this.customDescription = customDescription;
    }

    public String getCustomDescription() 
    {
        return customDescription;
    }

    public void setSortOrder(Integer sortOrder) 
    {
        this.sortOrder = sortOrder;
    }

    public Integer getSortOrder() 
    {
        return sortOrder;
    }

    public void setViewCount(Integer viewCount) 
    {
        this.viewCount = viewCount;
    }

    public Integer getViewCount() 
    {
        return viewCount;
    }

    public String getSiteName() 
    {
        return siteName;
    }

    public void setSiteName(String siteName) 
    {
        this.siteName = siteName;
    }

    public String getBoxName() 
    {
        return boxName;
    }

    public void setBoxName(String boxName) 
    {
        this.boxName = boxName;
    }

    public String getBoxSlug() 
    {
        return boxSlug;
    }

    public void setBoxSlug(String boxSlug) 
    {
        this.boxSlug = boxSlug;
    }

    public String getBoxCoverImage() 
    {
        return boxCoverImage;
    }

    public void setBoxCoverImage(String boxCoverImage) 
    {
        this.boxCoverImage = boxCoverImage;
    }

    public String getBoxType() 
    {
        return boxType;
    }

    public void setBoxType(String boxType) 
    {
        this.boxType = boxType;
    }

    public void setRelationType(String relationType) 
    {
        this.relationType = relationType;
    }

    public String getRelationType() 
    {
        return relationType;
    }

    public void setIsEditable(String isEditable) 
    {
        this.isEditable = isEditable;
    }

    public String getIsEditable() 
    {
        return isEditable;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("siteId", getSiteId())
            .append("boxId", getBoxId())
            .append("isVisible", getIsVisible())
            .append("isFeatured", getIsFeatured())
            .append("customName", getCustomName())
            .append("customDescription", getCustomDescription())
            .append("sortOrder", getSortOrder())
            .append("viewCount", getViewCount())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
