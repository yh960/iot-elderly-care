package com.iot.elderly.elderlycarebackend.repository;

import com.iot.elderly.elderlycarebackend.entity.FallEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 跌倒事件仓库 —— 操作 fall_event 表的数据访问层
 *
 * 方法说明：
 * - findByEdgeDevice_DeviceIdAndEventTimeAfter: 查询某设备在指定时间之后的跌倒事件
 *   注意方法名中的 "EdgeDevice_DeviceId" 是跨表查询的写法：
 *   FallEvent -> EdgeDevice（关联关系）-> deviceId（字段），Spring Data JPA 自动解析为 JOIN 查询
 *   常用于"查看最近 N 小时内的跌倒事件"
 */
@Repository
public interface FallEventRepository extends JpaRepository<FallEvent, Long> {

    /**
     * 根据设备ID查询指定时间之后的跌倒事件
     */
    List<FallEvent> findByEdgeDevice_DeviceIdAndEventTimeAfter(String deviceId, Date time);
}
