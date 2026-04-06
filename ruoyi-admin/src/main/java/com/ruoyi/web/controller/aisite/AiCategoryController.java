package com.ruoyi.web.controller.aisite;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.aisite.domain.AiCategory;
import com.ruoyi.aisite.service.IAiCategoryService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;

/**
 * 通用分类管理
 */
@RestController
@RequestMapping("/aisite/categories")
public class AiCategoryController extends BaseController
{
    @Autowired
    private IAiCategoryService aiCategoryService;

    @PreAuthorize("@ss.hasPermi('aisite:category:list')")
    @GetMapping("/list")
    public TableDataInfo list(AiCategory query)
    {
        startPage();
        List<AiCategory> list = aiCategoryService.selectAiCategoryList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('aisite:category:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return success(aiCategoryService.selectAiCategoryById(id));
    }

    @PreAuthorize("@ss.hasPermi('aisite:category:add')")
    @Log(title = "通用分类", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody AiCategory entity)
    {
        return toAjax(aiCategoryService.insertAiCategory(entity));
    }

    @PreAuthorize("@ss.hasPermi('aisite:category:edit')")
    @Log(title = "通用分类", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody AiCategory entity)
    {
        return toAjax(aiCategoryService.updateAiCategory(entity));
    }

    @PreAuthorize("@ss.hasPermi('aisite:category:remove')")
    @Log(title = "通用分类", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(aiCategoryService.deleteAiCategoryByIds(ids));
    }
}
