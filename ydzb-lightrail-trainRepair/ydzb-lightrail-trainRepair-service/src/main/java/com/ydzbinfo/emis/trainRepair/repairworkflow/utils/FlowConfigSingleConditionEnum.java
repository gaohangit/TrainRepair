package com.ydzbinfo.emis.trainRepair.repairworkflow.utils;

import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetBaseInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.FlowInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.TrainConditionValue;
import com.ydzbinfo.emis.utils.CacheUtil;
import com.ydzbinfo.emis.utils.CommonUtils;
import com.ydzbinfo.emis.utils.RemoteServiceCachedDataUtil;
import com.ydzbinfo.emis.utils.entity.ReflectUtil;
import com.ydzbinfo.emis.utils.result.RestRequestException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ComparatorUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


public enum FlowConfigSingleConditionEnum {
    TRAIN(TrainCondition.class),
    KEY_WORDS(KeyWordsCondition.class),
    REPAIR_TYPE(RepairTypeCondition.class);

    FlowConfigSingleConditionBase<?> condition;

    FlowConfigSingleConditionEnum(Class<? extends FlowConfigSingleConditionBase<?>> flowConfigConditionClass) {
        try {
            this.condition = flowConfigConditionClass.getDeclaredConstructor(FlowConfigSingleConditionEnum.class).newInstance(this);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw RestRequestException.serviceInnerFatalFail("FlowConfigSingleConditionEnum初始化失败", e);
        }
    }

    public static class TrainCondition extends FlowConfigSingleConditionBase<TrainConditionValue> {
        private static FlowConfigSingleConditionEnum conditionEnum;
        
        TrainCondition(FlowConfigSingleConditionEnum conditionEnum) {
            TrainCondition.conditionEnum = conditionEnum;
        }

        public static TrainCondition getInstance() {
            return (TrainCondition) conditionEnum.getCondition();
        }

        private void checkValueRange(TrainConditionValue checkValue, List<TrainsetBaseInfo> allTrainsetBaseInfos) {
            TrainInfoConditionLevelEnum highestNotEmptyLevel = CommonUtils.find(TrainInfoConditionLevelEnum.getIterableToMin(TrainInfoConditionLevelEnum.TRAIN_TYPE_LEVEL), v -> v.hasValue(checkValue));
            assert highestNotEmptyLevel != null;
            List<TrainsetBaseInfo> curTrainsetBaseInfos = allTrainsetBaseInfos;
            TrainInfoConditionLevelEnum lastNotEmptyLevel = null;
            for (TrainInfoConditionLevelEnum levelEnum : TrainInfoConditionLevelEnum.getIterableToMin(highestNotEmptyLevel)) {
                if (levelEnum.hasValue(checkValue)) {
                    lastNotEmptyLevel = levelEnum;
                    curTrainsetBaseInfos = curTrainsetBaseInfos.stream().filter(levelEnum.getTrainsetFilter(checkValue)).collect(Collectors.toList());
                }

                TrainInfoConditionLevelEnum lowerLevelEnum = TrainInfoConditionLevelEnum.getLower(levelEnum);
                if (lowerLevelEnum != null) {
                    Set<String> lowerLevelValues = lowerLevelEnum.getValue(checkValue);
                    Set<String> lowerLevelTrainsetInfoValues = curTrainsetBaseInfos.stream().map(lowerLevelEnum::getValueFromTrainsetInfo).collect(Collectors.toSet());
                    // 只要存在一个批次不在之前车型条件过滤出的车组列表里，则返回错误
                    if (lowerLevelValues.stream().anyMatch(v -> !lowerLevelTrainsetInfoValues.contains(v))) {
                        Function<TrainInfoConditionLevelEnum, String> conditionTypeGetter = (trainInfoConditionLevelEnum) -> trainInfoConditionLevelEnum.checkIsExclude(checkValue) ? "排除" : "包含";
                        Function<TrainInfoConditionLevelEnum, Set<String>> valuesGetter = (trainInfoConditionLevelEnum) -> {
                            if (trainInfoConditionLevelEnum == TrainInfoConditionLevelEnum.TRAINSET_LEVEL) {
                                return toTrainsetNames(trainInfoConditionLevelEnum.getValue(checkValue));
                            } else {
                                return trainInfoConditionLevelEnum.getValue(checkValue);
                            }
                        };
                        assert lastNotEmptyLevel != null;
                        throw new RuntimeException("在满足" +
                            lastNotEmptyLevel.getConditionName() +
                            "(" + conditionTypeGetter.apply(lastNotEmptyLevel) + "): 【" +
                            String.join("，", valuesGetter.apply(lastNotEmptyLevel)) +
                            "】的前提下，无法设置" + lowerLevelEnum.getConditionName() +
                            "(" + conditionTypeGetter.apply(lowerLevelEnum) + "): 【" +
                            String.join("，", valuesGetter.apply(lowerLevelEnum)) + "】"
                        );
                    }
                }
            }
        }

