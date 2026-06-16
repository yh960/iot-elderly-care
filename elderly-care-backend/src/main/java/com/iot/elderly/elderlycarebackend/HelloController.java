package com.iot.elderly.elderlycarebackend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查控制器
 *
 * 最简单的接口，用于验证后端服务是否正常运行。
 * 访问 GET /api/hello 如果返回 "Hello, Spring Boot!" 说明服务启动成功。
 * 这个接口在 JwtInterceptor 的白名单中，不需要 token 即可访问。
 */
@RestController
public class HelloController {
    @GetMapping("/api/hello")
    public String hello() {
        return "Hello, Spring Boot!";
    }
}
