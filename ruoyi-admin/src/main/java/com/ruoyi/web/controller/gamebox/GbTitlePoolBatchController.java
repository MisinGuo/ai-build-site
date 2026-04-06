package com.ruoyi.web.controller.gamebox;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.gamebox.domain.GbTitlePoolBatch;
import com.ruoyi.gamebox.mapper.GbTitlePoolBatchMapper;
import com.ruoyi.gamebox.mapper.GbSiteMapper;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 标题池批次Controller
 * 
 * @author ruoyi
 * @date 2026-01-01
 */
@RestController
@RequestMapping("/gamebox/title/batch")
public class GbTitlePoolBatchController extends BaseController
{
    @Autowired
    private GbTitlePoolBatchMapper batchMapper;

    @Autowired
    private GbSiteMapper gbSiteMapper;

    private void injectPersonalSiteId(GbTitlePoolBatch batch) {
        try {
            Long personalSiteId = gbSiteMapper.selectPersonalSiteIdByUserId(SecurityUtils.getUserId());
            if (personalSiteId != null) {
                batch.getParams().put("personalSiteId", personalSiteId);
            }
        } catch (Exception ignored) {}
}

    /**
     * 查询标题池批次列表
     */
    @PreAuthorize("@ss.hasPermi('gamebox:titlePool:list')")
    @GetMapping("/list")
    public TableDataInfo list(GbTitlePoolBatch batch)
    {
        startPage();
        injectPersonalSiteId(batch);
        List<GbTitlePoolBatch> list = batchMapper.selectGbTitlePoolBatchList(batch);
        return getDataTable(list);
    }

    /**
     * 查询可见标题池批次列表（用于表单下拉框，不包含被排除的数据）
     */
    @PreAuthorize("@ss.hasPermi('gamebox:titlePool:list')")
    @GetMapping("/visibleList")
    public AjaxResult visibleList(GbTitlePoolBatch batch)
    {
        // 强制使用关联模式，返回当前网站可见的所有批次（自有+默认+共享）
        batch.setQueryMode("related");
        // 强制设置isExcluded=0，只查询未被排除的数据
        batch.setIsExcluded("0");
        injectPersonalSiteId(batch);
        List<GbTitlePoolBatch> list = batchMapper.selectGbTitlePoolBatchList(batch);
        // 只返回可见的数据（is_visible = '1'）
        list.removeIf(b -> !"1".equals(b.getIsVisible()));
        return success(list);
    }

    /**
     * 导出标题池批次列表
     */
    @PreAuthorize("@ss.hasPermi('gamebox:titlePool:export')")
    @Log(title = "标题池批次", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, GbTitlePoolBatch batch)
    {
        injectPersonalSiteId(batch);
        List<GbTitlePoolBatch> list = batchMapper.selectGbTitlePoolBatchList(batch);
        ExcelUtil<GbTitlePoolBatch> util = new ExcelUtil<GbTitlePoolBatch>(GbTitlePoolBatch.class);
        util.exportExcel(response, list, "标题池批次数据");
    }

    /**
     * 获取标题池批次详细信息
     */
    @PreAuthorize("@ss.hasPermi('gamebox:titlePool:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(batchMapper.selectGbTitlePoolBatchById(id));
    }

    /**
     * 根据批次号获取批次详细信息
     */
    @PreAuthorize("@ss.hasPermi('gamebox:titlePool:query')")
    @GetMapping(value = "/code/{batchCode}")
    public AjaxResult getInfoByCode(@PathVariable("batchCode") String batchCode)
    {
        return success(batchMapper.selectGbTitlePoolBatchByCode(batchCode));
    }

    /**
     * 新增标题池批次
     */
    @PreAuthorize("@ss.hasPermi('gamebox:titlePool:add')")
    @Log(title = "标题池批次", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GbTitlePoolBatch batch)
    {
        // 自动生成批次编号
        if (batch.getBatchCode() == null || batch.getBatchCode().isEmpty()) {
            batch.setBatchCode("BATCH_" + System.currentTimeMillis());
        }
        // 设置导入日期为当前日期
        if (batch.getImportDate() == null) {
            batch.setImportDate(new java.util.Date());
        }
        // 设置默认标题数量
        if (batch.getTitleCount() == null) {
            batch.setTitleCount(0);
        }
        // 设置默认状态
        if (batch.getStatus() == null || batch.getStatus().isEmpty()) {
            batch.setStatus("1"); // 默认启用
        }
        return toAjax(batchMapper.insertGbTitlePoolBatch(batch));
    }

    /**
     * 修改标题池批次
     */
    @PreAuthorize("@ss.hasPermi('gamebox:titlePool:edit')")
    @Log(title = "标题池批次", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GbTitlePoolBatch batch)
    {
        return toAjax(batchMapper.updateGbTitlePoolBatch(batch));
    }

    /**
     * 删除标题池批次
     */
    @PreAuthorize("@ss.hasPermi('gamebox:titlePool:remove')")
    @Log(title = "标题池批次", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        // 先逻辑删除批次关联的标题
        batchMapper.markTitlesAsDeletedByBatchIds(ids);
        // 再删除批次记录
        return toAjax(batchMapper.deleteGbTitlePoolBatchByIds(ids));
    }

    /**
     * 更新批次的标题数量
     */
    @PreAuthorize("@ss.hasPermi('gamebox:titlePool:edit')")
    @Log(title = "更新批次标题数量", businessType = BusinessType.UPDATE)
    @PutMapping("/updateCount/{batchCode}")
    public AjaxResult updateTitleCount(@PathVariable String batchCode)
    {
        return toAjax(batchMapper.updateBatchTitleCount(batchCode));
    }

    /**
     * 更新批次本身的可见性
     */
    @PreAuthorize("@ss.hasPermi('gamebox:titlePool:edit')")
    @Log(title = "更新批次可见性", businessType = BusinessType.UPDATE)
    @PutMapping("/toggleVisibility")
    public AjaxResult toggleVisibility(@RequestBody Map<String, Object> params)
    {
        Long batchId = Long.valueOf(params.get("batchId").toString());
        Integer isVisible = Integer.valueOf(params.get("isVisible").toString());
        GbTitlePoolBatch batch = new GbTitlePoolBatch();
        batch.setId(batchId);
        batch.setIsVisible(isVisible == 1 ? "1" : "0");
        return toAjax(batchMapper.updateGbTitlePoolBatch(batch));
    }

}
