package com.iot.elderly.elderlycarebackend.service.impl;

import com.iot.elderly.elderlycarebackend.entity.User;
import com.iot.elderly.elderlycarebackend.entity.UserType;
import com.iot.elderly.elderlycarebackend.repository.UserRepository;
import com.iot.elderly.elderlycarebackend.service.WechatAuthService;
import com.iot.elderly.elderlycarebackend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 【废案】微信认证服务实现
 *
 * 这个类是为微信小程序登录准备的，项目后来放弃了微信小程序，所以已经不再使用。
 *
 * 原本的 login 流程：
 * 1. 用 code 调用微信 API 换取 openid（当前是 mock 实现，直接拼接字符串）
 * 2. 根据 openid 查找用户，如果不存在则自动创建新用户
 * 3. 生成包含 userId + openid + userType 的 JWT Token
 *
 * generateTestToken: 生成一个测试用的 JWT Token（userId=1, openid=test_openid_123）
 * 比赛演示时可以直接用这个 token 访问其他接口，不需要真正调用微信登录
 *
 * getOpenidByCode: 当前是 mock 实现，真实接入时需要替换为微信 API 调用
 */
@Service
public class WechatAuthServiceImpl implements WechatAuthService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public String login(String code, UserType userType) {
        String openid = getOpenidByCode(code);
        Optional<User> userOptional = userRepository.findByOpenid(openid);
        User user = userOptional.orElseGet(() -> {
            User newUser = new User();
            newUser.setOpenid(openid);
            newUser.setUserType(userType.getCode());
            newUser.setName("微信用户");
            newUser.setFamilyPhone("00000000000");
            return userRepository.save(newUser);
        });
        return JwtUtil.generateToken(user.getId(), user.getOpenid(), user.getUserType());
    }

    @Override
    public String generateTestToken() {
        return JwtUtil.generateToken(1L, "test_openid_123", "family");
    }

    private String getOpenidByCode(String code) {
        // TODO: 真实接入时替换以下逻辑
        // 1. GET https://api.weixin.qq.com/sns/jscode2session?appid={APPID}&secret={SECRET}&js_code={code}&grant_type=authorization_code
        // 2. 解析返回JSON中的 openid 字段
        // 3. 需要在 application.yml 中配置 wechat.appid 和 wechat.secret
        return "test_openid_" + code;
    }
}
