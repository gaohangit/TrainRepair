package com.ydzbinfo.emis.trainRepair.trainMonitor.service;

import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.track.ZtTrackAreaEntity;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.track.ZtTrackPositionEntity;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrackPowerStateCur;

import java.util.List;
import java.util.Set;

public interface TrackPowerStateCurService extends IService<TrackPowerStateCur> {

    List<TrackPowerStateCur> getTrackPowerInfo(Set<String> trackCodeList, String unitCode);

    List<TrackPowerStateCur> selectTrackPowersFromTrackPositions(List<ZtTrackPositionEntity> trackPositions);

    List<TrackPowerStateCur> getTrackPowerInfoByTrackPower(TrackPowerStateCur trackPowerEntity);
    int deleteByTrackPower(TrackPowerStateCur trackPowerEntity);
    int addTrackPowerInfo(TrackPowerStateCur trackPowerEntity);
    void setTrackPowerInfo(TrackPowerStateCur trackPowerInfo);

    List<TrackPowerStateCur> getAllTrackPowers(String unitCode);

    List<ZtTrackAreaEntity> getTrackAreaByDept(String deptCode);
}
