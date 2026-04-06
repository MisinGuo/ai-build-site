package com.ruoyi.gamebox.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 网站-游戏关联对象 gb_site_game_relations
 * 
 * @author ruoyi
 * @date 2025-12-27
 */
public class GbSiteGameRelation extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 网站ID */
    @Excel(name = "网站ID")
    private Long siteId;

    /** 游戏ID */
    @Excel(name = "游戏ID")
    private Long gameId;

    /** 是否可见：0-隐藏 1-可见 */
    @Excel(name = "是否可见", readConverterExp = "0=隐藏,1=可见")
    private String isVisible;

    /** 是否推荐：0-否 1-是 */
    @Excel(name = "是否推荐", readConverterExp = "0=否,1=是")
    private String isFeatured;

    /** 是否新游：0-否 1-是 */
    @Excel(name = "是否新游", readConverterExp = "0=否,1=是")
    private String isNew;

    /** 自定义名称 */
    @Excel(name = "自定义名称")
    private String customName;

    /** 自定义描述 */
    @Excel(name = "自定义描述")
    private String customDescription;

    /** 自定义下载链接 */
    @Excel(name = "自定义下载链接")
    private String customDownloadUrl;

    /** 在该网站的排序 */
    @Excel(name = "在该网站的排序")
    private Integer sortOrder;

    /** 在该网站的浏览量 */
    @Excel(name = "在该网站的浏览量")
    private Integer viewCount;

    /** 在该网站的下载量 */
    @Excel(name = "在该网站的下载量")
    private Integer downloadCount;

    /** 关联类型：include-正向关联, exclude-排除 */
    @Excel(name = "关联类型", readConverterExp = "include=正向关联,exclude=排除")
    private String relationType;

    /** 是否可编辑：0-不可编辑(系统), 1-可编辑(用户) */
    @Excel(name = "是否可编辑", readConverterExp = "0=不可编辑(系统),1=可编辑(用户)")
    private String isEditable;

    // 扩展字段（用于关联查询）
    /** 网站名称 */
    private String siteName;

    /** 游戏名称 */
    private String gameName;

    /** 游戏标识 */
    private String gameSlug;

    /** 游戏封面 */
    private String gameCoverImage;

    /** 游戏图标 */
    private String gameIcon;

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

    public void setGameId(Long gameId) 
    {
        this.gameId = gameId;
    }

    public Long getGameId() 
    {
        return gameId;
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

    public void setIsNew(String isNew) 
    {
        this.isNew = isNew;
    }

    public String getIsNew() 
    {
        return isNew;
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

    public void setCustomDownloadUrl(String customDownloadUrl) 
    {
        this.customDownloadUrl = customDownloadUrl;
    }

    public String getCustomDownloadUrl() 
    {
        return customDownloadUrl;
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

    public void setDownloadCount(Integer downloadCount) 
    {
        this.downloadCount = downloadCount;
    }

    public Integer getDownloadCount() 
    {
        return downloadCount;
    }

    public String getSiteName() 
    {
        return siteName;
    }

    public void setSiteName(String siteName) 
    {
        this.siteName = siteName;
    }

    public String getGameName() 
    {
        return gameName;
    }

    public void setGameName(String gameName) 
    {
        this.gameName = gameName;
    }

    public String getGameSlug() 
    {
        return gameSlug;
    }

    public void setGameSlug(String gameSlug) 
    {
        this.gameSlug = gameSlug;
    }

    public String getGameCoverImage() 
    {
        return gameCoverImage;
    }

    public void setGameCoverImage(String gameCoverImage) 
    {
        this.gameCoverImage = gameCoverImage;
    }

    public String getGameIcon() 
    {
        return gameIcon;
    }

    public void setGameIcon(String gameIcon) 
    {
        this.gameIcon = gameIcon;
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
            .append("gameId", getGameId())
            .append("isVisible", getIsVisible())
            .append("isFeatured", getIsFeatured())
            .append("isNew", getIsNew())
            .append("customName", getCustomName())
            .append("customDescription", getCustomDescription())
            .append("customDownloadUrl", getCustomDownloadUrl())
            .append("sortOrder", getSortOrder())
            .append("viewCount", getViewCount())
            .append("downloadCount", getDownloadCount())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