        private List<String> getValues(FlowInfo flowInfo, TrainInfoConditionLevelEnum levelEnum) {
            List<String> values;
            switch (levelEnum) {
                case TRAINSET_LEVEL:
                    values = flowInfo.getTrainsetIds();
                    break;
                case TRAIN_TEMPLATE_LEVEL:
                    values = flowInfo.getTrainTemplates();
                    break;
                case TRAIN_TYPE_LEVEL:
                    values = flowInfo.getTrainTypes();
                    break;
                case NONE:
                    values = new ArrayList<>(Collections.singletonList("ALL"));
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + levelEnum);
            }
            return values;
        }

        private void setValues(FlowInfo flowInfo, TrainInfoConditionLevelEnum levelEnum, List<String> values) {
            switch (levelEnum) {
                case TRAINSET_LEVEL:
                    flowInfo.setTrainsetIds(values);
                    break;
                case TRAIN_TEMPLATE_LEVEL:
                    flowInfo.setTrainTemplates(values);
                    break;
                case TRAIN_TYPE_LEVEL:
                    flowInfo.setTrainTypes(values);
                    break;
            }
        }

        @Override
        public void checkValue(FlowInfo flowInfo) {
            TrainConditionValue checkValue = this.from(flowInfo);
            // 找到最小的不为空的包含条件，此前的条件(大于当前级别的条件)均不能为空
            TrainInfoConditionLevelEnum minIncludeNotNullLevel = CommonUtils.find(TrainInfoConditionLevelEnum.values(), v -> !v.checkIsExclude(checkValue) && v.hasValue(checkValue));
            if (minIncludeNotNullLevel != null) {
                for (TrainInfoConditionLevelEnum curLevel : TrainInfoConditionLevelEnum.getIterableToMax(TrainInfoConditionLevelEnum.getHigher(minIncludeNotNullLevel))) {
                    if (!curLevel.hasValue(checkValue)) {
                        throw new RuntimeException("当" + minIncludeNotNullLevel.getConditionName() + "(包含逻辑)不为空时，" + curLevel.getConditionName() + "不能为空。");
                    }
                }
            }

            if (Arrays.stream(TrainInfoConditionLevelEnum.values()).anyMatch(v -> !v.hasValue(checkValue) && v.checkIsExclude(checkValue))) {
                throw new RuntimeException("排除逻辑条件不能为空");
            }

            boolean isExcludeCondition = FlowConfigSingleConditionUtil.getIsExcludeCondition(checkValue);
            if (isExcludeCondition) {
                // 获取最大级别的排除条件，此后的条件(小于当前级别的条件)均不能为非空的包含条件
                TrainInfoConditionLevelEnum startExcludeLevel = CommonUtils.find(TrainInfoConditionLevelEnum.getIterableToMin(TrainInfoConditionLevelEnum.TRAIN_TYPE_LEVEL), v -> v.checkIsExclude(checkValue));
                assert startExcludeLevel != null;
                TrainInfoConditionLevelEnum curExcludeLevel = TrainInfoConditionLevelEnum.getLower(startExcludeLevel);
                while (curExcludeLevel != null) {
                    if (curExcludeLevel.hasValue(checkValue) && !curExcludeLevel.checkIsExclude(checkValue)) {
                        throw new RuntimeException("当" + startExcludeLevel.getConditionName() + "为排除条件时，" + curExcludeLevel.getConditionName() + "只能为空条件或排除条件。");
                    }
                    curExcludeLevel = TrainInfoConditionLevelEnum.getLower(curExcludeLevel);
                }
            }
            List<TrainsetBaseInfo> allTrainsetBaseInfos = RemoteServiceCachedDataUtil.getTrainsetList();
            // 校验各个层级的值是否合理，比如车型条件排除了A，批次条件就不能有A下的批次了
            checkValueRange(checkValue, allTrainsetBaseInfos);
        }

