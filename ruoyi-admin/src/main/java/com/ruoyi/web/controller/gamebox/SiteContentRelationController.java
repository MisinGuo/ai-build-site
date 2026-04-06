package com.ruoyi.web.controller.gamebox;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Date;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.gamebox.service.ISiteContentRelationService;
import com.ruoyi.gamebox.mapper.GbSiteAiPlatformRelationMapper;
import com.ruoyi.gamebox.mapper.GbSiteTemplateRelationMapper;
import com.ruoyi.gamebox.mapper.GbSiteCategoryRelationMapper;
import com.ruoyi.gamebox.mapper.GbSiteBoxRelationMapper;
import com.ruoyi.gamebox.mapper.GbSiteGameRelationMapper;
import com.ruoyi.gamebox.mapper.GbSiteArticleRelationMapper;
import com.ruoyi.gamebox.mapper.GbSiteStorageConfigRelationMapper;
import com.ruoyi.gamebox.mapper.GbSiteAtomicToolRelationMapper;
import com.ruoyi.gamebox.mapper.GbSiteWorkflowRelationMapper;
import com.ruoyi.gamebox.domain.GbSiteCategoryRelation;
import com.ruoyi.gamebox.domain.GbSiteBoxRelation;
import com.ruoyi.gamebox.domain.GbSiteGameRelation;
import com.ruoyi.gamebox.domain.GbSiteArticleRelation;
import com.ruoyi.gamebox.domain.GbSiteStorageConfigRelation;
import com.ruoyi.gamebox.domain.GbSiteAiPlatformRelation;
import com.ruoyi.gamebox.domain.GbSiteAtomicToolRelation;
import com.ruoyi.gamebox.domain.GbSiteWorkflowRelation;
import com.ruoyi.gamebox.domain.GbSiteTemplateRelation;
import com.ruoyi.gamebox.domain.GbSiteTitleBatchRelation;
import com.ruoyi.gamebox.mapper.GbSiteTitleBatchRelationMapper;
import com.ruoyi.gamebox.mapper.GbTitlePoolBatchMapper;

/**
 * 网站内容关联管理Controller
 * 
 * @author ruoyi
 * @date 2025-12-27
 */
@RestController
@RequestMapping("/gamebox/siterelation")
public class SiteContentRelationController extends BaseController
{
    @Autowired
    private ISiteContentRelationService siteContentRelationService;
    
    @Autowired
    private GbSiteCategoryRelationMapper siteCategoryRelationMapper;
    
    @Autowired
    private GbSiteBoxRelationMapper siteBoxRelationMapper;
    
    @Autowired
    private GbSiteGameRelationMapper siteGameRelationMapper;
    
    @Autowired
    private GbSiteArticleRelationMapper siteArticleRelationMapper;
    
    @Autowired
    private GbSiteStorageConfigRelationMapper siteStorageConfigRelationMapper;
    
    @Autowired
    private GbSiteAiPlatformRelationMapper siteAiPlatformRelationMapper;
    
    @Autowired
    private GbSiteTemplateRelationMapper siteTemplateRelationMapper;
    
    @Autowired
    private GbSiteAtomicToolRelationMapper siteAtomicToolRelationMapper;
    
    @Autowired
    private GbSiteWorkflowRelationMapper siteWorkflowRelationMapper;

    @Autowired
    private GbSiteTitleBatchRelationMapper siteTitleBatchRelationMapper;

    @Autowired
    private GbTitlePoolBatchMapper titleBatchMapper;

    // ==================== 分类关联 ====================
    
    /**
     * 添加指定类型的分类关系（统一方法）
     */
    private int addCategoryToSiteWithType(Long siteId, Long categoryId, String relationType) {
        // 检查该类型的关系是否已存在
        if ("exclude".equals(relationType) && 
            siteCategoryRelationMapper.checkExcludeRelationExists(siteId, categoryId) > 0) {
            return 0;
        }
        
        GbSiteCategoryRelation relation = new GbSiteCategoryRelation();
        relation.setSiteId(siteId);
        relation.setCategoryId(categoryId);
        relation.setRelationType(relationType);
        relation.setIsVisible("exclude".equals(relationType) ? "0" : "1");
        relation.setIsEditable("exclude".equals(relationType) ? "0" : "1");
        relation.setCreateBy(SecurityUtils.getUsername());
        relation.setCreateTime(new Date());
        
        return siteCategoryRelationMapper.insertGbSiteCategoryRelation(relation);
    }
    
    /**
     * 查询分类在哪些网站可用
     */
    @PreAuthorize("@ss.hasPermi('gamebox:siterelation:list')")
    @GetMapping("/category/sites/{categoryId}")
    public AjaxResult getSitesByCategory(@PathVariable Long categoryId) {
        List<GbSiteCategoryRelation> list = siteCategoryRelationMapper.selectSitesByCategoryId(categoryId);
        return AjaxResult.success(list);
    }
    
    /**
     * 更新分类在网站的可见性
     */
    @PreAuthorize("@ss.hasPermi('gamebox:siterelation:edit')")
    @Log(title = "更新分类可见性", businessType = BusinessType.UPDATE)
    @PutMapping("/category/visibility")
    public AjaxResult updateCategoryVisibility(@RequestBody Map<String, Object> params) {
        Long siteId = Long.parseLong(params.get("siteId").toString());
        Long categoryId = Long.parseLong(params.get("categoryId").toString());
        String isVisible = params.get("isVisible").toString();
        
        // 更新关联表的is_visible字段
        GbSiteCategoryRelation relation = new GbSiteCategoryRelation();
        relation.setSiteId(siteId);
        relation.setCategoryId(categoryId);
        relation.setIsVisible(isVisible);
        
        int result = siteCategoryRelationMapper.updateVisibility(relation);
        return toAjax(result);
    }
    
    // ==================== 游戏盒子关联 ====================
    
    /**
     * 查询盒子在哪些网站可用
     */
    @PreAuthorize("@ss.hasPermi('gamebox:siterelation:list')")
    @GetMapping("/box/sites/{boxId}")
    public AjaxResult getSitesByBox(@PathVariable Long boxId) {
        List<GbSiteBoxRelation> list = siteBoxRelationMapper.selectSitesByBoxId(boxId);
        return AjaxResult.success(list);
    }
    
    /**
     * 更新游戏盒子在网站的可见性
     */
    @PreAuthorize("@ss.hasPermi('gamebox:siterelation:edit')")
    @Log(title = "更新游戏盒子可见性", businessType = BusinessType.UPDATE)
    @PutMapping("/box/visibility")
    public AjaxResult updateBoxVisibility(@RequestBody Map<String, Object> params) {
        Long siteId = Long.parseLong(params.get("siteId").toString());
        Long boxId = Long.parseLong(params.get("boxId").toString());
        String isVisible = params.get("isVisible").toString();
        
        return toAjax(siteBoxRelationMapper.updateVisibilityBySiteIdAndBoxId(siteId, boxId, isVisible));
    }

    /**
     * 更新游戏盒子在网站的完整配置（可见性、推荐、自定义名、排序等）
     */
    @PreAuthorize("@ss.hasPermi('gamebox:siterelation:edit')")
    @Log(title = "更新游戏盒子网站配置", businessType = BusinessType.UPDATE)
    @PutMapping("/box/updateConfig")
    public AjaxResult updateBoxSiteConfig(@RequestBody Map<String, Object> params) {
        Long siteId = Long.parseLong(params.get("siteId").toString());
        Long boxId = Long.parseLong(params.get("boxId").toString());
        GbSiteBoxRelation relation = new GbSiteBoxRelation();
        if (params.get("isVisible") != null) relation.setIsVisible(params.get("isVisible").toString());
        if (params.get("isFeatured") != null) relation.setIsFeatured(params.get("isFeatured").toString());
        if (params.containsKey("customName")) relation.setCustomName(params.get("customName") != null ? params.get("customName").toString() : "");
        if (params.containsKey("customDescription")) relation.setCustomDescription(params.get("customDescription") != null ? params.get("customDescription").toString() : "");
        if (params.get("sortOrder") != null) relation.setSortOrder(Integer.parseInt(params.get("sortOrder").toString()));
        return toAjax(siteBoxRelationMapper.updateConfigBySiteIdAndBoxId(siteId, boxId, relation));
    }
    
