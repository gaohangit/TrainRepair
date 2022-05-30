package com.ydzbinfo.emis.trainRepair.bill.fillback.service.impl.OneTwoRepairCheckInfoInitPackage;


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
 * <p>
 * 一级修记录单
 * </p>
 *
 * @author 韩旭
 * @since 2021-08-16
 */
@Component
public class FirstPersonKeepAndTest extends CheckInfoInit {

    @Autowired
    BillCommon billCommon;
    /**
     * 初始化规则
     */
    public void initContentRule(ChecklistSummaryInfoForShow content) {
        super.initContentRule(content);
        //初始化数据
        List<ChecklistDetailInfoForShow> contentList = content.getCells();
        for (ChecklistDetailInfoForShow detail : contentList) {
            if (AttributeEnum.ATTR_REPAIR_RESULT.getValue().equals(detail.getAttributeCode())) {
                 String fillVauleToFaultConfig = getFillVauleToFaultConfig();
                 //遇到X，需要进行提示，如果用户选择是，则调用故障录入界面
                 detail.setTriggerConditionOption(fillVauleToFaultConfig);
                 detail.setTriggerUrlConfirmMessage("您确认进行故障录入吗？");
                 UrlInfo triggerUrlInfo = new UrlInfo();
                 triggerUrlInfo.setUrlType(BillCellTriggerUrlTypeEnum.FUNCTION);
                 triggerUrlInfo.setUrl(billCommon.addApplicationPrefix("/checkInfo/faultJump"));//跳转故障界面
                 detail.setTriggerUrlInfo(triggerUrlInfo);
                 detail.setTriggerTiming(BillCellTriggerTimingEnum.AFTER_CHANGE);
             }
        }
    }
}
