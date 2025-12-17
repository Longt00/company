package com.manage.service.impl;

import com.manage.common.ResultCode;
import com.manage.entity.User;
import com.manage.exception.BusinessException;
import com.manage.repository.UserRepository;
import com.manage.service.UserService;
import com.manage.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;

/**
 * 用户服务实现类
 *
 * @author System
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 用户注册
     *
     * @param user 用户信息
     * @return 注册成功的用户信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public User register(User user) {
        log.info("用户注册：{}", user.getUsername());

        // 检查用户名是否已存在
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXISTS);
        }

        // 检查邮箱是否已存在（如果邮箱不为空）
        if (user.getEmail() != null && !user.getEmail().trim().isEmpty()
                && userRepository.existsByEmail(user.getEmail())) {
            throw new BusinessException(ResultCode.EMAIL_ALREADY_EXISTS);
        }

        // 使用BCrypt加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 设置默认值
        user.setCreateTime(LocalDateTime.now());
        user.setStatus(1); // 默认启用

        // 保存用户
        User savedUser = userRepository.save(user);
        log.info("用户注册成功：{}", savedUser.getUsername());

        // 返回用户信息（不包含密码）
        return savedUser;
    }

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return JWT Token
     */
    @Override
    public String login(String username, String password) {
        log.info("用户登录：{}", username);

        // 查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ResultCode.USERNAME_OR_PASSWORD_ERROR));

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        // 验证密码（使用BCrypt比较）
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException(ResultCode.USERNAME_OR_PASSWORD_ERROR);
        }

        // 生成JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        log.info("用户登录成功：{}", username);

        return token;
    }

    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_FOUND));
    }

    /**
     * 根据ID查找用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_FOUND));
    }

    /**
     * 修改密码
     *
     * @param userId      用户ID
     * @param oldPassword 原密码
     * @param newPassword 新密码
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        log.info("修改密码，用户ID：{}", userId);

        // 查找用户
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_FOUND));

        // 验证原密码（使用BCrypt比较）
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(ResultCode.OLD_PASSWORD_ERROR);
        }

        // 更新密码（使用BCrypt加密）
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdateTime(LocalDateTime.now());

        userRepository.save(user);
        log.info("密码修改成功，用户ID：{}", userId);
    }

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return 存在返回true，不存在返回false
     */
    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * 检查邮箱是否存在
     *
     * @param email 邮箱
     * @return 存在返回true，不存在返回false
     */
    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Spring Security UserDetailsService实现
     * 用于加载用户详情进行认证
     *
     * @param username 用户名
     * @return UserDetails
     * @throws UsernameNotFoundException 用户不存在异常
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new UsernameNotFoundException("用户已被禁用: " + username);
        }

        // 构建UserDetails
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(user.getStatus() == 0)
                .build();
    }
}