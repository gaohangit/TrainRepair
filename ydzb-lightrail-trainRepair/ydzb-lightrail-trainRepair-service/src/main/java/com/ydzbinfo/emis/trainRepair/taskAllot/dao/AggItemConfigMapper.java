package com.ydzbinfo.emis.trainRepair.taskAllot.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.AggItemConfigModel;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.AggItemConfig;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @since 2021-03-17
 */
public interface AggItemConfigMapper extends BaseMapper<AggItemConfig> {
    List<AggItemConfigModel> getAggItemConfigList( AggItemConfigModel aggItemConfig);

}
