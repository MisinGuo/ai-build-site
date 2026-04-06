package com.ruoyi.gamebox.mapper;

import java.util.List;
import com.ruoyi.gamebox.domain.GbAiPlatform;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI平台配置Mapper接口
 * 
 * @author ruoyi
 */
@Mapper
public interface GbAiPlatformMapper 
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
     * 删除AI平台配置
     * 
     * @param id 平台主键
     * @return 结果
     */
    public int deleteGbAiPlatformById(Long id);

    /**
     * 批量删除AI平台配置
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGbAiPlatformByIds(Long[] ids);
}
