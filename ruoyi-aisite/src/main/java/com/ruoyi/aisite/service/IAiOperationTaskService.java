package com.ruoyi.aisite.service;

import java.util.List;
import com.ruoyi.aisite.domain.AiOperationTask;

public interface IAiOperationTaskService
{
    AiOperationTask selectAiOperationTaskById(Long id);
    List<AiOperationTask> selectAiOperationTaskList(AiOperationTask query);
    int insertAiOperationTask(AiOperationTask entity);
    int updateAiOperationTask(AiOperationTask entity);
    int deleteAiOperationTaskById(Long id);
    int deleteAiOperationTaskByIds(Long[] ids);
}
