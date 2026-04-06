package com.ruoyi.gamebox.service;

import java.util.List;
import com.ruoyi.gamebox.domain.GbBoxFieldMapping;

/**
 * 字段映射配置Service接口
 * 
 * @author ruoyi
 * @date 2026-01-25
 */
public interface IGbBoxFieldMappingService
{
    /**
     * 查询字段映射配置
     * 
     * @param id 字段映射配置主键
     * @return 字段映射配置
     */
    public GbBoxFieldMapping selectGbBoxFieldMappingById(Long id);

    /**
     * 查询字段映射配置列表
     * 
     * @param GbBoxFieldMapping 字段映射配置
     * @return 字段映射配置集合
     */
    public List<GbBoxFieldMapping> selectGbBoxFieldMappingList(GbBoxFieldMapping GbBoxFieldMapping);

    /**
     * 根据平台和资源类型查询字段映射配�?
     * 
     * @param platform 平台标识
     * @param resourceType 资源类型
     * @return 字段映射配置集合
     */
    public List<GbBoxFieldMapping> selectByPlatformAndType(String platform, String resourceType);

    /**
     * 根据盒子ID查询字段映射配置
     * 
     * @param boxId 盒子ID
     * @return 字段映射配置集合
     */
    public List<GbBoxFieldMapping> selectByBoxId(Long boxId);

    /**
     * 删除指定盒子的所有字段映射配�?
     * 
     * @param boxId 盒子ID
     * @return 结果
     */
    public int deleteByBoxId(Long boxId);

    /**
     * 新增字段映射配置
     * 
     * @param GbBoxFieldMapping 字段映射配置
     * @return 结果
     */
    public int insertGbBoxFieldMapping(GbBoxFieldMapping GbBoxFieldMapping);

    /**
     * 批量新增字段映射配置
     * 
     * @param list 字段映射配置列表
     * @return 结果
     */
    public int batchInsertGbBoxFieldMapping(List<GbBoxFieldMapping> list);

    /**
     * 批量更新字段映射配置
     * 
     * @param list 字段映射配置列表
     * @return 结果
     */
    public int batchUpdateGbBoxFieldMapping(List<GbBoxFieldMapping> list);

    /**
     * 修改字段映射配置
     * 
     * @param GbBoxFieldMapping 字段映射配置
     * @return 结果
     */
    public int updateGbBoxFieldMapping(GbBoxFieldMapping GbBoxFieldMapping);

    /**
     * 批量删除字段映射配置
     * 
     * @param ids 需要删除的字段映射配置主键集合
     * @return 结果
     */
    public int deleteGbBoxFieldMappingByIds(Long[] ids);

    /**
     * 删除字段映射配置信息
     * 
     * @param id 字段映射配置主键
     * @return 结果
     */
    public int deleteGbBoxFieldMappingById(Long id);

    /**
     * 批量导入字段映射配置
     * 
     * @param mappings 字段映射配置列表
     * @param updateSupport 是否支持更新已存在的配置
     * @return 导入结果消息
     */
    public String importFieldMappings(List<GbBoxFieldMapping> mappings, boolean updateSupport);

    /**
     * 导出字段映射配置为JSON格式
     * 
     * @param platform 平台标识
     * @param resourceType 资源类型
     * @return JSON字符�?
     */
    public String exportFieldMappingsAsJson(String platform, String resourceType);

    /**
     * 获取指定表字段的所有不同值
     * 
     * @param tableName 表名
     * @param fieldName 字段名
     * @return 字段的所有不同值列表
     */
    public List<String> getFieldDistinctValues(String tableName, String fieldName);
}
