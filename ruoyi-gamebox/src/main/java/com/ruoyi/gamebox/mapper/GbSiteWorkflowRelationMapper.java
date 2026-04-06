package com.ruoyi.gamebox.mapper;

import java.util.List;
import java.util.Map;
import com.ruoyi.gamebox.domain.GbSiteWorkflowRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 网站-工作流关联Mapper接口
 * 
 * @author ruoyi
 * @date 2026-02-17
 */
@Mapper
public interface GbSiteWorkflowRelationMapper 
{
    /**
     * 查询网站的所有工作流（带工作流详情）
     * 
     * @param siteId 网站ID
     * @return 工作流关联列表
     */
    public List<GbSiteWorkflowRelation> selectWorkflowsBySiteId(@Param("siteId") Long siteId);

    /**
     * 查询工作流在哪些网站可用
     * 
     * @param workflowId 工作流ID
     * @return 网站关联列表
     */
    public List<GbSiteWorkflowRelation> selectSitesByWorkflowId(@Param("workflowId") Long workflowId);

    /**
     * 检查关联是否存在
     * 
     * @param siteId 网站ID
     * @param workflowId 工作流ID
     * @return 是否存在
     */
    public int checkRelationExists(@Param("siteId") Long siteId, @Param("workflowId") Long workflowId);
    
    /**
     * 检查排除关联是否存在
     * 
     * @param siteId 网站ID
     * @param workflowId 工作流ID
     * @return 是否存在
     */
    public int checkExcludeRelationExists(@Param("siteId") Long siteId, @Param("workflowId") Long workflowId);

    /**
     * 新增网站-工作流关联
     * 
     * @param relation 关联对象
     * @return 结果
     */
    public int insertSiteWorkflowRelation(GbSiteWorkflowRelation relation);

    /**
     * 更新网站-工作流关联
     * 
     * @param relation 关联对象
     * @return 结果
     */
    public int updateSiteWorkflowRelation(GbSiteWorkflowRelation relation);

    /**
     * 批量新增网站-工作流关联
     * 
     * @param relations 关联列表
     * @return 结果
     */
    public int batchInsertSiteWorkflowRelations(@Param("relations") List<GbSiteWorkflowRelation> relations);

    /**
     * 删除网站-工作流关联（支持按类型删除）
     * 
     * @param siteId 网站ID
     * @param workflowId 工作流ID
     * @param relationType 关系类型（可选，为空则删除所有类型）
     * @return 结果
     */
    public int deleteSiteWorkflowRelation(@Param("siteId") Long siteId, 
                                          @Param("workflowId") Long workflowId, 
                                          @Param("relationType") String relationType);
    
    /**
     * 更新工作流可见性
     * 
     * @param params 包含siteId, workflowId, isVisible等参数
     * @return 结果
     */
    public int updateSiteWorkflowVisibility(Map<String, Object> params);
    
    /**
     * 查询默认工作流被哪些网站排除（按关联类型）
     * 
     * @param workflowId 工作流ID
     * @param relationType 关联类型
     * @return 被排除的网站ID列表
     */
    public List<Long> selectExcludedSiteIdsByRelationType(@Param("workflowId") Long workflowId, 
                                                          @Param("relationType") String relationType);

    /**
     * 批量删除：对给定 workflowIds+relationType，删除不在 keepSiteIds 中的网站关联
     */
    public void deleteByWorkflowIdsAndTypeExcluding(@Param("workflowIds") List<Long> workflowIds,
                                                     @Param("relationType") String relationType,
                                                     @Param("keepSiteIds") List<Long> keepSiteIds);
}
