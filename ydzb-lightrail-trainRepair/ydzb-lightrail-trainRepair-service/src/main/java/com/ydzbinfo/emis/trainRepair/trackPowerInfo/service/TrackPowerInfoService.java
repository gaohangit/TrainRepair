package com.ydzbinfo.emis.trainRepair.trackPowerInfo.service;

import com.ydzbinfo.emis.trainRepair.trainsetPostion.querymodel.TrackPowerEntity;

import java.util.List;

public interface TrackPowerInfoService {

    List<TrackPowerEntity> getTrackPowerInfoByOne(String trackCode, String unitCode, String trackPlaCode);

}
