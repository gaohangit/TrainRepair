package com.ydzbinfo.emis.common.trainMonitor.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrackPowerStateHis;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author gaohan
 * @since 2021-02-26
 */
public interface ITrackPowerStateHisMapper extends BaseMapper<TrackPowerStateHis> {
    int addTrackPowerHisInfo(TrackPowerStateHis trackPowerHisEntity);


}
