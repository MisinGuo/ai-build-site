package com.ruoyi.gamebox.service.impl;

import java.util.*;
import java.util.stream.Collectors;
import java.util.Date;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.gamebox.domain.GbBoxFieldMapping;
import com.ruoyi.gamebox.service.IGbBoxFieldMappingService;
import com.ruoyi.gamebox.service.IFieldMappingApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 字段映射应用服务实现
 * 
 * @author ruoyi
 * @date 2026-01-25
 */
@Service
public class FieldMappingApplyServiceImpl implements IFieldMappingApplyService {
    
    private static final Logger log = LoggerFactory.getLogger(FieldMappingApplyServiceImpl.class);
    
    @Autowired
    private IGbBoxFieldMappingService fieldMappingService;
    
    /**
     * 应用字段映射，将源数据转换为系统标准格式
     * @param sourceData 源数据
     * @param boxId 盒子ID（如果为null则使用通用配置）
     * @param resourceType 资源类型
     */
    @Override
    public Map<String, Object> applyFieldMappings(Map<String, Object> sourceData, 
                                                   String platformCode, 
                                                   String resourceType) {
        // 注意：旧版API，platformCode 参数已废弃，如果传入则尝试解析为 boxId
        Long boxId = null;
        try {
            if (platformCode != null && platformCode.matches("\\d+")) {
                boxId = Long.parseLong(platformCode);
            }
        } catch (Exception e) {
            log.warn("无法将 platformCode 解析为 boxId: {}", platformCode);
        }
        
        // 查询该盒子的字段映射配置
        List<GbBoxFieldMapping> mappings;
        if (boxId != null) {
            mappings = fieldMappingService.selectByBoxId(boxId);
        } else {
            // 如果没有 boxId，查询通用配置（boxId 为 null 的记录）
            GbBoxFieldMapping query = new GbBoxFieldMapping();
            query.setResourceType(resourceType);
            query.setStatus("1");
            mappings = fieldMappingService.selectGbBoxFieldMappingList(query);
        }
        
        if (mappings == null || mappings.isEmpty()) {
            log.warn("未找到盒子 {} 资源类型 {} 的字段映射配置", boxId, resourceType);
            return Collections.emptyMap();
        }
        
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> mainFields = new HashMap<>();
        Map<String, Object> extensionFields = new HashMap<>();
        Map<String, Object> promotionLinks = new HashMap<>();
        Map<String, Object> relationFields = new HashMap<>(); // 关联表字段
        Map<String, Object> categoryRelationFields = new HashMap<>(); // 分类关联字段
        
        // 应用每个字段映射
        for (GbBoxFieldMapping mapping : mappings) {
            String sourceField = mapping.getSourceField();
            String targetField = mapping.getTargetField();
            String fieldLocation = mapping.getTargetLocation();
            String defaultValue = mapping.getDefaultValue();
            
            // 获取源数据值（支持多字段合并：sourceField 为 JSON 数组格式 ["f1","f2",...]）
            Object sourceValue;
            if (sourceField != null && sourceField.trim().startsWith("[")) {
                sourceValue = collectMultipleSourceFields(sourceData, sourceField.trim());
            } else {
                sourceValue = getNestedValue(sourceData, sourceField);
            }
            
            // 如果源数据为空且有默认值，使用默认值
            if (sourceValue == null && defaultValue != null && !defaultValue.isEmpty()) {
                sourceValue = defaultValue;
            }
            
            // 跳过空值（除非是必填字段）
            if (sourceValue == null) {
                if ("1".equals(mapping.getIsRequired())) {
                    log.warn("必填字段 {} 的值为空", sourceField);
                }
                continue;
            }
            
            Object transformedValue = applyMappingPipeline(sourceValue, mapping);
            
            // 根据字段位置分配到不同的映射
            if ("main".equals(fieldLocation)) {
                mainFields.put(targetField, transformedValue);
            } else if ("promotion_link".equals(fieldLocation)) {
                promotionLinks.put(targetField, transformedValue);
            } else if ("platform_data".equals(fieldLocation) || "ext".equals(fieldLocation)) {
                extensionFields.put(targetField, transformedValue);
            } else if ("relation".equals(fieldLocation)) {
                relationFields.put(targetField, transformedValue);
            } else if ("category_relation".equals(fieldLocation)) {
                categoryRelationFields.put(targetField, transformedValue);
            } else {
                extensionFields.put(targetField, transformedValue);
            }
        }
        
        // 组装结果
        result.put("mainFields", mainFields);
        result.put("extensionFields", extensionFields);
        result.put("promotionLinks", promotionLinks);
        result.put("relationFields", relationFields);
        result.put("categoryRelationFields", categoryRelationFields);
        result.put("platformSource", platformCode);
        
        return result;
    }
    
