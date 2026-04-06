package com.ruoyi.gamebox.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 工作流对象 gb_workflow
 * 
 * @author ruoyi
 * @date 2026-01-04
 */
public class GbWorkflow extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 工作流ID */
    private Long id;

    /** 工作流编码（唯一标识） */
    @Excel(name = "工作流编码")
    private String workflowCode;

    /** 工作流名称 */
    @Excel(name = "工作流名称")
    private String workflowName;

    /** 工作流描述 */
    @Excel(name = "工作流描述")
    private String description;

    /** 触发类型：manual-手动触发, scheduled-定时触发 */
    @Excel(name = "触发类型", readConverterExp = "manual=手动触发,scheduled=定时触发")
    private String triggerType;

    /** Cron表达式（triggerType=scheduled时使用） */
    private String scheduleExpression;

    /** 工作流定义（JSON格式，包含inputs和steps） */
    private String definition;

    /** 步骤数量 */
    @Excel(name = "步骤数量")
    private Integer stepCount;

    /** 是否启用：0-禁用 1-启用 */
    @Excel(name = "是否启用", readConverterExp = "0=禁用,1=启用")
    private Integer enabled;

    /** 站点ID（多站点支持） */
    @Excel(name = "站点ID")
    private Long siteId;

    /** 分类ID */
    @Excel(name = "分类ID")
    private Long categoryId;

    /** 查询模式：creator-创建者模式 related-关联模式 */
    private String queryMode;

    /** 是否包含默认配置（创建者模式使用） */
    private Boolean includeDefault;

    /** 分类名称（关联查询） */
    private String categoryName;

    /** 分类图标（关联查询） */
    private String categoryIcon;

    /** 站点名称（关联查询） */
    private String siteName;

    /** 关联来源：own-自有 default-默认配置 shared-跨站共享 */
    private String relationSource;

    /** 是否被排除（针对默认配置） */
    private Integer isExcluded;

    /** 是否可见（关联模式下的统一状态） */
    private String isVisible;

    /** 关联的网站数量 */
    private Integer relatedSitesCount;

    // getter and setter methods

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWorkflowCode() {
        return workflowCode;
    }

    public void setWorkflowCode(String workflowCode) {
        this.workflowCode = workflowCode;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(String triggerType) {
        this.triggerType = triggerType;
    }

    public String getScheduleExpression() {
        return scheduleExpression;
    }

    public void setScheduleExpression(String scheduleExpression) {
        this.scheduleExpression = scheduleExpression;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public Integer getStepCount() {
        return stepCount;
    }

    public void setStepCount(Integer stepCount) {
        this.stepCount = stepCount;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getQueryMode() {
        return queryMode;
    }

    public void setQueryMode(String queryMode) {
        this.queryMode = queryMode;
    }

    public Boolean getIncludeDefault() {
        return includeDefault;
    }

    public void setIncludeDefault(Boolean includeDefault) {
        this.includeDefault = includeDefault;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryIcon() {
        return categoryIcon;
    }

    public void setCategoryIcon(String categoryIcon) {
        this.categoryIcon = categoryIcon;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getRelationSource() {
        return relationSource;
    }

    public void setRelationSource(String relationSource) {
        this.relationSource = relationSource;
    }

    public Integer getIsExcluded() {
        return isExcluded;
    }

    public void setIsExcluded(Integer isExcluded) {
        this.isExcluded = isExcluded;
    }

    public String getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(String isVisible) {
        this.isVisible = isVisible;
    }

    public Integer getRelatedSitesCount() {
        return relatedSitesCount;
    }

    public void setRelatedSitesCount(Integer relatedSitesCount) {
        this.relatedSitesCount = relatedSitesCount;
    }
}
