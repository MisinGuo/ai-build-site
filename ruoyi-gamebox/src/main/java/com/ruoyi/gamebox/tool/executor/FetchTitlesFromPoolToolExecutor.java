package com.ruoyi.gamebox.tool.executor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.gamebox.domain.GbArticleTitlePool;
import com.ruoyi.gamebox.mapper.GbArticleTitlePoolMapper;
import com.ruoyi.gamebox.tool.SystemTool;
import com.ruoyi.gamebox.tool.ToolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 标题池读取工具执行器
 * 用于从标题池中读取标题，支持按批次、状态、优先级筛选
 * 
 * @author ruoyi
 * @date 2026-01-05
 */
@Component
@SystemTool(
    code = "fetch_titles_from_pool",
    name = "标题池读取工具",
    description = "从标题池中读取标题，支持按批次号、使用状态、数量等条件筛选，可用于工作流中批量获取标题进行文章生成",
    inputs = "[{\"name\":\"batchCode\",\"type\":\"text\",\"label\":\"批次号\",\"required\":false,\"description\":\"标题池批次号，如SITE_1_20260101_001。如不指定则获取所有未使用的标题\"},{\"name\":\"usageStatus\",\"type\":\"select\",\"label\":\"使用状态\",\"required\":false,\"default\":\"0\",\"options\":[{\"label\":\"未使用\",\"value\":\"0\"},{\"label\":\"已使用\",\"value\":\"1\"},{\"label\":\"已废弃\",\"value\":\"2\"},{\"label\":\"全部\",\"value\":\"all\"}],\"description\":\"标题使用状态筛选\"},{\"name\":\"limit\",\"type\":\"number\",\"label\":\"获取数量\",\"required\":false,\"default\":10,\"description\":\"要获取的标题数量，默认10条\"},{\"name\":\"orderBy\",\"type\":\"select\",\"label\":\"排序方式\",\"required\":false,\"default\":\"priority_desc\",\"options\":[{\"label\":\"优先级降序\",\"value\":\"priority_desc\"},{\"label\":\"优先级升序\",\"value\":\"priority_asc\"},{\"label\":\"创建时间降序\",\"value\":\"create_time_desc\"},{\"label\":\"创建时间升序\",\"value\":\"create_time_asc\"}],\"description\":\"标题排序方式\"},{\"name\":\"markAsUsed\",\"type\":\"boolean\",\"label\":\"标记为已使用\",\"required\":false,\"default\":false,\"description\":\"获取后是否将标题标记为已使用状态\"}]",
    outputs = "[{\"name\":\"titles\",\"type\":\"array\",\"label\":\"标题列表\",\"description\":\"获取到的标题数组，每个元素包含id、title、keywords、referenceContent等字段\"},{\"name\":\"count\",\"type\":\"number\",\"label\":\"标题数量\",\"description\":\"实际获取到的标题数量\"},{\"name\":\"batchCode\",\"type\":\"text\",\"label\":\"批次号\",\"description\":\"标题所属的批次号\"},{\"name\":\"success\",\"type\":\"boolean\",\"label\":\"是否成功\",\"description\":\"操作是否成功\"},{\"name\":\"message\",\"type\":\"text\",\"label\":\"返回消息\",\"description\":\"操作结果消息\"}]",
    order = 101
)
public class FetchTitlesFromPoolToolExecutor implements ToolExecutor {
    
    private static final Logger log = LoggerFactory.getLogger(FetchTitlesFromPoolToolExecutor.class);
    
    @Autowired
    private GbArticleTitlePoolMapper titlePoolMapper;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Override
    public String getType() {
        return "fetch_titles_from_pool";
    }
    
