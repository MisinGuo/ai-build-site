package com.ruoyi.aisite.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.aisite.domain.AiSeoMetrics;

@Mapper
public interface AiSeoMetricsMapper
{
    AiSeoMetrics selectById(Long id);
    List<AiSeoMetrics> selectBySiteId(Long siteId);
    List<AiSeoMetrics> selectList(AiSeoMetrics query);
    int insert(AiSeoMetrics entity);
    int update(AiSeoMetrics entity);
    int deleteById(Long id);
}
