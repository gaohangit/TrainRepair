package com.ydzbinfo.emis.trainRepair.repairworkflow.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.trainRepair.repairworkflow.dao.FlowExtraInfoMapper;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.FlowExtraInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.IFlowExtraInfoService;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.eqParam;

/**
 * <p>
 * 流程额外信息表	如：是否为默认流程，流程选择聚合条件，切换人员聚合条件，触发人员聚合条件 服务实现类
 * </p>
 *
 * @author gaohan
 * @since 2021-04-02
 */
@Service
public class FlowExtraInfoServiceImpl extends ServiceImpl<FlowExtraInfoMapper, FlowExtraInfo> implements IFlowExtraInfoService {
    @Resource
    FlowExtraInfoMapper flowExtraInfoMapper;

    @Override
    public void delFlowExtraInfoByFlowId(String id) {
        MybatisPlusUtils.delete(
            flowExtraInfoMapper,
            eqParam(FlowExtraInfo::getFlowId, id)
        );
    }

    @Override
    public void addFlowExtraInfo(FlowExtraInfo flowExtraInfo) {
        flowExtraInfoMapper.insert(flowExtraInfo);
    }

    @Override
    public FlowExtraInfo getFlowExtraInfoByFlowId(String flowId) {
        FlowExtraInfo flowExtraInfo = new FlowExtraInfo();
        flowExtraInfo.setType("CONDITION");
        flowExtraInfo.setFlowId(flowId);
        return flowExtraInfoMapper.selectOne(flowExtraInfo);
    }

    @Override
    public void setFlowDefault(String flowId, String defaultType) {
        FlowExtraInfo flowExtraInfo = new FlowExtraInfo();
        flowExtraInfo.setFlowId(flowId);
        flowExtraInfo.setType("DEFAULT");
        if (flowExtraInfoMapper.selectOne(flowExtraInfo) == null) {
            flowExtraInfo.setType("0");
            flowExtraInfoMapper.insert(flowExtraInfo);
        } else {
            flowExtraInfoMapper.setDefaultType(flowId, defaultType);
        }
    }

    @Override
    public FlowExtraInfo getFlowExtraDefaultByFlowId(String flowId) {
        FlowExtraInfo flowExtraInfo = new FlowExtraInfo();
        flowExtraInfo.setFlowId(flowId);
        flowExtraInfo.setType("DEFAULT");
        return flowExtraInfoMapper.selectOne(flowExtraInfo);
    }
}
