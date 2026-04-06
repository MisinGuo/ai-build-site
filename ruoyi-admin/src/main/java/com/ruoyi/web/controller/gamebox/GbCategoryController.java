package com.ruoyi.web.controller.gamebox;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.util.StringUtils;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.gamebox.domain.GbCategory;
import com.ruoyi.gamebox.service.IGbCategoryService;
import com.ruoyi.gamebox.service.ITranslationService;

/**
 * 分类管理Controller
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/gamebox/category")
public class GbCategoryController extends BaseController
{
    @Autowired
    private IGbCategoryService gbCategoryService;
    
    @Autowired
    private ITranslationService translationService;

    /**
     * 查询分类列表
     */
    @PreAuthorize("@ss.hasPermi('gamebox:category:list')")
    @GetMapping("/list")
    public TableDataInfo list(GbCategory gbCategory)
    {
        startPage();
        List<GbCategory> list = gbCategoryService.selectGbCategoryList(gbCategory);
        return getDataTable(list);
    }
    
    /**
     * 查询支持多语言的分类列表
     */
    @PreAuthorize("@ss.hasPermi('gamebox:category:list')")
    @GetMapping("/list/multilang")
    public TableDataInfo listWithTranslations(@RequestParam(value = "locale", defaultValue = "zh-CN") String locale,
                                            GbCategory gbCategory)
    {
        startPage();
        List<GbCategory> list = gbCategoryService.selectGbCategoryList(gbCategory);
        
        // 批量获取所有分类的翻译数据
        if (!list.isEmpty()) {
            List<Long> entityIds = list.stream().map(GbCategory::getId).collect(Collectors.toList());
            Map<Long, Map<String, String>> allTranslations = translationService.getBatchEntityTranslations("category", entityIds, locale);
            
            // 将翻译数据应用到每个分类对象
            for (GbCategory category : list) {
                Map<String, String> translations = allTranslations.get(category.getId());
                if (translations != null) {
                    if (StringUtils.hasText(translations.get("name"))) {
                        category.setName(translations.get("name"));
                    }
                    if (StringUtils.hasText(translations.get("description"))) {
                        category.setDescription(translations.get("description"));
                    }
                }
            }
        }
        
        return getDataTable(list);
    }

    /**
     * 查询可见的分类列表（用于编辑器选择）
     */
    @PreAuthorize("@ss.hasPermi('gamebox:category:list')")
    @GetMapping("/visibleList")
    public TableDataInfo visibleList(GbCategory gbCategory)
    {
        startPage();
        List<GbCategory> list = gbCategoryService.selectVisibleGbCategoryList(gbCategory);
        return getDataTable(list);
    }
    
    /**
     * 查询支持多语言的可见分类列表
     */
    @PreAuthorize("@ss.hasPermi('gamebox:category:list')")
    @GetMapping("/visibleList/multilang")
    public TableDataInfo visibleListWithTranslations(@RequestParam(value = "locale", defaultValue = "zh-CN") String locale,
                                                   GbCategory gbCategory)
    {
        startPage();
        List<GbCategory> list = gbCategoryService.selectVisibleGbCategoryList(gbCategory);
        
        // 批量获取所有分类的翻译数据
        if (!list.isEmpty()) {
            List<Long> entityIds = list.stream().map(GbCategory::getId).collect(Collectors.toList());
            Map<Long, Map<String, String>> allTranslations = translationService.getBatchEntityTranslations("category", entityIds, locale);
            
            // 将翻译数据应用到每个分类对象
            for (GbCategory category : list) {
                Map<String, String> translations = allTranslations.get(category.getId());
                if (translations != null) {
                    if (StringUtils.hasText(translations.get("name"))) {
                        category.setName(translations.get("name"));
                    }
                    if (StringUtils.hasText(translations.get("description"))) {
                        category.setDescription(translations.get("description"));
                    }
                }
            }
        }
        
        return getDataTable(list);
    }

    /**
     * 导出分类列表
     */
    @PreAuthorize("@ss.hasPermi('gamebox:category:export')")
    @Log(title = "分类管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(GbCategory gbCategory)
    {
        List<GbCategory> list = gbCategoryService.selectGbCategoryList(gbCategory);
        ExcelUtil<GbCategory> util = new ExcelUtil<GbCategory>(GbCategory.class);
        util.exportExcel(list, "分类数据");
    }

    /**
     * 获取分类详细信息
     */
    @PreAuthorize("@ss.hasPermi('gamebox:category:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(gbCategoryService.selectGbCategoryById(id));
    }

    /**
     * 获取分类类型选项列表
     */
    @PreAuthorize("@ss.hasPermi('gamebox:category:list')")
    @GetMapping("/categoryTypes")
    public AjaxResult getCategoryTypes()
    {
        return success(gbCategoryService.getCategoryTypeOptions());
    }

    /**
     * 新增分类
     */
    @PreAuthorize("@ss.hasPermi('gamebox:category:add')")
    @Log(title = "分类管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody GbCategory gbCategory)
    {
        gbCategory.setCreateBy(getUsername());
        int result = gbCategoryService.insertGbCategory(gbCategory);
        if (result > 0) {
            return AjaxResult.success("新增成功", gbCategory.getId());
        }
        return error("新增失败");
    }

    /**
     * 修改分类
     */
    @PreAuthorize("@ss.hasPermi('gamebox:category:edit')")
    @Log(title = "分类管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody GbCategory gbCategory)
    {
        gbCategory.setUpdateBy(getUsername());
        return toAjax(gbCategoryService.updateGbCategory(gbCategory));
    }

    /**
     * 删除分类
     */
    @PreAuthorize("@ss.hasPermi('gamebox:category:remove')")
    @Log(title = "分类管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(gbCategoryService.deleteGbCategoryByIds(ids));
    }

    /**
     * 查询文章板块列表
     */
    @PreAuthorize("@ss.hasPermi('gamebox:category:list')")
    @GetMapping("/sections")
    public AjaxResult listSections(Long siteId)
    {
        List<GbCategory> list = gbCategoryService.selectArticleSections(siteId);
        return success(list);
    }

    /**
     * 通过板块ID查询分类列表
     */
    @PreAuthorize("@ss.hasPermi('gamebox:category:list')")
    @GetMapping("/sections/{sectionId}/categories")
    public AjaxResult listCategoriesBySection(@PathVariable("sectionId") Long sectionId)
    {
        List<GbCategory> list = gbCategoryService.selectCategoriesBySection(sectionId);
        return success(list);
    }
    
    /**
     * 获取所有游戏分类（用于字段映射配置）
     */
    @PreAuthorize("@ss.hasPermi('gamebox:category:list')")
    @GetMapping("/options")
    public AjaxResult getCategoryOptions()
    {
        GbCategory query = new GbCategory();
        query.setCategoryType("game");
        query.setStatus("1");
        List<GbCategory> list = gbCategoryService.selectGbCategoryList(query);
        
        // 转换为简化的选项格式
        List<Map<String, Object>> options = list.stream().map(category -> {
            Map<String, Object> option = new java.util.HashMap<>();
            option.put("id", category.getId());
            option.put("name", category.getName());
            option.put("slug", category.getSlug());
            option.put("description", category.getDescription());
            return option;
        }).collect(Collectors.toList());
        
        return success(options);
    }
}
