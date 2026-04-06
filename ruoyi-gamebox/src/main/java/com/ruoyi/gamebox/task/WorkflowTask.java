package com.ruoyi.gamebox.task;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.gamebox.service.IGbWorkflowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;

/**
 * 工作流定时任务执行器
 * <p>
 * Quartz invokeTarget 格式：workflowTask.executeWorkflow('wf_xxx')
 * </p>
 *
 * @author ruoyi
 */
@Component("workflowTask")
public class WorkflowTask {

    private static final Logger log = LoggerFactory.getLogger(WorkflowTask.class);

    /** 定时任务使用的虚拟操作人标识 */
    private static final String SYSTEM_USER = "scheduled";

    @Autowired
    private IGbWorkflowService gbWorkflowService;

    /**
     * 按工作流编码执行工作流（由 Quartz 定时调用）
     *
     * @param workflowCode 工作流编码
     */
    public void executeWorkflow(String workflowCode) {
        log.info("[定时触发] 开始执行工作流: {}", workflowCode);
        // Quartz 线程没有 Spring Security 上下文，注入虚拟系统用户
        // 使所有 SecurityUtils.getUsername() / getUserId() 调用可正常返回
        setSystemSecurityContext();
        try {
            gbWorkflowService.executeWorkflow(workflowCode, "scheduled", new HashMap<>());
            log.info("[定时触发] 工作流执行成功: {}", workflowCode);
        } catch (Exception e) {
            log.error("[定时触发] 工作流执行失败: {}, 原因: {}", workflowCode, e.getMessage(), e);
            // 不向上抛出，避免 Quartz 标记任务为 ERROR 状态
        } finally {
            // 执行完毕后清理 SecurityContext，防止线程池复用时泄露
            SecurityContextHolder.clearContext();
        }
    }

    /**
     * 向当前线程的 SecurityContextHolder 注入一个虚拟系统用户，
     * 使下游 SecurityUtils.getUsername() / getUserId() 能正常返回而不抛异常。
     */
    private void setSystemSecurityContext() {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(0L);
        sysUser.setUserName(SYSTEM_USER);
        sysUser.setPassword("");  // getPassword() 会读此字段，避免 NPE

        LoginUser loginUser = new LoginUser(0L, null, sysUser, Collections.emptySet());

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(loginUser, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
