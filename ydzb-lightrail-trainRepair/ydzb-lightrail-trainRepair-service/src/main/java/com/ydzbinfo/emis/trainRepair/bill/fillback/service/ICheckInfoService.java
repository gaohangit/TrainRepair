package com.ydzbinfo.emis.trainRepair.bill.fillback.service;

import com.ydzbinfo.emis.trainRepair.bill.model.bill.*;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChecklistSummary;

import java.util.List;

/**
 * @Description:
 * @Data: 2021/7/31
 * @Author: 韩旭
 */
public interface ICheckInfoService {

    ChecklistSummaryInfoForShow getDetailInfoShow(OneTwoRepairCheckList checklistSummary);

    ChecklistSummary getChecklistSummaryBySummaryID(String summaryID);
    /**
     * @author: 韩旭
     * @date: 2021/8/12
     * 工长签字
     */
    ChecklistTriggerUrlCallResult setOneTwoRepairForemanSign(ChecklistSummaryInfoForSave saveInfo);

    /**
     * @author: 韩旭
     * @date: 2021/8/12
     * 质检签字
     */
    ChecklistTriggerUrlCallResult setOneTwoRepairQualitySign(ChecklistSummaryInfoForSave saveInfo);

    //批量工长签字
    SignMessage setOneTwoRepairAllForemanSign(List<OneTwoRepairCheckList> summaryList);
    //批量质检签字
    SignMessage setOneTwoRepairAllQualitySign(List<OneTwoRepairCheckList> summaryList);
    //记录单跳转故障后的故障录入信息
    String faultJumpByChecklist(FaultJumpByChecklist faultJumpByChecklist);
    //故障跳转
    ChecklistTriggerUrlCallResult faultJump(ChecklistSummaryInfoForSave saveInfo);
    //保存一二级修记录单数据
    ChecklistTriggerUrlCallResult saveOneTwoRepairCell(ChecklistSummaryInfoForSave saveInfo);
    //检修工人签字
    ChecklistTriggerUrlCallResult repairPersonSign(ChecklistSummaryInfoForSave saveInfo);
    //导入数据
    ChecklistTriggerUrlCallResult ImportData(ChecklistSummary checklistSummary);
    //获取更改的确认值单元格
    ChecklistTriggerUrlCallResult changeStateCells(ChecklistSummaryInfoForSave checklistSummary);
    //保存镟修探伤的镟轮数据
    ChecklistTriggerUrlCallResult saveWheelDatas(ChecklistSummaryInfoForSave checklistSummary);
    //设置检修人员信息
    ChecklistTriggerUrlCallResult setRepairPerson(ChecklistSummaryInfoForSave checklistSummary);
    //确认人签字
    ChecklistTriggerUrlCallResult confirmPerson(ChecklistSummaryInfoForSave checklistSummary);
    //机检技术员签字
    ChecklistTriggerUrlCallResult signTechnique(ChecklistSummaryInfoForSave checklistSummary);
    //镟修探伤轴质检签字
    ChecklistTriggerUrlCallResult setAxleQualitySign(ChecklistSummaryInfoForSave saveInfo);
    //校验单据是否回填
    String checkIsChangeed(ChecklistSummaryInfoForSave saveInfo);
}
