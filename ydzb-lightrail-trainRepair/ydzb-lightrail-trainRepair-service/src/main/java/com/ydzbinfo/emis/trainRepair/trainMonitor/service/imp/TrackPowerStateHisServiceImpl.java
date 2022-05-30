package com.ydzbinfo.emis.trainRepair.trainMonitor.service.imp;

import com.ydzbinfo.emis.trainRepair.trainMonitor.dao.TrackPowerStateHisMapper;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrackPowerStateHis;
import com.ydzbinfo.emis.trainRepair.trainMonitor.service.TrackPowerStateHisService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author gaohan
 * @description
 * @createDate 2021/2/26 15:07
 **/
@Service
@Transactional
public class TrackPowerStateHisServiceImpl implements TrackPowerStateHisService {
    @Resource
    TrackPowerStateHisMapper trackpowerStateHisMapper;
    @Override
    public int addTrackPowerHisInfo(TrackPowerStateHis trackPowerHisEntity) {
        return trackpowerStateHisMapper.addTrackPowerHisInfo(trackPowerHisEntity);
    }
}
