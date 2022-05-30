package com.ydzbinfo.emis.trainRepair.taskAllot.service;

import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.AllotPersonConfig;


/**
 * <p>
 *  服务类
 * </p>
 *
 * @since 2021-03-23
 */
public interface IAllotPersonConfigService extends IService<AllotPersonConfig>  {
    void updAllotPersonConfig(AllotPersonConfig allotPersonConfig);



}
