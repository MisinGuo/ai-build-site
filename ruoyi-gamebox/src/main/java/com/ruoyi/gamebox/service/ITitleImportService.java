package com.ruoyi.gamebox.service;

import com.ruoyi.gamebox.domain.GbTitleImportLog;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 标题导入Service接口
 * 
 * @author ruoyi
 * @date 2025-12-30
 */
public interface ITitleImportService 
{
    /**
     * 导入JSON数据
     * 
     * @param jsonData JSON数据字符串
     * @param siteId 所属网站ID
     * @param categoryId 所属分类ID
     * @param batchName 批次名称(可选)
     * @param sourceName 数据来源名称
     * @return 导入日志
     */
    GbTitleImportLog importFromJson(String jsonData, Long siteId, Long categoryId, String batchName, String sourceName);

    /**
     * 导入Excel文件
     * 
     * @param file Excel文件
     * @param siteId 所属网站ID
     * @param categoryId 所属分类ID
     * @param batchName 批次名称(可选)
     * @param sourceName 数据来源名称
     * @return 导入日志
     */
    GbTitleImportLog importFromExcel(MultipartFile file, Long siteId, Long categoryId, String batchName, String sourceName);

    /**
     * 查询导入历史
     * 
     * @param importBatch 导入批次号(可选)
     * @return 导入日志列表
     */
    List<GbTitleImportLog> queryImportHistory(String importBatch);

    /**
     * 获取导入统计
     * 
     * @return 统计信息
     */
    Map<String, Object> getImportStatistics();
}
