package com.ydzbinfo.emis.trainRepair.mobile.fillback.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.trainRepair.bill.fillback.dao.ChecklistDetailMapper;
import com.ydzbinfo.emis.trainRepair.bill.fillback.service.ICheckInfoService;
import com.ydzbinfo.emis.trainRepair.bill.fillback.service.IChecklistSummaryService;
import com.ydzbinfo.emis.trainRepair.bill.fillback.service.impl.CheckInfoServiceImpl;
import com.ydzbinfo.emis.trainRepair.bill.fillback.service.impl.NoPlanTaskCheckInfoInit;
import com.ydzbinfo.emis.trainRepair.bill.fillback.utils.AttributeEnum;
import com.ydzbinfo.emis.trainRepair.bill.fillback.utils.BillCommon;
import com.ydzbinfo.emis.trainRepair.bill.fillback.utils.TemplateTypeNameEnum;
import com.ydzbinfo.emis.trainRepair.bill.general.BillCellChangeInfo;
import com.ydzbinfo.emis.trainRepair.bill.general.constant.BillEntityChangeTypeEnum;
import com.ydzbinfo.emis.trainRepair.bill.model.attribute.TemplateAttributeForSave;
import com.ydzbinfo.emis.trainRepair.bill.model.attribute.TemplateAttributeForShow;
import com.ydzbinfo.emis.trainRepair.bill.model.attribute.TemplateAttributeQueryParamModel;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.*;
import com.ydzbinfo.emis.trainRepair.bill.model.phoneBill.*;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateValue;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.base.TemplateTypeBase;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChecklistDetail;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChecklistSummary;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChkDetailLinkContent;
import com.ydzbinfo.emis.trainRepair.common.service.IRepairMidGroundService;
import com.ydzbinfo.emis.trainRepair.constant.MarshallingTypeEnum;
import com.ydzbinfo.emis.trainRepair.mobile.fillback.dao.PhoneOneTwoMapper;
import com.ydzbinfo.emis.trainRepair.mobile.fillback.service.IPhoneOneTwoService;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetBaseInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.FlowExtraInfo;
import com.ydzbinfo.emis.utils.CacheUtil;
import com.ydzbinfo.emis.utils.StringUtils;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import lombok.Data;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.eqParam;
import static org.slf4j.LoggerFactory.getLogger;


/**
 * @description: 手持机记录单列表查询接口实现类
 * @date: 2021/10/27
 * @author: 冯帅
 */
@Service
public class PhoneOneTwoServiceImpl extends ServiceImpl<PhoneOneTwoMapper, ChecklistSummary> implements IPhoneOneTwoService {

    //日志记录
    protected static final Logger logger = getLogger(CheckInfoServiceImpl.class);

    //PC端记录单列表服务
    @Autowired
    IChecklistSummaryService iChecklistSummaryService;

    //PC记录单详细信息服务
    @Autowired
    ICheckInfoService checkInfoService;

    //中台服务
    @Autowired
    IRepairMidGroundService repairMidGroundService;

    //手持机初始化规则服务
    @Autowired
    PhoneFirstPersonKeepAndTest phoneFirstPersonKeepAndTest;

    @Autowired
    BillCommon billCommon;

    @Autowired
    ChecklistDetailMapper checklistDetailMapper;

    @Autowired
    NoPlanTaskCheckInfoInit noPlanTaskCheckInfoInit;


    /***
     * @description: 手持机一二级修记录单获取列表数据
     * @author: 冯帅
     * @date: 2021/10/27
     * @param: [queryCheckListSummary]
     * @return: com.ydzbinfo.emis.trainRepair.bill.model.phoneBill.PhoneOneTwoRepairCheck
     */
    @Override
    public PhoneOneTwoRepairCheck getPhoneCheckList(QueryCheckListSummary queryCheckListSummary) {
        PhoneOneTwoRepairCheck phoneOneTwoRepairCheck = new PhoneOneTwoRepairCheck();//返回结果
        List<TrainsetBaseInfo> trainseList = new ArrayList<>();//查询条件-车组基本信息集合
        List<FillState> fillStateList = new ArrayList<>();//查询条件-单据状态集合
        List<String> marshallingTypeList = new ArrayList<>();//查询条件-编组形式集合
        List<TemplateTypeBase> templateTypeList = new ArrayList<>();//查询条件-单据类型集合
        String itemCode = queryCheckListSummary.getItemCode();
        Date beginTime = new Date();
        logger.info("----开始调用PC一二级修记录单列表查询接口...");
        //1.调用PC接口从数据库中获取数据
        List<OneTwoRepairCheckList> checklistSummaryList = iChecklistSummaryService.getChecklistSummaryList(queryCheckListSummary);
        Date endTime = new Date();
        logger.info("----调用查PC一二级修记录单列表查询接口结束，共耗时："+(endTime.getTime() - beginTime.getTime()));
        //1.2过滤掉一级修和二级修预防性之外的记录单
        checklistSummaryList = Optional.ofNullable(checklistSummaryList).orElseGet(ArrayList::new).stream().filter(t->("一级修".equals(t.getMainCyc()))||("二级修".equals(t.getMainCyc())&&TemplateTypeNameEnum.RECORD_SECOND_PREVENTIVE.getValue().equals(t.getTemplateType()))).collect(Collectors.toList());
        //1.3如果查询参数项目编码不为空，则根据项目编码过滤
        if(StringUtils.isNotBlank(itemCode)){
            checklistSummaryList = Optional.ofNullable(checklistSummaryList).orElseGet(ArrayList::new).stream().filter(t->itemCode.equals(t.getItemCode())).collect(Collectors.toList());
        }
        if(!CollectionUtils.isEmpty(checklistSummaryList)){
            //1.4根据单据配置情况排序，将已配置的排在前边
            checklistSummaryList = checklistSummaryList.stream().sorted(Comparator.comparing(OneTwoRepairCheckList::getTemplateExisit)).collect(Collectors.toList());
            //2.组织查询条件实体
            checklistSummaryList.stream().map(summary->{
                //2.0将从PC接口查询出来的数据克隆到手持机实体中并
                //2.1查询条件-车组信息集合
                String trainsetId = summary.getTrainsetId();
                String trainsetName = summary.getTrainsetName();
                TrainsetBaseInfo trainsetBaseInfo = new TrainsetBaseInfo();
                trainsetBaseInfo.setTrainsetid(trainsetId);
                trainsetBaseInfo.setTrainsetname(trainsetName);
                //2.1.1 如果集合为空则直接将对象添加进去
                if(!ObjectUtils.isEmpty(trainsetId)&&!ObjectUtils.isEmpty(trainsetName)){
                    if(CollectionUtils.isEmpty(trainseList)){
                        trainseList.add(trainsetBaseInfo);
                    }else{//如果集合中没有此对象，则将此对象添加到集合中
                        List<TrainsetBaseInfo> filterTrainsetList = trainseList.stream().filter(t -> trainsetId.equals(t.getTrainsetid()) && trainsetName.equals(t.getTrainsetname())).collect(Collectors.toList());
                        if(CollectionUtils.isEmpty(filterTrainsetList)){
                            trainseList.add(trainsetBaseInfo);
                        }
                    }
                }
                //2.2查询条件-单据状态集合
                String fillStateCode = summary.getFillStateCode();
                String fillStateName = summary.getFillStateName();
                FillState fillState = new FillState(fillStateCode,fillStateName);
                if(!ObjectUtils.isEmpty(fillStateCode)&&!ObjectUtils.isEmpty(fillStateName)){
                    if(CollectionUtils.isEmpty(fillStateList)){
                        fillStateList.add(fillState);
                    }else{
                        List<FillState> filterFillStateList = fillStateList.stream().filter(t -> fillStateCode.equals(t.getStateCode()) && fillStateName.equals(t.getStateName())).collect(Collectors.toList());
                        if(CollectionUtils.isEmpty(filterFillStateList)){
                            fillStateList.add(fillState);
                        }
                    }
                }
                //2.3查询条件-编组形式集合
                String marshallingType = summary.getMarshallingType();
                if(!ObjectUtils.isEmpty(marshallingType)&&!marshallingTypeList.contains(marshallingType)){
                    marshallingTypeList.add(marshallingType);
                }
                //2.4查询条件-单据类型集合
                String templateTypeCode = summary.getTemplateType();
                String templateTypeName = summary.getTemplateTypeName();
                TemplateTypeBase templateTypeBase = new TemplateTypeBase();
                templateTypeBase.setTemplateTypeCode(templateTypeCode);
                templateTypeBase.setTemplateTypeName(templateTypeName);
                if(!ObjectUtils.isEmpty(templateTypeCode)&&!ObjectUtils.isEmpty(templateTypeName)){
                    if(CollectionUtils.isEmpty(templateTypeList)){
                        templateTypeList.add(templateTypeBase);
                    }else{
                        List<TemplateTypeBase> filterTemplateList = templateTypeList.stream().filter(t -> templateTypeCode.equals(t.getTemplateTypeCode()) && templateTypeName.equals(t.getTemplateTypeName())).collect(Collectors.toList());
                        if(CollectionUtils.isEmpty(filterTemplateList)){
                            templateTypeList.add(templateTypeBase);
                        }
                    }
                }
                return summary;
            }).collect(Collectors.toList());
            //3.给返回结果赋值
            phoneOneTwoRepairCheck.setOneTwoRepairCheckList(checklistSummaryList);
            phoneOneTwoRepairCheck.setTrainsetList(trainseList);
            phoneOneTwoRepairCheck.setFillStateList(fillStateList);
            phoneOneTwoRepairCheck.setMarshallingTypeList(marshallingTypeList);
            phoneOneTwoRepairCheck.setTemplateTypeList(templateTypeList);
        }
        return phoneOneTwoRepairCheck;
    }

