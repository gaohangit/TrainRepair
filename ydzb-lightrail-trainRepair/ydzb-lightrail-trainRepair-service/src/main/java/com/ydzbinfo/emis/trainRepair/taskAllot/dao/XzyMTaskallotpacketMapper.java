package com.ydzbinfo.emis.trainRepair.taskAllot.dao;

import com.baomidou.mybatisplus.plugins.Page;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.RepairItemVo;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.TaskAllotCarPartCount;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyMTaskallotpacket;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface XzyMTaskallotpacketMapper {

    List<XzyMTaskallotpacket> getTaskAllotPacketByTaskId(List<String> taskIds);

    int setTaskAllotPacket(XzyMTaskallotpacket taskallotpacket);

    List<XzyMTaskallotpacket> getTaskAllotPacketById(String packetId);

    int deleteAll(String packetId);
    
    int deletePackets(Map<String,String> map);
    
    List<XzyMTaskallotpacket> getTaskAllotPackets(Map<String,String> map);

    List<RepairItemVo> selectRepairItemList(Map<String,String> map, Page page);

    List<TaskAllotCarPartCount> getTaskCarPartCount(@Param("unitCode") String unitCode, @Param("dayPlanId") String dayPlanId);
}
