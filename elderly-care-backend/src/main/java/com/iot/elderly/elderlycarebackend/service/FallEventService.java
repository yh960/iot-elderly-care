package com.iot.elderly.elderlycarebackend.service;

import com.iot.elderly.elderlycarebackend.entity.FallEvent;

import java.util.List;

/**
 * 跌倒事件服务接口 —— 定义跌倒事件的 CRUD 操作
 *
 * 实现类：FallEventServiceImpl
 *
 * 方法说明：
 * - deleteFallEvent(id): 删除指定 ID 的跌倒事件
 * - getRecentEvents(deviceId, hours): 查询某设备近期的跌倒事件
 * - save(fallEvent): 保存跌倒事件到数据库
 */
public interface FallEventService {
    /**
     * 删除指定ID的跌倒事件
     * @param id 跌倒事件ID
     */
    void deleteFallEvent(Long id);

    /**
     * 获取指定设备的近期跌倒事件（可选扩展）
     * @param deviceId 设备ID（硬件ID，如"radar_002"）
     * @param hours 小时数（如获取最近24小时的事件）
     * @return 跌倒事件列表
     */
    List<FallEvent> getRecentEvents(String deviceId, int hours);

    FallEvent save(FallEvent fallEvent);
}
