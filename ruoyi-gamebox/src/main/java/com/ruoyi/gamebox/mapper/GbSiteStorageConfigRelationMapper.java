package com.ruoyi.gamebox.mapper;

import java.util.List;
import com.ruoyi.gamebox.domain.GbSiteStorageConfigRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 网站-存储配置关联Mapper接口
 * 
 * @author ruoyi
 * @date 2025-12-27
 */
@Mapper
public interface GbSiteStorageConfigRelationMapper 
{
    /**
     * 查询网站的所有存储配置（带配置详情）
     * 
     * @param siteId 网站ID
     * @return 存储配置关联列表
     */
    public List<GbSiteStorageConfigRelation> selectStorageConfigsBySiteId(@Param("siteId") Long siteId);

    /**
     * 查询存储配置在哪些网站可用
     * 
     * @param storageConfigId 存储配置ID
     * @return 网站关联列表
     */
    public List<GbSiteStorageConfigRelation> selectSitesByStorageConfigId(@Param("storageConfigId") Long storageConfigId);

    /**
     * 检查关联是否存在
     * 
     * @param siteId 网站ID
     * @param storageConfigId 存储配置ID
     * @return 是否存在
     */
    public int checkRelationExists(@Param("siteId") Long siteId, @Param("storageConfigId") Long storageConfigId);
    
    /**
     * 检查 include 关联是否存在
     *
     * @param siteId 网站ID
     * @param storageConfigId 存储配置ID
     * @return 是否存在
     */
    public int checkIncludeRelationExists(@Param("siteId") Long siteId, @Param("storageConfigId") Long storageConfigId);

    /**
     * 检查排除关联是否存在
     * 
     * @param siteId 网站ID
     * @param storageConfigId 存储配置ID
     * @return 是否存在
     */
    public int checkExcludeRelationExists(@Param("siteId") Long siteId, @Param("storageConfigId") Long storageConfigId);

    /**
     * 新增网站-存储配置关联
     * 
     * @param relation 关联对象
     * @return 结果
     */
    public int insertGbSiteStorageConfigRelation(GbSiteStorageConfigRelation relation);

    /**
     * 更新网站-存储配置关联
     * 
     * @param relation 关联对象
     * @return 结果
     */
    public int updateGbSiteStorageConfigRelation(GbSiteStorageConfigRelation relation);

    /**
     * 批量新增网站-存储配置关联
     * 
     * @param relations 关联列表
     * @return 结果
     */
    public int batchInsertGbSiteStorageConfigRelation(@Param("relations") List<GbSiteStorageConfigRelation> relations);

    /**
     * 删除网站-存储配置关联（支持按类型删除）
     * 
     * @param siteId 网站ID
     * @param storageConfigId 存储配置ID
     * @param relationType 关系类型（可选，为空则删除所有类型）
     * @return 结果
     */
    public int deleteGbSiteStorageConfigRelation(@Param("siteId") Long siteId, @Param("storageConfigId") Long storageConfigId, @Param("relationType") String relationType);
    
    /**
     * 更新存储配置在网站的可见性
     * 
     * @param params 参数map，包含siteId, storageConfigId, isVisible
     * @return 结果
     */
    public int updateSiteStorageConfigVisibility(java.util.Map<String, Object> params);
    
    /**
     * 查询默认存储配置被哪些网站排除
     * 
     * @param storageConfigId 存储配置ID
     * @return 被排除的网站ID列表
     */
    public List<Long> selectExcludedSiteIds(@Param("storageConfigId") Long storageConfigId);
    
    /**
     * 批量删除排除关联
     * 
     * @param storageConfigId 存储配置ID
     * @param siteIds 网站ID列表
     * @return 结果
     */
    public int batchDeleteExcludeRelations(@Param("storageConfigId") Long storageConfigId, @Param("siteIds") List<Long> siteIds);

    /**
     * 批量删除：对给定 storageConfigIds+relationType，删除不在 keepSiteIds 中的网站关联
     */
    public void deleteByStorageConfigIdsAndTypeExcluding(@Param("storageConfigIds") List<Long> storageConfigIds,
                                                          @Param("relationType") String relationType,
                                                          @Param("keepSiteIds") List<Long> keepSiteIds);
}
