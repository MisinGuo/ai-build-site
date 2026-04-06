package com.ruoyi.gamebox.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 网站-主文章关联对象 gb_site_master_article_relations
 * 
 * @author ruoyi
 * @date 2026-01-13
 */
public class GbSiteMasterArticleRelation extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 网站ID */
    @Excel(name = "网站ID")
    private Long siteId;

    /** 主文章ID */
    @Excel(name = "主文章ID")
    private Long masterArticleId;

    /** 关联类型：include-正向关联 exclude-排除默认配置 */
    @Excel(name = "关联类型", readConverterExp = "include=正向关联,exclude=排除默认配置")
    private String relationType;

    /** 是否可见：0-隐藏 1-可见 */
    @Excel(name = "是否可见", readConverterExp = "0=隐藏,1=可见")
    private String isVisible;

    /** 是否置顶：0-否 1-是 */
    @Excel(name = "是否置顶", readConverterExp = "0=否,1=是")
    private String isTop;

    /** 是否推荐：0-否 1-是 */
    @Excel(name = "是否推荐", readConverterExp = "0=否,1=是")
    private String isRecommend;

    /** 自定义标题 */
    @Excel(name = "自定义标题")
    private String customTitle;

    /** 在该网站的排序 */
    @Excel(name = "排序")
    private Integer sortOrder;

    /** 在该网站的浏览量 */
    @Excel(name = "浏览量")
    private Integer viewCount;

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

    public void setMasterArticleId(Long masterArticleId) 
    {
        this.masterArticleId = masterArticleId;
    }

    public Long getMasterArticleId() 
    {
        return masterArticleId;
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

    public void setIsTop(String isTop) 
    {
        this.isTop = isTop;
    }

    public String getIsTop() 
    {
        return isTop;
    }

    public void setIsRecommend(String isRecommend) 
    {
        this.isRecommend = isRecommend;
    }

    public String getIsRecommend() 
    {
        return isRecommend;
    }

    public void setCustomTitle(String customTitle) 
    {
        this.customTitle = customTitle;
    }

    public String getCustomTitle() 
    {
        return customTitle;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("siteId", getSiteId())
            .append("masterArticleId", getMasterArticleId())
            .append("relationType", getRelationType())
            .append("isVisible", getIsVisible())
            .append("isTop", getIsTop())
            .append("isRecommend", getIsRecommend())
            .append("customTitle", getCustomTitle())
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
