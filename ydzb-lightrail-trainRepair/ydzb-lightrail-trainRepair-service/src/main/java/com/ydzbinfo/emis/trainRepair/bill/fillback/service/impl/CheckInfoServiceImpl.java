package com.ydzbinfo.emis.trainRepair.bill.fillback.service.impl;

import com.ydzbinfo.emis.trainRepair.bill.fillback.dao.ChecklistSummaryMapper;
import com.ydzbinfo.emis.trainRepair.bill.fillback.service.*;
import com.ydzbinfo.emis.trainRepair.bill.fillback.service.impl.OneTwoRepairCheckInfoInitPackage.*;
import com.ydzbinfo.emis.trainRepair.bill.fillback.utils.BillCommon;
import com.ydzbinfo.emis.trainRepair.bill.fillback.utils.TemplateTypeNameEnum;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.*;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChecklistFaultRelation;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChecklistSummary;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChecklistWithDetail;
import com.ydzbinfo.emis.trainRepair.common.service.IRepairMidGroundService;
import com.ydzbinfo.emis.utils.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @Description:
 * @Data: 2021/7/31
 * @Author: 韩旭
 */
@Service
public class CheckInfoServiceImpl implements ICheckInfoService {

    protected static final Logger logger = getLogger(CheckInfoServiceImpl.class);
    @Autowired
    private IRepairMidGroundService midGroundService;
    @Autowired
    CheckInfoInit checkInfoInit;
    @Autowired
    FirstPersonKeepAndTest firstPersonKeepAndTest;
    @Autowired
    FirstEquipmentKeep firstEquipmentKeep;
    @Autowired
    SecondPreventive secondPreventive;
    @Autowired
    WheelTask wheelTask;
    @Autowired
    LUAxleTask luAxleTask;
    @Autowired
    InspectionAxleTask inspectionAxleTask;
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    ChecklistSummaryMapper checklistSummaryMapper;
    @Autowired
    IChecklistFaultRelationService iChecklistFaultRelationService;
    @Autowired
    IChecklistAreaService checklistAreaService;
    @Autowired
    IChecklistDetailService checklistDetailService;
    @Autowired
    IChecklistLinkControlService checklistLinkControlService;
    @Autowired
    IChkDetailLinkContentService chkDetailLinkContentService;
    @Autowired
    BillCommon billCommon;
    @Override
    /**
     * @author: 韩旭
     * @date: 2021/8/12
     * 组织一二级修记录单明细数据
     */
    public ChecklistSummaryInfoForShow getDetailInfoShow(OneTwoRepairCheckList checklistSummary) {
        ChecklistSummaryInfoForShow summaryInfoForShow = new ChecklistSummaryInfoForShow();
        try {
            //组织主表数据
            ChecklistSummary summary = checklistSummaryMapper.selectById(checklistSummary.getChecklistSummaryId());
            //如果记录单总表没有数据
            if (summary == null || StringUtils.hasText(summary.getChecklistSummaryId()) == false) {
                logger.error("/CheckInfoInit/getDetailInfoShow! 主键:" + checklistSummary.getChecklistSummaryId());
                return summaryInfoForShow;
            }
            ChecklistWithDetail detail = checkInfoInit.getDetailsBySummaryID(checklistSummary.getChecklistSummaryId());
            boolean isInitData = false;
            //没有明细，需要重新组织数据
            if (detail.getContents() == null || detail.getContents().size() == 0) {
                isInitData = true;
            }
            summaryInfoForShow = checkInfoInit.getDetailInfoShow(checklistSummary.getChecklistSummaryId(),checklistSummary.getTemplateId(),checklistSummary.getTemplateType(),true);
            summaryInfoForShow.setExtraObject(getChecklistSummaryBySummaryID(checklistSummary.getChecklistSummaryId()));
            if(isInitData)
            {
                checkInfoInit.initContentData(summaryInfoForShow, checklistSummary.getTemplateType());
            }
            checkInfoInit.initReadOnly(summaryInfoForShow);
            if(isInitData) {
                checkInfoInit.initAllotBackFill(summaryInfoForShow);
            }
            checkInfoInit.setReadOnly(summaryInfoForShow.getCells());
            if(summary != null)
            {
                InitContentRule(summaryInfoForShow, summary.getTemplateType());
            }
        } catch (Exception ex) {
            logger.error("/CheckInfoServiceImpl/getDetailInfoShow! 主键:" + checklistSummary.getChecklistSummaryId(), ex);
        }
        return summaryInfoForShow;
    }



