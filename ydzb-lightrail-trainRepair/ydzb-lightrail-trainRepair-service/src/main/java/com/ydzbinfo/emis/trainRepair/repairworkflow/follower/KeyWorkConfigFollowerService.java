package com.ydzbinfo.emis.trainRepair.repairworkflow.follower;

import com.ydzbinfo.emis.configs.kafka.repairworkflow.RepairWorkflowConfigEnableReceiveCloudDataCondition;
import com.ydzbinfo.emis.configs.kafka.repairworkflow.RepairWorkflowConfigMqSink;
import com.ydzbinfo.emis.trainRepair.repairworkflow.constant.KeyWorkConfigHeaderConstant;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.KeyWorkWithDetail;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.KeyWorkType;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.IKeyWorkConfigDetailService;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.IKeyWorkConfigService;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.IKeyWorkTypeService;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 高晗
 * @description监听关键作业配置数据推送
 * @createDate 2021/11/16 15:35
 **/
@Component
@Conditional(RepairWorkflowConfigEnableReceiveCloudDataCondition.class)
public class KeyWorkConfigFollowerService {
    @Resource
    IKeyWorkTypeService keyWorkTypeService;

    @Resource
    IKeyWorkConfigService keyWorkConfigService;

    @Resource
    IKeyWorkConfigDetailService keyWorkConfigDetailService;

    @Transactional
    @StreamListener(target = RepairWorkflowConfigMqSink.KEYWORKCONFIG_INPUT, condition = "headers['operateType']=='" + KeyWorkConfigHeaderConstant.SET_KEY_WORK_TYPE + "'")
    public void setKeyWorkTypeConfigData(List<KeyWorkType> keyWorkTypeList) {
        keyWorkTypeService.sendSetKeyWorkType(keyWorkTypeList);
    }

    @Transactional
    @StreamListener(target = RepairWorkflowConfigMqSink.KEYWORKCONFIG_INPUT, condition = "headers['operateType']=='" + KeyWorkConfigHeaderConstant.SET_KEY_WORK + "'")
    public void setKeyWorkConfigData(List<KeyWorkWithDetail> keyWorkWithDetails) {
        keyWorkWithDetails.forEach(keyWorkWithDetail -> {
            keyWorkConfigService.delKeyWorkConfig(keyWorkWithDetail.getKeyWorkConfig().getId());
            keyWorkConfigService.insert(keyWorkWithDetail.getKeyWorkConfig());
            keyWorkWithDetail.getKeyWorkConfigDetails().forEach(keyWorkConfigDetail -> {
                keyWorkConfigDetailService.addKeyWorkConfigDetail(keyWorkConfigDetail);
            });
        });
    }

    @Transactional
    @StreamListener(target = RepairWorkflowConfigMqSink.KEYWORKCONFIG_INPUT, condition = "headers['operateType']=='" + KeyWorkConfigHeaderConstant.DELETE_KEY_WORK + "'")
    public void deleteKeyWorkConfigData(String id) {
        keyWorkConfigService.sendDelKeyWorkConfig(id);
    }
}
