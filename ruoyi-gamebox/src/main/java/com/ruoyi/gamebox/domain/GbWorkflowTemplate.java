package com.ruoyi.gamebox.domain;

import java.util.Date;

/**
 * 工作流模板
 *
 * 对应表: gb_workflow_template
 */
public class GbWorkflowTemplate {

    private Long id;

    /** 模板编码（唯一） */
    private String templateCode;

    /** 模板名称 */
    private String templateName;

    /** 模板描述 */
    private String description;

    /** 分类（content/general 等） */
    private String category;

    /** 显示图标（emoji 或 icon class） */
    private String icon;

    /** 工作流定义（JSON，结构同 gb_workflow.definition） */
    private String definition;

    /** 使用次数 */
    private Integer usageCount;

    /** 是否公开：1-是 0-否 */
    private Integer isPublic;

    /** 排序 */
    private Integer sortOrder;

    private String createBy;
    private Date createTime;
    private String updateBy;
    private Date updateTime;

    // ===== getters & setters =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTemplateCode() { return templateCode; }
    public void setTemplateCode(String templateCode) { this.templateCode = templateCode; }

    public String getTemplateName() { return templateName; }
    public void setTemplateName(String templateName) { this.templateName = templateName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getDefinition() { return definition; }
    public void setDefinition(String definition) { this.definition = definition; }

    public Integer getUsageCount() { return usageCount; }
    public void setUsageCount(Integer usageCount) { this.usageCount = usageCount; }

    public Integer getIsPublic() { return isPublic; }
    public void setIsPublic(Integer isPublic) { this.isPublic = isPublic; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public String getCreateBy() { return createBy; }
    public void setCreateBy(String createBy) { this.createBy = createBy; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public String getUpdateBy() { return updateBy; }
    public void setUpdateBy(String updateBy) { this.updateBy = updateBy; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
