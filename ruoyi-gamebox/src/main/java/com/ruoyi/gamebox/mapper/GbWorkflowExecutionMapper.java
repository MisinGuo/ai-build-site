package com.ruoyi.gamebox.mapper;

import com.ruoyi.gamebox.domain.GbWorkflowExecution;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 工作流执行记录 Mapper 接口
 *
 * @date 2026-02-20
 */
public interface GbWorkflowExecutionMapper
{
    /**
     * 插入执行记录
     */
    int insertExecution(GbWorkflowExecution execution);

    /**
     * 更新执行记录
     */
    int updateExecution(GbWorkflowExecution execution);

    /**
     * 根据 executionId 查询
     */
    GbWorkflowExecution selectByExecutionId(@Param("executionId") String executionId);

    /**
     * 条件查询执行记录列表（分页在 Service 层处理）
     */
    List<GbWorkflowExecution> selectList(GbWorkflowExecution query);

    /**
     * 按工作流编码统计各状态数量
     * 返回：[{workflowCode, totalCount, successCount, failedCount, avgDuration}]
     */
    List<Map<String, Object>> selectStatsByWorkflowCode(@Param("workflowCode") String workflowCode);

    /**
     * 批量统计多个工作流的执行统计
     */
    List<Map<String, Object>> selectStatsForWorkflows(@Param("workflowCodes") List<String> workflowCodes);

    /**
     * 删除过期执行记录（保留最近 N 条）
     */
    int deleteOldRecords(@Param("workflowCode") String workflowCode, @Param("keepCount") int keepCount);
}
