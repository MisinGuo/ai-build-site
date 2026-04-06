package com.ruoyi.gamebox.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 短剧供应商对象 gb_drama_vendors
 * 
 * @author ruoyi
 */
public class GbDramaVendor extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 供应商ID */
    private Long id;

    /** 供应商名称 */
    @Excel(name = "供应商名称")
    private String name;

    /** 供应商代码 */
    @Excel(name = "供应商代码")
    private String code;

    /** 联系人 */
    @Excel(name = "联系人")
    private String contactPerson;

    /** 联系电话 */
    @Excel(name = "联系电话")
    private String contactPhone;

    /** 联系邮箱 */
    @Excel(name = "联系邮箱")
    private String contactEmail;

    /** 分成比例(百分比) */
    @Excel(name = "分成比例(%)")
    private Integer shareRatio;

    /** 结算周期(天) */
    @Excel(name = "结算周期(天)")
    private Integer settlementCycle;

    /** API接口地址 */
    @Excel(name = "API接口地址")
    private String apiUrl;

    /** API密钥 */
    private String apiKey;

    /** API密钥(备用) */
    private String apiSecret;

    /** 排序号 */
    @Excel(name = "排序号")
    private Integer sortOrder;

    /** 状态：0禁用 1启用 */
    @Excel(name = "状态", readConverterExp = "0=禁用,1=启用")
    private String status;

    /** 删除标志：0存在 2删除 */
    private String delFlag;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
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
    public void setContactPerson(String contactPerson) 
    {
        this.contactPerson = contactPerson;
    }

    public String getContactPerson() 
    {
        return contactPerson;
    }
    public void setContactPhone(String contactPhone) 
    {
        this.contactPhone = contactPhone;
    }

    public String getContactPhone() 
    {
        return contactPhone;
    }
    public void setContactEmail(String contactEmail) 
    {
        this.contactEmail = contactEmail;
    }

    public String getContactEmail() 
    {
        return contactEmail;
    }
    public void setShareRatio(Integer shareRatio) 
    {
        this.shareRatio = shareRatio;
    }

    public Integer getShareRatio() 
    {
        return shareRatio;
    }
    public void setSettlementCycle(Integer settlementCycle) 
    {
        this.settlementCycle = settlementCycle;
    }

    public Integer getSettlementCycle() 
    {
        return settlementCycle;
    }
    public void setApiUrl(String apiUrl) 
    {
        this.apiUrl = apiUrl;
    }

    public String getApiUrl() 
    {
        return apiUrl;
    }
    public void setApiKey(String apiKey) 
    {
        this.apiKey = apiKey;
    }

    public String getApiKey() 
    {
        return apiKey;
    }
    public void setApiSecret(String apiSecret) 
    {
        this.apiSecret = apiSecret;
    }

    public String getApiSecret() 
    {
        return apiSecret;
    }
    public void setSortOrder(Integer sortOrder) 
    {
        this.sortOrder = sortOrder;
    }

    public Integer getSortOrder() 
    {
        return sortOrder;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("code", getCode())
            .append("contactPerson", getContactPerson())
            .append("contactPhone", getContactPhone())
            .append("contactEmail", getContactEmail())
            .append("shareRatio", getShareRatio())
            .append("settlementCycle", getSettlementCycle())
            .append("apiUrl", getApiUrl())
            .append("apiKey", getApiKey())
            .append("apiSecret", getApiSecret())
            .append("sortOrder", getSortOrder())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
