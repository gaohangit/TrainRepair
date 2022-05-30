package com.ydzbinfo.emis.trainRepair.bill.fillback.service.impl.OneTwoRepairCheckInfoInitPackage;

import com.ydzbinfo.emis.trainRepair.bill.fillback.service.impl.AxleWheelDataServiceImpl;
import com.ydzbinfo.emis.trainRepair.bill.fillback.utils.AttributeEnum;
import com.ydzbinfo.emis.trainRepair.bill.fillback.utils.BillCommon;
import com.ydzbinfo.emis.trainRepair.bill.general.UrlInfo;
import com.ydzbinfo.emis.trainRepair.bill.general.constant.BillCellTriggerTimingEnum;
import com.ydzbinfo.emis.trainRepair.bill.general.constant.BillCellTriggerUrlTypeEnum;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.*;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChecklistSummary;
import com.ydzbinfo.emis.trainRepair.remotemodel.device.EntityUlatheDevice;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.AxleWheel;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.AxleWheelDiameterEntity;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.StateConfigInfo;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetBaseInfo;
import com.ydzbinfo.emis.utils.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class WheelTask  extends XxtsTaskBase {

    protected static final Logger logger = getLogger(WheelTask.class);
    @Autowired
    private BillCommon billCommon;

    @Autowired
    private UlatheState ulatheState;

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
        String sDate = getDateNow(0);
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
        //将计划未下发的车轴的1位和2位的镟前、镟后轮径值置为N/A
        //获取所有的确认值
        StateConfigInfo stateConfigInfo = ulatheState.getStateConfigInfo(train.getTrainsetid(), new ArrayList<AxleWheelDiameterEntity>());
        result.addAll(ulatheState.setStateConfigInfoForShow(contentList, stateConfigInfo));
        //获取通用属性
        result.addAll(initCommonAttribute(summary, contentList));
        return result;
    }
    /**
     * 初始化规则
     */
    public void initContentRule(ChecklistSummaryInfoForShow content) {
        super.initContentRule(content);
        ChecklistDetailInfoForShow excCell = null;
        try{
            List<ChecklistDetailInfoForShow> cells = content.getCells();
            for(ChecklistDetailInfoForShow cell : cells){
                excCell = cell;
                if(cell.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_ONEXH.getValue()) || cell.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_TWOXH.getValue())){
                    if(cell.getReadOnly() != null && cell.getReadOnly()) //如果是只读，说明需要显示N/A，计划未下发，无需添加单元格更改方法
                        continue;
                    UrlInfo triggerUrlInfo = new UrlInfo();
                    triggerUrlInfo.setUrlType(BillCellTriggerUrlTypeEnum.FUNCTION);
                    triggerUrlInfo.setUrl(billCommon.addApplicationPrefix("/checkInfo/changeStateCells"));
                    cell.setTriggerUrlInfo(triggerUrlInfo);
                    cell.setTriggerTiming(BillCellTriggerTimingEnum.AFTER_CHANGE);
                }
                if(cell.getAttributeCode().equals(AttributeEnum.ATTR_CONFIRM_PERSON.getValue())){
                    UrlInfo triggerUrlInfo = new UrlInfo();
                    triggerUrlInfo.setUrlType(BillCellTriggerUrlTypeEnum.FUNCTION);
                    triggerUrlInfo.setUrl(billCommon.addApplicationPrefix("/checkInfo/confirmPerson"));//签字
                    cell.setTriggerUrlInfo(triggerUrlInfo);
                    cell.setTriggerTiming(BillCellTriggerTimingEnum.AFTER_CHANGE);
                }
                if(cell.getAttributeCode().equals(AttributeEnum.ATTR_QUALITY_AXLESIGN.getValue())){
                    UrlInfo triggerUrlInfo = new UrlInfo();
                    triggerUrlInfo.setUrlType(BillCellTriggerUrlTypeEnum.FUNCTION);
                    triggerUrlInfo.setUrl(billCommon.addApplicationPrefix("/checkInfo/setAxleQualitySign"));//质检签字
                    cell.setTriggerUrlInfo(triggerUrlInfo);
                    cell.setTriggerTiming(BillCellTriggerTimingEnum.AFTER_CHANGE);
                }
            }
        }catch (Exception ex){
            logger.error(String.format("镟修记录单初始化规则时引发异常:%s",excCell), ex);
        }
    }

    /**
     *  导入数据
     * @param checklistSummary 记录单实体
     * @return 修改的单元格集合
     */
    public ChecklistTriggerUrlCallResult ImportData(ChecklistSummaryInfoForShow checklistSummary) {
        ChecklistTriggerUrlCallResult result = new ChecklistTriggerUrlCallResult();
        boolean isImport = false;
        String dayPlanId = "";
        String summaryId = "";
        String trainSetName = "";
        String itemName = "";
        try {
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
            //获取开始和结束日期
            String sStartDate = getDateNow(-3);    //开始日期
            String sEndDate = getDateNow(3);       //结束日期
            List<EntityUlatheDevice> entityUlatheDevices = getWheelGetDeviceRecord(train.getTrainsetname(), sStartDate, sEndDate);
            if (entityUlatheDevices != null && entityUlatheDevices.size() > 0) {
                for (int i = 0; i < entityUlatheDevices.size(); i++) {
                    EntityUlatheDevice entity = entityUlatheDevices.get(i);
                    String wheelset_id = entity.getS_WHEELSET_ID();
                    wheelset_id = wheelset_id.replace("---", "-");
                    wheelset_id = wheelset_id.replace("--", "-");
                    String[] array = wheelset_id.split("-");
                    if (array.length != 4) {
                        continue;
                    }
                    String carNo = array[2];
                    String axle = array[3].replace("0", "");
                    boolean isAllowFill = true;
                    for(CarAxleEntity carAxle : lstCarAxleEntitys){
                        if(carAxle.getCarNo().equals(carNo) && carAxle.getAxleNo().equals(axle)){
                            isAllowFill = carAxle.getAllowFill();
                            break;
                        }
                    }
                    if(!isAllowFill)
                        continue;
                    ChecklistDetailInfoForShow oneXQdetail = getDetail(summary.getTrainsetId(), lstDetails, carNo, axle, AttributeEnum.ATTR_WHEEL_ONEXQ, entity.getI_PRE_WHEEL_DL());         //1位镟前值
                    ChecklistDetailInfoForShow oneXHdetail = getDetail(summary.getTrainsetId(), lstDetails, carNo, axle, AttributeEnum.ATTR_WHEEL_ONEXH, entity.getI_LT_WHEEL_DL());          //1位镟后值
                    ChecklistDetailInfoForShow oneXQLHdetail = getDetail(summary.getTrainsetId(), lstDetails, carNo, axle, AttributeEnum.ATTR_WHEEL_ONELYHDXQ, entity.getI_PRE_SDL());        //1位镟前轮缘厚度
                    ChecklistDetailInfoForShow oneXHLHdetail = getDetail(summary.getTrainsetId(), lstDetails, carNo, axle, AttributeEnum.ATTR_WHEEL_ONELYHDXH, entity.getI_LT_SDL());         //1位镟后轮缘厚度
                    ChecklistDetailInfoForShow oneXQLGdetail = getDetail(summary.getTrainsetId(), lstDetails, carNo, axle, AttributeEnum.ATTR_WHEEL_ONELYGDXQ, entity.getI_PRE_SHL());        //1位镟后轮缘高度
                    ChecklistDetailInfoForShow oneXHLGdetail = getDetail(summary.getTrainsetId(), lstDetails, carNo, axle, AttributeEnum.ATTR_WHEEL_ONELYGDXH, entity.getI_LT_SHL());         //1位镟后轮缘高度
                    ChecklistDetailInfoForShow oneXQQRdetail = getDetail(summary.getTrainsetId(), lstDetails, carNo, axle, AttributeEnum.ATTR_WHEEL_ONEQRXQ, entity.getI_PRE_QRL());          //1位镟前QR值
                    ChecklistDetailInfoForShow oneXHQRdetail = getDetail(summary.getTrainsetId(), lstDetails, carNo, axle, AttributeEnum.ATTR_WHEEL_ONEQRXH, entity.getI_LT_QRL());           //1位镟后QR值
                    if (oneXQdetail != null) {
                        changeCells.add(oneXQdetail);
                    }
                    if (oneXHdetail != null) {
                        changeCells.add(oneXHdetail);
                    }
                    if (oneXQLHdetail != null) {
                        changeCells.add(oneXQLHdetail);
                    }
                    if (oneXHLHdetail != null) {
                        changeCells.add(oneXHLHdetail);
                    }
                    if (oneXQLGdetail != null) {
                        changeCells.add(oneXQLGdetail);
                    }
                    if (oneXHLGdetail != null) {
                        changeCells.add(oneXHLGdetail);
                    }
                    if (oneXQQRdetail != null) {
                        changeCells.add(oneXQQRdetail);
                    }
                    if (oneXHQRdetail != null) {
                        changeCells.add(oneXHQRdetail);
                    }

                    ChecklistDetailInfoForShow twoXQdetail = getDetail(summary.getTrainsetId(), lstDetails, carNo, axle, AttributeEnum.ATTR_WHEEL_TWOXQ, entity.getI_PRE_WHEEL_DR());         //2位镟前值
                    ChecklistDetailInfoForShow twoXHdetail = getDetail(summary.getTrainsetId(), lstDetails, carNo, axle, AttributeEnum.ATTR_WHEEL_TWOXH, entity.getI_LT_WHEEL_DR());          //2位镟后值
                    ChecklistDetailInfoForShow twoXQLHdetail = getDetail(summary.getTrainsetId(), lstDetails, carNo, axle, AttributeEnum.ATTR_WHEEL_TWOLYHDXQ, entity.getI_PRE_SDR());        //2位镟前轮缘厚度
                    ChecklistDetailInfoForShow twoXHLHdetail = getDetail(summary.getTrainsetId(), lstDetails, carNo, axle, AttributeEnum.ATTR_WHEEL_TWOLYHDXH, entity.getI_LT_SDR());         //2位镟后轮缘厚度
                    ChecklistDetailInfoForShow twoXQLGdetail = getDetail(summary.getTrainsetId(), lstDetails, carNo, axle, AttributeEnum.ATTR_WHEEL_TWOLYGDXQ, entity.getI_PRE_SHR());        //2位镟后轮缘高度
                    ChecklistDetailInfoForShow twoXHLGdetail = getDetail(summary.getTrainsetId(), lstDetails, carNo, axle, AttributeEnum.ATTR_WHEEL_TWOLYGDXH, entity.getI_LT_SHR());         //2位镟后轮缘高度
                    ChecklistDetailInfoForShow twoXQQRdetail = getDetail(summary.getTrainsetId(), lstDetails, carNo, axle, AttributeEnum.ATTR_WHEEL_TWOQRXQ, entity.getI_PRE_QRR());          //2位镟前QR值
                    ChecklistDetailInfoForShow twoXHQRdetail = getDetail(summary.getTrainsetId(), lstDetails, carNo, axle, AttributeEnum.ATTR_WHEEL_TWOQRXH, entity.getI_LT_QRR());           //2位镟后QR值
                    if (twoXQdetail != null) {
                        changeCells.add(twoXQdetail);
                    }
                    if (twoXHdetail != null) {
                        changeCells.add(twoXHdetail);
                    }
                    if (twoXQLHdetail != null) {
                        changeCells.add(twoXQLHdetail);
                    }
                    if (twoXHLHdetail != null) {
                        changeCells.add(twoXHLHdetail);
                    }
                    if (twoXQLGdetail != null) {
                        changeCells.add(twoXQLGdetail);
                    }
                    if (twoXHLGdetail != null) {
                        changeCells.add(twoXHLGdetail);
                    }
                    if (twoXQQRdetail != null) {
                        changeCells.add(twoXQQRdetail);
                    }
                    if (twoXHQRdetail != null) {
                        changeCells.add(twoXHQRdetail);
                    }
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
            axleWheelDataService.updateUlatheImportRecord(summary, entityUlatheDevices);
        }
        catch (Exception e){
            isImport = false;
            logger.error("导入镟修探伤数据时失败", e);
        }
        axleWheelDataService.updateAxleEntity(dayPlanId, summaryId, trainSetName, itemName, "5", isImport);
        return result;
    }

    /**
     * 更改确认值
     * @param checklistSummary 镟修记录单
     * @return
     */
    public ChecklistTriggerUrlCallResult changeStateCells(ChecklistSummaryInfoForSave checklistSummary) {
        ChecklistTriggerUrlCallResult result = new ChecklistTriggerUrlCallResult();
        try {
            ChecklistSummary summary = checklistSummary.getExtraObject();
            if (summary == null) {
                logger.error("获取确认值信息时，界面未提供记录单主表信息");
                throw new Exception("获取确认值信息时，界面未提供记录单主表信息");
            }
            List<ChecklistDetailInfoForSave> cells = checklistSummary.getCells();
            if (cells == null || cells.size() == 0) {
                logger.error("获取确认值信息时，界面未提供记录单详细信息");
                throw new Exception("获取确认值信息时，界面未提供记录单详细信息");
            }
            if (!isAllBackFill(cells)) {
                return result;
            } else {

            }
            ChecklistDetailInfoForSave confirmDetail = getFirstSingleDetailByAttrForSave(cells, AttributeEnum.ATTR_CONFIRM_PERSON);
            if (confirmDetail == null || StringUtils.isEmpty(confirmDetail.getValue())) {
                TrainsetBaseInfo train = billCommon.getTrainsetInfo(summary.getTrainsetId());
                List<AxleWheelDiameterEntity> lstWheelDatas = ulatheState.convertLathingData(train, cells);
                if (lstWheelDatas.size() == 0) {
                    logger.error("获取确认值信息时，未能成功将单据的单元格转换为镟修轮径值信息");
                    throw new Exception("获取确认值信息时，未能成功将单据的单元格转换为镟修轮径值信息");
                }
                StateConfigInfo stateConfigInfo = ulatheState.getStateConfigInfo(summary.getTrainsetId(), lstWheelDatas);
                List<ChecklistDetailInfoForShow> changeCells = ulatheState.setStateConfigInfoForSave(cells, stateConfigInfo);
                List<AttributeEnum> attrs = ulatheState.getAllStateConfigAttr();
                List<String> lstMsg = new ArrayList<String>();
                for(AttributeEnum attr : attrs){
                    ChecklistDetailInfoForShow show = getFirstSingleDetailByAttrForShow(changeCells, attr);
                    ChecklistDetailInfoForSave save = getFirstSingleDetailByAttrForSave(cells, attr);
                    if(show != null && save != null){
                        if(!show.getValue().equals(save.getValue())){
                            lstMsg.add(String.format("%s由%s更改为了%s", attr.getLabel(), save.getValue(), show.getValue()));
                        }
                    }
                }
                StringBuilder sb = new StringBuilder();
                if(lstMsg.size() > 0){
                    for(int i = 0; i < lstMsg.size(); i++){
                        sb.append(lstMsg.get(i));
                        if(i < lstMsg.size() - 1){
                            sb.append("\r\n");
                        }
                    }
                    sb.append("，如有错误请更正");
                }
                result.setChangedCells(changeCells);
                result.setOperationResultMessage(sb.toString());
            }
        } catch (Exception ex) {
            logger.error("/WheelTask/changeStateCells----更改确认值", ex);
        }
        return result;
    }

    /**
     * 保存镟修探伤的镟轮数据
     * @param checklistSummary 记录单
     * @return
     */
    public ChecklistTriggerUrlCallResult saveWheelDatas(ChecklistSummaryInfoForSave checklistSummary) {
        ChecklistTriggerUrlCallResult result = new ChecklistTriggerUrlCallResult();
        try {
            axleWheelDataService.addAxleWheelData(checklistSummary);
        } catch (Exception ex) {
            logger.error("/WheelTask/saveWheelDatas----保存镟修数据", ex);
        }
        return result;
    }

    /**
     * 判断镟后值是否完全回填
     * @param cells 镟修单据上所有的单元格
     * @return 镟后值是否完全回填
     */
    private boolean isAllBackFill(List<ChecklistDetailInfoForSave> cells){
        boolean allBackFill = true;
        for(ChecklistDetailInfoForSave cell : cells){
            if(cell.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_ONEXH.getValue()) || cell.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_TWOXH.getValue())){
                if(StringUtils.isEmpty(cell.getValue())){
                    allBackFill = false;
                    break;
                }
            }
        }
        return  allBackFill;
    }
}
