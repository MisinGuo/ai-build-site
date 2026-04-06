package com.ruoyi.web.websocket;

import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.gamebox.service.IGbCodeManageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.*;

/**
 * WebSocket 端点：git pull / build / pnpm install / git push 日志实时推送
 *
 * <p>路径：{@code ws(s)://host/ws/op-log/{siteId}/{opType}}
 * <ul>
 *   <li>{@code opType} 可取值：{@code pull} | {@code build} | {@code install} | {@code push}</li>
 * </ul>
 *
 * <p>前端先通过 HTTP 触发对应操作（立即返回），随即建立 WebSocket 连接。
 * 服务端共享 {@link ScheduledExecutorService} 调度 tick，每 200ms 轮读
 * {@link IGbCodeManageService#getPullStatus} 等接口返回的日志增量并推送。
 *
 * <h3>消息格式</h3>
 * <pre>
 * // 增量日志
 * {"type":"log","data":"新增日志文本"}
 * // 操作完成
 * {"type":"done","success":true,"message":"推送成功"}
 * </pre>
 */
@Component
@ServerEndpoint("/ws/op-log/{siteId}/{opType}")
public class OpLogEndpoint {

    private static final Logger log = LoggerFactory.getLogger(OpLogEndpoint.class);

    /** tick 间隔（ms）：运维日志对实时性要求低于重建，200ms 即可 */
    private static final long POLL_INTERVAL_MS = 200;

    /** 等待操作开始的超时（ms）：30 秒 */
    private static final long OP_START_TIMEOUT_MS = 30_000L;

    /** 最长推送时间（ms）：60 分钟 */
    private static final long MAX_PUSH_DURATION_MS = 60 * 60 * 1000L;

    private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(
            Runtime.getRuntime().availableProcessors(),
            r -> {
                Thread t = new Thread(r, "ws-oplog-sched");
                t.setDaemon(true);
                return t;
            }
    );

    private static final ConcurrentHashMap<String, PushState> PUSH_STATES = new ConcurrentHashMap<>();

    // ─────────────────────────────────────────────────────────────────────────

    @OnOpen
    public void onOpen(Session session,
                       @PathParam("siteId") String siteId,
                       @PathParam("opType") String opType) {
        long siteIdLong;
        try {
            siteIdLong = Long.parseLong(siteId);
        } catch (NumberFormatException e) {
            sendText(session, "{\"type\":\"done\",\"success\":false,\"message\":\"无效的 siteId\"}");
            closeQuietly(session);
            return;
        }
        if (!isValidOpType(opType)) {
            sendText(session, "{\"type\":\"done\",\"success\":false,\"message\":\"未知操作类型: " + opType + "\"}");
            closeQuietly(session);
            return;
        }
        log.info("[WS-OP] 连接建立 siteId={} opType={} sessionId={}", siteId, opType, session.getId());
        PushState state = new PushState(siteIdLong);
        PUSH_STATES.put(session.getId(), state);
        state.future = SCHEDULER.scheduleWithFixedDelay(
                () -> pushTick(session, siteIdLong, opType, state),
                0, POLL_INTERVAL_MS, TimeUnit.MILLISECONDS
        );
    }

    @OnClose
    public void onClose(Session session,
                        @PathParam("siteId") String siteId,
                        @PathParam("opType") String opType) {
        log.info("[WS-OP] 连接关闭 siteId={} opType={} sessionId={}", siteId, opType, session.getId());
        cancelState(session.getId());
    }

    @OnError
    public void onError(Session session, Throwable error,
                        @PathParam("siteId") String siteId,
                        @PathParam("opType") String opType) {
        log.warn("[WS-OP] 连接异常 siteId={} opType={} sessionId={} error={}",
                siteId, opType, session.getId(), error.getMessage());
        cancelState(session.getId());
    }

    // ─────────────────────────────────────────────────────────────────────────

