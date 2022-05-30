package com.ydzbinfo.emis.trainRepair.repairworkflow.utils;

import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetBaseInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.TrainConditionValue;
import com.ydzbinfo.emis.utils.CommonUtils;
import lombok.AllArgsConstructor;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 车组条件级别枚举
 * 由于使用了ordinal，禁止改变枚举值之间的顺序
 *
 * @author 张天可
 * @since 2021/9/8
 */
@AllArgsConstructor
public enum TrainInfoConditionLevelEnum {
    TRAINSET_LEVEL(
        "车组条件",
        TrainConditionValue::getTrainsetIds,
        TrainConditionValue::getTrainsetIdExclude,
        TrainsetBaseInfo::getTrainsetid
    ),
    TRAIN_TEMPLATE_LEVEL(
        "批次条件",
        TrainConditionValue::getTrainTemplates,
        TrainConditionValue::getTrainTemplateExclude,
        TrainsetBaseInfo::getTraintempid
    ),
    TRAIN_TYPE_LEVEL(
        "车型条件",
        TrainConditionValue::getTrainTypes,
        TrainConditionValue::getTrainTypeExclude,
        TrainsetBaseInfo::getTraintype
    ),
    // NONE类型用于简化相关逻辑
    NONE(
        "全部车型",
        v -> Collections.singleton("ALL"),
        v -> false,
        v -> "ALL"
    );

    private final String conditionName;
    private final Function<TrainConditionValue, Set<String>> conditionValueGetter;
    private final Predicate<TrainConditionValue> excludeChecker;
    private final Function<TrainsetBaseInfo, String> trainsetInfoValueGetter;

    public boolean hasValue(TrainConditionValue trainConditionValue) {
        Set<String> values = conditionValueGetter.apply(trainConditionValue);
        return values != null && values.size() > 0;
    }

    public Set<String> getValue(TrainConditionValue trainConditionValue) {
        return conditionValueGetter.apply(trainConditionValue);
    }

    public boolean checkIsExclude(TrainConditionValue trainConditionValue) {
        return excludeChecker.test(trainConditionValue);
    }

    public String getConditionName() {
        return conditionName;
    }

    public String getSimpleConditionName() {
        return conditionName.replace("条件", "");
    }

    public String getValueFromTrainsetInfo(TrainsetBaseInfo trainsetBaseInfo) {
        return trainsetInfoValueGetter.apply(trainsetBaseInfo);
    }

    public static TrainInfoConditionLevelEnum max() {
        TrainInfoConditionLevelEnum[] trainInfoConditionLevelEnums = TrainInfoConditionLevelEnum.values();
        return trainInfoConditionLevelEnums[trainInfoConditionLevelEnums.length - 1];
    }

    private static final Map<Integer, TrainInfoConditionLevelEnum> cacheMap = CommonUtils.collectionToMap(Arrays.asList(TrainInfoConditionLevelEnum.values()), Enum::ordinal);

    public static TrainInfoConditionLevelEnum getHigher(TrainInfoConditionLevelEnum trainInfoConditionLevelEnum) {
        return cacheMap.get(trainInfoConditionLevelEnum.ordinal() + 1);
    }

    public static TrainInfoConditionLevelEnum getLower(TrainInfoConditionLevelEnum trainInfoConditionLevelEnum) {
        return cacheMap.get(trainInfoConditionLevelEnum.ordinal() - 1);
    }

    public Predicate<TrainsetBaseInfo> getTrainsetFilter(TrainConditionValue trainConditionValue) {
        if (!this.hasValue(trainConditionValue)) {
            return v -> true;
        } else {
            return (trainsetInfo) -> {
                Set<String> values = this.getValue(trainConditionValue);
                String value = this.getValueFromTrainsetInfo(trainsetInfo);
                return values.contains(value) != this.checkIsExclude(trainConditionValue);
            };
        }
    }

    public static Iterable<TrainInfoConditionLevelEnum> getIterableToMax(TrainInfoConditionLevelEnum from) {
        return getIterable(from, TrainInfoConditionLevelEnum::getHigher);
    }

    public static Iterable<TrainInfoConditionLevelEnum> getIterableToMin(TrainInfoConditionLevelEnum from) {
        return getIterable(from, TrainInfoConditionLevelEnum::getLower);
    }

    private static Iterable<TrainInfoConditionLevelEnum> getIterable(TrainInfoConditionLevelEnum from, Function<TrainInfoConditionLevelEnum, TrainInfoConditionLevelEnum> nextGetter) {
        return () -> new Iterator<TrainInfoConditionLevelEnum>() {
            TrainInfoConditionLevelEnum current = null;
            boolean initialized = false;

            @Override
            public boolean hasNext() {
                if (!initialized) {
                    return from != null;
                } else {
                    return nextGetter.apply(current) != null;
                }
            }

            @Override
            public TrainInfoConditionLevelEnum next() {
                if (!initialized) {
                    initialized = true;
                    current = Objects.requireNonNull(from);
                } else {
                    current = nextGetter.apply(current);
                }
                return current;
            }
        };
    }
}
