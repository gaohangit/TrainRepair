package com.ydzbinfo.emis.trainRepair.repairworkflow.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.jxdinfo.hussar.core.mq.MessageUtil;
import com.jxdinfo.hussar.core.mq.MessageWrapper;
import com.ydzbinfo.emis.configs.kafka.repairworkflow.RepairWorkflowConfigMqSource;
import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.trainRepair.repairworkflow.constant.KeyWorkConfigHeaderConstant;
import com.ydzbinfo.emis.trainRepair.repairworkflow.dao.KeyWorkConfigMapper;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.KeyWorkConfigInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.KeyWorkExtraColumnOption;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.KeyWorkWithDetail;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.KeyWorkConfig;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.KeyWorkConfigDetail;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.KeyWorkType;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend.KeyWorkConfigWithDetail;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.IKeyWorkConfigDetailService;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.IKeyWorkConfigService;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.IKeyWorkTypeService;
import com.ydzbinfo.emis.trainRepair.repairworkflow.utils.FlowUtil;
import com.ydzbinfo.emis.trainRepair.repairworkflow.utils.WorkEnvEnum;
import com.ydzbinfo.emis.utils.*;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.eqParam;

/**
 * <p>
 * 关键作业配置表 服务实现类
 * </p>
 *
 * @author 高晗
 * @since 2021-06-18
 */
@Service
@Transactional
public class KeyWorkConfigServiceImpl extends ServiceImpl<KeyWorkConfigMapper, KeyWorkConfig> implements IKeyWorkConfigService {
    @Autowired
    KeyWorkConfigMapper keyWorkConfigMapper;

    @Autowired
    IKeyWorkConfigDetailService keyWorkConfigDetailService;

    @Autowired
    IKeyWorkTypeService keyWorkTypeService;

    @Autowired
    IRemoteService remoteService;


    @Autowired(required = false)
    private RepairWorkflowConfigMqSource repairWorkflowConfigSource;

    @Override
    public List<KeyWorkConfigInfo> getKeyWorkConfigInfoForCarNo(String unitCode, String content) {
        List<KeyWorkConfigInfo> keyWorkConfigInfoList = this.getKeyWorkConfigInfo(unitCode, content, null);
        return keyWorkConfigInfoList;
    }

    @Override
    public List<KeyWorkConfigInfo> getKeyWorkConfigInfoByTrainModel(String unitCode, String trainModel) {
        return this.getKeyWorkConfigInfo(unitCode, null, trainModel);
    }

    @Override
    public List<KeyWorkConfigInfo> getKeyWorkConfigInfo(String unitCode, String content, String trainModel) {
        List<KeyWorkConfigInfo> keyWorkConfigInfoList = new ArrayList<>();
        List<KeyWorkConfigWithDetail> keyWorkConfigWithDetails = keyWorkConfigMapper.getKeyWorkConfigWithDetails(unitCode, MybatisOgnlUtils.replaceWildcardChars(content), trainModel);

        for (KeyWorkConfigWithDetail keyWorkConfigWithDetail : keyWorkConfigWithDetails) {
            KeyWorkConfigInfo keyWorkConfigInfo = new KeyWorkConfigInfo();
            BeanUtils.copyProperties(keyWorkConfigWithDetail, keyWorkConfigInfo);
            for (KeyWorkConfigDetail keyWorkConfigDetail : keyWorkConfigWithDetail.getKeyWorkConfigDetails()) {
                switch (keyWorkConfigDetail.getType()) {
                    case "FUNCTION_CLASS":
                        keyWorkConfigInfo.setFunctionClass(keyWorkConfigDetail.getValue());
                        break;
                    case "BATCH_BOM_NODE_CODE":
                        keyWorkConfigInfo.setBatchBomNodeCode(keyWorkConfigDetail.getValue());
                        break;
                    case "KEY_WORK_TYPE":
                        keyWorkConfigInfo.setKeyWorkType(keyWorkConfigDetail.getValue());
                        break;
                    case "CAR":
                        if (keyWorkConfigDetail.getValue() != null) {
                            keyWorkConfigInfo.setCarNoList(Arrays.asList(keyWorkConfigDetail.getValue().split(",")));
                        }
                        break;
                    case "POSITION":
                        keyWorkConfigInfo.setPosition(keyWorkConfigDetail.getValue());
                        break;
                    case "WORK_ENV":
                        keyWorkConfigInfo.setWorkEnv(keyWorkConfigDetail.getValue());
                        break;
                }
            }
            keyWorkConfigInfoList.add(keyWorkConfigInfo);
        }
        return keyWorkConfigInfoList;
    }

