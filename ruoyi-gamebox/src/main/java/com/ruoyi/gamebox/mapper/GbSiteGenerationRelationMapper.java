package com.ruoyi.gamebox.mapper;

import com.ruoyi.gamebox.domain.GbSiteRelation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章生成任务-网站关联Mapper接口
 * 
 * @author ruoyi
 * @date 2026-01-01
 */
public interface GbSiteGenerationRelationMapper 
{
    /**
     * 检查排除关联是否存在
     * 
     * @param siteId 网站ID
     * @param generationId 文章生成任务ID
     * @return 数量
     */
    int checkExcludeRelationExists(@Param("siteId") Long siteId, @Param("generationId") Long generationId);
    
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
     * @param generationId 文章生成任务ID
     * @param relationType 关联类型
     * @return 结果
     */
    int deleteSiteRelation(@Param("siteId") Long siteId, 
                          @Param("generationId") Long generationId, 
                          @Param("relationType") String relationType);
    
    /**
     * 查询被排除的网站ID列表
     * 
     * @param generationId 文章生成任务ID
     * @param relationType 关联类型
     * @return 网站ID列表
     */
    List<Long> selectExcludedSiteIdsByRelationType(@Param("generationId") Long generationId, 
                                                   @Param("relationType") String relationType);
}
