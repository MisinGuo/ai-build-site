package com.ruoyi.web.websocket;

import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.gamebox.service.GbRebuildTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.*;

/**
 * WebSocket 端点：重建日志实时推送
 *
 * <p>客户端在提交重建任务拿到 taskId 后，直接连接：
 * {@code ws(s)://host/ws/rebuild-log/{taskId}}
 *
 * <p>使用共享 {@link ScheduledExecutorService} 替代每连接一线程的方案：
 * N 个并发连接共用固定大小的调度线程池（核心数个线程），每次 tick 任务仅
 * 读取 {@link GbRebuildTaskService.RebuildTask#logBuffer} 增量并发送，
 * 执行时间极短（微秒级），线程压力不随连接数线性增长。
 *
 * <h3>消息格式</h3>
 * <pre>
 * // 增量日志
 * {"type":"log","data":"新增日志内容"}
 *
 * // 任务结束
 * {"type":"done","success":true,"message":"重建成功"}
 * </pre>
 *
 * <p>Spring DI 说明：{@code @ServerEndpoint} 实例由 Tomcat 创建，不经过 Spring 容器，
 * 因此通过 {@link SpringUtils#getBean} 获取服务 bean。
 */
@Component
@ServerEndpoint("/ws/rebuild-log/{taskId}")
public class RebuildLogEndpoint {

    private static final Logger log = LoggerFactory.getLogger(RebuildLogEndpoint.class);

    /** tick 间隔（ms）：每 100ms 检查一次 logBuffer 增量 */
    private static final long POLL_INTERVAL_MS = 100;

    /** 等待任务出现的最长时间（ms）：30 秒 */
    private static final long TASK_WAIT_TIMEOUT_MS = 30_000L;

    /** 单个连接最长推送时间（ms）：60 分钟 */
    private static final long MAX_PUSH_DURATION_MS = 60 * 60 * 1000L;

    /**
     * 共享调度线程池。
     * <p>核心线程数 = CPU 核心数（通常 4-8），足以支撑大量并发 WS 连接，
     * 因为每个 tick 任务是 CPU 极轻的 I/O 读取 + 写操作，几乎不占用线程时间。
     * <p>使用 {@code scheduleWithFixedDelay} 而非 {@code scheduleAtFixedRate}：
     * 前者在上一个 tick 完成后才计时，避免慢发送时任务堆积。
     */
    private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(
            Runtime.getRuntime().availableProcessors(),
            r -> {
                Thread t = new Thread(r, "ws-rebuild-sched");
                t.setDaemon(true);
                return t;
            }
    );

    /** 每个会话的推送状态，keyed by session.getId() */
    private static final ConcurrentHashMap<String, PushState> PUSH_STATES = new ConcurrentHashMap<>();

    // ─────────────────────────────────────────────────────────────────────────

    @OnOpen
    public void onOpen(Session session, @PathParam("taskId") String taskId) {
        log.info("[WS] 连接建立 taskId={} sessionId={}", taskId, session.getId());
        PushState state = new PushState();
        PUSH_STATES.put(session.getId(), state);
        // scheduleWithFixedDelay：上一次 tick 完成后等 POLL_INTERVAL_MS 再执行下一次
        state.future = SCHEDULER.scheduleWithFixedDelay(
                () -> pushTick(session, taskId, state),
                0, POLL_INTERVAL_MS, TimeUnit.MILLISECONDS
        );
    }

    @OnClose
    public void onClose(Session session, @PathParam("taskId") String taskId) {
        log.info("[WS] 连接关闭 taskId={} sessionId={}", taskId, session.getId());
        cancelState(session.getId());
    }

    @OnError
    public void onError(Session session, Throwable error, @PathParam("taskId") String taskId) {
        log.warn("[WS] 连接异常 taskId={} sessionId={} error={}", taskId, session.getId(), error.getMessage());
        cancelState(session.getId());
    }

