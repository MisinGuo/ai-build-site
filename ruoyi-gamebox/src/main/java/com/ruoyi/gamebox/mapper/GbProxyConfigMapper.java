package com.ruoyi.gamebox.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.gamebox.domain.GbProxyConfig;

/**
 * 系统功能代理配置 Mapper 接口
 */
@Mapper
public interface GbProxyConfigMapper
{
    /**
     * 查询代理配置
     *
     * @param id 主键
     * @return 代理配置
     */
    GbProxyConfig selectGbProxyConfigById(Long id);

    /**
     * 根据功能标识符查询代理配置
     *
     * @param featureKey 功能标识
     * @return 代理配置
     */
    GbProxyConfig selectGbProxyConfigByFeatureKey(String featureKey);

    /**
     * 查询全部代理配置列表（按分组+排序号排序）
     *
     * @param gbProxyConfig 查询条件
     * @return 代理配置集合
     */
    List<GbProxyConfig> selectGbProxyConfigList(GbProxyConfig gbProxyConfig);

    /**
     * 更新代理配置（只允许更新代理相关字段，不允许修改功能定义字段）
     *
     * @param gbProxyConfig 代理配置
     * @return 影响行数
     */
    int updateGbProxyConfig(GbProxyConfig gbProxyConfig);
}
