package com.manage.service;

import com.manage.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 用户服务接口
 *
 * @author System
 * @version 1.0
 */
public interface UserService extends UserDetailsService {

    /**
     * 用户注册
     *
     * @param user 用户信息
     * @return 注册成功的用户信息
     */
    User register(User user);

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return JWT Token
     */
    String login(String username, String password);

    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    User findByUsername(String username);

    /**
     * 根据ID查找用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    User findById(Long id);

    /**
     * 修改密码
     *
     * @param userId      用户ID
     * @param oldPassword 原密码
     * @param newPassword 新密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return 存在返回true，不存在返回false
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     *
     * @param email 邮箱
     * @return 存在返回true，不存在返回false
     */
    boolean existsByEmail(String email);
}