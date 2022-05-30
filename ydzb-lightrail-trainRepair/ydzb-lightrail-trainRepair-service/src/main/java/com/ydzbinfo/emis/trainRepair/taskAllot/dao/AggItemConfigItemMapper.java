package com.ydzbinfo.emis.trainRepair.taskAllot.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.AggItemConfigItem;
import org.apache.ibatis.annotations.Param;


/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @since 2021-03-17
 */
public interface AggItemConfigItemMapper extends BaseMapper<AggItemConfigItem> {
    void delAggItemConfigItem(@Param("id") String id);

}
