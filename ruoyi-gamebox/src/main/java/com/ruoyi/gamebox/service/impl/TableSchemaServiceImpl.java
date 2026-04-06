package com.ruoyi.gamebox.service.impl;

import com.ruoyi.gamebox.service.ITableSchemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据库表结构查询Service实现
 * 
 * @author ruoyi
 */
@Service
public class TableSchemaServiceImpl implements ITableSchemaService
{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 查询表结构的SQL
     */
    private static final String QUERY_SCHEMA_SQL = 
        "SELECT " +
        "  COLUMN_NAME as name, " +
        "  DATA_TYPE as dataType, " +
        "  COLUMN_COMMENT as comment, " +
        "  IS_NULLABLE as nullable, " +
        "  COLUMN_TYPE as columnType, " +
        "  COLUMN_DEFAULT as defaultValue " +
        "FROM INFORMATION_SCHEMA.COLUMNS " +
        "WHERE TABLE_SCHEMA = DATABASE() " +
        "  AND TABLE_NAME = ? " +
        "ORDER BY ORDINAL_POSITION";

    @Override
    public List<Map<String, Object>> getTableSchema(String tableName)
    {
        return jdbcTemplate.queryForList(QUERY_SCHEMA_SQL, tableName);
    }

    @Override
    public Map<String, Object> getAllTableFields()
    {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. 查询主表(gb_games)字段
            List<Map<String, Object>> mainFields = getTableSchema("gb_games");
            Map<String, Object> mainGroup = new HashMap<>();
            mainGroup.put("label", "游戏主表");
            mainGroup.put("table", "gb_games");
            mainGroup.put("description", "游戏基本信息，包括名称、图标、描述等");
            mainGroup.put("fields", convertToFieldOptions(mainFields));
            result.put("main", mainGroup);
            
            // 2. 查询推广链接配置表(gb_promotion_link_configs)字段
            List<Map<String, Object>> promotionFields = new ArrayList<>();
            promotionFields.add(createField("download_url", "varchar(500)", "下载链接", "string"));
            promotionFields.add(createField("web_url", "varchar(500)", "网页链接", "string"));
            promotionFields.add(createField("qrcode", "varchar(500)", "二维码URL", "string"));
            promotionFields.add(createField("description", "varchar(200)", "链接描述", "string"));
            promotionFields.add(createField("link_type", "varchar(50)", "链接类型", "string"));
            promotionFields.add(createField("is_primary", "char(1)", "是否主要链接(0/1)", "string"));
            Map<String, Object> promotionGroup = new HashMap<>();
            promotionGroup.put("label", "推广链接");
            promotionGroup.put("table", "gb_box_game_relations.promotion_links");
            promotionGroup.put("description", "推广链接JSON字段，存储在盒子关联表的promotion_links字段中");
            promotionGroup.put("fields", convertToFieldOptions(promotionFields));
            result.put("promotion_link", promotionGroup);
            
            // 3. 查询平台扩展表(gb_games_platform_ext)字段
            List<Map<String, Object>> platformFields = new ArrayList<>();
            platformFields.add(createField("custom_field_1", "varchar(500)", "自定义字段1", "string"));
            platformFields.add(createField("custom_field_2", "varchar(500)", "自定义字段2", "string"));
            platformFields.add(createField("custom_field_3", "varchar(500)", "自定义字段3", "string"));
            platformFields.add(createField("rating", "decimal(3,1)", "评分", "decimal"));
            platformFields.add(createField("download_count", "int", "下载次数", "int"));
            platformFields.add(createField("tags", "varchar(500)", "标签（逗号分隔）", "string"));
            platformFields.add(createField("extra_data", "text", "额外数据（自由格式）", "string"));
            Map<String, Object> platformGroup = new HashMap<>();
            platformGroup.put("label", "平台扩展数据");
            platformGroup.put("table", "gb_box_game_relations.platform_data");
            platformGroup.put("description", "平台特定数据JSON字段，存储在盒子关联表的platform_data字段中");
            platformGroup.put("fields", convertToFieldOptions(platformFields));
            result.put("platform_data", platformGroup);
            
            // 4. 查询盒子游戏关联表(gb_box_game_relations)字段
            List<Map<String, Object>> relationFields = getTableSchema("gb_box_game_relations");
            Map<String, Object> relationGroup = new HashMap<>();
            relationGroup.put("label", "盒子-游戏关联表");
            relationGroup.put("table", "gb_box_game_relations");
            relationGroup.put("description", "盒子与游戏的关联信息，包括折扣、扶持、推广等数据");
            relationGroup.put("fields", convertToFieldOptions(relationFields));
            result.put("relation", relationGroup);
            
            // 5. 查询分类关联表(gb_game_category_relation)字段
            List<Map<String, Object>> categoryRelationFields = new ArrayList<>();
            categoryRelationFields.add(createField("category_id", "bigint(20)", "分类ID（需配置值映射：分类名→分类ID）", "int"));
            categoryRelationFields.add(createField("is_primary", "char(1)", "是否主要分类(0-否/1-是)", "string"));
            categoryRelationFields.add(createField("sort_order", "int", "排序（越小越靠前）", "int"));
            Map<String, Object> categoryGroup = new HashMap<>();
            categoryGroup.put("label", "游戏-分类关联表");
            categoryGroup.put("table", "gb_game_category_relation");
            categoryGroup.put("description", "游戏与分类的关联关系，支持多分类。通常配合值映射使用，将平台分类名转换为系统分类ID");
            categoryGroup.put("fields", convertToFieldOptions(categoryRelationFields));
            result.put("category_relation", categoryGroup);
            
        } catch (Exception e) {
            e.printStackTrace();
            // 返回空结构
            result.put("main", createEmptyGroup("游戏主表", "gb_games", "游戏基本信息"));
            result.put("promotion_link", createEmptyGroup("推广链接", "gb_box_game_relations.promotion_links", "推广链接JSON字段"));
            result.put("platform_data", createEmptyGroup("平台扩展数据", "gb_box_game_relations.platform_data", "平台特定数据JSON字段"));
            result.put("relation", createEmptyGroup("盒子-游戏关联表", "gb_box_game_relations", "盒子与游戏的关联信息"));
            result.put("category_relation", createEmptyGroup("游戏-分类关联表", "gb_game_category_relation", "游戏与分类的关联关系"));
        }
        
