package com.ydzbinfo.emis.trainRepair.bill.fillback.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ydzbinfo.emis.trainRepair.bill.fillback.dao.ChecklistDetailMapper;
import com.ydzbinfo.emis.trainRepair.bill.fillback.service.*;
import com.ydzbinfo.emis.trainRepair.bill.fillback.utils.AttributeEnum;
import com.ydzbinfo.emis.trainRepair.bill.fillback.utils.BillCommon;
import com.ydzbinfo.emis.trainRepair.bill.fillback.utils.TemplateTypeNameEnum;
import com.ydzbinfo.emis.trainRepair.bill.general.BillCellChangeInfo;
import com.ydzbinfo.emis.trainRepair.bill.general.constant.BillEntityChangeTypeEnum;
import com.ydzbinfo.emis.trainRepair.bill.model.attribute.TemplateTypeInfo;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.*;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateQuery;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.*;
import com.ydzbinfo.emis.trainRepair.bill.model.templatesummary.TemplateSummaryInfo;
import com.ydzbinfo.emis.trainRepair.common.model.User;
import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.trainRepair.common.service.IRepairMidGroundService;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetBaseInfo;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.ItemTypeDict;
import com.ydzbinfo.emis.utils.*;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.eqParam;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @description: 无任务记录单
 * @date: 2021/12/1
 * @author: 冯帅
 */
@Service
public class NoPlanTaskCheckServiceImpl implements INoPlanTaskCheckService {
    //日志服务
    protected static final Logger logger = getLogger(NoPlanTaskCheckServiceImpl.class);

    @Autowired
    CheckInfoInit checkInfoInit;

    @Autowired
    BillCommon billCommon;

    @Autowired
    private IRepairMidGroundService midGroundService;

    @Autowired
    ChecklistDetailMapper checklistDetailMapper;

    @Autowired
    IChecklistIntegrationService checklistIntegrationService;

    @Autowired
    IChecklistLiveCheckService checklistLiveCheckService;

    @Autowired
    NoPlanTaskCheckInfoInit noPlanTaskCheckInfoInit;

    @Autowired
    IRemoteService remoteService;

    @Autowired
    IChecklistAreaService checklistAreaService;

    @Autowired
    IChecklistLinkControlService checklistLinkControlService;

    @Autowired
    IChkDetailLinkContentService chkDetailLinkContentService;


