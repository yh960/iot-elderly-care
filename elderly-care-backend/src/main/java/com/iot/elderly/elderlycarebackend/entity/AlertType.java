package com.iot.elderly.elderlycarebackend.entity;

/**
 * 告警类型枚举
 *
 * - FALL: 跌倒报警 —— 雷达检测到速度突变（> 3.0 m/s），判定为跌倒
 * - SITTING: 长时间静止 —— 雷达检测到长时间无运动，可能老人晕倒或无法行动
 *
 * fromCode() 方法：将字符串转为枚举，用于前端传入的 alertType 字符串转枚举
 */
public enum AlertType {
    FALL("FALL", "跌倒报警"),
    SITTING("sitting", "长时间静止");

    private final String code;
    private final String description;

    AlertType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }

    // 【关键修复】：补充 fromCode 方法，把字符串转成枚举
    public static AlertType fromCode(String code) {
        for (AlertType type : AlertType.values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的报警类型: " + code);
    }
}
