package com.ruoyi.gamebox.tool;

import java.lang.annotation.*;

/**
 * 系统工具注解
 * 标注此注解的ToolExecutor会在系统启动时自动注册为系统级工具
 * 
 * @author ruoyi
 * @date 2026-01-05
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SystemTool {
    
    /**
     * 工具代码（唯一标识）
     */
    String code();

    /**
     * 工具在前端的分类类型。
     * - "system"：系统内置操作，无外部网络调用（如保存文章、导入游戏）
     * - "integration"：系统预置的外部服务集成，含特定处理逻辑（如Git文件读取+base64解码）
     */
    String toolType() default "system";
    
    /**
     * 工具名称
     */
    String name();
    
    /**
     * 工具描述
     */
    String description();
    
    /**
     * 输入参数定义（JSON格式）
     */
    String inputs();
    
    /**
     * 输出参数定义（JSON格式）
     */
    String outputs();
    
    /**
     * 排序顺序
     */
    int order() default 0;
}
