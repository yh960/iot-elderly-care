package com.iot.elderly.elderlycarebackend.entity;

/**
 * 告警状态枚举
 *
 * - PENDING: 待处理 —— 刚产生的报警，等待家属或管理员确认
 * - RESOLVED: 已处理 —— 家属已查看并确认安全（通过 PUT /api/alert/resolve/{id} 更新）
 *
 * 数据库中以字符串形式存储（@Enumerated(EnumType.STRING)），如 "PENDING"、"RESOLVED"
 */
public enum AlertStatus {
    PENDING("pending", "待处理"),   // 刚报警，家属还没看
    RESOLVED("resolved", "已处理"); // 家属已确认安全

    private final String code;
    private final String description;

    AlertStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }
}
