package com.ruoyi.gamebox.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 多语言存储配置对象 gb_multilang_storage_configs
 * 
 * @author ruoyi
 * @date 2026-01-12
 */
public class GbMultilangStorageConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 语言代码 */
    @Excel(name = "语言代码")
    private String langCode;

    /** 语言名称 */
    @Excel(name = "语言名称")
    private String langName;

    /** 存储类型 */
    @Excel(name = "存储类型", readConverterExp = "local=本地存储,oss=对象存储,cdn=CDN存储")
    private String storageType;

    /** 存储配置(JSON格式) */
    @Excel(name = "存储配置")
    private String storageConfig;

    /** 基础路径 */
    @Excel(name = "基础路径")
    private String basePath;

    /** 是否默认配置 */
    @Excel(name = "是否默认配置", readConverterExp = "0=否,1=是")
    private String isDefault;

    /** 配置状态 */
    @Excel(name = "配置状态", readConverterExp = "0=禁用,1=启用")
    private String status;

    /** 优先级 */
    @Excel(name = "优先级")
    private Integer priority;

    /** 最后测试时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "最后测试时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date lastTestAt;

    /** 最后测试结果 */
    @Excel(name = "最后测试结果", readConverterExp = "0=失败,1=成功")
    private String lastTestResult;

    /** 测试结果消息 */
    @Excel(name = "测试结果消息")
    private String testMessage;

    /** 删除标志 */
    private String delFlag;

    /** 用于批量操作的ID数组 */
    private Long[] ids;

    // getters and setters
    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setLangCode(String langCode) 
    {
        this.langCode = langCode;
    }

    public String getLangCode() 
    {
        return langCode;
    }

    public void setLangName(String langName) 
    {
        this.langName = langName;
    }

    public String getLangName() 
    {
        return langName;
    }

    public void setStorageType(String storageType) 
    {
        this.storageType = storageType;
    }

    public String getStorageType() 
    {
        return storageType;
    }

    public void setStorageConfig(String storageConfig) 
    {
        this.storageConfig = storageConfig;
    }

    public String getStorageConfig() 
    {
        return storageConfig;
    }

    public void setBasePath(String basePath) 
    {
        this.basePath = basePath;
    }

    public String getBasePath() 
    {
        return basePath;
    }

    public void setIsDefault(String isDefault) 
    {
        this.isDefault = isDefault;
    }

    public String getIsDefault() 
    {
        return isDefault;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setPriority(Integer priority) 
    {
        this.priority = priority;
    }

    public Integer getPriority() 
    {
        return priority;
    }

    public void setLastTestAt(Date lastTestAt) 
    {
        this.lastTestAt = lastTestAt;
    }

    public Date getLastTestAt() 
    {
        return lastTestAt;
    }

    public void setLastTestResult(String lastTestResult) 
    {
        this.lastTestResult = lastTestResult;
    }

    public String getLastTestResult() 
    {
        return lastTestResult;
    }

    public void setTestMessage(String testMessage) 
    {
        this.testMessage = testMessage;
    }

    public String getTestMessage() 
    {
        return testMessage;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("langCode", getLangCode())
            .append("langName", getLangName())
            .append("storageType", getStorageType())
            .append("storageConfig", getStorageConfig())
            .append("basePath", getBasePath())
            .append("isDefault", getIsDefault())
            .append("status", getStatus())
            .append("priority", getPriority())
            .append("lastTestAt", getLastTestAt())
            .append("lastTestResult", getLastTestResult())
            .append("testMessage", getTestMessage())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }

    public Long[] getIds() {
        return ids;
    }

    public void setIds(Long[] ids) {
        this.ids = ids;
    }
}