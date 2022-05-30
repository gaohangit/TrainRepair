package com.ydzbinfo.emis.trainRepair.repairworkflow.utils;

import com.ydzbinfo.emis.trainRepair.repairworkflow.model.FlowInfo;
import com.ydzbinfo.emis.utils.EnumUtils;
import com.ydzbinfo.emis.utils.entity.ReflectUtil;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

public abstract class FlowConfigSingleConditionBase<T> implements ISingleCondition<T> {

    // 加载类时需要初始化依赖的枚举类，防止出现空指针异常
    static {
        EnumUtils.staticInitializeEnum(FlowConfigSingleConditionEnum.class);
    }

    public abstract T from(FlowInfo flowInfo);

    /**
     * 获取当前条件的错误信息
     *
     * @param checkValue
     * @param existValue
     * @param existFlowInfo
     * @return
     */
    public abstract String getErrorMessage(T checkValue, T existValue, FlowInfo existFlowInfo);

    /**
     * 校验条件是否合法
     *
     * @param flowInfo
     */
    public void checkValue(FlowInfo flowInfo) {

    }

    /**
     * 化简条件值
     *
     * @param flowInfo 流程配置信息
     * @return 警告信息
     */
    public String predigestValue(FlowInfo flowInfo) {
        return null;
    }
}
