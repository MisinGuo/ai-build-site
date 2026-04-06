package com.ruoyi.aisite.service.impl;

import com.ruoyi.aisite.domain.AiStorageConfig;
import com.ruoyi.aisite.mapper.AiStorageConfigMapper;
import com.ruoyi.aisite.service.IAiStorageConfigService;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 对象存储配置 Service 实现
 */
@Service
public class AiStorageConfigServiceImpl implements IAiStorageConfigService
{
    @Autowired
    private AiStorageConfigMapper storageConfigMapper;

    @Override
    public AiStorageConfig selectById(Long id) {
        return storageConfigMapper.selectById(id);
    }

    @Override
    public List<AiStorageConfig> selectList(AiStorageConfig config) {
        return storageConfigMapper.selectList(config);
    }

    @Override
    public int insert(AiStorageConfig config) {
        if (config.getIsDefault() == null) config.setIsDefault("0");
        if (config.getStatus() == null) config.setStatus("1");
        config.setDelFlag("0");
        config.setCreateBy(SecurityUtils.getUsername());
        config.setUpdateBy(SecurityUtils.getUsername());
        return storageConfigMapper.insert(config);
    }

    @Override
    public int update(AiStorageConfig config) {
        config.setUpdateBy(SecurityUtils.getUsername());
        return storageConfigMapper.update(config);
    }

    @Override
    public int deleteByIds(Long[] ids) {
        return storageConfigMapper.deleteByIds(ids);
    }
}
