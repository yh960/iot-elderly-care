package com.iot.elderly.elderlycarebackend.controller;

import com.iot.elderly.elderlycarebackend.dto.Result;
import com.iot.elderly.elderlycarebackend.entity.EdgeDevice;
import com.iot.elderly.elderlycarebackend.entity.RadarData;
import com.iot.elderly.elderlycarebackend.repository.EdgeDeviceRepository;
import com.iot.elderly.elderlycarebackend.service.RadarDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * 雷达数据接收控制器 —— 接收 IoT 设备上传的雷达传感器数据
 *
 * 接口：POST /api/radar/data
 * 流程：
 * 1. 接收雷达设备上传的数据（速度、轨迹坐标等）
 * 2. 验证设备 ID 是否存在（防止数据挂到不存在的设备上）
 * 3. 如果没有时间戳则自动补上当前时间
 * 4. 保存到数据库的 radar_data 表
 *
 * 这是整个数据链路的入口：雷达设备 -> 这个接口 -> 数据库 -> 跌倒检测/AI 分析
 */
@RestController
@RequestMapping("/api/radar/data")
public class RadarDataController {
    @Autowired
    private RadarDataService radarDataService;
    @Autowired
    private EdgeDeviceRepository edgeDeviceRepository;

    @PostMapping
    public Result<RadarData> receiveData(@RequestBody RadarData radarData) {
        if (radarData.getTimestamp() == null) {
            radarData.setTimestamp(new Date());
        }
        if (radarData.getEdgeDevice() == null || radarData.getEdgeDevice().getDeviceId() == null) {
            return Result.error("设备ID不能为空");
        }
        EdgeDevice device = edgeDeviceRepository.findByDeviceId(radarData.getEdgeDevice().getDeviceId());
        if (device == null) {
            return Result.error("设备不存在: " + radarData.getEdgeDevice().getDeviceId());
        }
        radarData.setEdgeDevice(device);
        RadarData saved = radarDataService.saveRadarData(radarData);
        return Result.success(saved);
    }
}
