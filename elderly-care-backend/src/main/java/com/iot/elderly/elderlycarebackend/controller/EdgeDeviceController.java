package com.iot.elderly.elderlycarebackend.controller;

import com.iot.elderly.elderlycarebackend.dto.Result;
import com.iot.elderly.elderlycarebackend.entity.EdgeDevice;
import com.iot.elderly.elderlycarebackend.entity.User;
import com.iot.elderly.elderlycarebackend.repository.EdgeDeviceRepository;
import com.iot.elderly.elderlycarebackend.repository.UserRepository;
import com.iot.elderly.elderlycarebackend.service.EdgeDeviceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 边缘设备管理控制器 —— 管理 IoT 雷达设备
 *
 * "边缘设备"指的是安装在老人家中的人体雷达传感器，用于采集运动数据。
 *
 * 接口列表：
 * - POST   /api/edge/devices                      注册新设备
 * - PUT    /api/edge/devices/{deviceId}            更新设备信息（位置、绑定用户）
 * - PUT    /api/edge/devices/{deviceId}/status     更新设备状态（online/offline）
 * - GET    /api/edge/devices/user/{userId}         获取某用户的所有设备
 * - POST   /api/edge/devices/register-with-token   带 Token 注册设备（备用接口）
 * - DELETE /api/edge/devices/{deviceId}            删除设备
 *
 * 设备与用户的关系：一个用户（老人）可以有多个设备（不同房间的雷达）
 */
@RestController
@RequestMapping("/api/edge/devices")
public class EdgeDeviceController {

    private final EdgeDeviceService edgeDeviceService;
    private final EdgeDeviceRepository edgeDeviceRepository;
    private final UserRepository userRepository;

    public EdgeDeviceController(EdgeDeviceService edgeDeviceService, EdgeDeviceRepository edgeDeviceRepository, UserRepository userRepository) {
        this.edgeDeviceService = edgeDeviceService;
        this.edgeDeviceRepository = edgeDeviceRepository;
        this.userRepository = userRepository;
    }

    // 添加设备（前端调用路径：/edge/devices）
    @PostMapping
    public Result<EdgeDevice> registerDevice(@RequestBody EdgeDevice device) {
        return Result.success(edgeDeviceService.registerDevice(device));
    }

    // 更新设备
    @PutMapping("/{deviceId}")
    public Result<EdgeDevice> updateDevice(
            @PathVariable String deviceId,
            @RequestBody EdgeDevice device) {
        EdgeDevice existing = edgeDeviceRepository.findByDeviceId(deviceId);
        if (existing == null) {
            return Result.error("设备不存在");
        }
        existing.setLocation(device.getLocation());
        if (device.getUser() != null && device.getUser().getId() != null) {
            User user = userRepository.findById(device.getUser().getId())
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
            existing.setUser(user);
        }
        EdgeDevice saved = edgeDeviceRepository.save(existing);
        return Result.success(saved);
    }

    // 更新设备状态（使用String类型的deviceId，与Service方法匹配）
    @PutMapping("/{deviceId}/status")
    public Result<EdgeDevice> updateStatus(
            @PathVariable String deviceId,
            @RequestBody String status) {
        EdgeDevice device = edgeDeviceService.updateDeviceStatus(deviceId, status);
        if (device == null) {
            return Result.error("设备不存在: " + deviceId);
        }
        return Result.success(device);
    }

    // 获取用户设备列表（前端调用路径：/edge/devices/user/{userId}）
    @GetMapping("/user/{userId}")
    public Result<List<EdgeDevice>> getDevicesByUserId(@PathVariable Long userId) {
        List<EdgeDevice> devices = edgeDeviceService.getDevicesByUserId(userId);
        return Result.success(devices);
    }

    // 带Token注册设备（备用接口）
    @PostMapping("/register-with-token")
    public Result<EdgeDevice> registerDeviceWithToken(
            @RequestHeader("Authorization") String token,
            @RequestBody EdgeDevice device) {
        try {
            EdgeDevice savedDevice = edgeDeviceService.registerDevice(token, device);
            return Result.success(savedDevice);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // 删除设备
    @DeleteMapping("/{deviceId}")
    public Result<Void> deleteDevice(@PathVariable String deviceId) {
        EdgeDevice device = edgeDeviceRepository.findByDeviceId(deviceId);
        if (device == null) {
            return Result.error("设备不存在");
        }
        edgeDeviceRepository.delete(device);
        return Result.success();
    }
}
