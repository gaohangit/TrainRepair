package com.ydzbinfo.emis.common.trainMonitor.service;

import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.track.ZtTrackPositionEntity;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrackPowerStateCur;

import java.util.List;

public interface ITrackPowerStateCurService {

    List<TrackPowerStateCur> getTrackPowerInfo(List<String> trackCodeList, List<String> unitCodeList);

    List<TrackPowerStateCur> selectTrackPowersFromTrackPositions(List<ZtTrackPositionEntity> trackPositions);

    List<TrackPowerStateCur> getTrackPowerInfoByTrackPower(TrackPowerStateCur trackPowerEntity);
    int deleteByTrackPower(TrackPowerStateCur trackPowerEntity);
    int addTrackPowerInfo(TrackPowerStateCur trackPowerEntity);
    void setTrackPowerInfo(List<TrackPowerStateCur> trackPowerInfo);

}