    /**
     * 复制游戏盒子到目标网站（含游戏和分类，无等价新接口）
     */
    @PreAuthorize("@ss.hasPermi('gamebox:siterelation:edit')")
    @Log(title = "复制游戏盒子到目标网站", businessType = BusinessType.INSERT)
    @PostMapping("/box/copy")
    public AjaxResult copyBoxToSite(@RequestBody Map<String, Object> params) {
        Long boxId = Long.parseLong(params.get("boxId").toString());
        Long targetSiteId = Long.parseLong(params.get("targetSiteId").toString());
        int result = siteContentRelationService.copyBoxToSite(boxId, targetSiteId);
        return toAjax(result);
    }
    
    private int addBoxToSiteWithType(Long siteId, Long boxId, String relationType, boolean includeGames) {
        if ("exclude".equals(relationType)) {
            if (siteBoxRelationMapper.checkExcludeRelationExists(siteId, boxId) > 0) {
                return 1;
            }
            GbSiteBoxRelation relation = new GbSiteBoxRelation();
            relation.setSiteId(siteId);
            relation.setBoxId(boxId);
            relation.setRelationType("exclude");
            relation.setIsVisible("0");
            relation.setCreateBy(SecurityUtils.getUsername());
            relation.setCreateTime(new Date());
            return siteBoxRelationMapper.insertGbSiteBoxRelation(relation);
        } else {
            return siteContentRelationService.addBoxToSite(siteId, boxId, includeGames);
        }
    }
    
    // ==================== 游戏关联 ====================
    
    private int addGameToSiteWithType(Long siteId, Long gameId, String relationType) {
        if ("exclude".equals(relationType) && 
            siteGameRelationMapper.checkExcludeRelationExists(siteId, gameId) > 0) {
            return 1; // 排除关系已存在，视为成功
        }
        
        GbSiteGameRelation relation = new GbSiteGameRelation();
        relation.setSiteId(siteId);
        relation.setGameId(gameId);
        relation.setRelationType(relationType);
        relation.setIsVisible("exclude".equals(relationType) ? "0" : "1");
        relation.setCreateBy(SecurityUtils.getUsername());
        relation.setCreateTime(new Date());
        
        return siteGameRelationMapper.insertGbSiteGameRelation(relation);
    }
    
    /**
     * 查询游戏在哪些网站可用
     */
    @PreAuthorize("@ss.hasPermi('gamebox:siterelation:list')")
    @GetMapping("/game/sites/{gameId}")
    public AjaxResult getSitesByGame(@PathVariable Long gameId) {
        List<GbSiteGameRelation> list = siteGameRelationMapper.selectSitesByGameId(gameId);
        return AjaxResult.success(list);
    }
    
    /**
     * 更新游戏在网站的可见性
     */
    @PreAuthorize("@ss.hasPermi('gamebox:siterelation:edit')")
    @Log(title = "更新游戏可见性", businessType = BusinessType.UPDATE)
    @PutMapping("/game/visibility")
    public AjaxResult updateGameVisibility(@RequestBody Map<String, Object> params) {
        Long siteId = Long.parseLong(params.get("siteId").toString());
        Long gameId = Long.parseLong(params.get("gameId").toString());
        String isVisible = params.get("isVisible").toString();
        
        return toAjax(siteGameRelationMapper.updateVisibilityBySiteIdAndGameId(siteId, gameId, isVisible));
    }

    /**
     * 更新游戏在网站的完整配置（可见性、推荐、新游、自定义名、排序等）
     */
    @PreAuthorize("@ss.hasPermi('gamebox:siterelation:edit')")
    @Log(title = "更新游戏网站配置", businessType = BusinessType.UPDATE)
    @PutMapping("/game/updateConfig")
    public AjaxResult updateGameSiteConfig(@RequestBody Map<String, Object> params) {
        Long siteId = Long.parseLong(params.get("siteId").toString());
        Long gameId = Long.parseLong(params.get("gameId").toString());
        GbSiteGameRelation relation = new GbSiteGameRelation();
        if (params.get("isVisible") != null) relation.setIsVisible(params.get("isVisible").toString());
        if (params.get("isFeatured") != null) relation.setIsFeatured(params.get("isFeatured").toString());
        if (params.get("isNew") != null) relation.setIsNew(params.get("isNew").toString());
        if (params.containsKey("customName")) relation.setCustomName(params.get("customName") != null ? params.get("customName").toString() : "");
        if (params.containsKey("customDescription")) relation.setCustomDescription(params.get("customDescription") != null ? params.get("customDescription").toString() : "");
        if (params.containsKey("customDownloadUrl")) relation.setCustomDownloadUrl(params.get("customDownloadUrl") != null ? params.get("customDownloadUrl").toString() : "");
        if (params.get("sortOrder") != null) relation.setSortOrder(Integer.parseInt(params.get("sortOrder").toString()));
        return toAjax(siteGameRelationMapper.updateConfigBySiteIdAndGameId(siteId, gameId, relation));
    }
    
    // ==================== 文章关联 ====================
    
    /**
     * 查询文章在哪些网站可用
     */
    @PreAuthorize("@ss.hasPermi('gamebox:siterelation:list')")
    @GetMapping("/article/sites/{articleId}")
    public AjaxResult getSitesByArticle(@PathVariable Long articleId) {
        List<GbSiteArticleRelation> list = siteArticleRelationMapper.selectSitesByArticleId(articleId);
        return AjaxResult.success(list);
    }
    
    /**
     * 更新文章在网站的配置
     */
    @PreAuthorize("@ss.hasPermi('gamebox:siterelation:edit')")
    @Log(title = "更新文章配置", businessType = BusinessType.UPDATE)
    @PutMapping("/article/updateConfig")
    public AjaxResult updateArticleSiteConfig(@RequestBody GbSiteArticleRelation relation) {
        return toAjax(siteArticleRelationMapper.updateGbSiteArticleRelation(relation));
    }

    /**
     * 更新文章在网站的可见性（仅更新 is_visible 字段）
     */
    @PreAuthorize("@ss.hasPermi('gamebox:siterelation:edit')")
    @Log(title = "更新文章可见性", businessType = BusinessType.UPDATE)
    @PutMapping("/article/visibility")
    public AjaxResult updateArticleVisibility(@RequestBody Map<String, Object> params) {
        Long siteId = Long.parseLong(params.get("siteId").toString());
        Long articleId = Long.parseLong(params.get("articleId").toString());
        String isVisible = params.get("isVisible").toString();
        return toAjax(siteArticleRelationMapper.updateArticleVisibility(siteId, articleId, isVisible));
    }
    
    private int addArticleToSiteWithType(Long siteId, Long articleId, String relationType) {
        if ("exclude".equals(relationType) &&
            siteArticleRelationMapper.checkExcludeRelationExists(siteId, articleId) > 0) {
            return 0;
        }
        GbSiteArticleRelation relation = new GbSiteArticleRelation();
        relation.setSiteId(siteId);
        relation.setArticleId(articleId);
        relation.setRelationType(relationType);
        relation.setIsVisible("exclude".equals(relationType) ? "0" : "1");
        relation.setCreateBy(SecurityUtils.getUsername());
        relation.setCreateTime(new Date());
        return siteArticleRelationMapper.insertGbSiteArticleRelation(relation);
    }
    
    // ==================== 存储配置关联 ====================
    