    /***
     * @author: 冯帅
     * @date: 2021/12/15
     * @description: 初始化获取详细数据
     */
    @Override
    public SummaryInfoForShow getNoPlanTaskSummaryInfo(String summaryId,String templateTypeCode) {
        SummaryInfoForShow res = new SummaryInfoForShow<>();
        //单元格是否初始化过
        boolean initialized = true;
        if(StringUtils.isBlank(summaryId)){
            summaryId = UUID.randomUUID().toString();
            initialized=false;
        }
        //2.子表相关数据
        //2.1获取当前用户段所信息
        User userInfo = UserUtil.getUserInfo();
        //段编码
        String depotCode = ContextUtils.getDepotCode();
        //运用所编码
        String unitCode = ContextUtils.getUnitCode();
        //运用所名称
        String unitName = ContextUtils.getUnitName();
        //班组编码
        String deptCode = userInfo.getWorkTeam().getTeamCode();
        //班组名称
        String deptName = userInfo.getWorkTeam().getTeamName();
        //2.2根据单据类型编码获取模板id
        String templateQueryUnitCode = unitCode;
        TemplateTypeInfo templateTypeInfo = billCommon.getTemplateTypeInfoCache(templateTypeCode);
        if (templateTypeInfo.getLinkQueries() != null) {
            //处理运用所条件
            List<TemplateQuery> templateQueriesUnitCode = templateTypeInfo.getLinkQueries().stream().filter((TemplateQuery t) -> t.getQueryCode().equals("Unit")).collect(Collectors.toList());
            if (templateQueriesUnitCode.size() == 0) {
                templateQueryUnitCode = "";
            }
        }
        List<TemplateSummaryInfo> templateSummaryInfoList = billCommon.getTemplateCache(depotCode, templateQueryUnitCode, templateTypeCode, null, null, null, null, 0);
        //2.2调用PC接口获取子表详细数据
        if(!CollectionUtils.isEmpty(templateSummaryInfoList)){
            TemplateSummaryInfo templateSummaryInfo = templateSummaryInfoList.stream().findFirst().orElse(null);
            if(!ObjectUtils.isEmpty(templateSummaryInfo)){
                //主表数据
                if(StringUtils.isNotBlank(templateTypeCode)){
                    if(templateTypeCode.equals(TemplateTypeNameEnum.MANAGER_INTEGRATION_CELL.getValue())){
                        res = new SummaryInfoForShow<ChecklistIntegration>();
                        ChecklistIntegration checklistIntegration = new ChecklistIntegration();
                        if(initialized){
                            List<ChecklistIntegration> checklistIntegrationList = MybatisPlusUtils.selectList(checklistIntegrationService, eqParam(ChecklistIntegration::getChecklistSummaryId, summaryId));
                            if(!CollectionUtils.isEmpty(checklistIntegrationList)){
                                checklistIntegration = checklistIntegrationList.stream().findFirst().orElse(null);
                            }
                        }else{
                            checklistIntegration.setChecklistSummaryId(summaryId);
                            checklistIntegration.setTemplateType(templateSummaryInfo.getTemplateTypeCode());
                            checklistIntegration.setTemplateId(templateSummaryInfo.getTemplateId());
                            checklistIntegration.setTemplateNo(templateSummaryInfo.getTemplateNo());
                            checklistIntegration.setDeptCode(deptCode);
                            checklistIntegration.setDeptName(deptName);
                            checklistIntegration.setUnitCode(unitCode);
                            checklistIntegration.setUnitName(unitName);
                        }
                        res.setExtraObject(checklistIntegration);
                    }else if(templateTypeCode.equals(TemplateTypeNameEnum.MANAGER_LIVECHECK_CELL.getValue())){
                        res = new SummaryInfoForShow<ChecklistLiveCheck>();
                        ChecklistLiveCheck checklistLiveCheck = new ChecklistLiveCheck();
                        if(initialized){
                            List<ChecklistLiveCheck> checklistLiveCheckList =  MybatisPlusUtils.selectList(checklistLiveCheckService, eqParam(ChecklistLiveCheck::getChecklistSummaryId, summaryId));
                            if(!CollectionUtils.isEmpty(checklistLiveCheckList)){
                                checklistLiveCheck = checklistLiveCheckList.stream().findFirst().orElse(null);
                            }
                        }else{
                            checklistLiveCheck.setChecklistSummaryId(summaryId);
                            checklistLiveCheck.setTemplateType(templateSummaryInfo.getTemplateTypeCode());
                            checklistLiveCheck.setTemplateId(templateSummaryInfo.getTemplateId());
                            checklistLiveCheck.setTemplateNo(templateSummaryInfo.getTemplateNo());
                            checklistLiveCheck.setUnitCode(unitCode);
                            checklistLiveCheck.setUnitName(unitName);
                        }
                        res.setExtraObject(checklistLiveCheck);
                    }
                }
                //子表数据
                ChecklistSummaryInfoForShow detailInfoShow = checkInfoInit.getDetailInfoShow(summaryId, templateSummaryInfo.getTemplateId(), templateTypeCode,false);
                if(!ObjectUtils.isEmpty(detailInfoShow)){
                    res.setAreas(detailInfoShow.getAreas());
                    //设置单元格集合及是否初始化信息
                    List<ChecklistDetailInfoForShow> cells = detailInfoShow.getCells();
                    if(!CollectionUtils.isEmpty(cells)){
                        if(!initialized){//如果单元格没有初始化，则将saved和initialized两个属性值都设置为false
                            cells = cells.stream().map(detailItem->{
                                detailItem.setSaved(false);
                                detailItem.setInitialized(false);
                                return detailItem;
                            }).collect(Collectors.toList());
                        }
                        res.setCells(cells);
                        //初始化数据
                        List<ChecklistDetailInfoForShow> initDataCells = noPlanTaskCheckInfoInit.initContentData(res,templateTypeCode);
                        res.setCells(initDataCells);
                        //初始化规则
                        List<ChecklistDetailInfoForShow> initRuleCells = noPlanTaskCheckInfoInit.initContentRule(res);
                        res.setCells(initRuleCells);
                    }
                }
            }
        }
        //单据类型编码
        res.setTemplateType(templateTypeCode);
        return res;
    }