    /**
     * 批量应用字段映射
     */
    @Override
    public List<Map<String, Object>> batchApplyFieldMappings(List<Map<String, Object>> sourceDataList,
                                                               String platformCode,
                                                               String resourceType) {
        return sourceDataList.stream()
                .map(data -> applyFieldMappings(data, platformCode, resourceType))
                .collect(Collectors.toList());
    }
    
    /**
     * 获取支持的平台列表
     */
    @Override
    public List<String> getSupportedPlatforms() {
        return Arrays.asList("u2game", "milu", "277sy", "dongyouxi");
    }
    
    /**
     * 验证平台数据格式
     */
    @Override
    public String validatePlatformData(Map<String, Object> sourceData, 
                                        String platformCode, 
                                        String resourceType) {
        // 尝试解析 boxId
        Long boxId = null;
        try {
            if (platformCode != null && platformCode.matches("\\d+")) {
                boxId = Long.parseLong(platformCode);
            }
        } catch (Exception e) {
            // 忽略
        }
        
        // 查询必填字段映射
        List<GbBoxFieldMapping> requiredMappings;
        if (boxId != null) {
            List<GbBoxFieldMapping> all = fieldMappingService.selectByBoxId(boxId);
            requiredMappings = all.stream()
                .filter(m -> "1".equals(m.getIsRequired()) && "1".equals(m.getStatus()))
                .collect(Collectors.toList());
        } else {
            GbBoxFieldMapping query = new GbBoxFieldMapping();
            query.setResourceType(resourceType);
            query.setIsRequired("1");
            query.setStatus("1");
            requiredMappings = fieldMappingService.selectGbBoxFieldMappingList(query);
        }
        
        // 验证必填字段
        List<String> missingFields = new ArrayList<>();
        for (GbBoxFieldMapping mapping : requiredMappings) {
            Object value = getNestedValue(sourceData, mapping.getSourceField());
            if (value == null || (value instanceof String && ((String) value).isEmpty())) {
                missingFields.add(mapping.getSourceField());
            }
        }
        
        if (!missingFields.isEmpty()) {
            return "缺少必填字段: " + String.join(", ", missingFields);
        }
        
        return null;
    }
    
    /**
     * 应用值映射规则
     * 将源数据值按照配置的映射规则转换为目标值
     * 
     * @param sourceValue 源数据值
     * @param mapping 字段映射配置
     * @return 转换后的值
     */
    private Object applyValueMapping(Object sourceValue, GbBoxFieldMapping mapping) {
        if (sourceValue == null) {
            return null;
        }
        
        String valueMappingJson = mapping.getValueMapping();
        if (valueMappingJson == null || valueMappingJson.isEmpty()) {
            return sourceValue; // 没有配置值映射，直接返回原值
        }
        
        try {
            JSONObject valueMappingConfig = JSON.parseObject(valueMappingJson);
            String mappingType = valueMappingConfig.getString("type");
            
            if (mappingType == null) {
                log.warn("值映射配置缺少type字段: {}", valueMappingJson);
                return sourceValue;
            }
            
            // 1. 直接映射：key-value对应
            if ("direct".equals(mappingType)) {
                JSONObject mappings = valueMappingConfig.getJSONObject("mappings");
                if (mappings == null || mappings.isEmpty()) {
                    log.warn("直接映射配置为空");
                    return sourceValue;
                }
                
                String sourceStr = String.valueOf(sourceValue);
                
                // 查找映射值
                if (mappings.containsKey(sourceStr)) {
                    Object mappedValue = mappings.get(sourceStr);
                    
                    // 如果映射值是对象格式 {value: "1", description: "卡牌类游戏"}
                    if (mappedValue instanceof JSONObject) {
                        JSONObject mappedObj = (JSONObject) mappedValue;
                        Object targetValue = mappedObj.get("value");
                        String description = mappedObj.getString("description");
                        log.debug("应用直接映射: {} → {} ({})", sourceStr, targetValue, description);
                        return targetValue;
                    }
                    
                    // 简单值格式
                    log.debug("应用直接映射: {} → {}", sourceStr, mappedValue);
                    return mappedValue;
                }
                
                // 未找到映射，返回原值
                log.warn("未找到值映射: 源值={}, 字段={}", sourceStr, mapping.getSourceField());
                return sourceValue;
            }
            
            // 2. 正则替换：使用正则表达式替换
            else if ("regex".equals(mappingType)) {
                String pattern = valueMappingConfig.getString("pattern");
                String replacement = valueMappingConfig.getString("replacement");
                
                if (pattern == null || pattern.isEmpty()) {
                    log.warn("正则替换配置缺少pattern");
                    return sourceValue;
                }
                
                String sourceStr = String.valueOf(sourceValue);
                String result = sourceStr.replaceAll(pattern, replacement != null ? replacement : "");
                log.debug("应用正则替换: {} → {} (pattern: {})", sourceStr, result, pattern);
                return result;
            }
            
            // 3. 函数转换：执行JavaScript函数（暂不实现，安全风险）
            else if ("function".equals(mappingType)) {
                log.warn("函数转换暂不支持，出于安全考虑");
                return sourceValue;
            }
            
            else {
                log.warn("未知的映射类型: {}", mappingType);
                return sourceValue;
            }
            
        } catch (Exception e) {
            log.error("应用值映射失败: sourceValue={}, mapping={}", sourceValue, valueMappingJson, e);
            return sourceValue;
        }
    }

