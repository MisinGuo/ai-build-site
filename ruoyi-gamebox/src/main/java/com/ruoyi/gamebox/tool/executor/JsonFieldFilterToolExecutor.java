package com.ruoyi.gamebox.tool.executor;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.gamebox.tool.SystemTool;
import com.ruoyi.gamebox.tool.ToolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * JSON 字段筛选工具执行器
 *
 * <p>从 JSON 字符串中提取符合条件的数据项，支持多种匹配模式：
 * 精确匹配、包含匹配（适配逗号/竖线/点号分隔字符串及数组）、前缀/后缀、正则。</p>
 *
 * <p>支持的数据格式示例：</p>
 * <ul>
 *   <li>u2game: {@code "gametype": "卡牌"}</li>
 *   <li>milu: {@code "game_genre": ["卡牌", "武侠"]}</li>
 *   <li>dongyouxi: {@code "游戏分类": "卡牌.三国"}</li>
 *   <li>277sy: {@code "genre_name": "挂机|传奇"}</li>
 * </ul>
 *
 * @author ruoyi
 * @date 2026-03-25
 */
@Component
@SystemTool(
    code = "json_field_filter",
    name = "JSON字段筛选器",
    description = "从输入的 JSON 字符串中，按照指定字段和匹配条件筛选数据项，输出符合条件的子集。"
        + "支持字符串精确匹配、包含匹配（兼容逗号/竖线/点号分隔格式及数组类型字段）、"
        + "前缀/后缀匹配和正则匹配。典型用途：从多平台游戏数据中提取特定分类的游戏。",
    inputs = "["
        + "{\"name\":\"jsonData\",\"type\":\"textarea\",\"label\":\"JSON数据\",\"required\":true,"
        + "\"description\":\"要筛选的 JSON 字符串，支持对象或数组格式\",\"rows\":8},"

        + "{\"name\":\"filterField\",\"type\":\"text\",\"label\":\"筛选字段\",\"required\":true,"
        + "\"description\":\"要筛选的字段名，支持点分嵌套路径，如 gametype 或 info.category\"},"

        + "{\"name\":\"filterValue\",\"type\":\"text\",\"label\":\"匹配值\",\"required\":true,"
        + "\"description\":\"需要匹配的值，如 卡牌\"},"

        + "{\"name\":\"matchMode\",\"type\":\"text\",\"label\":\"匹配模式\",\"required\":false,"
        + "\"description\":\"匹配方式：contains（包含，默认，兼容数组和分隔字符串）、equals（精确相等）、startsWith（前缀）、endsWith（后缀）、regex（正则）\"},"

        + "{\"name\":\"dataPath\",\"type\":\"text\",\"label\":\"数据路径\",\"required\":false,"
        + "\"description\":\"JSON 中数据数组的路径，支持点分语法如 items 或 result.data；为空时尝试根节点本身\"},"

        + "{\"name\":\"preserveWrapper\",\"type\":\"text\",\"label\":\"保留外层结构\",\"required\":false,"
        + "\"description\":\"是否保留 JSON 外层对象（如 platform、crawl_time 等元数据），并替换数组节点：true（默认）或 false（仅返回数组）\"},"

        + "{\"name\":\"caseSensitive\",\"type\":\"text\",\"label\":\"大小写敏感\",\"required\":false,"
        + "\"description\":\"匹配时是否区分大小写：true 或 false（默认 false，不区分）\"}"
        + "]",
    outputs = "["
        + "{\"name\":\"filteredData\",\"type\":\"textarea\",\"label\":\"筛选结果(JSON)\",\"description\":\"符合条件的数据，JSON 格式\"},"
        + "{\"name\":\"filteredCount\",\"type\":\"number\",\"label\":\"筛选数量\",\"description\":\"符合条件的数据项数量\"},"
        + "{\"name\":\"totalCount\",\"type\":\"number\",\"label\":\"总数量\",\"description\":\"原始数据中的总条目数\"},"
        + "{\"name\":\"message\",\"type\":\"text\",\"label\":\"结果消息\",\"description\":\"操作结果描述\"},"
        + "{\"name\":\"success\",\"type\":\"boolean\",\"label\":\"是否成功\",\"description\":\"是否成功完成筛选\"}"
        + "]",
    order = 170
)
public class JsonFieldFilterToolExecutor implements ToolExecutor {

    private static final Logger log = LoggerFactory.getLogger(JsonFieldFilterToolExecutor.class);

    @Override
    public String getType() {
        return "json_field_filter";
    }

