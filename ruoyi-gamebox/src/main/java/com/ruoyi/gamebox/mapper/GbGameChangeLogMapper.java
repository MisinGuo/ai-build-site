package com.ruoyi.gamebox.mapper;

import com.ruoyi.gamebox.domain.GbGameChangeLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 游戏字段变更日志 Mapper 接口
 *
 * @author ruoyi
 */
@Mapper
public interface GbGameChangeLogMapper {

    /** 按 ID 查询 */
    GbGameChangeLog selectById(Long id);

    /** 按批次 ID 查询变更列表 */
    List<GbGameChangeLog> selectByBatchId(Long batchId);

    /** 按游戏 ID 查询变更历史（时间倒序） */
    List<GbGameChangeLog> selectByGameId(Long gameId);

    /** 按批次 ID 查询指定目标表的变更列表 */
    List<GbGameChangeLog> selectByBatchIdAndTable(@Param("batchId") Long batchId,
                                                   @Param("targetTable") String targetTable);

    /** 按批次 ID 和游戏 ID 查询变更列表 */
    List<GbGameChangeLog> selectByBatchIdAndGameId(@Param("batchId") Long batchId,
                                                    @Param("gameId") Long gameId);

    /** 按批次号查询变更列表 */
    List<GbGameChangeLog> selectByBatchNo(@Param("batchNo") String batchNo);

    /** 按批次号和游戏ID查询变更列表 */
    List<GbGameChangeLog> selectByBatchNoAndGameId(@Param("batchNo") String batchNo,
                                                   @Param("gameId") Long gameId);

    /** 批量插入变更日志 */
    int batchInsert(List<GbGameChangeLog> logs);

    /** 单条插入 */
    int insert(GbGameChangeLog log);

    /** 标记单条变更已撤回 */
    int markReverted(@Param("id") Long id,
                     @Param("revertBy") String revertBy);

    /** 统计批次中未撤回的条数 */
    int countNotReverted(@Param("batchId") Long batchId);

    /** 统计批次号中未撤回的条数 */
    int countNotRevertedByBatchNo(@Param("batchNo") String batchNo);

    /** 取消单条变更的撤回标记（重新应用时使用） */
    int markUnreverted(@Param("id") Long id,
                       @Param("revertBy") String revertBy);
}
