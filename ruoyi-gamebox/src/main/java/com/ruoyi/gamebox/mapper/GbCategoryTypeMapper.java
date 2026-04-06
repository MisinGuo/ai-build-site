package com.ruoyi.gamebox.mapper;

import java.util.List;
import com.ruoyi.gamebox.domain.GbCategoryType;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分类类型配置Mapper接口
 * 
 * @author ruoyi
 */
@Mapper
public interface GbCategoryTypeMapper 
{
    /**
     * 查询分类类型配置
     * 
     * @param id 分类类型配置主键
     * @return 分类类型配置
     */
    public GbCategoryType selectGbCategoryTypeById(Long id);

    /**
     * 查询分类类型配置列表
     * 
     * @param gbCategoryType 分类类型配置
     * @return 分类类型配置集合
     */
    public List<GbCategoryType> selectGbCategoryTypeList(GbCategoryType gbCategoryType);

    /**
     * 新增分类类型配置
     * 
     * @param gbCategoryType 分类类型配置
     * @return 结果
     */
    public int insertGbCategoryType(GbCategoryType gbCategoryType);

    /**
     * 修改分类类型配置
     * 
     * @param gbCategoryType 分类类型配置
     * @return 结果
     */
    public int updateGbCategoryType(GbCategoryType gbCategoryType);

    /**
     * 删除分类类型配置
     * 
     * @param id 分类类型配置主键
     * @return 结果
     */
    public int deleteGbCategoryTypeById(Long id);

    /**
     * 批量删除分类类型配置
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGbCategoryTypeByIds(Long[] ids);
}
