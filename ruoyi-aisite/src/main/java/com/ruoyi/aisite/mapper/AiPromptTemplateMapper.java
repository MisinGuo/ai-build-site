package com.ruoyi.aisite.mapper;

import com.ruoyi.aisite.domain.AiPromptTemplate;
import java.util.List;

/**
 * Prompt 模板 Mapper
 */
public interface AiPromptTemplateMapper
{
    AiPromptTemplate selectById(Long id);

    AiPromptTemplate selectByCode(String code);

    List<AiPromptTemplate> selectList(AiPromptTemplate template);

    int insert(AiPromptTemplate template);

    int update(AiPromptTemplate template);

    int deleteById(Long id);

    int deleteByIds(Long[] ids);
}
