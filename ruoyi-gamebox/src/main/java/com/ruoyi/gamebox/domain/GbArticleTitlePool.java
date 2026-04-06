package com.ruoyi.gamebox.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 标题池对象 gb_title_pool
 * 
 * @author ruoyi
 * @date 2026-01-01
 */
public class GbArticleTitlePool extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 所属网站ID(通过JOIN批次表获取，不存储在本表) */
    @Excel(name = "所属网站ID")
    private Long siteId;

    /** 分类ID(通过JOIN批次表获取，不存储在本表) */
    @Excel(name = "分类ID")
    private Long categoryId;

    /** 文章标题 */
    @Excel(name = "文章标题")
    private String title;

    /** 关键词(逗号分隔) */
    @Excel(name = "关键词")
    private String keywords;

    /** 参考内容/描述 */
    @Excel(name = "参考内容")
    private String referenceContent;

    /** 数据来源名称 */
    @Excel(name = "数据来源")
    private String sourceName;

    /** 来源URL */
    @Excel(name = "来源URL")
    private String sourceUrl;

    /** 导入批次号 */
    @Excel(name = "导入批次")
    private String importBatch;

    /** 使用状态: 0-未使用 1-已使用 2-已废弃 */
    @Excel(name = "使用状态", readConverterExp = "0=未使用,1=已使用,2=已废弃")
    private String usageStatus;

    /** 已使用次数 */
    @Excel(name = "使用次数")
    private Integer usedCount;

    /** 最后使用时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "最后使用时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date usedTime;

    /** 使用该标题生成的文章IDs(逗号分隔) */
    private String usedArticleIds;

    /** 计划发布日期(扩展字段,后续使用) */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "计划发布日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date scheduledDate;

    /** 目标站点ID(扩展字段,后续使用) */
    @Excel(name = "目标站点ID")
    private Long targetSiteId;

    /** 优先级(数值越大优先级越高) */
    @Excel(name = "优先级")
    private Integer priority;

    /** 标签(逗号分隔) */
    @Excel(name = "标签")
    private String tags;

    /** 扩展数据(JSON格式,存储其他字段) */
    private String extraData;

    /** 删除标志: 0-存在 2-删除 */
    private String delFlag;

    // ========== 多站点支持的额外字段（查询时使用，不直接对应表字段） ==========
    
    /** 查询模式：creator-创建者模式，related-关联模式 */
    private String queryMode;

    /** 是否包含默认配置（仅在创建者模式下使用） */
    private Boolean includeDefault;

    /** 关联来源：own-自有，default-默认配置 */
    private String relationSource;

    /** 是否被排除（默认配置被某网站排除） */
    private Integer isExcluded;

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

    public void setCategoryId(Long categoryId) 
    {
        this.categoryId = categoryId;
    }

    public Long getCategoryId() 
    {
        return categoryId;
    }

    public void setTitle(String title) 
    {
        this.title = title;
    }

    public String getTitle() 
    {
        return title;
    }

    public void setKeywords(String keywords) 
    {
        this.keywords = keywords;
    }

    public String getKeywords() 
    {
        return keywords;
    }

    public void setReferenceContent(String referenceContent) 
    {
        this.referenceContent = referenceContent;
    }

    public String getReferenceContent() 
    {
        return referenceContent;
    }

    public void setSourceName(String sourceName) 
    {
        this.sourceName = sourceName;
    }

    public String getSourceName() 
    {
        return sourceName;
    }

    public void setSourceUrl(String sourceUrl) 
    {
        this.sourceUrl = sourceUrl;
    }

    public String getSourceUrl() 
    {
        return sourceUrl;
    }

    public void setImportBatch(String importBatch) 
    {
        this.importBatch = importBatch;
    }

    public String getImportBatch() 
    {
        return importBatch;
    }

    public void setUsageStatus(String usageStatus) 
    {
        this.usageStatus = usageStatus;
    }

    public String getUsageStatus() 
    {
        return usageStatus;
    }

    public void setUsedCount(Integer usedCount) 
    {
        this.usedCount = usedCount;
    }

    public Integer getUsedCount() 
    {
        return usedCount;
    }

    public void setUsedTime(Date usedTime) 
    {
        this.usedTime = usedTime;
    }

    public Date getUsedTime() 
    {
        return usedTime;
    }

    public void setUsedArticleIds(String usedArticleIds) 
    {
        this.usedArticleIds = usedArticleIds;
    }

    public String getUsedArticleIds() 
    {
        return usedArticleIds;
    }

    public void setScheduledDate(Date scheduledDate) 
    {
        this.scheduledDate = scheduledDate;
    }

    public Date getScheduledDate() 
    {
        return scheduledDate;
    }

    public void setTargetSiteId(Long targetSiteId) 
    {
        this.targetSiteId = targetSiteId;
    }

    public Long getTargetSiteId() 
    {
        return targetSiteId;
    }

    public void setPriority(Integer priority) 
    {
        this.priority = priority;
    }

    public Integer getPriority() 
    {
        return priority;
    }

    public void setTags(String tags) 
    {
        this.tags = tags;
    }

    public String getTags() 
    {
        return tags;
    }

    public void setExtraData(String extraData) 
    {
        this.extraData = extraData;
    }

    public String getExtraData() 
    {
        return extraData;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
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

    public void setRelationSource(String relationSource) 
    {
        this.relationSource = relationSource;
    }

    public String getRelationSource() 
    {
        return relationSource;
    }

    public void setIsExcluded(Integer isExcluded) 
    {
        this.isExcluded = isExcluded;
    }

    public Integer getIsExcluded() 
    {
        return isExcluded;
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
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("siteId", getSiteId())
            .append("categoryId", getCategoryId())
            .append("title", getTitle())
            .append("keywords", getKeywords())
            .append("referenceContent", getReferenceContent())
            .append("sourceName", getSourceName())
            .append("sourceUrl", getSourceUrl())
            .append("importBatch", getImportBatch())
            .append("usageStatus", getUsageStatus())
            .append("usedCount", getUsedCount())
            .append("usedTime", getUsedTime())
            .append("usedArticleIds", getUsedArticleIds())
            .append("scheduledDate", getScheduledDate())
            .append("targetSiteId", getTargetSiteId())
            .append("priority", getPriority())
            .append("tags", getTags())
            .append("extraData", getExtraData())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
