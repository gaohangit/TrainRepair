package com.ydzbinfo.emis.trainRepair.repairworkflow.utils.extrainfo;


import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.base.IExtraInfoBase;

import java.util.List;


/**
 * 额外信息转换
 *
 * @param <VALUE>            转换值类
 * @param <MODEL>            完整对象类
 * @param <EXTRA_INFO_MODEL> 额外信息对象类
 * @author 张天可
 */
public interface IExtraInfoConvertor<VALUE, MODEL, EXTRA_INFO_MODEL extends IExtraInfoBase> {

    /**
     * 根据额外信息设置当前类型的目标完整对象信息
     *
     * @param model
     * @param extraInfoModels
     */
    void setValueFromExtraInfoList(MODEL model, List<EXTRA_INFO_MODEL> extraInfoModels);

    /**
     * 根据目标完整对象信息生成当前类型的额外信息
     * 注：没有节点id
     *
     * @param model
     * @return
     */
    List<EXTRA_INFO_MODEL> getCurrentTypeExtraInfoList(MODEL model);

    /**
     * 额外信息转换为当前类型对应值
     *
     * @param nodeExtraInfos
     * @return
     */
    VALUE transformToValue(List<EXTRA_INFO_MODEL> nodeExtraInfos);

    /**
     * 将实际值转换为额外信息
     * 注：没有节点id
     *
     * @param value
     * @return
     */
    List<EXTRA_INFO_MODEL> transformToExtraInfoList(VALUE value);

    /**
     * 从目标完整对象信息中获取当前类型值
     *
     * @param nodeInfo
     * @return
     */
    VALUE getValue(MODEL nodeInfo);

    /**
     * 设置节点当前类型值
     *
     * @param nodeInfo
     * @param value
     */
    void setValue(MODEL nodeInfo, VALUE value);

}