    /**
     * 获取嵌套字段的值（支持 a.b.c 格式和数组操作）
     * 支持格式：
     * - a.b.c: 普通嵌套字段
     * - a[0].b: 数组索引访问
     * - a[].b: 提取数组所有元素的b字段，返回List
     */
    private Object getNestedValue(Map<String, Object> data, String fieldPath) {
        if (fieldPath == null || fieldPath.isEmpty()) {
            return null;
        }
        
        String[] parts = fieldPath.split("\\.");
        Object current = data;
        
        for (String part : parts) {
            if (current == null) {
                return null;
            }
            
            // 处理数组操作 a[0] 或 a[]
            if (part.contains("[")) {
                String fieldName = part.substring(0, part.indexOf("["));
                String indexPart = part.substring(part.indexOf("[") + 1, part.indexOf("]"));
                
                // 获取数组字段
                if (current instanceof Map) {
                    current = ((Map<?, ?>) current).get(fieldName);
                } else {
                    return null;
                }
                
                if (!(current instanceof List)) {
                    log.warn("字段 {} 不是数组类型", fieldName);
                    return null;
                }
                
                List<?> list = (List<?>) current;
                
                // a[] - 提取所有元素
                if (indexPart.isEmpty()) {
                    // 如果是最后一个part，直接返回整个列表
                    if (part.equals(parts[parts.length - 1])) {
                        return list;
                    }
                    // 否则返回列表，让后续处理继续遍历
                    current = list;
                } 
                // a[0] - 提取指定索引
                else {
                    try {
                        int index = Integer.parseInt(indexPart);
                        if (index >= 0 && index < list.size()) {
                            current = list.get(index);
                        } else {
                            log.warn("数组索引越界: index={}, size={}", index, list.size());
                            return null;
                        }
                    } catch (NumberFormatException e) {
                        log.warn("无效的数组索引: {}", indexPart);
                        return null;
                    }
                }
            } 
            // 处理普通字段
            else if (current instanceof Map) {
                current = ((Map<?, ?>) current).get(part);
            } 
            // 处理数组元素的字段提取 (photo[].url 的后续处理)
            else if (current instanceof List) {
                List<?> list = (List<?>) current;
                List<Object> results = new java.util.ArrayList<>();
                for (Object item : list) {
                    if (item instanceof Map) {
                        Object value = ((Map<?, ?>) item).get(part);
                        if (value != null) {
                            results.add(value);
                        }
                    }
                }
                return results.isEmpty() ? null : results;
            } 
            else {
                return null;
            }
        }
        
        return current;
    }
    
