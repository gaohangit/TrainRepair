package com.ydzbinfo.emis.trainRepair.bill.fillback.service.impl.OneTwoRepairCheckInfoInitPackage;

import com.ydzbinfo.emis.trainRepair.bill.fillback.service.impl.CheckInfoInit;
import com.ydzbinfo.emis.trainRepair.bill.fillback.utils.AttributeEnum;
import com.ydzbinfo.emis.trainRepair.bill.fillback.utils.BillCommon;
import com.ydzbinfo.emis.trainRepair.bill.general.UrlInfo;
import com.ydzbinfo.emis.trainRepair.bill.general.constant.BillCellTriggerTimingEnum;
import com.ydzbinfo.emis.trainRepair.bill.general.constant.BillCellTriggerUrlTypeEnum;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.*;
import com.ydzbinfo.emis.utils.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * <p>
 * 二级修预防性记录单
 * </p>
 *
 * @author 韩旭
 * @since 2021-08-16
 */
@Component
public class SecondPreventive extends CheckInfoInit {
    protected static final Logger logger = getLogger(SecondPreventive.class);
    @Autowired
    BillCommon billCommon;
    @Override
    public void initContentRule(ChecklistSummaryInfoForShow content) {
        super.initContentRule(content);
        List<ChecklistDetailInfoForShow> contentList = content.getCells();
        for (ChecklistDetailInfoForShow detail : contentList) {
            if (detail.getAttributeCode().equals(AttributeEnum.ATTR_REPAIR_PERSON_ONE.getValue()) ||
                detail.getAttributeCode().equals(AttributeEnum.ATTR_REPAIR_PERSON_TWO.getValue()) ||
                detail.getAttributeCode().equals(AttributeEnum.ATTR_REPAIR_PERSON_THREE.getValue()) ||
                detail.getAttributeCode().equals(AttributeEnum.ATTR_REPAIR_PERSON_FOUR.getValue()) ||
                detail.getAttributeCode().equals(AttributeEnum.ATTR_REPAIR_PERSON.getValue())) {
                UrlInfo triggerUrlInfo = new UrlInfo();
                triggerUrlInfo.setUrlType(BillCellTriggerUrlTypeEnum.FUNCTION);
                triggerUrlInfo.setUrl(billCommon.addApplicationPrefix("/checkInfo/repairPersonSign"));//签字
                detail.setTriggerUrlInfo(triggerUrlInfo);
                detail.setTriggerTiming(BillCellTriggerTimingEnum.AFTER_CHANGE);
            } else if (AttributeEnum.ATTR_REPAIR_RESULT.getValue().equals(detail.getAttributeCode())) {
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

    public ChecklistTriggerUrlCallResult repairPersonSign(ChecklistSummaryInfoForSave saveInfo) {
        try {
            List<ChecklistDetailInfoForSave> contentList = saveInfo.getCells();
            String personShow = "";
            List<ChecklistDetailInfoForSave> filterContent = new ArrayList<>();
            filterContent.addAll(contentList.stream().filter((ChecklistDetailInfoForSave t) -> t.getAttributeCode().equals(AttributeEnum.ATTR_REPAIR_PERSON_ONE.getValue())).
                    collect(Collectors.toList()));
            filterContent.addAll(contentList.stream().filter((ChecklistDetailInfoForSave t) -> t.getAttributeCode().equals(AttributeEnum.ATTR_REPAIR_PERSON_TWO.getValue())).
                    collect(Collectors.toList()));
            filterContent.addAll(contentList.stream().filter((ChecklistDetailInfoForSave t) -> t.getAttributeCode().equals(AttributeEnum.ATTR_REPAIR_PERSON_THREE.getValue())).
                    collect(Collectors.toList()));
            filterContent.addAll(contentList.stream().filter((ChecklistDetailInfoForSave t) -> t.getAttributeCode().equals(AttributeEnum.ATTR_REPAIR_PERSON_FOUR.getValue())).
                    collect(Collectors.toList()));
            filterContent.addAll(contentList.stream().filter((ChecklistDetailInfoForSave t) -> t.getAttributeCode().equals(AttributeEnum.ATTR_REPAIR_PERSON.getValue())).
                    collect(Collectors.toList()));
            List<String> strList = filterContent.stream().map(ChecklistDetailInfoForSave::getValue).collect(Collectors.toList());
            strList = strList.stream().distinct().collect(Collectors.toList());
            strList.remove("");
            personShow = StringUtils.join(strList, ",");
            ChecklistTriggerUrlCallResult result = new ChecklistTriggerUrlCallResult();
            List<ChecklistDetailInfoForShow> changedCells = new ArrayList<>();
            List<ChecklistDetailInfoForSave> saveInfos = contentList.stream().filter((ChecklistDetailInfoForSave t) -> t.getAttributeCode().equals(AttributeEnum.ATTR_REPAIR_PERSON_SHOW.getValue())).
                    collect(Collectors.toList());
            for (ChecklistDetailInfoForSave info : saveInfos) {
                ChecklistDetailInfoForShow infoForShow = new ChecklistDetailInfoForShow();
                BeanUtils.copyProperties(info, infoForShow);
                infoForShow.setValue(personShow);
                changedCells.add(infoForShow);
            }
            result.setChangedCells(changedCells);
            return result;
        } catch (Exception exc) {
            logger.error("更新显示人员信息失败!", exc);
            return null;
        }
    }
}
