package com.iot.elderly.elderlycarebackend.controller;

import com.iot.elderly.elderlycarebackend.dto.AlertRequest;
import com.iot.elderly.elderlycarebackend.dto.Result;
import com.iot.elderly.elderlycarebackend.entity.AlertLog;
import com.iot.elderly.elderlycarebackend.entity.AlertStatus;
import com.iot.elderly.elderlycarebackend.repository.AlertLogRepository;
import com.iot.elderly.elderlycarebackend.service.AlertService;
import com.iot.elderly.elderlycarebackend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * 告警管理控制器 —— 告警的上报、查询、处理
 *
 * 接口列表：
 * - POST   /api/alert/report        上报告警（设备端或前端触发，会调用 AI 分析）
 * - GET    /api/alert/history        分页查询告警历史（支持按用户过滤）
 * - PUT    /api/alert/resolve/{id}   处理告警（将状态从 PENDING 改为 RESOLVED）
 *
 * 数据流：设备上报 -> processAlert -> 调用 AI 分析 -> 保存告警日志 -> 前端可查询
 */
@RestController
@RequestMapping("/api/alert")
public class AlertController {

    @Autowired
    private AlertService alertService;

    @Autowired
    private AlertLogRepository alertLogRepository;


    @PostMapping("/report")
    public Result<String> reportAlert(@RequestBody AlertRequest request) {
        alertService.processAlert(request);
        return Result.success("报警请求已接收，正在调用AI分析");
    }

    @GetMapping("/history")
    public Result<Page<AlertLog>> getHistory(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<AlertLog> history = alertService.getAlertHistory(page, size);
        return Result.success(history);
    }

    /**
     * 处理告警：将PENDING改为RESOLVED
     */
    @PutMapping("/resolve/{id}")
    public Result<Void> resolveAlert(@PathVariable Long id) {
        AlertLog alertLog = alertLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("告警不存在: " + id));
        alertLog.setStatus(AlertStatus.RESOLVED);
        alertLogRepository.save(alertLog);
        return Result.success();
    }
}
