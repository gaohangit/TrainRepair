package com.ydzbinfo.emis.trainRepair.bill.fillback.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.trainRepair.bill.fillback.dao.ChecklistIntegrationMapper;
import com.ydzbinfo.emis.trainRepair.bill.fillback.service.*;
import com.ydzbinfo.emis.trainRepair.bill.fillback.utils.BillCommon;
import com.ydzbinfo.emis.trainRepair.bill.fillback.utils.TemplateTypeNameEnum;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.Integration.IntegrationSummary;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.Integration.InterationQueryCondition;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChecklistIntegration;
import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetBaseInfo;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import com.ydzbinfo.emis.utils.mybatisplus.param.ColumnParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.*;

/**
 * <p>
 * 一体化作业申请单总表 服务实现类
 * </p>
 *
 * @author 韩旭
 * @since 2021-11-23
 */
@Service
public class ChecklistIntegrationServiceImpl extends ServiceImpl<ChecklistIntegrationMapper, ChecklistIntegration> implements IChecklistIntegrationService {

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

   @Resource
   IRemoteService remoteService;

   public Page<IntegrationSummary> getChecklistSummaryList(InterationQueryCondition queryCheckListSummary)
   {
      //从数据库中直接获取主表数据
      Page<IntegrationSummary> resPage = new Page<>();
      //1.组织查询条件
      List<ColumnParam<ChecklistIntegration>> columnParamList = new ArrayList<>();
      ChecklistIntegration eqModel = new ChecklistIntegration();
      if(!ObjectUtils.isEmpty(queryCheckListSummary.getStuffId())){
         columnParamList.add(eqParam(ChecklistIntegration::getStuffId, queryCheckListSummary.getStuffId()));
      }
      if(!ObjectUtils.isEmpty(queryCheckListSummary.getDeptCode())){
         columnParamList.add(eqParam(ChecklistIntegration::getDeptCode, queryCheckListSummary.getDeptCode()));
      }
      if(!ObjectUtils.isEmpty(queryCheckListSummary.getStuffName())){
         columnParamList.add(likeIgnoreCaseParam(ChecklistIntegration::getStuffName, queryCheckListSummary.getStuffName()));
      }
      if(!ObjectUtils.isEmpty(queryCheckListSummary.getDeptName())){
         columnParamList.add(likeIgnoreCaseParam(ChecklistIntegration::getDeptName, queryCheckListSummary.getDeptName()));
      }
      if(!ObjectUtils.isEmpty(queryCheckListSummary.getUnitCode())){
         columnParamList.add(eqParam(ChecklistIntegration::getUnitCode, queryCheckListSummary.getUnitCode()));
      }
      if(!ObjectUtils.isEmpty(queryCheckListSummary.getTrainsetId())){
         columnParamList.add(eqParam(ChecklistIntegration::getTrainsetId,queryCheckListSummary.getTrainsetId()));
      }
      if(!ObjectUtils.isEmpty(queryCheckListSummary.getApplyBeginTime())&&!ObjectUtils.isEmpty(queryCheckListSummary.getApplyEndTime())){
         columnParamList.add(betweenParam(ChecklistIntegration::getApplyBeginTime,queryCheckListSummary.getApplyBeginTime(),queryCheckListSummary.getApplyEndTime()));
         columnParamList.add(betweenParam(ChecklistIntegration::getApplyEndTime,queryCheckListSummary.getApplyBeginTime(),queryCheckListSummary.getApplyEndTime()));
      }
      String state = queryCheckListSummary.getState();
      if(!ObjectUtils.isEmpty(state)){
         switch (state){
            case "1":
               columnParamList.add(neParam(ChecklistIntegration::getDispatchSign,null));
               break;
            case "2":
               columnParamList.add(neParam(ChecklistIntegration::getTechniqueSign,null));
               break;
            case "3":
               columnParamList.add(neParam(ChecklistIntegration::getDirectorSign,null));
               break;
            case "4":
               columnParamList.add(neParam(ChecklistIntegration::getFinishWorkPersonSign,null));
               break;
            case "5":
               columnParamList.add(neParam(ChecklistIntegration::getFinishDispatchSign,null));
               break;
         }
      }

      // EntityWrapper<ChecklistIntegration> ew = new EntityWrapper<>(eqModel);
      // if(!ObjectUtils.isEmpty(queryCheckListSummary.getApplyBeginTime())&&!ObjectUtils.isEmpty(queryCheckListSummary.getApplyEndTime())){
      //    columnParamList.add(betweenParam(ChecklistIntegration::getApplyBeginTime,queryCheckListSummary.getApplyBeginTime(),queryCheckListSummary.getApplyEndTime()));
      // }
      //3.查询数据库获取数据
      Page<ChecklistIntegration> checklistIntegrationPage = MybatisPlusUtils.selectPage(
          this,
          queryCheckListSummary.getPageNum(),
          queryCheckListSummary.getPageSize(),
          columnParamList
      );
      //从履历中获取所有车组的信息
      List<TrainsetBaseInfo> trainsetList = remoteService.getTrainsetList();
      if(!ObjectUtils.isEmpty(checklistIntegrationPage)){
         List<IntegrationSummary> resList= Optional.ofNullable(checklistIntegrationPage.getRecords()).orElseGet(ArrayList::new).stream().map(integration->{
            IntegrationSummary integrationSummary = new IntegrationSummary();
            BeanUtils.copyProperties(integration,integrationSummary);
            TrainsetBaseInfo trainsetBaseInfo = Optional.ofNullable(trainsetList).orElseGet(ArrayList::new).stream().filter(t -> t.getTrainsetid().equals(integration.getTrainsetId())).findFirst().orElse(null);
            if(!ObjectUtils.isEmpty(trainsetBaseInfo)){
               integrationSummary.setTrainsetName(trainsetBaseInfo.getTrainsetname());
            }
            TemplateTypeNameEnum templateTypeNameEnum = TemplateTypeNameEnum.valueOf(integration.getTemplateType());
            if(!ObjectUtils.isEmpty(templateTypeNameEnum)){
               integrationSummary.setTemplateTypeName(templateTypeNameEnum.getLabel());
            }
            return integrationSummary;
         }).collect(Collectors.toList());
         if(!CollectionUtils.isEmpty(resList)){
            BeanUtils.copyProperties(checklistIntegrationPage,resPage);
            resPage.setRecords(resList);
         }
      }
      return resPage;
   }
}
