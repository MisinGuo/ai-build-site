package com.ruoyi.gamebox.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.gamebox.domain.GbBoxGameRelation;
import com.ruoyi.gamebox.domain.GbGameCategoryRelation;
import com.ruoyi.gamebox.domain.GbGame;
import com.ruoyi.gamebox.domain.GbGameChangeLog;
import com.ruoyi.gamebox.mapper.GbBoxGameRelationMapper;
import com.ruoyi.gamebox.mapper.GbGameChangeLogMapper;
import com.ruoyi.gamebox.mapper.GbGameMapper;
import com.ruoyi.gamebox.service.IGbGameChangeLogService;
import com.ruoyi.gamebox.service.IGbBoxGameRelationService;
import com.ruoyi.gamebox.service.IGbGameCategoryRelationService;
import com.ruoyi.gamebox.service.IGbGameService;
import com.ruoyi.gamebox.service.IGbImportBatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

/**
 * 游戏字段变更日志 服务实现
 *
 * @author ruoyi
 */
@Service
public class GbGameChangeLogServiceImpl implements IGbGameChangeLogService {

    private static final Logger log = LoggerFactory.getLogger(GbGameChangeLogServiceImpl.class);

    private static final String TABLE_GAMES    = "gb_games";
    private static final String TABLE_RELATION = "gb_box_game_relations";
    private static final String TABLE_GAME_CATEGORIES = "gb_game_category_relations";

    @Autowired
    private GbGameChangeLogMapper changeLogMapper;

    @Autowired
    private IGbGameService gameService;

    @Autowired
    private IGbBoxGameRelationService boxGameRelationService;

    @Autowired
    private IGbImportBatchService importBatchService;

    @Autowired
    private GbGameMapper gameMapper;

    @Autowired
    private GbBoxGameRelationMapper boxGameRelationMapper;

    @Autowired
    private IGbGameCategoryRelationService gameCategoryRelationService;

    // ──────────────────────────────────────────────────────────────────────────
    // 查询
    // ──────────────────────────────────────────────────────────────────────────

    @Override
    public List<GbGameChangeLog> selectByBatchId(Long batchId) {
        return changeLogMapper.selectByBatchId(batchId);
    }

    @Override
    public List<GbGameChangeLog> selectByBatchNo(String batchNo) {
        return changeLogMapper.selectByBatchNo(batchNo);
    }

    @Override
    public List<GbGameChangeLog> selectByGameId(Long gameId) {
        return changeLogMapper.selectByGameId(gameId);
    }

