package com.ydzbinfo.emis.trainRepair.trainMonitor.service;

import com.ydzbinfo.emis.trainRepair.trainMonitor.model.RepairTask;
import com.ydzbinfo.emis.trainRepair.trainMonitor.model.TrainsetIsConnect;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrainsetPostionCur;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author gaohan
 * @description
 * @createDate 2021/2/26 12:06
 **/
public interface TrainsetPostIonCurService {

    List<TrainsetPostionCur> getTrainsetPostion(Set<String> trackCodes, String unitCode);

    List<TrainsetPostionCur> getTrainsetPostionByTrackCode(String trackCode);

    int addTrainsetPostion(TrainsetPostionCur trainsetPositionEntity);

    int updateTrainsetPostion(TrainsetPostionCur trainsetPositionEntity);

    TrainsetPostionCur getTrainsetPositionById(String trainsetId);

    int deleteTrainsetPostion(String trainsetId);

    String setTrainsetPosition(TrainsetPostionCur trainsetPositionEntity);

    void setTrainsetState(TrainsetIsConnect trainsetPostIonIds);

    Date getOutTime(String nowDate, String tackCode);

    void updTrackCode(TrainsetPostionCur trainsetPostIonCur);

    List<TrainsetPostionCur> getByTrainsetIds(String... trainsetIds);

    /**
     * 获取车组匹配到的修程
     */
    List<RepairTask> getRepairTask(String dayPlanId, String trainsetIdStr, String unitCode, Boolean showDayRepairTask,Boolean showForceEndFlowRun);

    /**
     * 获取某修程的车组和股道供断电信息
     */
    Map getTrainsetAndTrackPowerInfo(String dayPlanId, String flowPageCode, String unitCode,Date startDateTime,Date endDateTime);
}
