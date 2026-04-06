package com.ruoyi.aisite.service;

import java.util.List;
import com.ruoyi.aisite.domain.AiSeoMetrics;

public interface IAiSeoMetricsService
{
    AiSeoMetrics selectAiSeoMetricsById(Long id);
    List<AiSeoMetrics> selectAiSeoMetricsBySiteId(Long siteId);
    List<AiSeoMetrics> selectAiSeoMetricsList(AiSeoMetrics query);
    int insertAiSeoMetrics(AiSeoMetrics entity);
    int updateAiSeoMetrics(AiSeoMetrics entity);
    int deleteAiSeoMetricsById(Long id);
}
