package com.ydzbinfo.emis.trainRepair.bill.fillback.service.impl.OneTwoRepairCheckInfoInitPackage;

import com.ydzbinfo.emis.trainRepair.bill.fillback.service.impl.AxleWheelDataServiceImpl;
import com.ydzbinfo.emis.trainRepair.bill.fillback.utils.AttributeEnum;
import com.ydzbinfo.emis.trainRepair.bill.fillback.utils.BillCommon;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.CarAxleEntity;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.ChecklistDetailInfoForShow;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.ChecklistSummaryInfoForShow;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.ChecklistTriggerUrlCallResult;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChecklistSummary;
import com.ydzbinfo.emis.trainRepair.remotemodel.device.EntityLUDevice;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.AxleWheel;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetBaseInfo;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class LUAxleTask  extends XxtsTaskBase {

    protected static final Logger logger = getLogger(LUAxleTask.class);

    @Autowired
    private BillCommon billCommon;

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
        //设置轴属性
        result.addAll(setViewCell(train.getTrainsetid(),sDate,contentList,AttributeEnum.ATTR_AXLE_ATTRIBUTE,lstAxleWheels, lstCarNoDetails,lstAxleDetails));
        //设置一位轮号
        result.addAll(setViewCell(train.getTrainsetid(),sDate,contentList,AttributeEnum.ATTR_WHEEL_ONENUMBER,lstAxleWheels, lstCarNoDetails,lstAxleDetails));
        //设置二位轮号
        result.addAll(setViewCell(train.getTrainsetid(),sDate,contentList,AttributeEnum.ATTR_WHEEL_TWONUMBER,lstAxleWheels, lstCarNoDetails,lstAxleDetails));
        //设置一位轮走行
        result.addAll(setViewCell(train.getTrainsetid(),sDate,contentList,AttributeEnum.ATTR_WHEEL_ONEACCUMILE,lstAxleWheels, lstCarNoDetails,lstAxleDetails));
        //设置二位轮走行
        result.addAll(setViewCell(train.getTrainsetid(),sDate,contentList,AttributeEnum.ATTR_WHEEL_TWOACCUMILE,lstAxleWheels, lstCarNoDetails,lstAxleDetails));
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
            List<EntityLUDevice> entityLUDevices = getLUGetDeviceRecord(train.getTrainsetname(), sStartDate, sEndDate);
            //新建一个Hash字典表，记录导入人员的名称和随机职工编码
            HashMap<String, String> hashStaffInfo = new HashMap<String, String>();
            for(EntityLUDevice lu : entityLUDevices){
                String carNo = lu.getCARRIAGENO().substring(lu.getCARRIAGENO().length() - 2, lu.getCARRIAGENO().length());    //辆序
                String axle = lu.getALXEPOSITION();                                                 //轴位

                boolean isAllowFill = true;
                for(CarAxleEntity carAxle : lstCarAxleEntitys){
                    if(carAxle.getCarNo().equals(carNo) && carAxle.getAxleNo().equals(axle)){
                        isAllowFill = carAxle.getAllowFill();
                        break;
                    }
                }
                if(!isAllowFill)
                    continue;

                List<ChecklistDetailInfoForShow> detailWheelConfir = getDetailArray(summary.getTrainsetId(), lstDetails,carNo, axle,AttributeEnum.ATTR_WHEEL_CONFIRM, "√"); //轮号核实
                if(detailWheelConfir != null)
                    changeCells.addAll(detailWheelConfir);
                List<ChecklistDetailInfoForShow> detailResult1 = getDetailArray(summary.getTrainsetId(), lstDetails,carNo, axle,AttributeEnum.ATTR_REPAIR_ONERESULT, lu.getFIRSTWHEELRESULT().equals("0") ?"√":"×");     //探测结果
                if(detailResult1 != null){
                    changeCells.addAll(detailResult1);
                }
                List<ChecklistDetailInfoForShow> detailResult2 = getDetailArray(summary.getTrainsetId(), lstDetails,carNo, axle,AttributeEnum.ATTR_REPAIR_TWORESULT, lu.getSECONDWHEELRESULT().equals("0") ?"√":"×");     //探测结果
                if(detailResult2 != null){
                    changeCells.addAll(detailResult2);
                }
                List<ChecklistDetailInfoForShow> detailOperator = getDetailArray(summary.getTrainsetId(), lstDetails,carNo, axle,AttributeEnum.ATTR_REPAIR_PERSON, lu.getOPERATORNAME());     //作业人
                if(detailOperator != null) {
                    for(ChecklistDetailInfoForShow operator: detailOperator){
                        if(!hashStaffInfo.containsKey(operator.getValue())){
                            hashStaffInfo.put(operator.getValue(), getImportRandom());
                        }
                        if(hashStaffInfo.containsKey(operator.getValue())){
                            operator.setCode(hashStaffInfo.get(operator.getValue()));
                        }
                    }
                    changeCells.addAll(detailOperator);
                }
            }
            for(ChecklistDetailInfoForShow detail : changeCells){
                detail.setSaved(false);
            }
            ChecklistDetailInfoForShow repairShow = setRepairPerson(lstDetails);
            if(repairShow != null && repairShow.getAttributeCode().equals(AttributeEnum.ATTR_REPAIR_PERSON_SHOW.getValue()))
                changeCells.add(repairShow);
            result.setChangedCells(changeCells);
            isImport = changeCells.size()>0;
            axleWheelDataService.updateLuImportRecord(summary, entityLUDevices);
        }catch (Exception e){
            isImport = false;
            logger.error("导入轮辋轮辐探伤数据时失败", e);
        }
        axleWheelDataService.updateAxleEntity(dayPlanId, summaryId, trainSetName, itemName, "7", isImport);
        return result;
    }

}