    @Override
    public ChecklistTriggerUrlCallResult saveNoPlanTaskRepairCell(SummaryInfoForSave summaryInfoForSave){
        ChecklistTriggerUrlCallResult result = new ChecklistTriggerUrlCallResult();
        boolean isAll = true;
        //从履历中获取所有车组的信息
        List<TrainsetBaseInfo> trainsetList = remoteService.getTrainsetList();
        //1.获取当前登录人信息
        User userInfo = UserUtil.getUserInfo();
        //1.1单据最后一次回填时间
        Date currentDate = new Date();
        //1.2单据最后一次回填人编码
        String lastFillWorkCode = userInfo.getStaffId();
        //1.3单据最后一次回填人名称
        String lastFillWorkName = userInfo.getName();
        //1.4单据最后一次回填设备名称
        String lastHostName = "";
        //1.5单据最后一次回填设备ip地址
        String lastIPAdress = userInfo.getIpAddress();
        //2.调用PC接口保存单元格信息
        List<ChecklistDetailInfoForSave> cells = summaryInfoForSave.getCells();
        List<ChecklistAreaInfoForSave> areas = summaryInfoForSave.getAreas();
        //当前单元格
        List<BillCellChangeInfo<ChecklistDetailInfoForSave>> triggerCells = summaryInfoForSave.getTriggerCells();
        JSONObject extraObject = (JSONObject) summaryInfoForSave.getExtraObject();
        String templateTypeCode = "";
        if(!ObjectUtils.isEmpty(extraObject)&&!ObjectUtils.isEmpty(extraObject.getString("templateType"))){
            templateTypeCode = extraObject.getString("templateType");
        }
        String summaryId = "";
        ChecklistIntegration checklistIntegration = new ChecklistIntegration();
        ChecklistLiveCheck checklistLiveCheck = new ChecklistLiveCheck();
        if(!CollectionUtils.isEmpty(cells)){
            result.setAllowChange(true);
            if(templateTypeCode.equals(TemplateTypeNameEnum.MANAGER_INTEGRATION_CELL.getValue())){
                String strs = JSONObject.toJSONString(summaryInfoForSave.getExtraObject());
                checklistIntegration = JSONObject.parseObject(strs,ChecklistIntegration.class);
                summaryId = checklistIntegration.getChecklistSummaryId();
                checklistIntegration.setLastFillTime(currentDate);
                checklistIntegration.setLastFillWorkCode(lastFillWorkCode);
                checklistIntegration.setLastFillWorkName(lastFillWorkName);
                checklistIntegration.setLastHostName(lastHostName);
                checklistIntegration.setLastIpAddress(lastIPAdress);
                //循环单元格，将部分单元格数据更新到主表字段中
                for(ChecklistDetailInfoForSave cell : cells){
                    if(cell.getAttributeCode().equals(AttributeEnum.ATTR_APPLY_APPLYPERSON.getValue())){//一体化申请单—申请人
                        checklistIntegration.setStuffId(cell.getCode());
                        checklistIntegration.setStuffName(cell.getValue());
                    }else if(cell.getAttributeCode().equals(AttributeEnum.ATTR_APPLY_TRAINSETID.getValue())){//一体化申请单—车组号
                        if(StringUtils.isBlank(cell.getValue())){
                            result.setAllowChange(false);
                            result.setOperationResultMessage("车组号不能为空!");
                            break;
                        }else{
                            TrainsetBaseInfo trainsetBaseInfo = Optional.ofNullable(trainsetList).orElseGet(ArrayList::new).stream().filter(t -> t.getTrainsetname().equals(cell.getValue())).findFirst().orElse(null);
                            if(!ObjectUtils.isEmpty(trainsetBaseInfo)){
                                checklistIntegration.setTrainsetId(trainsetBaseInfo.getTrainsetid());
                                checklistIntegration.setTrainsetName(trainsetBaseInfo.getTrainsetname());
                            }
                        }
                    }else if(cell.getAttributeCode().equals(AttributeEnum.ATTR_APPLY_BEGINTIME.getValue())){//一体化申请单—申请开始时间
                        if(StringUtils.isBlank(cell.getValue())){
                            result.setAllowChange(false);
                            result.setOperationResultMessage("作业申请开始时间不能为空!");
                            break;
                        }else {
                            try{
                                Date applyBeginTime =DateUtils.strToFormatDate(cell.getValue(),"yyyy-MM-dd HH:mm");
                                checklistIntegration.setApplyBeginTime(applyBeginTime);
                            }catch (Exception ex){
                                result.setAllowChange(false);
                                result.setOperationResultMessage("作业申请开始时间格式错误!");
                            }
                        }
                    }else if(cell.getAttributeCode().equals(AttributeEnum.ATTR_APPLY_ENDTIME.getValue())){//一体化申请单—申请结束时间
                        if(StringUtils.isBlank(cell.getValue())){
                            result.setAllowChange(false);
                            result.setOperationResultMessage("作业申请结束时间不能为空!");
                            break;
                        }else {
                            try{
                                Date applyEndTime = DateUtils.strToFormatDate(cell.getValue(),"yyyy-MM-dd HH:mm");
                                if(!checklistIntegration.getApplyBeginTime().before(applyEndTime)){
                                    result.setAllowChange(false);
                                    result.setOperationResultMessage("作业申请开始时间不能大于或等于申请结束时间!");
                                    break;
                                }
                                checklistIntegration.setApplyEndTime(applyEndTime);
                            }catch (Exception ex){
                                result.setAllowChange(false);
                                result.setOperationResultMessage("作业申请结束时间格式错误!");
                                break;
                            }
                        }
                    }else if(cell.getAttributeCode().equals(AttributeEnum.ATTR_APPLY_CONTENT.getValue())){//一体化申请单—作业内容
                        checklistIntegration.setApplyContent(cell.getValue());
                    }else if(cell.getAttributeCode().equals(AttributeEnum.ATTR_DISPATCH_SIGN.getValue())){//一体化申请单-调度签字
                        ChecklistDetailInfoForSave checklistDetailInfoForSave = this.afterCell(triggerCells, cell.getAttributeCode());
                        if(!ObjectUtils.isEmpty(checklistDetailInfoForSave)){
                            BeanUtils.copyProperties(checklistDetailInfoForSave,cell);
                        }
                        checklistIntegration.setDispatchSign(cell.getValue());
                    }else if(cell.getAttributeCode().equals(AttributeEnum.ATTR_TECHNIQUE_SIGN.getValue())){//一体化申请单—技术签字
                        ChecklistDetailInfoForSave checklistDetailInfoForSave = this.afterCell(triggerCells, cell.getAttributeCode());
                        if(!ObjectUtils.isEmpty(checklistDetailInfoForSave)){
                            BeanUtils.copyProperties(checklistDetailInfoForSave,cell);
                        }
                        checklistIntegration.setTechniqueSign(cell.getValue());
                    }else if(cell.getAttributeCode().equals(AttributeEnum.ATTR_DIRECTOR_SIGN.getValue())){//一体化申请单—所长签字
                        ChecklistDetailInfoForSave checklistDetailInfoForSave = this.afterCell(triggerCells, cell.getAttributeCode());
                        if(!ObjectUtils.isEmpty(checklistDetailInfoForSave)){
                            BeanUtils.copyProperties(checklistDetailInfoForSave,cell);
                        }
                        checklistIntegration.setDirectorSign(cell.getValue());
                    }else if(cell.getAttributeCode().equals(AttributeEnum.ATTR_FINISH_WORKPERSON_SIGN.getValue())){//一体化申请单—作业负责人签字
                        ChecklistDetailInfoForSave checklistDetailInfoForSave = this.afterCell(triggerCells, cell.getAttributeCode());
                        if(!ObjectUtils.isEmpty(checklistDetailInfoForSave)){
                            BeanUtils.copyProperties(checklistDetailInfoForSave,cell);
                        }
                        checklistIntegration.setFinishWorkPersonSign(cell.getValue());
                    }else if(cell.getAttributeCode().equals(AttributeEnum.ATTR_FINISH_DISPATCH_SIGN.getValue())){//一体化申请单—调度销记签字
                        ChecklistDetailInfoForSave checklistDetailInfoForSave = this.afterCell(triggerCells, cell.getAttributeCode());
                        if(!ObjectUtils.isEmpty(checklistDetailInfoForSave)){
                            BeanUtils.copyProperties(checklistDetailInfoForSave,cell);
                        }
                        checklistIntegration.setFinishDispatchSign(cell.getValue());
                    }
                }
            }else if(templateTypeCode.equals(TemplateTypeNameEnum.MANAGER_LIVECHECK_CELL.getValue())){
                String strs = JSONObject.toJSONString(summaryInfoForSave.getExtraObject());
                checklistLiveCheck = JSONObject.parseObject(strs,ChecklistLiveCheck.class);
                // checklistLiveCheck = (ChecklistLiveCheck)summaryInfoForSave.getExtraObject();
                summaryId = checklistLiveCheck.getChecklistSummaryId();
                checklistLiveCheck.setLastFillTime(currentDate);
                checklistLiveCheck.setLastFillWorkCode(lastFillWorkCode);
                checklistLiveCheck.setLastFillWorkName(lastFillWorkName);
                checklistLiveCheck.setLastHostName(lastHostName);
                checklistLiveCheck.setLastIpAddress(lastIPAdress);
                checklistLiveCheck.setCreatestuffid(lastFillWorkCode);
                checklistLiveCheck.setCreatestuffname(lastFillWorkName);
                checklistLiveCheck.setCreateTime(currentDate);
                //循环单元格，将部分单元格数据更新到主表字段中
                for(ChecklistDetailInfoForSave cell : cells){
                    if(cell.getAttributeCode().equals(AttributeEnum.ATTR_LEAVE_TRAINSETID.getValue())){//出所联检单—出所联检车组
                        if(StringUtils.isBlank(cell.getValue())){
                            result.setAllowChange(false);
                            result.setOperationResultMessage("车组号不能为空!");
                            break;
                        }else{
                            TrainsetBaseInfo trainsetBaseInfo = Optional.ofNullable(trainsetList).orElseGet(ArrayList::new).stream().filter(t -> t.getTrainsetname().equals(cell.getValue())).findFirst().orElse(null);
                            if(!ObjectUtils.isEmpty(trainsetBaseInfo)){
                                checklistLiveCheck.setTrainsetId(trainsetBaseInfo.getTrainsetid());
                                checklistLiveCheck.setTrainsetName(trainsetBaseInfo.getTrainsetname());
                            }
                        }
                    }else if(cell.getAttributeCode().equals(AttributeEnum.ATTR_LEAVE_CONNECT_TRACK.getValue())){//出所联检单—交接股道
                        if(StringUtils.isBlank(cell.getValue())){
                            result.setAllowChange(false);
                            result.setOperationResultMessage("交接股道不能为空!");
                            break;
                        }else{
                            checklistLiveCheck.setTrack(cell.getValue());
                        }
                    }else if(cell.getAttributeCode().equals(AttributeEnum.ATTR_LEAVE_CONNECT_TIME.getValue())){//出所联检单—交接完成时间
                        if(StringUtils.isBlank(cell.getValue())){
                            result.setAllowChange(false);
                            result.setOperationResultMessage("交接完成时间不能为空!");
                            break;
                        }else{
                            checklistLiveCheck.setConnectTime(DateUtils.strToFormatDate(cell.getValue(),"yyyy-MM-dd HH:mm"));
                        }
                    }
                }
            }
            if(StringUtils.isNotBlank(summaryId)&&result.getAllowChange()){
                // boolean isChanged = billCommon.saveCellDetail(cells, summaryId);
                boolean isChanged = this.operationCells(cells,areas,summaryId);
                //4.插入/更新主表数据
                if(templateTypeCode.equals(TemplateTypeNameEnum.MANAGER_INTEGRATION_CELL.getValue())){
                    boolean b = checklistIntegrationService.insertOrUpdate(checklistIntegration);
                    result.setExtraObject(checklistIntegration);
                }else if(templateTypeCode.equals(TemplateTypeNameEnum.MANAGER_LIVECHECK_CELL.getValue())){
                    boolean b = checklistLiveCheckService.insertOrUpdate(checklistLiveCheck);
                    result.setExtraObject(checklistLiveCheck);
                }
                //5.更新所有已经保存数据的状态
                List<ChecklistDetailInfoForShow> showChangedCells = new ArrayList<>();
                billCommon.UpdateSavedState(showChangedCells, cells);
                //6.保存之后，出所联检车组号、交接股道不能修改
                //6.1出所联检车组号属性设置只读
                ChecklistDetailInfoForShow liveCheckTrainsetIdShow = showChangedCells.stream().filter(t -> t.getAttributeCode().equals(AttributeEnum.ATTR_LEAVE_TRAINSETID.getValue())).findFirst().orElse(null);
                if(!ObjectUtils.isEmpty(liveCheckTrainsetIdShow)){
                    liveCheckTrainsetIdShow.setReadOnly(true);
                }
                //6.2出所联检交接股道属性设置只读
                ChecklistDetailInfoForShow liveCheckTrackShow = showChangedCells.stream().filter(t -> t.getAttributeCode().equals(AttributeEnum.ATTR_LEAVE_CONNECT_TRACK.getValue())).findFirst().orElse(null);
                if(!ObjectUtils.isEmpty(liveCheckTrackShow)){
                    liveCheckTrackShow.setReadOnly(true);
                }
                //7.出所联检单根据当前登录人的角色来交接完成时间设置只读状态
                ChecklistDetailInfoForShow liveCheckConnectTimeShow = showChangedCells.stream().filter(t -> t.getAttributeCode().equals(AttributeEnum.ATTR_LEAVE_CONNECT_TIME.getValue())).findFirst().orElse(null);
                if(!ObjectUtils.isEmpty(liveCheckConnectTimeShow)){
                    boolean b = noPlanTaskCheckInfoInit.currentUserAttributeIsReadOnly(AttributeEnum.ATTR_LEAVE_QUALITY_SIGN);
                    liveCheckConnectTimeShow.setReadOnly(b);
                }
                //8.改变的单元格集合中有车辆质检员签字的情况下， 根据签字/撤销签字操作来设置交接完成时间单元格的只读状态
                if(!CollectionUtils.isEmpty(triggerCells)){
                    List<ChecklistDetailInfoForSave> afterSaveList = triggerCells.stream().map(t -> t.getAfterChangeCellInfo()).collect(Collectors.toList());
                    if(!CollectionUtils.isEmpty(afterSaveList)){
                        List<ChecklistDetailInfoForSave> qualitySignAfterSaveList = afterSaveList.stream().filter(t -> t.getAttributeCode().equals(AttributeEnum.ATTR_LEAVE_QUALITY_SIGN.getValue())).collect(Collectors.toList());
                        if(!CollectionUtils.isEmpty(qualitySignAfterSaveList)){
                            //8.出所联检单车辆质检员签字/撤销签字操作，设置交接完成时间的只读状态
                            ChecklistDetailInfoForShow liveCheckQualitySignShow = showChangedCells.stream().filter(t -> t.getAttributeCode().equals(AttributeEnum.ATTR_LEAVE_QUALITY_SIGN.getValue())&&StringUtils.isNotBlank(t.getValue())).findFirst().orElse(null);
                            //8.1签字操作
                            if(!ObjectUtils.isEmpty(liveCheckQualitySignShow)){
                                //8.1.1如果能获取到交接完成时间单元格子，则直接将其只读属性设置为true
                                ChecklistDetailInfoForShow qualitySignConnectTimeShow = showChangedCells.stream().filter(t -> t.getAttributeCode().equals(AttributeEnum.ATTR_LEAVE_CONNECT_TIME.getValue())).findFirst().orElse(null);
                                if(!ObjectUtils.isEmpty(qualitySignConnectTimeShow)){
                                    qualitySignConnectTimeShow.setReadOnly(true);
                                }else{//8.2.2否则创建交接完成时间show属性，将其只读属性设置为true
                                    qualitySignConnectTimeShow = new ChecklistDetailInfoForShow();
                                    ChecklistDetailInfoForSave connectTimeSave = cells.stream().filter(t -> t.getAttributeCode().equals(AttributeEnum.ATTR_LEAVE_CONNECT_TIME.getValue())).findFirst().orElse(null);
                                    if(!ObjectUtils.isEmpty(connectTimeSave)){
                                        BeanUtils.copyProperties(connectTimeSave,qualitySignConnectTimeShow);
                                        qualitySignConnectTimeShow.setSaved(true);
                                        qualitySignConnectTimeShow.setReadOnly(true);
                                        showChangedCells.add(qualitySignConnectTimeShow);
                                    }
                                }
                            }
                            //8.2撤销签字操作
                            ChecklistDetailInfoForShow liveCheckQualityRollBackSignShow = showChangedCells.stream().filter(t -> t.getAttributeCode().equals(AttributeEnum.ATTR_LEAVE_QUALITY_SIGN.getValue())&&StringUtils.isBlank(t.getValue())).findFirst().orElse(null);
                            //车辆质检员签字单元格属性集合
                            ChecklistDetailInfoForSave qualitySignSave = cells.stream().filter(t -> t.getAttributeCode().equals(AttributeEnum.ATTR_LEAVE_QUALITY_SIGN.getValue()) && StringUtils.isNotBlank(t.getValue())).findFirst().orElse(null);
                            if(!ObjectUtils.isEmpty(liveCheckQualityRollBackSignShow)&&ObjectUtils.isEmpty(qualitySignSave)){
                                //8.2.1如果能获取到交接完成时间单元格子，则直接将其只读属性设置为false
                                ChecklistDetailInfoForShow qualityRollBackSignConnectTimeShow = showChangedCells.stream().filter(t -> t.getAttributeCode().equals(AttributeEnum.ATTR_LEAVE_CONNECT_TIME.getValue())).findFirst().orElse(null);
                                if(!ObjectUtils.isEmpty(qualityRollBackSignConnectTimeShow)){
                                    qualityRollBackSignConnectTimeShow.setReadOnly(false);
                                }else{//8.2.2创建交接完成时间show属性，将其设置为只读
                                    qualityRollBackSignConnectTimeShow = new ChecklistDetailInfoForShow();
                                    ChecklistDetailInfoForSave connectTimeSave = cells.stream().filter(t -> t.getAttributeCode().equals(AttributeEnum.ATTR_LEAVE_CONNECT_TIME.getValue())).findFirst().orElse(null);
                                    if(!ObjectUtils.isEmpty(connectTimeSave)){
                                        BeanUtils.copyProperties(connectTimeSave,qualityRollBackSignConnectTimeShow);
                                        qualityRollBackSignConnectTimeShow.setSaved(true);
                                        qualityRollBackSignConnectTimeShow.setReadOnly(false);
                                        showChangedCells.add(qualityRollBackSignConnectTimeShow);
                                    }
                                }
                            }
                        }
                    }
                }
                result.setChangedCells(showChangedCells);
            }
        }
        return result;
    }

