package com.ruoyi.web.controller.aisite;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.aisite.domain.AiAtomicTool;
import com.ruoyi.aisite.service.IAiAtomicToolService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;

/**
 * 原子工具管理
 */
@RestController
@RequestMapping("/aisite/atomicTools")
public class AiAtomicToolController extends BaseController
{
    @Autowired
    private IAiAtomicToolService aiAtomicToolService;

    @PreAuthorize("@ss.hasPermi('aisite:atomicTool:list')")
    @GetMapping("/list")
    public TableDataInfo list(AiAtomicTool query)
    {
        startPage();
        List<AiAtomicTool> list = aiAtomicToolService.selectAiAtomicToolList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('aisite:atomicTool:list')")
    @GetMapping("/all")
    public AjaxResult all(AiAtomicTool query)
    {
        return success(aiAtomicToolService.selectAiAtomicToolList(query));
    }

    @PreAuthorize("@ss.hasPermi('aisite:atomicTool:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return success(aiAtomicToolService.selectAiAtomicToolById(id));
    }

    @PreAuthorize("@ss.hasPermi('aisite:atomicTool:add')")
    @Log(title = "原子工具", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AiAtomicTool entity)
    {
        return toAjax(aiAtomicToolService.insertAiAtomicTool(entity));
    }

    @PreAuthorize("@ss.hasPermi('aisite:atomicTool:edit')")
    @Log(title = "原子工具", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AiAtomicTool entity)
    {
        return toAjax(aiAtomicToolService.updateAiAtomicTool(entity));
    }

    @PreAuthorize("@ss.hasPermi('aisite:atomicTool:remove')")
    @Log(title = "原子工具", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(aiAtomicToolService.deleteAiAtomicToolByIds(ids));
    }
}
