package com.ruoyi.gamebox.service;

import com.ruoyi.gamebox.domain.GbWorkflow;

import java.util.List;
import java.util.Map;

/**
 * 工作流Service接口
 * 
 * @author ruoyi
 * @date 2026-01-04
 */
public interface IGbWorkflowService 
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
    public GbWorkflow selectGbWorkflowByCode(String workflowCode);

    /**
     * 新增工作流
     */
    public int insertGbWorkflow(GbWorkflow gbWorkflow);

    /**
     * 修改工作流
     */
    public int updateGbWorkflow(GbWorkflow gbWorkflow);

    /**
     * 批量删除工作流
     */
    public int deleteGbWorkflowByIds(Long[] ids);

    /**
     * 删除工作流信息
     */
    public int deleteGbWorkflowById(Long id);

    /**
     * 执行工作流
     */
    public Map<String, Object> executeWorkflow(String workflowCode, String mode, Map<String, Object> inputData) throws Exception;

    /**
     * 获取执行历史
     */
    public List<Map<String, Object>> getExecutionHistory(String workflowCode);

    /**
     * 验证工作流定义
     */
    public List<String> validateWorkflow(GbWorkflow gbWorkflow);

    /**
     * 获取所有可用的原子工具列表
     */
    public List<Map<String, Object>> getAvailableTools(Long siteId);

    /**
     * 验证步骤参数映射
     */
    public Map<String, Object> validateStepMapping(String toolCode, Map<String, Object> inputMapping);

    /**
     * 模拟执行工作流（不保存数据，仅用于测试）
     */
    public Map<String, Object> simulateWorkflow(GbWorkflow gbWorkflow, Map<String, Object> inputData) throws Exception;

    /**
     * 获取工作流关联的网站列表
     * 
     * @param workflowId 工作流ID
     * @return 关联的网站列表
     */
    public List<Map<String, Object>> getWorkflowSites(Long workflowId);

    /**
     * 切换关联可见性
     * 
     * @param siteId 网站ID
     * @param workflowId 工作流ID
     * @param isVisible 是否可见
     * @return 结果
     */
    public int toggleWorkflowVisibility(Long siteId, Long workflowId, String isVisible);

    /**
     * 获取工作流已排除的网站列表
     * 
     * @param workflowId 工作流ID
     * @return 已排除的网站ID列表
     */
    public List<Long> getExcludedSitesByWorkflow(Long workflowId);

    /**
     * 导入工作流（JSON格式）
     * 
     * @param data 导入的数据
     * @param importRelatedTitles 是否导入关联的标题
     * @param importRelatedArticles 是否导入关联的文章
     * @param updateStrategy 更新策略：skip-跳过, update-更新, replace-替换
     * @return 导入结果
     */
    public Map<String, Object> importWorkflows(Map<String, Object> data, Boolean importRelatedTitles,
                                               Boolean importRelatedArticles, String updateStrategy);

    /**
     * 全站导入工作流（JSON格式）
     * 
     * @param data 导入的数据
     * @param importRelatedTitles 是否导入关联的标题
     * @param importRelatedArticles 是否导入关联的文章
     * @param updateStrategy 更新策略：skip-跳过, update-更新, replace-替换
     * @param clearExisting 是否清除现有数据
     * @return 导入结果
     */
    public Map<String, Object> importAllWorkflows(Map<String, Object> data, Boolean importRelatedTitles,
                                                   Boolean importRelatedArticles, String updateStrategy, Boolean clearExisting);
}
