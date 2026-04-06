package com.ruoyi.gamebox.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 标题池批次对象 gb_title_pool_batch
 * 
 * @author ruoyi
 * @date 2026-01-01
 */
public class GbTitlePoolBatch extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 所属网站ID(0表示全局默认) */
    @Excel(name = "所属网站ID")
    private Long siteId;

    /** 批次号 */
    @Excel(name = "批次号")
    private String batchCode;

    /** 批次名称 */
    @Excel(name = "批次名称")
    private String batchName;

    /** 导入日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "导入日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date importDate;

    /** 导入来源 */
    @Excel(name = "导入来源")
    private String importSource;

    /** 标题数量 */
    @Excel(name = "标题数量")
    private Integer titleCount;

    /** 关联分类ID */
    @Excel(name = "关联分类ID")
    private Long categoryId;

    /** 状态 */
    @Excel(name = "状态", readConverterExp = "0=禁用,1=启用")
    private String status;

    /** 是否被排除（默认配置被某网站排除） */
    private String isExcluded;

    // 关联查询字段
    private String siteName;
    private String categoryName;
    private String categoryIcon;
    private String relationSource; // 关联来源：own/default/shared
    private String isVisible; // 可见性状态（关联模式下使用）
    private Integer relatedSitesCount; // 关联的网站数量
    private Integer excludedSitesCount; // 排除的网站数量
    
    // 查询参数字段（不映射到数据库）
    private String queryMode; // 查询模式：creator/related
    private Boolean includeDefault; // 是否包含默认配置

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

    public void setBatchCode(String batchCode) 
    {
        this.batchCode = batchCode;
    }

    public String getBatchCode() 
    {
        return batchCode;
    }

    public void setBatchName(String batchName) 
    {
        this.batchName = batchName;
    }

    public String getBatchName() 
    {
        return batchName;
    }

    public void setImportDate(Date importDate) 
    {
        this.importDate = importDate;
    }

    public Date getImportDate() 
    {
        return importDate;
    }

    public void setImportSource(String importSource) 
    {
        this.importSource = importSource;
    }

    public String getImportSource() 
    {
        return importSource;
    }

    public void setTitleCount(Integer titleCount) 
    {
        this.titleCount = titleCount;
    }

    public Integer getTitleCount() 
    {
        return titleCount;
    }

    public void setCategoryId(Long categoryId) 
    {
        this.categoryId = categoryId;
    }

    public Long getCategoryId() 
    {
        return categoryId;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public String getIsExcluded() 
    {
        return isExcluded;
    }

    public void setIsExcluded(String isExcluded) 
    {
        this.isExcluded = isExcluded;
    }

    public String getSiteName() 
    {
        return siteName;
    }

    public void setSiteName(String siteName) 
    {
        this.siteName = siteName;
    }

    public String getCategoryName() 
    {
        return categoryName;
    }

    public void setCategoryName(String categoryName) 
    {
        this.categoryName = categoryName;
    }

    public String getCategoryIcon() 
    {
        return categoryIcon;
    }

    public void setCategoryIcon(String categoryIcon) 
    {
        this.categoryIcon = categoryIcon;
    }

    public String getRelationSource() 
    {
        return relationSource;
    }

    public void setRelationSource(String relationSource) 
    {
        this.relationSource = relationSource;
    }

    public String getIsVisible() 
    {
        return isVisible;
    }

    public void setIsVisible(String isVisible) 
    {
        this.isVisible = isVisible;
    }

    public String getQueryMode() 
    {
        return queryMode;
    }

    public void setQueryMode(String queryMode) 
    {
        this.queryMode = queryMode;
    }

    public Boolean getIncludeDefault() 
    {
        return includeDefault;
    }

    public void setIncludeDefault(Boolean includeDefault) 
    {
        this.includeDefault = includeDefault;
    }

    public Integer getRelatedSitesCount() 
    {
        return relatedSitesCount;
    }

    public void setRelatedSitesCount(Integer relatedSitesCount) 
    {
        this.relatedSitesCount = relatedSitesCount;
    }

    public Integer getExcludedSitesCount() 
    {
        return excludedSitesCount;
    }

    public void setExcludedSitesCount(Integer excludedSitesCount) 
    {
        this.excludedSitesCount = excludedSitesCount;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("siteId", getSiteId())
            .append("batchCode", getBatchCode())
            .append("batchName", getBatchName())
            .append("importDate", getImportDate())
            .append("importSource", getImportSource())
            .append("titleCount", getTitleCount())
            .append("categoryId", getCategoryId())
            .append("status", getStatus())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
