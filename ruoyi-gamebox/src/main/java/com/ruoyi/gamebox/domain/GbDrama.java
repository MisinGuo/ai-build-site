package com.ruoyi.gamebox.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 短剧管理对象 gb_dramas
 * 
 * @author ruoyi
 */
public class GbDrama extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 短剧ID */
    private Long id;

    /** 站点ID */
    @Excel(name = "站点ID")
    private Long siteId;

    /** 供应商ID */
    @Excel(name = "供应商ID")
    private Long vendorId;

    /** 短剧名称 */
    @Excel(name = "短剧名称")
    private String name;

    /** 副标题 */
    @Excel(name = "副标题")
    private String subtitle;

    /** 分类ID */
    @Excel(name = "分类ID")
    private Long categoryId;

    /** 标签JSON数组 */
    @Excel(name = "标签")
    private String tags;

    /** 封面URL */
    @Excel(name = "封面URL")
    private String coverUrl;

    /** 横版封面URL */
    @Excel(name = "横版封面URL")
    private String coverHorizontalUrl;

    /** 预告片URL */
    @Excel(name = "预告片URL")
    private String trailerUrl;

    /** 短剧简介 */
    @Excel(name = "短剧简介")
    private String description;

    /** 详细介绍 */
    private String content;

    /** 总集数 */
    @Excel(name = "总集数")
    private Integer totalEpisodes;

    /** 已更新集数 */
    @Excel(name = "已更新集数")
    private Integer currentEpisodes;

    /** 免费集数 */
    @Excel(name = "免费集数")
    private Integer freeEpisodes;

    /** 更新状态：updating-更新中, completed-已完结 */
    @Excel(name = "更新状态", readConverterExp = "updating=更新中,completed=已完结")
    private String updateStatus;

    /** 单集价格(分) */
    @Excel(name = "单集价格(分)")
    private Integer episodePrice;

    /** 全集价格(分) */
    @Excel(name = "全集价格(分)")
    private Integer fullPrice;

    /** 解锁方式：free-免费, coin-金币解锁, vip-VIP免费 */
    @Excel(name = "解锁方式", readConverterExp = "free=免费,coin=金币解锁,vip=VIP免费")
    private String unlockType;

    /** 观看次数 */
    @Excel(name = "观看次数")
    private Long viewCount;

    /** 点赞数 */
    @Excel(name = "点赞数")
    private Long likeCount;

    /** 收藏数 */
    @Excel(name = "收藏数")
    private Long favoriteCount;

    /** 评分 */
    @Excel(name = "评分")
    private BigDecimal rating;

    /** 导演 */
    @Excel(name = "导演")
    private String director;

    /** 演员列表 */
    @Excel(name = "演员列表")
    private String actors;

    /** 出品方 */
    @Excel(name = "出品方")
    private String producer;

    /** 上映时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "上映时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date releaseDate;

    /** 是否新品 */
    @Excel(name = "是否新品", readConverterExp = "0=否,1=是")
    private String isNew;

    /** 是否热门 */
    @Excel(name = "是否热门", readConverterExp = "0=否,1=是")
    private String isHot;

    /** 是否推荐 */
    @Excel(name = "是否推荐", readConverterExp = "0=否,1=是")
    private String isRecommend;

    /** 排序号 */
    @Excel(name = "排序号")
    private Integer sortOrder;

    /** 状态：0禁用 1启用 */
    @Excel(name = "状态", readConverterExp = "0=禁用,1=启用")
    private String status;

    /** 删除标志：0存在 2删除 */
    private String delFlag;
    /** 分类名称（关联查询字段） */
    private String categoryName;

    /** 分类图标（关联查询字段） */
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
    public void setVendorId(Long vendorId) 
    {
        this.vendorId = vendorId;
    }

    public Long getVendorId() 
    {
        return vendorId;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setSubtitle(String subtitle) 
    {
        this.subtitle = subtitle;
    }

    public String getSubtitle() 
    {
        return subtitle;
    }
    public void setCategoryId(Long categoryId) 
    {
        this.categoryId = categoryId;
    }

    public Long getCategoryId() 
    {
        return categoryId;
    }
    public void setTags(String tags) 
    {
        this.tags = tags;
    }

    public String getTags() 
    {
        return tags;
    }
    public void setCoverUrl(String coverUrl) 
    {
        this.coverUrl = coverUrl;
    }

    public String getCoverUrl() 
    {
        return coverUrl;
    }
    public void setCoverHorizontalUrl(String coverHorizontalUrl) 
    {
        this.coverHorizontalUrl = coverHorizontalUrl;
    }

    public String getCoverHorizontalUrl() 
    {
        return coverHorizontalUrl;
    }
    public void setTrailerUrl(String trailerUrl) 
    {
        this.trailerUrl = trailerUrl;
    }

    public String getTrailerUrl() 
    {
        return trailerUrl;
    }
    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }
    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }
    public void setTotalEpisodes(Integer totalEpisodes) 
    {
        this.totalEpisodes = totalEpisodes;
    }

    public Integer getTotalEpisodes() 
    {
        return totalEpisodes;
    }
    public void setCurrentEpisodes(Integer currentEpisodes) 
    {
        this.currentEpisodes = currentEpisodes;
    }

    public Integer getCurrentEpisodes() 
    {
        return currentEpisodes;
    }
    public void setFreeEpisodes(Integer freeEpisodes) 
    {
        this.freeEpisodes = freeEpisodes;
    }

    public Integer getFreeEpisodes() 
    {
        return freeEpisodes;
    }
    public void setUpdateStatus(String updateStatus) 
    {
        this.updateStatus = updateStatus;
    }

    public String getUpdateStatus() 
    {
        return updateStatus;
    }
    public void setEpisodePrice(Integer episodePrice) 
    {
        this.episodePrice = episodePrice;
    }

    public Integer getEpisodePrice() 
    {
        return episodePrice;
    }
    public void setFullPrice(Integer fullPrice) 
    {
        this.fullPrice = fullPrice;
    }

    public Integer getFullPrice() 
    {
        return fullPrice;
    }
    public void setUnlockType(String unlockType) 
    {
        this.unlockType = unlockType;
    }

    public String getUnlockType() 
    {
        return unlockType;
    }
    public void setViewCount(Long viewCount) 
    {
        this.viewCount = viewCount;
    }

    public Long getViewCount() 
    {
        return viewCount;
    }
    public void setLikeCount(Long likeCount) 
    {
        this.likeCount = likeCount;
    }

    public Long getLikeCount() 
    {
        return likeCount;
    }
    public void setFavoriteCount(Long favoriteCount) 
    {
        this.favoriteCount = favoriteCount;
    }

    public Long getFavoriteCount() 
    {
        return favoriteCount;
    }
    public void setRating(BigDecimal rating) 
    {
        this.rating = rating;
    }

    public BigDecimal getRating() 
    {
        return rating;
    }
    public void setDirector(String director) 
    {
        this.director = director;
    }

    public String getDirector() 
    {
        return director;
    }
    public void setActors(String actors) 
    {
        this.actors = actors;
    }

    public String getActors() 
    {
        return actors;
    }
    public void setProducer(String producer) 
    {
        this.producer = producer;
    }

    public String getProducer() 
    {
        return producer;
    }
    public void setReleaseDate(Date releaseDate) 
    {
        this.releaseDate = releaseDate;
    }

    public Date getReleaseDate() 
    {
        return releaseDate;
    }
    public void setIsNew(String isNew) 
    {
        this.isNew = isNew;
    }

    public String getIsNew() 
    {
        return isNew;
    }
    public void setIsHot(String isHot) 
    {
        this.isHot = isHot;
    }

    public String getIsHot() 
    {
        return isHot;
    }
    public void setIsRecommend(String isRecommend) 
    {
        this.isRecommend = isRecommend;
    }

    public String getIsRecommend() 
    {
        return isRecommend;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("siteId", getSiteId())
            .append("vendorId", getVendorId())
            .append("name", getName())
            .append("subtitle", getSubtitle())
            .append("categoryId", getCategoryId())
            .append("tags", getTags())
            .append("coverUrl", getCoverUrl())
            .append("coverHorizontalUrl", getCoverHorizontalUrl())
            .append("trailerUrl", getTrailerUrl())
            .append("description", getDescription())
            .append("content", getContent())
            .append("totalEpisodes", getTotalEpisodes())
            .append("currentEpisodes", getCurrentEpisodes())
            .append("freeEpisodes", getFreeEpisodes())
            .append("updateStatus", getUpdateStatus())
            .append("episodePrice", getEpisodePrice())
            .append("fullPrice", getFullPrice())
            .append("unlockType", getUnlockType())
            .append("viewCount", getViewCount())
            .append("likeCount", getLikeCount())
            .append("favoriteCount", getFavoriteCount())
            .append("rating", getRating())
            .append("director", getDirector())
            .append("actors", getActors())
            .append("producer", getProducer())
            .append("releaseDate", getReleaseDate())
            .append("isNew", getIsNew())
            .append("isHot", getIsHot())
            .append("isRecommend", getIsRecommend())
            .append("sortOrder", getSortOrder())
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
