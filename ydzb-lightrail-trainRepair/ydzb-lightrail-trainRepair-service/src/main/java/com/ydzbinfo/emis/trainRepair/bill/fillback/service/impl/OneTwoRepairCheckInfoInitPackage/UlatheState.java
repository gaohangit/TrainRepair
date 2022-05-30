package com.ydzbinfo.emis.trainRepair.bill.fillback.service.impl.OneTwoRepairCheckInfoInitPackage;

import com.ydzbinfo.emis.trainRepair.bill.fillback.utils.AttributeEnum;
import com.ydzbinfo.emis.trainRepair.bill.fillback.utils.AxleTypeEnum;
import com.ydzbinfo.emis.trainRepair.bill.fillback.utils.BillCommon;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.ChecklistDetailInfoForSave;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.ChecklistDetailInfoForShow;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.ChkDetailLinkContentForModule;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.*;
import com.ydzbinfo.emis.utils.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class UlatheState extends XxtsTaskBase {

    protected static final Logger logger = getLogger(WheelTask.class);
    @Autowired
    private BillCommon billCommon;

    /**
     * 获取所有的确认值属性编码
     * @return 所有的确认值属性编码
     */
    public List<AttributeEnum> getAllStateConfigAttr(){
        List<AttributeEnum> attrs = new ArrayList<AttributeEnum>();
        attrs.add(AttributeEnum.ATTR_WHEEL_TYZLJC);
        attrs.add(AttributeEnum.ATTR_WHEEL_TYZXJLJC);
        attrs.add(AttributeEnum.ATTR_WHEEL_TYCLLJC);
        attrs.add(AttributeEnum.ATTR_WHEEL_TCTYZXJLJC);
        attrs.add(AttributeEnum.ATTR_WHEEL_DCTYZXJLJC);
        attrs.add(AttributeEnum.ATTR_WHEEL_DCTYCLLJC);
        attrs.add(AttributeEnum.ATTR_WHEEL_TCTYCLLJC);
        attrs.add(AttributeEnum.ATTR_WHEEL_TYCLDYNCLJLJC);
        attrs.add(AttributeEnum.ATTR_WHEEL_TYDCNLTDLLDLJC);
        attrs.add(AttributeEnum.ATTR_WHEEL_XLCXLJC);
        attrs.add(AttributeEnum.ATTR_WHEEL_DCTYLDLJC);
        attrs.add(AttributeEnum.ATTR_WHEEL_TCTYLDLJC);
        return attrs;
    }

    /**
     * 设置确认值信息
     * @param contentList 数据信息
     * @param stateConfigInfo 确认值信息
     * @return 所有被修改的确认值单元格
     */
    public List<ChecklistDetailInfoForShow> setStateConfigInfoForShow(List<ChecklistDetailInfoForShow> contentList, StateConfigInfo stateConfigInfo){
        List<ChecklistDetailInfoForShow> result = new ArrayList<ChecklistDetailInfoForShow>();
        for(int i=0;i< contentList.size();i++){
            ChecklistDetailInfoForShow detail = contentList.get(i);
            if (detail.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_TYZLJC.getValue())){
                detail.setValue(stateConfigInfo.getTyz());
                result.add(detail);
            }
            if (detail.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_TYZXJLJC.getValue())){
                detail.setValue(stateConfigInfo.getTyzxj());
                result.add(detail);
            }
            if (detail.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_TYCLLJC.getValue())){
                detail.setValue(stateConfigInfo.getTycllj());
                result.add(detail);
            }
            if (detail.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_TCTYZXJLJC.getValue())){
                detail.setValue(stateConfigInfo.getTtyzxj());
                result.add(detail);
            }
            if (detail.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_DCTYZXJLJC.getValue())){
                detail.setValue(stateConfigInfo.getDtyzxj());
                result.add(detail);
            }
            if (detail.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_DCTYCLLJC.getValue())){
                detail.setValue(stateConfigInfo.getTtycllj());
                result.add(detail);
            }
            if (detail.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_TCTYCLLJC.getValue())){
                detail.setValue(stateConfigInfo.getDtycllj());
                result.add(detail);
            }
            if (detail.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_TYCLDYNCLJLJC.getValue())){
                detail.setValue(stateConfigInfo.getTycldynclj());
                result.add(detail);
            }
            if (detail.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_TYDCNLTDLLDLJC.getValue())){
                detail.setValue(stateConfigInfo.getTydcnltdlldlj());
                result.add(detail);
            }
            if (detail.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_XLCXLJC.getValue())){
                detail.setValue(stateConfigInfo.getXlcxjlj());
                result.add(detail);
            }
            if (detail.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_DCTYLDLJC.getValue())){
                detail.setValue(stateConfigInfo.getDtyldlj());
                result.add(detail);
            }
            if (detail.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_TCTYLDLJC.getValue())){
                detail.setValue(stateConfigInfo.getTtyldlj());
                result.add(detail);
            }
        }
        return result;
    }

    /**
     * 设置确认值信息
     * @param contentList 数据信息
     * @param stateConfigInfo 确认值信息
     * @return 所有被修改的确认值单元格
     */
    public List<ChecklistDetailInfoForShow> setStateConfigInfoForSave(List<ChecklistDetailInfoForSave> contentList, StateConfigInfo stateConfigInfo){
        List<ChecklistDetailInfoForShow> result = new ArrayList<ChecklistDetailInfoForShow>();
        for(int i=0;i< contentList.size();i++){
            ChecklistDetailInfoForSave detail = new ChecklistDetailInfoForSave();
            BeanUtils.copyProperties(contentList.get(i), detail);
            if (detail.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_TYZLJC.getValue())){
                result.add(convertDetailForShow(detail, stateConfigInfo.getTyz(), AttributeEnum.ATTR_WHEEL_TYZLJC.getLabel()));
            }
            if (detail.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_TYZXJLJC.getValue())){
                result.add(convertDetailForShow(detail, stateConfigInfo.getTyzxj(), AttributeEnum.ATTR_WHEEL_TYZXJLJC.getLabel()));
            }
            if (detail.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_TYCLLJC.getValue())){
                result.add(convertDetailForShow(detail, stateConfigInfo.getTycllj(), AttributeEnum.ATTR_WHEEL_TYCLLJC.getLabel()));
            }
            if (detail.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_TCTYZXJLJC.getValue())){
                result.add(convertDetailForShow(detail, stateConfigInfo.getTtyzxj(), AttributeEnum.ATTR_WHEEL_TCTYZXJLJC.getLabel()));
            }
            if (detail.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_DCTYZXJLJC.getValue())){
                result.add(convertDetailForShow(detail, stateConfigInfo.getDtyzxj(), AttributeEnum.ATTR_WHEEL_DCTYZXJLJC.getLabel()));
            }
            if (detail.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_DCTYCLLJC.getValue())){
                result.add(convertDetailForShow(detail, stateConfigInfo.getDtycllj(), AttributeEnum.ATTR_WHEEL_DCTYCLLJC.getLabel()));
            }
            if (detail.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_TCTYCLLJC.getValue())){
                result.add(convertDetailForShow(detail, stateConfigInfo.getTtycllj(), AttributeEnum.ATTR_WHEEL_TCTYCLLJC.getLabel()));
            }
            if (detail.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_TYCLDYNCLJLJC.getValue())){
                result.add(convertDetailForShow(detail, stateConfigInfo.getTycldynclj(), AttributeEnum.ATTR_WHEEL_TYCLDYNCLJLJC.getLabel()));
            }
            if (detail.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_TYDCNLTDLLDLJC.getValue())){
                result.add(convertDetailForShow(detail, stateConfigInfo.getTydcnltdlldlj(), AttributeEnum.ATTR_WHEEL_TYDCNLTDLLDLJC.getLabel()));
            }
            if (detail.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_XLCXLJC.getValue())){
                result.add(convertDetailForShow(detail, stateConfigInfo.getXlcxjlj(), AttributeEnum.ATTR_WHEEL_XLCXLJC.getLabel()));
            }
            if (detail.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_DCTYLDLJC.getValue())){
                result.add(convertDetailForShow(detail, stateConfigInfo.getDtyldlj(), AttributeEnum.ATTR_WHEEL_DCTYLDLJC.getLabel()));
            }
            if (detail.getAttributeCode().equals(AttributeEnum.ATTR_WHEEL_TCTYLDLJC.getValue())){
                result.add(convertDetailForShow(detail, stateConfigInfo.getTtyldlj(), AttributeEnum.ATTR_WHEEL_TCTYLDLJC.getLabel()));
            }
        }
        return result;
    }

    /**
     * 获取镟修数据的状态确认值
     * @param trainSetId 车组ID
     * @param lathingDatas 传输进入的新镟修数据
     * @return 状态确认值
     */
    public StateConfigInfo getStateConfigInfo(String trainSetId, List<AxleWheelDiameterEntity> lathingDatas){
        StateConfigInfo state = new StateConfigInfo();
        List<AxleWheelDiameterEntity> oldLathing = billCommon.getLatestAxleTurningdata(trainSetId);
        List<AxleWheelDiameterEntity> lstLathingDatas = compareToLathing(trainSetId,oldLathing, lathingDatas);
        //同一轴轮径差最大值
        StateInfo tyzState = getTYZMaxDiff(lstLathingDatas);
        if(tyzState != null){
            state.setTyz(tyzState.getConfigValue());
            state.setTyz_source(tyzState.getValueSource());
        }
        //同一转向架轮径差最大值
        StateInfo tyzxjState = getTYZXJMaxDiff(lstLathingDatas);
        if( tyzxjState !=null){
            state.setTyzxj(tyzxjState.getConfigValue());
            state.setTyzxj_source(tyzxjState.getValueSource());
        }
        //动车同一转向架轮径差最大值
        StateInfo dtyzxjState = getDTYZXJMaxxDiff(lstLathingDatas);
        if(dtyzxjState !=null){
            state.setDtyzxj(dtyzxjState.getConfigValue());
            state.setDtyzxj_source(dtyzxjState.getValueSource());
        }
        //拖车同一转向架轮径差最大值
        StateInfo ttyzxjState = getTTYZXJMaxxDiff(lstLathingDatas);
        if(ttyzxjState !=null){
            state.setTtyzxj(ttyzxjState.getConfigValue());
            state.setTtyzxj_source(ttyzxjState.getValueSource());
        }
        //同一车辆轮径差最大值
        StateInfo tyclljState = getTYCLMaxDiff(lstLathingDatas);
        if(tyclljState !=null){
            state.setTycllj(tyclljState.getConfigValue());
            state.setTycllj_source(tyclljState.getValueSource());
        }
        //动车同一车辆轮径差最大值
        StateInfo dtyclljState = getDTYCLMaxDiff(lstLathingDatas);
        if(dtyclljState !=null){
            state.setDtycllj(dtyclljState.getConfigValue());
            state.setDtycllj_source(dtyclljState.getValueSource());
        }
        //拖车同一车辆轮径差最大值
        StateInfo ttyclljState = getTTYCLMaxDiff(lstLathingDatas);
        if(ttyclljState !=null){
            state.setTtycllj(ttyclljState.getConfigValue());
            state.setTtycllj_source(ttyclljState.getValueSource());
        }
        //相邻车厢轮径差最大值
        StateInfo xlcxjljState = getXLCXJLJMaxDiff(lstLathingDatas,trainSetId);
        if (xlcxjljState != null){
            state.setXlcxjlj(xlcxjljState.getConfigValue());
            state.setXlcxjlj_source(xlcxjljState.getValueSource());
        }
        //同一动车内两条动力轮对轮径差最大值(5型车独有)
        StateInfo tydcnltdlldljState = getDDLLDMaxDiff(lathingDatas);
        if (tydcnltdlldljState != null){
            state.setTydcnltdlldlj(tydcnltdlldljState.getConfigValue());
            state.setTydcnltdlldlj_source(tydcnltdlldljState.getValueSource());
        }
        //动车同一轮对轮径差最大值
        StateInfo dtyldljState = getDTYLDLJMaxDiff(lstLathingDatas);
        if(dtyldljState !=null){
            state.setDtyldlj(dtyldljState.getConfigValue());
            state.setDtyldlj_source(dtyldljState.getValueSource());
        }
        //拖车同一轮对轮径差最大值
        StateInfo ttyldljState = getTTYLDLJMaxDiff(lstLathingDatas);
        if(ttyldljState !=null){
            state.setTtyldlj(ttyldljState.getConfigValue());
            state.setTtyldlj_source(ttyldljState.getValueSource());
        }
        return  state;
    }

    /**
     * 比较并拼接镟修记录
     * @param trainsetId 车组ID
     * @param oldLathing 历史最新镟修记录
     * @param newLathing 记录单传输来的镟修记录
     * @return 以记录单传输来的镟修记录，并向内补充历史最新镟修记录的集合
     */
    private List<AxleWheelDiameterEntity> compareToLathing(String trainsetId, List<AxleWheelDiameterEntity> oldLathing, List<AxleWheelDiameterEntity> newLathing){
        //如果记录单没有传输过来数据，则直接使用历史最新镟修数据
        if(newLathing ==null || newLathing.size() == 0){
            return  oldLathing;
        }
        List<AxleWheelDiameterEntity> lstLathingDatas = new ArrayList<AxleWheelDiameterEntity>();
        TrainsetBaseInfo trainsetInfo = billCommon.getTrainsetInfo(trainsetId);
        if(trainsetInfo!=null){
            List<String> lstCarNos = billCommon.getTrainMarshlType(trainsetInfo.getTrainsetid());
            //循环辆序
            for(int i=0;i<lstCarNos.size();i++){
                String carNo = lstCarNos.get(i);        //辆序
                //循环轴位
                for(int a = 1; a < 5; a++){
                    String axle = String.valueOf(a);    //轴位
                    boolean isFind = false;
                    //循环记录单输入的镟修数据
                    for(int n = 0; n < newLathing.size(); n++){
                        AxleWheelDiameterEntity newData = newLathing.get(n);
                        if(newData.getCarNo().equals(carNo) && newData.getLocatetionnum().equals(axle)){
                            lstLathingDatas.add(newData);
                            isFind = true;
                        }
                    }
                    //如果镟修记录单输入的数据找到了，则不再查找历史镟修数据
                    if (isFind)
                        continue;
                    for(int o = 0; o < oldLathing.size(); o++){
                        AxleWheelDiameterEntity oldData = oldLathing.get(o);
                        if(oldData.getCarNo().equals(carNo) && oldData.getLocatetionnum().equals(axle)){
                            lstLathingDatas.add(oldData);
                        }
                    }
                }
            }
        }
        return  lstLathingDatas;
    }

    /**
     *  获取同一轴轮径差最大值（所有车型）
     * @param lstLathing 当前镟修记录
     * @return 同一轴轮径差最大值和数据来源
     */
    private StateInfo getTYZMaxDiff(List<AxleWheelDiameterEntity> lstLathing){
        StateInfo state = new StateInfo();
        BigDecimal maxValue = new BigDecimal("-1");
        BigDecimal currentValue = new BigDecimal("-1");
        for(int i = 0; i < lstLathing.size(); i++){
            AxleWheelDiameterEntity data = lstLathing.get(i);
            String eddybOne = data.getXxhlj_1();
            String eddybTwo = data.getXxhlj_2();
            if (eddybOne != null && eddybOne != "" && eddybTwo != null && eddybTwo != ""){
                BigDecimal decimalOne = new BigDecimal(eddybOne);
                BigDecimal decimalTwo = new BigDecimal(eddybTwo);
                currentValue = decimalOne.subtract(decimalTwo).abs();
                if(currentValue.compareTo(maxValue) == 1){
                    maxValue = currentValue;
                    String source = String.format("%s车%s轴1轮--%s车%s轴2轮", data.getCarNo(), data.getLocatetionnum(), data.getCarNo(), data.getLocatetionnum());
                    state.setConfigValue(maxValue.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                    state.setValueSource(source);
                }
            }
        }
        return state;
    }

    /**
     *  获取同一转向架轮径差最大值（CRH380A(L)/CRH5/CRH2）
     * @param lstLathing 当前镟修记录
     * @return 同一转向架轮径差最大值
     */
    private StateInfo getTYZXJMaxDiff(List<AxleWheelDiameterEntity> lstLathing){
        StateInfo stateInfo = new StateInfo();
        BigDecimal maxValue = new BigDecimal("-1");
        String source = "";
        List<String> lstCarNos = new ArrayList<String>();
        for(int i=0;i < lstLathing.size();i++){
            if(!lstCarNos.contains(lstLathing.get(i).getCarNo())){
                lstCarNos.add(lstLathing.get(i).getCarNo());
            }
        }
        for(int i=0;i < lstCarNos.size();i++){
            List<AxleWheelDiameterEntity> lathingDataOne = new ArrayList<AxleWheelDiameterEntity>();
            List<AxleWheelDiameterEntity> lathingDataTwo = new ArrayList<AxleWheelDiameterEntity>();
            for(int j=0;j<lstLathing.size();j++){
                if(lstLathing.get(j).getCarNo().equals(lstCarNos.get(i)) && (lstLathing.get(j).getLocatetionnum().equals("1") || lstLathing.get(j).getLocatetionnum().equals("2"))){
                    lathingDataOne.add(lstLathing.get(j));
                }
                if(lstLathing.get(j).getCarNo().equals(lstCarNos.get(i)) && (lstLathing.get(j).getLocatetionnum().equals("3") || lstLathing.get(j).getLocatetionnum().equals("4"))){
                    lathingDataTwo.add(lstLathing.get(j));
                }
            }
            StateInfo currentState = getSZMaxDiff(lathingDataOne);
            BigDecimal currentValue = new BigDecimal(currentState.getConfigValue());
            if (currentValue.compareTo(maxValue) == 1){
                maxValue = currentValue;
                source = currentState.getValueSource();
            }
            currentState = getSZMaxDiff(lathingDataTwo);
            currentValue = new BigDecimal(currentState.getConfigValue());
            if (currentValue.compareTo(maxValue) == 1){
                maxValue = currentValue;
                source = currentState.getValueSource();
            }
        }
        stateInfo.setConfigValue(maxValue.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        stateInfo.setValueSource(source);
        return stateInfo;
    }

    /**
     * 获取动车同一转向架轮径差
     * @param lstLathing 当前镟修记录
     * @return 动车同一转向架轮径差
     */
    private StateInfo getDTYZXJMaxxDiff(List<AxleWheelDiameterEntity> lstLathing){
        List<AxleWheelDiameterEntity> lathingDatas = new ArrayList<AxleWheelDiameterEntity>();
        for(int i=0;i< lstLathing.size();i++){
            if(lstLathing.get(i).getDtType().equals(AxleTypeEnum.AXLE_TYPE_MOTOR.getText())){
                lathingDatas.add(lstLathing.get(i));
            }
        }
        return getTYZXJMaxDiff(lathingDatas);
    }

    /**
     * 获取拖车同一转向架轮径差
     * @param lstLathing 当前镟修记录
     * @return 拖车同一转向架轮径差
     */
    private StateInfo getTTYZXJMaxxDiff(List<AxleWheelDiameterEntity> lstLathing){
        List<AxleWheelDiameterEntity> lathingDatas = new ArrayList<AxleWheelDiameterEntity>();
        for(int i=0;i< lstLathing.size();i++){
            if(lstLathing.get(i).getDtType().equals(AxleTypeEnum.AXLE_TYPE_TRAILER.getText())){
                lathingDatas.add(lstLathing.get(i));
            }
        }
        return getTYZXJMaxDiff(lathingDatas);
    }

    /**
     * 获取同一车辆轮径差最大值（CRH380A(L)/CRH2）
     * @param lstLathing 当前镟修记录
     * @return 同一车辆轮径差最大值
     */
    private StateInfo getTYCLMaxDiff(List<AxleWheelDiameterEntity> lstLathing){
        StateInfo stateInfo = new StateInfo();
        BigDecimal maxValue = new BigDecimal("-1");
        String source = "";
        List<String> lstCarNos = new ArrayList<String>();
        for(int i=0;i < lstLathing.size();i++){
            if(!lstCarNos.contains(lstLathing.get(i).getCarNo())){
                lstCarNos.add(lstLathing.get(i).getCarNo());
            }
        }
        for(int i=0;i< lstCarNos.size();i++){
            List<AxleWheelDiameterEntity> lathingDatas = new ArrayList<AxleWheelDiameterEntity>();
            for (int j=0;j < lstLathing.size();j++){
                if(lstLathing.get(j).getCarNo().equals(lstCarNos.get(i))){
                    lathingDatas.add(lstLathing.get(j));
                }
            }
            StateInfo currentState = getSZMaxDiff(lathingDatas);
            if(currentState!=null){
                BigDecimal currentValue = new BigDecimal(currentState.getConfigValue());
                if(currentValue.compareTo(maxValue) == 1){
                    maxValue = currentValue;
                    source = currentState.getValueSource();
                }
            }
        }
        stateInfo.setConfigValue(maxValue.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        stateInfo.setValueSource(source);
        return stateInfo;
    }

    /**
     * 获取动车同一车辆轮径差最大值
     * @param lstLathing 当前镟修记录
     * @return 动车同一车辆轮径差最大值
     */
    private StateInfo getDTYCLMaxDiff(List<AxleWheelDiameterEntity> lstLathing){
        List<AxleWheelDiameterEntity> lathingDatas = new ArrayList<AxleWheelDiameterEntity>();
        for(int i=0;i< lstLathing.size();i++){
            if(lstLathing.get(i).getDtType().equals(AxleTypeEnum.AXLE_TYPE_MOTOR.getText())){
                lathingDatas.add(lstLathing.get(i));
            }
        }
        return getTYCLMaxDiff(lathingDatas);
    }

    /**
     * 获取拖车同一车辆轮径差最大值
     * @param lstLathing 当前镟修记录
     * @return 拖车同一车辆轮径差最大值
     */
    private StateInfo getTTYCLMaxDiff(List<AxleWheelDiameterEntity> lstLathing){
        List<AxleWheelDiameterEntity> lathingDatas = new ArrayList<AxleWheelDiameterEntity>();
        for(int i=0;i< lstLathing.size();i++){
            if(lstLathing.get(i).getDtType().equals(AxleTypeEnum.AXLE_TYPE_TRAILER.getText())){
                lathingDatas.add(lstLathing.get(i));
            }
        }
        return getTYCLMaxDiff(lathingDatas);
    }

    /**
     * 获取相邻车厢间轮径差最大值
     * @param lstLathing 当前镟修记录
     * @param trainSetId 车组ID
     * @return 相邻车厢间轮径差最大值
     */
    private StateInfo getXLCXJLJMaxDiff(List<AxleWheelDiameterEntity> lstLathing, String trainSetId){
        StateInfo stateInfo = new StateInfo();
        TrainsetBaseInfo trainsetInfo = billCommon.getTrainsetInfo(trainSetId);
        if(trainsetInfo != null){
            List<String> lstCarNos = billCommon.getTrainMarshlType(trainsetInfo.getTrainsetid());
            String minPosition = "";    //最小轮径值所在位置
            String maxPosition = "";    //最大轮径值所在位置
            BigDecimal maxValue = new BigDecimal("0");
            BigDecimal minValue = new BigDecimal("0");
            BigDecimal zeroValue = new BigDecimal("0");
            for(int i=0; i < lstCarNos.size() - 1; i++){
                String currentCarNo = lstCarNos.get(i);
                String nextCarNo = lstCarNos.get(i + 1);
                //获得相邻辆序的轮径数据
                List<AxleWheelDiameterEntity> ulatheArray = new ArrayList<AxleWheelDiameterEntity>();
                for(int j=0;j< lstLathing.size();j++){
                    if(lstLathing.get(j).getCarNo().equals(currentCarNo)){
                        ulatheArray.add(lstLathing.get(j));
                    }
                    if(lstLathing.get(j).getCarNo().equals(nextCarNo)){
                        ulatheArray.add(lstLathing.get(j));
                    }
                }
                //计算16个车轮内的轮径差最大值
                for(AxleWheelDiameterEntity axle : ulatheArray){
                    BigDecimal wheelValue1 = new BigDecimal(axle.getXxhlj_1());
                    BigDecimal wheelValue2 = new BigDecimal(axle.getXxhlj_2());
                    //一轮如果大于2轮的话
                    if(wheelValue1.compareTo(wheelValue2) == 1){
                        //如果一轮大于最大轮，或最大轮的值等于0时，一轮赋值给最大值
                        if(wheelValue1.compareTo(maxValue) == 1 || maxValue.compareTo(zeroValue) == 0){
                            maxValue = wheelValue1;
                            maxPosition = String.format("%s车%s轴1轮", axle.getCarNo(), axle.getLocatetionnum());
                        }
                        //如果二轮小于最小轮，或最小轮的值等于0时，二轮赋值给最小值
                        if(wheelValue2.compareTo(minValue) == -1 || minValue.compareTo(zeroValue) == 0){
                            minValue = wheelValue2;
                            minPosition = String.format("%s车%s轴2轮", axle.getCarNo(), axle.getLocatetionnum());
                        }
                    }else{
                        //如果二轮大于最大轮，或最大轮的值等于0时，二轮赋值给最大值
                        if(wheelValue2.compareTo(maxValue) == 1 || maxValue.compareTo(zeroValue) == 0){
                            maxValue = wheelValue2;
                            maxPosition = String.format("%s车%s轴2轮", axle.getCarNo(), axle.getLocatetionnum());
                        }
                        //如果一轮小于最小轮，或最小轮的值等于0时，一轮赋值给最小值
                        if(wheelValue1.compareTo(minValue) == -1 || minValue.compareTo(zeroValue) == 0){
                            minValue = wheelValue1;
                            minPosition = String.format("%s车%s轴1轮", axle.getCarNo(), axle.getLocatetionnum());
                        }

                    }
                }
            }
            stateInfo.setConfigValue(maxValue.subtract(minValue).toString());
            stateInfo.setValueSource(String.format("%s--%s",maxPosition, minPosition));
        }
        return  stateInfo;
    }

    /**
     * 获取同一动车内两条动力轮对轮径差最大值(CRH5)
     * @param lstLathing 当前镟修记录
     * @return 同一动车内两条动力轮对轮径差最大值
     */
    private StateInfo getDDLLDMaxDiff(List<AxleWheelDiameterEntity> lstLathing){
        List<AxleWheelDiameterEntity> lathingDatas = new ArrayList<AxleWheelDiameterEntity>();
        for(AxleWheelDiameterEntity axleWheelDiameterEntity : lstLathing){
            if(axleWheelDiameterEntity.getDtType().equals(AxleTypeEnum.AXLE_TYPE_MOTOR.getText())){
                lathingDatas.add(axleWheelDiameterEntity);
            }
        }
        return getTYCLMaxDiff(lathingDatas);
    }

    /**
     *  获取[动车]同一轮对轮径差最大值（CRH3A）[动车]
     * @param lstLathing 当前镟修记录
     * @return [动车]同一轮对轮径差最大值（CRH3A）
     */
    private StateInfo getDTYLDLJMaxDiff(List<AxleWheelDiameterEntity> lstLathing){
        List<AxleWheelDiameterEntity> lathingDatas = new ArrayList<AxleWheelDiameterEntity>();
        for(int i=0;i< lstLathing.size();i++){
            if(lstLathing.get(i).getDtType().equals(AxleTypeEnum.AXLE_TYPE_MOTOR.getText())){
                lathingDatas.add(lstLathing.get(i));
            }
        }
        return getTYZMaxDiff(lathingDatas);
    }

    /**
     * 获取[拖车]同一轮对轮径差最大值（CRH3A）[拖车]
     * @param lstLathing 当前镟修记录
     * @return 获取[拖车]同一轮对轮径差最大值（CRH3A）
     */
    private StateInfo getTTYLDLJMaxDiff(List<AxleWheelDiameterEntity> lstLathing){
        List<AxleWheelDiameterEntity> lathingDatas = new ArrayList<AxleWheelDiameterEntity>();
        for(int i=0;i< lstLathing.size();i++){
            if(lstLathing.get(i).getDtType().equals(AxleTypeEnum.AXLE_TYPE_TRAILER.getText())){
                lathingDatas.add(lstLathing.get(i));
            }
        }
        return getTYZMaxDiff(lathingDatas);
    }

    /**
     * 获取一组值的最大差值
     * @param lstLathing
     * @return
     */
    private StateInfo getSZMaxDiff(List<AxleWheelDiameterEntity> lstLathing){
        StateInfo state = new StateInfo();
        BigDecimal maxValue = new BigDecimal("0");
        BigDecimal minValue = new BigDecimal("10000");
        String maxSource = "";
        String minSource = "";
        for(int i = 0; i < lstLathing.size(); i++){
            AxleWheelDiameterEntity data = lstLathing.get(i);
            String eddybOne = data.getXxhlj_1();
            String eddybTwo = data.getXxhlj_2();
            if(eddybOne !=null && eddybOne != "" && eddybTwo !=null && eddybTwo != ""){
                BigDecimal currentValue = new BigDecimal(eddybTwo);
                if(currentValue.compareTo(maxValue) == 1){
                    maxValue = currentValue;
                    maxSource = String.format("%s车%s轴2轮", data.getCarNo(), data.getLocatetionnum());
                }
                if(currentValue.compareTo(minValue) == -1){
                    minValue = currentValue;
                    minSource = String.format("%s车%s轴2轮", data.getCarNo(), data.getLocatetionnum());
                }

                currentValue = new BigDecimal(eddybOne);
                if(currentValue.compareTo(maxValue) == 1){
                    maxValue = currentValue;
                    maxSource = String.format("%s车%s轴1轮", data.getCarNo(), data.getLocatetionnum());
                }
                if(currentValue.compareTo(minValue) == -1){
                    minValue = currentValue;
                    minSource = String.format("%s车%s轴1轮", data.getCarNo(), data.getLocatetionnum());
                }
            }
        }
        state.setConfigValue(maxValue.subtract(minValue).toString());
        state.setValueSource(minSource + " -- " + maxSource);
        return state;
    }

    /**
     * 将记录单内的数据转换为镟轮数据
     * @param train 车组信息
     * @param contentList 记录单详细数据
     * @return 镟轮数据
     */
    public List<AxleWheelDiameterEntity> convertLathingData(TrainsetBaseInfo train, List<ChecklistDetailInfoForSave> contentList){
        List<AxleWheelDiameterEntity> lstLathings = new ArrayList<AxleWheelDiameterEntity>();
        //获取数据实体中所有属性为辆序的实体
        List<ChecklistDetailInfoForSave> lstCarNoDetails = getDetailByAttrForSave(contentList,AttributeEnum.ATTR_CAR);
        //获取数据实体中所有属性为轴位的实体
        List<ChecklistDetailInfoForSave> lstAxleDetails = getDetailByAttrForSave(contentList,AttributeEnum.ATTR_AXLE_POSTION);
        //获取当前日期
        String sDate = getDateNow(0);
        getLathingData(train,sDate, AttributeEnum.ATTR_WHEEL_ONEXQ,contentList,lstCarNoDetails, lstAxleDetails,lstLathings);    //获取1位镟前轮径值
        getLathingData(train,sDate, AttributeEnum.ATTR_WHEEL_ONEXH,contentList,lstCarNoDetails, lstAxleDetails,lstLathings);    //获取1位镟后轮径值
        getLathingData(train,sDate, AttributeEnum.ATTR_WHEEL_TWOXQ,contentList,lstCarNoDetails, lstAxleDetails,lstLathings);    //获取2位镟前轮径值
        getLathingData(train,sDate, AttributeEnum.ATTR_WHEEL_TWOXH,contentList,lstCarNoDetails, lstAxleDetails,lstLathings);    //获取2位镟后轮径值
        return  lstLathings;
    }

    /**
     * 设置轮径值
     * @param train 车组信息
     * @param sDate 日期
     * @param attr 属性类型
     * @param contentList 数据集合
     * @param lstCarNoDetails 辆序数据集合
     * @param lstAxleDetails 车轴数据集合
     * @param lstLathings 镟轮数据
     */
    private List<AxleWheelDiameterEntity> getLathingData(TrainsetBaseInfo train, String sDate, AttributeEnum attr,
                                                         List<ChecklistDetailInfoForSave> contentList,
                                                         List<ChecklistDetailInfoForSave> lstCarNoDetails,
                                                         List<ChecklistDetailInfoForSave> lstAxleDetails,
                                                         List<AxleWheelDiameterEntity> lstLathings) {
        List<ChecklistDetailInfoForSave> lstWheelDetails = getDetailByAttrForSave(contentList, attr);
        List<AxleWheel> lstAxleWheel = new ArrayList<AxleWheel>();
        //循环镟轮数据
        for (int i = 0; i < lstWheelDetails.size(); i++) {
            ChecklistDetailInfoForSave wheelDetail = lstWheelDetails.get(i);
            //如果是N/A则跳过
            if(wheelDetail.getValue().equals("N/A"))
                continue;
            String carNo = "";              //辆序
            String axlePosition = "";       //轴位
            for (ChkDetailLinkContentForModule linkContent : wheelDetail.getLinkCells()) {
                if (StringUtils.isEmpty(carNo)) {
                    ChecklistDetailInfoForSave carNoDetail = findDetailByContentIdForSave(lstCarNoDetails, AttributeEnum.ATTR_CAR, linkContent.getLinkCellId());
                    if (carNoDetail != null) {
                        carNo = carNoDetail.getValue();
                        continue;
                    }
                }
                if (StringUtils.isEmpty(axlePosition)) {
                    ChecklistDetailInfoForSave axlePositionDetail = findDetailByContentIdForSave(lstAxleDetails, AttributeEnum.ATTR_AXLE_POSTION, linkContent.getLinkCellId());
                    if (axlePositionDetail != null) {
                        axlePosition = axlePositionDetail.getValue();
                        continue;
                    }
                }
                //当辆序和轴位有未找到的时候，直接跳过
                if (carNo != null && axlePosition != null) {
                    break;
                }
            }
            //当辆序和轴位有未找到的时候，直接跳过
            if (carNo == null || axlePosition == null) {
                continue;
            }
            //当前镟轮数据的镟修数据实体
            AxleWheelDiameterEntity lathingData = null;
            for (int j = 0; j < lstLathings.size(); j++) {
                if (lstLathings.get(j).getCarNo().equals(carNo) && lstLathings.get(j).getLocatetionnum().equals(axlePosition)) {
                    lathingData = lstLathings.get(j);
                    break;
                }
            }
            if (lathingData == null) {
                lathingData = new AxleWheelDiameterEntity();
                lathingData.setTrainSetID(train.getTrainsetid());
                lathingData.setCarNo(carNo);
                lathingData.setLocatetionnum(axlePosition);
                AxleWheel axleWheel = getAxleWheel(lstAxleWheel, train.getTrainsetid(), carNo, sDate, axlePosition, true);
                if (axleWheel != null) {
                    lathingData.setDtType(axleWheel.getDtType());
                    lathingData.setSerialnum_axle(axleWheel.getSerialNum());
                    lathingData.setRunMiles(axleWheel.getRunmiles());
                }
                lstLathings.add(lathingData);
            }
            //当镟轮数据不为空时，对数据进行赋值
            if (lathingData != null) {
                if (attr.equals(AttributeEnum.ATTR_WHEEL_ONEXQ)) {
                    lathingData.setXxqlj_1(wheelDetail.getValue());       //设置1位镟前轮径值
                } else if (attr.equals(AttributeEnum.ATTR_WHEEL_ONEXH)) {
                    lathingData.setXxhlj_1(wheelDetail.getValue());        //设置1位镟后轮径值
                } else if (attr.equals(AttributeEnum.ATTR_WHEEL_TWOXQ)) {
                    lathingData.setXxqlj_2(wheelDetail.getValue());       //设置2位镟前轮径值
                } else if (attr.equals(AttributeEnum.ATTR_WHEEL_TWOXH)) {
                    lathingData.setXxhlj_2(wheelDetail.getValue());        //设置2位镟后轮径值
                }
            }
        }
        return lstLathings;
    }

    /**
     * 将保存数据转换为展示数据，并为确认值赋值
     * @param detail
     * @param configValue
     * @param tip
     * @return
     */
    private ChecklistDetailInfoForShow convertDetailForShow(ChecklistDetailInfoForSave detail, String configValue, String tip){
        ChecklistDetailInfoForShow detailInfoForShow = new ChecklistDetailInfoForShow();
        String tipMsg = "";
        //如果原确认值和新计算的确认值不一致，且原确认值不为空，则添加提示，并使用新的确认值
        if(!StringUtils.isEmpty(detail.getValue()) && !StringUtils.isEmpty(configValue)){
            boolean isCompare = compareConfigValue(detail.getValue(), configValue);
            if(!isCompare) {
                tipMsg = String.format("%s：确认值已由%s更改为%s，请确认！", tip, detail.getValue(), configValue);
            }
        }
        detail.setValue(configValue);
        BeanUtils.copyProperties(detail, detailInfoForShow);
        detailInfoForShow.setTip(tipMsg);
        return detailInfoForShow;
    }

    /**
     * 比较确认值
     * @param source 原确认值
     * @param newValue 新计算出来的确认值
     * @return 是否相同
     */
    private boolean compareConfigValue(String source, String newValue){
        BigDecimal bigSource = new BigDecimal(source);
        BigDecimal bigNewValue = new BigDecimal(newValue);
        return bigSource.compareTo(bigNewValue) == 0;
    }

}
