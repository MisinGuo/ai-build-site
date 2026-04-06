package com.ruoyi.gamebox.domain;

import java.util.Date;

/**
 * 站点模板对象 gb_site_templates
 */
public class GbSiteTemplate {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 模板名称 */
    private String name;

    /** 适用站点功能类型：seo_site / landing_site */
    private String siteFunctionType;

    /** 所属行业 */
    private String industry;

    /** 模板描述 */
    private String description;

    /** 预览图 URL */
    private String previewImage;

    /** 模板 Git 仓库地址 */
    private String templateGitUrl;

    /** 模板 Git 分支 */
    private String templateGitBranch;

    /** 代码根目录（模板仓库子目录，留空表示根目录） */
    private String entryDir;

    /** 框架类型：nextjs / nuxt / react 等 */
    private String framework;

    /** 环境变量模板（JSON：key=变量名，value=默认值/占位符） */
    private String envTemplate;

    /** 配置文件映射（JSON 数组：定义哪些 TS 文件变量开放给用户编辑） */
    private String configMappings;

    /** 克隆模板仓库时使用的 Git 账号 ID（可选，留空则仅适用公开仓库） */
    private Long gitAccountId;

    /** 排序 */
    private Integer sortOrder;

    /** 状态：0-禁用 1-启用 */
    private String status;

    /** 创建者 */
    private String createBy;

    /** 创建时间 */
    private Date createTime;

    /** 更新者 */
    private String updateBy;

    /** 更新时间 */
    private Date updateTime;

    /** 备注 */
    private String remark;

    // ===== Getters & Setters =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSiteFunctionType() { return siteFunctionType; }
    public void setSiteFunctionType(String siteFunctionType) { this.siteFunctionType = siteFunctionType; }

    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPreviewImage() { return previewImage; }
    public void setPreviewImage(String previewImage) { this.previewImage = previewImage; }

    public String getTemplateGitUrl() { return templateGitUrl; }
    public void setTemplateGitUrl(String templateGitUrl) { this.templateGitUrl = templateGitUrl; }

    public String getTemplateGitBranch() { return templateGitBranch; }
    public void setTemplateGitBranch(String templateGitBranch) { this.templateGitBranch = templateGitBranch; }

    public String getEntryDir() { return entryDir; }
    public void setEntryDir(String entryDir) { this.entryDir = entryDir; }

    public String getFramework() { return framework; }
    public void setFramework(String framework) { this.framework = framework; }

    public String getEnvTemplate() { return envTemplate; }
    public void setEnvTemplate(String envTemplate) { this.envTemplate = envTemplate; }

    public String getConfigMappings() { return configMappings; }
    public void setConfigMappings(String configMappings) { this.configMappings = configMappings; }

    public Long getGitAccountId() { return gitAccountId; }
    public void setGitAccountId(Long gitAccountId) { this.gitAccountId = gitAccountId; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreateBy() { return createBy; }
    public void setCreateBy(String createBy) { this.createBy = createBy; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public String getUpdateBy() { return updateBy; }
    public void setUpdateBy(String updateBy) { this.updateBy = updateBy; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
