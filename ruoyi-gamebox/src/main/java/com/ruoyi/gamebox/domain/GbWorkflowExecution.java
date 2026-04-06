package com.ruoyi.gamebox.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 工作流执行记录 gb_workflow_execution
 *
 * @date 2026-02-20
 */
public class GbWorkflowExecution
{
    private static final long serialVersionUID = 1L;

    /** 执行记录ID */
    private Long id;

    /** 执行ID（唯一标识） */
    private String executionId;

    /** 工作流编码 */
    private String workflowCode;

    /** 工作流名称（冗余便于展示） */
    private String workflowName;

    /** 执行模式：sync-同步 async-异步 batch-批量 */
    private String mode;

    /** 执行状态：pending running success failed cancelled */
    private String status;

    /** 输入数据（JSON） */
    private String inputData;

    /** 输出数据（JSON） */
    private String outputData;

    /** 错误信息 */
    private String error;

    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /** 结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /** 执行耗时（毫秒） */
    private Long duration;

    /** 站点ID */
    private Long siteId;

    /** 创建者 */
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    // ---- 查询扩展字段 ----
    /** 查询起始时间 */
    private String beginTime;

    /** 查询结束时间 */
    private String endTimeStr;

    /** 页码 */
    private Integer pageNum;

    /** 每页条数 */
    private Integer pageSize;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getExecutionId() { return executionId; }
    public void setExecutionId(String executionId) { this.executionId = executionId; }

    public String getWorkflowCode() { return workflowCode; }
    public void setWorkflowCode(String workflowCode) { this.workflowCode = workflowCode; }

    public String getWorkflowName() { return workflowName; }
    public void setWorkflowName(String workflowName) { this.workflowName = workflowName; }

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getInputData() { return inputData; }
    public void setInputData(String inputData) { this.inputData = inputData; }

    public String getOutputData() { return outputData; }
    public void setOutputData(String outputData) { this.outputData = outputData; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }

    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }

    public Long getDuration() { return duration; }
    public void setDuration(Long duration) { this.duration = duration; }

    public Long getSiteId() { return siteId; }
    public void setSiteId(Long siteId) { this.siteId = siteId; }

    public String getCreateBy() { return createBy; }
    public void setCreateBy(String createBy) { this.createBy = createBy; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public String getBeginTime() { return beginTime; }
    public void setBeginTime(String beginTime) { this.beginTime = beginTime; }

    public String getEndTimeStr() { return endTimeStr; }
    public void setEndTimeStr(String endTimeStr) { this.endTimeStr = endTimeStr; }

    public Integer getPageNum() { return pageNum; }
    public void setPageNum(Integer pageNum) { this.pageNum = pageNum; }

    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
}
