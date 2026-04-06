package com.ruoyi.gamebox.search.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 游戏盒子搜索文档
 * 
 * @author ruoyi
 */
@Document(indexName = "gb_game_boxes")
@Setting(shards = 1, replicas = 0)
public class GameBoxDocument
{
    /** 游戏盒ID */
    @Id
    private Long id;

    /** 网站ID */
    @Field(type = FieldType.Long)
    private Long siteId;

    /** 盒子名称 - 支持中文分词 */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String name;

    /** Logo URL */
    @Field(type = FieldType.Keyword)
    private String logoUrl;

    /** Banner URL */
    @Field(type = FieldType.Keyword)
    private String bannerUrl;

    /** 盒子描述 */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String description;

    /** 官网地址 */
    @Field(type = FieldType.Keyword)
    private String officialUrl;

    /** 折扣率 */
    @Field(type = FieldType.Double)
    private BigDecimal discountRate;

    /** 特色功能 */
    @Field(type = FieldType.Keyword)
    private String[] features;

    /** 收录游戏数 */
    @Field(type = FieldType.Integer)
    private Integer gameCount;

    /** 排序 */
    @Field(type = FieldType.Integer)
    private Integer sortOrder;

    /** 状态 */
    @Field(type = FieldType.Keyword)
    private String status;

    /** 删除标志（0代表存在 2代表删除） */
    @Field(type = FieldType.Keyword)
    private String delFlag;

    /** 分类ID */
    @Field(type = FieldType.Long)
    private Long categoryId;

    /** 分类名称 */
    @Field(type = FieldType.Keyword)
    private String categoryName;

    /** 创建时间 */
    @Field(type = FieldType.Date)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getSiteId()
    {
        return siteId;
    }

    public void setSiteId(Long siteId)
    {
        this.siteId = siteId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getLogoUrl()
    {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl)
    {
        this.logoUrl = logoUrl;
    }

    public String getBannerUrl()
    {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl)
    {
        this.bannerUrl = bannerUrl;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getOfficialUrl()
    {
        return officialUrl;
    }

    public void setOfficialUrl(String officialUrl)
    {
        this.officialUrl = officialUrl;
    }

    public BigDecimal getDiscountRate()
    {
        return discountRate;
    }

    public void setDiscountRate(BigDecimal discountRate)
    {
        this.discountRate = discountRate;
    }

    public String[] getFeatures()
    {
        return features;
    }

    public void setFeatures(String[] features)
    {
        this.features = features;
    }

    public Integer getGameCount()
    {
        return gameCount;
    }

    public void setGameCount(Integer gameCount)
    {
        this.gameCount = gameCount;
    }

    public Integer getSortOrder()
    {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder)
    {
        this.sortOrder = sortOrder;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public Long getCategoryId()
    {
        return categoryId;
    }

    public void setCategoryId(Long categoryId)
    {
        this.categoryId = categoryId;
    }

    public String getCategoryName()
    {
        return categoryName;
    }

    public void setCategoryName(String categoryName)
    {
        this.categoryName = categoryName;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }
}
