package com.ruoyi.aisite.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.aisite.domain.AiSeoMetrics;
import com.ruoyi.aisite.mapper.AiSeoMetricsMapper;
import com.ruoyi.aisite.service.IAiSeoMetricsService;

@Service
public class AiSeoMetricsServiceImpl implements IAiSeoMetricsService
{
    @Autowired
    private AiSeoMetricsMapper aiSeoMetricsMapper;

    @Override
    public AiSeoMetrics selectAiSeoMetricsById(Long id)
    {
        return aiSeoMetricsMapper.selectById(id);
    }

    @Override
    public List<AiSeoMetrics> selectAiSeoMetricsBySiteId(Long siteId)
    {
        return aiSeoMetricsMapper.selectBySiteId(siteId);
    }

    @Override
    public List<AiSeoMetrics> selectAiSeoMetricsList(AiSeoMetrics query)
    {
        return aiSeoMetricsMapper.selectList(query);
    }

    @Override
    public int insertAiSeoMetrics(AiSeoMetrics entity)
    {
        if (entity.getIndexedPages() == null) entity.setIndexedPages(0);
        if (entity.getClicks() == null) entity.setClicks(0);
        if (entity.getImpressions() == null) entity.setImpressions(0);
        return aiSeoMetricsMapper.insert(entity);
    }

    @Override
    public int updateAiSeoMetrics(AiSeoMetrics entity)
    {
        return aiSeoMetricsMapper.update(entity);
    }

    @Override
    public int deleteAiSeoMetricsById(Long id)
    {
        return aiSeoMetricsMapper.deleteById(id);
    }
}
