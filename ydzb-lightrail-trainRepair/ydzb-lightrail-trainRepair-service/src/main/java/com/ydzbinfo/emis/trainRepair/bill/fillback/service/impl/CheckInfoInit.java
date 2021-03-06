package com.ydzbinfo.emis.trainRepair.bill.fillback.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import com.ydzbinfo.emis.trainRepair.bill.fillback.dao.*;
import com.ydzbinfo.emis.trainRepair.bill.fillback.service.*;
import com.ydzbinfo.emis.trainRepair.bill.fillback.service.impl.OneTwoRepairCheckInfoInitPackage.*;
import com.ydzbinfo.emis.trainRepair.bill.fillback.utils.AttributeEnum;
import com.ydzbinfo.emis.trainRepair.bill.fillback.utils.BillCommon;
import com.ydzbinfo.emis.trainRepair.bill.fillback.utils.MainCycEnum;
import com.ydzbinfo.emis.trainRepair.bill.fillback.utils.TemplateTypeNameEnum;
import com.ydzbinfo.emis.trainRepair.bill.general.BillCellChangeInfo;
import com.ydzbinfo.emis.trainRepair.bill.general.UrlInfo;
import com.ydzbinfo.emis.trainRepair.bill.general.constant.BillCellTriggerTimingEnum;
import com.ydzbinfo.emis.trainRepair.bill.general.constant.BillCellTriggerUrlTypeEnum;
import com.ydzbinfo.emis.trainRepair.bill.general.constant.BillEntityChangeTypeEnum;
import com.ydzbinfo.emis.trainRepair.bill.model.*;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.*;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateAttribute;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.*;
import com.ydzbinfo.emis.trainRepair.common.model.ConfigParamsModel;
import com.ydzbinfo.emis.trainRepair.common.querymodel.Log;
import com.ydzbinfo.emis.trainRepair.common.querymodel.XzyCConfig;
import com.ydzbinfo.emis.trainRepair.common.service.ILogService;
import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.trainRepair.common.service.IRepairMidGroundService;
import com.ydzbinfo.emis.trainRepair.common.service.impl.RepairMidGroundServiceImpl;
import com.ydzbinfo.emis.trainRepair.constant.BillTemplateStateEnum;
import com.ydzbinfo.emis.trainRepair.remotemodel.item.RepairItemInfo;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetBaseInfo;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.ZtTaskItemEntity;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.ZtTaskPacketEntity;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.runRouting.LeaveBackTrainNoResult;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.RepairPacketStatu;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.TaskAllotItemEntity;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.TaskAllotPacketEntity;
import com.ydzbinfo.emis.trainRepair.workprocess.model.WorkTime;
import com.ydzbinfo.emis.utils.*;
import com.ydzbinfo.emis.utils.result.RestRequestException;
import org.apache.poi.util.StringUtil;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.w3c.dom.Attr;

import java.util.*;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * <p>
 * ??????????????????????????????????????????
 * </p>
 *
 * @author ??????
 * @since 2021-08-23
 */
@Service
public class CheckInfoInit {
    protected static final Logger logger = getLogger(CheckInfoInit.class);
    @Autowired
    BillCommon billCommon;
    @Autowired
    ILogService iLogService;
    @Autowired
    private IRemoteService iRemoteService;
    @Autowired
    private IRepairMidGroundService midGroundService;
    @Autowired
    IChecklistAreaService checklistAreaService;
    @Autowired
    IChecklistDetailService checklistDetailService;
    @Autowired
    IChecklistLinkControlService checklistLinkControlService;
    @Autowired
    IChkDetailLinkContentService chkDetailLinkContentService;
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    AbnormalHandlerLogMapper abnormalHandlerLogMapper;
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    ChklstsummaryLv1OldMapper chklstsummaryLv1OldMapper;
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    ChecklistSummaryOldMapper checklistSummaryOldMapper;

    @Autowired
    IChecklistSummaryService checklistSummaryService;
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
    RepairStatNewMapper repairStatNewMapper;
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    ChecklistSummaryMapper checklistSummaryMapper;
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    ChecklistDetailMapper checklistDetailMapper;
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    ChecklistAreaMapper checklistAreaMapper;
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    ChecklistLinkControlMapper checklistLinkControlMapper;
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    ChkDetailLinkContentMapper chkDetailLinkContentMapper;
    @Autowired
    RepairMidGroundServiceImpl repairMidGroundService;

    //??????????????????
    private ChecklistSummaryInfoForShow convertChecklistSummaryInfoForShow(ChecklistWithDetail detail) {
        ChecklistSummaryInfoForShow info = new ChecklistSummaryInfoForShow();
        if (detail != null) {
         //   info.setExtraObject(summary);
            info.setShowImportButton(false);
            List<ChecklistAreaInfoForShow> areaInfoForShows = new ArrayList<>();
            for (ChecklistArea checklistArea : detail.getAreas()) {
                ChecklistAreaInfoForShow areaInfoForShow = new ChecklistAreaInfoForShow();
                BeanUtils.copyProperties(checklistArea, areaInfoForShow);
                areaInfoForShows.add(areaInfoForShow);
            }
            info.setAreas(areaInfoForShows);
            List<ChecklistDetailInfoForShow> detailInfoForShows = new ArrayList<>();
            for (ChecklistContentWithDetail contentWithDetail : detail.getContents()) {
                ChecklistDetailInfoForShow checklistDetailInfoForShow = new ChecklistDetailInfoForShow();
                BeanUtils.copyProperties(contentWithDetail, checklistDetailInfoForShow);
                //??????ChkDetailLinkContent??????
                List<ChkDetailLinkContent> linkContents = new ArrayList<>();
                for (ChkDetailLinkContent detailLinkContent : contentWithDetail.getLinkContents()) {
                    ChkDetailLinkContent linkContent = new ChkDetailLinkContent();
                    BeanUtils.copyProperties(detailLinkContent, linkContent);
                    linkContents.add(linkContent);
                }
                contentWithDetail.setLinkContents(linkContents);
                List<ChecklistLinkControl> linkControls = new ArrayList<>();
                for (ChecklistLinkControl checklistLinkControl : contentWithDetail.getControls()) {
                    ChecklistLinkControl linkControl = new ChecklistLinkControl();
                    BeanUtils.copyProperties(checklistLinkControl, linkControl);
                    linkControls.add(linkControl);
                }
                contentWithDetail.setControls(linkControls);
                detailInfoForShows.add(checklistDetailInfoForShow);
            }
            info.setCells(detailInfoForShows);
        }
        return info;
    }


    public ChecklistWithDetail getDetailsBySummaryID(String summaryID) {
        ChecklistWithDetail withDetail = new ChecklistWithDetail();
        //??????SUMMARYID????????????
        Map<String, Object> map = new HashMap<>();
        map.put("S_CHECKLISTSUMMARYID", summaryID);
        List<ChecklistDetail> selectDetails = checklistDetailService.selectByMap(map);
        if (selectDetails.size() > 0) {
            List<ChecklistArea> areas = checklistAreaService.selectByMap(map);
            List<ChkDetailLinkContent> linkContents = chkDetailLinkContentService.selectByMap(map);
            List<ChecklistLinkControl> linkControls = checklistLinkControlService.selectByMap(map);
            withDetail.setAreas(areas);
            List<ChecklistContentWithDetail> contentWithDetails = new ArrayList<>();
            for (ChecklistDetail selectDetail : selectDetails) {
                ChecklistContentWithDetail contentWithDetail = new ChecklistContentWithDetail();
                BeanUtils.copyProperties(selectDetail, contentWithDetail);
                contentWithDetail.setControls(linkControls.stream()
                    .filter(t -> t.getContentId().equals(selectDetail.getId())).collect(Collectors.toList()));
                contentWithDetail.setLinkContents(linkContents.stream()
                    .filter(t -> t.getContentId().equals(selectDetail.getId())).collect(Collectors.toList()));
                contentWithDetails.add(contentWithDetail);
            }
            withDetail.setContents(contentWithDetails);
        }
        return withDetail;
    }

    public ChecklistSummaryInfoForShow getDetailInfoShow(String checklistSummaryID, String templateID, String templateType,boolean isSaved) {
        ChecklistSummaryInfoForShow summaryInfoForShow = new ChecklistSummaryInfoForShow();
        try {
            //????????????
            //??????????????????????????????????????????????????????
            //?????????????????????????????????????????????

            ChecklistWithDetail detail = getDetailsBySummaryID(checklistSummaryID);
            //???????????????????????????????????????
            if (detail.getContents() == null || detail.getContents().size() == 0) {
                initDetailInfoShow(detail, checklistSummaryID, templateID, templateType,isSaved);
                //????????????
                summaryInfoForShow = convertChecklistSummaryInfoForShow(detail);
            } else {
                //????????????
                summaryInfoForShow = convertChecklistSummaryInfoForShow(detail);
            }

        } catch (Exception ex) {
            logger.error("/CheckInfoInit/getDetailInfoShow! ??????:" + checklistSummaryID, ex);
        }
        return summaryInfoForShow;
    }



    public void initContentData(ChecklistSummaryInfoForShow summaryInfoForShow, String templateType) {
        try {
            List<ChecklistDetailInfoForShow> initDetails = null;
            if (templateType.equals(TemplateTypeNameEnum.RECORD_FIRST_PERSON_TEST.getValue()) || templateType.equals(TemplateTypeNameEnum.RECORD_FIRST_PERSON_KEEP.getValue())) {
                initDetails = firstPersonKeepAndTest.initContentData(summaryInfoForShow);
            } else if (templateType.equals(TemplateTypeNameEnum.RECORD_SECOND_PREVENTIVE.getValue())) {
                initDetails = secondPreventive.initContentData(summaryInfoForShow);
            } else if (templateType.equals(TemplateTypeNameEnum.RECORD_SECOND_WHEEL.getValue()) || templateType.equals(TemplateTypeNameEnum.RECORD_TEMP_CELL.getValue())) {
                initDetails = wheelTask.initContentData(summaryInfoForShow);
            } else if (templateType.equals(TemplateTypeNameEnum.RECORD_SECOND_AXLEEDDY.getValue())) {
                initDetails = inspectionAxleTask.initContentData(summaryInfoForShow);
            } else if (templateType.equals(TemplateTypeNameEnum.RECORD_SECOND_LUEDDY.getValue())) {
                initDetails = luAxleTask.initContentData(summaryInfoForShow);
            }else if(templateType.equals(TemplateTypeNameEnum.RECORD_FIRST_EQUIPMENT_KEEP.getValue())){
                initDetails = firstEquipmentKeep.initContentData(summaryInfoForShow);
            }
            if (initDetails != null && initDetails.size() > 0) {
                List<ChecklistDetail> details = new ArrayList<>();
                for (ChecklistDetailInfoForShow initDetail : initDetails
                    ) {
                    ChecklistDetail detail = new ChecklistDetail();
                    BeanUtils.copyProperties(initDetail, detail);
                    details.add(detail);
                }
                //????????????
                checklistDetailService.updateByPrimaryKey(details);
            }
            ChecklistSummary summary = summaryInfoForShow.getExtraObject();
            if(!StringUtils.hasText(summary.getAccuMile())){
                String accumile = "";
                if (initDetails != null && initDetails.size() > 0){
                    for(ChecklistDetailInfoForShow cell : initDetails){
                        if(cell.getAttributeCode().equals(AttributeEnum.ATTR_REPAIR_ACCUMILE.getValue())){
                            accumile = cell.getValue();
                            break;
                        }
                    }
                }
                if(!StringUtils.hasText(accumile)){
                    accumile = billCommon.getAccumile(summary.getTrainsetId(), summary.getDayPlanId()) + "";
                }
                summary.setAccuMile(accumile);
                if(StringUtils.hasText(summary.getAccuMile())){
                    checklistSummaryService.updateById(summary);
                }
            }
        } catch (Exception ex) {
            logger.error("/CheckInfoInit/InitContentData! ??????:" + summaryInfoForShow.getExtraObject().getChecklistSummaryId(), ex);
        }
    }

