/**
 * WebSocket配置类
 * 配置Spring WebSocket消息代理和STOMP端点
 * 
 * 功能：
 * - 启用WebSocket消息代理
 * - 配置消息代理前缀和应用程序前缀
 * - 注册STOMP端点用于实时进度推送
 * - 支持跨域连接和SockJS回退
 */
package com.storyteller.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.SockJsServiceRegistration;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    /**
     * 配置消息代理
     * 
     * 设置消息代理前缀和应用程序目标前缀
     * 
     * @param config 消息代理注册器
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 启用简单的内存消息代理，目的地以"/topic"开头
        config.enableSimpleBroker("/topic");
        // 设置应用程序目标前缀为"/app"
        config.setApplicationDestinationPrefixes("/app");
    }
    
    /**
     * 注册STOMP端点
     * 
     * 配置WebSocket连接端点和SockJS支持
     * 
     * @param registry STOMP端点注册器
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 注册WebSocket端点"/ws/progress"用于进度推送
        registry.addEndpoint("/ws/progress")
                // 允许所有来源的跨域请求
                .setAllowedOriginPatterns("*")
                // 启用SockJS支持，提供WebSocket回退选项
                .withSockJS();
    }
}