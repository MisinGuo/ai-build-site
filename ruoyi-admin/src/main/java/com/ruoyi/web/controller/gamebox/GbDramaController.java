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
import com.ruoyi.gamebox.domain.GbDrama;
import com.ruoyi.gamebox.service.IGbDramaService;

/**
 * 短剧管理Controller
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/gamebox/drama")
public class GbDramaController extends BaseController
{
    @Autowired
    private IGbDramaService gbDramaService;

    /**
     * 查询短剧列表
     */
    @PreAuthorize("@ss.hasPermi('gamebox:drama:list')")
    @GetMapping("/list")
    public TableDataInfo list(GbDrama gbDrama)
    {
        startPage();
        List<GbDrama> list = gbDramaService.selectGbDramaList(gbDrama);
        return getDataTable(list);
    }

    /**
     * 导出短剧列表
     */
    @PreAuthorize("@ss.hasPermi('gamebox:drama:export')")
    @Log(title = "短剧管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(GbDrama gbDrama)
    {
        List<GbDrama> list = gbDramaService.selectGbDramaList(gbDrama);
        ExcelUtil<GbDrama> util = new ExcelUtil<GbDrama>(GbDrama.class);
        util.exportExcel(list, "短剧数据");
    }

    /**
     * 获取短剧详细信息
     */
    @PreAuthorize("@ss.hasPermi('gamebox:drama:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(gbDramaService.selectGbDramaById(id));
    }

    /**
     * 新增短剧
     */
    @PreAuthorize("@ss.hasPermi('gamebox:drama:add')")
    @Log(title = "短剧管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody GbDrama gbDrama)
    {
        gbDrama.setCreateBy(getUsername());
        return toAjax(gbDramaService.insertGbDrama(gbDrama));
    }

    /**
     * 修改短剧
     */
    @PreAuthorize("@ss.hasPermi('gamebox:drama:edit')")
    @Log(title = "短剧管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody GbDrama gbDrama)
    {
        gbDrama.setUpdateBy(getUsername());
        return toAjax(gbDramaService.updateGbDrama(gbDrama));
    }

    /**
     * 删除短剧
     */
    @PreAuthorize("@ss.hasPermi('gamebox:drama:remove')")
    @Log(title = "短剧管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(gbDramaService.deleteGbDramaByIds(ids));
    }
}
