package com.ruoyi.gamebox.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.gamebox.service.ITableSchemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 数据库表结构查询Controller
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/gamebox/table-schema")
public class TableSchemaController extends BaseController
{
    @Autowired
    private ITableSchemaService tableSchemaService;

    /**
     * 获取指定表的字段结构
     */
    @GetMapping("/{tableName}")
    public AjaxResult getTableSchema(@PathVariable String tableName)
    {
        return AjaxResult.success(tableSchemaService.getTableSchema(tableName));
    }

    /**
     * 获取所有相关表的字段结构(主表、推广链接表、平台扩展表)
     */
    @GetMapping("/all-fields")
    public AjaxResult getAllTableFields()
    {
        return AjaxResult.success(tableSchemaService.getAllTableFields());
    }
}
