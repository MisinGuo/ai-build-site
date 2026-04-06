package com.ruoyi.gamebox.mapper;

import java.util.List;
import com.ruoyi.gamebox.domain.GbGameCategoryRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 游戏分类关联Mapper接口
 * 
 * @author ruoyi
 * @date 2025-12-25
 */
@Mapper
public interface GbGameCategoryRelationMapper 
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
     * 查询游戏的所有分类（带分类详情）
     * 
     * @param gameId 游戏ID
     * @return 分类关联列表
     */
    public List<GbGameCategoryRelation> selectCategoriesByGameId(@Param("gameId") Long gameId);

    /**
     * 批量查询多个游戏的分类（带分类详情），用于避免N+1查询
     *
     * @param gameIds 游戏ID列表
     * @return 分类关联列表（含 game_id）
     */
    public List<GbGameCategoryRelation> selectCategoriesByGameIds(@Param("gameIds") List<Long> gameIds);

    /**
     * 查询游戏的所有分类ID
     * 
     * @param gameId 游戏ID
     * @return 分类ID列表
     */
    public List<Long> selectCategoryIdsByGameId(@Param("gameId") Long gameId);

    /**
     * 查询分类下的所有游戏ID
     * 
     * @param categoryId 分类ID
     * @return 游戏ID列表
     */
    public List<Long> selectGameIdsByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 查询游戏的主分类
     * 
     * @param gameId 游戏ID
     * @return 主分类关联
     */
    public GbGameCategoryRelation selectPrimaryCategoryByGameId(@Param("gameId") Long gameId);

    /**
     * 新增游戏分类关联
     * 
     * @param gbGameCategoryRelation 游戏分类关联
     * @return 结果
     */
    public int insertGbGameCategoryRelation(GbGameCategoryRelation gbGameCategoryRelation);

    /**
     * 批量新增游戏分类关联
     * 
     * @param relations 关联列表
     * @return 结果
     */
    public int batchInsertGbGameCategoryRelation(@Param("relations") List<GbGameCategoryRelation> relations);

    /**
     * 修改游戏分类关联
     * 
     * @param gbGameCategoryRelation 游戏分类关联
     * @return 结果
     */
    public int updateGbGameCategoryRelation(GbGameCategoryRelation gbGameCategoryRelation);

    /**
     * 删除游戏分类关联
     * 
     * @param id 游戏分类关联主键
     * @return 结果
     */
    public int deleteGbGameCategoryRelationById(Long id);

    /**
     * 批量删除游戏分类关联
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGbGameCategoryRelationByIds(Long[] ids);

    /**
     * 删除游戏的所有分类关联
     * 
     * @param gameId 游戏ID
     * @return 结果
     */
    public int deleteGbGameCategoryRelationByGameId(@Param("gameId") Long gameId);

    /**
     * 删除分类的所有游戏关联
     * 
     * @param categoryId 分类ID
     * @return 结果
     */
    public int deleteGbGameCategoryRelationByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 更新游戏的主分类
     * 
     * @param gameId 游戏ID
     * @param categoryId 分类ID
     * @return 结果
     */
    public int updatePrimaryCategory(@Param("gameId") Long gameId, @Param("categoryId") Long categoryId);

    /**
     * 检查游戏和分类是否已关联
     * 
     * @param gameId 游戏ID
     * @param categoryId 分类ID
     * @return 关联记录
     */
    public GbGameCategoryRelation checkRelationExists(@Param("gameId") Long gameId, @Param("categoryId") Long categoryId);

    /**
     * 批量删除多个游戏的分类关联
     * 
     * @param gameIds 游戏ID列表
     * @return 删除的记录数
     */
    public int batchDeleteByGameIds(@Param("gameIds") List<Long> gameIds);
}