    @Override
    public void setKeyWorkConfig(KeyWorkConfigInfo keyWorkConfigInfo) {
        List<String> trainModelList = Arrays.asList(keyWorkConfigInfo.getTrainModel().split(","));
        trainModelList.forEach(trainModel -> {
            //同一车型下不能有相同的作业内容
            KeyWorkConfig config = this.getKeyWorkConfig(trainModel, keyWorkConfigInfo.getContent());
            if (config != null && !config.getId().equals(keyWorkConfigInfo.getId())) {
                throw new RuntimeException(trainModel + ":同一车型下不能有相同的作业内容");
            }
        });

        String id = keyWorkConfigInfo.getId();
        if (StringUtils.isNotBlank(id)) {
            this.delKeyWorkConfig(id);
        }

        List<KeyWorkWithDetail> keyWorkWithDetails = new ArrayList<>();
        for (String trainModel : trainModelList) {
            KeyWorkConfig keyWorkConfig = new KeyWorkConfig();
            BeanUtils.copyProperties(keyWorkConfigInfo, keyWorkConfig);
            keyWorkConfig.setUnitName(ContextUtils.getUnitName());
            keyWorkConfig.setTrainModel(trainModel);
            this.addKeyWorkConfig(keyWorkConfig);

            List<KeyWorkConfigDetail> keyWorkConfigDetails = new ArrayList<>();
            KeyWorkConfigDetail keyWorkConfigDetail;
            if (StringUtils.isNotBlank(keyWorkConfigInfo.getFunctionClass())) {
                keyWorkConfigDetail = new KeyWorkConfigDetail();
                keyWorkConfigDetail.setType("FUNCTION_CLASS");
                keyWorkConfigDetail.setValue(keyWorkConfigInfo.getFunctionClass());
                keyWorkConfigDetails.add(keyWorkConfigDetail);
            }
            if (StringUtils.isNotBlank(keyWorkConfigInfo.getBatchBomNodeCode())) {
                keyWorkConfigDetail = new KeyWorkConfigDetail();
                keyWorkConfigDetail.setType("BATCH_BOM_NODE_CODE");
                keyWorkConfigDetail.setValue(keyWorkConfigInfo.getBatchBomNodeCode());
                keyWorkConfigDetails.add(keyWorkConfigDetail);
            }
            if (StringUtils.isNotBlank(keyWorkConfigInfo.getKeyWorkType())) {
                keyWorkConfigDetail = new KeyWorkConfigDetail();
                keyWorkConfigDetail.setType("KEY_WORK_TYPE");
                keyWorkConfigDetail.setValue(keyWorkConfigInfo.getKeyWorkType());
                keyWorkConfigDetails.add(keyWorkConfigDetail);
            }
            if (keyWorkConfigInfo.getCarNoList() != null) {
                keyWorkConfigDetail = new KeyWorkConfigDetail();
                keyWorkConfigDetail.setType("CAR");
                List<String> carNoList = TrainsetUtils.generateCarNoListFromMarshalCount(
                    remoteService.getMarshalCountByTrainType(trainModel)
                );
                List<String> filterCarNos = keyWorkConfigInfo.getCarNoList().stream().filter(v -> carNoList.contains(v)).collect(Collectors.toList());
                keyWorkConfigDetail.setValue(String.join(",", filterCarNos));
                keyWorkConfigDetails.add(keyWorkConfigDetail);
            }
            if (StringUtils.isNotBlank(keyWorkConfigInfo.getPosition())) {
                keyWorkConfigDetail = new KeyWorkConfigDetail();
                keyWorkConfigDetail.setType("POSITION");
                keyWorkConfigDetail.setValue(keyWorkConfigInfo.getPosition());
                keyWorkConfigDetails.add(keyWorkConfigDetail);
            }
            if (StringUtils.isNotBlank(keyWorkConfigInfo.getWorkEnv())) {
                keyWorkConfigDetail = new KeyWorkConfigDetail();
                keyWorkConfigDetail.setType("WORK_ENV");
                keyWorkConfigDetail.setValue(keyWorkConfigInfo.getWorkEnv());
                keyWorkConfigDetails.add(keyWorkConfigDetail);
            }
            //组织数据同步
            KeyWorkWithDetail keyWorkWithDetail = new KeyWorkWithDetail();
            keyWorkWithDetail.setKeyWorkConfig(keyWorkConfig);
            List<KeyWorkConfigDetail> keyWorkConfigDetailList = new ArrayList<>();
            for (KeyWorkConfigDetail workConfigDetail : keyWorkConfigDetails) {
                workConfigDetail.setKeyWorkConfigId(keyWorkConfig.getId());
                keyWorkConfigDetailService.addKeyWorkConfigDetail(workConfigDetail);
                keyWorkConfigDetailList.add(workConfigDetail);
            }
            keyWorkWithDetail.setKeyWorkConfigDetails(keyWorkConfigDetailList);
            keyWorkWithDetails.add(keyWorkWithDetail);
        }

        //发送数据同步
        if (SpringCloudStreamUtil.enableSendCloudData(RepairWorkflowConfigMqSource.class)) {
            MessageWrapper<List<KeyWorkWithDetail>> messageWrapper = new MessageWrapper<>(KeyWorkConfigHeaderConstant.class, KeyWorkConfigHeaderConstant.SET_KEY_WORK, keyWorkWithDetails);
            boolean sendFlag = MessageUtil.sendMessage(repairWorkflowConfigSource.keyWorkConfigChannel(), messageWrapper);
        }
    }

