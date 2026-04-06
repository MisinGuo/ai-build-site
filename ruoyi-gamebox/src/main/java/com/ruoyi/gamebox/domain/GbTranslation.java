package com.ruoyi.gamebox.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 多语言翻译表 gb_translations
 * 
 * @author ruoyi
 * @date 2026-01-10
 */
public class GbTranslation extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 实体类型：game, gamebox, category */
    private String entityType;

    /** 实体ID */
    private Long entityId;

    /** 语言代码 */
    private String locale;

    /** 字段名称 */
    private String fieldName;

    /** 字段值 */
    private String fieldValue;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setEntityType(String entityType) 
    {
        this.entityType = entityType;
    }

    public String getEntityType() 
    {
        return entityType;
    }

    public void setEntityId(Long entityId) 
    {
        this.entityId = entityId;
    }

    public Long getEntityId() 
    {
        return entityId;
    }

    public void setLocale(String locale) 
    {
        this.locale = locale;
    }

    public String getLocale() 
    {
        return locale;
    }

    public void setFieldName(String fieldName) 
    {
        this.fieldName = fieldName;
    }

    public String getFieldName() 
    {
        return fieldName;
    }

    public void setFieldValue(String fieldValue) 
    {
        this.fieldValue = fieldValue;
    }

    public String getFieldValue() 
    {
        return fieldValue;
    }

    @Override
    public String toString() {
        return new StringBuilder()
            .append(getClass().getSimpleName())
            .append(" [")
            .append("Hash = ").append(hashCode())
            .append(", id=").append(id)
            .append(", entityType=").append(entityType)
            .append(", entityId=").append(entityId)
            .append(", locale=").append(locale)
            .append(", fieldName=").append(fieldName)
            .append(", fieldValue=").append(fieldValue)
            .append(", serialVersionUID=").append(serialVersionUID)
            .append("]").toString();
    }
}