    /**
     * 转换数据值
     */
    private Object transformValue(Object value, String dataType, String transformRule) {
        if (value == null) {
            return null;
        }
        
        try {
            // 先应用转换规则
            if (transformRule != null && !transformRule.isEmpty()) {
                value = applyTransformRule(value, transformRule);
            }
            
            // 处理数据类型转换
            if ("string".equals(dataType)) {
                // 如果值是List，转换为逗号分隔字符串
                if (value instanceof List) {
                    return ((List<?>) value).stream()
                        .map(Object::toString)
                        .collect(java.util.stream.Collectors.joining(","));
                }
                return value.toString();
            }
            
            // 处理整数类型
            if ("integer".equals(dataType) || "int".equals(dataType)) {
                if (value instanceof Number) {
                    return ((Number) value).intValue();
                } else if (value instanceof String) {
                    try {
                        return Integer.parseInt((String) value);
                    } catch (NumberFormatException e) {
                        log.warn("无法转换为整数: {}", value);
                        return null;
                    }
                }
            }
            
            // 处理小数类型
            if ("decimal".equals(dataType) || "number".equals(dataType)) {
                if (value instanceof Number) {
                    return value;
                } else if (value instanceof String) {
                    try {
                        return Double.parseDouble((String) value);
                    } catch (NumberFormatException e) {
                        log.warn("无法转换为数字: {}", value);
                        return null;
                    }
                }
            }
            
            // 处理日期时间类型
            if ("datetime".equals(dataType) || "date".equals(dataType)) {
                if (value instanceof Date) {
                    return value;
                } else if (value instanceof String) {
                    try {
                        String dateStr = (String) value;
                        // 优先检查是否为纯数字（Unix 时间戳）
                        try {
                            long timestamp = Long.parseLong(dateStr.trim());
                            // 10位以内视为秒，否则视为毫秒
                            return new Date(timestamp < 10_000_000_000L ? timestamp * 1000L : timestamp);
                        } catch (NumberFormatException ignored) {
                            // 不是数字，继续尝试日期格式
                        }
                        // 尝试多种日期格式
                        java.text.SimpleDateFormat[] formats = {
                            new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss"),
                            new java.text.SimpleDateFormat("yyyy-MM-dd"),
                            new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss"),
                            new java.text.SimpleDateFormat("yyyy/MM/dd")
                        };
                        for (java.text.SimpleDateFormat format : formats) {
                            try {
                                return format.parse(dateStr);
                            } catch (Exception e) {
                                // 继续尝试下一个格式
                            }
                        }
                        log.warn("无法解析日期: {}", value);
                        return null;
                    } catch (Exception e) {
                        log.warn("日期转换失败: {}", value, e);
                        return null;
                    }
                } else if (value instanceof Number) {
                    // 时间戳
                    return new Date(((Number) value).longValue());
                }
            }
            
            // 处理JSON类型
            if ("json".equals(dataType)) {
                if (value instanceof String) {
                    // 验证是否为有效JSON
                    String strValue = (String) value;
                    if (strValue.isEmpty() || strValue.trim().isEmpty()) {
                        log.debug("JSON字段值为空字符串，返回null");
                        return null;
                    }
                    return strValue;
                } else if (value instanceof List) {
                    List<?> list = (List<?>) value;
                    // 空列表返回 null 而不是 "[]"
                    if (list.isEmpty()) {
                        log.debug("JSON字段值为空列表，返回null");
                        return null;
                    }
                    String jsonStr = JSON.toJSONString(list);
                    log.debug("List转JSON: {}", jsonStr);
                    return jsonStr;
                } else {
                    String jsonStr = JSON.toJSONString(value);
                    log.debug("对象转JSON: {}", jsonStr);
                    return jsonStr;
                }
            }
            
            // 处理数组类型
            if ("array".equals(dataType)) {
                if (value instanceof List || value instanceof JSONArray) {
                    List<?> list = value instanceof List ? (List<?>) value : ((JSONArray) value).toJavaList(Object.class);
                    // 空列表返回 null
                    if (list.isEmpty()) {
                        log.debug("Array字段值为空列表，返回null");
                        return null;
                    }
                    // 转换为JSON字符串
                    String jsonStr = JSON.toJSONString(list);
                    log.debug("Array转JSON: {}", jsonStr);
                    return jsonStr;
                } else if (value instanceof String) {
                    String strValue = (String) value;
                    if (strValue.isEmpty() || strValue.trim().isEmpty()) {
                        log.debug("Array字段值为空字符串，返回null");
                        return null;
                    }
                    return strValue;
                }
            }
            
            // 处理布尔类型
            if ("boolean".equals(dataType)) {
                if (value instanceof Boolean) {
                    return ((Boolean) value) ? "1" : "0";
                } else if (value instanceof String) {
                    String str = ((String) value).toLowerCase();
                    return ("true".equals(str) || "1".equals(str)) ? "1" : "0";
                } else if (value instanceof Number) {
                    return ((Number) value).intValue() != 0 ? "1" : "0";
                }
            }
            
            // 默认返回字符串
            return value.toString();
            
        } catch (Exception e) {
            log.error("转换字段值失败: value={}, dataType={}, transformRule={}", value, dataType, transformRule, e);
            return value;
        }
    }
    
