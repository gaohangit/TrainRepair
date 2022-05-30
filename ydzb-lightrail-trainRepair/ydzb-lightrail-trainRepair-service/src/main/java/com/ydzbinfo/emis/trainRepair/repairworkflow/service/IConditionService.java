package com.ydzbinfo.emis.trainRepair.repairworkflow.service;

import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.Condition;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend.ConditionWithValues;

import java.util.List;

/**
 * <p>
 * 条件表 服务类
 * </p>
 *
 * @author gaohan
 * @since 2021-04-09
 */
public interface IConditionService extends IService<Condition> {
    /**
     * 查询条件
     */
    List<ConditionWithValues> getConditionWithValues(List<String> valueList);

}