    /***
     * @description: 手持机一二级修记录单获取详细数据
     * @author: 冯帅
     * @date: 2021/10/27
     * @param: [queryModel]
     * @return: com.ydzbinfo.emis.trainRepair.bill.model.phoneBill.PhoneChecklistSummaryInfo
     */
    @Override
    public PhoneChecklistSummaryInfo getPhoneCheckDetailInfo(OneTwoRepairCheckList queryModel) {
        //返回结果
        PhoneChecklistSummaryInfo resultInfo = new PhoneChecklistSummaryInfo();
        //检修工长和质检员签字信息
        List<PhoneChecklistDetailInfo> signList = new ArrayList<>();
        //明细数据
        List<PhoneChecklistContentList> contentList = new ArrayList<>();
        //1.调用PC接口获取包括总表和明细的数据
        ChecklistSummaryInfoForShow detailInfoShow = checkInfoService.getDetailInfoShow(queryModel);
        if(!ObjectUtils.isEmpty(detailInfoShow)){
            //2.将从PC接口获取到的数据转换为手持机的实体数据
            //2.1获取主表数据
            ChecklistSummary checklistSummary = detailInfoShow.getExtraObject();
            if(!ObjectUtils.isEmpty(checklistSummary)){
                //手持机重写初始化规则
                if(checklistSummary.getTemplateType().equals(TemplateTypeNameEnum.RECORD_FIRST_PERSON_TEST.getValue())||
                    checklistSummary.getTemplateType().equals(TemplateTypeNameEnum.RECORD_FIRST_PERSON_KEEP.getValue())||
                    checklistSummary.getTemplateType().equals(TemplateTypeNameEnum.RECORD_SECOND_PREVENTIVE.getValue())){
                    phoneFirstPersonKeepAndTest.initContentRule(detailInfoShow);
                }
                //2.2主表数据直接赋值
                resultInfo.setExtraObject(checklistSummary);
                //2.3组织表头
                List<String> titleList = new ArrayList<>();
                //2.3.1表头第一行：日计划
                String dayplanId = checklistSummary.getDayPlanId();
                if(!ObjectUtils.isEmpty(dayplanId)){
                    // String title = "日计划："+dayplanId.replaceFirst("-", "年").replaceFirst("-", "月").replaceFirst("-", "日");
                    String title = "日计划："+dayplanId;
                    if(title.lastIndexOf("00")>0){
                        title = title.substring(0,title.length()-3)+" 白班";
                    }else{
                        title = title.substring(0,title.length()-3)+" 夜班";
                    }
                    titleList.add(title);
                }
                //2.3.2表头第二行：车组信息 编组形式
                //2.3.3根据车组id获取车组名称
                String trainsetName = "";
                TrainsetBaseInfo trainsetInfo = billCommon.getTrainsetInfo(checklistSummary.getTrainsetId());
                if(!ObjectUtils.isEmpty(trainsetInfo)){
                    trainsetName = trainsetInfo.getTrainsetname();
                }
                String title2 = trainsetName +"  "+ MarshallingTypeEnum.getLabelByKey(checklistSummary.getMarshallingType());
                titleList.add(title2);
                String title3 = checklistSummary.getItemName();
                titleList.add(title3);
                resultInfo.setTitles(titleList);
                //2.4获取属性信息集合
                TemplateAttributeQueryParamModel attributeQueryParamModel = new TemplateAttributeQueryParamModel();
                attributeQueryParamModel.setPageNum(1);
                attributeQueryParamModel.setPageSize(-1);
                List<TemplateAttributeForShow> templateAttributeList =  CacheUtil.getDataUseThreadCache(
                    "repairMidGroundService.getTemplateAttributeList",
                    () -> repairMidGroundService.getTemplateAttributeList(attributeQueryParamModel)
                );
                //2.5组织明细数据
                List<String> attrResultCodeList = new ArrayList<>();
                attrResultCodeList.add("ATTR_REPAIR_RESULT");
                attrResultCodeList.add("ATTR_NUMBERVALUE");
                if(TemplateTypeNameEnum.RECORD_FIRST_PERSON_KEEP.getValue().equals(checklistSummary.getTemplateType())){//一级修人检记录单
                    contentList = this.getNextContentList(detailInfoShow,templateAttributeList,"ATTR_REPAIR_PERSON",attrResultCodeList,"ATTR_REPAIR_POSTION","ATTR_ITEMPOINT","ATTR_CAR");
                    signList = this.getSignRes(detailInfoShow,templateAttributeList,"ATTR_FOREMAN_SIGN","ATTR_QUALITY_SIGN");
                }else if(TemplateTypeNameEnum.RECORD_FIRST_PERSON_TEST.getValue().equals(checklistSummary.getTemplateType())){//一级修人检实验单
                    contentList = this.getNextContentList(detailInfoShow,templateAttributeList,"ATTR_REPAIR_PERSON",attrResultCodeList,"ATTR_TEST_ITEM","ATTR_TEST_CONTENT","ATTR_CAR");
                    signList = this.getSignRes(detailInfoShow,templateAttributeList,"ATTR_FOREMAN_SIGN","ATTR_QUALITY_SIGN");
                }else if(TemplateTypeNameEnum.RECORD_SECOND_PREVENTIVE.getValue().equals(checklistSummary.getTemplateType())){//二级修预防性记录单
                    contentList = this.getNextContentList(detailInfoShow,templateAttributeList,"ATTR_REPAIR_PERSON",attrResultCodeList,"ATTR_ITEMPOINT","ATTR_CAR","");
                    signList = this.getSignRes(detailInfoShow,templateAttributeList,"ATTR_FOREMAN_SIGN","ATTR_QUALITY_SIGN");
                }
            }
            resultInfo.setSignList(signList);
            resultInfo.setContents(contentList);
        }else{
            return null;
        }
        return resultInfo;
    }

    /***
     * 保存记录单详细信息（只保存修改了的属性）
     */
    @Override
    public ChecklistTriggerUrlCallResult savePhoneRepairCell(PhoneChecklistSummaryInfoForSave phoneSaveInfo){
        ChecklistTriggerUrlCallResult result = new ChecklistTriggerUrlCallResult();
        try{
            if(!ObjectUtils.isEmpty(phoneSaveInfo)){
                ChecklistSummaryInfoForSave saveInfo = new ChecklistSummaryInfoForSave();
                List<ChecklistDetailInfoForSave> cells = new ArrayList<>();
                //1.将实体对象转换为ChecklistSummaryInfoForSave类型的实体对象
                saveInfo.setExtraObject(phoneSaveInfo.getExtraObject());
                List<PhoneChecklistDetailInfo> updateDetailList = phoneSaveInfo.getUpdateDetailList();
                if(!CollectionUtils.isEmpty(updateDetailList)){
                    updateDetailList.forEach(v->{
                        ChecklistDetailInfoForSave cell = new ChecklistDetailInfoForSave();
                        BeanUtils.copyProperties(v,cell);
                        if(ObjectUtils.isEmpty(v.getChangeType())){
                            cell.setChangeType(BillEntityChangeTypeEnum.NO_CHANGE);
                        }else{
                            if("2".equals(v.getChangeType().getValue())){
                                cell.setChangeType(BillEntityChangeTypeEnum.UPDATE);
                            }
                        }
                        cells.add(cell);
                    });
                }
                saveInfo.setCells(cells);
                //2.调用PC保存记录单接口
                result = checkInfoService.saveOneTwoRepairCell(saveInfo);
                System.out.println("1111111");
            }
        }catch (Exception ex){
            logger.error("一二级修记录单保存报错" + ex);
            return null;
        }
        return result;
    }