    /**
     * 应用完整的映射流水线：值映射 + 数据类型转换。
     * <p>
     * 若源值已是 List，直接对每个元素执行值映射，返回 List。
     * regex_all:PATTERN — 用正则找出所有匹配项，逐项值映射，返回 List。
     * 其他规则走原流程：先值映射，再 transform + 类型转换。
     */
    private Object applyMappingPipeline(Object sourceValue, GbBoxFieldMapping mapping) {
        String dataType = mapping.getFieldType();
        String transformRule = mapping.getTransformExpression();

        // 如果源值本身已经是 List：
        // - 有值映射（value_mapping）→ 逐项映射，返回 List（用于 category_relation 等）
        // - 无值映射 → 整体交给 transformValue 处理（json/array 类型会序列化为 JSON 字符串）
        if (sourceValue instanceof List) {
            String valueMappingJson = mapping.getValueMapping();
            if (valueMappingJson == null || valueMappingJson.isEmpty()) {
                return transformValue(sourceValue, dataType, transformRule);
            }
            List<?> sourceList = (List<?>) sourceValue;
            List<Object> results = new ArrayList<>();
            for (Object item : sourceList) {
                if (item == null) continue;
                Object mapped = applySingleDirectMapping(String.valueOf(item).trim(), mapping);
                if (mapped == null) continue;
                Object converted = convertValueToType(mapped, dataType);
                if (converted != null) results.add(converted);
            }
            return results.isEmpty() ? null : results;
        }

        // regex_all:PATTERN — 找出所有匹配项，逐项值映射，返回 List
        if (transformRule != null && transformRule.startsWith("regex_all:")) {
            String pattern = transformRule.substring("regex_all:".length());
            if (pattern.isEmpty()) return null;
            String sourceStr = String.valueOf(sourceValue).trim();
            if (sourceStr.isEmpty()) return null;
            java.util.regex.Matcher m = java.util.regex.Pattern.compile(pattern).matcher(sourceStr);
            List<Object> results = new ArrayList<>();
            while (m.find()) {
                String part = m.groupCount() > 0 ? m.group(1) : m.group(0);
                if (part == null || part.trim().isEmpty()) continue;
                Object mapped = applySingleDirectMapping(part.trim(), mapping);
                if (mapped == null) continue;
                Object converted = convertValueToType(mapped, dataType);
                if (converted != null) results.add(converted);
            }
            return results.isEmpty() ? null : results;
        }

        Object mappedValue = applyValueMapping(sourceValue, mapping);
        return transformValue(mappedValue, dataType, transformRule);
    }

    /**
     * 对单个字符串值做直接值映射（仅 direct 类型）。
     * 用于 List 元素、regex_all 各子项的逐项映射。未找到映射时返回 null（跳过该项）。
     */
    private Object applySingleDirectMapping(String value, GbBoxFieldMapping mapping) {
        String valueMappingJson = mapping.getValueMapping();
        if (valueMappingJson == null || valueMappingJson.isEmpty()) return value;
        try {
            JSONObject config = JSON.parseObject(valueMappingJson);
            if (!"direct".equals(config.getString("type"))) return value;
            JSONObject mappings = config.getJSONObject("mappings");
            if (mappings == null || !mappings.containsKey(value)) {
                log.debug("未找到值映射: 源值={}, 字段={}", value, mapping.getSourceField());
                return null;
            }
            Object mapped = mappings.get(value);
            if (mapped instanceof JSONObject) {
                return ((JSONObject) mapped).get("value");
            }
            return mapped;
        } catch (Exception e) {
            log.error("值映射失败: value={}", value, e);
            return null;
        }
    }

    /**
     * 将值转换为指定数据类型（纯类型转换，不应用任何 transform rule）。
     */
    private Object convertValueToType(Object value, String dataType) {
        if (value == null) return null;
        if ("integer".equals(dataType) || "int".equals(dataType)) {
            if (value instanceof Number) return ((Number) value).intValue();
            try { return Integer.parseInt(String.valueOf(value)); } catch (Exception e) { return null; }
        }
        if ("decimal".equals(dataType) || "number".equals(dataType)) {
            if (value instanceof Number) return value;
            try { return Double.parseDouble(String.valueOf(value)); } catch (Exception e) { return null; }
        }
        return value;
    }

