package com.ydzbinfo.emis.trainRepair.trainMonitor.service.imp;

import com.ydzbinfo.emis.trainRepair.common.dao.ConfigMapper;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.Config;
import com.ydzbinfo.emis.trainRepair.trainMonitor.service.ConfigService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author gaohan
 * @description
 * @createDate 2021/3/3 9:37
 **/
@Service
public class ConfigServiceImpl implements ConfigService {
    @Resource
    ConfigMapper configMapper;

    @Override
    public List<Config> getConfigList() {
        return configMapper.getConfigList();
    }

    @Override
    public Config getConfig(Config config) {
        return configMapper.getConfig(config);
    }

    @Override
    public int addConfig(Config config) {
        return configMapper.addConfig(config);
    }

    @Override
    public int updConfig(Config config) {
        return configMapper.updConfig(config);
    }

    @Override
    public Config getUploadMax(String pramName, String pramValue) {
        Config config = new Config();
        config.setParamName(pramName);
        Config pictureUploadMax = this.getConfig(config);
        if (pictureUploadMax == null) {
            config.setParamName(pramName);
            config.setParamValue(pramValue);
            this.addConfig(config);
            return config;
        }
        return pictureUploadMax;
    }
}
