package com.ruoyi.aisite.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * Prompt 模板实体 ai_prompt_templates
 */
public class AiPromptTemplate extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    /** 模板名称 */
    private String name;
    /** 模板编码 */
    private String code;
    /** 应用场景 */
    private String scene;
    /** 系统提示词 */
    private String systemPrompt;
    /** 用户提示词模板 */
    private String userPrompt;
    /** 变量定义 JSONB */
    private String variables;
    /** 适用平台类型 */
    private String platformType;
    /** 是否内置 */
    private String isBuiltin;
    private String status;
    private String delFlag;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getScene() { return scene; }
    public void setScene(String scene) { this.scene = scene; }
    public String getSystemPrompt() { return systemPrompt; }
    public void setSystemPrompt(String systemPrompt) { this.systemPrompt = systemPrompt; }
    public String getUserPrompt() { return userPrompt; }
    public void setUserPrompt(String userPrompt) { this.userPrompt = userPrompt; }
    public String getVariables() { return variables; }
    public void setVariables(String variables) { this.variables = variables; }
    public String getPlatformType() { return platformType; }
    public void setPlatformType(String platformType) { this.platformType = platformType; }
    public String getIsBuiltin() { return isBuiltin; }
    public void setIsBuiltin(String isBuiltin) { this.isBuiltin = isBuiltin; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
}
