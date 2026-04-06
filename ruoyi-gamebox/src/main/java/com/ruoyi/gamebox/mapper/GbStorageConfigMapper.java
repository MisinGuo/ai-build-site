package com.ruoyi.gamebox.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.gamebox.domain.GbStorageConfig;

/**
 * 存储配置Mapper接口
 * 
 * @author ruoyi
 */
@Mapper
public interface GbStorageConfigMapper 
{
    /**
     * 查询存储配置
     * 
     * @param id 配置主键
     * @return 存储配置
     */
    public GbStorageConfig selectGbStorageConfigById(Long id);

    /**
     * 查询存储配置列表
     * 
     * @param gbStorageConfig 存储配置
     * @return 存储配置集合
     */
    public List<GbStorageConfig> selectGbStorageConfigList(GbStorageConfig gbStorageConfig);

    /**
     * 新增存储配置
     * 
     * @param gbStorageConfig 存储配置
     * @return 结果
     */
    public int insertGbStorageConfig(GbStorageConfig gbStorageConfig);

    /**
     * 修改存储配置
     * 
     * @param gbStorageConfig 存储配置
     * @return 结果
     */
    public int updateGbStorageConfig(GbStorageConfig gbStorageConfig);

    /**
     * 删除存储配置
     * 
     * @param id 配置主键
     * @return 结果
     */
    public int deleteGbStorageConfigById(Long id);

    /**
     * 批量删除存储配置
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGbStorageConfigByIds(Long[] ids);
}
