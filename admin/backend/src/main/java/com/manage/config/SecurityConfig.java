package com.manage.config;

import com.manage.service.UserService;
import com.manage.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Spring Security配置类
 *
 * @author System
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    /**
     * 密码编码器（使用BCrypt加密）
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // 使用BCrypt强加密算法
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    }

    /**
     * 认证管理器
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * 认证提供者
     */
    @Bean
    public AuthenticationProvider authenticationProvider(UserService userService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * JWT认证过滤器
     */
    @Bean
    public com.manage.filter.JwtAuthenticationFilter jwtAuthenticationFilter(UserService userService) {
        return new com.manage.filter.JwtAuthenticationFilter(jwtUtil, userService);
    }

    /**
     * 安全过滤器链
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, UserService userService) throws Exception {
        http
                // 禁用CSRF
                .csrf().disable()
                // 配置CORS
                .cors().configurationSource(corsConfigurationSource())
                .and()
                // 配置会话管理为无状态
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // 配置认证提供者
                .authenticationProvider(authenticationProvider(userService))
                // 添加JWT过滤器
                .addFilterBefore(jwtAuthenticationFilter(userService), UsernamePasswordAuthenticationFilter.class)
                // 配置授权规则 - 注意顺序很重要，更具体的规则要在前面
                .authorizeRequests(authz -> authz
                        // 允许访问的端点
                        .antMatchers("/api/auth/**").permitAll()
                        .antMatchers("/api/public/**").permitAll()
                        // 公司信息接口 - 公开接口允许匿名访问，管理接口需要认证
                        .antMatchers("/api/company/info").permitAll()
                        .antMatchers("/api/company/public/**").permitAll()
                        .antMatchers("/api/company/admin/**").authenticated()
                        // 产品接口 - 公开查询接口允许匿名访问，管理接口需要认证
                        .antMatchers(HttpMethod.GET, "/api/products").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                        .antMatchers(HttpMethod.POST, "/api/products").authenticated()
                        .antMatchers(HttpMethod.PUT, "/api/products/**").authenticated()
                        .antMatchers(HttpMethod.DELETE, "/api/products/**").authenticated()
                        // 富内容接口 - 公开查询接口允许匿名访问，管理接口需要认证
                        .antMatchers(HttpMethod.GET, "/api/rich-content/public/**").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/rich-content/search").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/rich-content/tag/**").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/rich-content/author/**").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/rich-content/related").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/rich-content/statistics").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/rich-content/check-title/**").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/rich-content/popular").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/rich-content/featured").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/rich-content/top").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/rich-content/type/**").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/rich-content/date-range").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/rich-content/recent").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/rich-content/published").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/rich-content/status/**").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/rich-content/{id}").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/rich-content").permitAll()
                        // 浏览次数、点赞次数、评论次数等统计接口 - 公开访问
                        .antMatchers(HttpMethod.PUT, "/api/rich-content/*/view").permitAll()
                        .antMatchers(HttpMethod.PUT, "/api/rich-content/*/like").permitAll()
                        .antMatchers(HttpMethod.PUT, "/api/rich-content/*/comment").permitAll()
                        .antMatchers(HttpMethod.POST, "/api/rich-content").authenticated()
                        // 其他富文本内容管理接口需要认证（排除已公开的接口）
                        .antMatchers(HttpMethod.PUT, "/api/rich-content/batch/**").authenticated()
                        .antMatchers(HttpMethod.PUT, "/api/rich-content/*/top").authenticated()
                        .antMatchers(HttpMethod.PUT, "/api/rich-content/*/featured").authenticated()
                        .antMatchers(HttpMethod.PUT, "/api/rich-content/*/unpublish").authenticated()
                        .antMatchers(HttpMethod.DELETE, "/api/rich-content/**").authenticated()
                        .antMatchers(HttpMethod.POST, "/api/rich-content/publish").authenticated()
                        .antMatchers(HttpMethod.PUT, "/api/rich-content/batch/**").authenticated()
                        .antMatchers("/error").permitAll()
                        .antMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // 健康检查接口 - 允许公开访问
                        .antMatchers("/api/health").permitAll()
                        .antMatchers("/api/alive").permitAll()
                        // 仪表盘接口 - 需要认证
                        .antMatchers("/api/dashboard/**").authenticated()
                        // 文件访问接口 - 允许公开访问
                        .antMatchers("/api/files/**").permitAll()
                        // 管理上传接口 - 需要认证
                        .antMatchers("/api/admin/upload/**").authenticated()
                        // 测试上传接口 - 允许公开访问
                        .antMatchers("/api/test/upload/**").permitAll()
                        // 其他请求需要认证
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    /**
     * CORS配置
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}