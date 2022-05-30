package com.ydzbinfo.emis.trainRepair.repairworkflow.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.FlowExtraInfo;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 流程额外信息表	如：是否为默认流程，流程选择聚合条件，切换人员聚合条件，触发人员聚合条件 Mapper 接口
 * </p>
 *
 * @author gaohan
 * @since 2021-04-02
 */
public interface FlowExtraInfoMapper extends BaseMapper<FlowExtraInfo> {
    void setDefaultType(@Param("flowId") String flowId,@Param("defaultType")String defaultType);

}
