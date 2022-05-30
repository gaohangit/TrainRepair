package com.ydzbinfo.emis.trainRepair.repairworkflow.dao;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.FlowMarkConfig;

/**
 * <p>
 * 流程标记配置表，目前仅支持临修作业的关键字配置 Mapper 接口
 * </p>
 *
 * @author gaohan
 * @since 2021-04-30
 */
public interface FlowMarkConfigMapper extends BaseMapper<FlowMarkConfig> {

}
