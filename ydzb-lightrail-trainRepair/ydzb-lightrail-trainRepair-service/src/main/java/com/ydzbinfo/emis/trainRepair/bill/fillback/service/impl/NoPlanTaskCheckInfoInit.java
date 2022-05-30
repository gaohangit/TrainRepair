package com.ydzbinfo.emis.trainRepair.bill.fillback.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import com.ydzbinfo.emis.trainRepair.bill.fillback.service.IChecklistDetailService;
import com.ydzbinfo.emis.trainRepair.bill.fillback.utils.AttributeEnum;
import com.ydzbinfo.emis.trainRepair.bill.fillback.utils.BillCommon;
import com.ydzbinfo.emis.trainRepair.bill.general.UrlInfo;
import com.ydzbinfo.emis.trainRepair.bill.general.constant.BillCellTriggerTimingEnum;
import com.ydzbinfo.emis.trainRepair.bill.general.constant.BillCellTriggerUrlTypeEnum;
import com.ydzbinfo.emis.trainRepair.bill.model.attribute.TemplateAttributeForShow;
import com.ydzbinfo.emis.trainRepair.bill.model.attribute.TemplateAttributeQueryParamModel;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.ChecklistDetailInfoForShow;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.SummaryInfoForShow;
import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.trainRepair.common.service.IRepairMidGroundService;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetBaseInfo;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.runRouting.LeaveBackTrainNoResult;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.track.ZtTrackAreaEntity;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.track.ZtTrackEntity;
import com.ydzbinfo.emis.utils.DateUtils;
import com.ydzbinfo.emis.utils.StringUtils;
import com.ydzbinfo.emis.utils.UserUtil;
import com.ydzbinfo.hussar.core.util.DateUtil;
import com.ydzbinfo.hussar.system.permit.SysRoles;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description:
 * @date: 2021/12/17
 * @author: 冯帅
 */
@Component
public class NoPlanTaskCheckInfoInit {

    @Autowired
    BillCommon billCommon;

    @Resource
    IRemoteService remoteService;

    @Autowired
    IChecklistDetailService checklistDetailService;

    @Autowired
    IRepairMidGroundService repairMidGroundService;

