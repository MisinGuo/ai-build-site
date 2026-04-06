package com.ruoyi.web.controller.gamebox;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
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
import com.ruoyi.gamebox.domain.GbBoxFieldMapping;
import com.ruoyi.gamebox.service.IGbBoxFieldMappingService;
import com.ruoyi.gamebox.service.ITableSchemaService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;

/**
 * 盒子字段映射配置Controller
 * 
 * @author ruoyi
 * @date 2026-01-25
 */
@RestController
@RequestMapping("/gamebox/box-field-mapping")
public class GbBoxFieldMappingController extends BaseController
{
    @Autowired
    private IGbBoxFieldMappingService gbBoxFieldMappingService;

    @Autowired
    private ITableSchemaService tableSchemaService;

    /**
     * 不允许映射的系统关键字段
     */
    private static final Set<String> EXCLUDED_FIELDS = new HashSet<>(Arrays.asList(
        "id", "create_by", "create_time", "update_by", "update_time", 
        "del_flag", "site_id", "box_id", "game_id"
    ));

    /**
     * 查询字段映射配置列表
     */
    @PreAuthorize("@ss.hasPermi('gamebox:field-mapping:list')")
    @GetMapping("/list")
    public TableDataInfo list(GbBoxFieldMapping gbBoxFieldMapping)
    {
        startPage();
        List<GbBoxFieldMapping> list = gbBoxFieldMappingService.selectGbBoxFieldMappingList(gbBoxFieldMapping);
        return getDataTable(list);
    }

    /**
     * 根据平台和资源类型查询字段映射配置（不分页）
     */
    @PreAuthorize("@ss.hasPermi('gamebox:field-mapping:query')")
    @GetMapping("/listByPlatform")
    public AjaxResult listByPlatform(String platform, String resourceType)
    {
        List<GbBoxFieldMapping> list = gbBoxFieldMappingService.selectByPlatformAndType(platform, resourceType);
        return success(list);
    }

    /**
     * 根据盒子ID查询字段映射配置
     */
    @PreAuthorize("@ss.hasPermi('gamebox:field-mapping:query')")
    @GetMapping("/box/{boxId}")
    public AjaxResult listByBoxId(@PathVariable Long boxId)
    {
        List<GbBoxFieldMapping> list = gbBoxFieldMappingService.selectByBoxId(boxId);
        return success(list);
    }

    /**
     * 保存盒子的字段映射配置（批量）
     * 会删除该盒子原有的映射配置，然后保存新的配置
     */
    @PreAuthorize("@ss.hasPermi('gamebox:field-mapping:edit')")
    @Log(title = "字段映射配置", businessType = BusinessType.UPDATE)
    @PostMapping("/box/{boxId}")
    public AjaxResult saveBoxMappings(@PathVariable Long boxId, @RequestBody List<GbBoxFieldMapping> mappings)
    {
        // 验证是否包含系统关键字段（仅对主表字段检查，JSON字段不限制）
        for (GbBoxFieldMapping mapping : mappings) {
            String targetField = mapping.getTargetField();
            String targetLocation = mapping.getTargetLocation();
            // 只对主表、分类关联表、关系表的字段进行限制，JSON字段不限制
            boolean isMainTable = "main".equals(targetLocation) || "category_relation".equals(targetLocation) || "relation".equals(targetLocation);
            if (isMainTable && targetField != null && EXCLUDED_FIELDS.contains(targetField.toLowerCase())) {
                return error("字段 [" + targetField + "] 是系统关键字段，不允许映射");
            }
        }
        
        // 删除旧配置
        gbBoxFieldMappingService.deleteByBoxId(boxId);
        
        // 设置boxId和默认值
        for (GbBoxFieldMapping mapping : mappings) {
            mapping.setBoxId(boxId);
            
            // 设置 resourceType 默认值（如果前端没有传）
            if (StringUtils.isEmpty(mapping.getResourceType())) {
                mapping.setResourceType("game"); // 默认为 game 类型
            }
        }
        
        // 批量保存新配置
        int count = gbBoxFieldMappingService.batchInsertGbBoxFieldMapping(mappings);
        
        return success("保存成功，共" + count + "条配置");
    }

