package com.ruoyi.gamebox.mapper;

import java.util.List;
import com.ruoyi.gamebox.domain.GbSiteBoxRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 网站-盒子关联Mapper接口
 * 
 * @author ruoyi
 * @date 2025-12-27
 */
@Mapper
public interface GbSiteBoxRelationMapper 
{
    /**
     * 查询网站-游戏盒子关联
     * 
     * @param id 主键
     * @return 网站-游戏盒子关联
     */
    public GbSiteBoxRelation selectGbSiteBoxRelationById(Long id);

    /**
     * 查询网站-游戏盒子关联列表
     * 
     * @param gbSiteBoxRelation 网站-游戏盒子关联
     * @return 网站-游戏盒子关联集合
     */
    public List<GbSiteBoxRelation> selectGbSiteBoxRelationList(GbSiteBoxRelation gbSiteBoxRelation);

    /**
     * 查询网站的所有盒子（带盒子详情）
     * 
     * @param siteId 网站ID
     * @return 盒子关联列表
     */
    public List<GbSiteBoxRelation> selectBoxesBySiteId(@Param("siteId") Long siteId);

    /**
     * 查询盒子在哪些网站可用
     * 
     * @param boxId 盒子ID
     * @return 网站关联列表
     */
    public List<GbSiteBoxRelation> selectSitesByBoxId(@Param("boxId") Long boxId);

    /**
     * 检查关联是否存在
     * 
     * @param siteId 网站ID
     * @param boxId 盒子ID
     * @return 是否存在
     */
    public int checkRelationExists(@Param("siteId") Long siteId, @Param("boxId") Long boxId);
    
    /**
     * 检查盒子与网站的特定类型关联是否存在
     * 
     * @param siteId 网站ID
     * @param boxId 盒子ID
     * @param relationType 关系类型
     * @return 关联数量
     */
    public int checkRelationExistsByType(@Param("siteId") Long siteId, @Param("boxId") Long boxId, @Param("relationType") String relationType);

    /**
     * 新增网站-游戏盒子关联
     * 
     * @param gbSiteBoxRelation 网站-游戏盒子关联
     * @return 结果
     */
    public int insertGbSiteBoxRelation(GbSiteBoxRelation gbSiteBoxRelation);

    /**
     * 批量新增网站-游戏盒子关联
     * 
     * @param relations 关联列表
     * @return 结果
     */
    public int batchInsertGbSiteBoxRelation(@Param("relations") List<GbSiteBoxRelation> relations);

    /**
     * 修改网站-游戏盒子关联
     * 
     * @param gbSiteBoxRelation 网站-游戏盒子关联
     * @return 结果
     */
    public int updateGbSiteBoxRelation(GbSiteBoxRelation gbSiteBoxRelation);

    /**
     * 根据网站ID和盒子ID更新可见性
     * 
     * @param siteId 网站ID
     * @param boxId 盒子ID
     * @param isVisible 是否可见
     * @return 结果
     */
    public int updateVisibilityBySiteIdAndBoxId(@Param("siteId") Long siteId, @Param("boxId") Long boxId, @Param("isVisible") String isVisible);

    /**
     * 根据网站ID和盒子ID更新配置
     * 
     * @param siteId 网站ID
     * @param boxId 盒子ID
     * @param relation 关联信息（包含要更新的字段）
     * @return 结果
     */
    public int updateConfigBySiteIdAndBoxId(@Param("siteId") Long siteId, @Param("boxId") Long boxId, @Param("relation") GbSiteBoxRelation relation);

    /**
     * 删除网站-游戏盒子关联
     * 
     * @param id 主键
     * @return 结果
     */
    public int deleteGbSiteBoxRelationById(Long id);

    /**
     * 删除网站的所有盒子关联
     * 
     * @param siteId 网站ID
     * @return 结果
     */
    public int deleteGbSiteBoxRelationsBySiteId(@Param("siteId") Long siteId);

    /**
     * 删除盒子的所有网站关联
     * 
     * @param boxId 盒子ID
     * @return 结果
     */
    public int deleteGbSiteBoxRelationsByBoxId(@Param("boxId") Long boxId);

    /**
     * 删除指定网站和盒子的关联（支持按类型删除）
     * 
     * @param siteId 网站ID
     * @param boxId 盒子ID
     * @param relationType 关系类型（可选，为空则删除所有类型）
     * @return 结果
     */
    public int deleteGbSiteBoxRelation(@Param("siteId") Long siteId, @Param("boxId") Long boxId, @Param("relationType") String relationType);
    
    /**
     * 检查排除关联是否存在
     * 
     * @param siteId 网站ID
     * @param boxId 盒子ID
     * @return 是否存在
     */
    public int checkExcludeRelationExists(@Param("siteId") Long siteId, @Param("boxId") Long boxId);

    /**
     * 批量删除网站-游戏盒子关联
     * 
     * @param ids 主键数组
     * @return 结果
     */
    public int deleteGbSiteBoxRelationByIds(Long[] ids);

    /**
     * 增加浏览量
     * 
     * @param siteId 网站ID
     * @param boxId 盒子ID
     * @return 结果
     */
    public int incrementViewCount(@Param("siteId") Long siteId, @Param("boxId") Long boxId);

    /**
     * 删除排除关联
     * 
     * @param siteId 网站ID
     * @param boxId 盒子ID
     * @return 结果
     */
    public int deleteExcludeRelation(@Param("siteId") Long siteId, @Param("boxId") Long boxId);

    /**
     * 查询被排除的网站ID列表
     * 
     * @param boxId 盒子ID
     * @return 网站ID列表
     */
    public List<Long> selectExcludedSiteIds(@Param("boxId") Long boxId);

    /**
     * 批量删除排除关联
     * 
     * @param boxId 盒子ID
     * @param siteIds 网站ID列表
     * @return 结果
     */
    public int batchDeleteExcludeRelations(@Param("boxId") Long boxId, @Param("siteIds") List<Long> siteIds);

    /**
     * 批量删除：对给定 boxIds+relationType，删除不在 keepSiteIds 中的网站关联
     */
    public void deleteByBoxIdsAndTypeExcluding(@Param("boxIds") List<Long> boxIds,
                                               @Param("relationType") String relationType,
                                               @Param("keepSiteIds") List<Long> keepSiteIds);
}
