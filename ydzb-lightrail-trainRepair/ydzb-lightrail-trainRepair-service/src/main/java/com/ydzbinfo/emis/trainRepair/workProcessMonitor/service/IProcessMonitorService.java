package com.ydzbinfo.emis.trainRepair.workProcessMonitor.service;

import com.ydzbinfo.emis.trainRepair.trainsetPostion.querymodel.TrainsetPositionEntityBase;
import com.ydzbinfo.emis.trainRepair.workProcessMonitor.model.FaultItem;
import com.ydzbinfo.emis.trainRepair.workProcessMonitor.model.QueryProcessMonitorTrack;
import com.ydzbinfo.emis.trainRepair.workProcessMonitor.model.TwoItem;

import java.util.Date;
import java.util.List;

/**
 * @author: 冯帅
 * @Date: 2021/5/24 14:57
 * @Description:
 */
public interface IProcessMonitorService {

    List<QueryProcessMonitorTrack> getOneWorkProcessMonitorList(String unitCode,String dayPlanId,String trackCodesJsonStr, String trainsetNameStr);

    List<QueryProcessMonitorTrack> getTwoWorkProcessMonitorList(String unitCode, String dayPlanId, String trackCodesJsonStr, String trainsetNameStr);

    List<TwoItem> getTwoItemList(String unitCode,String dayPlanId,String trainsetId,String packetCode,String packetName);

    List<FaultItem> getFaultList(String unitCode,String dayPlanId,String trainsetId,String carNo);

    Date getOutTime(String dayPlanId,String unitCode,String trainsetId);

    List<TrainsetPositionEntityBase> getOneWorkProcessMonitorConfig(List<Integer> trackCode);


}