        @Override
        public String predigestValue(FlowInfo flowInfo) {
            StringBuffer stringBuffer = null;
            TrainConditionValue checkValue = this.from(flowInfo);
            // 简化可能的包含条件，以保证相同匹配效果的条件的数据同一性
            // 找到最小级别的不为空的包含条件
            TrainInfoConditionLevelEnum lowestNotExcludeAndNotEmptyLevel = CommonUtils.find(TrainInfoConditionLevelEnum.getIterableToMax(
                TrainInfoConditionLevelEnum.TRAINSET_LEVEL),
                (v) -> !v.checkIsExclude(checkValue) && v.hasValue(checkValue)
            );

            if (lowestNotExcludeAndNotEmptyLevel != null) {
                List<TrainsetBaseInfo> allTrainsetBaseInfos = RemoteServiceCachedDataUtil.getTrainsetList();
                List<TrainsetBaseInfo> filteredTrainsetInfos = allTrainsetBaseInfos.stream().filter(lowestNotExcludeAndNotEmptyLevel.getTrainsetFilter(checkValue)).collect(Collectors.toList());
                // 此前的级别对应值均要与目标级别过滤出的车组进行比对，如果存在多余值则去除
                for (TrainInfoConditionLevelEnum levelEnum : TrainInfoConditionLevelEnum.getIterableToMax(TrainInfoConditionLevelEnum.getHigher(lowestNotExcludeAndNotEmptyLevel))) {
                    if (levelEnum.checkIsExclude(checkValue)) {
                        throw new IllegalArgumentException("程序错误");
                    }
                    Set<String> trainsetInfoValues = filteredTrainsetInfos.stream().map(levelEnum::getValueFromTrainsetInfo).collect(Collectors.toSet());
                    Set<String> values = levelEnum.getValue(checkValue);
                    if (!trainsetInfoValues.equals(values)) {
                        List<String> originalValues = getValues(flowInfo, levelEnum);
                        List<String> newValues = originalValues.stream().filter(trainsetInfoValues::contains).collect(Collectors.toList());
                        setValues(flowInfo, levelEnum, newValues);

                        if (stringBuffer == null) {
                            stringBuffer = new StringBuffer();
                        }
                        if (stringBuffer.length() > 0) {
                            stringBuffer.append("；");
                        }
                        stringBuffer
                            .append("以下")
                            .append(levelEnum.getSimpleConditionName())
                            .append("未配置其下的")
                            .append(TrainInfoConditionLevelEnum.getLower(levelEnum).getSimpleConditionName())
                            .append("：【")
                            .append(values.stream().filter(v -> !trainsetInfoValues.contains(v)).collect(Collectors.joining("，")))
                            .append("】，已从条件中移除");
                    }
                }
                if (stringBuffer != null) {
                    stringBuffer.append("。");
                }
            }
            return stringBuffer == null ? null : stringBuffer.toString();
        }

        @Override
        public boolean equals(TrainConditionValue value, TrainConditionValue otherValue) {
            return value.equals(otherValue);
        }

        /**
         * 两个车组条件的指定级别是否存在交集，禁止一方在此级别为空
         *
         * @param valueA
         * @param valueB
         * @param valueLevel
         * @return
         */
        boolean hasIntersectionInner(TrainConditionValue valueA, TrainConditionValue valueB, TrainInfoConditionLevelEnum valueLevel) {
            Set<String> curLevelValuesA = valueLevel.getValue(valueA);
            Set<String> curLevelValuesB = valueLevel.getValue(valueB);
            if (CollectionUtils.isEmpty(curLevelValuesA) || CollectionUtils.isEmpty(curLevelValuesB)) {
                throw new RuntimeException("由于车组各个条件具有整体性，单独某个条件为空无法直接判断在此级别是否存在交集");
            } else {
                return CommonUtils.some(curLevelValuesA, curLevelValuesB::contains);
            }
        }

