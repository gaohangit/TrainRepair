package com.ydzbinfo.emis.trainRepair.repairworkflow.service;

import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.FlowTypeInfoWithPackets;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.ExtraFlowType;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend.ExtraFlowTypeWithPackets;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 额外流程类型表 服务类
 * </p>
 *
 * @author gaohan
 * @since 2021-04-28
 */
public interface IExtraFlowTypeService extends IService<ExtraFlowType> {

    List<ExtraFlowTypeWithPackets> getExtraFlowTypeWithPacket(String unitCode,List<String> codeList,String parentFlowTypeCode);

    void setExtraFlowType(FlowTypeInfoWithPackets flowTypeInfoWithPackets);

    Set<String> getPacketCodeForConfigurablePacketList(String unitCode,String parentFlowTypeCode, String flowTypeCode);

    List<ExtraFlowType> getExtraFlowTypeList(String code,String unitCode);

    List<ExtraFlowType> getPacketIndependent(String packetCode, String unitCode);

    List<ExtraFlowTypeWithPackets> getPacketNarrow(String unitCode);

    List<String> getExtraFlowTypeListByFlow(String unitCode,List<String> parentFlowTypeCode);



}
