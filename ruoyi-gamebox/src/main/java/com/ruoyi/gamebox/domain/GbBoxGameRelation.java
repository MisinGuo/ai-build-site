package com.ruoyi.gamebox.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 游戏盒子-游戏关联对象 gb_box_game_relations
 * 
 * @author ruoyi
 */
public class GbBoxGameRelation extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 游戏盒子ID */
    @Excel(name = "游戏盒子ID")
    private Long boxId;

    /** 游戏ID */
    @Excel(name = "游戏ID")
    private Long gameId;

    /** 盒子名称（关联查询字段） */
    private String boxName;

    /** 游戏名称（关联查询字段） */
    private String gameName;

    /** 盒子Logo URL（关联查询字段） */
    private String boxLogoUrl;

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

    /** 游戏下载链接 */
    @Excel(name = "游戏下载链接")
    private String downloadUrl;

    /** 游戏推广网站链接 */
    private String promoteUrl;

    /** 游戏二维码URL */
    private String qrcodeUrl;

    /** 推广语 */
    private String promoteText;

    /** 宣传卡片/海报URL */
    private String posterUrl;

    /** 推广链接集合（JSON格式，含 download_url/web_url/qrcode 等） */
    private String promotionLinks;

    /** 平台特有数据（JSON格式，含 is_h5/cps_ratio/platform_game_id 等） */
    private String platformData;

    /** 是否推荐：0-否 1-是 */
    @Excel(name = "是否推荐", readConverterExp = "0=否,1=是")
    private String isFeatured;

    /** 是否独占：0-否 1-是 */
    @Excel(name = "是否独占", readConverterExp = "0=否,1=是")
    private String isExclusive;

    /** 是否新游：0-否 1-是 */
    @Excel(name = "是否新游", readConverterExp = "0=否,1=是")
    private String isNew;

    /** 自定义名称 */
    @Excel(name = "自定义名称")
    private String customName;

    /** 自定义描述 */
    private String customDescription;

    /** 自定义下载链接 */
    @Excel(name = "自定义下载链接")
    private String customDownloadUrl;

    /** 浏览量 */
    @Excel(name = "浏览量")
    private Integer viewCount;

    /** 排序 */
    @Excel(name = "排序")
    private Integer sortOrder;

    /** 添加时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "添加时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date addedAt;

    // Getters and Setters
    
    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }

    public void setBoxId(Long boxId)
    {
        this.boxId = boxId;
    }

    public Long getBoxId()
    {
        return boxId;
    }

    public void setGameId(Long gameId)
    {
        this.gameId = gameId;
    }

    public Long getGameId()
    {
        return gameId;
    }

    public void setBoxName(String boxName)
    {
        this.boxName = boxName;
    }

    public String getBoxName()
    {
        return boxName;
    }

    public void setGameName(String gameName)
    {
        this.gameName = gameName;
    }

    public String getGameName()
    {
        return gameName;
    }

    public void setBoxLogoUrl(String boxLogoUrl)
    {
        this.boxLogoUrl = boxLogoUrl;
    }

    public String getBoxLogoUrl()
    {
        return boxLogoUrl;
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

    public void setDownloadUrl(String downloadUrl)
    {
        this.downloadUrl = downloadUrl;
    }

    public String getDownloadUrl()
    {
        return downloadUrl;
    }

    public void setPromoteUrl(String promoteUrl)
    {
        this.promoteUrl = promoteUrl;
    }

    public String getPromoteUrl()
    {
        return promoteUrl;
    }

    public void setQrcodeUrl(String qrcodeUrl)
    {
        this.qrcodeUrl = qrcodeUrl;
    }

    public String getQrcodeUrl()
    {
        return qrcodeUrl;
    }

    public void setPromoteText(String promoteText)
    {
        this.promoteText = promoteText;
    }

    public String getPromoteText()
    {
        return promoteText;
    }

    public void setPosterUrl(String posterUrl)
    {
        this.posterUrl = posterUrl;
    }

    public String getPosterUrl()
    {
        return posterUrl;
    }

    public void setPromotionLinks(String promotionLinks)
    {
        this.promotionLinks = promotionLinks;
    }

    public String getPromotionLinks()
    {
        return promotionLinks;
    }

    public void setPlatformData(String platformData)
    {
        this.platformData = platformData;
    }

    public String getPlatformData()
    {
        return platformData;
    }

    public void setIsFeatured(String isFeatured)
    {
        this.isFeatured = isFeatured;
    }

    public String getIsFeatured()
    {
        return isFeatured;
    }

    public void setIsExclusive(String isExclusive)
    {
        this.isExclusive = isExclusive;
    }

    public String getIsExclusive()
    {
        return isExclusive;
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

    public void setViewCount(Integer viewCount)
    {
        this.viewCount = viewCount;
    }

    public Integer getViewCount()
    {
        return viewCount;
    }

    public void setSortOrder(Integer sortOrder)
    {
        this.sortOrder = sortOrder;
    }

    public Integer getSortOrder()
    {
        return sortOrder;
    }

    public void setAddedAt(Date addedAt)
    {
        this.addedAt = addedAt;
    }

    public Date getAddedAt()
    {
        return addedAt;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("boxId", getBoxId())
            .append("gameId", getGameId())
            .append("discountLabel", getDiscountLabel())
            .append("firstChargeDomestic", getFirstChargeDomestic())
            .append("firstChargeOverseas", getFirstChargeOverseas())
            .append("rechargeDomestic", getRechargeDomestic())
            .append("rechargeOverseas", getRechargeOverseas())
            .append("hasSupport", getHasSupport())
            .append("supportDesc", getSupportDesc())
            .append("downloadUrl", getDownloadUrl())
            .append("promoteUrl", getPromoteUrl())
            .append("qrcodeUrl", getQrcodeUrl())
            .append("promoteText", getPromoteText())
            .append("posterUrl", getPosterUrl())
            .append("promotionLinks", getPromotionLinks())
            .append("platformData", getPlatformData())
            .append("isFeatured", getIsFeatured())
            .append("isExclusive", getIsExclusive())
            .append("isNew", getIsNew())
            .append("customName", getCustomName())
            .append("customDescription", getCustomDescription())
            .append("customDownloadUrl", getCustomDownloadUrl())
            .append("viewCount", getViewCount())
            .append("sortOrder", getSortOrder())
            .append("addedAt", getAddedAt())
            .append("createTime", getCreateTime())
            .toString();
    }
}
