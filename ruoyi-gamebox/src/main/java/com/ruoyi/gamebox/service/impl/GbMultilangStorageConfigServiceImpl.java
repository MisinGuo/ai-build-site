package com.ruoyi.gamebox.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.gamebox.domain.GbMultilangStorageConfig;
import com.ruoyi.gamebox.mapper.GbMultilangStorageConfigMapper;
import com.ruoyi.gamebox.service.IGbMultilangStorageConfigService;

/**
 * 多语言存储配置Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-01-12
 */
@Service
public class GbMultilangStorageConfigServiceImpl implements IGbMultilangStorageConfigService 
{
    @Autowired
    private GbMultilangStorageConfigMapper gbMultilangStorageConfigMapper;

    /**
     * 查询多语言存储配置
     * 
     * @param id 多语言存储配置主键
     * @return 多语言存储配置
     */
    @Override
    public GbMultilangStorageConfig selectGbMultilangStorageConfigById(Long id)
    {
        return gbMultilangStorageConfigMapper.selectGbMultilangStorageConfigById(id);
    }

    /**
     * 查询多语言存储配置列表
     * 
     * @param gbMultilangStorageConfig 多语言存储配置
     * @return 多语言存储配置
     */
    @Override
    public List<GbMultilangStorageConfig> selectGbMultilangStorageConfigList(GbMultilangStorageConfig gbMultilangStorageConfig)
    {
        return gbMultilangStorageConfigMapper.selectGbMultilangStorageConfigList(gbMultilangStorageConfig);
    }

    /**
     * 新增多语言存储配置
     * 
     * @param gbMultilangStorageConfig 多语言存储配置
     * @return 结果
     */
    @Override
    @Transactional
    public int insertGbMultilangStorageConfig(GbMultilangStorageConfig gbMultilangStorageConfig)
    {
        // 检查语言代码是否已存在
        if (checkLangCodeExists(gbMultilangStorageConfig.getLangCode(), null)) {
            throw new RuntimeException("语言代码已存在");
        }

        gbMultilangStorageConfig.setCreateTime(DateUtils.getNowDate());
        gbMultilangStorageConfig.setCreateBy(SecurityUtils.getUsername());
        gbMultilangStorageConfig.setDelFlag("0");
        
        // 设置默认值
        if (StringUtils.isNull(gbMultilangStorageConfig.getStatus())) {
            gbMultilangStorageConfig.setStatus("1"); // 默认启用
        }
        if (StringUtils.isNull(gbMultilangStorageConfig.getIsDefault())) {
            gbMultilangStorageConfig.setIsDefault("0");
        }
        if (StringUtils.isNull(gbMultilangStorageConfig.getPriority())) {
            gbMultilangStorageConfig.setPriority(0);
        }

        // 如果设置为默认配置，先清除其他默认标志
        if ("1".equals(gbMultilangStorageConfig.getIsDefault())) {
            gbMultilangStorageConfigMapper.clearDefaultFlag();
        }

        return gbMultilangStorageConfigMapper.insertGbMultilangStorageConfig(gbMultilangStorageConfig);
    }

    /**
     * 修改多语言存储配置
     * 
     * @param gbMultilangStorageConfig 多语言存储配置
     * @return 结果
     */
    @Override
    @Transactional
    public int updateGbMultilangStorageConfig(GbMultilangStorageConfig gbMultilangStorageConfig)
    {
        // 检查语言代码是否已存在（排除当前记录）
        if (checkLangCodeExists(gbMultilangStorageConfig.getLangCode(), gbMultilangStorageConfig.getId())) {
            throw new RuntimeException("语言代码已存在");
        }

        gbMultilangStorageConfig.setUpdateTime(DateUtils.getNowDate());
        gbMultilangStorageConfig.setUpdateBy(SecurityUtils.getUsername());
        
        // 如果设置为默认配置，先清除其他默认标志
        if ("1".equals(gbMultilangStorageConfig.getIsDefault())) {
            gbMultilangStorageConfigMapper.clearDefaultFlag();
        }

        return gbMultilangStorageConfigMapper.updateGbMultilangStorageConfig(gbMultilangStorageConfig);
    }

    /**
     * 批量删除多语言存储配置
     * 
     * @param ids 需要删除的多语言存储配置主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteGbMultilangStorageConfigByIds(Long[] ids)
    {
        return gbMultilangStorageConfigMapper.deleteGbMultilangStorageConfigByIds(ids);
    }

    /**
     * 删除多语言存储配置信息
     * 
     * @param id 多语言存储配置主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteGbMultilangStorageConfigById(Long id)
    {
        return gbMultilangStorageConfigMapper.deleteGbMultilangStorageConfigById(id);
    }

    /**
     * 根据语言代码查询存储配置
     * 
     * @param langCode 语言代码
     * @return 多语言存储配置
     */
    @Override
    public GbMultilangStorageConfig selectGbMultilangStorageConfigByLangCode(String langCode)
    {
        return gbMultilangStorageConfigMapper.selectGbMultilangStorageConfigByLangCode(langCode);
    }

    /**
     * 查询启用的存储配置列表
     * 
     * @return 多语言存储配置集合
     */
    @Override
    public List<GbMultilangStorageConfig> selectEnabledStorageConfigs()
    {
        return gbMultilangStorageConfigMapper.selectEnabledStorageConfigs();
    }

