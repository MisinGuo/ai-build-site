package com.ruoyi.gamebox.service;

import com.ruoyi.gamebox.service.IGbCodeManageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 站点重建任务服务
 * <p>
 * 将耗时的 setupFromTemplate 操作（克隆模板 → 注入配置 → git push）改为异步执行，
 * 前端通过轮询 /rebuild-status?taskId=xxx 获取进度与日志。
 * </p>
 */
@Service
public class GbRebuildTaskService {

    private static final Logger log = LoggerFactory.getLogger(GbRebuildTaskService.class);

    /** 自引用：让 @Async 代理生效（Spring AOP 不拦截 this.xxx() 内部调用） */
    @Autowired
    @Lazy
    private GbRebuildTaskService self;

    @Autowired
    private IGbCodeManageService codeManageService;

    /** 任务状态存储（内存，重启后清空，满足场景需求） */
    private final ConcurrentHashMap<String, RebuildTask> tasks = new ConcurrentHashMap<>();

    /**
     * 提交一个重建任务，立即返回 taskId，实际工作在线程池中异步执行。
     */
    public String submit(Long siteId, Map<String, Object> params) {
        String taskId = UUID.randomUUID().toString();
        tasks.put(taskId, new RebuildTask(taskId));
        log.info("[重建任务] 提交异步任务 taskId={} siteId={}", taskId, siteId);
        self.executeAsync(siteId, params, taskId);
        return taskId;
    }

    /**
     * 异步执行重建，通过 threadPoolTaskExecutor 线程池调度。
     * 将 task.logBuffer 传给 setupFromTemplate，使日志在执行过程中实时写入，
     * 前端轮询时即可通过 getLog() 读到中间步骤。
     */
    @Async("threadPoolTaskExecutor")
    public void executeAsync(Long siteId, Map<String, Object> params, String taskId) {
        RebuildTask task = tasks.get(taskId);
        if (task == null) return;

        log.info("[重建任务] 开始执行 taskId={} siteId={}", taskId, siteId);
        try {
            Map<String, Object> result = codeManageService.setupFromTemplate(siteId, params, task.logBuffer);
            boolean success = Boolean.TRUE.equals(result.get("success"));
            task.status  = success ? "SUCCESS" : "FAILED";
            task.message = String.valueOf(result.getOrDefault("message", ""));
            log.info("[重建任务] 完成 taskId={} success={} message={}", taskId, success, task.message);
        } catch (Exception e) {
            log.error("[重建任务] 异常 taskId={} siteId={}", taskId, siteId, e);
            task.logBuffer.append("[ERROR] ").append(e.getMessage()).append("\n");
            task.status  = "FAILED";
            task.message = "执行异常: " + e.getMessage();
        }
    }

    /**
     * 查询任务状态，前端轮询时调用。
     */
    public RebuildTask getTask(String taskId) {
        return tasks.get(taskId);
    }

    // ── VO ─────────────────────────────────────────────────────────────────────

    public static class RebuildTask {
        public final String taskId;
        /** RUNNING / SUCCESS / FAILED */
        public volatile String status  = "RUNNING";
        /** 与 setupFromTemplate 共享的日志缓冲区，实时追加，轮询时可随时读取 */
        public final StringBuilder logBuffer = new StringBuilder();
        public volatile String message = "重建中，请稍候...";
        public final long startTime    = System.currentTimeMillis();

        public RebuildTask(String taskId) {
            this.taskId = taskId;
        }

        /** 返回当前已累积的日志快照（线程安全的字符串读取） */
        public String getLog() {
            return logBuffer.toString();
        }

        public boolean isFinished() {
            return "SUCCESS".equals(status) || "FAILED".equals(status);
        }
    }
}
