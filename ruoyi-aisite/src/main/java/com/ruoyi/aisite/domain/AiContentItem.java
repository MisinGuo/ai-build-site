package com.ruoyi.aisite.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 内容项对象 ai_content_items（全行业通用）
 */
public class AiContentItem extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 站点ID */
    @Excel(name = "站点ID")
    @NotNull(message = "站点ID不能为空")
    private Long siteId;

    /** 内容类型编码 */
    @Excel(name = "内容类型")
    @NotBlank(message = "内容类型不能为空")
    private String typeCode;

    /** 主标题（搜索/SEO用） */
    @Excel(name = "标题")
    @NotBlank(message = "标题不能为空")
    private String title;

    /** URL 友好标识 */
    @Excel(name = "Slug")
    private String slug;

    /** 封面图 */
    @Excel(name = "封面图")
    private String coverImage;

    /** 摘要 */
    @Excel(name = "摘要")
    private String summary;

    /** 正文内容（富文本） */
    private String content;

    /** 动态字段 JSON（按 schema 填写） */
    private String fieldsJson;

    /** SEO 标题 */
    @Excel(name = "SEO标题")
    private String seoTitle;

    /** SEO 描述 */
    @Excel(name = "SEO描述")
    private String seoDescription;

    /** SEO 关键词 */
    @Excel(name = "SEO关键词")
    private String seoKeywords;

    /** 规范链接 */
    private String canonicalUrl;

    /** 发布状态：draft/published/archived */
    @Excel(name = "发布状态")
    private String publishStatus;

    /** 发布时间 */
    @Excel(name = "发布时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date publishAt;

    /** 是否精选：0-否 1-是 */
    @Excel(name = "是否精选")
    private String isFeatured;

    /** 排序号 */
    @Excel(name = "排序号")
    private Integer sortOrder;

    /** 浏览量 */
    @Excel(name = "浏览量")
    private Long viewCount;

    /** 创建者用户ID */
    private Long creatorId;

    /** 部门ID */
    private Long deptId;

    /** 状态：0禁用 1启用 */
    @Excel(name = "状态")
    private String status;

    /** 删除标志 */
    private String delFlag;

    /** 内容类型名称（非数据库，用于显示） */
    private String typeName;

    /** 所属分类列表（非数据库，查询时附带） */
    private String categoryIds;

    public void setId(Long id) { this.id = id; }
    public Long getId() { return id; }

    public void setSiteId(Long siteId) { this.siteId = siteId; }
    public Long getSiteId() { return siteId; }

    public void setTypeCode(String typeCode) { this.typeCode = typeCode; }
    public String getTypeCode() { return typeCode; }

    public void setTitle(String title) { this.title = title; }
    public String getTitle() { return title; }

    public void setSlug(String slug) { this.slug = slug; }
    public String getSlug() { return slug; }

    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
    public String getCoverImage() { return coverImage; }

    public void setSummary(String summary) { this.summary = summary; }
    public String getSummary() { return summary; }

    public void setContent(String content) { this.content = content; }
    public String getContent() { return content; }

    public void setFieldsJson(String fieldsJson) { this.fieldsJson = fieldsJson; }
    public String getFieldsJson() { return fieldsJson; }

    public void setSeoTitle(String seoTitle) { this.seoTitle = seoTitle; }
    public String getSeoTitle() { return seoTitle; }

    public void setSeoDescription(String seoDescription) { this.seoDescription = seoDescription; }
    public String getSeoDescription() { return seoDescription; }

    public void setSeoKeywords(String seoKeywords) { this.seoKeywords = seoKeywords; }
    public String getSeoKeywords() { return seoKeywords; }

    public void setCanonicalUrl(String canonicalUrl) { this.canonicalUrl = canonicalUrl; }
    public String getCanonicalUrl() { return canonicalUrl; }

    public void setPublishStatus(String publishStatus) { this.publishStatus = publishStatus; }
    public String getPublishStatus() { return publishStatus; }

    public void setPublishAt(Date publishAt) { this.publishAt = publishAt; }
    public Date getPublishAt() { return publishAt; }

    public void setIsFeatured(String isFeatured) { this.isFeatured = isFeatured; }
    public String getIsFeatured() { return isFeatured; }

    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public Integer getSortOrder() { return sortOrder; }

    public void setViewCount(Long viewCount) { this.viewCount = viewCount; }
    public Long getViewCount() { return viewCount; }

    public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }
    public Long getCreatorId() { return creatorId; }

    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public Long getDeptId() { return deptId; }

    public void setStatus(String status) { this.status = status; }
    public String getStatus() { return status; }

    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
    public String getDelFlag() { return delFlag; }

    public void setTypeName(String typeName) { this.typeName = typeName; }
    public String getTypeName() { return typeName; }

    public void setCategoryIds(String categoryIds) { this.categoryIds = categoryIds; }
    public String getCategoryIds() { return categoryIds; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("siteId", getSiteId())
            .append("typeCode", getTypeCode())
            .append("title", getTitle())
            .append("slug", getSlug())
            .append("publishStatus", getPublishStatus())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .toString();
    }
}
