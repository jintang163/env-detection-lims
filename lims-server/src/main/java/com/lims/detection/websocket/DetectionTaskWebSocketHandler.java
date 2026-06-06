package com.lims.detection.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lims.detection.vo.WebSocketMessageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class DetectionTaskWebSocketHandler extends TextWebSocketHandler {

    private static final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Long, String> userSessions = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Long, ConcurrentHashMap<String, WebSocketSession>> deptSessions = new ConcurrentHashMap<>();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = session.getId();
        sessions.put(sessionId, session);

        String query = session.getUri().getQuery();
        if (query != null) {
            for (String param : query.split("&")) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2) {
                    if ("userId".equals(keyValue[0])) {
                        Long userId = Long.parseLong(keyValue[1]);
                        userSessions.put(userId, sessionId);
                        log.info("用户 {} 建立WebSocket连接，SessionId: {}", userId, sessionId);
                    } else if ("deptId".equals(keyValue[0])) {
                        Long deptId = Long.parseLong(keyValue[1]);
                        deptSessions.computeIfAbsent(deptId, k -> new ConcurrentHashMap<>())
                                .put(sessionId, session);
                        log.info("科室 {} 建立WebSocket连接，SessionId: {}", deptId, sessionId);
                    }
                }
            }
        }

        log.info("WebSocket连接建立成功，SessionId: {}，当前连接数: {}", sessionId, sessions.size());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.debug("收到WebSocket消息，SessionId: {}，内容: {}", session.getId(), message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String sessionId = session.getId();
        sessions.remove(sessionId);

        userSessions.entrySet().removeIf(entry -> sessionId.equals(entry.getValue()));

        for (ConcurrentHashMap<String, WebSocketSession> deptSessionMap : deptSessions.values()) {
            deptSessionMap.remove(sessionId);
        }

        log.info("WebSocket连接关闭，SessionId: {}，当前连接数: {}", sessionId, sessions.size());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket传输错误，SessionId: {}", session.getId(), exception);
    }

    public void sendMessage(WebSocketMessageVO message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            TextMessage textMessage = new TextMessage(json);

            if (message.getTargetUserId() != null) {
                String sessionId = userSessions.get(message.getTargetUserId());
                if (sessionId != null) {
                    WebSocketSession session = sessions.get(sessionId);
                    if (session != null && session.isOpen()) {
                        session.sendMessage(textMessage);
                        log.info("发送WebSocket消息给用户 {}: {}", message.getTargetUserId(), json);
                    }
                }
            } else if (message.getTargetDeptId() != null) {
                ConcurrentHashMap<String, WebSocketSession> deptSessionMap = deptSessions.get(message.getTargetDeptId());
                if (deptSessionMap != null) {
                    for (WebSocketSession session : deptSessionMap.values()) {
                        if (session.isOpen()) {
                            session.sendMessage(textMessage);
                        }
                    }
                    log.info("发送WebSocket消息给科室 {}: {}", message.getTargetDeptId(), json);
                }
            } else {
                for (WebSocketSession session : sessions.values()) {
                    if (session.isOpen()) {
                        session.sendMessage(textMessage);
                    }
                }
                log.info("广播WebSocket消息: {}", json);
            }
        } catch (Exception e) {
            log.error("发送WebSocket消息失败", e);
        }
    }

    public void sendToUser(Long userId, WebSocketMessageVO message) {
        message.setTargetUserId(userId);
        sendMessage(message);
    }

    public void sendToDept(Long deptId, WebSocketMessageVO message) {
        message.setTargetDeptId(deptId);
        sendMessage(message);
    }

    public void broadcast(WebSocketMessageVO message) {
        sendMessage(message);
    }
}
