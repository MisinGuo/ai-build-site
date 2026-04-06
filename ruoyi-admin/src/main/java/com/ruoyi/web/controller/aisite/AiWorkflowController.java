package com.ruoyi.web.controller.aisite;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.aisite.domain.AiWorkflow;
import com.ruoyi.aisite.service.IAiWorkflowService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;

/**
 * 工作流管理
 */
@RestController
@RequestMapping("/aisite/workflows")
public class AiWorkflowController extends BaseController
{
    @Autowired
    private IAiWorkflowService aiWorkflowService;

    @PreAuthorize("@ss.hasPermi('aisite:workflow:list')")
    @GetMapping("/list")
    public TableDataInfo list(AiWorkflow query)
    {
        startPage();
        List<AiWorkflow> list = aiWorkflowService.selectAiWorkflowList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('aisite:workflow:list')")
    @GetMapping("/all")
    public AjaxResult all(AiWorkflow query)
    {
        return success(aiWorkflowService.selectAiWorkflowList(query));
    }

    @PreAuthorize("@ss.hasPermi('aisite:workflow:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return success(aiWorkflowService.selectAiWorkflowById(id));
    }

    @PreAuthorize("@ss.hasPermi('aisite:workflow:query')")
    @GetMapping("/code/{code}")
    public AjaxResult getByCode(@PathVariable String code)
    {
        return success(aiWorkflowService.selectAiWorkflowByCode(code));
    }

    @PreAuthorize("@ss.hasPermi('aisite:workflow:add')")
    @Log(title = "工作流", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AiWorkflow entity)
    {
        entity.setCreateBy(getUsername());
        return toAjax(aiWorkflowService.insertAiWorkflow(entity));
    }

    @PreAuthorize("@ss.hasPermi('aisite:workflow:edit')")
    @Log(title = "工作流", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AiWorkflow entity)
    {
        entity.setUpdateBy(getUsername());
        return toAjax(aiWorkflowService.updateAiWorkflow(entity));
    }

    @PreAuthorize("@ss.hasPermi('aisite:workflow:remove')")
    @Log(title = "工作流", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(aiWorkflowService.deleteAiWorkflowByIds(ids));
    }
}
