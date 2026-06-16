package com.iot.elderly.elderlycarebackend.controller;

import com.iot.elderly.elderlycarebackend.dto.Result;
import com.iot.elderly.elderlycarebackend.entity.FallEvent;
import com.iot.elderly.elderlycarebackend.entity.RadarData;
import com.iot.elderly.elderlycarebackend.service.FallDetectionService;
import com.iot.elderly.elderlycarebackend.service.FallEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 跌倒检测控制器 —— 根据雷达数据判断是否发生跌倒
 *
 * 接口列表：
 * - POST /api/fall/events                          提交雷达数据进行跌倒检测
 *   流程：接收 RadarData -> FallDetectionService 用阈值算法判断（速度 > 3.0 m/s = 跌倒）
 *        -> 如果检测到跌倒，保存 FallEvent 到数据库
 *
 * - GET  /api/fall/events/device/{deviceId}/recent  查询某设备近期的跌倒事件
 *   参数：hours（查询最近多少小时内的事件）
 */
@RestController
@RequestMapping("/api/fall/events")
public class FallDetectionController {

    @Autowired
    private FallDetectionService fallDetectionService;

    @Autowired
    private FallEventService fallEventService;

    @GetMapping("/device/{deviceId}/recent")
    public Result<List<FallEvent>> getRecentEvents(
            @PathVariable String deviceId,
            @RequestParam int hours) {
        List<FallEvent> events = fallEventService.getRecentEvents(deviceId, hours);
        return Result.success(events);
    }

    @PostMapping
    public ResponseEntity<Result<FallEvent>> detectFall(@RequestBody RadarData radarData) {
        FallEvent fallEvent = fallDetectionService.detectFall(radarData);
        if (fallEvent != null) {
            fallEventService.save(fallEvent);
            return ResponseEntity.ok(Result.success("检测到跌倒事件", fallEvent));
        }
        return ResponseEntity.ok(Result.success("未检测到跌倒", null));
    }
}
