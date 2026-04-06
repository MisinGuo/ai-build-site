package com.ruoyi.aisite.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.aisite.domain.AiContentType;
import com.ruoyi.aisite.mapper.AiContentTypeMapper;
import com.ruoyi.aisite.service.IAiContentTypeService;
import com.ruoyi.common.utils.DateUtils;

/**
 * 内容类型 Service 实现
 */
@Service
public class AiContentTypeServiceImpl implements IAiContentTypeService
{
    @Autowired
    private AiContentTypeMapper aiContentTypeMapper;

    @Override
    public AiContentType selectAiContentTypeById(Long id)
    {
        return aiContentTypeMapper.selectAiContentTypeById(id);
    }

    @Override
    public AiContentType selectAiContentTypeByCode(Long siteId, String code)
    {
        return aiContentTypeMapper.selectAiContentTypeByCode(siteId, code);
    }

    @Override
    public List<AiContentType> selectAiContentTypeList(AiContentType aiContentType)
    {
        return aiContentTypeMapper.selectAiContentTypeList(aiContentType);
    }

    @Override
    public int insertAiContentType(AiContentType aiContentType)
    {
        aiContentType.setCreateTime(DateUtils.getNowDate());
        aiContentType.setDelFlag("0");
        if (aiContentType.getStatus() == null) {
            aiContentType.setStatus("1");
        }
        if (aiContentType.getSortOrder() == null) {
            aiContentType.setSortOrder(0);
        }
        return aiContentTypeMapper.insertAiContentType(aiContentType);
    }

    @Override
    public int updateAiContentType(AiContentType aiContentType)
    {
        aiContentType.setUpdateTime(DateUtils.getNowDate());
        return aiContentTypeMapper.updateAiContentType(aiContentType);
    }

    @Override
    public int deleteAiContentTypeById(Long id)
    {
        return aiContentTypeMapper.deleteAiContentTypeById(id);
    }

    @Override
    public int deleteAiContentTypeByIds(Long[] ids)
    {
        return aiContentTypeMapper.deleteAiContentTypeByIds(ids);
    }
}
