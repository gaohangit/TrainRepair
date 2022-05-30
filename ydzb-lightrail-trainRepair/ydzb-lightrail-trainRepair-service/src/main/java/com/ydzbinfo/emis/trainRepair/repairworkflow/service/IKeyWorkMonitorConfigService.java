package com.ydzbinfo.emis.trainRepair.repairworkflow.service;

import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.KeyWorkMonitorConfig;

import java.util.List;

/**
 * <p>
 * 关键作业大屏配置 服务类
 * </p>
 *
 * @author 张天可
 * @since 2021-08-09
 */
public interface IKeyWorkMonitorConfigService extends IService<KeyWorkMonitorConfig> {

    List<KeyWorkMonitorConfig> getKeyWorkMonitorConfigs();

    void setKeyWorkMonitorConfigs(List<KeyWorkMonitorConfig> keyWorkMonitorConfigs);


}
