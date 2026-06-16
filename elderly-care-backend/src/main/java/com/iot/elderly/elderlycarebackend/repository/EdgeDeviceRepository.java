// EdgeDeviceRepository.java
package com.iot.elderly.elderlycarebackend.repository;

import com.iot.elderly.elderlycarebackend.entity.FallEvent;
import com.iot.elderly.elderlycarebackend.entity.RadarData;
import org.springframework.data.jpa.repository.JpaRepository;
import com.iot.elderly.elderlycarebackend.entity.EdgeDevice; // 仅保留必要的导入
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 边缘设备仓库 —— 操作 edge_device 表的数据访问层
 *
 * 方法说明：
 * - findByDeviceId: 根据设备硬件 ID 查询设备（业务层常用，deviceId 是设备的唯一标识）
 * - findByUserId: 根据用户 ID 查询该用户下的所有设备（一个老人可以有多个雷达）
 * - existsByDeviceId: 判断某个 deviceId 是否已存在（注册设备时做重复校验用）
 */
@Repository
public interface EdgeDeviceRepository extends JpaRepository<EdgeDevice, Long> {
    EdgeDevice findByDeviceId(String deviceId); // 根据设备ID查询
    List<EdgeDevice> findByUserId(Long userId); // 根据用户ID查询关联设备
    boolean existsByDeviceId(String deviceId);
}



