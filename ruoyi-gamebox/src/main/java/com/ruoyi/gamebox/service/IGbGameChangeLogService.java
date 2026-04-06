package com.ruoyi.gamebox.service;

import com.ruoyi.gamebox.domain.GbGameChangeLog;

import java.util.List;

/**
 * 游戏字段变更日志 服务接口
 *
 * @author ruoyi
 */
public interface IGbGameChangeLogService {

    /** 按批次 ID 查询全部变更记录 */
    List<GbGameChangeLog> selectByBatchId(Long batchId);

    /** 按游戏 ID 查询历史变更（时间倒序） */
    List<GbGameChangeLog> selectByGameId(Long gameId);

    /** 按 ID 查询单条记录 */
    GbGameChangeLog selectById(Long id);

    /** 批量保存变更日志 */
    void saveLogs(List<GbGameChangeLog> logs);

    /**
     * 撤回单条变更记录（按快照恢复数据）。
     *
     * @param id       变更日志 ID
     * @param operator 操作人
     */
    void revertChange(Long id, String operator);

    /**
     * 撤回指定批次的全部未撤回变更（按逆序，先撤关联再撤主表）。
     *
     * @param batchId  批次 ID
     * @param operator 操作人
     * @return 实际撤回条数
     */
    int revertBatch(Long batchId, String operator);

    /**
     * 重新应用单条已撤回变更（将 after_snapshot 写回数据库）。
     *
     * @param id       变更日志 ID
     * @param operator 操作人
     */
    void reApplyChange(Long id, String operator);

    /**
     * 重新应用指定批次的全部已撤回变更（按正序，先应用主表再应用关联表）。
     *
     * @param batchId  批次 ID
     * @param operator 操作人
     * @return 实际重新应用条数
     */
    int reApplyBatch(Long batchId, String operator);

    /**
     * 撤回指定批次中指定游戏的全部未撤回变更（先撤关联表再撤主表）。
     *
     * @param batchId  批次 ID
     * @param gameId   游戏 ID
     * @param operator 操作人
     * @return 实际撤回条数
     */
    int revertByBatchAndGame(Long batchId, Long gameId, String operator);

    /**
     * 重新应用指定批次中指定游戏的全部已撤回变更（先应用主表再应用关联表）。
     *
     * @param batchId  批次 ID
     * @param gameId   游戏 ID
     * @param operator 操作人
     * @return 实际重新应用条数
     */
    int reApplyByBatchAndGame(Long batchId, Long gameId, String operator);

    /** 按批次号查询全部变更记录（用于手工批次） */
    List<GbGameChangeLog> selectByBatchNo(String batchNo);

    /** 撤回指定批次号的全部未撤回变更 */
    int revertBatchByBatchNo(String batchNo, String operator);

    /** 重新应用指定批次号的全部已撤回变更 */
    int reApplyBatchByBatchNo(String batchNo, String operator);

    /** 撤回指定批次号中指定游戏的全部未撤回变更 */
    int revertByBatchNoAndGame(String batchNo, Long gameId, String operator);

    /** 重新应用指定批次号中指定游戏的全部已撤回变更 */
    int reApplyByBatchNoAndGame(String batchNo, Long gameId, String operator);
}
