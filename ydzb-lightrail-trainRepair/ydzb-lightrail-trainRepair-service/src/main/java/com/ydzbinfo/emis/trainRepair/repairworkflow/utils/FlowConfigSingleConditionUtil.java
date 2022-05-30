package com.ydzbinfo.emis.trainRepair.repairworkflow.utils;

import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetBaseInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.TrainConditionValue;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author 张天可
 * @description
 * @createDate 2021/6/2 14:19
 **/
public class FlowConfigSingleConditionUtil {

    /**
     * 判断车组条件是否为排除条件
     *
     * @param value
     * @return
     */
    public static boolean getIsExcludeCondition(TrainConditionValue value) {
        return Arrays.stream(TrainInfoConditionLevelEnum.values()).anyMatch(v -> v.checkIsExclude(value));
    }

    /**
     * 根据指定车组条件过滤出可能的车组ID列表
     *
     * @param trainsetBaseInfos
     * @param value
     * @return
     */
    public static Set<String> getFilteredTrainsetIds(List<TrainsetBaseInfo> trainsetBaseInfos, TrainConditionValue value) {
        return filterByTrainConditionValue(trainsetBaseInfos, value).stream()
            .map(TrainsetBaseInfo::getTrainsetid)
            .collect(Collectors.toSet());
    }

    /**
     * 根据指定车组条件过滤出可能的车组信息列表
     *
     * @param trainsetBaseInfos
     * @param value
     * @return
     */
    public static List<TrainsetBaseInfo> filterByTrainConditionValue(List<TrainsetBaseInfo> trainsetBaseInfos, TrainConditionValue value) {
        Stream<TrainsetBaseInfo> trainsetBaseInfoStream = trainsetBaseInfos.parallelStream();
        for (TrainInfoConditionLevelEnum levelEnum : TrainInfoConditionLevelEnum.values()) {
            if (levelEnum != TrainInfoConditionLevelEnum.NONE) {
                trainsetBaseInfoStream = trainsetBaseInfoStream.filter(levelEnum.getTrainsetFilter(value));
            }
        }
        return trainsetBaseInfoStream.collect(Collectors.toList());
    }

    /**
     * 获取车组条件的最小层级
     *
     * @param value
     * @return
     */
    public static TrainInfoConditionLevelEnum getMinLevel(TrainConditionValue value) {
        for (TrainInfoConditionLevelEnum trainInfoConditionLevelEnum : TrainInfoConditionLevelEnum.values()) {
            if (trainInfoConditionLevelEnum.hasValue(value)) {
                return trainInfoConditionLevelEnum;
            }
        }
        throw new RuntimeException("TrainInfoMinLevelEnum定义异常");
    }

    public static String getErrorMessage(Set<String> checkValues, Set<String> existValues, String existFlowInfoName, String conditionName) {
        Set<String> intersectionValues = checkValues.stream().filter(existValues::contains).collect(Collectors.toSet());
        return "当前流程条件与[" + existFlowInfoName + "]流程在" + conditionName + "上存在非法交集：" + intersectionValues + "，容易导致匹配流程的混乱。如果想在其他条件一致的情况下，针对" + intersectionValues + "配置多个流程，请将" + intersectionValues + "从" + existFlowInfoName + "流程和当前流程中排除，并使用复制功能单独配置" + intersectionValues + "的多个流程。如果想配置备用流程，需要与默认流程具有完全相同的条件。";
    }
}