    /**
     * @author: ??????
     * @date: 2021/8/12
     * ?????????????????????
     */
   synchronized  public void initDetailInfoShow(ChecklistWithDetail detail, String summaryID, String templateID, String templateType,Boolean isSaved) {
        try {
            //??????templateId????????????
            List<TemplateAll> templateAlls = midGroundService.getBillTemplateDetail(templateID, BillTemplateStateEnum.RELEASED);
            if (templateAlls.size() > 0) {
                TemplateAll templateAll = templateAlls.get(0);
                //??????areas
                String summaryId = summaryID;
                List<ChecklistArea> areas = new ArrayList<>();
                for (TemplateAllArea allArea : templateAll.getAreas()) {
                    ChecklistArea area = new ChecklistArea();
                    BeanUtils.copyProperties(allArea, area);
                    area.setChecklistSummaryId(summaryId);
                    String uuid = UUID.randomUUID().toString();
                    area.setId(uuid);
                    areas.add(area);
                }
                detail.setAreas(areas);
                if(isSaved) {
                    if (areas.size() > 0) {
                        Map<String, Object> deleteMap = new HashMap<>();
                        deleteMap.put("S_CHECKLISTSUMMARYID", summaryId);
                        checklistAreaService.deleteByMap(deleteMap);
                        checklistAreaService.insertBatch(areas);
                    }
                }
                //??????contents
                List<ChecklistContentWithDetail> contents = new ArrayList<>();
                //??????ChecklistDetail???????????????
                List<ChecklistDetail> checklistDetailList = new ArrayList<>();
                for (TemplateAllContent templateAllContent : templateAll.getContents()) {
                    ChecklistContentWithDetail content = new ChecklistContentWithDetail();
                    BeanUtils.copyProperties(templateAllContent, content);
                    content.setValue(templateAllContent.getContent());
                    String detailId = templateAllContent.getId();
                    content.setId(detailId);
                    content.setChecklistSummaryId(summaryId);
                    content.setTemplateId(templateAllContent.getTemplateId());
                    content.setTemplateType(templateType);
                    //??????ChkDetailLinkContent??????
                    List<ChkDetailLinkContent> linkContents = new ArrayList<>();
                    for (TemplateAllLinkContent templateAllLinkContent : templateAllContent.getLinkContents()) {
                        ChkDetailLinkContent linkContent = new ChkDetailLinkContent();
                        BeanUtils.copyProperties(templateAllLinkContent, linkContent);
                        String uuidlinkContent = UUID.randomUUID().toString();
                        linkContent.setId(uuidlinkContent);
                        linkContent.setChecklistSummaryId(summaryId);
                        linkContents.add(linkContent);
                    }
                    content.setLinkContents(linkContents);
                    if(isSaved) {
                        if (linkContents.size() > 0) {
                            chkDetailLinkContentService.insertBatch(linkContents);
                        }
                    }
                    //??????ChecklistLinkControl
                    List<ChecklistLinkControl> linkControls = new ArrayList<>();
                    for (TemplateAllControl templateAllControl : templateAllContent.getControls()) {
                        ChecklistLinkControl linkControl = new ChecklistLinkControl();
                        BeanUtils.copyProperties(templateAllControl, linkControl);
                        String uuidlinkControls = UUID.randomUUID().toString();
                        linkControl.setId(uuidlinkControls);
                        linkControl.setChecklistSummaryId(summaryId);
                        linkControls.add(linkControl);
                    }
                    content.setControls(linkControls);
                    if(isSaved) {
                        if (linkControls.size() > 0) {
                            checklistLinkControlService.insertBatch(linkControls);
                        }
                    }
                    ChecklistDetail checklistDetail = new ChecklistDetail();
                    BeanUtils.copyProperties(content, checklistDetail);
                    checklistDetailList.add(checklistDetail);
                    contents.add(content);
                }
                detail.setContents(contents);
                if(isSaved) {
                    if (checklistDetailList.size() > 0) {
                        checklistDetailService.insertBatch(checklistDetailList);
                    }
                }
            }
        } catch (Exception exc) {
            logger.error("???????????????????????????! ??????:" + summaryID, exc);
        }
    }

    /**
     * ???????????????,????????????????????????
     */
    public List<ChecklistDetailInfoForShow> initContentData(ChecklistSummaryInfoForShow content) {
        //???????????????
        List<ChecklistDetailInfoForShow> contentList = content.getCells();
        //??????????????????
        List<ChecklistDetailInfoForShow> returnUpdateDetail = new ArrayList<>();
        ChecklistSummary summary = content.getExtraObject();
        RepairItemInfo repairItem = billCommon.getItemInfoByCode(summary.getItemCode(), summary.getUnitCode());
        for (ChecklistDetailInfoForShow detail : contentList) {
            if (detail.getAttributeCode().equals(AttributeEnum.ATTR_ALL_DEPT.getValue())) {
                String depot = UserUtil.getUserInfo().getDepot().getName();
                detail.setValue(depot + summary.getUnitName());
                returnUpdateDetail.add(detail);
            } else if (detail.getAttributeCode().equals(AttributeEnum.ATTR_REPAIR_DATE.getValue())) {
                detail.setValue(summary.getDayPlanId().substring(0, 10));
                returnUpdateDetail.add(detail);
            } else if (detail.getAttributeCode().equals(AttributeEnum.ATTR_TRAINSETID.getValue())) {
                TrainsetBaseInfo info = billCommon.getTrainsetInfo(summary.getTrainsetId());
                detail.setValue(info.getTrainsetname());
                returnUpdateDetail.add(detail);
            } else if (detail.getAttributeCode().equals(AttributeEnum.ATTR_IN_OUT_TRAINNO.getValue())) {
                //???????????????  ????????????/????????????
                List<ChecklistDetailInfoForShow> resultTrainsetId = contentList.stream().
                    filter((ChecklistDetailInfoForShow t) -> t.getAttributeCode().equals(AttributeEnum.ATTR_TRAINSETID.getValue())).
                    collect(Collectors.toList());
                String dayPlanId = summary.getDayPlanId();
                if (resultTrainsetId.size() > 0) {
                    String trainsetId = resultTrainsetId.get(0).getValue();
                    String querydate = dayPlanId.substring(0, 4) +
                        dayPlanId.substring(5, 7) + dayPlanId.substring(8, 10);
                    LeaveBackTrainNoResult leaveBackTrainNoResult = billCommon.getTrainsetLeaveBack(trainsetId, querydate);
                    if (leaveBackTrainNoResult != null) {
                        String leaveBack = leaveBackTrainNoResult.getBackTrainNo() + "/" + leaveBackTrainNoResult.getDepTrainNo();
                        detail.setValue(leaveBack);
                        returnUpdateDetail.add(detail);
                    }
                }
            } else if (detail.getAttributeCode().equals(AttributeEnum.ATTR_REPAIR_DEPT.getValue())) {
                detail.setValue(summary.getDeptName());
                returnUpdateDetail.add(detail);
            } else if (detail.getAttributeCode().equals(AttributeEnum.ATTR_REPAIR_PERSON_SHOW.getValue())) {
                detail.setValue(summary.getFillWorkName());
                returnUpdateDetail.add(detail);
            } else if (detail.getAttributeCode().equals(AttributeEnum.ATTR_REPAIR_ACCUMILE.getValue())) {
                detail.setValue(String.valueOf(billCommon.getAccumile(summary.getTrainsetId(), summary.getDayPlanId())));
                returnUpdateDetail.add(detail);
            }else if (detail.getAttributeCode().equals(AttributeEnum.ATTR_REPAIR_PART.getValue()) && repairItem != null) {
                detail.setValue(repairItem.getStrPartsTypeName());
                returnUpdateDetail.add(detail);
            }else if (detail.getAttributeCode().equals(AttributeEnum.ATTR_REPAIR_SYSTEM.getValue()) && repairItem != null) {
                detail.setValue(repairItem.getFuncSysName());
                returnUpdateDetail.add(detail);
            }else if (detail.getAttributeCode().equals(AttributeEnum.ATTR_REPAIR_CYCLE.getValue()) && repairItem != null) { //????????????
                detail.setValue(billCommon.getItemCycle(repairItem.getItemCycleVos()));
                returnUpdateDetail.add(detail);
            }else if (detail.getAttributeCode().equals(AttributeEnum.ATTR_REPAIR_CARD.getValue()) && repairItem != null) {  //????????????
                detail.setValue(repairItem.getMainTainCardNo());
                returnUpdateDetail.add(detail);
            }else if (detail.getAttributeCode().equals(AttributeEnum.ATTR_REPAIR_ITEM.getValue())) {  //????????????
                if(repairItem != null){
                    detail.setValue(repairItem.getItemName());
                }else{
                    detail.setValue(summary.getItemName());
                }
                returnUpdateDetail.add(detail);
            }else if (detail.getAttributeCode().equals(AttributeEnum.ATTR_REPAIR_MAINCYC.getValue())) {   //??????
                for(MainCycEnum main : MainCycEnum.values()){
                    if(summary.getMainCyc().equals(main.getMainCyc().getTaskRepairCode())){
                        detail.setValue(main.getMainCyc().getTaskRepairName());
                        break;
                    }
                }
                //detail.setValue(summary.getMainCyc());
                returnUpdateDetail.add(detail);
            }else if (detail.getAttributeCode().equals(AttributeEnum.ATTR_QUALITY_CONTROL.getValue()) && repairItem != null) {  //??????????????????
                detail.setValue("");
                returnUpdateDetail.add(detail);
            }
        }
        return returnUpdateDetail;
    }

