package com.ruoyi.gamebox.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.gamebox.domain.GbGameChangeLog;
import com.ruoyi.gamebox.domain.GbImportBatch;
import com.ruoyi.gamebox.service.IGbGameChangeLogService;
import com.ruoyi.gamebox.service.IGbImportBatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 游戏导入批次 & 变更日志 Controller
 * 提供批次列表、变更明细、撤回等接口。
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/gamebox/import")
public class GbImportBatchController extends BaseController {

    @Autowired
    private IGbImportBatchService importBatchService;

    @Autowired
    private IGbGameChangeLogService changeLogService;

    // ──────────────────────────────────────────────────────────────────────────
    // 批次管理
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * 查询导入批次列表（分页）
     */
    @PreAuthorize("@ss.hasPermi('gamebox:importBatch:list')")
    @GetMapping("/batch/list")
    public TableDataInfo batchList(GbImportBatch query) {
        startPage();
        List<GbImportBatch> list = importBatchService.selectList(query);
        return getDataTable(list);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 变更日志 —— 按批次
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * 查询批次变更记录（batchId / batchNo 二选一）
     */
    @PreAuthorize("@ss.hasPermi('gamebox:importBatch:query')")
    @GetMapping("/batch/changes")
    public AjaxResult batchChanges(@RequestParam(required = false) Long batchId,
                                   @RequestParam(required = false) String batchNo) {
        if (batchId == null && !StringUtils.hasText(batchNo)) {
            return AjaxResult.error("缺少批次参数：batchId 或 batchNo 需至少提供一个");
        }
        List<GbGameChangeLog> logs = batchId != null
                ? changeLogService.selectByBatchId(batchId)
                : changeLogService.selectByBatchNo(batchNo.trim());
        return AjaxResult.success(logs);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 变更日志 —— 按游戏
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * 查询指定游戏的全部历史变更（时间倒序）
     */
    @PreAuthorize("@ss.hasPermi('gamebox:importBatch:query')")
    @GetMapping("/game/{gameId}/history")
    public AjaxResult gameHistory(@PathVariable Long gameId) {
        List<GbGameChangeLog> logs = changeLogService.selectByGameId(gameId);
        return AjaxResult.success(logs);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 单条变更撤回 / 重新应用
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * 撤回单条变更记录
     */
    @PreAuthorize("@ss.hasPermi('gamebox:importBatch:revert')")
    @PostMapping("/change/{id}/revert")
    public AjaxResult revertChange(@PathVariable Long id) {
        try {
            changeLogService.revertChange(id, getUsername());
            return AjaxResult.success("变更已撤回");
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 批次 + 指定游戏的撤回 / 重新应用
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * 撤回整批（batchId / batchNo 二选一）
     */
    @PreAuthorize("@ss.hasPermi('gamebox:importBatch:revert')")
    @PostMapping("/batch/revert")
    public AjaxResult revertBatch(@RequestParam(required = false) Long batchId,
                                  @RequestParam(required = false) String batchNo) {
        if (batchId == null && !StringUtils.hasText(batchNo)) {
            return AjaxResult.error("缺少批次参数：batchId 或 batchNo 需至少提供一个");
        }
        try {
            int reverted = batchId != null
                    ? changeLogService.revertBatch(batchId, getUsername())
                    : changeLogService.revertBatchByBatchNo(batchNo.trim(), getUsername());
            return AjaxResult.success("成功撤回 " + reverted + " 条变更");
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 重新应用整批（batchId / batchNo 二选一）
     */
    @PreAuthorize("@ss.hasPermi('gamebox:importBatch:revert')")
    @PostMapping("/batch/reapply")
    public AjaxResult reApplyBatch(@RequestParam(required = false) Long batchId,
                                   @RequestParam(required = false) String batchNo) {
        if (batchId == null && !StringUtils.hasText(batchNo)) {
            return AjaxResult.error("缺少批次参数：batchId 或 batchNo 需至少提供一个");
        }
        try {
            int applied = batchId != null
                    ? changeLogService.reApplyBatch(batchId, getUsername())
                    : changeLogService.reApplyBatchByBatchNo(batchNo.trim(), getUsername());
            return AjaxResult.success("成功重新应用 " + applied + " 条变更");
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 撤回指定批次中指定游戏的全部变更（batchId / batchNo 二选一）
     */
    @PreAuthorize("@ss.hasPermi('gamebox:importBatch:revert')")
    @PostMapping("/batch/game/revert")
    public AjaxResult revertByBatchAndGame(@RequestParam Long gameId,
                                           @RequestParam(required = false) Long batchId,
                                           @RequestParam(required = false) String batchNo) {
        if (batchId == null && !StringUtils.hasText(batchNo)) {
            return AjaxResult.error("缺少批次参数：batchId 或 batchNo 需至少提供一个");
        }
        try {
            int reverted = batchId != null
                    ? changeLogService.revertByBatchAndGame(batchId, gameId, getUsername())
                    : changeLogService.revertByBatchNoAndGame(batchNo.trim(), gameId, getUsername());
            return AjaxResult.success("成功撤回 " + reverted + " 条变更");
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 重新应用指定批次中指定游戏的全部已撤回变更（batchId / batchNo 二选一）
     */
    @PreAuthorize("@ss.hasPermi('gamebox:importBatch:revert')")
    @PostMapping("/batch/game/reapply")
    public AjaxResult reApplyByBatchAndGame(@RequestParam Long gameId,
                                            @RequestParam(required = false) Long batchId,
                                            @RequestParam(required = false) String batchNo) {
        if (batchId == null && !StringUtils.hasText(batchNo)) {
            return AjaxResult.error("缺少批次参数：batchId 或 batchNo 需至少提供一个");
        }
        try {
            int applied = batchId != null
                    ? changeLogService.reApplyByBatchAndGame(batchId, gameId, getUsername())
                    : changeLogService.reApplyByBatchNoAndGame(batchNo.trim(), gameId, getUsername());
            return AjaxResult.success("成功重新应用 " + applied + " 条变更");
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }
}
