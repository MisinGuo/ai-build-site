package com.ruoyi.gamebox.service;

import java.util.List;
import com.ruoyi.gamebox.domain.GbGameCategoryRelation;
import com.ruoyi.gamebox.domain.GbCategory;

/**
 * 游戏分类关联Service接口
 * 
 * @author ruoyi
 * @date 2025-12-25
 */
public interface IGbGameCategoryRelationService 
{
    /**
     * 查询游戏分类关联
     * 
     * @param id 游戏分类关联主键
     * @return 游戏分类关联
     */
    public GbGameCategoryRelation selectGbGameCategoryRelationById(Long id);

    /**
     * 查询游戏分类关联列表
     * 
     * @param gbGameCategoryRelation 游戏分类关联
     * @return 游戏分类关联集合
     */
    public List<GbGameCategoryRelation> selectGbGameCategoryRelationList(GbGameCategoryRelation gbGameCategoryRelation);

    /**
     * 查询游戏的所有分类关联（包含分类详情）
     * 
     * @param gameId 游戏ID
     * @return 游戏分类关联列表（包含分类名称、图标等信息）
     */
    public List<GbGameCategoryRelation> selectCategoriesByGameId(Long gameId);

    /**
     * 批量查询多个游戏的分类关联（导入判重去重专用，避免 N+1 查询）
     *
     * @param gameIds 游戏ID列表
     * @return 分类关联列表（含 game_id）
     */
    public List<GbGameCategoryRelation> selectCategoriesByGameIds(List<Long> gameIds);

    /**
     * 查询游戏的所有分类ID
     * 
     * @param gameId 游戏ID
     * @return 分类ID列表
     */
    public List<Long> selectCategoryIdsByGameId(Long gameId);

    /**
     * 查询分类下的所有游戏ID
     * 
     * @param categoryId 分类ID
     * @return 游戏ID列表
     */
    public List<Long> selectGameIdsByCategoryId(Long categoryId);

    /**
     * 查询游戏的主分类
     * 
     * @param gameId 游戏ID
     * @return 主分类
     */
    public GbCategory selectPrimaryCategoryByGameId(Long gameId);

    /**
     * 新增游戏分类关联
     * 
     * @param gbGameCategoryRelation 游戏分类关联
     * @return 结果
     */
    public int insertGbGameCategoryRelation(GbGameCategoryRelation gbGameCategoryRelation);

    /**
     * 修改游戏分类关联
     * 
     * @param gbGameCategoryRelation 游戏分类关联
     * @return 结果
     */
    public int updateGbGameCategoryRelation(GbGameCategoryRelation gbGameCategoryRelation);

    /**
     * 批量删除游戏分类关联
     * 
     * @param ids 需要删除的游戏分类关联主键集合
     * @return 结果
     */
    public int deleteGbGameCategoryRelationByIds(Long[] ids);

    /**
     * 删除游戏分类关联信息
     * 
     * @param id 游戏分类关联主键
     * @return 结果
     */
    public int deleteGbGameCategoryRelationById(Long id);

    /**
     * 保存游戏的分类关联（删除旧的，新增新的）
     * 
     * @param gameId 游戏ID
     * @param relations 分类关联列表
     * @return 结果
     */
    public int saveGameCategoryRelations(Long gameId, List<GbGameCategoryRelation> relations);

    /**
     * 追加游戏的分类关联（合并而不是覆盖）
     * 如果分类已存在则跳过，不存在则追加
     * 
     * @param gameId 游戏ID
     * @param relations 要追加的分类关联列表
     * @return 新增的关联数
     */
    public int appendGameCategoryRelations(Long gameId, List<GbGameCategoryRelation> relations);

    /**
     * 更新游戏的主分类
     * 
     * @param gameId 游戏ID
     * @param categoryId 分类ID
     * @return 结果
     */
    public int updatePrimaryCategory(Long gameId, Long categoryId);

    /**
     * 检查游戏和分类是否已关联
     * 
     * @param gameId 游戏ID
     * @param categoryId 分类ID
     * @return true-已关联 false-未关联
     */
    public boolean checkRelationExists(Long gameId, Long categoryId);

    /**
     * 批量删除多个游戏的分类关联
     * 
     * @param gameIds 游戏ID列表
     * @return 删除的记录数
     */
    public int batchDeleteByGameIds(List<Long> gameIds);

    /**
     * 批量插入分类关联
     * 
     * @param relations 关联记录列表
     * @return 插入的记录数
     */
    public int batchInsertRelations(List<GbGameCategoryRelation> relations);

    /**
     * 批量追加分类关联（自动去重）
     * 
     * @param relations 关联记录列表
     * @return 新增的记录数
     */
    public int batchAppendRelations(List<GbGameCategoryRelation> relations);
}
