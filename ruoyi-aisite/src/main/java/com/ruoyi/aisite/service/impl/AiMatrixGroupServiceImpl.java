package com.ruoyi.aisite.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.aisite.domain.AiMatrixGroup;
import com.ruoyi.aisite.mapper.AiMatrixGroupMapper;
import com.ruoyi.aisite.service.IAiMatrixGroupService;

@Service
public class AiMatrixGroupServiceImpl implements IAiMatrixGroupService
{
    @Autowired
    private AiMatrixGroupMapper aiMatrixGroupMapper;

    @Override
    public AiMatrixGroup selectAiMatrixGroupById(Long id)
    {
        return aiMatrixGroupMapper.selectById(id);
    }

    @Override
    public List<AiMatrixGroup> selectAiMatrixGroupList(AiMatrixGroup query)
    {
        return aiMatrixGroupMapper.selectList(query);
    }

    @Override
    public int insertAiMatrixGroup(AiMatrixGroup entity)
    {
        entity.setDelFlag("0");
        if (entity.getStatus() == null) entity.setStatus("draft");
        if (entity.getIndustry() == null) entity.setIndustry("general");
        if (entity.getTotalCount() == null) entity.setTotalCount(0);
        if (entity.getBuiltCount() == null) entity.setBuiltCount(0);
        if (entity.getLiveCount() == null) entity.setLiveCount(0);
        return aiMatrixGroupMapper.insert(entity);
    }

    @Override
    public int updateAiMatrixGroup(AiMatrixGroup entity)
    {
        return aiMatrixGroupMapper.update(entity);
    }

    @Override
    public int deleteAiMatrixGroupById(Long id)
    {
        return aiMatrixGroupMapper.deleteById(id);
    }

    @Override
    public int deleteAiMatrixGroupByIds(Long[] ids)
    {
        return aiMatrixGroupMapper.deleteByIds(ids);
    }
}
