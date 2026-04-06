package com.ruoyi.gamebox.mapper;

import java.util.List;
import com.ruoyi.gamebox.domain.GbSiteAiPlatformRelation;
import com.ruoyi.gamebox.domain.GbSiteRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 网站-AI平台配置关联Mapper接口
 * 
 * @author ruoyi
 * @date 2025-12-31
 */
@Mapper
public interface GbSiteAiPlatformRelationMapper 
{
    /**
     * 查询网站的所有AI平台配置（带配置详情）
     * 
     * @param siteId 网站ID
     * @return AI平台配置关联列表
     */
    public List<GbSiteAiPlatformRelation> selectAiPlatformsBySiteId(@Param("siteId") Long siteId);

    /**
     * 查询AI平台配置在哪些网站可用
     * 
     * @param aiPlatformId AI平台配置ID
     * @return 网站关联列表
     */
    public List<GbSiteAiPlatformRelation> selectSitesByAiPlatformId(@Param("aiPlatformId") Long aiPlatformId);

    /**
     * 检查关联是否存在
     * 
     * @param siteId 网站ID
     * @param aiPlatformId AI平台配置ID
     * @return 是否存在
     */
    public int checkRelationExists(@Param("siteId") Long siteId, @Param("aiPlatformId") Long aiPlatformId);

    /**
     * 检查关联关系是否存在（只检查include类型）
     * 
     * @param siteId 网站ID
     * @param aiPlatformId AI平台配置ID
     * @return 是否存在
     */
    public int checkIncludeRelationExists(@Param("siteId") Long siteId, @Param("aiPlatformId") Long aiPlatformId);

    /**
     * 新增网站-AI平台配置关联
     * 
     * @param relation 关联对象
     * @return 结果
     */
    public int insertGbSiteAiPlatformRelation(GbSiteAiPlatformRelation relation);

    /**
     * 更新网站-AI平台配置关联
     * 
     * @param relation 关联对象
     * @return 结果
     */
    public int updateGbSiteAiPlatformRelation(GbSiteAiPlatformRelation relation);

    /**
     * 批量新增网站-AI平台配置关联
     * 
     * @param relations 关联列表
     * @return 结果
     */
    public int batchInsertGbSiteAiPlatformRelation(@Param("relations") List<GbSiteAiPlatformRelation> relations);

    /**
     * 删除网站-AI平台配置关联（支持按类型删除）
     * 
     * @param siteId 网站ID
     * @param aiPlatformId AI平台配置ID
     * @param relationType 关系类型（可选，为空则删除所有类型）
     * @return 结果
     */
    public int deleteGbSiteAiPlatformRelation(@Param("siteId") Long siteId, @Param("aiPlatformId") Long aiPlatformId, @Param("relationType") String relationType);
    
    /**
     * 检查排除关联是否存在
     * 
     * @param siteId 网站ID
     * @param aiPlatformId AI平台配置ID
     * @return 是否存在
     */
    public int checkExcludeRelationExists(@Param("siteId") Long siteId, @Param("aiPlatformId") Long aiPlatformId);
    
    /**
     * 查询默认AI平台配置被哪些网站排除
     * 
     * @param aiPlatformId AI平台配置ID
     * @return 被排除的网站ID列表
     */
    public List<Long> selectExcludedSiteIds(@Param("aiPlatformId") Long aiPlatformId);
    
    /**
     * 批量删除排除关联
     * 
     * @param aiPlatformId AI平台配置ID
     * @param siteIds 网站ID列表
     * @return 结果
     */
    public int batchDeleteExcludeRelations(@Param("aiPlatformId") Long aiPlatformId, @Param("siteIds") List<Long> siteIds);
    
    /**
     * 更新AI平台配置的可见性
     * 
     * @param params 参数Map (siteId, aiPlatformId, isVisible, updateBy, updateTime)
     * @return 结果
     */
    public int updateSiteAiPlatformVisibility(java.util.Map<String, Object> params);
    
    // ==================== 通用gb_site_relation表操作 ====================
    
    /**
     * 插入通用site_relation记录
     * 
     * @param relation 关联对象
     * @return 结果
     */
    public int insertSiteRelation(GbSiteRelation relation);
    
    /**
     * 批量插入通用site_relation记录
     * 
     * @param relations 关联列表
     * @return 结果
     */
    public int batchInsertSiteRelations(@Param("relations") List<GbSiteRelation> relations);
    
    /**
     * 删除通用site_relation记录
     * 
     * @param siteId 网站ID
     * @param relatedId 关联ID
     * @param relationType 关联类型
     * @return 结果
     */
    public int deleteSiteRelation(@Param("siteId") Long siteId, @Param("relatedId") Long relatedId, @Param("relationType") String relationType);
    
    /**
     * 查询被排除的网站ID列表（按关联类型）
     * 
     * @param relatedId 关联ID
     * @param relationType 关联类型
     * @return 网站ID列表
     */
    public List<Long> selectExcludedSiteIdsByRelationType(@Param("relatedId") Long relatedId, @Param("relationType") String relationType);

    /**
     * 批量删除：对给定 aiPlatformIds+relationType，删除不在 keepSiteIds 中的网站关联
     */
    public void deleteByAiPlatformIdsAndTypeExcluding(@Param("aiPlatformIds") List<Long> aiPlatformIds,
                                                       @Param("relationType") String relationType,
                                                       @Param("keepSiteIds") List<Long> keepSiteIds);
}
