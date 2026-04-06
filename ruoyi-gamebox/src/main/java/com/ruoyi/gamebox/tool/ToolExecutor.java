package com.ruoyi.gamebox.tool;

import java.util.Map;

/**
 * 工具执行器接口
 * 
 * @author ruoyi
 * @date 2026-01-04
 */
public interface ToolExecutor {
    
    /**
     * 执行工具
     * 
     * @param params 输入参数
     * @return 执行结果
     * @throws Exception 执行异常
     */
    Map<String, Object> execute(Map<String, Object> params) throws Exception;
    
    /**
     * 验证参数
     * 
     * @param params 输入参数
     * @param inputDefinitions 输入参数定义（JSON数组）
     * @throws Exception 验证失败异常
     */
    default void validateParams(Map<String, Object> params, String inputDefinitions) throws Exception {
        // 默认实现：不做验证
    }
    
    /**
     * 获取执行器类型
     * 
     * @return 类型标识
     */
    String getType();
}
