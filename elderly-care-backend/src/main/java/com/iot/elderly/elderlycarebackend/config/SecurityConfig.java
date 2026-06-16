package com.iot.elderly.elderlycarebackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

/**
 * Spring Security 安全配置
 *
 * 当前配置的核心策略：
 * - CSRF 禁用：因为是 REST API + JWT 架构，不需要 CSRF token（前后端分离场景下 CSRF 无意义）
 * - 无状态会话（STATELESS）：不使用 HttpSession，每次请求都通过 JWT 验证身份
 * - 所有路径放行（permitAll）：Security 层不做拦截，实际认证由 JwtInterceptor 负责
 *   这样做的好处是 JWT 拦截器的白名单配置更灵活，不被 Security 的 filter 链干扰
 *
 * 提供的 Bean：
 * - BCryptPasswordEncoder：密码加密器，用于用户注册时加密密码、登录时验证密码
 * - CorsFilter：跨域过滤器（与 WebConfig 中的 CORS 配置重复，但 Security 需要自己的 CORS 处理）
 *
 * 【设计选择】为什么同时用 Spring Security 和自定义 JWT 拦截器？
 * Spring Security 功能强大但配置复杂，本项目只需要简单的 token 校验，
 * 所以 Security 只用来做密码加密和 CORS，认证逻辑交给更轻量的 JwtInterceptor
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(Arrays.asList("*"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors()
            .and()
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers("/**").permitAll();

        return http.build();
    }
}