package com.iot.elderly.elderlycarebackend.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * 雷达数据实体 —— 对应数据库 radar_data 表
 *
 * 这是整个系统最底层的数据来源。IoT 雷达传感器每隔一段时间上传一条数据，
 * 包含目标的速度和轨迹坐标，后端根据这些数据做跌倒检测和 AI 分析。
 *
 * 字段说明：
 * - id: 自增主键
 * - edgeDevice: 关联的设备（多对一），通过 device_id 外键关联
 * - timestamp: 数据采集时间
 * - rawData: 原始雷达数据（JSON 字符串，TEXT 类型，可能很长）
 * - processedData: 处理后的数据（可选）
 * - speed: 目标运动速度（m/s），这是跌倒检测的关键指标 —— 速度 > 3.0 判定为跌倒
 * - trajectoryX: 轨迹 X 坐标
 * - trajectoryY: 轨迹 Y 坐标
 */
@Entity
@Table(name = "radar_data")
public class RadarData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false)
    private EdgeDevice edgeDevice;

    @Column(name = "timestamp", nullable = false)
    private Date timestamp;

    @Column(name = "raw_data", nullable = false, columnDefinition = "TEXT")
    private String rawData;

    @Column(name = "processed_data", length = 255)
    private String processedData;

    @Column(nullable = false)
    private float speed;

    @Column(name = "trajectoryx", nullable = false)
    private float trajectoryX;

    @Column(name = "trajectoryy", nullable = false)
    private float trajectoryY;

    // Getter 和 Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public EdgeDevice getEdgeDevice() { return edgeDevice; }
    public void setEdgeDevice(EdgeDevice edgeDevice) { this.edgeDevice = edgeDevice; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public String getRawData() { return rawData; }
    public void setRawData(String rawData) { this.rawData = rawData; }

    public String getProcessedData() { return processedData; }
    public void setProcessedData(String processedData) { this.processedData = processedData; }

    public float getSpeed() { return speed; }
    public void setSpeed(float speed) { this.speed = speed; }

    public float getTrajectoryX() { return trajectoryX; }
    public void setTrajectoryX(float trajectoryX) { this.trajectoryX = trajectoryX; }

    public float getTrajectoryY() { return trajectoryY; }
    public void setTrajectoryY(float trajectoryY) { this.trajectoryY = trajectoryY; }
}
