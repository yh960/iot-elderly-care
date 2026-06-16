package com.iot.elderly.elderlycarebackend.dto;

/**
 * 统一返回结果封装 —— 所有 Controller 接口的返回格式
 *
 * 这是整个项目通用的响应格式，前端拿到的 JSON 都长这样：
 * {
 *   "code": 200,       // 200=成功，500=失败，401=未认证
 *   "message": "操作成功",
 *   "data": { ... }    // 实际数据，可以是任意类型
 * }
 *
 * 使用方式：
 * - 成功：Result.success(data) 或 Result.success("提示信息", data)
 * - 失败：Result.error("错误信息")
 *
 * @param <T> data 字段的泛型类型
 */
public class Result<T> {

    private int code;       // 状态码：200成功，其他失败
    private String message;  // 提示信息
    private T data;          // 返回数据

    // 私有构造方法
    private Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // ========== 成功方法 ==========

    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    // ========== 失败方法 ==========

    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }

    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }

    // ========== Getter和Setter ==========

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}
