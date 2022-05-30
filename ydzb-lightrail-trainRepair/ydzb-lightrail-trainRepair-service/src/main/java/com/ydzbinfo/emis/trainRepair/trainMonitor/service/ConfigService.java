package com.ydzbinfo.emis.trainRepair.trainMonitor.service;

import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.Config;

import java.util.List;


/**
 * <p>
 *  服务类
 * </p>
 *
 * @author gaohan
 * @since 2021-03-03
 */
public interface ConfigService  {
    List<Config> getConfigList();

    Config getConfig(Config config);

    int addConfig(Config config);

    int updConfig(Config config);

    Config getUploadMax(String pramName,String pramValue);

}
