package com.ruoyi.gamebox.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 游戏对象 gb_games
 * 
 * @author ruoyi
 */
public class GbGame extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 所属网站ID */
    @Excel(name = "所属网站ID")
    private Long siteId;

    /** 游戏名称 */
    @Excel(name = "游戏名称")
    @NotBlank(message = "游戏名称不能为空")
    @Size(min = 0, max = 100, message = "游戏名称长度不能超过100个字符")
    private String name;

    /** 游戏副标题/版本说明 */
    @Excel(name = "游戏副标题")
    private String subtitle;

    /** 游戏简称 */
    private String shortName;

    /** 游戏类型：official-官方 discount-折扣 bt-BT版 coming-即将上线 */
    @Excel(name = "游戏类型", readConverterExp = "official=官方,discount=折扣,bt=BT版,coming=即将上线")
    private String gameType;

    /** 游戏图标URL */
    private String iconUrl;

    /** 游戏封面URL */
    private String coverUrl;

    /** 游戏横幅URL */
    private String bannerUrl;

    /** 游戏截图（JSON数组） */
    private String screenshots;

    /** 游戏视频URL */
    private String videoUrl;

    /** 游戏描述 */
    private String description;

    /** 推广说明 */
    private String promotionDesc;

    /** 开发商 */
    @Excel(name = "开发商")
    private String developer;

    /** 发行商 */
    private String publisher;

    /** 包名 */
    private String packageName;

    /** 版本号 */
    private String version;

    /** 安装包大小 */
    private String size;

    /** 设备支持：android-安卓 ios-iOS both-双端 */
    @Excel(name = "设备支持", readConverterExp = "android=安卓,ios=iOS,both=双端")
    private String deviceSupport;

    /** 下载次数 */
    @Excel(name = "下载次数")
    private Integer downloadCount;

    /** 评分（0-5） */
    @Excel(name = "评分")
    private BigDecimal rating;

    /** 游戏特性（JSON数组） */
    private String features;

    /** 标签，逗号分隔 */
    @Excel(name = "标签")
    private String tags;

    /** 上架/开服时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "上架时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date launchTime;

    /** 是否新游：0-否 1-是 */
    @Excel(name = "是否新游", readConverterExp = "0=否,1=是")
    private String isNew;

    /** 是否热门：0-否 1-是 */
    @Excel(name = "是否热门", readConverterExp = "0=否,1=是")
    private String isHot;

    /** 是否推荐：0-否 1-是 */
    @Excel(name = "是否推荐", readConverterExp = "0=否,1=是")
    private String isRecommend;

    /** 排序 */
    private Integer sortOrder;

    /** 状态：0-下架 1-上架 */
    @Excel(name = "状态", readConverterExp = "0=下架,1=上架")
    private String status;

    /** 删除标志 */
    private String delFlag;

    /** 首次/最近覆盖从哪个盒子导入（主要用于区分渠道特殊数据） */
    @Excel(name = "数据来源盒子ID")
    private Long sourceBoxId;

    /** 游戏下载链接（主下载地址） */
    @Excel(name = "游戏下载链接")
    private String downloadUrl;

    /** 安卓下载链接 */
    private String androidUrl;

    /** iOS下载链接 */
    private String iosUrl;

    /** APK下载链接 */
    private String apkUrl;

    /** 折扣标签 */
    @Excel(name = "折扣标签")
    private String discountLabel;

    /** 首充折扣-国内 */
    @Excel(name = "首充折扣-国内")
    private BigDecimal firstChargeDomestic;

    /** 首充折扣-海外 */
    @Excel(name = "首充折扣-海外")
    private BigDecimal firstChargeOverseas;

    /** 续充折扣-国内 */
    @Excel(name = "续充折扣-国内")
    private BigDecimal rechargeDomestic;

    /** 续充折扣-海外 */
    @Excel(name = "续充折扣-海外")
    private BigDecimal rechargeOverseas;

    /** 是否有扶持：0-无 1-有 */
    @Excel(name = "是否有扶持", readConverterExp = "0=无,1=有")
    private String hasSupport;

    /** 扶持说明 */
    private String supportDesc;

    /** 是否有低扣券：0-无 1-有 */
    @Excel(name = "是否有低扣券", readConverterExp = "0=无,1=有")
    private String hasLowDeductCoupon;

    /** 低扣券链接 */
    private String lowDeductCouponUrl;

    /** 所属盒子数量（用于列表展示，非数据库字段） */
    private Integer boxCount;

    /** 所属盒子ID列表（逗号分隔，仅用于列表展示，非数据库字段） */
    private String boxIds;

    /** 分类名称（关联查询字段） */
    private String categoryName;

    /** 分类图标（关联查询字段） */
    private String categoryIcon;
    
    /** 分类ID（仅用于查询参数，不映射到数据库字段） */
    private Long categoryId;
    
    /** 游戏的多分类列表（非数据库字段，用于列表展示） */
    private List<GbGameCategoryRelation> categories;

    /** 游戏关联的盒子列表（非数据库字段，用于搜索结果展示） */
    @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY)
    private List<Map<String, Object>> boxes;

    /** 查询模式：creator-创建者, related-关联网站（仅用于查询，不存储） */
    private String queryMode;

    /** 是否包含默认配置（仅用于查询，不存储） */
    private Boolean includeDefault;

    /** 关联网站数量（仅用于查询，不存储） */
    private Integer relatedSitesCount;
    
    /** 排除网站数量（仅用于查询，不存储） */
    private Integer excludedSitesCount;

    /** 关联来源标识（仅用于查询，不存储）：own-自有，default-默认配置，shared-跨站共享 */
    private String relationSource;

    /** 统一的可见性状态（仅用于查询，不存储）：0-不可见，1-可见 */
    private String isVisible;

    /** 是否被排除（仅用于查询，不存储）：true-已排除，false-未排除（仅对默认配置有效） */
    private Boolean isExcluded;

    /** 仅查询无分类游戏（仅用于查询，不存储）：true-只返回无任何分类关联的游戏 */
    private Boolean uncategorizedOnly;

    /** 按盒子搜索（仅用于查询，不存储）：指定盒子ID时，仅返回该盒子关联的游戏 */
    private Long boxId;

    /** 按多个盒子搜索（仅用于查询，不存储）：逗号分隔的盒子ID列表 */
    private String boxIdsFilter;

    /** 按多个分类搜索（仅用于查询，不存储）：逗号分隔的分类ID列表 */
    private String categoryIdsFilter;

    /** 更新时间范围开始（时间戳毫秒，仅用于查询，不存储） */
    private Long updateTimeStart;

    /** 更新时间范围结束（时间戳毫秒，仅用于查询，不存储） */
    private Long updateTimeEnd;

    public Long getUpdateTimeStart() { return updateTimeStart; }
    public void setUpdateTimeStart(Long updateTimeStart) { this.updateTimeStart = updateTimeStart; }

    public Long getUpdateTimeEnd() { return updateTimeEnd; }
    public void setUpdateTimeEnd(Long updateTimeEnd) { this.updateTimeEnd = updateTimeEnd; }
    public void setUncategorizedOnly(Boolean uncategorizedOnly) { this.uncategorizedOnly = uncategorizedOnly; }

    public Long getBoxId() { return boxId; }
    public void setBoxId(Long boxId) { this.boxId = boxId; }

    public String getBoxIdsFilter() { return boxIdsFilter; }
    public void setBoxIdsFilter(String boxIdsFilter) { this.boxIdsFilter = boxIdsFilter; }

    public String getCategoryIdsFilter() { return categoryIdsFilter; }
    public void setCategoryIdsFilter(String categoryIdsFilter) { this.categoryIdsFilter = categoryIdsFilter; }

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

    public void setShortName(String shortName)
    {
        this.shortName = shortName;
    }

    public String getShortName()
    {
        return shortName;
    }

    public void setGameType(String gameType)
    {
        this.gameType = gameType;
    }

    public String getGameType()
    {
        return gameType;
    }

    public void setIconUrl(String iconUrl)
    {
        this.iconUrl = iconUrl;
    }

    public String getIconUrl()
    {
        return iconUrl;
    }

    public void setCoverUrl(String coverUrl)
    {
        this.coverUrl = coverUrl;
    }

    public String getCoverUrl()
    {
        return coverUrl;
    }

    public void setBannerUrl(String bannerUrl)
    {
        this.bannerUrl = bannerUrl;
    }

    public String getBannerUrl()
    {
        return bannerUrl;
    }

    public void setScreenshots(String screenshots)
    {
        this.screenshots = screenshots;
    }

    public String getScreenshots()
    {
        return screenshots;
    }

    public void setVideoUrl(String videoUrl)
    {
        this.videoUrl = videoUrl;
    }

    public String getVideoUrl()
    {
        return videoUrl;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
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

    public void setRelationSource(String relationSource)
    {
        this.relationSource = relationSource;
    }

    public String getRelationSource()
    {
        return relationSource;
    }

    public void setIsVisible(String isVisible)
    {
        this.isVisible = isVisible;
    }

    public String getIsVisible()
    {
        return isVisible;
    }

    public void setIsExcluded(Boolean isExcluded)
    {
        this.isExcluded = isExcluded;
    }

    public Boolean getIsExcluded()
    {
        return isExcluded;
    }

    public void setPromotionDesc(String promotionDesc)
    {
        this.promotionDesc = promotionDesc;
    }

    public String getPromotionDesc()
    {
        return promotionDesc;
    }

    public void setDeveloper(String developer)
    {
        this.developer = developer;
    }

    public String getDeveloper()
    {
        return developer;
    }

    public void setPublisher(String publisher)
    {
        this.publisher = publisher;
    }

    public String getPublisher()
    {
        return publisher;
    }

    public void setPackageName(String packageName)
    {
        this.packageName = packageName;
    }

    public String getPackageName()
    {
        return packageName;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getVersion()
    {
        return version;
    }

    public void setSize(String size)
    {
        this.size = size;
    }

    public String getSize()
    {
        return size;
    }

    public void setDeviceSupport(String deviceSupport)
    {
        this.deviceSupport = deviceSupport;
    }

    public String getDeviceSupport()
    {
        return deviceSupport;
    }

    public void setDownloadCount(Integer downloadCount)
    {
        this.downloadCount = downloadCount;
    }

    public Integer getDownloadCount()
    {
        return downloadCount;
    }

    public void setRating(BigDecimal rating)
    {
        this.rating = rating;
    }

    public BigDecimal getRating()
    {
        return rating;
    }

    public void setFeatures(String features)
    {
        this.features = features;
    }

    public String getFeatures()
    {
        return features;
    }

    public void setTags(String tags)
    {
        this.tags = tags;
    }

    public String getTags()
    {
        return tags;
    }

    public void setLaunchTime(Date launchTime)
    {
        this.launchTime = launchTime;
    }

    public Date getLaunchTime()
    {
        return launchTime;
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

    public void setSourceBoxId(Long sourceBoxId)
    {
        this.sourceBoxId = sourceBoxId;
    }

    public Long getSourceBoxId()
    {
        return sourceBoxId;
    }

    public void setDownloadUrl(String downloadUrl)
    {
        this.downloadUrl = downloadUrl;
    }

    public String getDownloadUrl()
    {
        return downloadUrl;
    }

    public void setAndroidUrl(String androidUrl)
    {
        this.androidUrl = androidUrl;
    }

    public String getAndroidUrl()
    {
        return androidUrl;
    }

    public void setIosUrl(String iosUrl)
    {
        this.iosUrl = iosUrl;
    }

    public String getIosUrl()
    {
        return iosUrl;
    }

    public void setApkUrl(String apkUrl)
    {
        this.apkUrl = apkUrl;
    }

    public String getApkUrl()
    {
        return apkUrl;
    }

    public void setDiscountLabel(String discountLabel)
    {
        this.discountLabel = discountLabel;
    }

    public String getDiscountLabel()
    {
        return discountLabel;
    }

    public void setFirstChargeDomestic(BigDecimal firstChargeDomestic)
    {
        this.firstChargeDomestic = firstChargeDomestic;
    }

    public BigDecimal getFirstChargeDomestic()
    {
        return firstChargeDomestic;
    }

    public void setFirstChargeOverseas(BigDecimal firstChargeOverseas)
    {
        this.firstChargeOverseas = firstChargeOverseas;
    }

    public BigDecimal getFirstChargeOverseas()
    {
        return firstChargeOverseas;
    }

    public void setRechargeDomestic(BigDecimal rechargeDomestic)
    {
        this.rechargeDomestic = rechargeDomestic;
    }

    public BigDecimal getRechargeDomestic()
    {
        return rechargeDomestic;
    }

    public void setRechargeOverseas(BigDecimal rechargeOverseas)
    {
        this.rechargeOverseas = rechargeOverseas;
    }

    public BigDecimal getRechargeOverseas()
    {
        return rechargeOverseas;
    }

    public void setHasSupport(String hasSupport)
    {
        this.hasSupport = hasSupport;
    }

    public String getHasSupport()
    {
        return hasSupport;
    }

    public void setSupportDesc(String supportDesc)
    {
        this.supportDesc = supportDesc;
    }

    public String getSupportDesc()
    {
        return supportDesc;
    }

    public void setHasLowDeductCoupon(String hasLowDeductCoupon)
    {
        this.hasLowDeductCoupon = hasLowDeductCoupon;
    }

    public String getHasLowDeductCoupon()
    {
        return hasLowDeductCoupon;
    }

    public void setLowDeductCouponUrl(String lowDeductCouponUrl)
    {
        this.lowDeductCouponUrl = lowDeductCouponUrl;
    }

    public String getLowDeductCouponUrl()
    {
        return lowDeductCouponUrl;
    }

    public void setBoxCount(Integer boxCount)
    {
        this.boxCount = boxCount;
    }

    public Integer getBoxCount()
    {
        return boxCount;
    }

    public void setBoxIds(String boxIds)
    {
        this.boxIds = boxIds;
    }

    public String getBoxIds()
    {
        return boxIds;
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

    public void setCategoryId(Long categoryId)
    {
        this.categoryId = categoryId;
    }

    public Long getCategoryId()
    {
        return categoryId;
    }
    
    public void setCategories(List<GbGameCategoryRelation> categories)
    {
        this.categories = categories;
    }
    
    public List<GbGameCategoryRelation> getCategories()
    {
        return categories;
    }

    public void setBoxes(List<Map<String, Object>> boxes)
    {
        this.boxes = boxes;
    }

    public List<Map<String, Object>> getBoxes()
    {
        return boxes;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("siteId", getSiteId())
            .append("name", getName())
            .append("subtitle", getSubtitle())
            .append("shortName", getShortName())
            .append("gameType", getGameType())
            .append("downloadCount", getDownloadCount())
            .append("rating", getRating())
            .append("launchTime", getLaunchTime())
            .append("iconUrl", getIconUrl())
            .append("coverUrl", getCoverUrl())
            .append("bannerUrl", getBannerUrl())
            .append("screenshots", getScreenshots())
            .append("videoUrl", getVideoUrl())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
