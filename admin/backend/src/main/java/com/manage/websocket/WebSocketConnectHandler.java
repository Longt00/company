package com.manage.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * WebSocket连接事件处理器
 *
 * @author System
 * @version 1.0
 */
@Slf4j
@Component
public class WebSocketConnectHandler {

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = headerAccessor.getUser().getName();
        String sessionId = headerAccessor.getSessionId();

        log.info("WebSocket连接建立，用户：{}，会话ID：{}", username, sessionId);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = headerAccessor.getUser().getName();
        String sessionId = headerAccessor.getSessionId();

        log.info("WebSocket连接断开，用户：{}，会话ID：{}", username, sessionId);
    }
}