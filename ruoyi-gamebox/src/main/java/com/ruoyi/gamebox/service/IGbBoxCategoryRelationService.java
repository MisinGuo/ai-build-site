package com.ruoyi.gamebox.service;

import java.util.List;
import com.ruoyi.gamebox.domain.GbBoxCategoryRelation;
import com.ruoyi.gamebox.domain.GbCategory;

/**
 * 游戏盒子分类关联Service接口
 * 
 * @author ruoyi
 * @date 2025-12-25
 */
public interface IGbBoxCategoryRelationService 
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
     * 查询盒子的所有分类关联（包含分类详情）
     * 
     * @param boxId 盒子ID
     * @return 盒子分类关联列表（包含分类名称、图标等信息）
     */
    public List<GbBoxCategoryRelation> selectCategoriesByBoxId(Long boxId);

    /**
     * 查询盒子的所有分类ID
     * 
     * @param boxId 盒子ID
     * @return 分类ID列表
     */
    public List<Long> selectCategoryIdsByBoxId(Long boxId);

    /**
     * 查询分类下的所有盒子ID
     * 
     * @param categoryId 分类ID
     * @return 盒子ID列表
     */
    public List<Long> selectBoxIdsByCategoryId(Long categoryId);

    /**
     * 查询盒子的主分类
     * 
     * @param boxId 盒子ID
     * @return 主分类
     */
    public GbCategory selectPrimaryCategoryByBoxId(Long boxId);

    /**
     * 新增盒子分类关联
     * 
     * @param gbBoxCategoryRelation 盒子分类关联
     * @return 结果
     */
    public int insertGbBoxCategoryRelation(GbBoxCategoryRelation gbBoxCategoryRelation);

    /**
     * 修改盒子分类关联
     * 
     * @param gbBoxCategoryRelation 盒子分类关联
     * @return 结果
     */
    public int updateGbBoxCategoryRelation(GbBoxCategoryRelation gbBoxCategoryRelation);

    /**
     * 批量删除盒子分类关联
     * 
     * @param ids 需要删除的盒子分类关联主键集合
     * @return 结果
     */
    public int deleteGbBoxCategoryRelationByIds(Long[] ids);

    /**
     * 删除盒子分类关联信息
     * 
     * @param id 盒子分类关联主键
     * @return 结果
     */
    public int deleteGbBoxCategoryRelationById(Long id);

    /**
     * 保存盒子的分类关联（删除旧的，新增新的）
     * 
     * @param boxId 盒子ID
     * @param relations 分类关联列表
     * @return 结果
     */
    public int saveBoxCategories(Long boxId, List<GbBoxCategoryRelation> relations);

    /**
     * 删除盒子的所有分类关联
     * 
     * @param boxId 盒子ID
     * @return 结果
     */
    public int deleteByBoxId(Long boxId);

    /**
     * 删除分类的所有盒子关联
     * 
     * @param categoryId 分类ID
     * @return 结果
     */
    public int deleteByCategoryId(Long categoryId);
}
