package com.ruoyi.gamebox.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 分类类型配置对象 gb_category_types
 * 
 * @author ruoyi
 */
public class GbCategoryType extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 分类类型值（唯一标识） */
    @Excel(name = "分类类型值")
    private String value;

    /** 分类类型标签（显示名称） */
    @Excel(name = "分类类型标签")
    private String label;

    /** Tag标签类型 */
    @Excel(name = "Tag标签类型")
    private String tagType;

    /** 分类类型描述 */
    @Excel(name = "分类类型描述")
    private String description;

    /** 排序号 */
    @Excel(name = "排序号")
    private Integer sortOrder;

    /** 状态：0禁用 1启用 */
    @Excel(name = "状态", readConverterExp = "0=禁用,1=启用")
    private String status;

    /** 是否系统内置：0否 1是 */
    @Excel(name = "是否系统内置", readConverterExp = "0=否,1=是")
    private String isSystem;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setValue(String value) 
    {
        this.value = value;
    }

    public String getValue() 
    {
        return value;
    }

    public void setLabel(String label) 
    {
        this.label = label;
    }

    public String getLabel() 
    {
        return label;
    }

    public void setTagType(String tagType) 
    {
        this.tagType = tagType;
    }

    public String getTagType() 
    {
        return tagType;
    }

    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
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

    public void setIsSystem(String isSystem) 
    {
        this.isSystem = isSystem;
    }

    public String getIsSystem() 
    {
        return isSystem;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("value", getValue())
            .append("label", getLabel())
            .append("tagType", getTagType())
            .append("description", getDescription())
            .append("sortOrder", getSortOrder())
            .append("status", getStatus())
            .append("isSystem", getIsSystem())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
