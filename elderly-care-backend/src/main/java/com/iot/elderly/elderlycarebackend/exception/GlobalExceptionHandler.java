package com.iot.elderly.elderlycarebackend.exception;

import com.iot.elderly.elderlycarebackend.dto.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器 —— 统一捕获并处理 Controller 层抛出的异常
 *
 * @RestControllerAdvice: 组合了 @ControllerAdvice + @ResponseBody，
 *   作用是拦截所有 Controller 的异常，返回统一的 JSON 格式错误信息
 *
 * 处理的异常类型（按优先级从高到低）：
 * 1. MalformedJwtException -> 401 token 格式错误
 * 2. ExpiredJwtException   -> 401 token 已过期
 * 3. DeviceAlreadyExistsException -> 400 设备已存在
 * 4. RuntimeException      -> 400 通用业务异常（如"用户不存在"）
 * 5. Exception             -> 500 兜底，未知异常
 *
 * 这样做的好处：Controller 不需要每个方法都 try-catch，抛出异常即可，
 * 全局处理器会统一转换为前端友好的 JSON 错误响应
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理JWT格式错误
     */
    @ExceptionHandler(io.jsonwebtoken.MalformedJwtException.class)
    public ResponseEntity<Result<Void>> handleMalformedJwt(io.jsonwebtoken.MalformedJwtException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Result.error(401, "token格式错误，请重新获取"));
    }

    /**
     * 处理JWT过期
     */
    @ExceptionHandler(io.jsonwebtoken.ExpiredJwtException.class)
    public ResponseEntity<Result<Void>> handleExpiredJwt(io.jsonwebtoken.ExpiredJwtException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Result.error(401, "token已过期，请重新获取"));
    }

    /**
     * 处理设备已存在异常
     */
    @ExceptionHandler(DeviceAlreadyExistsException.class)
    public ResponseEntity<Result<Void>> handleDeviceAlreadyExists(DeviceAlreadyExistsException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Result.error(400, e.getMessage()));
    }

    /**
     * 处理运行时异常（如"用户不存在"、"设备不存在"等）
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Result<Void>> handleRuntimeException(RuntimeException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Result.error(400, e.getMessage()));
    }

    /**
     * 处理所有未捕获的异常（兜底）
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception e) {
        e.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.error(500, "服务器内部错误：" + e.getMessage()));
    }
}