    /**
     * 添加指定类型的存储配置关系（统一方法）
     */
    private int addStorageConfigToSiteWithType(Long siteId, Long storageConfigId, String relationType) {
        // 检查该类型的关系是否已存在
        if ("exclude".equals(relationType) && 
            siteStorageConfigRelationMapper.checkExcludeRelationExists(siteId, storageConfigId) > 0) {
            return 0;
        }
        
        GbSiteStorageConfigRelation relation = new GbSiteStorageConfigRelation();
        relation.setSiteId(siteId);
        relation.setStorageConfigId(storageConfigId);
        relation.setRelationType(relationType);
        relation.setIsVisible("exclude".equals(relationType) ? "0" : "1");
        relation.setIsEditable("exclude".equals(relationType) ? "0" : "1");
        relation.setSortOrder(0);
        relation.setCreateBy(SecurityUtils.getUsername());
        relation.setCreateTime(DateUtils.getNowDate());
        
        return siteStorageConfigRelationMapper.insertGbSiteStorageConfigRelation(relation);
    }
    
    private int addStorageConfigToSiteInternal(Long siteId, Long storageConfigId) {
        // 检查 include 类型的关联是否已存在
        if (siteStorageConfigRelationMapper.checkIncludeRelationExists(siteId, storageConfigId) > 0) {
            return 0;
        }
        
        GbSiteStorageConfigRelation relation = new GbSiteStorageConfigRelation();
        relation.setSiteId(siteId);
        relation.setStorageConfigId(storageConfigId);
        relation.setRelationType("include");
        relation.setIsVisible("1");
        relation.setIsEditable("1");
        relation.setSortOrder(0);
        return siteStorageConfigRelationMapper.insertGbSiteStorageConfigRelation(relation);
    }
    
    /**
     * 批量保存存储配置的网站关联关系（批量SQL优化版）
     * Body: { storageConfigIds: [], includeSiteIds?: [], excludeSiteIds?: [] }
     */
    @PreAuthorize("@ss.hasPermi('gamebox:siterelation:edit')")
    @Log(title = "批量保存存储配置网站关联", businessType = BusinessType.UPDATE)
    @PutMapping("/storageConfig/batchSaveRelations")
    public AjaxResult batchSaveStorageConfigSiteRelations(@RequestBody Map<String, Object> params) {
        List<Long> ids = parseLongList(params, "storageConfigIds");
        if (ids.isEmpty()) return success();
        boolean hi = params.containsKey("includeSiteIds"), he = params.containsKey("excludeSiteIds");
        List<Long> inc = hi ? parseLongList(params, "includeSiteIds") : new ArrayList<>();
        List<Long> exc = he ? parseLongList(params, "excludeSiteIds") : new ArrayList<>();
        String user = SecurityUtils.getUsername(); Date now = DateUtils.getNowDate();
        if (hi) {
            siteStorageConfigRelationMapper.deleteByStorageConfigIdsAndTypeExcluding(ids, "include", inc);
            if (!inc.isEmpty()) {
                List<GbSiteStorageConfigRelation> rows = new ArrayList<>();
                for (Long id : ids) for (Long s : inc) { GbSiteStorageConfigRelation r = new GbSiteStorageConfigRelation(); r.setSiteId(s); r.setStorageConfigId(id); r.setRelationType("include"); r.setIsVisible("1"); r.setIsEditable("1"); r.setSortOrder(0); r.setCreateBy(user); r.setCreateTime(now); rows.add(r); }
                siteStorageConfigRelationMapper.batchInsertGbSiteStorageConfigRelation(rows);
            }
        }
        if (he) {
            siteStorageConfigRelationMapper.deleteByStorageConfigIdsAndTypeExcluding(ids, "exclude", exc);
            if (!exc.isEmpty()) {
                List<GbSiteStorageConfigRelation> rows = new ArrayList<>();
                for (Long id : ids) for (Long s : exc) { GbSiteStorageConfigRelation r = new GbSiteStorageConfigRelation(); r.setSiteId(s); r.setStorageConfigId(id); r.setRelationType("exclude"); r.setIsVisible("0"); r.setIsEditable("0"); r.setSortOrder(0); r.setCreateBy(user); r.setCreateTime(now); rows.add(r); }
                siteStorageConfigRelationMapper.batchInsertGbSiteStorageConfigRelation(rows);
            }
        }
        return success();
    }

    /**
     * 更新存储配置在网站的可见性
     */
    @PreAuthorize("@ss.hasPermi('gamebox:siterelation:edit')")
    @Log(title = "更新存储配置可见性", businessType = BusinessType.UPDATE)
    @PutMapping("/storageConfig/visibility")
    public AjaxResult updateStorageConfigVisibility(@RequestBody Map<String, Object> params) {
        Long siteId = Long.parseLong(params.get("siteId").toString());
        Long storageConfigId = Long.parseLong(params.get("storageConfigId").toString());
        String isVisible = params.get("isVisible").toString();
        
        int result = siteContentRelationService.updateStorageConfigVisibility(siteId, storageConfigId, isVisible);
        return toAjax(result);
    }
    
    /**
     * 查询存储配置在哪些网站可用
     */
    @PreAuthorize("@ss.hasPermi('gamebox:siterelation:list')")
    @GetMapping("/storageConfig/sites/{storageConfigId}")
    public AjaxResult getSitesByStorageConfig(@PathVariable Long storageConfigId) {
        List<GbSiteStorageConfigRelation> list = siteStorageConfigRelationMapper.selectSitesByStorageConfigId(storageConfigId);
        return AjaxResult.success(list);
    }
    
    // ==================== AI平台配置关联 ====================
    
    /**
     * 查询AI平台配置在哪些网站可用
     */
    @PreAuthorize("@ss.hasPermi('gamebox:siterelation:list')")
    @GetMapping("/aiPlatform/sites/{aiPlatformId}")
    public AjaxResult getSitesByAiPlatform(@PathVariable Long aiPlatformId) {
        List<GbSiteAiPlatformRelation> list = siteAiPlatformRelationMapper.selectSitesByAiPlatformId(aiPlatformId);
        return AjaxResult.success(list);
    }
    
    /**
     * 添加指定类型的AI平台关系（统一方法）
     */
    private int addAiPlatformToSiteWithType(Long siteId, Long aiPlatformId, String relationType) {
        // 检查该类型的关系是否已存在
        if ("exclude".equals(relationType) && 
            siteAiPlatformRelationMapper.checkExcludeRelationExists(siteId, aiPlatformId) > 0) {
            return 0;
        }
        
        GbSiteAiPlatformRelation relation = new GbSiteAiPlatformRelation();
        relation.setSiteId(siteId);
        relation.setAiPlatformId(aiPlatformId);
        relation.setRelationType(relationType);
        relation.setIsVisible("exclude".equals(relationType) ? "0" : "1");
        relation.setIsEditable("exclude".equals(relationType) ? "0" : "1");
        relation.setSortOrder(0);
        relation.setCreateBy(SecurityUtils.getUsername());
        relation.setCreateTime(DateUtils.getNowDate());
        
        return siteAiPlatformRelationMapper.insertGbSiteAiPlatformRelation(relation);
    }
    
    private int addAiPlatformToSiteInternal(Long siteId, Long aiPlatformId) {
        // 检查 include 类型的关联是否已存在
        if (siteAiPlatformRelationMapper.checkIncludeRelationExists(siteId, aiPlatformId) > 0) {
            return 0;
        }
        
        GbSiteAiPlatformRelation relation = new GbSiteAiPlatformRelation();
        relation.setSiteId(siteId);
        relation.setAiPlatformId(aiPlatformId);
        relation.setRelationType("include");
        relation.setIsVisible("1");
        relation.setIsEditable("1");
        relation.setSortOrder(0);
        return siteAiPlatformRelationMapper.insertGbSiteAiPlatformRelation(relation);
    }
    
    /**
     * 更新AI平台在网站的可见性
     */
    @PreAuthorize("@ss.hasPermi('gamebox:siterelation:edit')")
    @Log(title = "更新AI平台可见性", businessType = BusinessType.UPDATE)
    @PutMapping("/aiPlatform/visibility")
    public AjaxResult updateAiPlatformVisibility(@RequestBody Map<String, Object> params) {
        Long siteId = Long.parseLong(params.get("siteId").toString());
        Long platformId = Long.parseLong(params.get("platformId").toString());
        String isVisible = params.get("isVisible").toString();
        
        int result = siteContentRelationService.updateAiPlatformVisibility(siteId, platformId, isVisible);
        return toAjax(result);
    }
    
