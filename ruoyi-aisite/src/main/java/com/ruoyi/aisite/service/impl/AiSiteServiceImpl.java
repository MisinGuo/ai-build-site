package com.ruoyi.aisite.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.aisite.domain.AiSite;
import com.ruoyi.aisite.mapper.AiSiteMapper;
import com.ruoyi.aisite.service.IAiSiteService;

@Service
public class AiSiteServiceImpl implements IAiSiteService
{
    @Autowired
    private AiSiteMapper aiSiteMapper;

    @Override
    public AiSite selectAiSiteById(Long id)
    {
        return aiSiteMapper.selectById(id);
    }

    @Override
    public AiSite selectAiSiteByDomain(String domain)
    {
        return aiSiteMapper.selectByDomain(domain);
    }

    @Override
    public List<AiSite> selectAiSiteList(AiSite aiSite)
    {
        return aiSiteMapper.selectList(aiSite);
    }

    @Override
    public int insertAiSite(AiSite aiSite)
    {
        aiSite.setDelFlag("0");
        if (aiSite.getStatus() == null || aiSite.getStatus().isEmpty())
        {
            aiSite.setStatus("1");
        }
        if (aiSite.getIndustry() == null || aiSite.getIndustry().isEmpty())
        {
            aiSite.setIndustry("general");
        }
        if (aiSite.getDefaultLocale() == null || aiSite.getDefaultLocale().isEmpty())
        {
            aiSite.setDefaultLocale("zh-CN");
        }
        return aiSiteMapper.insert(aiSite);
    }

    @Override
    public int updateAiSite(AiSite aiSite)
    {
        return aiSiteMapper.update(aiSite);
    }

    @Override
    public int deleteAiSiteById(Long id)
    {
        return aiSiteMapper.deleteById(id);
    }

    @Override
    public int deleteAiSiteByIds(Long[] ids)
    {
        return aiSiteMapper.deleteByIds(ids);
    }
}
