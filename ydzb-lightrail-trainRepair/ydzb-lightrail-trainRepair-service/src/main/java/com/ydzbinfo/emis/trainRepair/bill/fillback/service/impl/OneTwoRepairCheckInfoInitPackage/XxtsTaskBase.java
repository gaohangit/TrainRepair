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
 * 镟修、空心轴和LU探伤基类
 */
@Component
public class XxtsTaskBase  extends CheckInfoInit {

    protected static final Logger logger = getLogger(XxtsTaskBase.class);
    @Autowired
    private BillCommon billCommon;

    @Autowired
    private RecheckTaskProperties recheckTaskProperties;


    /**
     * 初始化规则
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
     * 获取通用的属性编码
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
     * 初始化通用属性数据
     */
    public List<ChecklistDetailInfoForShow> initCommonAttribute(ChecklistSummary summary, List<ChecklistDetailInfoForShow> cells){
        List<ChecklistDetailInfoForShow> result = new ArrayList<ChecklistDetailInfoForShow>();
        RepairItemInfo repairItem = billCommon.getItemInfoByCode(summary.getItemCode(), summary.getUnitCode());
        List<String> attrs = getCommonAttribute();
        for(ChecklistDetailInfoForShow cell: cells){
            String attributeCode = cell.getAttributeCode();
            if(attributeCode.equals(AttributeEnum.ATTR_REPAIR_MAINCYC.getValue())){    //修程
                for(MainCycEnum main : MainCycEnum.values()){
                    if(summary.getMainCyc().equals(main.getMainCyc().getTaskRepairCode())){
                        cell.setValue(main.getMainCyc().getTaskRepairName());
                        break;
                    }
                }
            }
            if(attributeCode.equals(AttributeEnum.ATTR_REPAIR_CARD.getValue()) && repairItem != null){       //工艺卡编号
                cell.setValue(repairItem.getMainTainCardNo());
            }
            if(attributeCode.equals(AttributeEnum.ATTR_REPAIR_CYCLE.getValue()) && repairItem != null){      //维修周期
               cell.setValue(billCommon.getItemCycle(repairItem.getItemCycleVos()));
            }
            if(attributeCode.equals(AttributeEnum.ATTR_REPAIR_SYSTEM.getValue()) && repairItem != null){     //系统分类
                cell.setValue(repairItem.getFuncSysName());
            }
            if(attributeCode.equals(AttributeEnum.ATTR_REPAIR_PART.getValue()) && repairItem != null){       //部件
                cell.setValue(repairItem.getStrPartsTypeName());
            }
            if(attributeCode.equals(AttributeEnum.ATTR_QUALITY_CONTROL.getValue()) && repairItem != null){    //质量控制方式
                //TODO 暂时还不知道如何获取项目的质量控制方式，先置为空字符串
                cell.setValue("");
            }
            if(attrs.contains(attributeCode) || cell.getValue().equals("N/A")){
                result.add(cell);
            }
        }
        return result;
    }


