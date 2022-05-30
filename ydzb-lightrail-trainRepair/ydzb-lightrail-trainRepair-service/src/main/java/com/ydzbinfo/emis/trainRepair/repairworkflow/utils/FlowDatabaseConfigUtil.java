package com.ydzbinfo.emis.trainRepair.repairworkflow.utils;

import com.ydzbinfo.emis.trainRepair.common.dao.ConfigMapper;
import com.ydzbinfo.emis.trainRepair.common.util.ConfigUtil;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.Config;
import com.ydzbinfo.emis.utils.CacheUtil;
import com.ydzbinfo.emis.utils.EnumUtils;
import com.ydzbinfo.emis.utils.result.RestRequestException;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 流程相关功能配置工具类
 *
 * @author 张天可
 * @since 2022/1/17
 */
@Component
public class FlowDatabaseConfigUtil extends ConfigUtil {

    public FlowDatabaseConfigUtil(ConfigMapper configMapper) {
        super(configMapper);
    }

    private static List<Config> getFlowConfigsByName(String name) {
        return CacheUtil.getDataUseThreadCache(
            "FlowDatabaseConfigUtil.getFlowConfigsByName_" + name,
            () -> MybatisPlusUtils.selectList(configMapper,
                MybatisPlusUtils.inParam(Config::getType, Arrays.asList("FLOW", "KEYWORK")),
                MybatisPlusUtils.eqParam(Config::getParamName, name)
            )
        );
    }
    /**
     * 是否根据派工查看作业人员和岗位
     */
    public static boolean isPersonPostByTask() {
        return getBooleanConfig("PERSON_POST_TASK");
    }
    /**
     * 是否使用基本整备任务类型
     */
    public static boolean isUseDefaultHostlingFlowType() {
        return getBooleanConfig("FLOW_TYPE_USE_DEFAULT_HOSTLING");
    }

    /**
     * 是否在关键作业录入时，校验录入车组是否在股道上
     */
    public static boolean isVerifyTrackByTrainSet() {
        return getBooleanConfig("VerifyTrackByTrainSet");
    }

    /**
     * 获取额外页面类型配置，
     * 其中key为额外页面类型编码
     * value为额外页面类型下包含的流程类型
     */
    public static Map<String, Set<String>> getAllExtraFlowPages() {
        Map<String, Set<String>> extraFlowPages = new HashMap<>();
        List<Config> configList = getFlowConfigsByName("EXTRA_PAGE_CODE");
        for (Config con : configList) {
            String[] splits = con.getParamValue().split(",");
            String flowPageCode = splits[0];
            String flowTypeCode = splits[1];
            if (!extraFlowPages.containsKey(flowPageCode)) {
                extraFlowPages.put(flowPageCode, new HashSet<>());
            }
            extraFlowPages.get(flowPageCode).add(flowTypeCode);
        }
        return extraFlowPages;
    }

    /**
     * 获取流程额外页面类型对应的流程类型列表
     */
    public static Set<String> getExtraFlowPageFlowTypes(String extraFlowPageCode) {
        Set<String> flowTypes = getAllExtraFlowPages().get(extraFlowPageCode);
        if (flowTypes == null) {
            throw RestRequestException.fatalFail("缺少额外页面类型配置：EXTRA_PAGE_CODE，需求的额外页面类型：" + extraFlowPageCode);
        } else {
            return flowTypes;
        }
    }

    /**
     * 获取关键作业可显示的列
     */
    public static List<KeyWorkExtraColumnEnum> getKeyWorkExtraColumns() {
        List<Config> columnConfigs = getFlowConfigsByName("KEY_WORK_COLUMN");
        return columnConfigs.stream()
            .map(v -> v.getParamValue().split(","))// 逗号分割，左侧为列编码，右侧为排序值
            .sorted(Comparator.comparingInt(v -> Integer.parseInt(v[1])))// 根据排序值排序
            .map(v -> EnumUtils.findEnum(KeyWorkExtraColumnEnum.class, KeyWorkExtraColumnEnum::getKey, v[0]))// 只取列编码，并转换为枚举值
            .filter(Objects::nonNull)// 过滤掉为null的
            .collect(Collectors.toList());
    }

    /**
     * 获取可用的流程类型
     */
    public static Set<String> getAllowFlowTypes() {
        List<Config> columnConfigs = getFlowConfigsByName("FLOW_TYPE");
        return columnConfigs.stream()
            .map(Config::getParamValue)
            .collect(Collectors.toSet());
    }

    /**
     * 关键作业大屏是否显示驳回流程
     */
    public static boolean isShowForceEndFlowRun() {
        return getBooleanConfig("FlowRunForceEnd");
    }

    /**
     * 关键作业大屏显示最近几个日计划的关键作业
     */
    public static int getKeyWorkMonitorShowDayPlanCount() {
        String paramName = "MonitorShowDataInfo";
        List<Config> configs = getFlowConfigsByName(paramName);
        if (configs.size() > 0) {
            return Integer.parseInt(configs.get(0).getParamValue());
        } else {
            throw getFatalRestRequestExceptionByParamName(paramName);
        }
    }

}
