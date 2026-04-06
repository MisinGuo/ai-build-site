package com.ruoyi.web.controller.gamebox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.ruoyi.gamebox.domain.GbArticle;
import com.ruoyi.gamebox.domain.GbGameBox;
import com.ruoyi.gamebox.domain.GbBoxCategoryRelation;
import com.ruoyi.gamebox.domain.dto.CategoryValidationResult;
import com.ruoyi.gamebox.service.IGbArticleService;
import com.ruoyi.gamebox.service.IGbGameBoxService;
import com.ruoyi.gamebox.service.IGbBoxCategoryRelationService;
import com.ruoyi.gamebox.service.ITranslationService;
import com.ruoyi.gamebox.service.IGbMasterArticleHomepageBindingService;
import com.ruoyi.gamebox.service.IGbSiteService;
import com.ruoyi.gamebox.domain.GbMasterArticleHomepageBinding;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.util.StringUtils;

/**
 * 游戏盒子管理Controller
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/gamebox/box")
public class GbGameBoxController extends BaseController
{
    @Autowired
    private IGbGameBoxService gbGameBoxService;
    
    @Autowired
    private IGbArticleService gbArticleService;
    
    @Autowired
    private IGbBoxCategoryRelationService boxCategoryRelationService;
    
    @Autowired
    private ITranslationService translationService;
    
    @Autowired
    private IGbMasterArticleHomepageBindingService masterArticleHomepageBindingService;

    @Autowired
    private IGbSiteService siteService;

    /**
     * 查询游戏盒子列表
     */
    @PreAuthorize("@ss.hasPermi('gamebox:box:list')")
    @GetMapping("/list")
    public TableDataInfo list(GbGameBox gbGameBox)
    {
        // related 模式下注入 personalSiteId，使 game_count 子查询能按站点可见性统计
        if ("related".equals(gbGameBox.getQueryMode()) && gbGameBox.getSiteId() != null) {
            try {
                Long personalSiteId = siteService.resolvePersonalSiteIdForSite(gbGameBox.getSiteId());
                if (personalSiteId != null) {
                    gbGameBox.getParams().put("personalSiteId", personalSiteId);
                }
            } catch (Exception ignored) {}
        }
        startPage();
        List<GbGameBox> list = gbGameBoxService.selectGbGameBoxList(gbGameBox);
        return getDataTable(list);
    }
    
    /**
     * 查询支持多语言的游戏盒子列表
     */
    @PreAuthorize("@ss.hasPermi('gamebox:box:list')")
    @GetMapping("/list/multilang")
    public TableDataInfo listWithTranslations(@RequestParam(value = "locale", defaultValue = "zh-CN") String locale,
                                            GbGameBox gbGameBox)
    {
        startPage();
        List<GbGameBox> list = gbGameBoxService.selectGbGameBoxList(gbGameBox);
        
        // 批量获取所有盒子的翻译数据
        if (!list.isEmpty()) {
            List<Long> entityIds = list.stream().map(GbGameBox::getId).collect(Collectors.toList());
            Map<Long, Map<String, String>> allTranslations = translationService.getBatchEntityTranslations("gamebox", entityIds, locale);
            
            // 将翻译数据应用到每个盒子对象
            for (GbGameBox box : list) {
                Map<String, String> translations = allTranslations.get(box.getId());
                if (translations != null) {
                    if (StringUtils.hasText(translations.get("name"))) {
                        box.setName(translations.get("name"));
                    }
                    if (StringUtils.hasText(translations.get("description"))) {
                        box.setDescription(translations.get("description"));
                    }
                }
            }
        }
        
        return getDataTable(list);
    }

    /**
     * 导出游戏盒子列表
     */
    @PreAuthorize("@ss.hasPermi('gamebox:box:export')")
    @Log(title = "游戏盒子管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(GbGameBox gbGameBox)
    {
        List<GbGameBox> list = gbGameBoxService.selectGbGameBoxList(gbGameBox);
        ExcelUtil<GbGameBox> util = new ExcelUtil<GbGameBox>(GbGameBox.class);
        util.exportExcel(list, "游戏盒子数据");
    }

    /**
     * 获取游戏盒子详细信息
     */
    @PreAuthorize("@ss.hasPermi('gamebox:box:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(gbGameBoxService.selectGbGameBoxById(id));
    }

    /**
     * 新增游戏盒子
     */
    @PreAuthorize("@ss.hasPermi('gamebox:box:add')")
    @Log(title = "游戏盒子管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody GbGameBox gbGameBox)
    {
        gbGameBox.setCreateBy(getUsername());
        int result = gbGameBoxService.insertGbGameBox(gbGameBox);
        if (result > 0) {
            return AjaxResult.success("新增成功", gbGameBox.getId());
        }
        return error("新增失败");
    }

    /**
     * 修改游戏盒子
     */
    @PreAuthorize("@ss.hasPermi('gamebox:box:edit')")
    @Log(title = "游戏盒子管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody GbGameBox gbGameBox)
    {
        gbGameBox.setUpdateBy(getUsername());
        
        // 检查是否需要同步游戏siteId
        GbGameBox oldBox = gbGameBoxService.selectGbGameBoxById(gbGameBox.getId());
        boolean siteIdChanged = oldBox != null && 
                                gbGameBox.getSiteId() != null && 
                                !gbGameBox.getSiteId().equals(oldBox.getSiteId());
        boolean needSyncGameSiteId = siteIdChanged && 
                                     gbGameBox.getSyncGameSiteId() != null && 
                                     gbGameBox.getSyncGameSiteId();
        
        int result = gbGameBoxService.updateGbGameBox(gbGameBox);
        
        if (result > 0) {
            // 构建返回消息
            StringBuilder message = new StringBuilder("修改成功");
            
            if (needSyncGameSiteId) {
                // 查询更新了多少游戏
                try {
                    // 这里可以添加查询逻辑来获取实际更新的游戏数量
                    message.append("\n✓ 已同步更新该盒子下所有游戏的所属网站");
                } catch (Exception e) {
                    logger.error("查询同步结果失败", e);
                }
            }
            
            return AjaxResult.success(message.toString());
        }
        
        return toAjax(result);
    }
    
    /**
     * 更新游戏盒子状态
     */
    @PreAuthorize("@ss.hasPermi('gamebox:box:edit')")
    @Log(title = "游戏盒子状态", businessType = BusinessType.UPDATE)
    @PutMapping("/status")
    public AjaxResult updateStatus(@RequestBody Map<String, Object> params)
    {
        Long id = Long.parseLong(params.get("id").toString());
        String status = params.get("status").toString();
        
        GbGameBox gbGameBox = new GbGameBox();
        gbGameBox.setId(id);
        gbGameBox.setStatus(status);
        gbGameBox.setUpdateBy(getUsername());
        
        return toAjax(gbGameBoxService.updateGbGameBox(gbGameBox));
    }

    /**
     * 删除游戏盒子
     */
    @PreAuthorize("@ss.hasPermi('gamebox:box:remove')")
    @Log(title = "游戏盒子管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(gbGameBoxService.deleteGbGameBoxByIds(ids));
    }
    
    /**
     * 获取盒子的分类列表
     */
    @GetMapping("/{id}/categories")
    public AjaxResult getBoxCategories(@PathVariable("id") Long id)
    {
        try {
            List<GbBoxCategoryRelation> categories = boxCategoryRelationService.selectCategoriesByBoxId(id);
            return success(categories);
        } catch (Exception e) {
            logger.error("获取盒子分类列表失败", e);
            return error("获取分类列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 保存盒子的分类关联
     */
    @PostMapping("/{id}/categories")
    public AjaxResult saveBoxCategories(@PathVariable("id") Long id, @RequestBody List<Map<String, Object>> categories)
    {
        try {
            // 转换为 GbBoxCategoryRelation 列表
            List<GbBoxCategoryRelation> relations = categories.stream().map(cat -> {
                GbBoxCategoryRelation relation = new GbBoxCategoryRelation();
                relation.setBoxId(id);
                relation.setCategoryId(Long.valueOf(cat.get("categoryId").toString()));
                relation.setIsPrimary(cat.get("isPrimary") != null ? cat.get("isPrimary").toString() : "0");
                // sortOrder 可以根据列表顺序自动生成
                return relation;
            }).collect(Collectors.toList());
            
            // 使用 service 保存（会先删除旧关联再插入新关联）
            boxCategoryRelationService.saveBoxCategories(id, relations);
            return success();
        } catch (Exception e) {
            logger.error("保存盒子分类关联失败", e);
            return error("保存分类关联失败: " + e.getMessage());
        }
    }
    
    /**
     * 验证盒子的siteId变更后分类是否有效
     */
    @GetMapping("/{id}/validate-categories")
    public AjaxResult validateCategories(@PathVariable("id") Long id, Long newSiteId)
    {
        try {
            CategoryValidationResult result = gbGameBoxService.validateCategoriesForSiteChange(id, newSiteId);
            return success(result);
        } catch (Exception e) {
            logger.error("验证分类有效性失败", e);
            return error("验证失败: " + e.getMessage());
        }
    }    
    /**
     * 获取游戏盒子的主页绑定
     */
    @GetMapping("/{id}/homepage")
    public AjaxResult getGameBoxHomepage(@PathVariable("id") Long id)
    {
        try {
            GbMasterArticleHomepageBinding binding = masterArticleHomepageBindingService.selectByGameBoxIdWithTitle(id);
            return success(binding);
        } catch (Exception e) {
            logger.error("获取游戏盒子主页绑定失败", e);
            return error("获取主页绑定失败: " + e.getMessage());
        }
    }
    
    /**
     * 绑定主文章到游戏盒子主页
     */
    @PostMapping("/{id}/homepage")
    public AjaxResult bindGameBoxHomepage(@PathVariable("id") Long id, @RequestBody Map<String, Object> params)
    {
        try {
            Long masterArticleId = Long.valueOf(params.get("masterArticleId").toString());
            boolean force = params.containsKey("force") && Boolean.parseBoolean(params.get("force").toString());
            
            AjaxResult result = masterArticleHomepageBindingService.bindToGameBox(masterArticleId, id, force);
            return result;
        } catch (Exception e) {
            logger.error("绑定游戏盒子主页失败", e);
            return error("绑定主页失败: " + e.getMessage());
        }
    }
    
    /**
     * 解绑游戏盒子主页
     */
    @DeleteMapping("/{id}/homepage")
    public AjaxResult unbindGameBoxHomepage(@PathVariable("id") Long id, @RequestBody Map<String, Object> params)
    {
        try {
            Long masterArticleId = Long.valueOf(params.get("masterArticleId").toString());
            
            int result = masterArticleHomepageBindingService.unbindFromGameBox(masterArticleId, id);
            return result > 0 ? success("解绑主页成功") : error("解绑主页失败");
        } catch (Exception e) {
            logger.error("解绑游戏盒子主页失败", e);
            return error("解绑主页失败: " + e.getMessage());
        }
    }}
