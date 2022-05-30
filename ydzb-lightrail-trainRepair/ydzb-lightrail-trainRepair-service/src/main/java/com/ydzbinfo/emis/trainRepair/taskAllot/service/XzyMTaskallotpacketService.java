package com.ydzbinfo.emis.trainRepair.taskAllot.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.AllotData;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.QueryTaskAllot;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.RepairItemVo;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyMTaskallotpacket;

import java.util.List;
import java.util.Map;

public interface XzyMTaskallotpacketService {

    List<XzyMTaskallotpacket> getTaskAllotPacketByTaskId(List<String> taskIds);

    int setTaskAllotPacket(XzyMTaskallotpacket taskallotpacket);

    List<XzyMTaskallotpacket> getTaskAllotPacketById(String packetId);

    int deleteAll(String packetId);
    
    int deletePackets(Map<String,String> map);
    
    List<XzyMTaskallotpacket> getTaskAllotPackets(Map<String,String> map);

    JSONObject getRepairTask(String unitCode, String dayplanId, String deptCode, String taskAllotType, String mode);

    JSONObject getQuery(String unitCode, String dayplanId, String deptCode, String taskAllotType, String mode);

    int setTaskAllotPackets(List<XzyMTaskallotpacket> taskallotpackets);

    Map setTaskAllot(Map<String, Object> map);

    boolean setTaskDataAndStatus(List<AllotData> allotDataList);

    /**
     * 派工查询接口
     * @return QueryTaskAllotModel实体集合
     */
    List<QueryTaskAllot> getQueryTaskAllotList(JSONObject jsonObject);

    /**
     * 查询派工配置二级修项目
     */
    List<RepairItemVo> selectRepairItemList(Map map, Page page);

}
