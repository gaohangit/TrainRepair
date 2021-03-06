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
    //??????????????????????????????
    public boolean saveCellDetail(List<ChecklistDetailInfoForSave> cells, String checklistSummaryID) {
        boolean isChanged = false;
        try {
            //????????????
            //   ChecklistSummary summary = saveInfo.getExtraObject();
            //  List<ChecklistDetailInfoForSave> cells = saveInfo.getCells();
            for (ChecklistDetailInfoForSave info : cells) {
                ChecklistDetail detail = new ChecklistDetail();
                BeanUtils.copyProperties(info, detail);
                List<ChkDetailLinkContentForModule> linkCells = info.getLinkCells();
                List<ChecklistLinkControl> controls = info.getControls();
                //??????DETAIL???
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
            logger.error("/BillCommon/saveCellDetail----??????????????????...", exc);
            throw exc;
        }
        return isChanged ;
    }


    /**
     * ???????????????????????????
     * @param itemCycles ??????????????????  ??????(0101-1231):???????????????:????????????(540???-54, +54??????)/??????(60.0???-6.0, +6.0????????????)
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
                        case "A":   //????????????
                        case "C":   //????????????
                            if(lstItemCycs.size() >0){
                                lstItemCycs.add(0, String.format("%s(%s,-%s)???", cyc.getCycValue(), cyc.getCycPlusValue(), cyc.getCycMinusValue()));
                            }else {
                                lstItemCycs.add(String.format("%s(%s,-%s)???", cyc.getCycValue(), cyc.getCycPlusValue(), cyc.getCycMinusValue()));
                            }
                            break;
                        case "B":   //??????
                            lstItemCycs.add(String.format("%s(%s,-%s)?????????", cyc.getCycValue(), cyc.getCycPlusValue(), cyc.getCycMinusValue()));
                            break;
                        case "D":   //???
                            break;
                    }
                }
                itemCycle = String.join("/", lstItemCycs);
            }
        }catch (Exception ex){
            logger.error("BillCommon.getItemCycle?????????????????????????????????????????????????????????",ex);
            itemCycle = "";
        }
        return itemCycle;
    }
    /**
     * ??????????????????
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
            logger.error("??????????????????", ex);
            return 0;
        }
    }
    //????????????CODE???????????????????????????
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
            logger.error("????????????CODE?????????????????????????????????!", ex);
            return null;
        }
    }


    public LeaveBackTrainNoResult getTrainsetLeaveBack(String trainsetId, String date) {
        try {
            LeaveBackTrainNoResult trainNoList = iRemoteService.getLeavBackTrainNo(trainsetId, date);
            return trainNoList;
        } catch (Exception ex) {
            logger.error("???????????????????????????", ex);
        }
        return new LeaveBackTrainNoResult();
    }


    //???????????????????????????????????????
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
            logger.error("??????????????????", ex);
            return null;
        }
    }

    //??????????????????
    public List<TemplateType> getTemplateTypeCache() {
        try {
            return CacheUtil.getDataUseThreadCache("BillCommon.TemplateTypeCache", () -> {
                SummaryTemplateTypeEnum[] summaryTemplateTypeEnums = new SummaryTemplateTypeEnum[]{
                    SummaryTemplateTypeEnum.CYC_ONE,  //?????????
                    SummaryTemplateTypeEnum.CYC_TWO, //?????????
                    SummaryTemplateTypeEnum.CYC_TEMP   //????????????
                };
                List<String> queryTemplateTypeCode = Arrays.stream(summaryTemplateTypeEnums).map(v -> v.getValue()).collect(Collectors.toList());
                List<TemplateType> templateTypeList = midGroundService.getAllChildTemplateTypeList(queryTemplateTypeCode);
                return templateTypeList;
            });
        } catch (Exception ex) {
            logger.error("???????????????????????????????????????????????????", ex);
            List<TemplateType> infos = new ArrayList<>();
            return infos;
        }
    }
    //????????????????????????????????????????????????
    public TemplateTypeInfo getTemplateTypeInfoCache(String templateTypeCode)
    {
        try {
            return CacheUtil.getDataUseThreadCache("BillCommon.getTemplateTypeInfoCache_" +
                templateTypeCode, () -> {
                TemplateTypeInfo infos = midGroundService.getTemplateTypeInfo(templateTypeCode);
                return infos;
            });
        } catch (Exception ex) {
            logger.error("??????????????????????????????????????????", ex);
            TemplateTypeInfo infos = new TemplateTypeInfo();
            return infos;
        }
    }


    //????????????????????????????????????????????????????????????????????????????????????CODE??????????????????
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
            logger.error("?????????????????????????????????????????????????????????????????????CODE??????????????????", ex);
            List<TemplateSummaryInfo> infos = new ArrayList<>();
            return infos;
        }
    }

    /**
     * ??????????????????????????????
     * @param trainSetId ??????ID
     * @param carNo ??????
     * @param date ??????
     * @return ????????????
     */
    public List<AxleWheel>  getPartByTrainSetId(String trainSetId, String carNo, String date) {
        List<AxleWheel> lstAxleWheel = new ArrayList<AxleWheel>();
        try {
            lstAxleWheel = iRemoteService.getPartByTrainSetId(trainSetId,carNo,date);
        } catch (Exception ex) {
            logger.error("??????????????????????????????", ex);
        }
        return  lstAxleWheel;
    }

    /**
     * ????????????ID????????????????????????????????????????????????
     * @param trainSetId ??????ID
     * @return
     */
    public List<AxleWheelDiameterEntity> getLatestAxleTurningdata(String trainSetId){
        List<AxleWheelDiameterEntity> lathingDatas = new ArrayList<AxleWheelDiameterEntity>();
        try {
            lathingDatas = iRemoteService.getLatestAxleTurningdata(trainSetId);
        } catch (Exception ex) {
            logger.error("????????????ID????????????????????????????????????????????????", ex);
        }
        return lathingDatas;
    }

    /**
     * ????????????ID??????????????????????????????
     * @param trainsetId ??????ID
     * @return ????????????
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
            logger.error("????????????ID??????????????????????????????", e);
        }
        return lstCarNos;
    }

    /**
     * ??????????????????????????????????????????
     * @param itemCode ????????????
     * @param unitCode ???????????????
     * @return ?????????????????????
     */
    public RepairItemInfo getItemInfoByCode(String itemCode, String unitCode){
        return iRemoteService.getItemInfoByCode(itemCode, unitCode);
    }
}