    /***
     * 检修工长签字
     */
    @Override
    public ChecklistTriggerUrlCallResult setPhoneOneTwoRepairForemanSign(PhoneChecklistSummaryInfoForSave phoneSaveInfo){
        ChecklistTriggerUrlCallResult result = new ChecklistTriggerUrlCallResult();
        //1.先判断当前登录人有没有工长签字的权限
        boolean isReadOnly = noPlanTaskCheckInfoInit.currentUserAttributeIsReadOnly(AttributeEnum.ATTR_FOREMAN_SIGN);
        if(true==isReadOnly){
            //如果没有签字权限的话就直接返回结果
            result.setOperationResultMessage("您没有签字权限");
            result.setAllowChange(false);
            return result;
        }
        try{
            if(!ObjectUtils.isEmpty(phoneSaveInfo)){
                ChecklistSummaryInfoForSave saveInfo = new ChecklistSummaryInfoForSave();
                List<ChecklistDetailInfoForSave> cells = new ArrayList<>();
                //1.将实体对象转换为ChecklistSummaryInfoForSave类型的实体对象
                saveInfo.setExtraObject(phoneSaveInfo.getExtraObject());
                List<PhoneChecklistDetailInfo> updateDetailList = phoneSaveInfo.getUpdateDetailList();
                if(!CollectionUtils.isEmpty(updateDetailList)){
                    updateDetailList.forEach(v->{
                        ChecklistDetailInfoForSave cell = new ChecklistDetailInfoForSave();
                        BeanUtils.copyProperties(v,cell);
                        if(ObjectUtils.isEmpty(v.getChangeType())){
                            cell.setChangeType(BillEntityChangeTypeEnum.NO_CHANGE);
                        }else{
                            if("2".equals(v.getChangeType().getValue())){
                                cell.setChangeType(BillEntityChangeTypeEnum.UPDATE);
                            }
                        }
                        cells.add(cell);
                    });
                }
                saveInfo.setCells(cells);
                //2.组织变更单元格数据
                List<BillCellChangeInfo<ChecklistDetailInfoForSave>> triggerCells = new ArrayList<>();
                BillCellChangeInfo<ChecklistDetailInfoForSave> billCellChangeInfo = new BillCellChangeInfo<>();
                //2.1变更前
                PhoneChecklistDetailInfo beforePhoneDetailInfo = phoneSaveInfo.getBeforePhoneDetailInfo();
                if(!ObjectUtils.isEmpty(beforePhoneDetailInfo)){
                    ChecklistDetailInfoForSave beforeChangeCellInfo = new ChecklistDetailInfoForSave();
                    BeanUtils.copyProperties(beforePhoneDetailInfo,beforeChangeCellInfo);
                    beforeChangeCellInfo.setChangeType(BillEntityChangeTypeEnum.NO_CHANGE);
                    billCellChangeInfo.setBeforeChangeCellInfo(beforeChangeCellInfo);
                }
                //2.2变更后
                PhoneChecklistDetailInfo afterPhoneDetailInfo = phoneSaveInfo.getAfterPhoneDetailInfo();
                if(!ObjectUtils.isEmpty(afterPhoneDetailInfo)){
                    ChecklistDetailInfoForSave afterChangeCellInfo = new ChecklistDetailInfoForSave();
                    BeanUtils.copyProperties(afterPhoneDetailInfo,afterChangeCellInfo);
                    afterPhoneDetailInfo.setChangeType(BillEntityChangeTypeEnum.UPDATE);
                    billCellChangeInfo.setAfterChangeCellInfo(afterChangeCellInfo);
                }
                triggerCells.add(billCellChangeInfo);
                saveInfo.setTriggerCells(triggerCells);
                //3.调用PC检修工长签字接口
                result = checkInfoService.setOneTwoRepairForemanSign(saveInfo);
                //4.获取签字的提示信息
                if(!ObjectUtils.isEmpty(result)){
                    if(ObjectUtils.isEmpty(result.getAllowChange())||result.getAllowChange()){
                        String signMsg = this.getSignMsg(beforePhoneDetailInfo,afterPhoneDetailInfo);
                        result.setOperationResultMessage(signMsg);
                    }
                }
            }
        }catch (Exception ex){
            logger.error("一二级修记录单检修工长签字报错：" + ex);
        }
        return result;
    }

    /***
     * 质检员签字
     */
    @Override
    public ChecklistTriggerUrlCallResult setPhoneOneTwoRepairQualitySign(PhoneChecklistSummaryInfoForSave phoneSaveInfo){
        ChecklistTriggerUrlCallResult result = new ChecklistTriggerUrlCallResult();
        //1.先判断当前登录人有没有工长签字的权限
        boolean isReadOnly = noPlanTaskCheckInfoInit.currentUserAttributeIsReadOnly(AttributeEnum.ATTR_QUALITY_SIGN);
        if(true==isReadOnly){
            //如果没有签字权限的话就直接返回结果
            result.setOperationResultMessage("您没有签字权限");
            result.setAllowChange(false);
            return result;
        }
        try{
            if(!ObjectUtils.isEmpty(phoneSaveInfo)){
                ChecklistSummaryInfoForSave saveInfo = new ChecklistSummaryInfoForSave();
                List<ChecklistDetailInfoForSave> cells = new ArrayList<>();
                //1.将实体对象转换为ChecklistSummaryInfoForSave类型的实体对象
                saveInfo.setExtraObject(phoneSaveInfo.getExtraObject());
                List<PhoneChecklistDetailInfo> updateDetailList = phoneSaveInfo.getUpdateDetailList();
                if(!CollectionUtils.isEmpty(updateDetailList)){
                    updateDetailList.forEach(v->{
                        ChecklistDetailInfoForSave cell = new ChecklistDetailInfoForSave();
                        BeanUtils.copyProperties(v,cell);
                        if(ObjectUtils.isEmpty(v.getChangeType())){
                            cell.setChangeType(BillEntityChangeTypeEnum.NO_CHANGE);
                        }else{
                            if("2".equals(v.getChangeType().getValue())){
                                cell.setChangeType(BillEntityChangeTypeEnum.UPDATE);
                            }
                        }
                        cells.add(cell);
                    });
                }
                saveInfo.setCells(cells);
                //2.组织变更单元格数据
                List<BillCellChangeInfo<ChecklistDetailInfoForSave>> triggerCells = new ArrayList<>();
                BillCellChangeInfo<ChecklistDetailInfoForSave> billCellChangeInfo = new BillCellChangeInfo<>();
                //2.1变更前
                PhoneChecklistDetailInfo beforePhoneDetailInfo = phoneSaveInfo.getBeforePhoneDetailInfo();
                if(!ObjectUtils.isEmpty(beforePhoneDetailInfo)){
                    ChecklistDetailInfoForSave beforeChangeCellInfo = new ChecklistDetailInfoForSave();
                    BeanUtils.copyProperties(beforePhoneDetailInfo,beforeChangeCellInfo);
                    beforeChangeCellInfo.setChangeType(BillEntityChangeTypeEnum.NO_CHANGE);
                    billCellChangeInfo.setBeforeChangeCellInfo(beforeChangeCellInfo);
                }
                //2.2变更后
                PhoneChecklistDetailInfo afterPhoneDetailInfo = phoneSaveInfo.getAfterPhoneDetailInfo();
                if(!ObjectUtils.isEmpty(afterPhoneDetailInfo)){
                    ChecklistDetailInfoForSave afterChangeCellInfo = new ChecklistDetailInfoForSave();
                    BeanUtils.copyProperties(afterPhoneDetailInfo,afterChangeCellInfo);
                    afterPhoneDetailInfo.setChangeType(BillEntityChangeTypeEnum.UPDATE);
                    billCellChangeInfo.setAfterChangeCellInfo(afterChangeCellInfo);
                }
                triggerCells.add(billCellChangeInfo);
                saveInfo.setTriggerCells(triggerCells);
                //2.调用PC质检员签字接口
                result = checkInfoService.setOneTwoRepairQualitySign(saveInfo);
                //3.获取签字的提示信息
                if(!ObjectUtils.isEmpty(result)){
                    if(ObjectUtils.isEmpty(result.getAllowChange())||result.getAllowChange()){
                        String signMsg = this.getSignMsg(beforePhoneDetailInfo,afterPhoneDetailInfo);
                        result.setOperationResultMessage(signMsg);
                    }
                }
            }
        }catch (Exception ex){
            logger.error("一二级修记录单质检员签字报错：" + ex);
        }
        return result;
    }

