package com.ydzbinfo.emis.trainRepair.bill.fillback.service.impl.OneTwoRepairCheckInfoInitPackage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ydzbinfo.emis.guns.config.RecheckTaskProperties;
import com.ydzbinfo.emis.trainRepair.bill.fillback.service.impl.CheckInfoInit;
import com.ydzbinfo.emis.trainRepair.bill.fillback.utils.AttributeEnum;
import com.ydzbinfo.emis.trainRepair.bill.fillback.utils.BillCommon;
import com.ydzbinfo.emis.trainRepair.bill.fillback.utils.MainCycEnum;
import com.ydzbinfo.emis.trainRepair.bill.general.UrlInfo;
import com.ydzbinfo.emis.trainRepair.bill.general.constant.BillCellTriggerTimingEnum;
import com.ydzbinfo.emis.trainRepair.bill.general.constant.BillCellTriggerUrlTypeEnum;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.ChecklistDetailInfoForSave;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.ChecklistDetailInfoForShow;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.ChecklistSummaryInfoForShow;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChecklistSummary;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChkDetailLinkContent;
import com.ydzbinfo.emis.trainRepair.remotemodel.device.EntityAxleDevice;
import com.ydzbinfo.emis.trainRepair.remotemodel.device.EntityLUDevice;
import com.ydzbinfo.emis.trainRepair.remotemodel.device.EntityUlatheDevice;
import com.ydzbinfo.emis.trainRepair.remotemodel.item.RepairItemInfo;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.AxleWheel;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.pojo.JsonRootBean;
import com.ydzbinfo.emis.utils.CacheUtil;
import com.ydzbinfo.emis.utils.HttpUtil;
import com.ydzbinfo.emis.utils.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * ?????????????????????LU????????????
 */
@Component
public class XxtsTaskBase  extends CheckInfoInit {

    protected static final Logger logger = getLogger(XxtsTaskBase.class);
    @Autowired
    private BillCommon billCommon;

    @Autowired
    private RecheckTaskProperties recheckTaskProperties;


    /**
     * ???????????????
     */
    public void initContentRule(ChecklistSummaryInfoForShow content) {
        super.initContentRule(content);
        List<ChecklistDetailInfoForShow> cells = content.getCells();
        for (ChecklistDetailInfoForShow cell : cells){
            if(cell.getAttributeCode().equals(AttributeEnum.ATTR_REPAIR_PERSON.getValue())||
                    cell.getAttributeCode().equals(AttributeEnum.ATTR_AXLE_TASKDOWN_A.getValue())||
                    cell.getAttributeCode().equals(AttributeEnum.ATTR_AXLE_TASKDOWN_B.getValue())||
                    cell.getAttributeCode().equals(AttributeEnum.ATTR_AXLE_TASKDOWN_E.getValue())||
                    cell.getAttributeCode().equals(AttributeEnum.ATTR_AXLE_INSTALL_A.getValue())||
                    cell.getAttributeCode().equals(AttributeEnum.ATTR_AXLE_INSTALL_B.getValue())||
                    cell.getAttributeCode().equals(AttributeEnum.ATTR_AXLE_INSTALL_E.getValue())||
                    cell.getAttributeCode().equals(AttributeEnum.ATTR_AXLE_PUTINTO.getValue())||
                    cell.getAttributeCode().equals(AttributeEnum.ATTR_QUALITY_AXLESIGN.getValue())){
                UrlInfo triggerUrlInfo = new UrlInfo();
                triggerUrlInfo.setUrlType(BillCellTriggerUrlTypeEnum.FUNCTION);
                triggerUrlInfo.setUrl(billCommon.addApplicationPrefix("/checkInfo/setRepairPerson"));
                cell.setTriggerUrlInfo(triggerUrlInfo);
                cell.setTriggerTiming(BillCellTriggerTimingEnum.AFTER_CHANGE);
            }
        }
        content.setShowImportButton(true);
    }
    /**
     * ???????????????????????????
     * @return
     */
    private List<String> getCommonAttribute(){
        List<String> attrs = new ArrayList<String>();
        attrs.add(AttributeEnum.ATTR_TRAINSETID.getValue());
        attrs.add(AttributeEnum.ATTR_ALL_DEPT.getValue());
        attrs.add(AttributeEnum.ATTR_REPAIR_DATE.getValue());
        attrs.add(AttributeEnum.ATTR_REPAIR_DEPT.getValue());
        attrs.add(AttributeEnum.ATTR_REPAIR_ACCUMILE.getValue());
        attrs.add(AttributeEnum.ATTR_REPAIR_CARD.getValue());
        attrs.add(AttributeEnum.ATTR_REPAIR_CYCLE.getValue());
        attrs.add(AttributeEnum.ATTR_REPAIR_MAINCYC.getValue());
        attrs.add(AttributeEnum.ATTR_REPAIR_PERSON_SHOW.getValue());
        attrs.add(AttributeEnum.ATTR_QUALITY_CONTROL.getValue());
        attrs.add(AttributeEnum.ATTR_REPAIR_SYSTEM.getValue());
        attrs.add(AttributeEnum.ATTR_REPAIR_PART.getValue());
        return attrs;
    }

