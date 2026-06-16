package com.iot.elderly.elderlycarebackend.controller;

import com.iot.elderly.elderlycarebackend.dto.Result;
import com.iot.elderly.elderlycarebackend.entity.User;
import com.iot.elderly.elderlycarebackend.repository.UserRepository;
import com.iot.elderly.elderlycarebackend.service.UserService;
import com.iot.elderly.elderlycarebackend.util.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制器 —— 用户的注册、查询、修改、删除
 *
 * 接口列表：
 * - POST   /api/user/register   用户注册（密码会用 BCrypt 加密后存库）
 * - GET    /api/user/info        获取当前登录用户信息（从 JWT 中解析 userId）
 * - PUT    /api/user/info        更新当前登录用户信息
 * - GET    /api/user/list        获取所有用户列表（管理用）
 * - PUT    /api/user/{id}        按 ID 更新用户信息
 * - DELETE /api/user/{id}        按 ID 删除用户
 *
 * 注册接口在 WebConfig 中被排除了 token 校验，其他接口都需要登录后才能访问
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserController(UserService userService, UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/info")
    public Result<User> getUserInfo(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = JwtUtil.getUserIdFromToken(token);
        User user = userService.getUserById(userId);
        return Result.success(user);
    }

    @PutMapping("/info")
    public Result<User> updateUserInfo(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody User updatedUser) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = JwtUtil.getUserIdFromToken(token);
        User user = userService.updateUser(userId, updatedUser);
        return Result.success(user);
    }

    @PostMapping("/register")
    public Result<User> register(@RequestBody User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return Result.error("用户名已存在");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        return Result.success(savedUser);
    }

    @GetMapping("/list")
    public Result<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return Result.success(users);
    }

    @PutMapping("/{id}")
    public Result<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        User user = userService.updateUser(id, updatedUser);
        return Result.success(user);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return Result.success();
    }
}
