package com.ydzbinfo.emis.trainRepair.bill.fillback.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.trainRepair.bill.fillback.dao.ChecklistLiveCheckMapper;
import com.ydzbinfo.emis.trainRepair.bill.fillback.service.*;
import com.ydzbinfo.emis.trainRepair.bill.fillback.utils.AttributeEnum;
import com.ydzbinfo.emis.trainRepair.bill.fillback.utils.BillCommon;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.ChecklistDetailInfoForSave;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.ChecklistDetailInfoForShow;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.ChecklistTriggerUrlCallResult;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.LiveCheck.LiveCheckQueryCondition;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.LiveCheck.LiveCheckSummary;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.SummaryInfoForSave;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChecklistLiveCheck;
import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetBaseInfo;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.runRouting.LeaveBackTrainNoResult;
import com.ydzbinfo.emis.utils.StringUtils;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import com.ydzbinfo.emis.utils.mybatisplus.param.ColumnParam;
import com.ydzbinfo.hussar.core.util.DateUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.betweenParam;
import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.eqParam;

/**
 * <p>
 * 出所联检单总表 服务实现类
 * </p>
 *
 * @author 韩旭
 * @since 2021-11-23
 */
@Service
public class ChecklistLiveCheckServiceImpl extends ServiceImpl<ChecklistLiveCheckMapper, ChecklistLiveCheck> implements IChecklistLiveCheckService {

    @Autowired
    IChecklistIntegrationService checklistIntegrationService;

    @Autowired
    BillCommon billCommon;

    //回填区域表服务
    @Autowired
    IChecklistAreaService checklistAreaService;

    //回填内容表服务
    @Autowired
    IChecklistDetailService checklistDetailService;

    //回填内容关联内容表服务
    @Autowired
    IChkDetailLinkContentService chkDetailLinkContentService;

    //回填内容卡控表服务
    @Autowired
    IChecklistLinkControlService checklistLinkControlService;

    @Autowired
    IRemoteService remoteService;


    @Override
    public Page<LiveCheckSummary> getLiveCheckSummaryList(LiveCheckQueryCondition queryCheckListSummary) {
        Page<LiveCheckSummary> resPage = new Page<>();
        if (!ObjectUtils.isEmpty(queryCheckListSummary)) {
            //1.组织查询条件
            List<ColumnParam<ChecklistLiveCheck>> columnParamList = new ArrayList<>();
            Date beginDate = queryCheckListSummary.getCreateBeginTime();
            Date endDate = queryCheckListSummary.getCreateEndTime();
            if(!ObjectUtils.isEmpty(beginDate)&&!ObjectUtils.isEmpty(endDate)){
                columnParamList.add(betweenParam(ChecklistLiveCheck::getConnectTime, beginDate, endDate));
            }
            if(!ObjectUtils.isEmpty(queryCheckListSummary.getTrainsetId())){
                columnParamList.add(eqParam(ChecklistLiveCheck::getTrainsetId,queryCheckListSummary.getTrainsetId()));
            }
            if(!ObjectUtils.isEmpty(queryCheckListSummary.getTrack())){
                columnParamList.add(eqParam(ChecklistLiveCheck::getTrack,queryCheckListSummary.getTrack()));
            }
            if(!ObjectUtils.isEmpty(queryCheckListSummary.getUnitCode())){
                columnParamList.add(eqParam(ChecklistLiveCheck::getUnitCode,queryCheckListSummary.getUnitCode()));
            }
            Page<ChecklistLiveCheck> checklistLiveCheckPage = MybatisPlusUtils.selectPage(
                this,
                queryCheckListSummary.getPageNum(),
                queryCheckListSummary.getPageSize(),
                columnParamList
            );
            //从履历中获取所有车组的信息
            List<TrainsetBaseInfo> trainsetList = remoteService.getTrainsetList();
            if (!ObjectUtils.isEmpty(checklistLiveCheckPage) && !CollectionUtils.isEmpty(checklistLiveCheckPage.getRecords())) {
                List<LiveCheckSummary> resList = new ArrayList<>();
                checklistLiveCheckPage.getRecords().forEach(checklistLiveCheck -> {
                    LiveCheckSummary resItem = new LiveCheckSummary();
                    BeanUtils.copyProperties(checklistLiveCheck, resItem);
                    resItem.setTrainsetId(checklistLiveCheck.getTrainsetId());
                    TrainsetBaseInfo trainsetBaseInfo = Optional.ofNullable(trainsetList).orElseGet(ArrayList::new).stream().filter(t -> t.getTrainsetid().equals(checklistLiveCheck.getTrainsetId())).findFirst().orElse(null);
                    if (!ObjectUtils.isEmpty(trainsetBaseInfo)) {
                        resItem.setTrainsetName(trainsetBaseInfo.getTrainsetname());
                    }
                    resList.add(resItem);
                });
                BeanUtils.copyProperties(checklistLiveCheckPage, resPage);
                resPage.setRecords(resList);
            }
        }
        return resPage;
    }
    //变更车组后获取出入所信息
    public ChecklistTriggerUrlCallResult changeTrainnoByTrainset(SummaryInfoForSave summaryInfoForSave) {
        try {
            ChecklistTriggerUrlCallResult result = new ChecklistTriggerUrlCallResult();
            result.setExtraObject(summaryInfoForSave.getExtraObject());
            List<ChecklistDetailInfoForSave> contentList = summaryInfoForSave.getCells();
            List<ChecklistDetailInfoForShow> changedCells = new ArrayList<>();
            //找到车组号
            List<ChecklistDetailInfoForSave> saveInfos = contentList.stream().filter((ChecklistDetailInfoForSave t) -> t.getAttributeCode().equals(AttributeEnum.ATTR_LEAVE_TRAINSETID.getValue())).
                collect(Collectors.toList());
            if (saveInfos.size() > 0 && StringUtils.hasText(saveInfos.get(0).getValue())) {
                //根据车组名称获取ID
                String trainsetName = saveInfos.get(0).getValue();
                String querydate = DateUtil.formatDate(new Date(), "yyyyMMdd");
                //根据车组号获取出入所车次
                LeaveBackTrainNoResult leaveBackTrainNoResult = billCommon.getTrainsetLeaveBack(trainsetName, querydate);
                if (leaveBackTrainNoResult != null) {
                    String leaveBack = "";
                    if(!ObjectUtils.isEmpty(leaveBackTrainNoResult.getBackTrainNo())||!ObjectUtils.isEmpty(leaveBackTrainNoResult.getDepTrainNo())){
                        leaveBack = leaveBackTrainNoResult.getBackTrainNo() + "/" + leaveBackTrainNoResult.getDepTrainNo();
                    }
                    //找到出入所车次属性
                    List<ChecklistDetailInfoForSave> saveInfos1 = contentList.stream().filter((ChecklistDetailInfoForSave t) -> t.getAttributeCode().equals(AttributeEnum.ATTR_IN_OUT_TRAINNO.getValue())).
                        collect(Collectors.toList());
                    for (ChecklistDetailInfoForSave info : saveInfos1) {
                        ChecklistDetailInfoForShow infoForShow = new ChecklistDetailInfoForShow();
                        BeanUtils.copyProperties(info, infoForShow);
                        infoForShow.setValue(leaveBack);
                        changedCells.add(infoForShow);
                    }
                }
                result.setAllowChange(true);
                result.setChangedCells(changedCells);
                return result;
            }
        } catch (Exception exc) {
            throw new RuntimeException("变更车组后获取出入所信息失败!");
        }
        return null;
    }
}
