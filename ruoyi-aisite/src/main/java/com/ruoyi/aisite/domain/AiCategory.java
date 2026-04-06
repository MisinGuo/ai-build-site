package com.ruoyi.aisite.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import javax.validation.constraints.NotBlank;

/**
 * 通用分类对象 ai_categories
 */
public class AiCategory extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 站点ID */
    @Excel(name = "站点ID")
    private Long siteId;

    /** 内容类型编码（关联 ai_content_types.code） */
    @Excel(name = "内容类型")
    private String typeCode;

    /** 父分类ID，0 表示顶级 */
    @Excel(name = "父分类ID")
    private Long parentId;

    /** 分类名称 */
    @Excel(name = "分类名称")
    @NotBlank(message = "分类名称不能为空")
    private String name;

    /** URL 友好标识 */
    @Excel(name = "Slug")
    @NotBlank(message = "Slug不能为空")
    private String slug;

    /** 图标 */
    @Excel(name = "图标")
    private String icon;

    /** 描述 */
    @Excel(name = "描述")
    private String description;

    /** 排序号 */
    @Excel(name = "排序号")
    private Integer sortOrder;

    /** 关联内容项数量 */
    @Excel(name = "内容数量")
    private Integer itemCount;

    /** 状态：0禁用 1启用 */
    @Excel(name = "状态")
    private String status;

    /** 删除标志 */
    private String delFlag;

    public void setId(Long id) { this.id = id; }
    public Long getId() { return id; }

    public void setSiteId(Long siteId) { this.siteId = siteId; }
    public Long getSiteId() { return siteId; }

    public void setTypeCode(String typeCode) { this.typeCode = typeCode; }
    public String getTypeCode() { return typeCode; }

    public void setParentId(Long parentId) { this.parentId = parentId; }
    public Long getParentId() { return parentId; }

    public void setName(String name) { this.name = name; }
    public String getName() { return name; }

    public void setSlug(String slug) { this.slug = slug; }
    public String getSlug() { return slug; }

    public void setIcon(String icon) { this.icon = icon; }
    public String getIcon() { return icon; }

    public void setDescription(String description) { this.description = description; }
    public String getDescription() { return description; }

    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public Integer getSortOrder() { return sortOrder; }

    public void setItemCount(Integer itemCount) { this.itemCount = itemCount; }
    public Integer getItemCount() { return itemCount; }

    public void setStatus(String status) { this.status = status; }
    public String getStatus() { return status; }

    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
    public String getDelFlag() { return delFlag; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("siteId", getSiteId())
            .append("typeCode", getTypeCode())
            .append("parentId", getParentId())
            .append("name", getName())
            .append("slug", getSlug())
            .append("sortOrder", getSortOrder())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .toString();
    }
}
