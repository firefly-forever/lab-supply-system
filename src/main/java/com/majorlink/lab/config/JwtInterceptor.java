package com.majorlink.lab.config;

import com.majorlink.lab.common.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT认证拦截器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        // ✅ 关键1：放行 CORS 预检请求（必须放最前面）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }

        String uri = request.getRequestURI();

        // ✅ 关键2：登录接口直接放行（避免没token无法登录）
        if (uri.contains("/auth/login") || uri.contains("/auth/register")) {
            return true;
        }

        // 获取Token
        String authHeader = request.getHeader("Authorization");

        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("缺少认证Token，请先登录");
        }

        String token = authHeader.substring(7);

        Claims claims;
        try {
            claims = jwtUtil.parseToken(token);
        } catch (Exception e) {
            throw new UnauthorizedException("Token已过期或无效，请重新登录");
        }

        if (claims == null) {
            throw new UnauthorizedException("Token解析失败");
        }

        // 存入上下文
        Long userId = claims.get("userId", Long.class);
        String username = claims.getSubject();
        String role = claims.get("role", String.class);

        UserContext.set(userId, username, role);

        log.debug("用户 [{}] ({}) 请求: {} {}",
                username, role, request.getMethod(), uri);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {

        // 清理ThreadLocal
        UserContext.clear();
    }
}