    /**
     * 初始化数据
     */
    public List<ChecklistDetailInfoForShow> initContentData(SummaryInfoForShow content,String templateTypeCode) {
        ShiroUser currentUser = ShiroKit.getUser();
        //初始化数据
        List<ChecklistDetailInfoForShow> contentList = content.getCells();
        JSONObject extraObject = JSONObject.parseObject(JSONObject.toJSONString(content.getExtraObject()));
        //获取所编码
        String unitCode = extraObject.getString("unitCode");
        // JSONObject extraObject = (JSONObject) content.getExtraObject();
        if(!ObjectUtils.isEmpty(extraObject)){
            //获取所有车组
            List<TrainsetBaseInfo> trainsetList = new ArrayList<>();
            if(StringUtils.isNotBlank(unitCode)){
                trainsetList = remoteService.getTrainsetNameListRepair(unitCode);
            }
            for (ChecklistDetailInfoForShow detail : contentList) {
                if (detail.getAttributeCode().equals(AttributeEnum.ATTR_ALL_DEPT.getValue())) {//通用—段所全称
                    String depot = UserUtil.getUserInfo().getDepot().getName();
                    detail.setValue(depot + extraObject.getString("unitName"));
                } else if (detail.getAttributeCode().equals(AttributeEnum.ATTR_TRAINSETID.getValue())) {//通用-车组id
                    TrainsetBaseInfo info = billCommon.getTrainsetInfo(extraObject.getString("trainsetId"));
                    detail.setValue(info.getTrainsetname());
                } else if (detail.getAttributeCode().equals(AttributeEnum.ATTR_REPAIR_DEPT.getValue())) {//通用—检修班组
                    //如果没有保存过，就初始化为当前班组
                    if(ObjectUtils.isEmpty(detail.getValue())){
                        detail.setValue(extraObject.getString("deptName"));
                    }
                } else if(detail.getAttributeCode().equals(AttributeEnum.ATTR_APPLY_APPLYPERSON.getValue())){//一体化申请单-申请人
                    //如果没有保存过，就初始化为当前用户
                    if(ObjectUtils.isEmpty(detail.getValue())){
                        detail.setCode(currentUser.getStaffId());
                        detail.setValue(currentUser.getName());
                    }
                } else if(detail.getAttributeCode().equals(AttributeEnum.ATTR_APPLY_TRAINSETID.getValue())){//一体化申请单-一体化作业申请车组
                    List<String> trainsetNameList = Optional.ofNullable(trainsetList).orElseGet(ArrayList::new).stream().filter(t -> StringUtils.isNotBlank(t.getTrainsetname())).map(t -> t.getTrainsetname()).collect(Collectors.toList());
                    detail.setExtraOptions(trainsetNameList);
                } else if(detail.getAttributeCode().equals(AttributeEnum.ATTR_APPLY_BATTERY.getValue())){//一体化申请单-蓄电池
                    detail.setNeedInitializeByAttributeDefaultValue(true);
                } else if(detail.getAttributeCode().equals(AttributeEnum.ATTR_APPLY_CATENARY.getValue())){//一体化申请单-接触网
                    detail.setNeedInitializeByAttributeDefaultValue(true);
                } else if(detail.getAttributeCode().equals(AttributeEnum.ATTR_APPLY_WORKAREA.getValue())){//一体化申请单-作业地点
                    detail.setNeedInitializeByAttributeDefaultValue(true);
                } else if(detail.getAttributeCode().equals(AttributeEnum.ATTR_LEAVE_TRAINSETID.getValue())){//出所联检单—出所联检车组
                    List<String> trainsetNameList = Optional.ofNullable(trainsetList).orElseGet(ArrayList::new).stream().filter(t -> StringUtils.isNotBlank(t.getTrainsetname())).map(t -> t.getTrainsetname()).collect(Collectors.toList());
                    detail.setExtraOptions(trainsetNameList);
                    //如果出所联检单的车组号保存过，再次打开的时候就不允许修改了
                    if(StringUtils.isNotBlank(detail.getValue())){
                        detail.setReadOnly(true);
                    }
                } else if(detail.getAttributeCode().equals(AttributeEnum.ATTR_LEAVE_CONNECT_TIME.getValue())){//出所联检单—交接完成时间
                    //如果没有保存过，就初始化为当前时间
                    if(ObjectUtils.isEmpty(detail.getValue())){
                        detail.setValue(DateUtils.dateToFormatStr(new Date(),"yyyy-MM-dd HH:mm"));
                    }
                    //设置交接完成时间只读状态
                    boolean readOnly = this.currentUserAttributeIsReadOnly(AttributeEnum.ATTR_LEAVE_QUALITY_SIGN);
                    detail.setReadOnly(readOnly);
                } else if(detail.getAttributeCode().equals(AttributeEnum.ATTR_LEAVE_QUALITY_STATE.getValue())){//出所联检单—质量状态
                    // List<String> qualityStateList = new ArrayList<>();
                    // qualityStateList.add("/");
                    // qualityStateList.add("正常");
                    // qualityStateList.add("异常");
                    // detail.setExtraOptions(qualityStateList);
                } else if(detail.getAttributeCode().equals(AttributeEnum.ATTR_LEAVE_CONNECT_TRACK.getValue())){//出所联检单—交接股道
                    if(StringUtils.isNotBlank(unitCode)){
                        List<ZtTrackAreaEntity> trackAreaByDept = remoteService.getTrackAreaByDept(unitCode);
                        List<ZtTrackEntity> ztTrackEntityList = Optional.ofNullable(trackAreaByDept).orElseGet(ArrayList::new).stream().flatMap(t -> t.getLstTrackInfo().stream()).collect(Collectors.toList());
                        List<String> trackNameList = Optional.ofNullable(ztTrackEntityList).orElseGet(ArrayList::new).stream().map(t -> t.getTrackName()).sorted().collect(Collectors.toList());
                        detail.setExtraOptions(trackNameList);
                    }
                    //如果出所联检单的交接股道保存过，再次打开的时候就不允许修改了
                    if(StringUtils.isNotBlank(detail.getValue())){
                        detail.setReadOnly(true);
                    }
                }else if (detail.getAttributeCode().equals(AttributeEnum.ATTR_IN_OUT_TRAINNO.getValue())) {//出所联检单—入/出所车次
                    //出入所信息  入所车次/出所车次
                    List<ChecklistDetailInfoForShow> resultTrainsetId = contentList.stream().filter(t -> t.getAttributeCode().equals(AttributeEnum.ATTR_LEAVE_TRAINSETID.getValue())).collect(Collectors.toList());
                    if (resultTrainsetId.size() > 0) {
                        String trainsetId = resultTrainsetId.get(0).getValue();
                        String querydate = DateUtil.formatDate(new Date(), "yyyyMMdd");
                        LeaveBackTrainNoResult leaveBackTrainNoResult = billCommon.getTrainsetLeaveBack(trainsetId, querydate);
                        if (leaveBackTrainNoResult != null) {
                            String leaveBack = "";
                            if(!ObjectUtils.isEmpty(leaveBackTrainNoResult.getBackTrainNo())||!ObjectUtils.isEmpty(leaveBackTrainNoResult.getDepTrainNo())){
                                leaveBack = leaveBackTrainNoResult.getBackTrainNo() + "/" + leaveBackTrainNoResult.getDepTrainNo();
                            }
                            detail.setValue(leaveBack);
                        }
                    }
                }else if(detail.getAttributeCode().equals(AttributeEnum.ATTR_LEAVE_QUALITY_SIGN.getValue())){//出所联检单—车辆质检员签字
                    //如果出所联检单—车辆质检员签字已经有值，则交接完成时间不能修改
                    if(StringUtils.isNotBlank(detail.getValue())){
                        ChecklistDetailInfoForShow connectTrackShow = contentList.stream().filter(t -> t.getAttributeCode().equals(AttributeEnum.ATTR_LEAVE_CONNECT_TIME.getValue())).findFirst().orElse(null);
                        if(!ObjectUtils.isEmpty(connectTrackShow)){
                            connectTrackShow.setReadOnly(true);
                        }
                    }
                }
            }
        }
        return contentList;
    }

