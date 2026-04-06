package com.ruoyi.web.controller.gamebox.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 空字符串转List的反序列化器
 * 处理前端传递空字符串 "" 的情况，将其转换为空 List
 * 
 * @author ruoyi
 * @date 2025-01-18
 */
public class EmptyStringToListDeserializer extends JsonDeserializer<List<Long>> 
{
    @Override
    public List<Long> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException 
    {
        JsonNode node = p.getCodec().readTree(p);
        
        // 如果是空字符串或 null，返回空 List
        if (node.isNull() || (node.isTextual() && node.asText().isEmpty())) 
        {
            return new ArrayList<>();
        }
        
        // 如果是数组，正常解析
        if (node.isArray()) 
        {
            List<Long> result = new ArrayList<>();
            for (JsonNode element : node) 
            {
                if (element.isNumber()) 
                {
                    result.add(element.asLong());
                } 
                else if (element.isTextual()) 
                {
                    String text = element.asText();
                    if (!text.isEmpty()) 
                    {
                        try 
                        {
                            result.add(Long.parseLong(text));
                        } 
                        catch (NumberFormatException e) 
                        {
                            // 忽略无效的数字
                        }
                    }
                }
            }
            return result;
        }
        
        // 其他情况返回空 List
        return new ArrayList<>();
    }
}
