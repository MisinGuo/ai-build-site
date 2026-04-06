package com.ruoyi.web.controller.aisite;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.aisite.domain.AiContentItem;
import com.ruoyi.aisite.service.IAiContentItemService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;

/**
 * 内容条目管理
 */
@RestController
@RequestMapping("/aisite/content-items")
public class AiContentItemController extends BaseController
{
    @Autowired
    private IAiContentItemService aiContentItemService;

    @PreAuthorize("@ss.hasPermi('aisite:contentItem:list')")
    @GetMapping("/list")
    public TableDataInfo list(AiContentItem query)
    {
        startPage();
        List<AiContentItem> list = aiContentItemService.selectAiContentItemList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('aisite:contentItem:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return success(aiContentItemService.selectAiContentItemById(id));
    }

    @PreAuthorize("@ss.hasPermi('aisite:contentItem:add')")
    @Log(title = "内容条目", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody AiContentItem entity)
    {
        return toAjax(aiContentItemService.insertAiContentItem(entity));
    }

    @PreAuthorize("@ss.hasPermi('aisite:contentItem:edit')")
    @Log(title = "内容条目", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody AiContentItem entity)
    {
        return toAjax(aiContentItemService.updateAiContentItem(entity));
    }

    @PreAuthorize("@ss.hasPermi('aisite:contentItem:remove')")
    @Log(title = "内容条目", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(aiContentItemService.deleteAiContentItemByIds(ids));
    }
}
