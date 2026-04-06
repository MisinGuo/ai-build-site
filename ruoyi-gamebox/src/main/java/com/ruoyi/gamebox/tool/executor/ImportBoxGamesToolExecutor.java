package com.ruoyi.gamebox.tool.executor;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.gamebox.service.IGbGameImportService;
import com.ruoyi.gamebox.tool.SystemTool;
import com.ruoyi.gamebox.tool.ToolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 导入游戏到盒子（JSON 解析版）工具执行器
 *
 * <p>接收一段 JSON 字符串（通常是 {@code fetch_git_file} 的输出 {@code content}），
 * 解析出游戏数据列表，通过已配置的字段映射规则转换为系统格式，
 * 然后批量写入目标盒子。</p>
 *
 * <p>典型工作流：fetch_git_file → import_box_games</p>
 *
 * @author ruoyi
 * @date 2026-03-05
 */
@Component
@SystemTool(
    code = "import_box_games",
    name = "导入游戏到盒子（JSON）",
    description = "解析 JSON 字符串，按字段映射规则将游戏数据批量导入到指定游戏盒子中。"
        + "支持指定 JSON 内数据路径（如 'data' 或 'result.games'），支持跳过或覆盖已存在的游戏。",
    inputs = "["
        + "{\"name\":\"jsonData\",\"type\":\"textarea\",\"label\":\"JSON数据\",\"required\":true,"
        + "\"description\":\"包含游戏数据的 JSON 字符串，可以是数组或包含数组的对象\"},"
        + "{\"name\":\"boxId\",\"type\":\"number\",\"label\":\"目标盒子ID\",\"required\":true,"
        + "\"description\":\"要将游戏导入到哪个游戏盒子（用于关联字段映射配置）\"},"
        + "{\"name\":\"siteId\",\"type\":\"number\",\"label\":\"目标网站ID\",\"required\":true,"
        + "\"description\":\"游戏所属网站 ID，导入的游戏将归属于该网站\"},"
        + "{\"name\":\"dataPath\",\"type\":\"text\",\"label\":\"数据路径\",\"required\":false,"
        + "\"description\":\"JSON 中游戏数组的路径，支持点分语法，如 'data'、'result.games'；为空时自动探测根节点\"},"
        + "{\"name\":\"updateExisting\",\"type\":\"text\",\"label\":\"更新已有游戏\",\"required\":false,"
        + "\"description\":\"是否更新同名已存在的游戏：true=更新，false=跳过（默认 false）\"}"
        + "]",
    outputs = "["
        + "{\"name\":\"totalCount\",\"type\":\"number\",\"label\":\"总数\",\"description\":\"JSON 中的总游戏数\"},"
        + "{\"name\":\"successCount\",\"type\":\"number\",\"label\":\"成功数\",\"description\":\"成功新增或更新的游戏数\"},"
        + "{\"name\":\"newCount\",\"type\":\"number\",\"label\":\"新增数\",\"description\":\"成功新增的游戏数\"},"
        + "{\"name\":\"updatedCount\",\"type\":\"number\",\"label\":\"更新数\",\"description\":\"成功更新的游戏数\"},"
        + "{\"name\":\"skippedCount\",\"type\":\"number\",\"label\":\"跳过数\",\"description\":\"因已存在而跳过的游戏数\"},"
        + "{\"name\":\"failureCount\",\"type\":\"number\",\"label\":\"失败数\",\"description\":\"处理失败的游戏数\"},"
        + "{\"name\":\"message\",\"type\":\"text\",\"label\":\"结果消息\",\"description\":\"导入结果汇总\"},"
        + "{\"name\":\"success\",\"type\":\"boolean\",\"label\":\"是否成功\",\"description\":\"是否成功完成（false 表示整批失败）\"}"
        + "]",
    order = 160
)
public class ImportBoxGamesToolExecutor implements ToolExecutor {

    private static final Logger log = LoggerFactory.getLogger(ImportBoxGamesToolExecutor.class);

    @Autowired
    private IGbGameImportService gbGameImportService;

    @Override
    public String getType() {
        return "import_box_games";
    }

    @Override
    public Map<String, Object> execute(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<>();

        // ── 参数读取 ──────────────────────────────────────────────────────────
        String jsonData       = getStringParam(params, "jsonData");
        Long   boxId          = getLongParam(params, "boxId");
        Long   siteId         = getLongParam(params, "siteId");
        String dataPath       = getStringParam(params, "dataPath");
        boolean updateExisting = parseBooleanParam(params, "updateExisting", false);

        // ── 参数校验 ──────────────────────────────────────────────────────────
        if (jsonData == null || jsonData.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "JSON 数据不能为空");
            return result;
        }
        if (boxId == null) {
            result.put("success", false);
            result.put("message", "目标盒子 ID 不能为空");
            return result;
        }
        if (siteId == null) {
            result.put("success", false);
            result.put("message", "目标网站 ID 不能为空");
            return result;
        }

