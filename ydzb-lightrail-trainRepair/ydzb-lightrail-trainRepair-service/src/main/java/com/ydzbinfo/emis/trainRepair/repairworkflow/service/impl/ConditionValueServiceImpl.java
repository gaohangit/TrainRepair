package com.ydzbinfo.emis.trainRepair.repairworkflow.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.trainRepair.repairworkflow.dao.ConditionValueMapper;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.ConditionValue;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.IConditionValueService;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.eqParam;

/**
 * <p>
 * 条件值表 服务实现类
 * </p>
 *
 * @author gaohan
 * @since 2021-04-09
 */
@Service
public class ConditionValueServiceImpl extends ServiceImpl<ConditionValueMapper, ConditionValue> implements IConditionValueService {
    @Resource
    ConditionValueMapper conditionValueMapper;

    @Override
    public void addConditionValue(ConditionValue conditionValue) {
        conditionValueMapper.insert(conditionValue);
    }

    @Override
    public void delConditionValue(String id) {
        MybatisPlusUtils.delete(
            conditionValueMapper,
            eqParam(ConditionValue::getConditionId, id)
        );
    }
}