        @Override
        public boolean hasIntersection(TrainConditionValue value, TrainConditionValue otherValue) {
            TrainInfoConditionLevelEnum valueMinLevel = FlowConfigSingleConditionUtil.getMinLevel(value);

            //判断已选择最后一项
            boolean valueIsExcludeCondition = FlowConfigSingleConditionUtil.getIsExcludeCondition(value);

            TrainInfoConditionLevelEnum otherValueMinLevel = FlowConfigSingleConditionUtil.getMinLevel(otherValue);
            //判断已选择最后一项
            boolean otherValueIsExcludeCondition = FlowConfigSingleConditionUtil.getIsExcludeCondition(otherValue);

            if (!valueIsExcludeCondition && !otherValueIsExcludeCondition) {
                // 如果最小层级相同，则需要判断最小层是否重叠
                if (valueMinLevel == otherValueMinLevel) {
                    return hasIntersectionInner(value, otherValue, valueMinLevel);
                } else {
                    TrainInfoConditionLevelEnum largeLevel = ComparatorUtils.max(valueMinLevel, otherValueMinLevel, TrainInfoConditionLevelEnum::compareTo);
                    // 否则看最小的均有值的层级是否存在重叠
                    return hasIntersectionInner(value, otherValue, largeLevel);
                }
            } else {
                // 如果都是排除条件，并且最小级别相同，并且次小级别本身不存在 或者 次小级别条件存在交集，则固定返回true
                if (valueIsExcludeCondition && otherValueIsExcludeCondition && valueMinLevel == otherValueMinLevel && (
                    valueMinLevel == TrainInfoConditionLevelEnum.max() || hasIntersectionInner(value, otherValue, TrainInfoConditionLevelEnum.getHigher(valueMinLevel))
                )) {
                    return true;
                }
                // 获取所有车组
                List<TrainsetBaseInfo> trainsetBaseInfos = RemoteServiceCachedDataUtil.getTrainsetList();
                // 根据value过滤出车组ID列表
                Set<String> trainsetIdsFilterByValue = FlowConfigSingleConditionUtil.getFilteredTrainsetIds(trainsetBaseInfos, value);
                // 根据otherValue过滤出车组ID列表
                Set<String> trainsetIdsFilterByOtherValue = FlowConfigSingleConditionUtil.getFilteredTrainsetIds(trainsetBaseInfos, otherValue);
                // 判断两个列表是否存在重叠
                return CommonUtils.some(trainsetIdsFilterByValue, trainsetId -> trainsetIdsFilterByOtherValue.contains(trainsetId));
            }
        }

        @Override
        public DescriptionLevelDifferentDirectionEnum getDescriptionLevelDifferentDirection(TrainConditionValue value, TrainConditionValue otherValue) {
            TrainInfoConditionLevelEnum valueMinLevel = FlowConfigSingleConditionUtil.getMinLevel(value);
            boolean valueIsExcludeCondition = FlowConfigSingleConditionUtil.getIsExcludeCondition(value);

            TrainInfoConditionLevelEnum otherValueMinLevel = FlowConfigSingleConditionUtil.getMinLevel(otherValue);
            boolean otherValueIsExcludeCondition = FlowConfigSingleConditionUtil.getIsExcludeCondition(otherValue);
            if (!valueIsExcludeCondition && !otherValueIsExcludeCondition) {// 只有均为包含条件，空值才有实际意义
                if (valueMinLevel == otherValueMinLevel) {// 等级相同则说明没有空值上的级别区别
                    return null;
                } else {
                    // 最小公共级别
                    TrainInfoConditionLevelEnum minCommonLevel = ComparatorUtils.max(valueMinLevel, otherValueMinLevel, TrainInfoConditionLevelEnum::compareTo);
                    Set<String> minCommonLevelValue = minCommonLevel.getValue(value);
                    Set<String> minCommonLevelOtherValue = minCommonLevel.getValue(otherValue);
                    // 如果最小公共级别对应的值的包含方向与最小级别的方向相同，则返回对应的包含方向
                    if (minCommonLevelValue.containsAll(minCommonLevelOtherValue) && valueMinLevel.compareTo(otherValueMinLevel) > 0) {
                        return DescriptionLevelDifferentDirectionEnum.LEFT;
                    } else if (minCommonLevelOtherValue.containsAll(minCommonLevelValue) && otherValueMinLevel.compareTo(valueMinLevel) > 0) {
                        return DescriptionLevelDifferentDirectionEnum.RIGHT;
                    } else {
                        return null;
                    }
                }
            } else {
                return null;
            }
        }

