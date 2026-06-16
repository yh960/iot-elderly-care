package com.iot.elderly.elderlycarebackend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.elderly.elderlycarebackend.entity.RadarData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * AI 分析服务 —— 调用智谱 GLM-4 大模型分析雷达数据
 *
 * 这是项目中"AI + Java"结合的核心服务。它不是一个接口+实现的模式，
 * 而直接是一个 @Service 类，因为不需要多种实现。
 *
 * 工作流程：
 * 1. 接收一条 RadarData（速度、轨迹坐标）
 * 2. 构造提示词（prompt），告诉大模型判断标准（速度>3.0=高风险等）
 * 3. 调用智谱 GLM-4-Flash API，发送 HTTP POST 请求
 * 4. 解析大模型返回的 JSON 结果（riskLevel、conclusion、reason、suggestion）
 * 5. 如果 API 调用失败（如网络问题、API Key 过期），自动降级为本地模拟分析
 *
 * 配置项（在 application.yml 中）：
 * - ai.api-key: 智谱 API 密钥
 * - ai.api-url: API 地址（https://open.bigmodel.cn/api/paas/v4/chat/completions）
 * - ai.model: 模型名称（默认 glm-4-flash）
 */
@Service
public class AiAnalysisService {

    @Value("${ai.api-key}")
    private String apiKey;

    @Value("${ai.api-url}")
    private String apiUrl;

    @Value("${ai.model:glm-4-flash}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String analyzeRadarData(RadarData radarData) {
        try {
            // 尝试调用真实API，如果失败则使用模拟数据
            return callRealAiApi(radarData);
        } catch (Exception e) {
            System.err.println("AI分析失败，使用模拟数据: " + e.getMessage());
            return generateMockAnalysis(radarData);
        }
    }
    
    private String generateMockAnalysis(RadarData radarData) {
        float speed = radarData.getSpeed();
        float trajectoryY = radarData.getTrajectoryY();
        
        String riskLevel;
        String conclusion;
        String reason;
        String suggestion;
        
        if (speed > 3.0 || trajectoryY < -3.0) {
            riskLevel = "HIGH";
            conclusion = "跌倒";
            reason = String.format("速度(%.2f m/s)超过3.0 m/s阈值，或垂直位移(%.2f m)超过-3.0 m阈值，符合跌倒特征。", speed, trajectoryY);
            suggestion = "立即通知家属和医护人员前往查看老人情况，检查是否受伤，并及时采取必要的医疗措施。";
        } else if (speed > 1.5) {
            riskLevel = "MEDIUM";
            conclusion = "需要关注";
            reason = String.format("速度(%.2f m/s)处于1.5-3.0 m/s之间，建议持续监测老人活动状态。", speed);
            suggestion = "建议加强观察，如有异常及时采取措施。";
        } else {
            riskLevel = "LOW";
            conclusion = "正常活动";
            reason = String.format("速度(%.2f m/s)低于1.5 m/s，属于正常活动范围。", speed);
            suggestion = "当前状态正常，继续监测即可。";
        }
        
        return String.format(
            "{\"riskLevel\":\"%s\",\"conclusion\":\"%s\",\"reason\":\"%s\",\"suggestion\":\"%s\"}",
            riskLevel, conclusion, reason, suggestion
        );
    }

    private String callRealAiApi(RadarData radarData) throws Exception {
        float speed = radarData.getSpeed();
        float trajectoryX = radarData.getTrajectoryX();
        float trajectoryY = radarData.getTrajectoryY();
        
        // 1. 构造更详细的提示词
        String prompt = String.format(
                "你是一个专业的老年人跌倒检测AI分析专家。请分析以下雷达传感器数据：\n" +
                "【数据参数】\n" +
                "- 速度：%.2f m/s\n" +
                "- X轴轨迹变化：%.2f\n" +
                "- Y轴轨迹变化：%.2f\n" +
                "\n【判断标准】\n" +
                "1. 跌倒风险判定：\n" +
                "   - 高风险：速度 > 3.0 m/s（快速移动可能表示摔倒）\n" +
                "   - 中风险：速度 1.5-3.0 m/s（需要关注）\n" +
                "   - 低风险：速度 ≤ 1.5 m/s（正常活动）\n" +
                "2. 垂直位移判定：Y轴变化 < -3.0 表示可能发生跌倒\n" +
                "\n【输出要求】\n" +
                "请以JSON格式输出分析结果，包含以下字段：\n" +
                "- riskLevel: 风险等级（HIGH/MEDIUM/LOW）\n" +
                "- conclusion: 分析结论（跌倒/正常活动/需要关注）\n" +
                "- reason: 判断理由\n" +
                "- suggestion: 处理建议",
                speed, trajectoryX, trajectoryY
        );

        // 2. 构造请求体
        String requestBody = String.format(
                "{\"model\": \"%s\", \"messages\": [{\"role\": \"user\", \"content\": \"%s\"}]}",
                model, prompt
        );

        // 3. 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        // 4. 发送请求
        String responseStr = restTemplate.postForObject(apiUrl, entity, String.class);

        // 5. 解析结果
        JsonNode root = objectMapper.readTree(responseStr);
        String aiResult = root.path("choices").get(0).path("message").path("content").asText().trim();
        
        return aiResult;
    }

}
