package com.manage.controller;

import com.manage.common.Result;
import com.manage.common.ResultCode;
import com.manage.dto.UserResponse;
import com.manage.entity.User;
import com.manage.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 *
 * @author System
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final UserService userService;

    /**
     * 用户注册
     *
     * @param registerRequest 注册请求
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result<UserResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("用户注册请求：{}", registerRequest.getUsername());

        // 检查用户名是否已存在
        if (userService.existsByUsername(registerRequest.getUsername())) {
            return Result.error(ResultCode.USER_ALREADY_EXISTS);
        }

        // 检查邮箱是否已存在（如果提供了邮箱）
        if (registerRequest.getEmail() != null && !registerRequest.getEmail().trim().isEmpty()
                && userService.existsByEmail(registerRequest.getEmail())) {
            return Result.error(ResultCode.EMAIL_ALREADY_EXISTS);
        }

        // 创建用户
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(registerRequest.getPassword());
        user.setEmail(registerRequest.getEmail());

        try {
            User registeredUser = userService.register(user);
            UserResponse userResponse = UserResponse.from(registeredUser);
            return Result.success("注册成功", userResponse);
        } catch (Exception e) {
            log.error("用户注册失败：{}", e.getMessage(), e);
            return Result.error("注册失败：" + e.getMessage());
        }
    }

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求
     * @return 登录结果
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("用户登录请求：{}", loginRequest.getUsername());

        try {
            String token = userService.login(loginRequest.getUsername(), loginRequest.getPassword());

            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("tokenType", "Bearer");
            result.put("username", loginRequest.getUsername());

            return Result.success("登录成功", result);
        } catch (Exception e) {
            log.error("用户登录失败：{}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/user-info")
    public Result<UserResponse> getCurrentUserInfo() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            User user = userService.findByUsername(username);
            UserResponse userResponse = UserResponse.from(user);
            return Result.success(userResponse);
        } catch (Exception e) {
            log.error("获取用户信息失败：{}", e.getMessage());
            return Result.error("获取用户信息失败：" + e.getMessage());
        }
    }

    /**
     * 修改密码
     *
     * @param changePasswordRequest 修改密码请求
     * @return 修改结果
     */
    @PostMapping("/change-password")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            User user = userService.findByUsername(username);
            userService.changePassword(user.getId(), changePasswordRequest.getOldPassword(),
                    changePasswordRequest.getNewPassword());

            return Result.success("密码修改成功");
        } catch (Exception e) {
            log.error("修改密码失败：{}", e.getMessage());
            return Result.error("修改密码失败：" + e.getMessage());
        }
    }

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return 检查结果
     */
    @GetMapping("/check-username")
    public Result<Map<String, Boolean>> checkUsername(@RequestParam String username) {
        boolean exists = userService.existsByUsername(username);
        Map<String, Boolean> result = new HashMap<>();
        result.put("exists", exists);
        return Result.success(result);
    }

    /**
     * 检查邮箱是否存在
     *
     * @param email 邮箱
     * @return 检查结果
     */
    @GetMapping("/check-email")
    public Result<Map<String, Boolean>> checkEmail(@RequestParam String email) {
        boolean exists = userService.existsByEmail(email);
        Map<String, Boolean> result = new HashMap<>();
        result.put("exists", exists);
        return Result.success(result);
    }

    /**
     * 注册请求DTO
     */
    public static class RegisterRequest {
        @NotBlank(message = "用户名不能为空")
        @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
        private String username;

        @NotBlank(message = "密码不能为空")
        @Size(min = 6, max = 100, message = "密码长度必须在6-100个字符之间")
        private String password;

        private String email;

        // Getters and Setters
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    /**
     * 登录请求DTO
     */
    public static class LoginRequest {
        @NotBlank(message = "用户名不能为空")
        private String username;

        @NotBlank(message = "密码不能为空")
        private String password;

        // Getters and Setters
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    /**
     * 修改密码请求DTO
     */
    public static class ChangePasswordRequest {
        @NotBlank(message = "原密码不能为空")
        private String oldPassword;

        @NotBlank(message = "新密码不能为空")
        @Size(min = 6, max = 100, message = "新密码长度必须在6-100个字符之间")
        private String newPassword;

        // Getters and Setters
        public String getOldPassword() {
            return oldPassword;
        }

        public void setOldPassword(String oldPassword) {
            this.oldPassword = oldPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }
}