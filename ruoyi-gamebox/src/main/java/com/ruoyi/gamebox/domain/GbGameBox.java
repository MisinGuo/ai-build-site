package com.ruoyi.gamebox.domain;

import java.math.BigDecimal;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 游戏盒子对象 gb_game_boxes
 * 
 * @author ruoyi
 */
public class GbGameBox extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 所属网站ID */
    @Excel(name = "所属网站ID")
    private Long siteId;

    /** 盒子名称 */
    @Excel(name = "盒子名称")
    @NotBlank(message = "盒子名称不能为空")
    @Size(min = 0, max = 100, message = "盒子名称长度不能超过100个字符")
    private String name;

    /** 盒子Logo */
    private String logoUrl;

    /** 盒子Banner图 */
    private String bannerUrl;

    /** 盒子二维码图片URL */
    private String qrcodeUrl;

    /** 盒子描述 */
    private String description;

    /** 官网地址 */
    @Excel(name = "官网地址")
    private String officialUrl;

    /** 通用下载地址 */
    @Excel(name = "下载地址")
    private String downloadUrl;

    /** 安卓下载 */
    private String androidUrl;

    /** iOS下载 */
    private String iosUrl;

    /** 推广链接① */
    private String promoteUrl1;

    /** 推广链接② */
    private String promoteUrl2;

    /** 推广链接③ */
    private String promoteUrl3;

    /** 先注册再下载链接④ */
    private String registerDownloadUrl;

    /** 折扣率（0.1-1.0） */
    @Excel(name = "折扣率")
    private BigDecimal discountRate;

    /** 特色功能（JSON数组） */
    private String features;

    /** 收录游戏数 */
    @Excel(name = "收录游戏数")
    private Integer gameCount;

    /** 排序 */
    private Integer sortOrder;

    /** 盒子优先级（0-100，数值越大优先级越高） */
    @Excel(name = "优先级")
    private Integer priority;

    /** 状态：0-下架 1-上架 */
    @Excel(name = "状态", readConverterExp = "0=下架,1=上架")
    private String status;

    /** 删除标志 */
    private String delFlag;

    /** 分类ID */
    private Long categoryId;

    /** 分类名称（关联查询字段） */
    private String categoryName;

    /** 分类图标（关联查询字段） */
    private String categoryIcon;

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

    /** 盒子的所有分类关联（用于多对多关系） */
    private List<GbBoxCategoryRelation> categories;

    /** 是否同步更新游戏的siteId（仅用于更新操作的参数传递，不对应数据库字段） */
    private Boolean syncGameSiteId;

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

    public void setLogoUrl(String logoUrl)
    {
        this.logoUrl = logoUrl;
    }

    public String getLogoUrl()
    {
        return logoUrl;
    }

    public void setBannerUrl(String bannerUrl)
    {
        this.bannerUrl = bannerUrl;
    }

    public String getBannerUrl()
    {
        return bannerUrl;
    }

    public void setQrcodeUrl(String qrcodeUrl)
    {
        this.qrcodeUrl = qrcodeUrl;
    }

    public String getQrcodeUrl()
    {
        return qrcodeUrl;
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

    public void setOfficialUrl(String officialUrl)
    {
        this.officialUrl = officialUrl;
    }

    public String getOfficialUrl()
    {
        return officialUrl;
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

    public void setPromoteUrl1(String promoteUrl1)
    {
        this.promoteUrl1 = promoteUrl1;
    }

    public String getPromoteUrl1()
    {
        return promoteUrl1;
    }

    public void setPromoteUrl2(String promoteUrl2)
    {
        this.promoteUrl2 = promoteUrl2;
    }

    public String getPromoteUrl2()
    {
        return promoteUrl2;
    }

    public void setPromoteUrl3(String promoteUrl3)
    {
        this.promoteUrl3 = promoteUrl3;
    }

    public String getPromoteUrl3()
    {
        return promoteUrl3;
    }

    public void setRegisterDownloadUrl(String registerDownloadUrl)
    {
        this.registerDownloadUrl = registerDownloadUrl;
    }

    public String getRegisterDownloadUrl()
    {
        return registerDownloadUrl;
    }

    public void setDiscountRate(BigDecimal discountRate)
    {
        this.discountRate = discountRate;
    }

    public BigDecimal getDiscountRate()
    {
        return discountRate;
    }

    public void setFeatures(String features)
    {
        this.features = features;
    }

    public String getFeatures()
    {
        return features;
    }

    public void setGameCount(Integer gameCount)
    {
        this.gameCount = gameCount;
    }

    public Integer getGameCount()
    {
        return gameCount;
    }

    public void setSortOrder(Integer sortOrder)
    {
        this.sortOrder = sortOrder;
    }

    public Integer getSortOrder()
    {
        return sortOrder;
    }

    public void setPriority(Integer priority)
    {
        this.priority = priority;
    }

    public Integer getPriority()
    {
        return priority;
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

    public void setCategoryId(Long categoryId)
    {
        this.categoryId = categoryId;
    }

    public Long getCategoryId()
    {
        return categoryId;
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

    public void setCategories(List<GbBoxCategoryRelation> categories)
    {
        this.categories = categories;
    }

    public List<GbBoxCategoryRelation> getCategories()
    {
        return categories;
    }

    public void setSyncGameSiteId(Boolean syncGameSiteId)
    {
        this.syncGameSiteId = syncGameSiteId;
    }

    public Boolean getSyncGameSiteId()
    {
        return syncGameSiteId;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("siteId", getSiteId())
            .append("name", getName())
            .append("officialUrl", getOfficialUrl())
            .append("downloadUrl", getDownloadUrl())
            .append("discountRate", getDiscountRate())
            .append("gameCount", getGameCount())
            .append("sortOrder", getSortOrder())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
