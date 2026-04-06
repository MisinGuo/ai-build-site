package com.ruoyi.gamebox.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 原子工具对象 gb_atomic_tool
 * 
 * @author ruoyi
 * @date 2026-01-04
 */
public class GbAtomicTool extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 工具ID */
    private Long id;

    /** 工具编码（唯一标识） */
    @Excel(name = "工具编码")
    private String toolCode;

    /** 工具名称 */
    @Excel(name = "工具名称")
    private String toolName;

    /** 工具类型：ai-AI生成工具, api-外部API工具, builtin-内置工具 */
    @Excel(name = "工具类型")
    private String toolType;

    /** 工具描述 */
    @Excel(name = "工具描述")
    private String description;

    /** 工具配置（JSON格式） */
    private String config;

    /** 输入参数定义（JSON数组） */
    private String inputs;

    /** 输出参数定义（JSON数组） */
    private String outputs;

    /** 是否启用：0-禁用 1-启用 */
    @Excel(name = "是否启用", readConverterExp = "0=禁用,1=启用")
    private Integer enabled;

    /** 站点ID（多站点支持） */
    @Excel(name = "站点ID")
    private Long siteId;

    /** 分类ID */
    @Excel(name = "分类ID")
    private Long categoryId;

    /** 分类名称（查询时关联） */
    private String categoryName;

    /** 分类图标（查询时关联） */
    private String categoryIcon;

    /** 来源类型（关联模式下使用：own-自有, default-默认配置, shared-跨站共享） */
    private String relationSource;

    /** 是否可见（关联模式下使用：0-隐藏 1-显示） */
    private String isVisible;

    /** 关联的网站数量 */
    private Integer relatedSitesCount;

    /** 排除的网站数量（默认配置专用） */
    private Integer excludedSitesCount;

    /** 是否被当前网站排除（关联模式下使用：0-未排除 1-已排除） */
    private Integer isExcluded;

    /** 查询模式（查询参数：creator-创建者模式, related-关联模式） */
    private String queryMode;

    /** 是否包含默认配置（查询参数：创建者模式下使用） */
    private Boolean includeDefault;

    // getter and setter methods

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToolCode() {
        return toolCode;
    }

    public void setToolCode(String toolCode) {
        this.toolCode = toolCode;
    }

    public String getToolName() {
        return toolName;
    }

    public void setToolName(String toolName) {
        this.toolName = toolName;
    }

    public String getToolType() {
        return toolType;
    }

    public void setToolType(String toolType) {
        this.toolType = toolType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getInputs() {
        return inputs;
    }

    public void setInputs(String inputs) {
        this.inputs = inputs;
    }

    public String getOutputs() {
        return outputs;
    }

    public void setOutputs(String outputs) {
        this.outputs = outputs;
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

    public String getRelationSource() {
        return relationSource;
    }

    public void setRelationSource(String relationSource) {
        this.relationSource = relationSource;
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

    public Integer getExcludedSitesCount() {
        return excludedSitesCount;
    }

    public void setExcludedSitesCount(Integer excludedSitesCount) {
        this.excludedSitesCount = excludedSitesCount;
    }

    public Integer getIsExcluded() {
        return isExcluded;
    }

    public void setIsExcluded(Integer isExcluded) {
        this.isExcluded = isExcluded;
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
}
