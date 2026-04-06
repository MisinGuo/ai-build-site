package com.ruoyi.web.controller.aisite;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.aisite.domain.AiPromptTemplate;
import com.ruoyi.aisite.service.IAiPromptTemplateService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;

/**
 * Prompt 模板管理
 */
@RestController
@RequestMapping("/aisite/promptTemplates")
public class AiPromptTemplateController extends BaseController
{
    @Autowired
    private IAiPromptTemplateService promptTemplateService;

    @PreAuthorize("@ss.hasPermi('aisite:promptTemplate:list')")
    @GetMapping("/list")
    public TableDataInfo list(AiPromptTemplate query)
    {
        startPage();
        List<AiPromptTemplate> list = promptTemplateService.selectList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('aisite:promptTemplate:list')")
    @GetMapping("/all")
    public AjaxResult all(AiPromptTemplate query)
    {
        return success(promptTemplateService.selectList(query));
    }

    @PreAuthorize("@ss.hasPermi('aisite:promptTemplate:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return success(promptTemplateService.selectById(id));
    }

    @PreAuthorize("@ss.hasPermi('aisite:promptTemplate:query')")
    @GetMapping("/code/{code}")
    public AjaxResult getByCode(@PathVariable String code)
    {
        return success(promptTemplateService.selectByCode(code));
    }

    @PreAuthorize("@ss.hasPermi('aisite:promptTemplate:add')")
    @Log(title = "Prompt模板", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AiPromptTemplate entity)
    {
        entity.setCreateBy(getUsername());
        return toAjax(promptTemplateService.insert(entity));
    }

    @PreAuthorize("@ss.hasPermi('aisite:promptTemplate:edit')")
    @Log(title = "Prompt模板", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AiPromptTemplate entity)
    {
        entity.setUpdateBy(getUsername());
        return toAjax(promptTemplateService.update(entity));
    }

    @PreAuthorize("@ss.hasPermi('aisite:promptTemplate:remove')")
    @Log(title = "Prompt模板", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(promptTemplateService.deleteByIds(ids));
    }
}
