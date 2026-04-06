package com.ruoyi.gamebox.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 存储配置对象 gb_storage_configs
 * 
 * @author ruoyi
 */
public class GbStorageConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 配置ID */
    private Long id;

    /** 站点ID */
    @Excel(name = "站点ID")
    private Long siteId;

    /** 配置名称 */
    @Excel(name = "配置名称")
    private String name;

    /** 配置代码 */
    @Excel(name = "配置代码")
    private String code;

    /** 存储类型：local-本地存储, aliyun_oss-阿里云OSS, tencent_cos-腾讯COS, qiniu-七牛云, s3-AWS S3, github-GitHub仓库 */
    @Excel(name = "存储类型", readConverterExp = "local=本地存储,aliyun_oss=阿里云OSS,tencent_cos=腾讯COS,qiniu=七牛云,s3=AWS S3,github=GitHub仓库")
    private String storageType;

    /** 存储用途：article-文章 resource-资源 mixed-混合 */
    @Excel(name = "存储用途")
    private String storagePurpose;

    /** GitHub Token */
    private String githubToken;

    /** GitHub 仓库所有者（用户名或组织名） */
    @Excel(name = "GitHub所有者")
    private String githubOwner;

    /** GitHub 仓库名称 */
    @Excel(name = "GitHub仓库")
    private String githubRepo;

    /** GitHub 分支名称 */
    @Excel(name = "GitHub分支")
    private String githubBranch;

    /** GitHub 路径前缀 */
    @Excel(name = "GitHub路径")
    private String githubPathPrefix;

    /** MinIO 配置 */
    @Excel(name = "MinIO端点")
    private String minioEndpoint;
    @Excel(name = "MinIO端口")
    private Integer minioPort;
    @Excel(name = "MinIO AccessKey")
    private String minioAccessKey;
    @Excel(name = "MinIO SecretKey")
    private String minioSecretKey;
    @Excel(name = "MinIO Bucket")
    private String minioBucket;
    @Excel(name = "MinIO Region")
    private String minioRegion;

    /** R2 / S3 配置 */
    @Excel(name = "R2账户ID")
    private String r2AccountId;
    @Excel(name = "R2 AccessKey")
    private String r2AccessKey;
    @Excel(name = "R2 SecretKey")
    private String r2SecretKey;
    @Excel(name = "R2 Bucket")
    private String r2Bucket;
    @Excel(name = "R2公开URL")
    private String r2PublicUrl;

    /** 阿里云 OSS 配置 */
    @Excel(name = "OSS端点")
    private String ossEndpoint;
    @Excel(name = "OSS AccessKey")
    private String ossAccessKey;
    @Excel(name = "OSS SecretKey")
    private String ossSecretKey;
    @Excel(name = "OSS Bucket")
    private String ossBucket;
    @Excel(name = "OSS Region")
    private String ossRegion;

    /** 腾讯云 COS 配置 */
    @Excel(name = "COS SecretId")
    private String cosSecretId;
    @Excel(name = "COS SecretKey")
    private String cosSecretKey;
    @Excel(name = "COS Bucket")
    private String cosBucket;
    @Excel(name = "COS Region")
    private String cosRegion;

    /** 基础路径前缀 */
    @Excel(name = "基础路径")
    private String basePath;

    /** 自定义访问域名 */
    @Excel(name = "自定义域名")
    private String customDomain;

    /** 最大文件大小（字节） */
    @Excel(name = "最大文件大小")
    private Long maxFileSize;

    /** 允许的文件扩展名 */
    @Excel(name = "允许的扩展名")
    private String allowedExtensions;

    /** 是否默认配置 */
    @Excel(name = "是否默认", readConverterExp = "0=否,1=是")
    private String isDefault;

    /** 优先级 */
    @Excel(name = "优先级")
    private Integer priority;

    /** 状态：0禁用 1启用 */
    @Excel(name = "状态", readConverterExp = "0=禁用,1=启用")
    private String status;

    /** 删除标志：0存在 2删除 */
    private String delFlag;

    /** 分类ID */
    private Long categoryId;

    /** 分类名称（关联查询字段） */
    private String categoryName;

    /** 分类图标（关联查询字段） */
    private String categoryIcon;

    /** 配置描述 */
    @Excel(name = "配置描述")
    private String description;

    /** 查询模式：creator-创建者, related-关联网站（仅用于查询，不存储） */
    private String queryMode;

    /** 是否包含默认配置（仅用于查询，不存储） */
    private Boolean includeDefault;

    /** 关联网站数量（仅用于查询，不存储） */
    private Integer relatedSitesCount;

    /** 排除网站数量（仅对默认配置有效，仅用于查询，不存储） */
    private Integer excludedSitesCount;

    /** 是否可见（仅用于查询，不存储） */
    private String isVisible;

    /** 是否被排除（仅用于查询，不存储） */
    private Integer isExcluded;

    /** 关联来源：own-自己的, default-默认配置, shared-其他网站分享（仅用于查询，不存储） */
    private String relationSource;

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
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setCode(String code) 
    {
        this.code = code;
    }

    public String getCode() 
    {
        return code;
    }
    public void setStorageType(String storageType) 
    {
        this.storageType = storageType;
    }

    public String getStorageType() 
    {
        return storageType;
    }
    public void setStoragePurpose(String storagePurpose) 
    {
        this.storagePurpose = storagePurpose;
    }

    public String getStoragePurpose() 
    {
        return storagePurpose;
    }
    public void setGithubToken(String githubToken) 
    {
        this.githubToken = githubToken;
    }

    public String getGithubToken() 
    {
        return githubToken;
    }
    public void setGithubOwner(String githubOwner) 
    {
        this.githubOwner = githubOwner;
    }

    public String getGithubOwner() 
    {
        return githubOwner;
    }
    public void setGithubRepo(String githubRepo) 
    {
        this.githubRepo = githubRepo;
    }

    public String getGithubRepo() 
    {
        return githubRepo;
    }
    public void setGithubBranch(String githubBranch) 
    {
        this.githubBranch = githubBranch;
    }

    public String getGithubBranch() 
    {
        return githubBranch;
    }
    public void setGithubPathPrefix(String githubPathPrefix) 
    {
        this.githubPathPrefix = githubPathPrefix;
    }

    public String getGithubPathPrefix() 
    {
        return githubPathPrefix;
    }

    // MinIO getter/setter
    public void setMinioEndpoint(String minioEndpoint) { this.minioEndpoint = minioEndpoint; }
    public String getMinioEndpoint() { return minioEndpoint; }
    public void setMinioPort(Integer minioPort) { this.minioPort = minioPort; }
    public Integer getMinioPort() { return minioPort; }
    public void setMinioAccessKey(String minioAccessKey) { this.minioAccessKey = minioAccessKey; }
    public String getMinioAccessKey() { return minioAccessKey; }
    public void setMinioSecretKey(String minioSecretKey) { this.minioSecretKey = minioSecretKey; }
    public String getMinioSecretKey() { return minioSecretKey; }
    public void setMinioBucket(String minioBucket) { this.minioBucket = minioBucket; }
    public String getMinioBucket() { return minioBucket; }
    public void setMinioRegion(String minioRegion) { this.minioRegion = minioRegion; }
    public String getMinioRegion() { return minioRegion; }

    // R2/S3 getter/setter
    public void setR2AccountId(String r2AccountId) { this.r2AccountId = r2AccountId; }
    public String getR2AccountId() { return r2AccountId; }
    public void setR2AccessKey(String r2AccessKey) { this.r2AccessKey = r2AccessKey; }
    public String getR2AccessKey() { return r2AccessKey; }
    public void setR2SecretKey(String r2SecretKey) { this.r2SecretKey = r2SecretKey; }
    public String getR2SecretKey() { return r2SecretKey; }
    public void setR2Bucket(String r2Bucket) { this.r2Bucket = r2Bucket; }
    public String getR2Bucket() { return r2Bucket; }
    public void setR2PublicUrl(String r2PublicUrl) { this.r2PublicUrl = r2PublicUrl; }
    public String getR2PublicUrl() { return r2PublicUrl; }

    // OSS getter/setter
    public void setOssEndpoint(String ossEndpoint) { this.ossEndpoint = ossEndpoint; }
    public String getOssEndpoint() { return ossEndpoint; }
    public void setOssAccessKey(String ossAccessKey) { this.ossAccessKey = ossAccessKey; }
    public String getOssAccessKey() { return ossAccessKey; }
    public void setOssSecretKey(String ossSecretKey) { this.ossSecretKey = ossSecretKey; }
    public String getOssSecretKey() { return ossSecretKey; }
    public void setOssBucket(String ossBucket) { this.ossBucket = ossBucket; }
    public String getOssBucket() { return ossBucket; }
    public void setOssRegion(String ossRegion) { this.ossRegion = ossRegion; }
    public String getOssRegion() { return ossRegion; }

    // COS getter/setter
    public void setCosSecretId(String cosSecretId) { this.cosSecretId = cosSecretId; }
    public String getCosSecretId() { return cosSecretId; }
    public void setCosSecretKey(String cosSecretKey) { this.cosSecretKey = cosSecretKey; }
    public String getCosSecretKey() { return cosSecretKey; }
    public void setCosBucket(String cosBucket) { this.cosBucket = cosBucket; }
    public String getCosBucket() { return cosBucket; }
    public void setCosRegion(String cosRegion) { this.cosRegion = cosRegion; }
    public String getCosRegion() { return cosRegion; }

    public void setBasePath(String basePath) 
    {
        this.basePath = basePath;
    }

    public String getBasePath() 
    {
        return basePath;
    }
    public void setCustomDomain(String customDomain) 
    {
        this.customDomain = customDomain;
    }

    public String getCustomDomain() 
    {
        return customDomain;
    }
    public void setMaxFileSize(Long maxFileSize) 
    {
        this.maxFileSize = maxFileSize;
    }

    public Long getMaxFileSize() 
    {
        return maxFileSize;
    }
    public void setAllowedExtensions(String allowedExtensions) 
    {
        this.allowedExtensions = allowedExtensions;
    }

    public String getAllowedExtensions() 
    {
        return allowedExtensions;
    }
    public void setIsDefault(String isDefault) 
    {
        this.isDefault = isDefault;
    }

    public String getIsDefault() 
    {
        return isDefault;
    }
    public void setPriority(Integer priority) 
    {
        this.priority = priority;
    }

    public Integer getPriority() 
    {
        return priority;
    }
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }
    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    public void setCategoryId(Long categoryId)
    {
        this.categoryId = categoryId;
    }

    public Long getCategoryId()
    {
        return categoryId;
    }

    public void setCategoryName(String categoryName)
    {
        this.categoryName = categoryName;
    }

    public String getCategoryName()
    {
        return categoryName;
    }

    public void setCategoryIcon(String categoryIcon)
    {
        this.categoryIcon = categoryIcon;
    }

    public String getCategoryIcon()
    {
        return categoryIcon;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }

    public void setQueryMode(String queryMode)
    {
        this.queryMode = queryMode;
    }

    public String getQueryMode()
    {
        return queryMode;
    }

    public void setIncludeDefault(Boolean includeDefault)
    {
        this.includeDefault = includeDefault;
    }

    public Boolean getIncludeDefault()
    {
        return includeDefault;
    }

    public void setRelatedSitesCount(Integer relatedSitesCount)
    {
        this.relatedSitesCount = relatedSitesCount;
    }

    public Integer getRelatedSitesCount()
    {
        return relatedSitesCount;
    }

    public void setExcludedSitesCount(Integer excludedSitesCount)
    {
        this.excludedSitesCount = excludedSitesCount;
    }

    public Integer getExcludedSitesCount()
    {
        return excludedSitesCount;
    }

    public void setIsVisible(String isVisible)
    {
        this.isVisible = isVisible;
    }

    public String getIsVisible()
    {
        return isVisible;
    }

    public void setIsExcluded(Integer isExcluded)
    {
        this.isExcluded = isExcluded;
    }

    public Integer getIsExcluded()
    {
        return isExcluded;
    }

    public void setRelationSource(String relationSource)
    {
        this.relationSource = relationSource;
    }

    public String getRelationSource()
    {
        return relationSource;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("siteId", getSiteId())
            .append("name", getName())
            .append("code", getCode())
            .append("storageType", getStorageType())
            .append("storagePurpose", getStoragePurpose())
            .append("githubToken", getGithubToken())
            .append("githubOwner", getGithubOwner())
            .append("githubRepo", getGithubRepo())
            .append("githubBranch", getGithubBranch())
            .append("githubPathPrefix", getGithubPathPrefix())
            .append("basePath", getBasePath())
            .append("customDomain", getCustomDomain())
            .append("maxFileSize", getMaxFileSize())
            .append("allowedExtensions", getAllowedExtensions())
            .append("isDefault", getIsDefault())
            .append("priority", getPriority())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("description", getDescription())
            .toString();
    }
}
