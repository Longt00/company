package com.manage.websocket;

import com.manage.dto.richcontent.RichContentResponse;
import com.manage.service.RichContentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 富文本内容WebSocket处理器
 *
 * @author System
 * @version 1.0
 */
@Slf4j
@Controller
public class RichContentWebSocketHandler {

    private RichContentService richContentService;
    private final SimpMessagingTemplate messagingTemplate;

    public RichContentWebSocketHandler(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Autowired
    @Lazy
    public void setRichContentService(RichContentService richContentService) {
        this.richContentService = richContentService;
    }

    /**
     * 处理内容更新通知
     */
    @MessageMapping("/content/update")
    public void handleContentUpdate(@Payload Map<String, Object> message, Principal principal) {
        try {
            Long contentId = Long.valueOf(message.get("contentId").toString());
            String updateType = message.get("updateType").toString();

            log.info("收到内容更新通知，内容ID：{}，更新类型：{}，用户：{}",
                    contentId, updateType, principal.getName());

            // 获取最新的内容信息
            RichContentResponse contentResponse = richContentService.getContentById(contentId);

            // 构建通知消息
            Map<String, Object> notification = new HashMap<>();
            notification.put("type", "content_update");
            notification.put("contentId", contentId);
            notification.put("updateType", updateType);
            notification.put("content", contentResponse);
            notification.put("timestamp", LocalDateTime.now());
            notification.put("updatedBy", principal.getName());

            // 向特定主题发送更新通知
            messagingTemplate.convertAndSend("/topic/content/" + contentId + "/updates", notification);

            // 向订阅该内容更新的用户发送通知
            messagingTemplate.convertAndSendToUser(principal.getName(), "/content/updates", notification);

        } catch (Exception e) {
            log.error("处理内容更新通知失败：{}", e.getMessage(), e);

            // 发送错误消息给用户
            Map<String, Object> errorNotification = new HashMap<>();
            errorNotification.put("type", "error");
            errorNotification.put("message", "内容更新通知处理失败：" + e.getMessage());
            errorNotification.put("timestamp", LocalDateTime.now());

            messagingTemplate.convertAndSendToUser(principal.getName(), "/content/errors", errorNotification);
        }
    }

    /**
     * 处理内容编辑协作（多用户同时编辑）
     */
    @MessageMapping("/content/collaborate")
    @SendToUser("/content/collaboration")
    public Map<String, Object> handleContentCollaboration(@Payload Map<String, Object> message, Principal principal) {
        try {
            Long contentId = Long.valueOf(message.get("contentId").toString());
            String operation = message.get("operation").toString(); // edit, view, leave
            Object data = message.get("data");

            log.info("收到内容协作请求，内容ID：{}，操作：{}，用户：{}",
                    contentId, operation, principal.getName());

            Map<String, Object> response = new HashMap<>();
            response.put("type", "collaboration");
            response.put("contentId", contentId);
            response.put("operation", operation);
            response.put("user", principal.getName());
            response.put("timestamp", LocalDateTime.now());
            response.put("data", data);

            // 通知其他协作者
            messagingTemplate.convertAndSend("/topic/content/" + contentId + "/collaboration", response);

            return response;

        } catch (Exception e) {
            log.error("处理内容协作请求失败：{}", e.getMessage(), e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("type", "error");
            errorResponse.put("message", "协作请求处理失败：" + e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());

            return errorResponse;
        }
    }

    /**
     * 处理内容发布通知
     */
    @MessageMapping("/content/publish")
    public void handleContentPublish(@Payload Map<String, Object> message, Principal principal) {
        try {
            Long contentId = Long.valueOf(message.get("contentId").toString());
            String notification = (String) message.get("notification");

            log.info("收到内容发布通知，内容ID：{}，用户：{}", contentId, principal.getName());

            // 获取内容信息
            RichContentResponse contentResponse = richContentService.getContentById(contentId);

            // 构建发布通知
            Map<String, Object> publishNotification = new HashMap<>();
            publishNotification.put("type", "content_publish");
            publishNotification.put("contentId", contentId);
            publishNotification.put("content", contentResponse);
            publishNotification.put("notification", notification);
            publishNotification.put("publishedBy", principal.getName());
            publishNotification.put("timestamp", LocalDateTime.now());

            // 向所有订阅发布通知的用户发送消息
            messagingTemplate.convertAndSend("/topic/content/published", publishNotification);

            // 向特定类型的订阅者发送消息
            if (contentResponse.getContentType() != null) {
                messagingTemplate.convertAndSend("/topic/content/" + contentResponse.getContentType() + "/published", publishNotification);
            }

        } catch (Exception e) {
            log.error("处理内容发布通知失败：{}", e.getMessage(), e);
        }
    }

    /**
     * 处理实时内容同步（草稿自动保存）
     */
    @MessageMapping("/content/sync")
    public void handleContentSync(@Payload Map<String, Object> message, Principal principal) {
        try {
            Long contentId = Long.valueOf(message.get("contentId").toString());
            String content = (String) message.get("content");
            Integer cursorPosition = (Integer) message.get("cursorPosition");

            log.debug("收到内容同步请求，内容ID：{}，用户：{}", contentId, principal.getName());

            // 构建同步消息
            Map<String, Object> syncMessage = new HashMap<>();
            syncMessage.put("type", "content_sync");
            syncMessage.put("contentId", contentId);
            syncMessage.put("content", content);
            syncMessage.put("cursorPosition", cursorPosition);
            syncMessage.put("syncedBy", principal.getName());
            syncMessage.put("timestamp", LocalDateTime.now());

            // 向其他编辑同一内容的用户发送同步消息
            messagingTemplate.convertAndSend("/topic/content/" + contentId + "/sync", syncMessage);

        } catch (Exception e) {
            log.error("处理内容同步请求失败：{}", e.getMessage(), e);
        }
    }

    /**
     * 广播系统消息
     */
    public void broadcastSystemMessage(String message, String type) {
        try {
            Map<String, Object> systemMessage = new HashMap<>();
            systemMessage.put("type", "system");
            systemMessage.put("messageType", type);
            systemMessage.put("message", message);
            systemMessage.put("timestamp", LocalDateTime.now());

            messagingTemplate.convertAndSend("/topic/system/notifications", systemMessage);
            log.info("系统消息广播成功，类型：{}，消息：{}", type, message);

        } catch (Exception e) {
            log.error("系统消息广播失败：{}", e.getMessage(), e);
        }
    }

    /**
     * 向特定用户发送消息
     */
    public void sendToUser(String username, String message, String type) {
        try {
            Map<String, Object> userMessage = new HashMap<>();
            userMessage.put("type", type);
            userMessage.put("message", message);
            userMessage.put("timestamp", LocalDateTime.now());

            messagingTemplate.convertAndSendToUser(username, "/notifications", userMessage);
            log.info("用户消息发送成功，用户：{}，类型：{}", username, type);

        } catch (Exception e) {
            log.error("用户消息发送失败，用户：{}，错误：{}", username, e.getMessage(), e);
        }
    }

    /**
     * 向内容订阅者发送消息
     */
    public void sendToContentSubscribers(Long contentId, Object message, String messageType) {
        try {
            Map<String, Object> contentMessage = new HashMap<>();
            contentMessage.put("type", messageType);
            contentMessage.put("contentId", contentId);
            contentMessage.put("data", message);
            contentMessage.put("timestamp", LocalDateTime.now());

            messagingTemplate.convertAndSend("/topic/content/" + contentId + "/notifications", contentMessage);
            log.info("内容订阅者消息发送成功，内容ID：{}，类型：{}", contentId, messageType);

        } catch (Exception e) {
            log.error("内容订阅者消息发送失败，内容ID：{}，错误：{}", contentId, e.getMessage(), e);
        }
    }
}