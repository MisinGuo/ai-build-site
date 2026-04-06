package com.ruoyi.gamebox.service.impl;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.ruoyi.gamebox.domain.GbCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.gamebox.mapper.GbGameCategoryRelationMapper;
import com.ruoyi.gamebox.domain.GbGameCategoryRelation;
import com.ruoyi.gamebox.service.IGbGameCategoryRelationService;

/**
 * 游戏分类关联Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-12-25
 */
@Service
public class GbGameCategoryRelationServiceImpl implements IGbGameCategoryRelationService 
{
    @Autowired
    private GbGameCategoryRelationMapper gbGameCategoryRelationMapper;

    /**
     * 查询游戏分类关联
     * 
     * @param id 游戏分类关联主键
     * @return 游戏分类关联
     */
    @Override
    public GbGameCategoryRelation selectGbGameCategoryRelationById(Long id)
    {
        return gbGameCategoryRelationMapper.selectGbGameCategoryRelationById(id);
    }

    /**
     * 查询游戏分类关联列表
     * 
     * @param gbGameCategoryRelation 游戏分类关联
     * @return 游戏分类关联
     */
    @Override
    public List<GbGameCategoryRelation> selectGbGameCategoryRelationList(GbGameCategoryRelation gbGameCategoryRelation)
    {
        return gbGameCategoryRelationMapper.selectGbGameCategoryRelationList(gbGameCategoryRelation);
    }

    /**
     * 查询游戏的所有分类关联（包含分类详情）
     * 
     * @param gameId 游戏ID
     * @return 游戏分类关联列表（包含分类名称、图标等信息）
     */
    @Override
    public List<GbGameCategoryRelation> selectCategoriesByGameId(Long gameId)
    {
        return gbGameCategoryRelationMapper.selectCategoriesByGameId(gameId);
    }

    /**
     * 批量查询多个游戏的分类关联（导入判重去重专用，避免 N+1 查询）
     */
    @Override
    public List<GbGameCategoryRelation> selectCategoriesByGameIds(List<Long> gameIds)
    {
        if (gameIds == null || gameIds.isEmpty()) return java.util.Collections.emptyList();
        return gbGameCategoryRelationMapper.selectCategoriesByGameIds(gameIds);
    }

    /**
     * 查询游戏的所有分类ID
     * 
     * @param gameId 游戏ID
     * @return 分类ID列表
     */
    @Override
    public List<Long> selectCategoryIdsByGameId(Long gameId)
    {
        List<GbGameCategoryRelation> relations = gbGameCategoryRelationMapper.selectCategoriesByGameId(gameId);
        return relations.stream().map(GbGameCategoryRelation::getCategoryId).collect(Collectors.toList());
    }

    /**
     * 查询分类下的所有游戏ID
     * 
     * @param categoryId 分类ID
     * @return 游戏ID列表
     */
    @Override
    public List<Long> selectGameIdsByCategoryId(Long categoryId)
    {
        return gbGameCategoryRelationMapper.selectGameIdsByCategoryId(categoryId);
    }

