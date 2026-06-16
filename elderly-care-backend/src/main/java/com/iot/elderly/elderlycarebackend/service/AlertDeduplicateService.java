package com.iot.elderly.elderlycarebackend.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 告警去重服务 —— 防止硬件抖动导致重复报警
 *
 * 问题背景：雷达传感器在检测到异常时可能在短时间内连续触发多次报警（硬件抖动），
 * 如果不去重，家属会收到大量重复的告警通知。
 *
 * 解决方案：使用 ConcurrentHashMap 实现一个简单的 10 秒防抖窗口。
 * - key = deviceId + alertType（如 "RADAR-001_FALL"）
 * - value = 上次触发的时间戳
 * - 同一个 key 在 10 秒内的重复触发会被判定为"重复"并丢弃
 *
 * 线程安全：ConcurrentHashMap 是线程安全的，可以在多线程环境下使用
 * （配合 AsyncConfig 中的异步线程池）
 */
@Service
public class AlertDeduplicateService {
    // key: deviceId_alertType, value: 上次触发的时间戳
    private final ConcurrentHashMap<String, Long> alertCache = new ConcurrentHashMap<>();

    // 防抖时间窗口：10秒（同一个设备10秒内相同的报警只处理一次）
    private static final long WINDOW_MS = 10 * 1000;

    /**
     * 判断是否是重复的抖动报警
     * @return true 表示重复（应丢弃），false 表示是新报警（应处理）
     */
    public boolean isDuplicate(String deviceId, String alertType) {
        String key = deviceId + "_" + alertType;
        long now = System.currentTimeMillis();

        Long lastTime = alertCache.get(key);
        if (lastTime != null && (now - lastTime) < WINDOW_MS) {
            return true; // 在时间窗口内，认为是硬件抖动误报
        }

        // 不在窗口内，更新时间戳
        alertCache.put(key, now);
        return false;
    }
}
