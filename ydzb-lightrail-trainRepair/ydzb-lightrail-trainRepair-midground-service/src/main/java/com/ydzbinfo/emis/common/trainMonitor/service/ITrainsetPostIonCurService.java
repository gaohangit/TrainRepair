package com.ydzbinfo.emis.common.trainMonitor.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.trainMonitor.model.TrainsetIsConnect;
import com.ydzbinfo.emis.trainRepair.trainMonitor.model.TrainsetPostionCurWithNextTrack;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrainsetPostionCur;

import java.text.ParseException;
import java.util.List;

/**
 * @author gaohan
 * @description
 * @createDate 2021/2/26 12:06
 **/
public interface ITrainsetPostIonCurService extends IService<TrainsetPostionCur> {

    List<TrainsetPostionCurWithNextTrack> getTrainsetPostion(Page page, List<String> trackCodes, List<String> trainsetNames, List<String> unitCodes, boolean showShuntPlan) throws ParseException;

    List<TrainsetPostionCur> getTrainsetPostionByTrackCode(String trackCode);

    int addTrainsetPostion(TrainsetPostionCur trainsetPositionEntity);

    int updateTrainsetPostion(TrainsetPostionCur trainsetPositionEntity) throws ParseException;

    TrainsetPostionCur getTrainsetPositionById(String trainsetId);

    int deleteTrainsetPostion(String trainsetId);

    String setTrainsetPosition(TrainsetPostionCur trainsetPositionEntity) throws ParseException;

    void setTrainsetState(TrainsetIsConnect trainsetPostIonIds);

    String updTrackCode(List<TrainsetPostionCur> trainsetIsConnects) throws ParseException;

}
