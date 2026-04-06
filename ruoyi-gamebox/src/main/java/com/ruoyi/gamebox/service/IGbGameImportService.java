package com.ruoyi.gamebox.service;

import java.util.List;
import java.util.Map;

/**
 * 游戏批量导入服务接口
 * 封装"通过字段映射将源数据导入到指定盒子"的核心逻辑，
 * 供控制器（GbGameController）和原子工具执行器（ImportBoxGamesToolExecutor）复用。
 *
 * 分类关联的冲突策略（merge/overwrite）从字段映射配置中读取，不再作为 API 参数传递。
 *
 * @author ruoyi
 * @date 2026-03-05
 */
public interface IGbGameImportService {

    /**
     * 通过字段映射将游戏数据导入到指定盒子。
     *
     * @param gameDataList   源游戏数据列表（每项是一个原始 JSON 对象的 Map 表示）
     * @param boxId          目标游戏盒子 ID
     * @param siteId         目标网站 ID
     * @param updateExisting 是否更新已存在的同名游戏（false = 跳过重名）
     * @return 导入结果，包含 successCount / failureCount / newCount / updatedCount / batchId / batchNo 等字段
     */
    Map<String, Object> importGamesWithFieldMapping(
        List<Map<String, Object>> gameDataList,
        Long boxId,
        Long siteId,
        boolean updateExisting
    );

    /**
     * 通过字段映射将游戏数据导入到指定盒子（携带操作人与名称，便于审计）。
     *
     * @param gameDataList        源游戏数据列表
     * @param boxId               目标游戏盒子 ID
     * @param siteId              目标网站 ID
     * @param updateExisting      是否更新已存在的同名游戏
     * @param operator            操作人（null 时使用 "system"）
     * @param boxName             盒子名称（冗余记录，可为 null）
     * @param siteName            网站名称（冗余记录，可为 null）
     * @return 导入结果，包含 batchId / batchNo 以供后续查询审计
     */
    Map<String, Object> importGamesWithFieldMapping(
        List<Map<String, Object>> gameDataList,
        Long boxId,
        Long siteId,
        boolean updateExisting,
        String operator,
        String boxName,
        String siteName
    );
}
