package com.ruoyi.gamebox.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.gamebox.domain.GbWorkflow;
import com.ruoyi.gamebox.domain.GbWorkflowExecution;
import com.ruoyi.gamebox.domain.GbWorkflowStepExecution;
import com.ruoyi.gamebox.domain.GbWorkflowTemplate;
import com.ruoyi.gamebox.mapper.GbWorkflowExecutionMapper;
import com.ruoyi.gamebox.mapper.GbWorkflowMapper;
import com.ruoyi.gamebox.mapper.GbWorkflowStepExecutionMapper;
import com.ruoyi.gamebox.mapper.GbWorkflowTemplateMapper;
import com.ruoyi.gamebox.mapper.GbSiteMapper;
import com.ruoyi.gamebox.service.IGbWorkflowService;
import com.ruoyi.gamebox.support.RelatedModeSiteValidator;
import com.ruoyi.common.constant.ScheduleConstants;
import com.ruoyi.quartz.domain.SysJob;
import com.ruoyi.quartz.mapper.SysJobMapper;
import com.ruoyi.quartz.util.CronUtils;
import com.ruoyi.quartz.util.ScheduleUtils;
import org.quartz.Scheduler;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.Date;
import java.util.UUID;

/**
 * 工作流Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-01-04
 */
@Service
public class GbWorkflowServiceImpl implements IGbWorkflowService 
{
    private static final Logger log = LoggerFactory.getLogger(GbWorkflowServiceImpl.class);
    
    @Autowired
    private GbWorkflowMapper workflowMapper;

    @Autowired
    private GbWorkflowExecutionMapper executionMapper;

    @Autowired
    private GbWorkflowStepExecutionMapper stepExecutionMapper;

    @Autowired
    private GbWorkflowTemplateMapper templateMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private com.ruoyi.gamebox.service.IGbAtomicToolService atomicToolService;

    @Autowired(required = false)
    private Scheduler scheduler;

    @Autowired(required = false)
    private SysJobMapper sysJobMapper;

    @Autowired
    private GbSiteMapper gbSiteMapper;

    @Autowired
    private RelatedModeSiteValidator relatedModeSiteValidator;

    /** Quartz 定时任务组名 */
    private static final String WORKFLOW_JOB_GROUP = "WORKFLOW";

    private void injectPersonalSiteId(BaseEntity entity) {
        try {
            Long personalSiteId = gbSiteMapper.selectPersonalSiteIdByUserId(SecurityUtils.getUserId());
            if (personalSiteId != null) {
                entity.getParams().put("personalSiteId", personalSiteId);
            }
        } catch (Exception ignored) {}
}

    @Override
    public List<GbWorkflow> selectGbWorkflowList(GbWorkflow gbWorkflow)
    {
        relatedModeSiteValidator.validate(gbWorkflow.getQueryMode(), gbWorkflow.getSiteId());
        injectPersonalSiteId(gbWorkflow);
        return workflowMapper.selectGbWorkflowList(gbWorkflow);
    }

    @Override
    public GbWorkflow selectGbWorkflowById(Long id)
    {
        return workflowMapper.selectGbWorkflowById(id);
    }

    @Override
    public GbWorkflow selectGbWorkflowByCode(String workflowCode)
    {
        return workflowMapper.selectGbWorkflowByCode(workflowCode);
    }

    @Override
    public int insertGbWorkflow(GbWorkflow gbWorkflow)
    {
        recalcStepCount(gbWorkflow);
        int rows = workflowMapper.insertGbWorkflow(gbWorkflow);
        if (rows > 0) {
            syncQuartzJob(gbWorkflow);
        }
        return rows;
    }

    @Override
    public int updateGbWorkflow(GbWorkflow gbWorkflow)
    {
        recalcStepCount(gbWorkflow);
        // 如果触发类型修改为非-scheduled，先查出旧定义以便清理 Quartz Job
        GbWorkflow oldWorkflow = null;
        if (gbWorkflow.getId() != null && !"scheduled".equals(gbWorkflow.getTriggerType())) {
            oldWorkflow = workflowMapper.selectGbWorkflowById(gbWorkflow.getId());
        }
        int rows = workflowMapper.updateGbWorkflow(gbWorkflow);
        if (rows > 0) {
            if (oldWorkflow != null && "scheduled".equals(oldWorkflow.getTriggerType())) {
                // 从 scheduled 切换为其他类型，删除 Quartz Job
                removeQuartzJob(oldWorkflow.getWorkflowCode());
            } else {
                syncQuartzJob(gbWorkflow);
            }
        }
        return rows;
    }

    /** 从 definition JSON 中自动计算并写入 stepCount */
    private void recalcStepCount(GbWorkflow wf) {
        if (wf.getDefinition() == null || wf.getDefinition().trim().isEmpty()) {
            return;
        }
        try {
            com.fasterxml.jackson.databind.JsonNode root = objectMapper.readTree(wf.getDefinition());
            com.fasterxml.jackson.databind.JsonNode steps = root.get("steps");
            wf.setStepCount(steps != null && steps.isArray() ? steps.size() : 0);
        } catch (Exception e) {
            // 解析失败时保留原值，不覆盖
        }
    }

    @Override
    public int deleteGbWorkflowByIds(Long[] ids)
    {
        // 删除前先清理相关 Quartz 定时任务
        if (ids != null && scheduler != null && sysJobMapper != null) {
            for (Long id : ids) {
                try {
                    GbWorkflow wf = workflowMapper.selectGbWorkflowById(id);
                    if (wf != null && "scheduled".equals(wf.getTriggerType())) {
                        removeQuartzJob(wf.getWorkflowCode());
                    }
                } catch (Exception e) {
                    log.warn("删除工作流 Quartz Job 失败 (id={}): {}", id, e.getMessage());
                }
            }
        }
        return workflowMapper.deleteGbWorkflowByIds(ids);
    }

    @Override
    public int deleteGbWorkflowById(Long id)
    {
        try {
            GbWorkflow wf = workflowMapper.selectGbWorkflowById(id);
            if (wf != null && "scheduled".equals(wf.getTriggerType())) {
                removeQuartzJob(wf.getWorkflowCode());
            }
        } catch (Exception e) {
            log.warn("删除工作流 Quartz Job 失败 (id={}): {}", id, e.getMessage());
        }
        return workflowMapper.deleteGbWorkflowById(id);
    }

    // ================================================================
    // Quartz 定时调度同步方法
    // ================================================================