    //判断当前单元格是否变更
    public ChecklistDetailInfoForSave afterCell(List<BillCellChangeInfo<ChecklistDetailInfoForSave>> triggerCells,String attributeCode){
        ChecklistDetailInfoForSave result = null;
        if(!CollectionUtils.isEmpty(triggerCells)&&!ObjectUtils.isEmpty(attributeCode)){
            List<ChecklistDetailInfoForSave> cells = triggerCells.stream().map(v->v.getAfterChangeCellInfo()).collect(Collectors.toList());
            result = triggerCells.stream().filter(t -> !ObjectUtils.isEmpty(t.getAfterChangeCellInfo()) && attributeCode.equals(t.getAfterChangeCellInfo().getAttributeCode())).map(v->v.getAfterChangeCellInfo()).findFirst().orElse(null);
        }
        return result;
    }

    //对单元格数据及其关联数据和控制数据进行：插入、删除、修改操作
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean operationCells(List<ChecklistDetailInfoForSave> cells,List<ChecklistAreaInfoForSave> areas,String summaryId){
        boolean res = false;
        boolean isAdd = true;//是否是新增操作
        //1.循环单元格，组织需要保存的实体集合
        if(!CollectionUtils.isEmpty(cells)){
            List<ChecklistLinkControl> controlList = new ArrayList<>();
            List<ChkDetailLinkContent> linkContentList = new ArrayList<>();
            for(ChecklistDetailInfoForSave cell:cells){
                ChecklistDetail detail = new ChecklistDetail();
                BeanUtils.copyProperties(cell, detail);
                List<ChkDetailLinkContentForModule> linkContents = cell.getLinkCells();
                List<ChecklistLinkControl> controls = cell.getControls();
                if(cell.getChangeType().equals(BillEntityChangeTypeEnum.INSERT)) {
                    //1.1组织控制数据
                    if(!CollectionUtils.isEmpty(controls)){
                        for(ChecklistLinkControl control: controls){
                            control.setId(UUID.randomUUID().toString());
                            control.setChecklistSummaryId(summaryId);
                            control.setContentId(detail.getId());
                        }
                        controlList.addAll(controls);
                    }
                    //1.2组织关联数据
                    for(ChkDetailLinkContentForModule linkContentForModule:linkContents){
                        ChkDetailLinkContent linkContent = new ChkDetailLinkContent();
                        linkContent.setChecklistSummaryId(summaryId);
                        linkContent.setContentId(detail.getId());
                        linkContent.setLinkContentId(linkContentForModule.getLinkCellId());
                        linkContentList.add(linkContent);
                    }
                    //1.3插入表格数据
                    detail.setChecklistSummaryId(summaryId);
                    checklistDetailMapper.insert(detail);
                    res = true;
                }else if(cell.getChangeType().equals(BillEntityChangeTypeEnum.DELETE)) {
                    //1.4根据主键删除单元格数据
                    checklistDetailMapper.deleteByPrimaryKey(detail);
                    res = true;
                    isAdd = false;
                }else if(cell.getChangeType().equals(BillEntityChangeTypeEnum.UPDATE)) {
                    //1.5根据主键更新单元格数据
                    checklistDetailMapper.updatePrimaryKey(detail);
                    res = true;
                    isAdd = false;
                }
            }
            //2.如果是新增操作，则插入区域数据、控制数据、关联数据
            if(isAdd){
                //2.1插入区域数据
                if(!CollectionUtils.isEmpty(areas)){
                    List<ChecklistArea> checklistAreaList = new ArrayList<>();
                    for(ChecklistAreaInfoForSave area:areas){
                        ChecklistArea checklistArea = new ChecklistArea();
                        BeanUtils.copyProperties(area,checklistArea);
                        checklistAreaList.add(checklistArea);
                    }
                    if(!CollectionUtils.isEmpty(checklistAreaList)){
                        checklistAreaService.insertBatch(checklistAreaList);
                    }
                }
                //2.2插入控制数据
                if(!CollectionUtils.isEmpty(controlList)){
                    checklistLinkControlService.insertBatch(controlList);
                }
                //2.3插入关联数据
                if(!CollectionUtils.isEmpty(linkContentList)){
                    chkDetailLinkContentService.insertBatch(linkContentList);
                }
            }
        }
        return res;
    }

}
