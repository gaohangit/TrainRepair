package com.ydzbinfo.emis.trainRepair.taskAllot.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.AllotPersonConfig;


/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @since 2021-03-23
 */
public interface AllotPersonConfigMapper extends BaseMapper<AllotPersonConfig> {

    void updAllotPersonConfig(AllotPersonConfig allotPersonConfig);

}
