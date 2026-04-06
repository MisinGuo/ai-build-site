package com.ruoyi.gamebox.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.gamebox.domain.GbAtomicTool;
import com.ruoyi.gamebox.mapper.GbAtomicToolMapper;
import com.ruoyi.gamebox.mapper.GbSiteMapper;
import com.ruoyi.gamebox.service.IGbAtomicToolService;
import com.ruoyi.gamebox.support.RelatedModeSiteValidator;
import com.ruoyi.gamebox.tool.SystemTool;
import com.ruoyi.gamebox.tool.ToolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 原子工具Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-01-04
 */
@Service
public class GbAtomicToolServiceImpl implements IGbAtomicToolService 
{
    private static final Logger log = LoggerFactory.getLogger(GbAtomicToolServiceImpl.class);
    
    @Autowired
    private GbAtomicToolMapper gbAtomicToolMapper;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired(required = false)
    private List<ToolExecutor> toolExecutors;

    @Autowired
    private GbSiteMapper gbSiteMapper;

    @Autowired
    private RelatedModeSiteValidator relatedModeSiteValidator;

    private void injectPersonalSiteId(BaseEntity entity) {
        try {
            Long personalSiteId = gbSiteMapper.selectPersonalSiteIdByUserId(SecurityUtils.getUserId());
            if (personalSiteId != null) {
                entity.getParams().put("personalSiteId", personalSiteId);
            }
        } catch (Exception ignored) {}
}
    
    // 工具执行器注册表
    private Map<String, ToolExecutor> executorRegistry = new HashMap<>();
    
    /**
     * 初始化执行器注册表。
     *
     * 注册规则：
     *   - 有 @SystemTool 注解的执行器（system / integration 类型）：按 code 注册
     *   - 无 @SystemTool 注解的通用执行器（ai / api 类型）：按执行器自身的 type 注册
     */
    @PostConstruct
    public void init() {
        if (toolExecutors != null) {
            for (ToolExecutor executor : toolExecutors) {
                SystemTool systemTool = executor.getClass().getAnnotation(SystemTool.class);
                if (systemTool != null) {
                    // system / integration 类型：按工具代码注册，保证与 DB 中 tool_code 字段匹配
                    executorRegistry.put(systemTool.code(), executor);
                    log.info("注册 [{}] 执行器: code={}, class={}",
                            systemTool.toolType(), systemTool.code(), executor.getClass().getSimpleName());
                } else {
                    // ai / api 类型：按通用类型注册，与 DB 中 tool_type 字段匹配
                    executorRegistry.put(executor.getType(), executor);
                    log.info("注册 [{}] 执行器: type={}, class={}",
                            executor.getType(), executor.getType(), executor.getClass().getSimpleName());
                }
            }
        }
    }
    
    @Override
    public List<GbAtomicTool> selectGbAtomicToolList(GbAtomicTool gbAtomicTool)
    {
        relatedModeSiteValidator.validate(gbAtomicTool.getQueryMode(), gbAtomicTool.getSiteId());
        injectPersonalSiteId(gbAtomicTool);
        return gbAtomicToolMapper.selectGbAtomicToolList(gbAtomicTool);
    }

    @Override
    public GbAtomicTool selectGbAtomicToolById(Long id)
    {
        return gbAtomicToolMapper.selectGbAtomicToolById(id);
    }