    // ==================== 标题池批次关联 ====================

    /**
     * 批量保存AI平台的网站关联关系（批量SQL优化版）
     * Body: { aiPlatformIds: [], includeSiteIds?: [], excludeSiteIds?: [] }
     */
    @PreAuthorize("@ss.hasPermi('gamebox:siterelation:edit')")
    @Log(title = "批量保存AI平台网站关联", businessType = BusinessType.UPDATE)
    @PutMapping("/aiPlatform/batchSaveRelations")
    public AjaxResult batchSaveAiPlatformSiteRelations(@RequestBody Map<String, Object> params) {
        List<Long> ids = parseLongList(params, "aiPlatformIds");
        if (ids.isEmpty()) return success();
        boolean hi = params.containsKey("includeSiteIds"), he = params.containsKey("excludeSiteIds");
        List<Long> inc = hi ? parseLongList(params, "includeSiteIds") : new ArrayList<>();
        List<Long> exc = he ? parseLongList(params, "excludeSiteIds") : new ArrayList<>();
        String user = SecurityUtils.getUsername(); Date now = DateUtils.getNowDate();
        if (hi) {
            siteAiPlatformRelationMapper.deleteByAiPlatformIdsAndTypeExcluding(ids, "include", inc);
            if (!inc.isEmpty()) {
                List<GbSiteAiPlatformRelation> rows = new ArrayList<>();
                for (Long id : ids) for (Long s : inc) { GbSiteAiPlatformRelation r = new GbSiteAiPlatformRelation(); r.setSiteId(s); r.setAiPlatformId(id); r.setRelationType("include"); r.setIsVisible("1"); r.setIsEditable("1"); r.setSortOrder(0); r.setCreateBy(user); r.setCreateTime(now); rows.add(r); }
                siteAiPlatformRelationMapper.batchInsertGbSiteAiPlatformRelation(rows);
            }
        }
        if (he) {
            siteAiPlatformRelationMapper.deleteByAiPlatformIdsAndTypeExcluding(ids, "exclude", exc);
            if (!exc.isEmpty()) {
                List<GbSiteAiPlatformRelation> rows = new ArrayList<>();
                for (Long id : ids) for (Long s : exc) { GbSiteAiPlatformRelation r = new GbSiteAiPlatformRelation(); r.setSiteId(s); r.setAiPlatformId(id); r.setRelationType("exclude"); r.setIsVisible("0"); r.setIsEditable("0"); r.setSortOrder(0); r.setCreateBy(user); r.setCreateTime(now); rows.add(r); }
                siteAiPlatformRelationMapper.batchInsertGbSiteAiPlatformRelation(rows);
            }
        }
        return success();
    }

    // ==================== 标题池批次关联 ====================
    
    /**
     * 更新标题池批次在网站的可见性
     */
    @PreAuthorize("@ss.hasPermi('gamebox:siterelation:edit')")
    @Log(title = "更新标题池批次可见性", businessType = BusinessType.UPDATE)
    @PutMapping("/batch/visibility")
    public AjaxResult updateBatchVisibility(@RequestBody Map<String, Object> params) {
        Long siteId = Long.parseLong(params.get("siteId").toString());
        Long batchId = Long.parseLong(params.get("batchId").toString());
        String isVisible = params.get("isVisible").toString();
        int result = siteContentRelationService.updateBatchVisibility(siteId, batchId, isVisible);
        return toAjax(result);
    }
    
    // ==================== 原子工具关联 ====================
    
    /**
     * 添加指定类型的原子工具关系（统一方法）
     */
    private int addAtomicToolToSiteWithType(Long siteId, Long atomicToolId, String relationType) {
        // 检查该类型的关系是否已存在
        if ("exclude".equals(relationType) && 
            siteAtomicToolRelationMapper.checkExcludeRelationExists(siteId, atomicToolId) > 0) {
            return 0;
        }
        
        GbSiteAtomicToolRelation relation = new GbSiteAtomicToolRelation();
        relation.setSiteId(siteId);
        relation.setAtomicToolId(atomicToolId);
        relation.setRelationType(relationType);
        relation.setIsVisible("exclude".equals(relationType) ? "0" : "1");
        relation.setSortOrder(0);
        relation.setCreateBy(SecurityUtils.getUsername());
        relation.setCreateTime(DateUtils.getNowDate());
        
        return siteAtomicToolRelationMapper.insertSiteAtomicToolRelation(relation);
    }
    
    private int addAtomicToolToSiteInternal(Long siteId, Long atomicToolId) {
        // 检查 include 类型的关联是否已存在
        if (siteAtomicToolRelationMapper.checkRelationExists(siteId, atomicToolId) > 0) {
            return 0;
        }
        
        GbSiteAtomicToolRelation relation = new GbSiteAtomicToolRelation();
        relation.setSiteId(siteId);
        relation.setAtomicToolId(atomicToolId);
        relation.setRelationType("include");
        relation.setIsVisible("1");
        relation.setSortOrder(0);
        return siteAtomicToolRelationMapper.insertSiteAtomicToolRelation(relation);
    }
    
    /**
     * 查询原子工具在哪些网站可用
     */
    @PreAuthorize("@ss.hasPermi('gamebox:siterelation:list')")
    @GetMapping("/atomicTool/sites/{atomicToolId}")
    public AjaxResult getSitesByAtomicTool(@PathVariable Long atomicToolId) {
        List<GbSiteAtomicToolRelation> list = siteAtomicToolRelationMapper.selectSitesByAtomicToolId(atomicToolId);
        return AjaxResult.success(list);
    }
    
    /**
     * 更新原子工具在网站的可见性
     */
    @PreAuthorize("@ss.hasPermi('gamebox:siterelation:edit')")
    @Log(title = "更新原子工具可见性", businessType = BusinessType.UPDATE)
    @PutMapping("/atomicTool/visibility")
    public AjaxResult updateAtomicToolVisibility(@RequestBody Map<String, Object> params) {
        Long siteId = Long.parseLong(params.get("siteId").toString());
        Long atomicToolId = Long.parseLong(params.get("atomicToolId").toString());
        String isVisible = params.get("isVisible").toString();
        
        int result = siteContentRelationService.updateAtomicToolVisibility(siteId, atomicToolId, isVisible);
        return toAjax(result);
    }
    
    // ==================== 工作流关联 ====================
    
    private int addWorkflowToSiteWithType(Long siteId, Long workflowId, String relationType) {
        // 检查该类型的关系是否已存在
        if ("exclude".equals(relationType)) {
            if (siteWorkflowRelationMapper.checkExcludeRelationExists(siteId, workflowId) > 0) {
                return 0; // 排除关系已存在,跳过
            }
        } else {
            // include 类型也需要检查
            if (siteWorkflowRelationMapper.checkRelationExists(siteId, workflowId) > 0) {
                return 0; // 关联关系已存在,跳过
            }
        }
        
        GbSiteWorkflowRelation relation = new GbSiteWorkflowRelation();
        relation.setSiteId(siteId);
        relation.setWorkflowId(workflowId);
        relation.setRelationType(relationType);
        relation.setIsVisible("exclude".equals(relationType) ? "0" : "1");
        relation.setCreateBy(SecurityUtils.getUsername());
        relation.setCreateTime(DateUtils.getNowDate());
        
        return siteWorkflowRelationMapper.insertSiteWorkflowRelation(relation);
    }
    
    private int addWorkflowToSiteInternal(Long siteId, Long workflowId) {
        // 检查 include 类型的关联是否已存在
        if (siteWorkflowRelationMapper.checkRelationExists(siteId, workflowId) > 0) {
            return 0;
        }
        
        GbSiteWorkflowRelation relation = new GbSiteWorkflowRelation();
        relation.setSiteId(siteId);
        relation.setWorkflowId(workflowId);
        relation.setRelationType("include");
        relation.setIsVisible("1");
        return siteWorkflowRelationMapper.insertSiteWorkflowRelation(relation);
    }
    
