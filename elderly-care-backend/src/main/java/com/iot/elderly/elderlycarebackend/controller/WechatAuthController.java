package com.iot.elderly.elderlycarebackend.controller;

import com.iot.elderly.elderlycarebackend.dto.Result;
import com.iot.elderly.elderlycarebackend.dto.WechatLoginRequest;
import com.iot.elderly.elderlycarebackend.entity.UserType;
import com.iot.elderly.elderlycarebackend.service.WechatAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 【废案】微信小程序登录控制器
 *
 * 这个控制器是为微信小程序登录准备的，项目后来放弃了微信小程序，所以整个控制器已经不再使用。
 *
 * 原本的设计流程：
 * 1. 前端（微信小程序）调用 wx.login() 获取临时 code
 * 2. 前端将 code 发送到 POST /api/auth/wechat/login
 * 3. 后端用 code 调用微信 API 换取 openid（当前实现是 mock 的）
 * 4. 根据 openid 自动创建用户或查找已有用户
 * 5. 生成真正的 JWT Token 返回给前端
 *
 * 当前状态：WechatAuthServiceImpl 中的微信 API 调用是模拟的（mock），没有真正对接微信服务器
 */
@RestController
@RequestMapping("/api/auth/wechat")
public class WechatAuthController {
    @Autowired
    private WechatAuthService wechatAuthService;

    @PostMapping("/login")
    public Result<String> login(@RequestBody WechatLoginRequest request) {
        UserType userType = UserType.fromCode(request.getUserType());
        String token = wechatAuthService.login(request.getCode(), userType);
        return Result.success("登录成功", token);
    }

    /**
     * 测试接口：获取一个测试用的Token（比赛演示时可用）
     */
    @GetMapping("/test-token")
    public Result<String> getTestToken() {
        String token = wechatAuthService.generateTestToken();
        return Result.success(token);
    }
}
