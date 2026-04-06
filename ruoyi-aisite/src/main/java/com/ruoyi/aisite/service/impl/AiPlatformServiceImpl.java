package com.ruoyi.aisite.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.aisite.domain.AiPlatform;
import com.ruoyi.aisite.mapper.AiPlatformMapper;
import com.ruoyi.aisite.service.IAiPlatformService;

@Service
public class AiPlatformServiceImpl implements IAiPlatformService
{
    @Autowired
    private AiPlatformMapper aiPlatformMapper;

    @Override
    public AiPlatform selectAiPlatformById(Long id)
    {
        return aiPlatformMapper.selectById(id);
    }

    @Override
    public List<AiPlatform> selectAiPlatformList(AiPlatform query)
    {
        return aiPlatformMapper.selectList(query);
    }

    @Override
    public int insertAiPlatform(AiPlatform entity)
    {
        entity.setDelFlag("0");
        if (entity.getStatus() == null) entity.setStatus("1");
        if (entity.getIsDefault() == null) entity.setIsDefault("0");
        if (entity.getMaxTokens() == null) entity.setMaxTokens(4096);
        return aiPlatformMapper.insert(entity);
    }

    @Override
    public int updateAiPlatform(AiPlatform entity)
    {
        return aiPlatformMapper.update(entity);
    }

    @Override
    public int deleteAiPlatformById(Long id)
    {
        return aiPlatformMapper.deleteById(id);
    }

    @Override
    public int deleteAiPlatformByIds(Long[] ids)
    {
        return aiPlatformMapper.deleteByIds(ids);
    }
}
