package com.ruoyi.gamebox.mapper;

import java.util.List;
import com.ruoyi.gamebox.domain.GbBoxGameRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 游戏盒子-游戏关联Mapper接口
 * 
 * @author ruoyi
 */
@Mapper
public interface GbBoxGameRelationMapper {
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
    public List<GbBoxGameRelation> selectGamesByBoxId(@Param("boxId") Long boxId);

    /**
     * 根据游戏ID查询关联的盒子列表
     * 
     * @param gameId 游戏ID
     * @return 盒子关联列表
     */
    public List<GbBoxGameRelation> selectBoxesByGameId(@Param("gameId") Long gameId);

    /**
     * 根据游戏ID查询关联的盒子列表（按站点可见性过滤）
     *
     * @param gameId         游戏ID
     * @param siteId         当前站点ID
     * @param personalSiteId 默认配置站点ID
     * @return 盒子关联列表
     */
    public List<GbBoxGameRelation> selectBoxesByGameIdWithSite(@Param("gameId") Long gameId, @Param("siteId") Long siteId, @Param("personalSiteId") Long personalSiteId);

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
     * 按快照全字段覆盖更新（包含 null 字段），用于变更日志撤回/重放。
     *
     * @param gbBoxGameRelation 关联快照（必须包含 id）
     * @return 结果
     */
    public int overwriteGbBoxGameRelationBySnapshot(GbBoxGameRelation gbBoxGameRelation);

    /**
     * 删除游戏盒子-游戏关联
     * 
     * @param id 游戏盒子-游戏关联主键
     * @return 结果
     */
    public int deleteGbBoxGameRelationById(Long id);

    /**
     * 批量删除游戏盒子-游戏关联
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGbBoxGameRelationByIds(Long[] ids);

    /**
     * 检查盒子-游戏关联是否已存在
     * 
     * @param boxId  盒子ID
     * @param gameId 游戏ID
     * @return 结果
     */
    public GbBoxGameRelation checkBoxGameRelation(@Param("boxId") Long boxId, @Param("gameId") Long gameId);

    /**
     * 删除指定盒子的所有游戏关联
     * 
     * @param boxId 盒子ID
     * @return 结果
     */
    public int deleteRelationsByBoxId(@Param("boxId") Long boxId);

    /**
     * 删除指定游戏的所有盒子关联
     * 
     * @param gameId 游戏ID
     * @return 结果
     */
    public int deleteRelationsByGameId(@Param("gameId") Long gameId);

    /**
     * 批量查询盒子和游戏的关联关系
     * 
     * @param boxId 盒子ID
     * @param gameIds 游戏ID列表
     * @return 关联关系列表
     */
    public List<GbBoxGameRelation> selectRelationsByBoxIdAndGameIds(@Param("boxId") Long boxId, @Param("gameIds") List<Long> gameIds);

    /**
     * 批量插入关联记录
     * 
     * @param relations 关联记录列表
     * @return 插入的记录数
     */
    public int batchInsertRelations(List<GbBoxGameRelation> relations);

    /**
     * 根据盒子ID和游戏ID删除关联
     * 
     * @param boxId 盒子ID
     * @param gameId 游戏ID
     * @return 删除的记录数
     */
    public int deleteByBoxIdAndGameId(@Param("boxId") Long boxId, @Param("gameId") Long gameId);

    /**
     * 批量查询多个游戏所关联的盒子摘要信息
     * 
     * @param gameIds 游戏ID列表
     * @return 关联关系列表（含 boxId, boxName, boxLogoUrl）
     */
    public List<GbBoxGameRelation> selectBoxSummaryByGameIds(@Param("gameIds") List<Long> gameIds);

    /**
     * 批量更新关联（通过主键 id 定位，仅更新传入的非 null 字段，null 保留原值）
     *
     * @param relations 关联列表（每条必须有 id）
     * @return 受影响行数
     */
    public int batchUpdateRelations(@Param("relations") List<GbBoxGameRelation> relations);
}
