// RadarDataServiceImpl.java
package com.iot.elderly.elderlycarebackend.service.impl;

import com.iot.elderly.elderlycarebackend.entity.RadarData;
import com.iot.elderly.elderlycarebackend.repository.RadarDataRepository;
import com.iot.elderly.elderlycarebackend.service.RadarDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 雷达数据服务实现 —— 最简单的 CRUD 操作
 *
 * 只有一个 saveRadarData 方法，将雷达数据保存到数据库。
 * 数据验证（如设备是否存在）在 Controller 层完成，这里只负责持久化。
 */
@Service
public class RadarDataServiceImpl implements RadarDataService {
    @Autowired
    private RadarDataRepository radarDataRepository;

    @Override
    public RadarData saveRadarData(RadarData radarData) {
        return radarDataRepository.save(radarData);
    }
}
