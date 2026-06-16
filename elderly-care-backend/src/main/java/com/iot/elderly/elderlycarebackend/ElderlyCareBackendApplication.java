package com.iot.elderly.elderlycarebackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Spring Boot 主启动类 —— 整个后端服务的入口
 *
 * @SpringBootApplication: 组合注解，包含 @Configuration + @EnableAutoConfiguration + @ComponentScan
 * @EnableScheduling: 启用定时任务（项目中用于告警去重的过期清理等场景）
 * @EnableAsync: 启用异步方法调用（配合 AsyncConfig 中的线程池，用于异步处理告警）
 * @EntityScan: 显式指定 JPA 实体类所在的包路径，确保 Spring 能扫描到所有 @Entity
 */
@SpringBootApplication
@EnableScheduling
@EnableAsync
@EntityScan("com.iot.elderly.elderlycarebackend.entity")
public class ElderlyCareBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(ElderlyCareBackendApplication.class, args);
	}
}
