package com.ydzbinfo.emis.trainRepair.taskAllot.service;

import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.AggItemConfigItem;
import org.apache.ibatis.annotations.Param;


/**
 * <p>
 *  服务类
 * </p>
 *
 * @author gaohan
 * @since 2021-03-17
 */
public interface IAggItemConfigItemService extends IService<AggItemConfigItem> {

    void delAggItemConfigItem(@Param("id") String id);


}