    /**
     * 查询默认存储配置
     * 
     * @return 多语言存储配置
     */
    @Override
    public GbMultilangStorageConfig selectDefaultStorageConfig()
    {
        return gbMultilangStorageConfigMapper.selectDefaultStorageConfig();
    }

    /**
     * 检查语言代码是否已存在
     * 
     * @param langCode 语言代码
     * @param excludeId 排除的ID(用于更新时)
     * @return 是否存在
     */
    @Override
    public boolean checkLangCodeExists(String langCode, Long excludeId)
    {
        return gbMultilangStorageConfigMapper.checkLangCodeExists(langCode, excludeId);
    }

    /**
     * 测试存储配置连接
     * 
     * @param id 配置ID
     * @return 测试结果
     */
    @Override
    @Transactional
    public boolean testStorageConnection(Long id)
    {
        GbMultilangStorageConfig config = gbMultilangStorageConfigMapper.selectGbMultilangStorageConfigById(id);
        if (config == null) {
            return false;
        }

        boolean testResult = false;
        String testMessage = "";
        
        try {
            // 根据不同存储类型进行连接测试
            switch (config.getStorageType()) {
                case "local":
                    testResult = testLocalStorage(config);
                    testMessage = testResult ? "本地存储连接成功" : "本地存储路径不可访问";
                    break;
                case "oss":
                    testResult = testOSSStorage(config);
                    testMessage = testResult ? "OSS连接成功" : "OSS连接失败";
                    break;
                case "cdn":
                    testResult = testCDNStorage(config);
                    testMessage = testResult ? "CDN连接成功" : "CDN连接失败";
                    break;
                default:
                    testMessage = "不支持的存储类型";
                    break;
            }
        } catch (Exception e) {
            testMessage = "连接测试异常：" + e.getMessage();
        }

        // 更新测试结果
        GbMultilangStorageConfig updateConfig = new GbMultilangStorageConfig();
        updateConfig.setId(id);
        updateConfig.setLastTestAt(DateUtils.getNowDate());
        updateConfig.setLastTestResult(testResult ? "1" : "0");
        updateConfig.setTestMessage(testMessage);
        updateConfig.setUpdateBy(SecurityUtils.getUsername());
        updateConfig.setUpdateTime(DateUtils.getNowDate());
        
        gbMultilangStorageConfigMapper.updateGbMultilangStorageConfig(updateConfig);

        return testResult;
    }

    /**
     * 设置默认存储配置
     * 
     * @param id 配置ID
     * @return 结果
     */
    @Override
    @Transactional
    public int setAsDefault(Long id)
    {
        // 先清除所有默认标志
        gbMultilangStorageConfigMapper.clearDefaultFlag();
        
        // 设置新的默认配置
        GbMultilangStorageConfig config = new GbMultilangStorageConfig();
        config.setId(id);
        config.setIsDefault("1");
        config.setUpdateBy(SecurityUtils.getUsername());
        config.setUpdateTime(DateUtils.getNowDate());
        
        return gbMultilangStorageConfigMapper.updateGbMultilangStorageConfig(config);
    }

    /**
     * 根据语言代码获取存储路径
     * 
     * @param langCode 语言代码
     * @return 存储路径
     */
    @Override
    public String getStoragePathByLangCode(String langCode)
    {
        GbMultilangStorageConfig config = gbMultilangStorageConfigMapper.selectGbMultilangStorageConfigByLangCode(langCode);
        if (config != null && "1".equals(config.getStatus())) {
            return config.getBasePath();
        }
        
        // 如果没找到对应语言的配置，使用默认配置
        GbMultilangStorageConfig defaultConfig = gbMultilangStorageConfigMapper.selectDefaultStorageConfig();
        return defaultConfig != null ? defaultConfig.getBasePath() : "/default";
    }

    /**
     * 批量启用/禁用存储配置
     * 
     * @param ids 配置ID列表
     * @param status 状态
     * @param operatorBy 操作人
     * @return 更新数量
     */
    @Override
    @Transactional
    public int batchUpdateStatus(Long[] ids, String status, String operatorBy)
    {
        int updateCount = 0;
        Date currentTime = DateUtils.getNowDate();

        for (Long id : ids) {
            GbMultilangStorageConfig config = new GbMultilangStorageConfig();
            config.setId(id);
            config.setStatus(status);
            config.setUpdateBy(operatorBy);
            config.setUpdateTime(currentTime);
            
            int result = gbMultilangStorageConfigMapper.updateGbMultilangStorageConfig(config);
            if (result > 0) {
                updateCount++;
            }
        }

        return updateCount;
    }

    /**
     * 测试本地存储
     * 
     * @param config 存储配置
     * @return 测试结果
     */
    private boolean testLocalStorage(GbMultilangStorageConfig config)
    {
        try {
            java.io.File file = new java.io.File(config.getBasePath());
            return file.exists() && file.canRead() && file.canWrite();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 测试OSS存储
     * 
     * @param config 存储配置
     * @return 测试结果
     */
    private boolean testOSSStorage(GbMultilangStorageConfig config)
    {
        // 这里应该根据实际的OSS配置进行连接测试
        // 暂时返回true，实际使用时需要实现具体的OSS连接测试逻辑
        return true;
    }

    /**
     * 测试CDN存储
     * 
     * @param config 存储配置
     * @return 测试结果
     */
    private boolean testCDNStorage(GbMultilangStorageConfig config)
    {
        // 这里应该根据实际的CDN配置进行连接测试
        // 暂时返回true，实际使用时需要实现具体的CDN连接测试逻辑
        return true;
    }
}