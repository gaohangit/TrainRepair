package com.ydzbinfo.emis.trainRepair.bill.fillback.service.impl.OneTwoRepairCheckInfoInitPackage;

import com.ydzbinfo.emis.trainRepair.bill.fillback.service.IChecklistDetailService;
import com.ydzbinfo.emis.trainRepair.bill.fillback.service.impl.AxleWheelDataServiceImpl;
import com.ydzbinfo.emis.trainRepair.bill.fillback.utils.AttributeEnum;
import com.ydzbinfo.emis.trainRepair.bill.fillback.utils.BillCommon;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.CarAxleEntity;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.ChecklistDetailInfoForShow;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.ChecklistSummaryInfoForShow;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.ChecklistTriggerUrlCallResult;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChecklistSummary;
import com.ydzbinfo.emis.trainRepair.remotemodel.device.EntityAxleDevice;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.AxleWheel;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetBaseInfo;
import com.ydzbinfo.emis.utils.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class InspectionAxleTask  extends XxtsTaskBase {

    @Autowired
    private BillCommon billCommon;

    protected static final Logger logger = getLogger(InspectionAxleTask.class);
    @Autowired
    IChecklistDetailService checklistDetailService;
    @Autowired
    AxleWheelDataServiceImpl axleWheelDataService;
    /**
     * 初始化数据
     */
    public List<ChecklistDetailInfoForShow> initContentData(ChecklistSummaryInfoForShow content) {
        super.initContentData(content);
        //显示导入按钮
        content.setShowImportButton(true);
        //修改的单元格
        List<ChecklistDetailInfoForShow> result = new ArrayList<ChecklistDetailInfoForShow>();
        //初始化数据
        ChecklistSummary summary = content.getExtraObject();
        List<ChecklistDetailInfoForShow> contentList = content.getCells();
        //获取车组ID和车组辆序
        TrainsetBaseInfo train = billCommon.getTrainsetInfo(summary.getTrainsetId());
        //获取当前日期
        String sDate =  getDateNow(0);
        //获取数据实体中所有属性为辆序的实体
        List<ChecklistDetailInfoForShow> lstCarNoDetails = getDetailByAttrForShow(contentList,AttributeEnum.ATTR_CAR);
        //获取数据实体中所有属性为轴位的实体
        List<ChecklistDetailInfoForShow> lstAxleDetails = getDetailByAttrForShow(contentList,AttributeEnum.ATTR_AXLE_POSTION);
        //初始化轮轴集合
        List<AxleWheel> lstAxleWheels = new ArrayList<AxleWheel>();
        //设置轴号
        result.addAll(setViewCell(train.getTrainsetid(),sDate,contentList,AttributeEnum.ATTR_AXLE_NUMBER,lstAxleWheels, lstCarNoDetails,lstAxleDetails));
        //设置轴走行公里
        result.addAll(setViewCell(train.getTrainsetid(),sDate,contentList,AttributeEnum.ATTR_AXLE_ACCUMILE,lstAxleWheels, lstCarNoDetails,lstAxleDetails));
        //设置轴属性
        result.addAll(setViewCell(train.getTrainsetid(),sDate,contentList,AttributeEnum.ATTR_AXLE_ATTRIBUTE,lstAxleWheels, lstCarNoDetails,lstAxleDetails));
        //获取通用属性
        result.addAll(initCommonAttribute(summary, contentList));
        return result;
    }
    /**
     * 初始化规则
     */
    public void initContentRule(ChecklistSummaryInfoForShow content) {
        super.initContentRule(content);
    }

    /**
     *  导入数据
     * @param checklistSummary 记录单实体
     * @return
     */
    public ChecklistTriggerUrlCallResult ImportData(ChecklistSummaryInfoForShow checklistSummary) {
        ChecklistTriggerUrlCallResult result = new ChecklistTriggerUrlCallResult();
        boolean isImport = false;
        String dayPlanId = "";
        String summaryId = "";
        String trainSetName = "";
        String itemName = "";
        try
        {
            ChecklistSummary summary = checklistSummary.getExtraObject();
            List<ChecklistDetailInfoForShow> lstDetails = checklistSummary.getCells();
            if (summary == null || lstDetails == null || lstDetails.size() == 0) {
                logger.error("数据总表或子表没有找到数据");
                return result;
            }
            //获取车组ID和车组辆序
            TrainsetBaseInfo train = billCommon.getTrainsetInfo(summary.getTrainsetId());
            if (train == null) {
                logger.error("根据车组ID获取车组信息的返回信息为null");
                return result;
            }
            dayPlanId = summary.getDayPlanId();
            summaryId = summary.getChecklistSummaryId();
            itemName = summary.getItemName();
            trainSetName = train.getTrainsetname();
            List<ChecklistDetailInfoForShow> changeCells = new ArrayList<ChecklistDetailInfoForShow>();
            List<CarAxleEntity> lstCarAxleEntitys = getCarNoAxles(summary);
            //获取开始日期和结束日期
            String sStartDate = getDateNow(-3);    //开始日期
            String sEndDate = getDateNow(3);       //结束日期
            List<EntityAxleDevice> entityAxleDevices = getAxleGetDeviceRecord(train.getTrainsetname(), sStartDate, sEndDate);
            //新建一个Hash字典表，记录导入人员的名称和随机职工编码
            HashMap<String, String> hashStaffInfo = new HashMap<String, String>();
            //循环获取到的设备数据
            for(int i=0;i< entityAxleDevices.size();i++){
                EntityAxleDevice entity = entityAxleDevices.get(i);
                String carNo = entity.getCARNO();
                String axle = String.valueOf(entity.getAXLEPOS());

                boolean isAllowFill = true;
                for(CarAxleEntity carAxle : lstCarAxleEntitys){
                    if(carAxle.getCarNo().equals(carNo) && carAxle.getAxleNo().equals(axle)){
                        isAllowFill = carAxle.getAllowFill();
                        break;
                    }
                }
                if(!isAllowFill)
                    continue;

                ChecklistDetailInfoForShow axleConfirm = getDetail(summary.getTrainsetId(), lstDetails, carNo, axle, AttributeEnum.ATTR_AXLE_CONFIRM, "√");             //轴号核实
                ChecklistDetailInfoForShow axleInpostion = getDetail(summary.getTrainsetId(), lstDetails, carNo, axle, AttributeEnum.ATTR_AXLE_INPOSTION, entity.getADAPTSIDE().equals("1")?"A":"B");         //进入轴端
                ChecklistDetailInfoForShow axleCheckResult = getDetail(summary.getTrainsetId(), lstDetails, carNo, axle, AttributeEnum.ATTR_REPAIR_RESULT, entity.getINSPECTIONRESLUT().equals("1")? "×":"√");        //探测结果
                ChecklistDetailInfoForShow axleUnLoaderA = getDetail(summary.getTrainsetId(), lstDetails, carNo, axle, AttributeEnum.ATTR_AXLE_TASKDOWN_A, entity.getASIDEUNLOADER());        //轴端拆卸A端
                ChecklistDetailInfoForShow axleUnLoaderB = getDetail(summary.getTrainsetId(), lstDetails, carNo, axle, AttributeEnum.ATTR_AXLE_TASKDOWN_B, entity.getBSIDEUNLOADER());        //轴端拆卸B端
                ChecklistDetailInfoForShow axleUnLoaderE = getDetail(summary.getTrainsetId(), lstDetails, carNo, axle, AttributeEnum.ATTR_AXLE_TASKDOWN_E, entity.getELECTRICUNLOADER());        //轴端拆卸电务
                ChecklistDetailInfoForShow axleLoaderA = getDetail(summary.getTrainsetId(), lstDetails, carNo, axle, AttributeEnum.ATTR_AXLE_INSTALL_A, entity.getASIDELOADER());           //轴端安装A端
                ChecklistDetailInfoForShow axleLoaderB = getDetail(summary.getTrainsetId(), lstDetails, carNo, axle, AttributeEnum.ATTR_AXLE_INSTALL_B, entity.getBSIDELOADER());           //轴端安装B端
                ChecklistDetailInfoForShow axleLoaderE = getDetail(summary.getTrainsetId(), lstDetails, carNo, axle, AttributeEnum.ATTR_AXLE_INSTALL_E, entity.getELECTRICLOADER());           //轴端安装电务
                ChecklistDetailInfoForShow axlePutioto = getDetail(summary.getTrainsetId(), lstDetails, carNo, axle, AttributeEnum.ATTR_AXLE_PUTINTO, entity.getANTIRUST());             //轴孔防锈油实施者
                ChecklistDetailInfoForShow axlePerson = getDetail(summary.getTrainsetId(), lstDetails, carNo, axle, AttributeEnum.ATTR_REPAIR_PERSON, entity.getINSPECTOR());             //探伤工
                ChecklistDetailInfoForShow axleQuality = getDetail(summary.getTrainsetId(), lstDetails, carNo, axle, AttributeEnum.ATTR_QUALITY_AXLESIGN, entity.getQUALITYINSPECTOR());         //轴质检
                if(axleConfirm!=null)
                    changeCells.add(axleConfirm);
                if(axleInpostion!=null)
                    changeCells.add(axleInpostion);
                if(axleCheckResult!=null)
                    changeCells.add(axleCheckResult);
                if(axleUnLoaderA!=null){
                    if(!StringUtils.isEmpty(axleUnLoaderA.getValue())){
                        if(!hashStaffInfo.containsKey(axleUnLoaderA.getValue())){
                            hashStaffInfo.put(axleUnLoaderA.getValue(), getImportRandom());
                        }
                        if(hashStaffInfo.containsKey(axleUnLoaderA.getValue())){
                            axleUnLoaderA.setCode(hashStaffInfo.get(axleUnLoaderA.getValue()));
                        }
                    }
                    changeCells.add(axleUnLoaderA);
                }
                if(axleUnLoaderB!=null) {
                    if(!StringUtils.isEmpty(axleUnLoaderB.getValue())){
                        if(!hashStaffInfo.containsKey(axleUnLoaderB.getValue())){
                            hashStaffInfo.put(axleUnLoaderB.getValue(), getImportRandom());
                        }
                        if(hashStaffInfo.containsKey(axleUnLoaderB.getValue())){
                            axleUnLoaderB.setCode(hashStaffInfo.get(axleUnLoaderB.getValue()));
                        }
                    }
                    changeCells.add(axleUnLoaderB);
                }
                if(axleUnLoaderE!=null) {
                    if(!StringUtils.isEmpty(axleUnLoaderE.getValue())){
                        if(!hashStaffInfo.containsKey(axleUnLoaderE.getValue())){
                            hashStaffInfo.put(axleUnLoaderE.getValue(), getImportRandom());
                        }
                        if(hashStaffInfo.containsKey(axleUnLoaderE.getValue())){
                            axleUnLoaderE.setCode(hashStaffInfo.get(axleUnLoaderE.getValue()));
                        }
                    }
                    changeCells.add(axleUnLoaderE);
                }
                if(axleLoaderA!=null) {
                    if(!StringUtils.isEmpty(axleLoaderA.getValue())){
                        if(!hashStaffInfo.containsKey(axleLoaderA.getValue())){
                            hashStaffInfo.put(axleLoaderA.getValue(), getImportRandom());
                        }
                        if(hashStaffInfo.containsKey(axleLoaderA.getValue())){
                            axleLoaderA.setCode(hashStaffInfo.get(axleLoaderA.getValue()));
                        }
                    }
                    changeCells.add(axleLoaderA);
                }
                if(axleLoaderB!=null) {
                    if(!StringUtils.isEmpty(axleLoaderB.getValue())){
                        if(!hashStaffInfo.containsKey(axleLoaderB.getValue())){
                            hashStaffInfo.put(axleLoaderB.getValue(), getImportRandom());
                        }
                        if(hashStaffInfo.containsKey(axleLoaderB.getValue())){
                            axleLoaderB.setCode(hashStaffInfo.get(axleLoaderB.getValue()));
                        }
                    }
                    changeCells.add(axleLoaderB);
                }
                if(axleLoaderE!=null){
                    if(!StringUtils.isEmpty(axleLoaderE.getValue())){
                        if(!hashStaffInfo.containsKey(axleLoaderE.getValue())){
                            hashStaffInfo.put(axleLoaderE.getValue(), getImportRandom());
                        }
                        if(hashStaffInfo.containsKey(axleLoaderE.getValue())){
                            axleLoaderE.setCode(hashStaffInfo.get(axleLoaderE.getValue()));
                        }
                    }
                    changeCells.add(axleLoaderE);
                }
                if(axlePutioto!=null){
                    if(!StringUtils.isEmpty(axlePutioto.getValue())){
                        if(!hashStaffInfo.containsKey(axlePutioto.getValue())){
                            hashStaffInfo.put(axlePutioto.getValue(), getImportRandom());
                        }
                        if(hashStaffInfo.containsKey(axlePutioto.getValue())){
                            axlePutioto.setCode(hashStaffInfo.get(axlePutioto.getValue()));
                        }
                    }
                    changeCells.add(axlePutioto);
                }
                if(axlePerson!=null){
                    if(!StringUtils.isEmpty(axlePerson.getValue())){
                        if(!hashStaffInfo.containsKey(axlePerson.getValue())){
                            hashStaffInfo.put(axlePerson.getValue(), getImportRandom());
                        }
                        if(hashStaffInfo.containsKey(axlePerson.getValue())){
                            axlePerson.setCode(hashStaffInfo.get(axlePerson.getValue()));
                        }
                    }
                    changeCells.add(axlePerson);
                }
                if(axleQuality!=null) {
                    if(!StringUtils.isEmpty(axleQuality.getValue())){
                        if(!hashStaffInfo.containsKey(axleQuality.getValue())){
                            hashStaffInfo.put(axleQuality.getValue(), getImportRandom());
                        }
                        if(hashStaffInfo.containsKey(axleQuality.getValue())){
                            axleQuality.setCode(hashStaffInfo.get(axleQuality.getValue()));
                        }
                    }
                    changeCells.add(axleQuality);
                }
            }
            for(ChecklistDetailInfoForShow detail : changeCells){
                detail.setSaved(false);
            }
            ChecklistDetailInfoForShow repairShow = setRepairPerson(lstDetails);
            if(repairShow != null && repairShow.getAttributeCode().equals(AttributeEnum.ATTR_REPAIR_PERSON_SHOW.getValue()))
                changeCells.add(repairShow);
            axleWheelDataService.updateAxleImportRecord(summary, entityAxleDevices);
            result.setChangedCells(changeCells);
            isImport = changeCells.size()>0;
        }catch (Exception e){
            isImport = false;
            logger.error("导入空心轴探伤数据时失败", e);
        }
        axleWheelDataService.updateAxleEntity(dayPlanId, summaryId, trainSetName, itemName, "6", isImport);
        return result;
    }

}