    /**
     * ?????????????????????
     *
     * @param checklistSummaryInfoForShow
     */
    public void initReadOnly(ChecklistSummaryInfoForShow checklistSummaryInfoForShow) {
        List<ChecklistDetailInfoForShow> cells = checklistSummaryInfoForShow.getCells();
        ChecklistSummary summary = checklistSummaryInfoForShow.getExtraObject();
        boolean isConfig = false;
        if (summary.getTemplateType().equals(TemplateTypeNameEnum.RECORD_SECOND_WHEEL.getValue())) {
            for (ChecklistDetailInfoForShow cell : cells) {
                if (cell.getAttributeCode().equals(AttributeEnum.ATTR_CONFIRM_PERSON.getValue()) && !StringUtils.isEmpty(cell.getValue())) {
                    isConfig = true;
                    setSignReadOnly(cell);
                    cell.setReadOnly(true);
                    break;
                }
            }
        }
        for (ChecklistDetailInfoForShow cell : cells) {
            if (StringUtils.isEmpty(cell.getValue()))
                continue;
            if (cell.getValue().equals("N/A")) {
                cell.setReadOnly(true);
            } else {
                cell.setReadOnly(false);
            }
            if (isConfig) {
                if (cell.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_TYZLJC.getValue()) ||
                        cell.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_TYZXJLJC.getValue()) ||
                        cell.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_TYCLLJC.getValue()) ||
                        cell.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_TCTYZXJLJC.getValue()) ||
                        cell.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_DCTYZXJLJC.getValue()) ||
                        cell.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_DCTYCLLJC.getValue()) ||
                        cell.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_TCTYCLLJC.getValue()) ||
                        cell.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_TYCLDYNCLJLJC.getValue()) ||
                        cell.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_TYDCNLTDLLDLJC.getValue()) ||
                        cell.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_XLCXLJC.getValue()) ||
                        cell.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_DCTYLDLJC.getValue()) ||
                        cell.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_TCTYLDLJC.getValue()) ||
                        cell.getAttributeCode().equals(AttributeEnum.ATTR_DEFECT_CLEAR.getValue())) {
                    cell.setReadOnly(true);
                    cell.setTip("???????????????????????????????????????");
                }
                if (cell.getAttributeCode().equals(AttributeEnum.ATTR_CONFIRM_PERSON.getValue())) {
                    cell.setReadOnly(true);
                }
            }
            if (cell.getAttributeCode().equals(AttributeEnum.ATTR_FOREMAN_SIGN.getValue()) ||
                    cell.getAttributeCode().equals(AttributeEnum.ATTR_QUALITY_SIGN.getValue())) {
                setSignReadOnly(cell);
            }
        }
        if (summary.getTemplateType().equals(TemplateTypeNameEnum.RECORD_FIRST_EQUIPMENT_KEEP.getValue())) {
            List<ChecklistDetailInfoForShow> technicianCell = cells.stream().filter(c -> c.getAttributeCode().equals(AttributeEnum.ATTR_CHEEK_TECHNIQUE_SIGN.getValue())).collect(Collectors.toList());
            if (technicianCell.size() > 0 && !StringUtils.isEmpty(technicianCell.get(0).getValue())) {
                for (ChecklistDetailInfoForShow cell : cells) {
                    cell.setReadOnly(true);
                    if (cell.getAttributeCode().equals(AttributeEnum.ATTR_CHEEK_CONDITION.getValue()) ||
                            cell.getAttributeCode().equals(AttributeEnum.ATTR_CHEEK_RESULT.getValue()) ||
                            cell.getAttributeCode().equals(AttributeEnum.ATTR_CHEEK_CHEEKPERSON.getValue()) ||
                            cell.getAttributeCode().equals(AttributeEnum.ATTR_CHEEK_DISPOSALRESULT.getValue()) ||
                            cell.getAttributeCode().equals(AttributeEnum.ATTR_CHEEK_DISPOSALPERSON.getValue()) ||
                            cell.getAttributeCode().equals(AttributeEnum.ATTR_CHEEK_REMARK.getValue()) ||
                            cell.getAttributeCode().equals(AttributeEnum.ATTR_CHEEK_FINISHSTATE.getValue()) ||
                            cell.getAttributeCode().equals(AttributeEnum.ATTR_CHEEK_DISPATCH_SIGN.getValue()) ||
                            cell.getAttributeCode().equals(AttributeEnum.ATTR_CHEEK_TECHNIQUE_SIGN.getValue())) {
                        cell.setTip("???????????????????????????????????????");
                    }
                }
            }
        }
    }

    /**
     * ?????????????????????????????????????????????????????????????????????????????????
     * @param cells ???????????????
     */
    public void setReadOnly(List<ChecklistDetailInfoForShow> cells){
        //?????????????????????????????????????????????????????????????????????????????????
        List<ChecklistDetailInfoForShow> signCells = cells.stream().filter(f->f.getAttributeCode().equals(AttributeEnum.ATTR_QUALITY_SIGN.getValue()) ||
                f.getAttributeCode().equals(AttributeEnum.ATTR_FOREMAN_SIGN.getValue())).collect(Collectors.toList());
        boolean readOnly = false;
        for(ChecklistDetailInfoForShow cell : signCells){
            if(StringUtils.hasText(cell.getValue())){
                readOnly = true;
                cell.setReadOnly(isHasValue(cell.getCode(), ShiroKit.getUser().getStaffId()));
            }
        }
        //????????????????????????????????????????????????????????????????????????????????????
        if(readOnly){
            for(ChecklistDetailInfoForShow cell : cells){
                if(cell.getAttributeCode().equals(AttributeEnum.ATTR_QUALITY_AXLESIGN.getValue()) ||
                        cell.getAttributeCode().equals(AttributeEnum.ATTR_QUALITY_SIGN.getValue()) ||
                        cell.getAttributeCode().equals(AttributeEnum.ATTR_FOREMAN_SIGN.getValue())){
                    continue;
                }
                cell.setReadOnly(readOnly);
            }
        }
    }

    //???????????????????????????????????????CODE??????????????????????????????
    public void setSignReadOnly(ChecklistDetailInfoForShow detail) {
        //???????????????CODE
        String loginStuffID = ShiroKit.getUser().getStaffId();
        String fillStuffID = detail.getCode();
        if (fillStuffID == null)
            fillStuffID = "";
        //??????CODE
        String[] strList = fillStuffID.split(",");
        int size = Arrays.stream(strList).filter(t -> t.equals(loginStuffID)).collect(Collectors.toList()).size();
        if (size > 0) {
            detail.setTip("????????????,??????????????????");
            detail.setReadOnly(true);
        }
    }

    /**
     * ???????????????
     */
    public void initContentRule(ChecklistSummaryInfoForShow content) {
        //???????????????
        List<ChecklistDetailInfoForShow> contentList = content.getCells();
        for (ChecklistDetailInfoForShow detail : contentList) {
          if (AttributeEnum.ATTR_FOREMAN_SIGN.getValue().equals(detail.getAttributeCode())) {
                UrlInfo triggerUrlInfo = new UrlInfo();
                triggerUrlInfo.setUrlType(BillCellTriggerUrlTypeEnum.FUNCTION);
                triggerUrlInfo.setUrl(billCommon.addApplicationPrefix("/checkInfo/setOneTwoRepairForemanSign"));//??????
                detail.setTriggerUrlInfo(triggerUrlInfo);
                detail.setTriggerTiming(BillCellTriggerTimingEnum.BEFORE_CHANGE);
                setSignReadOnly(detail);
            } else if (AttributeEnum.ATTR_QUALITY_SIGN.getValue().equals(detail.getAttributeCode())) {
                UrlInfo triggerUrlInfo = new UrlInfo();
                triggerUrlInfo.setUrlType(BillCellTriggerUrlTypeEnum.FUNCTION);
                triggerUrlInfo.setUrl(billCommon.addApplicationPrefix("/checkInfo/setOneTwoRepairQualitySign"));//??????
                detail.setTriggerUrlInfo(triggerUrlInfo);
                detail.setTriggerTiming(BillCellTriggerTimingEnum.BEFORE_CHANGE);
                setSignReadOnly(detail);
            }
        }
    }

    /**
     * ??????????????????????????????????????????????????????
     */
    protected String getFillVauleToFaultConfig() {
        ConfigParamsModel configParamsModel = new ConfigParamsModel();
        configParamsModel.setType("10");
        configParamsModel.setName("FillValueToFault");
        List<XzyCConfig> configs = midGroundService.getXzyCConfigs(configParamsModel);
        String checkListSource = "0";
        if (configs.size() > 0) {
            checkListSource = configs.get(0).getParamValue();
        }
        return checkListSource;
    }

    public ChecklistTriggerUrlCallResult setOneTwoRepairForemanSign(ChecklistSummaryInfoForSave detail) {
        ChecklistTriggerUrlCallResult result = new ChecklistTriggerUrlCallResult();
        List<String> attributeParam = new ArrayList<>();
        attributeParam.add(AttributeEnum.ATTR_FOREMAN_SIGN.getValue());
        try {
            String returnMsg = "";
            result = setOneTwoRepairSign(detail, attributeParam, "1", false,returnMsg);
            ChecklistSummary selectSummary = checklistSummaryMapper.selectById(detail.getExtraObject().getChecklistSummaryId());
            result.setExtraObject(selectSummary);
            return result;
        } catch (Exception exc)
        {
            logger.error("/checkInfoInit/setOneTwoRepairForemanSign----????????????????????????...", exc);
            throw exc;
        }
    }

    /**
     * ?????????????????????????????????
     * @param checklistSummary ???????????????
     * @return
     */
    public  ChecklistTriggerUrlCallResult signTechnique(ChecklistSummaryInfoForSave checklistSummary){
        ChecklistTriggerUrlCallResult result = new ChecklistTriggerUrlCallResult();
        try{
            checklistSummary.getExtraObject().setForemanSignCode(ShiroKit.getUser().getStaffId());
            checklistSummary.getExtraObject().setForemanSignName(ShiroKit.getUser().getName());
            saveCell(checklistSummary);
            List<ChecklistDetailInfoForSave> cells = checklistSummary.getCells();
            List<ChecklistDetailInfoForShow> changeCells = new ArrayList<ChecklistDetailInfoForShow>();
            for(ChecklistDetailInfoForSave cell : cells){
                ChecklistDetailInfoForShow changeCell = new ChecklistDetailInfoForShow();
                BeanUtils.copyProperties(cell, changeCell);
                changeCell.setReadOnly(true);
                if (changeCell.getAttributeCode().equals(AttributeEnum.ATTR_CHEEK_CONDITION.getValue()) ||
                        changeCell.getAttributeCode().equals(AttributeEnum.ATTR_CHEEK_RESULT.getValue()) ||
                        changeCell.getAttributeCode().equals(AttributeEnum.ATTR_CHEEK_CHEEKPERSON.getValue()) ||
                        changeCell.getAttributeCode().equals(AttributeEnum.ATTR_CHEEK_DISPOSALRESULT.getValue()) ||
                        changeCell.getAttributeCode().equals(AttributeEnum.ATTR_CHEEK_DISPOSALPERSON.getValue()) ||
                        changeCell.getAttributeCode().equals(AttributeEnum.ATTR_CHEEK_REMARK.getValue()) ||
                        changeCell.getAttributeCode().equals(AttributeEnum.ATTR_CHEEK_FINISHSTATE.getValue()) ||
                        changeCell.getAttributeCode().equals(AttributeEnum.ATTR_CHEEK_DISPATCH_SIGN.getValue()) ||
                        changeCell.getAttributeCode().equals(AttributeEnum.ATTR_CHEEK_OTHER.getValue()) ||
                        changeCell.getAttributeCode().equals(AttributeEnum.ATTR_CHEEK_TECHNIQUE_SIGN.getValue())) {
                    changeCell.setTip("???????????????????????????????????????");
                }
                changeCells.add(changeCell);
            }
            ChecklistSummary selectSummary = checklistSummaryMapper.selectById(checklistSummary.getExtraObject().getChecklistSummaryId());
            String guid = UUID.randomUUID().toString();
            //??????????????????
            updateRepairPacketStatus(selectSummary, guid);
            //?????????
            updateListItemPerfPool(selectSummary, guid);
            result.setExtraObject(selectSummary);
            result.setChangedCells(changeCells);
            return result;
        }catch (Exception e){
            logger.error("/checkInfoInit/signTechnique----?????????????????????????????????????????????...", e);
            throw e;
        }
    }

    /**
     * ????????????????????????????????????SUMMARY????????????type=1?????????????????????type=2??????????????????,isAllSign ??????????????????,????????????????????????
     */
    private String setOneTwoRepairSign(ChecklistSummary summary,List<String> attributeParam,String type,boolean isAllSign) {
        String flag = "";
        try {
            String summaryId = summary.getChecklistSummaryId();
            //?????????????????????????????????????????????????????????
            ChecklistSummary selectSummary = checklistSummaryMapper.selectById(summaryId);
            //????????????????????????
            if (selectSummary.getBackFillType().equals("1")) {
                List<ChecklistDetail> resultChecklistDetails = checklistDetailMapper.selectByAttribute(summaryId, attributeParam);
                List<String> personCodeList = new ArrayList<>();
                List<String> personNameList = new ArrayList<>();
                //?????????
                Log log = new Log();
                log.setModule("?????????????????????????????????");
                log.setType("1");
                String uuid = UUID.randomUUID().toString();
                log.setId(uuid);
                String content = JSONObject.toJSONString(summary);
                if (type.equals("1")) {
                    content = "????????????;" + content;
                } else if (type.equals("2")) {
                    content = "????????????;" + content;
                }
                log.setContent(content);
                iLogService.addLog(log);
                if (resultChecklistDetails.size() > 0) {
                    List<ChecklistDetail> ds = new ArrayList<ChecklistDetail>();
                    for (ChecklistDetail detailPerson : resultChecklistDetails) {
                        //???????????????????????????????????????????????????????????????
                        List<String> splitCodes = new ArrayList<>();
                        List<String> splitNamess = new ArrayList<>();
                        if(StringUtils.hasText(detailPerson.getCode())) {
                            splitCodes = Arrays.asList(detailPerson.getCode().split(","));
                        }
                        if(StringUtils.hasText(detailPerson.getValue())) {
                            splitNamess = Arrays.asList(detailPerson.getValue().split(","));
                        }

                        //???????????????????????????????????????????????????/?????????????????????
                        if (isAllSign) {
                            //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                            List<String> allSignCodes = new ArrayList<>();
                            allSignCodes.add(ShiroKit.getUser().getStaffId());
                            List<String> allSignNames = new ArrayList<>();
                            allSignNames.add(ShiroKit.getUser().getName());
                            for (String splitCode : splitCodes) {
                                if (!allSignCodes.contains(splitCode)) {
                                    allSignCodes.add(splitCode);
                                }
                            }
                            for (String splitName : splitNamess) {
                                if (!allSignNames.contains(splitName)) {
                                    allSignNames.add(splitName);
                                }
                            }
                            //??????????????????????????????????????????SUMMARY???????????????????????????personCodeList
                            for (String splitCode : allSignCodes) {
                                if (!personCodeList.contains(splitCode)) {
                                    personCodeList.add(splitCode);
                                }
                            }
                            for (String splitName : allSignNames) {
                                if (!personNameList.contains(splitName)) {
                                    personNameList.add(splitName);
                                }
                            }
                            detailPerson.setCode(StringUtils.join(allSignCodes, ","));
                            detailPerson.setValue(StringUtils.join(allSignNames, ","));
                            ds.add(detailPerson);
                           // checklistDetailService.updateById(detailPerson);
                        }else   //??????????????????????????????????????????SUMMARY
                        {
                            if(!CollectionUtils.isEmpty(splitCodes)){
                                for(int i =0;i<splitCodes.size();i++){
                                    String splitCode =  splitCodes.get(i);
                                    if (!personCodeList.contains(splitCode)) {
                                        personCodeList.add(splitCode);
                                        personNameList.add(splitNamess.get(i));
                                    }
                                }
                            }
                            /** ????????????????????????????????????????????????????????????
                            for (String splitCode : splitCodes) {
                                if (!personCodeList.contains(splitCode)) {
                                    personCodeList.add(detailPerson.getCode());
                                    personNameList.add(detailPerson.getValue());
                                }
                            }**/
                        }
                    }
                    if(ds.size()>0){
                        checklistDetailService.updateByPrimaryKey(ds);
                    }
                    if (personCodeList.size() > 0) {
                        String personCode = StringUtils.join(personCodeList, ",");
                        String personName = StringUtils.join(personNameList, ",");
                        if (type.equals("1")) {
                            selectSummary.setForemanSignCode(personCode);
                            selectSummary.setForemanSignName(personName);
                        } else if (type.equals("2")) {
                            selectSummary.setQualitySignCode(personCode);
                            selectSummary.setQualitySignName(personName);
                        }
                        checklistSummaryService.updateById(selectSummary);
                    }
                }

                if (type.equals("1")) {
                    //??????????????????
                    updateRepairPacketStatus(selectSummary, uuid);
                    //?????????
                    updateListItemPerfPool(selectSummary, uuid);
                    //???????????????
                    uploadMainCycPerf(selectSummary, uuid);
                    //??????C/S???????????????????????????
                    uploadOldChecklistSummary(selectSummary, uuid);
                    //???????????????????????????????????????????????????
                    uppladAbnorhalHandler(selectSummary,uuid);
                }
            }else
            {
                flag = "?????????????????????,????????????!";
            }
        } catch (Exception exc) {
            flag  = exc.getMessage();
        }
        return flag;
    }
    //???????????????????????????????????????????????????
    private void uppladAbnorhalHandler(ChecklistSummary summary,String logUuid)
    {
        try {
            //?????????????????????
            String staffId = UserUtil.getUserInfo().getStaffId();
            //?????????????????????
            String name = UserUtil.getUserInfo().getName();
            //????????????ip
            String ip = UserUtil.getUserInfo().getIpAddress();
            AbnormalHandlerLog abnormalHandlerLog = new AbnormalHandlerLog();
            abnormalHandlerLog.setAbnormalHandlerLogId(UUID.randomUUID().toString());
            abnormalHandlerLog.setChecklistSummaryId(summary.getChecklistSummaryId());
            abnormalHandlerLog.setDayPlanId(summary.getDayPlanId());
            abnormalHandlerLog.setTrainsetName(billCommon.getTrainsetNameById(summary.getTrainsetId()));
            abnormalHandlerLog.setHandlerType("2");
            abnormalHandlerLog.setItemCode(summary.getItemCode());
            abnormalHandlerLog.setItemName(summary.getItemName());
            abnormalHandlerLog.setMainCyc(summary.getMainCyc());
            abnormalHandlerLog.setPacketCode(summary.getPacketCode());
            abnormalHandlerLog.setPacketName(summary.getPacketName());
            abnormalHandlerLog.setStaffCode(staffId);
            abnormalHandlerLog.setStaffName(name);
            abnormalHandlerLog.setUseFlag("0");
            abnormalHandlerLog.setCreateTime(new Date());
            abnormalHandlerLog.setIpAddress(ip);
            abnormalHandlerLog.setHostname("");
            abnormalHandlerLogMapper.insert(abnormalHandlerLog);
        }catch (Exception exc)
        {
            //?????????
            Log log = new Log();
            log.setModule("?????????????????????????????????????????????????????????");
            log.setType("1");
            String uuid = UUID.randomUUID().toString();
            log.setId(uuid);
            log.setContent("???????????????:"+logUuid + ";????????????:"+exc.getMessage());
            iLogService.addLog(log);
        }
    }

    //??????C/S???????????????????????????
    private  void uploadOldChecklistSummary(ChecklistSummary summary,String logUuid)
    {
        try {
            if (summary.getTemplateType().equals(TemplateTypeNameEnum.RECORD_FIRST_PERSON_TEST.getValue())
                || summary.getTemplateType().equals(TemplateTypeNameEnum.RECORD_FIRST_PERSON_KEEP.getValue())) {
                //???????????????????????????
                ChklstsummaryLv1Old chklstsummaryLv1Old = new ChklstsummaryLv1Old();
                chklstsummaryLv1Old.setChecklistSummaryId(summary.getChecklistSummaryId());
                chklstsummaryLv1Old.setDayPlanId(summary.getDayPlanId());
                chklstsummaryLv1Old.setTrainsetId(billCommon.getTrainsetNameById(summary.getTrainsetId()));
                chklstsummaryLv1Old.setSppacketcode(summary.getPacketCode());
                chklstsummaryLv1Old.setSppacketname(summary.getPacketName());
                if (summary.getTemplateType().equals(TemplateTypeNameEnum.RECORD_FIRST_PERSON_TEST.getValue())) {
                    chklstsummaryLv1Old.setDoctype("4");
                }else if (summary.getTemplateType().equals(TemplateTypeNameEnum.RECORD_FIRST_PERSON_KEEP.getValue())) {
                    chklstsummaryLv1Old.setDoctype("3");
                }
                chklstsummaryLv1Old.setMainCycCode(summary.getMainCyc());
                chklstsummaryLv1Old.setRepairaccumile(Integer.parseInt(summary.getAccuMile()));
                chklstsummaryLv1Old.setForemansigncodes(summary.getForemanSignCode());
                chklstsummaryLv1Old.setForemansigntime(new Date());
                chklstsummaryLv1Old.setBackfillstate("2");
                chklstsummaryLv1Old.setForemansignnames(summary.getForemanSignName());
                chklstsummaryLv1OldMapper.insert(chklstsummaryLv1Old);
            } else if (summary.getTemplateType().equals(TemplateTypeNameEnum.RECORD_SECOND_PREVENTIVE.getValue())
                || summary.getTemplateType().equals(TemplateTypeNameEnum.RECORD_SECOND_WHEEL.getValue())
                || summary.getTemplateType().equals(TemplateTypeNameEnum.RECORD_SECOND_AXLEEDDY.getValue())
                || summary.getTemplateType().equals(TemplateTypeNameEnum.RECORD_SECOND_LUEDDY.getValue())) {
                //???????????????????????????
                ChecklistSummaryOld checklistSummaryOld = new ChecklistSummaryOld();
                checklistSummaryOld.setChecklistSummaryId(summary.getChecklistSummaryId());
                checklistSummaryOld.setDayPlanId(summary.getDayPlanId());
                checklistSummaryOld.setTrainsetId(billCommon.getTrainsetNameById(summary.getTrainsetId()));
                checklistSummaryOld.setSppacketcode(summary.getPacketCode());
                checklistSummaryOld.setSppacketname(summary.getPacketName());
                checklistSummaryOld.setItemType(summary.getItemType());
                checklistSummaryOld.setSprepairitemcode(summary.getItemCode());
                checklistSummaryOld.setSprepairitemname(summary.getItemName());
                checklistSummaryOld.setMainCycCode(summary.getMainCyc());
                checklistSummaryOld.setChecklistId(summary.getTemplateId());
                checklistSummaryOld.setRepairaccumile(Integer.parseInt(summary.getAccuMile()));
                checklistSummaryOld.setForemansigncodes(summary.getForemanSignCode());
                checklistSummaryOld.setForemansign(new Date());
                checklistSummaryOld.setForemansignnames(summary.getForemanSignName());
                checklistSummaryOld.setBackfillstate("2");
                checklistSummaryOldMapper.insert(checklistSummaryOld);
            }
        }catch (Exception exc)
        {
            //?????????
            Log log = new Log();
            log.setModule("??????C/S?????????????????????????????????");
            log.setType("1");
            String uuid = UUID.randomUUID().toString();
            log.setId(uuid);
            log.setContent("???????????????:"+logUuid + ";????????????:"+exc.getMessage());
            iLogService.addLog(log);
        }

    }

    //???????????????
    private  void uploadMainCycPerf(ChecklistSummary summary,String logUuid) {
        try {
            RepairStatNew repairStatNew = new RepairStatNew();
            repairStatNew.setAccmile(summary.getAccuMile());
            Date currentDate = new Date();
            repairStatNew.setCreateTime(currentDate);
            String dayPlan=summary.getDayPlanId().substring(0,10).replace("-","");
            repairStatNew.setDate(dayPlan);
            repairStatNew.setRepairCode(summary.getMainCyc());
            repairStatNew.setDeptCode(summary.getUnitCode());
            repairStatNew.setTrainsetId(summary.getTrainsetId());
            repairStatNewMapper.insert(repairStatNew);
        }catch (Exception exc)
        {
            //?????????
            Log log = new Log();
            log.setModule("???????????????????????????");
            log.setType("2");
            String uuid = UUID.randomUUID().toString();
            log.setId(uuid);
            log.setContent("???????????????:"+logUuid + ";????????????:"+exc.getMessage());
            iLogService.addLog(log);
        }
    }

    //??????????????????
    private  void updateListItemPerfPool(ChecklistSummary summary,String logUuid) {
        ItemPerformance itemPerformance = new ItemPerformance();
        String dayPlanID = summary.getDayPlanId();
        itemPerformance.setSDayplandate(dayPlanID.substring(0, 10));
        try {
            WorkTime workTime = DayPlanUtil.getWorkTimeByDayPlanId(summary.getDayPlanId());
            if (workTime != null) {
                itemPerformance.setDRepairbegintime(workTime.getStartTime());
                itemPerformance.setDRepairendtime(workTime.getEndTime());
            }
        } catch (Exception e) {
            //?????????
            Log log = new Log();
            log.setModule("??????????????????????????????");
            log.setType("1");
            String uuid = UUID.randomUUID().toString();
            log.setId(uuid);
            log.setContent("?????????????????????????????????????????????????????????ID = " + summary.getDayPlanId());
            iLogService.addLog(log);
        }

        itemPerformance.setSMaintcyccode(summary.getMainCyc());
        itemPerformance.setSRepaircar(summary.getCar());
        itemPerformance.setSRepairbeginaccumile(summary.getAccuMile());
        itemPerformance.setSRepairendaccumile(summary.getAccuMile());
        itemPerformance.setSTrainsetid(summary.getTrainsetId());
        TrainsetBaseInfo trainsetBaseInfo = billCommon.getTrainsetInfo(summary.getTrainsetId());
        if(trainsetBaseInfo != null) {
            itemPerformance.setSTrainsetname(trainsetBaseInfo.getTrainsetname());
        }
        itemPerformance.setSRepairdeptcode(summary.getDeptCode());
        itemPerformance.setSRepairdeptname(summary.getDeptName());
        itemPerformance.setSRepairmancode(summary.getFillWorkCode());
        itemPerformance.setSRepairmanname(summary.getFillWorkName());
        String uuidItem = UUID.randomUUID().toString();
        itemPerformance.setSItemrecordid(uuidItem);
        itemPerformance.setSSprepairitemcode(summary.getItemCode());
        itemPerformance.setSSprepairitemname(summary.getItemName());
        itemPerformance.setSRepairunitcode(summary.getUnitCode());
        String dayornight = dayPlanID.substring(dayPlanID.length() - 1);
        itemPerformance.setCDayornight(dayornight);
        List<ItemPerformance> list =new ArrayList<>();
        list.add(itemPerformance);

        JSONObject result = iRemoteService.addListItemPerfPool(list);
        if (result.getString("code").equals("200") == false) {
            //?????????
            Log log = new Log();
            log.setModule("??????????????????????????????");
            log.setType("1");
            String uuid = UUID.randomUUID().toString();
            log.setId(uuid);
            log.setContent("?????????:" + result.get("code").toString() + ";????????????:" + result.get("log").toString() + ";???????????????:" + logUuid);
            iLogService.addLog(log);
        }
    }

    //??????????????????
    private void updateRepairPacketStatus(ChecklistSummary summary,String logUuid)
    {
        List<RepairPacketStatu> repairPacketStatuList = new ArrayList<>();//???????????????????????????????????????
        RepairPacketStatu repairPacketStatu = new RepairPacketStatu();
        repairPacketStatu.setSTrainsetid(summary.getTrainsetId());
        repairPacketStatu.setSDayplanid(summary.getDayPlanId());
        repairPacketStatu.setSItemcode(summary.getItemCode());
        repairPacketStatu.setSDeptcode(summary.getUnitCode());
        repairPacketStatu.setSPacketcode("");
        repairPacketStatu.setCStatue("3");//???????????????
        repairPacketStatuList.add(repairPacketStatu);
        //??????????????????-????????????????????????
        try {
            if (!CollectionUtils.isEmpty(repairPacketStatuList)) {
                JSONObject result = iRemoteService.updateRepairPacketStatus(repairPacketStatuList);
                if (result.getString("code").equals("200") == false) {
                    //?????????
                    Log log = new Log();
                    log.setModule("?????????????????????????????????");
                    log.setType("1");
                    String uuid = UUID.randomUUID().toString();
                    log.setId(uuid);
                    log.setContent("?????????:"+result.get("code").toString()+";????????????:"+result.get("log").toString()+";???????????????:"+logUuid);
                    iLogService.addLog(log);
                }
            }
        }catch (Exception ex) {
            logger.error("??????????????????????????????????????????:" + ex.getMessage());

        }
    }



