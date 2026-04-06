package com.ruoyi.aisite.mapper;

import com.ruoyi.aisite.domain.AiStorageConfig;
import java.util.List;

/**
 * 对象存储配置 Mapper
 */
public interface AiStorageConfigMapper
{
    AiStorageConfig selectById(Long id);

    List<AiStorageConfig> selectList(AiStorageConfig config);

    int insert(AiStorageConfig config);

    int update(AiStorageConfig config);

    int deleteById(Long id);

    int deleteByIds(Long[] ids);
}
