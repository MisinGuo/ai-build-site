package com.ruoyi.web.controller.gamebox;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.gamebox.domain.GbSiteTemplate;
import com.ruoyi.gamebox.service.IGbSiteTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 站点模板管理 Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/gamebox/site-templates")
public class GbSiteTemplateController extends BaseController {

    @Autowired
    private IGbSiteTemplateService siteTemplateService;

    /**
     * 查询模板列表（管理端，含 gitAccountId）
     */
    @PreAuthorize("@ss.hasPermi('gamebox:siteTemplate:list')")
    @GetMapping("/list")
    public TableDataInfo list(GbSiteTemplate query) {
        startPage();
        List<GbSiteTemplate> list = siteTemplateService.selectTemplateList(query);
        return getDataTable(list);
    }

    /**
     * 获取已启用模板列表（向导选模板用，仅需登录，不暴露 gitAccountId 等敏感字段）
     */
    @GetMapping("/enabled")
    public AjaxResult listEnabled() {
        GbSiteTemplate query = new GbSiteTemplate();
        query.setStatus("1");
        List<GbSiteTemplate> list = siteTemplateService.selectTemplateList(query);
        // 抹掉凭据字段，确保不泄露给前端普通用户
        list.forEach(t -> t.setGitAccountId(null));
        return success(list);
    }

    /**
     * 获取模板详情
     */
    @PreAuthorize("@ss.hasPermi('gamebox:siteTemplate:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        return success(siteTemplateService.selectTemplateById(id));
    }

    /**
     * 新增模板
     */
    @PreAuthorize("@ss.hasPermi('gamebox:siteTemplate:add')")
    @Log(title = "站点模板", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GbSiteTemplate template) {
        return toAjax(siteTemplateService.insertTemplate(template));
    }

    /**
     * 修改模板
     */
    @PreAuthorize("@ss.hasPermi('gamebox:siteTemplate:edit')")
    @Log(title = "站点模板", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GbSiteTemplate template) {
        return toAjax(siteTemplateService.updateTemplate(template));
    }

    /**
     * 删除模板
     */
    @PreAuthorize("@ss.hasPermi('gamebox:siteTemplate:remove')")
    @Log(title = "站点模板", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(siteTemplateService.deleteTemplateByIds(ids));
    }
}
