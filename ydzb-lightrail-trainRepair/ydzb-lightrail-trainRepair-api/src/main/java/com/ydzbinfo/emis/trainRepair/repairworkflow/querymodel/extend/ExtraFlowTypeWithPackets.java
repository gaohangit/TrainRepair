package com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend;

import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.ExtraFlowType;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.ExtraFlowTypePacket;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author gaohan
 * @description
 * @createDate 2021/5/8 16:38
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtraFlowTypeWithPackets extends ExtraFlowType {
    //作业包配置
    List<ExtraFlowTypePacket> extraFlowTypePackets;
}