private  ChecklistTriggerUrlCallResult setOneTwoRepairSign(ChecklistSummaryInfoForSave detail,List<String> attributeParam ,String type,boolean isAllSign,String returnMsg)
{
    boolean isCellChange = true;
    ChecklistTriggerUrlCallResult result = new ChecklistTriggerUrlCallResult();
    ChecklistSummary summary = detail.getExtraObject();
    String[] arrayCodes = summary.getFillWorkCode().split(",");
    String[] arrayNames = summary.getFillWorkName().split(",");
    //?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
    List<ChecklistDetailInfoForSave> cells = detail.getTriggerCells().stream().map(v->v.getAfterChangeCellInfo()).collect(Collectors.toList());
    for(ChecklistDetailInfoForSave cell : cells){
        List<ChecklistDetailInfoForSave> cs = detail.getCells().stream().filter(f->f.getId().equals(cell.getId())).collect(Collectors.toList());
        for (ChecklistDetailInfoForSave c : cs){
            c.setCode(cell.getCode());
            c.setValue(cell.getValue());
            c.setChangeType(cell.getChangeType());
        }
    }
    saveCell(detail);
    //???????????????????????????????????????
    List<ChecklistDetailInfoForShow> showChangedCells = new ArrayList<>();
    billCommon.UpdateSavedState(showChangedCells,detail.getCells());
    String flag = setOneTwoRepairSign(summary, attributeParam, type, isAllSign);
    returnMsg = flag;
    if (StringUtils.hasText(flag) == false) {
        //???????????????????????????????????????????????????readonly????????????
        List<ChecklistDetailInfoForSave> changedCells = detail.getTriggerCells().stream().map(v -> v.getAfterChangeCellInfo()).collect(Collectors.toList());
        for (ChecklistDetailInfoForSave cell : changedCells)
        {
            List<ChecklistDetailInfoForShow> filterShowCells =   showChangedCells.stream().filter(t->t.getId().equals(cell.getId())).collect(Collectors.toList());
            for (ChecklistDetailInfoForShow showcell : filterShowCells)
            {
                showcell.setReadOnly(true);
            }
        }
        ChecklistSummaryInfoForShow summaryInfoForShow = getDetailInfoShow(summary.getChecklistSummaryId(),summary.getTemplateId(), summary.getTemplateType(), false);
        for(ChecklistDetailInfoForShow cell : summaryInfoForShow.getCells()){
            //??????????????????
            if(attributeParam.contains(AttributeEnum.ATTR_FOREMAN_SIGN.getValue())){
                if(cell.getAttributeCode().equals(AttributeEnum.ATTR_QUALITY_SIGN.getValue()) || cell.getAttributeCode().equals(AttributeEnum.ATTR_QUALITY_AXLESIGN.getValue())){
                    cell.setReadOnly(isHasValue(cell.getCode(), ShiroKit.getUser().getStaffId()));
                    continue;
                }
            }
            //???????????????
            if(attributeParam.contains(AttributeEnum.ATTR_QUALITY_SIGN.getValue())){
                if(cell.getAttributeCode().equals(AttributeEnum.ATTR_FOREMAN_SIGN.getValue()) || cell.getAttributeCode().equals(AttributeEnum.ATTR_QUALITY_AXLESIGN.getValue())){
                    cell.setReadOnly(isHasValue(cell.getCode(), ShiroKit.getUser().getStaffId()));
                    continue;
                }
            }
            //???????????????
            if(attributeParam.contains(AttributeEnum.ATTR_QUALITY_AXLESIGN.getValue())){
                if(cell.getAttributeCode().equals(AttributeEnum.ATTR_FOREMAN_SIGN.getValue()) || cell.getAttributeCode().equals(AttributeEnum.ATTR_QUALITY_SIGN.getValue())){
                    cell.setReadOnly(isHasValue(cell.getCode(), ShiroKit.getUser().getStaffId()));
                    continue;
                }
            }
            cell.setReadOnly(true);
            showChangedCells.add(cell);
        }
    }else {
        //???????????????????????????????????????????????????????????????????????????????????????
        //??????????????????????????????????????????????????????????????????????????????????????????
        //1??????????????????????????????????????????????????????????????????????????????
        //2?????????????????????????????????????????????????????????????????????????????????????????????????????????
        //3???????????????
        if (detail.getTriggerCells().size() > 0) {
            ChecklistDetailInfoForSave forSave = detail.getTriggerCells().get(0).getBeforeChangeCellInfo();
            ChecklistDetailInfoForSave forAfter = detail.getTriggerCells().get(0).getAfterChangeCellInfo();
            boolean isCodeExists = false;
            boolean isNameExists = false;
            for(String code : arrayCodes){
                if(code.equals(forAfter.getCode())){
                    isCodeExists = true;
                    break;
                }
            }
            for(String name : arrayNames){
                if(name.equals(forAfter.getValue())){
                    isNameExists = true;
                    break;
                }
            }
            if(!isCodeExists){
                summary.setFillWorkCode(String.join(",", arrayCodes));
            }
            if(!isNameExists){
                summary.setFillWorkName(String.join(",", arrayNames));
            }
            List<ChecklistDetail> updateCells = new ArrayList<ChecklistDetail>();
            updateCells.add(forSave);
            checklistDetailService.updateByPrimaryKey(updateCells);
            checklistSummaryService.updateById(summary);
            ChecklistDetailInfoForShow showcell = new ChecklistDetailInfoForShow();
            BeanUtils.copyProperties(forSave, showcell);
            showChangedCells.clear();
            showChangedCells.add(showcell);
            isCellChange = false;
        } else {
            RestRequestException.fatalFail("??????????????????????????????????????????!");
        }
    }
    result.setChangedCells(showChangedCells);
    result.setOperationResultMessage(returnMsg);
    result.setAllowChange(isCellChange);
    return result;
}


    public ChecklistTriggerUrlCallResult setOneTwoRepairQualitySign(ChecklistSummaryInfoForSave detail) {
        ChecklistTriggerUrlCallResult result = new ChecklistTriggerUrlCallResult();
        try {

            List<String> attributeParam = new ArrayList<>();
            attributeParam.add(AttributeEnum.ATTR_QUALITY_SIGN.getValue());
            String returnMsg = "";
            result = setOneTwoRepairSign(detail, attributeParam, "2", false,returnMsg);
            //??????checklistSummary
            ChecklistSummary selectSummary = checklistSummaryMapper.selectById(detail.getExtraObject().getChecklistSummaryId());
            result.setExtraObject(selectSummary);
            return result;
        } catch (Exception exc) {
            logger.error("/checkInfoInit/setOneTwoRepairQualitySign----????????????????????????...", exc);
            throw exc;
        }
    }

    //??????????????????
    public SignMessage setOneTwoRepairAllForemanSign(List<OneTwoRepairCheckList> summaryList) {
        SignMessage msg = new SignMessage();
        try {
            List<String> attributeParamTemp = new ArrayList<>();
            attributeParamTemp.add(AttributeEnum.ATTR_FOREMAN_SIGN.getValue());
            int total = summaryList.size();
            int count = 0;
            for (OneTwoRepairCheckList checkSummary : summaryList
                    ) {
                if (StringUtils.hasText(checkSummary.getTemplateId())) {
                    String isSuccess = setOneTwoRepairSign(convertSummaryByOneTwoRepair(checkSummary), attributeParamTemp, "1", true);
                    if (StringUtils.hasText(isSuccess) == false)
                        count++;
                }
            }
            if (total == count) {
                msg.setMsg("?????????????????????");
                msg.setCode("1");
            } else if (total > count && count > 0) {
                msg.setMsg("?????????????????????????????????????????????????????????");
                msg.setCode("0");
            } else if (count == 0) {
                msg.setMsg("???????????????????????????????????????????????????");
                msg.setCode("-1");
            }
        } catch (Exception ex) {
            logger.error(ex.toString());
            throw ex;
        }
        return msg;
    }

    private ChecklistSummary convertSummaryByOneTwoRepair(OneTwoRepairCheckList checkSummary)
    {
        ChecklistSummary  summary = new ChecklistSummary();
        BeanUtils.copyProperties(checkSummary, summary);
        return summary;
    }

    //??????????????????
    public SignMessage setOneTwoRepairAllQualitySign(List<OneTwoRepairCheckList> summaryList) {
        SignMessage msg = new SignMessage();
        try {
            List<String> attributeParam = new ArrayList<>();
            attributeParam.add(AttributeEnum.ATTR_QUALITY_SIGN.getValue());
            int total = summaryList.size();
            int count = 0;
            for (OneTwoRepairCheckList checkSummary:summaryList
                ) {
                if(StringUtils.hasText(checkSummary.getTemplateId())) {
                    String isSuccess =  setOneTwoRepairSign(convertSummaryByOneTwoRepair(checkSummary), attributeParam, "2", true);
                    if (StringUtils.hasText(isSuccess) == false)
                        count++;
                }
            }
            if (total == count) {
                msg.setMsg("?????????????????????");
                msg.setCode("1");
            } else if (total > count && count > 0) {
                msg.setMsg("?????????????????????????????????????????????????????????");
                msg.setCode("0");
            } else if (count == 0) {
                msg.setMsg("???????????????????????????????????????????????????");
                msg.setCode("-1");
            }
        } catch (Exception ex) {
            logger.error(ex.toString());
            throw ex;
        }
        return msg;
    }

    /**
     * ????????????
     */
    public ChecklistTriggerUrlCallResult faultJump(ChecklistSummaryInfoForSave saveInfo) {
        ChecklistTriggerUrlCallResult triggerUrlCallResult = new ChecklistTriggerUrlCallResult();
        UrlInfo triggerUrlInfo = new UrlInfo();
        triggerUrlInfo.setUrlType(BillCellTriggerUrlTypeEnum.PAGE);
        triggerUrlInfo.setUrl("/webTrainFault/fault/insertView");//??????????????????
        triggerUrlCallResult.setNextUrlInfo(triggerUrlInfo);
        ChecklistSummary  summary =  saveInfo.getExtraObject();
        UserUtil.getUserInfo();
        ShiroUser currentUser = ShiroKit.getUser();
        //??????
        String carNo = "";
        List<BillCellChangeInfo<ChecklistDetailInfoForSave>> triggerCells = saveInfo.getTriggerCells();
        if(triggerCells.size()>0)
        {
            List<ChkDetailLinkContentForModule> linkContentForModules = triggerCells.get(0).getAfterChangeCellInfo().getLinkCells();
            List<ChecklistDetailInfoForSave> detailInfoForSaves =  saveInfo.getCells().stream().filter(t->t.getAttributeCode().equals(AttributeEnum.ATTR_CAR.getValue())).collect(Collectors.toList());
           for (ChkDetailLinkContentForModule forModule :linkContentForModules ) {
               List<ChecklistDetailInfoForSave> forSaves = detailInfoForSaves.stream().filter(t -> t.getId().equals(forModule.getLinkCellId())).collect(Collectors.toList());
               if (forSaves.size() > 0) {
                   carNo = forSaves.get(0).getValue();
               }
           }
        }
        Map<String, String> keyMap = new HashMap<>();
        //??????
        keyMap.put("trainSetId",summary.getTrainsetId());
        keyMap.put("carNo",carNo);
        keyMap.put("faultSourceName","03");
        keyMap.put("findTime", DateTimeUtil.format(new Date()));
        keyMap.put("faultFindBranchName",currentUser.getDepartMentOrgan().getOrganCode());
        keyMap.put("findFaultMan",currentUser.getStaffId());
        keyMap.put("type","OneTwoRepair");
        keyMap.put("checklistSummaryId",summary.getChecklistSummaryId());
        if(triggerCells.size()>0)
        {
            keyMap.put("detailId",triggerCells.get(0).getAfterChangeCellInfo().getId());
        }else
        {
            keyMap.put("detailId","");
        }
        keyMap.put("dayPlanId",summary.getDayPlanId());
        triggerUrlCallResult.setNextUrlParams(keyMap);
        return triggerUrlCallResult;
    }

    /**
     * ????????????
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveCell(ChecklistSummaryInfoForSave saveInfo) {
        try {
            ChecklistSummary summary = saveInfo.getExtraObject();
            boolean isChanged = billCommon.saveCellDetail(saveInfo.getCells(),summary.getChecklistSummaryId());
            //????????????????????????????????????
            if(isChanged) {
                //????????????
            //    ChecklistSummary summary = saveInfo.getExtraObject();
                summary.setLastFillTime(new Date());
                summary.setLastFillWorkCode(UserUtil.getUserInfo().getStaffId());
                summary.setLastFillWorkName(UserUtil.getUserInfo().getName());
                summary.setLastHostName("");
                summary.setLastIpAddress("");

                //????????????????????????????????????????????????????????????????????????????????????
                //????????????????????????????????????????????????????????????????????????????????????
                List<TemplateAttribute> attributes = billCommon.getTemplateAttributeTypeInfos(summary.getTemplateType(), "backFillVerify", "1");
                List<String> attributeParam = new ArrayList<>();
                for (TemplateAttribute attribute : attributes) {
                    attributeParam.add(attribute.getAttributeCode());
                }
                List<ChecklistDetail> resultChecklistDetails = checklistDetailMapper.selectByAttribute(summary.getChecklistSummaryId(), attributeParam);
                if (resultChecklistDetails.stream().filter((ChecklistDetail t) -> StringUtils.hasText(t.getValue()) == false).collect(Collectors.toList()).size() > 0) {
                    summary.setBackFillType("0");
                }else
                {
                    summary.setBackFillType("1");
                }
                //???????????????????????????????????????????????????????????????????????????????????????????????????????????????
                List<String> attributeParamT = new ArrayList<>();
                //???????????????????????????????????????
                attributes = billCommon.getTemplateAttributeTypeInfos(summary.getTemplateType(), "attributeModeCode", "SignControl");
                attributeParam = new ArrayList<>();
                for (TemplateAttribute attribute : attributes) {
                    attributeParam.add(attribute.getAttributeCode());
                }
                List<ChecklistDetail> resultPersons = checklistDetailMapper.selectByAttribute(summary.getChecklistSummaryId(), attributeParam);
                List<String> personCodeList = new ArrayList<>();
                List<String> personNameList = new ArrayList<>();
                for (ChecklistDetail detailPerson : resultPersons) {
                    if(StringUtils.hasText(detailPerson.getCode())){
                        String[] codes = detailPerson.getCode().split(",");
                        for(String code : codes){
                            if(personCodeList.contains(code) == false){
                                personCodeList.add(code);
                            }
                        }
                    }
                    if(StringUtils.hasText(detailPerson.getValue()) && !detailPerson.getValue().equals("N/A")){
                        String[] values = detailPerson.getValue().split(",");
                        for(String val : values){
                            if(personNameList.contains(val) == false){
                                personNameList.add(val);
                            }
                        }
                    }
                }
                if (personCodeList.size() > 0 && personNameList.size() > 0) {
                    String personCode = StringUtils.join(personCodeList, ",");
                    String personName = StringUtils.join(personNameList, ",");
                    summary.setFillWorkCode(personCode);
                    summary.setFillWorkName(personName);
                    List<ChecklistDetailInfoForSave> forSaves = saveInfo.getCells().stream().filter((ChecklistDetailInfoForSave t) -> AttributeEnum.ATTR_REPAIR_PERSON_SHOW.getValue().equals(t.getAttributeCode())).collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(forSaves)) {
                        for (ChecklistDetailInfoForSave forSave : forSaves
                            ) {
                            ChecklistDetail detail = new ChecklistDetail();
                            BeanUtils.copyProperties(forSave, detail);
                            detail.setValue(personName);
                            checklistDetailMapper.updatePrimaryKey(detail);
                        }
                    }
                }else{
                    summary.setFillWorkCode("");
                    summary.setFillWorkName("");
                    List<ChecklistDetailInfoForSave> forSaves = saveInfo.getCells().stream().filter((ChecklistDetailInfoForSave t) -> AttributeEnum.ATTR_REPAIR_PERSON_SHOW.getValue().equals(t.getAttributeCode())).collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(forSaves)) {
                        for (ChecklistDetailInfoForSave forSave : forSaves
                                ) {
                            ChecklistDetail detail = new ChecklistDetail();
                            BeanUtils.copyProperties(forSave, detail);
                            detail.setValue("");
                            checklistDetailMapper.updatePrimaryKey(detail);
                        }
                    }
                }
                //????????????????????????????????????????????????????????????????????????????????????????????????
                if(TemplateTypeNameEnum.RECORD_SECOND_WHEEL.getValue().equals(summary.getTemplateType())) {
                    List<ChecklistDetailInfoForSave> qualitys = saveInfo.getCells().stream().filter((ChecklistDetailInfoForSave t) -> AttributeEnum.ATTR_QUALITY_AXLESIGN.getValue().equals(t.getAttributeCode())).collect(Collectors.toList());
                    List<String> qualityCodes = new ArrayList<String>();
                    List<String> qualityNames = new ArrayList<String>();
                    if(!CollectionUtils.isEmpty(qualitys)){
                        for(ChecklistDetailInfoForSave q : qualitys){
                            if(StringUtils.hasText(q.getCode()) && !q.getCode().equals("N/A") && !qualityCodes.contains(q.getCode()) ){
                                qualityCodes.add(q.getCode());
                            }
                            else
                                continue;
                            if(StringUtils.hasText(q.getValue()) && !q.getValue().equals("N/A") && !qualityNames.contains(q.getValue())){
                                qualityNames.add(q.getValue());
                            }
                        }
                    }
                    if(qualityCodes.size() >0 && qualityNames.size() == qualityCodes.size()){
                        summary.setQualitySignCode(String.join(",", qualityCodes));
                        summary.setQualitySignName(String.join(",", qualityNames));
                    }else{
                        summary.setQualitySignCode("");
                        summary.setQualitySignName("");
                    }
                }
                //??????????????????????????????????????????????????????????????????
                if(TemplateTypeNameEnum.RECORD_FIRST_EQUIPMENT_KEEP.getValue().equals(summary.getTemplateType())){
                    List<ChecklistDetailInfoForSave> equipments = saveInfo.getCells().stream().filter((ChecklistDetailInfoForSave t) -> AttributeEnum.ATTR_CHEEK_DISPATCH_SIGN.getValue().equals(t.getAttributeCode()) || AttributeEnum.ATTR_CHEEK_TECHNIQUE_SIGN.getValue().equals(t.getAttributeCode())).collect(Collectors.toList());
                    List<String> dispatchCodes = new ArrayList<String>();
                    List<String> dispatchNames = new ArrayList<String>();
                    List<String> techniqueCodes = new ArrayList<String>();
                    List<String> techniqueNames = new ArrayList<String>();
                    if(!CollectionUtils.isEmpty(equipments)){
                        for(ChecklistDetailInfoForSave e : equipments){
                            if(AttributeEnum.ATTR_CHEEK_DISPATCH_SIGN.getValue().equals(e.getAttributeCode())){
                                if(!StringUtils.isEmpty(e.getCode()) && !dispatchCodes.contains(e.getCode())){
                                    dispatchCodes.add(e.getCode());
                                }
                                if(!StringUtils.isEmpty(e.getValue()) && !dispatchNames.contains(e.getValue())){
                                    dispatchNames.add(e.getValue());
                                }
                            }
                            if(AttributeEnum.ATTR_CHEEK_TECHNIQUE_SIGN.getValue().equals(e.getAttributeCode())){
                                if(!StringUtils.isEmpty(e.getCode()) && !techniqueCodes.contains(e.getCode())){
                                    techniqueCodes.add(e.getCode());
                                }
                                if(!StringUtils.isEmpty(e.getValue()) && !techniqueNames.contains(e.getValue())){
                                    techniqueNames.add(e.getValue());
                                }
                            }
                        }
                    }
                    if(dispatchCodes.size() >0 && dispatchNames.size() > 0){
                        summary.setQualitySignCode(String.join(",", dispatchCodes));
                        summary.setQualitySignName(String.join(",", dispatchNames));
                    }else{
                        summary.setQualitySignCode("");
                        summary.setQualitySignName("");
                    }
                    if(techniqueCodes.size() >0 && techniqueNames.size() > 0){
                        summary.setForemanSignCode(String.join(",", techniqueCodes));
                        summary.setForemanSignName(String.join(",", techniqueNames));
                    }else{
                        summary.setForemanSignCode("");
                        summary.setForemanSignName("");
                    }
                }
                checklistSummaryService.updateById(summary);
            }
        } catch (Exception exc) {
            logger.error("/checkInfoInit/SaveCell----??????????????????...", exc);
            throw exc;
        }
    }

    /**
     * ????????????????????????????????????
     * @param saveInfo ????????????
     * @return ????????????
     */
    public String getSaveButtonClickMsg(ChecklistSummaryInfoForSave saveInfo){
        String msg = "";
        try{
            ChecklistSummary summary = saveInfo.getExtraObject();
            List<ChecklistDetailInfoForSave> cells = saveInfo.getCells();
            if(summary.getTemplateType().equals(TemplateTypeNameEnum.RECORD_FIRST_EQUIPMENT_KEEP.getValue())){         //???????????????
                List<ChecklistDetailInfoForSave> cs = cells.stream().filter(t->t.getAttributeCode().equals(AttributeEnum.ATTR_CHEEK_TECHNIQUE_SIGN.getValue())).collect(Collectors.toList());
                if(cs.size() >0){
                    if(!StringUtils.isEmpty(cs.get(0).getValue())){
                        msg = "????????????????????????????????????????????????";
                    }
                }
            }else if(summary.getTemplateType().equals(TemplateTypeNameEnum.RECORD_FIRST_PERSON_KEEP.getValue()) ||
                    summary.getTemplateType().equals(TemplateTypeNameEnum.RECORD_FIRST_PERSON_TEST.getValue()) ||
                    summary.getTemplateType().equals(TemplateTypeNameEnum.RECORD_SECOND_PREVENTIVE.getValue())){       //????????????????????????????????????????????????????????????
                List<ChecklistDetailInfoForSave> cs = cells.stream().filter(t->t.getAttributeCode().equals(AttributeEnum.ATTR_FOREMAN_SIGN.getValue())).collect(Collectors.toList());
                if(cs.size() >0){
                    if(!StringUtils.isEmpty(cs.get(0).getValue())){
                        msg = "???????????????????????????????????????????????????";
                    }
                }
            }
        }catch (Exception e){
            logger.error("???????????????????????????????????????????????????/checkInfoInit/getSaveButtonClickMsg???...",e);
        }
        return  msg;
    }

    /**
     * ?????????????????????????????????
     * @param summary
     */
    public void initAllotBackFill(ChecklistSummaryInfoForShow summary){
        try {
            List<ChecklistDetail> ds = new ArrayList<ChecklistDetail>();
            List<CarAxleEntity> carAxleEntities = getCarNoAxles(summary.getExtraObject());
            List<ChecklistDetailInfoForShow> cells = summary.getCells();
            List<ChecklistDetailInfoForShow> lstCarNos = getDetailByAttrForShow(cells, AttributeEnum.ATTR_CAR);
            List<ChecklistDetailInfoForShow> lstAxles = getDetailByAttrForShow(cells, AttributeEnum.ATTR_AXLE_POSTION);
            //?????????????????????????????????LU???????????????????????????
            boolean isPart = summary.getExtraObject().getTemplateType().equals(TemplateTypeNameEnum.RECORD_SECOND_WHEEL.getValue()) ||
                    summary.getExtraObject().getTemplateType().equals(TemplateTypeNameEnum.RECORD_SECOND_AXLEEDDY.getValue()) ||
                    summary.getExtraObject().getTemplateType().equals(TemplateTypeNameEnum.RECORD_SECOND_LUEDDY.getValue());
            List<String> attrs = getBackFillArea();
            for (ChecklistDetailInfoForShow cell : cells) {
                if (!attrs.contains(cell.getAttributeCode()))
                    continue;
                String carNo = getSingleDetailValue(lstCarNos, cell);
                if (isPart) {
                    String axleNO = getSingleDetailValue(lstAxles, cell);
                    for (CarAxleEntity carAxleEntity : carAxleEntities) {
                        if (carAxleEntity.getCarNo().equals(carNo) && carAxleEntity.getAxleNo().equals(axleNO)) {
                            if (!carAxleEntity.getAllowFill()) {
                                cell.setValue("N/A");
                                cell.setReadOnly(true);
                                break;
                            } else {
                                cell.setReadOnly(false);
                            }
                        }
                    }
                } else {
                    for (CarAxleEntity carAxleEntity : carAxleEntities) {
                        if (carAxleEntity.getCarNo().equals(carNo)) {
                            if (!carAxleEntity.getAllowFill()) {
                                cell.setValue("N/A");
                                cell.setReadOnly(true);
                                break;
                            } else {
                                cell.setReadOnly(false);
                            }
                        }
                    }
                }
                ds.add(cell);
            }
            checklistDetailService.updateByPrimaryKey(ds);
        }catch (Exception ex){
            logger.error("?????????????????????????????????",ex);
        }
    }

    /**
     * ?????????????????????????????????????????????
     * @param cells ????????????
     * @param detail
     * @return
     */
    private String getSingleDetailValue(List<ChecklistDetailInfoForShow> cells, ChecklistDetailInfoForShow detail){
        String value = "";
        for(ChkDetailLinkContent link : detail.getLinkContents()){
            ChecklistDetailInfoForShow findDetail = findDetailByContentIdForShow(cells, link.getLinkContentId());
            if(findDetail != null){
                value = findDetail.getValue();
                break;
            }
        }
        return value;
    }

    /**
     * ?????????????????????
     * @param s ?????????
     * @return ??????
     */
    public List<CarAxleEntity> getCarNoAxles(ChecklistSummary s) {
        List<CarAxleEntity> result = new ArrayList<CarAxleEntity>();
        //???????????????????????????
        List<String> carnos = billCommon.getTrainMarshlType(s.getTrainsetId());
        //????????????????????????
        List<ZtTaskItemEntity> items = getPlanItemEntity(s);
        if (items == null || items.size() == 0){
            return result;
        }
        //??????????????????????????????
        String arrageType = items.get(0).getArrangeType();
        for (String carNo : carnos) {
            if(arrageType.equals("1")){
                for (int axle = 1; axle < 5; axle++) {
                    CarAxleEntity carAxleEntity = new CarAxleEntity();
                    carAxleEntity.setCarNo(carNo);
                    carAxleEntity.setAxleNo(String.valueOf(axle));
                    result.add(carAxleEntity);
                }
            }else{
                CarAxleEntity carAxleEntity = new CarAxleEntity();
                carAxleEntity.setCarNo(carNo);
                result.add(carAxleEntity);
            }
        }
        for(ZtTaskItemEntity item : items){
            String carNo = item.getCarNo();
            if(arrageType.equals("1")){
                String location = item.getLocationCode();
                String axle = location;
                if(!s.getTemplateType().equals(TemplateTypeNameEnum.RECORD_SECOND_AXLEEDDY.getValue())){
                    axle = convertAxle(location);
                }
                for(CarAxleEntity carAxleEntity : result){
                    if(carAxleEntity.getCarNo().equals(carNo) && carAxleEntity.getAxleNo().equals(axle)){
                        carAxleEntity.setAllowFill(true);
                    }
                }
            }else {
                for(CarAxleEntity carAxleEntity : result){
                    if(carAxleEntity.getCarNo().equals(carNo) || carNo.equals("??????")){
                        carAxleEntity.setAllowFill(true);
                    }
                }
            }
        }
        return result;
    }

    /**
     *  ??????????????????????????????????????????????????????
     * @param contentList ??????????????????
     * @param attr ????????????
     * @return ??????
     */
    private List<ChecklistDetailInfoForShow> getDetailByAttrForShow(List<ChecklistDetailInfoForShow> contentList, AttributeEnum attr) {
        List<ChecklistDetailInfoForShow> ds = new ArrayList<ChecklistDetailInfoForShow>();
        for (int i = 0; i < contentList.size(); i++) {
            ChecklistDetailInfoForShow t = contentList.get(i);
            if (t.getAttributeCode().equals(attr.getValue())) {
                ds.add(t);
            }
        }
        return ds;
    }

    /**
     * ???????????????????????????????????????
     * @param lstDetails ??????????????????
     * @param contentId ????????????
     * @return ????????????
     */
    public ChecklistDetailInfoForShow findDetailByContentIdForShow(List<ChecklistDetailInfoForShow> lstDetails, String contentId){
        ChecklistDetailInfoForShow detail = null;
        for (int i=0; i< lstDetails.size(); i++){
            for (int j= 0;j < lstDetails.get(i).getLinkContents().size();j++){
                ChkDetailLinkContent content = lstDetails.get(i).getLinkContents().get(j);
                if (content.getContentId().equals(contentId)){
                    detail = lstDetails.get(i);
                    break;
                }
            }
        }
        return  detail;
    }

    /**
     * ????????????????????????
     * @param checklistSummary ?????????????????????LU????????????
     * @return
     */
    public ChecklistTriggerUrlCallResult setRepairPerson(ChecklistSummaryInfoForSave checklistSummary){
        ChecklistTriggerUrlCallResult result = new ChecklistTriggerUrlCallResult();
        try{
            List<String> lstPersonNames = new ArrayList<String>();
            List<String> lstPersonCodes = new ArrayList<String>();
            List<ChecklistDetailInfoForSave> cells = checklistSummary.getCells();
            List<ChecklistDetailInfoForShow> cellShow = new ArrayList<ChecklistDetailInfoForShow>();
            ChecklistDetailInfoForShow repairPersonShowCell = new ChecklistDetailInfoForShow();
            boolean isFindShowCell = false;
            for(ChecklistDetailInfoForSave cell : cells){
                if(cell.getAttributeCode().equals(AttributeEnum.ATTR_REPAIR_PERSON_SHOW.getValue()) && !isFindShowCell){
                    BeanUtils.copyProperties(cell, repairPersonShowCell);
                    isFindShowCell = true;
                    continue;
                }
                if((cell.getAttributeCode().equals(AttributeEnum.ATTR_REPAIR_PERSON.getValue())||
                        cell.getAttributeCode().equals(AttributeEnum.ATTR_AXLE_TASKDOWN_A.getValue())||
                        cell.getAttributeCode().equals(AttributeEnum.ATTR_AXLE_TASKDOWN_B.getValue())||
                        cell.getAttributeCode().equals(AttributeEnum.ATTR_AXLE_TASKDOWN_E.getValue())||
                        cell.getAttributeCode().equals(AttributeEnum.ATTR_AXLE_INSTALL_A.getValue())||
                        cell.getAttributeCode().equals(AttributeEnum.ATTR_AXLE_INSTALL_B.getValue())||
                        cell.getAttributeCode().equals(AttributeEnum.ATTR_AXLE_INSTALL_E.getValue())||
                        cell.getAttributeCode().equals(AttributeEnum.ATTR_AXLE_PUTINTO.getValue())||
                        cell.getAttributeCode().equals(AttributeEnum.ATTR_QUALITY_AXLESIGN.getValue())) && !cell.getValue().equals("N/A")) {
                    if(!StringUtils.isEmpty(cell.getValue())){
                        List<String> lstNames = convertList(cell.getValue().split(","));
                        for(String name : lstNames){
                            if(!lstPersonNames.contains(name)){
                                lstPersonNames.add(name);
                            }
                        }
                        List<String> lstCodes = convertList(cell.getCode().split(","));
                        for(String code : lstCodes){
                            if(!lstPersonCodes.contains(code)){
                                lstPersonCodes.add(code);
                            }
                        }
                    }
                }
            }
            if(repairPersonShowCell != null && repairPersonShowCell.getAttributeCode().equals(AttributeEnum.ATTR_REPAIR_PERSON_SHOW.getValue())){
                List<String> repairShow = new ArrayList<String>();
                if(lstPersonNames.size() > 0){
                    for(String repair : lstPersonNames){
                        if(!repairShow.contains(repair)){
                            repairShow.add(repair);
                        }
                    }
                }
                if(repairShow.size()>0)
                    repairPersonShowCell.setValue(String.join(",", repairShow));
                else
                    repairPersonShowCell.setValue("");
                cellShow.add(repairPersonShowCell);
            }
            List<ChecklistDetailInfoForShow> lstDetails = updateRepairPerson(checklistSummary);
            if(lstDetails.size()>0){
                cellShow.addAll(lstDetails);
            }
            if(cellShow.size()>0){
                result.setChangedCells(cellShow);
            }
        }catch (Exception ex){
            logger.error(String.format("???????????????????????????????????????????????????????????????%s", checklistSummary.toString()), ex);
        }
        return result;
    }

    /**
     * ????????????????????????
     * @param totalCells ???????????????
     */
    public ChecklistDetailInfoForShow setRepairPerson(List<ChecklistDetailInfoForShow> totalCells){
        List<String> lstRepairPerson = new ArrayList<String>();
        ChecklistDetailInfoForShow repairPersonShowCell = new ChecklistDetailInfoForShow();
        for(ChecklistDetailInfoForShow cell : totalCells){
            if(cell.getAttributeCode().equals(AttributeEnum.ATTR_REPAIR_PERSON_SHOW.getValue())){
                BeanUtils.copyProperties(cell, repairPersonShowCell);
                continue;
            }
            if((cell.getAttributeCode().equals(AttributeEnum.ATTR_REPAIR_PERSON.getValue())||
                    cell.getAttributeCode().equals(AttributeEnum.ATTR_AXLE_TASKDOWN_A.getValue())||
                    cell.getAttributeCode().equals(AttributeEnum.ATTR_AXLE_TASKDOWN_B.getValue())||
                    cell.getAttributeCode().equals(AttributeEnum.ATTR_AXLE_TASKDOWN_E.getValue())||
                    cell.getAttributeCode().equals(AttributeEnum.ATTR_AXLE_INSTALL_A.getValue())||
                    cell.getAttributeCode().equals(AttributeEnum.ATTR_AXLE_INSTALL_B.getValue())||
                    cell.getAttributeCode().equals(AttributeEnum.ATTR_AXLE_INSTALL_E.getValue())||
                    cell.getAttributeCode().equals(AttributeEnum.ATTR_AXLE_PUTINTO.getValue())||
                    cell.getAttributeCode().equals(AttributeEnum.ATTR_QUALITY_AXLESIGN.getValue()))
                    && !StringUtils.isEmpty(cell.getValue()) && !cell.getValue().equals("N/A")) {
                if(!lstRepairPerson.contains(cell.getValue())){
                    lstRepairPerson.add(cell.getValue());
                }
            }
        }
        repairPersonShowCell.setValue(String.join(",", lstRepairPerson));
        return  repairPersonShowCell;
    }

    /**
     * ????????????????????????
     * @param checklistSummary
     * @return
     */
    private List<ChecklistDetailInfoForShow> updateRepairPerson(ChecklistSummaryInfoForSave checklistSummary){
        List<ChecklistDetailInfoForShow> changeCells = new ArrayList<ChecklistDetailInfoForShow>();
        try{
            ShiroUser currentUser = ShiroKit.getUser();
            List<BillCellChangeInfo<ChecklistDetailInfoForSave>> details = checklistSummary.getTriggerCells();
            for(BillCellChangeInfo<ChecklistDetailInfoForSave> cell : details){
                ChecklistDetailInfoForSave afterCell = cell.getAfterChangeCellInfo();
                if(StringUtils.isEmpty(afterCell.getValue()) || StringUtils.isEmpty(afterCell.getCode()))
                {
                    ChecklistDetailInfoForShow show = new ChecklistDetailInfoForShow();
                    BeanUtils.copyProperties(afterCell, show);
                    changeCells.add(show);
                    continue;
                }
                List<String> codeAfters = convertList(afterCell.getCode().split(","));
                List<String> nameAfters =  convertList(afterCell.getValue().split(","));
                if(isImportCell(codeAfters) && !codeAfters.contains(currentUser.getStaffId()) && nameAfters.contains(currentUser.getName())){
                    removeImport(codeAfters);
                    codeAfters.remove(currentUser.getStaffId());
                    nameAfters.remove(currentUser.getName());
                    if(!codeAfters.contains(currentUser.getStaffId()))
                        codeAfters.add(currentUser.getStaffId());
                    if(!nameAfters.contains(currentUser.getName()))
                        nameAfters.add(currentUser.getName());
                }
                //????????????????????????????????????????????????????????????????????????????????????ID??????????????????IMPORT????????????
                ChecklistDetailInfoForSave beforeCell = cell.getBeforeChangeCellInfo();
                List<String> codeBefores = convertList(beforeCell.getCode().split(","));
                List<String> nameBefores =  convertList(beforeCell.getValue().split(","));
                if(!codeBefores.contains(currentUser.getStaffId()) && nameBefores.contains(currentUser.getName())){
                    removeImport(codeAfters);
                }
                afterCell.setCode(String.join(",",codeAfters));
                afterCell.setValue(String.join(",",nameAfters));
                ChecklistDetailInfoForShow show = new ChecklistDetailInfoForShow();
                BeanUtils.copyProperties(afterCell, show);
                changeCells.add(show);
            }
        }catch (Exception ex){
            logger.error("????????????????????????", ex);
        }
        return changeCells;
    }

    /**
     * ???????????????
     * @param checklistSummary ???????????????
     * @return
     */
    public ChecklistTriggerUrlCallResult confirmPerson(ChecklistSummaryInfoForSave checklistSummary){
        ChecklistTriggerUrlCallResult result = new ChecklistTriggerUrlCallResult();
        try{
            List<ChecklistDetailInfoForShow> viewCells = new ArrayList<ChecklistDetailInfoForShow>();
            List<ChecklistDetailInfoForSave> cells = checklistSummary.getCells();
            for (ChecklistDetailInfoForSave cell : cells){
                if(cell.getAttributeCode().equals(AttributeEnum.ATTR_CONFIRM_PERSON.getValue())){
                    if(!StringUtils.isEmpty(cell.getValue())){
                        ChecklistDetailInfoForShow cellShow = new ChecklistDetailInfoForShow();
                        BeanUtils.copyProperties(cell,cellShow);
                        cellShow.setReadOnly(true);
                        viewCells.add(cellShow);
                    }
                }
                if(cell.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_TYZLJC.getValue()) ||
                        cell.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_TYZXJLJC.getValue())||
                        cell.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_TYCLLJC.getValue())||
                        cell.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_TCTYZXJLJC.getValue())||
                        cell.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_DCTYZXJLJC.getValue())||
                        cell.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_DCTYCLLJC.getValue())||
                        cell.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_TCTYCLLJC.getValue())||
                        cell.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_TYCLDYNCLJLJC.getValue())||
                        cell.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_TYDCNLTDLLDLJC.getValue())||
                        cell.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_XLCXLJC.getValue())||
                        cell.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_DCTYLDLJC.getValue())||
                        cell.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_TCTYLDLJC.getValue())||
                        cell.getAttributeCode().equals(AttributeEnum.ATTR_DEFECT_CLEAR.getValue())){
                    ChecklistDetailInfoForShow cellShow = new ChecklistDetailInfoForShow();
                    BeanUtils.copyProperties(cell,cellShow);
                    cellShow.setReadOnly(true);
                    viewCells.add(cellShow);
                }
            }
            result.setChangedCells(viewCells);
            if(result.getChangedCells() != null && result.getChangedCells().size() >0){
                checklistDetailService.updateByPrimaryKey(result.getChangedCells());
                for(int i = 0; i < result.getChangedCells().size(); i++){
                    ChecklistDetailInfoForShow s = (ChecklistDetailInfoForShow)result.getChangedCells().get(i);
                    s.setSaved(true);
                }
            }
        }catch (Exception ex){
            logger.error("???????????????", ex);
        }
        return  result;
    }

    /**
     * ?????????????????????????????????
     * @param saveInfo
     * @return
     */
    public ChecklistTriggerUrlCallResult setAxleQualitySign(ChecklistSummaryInfoForSave saveInfo) {
        ChecklistTriggerUrlCallResult result = new ChecklistTriggerUrlCallResult();
        try {
            saveCell(saveInfo);
        } catch (Exception e) {
            logger.error("?????????????????????????????????", e);
            throw e;
        }
        return result;
    }

    /**
     * ??????????????????????????????
     * @param saveInfo ???????????????
     * @return
     */
    public String checkIsChangeed(ChecklistSummaryInfoForSave saveInfo){
        String msg = "";
        try{
            int count = saveInfo.getCells().stream().filter(f-> !f.getChangeType().equals(BillEntityChangeTypeEnum.NO_CHANGE)).collect(Collectors.toList()).size();
            if(count >0){
                msg = "??????????????????????????????????????????";
            }
        }catch (Exception e){
            msg = "";
            logger.error("??????????????????????????????", e);
            throw e;
        }
        return msg;
    }

    /**
     * ??????????????????IMPORT
     * @param array ????????????IMPORT?????????
     * @return ??????????????????
     */
    public List<String> removeImport(List<String> array){
        List<String> result = new ArrayList<String>();
        try{
            if(array != null && array.size() >0){
                for(String val : array){
                    if(!isImportCell(val)){
                        result.add(val);
                    }
                }
            }
        }catch (Exception e){
            logger.error("??????????????????IMPORT:" + e.toString());
        }
        return  result;
    }

    /**
     * ????????????????????? IMPORT ?????????
     * @param array ??????????????????????????????
     * @return
     */
    public boolean isImportCell(List<String> array){
        boolean isImport = false;
        try{
            if(array != null && array.size() >0){
                for(String val : array){
                    isImport = isImportCell(val);
                    break;
                }
            }
        }catch (Exception e){
            logger.error("????????????????????? IMPORT ?????????:" + e.toString());
        }
        return  isImport;
    }

    /**
     * ???????????? IMPORT ?????????
     * @param value ????????????????????????
     * @return
     */
    private boolean isImportCell(String value){
        boolean isImport = false;
        try{
            if(StringUtils.isEmpty(value)){
                int index = value.indexOf("IMPORT");
                isImport = index >= 0;
            }
        }catch (Exception e){
            logger.error("???????????? IMPORT ?????????:" + e.toString());
        }
        return  isImport;
    }

    /**
     * ???????????????List????????????
     * @param array
     * @return
     */
    private List<String> convertList(String[] array){
        List<String> codes = new ArrayList<String>();
        for(String arr : array){
            if(!codes.contains(arr))
                codes.add(arr);
        }
        return codes;
    }

    /**
     * ??????????????????????????????????????????????????????????????????
     * @param cellValue ?????????
     * @param value ???????????????
     * @return
     */
    private boolean isHasValue(String cellValue, String value){
        if(StringUtils.hasText(cellValue)){
            String[] array = cellValue.split(",");
            List<String> lstCodes = convertList(array);
            return lstCodes.contains(value);
        }
        return false;
    }
    
    /**
     * ??????????????????
     *
     * @return
     */
    private List<ZtTaskItemEntity> getPlanItemEntity(ChecklistSummary s) {
        List<ZtTaskItemEntity> result = new ArrayList<ZtTaskItemEntity>();
        try{
            List<ZtTaskPacketEntity> taskPacketEntities = iRemoteService.getPacketTaskByCondition(s.getDayPlanId(), s.getTrainsetId(),"", s.getDeptCode(), s.getUnitCode());
            for(ZtTaskPacketEntity packetEntity : taskPacketEntities){
               result = packetEntity.getLstTaskItemInfo().stream().filter(f->f.getItemCode().equals(s.getItemCode())).collect(Collectors.toList());
               if(result.size() > 0){
                   break;
               }
            }
        }catch (Exception ex){
            logger.error("????????????????????????????????????????????????",ex);
        }
        return result;
    }

    /**
     * ????????????????????????
     * @param location ??????
     * @return ??????
     */
    private String convertAxle(String location){
        String axle = "";
        if(location.equals("1") || location.equals("2")){
            axle = "1";
        }
        if(location.equals("3") || location.equals("4")){
            axle = "2";
        }
        if(location.equals("5") || location.equals("6")){
            axle = "3";
        }
        if(location.equals("7") || location.equals("8")){
            axle = "4";
        }
        return axle;
    }

    /**
     * ????????????????????????????????????
     * @return ??????????????????
     */
    private List<String> getBackFillArea(){
        List<String> attrs = new ArrayList<String>();
        //?????????????????????
        attrs.add(AttributeEnum.ATTR_REPAIR_RESULT.getValue());
        attrs.add(AttributeEnum.ATTR_REPAIR_PERSON.getValue());
        //?????????????????????
        attrs.add(AttributeEnum.ATTR_NUMBERVALUE.getValue());
        //???????????????
        attrs.add(AttributeEnum.ATTR_WHEEL_ONEXQ.getValue());
        attrs.add(AttributeEnum.ATTR_WHEEL_ONEXH.getValue());
        attrs.add(AttributeEnum.ATTR_WHEEL_ONELYHDXQ.getValue());
        attrs.add(AttributeEnum.ATTR_WHEEL_ONELYHDXH.getValue());
        attrs.add(AttributeEnum.ATTR_WHEEL_ONELYGDXQ.getValue());
        attrs.add(AttributeEnum.ATTR_WHEEL_ONELYGDXH.getValue());
        attrs.add(AttributeEnum.ATTR_WHEEL_ONEQRXQ.getValue());
        attrs.add(AttributeEnum.ATTR_WHEEL_ONEQRXH.getValue());
        attrs.add(AttributeEnum.ATTR_WHEEL_TWOXQ.getValue());
        attrs.add(AttributeEnum.ATTR_WHEEL_TWOXH.getValue());
        attrs.add(AttributeEnum.ATTR_WHEEL_TWOLYHDXQ.getValue());
        attrs.add(AttributeEnum.ATTR_WHEEL_TWOLYHDXH.getValue());
        attrs.add(AttributeEnum.ATTR_WHEEL_TWOLYGDXQ.getValue());
        attrs.add(AttributeEnum.ATTR_WHEEL_TWOLYGDXH.getValue());
        attrs.add(AttributeEnum.ATTR_WHEEL_TWOQRXQ.getValue());
        attrs.add(AttributeEnum.ATTR_WHEEL_TWOQRXH.getValue());
        //??????????????????
        attrs.add(AttributeEnum.ATTR_AXLE_CONFIRM.getValue());
        attrs.add(AttributeEnum.ATTR_AXLE_INPOSTION.getValue());
        attrs.add(AttributeEnum.ATTR_AXLE_TASKDOWN_A.getValue());
        attrs.add(AttributeEnum.ATTR_AXLE_TASKDOWN_B.getValue());
        attrs.add(AttributeEnum.ATTR_AXLE_TASKDOWN_E.getValue());
        attrs.add(AttributeEnum.ATTR_AXLE_INSTALL_A.getValue());
        attrs.add(AttributeEnum.ATTR_AXLE_INSTALL_B.getValue());
        attrs.add(AttributeEnum.ATTR_AXLE_INSTALL_E.getValue());
        attrs.add(AttributeEnum.ATTR_AXLE_PUTINTO.getValue());
        attrs.add(AttributeEnum.ATTR_QUALITY_AXLESIGN.getValue());
        //??????????????????
        attrs.add(AttributeEnum.ATTR_WHEEL_CONFIRM.getValue());
        attrs.add(AttributeEnum.ATTR_REPAIR_ONERESULT.getValue());
        attrs.add(AttributeEnum.ATTR_REPAIR_TWORESULT.getValue());
        return attrs;
    }

}
