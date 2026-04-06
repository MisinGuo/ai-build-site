package com.ruoyi.web.controller.aisite;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.aisite.domain.AiSeoMetrics;
import com.ruoyi.aisite.service.IAiSeoMetricsService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * SEO 指标看板
 */
@RestController
@RequestMapping("/aisite/seo")
public class AiSeoMetricsController extends BaseController
{
    @Autowired
    private IAiSeoMetricsService aiSeoMetricsService;

    @PreAuthorize("@ss.hasPermi('aisite:seo:view')")
    @GetMapping("/list")
    public TableDataInfo list(AiSeoMetrics query)
    {
        startPage();
        List<AiSeoMetrics> list = aiSeoMetricsService.selectAiSeoMetricsList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('aisite:seo:view')")
    @GetMapping("/site/{siteId}")
    public AjaxResult getBySite(@PathVariable Long siteId)
    {
        return success(aiSeoMetricsService.selectAiSeoMetricsBySiteId(siteId));
    }

    @PreAuthorize("@ss.hasPermi('aisite:seo:view')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return success(aiSeoMetricsService.selectAiSeoMetricsById(id));
    }

    @PreAuthorize("@ss.hasPermi('aisite:seo:edit')")
    @PostMapping
    public AjaxResult add(@RequestBody AiSeoMetrics entity)
    {
        return toAjax(aiSeoMetricsService.insertAiSeoMetrics(entity));
    }

    @PreAuthorize("@ss.hasPermi('aisite:seo:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody AiSeoMetrics entity)
    {
        return toAjax(aiSeoMetricsService.updateAiSeoMetrics(entity));
    }

    @PreAuthorize("@ss.hasPermi('aisite:seo:remove')")
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id)
    {
        return toAjax(aiSeoMetricsService.deleteAiSeoMetricsById(id));
    }
}
