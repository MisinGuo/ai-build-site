package com.ruoyi.aisite.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.aisite.domain.AiWorkflow;
import com.ruoyi.aisite.mapper.AiWorkflowMapper;
import com.ruoyi.aisite.service.IAiWorkflowService;

@Service
public class AiWorkflowServiceImpl implements IAiWorkflowService
{
    @Autowired
    private AiWorkflowMapper aiWorkflowMapper;

    @Override
    public AiWorkflow selectAiWorkflowById(Long id)
    {
        return aiWorkflowMapper.selectById(id);
    }

    @Override
    public AiWorkflow selectAiWorkflowByCode(String code)
    {
        return aiWorkflowMapper.selectByCode(code);
    }

    @Override
    public List<AiWorkflow> selectAiWorkflowList(AiWorkflow query)
    {
        return aiWorkflowMapper.selectList(query);
    }

    @Override
    public int insertAiWorkflow(AiWorkflow entity)
    {
        entity.setDelFlag("0");
        if (entity.getIsBuiltin() == null) entity.setIsBuiltin("0");
        if (entity.getIsEnabled() == null) entity.setIsEnabled("1");
        if (entity.getVersion() == null) entity.setVersion(1);
        if (entity.getCategory() == null) entity.setCategory("general");
        return aiWorkflowMapper.insert(entity);
    }

    @Override
    public int updateAiWorkflow(AiWorkflow entity)
    {
        return aiWorkflowMapper.update(entity);
    }

    @Override
    public int deleteAiWorkflowById(Long id)
    {
        return aiWorkflowMapper.deleteById(id);
    }

    @Override
    public int deleteAiWorkflowByIds(Long[] ids)
    {
        return aiWorkflowMapper.deleteByIds(ids);
    }
}
