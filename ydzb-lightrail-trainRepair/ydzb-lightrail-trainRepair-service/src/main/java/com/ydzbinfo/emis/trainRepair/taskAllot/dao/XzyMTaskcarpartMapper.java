package com.ydzbinfo.emis.trainRepair.taskAllot.dao;

import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyMTaskcarpart;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface XzyMTaskcarpartMapper {

    List<XzyMTaskcarpart> getTaskAllotListByPacket(String packetId);

    int setTaskAllot(XzyMTaskcarpart taskcarpart);

    List<XzyMTaskcarpart> getCarPartListByParam(Map<String,String> map);

    int deleteAll(@Param("unitCode") String unitCode,@Param("deptCode") String deptCode);
    
    int deleteTaskCarPatrs(Map<String,String> map);
    
    List<XzyMTaskcarpart> getCarPartLists(Map<String,String> map);

    List<XzyMTaskcarpart> getTaskAllotListByPacketIds(List<String> taskAllotPacketIds);

    List<XzyMTaskcarpart> getCarPartListByItemCodeList(List<String> itemCodeList);
}
