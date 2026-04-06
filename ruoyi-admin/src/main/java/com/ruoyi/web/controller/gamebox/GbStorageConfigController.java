package com.ruoyi.web.controller.gamebox;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.gamebox.domain.GbStorageConfig;
import com.ruoyi.gamebox.service.IGbStorageConfigService;

/**
 * 存储配置管理Controller
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/gamebox/storage")
public class GbStorageConfigController extends BaseController
{
    @Autowired
    private IGbStorageConfigService gbStorageConfigService;

    /**
     * 查询存储配置列表
     */
    @PreAuthorize("@ss.hasPermi('gamebox:storage:list')")
    @GetMapping("/list")
    public TableDataInfo list(GbStorageConfig gbStorageConfig)
    {
        startPage();
        List<GbStorageConfig> list = gbStorageConfigService.selectGbStorageConfigList(gbStorageConfig);
        return getDataTable(list);
    }

    /**
     * 导出存储配置列表
     */
    @PreAuthorize("@ss.hasPermi('gamebox:storage:export')")
    @Log(title = "存储配置管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(GbStorageConfig gbStorageConfig)
    {
        List<GbStorageConfig> list = gbStorageConfigService.selectGbStorageConfigList(gbStorageConfig);
        ExcelUtil<GbStorageConfig> util = new ExcelUtil<GbStorageConfig>(GbStorageConfig.class);
        util.exportExcel(list, "存储配置数据");
    }

    /**
     * 获取存储配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('gamebox:storage:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(gbStorageConfigService.selectGbStorageConfigById(id));
    }

    /**
     * 新增存储配置
     */
    @PreAuthorize("@ss.hasPermi('gamebox:storage:add')")
    @Log(title = "存储配置管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody GbStorageConfig gbStorageConfig)
    {
        gbStorageConfig.setCreateBy(getUsername());
        int result = gbStorageConfigService.insertGbStorageConfig(gbStorageConfig);
        if (result > 0) {
            return AjaxResult.success("新增成功", gbStorageConfig.getId());
        }
        return error("新增失败");
    }

    /**
     * 修改存储配置
     */
    @PreAuthorize("@ss.hasPermi('gamebox:storage:edit')")
    @Log(title = "存储配置管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody GbStorageConfig gbStorageConfig)
    {
        gbStorageConfig.setUpdateBy(getUsername());
        return toAjax(gbStorageConfigService.updateGbStorageConfig(gbStorageConfig));
    }

    /**
     * 删除存储配置
     */
    @PreAuthorize("@ss.hasPermi('gamebox:storage:remove')")
    @Log(title = "存储配置管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(gbStorageConfigService.deleteGbStorageConfigByIds(ids));
    }

    /**
     * 验证存储配置
     */
    @PreAuthorize("@ss.hasPermi('gamebox:storage:edit')")
    @Log(title = "存储配置管理", businessType = BusinessType.OTHER)
    @PostMapping("/validate")
    public AjaxResult validate(@RequestBody GbStorageConfig gbStorageConfig)
    {
        String result = gbStorageConfigService.validateConfig(gbStorageConfig);
        if ("success".equals(result))
        {
            return success("配置验证成功");
        }
        else
        {
            return error(result);
        }
    }
}
