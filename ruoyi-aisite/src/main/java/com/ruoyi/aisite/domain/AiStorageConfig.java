package com.ruoyi.aisite.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 对象存储配置实体 ai_storage_configs
 */
public class AiStorageConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    /** 关联站点ID（可为空，为空则为全局配置） */
    private Long siteId;
    /** 配置名称 */
    private String name;
    /** 配置编码 */
    private String code;
    /** 存储类型: s3/r2/oss/cos/minio */
    private String storageType;
    /** 是否默认 */
    private String isDefault;
    /** 配置详情 JSONB */
    private String configJson;
    /** CDN 地址 */
    private String cdnUrl;
    private String status;
    private String delFlag;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSiteId() { return siteId; }
    public void setSiteId(Long siteId) { this.siteId = siteId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getStorageType() { return storageType; }
    public void setStorageType(String storageType) { this.storageType = storageType; }
    public String getIsDefault() { return isDefault; }
    public void setIsDefault(String isDefault) { this.isDefault = isDefault; }
    public String getConfigJson() { return configJson; }
    public void setConfigJson(String configJson) { this.configJson = configJson; }
    public String getCdnUrl() { return cdnUrl; }
    public void setCdnUrl(String cdnUrl) { this.cdnUrl = cdnUrl; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
}
