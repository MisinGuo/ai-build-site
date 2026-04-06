package com.ruoyi.gamebox.mapper;

import com.ruoyi.gamebox.domain.GbWorkflow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 工作流Mapper接口
 * 
 * @author ruoyi
 * @date 2026-01-04
 */
@Mapper
public interface GbWorkflowMapper 
{
    /**
     * 查询工作流列表
     */
    public List<GbWorkflow> selectGbWorkflowList(GbWorkflow gbWorkflow);

    /**
     * 查询工作流
     */
    public GbWorkflow selectGbWorkflowById(Long id);

    /**
     * 根据工作流编码查询工作流
     */
    public GbWorkflow selectGbWorkflowByCode(@Param("workflowCode") String workflowCode);

    /**
     * 新增工作流
     */
    public int insertGbWorkflow(GbWorkflow gbWorkflow);

    /**
     * 修改工作流
     */
    public int updateGbWorkflow(GbWorkflow gbWorkflow);

    /**
     * 删除工作流
     */
    public int deleteGbWorkflowById(Long id);

    /**
     * 批量删除工作流
     */
    public int deleteGbWorkflowByIds(Long[] ids);

    /**
     * 更新网站工作流关联关系
     * 
     * @param siteId 网站ID
     * @param workflowId 工作流ID
     * @param relationType 关系类型：include-包含 exclude-排除
     * @return 结果
     */
    public int updateSiteWorkflowRelation(@Param("siteId") Long siteId, @Param("workflowId") Long workflowId, @Param("relationType") String relationType);

    /**
     * 获取工作流关联的网站列表
     * 
     * @param workflowId 工作流ID
     * @return 关联的网站列表
     */
    public List<Map<String, Object>> getWorkflowSites(@Param("workflowId") Long workflowId);

    /**
     * 删除网站工作流关联关系
     * 
     * @param siteId 网站ID
     * @param workflowId 工作流ID
     * @param relationType 关系类型：include-关联 exclude-排除，null-删除所有类型
     * @return 结果
     */
    public int deleteSiteWorkflowRelation(@Param("siteId") Long siteId, @Param("workflowId") Long workflowId, @Param("relationType") String relationType);

    /**
     * 删除工作流所有关联关系
     * 
     * @param workflowId 工作流ID
     * @return 结果
     */
    public int deleteWorkflowRelations(@Param("workflowId") Long workflowId);

    /**
     * 批量插入工作流关联关系
     * 
     * @param workflowId 工作流ID
     * @param siteIds 网站ID列表
     * @param relationType 关系类型：include-关联 exclude-排除
     * @return 结果
     */
    public int batchInsertWorkflowRelations(@Param("workflowId") Long workflowId, @Param("siteIds") List<Long> siteIds, @Param("relationType") String relationType);

    /**
     * 切换关联可见性（用于跨站共享的快速切换）
     * 
     * @param siteId 网站ID
     * @param workflowId 工作流ID
     * @param isVisible 是否可见
     * @return 结果
     */
    public int toggleWorkflowVisibility(@Param("siteId") Long siteId, @Param("workflowId") Long workflowId, @Param("isVisible") String isVisible);

    /**
     * 删除工作流的所有排除关系
     * 
     * @param workflowId 工作流ID
     * @return 结果
     */
    public int deleteWorkflowExclusions(@Param("workflowId") Long workflowId);

    /**
     * 批量插入工作流排除关系
     * 
     * @param workflowId 工作流ID
     * @param excludedSiteIds 要排除的网站ID列表
     * @return 结果
     */
    public int batchInsertWorkflowExclusions(@Param("workflowId") Long workflowId, @Param("excludedSiteIds") List<Long> excludedSiteIds);

    /**
     * 获取工作流已排除的网站列表
     * 
     * @param workflowId 工作流ID
     * @return 已排除的网站ID列表
     */
    public List<Long> getExcludedSitesByWorkflow(@Param("workflowId") Long workflowId);
}
