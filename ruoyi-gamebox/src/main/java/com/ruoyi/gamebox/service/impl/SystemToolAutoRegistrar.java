package com.ruoyi.gamebox.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.gamebox.domain.GbAtomicTool;
import com.ruoyi.gamebox.mapper.GbAtomicToolMapper;
import com.ruoyi.gamebox.tool.SystemTool;
import com.ruoyi.gamebox.tool.ToolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 系统工具执行器扫描器
 * 在系统启动时扫描所有标注了 @SystemTool 的执行器，仅记录日志，不自动插入数据库
 * 
 * 设计说明：
 * - 执行器只是代码层面的 Bean，提供功能实现
 * - 数据库中的 gb_atomic_tool 记录是用户配置的工具实例
 * - 用户通过前端选择执行器后，才会创建数据库记录
 * 
 * @author ruoyi
 * @date 2026-01-05
 */
@Component
public class SystemToolAutoRegistrar implements CommandLineRunner {
    
    private static final Logger log = LoggerFactory.getLogger(SystemToolAutoRegistrar.class);
    
    @Autowired
    private List<ToolExecutor> toolExecutors;
    
    @Override
    public void run(String... args) throws Exception {
        log.info("========== 系统工具执行器扫描 ==========");
        
        int systemToolCount = 0;
        
        for (ToolExecutor executor : toolExecutors) {
            // 获取目标类（处理 Spring 代理）
            Class<?> executorClass = AopUtils.isAopProxy(executor) 
                ? AopUtils.getTargetClass(executor) 
                : executor.getClass();
            SystemTool annotation = executorClass.getAnnotation(SystemTool.class);
            
            if (annotation != null) {
                systemToolCount++;
                log.info("发现系统工具执行器: {} - {} ({})", 
                    annotation.code(), 
                    annotation.name(), 
                    executorClass.getSimpleName());
            }
        }
        
        log.info("系统工具执行器扫描完成: 共发现 {} 个执行器", systemToolCount);
        log.info("提示: 执行器仅存在于代码中，用户需通过前端选择执行器创建工具配置");
        log.info("========================================");
    }
}
