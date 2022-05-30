package com.ydzbinfo.emis.trainRepair.bill.model.bill;

import com.ydzbinfo.emis.trainRepair.bill.general.IBillCellInfoForShowGeneral;
import com.ydzbinfo.emis.trainRepair.bill.general.UrlInfo;
import com.ydzbinfo.emis.trainRepair.bill.general.constant.BillCellTriggerTimingEnum;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChecklistDetail;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChecklistLinkControl;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChkDetailLinkContent;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Description:
 * @Data: 2021/7/31
 * @Author: 韩旭
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ChecklistDetailInfoForShow extends ChecklistDetail implements IBillCellInfoForShowGeneral<ChecklistLinkControl> {
    /**
     * 单元格信息是否已保存，初始化数据时如果为null视作true，额外调用url时如果为null视作false
     */
    private Boolean saved;

    /**
     * 单元格信息是否已初始化过，saved为false时生效，如果initialized为false意味着初次保存时传递的changeType为INSERT，为null时视作true
     */
    private Boolean initialized;

    /**
     * 单元格信息是需要使用属性默认值进行初始化，为null时视作false（单元格内容为空时生效）
     */
    private Boolean needInitializeByAttributeDefaultValue;

    /**
     * 单元格是否临时设置为只读，为null时使用属性的只读信息设置
     */
    private Boolean readOnly;

    /**
     * 回填控制数据
     */
    private List<ChecklistLinkControl> controls;

    /**
     * 关联数据
     */
    private List<ChkDetailLinkContent> linkContents;
    /**
     * 额外下拉列表选项
     */
    private List<String> extraOptions;

    /**
     * 当用户选择、输入什么值时才触发url
     */
    private String triggerConditionOption;

    /**
     * 触发时机 (1—设置界面数据前触发, 2—设置界面数据后触发) 前端逻辑中默认2
     */
    private BillCellTriggerTimingEnum triggerTiming;

    /**
     * 数据变更时或者按钮按下时要触发的url
     */
    private UrlInfo triggerUrlInfo;

    /**
     * 批量触发url操作时，批量传递单元格信息时的分组依据，为空时使用triggerUrlInfo的url值
     */
    private String triggerGroupKey;

    /**
     * url调用前需要用户进行确认的信息
     */
    private String triggerUrlConfirmMessage;

    /**
     * 单元格提示信息
     */
    private String tip;

}