    /**
     * 应用转换规则
     */
    private Object applyTransformRule(Object value, String transformRule) {
        try {

            // join规则：提取数组元素的指定字段，返回List（不是字符串）
            // 格式：join:fieldName 或 join: (提取整个元素)
            if (transformRule.startsWith("join:")) {
                String fieldName = transformRule.substring(5).trim();
                List<String> result = new ArrayList<>();
                
                if (value instanceof List) {
                    List<?> list = (List<?>) value;
                    for (Object item : list) {
                        // 如果是Map对象，提取指定字段
                        if (item instanceof Map) {
                            Map<?, ?> map = (Map<?, ?>) item;
                            // 如果未指定字段名或字段名为空，尝试提取url字段
                            if (fieldName.isEmpty() && map.containsKey("url")) {
                                result.add(map.get("url").toString());
                            } else if (!fieldName.isEmpty() && map.containsKey(fieldName)) {
                                result.add(map.get(fieldName).toString());
                            }
                        } else {
                            result.add(item.toString());
                        }
                    }
                } else if (value instanceof JSONArray) {
                    JSONArray array = (JSONArray) value;
                    for (int i = 0; i < array.size(); i++) {
                        Object item = array.get(i);
                        if (item instanceof Map) {
                            Map<?, ?> map = (Map<?, ?>) item;
                            if (fieldName.isEmpty() && map.containsKey("url")) {
                                result.add(map.get("url").toString());
                            } else if (!fieldName.isEmpty() && map.containsKey(fieldName)) {
                                result.add(map.get(fieldName).toString());
                            }
                        } else {
                            result.add(item.toString());
                        }
                    }
                }
                
                // 返回List而不是字符串，让后续的convertValue处理JSON转换
                return result.isEmpty() ? null : result;
            }
            
            // regex:PATTERN 规则：使用正则表达式提取内容，返回第一个捕获组（如无捕获组则返回整个匹配）
            // 格式：regex:PATTERN，例如：regex:^([^（(]+)
            if (transformRule.startsWith("regex:")) {
                String pattern = transformRule.substring("regex:".length());
                if (!pattern.isEmpty()) {
                    String str = String.valueOf(value).trim();
                    try {
                        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile(pattern).matcher(str);
                        if (matcher.find()) {
                            String matched = matcher.groupCount() > 0 ? matcher.group(1) : matcher.group(0);
                            String result = matched != null ? matched.trim() : str;
                            log.debug("应用转换规则(regex): {} -> {} (pattern={})", str, result, pattern);
                            return result;
                        }
                    } catch (Exception e) {
                        log.warn("正则表达式无效或匹配失败: pattern={}, value={}", pattern, str);
                    }
                }
                return value;
            }

            // regex_replace:PATTERN=>REPLACEMENT 规则：正则替换
            // 格式：regex_replace:[（(][^）)]*[）)]=>
            if (transformRule.startsWith("regex_replace:")) {
                String expr = transformRule.substring("regex_replace:".length());
                int sep = expr.indexOf("=>");
                if (sep > -1) {
                    String pattern = expr.substring(0, sep);
                    String replacement = expr.substring(sep + 2);
                    String str = String.valueOf(value);
                    try {
                        String result = str.replaceAll(pattern, replacement);
                        log.debug("应用转换规则(regex_replace): {} -> {} (pattern={}, replacement={})", str, result, pattern, replacement);
                        return result;
                    } catch (Exception e) {
                        log.warn("regex_replace 规则无效: pattern={}, value={}", pattern, str);
                        return value;
                    }
                }
                log.warn("regex_replace 规则格式无效: {}", transformRule);
                return value;
            }

            // 其他转换规则可以在这里扩展
            
            return value;
        } catch (Exception e) {
            log.warn("应用转换规则失败: value={}, rule={}", value, transformRule, e);
            return value;
        }
    }
    
