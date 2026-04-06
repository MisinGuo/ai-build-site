package com.ruoyi.gamebox.service.impl;

import java.util.Date;
import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.gamebox.mapper.GbBoxFieldMappingMapper;
import com.ruoyi.gamebox.domain.GbBoxFieldMapping;
import com.ruoyi.gamebox.service.IGbBoxFieldMappingService;

/**
 * 字段映射配置Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-01-25
 */
@Service
public class GbBoxFieldMappingServiceImpl implements IGbBoxFieldMappingService 
{
    @Autowired
    private GbBoxFieldMappingMapper gbBoxFieldMappingMapper;

    /**
     * 查询字段映射配置
     * 
     * @param id 字段映射配置主键
     * @return 字段映射配置
     */
    @Override
    public GbBoxFieldMapping selectGbBoxFieldMappingById(Long id)
    {
        return gbBoxFieldMappingMapper
                .selectGbBoxFieldMappingById(id);
    }

    /**
     * 查询字段映射配置列表
     * 
     * @param GbBoxFieldMapping 字段映射配置
     * @return 字段映射配置
     */
    @Override
    public List<GbBoxFieldMapping> selectGbBoxFieldMappingList(GbBoxFieldMapping GbBoxFieldMapping)
    {
        return gbBoxFieldMappingMapper.selectGbBoxFieldMappingList(GbBoxFieldMapping);
    }

    /**
     * 根据平台和资源类型查询字段映射配置
     * 
     * @param platform 平台标识
     * @param resourceType 资源类型
     * @return 字段映射配置集合
     */
    @Override
    public List<GbBoxFieldMapping> selectByPlatformAndType(String platform, String resourceType)
    {
        return gbBoxFieldMappingMapper.selectByPlatformAndType(platform, resourceType);
    }

    /**
     * 根据盒子ID查询字段映射配置
     * 
     * @param boxId 盒子ID
     * @return 字段映射配置集合
     */
    @Override
    public List<GbBoxFieldMapping> selectByBoxId(Long boxId)
    {
        return gbBoxFieldMappingMapper.selectByBoxId(boxId);
    }

    /**
     * 删除指定盒子的所有字段映射配置
     * 
     * @param boxId 盒子ID
     * @return 结果
     */
    @Override
    public int deleteByBoxId(Long boxId)
    {
        return gbBoxFieldMappingMapper.deleteByBoxId(boxId);
    }

    /**
     * 新增字段映射配置
     * 
     * @param GbBoxFieldMapping 字段映射配置
     * @return 结果
     */
    @Override
    public int insertGbBoxFieldMapping(GbBoxFieldMapping GbBoxFieldMapping)
    {
        GbBoxFieldMapping.setCreateTime(DateUtils.getNowDate());
        return gbBoxFieldMappingMapper.insertGbBoxFieldMapping(GbBoxFieldMapping);
    }

    /**
     * 批量新增字段映射配置
     * 
     * @param list 字段映射配置列表
     * @return 结果
     */
    @Override
    public int batchInsertGbBoxFieldMapping(List<GbBoxFieldMapping> list)
    {
        if (list == null || list.isEmpty()) {
            return 0;
        }
        
        // 设置创建时间
        Date now = DateUtils.getNowDate();
        for (GbBoxFieldMapping mapping : list) {
            mapping.setCreateTime(now);
        }
        
        return gbBoxFieldMappingMapper.batchInsertGbBoxFieldMapping(list);
    }

    /**
     * 批量更新字段映射配置
     * 
     * @param list 字段映射配置列表
     * @return 结果
     */
    @Override
    public int batchUpdateGbBoxFieldMapping(List<GbBoxFieldMapping> list)
    {
        if (list == null || list.isEmpty()) {
            return 0;
        }
        
        return gbBoxFieldMappingMapper.batchUpdateGbBoxFieldMapping(list);
    }

    /**
     * 修改字段映射配置
     * 
     * @param GbBoxFieldMapping 字段映射配置
     * @return 结果
     */
    @Override
    public int updateGbBoxFieldMapping(GbBoxFieldMapping GbBoxFieldMapping)
    {
        GbBoxFieldMapping.setUpdateTime(DateUtils.getNowDate());
        return gbBoxFieldMappingMapper.updateGbBoxFieldMapping(GbBoxFieldMapping);
    }

