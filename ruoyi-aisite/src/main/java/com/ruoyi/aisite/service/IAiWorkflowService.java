package com.ruoyi.aisite.service;

import java.util.List;
import com.ruoyi.aisite.domain.AiWorkflow;

public interface IAiWorkflowService
{
    AiWorkflow selectAiWorkflowById(Long id);
    AiWorkflow selectAiWorkflowByCode(String code);
    List<AiWorkflow> selectAiWorkflowList(AiWorkflow query);
    int insertAiWorkflow(AiWorkflow entity);
    int updateAiWorkflow(AiWorkflow entity);
    int deleteAiWorkflowById(Long id);
    int deleteAiWorkflowByIds(Long[] ids);
}