    @Override
    public GbGameChangeLog selectById(Long id) {
        return changeLogMapper.selectById(id);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 写入
    // ──────────────────────────────────────────────────────────────────────────

    @Override
    public void saveLogs(List<GbGameChangeLog> logs) {
        if (logs == null || logs.isEmpty()) return;
        changeLogMapper.batchInsert(logs);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 撤回（单条）
    // ──────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void revertChange(Long id, String operator) {
        GbGameChangeLog log2 = changeLogMapper.selectById(id);
        if (log2 == null) throw new RuntimeException("变更记录不存在：id=" + id);
        if (log2.getReverted() != null && log2.getReverted() == 1) {
            throw new RuntimeException("该变更已撤回，无需重复操作");
        }
        doRevert(log2, operator);
        changeLogMapper.markReverted(id, operator);
        log.info("[ChangeLog] 单条撤回成功 id={}, table={}, targetId={}", id, log2.getTargetTable(), log2.getTargetId());
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 撤回（批次）
    // ──────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int revertBatch(Long batchId, String operator) {
        List<GbGameChangeLog> logs = changeLogMapper.selectByBatchId(batchId);
        if (logs == null || logs.isEmpty()) return 0;

        // 先撤 gb_box_game_relations（关联表），再撤 gb_games（主表），
        // 同为主表时 INSERT 后于 UPDATE 撤回（先撤晚发生的变更）
        logs.sort(Comparator
                .<GbGameChangeLog>comparingInt(l -> TABLE_GAMES.equals(l.getTargetTable()) ? 1 : 0)
                .thenComparing(Comparator.comparingLong(GbGameChangeLog::getId).reversed()));

        int reverted = 0;
        for (GbGameChangeLog entry : logs) {
            if (entry.getReverted() != null && entry.getReverted() == 1) continue;
            try {
                doRevert(entry, operator);
                changeLogMapper.markReverted(entry.getId(), operator);
                reverted++;
            } catch (Exception e) {
                log.error("[ChangeLog] 批次撤回中单条失败 id={}: {}", entry.getId(), e.getMessage(), e);
            }
        }

        // 如果全部撤回，标记批次为已撤回
        if (changeLogMapper.countNotReverted(batchId) == 0) {
            importBatchService.markReverted(batchId);
        }
        return reverted;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 重新应用（单条）
    // ──────────────────────────────────────────────────────────────────────

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reApplyChange(Long id, String operator) {
        GbGameChangeLog log2 = changeLogMapper.selectById(id);
        if (log2 == null) throw new RuntimeException("变更记录不存在：id=" + id);
        if (log2.getReverted() == null || log2.getReverted() != 1) {
            throw new RuntimeException("该变更未处于撤回状态，无法重新应用");
        }
        doReApply(log2, operator);
        changeLogMapper.markUnreverted(id, operator);
        log.info("[ChangeLog] 单条重新应用成功 id={}, table={}, targetId={}", id, log2.getTargetTable(), log2.getTargetId());
    }

    // ──────────────────────────────────────────────────────────────────────
    // 重新应用（批次）
    // ──────────────────────────────────────────────────────────────────────

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int reApplyBatch(Long batchId, String operator) {
        List<GbGameChangeLog> logs = changeLogMapper.selectByBatchId(batchId);
        if (logs == null || logs.isEmpty()) return 0;

        // 重新应用时正向顺序：先应用主表（gb_games），再应用关联表（gb_box_game_relations）
        logs.sort(Comparator
                .<GbGameChangeLog>comparingInt(l -> TABLE_GAMES.equals(l.getTargetTable()) ? 0 : 1)
                .thenComparing(Comparator.comparingLong(GbGameChangeLog::getId)));

        int applied = 0;
        for (GbGameChangeLog entry : logs) {
            if (entry.getReverted() == null || entry.getReverted() != 1) continue;
            try {
                doReApply(entry, operator);
                changeLogMapper.markUnreverted(entry.getId(), operator);
                applied++;
            } catch (Exception e) {
                log.error("[ChangeLog] 批量重新应用中单条失败 id={}: {}", entry.getId(), e.getMessage(), e);
            }
        }

        if (applied > 0) {
            importBatchService.markUnreverted(batchId);
        }
        return applied;
    }

    // ──────────────────────────────────────────────────────────────────────
    // 核心重新应用逻辑
    // ──────────────────────────────────────────────────────────────────────

    private void doReApply(GbGameChangeLog entry, String operator) {
        String changeType  = entry.getChangeType();
        String targetTable = entry.getTargetTable();
        Long   targetId    = entry.getTargetId();
        String after       = entry.getAfterSnapshot();

        if (after == null || after.isEmpty()) {
            log.warn("[ChangeLog] 重新应用但 after_snapshot 为空，跳过 id={}", entry.getId());
            return;
        }

        if ("INSERT".equals(changeType)) {
            // INSERT 重新应用：
            // gb_games 是软删除（del_flag='2'），记录仍在，直接 updateGbGame 恢复所有字段（含 del_flag='0'）
            // gb_box_game_relations 是物理删除，需要重新 INSERT
            if (TABLE_GAMES.equals(targetTable)) {
                GbGame game = JSON.parseObject(after, GbGame.class);
                game.setId(targetId);
                gameMapper.overwriteGbGameBySnapshot(game);
            } else if (TABLE_RELATION.equals(targetTable)) {
                GbBoxGameRelation relation = JSON.parseObject(after, GbBoxGameRelation.class);
                relation.setId(targetId);
                GbBoxGameRelation existing = boxGameRelationService.selectGbBoxGameRelationById(targetId);
                if (existing == null) {
                    boxGameRelationService.insertGbBoxGameRelation(relation);
                } else {
                    boxGameRelationMapper.overwriteGbBoxGameRelationBySnapshot(relation);
                }
            } else if (TABLE_GAME_CATEGORIES.equals(targetTable)) {
                List<GbGameCategoryRelation> restoredRelations = parseCategoryRelationsSnapshot(after, entry.getGameId());
                gameCategoryRelationService.saveGameCategoryRelations(entry.getGameId(), restoredRelations);
            }
        } else if ("UPDATE".equals(changeType)) {
            // UPDATE 重新应用 = 再次写入 after_snapshot
            if (TABLE_GAMES.equals(targetTable)) {
                GbGame game = JSON.parseObject(after, GbGame.class);
                game.setId(targetId);
                gameMapper.overwriteGbGameBySnapshot(game);
            } else if (TABLE_RELATION.equals(targetTable)) {
                GbBoxGameRelation relation = JSON.parseObject(after, GbBoxGameRelation.class);
                relation.setId(targetId);
                boxGameRelationMapper.overwriteGbBoxGameRelationBySnapshot(relation);
            } else if (TABLE_GAME_CATEGORIES.equals(targetTable)) {
                List<GbGameCategoryRelation> restoredRelations = parseCategoryRelationsSnapshot(after, entry.getGameId());
                gameCategoryRelationService.saveGameCategoryRelations(entry.getGameId(), restoredRelations);
            }
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 撤回（批次 + 指定游戏）
    // ──────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int revertByBatchAndGame(Long batchId, Long gameId, String operator) {
        List<GbGameChangeLog> logs = changeLogMapper.selectByBatchIdAndGameId(batchId, gameId);
        if (logs == null || logs.isEmpty()) return 0;

        // 先撤关联表（gb_box_game_relations），再撤主表（gb_games）
        logs.sort(Comparator
                .<GbGameChangeLog>comparingInt(l -> TABLE_GAMES.equals(l.getTargetTable()) ? 1 : 0)
                .thenComparing(Comparator.comparingLong(GbGameChangeLog::getId).reversed()));

        int reverted = 0;
        for (GbGameChangeLog entry : logs) {
            if (entry.getReverted() != null && entry.getReverted() == 1) continue;
            try {
                doRevert(entry, operator);
                changeLogMapper.markReverted(entry.getId(), operator);
                reverted++;
            } catch (Exception e) {
                log.error("[ChangeLog] 游戏批次撤回中单条失败 id={}: {}", entry.getId(), e.getMessage(), e);
            }
        }

        // 若该批次内全部变更均已撤回，则标记批次为已撤回
        if (reverted > 0 && changeLogMapper.countNotReverted(batchId) == 0) {
            importBatchService.markReverted(batchId);
        }
        return reverted;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 重新应用（批次 + 指定游戏）
    // ──────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int reApplyByBatchAndGame(Long batchId, Long gameId, String operator) {
        List<GbGameChangeLog> logs = changeLogMapper.selectByBatchIdAndGameId(batchId, gameId);
        if (logs == null || logs.isEmpty()) return 0;

        // 先应用主表（gb_games），再应用关联表（gb_box_game_relations）
        logs.sort(Comparator
                .<GbGameChangeLog>comparingInt(l -> TABLE_GAMES.equals(l.getTargetTable()) ? 0 : 1)
                .thenComparing(Comparator.comparingLong(GbGameChangeLog::getId)));

        int applied = 0;
        for (GbGameChangeLog entry : logs) {
            if (entry.getReverted() == null || entry.getReverted() != 1) continue;
            try {
                doReApply(entry, operator);
                changeLogMapper.markUnreverted(entry.getId(), operator);
                applied++;
            } catch (Exception e) {
                log.error("[ChangeLog] 游戏批次重新应用中单条失败 id={}: {}", entry.getId(), e.getMessage(), e);
            }
        }

        if (applied > 0) {
            importBatchService.markUnreverted(batchId);
        }
        return applied;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int revertBatchByBatchNo(String batchNo, String operator) {
        List<GbGameChangeLog> logs = changeLogMapper.selectByBatchNo(batchNo);
        if (logs == null || logs.isEmpty()) return 0;

        logs.sort(Comparator
                .<GbGameChangeLog>comparingInt(l -> TABLE_GAMES.equals(l.getTargetTable()) ? 1 : 0)
                .thenComparing(Comparator.comparingLong(GbGameChangeLog::getId).reversed()));

        int reverted = 0;
        for (GbGameChangeLog entry : logs) {
            if (entry.getReverted() != null && entry.getReverted() == 1) continue;
            try {
                doRevert(entry, operator);
                changeLogMapper.markReverted(entry.getId(), operator);
                reverted++;
            } catch (Exception e) {
                log.error("[ChangeLog] 批次号撤回中单条失败 id={}: {}", entry.getId(), e.getMessage(), e);
            }
        }
        return reverted;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int reApplyBatchByBatchNo(String batchNo, String operator) {
        List<GbGameChangeLog> logs = changeLogMapper.selectByBatchNo(batchNo);
        if (logs == null || logs.isEmpty()) return 0;

        logs.sort(Comparator
                .<GbGameChangeLog>comparingInt(l -> TABLE_GAMES.equals(l.getTargetTable()) ? 0 : 1)
                .thenComparing(Comparator.comparingLong(GbGameChangeLog::getId)));

        int applied = 0;
        for (GbGameChangeLog entry : logs) {
            if (entry.getReverted() == null || entry.getReverted() != 1) continue;
            try {
                doReApply(entry, operator);
                changeLogMapper.markUnreverted(entry.getId(), operator);
                applied++;
            } catch (Exception e) {
                log.error("[ChangeLog] 批次号重新应用中单条失败 id={}: {}", entry.getId(), e.getMessage(), e);
            }
        }
        return applied;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int revertByBatchNoAndGame(String batchNo, Long gameId, String operator) {
        List<GbGameChangeLog> logs = changeLogMapper.selectByBatchNoAndGameId(batchNo, gameId);
        if (logs == null || logs.isEmpty()) return 0;

        logs.sort(Comparator
                .<GbGameChangeLog>comparingInt(l -> TABLE_GAMES.equals(l.getTargetTable()) ? 1 : 0)
                .thenComparing(Comparator.comparingLong(GbGameChangeLog::getId).reversed()));

        int reverted = 0;
        for (GbGameChangeLog entry : logs) {
            if (entry.getReverted() != null && entry.getReverted() == 1) continue;
            try {
                doRevert(entry, operator);
                changeLogMapper.markReverted(entry.getId(), operator);
                reverted++;
            } catch (Exception e) {
                log.error("[ChangeLog] 批次号+游戏撤回中单条失败 id={}: {}", entry.getId(), e.getMessage(), e);
            }
        }
        return reverted;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int reApplyByBatchNoAndGame(String batchNo, Long gameId, String operator) {
        List<GbGameChangeLog> logs = changeLogMapper.selectByBatchNoAndGameId(batchNo, gameId);
        if (logs == null || logs.isEmpty()) return 0;

        logs.sort(Comparator
                .<GbGameChangeLog>comparingInt(l -> TABLE_GAMES.equals(l.getTargetTable()) ? 0 : 1)
                .thenComparing(Comparator.comparingLong(GbGameChangeLog::getId)));

        int applied = 0;
        for (GbGameChangeLog entry : logs) {
            if (entry.getReverted() == null || entry.getReverted() != 1) continue;
            try {
                doReApply(entry, operator);
                changeLogMapper.markUnreverted(entry.getId(), operator);
                applied++;
            } catch (Exception e) {
                log.error("[ChangeLog] 批次号+游戏重新应用中单条失败 id={}: {}", entry.getId(), e.getMessage(), e);
            }
        }
        return applied;
    }

    private void doRevert(GbGameChangeLog entry, String operator) {
        String changeType  = entry.getChangeType();
        String targetTable = entry.getTargetTable();
        Long   targetId    = entry.getTargetId();

        if ("INSERT".equals(changeType)) {
            // INSERT 撤回 = 删除新增的记录
            if (TABLE_GAMES.equals(targetTable)) {
                gameService.deleteGbGameById(targetId);
            } else if (TABLE_RELATION.equals(targetTable)) {
                // 删除关联行
                boxGameRelationService.deleteGbBoxGameRelationById(targetId);
            }
        } else if ("UPDATE".equals(changeType)) {
            // UPDATE 撤回 = 将 before_snapshot 恢复到数据库
            String before = entry.getBeforeSnapshot();
            if (before == null || before.isEmpty()) {
                log.warn("[ChangeLog] UPDATE 撤回但 before_snapshot 为空，跳过: id={}", entry.getId());
                return;
            }
            if (TABLE_GAMES.equals(targetTable)) {
                GbGame restored = JSON.parseObject(before, GbGame.class);
                restored.setId(targetId);
                gameMapper.overwriteGbGameBySnapshot(restored);
            } else if (TABLE_RELATION.equals(targetTable)) {
                GbBoxGameRelation restored = JSON.parseObject(before, GbBoxGameRelation.class);
                restored.setId(targetId);
                boxGameRelationMapper.overwriteGbBoxGameRelationBySnapshot(restored);
            } else if (TABLE_GAME_CATEGORIES.equals(targetTable)) {
                List<GbGameCategoryRelation> restoredRelations = parseCategoryRelationsSnapshot(before, entry.getGameId());
                gameCategoryRelationService.saveGameCategoryRelations(entry.getGameId(), restoredRelations);
            }
        }
    }

    private List<GbGameCategoryRelation> parseCategoryRelationsSnapshot(String snapshot, Long gameId) {
        List<GbGameCategoryRelation> relations = new ArrayList<>();
        if (snapshot == null || snapshot.isEmpty() || gameId == null) {
            return relations;
        }
        JSONObject root = JSON.parseObject(snapshot);
        if (root == null) {
            return relations;
        }
        JSONArray categories = root.getJSONArray("categories");
        if (categories == null || categories.isEmpty()) {
            return relations;
        }
        for (int i = 0; i < categories.size(); i++) {
            JSONObject item = categories.getJSONObject(i);
            if (item == null || item.getLong("categoryId") == null) {
                continue;
            }
            GbGameCategoryRelation relation = new GbGameCategoryRelation();
            relation.setGameId(gameId);
            relation.setCategoryId(item.getLong("categoryId"));
            relation.setSortOrder(item.getInteger("sortOrder") == null ? i : item.getInteger("sortOrder"));
            relation.setIsPrimary(item.getString("isPrimary") == null ? "0" : item.getString("isPrimary"));
            relations.add(relation);
        }
        return relations;
    }
}
