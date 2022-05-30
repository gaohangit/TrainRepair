package com.ydzbinfo.emis.trainRepair.repairworkflow.service;

import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.FlowTypeInfoWithPackets;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.FlowType;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.base.BaseFlowType;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend.FlowTypeWithComments;

import java.util.List;

/**
 * <p>
 * 流程类型表 服务类
 * </p>
 *
 * @author gaohan
 * @since 2021-04-02
 */
public interface IFlowTypeService extends IService<FlowType> {
    List<BaseFlowType> getFlowTypeList(String unitCode);

    List<BaseFlowType> getFlowTypeList(String unitCode, boolean config);

    /**
     * 查询流程类型
     *
     * @param config
     * @param extraFlowType
     * @return
     */
    List<BaseFlowType> getFlowTypeList(String unitCode, boolean config, boolean extraFlowType);

    /**
     * 获取全部流程类型和作业包配置
     *
     * @return
     */
    List<FlowTypeInfoWithPackets> getFlowTypeAndPacket(String unitCode);


    List<FlowType> getFlowTypeInfos();


    List<FlowTypeWithComments> getFlowTypeWithComment(String unitCode, List<String> codeList);

    List<BaseFlowType> getFlowTypesByFlowPageCode(String flowPageCode,String unitCode);


}
