package com.ruoyi.web.controller.gamebox;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import com.ruoyi.gamebox.domain.GbMasterArticle;
import com.ruoyi.gamebox.domain.dto.GbMasterArticlePublishDTO;
import com.ruoyi.gamebox.domain.dto.HomepageBindingDTO;
import com.ruoyi.gamebox.service.IGbMasterArticleService;
import com.ruoyi.gamebox.service.IGbMasterArticleHomepageBindingService;
import com.ruoyi.gamebox.service.IGbMasterArticleGamesService;
import com.ruoyi.gamebox.service.IGbMasterArticleGameBoxesService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 主文章内容Controller
 * 
 * @author ruoyi
 * @date 2026-01-12
 */
@RestController
@RequestMapping("/gamebox/masterArticle")
public class GbMasterArticleController extends BaseController
{
    @Autowired
    private IGbMasterArticleService gbMasterArticleService;

    @Autowired
    private IGbMasterArticleHomepageBindingService homepageBindingService;

    @Autowired
    private IGbMasterArticleGamesService masterArticleGamesService;

    @Autowired
    private IGbMasterArticleGameBoxesService masterArticleGameBoxesService;

    /**
     * 查询主文章内容列表
     */
    @PreAuthorize("@ss.hasPermi('gamebox:article:list')")
    @GetMapping("/list")
    public TableDataInfo list(GbMasterArticle gbMasterArticle)
    {
        startPage();
        List<GbMasterArticle> list = gbMasterArticleService.selectGbMasterArticleList(gbMasterArticle);
        return getDataTable(list);
    }

    /**
     * 导出主文章内容列表
     */
    @PreAuthorize("@ss.hasPermi('gamebox:article:export')")
    @Log(title = "主文章内容", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, GbMasterArticle gbMasterArticle)
    {
        List<GbMasterArticle> list = gbMasterArticleService.selectGbMasterArticleList(gbMasterArticle);
        ExcelUtil<GbMasterArticle> util = new ExcelUtil<GbMasterArticle>(GbMasterArticle.class);
        util.exportExcel(response, list, "主文章内容数据");
    }

    /**
     * 获取主文章内容详细信息
     */
    @PreAuthorize("@ss.hasPermi('gamebox:article:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(gbMasterArticleService.selectGbMasterArticleById(id));
    }