    /**
     * 同步 Quartz 定时任务：
     * - triggerType=scheduled 且 scheduleExpression 非空 → 创建/更新 Job
     * - 其他情况 → 无操作（具体删除逻辑由 updateGbWorkflow 判断后调用 removeQuartzJob）
     */
    private void syncQuartzJob(GbWorkflow wf) {
        if (scheduler == null || sysJobMapper == null) {
            log.warn("Quartz Scheduler 未初始化，跳过定时任务同步");
            return;
        }
        if (!"scheduled".equals(wf.getTriggerType())) {
            return;
        }
        String cron = wf.getScheduleExpression();
        if (!org.springframework.util.StringUtils.hasText(cron)) {
            log.warn("工作流 {} 的 scheduleExpression 为空，跳过 Quartz Job 同步", wf.getWorkflowCode());
            return;
        }
        if (!CronUtils.isValid(cron)) {
            throw new IllegalArgumentException("工作流 [" + wf.getWorkflowName() + "] 的 Cron 表达式 '" + cron
                    + "' 非法。Quartz Cron 必须为 6~7 个字段（秒 分 时 日 月 周 [年]），"
                    + "如: 0 0 8 * * ? 表示每天 08:00 触发");
        }

        try {
            // 构建 SysJob
            SysJob job = findExistingQuartzJob(wf.getWorkflowCode());
            boolean isNew = (job == null);
            if (isNew) {
                job = new SysJob();
            }

            job.setJobName("工作流_" + wf.getWorkflowCode());
            job.setJobGroup(WORKFLOW_JOB_GROUP);
            job.setInvokeTarget("workflowTask.executeWorkflow('" + wf.getWorkflowCode() + "')");
            job.setCronExpression(cron);
            job.setConcurrent("1");          // 禁止并发
            job.setMisfirePolicy("3");       // 错过后不补触发
            // enabled=1 → NORMAL(0)；enabled=0 → PAUSE(1)
            String status = (wf.getEnabled() == null || wf.getEnabled() == 1)
                    ? ScheduleConstants.Status.NORMAL.getValue()
                    : ScheduleConstants.Status.PAUSE.getValue();
            job.setStatus(status);

            if (isNew) {
                // 先插库再注册到 Scheduler
                job.setStatus(ScheduleConstants.Status.PAUSE.getValue());
                sysJobMapper.insertJob(job);
                job.setStatus(status);
                ScheduleUtils.createScheduleJob(scheduler, job);
                log.info("创建工作流定时任务成功: workflowCode={}, jobId={}, cron={}", wf.getWorkflowCode(), job.getJobId(), cron);
            } else {
                // 更新数据库再刷新 Scheduler
                sysJobMapper.updateJob(job);
                Long jobId = job.getJobId();
                String jobGroup = job.getJobGroup();
                org.quartz.JobKey jobKey = ScheduleUtils.getJobKey(jobId, jobGroup);
                if (scheduler.checkExists(jobKey)) {
                    scheduler.deleteJob(jobKey);
                }
                ScheduleUtils.createScheduleJob(scheduler, job);
                log.info("更新工作流定时任务成功: workflowCode={}, jobId={}, cron={}", wf.getWorkflowCode(), job.getJobId(), cron);
            }
        } catch (Exception e) {
            log.error("同步工作流 Quartz Job 失败: workflowCode={}, 原因: {}", wf.getWorkflowCode(), e.getMessage(), e);
        }
    }

    /**
     * 删除指定工作流的 Quartz 定时任务
     */
    private void removeQuartzJob(String workflowCode) {
        if (scheduler == null || sysJobMapper == null) {
            return;
        }
        try {
            SysJob job = findExistingQuartzJob(workflowCode);
            if (job != null) {
                org.quartz.JobKey jobKey = ScheduleUtils.getJobKey(job.getJobId(), job.getJobGroup());
                if (scheduler.checkExists(jobKey)) {
                    scheduler.deleteJob(jobKey);
                }
                sysJobMapper.deleteJobById(job.getJobId());
                log.info("删除工作流定时任务成功: workflowCode={}, jobId={}", workflowCode, job.getJobId());
            }
        } catch (Exception e) {
            log.error("删除工作流 Quartz Job 失败: workflowCode={}, 原因: {}", workflowCode, e.getMessage(), e);
        }
    }

    /**
     * 按工作流编码查找已有的 Quartz Job 记录（jobName = "工作流_" + workflowCode）
     */
    private SysJob findExistingQuartzJob(String workflowCode) {
        if (sysJobMapper == null) return null;
        SysJob query = new SysJob();
        query.setJobName("工作流_" + workflowCode);
        query.setJobGroup(WORKFLOW_JOB_GROUP);
        List<SysJob> list = sysJobMapper.selectJobList(query);
        return (list != null && !list.isEmpty()) ? list.get(0) : null;
    }

    @Override
    public Map<String, Object> executeWorkflow(String workflowCode, String mode, Map<String, Object> inputData) throws Exception
    {
        return executeWorkflowWithId(workflowCode, mode, inputData, null);
    }