    /**
     * 查询工作流在哪些网站可用
     */
    @PreAuthorize("@ss.hasPermi('gamebox:siterelation:list')")
    @GetMapping("/workflow/sites/{workflowId}")
    public AjaxResult getSitesByWorkflow(@PathVariable Long workflowId) {
        List<GbSiteWorkflowRelation> list = siteWorkflowRelationMapper.selectSitesByWorkflowId(workflowId);
        return AjaxResult.success(list);
    }

    /**
     * 更新工作流在网站的可见性
     */
    @PreAuthorize("@ss.hasPermi('gamebox:siterelation:edit')")
    @Log(title = "更新工作流可见性", businessType = BusinessType.UPDATE)
    @PutMapping("/workflow/visibility")
    public AjaxResult updateWorkflowVisibility(@RequestBody Map<String, Object> params) {
        Long siteId = Long.parseLong(params.get("siteId").toString());
        Long workflowId = Long.parseLong(params.get("workflowId").toString());
        String isVisible = params.get("isVisible").toString();
        GbSiteWorkflowRelation relation = new GbSiteWorkflowRelation();
        relation.setSiteId(siteId);
        relation.setWorkflowId(workflowId);
        relation.setIsVisible(isVisible);
        return toAjax(siteWorkflowRelationMapper.updateSiteWorkflowRelation(relation));
    }

    // ==================== 统一保存关联关系接口（同时处理 include 和 exclude） ====================

    /**
     * 辅助方法：将 Integer 列表解析为 Long 列表
     */
    @SuppressWarnings("unchecked")
    private List<Long> parseLongList(Map<String, Object> params, String key) {
        if (!params.containsKey(key) || params.get(key) == null) return new ArrayList<>();
        List<Integer> ints = (List<Integer>) params.get(key);
        List<Long> longs = new ArrayList<>();
        for (Integer i : ints) {
            longs.add(i.longValue());
        }
        return longs;
    }

    /**
     * 批量保存分类的网站关联关系（批量SQL优化版：N×K 查询 → 最多 4 条 SQL）
     * Body: { categoryIds: [], includeSiteIds?: [], excludeSiteIds?: [] }
     * includeSiteIds/excludeSiteIds 任意一个可省略，省略则不更新对应侧
     */
    @PreAuthorize("@ss.hasPermi('gamebox:siterelation:edit')")
    @Log(title = "批量保存分类网站关联", businessType = BusinessType.UPDATE)
    @PutMapping("/category/batchSaveRelations")
    public AjaxResult batchSaveCategorySiteRelations(@RequestBody Map<String, Object> params) {
        List<Long> categoryIds = parseLongList(params, "categoryIds");
        if (categoryIds.isEmpty()) return success();
        boolean hasIncludes = params.containsKey("includeSiteIds");
        boolean hasExcludes = params.containsKey("excludeSiteIds");
        List<Long> includeSiteIds = hasIncludes ? parseLongList(params, "includeSiteIds") : new ArrayList<>();
        List<Long> excludeSiteIds = hasExcludes ? parseLongList(params, "excludeSiteIds") : new ArrayList<>();
        String username = SecurityUtils.getUsername();
        Date now = DateUtils.getNowDate();

        if (hasIncludes) {
            // 删除不在新 include 列表中的关联记录（1 条 SQL）
            siteCategoryRelationMapper.deleteByCategoryIdsAndTypeExcluding(categoryIds, "include", includeSiteIds);
            // 批量插入新 include（ON DUPLICATE KEY UPDATE 保障幂等，1 条 SQL）
            if (!includeSiteIds.isEmpty()) {
                List<GbSiteCategoryRelation> toInsert = new ArrayList<>(categoryIds.size() * includeSiteIds.size());
                for (Long catId : categoryIds) {
                    for (Long siteId : includeSiteIds) {
                        GbSiteCategoryRelation rel = new GbSiteCategoryRelation();
                        rel.setSiteId(siteId);
                        rel.setCategoryId(catId);
                        rel.setRelationType("include");
                        rel.setIsVisible("1");
                        rel.setCreateBy(username);
                        rel.setCreateTime(now);
                        toInsert.add(rel);
                    }
                }
                siteCategoryRelationMapper.batchInsertGbSiteCategoryRelation(toInsert);
            }
        }
        if (hasExcludes) {
            // 删除不在新 exclude 列表中的关联记录（1 条 SQL）
            siteCategoryRelationMapper.deleteByCategoryIdsAndTypeExcluding(categoryIds, "exclude", excludeSiteIds);
            // 批量插入新 exclude（ON DUPLICATE KEY UPDATE 保障幂等，1 条 SQL）
            if (!excludeSiteIds.isEmpty()) {
                List<GbSiteCategoryRelation> toInsert = new ArrayList<>(categoryIds.size() * excludeSiteIds.size());
                for (Long catId : categoryIds) {
                    for (Long siteId : excludeSiteIds) {
                        GbSiteCategoryRelation rel = new GbSiteCategoryRelation();
                        rel.setSiteId(siteId);
                        rel.setCategoryId(catId);
                        rel.setRelationType("exclude");
                        rel.setIsVisible("0");
                        rel.setCreateBy(username);
                        rel.setCreateTime(now);
                        toInsert.add(rel);
                    }
                }
                siteCategoryRelationMapper.batchInsertGbSiteCategoryRelation(toInsert);
            }
        }
        return success();
    }

    /**
     * 保存AI平台的网站关联关系（统一接口，同时处理关联和排除）
     * Body: { aiPlatformId, includeSiteIds: [], excludeSiteIds: [] }
     */
    @PreAuthorize("@ss.hasPermi('gamebox:siterelation:edit')")
    @Log(title = "保存AI平台网站关联", businessType = BusinessType.UPDATE)
    @PutMapping("/aiPlatform/saveRelations")
    public AjaxResult saveAiPlatformSiteRelations(@RequestBody Map<String, Object> params) {
        Long aiPlatformId = Long.parseLong(params.get("aiPlatformId").toString());
        List<Long> includeSiteIds = parseLongList(params, "includeSiteIds");
        List<Long> excludeSiteIds = parseLongList(params, "excludeSiteIds");

        List<GbSiteAiPlatformRelation> currentRelations = siteAiPlatformRelationMapper.selectSitesByAiPlatformId(aiPlatformId);
        List<Long> currentIncludes = currentRelations.stream()
            .filter(r -> !"exclude".equals(r.getRelationType()))
            .map(GbSiteAiPlatformRelation::getSiteId).collect(Collectors.toList());
        List<Long> currentExcludes = currentRelations.stream()
            .filter(r -> "exclude".equals(r.getRelationType()))
            .map(GbSiteAiPlatformRelation::getSiteId).collect(Collectors.toList());

        for (Long siteId : includeSiteIds) {
            if (!currentIncludes.contains(siteId)) addAiPlatformToSiteInternal(siteId, aiPlatformId);
        }
        for (Long siteId : currentIncludes) {
            if (!includeSiteIds.contains(siteId)) siteAiPlatformRelationMapper.deleteGbSiteAiPlatformRelation(siteId, aiPlatformId, "include");
        }
        for (Long siteId : excludeSiteIds) {
            if (!currentExcludes.contains(siteId)) addAiPlatformToSiteWithType(siteId, aiPlatformId, "exclude");
        }
        for (Long siteId : currentExcludes) {
            if (!excludeSiteIds.contains(siteId)) siteAiPlatformRelationMapper.deleteGbSiteAiPlatformRelation(siteId, aiPlatformId, "exclude");
        }
        return success();
    }