    /**
     *  根据属性编码和单元格数据获取数据实体
     * @param trainsetId 车组ID
     * @param contentList 数据实体集合
     * @param attr 属性编码
     * @param cellValue 单元格数据
     * @return 实体
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
     *  根据属性编码和单元格数据获取数据实体
     * @param contentList 数据实体集合
     * @param attr 属性编码
     * @return 实体
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
     *  根据属性编码和单元格数据获取数据实体
     * @param contentList 数据实体集合
     * @param attr 属性编码
     * @return 实体
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
     *  根据属性编码获取第一个实体
     * @param cells 数据实体集合
     * @param attr 属性编码
     * @return 实体
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
     *  根据属性编码获取第一个实体
     * @param cells 数据实体集合
     * @param attr 属性编码
     * @return 实体
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
     * 获取指定辆序和轴位的数据，并对数据进行设置
     * @param trainSetId 车组ID
     * @param lstDetails 数据集
     * @param carNo 辆序
     * @param axle 轴位
     * @param attr 属性
     * @param value 赋值数据
     * @return 已赋值的数据
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
     * 获取指定辆序和轴位的数据，并对数据进行设置
     * @param trainSetId 车组ID
     * @param lstDetails 数据集
     * @param carNo 辆序
     * @param axle 轴位
     * @param attr 属性
     * @param value 赋值数据
     * @return 已赋值的数据
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
     * 获取轮轴数据
     * @param lstAxleWheels 轮轴数据集合
     * @param trainsetId 车组ID
     * @param carNo 辆序 (01)
     * @param date 日期（yyyyMMdd）
     * @param axlePosition 轴位
     * @param firstFind 是否时第一次查找轮轴数据
     * @return 轮轴数据集合
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
     * 根据关联主键，获取数据实体
     * @param lstDetails 数据实体集合
     * @param contentId 关联主键
     * @return 数据实体
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
     * 根据关联主键，获取数据实体
     * @param lstDetails 数据实体集合
     * @param contentId 关联主键
     * @return 数据实体
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
     * 设置显示的数据
     * @param trainsetId 车组ID
     * @param sDate 日期
     * @param lstDetail 记录单上所有的数据
     * @param attributeEnum 要设置的属性
     * @param lstAxleWheels 轮轴数据
     * @param lstCarNos 辆序集合
     * @param lstAxles 轴位集合
     * @return 被修改的数据集合
     */
    public List<ChecklistDetailInfoForShow> setViewCell(String trainsetId, String sDate, List<ChecklistDetailInfoForShow> lstDetail, AttributeEnum attributeEnum,
                                                         List<AxleWheel> lstAxleWheels, List<ChecklistDetailInfoForShow> lstCarNos, List<ChecklistDetailInfoForShow> lstAxles){
        List<ChecklistDetailInfoForShow> result = new ArrayList<ChecklistDetailInfoForShow>();
        //循环
        for(int i=0;i< lstDetail.size();i++){
            ChecklistDetailInfoForShow detail = lstDetail.get(i);
            //不是该属性的数据则跳过
            if (!detail.getAttributeCode().equals(attributeEnum.getValue()))
                continue;
            ChecklistDetailInfoForShow carNoDetail = null;      //辆序
            ChecklistDetailInfoForShow axleDetail = null;       //轴位
            //通过轴号的关联主键，获取辆序实体和轴位实体
            for (int j = 0;j < detail.getLinkContents().size(); j++){
                ChkDetailLinkContent linkContent = detail.getLinkContents().get(j);
                //获取辆序
                ChecklistDetailInfoForShow findDetail = findDetailByContentIdForShow(lstCarNos,linkContent.getLinkContentId());
                if(findDetail!=null){
                    carNoDetail = findDetail;
                    continue;
                }
                //获取轴位
                findDetail = findDetailByContentIdForShow(lstAxles,linkContent.getLinkContentId());
                if(findDetail!=null){
                    axleDetail = findDetail;
                    continue;
                }
                if(carNoDetail != null && axleDetail != null){
                    break;
                }
            }
            //通过辆序和轴位获取轮轴信息
            if(carNoDetail!=null && axleDetail!=null){
                String carNo = carNoDetail.getValue();
                String axlePosition = axleDetail.getValue();
                //如果轮轴信息存在于轮轴集合内，则直接使用并赋值，如果不存在则调用履历接口获取，并将数据添加到轮轴集合内
                AxleWheel axleWheel = getAxleWheel(lstAxleWheels,trainsetId, carNo, sDate, axlePosition,true);

                //设置轴号
                if(attributeEnum.getValue().equals(AttributeEnum.ATTR_AXLE_NUMBER.getValue())){
                    detail.setValue(axleWheel.getSerialNum());
                    result.add(detail);
                }
                //设置轴走行
                if(attributeEnum.getValue().equals(AttributeEnum.ATTR_AXLE_ACCUMILE.getValue())){
                    detail.setValue(axleWheel.getRunmiles());
                    result.add(detail);
                }
                //设置轴属性
                if(attributeEnum.getValue().equals(AttributeEnum.ATTR_AXLE_ATTRIBUTE.getValue())){
                    detail.setValue(axleWheel.getDtType());
                    result.add(detail);
                }
                //设置一位轮号
                if(attributeEnum.getValue().equals(AttributeEnum.ATTR_WHEEL_ONENUMBER.getValue())){
                    detail.setValue(axleWheel.getZSerialNum());
                    result.add(detail);
                }
                //设置二位轮号
                if(attributeEnum.getValue().equals(AttributeEnum.ATTR_WHEEL_TWONUMBER.getValue())){
                    detail.setValue(axleWheel.getYSerialnum());
                    result.add(detail);
                }
                //设置一位轮走行
                if(attributeEnum.getValue().equals(AttributeEnum.ATTR_WHEEL_ONEACCUMILE.getValue())){
                    detail.setValue(axleWheel.getZRunmiles());
                    result.add(detail);
                }
                //设置二位论走行
                if(attributeEnum.getValue().equals(AttributeEnum.ATTR_WHEEL_TWOACCUMILE.getValue())){
                    detail.setValue(axleWheel.getYrunmiles());
                    result.add(detail);
                }
            }
        }
        return result;
    }

