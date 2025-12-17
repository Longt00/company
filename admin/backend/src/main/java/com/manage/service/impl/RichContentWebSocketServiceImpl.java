package com.manage.service.impl;

import com.manage.dto.richcontent.RichContentResponse;
import com.manage.service.RichContentService;
import com.manage.service.RichContentWebSocketService;
import com.manage.websocket.RichContentWebSocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * 富文本内容WebSocket服务实现类
 *
 * @author System
 * @version 1.0
 */
@Slf4j
@Service
public class RichContentWebSocketServiceImpl implements RichContentWebSocketService {

    private final RichContentWebSocketHandler webSocketHandler;
    private RichContentService richContentService;

    public RichContentWebSocketServiceImpl(RichContentWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    @Autowired
    @Lazy
    public void setRichContentService(RichContentService richContentService) {
        this.richContentService = richContentService;
    }

    @Override
    public void notifyContentUpdate(Long contentId, String updateType, String updatedBy, RichContentResponse content) {
        try {
            log.info("通知内容更新，内容ID：{}，更新类型：{}，更新人：{}", contentId, updateType, updatedBy);

            // 通过WebSocket处理器发送更新通知
            webSocketHandler.sendToContentSubscribers(contentId, content, "content_update");

        } catch (Exception e) {
            log.error("通知内容更新失败，内容ID：{}，错误：{}", contentId, e.getMessage(), e);
        }
    }

    @Override
    public void notifyContentPublish(Long contentId, String publishedBy, RichContentResponse content, String notification) {
        try {
            log.info("通知内容发布，内容ID：{}，发布人：{}", contentId, publishedBy);

            // 广播发布通知
            if (notification != null && !notification.trim().isEmpty()) {
                webSocketHandler.broadcastSystemMessage(notification, "content_publish");
            }

            // 通知内容订阅者
            webSocketHandler.sendToContentSubscribers(contentId, content, "content_publish");

        } catch (Exception e) {
            log.error("通知内容发布失败，内容ID：{}，错误：{}", contentId, e.getMessage(), e);
        }
    }

    @Override
    public void notifyCollaboration(Long contentId, String user, String operation, Object data) {
        try {
            log.info("通知内容协作，内容ID：{}，用户：{}，操作：{}", contentId, user, operation);

            // 通知协作者
            webSocketHandler.sendToContentSubscribers(contentId, data, "collaboration");

        } catch (Exception e) {
            log.error("通知内容协作失败，内容ID：{}，错误：{}", contentId, e.getMessage(), e);
        }
    }

    @Override
    public void broadcastSystemMessage(String message, String type) {
        try {
            log.info("广播系统消息，类型：{}，消息：{}", type, message);
            webSocketHandler.broadcastSystemMessage(message, type);

        } catch (Exception e) {
            log.error("广播系统消息失败，错误：{}", e.getMessage(), e);
        }
    }

    @Override
    public void sendToUser(String username, String message, String type) {
        try {
            log.info("向用户发送消息，用户：{}，类型：{}，消息：{}", username, type, message);
            webSocketHandler.sendToUser(username, message, type);

        } catch (Exception e) {
            log.error("向用户发送消息失败，用户：{}，错误：{}", username, e.getMessage(), e);
        }
    }

    @Override
    public void notifyContentSubscribers(Long contentId, Object message, String type) {
        try {
            log.info("通知内容订阅者，内容ID：{}，类型：{}", contentId, type);
            webSocketHandler.sendToContentSubscribers(contentId, message, type);

        } catch (Exception e) {
            log.error("通知内容订阅者失败，内容ID：{}，错误：{}", contentId, e.getMessage(), e);
        }
    }
}