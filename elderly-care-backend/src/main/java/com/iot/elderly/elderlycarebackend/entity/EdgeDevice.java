package com.iot.elderly.elderlycarebackend.entity;

import lombok.Data;
import javax.persistence.*;

/**
 * 边缘设备实体 —— 对应数据库 edge_device 表
 *
 * "边缘设备"是指安装在老人家中的人体雷达传感器，每个设备有唯一的 deviceId（硬件编号）。
 * 设备通过 IoT 协议将雷达数据上传到后端，后端再做跌倒检测和 AI 分析。
 *
 * 字段说明：
 * - id: 自增主键（数据库内部使用）
 * - deviceId: 设备硬件 ID（如 "RADAR-001"），业务层面的唯一标识
 * - location: 设备安装位置（如 "客厅"、"卧室"）
 * - status: 设备状态（"online" 在线 / "offline" 离线）
 * - user: 关联的用户（老人），多对一关系 —— 一个老人可以有多个设备
 *
 * 使用了 Lombok 的 @Data 注解，自动生成 getter/setter/toString/equals/hashCode
 */
@Data
@Entity
@Table(name = "edge_device", uniqueConstraints = @UniqueConstraint(columnNames = "device_id"))
public class EdgeDevice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_id", unique = true, length = 50)
    private String deviceId;

    @Column(name = "location", length = 100)
    private String location;

    @Column(name = "status", length = 20)
    private String status;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
