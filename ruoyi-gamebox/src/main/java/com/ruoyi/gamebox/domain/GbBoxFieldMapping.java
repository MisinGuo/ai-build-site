package com.ruoyi.gamebox.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 盒子字段映射配置对象 gb_box_field_mappings
 * 
 * @author ruoyi
 * @date 2026-01-25
 */
public class GbBoxFieldMapping extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 关联的盒子ID（NULL表示通用配置） */
    private Long boxId;

    /** 资源类型：game-游戏 / box-盒子 */
    @Excel(name = "资源类型", readConverterExp = "game=游戏,box=盒子")
    @NotBlank(message = "资源类型不能为空")
    @Size(max = 20, message = "资源类型长度不能超过20个字符")
    private String resourceType;

    /** 源字段路径（支持嵌套，如：a.b.c或a.0.b） */
    @Excel(name = "源字段路径")
    @NotBlank(message = "源字段路径不能为空")
    @Size(max = 200, message = "源字段路径长度不能超过200个字符")
    private String sourceField;

    /** 目标字段名（主表字段或platform_data中的key） */
    @Excel(name = "目标字段名")
    @NotBlank(message = "目标字段名不能为空")
    @Size(max = 100, message = "目标字段名长度不能超过100个字符")
    private String targetField;

    /** 字段类型：string/int/decimal/json/date/boolean */
    @Excel(name = "字段类型", readConverterExp = "string=字符串,int=整数,decimal=小数,json=JSON,date=日期,boolean=布尔")
    private String fieldType;

    /** 目标位置：main-主表 / ext-附表platform_data */
    @Excel(name = "目标位置", readConverterExp = "main=主表,ext=附表")
    private String targetLocation;

    /** 转换表达式（可选，用于字段值转换） */
    private String transformExpression;

    /** 值映射规则(JSON格式): {type: "direct/regex/function", mappings: {...}} */
    private String valueMapping;

    /** 默认值（源字段不存在或为空时使用） */
    private String defaultValue;

    /** 是否必填：0-否 1-是 */
    @Excel(name = "是否必填", readConverterExp = "0=否,1=是")
    private String isRequired;

    /** 排序 */
    @Excel(name = "排序")
    private Integer sortOrder;

    /** 状态：0-禁用 1-启用 */
    @Excel(name = "状态", readConverterExp = "0=禁用,1=启用")
    private String status;

    /**
     * 字段冲突策略（对所有 targetLocation 均生效）。
     * main 默认 fill_empty；relation/promotion_link/platform_data 默认 overwrite。
     * fill_empty：仅补全空字段，已有数据不覆盖
     * overwrite：始终用最新导入数据覆盖
     * skip：已存在时始终跳过此字段，不进行任何更新
     */
    private String conflictStrategy;

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

    public void setResourceType(String resourceType) 
    {
        this.resourceType = resourceType;
    }

    public String getResourceType() 
    {
        return resourceType;
    }

    public void setSourceField(String sourceField) 
    {
        this.sourceField = sourceField;
    }

    public String getSourceField() 
    {
        return sourceField;
    }

    public void setTargetField(String targetField) 
    {
        this.targetField = targetField;
    }

    public String getTargetField() 
    {
        return targetField;
    }

    public void setFieldType(String fieldType) 
    {
        this.fieldType = fieldType;
    }

    public String getFieldType() 
    {
        return fieldType;
    }

    public void setTargetLocation(String targetLocation) 
    {
        this.targetLocation = targetLocation;
    }

    public String getTargetLocation() 
    {
        return targetLocation;
    }

    public void setTransformExpression(String transformExpression) 
    {
        this.transformExpression = transformExpression;
    }

    public String getTransformExpression() 
    {
        return transformExpression;
    }

    public void setValueMapping(String valueMapping) 
    {
        this.valueMapping = valueMapping;
    }

    public String getValueMapping() 
    {
        return valueMapping;
    }

    public void setDefaultValue(String defaultValue) 
    {
        this.defaultValue = defaultValue;
    }

    public String getDefaultValue() 
    {
        return defaultValue;
    }

    public void setIsRequired(String isRequired) 
    {
        this.isRequired = isRequired;
    }

    public String getIsRequired() 
    {
        return isRequired;
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

    public void setConflictStrategy(String conflictStrategy)
    {
        this.conflictStrategy = conflictStrategy;
    }

    public String getConflictStrategy()
    {
        return conflictStrategy;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("resourceType", getResourceType())
            .append("sourceField", getSourceField())
            .append("targetField", getTargetField())
            .append("fieldType", getFieldType())
            .append("targetLocation", getTargetLocation())
            .append("transformExpression", getTransformExpression())
            .append("valueMapping", getValueMapping())
            .append("defaultValue", getDefaultValue())
            .append("isRequired", getIsRequired())
            .append("sortOrder", getSortOrder())
            .append("status", getStatus())
            .append("conflictStrategy", getConflictStrategy())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
