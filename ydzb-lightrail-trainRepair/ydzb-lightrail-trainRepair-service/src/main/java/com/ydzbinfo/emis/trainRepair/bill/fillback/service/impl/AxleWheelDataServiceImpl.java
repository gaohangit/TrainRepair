package com.ydzbinfo.emis.trainRepair.bill.fillback.service.impl;

import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import com.ydzbinfo.emis.trainRepair.bill.fillback.utils.AttributeEnum;
import com.ydzbinfo.emis.trainRepair.bill.fillback.utils.BillCommon;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.ChecklistDetailInfoForSave;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.ChecklistSummaryInfoForSave;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.ChkDetailLinkContentForModule;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.*;
import com.ydzbinfo.emis.trainRepair.remotemodel.device.EntityAxleDevice;
import com.ydzbinfo.emis.trainRepair.remotemodel.device.EntityLUDevice;
import com.ydzbinfo.emis.trainRepair.remotemodel.device.EntityUlatheDevice;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetBaseInfo;
import com.ydzbinfo.emis.utils.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class AxleWheelDataServiceImpl {

    protected static final Logger logger = getLogger(AxleWheelDataServiceImpl.class);

    @Autowired
    AxleWheelDataCurServiceImpl axleWheelDataCurService;

    @Autowired
    AxleWheelDataHisServiceImpl axleWheelDataHisService;

    @Autowired
    ImportRecordServiceImpl importRecordService;

    @Autowired
    ImportDetailServiceImpl importDetailService;

    @Autowired
    private BillCommon billCommon;

    /**
     * 导入数据时写入数据
     */
    @Autowired
    XxtsImportRecordServiceImpl xxtsImportRecordService;

    /**
     * 写入镟修数据
     * @param content UI显示的记录单数据信息
     */
    public void addAxleWheelData(ChecklistSummaryInfoForSave content){
        ChecklistSummary summary = content.getExtraObject();
        try{
            List<AxleWheelDataCur> lstAxleWheelDatas = new ArrayList<AxleWheelDataCur>();
            List<ChecklistDetailInfoForSave> cells = content.getCells();
            List<ChecklistDetailInfoForSave> lstCarNos = getDetailByAttr(cells, AttributeEnum.ATTR_CAR);                       //辆序集合
            List<ChecklistDetailInfoForSave> lstAxles = getDetailByAttr(cells, AttributeEnum.ATTR_AXLE_POSTION);              //轴位集合
            int count = 0;
            for(ChecklistDetailInfoForSave cell : cells){
                String carNo = "";          //辆序
                String axlePosition = "";   //轴位
                String partLocation = "";   //端位
                String axleRumiles = "";    //轴走行公里
                String repairPerson = "";   //检修人员签字
                String qualityAxle = "";    //轴质检人员签字
                boolean isXq = true;
                AxleWheelDataCur axleWheelDataCur = null;

                //一位镟前轮径值
                if (cell.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_ONEXQ.getValue())){
                    carNo = getCellValue(cell.getLinkCells(), lstCarNos);
                    axlePosition = getCellValue(cell.getLinkCells(), lstAxles);
                    partLocation = getPartLocation(axlePosition, true);
                    isXq = true;
                }
                //一位镟后轮径值
                if (cell.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_ONEXH.getValue())){
                    carNo = getCellValue(cell.getLinkCells(), lstCarNos);
                    axlePosition = getCellValue(cell.getLinkCells(), lstAxles);
                    partLocation = getPartLocation(axlePosition, true);
                    isXq = false;
                }
                //二位镟前轮径值
                if (cell.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_TWOXQ.getValue())){
                    carNo = getCellValue(cell.getLinkCells(), lstCarNos);
                    axlePosition = getCellValue(cell.getLinkCells(), lstAxles);
                    partLocation = getPartLocation(axlePosition, false);
                    isXq = true;
                }
                //二位镟后轮径值
                if (cell.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_TWOXH.getValue())){
                    carNo = getCellValue(cell.getLinkCells(), lstCarNos);
                    axlePosition = getCellValue(cell.getLinkCells(), lstAxles);
                    partLocation = getPartLocation(axlePosition, false);
                    isXq = false;
                }
                //轴走行公里
                if (cell.getAttributeCode().equals(AttributeEnum.ATTR_AXLE_ACCUMILE.getValue())){
                    carNo = getCellValue(cell.getLinkCells(), lstCarNos);
                    axlePosition = getCellValue(cell.getLinkCells(), lstAxles);
                    axleRumiles = cell.getValue();
                }
                //检修人员签字
                if (cell.getAttributeCode().equals(AttributeEnum.ATTR_REPAIR_PERSON.getValue())){
                    carNo = getCellValue(cell.getLinkCells(), lstCarNos);
                    axlePosition = getCellValue(cell.getLinkCells(), lstAxles);
                    repairPerson = cell.getValue();
                }
                //轴质检签字
                if (cell.getAttributeCode().equals(AttributeEnum.ATTR_QUALITY_AXLESIGN.getValue())){
                    carNo = getCellValue(cell.getLinkCells(), lstCarNos);
                    axlePosition = getCellValue(cell.getLinkCells(), lstAxles);
                    qualityAxle = cell.getValue();
                }
                //辆序、轴位、端位全部有值的情况
                if(!IsNullOrEmpty(carNo) && !IsNullOrEmpty(axlePosition) && !IsNullOrEmpty(partLocation)){
                    axleWheelDataCur = getAxleWheelData(summary, lstAxleWheelDatas,carNo,axlePosition, partLocation);
                    if(axleWheelDataCur != null){
                        if(isXq){
                            axleWheelDataCur.setXq(cell.getValue());
                        }else{
                            axleWheelDataCur.setXh(cell.getValue());
                        }
                    }
                }
                //辆序、轴位有值，但是端位没有值的情况
                if (!IsNullOrEmpty(carNo) && !IsNullOrEmpty(axlePosition) && IsNullOrEmpty(partLocation)){
                    List<AxleWheelDataCur> cs = getAxleWheelData(summary, lstAxleWheelDatas,carNo,axlePosition);
                    for(AxleWheelDataCur c : cs){
                        if(!StringUtils.isEmpty(axleRumiles)){
                            c.setAxleRumiles(axleRumiles);
                        }
                        if(!StringUtils.isEmpty(repairPerson)){
                            c.setEddyStatffName(repairPerson);
                        }
                        if(!StringUtils.isEmpty(qualityAxle)){
                            c.setInspectorStaffName(qualityAxle);
                        }
                    }
                }
            }
            List<AxleWheelDataCur> lstNA = new ArrayList<AxleWheelDataCur>();
            for(AxleWheelDataCur cur : lstAxleWheelDatas){
                if(StringUtils.isEmpty(cur.getXh()) || StringUtils.isEmpty(cur.getXq()) || cur.getXh().equals("N/A") || cur.getXq().equals("N/A")){
                    lstNA.add(cur);
                }
            }
            lstAxleWheelDatas.removeAll(lstNA);
            updateAxleWheelData(lstAxleWheelDatas);
        }catch (Exception ex){
            logger.error(String.format("日计划：%s 动车组：%s 的记录单：%s 向轮径变化通知单和轴轮径记录单使用的镟修记录表写入数据时失败",
                    summary.getDayPlanId(), summary.getTrainsetId(), summary.getChecklistSummaryId()),ex);
        }
    }

    /**
     * 更新数据
     * @param lstAxleWheelDatas 数据集合
     */
    @Transactional
    void updateAxleWheelData(List<AxleWheelDataCur> lstAxleWheelDatas){
        List<AxleWheelDataHis> lstAxleWheelDataHis = new ArrayList<AxleWheelDataHis>();
        for(AxleWheelDataCur cur : lstAxleWheelDatas){
            AxleWheelDataHis his = new AxleWheelDataHis();
            BeanUtils.copyProperties(cur, his);
            lstAxleWheelDataHis.add(his);
        }
        axleWheelDataCurService.update(lstAxleWheelDatas);
        axleWheelDataHisService.insertBatch(lstAxleWheelDataHis);
    }

    /**
     * 更新导入记录
     * @param dayplanid 日计划ID
     * @param summaryId 记录单ID
     * @param trainSetName 车组名称
     * @param itemName 项目名称
     * @param docType 单据类型
     * @param isSuccess 是否成功导入数据
     */
    public void updateAxleEntity(String dayplanid, String summaryId, String trainSetName, String itemName, String docType, boolean isSuccess){
        try {
            XxtsImportRecord importRecord = new XxtsImportRecord();
            importRecord.setId(UUID.randomUUID().toString());
            importRecord.setCheckListSummaryId(summaryId);
            importRecord.setDayPalnId(dayplanid);
            importRecord.setTrainSetName(trainSetName);
            importRecord.setSpRepairItemName(itemName);
            importRecord.setDocType(docType);
            importRecord.setState(isSuccess ? "1" : "0");
            xxtsImportRecordService.updateAxleEntity(importRecord);
        }
        catch (Exception e){
            logger.error(String.format("日计划：%s 动车组：%s 项目：%s 的 %s 类型记录单：%s 写入导入记录时失败",
                    dayplanid, trainSetName, itemName, docType, summaryId),e);
        }
    }

    /**
     * 更新导入数据
     * @param summary 记录单数据
     * @param entityUlatheDevices 镟修探伤设备接口数据
     */
    @Transactional
    public void updateUlatheImportRecord(ChecklistSummary summary, List<EntityUlatheDevice> entityUlatheDevices){
        try {
            ImportRecord record = createImportRecord(summary);
            List<ImportDetail> details = new ArrayList<ImportDetail>();
            if (entityUlatheDevices != null && entityUlatheDevices.size() > 0) {
                for (EntityUlatheDevice device : entityUlatheDevices) {
                    String wheelset_id = device.getS_WHEELSET_ID();
                    wheelset_id = wheelset_id.replace("---", "-");
                    wheelset_id = wheelset_id.replace("--", "-");
                    String[] array = wheelset_id.split("-");
                    if (array.length != 4) {
                        continue;
                    }
                    ImportDetail detail = new ImportDetail();
                    detail.setId(UUID.randomUUID().toString());
                    detail.setImportRecordId(record.getId());
                    detail.setCheckListSummaryId(record.getCheckListSummaryId());
                    detail.setTrainSetId(record.getTrainSetId());
                    detail.setCarNo(array[2]);
                    detail.setAxlePosition(array[3]);
                    detail.setDeviceDataId(device.getS_ID());
                    details.add(detail);
                }
            }
            record.setImportState(details.size() > 0 ? "1" : "0");
            importRecordService.insert(record);
            importDetailService.updateImportDetail(details);
        }
        catch (Exception e){
            logger.error("更新镟修探伤导入数据",e);
        }
    }

    /**
     * 更新导入数据
     * @param summary 记录单数据
     * @param entityAxleDevices 空心轴探伤设备接口数据
     */
    @Transactional
    public void updateAxleImportRecord(ChecklistSummary summary, List<EntityAxleDevice> entityAxleDevices) {
        try {
            ImportRecord record = createImportRecord(summary);
            List<ImportDetail> details = new ArrayList<ImportDetail>();
            if (entityAxleDevices != null && entityAxleDevices.size() > 0) {
                for (EntityAxleDevice device : entityAxleDevices) {
                    String carNo = device.getCARNO().substring(device.getCARNO().length() - 2, 2);
                    String axle = device.getAXLENO();
                    ImportDetail detail = new ImportDetail();
                    detail.setId(UUID.randomUUID().toString());
                    detail.setImportRecordId(record.getId());
                    detail.setCheckListSummaryId(record.getCheckListSummaryId());
                    detail.setTrainSetId(record.getTrainSetId());
                    detail.setCarNo(carNo);
                    detail.setAxlePosition(axle);
                    detail.setDeviceDataId(device.getINSPECTID());
                    details.add(detail);
                }
            }
            record.setImportState(details.size() > 0 ? "1" : "0");
            importRecordService.insert(record);
            importDetailService.updateImportDetail(details);
        } catch (Exception e) {
            logger.error("更新空心轴探伤导入数据", e);
        }
    }

    /**
     * 更新导入数据
     * @param summary 记录单数据
     * @param entityLUDevices LU探伤设备接口数据
     */
    @Transactional
    public void updateLuImportRecord(ChecklistSummary summary, List<EntityLUDevice> entityLUDevices) {
        try {
            ImportRecord record = createImportRecord(summary);
            List<ImportDetail> details = new ArrayList<ImportDetail>();
            if (entityLUDevices != null && entityLUDevices.size() > 0) {
                for (EntityLUDevice device : entityLUDevices) {
                    String carNo = device.getCARRIAGENO().substring(device.getCARRIAGENO().length() - 2, device.getCARRIAGENO().length());    //辆序
                    String axle = device.getALXEPOSITION();                                                 //轴位
                    ImportDetail detail = new ImportDetail();
                    detail.setId(UUID.randomUUID().toString());
                    detail.setImportRecordId(record.getId());
                    detail.setCheckListSummaryId(record.getCheckListSummaryId());
                    detail.setTrainSetId(record.getTrainSetId());
                    detail.setCarNo(carNo);
                    detail.setAxlePosition(axle);
                    detail.setDeviceDataId(device.getLUCHECKRECORDID());
                    details.add(detail);
                }
            }
            record.setImportState(details.size() > 0 ? "1" : "0");
            importRecordService.insert(record);
            importDetailService.updateImportDetail(details);
        } catch (Exception e) {
            logger.error("更新LU探伤导入数据", e);
        }
    }

    /**
     * 生成导入记录数据实体
     * @param summary 记录单数据
     * @return 导入记录
     */
    private ImportRecord createImportRecord(ChecklistSummary summary){
        ImportRecord record = new ImportRecord();
        record.setId(UUID.randomUUID().toString());
        record.setDayPlanId(summary.getDayPlanId());
        record.setCheckListSummaryId(summary.getChecklistSummaryId());
        record.setTrainSetId(summary.getTrainsetId());
        record.setItemCode(summary.getItemCode());
        record.setItemName(summary.getItemName());
        record.setTempLateTypeCode(summary.getTemplateType());
        record.setImportTime(new Date());
        ShiroUser currentUser = ShiroKit.getUser();
        record.setStaffCode(currentUser.getStaffId());
        record.setStaffName(currentUser.getName());
        return record;
    }

    /**
     * 获取整理要的数据实体
     * @param summary 记录单信息
     * @param ds 数据实体集合
     * @param carNo 辆序
     * @param axlePosition 轴位
     * @return 数据实体集合
     */
    private List<AxleWheelDataCur> getAxleWheelData(ChecklistSummary summary, List<AxleWheelDataCur> ds, String carNo, String axlePosition){
        List<AxleWheelDataCur> cs = new ArrayList<AxleWheelDataCur>();
        TrainsetBaseInfo train = billCommon.getTrainsetInfo(summary.getTrainsetId());
        for(AxleWheelDataCur d : ds){
            if(d.getCarNo().equals(carNo) && d.getAxleLocation().equals(axlePosition)){
                cs.add(d);
            }
        }
        List<String> partLocations = getPartLocation(axlePosition);
        for(String partLocation : partLocations){
            boolean isExists = false;   //是否存在指定端位的数据
            for(AxleWheelDataCur c : cs){
                if(c.getPartLocation().equals(partLocation)){
                    isExists = true;
                    break;
                }
            }
            //不存在则新建数据实体
            if(!isExists){
                AxleWheelDataCur data = new AxleWheelDataCur();
                data.setId(UUID.randomUUID().toString());
                data.setDayPlanId(summary.getDayPlanId());
                data.setTrainSetId(summary.getTrainsetId());
                if (train != null) {
                    data.setTrainSetName(train.getTrainsetname());
                }
                data.setCheckListSummaryId(summary.getChecklistSummaryId());
                data.setCarNo(carNo);
                data.setAxleLocation(axlePosition);
                data.setPartLocation(partLocation);
                data.setCreateTime(new Date());
                ds.add(data);
                cs.add(data);
            }
        }
        return cs;
    }

    /**
     * 获取整理要的数据实体
     * @param summary 记录单信息
     * @param ds 数据实体集合
     * @param carNo 辆序
     * @param axlePosition 轴位
     * @param partLocation 端位
     * @return 数据实体
     */
    private AxleWheelDataCur getAxleWheelData(ChecklistSummary summary, List<AxleWheelDataCur> ds, String carNo, String axlePosition, String partLocation) {
        AxleWheelDataCur data = null;
        for(AxleWheelDataCur d : ds){
            if(d.getCarNo().equals(carNo) && d.getAxleLocation().equals(axlePosition) && d.getPartLocation().equals(partLocation)){
                data = d;
            }
        }
        if (data == null) {
            data = new AxleWheelDataCur();
            data.setId(UUID.randomUUID().toString());
            data.setDayPlanId(summary.getDayPlanId());
            data.setTrainSetId(summary.getTrainsetId());
            //获取车组ID和车组辆序
            TrainsetBaseInfo train = billCommon.getTrainsetInfo(summary.getTrainsetId());
            if (train != null) {
                data.setTrainSetName(train.getTrainsetname());
            }
            data.setCarNo(carNo);
            data.setAxleLocation(axlePosition);
            data.setPartLocation(partLocation);
            ds.add(data);
        }
        return data;
    }


    /**
     * 获取指定数据值
     * @param lstLinkContents 关联主键
     * @param cells 取值范围单元格
     * @return 单元格值
     */
    private String getCellValue(List<ChkDetailLinkContentForModule> lstLinkContents, List<ChecklistDetailInfoForSave> cells){
        String value = "";
        for(ChecklistDetailInfoForSave cell : cells){
            for(ChkDetailLinkContentForModule link : lstLinkContents){
                if(cell.getId().equals(link.getLinkCellId())){
                    value = cell.getValue();
                    break;
                }
            }
            if(!StringUtils.isEmpty(value)){
                break;
            }
        }
        return value;
    }

    /**
     * 获取端位 //1轴：1位侧=1端，2位侧=2端；2轴：1位侧=3端，2位侧=4端；3轴：1位侧=5端，2位侧=6端；4轴：1位侧=7端，2位侧=8端
     * @param axleLocation 轴位
     * @param isOne true：1位侧；false：2位侧
     * @return 端位
     */
    private String getPartLocation(String axleLocation, boolean isOne){
        String partLocation = "";
        switch (axleLocation){
            case "1":
                partLocation = isOne ? "1":"2";
                break;
            case "2":
                partLocation = isOne ? "3":"4";
                break;
            case "3":
                partLocation = isOne ? "5":"6";
                break;
            case "4":
                partLocation = isOne ? "7":"8";
                break;
        }
        return partLocation;
    }

    /**
     * 根据轴位获取端位
     * @param axleLocation 轴位
     * @return
     */
    private List<String> getPartLocation(String axleLocation){
        List<String> partLocations = new ArrayList<String>();
        switch (axleLocation){
            case "1":
                partLocations.add("1");
                partLocations.add("2");
                break;
            case "2":
                partLocations.add("3");
                partLocations.add("4");
                break;
            case "3":
                partLocations.add("5");
                partLocations.add("6");
                break;
            case "4":
                partLocations.add("7");
                partLocations.add("8");
                break;
        }
        return partLocations;
    }

    /**
     *  根据属性编码和单元格数据获取数据实体
     * @param contentList 数据实体集合
     * @param attr 属性编码
     * @return 实体
     */
    private List<ChecklistDetailInfoForSave> getDetailByAttr(List<ChecklistDetailInfoForSave> contentList, AttributeEnum attr) {
        List<ChecklistDetailInfoForSave> ds = new ArrayList<ChecklistDetailInfoForSave>();
        for (int i = 0; i < contentList.size(); i++) {
            ChecklistDetailInfoForSave t = contentList.get(i);
            if (t.getAttributeCode().equals(attr.getValue())) {
                ds.add(t);
            }
        }
        return ds;
    }

    /**
     * 指定字符串为null或空字符串
     * @param value 指定字符串
     * @return true：为null或空字符串； false：不为null，也不为空字符串
     */
    private boolean IsNullOrEmpty(String value){
        return value == null || value == "";
    }

}
