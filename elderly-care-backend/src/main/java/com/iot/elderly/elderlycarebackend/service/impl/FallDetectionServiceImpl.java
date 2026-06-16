package com.iot.elderly.elderlycarebackend.service.impl;

import com.iot.elderly.elderlycarebackend.entity.FallEvent;
import com.iot.elderly.elderlycarebackend.entity.RadarData;
import com.iot.elderly.elderlycarebackend.repository.FallEventRepository;
import com.iot.elderly.elderlycarebackend.service.FallDetectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 跌倒检测服务实现 —— 基于阈值算法的跌倒判定
 *
 * detectFallAlgorithm 方法是核心算法：
 * - 速度 > 3.0 m/s -> 判定为跌倒
 * - 这是一个简单的阈值算法，实际场景中可以用更复杂的算法（如机器学习模型）
 *
 * detectFall 方法流程：
 * 1. 调用算法判断是否跌倒
 * 2. 如果是跌倒，创建 FallEvent 对象（包含设备、时间、类型、状态、描述）
 * 3. 返回 FallEvent（由 Controller 决定是否保存到数据库）
 * 4. 如果不是跌倒，返回 null
 */
@Service
public class FallDetectionServiceImpl implements FallDetectionService {

    @Autowired
    private FallEventRepository fallEventRepository;

    @Override
    public FallEvent detectFall(RadarData radarData) {
        boolean isFall = detectFallAlgorithm(radarData);
        if (isFall) {
            FallEvent fallEvent = new FallEvent();
            fallEvent.setEdgeDevice(radarData.getEdgeDevice());
            fallEvent.setEventTime(new Date());
            fallEvent.setType("fall_detected");
            fallEvent.setStatus("pending");
            fallEvent.setDescription("雷达检测到跌倒事件");
            return fallEvent;
        }
        return null;
    }

    @Override
    public List<FallEvent> getRecentEvents(String deviceId, int hours) {
        long startTime = System.currentTimeMillis() - hours * 3600 * 1000L;
        return fallEventRepository.findByEdgeDevice_DeviceIdAndEventTimeAfter(deviceId, new Date(startTime));
    }

    private boolean detectFallAlgorithm(RadarData radarData) {
        return radarData.getSpeed() > 3.0f;
    }
}
