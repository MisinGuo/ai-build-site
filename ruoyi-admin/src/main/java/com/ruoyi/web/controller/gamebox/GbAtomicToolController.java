package com.ruoyi.web.controller.gamebox;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.gamebox.domain.GbAtomicTool;
import com.ruoyi.gamebox.service.IGbAtomicToolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 原子工具Controller
 * 
 * @author ruoyi
 * @date 2026-01-04
 */
@RestController
@RequestMapping("/gamebox/atomicTool")
public class GbAtomicToolController extends BaseController
{
    @Autowired
    private IGbAtomicToolService gbAtomicToolService;

    /**
     * 查询原子工具列表
     */
    @PreAuthorize("@ss.hasPermi('gamebox:atomicTool:list')")
    @GetMapping("/list")
    public TableDataInfo list(GbAtomicTool gbAtomicTool)
    {
        startPage();
        List<GbAtomicTool> list = gbAtomicToolService.selectGbAtomicToolList(gbAtomicTool);
        return getDataTable(list);
    }

    /**
     * 导出原子工具列表
     */
    @PreAuthorize("@ss.hasPermi('gamebox:atomicTool:export')")
    @Log(title = "原子工具", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, GbAtomicTool gbAtomicTool)
    {
        List<GbAtomicTool> list = gbAtomicToolService.selectGbAtomicToolList(gbAtomicTool);
        ExcelUtil<GbAtomicTool> util = new ExcelUtil<GbAtomicTool>(GbAtomicTool.class);
        util.exportExcel(response, list, "原子工具数据");
    }

    /**
     * 获取原子工具详细信息
     */
    @PreAuthorize("@ss.hasPermi('gamebox:atomicTool:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(gbAtomicToolService.selectGbAtomicToolById(id));
    }

    /**
     * 获取所有可用的系统工具执行器列表
     */
    @PreAuthorize("@ss.hasPermi('gamebox:atomicTool:query')")
    @GetMapping("/executors")
    public AjaxResult getExecutors()
    {
        List<Map<String, Object>> executors = gbAtomicToolService.getAvailableExecutors();
        return success(executors);
    }

    /**
     * 新增原子工具
     */
    @PreAuthorize("@ss.hasPermi('gamebox:atomicTool:add')")
    @Log(title = "原子工具", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GbAtomicTool gbAtomicTool)
    {
        return toAjax(gbAtomicToolService.insertGbAtomicTool(gbAtomicTool));
    }

    /**
     * 修改原子工具
     */
    @PreAuthorize("@ss.hasPermi('gamebox:atomicTool:edit')")
    @Log(title = "原子工具", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GbAtomicTool gbAtomicTool)
    {
        return toAjax(gbAtomicToolService.updateGbAtomicTool(gbAtomicTool));
    }

    /**
     * 删除原子工具
     */
    @PreAuthorize("@ss.hasPermi('gamebox:atomicTool:remove')")
    @Log(title = "原子工具", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(gbAtomicToolService.deleteGbAtomicToolByIds(ids));
    }

    /**
     * 测试工具执行（已禁用，请使用 /executeById/{id}）
     */
    @PreAuthorize("@ss.hasPermi('gamebox:atomicTool:test')")
    @PostMapping("/test/{toolCode}")
    public AjaxResult test(@PathVariable("toolCode") String toolCode, @RequestBody Map<String, Object> params)
    {
        return error("不支持通过工具代码执行，请使用 /executeById/{id} 接口");
    }

    /**
     * 执行工具（已禁用，请使用 /executeById/{id}）
     */
    @PreAuthorize("@ss.hasPermi('gamebox:atomicTool:execute')")
    @PostMapping("/execute/{toolCode}")
    public AjaxResult execute(@PathVariable("toolCode") String toolCode, @RequestBody Map<String, Object> params)
    {
        return error("不支持通过工具代码执行，请使用 /executeById/{id} 接口");
    }

    /**
     * 执行工具（通过ID）
     */
    @PreAuthorize("@ss.hasPermi('gamebox:atomicTool:execute')")
    @PostMapping("/executeById/{id}")
    public AjaxResult executeById(@PathVariable("id") Long id, @RequestBody Map<String, Object> params)
    {
        try {
            Map<String, Object> result = gbAtomicToolService.executeToolById(id, params);
            return success(result);
        } catch (Exception e) {
            logger.error("工具执行失败", e);
            return error("执行失败: " + e.getMessage());
        }
    }
}
