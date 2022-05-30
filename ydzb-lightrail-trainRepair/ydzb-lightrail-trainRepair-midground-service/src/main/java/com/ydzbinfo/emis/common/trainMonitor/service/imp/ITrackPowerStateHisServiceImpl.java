package com.ydzbinfo.emis.common.trainMonitor.service.imp;

import com.ydzbinfo.emis.common.trainMonitor.dao.ITrackPowerStateHisMapper;
import com.ydzbinfo.emis.common.trainMonitor.service.ITrackPowerStateHisService;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrackPowerStateHis;
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
public class ITrackPowerStateHisServiceImpl implements ITrackPowerStateHisService {
    @Resource
    ITrackPowerStateHisMapper trackpowerStateHisMapperI;
    @Override
    public int addTrackPowerHisInfo(TrackPowerStateHis trackPowerHisEntity) {
        return trackpowerStateHisMapperI.addTrackPowerHisInfo(trackPowerHisEntity);
    }
}
