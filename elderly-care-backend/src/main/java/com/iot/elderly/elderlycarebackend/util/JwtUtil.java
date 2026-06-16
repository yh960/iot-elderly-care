package com.iot.elderly.elderlycarebackend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

/**
 * JWT 工具类 —— Token 的生成和解析
 *
 * JWT（JSON Web Token）是无状态认证的核心：
 * 用户登录后生成 token，后续请求携带 token，服务端解析 token 获取用户信息。
 *
 * 两种 generateToken 重载：
 * 1. generateToken(userId, openid, userType): 微信登录用（废案），token 中包含 openid 和 userType
 * 2. generateToken(userId, username): 用户名密码登录用，token 中包含 username
 *
 * Token 结构（三段式，用 . 分隔）：
 * - Header: 算法信息（HS256）
 * - Payload: 用户数据（userId 作为 subject，加上自定义 claim）
 * - Signature: 用密钥对前两段签名，防止篡改
 *
 * 配置项：
 * - JWT_SECRET: 签名密钥（硬编码，生产环境应改为配置文件）
 * - EXPIRATION_TIME: 过期时间 1 小时（3600 * 1000 毫秒）
 */
public class JwtUtil {
    private static final String JWT_SECRET = "this_is_a_32_byte_long_secret_key_for_jwt_hs256";
    private static final long EXPIRATION_TIME = 3600 * 1000;

    public static String generateToken(Long userId, String openid, String userType) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("openid", openid)
                .claim("userType", userType)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET.getBytes())
                .compact();
    }

    public static String generateToken(Long userId, String username) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("username", username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET.getBytes())
                .compact();
    }

    public static Claims parseToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return Jwts.parser()
                .setSigningKey(JWT_SECRET.getBytes())
                .parseClaimsJws(token)
                .getBody();
    }

    public static Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return Long.parseLong(claims.getSubject());
    }
}
