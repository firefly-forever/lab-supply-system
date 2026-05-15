package com.majorlink.lab.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT工具类
 * <p>
 * 负责Token的生成、解析和校验。
 * Token中存储用户ID、用户名和角色，避免频繁查库。
 * </p>
 */
@Slf4j
@Component
public class JwtUtil {

    @Value("${lab.jwt.secret}")
    private String secret;

    @Value("${lab.jwt.expire}")
    private long expireSeconds;

    /** 从secret生成安全密钥 */
    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成JWT Token
     *
     * @param userId   用户ID
     * @param username 用户名
     * @param role     用户角色
     * @return 签名后的JWT字符串
     */
    public String generateToken(Long userId, String username, String role) {
        Date now = new Date();
        Date expireTime = new Date(now.getTime() + expireSeconds * 1000);

        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expireTime)
                .signWith(getKey())
                .compact();
    }

    /**
     * 解析Token，获取Claims（包含userId、username、role等）
     *
     * @param token JWT字符串
     * @return Claims对象，解析失败返回null
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("Token已过期: {}", e.getMessage());
            return null;
        } catch (JwtException e) {
            log.warn("Token无效: {}", e.getMessage());
            return null;
        }
    }

    /** 从Token中提取用户ID */
    public Long getUserId(String token) {
        Claims claims = parseToken(token);
        return claims != null ? claims.get("userId", Long.class) : null;
    }

    /** 从Token中提取角色 */
    public String getRole(String token) {
        Claims claims = parseToken(token);
        return claims != null ? claims.get("role", String.class) : null;
    }

    /** 校验Token是否有效（未过期且签名正确） */
    public boolean isValid(String token) {
        return parseToken(token) != null;
    }
}
