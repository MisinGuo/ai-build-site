package com.ruoyi.gamebox.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 标题导入日志对象 gb_title_import_log
 * 
 * @author ruoyi
 * @date 2025-12-30
 */
public class GbTitleImportLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 导入批次号 */
    @Excel(name = "导入批次号")
    private String importBatch;

    /** 导入类型: json-JSON文件 excel-Excel文件 api-API接口 */
    @Excel(name = "导入类型", readConverterExp = "json=JSON文件,excel=Excel文件,api=API接口")
    private String importType;

    /** 文件名称 */
    @Excel(name = "文件名称")
    private String fileName;

    /** 文件路径 */
    private String filePath;

    /** 总数量 */
    @Excel(name = "总数量")
    private Integer totalCount;

    /** 成功数量 */
    @Excel(name = "成功数量")
    private Integer successCount;

    /** 失败数量 */
    @Excel(name = "失败数量")
    private Integer failedCount;

    /** 重复数量 */
    @Excel(name = "重复数量")
    private Integer duplicateCount;

    /** 导入状态: processing-处理中 completed-已完成 failed-失败 */
    @Excel(name = "导入状态", readConverterExp = "processing=处理中,completed=已完成,failed=失败")
    private String importStatus;

    /** 错误信息 */
    private String errorMessage;

    /** 导入详情JSON */
    private String importDetails;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setImportBatch(String importBatch) 
    {
        this.importBatch = importBatch;
    }

    public String getImportBatch() 
    {
        return importBatch;
    }

    public void setImportType(String importType) 
    {
        this.importType = importType;
    }

    public String getImportType() 
    {
        return importType;
    }

    public void setFileName(String fileName) 
    {
        this.fileName = fileName;
    }

    public String getFileName() 
    {
        return fileName;
    }

    public void setFilePath(String filePath) 
    {
        this.filePath = filePath;
    }

    public String getFilePath() 
    {
        return filePath;
    }

    public void setTotalCount(Integer totalCount) 
    {
        this.totalCount = totalCount;
    }

    public Integer getTotalCount() 
    {
        return totalCount;
    }

    public void setSuccessCount(Integer successCount) 
    {
        this.successCount = successCount;
    }

    public Integer getSuccessCount() 
    {
        return successCount;
    }

    public void setFailedCount(Integer failedCount) 
    {
        this.failedCount = failedCount;
    }

    public Integer getFailedCount() 
    {
        return failedCount;
    }

    public void setDuplicateCount(Integer duplicateCount) 
    {
        this.duplicateCount = duplicateCount;
    }

    public Integer getDuplicateCount() 
    {
        return duplicateCount;
    }

    public void setImportStatus(String importStatus) 
    {
        this.importStatus = importStatus;
    }

    public String getImportStatus() 
    {
        return importStatus;
    }

    public void setErrorMessage(String errorMessage) 
    {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() 
    {
        return errorMessage;
    }

    public void setImportDetails(String importDetails) 
    {
        this.importDetails = importDetails;
    }

    public String getImportDetails() 
    {
        return importDetails;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("importBatch", getImportBatch())
            .append("importType", getImportType())
            .append("fileName", getFileName())
            .append("filePath", getFilePath())
            .append("totalCount", getTotalCount())
            .append("successCount", getSuccessCount())
            .append("failedCount", getFailedCount())
            .append("duplicateCount", getDuplicateCount())
            .append("importStatus", getImportStatus())
            .append("errorMessage", getErrorMessage())
            .append("importDetails", getImportDetails())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .toString();
    }
}