    /**
     * 执行工作流（支持预生成 executionId，供异步场景使用）
     */
    public Map<String, Object> executeWorkflowWithId(String workflowCode, String mode, Map<String, Object> inputData, String preExecutionId) throws Exception
    {
        long startTime = System.currentTimeMillis();
        String executionId = (preExecutionId != null && !preExecutionId.isEmpty()) ? preExecutionId : UUID.randomUUID().toString();
        final Map<String, Object> workflowInputData = (inputData == null) ? new HashMap<>() : new HashMap<>(inputData);
        
        log.info("开始执行工作流: workflowCode={}, executionId={}, mode={}", workflowCode, executionId, mode);

        // 1. 先查询工作流定义，将 inputs 默认值填充到 inputData（定时触发时 inputData 为空）
        GbWorkflow workflow = selectGbWorkflowByCode(workflowCode);
        if (workflow == null) {
            throw new IllegalArgumentException("工作流不存在: " + workflowCode);
        }
        if (workflow.getEnabled() == null || workflow.getEnabled() != 1) {
            throw new IllegalStateException("工作流已禁用: " + workflowCode);
        }
        JsonNode definition = objectMapper.readTree(workflow.getDefinition());
        JsonNode inputsDef0 = definition.get("inputs");
        if (inputsDef0 != null && inputsDef0.isArray()) {
            for (JsonNode inputDef : inputsDef0) {
                JsonNode nameNode0 = inputDef.get("name");
                JsonNode defaultNode = inputDef.get("default");
                if (nameNode0 != null && defaultNode != null) {
                    String inputName = nameNode0.asText();
                    Object existing = workflowInputData.get(inputName);
                    boolean missing = existing == null || "".equals(existing);
                    if (missing) {
                        if (defaultNode.isTextual()) {
                            workflowInputData.put(inputName, defaultNode.asText());
                        } else if (defaultNode.isNumber()) {
                            workflowInputData.put(inputName, defaultNode.asLong());
                        } else if (defaultNode.isBoolean()) {
                            workflowInputData.put(inputName, defaultNode.asBoolean());
                        } else {
                            workflowInputData.put(inputName, defaultNode.asText());
                        }
                        log.info("使用工作流输入默认值: {} = {}", inputName, workflowInputData.get(inputName));
                    }
                }
            }
        }

        // 持久化一条 running 状态的记录（此时 inputData 已含默认值）
        GbWorkflowExecution execRecord = new GbWorkflowExecution();
        execRecord.setExecutionId(executionId);
        execRecord.setWorkflowCode(workflowCode);
        execRecord.setMode(mode);
        execRecord.setStatus("running");
        execRecord.setStartTime(new Date(startTime));
        try { execRecord.setInputData(objectMapper.writeValueAsString(workflowInputData)); } catch (Exception ignored) {}
        try {
            executionMapper.insertExecution(execRecord);
        } catch (Exception dbEx) {
            log.warn("保存执行记录失败（不影响执行）: {}", dbEx.getMessage());
        }

        try {
            log.info("工作流定义: {}", workflow.getDefinition());
            
            JsonNode steps = definition.get("steps");
            
            if (steps == null || !steps.isArray() || steps.size() == 0) {
                throw new IllegalArgumentException("工作流步骤配置无效");
            }
            
            log.info("步骤数量: {}", steps.size());
            
            // 3. 准备执行上下文（用于在步骤间传递数据）
            Map<String, Object> context = new HashMap<>(workflowInputData);
            List<Map<String, Object>> stepResults = new ArrayList<>();
            
            // 4. 依次执行每个步骤
            for (int i = 0; i < steps.size(); i++) {
                JsonNode step = steps.get(i);
                
                log.info("步骤 {} 原始数据: {}", i + 1, step.toString());
                
                JsonNode nameNode = step.get("name");
                // 向后兼容：新格式使用 stepName 字段
                if (nameNode == null) {
                    nameNode = step.get("stepName");
                }
                JsonNode toolNode = step.get("tool");
                // 向后兼容：如果没有 tool 字段，尝试读取 toolCode 字段
                if (toolNode == null) {
                    toolNode = step.get("toolCode");
                }
                JsonNode configNode = step.get("config");
                // 向后兼容：如果没有 config 字段，尝试从 inputMapping/paramMapping 和 customValues 构建
                if (configNode == null) {
                    JsonNode inputMappingNode = step.get("inputMapping");
                    JsonNode paramMappingNode = step.get("paramMapping");
                    JsonNode customValuesNode = step.get("customValues");
                    
                    // 优先使用 inputMapping,然后是 paramMapping
                    JsonNode mappingNode = inputMappingNode != null ? inputMappingNode : paramMappingNode;
                    
                    if (mappingNode != null || customValuesNode != null) {
                        // 如果使用 inputMapping,则直接使用,已经是正确格式
                        if (inputMappingNode != null) {
                            configNode = inputMappingNode;
                        } else {
                            // 旧格式兼容:合并 paramMapping 和 customValues 为 config
                            Map<String, Object> configMap = new HashMap<>();
                            
                            // 处理 paramMapping：格式为 {"paramName": "source.field"}
                            if (paramMappingNode != null && paramMappingNode.isObject()) {
                                paramMappingNode.fields().forEachRemaining(entry -> {
                                    String paramName = entry.getKey();
                                    String mappingValue = entry.getValue().asText();
                                    
                                    // 解析映射值，格式如 "input.title" 或 "step0.generatedText"
                                    if (mappingValue != null && mappingValue.contains(".")) {
                                        String[] parts = mappingValue.split("\\.", 2);
                                        Map<String, String> mapping = new HashMap<>();
                                        mapping.put("source", parts[0]);
                                        mapping.put("field", parts[1]);
                                        configMap.put(paramName, mapping);
                                    }
                                });
                            }
                            
                            // 处理 customValues：直接值
                            if (customValuesNode != null && customValuesNode.isObject()) {
                                customValuesNode.fields().forEachRemaining(entry -> {
                                    String paramName = entry.getKey();
                                    JsonNode value = entry.getValue();
                                    if (value.isTextual()) {
                                        configMap.put(paramName, value.asText());
                                    } else if (value.isNumber()) {
                                        configMap.put(paramName, value.asLong());
                                    } else if (value.isBoolean()) {
                                        configMap.put(paramName, value.asBoolean());
                                    } else {
                                        configMap.put(paramName, value);
                                    }
                                });
                            }
                            
                            configNode = objectMapper.valueToTree(configMap);
                        }
                    }
                }
                
                log.info("步骤 {} - name: {}, tool: {}, config: {}", 
                    i + 1, 
                    nameNode != null ? nameNode.asText() : "null",
                    toolNode != null ? toolNode.asText() : "null",
                    configNode != null ? configNode.toString() : "null"
                );
                
                String stepName = nameNode != null ? nameNode.asText() : "步骤" + (i + 1);
                String stepType = step.has("stepType") ? step.get("stepType").asText("tool") : "tool";
                String toolCode = null;
                String targetWorkflowCode = null;

                if ("workflow".equals(stepType)) {
                    JsonNode workflowCodeNode = step.get("workflowCode");
                    if (workflowCodeNode == null || !org.springframework.util.StringUtils.hasText(workflowCodeNode.asText())) {
                        throw new IllegalArgumentException("步骤 " + (i + 1) + " 缺少必需的 workflowCode 字段");
                    }
                    targetWorkflowCode = workflowCodeNode.asText();
                } else {
                    if (toolNode == null || !org.springframework.util.StringUtils.hasText(toolNode.asText())) {
                        throw new IllegalArgumentException("步骤 " + (i + 1) + " 缺少必需的 tool 字段");
                    }
                    toolCode = toolNode.asText();
                }

                // 获取步骤ID 和 continueOnError 标志
                String currentStepId = step.has("stepId") ? step.get("stepId").asText() : ("step_" + (i + 1));
                boolean continueOnError = step.has("continueOnError") && step.get("continueOnError").asBoolean(false);
                boolean allowContinueOnError = continueOnError && !"workflow".equals(stepType);
                // 跳过禁用的步骤
                boolean stepEnabled = !step.has("enabled") || step.get("enabled").asBoolean(true);
                if (!stepEnabled) {
                    log.info("步骤 {} 已禁用，跳过", stepName);
                    continue;
                }
                
                String stepTargetCode = "workflow".equals(stepType) ? targetWorkflowCode : toolCode;
                log.info("执行步骤 {}/{}: {} (类型: {}, 目标: {})", i + 1, steps.size(), stepName, stepType, stepTargetCode);

                com.ruoyi.gamebox.domain.GbAtomicTool tool = null;
                if ("tool".equals(stepType)) {
                    tool = atomicToolService.selectGbAtomicToolByCodeAndSite(toolCode, workflow.getSiteId());
                    if (tool == null) {
                        throw new IllegalStateException("工具不存在: " + toolCode);
                    }
                }

                // 准备步骤参数
                Map<String, Object> stepParams = new HashMap<>();
                
                // 将工具配置作为 _config 参数传递给执行器
                if (tool != null && tool.getConfig() != null && !tool.getConfig().trim().isEmpty()) {
                    stepParams.put("_config", tool.getConfig());
                }
                
                // 处理 config 中的参数映射和自定义值
                if (configNode != null && configNode.isObject()) {
                    log.info("开始处理步骤参数映射, configNode: {}", configNode);
                    
                    configNode.fields().forEachRemaining(entry -> {
                        String paramName = entry.getKey();
                        JsonNode paramValue = entry.getValue();
                        
                        log.info("处理参数: {}, 值类型: {}, 值: {}", paramName, 
                            paramValue.getNodeType(), paramValue);
                        
                        // 检查是否是映射对象（包含 source 和 field/value）
                        if (paramValue.isObject() && paramValue.has("source")) {
                            String source = paramValue.get("source").asText();
                            String field = null;
                            
                            // 兼容两种格式: field 或 value
                            if (paramValue.has("field")) {
                                field = paramValue.get("field").asText();
                            } else if (paramValue.has("value")) {
                                // 如果是 constant 类型,value 就是实际值
                                if ("constant".equals(source)) {
                                    JsonNode valueNode = paramValue.get("value");
                                    if (valueNode.isTextual()) {
                                        stepParams.put(paramName, valueNode.asText());
                                        log.info("常量值(文本): {} = {}", paramName, valueNode.asText());
                                    } else if (valueNode.isNumber()) {
                                        stepParams.put(paramName, valueNode.asLong());
                                        log.info("常量值(数字): {} = {}", paramName, valueNode.asLong());
                                    } else if (valueNode.isBoolean()) {
                                        stepParams.put(paramName, valueNode.asBoolean());
                                        log.info("常量值(布尔): {} = {}", paramName, valueNode.asBoolean());
                                    } else {
                                        stepParams.put(paramName, valueNode);
                                        log.info("常量值(其他): {} = {}", paramName, valueNode);
                                    }
                                    return; // 跳过后续处理
                                } else {
                                    // 对于 step_X.output.fieldName 格式,提取 fieldName
                                    String valueStr = paramValue.get("value").asText();
                                    if (valueStr.contains(".")) {
                                        String[] parts = valueStr.split("\\.");
                                        if (parts.length >= 3) {
                                            field = parts[2]; // step_1.output.titles -> titles
                                        }
                                    }
                                }
                            }
                            
                            log.info("参数映射: {} -> source: {}, field: {}", paramName, source, field);
                            
                            // 从上下文中获取映射的值
                            if ("workflow".equals(source) && field != null) {
                                // 从工作流输入参数中获取
                                if (workflowInputData.containsKey(field)) {
                                    stepParams.put(paramName, workflowInputData.get(field));
                                    log.info("从工作流输入获取: {} = {}", paramName, workflowInputData.get(field));
                                }
                            } else if (source.startsWith("step") && field != null) {
                                // 从之前步骤的结果中获取
                                if (context.containsKey(field)) {
                                    Object value = context.get(field);
                                    
                                    // 智能处理List类型：如果值是List且第一个元素是Map，尝试提取title字段
                                    if (value instanceof List && !((List<?>) value).isEmpty()) {
                                        List<?> list = (List<?>) value;
                                        Object firstItem = list.get(0);
                                        
                                        // 如果是Map类型（如标题池对象），尝试提取title字段
                                        if (firstItem instanceof Map) {
                                            Map<?, ?> map = (Map<?, ?>) firstItem;
                                            if (map.containsKey("title")) {
                                                value = map.get("title");
                                                log.info("从List<Map>中提取title字段: {} = {}", paramName, value);
                                            }
                                        } 
                                        // 如果只有一个元素且是简单类型，直接使用
                                        else if (list.size() == 1 && (firstItem instanceof String || firstItem instanceof Number)) {
                                            value = firstItem;
                                            log.info("从单元素List中提取值: {} = {}", paramName, value);
                                        }
                                    }
                                    
                                    stepParams.put(paramName, value);
                                    log.info("从步骤结果获取: {} = {} (类型: {})", paramName, value, 
                                        value != null ? value.getClass().getSimpleName() : "null");
                                } else {
                                    log.warn("上下文中未找到字段: {}, 当前上下文键: {}", field, context.keySet());
                                }
                            }
                        } else {
                            // 直接使用自定义值
                            if (paramValue.isTextual()) {
                                stepParams.put(paramName, paramValue.asText());
                                log.info("常量值(文本): {} = {}", paramName, paramValue.asText());
                            } else if (paramValue.isNumber()) {
                                stepParams.put(paramName, paramValue.asLong());
                                log.info("常量值(数字): {} = {}", paramName, paramValue.asLong());
                            } else if (paramValue.isBoolean()) {
                                stepParams.put(paramName, paramValue.asBoolean());
                                log.info("常量值(布尔): {} = {}", paramName, paramValue.asBoolean());
                            } else {
                                stepParams.put(paramName, paramValue);
                                log.info("常量值(其他): {} = {}", paramName, paramValue);
                            }
                        }
                    });
                }
                
                log.info("步骤参数: {}", stepParams);

                // 持久化步骤执行记录（running）
                long stepStartMs = System.currentTimeMillis();
                GbWorkflowStepExecution stepExec = new GbWorkflowStepExecution();
                stepExec.setExecutionId(executionId);
                stepExec.setStepId(currentStepId);
                stepExec.setStepName(stepName);
                stepExec.setStepType(stepType);
                stepExec.setToolCode(stepTargetCode);
                stepExec.setStatus("running");
                stepExec.setStartTime(new Date(stepStartMs));
                try { stepExec.setInputData(objectMapper.writeValueAsString(stepParams)); } catch (Exception ignore) {}
                try { stepExec.setStepConfig(configNode != null ? configNode.toString() : null); } catch (Exception ignore) {}
                try { stepExecutionMapper.insertStepExecution(stepExec); } catch (Exception dbEx) {
                    log.warn("保存步骤执行记录失败（不影响执行）: {}", dbEx.getMessage());
                }

                // 执行工具（支持 continueOnError）
                // 通过 atomicToolService.executeToolById 统一委托执行器查找和执行逻辑
                // 注意：executeToolById 返回格式为 {status, message, output:{...真实字段...}}
                Map<String, Object> stepResult;   // 包含真实输出字段（已解包 output 层）
                Map<String, Object> rawResult;    // executeToolById 原始返回，用于记录入库
                try {
                    if ("workflow".equals(stepType)) {
                        Map<String, Object> childResult = executeWorkflow(targetWorkflowCode, "sync", stepParams);
                        Object childStatus = childResult.get("status");
                        if (childStatus == null || !"success".equals(String.valueOf(childStatus))) {
                            throw new RuntimeException(String.valueOf(childResult.getOrDefault("message", "子工作流执行失败")));
                        }
                        @SuppressWarnings("unchecked")
                        Map<String, Object> childOutput = childResult.get("outputData") instanceof Map
                                ? (Map<String, Object>) childResult.get("outputData")
                                : new HashMap<>();
                        stepResult = childOutput;
                        rawResult = childResult;
                    } else {
                        rawResult = atomicToolService.executeToolById(tool.getId(), stepParams);

                        // executeToolById 用 status 字段表示框架级错误（工具不存在、未注册等）
                        if (rawResult != null && "error".equals(rawResult.get("status"))) {
                            throw new RuntimeException(String.valueOf(rawResult.getOrDefault("message", "工具执行框架错误")));
                        }

                        // 解包真实输出：executor.execute() 的返回值被包装在 "output" 字段里
                        @SuppressWarnings("unchecked")
                        Map<String, Object> outputMap = (rawResult != null && rawResult.get("output") instanceof Map)
                                ? (Map<String, Object>) rawResult.get("output")
                                : rawResult;
                        stepResult = outputMap != null ? outputMap : new HashMap<>();

                        // 检查工具软性失败（executor 内部返回 {success: false, message: ...}）
                        if (stepResult.containsKey("success") && Boolean.FALSE.equals(stepResult.get("success"))) {
                            String errMsg = stepResult.containsKey("message")
                                    ? String.valueOf(stepResult.get("message"))
                                    : "工具执行返回失败";
                            throw new RuntimeException(errMsg);
                        }
                    }

                    long stepEndMs = System.currentTimeMillis();
                    // 更新步骤记录为 success
                    GbWorkflowStepExecution stepUpd = new GbWorkflowStepExecution();
                    stepUpd.setExecutionId(executionId);
                    stepUpd.setStepId(currentStepId);
                    stepUpd.setStatus("success");
                    stepUpd.setEndTime(new Date(stepEndMs));
                    stepUpd.setDuration(stepEndMs - stepStartMs);
                    try { stepUpd.setOutputData(objectMapper.writeValueAsString(stepResult)); } catch (Exception ignore) {}
                    try { stepExecutionMapper.updateStepExecution(stepUpd); } catch (Exception dbEx) {
                        log.warn("更新步骤执行记录失败: {}", dbEx.getMessage());
                    }
                } catch (Exception stepEx) {
                    long stepEndMs = System.currentTimeMillis();
                    // 更新步骤记录为 failed
                    GbWorkflowStepExecution stepUpd = new GbWorkflowStepExecution();
                    stepUpd.setExecutionId(executionId);
                    stepUpd.setStepId(currentStepId);
                    stepUpd.setStatus("failed");
                    stepUpd.setError(stepEx.getMessage());
                    stepUpd.setEndTime(new Date(stepEndMs));
                    stepUpd.setDuration(stepEndMs - stepStartMs);
                    try { stepExecutionMapper.updateStepExecution(stepUpd); } catch (Exception dbEx) {
                        log.warn("更新步骤执行记录失败: {}", dbEx.getMessage());
                    }
                    if (allowContinueOnError) {
                        log.warn("步骤 {} 执行失败（continueOnError=true，继续后续步骤）: {}", stepName, stepEx.getMessage());
                        stepResult = new HashMap<>();
                        stepResult.put("_error", stepEx.getMessage());
                    } else {
                        if (continueOnError && "workflow".equals(stepType)) {
                            log.warn("步骤 {} 为子工作流步骤，忽略 continueOnError，按顺序中断执行", stepName);
                        }
                        throw stepEx;
                    }
                }

                // 保存步骤结果
                Map<String, Object> stepRecord = new HashMap<>();
                stepRecord.put("stepId", currentStepId);
                stepRecord.put("stepName", stepName);
                stepRecord.put("stepType", stepType);
                stepRecord.put("toolCode", stepTargetCode);
                stepRecord.put("result", stepResult);
                stepResults.add(stepRecord);

                // 将真实输出字段合并到上下文，供后续步骤通过字段名直接引用
                // stepResult 已是解包后的 output map，例如 {content, fileName, fileSize, success, ...}
                if (stepResult != null) {
                    context.putAll(stepResult);
                }
                
                log.info("步骤 {} 执行完成", stepName);
            }
            
            // 5. 根据outputs配置过滤输出数据
            Map<String, Object> filteredOutputData = new HashMap<>();
            JsonNode outputs = definition.get("outputs");
            
            if (outputs != null && outputs.isArray() && outputs.size() > 0) {
                log.info("开始处理工作流输出配置, outputs: {}", outputs);
                
                for (JsonNode output : outputs) {
                    String outputName = output.has("name") ? output.get("name").asText() : null;
                    String source = output.has("source") ? output.get("source").asText() : null;
                    
                    if (outputName != null && source != null) {
                        // 解析source格式: step_X.output.fieldName
                        String[] parts = source.split("\\.");
                        if (parts.length >= 3) {
                            String fieldName = parts[2]; // 提取字段名
                            
                            if (context.containsKey(fieldName)) {
                                filteredOutputData.put(outputName, context.get(fieldName));
                                log.info("输出字段: {} -> {} (从context.{}获取)", outputName, context.get(fieldName), fieldName);
                            } else {
                                log.warn("输出字段 {} 在上下文中未找到: {}", outputName, fieldName);
                            }
                        }
                    }
                }
            } else {
                // 如果没有配置outputs,返回所有上下文数据
                filteredOutputData = context;
                log.info("未配置outputs,返回所有上下文数据");
            }
            
            // 6. 构建返回结果
            long endTime = System.currentTimeMillis();
            Map<String, Object> result = new HashMap<>();
            result.put("executionId", executionId);
            result.put("workflowCode", workflowCode);
            result.put("mode", mode);
            result.put("status", "success");
            result.put("message", "执行成功");
            result.put("inputData", workflowInputData);
            result.put("outputData", filteredOutputData);
            result.put("stepResults", stepResults);
            result.put("startTime", new Date(startTime));
            result.put("endTime", new Date(endTime));
            result.put("duration", endTime - startTime);

            // 更新执行记录为 success
            try {
                GbWorkflowExecution upd = new GbWorkflowExecution();
                upd.setExecutionId(executionId);
                upd.setStatus("success");
                upd.setEndTime(new Date(endTime));
                upd.setDuration(endTime - startTime);
                upd.setOutputData(objectMapper.writeValueAsString(filteredOutputData));
                executionMapper.updateExecution(upd);
            } catch (Exception dbEx) {
                log.warn("更新执行记录失败: {}", dbEx.getMessage());
            }
            
            log.info("工作流执行成功: executionId={}, duration={}ms", executionId, endTime - startTime);
            
            return result;
            
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("工作流执行失败: workflowCode={}, executionId={}", workflowCode, executionId, e);
            
            Map<String, Object> result = new HashMap<>();
            result.put("executionId", executionId);
            result.put("workflowCode", workflowCode);
            result.put("mode", mode);
            result.put("status", "failed");
            result.put("message", "执行失败: " + e.getMessage());
            result.put("error", e.toString());
            result.put("inputData", workflowInputData);
            result.put("outputData", new HashMap<>());
            result.put("startTime", new Date(startTime));
            result.put("endTime", new Date(endTime));
            result.put("duration", endTime - startTime);

            // 更新执行记录为 failed
            try {
                GbWorkflowExecution upd = new GbWorkflowExecution();
                upd.setExecutionId(executionId);
                upd.setStatus("failed");
                upd.setEndTime(new Date(endTime));
                upd.setDuration(endTime - startTime);
                upd.setError(e.toString());
                executionMapper.updateExecution(upd);
            } catch (Exception dbEx) {
                log.warn("更新执行记录失败: {}", dbEx.getMessage());
            }

            return result;
        }
    }
    

