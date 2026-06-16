package com.iot.elderly.elderlycarebackend.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * 跌倒事件实体 —— 对应数据库 fall_event 表
 *
 * 当 FallDetectionService 通过雷达数据检测到跌倒时，会创建一条 FallEvent 记录。
 * 与 AlertLog 的区别：FallEvent 记录的是跌倒事件本身，AlertLog 记录的是告警处理流程（包含 AI 分析结果）。
 *
 * 字段说明：
 * - id: 自增主键
 * - edgeDevice: 关联的设备（多对一），通过 edge_device_id 外键关联
 * - eventTime: 跌倒发生时间
 * - type: 事件类型（如 "fall"）
 * - status: 事件状态
 * - description: 事件描述（可存储 AI 分析的摘要）
 */
@Entity
@Table(name = "fall_event")
public class FallEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "edge_device_id", nullable = false)
    private EdgeDevice edgeDevice;

    @Column(name = "event_time", nullable = false)
    private Date eventTime;

    @Column(length = 50)
    private String type;

    @Column(length = 20)
    private String status;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Getter 和 Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public EdgeDevice getEdgeDevice() { return edgeDevice; }
    public void setEdgeDevice(EdgeDevice edgeDevice) { this.edgeDevice = edgeDevice; }

    public Date getEventTime() { return eventTime; }
    public void setEventTime(Date eventTime) { this.eventTime = eventTime; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
