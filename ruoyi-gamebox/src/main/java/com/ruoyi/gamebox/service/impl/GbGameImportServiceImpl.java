package com.ruoyi.gamebox.service.impl;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.gamebox.domain.GbBoxFieldMapping;
import com.ruoyi.gamebox.domain.GbBoxGameRelation;
import com.ruoyi.gamebox.domain.GbGame;
import com.ruoyi.gamebox.domain.GbGameCategoryRelation;
import com.ruoyi.gamebox.domain.GbGameChangeLog;
import com.ruoyi.gamebox.domain.GbImportBatch;
import com.ruoyi.gamebox.mapper.GbGameChangeLogMapper;
import com.ruoyi.gamebox.mapper.GbGameMapper;
import com.ruoyi.gamebox.domain.GbGameBox;
import com.ruoyi.gamebox.domain.GbSite;
import com.ruoyi.gamebox.search.helper.ElasticsearchSyncHelper;
import com.ruoyi.gamebox.service.IFieldMappingApplyService;
import com.ruoyi.gamebox.service.IGbBoxFieldMappingService;
import com.ruoyi.gamebox.service.IGbBoxGameRelationService;
import com.ruoyi.gamebox.service.IGbGameBoxService;
import com.ruoyi.gamebox.service.IGbGameCategoryRelationService;
import com.ruoyi.gamebox.service.IGbGameImportService;
import com.ruoyi.gamebox.service.IGbGameService;
import com.ruoyi.gamebox.service.IGbImportBatchService;
import com.ruoyi.gamebox.service.IGbSiteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.ruoyi.common.utils.DateUtils;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * 游戏批量导入服务实现
 * 将"通过字段映射将源数据导入到指定盒子"的核心逻辑封装到独立的 Service。
 * 控制器（GbGameController）和原子工具执行器（ImportBoxGamesToolExecutor）均可复用。
 *
 * @author ruoyi
 * @date 2026-03-05
 */
@Service
public class GbGameImportServiceImpl implements IGbGameImportService {

    private static final Logger log = LoggerFactory.getLogger(GbGameImportServiceImpl.class);

    @Autowired
    private IFieldMappingApplyService fieldMappingApplyService;

    @Autowired
    private IGbBoxFieldMappingService gbBoxFieldMappingService;

    @Autowired
    private IGbGameService gbGameService;

    @Autowired
    private IGbGameCategoryRelationService gameCategoryRelationService;

    @Autowired
    private IGbBoxGameRelationService boxGameRelationService;

    @Autowired
    private IGbImportBatchService importBatchService;

    @Autowired
    private IGbGameBoxService gbGameBoxService;

    @Autowired
    private IGbSiteService gbSiteService;

    @Autowired
    private GbGameChangeLogMapper changeLogMapper;

    @Autowired
    private GbGameMapper gbGameMapper;

    @Autowired(required = false)
    private ElasticsearchSyncHelper elasticsearchSyncHelper;

