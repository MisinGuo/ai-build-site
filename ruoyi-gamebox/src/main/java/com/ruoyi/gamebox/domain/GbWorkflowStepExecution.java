package com.ruoyi.gamebox.domain;

import java.util.Date;

/**
 * 工作流步骤执行记录
 *
 * 对应表: gb_workflow_step_execution
 */
public class GbWorkflowStepExecution {

    private Long id;

    /** 所属工作流执行ID */
    private String executionId;

    /** 步骤ID（同 definition 中的 stepId） */
    private String stepId;

    /** 步骤名称 */
    private String stepName;

    /** 步骤类型（tool） */
    private String stepType;

    /** 工具编码 */
    private String toolCode;

    /** 执行状态：running / success / failed / skipped */
    private String status;

    /** 步骤输入数据（JSON） */
    private String inputData;

    /** 步骤输出数据（JSON） */
    private String outputData;

    /** 步骤配置快照（JSON，记录执行时的 inputMapping 配置） */
    private String stepConfig;

    /** 错误信息 */
    private String error;

    /** 开始时间 */
    private Date startTime;

    /** 结束时间 */
    private Date endTime;

    /** 执行耗时（毫秒） */
    private Long duration;

    /** 创建时间 */
    private Date createTime;

    // ===== getters & setters =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getExecutionId() { return executionId; }
    public void setExecutionId(String executionId) { this.executionId = executionId; }

    public String getStepId() { return stepId; }
    public void setStepId(String stepId) { this.stepId = stepId; }

    public String getStepName() { return stepName; }
    public void setStepName(String stepName) { this.stepName = stepName; }

    public String getStepType() { return stepType; }
    public void setStepType(String stepType) { this.stepType = stepType; }

    public String getToolCode() { return toolCode; }
    public void setToolCode(String toolCode) { this.toolCode = toolCode; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getInputData() { return inputData; }
    public void setInputData(String inputData) { this.inputData = inputData; }

    public String getOutputData() { return outputData; }
    public void setOutputData(String outputData) { this.outputData = outputData; }

    public String getStepConfig() { return stepConfig; }
    public void setStepConfig(String stepConfig) { this.stepConfig = stepConfig; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }

    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }

    public Long getDuration() { return duration; }
    public void setDuration(Long duration) { this.duration = duration; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