        @Override
        public String getErrorMessage(TrainConditionValue checkValue, TrainConditionValue existValue, FlowInfo existFlowInfo) {
            TrainInfoConditionLevelEnum checkValueMinLevel = FlowConfigSingleConditionUtil.getMinLevel(checkValue);
            boolean checkValueIsExcludeCondition = FlowConfigSingleConditionUtil.getIsExcludeCondition(checkValue);

            TrainInfoConditionLevelEnum existValueMinLevel = FlowConfigSingleConditionUtil.getMinLevel(existValue);
            boolean existValueIsExcludeCondition = FlowConfigSingleConditionUtil.getIsExcludeCondition(existValue);

            if (!checkValueIsExcludeCondition && !existValueIsExcludeCondition) {
                // 如果最小层级相同，则根据最小层级的值生成错误信息
                if (checkValueMinLevel == existValueMinLevel) {
                    // 车组特殊处理
                    if (checkValueMinLevel == TrainInfoConditionLevelEnum.TRAINSET_LEVEL) {
                        return FlowConfigSingleConditionUtil.getErrorMessage(
                            toTrainsetNames(checkValueMinLevel.getValue(checkValue)),
                            toTrainsetNames(existValueMinLevel.getValue(existValue)),
                            existFlowInfo.getName(),
                            checkValueMinLevel.getConditionName()
                        );
                    } else {
                        return FlowConfigSingleConditionUtil.getErrorMessage(
                            checkValueMinLevel.getValue(checkValue),
                            existValueMinLevel.getValue(existValue),
                            existFlowInfo.getName(),
                            checkValueMinLevel.getConditionName()
                        );
                    }
                } else {
                    throw new RuntimeException("程序逻辑异常");
                }
            } else {
                // 如果都是排除条件，并且最小级别相同，并且次小级别本身不存在 或者 次小级别条件存在交集，则仍可直接根据配置信息生成错误信息
                if (checkValueIsExcludeCondition && existValueIsExcludeCondition && checkValueMinLevel == existValueMinLevel && (
                    checkValueMinLevel == TrainInfoConditionLevelEnum.max() || hasIntersectionInner(checkValue, existValue, TrainInfoConditionLevelEnum.getHigher(checkValueMinLevel))
                )) {
                    if (TrainInfoConditionLevelEnum.getHigher(checkValueMinLevel) == TrainInfoConditionLevelEnum.max()) {
                        return "相同流程类型的流程配置，禁止出现以下情况：两个流程配置，车型以外的其他条件均相同或存在交集，并且车型条件均为排除条件";
                    } else {
                        TrainInfoConditionLevelEnum targetLevel = TrainInfoConditionLevelEnum.getHigher(checkValueMinLevel);
                        return FlowConfigSingleConditionUtil.getErrorMessage(
                            targetLevel.getValue(checkValue),
                            targetLevel.getValue(existValue),
                            existFlowInfo.getName(),
                            targetLevel.getConditionName()
                        );
                    }
                }
                // 获取所有车组
                List<TrainsetBaseInfo> trainsetBaseInfos = RemoteServiceCachedDataUtil.getTrainsetList();
                // 根据value过滤出车组ID列表
                Set<String> trainsetIdsFilterByCheckValue = FlowConfigSingleConditionUtil.getFilteredTrainsetIds(trainsetBaseInfos, checkValue);
                // 根据otherValue过滤出车组ID列表
                Set<String> trainsetIdsFilterByExistValue = FlowConfigSingleConditionUtil.getFilteredTrainsetIds(trainsetBaseInfos, existValue);
                return FlowConfigSingleConditionUtil.getErrorMessage(
                    toTrainsetNames(trainsetIdsFilterByCheckValue),
                    toTrainsetNames(trainsetIdsFilterByExistValue),
                    existFlowInfo.getName(),
                    TrainInfoConditionLevelEnum.TRAINSET_LEVEL.getConditionName() + "(最终匹配)"
                );
            }
        }

        Set<String> toTrainsetNames(Set<String> trainsetIds) {
            // 获取所有车组
            List<TrainsetBaseInfo> trainsetBaseInfos = RemoteServiceCachedDataUtil.getTrainsetList();
            Map<String, TrainsetBaseInfo> trainsetBaseInfoMap = CommonUtils.collectionToMap(trainsetBaseInfos, TrainsetBaseInfo::getTrainsetid);

            return trainsetIds.stream().map(v -> {
                if (trainsetBaseInfoMap.containsKey(v)) {
                    return trainsetBaseInfoMap.get(v).getTrainsetname();
                } else {
                    return v;
                }
            }).collect(Collectors.toSet());
        }

