package com.ruoyi.gamebox.mapper;

import java.util.List;
import com.ruoyi.gamebox.domain.GbSiteGameRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 网站-游戏关联Mapper接口
 * 
 * @author ruoyi
 * @date 2025-12-27
 */
@Mapper
public interface GbSiteGameRelationMapper 
{
    /**
     * 查询网站-游戏关联
     * 
     * @param id 主键
     * @return 网站-游戏关联
     */
    public GbSiteGameRelation selectGbSiteGameRelationById(Long id);

    /**
     * 查询网站-游戏关联列表
     * 
     * @param gbSiteGameRelation 网站-游戏关联
     * @return 网站-游戏关联集合
     */
    public List<GbSiteGameRelation> selectGbSiteGameRelationList(GbSiteGameRelation gbSiteGameRelation);

    /**
     * 查询网站的所有游戏（带游戏详情）
     * 
     * @param siteId 网站ID
     * @return 游戏关联列表
     */
    public List<GbSiteGameRelation> selectGamesBySiteId(@Param("siteId") Long siteId);

    /**
     * 查询游戏在哪些网站可用
     * 
     * @param gameId 游戏ID
     * @return 网站关联列表
     */
    public List<GbSiteGameRelation> selectSitesByGameId(@Param("gameId") Long gameId);

    /**
     * 检查关联是否存在
     * 
     * @param siteId 网站ID
     * @param gameId 游戏ID
     * @return 是否存在
     */
    public int checkRelationExists(@Param("siteId") Long siteId, @Param("gameId") Long gameId);

    /**
     * 新增网站-游戏关联
     * 
     * @param gbSiteGameRelation 网站-游戏关联
     * @return 结果
     */
    public int insertGbSiteGameRelation(GbSiteGameRelation gbSiteGameRelation);

    /**
     * 批量新增网站-游戏关联
     * 
     * @param relations 关联列表
     * @return 结果
     */
    public int batchInsertGbSiteGameRelation(@Param("relations") List<GbSiteGameRelation> relations);

    /**
     * 修改网站-游戏关联
     * 
     * @param gbSiteGameRelation 网站-游戏关联
     * @return 结果
     */
    public int updateGbSiteGameRelation(GbSiteGameRelation gbSiteGameRelation);

    /**
     * 根据网站ID和游戏ID更新可见性
     * 
     * @param siteId 网站ID
     * @param gameId 游戏ID
     * @param isVisible 是否可见
     * @return 结果
     */
    public int updateVisibilityBySiteIdAndGameId(@Param("siteId") Long siteId, @Param("gameId") Long gameId, @Param("isVisible") String isVisible);

    /**
     * 根据网站ID和游戏ID更新配置
     * 
     * @param siteId 网站ID
     * @param gameId 游戏ID
     * @param relation 关联信息（包含要更新的字段）
     * @return 结果
     */
    public int updateConfigBySiteIdAndGameId(@Param("siteId") Long siteId, @Param("gameId") Long gameId, @Param("relation") GbSiteGameRelation relation);

    /**
     * 删除网站-游戏关联
     * 
     * @param id 主键
     * @return 结果
     */
    public int deleteGbSiteGameRelationById(Long id);

    /**
     * 删除网站的所有游戏关联
     * 
     * @param siteId 网站ID
     * @return 结果
     */
    public int deleteGbSiteGameRelationsBySiteId(@Param("siteId") Long siteId);

    /**
     * 删除游戏的所有网站关联
     * 
     * @param gameId 游戏ID
     * @return 结果
     */
    public int deleteGbSiteGameRelationsByGameId(@Param("gameId") Long gameId);

    /**
     * 删除指定网站和游戏的关联（支持按类型删除）
     * 
     * @param siteId 网站ID
     * @param gameId 游戏ID
     * @param relationType 关系类型（可选，为空则删除所有类型）
     * @return 结果
     */
    public int deleteGbSiteGameRelation(@Param("siteId") Long siteId, @Param("gameId") Long gameId, @Param("relationType") String relationType);
    
    /**
     * 检查排除关联是否存在
     * 
     * @param siteId 网站ID
     * @param gameId 游戏ID
     * @return 是否存在
     */
    public int checkExcludeRelationExists(@Param("siteId") Long siteId, @Param("gameId") Long gameId);

    /**
     * 批量删除指定网站和多个游戏的关联
     * 
     * @param siteId 网站ID
     * @param gameIds 游戏ID列表
     * @return 结果
     */
    public int batchDeleteGbSiteGameRelations(@Param("siteId") Long siteId, @Param("gameIds") List<Long> gameIds);

    /**
     * 批量删除网站-游戏关联
     * 
     * @param ids 主键数组
     * @return 结果
     */
    public int deleteGbSiteGameRelationByIds(Long[] ids);

    /**
     * 增加浏览量
     * 
     * @param siteId 网站ID
     * @param gameId 游戏ID
     * @return 结果
     */
    public int incrementViewCount(@Param("siteId") Long siteId, @Param("gameId") Long gameId);

    /**
     * 增加下载量
     * 
     * @param siteId 网站ID
     * @param gameId 游戏ID
     * @return 结果
     */
    public int incrementDownloadCount(@Param("siteId") Long siteId, @Param("gameId") Long gameId);

    /**
     * 删除排除关联
     * 
     * @param siteId 网站ID
     * @param gameId 游戏ID
     * @return 结果
     */
    public int deleteExcludeRelation(@Param("siteId") Long siteId, @Param("gameId") Long gameId);

    /**
     * 查询被排除的网站ID列表
     * 
     * @param gameId 游戏ID
     * @return 网站ID列表
     */
    public List<Long> selectExcludedSiteIds(@Param("gameId") Long gameId);

    /**
     * 批量删除排除关联
     * 
     * @param gameId 游戏ID
     * @param siteIds 网站ID列表
     * @return 结果
     */
    public int batchDeleteExcludeRelations(@Param("gameId") Long gameId, @Param("siteIds") List<Long> siteIds);

    /**
     * 批量删除：对给定 gameIds+relationType，删除不在 keepSiteIds 中的网站关联
     */
    public void deleteByGameIdsAndTypeExcluding(@Param("gameIds") List<Long> gameIds,
                                                @Param("relationType") String relationType,
                                                @Param("keepSiteIds") List<Long> keepSiteIds);
}
