package com.ydzbinfo.emis.trainRepair.bill.general;

import com.ydzbinfo.emis.trainRepair.bill.general.base.IBillCellControlGeneral;
import com.ydzbinfo.emis.trainRepair.bill.general.base.IBillCellInfoGeneral;
import com.ydzbinfo.emis.trainRepair.bill.general.constant.BillCellTriggerTimingEnum;

import java.util.List;

/**
 * 记录单回填，返回给前端的单元格数据实体类需要实现的接口
 * 展示用
 *
 * @author 张天可
 * @since 2021/6/25
 */
public interface IBillCellInfoForShowGeneral<CTRL extends IBillCellControlGeneral> extends IBillCellInfoGeneral {

    /**
     * 单元格信息id
     */
    String getId();

    void setId(String id);

    /**
     * 单元格信息是否已保存，初始化数据时如果为null视作true，额外调用url时如果为null视作false
     */
    Boolean getSaved();

    void setSaved(Boolean saved);

    /**
     * 单元格信息是否已初始化过，saved为false时生效，如果initialized为false意味着初次保存时传递的changeType为INSERT，为null时视作true
     */
    Boolean getInitialized();

    void setInitialized(Boolean initialized);

    /**
     * 单元格信息是需要使用属性默认值进行初始化，为null时视作false（单元格内容为空时生效）
     */
    Boolean getNeedInitializeByAttributeDefaultValue();

    void setNeedInitializeByAttributeDefaultValue(Boolean needInitializeByAttributeDefaultValue);

    /**
     * 单元格是否临时设置为只读，为null时使用属性的只读信息设置
     */
    Boolean getReadOnly();

    void setReadOnly(Boolean readOnly);

    /**
     * 回填控制数据
     */
    List<CTRL> getControls();

    void setControls(List<CTRL> controls);

    /**
     * 额外下拉列表选项
     */
    List<String> getExtraOptions();

    void setExtraOptions(List<String> extraOptions);

    /**
     * 当用户选择、输入什么值时才触发url
     */
    String getTriggerConditionOption();

    void setTriggerConditionOption(String triggerConditionOption);

    /**
     * 触发时机 (1—设置界面数据前触发, 2—设置界面数据后触发) 前端逻辑中默认2
     */
    BillCellTriggerTimingEnum getTriggerTiming();

    void setTriggerTiming(BillCellTriggerTimingEnum triggerTiming);

    /**
     * 数据变更时或者按钮按下时要触发的url
     */
    UrlInfo getTriggerUrlInfo();

    void setTriggerUrlInfo(UrlInfo triggerUrlInfo);

    /**
     * 批量触发url操作时，批量传递单元格信息时的分组依据，为空时使用triggerUrlInfo的url值
     */
    String getTriggerGroupKey();

    void setTriggerGroupKey(String triggerGroupKey);

    /**
     * url调用前需要用户进行确认的信息
     */
    String getTriggerUrlConfirmMessage();

    void setTriggerUrlConfirmMessage(String triggerUrlConfirmMessage);

    /**
     * 单元格提示信息
     */
    String getTip();

    void setTip(String tip);
}
