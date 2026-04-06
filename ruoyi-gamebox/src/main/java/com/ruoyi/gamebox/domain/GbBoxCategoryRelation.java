package com.ruoyi.gamebox.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 游戏盒子分类关联对象 gb_box_category_relations
 * 
 * @author ruoyi
 * @date 2025-12-25
 */
public class GbBoxCategoryRelation extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 盒子ID */
    private Long boxId;

    /** 分类ID */
    private Long categoryId;

    /** 在该分类中的排序 */
    private Integer sortOrder;

    /** 是否主分类：0-否 1-是 */
    private String isPrimary;

    // 扩展字段（用于关联查询）
    /** 分类名称 */
    private String categoryName;

    /** 分类标识 */
    private String categorySlug;

    /** 分类图标 */
    private String categoryIcon;

    /** 盒子名称 */
    private String boxName;

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

    public void setCategoryId(Long categoryId) 
    {
        this.categoryId = categoryId;
    }

    public Long getCategoryId() 
    {
        return categoryId;
    }

    public void setSortOrder(Integer sortOrder) 
    {
        this.sortOrder = sortOrder;
    }

    public Integer getSortOrder() 
    {
        return sortOrder;
    }

    public void setIsPrimary(String isPrimary) 
    {
        this.isPrimary = isPrimary;
    }

    public String getIsPrimary() 
    {
        return isPrimary;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategorySlug() {
        return categorySlug;
    }

    public void setCategorySlug(String categorySlug) {
        this.categorySlug = categorySlug;
    }

    public String getCategoryIcon() {
        return categoryIcon;
    }

    public void setCategoryIcon(String categoryIcon) {
        this.categoryIcon = categoryIcon;
    }

    public String getBoxName() {
        return boxName;
    }

    public void setBoxName(String boxName) {
        this.boxName = boxName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("boxId", getBoxId())
            .append("categoryId", getCategoryId())
            .append("sortOrder", getSortOrder())
            .append("isPrimary", getIsPrimary())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
