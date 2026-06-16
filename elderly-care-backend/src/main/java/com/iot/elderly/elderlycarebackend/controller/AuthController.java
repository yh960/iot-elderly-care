package com.iot.elderly.elderlycarebackend.controller;

import com.iot.elderly.elderlycarebackend.dto.Result;
import com.iot.elderly.elderlycarebackend.entity.User;
import com.iot.elderly.elderlycarebackend.repository.UserRepository;
import com.iot.elderly.elderlycarebackend.util.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户认证控制器 —— 用户名密码登录
 *
 * 接口：POST /api/auth/login
 * 流程：
 * 1. 接收 username + password
 * 2. 从数据库查找用户，用 BCrypt 验证密码
 * 3. 验证通过后生成 JWT Token 返回给前端
 * 4. 前端后续请求在 Header 中携带 Authorization: Bearer <token>
 *
 * 这是当前项目实际使用的登录方式（用户名密码），区别于【废案】微信登录
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public Result<String> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        User user = userRepository.findByUsername(username);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return Result.error("用户名或密码错误");
        }

        String token = JwtUtil.generateToken(user.getId(), user.getUsername());
        return Result.success(token);
    }
}