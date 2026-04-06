package com.ruoyi.gamebox.service;

import java.util.List;
import java.util.Map;

/**
 * 字段映射应用服务接口
 * 用于根据字段映射配置动态转换不同平台的数据
 * 
 * @author ruoyi
 * @date 2026-01-25
 */
public interface IFieldMappingApplyService {
    
    /**
     * 应用字段映射，将源数据转换为系统标准格式
     * 
     * @param sourceData 源数据（来自不同平台的JSON数据）
     * @param platformCode 平台代码（u2game/milu/277sy/dongyouxi）
     * @param resourceType 资源类型（game/box）
     * @return 转换后的数据，包含主表字段和扩展表字段
     */
    Map<String, Object> applyFieldMappings(Map<String, Object> sourceData, String platformCode, String resourceType);
    
    /**
     * 批量应用字段映射
     * 
     * @param sourceDataList 源数据列表
     * @param platformCode 平台代码
     * @param resourceType 资源类型
     * @return 转换后的数据列表
     */
    List<Map<String, Object>> batchApplyFieldMappings(List<Map<String, Object>> sourceDataList, 
                                                       String platformCode, 
                                                       String resourceType);
    
    /**
     * 获取支持的平台列表
     * 
     * @return 平台代码列表
     */
    List<String> getSupportedPlatforms();
    
    /**
     * 验证平台数据格式
     * 
     * @param sourceData 源数据
     * @param platformCode 平台代码
     * @param resourceType 资源类型
     * @return 验证结果消息，如果验证通过返回null
     */
    String validatePlatformData(Map<String, Object> sourceData, String platformCode, String resourceType);
    
    /**
     * 应用盒子字段映射，将源数据转换为系统标准格式
     * 
     * @param sourceData 源数据（来自不同平台的JSON数据）
     * @param boxId 盒子ID
     * @param resourceType 资源类型（game/box）
     * @return 转换后的数据，包含主表字段和扩展表字段
     */
    Map<String, Object> applyBoxFieldMappings(Map<String, Object> sourceData, Long boxId, String resourceType);
    
    /**
     * 批量应用盒子字段映射
     * 
     * @param sourceDataList 源数据列表
     * @param boxId 盒子ID
     * @param resourceType 资源类型
     * @return 转换后的数据列表
     */
    List<Map<String, Object>> batchApplyBoxFieldMappings(List<Map<String, Object>> sourceDataList, 
                                                          Long boxId, 
                                                          String resourceType);
}
