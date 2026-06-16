package com.iot.elderly.elderlycarebackend.service.impl;

import com.iot.elderly.elderlycarebackend.entity.EdgeDevice;
import com.iot.elderly.elderlycarebackend.entity.User;
import com.iot.elderly.elderlycarebackend.repository.EdgeDeviceRepository;
import com.iot.elderly.elderlycarebackend.repository.UserRepository;
import com.iot.elderly.elderlycarebackend.service.EdgeDeviceService;
import com.iot.elderly.elderlycarebackend.exception.DeviceAlreadyExistsException;
import com.iot.elderly.elderlycarebackend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 边缘设备服务实现 —— 设备注册、状态管理的业务逻辑
 *
 * registerDevice 方法的核心流程：
 * 1. 确定设备绑定的用户（从前端传入，或默认绑定到第一个 elderly 用户）
 * 2. 检查该用户下是否已有同名设备（防止重复注册）
 * 3. 设置设备默认状态为 "online"，保存到数据库
 *
 * registerDevice(token, device) 是带 Token 的版本：
 * 从 JWT Token 中解析用户 ID，自动绑定设备到当前登录用户
 */
@Service
public class EdgeDeviceServiceImpl implements EdgeDeviceService {
    private final EdgeDeviceRepository edgeDeviceRepository;
    private final UserRepository userRepository;

    @Autowired
    public EdgeDeviceServiceImpl(EdgeDeviceRepository edgeDeviceRepository, UserRepository userRepository) {
        this.edgeDeviceRepository = edgeDeviceRepository;
        this.userRepository = userRepository;
    }

    @Override
    public EdgeDevice registerDevice(EdgeDevice device) {
        // 获取用户
        User user;
        if (device.getUser() != null && device.getUser().getId() != null) {
            user = userRepository.findById(device.getUser().getId())
                    .orElseThrow(() -> new RuntimeException("用户不存在: " + device.getUser().getId()));
        } else {
            // 绑定到第一个elderly类型用户
            user = userRepository.findAll().stream()
                    .filter(u -> "elderly".equals(u.getUserType()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("系统中无被监测老人，请先通过微信登录创建"));
        }
        
        // 检查同一用户下是否已存在相同设备ID
        List<EdgeDevice> userDevices = edgeDeviceRepository.findByUserId(user.getId());
        boolean deviceExists = userDevices.stream()
                .anyMatch(d -> d.getDeviceId().equals(device.getDeviceId()));
        
        if (deviceExists) {
            throw new DeviceAlreadyExistsException("设备已存在: " + device.getDeviceId());
        }
        
        device.setUser(user);
        device.setStatus("online");
        return edgeDeviceRepository.save(device);
    }

    @Override
    public EdgeDevice registerDevice(String token, EdgeDevice device) {
        Long userId = JwtUtil.getUserIdFromToken(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + userId));
        if (edgeDeviceRepository.findByDeviceId(device.getDeviceId()) != null) {
            throw new DeviceAlreadyExistsException("设备已存在: " + device.getDeviceId());
        }
        device.setUser(user);
        device.setStatus("online");
        return edgeDeviceRepository.save(device);
    }

    @Override
    public EdgeDevice updateDeviceStatus(String deviceId, String status) {
        EdgeDevice device = edgeDeviceRepository.findByDeviceId(deviceId);
        if (device != null) {
            device.setStatus(status);
            return edgeDeviceRepository.save(device);
        }
        return null;
    }

    @Override
    public List<EdgeDevice> getDevicesByUserId(Long userId) {
        return edgeDeviceRepository.findByUserId(userId);
    }
}
