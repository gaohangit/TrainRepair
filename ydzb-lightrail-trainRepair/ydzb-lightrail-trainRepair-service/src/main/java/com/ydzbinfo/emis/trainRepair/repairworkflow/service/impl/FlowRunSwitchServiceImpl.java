package com.ydzbinfo.emis.trainRepair.repairworkflow.service.impl;


import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.trainRepair.repairworkflow.dao.FlowRunSwitchMapper;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.FlowRunSwitch;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.IFlowRunSwitchService;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.eqParam;

/**
 * <p>
 * 流程运行切换记录表 服务实现类
 * </p>
 *
 * @author gaohan
 * @since 2021-05-13
 */
@Service
public class FlowRunSwitchServiceImpl extends ServiceImpl<FlowRunSwitchMapper, FlowRunSwitch> implements IFlowRunSwitchService {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    FlowRunSwitchMapper flowRunSwitchMapper;

    @Override
    public List<FlowRunSwitch> getFlowRunSwitchListByDayPlanId(String dayPlanId) {
        return MybatisPlusUtils.selectList(
            flowRunSwitchMapper,
            eqParam(FlowRunSwitch::getDayPlanId, dayPlanId)
        );
    }
}
