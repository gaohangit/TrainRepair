package com.ydzbinfo.emis.trainRepair.trackPowerInfo.dao;

import com.ydzbinfo.emis.trainRepair.trainsetPostion.querymodel.TrackPowerEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TrackPowerInfoMapper {

    List<TrackPowerEntity> getTrackPowerInfoByOne(@Param("trackCode") String trackCode, @Param("unitCode") String unitCode, @Param("trackPlaCode") String trackPlaCode);
}
