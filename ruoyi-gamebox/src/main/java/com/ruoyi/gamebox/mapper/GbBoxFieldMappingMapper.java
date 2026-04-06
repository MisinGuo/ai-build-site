package com.ruoyi.gamebox.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.gamebox.domain.GbBoxFieldMapping;

/**
 * 盒子字段映射配置Mapper接口
 * 
 * @author ruoyi
 * @date 2026-01-25
 */
public interface GbBoxFieldMappingMapper 
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
     * @param gbBoxFieldMapping 字段映射配置
     * @return 字段映射配置集合
     */
    public List<GbBoxFieldMapping> selectGbBoxFieldMappingList(GbBoxFieldMapping gbBoxFieldMapping);

    /**
     * 根据平台和资源类型查询字段映射配置
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
     * 删除指定盒子的所有字段映射配置
     * 
     * @param boxId 盒子ID
     * @return 结果
     */
    public int deleteByBoxId(Long boxId);

    /**
     * 新增字段映射配置
     * 
     * @param gbBoxFieldMapping 字段映射配置
     * @return 结果
     */
    public int insertGbBoxFieldMapping(GbBoxFieldMapping gbBoxFieldMapping);

    /**
     * 修改字段映射配置
     * 
     * @param gbBoxFieldMapping 字段映射配置
     * @return 结果
     */
    public int updateGbBoxFieldMapping(GbBoxFieldMapping gbBoxFieldMapping);

    /**
     * 删除字段映射配置
     * 
     * @param id 字段映射配置主键
     * @return 结果
     */
    public int deleteGbBoxFieldMappingById(Long id);

    /**
     * 批量删除字段映射配置
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGbBoxFieldMappingByIds(Long[] ids);

    /**
     * 批量插入字段映射配置
     * 
     * @param mappings 字段映射配置列表
     * @return 结果
     */
    public int batchInsertGbBoxFieldMapping(List<GbBoxFieldMapping> mappings);

    /**
     * 批量更新字段映射配置
     * 
     * @param mappings 字段映射配置列表
     * @return 结果
     */
    public int batchUpdateGbBoxFieldMapping(List<GbBoxFieldMapping> mappings);

    /**
     * 检查字段映射是否存在
     * 
     * @param platform 平台标识
     * @param resourceType 资源类型
     * @param sourceField 源字段
     * @return 存在的记录ID，不存在返回null
     */
    public Long checkFieldMappingExists(String platform, String resourceType, String sourceField);

    /**
     * 查询指定表字段的所有不同值
     * 
     * @param tableName 表名
     * @param fieldName 字段名
     * @return 字段的所有不同值列表
     */
    public List<String> selectFieldDistinctValues(@Param("tableName") String tableName, @Param("fieldName") String fieldName);
}