    /**
     * 直接返回列表（供 Controller 层配合 PageHelper 使用）
     */
    public List<GbWorkflowExecution> listExecutionHistory(GbWorkflowExecution query) {
        return executionMapper.selectList(query);
    }

    /**
     * 按 executionId 查询单条执行记录
     */
    public GbWorkflowExecution getExecution(String executionId) {
        return executionMapper.selectByExecutionId(executionId);
    }

    @Override
    public List<Map<String, Object>> getExecutionHistory(String workflowCode)
    {
        GbWorkflowExecution query = new GbWorkflowExecution();
        query.setWorkflowCode(workflowCode);
        List<GbWorkflowExecution> list = executionMapper.selectList(query);
        List<Map<String, Object>> result = new ArrayList<>();
        for (GbWorkflowExecution exec : list) {
            Map<String, Object> item = new HashMap<>();
            item.put("executionId",  exec.getExecutionId());
            item.put("workflowCode", exec.getWorkflowCode());
            item.put("workflowName", exec.getWorkflowName());
            item.put("mode",         exec.getMode());
            item.put("status",       exec.getStatus());
            item.put("startTime",    exec.getStartTime());
            item.put("endTime",      exec.getEndTime());
            item.put("duration",     exec.getDuration());
            item.put("error",        exec.getError());
            item.put("inputData",    exec.getInputData());
            item.put("outputData",   exec.getOutputData());
            item.put("createTime",   exec.getCreateTime());
            result.add(item);
        }
        return result;
    }

