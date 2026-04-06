package com.ruoyi.aisite.service;

import com.ruoyi.aisite.domain.AiPromptTemplate;
import java.util.List;

/**
 * Prompt 模板 Service 接口
 */
public interface IAiPromptTemplateService
{
    AiPromptTemplate selectById(Long id);

    AiPromptTemplate selectByCode(String code);

    List<AiPromptTemplate> selectList(AiPromptTemplate template);

    int insert(AiPromptTemplate template);

    int update(AiPromptTemplate template);

    int deleteByIds(Long[] ids);
}