    /**
     * ???????????????????????????
     */
    public List<ChecklistDetailInfoForShow> initCommonAttribute(ChecklistSummary summary, List<ChecklistDetailInfoForShow> cells){
        List<ChecklistDetailInfoForShow> result = new ArrayList<ChecklistDetailInfoForShow>();
        RepairItemInfo repairItem = billCommon.getItemInfoByCode(summary.getItemCode(), summary.getUnitCode());
        List<String> attrs = getCommonAttribute();
        for(ChecklistDetailInfoForShow cell: cells){
            String attributeCode = cell.getAttributeCode();
            if(attributeCode.equals(AttributeEnum.ATTR_REPAIR_MAINCYC.getValue())){    //??????
                for(MainCycEnum main : MainCycEnum.values()){
                    if(summary.getMainCyc().equals(main.getMainCyc().getTaskRepairCode())){
                        cell.setValue(main.getMainCyc().getTaskRepairName());
                        break;
                    }
                }
            }
            if(attributeCode.equals(AttributeEnum.ATTR_REPAIR_CARD.getValue()) && repairItem != null){       //???????????????
                cell.setValue(repairItem.getMainTainCardNo());
            }
            if(attributeCode.equals(AttributeEnum.ATTR_REPAIR_CYCLE.getValue()) && repairItem != null){      //????????????
               cell.setValue(billCommon.getItemCycle(repairItem.getItemCycleVos()));
            }
            if(attributeCode.equals(AttributeEnum.ATTR_REPAIR_SYSTEM.getValue()) && repairItem != null){     //????????????
                cell.setValue(repairItem.getFuncSysName());
            }
            if(attributeCode.equals(AttributeEnum.ATTR_REPAIR_PART.getValue()) && repairItem != null){       //??????
                cell.setValue(repairItem.getStrPartsTypeName());
            }
            if(attributeCode.equals(AttributeEnum.ATTR_QUALITY_CONTROL.getValue()) && repairItem != null){    //??????????????????
                //TODO ?????????????????????????????????????????????????????????????????????????????????
                cell.setValue("");
            }
            if(attrs.contains(attributeCode) || cell.getValue().equals("N/A")){
                result.add(cell);
            }
        }
        return result;
    }


    /**
     *  ??????????????????????????????????????????????????????
     * @param trainsetId ??????ID
     * @param contentList ??????????????????
     * @param attr ????????????
     * @param cellValue ???????????????
     * @return ??????
     */
    public List<ChecklistDetailInfoForShow> getDetailByAttrForShow(String trainsetId, List<ChecklistDetailInfoForShow> contentList, AttributeEnum attr, String cellValue) {
        return CacheUtil.getDataUseThreadCache("XxtsTaskBase.getDetailByAttrForShow_" + trainsetId + "_" + attr.getValue() + "_" + cellValue, ()->{
            List<ChecklistDetailInfoForShow> ds = new ArrayList<ChecklistDetailInfoForShow>();
            for (int i = 0; i < contentList.size(); i++) {
                ChecklistDetailInfoForShow t = contentList.get(i);
                if (t.getAttributeCode().equals(attr.getValue())) {
                    if (cellValue == null || cellValue == "") {
                        ds.add(t);
                    } else if (t.getValue().equals(cellValue)) {
                        ds.add(t);
                    }
                }
            }
            return ds;
        });
    }

