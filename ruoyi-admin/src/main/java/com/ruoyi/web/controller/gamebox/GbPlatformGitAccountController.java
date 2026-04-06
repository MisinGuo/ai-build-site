package com.ruoyi.web.controller.gamebox;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.gamebox.domain.GbPlatformGitAccount;
import com.ruoyi.gamebox.service.IGbPlatformGitAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 平台Git账号管理 Controller
 */
@RestController
@RequestMapping("/gamebox/git-accounts")
public class GbPlatformGitAccountController extends BaseController {

    @Autowired
    private IGbPlatformGitAccountService service;

    /** 列表 */
    @PreAuthorize("@ss.hasPermi('gamebox:gitAccount:list')")
    @GetMapping("/list")
    public TableDataInfo list(GbPlatformGitAccount query) {
        startPage();
        List<GbPlatformGitAccount> list = service.selectList(query);
        return getDataTable(list);
    }

    /** 启用账号下拉（供向导使用，无需权限校验） */
    @GetMapping("/enabled")
    public AjaxResult enabledList() {
        return success(service.selectEnabledList());
    }

    /** 详情 */
    @PreAuthorize("@ss.hasPermi('gamebox:gitAccount:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        return success(service.selectById(id));
    }

    /** 新增 */
    @PreAuthorize("@ss.hasPermi('gamebox:gitAccount:add')")
    @Log(title = "Git账号管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GbPlatformGitAccount account) {
        return toAjax(service.insert(account));
    }

    /** 修改 */
    @PreAuthorize("@ss.hasPermi('gamebox:gitAccount:edit')")
    @Log(title = "Git账号管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GbPlatformGitAccount account) {
        return toAjax(service.update(account));
    }

    /** 删除 */
    @PreAuthorize("@ss.hasPermi('gamebox:gitAccount:remove')")
    @Log(title = "Git账号管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(service.deleteByIds(ids));
    }

    /** 设为默认 */
    @PreAuthorize("@ss.hasPermi('gamebox:gitAccount:edit')")
    @Log(title = "Git账号管理", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/default")
    public AjaxResult setDefault(@PathVariable Long id) {
        return toAjax(service.setDefault(id));
    }

    /** 验证账号 Token */
    @PreAuthorize("@ss.hasPermi('gamebox:gitAccount:query')")
    @GetMapping("/{id}/verify")
    public AjaxResult verify(@PathVariable Long id) {
        return success(service.verify(id));
    }
}
