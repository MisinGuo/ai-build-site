-- 为字段映射配置表添加值映射字段
-- 用于存储字段值的转换规则(JSON格式)

ALTER TABLE gb_field_mappings 
ADD COLUMN value_mapping TEXT COMMENT '值映射规则(JSON): {type: "direct/regex/function", mappings: {...}}' AFTER transform_expression;

-- 更新表注释
ALTER TABLE gb_field_mappings COMMENT='字段映射配置表(支持值映射规则)';

-- 示例值映射数据
/*
直接映射示例:
{
  "type": "direct",
  "mappings": {
    "角色扮演": "1",
    "卡牌": "5",
    "动作": "3"
  }
}

正则替换示例:
{
  "type": "regex",
  "pattern": "^(\\d+).*$",
  "replacement": "$1"
}

函数转换示例:
{
  "type": "function",
  "code": "function(value) { return value.toUpperCase(); }"
}
*/
