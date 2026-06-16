package com.iot.elderly.elderlycarebackend.config;

import com.iot.elderly.elderlycarebackend.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * JWT 认证拦截器 —— 所有 /api/** 接口的"门卫"
 *
 * 工作流程：
 * 1. 请求进入时，preHandle 方法先检查是否在白名单中（如 /api/hello），是则直接放行
 * 2. 不在白名单的请求，必须携带 Authorization: Bearer <token> 请求头
 * 3. 解析 token，将 userId 存入 request attribute，后续 Controller 可通过 request.getAttribute("userId") 获取当前用户
 * 4. token 过期或无效则返回 401 错误
 *
 * 与 WebConfig 配合：WebConfig 中注册了这个拦截器，并通过 excludePathPatterns 指定哪些路径不需要 token
 * 这里额外硬编码了两个白名单作为双重保险
 *
 * 注意：这个拦截器和 Spring Security 的 filterChain 是两套独立的认证机制
 * 当前 SecurityConfig 把所有路径都 permitAll()，实际认证完全由这个 JWT 拦截器负责
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String method = request.getMethod();
        String path = request.getRequestURI();

        // 白名单（仅保留绝对不需要token的接口）
        if ("GET".equals(method) && "/api/hello".equals(path)) return true;
        if ("GET".equals(method) && "/api/auth/wechat/test-token".equals(path)) return true;

        // 其他所有接口都需要token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendError(response, 401, "未登录，请先获取token");
            return false;
        }

        try {
            String token = authHeader.substring(7);
            Claims claims = JwtUtil.parseToken(token);
            // 将当前登录用户的 ID 存入 request，后续 Controller 可通过 request.getAttribute("userId") 获取
            request.setAttribute("userId", Long.parseLong(claims.getSubject()));
            return true;
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            sendError(response, 401, "token已过期，请重新登录");
            return false;
        } catch (Exception e) {
            sendError(response, 401, "token无效");
            return false;
        }
    }

    private void sendError(HttpServletResponse response, int code, String message) throws Exception {
        response.setStatus(code);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":" + code + ",\"message\":\"" + message + "\"}");
    }
}
