package com.ruoyi.web.controller.gamebox;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import com.ruoyi.gamebox.domain.GbWorkflow;
import com.ruoyi.gamebox.domain.GbWorkflowExecution;
import com.ruoyi.gamebox.domain.GbWorkflowTemplate;
import com.ruoyi.gamebox.service.IGbWorkflowService;
import com.ruoyi.gamebox.service.impl.GbWorkflowServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;

/**
 * 工作流Controller
 * 
 * @author ruoyi
 * @date 2026-01-04
 */
@RestController
@RequestMapping("/gamebox/workflow")
public class GbWorkflowController extends BaseController
{
    @Autowired
    private IGbWorkflowService gbWorkflowService;

    // 强转ServiceImpl以调用拓展方法
    private GbWorkflowServiceImpl getServiceImpl() {
        return (GbWorkflowServiceImpl) gbWorkflowService;
    }

    /**
     * 查询工作流列表
     */
    @PreAuthorize("@ss.hasPermi('content:workflow:query')")
    @GetMapping("/list")
    public TableDataInfo list(GbWorkflow gbWorkflow)
    {
        startPage();
        List<GbWorkflow> list = gbWorkflowService.selectGbWorkflowList(gbWorkflow);
        return getDataTable(list);
    }

    /**
     * 导出工作流列表
     */
    @PreAuthorize("@ss.hasPermi('content:workflow:export')")
    @Log(title = "工作流", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, GbWorkflow gbWorkflow)
    {
        List<GbWorkflow> list = gbWorkflowService.selectGbWorkflowList(gbWorkflow);
        ExcelUtil<GbWorkflow> util = new ExcelUtil<GbWorkflow>(GbWorkflow.class);
        util.exportExcel(response, list, "工作流数据");
    }

    /**
     * 获取工作流详细信息
     */
    @PreAuthorize("@ss.hasPermi('content:workflow:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(gbWorkflowService.selectGbWorkflowById(id));
    }

    /**
     * 根据工作流编码获取工作流定义
     */
    @PreAuthorize("@ss.hasPermi('content:workflow:query')")
    @GetMapping(value = "/code/{workflowCode}")
    public AjaxResult getByCode(@PathVariable("workflowCode") String workflowCode)
    {
        return success(gbWorkflowService.selectGbWorkflowByCode(workflowCode));
    }

    /**
     * 新增工作流
     */
    @PreAuthorize("@ss.hasPermi('content:workflow:add')")
    @Log(title = "工作流", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GbWorkflow gbWorkflow)
    {
        return toAjax(gbWorkflowService.insertGbWorkflow(gbWorkflow));
    }

    /**
     * 修改工作流
     */
    @PreAuthorize("@ss.hasPermi('content:workflow:edit')")
    @Log(title = "工作流", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GbWorkflow gbWorkflow)
    {
        return toAjax(gbWorkflowService.updateGbWorkflow(gbWorkflow));
    }

    /**
     * 删除工作流
     */
    @PreAuthorize("@ss.hasPermi('content:workflow:remove')")
    @Log(title = "工作流", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(gbWorkflowService.deleteGbWorkflowByIds(ids));
    }

