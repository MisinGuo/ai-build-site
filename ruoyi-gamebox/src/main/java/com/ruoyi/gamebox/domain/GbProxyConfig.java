package com.ruoyi.gamebox.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 系统功能代理配置对象 gb_proxy_config
 */
public class GbProxyConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 功能标识符（唯一键，不可修改） */
    private String featureKey;

    /** 功能名称 */
    private String featureName;

    /** 功能分组 */
    private String featureGroup;

    /** 功能描述（说明该功能的外部调用情况） */
    private String featureDesc;

    /**
     * 是否支持代理配置：
     * 0 - 不支持（本地纯数据库操作，无外部网络调用）
     * 1 - 支持（有外部网络调用，可配置代理）
     */
    private Integer proxyApplicable;

    /** 代理使用注意事项（不建议使用代理或有特殊说明时填写） */
    private String proxyWarning;

    /** 是否启用代理：0-不启用，1-启用（仅 proxyApplicable=1 时生效） */
    private Integer proxyEnabled;

    /** 代理协议类型：http / https / socks5 */
    private String proxyType;

    /** 代理服务器地址（IP 或域名） */
    private String proxyHost;

    /** 代理服务器端口 */
    private Integer proxyPort;

    /** 代理认证用户名（不需要认证则留空） */
    private String proxyUsername;

    /** 代理认证密码（不需要认证则留空） */
    private String proxyPassword;

    /** 排序权重 */
    private Integer sortOrder;

    // ---- Getters & Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFeatureKey() { return featureKey; }
    public void setFeatureKey(String featureKey) { this.featureKey = featureKey; }

    public String getFeatureName() { return featureName; }
    public void setFeatureName(String featureName) { this.featureName = featureName; }

    public String getFeatureGroup() { return featureGroup; }
    public void setFeatureGroup(String featureGroup) { this.featureGroup = featureGroup; }

    public String getFeatureDesc() { return featureDesc; }
    public void setFeatureDesc(String featureDesc) { this.featureDesc = featureDesc; }

    public Integer getProxyApplicable() { return proxyApplicable; }
    public void setProxyApplicable(Integer proxyApplicable) { this.proxyApplicable = proxyApplicable; }

    public String getProxyWarning() { return proxyWarning; }
    public void setProxyWarning(String proxyWarning) { this.proxyWarning = proxyWarning; }

    public Integer getProxyEnabled() { return proxyEnabled; }
    public void setProxyEnabled(Integer proxyEnabled) { this.proxyEnabled = proxyEnabled; }

    public String getProxyType() { return proxyType; }
    public void setProxyType(String proxyType) { this.proxyType = proxyType; }

    public String getProxyHost() { return proxyHost; }
    public void setProxyHost(String proxyHost) { this.proxyHost = proxyHost; }

    public Integer getProxyPort() { return proxyPort; }
    public void setProxyPort(Integer proxyPort) { this.proxyPort = proxyPort; }

    public String getProxyUsername() { return proxyUsername; }
    public void setProxyUsername(String proxyUsername) { this.proxyUsername = proxyUsername; }

    public String getProxyPassword() { return proxyPassword; }
    public void setProxyPassword(String proxyPassword) { this.proxyPassword = proxyPassword; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("featureKey", getFeatureKey())
            .append("featureName", getFeatureName())
            .append("featureGroup", getFeatureGroup())
            .append("proxyApplicable", getProxyApplicable())
            .append("proxyEnabled", getProxyEnabled())
            .append("proxyType", getProxyType())
            .append("proxyHost", getProxyHost())
            .append("proxyPort", getProxyPort())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
