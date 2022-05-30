package com.ydzbinfo.emis.trainRepair.common.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.Config;

import java.util.List;


/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author gaohan
 * @since 2021-03-03
 */
public interface ConfigMapper extends BaseMapper<Config> {
    List<Config> getConfigList();

    Config getConfig(Config config);

    int addConfig(Config config);

    int updConfig(Config config);

}
