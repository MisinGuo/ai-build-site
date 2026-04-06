package com.ruoyi.gamebox.service;

import com.ruoyi.gamebox.domain.GbImportBatch;

import java.util.List;

/**
 * 游戏导入批次 服务接口
 *
 * @author ruoyi
 */
public interface IGbImportBatchService {

    /** 分页列表查询 */
    List<GbImportBatch> selectList(GbImportBatch query);

    /** 按 ID 查询 */
    GbImportBatch selectById(Long id);

    /** 创建新批次记录，返回带ID的实体 */
    GbImportBatch createBatch(Long boxId, String boxName, Long siteId, String siteName, String platformType, String operator);

    /** 完成批次——更新统计数字与状态 */
    void finishBatch(Long batchId, int total, int newCnt, int updated, int skipped, int failed, String summary);

    /** 标记整个批次已撤回 */
    void markReverted(Long batchId);

    /** 取消批次撤回标记（重新应用后） */
    void markUnreverted(Long batchId);
}
