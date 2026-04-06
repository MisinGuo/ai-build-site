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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.gamebox.domain.GbAiPlatform;
import com.ruoyi.gamebox.service.IGbAiPlatformService;

/**
 * AI平台配置管理Controller
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/gamebox/platform")
public class GbAiPlatformController extends BaseController
{
    @Autowired
    private IGbAiPlatformService gbAiPlatformService;

    /**
     * 查询AI平台配置列表
     */
    @PreAuthorize("@ss.hasPermi('gamebox:platform:list')")
    @GetMapping("/list")
    public TableDataInfo list(GbAiPlatform gbAiPlatform)
    {
        startPage();
        List<GbAiPlatform> list = gbAiPlatformService.selectGbAiPlatformList(gbAiPlatform);
        return getDataTable(list);
    }

    /**
     * 查询可见AI平台配置列表（用于表单下拉框，不包含被排除的数据）
     */
    @PreAuthorize("@ss.hasPermi('gamebox:platform:list')")
    @GetMapping("/visibleList")
    public AjaxResult visibleList(GbAiPlatform gbAiPlatform)
    {
        // 强制使用关联模式，返回当前网站可见的所有AI平台（自有+默认+共享）
        gbAiPlatform.setQueryMode("related");
        // 强制设置isExcluded=0，只查询未被排除的数据
        gbAiPlatform.setIsExcluded(0);
        List<GbAiPlatform> list = gbAiPlatformService.selectGbAiPlatformList(gbAiPlatform);
        // 只返回可见的数据（is_visible = '1'）
        list.removeIf(platform -> !"1".equals(platform.getIsVisible()));
        return success(list);
    }

    /**
     * 导出AI平台配置列表
     */
    @PreAuthorize("@ss.hasPermi('gamebox:platform:export')")
    @Log(title = "AI平台配置管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(GbAiPlatform gbAiPlatform)
    {
        List<GbAiPlatform> list = gbAiPlatformService.selectGbAiPlatformList(gbAiPlatform);
        ExcelUtil<GbAiPlatform> util = new ExcelUtil<GbAiPlatform>(GbAiPlatform.class);
        util.exportExcel(list, "AI平台配置数据");
    }

    /**
     * 获取AI平台配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('gamebox:platform:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(gbAiPlatformService.selectGbAiPlatformById(id));
    }

    /**
     * 新增AI平台配置
     */
    @PreAuthorize("@ss.hasPermi('gamebox:platform:add')")
    @Log(title = "AI平台配置管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody GbAiPlatform gbAiPlatform)
    {
        gbAiPlatform.setCreateBy(getUsername());
        int result = gbAiPlatformService.insertGbAiPlatform(gbAiPlatform);
        if (result > 0) {
            return AjaxResult.success("新增成功", gbAiPlatform.getId());
        }
        return error("新增失败");
    }

    /**
     * 修改AI平台配置
     */
    @PreAuthorize("@ss.hasPermi('gamebox:platform:edit')")
    @Log(title = "AI平台配置管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody GbAiPlatform gbAiPlatform)
    {
        gbAiPlatform.setUpdateBy(getUsername());
        return toAjax(gbAiPlatformService.updateGbAiPlatform(gbAiPlatform));
    }

    /**
     * 删除AI平台配置
     */
    @PreAuthorize("@ss.hasPermi('gamebox:platform:remove')")
    @Log(title = "AI平台配置管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(gbAiPlatformService.deleteGbAiPlatformByIds(ids));
    }

    /**
     * 根据平台类型获取可用模型列表
     */
    @PreAuthorize("@ss.hasPermi('gamebox:platform:query')")
    @GetMapping("/models/{platformType}")
    public AjaxResult getModels(
        @PathVariable String platformType,
        @RequestParam(required = false) String apiKey,
        @RequestParam(required = false) String baseUrl)
    {
        // 打印接收到的参数用于调试
        System.out.println("=== 接收到获取模型请求 ===");
        System.out.println("platformType: " + platformType);
        System.out.println("apiKey: " + (apiKey != null ? "***" + apiKey.substring(Math.max(0, apiKey.length() - 4)) : "null"));
        System.out.println("baseUrl: " + baseUrl);
        System.out.println("=========================");
        
        return success(gbAiPlatformService.getAvailableModels(platformType, apiKey, baseUrl));
    }

    /**
     * 测试AI平台连接
     */
    @PreAuthorize("@ss.hasPermi('gamebox:platform:edit')")
    @PostMapping("/test/{id}")
    public AjaxResult testPlatform(@PathVariable("id") Long id)
    {
        try {
            // 调用 service 执行测试并返回结构化数据
            java.util.Map<String, Object> data = gbAiPlatformService.testConnection(id);
            AjaxResult result = success(data);
            result.put("msg", "测试完成");
            return result;
        } catch (Exception e) {
            return error("测试失败: " + e.getMessage());
        }
    }
}
