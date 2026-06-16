package com.iot.elderly.elderlycarebackend.service;

import com.iot.elderly.elderlycarebackend.entity.EdgeDevice;
import com.iot.elderly.elderlycarebackend.exception.DeviceAlreadyExistsException;
import java.util.List;

/**
 * 边缘设备服务接口 —— 定义设备管理的核心方法
 *
 * 实现类：EdgeDeviceServiceImpl
 *
 * 方法说明：
 * - registerDevice(device): 注册新设备（前端调用，会自动绑定用户）
 * - registerDevice(token, device): 带 JWT Token 注册设备（从 token 中解析用户 ID）
 * - updateDeviceStatus(deviceId, status): 更新设备状态（online/offline）
 * - getDevicesByUserId(userId): 获取某用户下的所有设备列表
 */
public interface EdgeDeviceService {
    EdgeDevice registerDevice(EdgeDevice device) throws DeviceAlreadyExistsException;
    EdgeDevice registerDevice(String token, EdgeDevice device) throws DeviceAlreadyExistsException; // 新增带 token 的方法
    EdgeDevice updateDeviceStatus(String deviceId, String status);
    List<EdgeDevice> getDevicesByUserId(Long userId);
}
