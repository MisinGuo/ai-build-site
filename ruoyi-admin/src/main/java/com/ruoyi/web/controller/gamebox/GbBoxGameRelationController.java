package com.ruoyi.web.controller.gamebox;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
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
import com.ruoyi.gamebox.domain.GbBoxGameRelation;
import com.ruoyi.gamebox.domain.GbGame;
import com.ruoyi.gamebox.domain.GbGameChangeLog;
import com.ruoyi.gamebox.mapper.GbGameChangeLogMapper;
import com.ruoyi.gamebox.mapper.GbSiteMapper;
import com.ruoyi.gamebox.service.IGbBoxGameRelationService;
import com.ruoyi.gamebox.service.IGbGameService;
import com.ruoyi.common.utils.SecurityUtils;
import com.alibaba.fastjson2.JSON;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 游戏盒子-游戏关联Controller
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/gamebox/boxgame")
public class GbBoxGameRelationController extends BaseController
{
    @Autowired
    private IGbBoxGameRelationService gbBoxGameRelationService;

    @Autowired
    private IGbGameService gbGameService;

    @Autowired
    private GbGameChangeLogMapper gameChangeLogMapper;

    @Autowired
    private GbSiteMapper gbSiteMapper;

    private static final String TABLE_RELATION = "gb_box_game_relations";

    /**
     * 查询游戏盒子-游戏关联列表
     */
    @PreAuthorize("@ss.hasPermi('gamebox:boxgame:list')")
    @GetMapping("/list")
    public TableDataInfo list(GbBoxGameRelation gbBoxGameRelation)
    {
        startPage();
        List<GbBoxGameRelation> list = gbBoxGameRelationService.selectGbBoxGameRelationList(gbBoxGameRelation);
        return getDataTable(list);
    }

    /**
     * 根据盒子ID查询关联的游戏列表
     */
    @PreAuthorize("@ss.hasPermi('gamebox:boxgame:list')")
    @GetMapping("/box/{boxId}")
    public AjaxResult getGamesByBoxId(@PathVariable Long boxId)
    {
        List<GbBoxGameRelation> list = gbBoxGameRelationService.selectGamesByBoxId(boxId);
        return success(list);
    }

    /**
     * 根据游戏ID查询关联的盒子列表（含详细信息）
     * 用于BoxRelationManager组件
     * @param siteId 可选，传入则按站点三源可见性过滤
     */
    @PreAuthorize("@ss.hasPermi('gamebox:boxgame:list')")
    @GetMapping("/game/{gameId}/boxes")
    public AjaxResult getGameBoxes(@PathVariable Long gameId,
                                   @RequestParam(required = false) Long siteId)
    {
        List<GbBoxGameRelation> list;
        if (siteId != null && siteId > 0) {
            list = gbBoxGameRelationService.selectBoxesByGameIdWithSite(gameId, siteId);
        } else {
            list = gbBoxGameRelationService.selectBoxesByGameId(gameId);
        }
        return success(list);
    }

    /**
     * 批量添加游戏到多个盒子
     * 请求数据格式: {gameId: 1, boxIds: [1,2,3]}
     */
    @PreAuthorize("@ss.hasPermi('gamebox:boxgame:add')")
    @Log(title = "批量添加游戏到盒子", businessType = BusinessType.INSERT)
    @PostMapping("/batch-add")
    public AjaxResult batchAddGameToBoxes(@RequestBody Map<String, Object> data)
    {
        try {
            Long gameId = Long.valueOf(data.get("gameId").toString());
            @SuppressWarnings("unchecked")
            List<Integer> boxIdInts = (List<Integer>) data.get("boxIds");
            List<Long> boxIds = boxIdInts.stream()
                .map(Long::valueOf)
                .collect(Collectors.toList());
            
            int count = 0;
            for (Long boxId : boxIds) {
                // 检查是否已存在关联
                List<GbBoxGameRelation> existing = gbBoxGameRelationService.selectRelationsByBoxIdAndGameIds(
                    boxId, java.util.Arrays.asList(gameId));
                if (existing.isEmpty()) {
                    GbBoxGameRelation relation = new GbBoxGameRelation();
                    relation.setBoxId(boxId);
                    relation.setGameId(gameId);
                    relation.setSortOrder(0);
                    relation.setIsFeatured("0");
                    relation.setIsNew("0");
                    int inserted = gbBoxGameRelationService.insertGbBoxGameRelation(relation);
                    if (inserted > 0) {
                        GbBoxGameRelation after = gbBoxGameRelationService.selectRelationByBoxIdAndGameId(boxId, gameId);
                        recordRelationChangeLog("INSERT", null, after);
                    }
                    count++;
                }
            }
            
            return success("成功添加 " + count + " 个盒子关联");
        } catch (Exception e) {
            logger.error("批量添加游戏到盒子失败", e);
            return error("批量添加失败: " + e.getMessage());
        }
    }

