package com.iot.elderly.elderlycarebackend.entity;

/**
 * 用户类型枚举
 *
 * - ELDERLY: 被监测老人 —— 系统的核心服务对象，设备绑定在老人名下
 * - FAMILY: 家属 —— 接收报警通知的人（如子女）
 * - ADMIN: 管理员 —— 系统管理人员
 *
 * fromCode() 方法：将字符串转为枚举，用于微信登录时的 userType 字段转换
 */
public enum UserType {
    ELDERLY("elderly", "被监测老人"),
    FAMILY("family", "家属（接收报警）"),
    ADMIN("admin", "管理员");

    private final String code;
    private final String description;


    UserType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static UserType fromCode(String code) {
        for (UserType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知用户类型: " + code);
    }
}
