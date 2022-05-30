package com.ydzbinfo.emis.trainRepair.repairworkflow.service.impl;


import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.jxdinfo.hussar.core.mq.MessageUtil;
import com.jxdinfo.hussar.core.mq.MessageWrapper;
import com.ydzbinfo.emis.configs.kafka.repairworkflow.RepairWorkflowConfigMqSource;
import com.ydzbinfo.emis.trainRepair.repairworkflow.constant.KeyWorkConfigHeaderConstant;
import com.ydzbinfo.emis.trainRepair.repairworkflow.dao.KeyWorkTypeMapper;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.KeyWorkType;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.IKeyWorkTypeService;
import com.ydzbinfo.emis.trainRepair.repairworkflow.utils.FlowUtil;
import com.ydzbinfo.emis.utils.ContextUtils;
import com.ydzbinfo.emis.utils.SpringCloudStreamUtil;
import com.ydzbinfo.emis.utils.StringUtils;
import com.ydzbinfo.emis.utils.UserUtil;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import com.ydzbinfo.emis.utils.mybatisplus.param.ColumnParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.eqParam;

/**
 * <p>
 * 关键作业类型表 服务实现类
 * </p>
 *
 * @author 高晗
 * @since 2021-06-18
 */
@Transactional
@Service
public class KeyWorkTypeServiceImpl extends ServiceImpl<KeyWorkTypeMapper, KeyWorkType> implements IKeyWorkTypeService {
    @Autowired
    KeyWorkTypeMapper keyWorkTypeMapper;

    @Autowired(required = false)
    private RepairWorkflowConfigMqSource repairWorkflowConfigSource;

    @Override
    public void addKeyWorkType(KeyWorkType keyWorkType) {
        keyWorkType.setCreateTime(new Date());
        if (StringUtils.isBlank(keyWorkType.getCreateWorkerId())) {
            keyWorkType.setCreateWorkerId(UserUtil.getUserInfo().getStaffId());
        }
        if (StringUtils.isBlank(keyWorkType.getCreateWorkerName())) {
            keyWorkType.setCreateWorkerName(UserUtil.getUserInfo().getName());
        }
        keyWorkType.setUnitCode(ContextUtils.getUnitCode());
        keyWorkType.setDeleted("0");
        keyWorkTypeMapper.insert(keyWorkType);
    }

    @Override
    public void delKeyWorkTypeById(KeyWorkType keyWorkType) {
        keyWorkType.setDeleted("1");
        keyWorkTypeMapper.updateById(keyWorkType);
    }

    @Override
    public void updKeyWorkType(KeyWorkType keyWorkType) {
        keyWorkType.setUpdateTime(new Date());
        if (StringUtils.isBlank(keyWorkType.getUpdateWorkerId())) {
            keyWorkType.setUpdateWorkerId(UserUtil.getUserInfo().getStaffId());
        }
        if (StringUtils.isBlank(keyWorkType.getUpdateWorkerName())) {
            keyWorkType.setUpdateWorkerName(UserUtil.getUserInfo().getName());
        }
        keyWorkType.setUnitCode(ContextUtils.getUnitCode());
        keyWorkTypeMapper.updateById(keyWorkType);
    }

    @Override
    public void setKeyWorkType(List<KeyWorkType> keyWorkTypes) {
        this.sendSetKeyWorkType(keyWorkTypes);
        //发送同步数据
        if (SpringCloudStreamUtil.enableSendCloudData(RepairWorkflowConfigMqSource.class)) {
            MessageWrapper<List<KeyWorkType>> messageWrapper = new MessageWrapper<>(KeyWorkConfigHeaderConstant.class, KeyWorkConfigHeaderConstant.SET_KEY_WORK_TYPE, keyWorkTypes);
            boolean sendFlag = MessageUtil.sendMessage(repairWorkflowConfigSource.keyWorkConfigChannel(), messageWrapper);
        }
    }

    @Override
    public void sendSetKeyWorkType(List<KeyWorkType> keyWorkTypes) {
        List<KeyWorkType> queryKeyWorkTypes = this.getKeyWorkTypeList(ContextUtils.getUnitCode(), false);
        List<String> queryIds = queryKeyWorkTypes.stream().map(v -> v.getId()).collect(Collectors.toList());
        List<String> names = keyWorkTypes.stream().map(v -> v.getName()).collect(Collectors.toList());
        names.forEach(name -> {
            if (names.stream().filter(v -> v.equals(name)).count() > 1) {
                throw new RuntimeException(name + " 出现重复的类型配置!");
            }
        });
        int sort = 1;
        for (KeyWorkType keyWorkType : keyWorkTypes) {
            keyWorkType.setSort(sort);
            String id = keyWorkType.getId();
            if (StringUtils.isNotBlank(id) && queryIds.contains(id)) {
                if (keyWorkType.getDeleted().equals("1")) {
                    if (StringUtils.isBlank(keyWorkType.getDeleteWorkerId())) {
                        keyWorkType.setDeleteWorkerId(UserUtil.getUserInfo().getStaffId());
                    }
                    if (StringUtils.isBlank(keyWorkType.getDeleteWorkerName())) {
                        keyWorkType.setDeleteWorkerName(UserUtil.getUserInfo().getName());
                    }
                    this.delKeyWorkTypeById(keyWorkType);
                } else {
                    if (StringUtils.isBlank(keyWorkType.getUpdateWorkerId())) {
                        keyWorkType.setUpdateWorkerId(UserUtil.getUserInfo().getStaffId());
                    }
                    if (StringUtils.isBlank(keyWorkType.getUpdateWorkerName())) {
                        keyWorkType.setUpdateWorkerName(UserUtil.getUserInfo().getName());
                    }
                    this.updKeyWorkType(keyWorkType);
                }
            } else {
                if (StringUtils.isBlank(keyWorkType.getCreateWorkerId())) {
                    keyWorkType.setCreateWorkerId(UserUtil.getUserInfo().getStaffId());
                }
                if (StringUtils.isBlank(keyWorkType.getCreateWorkerName())) {
                    keyWorkType.setCreateWorkerName(UserUtil.getUserInfo().getName());
                }
                this.addKeyWorkType(keyWorkType);
            }
            sort++;
        }
    }

    @Override
    public List<KeyWorkType> getKeyWorkTypeList(String unitCode, boolean showDelete) {
        List<ColumnParam<KeyWorkType>> columnParamList = new ArrayList<>();
        if (StringUtils.isNotBlank(unitCode)) {
            columnParamList.add(eqParam(KeyWorkType::getUnitCode, unitCode));
        }
        if(!showDelete){
            columnParamList.add(eqParam(KeyWorkType::getDeleted, FlowUtil.booleanToString(false)));
        }
        List<KeyWorkType> keyWorkTypes = MybatisPlusUtils.selectList(
            keyWorkTypeMapper,
            columnParamList
        );
        if (keyWorkTypes.stream().noneMatch(v -> v.getSort() == null)) {
            keyWorkTypes.sort(Comparator.comparing(KeyWorkType::getSort));
        }
        return keyWorkTypes;
    }

    @Override
    public KeyWorkType getKeyWorkType(String name) {
        KeyWorkType keyWorkType = new KeyWorkType();
        keyWorkType.setUnitCode(ContextUtils.getUnitCode());
        keyWorkType.setName(name);
        return keyWorkTypeMapper.selectOne(keyWorkType);
    }
}