    /**
     *  ??????????????????????????????????????????????????????
     * @param contentList ??????????????????
     * @param attr ????????????
     * @return ??????
     */
    public List<ChecklistDetailInfoForShow> getDetailByAttrForShow(List<ChecklistDetailInfoForShow> contentList, AttributeEnum attr) {
        List<ChecklistDetailInfoForShow> ds = new ArrayList<ChecklistDetailInfoForShow>();
        for (int i = 0; i < contentList.size(); i++) {
            ChecklistDetailInfoForShow t = contentList.get(i);
            if (t.getAttributeCode().equals(attr.getValue())) {
                ds.add(t);
            }
        }
        return ds;
    }

    /**
     *  ??????????????????????????????????????????????????????
     * @param contentList ??????????????????
     * @param attr ????????????
     * @return ??????
     */
    public List<ChecklistDetailInfoForSave> getDetailByAttrForSave(List<ChecklistDetailInfoForSave> contentList, AttributeEnum attr) {
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
     *  ???????????????????????????????????????
     * @param cells ??????????????????
     * @param attr ????????????
     * @return ??????
     */
    public ChecklistDetailInfoForSave getFirstSingleDetailByAttrForSave(List<ChecklistDetailInfoForSave> cells, AttributeEnum attr){
        ChecklistDetailInfoForSave detail = null;
        for(ChecklistDetailInfoForSave d : cells){
            if(d.getAttributeCode().equals(attr.getValue())){
                detail = d;
                break;
            }
        }
        return detail;
    }

    /**
     *  ???????????????????????????????????????
     * @param cells ??????????????????
     * @param attr ????????????
     * @return ??????
     */
    public ChecklistDetailInfoForShow getFirstSingleDetailByAttrForShow(List<ChecklistDetailInfoForShow> cells, AttributeEnum attr){
        ChecklistDetailInfoForShow detail = null;
        for(ChecklistDetailInfoForShow d : cells){
            if(d.getAttributeCode().equals(attr.getValue())){
                detail = d;
                break;
            }
        }
        return detail;
    }

    /**
     * ???????????????????????????????????????????????????????????????
     * @param trainSetId ??????ID
     * @param lstDetails ?????????
     * @param carNo ??????
     * @param axle ??????
     * @param attr ??????
     * @param value ????????????
     * @return ??????????????????
     */
    public ChecklistDetailInfoForShow getDetail(String trainSetId, List<ChecklistDetailInfoForShow> lstDetails, String carNo, String axle, AttributeEnum attr, String value){
        ChecklistDetailInfoForShow detail = null;
        try
        {
            List<ChecklistDetailInfoForShow> lstDetailAttr = getDetailByAttrForShow(lstDetails, attr);
            List<ChecklistDetailInfoForShow> lstCarNos = getDetailByAttrForShow(trainSetId, lstDetails,AttributeEnum.ATTR_CAR, carNo);
            List<ChecklistDetailInfoForShow> lstAxles = getDetailByAttrForShow(trainSetId, lstDetails,AttributeEnum.ATTR_AXLE_POSTION, axle);
            for(int i=0;i < lstDetailAttr.size();i++){
                ChecklistDetailInfoForShow d = lstDetailAttr.get(i);
                boolean isCarNo = false;
                boolean isAxle = false;
                for (int j= 0;j < d.getLinkContents().size();j++){
                    if(!isCarNo){
                        ChecklistDetailInfoForShow carDetail = findDetailByContentIdForShow(lstCarNos, d.getLinkContents().get(j).getLinkContentId());
                        isCarNo = carDetail != null;
                    }
                    if (!isAxle){
                        ChecklistDetailInfoForShow axleDetail = findDetailByContentIdForShow(lstAxles, d.getLinkContents().get(j).getLinkContentId());
                        isAxle = axleDetail != null;
                    }
                }
                if(isCarNo && isAxle){
                    detail = d;
                    break;
                }
            }
            if(detail != null && (StringUtils.isEmpty(detail.getValue()) || (!StringUtils.isEmpty(detail.getValue()) && detail.getValue().equals("N/A")))){
                detail.setValue(value);
            }
            if(detail != null){
                ChecklistDetailInfoForShow detailForShow = new ChecklistDetailInfoForShow();
                BeanUtils.copyProperties(detail, detailForShow);
                return detailForShow;
            }
            return null;
        }
        catch (Exception e){
            logger.error(e.toString());
        }

        return  detail;
    }

    /**
     * ???????????????????????????????????????????????????????????????
     * @param trainSetId ??????ID
     * @param lstDetails ?????????
     * @param carNo ??????
     * @param axle ??????
     * @param attr ??????
     * @param value ????????????
     * @return ??????????????????
     */
    public List<ChecklistDetailInfoForShow> getDetailArray(String trainSetId, List<ChecklistDetailInfoForShow> lstDetails, String carNo, String axle, AttributeEnum attr, String value){
        List<ChecklistDetailInfoForShow> ds = new ArrayList<ChecklistDetailInfoForShow>();
        try
        {
            List<ChecklistDetailInfoForShow> lstDetailAttr = getDetailByAttrForShow(lstDetails, attr);
            List<ChecklistDetailInfoForShow> lstCarNos = getDetailByAttrForShow(trainSetId, lstDetails,AttributeEnum.ATTR_CAR, carNo);
            List<ChecklistDetailInfoForShow> lstAxles = getDetailByAttrForShow(trainSetId, lstDetails,AttributeEnum.ATTR_AXLE_POSTION, axle);
            for(int i=0;i < lstDetailAttr.size();i++){
                ChecklistDetailInfoForShow d = lstDetailAttr.get(i);
                boolean isCarNo = false;
                boolean isAxle = false;
                for (int j= 0;j < d.getLinkContents().size();j++){
                    if(!isCarNo){
                        ChecklistDetailInfoForShow carDetail = findDetailByContentIdForShow(lstCarNos, d.getLinkContents().get(j).getLinkContentId());
                        isCarNo = carDetail != null;
                    }
                    if (!isAxle){
                        ChecklistDetailInfoForShow axleDetail = findDetailByContentIdForShow(lstAxles, d.getLinkContents().get(j).getLinkContentId());
                        isAxle = axleDetail != null;
                    }
                }
                if(isCarNo && isAxle){
                    if(d != null && (StringUtils.isEmpty(d.getValue()) || (!StringUtils.isEmpty(d.getValue()) && d.getValue().equals("N/A")))){
                        d.setValue(value);
                    }
                    ChecklistDetailInfoForShow detailForShow = new ChecklistDetailInfoForShow();
                    BeanUtils.copyProperties(d, detailForShow);
                    ds.add(detailForShow);
                }
            }
        }
        catch (Exception e){
            logger.error(e.toString());
        }
        return  ds;
    }

    /**
     * ??????????????????
     * @param lstAxleWheels ??????????????????
     * @param trainsetId ??????ID
     * @param carNo ?????? (01)
     * @param date ?????????yyyyMMdd???
     * @param axlePosition ??????
     * @param firstFind ????????????????????????????????????
     * @return ??????????????????
     */
    public AxleWheel getAxleWheel(List<AxleWheel> lstAxleWheels, String trainsetId, String carNo, String date, String axlePosition, boolean firstFind) {
        AxleWheel axleWheel = null;
        for (int i=0 ;i < lstAxleWheels.size();i++)
        {
            if(lstAxleWheels.get(i).getCarNo().equals(carNo) && lstAxleWheels.get(i).getLocatetionNum().equals(axlePosition)){
                axleWheel = lstAxleWheels.get(i);
                break;
            }
        }
        if( axleWheel == null && firstFind){
            List<AxleWheel> ws = billCommon.getPartByTrainSetId(trainsetId, carNo, date);
            lstAxleWheels.addAll(ws);
            axleWheel = getAxleWheel(lstAxleWheels,trainsetId, carNo, date, axlePosition, false);
        }
        return axleWheel;
    }

    /**
     * ???????????????????????????????????????
     * @param lstDetails ??????????????????
     * @param contentId ????????????
     * @return ????????????
     */
    public ChecklistDetailInfoForShow findDetailByContentIdForShow(List<ChecklistDetailInfoForShow> lstDetails, String contentId){
        ChecklistDetailInfoForShow detail = null;
        for (int i=0; i< lstDetails.size(); i++){
            for (int j= 0;j < lstDetails.get(i).getLinkContents().size();j++){
                ChkDetailLinkContent content = lstDetails.get(i).getLinkContents().get(j);
                if (content.getContentId().equals(contentId)){
                    detail = lstDetails.get(i);
                    break;
                }
            }
        }
        return  detail;
    }

    /**
     * ???????????????????????????????????????
     * @param lstDetails ??????????????????
     * @param contentId ????????????
     * @return ????????????
     */
    public ChecklistDetailInfoForSave findDetailByContentIdForSave(List<ChecklistDetailInfoForSave> lstDetails, AttributeEnum attr, String contentId){
        ChecklistDetailInfoForSave detail = null;
        for(ChecklistDetailInfoForSave d : lstDetails) {
            if(d.getId().equals(contentId)){
                detail = d;
                break;
            }
        }
        return  detail;
    }

    /**
     * ?????????????????????
     * @param trainsetId ??????ID
     * @param sDate ??????
     * @param lstDetail ???????????????????????????
     * @param attributeEnum ??????????????????
     * @param lstAxleWheels ????????????
     * @param lstCarNos ????????????
     * @param lstAxles ????????????
     * @return ????????????????????????
     */
    public List<ChecklistDetailInfoForShow> setViewCell(String trainsetId, String sDate, List<ChecklistDetailInfoForShow> lstDetail, AttributeEnum attributeEnum,
                                                         List<AxleWheel> lstAxleWheels, List<ChecklistDetailInfoForShow> lstCarNos, List<ChecklistDetailInfoForShow> lstAxles){
        List<ChecklistDetailInfoForShow> result = new ArrayList<ChecklistDetailInfoForShow>();
        //??????
        for(int i=0;i< lstDetail.size();i++){
            ChecklistDetailInfoForShow detail = lstDetail.get(i);
            //?????????????????????????????????
            if (!detail.getAttributeCode().equals(attributeEnum.getValue()))
                continue;
            ChecklistDetailInfoForShow carNoDetail = null;      //??????
            ChecklistDetailInfoForShow axleDetail = null;       //??????
            //???????????????????????????????????????????????????????????????
            for (int j = 0;j < detail.getLinkContents().size(); j++){
                ChkDetailLinkContent linkContent = detail.getLinkContents().get(j);
                //????????????
                ChecklistDetailInfoForShow findDetail = findDetailByContentIdForShow(lstCarNos,linkContent.getLinkContentId());
                if(findDetail!=null){
                    carNoDetail = findDetail;
                    continue;
                }
                //????????????
                findDetail = findDetailByContentIdForShow(lstAxles,linkContent.getLinkContentId());
                if(findDetail!=null){
                    axleDetail = findDetail;
                    continue;
                }
                if(carNoDetail != null && axleDetail != null){
                    break;
                }
            }
            //???????????????????????????????????????
            if(carNoDetail!=null && axleDetail!=null){
                String carNo = carNoDetail.getValue();
                String axlePosition = axleDetail.getValue();
                //?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                AxleWheel axleWheel = getAxleWheel(lstAxleWheels,trainsetId, carNo, sDate, axlePosition,true);

                //????????????
                if(attributeEnum.getValue().equals(AttributeEnum.ATTR_AXLE_NUMBER.getValue())){
                    detail.setValue(axleWheel.getSerialNum());
                    result.add(detail);
                }
                //???????????????
                if(attributeEnum.getValue().equals(AttributeEnum.ATTR_AXLE_ACCUMILE.getValue())){
                    detail.setValue(axleWheel.getRunmiles());
                    result.add(detail);
                }
                //???????????????
                if(attributeEnum.getValue().equals(AttributeEnum.ATTR_AXLE_ATTRIBUTE.getValue())){
                    detail.setValue(axleWheel.getDtType());
                    result.add(detail);
                }
                //??????????????????
                if(attributeEnum.getValue().equals(AttributeEnum.ATTR_WHEEL_ONENUMBER.getValue())){
                    detail.setValue(axleWheel.getZSerialNum());
                    result.add(detail);
                }
                //??????????????????
                if(attributeEnum.getValue().equals(AttributeEnum.ATTR_WHEEL_TWONUMBER.getValue())){
                    detail.setValue(axleWheel.getYSerialnum());
                    result.add(detail);
                }
                //?????????????????????
                if(attributeEnum.getValue().equals(AttributeEnum.ATTR_WHEEL_ONEACCUMILE.getValue())){
                    detail.setValue(axleWheel.getZRunmiles());
                    result.add(detail);
                }
                //?????????????????????
                if(attributeEnum.getValue().equals(AttributeEnum.ATTR_WHEEL_TWOACCUMILE.getValue())){
                    detail.setValue(axleWheel.getYrunmiles());
                    result.add(detail);
                }
            }
        }
        return result;
    }

    /**
     * ??????????????????
     * @param days ??????????????????
     * @return ???????????? ???yyyyMMdd???
     */
    public String getDateNow(int days){
        String s_date = "";
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if(days != 0){
            Calendar calendarDate = Calendar.getInstance();
            calendarDate.setTime(date);
            calendarDate.add(Calendar.DAY_OF_YEAR, days);
            Date date1 = calendarDate.getTime();
            s_date = dateFormat.format(date1);
        }else {
            s_date = dateFormat.format(date);
        }
        return s_date;
    }

    /**
     * ?????????????????????????????????????????????????????????
     *
     * @param trainsetName ????????????
     * @param startDate    ????????????
     * @param endDate      ????????????
     * @return ????????????
     */
    public List<EntityUlatheDevice> getWheelGetDeviceRecord(String trainsetName, String startDate, String endDate) {
        List<EntityUlatheDevice> lstEntityUlatheDevices = new ArrayList<EntityUlatheDevice>();
        try {
            Map requestMap = new HashMap();
            requestMap.put("TrainsetName", trainsetName);
            requestMap.put("StartDate", startDate);
            requestMap.put("EndDate", endDate);
            JsonRootBean jsonRootBean = HttpUtil.getToken(recheckTaskProperties);
            JSONObject wheelJson = HttpUtil.doPost(recheckTaskProperties.getWheelGetDeviceRecordUrl(), requestMap, recheckTaskProperties, jsonRootBean);
            List<EntityUlatheDevice> entityUlatheDevices = JSON.parseArray(wheelJson.getString("Data"), EntityUlatheDevice.class);
            //???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????QR??????????????????????????????
            for(int i=0;i < entityUlatheDevices.size();i++){
                //??????????????????
                List<EntityUlatheDevice> es = GetRepeatDatas(entityUlatheDevices, entityUlatheDevices.get(i).getS_WHEELSET_ID());
                //??????????????????????????????????????????????????????????????????
                if(es.size() == 1){
                    lstEntityUlatheDevices.add(es.get(0));
                }else if(es.size()> 1){
                    //???????????????????????????????????????????????????????????????????????????
                    EntityUlatheDevice entityUlatheDevice = es.get(0);
                    entityUlatheDevice.setI_PRE_WHEEL_DL(GetValue(es, "I_PRE_WHEEL_DL",true));  //???????????????(???)
                    entityUlatheDevice.setI_LT_WHEEL_DL(GetValue(es, "I_LT_WHEEL_DL",false));   //???????????????(???)
                    entityUlatheDevice.setI_PRE_WHEEL_DR(GetValue(es, "I_PRE_WHEEL_DR",true));  //???????????????(???)
                    entityUlatheDevice.setI_LT_WHEEL_DR(GetValue(es, "I_LT_WHEEL_DR",false));   //???????????????(???)
                    entityUlatheDevice.setI_PRE_SDL(GetValue(es, "I_PRE_SDL")); //1????????????????????????
                    entityUlatheDevice.setI_LT_SDL(GetValue(es, "I_LT_SDL"));   //1????????????????????????
                    entityUlatheDevice.setI_PRE_SDR(GetValue(es, "I_PRE_SDR")); //2????????????????????????
                    entityUlatheDevice.setI_LT_SDR(GetValue(es, "I_LT_SDR"));   //2????????????????????????
                    entityUlatheDevice.setI_PRE_SHL(GetValue(es, "I_PRE_SHL")); //1????????????????????????
                    entityUlatheDevice.setI_LT_SHL(GetValue(es, "I_LT_SHL"));   //1????????????????????????
                    entityUlatheDevice.setI_PRE_SHR(GetValue(es, "I_PRE_SHR")); //2????????????????????????
                    entityUlatheDevice.setI_LT_SHR(GetValue(es, "I_LT_SHR"));   //2????????????????????????
                    entityUlatheDevice.setI_PRE_QRL(GetValue(es, "I_PRE_QRL")); //1????????????QR???
                    entityUlatheDevice.setI_LT_QRL(GetValue(es, "I_LT_QRL"));   //1????????????QR???
                    entityUlatheDevice.setI_PRE_SDL(GetValue(es, "I_PRE_SDL")); //2????????????QR???
                    entityUlatheDevice.setI_LT_QRR(GetValue(es, "I_LT_QRR"));   //2????????????QR???
                    lstEntityUlatheDevices.add(entityUlatheDevice);
                }
            }
            return lstEntityUlatheDevices;
        } catch (Exception e) {
            logger.warn(String.format("??????????????????[%s]????????????[%s]????????????[%s]??????????????????????????????????????????????????????????????????????????????%s", trainsetName, startDate, endDate, e.toString()));
            return null;
        }
    }

    /**
     * ???????????????????????????????????????
     * @param trainsetName ????????????
     * @param startDate ????????????
     * @param endDate ????????????
     * @return
     */
    public List<EntityAxleDevice> getAxleGetDeviceRecord(String trainsetName, String startDate, String endDate){
        List<EntityAxleDevice> entityAxleDevices = null;
        try
        {
            Map requestMap = new HashMap();
            requestMap.put("TrainsetName", trainsetName);
            requestMap.put("StartDate", startDate);
            requestMap.put("EndDate", endDate);
            JsonRootBean jsonRootBean = HttpUtil.getToken(recheckTaskProperties);
            JSONObject axleJson = HttpUtil.doPost(recheckTaskProperties.getAxleGetDeviceRecordUrl(), requestMap, recheckTaskProperties, jsonRootBean);
            entityAxleDevices = JSON.parseArray(axleJson.getString("Data"), EntityAxleDevice.class);
        }
        catch (Exception e){
            logger.warn(String.format("??????????????????[%s]????????????[%s]????????????[%s]??????????????????????????????????????????????????????????????????????????????%s", trainsetName, startDate, endDate, e.toString()));
            return null;
        }
        return entityAxleDevices;
    }

    /**
     * ??????LU????????????????????????
     * @param trainsetName ????????????
     * @param startDate ????????????
     * @param endDate ????????????
     * @return
     **/
    public List<EntityLUDevice> getLUGetDeviceRecord(String trainsetName, String startDate, String endDate){
        List<EntityLUDevice> entityLuDevices = null;
        try
        {
            Map requestMap = new HashMap();
            requestMap.put("TrainsetName", trainsetName);
            requestMap.put("StartDate", startDate);
            requestMap.put("EndDate", endDate);
            JsonRootBean jsonRootBean = HttpUtil.getToken(recheckTaskProperties);
            JSONObject luJson = HttpUtil.doPost(recheckTaskProperties.getLuGetDeviceRecordUrl(), requestMap, recheckTaskProperties, jsonRootBean);
            entityLuDevices = JSON.parseArray(luJson.getString("Data"), EntityLUDevice.class);
        }
        catch (Exception e){
            logger.error(String.format("??????????????????[%s]????????????[%s]????????????[%s]??????LU???????????????????????????????????????????????????????????????%s", trainsetName, startDate, endDate, e.toString()));
            return null;
        }
        return entityLuDevices;
    }

    /**
     * ?????????????????????????????????
     * @return ??????????????????
     */
    public String getImportRandom(){
        String result = "IMPORT";
        try{
            double d_value = Math.random() * Math.random();
            int value = (int)(d_value * 10000);
            Random r = new Random(value);
            result = result + "_"+ r.nextInt(1000);
        }catch (Exception e){
            logger.error("?????????????????????????????????:" + e.toString());
        }
        return result;
    }

    /**
     *  ????????????????????????????????????
     * @param devices ?????????
     * @param fieldName ????????????????????????
     * @return ??????????????????
     */
    private String GetValue(List<EntityUlatheDevice> devices, String fieldName) {
        String result = "0.00";
        EntityUlatheDevice eneity = null;
        for (int i = 0; i < devices.size(); i++) {
            if (eneity == null) {
                eneity = devices.get(i);
                continue;
            }
            //??????????????????????????????????????????
            BigDecimal bigDecimal = new BigDecimal(GetValue(devices.get(i), fieldName));
            //?????????0?????????????????????
            if (bigDecimal.compareTo(BigDecimal.ZERO) == 0)
                continue;
            //??????????????????????????????????????????????????????????????????????????????
            Date date1 = ParseDate(eneity.getD_PRODUCT_TIME() + " " + eneity.getS_END_PRODUCT_TIME());
            Date date2 = ParseDate(devices.get(i).getD_PRODUCT_TIME() + " " + devices.get(i).getS_END_PRODUCT_TIME());
            if (date1.compareTo(date2) < 0)
                eneity = devices.get(i);
        }
        return result;
    }

    /**
     *  ??????????????????????????????????????????????????????
     * @param devices ????????????
     * @param fieldName ??????????????????????????? S_WHEELSET_ID
     * @param isMax ?????????????????????????????????true:????????????false:?????????????????????
     * @return ???????????????
     */
    private String GetValue(List<EntityUlatheDevice> devices, String fieldName, boolean isMax) {
        String result = "0.00";
        BigDecimal decimal = null;  //?????????????????????
        for (int i = 0; i < devices.size(); i++) {
            //??????????????????????????????????????????
            BigDecimal bigDecimal = new BigDecimal(GetValue(devices.get(i), fieldName));
            //????????????????????????????????????????????????
            if (decimal == null)
                decimal = bigDecimal;
            else {
                //?????????0?????????????????????
                if (decimal.compareTo(BigDecimal.ZERO) == 0)
                    continue;
                //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                if (decimal.compareTo(bigDecimal) == 1 && isMax) {
                    decimal = bigDecimal;
                }
                //???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                if (decimal.compareTo(bigDecimal) == -1 && !isMax) {
                    decimal = bigDecimal;
                }
            }
            result = decimal.toString();
        }
        return result;
    }

    /**
     * ?????????????????????????????????
     * @param obj ??????
     * @param fieldName ????????????
     * @return ??????????????????
     */
    private String GetValue(Object obj, String fieldName){
        String result = "";
        try{
            //??????????????????????????????
            Field field = obj.getClass().getDeclaredField(fieldName);
            //????????????????????????????????????????????????
            field.setAccessible(true);
            //?????????????????????????????????
            result = field.get(obj).toString();
        }
        catch (NoSuchFieldException noSuchFieldException){
            logger.error(String.format("????????????%s???????????????[NoSuchFieldException]??????%s",fieldName, noSuchFieldException.toString()));
        }
        catch (IllegalArgumentException illegalArgumentException){
            logger.error(String.format("????????????%s???????????????[IllegalArgumentException]??????%s",fieldName, illegalArgumentException.toString()));
        }
        catch (IllegalAccessException illegalAccessException){
            logger.error(String.format("????????????%s???????????????[IllegalAccessException]??????%s",fieldName, illegalAccessException.toString()));
        }
        return result;
    }

    /**
     * ?????????????????????????????????????????????
     * @param entityUlatheDevices ???????????????
     * @param wheelsetid ????????????
     * @return ????????????
     */
    private List<EntityUlatheDevice> GetRepeatDatas(List<EntityUlatheDevice> entityUlatheDevices, String wheelsetid){
        List<EntityUlatheDevice> result = new ArrayList<EntityUlatheDevice>();
        for(int i = 0;i <entityUlatheDevices.size();i++){
            if(entityUlatheDevices.get(i).getS_WHEELSET_ID().equals(wheelsetid)){
                result.add(entityUlatheDevices.get(i));
            }
        }
        return  result;
    }

    /**
     * ??????????????????????????????????????????
     * @param datetime ????????????????????????
     * @return ???????????????
     */
    private Date ParseDate(String datetime){
        Date date = new Date();
        try
        {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = format.parse(datetime);
        }
        catch (ParseException parseException){
            logger.error(String.format("???%s????????????????????????????????????[ParseException]%s", datetime, parseException.toString()));
        }
        return date;
    }


}
