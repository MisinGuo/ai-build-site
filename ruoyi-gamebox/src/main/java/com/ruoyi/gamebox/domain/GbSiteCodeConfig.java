package com.ruoyi.gamebox.domain;

import java.util.Date;

/**
 * 网站代码管理配置 gb_site_code_config
 * 与 gb_sites 1:1 关联，存放 Git 仓库 + 部署相关配置
 */
public class GbSiteCodeConfig {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 关联网站ID（唯一） */
    private Long siteId;

    /** Git提供商：github/gitlab/gitee */
    private String gitProvider;

    /** Git仓库URL */
    private String gitRepoUrl;

    /** Git分支名称 */
    private String gitBranch;

    /** 是否自动同步：0-否 1-是 */
    private String gitAutoSync;

    /** 关联Git平台账号ID */
    private Long gitAccountId;

    /** 关联CF平台账号ID */
    private Long cfAccountId;

    /** 部署平台：local/cloudflare-pages/cloudflare-workers */
    private String deployPlatform;

    /** Cloudflare项目名称 */
    private String cloudflareProjectName;

    /** Cloudflare项目ID */
    private String cloudflareProjectId;

    /** 自定义域名列表（JSON数组） */
    private String customDomains;

    /** 部署状态：not_deployed/deploying/deployed/failed */
    private String deployStatus;

    /** 部署访问URL */
    private String deployUrl;

    /** 最后操作时间 */
    private Date lastDeployTime;

    /** 最后操作日志 */
    private String lastDeployLog;

    /** 部署配置（JSON）：构建命令、环境变量等 */
    private String deployConfig;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;

    // ===== Getters & Setters =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSiteId() { return siteId; }
    public void setSiteId(Long siteId) { this.siteId = siteId; }

    public String getGitProvider() { return gitProvider; }
    public void setGitProvider(String gitProvider) { this.gitProvider = gitProvider; }

    public String getGitRepoUrl() { return gitRepoUrl; }
    public void setGitRepoUrl(String gitRepoUrl) { this.gitRepoUrl = gitRepoUrl; }

    public String getGitBranch() { return gitBranch; }
    public void setGitBranch(String gitBranch) { this.gitBranch = gitBranch; }

    public String getGitAutoSync() { return gitAutoSync; }
    public void setGitAutoSync(String gitAutoSync) { this.gitAutoSync = gitAutoSync; }

    public Long getGitAccountId() { return gitAccountId; }
    public void setGitAccountId(Long gitAccountId) { this.gitAccountId = gitAccountId; }

    public Long getCfAccountId() { return cfAccountId; }
    public void setCfAccountId(Long cfAccountId) { this.cfAccountId = cfAccountId; }

    public String getDeployPlatform() { return deployPlatform; }
    public void setDeployPlatform(String deployPlatform) { this.deployPlatform = deployPlatform; }

    public String getCloudflareProjectName() { return cloudflareProjectName; }
    public void setCloudflareProjectName(String cloudflareProjectName) { this.cloudflareProjectName = cloudflareProjectName; }

    public String getCloudflareProjectId() { return cloudflareProjectId; }
    public void setCloudflareProjectId(String cloudflareProjectId) { this.cloudflareProjectId = cloudflareProjectId; }

    public String getCustomDomains() { return customDomains; }
    public void setCustomDomains(String customDomains) { this.customDomains = customDomains; }

    public String getDeployStatus() { return deployStatus; }
    public void setDeployStatus(String deployStatus) { this.deployStatus = deployStatus; }

    public String getDeployUrl() { return deployUrl; }
    public void setDeployUrl(String deployUrl) { this.deployUrl = deployUrl; }

    public Date getLastDeployTime() { return lastDeployTime; }
    public void setLastDeployTime(Date lastDeployTime) { this.lastDeployTime = lastDeployTime; }

    public String getLastDeployLog() { return lastDeployLog; }
    public void setLastDeployLog(String lastDeployLog) { this.lastDeployLog = lastDeployLog; }

    public String getDeployConfig() { return deployConfig; }
    public void setDeployConfig(String deployConfig) { this.deployConfig = deployConfig; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
