package com.ydzbinfo.emis.trainRepair.repairworkflow.service;


import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.PacketInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.FlowTypeInfoWithPackets;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.ExtraFlowTypePacket;

import java.util.List;

/**
 * <p>
 * 额外流程类型作业包配置表 服务类
 * </p>
 *
 * @author gaohan
 * @since 2021-05-08
 */
public interface IExtraFlowTypePacketService extends IService<ExtraFlowTypePacket> {
    //修改额外流程类型作业包
    void setExtraFlowTypePacket(FlowTypeInfoWithPackets flowTypeInfoWithPackets);

    void sendSetExtraFlowTypePacket(FlowTypeInfoWithPackets flowTypeInfoWithPackets);

    List<PacketInfo> getPacketsByFlowType(String flowTypeCode, String parentFlowTypeCode, String unitCode, Boolean flag);

}
