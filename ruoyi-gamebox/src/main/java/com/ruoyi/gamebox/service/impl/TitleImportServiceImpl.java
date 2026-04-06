package com.ruoyi.gamebox.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.gamebox.domain.GbArticleTitlePool;
import com.ruoyi.gamebox.domain.GbTitleImportLog;
import com.ruoyi.gamebox.domain.GbTitlePoolBatch;
import com.ruoyi.gamebox.mapper.GbArticleTitlePoolMapper;
import com.ruoyi.gamebox.mapper.GbTitleImportLogMapper;
import com.ruoyi.gamebox.mapper.GbTitlePoolBatchMapper;
import com.ruoyi.gamebox.service.ITitleImportService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 标题导入Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-12-30
 */
@Service
public class TitleImportServiceImpl implements ITitleImportService 
{
    private static final Logger log = LoggerFactory.getLogger(TitleImportServiceImpl.class);

    @Autowired
    private GbArticleTitlePoolMapper titlePoolMapper;

    @Autowired
    private GbTitleImportLogMapper importLogMapper;

    @Autowired
    private GbTitlePoolBatchMapper batchMapper;

    /**
     * 导入JSON数据
     */
    @Override
    @Transactional
    public GbTitleImportLog importFromJson(String jsonData, Long siteId, Long categoryId, String batchName, String sourceName) 
    {
        // 参数验证
        if (siteId == null) {
            throw new RuntimeException("网站ID不能为空");
        }
        
        String importDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        
        // 生成批次号
        String batchCode = batchMapper.generateBatchCode(siteId, importDate);
        
        // 创建批次记录
        GbTitlePoolBatch batch = new GbTitlePoolBatch();
        batch.setSiteId(siteId);
        batch.setCategoryId(categoryId);
        batch.setBatchCode(batchCode);
        batch.setBatchName(StringUtils.isNotBlank(batchName) ? batchName : "JSON导入-" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        batch.setImportDate(DateUtils.getNowDate());
        batch.setImportSource("json");
        batch.setTitleCount(0);
        batch.setStatus("1");
        batch.setCreateBy(SecurityUtils.getUsername());
        batch.setCreateTime(DateUtils.getNowDate());
        batchMapper.insertGbTitlePoolBatch(batch);
        
        // 生成导入批次号（使用批次code）
        String importBatch = batchCode;
        
        // 创建导入日志
        GbTitleImportLog importLog = new GbTitleImportLog();
        importLog.setImportBatch(importBatch);
        importLog.setImportType("json");
        importLog.setImportStatus("processing");
        importLog.setCreateBy(SecurityUtils.getUsername());
        importLog.setCreateTime(DateUtils.getNowDate());
        importLogMapper.insertGbTitleImportLog(importLog);

        int totalCount = 0;
        int successCount = 0;
        int failedCount = 0;
        int duplicateCount = 0;
        List<String> errorMessages = new ArrayList<>();

        try {
            // 解析JSON数据
            JSONArray jsonArray = JSON.parseArray(jsonData);
            totalCount = jsonArray.size();

            for (int i = 0; i < jsonArray.size(); i++) {
                try {
                    JSONObject item = jsonArray.getJSONObject(i);
                    
                    // 创建标题对象
                    GbArticleTitlePool titlePool = new GbArticleTitlePool();
                    // 注意：site_id和category_id由批次管理，不在标题对象中直接设置
                    titlePool.setTitle(item.getString("title"));
                    titlePool.setKeywords(item.getString("keywords"));
                    titlePool.setReferenceContent(item.getString("content"));
                    titlePool.setSourceName(StringUtils.isNotBlank(sourceName) ? sourceName : item.getString("source"));
                    titlePool.setSourceUrl(item.getString("url"));
                    titlePool.setImportBatch(importBatch);
                    titlePool.setUsageStatus("0");
                    titlePool.setUsedCount(0);
                    titlePool.setPriority(item.getInteger("priority") != null ? item.getInteger("priority") : 0);
                    titlePool.setTags(item.getString("tags"));
                    titlePool.setDelFlag("0");
                    titlePool.setCreateBy(SecurityUtils.getUsername());
                    titlePool.setCreateTime(DateUtils.getNowDate());

                    // 检查是否重复(标题相同视为重复)
                    if (titlePoolMapper.checkTitleExists(titlePool.getTitle()) > 0) {
                        duplicateCount++;
                        continue;
                    }

                    // 插入数据库
                    titlePoolMapper.insertGbArticleTitlePool(titlePool);
                    successCount++;
                    
                } catch (Exception e) {
                    failedCount++;
                    errorMessages.add(String.format("行%d导入失败: %s", i + 1, e.getMessage()));
                    log.error("导入JSON数据第{}行失败", i + 1, e);
                }
            }

            // 更新导入日志
            importLog.setTotalCount(totalCount);
            importLog.setSuccessCount(successCount);
            importLog.setFailedCount(failedCount);
            importLog.setDuplicateCount(duplicateCount);
            importLog.setImportStatus("completed");
            if (!errorMessages.isEmpty()) {
                importLog.setErrorMessage(String.join("\n", errorMessages));
            }
            importLogMapper.updateGbTitleImportLog(importLog);
            
            // 更新批次的标题数量
            batchMapper.updateBatchTitleCount(batchCode);

        } catch (Exception e) {
            log.error("导入JSON数据失败", e);
            importLog.setImportStatus("failed");
            importLog.setErrorMessage(e.getMessage());
            importLogMapper.updateGbTitleImportLog(importLog);
            throw new RuntimeException("导入失败: " + e.getMessage());
        }

        return importLog;
    }

    /**
     * 导入Excel文件
     */
    @Override
    @Transactional
    public GbTitleImportLog importFromExcel(MultipartFile file, Long siteId, Long categoryId, String batchName, String sourceName) 
    {
        // 参数验证
        if (siteId == null) {
            throw new RuntimeException("网站ID不能为空");
        }
        
        String importDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        
        // 生成批次号
        String batchCode = batchMapper.generateBatchCode(siteId, importDate);
        
        // 创建批次记录
        GbTitlePoolBatch batch = new GbTitlePoolBatch();
        batch.setSiteId(siteId);
        batch.setCategoryId(categoryId);
        batch.setBatchCode(batchCode);
        batch.setBatchName(StringUtils.isNotBlank(batchName) ? batchName : "Excel导入-" + file.getOriginalFilename());
        batch.setImportDate(DateUtils.getNowDate());
        batch.setImportSource("excel");
        batch.setTitleCount(0);
        batch.setStatus("1");
        batch.setCreateBy(SecurityUtils.getUsername());
        batch.setCreateTime(DateUtils.getNowDate());
        batchMapper.insertGbTitlePoolBatch(batch);
        
        // 生成导入批次号（使用批次code）
        String importBatch = batchCode;
        
        // 创建导入日志
        GbTitleImportLog importLog = new GbTitleImportLog();
        importLog.setImportBatch(importBatch);
        importLog.setImportType("excel");
        importLog.setFileName(file.getOriginalFilename());
        importLog.setImportStatus("processing");
        importLog.setCreateBy(SecurityUtils.getUsername());
        importLog.setCreateTime(DateUtils.getNowDate());
        importLogMapper.insertGbTitleImportLog(importLog);

        int totalCount = 0;
        int successCount = 0;
        int failedCount = 0;
        int duplicateCount = 0;
        List<String> errorMessages = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            totalCount = sheet.getLastRowNum(); // 不包含表头

            // 从第2行开始读取(第1行是表头)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                try {
                    Row row = sheet.getRow(i);
                    if (row == null) continue;

                    // 读取Excel列数据(假设列顺序: 标题, 关键词, 参考内容, 来源URL, 优先级, 标签)
                    String title = getCellValue(row.getCell(0));
                    String keywords = getCellValue(row.getCell(1));
                    String content = getCellValue(row.getCell(2));
                    String url = getCellValue(row.getCell(3));
                    Integer priority = parsePriority(getCellValue(row.getCell(4)));
                    String tags = getCellValue(row.getCell(5));

                    if (StringUtils.isBlank(title)) {
                        failedCount++;
                        errorMessages.add(String.format("行%d: 标题不能为空", i + 1));
                        continue;
                    }

                    // 创建标题对象
                    GbArticleTitlePool titlePool = new GbArticleTitlePool();
                    // 注意：site_id和category_id由批次管理，不在标题对象中直接设置
                    titlePool.setTitle(title);
                    titlePool.setKeywords(keywords);
                    titlePool.setReferenceContent(content);
                    titlePool.setSourceName(StringUtils.isNotBlank(sourceName) ? sourceName : "Excel导入");
                    titlePool.setSourceUrl(url);
                    titlePool.setImportBatch(importBatch);
                    titlePool.setUsageStatus("0");
                    titlePool.setUsedCount(0);
                    titlePool.setPriority(priority != null ? priority : 0);
                    titlePool.setTags(tags);
                    titlePool.setDelFlag("0");
                    titlePool.setCreateBy(SecurityUtils.getUsername());
                    titlePool.setCreateTime(DateUtils.getNowDate());

                    // 检查是否重复
                    if (titlePoolMapper.checkTitleExists(title) > 0) {
                        duplicateCount++;
                        continue;
                    }

                    // 插入数据库
                    titlePoolMapper.insertGbArticleTitlePool(titlePool);
                    successCount++;
                    
                } catch (Exception e) {
                    failedCount++;
                    errorMessages.add(String.format("行%d导入失败: %s", i + 1, e.getMessage()));
                    log.error("导入Excel第{}行失败", i + 1, e);
                }
            }