    public void InitContentRule(ChecklistSummaryInfoForShow summaryInfoForShow,String templateType)
    {
        if (TemplateTypeNameEnum.RECORD_FIRST_PERSON_TEST.getValue().equals(templateType) || TemplateTypeNameEnum.RECORD_FIRST_PERSON_KEEP.getValue().equals(templateType)) {
        //    firstPersonKeepAndTest.initContentData(summaryInfoForShow);
            firstPersonKeepAndTest.initContentRule(summaryInfoForShow);
        } else if (TemplateTypeNameEnum.RECORD_SECOND_PREVENTIVE.getValue().equals(templateType)) {
        //    secondPreventive.initContentData(summaryInfoForShow);
            secondPreventive.initContentRule(summaryInfoForShow);
        } else if (TemplateTypeNameEnum.RECORD_SECOND_WHEEL.getValue().equals(templateType)) {
        //    wheelTask.initContentData(summaryInfoForShow);
            wheelTask.initContentRule(summaryInfoForShow);
        } else if (TemplateTypeNameEnum.RECORD_SECOND_AXLEEDDY.getValue().equals(templateType)) {
        //    inspectionAxleTask.initContentData(summaryInfoForShow);
            inspectionAxleTask.initContentRule(summaryInfoForShow);
        } else if (TemplateTypeNameEnum.RECORD_SECOND_LUEDDY.getValue().equals(templateType)) {
        //    luAxleTask.initContentData(summaryInfoForShow);
            luAxleTask.initContentRule(summaryInfoForShow);
        }else if(TemplateTypeNameEnum.RECORD_FIRST_EQUIPMENT_KEEP.getValue().equals(templateType)){
            firstEquipmentKeep.initContentRule(summaryInfoForShow);
        }
    }



    /**
     * @author: 韩旭
     * @date: 2021/8/12
     * 质检签字
     */
    @Override
    public ChecklistTriggerUrlCallResult setOneTwoRepairQualitySign(ChecklistSummaryInfoForSave saveInfo) {
        try {
            ChecklistTriggerUrlCallResult result = new ChecklistTriggerUrlCallResult();
            boolean isDefultSave = true; //是否使用默认保存方法
            if(isDefultSave == true) {
                result =  checkInfoInit.setOneTwoRepairQualitySign(saveInfo);
            }
            return result;
        } catch (Exception exc) {
            logger.error("质检签字报错", exc);
            return null;
        }
    }

    //批量工长签字

    /**
     * @author: 韩旭
     * @date: 2021/8/12
     * 工长签字
     */
    @Override
    public ChecklistTriggerUrlCallResult setOneTwoRepairForemanSign(ChecklistSummaryInfoForSave saveInfo) {
        try {
            ChecklistTriggerUrlCallResult result = new ChecklistTriggerUrlCallResult();
            boolean isDefultSave = true; //是否使用默认方法
            if(isDefultSave == true) {
                result =  checkInfoInit.setOneTwoRepairForemanSign(saveInfo);
            }
            if(TemplateTypeNameEnum.RECORD_SECOND_WHEEL.getValue().equals(saveInfo.getExtraObject().getTemplateType())){
                saveWheelDatas(saveInfo);
            }
            return result;
        } catch (Exception exc) {
            logger.error("工长签字报错", exc);
            return null;
        }
    }
    @Override
    public SignMessage setOneTwoRepairAllForemanSign(List<OneTwoRepairCheckList> summaryList) {
        SignMessage msg =  new SignMessage();
        try {
            boolean isDefultSave = true; //是否使用默认保存方法
            if (isDefultSave == true) {
                msg = checkInfoInit.setOneTwoRepairAllForemanSign(summaryList);
            }
        } catch (Exception exc) {
            logger.error("批量工长签字报错!", exc);
            msg.setCode("-1");
            msg.setMsg("批量工长签字报错！");
        }
        return msg;
    }