    @Override
    public void delKeyWorkConfig(String id) {
        this.sendDelKeyWorkConfig(id);
        //发送数据同步
        if (SpringCloudStreamUtil.enableSendCloudData(RepairWorkflowConfigMqSource.class)) {
            MessageWrapper<String> messageWrapper = new MessageWrapper<>(KeyWorkConfigHeaderConstant.class, KeyWorkConfigHeaderConstant.DELETE_KEY_WORK, id);
            boolean sendFlag = MessageUtil.sendMessage(repairWorkflowConfigSource.keyWorkConfigChannel(), messageWrapper);
        }
    }

    @Override
    public void sendDelKeyWorkConfig(String id) {
        keyWorkConfigDetailService.delKeyWorkConfigDetail(id);
        MybatisPlusUtils.delete(
            keyWorkConfigMapper,
            eqParam(KeyWorkConfig::getId, id)
        );
    }

    @Override
    public void addKeyWorkConfig(KeyWorkConfig keyWorkConfig) {
        keyWorkConfig.setCreateTime(new Date());
        keyWorkConfig.setCreateWorkerId(UserUtil.getUserInfo().getStaffId());
        keyWorkConfig.setCreateWorkerName(UserUtil.getUserInfo().getName());
        keyWorkConfigMapper.insert(keyWorkConfig);
    }

    @Override
    public List<KeyWorkExtraColumnOption> getKeyWorkExtraColumnValueList(String columnKey, String trainModel, boolean showDelete) {
        List<KeyWorkExtraColumnOption> keyWorkExtraColumnOptions = new ArrayList<>();
        KeyWorkExtraColumnOption keyWorkExtraColumnOption;
        switch (columnKey) {
            case "KEY_WORK_TYPE":
                List<KeyWorkType> keyWorkTypes = keyWorkTypeService.getKeyWorkTypeList(ContextUtils.getUnitCode(),showDelete);
                for (KeyWorkType keyWorkType : keyWorkTypes) {
                    keyWorkExtraColumnOption = new KeyWorkExtraColumnOption();
                    keyWorkExtraColumnOption.setLabel(keyWorkType.getName());
                    keyWorkExtraColumnOption.setValue(keyWorkType.getId());
                    keyWorkExtraColumnOption.setIsDelete(FlowUtil.stringToBoolean(keyWorkType.getDeleted(),true));
                    keyWorkExtraColumnOptions.add(keyWorkExtraColumnOption);
                }
                break;
            case "WORK_ENV":
                List<Map<String, Object>> maps = Arrays.asList(
                    WorkEnvEnum.HavePower,
                    WorkEnvEnum.NotPower,
                    WorkEnvEnum.NoRequirement
                ).stream().map(EnumUtils::toMap).collect(Collectors.toList());
                for (Map<String, Object> map : maps) {
                    keyWorkExtraColumnOption = new KeyWorkExtraColumnOption();
                    keyWorkExtraColumnOption.setLabel(map.get("label").toString());
                    keyWorkExtraColumnOption.setValue(map.get("value").toString());
                    keyWorkExtraColumnOptions.add(keyWorkExtraColumnOption);
                }
                break;
            case "CAR":
                List<String> carNoList = TrainsetUtils.generateCarNoListFromMarshalCount(
                    remoteService.getMarshalCountByTrainType(trainModel)
                );
                for (String carNo : carNoList) {
                    keyWorkExtraColumnOption = new KeyWorkExtraColumnOption();
                    keyWorkExtraColumnOption.setLabel(carNo);
                    keyWorkExtraColumnOption.setValue(carNo);
                    keyWorkExtraColumnOptions.add(keyWorkExtraColumnOption);
                }
                break;
        }
        return keyWorkExtraColumnOptions;
    }

    @Override
    public KeyWorkConfig getKeyWorkConfig(String trainModel, String content) {
        KeyWorkConfig keyWorkConfig = new KeyWorkConfig();
        keyWorkConfig.setTrainModel(trainModel);
        keyWorkConfig.setContent(content);
        keyWorkConfig.setUnitCode(ContextUtils.getUnitCode());
        return keyWorkConfigMapper.selectOne(keyWorkConfig);
    }
}
