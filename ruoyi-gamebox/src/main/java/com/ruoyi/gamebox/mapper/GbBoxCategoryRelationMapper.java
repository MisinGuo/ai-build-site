package com.ruoyi.gamebox.mapper;

import java.util.List;
import com.ruoyi.gamebox.domain.GbBoxCategoryRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 游戏盒子分类关联Mapper接口
 * 
 * @author ruoyi
 * @date 2025-12-25
 */
@Mapper
public interface GbBoxCategoryRelationMapper 
{
    /**
     * 查询盒子分类关联
     * 
     * @param id 盒子分类关联主键
     * @return 盒子分类关联
     */
    public GbBoxCategoryRelation selectGbBoxCategoryRelationById(Long id);

    /**
     * 查询盒子分类关联列表
     * 
     * @param gbBoxCategoryRelation 盒子分类关联
     * @return 盒子分类关联集合
     */
    public List<GbBoxCategoryRelation> selectGbBoxCategoryRelationList(GbBoxCategoryRelation gbBoxCategoryRelation);

    /**
     * 查询盒子的所有分类（带分类详情）
     * 
     * @param boxId 盒子ID
     * @return 分类关联列表
     */
    public List<GbBoxCategoryRelation> selectCategoriesByBoxId(@Param("boxId") Long boxId);

    /**
     * 查询分类下的所有盒子ID
     * 
     * @param categoryId 分类ID
     * @return 盒子ID列表
     */
    public List<Long> selectBoxIdsByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 查询盒子的主分类
     * 
     * @param boxId 盒子ID
     * @return 主分类关联
     */
    public GbBoxCategoryRelation selectPrimaryCategoryByBoxId(@Param("boxId") Long boxId);

    /**
     * 新增盒子分类关联
     * 
     * @param gbBoxCategoryRelation 盒子分类关联
     * @return 结果
     */
    public int insertGbBoxCategoryRelation(GbBoxCategoryRelation gbBoxCategoryRelation);

    /**
     * 批量新增盒子分类关联
     * 
     * @param relations 关联列表
     * @return 结果
     */
    public int batchInsertGbBoxCategoryRelation(@Param("relations") List<GbBoxCategoryRelation> relations);

    /**
     * 修改盒子分类关联
     * 
     * @param gbBoxCategoryRelation 盒子分类关联
     * @return 结果
     */
    public int updateGbBoxCategoryRelation(GbBoxCategoryRelation gbBoxCategoryRelation);

    /**
     * 删除盒子分类关联
     * 
     * @param id 盒子分类关联主键
     * @return 结果
     */
    public int deleteGbBoxCategoryRelationById(Long id);

    /**
     * 批量删除盒子分类关联
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGbBoxCategoryRelationByIds(Long[] ids);

    /**
     * 删除盒子的所有分类关联
     * 
     * @param boxId 盒子ID
     * @return 结果
     */
    public int deleteByBoxId(@Param("boxId") Long boxId);

    /**
     * 删除分类的所有盒子关联
     * 
     * @param categoryId 分类ID
     * @return 结果
     */
    public int deleteByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 删除指定盒子的指定分类关联
     * 
     * @param boxId 盒子ID
     * @param categoryId 分类ID
     * @return 结果
     */
    public int deleteByBoxIdAndCategoryId(@Param("boxId") Long boxId, @Param("categoryId") Long categoryId);

    /**
     * 检查盒子是否已关联该分类
     * 
     * @param boxId 盒子ID
     * @param categoryId 分类ID
     * @return 结果
     */
    public int checkBoxCategoryExists(@Param("boxId") Long boxId, @Param("categoryId") Long categoryId);

    /**
     * 取消盒子的所有主分类标记
     * 
     * @param boxId 盒子ID
     * @return 结果
     */
    public int cancelPrimaryCategory(@Param("boxId") Long boxId);

    /**
     * 设置盒子的主分类
     * 
     * @param boxId 盒子ID
     * @param categoryId 分类ID
     * @return 结果
     */
    public int setPrimaryCategory(@Param("boxId") Long boxId, @Param("categoryId") Long categoryId);
}