    /**
     * 分页查询执行历史
     */
    public Map<String, Object> getExecutionHistoryPaged(GbWorkflowExecution query)
    {
        PageHelper.startPage(query.getPageNum() != null ? query.getPageNum() : 1,
                             query.getPageSize() != null ? query.getPageSize() : 10);
        List<GbWorkflowExecution> list = executionMapper.selectList(query);
        PageInfo<GbWorkflowExecution> page = new PageInfo<>(list);
        Map<String, Object> result = new HashMap<>();
        result.put("rows",  page.getList());
        result.put("total", page.getTotal());
        return result;
    }

    /**
     * 查询执行统计信息
     */
    public Map<String, Object> getExecutionStats(String workflowCode)
    {
        List<Map<String, Object>> stats = executionMapper.selectStatsByWorkflowCode(workflowCode);
        if (stats != null && !stats.isEmpty()) {
            return stats.get(0);
        }
        return new HashMap<>();
    }

    @Override
    public List<String> validateWorkflow(GbWorkflow gbWorkflow)
    {
        // TODO: 实现工作流验证逻辑
        List<String> errors = new ArrayList<>();
        if (gbWorkflow.getWorkflowCode() == null || gbWorkflow.getWorkflowCode().isEmpty()) {
            errors.add("工作流编码不能为空");
        }
        if (gbWorkflow.getWorkflowName() == null || gbWorkflow.getWorkflowName().isEmpty()) {
            errors.add("工作流名称不能为空");
        }
        return errors;
    }

