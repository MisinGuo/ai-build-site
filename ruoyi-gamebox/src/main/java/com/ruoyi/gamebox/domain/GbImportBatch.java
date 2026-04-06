package com.ruoyi.gamebox.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 游戏导入批次记录 gb_import_batch
 *
 * @author ruoyi
 */
public class GbImportBatch {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 批次号（UUID） */
    private String batchNo;

    /** 关联盒子ID */
    private Long boxId;

    /** 盒子名称（冗余） */
    private String boxName;

    /** 关联网站ID */
    private Long siteId;

    /** 网站名称（冗余） */
    private String siteName;

    /** 来源平台类型 */
    private String platformType;

    /** 处理总条数 */
    private Integer totalCount;

    /** 新增条数 */
    private Integer newCount;

    /** 更新条数 */
    private Integer updatedCount;

    /** 跳过条数 */
    private Integer skippedCount;

    /** 失败条数 */
    private Integer failedCount;

    /** 批次状态：completed / partial_failed */
    private String status;

    /** 批次摘要（含错误列表JSON） */
    private String summary;

    /** 查询条件：按游戏名称模糊筛选（基于变更日志） */
    private String gameName;

    /** 查询条件：批次号列表（逗号分隔） */
    private String batchNos;

    /** 是否已全部撤回（0-否 1-是） */
    private Integer reverted;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 创建人 */
    private String createBy;

    // -------- getters / setters --------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }

    public Long getBoxId() { return boxId; }
    public void setBoxId(Long boxId) { this.boxId = boxId; }

    public String getBoxName() { return boxName; }
    public void setBoxName(String boxName) { this.boxName = boxName; }

    public Long getSiteId() { return siteId; }
    public void setSiteId(Long siteId) { this.siteId = siteId; }

    public String getSiteName() { return siteName; }
    public void setSiteName(String siteName) { this.siteName = siteName; }

    public String getPlatformType() { return platformType; }
    public void setPlatformType(String platformType) { this.platformType = platformType; }

    public Integer getTotalCount() { return totalCount; }
    public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }

    public Integer getNewCount() { return newCount; }
    public void setNewCount(Integer newCount) { this.newCount = newCount; }

    public Integer getUpdatedCount() { return updatedCount; }
    public void setUpdatedCount(Integer updatedCount) { this.updatedCount = updatedCount; }

    public Integer getSkippedCount() { return skippedCount; }
    public void setSkippedCount(Integer skippedCount) { this.skippedCount = skippedCount; }

    public Integer getFailedCount() { return failedCount; }
    public void setFailedCount(Integer failedCount) { this.failedCount = failedCount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getGameName() { return gameName; }
    public void setGameName(String gameName) { this.gameName = gameName; }

    public String getBatchNos() { return batchNos; }
    public void setBatchNos(String batchNos) { this.batchNos = batchNos; }

    public Integer getReverted() { return reverted; }
    public void setReverted(Integer reverted) { this.reverted = reverted; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public String getCreateBy() { return createBy; }
    public void setCreateBy(String createBy) { this.createBy = createBy; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("batchNo", getBatchNo())
                .append("boxId", getBoxId())
                .append("siteId", getSiteId())
                .append("totalCount", getTotalCount())
                .append("newCount", getNewCount())
                .append("updatedCount", getUpdatedCount())
                .append("status", getStatus())
                .append("createTime", getCreateTime())
                .toString();
    }
}
