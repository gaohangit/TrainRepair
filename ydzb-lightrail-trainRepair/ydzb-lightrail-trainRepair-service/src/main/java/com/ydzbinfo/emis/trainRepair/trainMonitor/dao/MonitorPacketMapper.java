package com.ydzbinfo.emis.trainRepair.trainMonitor.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.MonitorPacket;

import java.util.List;


/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author gaohan
 * @since 2021-03-01
 */
public interface MonitorPacketMapper extends BaseMapper<MonitorPacket> {

    List<MonitorPacket> getMonitorPackets();

    List<MonitorPacket>  getMonitorPacket(MonitorPacket monitorPacket);

    int addMonitorPacket(MonitorPacket monitorPacket);

    int updMonitorPacket(MonitorPacket monitorPacket);

    int delMonitorPacket(MonitorPacket monitorPacket);


}
