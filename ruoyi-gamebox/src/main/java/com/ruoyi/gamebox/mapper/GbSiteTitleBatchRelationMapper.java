package com.ruoyi.gamebox.mapper;

import com.ruoyi.gamebox.domain.GbSiteRelation;
import com.ruoyi.gamebox.domain.GbSiteTitleBatchRelation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 网站-标题池批次关联Mapper接口
 * 
 * @author ruoyi
 * @date 2026-01-02
 */
public interface GbSiteTitleBatchRelationMapper 
{
    /**
     * 检查排除关联是否存在
     * 
     * @param siteId 网站ID
     * @param batchId 批次ID
     * @return 数量
     */
    int checkExcludeRelationExists(@Param("siteId") Long siteId, @Param("batchId") Long batchId);
    
    /**
     * 检查关联是否存在（include类型）
     * 
     * @param siteId 网站ID
     * @param batchId 批次ID
     * @return 数量
     */
    int checkRelationExists(@Param("siteId") Long siteId, @Param("batchId") Long batchId);
    
    /**
     * 插入网站-标题池批次关联
     * 
     * @param relation 关联信息
     * @return 结果
     */
    int insertGbSiteTitleBatchRelation(GbSiteTitleBatchRelation relation);
    
    /**
     * 插入网站关联
     * 
     * @param relation 关联信息
     * @return 结果
     */
    int insertSiteRelation(GbSiteRelation relation);
    
    /**
     * 批量插入网站关联
     * 
     * @param relations 关联列表
     * @return 结果
     */
    int batchInsertSiteRelations(@Param("list") List<GbSiteRelation> relations);
    
    /**
     * 删除网站关联
     * 
     * @param siteId 网站ID
     * @param batchId 批次ID
     * @param relationType 关联类型
     * @return 结果
     */
    int deleteSiteRelation(@Param("siteId") Long siteId, 
                          @Param("batchId") Long batchId, 
                          @Param("relationType") String relationType);
    
    /**
     * 查询被排除的网站ID列表
     * 
     * @param batchId 批次ID
     * @param relationType 关联类型
     * @return 网站ID列表
     */
    List<Long> selectExcludedSiteIdsByRelationType(@Param("batchId") Long batchId, 
                                                   @Param("relationType") String relationType);

    /**
     * 查询批次的所有网站关联关系（含关联类型）
     *
     * @param batchId 批次ID
     * @return 关联列表
     */
    List<GbSiteTitleBatchRelation> selectSitesByBatchId(@Param("batchId") Long batchId);
    
    /**
     * 查询网站-标题池批次关联列表
     * 
     * @param relation 查询条件
     * @return 关联列表
     */
    List<GbSiteTitleBatchRelation> selectGbSiteTitleBatchRelationList(GbSiteTitleBatchRelation relation);
    
    /**
     * 更新网站-标题池批次关联
     * 
     * @param relation 关联信息
     * @return 结果
     */
    int updateGbSiteTitleBatchRelation(GbSiteTitleBatchRelation relation);

    /**
     * 批量插入标题池批次网站关联（ON DUPLICATE KEY UPDATE 保障幂等）
     */
    int batchInsertGbSiteTitleBatchRelations(@Param("relations") List<GbSiteTitleBatchRelation> relations);

    /**
     * 批量删除：对给定 batchIds+relationType，删除不在 keepSiteIds 中的网站关联
     */
    void deleteByBatchIdsAndTypeExcluding(@Param("batchIds") List<Long> batchIds,
                                          @Param("relationType") String relationType,
                                          @Param("keepSiteIds") List<Long> keepSiteIds);
}
