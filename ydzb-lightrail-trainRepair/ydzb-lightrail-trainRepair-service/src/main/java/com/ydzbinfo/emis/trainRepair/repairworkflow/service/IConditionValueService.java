package com.ydzbinfo.emis.trainRepair.repairworkflow.service;

import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.ConditionValue;


/**
 * <p>
 * 条件值表 服务类
 * </p>
 *
 * @author gaohan
 * @since 2021-04-09
 */
public interface IConditionValueService extends IService<ConditionValue> {
    /**
     * 新增
     */
    void addConditionValue(ConditionValue conditionValue);
    /**
     * 删除
     */
    void delConditionValue(String id);

}
