package com.ruoyi.aisite.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.aisite.domain.AiCategory;
import com.ruoyi.aisite.mapper.AiCategoryMapper;
import com.ruoyi.aisite.service.IAiCategoryService;

@Service
public class AiCategoryServiceImpl implements IAiCategoryService
{
    @Autowired
    private AiCategoryMapper aiCategoryMapper;

    @Override
    public AiCategory selectAiCategoryById(Long id)
    {
        return aiCategoryMapper.selectAiCategoryById(id);
    }

    @Override
    public AiCategory selectAiCategoryBySlug(Long siteId, String typeCode, String slug)
    {
        return aiCategoryMapper.selectAiCategoryBySlug(siteId, typeCode, slug);
    }

    @Override
    public List<AiCategory> selectAiCategoryList(AiCategory aiCategory)
    {
        return aiCategoryMapper.selectAiCategoryList(aiCategory);
    }

    @Override
    public int insertAiCategory(AiCategory aiCategory)
    {
        aiCategory.setDelFlag("0");
        if (aiCategory.getStatus() == null || aiCategory.getStatus().isEmpty())
        {
            aiCategory.setStatus("1");
        }
        if (aiCategory.getSortOrder() == null)
        {
            aiCategory.setSortOrder(0);
        }
        return aiCategoryMapper.insertAiCategory(aiCategory);
    }

    @Override
    public int updateAiCategory(AiCategory aiCategory)
    {
        return aiCategoryMapper.updateAiCategory(aiCategory);
    }

    @Override
    public int deleteAiCategoryById(Long id)
    {
        return aiCategoryMapper.deleteAiCategoryById(id);
    }

    @Override
    public int deleteAiCategoryByIds(Long[] ids)
    {
        return aiCategoryMapper.deleteAiCategoryByIds(ids);
    }
}
