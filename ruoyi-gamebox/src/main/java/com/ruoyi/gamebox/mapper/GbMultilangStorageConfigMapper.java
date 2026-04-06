package com.ruoyi.gamebox.mapper;

import java.util.List;
import com.ruoyi.gamebox.domain.GbMultilangStorageConfig;

/**
 * 多语言存储配置Mapper接口
 * 
 * @author ruoyi
 * @date 2026-01-12
 */
public interface GbMultilangStorageConfigMapper 
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
     * 删除多语言存储配置
     * 
     * @param id 多语言存储配置主键
     * @return 结果
     */
    public int deleteGbMultilangStorageConfigById(Long id);

    /**
     * 批量删除多语言存储配置
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGbMultilangStorageConfigByIds(Long[] ids);

    /**
     * 检查语言代码是否已存在
     * 
     * @param langCode 语言代码
     * @param excludeId 排除的ID(用于更新时)
     * @return 是否存在
     */
    public boolean checkLangCodeExists(String langCode, Long excludeId);

    /**
     * 清除默认配置标志
     * 
     * @return 结果
     */
    public int clearDefaultFlag();
}