    /**
     * 保存内容工具的网站关联关系（统一接口，同时处理关联和排除）
     * Body: { promptTemplateId, includeSiteIds: [], excludeSiteIds: [] }
     */
    @PreAuthorize("@ss.hasPermi('gamebox:siterelation:edit')")
    @Log(title = "保存内容工具网站关联", businessType = BusinessType.UPDATE)
    @PutMapping("/promptTemplate/saveRelations")
    public AjaxResult saveTemplateSiteRelations(@RequestBody Map<String, Object> params) {
        Long templateId = Long.parseLong(params.get("promptTemplateId").toString());
        List<Long> includeSiteIds = parseLongList(params, "includeSiteIds");
        List<Long> excludeSiteIds = parseLongList(params, "excludeSiteIds");

        List<GbSiteTemplateRelation> currentRelations = siteTemplateRelationMapper.selectSitesByTemplateId(templateId);
        List<Long> currentIncludes = currentRelations.stream()
            .filter(r -> !"exclude".equals(r.getRelationType()))
            .map(GbSiteTemplateRelation::getSiteId).collect(Collectors.toList());
        List<Long> currentExcludes = currentRelations.stream()
            .filter(r -> "exclude".equals(r.getRelationType()))
            .map(GbSiteTemplateRelation::getSiteId).collect(Collectors.toList());

        // 新增 include
        for (Long siteId : includeSiteIds) {
            if (!currentIncludes.contains(siteId)) {
                // 若已存在 exclude 关系，先删除后再添加
                if (currentExcludes.contains(siteId)) siteTemplateRelationMapper.deleteGbSiteTemplateRelation(siteId, templateId);
                GbSiteTemplateRelation rel = new GbSiteTemplateRelation();
                rel.setSiteId(siteId);
                rel.setPromptTemplateId(templateId);
                rel.setRelationType("include");
                rel.setIsVisible("1");
                rel.setCreateBy(SecurityUtils.getUsername());
                rel.setCreateTime(DateUtils.getNowDate());
                siteTemplateRelationMapper.insertGbSiteTemplateRelation(rel);
            }
        }
        // 删除不再 include 的
        for (Long siteId : currentIncludes) {
            if (!includeSiteIds.contains(siteId)) siteTemplateRelationMapper.deleteGbSiteTemplateRelation(siteId, templateId);
        }
        // 新增 exclude
        for (Long siteId : excludeSiteIds) {
            if (!currentExcludes.contains(siteId)) {
                if (currentIncludes.contains(siteId)) siteTemplateRelationMapper.deleteGbSiteTemplateRelation(siteId, templateId);
                GbSiteTemplateRelation rel = new GbSiteTemplateRelation();
                rel.setSiteId(siteId);
                rel.setPromptTemplateId(templateId);
                rel.setRelationType("exclude");
                rel.setIsVisible("0");
                rel.setCreateBy(SecurityUtils.getUsername());
                rel.setCreateTime(DateUtils.getNowDate());
                siteTemplateRelationMapper.insertGbSiteTemplateRelation(rel);
            }
        }
        // 删除不再 exclude 的
        for (Long siteId : currentExcludes) {
            if (!excludeSiteIds.contains(siteId)) siteTemplateRelationMapper.deleteGbSiteTemplateRelation(siteId, templateId);
        }
        return success();
    }

    // ==================== 批量保存关联关系接口（批量SQL优化版：N×K 查询 → 最多 4 条 SQL） ====================

    @PreAuthorize("@ss.hasPermi('gamebox:siterelation:edit')")
    @Log(title = "批量保存工作流网站关联", businessType = BusinessType.UPDATE)
    @PutMapping("/workflow/batchSaveRelations")
    public AjaxResult batchSaveWorkflowSiteRelations(@RequestBody Map<String, Object> params) {
        List<Long> ids = parseLongList(params, "workflowIds");
        if (ids.isEmpty()) return success();
        boolean hi = params.containsKey("includeSiteIds"), he = params.containsKey("excludeSiteIds");
        List<Long> inc = hi ? parseLongList(params, "includeSiteIds") : new ArrayList<>();
        List<Long> exc = he ? parseLongList(params, "excludeSiteIds") : new ArrayList<>();
        String user = SecurityUtils.getUsername(); Date now = DateUtils.getNowDate();
        if (hi) {
            siteWorkflowRelationMapper.deleteByWorkflowIdsAndTypeExcluding(ids, "include", inc);
            if (!inc.isEmpty()) {
                List<GbSiteWorkflowRelation> rows = new ArrayList<>();
                for (Long id : ids) for (Long s : inc) { GbSiteWorkflowRelation r = new GbSiteWorkflowRelation(); r.setSiteId(s); r.setWorkflowId(id); r.setRelationType("include"); r.setIsVisible("1"); r.setCreateBy(user); r.setCreateTime(now); rows.add(r); }
                siteWorkflowRelationMapper.batchInsertSiteWorkflowRelations(rows);
            }
        }
        if (he) {
            siteWorkflowRelationMapper.deleteByWorkflowIdsAndTypeExcluding(ids, "exclude", exc);
            if (!exc.isEmpty()) {
                List<GbSiteWorkflowRelation> rows = new ArrayList<>();
                for (Long id : ids) for (Long s : exc) { GbSiteWorkflowRelation r = new GbSiteWorkflowRelation(); r.setSiteId(s); r.setWorkflowId(id); r.setRelationType("exclude"); r.setIsVisible("0"); r.setCreateBy(user); r.setCreateTime(now); rows.add(r); }
                siteWorkflowRelationMapper.batchInsertSiteWorkflowRelations(rows);
            }
        }
        return success();
    }

    @PreAuthorize("@ss.hasPermi('gamebox:siterelation:edit')")
    @Log(title = "批量保存盒子网站关联", businessType = BusinessType.UPDATE)
    @PutMapping("/box/batchSaveRelations")
    public AjaxResult batchSaveBoxSiteRelations(@RequestBody Map<String, Object> params) {
        List<Long> ids = parseLongList(params, "boxIds");
        if (ids.isEmpty()) return success();
        boolean hi = params.containsKey("includeSiteIds"), he = params.containsKey("excludeSiteIds");
        List<Long> inc = hi ? parseLongList(params, "includeSiteIds") : new ArrayList<>();
        List<Long> exc = he ? parseLongList(params, "excludeSiteIds") : new ArrayList<>();
        String user = SecurityUtils.getUsername(); Date now = DateUtils.getNowDate();
        if (hi) {
            siteBoxRelationMapper.deleteByBoxIdsAndTypeExcluding(ids, "include", inc);
            if (!inc.isEmpty()) {
                List<GbSiteBoxRelation> rows = new ArrayList<>();
                for (Long id : ids) for (Long s : inc) { GbSiteBoxRelation r = new GbSiteBoxRelation(); r.setSiteId(s); r.setBoxId(id); r.setRelationType("include"); r.setIsVisible("1"); r.setCreateBy(user); r.setCreateTime(now); rows.add(r); }
                siteBoxRelationMapper.batchInsertGbSiteBoxRelation(rows);
            }
        }
        if (he) {
            siteBoxRelationMapper.deleteByBoxIdsAndTypeExcluding(ids, "exclude", exc);
            if (!exc.isEmpty()) {
                List<GbSiteBoxRelation> rows = new ArrayList<>();
                for (Long id : ids) for (Long s : exc) { GbSiteBoxRelation r = new GbSiteBoxRelation(); r.setSiteId(s); r.setBoxId(id); r.setRelationType("exclude"); r.setIsVisible("0"); r.setCreateBy(user); r.setCreateTime(now); rows.add(r); }
                siteBoxRelationMapper.batchInsertGbSiteBoxRelation(rows);
            }
        }
        return success();
    }