    /**
     * 初始化规则
     */
    public List<ChecklistDetailInfoForShow> initContentRule(SummaryInfoForShow content) {
        List<ChecklistDetailInfoForShow> contentList = content.getCells();
        for (ChecklistDetailInfoForShow detail : contentList) {
            if (detail.getAttributeCode().equals(AttributeEnum.ATTR_LEAVE_FOREMAN_SIGN.getValue())||   //检修工（组）长签字
                detail.getAttributeCode().equals(AttributeEnum.ATTR_LEAVE_QUALITY_SIGN.getValue())||   //车辆质检员签字
                detail.getAttributeCode().equals(AttributeEnum.ATTR_LEAVE_DRIVER_SIGN.getValue())||   //动车组司机
                detail.getAttributeCode().equals(AttributeEnum.ATTR_LEAVE_ATP_SIGN.getValue())||   //ATP检修人员签字
                detail.getAttributeCode().equals(AttributeEnum.ATTR_LEAVE_CIR_SIGN.getValue())||   //CIR检修人员签字
                detail.getAttributeCode().equals(AttributeEnum.ATTR_LEAVE_EOAS_SING.getValue())||   //EOAS检修人员签字
                detail.getAttributeCode().equals(AttributeEnum.ATTR_LEAVE_DIRTYFOREMAN_SIGN.getValue())||   //吸污工班长（负责人）
                detail.getAttributeCode().equals(AttributeEnum.ATTR_LEAVE_WASHFOREMAN_SIGN.getValue())||   //外皮清洗工班长（负责人）
                detail.getAttributeCode().equals(AttributeEnum.ATTR_LEAVE_PASSENGERQUALITY_SIGN.getValue())||   //吸污工班长（负责人）
                detail.getAttributeCode().equals(AttributeEnum.ATTR_LEAVE_POWER_PERSON.getValue())){    //供电检修人员签字
                UrlInfo triggerUrlInfo = new UrlInfo();
                triggerUrlInfo.setUrlType(BillCellTriggerUrlTypeEnum.FUNCTION);
                triggerUrlInfo.setUrl(billCommon.addApplicationPrefix("/noPlanTaskChecklist/noPlanTaskRepairSign"));//签字
                detail.setTriggerUrlInfo(triggerUrlInfo);
                detail.setTriggerTiming(BillCellTriggerTimingEnum.AFTER_CHANGE);
            }else if(detail.getAttributeCode().equals(AttributeEnum.ATTR_DISPATCH_SIGN.getValue()) || //调度签字
                detail.getAttributeCode().equals(AttributeEnum.ATTR_TECHNIQUE_SIGN.getValue()) || //技术签字
                detail.getAttributeCode().equals(AttributeEnum.ATTR_DIRECTOR_SIGN.getValue()) || //所长签字
                detail.getAttributeCode().equals(AttributeEnum.ATTR_FINISH_DISPATCH_SIGN.getValue()) || //调度销记签字
                detail.getAttributeCode().equals(AttributeEnum.ATTR_FINISH_WORKPERSON_SIGN.getValue())){ //作业负责人签字
                UrlInfo triggerUrlInfo = new UrlInfo();
                triggerUrlInfo.setUrlType(BillCellTriggerUrlTypeEnum.FUNCTION);
                triggerUrlInfo.setUrl(billCommon.addApplicationPrefix("/noPlanTaskChecklist/noPlanTaskRepairSign"));//签字
                detail.setTriggerUrlInfo(triggerUrlInfo);
                detail.setTriggerTiming(BillCellTriggerTimingEnum.BEFORE_CHANGE);
            }else if (detail.getAttributeCode().equals(AttributeEnum.ATTR_LEAVE_TRAINSETID.getValue())){//出所联检车组
                UrlInfo triggerUrlInfo = new UrlInfo();
                triggerUrlInfo.setUrlType(BillCellTriggerUrlTypeEnum.FUNCTION);
                triggerUrlInfo.setUrl(billCommon.addApplicationPrefix("/liveChecklist/changeTrainnoByTrainset"));//车组号改变
                detail.setTriggerUrlInfo(triggerUrlInfo);
                detail.setTriggerTiming(BillCellTriggerTimingEnum.AFTER_CHANGE);
            }
        }
        return contentList;
    }

