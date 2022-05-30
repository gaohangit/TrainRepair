package com.ydzbinfo.emis.trainRepair.repairworkflow.service.impl;


import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.trainRepair.repairworkflow.dao.FlowRunExtraInfoMapper;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.FlowRunExtraInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.IFlowRunExtraInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 流程运行额外信息表 服务实现类
 * </p>
 *
 * @author gaohan
 * @since 2021-05-14
 */
@Service
public class FlowRunExtraInfoServiceImpl extends ServiceImpl<FlowRunExtraInfoMapper, FlowRunExtraInfo> implements IFlowRunExtraInfoService {
    @Autowired
    FlowRunExtraInfoMapper flowRunExtraInfoMapper;

    @Override
    public void addFlowRunExtraInfo(FlowRunExtraInfo flowRunExtraInfo) {
        flowRunExtraInfoMapper.insert(flowRunExtraInfo);
    }
}
