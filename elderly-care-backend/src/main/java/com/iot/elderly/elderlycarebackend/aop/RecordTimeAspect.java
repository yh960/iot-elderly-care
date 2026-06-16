package com.iot.elderly.elderlycarebackend.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * AOP 性能监控切面 —— 记录 Controller 和 Service 方法的执行耗时
 *
 * 这是 AOP（面向切面编程）的典型应用：
 * 不需要在每个方法里手动写计时代码，通过切面统一拦截，自动记录耗时。
 *
 * @Aspect: 标记这是一个切面类
 * @Around: 环绕通知，在方法执行前后都能插入逻辑
 * 切点表达式：拦截 controller 包和 service 包下的所有公共方法
 *
 * 工作流程：
 * 1. 方法执行前记录开始时间
 * 2. 调用原始方法（pjp.proceed()）
 * 3. 方法执行后计算耗时并打印日志
 *
 * 日志示例：AlertServiceImpl.processAlert() 耗时: 1234ms
 */
@Slf4j
@Aspect
@Component
public class RecordTimeAspect {

    @Around("execution(* com.iot.elderly.elderlycarebackend.controller..*(..))"
            + " || execution(* com.iot.elderly.elderlycarebackend.service..*(..))")
    public Object recordTime(ProceedingJoinPoint pjp) throws Throwable {
        long begin = System.currentTimeMillis();

        Object result = pjp.proceed();//调用原始方法

        long end = System.currentTimeMillis();
        log.info("{} 耗时: {}ms", pjp.getSignature().toShortString(), end - begin);
        return result;
    }
}