        return result;
    }
    
    /**
     * 创建空的字段组
     */
    private Map<String, Object> createEmptyGroup(String label, String table, String description)
    {
        Map<String, Object> group = new HashMap<>();
        group.put("label", label);
        group.put("table", table);
        group.put("description", description);
        group.put("fields", new ArrayList<>());
        return group;
    }

    /**
     * 创建字段信息
     */
    private Map<String, Object> createField(String name, String dataType, String comment, String type)
    {
        Map<String, Object> field = new HashMap<>();
        field.put("name", name);
        field.put("dataType", dataType);
        field.put("comment", comment);
        field.put("type", type);
        return field;
    }

    /**
     * 将数据库字段信息转换为前端选项格式
     */
    private List<Map<String, Object>> convertToFieldOptions(List<Map<String, Object>> fields)
    {
        // 定义不允许映射的系统关键字段
        Set<String> excludedFields = new HashSet<>(Arrays.asList(
            "id",           // 主键
            "create_by",    // 创建者
            "create_time",  // 创建时间
            "update_by",    // 更新者
            "update_time",  // 更新时间
            "del_flag",     // 删除标志
            "site_id",      // 网站ID（应由系统自动设置）
            "box_id",       // 盒子ID（应由系统自动设置）
            "game_id"       // 游戏ID（关联表中，应由系统自动设置）
        ));
        
        return fields.stream()
            .filter(field -> {
                String fieldName = field.get("name") != null ? field.get("name").toString() : "";
                // 过滤掉系统关键字段
                return !excludedFields.contains(fieldName.toLowerCase());
            })
            .map(field -> {
                Map<String, Object> option = new HashMap<>();
                String fieldName = field.get("name") != null ? field.get("name").toString() : "";
                String comment = field.get("comment") != null ? field.get("comment").toString() : "";
                String dataType = field.get("dataType") != null ? field.get("dataType").toString() : "";
                String columnType = field.get("columnType") != null ? field.get("columnType").toString() : "";
                
                option.put("value", fieldName);
                option.put("label", fieldName);
                option.put("comment", comment.isEmpty() ? fieldName : comment);
                option.put("type", convertDataTypeToFieldType(dataType, columnType));
                option.put("dataType", dataType); // 原始数据类型
                option.put("columnType", columnType); // 完整列类型
                option.put("nullable", field.get("nullable")); // 是否可为空
                option.put("defaultValue", field.get("defaultValue")); // 默认值
                
                return option;
            }).collect(Collectors.toList());
    }

    /**
     * 将MySQL数据类型转换为字段类型
     */
    private String convertDataTypeToFieldType(String dataType, String columnType)
    {
        dataType = dataType.toLowerCase();
        columnType = columnType.toLowerCase();
        
        if (dataType.contains("int") || dataType.contains("tinyint")) {
            return "integer";
        } else if (dataType.contains("decimal") || dataType.contains("float") || dataType.contains("double")) {
            return "decimal";
        } else if (dataType.contains("date") || dataType.contains("time")) {
            return "datetime";
        } else if (dataType.contains("json") || columnType.contains("json")) {
            return "json";
        } else if (dataType.contains("text") || dataType.contains("varchar") || dataType.contains("char")) {
            return "string";
        } else {
            return "string";
        }
    }
    
    /**
     * 获取所有表字段的schema定义（包含字段类型）
     */
    @Override
    public Map<String, Object> getFieldSchemas()
    {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 获取各个表的字段schema
            result.put("main", getTableFieldSchemas("gb_games"));
            result.put("category_relation", getTableFieldSchemas("gb_game_category_relations"));
            result.put("relation", getTableFieldSchemas("gb_box_game_relations"));
            
            // JSON字段不需要schema限制，允许用户自定义
            result.put("promotion_link", new HashMap<>());
            result.put("platform_data", new HashMap<>());
            result.put("ext", new HashMap<>());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * 获取指定表的字段schema（字段名 -> 类型映射）
     */
    private Map<String, Map<String, String>> getTableFieldSchemas(String tableName)
    {
        Map<String, Map<String, String>> schemas = new HashMap<>();
        
        List<Map<String, Object>> columns = getTableSchema(tableName);
        for (Map<String, Object> column : columns) {
            String fieldName = (String) column.get("name");
            String dataType = (String) column.get("dataType");
            String columnType = (String) column.get("columnType");
            String comment = (String) column.get("comment");
            
            // 映射数据库类型到前端字段类型
            String mappedType = convertDataTypeToFieldType(dataType, columnType);
            
            Map<String, String> fieldSchema = new HashMap<>();
            fieldSchema.put("type", mappedType);
            fieldSchema.put("comment", comment);
            fieldSchema.put("dbType", dataType);
            
            schemas.put(fieldName, fieldSchema);
        }
        
        return schemas;
    }
}