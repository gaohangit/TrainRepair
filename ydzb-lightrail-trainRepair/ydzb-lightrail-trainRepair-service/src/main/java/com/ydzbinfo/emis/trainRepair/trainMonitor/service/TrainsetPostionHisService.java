package com.ydzbinfo.emis.trainRepair.trainMonitor.service;
import com.ydzbinfo.emis.trainRepair.trainMonitor.model.TrainsetIsConnect;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrainsetPostionHis;


public interface TrainsetPostionHisService {

    int addTrainsetPostionHis(TrainsetPostionHis trainsetPositionHisEntity);
    void setTrainsetState(TrainsetIsConnect trainsetPostIonIds);
}
