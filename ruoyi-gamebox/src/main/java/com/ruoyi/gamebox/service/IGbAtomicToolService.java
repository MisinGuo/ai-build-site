package com.ruoyi.gamebox.service;

import com.ruoyi.gamebox.domain.GbAtomicTool;

import java.util.List;
import java.util.Map;

/**
 * 原子工具Service接口
 * 
 * @author ruoyi
 * @date 2026-01-04
 */
public interface IGbAtomicToolService 
{
    /**
     * 查询原子工具列表
     */
    public List<GbAtomicTool> selectGbAtomicToolList(GbAtomicTool gbAtomicTool);

    /**
     * 查询原子工具
     */
    public GbAtomicTool selectGbAtomicToolById(Long id);

    /**
     * 根据工具编码查询原子工具
     */
    public GbAtomicTool selectGbAtomicToolByCode(String toolCode);

    /**
     * 按网站可见性规则查询工具
     */
    public GbAtomicTool selectGbAtomicToolByCodeAndSite(String toolCode, Long siteId);

    /**
     * 获取所有可用的系统工具执行器列表
     */
    public List<Map<String, Object>> getAvailableExecutors();

    /**
     * 通过工具ID执行工具
     * 
     * @param toolId 工具ID
     * @param params 输入参数
     * @return 执行结果
     */
    public Map<String, Object> executeToolById(Long toolId, Map<String, Object> params) throws Exception;

    /**
     * 新增原子工具
     */
    public int insertGbAtomicTool(GbAtomicTool gbAtomicTool);

    /**
     * 修改原子工具
     */
    public int updateGbAtomicTool(GbAtomicTool gbAtomicTool);

    /**
     * 批量删除原子工具
     */
    public int deleteGbAtomicToolByIds(Long[] ids);

    /**
     * 删除原子工具信息
     */
    public int deleteGbAtomicToolById(Long id);

    /**
     * 测试工具
     */
    public Map<String, Object> testTool(String toolCode, Map<String, Object> params) throws Exception;

    /**
     * 执行工具
     */
    public Map<String, Object> executeTool(String toolCode, Map<String, Object> params) throws Exception;
}