    /***
     * @author: 冯帅
     * @date: 2021/12/28
     * @desc: 获取当前登录人对指定属性单元格是否只读
     */
    public boolean currentUserAttributeIsReadOnly(AttributeEnum attributeEnum){
        boolean res = true;
        //1.获取当前登录用户
        ShiroUser currentUser = ShiroKit.getUser();
        //2.获取当前登录人的角色
        if(!ObjectUtils.isEmpty(currentUser)){
            List<SysRoles> sysRoleList = currentUser.getSysRoleList();
            if(!CollectionUtils.isEmpty(sysRoleList)){
                //3.获取当前登录人角色id集合
                List<String> currentUserRoleIdList = sysRoleList.stream().map(t->t.getRoleId()).collect(Collectors.toList());
                //4.根据属性编码获取属性信息
                TemplateAttributeQueryParamModel queryModel = new TemplateAttributeQueryParamModel();
                queryModel.setPageNum(1);
                queryModel.setPageSize(-1);
                queryModel.setAttributeCode(attributeEnum.getValue());
                List<TemplateAttributeForShow> templateAttributeList = repairMidGroundService.getTemplateAttributeList(queryModel);
                TemplateAttributeForShow templateAttributeForShow = Optional.ofNullable(templateAttributeList).orElseGet(ArrayList::new).stream().findFirst().orElse(null);
                //5.获取该属性的取值范围并转换为list集合
                if(!ObjectUtils.isEmpty(templateAttributeForShow)){
                    String templateValues = templateAttributeForShow.getTemplateValues();
                    if(StringUtils.isNotBlank(templateValues)){
                        List<String> attributeRoleIds = Arrays.asList(templateValues.split(","));
                        currentUserRoleIdList.retainAll(attributeRoleIds);
                        if(!CollectionUtils.isEmpty(currentUserRoleIdList)){
                            res = false;
                        }
                    }
                }
            }
        }
        return res;
    }

}
