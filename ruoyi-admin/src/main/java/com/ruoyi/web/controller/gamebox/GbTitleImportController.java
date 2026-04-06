package com.ruoyi.web.controller.gamebox;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.gamebox.domain.GbArticleTitlePool;
import com.ruoyi.gamebox.domain.GbTitleImportLog;
import com.ruoyi.gamebox.mapper.GbArticleTitlePoolMapper;
import com.ruoyi.gamebox.service.ITitleImportService;
import com.ruoyi.gamebox.support.RelatedModeSiteValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 标题导入Controller
 * 
 * @author ruoyi
 * @date 2025-12-30
 */
@RestController
@RequestMapping("/gamebox/title")
public class GbTitleImportController extends BaseController
{
    @Autowired
    private ITitleImportService titleImportService;

    @Autowired
    private GbArticleTitlePoolMapper titlePoolMapper;

    @Autowired
    private RelatedModeSiteValidator relatedModeSiteValidator;

    /**
     * 导入JSON数据
     */
    @PreAuthorize("@ss.hasPermi('gamebox:title:import')")
    @Log(title = "标题导入", businessType = BusinessType.IMPORT)
    @PostMapping("/import/json")
    public AjaxResult importJson(@RequestBody Map<String, Object> params)
    {
        String jsonData = (String) params.get("jsonData");
        Object siteIdObj = params.get("siteId");
        Object categoryIdObj = params.get("categoryId");
        String batchName = (String) params.get("batchName");
        String sourceName = (String) params.get("importSource");
        
        if (jsonData == null || jsonData.trim().isEmpty()) {
            return error("JSON数据不能为空");
        }
        if (siteIdObj == null) {
            return error("所属网站不能为空");
        }

        try {
            Long siteId = siteIdObj instanceof Integer ? ((Integer) siteIdObj).longValue() : (Long) siteIdObj;
            Long categoryId = categoryIdObj != null ? (categoryIdObj instanceof Integer ? ((Integer) categoryIdObj).longValue() : (Long) categoryIdObj) : null;
            
            GbTitleImportLog log = titleImportService.importFromJson(jsonData, siteId, categoryId, batchName, sourceName);
            return success(log);
        } catch (Exception e) {
            return error("导入失败: " + e.getMessage());
        }
    }

    /**
     * 导入Excel文件
     */
    @PreAuthorize("@ss.hasPermi('gamebox:title:import')")
    @Log(title = "标题导入", businessType = BusinessType.IMPORT)
    @PostMapping("/import/excel")
    public AjaxResult importExcel(@RequestParam("file") MultipartFile file,
                                   @RequestParam("siteId") Long siteId,
                                   @RequestParam(value = "categoryId", required = false) Long categoryId,
                                   @RequestParam(value = "batchName", required = false) String batchName,
                                   @RequestParam(value = "importSource", required = false) String sourceName)
    {
        if (file.isEmpty()) {
            return error("上传文件不能为空");
        }
        if (siteId == null) {
            return error("所属网站不能为空");
        }

        try {
            GbTitleImportLog log = titleImportService.importFromExcel(file, siteId, categoryId, batchName, sourceName);
            return success(log);
        } catch (Exception e) {
            return error("导入失败: " + e.getMessage());
        }
    }

    /**
     * 查询标题池列表
     */
    @PreAuthorize("@ss.hasPermi('gamebox:title:list')")
    @GetMapping("/pool/list")
    public TableDataInfo list(GbArticleTitlePool titlePool)
    {
        relatedModeSiteValidator.validate(titlePool.getQueryMode(), titlePool.getSiteId());
        startPage();
        List<GbArticleTitlePool> list = titlePoolMapper.selectGbArticleTitlePoolList(titlePool);
        return getDataTable(list);
    }

    /**
     * 获取标题池详细信息
     */
    @PreAuthorize("@ss.hasPermi('gamebox:title:query')")
    @GetMapping("/pool/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(titlePoolMapper.selectGbArticleTitlePoolById(id));
    }

    /**
     * 新增标题
     */
    @PreAuthorize("@ss.hasPermi('gamebox:title:add')")
    @Log(title = "标题池", businessType = BusinessType.INSERT)
    @PostMapping("/pool")
    public AjaxResult add(@RequestBody GbArticleTitlePool titlePool)
    {
        return toAjax(titlePoolMapper.insertGbArticleTitlePool(titlePool));
    }

    /**
     * 修改标题
     */
    @PreAuthorize("@ss.hasPermi('gamebox:title:edit')")
    @Log(title = "标题池", businessType = BusinessType.UPDATE)
    @PutMapping("/pool")
    public AjaxResult edit(@RequestBody GbArticleTitlePool titlePool)
    {
        return toAjax(titlePoolMapper.updateGbArticleTitlePool(titlePool));
    }

    /**
     * 删除标题
     */
    @PreAuthorize("@ss.hasPermi('gamebox:title:remove')")
    @Log(title = "标题池", businessType = BusinessType.DELETE)
	@DeleteMapping("/pool/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(titlePoolMapper.deleteGbArticleTitlePoolByIds(ids));
    }

    /**
     * 查询导入历史
     */
    @PreAuthorize("@ss.hasPermi('gamebox:title:list')")
    @GetMapping("/import/history")
    public TableDataInfo importHistory(@RequestParam(required = false) String importBatch)
    {
        startPage();
        List<GbTitleImportLog> list = titleImportService.queryImportHistory(importBatch);
        return getDataTable(list);
    }

    /**
     * 获取导入统计
     */
    @PreAuthorize("@ss.hasPermi('gamebox:title:list')")
    @GetMapping("/import/statistics")
    public AjaxResult getStatistics()
    {
        return success(titleImportService.getImportStatistics());
    }

    /**
     * 获取未使用的标题
     */
    @PreAuthorize("@ss.hasPermi('gamebox:title:list')")
    @GetMapping("/pool/unused")
    public AjaxResult getUnusedTitles(@RequestParam(defaultValue = "10") int limit)
    {
        List<GbArticleTitlePool> list = titlePoolMapper.selectUnusedTitles(limit);
        return success(list);
    }
}
