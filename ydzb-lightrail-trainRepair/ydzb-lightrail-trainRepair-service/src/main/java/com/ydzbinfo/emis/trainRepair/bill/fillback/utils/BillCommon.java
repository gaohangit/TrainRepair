package com.ydzbinfo.emis.trainRepair.bill.fillback.utils;

import com.ydzbinfo.emis.trainRepair.bill.fillback.dao.ChecklistDetailMapper;
import com.ydzbinfo.emis.trainRepair.bill.general.constant.BillEntityChangeTypeEnum;
import com.ydzbinfo.emis.trainRepair.bill.model.TemplateQueryParamModel;
import com.ydzbinfo.emis.trainRepair.bill.model.attribute.TemplateTypeInfo;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.ChecklistDetailInfoForSave;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.ChecklistDetailInfoForShow;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.ChkDetailLinkContentForModule;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateAttribute;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateAttributeTypeInfo;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateType;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChecklistDetail;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChecklistLinkControl;
import com.ydzbinfo.emis.trainRepair.bill.model.templatesummary.TemplateSummaryInfo;
import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.trainRepair.common.service.IRepairMidGroundService;
import com.ydzbinfo.emis.trainRepair.remotemodel.item.ItemCycleInfo;
import com.ydzbinfo.emis.trainRepair.remotemodel.item.RepairItemInfo;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.AxleWheel;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.AxleWheelDiameterEntity;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetBaseInfo;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetInfo;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.runRouting.LeaveBackTrainNoResult;
import com.ydzbinfo.emis.utils.CacheUtil;
import com.ydzbinfo.emis.utils.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class BillCommon {
    protected static final Logger logger = getLogger(BillCommon.class);
    @Autowired
    private IRemoteService iRemoteService;
    @Autowired
    private IRepairMidGroundService midGroundService;


    @Value("${spring.application.name}")
    private String applicationName;

    public String addApplicationPrefix(String url){
        return "/" + applicationName + url;
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    ChecklistDetailMapper checklistDetailMapper;

    @Transactional(propagation = Propagation.REQUIRED)
    //返回数据是否存在变更
    public boolean saveCellDetail(List<ChecklistDetailInfoForSave> cells, String checklistSummaryID) {
        boolean isChanged = false;
        try {
            //组装数据
            //   ChecklistSummary summary = saveInfo.getExtraObject();
            //  List<ChecklistDetailInfoForSave> cells = saveInfo.getCells();
            for (ChecklistDetailInfoForSave info : cells) {
                ChecklistDetail detail = new ChecklistDetail();
                BeanUtils.copyProperties(info, detail);
                List<ChkDetailLinkContentForModule> linkCells = info.getLinkCells();
                List<ChecklistLinkControl> controls = info.getControls();
                //更新DETAIL表
                if (BillEntityChangeTypeEnum.INSERT.equals(info.getChangeType())) {
                    if(ObjectUtils.isEmpty(info.getId())){
                        String uuidStr = UUID.randomUUID().toString();
                        detail.setId(uuidStr);
                    }
                    detail.setChecklistSummaryId(checklistSummaryID);
                    checklistDetailMapper.insert(detail);
                    isChanged = true;
                } else if (BillEntityChangeTypeEnum.DELETE.equals(info.getChangeType())) {
                    checklistDetailMapper.deleteByPrimaryKey(detail);
                    isChanged = true;
                } else if (BillEntityChangeTypeEnum.UPDATE.equals(info.getChangeType())) {
                    checklistDetailMapper.updatePrimaryKey(detail);
                    isChanged = true;
                }
            }
        }
        catch (Exception exc) {
            logger.error("/BillCommon/saveCellDetail----保存接口出错...", exc);
            throw exc;
        }
        return isChanged ;
    }


    /**
     * 获取项目的维修周期
     * @param itemCycles 维修周期集合  月份(0101-1231):循环定周期:绝对时间(540【-54, +54】天)/里程(60.0【-6.0, +6.0】万公里)
     * @return
     */
    public String getItemCycle(List<ItemCycleInfo> itemCycles){
        String itemCycle = "";
        try{
            if(itemCycles !=null && itemCycles.size() > 0){
                List<String> lstItemCycs = new ArrayList<String>();
                for(ItemCycleInfo cyc : itemCycles){
                    if(StringUtils.isEmpty(cyc.getCycCateId()))
                        continue;
                    switch (cyc.getCycCateId()){
                        case "A":   //绝对时间
                        case "C":   //运行时间
                            if(lstItemCycs.size() >0){
                                lstItemCycs.add(0, String.format("%s(%s,-%s)天", cyc.getCycValue(), cyc.getCycPlusValue(), cyc.getCycMinusValue()));
                            }else {
                                lstItemCycs.add(String.format("%s(%s,-%s)天", cyc.getCycValue(), cyc.getCycPlusValue(), cyc.getCycMinusValue()));
                            }
                            break;
                        case "B":   //里程
                            lstItemCycs.add(String.format("%s(%s,-%s)万公里", cyc.getCycValue(), cyc.getCycPlusValue(), cyc.getCycMinusValue()));
                            break;
                        case "D":   //无
                            break;
                    }
                }
                itemCycle = String.join("/", lstItemCycs);
            }
        }catch (Exception ex){
            logger.error("BillCommon.getItemCycle根据维修周期集合拼接维修周期时引发异常",ex);
            itemCycle = "";
        }
        return itemCycle;
    }
    /**
     * 获取走行公里
     *
     * @return
     */
    public int getAccumile(String trainsetId, String dayPlanId) {
        try {
            return CacheUtil.getDataUseThreadCache("BillCommon.getAccumile_" + trainsetId + "_" + dayPlanId, () -> {
                String querydate = dayPlanId.substring(0, 4) +
                    dayPlanId.substring(5, 7) + dayPlanId.substring(8, 10);
                int accumile = iRemoteService.getTrainsetAccMile(trainsetId, querydate);
                return accumile;
            });
        } catch (Exception ex) {
            logger.error("获取走行公里", ex);
            return 0;
        }
    }
    //根据类型CODE和属性条件获取数据
    public List<TemplateAttribute> getTemplateAttributeTypeInfos(String templateTypeCode, String attributeCode, String value)
    {

        try {
            return CacheUtil.getDataUseThreadCache("BillCommon.getTemplateAttributeTypeInfos" + templateTypeCode + "_" + attributeCode, () -> {
                 List<TemplateAttributeTypeInfo> attributeTypeInfos = midGroundService.getConfigAttr(templateTypeCode);
                 List<TemplateAttribute> attributes = new ArrayList<>();
                List<TemplateAttribute> attrs = new ArrayList<>();
                for (TemplateAttributeTypeInfo info : attributeTypeInfos) {
                    if("backFillVerify".equals(attributeCode))
                    {
                        if(!StringUtils.isEmpty(value)) {
                            attrs = info.getTemplateAttributes().stream().filter((TemplateAttribute b) -> b.getBackFillVerify().equals(value)).collect(Collectors.toList());
                        }else{
                            attrs = info.getTemplateAttributes();
                        }
                    }else  if ("attributeModeCode".equals(attributeCode))
                    {
                        if(!StringUtils.isEmpty(value)){
                            attrs =  info.getTemplateAttributes().stream().filter((TemplateAttribute b) -> b.getAttributeModeCode().equals(value)).collect(Collectors.toList());
                        }else {
                            attrs = info.getTemplateAttributes();
                        }
                    }
                    if(attrs.size()> 0){
                        for(TemplateAttribute attr : attrs){
                            List<TemplateAttribute> ts = attributes.stream().filter((TemplateAttribute b) -> b.getAttributeModeCode().equals(attr.getAttributeCode())).collect(Collectors.toList());
                            if(ts.size() == 0){
                                attributes.add(attr);
                            }
                        }
                    }
                }
                return attributes;
            });
        } catch (Exception ex) {
            logger.error("根据类型CODE和属性条件获取数据失败!", ex);
            return null;
        }
    }


    public LeaveBackTrainNoResult getTrainsetLeaveBack(String trainsetId, String date) {
        try {
            LeaveBackTrainNoResult trainNoList = iRemoteService.getLeavBackTrainNo(trainsetId, date);
            return trainNoList;
        } catch (Exception ex) {
            logger.error("获取出入所车组错误", ex);
        }
        return new LeaveBackTrainNoResult();
    }


    //更新记录单字表内容保存状态
    public  void UpdateSavedState(List<ChecklistDetailInfoForShow> showChangedCells, List<ChecklistDetailInfoForSave> cells)
    {
        for (ChecklistDetailInfoForSave info : cells) {
            boolean isChanged = false;
            if (info.getChangeType().equals(BillEntityChangeTypeEnum.INSERT)) {
                isChanged = true;
            } else if (info.getChangeType().equals(BillEntityChangeTypeEnum.DELETE)) {
                isChanged = true;
            } else if (info.getChangeType().equals(BillEntityChangeTypeEnum.UPDATE)) {
                isChanged = true;
            }
            if (isChanged) {
                List<ChecklistDetailInfoForShow> filterShowChangedCells = showChangedCells.stream().filter(t -> t.getId().equals(info.getId())).collect(Collectors.toList());
                if(filterShowChangedCells.size()>0)
                {
                    for (ChecklistDetailInfoForShow show : filterShowChangedCells)
                    {
                        show.setSaved(true);
                    }
                }else
                {
                    ChecklistDetailInfoForShow infoForShow = new ChecklistDetailInfoForShow();
                    BeanUtils.copyProperties(info, infoForShow);
                    infoForShow.setSaved(true);
                    showChangedCells.add(infoForShow);
                }
            }
        }
    }


    public String getTrainsetNameById(String trainsetId)
    {
        TrainsetBaseInfo trainsetBaseInfo =  getTrainsetInfo(trainsetId);
        if (trainsetBaseInfo == null || trainsetBaseInfo.getTrainsetname() == null) {
            return "";
        }else
        {
            return trainsetBaseInfo.getTrainsetname();
        }
    }

    public TrainsetBaseInfo getTrainsetInfo(String trainsetId) {
        try {
            List<String> strList = new ArrayList<>();
            strList.add(trainsetId);
            return CacheUtil.getDataUseThreadCache("remoteService.getTrainsetBaseInfoByIds_" + strList, () -> {
                List<TrainsetBaseInfo> infos = iRemoteService.getTrainsetBaseInfoByIds(strList);
                if (infos.size() > 0) {
                    return infos.get(0);
                }
                return new TrainsetBaseInfo();
            });
        } catch (Exception ex) {
            logger.error("获取车组信息", ex);
            return null;
        }
    }

    //模板类型缓存
    public List<TemplateType> getTemplateTypeCache() {
        try {
            return CacheUtil.getDataUseThreadCache("BillCommon.TemplateTypeCache", () -> {
                SummaryTemplateTypeEnum[] summaryTemplateTypeEnums = new SummaryTemplateTypeEnum[]{
                    SummaryTemplateTypeEnum.CYC_ONE,  //一级修
                    SummaryTemplateTypeEnum.CYC_TWO, //二级修
                    SummaryTemplateTypeEnum.CYC_TEMP   //临时任务
                };
                List<String> queryTemplateTypeCode = Arrays.stream(summaryTemplateTypeEnums).map(v -> v.getValue()).collect(Collectors.toList());
                List<TemplateType> templateTypeList = midGroundService.getAllChildTemplateTypeList(queryTemplateTypeCode);
                return templateTypeList;
            });
        } catch (Exception ex) {
            logger.error("获取一二级修记录单回填模板类型缓存", ex);
            List<TemplateType> infos = new ArrayList<>();
            return infos;
        }
    }
    //根据模板类型编码获取模板信息缓存
    public TemplateTypeInfo getTemplateTypeInfoCache(String templateTypeCode)
    {
        try {
            return CacheUtil.getDataUseThreadCache("BillCommon.getTemplateTypeInfoCache_" +
                templateTypeCode, () -> {
                TemplateTypeInfo infos = midGroundService.getTemplateTypeInfo(templateTypeCode);
                return infos;
            });
        } catch (Exception ex) {
            logger.error("根据模板类型编码获取模板信息", ex);
            TemplateTypeInfo infos = new TemplateTypeInfo();
            return infos;
        }
    }


    //根据段编码、所编码、模板类型、车型、批次、项目类型、项目CODE创建模板字典
    public List<TemplateSummaryInfo> getTemplateCache(String depotCode, String unitCode, String typeCode, String trainType,
                                                       String batch, String itemType, String itemCode, int marshlCount) {
        try {
            return CacheUtil.getDataUseThreadCache("BillCommon.getTemplateCache_" +
                depotCode + "_" + unitCode + "_" + typeCode + "_" + trainType + "_" + batch + "_" + itemType + "_" + itemCode + "_" + marshlCount, () -> {
                TemplateQueryParamModel templateQueryParamModel = new TemplateQueryParamModel();
                templateQueryParamModel.setItemCode(itemCode);
                templateQueryParamModel.setUnitCode(unitCode);
                templateQueryParamModel.setDepotCode(depotCode);
                templateQueryParamModel.setTemplateTypeCode(typeCode);
                templateQueryParamModel.setBatch(batch);
                templateQueryParamModel.setTrainsetType(trainType);
                if(marshlCount > 0){
                    templateQueryParamModel.setMarshalCount(marshlCount);
                }
                List<TemplateSummaryInfo> infos = midGroundService.queryTemplateSummaryInfo(templateQueryParamModel);
                return infos;
            });
        } catch (Exception ex) {
            logger.error("根据段编码、所编码、车型、批次、项目类型、项目CODE创建模板字典", ex);
            List<TemplateSummaryInfo> infos = new ArrayList<>();
            return infos;
        }
    }

    /**
     * 获取动车组的轮轴信息
     * @param trainSetId 车组ID
     * @param carNo 辆序
     * @param date 时间
     * @return 轮轴信息
     */
    public List<AxleWheel>  getPartByTrainSetId(String trainSetId, String carNo, String date) {
        List<AxleWheel> lstAxleWheel = new ArrayList<AxleWheel>();
        try {
            lstAxleWheel = iRemoteService.getPartByTrainSetId(trainSetId,carNo,date);
        } catch (Exception ex) {
            logger.error("获取动车组的轮轴信息", ex);
        }
        return  lstAxleWheel;
    }

    /**
     * 根据车组ID获取该车组所有车轮的最新镟轮数据
     * @param trainSetId 车组ID
     * @return
     */
    public List<AxleWheelDiameterEntity> getLatestAxleTurningdata(String trainSetId){
        List<AxleWheelDiameterEntity> lathingDatas = new ArrayList<AxleWheelDiameterEntity>();
        try {
            lathingDatas = iRemoteService.getLatestAxleTurningdata(trainSetId);
        } catch (Exception ex) {
            logger.error("根据车组ID获取该车组所有车轮的最新镟轮数据", ex);
        }
        return lathingDatas;
    }

    /**
     * 根据车组ID获取车组详细编组信息
     * @param trainsetId 车组ID
     * @return 编组集合
     */
    public List<String> getTrainMarshlType(String trainsetId) {
        List<String> lstCarNos = new ArrayList<String>();
        try {
            TrainsetInfo trainsetInfo = iRemoteService.getTrainsetDetialInfo(trainsetId);
            int marshalCount = Integer.parseInt(trainsetInfo.getIMarshalcount());
            for(int i=1;i<marshalCount;i++){
                String carNo = String.valueOf(i);
                if(carNo.length() == 1)
                    carNo = "0"+ carNo;
                lstCarNos.add(carNo);
            }
            lstCarNos.add("00");
        } catch (Exception e) {
            logger.error("根据车组ID获取车组详细编组信息", e);
        }
        return lstCarNos;
    }

    /**
     * 根据项目编码获取项目详细信息
     * @param itemCode 项目编码
     * @param unitCode 运用所编码
     * @return 项目的详细信息
     */
    public RepairItemInfo getItemInfoByCode(String itemCode, String unitCode){
        return iRemoteService.getItemInfoByCode(itemCode, unitCode);
    }
}
