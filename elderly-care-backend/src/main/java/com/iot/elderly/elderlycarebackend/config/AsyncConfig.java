package com.iot.elderly.elderlycarebackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步线程池配置
 *
 * 作用：为告警处理等耗时操作提供独立的线程池，避免阻塞主线程（HTTP 请求线程）
 * 使用场景：AlertServiceImpl 中的 @Async 方法会使用这个线程池来异步处理告警
 *
 * 线程池参数说明：
 * - 核心线程 5 个：平时常驻的线程数量
 * - 最大线程 10 个：高峰时最多扩展到 10 个线程
 * - 队列容量 100：核心线程忙时，任务先进队列排队，队列满了才创建额外线程
 * - 空闲 60 秒回收：非核心线程空闲 60 秒后会被销毁
 * - CallerRunsPolicy 拒绝策略：如果线程池和队列都满了，由调用者线程（即 HTTP 请求线程）自己执行，
 *   虽然会阻塞请求，但保证告警不会被丢弃 —— 这是"兜底"策略
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    // 核心线程数
    private static final int CORE_POOL_SIZE = 5;
    // 最大线程数
    private static final int MAX_POOL_SIZE = 10;
    // 队列容量
    private static final int QUEUE_CAPACITY = 100;
    // 线程空闲时间
    private static final int KEEP_ALIVE_SECONDS = 60;

    @Bean(name = "alertTaskExecutor")
    public Executor alertTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CORE_POOL_SIZE);
        executor.setMaxPoolSize(MAX_POOL_SIZE);
        executor.setQueueCapacity(QUEUE_CAPACITY);
        executor.setKeepAliveSeconds(KEEP_ALIVE_SECONDS);
        executor.setThreadNamePrefix("Alert-Async-");
        // 拒绝策略：由调用线程处理（保证报警不丢失，对应简历里的兜底）
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