        @Override
        public TrainConditionValue from(FlowInfo flowInfo) {
            TrainConditionValue trainConditionValue = new TrainConditionValue();
            BeanUtils.copyProperties(flowInfo, trainConditionValue);
            trainConditionValue.setTrainTypes(new HashSet<>(flowInfo.getTrainTypes()));
            trainConditionValue.setTrainTemplates(new HashSet<>(flowInfo.getTrainTemplates()));
            trainConditionValue.setTrainsetIds(new HashSet<>(flowInfo.getTrainsetIds()));

            return trainConditionValue;
        }
    }

    public static class KeyWordsCondition extends FlowConfigSingleConditionBase<Set<String>> {

        private static FlowConfigSingleConditionEnum conditionEnum;

        KeyWordsCondition(FlowConfigSingleConditionEnum conditionEnum) {
            KeyWordsCondition.conditionEnum = conditionEnum;
        }

        public static KeyWordsCondition getInstance() {
            return (KeyWordsCondition) conditionEnum.getCondition();
        }

        @Override
        public boolean equals(Set<String> value, Set<String> otherValue) {
            return value.equals(otherValue);
        }

        @Override
        public boolean hasIntersection(Set<String> value, Set<String> otherValue) {
            // 为空意味着匹配所有关键字，所以任意一边为空时会被当做有交集
            if (CollectionUtils.isEmpty(value) || CollectionUtils.isEmpty(otherValue)) {
                return true;
            } else {
                return CommonUtils.some(value, otherValue::contains);
            }
        }

        @Override
        public DescriptionLevelDifferentDirectionEnum getDescriptionLevelDifferentDirection(Set<String> value, Set<String> otherValue) {
            boolean isValueEmpty = CollectionUtils.isEmpty(value);
            boolean isOtherValueEmpty = CollectionUtils.isEmpty(otherValue);
            if (isValueEmpty == isOtherValueEmpty) {
                return null;
            } else {
                return isValueEmpty ? DescriptionLevelDifferentDirectionEnum.LEFT : DescriptionLevelDifferentDirectionEnum.RIGHT;
            }
        }

        @Override
        public String getErrorMessage(Set<String> checkValue, Set<String> existValue, FlowInfo existFlowInfo) {
            return FlowConfigSingleConditionUtil.getErrorMessage(checkValue, existValue, existFlowInfo.getName(), "关键字条件");
        }

        @Override
        public Set<String> from(FlowInfo flowInfo) {
            return new HashSet<>(flowInfo.getKeyWords());
        }
    }

    public static class RepairTypeCondition extends FlowConfigSingleConditionBase<String> {

        private static FlowConfigSingleConditionEnum conditionEnum;

        RepairTypeCondition(FlowConfigSingleConditionEnum conditionEnum) {
            RepairTypeCondition.conditionEnum = conditionEnum;
        }

        public static RepairTypeCondition getInstance() {
            return (RepairTypeCondition) conditionEnum.getCondition();
        }

        @Override
        public boolean equals(String value, String otherValue) {
            if (!StringUtils.hasText(value) && !StringUtils.hasText(otherValue)) {
                return true;
            }
            return Objects.equals(value, otherValue);
        }

        @Override
        public boolean hasIntersection(String value, String otherValue) {
            // 为空意味着匹配所有检修类型，所以任意一边为空时会被当做有交集
            if (!StringUtils.hasText(value) || !StringUtils.hasText(otherValue)) {
                return true;
            } else {
                // 两者均不为空时，相等即有交集
                return equals(value, otherValue);
            }
        }

        @Override
        public DescriptionLevelDifferentDirectionEnum getDescriptionLevelDifferentDirection(String value, String otherValue) {
            boolean isValueEmpty = !StringUtils.hasText(value);
            boolean isOtherValueEmpty = !StringUtils.hasText(otherValue);
            if (isValueEmpty == isOtherValueEmpty) {
                return null;
            } else {
                return isValueEmpty ? DescriptionLevelDifferentDirectionEnum.LEFT : DescriptionLevelDifferentDirectionEnum.RIGHT;
            }
        }

        @Override
        public String getErrorMessage(String checkValue, String existValue, FlowInfo existFlowInfo) {
            return "程序逻辑异常";
        }

        @Override
        public String from(FlowInfo flowInfo) {
            return flowInfo.getRepairType();
        }
    }

    public FlowConfigSingleConditionBase<?> getCondition() {
        return condition;
    }

    @SuppressWarnings("unchecked")
    public <T> FlowConfigSingleConditionBase<T> getCondition(Class<T> conditionClass) {
        if (!conditionClass.equals(ReflectUtil.getFirstTypeArgument(condition))) {
            throw RestRequestException.serviceInnerFatalFail("conditionClass与预期不符");
        }
        return (FlowConfigSingleConditionBase<T>) condition;
    }

}
