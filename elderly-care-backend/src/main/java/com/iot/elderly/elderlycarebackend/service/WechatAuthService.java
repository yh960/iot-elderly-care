package com.iot.elderly.elderlycarebackend.service;

import com.iot.elderly.elderlycarebackend.entity.UserType;

/**
 * 【废案】微信认证服务接口
 *
 * 这个接口是为微信小程序登录准备的，项目后来放弃了微信小程序，所以已经不再使用。
 *
 * 原本的设计：
 * - login(code, userType): 用微信临时 code 换取 openid，自动创建用户，返回 JWT token
 * - generateTestToken(): 生成一个测试用的 JWT token（比赛演示时用）
 *
 * 实现类：WechatAuthServiceImpl（其中的微信 API 调用是 mock 的）
 */
public interface WechatAuthService {
    String login(String code, UserType userType);
    String generateTestToken();
}
