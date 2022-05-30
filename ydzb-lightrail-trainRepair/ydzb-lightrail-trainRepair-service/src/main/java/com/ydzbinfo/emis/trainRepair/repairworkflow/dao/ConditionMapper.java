package com.ydzbinfo.emis.trainRepair.repairworkflow.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.Condition;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend.ConditionWithValues;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 条件表 Mapper 接口
 * </p>
 *
 * @author gaohan
 * @since 2021-04-09
 */
public interface ConditionMapper extends BaseMapper<Condition> {
    /**
     * 查询条件
     */
    List<ConditionWithValues> getConditionInfo(@Param("valueList") List<String> valueList);

}
