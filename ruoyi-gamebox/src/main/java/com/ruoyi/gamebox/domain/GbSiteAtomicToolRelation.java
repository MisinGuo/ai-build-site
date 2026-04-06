package com.ruoyi.gamebox.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 网站-原子工具关联对象 gb_site_atomic_tool_relation
 * 
 * @author ruoyi
 * @date 2025-01-08
 */
public class GbSiteAtomicToolRelation extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 网站ID */
    @Excel(name = "网站ID")
    private Long siteId;

    /** 原子工具ID */
    @Excel(name = "原子工具ID")
    private Long atomicToolId;

    /** 关联类型：own-自有，default-默认配置，shared-共享，exclude-排除 */
    @Excel(name = "关联类型", readConverterExp = "own=自有,default=默认配置,shared=共享,exclude=排除")
    private String relationType;

    /** 是否可见：0-否 1-是 */
    @Excel(name = "是否可见", readConverterExp = "0=否,1=是")
    private String isVisible;

    /** 排序 */
    @Excel(name = "排序")
    private Integer sortOrder;

    /** 网站名称（非数据库字段，用于显示） */
    private String siteName;

    /** 原子工具名称（非数据库字段，用于显示） */
    private String toolName;

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

    public void setAtomicToolId(Long atomicToolId) 
    {
        this.atomicToolId = atomicToolId;
    }

    public Long getAtomicToolId() 
    {
        return atomicToolId;
    }

    public void setRelationType(String relationType) 
    {
        this.relationType = relationType;
    }

    public String getRelationType() 
    {
        return relationType;
    }

    public void setIsVisible(String isVisible) 
    {
        this.isVisible = isVisible;
    }

    public String getIsVisible() 
    {
        return isVisible;
    }

    public void setSortOrder(Integer sortOrder) 
    {
        this.sortOrder = sortOrder;
    }

    public Integer getSortOrder() 
    {
        return sortOrder;
    }

    public String getSiteName() 
    {
        return siteName;
    }

    public void setSiteName(String siteName) 
    {
        this.siteName = siteName;
    }

    public String getToolName() 
    {
        return toolName;
    }

    public void setToolName(String toolName) 
    {
        this.toolName = toolName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("siteId", getSiteId())
            .append("atomicToolId", getAtomicToolId())
            .append("relationType", getRelationType())
            .append("isVisible", getIsVisible())
            .append("sortOrder", getSortOrder())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
