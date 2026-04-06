package com.ruoyi.gamebox.service;

import java.util.List;
import com.ruoyi.gamebox.domain.GbMultilangStorageConfig;

/**
 * 多语言存储配置Service接口
 * 
 * @author ruoyi
 * @date 2026-01-12
 */
public interface IGbMultilangStorageConfigService 
{
    /**
     * 查询多语言存储配置
     * 
     * @param id 多语言存储配置主键
     * @return 多语言存储配置
     */
    public GbMultilangStorageConfig selectGbMultilangStorageConfigById(Long id);

    /**
     * 查询多语言存储配置列表
     * 
     * @param gbMultilangStorageConfig 多语言存储配置
     * @return 多语言存储配置集合
     */
    public List<GbMultilangStorageConfig> selectGbMultilangStorageConfigList(GbMultilangStorageConfig gbMultilangStorageConfig);

    /**
     * 新增多语言存储配置
     * 
     * @param gbMultilangStorageConfig 多语言存储配置
     * @return 结果
     */
    public int insertGbMultilangStorageConfig(GbMultilangStorageConfig gbMultilangStorageConfig);

    /**
     * 修改多语言存储配置
     * 
     * @param gbMultilangStorageConfig 多语言存储配置
     * @return 结果
     */
    public int updateGbMultilangStorageConfig(GbMultilangStorageConfig gbMultilangStorageConfig);

    /**
     * 批量删除多语言存储配置
     * 
     * @param ids 需要删除的多语言存储配置主键集合
     * @return 结果
     */
    public int deleteGbMultilangStorageConfigByIds(Long[] ids);

    /**
     * 删除多语言存储配置信息
     * 
     * @param id 多语言存储配置主键
     * @return 结果
     */
    public int deleteGbMultilangStorageConfigById(Long id);

    /**
     * 根据语言代码查询存储配置
     * 
     * @param langCode 语言代码
     * @return 多语言存储配置
     */
    public GbMultilangStorageConfig selectGbMultilangStorageConfigByLangCode(String langCode);

    /**
     * 查询启用的存储配置列表
     * 
     * @return 多语言存储配置集合
     */
    public List<GbMultilangStorageConfig> selectEnabledStorageConfigs();

    /**
     * 查询默认存储配置
     * 
     * @return 多语言存储配置
     */
    public GbMultilangStorageConfig selectDefaultStorageConfig();

    /**
     * 检查语言代码是否已存在
     * 
     * @param langCode 语言代码
     * @param excludeId 排除的ID(用于更新时)
     * @return 是否存在
     */
    public boolean checkLangCodeExists(String langCode, Long excludeId);

    /**
     * 测试存储配置连接
     * 
     * @param id 配置ID
     * @return 测试结果
     */
    public boolean testStorageConnection(Long id);

    /**
     * 设置默认存储配置
     * 
     * @param id 配置ID
     * @return 结果
     */
    public int setAsDefault(Long id);

    /**
     * 根据语言代码获取存储路径
     * 
     * @param langCode 语言代码
     * @return 存储路径
     */
    public String getStoragePathByLangCode(String langCode);

    /**
     * 批量启用/禁用存储配置
     * 
     * @param ids 配置ID列表
     * @param status 状态
     * @param operatorBy 操作人
     * @return 更新数量
     */
    public int batchUpdateStatus(Long[] ids, String status, String operatorBy);
}