    /**
     * 获取当前日期
     * @param days 要相加的天数
     * @return 当前日期 （yyyyMMdd）
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
     * 根据车组名称和起止日期获取设备镟轮数据
     *
     * @param trainsetName 车组名称
     * @param startDate    开始日期
     * @param endDate      结束日期
     * @return 镟轮数据
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
            //对设备数据进行排重，当位置相同时，镟前轮径值取最大值，镟后轮径值取最小值，轮缘高、轮缘厚和QR值等，取非零的最新值
            for(int i=0;i < entityUlatheDevices.size();i++){
                //获取重复数据
                List<EntityUlatheDevice> es = GetRepeatDatas(entityUlatheDevices, entityUlatheDevices.get(i).getS_WHEELSET_ID());
                //设个位置没有重复数据时，直接向程序集添加数据
                if(es.size() == 1){
                    lstEntityUlatheDevices.add(es.get(0));
                }else if(es.size()> 1){
                    //有重复值，按照规则获取数据，然后向程序集内添加数据
                    EntityUlatheDevice entityUlatheDevice = es.get(0);
                    entityUlatheDevice.setI_PRE_WHEEL_DL(GetValue(es, "I_PRE_WHEEL_DL",true));  //镟前轮径值(左)
                    entityUlatheDevice.setI_LT_WHEEL_DL(GetValue(es, "I_LT_WHEEL_DL",false));   //镟后轮径值(左)
                    entityUlatheDevice.setI_PRE_WHEEL_DR(GetValue(es, "I_PRE_WHEEL_DR",true));  //镟前轮径值(右)
                    entityUlatheDevice.setI_LT_WHEEL_DR(GetValue(es, "I_LT_WHEEL_DR",false));   //镟后轮径值(右)
                    entityUlatheDevice.setI_PRE_SDL(GetValue(es, "I_PRE_SDL")); //1位侧镟前轮缘厚度
                    entityUlatheDevice.setI_LT_SDL(GetValue(es, "I_LT_SDL"));   //1位侧镟后轮缘厚度
                    entityUlatheDevice.setI_PRE_SDR(GetValue(es, "I_PRE_SDR")); //2位侧镟前轮缘厚度
                    entityUlatheDevice.setI_LT_SDR(GetValue(es, "I_LT_SDR"));   //2位侧镟后轮缘厚度
                    entityUlatheDevice.setI_PRE_SHL(GetValue(es, "I_PRE_SHL")); //1位侧镟前轮缘高度
                    entityUlatheDevice.setI_LT_SHL(GetValue(es, "I_LT_SHL"));   //1位侧镟后轮缘高度
                    entityUlatheDevice.setI_PRE_SHR(GetValue(es, "I_PRE_SHR")); //2位侧镟前轮缘高度
                    entityUlatheDevice.setI_LT_SHR(GetValue(es, "I_LT_SHR"));   //2位侧镟后轮缘高度
                    entityUlatheDevice.setI_PRE_QRL(GetValue(es, "I_PRE_QRL")); //1位侧镟前QR值
                    entityUlatheDevice.setI_LT_QRL(GetValue(es, "I_LT_QRL"));   //1位侧镟后QR值
                    entityUlatheDevice.setI_PRE_SDL(GetValue(es, "I_PRE_SDL")); //2位侧镟前QR值
                    entityUlatheDevice.setI_LT_QRR(GetValue(es, "I_LT_QRR"));   //2位侧镟后QR值
                    lstEntityUlatheDevices.add(entityUlatheDevice);
                }
            }
            return lstEntityUlatheDevices;
        } catch (Exception e) {
            logger.warn(String.format("根据车组名称[%s]开始日期[%s]结束日期[%s]获取该车组设备镟轮数据接口时引发异常，接口返回信息：%s", trainsetName, startDate, endDate, e.toString()));
            return null;
        }
    }

    /**
     * 获取空心轴探伤设备接口数据
     * @param trainsetName 车组名称
     * @param startDate 开始时间
     * @param endDate 结束时间
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
            logger.warn(String.format("根据车组名称[%s]开始日期[%s]结束日期[%s]获取空心轴探伤设备数据接口时引发异常，接口返回信息：%s", trainsetName, startDate, endDate, e.toString()));
            return null;
        }
        return entityAxleDevices;
    }

    /**
     * 获取LU探伤设备接口数据
     * @param trainsetName 车组名称
     * @param startDate 开始时间
     * @param endDate 结束时间
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
            logger.error(String.format("根据车组名称[%s]开始日期[%s]结束日期[%s]获取LU探伤设备数据接口时引发异常，接口返回信息：%s", trainsetName, startDate, endDate, e.toString()));
            return null;
        }
        return entityLuDevices;
    }

    /**
     * 导入时获取随机职工编码
     * @return 随机职工编码
     */
    public String getImportRandom(){
        String result = "IMPORT";
        try{
            double d_value = Math.random() * Math.random();
            int value = (int)(d_value * 10000);
            Random r = new Random(value);
            result = result + "_"+ r.nextInt(1000);
        }catch (Exception e){
            logger.error("导入时获取随机职工编码:" + e.toString());
        }
        return result;
    }