    /**
     * 查询游戏的主分类
     * 
     * @param gameId 游戏ID
     * @return 主分类
     */
    @Override
    public GbCategory selectPrimaryCategoryByGameId(Long gameId)
    {
        GbGameCategoryRelation relation = gbGameCategoryRelationMapper.selectPrimaryCategoryByGameId(gameId);
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
     * 新增游戏分类关联
     * 
     * @param gbGameCategoryRelation 游戏分类关联
     * @return 结果
     */
    @Override
    public int insertGbGameCategoryRelation(GbGameCategoryRelation gbGameCategoryRelation)
    {
        return gbGameCategoryRelationMapper.insertGbGameCategoryRelation(gbGameCategoryRelation);
    }

    /**
     * 修改游戏分类关联
     * 
     * @param gbGameCategoryRelation 游戏分类关联
     * @return 结果
     */
    @Override
    public int updateGbGameCategoryRelation(GbGameCategoryRelation gbGameCategoryRelation)
    {
        return gbGameCategoryRelationMapper.updateGbGameCategoryRelation(gbGameCategoryRelation);
    }

    /**
     * 批量删除游戏分类关联
     * 
     * @param ids 需要删除的游戏分类关联主键
     * @return 结果
     */
    @Override
    public int deleteGbGameCategoryRelationByIds(Long[] ids)
    {
        return gbGameCategoryRelationMapper.deleteGbGameCategoryRelationByIds(ids);
    }

    /**
     * 删除游戏分类关联信息
     * 
     * @param id 游戏分类关联主键
     * @return 结果
     */
    @Override
    public int deleteGbGameCategoryRelationById(Long id)
    {
        return gbGameCategoryRelationMapper.deleteGbGameCategoryRelationById(id);
    }

    /**
     * 保存游戏的分类关联（删除旧的，新增新的）
     * 
     * @param gameId 游戏ID
     * @param relations 分类关联列表
     * @return 结果
     */
    @Override
    @Transactional
    public int saveGameCategoryRelations(Long gameId, List<GbGameCategoryRelation> relations)
    {
        // 1. 删除旧的关联
        gbGameCategoryRelationMapper.deleteGbGameCategoryRelationByGameId(gameId);

        // 2. 如果没有分类，直接返回
        if (relations == null || relations.isEmpty())
        {
            return 0;
        }

        // 3. 设置sortOrder和时间
        for (int i = 0; i < relations.size(); i++)
        {
            GbGameCategoryRelation relation = relations.get(i);
            relation.setGameId(gameId);
            relation.setSortOrder(i);
            relation.setCreateTime(new Date());
        }

        // 4. 批量插入
        return gbGameCategoryRelationMapper.batchInsertGbGameCategoryRelation(relations);
    }

    /**
     * 追加游戏的分类关联（合并而不是覆盖）
     * 如果分类已存在则跳过，不存在则追加
     * 
     * @param gameId 游戏ID
     * @param relations 要追加的分类关联列表
     * @return 新增的关联数
     */
    @Override
    @Transactional
    public int appendGameCategoryRelations(Long gameId, List<GbGameCategoryRelation> relations)
    {
        // 1. 如果没有要追加的分类，直接返回
        if (relations == null || relations.isEmpty())
        {
            return 0;
        }

        // 2. 查询已有的分类ID列表
        List<Long> existingCategoryIds = gbGameCategoryRelationMapper.selectCategoryIdsByGameId(gameId);
        
        // 3. 过滤掉已存在的分类，只保留新分类
        List<GbGameCategoryRelation> newRelations = relations.stream()
                .filter(relation -> !existingCategoryIds.contains(relation.getCategoryId()))
                .collect(Collectors.toList());
        
        // 4. 如果没有新分类需要添加，直接返回
        if (newRelations.isEmpty())
        {
            return 0;
        }
        
        // 5. 获取当前最大的sortOrder，新分类追加在后面
        int maxSortOrder = existingCategoryIds.size();
        
        // 6. 设置sortOrder和时间
        for (int i = 0; i < newRelations.size(); i++)
        {
            GbGameCategoryRelation relation = newRelations.get(i);
            relation.setGameId(gameId);
            relation.setSortOrder(maxSortOrder + i);
            relation.setCreateTime(new Date());
            // 追加的分类不应该是主分类（除非当前游戏没有任何分类）
            if (existingCategoryIds.isEmpty() && i == 0)
            {
                relation.setIsPrimary("1");
            }
            else
            {
                relation.setIsPrimary("0");
            }
        }

        // 7. 批量插入新关联
        return gbGameCategoryRelationMapper.batchInsertGbGameCategoryRelation(newRelations);
    }

    /**
     * 更新游戏的主分类
     * 
     * @param gameId 游戏ID
     * @param categoryId 分类ID
     * @return 结果
     */
    @Override
    public int updatePrimaryCategory(Long gameId, Long categoryId)
    {
        return gbGameCategoryRelationMapper.updatePrimaryCategory(gameId, categoryId);
    }

    /**
     * 检查游戏和分类是否已关联
     * 
     * @param gameId 游戏ID
     * @param categoryId 分类ID
     * @return true-已关联 false-未关联
     */
    @Override
    public boolean checkRelationExists(Long gameId, Long categoryId)
    {
        GbGameCategoryRelation relation = gbGameCategoryRelationMapper.checkRelationExists(gameId, categoryId);
        return relation != null;
    }

    /**
     * 批量删除游戏的分类关联（优化版：分批处理）
     * 
     * @param gameIds 游戏ID列表
     * @return 删除的记录数
     */
    @Override
    @Transactional
    public int batchDeleteByGameIds(List<Long> gameIds)
    {
        if (gameIds == null || gameIds.isEmpty()) {
            return 0;
        }
        
        // 分批处理，每批最多200个ID
        final int BATCH_SIZE = 200;
        int totalDeleted = 0;
        int totalIds = gameIds.size();
        
        for (int i = 0; i < totalIds; i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, totalIds);
            List<Long> batch = gameIds.subList(i, end);
            totalDeleted += gbGameCategoryRelationMapper.batchDeleteByGameIds(batch);
        }
        
        return totalDeleted;
    }

