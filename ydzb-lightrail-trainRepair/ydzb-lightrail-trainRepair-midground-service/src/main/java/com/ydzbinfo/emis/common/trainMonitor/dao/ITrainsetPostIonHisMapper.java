package com.ydzbinfo.emis.common.trainMonitor.dao;


import com.ydzbinfo.emis.trainRepair.trainMonitor.model.TrainsetIsConnect;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrainsetPostionHis;

public interface ITrainsetPostIonHisMapper {

    int addTrainsetPostionHis(TrainsetPostionHis trainsetPositionHisEntity);
    void setTrainsetState(TrainsetIsConnect trainsetIsConnect);
}
