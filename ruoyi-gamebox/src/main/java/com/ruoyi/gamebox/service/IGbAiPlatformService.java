package com.ruoyi.gamebox.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.gamebox.domain.GbAiPlatform;

/**
 * AI平台配置Service接口
 * 
 * @author ruoyi
 */
public interface IGbAiPlatformService 
{
    /**
     * 查询AI平台配置
     * 
     * @param id 平台主键
     * @return AI平台配置
     */
    public GbAiPlatform selectGbAiPlatformById(Long id);

    /**
     * 查询AI平台配置列表
     * 
     * @param gbAiPlatform AI平台配置
     * @return AI平台配置集合
     */
    public List<GbAiPlatform> selectGbAiPlatformList(GbAiPlatform gbAiPlatform);

    /**
     * 新增AI平台配置
     * 
     * @param gbAiPlatform AI平台配置
     * @return 结果
     */
    public int insertGbAiPlatform(GbAiPlatform gbAiPlatform);

    /**
     * 修改AI平台配置
     * 
     * @param gbAiPlatform AI平台配置
     * @return 结果
     */
    public int updateGbAiPlatform(GbAiPlatform gbAiPlatform);

    /**
     * 批量删除AI平台配置
     * 
     * @param ids 需要删除的平台主键集合
     * @return 结果
     */
    public int deleteGbAiPlatformByIds(Long[] ids);

    /**
     * 删除AI平台配置信息
     * 
     * @param id 平台主键
     * @return 结果
     */
    public int deleteGbAiPlatformById(Long id);

    /**
     * 根据平台类型获取可用模型列表
     * 
     * @param platformType 平台类型
     * @param apiKey API密钥（可选，用于实时获取）
     * @param baseUrl API地址（可选）
     * @return 模型列表
     */
    public List<String> getAvailableModels(String platformType, String apiKey, String baseUrl);

    /**
     * 测试平台连接并返回详细信息（模型列表等）
     *
     * @param id 平台主键
     * @return 包含 platformType, modelCount, models 等信息的 Map
     * @throws Exception 连接失败时抛出异常，包含具体错误原因
     */
    public Map<String, Object> testConnection(Long id) throws Exception;

    /**
     * 调用AI平台生成文本
     *
     * @param platformId AI平台ID
     * @param systemPrompt 系统提示词
     * @param userPrompt 用户提示词
     * @param modelName 模型名称（可选）
     * @param temperature 温度参数（可选）
     * @param maxTokens 最大token数（可选）
     * @return 生成的文本内容
     */
    public String generateText(Long platformId, String systemPrompt, String userPrompt, 
                              String modelName, Double temperature, Integer maxTokens) throws Exception;
}
