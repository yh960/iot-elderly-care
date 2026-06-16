package com.iot.elderly.elderlycarebackend.service;

import com.iot.elderly.elderlycarebackend.dto.AlertRequest;
import com.iot.elderly.elderlycarebackend.entity.AlertLog;
import org.springframework.data.domain.Page;

/**
 * 告警服务接口 —— 定义告警处理的核心方法
 *
 * 实现类：AlertServiceImpl
 *
 * 方法说明：
 * - processAlert: 处理一个告警请求（核心流程：查设备 -> 取雷达数据 -> AI 分析 -> 保存日志）
 * - getAlertHistory(userId, page, size): 按用户 ID 分页查询告警历史
 * - getAlertHistory(page, size): 查询所有告警历史（管理用）
 */
public interface AlertService {
    void processAlert(AlertRequest request);
    Page<AlertLog> getAlertHistory(Long userId, int page, int size);
    Page<AlertLog> getAlertHistory(int page, int size);
}
