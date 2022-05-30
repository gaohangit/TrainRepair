package com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend;

import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.Condition;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.ConditionValue;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author gaohan
 * @description 条件信息
 * @createDate 2021/4/9 14:45
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class ConditionWithValues extends Condition {
    List<ConditionValue> conditionValues;

}
