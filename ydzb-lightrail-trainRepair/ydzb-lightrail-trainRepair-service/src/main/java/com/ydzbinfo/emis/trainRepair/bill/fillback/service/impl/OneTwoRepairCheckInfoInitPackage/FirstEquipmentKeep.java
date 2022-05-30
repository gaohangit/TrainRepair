package com.ydzbinfo.emis.trainRepair.bill.fillback.service.impl.OneTwoRepairCheckInfoInitPackage;

import com.ydzbinfo.emis.trainRepair.bill.fillback.service.impl.CheckInfoInit;
import com.ydzbinfo.emis.trainRepair.bill.fillback.service.impl.CheckInfoServiceImpl;
import com.ydzbinfo.emis.trainRepair.bill.fillback.utils.AttributeEnum;
import com.ydzbinfo.emis.trainRepair.bill.fillback.utils.BillCommon;
import com.ydzbinfo.emis.trainRepair.bill.fillback.utils.MainCycEnum;
import com.ydzbinfo.emis.trainRepair.bill.general.UrlInfo;
import com.ydzbinfo.emis.trainRepair.bill.general.constant.BillCellTriggerTimingEnum;
import com.ydzbinfo.emis.trainRepair.bill.general.constant.BillCellTriggerUrlTypeEnum;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.ChecklistDetailInfoForShow;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.ChecklistSummaryInfoForShow;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChecklistSummary;
import com.ydzbinfo.emis.trainRepair.remotemodel.item.RepairItemInfo;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetBaseInfo;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.runRouting.LeaveBackTrainNoResult;
import com.ydzbinfo.emis.utils.StringUtils;
import com.ydzbinfo.emis.utils.UserUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * <p>
 *  机检一级修
 * </p>
 *
 * @author 史艳涛
 * @since 2022-01-20
 */
@Component
public class FirstEquipmentKeep extends CheckInfoInit {

    protected static final Logger logger = getLogger(FirstEquipmentKeep.class);
    @Autowired
    BillCommon billCommon;
    /**
     * 初始化数据,返回修改的单元格
     */
    public List<ChecklistDetailInfoForShow> initContentData(ChecklistSummaryInfoForShow content) {
        //修改的单元格
        List<ChecklistDetailInfoForShow> result = new ArrayList<ChecklistDetailInfoForShow>();
        try {
            content.setShowSaveButton(true);
            //初始化数据
            List<ChecklistDetailInfoForShow> cells = content.getCells();
            //记录单
            ChecklistSummary summary = content.getExtraObject();
            for(ChecklistDetailInfoForShow cell : cells){
                if(cell.getAttributeCode().equals(AttributeEnum.ATTR_ALL_DEPT.getValue())){
                    String depot = UserUtil.getUserInfo().getDepot().getName();
                    if(depot != null && !StringUtils.isEmpty(depot)){
                        cell.setValue(depot + summary.getUnitName());
                    }else {
                        cell.setValue(summary.getUnitName());
                        logger.error("UserUtil.getUserInfo().getDepot().getName() 获取到的段名称为null");
                    }
                    result.add(cell);
                }
                if(cell.getAttributeCode().equals(AttributeEnum.ATTR_REPAIR_DATE.getValue())){
                    cell.setValue(summary.getDayPlanId().substring(0, 10));
                    result.add(cell);
                }
                if(cell.getAttributeCode().equals(AttributeEnum.ATTR_IN_OUT_TRAINNO.getValue())){
                    //出入所信息  入所车次/出所车次
                    List<ChecklistDetailInfoForShow> resultTrainsetId = cells.stream().
                            filter((ChecklistDetailInfoForShow t) -> t.getAttributeCode().equals(AttributeEnum.ATTR_TRAINSETID.getValue())).
                            collect(Collectors.toList());
                    String dayPlanId = summary.getDayPlanId();
                    if (resultTrainsetId.size() > 0) {
                        String trainsetId = resultTrainsetId.get(0).getValue();
                        String querydate = dayPlanId.substring(0, 4) +
                                dayPlanId.substring(5, 7) + dayPlanId.substring(8, 10);
                        LeaveBackTrainNoResult leaveBackTrainNoResult = billCommon.getTrainsetLeaveBack(trainsetId, querydate);
                        if (leaveBackTrainNoResult != null) {
                            String leaveBack = leaveBackTrainNoResult.getBackTrainNo() + "/" + leaveBackTrainNoResult.getDepTrainNo();
                            cell.setValue(leaveBack);
                            result.add(cell);
                        }
                    }
                }
                if(cell.getAttributeCode().equals(AttributeEnum.ATTR_TRAINSETID.getValue())){
                    TrainsetBaseInfo info = billCommon.getTrainsetInfo(summary.getTrainsetId());
                    cell.setValue(info.getTrainsetname());
                    result.add(cell);
                }
            }
        }catch (Exception e){
            logger.debug("一级修机检记录单初始化数据,返回修改的单元格,引发异常"+ e.toString());
            result = new ArrayList<ChecklistDetailInfoForShow>();
        }
        return result;
    }

    /**
     * 初始化规则
     */
    public void initContentRule(ChecklistSummaryInfoForShow content){
        try {
            content.setShowSaveButton(true);
            List<ChecklistDetailInfoForShow> cells = content.getCells();
            List<ChecklistDetailInfoForShow> cs = cells.stream().filter(t->t.getAttributeCode().equals(AttributeEnum.ATTR_CHEEK_TECHNIQUE_SIGN.getValue())).collect(Collectors.toList());
            if(cs.size() > 0){
                for (ChecklistDetailInfoForShow cell : cs){
                    UrlInfo triggerUrlInfo = new UrlInfo();
                    triggerUrlInfo.setUrlType(BillCellTriggerUrlTypeEnum.FUNCTION);
                    triggerUrlInfo.setUrl(billCommon.addApplicationPrefix("/checkInfo/signTechnique"));//签字
                    cell.setTriggerUrlInfo(triggerUrlInfo);
                    cell.setTriggerTiming(BillCellTriggerTimingEnum.AFTER_CHANGE);
                    if(!StringUtils.isEmpty(cell.getValue())){
                        content.setShowSaveButton(false);
                    }
                }
            }
        }catch (Exception e){
            logger.debug("一级修机检记录单初始化规则时,引发异常"+ e.toString());
        }
    }

}
