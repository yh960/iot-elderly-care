package com.iot.elderly.elderlycarebackend.service.impl;

import com.iot.elderly.elderlycarebackend.entity.FallEvent;
import com.iot.elderly.elderlycarebackend.repository.FallEventRepository;
import com.iot.elderly.elderlycarebackend.service.FallEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 跌倒事件服务实现 —— 跌倒事件的 CRUD 操作
 *
 * getRecentEvents 方法的实现逻辑：
 * 用当前时间减去 hours 小时，得到起始时间戳，然后查询该时间之后的所有事件。
 * 例如 hours=24，就是查询最近 24 小时内的跌倒事件。
 */
@Service
public class FallEventServiceImpl implements FallEventService {

    @Autowired
    private FallEventRepository fallEventRepository;

    @Override
    public void deleteFallEvent(Long id) {
        fallEventRepository.deleteById(id);
    }

    @Override
    public List<FallEvent> getRecentEvents(String deviceId, int hours) {
        long startTime = System.currentTimeMillis() - hours * 3600 * 1000;
        return fallEventRepository.findByEdgeDevice_DeviceIdAndEventTimeAfter(deviceId, new Date(startTime));
    }

    @Override
    public FallEvent save(FallEvent fallEvent) {
        return fallEventRepository.save(fallEvent);
    }
}
