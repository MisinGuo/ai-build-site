package com.ruoyi.gamebox.mapper;

import com.ruoyi.gamebox.domain.GbImportBatch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 游戏导入批次 Mapper 接口
 *
 * @author ruoyi
 */
@Mapper
public interface GbImportBatchMapper {

    /** 查询单个批次 */
    GbImportBatch selectById(Long id);

    /** 按批次号查询 */
    GbImportBatch selectByBatchNo(String batchNo);

    /** 分页列表查询 */
    List<GbImportBatch> selectList(GbImportBatch query);

    /** 插入批次记录，ID 回填 */
    int insert(GbImportBatch batch);

    /** 更新批次（用于导入完成后回填计数、状态） */
    int update(GbImportBatch batch);

    /** 标记批次整体撤回 */
    int markReverted(@Param("id") Long id);
}