    @Override
    public List<Map<String, Object>> getAvailableTools(Long siteId)
    {
        List<Map<String, Object>> tools = new ArrayList<>();
        
        try {
            // 查询指定网站可见且启用的原子工具
            com.ruoyi.gamebox.domain.GbAtomicTool query = new com.ruoyi.gamebox.domain.GbAtomicTool();
            query.setQueryMode("related");
            query.setSiteId(siteId);
            query.setEnabled(1);
            List<com.ruoyi.gamebox.domain.GbAtomicTool> atomicTools = atomicToolService.selectGbAtomicToolList(query);
            
            for (com.ruoyi.gamebox.domain.GbAtomicTool tool : atomicTools) {
                Map<String, Object> toolInfo = new HashMap<>();
                toolInfo.put("toolCode", tool.getToolCode());
                toolInfo.put("toolName", tool.getToolName());
                toolInfo.put("toolType", tool.getToolType());
                toolInfo.put("description", tool.getDescription());
                
                // 解析输入输出参数
                try {
                    if (tool.getInputs() != null && !tool.getInputs().isEmpty()) {
                        toolInfo.put("inputs", objectMapper.readValue(tool.getInputs(), List.class));
                    }
                    if (tool.getOutputs() != null && !tool.getOutputs().isEmpty()) {
                        toolInfo.put("outputs", objectMapper.readValue(tool.getOutputs(), List.class));
                    }
                } catch (Exception e) {
                    log.warn("解析工具参数失败: {}", tool.getToolCode(), e);
                }
                
                tools.add(toolInfo);
            }
        } catch (Exception e) {
            log.error("获取可用工具列表失败", e);
        }
        
        return tools;
    }

