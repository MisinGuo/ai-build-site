package com.ruoyi.gamebox.search.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 游戏搜索文档
 * 
 * @author ruoyi
 */
@Document(indexName = "gb_games")
@Setting(shards = 1, replicas = 0)
public class GameDocument
{
    /** 游戏ID */
    @Id
    private Long id;

    /** 网站ID */
    @Field(type = FieldType.Long)
    private Long siteId;

    /** 游戏名称 - 支持中文分词 */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String name;

    /** 游戏副标题 */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String subtitle;

    /** 游戏简称 */
    @Field(type = FieldType.Keyword)
    private String shortName;

    /** 游戏分类 */
    @Field(type = FieldType.Keyword)
    private String category;

    /** 游戏类型 */
    @Field(type = FieldType.Keyword)
    private String gameType;

    /** 游戏图标URL */
    @Field(type = FieldType.Keyword)
    private String iconUrl;

    /** 游戏封面URL */
    @Field(type = FieldType.Keyword)
    private String coverUrl;

    /** 游戏描述 */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String description;

    /** 推广说明 */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String promotionDesc;

    /** 开发商 */
    @Field(type = FieldType.Keyword)
    private String developer;

    /** 发行商 */
    @Field(type = FieldType.Keyword)
    private String publisher;

    /** 设备支持 */
    @Field(type = FieldType.Keyword)
    private String deviceSupport;

    /** 折扣标签 */
    @Field(type = FieldType.Keyword)
    private String discountTag;

    /** 原价 */
    @Field(type = FieldType.Double)
    private BigDecimal originalPrice;

    /** 当前价格 */
    @Field(type = FieldType.Double)
    private BigDecimal currentPrice;

    /** 评分 */
    @Field(type = FieldType.Float)
    private BigDecimal rating;

    /** 下载量 */
    @Field(type = FieldType.Long)
    private Long downloads;

    /** 热度值 */
    @Field(type = FieldType.Long)
    private Long hotValue;

    /** 排序 */
    @Field(type = FieldType.Integer)
    private Integer sortOrder;

    /** 状态 */
    @Field(type = FieldType.Keyword)
    private String status;

    /** 删除标志（0代表存在 2代表删除） */
    @Field(type = FieldType.Keyword)
    private String delFlag;

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

    public String getSubtitle()
    {
        return subtitle;
    }

    public void setSubtitle(String subtitle)
    {
        this.subtitle = subtitle;
    }

    public String getShortName()
    {
        return shortName;
    }

    public void setShortName(String shortName)
    {
        this.shortName = shortName;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public String getGameType()
    {
        return gameType;
    }

    public void setGameType(String gameType)
    {
        this.gameType = gameType;
    }

    public String getIconUrl()
    {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl)
    {
        this.iconUrl = iconUrl;
    }

    public String getCoverUrl()
    {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl)
    {
        this.coverUrl = coverUrl;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getPromotionDesc()
    {
        return promotionDesc;
    }

    public void setPromotionDesc(String promotionDesc)
    {
        this.promotionDesc = promotionDesc;
    }

    public String getDeveloper()
    {
        return developer;
    }

    public void setDeveloper(String developer)
    {
        this.developer = developer;
    }

    public String getPublisher()
    {
        return publisher;
    }

    public void setPublisher(String publisher)
    {
        this.publisher = publisher;
    }

    public String getDeviceSupport()
    {
        return deviceSupport;
    }

    public void setDeviceSupport(String deviceSupport)
    {
        this.deviceSupport = deviceSupport;
    }

    public String getDiscountTag()
    {
        return discountTag;
    }

    public void setDiscountTag(String discountTag)
    {
        this.discountTag = discountTag;
    }

    public BigDecimal getOriginalPrice()
    {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice)
    {
        this.originalPrice = originalPrice;
    }

    public BigDecimal getCurrentPrice()
    {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice)
    {
        this.currentPrice = currentPrice;
    }

    public BigDecimal getRating()
    {
        return rating;
    }

    public void setRating(BigDecimal rating)
    {
        this.rating = rating;
    }

    public Long getDownloads()
    {
        return downloads;
    }

    public void setDownloads(Long downloads)
    {
        this.downloads = downloads;
    }

    public Long getHotValue()
    {
        return hotValue;
    }

    public void setHotValue(Long hotValue)
    {
        this.hotValue = hotValue;
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

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    /** 所属游戏盒子列表（冗余存储，搜索时直接返回） */
    @Field(type = FieldType.Nested)
    private List<GameBoxInfo> boxes;

    public List<GameBoxInfo> getBoxes()
    {
        return boxes;
    }

    public void setBoxes(List<GameBoxInfo> boxes)
    {
        this.boxes = boxes;
    }

    /**
     * 游戏所属盒子简要信息（嵌套对象）
     */
    public static class GameBoxInfo
    {
        @Field(type = FieldType.Long)
        private Long boxId;

        @Field(type = FieldType.Keyword)
        private String boxName;

        @Field(type = FieldType.Keyword)
        private String logoUrl;

        public GameBoxInfo() {}

        public GameBoxInfo(Long boxId, String boxName, String logoUrl)
        {
            this.boxId = boxId;
            this.boxName = boxName;
            this.logoUrl = logoUrl;
        }

        public Long getBoxId() { return boxId; }
        public void setBoxId(Long boxId) { this.boxId = boxId; }

        public String getBoxName() { return boxName; }
        public void setBoxName(String boxName) { this.boxName = boxName; }

        public String getLogoUrl() { return logoUrl; }
        public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
    }
}
