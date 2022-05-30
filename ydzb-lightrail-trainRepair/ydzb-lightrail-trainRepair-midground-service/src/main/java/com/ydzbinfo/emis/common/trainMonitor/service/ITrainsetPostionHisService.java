package com.ydzbinfo.emis.common.trainMonitor.service;
import com.ydzbinfo.emis.trainRepair.trainMonitor.model.TrainsetIsConnect;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrainsetPostionHis;


public interface ITrainsetPostionHisService {

    int addTrainsetPostionHis(TrainsetPostionHis trainsetPositionHisEntity);
    void setTrainsetState(TrainsetIsConnect trainsetPostIonIds);
}
