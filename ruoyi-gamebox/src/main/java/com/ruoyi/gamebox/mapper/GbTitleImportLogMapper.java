package com.ruoyi.gamebox.mapper;

import com.ruoyi.gamebox.domain.GbTitleImportLog;
import java.util.List;

/**
 * 标题导入日志Mapper接口
 * 
 * @author ruoyi
 * @date 2025-12-30
 */
public interface GbTitleImportLogMapper 
{
    /**
     * 查询标题导入日志
     * 
     * @param id 标题导入日志主键
     * @return 标题导入日志
     */
    public GbTitleImportLog selectGbTitleImportLogById(Long id);

    /**
     * 查询标题导入日志列表
     * 
     * @param importBatch 导入批次号
     * @return 标题导入日志集合
     */
    public List<GbTitleImportLog> selectGbTitleImportLogList(String importBatch);

    /**
     * 新增标题导入日志
     * 
     * @param gbTitleImportLog 标题导入日志
     * @return 结果
     */
    public int insertGbTitleImportLog(GbTitleImportLog gbTitleImportLog);

    /**
     * 修改标题导入日志
     * 
     * @param gbTitleImportLog 标题导入日志
     * @return 结果
     */
    public int updateGbTitleImportLog(GbTitleImportLog gbTitleImportLog);

    /**
     * 统计导入总次数
     * 
     * @return 数量
     */
    public int countTotalImports();
}