    /**
     * 基于盒子ID应用字段映射
     */
    @Override
    public Map<String, Object> applyBoxFieldMappings(Map<String, Object> sourceData, 
                                                      Long boxId, 
                                                      String resourceType) {
        // 查询该盒子的字段映射配置
        List<GbBoxFieldMapping> mappings = fieldMappingService.selectByBoxId(boxId);
        
        if (mappings == null || mappings.isEmpty()) {
            log.warn("未找到盒子 {} 资源类型 {} 的字段映射配置", boxId, resourceType);
            return Collections.emptyMap();
        }
        
        // 过滤出指定资源类型且启用的映射
        mappings = mappings.stream()
            .filter(m -> resourceType.equals(m.getResourceType()) && "1".equals(m.getStatus()))
            .collect(Collectors.toList());

        if (mappings.isEmpty()) {
            log.warn("盒子 {} 在资源类型 {} 下无可用映射（已按 resourceType/status 过滤）", boxId, resourceType);
            return Collections.emptyMap();
        }
        
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> mainFields = new HashMap<>();
        Map<String, Object> extensionFields = new HashMap<>();
        Map<String, Object> promotionLinks = new HashMap<>();
        Map<String, Object> relationFields = new HashMap<>(); // 关联表字段
        Map<String, Object> categoryRelationFields = new HashMap<>(); // 分类关联字段
        
        // 应用每个字段映射
        for (GbBoxFieldMapping mapping : mappings) {
            String sourceField = mapping.getSourceField();
            String targetField = mapping.getTargetField();
            String fieldLocation = mapping.getTargetLocation();
            String defaultValue = mapping.getDefaultValue();
            
            // 获取源数据值（支持多字段合并：sourceField 为 JSON 数组格式 ["f1","f2",...]）
            Object sourceValue;
            if (sourceField != null && sourceField.trim().startsWith("[")) {
                sourceValue = collectMultipleSourceFields(sourceData, sourceField.trim());
            } else {
                sourceValue = getNestedValue(sourceData, sourceField);
            }
            
            // 如果源数据为空且有默认值，使用默认值
            if (sourceValue == null && defaultValue != null && !defaultValue.isEmpty()) {
                sourceValue = defaultValue;
            }
            
            // 跳过空值（除非是必填字段）
            if (sourceValue == null) {
                if ("1".equals(mapping.getIsRequired())) {
                    log.warn("必填字段 {} 的值为空", sourceField);
                }
                continue;
            }
            
            Object transformedValue = applyMappingPipeline(sourceValue, mapping);
            
            // 根据字段位置分配到不同的映射
            if ("main".equals(fieldLocation)) {
                mainFields.put(targetField, transformedValue);
            } else if ("promotion_link".equals(fieldLocation)) {
                promotionLinks.put(targetField, transformedValue);
            } else if ("platform_data".equals(fieldLocation) || "ext".equals(fieldLocation)) {
                extensionFields.put(targetField, transformedValue);
            } else if ("relation".equals(fieldLocation)) {
                relationFields.put(targetField, transformedValue);
            } else if ("category_relation".equals(fieldLocation)) {
                categoryRelationFields.put(targetField, transformedValue);
            } else {
                extensionFields.put(targetField, transformedValue);
            }
        }
        
        // 组装结果
        result.put("mainFields", mainFields);
        result.put("extensionFields", extensionFields);
        result.put("promotionLinks", promotionLinks);
        result.put("relationFields", relationFields);
        result.put("categoryRelationFields", categoryRelationFields);
        result.put("boxId", boxId);
        
        return result;
    }
    
    /**
     * 批量应用盒子字段映射
     */
    @Override
    public List<Map<String, Object>> batchApplyBoxFieldMappings(List<Map<String, Object>> sourceDataList,
                                                                 Long boxId,
                                                                 String resourceType) {
        // 优化：只查询一次字段映射配置，避免在循环中重复查询数据库
        if (sourceDataList == null || sourceDataList.isEmpty()) {
            return Collections.emptyList();
        }
        
        // 一次性查询该盒子的字段映射配置
        List<GbBoxFieldMapping> mappings;
        if (boxId != null) {
            mappings = fieldMappingService.selectByBoxId(boxId);
        } else {
            GbBoxFieldMapping query = new GbBoxFieldMapping();
            query.setResourceType(resourceType);
            query.setStatus("1");
            mappings = fieldMappingService.selectGbBoxFieldMappingList(query);
        }
        
        if (mappings == null || mappings.isEmpty()) {
            log.warn("未找到盒子 {} 资源类型 {} 的字段映射配置", boxId, resourceType);
            return Collections.emptyList();
        }

        // 与单条映射保持一致：只保留指定资源类型且启用的映射
        final List<GbBoxFieldMapping> filteredMappings = mappings.stream()
            .filter(m -> resourceType.equals(m.getResourceType()) && "1".equals(m.getStatus()))
            .collect(Collectors.toList());

        if (filteredMappings.isEmpty()) {
            log.warn("盒子 {} 在资源类型 {} 下无可用映射（批量模式过滤后）", boxId, resourceType);
            return Collections.emptyList();
        }

        log.debug("批量映射配置加载完成: boxId={}, resourceType={}, 原始映射数={}, 过滤后映射数={}",
            boxId, resourceType, mappings.size(), filteredMappings.size());
        
        // 批量应用映射到所有数据
        return sourceDataList.stream()
            .map(sourceData -> applyBoxFieldMappingsWithCache(sourceData, filteredMappings, resourceType))
                .collect(Collectors.toList());
    }
    
