package com.iot.elderly.elderlycarebackend.service;

import com.iot.elderly.elderlycarebackend.entity.FallEvent;
import com.iot.elderly.elderlycarebackend.entity.RadarData;

import java.util.List;

/**
 * 跌倒检测服务接口 —— 定义跌倒检测的核心方法
 *
 * 实现类：FallDetectionServiceImpl
 *
 * 方法说明：
 * - detectFall(radarData): 根据雷达数据判断是否跌倒，返回 FallEvent 对象或 null
 * - getRecentEvents(deviceId, hours): 查询某设备最近 N 小时内的跌倒事件
 */
public interface FallDetectionService {
    /**
     * 根据雷达数据检测跌倒事件，并返回跌倒记录
     * @param radarData 雷达数据
     * @return 跌倒事件对象（需保存到数据库）
     */
    FallEvent detectFall(RadarData radarData); // 根据雷达数据检测跌倒
    List<FallEvent> getRecentEvents(String deviceId, int hours); // 获取近期事件
}
