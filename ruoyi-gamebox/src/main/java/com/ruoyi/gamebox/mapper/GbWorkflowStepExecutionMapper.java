package com.ruoyi.gamebox.mapper;

import com.ruoyi.gamebox.domain.GbWorkflowStepExecution;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 工作流步骤执行记录 Mapper
 */
public interface GbWorkflowStepExecutionMapper {

    /**
     * 插入步骤执行记录
     */
    int insertStepExecution(GbWorkflowStepExecution record);

    /**
     * 更新步骤执行记录（用于执行结束时回填 output/error/duration/endTime/status）
     */
    int updateStepExecution(GbWorkflowStepExecution record);

    /**
     * 根据 executionId 查询所有步骤执行记录（按 start_time 升序）
     */
    List<GbWorkflowStepExecution> selectByExecutionId(@Param("executionId") String executionId);

    /**
     * 删除指定执行批次的所有步骤记录（配合清理场景使用）
     */
    int deleteByExecutionId(@Param("executionId") String executionId);
}