    /**
     * 移除游戏与盒子的关联
     * 请求数据格式: {gameId: 1, boxId: 1}
     */
    @PreAuthorize("@ss.hasPermi('gamebox:boxgame:remove')")
    @Log(title = "移除游戏盒子关联", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    public AjaxResult removeGameFromBox(@RequestBody Map<String, Object> data)
    {
        try {
            Long gameId = Long.valueOf(data.get("gameId").toString());
            Long boxId = Long.valueOf(data.get("boxId").toString());
            
            int count = gbBoxGameRelationService.deleteByBoxIdAndGameId(boxId, gameId);
            return toAjax(count);
        } catch (Exception e) {
            logger.error("移除游戏盒子关联失败", e);
            return error("移除失败: " + e.getMessage());
        }
    }

    /**
     * 更新游戏在盒子中的配置
     * 请求数据格式: {gameId: 1, boxId: 1, isFeatured: '1', isNew: '0', sortOrder: 10, customName: '自定义名称', ...}
     */
    @PreAuthorize("@ss.hasPermi('gamebox:boxgame:edit')")
    @Log(title = "更新游戏盒子配置", businessType = BusinessType.UPDATE)
    @PostMapping("/update-config")
    public AjaxResult updateGameBoxConfig(@RequestBody Map<String, Object> data)
    {
        try {
            Long gameId = Long.valueOf(data.get("gameId").toString());
            Long boxId = Long.valueOf(data.get("boxId").toString());
            
            // 查询现有关联
            List<GbBoxGameRelation> existing = gbBoxGameRelationService.selectRelationsByBoxIdAndGameIds(
                boxId, java.util.Arrays.asList(gameId));
            
            if (existing.isEmpty()) {
                return error("未找到该游戏盒子关联");
            }

            GbBoxGameRelation relation = existing.get(0);
            String beforeSnapshot = JSON.toJSONString(relation);
            
            // 更新字段
            if (data.containsKey("discountLabel")) {
                relation.setDiscountLabel(data.get("discountLabel") != null ? data.get("discountLabel").toString() : null);
            }
            if (data.containsKey("firstChargeDomestic")) {
                relation.setFirstChargeDomestic(data.get("firstChargeDomestic") != null ? 
                    new java.math.BigDecimal(data.get("firstChargeDomestic").toString()) : null);
            }
            if (data.containsKey("firstChargeOverseas")) {
                relation.setFirstChargeOverseas(data.get("firstChargeOverseas") != null ? 
                    new java.math.BigDecimal(data.get("firstChargeOverseas").toString()) : null);
            }
            if (data.containsKey("rechargeDomestic")) {
                relation.setRechargeDomestic(data.get("rechargeDomestic") != null ? 
                    new java.math.BigDecimal(data.get("rechargeDomestic").toString()) : null);
            }
            if (data.containsKey("rechargeOverseas")) {
                relation.setRechargeOverseas(data.get("rechargeOverseas") != null ? 
                    new java.math.BigDecimal(data.get("rechargeOverseas").toString()) : null);
            }
            if (data.containsKey("hasSupport")) {
                relation.setHasSupport(data.get("hasSupport").toString());
            }
            if (data.containsKey("supportDesc")) {
                relation.setSupportDesc(data.get("supportDesc") != null ? data.get("supportDesc").toString() : null);
            }
            if (data.containsKey("downloadUrl")) {
                relation.setDownloadUrl(data.get("downloadUrl") != null ? data.get("downloadUrl").toString() : null);
            }
            if (data.containsKey("promoteUrl")) {
                relation.setPromoteUrl(data.get("promoteUrl") != null ? data.get("promoteUrl").toString() : null);
            }
            if (data.containsKey("qrcodeUrl")) {
                relation.setQrcodeUrl(data.get("qrcodeUrl") != null ? data.get("qrcodeUrl").toString() : null);
            }
            if (data.containsKey("promoteText")) {
                relation.setPromoteText(data.get("promoteText") != null ? data.get("promoteText").toString() : null);
            }
            if (data.containsKey("posterUrl")) {
                relation.setPosterUrl(data.get("posterUrl") != null ? data.get("posterUrl").toString() : null);
            }
            if (data.containsKey("isFeatured")) {
                relation.setIsFeatured(data.get("isFeatured").toString());
            }
            if (data.containsKey("isExclusive")) {
                relation.setIsExclusive(data.get("isExclusive").toString());
            }
            if (data.containsKey("isNew")) {
                relation.setIsNew(data.get("isNew").toString());
            }
            if (data.containsKey("sortOrder")) {
                relation.setSortOrder(Integer.valueOf(data.get("sortOrder").toString()));
            }
            if (data.containsKey("customName")) {
                relation.setCustomName(data.get("customName") != null ? data.get("customName").toString() : null);
            }
            if (data.containsKey("customDescription")) {
                relation.setCustomDescription(data.get("customDescription") != null ? data.get("customDescription").toString() : null);
            }
            if (data.containsKey("customDownloadUrl")) {
                relation.setCustomDownloadUrl(data.get("customDownloadUrl") != null ? data.get("customDownloadUrl").toString() : null);
            }
            if (data.containsKey("promotionLinks")) {
                relation.setPromotionLinks(data.get("promotionLinks") != null ? data.get("promotionLinks").toString() : null);
            }
            if (data.containsKey("platformData")) {
                relation.setPlatformData(data.get("platformData") != null ? data.get("platformData").toString() : null);
            }
            
            int rows = gbBoxGameRelationService.updateGbBoxGameRelation(relation);
            if (rows > 0) {
                GbBoxGameRelation after = gbBoxGameRelationService.selectRelationByBoxIdAndGameId(boxId, gameId);
                recordRelationChangeLog("UPDATE", beforeSnapshot, after);
            }
            return toAjax(rows);
        } catch (Exception e) {
            logger.error("更新游戏盒子配置失败", e);
            return error("更新失败: " + e.getMessage());
        }
    }

    /**
     * 根据游戏ID查询关联的盒子列表
     */
    @PreAuthorize("@ss.hasPermi('gamebox:boxgame:list')")
    @GetMapping("/game/{gameId}")
    public AjaxResult getBoxesByGameId(@PathVariable Long gameId)
    {
        List<GbBoxGameRelation> list = gbBoxGameRelationService.selectBoxesByGameId(gameId);
        return success(list);
    }

    /**
     * 导出游戏盒子-游戏关联列表
     */
    @PreAuthorize("@ss.hasPermi('gamebox:boxgame:export')")
    @Log(title = "游戏盒子-游戏关联", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, GbBoxGameRelation gbBoxGameRelation)
    {
        List<GbBoxGameRelation> list = gbBoxGameRelationService.selectGbBoxGameRelationList(gbBoxGameRelation);
        ExcelUtil<GbBoxGameRelation> util = new ExcelUtil<GbBoxGameRelation>(GbBoxGameRelation.class);
        util.exportExcel(response, list, "游戏盒子-游戏关联数据");
    }

    /**
     * 获取游戏盒子-游戏关联详细信息
     */
    @PreAuthorize("@ss.hasPermi('gamebox:boxgame:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(gbBoxGameRelationService.selectGbBoxGameRelationById(id));
    }

    /**
     * 新增游戏盒子-游戏关联
     */
    @PreAuthorize("@ss.hasPermi('gamebox:boxgame:add')")
    @Log(title = "游戏盒子-游戏关联", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody GbBoxGameRelation gbBoxGameRelation)
    {
        int rows = gbBoxGameRelationService.insertGbBoxGameRelation(gbBoxGameRelation);
        if (rows > 0) {
            GbBoxGameRelation after = gbBoxGameRelationService.selectGbBoxGameRelationById(gbBoxGameRelation.getId());
            recordRelationChangeLog("INSERT", null, after);
        }
        return toAjax(rows);
    }

    /**
     * 修改游戏盒子-游戏关联
     */
    @PreAuthorize("@ss.hasPermi('gamebox:boxgame:edit')")
    @Log(title = "游戏盒子-游戏关联", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody GbBoxGameRelation gbBoxGameRelation)
    {
        GbBoxGameRelation before = gbBoxGameRelationService.selectGbBoxGameRelationById(gbBoxGameRelation.getId());
        int rows = gbBoxGameRelationService.updateGbBoxGameRelation(gbBoxGameRelation);
        if (rows > 0) {
            GbBoxGameRelation after = gbBoxGameRelationService.selectGbBoxGameRelationById(gbBoxGameRelation.getId());
            recordRelationChangeLog("UPDATE", before == null ? null : JSON.toJSONString(before), after);
        }
        return toAjax(rows);
    }

    private void recordRelationChangeLog(String changeType, String beforeSnapshot, GbBoxGameRelation after) {
        if (after == null || after.getId() == null || after.getGameId() == null) {
            return;
        }

        String afterSnapshot = JSON.toJSONString(after);
        if ("UPDATE".equals(changeType) && beforeSnapshot != null && beforeSnapshot.equals(afterSnapshot)) {
            return;
        }

        GbGame game = gbGameService.selectGbGameById(after.getGameId());
        GbGameChangeLog logEntry = new GbGameChangeLog();
        logEntry.setBatchNo("MANUAL-" + System.currentTimeMillis());
        logEntry.setGameId(after.getGameId());
        logEntry.setGameName(game == null ? after.getGameName() : game.getName());
        logEntry.setChangeType(changeType);
        logEntry.setTargetTable(TABLE_RELATION);
        logEntry.setTargetId(after.getId());
        logEntry.setBeforeSnapshot(beforeSnapshot);
        logEntry.setAfterSnapshot(afterSnapshot);
        logEntry.setCreateTime(new Date());
        logEntry.setCreateBy(getUsername());
        gameChangeLogMapper.insert(logEntry);
    }

    /**
     * 删除游戏盒子-游戏关联
     */
    @PreAuthorize("@ss.hasPermi('gamebox:boxgame:remove')")
    @Log(title = "游戏盒子-游戏关联", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(gbBoxGameRelationService.deleteGbBoxGameRelationByIds(ids));
    }

    /**
     * 批量添加游戏到盒子
     */
    @PreAuthorize("@ss.hasPermi('gamebox:boxgame:add')")
    @Log(title = "批量添加游戏到盒子", businessType = BusinessType.INSERT)
    @PostMapping("/batch/add/{boxId}")
    public AjaxResult batchAdd(@PathVariable Long boxId, @RequestBody Long[] gameIds)
    {
        return toAjax(gbBoxGameRelationService.batchAddGamesToBox(boxId, gameIds));
    }

    /**
     * 批量从盒子移除游戏
     */
    @PreAuthorize("@ss.hasPermi('gamebox:boxgame:remove')")
    @Log(title = "批量从盒子移除游戏", businessType = BusinessType.DELETE)
    @DeleteMapping("/batch/remove/{boxId}")
    public AjaxResult batchRemove(@PathVariable Long boxId, @RequestBody Long[] gameIds)
    {
        return toAjax(gbBoxGameRelationService.batchRemoveGamesFromBox(boxId, gameIds));
    }

    /**
     * 批量添加游戏到盒子（带关联数据）
     * 请求数据格式: [{gameId: 1, downloadUrl: '', promoteUrl: ''}]
     */
    @PreAuthorize("@ss.hasPermi('gamebox:boxgame:add')")
    @Log(title = "批量添加游戏到盒子（带关联数据）", businessType = BusinessType.INSERT)
    @PostMapping("/batch/add-with-relations/{boxId}")
    public AjaxResult batchAddWithRelations(@PathVariable Long boxId, @RequestBody List<Map<String, Object>> gameRelations)
    {
        try {
            // 获取所有游戏ID
            List<Long> gameIds = gameRelations.stream()
                .map(r -> Long.valueOf(r.get("gameId").toString()))
                .collect(Collectors.toList());
            
            // 批量查询已存在的关联
            List<GbBoxGameRelation> existingRelations = gbBoxGameRelationService.selectRelationsByBoxIdAndGameIds(boxId, gameIds);
            Map<Long, GbBoxGameRelation> existingMap = existingRelations.stream()
                .collect(Collectors.toMap(GbBoxGameRelation::getGameId, r -> r));
            
            // 分离需要更新和新增的记录
            List<GbBoxGameRelation> toUpdate = new ArrayList<>();
            List<GbBoxGameRelation> toInsert = new ArrayList<>();
            
            for (Map<String, Object> relation : gameRelations) {
                Long gameId = Long.valueOf(relation.get("gameId").toString());
                GbBoxGameRelation existing = existingMap.get(gameId);
                
                if (existing != null) {
                    // 已存在，准备更新
                    if (relation.containsKey("downloadUrl")) {
                        existing.setDownloadUrl(relation.get("downloadUrl").toString());
                    }
                    if (relation.containsKey("promoteUrl")) {
                        existing.setPromoteUrl(relation.get("promoteUrl").toString());
                    }
                    toUpdate.add(existing);
                } else {
                    // 不存在，准备插入
                    GbBoxGameRelation newRelation = new GbBoxGameRelation();
                    newRelation.setBoxId(boxId);
                    newRelation.setGameId(gameId);
                    newRelation.setSortOrder(0);
                    
                    if (relation.containsKey("downloadUrl")) {
                        newRelation.setDownloadUrl(relation.get("downloadUrl").toString());
                    }
                    if (relation.containsKey("promoteUrl")) {
                        newRelation.setPromoteUrl(relation.get("promoteUrl").toString());
                    }
                    
                    toInsert.add(newRelation);
                }
            }
            
            // 批量执行
            int totalCount = 0;
            if (!toInsert.isEmpty()) {
                totalCount += gbBoxGameRelationService.batchInsertRelations(toInsert);
            }
            if (!toUpdate.isEmpty()) {
                totalCount += gbBoxGameRelationService.batchUpdateRelations(toUpdate);
            }
            
            return success("成功处理 " + totalCount + " 个游戏关联");
        } catch (Exception e) {
            logger.error("批量添加游戏到盒子失败", e);
            return error("批量添加游戏失败: " + e.getMessage());
        }
    }
}
