package com.ydzbinfo.emis.trainRepair.repairworkflow.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.jxdinfo.hussar.core.mq.MessageUtil;
import com.jxdinfo.hussar.core.mq.MessageWrapper;
import com.ydzbinfo.emis.configs.kafka.repairworkflow.RepairWorkflowConfigMqSource;
import com.ydzbinfo.emis.trainRepair.repairworkflow.constant.FlowConfigHeaderConstant;
import com.ydzbinfo.emis.trainRepair.repairworkflow.dao.FlowMarkConfigMapper;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.FlowMarkConfig;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.IFlowMarkConfigService;
import com.ydzbinfo.emis.utils.SpringCloudStreamUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 流程标记配置表，目前仅支持临修作业的关键字配置 服务实现类
 * </p>
 *
 * @author gaohan
 * @since 2021-04-30
 */
@Service
public class FlowMarkConfigServiceImpl extends ServiceImpl<FlowMarkConfigMapper, FlowMarkConfig> implements IFlowMarkConfigService {
    @Resource
    FlowMarkConfigMapper flowMarkConfigMapper;

    @Autowired(required = false)
    private RepairWorkflowConfigMqSource repairWorkflowConfigSource;

    @Override
    public List<FlowMarkConfig> getFlowMarkConfigs(String unitCode) {
        Map map = new HashMap();
        map.put("S_UNITCODE", unitCode);

        return flowMarkConfigMapper.selectByMap(map);
    }

    @Override
    public void delFlowMarkConfigById(String id) {
        this.sendDelFlowMarkConfigById(id);
        //发送数据同步
        if (SpringCloudStreamUtil.enableSendCloudData(RepairWorkflowConfigMqSource.class)) {
            MessageWrapper<String> messageWrapper = new MessageWrapper<>(FlowConfigHeaderConstant.class, FlowConfigHeaderConstant.DELETE_FLOW_MARK_CONFIG, id);
            boolean sendFlag = MessageUtil.sendMessage(repairWorkflowConfigSource.flowConfigChannel(), messageWrapper);
        }
    }

    @Override
    public void sendDelFlowMarkConfigById(String id) {
        flowMarkConfigMapper.deleteById(id);
    }

    @Override
    public String addFlowMarkConfig(FlowMarkConfig flowMarkConfig) {
        String result = this.sendAddFlowMarkConfig(flowMarkConfig);

        if (SpringCloudStreamUtil.enableSendCloudData(RepairWorkflowConfigMqSource.class)) {
            MessageWrapper<FlowMarkConfig> messageWrapper = new MessageWrapper<>(FlowConfigHeaderConstant.class, FlowConfigHeaderConstant.INSERT_FLOW_MARK_CONFIG, flowMarkConfig);
            boolean sendFlag = MessageUtil.sendMessage(repairWorkflowConfigSource.flowConfigChannel(), messageWrapper);
        }
        return result;
    }

    @Override
    public String sendAddFlowMarkConfig(FlowMarkConfig flowMarkConfig) {
        FlowMarkConfig queryData = flowMarkConfigMapper.selectOne(flowMarkConfig);
        if (queryData != null) {
            return "该关键字已存在，请重新输入！";
        }
        flowMarkConfig.setType("1");
        flowMarkConfigMapper.insert(flowMarkConfig);
        return "";
    }
}