            // 更新导入日志
            importLog.setTotalCount(totalCount);
            importLog.setSuccessCount(successCount);
            importLog.setFailedCount(failedCount);
            importLog.setDuplicateCount(duplicateCount);
            importLog.setImportStatus("completed");
            if (!errorMessages.isEmpty()) {
                importLog.setErrorMessage(String.join("\n", errorMessages));
            }
            importLogMapper.updateGbTitleImportLog(importLog);
            
            // 更新批次的标题数量
            batchMapper.updateBatchTitleCount(batchCode);

        } catch (Exception e) {
            log.error("导入Excel文件失败", e);
            log.error("导入Excel文件失败", e);
            importLog.setImportStatus("failed");
            importLog.setErrorMessage(e.getMessage());
            importLogMapper.updateGbTitleImportLog(importLog);
            throw new RuntimeException("导入失败: " + e.getMessage());
        }

        return importLog;
    }

    /**
     * 查询导入历史
     */
    @Override
    public List<GbTitleImportLog> queryImportHistory(String importBatch) 
    {
        return importLogMapper.selectGbTitleImportLogList(importBatch);
    }

    /**
     * 获取导入统计
     */
    @Override
    public Map<String, Object> getImportStatistics() 
    {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalImports", importLogMapper.countTotalImports());
        stats.put("totalTitles", titlePoolMapper.countTotalTitles());
        stats.put("usedTitles", titlePoolMapper.countUsedTitles());
        stats.put("unusedTitles", titlePoolMapper.countUnusedTitles());
        return stats;
    }

    /**
     * 生成导入批次号
     */
    private String generateImportBatch() 
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return "BATCH_" + sdf.format(new Date()) + "_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * 获取单元格值
     */
    private String getCellValue(Cell cell) 
    {
        if (cell == null) return "";
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return new SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue());
                }
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    /**
     * 解析优先级
     */
    private Integer parsePriority(String value) 
    {
        if (StringUtils.isBlank(value)) return 0;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
