package com.ydzbinfo.emis.trainRepair.trackPowerInfo.service.impl;

import com.ydzbinfo.emis.trainRepair.trackPowerInfo.dao.TrackPowerInfoMapper;
import com.ydzbinfo.emis.trainRepair.trackPowerInfo.service.TrackPowerInfoService;
import com.ydzbinfo.emis.trainRepair.trainsetPostion.querymodel.TrackPowerEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Title:
 * Description:
 * Author: wuyuechang
 * Create Date Time: 2021/4/20 9:39
 * Update Date Time: 2021/4/20 9:39
 *
 * @see
 */
@Service
public class TrackPowerInfoServiceImpl implements TrackPowerInfoService {

    @Resource
    TrackPowerInfoMapper trackPowerInfoMapper;

    @Override
    public List<TrackPowerEntity> getTrackPowerInfoByOne(String trackCode, String unitCode, String trackPlaCode) {
        return trackPowerInfoMapper.getTrackPowerInfoByOne(trackCode, unitCode, trackPlaCode);
    }
}