    @PreAuthorize("@ss.hasPermi('gamebox:siterelation:edit')")
    @Log(title = "批量保存游戏网站关联", businessType = BusinessType.UPDATE)
    @PutMapping("/game/batchSaveRelations")
    public AjaxResult batchSaveGameSiteRelations(@RequestBody Map<String, Object> params) {
        List<Long> ids = parseLongList(params, "gameIds");
        if (ids.isEmpty()) return success();
        boolean hi = params.containsKey("includeSiteIds"), he = params.containsKey("excludeSiteIds");
        List<Long> inc = hi ? parseLongList(params, "includeSiteIds") : new ArrayList<>();
        List<Long> exc = he ? parseLongList(params, "excludeSiteIds") : new ArrayList<>();
        String user = SecurityUtils.getUsername(); Date now = DateUtils.getNowDate();
        if (hi) {
            siteGameRelationMapper.deleteByGameIdsAndTypeExcluding(ids, "include", inc);
            if (!inc.isEmpty()) {
                List<GbSiteGameRelation> rows = new ArrayList<>();
                for (Long id : ids) for (Long s : inc) { GbSiteGameRelation r = new GbSiteGameRelation(); r.setSiteId(s); r.setGameId(id); r.setRelationType("include"); r.setIsVisible("1"); r.setCreateBy(user); r.setCreateTime(now); rows.add(r); }
                siteGameRelationMapper.batchInsertGbSiteGameRelation(rows);
            }
        }
        if (he) {
            siteGameRelationMapper.deleteByGameIdsAndTypeExcluding(ids, "exclude", exc);
            if (!exc.isEmpty()) {
                List<GbSiteGameRelation> rows = new ArrayList<>();
                for (Long id : ids) for (Long s : exc) { GbSiteGameRelation r = new GbSiteGameRelation(); r.setSiteId(s); r.setGameId(id); r.setRelationType("exclude"); r.setIsVisible("0"); r.setCreateBy(user); r.setCreateTime(now); rows.add(r); }
                siteGameRelationMapper.batchInsertGbSiteGameRelation(rows);
            }
        }
        return success();
    }

    @PreAuthorize("@ss.hasPermi('gamebox:siterelation:edit')")
    @Log(title = "批量保存文章网站关联", businessType = BusinessType.UPDATE)
    @PutMapping("/article/batchSaveRelations")
    public AjaxResult batchSaveArticleSiteRelations(@RequestBody Map<String, Object> params) {
        List<Long> ids = parseLongList(params, "articleIds");
        if (ids.isEmpty()) return success();
        boolean hi = params.containsKey("includeSiteIds"), he = params.containsKey("excludeSiteIds");
        List<Long> inc = hi ? parseLongList(params, "includeSiteIds") : new ArrayList<>();
        List<Long> exc = he ? parseLongList(params, "excludeSiteIds") : new ArrayList<>();
        String user = SecurityUtils.getUsername(); Date now = DateUtils.getNowDate();
        if (hi) {
            siteArticleRelationMapper.deleteByArticleIdsAndTypeExcluding(ids, "include", inc);
            if (!inc.isEmpty()) {
                List<GbSiteArticleRelation> rows = new ArrayList<>();
                for (Long id : ids) for (Long s : inc) { GbSiteArticleRelation r = new GbSiteArticleRelation(); r.setSiteId(s); r.setArticleId(id); r.setRelationType("include"); r.setIsVisible("1"); r.setCreateBy(user); r.setCreateTime(now); rows.add(r); }
                siteArticleRelationMapper.batchInsertGbSiteArticleRelation(rows);
            }
        }
        if (he) {
            siteArticleRelationMapper.deleteByArticleIdsAndTypeExcluding(ids, "exclude", exc);
            if (!exc.isEmpty()) {
                List<GbSiteArticleRelation> rows = new ArrayList<>();
                for (Long id : ids) for (Long s : exc) { GbSiteArticleRelation r = new GbSiteArticleRelation(); r.setSiteId(s); r.setArticleId(id); r.setRelationType("exclude"); r.setIsVisible("0"); r.setCreateBy(user); r.setCreateTime(now); rows.add(r); }
                siteArticleRelationMapper.batchInsertGbSiteArticleRelation(rows);
            }
        }
        return success();
    }

    @PreAuthorize("@ss.hasPermi('gamebox:siterelation:edit')")
    @Log(title = "批量保存原子工具网站关联", businessType = BusinessType.UPDATE)
    @PutMapping("/atomicTool/batchSaveRelations")
    public AjaxResult batchSaveAtomicToolSiteRelations(@RequestBody Map<String, Object> params) {
        List<Long> ids = parseLongList(params, "atomicToolIds");
        if (ids.isEmpty()) return success();
        boolean hi = params.containsKey("includeSiteIds"), he = params.containsKey("excludeSiteIds");
        List<Long> inc = hi ? parseLongList(params, "includeSiteIds") : new ArrayList<>();
        List<Long> exc = he ? parseLongList(params, "excludeSiteIds") : new ArrayList<>();
        String user = SecurityUtils.getUsername(); Date now = DateUtils.getNowDate();
        if (hi) {
            siteAtomicToolRelationMapper.deleteByAtomicToolIdsAndTypeExcluding(ids, "include", inc);
            if (!inc.isEmpty()) {
                List<GbSiteAtomicToolRelation> rows = new ArrayList<>();
                for (Long id : ids) for (Long s : inc) { GbSiteAtomicToolRelation r = new GbSiteAtomicToolRelation(); r.setSiteId(s); r.setAtomicToolId(id); r.setRelationType("include"); r.setIsVisible("1"); r.setCreateBy(user); r.setCreateTime(now); rows.add(r); }
                siteAtomicToolRelationMapper.batchInsertSiteAtomicToolRelations(rows);
            }
        }
        if (he) {
            siteAtomicToolRelationMapper.deleteByAtomicToolIdsAndTypeExcluding(ids, "exclude", exc);
            if (!exc.isEmpty()) {
                List<GbSiteAtomicToolRelation> rows = new ArrayList<>();
                for (Long id : ids) for (Long s : exc) { GbSiteAtomicToolRelation r = new GbSiteAtomicToolRelation(); r.setSiteId(s); r.setAtomicToolId(id); r.setRelationType("exclude"); r.setIsVisible("0"); r.setCreateBy(user); r.setCreateTime(now); rows.add(r); }
                siteAtomicToolRelationMapper.batchInsertSiteAtomicToolRelations(rows);
            }
        }
        return success();
    }

    // ==================== 批量查询关联网站（多ID → Map） ====================

    /**
     * 批量查询多个分类各自关联的网站（一次请求代替 N 次）
     * Body: [1, 2, 3, ...]  返回: { categoryId -> sites[] }
     */
    @PreAuthorize("@ss.hasPermi('gamebox:siterelation:list')")
    @PostMapping("/category/batchSites")
    public AjaxResult getBatchSitesByCategories(@RequestBody List<Long> categoryIds) {
        Map<Long, List<GbSiteCategoryRelation>> result = new LinkedHashMap<>();
        for (Long id : categoryIds) {
            result.put(id, siteCategoryRelationMapper.selectSitesByCategoryId(id));
        }
        return AjaxResult.success(result);
    }

    /**
     * 批量查询多个游戏盒子各自关联的网站（一次请求代替 N 次）
     * Body: [1, 2, 3, ...]  返回: { boxId -> sites[] }
     */
    @PreAuthorize("@ss.hasPermi('gamebox:siterelation:list')")
    @PostMapping("/box/batchSites")
    public AjaxResult getBatchSitesByBoxes(@RequestBody List<Long> boxIds) {
        Map<Long, List<GbSiteBoxRelation>> result = new LinkedHashMap<>();
        for (Long id : boxIds) {
            result.put(id, siteBoxRelationMapper.selectSitesByBoxId(id));
        }
        return AjaxResult.success(result);
    }

    /**
     * 批量查询多个游戏各自关联的网站（一次请求代替 N 次）
     * Body: [1, 2, 3, ...]  返回: { gameId -> sites[] }
     */
    @PreAuthorize("@ss.hasPermi('gamebox:siterelation:list')")
    @PostMapping("/game/batchSites")
    public AjaxResult getBatchSitesByGames(@RequestBody List<Long> gameIds) {
        Map<Long, List<GbSiteGameRelation>> result = new LinkedHashMap<>();
        for (Long id : gameIds) {
            result.put(id, siteGameRelationMapper.selectSitesByGameId(id));
        }
        return AjaxResult.success(result);
    }