    /**
     * 获取主文章内容详细信息（包含关联数据）
     */
    @PreAuthorize("@ss.hasPermi('gamebox:article:query')")
    @GetMapping(value = "/details/{id}")
    public AjaxResult getDetailsInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(gbMasterArticleService.selectGbMasterArticleWithAssociations(id));
    }

    /**
     * 获取主文章编辑数据（包含默认语言版本内容）
     */
    @PreAuthorize("@ss.hasPermi('gamebox:article:query')")
    @GetMapping(value = "/editData/{id}")
    public AjaxResult getEditData(@PathVariable("id") Long id)
    {
        try {
            return gbMasterArticleService.getMasterArticleEditData(id);
        } catch (Exception e) {
            return AjaxResult.error("获取编辑数据失败：" + e.getMessage());
        }
    }

    /**
     * 新增主文章内容
     */
    @PreAuthorize("@ss.hasPermi('gamebox:article:add')")
    @Log(title = "主文章内容", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GbMasterArticle gbMasterArticle)
    {
        // 注释：content_key字段已从表中移除
        // if (gbMasterArticleService.checkContentKeyExists(gbMasterArticle.getContentKey(), null)) {
        //     return AjaxResult.error("内容标识已存在，请使用不同的标识");
        // }

        return toAjax(gbMasterArticleService.insertGbMasterArticle(gbMasterArticle));
    }

    /**
     * 修改主文章内容
     */
    @PreAuthorize("@ss.hasPermi('gamebox:article:edit')")
    @Log(title = "主文章内容", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GbMasterArticle gbMasterArticle)
    {
        // 注释：content_key字段已从表中移除
        // if (gbMasterArticleService.checkContentKeyExists(gbMasterArticle.getContentKey(), gbMasterArticle.getId())) {
        //     return AjaxResult.error("内容标识已存在，请使用不同的标识");
        // }

        return toAjax(gbMasterArticleService.updateGbMasterArticle(gbMasterArticle));
    }

    /**
     * 删除主文章内容
     */
    @PreAuthorize("@ss.hasPermi('gamebox:article:remove')")
    @Log(title = "主文章内容", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(gbMasterArticleService.deleteGbMasterArticleByIds(ids));
    }


    /**
     * 获取主文章统计信息
     */
    @PreAuthorize("@ss.hasPermi('gamebox:article:query')")
    @GetMapping("/statistics/{id}")
    public AjaxResult getStatistics(@PathVariable("id") Long id)
    {
        GbMasterArticle statistics = gbMasterArticleService.getMasterArticleStatistics(id);
        return AjaxResult.success(statistics);
    }

    /**
     * 创建多语言版本
     */
    @PreAuthorize("@ss.hasPermi('gamebox:article:add')")
    @Log(title = "创建多语言版本", businessType = BusinessType.INSERT)
    @PostMapping("/createLanguageVersions/{masterArticleId}")
    public AjaxResult createLanguageVersions(@PathVariable("masterArticleId") Long masterArticleId, 
                                           @RequestBody List<String> languageCodes)
    {
        int createdCount = gbMasterArticleService.createLanguageVersions(masterArticleId, languageCodes);
        return AjaxResult.success("成功创建 " + createdCount + " 个语言版本");
    }

    /**
     * 同步关联关系到语言版本
     */
    @PreAuthorize("@ss.hasPermi('gamebox:article:edit')")
    @Log(title = "同步关联关系", businessType = BusinessType.UPDATE)
    @PostMapping("/syncAssociations/{masterArticleId}")
    public AjaxResult syncAssociations(@PathVariable("masterArticleId") Long masterArticleId)
    {
        boolean result = gbMasterArticleService.syncAssociationsToLanguageVersions(masterArticleId);
        if (result) {
            return AjaxResult.success("关联关系同步成功");
        } else {
            return AjaxResult.error("关联关系同步失败");
        }
    }

    // 注释：主文章表已删除 status 字段，状态由各语言版本独立管理
    // 批量更新状态功能已废弃
    /**
     * 批量更新状态（已废弃）
     */
    // @PreAuthorize("@ss.hasPermi('gamebox:masterArticle:edit')")
    // @Log(title = "批量更新状态", businessType = BusinessType.UPDATE)
    // @PutMapping("/batchUpdateStatus")
    // public AjaxResult batchUpdateStatus(@RequestBody GbMasterArticle request)
    // {
    //     int updateCount = gbMasterArticleService.batchUpdateStatus(
    //         request.getIds(), 
    //         request.getStatus(), 
    //         getUsername()
    //     );
    //     return AjaxResult.success("成功更新 " + updateCount + " 条记录");
    // }

    // 注释：content_key字段已从表中移除
    /**
     * 检查内容标识唯一性
     */
    // @GetMapping("/checkContentKeyUnique")
    // public AjaxResult checkContentKeyUnique(String contentKey, Long id)
    // {
    //     boolean exists = gbMasterArticleService.checkContentKeyExists(contentKey, id);
    //     return AjaxResult.success(!exists);
    // }

    /**
     * 保存主文章为草稿
     */
    @PreAuthorize("@ss.hasPermi('gamebox:article:add')")
    @Log(title = "保存主文章草稿", businessType = BusinessType.INSERT)
    @PostMapping("/saveDraft")
    public AjaxResult saveDraft(@RequestBody GbMasterArticlePublishDTO publishDTO)
    {
        try {
            AjaxResult result = gbMasterArticleService.saveMasterArticleDraft(publishDTO);
            return result;
        } catch (Exception e) {
            return AjaxResult.error("保存草稿失败：" + e.getMessage());
        }
    }

    /**
     * 更新主文章草稿
     */
    @PreAuthorize("@ss.hasPermi('gamebox:article:edit')")
    @Log(title = "更新主文章草稿", businessType = BusinessType.UPDATE)
    @PostMapping("/updateDraft")
    public AjaxResult updateDraft(@RequestBody GbMasterArticlePublishDTO publishDTO)
    {
        try {
            AjaxResult result = gbMasterArticleService.updateMasterArticleDraft(publishDTO);
            return result;
        } catch (Exception e) {
            return AjaxResult.error("更新草稿失败：" + e.getMessage());
        }
    }



    /**
     * 查询主文章的主页绑定信息
     * @param masterArticleId 主文章ID
     */
    @PreAuthorize("@ss.hasPermi('gamebox:article:query')")
    @GetMapping("/homepageBinding/{masterArticleId}")
    public AjaxResult getHomepageBinding(@PathVariable("masterArticleId") Long masterArticleId)
    {
        try {
            if (masterArticleId == null) {
                return AjaxResult.error("主文章ID不能为空");
            }
            return AjaxResult.success(homepageBindingService.selectByMasterArticleId(masterArticleId));
        } catch (Exception e) {
            return AjaxResult.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 绑定主文章到游戏主页
     * @param dto 主页绑定DTO
     */
    @PreAuthorize("@ss.hasPermi('gamebox:article:edit')")
    @Log(title = "绑定游戏主页", businessType = BusinessType.UPDATE)
    @PostMapping("/bindToGame")
    public AjaxResult bindToGame(@RequestBody HomepageBindingDTO dto)
    {
        try {
            if (dto.getMasterArticleId() == null || dto.getGameId() == null) {
                return AjaxResult.error("主文章ID和游戏ID不能为空");
            }
            // 直接返回Service的结果，包含needConfirm标志
            return homepageBindingService.bindToGame(
                dto.getMasterArticleId(), 
                dto.getGameId(), 
                dto.getForce() != null && dto.getForce()
            );
        } catch (Exception e) {
            return AjaxResult.error("绑定失败：" + e.getMessage());
        }
    }

    /**
     * 绑定主文章到游戏盒子主页
     * @param dto 主页绑定DTO
     */
    @PreAuthorize("@ss.hasPermi('gamebox:article:edit')")
    @Log(title = "绑定游戏盒子主页", businessType = BusinessType.UPDATE)
    @PostMapping("/bindToGameBox")
    public AjaxResult bindToGameBox(@RequestBody HomepageBindingDTO dto)
    {
        try {
            if (dto.getMasterArticleId() == null || dto.getGameBoxId() == null) {
                return AjaxResult.error("主文章ID和游戏盒子ID不能为空");
            }
            // 直接返回Service的结果，包含needConfirm标志
            return homepageBindingService.bindToGameBox(
                dto.getMasterArticleId(), 
                dto.getGameBoxId(), 
                dto.getForce() != null && dto.getForce()
            );
        } catch (Exception e) {
            return AjaxResult.error("绑定失败：" + e.getMessage());
        }
    }

    /**
     * 解绑游戏主页
     * @param masterArticleId 主文章ID
     * @param gameId 游戏ID
     */
    @PreAuthorize("@ss.hasPermi('gamebox:article:edit')")
    @Log(title = "解绑游戏主页", businessType = BusinessType.UPDATE)
    @PostMapping("/unbindFromGame")
    public AjaxResult unbindFromGame(Long masterArticleId, Long gameId)
    {
        try {
            if (masterArticleId == null || gameId == null) {
                return AjaxResult.error("主文章ID和游戏ID不能为空");
            }
            homepageBindingService.unbindFromGame(masterArticleId, gameId);
            return AjaxResult.success("解绑成功");
        } catch (Exception e) {
            return AjaxResult.error("解绑失败：" + e.getMessage());
        }
    }

    /**
     * 解绑游戏盒子主页
     * @param masterArticleId 主文章ID
     * @param gameBoxId 游戏盒子ID
     */
    @PreAuthorize("@ss.hasPermi('gamebox:article:edit')")
    @Log(title = "解绑游戏盒子主页", businessType = BusinessType.UPDATE)
    @PostMapping("/unbindFromGameBox")
    public AjaxResult unbindFromGameBox(Long masterArticleId, Long gameBoxId)
    {
        try {
            if (masterArticleId == null || gameBoxId == null) {
                return AjaxResult.error("主文章ID和游戏盒子ID不能为空");
            }
            homepageBindingService.unbindFromGameBox(masterArticleId, gameBoxId);
            return AjaxResult.success("解绑成功");
        } catch (Exception e) {
            return AjaxResult.error("解绑失败：" + e.getMessage());
        }
    }

    // ==================== 游戏关联管理接口 ====================

    /**
     * 查询主文章关联的游戏列表
     */
    @PreAuthorize("@ss.hasPermi('gamebox:article:query')")
    @GetMapping("/games/{masterArticleId}")
    public AjaxResult getGames(@PathVariable("masterArticleId") Long masterArticleId)
    {
        try {
            return AjaxResult.success(masterArticleGamesService.selectByMasterArticleId(masterArticleId));
        } catch (Exception e) {
            return AjaxResult.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 保存主文章的游戏关联
     */
    @PreAuthorize("@ss.hasPermi('gamebox:article:edit')")
    @Log(title = "保存游戏关联", businessType = BusinessType.UPDATE)
    @PostMapping("/games/save")
    public AjaxResult saveGames(Long masterArticleId, @RequestBody List<Long> gameIds)
    {
        try {
            if (masterArticleId == null) {
                return AjaxResult.error("主文章ID不能为空");
            }
            masterArticleGamesService.saveGamesForMasterArticle(masterArticleId, gameIds, "manual", "related");
            return AjaxResult.success("保存成功");
        } catch (Exception e) {
            return AjaxResult.error("保存失败：" + e.getMessage());
        }
    }

    /**
     * 删除游戏关联
     */
    @PreAuthorize("@ss.hasPermi('gamebox:article:edit')")
    @Log(title = "删除游戏关联", businessType = BusinessType.DELETE)
    @DeleteMapping("/games/{masterArticleId}/{gameId}")
    public AjaxResult removeGame(@PathVariable("masterArticleId") Long masterArticleId, 
                                 @PathVariable("gameId") Long gameId)
    {
        try {
            masterArticleGamesService.deleteByMasterArticleIdAndGameId(masterArticleId, gameId);
            return AjaxResult.success("删除成功");
        } catch (Exception e) {
            return AjaxResult.error("删除失败：" + e.getMessage());
        }
    }

    // ==================== 游戏盒子关联管理接口 ====================

    /**
     * 查询主文章关联的游戏盒子列表
     */
    @PreAuthorize("@ss.hasPermi('gamebox:article:query')")
    @GetMapping("/gameboxes/{masterArticleId}")
    public AjaxResult getGameBoxes(@PathVariable("masterArticleId") Long masterArticleId)
    {
        try {
            return AjaxResult.success(masterArticleGameBoxesService.selectByMasterArticleId(masterArticleId));
        } catch (Exception e) {
            return AjaxResult.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 保存主文章的游戏盒子关联
     */
    @PreAuthorize("@ss.hasPermi('gamebox:article:edit')")
    @Log(title = "保存游戏盒子关联", businessType = BusinessType.UPDATE)
    @PostMapping("/gameboxes/save")
    public AjaxResult saveGameBoxes(Long masterArticleId, @RequestBody List<Long> gameBoxIds)
    {
        try {
            if (masterArticleId == null) {
                return AjaxResult.error("主文章ID不能为空");
            }
            masterArticleGameBoxesService.saveGameBoxesForMasterArticle(masterArticleId, gameBoxIds, "manual", "related");
            return AjaxResult.success("保存成功");
        } catch (Exception e) {
            return AjaxResult.error("保存失败：" + e.getMessage());
        }
    }

    /**
     * 删除游戏盒子关联
     */
    @PreAuthorize("@ss.hasPermi('gamebox:article:edit')")
    @Log(title = "删除游戏盒子关联", businessType = BusinessType.DELETE)
    @DeleteMapping("/gameboxes/{masterArticleId}/{gameBoxId}")
    public AjaxResult removeGameBox(@PathVariable("masterArticleId") Long masterArticleId, 
                                    @PathVariable("gameBoxId") Long gameBoxId)
    {
        try {
            masterArticleGameBoxesService.deleteByMasterArticleIdAndGameBoxId(masterArticleId, gameBoxId);
            return AjaxResult.success("删除成功");
        } catch (Exception e) {
            return AjaxResult.error("删除失败：" + e.getMessage());
        }
    }

    // ==================== 排除管理接口 ====================

    /**
     * 排除默认主文章
     */
    @PreAuthorize("@ss.hasPermi('gamebox:article:edit')")
    @Log(title = "排除默认主文章", businessType = BusinessType.UPDATE)
    @PostMapping("/exclude")
    public AjaxResult excludeDefaultMasterArticle(Long siteId, Long masterArticleId)
    {
        try {
            if (siteId == null || masterArticleId == null) {
                return AjaxResult.error("参数不能为空");
            }
            int result = gbMasterArticleService.excludeDefaultMasterArticle(siteId, masterArticleId);
            if (result > 0) {
                return AjaxResult.success("排除成功");
            } else {
                return AjaxResult.warn("主文章已被排除或不是默认配置");
            }
        } catch (Exception e) {
            return AjaxResult.error("排除失败：" + e.getMessage());
        }
    }

    /**
     * 恢复默认主文章（取消排除）
     */
    @PreAuthorize("@ss.hasPermi('gamebox:article:edit')")
    @Log(title = "恢复默认主文章", businessType = BusinessType.UPDATE)
    @PostMapping("/restore")
    public AjaxResult restoreDefaultMasterArticle(Long siteId, Long masterArticleId)
    {
        try {
            if (siteId == null || masterArticleId == null) {
                return AjaxResult.error("参数不能为空");
            }
            int result = gbMasterArticleService.restoreDefaultMasterArticle(siteId, masterArticleId);
            if (result > 0) {
                return AjaxResult.success("恢复成功");
            } else {
                return AjaxResult.warn("主文章未被排除");
            }
        } catch (Exception e) {
            return AjaxResult.error("恢复失败：" + e.getMessage());
        }
    }

    /**
     * 获取主文章被哪些网站排除
     */
    @PreAuthorize("@ss.hasPermi('gamebox:article:query')")
    @GetMapping("/excludedSites/{masterArticleId}")
    public AjaxResult getExcludedSites(@PathVariable("masterArticleId") Long masterArticleId)
    {
        try {
            List<Long> excludedSiteIds = gbMasterArticleService.getExcludedSitesByMasterArticle(masterArticleId);
            return AjaxResult.success(excludedSiteIds);
        } catch (Exception e) {
            return AjaxResult.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 批量管理主文章的排除网站
     */
    @PreAuthorize("@ss.hasPermi('gamebox:article:edit')")
    @Log(title = "批量管理排除网站", businessType = BusinessType.UPDATE)
    @PostMapping("/manageExclusions")
    public AjaxResult manageExclusions(Long masterArticleId, @RequestBody List<Long> excludedSiteIds)
    {
        try {
            if (masterArticleId == null) {
                return AjaxResult.error("主文章ID不能为空");
            }
            if (excludedSiteIds == null) {
                excludedSiteIds = new ArrayList<>();
            }
            int count = gbMasterArticleService.manageDefaultMasterArticleExclusions(masterArticleId, excludedSiteIds);
            return AjaxResult.success("管理成功，共操作" + count + "条记录");
        } catch (Exception e) {
            return AjaxResult.error("管理失败：" + e.getMessage());
        }
    }
}
