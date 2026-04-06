package com.ruoyi.gamebox.mapper;

import java.util.List;
import com.ruoyi.gamebox.domain.GbSiteTemplateRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 网站-提示词模板关联Mapper接口
 * 
 * @author ruoyi
 * @date 2025-12-31
 */
@Mapper
public interface GbSiteTemplateRelationMapper 
{
    /**
     * 查询网站的所有提示词模板（带模板详情）
     * 
     * @param siteId 网站ID
     * @return 提示词模板关联列表
     */
    public List<GbSiteTemplateRelation> selectTemplatesBySiteId(@Param("siteId") Long siteId);

    /**
     * 查询提示词模板在哪些网站可用
     * 
     * @param promptTemplateId 提示词模板ID
     * @return 网站关联列表
     */
    public List<GbSiteTemplateRelation> selectSitesByTemplateId(@Param("promptTemplateId") Long promptTemplateId);

    /**
     * 检查关联是否存在
     * 
     * @param siteId 网站ID
     * @param promptTemplateId 提示词模板ID
     * @return 是否存在
     */
    public int checkRelationExists(@Param("siteId") Long siteId, @Param("promptTemplateId") Long promptTemplateId);

    /**
     * 新增网站-提示词模板关联
     * 
     * @param relation 关联对象
     * @return 结果
     */
    public int insertGbSiteTemplateRelation(GbSiteTemplateRelation relation);

    /**
     * 批量新增网站-提示词模板关联
     * 
     * @param relations 关联列表
     * @return 结果
     */
    public int batchInsertGbSiteTemplateRelation(@Param("relations") List<GbSiteTemplateRelation> relations);

    /**
     * 删除网站-提示词模板关联
     * 
     * @param siteId 网站ID
     * @param promptTemplateId 提示词模板ID
     * @return 结果
     */
    public int deleteGbSiteTemplateRelation(@Param("siteId") Long siteId, @Param("promptTemplateId") Long promptTemplateId);
    
    /**
     * 查询默认提示词模板被哪些网站排除
     * 
     * @param promptTemplateId 提示词模板ID
     * @return 被排除的网站ID列表
     */
    public List<Long> selectExcludedSiteIds(@Param("promptTemplateId") Long promptTemplateId);
    
    /**
     * 批量删除排除关联
     * 
     * @param promptTemplateId 提示词模板ID
     * @param siteIds 网站ID列表
     * @return 结果
     */
    public int batchDeleteExcludeRelations(@Param("promptTemplateId") Long promptTemplateId, @Param("siteIds") List<Long> siteIds);
}
