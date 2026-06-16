package com.iot.elderly.elderlycarebackend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置 —— 注册拦截器、CORS 策略、RestTemplate
 *
 * 三个核心职责：
 * 1. CORS（跨域资源共享）：允许前端（不同域名/端口）访问后端 API
 *    - 允许所有来源（生产环境应改为具体域名）
 *    - 预检请求缓存 1 小时，减少 OPTIONS 请求次数
 *
 * 2. JWT 拦截器注册：将 JwtInterceptor 注册到 /api/** 路径
 *    - excludePathPatterns 列出的路径不需要 token（如登录、注册、健康检查）
 *    - 【注意】这里包含了微信相关的路径（/api/auth/wechat/login、/api/auth/test-login），
 *      这些是微信小程序登录的接口，虽然项目后来放弃了微信小程序，但路径排除配置保留了
 *
 * 3. RestTemplate Bean：用于调用外部 HTTP API（如调用智谱 AI 的 GLM-4 大模型接口）
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")  // 生产环境建议指定域名
                .allowedMethods("*")         // 支持所有HTTP方法
                .allowedHeaders("*")         // 支持所有header（包括Authorization）
                .allowCredentials(true)      // 允许携带cookie/token
                .maxAge(3600);              // 预检请求缓存1小时
    }



    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/hello",
                        // ========== 以下为【废案】微信小程序相关路径 ==========
                        // 项目原本计划接入微信小程序，后来放弃了，但这些排除路径仍保留
                        "/api/auth/wechat/test-token",   // 【废案】微信测试 token 接口
                        "/api/auth/wechat/login",        // 【废案】微信登录接口
                        "/api/auth/test-login",          // 【废案】微信 openid 测试登录
                        // ========== 废案结束 ==========
                        "/api/auth/login",
                        "/api/user/register",
                        "/api/user/list"
                );
    }
}
