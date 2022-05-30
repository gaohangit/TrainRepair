package com.ydzbinfo.emis.trainRepair.repairworkflow.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.trainRepair.repairworkflow.dao.ConditionMapper;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.Condition;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend.ConditionWithValues;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.IConditionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 条件表 服务实现类
 * </p>
 *
 * @author gaohan
 * @since 2021-04-09
 */
@Service
public class ConditionServiceImpl extends ServiceImpl<ConditionMapper, Condition> implements IConditionService {
    @Resource
    ConditionMapper conditionMapper;
    @Override
    public List<ConditionWithValues> getConditionWithValues(List<String> valueList) {
        return conditionMapper.getConditionInfo(valueList);
    }

}
