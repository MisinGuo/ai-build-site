package com.ruoyi.aisite.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * AI 运营任务实体 ai_operation_tasks
 */
public class AiOperationTask extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    /** 关联站点 ID (null 表示全局任务) */
    private Long siteId;
    /** 关联矩阵组 ID */
    private Long matrixGroupId;
    /** 任务类型: content_gen/seo_optimize/interlink/deploy/dead_link_scan */
    private String taskType;
    /** 任务名称 */
    private String taskName;
    /** 触发方式: manual/scheduled/event */
    private String triggerType;
    /** Cron 表达式（定时触发时有效） */
    private String cronExpr;
    /** 任务参数 JSONB */
    private String taskParams;
    /** 优先级 1-10 */
    private Integer priority;
    /** 状态: pending/running/success/failed/cancelled */
    private String status;
    /** 上次执行时间 */
    private java.util.Date lastRunAt;
    /** 下次执行时间 */
    private java.util.Date nextRunAt;
    /** 上次执行结果摘要 */
    private String lastResult;
    /** 已重试次数 */
    private Integer retryCount;
    /** 最大重试次数 */
    private Integer maxRetries;
    /** 创建者 ID */
    private Long creatorId;
    /** 部门 ID */
    private Long deptId;
    private String delFlag;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSiteId() { return siteId; }
    public void setSiteId(Long siteId) { this.siteId = siteId; }
    public Long getMatrixGroupId() { return matrixGroupId; }
    public void setMatrixGroupId(Long matrixGroupId) { this.matrixGroupId = matrixGroupId; }
    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    public String getTriggerType() { return triggerType; }
    public void setTriggerType(String triggerType) { this.triggerType = triggerType; }
    public String getCronExpr() { return cronExpr; }
    public void setCronExpr(String cronExpr) { this.cronExpr = cronExpr; }
    public String getTaskParams() { return taskParams; }
    public void setTaskParams(String taskParams) { this.taskParams = taskParams; }
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public java.util.Date getLastRunAt() { return lastRunAt; }
    public void setLastRunAt(java.util.Date lastRunAt) { this.lastRunAt = lastRunAt; }
    public java.util.Date getNextRunAt() { return nextRunAt; }
    public void setNextRunAt(java.util.Date nextRunAt) { this.nextRunAt = nextRunAt; }
    public String getLastResult() { return lastResult; }
    public void setLastResult(String lastResult) { this.lastResult = lastResult; }
    public Integer getRetryCount() { return retryCount; }
    public void setRetryCount(Integer retryCount) { this.retryCount = retryCount; }
    public Integer getMaxRetries() { return maxRetries; }
    public void setMaxRetries(Integer maxRetries) { this.maxRetries = maxRetries; }
    public Long getCreatorId() { return creatorId; }
    public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }
    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
}
