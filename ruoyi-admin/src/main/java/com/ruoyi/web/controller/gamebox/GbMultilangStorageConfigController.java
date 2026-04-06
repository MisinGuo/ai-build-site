package com.ruoyi.web.controller.gamebox;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.gamebox.domain.GbMultilangStorageConfig;
import com.ruoyi.gamebox.service.IGbMultilangStorageConfigService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 多语言存储配置Controller
 * 
 * @author ruoyi
 * @date 2026-01-12
 */
@RestController
@RequestMapping("/gamebox/multilangStorageConfig")
public class GbMultilangStorageConfigController extends BaseController
{
    @Autowired
    private IGbMultilangStorageConfigService gbMultilangStorageConfigService;

    /**
     * 查询多语言存储配置列表
     */
    @PreAuthorize("@ss.hasPermi('gamebox:multilangStorageConfig:list')")
    @GetMapping("/list")
    public TableDataInfo list(GbMultilangStorageConfig gbMultilangStorageConfig)
    {
        startPage();
        List<GbMultilangStorageConfig> list = gbMultilangStorageConfigService.selectGbMultilangStorageConfigList(gbMultilangStorageConfig);
        return getDataTable(list);
    }

    /**
     * 查询启用的存储配置列表（不分页）
     */
    @PreAuthorize("@ss.hasPermi('gamebox:multilangStorageConfig:list')")
    @GetMapping("/enabled")
    public AjaxResult getEnabledConfigs()
    {
        List<GbMultilangStorageConfig> list = gbMultilangStorageConfigService.selectEnabledStorageConfigs();
        return AjaxResult.success(list);
    }

    /**
     * 查询默认存储配置
     */
    @PreAuthorize("@ss.hasPermi('gamebox:multilangStorageConfig:list')")
    @GetMapping("/default")
    public AjaxResult getDefaultConfig()
    {
        GbMultilangStorageConfig defaultConfig = gbMultilangStorageConfigService.selectDefaultStorageConfig();
        return AjaxResult.success(defaultConfig);
    }

    /**
     * 导出多语言存储配置列表
     */
    @PreAuthorize("@ss.hasPermi('gamebox:multilangStorageConfig:export')")
    @Log(title = "多语言存储配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, GbMultilangStorageConfig gbMultilangStorageConfig)
    {
        List<GbMultilangStorageConfig> list = gbMultilangStorageConfigService.selectGbMultilangStorageConfigList(gbMultilangStorageConfig);
        ExcelUtil<GbMultilangStorageConfig> util = new ExcelUtil<GbMultilangStorageConfig>(GbMultilangStorageConfig.class);
        util.exportExcel(response, list, "多语言存储配置数据");
    }

    /**
     * 获取多语言存储配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('gamebox:multilangStorageConfig:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(gbMultilangStorageConfigService.selectGbMultilangStorageConfigById(id));
    }

    /**
     * 根据语言代码获取存储配置
     */
    @PreAuthorize("@ss.hasPermi('gamebox:multilangStorageConfig:query')")
    @GetMapping(value = "/lang/{langCode}")
    public AjaxResult getByLangCode(@PathVariable("langCode") String langCode)
    {
        GbMultilangStorageConfig config = gbMultilangStorageConfigService.selectGbMultilangStorageConfigByLangCode(langCode);
        return AjaxResult.success(config);
    }

    /**
     * 新增多语言存储配置
     */
    @PreAuthorize("@ss.hasPermi('gamebox:multilangStorageConfig:add')")
    @Log(title = "多语言存储配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GbMultilangStorageConfig gbMultilangStorageConfig)
    {
        try {
            return toAjax(gbMultilangStorageConfigService.insertGbMultilangStorageConfig(gbMultilangStorageConfig));
        } catch (RuntimeException e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 修改多语言存储配置
     */
    @PreAuthorize("@ss.hasPermi('gamebox:multilangStorageConfig:edit')")
    @Log(title = "多语言存储配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GbMultilangStorageConfig gbMultilangStorageConfig)
    {
        try {
            return toAjax(gbMultilangStorageConfigService.updateGbMultilangStorageConfig(gbMultilangStorageConfig));
        } catch (RuntimeException e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 删除多语言存储配置
     */
    @PreAuthorize("@ss.hasPermi('gamebox:multilangStorageConfig:remove')")
    @Log(title = "多语言存储配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(gbMultilangStorageConfigService.deleteGbMultilangStorageConfigByIds(ids));
    }

    /**
     * 测试存储连接
     */
    @PreAuthorize("@ss.hasPermi('gamebox:multilangStorageConfig:test')")
    @Log(title = "测试存储连接", businessType = BusinessType.OTHER)
    @PostMapping("/test/{id}")
    public AjaxResult testConnection(@PathVariable("id") Long id)
    {
        boolean result = gbMultilangStorageConfigService.testStorageConnection(id);
        if (result) {
            return AjaxResult.success("连接测试成功");
        } else {
            return AjaxResult.error("连接测试失败");
        }
    }

    /**
     * 设置为默认配置
     */
    @PreAuthorize("@ss.hasPermi('gamebox:multilangStorageConfig:edit')")
    @Log(title = "设置默认配置", businessType = BusinessType.UPDATE)
    @PutMapping("/setDefault/{id}")
    public AjaxResult setAsDefault(@PathVariable("id") Long id)
    {
        int result = gbMultilangStorageConfigService.setAsDefault(id);
        if (result > 0) {
            return AjaxResult.success("设置默认配置成功");
        } else {
            return AjaxResult.error("设置默认配置失败");
        }
    }

    /**
     * 批量启用/禁用
     */
    @PreAuthorize("@ss.hasPermi('gamebox:multilangStorageConfig:edit')")
    @Log(title = "批量更新状态", businessType = BusinessType.UPDATE)
    @PutMapping("/batchUpdateStatus")
    public AjaxResult batchUpdateStatus(@RequestBody GbMultilangStorageConfig request)
    {
        int updateCount = gbMultilangStorageConfigService.batchUpdateStatus(
            request.getIds(), 
            request.getStatus(), 
            getUsername()
        );
        return AjaxResult.success("成功更新 " + updateCount + " 条记录");
    }

    /**
     * 检查语言代码唯一性
     */
    @GetMapping("/checkLangCodeUnique")
    public AjaxResult checkLangCodeUnique(String langCode, Long id)
    {
        boolean exists = gbMultilangStorageConfigService.checkLangCodeExists(langCode, id);
        return AjaxResult.success(!exists);
    }

    /**
     * 根据语言代码获取存储路径
     */
    @GetMapping("/storagePath/{langCode}")
    public AjaxResult getStoragePath(@PathVariable("langCode") String langCode)
    {
        String storagePath = gbMultilangStorageConfigService.getStoragePathByLangCode(langCode);
        return AjaxResult.success("path", storagePath);
    }
}