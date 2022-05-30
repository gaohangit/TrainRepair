package com.ydzbinfo.emis.trainRepair.repairworkflow.utils.extrainfo;

import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.base.IExtraInfoBase;
import com.ydzbinfo.emis.utils.CacheUtil;
import org.springframework.util.StringUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 额外信息转换抽象类
 *
 * @author 张天可
 * @since 2021/7/6
 */
public abstract class ExtraInfoConvertorBase<VALUE, MODEL, EXTRA_INFO_MODEL extends IExtraInfoBase> implements IExtraInfoConvertor<VALUE, MODEL, EXTRA_INFO_MODEL> {

    @Override
    public void setValueFromExtraInfoList(MODEL nodeInfo, List<EXTRA_INFO_MODEL> nodeExtraInfoList) {
        setValue(nodeInfo, transformToValue(nodeExtraInfoList));
    }

    @Override
    public List<EXTRA_INFO_MODEL> getCurrentTypeExtraInfoList(MODEL nodeInfo) {
        return transformToExtraInfoList(getValue(nodeInfo));
    }

    protected Map<String, List<EXTRA_INFO_MODEL>> getGroupedNodeExtraInfos(List<EXTRA_INFO_MODEL> nodeExtraInfos) {
        return CacheUtil.getDataUseThreadCache(
            "ExtraInfoConvertorBase.this.getGroupedNodeExtraInfos_"  + (nodeExtraInfos.size() > 0 ? nodeExtraInfos.get(0).getClass().getName() : Void.TYPE.getName()) + "_" + nodeExtraInfos.hashCode(),
            () -> nodeExtraInfos.stream().collect(Collectors.groupingBy(EXTRA_INFO_MODEL::getType))
        );
    }

    protected VALUE transToValueByTypeName(List<EXTRA_INFO_MODEL> nodeExtraInfos, String typeName, Function<String[], VALUE> getValueFun) {
        List<EXTRA_INFO_MODEL> curNodeExtraInfos = this.getGroupedNodeExtraInfos(nodeExtraInfos).get(typeName);
        if (curNodeExtraInfos == null) {
            ParameterizedType parameterizedType = (ParameterizedType) this.getClass().getGenericSuperclass();
            Type valueType = parameterizedType.getActualTypeArguments()[0];
            Class<?> aClass;
            if (valueType instanceof Class) {
                aClass = (Class<?>) valueType;
            } else if (valueType instanceof ParameterizedType) {
                aClass = (Class<?>) ((ParameterizedType) valueType).getRawType();
            } else {
                throw new RuntimeException("代码错误");
            }
            if (Collection.class.isAssignableFrom(aClass)) {
                curNodeExtraInfos = new ArrayList<>();
            } else {
                return null;
            }

        }
        return getValueFun.apply(curNodeExtraInfos.stream().map(EXTRA_INFO_MODEL::getValue).toArray(String[]::new));
    }

    abstract protected EXTRA_INFO_MODEL newExtraInfoModel();

    protected List<EXTRA_INFO_MODEL> transToExtraInfoListByTypeName(String typeName, String... values) {
        return Arrays.stream(values).filter(v -> !StringUtils.isEmpty(v)).map(value -> {
            EXTRA_INFO_MODEL nodeExtraInfo = newExtraInfoModel();
            nodeExtraInfo.setValue(value);
            nodeExtraInfo.setType(typeName);
            return nodeExtraInfo;
        }).collect(Collectors.toList());
    }
}
