package com.ruoyi.gamebox.service.impl;

import com.ruoyi.gamebox.domain.GbImportBatch;
import com.ruoyi.gamebox.mapper.GbImportBatchMapper;
import com.ruoyi.gamebox.service.IGbImportBatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 游戏导入批次 服务实现
 *
 * @author ruoyi
 */
@Service
public class GbImportBatchServiceImpl implements IGbImportBatchService {

    @Autowired
    private GbImportBatchMapper importBatchMapper;

    @Override
    public List<GbImportBatch> selectList(GbImportBatch query) {
        return importBatchMapper.selectList(query);
    }

    @Override
    public GbImportBatch selectById(Long id) {
        return importBatchMapper.selectById(id);
    }

    @Override
    public GbImportBatch createBatch(Long boxId, String boxName, Long siteId, String siteName,
                                      String platformType, String operator) {
        GbImportBatch batch = new GbImportBatch();
        batch.setBatchNo(UUID.randomUUID().toString().replace("-", "").substring(0, 32));
        batch.setBoxId(boxId);
        batch.setBoxName(boxName);
        batch.setSiteId(siteId);
        batch.setSiteName(siteName);
        batch.setPlatformType(platformType);
        batch.setTotalCount(0);
        batch.setNewCount(0);
        batch.setUpdatedCount(0);
        batch.setSkippedCount(0);
        batch.setFailedCount(0);
        batch.setStatus("running");
        batch.setReverted(0);
        batch.setCreateTime(new Date());
        batch.setCreateBy(operator);
        importBatchMapper.insert(batch);
        return batch;
    }

    @Override
    public void finishBatch(Long batchId, int total, int newCnt, int updated, int skipped, int failed, String summary) {
        GbImportBatch update = new GbImportBatch();
        update.setId(batchId);
        update.setTotalCount(total);
        update.setNewCount(newCnt);
        update.setUpdatedCount(updated);
        update.setSkippedCount(skipped);
        update.setFailedCount(failed);
        update.setStatus(failed > 0 ? "partial_failed" : "completed");
        update.setSummary(summary);
        importBatchMapper.update(update);
    }

    @Override
    public void markReverted(Long batchId) {
        importBatchMapper.markReverted(batchId);
    }

    @Override
    public void markUnreverted(Long batchId) {
        GbImportBatch update = new GbImportBatch();
        update.setId(batchId);
        update.setReverted(0);
        importBatchMapper.update(update);
    }
}
