package com.iot.elderly.elderlycarebackend.service;

import com.iot.elderly.elderlycarebackend.entity.RadarData;

/**
 * 雷达数据服务接口 —— 定义雷达数据的保存操作
 *
 * 实现类：RadarDataServiceImpl
 *
 * 方法说明：
 * - saveRadarData(radarData): 保存雷达数据到数据库
 *   注释中提到的"预处理数据、发送到边缘层"等功能是预留的扩展点，当前未实现
 */
public interface RadarDataService {
    RadarData saveRadarData(RadarData radarData); // 保存雷达数据
    // 其他方法：预处理数据、发送到边缘层等
}