    // ─────────────────────────────────────────────────────────────────────────

    /**
     * 每个 tick 的逻辑：读取 logBuffer 增量 → 推送 → 检查完成。
     * <p>此方法在调度线程上执行，不允许阻塞（无 sleep/wait）。
     */
    private static void pushTick(Session session, String taskId, PushState state) {
        try {
            // 连接已关闭则取消调度
            if (!session.isOpen()) {
                cancelState(session.getId());
                return;
            }

            // 1. 等待任务出现
            if (state.task == null) {
                GbRebuildTaskService svc = SpringUtils.getBean(GbRebuildTaskService.class);
                state.task = svc.getTask(taskId);
                if (state.task == null) {
                    if (System.currentTimeMillis() - state.startTime > TASK_WAIT_TIMEOUT_MS) {
                        sendText(session, "{\"type\":\"done\",\"success\":false,\"message\":\"任务不存在或已过期\"}");
                        cancelState(session.getId());
                        closeQuietly(session);
                    }
                    return; // 继续等待下一个 tick
                }
            }

            // 2. 超时保护
            if (System.currentTimeMillis() - state.startTime > MAX_PUSH_DURATION_MS) {
                log.warn("[WS] 推送超时 taskId={}", taskId);
                sendText(session, "{\"type\":\"done\",\"success\":false,\"message\":\"推送超时，请刷新页面查看最终状态\"}");
                cancelState(session.getId());
                closeQuietly(session);
                return;
            }

            // 3. 推送 logBuffer 增量
            String currentLog = state.task.getLog();
            if (currentLog.length() > state.sentLen) {
                String chunk = currentLog.substring(state.sentLen);
                state.sentLen = currentLog.length();
                sendText(session, "{\"type\":\"log\",\"data\":" + jsonEscape(chunk) + "}");
            }

            // 4. 任务完成则发送结束帧并关闭
            if (state.task.isFinished()) {
                boolean success = "SUCCESS".equals(state.task.status);
                String msg = state.task.message != null ? state.task.message : (success ? "重建成功" : "重建失败");
                sendText(session, "{\"type\":\"done\",\"success\":" + success + ",\"message\":" + jsonEscape(msg) + "}");
                cancelState(session.getId());
                closeQuietly(session);
            }
        } catch (Exception e) {
            log.error("[WS] tick 异常 taskId={} sessionId={}", taskId, session.getId(), e);
            cancelState(session.getId());
            closeQuietly(session);
        }
    }

    private static void cancelState(String sessionId) {
        PushState state = PUSH_STATES.remove(sessionId);
        if (state != null && state.future != null) {
            state.future.cancel(false); // false：不打断正在执行的 tick
        }
    }

    // ─────────────────────────────────────────────────────────────────────────

    /** 每个 WebSocket 会话的推送状态（非线程安全，但同一 session 的 tick 是串行的） */
    private static class PushState {
        volatile GbRebuildTaskService.RebuildTask task;
        volatile int sentLen = 0;
        final long startTime = System.currentTimeMillis();
        volatile ScheduledFuture<?> future;
    }

    /**
     * 简单 JSON 字符串转义：将原始字符串包装为带引号的 JSON 字符串值。
     */
    private static String jsonEscape(String raw) {
        if (raw == null) return "\"\"";
        String escaped = raw
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r\n", "\\n")
                .replace("\n", "\\n")
                .replace("\r", "\\n")
                .replace("\t", "\\t");
        return "\"" + escaped + "\"";
    }

    private static void sendText(Session session, String text) {
        if (!session.isOpen()) return;
        try {
            session.getBasicRemote().sendText(text);
        } catch (IOException e) {
            log.debug("[WS] 发送消息失败，连接可能已断开: {}", e.getMessage());
        }
    }

    private static void closeQuietly(Session session) {
        try {
            if (session.isOpen()) {
                session.close();
            }
        } catch (IOException ignored) {
        }
    }
}
