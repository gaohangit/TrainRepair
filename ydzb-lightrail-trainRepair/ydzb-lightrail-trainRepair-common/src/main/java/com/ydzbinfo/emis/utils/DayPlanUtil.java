package com.ydzbinfo.emis.utils;

import com.ydzbinfo.emis.trainRepair.common.dao.ConfigMapper;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.Config;
import com.ydzbinfo.emis.trainRepair.workprocess.model.WorkTime;
import com.ydzbinfo.emis.utils.mybatisplus.param.LogicalLinkable;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.*;

/**
 * 日计划相关的工具方法
 *
 * @author 张天可
 * @since 2022/1/10
 */
@Component
public class DayPlanUtil {
    private static ConfigMapper configMapper;
    private static final String dayPlanIdRegStr = "^\\d{4}-\\d{2}-\\d{2}-0[01]$";

    public DayPlanUtil(ConfigMapper configMapper) {
        DayPlanUtil.configMapper = configMapper;
    }

    @Data
    public static class DayPlanConfig {
        private LocalTime beginDayTime;
        private LocalTime beginNightTime;
    }

    private static DayPlanConfig getDayPlanConfigInner(String unitCode) {
        String beginDayTimeConfigName = "BeginDayTime";
        String beginNightTimeConfigName = "BeginNightTime";
        List<LogicalLinkable<Config>> params = new ArrayList<>();
        params.add(
            inParam(
                Config::getParamName,
                Arrays.asList(
                    beginDayTimeConfigName,
                    beginNightTimeConfigName
                )
            )
        );
        if (unitCode != null) {
            params.add(
                anyMatch(
                    eqParam(Config::getUnitCode, unitCode),
                    eqParam(Config::getUnitCode, null)
                )
            );
        }
        List<Config> configs = selectList(configMapper, params);
        Map<String, List<Config>> configMap = configs.stream().collect(
            Collectors.groupingBy(Config::getParamName)
        );
        if (!configMap.containsKey(beginDayTimeConfigName)) {
            throw new RuntimeException("缺少配置：" + beginDayTimeConfigName);
        }
        if (!configMap.containsKey(beginNightTimeConfigName)) {
            throw new RuntimeException("缺少配置：" + beginNightTimeConfigName);
        }
        Function<String, String> getConfigValue = (configName) -> {
            List<Config> l = configMap.get(configName);
            if (l.size() == 1) {
                return l.get(0).getParamValue();
            } else {
                Config targetConfig = CommonUtils.find(l, v -> Objects.equals(unitCode, v.getUnitCode()));
                if (targetConfig != null) {
                    return targetConfig.getParamValue();
                } else {
                    return l.get(0).getParamValue();
                }
            }
        };
        String beginDayTimeConfigValue = getConfigValue.apply(beginDayTimeConfigName);
        String beginNightTimeConfigValue = getConfigValue.apply(beginNightTimeConfigName);
        LocalTime beginDayTime;
        LocalTime beginNightTime;
        try {
            beginDayTime = DateTimeUtil.parseLocalTime(beginDayTimeConfigValue);
            beginNightTime = DateTimeUtil.parseLocalTime(beginNightTimeConfigValue);
        } catch (Exception e) {
            throw new RuntimeException("解析配置错误：" + beginDayTimeConfigName + ":" + beginDayTimeConfigValue + "，" + beginNightTimeConfigName + ":" + beginNightTimeConfigValue, e);
        }
        if (beginDayTime.isAfter(beginNightTime)) {
            throw new RuntimeException("配置错误：" + beginDayTimeConfigName + ":" + beginDayTimeConfigValue + " 不能晚于" + beginNightTimeConfigName + ":" + beginNightTimeConfigValue);
        }
        DayPlanConfig dayPlanConfig = new DayPlanConfig();
        dayPlanConfig.setBeginDayTime(beginDayTime);
        dayPlanConfig.setBeginNightTime(beginNightTime);
        return dayPlanConfig;
    }

    public static DayPlanConfig getDayPlanConfig(String unitCode) {
        return CacheUtil.getDataUseThreadCache(
            "DayPlanUtil.getDayPlanConfig_" + unitCode,
            () -> getDayPlanConfigInner(unitCode)
        );
    }

    private static String getDayPlanIdByConfig(Date dateTime, DayPlanConfig dayPlanConfig) {
        LocalTime beginDayTime = dayPlanConfig.getBeginDayTime();
        LocalTime beginNightTime = dayPlanConfig.getBeginNightTime();

        LocalDate today = DateTimeUtil.asLocalDate(dateTime);
        LocalTime currentTime = DateTimeUtil.asLocalTime(dateTime);
        if (currentTime.isBefore(beginDayTime)) {
            return DateTimeUtil.formatLocalDate(today.minusDays(1)) + "-01";
        } else if (currentTime.isBefore(beginNightTime)) {
            return DateTimeUtil.formatLocalDate(today) + "-00";
        } else {
            return DateTimeUtil.formatLocalDate(today) + "-01";
        }
    }

    public static String getDayPlanId(String unitCode, Date dateTime) {
        DayPlanConfig dayPlanConfig = getDayPlanConfig(unitCode);
        return getDayPlanIdByConfig(dateTime, dayPlanConfig);
    }

    public static String getDayPlanId(Date dateTime) {
        return getDayPlanId(ContextUtils.getUnitCode(), dateTime);
    }

    public static String getDayPlanId(String unitCode) {
        return getDayPlanId(unitCode, new Date());
    }

    public static String getDayPlanId() {
        return getDayPlanId(ContextUtils.getUnitCode(), new Date());
    }

    public static WorkTime getWorkTimeByDayPlanId(String dayPlanId) {
        if (!dayPlanId.matches(dayPlanIdRegStr)) {
            throw new RuntimeException("日计划id格式错误：" + dayPlanId);
        }
        String dateString = dayPlanId.substring(0, 10);
        String dayOrNightString = dayPlanId.substring(11);

        DayPlanConfig dayPlanConfig = getDayPlanConfig(ContextUtils.getUnitCode());
        WorkTime workTime = new WorkTime();
        LocalDate date = DateTimeUtil.parseLocalDate(dateString);
        if (dayOrNightString.equals("00")) {
            LocalTime beginTime = dayPlanConfig.getBeginDayTime();
            workTime.setStartTime(DateTimeUtil.asDate(date.atTime(beginTime)));

            LocalTime endTime = dayPlanConfig.getBeginNightTime().minusMinutes(1);
            workTime.setEndTime(DateTimeUtil.asDate(date.atTime(endTime)));
        } else {
            LocalTime beginTime = dayPlanConfig.getBeginNightTime();
            workTime.setStartTime(DateTimeUtil.asDate(date.atTime(beginTime)));

            // 夜班结束时间是下一天白班开始前一分钟
            LocalTime endTime = dayPlanConfig.getBeginDayTime().minusMinutes(1);
            workTime.setEndTime(DateTimeUtil.asDate(date.plusDays(1).atTime(endTime)));
        }
        return workTime;
    }

}