    // ──────────────────────────────────────────────────────────────────────────
    // 主入口
    // ──────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> importGamesWithFieldMapping(
            List<Map<String, Object>> gameDataList,
            Long boxId,
            Long siteId,
            boolean updateExisting) {
        return importGamesWithFieldMapping(gameDataList, boxId, siteId, updateExisting, null, null, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> importGamesWithFieldMapping(
            List<Map<String, Object>> gameDataList,
            Long boxId,
            Long siteId,
            boolean updateExisting,
            String operator,
            String boxName,
            String siteName) {

        Map<String, Object> result = new HashMap<>();
        List<String> successList = new ArrayList<>();
        List<String> updatedList = new ArrayList<>();
        List<String> reusedList  = new ArrayList<>();
        List<String> skippedList = new ArrayList<>();
        List<String> errorList   = new ArrayList<>();

        if (gameDataList == null || gameDataList.isEmpty()) {
            result.put("successCount", 0);
            result.put("failureCount", 0);
            result.put("newCount", 0);
            result.put("updatedCount", 0);
            result.put("skippedCount", 0);
            result.put("message", "源数据列表为空，跳过导入");
            return result;
        }

        // ── 0. 创建导入批次记录 ───────────────────────────────────────────────
        // ── 自动查询盒子信息（名称 + 优先级） ─────────────────────────────────
        int incomingBoxPriority = 0;
        if (boxName == null) {
            try {
                GbGameBox box = gbGameBoxService.selectGbGameBoxById(boxId);
                if (box != null) {
                    boxName = box.getName();
                    incomingBoxPriority = box.getPriority() != null ? box.getPriority() : 0;
                }
            } catch (Exception ignored) {}
        } else {
            try {
                GbGameBox box = gbGameBoxService.selectGbGameBoxById(boxId);
                if (box != null) {
                    incomingBoxPriority = box.getPriority() != null ? box.getPriority() : 0;
                }
            } catch (Exception ignored) {}
        }
        if (siteName == null) {
            try {
                GbSite site = gbSiteService.selectGbSiteById(siteId);
                if (site != null) siteName = site.getName();
            } catch (Exception ignored) {}
        }
        final int boxPriority = incomingBoxPriority;

        String safeOperator = (operator != null && !operator.isEmpty()) ? operator : "system";
        GbImportBatch batch = importBatchService.createBatch(boxId, boxName, siteId, siteName, null, safeOperator);
        Long batchId  = batch.getId();
        String batchNo = batch.getBatchNo();
        Date   batchTime = new Date();

        // 变更日志暂存列表
        List<GbGameChangeLog> pendingLogs = new ArrayList<>();
        // 主表更新快照：{existingGame JSON, fillUpdateDelta game}（用于撤回时恢复原数据）
        // key = fillUpdateDelta.getId() → existingGame JSON
        Map<Long, String> gameBeforeSnapshots = new HashMap<>();
        // 关联表更新快照：existingRelation.getId() → existingRelation JSON
        Map<Long, String> relationBeforeSnapshots = new HashMap<>();

        log.info("[GameImport] 开始导入，共 {} 条，boxId={}, siteId={}, updateExisting={}, batchNo={}, boxPriority={}",
            gameDataList.size(), boxId, siteId, updateExisting, batchNo, boxPriority);

        // ── 1. 批量应用字段映射（只查一次映射配置，性能优先） ──────────────────
        List<Map<String, Object>> mappedDataList =
            fieldMappingApplyService.batchApplyBoxFieldMappings(gameDataList, boxId, "game");

        // ── 1.5 加载各位置字段冲突策略 ───────────────────────────────────────────
        Map<String, Map<String, String>> allStrategies   = loadAllFieldStrategies(boxId);
        Map<String, String> mainFieldStrategies          = allStrategies.getOrDefault("main",           Collections.emptyMap());
        Map<String, String> relationStrategies           = allStrategies.getOrDefault("relation",       Collections.emptyMap());
        Map<String, String> promotionStrategies          = allStrategies.getOrDefault("promotion_link", Collections.emptyMap());
        Map<String, String> platformStrategies           = allStrategies.getOrDefault("platform_data",  Collections.emptyMap());
        Map<String, String> categoryRelStrategies        = allStrategies.getOrDefault("category_relation", Collections.emptyMap());
        // 从字段映射配置中读取分类关联的冲突策略，默认为 merge
        final String catStrategy = categoryRelStrategies.getOrDefault("category_id", "merge");

        // ── 1.6 预加载游戏判重缓存（一次批量导入，晿免循环内 N+1） ─────────────────────────
        // 提取字段映射后的所有游戏名称（去重）
        Set<String> allMappedNames = new java.util.LinkedHashSet<>();
        for (Map<String, Object> md : mappedDataList) {
            if (md == null || md.isEmpty()) continue;
            @SuppressWarnings("unchecked")
            Map<String, Object> mf = (Map<String, Object>) md.get("mainFields");
            if (mf != null && mf.get("name") != null) {
                allMappedNames.add(String.valueOf(mf.get("name")));
            }
        }
        // 按名称批量查询，Java 层再按 subtitle/gameType 精确匹配，以 “name\x01subtitle\x01gameType” 为键
        Map<String, GbGame> existingGameCache = new HashMap<>();
        if (!allMappedNames.isEmpty()) {
            List<GbGame> preloadedGames = gbGameService.selectGbGamesByNamesForDedup(siteId, new ArrayList<>(allMappedNames));
            for (GbGame g : preloadedGames) {
                String k = buildGameDedupKey(g.getName(), g.getSubtitle(), g.getGameType());
                // 同一 key 可能有多条（实际不应该，局先保留最新的一条）
                existingGameCache.put(k, g);
            }
            log.info("[GameImport] 预加载游戏判重缓存，查询名称 {} 个，命中记录 {} 条",
                allMappedNames.size(), preloadedGames.size());
        }

        // ── 1.7 预加载盒子-游戏关联缓存（一次全量查询，避免循环内 N+1） ────────────────────
        Map<Long, GbBoxGameRelation> existingRelationCache = new HashMap<>();
        List<GbBoxGameRelation> currentBoxRelations = boxGameRelationService.selectGamesByBoxId(boxId);
        if (currentBoxRelations != null) {
            for (GbBoxGameRelation r : currentBoxRelations) {
                existingRelationCache.put(r.getGameId(), r);
            }
        }
        log.info("[GameImport] 预加载盒子关联缓存，boxId={}, 已有关联 {} 条", boxId, existingRelationCache.size());

        // ── 2. 准备写入数据集合 ─────────────────────────────────────────────────
        List<GbGame>                 gamesToInsert        = new ArrayList<>();
        List<GbGameCategoryRelation> categoryRelsToInsert = new ArrayList<>();
        List<GbBoxGameRelation>      boxGameRelsToInsert  = new ArrayList<>();
        // 已存在游戏的盒子关联需要更新（updateExisting=true 时）
        List<GbBoxGameRelation>      boxGameRelsToUpdate  = new ArrayList<>();
        // 已存在游戏但主表有空字段需要补全（fill-in 策略）
        List<GbGame>                 gamesToFillUpdate    = new ArrayList<>();
        // 高优先级完整覆盙列表（用 overwriteGbGameBySnapshot 写入）
        List<GbGame>                 gamesToOverwrite     = new ArrayList<>();
        // 同一批次内去重：防止同一个 box_id + game_id 被重复加入（统计会虚高，落库只会保留一条）
        Set<String> pendingRelationKeys = new LinkedHashSet<>();
        
        // 性能优化：缓存盒子优先级，避免在循环中产生 N+1 查询
        Map<Long, Integer> boxPriorityCache = new HashMap<>();
        boxPriorityCache.put(boxId, boxPriority);

        for (int i = 0; i < gameDataList.size(); i++) {
            Map<String, Object> rawData    = gameDataList.get(i);
            Map<String, Object> mappedData = (i < mappedDataList.size()) ? mappedDataList.get(i) : null;

            // 取一个可读的名字，用于错误信息
            String rawName = rawData.containsKey("gamename") ? String.valueOf(rawData.get("gamename"))
                           : rawData.containsKey("name")     ? String.valueOf(rawData.get("name"))
                           : "游戏#" + (i + 1);

            try {
                if (mappedData == null || mappedData.isEmpty()) {
                    errorList.add(rawName + ": 字段映射失败（无配置或转换结果为空）");
                    continue;
                }

                @SuppressWarnings("unchecked")
                Map<String, Object> mainFields          = (Map<String, Object>) mappedData.get("mainFields");
                @SuppressWarnings("unchecked")
                Map<String, Object> extensionFields     = (Map<String, Object>) mappedData.get("extensionFields");
                @SuppressWarnings("unchecked")
                Map<String, Object> promotionLinks      = (Map<String, Object>) mappedData.get("promotionLinks");
                @SuppressWarnings("unchecked")
                Map<String, Object> relationFields      = (Map<String, Object>) mappedData.get("relationFields");
                @SuppressWarnings("unchecked")
                Map<String, Object> categoryRelFields   = (Map<String, Object>) mappedData.get("categoryRelationFields");

                // ── 构建 GbGame 对象 ─────────────────────────────────────────
                GbGame game = new GbGame();
                game.setSiteId(siteId);

                if (mainFields != null && !mainFields.isEmpty()) {
                    applyMainFieldsToGame(game, mainFields);
                }

                // 必填校验
                if (game.getName() == null || game.getName().isEmpty()) {
                    errorList.add(rawName + ": 字段映射后游戏名称为空，请检查字段映射配置");
                    continue;
                }

                // 默认值
                if (game.getStatus() == null) game.setStatus("1");
                if (game.getDelFlag() == null) game.setDelFlag("0");

                // ── 第一级判重：游戏主表（name + subtitle + gameType + siteId） ─────────
                // 同名但副标题/游戏类型不同的游戏视为不同游戏（例：同一款游戏的不同版本或类型）
                // 使用预加载缓存，避免 N+1 查询
                GbGame existingGame = existingGameCache.get(buildGameDedupKey(game.getName(), game.getSubtitle(), game.getGameType()));

                Long gameId;
                boolean isNewGame;
                if (existingGame == null) {
                    // 游戏不存在 → 加入新增队列，暂存关联数据
                    gamesToInsert.add(game);
                    storeAssociatedData(game, extensionFields, promotionLinks, relationFields,
                                        categoryRelFields, mainFields, boxId);
                    isNewGame = true;
                    gameId = null; // 插入后才能拿到
                } else {
                    // 游戏已存在 → 复用 ID，不跳过，继续判盒子关联
                    gameId = existingGame.getId();
                    isNewGame = false;
                    // ── 优先级判断：动态读取来源盒子的当前优先级 ──────────
                    int existingPriority = -1;
                    if (existingGame.getSourceBoxId() != null) {
                        Long sourceBoxId = existingGame.getSourceBoxId();
                        if (!boxPriorityCache.containsKey(sourceBoxId)) {
                            GbGameBox sourceBox = gbGameBoxService.selectGbGameBoxById(sourceBoxId);
                            boxPriorityCache.put(sourceBoxId, (sourceBox != null && sourceBox.getPriority() != null) ? sourceBox.getPriority() : -1);
                        }
                        existingPriority = boxPriorityCache.get(sourceBoxId);
                    }
                    boolean isHigherPriority = boxPriority >= existingPriority;

                    // ── 构建有效策略：仅 overwrite 受优先级影响 ──────────
                    // overwrite：高优先级 → 保持 overwrite；低优先级 → 降级为 skip（不触碰）
                    // fill_empty：始终不变，无视优先级，仅补全空字段
                    // skip：始终不变，无视优先级，首次导入后不再更新
                    Map<String, String> effectiveStrategies = new HashMap<>(mainFieldStrategies);
                    if (!isHigherPriority) {
                        // 低优先级：仅将 overwrite 降级为 skip
                        for (Map.Entry<String, String> entry : effectiveStrategies.entrySet()) {
                            if ("overwrite".equals(entry.getValue())) {
                                entry.setValue("skip");
                            }
                        }
                    }

                    if (isHigherPriority) {
                        // ── 高优先级：构建完整快照覆盖（overwrite 字段支持置空） ──
                        GbGame overwriteGame = buildHighPriorityOverwriteGame(existingGame, game, effectiveStrategies);
                        overwriteGame.setSourceBoxId(boxId);
                        gamesToOverwrite.add(overwriteGame);
                        gameBeforeSnapshots.put(existingGame.getId(), toSortedJson(existingGame));
                    } else {
                        // ── 低优先级：overwrite 字段被跳过，仅处理 fill_empty 字段 ──
                        GbGame fillUpdate = buildFillUpdateGame(existingGame, game, effectiveStrategies);
                        if (fillUpdate != null) {
                            gamesToFillUpdate.add(fillUpdate);
                            gameBeforeSnapshots.put(existingGame.getId(), toSortedJson(existingGame));
                        }
                    }
                }

                if (!isNewGame) {
                    // ── 第二级判重：盒子关联表（box_id + game_id） ────────────────────────
                    // 使用预加载缓存，避免 N+1 查询
                    GbBoxGameRelation existingRelation = existingRelationCache.get(gameId);
                    if (existingRelation != null) {
                        if (!updateExisting) {
                            skippedList.add(game.getName() + "（盒子关联已存在，跳过）");
                            continue;
                        }
                        // 更新已有关联行（含 promotion_links / platform_data），应用字段冲突策略
                        Map<String, Object> filteredRelFields  = filterByRelationStrategy(existingRelation, relationFields, relationStrategies);
                        Map<String, Object> mergedPromotion    = mergeJsonByStrategy(existingRelation.getPromotionLinks(), promotionLinks, promotionStrategies);
                        Map<String, Object> mergedPlatform     = mergeJsonByStrategy(existingRelation.getPlatformData(), extensionFields, platformStrategies);
                        GbBoxGameRelation rel = buildBoxGameRelation(gameId, boxId,
                                mergedPlatform, mergedPromotion, filteredRelFields);
                        rel.setId(existingRelation.getId());
                        rel.setGameName(game.getName());
                        boxGameRelsToUpdate.add(rel);
                        // gameName 是虚拟 JOIN 字段，不应入快照；保存前设为 null
                        existingRelation.setGameName(null);
                        relationBeforeSnapshots.put(existingRelation.getId(), toSortedJson(existingRelation));
                        updatedList.add(game.getName());
                    } else {
                        // 关联不存在 → 新建关联
                        String relationKey = boxId + "-" + gameId;
                        if (pendingRelationKeys.contains(relationKey)) {
                            skippedList.add(game.getName() + "（本批次重复映射，已合并）");
                            continue;
                        }
                        pendingRelationKeys.add(relationKey);
                        GbBoxGameRelation rel = buildBoxGameRelation(gameId, boxId,
                                extensionFields, promotionLinks, relationFields);
                        rel.setGameName(game.getName());
                        boxGameRelsToInsert.add(rel);
                        reusedList.add(game.getName());
                    }
                }

            } catch (Exception e) {
                errorList.add(rawName + ": " + e.getMessage());
                log.error("[GameImport] 处理第 {} 条数据失败: {}", i + 1, e.getMessage(), e);
            }
        }

        // ── 2.5a 高优先级完整覆盙（支持置空未映射字段） ──────────────────────
        if (!gamesToOverwrite.isEmpty()) {
            log.info("[GameImport] 高优先级批量覆盙主表，共 {} 条", gamesToOverwrite.size());
            // 先在内存中构建变更日志（无DB调用）
            for (GbGame overwriteGame : gamesToOverwrite) {
                String beforeJson = gameBeforeSnapshots.get(overwriteGame.getId());
                if (beforeJson != null) {
                    GbGameChangeLog cl = buildChangeLog(batchId, batchNo, overwriteGame.getId(),
                            overwriteGame.getName() != null ? overwriteGame.getName() : "",
                            "OVERWRITE", "gb_games", overwriteGame.getId(),
                            beforeJson, toSortedJson(overwriteGame), safeOperator, batchTime);
                    pendingLogs.add(cl);
                }
            }
            // 分批执行，避免 description/screenshots 等大字段导致单 SQL 过大
            final int GAME_BATCH = 100;
            for (int bi = 0; bi < gamesToOverwrite.size(); bi += GAME_BATCH) {
                gbGameMapper.batchOverwriteGbGameBySnapshot(
                    gamesToOverwrite.subList(bi, Math.min(bi + GAME_BATCH, gamesToOverwrite.size())));
            }
        }

        // ── 2.5b 低优先级补全主表空字段（fill-in 策略） ──────────────────────
        if (!gamesToFillUpdate.isEmpty()) {
            log.info("[GameImport] 批量补全主表空字段，共 {} 条", gamesToFillUpdate.size());
            // 先在内存中构建变更日志（无DB调用）
            Date fillUpdateTime = DateUtils.getNowDate();
            for (GbGame fillGame : gamesToFillUpdate) {
                fillGame.setUpdateTime(fillUpdateTime);
                String beforeJson = gameBeforeSnapshots.get(fillGame.getId());
                if (beforeJson != null) {
                    GbGameChangeLog cl = buildChangeLog(batchId, batchNo, fillGame.getId(),
                            fillGame.getName() != null ? fillGame.getName() : "",
                            "UPDATE", "gb_games", fillGame.getId(),
                            beforeJson, mergeSnapshotJson(beforeJson, fillGame), safeOperator, batchTime);
                    pendingLogs.add(cl);
                }
            }
            // 分批执行，避免单 SQL 过大
            final int GAME_BATCH_B = 100;
            for (int bi = 0; bi < gamesToFillUpdate.size(); bi += GAME_BATCH_B) {
                gbGameMapper.batchFillUpdateGames(
                    gamesToFillUpdate.subList(bi, Math.min(bi + GAME_BATCH_B, gamesToFillUpdate.size())));
            }
        }

        // ── 3. 批量插入新游戏，ID 回填后构建关联 ──────────────────────────────────
        if (!gamesToInsert.isEmpty()) {
            // 新游戏设置来源信息
            for (GbGame g : gamesToInsert) {
                g.setSourceBoxId(boxId);
            }
            log.info("[GameImport] 批量插入新游戏 {} 条", gamesToInsert.size());
            // skipEsSync=true: 盒子关联尚未插入，由本方法在事务提交后统一触发同步
            gbGameService.batchInsertGames(gamesToInsert, true);
        }
        for (GbGame game : gamesToInsert) {
            if (game.getId() == null) {
                errorList.add(game.getName() + ": 插入后未获取到 ID，跳过关联");
                continue;
            }
            try {
                buildAndCollectAssociatedData(game, boxId, categoryRelsToInsert, boxGameRelsToInsert);
                successList.add(game.getName());
            } catch (Exception e) {
                errorList.add(game.getName() + ": 准备关联数据失败 - " + e.getMessage());
            }
        }

        // ── 4. 批量更新已有盒子关联行 ────────────────────────────────────────────
        if (!boxGameRelsToUpdate.isEmpty()) {
            // 先收集变更日志（计算不依赖 DB 回写，可在实际更新前完成）
            for (GbBoxGameRelation rel : boxGameRelsToUpdate) {
                String beforeJson = relationBeforeSnapshots.get(rel.getId());
                if (beforeJson != null) {
                    String gameNameForLog = rel.getGameName();
                    rel.setGameName(null);
                    GbGameChangeLog cl = buildChangeLog(batchId, batchNo, rel.getGameId(),
                            gameNameForLog != null ? gameNameForLog : "",
                            "UPDATE", "gb_box_game_relations", rel.getId(),
                            beforeJson, mergeSnapshotJson(beforeJson, rel), safeOperator, batchTime);
                    pendingLogs.add(cl);
                }
            }
            try {
                log.info("[GameImport] 批量更新盒子-游戏关联 {} 条", boxGameRelsToUpdate.size());
                boxGameRelationService.batchUpdateRelations(boxGameRelsToUpdate);
            } catch (Exception e) {
                log.error("[GameImport] 批量更新关联失败", e);
                for (GbBoxGameRelation rel : boxGameRelsToUpdate) {
                    errorList.add("关联#" + rel.getId() + ": 更新失败 - " + e.getMessage());
                }
            }
        }

        // ── 5. 批量插入新盒子关联和分类关联 ─────────────────────────────────────
        if (!categoryRelsToInsert.isEmpty()) {
            // 分类策略处理：overwrite 时先删除再插入，merge 时去重后追加
            if ("overwrite".equals(catStrategy)) {
                // 收集需要清理分类的游戏 ID（仅已存在的游戏，新游戏无需清理）
                Set<Long> existingGameIds = new java.util.HashSet<>();
                for (GbGame g : gamesToFillUpdate) {
                    if (g.getId() != null) existingGameIds.add(g.getId());
                }
                for (GbGameCategoryRelation cr : categoryRelsToInsert) {
                    if (existingGameIds.contains(cr.getGameId())) {
                        try {
                            gameCategoryRelationService.batchDeleteByGameIds(java.util.Arrays.asList(cr.getGameId()));
                            existingGameIds.remove(cr.getGameId()); // 每个游戏只删一次
                        } catch (Exception e) {
                            log.warn("[GameImport] 清理分类关联失败: gameId={}", cr.getGameId(), e);
                        }
                    }
                }
                log.info("[GameImport] 分类覆盖模式，批量插入分类关联 {} 条", categoryRelsToInsert.size());
                gameCategoryRelationService.batchInsertRelations(categoryRelsToInsert);
            } else {
                // merge 模式：批量一次查询所有相关 gameId 的已有分类，过滤掉已存在的关联（避免 N+1）
                Set<Long> catGameIds = new java.util.LinkedHashSet<>();
                for (GbGameCategoryRelation cr : categoryRelsToInsert) {
                    if (cr.getGameId() != null) catGameIds.add(cr.getGameId());
                }
                Map<Long, Set<Long>> existingCatMap = new HashMap<>();
                if (!catGameIds.isEmpty()) {
                    List<GbGameCategoryRelation> allExisting =
                        gameCategoryRelationService.selectCategoriesByGameIds(new ArrayList<>(catGameIds));
                    for (GbGameCategoryRelation ex : allExisting) {
                        existingCatMap.computeIfAbsent(ex.getGameId(), k -> new java.util.HashSet<>())
                                      .add(ex.getCategoryId());
                    }
                }
                List<GbGameCategoryRelation> filteredCatRels = new ArrayList<>();
                for (GbGameCategoryRelation cr : categoryRelsToInsert) {
                    Set<Long> existingCatIds = existingCatMap.getOrDefault(cr.getGameId(), Collections.emptySet());
                    if (!existingCatIds.contains(cr.getCategoryId())) {
                        filteredCatRels.add(cr);
                    }
                }
                if (!filteredCatRels.isEmpty()) {
                    log.info("[GameImport] 分类合并模式，批量插入分类关联 {} 条（原 {} 条）", filteredCatRels.size(), categoryRelsToInsert.size());
                    gameCategoryRelationService.batchInsertRelations(filteredCatRels);
                }
            }
        }
        if (!boxGameRelsToInsert.isEmpty()) {
            log.info("[GameImport] 批量插入盒子-游戏关联 {} 条，boxId={}", boxGameRelsToInsert.size(), boxId);
            boxGameRelationService.batchInsertRelations(boxGameRelsToInsert);
            // 记录关联表 INSERT 变更日志（ID 已由 batchInsertRelations 回填）
            for (GbBoxGameRelation rel : boxGameRelsToInsert) {
                if (rel.getId() == null) continue;
                GbGameChangeLog cl = buildChangeLog(batchId, batchNo, rel.getGameId(),
                        rel.getGameName() != null ? rel.getGameName() : "",
                        "INSERT", "gb_box_game_relations", rel.getId(),
                        null, toSortedJson(rel), safeOperator, batchTime);
                pendingLogs.add(cl);
            }
        }

        // 记录新游戏主表 INSERT 变更日志（gamesToInsert 在 batchInsertGames 后已有 ID）
        for (GbGame game : gamesToInsert) {
            if (game.getId() == null) continue;
            GbGameChangeLog cl = buildChangeLog(batchId, batchNo, game.getId(),
                    game.getName() != null ? game.getName() : "",
                    "INSERT", "gb_games", game.getId(),
                    null, toSortedJson(game), safeOperator, batchTime);
            pendingLogs.add(cl);
        }

        // ── 5.5 批量保存变更日志 ──────────────────────────────────────────────────
        if (!pendingLogs.isEmpty()) {
            try {
                changeLogMapper.batchInsert(pendingLogs);
            } catch (Exception e) {
                log.error("[GameImport] 保存变更日志失败（不影响主导入流程）: {}", e.getMessage(), e);
            }
        }

        // ── 5.9 事务提交后触发 ES 同步（盒子关联已写入 DB，docs.boxes 能正确填充）────
        if (elasticsearchSyncHelper != null) {
            // 收集所有需要同步的游戏，去重
            java.util.Map<Long, GbGame> syncMap = new java.util.LinkedHashMap<>();
            for (GbGame g : gamesToInsert) {
                if (g.getId() != null) syncMap.put(g.getId(), g);
            }
            for (GbGame g : gamesToFillUpdate) {
                if (g.getId() != null) syncMap.put(g.getId(), g);
            }
            // 复用游戏：只更新了盒子关联，游戏主表未变 → 也需要重新同步 ES（boxes 字段变了）
            Set<Long> extraIds = new java.util.LinkedHashSet<>();
            for (GbBoxGameRelation rel : boxGameRelsToInsert) {
                if (rel.getGameId() != null && !syncMap.containsKey(rel.getGameId())) {
                    extraIds.add(rel.getGameId());
                }
            }
            for (GbBoxGameRelation rel : boxGameRelsToUpdate) {
                if (rel.getGameId() != null && !syncMap.containsKey(rel.getGameId())) {
                    extraIds.add(rel.getGameId());
                }
            }
            if (!extraIds.isEmpty()) {
                try {
                    List<GbGame> extraGames = gbGameService.selectGbGameByIds(new ArrayList<>(extraIds));
                    for (GbGame g : extraGames) {
                        if (g.getId() != null) syncMap.put(g.getId(), g);
                    }
                } catch (Exception e) {
                    log.warn("[GameImport] 补查复用游戏对象失败，部分游戏 ES 同步可能缺失: {}", e.getMessage());
                }
            }
            if (!syncMap.isEmpty()) {
                final List<GbGame> finalGamesToSync = new ArrayList<>(syncMap.values());
                try {
                    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            log.info("[GameImport] 事务提交后触发 ES 同步，共 {} 个游戏（含盒子关联）", finalGamesToSync.size());
                            elasticsearchSyncHelper.syncGames(finalGamesToSync);
                        }
                    });
                } catch (Exception e) {
                    log.warn("[GameImport] 注册事务后 ES 同步回调失败，将跳过本次 ES 同步: {}", e.getMessage());
                }
            }
        }

        // ── 6. 组装结果 ──────────────────────────────────────────────────────────
        int totalCount = gameDataList.size();
        int newCount   = successList.size();
        int relUpdCount = updatedList.size();
        int reusedCount = reusedList.size();
        int updCount   = relUpdCount + reusedCount;
        int skipCount  = skippedList.size();
        int failCount  = errorList.size();

        String message = String.format(
            "导入完成！新增游戏 %d 条，复用已有游戏并新增盒子关联 %d 条，更新已有关联 %d 条，跳过 %d 条，失败 %d 条（共 %d 条）",
            newCount, reusedCount, relUpdCount, skipCount, failCount, totalCount
        );
        log.info("[GameImport] {}", message);

        // ── 6.5 更新批次统计 ──────────────────────────────────────────────────────
        try {
            Map<String, Object> summaryPayload = new HashMap<>();
            if (!errorList.isEmpty()) {
                summaryPayload.put("errorList", errorList);
            }
            if (!skippedList.isEmpty()) {
                summaryPayload.put("skippedList", skippedList);
            }
            String summaryJson = summaryPayload.isEmpty() ? null : JSON.toJSONString(summaryPayload);
            importBatchService.finishBatch(batchId, totalCount, newCount, updCount, skipCount, failCount,
                    summaryJson);
        } catch (Exception e) {
            log.error("[GameImport] 更新批次统计失败: {}", e.getMessage(), e);
        }

        result.put("totalCount",   totalCount);
        result.put("successCount", newCount + reusedCount + relUpdCount);
        result.put("failureCount", failCount);
        result.put("newCount",     newCount);
        result.put("updatedCount", updCount);
        result.put("relationUpdatedCount", relUpdCount);
        result.put("reusedCount",  reusedCount);
        result.put("skippedCount", skipCount);
        result.put("message",      message);
        result.put("batchId",      batchId);
        result.put("batchNo",      batchNo);
        if (!skippedList.isEmpty()) {
            result.put("skippedList", skippedList);
        }
        if (!reusedList.isEmpty()) {
            result.put("reusedList", reusedList);
        }
        if (!errorList.isEmpty()) {
            result.put("errorList", errorList);
        }
        return result;
    }

    /**
     * 构建游戏判重的缓存 key：name\x01subtitle\x01gameType。
     * null 的字段用空字符串替代，确保 IS NULL 匹配与普通字符串匹配一致。
     */
    private String buildGameDedupKey(String name, String subtitle, String gameType) {
        return (name != null ? name : "") + "\u0001"
             + (subtitle != null ? subtitle : "") + "\u0001"
             + (gameType != null ? gameType : "");
    }

    /** 构建变更日志对象（内部辅助方法） */
    private GbGameChangeLog buildChangeLog(Long batchId, String batchNo, Long gameId, String gameName,
                                            String changeType, String targetTable, Long targetId,
                                            String before, String after, String operator, Date createTime) {
        GbGameChangeLog log2 = new GbGameChangeLog();
        log2.setBatchId(batchId);
        log2.setBatchNo(batchNo);
        log2.setGameId(gameId);
        log2.setGameName(gameName);
        log2.setChangeType(changeType);
        log2.setTargetTable(targetTable);
        log2.setTargetId(targetId);
        log2.setBeforeSnapshot(before);
        log2.setAfterSnapshot(after);
        log2.setReverted(0);
        log2.setCreateTime(createTime);
        log2.setCreateBy(operator);
        return log2;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 工具方法：关联数据的暂存与构建
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * 将各类关联数据暂存到 game 对象的 remark 字段（JSON 格式）。
     * 使用约定好的特殊前缀，等游戏 ID 生成后再解析还原。
     */
    private void storeAssociatedData(GbGame game,
                                     Map<String, Object> extensionFields,
                                     Map<String, Object> promotionLinks,
                                     Map<String, Object> relationFields,
                                     Map<String, Object> categoryRelFields,
                                     Map<String, Object> mainFields,
                                     Long boxId) {
        Map<String, Object> temp = new HashMap<>();
        temp.put("extensionFields",   extensionFields);
        temp.put("promotionLinks",    promotionLinks);
        temp.put("relationFields",    relationFields);
        temp.put("categoryRelFields", categoryRelFields);
        temp.put("mainFields",        mainFields);
        temp.put("boxId",             boxId);
        // 用 remark 临时序列化；原有 remark 暂不保留（导入场景通常不填 remark）
        game.setRemark("__IMPORT_TEMP__:" + JSON.toJSONString(temp));
    }

    /**
     * 从 game.remark 中还原关联数据，并放入目标集合。
     * promotion_links / platform_data 直接写入关联表，不再写 gb_games_platform_ext。
     */
    @SuppressWarnings("unchecked")
    private void buildAndCollectAssociatedData(GbGame game,
                                               Long boxId,
                                               List<GbGameCategoryRelation> categoryRels,
                                               List<GbBoxGameRelation>      boxGameRels) {
        String remark = game.getRemark();
        if (remark != null && remark.startsWith("__IMPORT_TEMP__:")) {
            String json = remark.substring("__IMPORT_TEMP__:".length());
            Map<String, Object> temp = JSON.parseObject(json, Map.class);

            Map<String, Object> extensionFields  = (Map<String, Object>) temp.get("extensionFields");
            Map<String, Object> promotionLinks   = (Map<String, Object>) temp.get("promotionLinks");
            Map<String, Object> relationFields   = (Map<String, Object>) temp.get("relationFields");
            Map<String, Object> categoryRelFields= (Map<String, Object>) temp.get("categoryRelFields");

            // 清理临时标记
            game.setRemark(null);

            // 分类关联（支持多分类）
            List<Long> categoryIds = extractCategoryIds(categoryRelFields);
            for (int ci = 0; ci < categoryIds.size(); ci++) {
                GbGameCategoryRelation rel = new GbGameCategoryRelation();
                rel.setGameId(game.getId());
                rel.setCategoryId(categoryIds.get(ci));
                rel.setIsPrimary(ci == 0 ? "1" : "0"); // 第一个为主分类
                rel.setSortOrder(ci);
                if (ci == 0 && categoryRelFields != null) {
                    if (categoryRelFields.containsKey("is_primary")) {
                        rel.setIsPrimary(String.valueOf(categoryRelFields.get("is_primary")));
                    }
                    if (categoryRelFields.containsKey("sort_order")) {
                        try { rel.setSortOrder(Integer.parseInt(String.valueOf(categoryRelFields.get("sort_order")))); }
                        catch (NumberFormatException ignored) {}
                    }
                }
                categoryRels.add(rel);
            }

            // 盒子-游戏关联（含 promotion_links / platform_data）
            GbBoxGameRelation bgRel = buildBoxGameRelation(game.getId(), boxId,
                    extensionFields, promotionLinks, relationFields);
            // 关联行也带上游戏名称，方便变更日志展示
            bgRel.setGameName(game.getName());
            boxGameRels.add(bgRel);
        }
    }

    /**
     * 构建 GbBoxGameRelation 对象，将 extensionFields/promotionLinks/relationFields 写入关联行。
     */
    private GbBoxGameRelation buildBoxGameRelation(Long gameId, Long boxId,
                                                    Map<String, Object> extensionFields,
                                                    Map<String, Object> promotionLinks,
                                                    Map<String, Object> relationFields) {
        GbBoxGameRelation rel = new GbBoxGameRelation();
        rel.setBoxId(boxId);
        rel.setGameId(gameId);
        rel.setSortOrder(0);
        // 平台特有数据 → platform_data JSON 列（键排序保证序列化稳定）
        if (extensionFields != null && !extensionFields.isEmpty()) {
            rel.setPlatformData(toSortedJson(extensionFields));
        }
        // 推广链接 → promotion_links JSON 列（键排序保证序列化稳定）
        if (promotionLinks != null && !promotionLinks.isEmpty()) {
            rel.setPromotionLinks(toSortedJson(promotionLinks));
        }
        // 关联表明确列（discount_label / download_url 等）
        if (relationFields != null && !relationFields.isEmpty()) {
            applyRelationFieldsToBoxGameRelation(rel, relationFields);
        }
        return rel;
    }

    private Long extractCategoryId(Map<String, Object> categoryRelFields) {
        List<Long> ids = extractCategoryIds(categoryRelFields);
        return ids.isEmpty() ? null : ids.get(0);
    }

    /**
     * 从 categoryRelFields 提取所有分类 ID（支持单个整数、数字字符串、List、JSONArray）
     */
    @SuppressWarnings("unchecked")
    private List<Long> extractCategoryIds(Map<String, Object> categoryRelFields) {
        if (categoryRelFields == null || !categoryRelFields.containsKey("category_id")) return Collections.emptyList();
        Object value = categoryRelFields.get("category_id");
        Set<Long> unique = new LinkedHashSet<>();
        if (value instanceof List) {
            for (Object item : (List<?>) value) {
                if (item == null) continue;
                try { unique.add(Long.valueOf(item.toString().trim())); } catch (Exception ignored) {}
            }
        } else if (value instanceof com.alibaba.fastjson2.JSONArray) {
            com.alibaba.fastjson2.JSONArray arr = (com.alibaba.fastjson2.JSONArray) value;
            for (int i = 0; i < arr.size(); i++) {
                try {
                    Long categoryId = arr.getLong(i);
                    if (categoryId != null) {
                        unique.add(categoryId);
                    }
                } catch (Exception ignored) {}
            }
        } else {
            try { unique.add(Long.valueOf(value.toString().trim())); } catch (Exception ignored) {}
        }
        return new ArrayList<>(unique);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 字段设置工具方法（与 GbGameController 保持一致）
    // ──────────────────────────────────────────────────────────────────────────

    private void applyMainFieldsToGame(GbGame game, Map<String, Object> mainFields) {
        for (Map.Entry<String, Object> entry : mainFields.entrySet()) {
            String fieldName = entry.getKey();
            Object value     = entry.getValue();
            if (value == null) continue;
            try {
                switch (fieldName) {
                    case "name":              game.setName(value.toString()); break;
                    case "subtitle":          game.setSubtitle(value.toString()); break;
                    case "short_name":        game.setShortName(value.toString()); break;
                    case "game_type":         game.setGameType(value.toString()); break;
                    case "icon_url":          game.setIconUrl(value.toString()); break;
                    case "cover_url":         game.setCoverUrl(value.toString()); break;
                    case "banner_url":        game.setBannerUrl(value.toString()); break;
                    case "screenshots":       game.setScreenshots(value.toString()); break;
                    case "video_url":         game.setVideoUrl(value.toString()); break;
                    case "description":       game.setDescription(value.toString()); break;
                    case "promotion_desc":    game.setPromotionDesc(value.toString()); break;
                    case "developer":         game.setDeveloper(value.toString()); break;
                    case "publisher":         game.setPublisher(value.toString()); break;
                    case "package_name":      game.setPackageName(value.toString()); break;
                    case "version":           game.setVersion(value.toString()); break;
                    case "size":              game.setSize(value.toString()); break;
                    case "device_support":
                        if (value instanceof Number) {
                            int t = ((Number) value).intValue();
                            game.setDeviceSupport(t == 1 ? "android" : t == 2 ? "ios" : "both");
                        } else {
                            game.setDeviceSupport(value.toString());
                        }
                        break;
                    case "download_count":
                        if (value instanceof Number) game.setDownloadCount(((Number) value).intValue()); break;
                    case "rating":
                        if (value instanceof Number) game.setRating(new BigDecimal(value.toString())); break;
                    case "features":          game.setFeatures(value.toString()); break;
                    case "tags":              game.setTags(value.toString()); break;
                    case "launch_time":
                        if (value instanceof Date) game.setLaunchTime((Date) value); break;
                    case "is_new":            game.setIsNew(value.toString()); break;
                    case "is_hot":            game.setIsHot(value.toString()); break;
                    case "is_recommend":      game.setIsRecommend(value.toString()); break;
                    case "sort_order":
                        if (value instanceof Number) game.setSortOrder(((Number) value).intValue()); break;
                    case "status":            game.setStatus(value.toString()); break;
                    case "download_url":      game.setDownloadUrl(value.toString()); break;
                    case "android_url":       game.setAndroidUrl(value.toString()); break;
                    case "ios_url":           game.setIosUrl(value.toString()); break;
                    case "apk_url":           game.setApkUrl(value.toString()); break;
                    case "discount_label":    game.setDiscountLabel(value.toString()); break;
                    case "first_charge_discount":
                    case "first_charge_domestic":
                        if (value instanceof Number) game.setFirstChargeDomestic(new BigDecimal(value.toString())); break;
                    case "first_charge_overseas":
                        if (value instanceof Number) game.setFirstChargeOverseas(new BigDecimal(value.toString())); break;
                    case "recharge_discount":
                    case "recharge_domestic":
                        if (value instanceof Number) game.setRechargeDomestic(new BigDecimal(value.toString())); break;
                    case "recharge_overseas":
                        if (value instanceof Number) game.setRechargeOverseas(new BigDecimal(value.toString())); break;
                    case "has_support":       game.setHasSupport(value.toString()); break;
                    case "support_desc":      game.setSupportDesc(value.toString()); break;
                    case "has_low_deduct_coupon":    game.setHasLowDeductCoupon(value.toString()); break;
                    case "low_deduct_coupon_url":    game.setLowDeductCouponUrl(value.toString()); break;
                    case "category_id":       break; // 分类通过 categoryRelationFields 处理
                    default:
                        log.debug("[GameImport] 未处理的 mainField: {} = {}", fieldName, value);
                }
            } catch (Exception e) {
                log.warn("[GameImport] 设置字段 {} 失败: {}", fieldName, e.getMessage());
            }
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 加载字段冲突策略
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * 加载指定盒子的全部字段冲突策略，按 targetLocation 分组。
     * <ul>
     *   <li>main 默认策略：fill_empty（共享主表，保守不覆盖）</li>
     *   <li>relation / promotion_link / platform_data 默认策略：overwrite（盒子专属，重新导入应更新）</li>
     * </ul>
     */
    private Map<String, Map<String, String>> loadAllFieldStrategies(Long boxId) {
        if (boxId == null) return Collections.emptyMap();
        List<GbBoxFieldMapping> mappings = gbBoxFieldMappingService.selectByBoxId(boxId);
        if (mappings == null || mappings.isEmpty()) return Collections.emptyMap();
        Map<String, Map<String, String>> result = new HashMap<>();
        for (GbBoxFieldMapping m : mappings) {
            if (m.getConflictStrategy() != null && !m.getConflictStrategy().isEmpty()) {
                result.computeIfAbsent(m.getTargetLocation(), k -> new HashMap<>())
                      .put(m.getTargetField(), m.getConflictStrategy());
            }
        }
        return result;
    }

    /**
     * 按字段冲突策略过滤 relation 表的明确列字段。
     * relation 默认策略：overwrite（盒子专属，重新导入应更新）。
     */
    private Map<String, Object> filterByRelationStrategy(GbBoxGameRelation existing,
                                                          Map<String, Object> incoming,
                                                          Map<String, String> strategies) {
        if (incoming == null || incoming.isEmpty()) return Collections.emptyMap();
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : incoming.entrySet()) {
            String fieldName = entry.getKey();
            Object val = entry.getValue();
            if (val == null || val.toString().trim().isEmpty()) continue;
            String strategy = strategies.getOrDefault(fieldName, "overwrite");
            if ("skip".equals(strategy)) continue;
            if ("overwrite".equals(strategy)) {
                result.put(fieldName, val);
            } else { // fill_empty
                String existingVal = getRelationFieldAsString(existing, fieldName);
                if (isBlank(existingVal)) {
                    result.put(fieldName, val);
                }
            }
        }
        return result;
    }

    /**
     * 将 incoming 中的 key 按冲突策略合并到现有 JSON 上，返回合并后的 Map。
     * 以现有 JSON 为基础起点，确保未经本次映射的 key 被保留。
     * JSON 字段（promotion_link/platform_data）默认策略：overwrite。
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> mergeJsonByStrategy(String existingJson, Map<String, Object> incoming,
                                                    Map<String, String> strategies) {
        Map<String, Object> base = new HashMap<>();
        if (!isBlank(existingJson)) {
            try {
                Map<String, Object> parsed = JSON.parseObject(existingJson, Map.class);
                if (parsed != null) base.putAll(parsed);
            } catch (Exception e) {
                log.warn("[GameImport] 解析现有 JSON 失败，将以新数据替代: {}", e.getMessage());
            }
        }
        if (incoming != null) {
            for (Map.Entry<String, Object> entry : incoming.entrySet()) {
                String key = entry.getKey();
                Object val = entry.getValue();
                if (val == null || val.toString().trim().isEmpty()) continue;
                String strategy = strategies.getOrDefault(key, "overwrite");
                if ("skip".equals(strategy)) continue;
                if ("overwrite".equals(strategy)) {
                    base.put(key, val);
                } else { // fill_empty
                    if (!base.containsKey(key)) {
                        base.put(key, val);
                    }
                }
            }
        }
        return base;
    }

    /**
     * 获取 GbBoxGameRelation 中指定字段的当前值（用于 fill_empty 策略判断）。
     */
    private String getRelationFieldAsString(GbBoxGameRelation rel, String fieldName) {
        if (rel == null) return null;
        switch (fieldName) {
            case "discount_label":        return rel.getDiscountLabel();
            case "has_support":           return rel.getHasSupport();
            case "support_desc":          return rel.getSupportDesc();
            case "download_url":          return rel.getDownloadUrl();
            case "promote_url":           return rel.getPromoteUrl();
            case "qrcode_url":            return rel.getQrcodeUrl();
            case "promote_text":          return rel.getPromoteText();
            case "poster_url":            return rel.getPosterUrl();
            case "is_featured":           return rel.getIsFeatured();
            case "is_exclusive":          return rel.getIsExclusive();
            case "is_new":                return rel.getIsNew();
            case "custom_name":           return rel.getCustomName();
            case "custom_description":    return rel.getCustomDescription();
            case "custom_download_url":   return rel.getCustomDownloadUrl();
            case "first_charge_domestic": return rel.getFirstChargeDomestic() != null ? rel.getFirstChargeDomestic().toPlainString() : null;
            case "first_charge_overseas": return rel.getFirstChargeOverseas() != null ? rel.getFirstChargeOverseas().toPlainString() : null;
            case "recharge_domestic":     return rel.getRechargeDomestic() != null ? rel.getRechargeDomestic().toPlainString() : null;
            case "recharge_overseas":     return rel.getRechargeOverseas() != null ? rel.getRechargeOverseas().toPlainString() : null;
            case "view_count":            return rel.getViewCount() != null ? rel.getViewCount().toString() : null;
            default: return null;
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // fill-in 策略：补全主表空字段
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * 根据每字段策略决定是否写入 incoming 的值到 existing 的字段。
     * <ul>
     *   <li>fill_empty（默认）：仅补全空字段，已有数据不覆盖</li>
     *   <li>overwrite：始终用最新导入数据覆盖</li>
     *   <li>skip：游戏已存在时始终跳过此字段，不进行任何更新</li>
     * </ul>
     *
     * @param existing       数据库中已存在的游戏记录
     * @param incoming       经字段映射后构建的新游戏对象
     * @param strategies     targetField → 策略映射（空时全部使用 fill_empty）
     * @return 含需更新字段的 GbGame（仅 id + 待进行更新的字段非 null），或 null
     */
    private GbGame buildFillUpdateGame(GbGame existing, GbGame incoming, Map<String, String> strategies) {
        GbGame u = new GbGame();
        u.setId(existing.getId());
        // name / subtitle / game_type 是判重键列，INSERT 阶段必须有值（避免 NOT NULL 约束或判重逻辑失效）；
        // 先从 existing 回填，再由 shouldWrite 决定是否改写为 incoming 的值
        u.setName(existing.getName());
        u.setSubtitle(existing.getSubtitle());
        u.setGameType(existing.getGameType());

        // String 类字段
        if (shouldWrite("name", existing.getName(), incoming.getName(), strategies))
            { u.setName(incoming.getName()); }
        if (shouldWrite("subtitle", existing.getSubtitle(), incoming.getSubtitle(), strategies))
            { u.setSubtitle(incoming.getSubtitle()); }
        if (shouldWrite("short_name", existing.getShortName(), incoming.getShortName(), strategies))
            { u.setShortName(incoming.getShortName()); }
        if (shouldWrite("game_type", existing.getGameType(), incoming.getGameType(), strategies))
            { u.setGameType(incoming.getGameType()); }
        if (shouldWrite("icon_url", existing.getIconUrl(), incoming.getIconUrl(), strategies))
            { u.setIconUrl(incoming.getIconUrl()); }
        if (shouldWrite("cover_url", existing.getCoverUrl(), incoming.getCoverUrl(), strategies))
            { u.setCoverUrl(incoming.getCoverUrl()); }
        if (shouldWrite("banner_url", existing.getBannerUrl(), incoming.getBannerUrl(), strategies))
            { u.setBannerUrl(incoming.getBannerUrl()); }
        if (shouldWrite("screenshots", existing.getScreenshots(), incoming.getScreenshots(), strategies))
            { u.setScreenshots(incoming.getScreenshots()); }
        if (shouldWrite("video_url", existing.getVideoUrl(), incoming.getVideoUrl(), strategies))
            { u.setVideoUrl(incoming.getVideoUrl()); }
        if (shouldWrite("description", existing.getDescription(), incoming.getDescription(), strategies))
            { u.setDescription(incoming.getDescription()); }
        if (shouldWrite("promotion_desc", existing.getPromotionDesc(), incoming.getPromotionDesc(), strategies))
            { u.setPromotionDesc(incoming.getPromotionDesc()); }
        if (shouldWrite("developer", existing.getDeveloper(), incoming.getDeveloper(), strategies))
            { u.setDeveloper(incoming.getDeveloper()); }
        if (shouldWrite("publisher", existing.getPublisher(), incoming.getPublisher(), strategies))
            { u.setPublisher(incoming.getPublisher()); }
        if (shouldWrite("package_name", existing.getPackageName(), incoming.getPackageName(), strategies))
            { u.setPackageName(incoming.getPackageName()); }
        if (shouldWrite("version", existing.getVersion(), incoming.getVersion(), strategies))
            { u.setVersion(incoming.getVersion()); }
        if (shouldWrite("size", existing.getSize(), incoming.getSize(), strategies))
            { u.setSize(incoming.getSize()); }
        if (shouldWrite("device_support", existing.getDeviceSupport(), incoming.getDeviceSupport(), strategies))
            { u.setDeviceSupport(incoming.getDeviceSupport()); }
        if (shouldWrite("features", existing.getFeatures(), incoming.getFeatures(), strategies))
            { u.setFeatures(incoming.getFeatures()); }
        if (shouldWrite("tags", existing.getTags(), incoming.getTags(), strategies))
            { u.setTags(incoming.getTags()); }
        if (shouldWrite("is_new", existing.getIsNew(), incoming.getIsNew(), strategies))
            { u.setIsNew(incoming.getIsNew()); }
        if (shouldWrite("is_hot", existing.getIsHot(), incoming.getIsHot(), strategies))
            { u.setIsHot(incoming.getIsHot()); }
        if (shouldWrite("is_recommend", existing.getIsRecommend(), incoming.getIsRecommend(), strategies))
            { u.setIsRecommend(incoming.getIsRecommend()); }
        if (shouldWrite("download_url", existing.getDownloadUrl(), incoming.getDownloadUrl(), strategies))
            { u.setDownloadUrl(incoming.getDownloadUrl()); }
        if (shouldWrite("android_url", existing.getAndroidUrl(), incoming.getAndroidUrl(), strategies))
            { u.setAndroidUrl(incoming.getAndroidUrl()); }
        if (shouldWrite("ios_url", existing.getIosUrl(), incoming.getIosUrl(), strategies))
            { u.setIosUrl(incoming.getIosUrl()); }
        if (shouldWrite("apk_url", existing.getApkUrl(), incoming.getApkUrl(), strategies))
            { u.setApkUrl(incoming.getApkUrl()); }
        if (shouldWrite("discount_label", existing.getDiscountLabel(), incoming.getDiscountLabel(), strategies))
            { u.setDiscountLabel(incoming.getDiscountLabel()); }
        if (shouldWrite("has_support", existing.getHasSupport(), incoming.getHasSupport(), strategies))
            { u.setHasSupport(incoming.getHasSupport()); }
        if (shouldWrite("support_desc", existing.getSupportDesc(), incoming.getSupportDesc(), strategies))
            { u.setSupportDesc(incoming.getSupportDesc()); }
        if (shouldWrite("has_low_deduct_coupon", existing.getHasLowDeductCoupon(), incoming.getHasLowDeductCoupon(), strategies))
            { u.setHasLowDeductCoupon(incoming.getHasLowDeductCoupon()); }
        if (shouldWrite("low_deduct_coupon_url", existing.getLowDeductCouponUrl(), incoming.getLowDeductCouponUrl(), strategies))
            { u.setLowDeductCouponUrl(incoming.getLowDeductCouponUrl()); }
        
        // 数值类字段
        if (shouldWriteObj("download_count", existing.getDownloadCount(), incoming.getDownloadCount(), strategies))
            { u.setDownloadCount(incoming.getDownloadCount()); }
        if (shouldWriteObj("rating", existing.getRating(), incoming.getRating(), strategies))
            { u.setRating(incoming.getRating()); }
        if (shouldWriteObj("launch_time", existing.getLaunchTime(), incoming.getLaunchTime(), strategies))
            { u.setLaunchTime(incoming.getLaunchTime()); }
        if (shouldWriteObj("first_charge_domestic", existing.getFirstChargeDomestic(), incoming.getFirstChargeDomestic(), strategies))
            { u.setFirstChargeDomestic(incoming.getFirstChargeDomestic()); }
        if (shouldWriteObj("first_charge_overseas", existing.getFirstChargeOverseas(), incoming.getFirstChargeOverseas(), strategies))
            { u.setFirstChargeOverseas(incoming.getFirstChargeOverseas()); }
        if (shouldWriteObj("recharge_domestic", existing.getRechargeDomestic(), incoming.getRechargeDomestic(), strategies))
            { u.setRechargeDomestic(incoming.getRechargeDomestic()); }
        if (shouldWriteObj("recharge_overseas", existing.getRechargeOverseas(), incoming.getRechargeOverseas(), strategies))
            { u.setRechargeOverseas(incoming.getRechargeOverseas()); }
            
        // 无论是否有字段变更，只要跳过导入逻辑（低优先级或全补全），都更新 update_time 以表示今天已经处理过该游戏
        u.setUpdateTime(new Date());
        
        return u;
    }

    /**
     * 构建高优先级覆盖的完整游戏快照。
     * 以现有游戏数据为基础，用 incoming 数据覆盖有映射的字段。
     * 没有映射（incoming 为 null）但策略为 overwrite 的字段会被置空。
     * 系统字段（status、del_flag、create_by、create_time、sort_order、remark）保持不变。
     *
     * @param existing   数据库中已存在的游戏记录
     * @param incoming   经字段映射后构建的新游戏对象
     * @param strategies targetField → 策略映射
     * @return 完整的 GbGame 快照，可直接用 overwriteGbGameBySnapshot 写入
     */
    private GbGame buildHighPriorityOverwriteGame(GbGame existing, GbGame incoming, Map<String, String> strategies) {
        GbGame g = new GbGame();
        g.setId(existing.getId());
        // ── 保留系统字段（不被导入覆盖） ──
        g.setSiteId(existing.getSiteId());
        g.setSortOrder(existing.getSortOrder());
        g.setStatus(existing.getStatus());
        g.setDelFlag(existing.getDelFlag());
        g.setCreateBy(existing.getCreateBy());
        g.setCreateTime(existing.getCreateTime());
        g.setUpdateBy(existing.getUpdateBy());
        g.setUpdateTime(new Date());
        g.setRemark(existing.getRemark());
        // ── 数据字段：有 incoming 值则用新值，无 incoming 值且策略为 overwrite 则置空 ──
        g.setName(pickOverwrite("name", existing.getName(), incoming.getName(), strategies));
        g.setSubtitle(pickOverwrite("subtitle", existing.getSubtitle(), incoming.getSubtitle(), strategies));
        g.setShortName(pickOverwrite("short_name", existing.getShortName(), incoming.getShortName(), strategies));
        g.setGameType(pickOverwrite("game_type", existing.getGameType(), incoming.getGameType(), strategies));
        g.setIconUrl(pickOverwrite("icon_url", existing.getIconUrl(), incoming.getIconUrl(), strategies));
        g.setCoverUrl(pickOverwrite("cover_url", existing.getCoverUrl(), incoming.getCoverUrl(), strategies));
        g.setBannerUrl(pickOverwrite("banner_url", existing.getBannerUrl(), incoming.getBannerUrl(), strategies));
        g.setScreenshots(pickOverwrite("screenshots", existing.getScreenshots(), incoming.getScreenshots(), strategies));
        g.setVideoUrl(pickOverwrite("video_url", existing.getVideoUrl(), incoming.getVideoUrl(), strategies));
        g.setDescription(pickOverwrite("description", existing.getDescription(), incoming.getDescription(), strategies));
        g.setPromotionDesc(pickOverwrite("promotion_desc", existing.getPromotionDesc(), incoming.getPromotionDesc(), strategies));
        g.setDeveloper(pickOverwrite("developer", existing.getDeveloper(), incoming.getDeveloper(), strategies));
        g.setPublisher(pickOverwrite("publisher", existing.getPublisher(), incoming.getPublisher(), strategies));
        g.setPackageName(pickOverwrite("package_name", existing.getPackageName(), incoming.getPackageName(), strategies));
        g.setVersion(pickOverwrite("version", existing.getVersion(), incoming.getVersion(), strategies));
        g.setSize(pickOverwrite("size", existing.getSize(), incoming.getSize(), strategies));
        g.setDeviceSupport(pickOverwrite("device_support", existing.getDeviceSupport(), incoming.getDeviceSupport(), strategies));
        g.setFeatures(pickOverwrite("features", existing.getFeatures(), incoming.getFeatures(), strategies));
        g.setTags(pickOverwrite("tags", existing.getTags(), incoming.getTags(), strategies));
        g.setIsNew(pickOverwrite("is_new", existing.getIsNew(), incoming.getIsNew(), strategies));
        g.setIsHot(pickOverwrite("is_hot", existing.getIsHot(), incoming.getIsHot(), strategies));
        g.setIsRecommend(pickOverwrite("is_recommend", existing.getIsRecommend(), incoming.getIsRecommend(), strategies));
        g.setDownloadUrl(pickOverwrite("download_url", existing.getDownloadUrl(), incoming.getDownloadUrl(), strategies));
        g.setAndroidUrl(pickOverwrite("android_url", existing.getAndroidUrl(), incoming.getAndroidUrl(), strategies));
        g.setIosUrl(pickOverwrite("ios_url", existing.getIosUrl(), incoming.getIosUrl(), strategies));
        g.setApkUrl(pickOverwrite("apk_url", existing.getApkUrl(), incoming.getApkUrl(), strategies));
        g.setDiscountLabel(pickOverwrite("discount_label", existing.getDiscountLabel(), incoming.getDiscountLabel(), strategies));
        g.setHasSupport(pickOverwrite("has_support", existing.getHasSupport(), incoming.getHasSupport(), strategies));
        g.setSupportDesc(pickOverwrite("support_desc", existing.getSupportDesc(), incoming.getSupportDesc(), strategies));
        g.setHasLowDeductCoupon(pickOverwrite("has_low_deduct_coupon", existing.getHasLowDeductCoupon(), incoming.getHasLowDeductCoupon(), strategies));
        g.setLowDeductCouponUrl(pickOverwrite("low_deduct_coupon_url", existing.getLowDeductCouponUrl(), incoming.getLowDeductCouponUrl(), strategies));
        // 数值类字段
        g.setDownloadCount(pickOverwriteObj("download_count", existing.getDownloadCount(), incoming.getDownloadCount(), strategies));
        g.setRating(pickOverwriteObj("rating", existing.getRating(), incoming.getRating(), strategies));
        g.setLaunchTime(pickOverwriteObj("launch_time", existing.getLaunchTime(), incoming.getLaunchTime(), strategies));
        g.setFirstChargeDomestic(pickOverwriteObj("first_charge_domestic", existing.getFirstChargeDomestic(), incoming.getFirstChargeDomestic(), strategies));
        g.setFirstChargeOverseas(pickOverwriteObj("first_charge_overseas", existing.getFirstChargeOverseas(), incoming.getFirstChargeOverseas(), strategies));
        g.setRechargeDomestic(pickOverwriteObj("recharge_domestic", existing.getRechargeDomestic(), incoming.getRechargeDomestic(), strategies));
        g.setRechargeOverseas(pickOverwriteObj("recharge_overseas", existing.getRechargeOverseas(), incoming.getRechargeOverseas(), strategies));
        return g;
    }

    /**
     * 高优先级覆盖时选值（String 类型）：
     * 根据字段策略决定最终值。因为是全行写入，每个字段都必须有明确的值。
     * - overwrite + incoming有值 → incoming
     * - overwrite + incoming为空 → null（置空）
     * - fill_empty + existing为空且incoming有值 → incoming
     * - fill_empty + 其他 → existing
     * - skip → existing
     */
    private String pickOverwrite(String fieldName, String existingVal, String incomingVal, Map<String, String> strategies) {
        String strategy = strategies.getOrDefault(fieldName, "overwrite");
        boolean incomingEmpty = incomingVal == null || incomingVal.trim().isEmpty();
        switch (strategy) {
            case "skip":
                return existingVal;
            case "fill_empty":
                if (isBlank(existingVal) && !incomingEmpty) return incomingVal;
                return existingVal;
            case "overwrite":
            default:
                if (!incomingEmpty) return incomingVal;
                return null; // 未映射的 overwrite 字段 → 置空
        }
    }

    /**
     * 高优先级覆盖时选值（Object 类型）：同 pickOverwrite 逻辑。
     */
    @SuppressWarnings("unchecked")
    private <T> T pickOverwriteObj(String fieldName, T existingVal, T incomingVal, Map<String, String> strategies) {
        String strategy = strategies.getOrDefault(fieldName, "overwrite");
        switch (strategy) {
            case "skip":
                return existingVal;
            case "fill_empty":
                if (existingVal == null && incomingVal != null) return incomingVal;
                return existingVal;
            case "overwrite":
            default:
                if (incomingVal != null) return incomingVal;
                return null;
        }
    }

    /**
     * 判断是否应将 incomingVal 写入到 existing 对应字段（String 类型）。
     * <ul>
     *   <li>skip：全不写</li>
     *   <li>overwrite：incomingVal 非空时写</li>
     *   <li>fill_empty（默认）：existingVal 为空且 incomingVal 非空时写</li>
     * </ul>
     */
    private boolean shouldWrite(String fieldName, String existingVal, String incomingVal,
                                Map<String, String> strategies) {
        if (incomingVal == null || incomingVal.trim().isEmpty()) return false;
        String strategy = strategies.getOrDefault(fieldName, "fill_empty");
        if ("skip".equals(strategy)) return false;
        if ("overwrite".equals(strategy)) return true;
        return isBlank(existingVal);
    }

    /**
     * 判断是否应将 incomingVal 写入到 existing 对应字段（非 String 类型）。
     */
    private boolean shouldWriteObj(String fieldName, Object existingVal, Object incomingVal,
                                   Map<String, String> strategies) {
        if (incomingVal == null) return false;
        String strategy = strategies.getOrDefault(fieldName, "fill_empty");
        if ("skip".equals(strategy)) return false;
        if ("overwrite".equals(strategy)) return true;
        return existingVal == null;
    }

    /** String 是否为空（null 或纯空白） */
    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    /**
     * 计算 UPDATE 操作后的完整快照：将 updateObj 中非 null 字段覆盖到 beforeJson 代表的状态上。
     * 避免部分更新对象序列化后不相关字段显示为空的误导。
     */
    @SuppressWarnings("unchecked")
    private static String mergeSnapshotJson(String beforeJson, Object updateObj) {
        if (beforeJson == null) return toSortedJson(updateObj);
        Map<String, Object> merged = JSON.parseObject(beforeJson, Map.class);
        Map<String, Object> delta  = JSON.parseObject(JSON.toJSONString(updateObj), Map.class);
        for (Map.Entry<String, Object> e : delta.entrySet()) {
            if (e.getValue() != null) merged.put(e.getKey(), e.getValue());
        }
        return toSortedJson(merged);
    }

    /**
     * 递归对 Map 键按字母升序后序列化，确保历史快照/diff 对比结果稳定。
     */
    @SuppressWarnings("unchecked")
    private static String toSortedJson(Object obj) {
        String raw = JSON.toJSONString(obj);
        Object parsed = JSON.parseObject(raw, Object.class);
        return JSON.toJSONString(sortKeys(parsed));
    }

    @SuppressWarnings("unchecked")
    private static Object sortKeys(Object obj) {
        if (obj instanceof Map) {
            TreeMap<String, Object> sorted = new TreeMap<>();
            ((Map<String, Object>) obj).forEach((k, v) -> sorted.put(k, sortKeys(v)));
            return sorted;
        }
        if (obj instanceof List) {
            List<Object> list = new ArrayList<>();
            ((List<?>) obj).forEach(item -> list.add(sortKeys(item)));
            return list;
        }
        return obj;
    }

    private void applyRelationFieldsToBoxGameRelation(GbBoxGameRelation relation,
                                                       Map<String, Object> fields) {
        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            String k = entry.getKey();
            Object v = entry.getValue();
            if (v == null) continue;
            try {
                switch (k) {
                    case "discount_label":         relation.setDiscountLabel(v.toString()); break;
                    case "first_charge_domestic":
                        if (v instanceof Number) relation.setFirstChargeDomestic(new BigDecimal(v.toString())); break;
                    case "first_charge_overseas":
                        if (v instanceof Number) relation.setFirstChargeOverseas(new BigDecimal(v.toString())); break;
                    case "recharge_domestic":
                        if (v instanceof Number) relation.setRechargeDomestic(new BigDecimal(v.toString())); break;
                    case "recharge_overseas":
                        if (v instanceof Number) relation.setRechargeOverseas(new BigDecimal(v.toString())); break;
                    case "has_support":            relation.setHasSupport(v.toString()); break;
                    case "support_desc":           relation.setSupportDesc(v.toString()); break;
                    case "download_url":           relation.setDownloadUrl(v.toString()); break;
                    case "promote_url":            relation.setPromoteUrl(v.toString()); break;
                    case "qrcode_url":             relation.setQrcodeUrl(v.toString()); break;
                    case "promote_text":           relation.setPromoteText(v.toString()); break;
                    case "poster_url":             relation.setPosterUrl(v.toString()); break;
                    case "is_featured":            relation.setIsFeatured(v.toString()); break;
                    case "is_exclusive":           relation.setIsExclusive(v.toString()); break;
                    case "is_new":                 relation.setIsNew(v.toString()); break;
                    case "custom_name":            relation.setCustomName(v.toString()); break;
                    case "custom_description":     relation.setCustomDescription(v.toString()); break;
                    case "custom_download_url":    relation.setCustomDownloadUrl(v.toString()); break;
                    case "view_count":
                        if (v instanceof Number) relation.setViewCount(((Number) v).intValue()); break;
                    case "sort_order":
                        if (v instanceof Number) relation.setSortOrder(((Number) v).intValue()); break;
                    default:
                        log.debug("[GameImport] 未处理的关联字段: {}", k);
                }
            } catch (Exception e) {
                log.warn("[GameImport] 应用关联字段 {} 失败: {}", k, e.getMessage());
            }
        }
    }
}