    @Override
    public Map<String, Object> validateStepMapping(String toolCode, Map<String, Object> inputMapping)
    {
        Map<String, Object> result = new HashMap<>();
        result.put("valid", true);
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        try {
            // 查询工具定义
            com.ruoyi.gamebox.domain.GbAtomicTool tool = atomicToolService.selectGbAtomicToolByCode(toolCode);
            if (tool == null) {
                result.put("valid", false);
                errors.add("工具不存在: " + toolCode);
                result.put("errors", errors);
                return result;
            }
            
            // 解析工具的输入参数要求
            List<Map<String, Object>> requiredInputs = new ArrayList<>();
            if (tool.getInputs() != null && !tool.getInputs().isEmpty()) {
                requiredInputs = objectMapper.readValue(tool.getInputs(), List.class);
            }
            
            // 检查必填参数是否都有映射
            for (Map<String, Object> input : requiredInputs) {
                String paramName = (String) input.get("name");
                Boolean required = (Boolean) input.getOrDefault("required", false);
                
                if (required && (inputMapping == null || !inputMapping.containsKey(paramName))) {
                    errors.add("缺少必填参数映射: " + paramName);
                }
            }
            
            // 检查映射的参数是否存在于工具定义中
            if (inputMapping != null) {
                for (String mappedParam : inputMapping.keySet()) {
                    boolean found = false;
                    for (Map<String, Object> input : requiredInputs) {
                        if (mappedParam.equals(input.get("name"))) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        warnings.add("未知的参数映射: " + mappedParam);
                    }
                }
            }
            
            if (!errors.isEmpty()) {
                result.put("valid", false);
            }
            result.put("errors", errors);
            result.put("warnings", warnings);
            
        } catch (Exception e) {
            log.error("验证步骤映射失败", e);
            result.put("valid", false);
            errors.add("验证失败: " + e.getMessage());
            result.put("errors", errors);
        }
        
        return result;
    }

    @Override
    public Map<String, Object> simulateWorkflow(GbWorkflow gbWorkflow, Map<String, Object> inputData) throws Exception
    {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("message", "模拟执行完成");
        
        List<Map<String, Object>> stepResults = new ArrayList<>();
        Map<String, Object> context = new HashMap<>();
        
        // 将输入数据放入上下文
        context.put("input", inputData);
        
        try {
            // 解析工作流定义
            String definitionStr = gbWorkflow.getDefinition();
            if (definitionStr == null || definitionStr.isEmpty()) {
                throw new Exception("工作流定义为空");
            }
            
            JsonNode definition = objectMapper.readTree(definitionStr);
            JsonNode steps = definition.get("steps");
            
            if (steps == null || !steps.isArray()) {
                throw new Exception("工作流步骤定义无效");
            }
            
            // 逐步执行
            for (int i = 0; i < steps.size(); i++) {
                JsonNode step = steps.get(i);
                
                Map<String, Object> stepResult = new HashMap<>();
                stepResult.put("stepId", step.get("stepId").asText());
                stepResult.put("stepName", step.get("stepName").asText());
                stepResult.put("toolCode", step.get("toolCode").asText());
                
                try {
                    // 解析输入映射
                    Map<String, Object> stepInputs = resolveInputMapping(step.get("inputMapping"), context);
                    stepResult.put("resolvedInputs", stepInputs);
                    
                    // 这里是模拟执行，不真正调用工具
                    // 实际执行时需要调用 atomicToolService.executeTool()
                    Map<String, Object> mockOutput = new HashMap<>();
                    mockOutput.put("status", "simulated");
                    mockOutput.put("message", "模拟执行，未实际调用工具");
                    
                    stepResult.put("status", "success");
                    stepResult.put("output", mockOutput);
                    
                    // 将输出添加到上下文
                    context.put("step_" + (i + 1), mockOutput);
                    
                } catch (Exception e) {
                    stepResult.put("status", "error");
                    stepResult.put("error", e.getMessage());
                    log.error("步骤执行失败", e);
                }
                
                stepResults.add(stepResult);
            }
            
            result.put("steps", stepResults);
            result.put("context", context);
            
        } catch (Exception e) {
            log.error("工作流模拟执行失败", e);
            result.put("status", "error");
            result.put("message", "模拟执行失败: " + e.getMessage());
            throw e;
        }
        
        return result;
    }

    /**
     * 解析输入映射，将变量替换为实际值
     */
    private Map<String, Object> resolveInputMapping(JsonNode inputMapping, Map<String, Object> context) throws Exception
    {
        Map<String, Object> resolved = new HashMap<>();
        
        if (inputMapping == null || inputMapping.isNull()) {
            return resolved;
        }
        
        Iterator<String> fieldNames = inputMapping.fieldNames();
        while (fieldNames.hasNext()) {
            String paramName = fieldNames.next();
            JsonNode mappingConfig = inputMapping.get(paramName);
            
            String source = mappingConfig.get("source").asText();
            String value = mappingConfig.get("value").asText();
            
            // 根据source类型解析值
            Object resolvedValue = resolveValue(source, value, context);
            resolved.put(paramName, resolvedValue);
        }
        
        return resolved;
    }

    /**
     * 根据来源解析值
     */
    private Object resolveValue(String source, String valuePath, Map<String, Object> context)
    {
        if ("constant".equals(source)) {
            // 常量值，直接返回
            return valuePath;
        } else if ("input".equals(source)) {
            // 从输入数据中获取
            return getNestedValue(context.get("input"), valuePath.replace("input.", ""));
        } else if (source.startsWith("step_")) {
            // 从前置步骤的输出中获取
            String stepKey = source;
            String path = valuePath.replace(stepKey + ".output.", "");
            Object stepOutput = context.get(stepKey);
            return getNestedValue(stepOutput, path);
        }
        
        return valuePath;
    }

    /**
     * 获取嵌套对象的值
     */
    private Object getNestedValue(Object obj, String path)
    {
        if (obj == null || path == null || path.isEmpty()) {
            return obj;
        }
        
        String[] parts = path.split("\\.");
        Object current = obj;
        
        for (String part : parts) {
            if (current instanceof Map) {
                current = ((Map<?, ?>) current).get(part);
            } else {
                return null;
            }
        }
        
        return current;
    }

    @Override
    public List<Map<String, Object>> getWorkflowSites(Long workflowId) {
        return workflowMapper.getWorkflowSites(workflowId);
    }

    @Override
    public int toggleWorkflowVisibility(Long siteId, Long workflowId, String isVisible) {
        return workflowMapper.toggleWorkflowVisibility(siteId, workflowId, isVisible);
    }

    @Override
    public List<Long> getExcludedSitesByWorkflow(Long workflowId) {
        return workflowMapper.getExcludedSitesByWorkflow(workflowId);
    }