    //批量质检签字
    @Override
    public SignMessage setOneTwoRepairAllQualitySign(List<OneTwoRepairCheckList> summaryList) {
        SignMessage msg =  new SignMessage();
        try {
            boolean isDefultSave = true; //是否使用默认保存方法
            if (isDefultSave == true) {
                msg = checkInfoInit.setOneTwoRepairAllQualitySign(summaryList);
            }
        } catch (Exception exc) {
            logger.error("批量质检签字报错!", exc);
            msg.setCode("-1");
            msg.setMsg("批量质检签字报错！");
        }
        return msg;
    }
    @Override
    //
    public String faultJumpByChecklist(FaultJumpByChecklist faultJumpByChecklist)
    {
        try {
            ChecklistFaultRelation checklistFaultRelation = new ChecklistFaultRelation();
            BeanUtils.copyProperties(faultJumpByChecklist, checklistFaultRelation);
            checklistFaultRelation.setId(UUID.randomUUID().toString());
            //获取当前时间
            Date currentDate = new Date();
            checklistFaultRelation.setCreateTime(currentDate);
            iChecklistFaultRelationService.insert(checklistFaultRelation);
            return "";
        }catch (Exception ex)
        {
            throw ex;
        }
    }
    @Override
    //故障跳转
    public ChecklistTriggerUrlCallResult faultJump(ChecklistSummaryInfoForSave saveInfo) {
        try {
            ChecklistSummary summary = saveInfo.getExtraObject();
            ChecklistTriggerUrlCallResult result = new ChecklistTriggerUrlCallResult();
            if (TemplateTypeNameEnum.RECORD_FIRST_PERSON_TEST.getValue().equals(summary.getTemplateType()) || TemplateTypeNameEnum.RECORD_FIRST_PERSON_KEEP.getValue().equals(summary.getTemplateType())) {
                result = firstPersonKeepAndTest.faultJump(saveInfo);
            } else if (TemplateTypeNameEnum.RECORD_SECOND_PREVENTIVE.getValue().equals(summary.getTemplateType())) {
                result = secondPreventive.faultJump(saveInfo);
            }
            return result;
        } catch (Exception exc) {
            logger.error("故障跳转报错", exc);
            return null;
        }
    }

    //保存一二级修记录单
    @Override
    public ChecklistTriggerUrlCallResult saveOneTwoRepairCell(@RequestBody ChecklistSummaryInfoForSave saveInfo) {
        ChecklistTriggerUrlCallResult result = new ChecklistTriggerUrlCallResult();
        try {
            boolean isDefultSave = true; //是否使用默认保存方法
            if (isDefultSave == true) {
                String msg = checkInfoInit.getSaveButtonClickMsg(saveInfo);
                if (StringUtils.isEmpty(msg)) {
                    checkInfoInit.saveCell(saveInfo);
                    //更新所有已经保存数据的状态
                    List<ChecklistDetailInfoForShow> showChangedCells = new ArrayList<>();
                    billCommon.UpdateSavedState(showChangedCells, saveInfo.getCells());
                    result.setChangedCells(showChangedCells);
                } else {
                    result.setOperationResultMessage(msg);
                }
            }
            return result;
        } catch (Exception exc) {
            logger.error("保存一二级修记录单报错", exc);
            return null;
        }
    }

