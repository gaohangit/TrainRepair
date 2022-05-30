package com.ydzbinfo.emis.trainRepair.taskAllot.service;

import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyMTaskallotpacket;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyMTaskcarpart;

import java.util.List;
import java.util.Map;

public interface XzyMTaskcarpartService {

    List<XzyMTaskcarpart> getTaskAllotListByPacket(String packetId);

    boolean setTaskAllotData(List<XzyMTaskallotpacket> xzyMTaskallotpacketList,List<XzyMTaskcarpart> taskcarpartList);

    int setTaskAllot(List<XzyMTaskcarpart> taskcarpartList);


    List<XzyMTaskcarpart> getCarPartListByParam(Map<String,String> map);

    int deleteAll(String unitCode,String deptCode);
    
    int deleteTaskCarPatrs(Map<String,String> map);
    
    List<XzyMTaskcarpart> getCarPartLists(Map<String,String> map);

    List<XzyMTaskcarpart> getTaskAllotListByPacketIds(List<String> taskAllotPacketIds);

    List<XzyMTaskcarpart> getCarPartListByItemCodeList(List<String> itemCodeList);
}
