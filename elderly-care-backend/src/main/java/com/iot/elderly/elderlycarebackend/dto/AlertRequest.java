package com.iot.elderly.elderlycarebackend.dto;

/**
 * 告警请求 DTO（Data Transfer Object）
 *
 * 用于前端或设备端向 POST /api/alert/report 上报告警时的请求体
 * DTO 的作用：只接收外部传入的必要字段，不暴露完整的实体类（AlertLog）给外部
 *
 * 字段说明：
 * - deviceId: 触发报警的设备硬件 ID（如 "RADAR-001"）
 * - alertType: 报警类型字符串（如 "FALL" 跌倒、"sitting" 长时间静止），会在 Service 层转为 AlertType 枚举
 */
public class AlertRequest {
    private String deviceId;
    private String alertType;

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public String getAlertType() { return alertType; }
    public void setAlertType(String alertType) { this.alertType = alertType; }
}
