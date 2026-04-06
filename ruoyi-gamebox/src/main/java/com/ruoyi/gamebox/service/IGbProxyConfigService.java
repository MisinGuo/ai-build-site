package com.ruoyi.gamebox.service;

import java.util.List;
import com.ruoyi.gamebox.domain.GbProxyConfig;

/**
 * 系统功能代理配置 Service 接口
 */
public interface IGbProxyConfigService
{
    /**
     * 查询代理配置
     *
     * @param id 主键
     * @return 代理配置
     */
    GbProxyConfig selectGbProxyConfigById(Long id);

    /**
     * 根据功能标识查询代理配置
     *
     * @param featureKey 功能标识
     * @return 代理配置
     */
    GbProxyConfig selectGbProxyConfigByFeatureKey(String featureKey);

    /**
     * 查询全部代理配置列表
     *
     * @param gbProxyConfig 查询条件
     * @return 代理配置集合
     */
    List<GbProxyConfig> selectGbProxyConfigList(GbProxyConfig gbProxyConfig);

    /**
     * 更新代理配置（仅允许修改代理参数，不修改功能定义）
     *
     * @param gbProxyConfig 代理配置
     * @return 结果
     */
    int updateGbProxyConfig(GbProxyConfig gbProxyConfig);

    /**
     * 批量更新代理配置
     *
     * @param configs 代理配置列表
     * @return 成功更新数量
     */
    int batchUpdateGbProxyConfig(List<GbProxyConfig> configs);
}
