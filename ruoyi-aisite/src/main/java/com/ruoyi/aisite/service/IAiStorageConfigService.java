package com.ruoyi.aisite.service;

import com.ruoyi.aisite.domain.AiStorageConfig;
import java.util.List;

/**
 * 对象存储配置 Service 接口
 */
public interface IAiStorageConfigService
{
    AiStorageConfig selectById(Long id);

    List<AiStorageConfig> selectList(AiStorageConfig config);

    int insert(AiStorageConfig config);

    int update(AiStorageConfig config);

    int deleteByIds(Long[] ids);
}
