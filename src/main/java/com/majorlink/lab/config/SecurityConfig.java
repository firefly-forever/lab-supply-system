package com.majorlink.lab.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 安全配置
 * <p>
 * 仅注册 BCryptPasswordEncoder Bean，不启用完整的Spring Security过滤链。
 * 认证逻辑由自定义 JwtInterceptor 负责。
 * </p>
 */
@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
