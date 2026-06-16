package com.iot.elderly.elderlycarebackend.controller;

import com.iot.elderly.elderlycarebackend.dto.Result;
import com.iot.elderly.elderlycarebackend.entity.RadarData;
import com.iot.elderly.elderlycarebackend.service.AiAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * AI 分析控制器 —— 调用大模型分析雷达数据
 *
 * 接口：POST /api/ai/test
 * 功能：接收一条雷达数据，调用智谱 GLM-4 大模型 API 分析跌倒风险
 * 使用场景：前端 AI 测试页面，手动输入雷达数据后查看 AI 分析结果
 */
@RestController
@RequestMapping("/api/ai")
public class AiController {
    @Autowired
    private AiAnalysisService aiAnalysisService;

    /**
     * 测试真实AI分析接口
     */
    @PostMapping("/test")
    public Result testAiAnalysis(@RequestBody RadarData radarData) {
        try {
            String result = aiAnalysisService.analyzeRadarData(radarData);
            return Result.success("AI分析结果: " + result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("AI分析失败: " + e.getMessage());
        }
    }
}
