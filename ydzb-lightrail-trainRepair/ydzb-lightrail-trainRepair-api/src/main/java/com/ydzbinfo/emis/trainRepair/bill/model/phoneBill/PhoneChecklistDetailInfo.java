package com.ydzbinfo.emis.trainRepair.bill.model.phoneBill;

import com.ydzbinfo.emis.trainRepair.bill.general.UrlInfo;
import com.ydzbinfo.emis.trainRepair.bill.general.constant.BillEntityChangeTypeEnum;
import com.ydzbinfo.emis.trainRepair.bill.model.attribute.TemplateAttributeForSave;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChecklistDetail;
import lombok.Data;

@Data
public class PhoneChecklistDetailInfo extends ChecklistDetail {
    //变更状态
    private BillEntityChangeTypeEnum changeType;

    //属性类型
    private TemplateAttributeForSave  attribute;

    /**
     * 是否允许当前单元格变更（仅触发时机为1时有效）（多次调用时仅第一次有效）
     */
    private Boolean allowChange;

    /**
     * 当用户选择、输入什么值时才触发url
     */
    private String triggerConditionOption;

    /**
     * 数据变更时或者按钮按下时要触发的url
     */
    private UrlInfo triggerUrlInfo;

    /**
     * url调用前需要用户进行确认的信息
     */
    private String triggerUrlConfirmMessage;
}
