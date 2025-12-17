package com.manage.service;

import com.manage.dto.richcontent.RichContentResponse;

/**
 * 富文本内容WebSocket服务接口
 *
 * @author System
 * @version 1.0
 */
public interface RichContentWebSocketService {

    /**
     * 通知内容更新
     *
     * @param contentId    内容ID
     * @param updateType   更新类型
     * @param updatedBy    更新人
     * @param content      更新后的内容
     */
    void notifyContentUpdate(Long contentId, String updateType, String updatedBy, RichContentResponse content);

    /**
     * 通知内容发布
     *
     * @param contentId    内容ID
     * @param publishedBy  发布人
     * @param content      发布的内容
     * @param notification 发布通知
     */
    void notifyContentPublish(Long contentId, String publishedBy, RichContentResponse content, String notification);

    /**
     * 通知内容协作状态变更
     *
     * @param contentId 内容ID
     * @param user      用户
     * @param operation 操作类型
     * @param data      操作数据
     */
    void notifyCollaboration(Long contentId, String user, String operation, Object data);

    /**
     * 广播系统消息
     *
     * @param message 消息内容
     * @param type    消息类型
     */
    void broadcastSystemMessage(String message, String type);

    /**
     * 向特定用户发送消息
     *
     * @param username 用户名
     * @param message  消息内容
     * @param type     消息类型
     */
    void sendToUser(String username, String message, String type);

    /**
     * 通知内容订阅者
     *
     * @param contentId 内容ID
     * @param message   消息内容
     * @param type      消息类型
     */
    void notifyContentSubscribers(Long contentId, Object message, String type);
}