    /**
     * 批量删除字段映射配置
     * 
     * @param ids 需要删除的字段映射配置主键
     * @return 结果
     */
    @Override
    public int deleteGbBoxFieldMappingByIds(Long[] ids)
    {
        return gbBoxFieldMappingMapper.deleteGbBoxFieldMappingByIds(ids);
    }

    /**
     * 删除字段映射配置信息
     * 
     * @param id 字段映射配置主键
     * @return 结果
     */
    @Override
    public int deleteGbBoxFieldMappingById(Long id)
    {
        return gbBoxFieldMappingMapper.deleteGbBoxFieldMappingById(id);
    }

    /**
     * 批量导入字段映射配置
     * 
     * @param mappings 字段映射配置列表
     * @param updateSupport 是否支持更新已存在的配置
     * @return 导入结果消息
     */
    @Override
    public String importFieldMappings(List<GbBoxFieldMapping> mappings, boolean updateSupport)
    {
        if (StringUtils.isNull(mappings) || mappings.size() == 0)
        {
            return "导入数据不能为空";
        }
        
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        
        for (GbBoxFieldMapping mapping : mappings)
        {
            try
            {
                // 检查是否已存在（基于 boxId + resourceType + sourceField）
                // 注意：导入时必须指定 boxId
                if (mapping.getBoxId() == null) {
                    failureNum++;
                    failureMsg.append("<br/>").append(failureNum).append("、字段 ")
                        .append(mapping.getSourceField()).append(" 导入失败：未指定盒子ID");
                    continue;
                }
                
                // 简单检查：查询相同 boxId 和 sourceField 的数据
                GbBoxFieldMapping query = new GbBoxFieldMapping();
                query.setBoxId(mapping.getBoxId());
                query.setResourceType(mapping.getResourceType());
                query.setSourceField(mapping.getSourceField());
                List<GbBoxFieldMapping> existList = gbBoxFieldMappingMapper.selectGbBoxFieldMappingList(query);
                
                if (existList == null || existList.isEmpty())
                if (existList == null || existList.isEmpty())
                {
                    // 不存在，新增
                    this.insertGbBoxFieldMapping(mapping);
                    successNum++;
                    successMsg.append("<br/>").append(successNum).append("、盒子 ")
                        .append(mapping.getBoxId()).append(" 的字段 ")
                        .append(mapping.getSourceField()).append(" 导入成功");
                }
                else if (updateSupport)
                {
                    // 存在且支持更新
                    mapping.setId(existList.get(0).getId());
                    this.updateGbBoxFieldMapping(mapping);
                    successNum++;
                    successMsg.append("<br/>").append(successNum).append("、盒子 ")
                        .append(mapping.getBoxId()).append(" 的字段 ")
                        .append(mapping.getSourceField()).append(" 更新成功");
                }
                else
                {
                    // 存在但不支持更新
                    failureNum++;
                    failureMsg.append("<br/>").append(failureNum).append("、盒子 ")
                        .append(mapping.getBoxId()).append(" 的字段 ")
                        .append(mapping.getSourceField()).append(" 已存在");
                }
            }
            catch (Exception e)
            {
                failureNum++;
                String msg = "<br/>" + failureNum + "、盒子 " + mapping.getBoxId() 
                    + " 的字段 " + mapping.getSourceField() + " 导入失败：";
                failureMsg.append(msg).append(e.getMessage());
            }
        }
        
        if (failureNum > 0)
        {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new RuntimeException(failureMsg.toString());
        }
        else
        {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }
        
        return successMsg.toString();
    }

    /**
     * 导出字段映射配置为JSON格式
     * 
     * @param platform 平台标识（已废弃，保留仅为兼容）
     * @param resourceType 资源类型
     * @return JSON字符串
     */
    @Override
    public String exportFieldMappingsAsJson(String platform, String resourceType)
    {
        GbBoxFieldMapping query = new GbBoxFieldMapping();
        if (StringUtils.isNotEmpty(resourceType))
        {
            query.setResourceType(resourceType);
        }
        
        List<GbBoxFieldMapping> list = gbBoxFieldMappingMapper.selectGbBoxFieldMappingList(query);
        return JSON.toJSONString(list, JSONWriter.Feature.PrettyFormat);
    }

    /**
     * 获取指定表字段的所有不同值
     * 
     * @param tableName 表名
     * @param fieldName 字段名
     * @return 字段的所有不同值列表
     */
    @Override
    public List<String> getFieldDistinctValues(String tableName, String fieldName)
    {
        return gbBoxFieldMappingMapper.selectFieldDistinctValues(tableName, fieldName);
    }
}
