package com.ruoyi.aisite.service.impl;

import com.ruoyi.aisite.domain.AiPromptTemplate;
import com.ruoyi.aisite.mapper.AiPromptTemplateMapper;
import com.ruoyi.aisite.service.IAiPromptTemplateService;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Prompt 模板 Service 实现
 */
@Service
public class AiPromptTemplateServiceImpl implements IAiPromptTemplateService
{
    @Autowired
    private AiPromptTemplateMapper promptTemplateMapper;

    @Override
    public AiPromptTemplate selectById(Long id) {
        return promptTemplateMapper.selectById(id);
    }

    @Override
    public AiPromptTemplate selectByCode(String code) {
        return promptTemplateMapper.selectByCode(code);
    }

    @Override
    public List<AiPromptTemplate> selectList(AiPromptTemplate template) {
        return promptTemplateMapper.selectList(template);
    }

    @Override
    public int insert(AiPromptTemplate template) {
        if (template.getIsBuiltin() == null) template.setIsBuiltin("0");
        if (template.getStatus() == null) template.setStatus("1");
        template.setDelFlag("0");
        template.setCreateBy(SecurityUtils.getUsername());
        template.setUpdateBy(SecurityUtils.getUsername());
        return promptTemplateMapper.insert(template);
    }

    @Override
    public int update(AiPromptTemplate template) {
        template.setUpdateBy(SecurityUtils.getUsername());
        return promptTemplateMapper.update(template);
    }

    @Override
    public int deleteByIds(Long[] ids) {
        return promptTemplateMapper.deleteByIds(ids);
    }
}
