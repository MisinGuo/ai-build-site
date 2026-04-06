package com.ruoyi.web.controller.aisite;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.aisite.domain.AiStorageConfig;
import com.ruoyi.aisite.service.IAiStorageConfigService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;

/**
 * 对象存储配置管理
 */
@RestController
@RequestMapping("/aisite/storageConfigs")
public class AiStorageConfigController extends BaseController
{
    @Autowired
    private IAiStorageConfigService storageConfigService;

    @PreAuthorize("@ss.hasPermi('aisite:storageConfig:list')")
    @GetMapping("/list")
    public TableDataInfo list(AiStorageConfig query)
    {
        startPage();
        List<AiStorageConfig> list = storageConfigService.selectList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('aisite:storageConfig:list')")
    @GetMapping("/all")
    public AjaxResult all(AiStorageConfig query)
    {
        return success(storageConfigService.selectList(query));
    }

    @PreAuthorize("@ss.hasPermi('aisite:storageConfig:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return success(storageConfigService.selectById(id));
    }

    @PreAuthorize("@ss.hasPermi('aisite:storageConfig:add')")
    @Log(title = "存储配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AiStorageConfig entity)
    {
        entity.setCreateBy(getUsername());
        return toAjax(storageConfigService.insert(entity));
    }

    @PreAuthorize("@ss.hasPermi('aisite:storageConfig:edit')")
    @Log(title = "存储配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AiStorageConfig entity)
    {
        entity.setUpdateBy(getUsername());
        return toAjax(storageConfigService.update(entity));
    }

    @PreAuthorize("@ss.hasPermi('aisite:storageConfig:remove')")
    @Log(title = "存储配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(storageConfigService.deleteByIds(ids));
    }
}
