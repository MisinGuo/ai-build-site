package com.ruoyi.gamebox.service;

import java.util.List;
import java.util.Map;

/**
 * 数据库表结构查询Service接口
 * 
 * @author ruoyi
 */
public interface ITableSchemaService 
{
    /**
     * 获取指定表的字段结构
     * 
     * @param tableName 表名
     * @return 字段结构列表
     */
    List<Map<String, Object>> getTableSchema(String tableName);

    /**
     * 获取所有相关表的字段结构
     * 包括: gb_games(主表), gb_promotion_link_configs(推广链接), gb_games_platform_ext(平台扩展)
     * 
     * @return 分组的字段结构 { main: [...], promotion_link: [...], platform_data: [...] }
     */
    Map<String, Object> getAllTableFields();
    
    /**
     * 获取字段schema定义（包含字段类型等元数据）
     * 
     * @return 字段schema映射 { main: { field_name: { type: 'integer', comment: '...' } }, ... }
     */
    Map<String, Object> getFieldSchemas();
}