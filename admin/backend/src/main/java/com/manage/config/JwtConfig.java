package com.manage.config;

import com.manage.util.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JWT配置类
 *
 * @author System
 * @version 1.0
 */
@Configuration
public class JwtConfig {

    /**
     * JWT工具类
     */
    @Bean
    public JwtUtil jwtUtil() {
        JwtUtil jwtUtil = new JwtUtil();
        // 手动设置属性值
        jwtUtil.setSecret("mySecretKeyForJWTAuthenticationThatIsSecureEnoughForHMACSHA512AlgorithmAndMeetsThe256BitsRequirement");
        jwtUtil.setExpiration(86400000L);
        return jwtUtil;
    }
}