    @Override
    public GbAtomicTool selectGbAtomicToolByCode(String toolCode)
    {
        GbAtomicTool query = new GbAtomicTool();
        query.setToolCode(toolCode);
        List<GbAtomicTool> list = selectGbAtomicToolList(query);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public GbAtomicTool selectGbAtomicToolByCodeAndSite(String toolCode, Long siteId)
    {
        GbAtomicTool query = new GbAtomicTool();
        query.setToolCode(toolCode);
        query.setEnabled(1);
        if (siteId != null) {
            query.setQueryMode("related");
            query.setSiteId(siteId);

            // 与前端 related 查询逻辑保持一致：需要 personalSiteId 才能命中默认配置。
            // 在异步执行/系统上下文下可能拿不到当前用户，此时按 siteId 反查站点所属用户兜底。
            Long personalSiteId = null;
            try {
                personalSiteId = gbSiteMapper.selectPersonalSiteIdByUserId(SecurityUtils.getUserId());
            } catch (Exception ignored) {
            }
            if (personalSiteId == null) {
                try {
                    Long userId = gbSiteMapper.selectAnyUserIdBySiteId(siteId);
                    if (userId != null) {
                        personalSiteId = gbSiteMapper.selectPersonalSiteIdByUserId(userId);
                    }
                } catch (Exception ignored) {
                }
            }
            if (personalSiteId != null) {
                query.getParams().put("personalSiteId", personalSiteId);
            }
        }
        List<GbAtomicTool> list = selectGbAtomicToolList(query);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public int insertGbAtomicTool(GbAtomicTool gbAtomicTool)
    {
        // system / integration 工具：校验 tool_code 必须对应一个已注册的执行器
        if ("system".equals(gbAtomicTool.getToolType()) || "integration".equals(gbAtomicTool.getToolType())) {
            String code = gbAtomicTool.getToolCode();
            if (code == null || code.trim().isEmpty()) {
                throw new ServiceException("系统/集成工具必须选择执行器");
            }
            if (!executorRegistry.containsKey(code)) {
                throw new ServiceException("tool_code=[" + code + "] 未对应任何已注册的执行器，请从执行器列表中选择");
            }
            // 同一网站下每个执行器只能创建一个实例
            GbAtomicTool query = new GbAtomicTool();
            query.setToolCode(code);
            query.setSiteId(gbAtomicTool.getSiteId());
            if (!gbAtomicToolMapper.selectGbAtomicToolList(query).isEmpty()) {
                throw new ServiceException("该网站下已存在执行器 [" + code + "] 的实例，每个网站只能创建一个");
            }
        }
        gbAtomicTool.setCreateTime(DateUtils.getNowDate());
        return gbAtomicToolMapper.insertGbAtomicTool(gbAtomicTool);
    }

    @Override
    public int updateGbAtomicTool(GbAtomicTool gbAtomicTool)
    {
        // system / integration 工具的 tool_code 是执行器绑定键，一旦创建不允许修改
        GbAtomicTool existing = gbAtomicToolMapper.selectGbAtomicToolById(gbAtomicTool.getId());
        if (existing != null
                && ("system".equals(existing.getToolType()) || "integration".equals(existing.getToolType()))
                && gbAtomicTool.getToolCode() != null
                && !gbAtomicTool.getToolCode().equals(existing.getToolCode())) {
            throw new ServiceException("系统/集成工具的执行器绑定码 [tool_code] 不允许修改");
        }
        gbAtomicTool.setUpdateTime(DateUtils.getNowDate());
        return gbAtomicToolMapper.updateGbAtomicTool(gbAtomicTool);
    }

    @Override
    public int deleteGbAtomicToolByIds(Long[] ids)
    {
        return gbAtomicToolMapper.deleteGbAtomicToolByIds(ids);
    }

    @Override
    public int deleteGbAtomicToolById(Long id)
    {
        return gbAtomicToolMapper.deleteGbAtomicToolById(id);
    }

    /**
     * 通过工具 code 直接执行带 @SystemTool 注解的执行器。
     * 仅适用于 system / integration 类型工具的直接调用（不经过 DB 配置）。
     */
    @Override
    public Map<String, Object> testTool(String toolCode, Map<String, Object> params) throws Exception
    {
        return executeTool(toolCode, params);
    }

    @Override
    public Map<String, Object> executeTool(String toolCode, Map<String, Object> params) throws Exception
    {
        Map<String, Object> result = new HashMap<>();
        try {
            ToolExecutor executor = executorRegistry.get(toolCode);
            if (executor == null) {
                result.put("status", "error");
                result.put("message", "没有找到匹配 code=" + toolCode + " 的执行器，请确认是 system/integration 类型工具");
                return result;
            }
            Class<?> cls = org.springframework.aop.support.AopUtils.isAopProxy(executor)
                    ? org.springframework.aop.support.AopUtils.getTargetClass(executor)
                    : executor.getClass();
            SystemTool annotation = cls.getAnnotation(SystemTool.class);
            if (annotation == null) {
                result.put("status", "error");
                result.put("message", "code=" + toolCode + " 对应的执行器不是系统工具，请使用 executeToolById 接口");
                return result;
            }
            params.put("_inputs", annotation.inputs());
            params.put("_outputs", annotation.outputs());
            executor.validateParams(params, annotation.inputs());
            log.info("直接执行 [{}] 工具: code={}", annotation.toolType(), toolCode);
            Map<String, Object> output = executor.execute(params);
            result.put("status", "success");
            result.put("message", "执行成功");
            result.put("output", output);
        } catch (Exception e) {
            log.error("工具执行失败: toolCode={}", toolCode, e);
            result.put("status", "error");
            result.put("message", "执行失败: " + e.getMessage());
            result.put("error", e.getClass().getSimpleName());
        }
        return result;
    }

    /**
     * 通过 DB 工具 ID 执行工具。
     *
     * 执行器匹配规则：
     *   - system / integration 类型：按 tool_code 在注册表中找层考执行器
     *   - ai / api 类型：按 tool_type 在注册表中找通用执行器
     */
    @Override
    public Map<String, Object> executeToolById(Long toolId, Map<String, Object> params) throws Exception
    {
        Map<String, Object> result = new HashMap<>();
        try {
            GbAtomicTool tool = gbAtomicToolMapper.selectGbAtomicToolById(toolId);
            if (tool == null) {
                result.put("status", "error");
                result.put("message", "工具不存在: ID=" + toolId);
                return result;
            }
            if (tool.getEnabled() == null || tool.getEnabled() != 1) {
                result.put("status", "error");
                result.put("message", "工具已禁用: " + tool.getToolName());
                return result;
            }

            String toolType = tool.getToolType();
            ToolExecutor executor;
            if ("system".equals(toolType) || "integration".equals(toolType)) {
                // 按工具代码匹配专属执行器
                executor = executorRegistry.get(tool.getToolCode());
                if (executor == null) {
                    result.put("status", "error");
                    result.put("message", "未找到 code=[" + tool.getToolCode() + "] 对应的执行器，请确认执行器已注册");
                    return result;
                }
            } else {
                // ai / api 类型：按类型匹配通用执行器
                executor = executorRegistry.get(toolType);
                if (executor == null) {
                    result.put("status", "error");
                    result.put("message", "不支持的工具类型: " + toolType);
                    return result;
                }
            }

            params.put("_config", tool.getConfig());
            params.put("_inputs", tool.getInputs());
            params.put("_outputs", tool.getOutputs());
            executor.validateParams(params, tool.getInputs());

            log.info("执行工具: id={}, type={}, code={}", toolId, toolType, tool.getToolCode());
            Map<String, Object> output = executor.execute(params);
            result.put("status", "success");
            result.put("message", "执行成功");
            result.put("output", output);
        } catch (Exception e) {
            log.error("工具执行失败: toolId={}", toolId, e);
            result.put("status", "error");
            result.put("message", "执行失败: " + e.getMessage());
            result.put("error", e.getClass().getSimpleName());
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getAvailableExecutors()
    {
        List<Map<String, Object>> executors = new ArrayList<>();
        
        for (ToolExecutor executor : toolExecutors) {
            // 获取目标类（处理 Spring 代理）
            Class<?> executorClass = org.springframework.aop.support.AopUtils.isAopProxy(executor) 
                ? org.springframework.aop.support.AopUtils.getTargetClass(executor) 
                : executor.getClass();
            SystemTool annotation = executorClass.getAnnotation(SystemTool.class);
            
            if (annotation != null) {
                Map<String, Object> executorInfo = new HashMap<>();
                executorInfo.put("code", annotation.code());
                executorInfo.put("toolType", annotation.toolType());
                executorInfo.put("name", annotation.name());
                executorInfo.put("description", annotation.description());
                executorInfo.put("inputs", annotation.inputs());
                executorInfo.put("outputs", annotation.outputs());
                executorInfo.put("order", annotation.order());
                executorInfo.put("executorClass", executorClass.getSimpleName());
                
                executors.add(executorInfo);
            }
        }
        
        // 按 order 排序
        executors.sort((a, b) -> {
            Integer orderA = (Integer) a.get("order");
            Integer orderB = (Integer) b.get("order");
            return orderA.compareTo(orderB);
        });
        
        return executors;
    }
}

