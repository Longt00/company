package com.manage.filter;

import com.manage.common.ResultCode;
import com.manage.exception.BusinessException;
import com.manage.util.JwtUtil;
import com.manage.service.UserService;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT认证过滤器
 *
 * @author System
 * @version 1.0
 */
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
        try {
            // 从请求头中获取token
            String token = getTokenFromRequest(request);

            if (StringUtils.hasText(token)) {
                // 从token中获取用户名
                String username = jwtUtil.getUsernameFromToken(token);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // 加载用户详情
                    UserDetails userDetails = userService.loadUserByUsername(username);

                    // 验证token
                    if (jwtUtil.validateToken(token, username)) {
                        // 创建认证token
                        UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        // 设置到安全上下文
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.debug("用户 {} 认证成功", username);
                    } else {
                        log.warn("Token验证失败，用户：{}", username);
                        throw new BusinessException(ResultCode.TOKEN_INVALID);
                    }
                }
            }
        } catch (Exception e) {
            log.error("JWT认证失败：{}", e.getMessage());
            // 这里不抛出异常，让请求继续，后续的授权会失败
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 从请求中获取token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}