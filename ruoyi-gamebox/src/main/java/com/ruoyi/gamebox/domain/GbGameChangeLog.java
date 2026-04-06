package com.ruoyi.gamebox.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 游戏字段变更日志 gb_game_change_log
 * 每次导入操作对游戏数据（主表/关联表）的变更记录，支持快照对比和按需回滚。
 *
 * @author ruoyi
 */
public class GbGameChangeLog {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 所属批次ID */
    private Long batchId;

    /** 批次号 */
    private String batchNo;

    /** 游戏ID */
    private Long gameId;

    /** 游戏名称（冗余） */
    private String gameName;

    /** 变更类型：INSERT-新增 / UPDATE-更新 */
    private String changeType;

    /** 变更目标表：gb_games / gb_box_game_relations */
    private String targetTable;

    /** 目标表记录ID */
    private Long targetId;

    /** 变更前快照（JSON），INSERT 时为空 */
    private String beforeSnapshot;

    /** 变更后快照（JSON） */
    private String afterSnapshot;

    /** 是否已撤回（0-否 1-是） */
    private Integer reverted;

    /** 撤回时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date revertTime;

    /** 撤回操作人 */
    private String revertBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 创建人 */
    private String createBy;

    // -------- getters / setters --------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBatchId() { return batchId; }
    public void setBatchId(Long batchId) { this.batchId = batchId; }

    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }

    public Long getGameId() { return gameId; }
    public void setGameId(Long gameId) { this.gameId = gameId; }

    public String getGameName() { return gameName; }
    public void setGameName(String gameName) { this.gameName = gameName; }

    public String getChangeType() { return changeType; }
    public void setChangeType(String changeType) { this.changeType = changeType; }

    public String getTargetTable() { return targetTable; }
    public void setTargetTable(String targetTable) { this.targetTable = targetTable; }

    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }

    public String getBeforeSnapshot() { return beforeSnapshot; }
    public void setBeforeSnapshot(String beforeSnapshot) { this.beforeSnapshot = beforeSnapshot; }

    public String getAfterSnapshot() { return afterSnapshot; }
    public void setAfterSnapshot(String afterSnapshot) { this.afterSnapshot = afterSnapshot; }

    public Integer getReverted() { return reverted; }
    public void setReverted(Integer reverted) { this.reverted = reverted; }

    public Date getRevertTime() { return revertTime; }
    public void setRevertTime(Date revertTime) { this.revertTime = revertTime; }

    public String getRevertBy() { return revertBy; }
    public void setRevertBy(String revertBy) { this.revertBy = revertBy; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public String getCreateBy() { return createBy; }
    public void setCreateBy(String createBy) { this.createBy = createBy; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("batchId", getBatchId())
                .append("gameId", getGameId())
                .append("gameName", getGameName())
                .append("changeType", getChangeType())
                .append("targetTable", getTargetTable())
                .append("targetId", getTargetId())
                .append("reverted", getReverted())
                .toString();
    }
}