        log.info("[ImportBoxGames] 开始解析 JSON，boxId={}, siteId={}, dataPath={}, updateExisting={}",
            boxId, siteId, dataPath, updateExisting);

        // ── JSON 解析 ─────────────────────────────────────────────────────────
        List<Map<String, Object>> gameDataList;
        try {
            gameDataList = extractGameList(jsonData.trim(), dataPath);
        } catch (Exception e) {
            log.error("[ImportBoxGames] JSON 解析失败", e);
            result.put("success", false);
            result.put("message", "JSON 解析失败: " + e.getMessage());
            return result;
        }

        if (gameDataList.isEmpty()) {
            result.put("success", false);
            result.put("message", "未能从 JSON 中提取到任何游戏数据，请检查 dataPath 配置");
            result.put("totalCount", 0);
            return result;
        }

        log.info("[ImportBoxGames] 解析到 {} 条游戏数据，开始导入", gameDataList.size());

        // ── 调用导入服务 ────────────────────────────────────────────
        try {
            Map<String, Object> importResult =
                gbGameImportService.importGamesWithFieldMapping(gameDataList, boxId, siteId, updateExisting);

            result.putAll(importResult);
            result.put("success", true);
        } catch (Exception e) {
            log.error("[ImportBoxGames] 导入过程中出现异常", e);
            result.put("success", false);
            result.put("message", "导入失败: " + e.getMessage());
        }

        return result;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // JSON 提取：自动探测 + dataPath 支持
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * 从 JSON 字符串中提取游戏列表。
     *
     * <ul>
     *   <li>若 dataPath 不为空，按点分路径深入取值，取到的节点应为 JSON 数组</li>
     *   <li>若 dataPath 为空：
     *     <ul>
     *       <li>根节点为数组 → 直接作为游戏列表</li>
     *       <li>根节点为对象 → 尝试找第一个 JSON 数组类型的值</li>
     *     </ul>
     *   </li>
     * </ul>
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> extractGameList(String jsonData, String dataPath) {
        Object parsed;
        try {
            parsed = JSON.parse(jsonData);
        } catch (Exception e) {
            throw new IllegalArgumentException("JSON 格式无效: " + e.getMessage());
        }

        Object target = parsed;

        // 按 dataPath 导航（支持 "data"、"result.games" 等点分路径）
        if (dataPath != null && !dataPath.trim().isEmpty()) {
            String[] keys = dataPath.trim().split("\\.");
            for (String key : keys) {
                if (target instanceof JSONObject) {
                    target = ((JSONObject) target).get(key);
                } else if (target instanceof Map) {
                    target = ((Map<?, ?>) target).get(key);
                } else {
                    throw new IllegalArgumentException(
                        "沿路径 '" + dataPath + "' 导航时遇到非对象节点（key=" + key + "）");
                }
                if (target == null) {
                    throw new IllegalArgumentException(
                        "路径 '" + dataPath + "' 在 JSON 中不存在（key=" + key + "）");
                }
            }
        }

        // 最终节点应为数组
        if (target instanceof JSONArray) {
            return (List<Map<String, Object>>) (List<?>) ((JSONArray) target).toJavaList(Map.class);
        }
        if (target instanceof List) {
            return (List<Map<String, Object>>) target;
        }

        // 根节点是对象但没有指定 dataPath：自动找第一个数组字段
        if (target instanceof JSONObject || target instanceof Map) {
            Map<?, ?> obj = (Map<?, ?>) target;
            for (Object val : obj.values()) {
                if (val instanceof JSONArray) {
                    List<Map<String, Object>> list =
                        (List<Map<String, Object>>) (List<?>) ((JSONArray) val).toJavaList(Map.class);
                    if (!list.isEmpty() && list.get(0) instanceof Map) {
                        log.info("[ImportBoxGames] 自动探测到数据数组，大小={}", list.size());
                        return list;
                    }
                }
            }
            throw new IllegalArgumentException(
                "JSON 根节点为对象，但未找到任何 JSON 数组字段，请通过 dataPath 参数明确指定路径");
        }

        throw new IllegalArgumentException(
            "JSON 数据既不是数组也不是对象，无法解析为游戏列表");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 参数工具方法
    // ──────────────────────────────────────────────────────────────────────────

    private String getStringParam(Map<String, Object> params, String key) {
        Object val = params.get(key);
        return (val != null) ? val.toString().trim() : null;
    }

    private Long getLongParam(Map<String, Object> params, String key) {
        Object val = params.get(key);
        if (val == null) return null;
        try {
            if (val instanceof Number) return ((Number) val).longValue();
            return Long.parseLong(val.toString().trim());
        } catch (Exception e) {
            return null;
        }
    }

    private boolean parseBooleanParam(Map<String, Object> params, String key, boolean defaultValue) {
        Object val = params.get(key);
        if (val == null) return defaultValue;
        String s = val.toString().trim().toLowerCase();
        return "true".equals(s) || "1".equals(s) || "yes".equals(s);
    }
}