    /**
     * 批量插入分类关联（优化版：分批处理）
     * 
     * @param relations 关联记录列表
     * @return 插入的记录数
     */
    @Override
    @Transactional
    public int batchInsertRelations(List<GbGameCategoryRelation> relations)
    {
        if (relations == null || relations.isEmpty()) {
            return 0;
        }

        // 先按(game_id, category_id)去重，避免同一批次内重复导致唯一键冲突
        List<GbGameCategoryRelation> deduplicated = new ArrayList<>();
        Set<String> uniqueKeys = new HashSet<>();
        for (GbGameCategoryRelation relation : relations) {
            if (relation == null || relation.getGameId() == null || relation.getCategoryId() == null) {
                continue;
            }
            String key = relation.getGameId() + "-" + relation.getCategoryId();
            if (uniqueKeys.add(key)) {
                deduplicated.add(relation);
            }
        }

        if (deduplicated.isEmpty()) {
            return 0;
        }

        // 过滤数据库中已存在的关联，保证导入幂等
        List<GbGameCategoryRelation> relationsToInsert = deduplicated.stream()
                .filter(relation -> !checkRelationExists(relation.getGameId(), relation.getCategoryId()))
                .collect(Collectors.toList());

        if (relationsToInsert.isEmpty()) {
            return 0;
        }
        
        // 设置创建时间
        Date now = new Date();
        for (GbGameCategoryRelation relation : relationsToInsert) {
            relation.setCreateTime(now);
        }
        
        // 分批处理，每批最多200条
        final int BATCH_SIZE = 200;
        int totalInserted = 0;
        int totalRelations = relationsToInsert.size();
        
        for (int i = 0; i < totalRelations; i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, totalRelations);
            List<GbGameCategoryRelation> batch = relationsToInsert.subList(i, end);
            totalInserted += gbGameCategoryRelationMapper.batchInsertGbGameCategoryRelation(batch);
        }
        
        return totalInserted;
    }

    /**
     * 批量追加分类关联（自动去重）
     * 
     * @param relations 关联记录列表
     * @return 新增的记录数
     */
    @Override
    @Transactional
    public int batchAppendRelations(List<GbGameCategoryRelation> relations)
    {
        if (relations == null || relations.isEmpty()) {
            return 0;
        }
        
        // 过滤掉已存在的关联
        List<GbGameCategoryRelation> newRelations = new ArrayList<>();
        for (GbGameCategoryRelation relation : relations) {
            if (!checkRelationExists(relation.getGameId(), relation.getCategoryId())) {
                newRelations.add(relation);
            }
        }
        
        if (newRelations.isEmpty()) {
            return 0;
        }
        
        // 批量插入新关联
        return batchInsertRelations(newRelations);
    }
}
