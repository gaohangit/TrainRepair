package com.ydzbinfo.emis.common.trainMonitor.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.track.ZtTrackPositionEntity;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrackPowerStateCur;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author gaohan
 * @since 2021-02-26
 */
public interface ITrackPowerStateCurMapper extends BaseMapper<TrackPowerStateCur> {
    List<TrackPowerStateCur> getTrackPowerInfo(Map<String, Object> map);

    List<TrackPowerStateCur> selectTrackPowersFromTrackPositions(@Param("trackPositions")List<ZtTrackPositionEntity> trackPositions);

    List<TrackPowerStateCur> getTrackPowerInfoByTrackPower(TrackPowerStateCur trackPowerEntity);

    int deleteByTrackPower(TrackPowerStateCur trackPowerEntity);

    int addTrackPowerInfo(TrackPowerStateCur trackPowerEntity);

    int updTrackPowerInfo(TrackPowerStateCur trackPowerStateCur);
}