    @Override
    @Transactional
    public Map<String, Object> importWorkflows(Map<String, Object> data, Boolean importRelatedTitles,
                                               Boolean importRelatedArticles, String updateStrategy) {
        Map<String, Object> result = new HashMap<>();
        int successCount = 0;
        int skipCount = 0;
        int updateCount = 0;
        List<String> errors = new ArrayList<>();
        
        try {
            List<Map<String, Object>> workflows = (List<Map<String, Object>>) data.get("workflows");
            if (workflows == null || workflows.isEmpty()) {
                result.put("success", false);
                result.put("message", "导入数据为空");
                return result;
            }
            
            for (Map<String, Object> workflowData : workflows) {
                try {
                    String workflowCode = (String) workflowData.get("workflowCode");
                    GbWorkflow existingWorkflow = workflowMapper.selectGbWorkflowByCode(workflowCode);
                    
                    if (existingWorkflow != null) {
                        if ("skip".equals(updateStrategy)) {
                            skipCount++;
                            continue;
                        } else if ("update".equals(updateStrategy)) {
                            // 更新现有工作流
                            existingWorkflow.setWorkflowName((String) workflowData.get("workflowName"));
                            existingWorkflow.setDescription((String) workflowData.get("description"));
                            existingWorkflow.setDefinition((String) workflowData.get("definition"));
                            existingWorkflow.setTriggerType((String) workflowData.get("triggerType"));
                            existingWorkflow.setStepCount((Integer) workflowData.get("stepCount"));
                            existingWorkflow.setEnabled((Integer) workflowData.get("enabled"));
                            workflowMapper.updateGbWorkflow(existingWorkflow);
                            updateCount++;
                        }
                    } else {
                        // 新增工作流
                        GbWorkflow newWorkflow = new GbWorkflow();
                        newWorkflow.setWorkflowCode(workflowCode);
                        newWorkflow.setWorkflowName((String) workflowData.get("workflowName"));
                        newWorkflow.setDescription((String) workflowData.get("description"));
                        newWorkflow.setDefinition((String) workflowData.get("definition"));
                        newWorkflow.setTriggerType((String) workflowData.get("triggerType"));
                        newWorkflow.setStepCount((Integer) workflowData.get("stepCount"));
                        newWorkflow.setEnabled((Integer) workflowData.get("enabled"));
                        newWorkflow.setCategoryId(workflowData.get("categoryId") != null ? 
                            Long.valueOf(workflowData.get("categoryId").toString()) : null);
                        newWorkflow.setCreateTime(new Date());
                        workflowMapper.insertGbWorkflow(newWorkflow);
                        successCount++;
                    }
                } catch (Exception e) {
                    errors.add("导入工作流失败: " + workflowData.get("workflowCode") + " - " + e.getMessage());
                    log.error("导入工作流失败", e);
                }
            }
            
            result.put("success", true);
            result.put("successCount", successCount);
            result.put("updateCount", updateCount);
            result.put("skipCount", skipCount);
            result.put("errorCount", errors.size());
            result.put("errors", errors);
            result.put("message", String.format("导入完成：成功%d，更新%d，跳过%d，失败%d", 
                successCount, updateCount, skipCount, errors.size()));
            
        } catch (Exception e) {
            log.error("导入工作流失败", e);
            result.put("success", false);
            result.put("message", "导入失败: " + e.getMessage());
        }
        
        return result;
    }

    // ====================================================================
    // 步骤执行记录
    // ====================================================================

    /**
     * 查询某次工作流执行的所有步骤执行记录
     */
    public List<GbWorkflowStepExecution> getStepExecutions(String executionId) {
        return stepExecutionMapper.selectByExecutionId(executionId);
    }

    // ====================================================================
    // 工作流模板
    // ====================================================================

    /**
     * 查询模板列表
     */
    public List<GbWorkflowTemplate> listTemplates(GbWorkflowTemplate query) {
        return templateMapper.selectList(query);
    }

    /**
     * 根据 ID 获取模板（含 definition）
     */
    public GbWorkflowTemplate getTemplateById(Long id) {
        return templateMapper.selectById(id);
    }

    /**
     * 从模板创建工作流
     */
    @Transactional
    public GbWorkflow createWorkflowFromTemplate(Long templateId, GbWorkflow tpl) {
        GbWorkflowTemplate template = templateMapper.selectById(templateId);
        if (template == null) {
            throw new IllegalArgumentException("模板不存在: " + templateId);
        }
        // 构建新工作流
        GbWorkflow wf = new GbWorkflow();
        wf.setWorkflowCode("wf_" + System.currentTimeMillis());
        wf.setWorkflowName(tpl != null && tpl.getWorkflowName() != null
                ? tpl.getWorkflowName() : template.getTemplateName());
        wf.setDescription(tpl != null && tpl.getDescription() != null
                ? tpl.getDescription() : template.getDescription());
        wf.setDefinition(template.getDefinition());
        wf.setTriggerType(tpl != null && tpl.getTriggerType() != null ? tpl.getTriggerType() : "manual");
        // siteId 为 null 时默认写 0（表示默认配置），避免创建后列表查不到
        Long siteIdVal = (tpl != null && tpl.getSiteId() != null) ? tpl.getSiteId() : 0L;
        wf.setSiteId(siteIdVal);
        wf.setCategoryId(tpl != null ? tpl.getCategoryId() : null);
        wf.setEnabled(1);
        // 统计步骤数
        try {
            com.fasterxml.jackson.databind.JsonNode def = objectMapper.readTree(template.getDefinition());
            com.fasterxml.jackson.databind.JsonNode stepArr = def.get("steps");
            wf.setStepCount(stepArr != null && stepArr.isArray() ? stepArr.size() : 0);
        } catch (Exception e) {
            wf.setStepCount(0);
        }
        wf.setCreateTime(new java.util.Date());
        workflowMapper.insertGbWorkflow(wf);
        // 模板使用次数 +1
        templateMapper.incrementUsageCount(templateId);
        return wf;
    }

    /**
     * 新增模板
     */
    public int addTemplate(GbWorkflowTemplate template) {
        return templateMapper.insertTemplate(template);
    }

    /**
     * 更新模板
     */
    public int updateTemplate(GbWorkflowTemplate template) {
        return templateMapper.updateTemplate(template);
    }

    /**
     * 删除模板
     */
    public int deleteTemplate(Long id) {
        return templateMapper.deleteById(id);
    }

    // ====================================================================
    // 全站导入
    // ====================================================================

    @Override
    @Transactional
    public Map<String, Object> importAllWorkflows(Map<String, Object> data, Boolean importRelatedTitles,
                                                   Boolean importRelatedArticles, String updateStrategy, Boolean clearExisting) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 如果需要清除现有数据
            if (clearExisting != null && clearExisting) {
                log.info("清除现有工作流数据");
                // 这里可以添加清除逻辑，但需要谨慎
                // workflowMapper.deleteAllWorkflows();
            }
            
            // 调用普通导入逻辑
            result = importWorkflows(data, importRelatedTitles, importRelatedArticles, updateStrategy);
            result.put("fullImport", true);
            
        } catch (Exception e) {
            log.error("全站导入工作流失败", e);
            result.put("success", false);
            result.put("message", "全站导入失败: " + e.getMessage());
        }
        
        return result;
    }
}
