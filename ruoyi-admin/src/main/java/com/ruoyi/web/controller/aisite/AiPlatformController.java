package com.ruoyi.web.controller.aisite;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.aisite.domain.AiPlatform;
import com.ruoyi.aisite.service.IAiPlatformService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;

/**
 * AI 平台配置管理
 */
@RestController
@RequestMapping("/aisite/platforms")
public class AiPlatformController extends BaseController
{
    @Autowired
    private IAiPlatformService aiPlatformService;

    @PreAuthorize("@ss.hasPermi('aisite:platform:list')")
    @GetMapping("/list")
    public TableDataInfo list(AiPlatform query)
    {
        startPage();
        List<AiPlatform> list = aiPlatformService.selectAiPlatformList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('aisite:platform:list')")
    @GetMapping("/all")
    public AjaxResult all(AiPlatform query)
    {
        return success(aiPlatformService.selectAiPlatformList(query));
    }

    @PreAuthorize("@ss.hasPermi('aisite:platform:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return success(aiPlatformService.selectAiPlatformById(id));
    }

    @PreAuthorize("@ss.hasPermi('aisite:platform:add')")
    @Log(title = "AI平台配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AiPlatform entity)
    {
        entity.setCreateBy(getUsername());
        return toAjax(aiPlatformService.insertAiPlatform(entity));
    }

    @PreAuthorize("@ss.hasPermi('aisite:platform:edit')")
    @Log(title = "AI平台配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AiPlatform entity)
    {
        entity.setUpdateBy(getUsername());
        return toAjax(aiPlatformService.updateAiPlatform(entity));
    }

    @PreAuthorize("@ss.hasPermi('aisite:platform:remove')")
    @Log(title = "AI平台配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(aiPlatformService.deleteAiPlatformByIds(ids));
    }
}
