package com.ydzbinfo.emis.trainRepair.trainMonitor.dao;


import com.ydzbinfo.emis.trainRepair.trainMonitor.model.TrainsetIsConnect;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrainsetPostionHis;

public interface TrainsetPostIonHisMapper {

    int addTrainsetPostionHis(TrainsetPostionHis trainsetPositionHisEntity);
    void setTrainsetState(TrainsetIsConnect trainsetIsConnect);
}