    /***
     * 检修工人签字
     */
    @Override
    public ChecklistTriggerUrlCallResult setPhoneOneTwoRepairPersonSign(PhoneChecklistSummaryInfoForSave phoneSaveInfo){
        ChecklistTriggerUrlCallResult result = new ChecklistTriggerUrlCallResult();
        try{
            if(!ObjectUtils.isEmpty(phoneSaveInfo)){
                ChecklistSummaryInfoForSave saveInfo = new ChecklistSummaryInfoForSave();
                List<ChecklistDetailInfoForSave> cells = new ArrayList<>();
                //1.将实体对象转换为ChecklistSummaryInfoForSave类型的实体对象
                saveInfo.setExtraObject(phoneSaveInfo.getExtraObject());
                List<PhoneChecklistDetailInfo> updateDetailList = phoneSaveInfo.getUpdateDetailList();
                //2.定义所有变更了的检修工人签字人员stringbuffer
                StringBuffer signShowCodeBuffer = new StringBuffer();
                StringBuffer signShowValueBuffer = new StringBuffer();
                if(!CollectionUtils.isEmpty(updateDetailList)){
                    //3.循环处理传过来的单元格，设置变更状态
                    updateDetailList.forEach(v->{
                        ChecklistDetailInfoForSave cell = new ChecklistDetailInfoForSave();
                        BeanUtils.copyProperties(v,cell);
                        if(ObjectUtils.isEmpty(v.getChangeType())){
                            cell.setChangeType(BillEntityChangeTypeEnum.NO_CHANGE);
                        }else{
                            if("2".equals(v.getChangeType().getValue())){
                                cell.setChangeType(BillEntityChangeTypeEnum.UPDATE);
                            }
                        }
                        cells.add(cell);
                        //4.处理签字人员往ATTR_REPAIR_PERSON_SHOW属性格子里追加检修工人签字数据
                        if("ATTR_REPAIR_PERSON".equals(v.getAttributeCode())){
                            if(StringUtils.isNotBlank(v.getCode())){
                                if(!(signShowCodeBuffer.indexOf(v.getCode())>0)){
                                    signShowCodeBuffer.append(","+v.getCode());
                                    signShowValueBuffer.append(","+v.getValue());
                                }
                            }
                        }
                    });
                }
                saveInfo.setCells(cells);
                //5.调用PC检修工人签字接口
                result = checkInfoService.saveOneTwoRepairCell(saveInfo);
                //6.处理检修工人签字展示ATTR_REPAIR_PERSON_SHOW属性相关的数据
                String signShowCode = signShowCodeBuffer.toString();
                String signShowValue = signShowValueBuffer.toString();
                if(StringUtils.isNotBlank(signShowCode)&&StringUtils.isNotBlank(signShowValue)){
                    ChecklistDetail signDetailModel = new ChecklistDetail();
                    signDetailModel.setCode(signShowCode.substring(1,signShowCode.length()));
                    signDetailModel.setValue(signShowValue.substring(1,signShowValue.length()));
                    MybatisPlusUtils.update(
                        checklistDetailMapper,
                        signDetailModel,
                        eqParam(ChecklistDetail::getChecklistSummaryId, saveInfo.getExtraObject().getChecklistSummaryId()),
                        eqParam(ChecklistDetail::getAttributeCode, "ATTR_REPAIR_PERSON_SHOW")
                    );
                }
                if(!ObjectUtils.isEmpty(result)){
                    if(ObjectUtils.isEmpty(result.getAllowChange())||result.getAllowChange()){
                        //7.根据单元格变更前和变更后的value值判断是否是撤销签字，给出提示信息
                        PhoneChecklistDetailInfo beforePhoneDetailInfo = phoneSaveInfo.getBeforePhoneDetailInfo();//变更前
                        PhoneChecklistDetailInfo afterPhoneDetailInfo = phoneSaveInfo.getAfterPhoneDetailInfo();//变更后
                        String signMsg = this.getSignMsg(beforePhoneDetailInfo,afterPhoneDetailInfo);
                        result.setOperationResultMessage(signMsg);
                    }
                }
            }
        }catch (Exception ex){
            logger.error("一二级修记录单检修工人签字报错：" + ex);
        }
        return result;
    }

    //根据单元格变更前和变更后的value值获取签字提示信息
    public String getSignMsg( PhoneChecklistDetailInfo beforePhoneDetailInfo,PhoneChecklistDetailInfo afterPhoneDetailInfo){
        String msg = "签字成功";
        if(!ObjectUtils.isEmpty(beforePhoneDetailInfo)&&!ObjectUtils.isEmpty(afterPhoneDetailInfo)){
            String beforeValue = beforePhoneDetailInfo.getValue();
            String afterValue = afterPhoneDetailInfo.getValue();
            if(StringUtils.isNotBlank(beforeValue)){//变更前的值不为空才有可能会是撤销操作
                if(StringUtils.isBlank(afterValue)){//变更前不为空，变更后为空说明是撤销操作
                    msg = "撤销签字成功";
                }else{
                    int beforeValueLength = beforeValue.length();
                    int afterValueLength = afterValue.length();
                    if(beforeValueLength>afterValueLength){//变更后的值长度比变更前的小即说明是撤销签字操作
                        msg="撤销签字成功";
                    }else{
                        msg = "签字成功";
                    }
                }
            }else{
                msg = "签字成功";
            }
        }
        return msg;
    }




    @Data
    public static class TempKey{
        String contentId;
        Set<TempKey> tempKeySet;
    }

