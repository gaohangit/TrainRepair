package com.ydzbinfo.emis.trainRepair.trainMonitor.service.imp;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.trainRepair.trainMonitor.dao.TrainsetLocationConfigMapper;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrainsetLocationConfig;
import com.ydzbinfo.emis.trainRepair.trainMonitor.service.TrainsetLocationConfigService;
import com.ydzbinfo.emis.trainRepair.trainMonitor.utils.TrainSetLocationConfigEnum;
import com.ydzbinfo.emis.utils.ContextUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author gaohan
 * @description
 * @createDate 2021/3/1 14:47
 **/
@Service
@Transactional
public class TrainsetLocationConfigServiceImpl extends ServiceImpl<TrainsetLocationConfigMapper, TrainsetLocationConfig> implements TrainsetLocationConfigService {
    @Resource
    TrainsetLocationConfigMapper trainsetlocationConfigMapper;

    @Override
    public List<TrainsetLocationConfig> getTrainsetlocationConfigs(String unitCode) {
        List<TrainsetLocationConfig> trainsetLocationConfigs = trainsetlocationConfigMapper.getTrainsetlocationConfigs(unitCode);
        List<String> paramNameList = trainsetLocationConfigs.stream().filter(v -> v.getUnitCode().equals(unitCode)).map(v -> v.getParamName()).collect(Collectors.toList());
        for (TrainSetLocationConfigEnum value : TrainSetLocationConfigEnum.values()) {
            if(!paramNameList.contains(value.getValue())){
                TrainsetLocationConfig trainsetLocationConfig=new TrainsetLocationConfig();
                trainsetLocationConfig.setId(UUID.randomUUID().toString());
                trainsetLocationConfig.setParamName(value.getValue());
                trainsetLocationConfig.setParamValue(value.getLabel());
                trainsetLocationConfig.setUnitCode(unitCode);
                trainsetLocationConfig.setUnitName(ContextUtils.getUnitName());
                trainsetlocationConfigMapper.addTrainsetlocationConfig(trainsetLocationConfig);
            }
        }
        return trainsetlocationConfigMapper.getTrainsetlocationConfigs(unitCode);
    }

    @Override
    public TrainsetLocationConfig getTrainsetlocationConfig(TrainsetLocationConfig trainsetlocationConfig) {
        trainsetlocationConfig.setUnitCode(ContextUtils.getUnitCode());
        return trainsetlocationConfigMapper.getTrainsetlocationConfig(trainsetlocationConfig);
    }

    @Override
    public void addTrainsetlocationConfig(TrainsetLocationConfig trainsetlocationConfig) {
        TrainsetLocationConfig queryDate = new TrainsetLocationConfig();
        queryDate.setUnitCode(trainsetlocationConfig.getUnitCode());
        queryDate.setParamName(trainsetlocationConfig.getParamName());
        if(getTrainsetlocationConfig(queryDate)==null){
            trainsetlocationConfigMapper.addTrainsetlocationConfig(trainsetlocationConfig);
        }
    }

    @Override
    public int delTrainsetlocationConfig(TrainsetLocationConfig trainsetlocationConfig) {
        return trainsetlocationConfigMapper.delTrainsetlocationConfig(trainsetlocationConfig);
    }

    @Override
    public int updTrainsetlocationConfig(TrainsetLocationConfig trainsetlocationConfig) {
        return trainsetlocationConfigMapper.updTrainsetlocationConfig(trainsetlocationConfig);
    }
}