    /**
     *  获取数据集中最新的非零值
     * @param devices 数据集
     * @param fieldName 获取值的字段名称
     * @return 最新的非零值
     */
    private String GetValue(List<EntityUlatheDevice> devices, String fieldName) {
        String result = "0.00";
        EntityUlatheDevice eneity = null;
        for (int i = 0; i < devices.size(); i++) {
            if (eneity == null) {
                eneity = devices.get(i);
                continue;
            }
            //将字段的值转换为精确小数类型
            BigDecimal bigDecimal = new BigDecimal(GetValue(devices.get(i), fieldName));
            //数据为0时，直接过滤掉
            if (bigDecimal.compareTo(BigDecimal.ZERO) == 0)
                continue;
            //比较镟轮时间，取得镟轮时间靠后的数据，赋值给最终结果
            Date date1 = ParseDate(eneity.getD_PRODUCT_TIME() + " " + eneity.getS_END_PRODUCT_TIME());
            Date date2 = ParseDate(devices.get(i).getD_PRODUCT_TIME() + " " + devices.get(i).getS_END_PRODUCT_TIME());
            if (date1.compareTo(date2) < 0)
                eneity = devices.get(i);
        }
        return result;
    }

    /**
     *  当相同的位置有重复数据，需要进行排重
     * @param devices 设备数据
     * @param fieldName 获取数据的字段名称 S_WHEELSET_ID
     * @param isMax 获取最大值还是最小值（true:最大值；false:非零的最小值）
     * @return 获取的数据
     */
    private String GetValue(List<EntityUlatheDevice> devices, String fieldName, boolean isMax) {
        String result = "0.00";
        BigDecimal decimal = null;  //原精确小数实体
        for (int i = 0; i < devices.size(); i++) {
            //将字段的值转换为精确小数类型
            BigDecimal bigDecimal = new BigDecimal(GetValue(devices.get(i), fieldName));
            //当原精确小数实体为空时，直接赋值
            if (decimal == null)
                decimal = bigDecimal;
            else {
                //数据为0时，直接过滤掉
                if (decimal.compareTo(BigDecimal.ZERO) == 0)
                    continue;
                //如果原精确小数实体有值，新的精确小数实体数据比该数据大，且本次需要获取到最大值，则将数据赋值给原精确小数实体
                if (decimal.compareTo(bigDecimal) == 1 && isMax) {
                    decimal = bigDecimal;
                }
                //如果原精确小数实体有值，新的精确小数实体数据比该数据小，且本次需要获取最小值，则将数据赋值给原精确小数实体
                if (decimal.compareTo(bigDecimal) == -1 && !isMax) {
                    decimal = bigDecimal;
                }
            }
            result = decimal.toString();
        }
        return result;
    }

    /**
     * 获取实体内指定字段的值
     * @param obj 实体
     * @param fieldName 指定字段
     * @return 指定字段的值
     */
    private String GetValue(Object obj, String fieldName){
        String result = "";
        try{
            //通过反射获取指定字段
            Field field = obj.getClass().getDeclaredField(fieldName);
            //在通过字段获取数值时，先设置权限
            field.setAccessible(true);
            //通过字段获取该字段的值
            result = field.get(obj).toString();
        }
        catch (NoSuchFieldException noSuchFieldException){
            logger.error(String.format("获取属性%s的值时引发[NoSuchFieldException]异常%s",fieldName, noSuchFieldException.toString()));
        }
        catch (IllegalArgumentException illegalArgumentException){
            logger.error(String.format("获取属性%s的值时引发[IllegalArgumentException]异常%s",fieldName, illegalArgumentException.toString()));
        }
        catch (IllegalAccessException illegalAccessException){
            logger.error(String.format("获取属性%s的值时引发[IllegalAccessException]异常%s",fieldName, illegalAccessException.toString()));
        }
        return result;
    }

    /**
     * 根据轮对位置获取相同的重复数据
     * @param entityUlatheDevices 总的数据集
     * @param wheelsetid 轮对位置
     * @return 重复数据
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
     * 两个字符串相加后，转换为时间
     * @param datetime 日期和时间字符串
     * @return 日期加时间
     */
    private Date ParseDate(String datetime){
        Date date = new Date();
        try
        {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = format.parse(datetime);
        }
        catch (ParseException parseException){
            logger.error(String.format("将%s转换为时间时，引发了异常[ParseException]%s", datetime, parseException.toString()));
        }
        return date;
    }


}
