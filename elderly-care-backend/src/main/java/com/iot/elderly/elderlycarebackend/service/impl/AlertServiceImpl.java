package com.iot.elderly.elderlycarebackend.service.impl;

import com.iot.elderly.elderlycarebackend.dto.AlertRequest;
import com.iot.elderly.elderlycarebackend.entity.AlertLog;
import com.iot.elderly.elderlycarebackend.entity.AlertType;
import com.iot.elderly.elderlycarebackend.entity.EdgeDevice;
import com.iot.elderly.elderlycarebackend.entity.RadarData;
import com.iot.elderly.elderlycarebackend.entity.User;
import com.iot.elderly.elderlycarebackend.repository.AlertLogRepository;
import com.iot.elderly.elderlycarebackend.repository.EdgeDeviceRepository;
import com.iot.elderly.elderlycarebackend.repository.RadarDataRepository;
import com.iot.elderly.elderlycarebackend.repository.UserRepository;
import com.iot.elderly.elderlycarebackend.service.AiAnalysisService;
import com.iot.elderly.elderlycarebackend.service.AlertDeduplicateService;
import com.iot.elderly.elderlycarebackend.service.AlertService;
import com.iot.elderly.elderlycarebackend.service.WxPushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 告警服务实现 —— 告警处理的核心业务逻辑
 *
 * processAlert 方法是整个告警流程的编排者，步骤如下：
 * 1. 根据 deviceId 查询设备是否存在
 * 2. 获取该设备最新的雷达数据（如果没有则用模拟数据兜底）
 * 3. 调用 AiAnalysisService 分析雷达数据（调用智谱大模型）
 * 4. 将告警记录（含 AI 分析结果）保存到 alert_log 表
 *
 * 注入的依赖说明：
 * - EdgeDeviceRepository: 查询设备信息
 * - RadarDataRepository: 获取最新雷达数据
 * - AiAnalysisService: 调用大模型分析
 * - AlertLogRepository: 保存告警日志
 * - AlertDeduplicateService: 告警去重（当前 processAlert 中未直接调用，但已注入备用）
 * - WxPushService: 微信推送（当前为 TODO 桩实现）
 * - UserRepository: 查询用户信息
 */
@Service
public class AlertServiceImpl implements AlertService {
    @Autowired
    private EdgeDeviceRepository edgeDeviceRepository;
    @Autowired
    private AlertLogRepository alertLogRepository;
    @Autowired
    private RadarDataRepository radarDataRepository;
    @Autowired
    private WxPushService wxPushService;
    @Autowired
    private AiAnalysisService aiAnalysisService;
    @Autowired
    private AlertDeduplicateService alertDeduplicateService;
    @Autowired
    private UserRepository userRepository;

    @Override
    public void processAlert(AlertRequest request) {
        try {
            // 1. 查询设备
            EdgeDevice edgeDevice = edgeDeviceRepository.findByDeviceId(request.getDeviceId());
            if (edgeDevice == null) {
                throw new RuntimeException("上报报警失败：设备未注册 - " + request.getDeviceId());
            }

            // 2. 获取最新的雷达数据
            RadarData radarData = radarDataRepository.findFirstByEdgeDevice_DeviceIdOrderByTimestampDesc(request.getDeviceId());
            if (radarData == null) {
                System.out.println("警告：设备 " + request.getDeviceId() + " 没有雷达数据，使用模拟数据");
                radarData = new RadarData();
                radarData.setSpeed(4.5f);
                radarData.setTrajectoryX(12.0f);
                radarData.setTrajectoryY(5.0f);
            }

            // 3. 调用AI分析
            String aiResult = aiAnalysisService.analyzeRadarData(radarData);
            System.out.println("========== 【AI模型分析结果】 ==========");
            System.out.println("设备: " + request.getDeviceId() + ", AI判定: " + aiResult);
            System.out.println("=======================================");

            // 4. 记录日志入库
            AlertLog alertLog = new AlertLog();
            alertLog.setDeviceId(request.getDeviceId());
            alertLog.setUserId(edgeDevice.getUser().getId());
            alertLog.setAlertType(AlertType.fromCode(request.getAlertType()));
            alertLog.setAiAnalysisResult(aiResult);
            alertLogRepository.save(alertLog);
            System.out.println("告警已保存到数据库");

        } catch (Exception e) {
            System.err.println("处理报警失败: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public Page<AlertLog> getAlertHistory(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return alertLogRepository.findByUserIdOrderByCreateTimeDesc(userId, pageable);
    }

    @Override
    public Page<AlertLog> getAlertHistory(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return alertLogRepository.findAllByOrderByCreateTimeDesc(pageable);
    }
}
