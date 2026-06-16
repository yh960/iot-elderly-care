package com.iot.elderly.elderlycarebackend.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * 告警日志实体 —— 对应数据库 alert_log 表
 *
 * 每当系统检测到异常（如跌倒），就会创建一条 AlertLog 记录。
 * 这是告警功能的核心数据表，前端的"告警中心"页面就是从这张表读数据。
 *
 * 字段说明：
 * - id: 自增主键
 * - deviceId: 触发报警的设备 ID（字符串形式，如 "RADAR-001"）
 * - userId: 关联的被监测老人 ID
 * - alertType: 报警类型枚举（FALL 跌倒 / SITTING 长时间静止）
 * - status: 报警状态枚举（PENDING 待处理 / RESOLVED 已处理）
 * - createTime: 报警发生时间（通过 @PrePersist 在保存前自动设置）
 * - aiAnalysisResult: AI 大模型对这条告警的分析结果文本
 */
@Entity
@Table(name = "alert_log")
public class AlertLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_id")
    private String deviceId; // 触发报警的设备硬件ID

    @Column(name = "user_id")
    private Long userId; // 关联的被监测老人ID

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type")
    private AlertType alertType; // 报警类型（跌倒/静止等）

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AlertStatus status; // 报警状态

    @Column(name = "create_time")
    private Date createTime; // 报警发生时间

    // 【关键修复】：AI分析结果字段
    @Column(name = "ai_analysis_result")
    private String aiAnalysisResult;

    // Getter 和 Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public AlertType getAlertType() { return alertType; }
    public void setAlertType(AlertType alertType) { this.alertType = alertType; }
    public AlertStatus getStatus() { return status; }
    public void setStatus(AlertStatus status) { this.status = status; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public String getAiAnalysisResult() { return aiAnalysisResult; }
    public void setAiAnalysisResult(String aiAnalysisResult) { this.aiAnalysisResult = aiAnalysisResult; }

    @PrePersist
    public void prePersist() {
        this.createTime = new Date();
        if (this.status == null) {
            this.status = AlertStatus.PENDING;
        }
    }
}
