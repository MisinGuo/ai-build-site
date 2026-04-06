package com.ruoyi.gamebox.service;

import java.util.List;
import com.ruoyi.gamebox.domain.GbBoxGameRelation;

/**
 * 游戏盒子-游戏关联Service接口
 * 
 * @author ruoyi
 */
public interface IGbBoxGameRelationService
{
    /**
     * 查询游戏盒子-游戏关联
     * 
     * @param id 游戏盒子-游戏关联主键
     * @return 游戏盒子-游戏关联
     */
    public GbBoxGameRelation selectGbBoxGameRelationById(Long id);

    /**
     * 查询游戏盒子-游戏关联列表
     * 
     * @param gbBoxGameRelation 游戏盒子-游戏关联
     * @return 游戏盒子-游戏关联集合
     */
    public List<GbBoxGameRelation> selectGbBoxGameRelationList(GbBoxGameRelation gbBoxGameRelation);

    /**
     * 根据盒子ID查询关联的游戏列表
     * 
     * @param boxId 盒子ID
     * @return 游戏关联列表
     */
    public List<GbBoxGameRelation> selectGamesByBoxId(Long boxId);

    /**
     * 根据游戏ID查询关联的盒子列表
     * 
     * @param gameId 游戏ID
     * @return 盒子关联列表
     */
    public List<GbBoxGameRelation> selectBoxesByGameId(Long gameId);

    /**
     * 根据游戏ID查询关联的盒子列表（按站点可见性过滤）
     *
     * @param gameId         游戏ID
     * @param siteId         当前站点ID
     * @return 盒子关联列表（仅返回当前站点可见的盒子）
     */
    public List<GbBoxGameRelation> selectBoxesByGameIdWithSite(Long gameId, Long siteId);

    /**
     * 新增游戏盒子-游戏关联
     * 
     * @param gbBoxGameRelation 游戏盒子-游戏关联
     * @return 结果
     */
    public int insertGbBoxGameRelation(GbBoxGameRelation gbBoxGameRelation);

    /**
     * 修改游戏盒子-游戏关联
     * 
     * @param gbBoxGameRelation 游戏盒子-游戏关联
     * @return 结果
     */
    public int updateGbBoxGameRelation(GbBoxGameRelation gbBoxGameRelation);

    /**
     * 批量删除游戏盒子-游戏关联
     * 
     * @param ids 需要删除的游戏盒子-游戏关联主键集合
     * @return 结果
     */
    public int deleteGbBoxGameRelationByIds(Long[] ids);

    /**
     * 删除游戏盒子-游戏关联信息
     * 
     * @param id 游戏盒子-游戏关联主键
     * @return 结果
     */
    public int deleteGbBoxGameRelationById(Long id);

    /**
     * 批量添加游戏到盒子
     * 
     * @param boxId 盒子ID
     * @param gameIds 游戏ID数组
     * @return 结果
     */
    public int batchAddGamesToBox(Long boxId, Long[] gameIds);

    /**
     * 批量插入盒子-游戏关联关系（支持关联字段数据）
     * 
     * @param relations 关联关系列表
     * @return 结果
     */
    public int batchInsertRelations(List<GbBoxGameRelation> relations);

    /**
     * 批量从盒子移除游戏
     * 
     * @param boxId 盒子ID
     * @param gameIds 游戏ID数组
     * @return 结果
     */
    public int batchRemoveGamesFromBox(Long boxId, Long[] gameIds);

    /**
     * 根据盒子ID和游戏ID查询关联关系
     * 
     * @param boxId 盒子ID
     * @param gameId 游戏ID
     * @return 关联关系
     */
    public GbBoxGameRelation selectRelationByBoxIdAndGameId(Long boxId, Long gameId);

    /**
     * 批量查询盒子和游戏的关联关系
     * 
     * @param boxId 盒子ID
     * @param gameIds 游戏ID列表
     * @return 关联关系列表
     */
    public List<GbBoxGameRelation> selectRelationsByBoxIdAndGameIds(Long boxId, List<Long> gameIds);

    /**
     * 批量更新关联记录
     * 
     * @param relations 关联记录列表
     * @return 更新的记录数
     */
    public int batchUpdateRelations(List<GbBoxGameRelation> relations);

    /**
     * 根据盒子ID和游戏ID删除关联
     * 
     * @param boxId 盒子ID
     * @param gameId 游戏ID
     * @return 删除的记录数
     */
    public int deleteByBoxIdAndGameId(Long boxId, Long gameId);
}
