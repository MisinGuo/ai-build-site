package com.ruoyi.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocket 配置
 * <p>
 * 向 Spring 注册 ServerEndpointExporter，让所有标注了 {@code @ServerEndpoint}
 * 的端点类能够被嵌入式 Tomcat 的 WebSocket 容器自动发现并激活。
 * </p>
 */
@Configuration
public class WebSocketConfig {

    /**
     * 使 {@code @ServerEndpoint} 注解在 Spring Boot 嵌入式 Tomcat 下生效。
     * <p>注意：若部署到外部 Tomcat，需移除此 Bean，由容器自己扫描。</p>
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
