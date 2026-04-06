package com.ruoyi.gamebox.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.gamebox.mapper.GbProxyConfigMapper;
import com.ruoyi.gamebox.domain.GbProxyConfig;
import com.ruoyi.gamebox.service.IGbProxyConfigService;

/**
 * 系统功能代理配置 Service 业务层处理
 */
@Service
public class GbProxyConfigServiceImpl implements IGbProxyConfigService
{
    @Autowired
    private GbProxyConfigMapper gbProxyConfigMapper;

    @Override
    public GbProxyConfig selectGbProxyConfigById(Long id)
    {
        return gbProxyConfigMapper.selectGbProxyConfigById(id);
    }

    @Override
    public GbProxyConfig selectGbProxyConfigByFeatureKey(String featureKey)
    {
        return gbProxyConfigMapper.selectGbProxyConfigByFeatureKey(featureKey);
    }

    @Override
    public List<GbProxyConfig> selectGbProxyConfigList(GbProxyConfig gbProxyConfig)
    {
        return gbProxyConfigMapper.selectGbProxyConfigList(gbProxyConfig);
    }

    @Override
    public int updateGbProxyConfig(GbProxyConfig gbProxyConfig)
    {
        gbProxyConfig.setUpdateTime(DateUtils.getNowDate());
        return gbProxyConfigMapper.updateGbProxyConfig(gbProxyConfig);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchUpdateGbProxyConfig(List<GbProxyConfig> configs)
    {
        int count = 0;
        for (GbProxyConfig config : configs)
        {
            config.setUpdateTime(DateUtils.getNowDate());
            count += gbProxyConfigMapper.updateGbProxyConfig(config);
        }
        return count;
    }
}
