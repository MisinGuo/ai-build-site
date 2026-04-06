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
import com.ruoyi.gamebox.domain.GbSite;
import com.ruoyi.gamebox.service.IGbSiteService;

/**
 * 站点管理Controller
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/gamebox/site")
public class GbSiteController extends BaseController
{
    @Autowired
    private IGbSiteService gbSiteService;

    /**
     * 查询站点列表（只返回当前用户有权限的站点）
     */
    @PreAuthorize("@ss.hasPermi('gamebox:site:list')")
    @GetMapping("/list")
    public TableDataInfo list(GbSite gbSite)
    {
        startPage();
        List<GbSite> list = gbSiteService.selectGbSiteListForCurrentUser(gbSite);
        return getDataTable(list);
    }

    /**
     * 导出站点列表（只导出当前用户有权限的站点）
     */
    @PreAuthorize("@ss.hasPermi('gamebox:site:export')")
    @Log(title = "站点管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(GbSite gbSite)
    {
        List<GbSite> list = gbSiteService.selectGbSiteListForCurrentUser(gbSite);
        ExcelUtil<GbSite> util = new ExcelUtil<GbSite>(GbSite.class);
        util.exportExcel(list, "站点数据");
    }

    /**
     * 获取站点详细信息
     */
    @PreAuthorize("@ss.hasPermi('gamebox:site:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(gbSiteService.selectGbSiteById(id));
    }

    /**
     * 新增站点
     */
    @PreAuthorize("@ss.hasPermi('gamebox:site:add')")
    @Log(title = "站点管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody GbSite gbSite)
    {
        gbSite.setCreateBy(getUsername());
        gbSiteService.insertGbSite(gbSite);
        return AjaxResult.success(gbSite.getId());
    }

    /**
     * 修改站点
     */
    @PreAuthorize("@ss.hasPermi('gamebox:site:edit')")
    @Log(title = "站点管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody GbSite gbSite)
    {
        gbSite.setUpdateBy(getUsername());
        return toAjax(gbSiteService.updateGbSite(gbSite));
    }

    /**
     * 删除站点
     */
    @PreAuthorize("@ss.hasPermi('gamebox:site:remove')")
    @Log(title = "站点管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(gbSiteService.deleteGbSiteByIds(ids));
    }
}
