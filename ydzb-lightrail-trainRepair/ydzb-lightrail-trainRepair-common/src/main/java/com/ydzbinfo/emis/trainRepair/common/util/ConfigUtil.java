package com.ydzbinfo.emis.trainRepair.common.util;

import com.ydzbinfo.emis.trainRepair.common.dao.ConfigMapper;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.Config;
import com.ydzbinfo.emis.utils.CacheUtil;
import com.ydzbinfo.emis.utils.result.RestRequestException;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 全局配置工具类(仅各个模块通用的配置放到这里面来，模块内的特殊配置需要各自定义)
 *
 * @author 张天可
 * @since 2022/1/17
 */
@Component
public class ConfigUtil {

    protected static ConfigMapper configMapper;

    public ConfigUtil(ConfigMapper configMapper) {
        ConfigUtil.configMapper = configMapper;
    }

    protected static List<Config> getConfigsByName(String name) {
        return CacheUtil.getDataUseThreadCache(
            "ConfigUtil.getConfigsByName_" + name,
            () -> MybatisPlusUtils.selectList(configMapper, MybatisPlusUtils.eqParam(Config::getParamName, name))
        );
    }

    protected static RestRequestException getFatalRestRequestExceptionByParamName(String paramName) {
        return RestRequestException.fatalFail("缺少配置：" + paramName);
    }

    protected static boolean getBooleanConfig(String paramName) {
        List<Config> configs = getConfigsByName(paramName);
        if (configs.size() > 0) {
            return "1".equals(configs.get(0).getParamValue());
        } else {
            throw getFatalRestRequestExceptionByParamName(paramName);
        }
    }

    /**
     * 是否使用新项目
     */
    public static boolean isUseNewItem() {
        String paramName = "UseNewItem";
        return getBooleanConfig(paramName);
    }

    /**
     * 大屏股道列位显示方式
     * 1--使用TRACKPOSTIONNAME
     * 2--使用DIRECTIONCODE
     */
    public static String getMonitorTrackPlaceShowType() {
        String paramName = "MonitorTrackPlaceShow";
        List<Config> configs = getConfigsByName(paramName);
        if (configs.size() > 0) {
            return configs.get(0).getParamValue();
        } else {
            throw getFatalRestRequestExceptionByParamName(paramName);
        }
    }

}
