package com.ruoyi.aisite.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.aisite.domain.AiAtomicTool;
import com.ruoyi.aisite.mapper.AiAtomicToolMapper;
import com.ruoyi.aisite.service.IAiAtomicToolService;

@Service
public class AiAtomicToolServiceImpl implements IAiAtomicToolService
{
    @Autowired
    private AiAtomicToolMapper aiAtomicToolMapper;

    @Override
    public AiAtomicTool selectAiAtomicToolById(Long id)
    {
        return aiAtomicToolMapper.selectById(id);
    }

    @Override
    public AiAtomicTool selectAiAtomicToolByCode(String toolCode)
    {
        return aiAtomicToolMapper.selectByCode(toolCode);
    }

    @Override
    public List<AiAtomicTool> selectAiAtomicToolList(AiAtomicTool query)
    {
        return aiAtomicToolMapper.selectList(query);
    }

    @Override
    public int insertAiAtomicTool(AiAtomicTool entity)
    {
        if (entity.getIsBuiltin() == null) entity.setIsBuiltin("0");
        if (entity.getIsEnabled() == null) entity.setIsEnabled("1");
        if (entity.getSortOrder() == null) entity.setSortOrder(0);
        return aiAtomicToolMapper.insert(entity);
    }

    @Override
    public int updateAiAtomicTool(AiAtomicTool entity)
    {
        return aiAtomicToolMapper.update(entity);
    }

    @Override
    public int deleteAiAtomicToolById(Long id)
    {
        return aiAtomicToolMapper.deleteById(id);
    }

    @Override
    public int deleteAiAtomicToolByIds(Long[] ids)
    {
        return aiAtomicToolMapper.deleteByIds(ids);
    }
}
