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
import com.ruoyi.gamebox.domain.GbDramaVendor;
import com.ruoyi.gamebox.service.IGbDramaVendorService;

/**
 * 短剧厂商管理Controller
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/gamebox/vendor")
public class GbDramaVendorController extends BaseController
{
    @Autowired
    private IGbDramaVendorService gbDramaVendorService;

    /**
     * 查询短剧厂商列表
     */
    @PreAuthorize("@ss.hasPermi('gamebox:vendor:list')")
    @GetMapping("/list")
    public TableDataInfo list(GbDramaVendor gbDramaVendor)
    {
        startPage();
        List<GbDramaVendor> list = gbDramaVendorService.selectGbDramaVendorList(gbDramaVendor);
        return getDataTable(list);
    }

    /**
     * 导出短剧厂商列表
     */
    @PreAuthorize("@ss.hasPermi('gamebox:vendor:export')")
    @Log(title = "短剧厂商管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(GbDramaVendor gbDramaVendor)
    {
        List<GbDramaVendor> list = gbDramaVendorService.selectGbDramaVendorList(gbDramaVendor);
        ExcelUtil<GbDramaVendor> util = new ExcelUtil<GbDramaVendor>(GbDramaVendor.class);
        util.exportExcel(list, "短剧厂商数据");
    }

    /**
     * 获取短剧厂商详细信息
     */
    @PreAuthorize("@ss.hasPermi('gamebox:vendor:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(gbDramaVendorService.selectGbDramaVendorById(id));
    }

    /**
     * 新增短剧厂商
     */
    @PreAuthorize("@ss.hasPermi('gamebox:vendor:add')")
    @Log(title = "短剧厂商管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody GbDramaVendor gbDramaVendor)
    {
        gbDramaVendor.setCreateBy(getUsername());
        return toAjax(gbDramaVendorService.insertGbDramaVendor(gbDramaVendor));
    }

    /**
     * 修改短剧厂商
     */
    @PreAuthorize("@ss.hasPermi('gamebox:vendor:edit')")
    @Log(title = "短剧厂商管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody GbDramaVendor gbDramaVendor)
    {
        gbDramaVendor.setUpdateBy(getUsername());
        return toAjax(gbDramaVendorService.updateGbDramaVendor(gbDramaVendor));
    }

    /**
     * 删除短剧厂商
     */
    @PreAuthorize("@ss.hasPermi('gamebox:vendor:remove')")
    @Log(title = "短剧厂商管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(gbDramaVendorService.deleteGbDramaVendorByIds(ids));
    }
}
