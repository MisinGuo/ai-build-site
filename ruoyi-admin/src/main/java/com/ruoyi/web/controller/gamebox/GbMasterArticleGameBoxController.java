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
import com.ruoyi.gamebox.domain.GbMasterArticleGameBox;
import com.ruoyi.gamebox.service.IGbMasterArticleGameBoxService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.web.controller.gamebox.dto.GbMasterArticleAssociationDTO;

/**
 * 主文章-GameBox关联Controller
 * 
 * @author ruoyi
 * @date 2026-01-12
 */
@RestController
@RequestMapping("/gamebox/masterArticleGameBox")
public class GbMasterArticleGameBoxController extends BaseController
{
    @Autowired
    private IGbMasterArticleGameBoxService gbMasterArticleGameBoxService;

    /**
     * 查询主文章-GameBox关联列表
     */
    @PreAuthorize("@ss.hasPermi('gamebox:masterArticleGameBox:list')")
    @GetMapping("/list")
    public TableDataInfo list(GbMasterArticleGameBox gbMasterArticleGameBox)
    {
        startPage();
        List<GbMasterArticleGameBox> list = gbMasterArticleGameBoxService.selectGbMasterArticleGameBoxList(gbMasterArticleGameBox);
        return getDataTable(list);
    }

    /**
     * 根据主文章ID查询关联列表
     */
    @PreAuthorize("@ss.hasPermi('gamebox:masterArticleGameBox:list')")
    @GetMapping("/listByMasterArticle/{masterArticleId}")
    public AjaxResult listByMasterArticle(@PathVariable("masterArticleId") Long masterArticleId)
    {
        List<GbMasterArticleGameBox> list = gbMasterArticleGameBoxService.selectGbMasterArticleGameBoxByMasterArticleId(masterArticleId);
        return AjaxResult.success(list);
    }

    /**
     * 导出主文章-GameBox关联列表
     */
    @PreAuthorize("@ss.hasPermi('gamebox:masterArticleGameBox:export')")
    @Log(title = "主文章-GameBox关联", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, GbMasterArticleGameBox gbMasterArticleGameBox)
    {
        List<GbMasterArticleGameBox> list = gbMasterArticleGameBoxService.selectGbMasterArticleGameBoxList(gbMasterArticleGameBox);
        ExcelUtil<GbMasterArticleGameBox> util = new ExcelUtil<GbMasterArticleGameBox>(GbMasterArticleGameBox.class);
        util.exportExcel(response, list, "主文章-GameBox关联数据");
    }

    /**
     * 获取主文章-GameBox关联详细信息
     */
    @PreAuthorize("@ss.hasPermi('gamebox:masterArticleGameBox:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(gbMasterArticleGameBoxService.selectGbMasterArticleGameBoxById(id));
    }

    /**
     * 新增主文章-GameBox关联
     */
    @PreAuthorize("@ss.hasPermi('gamebox:masterArticleGameBox:add')")
    @Log(title = "主文章-GameBox关联", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GbMasterArticleGameBox gbMasterArticleGameBox)
    {
        // 检查是否已存在关联
        if (gbMasterArticleGameBoxService.checkAssociationExists(
            gbMasterArticleGameBox.getMasterArticleId(), 
            gbMasterArticleGameBox.getGameBoxId())) {
            return AjaxResult.error("该关联关系已存在");
        }

        return toAjax(gbMasterArticleGameBoxService.insertGbMasterArticleGameBox(gbMasterArticleGameBox));
    }

    /**
     * 批量添加GameBox关联
     */
    @PreAuthorize("@ss.hasPermi('gamebox:masterArticleGameBox:add')")
    @Log(title = "批量添加GameBox关联", businessType = BusinessType.INSERT)
    @PostMapping("/batchAdd")
    public AjaxResult batchAdd(@RequestBody GbMasterArticleAssociationDTO dto)
    {
        int addedCount = gbMasterArticleGameBoxService.batchAddGameBoxAssociations(
            dto.getMasterArticleId(), 
            dto.getGameBoxIds(), 
            dto.getRelationSource(), 
            dto.getRelationType(), 
            getUsername()
        );
        return AjaxResult.success("成功添加 " + addedCount + " 个关联关系");
    }

    /**
     * 修改主文章-GameBox关联
     */
    @PreAuthorize("@ss.hasPermi('gamebox:masterArticleGameBox:edit')")
    @Log(title = "主文章-GameBox关联", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GbMasterArticleGameBox gbMasterArticleGameBox)
    {
        return toAjax(gbMasterArticleGameBoxService.updateGbMasterArticleGameBox(gbMasterArticleGameBox));
    }

    /**
     * 删除主文章-GameBox关联
     */
    @PreAuthorize("@ss.hasPermi('gamebox:masterArticleGameBox:remove')")
    @Log(title = "主文章-GameBox关联", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(gbMasterArticleGameBoxService.deleteGbMasterArticleGameBoxByIds(ids));
    }

    /**
     * 审核关联
     */
    @PreAuthorize("@ss.hasPermi('gamebox:masterArticleGameBox:review')")
    @Log(title = "审核关联", businessType = BusinessType.UPDATE)
    @PutMapping("/review/{id}")
    public AjaxResult review(@PathVariable("id") Long id, @RequestBody GbMasterArticleGameBox request)
    {
        int result = gbMasterArticleGameBoxService.updateReviewStatus(
            id, 
            request.getReviewStatus(), 
            getUsername(), 
            request.getReviewNotes()
        );
        if (result > 0) {
            return AjaxResult.success("审核成功");
        } else {
            return AjaxResult.error("审核失败");
        }
    }

    /**
     * 批量审核关联
     */
    @PreAuthorize("@ss.hasPermi('gamebox:masterArticleGameBox:review')")
    @Log(title = "批量审核关联", businessType = BusinessType.UPDATE)
    @PutMapping("/batchReview")
    public AjaxResult batchReview(@RequestBody GbMasterArticleGameBox request)
    {
        int updateCount = gbMasterArticleGameBoxService.batchReview(
            request.getIds(), 
            request.getReviewStatus(), 
            getUsername(), 
            request.getReviewNotes()
        );
        return AjaxResult.success("成功审核 " + updateCount + " 条记录");
    }

    /**
     * 设置推荐状态
     */
    @PreAuthorize("@ss.hasPermi('gamebox:masterArticleGameBox:edit')")
    @Log(title = "设置推荐状态", businessType = BusinessType.UPDATE)
    @PutMapping("/featured/{id}")
    public AjaxResult setFeatured(@PathVariable("id") Long id, @RequestBody GbMasterArticleGameBox request)
    {
        int result = gbMasterArticleGameBoxService.setFeaturedStatus(
            id, 
            request.getIsFeatured(), 
            getUsername()
        );
        if (result > 0) {
            return AjaxResult.success("设置推荐状态成功");
        } else {
            return AjaxResult.error("设置推荐状态失败");
        }
    }

    /**
     * 增加点击次数
     */
    @PostMapping("/click/{id}")
    public AjaxResult incrementClick(@PathVariable("id") Long id)
    {
        int result = gbMasterArticleGameBoxService.incrementClickCount(id);
        if (result > 0) {
            return AjaxResult.success("记录点击成功");
        } else {
            return AjaxResult.error("记录点击失败");
        }
    }

    /**
     * 检查关联是否存在
     */
    @GetMapping("/checkExists")
    public AjaxResult checkExists(Long masterArticleId, Long gameBoxId)
    {
        boolean exists = gbMasterArticleGameBoxService.checkAssociationExists(masterArticleId, gameBoxId);
        return AjaxResult.success(!exists);
    }
}