    @Override
    public Map<String, Object> execute(Map<String, Object> params) throws Exception {
        log.info("执行标题池读取工具，参数: {}", params);
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 获取参数
            String batchCode = (String) params.get("batchCode");
            String usageStatus = (String) params.getOrDefault("usageStatus", "0");
            Integer limit = getIntParam(params, "limit", 10);
            String orderBy = (String) params.getOrDefault("orderBy", "priority_desc");
            Boolean markAsUsed = getBooleanParam(params, "markAsUsed", false);
            
            // 构建查询条件
            GbArticleTitlePool query = new GbArticleTitlePool();
            query.setDelFlag("0"); // 只查询未删除的
            
            if (batchCode != null && !batchCode.trim().isEmpty()) {
                query.setImportBatch(batchCode);
            }
            
            if (!"all".equals(usageStatus)) {
                query.setUsageStatus(usageStatus);
            }
            
            // 查询标题
            List<GbArticleTitlePool> titleList = titlePoolMapper.selectGbArticleTitlePoolList(query);
            
            // 排序
            titleList = sortTitles(titleList, orderBy);
            
            // 限制数量
            if (limit > 0 && titleList.size() > limit) {
                titleList = titleList.subList(0, limit);
            }
            
            // 如果需要标记为已使用
            if (markAsUsed && !titleList.isEmpty()) {
                for (GbArticleTitlePool title : titleList) {
                    title.setUsageStatus("1");
                    title.setUsedCount(title.getUsedCount() == null ? 1 : title.getUsedCount() + 1);
                    title.setUsedTime(new Date());
                    titlePoolMapper.updateGbArticleTitlePool(title);
                }
                log.info("已将{}条标题标记为已使用", titleList.size());
            }
            
            // 转换为简化格式
            List<Map<String, Object>> simplifiedTitles = titleList.stream().map(title -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", title.getId());
                map.put("title", title.getTitle());
                map.put("keywords", title.getKeywords());
                map.put("referenceContent", title.getReferenceContent());
                map.put("sourceName", title.getSourceName());
                map.put("sourceUrl", title.getSourceUrl());
                map.put("importBatch", title.getImportBatch());
                map.put("usageStatus", title.getUsageStatus());
                map.put("usedCount", title.getUsedCount());
                map.put("priority", title.getPriority());
                map.put("tags", title.getTags());
                return map;
            }).collect(Collectors.toList());
            
            // 设置返回结果
            result.put("success", true);
            result.put("message", "成功获取" + simplifiedTitles.size() + "条标题");
            result.put("titles", simplifiedTitles);
            result.put("count", simplifiedTitles.size());
            result.put("batchCode", batchCode != null ? batchCode : "all");
            
            log.info("标题池读取成功，获取{}条标题", simplifiedTitles.size());
            
        } catch (Exception e) {
            log.error("标题池读取失败", e);
            result.put("success", false);
            result.put("message", "读取失败: " + e.getMessage());
            result.put("titles", new ArrayList<>());
            result.put("count", 0);
        }
        
        return result;
    }
    
    /**
     * 排序标题列表
     */
    private List<GbArticleTitlePool> sortTitles(List<GbArticleTitlePool> titleList, String orderBy) {
        Comparator<GbArticleTitlePool> comparator;
        
        switch (orderBy) {
            case "priority_asc":
                comparator = Comparator.comparing(t -> t.getPriority() == null ? 0 : t.getPriority());
                break;
            case "create_time_desc":
                comparator = Comparator.comparing(GbArticleTitlePool::getCreateTime, 
                    Comparator.nullsLast(Comparator.reverseOrder()));
                break;
            case "create_time_asc":
                comparator = Comparator.comparing(GbArticleTitlePool::getCreateTime,
                    Comparator.nullsLast(Comparator.naturalOrder()));
                break;
            case "priority_desc":
            default:
                comparator = Comparator.comparing(t -> t.getPriority() == null ? 0 : t.getPriority(),
                    Comparator.reverseOrder());
                break;
        }
        
        return titleList.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取整数参数
     */
    private Integer getIntParam(Map<String, Object> params, String key, Integer defaultValue) {
        Object value = params.get(key);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * 获取布尔参数
     */
    private Boolean getBooleanParam(Map<String, Object> params, String key, Boolean defaultValue) {
        Object value = params.get(key);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return Boolean.parseBoolean(value.toString());
    }
}
