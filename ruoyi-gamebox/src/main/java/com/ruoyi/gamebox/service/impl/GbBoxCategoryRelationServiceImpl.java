package com.ruoyi.gamebox.service.impl;


import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.ruoyi.gamebox.domain.GbCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.gamebox.mapper.GbBoxCategoryRelationMapper;
import com.ruoyi.gamebox.domain.GbBoxCategoryRelation;
import com.ruoyi.gamebox.service.IGbBoxCategoryRelationService;

/**
 * 游戏盒子分类关联Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-12-25
 */
@Service
public class GbBoxCategoryRelationServiceImpl implements IGbBoxCategoryRelationService 
{
    @Autowired
    private GbBoxCategoryRelationMapper gbBoxCategoryRelationMapper;

    /**
     * 查询盒子分类关联
     * 
     * @param id 盒子分类关联主键
     * @return 盒子分类关联
     */
    @Override
    public GbBoxCategoryRelation selectGbBoxCategoryRelationById(Long id)
    {
        return gbBoxCategoryRelationMapper.selectGbBoxCategoryRelationById(id);
    }

    /**
     * 查询盒子分类关联列表
     * 
     * @param gbBoxCategoryRelation 盒子分类关联
     * @return 盒子分类关联
     */
    @Override
    public List<GbBoxCategoryRelation> selectGbBoxCategoryRelationList(GbBoxCategoryRelation gbBoxCategoryRelation)
    {
        return gbBoxCategoryRelationMapper.selectGbBoxCategoryRelationList(gbBoxCategoryRelation);
    }

    /**
     * 查询盒子的所有分类关联（包含分类详情）
     * 
     * @param boxId 盒子ID
     * @return 盒子分类关联列表（包含分类名称、图标等信息）
     */
    @Override
    public List<GbBoxCategoryRelation> selectCategoriesByBoxId(Long boxId)
    {
        return gbBoxCategoryRelationMapper.selectCategoriesByBoxId(boxId);
    }

    /**
     * 查询盒子的所有分类ID
     * 
     * @param boxId 盒子ID
     * @return 分类ID列表
     */
    @Override
    public List<Long> selectCategoryIdsByBoxId(Long boxId)
    {
        List<GbBoxCategoryRelation> relations = gbBoxCategoryRelationMapper.selectCategoriesByBoxId(boxId);
        return relations.stream().map(GbBoxCategoryRelation::getCategoryId).collect(Collectors.toList());
    }

    /**
     * 查询分类下的所有盒子ID
     * 
     * @param categoryId 分类ID
     * @return 盒子ID列表
     */
    @Override
    public List<Long> selectBoxIdsByCategoryId(Long categoryId)
    {
        return gbBoxCategoryRelationMapper.selectBoxIdsByCategoryId(categoryId);
    }

    /**
     * 查询盒子的主分类
     * 
     * @param boxId 盒子ID
     * @return 主分类
     */
    @Override
    public GbCategory selectPrimaryCategoryByBoxId(Long boxId)
    {
        GbBoxCategoryRelation relation = gbBoxCategoryRelationMapper.selectPrimaryCategoryByBoxId(boxId);
        if (relation != null)
        {
            GbCategory category = new GbCategory();
            category.setId(relation.getCategoryId());
            category.setName(relation.getCategoryName());
            category.setSlug(relation.getCategorySlug());
            category.setIcon(relation.getCategoryIcon());
            return category;
        }
        return null;
    }

    /**
     * 新增盒子分类关联
     * 
     * @param gbBoxCategoryRelation 盒子分类关联
     * @return 结果
     */
    @Override
    public int insertGbBoxCategoryRelation(GbBoxCategoryRelation gbBoxCategoryRelation)
    {
        return gbBoxCategoryRelationMapper.insertGbBoxCategoryRelation(gbBoxCategoryRelation);
    }

    /**
     * 修改盒子分类关联
     * 
     * @param gbBoxCategoryRelation 盒子分类关联
     * @return 结果
     */
    @Override
    public int updateGbBoxCategoryRelation(GbBoxCategoryRelation gbBoxCategoryRelation)
    {
        return gbBoxCategoryRelationMapper.updateGbBoxCategoryRelation(gbBoxCategoryRelation);
    }

    /**
     * 批量删除盒子分类关联
     * 
     * @param ids 需要删除的盒子分类关联主键
     * @return 结果
     */
    @Override
    public int deleteGbBoxCategoryRelationByIds(Long[] ids)
    {
        return gbBoxCategoryRelationMapper.deleteGbBoxCategoryRelationByIds(ids);
    }

    /**
     * 删除盒子分类关联信息
     * 
     * @param id 盒子分类关联主键
     * @return 结果
     */
    @Override
    public int deleteGbBoxCategoryRelationById(Long id)
    {
        return gbBoxCategoryRelationMapper.deleteGbBoxCategoryRelationById(id);
    }

    /**
     * 保存盒子的分类关联（删除旧的，新增新的）
     * 
     * @param boxId 盒子ID
     * @param relations 分类关联列表
     * @return 结果
     */
    @Override
    @Transactional
    public int saveBoxCategories(Long boxId, List<GbBoxCategoryRelation> relations)
    {
        // 1. 删除旧的关联
        gbBoxCategoryRelationMapper.deleteByBoxId(boxId);

        // 2. 如果没有分类，直接返回
        if (relations == null || relations.isEmpty())
        {
            return 0;
        }

        // 3. 设置sortOrder和时间
        for (int i = 0; i < relations.size(); i++)
        {
            GbBoxCategoryRelation relation = relations.get(i);
            relation.setBoxId(boxId);
            relation.setSortOrder(i);
            relation.setCreateTime(new Date());
        }

        // 4. 批量插入
        return gbBoxCategoryRelationMapper.batchInsertGbBoxCategoryRelation(relations);
    }

    /**
     * 删除盒子的所有分类关联
     * 
     * @param boxId 盒子ID
     * @return 结果
     */
    @Override
    public int deleteByBoxId(Long boxId)
    {
        return gbBoxCategoryRelationMapper.deleteByBoxId(boxId);
    }

    /**
     * 删除分类的所有盒子关联
     * 
     * @param categoryId 分类ID
     * @return 结果
     */
    @Override
    public int deleteByCategoryId(Long categoryId)
    {
        return gbBoxCategoryRelationMapper.deleteByCategoryId(categoryId);
    }
}