    /**
     * 导出字段映射配置列表
     */
    @PreAuthorize("@ss.hasPermi('gamebox:field-mapping:export')")
    @Log(title = "字段映射配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, GbBoxFieldMapping gbBoxFieldMapping)
    {
        List<GbBoxFieldMapping> list = gbBoxFieldMappingService.selectGbBoxFieldMappingList(gbBoxFieldMapping);
        ExcelUtil<GbBoxFieldMapping> util = new ExcelUtil<GbBoxFieldMapping>(GbBoxFieldMapping.class);
        util.exportExcel(response, list, "字段映射配置数据");
    }

    /**
     * 导出字段映射配置为JSON格式
     */
    @PreAuthorize("@ss.hasPermi('gamebox:field-mapping:export')")
    @Log(title = "字段映射配置", businessType = BusinessType.EXPORT)
    @GetMapping("/exportJson")
    public AjaxResult exportJson(String platform, String resourceType)
    {
        String jsonData = gbBoxFieldMappingService.exportFieldMappingsAsJson(platform, resourceType);
        return success(jsonData);
    }

    /**
     * 获取数据库表字段列表
     */
    @PreAuthorize("@ss.hasPermi('gamebox:field-mapping:query')")
    @GetMapping("/tableFields")
    public AjaxResult getTableFields()
    {
        // 使用 TableSchemaService 动态获取所有表字段
        Map<String, Object> tableFields = tableSchemaService.getAllTableFields();
        return success(tableFields);
    }
    
    /**
     * 获取字段schema定义（包含字段类型等元数据）
     */
    @PreAuthorize("@ss.hasPermi('gamebox:field-mapping:query')")
    @GetMapping("/fieldSchemas")
    public AjaxResult getFieldSchemas()
    {
        // 使用 TableSchemaService 获取字段schema
        Map<String, Object> fieldSchemas = tableSchemaService.getFieldSchemas();
        return success(fieldSchemas);
    }
    
    private Map<String, String> createField(String value, String label, String comment, String type) {
        Map<String, String> field = new HashMap<>();
        field.put("value", value);
        field.put("label", label);
        field.put("comment", comment);
        field.put("type", type);
        return field;
    }

    /**
     * 获取字段映射配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('gamebox:field-mapping:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(gbBoxFieldMappingService.selectGbBoxFieldMappingById(id));
    }

    /**
     * 新增字段映射配置
     */
    @PreAuthorize("@ss.hasPermi('gamebox:field-mapping:add')")
    @Log(title = "字段映射配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GbBoxFieldMapping gbBoxFieldMapping)
    {
        // 验证目标字段是否为系统关键字段（仅对主表字段检查，JSON字段不限制）
        String targetField = gbBoxFieldMapping.getTargetField();
        String targetLocation = gbBoxFieldMapping.getTargetLocation();
        // 只对主表、分类关联表、关系表的字段进行限制，JSON字段不限制
        boolean isMainTable = "main".equals(targetLocation) || "category_relation".equals(targetLocation) || "relation".equals(targetLocation);
        if (isMainTable && targetField != null && EXCLUDED_FIELDS.contains(targetField.toLowerCase())) {
            return error("字段 [" + targetField + "] 是系统关键字段，不允许映射");
        }
        
        return toAjax(gbBoxFieldMappingService.insertGbBoxFieldMapping(gbBoxFieldMapping));
    }

    /**
     * 修改字段映射配置
     */
    @PreAuthorize("@ss.hasPermi('gamebox:field-mapping:edit')")
    @Log(title = "字段映射配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GbBoxFieldMapping gbBoxFieldMapping)
    {
        // 验证目标字段是否为系统关键字段（仅对主表字段检查，JSON字段不限制）
        String targetField = gbBoxFieldMapping.getTargetField();
        String targetLocation = gbBoxFieldMapping.getTargetLocation();
        // 只对主表、分类关联表、关系表的字段进行限制，JSON字段不限制
        boolean isMainTable = "main".equals(targetLocation) || "category_relation".equals(targetLocation) || "relation".equals(targetLocation);
        if (isMainTable && targetField != null && EXCLUDED_FIELDS.contains(targetField.toLowerCase())) {
            return error("字段 [" + targetField + "] 是系统关键字段，不允许映射");
        }
        
        return toAjax(gbBoxFieldMappingService.updateGbBoxFieldMapping(gbBoxFieldMapping));
    }

    /**
     * 删除字段映射配置
     */
    @PreAuthorize("@ss.hasPermi('gamebox:field-mapping:remove')")
    @Log(title = "字段映射配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(gbBoxFieldMappingService.deleteGbBoxFieldMappingByIds(ids));
    }

    /**
     * 批量保存或更新字段映射配置（增量更新，不删除旧数据）
     * 根据ID判断是新增还是更新
     */
    @PreAuthorize("@ss.hasPermi('gamebox:field-mapping:edit')")
    @Log(title = "字段映射配置", businessType = BusinessType.UPDATE)
    @PostMapping("/batchSaveOrUpdate")
    public AjaxResult batchSaveOrUpdate(@RequestBody List<GbBoxFieldMapping> mappings)
    {
        List<GbBoxFieldMapping> toInsert = new ArrayList<>();
        List<GbBoxFieldMapping> toUpdate = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        
        // 验证并分类数据
        for (GbBoxFieldMapping mapping : mappings) {
            try {
                // 验证目标字段是否为系统关键字段（仅对主表字段检查，JSON字段不限制）
                String targetField = mapping.getTargetField();
                String targetLocation = mapping.getTargetLocation();
                boolean isMainTable = "main".equals(targetLocation) || "category_relation".equals(targetLocation) || "relation".equals(targetLocation);
                if (isMainTable && targetField != null && EXCLUDED_FIELDS.contains(targetField.toLowerCase())) {
                    errors.add("字段 [" + targetField + "] 是系统关键字段，不允许映射");
                    continue;
                }
                
                // 根据ID判断是新增还是更新
                if (mapping.getId() != null && mapping.getId() > 0) {
                    toUpdate.add(mapping);
                } else {
                    toInsert.add(mapping);
                }
            } catch (Exception e) {
                errors.add("字段 " + mapping.getSourceField() + " 验证失败：" + e.getMessage());
            }
        }
        
        int successCount = 0;
        int updateCount = 0;
        
        // 批量插入
        if (!toInsert.isEmpty()) {
            try {
                successCount = gbBoxFieldMappingService.batchInsertGbBoxFieldMapping(toInsert);
            } catch (Exception e) {
                errors.add("批量新增失败：" + e.getMessage());
            }
        }
        
        // 批量更新
        if (!toUpdate.isEmpty()) {
            try {
                updateCount = gbBoxFieldMappingService.batchUpdateGbBoxFieldMapping(toUpdate);
            } catch (Exception e) {
                errors.add("批量更新失败：" + e.getMessage());
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successCount);
        result.put("updateCount", updateCount);
        result.put("failedCount", errors.size());
        result.put("errors", errors);
        
        return success(result);
    }

    /**
     * 批量导入字段映射配置
     */
    @PreAuthorize("@ss.hasPermi('gamebox:field-mapping:import')")
    @Log(title = "字段映射配置", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(@RequestBody List<GbBoxFieldMapping> mappingList, boolean updateSupport)
    {
        String message = gbBoxFieldMappingService.importFieldMappings(mappingList, updateSupport);
        return success(message);
    }

    /**
     * 获取指定表字段的所有不同值
     * 用于值映射配置时的下拉选项
     */
    @GetMapping("/fieldValues")
    public AjaxResult getFieldDistinctValues(String tableName, String fieldName)
    {
        List<String> values = gbBoxFieldMappingService.getFieldDistinctValues(tableName, fieldName);
        return success(values);
    }
}

