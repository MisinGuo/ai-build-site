package com.ruoyi.aisite.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 原子工具实体 ai_atomic_tools
 */
public class AiAtomicTool extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    /** 工具唯一标识 */
    private String toolCode;
    /** 工具名称 */
    private String toolName;
    /** 分类: content/seo/deploy/data/ai/system/general */
    private String category;
    private String description;
    /** 输入参数 Schema JSONB */
    private String inputSchema;
    /** 输出参数 Schema JSONB */
    private String outputSchema;
    /** 是否内置 */
    private String isBuiltin;
    /** 是否启用 */
    private String isEnabled;
    private Integer sortOrder;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getToolCode() { return toolCode; }
    public void setToolCode(String toolCode) { this.toolCode = toolCode; }
    public String getToolName() { return toolName; }
    public void setToolName(String toolName) { this.toolName = toolName; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getInputSchema() { return inputSchema; }
    public void setInputSchema(String inputSchema) { this.inputSchema = inputSchema; }
    public String getOutputSchema() { return outputSchema; }
    public void setOutputSchema(String outputSchema) { this.outputSchema = outputSchema; }
    public String getIsBuiltin() { return isBuiltin; }
    public void setIsBuiltin(String isBuiltin) { this.isBuiltin = isBuiltin; }
    public String getIsEnabled() { return isEnabled; }
    public void setIsEnabled(String isEnabled) { this.isEnabled = isEnabled; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
}
