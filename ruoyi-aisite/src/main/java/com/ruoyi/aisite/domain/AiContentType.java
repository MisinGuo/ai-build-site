package com.ruoyi.aisite.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import javax.validation.constraints.NotBlank;

/**
 * 内容类型对象 ai_content_types
 */
public class AiContentType extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 站点ID，NULL表示全局模板类型 */
    @Excel(name = "站点ID")
    private Long siteId;

    /** 类型编码，如 game/drama/product/property */
    @Excel(name = "类型编码")
    @NotBlank(message = "类型编码不能为空")
    private String code;

    /** 显示名称 */
    @Excel(name = "显示名称")
    @NotBlank(message = "名称不能为空")
    private String name;

    /** 描述 */
    @Excel(name = "描述")
    private String description;

    /** 字段定义 JSON（FieldSchema[]） */
    private String schemaJson;

    /** 列表页显示字段 JSON */
    private String listFields;

    /** 详情页显示字段 JSON */
    private String detailFields;

    /** 筛选字段 JSON */
    private String filterFields;

    /** SEO 模板 JSON */
    private String seoTemplate;

    /** 排序号 */
    @Excel(name = "排序号")
    private Integer sortOrder;

    /** 是否系统内置：0-否 1-是 */
    @Excel(name = "是否系统内置")
    private String isSystem;

    /** 状态：0禁用 1启用 */
    @Excel(name = "状态")
    private String status;

    /** 删除标志 */
    private String delFlag;

    /** 关联内容项数量（非数据库字段） */
    private Integer itemCount;

    public void setId(Long id) { this.id = id; }
    public Long getId() { return id; }

    public void setSiteId(Long siteId) { this.siteId = siteId; }
    public Long getSiteId() { return siteId; }

    public void setCode(String code) { this.code = code; }
    public String getCode() { return code; }

    public void setName(String name) { this.name = name; }
    public String getName() { return name; }

    public void setDescription(String description) { this.description = description; }
    public String getDescription() { return description; }

    public void setSchemaJson(String schemaJson) { this.schemaJson = schemaJson; }
    public String getSchemaJson() { return schemaJson; }

    public void setListFields(String listFields) { this.listFields = listFields; }
    public String getListFields() { return listFields; }

    public void setDetailFields(String detailFields) { this.detailFields = detailFields; }
    public String getDetailFields() { return detailFields; }

    public void setFilterFields(String filterFields) { this.filterFields = filterFields; }
    public String getFilterFields() { return filterFields; }

    public void setSeoTemplate(String seoTemplate) { this.seoTemplate = seoTemplate; }
    public String getSeoTemplate() { return seoTemplate; }

    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public Integer getSortOrder() { return sortOrder; }

    public void setIsSystem(String isSystem) { this.isSystem = isSystem; }
    public String getIsSystem() { return isSystem; }

    public void setStatus(String status) { this.status = status; }
    public String getStatus() { return status; }

    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
    public String getDelFlag() { return delFlag; }

    public void setItemCount(Integer itemCount) { this.itemCount = itemCount; }
    public Integer getItemCount() { return itemCount; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("siteId", getSiteId())
            .append("code", getCode())
            .append("name", getName())
            .append("description", getDescription())
            .append("sortOrder", getSortOrder())
            .append("isSystem", getIsSystem())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
