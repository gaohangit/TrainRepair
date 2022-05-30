package com.ydzbinfo.emis.trainRepair.trainMonitor.service;

import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.PacketInfo;
import com.ydzbinfo.emis.trainRepair.trainMonitor.model.LevelRepairInfo;
import com.ydzbinfo.emis.trainRepair.trainMonitor.model.MonitorBase;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.MonitorPacket;

import java.util.List;

public interface MonitorPacketService {

    List<MonitorPacket> getMonitorPackets();

    List<MonitorPacket> getMonitorPacket(MonitorPacket monitorPacket);

    int addMonitorPacket(MonitorPacket monitorPacket);

    int updMonitorPacket(MonitorPacket monitorPacket);

    int delMonitorPacket(MonitorPacket monitorPacket);

    LevelRepairInfo getPacketRecordList(String trainsetName, String trainsetId , String unitCode)throws Exception;

    List<MonitorBase> getMonitorBase(String packetCode, String suitModel, String suitBatch);

    void updMonitorBase(List<MonitorBase> monitorPackets);

    List<PacketInfo> getPacketList(String packetCode, String suitModel, String suitBatch);
}