    @Override
    public Map<String, Object> execute(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<>();

        // ── 读取参数 ──────────────────────────────────────────────────────────
        String jsonData       = getStringParam(params, "jsonData");
        String filterField    = getStringParam(params, "filterField");
        String filterValue    = getStringParam(params, "filterValue");
        String matchModeRaw   = getStringParam(params, "matchMode");
        String dataPath       = getStringParam(params, "dataPath");
        String preserveStr    = getStringParam(params, "preserveWrapper");
        String caseStr        = getStringParam(params, "caseSensitive");

        String matchMode      = (matchModeRaw == null || matchModeRaw.trim().isEmpty()) ? "contains" : matchModeRaw.trim().toLowerCase();
        boolean preserveWrap  = preserveStr == null || preserveStr.trim().isEmpty() || "true".equalsIgnoreCase(preserveStr.trim());
        boolean caseSensitive = "true".equalsIgnoreCase(caseStr == null ? "" : caseStr.trim());

        // ── 参数校验 ─────────────────────────────────────────────────────────
        if (jsonData == null || jsonData.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "JSON数据不能为空");
            return result;
        }
        if (filterField == null || filterField.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "筛选字段不能为空");
            return result;
        }
        if (filterValue == null || filterValue.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "匹配值不能为空");
            return result;
        }

        log.info("执行JSON字段筛选: field={}, value={}, mode={}, dataPath={}", filterField, filterValue, matchMode, dataPath);

        try {
            // ── 解析 JSON ────────────────────────────────────────────────────
            Object parsed = JSON.parse(jsonData.trim());

            JSONArray sourceArray;
            JSONObject wrapperObject = null; // 仅在 preserveWrap 时使用

            if (parsed instanceof JSONArray) {
                // 根节点就是数组
                sourceArray = (JSONArray) parsed;
            } else if (parsed instanceof JSONObject) {
                JSONObject root = (JSONObject) parsed;
                if (dataPath != null && !dataPath.trim().isEmpty()) {
                    // 沿 dataPath 取数组
                    Object node = getNestedValue(root, dataPath.trim());
                    if (!(node instanceof JSONArray)) {
                        result.put("success", false);
                        result.put("message", "按路径 '" + dataPath + "' 未找到数组节点，实际类型为: " + (node == null ? "null" : node.getClass().getSimpleName()));
                        return result;
                    }
                    sourceArray = (JSONArray) node;
                    if (preserveWrap) {
                        // 克隆一份外层对象，后面替换数组节点
                        wrapperObject = JSON.parseObject(root.toJSONString());
                    }
                } else {
                    // 自动探测：寻找第一个 JSONArray 类型的值
                    String foundKey = null;
                    JSONArray foundArray = null;
                    for (String key : root.keySet()) {
                        Object val = root.get(key);
                        if (val instanceof JSONArray) {
                            foundKey = key;
                            foundArray = (JSONArray) val;
                            break;
                        }
                    }
                    if (foundArray == null) {
                        // 回退：把整个对象当成单元素做匹配
                        sourceArray = new JSONArray();
                        sourceArray.add(root);
                    } else {
                        sourceArray = foundArray;
                        if (preserveWrap) {
                            wrapperObject = JSON.parseObject(root.toJSONString());
                            dataPath = foundKey; // 记录找到的 key 以便后面替换
                        }
                    }
                }
            } else {
                result.put("success", false);
                result.put("message", "无法解析 JSON，请确认格式正确");
                return result;
            }

            int totalCount = sourceArray.size();

            // ── 执行筛选 ──────────────────────────────────────────────────────
            JSONArray filtered = new JSONArray();
            String effectiveValue = caseSensitive ? filterValue : filterValue.toLowerCase();
            Pattern regexPattern = "regex".equals(matchMode)
                    ? Pattern.compile(filterValue, caseSensitive ? 0 : Pattern.CASE_INSENSITIVE)
                    : null;

            for (int i = 0; i < sourceArray.size(); i++) {
                Object item = sourceArray.get(i);
                if (!(item instanceof JSONObject)) {
                    continue;
                }
                JSONObject obj = (JSONObject) item;
                Object fieldVal = getNestedValue(obj, filterField);
                if (fieldMatches(fieldVal, effectiveValue, matchMode, caseSensitive, regexPattern)) {
                    filtered.add(obj);
                }
            }

            // ── 构造输出 ──────────────────────────────────────────────────────
            String filteredJson;
            if (preserveWrap && wrapperObject != null && dataPath != null && !dataPath.trim().isEmpty()) {
                // 替换数组节点，保留元数据
                setNestedValue(wrapperObject, dataPath.trim(), filtered);
                // 更新 total_count 如果存在此字段
                if (wrapperObject.containsKey("total_count")) {
                    wrapperObject.put("total_count", filtered.size());
                }
                filteredJson = wrapperObject.toJSONString();
            } else {
                filteredJson = filtered.toJSONString();
            }

            result.put("success", true);
            result.put("filteredData", filteredJson);
            result.put("filteredCount", filtered.size());
            result.put("totalCount", totalCount);
            result.put("message", String.format("筛选完成：共 %d 条数据，匹配 '%s' 的有 %d 条", totalCount, filterValue, filtered.size()));

            log.info("JSON字段筛选完成：total={}, filtered={}", totalCount, filtered.size());
            return result;

        } catch (Exception e) {
            log.error("JSON字段筛选执行出错", e);
            result.put("success", false);
            result.put("message", "筛选失败: " + e.getMessage());
            result.put("filteredCount", 0);
            result.put("totalCount", 0);
            return result;
        }
    }

    // ── 字段匹配逻辑 ─────────────────────────────────────────────────────────

    private boolean fieldMatches(Object fieldVal, String targetValue, String matchMode,
                                  boolean caseSensitive, Pattern regexPattern) {
        if (fieldVal == null) {
            return false;
        }

        if (fieldVal instanceof JSONArray) {
            // 数组类型：检查数组中是否有元素匹配
            JSONArray arr = (JSONArray) fieldVal;
            for (int i = 0; i < arr.size(); i++) {
                Object elem = arr.get(i);
                if (elem == null) continue;
                if (matchSingle(elem.toString(), targetValue, matchMode, caseSensitive, regexPattern)) {
                    return true;
                }
            }
            return false;
        }

        String strVal = fieldVal.toString();

        // 对于 "contains" 模式，同时处理分隔符分割的字符串（|, ,, .）
        if ("contains".equals(matchMode)) {
            // 先做整体包含判断
            String compareVal = caseSensitive ? strVal : strVal.toLowerCase();
            if (compareVal.contains(targetValue)) {
                return true;
            }
            // 再按各种分隔符拆分后逐项精确匹配
            String[] separators = {"\\|", ",", "\\."};
            for (String sep : separators) {
                if (strVal.contains(sep.replace("\\", ""))) {
                    String[] parts = strVal.split(sep);
                    for (String part : parts) {
                        String p = caseSensitive ? part.trim() : part.trim().toLowerCase();
                        if (p.equals(targetValue)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        return matchSingle(strVal, targetValue, matchMode, caseSensitive, regexPattern);
    }

    private boolean matchSingle(String strVal, String targetValue, String matchMode,
                                 boolean caseSensitive, Pattern regexPattern) {
        String compareVal = caseSensitive ? strVal : strVal.toLowerCase();
        switch (matchMode) {
            case "equals":
                return compareVal.equals(targetValue);
            case "startswith":
            case "startsWith":
                return compareVal.startsWith(targetValue);
            case "endswith":
            case "endsWith":
                return compareVal.endsWith(targetValue);
            case "regex":
                return regexPattern != null && regexPattern.matcher(strVal).find();
            case "contains":
            default:
                return compareVal.contains(targetValue);
        }
    }

    // ── 路径导航工具 ─────────────────────────────────────────────────────────

    /**
     * 按点分路径从 JSONObject 中取值
     */
    private Object getNestedValue(JSONObject obj, String path) {
        if (path == null || path.trim().isEmpty()) return obj;
        String[] parts = path.split("\\.");
        Object current = obj;
        for (String part : parts) {
            if (!(current instanceof JSONObject)) return null;
            current = ((JSONObject) current).get(part);
        }
        return current;
    }

    /**
     * 按点分路径在 JSONObject 中设置值（仅处理最后一层）
     */
    private void setNestedValue(JSONObject obj, String path, Object value) {
        String[] parts = path.split("\\.");
        JSONObject current = obj;
        for (int i = 0; i < parts.length - 1; i++) {
            Object next = current.get(parts[i]);
            if (!(next instanceof JSONObject)) return;
            current = (JSONObject) next;
        }
        current.put(parts[parts.length - 1], value);
    }

    // ── 参数提取工具 ─────────────────────────────────────────────────────────

    private String getStringParam(Map<String, Object> params, String key) {
        Object val = params.get(key);
        if (val == null) return null;
        String str = val.toString().trim();
        return str.isEmpty() ? null : str;
    }
}
