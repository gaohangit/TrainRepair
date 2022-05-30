package com.ydzbinfo.emis.trainRepair.mobile.fillback.service.impl;

import com.ydzbinfo.emis.trainRepair.bill.fillback.service.impl.CheckInfoInit;
import com.ydzbinfo.emis.trainRepair.bill.fillback.utils.AttributeEnum;
import com.ydzbinfo.emis.trainRepair.bill.fillback.utils.BillCommon;
import com.ydzbinfo.emis.trainRepair.bill.general.UrlInfo;
import com.ydzbinfo.emis.trainRepair.bill.general.constant.BillCellTriggerTimingEnum;
import com.ydzbinfo.emis.trainRepair.bill.general.constant.BillCellTriggerUrlTypeEnum;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.ChecklistDetailInfoForShow;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.ChecklistSummaryInfoForShow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description: 手持机一二级修记录单初始化规则
 * @author: 冯帅
 */
@Component
public class PhoneFirstPersonKeepAndTest extends CheckInfoInit {

    @Autowired
    BillCommon billCommon;

    @Override
    public void initContentRule(ChecklistSummaryInfoForShow content) {
        //初始化数据
        List<ChecklistDetailInfoForShow> contentList = content.getCells();
        for (ChecklistDetailInfoForShow detail : contentList) {
            if (AttributeEnum.ATTR_REPAIR_PERSON_ONE.getValue().equals(detail.getAttributeCode()) ||
                AttributeEnum.ATTR_REPAIR_PERSON_TWO.getValue().equals(detail.getAttributeCode()) ||
                AttributeEnum.ATTR_REPAIR_PERSON_THREE.getValue().equals(detail.getAttributeCode()) ||
                AttributeEnum.ATTR_REPAIR_PERSON_FOUR.getValue().equals(detail.getAttributeCode()) ||
                AttributeEnum.ATTR_REPAIR_PERSON.getValue().equals(detail.getAttributeCode())) {
                UrlInfo triggerUrlInfo = new UrlInfo();
                triggerUrlInfo.setUrlType(BillCellTriggerUrlTypeEnum.FUNCTION);
                triggerUrlInfo.setUrl(billCommon.addApplicationPrefix("/mobileOneTwoFillBack/setPhoneOneTwoRepairPersonSign"));//检修工人签字
                detail.setTriggerUrlInfo(triggerUrlInfo);
                detail.setTriggerTiming(BillCellTriggerTimingEnum.AFTER_CHANGE);
            }
            if (AttributeEnum.ATTR_REPAIR_RESULT.getValue().equals(detail.getAttributeCode())) {
                String fillVauleToFaultConfig = getFillVauleToFaultConfig();
                //遇到X，需要进行提示，如果用户选择是，则调用故障录入界面
                detail.setTriggerConditionOption(fillVauleToFaultConfig);
                detail.setTriggerUrlConfirmMessage("您确认进行故障录入吗？");
                UrlInfo triggerUrlInfo = new UrlInfo();
                triggerUrlInfo.setUrlType(BillCellTriggerUrlTypeEnum.PAGE);
                triggerUrlInfo.setUrl("XM009_"+content.getExtraObject().getTrainsetId()+"_"+detail.getValue());//跳转故障界面，参数用下划线进行分割
                detail.setTriggerUrlInfo(triggerUrlInfo);
                detail.setTriggerTiming(BillCellTriggerTimingEnum.AFTER_CHANGE);
            } else if (AttributeEnum.ATTR_FOREMAN_SIGN.getValue().equals(detail.getAttributeCode())) {
                UrlInfo triggerUrlInfo = new UrlInfo();
                triggerUrlInfo.setUrlType(BillCellTriggerUrlTypeEnum.FUNCTION);
                triggerUrlInfo.setUrl(billCommon.addApplicationPrefix("/mobileOneTwoFillBack/setPhoneOneTwoRepairForemanSign"));//检修工长签字
                detail.setTriggerUrlInfo(triggerUrlInfo);
                detail.setTriggerTiming(BillCellTriggerTimingEnum.AFTER_CHANGE);
            } else if (AttributeEnum.ATTR_QUALITY_SIGN.getValue().equals(detail.getAttributeCode())) {
                UrlInfo triggerUrlInfo = new UrlInfo();
                triggerUrlInfo.setUrlType(BillCellTriggerUrlTypeEnum.FUNCTION);
                triggerUrlInfo.setUrl(billCommon.addApplicationPrefix("/mobileOneTwoFillBack/setPhoneOneTwoRepairQualitySign"));//质检签字
                detail.setTriggerUrlInfo(triggerUrlInfo);
                detail.setTriggerTiming(BillCellTriggerTimingEnum.AFTER_CHANGE);
            }
        }
    }
}
