package com.ruoyi.aisite.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.aisite.domain.AiOperationTask;
import com.ruoyi.aisite.mapper.AiOperationTaskMapper;
import com.ruoyi.aisite.service.IAiOperationTaskService;

@Service
public class AiOperationTaskServiceImpl implements IAiOperationTaskService
{
    @Autowired
    private AiOperationTaskMapper aiOperationTaskMapper;

    @Override
    public AiOperationTask selectAiOperationTaskById(Long id)
    {
        return aiOperationTaskMapper.selectById(id);
    }

    @Override
    public List<AiOperationTask> selectAiOperationTaskList(AiOperationTask query)
    {
        return aiOperationTaskMapper.selectList(query);
    }

    @Override
    public int insertAiOperationTask(AiOperationTask entity)
    {
        entity.setDelFlag("0");
        if (entity.getStatus() == null) entity.setStatus("pending");
        if (entity.getPriority() == null) entity.setPriority(5);
        if (entity.getRetryCount() == null) entity.setRetryCount(0);
        if (entity.getMaxRetries() == null) entity.setMaxRetries(3);
        return aiOperationTaskMapper.insert(entity);
    }

    @Override
    public int updateAiOperationTask(AiOperationTask entity)
    {
        return aiOperationTaskMapper.update(entity);
    }

    @Override
    public int deleteAiOperationTaskById(Long id)
    {
        return aiOperationTaskMapper.deleteById(id);
    }

    @Override
    public int deleteAiOperationTaskByIds(Long[] ids)
    {
        return aiOperationTaskMapper.deleteByIds(ids);
    }
}