    /***
     * 组织树形结构关联关系
     * totalDetailList-单据的所有属性集合、totalLinkContentList-单据所有的关联关系集合、attrResultCode-需要回填的单元格属性编码
     * oneAttrCode-第一层属性编码、twoAttrCode-第二层属性编码、thirdAttrCode-第三层属性编码
     */
    public Set<TempKey> getTempKeySet(List<ChecklistDetailInfoForShow> totalDetailList,List<ChkDetailLinkContent> totalLinkContentList, List<String> attrResultCodeList,String oneAttrCode,String twoAttrCode,String thirdAttrCode){
        Set<TempKey> tempKeySet = new HashSet<>();
        if(!CollectionUtils.isEmpty(totalDetailList)&&!CollectionUtils.isEmpty(totalLinkContentList)&&!CollectionUtils.isEmpty(attrResultCodeList)){
            //1.找到所有需要回填的单元格属性
            List<ChecklistDetailInfoForShow> attrResultList = totalDetailList.stream().filter(t->attrResultCodeList.contains(t.getAttributeCode())).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(attrResultList)){
                //2.循环需要回填的单元格属性
                for(ChecklistDetailInfoForShow attrResult : attrResultList){
                    //3.将他们的关联属性id拿出来
                    List<String> contentLinkIdList = attrResult.getLinkContents().stream().map(t->t.getLinkContentId()).collect(Collectors.toList());
                    if(!CollectionUtils.isEmpty(contentLinkIdList)){
                        //4.根据关联属性id拿到关联属性集合
                        List<ChecklistDetailInfoForShow> detailList = totalDetailList.stream().filter(t -> contentLinkIdList.contains(t.getId())).collect(Collectors.toList());
                        if(!CollectionUtils.isEmpty(detailList)){
                            //5.分别获取这三层属性
                            String oneId = detailList.stream().filter(t -> oneAttrCode.equals(t.getAttributeCode())).findFirst().orElse(new ChecklistDetailInfoForShow()).getId();
                            String twoId = detailList.stream().filter(t -> twoAttrCode.equals(t.getAttributeCode())).findFirst().orElse(new ChecklistDetailInfoForShow()).getId();
                            String equalTwoId = twoId;
                            List<String> twoIdList = detailList.stream().filter(t -> twoAttrCode.equals(t.getAttributeCode())).map(t -> t.getId()).collect(Collectors.toList());
                            String twoIds = "";
                            if(!CollectionUtils.isEmpty(twoIdList)){
                                twoIds = String.join(",",twoIdList);
                                equalTwoId = twoIds;
                            }
                            String thridId = detailList.stream().filter(t -> thirdAttrCode.equals(t.getAttributeCode())).findFirst().orElse(new ChecklistDetailInfoForShow()).getId();
                            if(StringUtils.isNotBlank(thridId)){
                                if(ObjectUtils.isEmpty(oneId)){
                                    logger.error("getTempKeySet方法失败：作业部位属性没有配置关联关系!");
                                    throw new RuntimeException("getTempKeySet方法失败：作业部位属性没有配置关联关系!");
                                }
                                if(ObjectUtils.isEmpty(twoId)){
                                    logger.error("getTempKeySet方法失败：检查项点属性没有配置关联关系!");
                                    throw new RuntimeException("getTempKeySet方法失败：检查项点属性没有配置关联关系!");
                                }
                                if(ObjectUtils.isEmpty(thridId)){
                                    logger.error("getTempKeySet方法失败：辆序属性没有配置关联关系!");
                                    throw new RuntimeException("getTempKeySet方法失败：辆序属性没有配置关联关系!");
                                }
                                //6.将第一层添加到树形结构中
                                TempKey oneKey = tempKeySet.stream().filter(t -> oneId.equals(t.getContentId())).findFirst().orElse(null);
                                if(ObjectUtils.isEmpty(oneKey)){
                                    oneKey = new TempKey();
                                    oneKey.setContentId(oneId);
                                    tempKeySet.add(oneKey);
                                }
                                //7.将第二层添加到第一层中
                                Set<TempKey> twoKeySet = oneKey.getTempKeySet();
                                if(CollectionUtils.isEmpty(twoKeySet)){
                                    twoKeySet = new HashSet<>();
                                }
                                String finalEqualTwoId = equalTwoId;
                                TempKey twoKey = twoKeySet.stream().filter(t -> finalEqualTwoId.equals(t.getContentId())).findFirst().orElse(null);
                                if(ObjectUtils.isEmpty(twoKey)){
                                    twoKey = new TempKey();
                                    if(twoIdList.size()>1){
                                        twoKey.setContentId(finalEqualTwoId);
                                    }else{
                                        twoKey.setContentId(twoId);
                                    }
                                    twoKeySet.add(twoKey);
                                }
                                oneKey.setTempKeySet(twoKeySet);
                                //8.将第三层添加到第二层中
                                Set<TempKey> thirdKeySet = twoKey.getTempKeySet();
                                if(CollectionUtils.isEmpty(thirdKeySet)){
                                    thirdKeySet = new HashSet<>();
                                }
                                TempKey thirdKey = thirdKeySet.stream().filter(t->thridId.equals(t.getContentId())).findFirst().orElse(null);
                                if(ObjectUtils.isEmpty(thirdKey)){
                                    thirdKey = new TempKey();
                                    thirdKey.setContentId(thridId);
                                    thirdKeySet.add(thirdKey);
                                }
                                twoKey.setTempKeySet(thirdKeySet);
                                //9.第四层：将需要回填的格子添加到第三层中
                                Set<TempKey> resultKeySet = thirdKey.getTempKeySet();
                                if(CollectionUtils.isEmpty(resultKeySet)){
                                    resultKeySet = new HashSet<>();
                                }
                                TempKey resultKey = resultKeySet.stream().filter(t->attrResult.getId().equals(t.getContentId())).findFirst().orElse(null);
                                if(ObjectUtils.isEmpty(resultKey)){
                                    resultKey = new TempKey();
                                    resultKey.setContentId(attrResult.getId());
                                    resultKeySet.add(resultKey);
                                }
                                thirdKey.setTempKeySet(resultKeySet);
                            }else{//二级修预防性记录单
                                if(ObjectUtils.isEmpty(oneId)){
                                    logger.error("getTempKeySet方法失败：检查项点属性没有配置关联关系!");
                                    throw new RuntimeException("getTempKeySet方法失败：检查项点属性没有配置关联关系!");
                                }
                                if(ObjectUtils.isEmpty(twoId)){
                                    logger.error("getTempKeySet方法失败：辆序属性没有配置关联关系!");
                                    throw new RuntimeException("getTempKeySet方法失败：辆序属性没有配置关联关系!");
                                }
                                //6.将第一层添加到树形结构中
                                TempKey oneKey = tempKeySet.stream().filter(t -> oneId.equals(t.getContentId())).findFirst().orElse(null);
                                if(ObjectUtils.isEmpty(oneKey)){
                                    oneKey = new TempKey();
                                    oneKey.setContentId(oneId);
                                    tempKeySet.add(oneKey);
                                }
                                //7.将第二层添加到第一层中
                                Set<TempKey> twoKeySet = oneKey.getTempKeySet();
                                if(CollectionUtils.isEmpty(twoKeySet)){
                                    twoKeySet = new HashSet<>();
                                }
                                TempKey twoKey = twoKeySet.stream().filter(t -> twoId.equals(t.getContentId())).findFirst().orElse(null);
                                if(ObjectUtils.isEmpty(twoKey)){
                                    twoKey = new TempKey();
                                    twoKey.setContentId(twoId);
                                    twoKeySet.add(twoKey);
                                }
                                oneKey.setTempKeySet(twoKeySet);

                                //9.第三层：将需要回填的格子添加到第二层中
                                Set<TempKey> resultKeySet = twoKey.getTempKeySet();
                                if(CollectionUtils.isEmpty(resultKeySet)){
                                    resultKeySet = new HashSet<>();
                                }
                                TempKey resultKey = resultKeySet.stream().filter(t->attrResult.getId().equals(t.getContentId())).findFirst().orElse(null);
                                if(ObjectUtils.isEmpty(resultKey)){
                                    resultKey = new TempKey();
                                    resultKey.setContentId(attrResult.getId());
                                    resultKeySet.add(resultKey);
                                }
                                twoKey.setTempKeySet(resultKeySet);
                            }
                        }
                    }
                }
            }
        }
        return tempKeySet;
    }

    /***
     * 获取下一层的明细数据
     * detailInfoShow-所有属性信息集合、repairPerson-检修工人签字属性编码、attrResultCode-需要回填的单元格属性编码
     * oneAttrCode-第一层属性编码、twoAttrCode-第二层属性编码、thirdAttrCode-第三层属性编码
     */
    public List<PhoneChecklistContentList> getNextContentList(ChecklistSummaryInfoForShow detailInfoShow,List<TemplateAttributeForShow> templateAttributeList,String repairPerson,List<String> attrResultCodeList,String oneAttrCode,String twoAttrCode,String thirdCode){
        List<PhoneChecklistContentList> resultList = new ArrayList<>();
        try{
            //获取主表数据
            ChecklistSummary summary = detailInfoShow.getExtraObject();
            //获取检修工长签字内容是否为空，如果不为空的话则将所有需要回填的格子设置为只读
            String foremanSignCode = summary.getForemanSignCode();
            boolean isReadOnly = false;
            if(StringUtils.isNotBlank(foremanSignCode)){
                isReadOnly = true;
            }
            //获取质检签字内容
            String qualitySignCode = summary.getQualitySignCode();
            //1.定义关联属性临时树形结构
            Set<TempKey> tempKeySet = new HashSet<>();
            //2.获取所有的单元格属性信息
            List<ChecklistDetailInfoForShow> totalDetailList = detailInfoShow.getCells();
            if(!CollectionUtils.isEmpty(totalDetailList)){
                //获取所有检修员签字属性集合
                List<ChecklistDetailInfoForShow> repairSignList = totalDetailList.stream().filter(t->repairPerson.equals(t.getAttributeCode())).collect(Collectors.toList());
                //3.获取所有关联内容集合
                List<ChkDetailLinkContent> totalLinkContentList = new ArrayList<>();
                totalDetailList.stream().map(source->{
                    if(!CollectionUtils.isEmpty(source.getLinkContents())){
                        totalLinkContentList.addAll(source.getLinkContents());
                    }
                    return source.getLinkContents();
                }).collect(Collectors.toList());
                //4.获取树形关联关系
                tempKeySet = this.getTempKeySet(totalDetailList,totalLinkContentList,attrResultCodeList,oneAttrCode,twoAttrCode,thirdCode);
                //5.组织返回对象
                boolean finalIsReadOnly = isReadOnly;//回填单元格是否只读
                tempKeySet.forEach(oneKeyItem->{
                    //第一层返回对象
                    PhoneChecklistContentList oneItem = new PhoneChecklistContentList();
                    oneItem.setAllFill(true);
                    //5.1从属性集合中获取该属性
                    ChecklistDetailInfoForShow oneDetail = totalDetailList.stream().filter(t -> oneKeyItem.getContentId().equals(t.getId())).findFirst().orElse(null);
                    if(!ObjectUtils.isEmpty(oneDetail)){
                        //5.2组织回填描述
                        PhoneChecklistDetailInfo oneTitleInfo = new PhoneChecklistDetailInfo();
                        BeanUtils.copyProperties(oneDetail,oneTitleInfo);
                        //5.3设置回填描述的属性
                        TemplateAttributeForSave oneAttributeSave = this.getAttributeSave(templateAttributeList,oneDetail.getAttributeCode());
                        if(!ObjectUtils.isEmpty(oneAttributeSave)){
                            oneTitleInfo.setValue(oneAttributeSave.getAttributeName()+"："+oneTitleInfo.getValue());
                        }
                        oneTitleInfo.setAttribute(oneAttributeSave);
                        //5.4第一层对象设置回填描述对象
                        oneItem.setTitleInfo(oneTitleInfo);
                        //5.5将检修工人签字挂在第一层里面
                        List<PhoneChecklistDetailInfo> repairSignDetailList = this.getRepairSignList(oneKeyItem, repairSignList, totalLinkContentList, totalDetailList, templateAttributeList, repairPerson,finalIsReadOnly);
                        oneItem.setSignList(repairSignDetailList);
                        /**
                        //5.5如果第三层的code为空，则将检修工人签字挂在第一层里面
                        if(ObjectUtils.isEmpty(thirdCode)){
                            List<PhoneChecklistDetailInfo> repairSignDetailList = this.getRepairSignList(oneKeyItem, repairSignList, totalLinkContentList, totalDetailList, templateAttributeList, repairPerson);
                            oneItem.setSignList(repairSignDetailList);
                        }**/
                    }
                    //6.组织第二层
                    Set<TempKey> twoKeySet = oneKeyItem.getTempKeySet();
                    if(!CollectionUtils.isEmpty(twoKeySet)){
                        //6.1第二层返回对象集合
                        List<PhoneChecklistContentList> twoContentList = new ArrayList<>();
                        twoKeySet.forEach(twoKeyItem->{
                            //6.2第二层返回对象
                            PhoneChecklistContentList twoItem = new PhoneChecklistContentList();
                            //6.3从属性集合中获取该属性
                            //6.3.1处理第二层检查项点的特殊情况
                            List<ChecklistDetailInfoForShow> twoDetailList = new ArrayList<>();
                            if(!ObjectUtils.isEmpty(twoKeyItem)&&!ObjectUtils.isEmpty(twoKeyItem.getContentId())){
                                String[] twoIds = twoKeyItem.getContentId().split(",");
                                if(!ObjectUtils.isEmpty(twoIds)){
                                    List<String> twoIdList = Arrays.asList(twoIds);
                                    if(!CollectionUtils.isEmpty(twoIdList)){
                                        for(String twoId:twoIdList){
                                            ChecklistDetailInfoForShow detailInfoForShow = totalDetailList.stream().filter(t -> twoId.equals(t.getId())).findFirst().orElse(null);
                                            if(!ObjectUtils.isEmpty(detailInfoForShow)){
                                                twoDetailList.add(detailInfoForShow);
                                            }
                                        }
                                    }
                                }
                            }
                            if(!CollectionUtils.isEmpty(twoDetailList)){
                                // twoDetailList = twoDetailList.stream().sorted(Comparator.comparing(ChecklistDetailInfoForShow::getColId)).collect(Collectors.toList());
                                twoDetailList = twoDetailList.stream().sorted(Comparator.comparing((ChecklistDetailInfoForShow t)->Integer.valueOf(t.getRowId()))).collect(Collectors.toList());
                            }
                            ChecklistDetailInfoForShow twoDetail = null;
                            if(twoDetailList.size()>1){
                                twoDetail = twoDetailList.get(1);
                                twoDetail.setValue(twoDetailList.get(0).getValue()+twoDetail.getValue());
                            }else{
                                twoDetail = totalDetailList.stream().filter(t -> twoKeyItem.getContentId().equals(t.getId())).findFirst().orElse(null);
                            }
                            if(!ObjectUtils.isEmpty(twoDetail)){
                                //6.4第二层回填描述
                                PhoneChecklistDetailInfo twoTitleInfo = new PhoneChecklistDetailInfo();
                                BeanUtils.copyProperties(twoDetail,twoTitleInfo);
                                //6.5设置回填描述属性
                                TemplateAttributeForSave twoAttributeSave = this.getAttributeSave(templateAttributeList,twoDetail.getAttributeCode());
                                if(!ObjectUtils.isEmpty(twoAttributeSave)){
                                    if(StringUtils.isNotBlank(thirdCode)){
                                        twoTitleInfo.setValue(twoAttributeSave.getAttributeName()+"："+twoTitleInfo.getValue());
                                    }else{
                                        twoTitleInfo.setValue(twoAttributeSave.getAttributeName()+twoTitleInfo.getValue()+"：");
                                    }
                                }
                                twoTitleInfo.setAttribute(twoAttributeSave);
                                //6.6设置第二层回填描述
                                twoItem.setTitleInfo(twoTitleInfo);

                                /**一级修记录单/试验单、二级修预防性记录单的检修人员签字都挂在第一层下，暂时将这里注释掉
                                //6.7第二层回填内容（检修员签字）
                                //6.7.1获取签字属性
                                //5.5如果第三层的code不为空，则将检修工人签字挂在第二层里面
                                List<PhoneChecklistDetailInfo> repairSignDetailList = this.getRepairSignList(twoKeyItem, repairSignList, totalLinkContentList, totalDetailList, templateAttributeList, repairPerson);
                                twoItem.setSignList(repairSignDetailList);
                                **/
                                //6.8如果thirdCode为空，则将回填结果属性直接挂到第二层下边
                                if(ObjectUtils.isEmpty(thirdCode)){
                                    Set<TempKey> resultKeySet = twoKeyItem.getTempKeySet();
                                    List<PhoneChecklistDetailInfo> detailInfoList = this.getDetailInfoList(resultKeySet, totalDetailList, templateAttributeList,finalIsReadOnly);
                                    twoItem.setDetailInfoList(detailInfoList);
                                    //6.9判断作业部位下所有需要回填的单元格是否都回填完毕
                                    boolean allFill = oneItem.isAllFill();
                                    if(!ObjectUtils.isEmpty(allFill)&&allFill){
                                        boolean b = Optional.ofNullable(detailInfoList).orElseGet(ArrayList::new).stream().anyMatch(t -> StringUtils.isBlank(t.getValue()));
                                        oneItem.setAllFill(!b);
                                    }
                                }
                            }
                            //7.thirdCode不为空的情况下组织第三层
                            Set<TempKey> thirdKeySet = twoKeyItem.getTempKeySet();
                            if(!ObjectUtils.isEmpty(thirdCode)&&!CollectionUtils.isEmpty(thirdKeySet)){
                                //7.1第三层返回集合对象
                                List<PhoneChecklistContentList> thirdContentList = new ArrayList<>();
                                thirdKeySet.forEach(thirdKeyItem->{
                                    //7.2第三层返回对象
                                    PhoneChecklistContentList thirdItem = new PhoneChecklistContentList();
                                    //7.3从属性集合中获取该属性
                                    ChecklistDetailInfoForShow thirdDetail = totalDetailList.stream().filter(t -> thirdKeyItem.getContentId().equals(t.getId())).findFirst().orElse(null);
                                    if(!ObjectUtils.isEmpty(thirdDetail)){
                                        //7.4第三层回填描述
                                        PhoneChecklistDetailInfo thirdTitleInfo = new PhoneChecklistDetailInfo();
                                        BeanUtils.copyProperties(thirdDetail,thirdTitleInfo);
                                        //7.5设置第三层回填描述属性
                                        TemplateAttributeForSave thirdAttributeSave = this.getAttributeSave(templateAttributeList,thirdDetail.getAttributeCode());
                                        if(!ObjectUtils.isEmpty(thirdAttributeSave)){
                                            thirdTitleInfo.setValue(thirdAttributeSave.getAttributeName()+thirdTitleInfo.getValue()+"：");
                                        }
                                        thirdTitleInfo.setAttribute(thirdAttributeSave);
                                        //7.6设置第三层回填描述
                                        thirdItem.setTitleInfo(thirdTitleInfo);
                                        //8 组织第三层回填内容
                                        Set<TempKey> resultKeySet = thirdKeyItem.getTempKeySet();
                                        if(!CollectionUtils.isEmpty(resultKeySet)){
                                            //8.1回填内容集合
                                            List<PhoneChecklistDetailInfo> detailInfoList = this.getDetailInfoList(resultKeySet,totalDetailList,templateAttributeList, finalIsReadOnly);
                                            thirdItem.setDetailInfoList(detailInfoList);
                                            //6.9判断作业部位下所有需要回填的单元格是否都回填完毕
                                            boolean allFill = oneItem.isAllFill();
                                            if(!ObjectUtils.isEmpty(allFill)&&allFill){
                                                boolean b = Optional.ofNullable(detailInfoList).orElseGet(ArrayList::new).stream().anyMatch(t -> StringUtils.isBlank(t.getValue()));
                                                oneItem.setAllFill(!b);
                                            }
                                            /** 提炼出来一个方法：getDetialInfoList，所以这里暂时注释掉
                                            resultKeySet.forEach(resultKeyItem->{
                                                //8.2从属性集合中获取该属性
                                                ChecklistDetailInfoForShow resultDetail = totalDetailList.stream().filter(t -> resultKeyItem.getContentId().equals(t.getId())).findFirst().orElse(null);
                                                if(!ObjectUtils.isEmpty(resultDetail)){
                                                    PhoneChecklistDetailInfo detailInfo = new PhoneChecklistDetailInfo();
                                                    BeanUtils.copyProperties(resultDetail,detailInfo);
                                                    //8.3设置回填内容属性
                                                    TemplateAttributeForSave resultAttributeSave = this.getAttributeSave(templateAttributeList,resultDetail.getAttributeCode());
                                                    detailInfo.setAttribute(resultAttributeSave);
                                                    //8.4将回填内容添加到回填内容集合中
                                                    detailInfoList.add(detailInfo);
                                                }
                                            }); */
                                        }
                                    }
                                    //7.7将第三层对象添加到第三层集合中
                                    thirdContentList.add(thirdItem);
                                });
                                //7.8第二层对象设置第三层对象集合
                                List<PhoneChecklistContentList> sortThirdContentList = thirdContentList.stream().sorted((content1,content2)->{
                                    int res = 0;
                                    PhoneChecklistDetailInfo titleInfo1 = content1.getTitleInfo();
                                    PhoneChecklistDetailInfo titleInfo2 = content2.getTitleInfo();
                                    if(!ObjectUtils.isEmpty(titleInfo1)&&!ObjectUtils.isEmpty(titleInfo2)){
                                        String value1 = titleInfo1.getValue();
                                        String value2 = titleInfo2.getValue();
                                        if(!ObjectUtils.isEmpty(value1)&&!ObjectUtils.isEmpty(value2)){
                                            String subStr1 = value1.substring(2,4);
                                            String subStr2 = value2.substring(2,4);
                                            if(!ObjectUtils.isEmpty(subStr1)&&!ObjectUtils.isEmpty(subStr2)){
                                                int i1 = 0;
                                                int i2 = 0;
                                                try{
                                                    i1 = Integer.parseInt(subStr1);
                                                    i2 = Integer.parseInt(subStr2);
                                                }catch (Exception ex){
                                                    logger.error("手持机获取记录单详细信息失败：属性关联关系错误，关联不到辆序！");
                                                    throw new RuntimeException(ex);
                                                }
                                                if(i1<i2){
                                                    res = -1;
                                                }else if(i1==i2){
                                                    res = 0;
                                                }else{
                                                    res = 1;
                                                }
                                            }
                                        }
                                    }
                                    return res;
                                }).collect(Collectors.toList());
                                PhoneChecklistContentList carZeroContent = Optional.ofNullable(sortThirdContentList).orElseGet(ArrayList::new).stream().filter(t -> !ObjectUtils.isEmpty(t.getTitleInfo()) && "辆序00：".equals(t.getTitleInfo().getValue())).findFirst().orElse(null);
                                if(!ObjectUtils.isEmpty(carZeroContent)){
                                    sortThirdContentList.remove(carZeroContent);
                                    sortThirdContentList.add(carZeroContent);
                                }
                                twoItem.setContentLists(sortThirdContentList);
                            }
                            //6.7添加到第二层对象集合中
                            twoContentList.add(twoItem);
                        });
                        //第一层对象设置第二层对象集合twoContentList
                        List<PhoneChecklistContentList> twoContentSortList = twoContentList;
                        //6.thirdCode为空的情况下把第二层的辆序排序
                        if(StringUtils.isBlank(thirdCode)){
                            twoContentSortList = twoContentList.stream().sorted((content1,content2)->{
                                int res = 0;
                                PhoneChecklistDetailInfo titleInfo1 = content1.getTitleInfo();
                                PhoneChecklistDetailInfo titleInfo2 = content2.getTitleInfo();
                                if(!ObjectUtils.isEmpty(titleInfo1)&&!ObjectUtils.isEmpty(titleInfo2)){
                                    String value1 = titleInfo1.getValue();
                                    String value2 = titleInfo2.getValue();
                                    if(!ObjectUtils.isEmpty(value1)&&!ObjectUtils.isEmpty(value2)){
                                        String subStr1 = value1.substring(2,4);
                                        String subStr2 = value2.substring(2,4);
                                        if(!ObjectUtils.isEmpty(subStr1)&&!ObjectUtils.isEmpty(subStr2)){
                                            int i1 = 0;
                                            int i2 = 0;
                                            try{
                                                i1 = Integer.parseInt(subStr1);
                                                i2 = Integer.parseInt(subStr2);
                                            }catch (Exception ex){
                                                logger.error("手持机获取记录单详细信息失败：属性关联关系错误，关联不到辆序！");
                                                throw new RuntimeException(ex);
                                            }
                                            if(i1<i2){
                                                res = -1;
                                            }else if(i1==i2){
                                                res = 0;
                                            }else{
                                                res = 1;
                                            }
                                        }
                                    }
                                }
                                return res;
                            }).collect(Collectors.toList());
                            PhoneChecklistContentList carZeroContent = Optional.ofNullable(twoContentSortList).orElseGet(ArrayList::new).stream().filter(t -> !ObjectUtils.isEmpty(t.getTitleInfo()) && "辆序00：".equals(t.getTitleInfo().getValue())).findFirst().orElse(null);
                            if(!ObjectUtils.isEmpty(carZeroContent)){
                                twoContentSortList.remove(carZeroContent);
                                twoContentSortList.add(carZeroContent);
                            }
                        }else{
                            // twoContentSortList = twoContentList.stream().sorted(Comparator.comparing((PhoneChecklistContentList v)->v.getTitleInfo().getRowId())).collect(Collectors.toList());
                            twoContentSortList = twoContentList.stream().sorted(Comparator.comparing((PhoneChecklistContentList v)->Integer.valueOf(v.getTitleInfo().getRowId()))).collect(Collectors.toList());
                        }
                        oneItem.setContentLists(twoContentSortList);
                    }
                    //设置显示回填默认值按钮（检修工长或者质检员任意一人签字之后，回填默认值按钮设置为只读）
                    oneItem.setShowDefaultButton(true);
                    if(StringUtils.isNotBlank(foremanSignCode)||StringUtils.isNotBlank(qualitySignCode)){
                        oneItem.setDefaultButtonReadOnly(true);
                    }else{
                        oneItem.setDefaultButtonReadOnly(false);
                    }
                    //5.5第一层对象添加到第一层集合中
                    resultList.add(oneItem);
                });
            }
        }catch (Exception ex){
            logger.error("手持机一级修人检记录单组织明细数据报错" + ex);
            return null;
        }
        List<PhoneChecklistContentList> resultSortedList = resultList;
        resultSortedList = resultList.stream().sorted(Comparator.comparing((PhoneChecklistContentList v)->Integer.valueOf(v.getTitleInfo().getRowId()))).collect(Collectors.toList());
        return resultSortedList;
    }


    /***
     * 根据keyset集合组织回填内容集合
     * tempKeySet：本属性层级，totalDetailList：所有detail信息集合，templateAttributeList：所有attribute信息集合
     */
    public List<PhoneChecklistDetailInfo> getDetailInfoList(Set<TempKey> tempKeySet, List<ChecklistDetailInfoForShow> totalDetailList, List<TemplateAttributeForShow> templateAttributeList, boolean isReadOnly){
        List<PhoneChecklistDetailInfo> detailInfoList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(tempKeySet)){
            tempKeySet.forEach(resultKeyItem->{
                //8.2从属性集合中获取该属性
                ChecklistDetailInfoForShow resultDetail = totalDetailList.stream().filter(t -> resultKeyItem.getContentId().equals(t.getId())).findFirst().orElse(null);
                if(!ObjectUtils.isEmpty(resultDetail)){
                    PhoneChecklistDetailInfo detailInfo = new PhoneChecklistDetailInfo();
                    BeanUtils.copyProperties(resultDetail,detailInfo);
                    //8.3设置回填内容属性
                    TemplateAttributeForSave resultAttributeSave = this.getAttributeSave(templateAttributeList,resultDetail.getAttributeCode());
                    if(!ObjectUtils.isEmpty(resultAttributeSave)){
                        if("N/A".equals(resultDetail.getValue())){
                            resultAttributeSave.setReadOnly("1");
                        }
                        //设置只读
                        if(isReadOnly){
                            resultAttributeSave.setReadOnly("1");
                        }
                    }
                    detailInfo.setAttribute(resultAttributeSave);
                    //8.4将回填内容添加到回填内容集合中
                    detailInfoList.add(detailInfo);
                }
            });
        }
        return detailInfoList;
    }

    /***
     * 组织检修工人签字集合
     * currentTempKey：本属性键对象，totalDetailList：所有detail信息集合，templateAttributeList：所有attribute信息集合,finalIsReadOnly:检修工人签字按钮是否只读
     */
    public List<PhoneChecklistDetailInfo> getRepairSignList(TempKey currentTempKey,List<ChecklistDetailInfoForShow> repairSignList,List<ChkDetailLinkContent> totalLinkContentList,
                                                            List<ChecklistDetailInfoForShow> totalDetailList,List<TemplateAttributeForShow> templateAttributeList,String repairPerson,boolean isReadOnly){
        List<PhoneChecklistDetailInfo> resultList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(repairSignList)){
            //1.获取签字属性的id集合
            List<String> signIdList = repairSignList.stream().map(t->t.getId()).collect(Collectors.toList());
            /** 暂时注释掉
            ChkDetailLinkContent signLink = totalLinkContentList.stream().filter(t ->!ObjectUtils.isEmpty(signIdList)&&signIdList.contains(t.getContentId())&&t.getLinkContentId().equals(currentTempKey.getContentId())).findFirst().orElse(null);
            if(!ObjectUtils.isEmpty(signLink)){
                //回填内容集合
                ChecklistDetailInfoForShow twoSign = totalDetailList.stream().filter(t->t.getId().equals(signLink.getContentId())).findFirst().orElse(null);
                if(!ObjectUtils.isEmpty(twoSign)){
                    List<PhoneChecklistDetailInfo> signList = new ArrayList<>();
                    PhoneChecklistDetailInfo signinfo = new PhoneChecklistDetailInfo();
                    BeanUtils.copyProperties(twoSign,signinfo);
                    //设置签字内容的属性
                    TemplateAttributeForSave repairSignAttributeSave = this.getAttributeSave(templateAttributeList,repairPerson);
                    signinfo.setAttribute(repairSignAttributeSave);
                    //将签字内容添加到内容集合中
                    signList.add(signinfo);
                    //设置签字内容集合
                    resultList.add(signinfo);
                }
            }**/
            //2.根据属性的id,找出来和它关联的"检修工人签字"属性的关联关系对象集合
            List<ChkDetailLinkContent> signLinkList = totalLinkContentList.stream().filter(t ->!ObjectUtils.isEmpty(signIdList)&&signIdList.contains(t.getContentId())&&t.getLinkContentId().equals(currentTempKey.getContentId())).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(signLinkList)){
                //3.根据检修工人签字关联关系对象集合获取属性的详细信息
                List<String> signContentIdList = signLinkList.stream().map(t -> t.getContentId()).collect(Collectors.toList());
                // List<ChecklistDetailInfoForShow> twoSignList = totalDetailList.stream().filter(t -> signContentIdList.contains(t.getId())).sorted(Comparator.comparing(ChecklistDetailInfoForShow::getRowId)).collect(Collectors.toList());
                List<ChecklistDetailInfoForShow> twoSignList = totalDetailList.stream().filter(t -> signContentIdList.contains(t.getId())).sorted(Comparator.comparing((ChecklistDetailInfoForShow t)->Integer.valueOf(t.getRowId()))).collect(Collectors.toList());
                if(!CollectionUtils.isEmpty(twoSignList)){
                    int signNum = twoSignList.size();
                    boolean isMultiple = signNum>1?true:false;
                    for(int i = 0;i<twoSignList.size();i++){
                        ChecklistDetailInfoForShow twoSign = twoSignList.get(i);
                        PhoneChecklistDetailInfo signinfo = new PhoneChecklistDetailInfo();
                        BeanUtils.copyProperties(twoSign,signinfo);
                        //4.设置签字内容的属性
                        TemplateAttributeForSave repairSignAttributeSave = this.getAttributeSave(templateAttributeList,repairPerson);
                        //5.设置签字的前置文本
                        //5.1获取该签字属性上侧单元格的横纵坐标
                        Integer rowIdInt = Integer.parseInt(twoSign.getRowId())-1;
                        String rowIdStr = rowIdInt.toString();
                        String colIdStr = twoSign.getColId();
                        //5.2根据横纵坐标和签字文本的属性编码，获取到前置文本属性
                        ChecklistDetailInfoForShow signTagDetail = totalDetailList.stream().filter(t -> t.getRowId().equals(rowIdStr) && t.getColId().equals(colIdStr) && t.getAttributeCode().equals("ATTR_SIGN_TAG_NUMBER")).findFirst().orElse(null);
                        if(!ObjectUtils.isEmpty(signTagDetail)){
                            String attributeName = signTagDetail.getValue();
                            if(attributeName.lastIndexOf(":")>0||attributeName.lastIndexOf("：")>0){
                                attributeName = attributeName.substring(0,attributeName.length()-1);
                            }
                            repairSignAttributeSave.setAttributeName(attributeName);
                        }else{
                            repairSignAttributeSave.setAttributeName("检修人员签字");
                        }
                        if(isReadOnly){
                            repairSignAttributeSave.setReadOnly("1");
                        }
                        signinfo.setAttribute(repairSignAttributeSave);
                        //设置签字内容集合
                        resultList.add(signinfo);
                    }
                }
            }
        }
        List<PhoneChecklistDetailInfo> resultOrderedList = resultList.stream().sorted(Comparator.comparing((PhoneChecklistDetailInfo t)->Integer.valueOf(t.getRowId()))).collect(Collectors.toList());
        return resultOrderedList;
    }


    /***
     * 根据属性编码获取属性对象
     */
    public TemplateAttributeForSave getAttributeSave(List<TemplateAttributeForShow> templateAttributeList,String attributeCode){
        TemplateAttributeForSave res = new TemplateAttributeForSave();
        if(!CollectionUtils.isEmpty(templateAttributeList)){
            TemplateAttributeForShow resultAttributeShow = templateAttributeList.stream().filter(t -> attributeCode.equals(t.getAttributeCode())).findFirst().orElse(null);
            BeanUtils.copyProperties(resultAttributeShow,res);
            if(!ObjectUtils.isEmpty(resultAttributeShow)){
                String templateValues = resultAttributeShow.getTemplateValues();
                if(StringUtils.isNotBlank(templateValues)){
                    String[] splitStrs = templateValues.split(",");
                    if(!ObjectUtils.isEmpty(splitStrs)){
                        List<TemplateValue> templateValueList = new ArrayList<>();
                        for(String str:splitStrs){
                            TemplateValue templateValue = new TemplateValue();
                            templateValue.setAttributeRangeValue(str);
                            templateValueList.add(templateValue);
                        }
                        res.setTemplateValueList(templateValueList);
                    }
                }
            }
        }
        return res;
    }

    /***
     * 组织检修工长签字和质检员签字
     */
    public List<PhoneChecklistDetailInfo> getSignRes(ChecklistSummaryInfoForShow detailInfoShow,List<TemplateAttributeForShow> templateAttributeList,String foreManSign,String qualitySign){
        List<PhoneChecklistDetailInfo> resList = new ArrayList<>();
        List<ChecklistDetailInfoForShow> totalDetailList = detailInfoShow.getCells();
        if(!CollectionUtils.isEmpty(totalDetailList)){
            //1.获取检修工长签字属性信息
            ChecklistDetailInfoForShow foreManDetail = totalDetailList.stream().filter(t->foreManSign.equals(t.getAttributeCode())).findFirst().orElse(null);
            if(!ObjectUtils.isEmpty(foreManDetail)){
                PhoneChecklistDetailInfo foreManItem = new PhoneChecklistDetailInfo();
                BeanUtils.copyProperties(foreManDetail,foreManItem);
                //1.2设置属性信息
                TemplateAttributeForSave attribute = this.getAttributeSave(templateAttributeList,foreManDetail.getAttributeCode());
                if(!ObjectUtils.isEmpty(attribute)){
                    foreManItem.setAttribute(attribute);
                }

                resList.add(foreManItem);
            }
            //2.获取质检员签字属性信息
            ChecklistDetailInfoForShow qualityDetail = totalDetailList.stream().filter(t->qualitySign.equals(t.getAttributeCode())).findFirst().orElse(null);
            if(!ObjectUtils.isEmpty(qualityDetail)){
                PhoneChecklistDetailInfo qualityItem = new PhoneChecklistDetailInfo();
                BeanUtils.copyProperties(qualityDetail,qualityItem);
                //2.2设置属性信息
                TemplateAttributeForSave attribute = this.getAttributeSave(templateAttributeList,qualityDetail.getAttributeCode());
                if(!ObjectUtils.isEmpty(attribute)){
                    qualityItem.setAttribute(attribute);
                }
                resList.add(qualityItem);
            }
        }
        return resList;
    }
}
