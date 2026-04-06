package com.ruoyi.aisite.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.aisite.domain.AiPlatform;

@Mapper
public interface AiPlatformMapper
{
    AiPlatform selectById(Long id);
    List<AiPlatform> selectList(AiPlatform query);
    int insert(AiPlatform entity);
    int update(AiPlatform entity);
    int deleteById(Long id);
    int deleteByIds(Long[] ids);
}
