package com.ruoyi.web.controller.aisite;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.aisite.domain.AiMatrixGroup;
import com.ruoyi.aisite.service.IAiMatrixGroupService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;

/**
 * 矩阵站组管理
 */
@RestController
@RequestMapping("/aisite/matrix")
public class AiMatrixGroupController extends BaseController
{
    @Autowired
    private IAiMatrixGroupService aiMatrixGroupService;

    @PreAuthorize("@ss.hasPermi('aisite:matrix:list')")
    @GetMapping("/list")
    public TableDataInfo list(AiMatrixGroup query)
    {
        startPage();
        List<AiMatrixGroup> list = aiMatrixGroupService.selectAiMatrixGroupList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('aisite:matrix:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return success(aiMatrixGroupService.selectAiMatrixGroupById(id));
    }

    @PreAuthorize("@ss.hasPermi('aisite:matrix:add')")
    @Log(title = "矩阵站组", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AiMatrixGroup entity)
    {
        entity.setCreateBy(getUsername());
        return toAjax(aiMatrixGroupService.insertAiMatrixGroup(entity));
    }

    @PreAuthorize("@ss.hasPermi('aisite:matrix:edit')")
    @Log(title = "矩阵站组", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AiMatrixGroup entity)
    {
        entity.setUpdateBy(getUsername());
        return toAjax(aiMatrixGroupService.updateAiMatrixGroup(entity));
    }

    @PreAuthorize("@ss.hasPermi('aisite:matrix:remove')")
    @Log(title = "矩阵站组", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(aiMatrixGroupService.deleteAiMatrixGroupByIds(ids));
    }
}