    /**
     * 批量查询多篇文章各自关联的网站（一次请求代替 N 次）
     * Body: [1, 2, 3, ...]  返回: { articleId -> sites[] }
     */
    @PreAuthorize("@ss.hasPermi('gamebox:siterelation:list')")
    @PostMapping("/article/batchSites")
    public AjaxResult getBatchSitesByArticles(@RequestBody List<Long> articleIds) {
        Map<Long, List<GbSiteArticleRelation>> result = new LinkedHashMap<>();
        for (Long id : articleIds) {
            result.put(id, siteArticleRelationMapper.selectSitesByArticleId(id));
        }
        return AjaxResult.success(result);
    }

    /**
     * 批量查询多个存储配置各自关联的网站（一次请求代替 N 次）
     * Body: [1, 2, 3, ...]  返回: { storageConfigId -> sites[] }
     */
    @PreAuthorize("@ss.hasPermi('gamebox:siterelation:list')")
    @PostMapping("/storageConfig/batchSites")
    public AjaxResult getBatchSitesByStorageConfigs(@RequestBody List<Long> storageConfigIds) {
        Map<Long, List<GbSiteStorageConfigRelation>> result = new LinkedHashMap<>();
        for (Long id : storageConfigIds) {
            result.put(id, siteStorageConfigRelationMapper.selectSitesByStorageConfigId(id));
        }
        return AjaxResult.success(result);
    }

    /**
     * 批量查询多个AI平台各自关联的网站（一次请求代替 N 次）
     * Body: [1, 2, 3, ...]  返回: { aiPlatformId -> sites[] }
     */
    @PreAuthorize("@ss.hasPermi('gamebox:siterelation:list')")
    @PostMapping("/aiPlatform/batchSites")
    public AjaxResult getBatchSitesByAiPlatforms(@RequestBody List<Long> aiPlatformIds) {
        Map<Long, List<GbSiteAiPlatformRelation>> result = new LinkedHashMap<>();
        for (Long id : aiPlatformIds) {
            result.put(id, siteAiPlatformRelationMapper.selectSitesByAiPlatformId(id));
        }
        return AjaxResult.success(result);
    }

    /**
     * 批量查询多个原子工具各自关联的网站（一次请求代替 N 次）
     * Body: [1, 2, 3, ...]  返回: { atomicToolId -> sites[] }
     */
    @PreAuthorize("@ss.hasPermi('gamebox:siterelation:list')")
    @PostMapping("/atomicTool/batchSites")
    public AjaxResult getBatchSitesByAtomicTools(@RequestBody List<Long> atomicToolIds) {
        Map<Long, List<GbSiteAtomicToolRelation>> result = new LinkedHashMap<>();
        for (Long id : atomicToolIds) {
            result.put(id, siteAtomicToolRelationMapper.selectSitesByAtomicToolId(id));
        }
        return AjaxResult.success(result);
    }

    /**
     * 批量查询多个工作流各自关联的网站（一次请求代替 N 次）
     * Body: [1, 2, 3, ...]  返回: { workflowId -> sites[] }
     */
    @PreAuthorize("@ss.hasPermi('gamebox:siterelation:list')")
    @PostMapping("/workflow/batchSites")
    public AjaxResult getBatchSitesByWorkflows(@RequestBody List<Long> workflowIds) {
        Map<Long, List<GbSiteWorkflowRelation>> result = new LinkedHashMap<>();
        for (Long id : workflowIds) {
            result.put(id, siteWorkflowRelationMapper.selectSitesByWorkflowId(id));
        }
        return AjaxResult.success(result);
    }

    // ==================== 标题池批次关联 ====================

    /**
     * 获取批次关联的网站列表
     */
    @PreAuthorize("@ss.hasPermi('gamebox:siterelation:list')")
    @GetMapping("/titleBatch/sites/{batchId}")
    public AjaxResult getTitleBatchSites(@PathVariable Long batchId) {
        List<Map<String, Object>> sites = titleBatchMapper.getBatchSites(batchId);
        return success(sites);
    }

    /**
     * 批量查询多个批次各自关联的网站（一次请求代替 N 次）
     * Body: [1, 2, 3, ...]  返回: { batchId -> sites[] }
     */
    @PreAuthorize("@ss.hasPermi('gamebox:siterelation:list')")
    @PostMapping("/titleBatch/batchSites")
    public AjaxResult getBatchSitesByBatchIds(@RequestBody List<Long> batchIds) {
        Map<Long, List<Map<String, Object>>> result = new LinkedHashMap<>();
        for (Long id : batchIds) {
            result.put(id, titleBatchMapper.getBatchSites(id));
        }
        return success(result);
    }

    /**
     * 更新批次在网站的可见性（关联关系维度）
     */
    @PreAuthorize("@ss.hasPermi('gamebox:siterelation:edit')")
    @Log(title = "更新批次关联可见性", businessType = BusinessType.UPDATE)
    @PutMapping("/titleBatch/visibility")
    public AjaxResult updateTitleBatchVisibility(@RequestBody Map<String, Object> params) {
        Long siteId = Long.valueOf(params.get("siteId").toString());
        Long batchId = Long.valueOf(params.get("batchId").toString());
        Integer isVisible = Integer.valueOf(params.get("isVisible").toString());
        titleBatchMapper.updateBatchRelationVisibility(siteId, batchId, isVisible);
        return success();
    }

    /**
     * 批量保存批次的网站关联关系（批量SQL优化版）
     * Body: { batchIds: [], includeSiteIds?: [], excludeSiteIds?: [] }
     * includeSiteIds/excludeSiteIds 任意一个可省略，省略则不更新对应侧
     */
    @PreAuthorize("@ss.hasPermi('gamebox:siterelation:edit')")
    @Log(title = "批量保存批次网站关联", businessType = BusinessType.UPDATE)
    @PutMapping("/titleBatch/batchSaveRelations")
    public AjaxResult batchSaveTitleBatchSiteRelations(@RequestBody Map<String, Object> params) {
        List<Long> ids = parseLongList(params, "batchIds");
        if (ids.isEmpty()) return success();
        boolean hi = params.containsKey("includeSiteIds"), he = params.containsKey("excludeSiteIds");
        List<Long> inc = hi ? parseLongList(params, "includeSiteIds") : new ArrayList<>();
        List<Long> exc = he ? parseLongList(params, "excludeSiteIds") : new ArrayList<>();
        String user = SecurityUtils.getUsername(); Date now = DateUtils.getNowDate();
        if (hi) {
            siteTitleBatchRelationMapper.deleteByBatchIdsAndTypeExcluding(ids, "include", inc);
            if (!inc.isEmpty()) {
                List<GbSiteTitleBatchRelation> rows = new ArrayList<>();
                for (Long id : ids) for (Long s : inc) { GbSiteTitleBatchRelation r = new GbSiteTitleBatchRelation(); r.setSiteId(s); r.setTitleBatchId(id); r.setRelationType("include"); r.setIsVisible("1"); r.setCreateBy(user); r.setCreateTime(now); rows.add(r); }
                siteTitleBatchRelationMapper.batchInsertGbSiteTitleBatchRelations(rows);
            }
        }
        if (he) {
            siteTitleBatchRelationMapper.deleteByBatchIdsAndTypeExcluding(ids, "exclude", exc);
            if (!exc.isEmpty()) {
                List<GbSiteTitleBatchRelation> rows = new ArrayList<>();
                for (Long id : ids) for (Long s : exc) { GbSiteTitleBatchRelation r = new GbSiteTitleBatchRelation(); r.setSiteId(s); r.setTitleBatchId(id); r.setRelationType("exclude"); r.setIsVisible("0"); r.setCreateBy(user); r.setCreateTime(now); rows.add(r); }
                siteTitleBatchRelationMapper.batchInsertGbSiteTitleBatchRelations(rows);
            }
        }
        return success();
    }
}