    //检修工人显示
    public ChecklistTriggerUrlCallResult repairPersonSign(ChecklistSummaryInfoForSave saveInfo) {
        try {
            ChecklistSummary summary = saveInfo.getExtraObject();
            ChecklistTriggerUrlCallResult result = new ChecklistTriggerUrlCallResult();
            if (TemplateTypeNameEnum.RECORD_SECOND_PREVENTIVE.getValue().equals(summary.getTemplateType())) {
                result = secondPreventive.repairPersonSign(saveInfo);
            }
            return result;
        } catch (Exception exc) {
            logger.error("检修工人显示报错", exc);
            return null;
        }
    }
    //导入数据
    @Override
    public ChecklistTriggerUrlCallResult ImportData(ChecklistSummary checklistSummary) {
        OneTwoRepairCheckList oneTwoRepairCheckList = new OneTwoRepairCheckList();
        BeanUtils.copyProperties(checklistSummary, oneTwoRepairCheckList);
        ChecklistSummaryInfoForShow checklistSummaryInfoForShow = getDetailInfoShow(oneTwoRepairCheckList);
        if(TemplateTypeNameEnum.RECORD_SECOND_WHEEL.getValue().equals(checklistSummary.getTemplateType()))
            return wheelTask.ImportData(checklistSummaryInfoForShow);
        else if(TemplateTypeNameEnum.RECORD_SECOND_AXLEEDDY.getValue().equals(checklistSummary.getTemplateType()))
            return inspectionAxleTask.ImportData(checklistSummaryInfoForShow);
        else if(TemplateTypeNameEnum.RECORD_SECOND_LUEDDY.getValue().equals(checklistSummary.getTemplateType()))
            return luAxleTask.ImportData(checklistSummaryInfoForShow);
        return null;
    }


    @Override
    //根据ID获取主表信息
    public  ChecklistSummary getChecklistSummaryBySummaryID(String summaryID)
    {
        ChecklistSummary summary = checklistSummaryMapper.selectById(summaryID);
        return summary;
    }

    /**
     * 获取更改的确认值单元格
     * @param checklistSummary 记录单数据
     * @return 更改的确认值单元格
     */
    @Override
    public ChecklistTriggerUrlCallResult changeStateCells(ChecklistSummaryInfoForSave checklistSummary){
        return  wheelTask.changeStateCells(checklistSummary);
    }

    /**
     * 保存镟修探伤的镟轮数据
     * @param checklistSummary 记录单
     * @return
     */
    @Override
    public ChecklistTriggerUrlCallResult saveWheelDatas(ChecklistSummaryInfoForSave checklistSummary){
        return wheelTask.saveWheelDatas(checklistSummary);
    }
    /**
     * 设置检修人员信息
     * @param checklistSummary 镟修、空心轴或LU探伤单据
     * @return
     */
    @Override
    public ChecklistTriggerUrlCallResult setRepairPerson(ChecklistSummaryInfoForSave checklistSummary){
        return checkInfoInit.setRepairPerson(checklistSummary);
    }
    /**
     * 确认人签字
     * @param checklistSummary 镟修记录单
     * @return
     */
    @Override
    public ChecklistTriggerUrlCallResult confirmPerson(ChecklistSummaryInfoForSave checklistSummary){
        return checkInfoInit.confirmPerson(checklistSummary);
    }
    /**
     * 机检记录单技术人员签字
     * @param checklistSummary 机检记录单
     * @return
     */
    @Override
    public  ChecklistTriggerUrlCallResult signTechnique(ChecklistSummaryInfoForSave checklistSummary){
        return  checkInfoInit.signTechnique(checklistSummary);
    }

    /**
     *  镟修探伤单据轴质检签字
     * @param saveInfo 镟修记录单
     * @return
     */
    @Override
    public ChecklistTriggerUrlCallResult setAxleQualitySign(ChecklistSummaryInfoForSave saveInfo){
        return checkInfoInit.setAxleQualitySign(saveInfo);
    }

    /**
     * 校验单据是否发生修改
     * @param saveInfo 记录单信息
     * @return
     */
    @Override
    public String checkIsChangeed(ChecklistSummaryInfoForSave saveInfo){
        return checkInfoInit.checkIsChangeed(saveInfo);
    }
}

