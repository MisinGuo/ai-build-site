package com.ruoyi.web.controller.aisite;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.aisite.domain.AiOperationTask;
import com.ruoyi.aisite.service.IAiOperationTaskService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;

/**
 * AI 运营任务管理
 */
@RestController
@RequestMapping("/aisite/operations")
public class AiOperationTaskController extends BaseController
{
    @Autowired
    private IAiOperationTaskService aiOperationTaskService;

    @PreAuthorize("@ss.hasPermi('aisite:operation:list')")
    @GetMapping("/list")
    public TableDataInfo list(AiOperationTask query)
    {
        startPage();
        List<AiOperationTask> list = aiOperationTaskService.selectAiOperationTaskList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('aisite:operation:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return success(aiOperationTaskService.selectAiOperationTaskById(id));
    }

    @PreAuthorize("@ss.hasPermi('aisite:operation:add')")
    @Log(title = "运营任务", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AiOperationTask entity)
    {
        entity.setCreateBy(getUsername());
        return toAjax(aiOperationTaskService.insertAiOperationTask(entity));
    }

    @PreAuthorize("@ss.hasPermi('aisite:operation:edit')")
    @Log(title = "运营任务", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AiOperationTask entity)
    {
        entity.setUpdateBy(getUsername());
        return toAjax(aiOperationTaskService.updateAiOperationTask(entity));
    }

    @PreAuthorize("@ss.hasPermi('aisite:operation:remove')")
    @Log(title = "运营任务", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(aiOperationTaskService.deleteAiOperationTaskByIds(ids));
    }
}
