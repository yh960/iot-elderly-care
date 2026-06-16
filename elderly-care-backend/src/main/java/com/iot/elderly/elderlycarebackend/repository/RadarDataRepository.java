package com.iot.elderly.elderlycarebackend.repository;

import com.iot.elderly.elderlycarebackend.entity.RadarData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 雷达数据仓库 —— 操作 radar_data 表的数据访问层
 *
 * 方法说明：
 * - findFirstByEdgeDevice_DeviceIdOrderByTimestampDesc: 查询某设备最新的一条雷达数据
 *   "findFirst" 只取第一条，"OrderByTimestampDesc" 按时间倒序（最新在前）
 *   用于告警处理时获取设备当前的运动状态，供 AI 分析使用
 */
@Repository
public interface RadarDataRepository extends JpaRepository<RadarData, Long> {

    /**
     * 根据设备ID查询最新的雷达数据
     */
    RadarData findFirstByEdgeDevice_DeviceIdOrderByTimestampDesc(String deviceId);
}
