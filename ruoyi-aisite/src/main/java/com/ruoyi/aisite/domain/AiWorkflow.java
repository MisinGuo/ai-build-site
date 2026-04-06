package com.ruoyi.aisite.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 工作流定义实体 ai_workflows
 */
public class AiWorkflow extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    /** 工作流名称 */
    private String name;
    /** 唯一编码 */
    private String code;
    private String description;
    /** 分类: build/content/seo/operation/general */
    private String category;
    /** 步骤定义 JSONB */
    private String stepsJson;
    /** 输入参数 Schema JSONB */
    private String paramsSchema;
    /** 是否内置 */
    private String isBuiltin;
    /** 是否启用 */
    private String isEnabled;
    private Integer version;
    private Long creatorId;
    private String delFlag;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getStepsJson() { return stepsJson; }
    public void setStepsJson(String stepsJson) { this.stepsJson = stepsJson; }
    public String getParamsSchema() { return paramsSchema; }
    public void setParamsSchema(String paramsSchema) { this.paramsSchema = paramsSchema; }
    public String getIsBuiltin() { return isBuiltin; }
    public void setIsBuiltin(String isBuiltin) { this.isBuiltin = isBuiltin; }
    public String getIsEnabled() { return isEnabled; }
    public void setIsEnabled(String isEnabled) { this.isEnabled = isEnabled; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    public Long getCreatorId() { return creatorId; }
    public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
}
