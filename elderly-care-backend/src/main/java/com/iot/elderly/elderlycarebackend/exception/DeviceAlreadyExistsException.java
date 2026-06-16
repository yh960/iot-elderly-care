
package com.iot.elderly.elderlycarebackend.exception;

/**
 * 自定义异常：设备已存在
 *
 * 当尝试注册一个 deviceId 已经存在于数据库中的设备时抛出。
 * 由 GlobalExceptionHandler 捕获并返回 400 错误给前端。
 */
public class DeviceAlreadyExistsException extends RuntimeException {
    public DeviceAlreadyExistsException(String message) {
        super(message);
    }
}
