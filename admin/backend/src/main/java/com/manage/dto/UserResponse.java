package com.manage.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户响应DTO
 * 用于返回用户信息，不包含敏感信息
 *
 * @author System
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 账号状态
     */
    private Integer status;

    /**
     * 从User实体创建UserResponse
     *
     * @param user User实体
     * @return UserResponse DTO
     */
    public static UserResponse from(com.manage.entity.User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setCreateTime(user.getCreateTime());
        response.setUpdateTime(user.getUpdateTime());
        response.setStatus(user.getStatus());
        return response;
    }
}