    /**
     * 使用缓存的字段映射配置应用映射（避免重复查询数据库）
     */
    private Map<String, Object> applyBoxFieldMappingsWithCache(Map<String, Object> sourceData,
                                                                List<GbBoxFieldMapping> mappings,
                                                                String resourceType) {
        if (sourceData == null || sourceData.isEmpty()) {
            return Collections.emptyMap();
        }
        
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> mainFields = new HashMap<>();
        Map<String, Object> extensionFields = new HashMap<>();
        Map<String, Object> promotionLinks = new HashMap<>();
        Map<String, Object> relationFields = new HashMap<>(); // 关联表字段
        Map<String, Object> categoryRelationFields = new HashMap<>(); // 分类关联字段
        
        // 应用每个字段映射
        for (GbBoxFieldMapping mapping : mappings) {
            String sourceField = mapping.getSourceField();
            String targetField = mapping.getTargetField();
            String fieldLocation = mapping.getTargetLocation();
            String defaultValue = mapping.getDefaultValue();
            
            // 获取源数据值（支持多字段合并：sourceField 为 JSON 数组格式 ["f1","f2",...]）
            Object sourceValue;
            if (sourceField != null && sourceField.trim().startsWith("[")) {
                sourceValue = collectMultipleSourceFields(sourceData, sourceField.trim());
            } else {
                sourceValue = getNestedValue(sourceData, sourceField);
            }
            
            // 如果源数据为空且有默认值，使用默认值
            if (sourceValue == null && defaultValue != null && !defaultValue.isEmpty()) {
                sourceValue = defaultValue;
            }
            
            // 跳过空值（除非是必填字段）
            if (sourceValue == null) {
                if ("1".equals(mapping.getIsRequired())) {
                    log.warn("必填字段 {} 的值为空", sourceField);
                }
                continue;
            }
            
            Object transformedValue = applyMappingPipeline(sourceValue, mapping);
            
            // 根据字段位置分配到不同的映射
            if ("main".equals(fieldLocation)) {
                mainFields.put(targetField, transformedValue);
            } else if ("promotion_link".equals(fieldLocation)) {
                promotionLinks.put(targetField, transformedValue);
            } else if ("platform_data".equals(fieldLocation) || "ext".equals(fieldLocation)) {
                extensionFields.put(targetField, transformedValue);
            } else if ("relation".equals(fieldLocation)) {
                relationFields.put(targetField, transformedValue);
            } else if ("category_relation".equals(fieldLocation)) {
                categoryRelationFields.put(targetField, transformedValue);
            } else {
                extensionFields.put(targetField, transformedValue);
            }
        }
        
        // 组装结果
        result.put("mainFields", mainFields);
        result.put("extensionFields", extensionFields);
        result.put("promotionLinks", promotionLinks);
        result.put("relationFields", relationFields);
        result.put("categoryRelationFields", categoryRelationFields);
        
        return result;
    }
    
    /**
     * 多字段合并：从多个源字段收集值，合并为 List。
     * sourceFieldJson 格式为 JSON 数组，如 ["screenshot1","screenshot2","screenshot3"]
     * 每个字段的值（若不为 null）均加入结果列表；若字段值本身已是 List，则展开后加入。
     */
    private Object collectMultipleSourceFields(Map<String, Object> sourceData, String sourceFieldJson) {
        try {
            JSONArray fields = JSON.parseArray(sourceFieldJson);
            if (fields == null || fields.isEmpty()) {
                return null;
            }
            List<Object> collected = new ArrayList<>();
            for (int i = 0; i < fields.size(); i++) {
                String fieldPath = fields.getString(i);
                if (fieldPath == null || fieldPath.trim().isEmpty()) continue;
                Object value = getNestedValue(sourceData, fieldPath.trim());
                if (value == null) continue;
                if (value instanceof List) {
                    // 展开嵌套列表
                    collected.addAll((List<?>) value);
                } else {
                    collected.add(value);
                }
            }
            return collected.isEmpty() ? null : collected;
        } catch (Exception e) {
            log.warn("解析多源字段失败: sourceFieldJson={}", sourceFieldJson, e);
            return null;
        }
    }
}
