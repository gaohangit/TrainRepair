package com.ydzbinfo.emis.trainRepair.repairworkflow.service;

import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.FlowMarkConfig;

import java.util.List;

/**
 * <p>
 * 流程标记配置表，目前仅支持临修作业的关键字配置 服务类
 * </p>
 *
 * @author gaohan
 * @since 2021-04-30
 */
public interface IFlowMarkConfigService extends IService<FlowMarkConfig> {
    List<FlowMarkConfig> getFlowMarkConfigs(String unitCode);

    void delFlowMarkConfigById(String id);

    void sendDelFlowMarkConfigById(String id);

    String addFlowMarkConfig(FlowMarkConfig flowMarkConfig);

    String sendAddFlowMarkConfig(FlowMarkConfig flowMarkConfig);
}
