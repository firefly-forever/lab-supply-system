package com.majorlink.lab.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 配置
 * - JWT拦截器
 * - CORS跨域配置（前后端分离）
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    /**
     * 不需要Token的路径
     */
    private static final String[] WHITE_LIST = {

            // 登录注册
            "/auth/login",
            "/auth/register",

            // ⭐ swagger（如果你以后用）
            "/swagger-ui/**",
            "/v3/api-docs/**",

            // ⭐ 前端静态资源（可选）
            "/index.html",
            "/static/**"
    };

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(WHITE_LIST);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/**")

                // ⭐ 关键1：允许所有前端地址（开发阶段）
                .allowedOriginPatterns("*")

                // ⭐ 关键2：必须包含 OPTIONS（预检请求）
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")

                // ⭐ 关键3：允许所有请求头（Authorization很重要）
                .allowedHeaders("*")

                // ⭐ 关键4：允许携带Token（必须）
                .allowCredentials(true)

                // ⭐ 关键5：暴露响应头（前端可读JWT相关信息）
                .exposedHeaders("*")

                .maxAge(3600);
    }
}