    private static void pushTick(Session session, long siteId, String opType, PushState state) {
        try {
            if (!session.isOpen()) {
                cancelState(session.getId());
                return;
            }

            IGbCodeManageService svc = SpringUtils.getBean(IGbCodeManageService.class);
            Map<String, Object> status = getStatus(svc, siteId, opType);

            // 1. 尚未有日志且未结束时，检查等待超时
            String fullLog = status.get("log") instanceof String ? (String) status.get("log") : "";
            boolean done = Boolean.TRUE.equals(status.get("done"));

            if (!done && fullLog.isEmpty()) {
                if (System.currentTimeMillis() - state.startTime > OP_START_TIMEOUT_MS) {
                    sendText(session, "{\"type\":\"done\",\"success\":false,\"message\":\"等待操作启动超时\"}");
                    cancelState(session.getId());
                    closeQuietly(session);
                }
                return;
            }

            // 2. 推送日志增量
            if (fullLog.length() > state.sentLen) {
                String chunk = fullLog.substring(state.sentLen);
                state.sentLen = fullLog.length();
                sendText(session, "{\"type\":\"log\",\"data\":" + jsonEscape(chunk) + "}");
            }

            // 3. 总时长保护
            if (System.currentTimeMillis() - state.startTime > MAX_PUSH_DURATION_MS) {
                log.warn("[WS-OP] 推送超时 siteId={} opType={}", siteId, opType);
                sendText(session, "{\"type\":\"done\",\"success\":false,\"message\":\"推送超时\"}");
                cancelState(session.getId());
                closeQuietly(session);
                return;
            }

            // 4. 操作完成
            if (done) {
                boolean success = Boolean.TRUE.equals(status.get("success"));
                String msg = success ? opSuccessMsg(opType) : opFailMsg(opType);
                sendText(session, "{\"type\":\"done\",\"success\":" + success + ",\"message\":" + jsonEscape(msg) + "}");
                cancelState(session.getId());
                closeQuietly(session);
            }
        } catch (Exception e) {
            log.error("[WS-OP] tick 异常 siteId={} opType={} sessionId={}", siteId, opType, session.getId(), e);
            cancelState(session.getId());
            closeQuietly(session);
        }
    }

    private static Map<String, Object> getStatus(IGbCodeManageService svc, long siteId, String opType) {
        switch (opType) {
            case "pull":    return svc.getPullStatus(siteId);
            case "build":   return svc.getBuildStatus(siteId);
            case "install": return svc.getInstallStatus(siteId);
            case "push":    return svc.getPushStatus(siteId);
            default:        throw new IllegalArgumentException("Unknown opType: " + opType);
        }
    }

    private static boolean isValidOpType(String opType) {
        return "pull".equals(opType) || "build".equals(opType)
                || "install".equals(opType) || "push".equals(opType);
    }

    private static String opSuccessMsg(String opType) {
        switch (opType) {
            case "pull":    return "拉取成功";
            case "build":   return "构建成功";
            case "install": return "依赖安装完成";
            case "push":    return "代码推送成功";
            default:        return "操作成功";
        }
    }

    private static String opFailMsg(String opType) {
        switch (opType) {
            case "pull":    return "拉取失败，请查看日志";
            case "build":   return "构建失败，请查看日志";
            case "install": return "安装失败，请查看日志";
            case "push":    return "推送失败，请查看日志";
            default:        return "操作失败";
        }
    }

    private static void cancelState(String sessionId) {
        PushState state = PUSH_STATES.remove(sessionId);
        if (state != null && state.future != null) {
            state.future.cancel(false);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────

    private static class PushState {
        final long siteId;
        volatile int sentLen = 0;
        final long startTime = System.currentTimeMillis();
        volatile ScheduledFuture<?> future;

        PushState(long siteId) {
            this.siteId = siteId;
        }
    }

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
            log.debug("[WS-OP] 发送消息失败: {}", e.getMessage());
        }
    }

    private static void closeQuietly(Session session) {
        try {
            if (session.isOpen()) session.close();
        } catch (IOException ignored) {
        }
    }
}
