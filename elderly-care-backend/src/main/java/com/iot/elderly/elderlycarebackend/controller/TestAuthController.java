package com.iot.elderly.elderlycarebackend.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.util.HashMap;

/**
 * 【废案】微信小程序测试登录控制器
 *
 * 这个控制器是为微信小程序开发阶段准备的测试接口，项目后来放弃了微信小程序，所以这个类已经不再使用。
 * 原本用途：接收微信 openid，返回模拟 token，方便前端开发时不需要真正调用微信 API 就能拿到 token。
 *
 * 接口：POST /api/auth/test-login
 * 注意：这个接口返回的 token 是假的（"test_token_" + openid），不是真正的 JWT，
 *       也不能被 JwtInterceptor 解析，所以用这个 token 访问其他接口会被 401 拒绝。
 */
@RestController
@RequestMapping("/api/auth")
public class TestAuthController {

    /**
     * 测试登录接口（接收微信openid，返回模拟token）
     * @param body 请求体（包含openid）
     * @return 响应结果（success、token、userId）
     */
    @PostMapping("/test-login")
    public ResponseEntity<Map<String, Object>> testLogin(@RequestBody Map<String, String> body) {
        // 1. 获取openid（微信登录返回的openid）
        String openid = body.get("openid");

        // 2. 验证openid是否为空
        if (openid == null || openid.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "openid is required"
            ));
        }

        // 3. 生成模拟token（实际项目中可替换为JWT或数据库生成的token）
        String token = "test_token_" + openid;

        // 4. 构造响应数据
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("token", token);
        response.put("userId", "test_user_" + openid.substring(0, 10)); // 简单生成userId（实际可关联数据库）

        // 5. 返回成功响应
        return ResponseEntity.ok(response);
    }
}
