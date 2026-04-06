package com.ruoyi.web.controller.aisite;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.aisite.domain.AiSite;
import com.ruoyi.aisite.service.IAiSiteService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;

/**
 * 站点管理
 */
@RestController
@RequestMapping("/aisite/sites")
public class AiSiteController extends BaseController
{
    @Autowired
    private IAiSiteService aiSiteService;

    @PreAuthorize("@ss.hasPermi('aisite:site:list')")
    @GetMapping("/list")
    public TableDataInfo list(AiSite query)
    {
        startPage();
        List<AiSite> list = aiSiteService.selectAiSiteList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('aisite:site:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return success(aiSiteService.selectAiSiteById(id));
    }

    @PreAuthorize("@ss.hasPermi('aisite:site:add')")
    @Log(title = "站点管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody AiSite entity)
    {
        return toAjax(aiSiteService.insertAiSite(entity));
    }

    @PreAuthorize("@ss.hasPermi('aisite:site:edit')")
    @Log(title = "站点管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody AiSite entity)
    {
        return toAjax(aiSiteService.updateAiSite(entity));
    }

    @PreAuthorize("@ss.hasPermi('aisite:site:remove')")
    @Log(title = "站点管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(aiSiteService.deleteAiSiteByIds(ids));
    }
}