    /**
     * 执行工作流
     */
    @PreAuthorize("@ss.hasPermi('content:workflow:execute')")
    @PostMapping("/execute/{workflowCode}")
    public AjaxResult execute(@PathVariable("workflowCode") String workflowCode, @RequestBody Map<String, Object> params)
    {
        try {
            String mode = (String) params.getOrDefault("mode", "sync");
            @SuppressWarnings("unchecked")
            Map<String, Object> inputData = (Map<String, Object>) params.get("inputData");

            // 两种模式都在后台线程中执行，避免 HTTP 超时
            // mode 字段原样传入 service，用于历史记录显示（sync=前台执行/async=后台执行/scheduled=定时）
            final Map<String, Object> asyncInput = inputData;
            final String executionId = java.util.UUID.randomUUID().toString();
            final String finalMode = mode;
            String _operator;
            try { _operator = SecurityUtils.getUsername(); } catch (Exception e2) { _operator = "system"; }
            final String asyncOperator = _operator;
            CompletableFuture.runAsync(() -> {
                SysUser sysUser = new SysUser();
                sysUser.setUserId(0L);
                sysUser.setUserName(asyncOperator);
                sysUser.setPassword("");
                LoginUser loginUser = new LoginUser(0L, null, sysUser, java.util.Collections.emptySet());
                SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(loginUser, null, java.util.Collections.emptyList())
                );
                try {
                    getServiceImpl().executeWorkflowWithId(workflowCode, finalMode, asyncInput, executionId);
                } catch (Exception ex) {
                    logger.error("工作流执行失败: workflowCode={}, mode={}", workflowCode, finalMode, ex);
                } finally {
                    SecurityContextHolder.clearContext();
                }
            });
            java.util.Map<String, Object> immediateResult = new java.util.HashMap<>();
            immediateResult.put("executionId", executionId);
            immediateResult.put("status", "running");
            return success(immediateResult);
        } catch (Exception e) {
            logger.error("工作流执行失败", e);
            return error("执行失败: " + e.getMessage());
        }
    }

    /**
     * 按 executionId 查询单条执行记录（前端轮询用）
     */
    @PreAuthorize("@ss.hasPermi('content:workflow:query')")
    @GetMapping("/execution/{executionId}")
    public AjaxResult getExecution(@PathVariable("executionId") String executionId)
    {
        return success(getServiceImpl().getExecution(executionId));
    }

    /**
     * 获取执行历史（支持按 status / mode 过滤）
     */
    @PreAuthorize("@ss.hasPermi('content:workflow:query')")
    @GetMapping("/execution/history")
    public TableDataInfo getExecutionHistory(GbWorkflowExecution query)
    {
        PageHelper.startPage(query.getPageNum() != null ? query.getPageNum() : 1,
                             query.getPageSize() != null ? query.getPageSize() : 10);
        List<GbWorkflowExecution> list = getServiceImpl().listExecutionHistory(query);
        return getDataTable(list);
    }

    /**
     * 获取指定工作流的执行统计（总次数、成功率、平均耗时等）
     */
    @PreAuthorize("@ss.hasPermi('content:workflow:query')")
    @GetMapping("/execution/stats/{workflowCode}")
    public AjaxResult getExecutionStats(@PathVariable("workflowCode") String workflowCode)
    {
        return success(getServiceImpl().getExecutionStats(workflowCode));
    }

    /**
     * 验证工作流定义
     */
    @PreAuthorize("@ss.hasPermi('content:workflow:query')")
    @PostMapping("/validate")
    public AjaxResult validate(@RequestBody GbWorkflow gbWorkflow)
    {
        try {
            List<String> errors = gbWorkflowService.validateWorkflow(gbWorkflow);
            if (errors.isEmpty()) {
                return success("验证通过");
            } else {
                return error(String.join("; ", errors));
            }
        } catch (Exception e) {
            logger.error("工作流验证失败", e);
            return error("验证失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有可用的原子工具列表
     */
    @PreAuthorize("@ss.hasPermi('content:workflow:query')")
    @GetMapping("/tools/available")
    public AjaxResult getAvailableTools(@RequestParam("siteId") Long siteId)
    {
        return success(gbWorkflowService.getAvailableTools(siteId));
    }

    /**
     * 验证步骤参数映射
     */
    @PreAuthorize("@ss.hasPermi('content:workflow:query')")
    @PostMapping("/step/validate-mapping")
    public AjaxResult validateStepMapping(@RequestBody Map<String, Object> params)
    {
        String toolCode = (String) params.get("toolCode");
        Map<String, Object> inputMapping = (Map<String, Object>) params.get("inputMapping");
        return success(gbWorkflowService.validateStepMapping(toolCode, inputMapping));
    }

    /**
     * 模拟执行工作流（测试模式，不保存数据）
     */
    @PreAuthorize("@ss.hasPermi('content:workflow:execute')")
    @PostMapping("/simulate")
    public AjaxResult simulate(@RequestBody Map<String, Object> params)
    {
        try {
            GbWorkflow workflow = (GbWorkflow) params.get("workflow");
            Map<String, Object> inputData = (Map<String, Object>) params.get("inputData");
            Map<String, Object> result = gbWorkflowService.simulateWorkflow(workflow, inputData);
            return success(result);
        } catch (Exception e) {
            logger.error("工作流模拟执行失败", e);
            return error("模拟执行失败: " + e.getMessage());
        }
    }

    /**
     * 获取工作流关联的网站列表
     */
    @PreAuthorize("@ss.hasPermi('content:workflow:query')")
    @GetMapping("/relation/sites/{workflowId}")
    public AjaxResult getWorkflowSites(@PathVariable Long workflowId)
    {
        return success(gbWorkflowService.getWorkflowSites(workflowId));
    }

    /**
     * 获取工作流已排除的网站列表
     */
    @PreAuthorize("@ss.hasPermi('content:workflow:query')")
    @GetMapping("/exclusions/{workflowId}")
    public AjaxResult getExcludedSites(@PathVariable Long workflowId)
    {
        List<Long> excludedSiteIds = gbWorkflowService.getExcludedSitesByWorkflow(workflowId);
        return AjaxResult.success(excludedSiteIds);
    }

    /**
     * 导入工作流（JSON格式）
     */
    @PreAuthorize("@ss.hasPermi('content:workflow:add')")
    @Log(title = "导入工作流", businessType = BusinessType.IMPORT)
    @PostMapping("/import")
    public AjaxResult importWorkflows(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false, defaultValue = "false") Boolean importRelatedTitles,
            @RequestParam(required = false, defaultValue = "false") Boolean importRelatedArticles,
            @RequestParam(required = false, defaultValue = "skip") String updateStrategy)
    {
        try {
            if (file.isEmpty()) {
                return error("上传文件不能为空");
            }
            
            // 读取JSON文件
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> data = objectMapper.readValue(file.getInputStream(), Map.class);
            
            Map<String, Object> result = gbWorkflowService.importWorkflows(
                data, importRelatedTitles, importRelatedArticles, updateStrategy);
            return success(result);
        } catch (IOException e) {
            logger.error("读取导入文件失败", e);
            return error("读取文件失败: " + e.getMessage());
        } catch (Exception e) {
            logger.error("导入工作流失败", e);
            return error("导入失败: " + e.getMessage());
        }
    }

    /**
     * 全站导入工作流（JSON格式）
     */
    @PreAuthorize("@ss.hasPermi('content:workflow:add')")
    @Log(title = "全站导入工作流", businessType = BusinessType.IMPORT)
    @PostMapping("/importAll")
    public AjaxResult importAllWorkflows(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false, defaultValue = "false") Boolean importRelatedTitles,
            @RequestParam(required = false, defaultValue = "false") Boolean importRelatedArticles,
            @RequestParam(required = false, defaultValue = "skip") String updateStrategy,
            @RequestParam(required = false, defaultValue = "false") Boolean clearExisting)
    {
        try {
            if (file.isEmpty()) {
                return error("上传文件不能为空");
            }
            
            // 读取JSON文件
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> data = objectMapper.readValue(file.getInputStream(), Map.class);
            
            Map<String, Object> result = gbWorkflowService.importAllWorkflows(
                data, importRelatedTitles, importRelatedArticles, updateStrategy, clearExisting);
            return success(result);
        } catch (IOException e) {
            logger.error("读取全站导入文件失败", e);
            return error("读取文件失败: " + e.getMessage());
        } catch (Exception e) {
            logger.error("全站导入工作流失败", e);
            return error("全站导入失败: " + e.getMessage());
        }
    }

    // ====================================================================
    // 步骤执行记录
    // ====================================================================

    /**
     * 查询某次工作流执行的所有步骤执行记录
     */
    @PreAuthorize("@ss.hasPermi('content:workflow:query')")
    @GetMapping("/execution/{executionId}/steps")
    public AjaxResult getStepExecutions(@PathVariable("executionId") String executionId)
    {
        return success(getServiceImpl().getStepExecutions(executionId));
    }

    // ====================================================================
    // 工作流模板
    // ====================================================================

    /**
     * 查询模板列表
     */
    @PreAuthorize("@ss.hasPermi('content:workflow:query')")
    @GetMapping("/template/list")
    public AjaxResult listTemplates(GbWorkflowTemplate query)
    {
        return success(getServiceImpl().listTemplates(query));
    }

    /**
     * 获取模板详情（含 definition）
     */
    @PreAuthorize("@ss.hasPermi('content:workflow:query')")
    @GetMapping("/template/{id}")
    public AjaxResult getTemplate(@PathVariable Long id)
    {
        return success(getServiceImpl().getTemplateById(id));
    }

    /**
     * 从模板创建工作流
     */
    @PreAuthorize("@ss.hasPermi('content:workflow:add')")
    @Log(title = "从模板创建工作流", businessType = BusinessType.INSERT)
    @PostMapping("/template/{templateId}/create-workflow")
    public AjaxResult createWorkflowFromTemplate(
            @PathVariable Long templateId,
            @RequestBody(required = false) GbWorkflow tpl)
    {
        try {
            GbWorkflow created = getServiceImpl().createWorkflowFromTemplate(templateId, tpl);
            return success(created);
        } catch (Exception e) {
            logger.error("从模板创建工作流失败", e);
            return error("创建失败: " + e.getMessage());
        }
    }

    /**
     * 新增模板
     */
    @PreAuthorize("@ss.hasPermi('content:workflow:add')")
    @Log(title = "工作流模板", businessType = BusinessType.INSERT)
    @PostMapping("/template")
    public AjaxResult addTemplate(@RequestBody GbWorkflowTemplate template)
    {
        template.setCreateBy(SecurityUtils.getUsername());
        return toAjax(getServiceImpl().addTemplate(template));
    }

    /**
     * 更新模板
     */
    @PreAuthorize("@ss.hasPermi('content:workflow:edit')")
    @Log(title = "工作流模板", businessType = BusinessType.UPDATE)
    @PutMapping("/template")
    public AjaxResult updateTemplate(@RequestBody GbWorkflowTemplate template)
    {
        template.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(getServiceImpl().updateTemplate(template));
    }

    /**
     * 删除模板
     */
    @PreAuthorize("@ss.hasPermi('content:workflow:remove')")
    @Log(title = "工作流模板", businessType = BusinessType.DELETE)
    @DeleteMapping("/template/{id}")
    public AjaxResult deleteTemplate(@PathVariable Long id)
    {
        return toAjax(getServiceImpl().deleteTemplate(id));
    }

}
